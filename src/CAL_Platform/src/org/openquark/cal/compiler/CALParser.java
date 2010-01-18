// $ANTLR 2.7.6 (2005-12-22): "CAL.g" -> "CALParser.java"$

// Package declaration
package org.openquark.cal.compiler;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

/*************************************** 
 * CAL grammar definition starts here  *   
 ***************************************/
@SuppressWarnings(\u0022all\u0022) final class CALParser extends antlr.LLkParser       implements CALTokenTypes
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

protected CALParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CALParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected CALParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public CALParser(TokenStream lexer) {
  this(lexer,2);
}

public CALParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void codeGemExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST codeGemExpr_AST = null;
		
		try {      // for error handling
			expr(LT(1), "a valid expression");
			astFactory.addASTChild(currentAST, returnAST);
			match(Token.EOF_TYPE);
			codeGemExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = codeGemExpr_AST;
	}
	
	public final void expr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expr_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_let:
			{
				AST tmp2_AST = null;
				tmp2_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp2_AST);
				match(LITERAL_let);
				letDefnList(LT(1), "a valid list of let definitions");
				astFactory.addASTChild(currentAST, returnAST);
				match(LITERAL_in);
				expr(LT(1), "a valid let body");
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_if:
			{
				AST tmp4_AST = null;
				tmp4_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp4_AST);
				match(LITERAL_if);
				expr(LT(1), "a valid conditional expression for an if expression");
				astFactory.addASTChild(currentAST, returnAST);
				match(LITERAL_then);
				expr(LT(1), "a valid then-clause expression");
				astFactory.addASTChild(currentAST, returnAST);
				match(LITERAL_else);
				expr(LT(1), "a valid else-clause expression");
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_case:
			{
				AST tmp7_AST = null;
				tmp7_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp7_AST);
				match(LITERAL_case);
				expr(LT(1), "a valid condition expression for a case expression");
				astFactory.addASTChild(currentAST, returnAST);
				match(LITERAL_of);
				altList(LT(1), "a list of case alternatives");
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
				break;
			}
			case BACKSLASH:
			{
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(BACKSLASH);
				if ( inputState.guessing==0 ) {
					node_AST.setType (LAMBDA_DEFN);
				}
				lambdaParamList(LT(1), "a valid parameter list for a lambda expression");
				astFactory.addASTChild(currentAST, returnAST);
				match(RARROW);
				expr(LT(1), "a valid defining expression for a lambda expression");
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
				break;
			}
			case CONS_ID:
			case VAR_ID:
			case OPEN_PAREN:
			case OPEN_BRACKET:
			case OPEN_BRACE:
			case STRING_LITERAL:
			case MINUS:
			case CHAR_LITERAL:
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
			{
				typeSignatureExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_1, _tokenSet_2);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_2);
			} else {
				throw ex;
			}
		}
		returnAST = expr_AST;
	}
	
	public final void startTypeSignature() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST startTypeSignature_AST = null;
		
		try {      // for error handling
			typeSignature(LT(1), "a valid type signature");
			astFactory.addASTChild(currentAST, returnAST);
			match(Token.EOF_TYPE);
			startTypeSignature_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = startTypeSignature_AST;
	}
	
	public final void typeSignature(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeSignature_AST = null;
		
		try {      // for error handling
			typeContextList(LT(1), "a type context");
			astFactory.addASTChild(currentAST, returnAST);
			type(LT(1), "a type");
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				typeSignature_AST = (AST)currentAST.root;
				typeSignature_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE_SIGNATURE,"TYPE_SIGNATURE")).add(typeSignature_AST));
				currentAST.root = typeSignature_AST;
				currentAST.child = typeSignature_AST!=null &&typeSignature_AST.getFirstChild()!=null ?
					typeSignature_AST.getFirstChild() : typeSignature_AST;
				currentAST.advanceChildToEnd();
			}
			typeSignature_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_3, _tokenSet_4);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_4);
			} else {
				throw ex;
			}
		}
		returnAST = typeSignature_AST;
	}
	
	public final void module() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST module_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			astFactory.addASTChild(currentAST, returnAST);
			moduleNameDecl(LT(1), "a valid module name");
			astFactory.addASTChild(currentAST, returnAST);
			importDeclarationList(LT(1), "a list of import declarations");
			astFactory.addASTChild(currentAST, returnAST);
			friendDeclarationList(LT(1), "a list of friend declarations");
			astFactory.addASTChild(currentAST, returnAST);
			outerDefnList(LT(1), "a list of top-level definitions");
			astFactory.addASTChild(currentAST, returnAST);
			match(Token.EOF_TYPE);
			if ( inputState.guessing==0 ) {
				module_AST = (AST)currentAST.root;
				module_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MODULE_DEFN,"MODULE_DEFN")).add(module_AST));
				currentAST.root = module_AST;
				currentAST.child = module_AST!=null &&module_AST.getFirstChild()!=null ?
					module_AST.getFirstChild() : module_AST;
				currentAST.advanceChildToEnd();
			}
			module_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = module_AST;
	}
	
	public final void optionalDocComment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST optionalDocComment_AST = null;
		AST comment_AST = null;
		AST moreComments_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CALDOC_OPEN:
			{
				docComment();
				comment_AST = (AST)returnAST;
				{
				switch ( LA(1)) {
				case CONS_ID:
				case VAR_ID:
				case LITERAL_module:
				case LITERAL_class:
				case OPEN_PAREN:
				case OPEN_BRACKET:
				case OPEN_BRACE:
				case LITERAL_data:
				case LITERAL_foreign:
				case LITERAL_instance:
				case LITERAL_primitive:
				case LITERAL_public:
				case LITERAL_private:
				case LITERAL_protected:
				case MINUS:
				case UNDERSCORE:
				case CHAR_LITERAL:
				case INTEGER_LITERAL:
				{
					if ( inputState.guessing==0 ) {
						optionalDocComment_AST = (AST)currentAST.root;
						optionalDocComment_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OPTIONAL_CALDOC_COMMENT,"OPTIONAL_CALDOC_COMMENT")).add(comment_AST));
						currentAST.root = optionalDocComment_AST;
						currentAST.child = optionalDocComment_AST!=null &&optionalDocComment_AST.getFirstChild()!=null ?
							optionalDocComment_AST.getFirstChild() : optionalDocComment_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				case CALDOC_OPEN:
				{
					if ( inputState.guessing==0 ) {
						reportUnassociatedCALDocCommentError((ParseTreeNode)comment_AST);
					}
					moreDocComments();
					moreComments_AST = (AST)returnAST;
					if ( inputState.guessing==0 ) {
						optionalDocComment_AST = (AST)currentAST.root;
						optionalDocComment_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OPTIONAL_CALDOC_COMMENT,"OPTIONAL_CALDOC_COMMENT")).add(moreComments_AST));
						currentAST.root = optionalDocComment_AST;
						currentAST.child = optionalDocComment_AST!=null &&optionalDocComment_AST.getFirstChild()!=null ?
							optionalDocComment_AST.getFirstChild() : optionalDocComment_AST;
						currentAST.advanceChildToEnd();
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				optionalDocComment_AST = (AST)currentAST.root;
				break;
			}
			case CONS_ID:
			case VAR_ID:
			case LITERAL_module:
			case LITERAL_class:
			case OPEN_PAREN:
			case OPEN_BRACKET:
			case OPEN_BRACE:
			case LITERAL_data:
			case LITERAL_foreign:
			case LITERAL_instance:
			case LITERAL_primitive:
			case LITERAL_public:
			case LITERAL_private:
			case LITERAL_protected:
			case MINUS:
			case UNDERSCORE:
			case CHAR_LITERAL:
			case INTEGER_LITERAL:
			{
				if ( inputState.guessing==0 ) {
					optionalDocComment_AST = (AST)currentAST.root;
					optionalDocComment_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(OPTIONAL_CALDOC_COMMENT,"OPTIONAL_CALDOC_COMMENT")));
					currentAST.root = optionalDocComment_AST;
					currentAST.child = optionalDocComment_AST!=null &&optionalDocComment_AST.getFirstChild()!=null ?
						optionalDocComment_AST.getFirstChild() : optionalDocComment_AST;
					currentAST.advanceChildToEnd();
				}
				optionalDocComment_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = optionalDocComment_AST;
	}
	
	public final void moduleNameDecl(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleNameDecl_AST = null;
		
		try {      // for error handling
			match(LITERAL_module);
			moduleName(LT(1), "a semicolon-terminated module name");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			moduleNameDecl_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_6, _tokenSet_7);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_7);
			} else {
				throw ex;
			}
		}
		returnAST = moduleNameDecl_AST;
	}
	
	public final void importDeclarationList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST importDeclarationList_AST = null;
		
		try {      // for error handling
			{
			_loop14:
			do {
				if ((LA(1)==LITERAL_import)) {
					importDeclaration(LT(1), "an import declaration");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop14;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				importDeclarationList_AST = (AST)currentAST.root;
				importDeclarationList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(IMPORT_DECLARATION_LIST,"IMPORT_DECLARATION_LIST")).add(importDeclarationList_AST));
				currentAST.root = importDeclarationList_AST;
				currentAST.child = importDeclarationList_AST!=null &&importDeclarationList_AST.getFirstChild()!=null ?
					importDeclarationList_AST.getFirstChild() : importDeclarationList_AST;
				currentAST.advanceChildToEnd();
			}
			importDeclarationList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_7, _tokenSet_8);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_8);
			} else {
				throw ex;
			}
		}
		returnAST = importDeclarationList_AST;
	}
	
	public final void friendDeclarationList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST friendDeclarationList_AST = null;
		
		try {      // for error handling
			{
			_loop35:
			do {
				if ((LA(1)==LITERAL_friend)) {
					friendDeclaration(LT(1), "a friend declaration");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop35;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				friendDeclarationList_AST = (AST)currentAST.root;
				friendDeclarationList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FRIEND_DECLARATION_LIST,"FRIEND_DECLARATION_LIST")).add(friendDeclarationList_AST));
				currentAST.root = friendDeclarationList_AST;
				currentAST.child = friendDeclarationList_AST!=null &&friendDeclarationList_AST.getFirstChild()!=null ?
					friendDeclarationList_AST.getFirstChild() : friendDeclarationList_AST;
				currentAST.advanceChildToEnd();
			}
			friendDeclarationList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_8, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = friendDeclarationList_AST;
	}
	
	public final void outerDefnList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST outerDefnList_AST = null;
		
		try {      // for error handling
			{
			_loop39:
			do {
				if ((_tokenSet_10.member(LA(1)))) {
					outerDefn(LT(1), "an outer definition");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop39;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				outerDefnList_AST = (AST)currentAST.root;
				outerDefnList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OUTER_DEFN_LIST,"OUTER_DEFN_LIST")).add(outerDefnList_AST));
				currentAST.root = outerDefnList_AST;
				currentAST.child = outerDefnList_AST!=null &&outerDefnList_AST.getFirstChild()!=null ?
					outerDefnList_AST.getFirstChild() : outerDefnList_AST;
				currentAST.advanceChildToEnd();
			}
			outerDefnList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_9, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = outerDefnList_AST;
	}
	
	public final void moduleHeader() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleHeader_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			astFactory.addASTChild(currentAST, returnAST);
			moduleNameDecl(LT(1), "a valid module name");
			astFactory.addASTChild(currentAST, returnAST);
			importDeclarationList(LT(1), "a list of import declarations");
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				moduleHeader_AST = (AST)currentAST.root;
				moduleHeader_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MODULE_DEFN,"MODULE_DEFN")).add(moduleHeader_AST));
				currentAST.root = moduleHeader_AST;
				currentAST.child = moduleHeader_AST!=null &&moduleHeader_AST.getFirstChild()!=null ?
					moduleHeader_AST.getFirstChild() : moduleHeader_AST;
				currentAST.advanceChildToEnd();
			}
			moduleHeader_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = moduleHeader_AST;
	}
	
	public final void adjunct() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST adjunct_AST = null;
		
		try {      // for error handling
			adjunctDefnList(LT(1), "a list of adjunct definitions");
			astFactory.addASTChild(currentAST, returnAST);
			match(Token.EOF_TYPE);
			adjunct_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = adjunct_AST;
	}
	
	public final void adjunctDefnList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST adjunctDefnList_AST = null;
		
		try {      // for error handling
			{
			_loop8:
			do {
				if ((_tokenSet_11.member(LA(1)))) {
					adjunctDefn(LT(1), "a valid adjunct definition");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop8;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				adjunctDefnList_AST = (AST)currentAST.root;
				adjunctDefnList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(OUTER_DEFN_LIST,"ADJUNCT_DEFN_LIST")).add(adjunctDefnList_AST));
				currentAST.root = adjunctDefnList_AST;
				currentAST.child = adjunctDefnList_AST!=null &&adjunctDefnList_AST.getFirstChild()!=null ?
					adjunctDefnList_AST.getFirstChild() : adjunctDefnList_AST;
				currentAST.advanceChildToEnd();
			}
			adjunctDefnList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_12, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = adjunctDefnList_AST;
	}
	
	public final void adjunctDefn(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST adjunctDefn_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			{
			if ((LA(1)==VAR_ID) && (LA(2)==COLONCOLON)) {
				topLevelTypeDeclarationWithOptionalDocComment(doc_AST, LT(1), "top level type declaration");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==LITERAL_instance)) {
				instanceDefnWithOptionalDocComment(doc_AST, LT(1), "instance definition");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((_tokenSet_13.member(LA(1))) && (_tokenSet_14.member(LA(2)))) {
				topLevelFunctionWithOptionalDocComment(doc_AST, LT(1), "top level function definition");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			adjunctDefn_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_11, _tokenSet_12);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_12);
			} else {
				throw ex;
			}
		}
		returnAST = adjunctDefn_AST;
	}
	
	public final void topLevelTypeDeclarationWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST topLevelTypeDeclarationWithOptionalDocComment_AST = null;
		
		try {      // for error handling
			typeDeclaration(LT(1), "a type declaration for a top-level function");
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				topLevelTypeDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
				topLevelTypeDeclarationWithOptionalDocComment_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(TOP_LEVEL_TYPE_DECLARATION,"TOP_LEVEL_TYPE_DECLARATION")).add(doc).add(topLevelTypeDeclarationWithOptionalDocComment_AST));
				currentAST.root = topLevelTypeDeclarationWithOptionalDocComment_AST;
				currentAST.child = topLevelTypeDeclarationWithOptionalDocComment_AST!=null &&topLevelTypeDeclarationWithOptionalDocComment_AST.getFirstChild()!=null ?
					topLevelTypeDeclarationWithOptionalDocComment_AST.getFirstChild() : topLevelTypeDeclarationWithOptionalDocComment_AST;
				currentAST.advanceChildToEnd();
			}
			topLevelTypeDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = topLevelTypeDeclarationWithOptionalDocComment_AST;
	}
	
	public final void instanceDefnWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instanceDefnWithOptionalDocComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(LITERAL_instance);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (INSTANCE_DEFN, "instance");
			}
			instanceName(LT(1), "an instance name");
			astFactory.addASTChild(currentAST, returnAST);
			match(LITERAL_where);
			instanceMethodList(LT(1), "a list of instance method definitions");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				instanceDefnWithOptionalDocComment_AST = (AST)currentAST.root;
				
				AST firstChild = instanceDefnWithOptionalDocComment_AST.getFirstChild();
				doc.setNextSibling(firstChild);
				instanceDefnWithOptionalDocComment_AST.setFirstChild(doc);
				
			}
			instanceDefnWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_16, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = instanceDefnWithOptionalDocComment_AST;
	}
	
	public final void topLevelFunctionWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST topLevelFunctionWithOptionalDocComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		Token  semicolonNode = null;
		AST semicolonNode_AST = null;
		
		try {      // for error handling
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			functionName(LT(1), "a top-level function name");
			astFactory.addASTChild(currentAST, returnAST);
			functionParamList(LT(1), "a parameter list for a top-level function");
			astFactory.addASTChild(currentAST, returnAST);
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(EQUALS);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (TOP_LEVEL_FUNCTION_DEFN, "TOP_LEVEL_FUNCTION_DEFN");
			}
			expr(LT(1), "a valid defining expression for a top-level function");
			astFactory.addASTChild(currentAST, returnAST);
			semicolonNode = LT(1);
			semicolonNode_AST = astFactory.create(semicolonNode);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				((ParseTreeNode)node_AST).addOmittedDelimiter(semicolonNode_AST);
			}
			if ( inputState.guessing==0 ) {
				topLevelFunctionWithOptionalDocComment_AST = (AST)currentAST.root;
				
				AST firstChild = topLevelFunctionWithOptionalDocComment_AST.getFirstChild();
				doc.setNextSibling(firstChild);
				topLevelFunctionWithOptionalDocComment_AST.setFirstChild(doc);
				
			}
			topLevelFunctionWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_13, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = topLevelFunctionWithOptionalDocComment_AST;
	}
	
	public final void moduleName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moduleName_AST = null;
		AST bareCons_AST = null;
		Token  dot = null;
		AST dot_AST = null;
		
		try {      // for error handling
			cons();
			bareCons_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				moduleName_AST = (AST)currentAST.root;
				moduleName_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(HIERARCHICAL_MODULE_NAME,"HIERARCHICAL_MODULE_NAME")).add(astFactory.create(HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER,"HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER")).add(bareCons_AST));
				currentAST.root = moduleName_AST;
				currentAST.child = moduleName_AST!=null &&moduleName_AST.getFirstChild()!=null ?
					moduleName_AST.getFirstChild() : moduleName_AST;
				currentAST.advanceChildToEnd();
			}
			{
			_loop32:
			do {
				if ((LA(1)==DOT) && (LA(2)==CONS_ID)) {
					dot = LT(1);
					dot_AST = astFactory.create(dot);
					astFactory.makeASTRoot(currentAST, dot_AST);
					match(DOT);
					if ( inputState.guessing==0 ) {
						dot_AST.initialize(HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME");
					}
					cons();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop32;
				}
				
			} while (true);
			}
			moduleName_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_18);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_18);
			} else {
				throw ex;
			}
		}
		returnAST = moduleName_AST;
	}
	
	public final void importDeclaration(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST importDeclaration_AST = null;
		Token  importNode = null;
		AST importNode_AST = null;
		Token  semicolonNode = null;
		AST semicolonNode_AST = null;
		
		try {      // for error handling
			importNode = LT(1);
			importNode_AST = astFactory.create(importNode);
			astFactory.makeASTRoot(currentAST, importNode_AST);
			match(LITERAL_import);
			moduleName(LT(1), "a valid module name");
			astFactory.addASTChild(currentAST, returnAST);
			maybeUsingClause(LT(1), "a semicolon or a using clause");
			astFactory.addASTChild(currentAST, returnAST);
			semicolonNode = LT(1);
			semicolonNode_AST = astFactory.create(semicolonNode);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				((ParseTreeNode)importNode_AST).addOmittedDelimiter(semicolonNode_AST);
			}
			importDeclaration_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_19, _tokenSet_7);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_7);
			} else {
				throw ex;
			}
		}
		returnAST = importDeclaration_AST;
	}
	
	public final void maybeUsingClause(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST maybeUsingClause_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_using:
			{
				usingClause(LT(1), "a semicolon-terminated using clause");
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			maybeUsingClause_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_20, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = maybeUsingClause_AST;
	}
	
	public final void usingClause(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST usingClause_AST = null;
		
		try {      // for error handling
			AST tmp17_AST = null;
			tmp17_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp17_AST);
			match(LITERAL_using);
			{
			_loop20:
			do {
				if (((LA(1) >= LITERAL_function && LA(1) <= LITERAL_typeClass))) {
					usingItem(LT(1), "a valid using item starting with 'function =', 'typeConstructor =', 'dataConstructor =', or 'typeClass ='");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop20;
				}
				
			} while (true);
			}
			usingClause_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_22, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = usingClause_AST;
	}
	
	public final void usingItem(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST usingItem_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_function:
			{
				AST tmp18_AST = null;
				tmp18_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp18_AST);
				match(LITERAL_function);
				match(EQUALS);
				usingVarList(LT(1), "a semicolon-terminated list of function names");
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMICOLON);
				usingItem_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_typeConstructor:
			{
				AST tmp21_AST = null;
				tmp21_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp21_AST);
				match(LITERAL_typeConstructor);
				match(EQUALS);
				usingConsList(LT(1), "a semicolon-terminated list of type constructor names");
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMICOLON);
				usingItem_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_dataConstructor:
			{
				AST tmp24_AST = null;
				tmp24_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp24_AST);
				match(LITERAL_dataConstructor);
				match(EQUALS);
				usingConsList(LT(1), "a semicolon-terminated list of data constructor names");
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMICOLON);
				usingItem_AST = (AST)currentAST.root;
				break;
			}
			case LITERAL_typeClass:
			{
				AST tmp27_AST = null;
				tmp27_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp27_AST);
				match(LITERAL_typeClass);
				match(EQUALS);
				usingConsList(LT(1), "a semicolon-terminated list of type class names");
				astFactory.addASTChild(currentAST, returnAST);
				match(SEMICOLON);
				usingItem_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_23, _tokenSet_24);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_24);
			} else {
				throw ex;
			}
		}
		returnAST = usingItem_AST;
	}
	
	public final void usingVarList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST usingVarList_AST = null;
		
		try {      // for error handling
			{
			var();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop29:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					var();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop29;
				}
				
			} while (true);
			}
			}
			usingVarList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = usingVarList_AST;
	}
	
	public final void usingConsList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST usingConsList_AST = null;
		
		try {      // for error handling
			{
			cons();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop25:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					cons();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop25;
				}
				
			} while (true);
			}
			}
			usingConsList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = usingConsList_AST;
	}
	
	public final void cons() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST cons_AST = null;
		
		try {      // for error handling
			AST tmp32_AST = null;
			tmp32_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp32_AST);
			match(CONS_ID);
			cons_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_25);
			} else {
			  throw ex;
			}
		}
		returnAST = cons_AST;
	}
	
	public final void var() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST var_AST = null;
		
		try {      // for error handling
			AST tmp33_AST = null;
			tmp33_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp33_AST);
			match(VAR_ID);
			var_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_26);
			} else {
			  throw ex;
			}
		}
		returnAST = var_AST;
	}
	
	public final void friendDeclaration(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST friendDeclaration_AST = null;
		
		try {      // for error handling
			AST tmp34_AST = null;
			tmp34_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp34_AST);
			match(LITERAL_friend);
			moduleName(LT(1), "a valid module name");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			friendDeclaration_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_27, _tokenSet_8);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_8);
			} else {
				throw ex;
			}
		}
		returnAST = friendDeclaration_AST;
	}
	
	public final void outerDefn(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST outerDefn_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case LITERAL_instance:
			{
				instanceDefnWithOptionalDocComment(doc_AST, LT(1), "an instance definition");
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LITERAL_primitive:
			{
				primitiveFunctionDeclarationWithOptionalDocComment(doc_AST, LT(1), "a primitive function declaration");
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LITERAL_foreign:
			{
				foreignFunctionDeclarationWithOptionalDocComment(doc_AST, LT(1), "a foreign function declaration");
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
				if ((LA(1)==VAR_ID) && (LA(2)==COLONCOLON)) {
					topLevelTypeDeclarationWithOptionalDocComment(doc_AST, LT(1), "a top-level type declaration");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((_tokenSet_13.member(LA(1))) && (_tokenSet_14.member(LA(2)))) {
					topLevelFunctionWithOptionalDocComment(doc_AST, LT(1), "a top-level function definition");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((LA(1)==LITERAL_data) && (LA(2)==LITERAL_foreign)) {
					foreignDataDeclarationWithOptionalDocComment(doc_AST, LT(1), "a foreign data declaration");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else if ((LA(1)==LITERAL_data) && (_tokenSet_28.member(LA(2)))) {
					dataDeclarationWithOptionalDocComment(doc_AST, LT(1), "a data declaration");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					boolean synPredMatched43 = false;
					if (((_tokenSet_29.member(LA(1))) && (_tokenSet_30.member(LA(2))))) {
						int _m43 = mark();
						synPredMatched43 = true;
						inputState.guessing++;
						try {
							{
							accessModifier(null,null);
							match(LITERAL_class);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched43 = false;
						}
						rewind(_m43);
inputState.guessing--;
					}
					if ( synPredMatched43 ) {
						typeClassDefnWithOptionalDocComment(doc_AST, LT(1), "a class definition");
						astFactory.addASTChild(currentAST, returnAST);
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}}
				}
				outerDefn_AST = (AST)currentAST.root;
			}
			catch (NoViableAltException ex) {
				if (inputState.guessing==0) {
					reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_10, _tokenSet_9);
				} else {
					throw ex;
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex, _tokenSet_9);
				} else {
					throw ex;
				}
			}
			returnAST = outerDefn_AST;
		}
		
	public final void foreignDataDeclarationWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST foreignDataDeclarationWithOptionalDocComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(LITERAL_data);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (FOREIGN_DATA_DECLARATION, "foreignData");
			}
			match(LITERAL_foreign);
			match(LITERAL_unsafe);
			match(LITERAL_import);
			match(LITERAL_jvm);
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			externalName();
			astFactory.addASTChild(currentAST, returnAST);
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			cons();
			astFactory.addASTChild(currentAST, returnAST);
			maybeDerivingClause(LT(1), "a deriving clause or a semicolon");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				foreignDataDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
				
				AST firstChild = foreignDataDeclarationWithOptionalDocComment_AST.getFirstChild();
				doc.setNextSibling(firstChild);
				foreignDataDeclarationWithOptionalDocComment_AST.setFirstChild(doc);
				
			}
			foreignDataDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_31, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = foreignDataDeclarationWithOptionalDocComment_AST;
	}
	
	public final void dataDeclarationWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataDeclarationWithOptionalDocComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(LITERAL_data);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (DATA_DECLARATION, "data");
			}
			simpleType(LT(1), "a type constructor");
			astFactory.addASTChild(currentAST, returnAST);
			match(EQUALS);
			dataDeclarationBody(LT(1), "a bar-separated list of data constructor definitions");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				dataDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
				
				AST firstChild = dataDeclarationWithOptionalDocComment_AST.getFirstChild();
				doc.setNextSibling(firstChild);
				dataDeclarationWithOptionalDocComment_AST.setFirstChild(doc);
				
			}
			dataDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_31, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = dataDeclarationWithOptionalDocComment_AST;
	}
	
	public final void accessModifier(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST accessModifier_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_public:
			{
				AST tmp43_AST = null;
				tmp43_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp43_AST);
				match(LITERAL_public);
				break;
			}
			case LITERAL_private:
			{
				AST tmp44_AST = null;
				tmp44_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp44_AST);
				match(LITERAL_private);
				break;
			}
			case LITERAL_protected:
			{
				AST tmp45_AST = null;
				tmp45_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp45_AST);
				match(LITERAL_protected);
				break;
			}
			case CONS_ID:
			case VAR_ID:
			case LITERAL_class:
			case STRING_LITERAL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				accessModifier_AST = (AST)currentAST.root;
				accessModifier_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ACCESS_MODIFIER,"ACCESS_MODIFIER")).add(accessModifier_AST));
				currentAST.root = accessModifier_AST;
				currentAST.child = accessModifier_AST!=null &&accessModifier_AST.getFirstChild()!=null ?
					accessModifier_AST.getFirstChild() : accessModifier_AST;
				currentAST.advanceChildToEnd();
			}
			accessModifier_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_32, _tokenSet_33);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_33);
			} else {
				throw ex;
			}
		}
		returnAST = accessModifier_AST;
	}
	
	public final void typeClassDefnWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeClassDefnWithOptionalDocComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(LITERAL_class);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (TYPE_CLASS_DEFN, "class");
			}
			classContextList(LT(1), "a class context");
			astFactory.addASTChild(currentAST, returnAST);
			typeClassName(LT(1), "a type class name");
			astFactory.addASTChild(currentAST, returnAST);
			typeClassParam(LT(1), "a type variable");
			astFactory.addASTChild(currentAST, returnAST);
			match(LITERAL_where);
			classMethodList(LT(1), "a list of class method definitions");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				typeClassDefnWithOptionalDocComment_AST = (AST)currentAST.root;
				
				AST firstChild = typeClassDefnWithOptionalDocComment_AST.getFirstChild();
				doc.setNextSibling(firstChild);
				typeClassDefnWithOptionalDocComment_AST.setFirstChild(doc);
				
			}
			typeClassDefnWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_29, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = typeClassDefnWithOptionalDocComment_AST;
	}
	
	public final void primitiveFunctionDeclarationWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primitiveFunctionDeclarationWithOptionalDocComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(LITERAL_primitive);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (PRIMITIVE_FUNCTION_DECLARATION, "primitiveFunc");
			}
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			typeDeclaration(LT(1), "a type declaration for a primitive function");
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				primitiveFunctionDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
				
				AST firstChild = primitiveFunctionDeclarationWithOptionalDocComment_AST.getFirstChild();
				doc.setNextSibling(firstChild);
				primitiveFunctionDeclarationWithOptionalDocComment_AST.setFirstChild(doc);
				
			}
			primitiveFunctionDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_34, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = primitiveFunctionDeclarationWithOptionalDocComment_AST;
	}
	
	public final void foreignFunctionDeclarationWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST foreignFunctionDeclarationWithOptionalDocComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(LITERAL_foreign);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (FOREIGN_FUNCTION_DECLARATION, "foreignFunction");
			}
			match(LITERAL_unsafe);
			match(LITERAL_import);
			match(LITERAL_jvm);
			externalName();
			astFactory.addASTChild(currentAST, returnAST);
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			typeDeclaration(LT(1), "a type declaration for a foreign function");
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				foreignFunctionDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
				
				AST firstChild = foreignFunctionDeclarationWithOptionalDocComment_AST.getFirstChild();
				doc.setNextSibling(firstChild);
				foreignFunctionDeclarationWithOptionalDocComment_AST.setFirstChild(doc);
				
			}
			foreignFunctionDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_35, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_9);
			} else {
				throw ex;
			}
		}
		returnAST = foreignFunctionDeclarationWithOptionalDocComment_AST;
	}
	
	public final void topLevelTypeDeclaration(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST topLevelTypeDeclaration_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			topLevelTypeDeclarationWithOptionalDocComment(doc_AST, LT(1), "a top-level type declaration");
			astFactory.addASTChild(currentAST, returnAST);
			topLevelTypeDeclaration_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_36, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = topLevelTypeDeclaration_AST;
	}
	
	public final void typeDeclaration(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeDeclaration_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			var();
			astFactory.addASTChild(currentAST, returnAST);
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(COLONCOLON);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (TYPE_DECLARATION, "::");
			}
			typeSignature(LT(1), "a valid type signature for a variable");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			typeDeclaration_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_37);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_37);
			} else {
				throw ex;
			}
		}
		returnAST = typeDeclaration_AST;
	}
	
	public final void letDefnTypeDeclarationWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST letDefnTypeDeclarationWithOptionalDocComment_AST = null;
		
		try {      // for error handling
			typeDeclaration(LT(1), "a type declaration for a let definition");
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				letDefnTypeDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
				letDefnTypeDeclarationWithOptionalDocComment_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(LET_DEFN_TYPE_DECLARATION,"LET_DEFN_TYPE_DECLARATION")).add(doc).add(letDefnTypeDeclarationWithOptionalDocComment_AST));
				currentAST.root = letDefnTypeDeclarationWithOptionalDocComment_AST;
				currentAST.child = letDefnTypeDeclarationWithOptionalDocComment_AST!=null &&letDefnTypeDeclarationWithOptionalDocComment_AST.getFirstChild()!=null ?
					letDefnTypeDeclarationWithOptionalDocComment_AST.getFirstChild() : letDefnTypeDeclarationWithOptionalDocComment_AST;
				currentAST.advanceChildToEnd();
			}
			letDefnTypeDeclarationWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_38);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_38);
			} else {
				throw ex;
			}
		}
		returnAST = letDefnTypeDeclarationWithOptionalDocComment_AST;
	}
	
	public final void typeContextList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeContextList_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		Token  closeParenNode = null;
		AST closeParenNode_AST = null;
		
		try {      // for error handling
			boolean synPredMatched51 = false;
			if (((LA(1)==CONS_ID||LA(1)==VAR_ID) && (_tokenSet_39.member(LA(2))))) {
				int _m51 = mark();
				synPredMatched51 = true;
				inputState.guessing++;
				try {
					{
					typeContext(null,null);
					match(IMPLIES);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched51 = false;
				}
				rewind(_m51);
inputState.guessing--;
			}
			if ( synPredMatched51 ) {
				typeContext(LT(1), "a type context");
				astFactory.addASTChild(currentAST, returnAST);
				match(IMPLIES);
				if ( inputState.guessing==0 ) {
					typeContextList_AST = (AST)currentAST.root;
					typeContextList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE_CONTEXT_SINGLETON,"TYPE_CONTEXT_SINGLETON")).add(typeContextList_AST));
					currentAST.root = typeContextList_AST;
					currentAST.child = typeContextList_AST!=null &&typeContextList_AST.getFirstChild()!=null ?
						typeContextList_AST.getFirstChild() : typeContextList_AST;
					currentAST.advanceChildToEnd();
				}
				typeContextList_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched53 = false;
				if (((LA(1)==OPEN_PAREN) && (_tokenSet_40.member(LA(2))))) {
					int _m53 = mark();
					synPredMatched53 = true;
					inputState.guessing++;
					try {
						{
						match(OPEN_PAREN);
						typeContextZeroOrMore(null, null);
						match(CLOSE_PAREN);
						match(IMPLIES);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched53 = false;
					}
					rewind(_m53);
inputState.guessing--;
				}
				if ( synPredMatched53 ) {
					openParenNode = LT(1);
					openParenNode_AST = astFactory.create(openParenNode);
					astFactory.makeASTRoot(currentAST, openParenNode_AST);
					match(OPEN_PAREN);
					if ( inputState.guessing==0 ) {
						openParenNode_AST.initialize (TYPE_CONTEXT_LIST, "TYPE_CONTEXT_LIST");
					}
					typeContextZeroOrMore(LT(1), "a comma-separated list of type contexts");
					astFactory.addASTChild(currentAST, returnAST);
					closeParenNode = LT(1);
					closeParenNode_AST = astFactory.create(closeParenNode);
					match(CLOSE_PAREN);
					if ( inputState.guessing==0 ) {
						((ParseTreeNode)openParenNode_AST).addOmittedDelimiter(closeParenNode_AST);
					}
					match(IMPLIES);
					typeContextList_AST = (AST)currentAST.root;
				}
				else if ((_tokenSet_3.member(LA(1))) && (_tokenSet_41.member(LA(2)))) {
					if ( inputState.guessing==0 ) {
						typeContextList_AST = (AST)currentAST.root;
						typeContextList_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(TYPE_CONTEXT_NOTHING,"TYPE_CONTEXT_NOTHING")));
						currentAST.root = typeContextList_AST;
						currentAST.child = typeContextList_AST!=null &&typeContextList_AST.getFirstChild()!=null ?
							typeContextList_AST.getFirstChild() : typeContextList_AST;
						currentAST.advanceChildToEnd();
					}
					typeContextList_AST = (AST)currentAST.root;
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (NoViableAltException ex) {
				if (inputState.guessing==0) {
					reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_3, _tokenSet_3);
				} else {
					throw ex;
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex, _tokenSet_3);
				} else {
					throw ex;
				}
			}
			returnAST = typeContextList_AST;
		}
		
	public final void type(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST type_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			typeApplication(LT(1), "a type application");
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case RARROW:
			{
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(RARROW);
				if ( inputState.guessing==0 ) {
					node_AST.initialize (FUNCTION_TYPE_CONSTRUCTOR, "->");
				}
				type(LT(1), "a type application");
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case COMMA:
			case SEMICOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_default:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			type_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_3, _tokenSet_4);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_4);
			} else {
				throw ex;
			}
		}
		returnAST = type_AST;
	}
	
	public final void typeContext(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeContext_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONS_ID:
			{
				classContext(LT(1), "a class context");
				astFactory.addASTChild(currentAST, returnAST);
				typeContext_AST = (AST)currentAST.root;
				break;
			}
			case VAR_ID:
			{
				lacksFieldContext(LT(1), "a lacks constraint");
				astFactory.addASTChild(currentAST, returnAST);
				typeContext_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_42, _tokenSet_43);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_43);
			} else {
				throw ex;
			}
		}
		returnAST = typeContext_AST;
	}
	
	public final void typeContextZeroOrMore(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeContextZeroOrMore_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case CONS_ID:
			case VAR_ID:
			{
				typeContext(LT(1), "a type context");
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop62:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						typeContext(LT(1), "a type context");
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop62;
					}
					
				} while (true);
				}
				break;
			}
			case CLOSE_PAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			typeContextZeroOrMore_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_40, _tokenSet_44);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_44);
			} else {
				throw ex;
			}
		}
		returnAST = typeContextZeroOrMore_AST;
	}
	
	public final void classContextList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classContextList_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		Token  closeParenNode = null;
		AST closeParenNode_AST = null;
		
		try {      // for error handling
			boolean synPredMatched56 = false;
			if (((LA(1)==CONS_ID) && (LA(2)==VAR_ID||LA(2)==DOT))) {
				int _m56 = mark();
				synPredMatched56 = true;
				inputState.guessing++;
				try {
					{
					classContext(null,null);
					match(IMPLIES);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched56 = false;
				}
				rewind(_m56);
inputState.guessing--;
			}
			if ( synPredMatched56 ) {
				classContext(LT(1), "a class context");
				astFactory.addASTChild(currentAST, returnAST);
				match(IMPLIES);
				if ( inputState.guessing==0 ) {
					classContextList_AST = (AST)currentAST.root;
					classContextList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CLASS_CONTEXT_SINGLETON,"CLASS_CONTEXT_SINGLETON")).add(classContextList_AST));
					currentAST.root = classContextList_AST;
					currentAST.child = classContextList_AST!=null &&classContextList_AST.getFirstChild()!=null ?
						classContextList_AST.getFirstChild() : classContextList_AST;
					currentAST.advanceChildToEnd();
				}
				classContextList_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched58 = false;
				if (((LA(1)==OPEN_PAREN))) {
					int _m58 = mark();
					synPredMatched58 = true;
					inputState.guessing++;
					try {
						{
						match(OPEN_PAREN);
						classContextZeroOrMore(null, null);
						match(CLOSE_PAREN);
						match(IMPLIES);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched58 = false;
					}
					rewind(_m58);
inputState.guessing--;
				}
				if ( synPredMatched58 ) {
					openParenNode = LT(1);
					openParenNode_AST = astFactory.create(openParenNode);
					astFactory.makeASTRoot(currentAST, openParenNode_AST);
					match(OPEN_PAREN);
					if ( inputState.guessing==0 ) {
						openParenNode_AST.initialize (CLASS_CONTEXT_LIST, "CLASS_CONTEXT_LIST");
					}
					classContextZeroOrMore(LT(1), "a comma-separated list of class contexts");
					astFactory.addASTChild(currentAST, returnAST);
					closeParenNode = LT(1);
					closeParenNode_AST = astFactory.create(closeParenNode);
					match(CLOSE_PAREN);
					if ( inputState.guessing==0 ) {
						((ParseTreeNode)openParenNode_AST).addOmittedDelimiter(closeParenNode_AST);
					}
					match(IMPLIES);
					classContextList_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==CONS_ID) && (_tokenSet_45.member(LA(2)))) {
					if ( inputState.guessing==0 ) {
						classContextList_AST = (AST)currentAST.root;
						classContextList_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(CLASS_CONTEXT_NOTHING,"CLASS_CONTEXT_NOTHING")));
						currentAST.root = classContextList_AST;
						currentAST.child = classContextList_AST!=null &&classContextList_AST.getFirstChild()!=null ?
							classContextList_AST.getFirstChild() : classContextList_AST;
						currentAST.advanceChildToEnd();
					}
					classContextList_AST = (AST)currentAST.root;
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (NoViableAltException ex) {
				if (inputState.guessing==0) {
					reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_46, _tokenSet_17);
				} else {
					throw ex;
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex, _tokenSet_17);
				} else {
					throw ex;
				}
			}
			returnAST = classContextList_AST;
		}
		
	public final void classContext(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classContext_AST = null;
		
		try {      // for error handling
			qualifiedCons(LT(1), "a class name");
			astFactory.addASTChild(currentAST, returnAST);
			var();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				classContext_AST = (AST)currentAST.root;
				classContext_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CLASS_CONTEXT,"CLASS_CONTEXT")).add(classContext_AST));
				currentAST.root = classContext_AST;
				currentAST.child = classContext_AST!=null &&classContext_AST.getFirstChild()!=null ?
					classContext_AST.getFirstChild() : classContext_AST;
				currentAST.advanceChildToEnd();
			}
			classContext_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_43);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_43);
			} else {
				throw ex;
			}
		}
		returnAST = classContext_AST;
	}
	
	public final void classContextZeroOrMore(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classContextZeroOrMore_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case CONS_ID:
			{
				classContext(LT(1), "a class context");
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop66:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						classContext(LT(1), "a class context");
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop66;
					}
					
				} while (true);
				}
				break;
			}
			case CLOSE_PAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			classContextZeroOrMore_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_47, _tokenSet_44);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_44);
			} else {
				throw ex;
			}
		}
		returnAST = classContextZeroOrMore_AST;
	}
	
	public final void lacksFieldContext(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lacksFieldContext_AST = null;
		Token  backslash = null;
		AST backslash_AST = null;
		
		try {      // for error handling
			var();
			astFactory.addASTChild(currentAST, returnAST);
			backslash = LT(1);
			backslash_AST = astFactory.create(backslash);
			astFactory.makeASTRoot(currentAST, backslash_AST);
			match(BACKSLASH);
			if ( inputState.guessing==0 ) {
				backslash_AST.initialize (LACKS_FIELD_CONTEXT, "LACKS_FIELD_CONTEXT");
			}
			fieldName(LT(1), "a valid record-field name");
			astFactory.addASTChild(currentAST, returnAST);
			lacksFieldContext_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_43);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_43);
			} else {
				throw ex;
			}
		}
		returnAST = lacksFieldContext_AST;
	}
	
	public final void qualifiedCons(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST qualifiedCons_AST = null;
		
		try {      // for error handling
			boolean synPredMatched231 = false;
			if (((LA(1)==CONS_ID) && (LA(2)==DOT))) {
				int _m231 = mark();
				synPredMatched231 = true;
				inputState.guessing++;
				try {
					{
					cons();
					match(DOT);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched231 = false;
				}
				rewind(_m231);
inputState.guessing--;
			}
			if ( synPredMatched231 ) {
				qualifiedCons_qualified(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				qualifiedCons_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==CONS_ID) && (_tokenSet_48.member(LA(2)))) {
				qualifiedCons_unqualified(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				qualifiedCons_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_48);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_48);
			} else {
				throw ex;
			}
		}
		returnAST = qualifiedCons_AST;
	}
	
	public final void fieldName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldName_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case VAR_ID:
			{
				var();
				astFactory.addASTChild(currentAST, returnAST);
				fieldName_AST = (AST)currentAST.root;
				break;
			}
			case ORDINAL_FIELD_NAME:
			{
				AST tmp58_AST = null;
				tmp58_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp58_AST);
				match(ORDINAL_FIELD_NAME);
				fieldName_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_49, _tokenSet_50);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_50);
			} else {
				throw ex;
			}
		}
		returnAST = fieldName_AST;
	}
	
	public final void typeApplication(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeApplication_AST = null;
		
		try {      // for error handling
			{
			int _cnt74=0;
			_loop74:
			do {
				if ((_tokenSet_3.member(LA(1)))) {
					atomicType(LT(1), "an atomic type");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt74>=1 ) { break _loop74; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt74++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				typeApplication_AST = (AST)currentAST.root;
				typeApplication_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE_APPLICATION,"@")).add(typeApplication_AST));
				currentAST.root = typeApplication_AST;
				currentAST.child = typeApplication_AST!=null &&typeApplication_AST.getFirstChild()!=null ?
					typeApplication_AST.getFirstChild() : typeApplication_AST;
				currentAST.advanceChildToEnd();
			}
			typeApplication_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_3, _tokenSet_51);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_51);
			} else {
				throw ex;
			}
		}
		returnAST = typeApplication_AST;
	}
	
	public final void atomicType(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atomicType_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		Token  closeParenNode = null;
		AST closeParenNode_AST = null;
		Token  openBracketNode = null;
		AST openBracketNode_AST = null;
		Token  closeBracketNode = null;
		AST closeBracketNode_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONS_ID:
			{
				qualifiedCons(LT(1), "a type constructor");
				astFactory.addASTChild(currentAST, returnAST);
				atomicType_AST = (AST)currentAST.root;
				break;
			}
			case VAR_ID:
			{
				var();
				astFactory.addASTChild(currentAST, returnAST);
				atomicType_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_PAREN:
			{
				openParenNode = LT(1);
				openParenNode_AST = astFactory.create(openParenNode);
				astFactory.makeASTRoot(currentAST, openParenNode_AST);
				match(OPEN_PAREN);
				if ( inputState.guessing==0 ) {
					openParenNode_AST.initialize (TUPLE_TYPE_CONSTRUCTOR, "Tuple");
				}
				typeListZeroOrMore(LT(1), "a comma-separated list of component types");
				astFactory.addASTChild(currentAST, returnAST);
				closeParenNode = LT(1);
				closeParenNode_AST = astFactory.create(closeParenNode);
				match(CLOSE_PAREN);
				if ( inputState.guessing==0 ) {
					((ParseTreeNode)openParenNode_AST).addOmittedDelimiter(closeParenNode_AST);
				}
				atomicType_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACKET:
			{
				openBracketNode = LT(1);
				openBracketNode_AST = astFactory.create(openBracketNode);
				astFactory.makeASTRoot(currentAST, openBracketNode_AST);
				match(OPEN_BRACKET);
				if ( inputState.guessing==0 ) {
					openBracketNode_AST.initialize (LIST_TYPE_CONSTRUCTOR, "List");
				}
				type(LT(1), "an element type");
				astFactory.addASTChild(currentAST, returnAST);
				closeBracketNode = LT(1);
				closeBracketNode_AST = astFactory.create(closeBracketNode);
				match(CLOSE_BRACKET);
				if ( inputState.guessing==0 ) {
					((ParseTreeNode)openBracketNode_AST).addOmittedDelimiter(closeBracketNode_AST);
				}
				atomicType_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACE:
			{
				recordType(LT(1), "a record type");
				astFactory.addASTChild(currentAST, returnAST);
				atomicType_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_3, _tokenSet_52);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_52);
			} else {
				throw ex;
			}
		}
		returnAST = atomicType_AST;
	}
	
	public final void typeListZeroOrMore(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeListZeroOrMore_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case CONS_ID:
			case VAR_ID:
			case OPEN_PAREN:
			case OPEN_BRACKET:
			case OPEN_BRACE:
			{
				type(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop92:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						type(contextToken, paraphrase);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop92;
					}
					
				} while (true);
				}
				break;
			}
			case CLOSE_PAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			typeListZeroOrMore_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_53, _tokenSet_44);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_44);
			} else {
				throw ex;
			}
		}
		returnAST = typeListZeroOrMore_AST;
	}
	
	public final void recordType(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST recordType_AST = null;
		Token  openBraceNode = null;
		AST openBraceNode_AST = null;
		Token  closeBraceNode = null;
		AST closeBraceNode_AST = null;
		
		try {      // for error handling
			openBraceNode = LT(1);
			openBraceNode_AST = astFactory.create(openBraceNode);
			astFactory.makeASTRoot(currentAST, openBraceNode_AST);
			match(OPEN_BRACE);
			if ( inputState.guessing==0 ) {
				openBraceNode_AST.initialize (RECORD_TYPE_CONSTRUCTOR, "Record");
			}
			recordVar(LT(1), "a base-record type variable");
			astFactory.addASTChild(currentAST, returnAST);
			fieldTypeAssignmentList(LT(1), "a comma-separated list of field type assignments");
			astFactory.addASTChild(currentAST, returnAST);
			closeBraceNode = LT(1);
			closeBraceNode_AST = astFactory.create(closeBraceNode);
			match(CLOSE_BRACE);
			if ( inputState.guessing==0 ) {
				((ParseTreeNode)openBraceNode_AST).addOmittedDelimiter(closeBraceNode_AST);
			}
			recordType_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_54, _tokenSet_52);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_52);
			} else {
				throw ex;
			}
		}
		returnAST = recordType_AST;
	}
	
	public final void recordVar(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST recordVar_AST = null;
		
		try {      // for error handling
			boolean synPredMatched79 = false;
			if (((LA(1)==VAR_ID) && (LA(2)==BAR))) {
				int _m79 = mark();
				synPredMatched79 = true;
				inputState.guessing++;
				try {
					{
					var();
					match(BAR);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched79 = false;
				}
				rewind(_m79);
inputState.guessing--;
			}
			if ( synPredMatched79 ) {
				{
				var();
				astFactory.addASTChild(currentAST, returnAST);
				match(BAR);
				}
				if ( inputState.guessing==0 ) {
					recordVar_AST = (AST)currentAST.root;
					recordVar_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RECORD_VAR,"RECORD_VAR")).add(recordVar_AST));
					currentAST.root = recordVar_AST;
					currentAST.child = recordVar_AST!=null &&recordVar_AST.getFirstChild()!=null ?
						recordVar_AST.getFirstChild() : recordVar_AST;
					currentAST.advanceChildToEnd();
				}
				recordVar_AST = (AST)currentAST.root;
			}
			else {
				boolean synPredMatched82 = false;
				if (((LA(1)==VAR_ID) && (_tokenSet_55.member(LA(2))))) {
					int _m82 = mark();
					synPredMatched82 = true;
					inputState.guessing++;
					try {
						{
						var();
						match(CLOSE_BRACE);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched82 = false;
					}
					rewind(_m82);
inputState.guessing--;
				}
				if ( synPredMatched82 ) {
					{
					var();
					astFactory.addASTChild(currentAST, returnAST);
					}
					if ( inputState.guessing==0 ) {
						recordVar_AST = (AST)currentAST.root;
						recordVar_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RECORD_VAR,"RECORD_VAR")).add(recordVar_AST));
						currentAST.root = recordVar_AST;
						currentAST.child = recordVar_AST!=null &&recordVar_AST.getFirstChild()!=null ?
							recordVar_AST.getFirstChild() : recordVar_AST;
						currentAST.advanceChildToEnd();
					}
					recordVar_AST = (AST)currentAST.root;
				}
				else if ((_tokenSet_55.member(LA(1))) && (_tokenSet_56.member(LA(2)))) {
					if ( inputState.guessing==0 ) {
						recordVar_AST = (AST)currentAST.root;
						recordVar_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(RECORD_VAR,"RECORD_VAR")));
						currentAST.root = recordVar_AST;
						currentAST.child = recordVar_AST!=null &&recordVar_AST.getFirstChild()!=null ?
							recordVar_AST.getFirstChild() : recordVar_AST;
						currentAST.advanceChildToEnd();
					}
					recordVar_AST = (AST)currentAST.root;
				}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
			catch (NoViableAltException ex) {
				if (inputState.guessing==0) {
					reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_55, _tokenSet_55);
				} else {
					throw ex;
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex, _tokenSet_55);
				} else {
					throw ex;
				}
			}
			returnAST = recordVar_AST;
		}
		
	public final void fieldTypeAssignmentList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldTypeAssignmentList_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case VAR_ID:
			case ORDINAL_FIELD_NAME:
			{
				fieldTypeAssignment(LT(1), "a field type assignment");
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop87:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						fieldTypeAssignment(LT(1), "a field type assignment");
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop87;
					}
					
				} while (true);
				}
				break;
			}
			case CLOSE_BRACE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				fieldTypeAssignmentList_AST = (AST)currentAST.root;
				fieldTypeAssignmentList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FIELD_TYPE_ASSIGNMENT_LIST,"FIELD_TYPE_ASSIGNMENT_LIST")).add(fieldTypeAssignmentList_AST));
				currentAST.root = fieldTypeAssignmentList_AST;
				currentAST.child = fieldTypeAssignmentList_AST!=null &&fieldTypeAssignmentList_AST.getFirstChild()!=null ?
					fieldTypeAssignmentList_AST.getFirstChild() : fieldTypeAssignmentList_AST;
				currentAST.advanceChildToEnd();
			}
			fieldTypeAssignmentList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_55, _tokenSet_57);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_57);
			} else {
				throw ex;
			}
		}
		returnAST = fieldTypeAssignmentList_AST;
	}
	
	public final void fieldTypeAssignment(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldTypeAssignment_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			fieldName(LT(1), "a valid record-field name");
			astFactory.addASTChild(currentAST, returnAST);
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(COLONCOLON);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (FIELD_TYPE_ASSIGNMENT, "FIELD_TYPE_ASSIGNMENT");
			}
			type(LT(1), "a field type");
			astFactory.addASTChild(currentAST, returnAST);
			fieldTypeAssignment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_49, _tokenSet_58);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_58);
			} else {
				throw ex;
			}
		}
		returnAST = fieldTypeAssignment_AST;
	}
	
	public final void foreignDataDeclaration(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST foreignDataDeclaration_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			foreignDataDeclarationWithOptionalDocComment(doc_AST, contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			foreignDataDeclaration_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_59, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = foreignDataDeclaration_AST;
	}
	
	public final void externalName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST externalName_AST = null;
		
		try {      // for error handling
			AST tmp62_AST = null;
			tmp62_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp62_AST);
			match(STRING_LITERAL);
			externalName_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_60);
			} else {
			  throw ex;
			}
		}
		returnAST = externalName_AST;
	}
	
	public final void maybeDerivingClause(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST maybeDerivingClause_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_deriving:
			{
				derivingClause(LT(1), "a deriving clause");
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			maybeDerivingClause_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_61, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = maybeDerivingClause_AST;
	}
	
	public final void dataDeclaration(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataDeclaration_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			dataDeclarationWithOptionalDocComment(doc_AST, contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			dataDeclaration_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_59, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = dataDeclaration_AST;
	}
	
	public final void simpleType(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST simpleType_AST = null;
		
		try {      // for error handling
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			typeConsName(LT(1), "a type constructor name");
			astFactory.addASTChild(currentAST, returnAST);
			typeConsParamList(LT(1), "a list of type constructor parameters");
			astFactory.addASTChild(currentAST, returnAST);
			simpleType_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_28, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		returnAST = simpleType_AST;
	}
	
	public final void dataDeclarationBody(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataDeclarationBody_AST = null;
		
		try {      // for error handling
			dataConstructorDefnList(LT(1), "a bar-separated list of data constructor definitions");
			astFactory.addASTChild(currentAST, returnAST);
			maybeDerivingClause(LT(1), "a bar, a deriving clause, or a semicolon");
			astFactory.addASTChild(currentAST, returnAST);
			dataDeclarationBody_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_63, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = dataDeclarationBody_AST;
	}
	
	public final void dataConstructorDefnList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataConstructorDefnList_AST = null;
		
		try {      // for error handling
			dataConstructorDefn(LT(1), "a data constructor definition");
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop107:
			do {
				if ((LA(1)==BAR)) {
					match(BAR);
					dataConstructorDefn(LT(1), "a data constructor definition");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop107;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				dataConstructorDefnList_AST = (AST)currentAST.root;
				dataConstructorDefnList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DATA_CONSTRUCTOR_DEFN_LIST,"DATA_CONSTRUCTOR_DEFN_LIST")).add(dataConstructorDefnList_AST));
				currentAST.root = dataConstructorDefnList_AST;
				currentAST.child = dataConstructorDefnList_AST!=null &&dataConstructorDefnList_AST.getFirstChild()!=null ?
					dataConstructorDefnList_AST.getFirstChild() : dataConstructorDefnList_AST;
				currentAST.advanceChildToEnd();
			}
			dataConstructorDefnList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_63, _tokenSet_61);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_61);
			} else {
				throw ex;
			}
		}
		returnAST = dataConstructorDefnList_AST;
	}
	
	public final void derivingClause(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST derivingClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp64_AST = null;
			tmp64_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp64_AST);
			match(LITERAL_deriving);
			qualifiedCons(LT(1), "a type class name");
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop118:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					qualifiedCons(LT(1), "a type class name");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop118;
				}
				
			} while (true);
			}
			}
			derivingClause_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_64, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = derivingClause_AST;
	}
	
	public final void typeConsName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeConsName_AST = null;
		
		try {      // for error handling
			cons();
			astFactory.addASTChild(currentAST, returnAST);
			typeConsName_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_65);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_65);
			} else {
				throw ex;
			}
		}
		returnAST = typeConsName_AST;
	}
	
	public final void typeConsParamList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeConsParamList_AST = null;
		
		try {      // for error handling
			{
			_loop104:
			do {
				if ((LA(1)==VAR_ID)) {
					var();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop104;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				typeConsParamList_AST = (AST)currentAST.root;
				typeConsParamList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(TYPE_CONS_PARAM_LIST,"TYPE_CONS_PARAM_LIST")).add(typeConsParamList_AST));
				currentAST.root = typeConsParamList_AST;
				currentAST.child = typeConsParamList_AST!=null &&typeConsParamList_AST.getFirstChild()!=null ?
					typeConsParamList_AST.getFirstChild() : typeConsParamList_AST;
				currentAST.advanceChildToEnd();
			}
			typeConsParamList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_65, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		returnAST = typeConsParamList_AST;
	}
	
	public final void dataConstructorDefn(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataConstructorDefn_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			astFactory.addASTChild(currentAST, returnAST);
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			cons();
			astFactory.addASTChild(currentAST, returnAST);
			dataConstructorArgList(LT(1), "a list of data constructor arguments");
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				dataConstructorDefn_AST = (AST)currentAST.root;
				dataConstructorDefn_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DATA_CONSTRUCTOR_DEFN,"DATA_CONSTRUCTOR_DEFN")).add(dataConstructorDefn_AST));
				currentAST.root = dataConstructorDefn_AST;
				currentAST.child = dataConstructorDefn_AST!=null &&dataConstructorDefn_AST.getFirstChild()!=null ?
					dataConstructorDefn_AST.getFirstChild() : dataConstructorDefn_AST;
				currentAST.advanceChildToEnd();
			}
			dataConstructorDefn_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_63, _tokenSet_66);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_66);
			} else {
				throw ex;
			}
		}
		returnAST = dataConstructorDefn_AST;
	}
	
	public final void dataConstructorArgList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataConstructorArgList_AST = null;
		
		try {      // for error handling
			{
			_loop111:
			do {
				if ((LA(1)==VAR_ID||LA(1)==ORDINAL_FIELD_NAME)) {
					dataConstructorArg(LT(1), "a data constructor argument");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop111;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				dataConstructorArgList_AST = (AST)currentAST.root;
				dataConstructorArgList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DATA_CONSTRUCTOR_ARG_LIST,"DATA_CONSTRUCTOR_ARG_LIST")).add(dataConstructorArgList_AST));
				currentAST.root = dataConstructorArgList_AST;
				currentAST.child = dataConstructorArgList_AST!=null &&dataConstructorArgList_AST.getFirstChild()!=null ?
					dataConstructorArgList_AST.getFirstChild() : dataConstructorArgList_AST;
				currentAST.advanceChildToEnd();
			}
			dataConstructorArgList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_67, _tokenSet_66);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_66);
			} else {
				throw ex;
			}
		}
		returnAST = dataConstructorArgList_AST;
	}
	
	public final void dataConstructorArg(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataConstructorArg_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			fieldName(LT(1), "a valid argument name");
			astFactory.addASTChild(currentAST, returnAST);
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(COLONCOLON);
			if ( inputState.guessing==0 ) {
				node_AST.setType (DATA_CONSTRUCTOR_NAMED_ARG);
			}
			dataConstructorArgType(LT(1), "a type for the data constructor argument");
			astFactory.addASTChild(currentAST, returnAST);
			dataConstructorArg_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_49, _tokenSet_67);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_67);
			} else {
				throw ex;
			}
		}
		returnAST = dataConstructorArg_AST;
	}
	
	public final void dataConstructorArgType(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataConstructorArgType_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case PLING:
			{
				{
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(PLING);
				if ( inputState.guessing==0 ) {
					{node_AST.initialize (STRICT_ARG, "!");}
				}
				atomicType(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				}
				dataConstructorArgType_AST = (AST)currentAST.root;
				break;
			}
			case CONS_ID:
			case VAR_ID:
			case OPEN_PAREN:
			case OPEN_BRACKET:
			case OPEN_BRACE:
			{
				atomicType(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				dataConstructorArgType_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_68, _tokenSet_67);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_67);
			} else {
				throw ex;
			}
		}
		returnAST = dataConstructorArgType_AST;
	}
	
	public final void typeClassDefn(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeClassDefn_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			typeClassDefnWithOptionalDocComment(doc_AST, contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			typeClassDefn_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_69, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = typeClassDefn_AST;
	}
	
	public final void typeClassName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeClassName_AST = null;
		
		try {      // for error handling
			cons();
			astFactory.addASTChild(currentAST, returnAST);
			typeClassName_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_15);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_15);
			} else {
				throw ex;
			}
		}
		returnAST = typeClassName_AST;
	}
	
	public final void typeClassParam(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeClassParam_AST = null;
		
		try {      // for error handling
			var();
			astFactory.addASTChild(currentAST, returnAST);
			typeClassParam_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_70);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_70);
			} else {
				throw ex;
			}
		}
		returnAST = typeClassParam_AST;
	}
	
	public final void classMethodList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classMethodList_AST = null;
		
		try {      // for error handling
			{
			_loop125:
			do {
				if ((_tokenSet_71.member(LA(1)))) {
					classMethod(LT(1), "a class method definition");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop125;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				classMethodList_AST = (AST)currentAST.root;
				classMethodList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CLASS_METHOD_LIST,"CLASS_METHOD_LIST")).add(classMethodList_AST));
				currentAST.root = classMethodList_AST;
				currentAST.child = classMethodList_AST!=null &&classMethodList_AST.getFirstChild()!=null ?
					classMethodList_AST.getFirstChild() : classMethodList_AST;
				currentAST.advanceChildToEnd();
			}
			classMethodList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_72, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = classMethodList_AST;
	}
	
	public final void classMethod(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classMethod_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			astFactory.addASTChild(currentAST, returnAST);
			accessModifier(LT(1), "an access modifier");
			astFactory.addASTChild(currentAST, returnAST);
			classMethodName(LT(1), "a class method name");
			astFactory.addASTChild(currentAST, returnAST);
			match(COLONCOLON);
			typeSignature(LT(1), "a type signature for a method");
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case LITERAL_default:
			{
				match(LITERAL_default);
				defaultClassMethodName(LT(1), "a default class method name");
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SEMICOLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				classMethod_AST = (AST)currentAST.root;
				classMethod_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CLASS_METHOD,"CLASS_METHOD")).add(classMethod_AST));
				currentAST.root = classMethod_AST;
				currentAST.child = classMethod_AST!=null &&classMethod_AST.getFirstChild()!=null ?
					classMethod_AST.getFirstChild() : classMethod_AST;
				currentAST.advanceChildToEnd();
			}
			classMethod_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_71, _tokenSet_72);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_72);
			} else {
				throw ex;
			}
		}
		returnAST = classMethod_AST;
	}
	
	public final void classMethodName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classMethodName_AST = null;
		
		try {      // for error handling
			var();
			astFactory.addASTChild(currentAST, returnAST);
			classMethodName_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_73);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_73);
			} else {
				throw ex;
			}
		}
		returnAST = classMethodName_AST;
	}
	
	public final void defaultClassMethodName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST defaultClassMethodName_AST = null;
		
		try {      // for error handling
			qualifiedVar(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			defaultClassMethodName_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_42, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = defaultClassMethodName_AST;
	}
	
	public final void qualifiedVar(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST qualifiedVar_AST = null;
		Token  qualifiedVarNode = null;
		AST qualifiedVarNode_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case VAR_ID:
			{
				var();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					qualifiedVar_AST = (AST)currentAST.root;
					qualifiedVar_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(QUALIFIED_VAR,"QUALIFIED_VAR")).add(astFactory.create(HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER,"HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER")).add(qualifiedVar_AST));
					currentAST.root = qualifiedVar_AST;
					currentAST.child = qualifiedVar_AST!=null &&qualifiedVar_AST.getFirstChild()!=null ?
						qualifiedVar_AST.getFirstChild() : qualifiedVar_AST;
					currentAST.advanceChildToEnd();
				}
				qualifiedVar_AST = (AST)currentAST.root;
				break;
			}
			case CONS_ID:
			{
				moduleName(LT(1), "a valid module name");
				astFactory.addASTChild(currentAST, returnAST);
				qualifiedVarNode = LT(1);
				qualifiedVarNode_AST = astFactory.create(qualifiedVarNode);
				astFactory.makeASTRoot(currentAST, qualifiedVarNode_AST);
				match(DOT);
				if ( inputState.guessing==0 ) {
					qualifiedVarNode_AST.initialize (QUALIFIED_VAR, "QUALIFIED_VAR");
				}
				var();
				astFactory.addASTChild(currentAST, returnAST);
				qualifiedVar_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_42, _tokenSet_74);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_74);
			} else {
				throw ex;
			}
		}
		returnAST = qualifiedVar_AST;
	}
	
	public final void instanceDefn(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instanceDefn_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			instanceDefnWithOptionalDocComment(doc_AST, contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			instanceDefn_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_75, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = instanceDefn_AST;
	}
	
	public final void instanceName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instanceName_AST = null;
		
		try {      // for error handling
			classContextList(LT(1), "a class context");
			astFactory.addASTChild(currentAST, returnAST);
			qualifiedCons(LT(1), "a class name");
			astFactory.addASTChild(currentAST, returnAST);
			instanceTypeConsName(LT(1), "a type constructor name");
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				instanceName_AST = (AST)currentAST.root;
				instanceName_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTANCE_NAME,"INSTANCE_NAME")).add(instanceName_AST));
				currentAST.root = instanceName_AST;
				currentAST.child = instanceName_AST!=null &&instanceName_AST.getFirstChild()!=null ?
					instanceName_AST.getFirstChild() : instanceName_AST;
				currentAST.advanceChildToEnd();
			}
			instanceName_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_46, _tokenSet_70);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_70);
			} else {
				throw ex;
			}
		}
		returnAST = instanceName_AST;
	}
	
	public final void instanceMethodList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instanceMethodList_AST = null;
		
		try {      // for error handling
			{
			_loop142:
			do {
				if ((LA(1)==CALDOC_OPEN||LA(1)==VAR_ID)) {
					instanceMethod(LT(1), "an instance method definition");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop142;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				instanceMethodList_AST = (AST)currentAST.root;
				instanceMethodList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INSTANCE_METHOD_LIST,"INSTANCE_METHOD_LIST")).add(instanceMethodList_AST));
				currentAST.root = instanceMethodList_AST;
				currentAST.child = instanceMethodList_AST!=null &&instanceMethodList_AST.getFirstChild()!=null ?
					instanceMethodList_AST.getFirstChild() : instanceMethodList_AST;
				currentAST.advanceChildToEnd();
			}
			instanceMethodList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_76, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = instanceMethodList_AST;
	}
	
	public final void instanceTypeConsName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instanceTypeConsName_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		Token  closeParenNode = null;
		AST closeParenNode_AST = null;
		Token  node = null;
		AST node_AST = null;
		Token  openParenNode2 = null;
		AST openParenNode2_AST = null;
		Token  closeParenNode2 = null;
		AST closeParenNode2_AST = null;
		Token  openBracketNode = null;
		AST openBracketNode_AST = null;
		Token  closeBracketNode = null;
		AST closeBracketNode_AST = null;
		Token  openBraceNode = null;
		AST openBraceNode_AST = null;
		Token  closeBraceNode = null;
		AST closeBraceNode_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONS_ID:
			{
				qualifiedCons(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					instanceTypeConsName_AST = (AST)currentAST.root;
					instanceTypeConsName_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(UNPARENTHESIZED_TYPE_CONSTRUCTOR,"UNPARENTHESIZED_TYPE_CONSTRUCTOR")).add(instanceTypeConsName_AST));
					currentAST.root = instanceTypeConsName_AST;
					currentAST.child = instanceTypeConsName_AST!=null &&instanceTypeConsName_AST.getFirstChild()!=null ?
						instanceTypeConsName_AST.getFirstChild() : instanceTypeConsName_AST;
					currentAST.advanceChildToEnd();
				}
				instanceTypeConsName_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACKET:
			{
				openBracketNode = LT(1);
				openBracketNode_AST = astFactory.create(openBracketNode);
				astFactory.makeASTRoot(currentAST, openBracketNode_AST);
				match(OPEN_BRACKET);
				if ( inputState.guessing==0 ) {
					openBracketNode_AST.initialize (LIST_TYPE_CONSTRUCTOR, "List");
				}
				var();
				astFactory.addASTChild(currentAST, returnAST);
				closeBracketNode = LT(1);
				closeBracketNode_AST = astFactory.create(closeBracketNode);
				match(CLOSE_BRACKET);
				if ( inputState.guessing==0 ) {
					((ParseTreeNode)openBracketNode_AST).addOmittedDelimiter(closeBracketNode_AST);
				}
				instanceTypeConsName_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACE:
			{
				openBraceNode = LT(1);
				openBraceNode_AST = astFactory.create(openBraceNode);
				astFactory.makeASTRoot(currentAST, openBraceNode_AST);
				match(OPEN_BRACE);
				if ( inputState.guessing==0 ) {
					openBraceNode_AST.initialize (RECORD_TYPE_CONSTRUCTOR, "Record");
				}
				var();
				astFactory.addASTChild(currentAST, returnAST);
				closeBraceNode = LT(1);
				closeBraceNode_AST = astFactory.create(closeBraceNode);
				match(CLOSE_BRACE);
				if ( inputState.guessing==0 ) {
					((ParseTreeNode)openBraceNode_AST).addOmittedDelimiter(closeBraceNode_AST);
				}
				instanceTypeConsName_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched135 = false;
				if (((LA(1)==OPEN_PAREN) && (LA(2)==CONS_ID))) {
					int _m135 = mark();
					synPredMatched135 = true;
					inputState.guessing++;
					try {
						{
						match(OPEN_PAREN);
						qualifiedCons(null,null);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched135 = false;
					}
					rewind(_m135);
inputState.guessing--;
				}
				if ( synPredMatched135 ) {
					openParenNode = LT(1);
					openParenNode_AST = astFactory.create(openParenNode);
					astFactory.makeASTRoot(currentAST, openParenNode_AST);
					match(OPEN_PAREN);
					if ( inputState.guessing==0 ) {
						openParenNode_AST.initialize (GENERAL_TYPE_CONSTRUCTOR, "GENERAL_TYPE_CONSTRUCTOR");
					}
					qualifiedCons(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop137:
					do {
						if ((LA(1)==VAR_ID)) {
							var();
							astFactory.addASTChild(currentAST, returnAST);
						}
						else {
							break _loop137;
						}
						
					} while (true);
					}
					closeParenNode = LT(1);
					closeParenNode_AST = astFactory.create(closeParenNode);
					match(CLOSE_PAREN);
					if ( inputState.guessing==0 ) {
						((ParseTreeNode)openParenNode_AST).addOmittedDelimiter(closeParenNode_AST);
					}
					instanceTypeConsName_AST = (AST)currentAST.root;
				}
				else {
					boolean synPredMatched139 = false;
					if (((LA(1)==OPEN_PAREN) && (LA(2)==VAR_ID))) {
						int _m139 = mark();
						synPredMatched139 = true;
						inputState.guessing++;
						try {
							{
							match(OPEN_PAREN);
							var();
							match(RARROW);
							}
						}
						catch (RecognitionException pe) {
							synPredMatched139 = false;
						}
						rewind(_m139);
inputState.guessing--;
					}
					if ( synPredMatched139 ) {
						match(OPEN_PAREN);
						var();
						astFactory.addASTChild(currentAST, returnAST);
						node = LT(1);
						node_AST = astFactory.create(node);
						astFactory.makeASTRoot(currentAST, node_AST);
						match(RARROW);
						if ( inputState.guessing==0 ) {
							node_AST.initialize (FUNCTION_TYPE_CONSTRUCTOR, "->");
						}
						var();
						astFactory.addASTChild(currentAST, returnAST);
						match(CLOSE_PAREN);
						instanceTypeConsName_AST = (AST)currentAST.root;
					}
					else if ((LA(1)==OPEN_PAREN) && (LA(2)==CLOSE_PAREN)) {
						openParenNode2 = LT(1);
						openParenNode2_AST = astFactory.create(openParenNode2);
						astFactory.makeASTRoot(currentAST, openParenNode2_AST);
						match(OPEN_PAREN);
						if ( inputState.guessing==0 ) {
							openParenNode2_AST.initialize (UNIT_TYPE_CONSTRUCTOR, "Unit");
						}
						closeParenNode2 = LT(1);
						closeParenNode2_AST = astFactory.create(closeParenNode2);
						match(CLOSE_PAREN);
						if ( inputState.guessing==0 ) {
							((ParseTreeNode)openParenNode2_AST).addOmittedDelimiter(closeParenNode2_AST);
						}
						instanceTypeConsName_AST = (AST)currentAST.root;
					}
				else {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}}
			}
			catch (NoViableAltException ex) {
				if (inputState.guessing==0) {
					reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_77, _tokenSet_70);
				} else {
					throw ex;
				}
			}
			catch (RecognitionException ex) {
				if (inputState.guessing==0) {
					reportError(ex, _tokenSet_70);
				} else {
					throw ex;
				}
			}
			returnAST = instanceTypeConsName_AST;
		}
		
	public final void instanceMethod(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instanceMethod_AST = null;
		AST doc_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			instanceMethodName(LT(1), "a method name");
			astFactory.addASTChild(currentAST, returnAST);
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(EQUALS);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (INSTANCE_METHOD, "INSTANCE_METHOD");
			}
			instanceMethodDefn(LT(1), "a function or method name");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			instanceMethod_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_36, _tokenSet_76);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_76);
			} else {
				throw ex;
			}
		}
		returnAST = instanceMethod_AST;
	}
	
	public final void instanceMethodName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instanceMethodName_AST = null;
		
		try {      // for error handling
			var();
			astFactory.addASTChild(currentAST, returnAST);
			instanceMethodName_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		returnAST = instanceMethodName_AST;
	}
	
	public final void instanceMethodDefn(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST instanceMethodDefn_AST = null;
		
		try {      // for error handling
			qualifiedVar(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			instanceMethodDefn_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_42, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_21);
			} else {
				throw ex;
			}
		}
		returnAST = instanceMethodDefn_AST;
	}
	
	public final void foreignFunctionDeclaration(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST foreignFunctionDeclaration_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			foreignFunctionDeclarationWithOptionalDocComment(doc_AST, contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			foreignFunctionDeclaration_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_78, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = foreignFunctionDeclaration_AST;
	}
	
	public final void primitiveFunctionDeclaration(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primitiveFunctionDeclaration_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			primitiveFunctionDeclarationWithOptionalDocComment(doc_AST, contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			primitiveFunctionDeclaration_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_79, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = primitiveFunctionDeclaration_AST;
	}
	
	public final void topLevelFunction(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST topLevelFunction_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			topLevelFunctionWithOptionalDocComment(doc_AST, contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			topLevelFunction_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_71, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_0);
			} else {
				throw ex;
			}
		}
		returnAST = topLevelFunction_AST;
	}
	
	public final void functionName(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionName_AST = null;
		
		try {      // for error handling
			var();
			astFactory.addASTChild(currentAST, returnAST);
			functionName_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_14);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_14);
			} else {
				throw ex;
			}
		}
		returnAST = functionName_AST;
	}
	
	public final void functionParamList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionParamList_AST = null;
		
		try {      // for error handling
			{
			_loop158:
			do {
				if ((LA(1)==VAR_ID||LA(1)==PLING)) {
					functionParam(LT(1), "a function parameter name");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop158;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				functionParamList_AST = (AST)currentAST.root;
				functionParamList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FUNCTION_PARAM_LIST,"FUNCTION_PARAM_LIST")).add(functionParamList_AST));
				currentAST.root = functionParamList_AST;
				currentAST.child = functionParamList_AST!=null &&functionParamList_AST.getFirstChild()!=null ?
					functionParamList_AST.getFirstChild() : functionParamList_AST;
				currentAST.advanceChildToEnd();
			}
			functionParamList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_14, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		returnAST = functionParamList_AST;
	}
	
	public final void functionParam(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionParam_AST = null;
		Token  node1 = null;
		AST node1_AST = null;
		Token  node2 = null;
		AST node2_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case VAR_ID:
			{
				node1 = LT(1);
				node1_AST = astFactory.create(node1);
				astFactory.addASTChild(currentAST, node1_AST);
				match(VAR_ID);
				if ( inputState.guessing==0 ) {
					node1_AST.setType (LAZY_PARAM);
				}
				functionParam_AST = (AST)currentAST.root;
				break;
			}
			case PLING:
			{
				match(PLING);
				node2 = LT(1);
				node2_AST = astFactory.create(node2);
				astFactory.addASTChild(currentAST, node2_AST);
				match(VAR_ID);
				if ( inputState.guessing==0 ) {
					node2_AST.setType (STRICT_PARAM);
				}
				functionParam_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_80, _tokenSet_81);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_81);
			} else {
				throw ex;
			}
		}
		returnAST = functionParam_AST;
	}
	
	public final void lambdaParamList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lambdaParamList_AST = null;
		
		try {      // for error handling
			{
			int _cnt162=0;
			_loop162:
			do {
				if ((LA(1)==VAR_ID||LA(1)==PLING)) {
					functionParam(LT(1), "a lambda parameter name");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt162>=1 ) { break _loop162; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt162++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				lambdaParamList_AST = (AST)currentAST.root;
				lambdaParamList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FUNCTION_PARAM_LIST,"FUNCTION_PARAM_LIST")).add(lambdaParamList_AST));
				currentAST.root = lambdaParamList_AST;
				currentAST.child = lambdaParamList_AST!=null &&lambdaParamList_AST.getFirstChild()!=null ?
					lambdaParamList_AST.getFirstChild() : lambdaParamList_AST;
				currentAST.advanceChildToEnd();
			}
			lambdaParamList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_80, _tokenSet_82);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_82);
			} else {
				throw ex;
			}
		}
		returnAST = lambdaParamList_AST;
	}
	
	public final void letDefnList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST letDefnList_AST = null;
		
		try {      // for error handling
			{
			int _cnt238=0;
			_loop238:
			do {
				if ((_tokenSet_83.member(LA(1)))) {
					letDefnOrTypeDeclarationOrPatternMatchDecl(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt238>=1 ) { break _loop238; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt238++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				letDefnList_AST = (AST)currentAST.root;
				letDefnList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(LET_DEFN_LIST,"LET_DEFN_LIST")).add(letDefnList_AST));
				currentAST.root = letDefnList_AST;
				currentAST.child = letDefnList_AST!=null &&letDefnList_AST.getFirstChild()!=null ?
					letDefnList_AST.getFirstChild() : letDefnList_AST;
				currentAST.advanceChildToEnd();
			}
			letDefnList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_83, _tokenSet_84);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_84);
			} else {
				throw ex;
			}
		}
		returnAST = letDefnList_AST;
	}
	
	public final void altList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST altList_AST = null;
		
		try {      // for error handling
			{
			int _cnt252=0;
			_loop252:
			do {
				if ((_tokenSet_85.member(LA(1)))) {
					alt(LT(1), "a case alternative");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt252>=1 ) { break _loop252; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt252++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				altList_AST = (AST)currentAST.root;
				altList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ALT_LIST,"ALT_LIST")).add(altList_AST));
				currentAST.root = altList_AST;
				currentAST.child = altList_AST!=null &&altList_AST.getFirstChild()!=null ?
					altList_AST.getFirstChild() : altList_AST;
				currentAST.advanceChildToEnd();
			}
			altList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_85, _tokenSet_2);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_2);
			} else {
				throw ex;
			}
		}
		returnAST = altList_AST;
	}
	
	public final void typeSignatureExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typeSignatureExpr_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			applyOpExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case COLONCOLON:
			{
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(COLONCOLON);
				if ( inputState.guessing==0 ) {
					node_AST.setType (EXPRESSION_TYPE_SIGNATURE);
				}
				typeSignature(LT(1), "a valid type signature");
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case COMMA:
			case SEMICOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			typeSignatureExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_2);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_2);
			} else {
				throw ex;
			}
		}
		returnAST = typeSignatureExpr_AST;
	}
	
	public final void applyOpExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST applyOpExpr_AST = null;
		
		try {      // for error handling
			orExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case DOLLAR:
			{
				AST tmp73_AST = null;
				tmp73_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp73_AST);
				match(DOLLAR);
				applyOpExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case COMMA:
			case SEMICOLON:
			case COLONCOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			applyOpExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_87);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_87);
			} else {
				throw ex;
			}
		}
		returnAST = applyOpExpr_AST;
	}
	
	public final void orExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orExpr_AST = null;
		
		try {      // for error handling
			andExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case BARBAR:
			{
				AST tmp74_AST = null;
				tmp74_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp74_AST);
				match(BARBAR);
				orExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case COMMA:
			case SEMICOLON:
			case COLONCOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			case DOLLAR:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			orExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_88);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_88);
			} else {
				throw ex;
			}
		}
		returnAST = orExpr_AST;
	}
	
	public final void andExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST andExpr_AST = null;
		
		try {      // for error handling
			relationalExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case AMPERSANDAMPERSAND:
			{
				AST tmp75_AST = null;
				tmp75_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp75_AST);
				match(AMPERSANDAMPERSAND);
				andExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case COMMA:
			case SEMICOLON:
			case COLONCOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			case DOLLAR:
			case BARBAR:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			andExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_89);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_89);
			} else {
				throw ex;
			}
		}
		returnAST = andExpr_AST;
	}
	
	public final void relationalExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpr_AST = null;
		
		try {      // for error handling
			listOpExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case EQUALS:
			case LESS_THAN:
			case LESS_THAN_OR_EQUALS:
			case EQUALSEQUALS:
			case NOT_EQUALS:
			case GREATER_THAN_OR_EQUALS:
			case GREATER_THAN:
			{
				{
				switch ( LA(1)) {
				case LESS_THAN:
				{
					AST tmp76_AST = null;
					tmp76_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp76_AST);
					match(LESS_THAN);
					break;
				}
				case LESS_THAN_OR_EQUALS:
				{
					AST tmp77_AST = null;
					tmp77_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp77_AST);
					match(LESS_THAN_OR_EQUALS);
					break;
				}
				case EQUALSEQUALS:
				{
					AST tmp78_AST = null;
					tmp78_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp78_AST);
					match(EQUALSEQUALS);
					break;
				}
				case EQUALS:
				{
					AST tmp79_AST = null;
					tmp79_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp79_AST);
					match(EQUALS);
					if ( inputState.guessing==0 ) {
						reportWithSuggestion(LT(0), "the equality operator ('==')");
					}
					break;
				}
				case NOT_EQUALS:
				{
					AST tmp80_AST = null;
					tmp80_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp80_AST);
					match(NOT_EQUALS);
					break;
				}
				case GREATER_THAN_OR_EQUALS:
				{
					AST tmp81_AST = null;
					tmp81_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp81_AST);
					match(GREATER_THAN_OR_EQUALS);
					break;
				}
				case GREATER_THAN:
				{
					AST tmp82_AST = null;
					tmp82_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp82_AST);
					match(GREATER_THAN);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				listOpExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case COMMA:
			case SEMICOLON:
			case COLONCOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			case DOLLAR:
			case BARBAR:
			case AMPERSANDAMPERSAND:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			relationalExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_90);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_90);
			} else {
				throw ex;
			}
		}
		returnAST = relationalExpr_AST;
	}
	
	public final void listOpExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST listOpExpr_AST = null;
		
		try {      // for error handling
			addExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case COLON:
			case PLUSPLUS:
			{
				{
				switch ( LA(1)) {
				case COLON:
				{
					AST tmp83_AST = null;
					tmp83_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp83_AST);
					match(COLON);
					break;
				}
				case PLUSPLUS:
				{
					AST tmp84_AST = null;
					tmp84_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp84_AST);
					match(PLUSPLUS);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				listOpExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case EQUALS:
			case COMMA:
			case SEMICOLON:
			case COLONCOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			case DOLLAR:
			case BARBAR:
			case AMPERSANDAMPERSAND:
			case LESS_THAN:
			case LESS_THAN_OR_EQUALS:
			case EQUALSEQUALS:
			case NOT_EQUALS:
			case GREATER_THAN_OR_EQUALS:
			case GREATER_THAN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			listOpExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_91);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_91);
			} else {
				throw ex;
			}
		}
		returnAST = listOpExpr_AST;
	}
	
	public final void addExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST addExpr_AST = null;
		
		try {      // for error handling
			multiplyExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop181:
			do {
				if ((LA(1)==PLUS||LA(1)==MINUS)) {
					{
					switch ( LA(1)) {
					case PLUS:
					{
						AST tmp85_AST = null;
						tmp85_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp85_AST);
						match(PLUS);
						break;
					}
					case MINUS:
					{
						AST tmp86_AST = null;
						tmp86_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp86_AST);
						match(MINUS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					multiplyExpr(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop181;
				}
				
			} while (true);
			}
			addExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_92);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_92);
			} else {
				throw ex;
			}
		}
		returnAST = addExpr_AST;
	}
	
	public final void multiplyExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplyExpr_AST = null;
		
		try {      // for error handling
			unaryExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop185:
			do {
				if (((LA(1) >= ASTERISK && LA(1) <= PERCENT))) {
					{
					switch ( LA(1)) {
					case ASTERISK:
					{
						AST tmp87_AST = null;
						tmp87_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp87_AST);
						match(ASTERISK);
						break;
					}
					case SOLIDUS:
					{
						AST tmp88_AST = null;
						tmp88_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp88_AST);
						match(SOLIDUS);
						break;
					}
					case PERCENT:
					{
						AST tmp89_AST = null;
						tmp89_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp89_AST);
						match(PERCENT);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					unaryExpr(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop185;
				}
				
			} while (true);
			}
			multiplyExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_93);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_93);
			} else {
				throw ex;
			}
		}
		returnAST = multiplyExpr_AST;
	}
	
	public final void unaryExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpr_AST = null;
		Token  minusNode = null;
		AST minusNode_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MINUS:
			{
				minusNode = LT(1);
				minusNode_AST = astFactory.create(minusNode);
				astFactory.makeASTRoot(currentAST, minusNode_AST);
				match(MINUS);
				if ( inputState.guessing==0 ) {
					minusNode_AST.setType(UNARY_MINUS);
				}
				unaryExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpr_AST = (AST)currentAST.root;
				break;
			}
			case CONS_ID:
			case VAR_ID:
			case OPEN_PAREN:
			case OPEN_BRACKET:
			case OPEN_BRACE:
			case STRING_LITERAL:
			case CHAR_LITERAL:
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
			{
				userOpExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_86, _tokenSet_94);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_94);
			} else {
				throw ex;
			}
		}
		returnAST = unaryExpr_AST;
	}
	
	public final void userOpExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST userOpExpr_AST = null;
		AST arg1_AST = null;
		AST opA_AST = null;
		AST arg2A_AST = null;
		Token  opB = null;
		AST opB_AST = null;
		AST arg2B_AST = null;
		AST result = null;
		
		try {      // for error handling
			applicationExpr(contextToken, paraphrase);
			arg1_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				result = arg1_AST;
			}
			{
			switch ( LA(1)) {
			case EOF:
			case EQUALS:
			case COMMA:
			case SEMICOLON:
			case COLONCOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			case DOLLAR:
			case BARBAR:
			case AMPERSANDAMPERSAND:
			case LESS_THAN:
			case LESS_THAN_OR_EQUALS:
			case EQUALSEQUALS:
			case NOT_EQUALS:
			case GREATER_THAN_OR_EQUALS:
			case GREATER_THAN:
			case COLON:
			case PLUSPLUS:
			case PLUS:
			case MINUS:
			case ASTERISK:
			case SOLIDUS:
			case PERCENT:
			case BACKQUOTE:
			{
				{
				_loop190:
				do {
					if ((LA(1)==BACKQUOTE)) {
						backquotedOperator(contextToken, paraphrase);
						opA_AST = (AST)returnAST;
						applicationExpr(contextToken, paraphrase);
						arg2A_AST = (AST)returnAST;
						if ( inputState.guessing==0 ) {
							result = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(BACKQUOTE,"`")).add((AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(APPLICATION,"@")).add(opA_AST).add(result).add(arg2A_AST))));
						}
					}
					else {
						break _loop190;
					}
					
				} while (true);
				}
				break;
			}
			case POUND:
			{
				{
				opB = LT(1);
				opB_AST = astFactory.create(opB);
				match(POUND);
				composeOpExpr(contextToken, paraphrase);
				arg2B_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					result = (AST)astFactory.make( (new ASTArray(3)).add(opB_AST).add(result).add(arg2B_AST));
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				userOpExpr_AST = (AST)currentAST.root;
				userOpExpr_AST = result;
				currentAST.root = userOpExpr_AST;
				currentAST.child = userOpExpr_AST!=null &&userOpExpr_AST.getFirstChild()!=null ?
					userOpExpr_AST.getFirstChild() : userOpExpr_AST;
				currentAST.advanceChildToEnd();
			}
			userOpExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_95, _tokenSet_94);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_94);
			} else {
				throw ex;
			}
		}
		returnAST = userOpExpr_AST;
	}
	
	public final void applicationExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST applicationExpr_AST = null;
		
		try {      // for error handling
			{
			int _cnt200=0;
			_loop200:
			do {
				if ((_tokenSet_95.member(LA(1)))) {
					selectFieldExpr(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt200>=1 ) { break _loop200; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt200++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				applicationExpr_AST = (AST)currentAST.root;
				applicationExpr_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(APPLICATION,"@")).add(applicationExpr_AST));
				currentAST.root = applicationExpr_AST;
				currentAST.child = applicationExpr_AST!=null &&applicationExpr_AST.getFirstChild()!=null ?
					applicationExpr_AST.getFirstChild() : applicationExpr_AST;
				currentAST.advanceChildToEnd();
			}
			applicationExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_95, _tokenSet_96);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_96);
			} else {
				throw ex;
			}
		}
		returnAST = applicationExpr_AST;
	}
	
	public final void backquotedOperator(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST backquotedOperator_AST = null;
		
		try {      // for error handling
			match(BACKQUOTE);
			{
			boolean synPredMatched195 = false;
			if (((LA(1)==CONS_ID||LA(1)==VAR_ID) && (LA(2)==DOT||LA(2)==BACKQUOTE))) {
				int _m195 = mark();
				synPredMatched195 = true;
				inputState.guessing++;
				try {
					{
					switch ( LA(1)) {
					case VAR_ID:
					{
						var();
						break;
					}
					case CONS_ID:
					{
						moduleName(null,null);
						match(DOT);
						var();
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched195 = false;
				}
				rewind(_m195);
inputState.guessing--;
			}
			if ( synPredMatched195 ) {
				qualifiedVar(LT(1), "a function or method name");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==CONS_ID) && (LA(2)==DOT||LA(2)==BACKQUOTE)) {
				qualifiedCons(LT(1), "a data constructor name");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			match(BACKQUOTE);
			backquotedOperator_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_97, _tokenSet_95);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_95);
			} else {
				throw ex;
			}
		}
		returnAST = backquotedOperator_AST;
	}
	
	public final void composeOpExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST composeOpExpr_AST = null;
		
		try {      // for error handling
			applicationExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case POUND:
			{
				AST tmp92_AST = null;
				tmp92_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp92_AST);
				match(POUND);
				composeOpExpr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case EQUALS:
			case COMMA:
			case SEMICOLON:
			case COLONCOLON:
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			case CLOSE_BRACE:
			case BAR:
			case LITERAL_then:
			case LITERAL_else:
			case LITERAL_of:
			case DOLLAR:
			case BARBAR:
			case AMPERSANDAMPERSAND:
			case LESS_THAN:
			case LESS_THAN_OR_EQUALS:
			case EQUALSEQUALS:
			case NOT_EQUALS:
			case GREATER_THAN_OR_EQUALS:
			case GREATER_THAN:
			case COLON:
			case PLUSPLUS:
			case PLUS:
			case MINUS:
			case ASTERISK:
			case SOLIDUS:
			case PERCENT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			composeOpExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_95, _tokenSet_94);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_94);
			} else {
				throw ex;
			}
		}
		returnAST = composeOpExpr_AST;
	}
	
	public final void selectFieldExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectFieldExpr_AST = null;
		Token  selectRecordFieldNode = null;
		AST selectRecordFieldNode_AST = null;
		Token  selectDCFieldNode = null;
		AST selectDCFieldNode_AST = null;
		
		try {      // for error handling
			atomicExpr(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop208:
			do {
				if ((LA(1)==DOT) && (LA(2)==VAR_ID||LA(2)==ORDINAL_FIELD_NAME)) {
					{
					selectRecordFieldNode = LT(1);
					selectRecordFieldNode_AST = astFactory.create(selectRecordFieldNode);
					astFactory.makeASTRoot(currentAST, selectRecordFieldNode_AST);
					match(DOT);
					if ( inputState.guessing==0 ) {
						selectRecordFieldNode_AST.initialize (SELECT_RECORD_FIELD, "SELECT_RECORD_FIELD");
					}
					fieldName(LT(1), "a valid record-field name");
					astFactory.addASTChild(currentAST, returnAST);
					}
				}
				else if ((LA(1)==DOT) && (LA(2)==CONS_ID)) {
					{
					selectDCFieldNode = LT(1);
					selectDCFieldNode_AST = astFactory.create(selectDCFieldNode);
					astFactory.makeASTRoot(currentAST, selectDCFieldNode_AST);
					match(DOT);
					if ( inputState.guessing==0 ) {
						selectDCFieldNode_AST.initialize (SELECT_DATA_CONSTRUCTOR_FIELD, "SELECT_DATA_CONSTRUCTOR_FIELD");
					}
					{
					boolean synPredMatched207 = false;
					if (((LA(1)==CONS_ID) && (LA(2)==DOT))) {
						int _m207 = mark();
						synPredMatched207 = true;
						inputState.guessing++;
						try {
							{
							cons();
							match(DOT);
							cons();
							}
						}
						catch (RecognitionException pe) {
							synPredMatched207 = false;
						}
						rewind(_m207);
inputState.guessing--;
					}
					if ( synPredMatched207 ) {
						qualifiedCons_qualified(LT(1), "a qualified data constructor name");
						astFactory.addASTChild(currentAST, returnAST);
					}
					else if ((LA(1)==CONS_ID) && (LA(2)==DOT)) {
						qualifiedCons_unqualified(LT(1), "a constructor name");
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					
					}
					match(DOT);
					fieldName(LT(1), "a valid field name");
					astFactory.addASTChild(currentAST, returnAST);
					}
				}
				else {
					break _loop208;
				}
				
			} while (true);
			}
			selectFieldExpr_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_95, _tokenSet_98);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_98);
			} else {
				throw ex;
			}
		}
		returnAST = selectFieldExpr_AST;
	}
	
	public final void atomicExpr(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST atomicExpr_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		Token  closeParenNode = null;
		AST closeParenNode_AST = null;
		Token  openBracketNode = null;
		AST openBracketNode_AST = null;
		Token  closeBracketNode = null;
		AST closeBracketNode_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case STRING_LITERAL:
			case CHAR_LITERAL:
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
			{
				literal(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				atomicExpr_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_PAREN:
			{
				openParenNode = LT(1);
				openParenNode_AST = astFactory.create(openParenNode);
				astFactory.makeASTRoot(currentAST, openParenNode_AST);
				match(OPEN_PAREN);
				if ( inputState.guessing==0 ) {
					openParenNode_AST.initialize (TUPLE_CONSTRUCTOR, "Tuple");
				}
				exprListZeroOrMore(LT(1), "a comma-separated list of expressions");
				astFactory.addASTChild(currentAST, returnAST);
				closeParenNode = LT(1);
				closeParenNode_AST = astFactory.create(closeParenNode);
				match(CLOSE_PAREN);
				if ( inputState.guessing==0 ) {
					((ParseTreeNode)openParenNode_AST).addOmittedDelimiter(closeParenNode_AST);
				}
				atomicExpr_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACKET:
			{
				openBracketNode = LT(1);
				openBracketNode_AST = astFactory.create(openBracketNode);
				astFactory.makeASTRoot(currentAST, openBracketNode_AST);
				match(OPEN_BRACKET);
				if ( inputState.guessing==0 ) {
					openBracketNode_AST.initialize (LIST_CONSTRUCTOR, "List");
				}
				exprListZeroOrMore(LT(1), "a comma-separated list of expressions");
				astFactory.addASTChild(currentAST, returnAST);
				closeBracketNode = LT(1);
				closeBracketNode_AST = astFactory.create(closeBracketNode);
				match(CLOSE_BRACKET);
				if ( inputState.guessing==0 ) {
					((ParseTreeNode)openBracketNode_AST).addOmittedDelimiter(closeBracketNode_AST);
				}
				atomicExpr_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACE:
			{
				recordValue(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				atomicExpr_AST = (AST)currentAST.root;
				break;
			}
			default:
				boolean synPredMatched211 = false;
				if (((LA(1)==CONS_ID||LA(1)==VAR_ID) && (_tokenSet_99.member(LA(2))))) {
					int _m211 = mark();
					synPredMatched211 = true;
					inputState.guessing++;
					try {
						{
						switch ( LA(1)) {
						case VAR_ID:
						{
							var();
							break;
						}
						case CONS_ID:
						{
							moduleName(null, null);
							match(DOT);
							var();
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
					}
					catch (RecognitionException pe) {
						synPredMatched211 = false;
					}
					rewind(_m211);
inputState.guessing--;
				}
				if ( synPredMatched211 ) {
					qualifiedVar(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					atomicExpr_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==CONS_ID) && (_tokenSet_99.member(LA(2)))) {
					qualifiedCons(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					atomicExpr_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_95, _tokenSet_99);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_99);
			} else {
				throw ex;
			}
		}
		returnAST = atomicExpr_AST;
	}
	
	public final void qualifiedCons_qualified(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST qualifiedCons_qualified_AST = null;
		AST bareCons_AST = null;
		Token  dot = null;
		AST dot_AST = null;
		
		try {      // for error handling
			cons();
			bareCons_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				qualifiedCons_qualified_AST = (AST)currentAST.root;
				qualifiedCons_qualified_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(HIERARCHICAL_MODULE_NAME,"HIERARCHICAL_MODULE_NAME")).add(astFactory.create(HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER,"HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER")).add(bareCons_AST));
				currentAST.root = qualifiedCons_qualified_AST;
				currentAST.child = qualifiedCons_qualified_AST!=null &&qualifiedCons_qualified_AST.getFirstChild()!=null ?
					qualifiedCons_qualified_AST.getFirstChild() : qualifiedCons_qualified_AST;
				currentAST.advanceChildToEnd();
			}
			{
			int _cnt234=0;
			_loop234:
			do {
				if ((LA(1)==DOT) && (LA(2)==CONS_ID)) {
					dot = LT(1);
					dot_AST = astFactory.create(dot);
					astFactory.makeASTRoot(currentAST, dot_AST);
					match(DOT);
					if ( inputState.guessing==0 ) {
						dot_AST.initialize(HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME");
					}
					cons();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt234>=1 ) { break _loop234; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt234++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				qualifiedCons_qualified_AST = (AST)currentAST.root;
				qualifiedCons_qualified_AST.initialize(QUALIFIED_CONS, "QUALIFIED_CONS");
			}
			qualifiedCons_qualified_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_48);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_48);
			} else {
				throw ex;
			}
		}
		returnAST = qualifiedCons_qualified_AST;
	}
	
	public final void qualifiedCons_unqualified(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST qualifiedCons_unqualified_AST = null;
		
		try {      // for error handling
			cons();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				qualifiedCons_unqualified_AST = (AST)currentAST.root;
				qualifiedCons_unqualified_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(QUALIFIED_CONS,"QUALIFIED_CONS")).add(astFactory.create(HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER,"HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER")).add(qualifiedCons_unqualified_AST));
				currentAST.root = qualifiedCons_unqualified_AST;
				currentAST.child = qualifiedCons_unqualified_AST!=null &&qualifiedCons_unqualified_AST.getFirstChild()!=null ?
					qualifiedCons_unqualified_AST.getFirstChild() : qualifiedCons_unqualified_AST;
				currentAST.advanceChildToEnd();
			}
			qualifiedCons_unqualified_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_48);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_48);
			} else {
				throw ex;
			}
		}
		returnAST = qualifiedCons_unqualified_AST;
	}
	
	public final void literal(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literal_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case INTEGER_LITERAL:
			{
				AST tmp94_AST = null;
				tmp94_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp94_AST);
				match(INTEGER_LITERAL);
				literal_AST = (AST)currentAST.root;
				break;
			}
			case FLOAT_LITERAL:
			{
				AST tmp95_AST = null;
				tmp95_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp95_AST);
				match(FLOAT_LITERAL);
				literal_AST = (AST)currentAST.root;
				break;
			}
			case CHAR_LITERAL:
			{
				AST tmp96_AST = null;
				tmp96_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp96_AST);
				match(CHAR_LITERAL);
				literal_AST = (AST)currentAST.root;
				break;
			}
			case STRING_LITERAL:
			{
				AST tmp97_AST = null;
				tmp97_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp97_AST);
				match(STRING_LITERAL);
				literal_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_100, _tokenSet_99);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_99);
			} else {
				throw ex;
			}
		}
		returnAST = literal_AST;
	}
	
	public final void exprListZeroOrMore(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exprListZeroOrMore_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case CONS_ID:
			case VAR_ID:
			case OPEN_PAREN:
			case BACKSLASH:
			case OPEN_BRACKET:
			case OPEN_BRACE:
			case STRING_LITERAL:
			case LITERAL_let:
			case LITERAL_if:
			case LITERAL_case:
			case MINUS:
			case CHAR_LITERAL:
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
			{
				expr(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop227:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						expr(contextToken, paraphrase);
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop227;
					}
					
				} while (true);
				}
				break;
			}
			case CLOSE_PAREN:
			case CLOSE_BRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			exprListZeroOrMore_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_101, _tokenSet_102);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_102);
			} else {
				throw ex;
			}
		}
		returnAST = exprListZeroOrMore_AST;
	}
	
	public final void recordValue(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST recordValue_AST = null;
		Token  openBraceNode = null;
		AST openBraceNode_AST = null;
		Token  closeBraceNode = null;
		AST closeBraceNode_AST = null;
		
		try {      // for error handling
			openBraceNode = LT(1);
			openBraceNode_AST = astFactory.create(openBraceNode);
			astFactory.makeASTRoot(currentAST, openBraceNode_AST);
			match(OPEN_BRACE);
			if ( inputState.guessing==0 ) {
				openBraceNode_AST.initialize (RECORD_CONSTRUCTOR, "Record");
			}
			baseRecord(LT(1), "a base-record expression");
			astFactory.addASTChild(currentAST, returnAST);
			fieldModificationList(LT(1), "a list of field extensions or field value updates");
			astFactory.addASTChild(currentAST, returnAST);
			closeBraceNode = LT(1);
			closeBraceNode_AST = astFactory.create(closeBraceNode);
			match(CLOSE_BRACE);
			if ( inputState.guessing==0 ) {
				((ParseTreeNode)openBraceNode_AST).addOmittedDelimiter(closeBraceNode_AST);
			}
			recordValue_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_54, _tokenSet_99);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_99);
			} else {
				throw ex;
			}
		}
		returnAST = recordValue_AST;
	}
	
	public final void baseRecord(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST baseRecord_AST = null;
		
		try {      // for error handling
			boolean synPredMatched215 = false;
			if (((_tokenSet_1.member(LA(1))) && (_tokenSet_103.member(LA(2))))) {
				int _m215 = mark();
				synPredMatched215 = true;
				inputState.guessing++;
				try {
					{
					expr(null,null);
					match(BAR);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched215 = false;
				}
				rewind(_m215);
inputState.guessing--;
			}
			if ( synPredMatched215 ) {
				{
				expr(LT(1), "a base-record expression");
				astFactory.addASTChild(currentAST, returnAST);
				match(BAR);
				}
				if ( inputState.guessing==0 ) {
					baseRecord_AST = (AST)currentAST.root;
					baseRecord_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(BASE_RECORD,"BASE_RECORD")).add(baseRecord_AST));
					currentAST.root = baseRecord_AST;
					currentAST.child = baseRecord_AST!=null &&baseRecord_AST.getFirstChild()!=null ?
						baseRecord_AST.getFirstChild() : baseRecord_AST;
					currentAST.advanceChildToEnd();
				}
				baseRecord_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_55.member(LA(1))) && (_tokenSet_104.member(LA(2)))) {
				if ( inputState.guessing==0 ) {
					baseRecord_AST = (AST)currentAST.root;
					baseRecord_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(BASE_RECORD,"BASE_RECORD")));
					currentAST.root = baseRecord_AST;
					currentAST.child = baseRecord_AST!=null &&baseRecord_AST.getFirstChild()!=null ?
						baseRecord_AST.getFirstChild() : baseRecord_AST;
					currentAST.advanceChildToEnd();
				}
				baseRecord_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_105, _tokenSet_55);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_55);
			} else {
				throw ex;
			}
		}
		returnAST = baseRecord_AST;
	}
	
	public final void fieldModificationList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldModificationList_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case VAR_ID:
			case ORDINAL_FIELD_NAME:
			{
				fieldModification(LT(1), "a field extension or field value update");
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop220:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						fieldModification(LT(1), "a field extension or field value update");
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop220;
					}
					
				} while (true);
				}
				break;
			}
			case CLOSE_BRACE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				fieldModificationList_AST = (AST)currentAST.root;
				fieldModificationList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FIELD_MODIFICATION_LIST,"FIELD_MODIFICATION_LIST")).add(fieldModificationList_AST));
				currentAST.root = fieldModificationList_AST;
				currentAST.child = fieldModificationList_AST!=null &&fieldModificationList_AST.getFirstChild()!=null ?
					fieldModificationList_AST.getFirstChild() : fieldModificationList_AST;
				currentAST.advanceChildToEnd();
			}
			fieldModificationList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_55, _tokenSet_57);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_57);
			} else {
				throw ex;
			}
		}
		returnAST = fieldModificationList_AST;
	}
	
	public final void fieldModification(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldModification_AST = null;
		Token  node = null;
		AST node_AST = null;
		Token  node2 = null;
		AST node2_AST = null;
		
		try {      // for error handling
			fieldName(LT(1), "a valid record-field name");
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case EQUALS:
			{
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(EQUALS);
				if ( inputState.guessing==0 ) {
					node_AST.initialize (FIELD_EXTENSION, "FIELD_EXTENSION");
				}
				break;
			}
			case COLONEQUALS:
			{
				node2 = LT(1);
				node2_AST = astFactory.create(node2);
				astFactory.makeASTRoot(currentAST, node2_AST);
				match(COLONEQUALS);
				if ( inputState.guessing==0 ) {
					node2_AST.initialize (FIELD_VALUE_UPDATE, "FIELD_VALUE_UPDATE");
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			expr(LT(1), "an expression");
			astFactory.addASTChild(currentAST, returnAST);
			fieldModification_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_49, _tokenSet_58);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_58);
			} else {
				throw ex;
			}
		}
		returnAST = fieldModification_AST;
	}
	
	public final void letDefnOrTypeDeclarationOrPatternMatchDecl(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST letDefnOrTypeDeclarationOrPatternMatchDecl_AST = null;
		AST doc_AST = null;
		
		try {      // for error handling
			optionalDocComment();
			doc_AST = (AST)returnAST;
			{
			boolean synPredMatched243 = false;
			if (((_tokenSet_85.member(LA(1))) && (_tokenSet_106.member(LA(2))))) {
				int _m243 = mark();
				synPredMatched243 = true;
				inputState.guessing++;
				try {
					{
					switch ( LA(1)) {
					case CONS_ID:
					{
						match(CONS_ID);
						break;
					}
					case OPEN_PAREN:
					{
						match(OPEN_PAREN);
						break;
					}
					case OPEN_BRACE:
					{
						match(OPEN_BRACE);
						break;
					}
					case OPEN_BRACKET:
					{
						match(OPEN_BRACKET);
						break;
					}
					case CHAR_LITERAL:
					{
						match(CHAR_LITERAL);
						break;
					}
					case INTEGER_LITERAL:
					{
						match(INTEGER_LITERAL);
						break;
					}
					case MINUS:
					{
						match(MINUS);
						break;
					}
					default:
						if ((LA(1)==VAR_ID||LA(1)==UNDERSCORE) && (LA(2)==COLON)) {
							{
							switch ( LA(1)) {
							case VAR_ID:
							{
								match(VAR_ID);
								break;
							}
							case UNDERSCORE:
							{
								match(UNDERSCORE);
								break;
							}
							default:
							{
								throw new NoViableAltException(LT(1), getFilename());
							}
							}
							}
							match(COLON);
						}
						else if ((LA(1)==UNDERSCORE) && (true)) {
							match(UNDERSCORE);
						}
					else {
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched243 = false;
				}
				rewind(_m243);
inputState.guessing--;
			}
			if ( synPredMatched243 ) {
				letPatternMatchDecl(doc_AST, LT(1), "a local pattern match declaration");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==VAR_ID) && (_tokenSet_14.member(LA(2)))) {
				letDefnWithOptionalDocComment(doc_AST, LT(1), "a let definition");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==VAR_ID) && (LA(2)==COLONCOLON)) {
				letDefnTypeDeclarationWithOptionalDocComment(doc_AST, LT(1), "a type declaration for a let definition");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			letDefnOrTypeDeclarationOrPatternMatchDecl_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_83, _tokenSet_38);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_38);
			} else {
				throw ex;
			}
		}
		returnAST = letDefnOrTypeDeclarationOrPatternMatchDecl_AST;
	}
	
	public final void letPatternMatchDecl(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST letPatternMatchDecl_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			letPatternMatchPattern(LT(1), "a pattern for a local pattern match declaration");
			astFactory.addASTChild(currentAST, returnAST);
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(EQUALS);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (LET_PATTERN_MATCH_DECL, "LET_PATTERN_MATCH_DECL");
			}
			expr(LT(1), "valid defining expression for a local pattern match declaration");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				
					if (doc != null && doc.getFirstChild() != null) {
				// a CALDoc comment is not allowed for a pattern match decl
				reportLocalPatternMatchDeclCannotHaveCALDocComment((ParseTreeNode)doc.getFirstChild());
					}
				
			}
			letPatternMatchDecl_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_85, _tokenSet_38);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_38);
			} else {
				throw ex;
			}
		}
		returnAST = letPatternMatchDecl_AST;
	}
	
	public final void letDefnWithOptionalDocComment(
		AST doc, Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST letDefnWithOptionalDocComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		Token  semicolonNode = null;
		AST semicolonNode_AST = null;
		
		try {      // for error handling
			functionName(LT(1), "a function name for a let definition");
			astFactory.addASTChild(currentAST, returnAST);
			functionParamList(LT(1), "a list of function parameters for a let definition");
			astFactory.addASTChild(currentAST, returnAST);
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(EQUALS);
			if ( inputState.guessing==0 ) {
				node_AST.initialize (LET_DEFN, "LET_DEFN");
			}
			expr(LT(1), "valid defining expression for a let definition");
			astFactory.addASTChild(currentAST, returnAST);
			semicolonNode = LT(1);
			semicolonNode_AST = astFactory.create(semicolonNode);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				letDefnWithOptionalDocComment_AST = (AST)currentAST.root;
				((ParseTreeNode)letDefnWithOptionalDocComment_AST).addOmittedDelimiter(semicolonNode_AST);
			}
			if ( inputState.guessing==0 ) {
				letDefnWithOptionalDocComment_AST = (AST)currentAST.root;
				
				AST firstChild = letDefnWithOptionalDocComment_AST.getFirstChild();
				doc.setNextSibling(firstChild);
				letDefnWithOptionalDocComment_AST.setFirstChild(doc);
				
			}
			letDefnWithOptionalDocComment_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_15, _tokenSet_38);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_38);
			} else {
				throw ex;
			}
		}
		returnAST = letDefnWithOptionalDocComment_AST;
	}
	
	public final void letPatternMatchPattern(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST letPatternMatchPattern_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		Token  closeParenNode = null;
		AST closeParenNode_AST = null;
		AST dataConstructorNameListOneOrMore_AST = null;
		Token  openBracketNode = null;
		AST openBracketNode_AST = null;
		AST maybeMinusIntSingleton_AST = null;
		AST maybeMinusIntListOneOrMore_AST = null;
		AST charSingleton_AST = null;
		AST charListOneOrMore_AST = null;
		AST wildcard_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONS_ID:
			{
				dataConstructorSingleton(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				dataConstructorArgumentBindings(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					letPatternMatchPattern_AST = (AST)currentAST.root;
					letPatternMatchPattern_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PATTERN_CONSTRUCTOR,"PATTERN_CONSTRUCTOR")).add(letPatternMatchPattern_AST));
					currentAST.root = letPatternMatchPattern_AST;
					currentAST.child = letPatternMatchPattern_AST!=null &&letPatternMatchPattern_AST.getFirstChild()!=null ?
						letPatternMatchPattern_AST.getFirstChild() : letPatternMatchPattern_AST;
					currentAST.advanceChildToEnd();
				}
				letPatternMatchPattern_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACE:
			{
				recordPattern(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				letPatternMatchPattern_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACKET:
			{
				openBracketNode = LT(1);
				openBracketNode_AST = astFactory.create(openBracketNode);
				astFactory.makeASTRoot(currentAST, openBracketNode_AST);
				match(OPEN_BRACKET);
				match(CLOSE_BRACKET);
				if ( inputState.guessing==0 ) {
					reportInvalidLocalPatternMatchNilPatternError((ParseTreeNode)openBracketNode_AST);
				}
				letPatternMatchPattern_AST = (AST)currentAST.root;
				break;
			}
			case MINUS:
			case INTEGER_LITERAL:
			{
				maybeMinusIntSingleton(contextToken, paraphrase);
				maybeMinusIntSingleton_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					reportInvalidLocalPatternMatchIntPatternError((ParseTreeNode)maybeMinusIntSingleton_AST);
				}
				letPatternMatchPattern_AST = (AST)currentAST.root;
				break;
			}
			case CHAR_LITERAL:
			{
				charSingleton();
				charSingleton_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					reportInvalidLocalPatternMatchCharPatternError((ParseTreeNode)charSingleton_AST);
				}
				letPatternMatchPattern_AST = (AST)currentAST.root;
				break;
			}
			default:
				if ((LA(1)==OPEN_PAREN) && (_tokenSet_107.member(LA(2)))) {
					openParenNode = LT(1);
					openParenNode_AST = astFactory.create(openParenNode);
					astFactory.makeASTRoot(currentAST, openParenNode_AST);
					match(OPEN_PAREN);
					if ( inputState.guessing==0 ) {
						openParenNode_AST.initialize (TUPLE_CONSTRUCTOR, "Tuple");
					}
					{
					switch ( LA(1)) {
					case VAR_ID:
					case UNDERSCORE:
					{
						patternVar(contextToken, paraphrase);
						astFactory.addASTChild(currentAST, returnAST);
						{
						_loop249:
						do {
							if ((LA(1)==COMMA)) {
								match(COMMA);
								patternVar(contextToken, paraphrase);
								astFactory.addASTChild(currentAST, returnAST);
							}
							else {
								break _loop249;
							}
							
						} while (true);
						}
						break;
					}
					case CLOSE_PAREN:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					closeParenNode = LT(1);
					closeParenNode_AST = astFactory.create(closeParenNode);
					match(CLOSE_PAREN);
					if ( inputState.guessing==0 ) {
						((ParseTreeNode)openParenNode_AST).addOmittedDelimiter(closeParenNode_AST);
					}
					letPatternMatchPattern_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==VAR_ID||LA(1)==UNDERSCORE) && (LA(2)==COLON)) {
					patternVar(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					AST tmp104_AST = null;
					tmp104_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp104_AST);
					match(COLON);
					patternVar(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					letPatternMatchPattern_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==OPEN_PAREN) && (LA(2)==CONS_ID)) {
					dataConstructorNameListOneOrMore(contextToken, paraphrase);
					dataConstructorNameListOneOrMore_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					dataConstructorArgumentBindings(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						reportInvalidLocalPatternMatchMultipleDataConsPatternError((ParseTreeNode)dataConstructorNameListOneOrMore_AST);
					}
					letPatternMatchPattern_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==OPEN_PAREN) && (LA(2)==MINUS||LA(2)==INTEGER_LITERAL)) {
					maybeMinusIntListOneOrMore(contextToken, paraphrase);
					maybeMinusIntListOneOrMore_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						reportInvalidLocalPatternMatchIntPatternError((ParseTreeNode)maybeMinusIntListOneOrMore_AST);
					}
					letPatternMatchPattern_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==OPEN_PAREN) && (LA(2)==CHAR_LITERAL)) {
					charListOneOrMore();
					charListOneOrMore_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						reportInvalidLocalPatternMatchCharPatternError((ParseTreeNode)charListOneOrMore_AST);
					}
					letPatternMatchPattern_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==UNDERSCORE) && (LA(2)==EQUALS)) {
					wildcard();
					wildcard_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						reportInvalidLocalPatternMatchWildcardPatternError((ParseTreeNode)wildcard_AST);
					}
					letPatternMatchPattern_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_85, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_62);
			} else {
				throw ex;
			}
		}
		returnAST = letPatternMatchPattern_AST;
	}
	
	public final void dataConstructorSingleton(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataConstructorSingleton_AST = null;
		
		try {      // for error handling
			qualifiedCons(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				dataConstructorSingleton_AST = (AST)currentAST.root;
				dataConstructorSingleton_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DATA_CONSTRUCTOR_NAME_SINGLETON,"DATA_CONSTRUCTOR_NAME_SINGLETON")).add(dataConstructorSingleton_AST));
				currentAST.root = dataConstructorSingleton_AST;
				currentAST.child = dataConstructorSingleton_AST!=null &&dataConstructorSingleton_AST.getFirstChild()!=null ?
					dataConstructorSingleton_AST.getFirstChild() : dataConstructorSingleton_AST;
				currentAST.advanceChildToEnd();
			}
			dataConstructorSingleton_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_17, _tokenSet_108);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_108);
			} else {
				throw ex;
			}
		}
		returnAST = dataConstructorSingleton_AST;
	}
	
	public final void dataConstructorArgumentBindings(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataConstructorArgumentBindings_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case VAR_ID:
			case EQUALS:
			case RARROW:
			case UNDERSCORE:
			{
				patternVarListZeroOrMore(LT(1), "a list of field names");
				astFactory.addASTChild(currentAST, returnAST);
				dataConstructorArgumentBindings_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACE:
			{
				match(OPEN_BRACE);
				fieldBindingVarAssignmentList(LT(1), "a list of comma-separated field bindings");
				astFactory.addASTChild(currentAST, returnAST);
				match(CLOSE_BRACE);
				dataConstructorArgumentBindings_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_108, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		returnAST = dataConstructorArgumentBindings_AST;
	}
	
	public final void patternVar(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST patternVar_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case VAR_ID:
			{
				var();
				astFactory.addASTChild(currentAST, returnAST);
				patternVar_AST = (AST)currentAST.root;
				break;
			}
			case UNDERSCORE:
			{
				wildcard();
				astFactory.addASTChild(currentAST, returnAST);
				patternVar_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_110, _tokenSet_111);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_111);
			} else {
				throw ex;
			}
		}
		returnAST = patternVar_AST;
	}
	
	public final void recordPattern(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST recordPattern_AST = null;
		Token  openBraceNode = null;
		AST openBraceNode_AST = null;
		Token  closeBraceNode = null;
		AST closeBraceNode_AST = null;
		
		try {      // for error handling
			openBraceNode = LT(1);
			openBraceNode_AST = astFactory.create(openBraceNode);
			astFactory.makeASTRoot(currentAST, openBraceNode_AST);
			match(OPEN_BRACE);
			if ( inputState.guessing==0 ) {
				openBraceNode_AST.initialize (RECORD_PATTERN, "RECORD_PATTERN");
			}
			baseRecordPattern(LT(1), "a valid base record pattern");
			astFactory.addASTChild(currentAST, returnAST);
			fieldBindingVarAssignmentList(LT(1), "a list of comma-separated field bindings");
			astFactory.addASTChild(currentAST, returnAST);
			closeBraceNode = LT(1);
			closeBraceNode_AST = astFactory.create(closeBraceNode);
			match(CLOSE_BRACE);
			if ( inputState.guessing==0 ) {
				((ParseTreeNode)openBraceNode_AST).addOmittedDelimiter(closeBraceNode_AST);
			}
			recordPattern_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_54, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		returnAST = recordPattern_AST;
	}
	
	public final void dataConstructorNameListOneOrMore(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dataConstructorNameListOneOrMore_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		
		try {      // for error handling
			openParenNode = LT(1);
			openParenNode_AST = astFactory.create(openParenNode);
			match(OPEN_PAREN);
			qualifiedCons(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop261:
			do {
				if ((LA(1)==BAR)) {
					match(BAR);
					qualifiedCons(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop261;
				}
				
			} while (true);
			}
			match(CLOSE_PAREN);
			if ( inputState.guessing==0 ) {
				dataConstructorNameListOneOrMore_AST = (AST)currentAST.root;
				dataConstructorNameListOneOrMore_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(DATA_CONSTRUCTOR_NAME_LIST,"DATA_CONSTRUCTOR_NAME_LIST")).add(dataConstructorNameListOneOrMore_AST));
				currentAST.root = dataConstructorNameListOneOrMore_AST;
				currentAST.child = dataConstructorNameListOneOrMore_AST!=null &&dataConstructorNameListOneOrMore_AST.getFirstChild()!=null ?
					dataConstructorNameListOneOrMore_AST.getFirstChild() : dataConstructorNameListOneOrMore_AST;
				currentAST.advanceChildToEnd();
			}
			dataConstructorNameListOneOrMore_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_112, _tokenSet_108);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_108);
			} else {
				throw ex;
			}
		}
		returnAST = dataConstructorNameListOneOrMore_AST;
	}
	
	public final void maybeMinusIntSingleton(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST maybeMinusIntSingleton_AST = null;
		
		try {      // for error handling
			maybeMinusInt(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				maybeMinusIntSingleton_AST = (AST)currentAST.root;
				maybeMinusIntSingleton_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MAYBE_MINUS_INT_LIST,"MAYBE_MINUS_INT_LIST")).add(maybeMinusIntSingleton_AST));
				currentAST.root = maybeMinusIntSingleton_AST;
				currentAST.child = maybeMinusIntSingleton_AST!=null &&maybeMinusIntSingleton_AST.getFirstChild()!=null ?
					maybeMinusIntSingleton_AST.getFirstChild() : maybeMinusIntSingleton_AST;
				currentAST.advanceChildToEnd();
			}
			maybeMinusIntSingleton_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_113, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		returnAST = maybeMinusIntSingleton_AST;
	}
	
	public final void maybeMinusIntListOneOrMore(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST maybeMinusIntListOneOrMore_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		
		try {      // for error handling
			openParenNode = LT(1);
			openParenNode_AST = astFactory.create(openParenNode);
			match(OPEN_PAREN);
			maybeMinusInt(LT(1), "an integer literal");
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop269:
			do {
				if ((LA(1)==BAR)) {
					match(BAR);
					maybeMinusInt(LT(1), "an integer literal");
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop269;
				}
				
			} while (true);
			}
			match(CLOSE_PAREN);
			if ( inputState.guessing==0 ) {
				maybeMinusIntListOneOrMore_AST = (AST)currentAST.root;
				maybeMinusIntListOneOrMore_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MAYBE_MINUS_INT_LIST,"MAYBE_MINUS_INT_LIST")).add(maybeMinusIntListOneOrMore_AST));
				currentAST.root = maybeMinusIntListOneOrMore_AST;
				currentAST.child = maybeMinusIntListOneOrMore_AST!=null &&maybeMinusIntListOneOrMore_AST.getFirstChild()!=null ?
					maybeMinusIntListOneOrMore_AST.getFirstChild() : maybeMinusIntListOneOrMore_AST;
				currentAST.advanceChildToEnd();
			}
			maybeMinusIntListOneOrMore_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_112, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		returnAST = maybeMinusIntListOneOrMore_AST;
	}
	
	public final void charSingleton() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST charSingleton_AST = null;
		
		try {      // for error handling
			AST tmp111_AST = null;
			tmp111_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp111_AST);
			match(CHAR_LITERAL);
			if ( inputState.guessing==0 ) {
				charSingleton_AST = (AST)currentAST.root;
				charSingleton_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CHAR_LIST,"CHAR_LIST")).add(charSingleton_AST));
				currentAST.root = charSingleton_AST;
				currentAST.child = charSingleton_AST!=null &&charSingleton_AST.getFirstChild()!=null ?
					charSingleton_AST.getFirstChild() : charSingleton_AST;
				currentAST.advanceChildToEnd();
			}
			charSingleton_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_109);
			} else {
			  throw ex;
			}
		}
		returnAST = charSingleton_AST;
	}
	
	public final void charListOneOrMore() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST charListOneOrMore_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		
		try {      // for error handling
			openParenNode = LT(1);
			openParenNode_AST = astFactory.create(openParenNode);
			match(OPEN_PAREN);
			AST tmp112_AST = null;
			tmp112_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp112_AST);
			match(CHAR_LITERAL);
			{
			_loop275:
			do {
				if ((LA(1)==BAR)) {
					match(BAR);
					AST tmp114_AST = null;
					tmp114_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp114_AST);
					match(CHAR_LITERAL);
				}
				else {
					break _loop275;
				}
				
			} while (true);
			}
			match(CLOSE_PAREN);
			if ( inputState.guessing==0 ) {
				charListOneOrMore_AST = (AST)currentAST.root;
				charListOneOrMore_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CHAR_LIST,"CHAR_LIST")).add(charListOneOrMore_AST));
				currentAST.root = charListOneOrMore_AST;
				currentAST.child = charListOneOrMore_AST!=null &&charListOneOrMore_AST.getFirstChild()!=null ?
					charListOneOrMore_AST.getFirstChild() : charListOneOrMore_AST;
				currentAST.advanceChildToEnd();
			}
			charListOneOrMore_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_109);
			} else {
			  throw ex;
			}
		}
		returnAST = charListOneOrMore_AST;
	}
	
	public final void wildcard() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST wildcard_AST = null;
		
		try {      // for error handling
			AST tmp116_AST = null;
			tmp116_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp116_AST);
			match(UNDERSCORE);
			wildcard_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_111);
			} else {
			  throw ex;
			}
		}
		returnAST = wildcard_AST;
	}
	
	public final void alt(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alt_AST = null;
		
		try {      // for error handling
			pat(contextToken, paraphrase);
			astFactory.addASTChild(currentAST, returnAST);
			match(RARROW);
			expr(LT(1), "a semicolon-terminated expression");
			astFactory.addASTChild(currentAST, returnAST);
			match(SEMICOLON);
			if ( inputState.guessing==0 ) {
				alt_AST = (AST)currentAST.root;
				alt_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ALT,"ALT")).add(alt_AST));
				currentAST.root = alt_AST;
				currentAST.child = alt_AST!=null &&alt_AST.getFirstChild()!=null ?
					alt_AST.getFirstChild() : alt_AST;
				currentAST.advanceChildToEnd();
			}
			alt_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_85, _tokenSet_114);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_114);
			} else {
				throw ex;
			}
		}
		returnAST = alt_AST;
	}
	
	public final void pat(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pat_AST = null;
		Token  openParenNode = null;
		AST openParenNode_AST = null;
		Token  closeParenNode = null;
		AST closeParenNode_AST = null;
		Token  openBracketNode = null;
		AST openBracketNode_AST = null;
		Token  closeBracketNode = null;
		AST closeBracketNode_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONS_ID:
			{
				dataConstructorSingleton(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				dataConstructorArgumentBindings(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					pat_AST = (AST)currentAST.root;
					pat_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PATTERN_CONSTRUCTOR,"PATTERN_CONSTRUCTOR")).add(pat_AST));
					currentAST.root = pat_AST;
					currentAST.child = pat_AST!=null &&pat_AST.getFirstChild()!=null ?
						pat_AST.getFirstChild() : pat_AST;
					currentAST.advanceChildToEnd();
				}
				pat_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACKET:
			{
				openBracketNode = LT(1);
				openBracketNode_AST = astFactory.create(openBracketNode);
				astFactory.makeASTRoot(currentAST, openBracketNode_AST);
				match(OPEN_BRACKET);
				if ( inputState.guessing==0 ) {
					openBracketNode_AST.initialize (LIST_CONSTRUCTOR, "List");
				}
				closeBracketNode = LT(1);
				closeBracketNode_AST = astFactory.create(closeBracketNode);
				match(CLOSE_BRACKET);
				if ( inputState.guessing==0 ) {
					((ParseTreeNode)openBracketNode_AST).addOmittedDelimiter(closeBracketNode_AST);
				}
				pat_AST = (AST)currentAST.root;
				break;
			}
			case MINUS:
			case INTEGER_LITERAL:
			{
				maybeMinusIntSingleton(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					pat_AST = (AST)currentAST.root;
					pat_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INT_PATTERN,"INT_PATTERN")).add(pat_AST));
					currentAST.root = pat_AST;
					currentAST.child = pat_AST!=null &&pat_AST.getFirstChild()!=null ?
						pat_AST.getFirstChild() : pat_AST;
					currentAST.advanceChildToEnd();
				}
				pat_AST = (AST)currentAST.root;
				break;
			}
			case CHAR_LITERAL:
			{
				charSingleton();
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					pat_AST = (AST)currentAST.root;
					pat_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CHAR_PATTERN,"CHAR_PATTERN")).add(pat_AST));
					currentAST.root = pat_AST;
					currentAST.child = pat_AST!=null &&pat_AST.getFirstChild()!=null ?
						pat_AST.getFirstChild() : pat_AST;
					currentAST.advanceChildToEnd();
				}
				pat_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_BRACE:
			{
				recordPattern(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				pat_AST = (AST)currentAST.root;
				break;
			}
			default:
				if ((LA(1)==OPEN_PAREN) && (LA(2)==CONS_ID)) {
					dataConstructorNameListOneOrMore(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					dataConstructorArgumentBindings(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						pat_AST = (AST)currentAST.root;
						pat_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PATTERN_CONSTRUCTOR,"PATTERN_CONSTRUCTOR")).add(pat_AST));
						currentAST.root = pat_AST;
						currentAST.child = pat_AST!=null &&pat_AST.getFirstChild()!=null ?
							pat_AST.getFirstChild() : pat_AST;
						currentAST.advanceChildToEnd();
					}
					pat_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==OPEN_PAREN) && (_tokenSet_107.member(LA(2)))) {
					openParenNode = LT(1);
					openParenNode_AST = astFactory.create(openParenNode);
					astFactory.makeASTRoot(currentAST, openParenNode_AST);
					match(OPEN_PAREN);
					if ( inputState.guessing==0 ) {
						openParenNode_AST.initialize (TUPLE_CONSTRUCTOR, "Tuple");
					}
					{
					switch ( LA(1)) {
					case VAR_ID:
					case UNDERSCORE:
					{
						patternVar(contextToken, paraphrase);
						astFactory.addASTChild(currentAST, returnAST);
						{
						_loop257:
						do {
							if ((LA(1)==COMMA)) {
								match(COMMA);
								patternVar(contextToken, paraphrase);
								astFactory.addASTChild(currentAST, returnAST);
							}
							else {
								break _loop257;
							}
							
						} while (true);
						}
						break;
					}
					case CLOSE_PAREN:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					closeParenNode = LT(1);
					closeParenNode_AST = astFactory.create(closeParenNode);
					match(CLOSE_PAREN);
					if ( inputState.guessing==0 ) {
						((ParseTreeNode)openParenNode_AST).addOmittedDelimiter(closeParenNode_AST);
					}
					pat_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==OPEN_PAREN) && (LA(2)==MINUS||LA(2)==INTEGER_LITERAL)) {
					maybeMinusIntListOneOrMore(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						pat_AST = (AST)currentAST.root;
						pat_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(INT_PATTERN,"INT_PATTERN")).add(pat_AST));
						currentAST.root = pat_AST;
						currentAST.child = pat_AST!=null &&pat_AST.getFirstChild()!=null ?
							pat_AST.getFirstChild() : pat_AST;
						currentAST.advanceChildToEnd();
					}
					pat_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==OPEN_PAREN) && (LA(2)==CHAR_LITERAL)) {
					charListOneOrMore();
					astFactory.addASTChild(currentAST, returnAST);
					if ( inputState.guessing==0 ) {
						pat_AST = (AST)currentAST.root;
						pat_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CHAR_PATTERN,"CHAR_PATTERN")).add(pat_AST));
						currentAST.root = pat_AST;
						currentAST.child = pat_AST!=null &&pat_AST.getFirstChild()!=null ?
							pat_AST.getFirstChild() : pat_AST;
						currentAST.advanceChildToEnd();
					}
					pat_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==VAR_ID||LA(1)==UNDERSCORE) && (LA(2)==COLON)) {
					patternVar(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					AST tmp120_AST = null;
					tmp120_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp120_AST);
					match(COLON);
					patternVar(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
					pat_AST = (AST)currentAST.root;
				}
				else if ((LA(1)==UNDERSCORE) && (LA(2)==RARROW)) {
					wildcard();
					astFactory.addASTChild(currentAST, returnAST);
					pat_AST = (AST)currentAST.root;
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_85, _tokenSet_82);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_82);
			} else {
				throw ex;
			}
		}
		returnAST = pat_AST;
	}
	
	public final void patternVarListZeroOrMore(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST patternVarListZeroOrMore_AST = null;
		
		try {      // for error handling
			{
			_loop265:
			do {
				if ((LA(1)==VAR_ID||LA(1)==UNDERSCORE)) {
					patternVar(contextToken, paraphrase);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop265;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				patternVarListZeroOrMore_AST = (AST)currentAST.root;
				patternVarListZeroOrMore_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(PATTERN_VAR_LIST,"PATTERN_VAR_LIST")).add(patternVarListZeroOrMore_AST));
				currentAST.root = patternVarListZeroOrMore_AST;
				currentAST.child = patternVarListZeroOrMore_AST!=null &&patternVarListZeroOrMore_AST.getFirstChild()!=null ?
					patternVarListZeroOrMore_AST.getFirstChild() : patternVarListZeroOrMore_AST;
				currentAST.advanceChildToEnd();
			}
			patternVarListZeroOrMore_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_115, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_109);
			} else {
				throw ex;
			}
		}
		returnAST = patternVarListZeroOrMore_AST;
	}
	
	public final void fieldBindingVarAssignmentList(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldBindingVarAssignmentList_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case VAR_ID:
			case ORDINAL_FIELD_NAME:
			{
				fieldBindingVarAssignment(LT(1), "a valid field-binding");
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop286:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						fieldBindingVarAssignment(LT(1), "a valid field-binding");
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop286;
					}
					
				} while (true);
				}
				break;
			}
			case CLOSE_BRACE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				fieldBindingVarAssignmentList_AST = (AST)currentAST.root;
				fieldBindingVarAssignmentList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FIELD_BINDING_VAR_ASSIGNMENT_LIST,"FIELD_BINDING_VAR_ASSIGNMENT_LIST")).add(fieldBindingVarAssignmentList_AST));
				currentAST.root = fieldBindingVarAssignmentList_AST;
				currentAST.child = fieldBindingVarAssignmentList_AST!=null &&fieldBindingVarAssignmentList_AST.getFirstChild()!=null ?
					fieldBindingVarAssignmentList_AST.getFirstChild() : fieldBindingVarAssignmentList_AST;
				currentAST.advanceChildToEnd();
			}
			fieldBindingVarAssignmentList_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_55, _tokenSet_57);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_57);
			} else {
				throw ex;
			}
		}
		returnAST = fieldBindingVarAssignmentList_AST;
	}
	
	public final void maybeMinusInt(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST maybeMinusInt_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MINUS:
			{
				{
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(MINUS);
				if ( inputState.guessing==0 ) {
					node_AST.initialize (MINUS, "-");
				}
				AST tmp122_AST = null;
				tmp122_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp122_AST);
				match(INTEGER_LITERAL);
				}
				maybeMinusInt_AST = (AST)currentAST.root;
				break;
			}
			case INTEGER_LITERAL:
			{
				AST tmp123_AST = null;
				tmp123_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp123_AST);
				match(INTEGER_LITERAL);
				maybeMinusInt_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_113, _tokenSet_116);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_116);
			} else {
				throw ex;
			}
		}
		returnAST = maybeMinusInt_AST;
	}
	
	public final void baseRecordPattern(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST baseRecordPattern_AST = null;
		
		try {      // for error handling
			boolean synPredMatched281 = false;
			if (((LA(1)==VAR_ID||LA(1)==UNDERSCORE) && (LA(2)==BAR))) {
				int _m281 = mark();
				synPredMatched281 = true;
				inputState.guessing++;
				try {
					{
					patternVar(null, null);
					match(BAR);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched281 = false;
				}
				rewind(_m281);
inputState.guessing--;
			}
			if ( synPredMatched281 ) {
				{
				patternVar(contextToken, paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				match(BAR);
				}
				if ( inputState.guessing==0 ) {
					baseRecordPattern_AST = (AST)currentAST.root;
					baseRecordPattern_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(BASE_RECORD_PATTERN,"BASE_RECORD_PATTERN")).add(baseRecordPattern_AST));
					currentAST.root = baseRecordPattern_AST;
					currentAST.child = baseRecordPattern_AST!=null &&baseRecordPattern_AST.getFirstChild()!=null ?
						baseRecordPattern_AST.getFirstChild() : baseRecordPattern_AST;
					currentAST.advanceChildToEnd();
				}
				baseRecordPattern_AST = (AST)currentAST.root;
			}
			else if ((_tokenSet_55.member(LA(1))) && (_tokenSet_117.member(LA(2)))) {
				if ( inputState.guessing==0 ) {
					baseRecordPattern_AST = (AST)currentAST.root;
					baseRecordPattern_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(BASE_RECORD_PATTERN,"BASE_RECORD_PATTERN")));
					currentAST.root = baseRecordPattern_AST;
					currentAST.child = baseRecordPattern_AST!=null &&baseRecordPattern_AST.getFirstChild()!=null ?
						baseRecordPattern_AST.getFirstChild() : baseRecordPattern_AST;
					currentAST.advanceChildToEnd();
				}
				baseRecordPattern_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_118, _tokenSet_55);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_55);
			} else {
				throw ex;
			}
		}
		returnAST = baseRecordPattern_AST;
	}
	
	public final void fieldBindingVarAssignment(
		Token contextToken, String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fieldBindingVarAssignment_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			boolean synPredMatched289 = false;
			if (((LA(1)==VAR_ID||LA(1)==ORDINAL_FIELD_NAME) && (LA(2)==EQUALS))) {
				int _m289 = mark();
				synPredMatched289 = true;
				inputState.guessing++;
				try {
					{
					fieldName(null, null);
					match(EQUALS);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched289 = false;
				}
				rewind(_m289);
inputState.guessing--;
			}
			if ( synPredMatched289 ) {
				fieldName(LT(1), "a valid record-field name");
				astFactory.addASTChild(currentAST, returnAST);
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(EQUALS);
				if ( inputState.guessing==0 ) {
					node_AST.initialize (FIELD_BINDING_VAR_ASSIGNMENT, "FIELD_BINDING_VAR_ASSIGNMENT");
				}
				patternVar(LT(1), "a valid variable name");
				astFactory.addASTChild(currentAST, returnAST);
				fieldBindingVarAssignment_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==VAR_ID||LA(1)==ORDINAL_FIELD_NAME) && (LA(2)==COMMA||LA(2)==CLOSE_BRACE)) {
				fieldName(LT(1), "a valid record-field name");
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					fieldBindingVarAssignment_AST = (AST)currentAST.root;
					fieldBindingVarAssignment_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FIELD_BINDING_VAR_ASSIGNMENT,"FIELD_BINDING_VAR_ASSIGNMENT")).add(fieldBindingVarAssignment_AST));
					currentAST.root = fieldBindingVarAssignment_AST;
					currentAST.child = fieldBindingVarAssignment_AST!=null &&fieldBindingVarAssignment_AST.getFirstChild()!=null ?
						fieldBindingVarAssignment_AST.getFirstChild() : fieldBindingVarAssignment_AST;
					currentAST.advanceChildToEnd();
				}
				fieldBindingVarAssignment_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				reportWithParaphrase(ex, contextToken, paraphrase, _tokenSet_49, _tokenSet_58);
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex, _tokenSet_58);
			} else {
				throw ex;
			}
		}
		returnAST = fieldBindingVarAssignment_AST;
	}
	
	public final void literal_index() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literal_index_AST = null;
		
		try {      // for error handling
			AST tmp125_AST = null;
			tmp125_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp125_AST);
			match(INTEGER_LITERAL);
			literal_index_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		returnAST = literal_index_AST;
	}
	
	public final void docComment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docComment_AST = null;
		Token  node = null;
		AST node_AST = null;
		Token  caldocCloseNode = null;
		AST caldocCloseNode_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_OPEN);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_COMMENT, "CALDOC_COMMENT");
			}
			docCommentDescriptionBlock();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentTaggedBlocks();
			astFactory.addASTChild(currentAST, returnAST);
			caldocCloseNode = LT(1);
			caldocCloseNode_AST = astFactory.create(caldocCloseNode);
			match(CALDOC_CLOSE);
			if ( inputState.guessing==0 ) {
				((ParseTreeNode)node_AST).addOmittedDelimiter(caldocCloseNode_AST);
			}
			docComment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_119);
			} else {
			  throw ex;
			}
		}
		returnAST = docComment_AST;
	}
	
	public final void moreDocComments() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST moreDocComments_AST = null;
		AST comment_AST = null;
		AST moreComments_AST = null;
		
		try {      // for error handling
			docComment();
			comment_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case CONS_ID:
			case VAR_ID:
			case LITERAL_module:
			case LITERAL_class:
			case OPEN_PAREN:
			case OPEN_BRACKET:
			case OPEN_BRACE:
			case LITERAL_data:
			case LITERAL_foreign:
			case LITERAL_instance:
			case LITERAL_primitive:
			case LITERAL_public:
			case LITERAL_private:
			case LITERAL_protected:
			case MINUS:
			case UNDERSCORE:
			case CHAR_LITERAL:
			case INTEGER_LITERAL:
			{
				if ( inputState.guessing==0 ) {
					moreDocComments_AST = (AST)currentAST.root;
					moreDocComments_AST = comment_AST;
					currentAST.root = moreDocComments_AST;
					currentAST.child = moreDocComments_AST!=null &&moreDocComments_AST.getFirstChild()!=null ?
						moreDocComments_AST.getFirstChild() : moreDocComments_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			case CALDOC_OPEN:
			{
				if ( inputState.guessing==0 ) {
					reportUnassociatedCALDocCommentError((ParseTreeNode)comment_AST);
				}
				moreDocComments();
				moreComments_AST = (AST)returnAST;
				if ( inputState.guessing==0 ) {
					moreDocComments_AST = (AST)currentAST.root;
					moreDocComments_AST = moreComments_AST;
					currentAST.root = moreDocComments_AST;
					currentAST.child = moreDocComments_AST!=null &&moreDocComments_AST.getFirstChild()!=null ?
						moreDocComments_AST.getFirstChild() : moreDocComments_AST;
					currentAST.advanceChildToEnd();
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			moreDocComments_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_5);
			} else {
			  throw ex;
			}
		}
		returnAST = moreDocComments_AST;
	}
	
	public final void docCommentDescriptionBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentDescriptionBlock_AST = null;
		AST text_AST = null;
		
		try {      // for error handling
			docCommentTextualContent();
			text_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				docCommentDescriptionBlock_AST = (AST)currentAST.root;
				docCommentDescriptionBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_DESCRIPTION_BLOCK,"CALDOC_DESCRIPTION_BLOCK")).add(text_AST));
				currentAST.root = docCommentDescriptionBlock_AST;
				currentAST.child = docCommentDescriptionBlock_AST!=null &&docCommentDescriptionBlock_AST.getFirstChild()!=null ?
					docCommentDescriptionBlock_AST.getFirstChild() : docCommentDescriptionBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentDescriptionBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentDescriptionBlock_AST;
	}
	
	public final void docCommentTaggedBlocks() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentTaggedBlocks_AST = null;
		
		try {      // for error handling
			{
			_loop356:
			do {
				switch ( LA(1)) {
				case CALDOC_AUTHOR_TAG:
				{
					docCommentAuthorBlock();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case CALDOC_DEPRECATED_TAG:
				{
					docCommentDeprecatedBlock();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case CALDOC_RETURN_TAG:
				{
					docCommentReturnBlock();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case CALDOC_VERSION_TAG:
				{
					docCommentVersionBlock();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case CALDOC_ARG_TAG:
				{
					docCommentArgBlock();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case CALDOC_SEE_TAG:
				{
					docCommentSeeBlock();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case CALDOC_UNKNOWN_TAG:
				{
					docCommentUnknownTagBlock();
					break;
				}
				default:
				{
					break _loop356;
				}
				}
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentTaggedBlocks_AST = (AST)currentAST.root;
				docCommentTaggedBlocks_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TAGGED_BLOCKS,"CALDOC_TAGGED_BLOCKS")).add(docCommentTaggedBlocks_AST));
				currentAST.root = docCommentTaggedBlocks_AST;
				currentAST.child = docCommentTaggedBlocks_AST!=null &&docCommentTaggedBlocks_AST.getFirstChild()!=null ?
					docCommentTaggedBlocks_AST.getFirstChild() : docCommentTaggedBlocks_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentTaggedBlocks_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_121);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentTaggedBlocks_AST;
	}
	
	public final void docCommentTextualContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentTextualContent_AST = null;
		
		try {      // for error handling
			{
			_loop302:
			do {
				switch ( LA(1)) {
				case CALDOC_OPEN_INLINE_TAG:
				case CALDOC_BLANK_TEXT_LINE:
				case CALDOC_TEXT_LINE:
				{
					docCommentTextSpan();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case CALDOC_NEWLINE:
				case CALDOC_NEWLINE_WITH_LEADING_ASTERISK:
				{
					docCommentLineBreak();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					break _loop302;
				}
				}
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentTextualContent_AST = (AST)currentAST.root;
				docCommentTextualContent_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT,"CALDOC_TEXT")).add(docCommentTextualContent_AST));
				currentAST.root = docCommentTextualContent_AST;
				currentAST.child = docCommentTextualContent_AST!=null &&docCommentTextualContent_AST.getFirstChild()!=null ?
					docCommentTextualContent_AST.getFirstChild() : docCommentTextualContent_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentTextualContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_122);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentTextualContent_AST;
	}
	
	public final void docCommentTextSpan() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentTextSpan_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CALDOC_TEXT_LINE:
			{
				AST tmp126_AST = null;
				tmp126_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp126_AST);
				match(CALDOC_TEXT_LINE);
				docCommentTextSpan_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_BLANK_TEXT_LINE:
			{
				AST tmp127_AST = null;
				tmp127_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp127_AST);
				match(CALDOC_BLANK_TEXT_LINE);
				docCommentTextSpan_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_OPEN_INLINE_TAG:
			{
				docCommentInlineBlock();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentTextSpan_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_123);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentTextSpan_AST;
	}
	
	public final void docCommentLineBreak() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLineBreak_AST = null;
		AST node_AST = null;
		
		try {      // for error handling
			docCommentNewline();
			node_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_LINE_BREAK, "CALDOC_TEXT_LINE_BREAK");
			}
			docCommentLineBreak_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_123);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLineBreak_AST;
	}
	
	public final void docCommentInlineBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlock_AST = null;
		Token  openInlineTag = null;
		AST openInlineTag_AST = null;
		AST content_AST = null;
		AST closeInlineTag_AST = null;
		
		try {      // for error handling
			openInlineTag = LT(1);
			openInlineTag_AST = astFactory.create(openInlineTag);
			match(CALDOC_OPEN_INLINE_TAG);
			docCommentInlineBlockContent();
			content_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			docCommentCloseInlineTag(openInlineTag);
			closeInlineTag_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				docCommentInlineBlock_AST = (AST)currentAST.root;
				
					 if (openInlineTag_AST == null || content_AST == null || closeInlineTag_AST == null) {// we did not match a proper inline block, and we reported a custom error for it, so the subtree for the close tag is null
					     docCommentInlineBlock_AST = null;
					 } else {
				docCommentInlineBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_INLINE_BLOCK,"CALDOC_TEXT_INLINE_BLOCK")).add(docCommentInlineBlock_AST));
				((ParseTreeNode)content_AST).addOmittedDelimiter(openInlineTag_AST);
				((ParseTreeNode)content_AST).addOmittedDelimiter(closeInlineTag_AST);
					 }
				
				currentAST.root = docCommentInlineBlock_AST;
				currentAST.child = docCommentInlineBlock_AST!=null &&docCommentInlineBlock_AST.getFirstChild()!=null ?
					docCommentInlineBlock_AST.getFirstChild() : docCommentInlineBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentInlineBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_123);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlock_AST;
	}
	
	public final void docCommentNewline() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentNewline_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CALDOC_NEWLINE:
			{
				AST tmp128_AST = null;
				tmp128_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp128_AST);
				match(CALDOC_NEWLINE);
				docCommentNewline_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_NEWLINE_WITH_LEADING_ASTERISK:
			{
				AST tmp129_AST = null;
				tmp129_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp129_AST);
				match(CALDOC_NEWLINE_WITH_LEADING_ASTERISK);
				docCommentNewline_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_123);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentNewline_AST;
	}
	
	public final void docCommentPreformattedBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentPreformattedBlock_AST = null;
		
		try {      // for error handling
			{
			_loop308:
			do {
				switch ( LA(1)) {
				case CALDOC_OPEN_INLINE_TAG:
				case CALDOC_BLANK_TEXT_LINE:
				case CALDOC_TEXT_LINE:
				{
					docCommentTextSpan();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case CALDOC_NEWLINE:
				case CALDOC_NEWLINE_WITH_LEADING_ASTERISK:
				{
					docCommentLineBreak();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					break _loop308;
				}
				}
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentPreformattedBlock_AST = (AST)currentAST.root;
				docCommentPreformattedBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_PREFORMATTED_BLOCK,"CALDOC_TEXT_PREFORMATTED_BLOCK")).add(docCommentPreformattedBlock_AST));
				currentAST.root = docCommentPreformattedBlock_AST;
				currentAST.child = docCommentPreformattedBlock_AST!=null &&docCommentPreformattedBlock_AST.getFirstChild()!=null ?
					docCommentPreformattedBlock_AST.getFirstChild() : docCommentPreformattedBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentPreformattedBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentPreformattedBlock_AST;
	}
	
	public final void docCommentTextBlockWithoutInlineTags(
		Token surroundingInlineTag
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentTextBlockWithoutInlineTags_AST = null;
		
		try {      // for error handling
			{
			_loop311:
			do {
				if ((_tokenSet_125.member(LA(1)))) {
					docCommentContentForTextBlockWithoutInlineTags(surroundingInlineTag);
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop311;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentTextBlockWithoutInlineTags_AST = (AST)currentAST.root;
				docCommentTextBlockWithoutInlineTags_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS,"CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS")).add(docCommentTextBlockWithoutInlineTags_AST));
				currentAST.root = docCommentTextBlockWithoutInlineTags_AST;
				currentAST.child = docCommentTextBlockWithoutInlineTags_AST!=null &&docCommentTextBlockWithoutInlineTags_AST.getFirstChild()!=null ?
					docCommentTextBlockWithoutInlineTags_AST.getFirstChild() : docCommentTextBlockWithoutInlineTags_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentTextBlockWithoutInlineTags_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentTextBlockWithoutInlineTags_AST;
	}
	
	public final void docCommentContentForTextBlockWithoutInlineTags(
		Token surroundingInlineTag
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentContentForTextBlockWithoutInlineTags_AST = null;
		Token  openInlineTag = null;
		AST openInlineTag_AST = null;
		AST tag_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CALDOC_OPEN_INLINE_TAG:
			{
				openInlineTag = LT(1);
				openInlineTag_AST = astFactory.create(openInlineTag);
				match(CALDOC_OPEN_INLINE_TAG);
				docCommentInlineBlockContent();
				tag_AST = (AST)returnAST;
				match(CALDOC_CLOSE_INLINE_TAG);
				if ( inputState.guessing==0 ) {
					reportCALDocInlineTagAppearingInInadmissibleLocationError(openInlineTag, surroundingInlineTag);
				}
				docCommentContentForTextBlockWithoutInlineTags_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_NEWLINE:
			case CALDOC_NEWLINE_WITH_LEADING_ASTERISK:
			case CALDOC_BLANK_TEXT_LINE:
			case CALDOC_TEXT_LINE:
			{
				{
				switch ( LA(1)) {
				case CALDOC_TEXT_LINE:
				{
					AST tmp131_AST = null;
					tmp131_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp131_AST);
					match(CALDOC_TEXT_LINE);
					break;
				}
				case CALDOC_BLANK_TEXT_LINE:
				{
					AST tmp132_AST = null;
					tmp132_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp132_AST);
					match(CALDOC_BLANK_TEXT_LINE);
					break;
				}
				case CALDOC_NEWLINE:
				case CALDOC_NEWLINE_WITH_LEADING_ASTERISK:
				{
					docCommentLineBreak();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				docCommentContentForTextBlockWithoutInlineTags_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_126);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentContentForTextBlockWithoutInlineTags_AST;
	}
	
	public final void docCommentInlineBlockContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContent_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CALDOC_INLINE_URL_TAG:
			{
				docCommentInlineBlockContentUrlTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_LINK_TAG:
			{
				docCommentInlineBlockContentLinkTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_EM_TAG:
			{
				docCommentInlineBlockContentEmTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_STRONG_TAG:
			{
				docCommentInlineBlockContentStrongTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_SUP_TAG:
			{
				docCommentInlineBlockContentSupTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_SUB_TAG:
			{
				docCommentInlineBlockContentSubTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_SUMMARY_TAG:
			{
				docCommentInlineBlockContentSummaryTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_CODE_TAG:
			{
				docCommentInlineBlockContentCodeTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_ORDERED_LIST_TAG:
			{
				docCommentInlineBlockContentOrderedListTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_UNORDERED_LIST_TAG:
			{
				docCommentInlineBlockContentUnorderedListTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_INLINE_UNKNOWN_TAG:
			{
				docCommentInlineBlockContentUnknownTagAndContent();
				astFactory.addASTChild(currentAST, returnAST);
				docCommentInlineBlockContent_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContent_AST;
	}
	
	public final void docCommentCloseInlineTag(
		Token openInlineTag
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentCloseInlineTag_AST = null;
		
		try {      // for error handling
			AST tmp133_AST = null;
			tmp133_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp133_AST);
			match(CALDOC_CLOSE_INLINE_TAG);
			docCommentCloseInlineTag_AST = (AST)currentAST.root;
		}
		catch (MismatchedTokenException ex) {
			if (inputState.guessing==0) {
				
				reportMaybeCALDocInlineBlockNotClosedBeforeEndOfTextBlockError(openInlineTag, ex);
				// no consumption of the invalid token... it may well be the close of the comment.
				
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_123);
				
			} else {
				throw ex;
			}
		}
		returnAST = docCommentCloseInlineTag_AST;
	}
	
	public final void docCommentInlineBlockContentUrlTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentUrlTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_URL_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_URL, "CALDOC_TEXT_URL");
			}
			docCommentTextBlockWithoutInlineTags(node);
			astFactory.addASTChild(currentAST, returnAST);
			docCommentInlineBlockContentUrlTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentUrlTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentLinkTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentLinkTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_LINK_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_LINK, "CALDOC_TEXT_LINK");
			}
			{
			switch ( LA(1)) {
			case CALDOC_SEE_TAG_FUNCTION_CONTEXT:
			{
				docCommentLinkFunction();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_MODULE_CONTEXT:
			{
				docCommentLinkModule();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_DATACONS_CONTEXT:
			{
				docCommentLinkDataCons();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_TYPECONS_CONTEXT:
			{
				docCommentLinkTypeCons();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_TYPECLASS_CONTEXT:
			{
				docCommentLinkTypeClass();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_UNKNOWN_CONTEXT:
			{
				docCommentLinkUnknown();
				break;
			}
			default:
				boolean synPredMatched323 = false;
				if (((LA(1)==CONS_ID||LA(1)==VAR_ID) && (LA(2)==EQUALS))) {
					int _m323 = mark();
					synPredMatched323 = true;
					inputState.guessing++;
					try {
						{
						{
						switch ( LA(1)) {
						case VAR_ID:
						{
							match(VAR_ID);
							break;
						}
						case CONS_ID:
						{
							match(CONS_ID);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(EQUALS);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched323 = false;
					}
					rewind(_m323);
inputState.guessing--;
				}
				if ( synPredMatched323 ) {
					docCommentLinkUnknownIdentifierLike();
				}
				else if ((_tokenSet_127.member(LA(1))) && (_tokenSet_128.member(LA(2)))) {
					docCommentLinkWithoutContext();
					astFactory.addASTChild(currentAST, returnAST);
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			docCommentInlineBlockContentLinkTagAndContent_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				
				reportMaybeMissingSeeOrLinkBlockContextInCALDocCommentError(ex);
				consume();
				consumeUntil(_tokenSet_124);
				
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_124);
				
			} else {
				throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentLinkTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentEmTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentEmTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_EM_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_EMPHASIZED_TEXT, "CALDOC_TEXT_EMPHASIZED_TEXT");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentInlineBlockContentEmTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentEmTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentStrongTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentStrongTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_STRONG_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT, "CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentInlineBlockContentStrongTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentStrongTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentSupTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentSupTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_SUP_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_SUPERSCRIPT_TEXT, "CALDOC_TEXT_SUPERSCRIPT_TEXT");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentInlineBlockContentSupTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentSupTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentSubTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentSubTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_SUB_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_SUBSCRIPT_TEXT, "CALDOC_TEXT_SUBSCRIPT_TEXT");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentInlineBlockContentSubTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentSubTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentSummaryTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentSummaryTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_SUMMARY_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_SUMMARY, "CALDOC_TEXT_SUMMARY");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentInlineBlockContentSummaryTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentSummaryTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentCodeTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentCodeTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_CODE_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_CODE_BLOCK, "CALDOC_TEXT_CODE_BLOCK");
			}
			docCommentPreformattedBlock();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentInlineBlockContentCodeTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentCodeTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentOrderedListTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentOrderedListTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_ORDERED_LIST_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_ORDERED_LIST, "CALDOC_TEXT_ORDERED_LIST");
			}
			{
			_loop332:
			do {
				if ((_tokenSet_129.member(LA(1)))) {
					docCommentListItem();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop332;
				}
				
			} while (true);
			}
			docCommentInlineBlockContentOrderedListTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentOrderedListTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentUnorderedListTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentUnorderedListTagAndContent_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_INLINE_UNORDERED_LIST_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_TEXT_UNORDERED_LIST, "CALDOC_TEXT_UNORDERED_LIST");
			}
			{
			_loop335:
			do {
				if ((_tokenSet_129.member(LA(1)))) {
					docCommentListItem();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop335;
				}
				
			} while (true);
			}
			docCommentInlineBlockContentUnorderedListTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentUnorderedListTagAndContent_AST;
	}
	
	public final void docCommentInlineBlockContentUnknownTagAndContent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentInlineBlockContentUnknownTagAndContent_AST = null;
		Token  unknownTag = null;
		AST unknownTag_AST = null;
		AST text_AST = null;
		
		try {      // for error handling
			unknownTag = LT(1);
			unknownTag_AST = astFactory.create(unknownTag);
			match(CALDOC_INLINE_UNKNOWN_TAG);
			docCommentTextualContent();
			text_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				reportUnrecognizedInlineTagInCALDocCommentError(unknownTag);
			}
			docCommentInlineBlockContentUnknownTagAndContent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentInlineBlockContentUnknownTagAndContent_AST;
	}
	
	public final void docCommentLinkFunction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkFunction_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_FUNCTION_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedQualifiedVar();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				docCommentLinkFunction_AST = (AST)currentAST.root;
				docCommentLinkFunction_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_LINK_FUNCTION,"CALDOC_TEXT_LINK_FUNCTION")).add(docCommentLinkFunction_AST));
				currentAST.root = docCommentLinkFunction_AST;
				currentAST.child = docCommentLinkFunction_AST!=null &&docCommentLinkFunction_AST.getFirstChild()!=null ?
					docCommentLinkFunction_AST.getFirstChild() : docCommentLinkFunction_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentLinkFunction_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkFunction_AST;
	}
	
	public final void docCommentLinkModule() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkModule_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_MODULE_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedModuleName();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				docCommentLinkModule_AST = (AST)currentAST.root;
				docCommentLinkModule_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_LINK_MODULE,"CALDOC_TEXT_LINK_MODULE")).add(docCommentLinkModule_AST));
				currentAST.root = docCommentLinkModule_AST;
				currentAST.child = docCommentLinkModule_AST!=null &&docCommentLinkModule_AST.getFirstChild()!=null ?
					docCommentLinkModule_AST.getFirstChild() : docCommentLinkModule_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentLinkModule_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkModule_AST;
	}
	
	public final void docCommentLinkDataCons() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkDataCons_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_DATACONS_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedQualifiedCons();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				docCommentLinkDataCons_AST = (AST)currentAST.root;
				docCommentLinkDataCons_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_LINK_DATACONS,"CALDOC_TEXT_LINK_DATACONS")).add(docCommentLinkDataCons_AST));
				currentAST.root = docCommentLinkDataCons_AST;
				currentAST.child = docCommentLinkDataCons_AST!=null &&docCommentLinkDataCons_AST.getFirstChild()!=null ?
					docCommentLinkDataCons_AST.getFirstChild() : docCommentLinkDataCons_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentLinkDataCons_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkDataCons_AST;
	}
	
	public final void docCommentLinkTypeCons() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkTypeCons_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_TYPECONS_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedQualifiedCons();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				docCommentLinkTypeCons_AST = (AST)currentAST.root;
				docCommentLinkTypeCons_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_LINK_TYPECONS,"CALDOC_TEXT_LINK_TYPECONS")).add(docCommentLinkTypeCons_AST));
				currentAST.root = docCommentLinkTypeCons_AST;
				currentAST.child = docCommentLinkTypeCons_AST!=null &&docCommentLinkTypeCons_AST.getFirstChild()!=null ?
					docCommentLinkTypeCons_AST.getFirstChild() : docCommentLinkTypeCons_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentLinkTypeCons_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkTypeCons_AST;
	}
	
	public final void docCommentLinkTypeClass() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkTypeClass_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_TYPECLASS_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedQualifiedCons();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				docCommentLinkTypeClass_AST = (AST)currentAST.root;
				docCommentLinkTypeClass_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_LINK_TYPECLASS,"CALDOC_TEXT_LINK_TYPECLASS")).add(docCommentLinkTypeClass_AST));
				currentAST.root = docCommentLinkTypeClass_AST;
				currentAST.child = docCommentLinkTypeClass_AST!=null &&docCommentLinkTypeClass_AST.getFirstChild()!=null ?
					docCommentLinkTypeClass_AST.getFirstChild() : docCommentLinkTypeClass_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentLinkTypeClass_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkTypeClass_AST;
	}
	
	public final void docCommentLinkUnknownIdentifierLike() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkUnknownIdentifierLike_AST = null;
		AST unknownContext_AST = null;
		
		try {      // for error handling
			docCommentLinkUnknownIdentifierLikeContext();
			unknownContext_AST = (AST)returnAST;
			docCommentLinkUnknownRemainder();
			if ( inputState.guessing==0 ) {
				reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError((ParseTreeNode)unknownContext_AST);
			}
			docCommentLinkUnknownIdentifierLike_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkUnknownIdentifierLike_AST;
	}
	
	public final void docCommentLinkWithoutContext() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkWithoutContext_AST = null;
		
		try {      // for error handling
			docCommentMaybeUncheckedReferenceWithoutContext();
			astFactory.addASTChild(currentAST, returnAST);
			if ( inputState.guessing==0 ) {
				docCommentLinkWithoutContext_AST = (AST)currentAST.root;
				docCommentLinkWithoutContext_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_TEXT_LINK_WITHOUT_CONTEXT,"CALDOC_TEXT_LINK_WITHOUT_CONTEXT")).add(docCommentLinkWithoutContext_AST));
				currentAST.root = docCommentLinkWithoutContext_AST;
				currentAST.child = docCommentLinkWithoutContext_AST!=null &&docCommentLinkWithoutContext_AST.getFirstChild()!=null ?
					docCommentLinkWithoutContext_AST.getFirstChild() : docCommentLinkWithoutContext_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentLinkWithoutContext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkWithoutContext_AST;
	}
	
	public final void docCommentLinkUnknown() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkUnknown_AST = null;
		Token  unknownContext = null;
		AST unknownContext_AST = null;
		
		try {      // for error handling
			unknownContext = LT(1);
			unknownContext_AST = astFactory.create(unknownContext);
			match(CALDOC_SEE_TAG_UNKNOWN_CONTEXT);
			docCommentLinkUnknownRemainder();
			if ( inputState.guessing==0 ) {
				reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError((ParseTreeNode)unknownContext_AST);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkUnknown_AST;
	}
	
	public final void docCommentListItem() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentListItem_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CALDOC_BLANK_TEXT_LINE:
			{
				match(CALDOC_BLANK_TEXT_LINE);
				docCommentListItem_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_NEWLINE:
			case CALDOC_NEWLINE_WITH_LEADING_ASTERISK:
			{
				docCommentNewline();
				docCommentListItem_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_OPEN_INLINE_TAG:
			{
				match(CALDOC_OPEN_INLINE_TAG);
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(CALDOC_INLINE_ITEM_TAG);
				if ( inputState.guessing==0 ) {
					node_AST.initialize(CALDOC_TEXT_LIST_ITEM, "CALDOC_TEXT_LIST_ITEM");
				}
				docCommentTextualContent();
				astFactory.addASTChild(currentAST, returnAST);
				match(CALDOC_CLOSE_INLINE_TAG);
				docCommentListItem_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_130);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentListItem_AST;
	}
	
	public final void docCommentLinkUnknownRemainder() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkUnknownRemainder_AST = null;
		
		try {      // for error handling
			{
			_loop343:
			do {
				switch ( LA(1)) {
				case CALDOC_SEE_TAG_FUNCTION_CONTEXT:
				{
					match(CALDOC_SEE_TAG_FUNCTION_CONTEXT);
					break;
				}
				case CALDOC_SEE_TAG_MODULE_CONTEXT:
				{
					match(CALDOC_SEE_TAG_MODULE_CONTEXT);
					break;
				}
				case CALDOC_SEE_TAG_DATACONS_CONTEXT:
				{
					match(CALDOC_SEE_TAG_DATACONS_CONTEXT);
					break;
				}
				case CALDOC_SEE_TAG_TYPECONS_CONTEXT:
				{
					match(CALDOC_SEE_TAG_TYPECONS_CONTEXT);
					break;
				}
				case CALDOC_SEE_TAG_TYPECLASS_CONTEXT:
				{
					match(CALDOC_SEE_TAG_TYPECLASS_CONTEXT);
					break;
				}
				case EQUALS:
				{
					match(EQUALS);
					break;
				}
				case DOT:
				{
					match(DOT);
					break;
				}
				case COMMA:
				{
					match(COMMA);
					break;
				}
				case CALDOC_SEE_TAG_QUOTE:
				{
					match(CALDOC_SEE_TAG_QUOTE);
					break;
				}
				case CONS_ID:
				{
					match(CONS_ID);
					break;
				}
				case VAR_ID:
				{
					match(VAR_ID);
					break;
				}
				case CALDOC_TEXT_LINE:
				{
					match(CALDOC_TEXT_LINE);
					break;
				}
				case CALDOC_BLANK_TEXT_LINE:
				{
					match(CALDOC_BLANK_TEXT_LINE);
					break;
				}
				case CALDOC_INLINE_SUMMARY_TAG:
				{
					match(CALDOC_INLINE_SUMMARY_TAG);
					break;
				}
				case CALDOC_INLINE_EM_TAG:
				{
					match(CALDOC_INLINE_EM_TAG);
					break;
				}
				case CALDOC_INLINE_STRONG_TAG:
				{
					match(CALDOC_INLINE_STRONG_TAG);
					break;
				}
				case CALDOC_INLINE_SUP_TAG:
				{
					match(CALDOC_INLINE_SUP_TAG);
					break;
				}
				case CALDOC_INLINE_SUB_TAG:
				{
					match(CALDOC_INLINE_SUB_TAG);
					break;
				}
				case CALDOC_INLINE_UNORDERED_LIST_TAG:
				{
					match(CALDOC_INLINE_UNORDERED_LIST_TAG);
					break;
				}
				case CALDOC_INLINE_ORDERED_LIST_TAG:
				{
					match(CALDOC_INLINE_ORDERED_LIST_TAG);
					break;
				}
				case CALDOC_INLINE_ITEM_TAG:
				{
					match(CALDOC_INLINE_ITEM_TAG);
					break;
				}
				case CALDOC_INLINE_CODE_TAG:
				{
					match(CALDOC_INLINE_CODE_TAG);
					break;
				}
				case CALDOC_INLINE_URL_TAG:
				{
					match(CALDOC_INLINE_URL_TAG);
					break;
				}
				case CALDOC_INLINE_LINK_TAG:
				{
					match(CALDOC_INLINE_LINK_TAG);
					break;
				}
				case CALDOC_NEWLINE:
				{
					match(CALDOC_NEWLINE);
					break;
				}
				case CALDOC_NEWLINE_WITH_LEADING_ASTERISK:
				{
					match(CALDOC_NEWLINE_WITH_LEADING_ASTERISK);
					break;
				}
				default:
				{
					break _loop343;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_124);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkUnknownRemainder_AST;
	}
	
	public final void docCommentLinkUnknownIdentifierLikeContext() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentLinkUnknownIdentifierLikeContext_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case VAR_ID:
			{
				AST tmp173_AST = null;
				tmp173_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp173_AST);
				match(VAR_ID);
				break;
			}
			case CONS_ID:
			{
				AST tmp174_AST = null;
				tmp174_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp174_AST);
				match(CONS_ID);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp175_AST = null;
			tmp175_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp175_AST);
			match(EQUALS);
			docCommentLinkUnknownIdentifierLikeContext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_131);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentLinkUnknownIdentifierLikeContext_AST;
	}
	
	public final void docCommentMaybeUncheckedQualifiedVar() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentMaybeUncheckedQualifiedVar_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONS_ID:
			case VAR_ID:
			{
				qualifiedVar(LT(1), "a qualified variable name");
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					docCommentMaybeUncheckedQualifiedVar_AST = (AST)currentAST.root;
					docCommentMaybeUncheckedQualifiedVar_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_CHECKED_QUALIFIED_VAR,"CALDOC_CHECKED_QUALIFIED_VAR")).add(docCommentMaybeUncheckedQualifiedVar_AST));
					currentAST.root = docCommentMaybeUncheckedQualifiedVar_AST;
					currentAST.child = docCommentMaybeUncheckedQualifiedVar_AST!=null &&docCommentMaybeUncheckedQualifiedVar_AST.getFirstChild()!=null ?
						docCommentMaybeUncheckedQualifiedVar_AST.getFirstChild() : docCommentMaybeUncheckedQualifiedVar_AST;
					currentAST.advanceChildToEnd();
				}
				docCommentMaybeUncheckedQualifiedVar_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_SEE_TAG_QUOTE:
			{
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(CALDOC_SEE_TAG_QUOTE);
				if ( inputState.guessing==0 ) {
					node_AST.initialize(CALDOC_UNCHECKED_QUALIFIED_VAR, "CALDOC_UNCHECKED_QUALIFIED_VAR");
				}
				qualifiedVar(LT(1), "a qualified variable name");
				astFactory.addASTChild(currentAST, returnAST);
				match(CALDOC_SEE_TAG_QUOTE);
				docCommentMaybeUncheckedQualifiedVar_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				
				reportMaybeUnrecognizedSeeOrLinkBlockReferenceInCALDocCommentError(ex);
				consume();
				consumeUntil(_tokenSet_132);
				
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_132);
				
			} else {
				throw ex;
			}
		}
		returnAST = docCommentMaybeUncheckedQualifiedVar_AST;
	}
	
	public final void docCommentMaybeUncheckedModuleName() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentMaybeUncheckedModuleName_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONS_ID:
			{
				moduleName(LT(1), "a valid module name");
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					docCommentMaybeUncheckedModuleName_AST = (AST)currentAST.root;
					docCommentMaybeUncheckedModuleName_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_CHECKED_MODULE_NAME,"CALDOC_CHECKED_MODULE_NAME")).add(docCommentMaybeUncheckedModuleName_AST));
					currentAST.root = docCommentMaybeUncheckedModuleName_AST;
					currentAST.child = docCommentMaybeUncheckedModuleName_AST!=null &&docCommentMaybeUncheckedModuleName_AST.getFirstChild()!=null ?
						docCommentMaybeUncheckedModuleName_AST.getFirstChild() : docCommentMaybeUncheckedModuleName_AST;
					currentAST.advanceChildToEnd();
				}
				docCommentMaybeUncheckedModuleName_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_SEE_TAG_QUOTE:
			{
				match(CALDOC_SEE_TAG_QUOTE);
				moduleName(LT(1), "a valid module name");
				astFactory.addASTChild(currentAST, returnAST);
				match(CALDOC_SEE_TAG_QUOTE);
				if ( inputState.guessing==0 ) {
					docCommentMaybeUncheckedModuleName_AST = (AST)currentAST.root;
					docCommentMaybeUncheckedModuleName_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_UNCHECKED_MODULE_NAME,"CALDOC_UNCHECKED_MODULE_NAME")).add(docCommentMaybeUncheckedModuleName_AST));
					currentAST.root = docCommentMaybeUncheckedModuleName_AST;
					currentAST.child = docCommentMaybeUncheckedModuleName_AST!=null &&docCommentMaybeUncheckedModuleName_AST.getFirstChild()!=null ?
						docCommentMaybeUncheckedModuleName_AST.getFirstChild() : docCommentMaybeUncheckedModuleName_AST;
					currentAST.advanceChildToEnd();
				}
				docCommentMaybeUncheckedModuleName_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				
				reportMaybeUnrecognizedSeeOrLinkBlockReferenceInCALDocCommentError(ex);
				consume();
				consumeUntil(_tokenSet_132);
				
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_132);
				
			} else {
				throw ex;
			}
		}
		returnAST = docCommentMaybeUncheckedModuleName_AST;
	}
	
	public final void docCommentMaybeUncheckedQualifiedCons() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentMaybeUncheckedQualifiedCons_AST = null;
		
		try {      // for error handling
			docCommentMaybeUncheckedQualifiedConsWithParaphrase("a qualified constructor name");
			astFactory.addASTChild(currentAST, returnAST);
			docCommentMaybeUncheckedQualifiedCons_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_132);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentMaybeUncheckedQualifiedCons_AST;
	}
	
	public final void docCommentMaybeUncheckedReferenceWithoutContext() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentMaybeUncheckedReferenceWithoutContext_AST = null;
		
		try {      // for error handling
			{
			boolean synPredMatched353 = false;
			if (((_tokenSet_127.member(LA(1))) && (_tokenSet_133.member(LA(2))))) {
				int _m353 = mark();
				synPredMatched353 = true;
				inputState.guessing++;
				try {
					{
					docCommentMaybeUncheckedQualifiedVar();
					}
				}
				catch (RecognitionException pe) {
					synPredMatched353 = false;
				}
				rewind(_m353);
inputState.guessing--;
			}
			if ( synPredMatched353 ) {
				docCommentMaybeUncheckedQualifiedVar();
				astFactory.addASTChild(currentAST, returnAST);
			}
			else if ((LA(1)==CONS_ID||LA(1)==CALDOC_SEE_TAG_QUOTE) && (_tokenSet_134.member(LA(2)))) {
				docCommentMaybeUncheckedQualifiedConsWithParaphrase("a qualified variable name or a qualified constructor name");
				astFactory.addASTChild(currentAST, returnAST);
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			docCommentMaybeUncheckedReferenceWithoutContext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_132);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentMaybeUncheckedReferenceWithoutContext_AST;
	}
	
	public final void docCommentMaybeUncheckedQualifiedConsWithParaphrase(
		String paraphrase
	) throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CONS_ID:
			{
				qualifiedCons(LT(1), paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				if ( inputState.guessing==0 ) {
					docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST = (AST)currentAST.root;
					docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_CHECKED_QUALIFIED_CONS,"CALDOC_CHECKED_QUALIFIED_CONS")).add(docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST));
					currentAST.root = docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST;
					currentAST.child = docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST!=null &&docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST.getFirstChild()!=null ?
						docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST.getFirstChild() : docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST;
					currentAST.advanceChildToEnd();
				}
				docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST = (AST)currentAST.root;
				break;
			}
			case CALDOC_SEE_TAG_QUOTE:
			{
				node = LT(1);
				node_AST = astFactory.create(node);
				astFactory.makeASTRoot(currentAST, node_AST);
				match(CALDOC_SEE_TAG_QUOTE);
				if ( inputState.guessing==0 ) {
					node_AST.initialize(CALDOC_UNCHECKED_QUALIFIED_CONS, "CALDOC_UNCHECKED_QUALIFIED_CONS");
				}
				qualifiedCons(LT(1), paraphrase);
				astFactory.addASTChild(currentAST, returnAST);
				match(CALDOC_SEE_TAG_QUOTE);
				docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				
				reportMaybeUnrecognizedSeeOrLinkBlockReferenceInCALDocCommentError(ex);
				consume();
				consumeUntil(_tokenSet_132);
				
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_132);
				
			} else {
				throw ex;
			}
		}
		returnAST = docCommentMaybeUncheckedQualifiedConsWithParaphrase_AST;
	}
	
	public final void docCommentAuthorBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentAuthorBlock_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_AUTHOR_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_AUTHOR_BLOCK, "CALDOC_AUTHOR_BLOCK");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentAuthorBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentAuthorBlock_AST;
	}
	
	public final void docCommentDeprecatedBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentDeprecatedBlock_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_DEPRECATED_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_DEPRECATED_BLOCK, "CALDOC_DEPRECATED_BLOCK");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentDeprecatedBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentDeprecatedBlock_AST;
	}
	
	public final void docCommentReturnBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentReturnBlock_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_RETURN_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_RETURN_BLOCK, "CALDOC_RETURN_BLOCK");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentReturnBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentReturnBlock_AST;
	}
	
	public final void docCommentVersionBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentVersionBlock_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_VERSION_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_VERSION_BLOCK, "CALDOC_VERSION_BLOCK");
			}
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentVersionBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentVersionBlock_AST;
	}
	
	public final void docCommentArgBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentArgBlock_AST = null;
		Token  node = null;
		AST node_AST = null;
		
		try {      // for error handling
			node = LT(1);
			node_AST = astFactory.create(node);
			astFactory.makeASTRoot(currentAST, node_AST);
			match(CALDOC_ARG_TAG);
			if ( inputState.guessing==0 ) {
				node_AST.initialize(CALDOC_ARG_BLOCK, "CALDOC_ARG_BLOCK");
			}
			fieldName(LT(1), "a valid argument name");
			astFactory.addASTChild(currentAST, returnAST);
			docCommentTextualContent();
			astFactory.addASTChild(currentAST, returnAST);
			docCommentArgBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentArgBlock_AST;
	}
	
	public final void docCommentSeeBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeBlock_AST = null;
		Token  seeTag = null;
		AST seeTag_AST = null;
		
		try {      // for error handling
			seeTag = LT(1);
			seeTag_AST = astFactory.create(seeTag);
			match(CALDOC_SEE_TAG);
			{
			switch ( LA(1)) {
			case CALDOC_SEE_TAG_FUNCTION_CONTEXT:
			{
				docCommentSeeFunctionBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_MODULE_CONTEXT:
			{
				docCommentSeeModuleBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_DATACONS_CONTEXT:
			{
				docCommentSeeDataConsBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_TYPECONS_CONTEXT:
			{
				docCommentSeeTypeConsBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_TYPECLASS_CONTEXT:
			{
				docCommentSeeTypeClassBlock();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CALDOC_SEE_TAG_UNKNOWN_CONTEXT:
			{
				docCommentSeeUnknownBlock();
				break;
			}
			default:
				boolean synPredMatched367 = false;
				if (((LA(1)==CONS_ID||LA(1)==VAR_ID) && (LA(2)==EQUALS))) {
					int _m367 = mark();
					synPredMatched367 = true;
					inputState.guessing++;
					try {
						{
						{
						switch ( LA(1)) {
						case VAR_ID:
						{
							match(VAR_ID);
							break;
						}
						case CONS_ID:
						{
							match(CONS_ID);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						match(EQUALS);
						}
					}
					catch (RecognitionException pe) {
						synPredMatched367 = false;
					}
					rewind(_m367);
inputState.guessing--;
				}
				if ( synPredMatched367 ) {
					docCommentSeeUnknownBlockIdentifierLike();
				}
				else if ((_tokenSet_127.member(LA(1))) && (_tokenSet_135.member(LA(2)))) {
					docCommentSeeBlockWithoutContext();
					astFactory.addASTChild(currentAST, returnAST);
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			if ( inputState.guessing==0 ) {
				docCommentSeeBlock_AST = (AST)currentAST.root;
				
				docCommentSeeBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_SEE_BLOCK,"CALDOC_SEE_BLOCK")).add(docCommentSeeBlock_AST));
				((ParseTreeNode)docCommentSeeBlock_AST).addOmittedDelimiter(seeTag_AST);
				
				currentAST.root = docCommentSeeBlock_AST;
				currentAST.child = docCommentSeeBlock_AST!=null &&docCommentSeeBlock_AST.getFirstChild()!=null ?
					docCommentSeeBlock_AST.getFirstChild() : docCommentSeeBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentSeeBlock_AST = (AST)currentAST.root;
		}
		catch (NoViableAltException ex) {
			if (inputState.guessing==0) {
				
				reportMaybeMissingSeeOrLinkBlockContextInCALDocCommentError(ex);
				consume();
				consumeUntil(_tokenSet_120);
				
			} else {
				throw ex;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_120);
				
			} else {
				throw ex;
			}
		}
		returnAST = docCommentSeeBlock_AST;
	}
	
	public final void docCommentUnknownTagBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentUnknownTagBlock_AST = null;
		Token  unknownTag = null;
		AST unknownTag_AST = null;
		AST text_AST = null;
		
		try {      // for error handling
			unknownTag = LT(1);
			unknownTag_AST = astFactory.create(unknownTag);
			match(CALDOC_UNKNOWN_TAG);
			docCommentTextualContent();
			text_AST = (AST)returnAST;
			if ( inputState.guessing==0 ) {
				reportUnrecognizedTagInCALDocCommentError(unknownTag);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentUnknownTagBlock_AST;
	}
	
	public final void docCommentSeeFunctionBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeFunctionBlock_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_FUNCTION_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedQualifiedVar();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop377:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					docCommentMaybeUncheckedQualifiedVar();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop377;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentSeeFunctionBlock_AST = (AST)currentAST.root;
				docCommentSeeFunctionBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_SEE_FUNCTION_BLOCK,"CALDOC_SEE_FUNCTION_BLOCK")).add(docCommentSeeFunctionBlock_AST));
				currentAST.root = docCommentSeeFunctionBlock_AST;
				currentAST.child = docCommentSeeFunctionBlock_AST!=null &&docCommentSeeFunctionBlock_AST.getFirstChild()!=null ?
					docCommentSeeFunctionBlock_AST.getFirstChild() : docCommentSeeFunctionBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentSeeFunctionBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeFunctionBlock_AST;
	}
	
	public final void docCommentSeeModuleBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeModuleBlock_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_MODULE_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedModuleName();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop380:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					docCommentMaybeUncheckedModuleName();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop380;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentSeeModuleBlock_AST = (AST)currentAST.root;
				docCommentSeeModuleBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_SEE_MODULE_BLOCK,"CALDOC_SEE_MODULE_BLOCK")).add(docCommentSeeModuleBlock_AST));
				currentAST.root = docCommentSeeModuleBlock_AST;
				currentAST.child = docCommentSeeModuleBlock_AST!=null &&docCommentSeeModuleBlock_AST.getFirstChild()!=null ?
					docCommentSeeModuleBlock_AST.getFirstChild() : docCommentSeeModuleBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentSeeModuleBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeModuleBlock_AST;
	}
	
	public final void docCommentSeeDataConsBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeDataConsBlock_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_DATACONS_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedQualifiedCons();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop383:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					docCommentMaybeUncheckedQualifiedCons();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop383;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentSeeDataConsBlock_AST = (AST)currentAST.root;
				docCommentSeeDataConsBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_SEE_DATACONS_BLOCK,"CALDOC_SEE_DATACONS_BLOCK")).add(docCommentSeeDataConsBlock_AST));
				currentAST.root = docCommentSeeDataConsBlock_AST;
				currentAST.child = docCommentSeeDataConsBlock_AST!=null &&docCommentSeeDataConsBlock_AST.getFirstChild()!=null ?
					docCommentSeeDataConsBlock_AST.getFirstChild() : docCommentSeeDataConsBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentSeeDataConsBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeDataConsBlock_AST;
	}
	
	public final void docCommentSeeTypeConsBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeTypeConsBlock_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_TYPECONS_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedQualifiedCons();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop386:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					docCommentMaybeUncheckedQualifiedCons();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop386;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentSeeTypeConsBlock_AST = (AST)currentAST.root;
				docCommentSeeTypeConsBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_SEE_TYPECONS_BLOCK,"CALDOC_SEE_TYPECONS_BLOCK")).add(docCommentSeeTypeConsBlock_AST));
				currentAST.root = docCommentSeeTypeConsBlock_AST;
				currentAST.child = docCommentSeeTypeConsBlock_AST!=null &&docCommentSeeTypeConsBlock_AST.getFirstChild()!=null ?
					docCommentSeeTypeConsBlock_AST.getFirstChild() : docCommentSeeTypeConsBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentSeeTypeConsBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeTypeConsBlock_AST;
	}
	
	public final void docCommentSeeTypeClassBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeTypeClassBlock_AST = null;
		
		try {      // for error handling
			match(CALDOC_SEE_TAG_TYPECLASS_CONTEXT);
			match(EQUALS);
			docCommentMaybeUncheckedQualifiedCons();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop389:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					docCommentMaybeUncheckedQualifiedCons();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop389;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentSeeTypeClassBlock_AST = (AST)currentAST.root;
				docCommentSeeTypeClassBlock_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_SEE_TYPECLASS_BLOCK,"CALDOC_SEE_TYPECLASS_BLOCK")).add(docCommentSeeTypeClassBlock_AST));
				currentAST.root = docCommentSeeTypeClassBlock_AST;
				currentAST.child = docCommentSeeTypeClassBlock_AST!=null &&docCommentSeeTypeClassBlock_AST.getFirstChild()!=null ?
					docCommentSeeTypeClassBlock_AST.getFirstChild() : docCommentSeeTypeClassBlock_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentSeeTypeClassBlock_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeTypeClassBlock_AST;
	}
	
	public final void docCommentSeeUnknownBlockIdentifierLike() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeUnknownBlockIdentifierLike_AST = null;
		AST unknownContext_AST = null;
		
		try {      // for error handling
			docCommentSeeUnknownBlockIdentifierLikeContext();
			unknownContext_AST = (AST)returnAST;
			docCommentSeeUnknownBlockRemainder();
			if ( inputState.guessing==0 ) {
				reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError((ParseTreeNode)unknownContext_AST);
			}
			docCommentSeeUnknownBlockIdentifierLike_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeUnknownBlockIdentifierLike_AST;
	}
	
	public final void docCommentSeeBlockWithoutContext() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeBlockWithoutContext_AST = null;
		
		try {      // for error handling
			docCommentMaybeUncheckedReferenceWithoutContext();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop392:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					docCommentMaybeUncheckedReferenceWithoutContext();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop392;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				docCommentSeeBlockWithoutContext_AST = (AST)currentAST.root;
				docCommentSeeBlockWithoutContext_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(CALDOC_SEE_BLOCK_WITHOUT_CONTEXT,"CALDOC_SEE_BLOCK_WITHOUT_CONTEXT")).add(docCommentSeeBlockWithoutContext_AST));
				currentAST.root = docCommentSeeBlockWithoutContext_AST;
				currentAST.child = docCommentSeeBlockWithoutContext_AST!=null &&docCommentSeeBlockWithoutContext_AST.getFirstChild()!=null ?
					docCommentSeeBlockWithoutContext_AST.getFirstChild() : docCommentSeeBlockWithoutContext_AST;
				currentAST.advanceChildToEnd();
			}
			docCommentSeeBlockWithoutContext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeBlockWithoutContext_AST;
	}
	
	public final void docCommentSeeUnknownBlock() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeUnknownBlock_AST = null;
		Token  unknownContext = null;
		AST unknownContext_AST = null;
		
		try {      // for error handling
			unknownContext = LT(1);
			unknownContext_AST = astFactory.create(unknownContext);
			match(CALDOC_SEE_TAG_UNKNOWN_CONTEXT);
			docCommentSeeUnknownBlockRemainder();
			if ( inputState.guessing==0 ) {
				reportUnrecognizedSeeOrLinkBlockContextInCALDocCommentError((ParseTreeNode)unknownContext_AST);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeUnknownBlock_AST;
	}
	
	public final void docCommentSeeUnknownBlockRemainder() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeUnknownBlockRemainder_AST = null;
		
		try {      // for error handling
			{
			_loop374:
			do {
				switch ( LA(1)) {
				case CALDOC_SEE_TAG_FUNCTION_CONTEXT:
				{
					match(CALDOC_SEE_TAG_FUNCTION_CONTEXT);
					break;
				}
				case CALDOC_SEE_TAG_MODULE_CONTEXT:
				{
					match(CALDOC_SEE_TAG_MODULE_CONTEXT);
					break;
				}
				case CALDOC_SEE_TAG_DATACONS_CONTEXT:
				{
					match(CALDOC_SEE_TAG_DATACONS_CONTEXT);
					break;
				}
				case CALDOC_SEE_TAG_TYPECONS_CONTEXT:
				{
					match(CALDOC_SEE_TAG_TYPECONS_CONTEXT);
					break;
				}
				case CALDOC_SEE_TAG_TYPECLASS_CONTEXT:
				{
					match(CALDOC_SEE_TAG_TYPECLASS_CONTEXT);
					break;
				}
				case EQUALS:
				{
					match(EQUALS);
					break;
				}
				case DOT:
				{
					match(DOT);
					break;
				}
				case COMMA:
				{
					match(COMMA);
					break;
				}
				case CALDOC_SEE_TAG_QUOTE:
				{
					match(CALDOC_SEE_TAG_QUOTE);
					break;
				}
				case CONS_ID:
				{
					match(CONS_ID);
					break;
				}
				case VAR_ID:
				{
					match(VAR_ID);
					break;
				}
				case CALDOC_TEXT_LINE:
				{
					match(CALDOC_TEXT_LINE);
					break;
				}
				case CALDOC_BLANK_TEXT_LINE:
				{
					match(CALDOC_BLANK_TEXT_LINE);
					break;
				}
				case CALDOC_OPEN_INLINE_TAG:
				{
					match(CALDOC_OPEN_INLINE_TAG);
					break;
				}
				case CALDOC_CLOSE_INLINE_TAG:
				{
					match(CALDOC_CLOSE_INLINE_TAG);
					break;
				}
				case CALDOC_INLINE_SUMMARY_TAG:
				{
					match(CALDOC_INLINE_SUMMARY_TAG);
					break;
				}
				case CALDOC_INLINE_EM_TAG:
				{
					match(CALDOC_INLINE_EM_TAG);
					break;
				}
				case CALDOC_INLINE_STRONG_TAG:
				{
					match(CALDOC_INLINE_STRONG_TAG);
					break;
				}
				case CALDOC_INLINE_SUP_TAG:
				{
					match(CALDOC_INLINE_SUP_TAG);
					break;
				}
				case CALDOC_INLINE_SUB_TAG:
				{
					match(CALDOC_INLINE_SUB_TAG);
					break;
				}
				case CALDOC_INLINE_UNORDERED_LIST_TAG:
				{
					match(CALDOC_INLINE_UNORDERED_LIST_TAG);
					break;
				}
				case CALDOC_INLINE_ORDERED_LIST_TAG:
				{
					match(CALDOC_INLINE_ORDERED_LIST_TAG);
					break;
				}
				case CALDOC_INLINE_ITEM_TAG:
				{
					match(CALDOC_INLINE_ITEM_TAG);
					break;
				}
				case CALDOC_INLINE_CODE_TAG:
				{
					match(CALDOC_INLINE_CODE_TAG);
					break;
				}
				case CALDOC_INLINE_URL_TAG:
				{
					match(CALDOC_INLINE_URL_TAG);
					break;
				}
				case CALDOC_INLINE_LINK_TAG:
				{
					match(CALDOC_INLINE_LINK_TAG);
					break;
				}
				case CALDOC_INLINE_UNKNOWN_TAG:
				{
					match(CALDOC_INLINE_UNKNOWN_TAG);
					break;
				}
				case CALDOC_NEWLINE:
				{
					match(CALDOC_NEWLINE);
					break;
				}
				case CALDOC_NEWLINE_WITH_LEADING_ASTERISK:
				{
					match(CALDOC_NEWLINE_WITH_LEADING_ASTERISK);
					break;
				}
				default:
				{
					break _loop374;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_120);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeUnknownBlockRemainder_AST;
	}
	
	public final void docCommentSeeUnknownBlockIdentifierLikeContext() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST docCommentSeeUnknownBlockIdentifierLikeContext_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case VAR_ID:
			{
				AST tmp225_AST = null;
				tmp225_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp225_AST);
				match(VAR_ID);
				break;
			}
			case CONS_ID:
			{
				AST tmp226_AST = null;
				tmp226_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp226_AST);
				match(CONS_ID);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp227_AST = null;
			tmp227_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp227_AST);
			match(EQUALS);
			docCommentSeeUnknownBlockIdentifierLikeContext_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_136);
			} else {
			  throw ex;
			}
		}
		returnAST = docCommentSeeUnknownBlockIdentifierLikeContext_AST;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"the start of a CALDoc comment '/**'",
		"the end of a CALDoc comment '*/'",
		"an identifier starting with a capital letter",
		"an identifier starting with a lowercase letter",
		"ORDINAL_FIELD_NAME",
		"'='",
		"','",
		"DOT",
		"CALDOC_NEWLINE",
		"CALDOC_WS",
		"CALDOC_NEWLINE_WITH_LEADING_ASTERISK",
		"CALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK_SPEC",
		"CALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK",
		"whitespace",
		"CALDOC_SEE_TAG_FUNCTION_CONTEXT",
		"CALDOC_SEE_TAG_MODULE_CONTEXT",
		"CALDOC_SEE_TAG_DATACONS_CONTEXT",
		"CALDOC_SEE_TAG_TYPECONS_CONTEXT",
		"CALDOC_SEE_TAG_TYPECLASS_CONTEXT",
		"CALDOC_SEE_TAG_CONTEXT",
		"CALDOC_SEE_TAG_CONS_ID",
		"CALDOC_SEE_TAG_VAR_ID",
		"'\\\"'",
		"'='",
		"','",
		"'.'",
		"CALDOC_AUTHOR_TAG",
		"CALDOC_DEPRECATED_TAG",
		"CALDOC_RETURN_TAG",
		"CALDOC_VERSION_TAG",
		"CALDOC_ARG_TAG",
		"CALDOC_ARG_TAG_VAR_ID",
		"CALDOC_ARG_TAG_ORDINAL_FIELD_NAME",
		"CALDOC_SEE_TAG",
		"CALDOC_OPEN_INLINE_TAG",
		"CALDOC_CLOSE_INLINE_TAG",
		"CALDOC_INLINE_SUMMARY_TAG",
		"CALDOC_INLINE_EM_TAG",
		"CALDOC_INLINE_STRONG_TAG",
		"CALDOC_INLINE_SUP_TAG",
		"CALDOC_INLINE_SUB_TAG",
		"CALDOC_INLINE_UNORDERED_LIST_TAG",
		"CALDOC_INLINE_ORDERED_LIST_TAG",
		"CALDOC_INLINE_ITEM_TAG",
		"CALDOC_INLINE_CODE_TAG",
		"CALDOC_INLINE_URL_TAG",
		"CALDOC_INLINE_LINK_TAG",
		"CALDOC_INLINE_TAG",
		"CALDOC_INLINE_UNKNOWN_TAG",
		"CALDOC_UNKNOWN_TAG",
		"CALDOC_SEE_TAG_UNKNOWN_CONTEXT",
		"CALDOC_SEE_TAG_UNKNOWN_REFERENCE",
		"CALDOC_REGULAR_TEXT_LINE",
		"CALDOC_BLANK_TEXT_LINE",
		"CALDOC_TEXT_LINE",
		"TOP_LEVEL_FUNCTION_DEFN",
		"FUNCTION_PARAM_LIST",
		"STRICT_PARAM",
		"LAZY_PARAM",
		"LAMBDA_DEFN",
		"APPLICATION",
		"ALT_LIST",
		"ALT",
		"LET_DEFN_LIST",
		"LET_DEFN",
		"LET_PATTERN_MATCH_DECL",
		"TOP_LEVEL_TYPE_DECLARATION",
		"LET_DEFN_TYPE_DECLARATION",
		"TYPE_DECLARATION",
		"FUNCTION_TYPE_CONSTRUCTOR",
		"TYPE_APPLICATION",
		"TUPLE_CONSTRUCTOR",
		"LIST_CONSTRUCTOR",
		"DATA_DECLARATION",
		"TYPE_CONS_PARAM_LIST",
		"DATA_CONSTRUCTOR_DEFN_LIST",
		"DATA_CONSTRUCTOR_DEFN",
		"DATA_CONSTRUCTOR_ARG_LIST",
		"DATA_CONSTRUCTOR_NAMED_ARG",
		"TYPE_CONTEXT_LIST",
		"TYPE_CONTEXT_SINGLETON",
		"TYPE_CONTEXT_NOTHING",
		"CLASS_CONTEXT_LIST",
		"CLASS_CONTEXT_SINGLETON",
		"CLASS_CONTEXT_NOTHING",
		"CLASS_CONTEXT",
		"LACKS_FIELD_CONTEXT",
		"FRIEND_DECLARATION_LIST",
		"IMPORT_DECLARATION_LIST",
		"OUTER_DEFN_LIST",
		"ACCESS_MODIFIER",
		"QUALIFIED_VAR",
		"QUALIFIED_CONS",
		"HIERARCHICAL_MODULE_NAME",
		"HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER",
		"COMPILATION_UNIT",
		"MODULE_DEFN",
		"PATTERN_CONSTRUCTOR",
		"UNIT_TYPE_CONSTRUCTOR",
		"LIST_TYPE_CONSTRUCTOR",
		"TUPLE_TYPE_CONSTRUCTOR",
		"INT_PATTERN",
		"CHAR_PATTERN",
		"FOREIGN_FUNCTION_DECLARATION",
		"PRIMITIVE_FUNCTION_DECLARATION",
		"FOREIGN_DATA_DECLARATION",
		"TYPE_CLASS_DEFN",
		"CLASS_METHOD_LIST",
		"CLASS_METHOD",
		"INSTANCE_DEFN",
		"INSTANCE_NAME",
		"INSTANCE_METHOD_LIST",
		"INSTANCE_METHOD",
		"UNPARENTHESIZED_TYPE_CONSTRUCTOR",
		"GENERAL_TYPE_CONSTRUCTOR",
		"PATTERN_VAR_LIST",
		"DATA_CONSTRUCTOR_NAME_LIST",
		"DATA_CONSTRUCTOR_NAME_SINGLETON",
		"MAYBE_MINUS_INT_LIST",
		"CHAR_LIST",
		"RECORD_CONSTRUCTOR",
		"BASE_RECORD",
		"FIELD_MODIFICATION_LIST",
		"FIELD_EXTENSION",
		"FIELD_VALUE_UPDATE",
		"RECORD_TYPE_CONSTRUCTOR",
		"RECORD_VAR",
		"FIELD_TYPE_ASSIGNMENT_LIST",
		"FIELD_TYPE_ASSIGNMENT",
		"SELECT_RECORD_FIELD",
		"SELECT_DATA_CONSTRUCTOR_FIELD",
		"RECORD_PATTERN",
		"BASE_RECORD_PATTERN",
		"FIELD_BINDING_VAR_ASSIGNMENT_LIST",
		"FIELD_BINDING_VAR_ASSIGNMENT",
		"TYPE_SIGNATURE",
		"STRICT_ARG",
		"EXPRESSION_TYPE_SIGNATURE",
		"UNARY_MINUS",
		"OPTIONAL_CALDOC_COMMENT",
		"CALDOC_COMMENT",
		"CALDOC_TEXT",
		"CALDOC_DESCRIPTION_BLOCK",
		"CALDOC_TAGGED_BLOCKS",
		"CALDOC_AUTHOR_BLOCK",
		"CALDOC_DEPRECATED_BLOCK",
		"CALDOC_RETURN_BLOCK",
		"CALDOC_VERSION_BLOCK",
		"CALDOC_ARG_BLOCK",
		"CALDOC_SEE_BLOCK",
		"CALDOC_SEE_FUNCTION_BLOCK",
		"CALDOC_SEE_MODULE_BLOCK",
		"CALDOC_SEE_DATACONS_BLOCK",
		"CALDOC_SEE_TYPECONS_BLOCK",
		"CALDOC_SEE_TYPECLASS_BLOCK",
		"CALDOC_SEE_BLOCK_WITHOUT_CONTEXT",
		"CALDOC_CHECKED_MODULE_NAME",
		"CALDOC_UNCHECKED_MODULE_NAME",
		"CALDOC_CHECKED_QUALIFIED_VAR",
		"CALDOC_UNCHECKED_QUALIFIED_VAR",
		"CALDOC_CHECKED_QUALIFIED_CONS",
		"CALDOC_UNCHECKED_QUALIFIED_CONS",
		"CALDOC_TEXT_PREFORMATTED_BLOCK",
		"CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS",
		"CALDOC_TEXT_LINE_BREAK",
		"CALDOC_TEXT_INLINE_BLOCK",
		"CALDOC_TEXT_URL",
		"CALDOC_TEXT_LINK",
		"CALDOC_TEXT_EMPHASIZED_TEXT",
		"CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT",
		"CALDOC_TEXT_SUPERSCRIPT_TEXT",
		"CALDOC_TEXT_SUBSCRIPT_TEXT",
		"CALDOC_TEXT_SUMMARY",
		"CALDOC_TEXT_CODE_BLOCK",
		"CALDOC_TEXT_ORDERED_LIST",
		"CALDOC_TEXT_UNORDERED_LIST",
		"CALDOC_TEXT_LIST_ITEM",
		"CALDOC_TEXT_LINK_FUNCTION",
		"CALDOC_TEXT_LINK_MODULE",
		"CALDOC_TEXT_LINK_DATACONS",
		"CALDOC_TEXT_LINK_TYPECONS",
		"CALDOC_TEXT_LINK_TYPECLASS",
		"CALDOC_TEXT_LINK_WITHOUT_CONTEXT",
		"VIRTUAL_LET_NONREC",
		"VIRTUAL_LET_REC",
		"VIRTUAL_DATA_CONSTRUCTOR_CASE",
		"VIRTUAL_RECORD_CASE",
		"VIRTUAL_TUPLE_CASE",
		"VIRTUAL_UNIT_DATA_CONSTRUCTOR",
		"\"module\"",
		"';'",
		"\"import\"",
		"\"using\"",
		"\"function\"",
		"\"typeConstructor\"",
		"\"dataConstructor\"",
		"\"typeClass\"",
		"\"friend\"",
		"\"class\"",
		"'::'",
		"'=>'",
		"'('",
		"')'",
		"'\\'",
		"'->'",
		"'['",
		"']'",
		"'{'",
		"'}'",
		"'|'",
		"\"data\"",
		"\"foreign\"",
		"\"unsafe\"",
		"\"jvm\"",
		"'!'",
		"\"deriving\"",
		"\"where\"",
		"\"default\"",
		"\"instance\"",
		"a string literal",
		"\"primitive\"",
		"\"public\"",
		"\"private\"",
		"\"protected\"",
		"\"let\"",
		"\"in\"",
		"\"if\"",
		"\"then\"",
		"\"else\"",
		"\"case\"",
		"\"of\"",
		"'$'",
		"'||'",
		"'&&'",
		"'<'",
		"'<='",
		"'=='",
		"'!='",
		"'>='",
		"'>'",
		"':'",
		"'++'",
		"'+'",
		"'-'",
		"'*'",
		"'/'",
		"'%'",
		"'#'",
		"'`'",
		"':='",
		"'_'",
		"a character literal",
		"an integer value",
		"FLOAT_LITERAL",
		"whitespace",
		"a comment",
		"a comment",
		"ESC",
		"HEX_DIGIT",
		"EXPONENT"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[10];
		data[0]=192L;
		data[3]=-9187340695066992640L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[8];
		data[0]=1026L;
		data[3]=6047317377028L;
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[8];
		data[0]=192L;
		data[3]=663552L;
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[8];
		data[0]=1026L;
		data[3]=6047854247940L;
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[10];
		data[0]=192L;
		data[3]=-4575657155896925182L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[8];
		data[3]=2L;
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = new long[8];
		data[0]=146L;
		data[3]=65510835720L;
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = new long[8];
		data[0]=146L;
		data[3]=65510835712L;
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = new long[8];
		data[0]=146L;
		data[3]=65510835200L;
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = new long[8];
		data[0]=144L;
		data[3]=65510835200L;
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = new long[8];
		data[0]=144L;
		data[3]=61203283968L;
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = new long[8];
		data[0]=146L;
		data[3]=61203283968L;
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = new long[8];
		data[0]=128L;
		data[3]=60129542144L;
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = new long[8];
		data[0]=640L;
		data[3]=67108864L;
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 128L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = new long[8];
		data[3]=1073741824L;
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 64L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = new long[8];
		data[0]=9007919802616864L;
		data[3]=20L;
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = new long[8];
		data[3]=8L;
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = new long[8];
		data[3]=20L;
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = new long[8];
		data[3]=4L;
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = new long[8];
		data[3]=16L;
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = new long[8];
		data[3]=480L;
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = new long[8];
		data[3]=484L;
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = new long[10];
		data[0]=9007919802617826L;
		data[3]=-2305845754901600236L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = new long[10];
		data[0]=441353758908112866L;
		data[3]=-2745620760572L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = new long[8];
		data[3]=512L;
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = new long[8];
		data[0]=64L;
		data[3]=60129542144L;
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = new long[8];
		data[3]=60129543168L;
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = new long[8];
		data[0]=64L;
		data[3]=9216L;
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = new long[8];
		data[3]=4194304L;
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = new long[8];
		data[0]=192L;
		data[3]=62277026816L;
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	private static final long[] mk_tokenSet_33() {
		long[] data = new long[8];
		data[0]=192L;
		data[3]=2147484672L;
		return data;
	}
	public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	private static final long[] mk_tokenSet_34() {
		long[] data = new long[8];
		data[3]=4294967296L;
		return data;
	}
	public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	private static final long[] mk_tokenSet_35() {
		long[] data = new long[8];
		data[3]=8388608L;
		return data;
	}
	public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	private static final long[] mk_tokenSet_36() {
		long[] data = { 144L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_36 = new BitSet(mk_tokenSet_36());
	private static final long[] mk_tokenSet_37() {
		long[] data = new long[10];
		data[0]=210L;
		data[3]=-4575657018457971712L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_37 = new BitSet(mk_tokenSet_37());
	private static final long[] mk_tokenSet_38() {
		long[] data = new long[10];
		data[0]=208L;
		data[3]=-4575657083968806912L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_38 = new BitSet(mk_tokenSet_38());
	private static final long[] mk_tokenSet_39() {
		long[] data = new long[8];
		data[0]=2176L;
		data[3]=32768L;
		return data;
	}
	public static final BitSet _tokenSet_39 = new BitSet(mk_tokenSet_39());
	private static final long[] mk_tokenSet_40() {
		long[] data = new long[8];
		data[0]=192L;
		data[3]=16384L;
		return data;
	}
	public static final BitSet _tokenSet_40 = new BitSet(mk_tokenSet_40());
	private static final long[] mk_tokenSet_41() {
		long[] data = new long[8];
		data[0]=3522L;
		data[3]=6047854977028L;
		return data;
	}
	public static final BitSet _tokenSet_41 = new BitSet(mk_tokenSet_41());
	private static final long[] mk_tokenSet_42() {
		long[] data = { 192L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_42 = new BitSet(mk_tokenSet_42());
	private static final long[] mk_tokenSet_43() {
		long[] data = new long[8];
		data[0]=1024L;
		data[3]=20480L;
		return data;
	}
	public static final BitSet _tokenSet_43 = new BitSet(mk_tokenSet_43());
	private static final long[] mk_tokenSet_44() {
		long[] data = new long[8];
		data[3]=16384L;
		return data;
	}
	public static final BitSet _tokenSet_44 = new BitSet(mk_tokenSet_44());
	private static final long[] mk_tokenSet_45() {
		long[] data = new long[8];
		data[0]=2240L;
		data[3]=663552L;
		return data;
	}
	public static final BitSet _tokenSet_45 = new BitSet(mk_tokenSet_45());
	private static final long[] mk_tokenSet_46() {
		long[] data = new long[8];
		data[0]=64L;
		data[3]=8192L;
		return data;
	}
	public static final BitSet _tokenSet_46 = new BitSet(mk_tokenSet_46());
	private static final long[] mk_tokenSet_47() {
		long[] data = new long[8];
		data[0]=64L;
		data[3]=16384L;
		return data;
	}
	public static final BitSet _tokenSet_47 = new BitSet(mk_tokenSet_47());
	private static final long[] mk_tokenSet_48() {
		long[] data = new long[10];
		data[0]=9007919802617826L;
		data[3]=-2305845754901600252L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_48 = new BitSet(mk_tokenSet_48());
	private static final long[] mk_tokenSet_49() {
		long[] data = { 384L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_49 = new BitSet(mk_tokenSet_49());
	private static final long[] mk_tokenSet_50() {
		long[] data = new long[10];
		data[0]=441353209085189858L;
		data[3]=-4611688765054879740L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_50 = new BitSet(mk_tokenSet_50());
	private static final long[] mk_tokenSet_51() {
		long[] data = new long[8];
		data[0]=1026L;
		data[3]=6047854313476L;
		return data;
	}
	public static final BitSet _tokenSet_51 = new BitSet(mk_tokenSet_51());
	private static final long[] mk_tokenSet_52() {
		long[] data = new long[8];
		data[0]=1474L;
		data[3]=6047989194756L;
		return data;
	}
	public static final BitSet _tokenSet_52 = new BitSet(mk_tokenSet_52());
	private static final long[] mk_tokenSet_53() {
		long[] data = new long[8];
		data[0]=192L;
		data[3]=679936L;
		return data;
	}
	public static final BitSet _tokenSet_53 = new BitSet(mk_tokenSet_53());
	private static final long[] mk_tokenSet_54() {
		long[] data = new long[8];
		data[3]=524288L;
		return data;
	}
	public static final BitSet _tokenSet_54 = new BitSet(mk_tokenSet_54());
	private static final long[] mk_tokenSet_55() {
		long[] data = new long[8];
		data[0]=384L;
		data[3]=1048576L;
		return data;
	}
	public static final BitSet _tokenSet_55 = new BitSet(mk_tokenSet_55());
	private static final long[] mk_tokenSet_56() {
		long[] data = new long[8];
		data[0]=1474L;
		data[3]=6047989196804L;
		return data;
	}
	public static final BitSet _tokenSet_56 = new BitSet(mk_tokenSet_56());
	private static final long[] mk_tokenSet_57() {
		long[] data = new long[8];
		data[3]=1048576L;
		return data;
	}
	public static final BitSet _tokenSet_57 = new BitSet(mk_tokenSet_57());
	private static final long[] mk_tokenSet_58() {
		long[] data = new long[8];
		data[0]=1024L;
		data[3]=1048576L;
		return data;
	}
	public static final BitSet _tokenSet_58 = new BitSet(mk_tokenSet_58());
	private static final long[] mk_tokenSet_59() {
		long[] data = new long[8];
		data[0]=16L;
		data[3]=4194304L;
		return data;
	}
	public static final BitSet _tokenSet_59 = new BitSet(mk_tokenSet_59());
	private static final long[] mk_tokenSet_60() {
		long[] data = new long[8];
		data[0]=192L;
		data[3]=60129542144L;
		return data;
	}
	public static final BitSet _tokenSet_60 = new BitSet(mk_tokenSet_60());
	private static final long[] mk_tokenSet_61() {
		long[] data = new long[8];
		data[3]=134217732L;
		return data;
	}
	public static final BitSet _tokenSet_61 = new BitSet(mk_tokenSet_61());
	private static final long[] mk_tokenSet_62() {
		long[] data = { 512L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_62 = new BitSet(mk_tokenSet_62());
	private static final long[] mk_tokenSet_63() {
		long[] data = new long[8];
		data[0]=80L;
		data[3]=60129542144L;
		return data;
	}
	public static final BitSet _tokenSet_63 = new BitSet(mk_tokenSet_63());
	private static final long[] mk_tokenSet_64() {
		long[] data = new long[8];
		data[3]=134217728L;
		return data;
	}
	public static final BitSet _tokenSet_64 = new BitSet(mk_tokenSet_64());
	private static final long[] mk_tokenSet_65() {
		long[] data = { 640L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_65 = new BitSet(mk_tokenSet_65());
	private static final long[] mk_tokenSet_66() {
		long[] data = new long[8];
		data[3]=136314884L;
		return data;
	}
	public static final BitSet _tokenSet_66 = new BitSet(mk_tokenSet_66());
	private static final long[] mk_tokenSet_67() {
		long[] data = new long[8];
		data[0]=384L;
		data[3]=136314884L;
		return data;
	}
	public static final BitSet _tokenSet_67 = new BitSet(mk_tokenSet_67());
	private static final long[] mk_tokenSet_68() {
		long[] data = new long[8];
		data[0]=192L;
		data[3]=67772416L;
		return data;
	}
	public static final BitSet _tokenSet_68 = new BitSet(mk_tokenSet_68());
	private static final long[] mk_tokenSet_69() {
		long[] data = new long[8];
		data[0]=16L;
		data[3]=60129543168L;
		return data;
	}
	public static final BitSet _tokenSet_69 = new BitSet(mk_tokenSet_69());
	private static final long[] mk_tokenSet_70() {
		long[] data = new long[8];
		data[3]=268435456L;
		return data;
	}
	public static final BitSet _tokenSet_70 = new BitSet(mk_tokenSet_70());
	private static final long[] mk_tokenSet_71() {
		long[] data = new long[8];
		data[0]=144L;
		data[3]=60129542144L;
		return data;
	}
	public static final BitSet _tokenSet_71 = new BitSet(mk_tokenSet_71());
	private static final long[] mk_tokenSet_72() {
		long[] data = new long[8];
		data[0]=144L;
		data[3]=60129542148L;
		return data;
	}
	public static final BitSet _tokenSet_72 = new BitSet(mk_tokenSet_72());
	private static final long[] mk_tokenSet_73() {
		long[] data = new long[8];
		data[3]=2048L;
		return data;
	}
	public static final BitSet _tokenSet_73 = new BitSet(mk_tokenSet_73());
	private static final long[] mk_tokenSet_74() {
		long[] data = new long[10];
		data[0]=9007919802617570L;
		data[3]=-6917531774268577788L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_74 = new BitSet(mk_tokenSet_74());
	private static final long[] mk_tokenSet_75() {
		long[] data = new long[8];
		data[0]=16L;
		data[3]=1073741824L;
		return data;
	}
	public static final BitSet _tokenSet_75 = new BitSet(mk_tokenSet_75());
	private static final long[] mk_tokenSet_76() {
		long[] data = new long[8];
		data[0]=144L;
		data[3]=4L;
		return data;
	}
	public static final BitSet _tokenSet_76 = new BitSet(mk_tokenSet_76());
	private static final long[] mk_tokenSet_77() {
		long[] data = new long[8];
		data[0]=64L;
		data[3]=663552L;
		return data;
	}
	public static final BitSet _tokenSet_77 = new BitSet(mk_tokenSet_77());
	private static final long[] mk_tokenSet_78() {
		long[] data = new long[8];
		data[0]=16L;
		data[3]=8388608L;
		return data;
	}
	public static final BitSet _tokenSet_78 = new BitSet(mk_tokenSet_78());
	private static final long[] mk_tokenSet_79() {
		long[] data = new long[8];
		data[0]=16L;
		data[3]=4294967296L;
		return data;
	}
	public static final BitSet _tokenSet_79 = new BitSet(mk_tokenSet_79());
	private static final long[] mk_tokenSet_80() {
		long[] data = new long[8];
		data[0]=128L;
		data[3]=67108864L;
		return data;
	}
	public static final BitSet _tokenSet_80 = new BitSet(mk_tokenSet_80());
	private static final long[] mk_tokenSet_81() {
		long[] data = new long[8];
		data[0]=640L;
		data[3]=67174400L;
		return data;
	}
	public static final BitSet _tokenSet_81 = new BitSet(mk_tokenSet_81());
	private static final long[] mk_tokenSet_82() {
		long[] data = new long[8];
		data[3]=65536L;
		return data;
	}
	public static final BitSet _tokenSet_82 = new BitSet(mk_tokenSet_82());
	private static final long[] mk_tokenSet_83() {
		long[] data = new long[10];
		data[0]=208L;
		data[3]=-4575657221407760384L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_83 = new BitSet(mk_tokenSet_83());
	private static final long[] mk_tokenSet_84() {
		long[] data = new long[8];
		data[3]=137438953472L;
		return data;
	}
	public static final BitSet _tokenSet_84 = new BitSet(mk_tokenSet_84());
	private static final long[] mk_tokenSet_85() {
		long[] data = new long[10];
		data[0]=192L;
		data[3]=-4575657221407760384L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_85 = new BitSet(mk_tokenSet_85());
	private static final long[] mk_tokenSet_86() {
		long[] data = new long[10];
		data[0]=192L;
		data[3]=-9187343237687664640L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_86 = new BitSet(mk_tokenSet_86());
	private static final long[] mk_tokenSet_87() {
		long[] data = new long[8];
		data[0]=1026L;
		data[3]=6047317379076L;
		return data;
	}
	public static final BitSet _tokenSet_87 = new BitSet(mk_tokenSet_87());
	private static final long[] mk_tokenSet_88() {
		long[] data = new long[8];
		data[0]=1026L;
		data[3]=14843410401284L;
		return data;
	}
	public static final BitSet _tokenSet_88 = new BitSet(mk_tokenSet_88());
	private static final long[] mk_tokenSet_89() {
		long[] data = new long[8];
		data[0]=1026L;
		data[3]=32435596445700L;
		return data;
	}
	public static final BitSet _tokenSet_89 = new BitSet(mk_tokenSet_89());
	private static final long[] mk_tokenSet_90() {
		long[] data = new long[8];
		data[0]=1026L;
		data[3]=67619968534532L;
		return data;
	}
	public static final BitSet _tokenSet_90 = new BitSet(mk_tokenSet_90());
	private static final long[] mk_tokenSet_91() {
		long[] data = new long[8];
		data[0]=1538L;
		data[3]=4500850851727364L;
		return data;
	}
	public static final BitSet _tokenSet_91 = new BitSet(mk_tokenSet_91());
	private static final long[] mk_tokenSet_92() {
		long[] data = new long[8];
		data[0]=1538L;
		data[3]=18011649733838852L;
		return data;
	}
	public static final BitSet _tokenSet_92 = new BitSet(mk_tokenSet_92());
	private static final long[] mk_tokenSet_93() {
		long[] data = new long[8];
		data[0]=1538L;
		data[3]=72054845262284804L;
		return data;
	}
	public static final BitSet _tokenSet_93 = new BitSet(mk_tokenSet_93());
	private static final long[] mk_tokenSet_94() {
		long[] data = new long[8];
		data[0]=1538L;
		data[3]=576458003527780356L;
		return data;
	}
	public static final BitSet _tokenSet_94 = new BitSet(mk_tokenSet_94());
	private static final long[] mk_tokenSet_95() {
		long[] data = new long[10];
		data[0]=192L;
		data[3]=-9223372034706628608L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_95 = new BitSet(mk_tokenSet_95());
	private static final long[] mk_tokenSet_96() {
		long[] data = new long[8];
		data[0]=1538L;
		data[3]=2305840260438050820L;
		return data;
	}
	public static final BitSet _tokenSet_96 = new BitSet(mk_tokenSet_96());
	private static final long[] mk_tokenSet_97() {
		long[] data = new long[8];
		data[3]=1152921504606846976L;
		return data;
	}
	public static final BitSet _tokenSet_97 = new BitSet(mk_tokenSet_97());
	private static final long[] mk_tokenSet_98() {
		long[] data = new long[10];
		data[0]=1730L;
		data[3]=-6917531774268577788L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_98 = new BitSet(mk_tokenSet_98());
	private static final long[] mk_tokenSet_99() {
		long[] data = new long[10];
		data[0]=3778L;
		data[3]=-6917531774268577788L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_99 = new BitSet(mk_tokenSet_99());
	private static final long[] mk_tokenSet_100() {
		long[] data = new long[10];
		data[3]=-9223372034707292160L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_100 = new BitSet(mk_tokenSet_100());
	private static final long[] mk_tokenSet_101() {
		long[] data = new long[10];
		data[0]=192L;
		data[3]=-9187340695066714112L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_101 = new BitSet(mk_tokenSet_101());
	private static final long[] mk_tokenSet_102() {
		long[] data = new long[8];
		data[3]=278528L;
		return data;
	}
	public static final BitSet _tokenSet_102 = new BitSet(mk_tokenSet_102());
	private static final long[] mk_tokenSet_103() {
		long[] data = new long[10];
		data[0]=3024L;
		data[3]=-2305849260467361792L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_103 = new BitSet(mk_tokenSet_103());
	private static final long[] mk_tokenSet_104() {
		long[] data = new long[10];
		data[0]=3778L;
		data[3]=-4611688765054883836L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_104 = new BitSet(mk_tokenSet_104());
	private static final long[] mk_tokenSet_105() {
		long[] data = new long[10];
		data[0]=448L;
		data[3]=-9187340695065944064L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_105 = new BitSet(mk_tokenSet_105());
	private static final long[] mk_tokenSet_106() {
		long[] data = new long[10];
		data[0]=3008L;
		data[3]=-4571153621779202048L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_106 = new BitSet(mk_tokenSet_106());
	private static final long[] mk_tokenSet_107() {
		long[] data = new long[8];
		data[0]=128L;
		data[3]=4611686018427404288L;
		return data;
	}
	public static final BitSet _tokenSet_107 = new BitSet(mk_tokenSet_107());
	private static final long[] mk_tokenSet_108() {
		long[] data = new long[8];
		data[0]=640L;
		data[3]=4611686018427977728L;
		return data;
	}
	public static final BitSet _tokenSet_108 = new BitSet(mk_tokenSet_108());
	private static final long[] mk_tokenSet_109() {
		long[] data = new long[8];
		data[0]=512L;
		data[3]=65536L;
		return data;
	}
	public static final BitSet _tokenSet_109 = new BitSet(mk_tokenSet_109());
	private static final long[] mk_tokenSet_110() {
		long[] data = new long[8];
		data[0]=128L;
		data[3]=4611686018427387904L;
		return data;
	}
	public static final BitSet _tokenSet_110 = new BitSet(mk_tokenSet_110());
	private static final long[] mk_tokenSet_111() {
		long[] data = new long[8];
		data[0]=1664L;
		data[3]=4616189618057986048L;
		return data;
	}
	public static final BitSet _tokenSet_111 = new BitSet(mk_tokenSet_111());
	private static final long[] mk_tokenSet_112() {
		long[] data = new long[8];
		data[3]=8192L;
		return data;
	}
	public static final BitSet _tokenSet_112 = new BitSet(mk_tokenSet_112());
	private static final long[] mk_tokenSet_113() {
		long[] data = new long[10];
		data[3]=36028797018963968L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_113 = new BitSet(mk_tokenSet_113());
	private static final long[] mk_tokenSet_114() {
		long[] data = new long[10];
		data[0]=1218L;
		data[3]=-4575651174090383356L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_114 = new BitSet(mk_tokenSet_114());
	private static final long[] mk_tokenSet_115() {
		long[] data = new long[8];
		data[0]=640L;
		data[3]=4611686018427453440L;
		return data;
	}
	public static final BitSet _tokenSet_115 = new BitSet(mk_tokenSet_115());
	private static final long[] mk_tokenSet_116() {
		long[] data = new long[8];
		data[0]=512L;
		data[3]=2179072L;
		return data;
	}
	public static final BitSet _tokenSet_116 = new BitSet(mk_tokenSet_116());
	private static final long[] mk_tokenSet_117() {
		long[] data = new long[8];
		data[0]=1536L;
		data[3]=1114112L;
		return data;
	}
	public static final BitSet _tokenSet_117 = new BitSet(mk_tokenSet_117());
	private static final long[] mk_tokenSet_118() {
		long[] data = new long[8];
		data[0]=384L;
		data[3]=4611686018428436480L;
		return data;
	}
	public static final BitSet _tokenSet_118 = new BitSet(mk_tokenSet_118());
	private static final long[] mk_tokenSet_119() {
		long[] data = new long[10];
		data[0]=208L;
		data[3]=-4575657155896925182L;
		data[4]=1L;
		return data;
	}
	public static final BitSet _tokenSet_119 = new BitSet(mk_tokenSet_119());
	private static final long[] mk_tokenSet_120() {
		long[] data = { 9007369979691040L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_120 = new BitSet(mk_tokenSet_120());
	private static final long[] mk_tokenSet_121() {
		long[] data = { 32L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_121 = new BitSet(mk_tokenSet_121());
	private static final long[] mk_tokenSet_122() {
		long[] data = { 9007919735504928L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_122 = new BitSet(mk_tokenSet_122());
	private static final long[] mk_tokenSet_123() {
		long[] data = { 441353758840999968L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_123 = new BitSet(mk_tokenSet_123());
	private static final long[] mk_tokenSet_124() {
		long[] data = { 549755813888L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_124 = new BitSet(mk_tokenSet_124());
	private static final long[] mk_tokenSet_125() {
		long[] data = { 432345839105495040L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_125 = new BitSet(mk_tokenSet_125());
	private static final long[] mk_tokenSet_126() {
		long[] data = { 432346388861308928L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_126 = new BitSet(mk_tokenSet_126());
	private static final long[] mk_tokenSet_127() {
		long[] data = { 67109056L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_127 = new BitSet(mk_tokenSet_127());
	private static final long[] mk_tokenSet_128() {
		long[] data = { 549755816128L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_128 = new BitSet(mk_tokenSet_128());
	private static final long[] mk_tokenSet_129() {
		long[] data = { 144115462953783296L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_129 = new BitSet(mk_tokenSet_129());
	private static final long[] mk_tokenSet_130() {
		long[] data = { 144116012709597184L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_130 = new BitSet(mk_tokenSet_130());
	private static final long[] mk_tokenSet_131() {
		long[] data = { 434596814360698560L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_131 = new BitSet(mk_tokenSet_131());
	private static final long[] mk_tokenSet_132() {
		long[] data = { 9007919735505952L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_132 = new BitSet(mk_tokenSet_132());
	private static final long[] mk_tokenSet_133() {
		long[] data = { 9007919735508192L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_133 = new BitSet(mk_tokenSet_133());
	private static final long[] mk_tokenSet_134() {
		long[] data = { 9007919735508064L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_134 = new BitSet(mk_tokenSet_134());
	private static final long[] mk_tokenSet_135() {
		long[] data = { 9007369979694304L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_135 = new BitSet(mk_tokenSet_135());
	private static final long[] mk_tokenSet_136() {
		long[] data = { 448108058845667040L, 0L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_136 = new BitSet(mk_tokenSet_136());
	
	}
