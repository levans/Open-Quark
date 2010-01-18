// $ANTLR 2.7.6 (2005-12-22): "CALTreeParser.g" -> "CALTreeParser.java"$

// Package declaration
package org.openquark.cal.compiler;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


/*******************************************
 * CAL tree grammar definition starts here *   
 *******************************************/
@SuppressWarnings(\u0022all\u0022) final class CALTreeParser extends antlr.TreeParser       implements CALTreeParserTokenTypes
 {

    // Add declarations for CALTreeParser class here

    private CALCompiler compiler;  
 
    /**
     * Construct CALTreeParser from a CALCompiler
     */ 
    CALTreeParser (CALCompiler compiler) {
        this ();
        this.compiler = compiler;
    }

    /**
     * Override reportError method to direct standard error handling through to the CALCompiler error scheme
     * @param ex the recognition exception that originated the problem
     */
    public void reportError (RecognitionException ex) {
       
        //tree parsing errors are FATAL because they should only occur when there has been a programming
        //mistake in CAL.g or CALTreeParser.g i.e. they cannot be caused by user error in .cal code.
        final SourcePosition startSourcePosition = new SourcePosition(ex.getLine(), ex.getColumn(), ex.getFilename());
        final SourcePosition endSourcePosition = new SourcePosition(ex.getLine(), ex.getColumn()+1, ex.getFilename());
        final SourceRange sourceRange = new SourceRange(startSourcePosition, endSourcePosition);
        compiler.logMessage(new CompilerMessage (sourceRange, new MessageKind.Fatal.TreeParsingError(), ex));
    }  
    
public CALTreeParser() {
	tokenNames = _tokenNames;
}

	public final void codeGemExpr(AST _t) throws RecognitionException {
		
		AST codeGemExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			expr(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void expr(AST _t) throws RecognitionException {
		
		AST expr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_let:
			{
				AST __t175 = _t;
				AST tmp1_AST_in = (AST)_t;
				match(_t,LITERAL_let);
				_t = _t.getFirstChild();
				letDefnList(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t175;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_if:
			{
				AST __t176 = _t;
				AST tmp2_AST_in = (AST)_t;
				match(_t,LITERAL_if);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t176;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_case:
			{
				AST __t177 = _t;
				AST tmp3_AST_in = (AST)_t;
				match(_t,LITERAL_case);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				altList(_t);
				_t = _retTree;
				_t = __t177;
				_t = _t.getNextSibling();
				break;
			}
			case LAMBDA_DEFN:
			{
				AST __t178 = _t;
				AST tmp4_AST_in = (AST)_t;
				match(_t,LAMBDA_DEFN);
				_t = _t.getFirstChild();
				lambdaParamList(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t178;
				_t = _t.getNextSibling();
				break;
			}
			case BARBAR:
			{
				AST __t179 = _t;
				AST tmp5_AST_in = (AST)_t;
				match(_t,BARBAR);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t179;
				_t = _t.getNextSibling();
				break;
			}
			case AMPERSANDAMPERSAND:
			{
				AST __t180 = _t;
				AST tmp6_AST_in = (AST)_t;
				match(_t,AMPERSANDAMPERSAND);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t180;
				_t = _t.getNextSibling();
				break;
			}
			case LESS_THAN:
			{
				AST __t181 = _t;
				AST tmp7_AST_in = (AST)_t;
				match(_t,LESS_THAN);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t181;
				_t = _t.getNextSibling();
				break;
			}
			case LESS_THAN_OR_EQUALS:
			{
				AST __t182 = _t;
				AST tmp8_AST_in = (AST)_t;
				match(_t,LESS_THAN_OR_EQUALS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t182;
				_t = _t.getNextSibling();
				break;
			}
			case EQUALSEQUALS:
			{
				AST __t183 = _t;
				AST tmp9_AST_in = (AST)_t;
				match(_t,EQUALSEQUALS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t183;
				_t = _t.getNextSibling();
				break;
			}
			case NOT_EQUALS:
			{
				AST __t184 = _t;
				AST tmp10_AST_in = (AST)_t;
				match(_t,NOT_EQUALS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t184;
				_t = _t.getNextSibling();
				break;
			}
			case GREATER_THAN_OR_EQUALS:
			{
				AST __t185 = _t;
				AST tmp11_AST_in = (AST)_t;
				match(_t,GREATER_THAN_OR_EQUALS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t185;
				_t = _t.getNextSibling();
				break;
			}
			case GREATER_THAN:
			{
				AST __t186 = _t;
				AST tmp12_AST_in = (AST)_t;
				match(_t,GREATER_THAN);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t186;
				_t = _t.getNextSibling();
				break;
			}
			case PLUS:
			{
				AST __t187 = _t;
				AST tmp13_AST_in = (AST)_t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t187;
				_t = _t.getNextSibling();
				break;
			}
			case MINUS:
			{
				AST __t188 = _t;
				AST tmp14_AST_in = (AST)_t;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t188;
				_t = _t.getNextSibling();
				break;
			}
			case ASTERISK:
			{
				AST __t189 = _t;
				AST tmp15_AST_in = (AST)_t;
				match(_t,ASTERISK);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t189;
				_t = _t.getNextSibling();
				break;
			}
			case SOLIDUS:
			{
				AST __t190 = _t;
				AST tmp16_AST_in = (AST)_t;
				match(_t,SOLIDUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t190;
				_t = _t.getNextSibling();
				break;
			}
			case PERCENT:
			{
				AST __t191 = _t;
				AST tmp17_AST_in = (AST)_t;
				match(_t,PERCENT);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t191;
				_t = _t.getNextSibling();
				break;
			}
			case COLON:
			{
				AST __t192 = _t;
				AST tmp18_AST_in = (AST)_t;
				match(_t,COLON);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t192;
				_t = _t.getNextSibling();
				break;
			}
			case PLUSPLUS:
			{
				AST __t193 = _t;
				AST tmp19_AST_in = (AST)_t;
				match(_t,PLUSPLUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t193;
				_t = _t.getNextSibling();
				break;
			}
			case UNARY_MINUS:
			{
				AST __t194 = _t;
				AST tmp20_AST_in = (AST)_t;
				match(_t,UNARY_MINUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				_t = __t194;
				_t = _t.getNextSibling();
				break;
			}
			case BACKQUOTE:
			{
				AST __t195 = _t;
				AST tmp21_AST_in = (AST)_t;
				match(_t,BACKQUOTE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				{
				_loop198:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==QUALIFIED_VAR||_t.getType()==QUALIFIED_CONS)) {
						{
						if (_t==null) _t=ASTNULL;
						switch ( _t.getType()) {
						case QUALIFIED_VAR:
						{
							qualifiedVar(_t);
							_t = _retTree;
							break;
						}
						case QUALIFIED_CONS:
						{
							qualifiedCons(_t);
							_t = _retTree;
							break;
						}
						default:
						{
							throw new NoViableAltException(_t);
						}
						}
						}
						expr(_t);
						_t = _retTree;
					}
					else {
						break _loop198;
					}
					
				} while (true);
				}
				_t = __t195;
				_t = _t.getNextSibling();
				break;
			}
			case POUND:
			{
				AST __t199 = _t;
				AST tmp22_AST_in = (AST)_t;
				match(_t,POUND);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t199;
				_t = _t.getNextSibling();
				break;
			}
			case DOLLAR:
			{
				AST __t200 = _t;
				AST tmp23_AST_in = (AST)_t;
				match(_t,DOLLAR);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				expr(_t);
				_t = _retTree;
				_t = __t200;
				_t = _t.getNextSibling();
				break;
			}
			case APPLICATION:
			{
				AST __t201 = _t;
				AST tmp24_AST_in = (AST)_t;
				match(_t,APPLICATION);
				_t = _t.getFirstChild();
				{
				int _cnt203=0;
				_loop203:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_0.member(_t.getType()))) {
						expr(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt203>=1 ) { break _loop203; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt203++;
				} while (true);
				}
				_t = __t201;
				_t = _t.getNextSibling();
				break;
			}
			case QUALIFIED_VAR:
			{
				qualifiedVar(_t);
				_t = _retTree;
				break;
			}
			case QUALIFIED_CONS:
			{
				qualifiedCons(_t);
				_t = _retTree;
				break;
			}
			case STRING_LITERAL:
			case CHAR_LITERAL:
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
			{
				literal(_t);
				_t = _retTree;
				break;
			}
			case TUPLE_CONSTRUCTOR:
			{
				AST __t204 = _t;
				AST tmp25_AST_in = (AST)_t;
				match(_t,TUPLE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				{
				_loop206:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_0.member(_t.getType()))) {
						expr(_t);
						_t = _retTree;
					}
					else {
						break _loop206;
					}
					
				} while (true);
				}
				_t = __t204;
				_t = _t.getNextSibling();
				break;
			}
			case LIST_CONSTRUCTOR:
			{
				AST __t207 = _t;
				AST tmp26_AST_in = (AST)_t;
				match(_t,LIST_CONSTRUCTOR);
				_t = _t.getFirstChild();
				{
				_loop209:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_0.member(_t.getType()))) {
						expr(_t);
						_t = _retTree;
					}
					else {
						break _loop209;
					}
					
				} while (true);
				}
				_t = __t207;
				_t = _t.getNextSibling();
				break;
			}
			case RECORD_CONSTRUCTOR:
			{
				AST __t210 = _t;
				AST tmp27_AST_in = (AST)_t;
				match(_t,RECORD_CONSTRUCTOR);
				_t = _t.getFirstChild();
				baseRecord(_t);
				_t = _retTree;
				fieldModificationList(_t);
				_t = _retTree;
				_t = __t210;
				_t = _t.getNextSibling();
				break;
			}
			case SELECT_RECORD_FIELD:
			{
				AST __t211 = _t;
				AST tmp28_AST_in = (AST)_t;
				match(_t,SELECT_RECORD_FIELD);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				fieldName(_t);
				_t = _retTree;
				_t = __t211;
				_t = _t.getNextSibling();
				break;
			}
			case SELECT_DATA_CONSTRUCTOR_FIELD:
			{
				AST __t212 = _t;
				AST tmp29_AST_in = (AST)_t;
				match(_t,SELECT_DATA_CONSTRUCTOR_FIELD);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				qualifiedCons(_t);
				_t = _retTree;
				fieldName(_t);
				_t = _retTree;
				_t = __t212;
				_t = _t.getNextSibling();
				break;
			}
			case EXPRESSION_TYPE_SIGNATURE:
			{
				AST __t213 = _t;
				AST tmp30_AST_in = (AST)_t;
				match(_t,EXPRESSION_TYPE_SIGNATURE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				typeSignature(_t);
				_t = _retTree;
				_t = __t213;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void startTypeSignature(AST _t) throws RecognitionException {
		
		AST startTypeSignature_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			typeSignature(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeSignature(AST _t) throws RecognitionException {
		
		AST typeSignature_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t57 = _t;
			AST tmp31_AST_in = (AST)_t;
			match(_t,TYPE_SIGNATURE);
			_t = _t.getFirstChild();
			typeContextList(_t);
			_t = _retTree;
			type(_t);
			_t = _retTree;
			_t = __t57;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void adjunct(AST _t) throws RecognitionException {
		
		AST adjunct_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t4 = _t;
			AST tmp32_AST_in = (AST)_t;
			match(_t,OUTER_DEFN_LIST);
			_t = _t.getFirstChild();
			{
			_loop6:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==TOP_LEVEL_FUNCTION_DEFN||_t.getType()==TOP_LEVEL_TYPE_DECLARATION||_t.getType()==INSTANCE_DEFN)) {
					adjunctDefn(_t);
					_t = _retTree;
				}
				else {
					break _loop6;
				}
				
			} while (true);
			}
			_t = __t4;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void adjunctDefn(AST _t) throws RecognitionException {
		
		AST adjunctDefn_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TOP_LEVEL_TYPE_DECLARATION:
			{
				topLevelTypeDeclaration(_t);
				_t = _retTree;
				break;
			}
			case INSTANCE_DEFN:
			{
				instanceDefn(_t);
				_t = _retTree;
				break;
			}
			case TOP_LEVEL_FUNCTION_DEFN:
			{
				topLevelFunction(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void topLevelTypeDeclaration(AST _t) throws RecognitionException {
		
		AST topLevelTypeDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t51 = _t;
			AST tmp33_AST_in = (AST)_t;
			match(_t,TOP_LEVEL_TYPE_DECLARATION);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			typeDeclaration(_t);
			_t = _retTree;
			_t = __t51;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void instanceDefn(AST _t) throws RecognitionException {
		
		AST instanceDefn_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t134 = _t;
			AST tmp34_AST_in = (AST)_t;
			match(_t,INSTANCE_DEFN);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			instanceName(_t);
			_t = _retTree;
			instanceMethodList(_t);
			_t = _retTree;
			_t = __t134;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void topLevelFunction(AST _t) throws RecognitionException {
		
		AST topLevelFunction_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t159 = _t;
			AST tmp35_AST_in = (AST)_t;
			match(_t,TOP_LEVEL_FUNCTION_DEFN);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			functionName(_t);
			_t = _retTree;
			functionParamList(_t);
			_t = _retTree;
			expr(_t);
			_t = _retTree;
			_t = __t159;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void module(AST _t) throws RecognitionException {
		
		AST module_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t9 = _t;
			AST tmp36_AST_in = (AST)_t;
			match(_t,MODULE_DEFN);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			moduleName(_t);
			_t = _retTree;
			importDeclarationList(_t);
			_t = _retTree;
			friendDeclarationList(_t);
			_t = _retTree;
			outerDefnList(_t);
			_t = _retTree;
			_t = __t9;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void optionalDocComment(AST _t) throws RecognitionException {
		
		AST optionalDocComment_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t298 = _t;
			AST tmp37_AST_in = (AST)_t;
			match(_t,OPTIONAL_CALDOC_COMMENT);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CALDOC_COMMENT:
			{
				docComment(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t298;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void moduleName(AST _t) throws RecognitionException {
		
		AST moduleName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t11 = _t;
			AST tmp38_AST_in = (AST)_t;
			match(_t,HIERARCHICAL_MODULE_NAME);
			_t = _t.getFirstChild();
			moduleNameQualifier(_t);
			_t = _retTree;
			cons(_t);
			_t = _retTree;
			_t = __t11;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void importDeclarationList(AST _t) throws RecognitionException {
		
		AST importDeclarationList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t16 = _t;
			AST tmp39_AST_in = (AST)_t;
			match(_t,IMPORT_DECLARATION_LIST);
			_t = _t.getFirstChild();
			{
			_loop18:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==LITERAL_import)) {
					importDeclaration(_t);
					_t = _retTree;
				}
				else {
					break _loop18;
				}
				
			} while (true);
			}
			_t = __t16;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void friendDeclarationList(AST _t) throws RecognitionException {
		
		AST friendDeclarationList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t40 = _t;
			AST tmp40_AST_in = (AST)_t;
			match(_t,FRIEND_DECLARATION_LIST);
			_t = _t.getFirstChild();
			{
			_loop42:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==LITERAL_friend)) {
					friendDeclaration(_t);
					_t = _retTree;
				}
				else {
					break _loop42;
				}
				
			} while (true);
			}
			_t = __t40;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void outerDefnList(AST _t) throws RecognitionException {
		
		AST outerDefnList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t46 = _t;
			AST tmp41_AST_in = (AST)_t;
			match(_t,OUTER_DEFN_LIST);
			_t = _t.getFirstChild();
			{
			_loop48:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_1.member(_t.getType()))) {
					outerDefn(_t);
					_t = _retTree;
				}
				else {
					break _loop48;
				}
				
			} while (true);
			}
			_t = __t46;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void moduleNameQualifier(AST _t) throws RecognitionException {
		
		AST moduleNameQualifier_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER:
			{
				AST tmp42_AST_in = (AST)_t;
				match(_t,HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
				_t = _t.getNextSibling();
				break;
			}
			case HIERARCHICAL_MODULE_NAME:
			{
				AST __t13 = _t;
				AST tmp43_AST_in = (AST)_t;
				match(_t,HIERARCHICAL_MODULE_NAME);
				_t = _t.getFirstChild();
				moduleNameQualifier(_t);
				_t = _retTree;
				cons(_t);
				_t = _retTree;
				_t = __t13;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void cons(AST _t) throws RecognitionException {
		
		AST cons_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST tmp44_AST_in = (AST)_t;
			match(_t,CONS_ID);
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void maybeModuleName(AST _t) throws RecognitionException {
		
		AST maybeModuleName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			moduleNameQualifier(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void importDeclaration(AST _t) throws RecognitionException {
		
		AST importDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t20 = _t;
			AST tmp45_AST_in = (AST)_t;
			match(_t,LITERAL_import);
			_t = _t.getFirstChild();
			moduleName(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_using:
			{
				usingClause(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t20;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void usingClause(AST _t) throws RecognitionException {
		
		AST usingClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t23 = _t;
			AST tmp46_AST_in = (AST)_t;
			match(_t,LITERAL_using);
			_t = _t.getFirstChild();
			{
			_loop25:
			do {
				if (_t==null) _t=ASTNULL;
				if (((_t.getType() >= LITERAL_function && _t.getType() <= LITERAL_typeClass))) {
					usingItem(_t);
					_t = _retTree;
				}
				else {
					break _loop25;
				}
				
			} while (true);
			}
			_t = __t23;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void usingItem(AST _t) throws RecognitionException {
		
		AST usingItem_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_function:
			{
				AST __t27 = _t;
				AST tmp47_AST_in = (AST)_t;
				match(_t,LITERAL_function);
				_t = _t.getFirstChild();
				usingVarList(_t);
				_t = _retTree;
				_t = __t27;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_typeConstructor:
			{
				AST __t28 = _t;
				AST tmp48_AST_in = (AST)_t;
				match(_t,LITERAL_typeConstructor);
				_t = _t.getFirstChild();
				usingConsList(_t);
				_t = _retTree;
				_t = __t28;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_dataConstructor:
			{
				AST __t29 = _t;
				AST tmp49_AST_in = (AST)_t;
				match(_t,LITERAL_dataConstructor);
				_t = _t.getFirstChild();
				usingConsList(_t);
				_t = _retTree;
				_t = __t29;
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_typeClass:
			{
				AST __t30 = _t;
				AST tmp50_AST_in = (AST)_t;
				match(_t,LITERAL_typeClass);
				_t = _t.getFirstChild();
				usingConsList(_t);
				_t = _retTree;
				_t = __t30;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void usingVarList(AST _t) throws RecognitionException {
		
		AST usingVarList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			var(_t);
			_t = _retTree;
			{
			_loop38:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COMMA)) {
					AST tmp51_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					var(_t);
					_t = _retTree;
				}
				else {
					break _loop38;
				}
				
			} while (true);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void usingConsList(AST _t) throws RecognitionException {
		
		AST usingConsList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			cons(_t);
			_t = _retTree;
			{
			_loop34:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COMMA)) {
					AST tmp52_AST_in = (AST)_t;
					match(_t,COMMA);
					_t = _t.getNextSibling();
					cons(_t);
					_t = _retTree;
				}
				else {
					break _loop34;
				}
				
			} while (true);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void var(AST _t) throws RecognitionException {
		
		AST var_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST tmp53_AST_in = (AST)_t;
			match(_t,VAR_ID);
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void friendDeclaration(AST _t) throws RecognitionException {
		
		AST friendDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t44 = _t;
			AST tmp54_AST_in = (AST)_t;
			match(_t,LITERAL_friend);
			_t = _t.getFirstChild();
			moduleName(_t);
			_t = _retTree;
			_t = __t44;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void outerDefn(AST _t) throws RecognitionException {
		
		AST outerDefn_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TOP_LEVEL_TYPE_DECLARATION:
			{
				topLevelTypeDeclaration(_t);
				_t = _retTree;
				break;
			}
			case FOREIGN_DATA_DECLARATION:
			{
				foreignDataDeclaration(_t);
				_t = _retTree;
				break;
			}
			case DATA_DECLARATION:
			{
				dataDeclaration(_t);
				_t = _retTree;
				break;
			}
			case TYPE_CLASS_DEFN:
			{
				typeClassDefn(_t);
				_t = _retTree;
				break;
			}
			case INSTANCE_DEFN:
			{
				instanceDefn(_t);
				_t = _retTree;
				break;
			}
			case FOREIGN_FUNCTION_DECLARATION:
			{
				foreignFunctionDeclaration(_t);
				_t = _retTree;
				break;
			}
			case PRIMITIVE_FUNCTION_DECLARATION:
			{
				primitiveFunctionDeclaration(_t);
				_t = _retTree;
				break;
			}
			case TOP_LEVEL_FUNCTION_DEFN:
			{
				topLevelFunction(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void foreignDataDeclaration(AST _t) throws RecognitionException {
		
		AST foreignDataDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t93 = _t;
			AST tmp55_AST_in = (AST)_t;
			match(_t,FOREIGN_DATA_DECLARATION);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			externalName(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			cons(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_deriving:
			{
				derivingClause(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t93;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void dataDeclaration(AST _t) throws RecognitionException {
		
		AST dataDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t96 = _t;
			AST tmp56_AST_in = (AST)_t;
			match(_t,DATA_DECLARATION);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			typeConsName(_t);
			_t = _retTree;
			typeConsParamList(_t);
			_t = _retTree;
			dataConstructorDefnList(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_deriving:
			{
				derivingClause(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t96;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeClassDefn(AST _t) throws RecognitionException {
		
		AST typeClassDefn_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t122 = _t;
			AST tmp57_AST_in = (AST)_t;
			match(_t,TYPE_CLASS_DEFN);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			classContextList(_t);
			_t = _retTree;
			typeClassName(_t);
			_t = _retTree;
			var(_t);
			_t = _retTree;
			classMethodList(_t);
			_t = _retTree;
			_t = __t122;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void foreignFunctionDeclaration(AST _t) throws RecognitionException {
		
		AST foreignFunctionDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t154 = _t;
			AST tmp58_AST_in = (AST)_t;
			match(_t,FOREIGN_FUNCTION_DECLARATION);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			externalName(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			typeDeclaration(_t);
			_t = _retTree;
			_t = __t154;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void primitiveFunctionDeclaration(AST _t) throws RecognitionException {
		
		AST primitiveFunctionDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t157 = _t;
			AST tmp59_AST_in = (AST)_t;
			match(_t,PRIMITIVE_FUNCTION_DECLARATION);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			typeDeclaration(_t);
			_t = _retTree;
			_t = __t157;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeDeclaration(AST _t) throws RecognitionException {
		
		AST typeDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t55 = _t;
			AST tmp60_AST_in = (AST)_t;
			match(_t,TYPE_DECLARATION);
			_t = _t.getFirstChild();
			var(_t);
			_t = _retTree;
			typeSignature(_t);
			_t = _retTree;
			_t = __t55;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void letDefnTypeDeclaration(AST _t) throws RecognitionException {
		
		AST letDefnTypeDeclaration_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t53 = _t;
			AST tmp61_AST_in = (AST)_t;
			match(_t,LET_DEFN_TYPE_DECLARATION);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			typeDeclaration(_t);
			_t = _retTree;
			_t = __t53;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeContextList(AST _t) throws RecognitionException {
		
		AST typeContextList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TYPE_CONTEXT_LIST:
			{
				AST __t59 = _t;
				AST tmp62_AST_in = (AST)_t;
				match(_t,TYPE_CONTEXT_LIST);
				_t = _t.getFirstChild();
				{
				_loop61:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CLASS_CONTEXT||_t.getType()==LACKS_FIELD_CONTEXT)) {
						typeContext(_t);
						_t = _retTree;
					}
					else {
						break _loop61;
					}
					
				} while (true);
				}
				_t = __t59;
				_t = _t.getNextSibling();
				break;
			}
			case TYPE_CONTEXT_SINGLETON:
			{
				AST __t62 = _t;
				AST tmp63_AST_in = (AST)_t;
				match(_t,TYPE_CONTEXT_SINGLETON);
				_t = _t.getFirstChild();
				typeContext(_t);
				_t = _retTree;
				_t = __t62;
				_t = _t.getNextSibling();
				break;
			}
			case TYPE_CONTEXT_NOTHING:
			{
				AST tmp64_AST_in = (AST)_t;
				match(_t,TYPE_CONTEXT_NOTHING);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void type(AST _t) throws RecognitionException {
		
		AST type_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FUNCTION_TYPE_CONSTRUCTOR:
			{
				AST __t74 = _t;
				AST tmp65_AST_in = (AST)_t;
				match(_t,FUNCTION_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				type(_t);
				_t = _retTree;
				type(_t);
				_t = _retTree;
				_t = __t74;
				_t = _t.getNextSibling();
				break;
			}
			case TUPLE_TYPE_CONSTRUCTOR:
			{
				AST __t75 = _t;
				AST tmp66_AST_in = (AST)_t;
				match(_t,TUPLE_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				{
				_loop77:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_2.member(_t.getType()))) {
						type(_t);
						_t = _retTree;
					}
					else {
						break _loop77;
					}
					
				} while (true);
				}
				_t = __t75;
				_t = _t.getNextSibling();
				break;
			}
			case LIST_TYPE_CONSTRUCTOR:
			{
				AST __t78 = _t;
				AST tmp67_AST_in = (AST)_t;
				match(_t,LIST_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				type(_t);
				_t = _retTree;
				_t = __t78;
				_t = _t.getNextSibling();
				break;
			}
			case TYPE_APPLICATION:
			{
				AST __t79 = _t;
				AST tmp68_AST_in = (AST)_t;
				match(_t,TYPE_APPLICATION);
				_t = _t.getFirstChild();
				{
				int _cnt81=0;
				_loop81:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_2.member(_t.getType()))) {
						type(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt81>=1 ) { break _loop81; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt81++;
				} while (true);
				}
				_t = __t79;
				_t = _t.getNextSibling();
				break;
			}
			case QUALIFIED_CONS:
			{
				qualifiedCons(_t);
				_t = _retTree;
				break;
			}
			case VAR_ID:
			{
				var(_t);
				_t = _retTree;
				break;
			}
			case RECORD_TYPE_CONSTRUCTOR:
			{
				AST __t82 = _t;
				AST tmp69_AST_in = (AST)_t;
				match(_t,RECORD_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				recordVar(_t);
				_t = _retTree;
				fieldTypeAssignmentList(_t);
				_t = _retTree;
				_t = __t82;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeContext(AST _t) throws RecognitionException {
		
		AST typeContext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CLASS_CONTEXT:
			{
				classContext(_t);
				_t = _retTree;
				break;
			}
			case LACKS_FIELD_CONTEXT:
			{
				lacksContext(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void classContextList(AST _t) throws RecognitionException {
		
		AST classContextList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CLASS_CONTEXT_LIST:
			{
				AST __t64 = _t;
				AST tmp70_AST_in = (AST)_t;
				match(_t,CLASS_CONTEXT_LIST);
				_t = _t.getFirstChild();
				{
				_loop66:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CLASS_CONTEXT)) {
						classContext(_t);
						_t = _retTree;
					}
					else {
						break _loop66;
					}
					
				} while (true);
				}
				_t = __t64;
				_t = _t.getNextSibling();
				break;
			}
			case CLASS_CONTEXT_SINGLETON:
			{
				AST __t67 = _t;
				AST tmp71_AST_in = (AST)_t;
				match(_t,CLASS_CONTEXT_SINGLETON);
				_t = _t.getFirstChild();
				classContext(_t);
				_t = _retTree;
				_t = __t67;
				_t = _t.getNextSibling();
				break;
			}
			case CLASS_CONTEXT_NOTHING:
			{
				AST tmp72_AST_in = (AST)_t;
				match(_t,CLASS_CONTEXT_NOTHING);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void classContext(AST _t) throws RecognitionException {
		
		AST classContext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t70 = _t;
			AST tmp73_AST_in = (AST)_t;
			match(_t,CLASS_CONTEXT);
			_t = _t.getFirstChild();
			qualifiedCons(_t);
			_t = _retTree;
			var(_t);
			_t = _retTree;
			_t = __t70;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void lacksContext(AST _t) throws RecognitionException {
		
		AST lacksContext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t72 = _t;
			AST tmp74_AST_in = (AST)_t;
			match(_t,LACKS_FIELD_CONTEXT);
			_t = _t.getFirstChild();
			var(_t);
			_t = _retTree;
			fieldName(_t);
			_t = _retTree;
			_t = __t72;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void qualifiedCons(AST _t) throws RecognitionException {
		
		AST qualifiedCons_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t229 = _t;
			AST tmp75_AST_in = (AST)_t;
			match(_t,QUALIFIED_CONS);
			_t = _t.getFirstChild();
			maybeModuleName(_t);
			_t = _retTree;
			cons(_t);
			_t = _retTree;
			_t = __t229;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldName(AST _t) throws RecognitionException {
		
		AST fieldName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR_ID:
			{
				var(_t);
				_t = _retTree;
				break;
			}
			case ORDINAL_FIELD_NAME:
			{
				AST tmp76_AST_in = (AST)_t;
				match(_t,ORDINAL_FIELD_NAME);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void recordVar(AST _t) throws RecognitionException {
		
		AST recordVar_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t84 = _t;
			AST tmp77_AST_in = (AST)_t;
			match(_t,RECORD_VAR);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR_ID:
			{
				var(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t84;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldTypeAssignmentList(AST _t) throws RecognitionException {
		
		AST fieldTypeAssignmentList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t87 = _t;
			AST tmp78_AST_in = (AST)_t;
			match(_t,FIELD_TYPE_ASSIGNMENT_LIST);
			_t = _t.getFirstChild();
			{
			_loop89:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==FIELD_TYPE_ASSIGNMENT)) {
					fieldTypeAssignment(_t);
					_t = _retTree;
				}
				else {
					break _loop89;
				}
				
			} while (true);
			}
			_t = __t87;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldTypeAssignment(AST _t) throws RecognitionException {
		
		AST fieldTypeAssignment_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t91 = _t;
			AST tmp79_AST_in = (AST)_t;
			match(_t,FIELD_TYPE_ASSIGNMENT);
			_t = _t.getFirstChild();
			fieldName(_t);
			_t = _retTree;
			type(_t);
			_t = _retTree;
			_t = __t91;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void accessModifier(AST _t) throws RecognitionException {
		
		AST accessModifier_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t161 = _t;
			AST tmp80_AST_in = (AST)_t;
			match(_t,ACCESS_MODIFIER);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LITERAL_public:
			{
				AST tmp81_AST_in = (AST)_t;
				match(_t,LITERAL_public);
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_private:
			{
				AST tmp82_AST_in = (AST)_t;
				match(_t,LITERAL_private);
				_t = _t.getNextSibling();
				break;
			}
			case LITERAL_protected:
			{
				AST tmp83_AST_in = (AST)_t;
				match(_t,LITERAL_protected);
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t161;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void externalName(AST _t) throws RecognitionException {
		
		AST externalName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST tmp84_AST_in = (AST)_t;
			match(_t,STRING_LITERAL);
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void derivingClause(AST _t) throws RecognitionException {
		
		AST derivingClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t118 = _t;
			AST tmp85_AST_in = (AST)_t;
			match(_t,LITERAL_deriving);
			_t = _t.getFirstChild();
			{
			int _cnt120=0;
			_loop120:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==QUALIFIED_CONS)) {
					qualifiedCons(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt120>=1 ) { break _loop120; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt120++;
			} while (true);
			}
			_t = __t118;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeConsName(AST _t) throws RecognitionException {
		
		AST typeConsName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			cons(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeConsParamList(AST _t) throws RecognitionException {
		
		AST typeConsParamList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			AST tmp86_AST_in = (AST)_t;
			match(_t,TYPE_CONS_PARAM_LIST);
			_t = _t.getNextSibling();
			{
			_loop102:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==VAR_ID)) {
					var(_t);
					_t = _retTree;
				}
				else {
					break _loop102;
				}
				
			} while (true);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void dataConstructorDefnList(AST _t) throws RecognitionException {
		
		AST dataConstructorDefnList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t104 = _t;
			AST tmp87_AST_in = (AST)_t;
			match(_t,DATA_CONSTRUCTOR_DEFN_LIST);
			_t = _t.getFirstChild();
			{
			int _cnt106=0;
			_loop106:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==DATA_CONSTRUCTOR_DEFN)) {
					dataConstructorDefn(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt106>=1 ) { break _loop106; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt106++;
			} while (true);
			}
			_t = __t104;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void dataConstructorDefn(AST _t) throws RecognitionException {
		
		AST dataConstructorDefn_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t108 = _t;
			AST tmp88_AST_in = (AST)_t;
			match(_t,DATA_CONSTRUCTOR_DEFN);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			cons(_t);
			_t = _retTree;
			dataConstructorArgList(_t);
			_t = _retTree;
			_t = __t108;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void dataConstructorArgList(AST _t) throws RecognitionException {
		
		AST dataConstructorArgList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t110 = _t;
			AST tmp89_AST_in = (AST)_t;
			match(_t,DATA_CONSTRUCTOR_ARG_LIST);
			_t = _t.getFirstChild();
			{
			_loop112:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==DATA_CONSTRUCTOR_NAMED_ARG)) {
					dataConstructorArg(_t);
					_t = _retTree;
				}
				else {
					break _loop112;
				}
				
			} while (true);
			}
			_t = __t110;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void dataConstructorArg(AST _t) throws RecognitionException {
		
		AST dataConstructorArg_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t114 = _t;
			AST tmp90_AST_in = (AST)_t;
			match(_t,DATA_CONSTRUCTOR_NAMED_ARG);
			_t = _t.getFirstChild();
			fieldName(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case STRICT_ARG:
			{
				AST __t116 = _t;
				AST tmp91_AST_in = (AST)_t;
				match(_t,STRICT_ARG);
				_t = _t.getFirstChild();
				type(_t);
				_t = _retTree;
				_t = __t116;
				_t = _t.getNextSibling();
				break;
			}
			case VAR_ID:
			case FUNCTION_TYPE_CONSTRUCTOR:
			case TYPE_APPLICATION:
			case QUALIFIED_CONS:
			case LIST_TYPE_CONSTRUCTOR:
			case TUPLE_TYPE_CONSTRUCTOR:
			case RECORD_TYPE_CONSTRUCTOR:
			{
				type(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t114;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void typeClassName(AST _t) throws RecognitionException {
		
		AST typeClassName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			cons(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void classMethodList(AST _t) throws RecognitionException {
		
		AST classMethodList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t125 = _t;
			AST tmp92_AST_in = (AST)_t;
			match(_t,CLASS_METHOD_LIST);
			_t = _t.getFirstChild();
			{
			_loop127:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CLASS_METHOD)) {
					classMethod(_t);
					_t = _retTree;
				}
				else {
					break _loop127;
				}
				
			} while (true);
			}
			_t = __t125;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void classMethod(AST _t) throws RecognitionException {
		
		AST classMethod_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t129 = _t;
			AST tmp93_AST_in = (AST)_t;
			match(_t,CLASS_METHOD);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			accessModifier(_t);
			_t = _retTree;
			classMethodName(_t);
			_t = _retTree;
			typeSignature(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case QUALIFIED_VAR:
			{
				defaultClassMethodName(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t129;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void classMethodName(AST _t) throws RecognitionException {
		
		AST classMethodName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			var(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void defaultClassMethodName(AST _t) throws RecognitionException {
		
		AST defaultClassMethodName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			qualifiedVar(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void qualifiedVar(AST _t) throws RecognitionException {
		
		AST qualifiedVar_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t227 = _t;
			AST tmp94_AST_in = (AST)_t;
			match(_t,QUALIFIED_VAR);
			_t = _t.getFirstChild();
			maybeModuleName(_t);
			_t = _retTree;
			var(_t);
			_t = _retTree;
			_t = __t227;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void instanceName(AST _t) throws RecognitionException {
		
		AST instanceName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t136 = _t;
			AST tmp95_AST_in = (AST)_t;
			match(_t,INSTANCE_NAME);
			_t = _t.getFirstChild();
			classContextList(_t);
			_t = _retTree;
			qualifiedCons(_t);
			_t = _retTree;
			instanceTypeConsName(_t);
			_t = _retTree;
			_t = __t136;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void instanceMethodList(AST _t) throws RecognitionException {
		
		AST instanceMethodList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t146 = _t;
			AST tmp96_AST_in = (AST)_t;
			match(_t,INSTANCE_METHOD_LIST);
			_t = _t.getFirstChild();
			{
			_loop148:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==INSTANCE_METHOD)) {
					instanceMethod(_t);
					_t = _retTree;
				}
				else {
					break _loop148;
				}
				
			} while (true);
			}
			_t = __t146;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void instanceTypeConsName(AST _t) throws RecognitionException {
		
		AST instanceTypeConsName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case GENERAL_TYPE_CONSTRUCTOR:
			{
				AST __t138 = _t;
				AST tmp97_AST_in = (AST)_t;
				match(_t,GENERAL_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				qualifiedCons(_t);
				_t = _retTree;
				{
				_loop140:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==VAR_ID)) {
						var(_t);
						_t = _retTree;
					}
					else {
						break _loop140;
					}
					
				} while (true);
				}
				_t = __t138;
				_t = _t.getNextSibling();
				break;
			}
			case UNPARENTHESIZED_TYPE_CONSTRUCTOR:
			{
				AST __t141 = _t;
				AST tmp98_AST_in = (AST)_t;
				match(_t,UNPARENTHESIZED_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				qualifiedCons(_t);
				_t = _retTree;
				_t = __t141;
				_t = _t.getNextSibling();
				break;
			}
			case FUNCTION_TYPE_CONSTRUCTOR:
			{
				AST __t142 = _t;
				AST tmp99_AST_in = (AST)_t;
				match(_t,FUNCTION_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				var(_t);
				_t = _retTree;
				var(_t);
				_t = _retTree;
				_t = __t142;
				_t = _t.getNextSibling();
				break;
			}
			case UNIT_TYPE_CONSTRUCTOR:
			{
				AST tmp100_AST_in = (AST)_t;
				match(_t,UNIT_TYPE_CONSTRUCTOR);
				_t = _t.getNextSibling();
				break;
			}
			case LIST_TYPE_CONSTRUCTOR:
			{
				AST __t143 = _t;
				AST tmp101_AST_in = (AST)_t;
				match(_t,LIST_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				var(_t);
				_t = _retTree;
				_t = __t143;
				_t = _t.getNextSibling();
				break;
			}
			case RECORD_TYPE_CONSTRUCTOR:
			{
				AST __t144 = _t;
				AST tmp102_AST_in = (AST)_t;
				match(_t,RECORD_TYPE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				var(_t);
				_t = _retTree;
				_t = __t144;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void instanceMethod(AST _t) throws RecognitionException {
		
		AST instanceMethod_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t150 = _t;
			AST tmp103_AST_in = (AST)_t;
			match(_t,INSTANCE_METHOD);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			instanceMethodName(_t);
			_t = _retTree;
			instanceMethodDefn(_t);
			_t = _retTree;
			_t = __t150;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void instanceMethodName(AST _t) throws RecognitionException {
		
		AST instanceMethodName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			var(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void instanceMethodDefn(AST _t) throws RecognitionException {
		
		AST instanceMethodDefn_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			qualifiedVar(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void functionName(AST _t) throws RecognitionException {
		
		AST functionName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			var(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void functionParamList(AST _t) throws RecognitionException {
		
		AST functionParamList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t165 = _t;
			AST tmp104_AST_in = (AST)_t;
			match(_t,FUNCTION_PARAM_LIST);
			_t = _t.getFirstChild();
			{
			_loop167:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==STRICT_PARAM||_t.getType()==LAZY_PARAM)) {
					functionParam(_t);
					_t = _retTree;
				}
				else {
					break _loop167;
				}
				
			} while (true);
			}
			_t = __t165;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void functionParam(AST _t) throws RecognitionException {
		
		AST functionParam_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LAZY_PARAM:
			{
				AST tmp105_AST_in = (AST)_t;
				match(_t,LAZY_PARAM);
				_t = _t.getNextSibling();
				break;
			}
			case STRICT_PARAM:
			{
				AST tmp106_AST_in = (AST)_t;
				match(_t,STRICT_PARAM);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void lambdaParamList(AST _t) throws RecognitionException {
		
		AST lambdaParamList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t169 = _t;
			AST tmp107_AST_in = (AST)_t;
			match(_t,FUNCTION_PARAM_LIST);
			_t = _t.getFirstChild();
			{
			int _cnt171=0;
			_loop171:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==STRICT_PARAM||_t.getType()==LAZY_PARAM)) {
					functionParam(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt171>=1 ) { break _loop171; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt171++;
			} while (true);
			}
			_t = __t169;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void letDefnList(AST _t) throws RecognitionException {
		
		AST letDefnList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t231 = _t;
			AST tmp108_AST_in = (AST)_t;
			match(_t,LET_DEFN_LIST);
			_t = _t.getFirstChild();
			{
			int _cnt233=0;
			_loop233:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case LET_DEFN:
				{
					letDefn(_t);
					_t = _retTree;
					break;
				}
				case LET_DEFN_TYPE_DECLARATION:
				{
					letDefnTypeDeclaration(_t);
					_t = _retTree;
					break;
				}
				case LET_PATTERN_MATCH_DECL:
				{
					letPatternMatchDecl(_t);
					_t = _retTree;
					break;
				}
				default:
				{
					if ( _cnt233>=1 ) { break _loop233; } else {throw new NoViableAltException(_t);}
				}
				}
				_cnt233++;
			} while (true);
			}
			_t = __t231;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void altList(AST _t) throws RecognitionException {
		
		AST altList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t247 = _t;
			AST tmp109_AST_in = (AST)_t;
			match(_t,ALT_LIST);
			_t = _t.getFirstChild();
			{
			int _cnt249=0;
			_loop249:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==ALT)) {
					alt(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt249>=1 ) { break _loop249; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt249++;
			} while (true);
			}
			_t = __t247;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void literal(AST _t) throws RecognitionException {
		
		AST literal_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INTEGER_LITERAL:
			{
				AST tmp110_AST_in = (AST)_t;
				match(_t,INTEGER_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			case FLOAT_LITERAL:
			{
				AST tmp111_AST_in = (AST)_t;
				match(_t,FLOAT_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			case CHAR_LITERAL:
			{
				AST tmp112_AST_in = (AST)_t;
				match(_t,CHAR_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			case STRING_LITERAL:
			{
				AST tmp113_AST_in = (AST)_t;
				match(_t,STRING_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void baseRecord(AST _t) throws RecognitionException {
		
		AST baseRecord_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t215 = _t;
			AST tmp114_AST_in = (AST)_t;
			match(_t,BASE_RECORD);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LAMBDA_DEFN:
			case APPLICATION:
			case TUPLE_CONSTRUCTOR:
			case LIST_CONSTRUCTOR:
			case QUALIFIED_VAR:
			case QUALIFIED_CONS:
			case RECORD_CONSTRUCTOR:
			case SELECT_RECORD_FIELD:
			case SELECT_DATA_CONSTRUCTOR_FIELD:
			case EXPRESSION_TYPE_SIGNATURE:
			case UNARY_MINUS:
			case STRING_LITERAL:
			case LITERAL_let:
			case LITERAL_if:
			case LITERAL_case:
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
			case POUND:
			case BACKQUOTE:
			case CHAR_LITERAL:
			case INTEGER_LITERAL:
			case FLOAT_LITERAL:
			{
				expr(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t215;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldModificationList(AST _t) throws RecognitionException {
		
		AST fieldModificationList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t218 = _t;
			AST tmp115_AST_in = (AST)_t;
			match(_t,FIELD_MODIFICATION_LIST);
			_t = _t.getFirstChild();
			{
			_loop220:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case FIELD_EXTENSION:
				{
					fieldExtension(_t);
					_t = _retTree;
					break;
				}
				case FIELD_VALUE_UPDATE:
				{
					fieldValueUpdate(_t);
					_t = _retTree;
					break;
				}
				default:
				{
					break _loop220;
				}
				}
			} while (true);
			}
			_t = __t218;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldExtension(AST _t) throws RecognitionException {
		
		AST fieldExtension_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t222 = _t;
			AST tmp116_AST_in = (AST)_t;
			match(_t,FIELD_EXTENSION);
			_t = _t.getFirstChild();
			fieldName(_t);
			_t = _retTree;
			expr(_t);
			_t = _retTree;
			_t = __t222;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldValueUpdate(AST _t) throws RecognitionException {
		
		AST fieldValueUpdate_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t224 = _t;
			AST tmp117_AST_in = (AST)_t;
			match(_t,FIELD_VALUE_UPDATE);
			_t = _t.getFirstChild();
			fieldName(_t);
			_t = _retTree;
			expr(_t);
			_t = _retTree;
			_t = __t224;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void letDefn(AST _t) throws RecognitionException {
		
		AST letDefn_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t235 = _t;
			AST tmp118_AST_in = (AST)_t;
			match(_t,LET_DEFN);
			_t = _t.getFirstChild();
			optionalDocComment(_t);
			_t = _retTree;
			functionName(_t);
			_t = _retTree;
			functionParamList(_t);
			_t = _retTree;
			expr(_t);
			_t = _retTree;
			_t = __t235;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void letPatternMatchDecl(AST _t) throws RecognitionException {
		
		AST letPatternMatchDecl_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t237 = _t;
			AST tmp119_AST_in = (AST)_t;
			match(_t,LET_PATTERN_MATCH_DECL);
			_t = _t.getFirstChild();
			letPatternMatchPattern(_t);
			_t = _retTree;
			expr(_t);
			_t = _retTree;
			_t = __t237;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void letPatternMatchPattern(AST _t) throws RecognitionException {
		
		AST letPatternMatchPattern_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PATTERN_CONSTRUCTOR:
			{
				AST __t239 = _t;
				AST tmp120_AST_in = (AST)_t;
				match(_t,PATTERN_CONSTRUCTOR);
				_t = _t.getFirstChild();
				AST __t240 = _t;
				AST tmp121_AST_in = (AST)_t;
				match(_t,DATA_CONSTRUCTOR_NAME_SINGLETON);
				_t = _t.getFirstChild();
				qualifiedCons(_t);
				_t = _retTree;
				_t = __t240;
				_t = _t.getNextSibling();
				dataConstructorArgumentBindings(_t);
				_t = _retTree;
				_t = __t239;
				_t = _t.getNextSibling();
				break;
			}
			case TUPLE_CONSTRUCTOR:
			{
				AST __t241 = _t;
				AST tmp122_AST_in = (AST)_t;
				match(_t,TUPLE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				{
				_loop243:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==VAR_ID||_t.getType()==UNDERSCORE)) {
						patternVar(_t);
						_t = _retTree;
					}
					else {
						break _loop243;
					}
					
				} while (true);
				}
				_t = __t241;
				_t = _t.getNextSibling();
				break;
			}
			case COLON:
			{
				AST __t244 = _t;
				AST tmp123_AST_in = (AST)_t;
				match(_t,COLON);
				_t = _t.getFirstChild();
				patternVar(_t);
				_t = _retTree;
				patternVar(_t);
				_t = _retTree;
				_t = __t244;
				_t = _t.getNextSibling();
				break;
			}
			case RECORD_PATTERN:
			{
				AST __t245 = _t;
				AST tmp124_AST_in = (AST)_t;
				match(_t,RECORD_PATTERN);
				_t = _t.getFirstChild();
				baseRecordPattern(_t);
				_t = _retTree;
				fieldBindingVarAssignmentList(_t);
				_t = _retTree;
				_t = __t245;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void dataConstructorArgumentBindings(AST _t) throws RecognitionException {
		
		AST dataConstructorArgumentBindings_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PATTERN_VAR_LIST:
			{
				patternVarListZeroOrMore(_t);
				_t = _retTree;
				break;
			}
			case FIELD_BINDING_VAR_ASSIGNMENT_LIST:
			{
				fieldBindingVarAssignmentList(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void patternVar(AST _t) throws RecognitionException {
		
		AST patternVar_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR_ID:
			{
				var(_t);
				_t = _retTree;
				break;
			}
			case UNDERSCORE:
			{
				wildcard(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void baseRecordPattern(AST _t) throws RecognitionException {
		
		AST baseRecordPattern_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t284 = _t;
			AST tmp125_AST_in = (AST)_t;
			match(_t,BASE_RECORD_PATTERN);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR_ID:
			case UNDERSCORE:
			{
				patternVar(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t284;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldBindingVarAssignmentList(AST _t) throws RecognitionException {
		
		AST fieldBindingVarAssignmentList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t287 = _t;
			AST tmp126_AST_in = (AST)_t;
			match(_t,FIELD_BINDING_VAR_ASSIGNMENT_LIST);
			_t = _t.getFirstChild();
			{
			_loop289:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==FIELD_BINDING_VAR_ASSIGNMENT)) {
					fieldBindingVarAssignment(_t);
					_t = _retTree;
				}
				else {
					break _loop289;
				}
				
			} while (true);
			}
			_t = __t287;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void alt(AST _t) throws RecognitionException {
		
		AST alt_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t251 = _t;
			AST tmp127_AST_in = (AST)_t;
			match(_t,ALT);
			_t = _t.getFirstChild();
			pat(_t);
			_t = _retTree;
			expr(_t);
			_t = _retTree;
			_t = __t251;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void pat(AST _t) throws RecognitionException {
		
		AST pat_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PATTERN_CONSTRUCTOR:
			{
				AST __t253 = _t;
				AST tmp128_AST_in = (AST)_t;
				match(_t,PATTERN_CONSTRUCTOR);
				_t = _t.getFirstChild();
				dataConstructorNameListOneOrMore(_t);
				_t = _retTree;
				dataConstructorArgumentBindings(_t);
				_t = _retTree;
				_t = __t253;
				_t = _t.getNextSibling();
				break;
			}
			case TUPLE_CONSTRUCTOR:
			{
				AST __t254 = _t;
				AST tmp129_AST_in = (AST)_t;
				match(_t,TUPLE_CONSTRUCTOR);
				_t = _t.getFirstChild();
				{
				_loop256:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==VAR_ID||_t.getType()==UNDERSCORE)) {
						patternVar(_t);
						_t = _retTree;
					}
					else {
						break _loop256;
					}
					
				} while (true);
				}
				_t = __t254;
				_t = _t.getNextSibling();
				break;
			}
			case LIST_CONSTRUCTOR:
			{
				AST tmp130_AST_in = (AST)_t;
				match(_t,LIST_CONSTRUCTOR);
				_t = _t.getNextSibling();
				break;
			}
			case INT_PATTERN:
			{
				AST __t257 = _t;
				AST tmp131_AST_in = (AST)_t;
				match(_t,INT_PATTERN);
				_t = _t.getFirstChild();
				maybeMinusIntListOneOrMore(_t);
				_t = _retTree;
				_t = __t257;
				_t = _t.getNextSibling();
				break;
			}
			case CHAR_PATTERN:
			{
				AST __t258 = _t;
				AST tmp132_AST_in = (AST)_t;
				match(_t,CHAR_PATTERN);
				_t = _t.getFirstChild();
				charListOneOrMore(_t);
				_t = _retTree;
				_t = __t258;
				_t = _t.getNextSibling();
				break;
			}
			case COLON:
			{
				AST __t259 = _t;
				AST tmp133_AST_in = (AST)_t;
				match(_t,COLON);
				_t = _t.getFirstChild();
				patternVar(_t);
				_t = _retTree;
				patternVar(_t);
				_t = _retTree;
				_t = __t259;
				_t = _t.getNextSibling();
				break;
			}
			case UNDERSCORE:
			{
				wildcard(_t);
				_t = _retTree;
				break;
			}
			case RECORD_PATTERN:
			{
				AST __t260 = _t;
				AST tmp134_AST_in = (AST)_t;
				match(_t,RECORD_PATTERN);
				_t = _t.getFirstChild();
				baseRecordPattern(_t);
				_t = _retTree;
				fieldBindingVarAssignmentList(_t);
				_t = _retTree;
				_t = __t260;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void dataConstructorNameListOneOrMore(AST _t) throws RecognitionException {
		
		AST dataConstructorNameListOneOrMore_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DATA_CONSTRUCTOR_NAME_LIST:
			{
				AST __t267 = _t;
				AST tmp135_AST_in = (AST)_t;
				match(_t,DATA_CONSTRUCTOR_NAME_LIST);
				_t = _t.getFirstChild();
				{
				int _cnt269=0;
				_loop269:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==QUALIFIED_CONS)) {
						qualifiedCons(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt269>=1 ) { break _loop269; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt269++;
				} while (true);
				}
				_t = __t267;
				_t = _t.getNextSibling();
				break;
			}
			case DATA_CONSTRUCTOR_NAME_SINGLETON:
			{
				AST __t270 = _t;
				AST tmp136_AST_in = (AST)_t;
				match(_t,DATA_CONSTRUCTOR_NAME_SINGLETON);
				_t = _t.getFirstChild();
				qualifiedCons(_t);
				_t = _retTree;
				_t = __t270;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void maybeMinusIntListOneOrMore(AST _t) throws RecognitionException {
		
		AST maybeMinusIntListOneOrMore_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t272 = _t;
			AST tmp137_AST_in = (AST)_t;
			match(_t,MAYBE_MINUS_INT_LIST);
			_t = _t.getFirstChild();
			{
			int _cnt274=0;
			_loop274:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==MINUS||_t.getType()==INTEGER_LITERAL)) {
					maybeMinusInt(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt274>=1 ) { break _loop274; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt274++;
			} while (true);
			}
			_t = __t272;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void charListOneOrMore(AST _t) throws RecognitionException {
		
		AST charListOneOrMore_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t278 = _t;
			AST tmp138_AST_in = (AST)_t;
			match(_t,CHAR_LIST);
			_t = _t.getFirstChild();
			{
			int _cnt280=0;
			_loop280:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CHAR_LITERAL)) {
					AST tmp139_AST_in = (AST)_t;
					match(_t,CHAR_LITERAL);
					_t = _t.getNextSibling();
				}
				else {
					if ( _cnt280>=1 ) { break _loop280; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt280++;
			} while (true);
			}
			_t = __t278;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void wildcard(AST _t) throws RecognitionException {
		
		AST wildcard_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST tmp140_AST_in = (AST)_t;
			match(_t,UNDERSCORE);
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void patternVarListZeroOrMore(AST _t) throws RecognitionException {
		
		AST patternVarListZeroOrMore_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t263 = _t;
			AST tmp141_AST_in = (AST)_t;
			match(_t,PATTERN_VAR_LIST);
			_t = _t.getFirstChild();
			{
			_loop265:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==VAR_ID||_t.getType()==UNDERSCORE)) {
					patternVar(_t);
					_t = _retTree;
				}
				else {
					break _loop265;
				}
				
			} while (true);
			}
			_t = __t263;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void maybeMinusInt(AST _t) throws RecognitionException {
		
		AST maybeMinusInt_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case MINUS:
			{
				AST __t276 = _t;
				AST tmp142_AST_in = (AST)_t;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				AST tmp143_AST_in = (AST)_t;
				match(_t,INTEGER_LITERAL);
				_t = _t.getNextSibling();
				_t = __t276;
				_t = _t.getNextSibling();
				break;
			}
			case INTEGER_LITERAL:
			{
				AST tmp144_AST_in = (AST)_t;
				match(_t,INTEGER_LITERAL);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void fieldBindingVarAssignment(AST _t) throws RecognitionException {
		
		AST fieldBindingVarAssignment_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t291 = _t;
			AST tmp145_AST_in = (AST)_t;
			match(_t,FIELD_BINDING_VAR_ASSIGNMENT);
			_t = _t.getFirstChild();
			fieldName(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR_ID:
			case UNDERSCORE:
			{
				patternVar(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t291;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void literal_index(AST _t) throws RecognitionException {
		
		AST literal_index_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST tmp146_AST_in = (AST)_t;
			match(_t,INTEGER_LITERAL);
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docComment(AST _t) throws RecognitionException {
		
		AST docComment_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t301 = _t;
			AST tmp147_AST_in = (AST)_t;
			match(_t,CALDOC_COMMENT);
			_t = _t.getFirstChild();
			docCommentDescriptionBlock(_t);
			_t = _retTree;
			docCommentTaggedBlocks(_t);
			_t = _retTree;
			_t = __t301;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentDescriptionBlock(AST _t) throws RecognitionException {
		
		AST docCommentDescriptionBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t303 = _t;
			AST tmp148_AST_in = (AST)_t;
			match(_t,CALDOC_DESCRIPTION_BLOCK);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t303;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentTaggedBlocks(AST _t) throws RecognitionException {
		
		AST docCommentTaggedBlocks_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t360 = _t;
			AST tmp149_AST_in = (AST)_t;
			match(_t,CALDOC_TAGGED_BLOCKS);
			_t = _t.getFirstChild();
			{
			_loop362:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case CALDOC_AUTHOR_BLOCK:
				{
					docCommentAuthorBlock(_t);
					_t = _retTree;
					break;
				}
				case CALDOC_DEPRECATED_BLOCK:
				{
					docCommentDeprecatedBlock(_t);
					_t = _retTree;
					break;
				}
				case CALDOC_RETURN_BLOCK:
				{
					docCommentReturnBlock(_t);
					_t = _retTree;
					break;
				}
				case CALDOC_VERSION_BLOCK:
				{
					docCommentVersionBlock(_t);
					_t = _retTree;
					break;
				}
				case CALDOC_ARG_BLOCK:
				{
					docCommentArgBlock(_t);
					_t = _retTree;
					break;
				}
				case CALDOC_SEE_BLOCK:
				{
					docCommentSeeBlock(_t);
					_t = _retTree;
					break;
				}
				default:
				{
					break _loop362;
				}
				}
			} while (true);
			}
			_t = __t360;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentTextualContent(AST _t) throws RecognitionException {
		
		AST docCommentTextualContent_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t305 = _t;
			AST tmp150_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT);
			_t = _t.getFirstChild();
			{
			_loop307:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case CALDOC_TEXT_LINE:
				{
					AST tmp151_AST_in = (AST)_t;
					match(_t,CALDOC_TEXT_LINE);
					_t = _t.getNextSibling();
					break;
				}
				case CALDOC_BLANK_TEXT_LINE:
				{
					AST tmp152_AST_in = (AST)_t;
					match(_t,CALDOC_BLANK_TEXT_LINE);
					_t = _t.getNextSibling();
					break;
				}
				case CALDOC_TEXT_INLINE_BLOCK:
				{
					docCommentInlineBlock(_t);
					_t = _retTree;
					break;
				}
				case CALDOC_TEXT_LINE_BREAK:
				{
					AST tmp153_AST_in = (AST)_t;
					match(_t,CALDOC_TEXT_LINE_BREAK);
					_t = _t.getNextSibling();
					break;
				}
				default:
				{
					break _loop307;
				}
				}
			} while (true);
			}
			_t = __t305;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t317 = _t;
			AST tmp154_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_INLINE_BLOCK);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CALDOC_TEXT_URL:
			{
				docCommentInlineUrlBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_LINK:
			{
				docCommentInlineLinkBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_EMPHASIZED_TEXT:
			{
				docCommentInlineEmBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT:
			{
				docCommentInlineStrongBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_SUPERSCRIPT_TEXT:
			{
				docCommentInlineSupBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_SUBSCRIPT_TEXT:
			{
				docCommentInlineSubBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_SUMMARY:
			{
				docCommentInlineSummaryBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_CODE_BLOCK:
			{
				docCommentInlineCodeBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_ORDERED_LIST:
			{
				docCommentInlineOrderedListBlock(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_UNORDERED_LIST:
			{
				docCommentInlineUnorderedListBlock(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t317;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentPreformattedBlock(AST _t) throws RecognitionException {
		
		AST docCommentPreformattedBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t309 = _t;
			AST tmp155_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_PREFORMATTED_BLOCK);
			_t = _t.getFirstChild();
			{
			_loop311:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case CALDOC_TEXT_LINE:
				{
					AST tmp156_AST_in = (AST)_t;
					match(_t,CALDOC_TEXT_LINE);
					_t = _t.getNextSibling();
					break;
				}
				case CALDOC_BLANK_TEXT_LINE:
				{
					AST tmp157_AST_in = (AST)_t;
					match(_t,CALDOC_BLANK_TEXT_LINE);
					_t = _t.getNextSibling();
					break;
				}
				case CALDOC_TEXT_INLINE_BLOCK:
				{
					docCommentInlineBlock(_t);
					_t = _retTree;
					break;
				}
				case CALDOC_TEXT_LINE_BREAK:
				{
					AST tmp158_AST_in = (AST)_t;
					match(_t,CALDOC_TEXT_LINE_BREAK);
					_t = _t.getNextSibling();
					break;
				}
				default:
				{
					break _loop311;
				}
				}
			} while (true);
			}
			_t = __t309;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentTextBlockWithoutInlineTags(AST _t) throws RecognitionException {
		
		AST docCommentTextBlockWithoutInlineTags_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t313 = _t;
			AST tmp159_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS);
			_t = _t.getFirstChild();
			{
			_loop315:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case CALDOC_TEXT_LINE:
				{
					AST tmp160_AST_in = (AST)_t;
					match(_t,CALDOC_TEXT_LINE);
					_t = _t.getNextSibling();
					break;
				}
				case CALDOC_BLANK_TEXT_LINE:
				{
					AST tmp161_AST_in = (AST)_t;
					match(_t,CALDOC_BLANK_TEXT_LINE);
					_t = _t.getNextSibling();
					break;
				}
				case CALDOC_TEXT_LINE_BREAK:
				{
					AST tmp162_AST_in = (AST)_t;
					match(_t,CALDOC_TEXT_LINE_BREAK);
					_t = _t.getNextSibling();
					break;
				}
				default:
				{
					break _loop315;
				}
				}
			} while (true);
			}
			_t = __t313;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineUrlBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineUrlBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t320 = _t;
			AST tmp163_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_URL);
			_t = _t.getFirstChild();
			docCommentTextBlockWithoutInlineTags(_t);
			_t = _retTree;
			_t = __t320;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineLinkBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineLinkBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t322 = _t;
			AST tmp164_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_LINK);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CALDOC_TEXT_LINK_FUNCTION:
			{
				docCommentLinkFunction(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_LINK_MODULE:
			{
				docCommentLinkModule(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_LINK_DATACONS:
			{
				docCommentLinkDataCons(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_LINK_TYPECONS:
			{
				docCommentLinkTypeCons(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_LINK_TYPECLASS:
			{
				docCommentLinkTypeClass(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_TEXT_LINK_WITHOUT_CONTEXT:
			{
				docCommentLinkWithoutContext(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t322;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineEmBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineEmBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t325 = _t;
			AST tmp165_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_EMPHASIZED_TEXT);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t325;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineStrongBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineStrongBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t327 = _t;
			AST tmp166_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t327;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineSupBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineSupBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t329 = _t;
			AST tmp167_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_SUPERSCRIPT_TEXT);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t329;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineSubBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineSubBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t331 = _t;
			AST tmp168_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_SUBSCRIPT_TEXT);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t331;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineSummaryBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineSummaryBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t333 = _t;
			AST tmp169_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_SUMMARY);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t333;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineCodeBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineCodeBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t335 = _t;
			AST tmp170_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_CODE_BLOCK);
			_t = _t.getFirstChild();
			docCommentPreformattedBlock(_t);
			_t = _retTree;
			_t = __t335;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineOrderedListBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineOrderedListBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t337 = _t;
			AST tmp171_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_ORDERED_LIST);
			_t = _t.getFirstChild();
			{
			_loop339:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CALDOC_TEXT_LIST_ITEM)) {
					docCommentListItem(_t);
					_t = _retTree;
				}
				else {
					break _loop339;
				}
				
			} while (true);
			}
			_t = __t337;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentInlineUnorderedListBlock(AST _t) throws RecognitionException {
		
		AST docCommentInlineUnorderedListBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t341 = _t;
			AST tmp172_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_UNORDERED_LIST);
			_t = _t.getFirstChild();
			{
			_loop343:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CALDOC_TEXT_LIST_ITEM)) {
					docCommentListItem(_t);
					_t = _retTree;
				}
				else {
					break _loop343;
				}
				
			} while (true);
			}
			_t = __t341;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentLinkFunction(AST _t) throws RecognitionException {
		
		AST docCommentLinkFunction_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t347 = _t;
			AST tmp173_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_LINK_FUNCTION);
			_t = _t.getFirstChild();
			docCommentMaybeUncheckedQualifiedVar(_t);
			_t = _retTree;
			_t = __t347;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentLinkModule(AST _t) throws RecognitionException {
		
		AST docCommentLinkModule_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t349 = _t;
			AST tmp174_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_LINK_MODULE);
			_t = _t.getFirstChild();
			docCommentMaybeUncheckedModuleName(_t);
			_t = _retTree;
			_t = __t349;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentLinkDataCons(AST _t) throws RecognitionException {
		
		AST docCommentLinkDataCons_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t351 = _t;
			AST tmp175_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_LINK_DATACONS);
			_t = _t.getFirstChild();
			docCommentMaybeUncheckedQualifiedCons(_t);
			_t = _retTree;
			_t = __t351;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentLinkTypeCons(AST _t) throws RecognitionException {
		
		AST docCommentLinkTypeCons_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t353 = _t;
			AST tmp176_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_LINK_TYPECONS);
			_t = _t.getFirstChild();
			docCommentMaybeUncheckedQualifiedCons(_t);
			_t = _retTree;
			_t = __t353;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentLinkTypeClass(AST _t) throws RecognitionException {
		
		AST docCommentLinkTypeClass_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t355 = _t;
			AST tmp177_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_LINK_TYPECLASS);
			_t = _t.getFirstChild();
			docCommentMaybeUncheckedQualifiedCons(_t);
			_t = _retTree;
			_t = __t355;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentLinkWithoutContext(AST _t) throws RecognitionException {
		
		AST docCommentLinkWithoutContext_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t357 = _t;
			AST tmp178_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_LINK_WITHOUT_CONTEXT);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CALDOC_CHECKED_QUALIFIED_CONS:
			case CALDOC_UNCHECKED_QUALIFIED_CONS:
			{
				docCommentMaybeUncheckedQualifiedCons(_t);
				_t = _retTree;
				break;
			}
			case CALDOC_CHECKED_QUALIFIED_VAR:
			case CALDOC_UNCHECKED_QUALIFIED_VAR:
			{
				docCommentMaybeUncheckedQualifiedVar(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t357;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentListItem(AST _t) throws RecognitionException {
		
		AST docCommentListItem_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t345 = _t;
			AST tmp179_AST_in = (AST)_t;
			match(_t,CALDOC_TEXT_LIST_ITEM);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t345;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentMaybeUncheckedQualifiedVar(AST _t) throws RecognitionException {
		
		AST docCommentMaybeUncheckedQualifiedVar_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CALDOC_CHECKED_QUALIFIED_VAR:
			{
				AST __t398 = _t;
				AST tmp180_AST_in = (AST)_t;
				match(_t,CALDOC_CHECKED_QUALIFIED_VAR);
				_t = _t.getFirstChild();
				qualifiedVar(_t);
				_t = _retTree;
				_t = __t398;
				_t = _t.getNextSibling();
				break;
			}
			case CALDOC_UNCHECKED_QUALIFIED_VAR:
			{
				AST __t399 = _t;
				AST tmp181_AST_in = (AST)_t;
				match(_t,CALDOC_UNCHECKED_QUALIFIED_VAR);
				_t = _t.getFirstChild();
				qualifiedVar(_t);
				_t = _retTree;
				_t = __t399;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentMaybeUncheckedModuleName(AST _t) throws RecognitionException {
		
		AST docCommentMaybeUncheckedModuleName_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CALDOC_CHECKED_MODULE_NAME:
			{
				AST __t395 = _t;
				AST tmp182_AST_in = (AST)_t;
				match(_t,CALDOC_CHECKED_MODULE_NAME);
				_t = _t.getFirstChild();
				moduleName(_t);
				_t = _retTree;
				_t = __t395;
				_t = _t.getNextSibling();
				break;
			}
			case CALDOC_UNCHECKED_MODULE_NAME:
			{
				AST __t396 = _t;
				AST tmp183_AST_in = (AST)_t;
				match(_t,CALDOC_UNCHECKED_MODULE_NAME);
				_t = _t.getFirstChild();
				moduleName(_t);
				_t = _retTree;
				_t = __t396;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentMaybeUncheckedQualifiedCons(AST _t) throws RecognitionException {
		
		AST docCommentMaybeUncheckedQualifiedCons_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CALDOC_CHECKED_QUALIFIED_CONS:
			{
				AST __t401 = _t;
				AST tmp184_AST_in = (AST)_t;
				match(_t,CALDOC_CHECKED_QUALIFIED_CONS);
				_t = _t.getFirstChild();
				qualifiedCons(_t);
				_t = _retTree;
				_t = __t401;
				_t = _t.getNextSibling();
				break;
			}
			case CALDOC_UNCHECKED_QUALIFIED_CONS:
			{
				AST __t402 = _t;
				AST tmp185_AST_in = (AST)_t;
				match(_t,CALDOC_UNCHECKED_QUALIFIED_CONS);
				_t = _t.getFirstChild();
				qualifiedCons(_t);
				_t = _retTree;
				_t = __t402;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentAuthorBlock(AST _t) throws RecognitionException {
		
		AST docCommentAuthorBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t364 = _t;
			AST tmp186_AST_in = (AST)_t;
			match(_t,CALDOC_AUTHOR_BLOCK);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t364;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentDeprecatedBlock(AST _t) throws RecognitionException {
		
		AST docCommentDeprecatedBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t366 = _t;
			AST tmp187_AST_in = (AST)_t;
			match(_t,CALDOC_DEPRECATED_BLOCK);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t366;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentReturnBlock(AST _t) throws RecognitionException {
		
		AST docCommentReturnBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t368 = _t;
			AST tmp188_AST_in = (AST)_t;
			match(_t,CALDOC_RETURN_BLOCK);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t368;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentVersionBlock(AST _t) throws RecognitionException {
		
		AST docCommentVersionBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t370 = _t;
			AST tmp189_AST_in = (AST)_t;
			match(_t,CALDOC_VERSION_BLOCK);
			_t = _t.getFirstChild();
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t370;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentArgBlock(AST _t) throws RecognitionException {
		
		AST docCommentArgBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t372 = _t;
			AST tmp190_AST_in = (AST)_t;
			match(_t,CALDOC_ARG_BLOCK);
			_t = _t.getFirstChild();
			fieldName(_t);
			_t = _retTree;
			docCommentTextualContent(_t);
			_t = _retTree;
			_t = __t372;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentSeeBlock(AST _t) throws RecognitionException {
		
		AST docCommentSeeBlock_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t374 = _t;
			AST tmp191_AST_in = (AST)_t;
			match(_t,CALDOC_SEE_BLOCK);
			_t = _t.getFirstChild();
			docCommentSeeBlockContent(_t);
			_t = _retTree;
			_t = __t374;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void docCommentSeeBlockContent(AST _t) throws RecognitionException {
		
		AST docCommentSeeBlockContent_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CALDOC_SEE_FUNCTION_BLOCK:
			{
				AST __t376 = _t;
				AST tmp192_AST_in = (AST)_t;
				match(_t,CALDOC_SEE_FUNCTION_BLOCK);
				_t = _t.getFirstChild();
				{
				int _cnt378=0;
				_loop378:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CALDOC_CHECKED_QUALIFIED_VAR||_t.getType()==CALDOC_UNCHECKED_QUALIFIED_VAR)) {
						docCommentMaybeUncheckedQualifiedVar(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt378>=1 ) { break _loop378; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt378++;
				} while (true);
				}
				_t = __t376;
				_t = _t.getNextSibling();
				break;
			}
			case CALDOC_SEE_MODULE_BLOCK:
			{
				AST __t379 = _t;
				AST tmp193_AST_in = (AST)_t;
				match(_t,CALDOC_SEE_MODULE_BLOCK);
				_t = _t.getFirstChild();
				{
				int _cnt381=0;
				_loop381:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CALDOC_CHECKED_MODULE_NAME||_t.getType()==CALDOC_UNCHECKED_MODULE_NAME)) {
						docCommentMaybeUncheckedModuleName(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt381>=1 ) { break _loop381; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt381++;
				} while (true);
				}
				_t = __t379;
				_t = _t.getNextSibling();
				break;
			}
			case CALDOC_SEE_DATACONS_BLOCK:
			{
				AST __t382 = _t;
				AST tmp194_AST_in = (AST)_t;
				match(_t,CALDOC_SEE_DATACONS_BLOCK);
				_t = _t.getFirstChild();
				{
				int _cnt384=0;
				_loop384:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CALDOC_CHECKED_QUALIFIED_CONS||_t.getType()==CALDOC_UNCHECKED_QUALIFIED_CONS)) {
						docCommentMaybeUncheckedQualifiedCons(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt384>=1 ) { break _loop384; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt384++;
				} while (true);
				}
				_t = __t382;
				_t = _t.getNextSibling();
				break;
			}
			case CALDOC_SEE_TYPECONS_BLOCK:
			{
				AST __t385 = _t;
				AST tmp195_AST_in = (AST)_t;
				match(_t,CALDOC_SEE_TYPECONS_BLOCK);
				_t = _t.getFirstChild();
				{
				int _cnt387=0;
				_loop387:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CALDOC_CHECKED_QUALIFIED_CONS||_t.getType()==CALDOC_UNCHECKED_QUALIFIED_CONS)) {
						docCommentMaybeUncheckedQualifiedCons(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt387>=1 ) { break _loop387; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt387++;
				} while (true);
				}
				_t = __t385;
				_t = _t.getNextSibling();
				break;
			}
			case CALDOC_SEE_TYPECLASS_BLOCK:
			{
				AST __t388 = _t;
				AST tmp196_AST_in = (AST)_t;
				match(_t,CALDOC_SEE_TYPECLASS_BLOCK);
				_t = _t.getFirstChild();
				{
				int _cnt390=0;
				_loop390:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==CALDOC_CHECKED_QUALIFIED_CONS||_t.getType()==CALDOC_UNCHECKED_QUALIFIED_CONS)) {
						docCommentMaybeUncheckedQualifiedCons(_t);
						_t = _retTree;
					}
					else {
						if ( _cnt390>=1 ) { break _loop390; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt390++;
				} while (true);
				}
				_t = __t388;
				_t = _t.getNextSibling();
				break;
			}
			case CALDOC_SEE_BLOCK_WITHOUT_CONTEXT:
			{
				AST __t391 = _t;
				AST tmp197_AST_in = (AST)_t;
				match(_t,CALDOC_SEE_BLOCK_WITHOUT_CONTEXT);
				_t = _t.getFirstChild();
				{
				int _cnt393=0;
				_loop393:
				do {
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case CALDOC_CHECKED_QUALIFIED_CONS:
					case CALDOC_UNCHECKED_QUALIFIED_CONS:
					{
						docCommentMaybeUncheckedQualifiedCons(_t);
						_t = _retTree;
						break;
					}
					case CALDOC_CHECKED_QUALIFIED_VAR:
					case CALDOC_UNCHECKED_QUALIFIED_VAR:
					{
						docCommentMaybeUncheckedQualifiedVar(_t);
						_t = _retTree;
						break;
					}
					default:
					{
						if ( _cnt393>=1 ) { break _loop393; } else {throw new NoViableAltException(_t);}
					}
					}
					_cnt393++;
				} while (true);
				}
				_t = __t391;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
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
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[10];
		data[0]=-9223372036854775808L;
		data[1]=1152921511049304065L;
		data[2]=24672L;
		data[3]=-6917535278965981184L;
		data[4]=3L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 576460752303423488L, 694891348762688L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 128L, 1653562410496L, 2L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	}
	
