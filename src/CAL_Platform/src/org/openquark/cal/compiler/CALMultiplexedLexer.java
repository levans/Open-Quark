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
 * CALMultiplexedLexer.java
 * Creation date: Jul 19, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.io.Reader;
import java.util.Collection;

import antlr.CharScanner;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamSelector;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * Instances of this class encapsulates the multiplexing of the two lexers used in lexing
 * CAL source: the main CAL lexer and the CALDoc lexer. Since these two lexers are codependent
 * (namely on scanning '/''*''*' as the start of a CALDoc comment, and '*''/' as the end of one),
 * the two lexers should not be created or accessed individually, but rather through an instance
 * of this class, which guarantees that they are always created and used as a pair.
 *
 * @author Joseph Wong
 */
public class CALMultiplexedLexer implements TokenStream {
    /** The number of spaces represented by a tab character '\t'. **/
    public static final int TAB_SIZE = 4;

    /**
     * The compiler instance that owns this multiplexed lexer, or null if this
     * lexer is used in a standalone fashion.
     */
    private final CALCompiler compiler;
    
    /**
     * collection to which filtered tokens are added
     * this may be null, in which case filtered tokens are discarded
     */
    private final Collection<SourceEmbellishment> embellishments;

    /** The main lexer for the CAL grammar. */
    private final CALLexer lexer;

    /** The lexer for the CALDoc grammar. */
    private final CALDocLexer caldocLexer;

    /** The stream selector for switching between the main lexer and the CALDoc lexer. */
    private final TokenStreamSelector streamSelector;

    /**
     * Construct a CALMultiplexedLexer hooked up to a java.io.Reader.
     *
     * @param compiler
     *            the CALCompiler that owns this multiplexed lexer, or null if
     *            this lexer is standalone.
     * @param reader
     *            the Reader to be scanned by this multiplexed lexer.
     * @param embellishments
     *            a collection that is used to store filtered tokens
     *            if this is null the tokens will be discarded, otherwise
     *            they will be added to the collection
     */
    public CALMultiplexedLexer(CALCompiler compiler, Reader reader,
                               Collection<SourceEmbellishment> embellishments) {
        this.compiler = compiler;

        // Make a main lexer and a CALDoc lexer
        lexer = new CALLexer(this, reader);
        caldocLexer = new CALDocLexer(this, lexer.getInputState());

        // Create a lexical selector and add the two lexers
        streamSelector = new TokenStreamSelector();
        streamSelector.addInputStream(lexer, "main");
        streamSelector.addInputStream(caldocLexer, "caldoclexer");

        // Set the starting token stream
        streamSelector.select(lexer);

        this.embellishments = embellishments;
    }

    /**
     * This filters out the comment tokens and buffers them
     * @return the next token from the currently selected lexer.
     */
    public Token nextToken() throws TokenStreamException {
        Token next;

        while ((next = streamSelector.nextToken()) != null &&
                SourceEmbellishment.isEmbellishmentToken(next)) {
            
            if (embellishments != null) {
                SourceEmbellishment emellishment = SourceEmbellishment.make(next);
                if (emellishment != null) {
                    embellishments.add(emellishment);
                }
            }
        }

        return next;
    }

    /**
     * Set the name of the file currently being scanned into the two constituent
     * lexers.
     *
     * @param filename
     *            the name of the file being scanned.
     */
    public void setFilename(String filename) {
        lexer.setFilename(filename);
        caldocLexer.setFilename(filename);
    }

    /**
     * @return the tab size used by the two constituent lexers.
     */
    public int getTabSize() {
        return lexer.getTabSize();
    }

    public Token getTokenObject() {
        return ((CharScanner) streamSelector.getCurrentStream()).getTokenObject();
    }

    /**
     * Switch the parser to use the CALDoc lexer.
     */
    void switchToCALDocLexer() {
        caldocLexer.resetState();
        streamSelector.select(caldocLexer);
    }

    /**
     * Switch the parser back to using the CAL main lexer.
     */
    void switchOutOfCALDocLexer() {
        streamSelector.select(lexer);
    }

    /**
     * Provides a reportError implementation to the two lexers so as to direct
     * standard error handling through to the CALCompiler error scheme.
     *
     * @param ex
     *            RecognitionException the recognition exception that originated
     *            the problem
     */
    void reportError(RecognitionException ex) {
        if (compiler != null) {
            final SourceRange sourceRange = CALParser.makeSourceRangeFromException(
                ex);
            compiler.logMessage(new CompilerMessage(sourceRange,
                                                    new MessageKind.Error.SyntaxError(),
                                                    ex));
        }
    }
}
