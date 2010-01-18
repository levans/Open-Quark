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
 * CALDoc.g
 * Creation date: July 14, 2005.
 * By: Joseph Wong
 */
 
/*
 * CALDoc.g
 *
 * Crystal Analytical Language (CAL) 
 *
 * The textual form of the grammar of documentation comments in the CAL language.
 *
 * $Header:  $
 *
 * Change History:
 * JWO 14-July-05   Initial Version.
 * JWO 18-Aug-05    Improved error reporting for malformed CALDoc comments.
 * JWO 25-Aug-05    Improved accuracy of source positions for CALDoc tags.
 * JWO 24-Oct-05    Added grammar for inline formatting tags in CALDoc.
 * JWO 1-Dec-05     Added CALDoc syntax for @see/{@link} without context, {@strong}, {@sup}, {@sub}.
 * JWO 12-Dec-06    Removed the CALDocLexer constructor that takes an InputStream parameter.
 *
 * $Log:  $
 *
 *
 * General Improvements to be done:
 * ----------------------------------------------------------------
 */

// This antlr grammar specifies the format of documentation comments in the CAL language, named CALDoc.
//
// The text of a CALDoc comment consists of the characters between the "/**" that begins the comment and
// the "*/" that closes it. The text is divided into one or more lines. On each of these lines, any leading
// whitespace and subsequent '*' characters are ignored. For example, the comment:
//
// |
// |<- assume this is the first column of a line
// |
// 
// /**foo bar baz
//   ****    This is a silly comment.
//   1234*/
//
// consists of three lines. The first line consists of the text "foo bar baz", the second line consists of the text
// "    This is a silly comment." (note the preservation of the whitespace characters after the leading '*' characters),
// and the third line consists of the text "  1234" (note again the preservation of whitespace in a line that has no leading '*'
// characters).
//
// The lexer is responsible for ignoring the leading whitespace and '*' characters. The lexeme that is returned for
// a text line starts at the first character after the final leading '*' character. Note that this would mean that
// the lexeme would likely start with a whitespace character, namely the whitespace separating the '*' and the first word
// on the line.
//
// A CALDoc comment begins with a general description section, consisting of lines of text. The description can then be
// followed by zero or more tagged blocks.
//
// The lines of text within a CALDoc comment can contain "inline blocks". Currently, CALDoc supports the following
// inline blocks:
//
// {@code x + y@}
// {@em This is emphasized text.@}
// {@url http://www.businessobjects.com@}
// {@link function = map@}
// {@summary This is a part of the comment's summary.@}
// {@unorderedList {@item one item@} {@item two items@}@}
// {@orderedList {@item one item@} {@item two items@}@}
//
// With the {@link} inline block, its format follows that of the @see block (see below), with the restriction that
// only one reference can be cited per {@link} block.
//
// In a {@code} block, the whitespace contained within it is respected and preserved in the formatted output.
//
// At any time, the '@' character itself can appear within the text of a CALDoc comment, if escaped by
// preceding it with a '\', e.g. '\@'. However, there are only certain occasions where escaping '@' is mandatory:
// - when preceded by the end of an inline block:
//     {@code a + 3@} \@foo
// - when at the start of a comment line (not counting leading whitespace):
//   /** \@bar */
//
//   or:
//   /**
//    * \@bar
//    */
// - when at the start of the textual portion of a tagged block:
//   /**
//    * @arg someArg \@baz
//    */
//
// In other circumstances, the escaping of '@' is optional.
//
// A blank CALDoc line signifies a paragraph break.
//
// A CALDoc line that begins with the '@' character followed by one of a few special keywords starts a tagged block. The tagged
// block extends up to, but not including, either the first line of the next tagged block or the end of the CALDoc coment.
//
// Currently, CALDoc supports the following tagged block formats:
// (In the following, {text} indicates general lines of text that can span one or more lines.)
//
// @author {text}
// @deprecated {text}
// @return {text}
// @version {text}
// @arg {argument name} {text}
// @see function = {function reference} (, {function reference})*
// @see module = {module reference} (, {module reference})*
// @see dataConstructor = {data constructor reference} (, {data constructor reference})*
// @see typeConstructor = {type constructor reference} (, {type constructor reference})*
// @see typeClass = {type class reference} (, {type class reference})*
// @see {reference} (, {reference})*
//
// In each of the above @see block variants, a reference can either be just a simple (qualified or unqualified) name (e.g. Eq, Prelude.map), or
// one that is surrounded by double quotes (e.g. "makeFileName", "Debug.Show"). Double-quoted names are not checked during the compilation
// process.
//
// The last @see block variant is a short syntax that omits the 'context' keyword. In this variant, references of different
// kinds (function and class method names, module names, type constructor names, data constructor names, and type class names)
// can appear in the same block.
//
// Also, while whitespace characters are significant in a text line, they are ignored in @see blocks. The ramification this has
// on the lexical grammar is that once the lexer detects the @see tag, it switches to a mode where whitespace characters are ignored.
// The lexer switches back to its whitespace-significant mode when the @see block terminates (e.g. by the start of another
// tagged block). The lexer accomplishes this by keeping a lexer state, which can be one of five enumerated values as defined in the
// LexerState class.
//
// For example, given the comment:
//
// /**
//  * Some comment.
//  * @arg someArg some arg
//  *   @see function=compose, Prelude.map
//  *   @see typeClass =
//  *          Ord, "Debug.Show"
//  * @return some value...
//  */
//
// the lexemes returned by the lexer are: (and the lexer's state is indicated in [])
//
// [state = LexerState.REGULAR]
// "" (the first line, which is blank after the initial "/**")
// "\n" (the newline character after the first line, which is significant)
// " Some comment."
// "\n"
// "@arg"
// [state = LexerState.ARG_NAME]
// "someArg"
// [state = LexerState.REGULAR]
// " some arg"
// "\n"
// "@see"
// [state = LexerState.SEE_TAG_CONTEXT]
// "function"
// "="
// [state = LexerState.SEE_TAG_LIST]
// "compose"
// ","
// "Prelude"
// "."
// "map"
// "@see"
// [state = LexerState.SEE_TAG_CONTEXT]
// "typeClass"
// "="
// [state = LexerState.SEE_TAG_LIST]
// "Ord"
// ","
// "\""
// "Debug"
// "."
// "Show"
// "\""
// "@return"
// [state = LexerState.REGULAR] (this means whitespace is now significant again)
// " some value..."
// "\n"
// " "
//
// Note that we generate a lexeme for the space before "*/". This is in keeping with the specification, which dictates that
// this last line is one without a leading '*', since the only '*' on the line belongs to "*/".
//

