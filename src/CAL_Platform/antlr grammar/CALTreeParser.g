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
 * CALTreeParser.g
 * Created: 20-June-00  
 * By: Bo Ilic
 */
 
/*
 * CALTreeParser.g
 *
 * Crystal Analytical Language (CAL) 
 *
 * Represents the grammar of the CAL language in tree form. This grammar is simpler than the textual CAL grammar and further
 * analysis of a CAL program for type checking and machine code generation are based on this form.
 *
 * Change History:
 * BI 20-June-00    Initial Module.  
 * BI 12-Dec-00     Added the ability to parse type declarations e.g. "head :: [a] -> a;" to the grammar.
 * BI 09-Jan-01     Added parsing of string and character literals.
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
 * BI 31-Aug-01     Eliminated atomic expressions in the tree parsing stage (a refactoring).
 * BI 29-April-02   First pass at a foreign function interface.
 * BI 24-June-02    Added unicode lexing and opaque foreign data types.
 * BI  7-Aug-02     Overrode reportError to improve tree parser error reporting. Fixed a bug with the () type.
 * BI 22-Aug-02     Added the ++ operator.
 * BI 26-Aug-02     Added syntax for declaring type classes and class instances.
 * BI 17-Feb-03     Added syntax for the wildcard pattern "_".
 * EL 18-Feb-03     Added a start (top-level) rule for type signatures.  These must consume an EOF.
 * BI 20-Feb-03     Wildcard patterns can now be used for pattern bound variables in case expression pattern matching. 
 * EL 11-Mar-03     Changed the top-level for the compiler from a "compilationUnit" (multiple modules) to a single module.
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
 * BI 09-Dec-04     Added UNARY_MINUS node instead of translating to Prelude.negate application.
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
 * JWO 23-Aug-05    Allowed CALDoc comments for inner function definitions.
 * EL  6-Sep-05     Added syntax for case unpacking of ints and chars.
 * EL  13-Sep-05    Unified handling of case unpacking for unparenthesized single data constructors and 
 *                  parenthesized data constructor groups.
 * BI  01-Nov-05    Added syntax for protected scope. 
 * BI  02-Nov-05    Added syntax for friend modules. 
 * JWO 1-Dec-05     Added CALDoc syntax for @see/{@link} without context, {@strong}, {@sup}, {@sub}.
 * BI  04-March-06  Added the remainder operator (%).
 * BI   1-May-06    Added syntax for the record field value update operator ":=". 
 * BI  27-July-06   Syntax for default class methods.
 * JWO 1-Nov-06     Added syntax for hierarchical module names.
 * JWO 19-Feb-07    Added syntax for lazy pattern matching by extending the let expression syntax.
 */

header {
// Package declaration
package org.openquark.cal.compiler;
}

/*******************************************
 * CAL tree grammar definition starts here *   
 *******************************************/

class CALTreeParser extends TreeParser;
options {
    importVocab=CAL;

    //Suppress warnings for the generated class.  Need to use unicode escapes since antlr copies backslashes into the source.
    //instead of "public", which is the default, make CALTreeParser package scope.
    classHeaderPrefix = "@SuppressWarnings(\u0022all\u0022) final";
}

// Preamble
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
    
}
// End Preamble

//a start rule for an expression.
codeGemExpr
    : expr
    ;

//a start rule for a type signature.
startTypeSignature
    : typeSignature
    ;

adjunct 
    : #(OUTER_DEFN_LIST (adjunctDefn)*)
    ;

// adjunct definitions
adjunctDefn
    : topLevelTypeDeclaration
    | instanceDefn
    | topLevelFunction
    ;
    
module
    : #(MODULE_DEFN optionalDocComment moduleName importDeclarationList friendDeclarationList outerDefnList)
    ;

moduleName
    : #(HIERARCHICAL_MODULE_NAME moduleNameQualifier cons)
    ;

moduleNameQualifier
    : HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER
    | #(HIERARCHICAL_MODULE_NAME moduleNameQualifier cons)
    ;

maybeModuleName
    : moduleNameQualifier
    ;

importDeclarationList
    : #(IMPORT_DECLARATION_LIST (importDeclaration)*)
    ;

