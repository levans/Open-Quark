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
 * CAL.g
 * Created: 22-Feb-00
 * By: Luke Evans
 */
 
/*    
 * CAL.g
 *
 * Crystal Analytical Language (CAL) 
 *
 * The textual form of the grammar of the CAL language. CAL's syntax is essentially a subset of that of Haskell, except that
 * CAL uses the semicolon as a statement terminator and does not use layout.
 *
 * Change History:
 * LWE 22-Feb-00    Initial Module.  Very simple core FP language.
 * LWE 29-Mar-00    Reworked the expression grammar definition by fully left-factoring to remove the syntactic predicates.
 *                  The previous grammar would compile without errors but the resulting parser executed unbelievably
 *                  slowly with only moderate tail recursive production invocations (i.e. nested subexpressions).  The change
 *                  has led to over two orders of magnitude speedup!!  
 * LWE 09-May-00    Repackage for name change from SAL to CAL (and CALC!)
 * LWE 15-May-00    Separate out the pragma lexer.  This saves the pragma lexemes (e.g. "on", "off") from polluting
 *                  the vocabulary of the main CAL grammar.  As this is all we actual want to achieve, we haven't 
 *                  separated out the grammar itself (by splitting the parser), though this is possible too.
 * BI 20-June-00    Changed the parser to generate an AST instead of using actions
 * BI 28-Sept-00    Distinguish between data constructors and variables as in Haskell. Also, removed the Pack keyword which
 *                  was used for non type-safe data constructors e.g. "Cons = Pack {0, 2}; Nil = Pack {1, 0};".
 * BI 12-Dec-00     Added the ability to parse type declarations e.g. "head :: [a] -> a;" to the grammar.
 * BI 09-Jan-01     Added parsing of string and character literals. The changes to the lexer were taken from the java sample
 *                  grammar supplied with antlr. 
 * BI 18-Jan-01     Added parsing of data declarations.
 * BI 01-Feb-01     Added local supercombinator definitions.
 * BI 22-Feb-01     Syntactic sugar. "if-then-else" syntax replaces the use of the "if" supercombinator. 
 *                  Special notation for lists e.g. [10, 2, 7], tuples e.g. ("a", 4) and the trivial type ().
 *                  The colon operator a : as. Simple pattern matching in case expressions.
 * BI 26-March-01   Added class constraints to type declarations.
 * BI  9-April-01   Removed the letrec keyword. let now means what letrec used to mean.
 * BI 28-May-01     Added module syntax (including qualified names).
 * BI 19-July-01    Added qualified context names.
 * BI 21-Aug-01     Added code gem expressions as a top level expression. These must consume an EOF.
 * BI 30-Aug-01     Made +, -, * and / left associative and added unary minus.
 * BI 31-Aug-01     Eliminated atomic expressions in the tree parsing stage (a refactoring).
 * BI 29-April-02   First pass at a foreign function interface.
 * BI 24-June-02    Added unicode lexing and opaque foreign data types.
 * BI  9-July-02    Changed ~= to !=. Suspended Unicode lexing due to an ANTLR bug (see check-in note for details)
 *                  but we can now still lex all ANSI tokens such as the $.
 * BI 22-Aug-02     Added the ++ operator and CALLexer.isKeyword.
 * BI 26-Aug-02     Added syntax for declaring type classes and class instances.
 * BI 17-Feb-03     Added syntax for the wildcard pattern "_".
 * EL 18-Feb-03     Added a start (top-level) rule for type signatures.  These must consume an EOF.
 * BI 20-Feb-03     Wildcard patterns can now be used for pattern bound variables in case expression pattern matching.
 * BI  6-March-03   Improved parsing error messages by increased use of the paraphrase option.
 * EL 11-Mar-03     Changed the top-level for the compiler from a "compilationUnit" (which encapsulated multiple modules)
 *                  to a single module.  These (now top-level) modules must consume an EOF.
 * BI 13-March-03   Changed the tabSize to 4 (from 8). This effects column positions in error messages as well
 *                  as syntax highlighting in the code gem.
 * BI 29-April-03   Generated CAL specific MismatchedTokenException to allow for localization and better CAL specific details.
 * BI  7-May-03     Added support for constrained instance declarations.
 * BI  8-July-03    Syntax for local type declarations.
 * BI 22-Oct-03     Added implementation scope for foreign type declarations.
 * RAC 5-Feb-04     Added rules for adjuncts.
 * BI 10-Feb-04     Syntax for extensible records.
 * RAC 26-Feb-4     Added type instances to adjuncts.
 * BI  4-April-04   Added "import module using" syntax to support the use of unqualified import symbols.
 * BI  2-June-04    Added syntax for strictness annotations in data constructors.
 * BI  9-June-04    Added syntax for strictness annotations on function arguments.
 * BI 16-June-04    Syntax for built-in primitive functions.
 * BI 23-Sept-04    Syntax for ordinal field names for records such as {#1 :: Int, #2 :: Char}.
 * BI 26-Oct-04     Syntax for declaring ad-hoc record instances, as well as the use of special notation for
 *                  tuple, unit, list, and function instances.
 * BI 29-Oct-04     Added expression type signatures e.g. expressions like myList = [1 :: Int, 2, 3].
 * PC 09-Nov-04     Internationalized the compilerMessage strings.
 * BI 09-Dec-04     Cleaned up int and float literal lexing. CAL doesn't accept hex and octal literal values
 *                  or type decorations on literals (such as f, l, d).
 *                  Removed the Pragma grammar. It was not being used. 
 *                  Added UNARY_MINUS node instead of translating to Prelude.negate application.
 * BI 11-April-05   Removed syntax for ad-hoc record instances. Added syntax for universal record instances.
 * BI 15-April-05   Tuple-record unification- removed syntax for delaring ad-hoc record instances for tuples of a fixed size.
 * GM 10-May-05     Added back quoted operators. Left associative. Priority is just higher than unary minus
 * BI 19-May-05     deriving clause for algebraic and foreign data declarations. Allows automatic creation of class instance decalarations
 *                  for certain core type classes, such as Eq.
 * JWO 16-June-05   Added the # (compose) and $ (low-precedence application) operators.
 * EL  7-July-05    Added syntax for named data constructor arguments, field name-based argument extraction in case expressions.
 * JWO 14-July-05   Added grammar for documentation comments.
 * JWO 3-Aug-05     Allowed an ordinal field name to appear in a CALDOC "@arg" tag.
 * EL  17-Aug-05    Added syntax for case unpacking using data constructor group patterns.
 * JWO 18-Aug-05    Improved error reporting for malformed CALDoc comments.
 * JWO 23-Aug-05    Allowed CALDoc comments for inner function definitions.
 * JWO 25-Aug-05    Improved accuracy of source positions for CALDoc tags.
 * EL  6-Sep-05     Added syntax for case unpacking of ints and chars.
 * EL  13-Sep-05    Unified handling of case unpacking for unparenthesized single data constructors and 
 *                  parenthesized data constructor groups.
 * JWR 26-Sep-05    Improved error messages for NoViableAltExceptions.  We now associate paraphrases with various high-level parsing targets, which are output to the user on error.
 * JWR 14-Oct-05    Improve the error message for missing BAR or 'deriving' token in data declarations by separating out the maybeDerivingClause rule; similarly improve detection of missing 'using' token.
 * JWO 24-Oct-05    Added grammar for inline formatting tags in CALDoc.
 * JWR 28-Oct-05    Improve "offending token" detection for NoViableAltExceptions; updated the paraphrase for codeGemExpression rule. 
 * BI  01-Nov-05    Added syntax for protected scope.
 * BI  02-Nov-05    Added syntax for friend modules. 
 * JWO 1-Dec-05     Added CALDoc syntax for @see/{@link} without context, {@strong}, {@sup}, {@sub}.
 * JWR 28-Feb-06    Updates to support the conversion from SourcePositions to SourceRanges
 * BI  04-March-06  Added the remainder operator (%).
 * JWR 18-Apr-06    Added proper SourceRange annotation for inline CALDoc elements and see blocks
 * JWR 21-Apr-06    Added support for a single-case paraphrased syntax error
 * BI   1-May-06    Added syntax for the record field value update operator ":=".
 * BI  27-July-06   Syntax for default class methods.
 * JWO 21-Sep-06    Fixed NPE in source range handling code of docCommentInlineBlock.
 * JWO 11-Oct-06    Fixed two more NPEs in source range handling code of docCommentInlineBlock.
 * JWO 1-Nov-06     Added syntax for hierarchical module names.
 * JWO 12-Dec-06    Removed the CALLexer constructor that takes an InputStream parameter.
 * JWO 19-Feb-07    Added syntax for lazy pattern matching by extending the let expression syntax.
 * GJM 28-May-07    Updates to use SourcePosition instead of SourceRanges.
 * BI  22-June-07   Added a static method to CALLexer to validate keywords.
 */
    
header {
// Package declaration
package org.openquark.cal.compiler;
}

/*************************************** 
 * CAL grammar definition starts here  *   
 ***************************************/

class CALParser extends Parser;
options {
    k = 2;                           // Token lookahead
    importVocab=CALCommon;
    importVocab=CALDoc;
    exportVocab=CAL;
    codeGenMakeSwitchThreshold = 2;  // Some optimizations
    codeGenBitsetTestThreshold = 3;  // "
    defaultErrorHandler = true;      // Generate parser error handlers.
    buildAST = true;                 // Build abstract syntax tree?

    //Suppress warnings for the generated class.  Need to use unicode escapes since antlr copies backslashes into the source.
    //instead of "public", which is the default, make CALParser package scope.        
    classHeaderPrefix = "@SuppressWarnings(\u0022all\u0022) final"; 
}

tokens {
    TOP_LEVEL_FUNCTION_DEFN;
    FUNCTION_PARAM_LIST;
    STRICT_PARAM;
    LAZY_PARAM;
    LAMBDA_DEFN;
    APPLICATION;
    ALT_LIST;
    ALT;   
    LET_DEFN_LIST;
    LET_DEFN;
    LET_PATTERN_MATCH_DECL;
    TOP_LEVEL_TYPE_DECLARATION;
    LET_DEFN_TYPE_DECLARATION;
    TYPE_DECLARATION;
    FUNCTION_TYPE_CONSTRUCTOR;
    TYPE_APPLICATION;
    TUPLE_CONSTRUCTOR;
    LIST_CONSTRUCTOR;
    DATA_DECLARATION;
    TYPE_CONS_PARAM_LIST;
    DATA_CONSTRUCTOR_DEFN_LIST;
    DATA_CONSTRUCTOR_DEFN;
    DATA_CONSTRUCTOR_ARG_LIST;
    DATA_CONSTRUCTOR_NAMED_ARG;
    TYPE_CONTEXT_LIST;
    TYPE_CONTEXT_SINGLETON;
    TYPE_CONTEXT_NOTHING;
    CLASS_CONTEXT_LIST;
    CLASS_CONTEXT_SINGLETON;
    CLASS_CONTEXT_NOTHING;
    CLASS_CONTEXT;
    LACKS_FIELD_CONTEXT;
    FRIEND_DECLARATION_LIST;
    IMPORT_DECLARATION_LIST;
    OUTER_DEFN_LIST;
    ACCESS_MODIFIER;
    QUALIFIED_VAR;
    QUALIFIED_CONS;
    HIERARCHICAL_MODULE_NAME;
    HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER;
    COMPILATION_UNIT;
    MODULE_DEFN;   
    PATTERN_CONSTRUCTOR;
    UNIT_TYPE_CONSTRUCTOR;
    LIST_TYPE_CONSTRUCTOR;
    TUPLE_TYPE_CONSTRUCTOR;
    INT_PATTERN;
    CHAR_PATTERN;
    FOREIGN_FUNCTION_DECLARATION;
    PRIMITIVE_FUNCTION_DECLARATION;
    FOREIGN_DATA_DECLARATION;
    TYPE_CLASS_DEFN;
    CLASS_METHOD_LIST;
    CLASS_METHOD;
    INSTANCE_DEFN;
    INSTANCE_NAME;  
    INSTANCE_METHOD_LIST;
    INSTANCE_METHOD;
    UNPARENTHESIZED_TYPE_CONSTRUCTOR; 
    GENERAL_TYPE_CONSTRUCTOR;   
    PATTERN_VAR_LIST;
    DATA_CONSTRUCTOR_NAME_LIST;
    DATA_CONSTRUCTOR_NAME_SINGLETON;
    MAYBE_MINUS_INT_LIST;
    CHAR_LIST;
    RECORD_CONSTRUCTOR; 
    BASE_RECORD;
    FIELD_MODIFICATION_LIST;
    FIELD_EXTENSION;
    FIELD_VALUE_UPDATE;    
    RECORD_TYPE_CONSTRUCTOR;
    RECORD_VAR;
    FIELD_TYPE_ASSIGNMENT_LIST;
    FIELD_TYPE_ASSIGNMENT;
    SELECT_RECORD_FIELD;
    SELECT_DATA_CONSTRUCTOR_FIELD;
    RECORD_PATTERN;
    BASE_RECORD_PATTERN;
    FIELD_BINDING_VAR_ASSIGNMENT_LIST;
    FIELD_BINDING_VAR_ASSIGNMENT;
    TYPE_SIGNATURE;
    STRICT_ARG;  
    EXPRESSION_TYPE_SIGNATURE;   
    UNARY_MINUS;
    
    OPTIONAL_CALDOC_COMMENT;
    CALDOC_COMMENT;
    CALDOC_TEXT;
    CALDOC_DESCRIPTION_BLOCK;
    CALDOC_TAGGED_BLOCKS;
    CALDOC_AUTHOR_BLOCK;
    CALDOC_DEPRECATED_BLOCK;
    CALDOC_RETURN_BLOCK;
    CALDOC_VERSION_BLOCK;
    CALDOC_ARG_BLOCK;
    CALDOC_SEE_BLOCK;
    CALDOC_SEE_FUNCTION_BLOCK;
    CALDOC_SEE_MODULE_BLOCK;
    CALDOC_SEE_DATACONS_BLOCK;
    CALDOC_SEE_TYPECONS_BLOCK;
    CALDOC_SEE_TYPECLASS_BLOCK;
    CALDOC_SEE_BLOCK_WITHOUT_CONTEXT;
    CALDOC_CHECKED_MODULE_NAME;
    CALDOC_UNCHECKED_MODULE_NAME;
    CALDOC_CHECKED_QUALIFIED_VAR;
    CALDOC_UNCHECKED_QUALIFIED_VAR;
    CALDOC_CHECKED_QUALIFIED_CONS;
    CALDOC_UNCHECKED_QUALIFIED_CONS;
    
    CALDOC_TEXT_PREFORMATTED_BLOCK;
    CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS;
    
    CALDOC_TEXT_LINE_BREAK;
    
    CALDOC_TEXT_INLINE_BLOCK;
    CALDOC_TEXT_URL;
    CALDOC_TEXT_LINK;
    CALDOC_TEXT_EMPHASIZED_TEXT;
    CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT;
    CALDOC_TEXT_SUPERSCRIPT_TEXT;
    CALDOC_TEXT_SUBSCRIPT_TEXT;
    CALDOC_TEXT_SUMMARY;
    CALDOC_TEXT_CODE_BLOCK;
    CALDOC_TEXT_ORDERED_LIST;
    CALDOC_TEXT_UNORDERED_LIST;
    CALDOC_TEXT_LIST_ITEM;
     
    CALDOC_TEXT_LINK_FUNCTION;
    CALDOC_TEXT_LINK_MODULE;
    CALDOC_TEXT_LINK_DATACONS;
    CALDOC_TEXT_LINK_TYPECONS;
    CALDOC_TEXT_LINK_TYPECLASS;
    CALDOC_TEXT_LINK_WITHOUT_CONTEXT;

    //virtual tokens are not used in CAL.g or CALTreeParser.g but rather during
    //subsequent static analysis. For example, LITERAL_let trees are converted
    //to (one or more) VIRTUAL_LET_NONREC or VIRTUAL_LET_REC trees depending on
    //the results of local dependency order static analysis.
    VIRTUAL_LET_NONREC;
    VIRTUAL_LET_REC;
    VIRTUAL_DATA_CONSTRUCTOR_CASE;    
    VIRTUAL_RECORD_CASE;
    VIRTUAL_TUPLE_CASE;
    VIRTUAL_UNIT_DATA_CONSTRUCTOR;
}