header {
// Package declaration
package org.openquark.cal.compiler;
}

/* ************************************* * 
 * CALDoc lexical definition starts here *   
 * ************************************* */

class CALDocLexer extends Lexer;

options {  
    charVocabulary='\u0000'..'\uFFFE';   // Allow any char but \uFFFF (16 bit -1)
    testLiterals=false;                  // Automatically test for literals?  Override in rules.
    k=6;                                 // Number of lookahead characters
    defaultErrorHandler = true;          // Generate lexer error handlers
    importVocab = CALCommon;
    exportVocab = CALDoc;

    //Suppress warnings for the generated class.  Need to use unicode escapes since antlr copies backslashes into the source.
    //instead of "public", which is the default, make CALTreeParser package scope.        
    classHeaderPrefix = "@SuppressWarnings(\u0022all\u0022) final"; 
}


// Preamble
{
    // Add declarations for CALDocLexer class here

    /** The multiplexed lexer that owns this instance. */
    private CALMultiplexedLexer parentMultiLexer = null;
    
    /** Typesafe enum for the lexer's state. */
    private static class LexerState {
        private LexerState() {}
        static final LexerState REGULAR = new LexerState();
        static final LexerState ARG_NAME = new LexerState();
        static final LexerState SEE_TAG_CONTEXT = new LexerState();
        static final LexerState SEE_TAG_LIST = new LexerState();
        static final LexerState INLINE_TAG = new LexerState();
    }
    
    /** The current state of the lexer. */
    private LexerState state = LexerState.REGULAR;
    
    /**
     * Construct a CALDocLexer from an Reader.
     * @param parent multiplexed lexer that owns this instance.
     * @param in
     */
    public CALDocLexer(CALMultiplexedLexer parent, Reader in) {
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
     * Construct a CALDocLexer from a LexerSharedInputState.
     * @param parent multiplexed lexer that owns this instance.
     * @param in
     */
    public CALDocLexer(CALMultiplexedLexer parent, LexerSharedInputState in) {
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
    
    /** Each time the parser switches to use the CALDoc lexer, its state must be reset. */
    void resetState() {
        state = LexerState.REGULAR;
    }
    
    /** @return whether the supplied string consists only of CALDoc whitespace characters. */
    static boolean isCALDocWhitespaceString(String text) {
        for (int i = 0, n = text.length(); i < n; i++) {
            switch (text.charAt(i)) {
                case ' ':
                case '\t':
                case '\f':
                    continue;
                default:
                    return false;
            }
        }
        return true;
    }

    /** Trims the given string of its leading CALDoc whitespace characters. */    
    static String trimLeadingCALDocWhitespace(String text) {
        for (int i = 0, n = text.length(); i < n; i++) {
            switch (text.charAt(i)) {
                case ' ':
                case '\t':
                case '\f':
                    continue;
                default:
                    return text.substring(i);
            }
        }
        return "";
    }

    /**
     * Override reportError method to direct standard error handling through to the CALCompiler error scheme
     * @param ex RecognitionException the recognition exception that originated the problem
     */
    public void reportError (RecognitionException ex) {
        parentMultiLexer.reportError(ex);
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
     *   Also remove corresponding method from CALLexer.
     */
    public void recover(RecognitionException ex,
                         BitSet tokenSet) throws CharStreamException, TokenStreamException {
        consume();
        consumeUntil(tokenSet);
    }

}
// End Preamble

protected
CALDOC_CLOSE
options {
    paraphrase = "the end of a CALDoc comment '*/'";
}
    : "*/" {parentMultiLexer.switchOutOfCALDocLexer(); resetState();}
    ;

protected
CALDOC_NEWLINE
    : (options {generateAmbigWarnings=false;} : "\r\n"|'\r'|'\n') {newline();}
    ;

protected
CALDOC_WS
    : ' '
    | '\t'
    | '\f'
    ;

// The optional leading whitespace and '*' characters are grouped and lexed with the newline.
// If the leading whitespace and '*' characters (which are to be ignored) were to be 
// recognized by a separate lexer rule, it would have caused an ambiguity in the grammar because
// a CALDOC_TEXT_LINE can also start with any number of whitespace characters, and the lexer only
// has finite lookahead.
protected
CALDOC_NEWLINE_WITH_LEADING_ASTERISK
    : CALDOC_NEWLINE (CALDOC_WS!)* ({LA(2) != '/'}? '*'!)+
    ;

protected
CALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK_SPEC
    : (CALDOC_NEWLINE (CALDOC_WS)* ({LA(2) != '/'}? '*')) => CALDOC_NEWLINE_WITH_LEADING_ASTERISK
    | CALDOC_NEWLINE
    ;

CALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK
    : {this.state == LexerState.REGULAR}?
        ( (CALDOC_NEWLINE (CALDOC_WS)* ({LA(2) != '/'}? '*')) => CALDOC_NEWLINE_WITH_LEADING_ASTERISK
            {$setType(CALDOC_NEWLINE_WITH_LEADING_ASTERISK);}
        | CALDOC_NEWLINE
            {$setType(CALDOC_NEWLINE);}
        )
    ;

protected
CONS_ID
options {
    testLiterals = true;
    paraphrase = "an identifier starting with a capital letter";  // Humanised name for errors
}
    :   ('A'..'Z') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*
    ;

protected
VAR_ID
options {
    testLiterals = true;
    paraphrase = "an identifier starting with a lowercase letter";  // Humanised name for errors
}
    :   ('a'..'z') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*    
    ;

protected
ORDINAL_FIELD_NAME
	: ('#') ('1'..'9') ('0'..'9')*
	;

CALDOC_IGNORED_DOC_WS
options {
    paraphrase = "whitespace";
}
    : {this.state != LexerState.REGULAR}? (CALDOC_WS | CALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK_SPEC)+ {$setType(Token.SKIP);}
    ;

protected
CALDOC_SEE_TAG_FUNCTION_CONTEXT
    : {this.state == LexerState.SEE_TAG_CONTEXT}? "function"
    ;

protected
CALDOC_SEE_TAG_MODULE_CONTEXT
    : {this.state == LexerState.SEE_TAG_CONTEXT}? "module"
    ;

protected
CALDOC_SEE_TAG_DATACONS_CONTEXT
    : {this.state == LexerState.SEE_TAG_CONTEXT}? "dataConstructor"
    ;

protected
CALDOC_SEE_TAG_TYPECONS_CONTEXT
    : {this.state == LexerState.SEE_TAG_CONTEXT}? "typeConstructor"
    ;

protected
CALDOC_SEE_TAG_TYPECLASS_CONTEXT
    : {this.state == LexerState.SEE_TAG_CONTEXT}? "typeClass"
    ;

CALDOC_SEE_TAG_CONTEXT
    : {this.state == LexerState.SEE_TAG_CONTEXT}? 
    ( (CALDOC_SEE_TAG_FUNCTION_CONTEXT  ('=' /*CALDOC_SEE_TAG_EQUALS*/|' '|'\t'|'\f'|'\n'|'\r'|'*')) =>
       CALDOC_SEE_TAG_FUNCTION_CONTEXT  {$setType(CALDOC_SEE_TAG_FUNCTION_CONTEXT);}
         
    | (CALDOC_SEE_TAG_MODULE_CONTEXT    ('=' /*CALDOC_SEE_TAG_EQUALS*/|' '|'\t'|'\f'|'\n'|'\r'|'*')) =>
       CALDOC_SEE_TAG_MODULE_CONTEXT    {$setType(CALDOC_SEE_TAG_MODULE_CONTEXT);}
         
    | (CALDOC_SEE_TAG_DATACONS_CONTEXT  ('=' /*CALDOC_SEE_TAG_EQUALS*/|' '|'\t'|'\f'|'\n'|'\r'|'*')) =>
       CALDOC_SEE_TAG_DATACONS_CONTEXT  {$setType(CALDOC_SEE_TAG_DATACONS_CONTEXT);}
         
    | (CALDOC_SEE_TAG_TYPECONS_CONTEXT  ('=' /*CALDOC_SEE_TAG_EQUALS*/|' '|'\t'|'\f'|'\n'|'\r'|'*')) =>
       CALDOC_SEE_TAG_TYPECONS_CONTEXT  {$setType(CALDOC_SEE_TAG_TYPECONS_CONTEXT);}
         
    | (CALDOC_SEE_TAG_TYPECLASS_CONTEXT ('=' /*CALDOC_SEE_TAG_EQUALS*/|' '|'\t'|'\f'|'\n'|'\r'|'*')) =>
       CALDOC_SEE_TAG_TYPECLASS_CONTEXT {$setType(CALDOC_SEE_TAG_TYPECLASS_CONTEXT);}
         
    | (CALDOC_SEE_TAG_CONS_ID) =>
       CALDOC_SEE_TAG_CONS_ID           {$setType(CONS_ID);}
        
    | (CALDOC_SEE_TAG_VAR_ID) =>
       CALDOC_SEE_TAG_VAR_ID            {$setType(VAR_ID);}
       
    | (CALDOC_SEE_TAG_QUOTE) =>
       CALDOC_SEE_TAG_QUOTE             {$setType(CALDOC_SEE_TAG_QUOTE);}
       
    |  CALDOC_SEE_TAG_UNKNOWN_CONTEXT   {$setType(CALDOC_SEE_TAG_UNKNOWN_CONTEXT);}
    )
    ;         

CALDOC_SEE_TAG_CONS_ID
    :
        {this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT}?
        CONS_ID
        {$setType(CONS_ID); this.state = LexerState.SEE_TAG_LIST;}
    ;

CALDOC_SEE_TAG_VAR_ID
    :
        {this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT}?
        VAR_ID
        {$setType(VAR_ID); this.state = LexerState.SEE_TAG_LIST;}
    ;

CALDOC_SEE_TAG_QUOTE
options {
    paraphrase = "'\"'";
}
    :
        {this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT}?
        '\"'
        {this.state = LexerState.SEE_TAG_LIST;}
    ;

CALDOC_SEE_TAG_EQUALS
options {
    paraphrase = "'='";
}
     :
        {this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT}?
        '='
        {$setType(EQUALS); this.state = LexerState.SEE_TAG_LIST;}
    ;

CALDOC_SEE_TAG_COMMA
options {
    paraphrase = "','";
}
    :
        {this.state == LexerState.SEE_TAG_LIST}?
        ','
        {$setType(COMMA);}
    ;

CALDOC_SEE_TAG_DOT
options {
    paraphrase = "'.'";
}
    :
        {this.state == LexerState.SEE_TAG_LIST}?
        '.'
        {$setType(DOT);}
    ;

protected
CALDOC_AUTHOR_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST}?
      "@author"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_DEPRECATED_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST}?
      "@deprecated"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_RETURN_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST}?
      "@return"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_VERSION_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST}?
      "@version"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_ARG_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST}?
      "@arg"
      {this.state = LexerState.ARG_NAME;}
    ;