importDeclaration
    : #("import" moduleName (usingClause)?)
    ;
    
usingClause
    : #("using" (usingItem)*)
    ;

usingItem
    : #("function" usingVarList)
    | #("typeConstructor" usingConsList)
    | #("dataConstructor" usingConsList)
    | #("typeClass" usingConsList)
    ;
        
usingConsList
    : (cons (COMMA! cons)*)
    ;
    
usingVarList
    : (var (COMMA! var) *)
    ;
    
    
friendDeclarationList
    : #(FRIEND_DECLARATION_LIST (friendDeclaration)*)
    ;
    
friendDeclaration
    : #("friend" moduleName)
    ;    
    

outerDefnList
    : #(OUTER_DEFN_LIST (outerDefn)*)
    ;

// Outermost definitions
outerDefn
    : topLevelTypeDeclaration
    | foreignDataDeclaration
    | dataDeclaration
    | typeClassDefn
    | instanceDefn
    | foreignFunctionDeclaration
    | primitiveFunctionDeclaration
    | topLevelFunction    
    ;

//////////////////////////////////////////////////////////////////////////////
// Type declarations
//////////////////////////////////////////////////////////////////////////////

topLevelTypeDeclaration
    : #(TOP_LEVEL_TYPE_DECLARATION optionalDocComment typeDeclaration)
    ;

letDefnTypeDeclaration
    : #(LET_DEFN_TYPE_DECLARATION optionalDocComment typeDeclaration)
    ;

typeDeclaration
    : #(TYPE_DECLARATION var typeSignature)
    ;
    
typeSignature
    : #(TYPE_SIGNATURE typeContextList type)
    ;    

typeContextList
    : #(TYPE_CONTEXT_LIST (typeContext)*)
    | #(TYPE_CONTEXT_SINGLETON typeContext)
    | TYPE_CONTEXT_NOTHING
    ;

classContextList
    : #(CLASS_CONTEXT_LIST (classContext)*)
    | #(CLASS_CONTEXT_SINGLETON classContext)
    | CLASS_CONTEXT_NOTHING
    ;   

typeContext
    : classContext
    | lacksContext
    ;
      
classContext
    : #(CLASS_CONTEXT qualifiedCons var)
    ;
    
lacksContext
    : #(LACKS_FIELD_CONTEXT var fieldName)
    ; 

type
    : #(FUNCTION_TYPE_CONSTRUCTOR type type)
    | #(TUPLE_TYPE_CONSTRUCTOR (type)*)  // the type of the tuple with one element is equivalent to the type of the element
    | #(LIST_TYPE_CONSTRUCTOR type)
    | #(TYPE_APPLICATION (type)+) 
    | qualifiedCons
    | var
    | #(RECORD_TYPE_CONSTRUCTOR recordVar fieldTypeAssignmentList)
    ;

recordVar
    : #(RECORD_VAR (var)?)
    ;

fieldTypeAssignmentList
    : #(FIELD_TYPE_ASSIGNMENT_LIST (fieldTypeAssignment)*)
    ;

fieldTypeAssignment
    : #(FIELD_TYPE_ASSIGNMENT fieldName type)
    ;

//////////////////////////////////////////////////////////////////////////////
// Foreign data declarations
//////////////////////////////////////////////////////////////////////////////

foreignDataDeclaration
    : #(FOREIGN_DATA_DECLARATION optionalDocComment accessModifier externalName accessModifier cons (derivingClause)?)
    ;

//////////////////////////////////////////////////////////////////////////////
// Data declarations
//////////////////////////////////////////////////////////////////////////////

dataDeclaration
    : #(DATA_DECLARATION optionalDocComment accessModifier typeConsName typeConsParamList dataConstructorDefnList (derivingClause)?)
    ;
    
typeConsName
    : cons
    ;    

typeConsParamList
    : (TYPE_CONS_PARAM_LIST (var)*)
    ;       

dataConstructorDefnList
    : #(DATA_CONSTRUCTOR_DEFN_LIST (dataConstructorDefn)+)
    ;