// Preamble
{
    // Add declarations for CALParser class here

    private CALCompiler compiler;  
 
    /**
     * Construct CALParser from a CALCompiler
     */ 
    CALParser (CALCompiler compiler, TokenStream tStream) {
        this (tStream);
        this.compiler = compiler;
    }

    /**
     * @return a SourceRange representing the position of the syntax error as contained in the given exception.
     */
    public static SourceRange makeSourceRangeFromException(RecognitionException ex) {
        // This is the best we can do given the information from ANTLR
        final SourcePosition startSourcePosition = new SourcePosition(ex.getLine(), ex.getColumn(), ex.getFilename());
        final SourcePosition endSourcePosition = new SourcePosition(ex.getLine(), ex.getColumn()+1, ex.getFilename());
        return new SourceRange(startSourcePosition, endSourcePosition);
    }
    
    /**
     * @return a SourcePosition representing the position of a token.
     */
    private static SourceRange makeSourceRangeFromToken(Token token) {
        // This is the best we can do given the information from ANTLR
        final SourcePosition startSourcePosition = new SourcePosition(token.getLine(), token.getColumn(), token.getFilename());
        final SourcePosition endSourcePosition = new SourcePosition(token.getLine(), token.getColumn()+1, token.getFilename());
        return new SourceRange(startSourcePosition, endSourcePosition);
    }
    
    /**
     * Override reportError method to direct standard error handling through to the CALCompiler error scheme
     * @param ex the recognition exception that originated the problem
     */
    public void reportError (RecognitionException ex) {
        SourceRange sourceRange = makeSourceRangeFromException(ex);
        
        // Special handling of a syntax error arising from having a CALDoc comment in an inadmissible location.
        if (isCALDocAppearingInInadmissibleLocation(ex)) {
            compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.CALDocCommentCannotAppearHere(), ex));
            // to prevent a bunch of meaningless errors from occuring right after this, we consume
            // everything until we see a CALDOC_CLOSE, so that we can regain sanity and resume the parsing properly
            try {
                consumeUntil(CALDOC_CLOSE);
                match(CALDOC_CLOSE);
            } catch (MismatchedTokenException e) {
            } catch (TokenStreamException e) {
            }
        } else {
            // Standard handling of syntax errors
            compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), ex));
        }
    }
    
    /**
     * Convenience method for custom exception handlers; includes the default resync calls.
     * @param ex the recognition exception that caused the problem to be reported
     * @param followSet The $FOLLOW set for the current rule.  Used to consume tokens in
     *                  an attempt to keep compiling after errors.
     */
    private void reportError (RecognitionException ex, BitSet followSet) throws RecognitionException, TokenStreamException {
        reportError(ex);
        consume();
        consumeUntil(followSet);
    }
    
    /**
     * @return true if the exception represents the syntax error of having a CALDoc comment in an inadmissible location; false otherwise.
     */
    private boolean isCALDocAppearingInInadmissibleLocation(RecognitionException ex) {
        if (ex instanceof NoViableAltException) {
            NoViableAltException noViableAltEx = (NoViableAltException)ex;
            return noViableAltEx.token != null && noViableAltEx.token.getType() == CALDOC_OPEN;
        } else if (ex instanceof CALMismatchedTokenException) {
            CALMismatchedTokenException mismatchedTokenEx = (CALMismatchedTokenException)ex;
            return mismatchedTokenEx.token != null && mismatchedTokenEx.token.getType() == CALDOC_OPEN;
        }
        return false;
    }
    
    /**
     * Reports an error for having encountered an unknown CALDoc tag.
     */
    private void reportUnrecognizedTagInCALDocCommentError(Token unknownTag) {
        SourceRange sourceRange = makeSourceRangeFromToken(unknownTag);
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.UnrecognizedTagInCALDocComment(unknownTag.getText())));
    }
    
    /**
     * Reports an error for having encountered an unknown CALDoc inline tag.
     */
    private void reportUnrecognizedInlineTagInCALDocCommentError(Token unknownTag) {
        SourceRange sourceRange = makeSourceRangeFromToken(unknownTag);
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.UnrecognizedInlineTagInCALDocComment("{@" + unknownTag.getText() + "}")));
    }
    
    /**
     * Reports an error for having encountered a CALDoc inline tag in an inadmissable location.
     */
    private void reportCALDocInlineTagAppearingInInadmissibleLocationError(Token openInlineTag, Token surroundingInlineTag) {
        SourceRange sourceRange = makeSourceRangeFromToken(openInlineTag);
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.InlineTagCannotAppearHereInsideCALDocCommentInlineTag("{@" + surroundingInlineTag.getText() + "}")));
    }
    
    /**
     * Reports an error for having encountered a CALDoc inline tag without a corresponding closing tag.
     */
    private void reportMaybeCALDocInlineBlockNotClosedBeforeEndOfTextBlockError(Token openInlineTag, MismatchedTokenException ex) {
        // report a custom error if the expected token is a '@}'.
        if (ex.expecting == CALDOC_CLOSE_INLINE_TAG) {
            SourceRange sourceRange = makeSourceRangeFromToken(openInlineTag);
            compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.InlineTagBlockMissingClosingTagInCALDocComment()));
        } else {
            // it is not the case that the expected token is a '@}', so fall back to the standard reportError mechanism.
            reportError(ex);
        }
    }
    
    /**
     * Reports an error for having encountered a missing context for a CALDoc "@see" or "@link" block.
     */
    private void reportMaybeMissingSeeOrLinkBlockContextInCALDocCommentError(NoViableAltException ex) {
        // report a custom error if the token causing the exception is an EQUALS, i.e. the '=' appears immediately after the "@see" or "@link" tag.
        if (ex.token != null && ex.token.getType() == EQUALS) {
            SourceRange sourceRange = makeSourceRangeFromException(ex);
            compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.MissingSeeOrLinkBlockContextInCALDocComment(), ex));
        } else {
            // it is not the case that the '=' appears immediately after the "@see" or "@link" tag, so fall back to the standard reportError mechanism.
            reportError(ex);
        }
    }
    
    /**
     * Reports an error for having encountered an unknown context for a CALDoc "@see" or "@link" block.
     */
    private void reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError(ParseTreeNode unknownContext) {
        SourceRange sourceRange = unknownContext.getSourceRange();
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.UnrecognizedSeeOrLinkBlockContextInCALDocComment(unknownContext.getText())));
    }
    
    /**
     * Reports an error for having encountered an unassociated CALDoc comment.
     */
    private void reportUnassociatedCALDocCommentError(ParseTreeNode unassociatedComment) {
        SourceRange sourceRange = unassociatedComment.getSourceRange();
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.UnassociatedCALDocComment()));
    }
    
    /**
     * Reports an error for having encountered an invalid reference in a CALDoc "@see" or "@link" block.
     */
    private void reportMaybeUnrecognizedSeeOrLinkBlockReferenceInCALDocCommentError(NoViableAltException ex) {
        // report a custom error if the token causing the exception is a CALDOC_SEE_TAG_UNKNOWN_REFERENCE, i.e. an invalid reference in a CALDoc @see block
        // a VAR_ID, i.e. a lowercase identifier when an uppercase one is expected, or a CONS_ID, i.e. an uppercase identifier when a loewrcase one is expected.
        if (ex.token != null && (ex.token.getType() == CALDOC_SEE_TAG_UNKNOWN_REFERENCE || ex.token.getType() == VAR_ID || ex.token.getType() == CONS_ID)) {
            SourceRange sourceRange = makeSourceRangeFromException(ex);
            compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.UnrecognizedSeeOrLinkBlockReferenceInCALDocComment(ex.token.getText())));
        } else {
            // it is not an invalid reference in a CALDoc @see block, so fall back to the standard reportError mechanism.
            reportError(ex);
        }
    }
    
    /**
     * Reports an error for having encountered a CALDoc comment for a local pattern match decl.
     */
    private void reportLocalPatternMatchDeclCannotHaveCALDocComment(final ParseTreeNode unassociatedComment) {
        SourceRange sourceRange = unassociatedComment.getSourceRange();
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.LocalPatternMatchDeclCannotHaveCALDocComment()));
    }
    
    /**
     * Reports an error for having encountered a wildcard pattern in a pattern match declaration in a let block.
     */
    private void reportInvalidLocalPatternMatchWildcardPatternError(final ParseTreeNode node) {
        SourceRange sourceRange = node.getSourceRange();
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.InvalidLocalPatternMatchWildcardPattern()));
    }
    
    /**
     * Reports an error for having encountered a Nil pattern in a pattern match declaration in a let block.
     */
    private void reportInvalidLocalPatternMatchNilPatternError(final ParseTreeNode node) {
        SourceRange sourceRange = node.getSourceRange();
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.InvalidLocalPatternMatchNilPattern()));
    }
    
    /**
     * Reports an error for having encountered a multiple data cons pattern in a pattern match declaration in a let block.
     */
    private void reportInvalidLocalPatternMatchMultipleDataConsPatternError(final ParseTreeNode node) {
        SourceRange sourceRange = node.getAssemblySourceRange();
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.InvalidLocalPatternMatchMultipleDataConsPattern()));
    }
    
    /**
     * Reports an error for having encountered an int pattern in a pattern match declaration in a let block.
     */
    private void reportInvalidLocalPatternMatchIntPatternError(final ParseTreeNode node) {
        SourceRange sourceRange = node.getAssemblySourceRange();
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.InvalidLocalPatternMatchIntPattern()));
    }
    
    /**
     * Reports an error for having encountered an char pattern in a pattern match declaration in a let block.
     */
    private void reportInvalidLocalPatternMatchCharPatternError(final ParseTreeNode node) {
        SourceRange sourceRange = node.getAssemblySourceRange();
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.InvalidLocalPatternMatchCharPattern()));
    }
    
    /**
     * Report a NoViableAltException from a context that provides a paraphrase.  The parser
     * state will be resynched in the standard way using the provided followSet.
     *
     * @param contextToken Token The first token in the context (this is probably not the next token
     *                     in the current lookahead)
     * @param paraphrase String The string to use as the paraphrase for this context
     * @param firstSet  The $FIRST set for the current rule
     * @param followSet The $FOLLOW set for the current rule.  We will consume the current
     *                  token and then carry on until the follow set in an attempt to resync
     *                  so that we can keep compiling after an error (this mirrors the default
     *                  handling for all RecognitionExceptions)
     */
    private void reportWithParaphrase(RecognitionException ex, Token contextToken, String paraphrase, BitSet firstSet, BitSet followSet) throws RecognitionException, TokenStreamException {

        if(contextToken != null && paraphrase != null && firstSet != null && followSet != null) {
                
            if(!firstSet.member(LA(1))) {
                Token errorToken = LT(1);
                SourceRange errorRange = makeSourceRangeFromToken(errorToken);
                String[] expectedTokens = bitsetToStringArray(firstSet);

                if(expectedTokens.length == 1) {
                    compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.SyntaxErrorWithParaphraseSingleExpected(getTokenDescription(errorToken), expectedTokens[0], getTokenDescription(contextToken), paraphrase)));
                } else {
                  compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.SyntaxErrorWithParaphrase(getTokenDescription(errorToken), expectedTokens, getTokenDescription(contextToken), paraphrase)));
                }

            } else if(!followSet.member(LA(2))) {
                Token errorToken = LT(2);
                SourceRange errorRange = makeSourceRangeFromToken(errorToken);
                String[] expectedTokens = bitsetToStringArray(followSet);
                
                if(expectedTokens.length == 1) {
                    compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.SyntaxErrorWithParaphraseSingleExpected(getTokenDescription(errorToken), expectedTokens[0], getTokenDescription(contextToken), paraphrase)));
                } else {
                    compiler.logMessage(new CompilerMessage(errorRange, new MessageKind.Error.SyntaxErrorWithParaphrase(getTokenDescription(errorToken), expectedTokens, getTokenDescription(contextToken), paraphrase)));
                }

            } else {
                reportError(ex);
            }

        } else {
            reportError(ex);
        }

        consume();
        consumeUntil(followSet);
    }
    
    /**
     * Converts a bitset of token IDs to an array of token names.
     * @param set The BitSet to convert
     * @return a String array of token names
     */
    private String[] bitsetToStringArray(BitSet set) {
        
        String[] stringArray = new String[set.degree()];
        
        int[] tokenIDs = set.toArray();
        for(int i = 0; i < tokenIDs.length; i++) {
            stringArray[i] = _tokenNames[tokenIDs[i]];
        }
        
        return stringArray;
    }

    /**
     * @param token The token to fetch a description for
     * @return String containing either the actual consumed text of the token if available, or the name of the token
     *          (for special tokens such as EOF that consume no text)
     */
    private String getTokenDescription(Token token) {
        String text = token.getText();
        if(text == null) {
            return _tokenNames[token.getType()];
        }
        
        return text;
    }

    /**
     * Report a syntax error along with a suggestion for how to fix it (e.g., by using '==' instead
     * of '=').  Unlike reportWithParaphrase, no resynch is performed (on the assumption that we will
     * instead parse as if we had encountered the suggestion).
     *
     * @param unexpectedToken Token The token that was not expected 
     * @param suggestion String The suggested token
     */
    private void reportWithSuggestion(Token unexpectedToken, String suggestion) {
        SourceRange sourceRange = makeSourceRangeFromToken(unexpectedToken);
        compiler.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxErrorWithSuggestion(unexpectedToken.getText(), suggestion)));
    }
        
    /**
     * Make sure current lookahead symbol matches token type <tt>t</tt>.
     * Throw an exception upon mismatch, which is catch by either the
     * error handler or by the syntactic predicate.
     */
    public void match(int t) throws MismatchedTokenException, TokenStreamException {
        if (LA(1) != t) {
            throw new CALMismatchedTokenException(tokenNames, LT(1), t, getFilename());
        } else {
            // mark token as consumed -- fetch next token deferred until LA/LT
            consume();
        }
    }    
}
// End Preamble


