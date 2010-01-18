// $ANTLR 2.7.6 (2005-12-22): "CALDoc.g" -> "CALDocLexer.java"$

// Package declaration
package org.openquark.cal.compiler;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

@SuppressWarnings(\u0022all\u0022) final class CALDocLexer extends antlr.CharScanner implements CALDocTokenTypes, TokenStream
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

public CALDocLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public CALDocLexer(Reader in) {
	this(new CharBuffer(in));
}
public CALDocLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public CALDocLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				if (((LA(1)=='#') && ((LA(2) >= '1' && LA(2) <= '9')) && (true) && (true) && (true) && (true))&&(this.state == LexerState.ARG_NAME)) {
					mCALDOC_ARG_TAG_ORDINAL_FIELD_NAME(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1)=='{') && (LA(2)=='@') && (true) && (true) && (true) && (true))&&(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST)) {
					mCALDOC_OPEN_INLINE_TAG(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1)=='@') && (LA(2)=='}') && (true) && (true) && (true) && (true))&&(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.INLINE_TAG)) {
					mCALDOC_CLOSE_INLINE_TAG(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1)=='\n'||LA(1)=='\r') && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.REGULAR)) {
					mCALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK(true);
					theRetToken=_returnToken;
				}
				else if (((_tokenSet_0.member(LA(1))) && (true) && (true) && (true) && (true) && (true))&&(this.state != LexerState.REGULAR)) {
					mCALDOC_IGNORED_DOC_WS(true);
					theRetToken=_returnToken;
				}
				else if (((_tokenSet_1.member(LA(1))) && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.SEE_TAG_CONTEXT)) {
					mCALDOC_SEE_TAG_CONTEXT(true);
					theRetToken=_returnToken;
				}
				else if ((((LA(1) >= 'A' && LA(1) <= 'Z')) && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT)) {
					mCALDOC_SEE_TAG_CONS_ID(true);
					theRetToken=_returnToken;
				}
				else if ((((LA(1) >= 'a' && LA(1) <= 'z')) && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT)) {
					mCALDOC_SEE_TAG_VAR_ID(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1)=='"') && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT)) {
					mCALDOC_SEE_TAG_QUOTE(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1)=='=') && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT)) {
					mCALDOC_SEE_TAG_EQUALS(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1)==',') && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.SEE_TAG_LIST)) {
					mCALDOC_SEE_TAG_COMMA(true);
					theRetToken=_returnToken;
				}
				else if (((LA(1)=='.') && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.SEE_TAG_LIST)) {
					mCALDOC_SEE_TAG_DOT(true);
					theRetToken=_returnToken;
				}
				else if ((((LA(1) >= 'a' && LA(1) <= 'z')) && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.ARG_NAME)) {
					mCALDOC_ARG_TAG_VAR_ID(true);
					theRetToken=_returnToken;
				}
				else if (((_tokenSet_2.member(LA(1))) && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.INLINE_TAG)) {
					mCALDOC_INLINE_TAG(true);
					theRetToken=_returnToken;
				}
				else if ((_tokenSet_3.member(LA(1))) && (true) && (true) && (true) && (true) && (true)) {
					mCALDOC_TEXT_LINE(true);
					theRetToken=_returnToken;
				}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				reportError(e);
				consume();
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	protected final void mCALDOC_CLOSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_CLOSE;
		int _saveIndex;
		
		try {      // for error handling
			match("*/");
			if ( inputState.guessing==0 ) {
				parentMultiLexer.switchOutOfCALDocLexer(); resetState();
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_NEWLINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_NEWLINE;
		int _saveIndex;
		
		try {      // for error handling
			{
			if ((LA(1)=='\r') && (LA(2)=='\n') && (true) && (true) && (true) && (true)) {
				match("\r\n");
			}
			else if ((LA(1)=='\r') && (true) && (true) && (true) && (true) && (true)) {
				match('\r');
			}
			else if ((LA(1)=='\n')) {
				match('\n');
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			if ( inputState.guessing==0 ) {
				newline();
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
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_WS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_WS;
		int _saveIndex;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ' ':
			{
				match(' ');
				break;
			}
			case '\t':
			{
				match('\t');
				break;
			}
			case '\u000c':
			{
				match('\f');
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_6);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_NEWLINE_WITH_LEADING_ASTERISK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_NEWLINE_WITH_LEADING_ASTERISK;
		int _saveIndex;
		
		try {      // for error handling
			mCALDOC_NEWLINE(false);
			{
			_loop7:
			do {
				if ((LA(1)=='\t'||LA(1)=='\u000c'||LA(1)==' ')) {
					_saveIndex=text.length();
					mCALDOC_WS(false);
					text.setLength(_saveIndex);
				}
				else {
					break _loop7;
				}
				
			} while (true);
			}
			{
			int _cnt9=0;
			_loop9:
			do {
				if (((LA(1)=='*'))&&(LA(2) != '/')) {
					_saveIndex=text.length();
					match('*');
					text.setLength(_saveIndex);
				}
				else {
					if ( _cnt9>=1 ) { break _loop9; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt9++;
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK_SPEC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK_SPEC;
		int _saveIndex;
		
		try {      // for error handling
			boolean synPredMatched15 = false;
			if (((LA(1)=='\n'||LA(1)=='\r') && (_tokenSet_7.member(LA(2))) && (true) && (true) && (true) && (true))) {
				int _m15 = mark();
				synPredMatched15 = true;
				inputState.guessing++;
				try {
					{
					mCALDOC_NEWLINE(false);
					{
					_loop13:
					do {
						if ((LA(1)=='\t'||LA(1)=='\u000c'||LA(1)==' ')) {
							mCALDOC_WS(false);
						}
						else {
							break _loop13;
						}
						
					} while (true);
					}
					{
					if (!(LA(2) != '/'))
					  throw new SemanticException("LA(2) != '/'");
					match('*');
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched15 = false;
				}
				rewind(_m15);
inputState.guessing--;
			}
			if ( synPredMatched15 ) {
				mCALDOC_NEWLINE_WITH_LEADING_ASTERISK(false);
			}
			else if ((LA(1)=='\n'||LA(1)=='\r') && (true) && (true) && (true) && (true) && (true)) {
				mCALDOC_NEWLINE(false);
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_0);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR))
			  throw new SemanticException("this.state == LexerState.REGULAR");
			{
			boolean synPredMatched22 = false;
			if (((LA(1)=='\n'||LA(1)=='\r') && (_tokenSet_7.member(LA(2))) && (true) && (true) && (true) && (true))) {
				int _m22 = mark();
				synPredMatched22 = true;
				inputState.guessing++;
				try {
					{
					mCALDOC_NEWLINE(false);
					{
					_loop20:
					do {
						if ((LA(1)=='\t'||LA(1)=='\u000c'||LA(1)==' ')) {
							mCALDOC_WS(false);
						}
						else {
							break _loop20;
						}
						
					} while (true);
					}
					{
					if (!(LA(2) != '/'))
					  throw new SemanticException("LA(2) != '/'");
					match('*');
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched22 = false;
				}
				rewind(_m22);
inputState.guessing--;
			}
			if ( synPredMatched22 ) {
				mCALDOC_NEWLINE_WITH_LEADING_ASTERISK(false);
				if ( inputState.guessing==0 ) {
					_ttype = CALDOC_NEWLINE_WITH_LEADING_ASTERISK;
				}
			}
			else if ((LA(1)=='\n'||LA(1)=='\r') && (true) && (true) && (true) && (true) && (true)) {
				mCALDOC_NEWLINE(false);
				if ( inputState.guessing==0 ) {
					_ttype = CALDOC_NEWLINE;
				}
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCONS_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CONS_ID;
		int _saveIndex;
		
		try {      // for error handling
			{
			matchRange('A','Z');
			}
			{
			_loop26:
			do {
				switch ( LA(1)) {
				case 'a':  case 'b':  case 'c':  case 'd':
				case 'e':  case 'f':  case 'g':  case 'h':
				case 'i':  case 'j':  case 'k':  case 'l':
				case 'm':  case 'n':  case 'o':  case 'p':
				case 'q':  case 'r':  case 's':  case 't':
				case 'u':  case 'v':  case 'w':  case 'x':
				case 'y':  case 'z':
				{
					matchRange('a','z');
					break;
				}
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':
				{
					matchRange('A','Z');
					break;
				}
				case '_':
				{
					match('_');
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					matchRange('0','9');
					break;
				}
				default:
				{
					break _loop26;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		_ttype = testLiteralsTable(new String(text.getBuffer(),_begin,text.length()-_begin),_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mVAR_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = VAR_ID;
		int _saveIndex;
		
		try {      // for error handling
			{
			matchRange('a','z');
			}
			{
			_loop30:
			do {
				switch ( LA(1)) {
				case 'a':  case 'b':  case 'c':  case 'd':
				case 'e':  case 'f':  case 'g':  case 'h':
				case 'i':  case 'j':  case 'k':  case 'l':
				case 'm':  case 'n':  case 'o':  case 'p':
				case 'q':  case 'r':  case 's':  case 't':
				case 'u':  case 'v':  case 'w':  case 'x':
				case 'y':  case 'z':
				{
					matchRange('a','z');
					break;
				}
				case 'A':  case 'B':  case 'C':  case 'D':
				case 'E':  case 'F':  case 'G':  case 'H':
				case 'I':  case 'J':  case 'K':  case 'L':
				case 'M':  case 'N':  case 'O':  case 'P':
				case 'Q':  case 'R':  case 'S':  case 'T':
				case 'U':  case 'V':  case 'W':  case 'X':
				case 'Y':  case 'Z':
				{
					matchRange('A','Z');
					break;
				}
				case '_':
				{
					match('_');
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					matchRange('0','9');
					break;
				}
				default:
				{
					break _loop30;
				}
				}
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		_ttype = testLiteralsTable(new String(text.getBuffer(),_begin,text.length()-_begin),_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mORDINAL_FIELD_NAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ORDINAL_FIELD_NAME;
		int _saveIndex;
		
		try {      // for error handling
			{
			match('#');
			}
			{
			matchRange('1','9');
			}
			{
			_loop35:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					matchRange('0','9');
				}
				else {
					break _loop35;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_IGNORED_DOC_WS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_IGNORED_DOC_WS;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state != LexerState.REGULAR))
			  throw new SemanticException("this.state != LexerState.REGULAR");
			{
			int _cnt38=0;
			_loop38:
			do {
				switch ( LA(1)) {
				case '\t':  case '\u000c':  case ' ':
				{
					mCALDOC_WS(false);
					break;
				}
				case '\n':  case '\r':
				{
					mCALDOC_NEWLINE_WITH_OPTIONAL_LEADING_ASTERISK_SPEC(false);
					break;
				}
				default:
				{
					if ( _cnt38>=1 ) { break _loop38; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				_cnt38++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				_ttype = Token.SKIP;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_SEE_TAG_FUNCTION_CONTEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_FUNCTION_CONTEXT;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_CONTEXT");
			match("function");
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_SEE_TAG_MODULE_CONTEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_MODULE_CONTEXT;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_CONTEXT");
			match("module");
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_SEE_TAG_DATACONS_CONTEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_DATACONS_CONTEXT;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_CONTEXT");
			match("dataConstructor");
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_SEE_TAG_TYPECONS_CONTEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_TYPECONS_CONTEXT;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_CONTEXT");
			match("typeConstructor");
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_SEE_TAG_TYPECLASS_CONTEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_TYPECLASS_CONTEXT;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_CONTEXT");
			match("typeClass");
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_SEE_TAG_CONTEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_CONTEXT;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_CONTEXT");
			{
			boolean synPredMatched48 = false;
			if (((LA(1)=='f') && (LA(2)=='u') && (LA(3)=='n') && (LA(4)=='c') && (LA(5)=='t') && (LA(6)=='i'))) {
				int _m48 = mark();
				synPredMatched48 = true;
				inputState.guessing++;
				try {
					{
					mCALDOC_SEE_TAG_FUNCTION_CONTEXT(false);
					{
					switch ( LA(1)) {
					case '=':
					{
						match('=');
						break;
					}
					case ' ':
					{
						match(' ');
						break;
					}
					case '\t':
					{
						match('\t');
						break;
					}
					case '\u000c':
					{
						match('\f');
						break;
					}
					case '\n':
					{
						match('\n');
						break;
					}
					case '\r':
					{
						match('\r');
						break;
					}
					case '*':
					{
						match('*');
						break;
					}
					default:
					{
						throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
					}
					}
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched48 = false;
				}
				rewind(_m48);
inputState.guessing--;
			}
			if ( synPredMatched48 ) {
				mCALDOC_SEE_TAG_FUNCTION_CONTEXT(false);
				if ( inputState.guessing==0 ) {
					_ttype = CALDOC_SEE_TAG_FUNCTION_CONTEXT;
				}
			}
			else {
				boolean synPredMatched51 = false;
				if (((LA(1)=='m') && (LA(2)=='o') && (LA(3)=='d') && (LA(4)=='u') && (LA(5)=='l') && (LA(6)=='e'))) {
					int _m51 = mark();
					synPredMatched51 = true;
					inputState.guessing++;
					try {
						{
						mCALDOC_SEE_TAG_MODULE_CONTEXT(false);
						{
						switch ( LA(1)) {
						case '=':
						{
							match('=');
							break;
						}
						case ' ':
						{
							match(' ');
							break;
						}
						case '\t':
						{
							match('\t');
							break;
						}
						case '\u000c':
						{
							match('\f');
							break;
						}
						case '\n':
						{
							match('\n');
							break;
						}
						case '\r':
						{
							match('\r');
							break;
						}
						case '*':
						{
							match('*');
							break;
						}
						default:
						{
							throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
						}
						}
						}
						}
					}
					catch (RecognitionException pe) {
						synPredMatched51 = false;
					}
					rewind(_m51);
inputState.guessing--;
				}
				if ( synPredMatched51 ) {
					mCALDOC_SEE_TAG_MODULE_CONTEXT(false);
					if ( inputState.guessing==0 ) {
						_ttype = CALDOC_SEE_TAG_MODULE_CONTEXT;
					}
				}
				else {
					boolean synPredMatched54 = false;
					if (((LA(1)=='d') && (LA(2)=='a') && (LA(3)=='t') && (LA(4)=='a') && (LA(5)=='C') && (LA(6)=='o'))) {
						int _m54 = mark();
						synPredMatched54 = true;
						inputState.guessing++;
						try {
							{
							mCALDOC_SEE_TAG_DATACONS_CONTEXT(false);
							{
							switch ( LA(1)) {
							case '=':
							{
								match('=');
								break;
							}
							case ' ':
							{
								match(' ');
								break;
							}
							case '\t':
							{
								match('\t');
								break;
							}
							case '\u000c':
							{
								match('\f');
								break;
							}
							case '\n':
							{
								match('\n');
								break;
							}
							case '\r':
							{
								match('\r');
								break;
							}
							case '*':
							{
								match('*');
								break;
							}
							default:
							{
								throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
							}
							}
							}
							}
						}
						catch (RecognitionException pe) {
							synPredMatched54 = false;
						}
						rewind(_m54);
inputState.guessing--;
					}
					if ( synPredMatched54 ) {
						mCALDOC_SEE_TAG_DATACONS_CONTEXT(false);
						if ( inputState.guessing==0 ) {
							_ttype = CALDOC_SEE_TAG_DATACONS_CONTEXT;
						}
					}
					else {
						boolean synPredMatched57 = false;
						if (((LA(1)=='t') && (LA(2)=='y') && (LA(3)=='p') && (LA(4)=='e') && (LA(5)=='C') && (LA(6)=='o'))) {
							int _m57 = mark();
							synPredMatched57 = true;
							inputState.guessing++;
							try {
								{
								mCALDOC_SEE_TAG_TYPECONS_CONTEXT(false);
								{
								switch ( LA(1)) {
								case '=':
								{
									match('=');
									break;
								}
								case ' ':
								{
									match(' ');
									break;
								}
								case '\t':
								{
									match('\t');
									break;
								}
								case '\u000c':
								{
									match('\f');
									break;
								}
								case '\n':
								{
									match('\n');
									break;
								}
								case '\r':
								{
									match('\r');
									break;
								}
								case '*':
								{
									match('*');
									break;
								}
								default:
								{
									throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
								}
								}
								}
								}
							}
							catch (RecognitionException pe) {
								synPredMatched57 = false;
							}
							rewind(_m57);
inputState.guessing--;
						}
						if ( synPredMatched57 ) {
							mCALDOC_SEE_TAG_TYPECONS_CONTEXT(false);
							if ( inputState.guessing==0 ) {
								_ttype = CALDOC_SEE_TAG_TYPECONS_CONTEXT;
							}
						}
						else {
							boolean synPredMatched60 = false;
							if (((LA(1)=='t') && (LA(2)=='y') && (LA(3)=='p') && (LA(4)=='e') && (LA(5)=='C') && (LA(6)=='l'))) {
								int _m60 = mark();
								synPredMatched60 = true;
								inputState.guessing++;
								try {
									{
									mCALDOC_SEE_TAG_TYPECLASS_CONTEXT(false);
									{
									switch ( LA(1)) {
									case '=':
									{
										match('=');
										break;
									}
									case ' ':
									{
										match(' ');
										break;
									}
									case '\t':
									{
										match('\t');
										break;
									}
									case '\u000c':
									{
										match('\f');
										break;
									}
									case '\n':
									{
										match('\n');
										break;
									}
									case '\r':
									{
										match('\r');
										break;
									}
									case '*':
									{
										match('*');
										break;
									}
									default:
									{
										throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
									}
									}
									}
									}
								}
								catch (RecognitionException pe) {
									synPredMatched60 = false;
								}
								rewind(_m60);
inputState.guessing--;
							}
							if ( synPredMatched60 ) {
								mCALDOC_SEE_TAG_TYPECLASS_CONTEXT(false);
								if ( inputState.guessing==0 ) {
									_ttype = CALDOC_SEE_TAG_TYPECLASS_CONTEXT;
								}
							}
							else {
								boolean synPredMatched62 = false;
								if ((((LA(1) >= 'A' && LA(1) <= 'Z')) && (true) && (true) && (true) && (true) && (true))) {
									int _m62 = mark();
									synPredMatched62 = true;
									inputState.guessing++;
									try {
										{
										mCALDOC_SEE_TAG_CONS_ID(false);
										}
									}
									catch (RecognitionException pe) {
										synPredMatched62 = false;
									}
									rewind(_m62);
inputState.guessing--;
								}
								if ( synPredMatched62 ) {
									mCALDOC_SEE_TAG_CONS_ID(false);
									if ( inputState.guessing==0 ) {
										_ttype = CONS_ID;
									}
								}
								else {
									boolean synPredMatched64 = false;
									if ((((LA(1) >= 'a' && LA(1) <= 'z')) && (true) && (true) && (true) && (true) && (true))) {
										int _m64 = mark();
										synPredMatched64 = true;
										inputState.guessing++;
										try {
											{
											mCALDOC_SEE_TAG_VAR_ID(false);
											}
										}
										catch (RecognitionException pe) {
											synPredMatched64 = false;
										}
										rewind(_m64);
inputState.guessing--;
									}
									if ( synPredMatched64 ) {
										mCALDOC_SEE_TAG_VAR_ID(false);
										if ( inputState.guessing==0 ) {
											_ttype = VAR_ID;
										}
									}
									else {
										boolean synPredMatched66 = false;
										if (((LA(1)=='"') && (true) && (true) && (true) && (true) && (true))) {
											int _m66 = mark();
											synPredMatched66 = true;
											inputState.guessing++;
											try {
												{
												mCALDOC_SEE_TAG_QUOTE(false);
												}
											}
											catch (RecognitionException pe) {
												synPredMatched66 = false;
											}
											rewind(_m66);
inputState.guessing--;
										}
										if ( synPredMatched66 ) {
											mCALDOC_SEE_TAG_QUOTE(false);
											if ( inputState.guessing==0 ) {
												_ttype = CALDOC_SEE_TAG_QUOTE;
											}
										}
										else if ((_tokenSet_1.member(LA(1))) && (true) && (true) && (true) && (true) && (true)) {
											mCALDOC_SEE_TAG_UNKNOWN_CONTEXT(false);
											if ( inputState.guessing==0 ) {
												_ttype = CALDOC_SEE_TAG_UNKNOWN_CONTEXT;
											}
										}
										else {
											throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
										}
										}}}}}}}
										}
									}
									catch (RecognitionException ex) {
										if (inputState.guessing==0) {
											reportError(ex);
											recover(ex,_tokenSet_4);
										} else {
										  throw ex;
										}
									}
									if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
										_token = makeToken(_ttype);
										_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
									}
									_returnToken = _token;
								}
								
	public final void mCALDOC_SEE_TAG_CONS_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_CONS_ID;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT");
			mCONS_ID(false);
			if ( inputState.guessing==0 ) {
				_ttype = CONS_ID; this.state = LexerState.SEE_TAG_LIST;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_SEE_TAG_VAR_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_VAR_ID;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT");
			mVAR_ID(false);
			if ( inputState.guessing==0 ) {
				_ttype = VAR_ID; this.state = LexerState.SEE_TAG_LIST;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_SEE_TAG_QUOTE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_QUOTE;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT");
			match('\"');
			if ( inputState.guessing==0 ) {
				this.state = LexerState.SEE_TAG_LIST;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_SEE_TAG_UNKNOWN_CONTEXT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_UNKNOWN_CONTEXT;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_CONTEXT");
			{
			int _cnt143=0;
			_loop143:
			do {
				if ((_tokenSet_1.member(LA(1)))) {
					{
					{
					match(_tokenSet_1);
					}
					}
				}
				else {
					if ( _cnt143>=1 ) { break _loop143; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt143++;
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_SEE_TAG_EQUALS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_EQUALS;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.SEE_TAG_CONTEXT");
			match('=');
			if ( inputState.guessing==0 ) {
				_ttype = EQUALS; this.state = LexerState.SEE_TAG_LIST;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_SEE_TAG_COMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_COMMA;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_LIST");
			match(',');
			if ( inputState.guessing==0 ) {
				_ttype = COMMA;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_SEE_TAG_DOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_DOT;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_LIST");
			match('.');
			if ( inputState.guessing==0 ) {
				_ttype = DOT;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_AUTHOR_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_AUTHOR_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST");
			match("@author");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_DEPRECATED_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_DEPRECATED_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST");
			match("@deprecated");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_RETURN_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_RETURN_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST");
			match("@return");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_VERSION_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_VERSION_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST");
			match("@version");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_ARG_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_ARG_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST");
			match("@arg");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.ARG_NAME;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_ARG_TAG_VAR_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_ARG_TAG_VAR_ID;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.ARG_NAME))
			  throw new SemanticException("this.state == LexerState.ARG_NAME");
			mVAR_ID(false);
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR; _ttype = VAR_ID;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_ARG_TAG_ORDINAL_FIELD_NAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_ARG_TAG_ORDINAL_FIELD_NAME;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.ARG_NAME))
			  throw new SemanticException("this.state == LexerState.ARG_NAME");
			mORDINAL_FIELD_NAME(false);
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR; _ttype = ORDINAL_FIELD_NAME;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_SEE_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST");
			match("@see");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.SEE_TAG_CONTEXT;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_OPEN_INLINE_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_OPEN_INLINE_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST");
			match("{@");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.INLINE_TAG;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_CLOSE_INLINE_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_CLOSE_INLINE_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST || this.state == LexerState.INLINE_TAG");
			match("@}");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_SUMMARY_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_SUMMARY_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("summary");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_EM_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_EM_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("em");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_STRONG_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_STRONG_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("strong");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_SUP_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_SUP_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("sup");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_SUB_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_SUB_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("sub");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_UNORDERED_LIST_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_UNORDERED_LIST_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("unorderedList");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_ORDERED_LIST_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_ORDERED_LIST_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("orderedList");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_ITEM_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_ITEM_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("item");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_CODE_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_CODE_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("code");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_URL_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_URL_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("url");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_INLINE_LINK_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_LINK_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			match("link");
			if ( inputState.guessing==0 ) {
				this.state = LexerState.SEE_TAG_CONTEXT;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_INLINE_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			{
			boolean synPredMatched98 = false;
			if (((LA(1)=='s') && (LA(2)=='u') && (LA(3)=='m') && (LA(4)=='m') && (LA(5)=='a') && (LA(6)=='r'))) {
				int _m98 = mark();
				synPredMatched98 = true;
				inputState.guessing++;
				try {
					{
					mCALDOC_INLINE_SUMMARY_TAG(false);
					{
					switch ( LA(1)) {
					case ' ':
					{
						match(' ');
						break;
					}
					case '\t':
					{
						match('\t');
						break;
					}
					case '\u000c':
					{
						match('\f');
						break;
					}
					case '\n':
					{
						match('\n');
						break;
					}
					case '\r':
					{
						match('\r');
						break;
					}
					case '*':
					{
						match('*');
						break;
					}
					default:
					{
						throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
					}
					}
					}
					}
				}
				catch (RecognitionException pe) {
					synPredMatched98 = false;
				}
				rewind(_m98);
inputState.guessing--;
			}
			if ( synPredMatched98 ) {
				mCALDOC_INLINE_SUMMARY_TAG(false);
				if ( inputState.guessing==0 ) {
					_ttype = CALDOC_INLINE_SUMMARY_TAG;
				}
			}
			else {
				boolean synPredMatched104 = false;
				if (((LA(1)=='s') && (LA(2)=='t') && (LA(3)=='r') && (LA(4)=='o') && (LA(5)=='n') && (LA(6)=='g'))) {
					int _m104 = mark();
					synPredMatched104 = true;
					inputState.guessing++;
					try {
						{
						mCALDOC_INLINE_STRONG_TAG(false);
						{
						switch ( LA(1)) {
						case ' ':
						{
							match(' ');
							break;
						}
						case '\t':
						{
							match('\t');
							break;
						}
						case '\u000c':
						{
							match('\f');
							break;
						}
						case '\n':
						{
							match('\n');
							break;
						}
						case '\r':
						{
							match('\r');
							break;
						}
						case '*':
						{
							match('*');
							break;
						}
						default:
						{
							throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
						}
						}
						}
						}
					}
					catch (RecognitionException pe) {
						synPredMatched104 = false;
					}
					rewind(_m104);
inputState.guessing--;
				}
				if ( synPredMatched104 ) {
					mCALDOC_INLINE_STRONG_TAG(false);
					if ( inputState.guessing==0 ) {
						_ttype = CALDOC_INLINE_STRONG_TAG;
					}
				}
				else {
					boolean synPredMatched113 = false;
					if (((LA(1)=='u') && (LA(2)=='n') && (LA(3)=='o') && (LA(4)=='r') && (LA(5)=='d') && (LA(6)=='e'))) {
						int _m113 = mark();
						synPredMatched113 = true;
						inputState.guessing++;
						try {
							{
							mCALDOC_INLINE_UNORDERED_LIST_TAG(false);
							{
							switch ( LA(1)) {
							case ' ':
							{
								match(' ');
								break;
							}
							case '\t':
							{
								match('\t');
								break;
							}
							case '\u000c':
							{
								match('\f');
								break;
							}
							case '\n':
							{
								match('\n');
								break;
							}
							case '\r':
							{
								match('\r');
								break;
							}
							case '*':
							{
								match('*');
								break;
							}
							default:
							{
								throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
							}
							}
							}
							}
						}
						catch (RecognitionException pe) {
							synPredMatched113 = false;
						}
						rewind(_m113);
inputState.guessing--;
					}
					if ( synPredMatched113 ) {
						mCALDOC_INLINE_UNORDERED_LIST_TAG(false);
						if ( inputState.guessing==0 ) {
							_ttype = CALDOC_INLINE_UNORDERED_LIST_TAG;
						}
					}
					else {
						boolean synPredMatched116 = false;
						if (((LA(1)=='o') && (LA(2)=='r') && (LA(3)=='d') && (LA(4)=='e') && (LA(5)=='r') && (LA(6)=='e'))) {
							int _m116 = mark();
							synPredMatched116 = true;
							inputState.guessing++;
							try {
								{
								mCALDOC_INLINE_ORDERED_LIST_TAG(false);
								{
								switch ( LA(1)) {
								case ' ':
								{
									match(' ');
									break;
								}
								case '\t':
								{
									match('\t');
									break;
								}
								case '\u000c':
								{
									match('\f');
									break;
								}
								case '\n':
								{
									match('\n');
									break;
								}
								case '\r':
								{
									match('\r');
									break;
								}
								case '*':
								{
									match('*');
									break;
								}
								default:
								{
									throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
								}
								}
								}
								}
							}
							catch (RecognitionException pe) {
								synPredMatched116 = false;
							}
							rewind(_m116);
inputState.guessing--;
						}
						if ( synPredMatched116 ) {
							mCALDOC_INLINE_ORDERED_LIST_TAG(false);
							if ( inputState.guessing==0 ) {
								_ttype = CALDOC_INLINE_ORDERED_LIST_TAG;
							}
						}
						else {
							boolean synPredMatched119 = false;
							if (((LA(1)=='i') && (LA(2)=='t') && (LA(3)=='e') && (LA(4)=='m') && (true) && (true))) {
								int _m119 = mark();
								synPredMatched119 = true;
								inputState.guessing++;
								try {
									{
									mCALDOC_INLINE_ITEM_TAG(false);
									{
									switch ( LA(1)) {
									case ' ':
									{
										match(' ');
										break;
									}
									case '\t':
									{
										match('\t');
										break;
									}
									case '\u000c':
									{
										match('\f');
										break;
									}
									case '\n':
									{
										match('\n');
										break;
									}
									case '\r':
									{
										match('\r');
										break;
									}
									case '*':
									{
										match('*');
										break;
									}
									default:
									{
										throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
									}
									}
									}
									}
								}
								catch (RecognitionException pe) {
									synPredMatched119 = false;
								}
								rewind(_m119);
inputState.guessing--;
							}
							if ( synPredMatched119 ) {
								mCALDOC_INLINE_ITEM_TAG(false);
								if ( inputState.guessing==0 ) {
									_ttype = CALDOC_INLINE_ITEM_TAG;
								}
							}
							else {
								boolean synPredMatched122 = false;
								if (((LA(1)=='c') && (LA(2)=='o') && (LA(3)=='d') && (LA(4)=='e') && (true) && (true))) {
									int _m122 = mark();
									synPredMatched122 = true;
									inputState.guessing++;
									try {
										{
										mCALDOC_INLINE_CODE_TAG(false);
										{
										switch ( LA(1)) {
										case ' ':
										{
											match(' ');
											break;
										}
										case '\t':
										{
											match('\t');
											break;
										}
										case '\u000c':
										{
											match('\f');
											break;
										}
										case '\n':
										{
											match('\n');
											break;
										}
										case '\r':
										{
											match('\r');
											break;
										}
										case '*':
										{
											match('*');
											break;
										}
										default:
										{
											throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
										}
										}
										}
										}
									}
									catch (RecognitionException pe) {
										synPredMatched122 = false;
									}
									rewind(_m122);
inputState.guessing--;
								}
								if ( synPredMatched122 ) {
									mCALDOC_INLINE_CODE_TAG(false);
									if ( inputState.guessing==0 ) {
										_ttype = CALDOC_INLINE_CODE_TAG;
									}
								}
								else {
									boolean synPredMatched128 = false;
									if (((LA(1)=='l') && (LA(2)=='i') && (LA(3)=='n') && (LA(4)=='k') && (true) && (true))) {
										int _m128 = mark();
										synPredMatched128 = true;
										inputState.guessing++;
										try {
											{
											mCALDOC_INLINE_LINK_TAG(false);
											{
											switch ( LA(1)) {
											case ' ':
											{
												match(' ');
												break;
											}
											case '\t':
											{
												match('\t');
												break;
											}
											case '\u000c':
											{
												match('\f');
												break;
											}
											case '\n':
											{
												match('\n');
												break;
											}
											case '\r':
											{
												match('\r');
												break;
											}
											case '*':
											{
												match('*');
												break;
											}
											default:
											{
												throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
											}
											}
											}
											}
										}
										catch (RecognitionException pe) {
											synPredMatched128 = false;
										}
										rewind(_m128);
inputState.guessing--;
									}
									if ( synPredMatched128 ) {
										mCALDOC_INLINE_LINK_TAG(false);
										if ( inputState.guessing==0 ) {
											_ttype = CALDOC_INLINE_LINK_TAG;
										}
									}
									else {
										boolean synPredMatched107 = false;
										if (((LA(1)=='s') && (LA(2)=='u') && (LA(3)=='p') && (true) && (true) && (true))) {
											int _m107 = mark();
											synPredMatched107 = true;
											inputState.guessing++;
											try {
												{
												mCALDOC_INLINE_SUP_TAG(false);
												{
												switch ( LA(1)) {
												case ' ':
												{
													match(' ');
													break;
												}
												case '\t':
												{
													match('\t');
													break;
												}
												case '\u000c':
												{
													match('\f');
													break;
												}
												case '\n':
												{
													match('\n');
													break;
												}
												case '\r':
												{
													match('\r');
													break;
												}
												case '*':
												{
													match('*');
													break;
												}
												default:
												{
													throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
												}
												}
												}
												}
											}
											catch (RecognitionException pe) {
												synPredMatched107 = false;
											}
											rewind(_m107);
inputState.guessing--;
										}
										if ( synPredMatched107 ) {
											mCALDOC_INLINE_SUP_TAG(false);
											if ( inputState.guessing==0 ) {
												_ttype = CALDOC_INLINE_SUP_TAG;
											}
										}
										else {
											boolean synPredMatched110 = false;
											if (((LA(1)=='s') && (LA(2)=='u') && (LA(3)=='b') && (true) && (true) && (true))) {
												int _m110 = mark();
												synPredMatched110 = true;
												inputState.guessing++;
												try {
													{
													mCALDOC_INLINE_SUB_TAG(false);
													{
													switch ( LA(1)) {
													case ' ':
													{
														match(' ');
														break;
													}
													case '\t':
													{
														match('\t');
														break;
													}
													case '\u000c':
													{
														match('\f');
														break;
													}
													case '\n':
													{
														match('\n');
														break;
													}
													case '\r':
													{
														match('\r');
														break;
													}
													case '*':
													{
														match('*');
														break;
													}
													default:
													{
														throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
													}
													}
													}
													}
												}
												catch (RecognitionException pe) {
													synPredMatched110 = false;
												}
												rewind(_m110);
inputState.guessing--;
											}
											if ( synPredMatched110 ) {
												mCALDOC_INLINE_SUB_TAG(false);
												if ( inputState.guessing==0 ) {
													_ttype = CALDOC_INLINE_SUB_TAG;
												}
											}
											else {
												boolean synPredMatched125 = false;
												if (((LA(1)=='u') && (LA(2)=='r') && (LA(3)=='l') && (true) && (true) && (true))) {
													int _m125 = mark();
													synPredMatched125 = true;
													inputState.guessing++;
													try {
														{
														mCALDOC_INLINE_URL_TAG(false);
														{
														switch ( LA(1)) {
														case ' ':
														{
															match(' ');
															break;
														}
														case '\t':
														{
															match('\t');
															break;
														}
														case '\u000c':
														{
															match('\f');
															break;
														}
														case '\n':
														{
															match('\n');
															break;
														}
														case '\r':
														{
															match('\r');
															break;
														}
														case '*':
														{
															match('*');
															break;
														}
														default:
														{
															throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
														}
														}
														}
														}
													}
													catch (RecognitionException pe) {
														synPredMatched125 = false;
													}
													rewind(_m125);
inputState.guessing--;
												}
												if ( synPredMatched125 ) {
													mCALDOC_INLINE_URL_TAG(false);
													if ( inputState.guessing==0 ) {
														_ttype = CALDOC_INLINE_URL_TAG;
													}
												}
												else {
													boolean synPredMatched101 = false;
													if (((LA(1)=='e') && (LA(2)=='m') && (true) && (true) && (true) && (true))) {
														int _m101 = mark();
														synPredMatched101 = true;
														inputState.guessing++;
														try {
															{
															mCALDOC_INLINE_EM_TAG(false);
															{
															switch ( LA(1)) {
															case ' ':
															{
																match(' ');
																break;
															}
															case '\t':
															{
																match('\t');
																break;
															}
															case '\u000c':
															{
																match('\f');
																break;
															}
															case '\n':
															{
																match('\n');
																break;
															}
															case '\r':
															{
																match('\r');
																break;
															}
															case '*':
															{
																match('*');
																break;
															}
															default:
															{
																throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
															}
															}
															}
															}
														}
														catch (RecognitionException pe) {
															synPredMatched101 = false;
														}
														rewind(_m101);
inputState.guessing--;
													}
													if ( synPredMatched101 ) {
														mCALDOC_INLINE_EM_TAG(false);
														if ( inputState.guessing==0 ) {
															_ttype = CALDOC_INLINE_EM_TAG;
														}
													}
													else if ((_tokenSet_2.member(LA(1))) && (true) && (true) && (true) && (true) && (true)) {
														mCALDOC_INLINE_UNKNOWN_TAG(false);
														if ( inputState.guessing==0 ) {
															_ttype = CALDOC_INLINE_UNKNOWN_TAG;
														}
													}
													else {
														throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
													}
													}}}}}}}}}}
													}
												}
												catch (RecognitionException ex) {
													if (inputState.guessing==0) {
														reportError(ex);
														recover(ex,_tokenSet_4);
													} else {
													  throw ex;
													}
												}
												if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
													_token = makeToken(_ttype);
													_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
												}
												_returnToken = _token;
											}
											
	protected final void mCALDOC_INLINE_UNKNOWN_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_INLINE_UNKNOWN_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.INLINE_TAG))
			  throw new SemanticException("this.state == LexerState.INLINE_TAG");
			{
			int _cnt133=0;
			_loop133:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					{
					{
					match(_tokenSet_2);
					}
					}
				}
				else {
					if ( _cnt133>=1 ) { break _loop133; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt133++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_UNKNOWN_TAG(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_UNKNOWN_TAG;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.REGULAR || this.state == LexerState.SEE_TAG_LIST");
			match('@');
			{
			_loop138:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					{
					{
					match(_tokenSet_2);
					}
					}
				}
				else {
					break _loop138;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_SEE_TAG_UNKNOWN_REFERENCE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_SEE_TAG_UNKNOWN_REFERENCE;
		int _saveIndex;
		
		try {      // for error handling
			if (!(this.state == LexerState.SEE_TAG_LIST))
			  throw new SemanticException("this.state == LexerState.SEE_TAG_LIST");
			{
			int _cnt148=0;
			_loop148:
			do {
				if ((_tokenSet_8.member(LA(1)))) {
					{
					{
					match(_tokenSet_8);
					}
					}
				}
				else {
					if ( _cnt148>=1 ) { break _loop148; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt148++;
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCALDOC_REGULAR_TEXT_LINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_REGULAR_TEXT_LINE;
		int _saveIndex;
		
		try {      // for error handling
			{
			if ((_tokenSet_9.member(LA(1)))) {
				{
				match(_tokenSet_9);
				}
			}
			else if (((LA(1)=='{'))&&(LA(2) != '@')) {
				match('{');
			}
			else if (((LA(1)=='*'))&&(LA(2) != '/')) {
				match('*');
			}
			else if ((LA(1)=='\\')) {
				match('\\');
				{
				boolean synPredMatched154 = false;
				if (((LA(1)=='{') && (LA(2)=='@') && (true) && (true) && (true) && (true))) {
					int _m154 = mark();
					synPredMatched154 = true;
					inputState.guessing++;
					try {
						{
						match("{@");
						}
					}
					catch (RecognitionException pe) {
						synPredMatched154 = false;
					}
					rewind(_m154);
inputState.guessing--;
				}
				if ( synPredMatched154 ) {
					match("{@");
				}
				else {
					boolean synPredMatched156 = false;
					if (((LA(1)=='@') && (true) && (true) && (true) && (true) && (true))) {
						int _m156 = mark();
						synPredMatched156 = true;
						inputState.guessing++;
						try {
							{
							match('@');
							}
						}
						catch (RecognitionException pe) {
							synPredMatched156 = false;
						}
						rewind(_m156);
inputState.guessing--;
					}
					if ( synPredMatched156 ) {
						match('@');
					}
					else {
					}
					}
					}
				}
				else {
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				
				}
				{
				_loop164:
				do {
					if (((LA(1)=='@'))&&(LA(2) != '}')) {
						match('@');
					}
					else if ((_tokenSet_9.member(LA(1)))) {
						{
						match(_tokenSet_9);
						}
					}
					else if (((LA(1)=='{'))&&(LA(2) != '@')) {
						match('{');
					}
					else if (((LA(1)=='*'))&&(LA(2) != '/')) {
						match('*');
					}
					else if ((LA(1)=='\\')) {
						match('\\');
						{
						boolean synPredMatched161 = false;
						if (((LA(1)=='{') && (LA(2)=='@') && (true) && (true) && (true) && (true))) {
							int _m161 = mark();
							synPredMatched161 = true;
							inputState.guessing++;
							try {
								{
								match("{@");
								}
							}
							catch (RecognitionException pe) {
								synPredMatched161 = false;
							}
							rewind(_m161);
inputState.guessing--;
						}
						if ( synPredMatched161 ) {
							match("{@");
						}
						else {
							boolean synPredMatched163 = false;
							if (((LA(1)=='@') && (true) && (true) && (true) && (true) && (true))) {
								int _m163 = mark();
								synPredMatched163 = true;
								inputState.guessing++;
								try {
									{
									match('@');
									}
								}
								catch (RecognitionException pe) {
									synPredMatched163 = false;
								}
								rewind(_m163);
inputState.guessing--;
							}
							if ( synPredMatched163 ) {
								match('@');
							}
							else {
							}
							}
							}
						}
						else {
							break _loop164;
						}
						
					} while (true);
					}
					if ( inputState.guessing==0 ) {
						this.state = LexerState.REGULAR;
					}
				}
				catch (RecognitionException ex) {
					if (inputState.guessing==0) {
						reportError(ex);
						recover(ex,_tokenSet_4);
					} else {
					  throw ex;
					}
				}
				if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
					_token = makeToken(_ttype);
					_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
				}
				_returnToken = _token;
			}
			
	protected final void mCALDOC_BLANK_TEXT_LINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_BLANK_TEXT_LINE;
		int _saveIndex;
		
		try {      // for error handling
			{
			int _cnt167=0;
			_loop167:
			do {
				if ((LA(1)=='\t'||LA(1)=='\u000c'||LA(1)==' ')) {
					mCALDOC_WS(false);
				}
				else {
					if ( _cnt167>=1 ) { break _loop167; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				
				_cnt167++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				this.state = LexerState.REGULAR;
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				recover(ex,_tokenSet_4);
			} else {
			  throw ex;
			}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCALDOC_TEXT_LINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CALDOC_TEXT_LINE;
		int _saveIndex;
		int nLeadingWS = 0;
		
		try {      // for error handling
			boolean synPredMatched170 = false;
			if (((LA(1)=='*') && (LA(2)=='/') && (true) && (true) && (true) && (true))) {
				int _m170 = mark();
				synPredMatched170 = true;
				inputState.guessing++;
				try {
					{
					match("*/");
					}
				}
				catch (RecognitionException pe) {
					synPredMatched170 = false;
				}
				rewind(_m170);
inputState.guessing--;
			}
			if ( synPredMatched170 ) {
				mCALDOC_CLOSE(false);
				if ( inputState.guessing==0 ) {
					_ttype = CALDOC_CLOSE;
				}
			}
			else {
				boolean synPredMatched174 = false;
				if (((_tokenSet_10.member(LA(1))) && (true) && (true) && (true) && (true) && (true))) {
					int _m174 = mark();
					synPredMatched174 = true;
					inputState.guessing++;
					try {
						{
						{
						_loop173:
						do {
							if ((LA(1)=='\t'||LA(1)=='\u000c'||LA(1)==' ')) {
								mCALDOC_WS(false);
							}
							else {
								break _loop173;
							}
							
						} while (true);
						}
						match('@');
						}
					}
					catch (RecognitionException pe) {
						synPredMatched174 = false;
					}
					rewind(_m174);
inputState.guessing--;
				}
				if ( synPredMatched174 ) {
					{
					{
					_loop177:
					do {
						if ((LA(1)=='\t'||LA(1)=='\u000c'||LA(1)==' ')) {
							_saveIndex=text.length();
							mCALDOC_WS(false);
							text.setLength(_saveIndex);
							if ( inputState.guessing==0 ) {
								nLeadingWS++;
							}
						}
						else {
							break _loop177;
						}
						
					} while (true);
					}
					{
					boolean synPredMatched181 = false;
					if (((LA(1)=='@') && (LA(2)=='a') && (LA(3)=='u') && (LA(4)=='t') && (LA(5)=='h') && (LA(6)=='o'))) {
						int _m181 = mark();
						synPredMatched181 = true;
						inputState.guessing++;
						try {
							{
							mCALDOC_AUTHOR_TAG(false);
							{
							switch ( LA(1)) {
							case ' ':
							{
								match(' ');
								break;
							}
							case '\t':
							{
								match('\t');
								break;
							}
							case '\u000c':
							{
								match('\f');
								break;
							}
							case '\n':
							{
								match('\n');
								break;
							}
							case '\r':
							{
								match('\r');
								break;
							}
							case '*':
							{
								match('*');
								break;
							}
							default:
							{
								throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
							}
							}
							}
							}
						}
						catch (RecognitionException pe) {
							synPredMatched181 = false;
						}
						rewind(_m181);
inputState.guessing--;
					}
					if ( synPredMatched181 ) {
						mCALDOC_AUTHOR_TAG(false);
						if ( inputState.guessing==0 ) {
							_ttype = CALDOC_AUTHOR_TAG;
						}
					}
					else {
						boolean synPredMatched184 = false;
						if (((LA(1)=='@') && (LA(2)=='d') && (LA(3)=='e') && (LA(4)=='p') && (LA(5)=='r') && (LA(6)=='e'))) {
							int _m184 = mark();
							synPredMatched184 = true;
							inputState.guessing++;
							try {
								{
								mCALDOC_DEPRECATED_TAG(false);
								{
								switch ( LA(1)) {
								case ' ':
								{
									match(' ');
									break;
								}
								case '\t':
								{
									match('\t');
									break;
								}
								case '\u000c':
								{
									match('\f');
									break;
								}
								case '\n':
								{
									match('\n');
									break;
								}
								case '\r':
								{
									match('\r');
									break;
								}
								case '*':
								{
									match('*');
									break;
								}
								default:
								{
									throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
								}
								}
								}
								}
							}
							catch (RecognitionException pe) {
								synPredMatched184 = false;
							}
							rewind(_m184);
inputState.guessing--;
						}
						if ( synPredMatched184 ) {
							mCALDOC_DEPRECATED_TAG(false);
							if ( inputState.guessing==0 ) {
								_ttype = CALDOC_DEPRECATED_TAG;
							}
						}
						else {
							boolean synPredMatched187 = false;
							if (((LA(1)=='@') && (LA(2)=='r') && (LA(3)=='e') && (LA(4)=='t') && (LA(5)=='u') && (LA(6)=='r'))) {
								int _m187 = mark();
								synPredMatched187 = true;
								inputState.guessing++;
								try {
									{
									mCALDOC_RETURN_TAG(false);
									{
									switch ( LA(1)) {
									case ' ':
									{
										match(' ');
										break;
									}
									case '\t':
									{
										match('\t');
										break;
									}
									case '\u000c':
									{
										match('\f');
										break;
									}
									case '\n':
									{
										match('\n');
										break;
									}
									case '\r':
									{
										match('\r');
										break;
									}
									case '*':
									{
										match('*');
										break;
									}
									default:
									{
										throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
									}
									}
									}
									}
								}
								catch (RecognitionException pe) {
									synPredMatched187 = false;
								}
								rewind(_m187);
inputState.guessing--;
							}
							if ( synPredMatched187 ) {
								mCALDOC_RETURN_TAG(false);
								if ( inputState.guessing==0 ) {
									_ttype = CALDOC_RETURN_TAG;
								}
							}
							else {
								boolean synPredMatched190 = false;
								if (((LA(1)=='@') && (LA(2)=='v') && (LA(3)=='e') && (LA(4)=='r') && (LA(5)=='s') && (LA(6)=='i'))) {
									int _m190 = mark();
									synPredMatched190 = true;
									inputState.guessing++;
									try {
										{
										mCALDOC_VERSION_TAG(false);
										{
										switch ( LA(1)) {
										case ' ':
										{
											match(' ');
											break;
										}
										case '\t':
										{
											match('\t');
											break;
										}
										case '\u000c':
										{
											match('\f');
											break;
										}
										case '\n':
										{
											match('\n');
											break;
										}
										case '\r':
										{
											match('\r');
											break;
										}
										case '*':
										{
											match('*');
											break;
										}
										default:
										{
											throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
										}
										}
										}
										}
									}
									catch (RecognitionException pe) {
										synPredMatched190 = false;
									}
									rewind(_m190);
inputState.guessing--;
								}
								if ( synPredMatched190 ) {
									mCALDOC_VERSION_TAG(false);
									if ( inputState.guessing==0 ) {
										_ttype = CALDOC_VERSION_TAG;
									}
								}
								else {
									boolean synPredMatched193 = false;
									if (((LA(1)=='@') && (LA(2)=='a') && (LA(3)=='r') && (LA(4)=='g') && (true) && (true))) {
										int _m193 = mark();
										synPredMatched193 = true;
										inputState.guessing++;
										try {
											{
											mCALDOC_ARG_TAG(false);
											{
											switch ( LA(1)) {
											case ' ':
											{
												match(' ');
												break;
											}
											case '\t':
											{
												match('\t');
												break;
											}
											case '\u000c':
											{
												match('\f');
												break;
											}
											case '\n':
											{
												match('\n');
												break;
											}
											case '\r':
											{
												match('\r');
												break;
											}
											case '*':
											{
												match('*');
												break;
											}
											default:
											{
												throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
											}
											}
											}
											}
										}
										catch (RecognitionException pe) {
											synPredMatched193 = false;
										}
										rewind(_m193);
inputState.guessing--;
									}
									if ( synPredMatched193 ) {
										mCALDOC_ARG_TAG(false);
										if ( inputState.guessing==0 ) {
											_ttype = CALDOC_ARG_TAG;
										}
									}
									else {
										boolean synPredMatched196 = false;
										if (((LA(1)=='@') && (LA(2)=='s') && (LA(3)=='e') && (LA(4)=='e') && (true) && (true))) {
											int _m196 = mark();
											synPredMatched196 = true;
											inputState.guessing++;
											try {
												{
												mCALDOC_SEE_TAG(false);
												{
												switch ( LA(1)) {
												case ' ':
												{
													match(' ');
													break;
												}
												case '\t':
												{
													match('\t');
													break;
												}
												case '\u000c':
												{
													match('\f');
													break;
												}
												case '\n':
												{
													match('\n');
													break;
												}
												case '\r':
												{
													match('\r');
													break;
												}
												case '*':
												{
													match('*');
													break;
												}
												default:
												{
													throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
												}
												}
												}
												}
											}
											catch (RecognitionException pe) {
												synPredMatched196 = false;
											}
											rewind(_m196);
inputState.guessing--;
										}
										if ( synPredMatched196 ) {
											mCALDOC_SEE_TAG(false);
											if ( inputState.guessing==0 ) {
												_ttype = CALDOC_SEE_TAG;
											}
										}
										else {
											boolean synPredMatched198 = false;
											if (((LA(1)=='@') && (LA(2)=='}') && (true) && (true) && (true) && (true))) {
												int _m198 = mark();
												synPredMatched198 = true;
												inputState.guessing++;
												try {
													{
													mCALDOC_CLOSE_INLINE_TAG(false);
													}
												}
												catch (RecognitionException pe) {
													synPredMatched198 = false;
												}
												rewind(_m198);
inputState.guessing--;
											}
											if ( synPredMatched198 ) {
												mCALDOC_CLOSE_INLINE_TAG(false);
												if ( inputState.guessing==0 ) {
													_ttype = CALDOC_CLOSE_INLINE_TAG;
												}
											}
											else if ((LA(1)=='@') && (true) && (true) && (true) && (true) && (true)) {
												mCALDOC_UNKNOWN_TAG(false);
												if ( inputState.guessing==0 ) {
													_ttype = CALDOC_UNKNOWN_TAG;
												}
											}
											else {
												throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
											}
											}}}}}}
											}
											}
											if ( inputState.guessing==0 ) {
												
												Token newToken = makeToken(_ttype);
												newToken.setColumn(newToken.getColumn() + nLeadingWS);
												newToken.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
												_token = newToken;
												
											}
										}
										else if (((_tokenSet_8.member(LA(1))) && (true) && (true) && (true) && (true) && (true))&&(this.state == LexerState.SEE_TAG_LIST)) {
											mCALDOC_SEE_TAG_UNKNOWN_REFERENCE(false);
											if ( inputState.guessing==0 ) {
												_ttype = CALDOC_SEE_TAG_UNKNOWN_REFERENCE;
											}
										}
										else if ((_tokenSet_11.member(LA(1))) && (true) && (true) && (true) && (true) && (true)) {
											mCALDOC_REGULAR_TEXT_LINE(false);
											if ( inputState.guessing==0 ) {
												if (isCALDocWhitespaceString(new String(text.getBuffer(),_begin,text.length()-_begin))) {_ttype = CALDOC_BLANK_TEXT_LINE;}
											}
										}
										else {
											throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
										}
										}
									}
									catch (RecognitionException ex) {
										if (inputState.guessing==0) {
											reportError(ex);
											recover(ex,_tokenSet_4);
										} else {
										  throw ex;
										}
									}
									if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
										_token = makeToken(_ttype);
										_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
									}
									_returnToken = _token;
								}
								
								
								private static final long[] mk_tokenSet_0() {
									long[] data = new long[1025];
									data[0]=4294981120L;
									return data;
								}
								public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
								private static final long[] mk_tokenSet_1() {
									long[] data = new long[2048];
									data[0]=-2305847411555186177L;
									for (int i = 1; i<=1022; i++) { data[i]=-1L; }
									data[1023]=9223372036854775807L;
									return data;
								}
								public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
								private static final long[] mk_tokenSet_2() {
									long[] data = new long[2048];
									data[0]=-4402341492225L;
									data[1]=-2L;
									for (int i = 2; i<=1022; i++) { data[i]=-1L; }
									data[1023]=9223372036854775807L;
									return data;
								}
								public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
								private static final long[] mk_tokenSet_3() {
									long[] data = new long[2048];
									data[0]=-9217L;
									for (int i = 1; i<=1022; i++) { data[i]=-1L; }
									data[1023]=9223372036854775807L;
									return data;
								}
								public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
								private static final long[] mk_tokenSet_4() {
									long[] data = new long[1025];
									return data;
								}
								public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
								private static final long[] mk_tokenSet_5() {
									long[] data = new long[1025];
									data[0]=4402341492224L;
									return data;
								}
								public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
								private static final long[] mk_tokenSet_6() {
									long[] data = new long[1025];
									data[0]=4402341492224L;
									data[1]=1L;
									return data;
								}
								public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
								private static final long[] mk_tokenSet_7() {
									long[] data = new long[1025];
									data[0]=4402341484032L;
									return data;
								}
								public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
								private static final long[] mk_tokenSet_8() {
									long[] data = new long[2048];
									data[0]=-92380451583489L;
									for (int i = 1; i<=1022; i++) { data[i]=-1L; }
									data[1023]=9223372036854775807L;
									return data;
								}
								public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
								private static final long[] mk_tokenSet_9() {
									long[] data = new long[2048];
									data[0]=-4398046520321L;
									data[1]=-576460752571858946L;
									for (int i = 2; i<=1022; i++) { data[i]=-1L; }
									data[1023]=9223372036854775807L;
									return data;
								}
								public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
								private static final long[] mk_tokenSet_10() {
									long[] data = new long[1025];
									data[0]=4294971904L;
									data[1]=1L;
									return data;
								}
								public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
								private static final long[] mk_tokenSet_11() {
									long[] data = new long[2048];
									data[0]=-9217L;
									data[1]=-2L;
									for (int i = 2; i<=1022; i++) { data[i]=-1L; }
									data[1023]=9223372036854775807L;
									return data;
								}
								public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
								
								}