dataConstructorDefn
    : #(DATA_CONSTRUCTOR_DEFN optionalDocComment accessModifier cons dataConstructorArgList)
    ;
    
dataConstructorArgList
    : #(DATA_CONSTRUCTOR_ARG_LIST (dataConstructorArg)*)
    ;
    
dataConstructorArg
    : #(DATA_CONSTRUCTOR_NAMED_ARG fieldName (#(STRICT_ARG type) | type))
    ;
    
derivingClause
    : #("deriving" (qualifiedCons)+); //e.g. deriving Prelude.Eq, Ord, Debug.Show
    
//////////////////////////////////////////////////////////////////////////////
// type class definitions
//////////////////////////////////////////////////////////////////////////////

typeClassDefn
    :#(TYPE_CLASS_DEFN optionalDocComment accessModifier classContextList typeClassName var classMethodList)
    ;

typeClassName
    : cons
    ;

classMethodList
    : #(CLASS_METHOD_LIST (classMethod)*)
    ;
        
classMethod
    : #(CLASS_METHOD optionalDocComment accessModifier classMethodName typeSignature (defaultClassMethodName)?)
    ;
        
classMethodName
    : var
    ;
    
defaultClassMethodName
    : qualifiedVar
    ;
    
//////////////////////////////////////////////////////////////////////////////
// class instance definitions
//////////////////////////////////////////////////////////////////////////////

instanceDefn
    : #(INSTANCE_DEFN optionalDocComment instanceName instanceMethodList)
    ;
    
instanceName
    : #(INSTANCE_NAME classContextList qualifiedCons instanceTypeConsName)      
    ;    
    
instanceTypeConsName
    : #(GENERAL_TYPE_CONSTRUCTOR qualifiedCons (var)*)
    | #(UNPARENTHESIZED_TYPE_CONSTRUCTOR qualifiedCons)
    | #(FUNCTION_TYPE_CONSTRUCTOR var var)
    | UNIT_TYPE_CONSTRUCTOR
    | #(LIST_TYPE_CONSTRUCTOR var)
    | #(RECORD_TYPE_CONSTRUCTOR var)
    ;
    
instanceMethodList
    : #(INSTANCE_METHOD_LIST (instanceMethod)*)
    ;
        
instanceMethod
    : #(INSTANCE_METHOD optionalDocComment instanceMethodName instanceMethodDefn)    
    ;
    
instanceMethodName
    : var
    ;
    
instanceMethodDefn
    : qualifiedVar
    ;
    
//////////////////////////////////////////////////////////////////////////////
// foreign function declarations
//////////////////////////////////////////////////////////////////////////////

//e.g. foreign unsafe import jvm "java.lang.Math.sin" public sin :: Double -> Double;
foreignFunctionDeclaration
    : #(FOREIGN_FUNCTION_DECLARATION optionalDocComment externalName accessModifier typeDeclaration)
    ;

externalName
    : STRING_LITERAL
    ;
    
//////////////////////////////////////////////////////////////////////////////
// primitive function declarations
//////////////////////////////////////////////////////////////////////////////    

primitiveFunctionDeclaration
    : #(PRIMITIVE_FUNCTION_DECLARATION optionalDocComment accessModifier typeDeclaration)
    ;

//////////////////////////////////////////////////////////////////////////////
// Functions
//////////////////////////////////////////////////////////////////////////////

topLevelFunction 
    : #(TOP_LEVEL_FUNCTION_DEFN optionalDocComment accessModifier functionName functionParamList expr)
    ;

accessModifier
    : #(ACCESS_MODIFIER ("public" | "private" | "protected")?)
    ;
    
functionName
    : var
    ;    
    
functionParamList
    : #(FUNCTION_PARAM_LIST (functionParam)*)
    ;
    
lambdaParamList
    : #(FUNCTION_PARAM_LIST (functionParam)+)
    ;       

functionParam
    : (LAZY_PARAM | STRICT_PARAM)
    ;   

// Expressions