CALDOC_ARG_TAG_VAR_ID
    : {this.state == LexerState.ARG_NAME}?
      VAR_ID
      {this.state = LexerState.REGULAR; $setType(VAR_ID);}
    ;
    
CALDOC_ARG_TAG_ORDINAL_FIELD_NAME
    : {this.state == LexerState.ARG_NAME}?
      ORDINAL_FIELD_NAME
      {this.state = LexerState.REGULAR; $setType(ORDINAL_FIELD_NAME);}
    ;

protected
CALDOC_SEE_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST}?
      "@see"
      {this.state = LexerState.SEE_TAG_CONTEXT;}
    ;
    
////==========================
/// Inline tags
//
CALDOC_OPEN_INLINE_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST}?
      "{@"
      {this.state = LexerState.INLINE_TAG;}
    ;

CALDOC_CLOSE_INLINE_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.INLINE_TAG}?
      "@}"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_SUMMARY_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "summary"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_EM_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "em"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_STRONG_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "strong"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_SUP_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "sup"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_SUB_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "sub"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_UNORDERED_LIST_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "unorderedList"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_ORDERED_LIST_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "orderedList"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_ITEM_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "item"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_CODE_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "code"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_URL_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "url"
      {this.state = LexerState.REGULAR;}
    ;