//a start rule for an expression.
codeGemExpr
    : expr[LT(1), "a valid expression"] EOF!
    ;

//a start rule for a type signature.
startTypeSignature
    : typeSignature[LT(1), "a valid type signature"] EOF!
    ;

//a start rule for a module.
module
    : optionalDocComment moduleNameDecl[LT(1), "a valid module name"] 
      importDeclarationList[LT(1), "a list of import declarations"]
      friendDeclarationList[LT(1), "a list of friend declarations"]
      outerDefnList[LT(1), "a list of top-level definitions"]
      EOF!
      {#module = #(#[MODULE_DEFN, "MODULE_DEFN"], module);}
    ;

//a start rule for a module header.
//From a module definition, parses out the module declaration and import declarations, and ignores everything else 
// (thus the lack of outerDefnList and EOF!).
moduleHeader
    : optionalDocComment moduleNameDecl[LT(1), "a valid module name"]
      importDeclarationList[LT(1), "a list of import declarations"]
      {#moduleHeader = #(#[MODULE_DEFN, "MODULE_DEFN"], moduleHeader);}
    ;

// a start rule for an adjunct
adjunct
    : adjunctDefnList[LT(1), "a list of adjunct definitions"] EOF!
    ;

adjunctDefnList[Token contextToken, String paraphrase]
    : (adjunctDefn[LT(1), "a valid adjunct definition"])*
      {#adjunctDefnList = #(#[OUTER_DEFN_LIST, "ADJUNCT_DEFN_LIST"], adjunctDefnList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

// adjunct definitions
adjunctDefn[Token contextToken, String paraphrase]
    : doc:optionalDocComment!
        ( topLevelTypeDeclarationWithOptionalDocComment[#doc, LT(1), "top level type declaration"]
        | instanceDefnWithOptionalDocComment[#doc, LT(1), "instance definition"]
        | topLevelFunctionWithOptionalDocComment[#doc, LT(1), "top level function definition"]
        )
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

moduleNameDecl[Token contextToken, String paraphrase]
    : "module"!
      moduleName[LT(1), "a semicolon-terminated module name"]
      SEMICOLON!
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
importDeclarationList[Token contextToken, String paraphrase]
    : (importDeclaration[LT(1), "an import declaration"])*
      {#importDeclarationList = #(#[IMPORT_DECLARATION_LIST, "IMPORT_DECLARATION_LIST"], importDeclarationList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

importDeclaration[Token contextToken, String paraphrase]
    : importNode:"import"^
      moduleName[LT(1), "a valid module name"]
      maybeUsingClause[LT(1), "a semicolon or a using clause"]
      semicolonNode:SEMICOLON!
      {((ParseTreeNode)#importNode).addOmittedDelimiter(#semicolonNode);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

maybeUsingClause[Token contextToken, String paraphrase]
    : (usingClause[LT(1), "a semicolon-terminated using clause"])?
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

usingClause[Token contextToken, String paraphrase]
    : "using"^
      (usingItem[LT(1), "a valid using item starting with 'function =', 'typeConstructor =', 'dataConstructor =', or 'typeClass ='"])*
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
usingItem[Token contextToken, String paraphrase]
    : "function"^
       EQUALS!
       usingVarList[LT(1), "a semicolon-terminated list of function names"]
       SEMICOLON!
    | "typeConstructor"^ 
       EQUALS!
       usingConsList[LT(1), "a semicolon-terminated list of type constructor names"]
       SEMICOLON!
    | "dataConstructor"^
       EQUALS! 
       usingConsList[LT(1), "a semicolon-terminated list of data constructor names"]
       SEMICOLON!
    | "typeClass"^
      EQUALS!
      usingConsList[LT(1), "a semicolon-terminated list of type class names"]
      SEMICOLON!
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
        
usingConsList[Token contextToken, String paraphrase]
    : (cons (COMMA! cons)*)
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
usingVarList[Token contextToken, String paraphrase]
    : (var (COMMA! var) *)
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
moduleName[Token contextToken, String paraphrase]
    : bareCons:cons
        {#moduleName = #(#[HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME"], #[HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER, "HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER"], #bareCons);}
      ( options { generateAmbigWarnings=false; }
      : dot:DOT^ {#dot.initialize(HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME");}
        cons
      )*
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


friendDeclarationList[Token contextToken, String paraphrase]
    : (friendDeclaration[LT(1), "a friend declaration"])*
      {#friendDeclarationList = #(#[FRIEND_DECLARATION_LIST, "FRIEND_DECLARATION_LIST"], friendDeclarationList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

friendDeclaration[Token contextToken, String paraphrase]
    : "friend"^ 
      moduleName[LT(1), "a valid module name"]
      SEMICOLON!
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }



outerDefnList[Token contextToken, String paraphrase]
    : (outerDefn[LT(1), "an outer definition"])*
      {#outerDefnList = #(#[OUTER_DEFN_LIST, "OUTER_DEFN_LIST"], outerDefnList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


// Outermost definitions
outerDefn[Token contextToken, String paraphrase]
    : doc:optionalDocComment!
        ( topLevelTypeDeclarationWithOptionalDocComment[#doc, LT(1), "a top-level type declaration"]
        | topLevelFunctionWithOptionalDocComment[#doc, LT(1), "a top-level function definition"]
        | foreignDataDeclarationWithOptionalDocComment[#doc, LT(1), "a foreign data declaration"]
        | dataDeclarationWithOptionalDocComment[#doc, LT(1), "a data declaration"]
        | (accessModifier[null,null] "class") => typeClassDefnWithOptionalDocComment[#doc, LT(1), "a class definition"]
        | instanceDefnWithOptionalDocComment[#doc, LT(1), "an instance definition"]
        | primitiveFunctionDeclarationWithOptionalDocComment[#doc, LT(1), "a primitive function declaration"]
        | foreignFunctionDeclarationWithOptionalDocComment[#doc, LT(1), "a foreign function declaration"]
        )
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

//////////////////////////////////////////////////////////////////////////////
// Type declarations
//////////////////////////////////////////////////////////////////////////////

topLevelTypeDeclaration[Token contextToken, String paraphrase]
    : doc:optionalDocComment!
      topLevelTypeDeclarationWithOptionalDocComment[#doc, LT(1), "a top-level type declaration"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

topLevelTypeDeclarationWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : typeDeclaration[LT(1), "a type declaration for a top-level function"]
      {#topLevelTypeDeclarationWithOptionalDocComment = #(#[TOP_LEVEL_TYPE_DECLARATION, "TOP_LEVEL_TYPE_DECLARATION"], doc, topLevelTypeDeclarationWithOptionalDocComment);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

letDefnTypeDeclarationWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : typeDeclaration[LT(1), "a type declaration for a let definition"]
      {#letDefnTypeDeclarationWithOptionalDocComment = #(#[LET_DEFN_TYPE_DECLARATION, "LET_DEFN_TYPE_DECLARATION"], doc, letDefnTypeDeclarationWithOptionalDocComment);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

typeDeclaration[Token contextToken, String paraphrase]
    : var
      node:COLONCOLON^ {#node.initialize (TYPE_DECLARATION, "::");}
      typeSignature[LT(1), "a valid type signature for a variable"]
      SEMICOLON!
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

typeSignature[Token contextToken, String paraphrase]
    : typeContextList[LT(1), "a type context"]
      type[LT(1), "a type"]
      {#typeSignature = #(#[TYPE_SIGNATURE, "TYPE_SIGNATURE"], typeSignature);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

//parsing typeContextList requires the use of syntactic predicates. This is because distinguishing between a declaration with a context
//and without one requires looking for a "=>" (IMPLIES) which may be arbitrarily many tokens ahead, and so not LL(k) for any k.
typeContextList[Token contextToken, String paraphrase]
    : (typeContext[null,null] IMPLIES) => typeContext[LT(1), "a type context"] IMPLIES! 
      {#typeContextList = #(#[TYPE_CONTEXT_SINGLETON, "TYPE_CONTEXT_SINGLETON"], typeContextList);}
    | (OPEN_PAREN typeContextZeroOrMore[null, null] CLOSE_PAREN IMPLIES) =>  
      openParenNode:OPEN_PAREN^ {#openParenNode.initialize (TYPE_CONTEXT_LIST, "TYPE_CONTEXT_LIST");} typeContextZeroOrMore[LT(1), "a comma-separated list of type contexts"] closeParenNode:CLOSE_PAREN! {((ParseTreeNode)#openParenNode).addOmittedDelimiter(#closeParenNode);} IMPLIES!
    | //empty type context
     {#typeContextList = #(#[TYPE_CONTEXT_NOTHING, "TYPE_CONTEXT_NOTHING"]);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

classContextList[Token contextToken, String paraphrase]
    : (classContext[null,null] IMPLIES) => classContext[LT(1), "a class context"] IMPLIES! 
      {#classContextList = #(#[CLASS_CONTEXT_SINGLETON, "CLASS_CONTEXT_SINGLETON"], classContextList);}
    | (OPEN_PAREN classContextZeroOrMore[null, null] CLOSE_PAREN IMPLIES) =>  
      openParenNode:OPEN_PAREN^ {#openParenNode.initialize (CLASS_CONTEXT_LIST, "CLASS_CONTEXT_LIST");} classContextZeroOrMore[LT(1), "a comma-separated list of class contexts"] closeParenNode:CLOSE_PAREN! {((ParseTreeNode)#openParenNode).addOmittedDelimiter(#closeParenNode);} IMPLIES!
    | //empty class context
     {#classContextList = #(#[CLASS_CONTEXT_NOTHING, "CLASS_CONTEXT_NOTHING"]);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
     
typeContextZeroOrMore[Token contextToken, String paraphrase]
    : (typeContext[LT(1), "a type context"] (COMMA! typeContext[LT(1), "a type context"])*)?
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

classContextZeroOrMore[Token contextToken, String paraphrase]
    : (classContext[LT(1), "a class context"] (COMMA! classContext[LT(1), "a class context"])*)?
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

typeContext[Token contextToken, String paraphrase]
    : classContext[LT(1), "a class context"]
    | lacksFieldContext[LT(1), "a lacks constraint"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
        
classContext[Token contextToken, String paraphrase]
    : qualifiedCons[LT(1), "a class name"] var
      {#classContext = #(#[CLASS_CONTEXT, "CLASS_CONTEXT"], classContext);}    
    ;       
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
   
lacksFieldContext[Token contextToken, String paraphrase]
    : var backslash:BACKSLASH^ {#backslash.initialize (LACKS_FIELD_CONTEXT, "LACKS_FIELD_CONTEXT");} fieldName[LT(1), "a valid record-field name"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
 

type[Token contextToken, String paraphrase]
    : typeApplication[LT(1), "a type application"] (node:RARROW^ {#node.initialize (FUNCTION_TYPE_CONSTRUCTOR, "->");} type[LT(1), "a type application"])? // Right assoc - function type
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

typeApplication[Token contextToken, String paraphrase]
    : (atomicType[LT(1), "an atomic type"])+ // Left associative - type application
      {#typeApplication = #(#[TYPE_APPLICATION, "@"], typeApplication);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

atomicType[Token contextToken, String paraphrase]
    : qualifiedCons[LT(1), "a type constructor"] // a type constructor such as 'Boolean' or 'TreeModule.Tree'
    | var              // a type variable such as 'a'. Cannot be qualified.
    // 0 types, the trivial type
    // 1 type, a parenthesized type expression
    // 2 or more types, a tuple type
    | openParenNode:OPEN_PAREN^ {#openParenNode.initialize (TUPLE_TYPE_CONSTRUCTOR, "Tuple");} typeListZeroOrMore[LT(1), "a comma-separated list of component types"] closeParenNode:CLOSE_PAREN! {((ParseTreeNode)#openParenNode).addOmittedDelimiter(#closeParenNode);}
    | openBracketNode:OPEN_BRACKET^ {#openBracketNode.initialize (LIST_TYPE_CONSTRUCTOR, "List");} type[LT(1), "an element type"] closeBracketNode:CLOSE_BRACKET! {((ParseTreeNode)#openBracketNode).addOmittedDelimiter(#closeBracketNode);} // list type
    | recordType[LT(1), "a record type"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

recordType[Token contextToken, String paraphrase]
    : openBraceNode:OPEN_BRACE^ {#openBraceNode.initialize (RECORD_TYPE_CONSTRUCTOR, "Record");} recordVar[LT(1), "a base-record type variable"] fieldTypeAssignmentList[LT(1), "a comma-separated list of field type assignments"] closeBraceNode:CLOSE_BRACE! {((ParseTreeNode)#openBraceNode).addOmittedDelimiter(#closeBraceNode);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

recordVar[Token contextToken, String paraphrase]
    : (var BAR) => (var BAR!) 
      {#recordVar = #(#[RECORD_VAR, "RECORD_VAR"], recordVar);}
    | (var CLOSE_BRACE) => (var)
      {#recordVar = #(#[RECORD_VAR, "RECORD_VAR"], recordVar);}          
    | //no record var supplied
      {#recordVar = #(#[RECORD_VAR, "RECORD_VAR"]);}
    ;       
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

fieldTypeAssignmentList[Token contextToken, String paraphrase]
    : (fieldTypeAssignment[LT(1), "a field type assignment"] (COMMA! fieldTypeAssignment[LT(1), "a field type assignment"])*)? 
      {#fieldTypeAssignmentList = #(#[FIELD_TYPE_ASSIGNMENT_LIST, "FIELD_TYPE_ASSIGNMENT_LIST"], fieldTypeAssignmentList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
fieldTypeAssignment[Token contextToken, String paraphrase]
    : fieldName[LT(1), "a valid record-field name"] node:COLONCOLON^ {#node.initialize (FIELD_TYPE_ASSIGNMENT, "FIELD_TYPE_ASSIGNMENT");} type[LT(1), "a field type"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
        

typeListZeroOrMore[Token contextToken, String paraphrase]
    : (type[contextToken, paraphrase] (COMMA! type[contextToken, paraphrase])*)?
    ;  
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

//////////////////////////////////////////////////////////////////////////////
// Foreign data declaration
//////////////////////////////////////////////////////////////////////////////

foreignDataDeclaration[Token contextToken, String paraphrase]
    : doc:optionalDocComment! foreignDataDeclarationWithOptionalDocComment[#doc, contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

foreignDataDeclarationWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : node:"data"^ {#node.initialize (FOREIGN_DATA_DECLARATION, "foreignData");}
      "foreign"! "unsafe"! "import"! "jvm"! accessModifier[LT(1), "an access modifier"] externalName accessModifier[LT(1), "an access modifier"] cons maybeDerivingClause[LT(1), "a deriving clause or a semicolon"] SEMICOLON!
      {
        AST firstChild = #foreignDataDeclarationWithOptionalDocComment.getFirstChild();
        #doc.setNextSibling(firstChild);
        #foreignDataDeclarationWithOptionalDocComment.setFirstChild(#doc);
      }
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

//////////////////////////////////////////////////////////////////////////////
// Data declarations
//////////////////////////////////////////////////////////////////////////////

dataDeclaration[Token contextToken, String paraphrase]
    : doc:optionalDocComment! dataDeclarationWithOptionalDocComment[#doc, contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

dataDeclarationWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : node:"data"^ {#node.initialize (DATA_DECLARATION, "data");} simpleType[LT(1), "a type constructor"] EQUALS! dataDeclarationBody[LT(1), "a bar-separated list of data constructor definitions"] SEMICOLON!
      {
        AST firstChild = #dataDeclarationWithOptionalDocComment.getFirstChild();
        #doc.setNextSibling(firstChild);
        #dataDeclarationWithOptionalDocComment.setFirstChild(#doc);
      }
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

dataDeclarationBody[Token contextToken, String paraphrase]
    : dataConstructorDefnList[LT(1), "a bar-separated list of data constructor definitions"] 
      maybeDerivingClause[LT(1), "a bar, a deriving clause, or a semicolon"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

maybeDerivingClause[Token contextToken, String paraphrase]
    : (derivingClause[LT(1), "a deriving clause"])?
    ;    
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

simpleType[Token contextToken, String paraphrase]
    : accessModifier[LT(1), "an access modifier"] typeConsName[LT(1), "a type constructor name"] typeConsParamList[LT(1), "a list of type constructor parameters"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

typeConsName[Token contextToken, String paraphrase]
    : cons
    ;    
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

typeConsParamList[Token contextToken, String paraphrase]
    : (var)*
      {#typeConsParamList = #(#[TYPE_CONS_PARAM_LIST, "TYPE_CONS_PARAM_LIST"], typeConsParamList);}
    ;    
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

dataConstructorDefnList[Token contextToken, String paraphrase]
    : dataConstructorDefn[LT(1), "a data constructor definition"] (BAR! dataConstructorDefn[LT(1), "a data constructor definition"])*
      {#dataConstructorDefnList = #(#[DATA_CONSTRUCTOR_DEFN_LIST, "DATA_CONSTRUCTOR_DEFN_LIST"], dataConstructorDefnList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
dataConstructorDefn [Token contextToken, String paraphrase]
    : optionalDocComment accessModifier[LT(1), "an access modifier"] cons dataConstructorArgList[LT(1), "a list of data constructor arguments"]
      {#dataConstructorDefn = #(#[DATA_CONSTRUCTOR_DEFN, "DATA_CONSTRUCTOR_DEFN"], dataConstructorDefn);}
    ; 
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

// data T a b = T (a->b) --ok, the parentheses are required.
dataConstructorArgList[Token contextToken, String paraphrase]
    : (dataConstructorArg[LT(1), "a data constructor argument"])*
      {#dataConstructorArgList = #(#[DATA_CONSTRUCTOR_ARG_LIST, "DATA_CONSTRUCTOR_ARG_LIST"], dataConstructorArgList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

dataConstructorArg[Token contextToken, String paraphrase]
    : fieldName[LT(1), "a valid argument name"] node:COLONCOLON^ {#node.setType (DATA_CONSTRUCTOR_NAMED_ARG);} dataConstructorArgType[LT(1), "a type for the data constructor argument"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

dataConstructorArgType[Token contextToken, String paraphrase]
    : (node:PLING^ {{#node.initialize (STRICT_ARG, "!");}} atomicType[contextToken, paraphrase])
    | atomicType[contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
derivingClause[Token contextToken, String paraphrase]
    : ("deriving"^ qualifiedCons[LT(1), "a type class name"] (COMMA! qualifiedCons[LT(1), "a type class name"])*) //e.g. deriving Prelude.Eq, Ord, Debug.Show
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
        
//////////////////////////////////////////////////////////////////////////////
// type class definitions
//////////////////////////////////////////////////////////////////////////////

typeClassDefn[Token contextToken, String paraphrase]
    : doc:optionalDocComment! typeClassDefnWithOptionalDocComment[#doc, contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

typeClassDefnWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : accessModifier[LT(1), "an access modifier"]
      node:"class"^ {#node.initialize (TYPE_CLASS_DEFN, "class");}
      classContextList[LT(1), "a class context"]
      typeClassName[LT(1), "a type class name"]
      typeClassParam[LT(1), "a type variable"]
      "where"!
      classMethodList[LT(1), "a list of class method definitions"]
      SEMICOLON!
      {
        AST firstChild = #typeClassDefnWithOptionalDocComment.getFirstChild();
        #doc.setNextSibling(firstChild);
        #typeClassDefnWithOptionalDocComment.setFirstChild(#doc);
      }
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

typeClassName[Token contextToken, String paraphrase]
    : cons
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
typeClassParam[Token contextToken, String paraphrase]
    : var
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

classMethodList[Token contextToken, String paraphrase]
    : (classMethod[LT(1), "a class method definition"])*
      {#classMethodList = #(#[CLASS_METHOD_LIST, "CLASS_METHOD_LIST"], classMethodList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
classMethod[Token contextToken, String paraphrase]
    : optionalDocComment
      accessModifier[LT(1), "an access modifier"]
      classMethodName[LT(1), "a class method name"]
      COLONCOLON!
      typeSignature[LT(1), "a type signature for a method"]
      ("default"! defaultClassMethodName[LT(1), "a default class method name"])? SEMICOLON!
      {#classMethod = #(#[CLASS_METHOD, "CLASS_METHOD"], classMethod);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
        
classMethodName[Token contextToken, String paraphrase]
    : var
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
defaultClassMethodName[Token contextToken, String paraphrase]
    : qualifiedVar[contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }    
    
//////////////////////////////////////////////////////////////////////////////
// class instance definitions
//////////////////////////////////////////////////////////////////////////////

instanceDefn[Token contextToken, String paraphrase]
    : doc:optionalDocComment! instanceDefnWithOptionalDocComment[#doc, contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
instanceDefnWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : node:"instance"^ {#node.initialize (INSTANCE_DEFN, "instance");} instanceName[LT(1), "an instance name"] "where"! instanceMethodList[LT(1), "a list of instance method definitions"] SEMICOLON!
      {
        AST firstChild = #instanceDefnWithOptionalDocComment.getFirstChild();
        #doc.setNextSibling(firstChild);
        #instanceDefnWithOptionalDocComment.setFirstChild(#doc);
      }
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

//some examples of instance names:
// Eq Int
// Eq a => Eq (List a)
// Eq a => Eq [a]
// (Eq a, Eq b) => Eq (LegacyTuple.Tuple2 a b)
// (Typeable a, Typeable b) => Typeable (a -> b)    
// (Ord r) => Ord {r}
instanceName[Token contextToken, String paraphrase]
    : classContextList[LT(1), "a class context"] qualifiedCons[LT(1), "a class name"] instanceTypeConsName[LT(1), "a type constructor name"]
      {#instanceName = #(#[INSTANCE_NAME, "INSTANCE_NAME"], instanceName);}     
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
instanceTypeConsName[Token contextToken, String paraphrase]
    : qualifiedCons[contextToken, paraphrase] {#instanceTypeConsName = #(#[UNPARENTHESIZED_TYPE_CONSTRUCTOR, "UNPARENTHESIZED_TYPE_CONSTRUCTOR"], instanceTypeConsName);}
    | (OPEN_PAREN qualifiedCons[null,null]) =>
      openParenNode:OPEN_PAREN^ {#openParenNode.initialize (GENERAL_TYPE_CONSTRUCTOR, "GENERAL_TYPE_CONSTRUCTOR");} qualifiedCons[contextToken, paraphrase] (var)* closeParenNode:CLOSE_PAREN! {((ParseTreeNode)#openParenNode).addOmittedDelimiter(#closeParenNode);}
    | (OPEN_PAREN var RARROW) => 
      OPEN_PAREN! var node:RARROW^ {#node.initialize (FUNCTION_TYPE_CONSTRUCTOR, "->");} var CLOSE_PAREN! // function type
    | openParenNode2:OPEN_PAREN^ {#openParenNode2.initialize (UNIT_TYPE_CONSTRUCTOR, "Unit");} closeParenNode2:CLOSE_PAREN! {((ParseTreeNode)#openParenNode2).addOmittedDelimiter(#closeParenNode2);} //unit type ()
    | openBracketNode:OPEN_BRACKET^ {#openBracketNode.initialize (LIST_TYPE_CONSTRUCTOR, "List");} var closeBracketNode:CLOSE_BRACKET! {((ParseTreeNode)#openBracketNode).addOmittedDelimiter(#closeBracketNode);} // list type    
    | openBraceNode:OPEN_BRACE^ {#openBraceNode.initialize (RECORD_TYPE_CONSTRUCTOR, "Record");} var closeBraceNode:CLOSE_BRACE! {((ParseTreeNode)#openBraceNode).addOmittedDelimiter(#closeBraceNode);} // record type   
    ;    
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
instanceMethodList[Token contextToken, String paraphrase]
    : (instanceMethod[LT(1), "an instance method definition"])*
      {#instanceMethodList = #(#[INSTANCE_METHOD_LIST, "INSTANCE_METHOD_LIST"], instanceMethodList);}    
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
instanceMethod[Token contextToken, String paraphrase]
    : doc:optionalDocComment instanceMethodName[LT(1), "a method name"] node:EQUALS^ {#node.initialize (INSTANCE_METHOD, "INSTANCE_METHOD");} instanceMethodDefn[LT(1), "a function or method name"] SEMICOLON!
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
instanceMethodName[Token contextToken, String paraphrase]
    : var
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
instanceMethodDefn[Token contextToken, String paraphrase]
    : qualifiedVar[contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

//////////////////////////////////////////////////////////////////////////////
// foreign function declarations
//////////////////////////////////////////////////////////////////////////////

//e.g. foreign unsafe import jvm "java.lang.Math.sin" public sin :: Double -> Double;
foreignFunctionDeclaration[Token contextToken, String paraphrase]
    : doc:optionalDocComment! foreignFunctionDeclarationWithOptionalDocComment[#doc, contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

foreignFunctionDeclarationWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : node:"foreign"^ {#node.initialize (FOREIGN_FUNCTION_DECLARATION, "foreignFunction");} "unsafe"! "import"! "jvm"! externalName accessModifier[LT(1), "an access modifier"] typeDeclaration[LT(1), "a type declaration for a foreign function"]
      {
        AST firstChild = #foreignFunctionDeclarationWithOptionalDocComment.getFirstChild();
        #doc.setNextSibling(firstChild);
        #foreignFunctionDeclarationWithOptionalDocComment.setFirstChild(#doc);
      }
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
externalName
    : STRING_LITERAL
    ;
    
//////////////////////////////////////////////////////////////////////////////
// primitive function declarations
//////////////////////////////////////////////////////////////////////////////       
    
primitiveFunctionDeclaration[Token contextToken, String paraphrase]
    : doc:optionalDocComment! primitiveFunctionDeclarationWithOptionalDocComment[#doc, contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

primitiveFunctionDeclarationWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : node:"primitive"^ {#node.initialize (PRIMITIVE_FUNCTION_DECLARATION, "primitiveFunc");} accessModifier[LT(1), "an access modifier"] typeDeclaration[LT(1), "a type declaration for a primitive function"]
      {
        AST firstChild = #primitiveFunctionDeclarationWithOptionalDocComment.getFirstChild();
        #doc.setNextSibling(firstChild);
        #primitiveFunctionDeclarationWithOptionalDocComment.setFirstChild(#doc);
      }
    ;    
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
//////////////////////////////////////////////////////////////////////////////
// Functions
//////////////////////////////////////////////////////////////////////////////

topLevelFunction[Token contextToken, String paraphrase]
    : doc:optionalDocComment! topLevelFunctionWithOptionalDocComment[#doc, contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

topLevelFunctionWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : accessModifier[LT(1), "an access modifier"] functionName[LT(1), "a top-level function name"] functionParamList[LT(1), "a parameter list for a top-level function"] node:EQUALS^ {#node.initialize (TOP_LEVEL_FUNCTION_DEFN, "TOP_LEVEL_FUNCTION_DEFN");} expr[LT(1), "a valid defining expression for a top-level function"] semicolonNode:SEMICOLON! {((ParseTreeNode)#node).addOmittedDelimiter(#semicolonNode);}
      {
        AST firstChild = #topLevelFunctionWithOptionalDocComment.getFirstChild();
        #doc.setNextSibling(firstChild);
        #topLevelFunctionWithOptionalDocComment.setFirstChild(#doc);
      }
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

accessModifier[Token contextToken, String paraphrase]
    : ("public" | "private" | "protected")?
      {#accessModifier = #(#[ACCESS_MODIFIER, "ACCESS_MODIFIER"], accessModifier);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

functionName[Token contextToken, String paraphrase]
    : var
    ;    
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

//formal arguments of a function (or local function, or lambda)    
functionParamList[Token contextToken, String paraphrase]
    : (functionParam[LT(1), "a function parameter name"])*
    {#functionParamList = #(#[FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST"], functionParamList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

functionParam[Token contextToken, String paraphrase]
    : node1:VAR_ID {#node1.setType (LAZY_PARAM);}
    | PLING! node2:VAR_ID {#node2.setType (STRICT_PARAM);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
          
lambdaParamList[Token contextToken, String paraphrase]
    : (functionParam[LT(1), "a lambda parameter name"])+
    {#lambdaParamList = #(#[FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST"], lambdaParamList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

// Expression grammar 
// We deal with precedence (1 lowest) and associativity 
// Used to use ANTLR's syntactic predicates for keep the grammar definition more readable by allowing common left patterns for
// production alternatives.  This proved to be a big mistake because of the use of true tail recursion in the expression grammar
// (for right associative alternatives).  The expression grammar is now fully left factored - resulting in > 2 orders of magnitude
// speedup in the resultant parser!

expr[Token contextToken, String paraphrase]        
    // Process expression intermediate forms 
    :  "let"^ letDefnList[LT(1), "a valid list of let definitions"] "in"! expr[LT(1), "a valid let body"]
    |  "if"^ expr[LT(1), "a valid conditional expression for an if expression"] "then"! expr[LT(1), "a valid then-clause expression"] "else"! expr[LT(1), "a valid else-clause expression"]
    |  "case"^ expr[LT(1), "a valid condition expression for a case expression"] "of"! altList[LT(1), "a list of case alternatives"]
    |  node:BACKSLASH^ {#node.setType (LAMBDA_DEFN);} lambdaParamList[LT(1), "a valid parameter list for a lambda expression"] RARROW! expr[LT(1), "a valid defining expression for a lambda expression"]  // lambda expressions
    |  typeSignatureExpr[contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

    
typeSignatureExpr[Token contextToken, String paraphrase]
    : applyOpExpr[contextToken, paraphrase] (node:COLONCOLON^ {#node.setType (EXPRESSION_TYPE_SIGNATURE);} typeSignature[LT(1), "a valid type signature"])?
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

    
applyOpExpr[Token contextToken, String paraphrase]
    : orExpr[contextToken, paraphrase] (DOLLAR^ applyOpExpr[contextToken, paraphrase])? // Right assoc - low-precedence application
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


orExpr[Token contextToken, String paraphrase]
    : andExpr[contextToken, paraphrase] (BARBAR^ orExpr[contextToken, paraphrase])? // Right assoc - boolean OR
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


andExpr[Token contextToken, String paraphrase]
    : relationalExpr[contextToken, paraphrase] (AMPERSANDAMPERSAND^ andExpr[contextToken, paraphrase])? // Right assoc - boolean AND
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


relationalExpr[Token contextToken, String paraphrase]
    : listOpExpr[contextToken, paraphrase] ((LESS_THAN^ |
                                   LESS_THAN_OR_EQUALS^ |
                                   EQUALSEQUALS^ |
                                   EQUALS^ {reportWithSuggestion(LT(0), "the equality operator ('==')");} |
                                   NOT_EQUALS^ |
                                   GREATER_THAN_OR_EQUALS^ |
                                   GREATER_THAN^) listOpExpr[contextToken, paraphrase])? // Non assoc - Relational operators
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

listOpExpr[Token contextToken, String paraphrase]
    : addExpr[contextToken, paraphrase] ((COLON^ | PLUSPLUS^) listOpExpr[contextToken, paraphrase])? //Right assoc - list operators (cons and append)
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


addExpr[Token contextToken, String paraphrase]
    : multiplyExpr[contextToken, paraphrase] ((PLUS^ | MINUS^) multiplyExpr[contextToken, paraphrase])* // Left assoc
    ; 
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


multiplyExpr[Token contextToken, String paraphrase]
    : unaryExpr[contextToken, paraphrase] ((ASTERISK^ | SOLIDUS^ | PERCENT^) unaryExpr[contextToken, paraphrase])* // Left assoc
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


//unary minus is equivalent to Prelude.negate
unaryExpr[Token contextToken, String paraphrase]
    : minusNode:MINUS^ {#minusNode.setType(UNARY_MINUS);} unaryExpr[contextToken, paraphrase]
    | userOpExpr[contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


// Antlr does not allow the ^ symbol to be used on non-token symbols so I have
// to do all this convoluted manipulation in order to make the op into the 
// root of the tree node.

// Here, we give the same precedence to the backquoted operator `a` and the compose operator #.
// The backquoted operator is left-associative, while the compose operator is right-associative.
// Therefore, we disallow the use of both operators in the same infix expression. For example,
// both:
//    a # b `c` d
// and
//    a `b` c # d
// are syntactically invalid in CAL.

userOpExpr[Token contextToken, String paraphrase]
{ AST #result = null; }
    : arg1:applicationExpr[contextToken, paraphrase]! {#result = #arg1;}
      (  (opA:backquotedOperator[contextToken, paraphrase]! arg2A:applicationExpr[contextToken, paraphrase]! {#result = #([BACKQUOTE, "`"],#(#[APPLICATION, "@"], opA, result, arg2A));})* // Left assoc
       | (opB:POUND!              arg2B:composeOpExpr[contextToken, paraphrase]!   {#result = #(opB, result, arg2B);}) // Right assoc
      )
      {#userOpExpr = #result;}
;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


backquotedOperator[Token contextToken, String paraphrase]
    : BACKQUOTE! ((var | moduleName[null,null] DOT var) => qualifiedVar[LT(1), "a function or method name"] | qualifiedCons[LT(1), "a data constructor name"]) BACKQUOTE!
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


composeOpExpr[Token contextToken, String paraphrase]
    : applicationExpr[contextToken, paraphrase] (POUND^ composeOpExpr[contextToken, paraphrase])? // Right assoc - compose operator
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


applicationExpr[Token contextToken, String paraphrase]
    : (selectFieldExpr[contextToken, paraphrase])+ // Left assoc - Application
      {#applicationExpr = #(#[APPLICATION, "@"], applicationExpr);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


// Dot operator -- field citation.
// Note that this is allowed: 
//   (function appl.).ModuleDCName.UnqualifiedDCName.dcArgFieldName.recordVarName.recordFieldName.ModuleDCName.UnqualifiedDCName...
selectFieldExpr[Token contextToken, String paraphrase]
    // Left assoc.
    : atomicExpr[contextToken, paraphrase] 
      // field selection from a record
      (  (selectRecordFieldNode:DOT^ {#selectRecordFieldNode.initialize (SELECT_RECORD_FIELD, "SELECT_RECORD_FIELD");} fieldName[LT(1), "a valid record-field name"])
      
      // field extraction from a data constructor
      // Note:  The syntactic predicate for qualified Cons's in qualifiedCons only matches against the first two tokens (moduleName DOT).
      //   This causes a problem in constructs such as (expression).UnqualifiedDCName.fieldName, where UnqualifiedDCName will be matched
      //   as the module name because of the DOT which follows it.
      //   Rather than change qualifiedCons to match against an extra token, we duplicate the rule and match against the three tokens here only.
       | (selectDCFieldNode:DOT^ {#selectDCFieldNode.initialize (SELECT_DATA_CONSTRUCTOR_FIELD, "SELECT_DATA_CONSTRUCTOR_FIELD");} 
         (  (cons DOT cons) => qualifiedCons_qualified[LT(1), "a qualified data constructor name"]     // See note above.
          | qualifiedCons_unqualified[LT(1), "a constructor name"]
         )
          DOT!
          fieldName[LT(1), "a valid field name"])
      )*
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

       

atomicExpr[Token contextToken, String paraphrase] // 'Atomic' expression
    : (var | moduleName[null, null] DOT var) => qualifiedVar[contextToken, paraphrase]
    | qualifiedCons[contextToken, paraphrase]
    | literal[contextToken, paraphrase]
    // 0 expressions, a value of the trivial data type
    // 1 expression, a parenthesized expression
    // 2 or more expressions, a tuple data value 
    | openParenNode:OPEN_PAREN^ {#openParenNode.initialize (TUPLE_CONSTRUCTOR, "Tuple");} exprListZeroOrMore[LT(1), "a comma-separated list of expressions"] closeParenNode:CLOSE_PAREN! {((ParseTreeNode)#openParenNode).addOmittedDelimiter(#closeParenNode);}
    // list data value
    | openBracketNode:OPEN_BRACKET^ {#openBracketNode.initialize (LIST_CONSTRUCTOR, "List");} exprListZeroOrMore[LT(1), "a comma-separated list of expressions"] closeBracketNode:CLOSE_BRACKET! {((ParseTreeNode)#openBracketNode).addOmittedDelimiter(#closeBracketNode);}
    | recordValue[contextToken, paraphrase]
    ; 
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }


recordValue[Token contextToken, String paraphrase]
    : openBraceNode:OPEN_BRACE^ {#openBraceNode.initialize (RECORD_CONSTRUCTOR, "Record");} 
      baseRecord[LT(1), "a base-record expression"]
      fieldModificationList[LT(1), "a list of field extensions or field value updates"]
      closeBraceNode:CLOSE_BRACE! {((ParseTreeNode)#openBraceNode).addOmittedDelimiter(#closeBraceNode);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
baseRecord[Token contextToken, String paraphrase]
    : (expr[null,null] BAR) => (expr[LT(1), "a base-record expression"] BAR!) 
      {#baseRecord = #(#[BASE_RECORD, "BASE_RECORD"], baseRecord);}
    | //no base record supplied e.g. {field1 = 10.0}. This really means {{} | field1 = 10.0}.
      {#baseRecord = #(#[BASE_RECORD, "BASE_RECORD"]);}
    ;       
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

fieldModificationList[Token contextToken, String paraphrase]
    : (fieldModification [LT(1), "a field extension or field value update"] (COMMA! fieldModification[LT(1), "a field extension or field value update"])*)? 
      {#fieldModificationList = #(#[FIELD_MODIFICATION_LIST, "FIELD_MODIFICATION_LIST"], fieldModificationList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
fieldModification[Token contextToken, String paraphrase]
    : fieldName[LT(1), "a valid record-field name"]
      (node:EQUALS^ {#node.initialize (FIELD_EXTENSION, "FIELD_EXTENSION");}
       | node2:COLONEQUALS^ {#node2.initialize (FIELD_VALUE_UPDATE, "FIELD_VALUE_UPDATE");})
      expr[LT(1), "an expression"]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
fieldName[Token contextToken, String paraphrase]
    : var
    | ORDINAL_FIELD_NAME
    ;     
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
  

exprListZeroOrMore[Token contextToken, String paraphrase]
    : (expr[contextToken, paraphrase] (COMMA! expr[contextToken, paraphrase])*)?
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

qualifiedVar[Token contextToken, String paraphrase] 
    : var
     {#qualifiedVar = #(#[QUALIFIED_VAR, "QUALIFIED_VAR"], #[HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER, "HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER"], qualifiedVar);}
    | moduleName[LT(1), "a valid module name"] qualifiedVarNode:DOT^ {#qualifiedVarNode.initialize (QUALIFIED_VAR, "QUALIFIED_VAR");} var
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

//the syntactic predicate (moduleName DOT) is required because we want to parse M.f as a qualified cons, and not as a cons followed by
//a field selection with fieldName f. The problem is that DOT is overloaded...
qualifiedCons[Token contextToken, String paraphrase]
    : (cons DOT) => qualifiedCons_qualified[contextToken, paraphrase]
    | qualifiedCons_unqualified[contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
qualifiedCons_qualified[Token contextToken, String paraphrase]
    : bareCons:cons
        {#qualifiedCons_qualified = #(#[HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME"], #[HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER, "HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER"], #bareCons);}
      ( options { generateAmbigWarnings=false; }
      : dot:DOT^ {#dot.initialize(HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME");}
        cons
      )+
      // change the top level type to QUALIFIED_CONS
      {#qualifiedCons_qualified.initialize(QUALIFIED_CONS, "QUALIFIED_CONS");}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

qualifiedCons_unqualified[Token contextToken, String paraphrase]
    : cons {#qualifiedCons_unqualified = #(#[QUALIFIED_CONS, "QUALIFIED_CONS"], #[HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER, "HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER"], qualifiedCons_unqualified);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }



/* End of expression grammar */

// Definitions
letDefnList[Token contextToken, String paraphrase]
    : ( letDefnOrTypeDeclarationOrPatternMatchDecl[contextToken, paraphrase] )+
      {#letDefnList = #(#[LET_DEFN_LIST, "LET_DEFN_LIST"], letDefnList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

letDefnOrTypeDeclarationOrPatternMatchDecl[Token contextToken, String paraphrase]
    : doc:optionalDocComment!
      ( (CONS_ID | OPEN_PAREN | OPEN_BRACE | (VAR_ID | UNDERSCORE) COLON | OPEN_BRACKET | CHAR_LITERAL | INTEGER_LITERAL | MINUS | UNDERSCORE) => // this is the 2-token-lookahead FIRST SET for letPatternMatchDecl, which unfortunately must be present as a predicate in order for antlr to resolve an ambiguity involving the 2-token-lookahead of VAR_ID EQUALS (which isn't even accepted by letPatternMatchDecl but antlr cannot figure this out!)
        letPatternMatchDecl[#doc, LT(1), "a local pattern match declaration"]
      | letDefnWithOptionalDocComment[#doc, LT(1), "a let definition"]
      | letDefnTypeDeclarationWithOptionalDocComment[#doc, LT(1), "a type declaration for a let definition"]
      )
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

letDefnWithOptionalDocComment[AST doc, Token contextToken, String paraphrase]
    : functionName[LT(1), "a function name for a let definition"] functionParamList[LT(1), "a list of function parameters for a let definition"] node:EQUALS^ {#node.initialize (LET_DEFN, "LET_DEFN");} expr[LT(1), "valid defining expression for a let definition"] semicolonNode:SEMICOLON! {((ParseTreeNode)#letDefnWithOptionalDocComment).addOmittedDelimiter(#semicolonNode);}
      {
        AST firstChild = #letDefnWithOptionalDocComment.getFirstChild();
        #doc.setNextSibling(firstChild);
        #letDefnWithOptionalDocComment.setFirstChild(#doc);
      }
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

letPatternMatchDecl[AST doc, Token contextToken, String paraphrase]
    : letPatternMatchPattern[LT(1), "a pattern for a local pattern match declaration"]
      node:EQUALS^ {#node.initialize (LET_PATTERN_MATCH_DECL, "LET_PATTERN_MATCH_DECL");}
      expr[LT(1), "valid defining expression for a local pattern match declaration"]
      SEMICOLON!
      {
      	if (#doc != null && #doc.getFirstChild() != null) {
            // a CALDoc comment is not allowed for a pattern match decl
            reportLocalPatternMatchDeclCannotHaveCALDocComment((ParseTreeNode)#doc.getFirstChild());
      	}
      }
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

letPatternMatchPattern[Token contextToken, String paraphrase]
    : dataConstructorSingleton[contextToken, paraphrase] dataConstructorArgumentBindings[contextToken, paraphrase] {#letPatternMatchPattern = #(#[PATTERN_CONSTRUCTOR, "PATTERN_CONSTRUCTOR"], letPatternMatchPattern);}
    | openParenNode:OPEN_PAREN^ {#openParenNode.initialize (TUPLE_CONSTRUCTOR, "Tuple");} (patternVar[contextToken, paraphrase] (COMMA! patternVar[contextToken, paraphrase])*)? closeParenNode:CLOSE_PAREN! {((ParseTreeNode)#openParenNode).addOmittedDelimiter(#closeParenNode);}
    | patternVar[contextToken, paraphrase] COLON^ patternVar[contextToken, paraphrase]
    | recordPattern[contextToken, paraphrase]
    // the following are patterns that are allowed in case expressions but disallowed here... they are here to act as "error traps" to produce better error messages
    | dataConstructorNameListOneOrMore:dataConstructorNameListOneOrMore[contextToken, paraphrase] dataConstructorArgumentBindings[contextToken, paraphrase]
        {reportInvalidLocalPatternMatchMultipleDataConsPatternError((ParseTreeNode)#dataConstructorNameListOneOrMore);}      
    | openBracketNode:OPEN_BRACKET^ CLOSE_BRACKET!
        {reportInvalidLocalPatternMatchNilPatternError((ParseTreeNode)#openBracketNode);}      
    | maybeMinusIntSingleton:maybeMinusIntSingleton[contextToken, paraphrase]
        {reportInvalidLocalPatternMatchIntPatternError((ParseTreeNode)#maybeMinusIntSingleton);}      
    | maybeMinusIntListOneOrMore:maybeMinusIntListOneOrMore[contextToken, paraphrase]
        {reportInvalidLocalPatternMatchIntPatternError((ParseTreeNode)#maybeMinusIntListOneOrMore);}      
    | charSingleton:charSingleton
        {reportInvalidLocalPatternMatchCharPatternError((ParseTreeNode)#charSingleton);}      
    | charListOneOrMore:charListOneOrMore
        {reportInvalidLocalPatternMatchCharPatternError((ParseTreeNode)#charListOneOrMore);}      
    | wildcard:wildcard
        {reportInvalidLocalPatternMatchWildcardPatternError((ParseTreeNode)#wildcard);}      
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

// Alternatives
altList[Token contextToken, String paraphrase]
    : (alt[LT(1), "a case alternative"])+
      {#altList = #(#[ALT_LIST, "ALT_LIST"], altList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

alt[Token contextToken, String paraphrase] 
    : pat[contextToken, paraphrase] RARROW! expr[LT(1), "a semicolon-terminated expression"] SEMICOLON!
      {#alt = #(#[ALT, "ALT"], alt);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

pat[Token contextToken, String paraphrase]
    : dataConstructorSingleton[contextToken, paraphrase]         dataConstructorArgumentBindings[contextToken, paraphrase] {#pat = #(#[PATTERN_CONSTRUCTOR, "PATTERN_CONSTRUCTOR"], pat);}
    | dataConstructorNameListOneOrMore[contextToken, paraphrase] dataConstructorArgumentBindings[contextToken, paraphrase] {#pat = #(#[PATTERN_CONSTRUCTOR, "PATTERN_CONSTRUCTOR"], pat);}
    | openParenNode:OPEN_PAREN^ {#openParenNode.initialize (TUPLE_CONSTRUCTOR, "Tuple");} (patternVar[contextToken, paraphrase] (COMMA! patternVar[contextToken, paraphrase])*)? closeParenNode:CLOSE_PAREN! {((ParseTreeNode)#openParenNode).addOmittedDelimiter(#closeParenNode);}
    | openBracketNode:OPEN_BRACKET^ {#openBracketNode.initialize (LIST_CONSTRUCTOR, "List");} closeBracketNode:CLOSE_BRACKET! {((ParseTreeNode)#openBracketNode).addOmittedDelimiter(#closeBracketNode);}
    | maybeMinusIntSingleton[contextToken, paraphrase]     {#pat = #(#[INT_PATTERN, "INT_PATTERN"], pat);}
    | maybeMinusIntListOneOrMore[contextToken, paraphrase] {#pat = #(#[INT_PATTERN, "INT_PATTERN"], pat);}
    | charSingleton     {#pat = #(#[CHAR_PATTERN, "CHAR_PATTERN"], pat);}
    | charListOneOrMore {#pat = #(#[CHAR_PATTERN, "CHAR_PATTERN"], pat);}
    | patternVar[contextToken, paraphrase] COLON^ patternVar[contextToken, paraphrase]
    | wildcard
    | recordPattern[contextToken, paraphrase]
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

dataConstructorSingleton[Token contextToken, String paraphrase]
    : qualifiedCons[contextToken, paraphrase]
      {#dataConstructorSingleton = #(#[DATA_CONSTRUCTOR_NAME_SINGLETON, "DATA_CONSTRUCTOR_NAME_SINGLETON"], dataConstructorSingleton);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
dataConstructorNameListOneOrMore[Token contextToken, String paraphrase]
    : openParenNode:OPEN_PAREN! qualifiedCons[contextToken, paraphrase] (BAR! qualifiedCons[contextToken, paraphrase])* CLOSE_PAREN!
      {#dataConstructorNameListOneOrMore = #(#[DATA_CONSTRUCTOR_NAME_LIST, "DATA_CONSTRUCTOR_NAME_LIST"], dataConstructorNameListOneOrMore);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

dataConstructorArgumentBindings[Token contextToken, String paraphrase]
    : patternVarListZeroOrMore[LT(1), "a list of field names"]                                  // positional
    | OPEN_BRACE! fieldBindingVarAssignmentList[LT(1), "a list of comma-separated field bindings"] CLOSE_BRACE!    // matching
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

patternVarListZeroOrMore[Token contextToken, String paraphrase]
    : (patternVar[contextToken, paraphrase])*
      {#patternVarListZeroOrMore = #(#[PATTERN_VAR_LIST, "PATTERN_VAR_LIST"], patternVarListZeroOrMore);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
maybeMinusIntSingleton[Token contextToken, String paraphrase]
    : maybeMinusInt[contextToken, paraphrase]
      {#maybeMinusIntSingleton = #(#[MAYBE_MINUS_INT_LIST, "MAYBE_MINUS_INT_LIST"], maybeMinusIntSingleton);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

maybeMinusIntListOneOrMore[Token contextToken, String paraphrase]
    : openParenNode:OPEN_PAREN! maybeMinusInt[LT(1), "an integer literal"] (BAR! maybeMinusInt[LT(1), "an integer literal"])* CLOSE_PAREN!
      {#maybeMinusIntListOneOrMore = #(#[MAYBE_MINUS_INT_LIST, "MAYBE_MINUS_INT_LIST"], maybeMinusIntListOneOrMore);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

// an integer literal which may have a leading '-'.
maybeMinusInt[Token contextToken, String paraphrase]
    : (node:MINUS^ {#node.initialize (MINUS, "-");} INTEGER_LITERAL)
    | INTEGER_LITERAL
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

charSingleton
    : CHAR_LITERAL
      {#charSingleton = #(#[CHAR_LIST, "CHAR_LIST"], charSingleton);}
    ;

charListOneOrMore
    : openParenNode:OPEN_PAREN! CHAR_LITERAL (BAR! CHAR_LITERAL)* CLOSE_PAREN!
      {#charListOneOrMore = #(#[CHAR_LIST, "CHAR_LIST"], charListOneOrMore);}
    ;

wildcard
    : UNDERSCORE
    ;
    
patternVar[Token contextToken, String paraphrase]
    : var | wildcard
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

recordPattern[Token contextToken, String paraphrase]
    : openBraceNode:OPEN_BRACE^ {#openBraceNode.initialize (RECORD_PATTERN, "RECORD_PATTERN");} baseRecordPattern[LT(1), "a valid base record pattern"] fieldBindingVarAssignmentList[LT(1), "a list of comma-separated field bindings"] closeBraceNode:CLOSE_BRACE! {((ParseTreeNode)#openBraceNode).addOmittedDelimiter(#closeBraceNode);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }
    
baseRecordPattern[Token contextToken, String paraphrase]
    : (patternVar[null, null] BAR) => (patternVar[contextToken, paraphrase] BAR!) 
      {#baseRecordPattern = #(#[BASE_RECORD_PATTERN, "BASE_RECORD_PATTERN"], baseRecordPattern);}
    | //no base record pattern supplied
      {#baseRecordPattern = #(#[BASE_RECORD_PATTERN, "BASE_RECORD_PATTERN"]);}
    ;       
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

fieldBindingVarAssignmentList[Token contextToken, String paraphrase]
    : (fieldBindingVarAssignment[LT(1), "a valid field-binding"] (COMMA! fieldBindingVarAssignment[LT(1), "a valid field-binding"])*)? 
      {#fieldBindingVarAssignmentList = #(#[FIELD_BINDING_VAR_ASSIGNMENT_LIST, "FIELD_BINDING_VAR_ASSIGNMENT_LIST"], fieldBindingVarAssignmentList);}
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

fieldBindingVarAssignment[Token contextToken, String paraphrase]
    : (fieldName[null, null] EQUALS) => fieldName[LT(1), "a valid record-field name"] node:EQUALS^ {#node.initialize (FIELD_BINDING_VAR_ASSIGNMENT, "FIELD_BINDING_VAR_ASSIGNMENT");} patternVar[LT(1), "a valid variable name"]
    |  fieldName[LT(1), "a valid record-field name"]
       {#fieldBindingVarAssignment = #(#[FIELD_BINDING_VAR_ASSIGNMENT, "FIELD_BINDING_VAR_ASSIGNMENT"], fieldBindingVarAssignment);}
    ;                 
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

// Literals
literal[Token contextToken, String paraphrase]
    : INTEGER_LITERAL
    | FLOAT_LITERAL
    | CHAR_LITERAL
    | STRING_LITERAL
    ;
exception
    catch[NoViableAltException ex] { reportWithParaphrase(ex, contextToken, paraphrase, $FIRST, $FOLLOW); }
    catch[RecognitionException ex] { reportError(ex, $FOLLOW); }

literal_index
    : INTEGER_LITERAL
    ;    

// Variables
var
    : VAR_ID
    ;

// Constructors
cons
    : CONS_ID
    ;
    
//////////////////////////////////////////////////////////////////////////////
// CALDoc comments
//////////////////////////////////////////////////////////////////////////////

optionalDocComment
    : comment:docComment!
        ( // intentionally blank alternative
          // that's it... just one comment
          {#optionalDocComment = #(#[OPTIONAL_CALDOC_COMMENT, "OPTIONAL_CALDOC_COMMENT"], comment);}
        | // there's more than one, so every comment except for the last one is unassociated and in error
          {reportUnassociatedCALDocCommentError((ParseTreeNode)#comment);} // report the error first, then recurse afterwards
          moreComments:moreDocComments!
          {#optionalDocComment = #(#[OPTIONAL_CALDOC_COMMENT, "OPTIONAL_CALDOC_COMMENT"], moreComments);}
        )
    | // no comment
        {#optionalDocComment = #(#[OPTIONAL_CALDOC_COMMENT, "OPTIONAL_CALDOC_COMMENT"]);}
    ;

moreDocComments
    : comment:docComment!
        ( // intentionally blank alternative
          // that's it... just one comment
          {#moreDocComments = #comment;}
        | // there's more than one, so every comment except for the last one is unassociated and in error
          {reportUnassociatedCALDocCommentError((ParseTreeNode)#comment);} // report the error first, then recurse afterwards
          moreComments:moreDocComments!
          {#moreDocComments = #moreComments;}
        )
    ;

docComment
    : node:CALDOC_OPEN^ {#node.initialize(CALDOC_COMMENT, "CALDOC_COMMENT");} docCommentDescriptionBlock docCommentTaggedBlocks caldocCloseNode:CALDOC_CLOSE! {((ParseTreeNode)#node).addOmittedDelimiter(#caldocCloseNode);}
    ;

docCommentDescriptionBlock
    : text:docCommentTextualContent!
        {#docCommentDescriptionBlock = #(#[CALDOC_DESCRIPTION_BLOCK, "CALDOC_DESCRIPTION_BLOCK"], text);}
    ;

docCommentTextualContent
    : (docCommentTextSpan | docCommentLineBreak)*
      {#docCommentTextualContent = #(#[CALDOC_TEXT, "CALDOC_TEXT"], docCommentTextualContent);}
    ;

docCommentTextSpan
    : CALDOC_TEXT_LINE
    | CALDOC_BLANK_TEXT_LINE
    | docCommentInlineBlock
    ;
    
docCommentLineBreak
    : node:docCommentNewline {#node.initialize(CALDOC_TEXT_LINE_BREAK, "CALDOC_TEXT_LINE_BREAK");}
    ;
    
docCommentNewline
    : CALDOC_NEWLINE
    | CALDOC_NEWLINE_WITH_LEADING_ASTERISK
    ;

docCommentPreformattedBlock
    : (docCommentTextSpan | docCommentLineBreak)*
      {#docCommentPreformattedBlock = #(#[CALDOC_TEXT_PREFORMATTED_BLOCK, "CALDOC_TEXT_PREFORMATTED_BLOCK"], docCommentPreformattedBlock);}
    ;

docCommentTextBlockWithoutInlineTags[Token surroundingInlineTag]
    : (docCommentContentForTextBlockWithoutInlineTags[surroundingInlineTag])*
      {#docCommentTextBlockWithoutInlineTags = #(#[CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS, "CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS"], docCommentTextBlockWithoutInlineTags);}
    ;

docCommentContentForTextBlockWithoutInlineTags[Token surroundingInlineTag]
    : openInlineTag:CALDOC_OPEN_INLINE_TAG! tag:docCommentInlineBlockContent! CALDOC_CLOSE_INLINE_TAG!
       {reportCALDocInlineTagAppearingInInadmissibleLocationError(openInlineTag, surroundingInlineTag);}
    | (CALDOC_TEXT_LINE | CALDOC_BLANK_TEXT_LINE | docCommentLineBreak)
    ;

docCommentInlineBlock
    : openInlineTag:CALDOC_OPEN_INLINE_TAG! content:docCommentInlineBlockContent closeInlineTag:docCommentCloseInlineTag[openInlineTag]!
      {
      	 if (#openInlineTag == null || #content == null || #closeInlineTag == null) {// we did not match a proper inline block, and we reported a custom error for it, so the subtree for the close tag is null
      	     #docCommentInlineBlock = null;
      	 } else {
             #docCommentInlineBlock = #(#[CALDOC_TEXT_INLINE_BLOCK, "CALDOC_TEXT_INLINE_BLOCK"], docCommentInlineBlock);
             ((ParseTreeNode)#content).addOmittedDelimiter(#openInlineTag);
             ((ParseTreeNode)#content).addOmittedDelimiter(#closeInlineTag);
      	 }
      }
    ;

docCommentCloseInlineTag[Token openInlineTag]
    : CALDOC_CLOSE_INLINE_TAG
    ;
exception
    catch [MismatchedTokenException ex] {
        reportMaybeCALDocInlineBlockNotClosedBeforeEndOfTextBlockError(openInlineTag, ex);
        // no consumption of the invalid token... it may well be the close of the comment.
    }
    catch [RecognitionException ex] {
        reportError(ex);
        consume();
        consumeUntil($FOLLOW);
    }

docCommentInlineBlockContent
    : docCommentInlineBlockContentUrlTagAndContent
    | docCommentInlineBlockContentLinkTagAndContent
    | docCommentInlineBlockContentEmTagAndContent
    | docCommentInlineBlockContentStrongTagAndContent
    | docCommentInlineBlockContentSupTagAndContent
    | docCommentInlineBlockContentSubTagAndContent
    | docCommentInlineBlockContentSummaryTagAndContent
    | docCommentInlineBlockContentCodeTagAndContent
    | docCommentInlineBlockContentOrderedListTagAndContent
    | docCommentInlineBlockContentUnorderedListTagAndContent
    | docCommentInlineBlockContentUnknownTagAndContent
    ;

docCommentInlineBlockContentUnknownTagAndContent
    : unknownTag:CALDOC_INLINE_UNKNOWN_TAG! text:docCommentTextualContent!
        {reportUnrecognizedInlineTagInCALDocCommentError(unknownTag);}
    ;

docCommentInlineBlockContentUrlTagAndContent
    : node:CALDOC_INLINE_URL_TAG^ {#node.initialize(CALDOC_TEXT_URL, "CALDOC_TEXT_URL");} docCommentTextBlockWithoutInlineTags[node]
    ;

docCommentInlineBlockContentLinkTagAndContent
    : node:CALDOC_INLINE_LINK_TAG^ {#node.initialize(CALDOC_TEXT_LINK, "CALDOC_TEXT_LINK");}
      ( docCommentLinkFunction
      | docCommentLinkModule
      | docCommentLinkDataCons
      | docCommentLinkTypeCons
      | docCommentLinkTypeClass
      | ((VAR_ID | CONS_ID) EQUALS) => docCommentLinkUnknownIdentifierLike! // for catching e.g. {@link foobar = lala@}, because foobar would be a VAR_ID
      | docCommentLinkWithoutContext
      | docCommentLinkUnknown!
      )
    ;
exception
    catch [NoViableAltException ex] {
        reportMaybeMissingSeeOrLinkBlockContextInCALDocCommentError(ex);
        consume();
        consumeUntil($FOLLOW);
    }
    catch [RecognitionException ex] {
        reportError(ex);
        consume();
        consumeUntil($FOLLOW);
    }
    
docCommentInlineBlockContentEmTagAndContent
    : node:CALDOC_INLINE_EM_TAG^ {#node.initialize(CALDOC_TEXT_EMPHASIZED_TEXT, "CALDOC_TEXT_EMPHASIZED_TEXT");} docCommentTextualContent
    ;
    
docCommentInlineBlockContentStrongTagAndContent
    : node:CALDOC_INLINE_STRONG_TAG^ {#node.initialize(CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT, "CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT");} docCommentTextualContent
    ;
    
docCommentInlineBlockContentSupTagAndContent
    : node:CALDOC_INLINE_SUP_TAG^ {#node.initialize(CALDOC_TEXT_SUPERSCRIPT_TEXT, "CALDOC_TEXT_SUPERSCRIPT_TEXT");} docCommentTextualContent
    ;
    
docCommentInlineBlockContentSubTagAndContent
    : node:CALDOC_INLINE_SUB_TAG^ {#node.initialize(CALDOC_TEXT_SUBSCRIPT_TEXT, "CALDOC_TEXT_SUBSCRIPT_TEXT");} docCommentTextualContent
    ;
    
docCommentInlineBlockContentSummaryTagAndContent
    : node:CALDOC_INLINE_SUMMARY_TAG^ {#node.initialize(CALDOC_TEXT_SUMMARY, "CALDOC_TEXT_SUMMARY");} docCommentTextualContent
    ;

docCommentInlineBlockContentCodeTagAndContent
    : node:CALDOC_INLINE_CODE_TAG^ {#node.initialize(CALDOC_TEXT_CODE_BLOCK, "CALDOC_TEXT_CODE_BLOCK");} docCommentPreformattedBlock
    ;

docCommentInlineBlockContentOrderedListTagAndContent
    : node:CALDOC_INLINE_ORDERED_LIST_TAG^ {#node.initialize(CALDOC_TEXT_ORDERED_LIST, "CALDOC_TEXT_ORDERED_LIST");} (docCommentListItem)*
    ;

docCommentInlineBlockContentUnorderedListTagAndContent
    : node:CALDOC_INLINE_UNORDERED_LIST_TAG^ {#node.initialize(CALDOC_TEXT_UNORDERED_LIST, "CALDOC_TEXT_UNORDERED_LIST");} (docCommentListItem)*
    ;

docCommentListItem
    : CALDOC_BLANK_TEXT_LINE!
    | docCommentNewline!
    | CALDOC_OPEN_INLINE_TAG! node:CALDOC_INLINE_ITEM_TAG^ {#node.initialize(CALDOC_TEXT_LIST_ITEM, "CALDOC_TEXT_LIST_ITEM");} docCommentTextualContent CALDOC_CLOSE_INLINE_TAG!
    ;

docCommentLinkUnknown!
    : unknownContext:CALDOC_SEE_TAG_UNKNOWN_CONTEXT! docCommentLinkUnknownRemainder!
      {reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError((ParseTreeNode)#unknownContext);}
    ;

docCommentLinkUnknownIdentifierLike
    : unknownContext:docCommentLinkUnknownIdentifierLikeContext! docCommentLinkUnknownRemainder!
      {reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError((ParseTreeNode)#unknownContext);}
    ;

docCommentLinkUnknownIdentifierLikeContext
    : (VAR_ID | CONS_ID) EQUALS
    ;

docCommentLinkUnknownRemainder!
    :
      // the broken "{@link}" block may still look like a valid one, therefore we consume
      // all the various tokens that may appear, except the valid "@" tags, since
      // we can resume parsing upon encountering one of the valid tags.
         ( CALDOC_SEE_TAG_FUNCTION_CONTEXT!
         | CALDOC_SEE_TAG_MODULE_CONTEXT!
         | CALDOC_SEE_TAG_DATACONS_CONTEXT!
         | CALDOC_SEE_TAG_TYPECONS_CONTEXT!
         | CALDOC_SEE_TAG_TYPECLASS_CONTEXT!
         | EQUALS!
         | DOT!
         | COMMA!
         | CALDOC_SEE_TAG_QUOTE!
         | CONS_ID!
         | VAR_ID!
         | CALDOC_TEXT_LINE!
         | CALDOC_BLANK_TEXT_LINE!
         | CALDOC_INLINE_SUMMARY_TAG!
         | CALDOC_INLINE_EM_TAG!
         | CALDOC_INLINE_STRONG_TAG!
         | CALDOC_INLINE_SUP_TAG!
         | CALDOC_INLINE_SUB_TAG!
         | CALDOC_INLINE_UNORDERED_LIST_TAG!
         | CALDOC_INLINE_ORDERED_LIST_TAG!
         | CALDOC_INLINE_ITEM_TAG!
         | CALDOC_INLINE_CODE_TAG!
         | CALDOC_INLINE_URL_TAG!
         | CALDOC_INLINE_LINK_TAG!
         | CALDOC_NEWLINE!
         | CALDOC_NEWLINE_WITH_LEADING_ASTERISK!
         )*
    ;

docCommentLinkFunction
    : CALDOC_SEE_TAG_FUNCTION_CONTEXT! EQUALS! docCommentMaybeUncheckedQualifiedVar
        {#docCommentLinkFunction = #(#[CALDOC_TEXT_LINK_FUNCTION, "CALDOC_TEXT_LINK_FUNCTION"], docCommentLinkFunction);}
    ;

docCommentLinkModule
    : CALDOC_SEE_TAG_MODULE_CONTEXT! EQUALS! docCommentMaybeUncheckedModuleName
        {#docCommentLinkModule = #(#[CALDOC_TEXT_LINK_MODULE, "CALDOC_TEXT_LINK_MODULE"], docCommentLinkModule);}
    ;

docCommentLinkDataCons
    : CALDOC_SEE_TAG_DATACONS_CONTEXT! EQUALS! docCommentMaybeUncheckedQualifiedCons
        {#docCommentLinkDataCons = #(#[CALDOC_TEXT_LINK_DATACONS, "CALDOC_TEXT_LINK_DATACONS"], docCommentLinkDataCons);}
    ;

docCommentLinkTypeCons
    : CALDOC_SEE_TAG_TYPECONS_CONTEXT! EQUALS! docCommentMaybeUncheckedQualifiedCons
        {#docCommentLinkTypeCons = #(#[CALDOC_TEXT_LINK_TYPECONS, "CALDOC_TEXT_LINK_TYPECONS"], docCommentLinkTypeCons);}
    ;

docCommentLinkTypeClass
    : CALDOC_SEE_TAG_TYPECLASS_CONTEXT! EQUALS! docCommentMaybeUncheckedQualifiedCons
        {#docCommentLinkTypeClass = #(#[CALDOC_TEXT_LINK_TYPECLASS, "CALDOC_TEXT_LINK_TYPECLASS"], docCommentLinkTypeClass);}
    ;

docCommentLinkWithoutContext
    : docCommentMaybeUncheckedReferenceWithoutContext
      {#docCommentLinkWithoutContext = #(#[CALDOC_TEXT_LINK_WITHOUT_CONTEXT, "CALDOC_TEXT_LINK_WITHOUT_CONTEXT"], docCommentLinkWithoutContext);}
    ;

docCommentMaybeUncheckedReferenceWithoutContext
    : ( (docCommentMaybeUncheckedQualifiedVar) => docCommentMaybeUncheckedQualifiedVar
      | docCommentMaybeUncheckedQualifiedConsWithParaphrase["a qualified variable name or a qualified constructor name"]
      )
    ;

docCommentTaggedBlocks
    :
        ( docCommentAuthorBlock
        | docCommentDeprecatedBlock
        | docCommentReturnBlock
        | docCommentVersionBlock
        | docCommentArgBlock
        | docCommentSeeBlock
        | docCommentUnknownTagBlock!
        )*
        {#docCommentTaggedBlocks = #(#[CALDOC_TAGGED_BLOCKS, "CALDOC_TAGGED_BLOCKS"], docCommentTaggedBlocks);}
    ;
    
docCommentUnknownTagBlock!
    : unknownTag:CALDOC_UNKNOWN_TAG! text:docCommentTextualContent!
        {reportUnrecognizedTagInCALDocCommentError(unknownTag);}
    ;

docCommentAuthorBlock
    : node:CALDOC_AUTHOR_TAG^ {#node.initialize(CALDOC_AUTHOR_BLOCK, "CALDOC_AUTHOR_BLOCK");} docCommentTextualContent
    ;
    
docCommentDeprecatedBlock
    : node:CALDOC_DEPRECATED_TAG^ {#node.initialize(CALDOC_DEPRECATED_BLOCK, "CALDOC_DEPRECATED_BLOCK");} docCommentTextualContent
    ;
    
docCommentReturnBlock
    : node:CALDOC_RETURN_TAG^ {#node.initialize(CALDOC_RETURN_BLOCK, "CALDOC_RETURN_BLOCK");} docCommentTextualContent
    ;

docCommentVersionBlock
    : node:CALDOC_VERSION_TAG^ {#node.initialize(CALDOC_VERSION_BLOCK, "CALDOC_VERSION_BLOCK");} docCommentTextualContent
    ;

docCommentArgBlock
    : node:CALDOC_ARG_TAG^ {#node.initialize(CALDOC_ARG_BLOCK, "CALDOC_ARG_BLOCK");} fieldName[LT(1), "a valid argument name"] docCommentTextualContent
    ;

docCommentSeeBlock
    : seeTag:CALDOC_SEE_TAG!
        ( docCommentSeeFunctionBlock
        | docCommentSeeModuleBlock
        | docCommentSeeDataConsBlock
        | docCommentSeeTypeConsBlock
        | docCommentSeeTypeClassBlock
        | ((VAR_ID | CONS_ID) EQUALS) => docCommentSeeUnknownBlockIdentifierLike! // for catching e.g. @see foobar = lala, because foobar would be a VAR_ID
        | docCommentSeeBlockWithoutContext
        | docCommentSeeUnknownBlock!
        )
        {
            #docCommentSeeBlock = #(#[CALDOC_SEE_BLOCK, "CALDOC_SEE_BLOCK"], docCommentSeeBlock);
            ((ParseTreeNode)#docCommentSeeBlock).addOmittedDelimiter(#seeTag);
           }
    ;
exception
    catch [NoViableAltException ex] {
        reportMaybeMissingSeeOrLinkBlockContextInCALDocCommentError(ex);
        consume();
        consumeUntil($FOLLOW);
    }
    catch [RecognitionException ex] {
        reportError(ex);
        consume();
        consumeUntil($FOLLOW);
    }

docCommentSeeUnknownBlock!
    : unknownContext:CALDOC_SEE_TAG_UNKNOWN_CONTEXT! docCommentSeeUnknownBlockRemainder!
      {reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError((ParseTreeNode)#unknownContext);}
    ;

docCommentSeeUnknownBlockIdentifierLike
    : unknownContext:docCommentSeeUnknownBlockIdentifierLikeContext! docCommentSeeUnknownBlockRemainder!
      {reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError((ParseTreeNode)#unknownContext);}
    ;

docCommentSeeUnknownBlockIdentifierLikeContext
    : (VAR_ID | CONS_ID) EQUALS
    ;

docCommentSeeUnknownBlockRemainder!
    :
      // the broken "@see" block may still look like a valid one, therefore we consume
      // all the various tokens that may appear, except the valid "@" tags, since
      // we can resume parsing upon encountering one of the valid tags.
         ( CALDOC_SEE_TAG_FUNCTION_CONTEXT!
         | CALDOC_SEE_TAG_MODULE_CONTEXT!
         | CALDOC_SEE_TAG_DATACONS_CONTEXT!
         | CALDOC_SEE_TAG_TYPECONS_CONTEXT!
         | CALDOC_SEE_TAG_TYPECLASS_CONTEXT!
         | EQUALS!
         | DOT!
         | COMMA!
         | CALDOC_SEE_TAG_QUOTE!
         | CONS_ID!
         | VAR_ID!
         | CALDOC_TEXT_LINE!
         | CALDOC_BLANK_TEXT_LINE!
         | CALDOC_OPEN_INLINE_TAG!
         | CALDOC_CLOSE_INLINE_TAG!
         | CALDOC_INLINE_SUMMARY_TAG!
         | CALDOC_INLINE_EM_TAG!
         | CALDOC_INLINE_STRONG_TAG!
         | CALDOC_INLINE_SUP_TAG!
         | CALDOC_INLINE_SUB_TAG!
         | CALDOC_INLINE_UNORDERED_LIST_TAG!
         | CALDOC_INLINE_ORDERED_LIST_TAG!
         | CALDOC_INLINE_ITEM_TAG!
         | CALDOC_INLINE_CODE_TAG!
         | CALDOC_INLINE_URL_TAG!
         | CALDOC_INLINE_LINK_TAG!
         | CALDOC_INLINE_UNKNOWN_TAG!
         | CALDOC_NEWLINE!
         | CALDOC_NEWLINE_WITH_LEADING_ASTERISK!
         )*
    ;

docCommentSeeFunctionBlock
    : CALDOC_SEE_TAG_FUNCTION_CONTEXT! EQUALS! docCommentMaybeUncheckedQualifiedVar (COMMA! docCommentMaybeUncheckedQualifiedVar)*
        {#docCommentSeeFunctionBlock = #(#[CALDOC_SEE_FUNCTION_BLOCK, "CALDOC_SEE_FUNCTION_BLOCK"], docCommentSeeFunctionBlock);}
    ;

docCommentSeeModuleBlock
    : CALDOC_SEE_TAG_MODULE_CONTEXT! EQUALS! docCommentMaybeUncheckedModuleName (COMMA! docCommentMaybeUncheckedModuleName)*
        {#docCommentSeeModuleBlock = #(#[CALDOC_SEE_MODULE_BLOCK, "CALDOC_SEE_MODULE_BLOCK"], docCommentSeeModuleBlock);}
    ;

docCommentSeeDataConsBlock
    : CALDOC_SEE_TAG_DATACONS_CONTEXT! EQUALS! docCommentMaybeUncheckedQualifiedCons (COMMA! docCommentMaybeUncheckedQualifiedCons)*
        {#docCommentSeeDataConsBlock = #(#[CALDOC_SEE_DATACONS_BLOCK, "CALDOC_SEE_DATACONS_BLOCK"], docCommentSeeDataConsBlock);}
    ;

docCommentSeeTypeConsBlock
    : CALDOC_SEE_TAG_TYPECONS_CONTEXT! EQUALS! docCommentMaybeUncheckedQualifiedCons (COMMA! docCommentMaybeUncheckedQualifiedCons)*
        {#docCommentSeeTypeConsBlock = #(#[CALDOC_SEE_TYPECONS_BLOCK, "CALDOC_SEE_TYPECONS_BLOCK"], docCommentSeeTypeConsBlock);}
    ;

docCommentSeeTypeClassBlock
    : CALDOC_SEE_TAG_TYPECLASS_CONTEXT! EQUALS! docCommentMaybeUncheckedQualifiedCons (COMMA! docCommentMaybeUncheckedQualifiedCons)*
        {#docCommentSeeTypeClassBlock = #(#[CALDOC_SEE_TYPECLASS_BLOCK, "CALDOC_SEE_TYPECLASS_BLOCK"], docCommentSeeTypeClassBlock);}
    ;

docCommentSeeBlockWithoutContext
    : docCommentMaybeUncheckedReferenceWithoutContext (COMMA! docCommentMaybeUncheckedReferenceWithoutContext)*
        {#docCommentSeeBlockWithoutContext = #(#[CALDOC_SEE_BLOCK_WITHOUT_CONTEXT, "CALDOC_SEE_BLOCK_WITHOUT_CONTEXT"], docCommentSeeBlockWithoutContext);}
    ;

docCommentMaybeUncheckedModuleName
    : moduleName[LT(1), "a valid module name"]
        {#docCommentMaybeUncheckedModuleName = #(#[CALDOC_CHECKED_MODULE_NAME, "CALDOC_CHECKED_MODULE_NAME"], docCommentMaybeUncheckedModuleName);}
    | CALDOC_SEE_TAG_QUOTE! moduleName[LT(1), "a valid module name"] CALDOC_SEE_TAG_QUOTE!
        {#docCommentMaybeUncheckedModuleName = #(#[CALDOC_UNCHECKED_MODULE_NAME, "CALDOC_UNCHECKED_MODULE_NAME"], docCommentMaybeUncheckedModuleName);}
    ;
exception
    catch [NoViableAltException ex] {
        reportMaybeUnrecognizedSeeOrLinkBlockReferenceInCALDocCommentError(ex);
        consume();
        consumeUntil($FOLLOW);
    }
    catch [RecognitionException ex] {
        reportError(ex);
        consume();
        consumeUntil($FOLLOW);
    }

docCommentMaybeUncheckedQualifiedVar
    : qualifiedVar[LT(1), "a qualified variable name"]
        {#docCommentMaybeUncheckedQualifiedVar = #(#[CALDOC_CHECKED_QUALIFIED_VAR, "CALDOC_CHECKED_QUALIFIED_VAR"], docCommentMaybeUncheckedQualifiedVar);}
    | node:CALDOC_SEE_TAG_QUOTE^ {#node.initialize(CALDOC_UNCHECKED_QUALIFIED_VAR, "CALDOC_UNCHECKED_QUALIFIED_VAR");}
      qualifiedVar[LT(1), "a qualified variable name"]
      CALDOC_SEE_TAG_QUOTE!
    ;
exception
    catch [NoViableAltException ex] {
        reportMaybeUnrecognizedSeeOrLinkBlockReferenceInCALDocCommentError(ex);
        consume();
        consumeUntil($FOLLOW);
    }
    catch [RecognitionException ex] {
        reportError(ex);
        consume();
        consumeUntil($FOLLOW);
    }

docCommentMaybeUncheckedQualifiedCons
    : docCommentMaybeUncheckedQualifiedConsWithParaphrase["a qualified constructor name"]
    ;

docCommentMaybeUncheckedQualifiedConsWithParaphrase[String paraphrase]
    : qualifiedCons[LT(1), paraphrase]
        {#docCommentMaybeUncheckedQualifiedConsWithParaphrase = #(#[CALDOC_CHECKED_QUALIFIED_CONS, "CALDOC_CHECKED_QUALIFIED_CONS"], docCommentMaybeUncheckedQualifiedConsWithParaphrase);}
    | node:CALDOC_SEE_TAG_QUOTE^ {#node.initialize(CALDOC_UNCHECKED_QUALIFIED_CONS, "CALDOC_UNCHECKED_QUALIFIED_CONS");}
      qualifiedCons[LT(1), paraphrase]
      CALDOC_SEE_TAG_QUOTE!
    ;
exception
    catch [NoViableAltException ex] {
        reportMaybeUnrecognizedSeeOrLinkBlockReferenceInCALDocCommentError(ex);
        consume();
        consumeUntil($FOLLOW);
    }
    catch [RecognitionException ex] {
        reportError(ex);
        consume();
        consumeUntil($FOLLOW);
    }

/*************************************** 
* CAL lexical definition starts here *   
***************************************/

class CALLexer extends Lexer;

options {  
    charVocabulary='\u0000'..'\uFFFE';   // Allow any char but \uFFFF (16 bit -1)
    testLiterals=false;                  // Automatically test for literals?  Override in rules.
    k=4;                                 // Number of lookahead characters
    defaultErrorHandler = true;          // Generate lexer error handlers

    //Suppress warnings for the generated class.  Need to use unicode escapes since antlr copies backslashes into the source.
    //instead of "public", which is the default, make CALTreeParser package scope.        
    classHeaderPrefix = "@SuppressWarnings(\u0022all\u0022) final"; 
}

//Imaginary Tokens
//tokens {
//  FLOAT_LITERAL;  
//  DOT;
//}


// Preamble
{
    // Add declarations for CALLexer class here

    /** The multiplexed lexer that owns this instance. */
    private CALMultiplexedLexer parentMultiLexer = null;
    
        
    /**
     * This instance of the CALLexer is only used for its isKeyword method. Attempts
     * to use it for actually lexing CAL source may result in NullPointerExceptions,
     * as it does not have a proper input stream nor a reference to a CALMultiplexedLexer.
     */
    private static final CALLexer keywordsLexer = new CALLexer ((LexerSharedInputState)null);
    
    
    /**
     * Construct CALLexer from a CALCompiler    
     * @param parent
     * @param in
     */
    public CALLexer(CALMultiplexedLexer parent, Reader in) {
        this (in);
        if (parent == null) {
            throw new NullPointerException();
        }
        parentMultiLexer = parent;
        
        //tab stops in our source CAL files as well as in the code gem are set to 4
        //this affects column information for error messages, as well as syntax
        //highlighting in the code gem.
        setTabSize(CALMultiplexedLexer.TAB_SIZE);

        // Use our custom token class.
        String tokenClassName = CALToken.class.getName();
        setTokenObjectClass(tokenClassName);
    }

    /**
     * Override reportError method to direct standard error handling through to the CALCompiler error scheme
     * @param ex RecognitionException the recognition exception that originated the problem
     */
    public void reportError (RecognitionException ex) {
        parentMultiLexer.reportError(ex);
    } 
    
    /**  
     * Method used to support unit testing of LanguageInfo.isKeyword. We don't want
     * LanguageInfo.isKeyword to require loading of the ANTLR jar, but we also don't want
     * it to get out of date, so this method supports the requisite JUnit test.
     *         
     * @param identifier the identifier to test 
     * @return true if the identifier is a CAL keyword such as "data".
     */
    static boolean isKeyword(final String identifier) {
        return keywordsLexer.literals.containsKey(new ANTLRHashString(identifier, keywordsLexer));        
    }
    
    /** 
     * Method used to support unit testing of LanguageInfo.isKeyword. We don't want
     * LanguageInfo.isKeyword to require loading of the ANTLR jar, but we also don't want
     * it to get out of date, so this method supports the requisite JUnit test.  
     *   
     * @return number of CAL keywords.
     */
    static int getNKeywords() {
        return keywordsLexer.literals.size();
    }
               
    /*
     *  (non-Javadoc)
     * @see antlr.CharScanner#makeToken(int)
     */
    protected Token makeToken(int t) {
        // Override to set the filename as well.
        Token token = super.makeToken(t);
        token.setFilename(getFilename());
        return token;
    }
    
    /**
     * Copied from antlr.Parser.recover()
     *
     * As of antlr 2.7.6, lexers generated with the default error handler contain calls to this method.
     * However, this method isn't generated by default or implemented by any superclasses, 
     * causing a compile error in the generated Java code.
     * 
     * TODOEL: Remove this method when feasible.  
     *   Also remove corresponding method from CALDocLexer.
     */
    public void recover(RecognitionException ex,
                         BitSet tokenSet) throws CharStreamException, TokenStreamException {
        consume();
        consumeUntil(tokenSet);
    }

}
// End Preamble

    
// Whitespace -- ignored
WS
options {
  paraphrase = "whitespace";
}
  :  (  ' '
       |  '\t'
       |  '\f'
       // handle newlines
       |  (  
            /*  '\r' '\n' can be matched in one alternative or by matching
            '\r' in one iteration and '\n' in another.  I am trying to
            handle any flavor of newline that comes in, but the language
            that allows both "\r\n" and "\r" and "\n" to all be valid
            newline is ambiguous.  Consequently, the resulting grammar
            must be ambiguous.  I'm shutting this warning off.
            */
            options { generateAmbigWarnings=false; } :
             "\r\n"  // Evil DOS
             |  '\r'    // Macintosh
             |  '\n'    // Unix (the right way)
          )
          { newline(); }
     ) +
  ;
          



// Single-line comments
SL_COMMENT
options {
  paraphrase = "a comment";
}
    :  "//" 
       ( ~( '\n' | '\r' ) )*
    ;

// Multiple-line comments
ML_COMMENT
options {
  paraphrase = "a comment";
}
    : "/*" ( ~('*'|'\r'|'\n') | ((options {generateAmbigWarnings=false;} : "\r\n"|'\r'|'\n') {newline();}) )
      // "/**" is considered the start of a documentation comment, which is parsed in a separate rule.
    (  /*  '\r' '\n' can be matched in one alternative or by matching
        '\r' in one iteration and '\n' in another.  I am trying to
        handle any flavor of newline that comes in, but the language
        that allows both "\r\n" and "\r" and "\n" to all be valid
        newline is ambiguous.  Consequently, the resulting grammar
        must be ambiguous.  I'm shutting this warning off.
       */
      options {
        generateAmbigWarnings=false;
      }
    :
      { LA(2)!='/' }? '*'
    |  '\r' '\n'    {newline();}
    |  '\r'         {newline();}
    |  '\n'         {newline();}
    |  ~( '*' | '\n' | '\r' )
    )*
    "*/"
  ;

// CALdoc comments
CALDOC_OPEN
options {
  paraphrase = "the start of a CALDoc comment '/**'";
}
    : "/**/" {$setType(Token.SKIP);} // "/**/" is not a CALDoc comment, but just a regular ML_COMMENT.
    | "/*" ({ LA(2)!='/' }? '*')+ {parentMultiLexer.switchToCALDocLexer();}
    ;

// Separators 
//note the use of the paraphrase option. This is so that in error messages generated by antlr, the token text expected
//is given as e.g. '(' rather than OPEN_PAREN.
OPEN_PAREN options {paraphrase = "'('";} : '(';
CLOSE_PAREN options {paraphrase = "')'";} : ')';    
OPEN_BRACE options {paraphrase = "'{'";} : '{';
CLOSE_BRACE options {paraphrase = "'}'";} : '}';
OPEN_BRACKET options {paraphrase = "'['";} : '[';
CLOSE_BRACKET options {paraphrase = "']'";} :']';
SEMICOLON options {paraphrase = "';'";} : ';';
COMMA options {paraphrase = "','";} : ',';
BACKQUOTE options {paraphrase = "'`'";} : '`';
//DOT : '.';  // Generated from aborted lex of INTEGER_LITERAL
UNDERSCORE options {paraphrase = "'_'";} : '_';
//QUOTE : '\''; //not a CAL token
IMPLIES options {paraphrase = "'=>'";} : "=>";
COLONCOLON options {paraphrase = "'::'";} : "::";
RARROW options {paraphrase = "'->'";} : "->";

// Operators
GREATER_THAN options {paraphrase = "'>'";} : '>';
LESS_THAN options {paraphrase = "'<'";} : '<';
//TILDE : '~'; //not a CAL token
//QUESTION : '?'; //not a CAL token
COLON options {paraphrase = "':'";} : ':';
EQUALS options {paraphrase = "'='";} : '=';
EQUALSEQUALS options {paraphrase = "'=='";} : "==";
LESS_THAN_OR_EQUALS options {paraphrase = "'<='";} : "<=";
GREATER_THAN_OR_EQUALS options {paraphrase = "'>='";} : ">=";
NOT_EQUALS options {paraphrase = "'!='";} : "!=";
PLUS options {paraphrase = "'+'";} : '+';
MINUS options {paraphrase = "'-'";} : '-';
ASTERISK options {paraphrase = "'*'";} : '*';
//COMMERCIAL_AT : '@'; //not a CAL token
SOLIDUS options {paraphrase = "'/'";} : '/';
//CARET : '^'; //not a CAL token
PERCENT options {paraphrase = "'%'";} : '%';
PLING options {paraphrase = "'!'";} : '!';
BACKSLASH options {paraphrase = "'\'";} : '\\';
//AMPERSAND : '&'; //not a CAL token
BAR options {paraphrase = "'|'";} : '|';
AMPERSANDAMPERSAND options {paraphrase = "'&&'";} : "&&";
BARBAR options {paraphrase = "'||'";} : "||";
PLUSPLUS options {paraphrase = "'++'";} : "++";
DOLLAR options {paraphrase = "'$'";} : "$";
POUND options {paraphrase = "'#'";} : "#";
COLONEQUALS options {paraphrase = "':='";} : ":=";


// Literals

// character literals
CHAR_LITERAL
options {
  paraphrase = "a character literal";
}
    :    '\'' ( ESC | ~'\'' ) '\''
    ;

// string literals
STRING_LITERAL
options {
  paraphrase = "a string literal";
} 
    :    '"' (ESC|~('"'|'\\'))* '"'
    ;

// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC
    :    '\\'
        (    'n'
        |    'r'
        |    't'
        |    'b'
        |    'f'
        |    '"'
        |    '\''
        |    '\\'
        |    ('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT 
        |    ('0'..'3')
            (
                options {
                    warnWhenFollowAmbig = false;
                }
            :    ('0'..'7')
                (    
                    options {
                        warnWhenFollowAmbig = false;
                    }
                :    '0'..'7'
                )?
            )?
        |    ('4'..'7')
            (
                options {
                    warnWhenFollowAmbig = false;
                }
            :    ('0'..'7')
            )?
        )
    ;

// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
    :    ('0'..'9'|'A'..'F'|'a'..'f')
    ;

// Number
// a numeric literal
INTEGER_LITERAL
options {
  paraphrase = "an integer value";
}    
    :    '.' {_ttype = DOT;}
            (('0'..'9')+ (EXPONENT)? { _ttype = FLOAT_LITERAL; })?
    |    (    '0'  // special case for just '0'            
        |    ('1'..'9') ('0'..'9')*  // non-zero decimal
        )
        (                    
            (    '.' ('0'..'9')* (EXPONENT)? 
            |    EXPONENT            
            )
            { _ttype = FLOAT_LITERAL; }
        )?
    ;

// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
    :    ('e'|'E') ('+'|'-')? ('0'..'9')+
    ;


CONS_ID
options {
    testLiterals = true;
    paraphrase = "an identifier starting with a capital letter";  // Humanised name for errors
}
    :   ('A'..'Z') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
    ;

VAR_ID
options {
    testLiterals = true;
    paraphrase = "an identifier starting with a lowercase letter";  // Humanised name for errors
}
    :   ('a'..'z') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*    
    ;
    
ORDINAL_FIELD_NAME
    : ('#') ('1'..'9') ('0'..'9')*
    ;