expr         
    : #("let" letDefnList expr)
    | #("if" expr expr expr)
    | #("case" expr altList)
    | #(LAMBDA_DEFN lambdaParamList expr) 
    | #(BARBAR expr expr)
    | #(AMPERSANDAMPERSAND expr expr)
    | #(LESS_THAN expr expr)
    | #(LESS_THAN_OR_EQUALS expr expr)
    | #(EQUALSEQUALS expr expr)
    | #(NOT_EQUALS expr expr)
    | #(GREATER_THAN_OR_EQUALS expr expr)
    | #(GREATER_THAN expr expr)
    | #(PLUS expr expr)
    | #(MINUS expr expr)
    | #(ASTERISK expr expr)
    | #(SOLIDUS expr expr)
    | #(PERCENT expr expr)
    | #(COLON expr expr)
    | #(PLUSPLUS expr expr)
    | #(UNARY_MINUS expr)
    | #(BACKQUOTE expr ((qualifiedVar|qualifiedCons) expr)*)
    | #(POUND expr expr)
    | #(DOLLAR expr expr)
    | #(APPLICATION (expr)+)
    | qualifiedVar
    | qualifiedCons
    | literal
    | #(TUPLE_CONSTRUCTOR (expr)*)
    | #(LIST_CONSTRUCTOR (expr)*)
    | #(RECORD_CONSTRUCTOR baseRecord fieldModificationList)
    | #(SELECT_RECORD_FIELD expr fieldName)
    | #(SELECT_DATA_CONSTRUCTOR_FIELD expr qualifiedCons fieldName)
    | #(EXPRESSION_TYPE_SIGNATURE expr typeSignature)
    ;
    
baseRecord
    : #(BASE_RECORD (expr)?)
    ;
    
fieldModificationList
    : #(FIELD_MODIFICATION_LIST (fieldExtension | fieldValueUpdate)*)
    ;

fieldExtension
    : #(FIELD_EXTENSION fieldName expr)
    ;

fieldValueUpdate
    : #(FIELD_VALUE_UPDATE fieldName expr)
    ;

fieldName 
    : var
    | ORDINAL_FIELD_NAME
    ;

qualifiedVar
    : #(QUALIFIED_VAR maybeModuleName var)
    ;

qualifiedCons
    : #(QUALIFIED_CONS maybeModuleName cons)
    ;

// Definitions
letDefnList
    : #(LET_DEFN_LIST (letDefn | letDefnTypeDeclaration | letPatternMatchDecl)+)
    ;

letDefn
    : #(LET_DEFN optionalDocComment functionName functionParamList expr)
    ;

letPatternMatchDecl
    : #(LET_PATTERN_MATCH_DECL letPatternMatchPattern expr)
    ;

letPatternMatchPattern
    : #(PATTERN_CONSTRUCTOR #(DATA_CONSTRUCTOR_NAME_SINGLETON qualifiedCons) dataConstructorArgumentBindings)
    | #(TUPLE_CONSTRUCTOR (patternVar)*)
    | #(COLON patternVar patternVar)
    | #(RECORD_PATTERN baseRecordPattern fieldBindingVarAssignmentList)
    ;

// Alternatives
altList
    : #(ALT_LIST (alt)+)
    ;

alt 
    : #(ALT pat expr)
    ;

pat : #(PATTERN_CONSTRUCTOR dataConstructorNameListOneOrMore dataConstructorArgumentBindings)
    | #(TUPLE_CONSTRUCTOR (patternVar)*)
    | LIST_CONSTRUCTOR
    | #(INT_PATTERN maybeMinusIntListOneOrMore)
    | #(CHAR_PATTERN charListOneOrMore)
    | #(COLON patternVar patternVar)
    | wildcard
    | #(RECORD_PATTERN baseRecordPattern fieldBindingVarAssignmentList)
    ;

dataConstructorArgumentBindings
    : patternVarListZeroOrMore
    | fieldBindingVarAssignmentList
    ;

patternVarListZeroOrMore
    : #(PATTERN_VAR_LIST (patternVar)*)
    ;
    
dataConstructorNameListOneOrMore
    : #(DATA_CONSTRUCTOR_NAME_LIST (qualifiedCons)+)
    | #(DATA_CONSTRUCTOR_NAME_SINGLETON qualifiedCons)
    ;