protected
CALDOC_INLINE_LINK_TAG
    : {this.state == LexerState.INLINE_TAG}?
      "link"
      {this.state = LexerState.SEE_TAG_CONTEXT;}
    ;

CALDOC_INLINE_TAG
    : {this.state == LexerState.INLINE_TAG}?
        ( (CALDOC_INLINE_SUMMARY_TAG         (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_SUMMARY_TAG        {$setType(CALDOC_INLINE_SUMMARY_TAG);}
        | (CALDOC_INLINE_EM_TAG              (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_EM_TAG             {$setType(CALDOC_INLINE_EM_TAG);}
        | (CALDOC_INLINE_STRONG_TAG          (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_STRONG_TAG         {$setType(CALDOC_INLINE_STRONG_TAG);}
        | (CALDOC_INLINE_SUP_TAG             (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_SUP_TAG            {$setType(CALDOC_INLINE_SUP_TAG);}
        | (CALDOC_INLINE_SUB_TAG             (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_SUB_TAG            {$setType(CALDOC_INLINE_SUB_TAG);}
        | (CALDOC_INLINE_UNORDERED_LIST_TAG  (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_UNORDERED_LIST_TAG {$setType(CALDOC_INLINE_UNORDERED_LIST_TAG);}
        | (CALDOC_INLINE_ORDERED_LIST_TAG    (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_ORDERED_LIST_TAG   {$setType(CALDOC_INLINE_ORDERED_LIST_TAG);}
        | (CALDOC_INLINE_ITEM_TAG            (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_ITEM_TAG           {$setType(CALDOC_INLINE_ITEM_TAG);}
        | (CALDOC_INLINE_CODE_TAG            (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_CODE_TAG           {$setType(CALDOC_INLINE_CODE_TAG);}
        | (CALDOC_INLINE_URL_TAG             (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_URL_TAG            {$setType(CALDOC_INLINE_URL_TAG);}
        | (CALDOC_INLINE_LINK_TAG            (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_INLINE_LINK_TAG           {$setType(CALDOC_INLINE_LINK_TAG);}
        |                                                                      CALDOC_INLINE_UNKNOWN_TAG        {$setType(CALDOC_INLINE_UNKNOWN_TAG);}
        )
    ;

////============================================
/// All the "unknown" token definitions go here: (these are for error reporting purposes)
//

// having checked all of the possible inline tags and not having found a match, the tag must be an erroneous one.
protected
CALDOC_INLINE_UNKNOWN_TAG
    : {this.state == LexerState.INLINE_TAG}?
      ((~(' '|'\t'|'\f'|'\n'|'\r'|'*'|'@'))!)+
      {this.state = LexerState.REGULAR;}
    ;

// having checked all of the possible tags and not having found a match, the tag must be an erroneous one.
protected
CALDOC_UNKNOWN_TAG
    : {this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST}?
      '@' ((~(' '|'\t'|'\f'|'\n'|'\r'|'*'|'@'))!)*
      {this.state = LexerState.REGULAR;}
    ;

// having checked all of the possible contexts and not having found a match, the context must be an erroneous one.
protected
CALDOC_SEE_TAG_UNKNOWN_CONTEXT
    : {this.state == LexerState.SEE_TAG_CONTEXT}? ((~('=' /*CALDOC_SEE_TAG_EQUALS*/|' '|'\t'|'\f'|'\n'|'\r'|'*'))!)+
    ;

// having checked all of the possible forms of references and not having found a match, the reference must be an erroneous one.
protected
CALDOC_SEE_TAG_UNKNOWN_REFERENCE
    : {this.state == LexerState.SEE_TAG_LIST}?
      ((~('\"' /*CALDOC_SEE_TAG_QUOTE*/
         |',' /*CALDOC_SEE_TAG_COMMA*/
         |'.' /*CALDOC_SEE_TAG_DOT*/
         |' '|'\t'|'\f'|'\n'|'\r'|'*'))!)+
    ;


// when it's nothing else, it is just part of the doc comment's text
protected
CALDOC_REGULAR_TEXT_LINE
    : ( ~('\n'|'\r'|'*'|'@'|'{'|'\\')
      | {LA(2) != '@'}? '{'
      | {LA(2) != '/'}? '*'
      | '\\'
        ( ("{@") => "{@"
        | ('@') => '@'   // no need to separately escape "@}" since that's covered by the escaping of '@'
        | /*empty*/
        )
      )
      
      ( {LA(2) != '}'}? '@'  // allow @ unescaped if in the middle of a regular line
      | ~('\n'|'\r'|'*'|'@'|'{'|'\\')
      | {LA(2) != '@'}? '{'
      | {LA(2) != '/'}? '*'
      | '\\'
        ( ("{@") => "{@"
        | ('@') => '@'   // no need to separately escape "@}" since that's covered by the escaping of '@'
        | /*empty*/
        )
      )*
      {this.state = LexerState.REGULAR;}
    ;

// this rule is meant as a marker rule and not a rule that is invoked (although it is correct and can be invoked)
protected
CALDOC_BLANK_TEXT_LINE
    : (CALDOC_WS)+
      {this.state = LexerState.REGULAR;}
    ;

CALDOC_TEXT_LINE
{ int nLeadingWS = 0; }
    : ("*/") => CALDOC_CLOSE {$setType(CALDOC_CLOSE);}
    
    | ((CALDOC_WS)* '@') =>
        ( (CALDOC_WS! {nLeadingWS++;})*
            ( (CALDOC_AUTHOR_TAG     (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_AUTHOR_TAG       {$setType(CALDOC_AUTHOR_TAG);}
            | (CALDOC_DEPRECATED_TAG (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_DEPRECATED_TAG   {$setType(CALDOC_DEPRECATED_TAG);}
            | (CALDOC_RETURN_TAG     (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_RETURN_TAG       {$setType(CALDOC_RETURN_TAG);}
            | (CALDOC_VERSION_TAG    (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_VERSION_TAG      {$setType(CALDOC_VERSION_TAG);}
            | (CALDOC_ARG_TAG        (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_ARG_TAG          {$setType(CALDOC_ARG_TAG);}
            | (CALDOC_SEE_TAG        (' '|'\t'|'\f'|'\n'|'\r'|'*')) => CALDOC_SEE_TAG          {$setType(CALDOC_SEE_TAG);}
            | (CALDOC_CLOSE_INLINE_TAG)                             => CALDOC_CLOSE_INLINE_TAG {$setType(CALDOC_CLOSE_INLINE_TAG);}
            |                                                          CALDOC_UNKNOWN_TAG      {$setType(CALDOC_UNKNOWN_TAG);}
            )
        )
        { 
          Token newToken = makeToken(_ttype);
          newToken.setColumn(newToken.getColumn() + nLeadingWS);
          newToken.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
          $setToken(newToken);
        }

    | {this.state == LexerState.SEE_TAG_LIST}? CALDOC_SEE_TAG_UNKNOWN_REFERENCE {$setType(CALDOC_SEE_TAG_UNKNOWN_REFERENCE);}

    | CALDOC_REGULAR_TEXT_LINE {if (isCALDocWhitespaceString($getText)) {$setType(CALDOC_BLANK_TEXT_LINE);}}
    ;