maybeMinusIntListOneOrMore
    : #(MAYBE_MINUS_INT_LIST (maybeMinusInt)+)
    ;

maybeMinusInt
    : #(MINUS INTEGER_LITERAL)
    | INTEGER_LITERAL
    ;
    
charListOneOrMore
    : #(CHAR_LIST (CHAR_LITERAL)+)
    ;

wildcard
    : UNDERSCORE
    ;
    
patternVar
    : var | wildcard
    ;    
    
baseRecordPattern
    : #(BASE_RECORD_PATTERN (patternVar)?)
    ;

fieldBindingVarAssignmentList
    : #(FIELD_BINDING_VAR_ASSIGNMENT_LIST (fieldBindingVarAssignment)*)
    ;
    
fieldBindingVarAssignment
    : #(FIELD_BINDING_VAR_ASSIGNMENT fieldName (patternVar)?)
    ;

// Literals
literal                                                                                                     
    : INTEGER_LITERAL
    | FLOAT_LITERAL
    | CHAR_LITERAL
    | STRING_LITERAL
    ;

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
    : #(OPTIONAL_CALDOC_COMMENT (docComment)?)
    ;

docComment
    : #(CALDOC_COMMENT docCommentDescriptionBlock docCommentTaggedBlocks)
    ;

docCommentDescriptionBlock
    : #(CALDOC_DESCRIPTION_BLOCK docCommentTextualContent)
    ;

//docCommentTextualContent
//    : #(CALDOC_TEXT (CALDOC_TEXT_LINE | CALDOC_NEWLINE | CALDOC_NEWLINE_WITH_LEADING_ASTERISK)*)
//    ;

docCommentTextualContent
    : #(CALDOC_TEXT (CALDOC_TEXT_LINE | CALDOC_BLANK_TEXT_LINE | docCommentInlineBlock | CALDOC_TEXT_LINE_BREAK)*)
    ;

docCommentPreformattedBlock
    : #(CALDOC_TEXT_PREFORMATTED_BLOCK (CALDOC_TEXT_LINE | CALDOC_BLANK_TEXT_LINE | docCommentInlineBlock | CALDOC_TEXT_LINE_BREAK)*)
    ;

docCommentTextBlockWithoutInlineTags
    : #(CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS (CALDOC_TEXT_LINE | CALDOC_BLANK_TEXT_LINE | CALDOC_TEXT_LINE_BREAK)*)
    ;

docCommentInlineBlock
    : #(CALDOC_TEXT_INLINE_BLOCK
         ( docCommentInlineUrlBlock
         | docCommentInlineLinkBlock
         | docCommentInlineEmBlock
         | docCommentInlineStrongBlock
         | docCommentInlineSupBlock
         | docCommentInlineSubBlock
         | docCommentInlineSummaryBlock
         | docCommentInlineCodeBlock
         | docCommentInlineOrderedListBlock
         | docCommentInlineUnorderedListBlock
         )
      )
    ;

docCommentInlineUrlBlock
    : #(CALDOC_TEXT_URL docCommentTextBlockWithoutInlineTags)
    ;

docCommentInlineLinkBlock
    : #(CALDOC_TEXT_LINK
         ( docCommentLinkFunction
         | docCommentLinkModule
         | docCommentLinkDataCons
         | docCommentLinkTypeCons
         | docCommentLinkTypeClass
         | docCommentLinkWithoutContext
         )
      )
    ;
    
docCommentInlineEmBlock
    : #(CALDOC_TEXT_EMPHASIZED_TEXT docCommentTextualContent)
    ;
    
docCommentInlineStrongBlock
    : #(CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT docCommentTextualContent)
    ;
    
docCommentInlineSupBlock
    : #(CALDOC_TEXT_SUPERSCRIPT_TEXT docCommentTextualContent)
    ;
    
docCommentInlineSubBlock
    : #(CALDOC_TEXT_SUBSCRIPT_TEXT docCommentTextualContent)
    ;
    
docCommentInlineSummaryBlock
    : #(CALDOC_TEXT_SUMMARY docCommentTextualContent)
    ;

docCommentInlineCodeBlock
    : #(CALDOC_TEXT_CODE_BLOCK docCommentPreformattedBlock)
    ;

docCommentInlineOrderedListBlock
    : #(CALDOC_TEXT_ORDERED_LIST (docCommentListItem)*)
    ;

docCommentInlineUnorderedListBlock
    : #(CALDOC_TEXT_UNORDERED_LIST (docCommentListItem)*)
    ;

docCommentListItem
    : #(CALDOC_TEXT_LIST_ITEM docCommentTextualContent)
    ;

docCommentLinkFunction
    : #(CALDOC_TEXT_LINK_FUNCTION docCommentMaybeUncheckedQualifiedVar)
    ;

docCommentLinkModule
    : #(CALDOC_TEXT_LINK_MODULE docCommentMaybeUncheckedModuleName)
    ;

docCommentLinkDataCons
    : #(CALDOC_TEXT_LINK_DATACONS docCommentMaybeUncheckedQualifiedCons)
    ;

docCommentLinkTypeCons
    : #(CALDOC_TEXT_LINK_TYPECONS docCommentMaybeUncheckedQualifiedCons)
    ;

docCommentLinkTypeClass
    : #(CALDOC_TEXT_LINK_TYPECLASS docCommentMaybeUncheckedQualifiedCons)
    ;

docCommentLinkWithoutContext
    : #(CALDOC_TEXT_LINK_WITHOUT_CONTEXT (docCommentMaybeUncheckedQualifiedCons | docCommentMaybeUncheckedQualifiedVar))
    ;

docCommentTaggedBlocks
    :
        #(CALDOC_TAGGED_BLOCKS
            ( docCommentAuthorBlock
            | docCommentDeprecatedBlock
            | docCommentReturnBlock
            | docCommentVersionBlock
            | docCommentArgBlock
            | docCommentSeeBlock
            )*
        )
    ;
    
docCommentAuthorBlock
    : #(CALDOC_AUTHOR_BLOCK docCommentTextualContent)
    ;
    
docCommentDeprecatedBlock
    : #(CALDOC_DEPRECATED_BLOCK docCommentTextualContent)
    ;
    
docCommentReturnBlock
    : #(CALDOC_RETURN_BLOCK docCommentTextualContent)
    ;

docCommentVersionBlock
    : #(CALDOC_VERSION_BLOCK docCommentTextualContent)
    ;

docCommentArgBlock
    : #(CALDOC_ARG_BLOCK fieldName docCommentTextualContent)
    ;

docCommentSeeBlock
    : #(CALDOC_SEE_BLOCK docCommentSeeBlockContent)
    ;

docCommentSeeBlockContent
    : #(CALDOC_SEE_FUNCTION_BLOCK (docCommentMaybeUncheckedQualifiedVar)+)
    | #(CALDOC_SEE_MODULE_BLOCK (docCommentMaybeUncheckedModuleName)+)
    | #(CALDOC_SEE_DATACONS_BLOCK (docCommentMaybeUncheckedQualifiedCons)+)
    | #(CALDOC_SEE_TYPECONS_BLOCK (docCommentMaybeUncheckedQualifiedCons)+)
    | #(CALDOC_SEE_TYPECLASS_BLOCK (docCommentMaybeUncheckedQualifiedCons)+)
    | #(CALDOC_SEE_BLOCK_WITHOUT_CONTEXT (docCommentMaybeUncheckedQualifiedCons | docCommentMaybeUncheckedQualifiedVar)+)
    ;

docCommentMaybeUncheckedModuleName
    : #(CALDOC_CHECKED_MODULE_NAME moduleName)
    | #(CALDOC_UNCHECKED_MODULE_NAME moduleName)
    ;

docCommentMaybeUncheckedQualifiedVar
    : #(CALDOC_CHECKED_QUALIFIED_VAR qualifiedVar)
    | #(CALDOC_UNCHECKED_QUALIFIED_VAR qualifiedVar)
    ;

docCommentMaybeUncheckedQualifiedCons
    : #(CALDOC_CHECKED_QUALIFIED_CONS qualifiedCons)
    | #(CALDOC_UNCHECKED_QUALIFIED_CONS qualifiedCons)
    ;
