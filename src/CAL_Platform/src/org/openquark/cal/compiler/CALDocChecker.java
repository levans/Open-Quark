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
 * CALDocChecker.java
 * Creation date: Aug 17, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.util.Pair;


/**
 * Checks the CALDoc comments in CAL source code. The process of checking the
 * comments produce as an end result instances of CALDocComment representing
 * these comments. These CALDocComment objects are then associated with the
 * appropriate entities stored in the ModuleTypeInfo, making them available
 * to clients for later retrieval.
 *
 * @author Joseph Wong
 */
class CALDocChecker {

    /** The CALCompiler associated with this checker. */
    private final CALCompiler compiler;

    /** The module type info of the current module. */
    private final ModuleTypeInfo currentModuleTypeInfo;
    
    /** The FreeVariableChecker associated with this checker. */
    private final FreeVariableFinder finder;
    
    /** The CALTypeChecker associated with this checker. */
    private final CALTypeChecker typeChecker;
    
    /**
     * An internal exception class used to signal that the processing of a CALDoc comment should
     * end after having encountered an error in the comment.
     *
     * @author Joseph Wong
     */
    private static final class InvalidCALDocException extends Exception {
       
        private static final long serialVersionUID = -5609261520985652737L;

        /** Private constructor for this internal exception class. */
        private InvalidCALDocException() {}
    }
    
    /**
     * A type-safe enumeration for constants representing the different 'categories' into which
     * a CALDoc cross-reference appearing without 'context' keywords may be resolved.
     * 
     * Such a reference may appear in a CALDoc comment, as in:
     * {at-link Prelude at-} - a module reference without context
     * {at-link Nothing at-} - a data constructor reference without context
     * at-see Prelude.Int - a type constructor reference without context
     * at-see Eq - a type class reference without context
     *
     * @author Joseph Wong
     */
    static final class CategoryForCALDocConsNameWithoutContextCrossReference {
        /**
         * Represents the fact that a CALDoc cross-reference appearing without a 'context' keyword unambiguously resolves to a module name.
         */
        static final CategoryForCALDocConsNameWithoutContextCrossReference MODULE_NAME = new CategoryForCALDocConsNameWithoutContextCrossReference("module name");
        /**
         * Represents the fact that a CALDoc cross-reference appearing without a 'context' keyword unambiguously resolves to a type constructor name.
         */
        static final CategoryForCALDocConsNameWithoutContextCrossReference TYPE_CONS_NAME = new CategoryForCALDocConsNameWithoutContextCrossReference("type constructor name");
        /**
         * Represents the fact that a CALDoc cross-reference appearing without a 'context' keyword unambiguously resolves to a data constructor name.
         */
        static final CategoryForCALDocConsNameWithoutContextCrossReference DATA_CONS_NAME = new CategoryForCALDocConsNameWithoutContextCrossReference("data constructor name");
        /**
         * Represents the fact that a CALDoc cross-reference appearing without a 'context' keyword unambiguously resolves to a type class name.
         */
        static final CategoryForCALDocConsNameWithoutContextCrossReference TYPE_CLASS_NAME = new CategoryForCALDocConsNameWithoutContextCrossReference("type class name");
        
        /** The display name for the enumeration constant. */
        private final String displayName;
        
        /** Private constructor for this type-safe enumeration. */
        private CategoryForCALDocConsNameWithoutContextCrossReference(String displayName) {
            this.displayName = displayName;
        }
        
        /** @return the display name for the enumeration constant. */
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    /**
     * Constructs a CALDocChecker.
     * 
     * @param compiler
     * @param currentModuleTypeInfo
     * @param finder
     * @param typeChecker
     */
    CALDocChecker(
        CALCompiler compiler,
        ModuleTypeInfo currentModuleTypeInfo,
        FreeVariableFinder finder,
        CALTypeChecker typeChecker) {
        
        this.compiler = compiler;
        this.currentModuleTypeInfo = currentModuleTypeInfo;
        this.finder = finder;
        this.typeChecker = typeChecker;
    }

    /**
     * Checks the CALDoc comments within the specified module definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param moduleDefnNode the module definition node whose subtree is to be checked.
     */
    void checkCALDocCommentsInModuleDefn(ParseTreeNode moduleDefnNode) {
        
        moduleDefnNode.verifyType(CALTreeParserTokenTypes.MODULE_DEFN);
        
        // check the module's CALDoc comment.
        ParseTreeNode optionalCALDocNode = moduleDefnNode.firstChild();
        CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, null, null, false, false, false);
        
        // associate the CALDoc with the entity
        currentModuleTypeInfo.setCALDocComment(caldoc);
        
        // check the children.
        ParseTreeNode moduleNameNode = optionalCALDocNode.nextSibling();
        moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
              
        ParseTreeNode importDeclarationListNode = moduleNameNode.nextSibling();
        importDeclarationListNode.verifyType(CALTreeParserTokenTypes.IMPORT_DECLARATION_LIST);
        
        ParseTreeNode friendDeclarationListNode = importDeclarationListNode.nextSibling();
        friendDeclarationListNode.verifyType(CALTreeParserTokenTypes.FRIEND_DECLARATION_LIST);
        
        ParseTreeNode outerDefnListNode = friendDeclarationListNode.nextSibling();
        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);
        checkCALDocCommentsInOuterDefnList(outerDefnListNode);
    }
    
    /**
     * Checks the CALDoc comments within the specified outer definition list. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param outerDefnListNode the outer definition list node whose subtree is to be checked.
     */
    void checkCALDocCommentsInOuterDefnList(ParseTreeNode outerDefnListNode) {
        
        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);
        
        for (final ParseTreeNode outerDefnNode : outerDefnListNode) {
            
            switch (outerDefnNode.getType()) {
              
                case CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION:
                {
                    checkCALDocCommentsInFunctionTypeDeclaration(outerDefnNode);
                    break;
                }
    
                case CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN:
                {
                    checkCALDocCommentsInAlgebraicFunctionDefn(outerDefnNode);
                    break;
                }
    
                case CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION:
                {
                    checkCALDocCommentsInForeignFunctionDefn(outerDefnNode);
                    break;
                }
                
                case CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION:
                {
                    checkCALDocCommentsInPrimitiveFunctionDefn(outerDefnNode);
                    break;
                }
    
                case CALTreeParserTokenTypes.DATA_DECLARATION:
                {                   
                    checkCALDocCommentsInAlgebraicTypeDefn(outerDefnNode);
                    break;
                }
    
                case CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION:
                {                     
                    checkCALDocCommentsInForeignTypeDefn(outerDefnNode);
                    break;
                }
    
                case CALTreeParserTokenTypes.TYPE_CLASS_DEFN:
                {                    
                    checkCALDocCommentsInTypeClassDefn(outerDefnNode);
                    break;
                }
    
                case CALTreeParserTokenTypes.INSTANCE_DEFN:
                {                   
                    checkCALDocCommentsInInstanceDefn(outerDefnNode);
                    break;
                }
    
                default:
                {
                    outerDefnNode.unexpectedParseTreeNode();
                    break;
                }
            }
        }
    }

    /**
     * Checks the CALDoc comments within the specified function type declaration. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param topLevelTypeDeclarationNode the function type declaration node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInFunctionTypeDeclaration(ParseTreeNode topLevelTypeDeclarationNode) {
        
        topLevelTypeDeclarationNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = topLevelTypeDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        ParseTreeNode typeDeclarationNode = optionalCALDocNode.nextSibling();
        typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
        
        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String functionName = functionNameNode.getText(); 
        
        ParseTreeNode topLevelFunctionDefnNode = typeChecker.getFunctionDefinitionNode(functionName);
        
        if (topLevelFunctionDefnNode == null) {
            // the function is declared but not defined. This is illegal in CAL.
            compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.DefinitionMissing(functionName)));
        } else {
            // fetch the parameter list from the function definition.
            topLevelFunctionDefnNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);                
    
            ParseTreeNode paramListNode = topLevelFunctionDefnNode.getChild(3);
            paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST); 
                
            // fetch the entity and then the type expression from the entity associated with the function.
            Function entity = currentModuleTypeInfo.getFunction(functionName);
            if (entity == null) {
                String displayName = getQualifiedNameDisplayString(functionNameNode);
                // TypeChecker: unknown function or variable {displayName}.
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.UnknownFunctionOrVariable(displayName)));
                
            } else {
                TypeExpr typeExpr = entity.getTypeExpr();
                
                // check the function's CALDoc with the function's type expression and the parameter list from the function definition.
                CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, paramListNode, typeExpr, true, true, false);
                
                // associate the CALDoc with the entity
                entity.setCALDocComment(caldoc);
            }
        }
    }

    /**
     * Checks the CALDoc comments within the specified algebraic function definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param topLevelFunctionDefnNode the algebraic function definition node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInAlgebraicFunctionDefn(ParseTreeNode topLevelFunctionDefnNode) {
        
        topLevelFunctionDefnNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);                
    
        ParseTreeNode optionalCALDocNode = topLevelFunctionDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);                 
    
        ParseTreeNode functionNameNode = accessModifierNode.nextSibling();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String functionName = functionNameNode.getText(); 
        
        ParseTreeNode paramListNode = functionNameNode.nextSibling();
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST); 
        
        ParseTreeNode definingExprNode = paramListNode.nextSibling();
        checkCALDocCommentsInExpr(definingExprNode);
    
        // if the function has an associated type declaration, then the CALDoc comment must appear
        // immediately before the type declaration, and not immediately before the function definition.
        if (typeChecker.hasFunctionTypeDeclaration(functionName)) {
            
            if (optionalCALDocNode.firstChild() != null) {
                compiler.logMessage(
                    new CompilerMessage(
                        optionalCALDocNode.firstChild(),
                        new MessageKind.Error.CALDocCommentForAlgebraicFunctionMustAppearBeforeTypeDeclaration(functionName)));
            }
            
        } else {
            // There is no associated type declaration, so it is okay to have a CALDoc comment before this function definition.
            
            // fetch the entity and then the type expression from the entity associated with the function.
            FunctionalAgent entity = currentModuleTypeInfo.getFunction(functionName);
            if (entity == null) {
                String displayName = getQualifiedNameDisplayString(functionNameNode);
                // TypeChecker: unknown function or variable {displayName}.
                compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.UnknownFunctionOrVariable(displayName)));
                
            } else {
                TypeExpr typeExpr = entity.getTypeExpr();
                
                // check the function's CALDoc with the function's type expression.
                CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, paramListNode, typeExpr, true, true, false);
                
                // associate the CALDoc with the entity
                entity.setCALDocComment(caldoc);
            }
        }
    }
    
    /**
     * Checks the CALDoc comments within the specified expression. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param parseTree the expression node whose subtree is to be checked.
     */
    void checkCALDocCommentsInExpr(ParseTreeNode parseTree) {
        int nodeType = parseTree.getType();

        switch (nodeType) {
            case CALTreeParserTokenTypes.VIRTUAL_LET_NONREC:
            case CALTreeParserTokenTypes.VIRTUAL_LET_REC:
            {
                checkCALDocCommentsInLet(parseTree);
                break;
            }

            case CALTreeParserTokenTypes.LAMBDA_DEFN:
            {
                ParseTreeNode paramListNode = parseTree.firstChild();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
                
                ParseTreeNode definingExprNode = paramListNode.nextSibling();
                checkCALDocCommentsInExpr(definingExprNode);
                break;
            }

            case CALTreeParserTokenTypes.LITERAL_if:
            {
                ParseTreeNode conditionNode = parseTree.firstChild();
                checkCALDocCommentsInExpr(conditionNode);

                ParseTreeNode ifTrueNode = conditionNode.nextSibling();
                checkCALDocCommentsInExpr(ifTrueNode);

                ParseTreeNode ifFalseNode = ifTrueNode.nextSibling();
                checkCALDocCommentsInExpr(ifFalseNode);
                break;
            }

            case CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE:
            {
                checkCALDocCommentsInDataConstructorCase(parseTree);
                break;
            }
            
            case CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE: 
            {
                checkCALDocCommentsInTupleCase(parseTree);
                break;
            }
            
            case CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE:
            {                
                checkCALDocCommentsInRecordCase(parseTree);
                break;
            }

            case CALTreeParserTokenTypes.BARBAR:              
            case CALTreeParserTokenTypes.AMPERSANDAMPERSAND: 
            case CALTreeParserTokenTypes.PLUSPLUS:               
            case CALTreeParserTokenTypes.EQUALSEQUALS:                              
            case CALTreeParserTokenTypes.NOT_EQUALS:              
            case CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS:             
            case CALTreeParserTokenTypes.GREATER_THAN:               
            case CALTreeParserTokenTypes.LESS_THAN:                
            case CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS:              
            case CALTreeParserTokenTypes.PLUS:               
            case CALTreeParserTokenTypes.MINUS:              
            case CALTreeParserTokenTypes.ASTERISK:              
            case CALTreeParserTokenTypes.SOLIDUS: 
            case CALTreeParserTokenTypes.PERCENT:
            case CALTreeParserTokenTypes.COLON:
            case CALTreeParserTokenTypes.POUND:
            {
                ParseTreeNode arg1Node = parseTree.firstChild();
                checkCALDocCommentsInExpr(arg1Node);
                
                ParseTreeNode arg2Node = arg1Node.nextSibling();        
                checkCALDocCommentsInExpr(arg2Node);
                break;
            }
                
            case CALTreeParserTokenTypes.UNARY_MINUS:
            {
                ParseTreeNode arg1Node = parseTree.firstChild();
                checkCALDocCommentsInExpr(arg1Node);
                break;
            }
            
            case CALTreeParserTokenTypes.DOLLAR:
            {
                // Treat the node representing the $ operator as an application node 
                checkCALDocCommentsInApplication(parseTree);
                break;
            }

            case CALTreeParserTokenTypes.BACKQUOTE:
            {
                // Skip the backquoted node 
                checkCALDocCommentsInApplication(parseTree.firstChild());
                break;
            }
            
            case CALTreeParserTokenTypes.APPLICATION:
            {
                checkCALDocCommentsInApplication(parseTree);
                break;
            }

            //variables and functions
            case CALTreeParserTokenTypes.QUALIFIED_VAR:
            //data constructors 
            case CALTreeParserTokenTypes.QUALIFIED_CONS:
            //literals
            case CALTreeParserTokenTypes.CHAR_LITERAL :
            case CALTreeParserTokenTypes.INTEGER_LITERAL :
            case CALTreeParserTokenTypes.FLOAT_LITERAL :
            case CALTreeParserTokenTypes.STRING_LITERAL : 
            {
                // nothing to do
                break;
            }

            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
            {
                for (final ParseTreeNode elementNode : parseTree) {

                    checkCALDocCommentsInExpr(elementNode);
                }
                break;
            }
            
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
            {
                for (final ParseTreeNode componentNode : parseTree) {
                                                     
                    checkCALDocCommentsInExpr(componentNode);
                }
                break;
            }
           
            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR :
            {
                ParseTreeNode baseRecordNode = parseTree.firstChild();
                baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);           
                
                ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();
                if (baseRecordExprNode != null) {
                    checkCALDocCommentsInExpr(baseRecordExprNode);
                }
                
                ParseTreeNode fieldModificationListNode = baseRecordNode.nextSibling();
                fieldModificationListNode.verifyType(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST);
                
                for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
                    
                    fieldModificationNode.verifyType(CALTreeParserTokenTypes.FIELD_EXTENSION,
                        CALTreeParserTokenTypes.FIELD_VALUE_UPDATE);
                    
                    ParseTreeNode fieldNameNode = fieldModificationNode.firstChild();
                    
                    ParseTreeNode valueExprNode = fieldNameNode.nextSibling();
                    
                    checkCALDocCommentsInExpr(valueExprNode);
                }
                break;
            }
            
            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD :
            {
                checkCALDocCommentsInDataConstructorFieldSelection(parseTree);
                break;
            }
                
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD:
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                checkCALDocCommentsInExpr(exprNode);                
                break;
            }
            
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                checkCALDocCommentsInExpr(exprNode);
                break;
            }

            default :
            {            
                parseTree.unexpectedParseTreeNode();               
                break;
            }
        }
    }
    
    /**
     * Checks the CALDoc comments within the specified application expression. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param parseTree the application expression node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInApplication(ParseTreeNode parseTree) {
               
        for (final ParseTreeNode exprNode : parseTree) {
            checkCALDocCommentsInExpr(exprNode);
        }
    }
    
    /**
     * Checks the CALDoc comments within the specified record case. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param recordCaseNode the record case node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInRecordCase(ParseTreeNode recordCaseNode) {
        
        recordCaseNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE);

        ParseTreeNode conditionNode = recordCaseNode.firstChild();
        checkCALDocCommentsInExpr(conditionNode);

        ParseTreeNode altListNode = conditionNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);
              
        if (!altListNode.hasExactlyOneChild()) {
            //record-case patterns have only 1 alternative. This should be caught earlier in static analysis.
            throw new IllegalArgumentException();
        }
        
        ParseTreeNode altNode = altListNode.firstChild();       
        altNode.verifyType(CALTreeParserTokenTypes.ALT);

        ParseTreeNode patternNode = altNode.firstChild();
        patternNode.verifyType(CALTreeParserTokenTypes.RECORD_PATTERN);

        ParseTreeNode exprNode = patternNode.nextSibling();
        checkCALDocCommentsInExpr(exprNode);
    }
    
    /**
     * Checks the CALDoc comments within the specified data constructor case. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param caseNode the data constructor case node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInDataConstructorCase(ParseTreeNode caseNode) {
        
        caseNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE);

        ParseTreeNode conditionNode = caseNode.firstChild();
        checkCALDocCommentsInExpr(conditionNode);

        ParseTreeNode altListNode = conditionNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);

        for (final ParseTreeNode altNode : altListNode) {

            altNode.verifyType(CALTreeParserTokenTypes.ALT);

            ParseTreeNode patternNode = altNode.firstChild();
            ParseTreeNode exprNode = patternNode.nextSibling();
            checkCALDocCommentsInExpr(exprNode);
        }
    }
    
    /**
     * Checks the CALDoc comments within the specified tupe case. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param caseNode the tuple case node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInTupleCase(ParseTreeNode caseNode) {
        
        caseNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE);

        ParseTreeNode conditionNode = caseNode.firstChild();
        checkCALDocCommentsInExpr(conditionNode);

        ParseTreeNode altListNode = conditionNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);
        
        if (!altListNode.hasExactlyOneChild()) {
            //tuple-case patterns have only 1 alternative. This should be caught earlier in static analysis.
            throw new IllegalArgumentException();
        }      

        ParseTreeNode altNode = altListNode.firstChild();            
        altNode.verifyType(CALTreeParserTokenTypes.ALT);

        ParseTreeNode patternNode = altNode.firstChild();
        patternNode.verifyType(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR);
        
        ParseTreeNode exprNode = patternNode.nextSibling();
        checkCALDocCommentsInExpr(exprNode);
    }
    
    /**
     * Checks the CALDoc comments within the specified data constructor field selection. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param selectNode the field selection node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInDataConstructorFieldSelection(ParseTreeNode selectNode) {
        
        selectNode.verifyType(CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD);
        
        ParseTreeNode conditionNode = selectNode.firstChild();
        checkCALDocCommentsInExpr(conditionNode);
        
        ParseTreeNode dcNameNode = conditionNode.nextSibling();
        dcNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode fieldNameNode = dcNameNode.nextSibling();
        fieldNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
        
        ParseTreeNode qualifiedVarNode = fieldNameNode.nextSibling();
        qualifiedVarNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);

        checkCALDocCommentsInExpr(qualifiedVarNode);
    }
    
    /**
     * Checks the CALDoc comments within the specified let expression. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param letNode the let expression node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInLet(ParseTreeNode letNode) {
        ParseTreeNode defnListNode = letNode.firstChild();
        defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);
        
        Map<String, ParseTreeNode> funcNamesToDefnNodes = new HashMap<String, ParseTreeNode>();
        Set<String> functionsWithTypeDecls = new HashSet<String>();

        // First pass: gather the function definition nodes into a map,
        // and the names of the functions with type declarations into a set.
        for (final ParseTreeNode defnNode : defnListNode) {

            defnNode.verifyType(CALTreeParserTokenTypes.LET_DEFN, CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION);
            
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                String functionName = localFunctionNameNode.getText();
    
                ParseTreeNode paramListNode = localFunctionNameNode.nextSibling();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
                
                ParseTreeNode definingExprNode = paramListNode.nextSibling();
                checkCALDocCommentsInExpr(definingExprNode);

                funcNamesToDefnNodes.put(functionName, defnNode);
                
            } else if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION) {
                
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                
                String functionName = typeDeclNode.firstChild().getText();
                
                functionsWithTypeDecls.add(functionName);
            }
        }

        // Second pass: perform the actual checking of the CALDoc comments
        for (final ParseTreeNode defnNode : defnListNode) {

            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                String functionName = localFunctionNameNode.getText();
    
                ParseTreeNode paramListNode = localFunctionNameNode.nextSibling();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

                // if we have previously encountered a type declaration for this function
                // in the first pass, then we require that the CALDoc comment be associated
                // with the type declaration and not with the function definition.
                if (functionsWithTypeDecls.contains(functionName)) {
                    if (optionalCALDocNode.firstChild() != null) {
                        compiler.logMessage(
                            new CompilerMessage(
                                optionalCALDocNode.firstChild(),
                                new MessageKind.Error.CALDocCommentForAlgebraicFunctionMustAppearBeforeTypeDeclaration(functionName)));
                    }
                } else {
                    // the information on the function's type is stored as
                    // data on the optionalCALDocNode associated with the function definition.
                    
                    // we use the information in the type to determine how many @arg tags
                    // is allowed to be in the CALDoc comment.
                    int nTopLevelArrowsInType = optionalCALDocNode.getFunctionTypeForLocalFunctionCALDocComment().getArity();
                    CALDocComment calDocComment = checkAndBuildCALDocComment(optionalCALDocNode, paramListNode, nTopLevelArrowsInType, true, true, false);
                    
                    LocalFunctionIdentifier identifier = typeChecker.getLocalFunctionIdentifier(functionName);
                    if(identifier != null) {
                        Function toplevelFunction = currentModuleTypeInfo.getFunction(identifier.getToplevelFunctionName().getUnqualifiedName());
                        Function localFunction = toplevelFunction.getLocalFunction(identifier);
                        if(localFunction != null) {
                            localFunction.setCALDocComment(calDocComment);
                        }
                    }
                }
                
            } else if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION) {
                
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                
                String functionName = typeDeclNode.firstChild().getText();
                
                // we need to fetch the function definition node corresponding to this
                // type declaration so that we can check the @arg tags against
                // the declared parameters of the function.
                ParseTreeNode funcDefnNode = funcNamesToDefnNodes.get(functionName);
                
                if (funcDefnNode == null) {
                    compiler.logMessage(new CompilerMessage(defnNode, new MessageKind.Error.DefinitionMissing(functionName)));
                    
                } else {
                    ParseTreeNode funcDefnOptionalCALDocNode = funcDefnNode.firstChild();
                    funcDefnOptionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                    
                    ParseTreeNode localFunctionNameNode = funcDefnOptionalCALDocNode.nextSibling();
                    
                    // get the node containing the parameters of the function (for checking against the @arg tags).
                    ParseTreeNode paramListNode = localFunctionNameNode.nextSibling();
                    paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);                        
                    
                    // the information on the function's type is stored as
                    // data on the optionalCALDocNode associated with the function definition.
                    
                    // we use the information in the type to determine how many @arg tags
                    // is allowed to be in the CALDoc comment.
                    int nTopLevelArrowsInType = funcDefnOptionalCALDocNode.getFunctionTypeForLocalFunctionCALDocComment().getArity();
                    CALDocComment calDocComment = checkAndBuildCALDocComment(optionalCALDocNode, paramListNode, nTopLevelArrowsInType, true, true, false);

                    LocalFunctionIdentifier identifier = typeChecker.getLocalFunctionIdentifier(functionName);
                    if(identifier != null) {
                        Function toplevelFunction = currentModuleTypeInfo.getFunction(identifier.getToplevelFunctionName().getUnqualifiedName());
                        Function localFunction = toplevelFunction.getLocalFunction(identifier);
                        if(localFunction != null) {
                            localFunction.setCALDocComment(calDocComment);
                        }
                    }
                }
            }
        }   
        
        ParseTreeNode inExprNode = defnListNode.nextSibling();
        if (inExprNode != null) {
            checkCALDocCommentsInExpr(inExprNode);
        }
    }

    /**
     * Checks the CALDoc comments within the specified foreign function definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param foreignFunctionDeclarationNode the foreign function definition node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInForeignFunctionDefn(ParseTreeNode foreignFunctionDeclarationNode) {
        
        foreignFunctionDeclarationNode.verifyType(CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION);
                               
        ParseTreeNode optionalCALDocNode = foreignFunctionDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
    
        ParseTreeNode externalNameNode = optionalCALDocNode.nextSibling();
        externalNameNode.verifyType(CALTreeParserTokenTypes.STRING_LITERAL);
                                                    
        ParseTreeNode accessModifierNode = externalNameNode.nextSibling();
        accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);                 
        
        ParseTreeNode typeDeclarationNode = accessModifierNode.nextSibling(); 
        typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                                                                                                                       
        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);             
        String functionName = functionNameNode.getText();
        
        // fetch the entity and then the type expression from the entity associated with the foreign function.
        FunctionalAgent entity = currentModuleTypeInfo.getFunction(functionName);
        if (entity == null) {
            String displayName = getQualifiedNameDisplayString(functionNameNode);
            // TypeChecker: unknown function or variable {displayName}.
            compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.UnknownFunctionOrVariable(displayName)));
            
        } else {
            TypeExpr typeExpr = entity.getTypeExpr();
    
            // check the foreign function's CALDoc with the function's type expression.
            CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, null, typeExpr, true, true, false);
            
            // associate the CALDoc with the entity
            entity.setCALDocComment(caldoc);
        }
    }

    /**
     * Checks the CALDoc comments within the specified primitive function definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param primitiveFunctionNode the primitive function definition node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInPrimitiveFunctionDefn(ParseTreeNode primitiveFunctionNode) {
        
        primitiveFunctionNode.verifyType(CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = primitiveFunctionNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
    
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);                 
        
        ParseTreeNode typeDeclarationNode = accessModifierNode.nextSibling();  
        typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);                            
                                                                                                         
        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);             
        String functionName = functionNameNode.getText();
        
        // fetch the entity and then the type expression from the entity associated with the primitive function.
        FunctionalAgent entity = currentModuleTypeInfo.getFunction(functionName);
        if (entity == null) {
            String displayName = getQualifiedNameDisplayString(functionNameNode);
            // TypeChecker: unknown function or variable {displayName}.
            compiler.logMessage(new CompilerMessage(functionNameNode, new MessageKind.Error.UnknownFunctionOrVariable(displayName)));
            
        } else {
            TypeExpr typeExpr = entity.getTypeExpr();
            
            // check the primitive function's CALDoc with the function's type expression.
            CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, null, typeExpr, true, true, false);
            
            // associate the CALDoc with the entity
            entity.setCALDocComment(caldoc);
        }
    }

    /**
     * Checks the CALDoc comments within the specified algebraic type definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param algebraicTypeDefnNode the algebraic type definition node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInAlgebraicTypeDefn(ParseTreeNode algebraicTypeDefnNode) {
        
        algebraicTypeDefnNode.verifyType(CALTreeParserTokenTypes.DATA_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = algebraicTypeDefnNode.firstChild();
        // check the class's CALDoc comment.
        CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, null, null, false, false, false);
    
        // check the children.
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);
        
        ParseTreeNode typeNameNode = accessModifierNode.nextSibling();
        typeNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
    
        ParseTreeNode typeParamListNode = typeNameNode.nextSibling();
        typeParamListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONS_PARAM_LIST);
        
        ParseTreeNode dataConsDefnListNode = typeParamListNode.nextSibling();
        dataConsDefnListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST);
        
        for (final ParseTreeNode dataConsDefnNode : dataConsDefnListNode) {
            
            checkCALDocCommentsInDataConsDefn(dataConsDefnNode);            
        }
        
        // associate the CALDoc with the entity
        currentModuleTypeInfo.getTypeConstructor(typeNameNode.getText()).setCALDocComment(caldoc);
    }

    /**
     * Checks the CALDoc comments within the specified data constructor definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param dataConsDefnNode the data constructor definition node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInDataConsDefn(ParseTreeNode dataConsDefnNode) {
    
        dataConsDefnNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);
    
        ParseTreeNode optionalCALDocNode = dataConsDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
    
        ParseTreeNode scopeNode = optionalCALDocNode.nextSibling();
        scopeNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);
              
        ParseTreeNode dataConsNameNode = scopeNode.nextSibling();
        dataConsNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
    
        ParseTreeNode dataConsArgListNode = dataConsNameNode.nextSibling();
        dataConsArgListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_ARG_LIST);
              
        CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, dataConsArgListNode, dataConsArgListNode.getNumberOfChildren(), true, false, true);
        
        // associate the CALDoc with the entity
        (currentModuleTypeInfo.getDataConstructor(dataConsNameNode.getText())).setCALDocComment(caldoc);
    }

    /**
     * Checks the CALDoc comments within the specified foreign type definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param foreignTypeDefnNode the foreign type definition node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInForeignTypeDefn(ParseTreeNode foreignTypeDefnNode) {
        
        foreignTypeDefnNode.verifyType(CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = foreignTypeDefnNode.firstChild();
        CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, null, null, false, false, false);
        
        ParseTreeNode implementationScopeNode = optionalCALDocNode.nextSibling();
        implementationScopeNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);  
    
        ParseTreeNode externalNameNode = implementationScopeNode.nextSibling();
        externalNameNode.verifyType(CALTreeParserTokenTypes.STRING_LITERAL);                                           
                      
        ParseTreeNode accessModifierNode = externalNameNode.nextSibling();
        accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);                 
        
        ParseTreeNode typeNameNode = accessModifierNode.nextSibling();
        typeNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);                                   
        String typeName = typeNameNode.getText();
        
        // associate the CALDoc with the entity
        currentModuleTypeInfo.getTypeConstructor(typeName).setCALDocComment(caldoc);
    }

    /**
     * Checks the CALDoc comments within the specified type class definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param typeClassDefnNode the type class definition node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInTypeClassDefn(ParseTreeNode typeClassDefnNode) {
        
        typeClassDefnNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);      
        
        ParseTreeNode optionalCALDocNode = typeClassDefnNode.firstChild();
        // check the instance's CALDoc comment.
        CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, null, null, false, false, false);
    
        // check the children.
        ParseTreeNode scopeNode = optionalCALDocNode.nextSibling();
        scopeNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);
        
        ParseTreeNode contextListNode = scopeNode.nextSibling();
        contextListNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);
        
        ParseTreeNode typeClassNameNode = contextListNode.nextSibling();
        typeClassNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);              
        
        ParseTreeNode typeVarNode = typeClassNameNode.nextSibling();
        typeVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
        ParseTreeNode classMethodListNode = typeVarNode.nextSibling();
        classMethodListNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD_LIST);
        
        String className = typeClassNameNode.getText();                       
        TypeClass typeClass = currentModuleTypeInfo.getTypeClass(className);
        
        for (final ParseTreeNode classMethodNode : classMethodListNode) {
            
            checkCALDocCommentsInClassMethodDefn(classMethodNode, typeClass);
        }
        
        // associate the CALDoc with the entity
        typeClass.setCALDocComment(caldoc);
    }

    /**
     * Checks the CALDoc comments within the specified class method definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param classMethodNode the class method definition node whose subtree is to be checked.
     * @param typeClass the type class entity.
     */
    private void checkCALDocCommentsInClassMethodDefn(ParseTreeNode classMethodNode, TypeClass typeClass) {
    
        classMethodNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD);  
        
        ParseTreeNode optionalCALDocNode = classMethodNode.firstChild();
        
        ParseTreeNode scopeNode = optionalCALDocNode.nextSibling();
        scopeNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);
        
        ParseTreeNode classMethodNameNode = scopeNode.nextSibling();                     
        classMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String methodName = classMethodNameNode.getText();
        
        TypeExpr typeExpr = currentModuleTypeInfo.getClassMethod(methodName).getTypeExpr();
        
        // check the class method CALDoc with the type expression calculated from the declared type signature.
        CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, null, typeExpr, true, true, false);
        
        // associate the CALDoc with the entity
        typeClass.getClassMethod(methodName).setCALDocComment(caldoc);
    }

    /**
     * Constructs a ClassInstanceIdentifier from an instance type cons node.
     * 
     * @param instanceTypeConsNode the instance type cons node.
     * @param typeClassName the name of the type class of the instance being declared.
     * @return a new ClassInstanceIdentifier representing the name of the instance.
     */
    private ClassInstanceIdentifier buildClassInstanceIdentifier(ParseTreeNode instanceTypeConsNode, QualifiedName typeClassName) {
        
        switch(instanceTypeConsNode.getType()) {
            case CALTreeParserTokenTypes.GENERAL_TYPE_CONSTRUCTOR:
            case CALTreeParserTokenTypes.UNPARENTHESIZED_TYPE_CONSTRUCTOR:
            {
                ParseTreeNode typeConsNameNode = instanceTypeConsNode.firstChild();                
                return new ClassInstanceIdentifier.TypeConstructorInstance(typeClassName, typeConsNameNode.toQualifiedName());
            }
                
            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR:            
            {
                return new ClassInstanceIdentifier.TypeConstructorInstance(typeClassName, CAL_Prelude.TypeConstructors.Function);
            }
            
            case CALTreeParserTokenTypes.UNIT_TYPE_CONSTRUCTOR:
            {               
                return new ClassInstanceIdentifier.TypeConstructorInstance(typeClassName, CAL_Prelude.TypeConstructors.Unit);                          
            }
            
            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR:           
            {
                return new ClassInstanceIdentifier.TypeConstructorInstance(typeClassName, CAL_Prelude.TypeConstructors.List);
            } 
            
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR:
            {
                return new ClassInstanceIdentifier.UniversalRecordInstance(typeClassName);
            }
            
            default: {
                instanceTypeConsNode.unexpectedParseTreeNode();
                return null;
            }
        }
    }

    /**
     * Checks the CALDoc comments within the specified instance definition. The checks
     * include verifying that identifiers contained within the comment are indeed
     * resolvable to entities within the module and/or other imported modules.
     * 
     * @param instanceDefnNode the instance definition node whose subtree is to be checked.
     */
    private void checkCALDocCommentsInInstanceDefn(ParseTreeNode instanceDefnNode) {
        
        instanceDefnNode.verifyType(CALTreeParserTokenTypes.INSTANCE_DEFN);
                
        ParseTreeNode optionalCALDocNode = instanceDefnNode.firstChild();
        // check the instance's CALDoc comment.
        CALDocComment caldoc = checkAndBuildCALDocComment(optionalCALDocNode, null, null, false, false, false);
    
        // check the children.
        ParseTreeNode instanceNameNode = optionalCALDocNode.nextSibling();
        instanceNameNode.verifyType(CALTreeParserTokenTypes.INSTANCE_NAME);
        
        ParseTreeNode contextListNode = instanceNameNode.firstChild();
        contextListNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);
        
        ParseTreeNode typeClassNameNode  = contextListNode.nextSibling();
        QualifiedName typeClassName = typeClassNameNode.toQualifiedName();
        
        ClassInstanceIdentifier instanceName = buildClassInstanceIdentifier(typeClassNameNode.nextSibling(), typeClassName);
        ClassInstance instance = currentModuleTypeInfo.getClassInstance(instanceName);
        
        // associate the CALDoc with the entity
        instance.setCALDocComment(caldoc);
                
        ParseTreeNode instanceMethodListNode = instanceNameNode.nextSibling();
        instanceMethodListNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD_LIST);
        
        for (final ParseTreeNode instanceMethodNode : instanceMethodListNode) {
            
            instanceMethodNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD);
            
            ParseTreeNode optionalInstanceMethodCALDocNode = instanceMethodNode.firstChild();
            optionalInstanceMethodCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            ParseTreeNode instanceMethodNameNode = optionalInstanceMethodCALDocNode.nextSibling();
            instanceMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            String instanceMethodName = instanceMethodNameNode.getText();
            
            ParseTreeNode instanceMethodDefnNode = instanceMethodNameNode.nextSibling();
            instanceMethodDefnNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
            
            // fetch the entity and then the type expression from the instance method's defining function.
            FunctionalAgent entity = retrieveQualifiedVar(instanceMethodDefnNode);
            if (entity == null) {
                String displayName = getQualifiedNameDisplayString(instanceMethodDefnNode);
                // TypeChecker: unknown function or variable {displayName}.
                compiler.logMessage(new CompilerMessage(instanceMethodDefnNode.getChild(1), new MessageKind.Error.UnknownFunctionOrVariable(displayName)));
            }
            
            TypeExpr typeExpr = entity.getTypeExpr();
    
            // check the instance method CALDoc with the defining function's type expression.
            CALDocComment methodCALDoc = checkAndBuildCALDocComment(optionalInstanceMethodCALDocNode, null, typeExpr, true, true, false);
            
            // associate the CALDoc with the entity
            instance.setMethodCALDocComment(instanceMethodName, methodCALDoc);
        }
    }

    /**
     * Constructs a CALDocComment.TextBlock from a parse tree node representing a text block
     * in a CALDoc comment.
     * 
     * @param blockNode the node representing the text block.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.TextBlock object representing the text block.
     */
    private CALDocComment.TextBlock buildCALDocTextBlock(ParseTreeNode blockNode, boolean inPreformattedContext, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT);
        
        return new CALDocComment.TextBlock(buildCALDocParagraphs(blockNode, inPreformattedContext, summaryCollector));
    }
    
    /**
     * Constructs a CALDocComment.TextBlock from a parse tree node representing a preformatted text segment in a CALDoc comment.
     * 
     * @param blockNode the ParseTreeNode representing a CALDoc preformatted text segment.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.TextBlock representing the preformatted text segment.
     */
    private CALDocComment.TextBlock buildCALDocPreformattedBlock(ParseTreeNode blockNode, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_PREFORMATTED_BLOCK);
        
        return new CALDocComment.TextBlock(buildCALDocParagraphs(blockNode, true, summaryCollector));
    }
    
    /**
     * Constructs a CALDocComment.TextParagraph from a parse tree node whose children represent the text segments of the paragraph.
     * It is an error for the these segments to contain a paragraph break.
     * 
     * @param parentNodeOfSegments the ParseTreeNode whose children represent text segments in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param contextTagName the name of the enclosing inline tag segment, for error reporting purposes.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.TextParagraph representing the single text paragraph.
     */
    private CALDocComment.TextParagraph checkAndBuildCALDocSingleTextParagraph(ParseTreeNode parentNodeOfSegments, boolean inPreformattedContext, String contextTagName, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        List<CALDocComment.Segment> segments = new ArrayList<CALDocComment.Segment>();

        ////
        /// A plain text segment is represented as one or more of CALDOC_TEXT_LINE, CALDOC_BLANK_TEXT_LINE and
        /// CALDOC_TEXT_LINE_BREAK. We loop through the children of the supplied node aggregating contiguous blocks
        /// of these nodes into plain text segments. A CALDOC_TEXT_INLINE_BLOCK node represents an inline block and is
        /// handled as an independent segment.
        //
        
        ////
        /// In the algorithm below, we trim any excess whitespace and newlines at the start of the paragraph and
        /// at the start of each line.
        ///
        /// We accomplish that by keeping track of whether the paragraph is empty - we trim whitespace
        /// for as long as nothing has been aggregated into the paragraph.
        ///
        /// We skip this step if we're in a pre-formatted context, since whitespace is preserved in that case.
        //
        boolean startOfLine = true;
        StringBuilder plainSegmentBuffer = new StringBuilder();
        boolean paragraphIsEmpty = true;
        
        loop:
        for (final ParseTreeNode contentNode : parentNodeOfSegments) {
            
            switch (contentNode.getType()) {
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE:
            {
                String text = contentNode.getText();
                if (startOfLine && !inPreformattedContext) {
                    text = CALDocLexer.trimLeadingCALDocWhitespace(text);
                }
                
                plainSegmentBuffer.append(text);
                
                if (paragraphIsEmpty && text.length() > 0) {
                    paragraphIsEmpty = false;
                }
                
                startOfLine = false;
                break;
            }
                
            case CALTreeParserTokenTypes.CALDOC_BLANK_TEXT_LINE:
            {
                String text = contentNode.getText();
                if (!inPreformattedContext) {
                    if (startOfLine) {
                        text = "";
                    } else {
                        text = " ";
                    }
                }
                
                plainSegmentBuffer.append(text);
                
                if (paragraphIsEmpty && text.length() > 0) {
                    paragraphIsEmpty = false;
                }
                
                // we don't change start-of-line status here
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE_BREAK:
            {
                // process newlines only if there's some text in the buffer already - we ignore initial newlines in a paragraph
                if (!paragraphIsEmpty) {
                    if (startOfLine) {
                        // we just saw a newline before, so this is a second one.... that's a paragraph break!
                        compiler.logMessage(new CompilerMessage(contentNode, new MessageKind.Error.ParagraphBreakCannotAppearHereInsideCALDocCommentInlineTag(contextTagName)));
                        break loop;
                        
                    } else {
                        plainSegmentBuffer.append('\n');
                        startOfLine = true;
                    }
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK:
            {
                // process the buffered up plain text first
                if (plainSegmentBuffer.length() > 0) {
                    // convert the plain text buffer into a segment and add it to the list
                    CALDocComment.PlainTextSegment plainTextSegment = CALDocComment.PlainTextSegment.makeWithEscapedText(plainSegmentBuffer.toString());
                    segments.add(plainTextSegment);
                    // reset the buffer afterwards for future use
                    plainSegmentBuffer = new StringBuilder();
                }
                
                // then process the inline tag segment
                ParseTreeNode inlineBlockNode = contentNode;
                ParseTreeNode inlineTagNode = inlineBlockNode.firstChild();
                
                int inlineTagNodeType = inlineTagNode.getType();
                
                if (inlineTagNodeType == CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST ||
                    inlineTagNodeType == CALTreeParserTokenTypes.CALDOC_TEXT_UNORDERED_LIST) {
                    
                    // this is an implicit paragraph break! not allowed!
                    String badTagName;
                    if (inlineTagNodeType == CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST) {
                        badTagName = "{@orderedList}";
                    } else {
                        badTagName = "{@unorderedList}";
                    }
                    
                    compiler.logMessage(new CompilerMessage(contentNode, new MessageKind.Error.ThisParticularInlineTagCannotAppearHereInsideCALDocCommentInlineTag(badTagName, contextTagName)));
                    
                } else {
                    addCALDocInlineTagSegmentToList(contentNode, inPreformattedContext, segments, summaryCollector);
                }
                
                paragraphIsEmpty = false;
                
                startOfLine = false;
                break;
            }
            
            default:
                throw new IllegalStateException("Unexpected parse tree node " + contentNode.toDebugString() + ".");
            }
        }
        
        // one final flush of the plain text buffer into a segment
        if (plainSegmentBuffer.length() > 0) {
            CALDocComment.PlainTextSegment plainTextSegment = CALDocComment.PlainTextSegment.makeWithEscapedText(plainSegmentBuffer.toString());
            segments.add(plainTextSegment);
        }
        
        return new CALDocComment.TextParagraph(segments.toArray(CALDocComment.TextParagraph.NO_SEGMENTS));
    }
    
    /**
     * Constructs a string from a parse tree node whose children represent the text segments of a single text paragraph without
     * inline tags. It is an error for the these segments to contain a paragraph break or inline tag segments.
     * 
     * @param parentNodeOfSegments the ParseTreeNode whose children represent text segments in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param contextTagName the name of the enclosing inline tag segment, for error reporting purposes.
     * @return a string representing a single text paragraph without inline tags. 
     */
    private String checkAndBuildCALDocSingleTextParagraphWithoutInlineTags(ParseTreeNode parentNodeOfSegments, boolean inPreformattedContext, String contextTagName) {
        
        ////
        /// A plain text segment is represented as one or more of CALDOC_TEXT_LINE, CALDOC_BLANK_TEXT_LINE and
        /// CALDOC_TEXT_LINE_BREAK. We loop through the children of the supplied node aggregating contiguous blocks
        /// of these nodes into plain text segments. A CALDOC_TEXT_INLINE_BLOCK node represents an inline block and is
        /// not allowed in a paragraph without inline tags. Such a node appearing would result in a compiler error.
        //
        
        ////
        /// In the algorithm below, we trim any excess whitespace and newlines at the start of the paragraph and
        /// at the start of each line.
        ///
        /// We accomplish that by keeping track of whether the paragraph is empty - we trim whitespace
        /// for as long as nothing has been aggregated into the paragraph.
        ///
        /// We skip this step if we're in a preformatted context, since whitespace is preserved in that case.
        //
        boolean startOfLine = true;
        StringBuilder plainSegmentBuffer = new StringBuilder();
        boolean paragraphIsEmpty = true;
        
        loop:
        for (final ParseTreeNode contentNode : parentNodeOfSegments) {
            
            switch (contentNode.getType()) {
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE:
            {
                String text = contentNode.getText();
                if (startOfLine && !inPreformattedContext) {
                    text = CALDocLexer.trimLeadingCALDocWhitespace(text);
                }
                
                plainSegmentBuffer.append(text);
                
                if (paragraphIsEmpty && text.length() > 0) {
                    paragraphIsEmpty = false;
                }
                
                startOfLine = false;
                break;
            }
                
            case CALTreeParserTokenTypes.CALDOC_BLANK_TEXT_LINE:
            {
                String text = contentNode.getText();
                if (!inPreformattedContext) {
                    if (startOfLine) {
                        text = "";
                    } else {
                        text = " ";
                    }
                }
                
                plainSegmentBuffer.append(text);
                
                if (paragraphIsEmpty && text.length() > 0) {
                    paragraphIsEmpty = false;
                }
                
                // we don't change start-of-line status here
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE_BREAK:
            {
                // process newlines only if there's some text in the buffer already - we ignore initial newlines in a paragraph
                if (!paragraphIsEmpty) {
                    if (startOfLine) {
                        // we just saw a newline before, so this is a second one.... that's a paragraph break!
                        compiler.logMessage(new CompilerMessage(contentNode, new MessageKind.Error.ParagraphBreakCannotAppearHereInsideCALDocCommentInlineTag(contextTagName)));
                        break loop;
                        
                    } else {
                        plainSegmentBuffer.append('\n');
                        startOfLine = true;
                    }
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK:
            {
                compiler.logMessage(new CompilerMessage(contentNode, new MessageKind.Error.InlineTagCannotAppearHereInsideCALDocCommentInlineTag(contextTagName)));
                break loop;
            }
            
            default:
                throw new IllegalStateException("Unexpected parse tree node " + contentNode.toDebugString() + ".");
            }
        }
        
        // one final flush of the plain text buffer into a segment
        return plainSegmentBuffer.toString();
    }
    
    /**
     * Constructs a string from a parse tree node representing a single-paragraph text block without
     * inline tags, of the type CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS.
     * It is an error for the the segments therein to contain a paragraph break or inline tag segments.
     * 
     * @param blockNode the node representing the text block of the type CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param contextTagName the name of the enclosing inline tag segment, for error reporting purposes.
     * @return a string representing a single text paragraph without inline tags.
     */
    private String checkAndBuildCALDocSingleParagraphTextBlockWithoutInlineTags(ParseTreeNode blockNode, boolean inPreformattedContext, String contextTagName) {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS);
        
        return checkAndBuildCALDocSingleTextParagraphWithoutInlineTags(blockNode, inPreformattedContext, contextTagName);
    }
    
    /**
     * Constructs a CALDocComment.TextParagraph from a parse tree node representing a text block
     * in a preformatted context in a CALDoc comment. Paragraph breaks are treated simply as newlines, and
     * so the result is always just one paragraph.
     * 
     * @param parentNodeOfSegments the ParseTreeNode whose children represent text segments in a CALDoc comment.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.TextParagraph representing the single preformatted paragraph.
     */
    private CALDocComment.TextParagraph buildCALDocTextBlockInPreformattedContext(ParseTreeNode parentNodeOfSegments, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        List<CALDocComment.Segment> segments = new ArrayList<CALDocComment.Segment>();
        
        ////
        /// A plain text segment is represented as one or more of CALDOC_TEXT_LINE, CALDOC_BLANK_TEXT_LINE and
        /// CALDOC_TEXT_LINE_BREAK. We loop through the children of the supplied node aggregating contiguous blocks
        /// of these nodes into plain text segments. A CALDOC_TEXT_INLINE_BLOCK node represents an inline block and is
        /// handled as an independent segment.
        //
        
        ////
        /// In the algorithm below, we preserve all whitespace as is.
        //
        StringBuilder plainSegmentBuffer = new StringBuilder();
               
        for (final ParseTreeNode contentNode : parentNodeOfSegments) {
            
            switch (contentNode.getType()) {
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE:
            case CALTreeParserTokenTypes.CALDOC_BLANK_TEXT_LINE:
            {
                String text = contentNode.getText();
                plainSegmentBuffer.append(text);
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE_BREAK:
            {
                plainSegmentBuffer.append('\n');
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK:
            {
                // process the buffered up plain text first
                if (plainSegmentBuffer.length() > 0) {
                    // convert the plain text buffer into a segment and add it to the list
                    CALDocComment.PlainTextSegment plainTextSegment = CALDocComment.PlainTextSegment.makeWithEscapedText(plainSegmentBuffer.toString());
                    segments.add(plainTextSegment);
                    // reset the buffer afterwards for future use
                    plainSegmentBuffer = new StringBuilder();
                }
                
                // then process the inline tag segment
                addCALDocInlineTagSegmentToList(contentNode, true, segments, summaryCollector);
                break;
            }
            
            default:
                throw new IllegalStateException("Unexpected parse tree node " + contentNode.toDebugString() + ".");
            }
        }
        
        // one final flush of the plain text buffer into a segment
        if (plainSegmentBuffer.length() > 0) {
            CALDocComment.PlainTextSegment plainTextSegment = CALDocComment.PlainTextSegment.makeWithEscapedText(plainSegmentBuffer.toString());
            segments.add(plainTextSegment);
        }
        
        return new CALDocComment.TextParagraph(segments.toArray(CALDocComment.TextParagraph.NO_SEGMENTS));
    }
    
    /**
     * Constructs an array of CALDocComment.Paragraph from a parse tree node whose children represent text segments
     * in a CALDoc comment. The segments are processed and aggregated into paragraphs.
     * 
     * @param parentNodeOfSegments the ParseTreeNode whose children represent text segments in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return an array of CALDocComment.Paragraph representing the paragraphs formed from the segments.
     */
    private CALDocComment.Paragraph[] buildCALDocParagraphs(ParseTreeNode parentNodeOfSegments, boolean inPreformattedContext, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {

        /// If the text is in a preformatted context, then we delegate to the method for constructing preformatted text blocks,
        /// and create a one-element array containing the resulting paragraph.
        //
        if (inPreformattedContext) {
            return new CALDocComment.Paragraph[] { buildCALDocTextBlockInPreformattedContext(parentNodeOfSegments, summaryCollector) };
        } else {
            /// Keep track of the paragraphs aggregated so far.
            //
            List<CALDocComment.Paragraph> paragraphs = new ArrayList<CALDocComment.Paragraph>();
            
            /// Keep track of the segments forming the current paragraph being built.
            //
            List<CALDocComment.Segment> segments = new ArrayList<CALDocComment.Segment>();
            
            ////
            /// A plain text segment is represented as one or more of CALDOC_TEXT_LINE, CALDOC_BLANK_TEXT_LINE and
            /// CALDOC_TEXT_LINE_BREAK. We loop through the children of the supplied node aggregating contiguous blocks
            /// of these nodes into plain text segments. A CALDOC_TEXT_INLINE_BLOCK node represents an inline block and is
            /// handled as an independent segment.
            //
            
            ////
            /// In the algorithm below, we trim any excess whitespace and newlines at the start of the paragraph and
            /// at the start of each line.
            ///
            /// We accomplish that by keeping track of whether the paragraph is empty - we trim whitespace
            /// for as long as nothing has been aggregated into the paragraph.
            ///
            /// We skip this step if we're in a preformatted context, since whitespace is preserved in that case.
            //
            
            ////
            /// We perform paragraph-breaking in the logic below. We treat a sequence of 2 or more newlines, potentially
            /// separated by whitespace, as a single paragraph break. We also treat {@unorderedList} and {@orderedList}
            /// as demarcations of new paragraphs (because lists are to be presented as separate from the surrounding text).
            //
            boolean startOfLine = true;
            StringBuilder plainSegmentBuffer = new StringBuilder();
            boolean paragraphIsEmpty = true;
            
            for (final ParseTreeNode contentNode : parentNodeOfSegments) {
                
                switch (contentNode.getType()) {
                case CALTreeParserTokenTypes.CALDOC_TEXT_LINE:
                {
                    String text = contentNode.getText();
                    if (startOfLine && !inPreformattedContext) {
                        text = CALDocLexer.trimLeadingCALDocWhitespace(text);
                    }
                    
                    plainSegmentBuffer.append(text);
                    
                    if (paragraphIsEmpty && text.length() > 0) {
                        paragraphIsEmpty = false;
                    }
                    
                    startOfLine = false;
                    break;
                }
                    
                case CALTreeParserTokenTypes.CALDOC_BLANK_TEXT_LINE:
                {
                    String text = contentNode.getText();
                    if (!inPreformattedContext) {
                        if (startOfLine) {
                            text = "";
                        } else {
                            text = " ";
                        }
                    }
                    
                    plainSegmentBuffer.append(text);
                    
                    if (paragraphIsEmpty && text.length() > 0) {
                        paragraphIsEmpty = false;
                    }
                    
                    // we don't change start-of-line status here
                    break;
                }
                
                case CALTreeParserTokenTypes.CALDOC_TEXT_LINE_BREAK:
                {
                    if (startOfLine) {
                        // we just saw a newline before, so this is a second one.... that's a paragraph break!
                        
                        // one final flush of the plain text buffer into a segment
                        if (plainSegmentBuffer.length() > 0) {
                            CALDocComment.PlainTextSegment plainTextSegment = CALDocComment.PlainTextSegment.makeWithEscapedText(plainSegmentBuffer.toString());
                            segments.add(plainTextSegment);
                        }
                        
                        plainSegmentBuffer = new StringBuilder();
                        
                        if (segments.size() > 0) {
                            CALDocComment.TextParagraph textParagraph = new CALDocComment.TextParagraph(segments.toArray(CALDocComment.TextParagraph.NO_SEGMENTS));
                            paragraphs.add(textParagraph);
                        }
                        
                        segments = new ArrayList<CALDocComment.Segment>();
                        
                        paragraphIsEmpty = true;
                        
                    } else {
                        // insert newlines only if there's some text in the buffer already - we ignore initial newlines in a paragraph
                        if (!paragraphIsEmpty) {
                            plainSegmentBuffer.append('\n');
                        }
                        startOfLine = true;
                    }
                    break;
                }
                
                case CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK:
                {
                    // process the buffered up plain text first
                    if (plainSegmentBuffer.length() > 0) {
                        // convert the plain text buffer into a segment and add it to the list
                        CALDocComment.PlainTextSegment plainTextSegment = CALDocComment.PlainTextSegment.makeWithEscapedText(plainSegmentBuffer.toString());
                        segments.add(plainTextSegment);
                        // reset the buffer afterwards for future use
                        plainSegmentBuffer = new StringBuilder();
                    }
                    
                    // then process the inline tag segment
                    
                    ParseTreeNode inlineBlockNode = contentNode;
                    ParseTreeNode inlineTagNode = inlineBlockNode.firstChild();
                    
                    switch (inlineTagNode.getType()) {
                    case CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST:
                    case CALTreeParserTokenTypes.CALDOC_TEXT_UNORDERED_LIST:
                    {
                        // this is an implicit paragraph break!
                        
                        // one final flush of the plain text buffer into a segment
                        if (plainSegmentBuffer.length() > 0) {
                            CALDocComment.PlainTextSegment plainTextSegment = CALDocComment.PlainTextSegment.makeWithEscapedText(plainSegmentBuffer.toString());
                            segments.add(plainTextSegment);
                        }
                        
                        plainSegmentBuffer = new StringBuilder();
                        
                        if (segments.size() > 0) {
                            CALDocComment.TextParagraph textParagraph = new CALDocComment.TextParagraph(segments.toArray(CALDocComment.TextParagraph.NO_SEGMENTS));
                            paragraphs.add(textParagraph);
                        }
                        
                        segments = new ArrayList<CALDocComment.Segment>();
                        
                        paragraphIsEmpty = true;
                        
                        // now process the list paragraph
                        paragraphs.add(buildCALDocInlineListTagSegment(inlineTagNode, summaryCollector));
                        break;
                    }
                        
                    default:
                    {
                        addCALDocInlineTagSegmentToList(contentNode, inPreformattedContext, segments, summaryCollector);
                        
                        paragraphIsEmpty = false;
                        
                        break;
                    }
                    }
                    
                    startOfLine = false;
                    break;
                }
                
                default:
                    throw new IllegalStateException("Unexpected parse tree node " + contentNode.toDebugString() + ".");
                }
            }

            // one final flush of the last paragraph
            if (plainSegmentBuffer.length() > 0) {
                CALDocComment.PlainTextSegment plainTextSegment = CALDocComment.PlainTextSegment.makeWithEscapedText(plainSegmentBuffer.toString());
                segments.add(plainTextSegment);
            }
            
            if (segments.size() > 0) {
                CALDocComment.TextParagraph textParagraph = new CALDocComment.TextParagraph(segments.toArray(CALDocComment.TextParagraph.NO_SEGMENTS));
                paragraphs.add(textParagraph);
            }
            
            return paragraphs.toArray(CALDocComment.TextBlock.NO_PARAGRAPHS);
        }
    }
    
    /**
     * Creates a CALDocComment.Segment from a parse tree node representing an inline tag segment, and adds it to the given list.
     *  
     * @param inlineBlockNode the ParseTreeNode representing an inline tag segment in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param segmentsList the list to which the new CALDocComment.Segment is to be added.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     */
    private void addCALDocInlineTagSegmentToList(ParseTreeNode inlineBlockNode, boolean inPreformattedContext, List<CALDocComment.Segment> segmentsList, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        inlineBlockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK);
        
        ParseTreeNode inlineTagNode = inlineBlockNode.firstChild();
        
        switch (inlineTagNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_TEXT_URL:
            segmentsList.add(buildCALDocInlineURLTagSegment(inlineTagNode));
            return;
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK:
            segmentsList.add(buildCALDocInlineLinkTagSegment(inlineTagNode));
            return;
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_EMPHASIZED_TEXT:
            segmentsList.add(buildCALDocInlineEmTagSegment(inlineTagNode, inPreformattedContext, summaryCollector));
            return;
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT:
            segmentsList.add(buildCALDocInlineStrongTagSegment(inlineTagNode, inPreformattedContext, summaryCollector));
            return;
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUPERSCRIPT_TEXT:
            segmentsList.add(buildCALDocInlineSupTagSegment(inlineTagNode, inPreformattedContext, summaryCollector));
            return;
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUBSCRIPT_TEXT:
            segmentsList.add(buildCALDocInlineSubTagSegment(inlineTagNode, inPreformattedContext, summaryCollector));
            return;
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUMMARY:
            addCALDocInlineSummaryTagSegmentToList(inlineTagNode, inPreformattedContext, segmentsList, summaryCollector);
            return;
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_CODE_BLOCK:
            segmentsList.add(buildCALDocInlineCodeTagSegment(inlineTagNode, summaryCollector));
            return;
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + inlineTagNode.toDebugString() + ".");
        }
    }
    
    /**
     * Constructs a CALDocComment.URLSegment from a parse tree node representing a hyperlinkable URL in a CALDoc comment.
     * @param urlNode the ParseTreeNode representing a hyperlinkable URL in a CALDoc comment.
     * @return a new CALDocComment.URLSegment object representing the URL.
     */
    private CALDocComment.URLSegment buildCALDocInlineURLTagSegment(ParseTreeNode urlNode) {
        urlNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_URL);
        
        ParseTreeNode contentNode = urlNode.firstChild();
        String content = checkAndBuildCALDocSingleParagraphTextBlockWithoutInlineTags(contentNode, false, "{@url}");
        
        return CALDocComment.URLSegment.makeWithEscapedText(content);
    }
    
    /**
     * Constructs a CALDocComment.LinkSegment from a parse tree node representing an inline cross-reference in a CALDoc comment.
     * @param linkNode the ParseTreeNode representing an inline cross-reference in a CALDoc comment.
     * @return a new CALDocComment.LinkSegment object representing the inline cross-reference.
     */
    private CALDocComment.LinkSegment buildCALDocInlineLinkTagSegment(ParseTreeNode linkNode) throws InvalidCALDocException {
        linkNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_LINK);

        // first make sure that function names and data constructor names are resolved and fully qualified
        // (for the case where the {@link} segment lacks the 'context' keyword, attempt to resolve it as a
        // data constructor and store the result of the resolution as an attribute on the refNode, i.e. linktNode's first grandchild)
        finder.findFreeVariablesInCALDocInlineLinkTagSegment(linkNode);
        
        ParseTreeNode linkContextNode = linkNode.firstChild();
        
        switch (linkContextNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_FUNCTION:
            return new CALDocComment.FunctionOrClassMethodLinkSegment(makeCALDocFunctionOrClassMethodCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_MODULE:
            return new CALDocComment.ModuleLinkSegment(makeCALDocModuleCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_DATACONS:
            return new CALDocComment.DataConsLinkSegment(makeCALDocDataConsCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECONS:
            return new CALDocComment.TypeConsLinkSegment(makeCALDocTypeConsCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECLASS:
            return new CALDocComment.TypeClassLinkSegment(makeCALDocTypeClassCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_WITHOUT_CONTEXT:
            return buildCALDocInlineLinkTagWithoutContextSegment(linkContextNode.firstChild());
        
        default:
            throw new IllegalStateException("Unexpected parse tree node " + linkNode.toDebugString() + ".");
        }
    }
    
    /**
     * Constructs a CALDocComment.LinkSegment from a parse tree node representing a cross-reference appearing without
     * a 'context' keyword in a CALDoc comment.
     * @param refNode the ParseTreeNode representing a cross-reference in a CALDoc comment.
     * @return a new CALDocComment.LinkSegment object representing the inline cross-reference.
     */
    private CALDocComment.LinkSegment buildCALDocInlineLinkTagWithoutContextSegment(ParseTreeNode refNode) throws InvalidCALDocException {
        switch (refNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR:
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR:
        {
            return new CALDocComment.FunctionOrClassMethodLinkSegment(makeCALDocFunctionOrClassMethodCrossReference(refNode));
        }
            
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS:
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS:
            return buildCALDocInlineLinkTagConsNameWithoutContextSegment(refNode);
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
        }
    }
    
    /**
     * Constructs a CALDocComment.LinkSegment from a parse tree node representing a cross-reference containing
     * an identifier starting with an uppercase character and appearing without a 'context' keyword in a CALDoc comment.
     * @param refNode the ParseTreeNode representing a cross-reference in a CALDoc comment.
     * @return a new CALDocComment.LinkSegment object representing the inline cross-reference.
     */
    private CALDocComment.LinkSegment buildCALDocInlineLinkTagConsNameWithoutContextSegment(ParseTreeNode refNode) throws InvalidCALDocException {
        Pair<CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference> pair = buildCALDocConsNameWithoutContextCrossReference(refNode, true);
        
        CategoryForCALDocConsNameWithoutContextCrossReference category = pair.fst();
        CALDocComment.Reference reference = pair.snd();
        
        if (category == CategoryForCALDocConsNameWithoutContextCrossReference.MODULE_NAME) {
            return new CALDocComment.ModuleLinkSegment((CALDocComment.ModuleReference)reference);
            
        } else if (category == CategoryForCALDocConsNameWithoutContextCrossReference.DATA_CONS_NAME) {
            return new CALDocComment.DataConsLinkSegment((CALDocComment.ScopedEntityReference)reference);
            
        } else if (category == CategoryForCALDocConsNameWithoutContextCrossReference.TYPE_CONS_NAME) {
            return new CALDocComment.TypeConsLinkSegment((CALDocComment.ScopedEntityReference)reference);
            
        } else if (category == CategoryForCALDocConsNameWithoutContextCrossReference.TYPE_CLASS_NAME) {
            return new CALDocComment.TypeClassLinkSegment((CALDocComment.ScopedEntityReference)reference);
            
        } else {
            throw new IllegalStateException();
        }
    }
    
    /*
     * todo-jowong: look into refactoring the following name resolution code. 
     */
    /**
     * Constructs a Pair (CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference) from a parse
     * tree node representing a cross-reference containing an identifier starting with an uppercase character and
     * appearing without a 'context' keyword in a CALDoc comment.
     * 
     * The CategoryForCALDocConsNameWithoutContextCrossReference is the category which the reference resolves to.
     * 
     * The CALDocComment.Reference is the actual object representing the cross-reference. 
     * 
     * @param refNode the ParseTreeNode representing a cross-reference in a CALDoc comment.
     * @param isLinkBlock true if this reference is in a "@link" block, false if this reference is in an "@see" block.
     * @return a Pair (CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference)
     */
    private Pair<CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference> buildCALDocConsNameWithoutContextCrossReference(ParseTreeNode refNode, boolean isLinkBlock) throws InvalidCALDocException {
        refNode.verifyType(CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS, CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
        
        boolean isUnchecked = (refNode.getType() == CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS);
        
        ParseTreeNode qualifiedConsNode = refNode.firstChild();
        qualifiedConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode nameBeforeDotNode = qualifiedConsNode.firstChild();
        nameBeforeDotNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        String nameBeforeDot = ModuleNameUtilities.getMaybeModuleNameStringFromParseTree(nameBeforeDotNode);
        
        boolean isUnqualified = (nameBeforeDot.length() == 0);
        
        ParseTreeNode nameAfterDotNode = nameBeforeDotNode.nextSibling();
        nameAfterDotNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String nameAfterDot = nameAfterDotNode.getText();

        String refName;
        if (isUnqualified) {
            refName = nameAfterDot;
        } else {
            refName = nameBeforeDot + "." + nameAfterDot;
        }
        
        /*
         * We simply do not handle unchecked references without context, e.g. {@link "Prelude"@}
         * 
         * We make this restriction because
         * - If the quoted reference is already resolvable, there's no need for the quotes.
         * 
         *   Furthermore, if the module defining the referenced entity changes (e.g. additions are made),
         *   the reference may then resolve to more than one entity... (because quoted references can
         *   refer to private entries)
         *   
         *   **This would be a compile error if the modules were compiled together, but would have
         *   escaped detection if the change was compiled after the reference is compiled. Therefore
         *   all references of this kind must be disallowed in the first place.**
         *   
         * - If the quoted reference is not resolvable, then we need more context to be able to tell
         *   what the reference is supposed to refer to.
         *   
         * - If the quoted reference is resolvable to more than one entity, then it is ambiguous.
         * 
         * 
         * In short: visibility of entities are non-local for quoted references... therefore all such
         *           references must be completely unambiguous and must not require any resolution.
         *           Hence they must appear with a context keyword.
         */
        if (isUnchecked) {
            compiler.logMessage(
                new CompilerMessage(
                    refNode,
                    new MessageKind.Error.UncheckedCrossReferenceIsAmbiguousInCALDocComment(refName)));
            
            throw new InvalidCALDocException();
        }
        
        /// Check to see if the name can be resolved as a module name (could be unqualified like M or qualified like K.L.M).
        //
        ModuleName resolvedAsModuleNameOrNull = null;
        
        ModuleNameResolver.ResolutionResult moduleNameResolution = currentModuleTypeInfo.getModuleNameResolver().resolve(ModuleName.make(refName));
        
        if (moduleNameResolution.isKnownUnambiguous()) {
            resolvedAsModuleNameOrNull = moduleNameResolution.getResolvedModuleName();
        }
        
        /// Check to see if the name can be resolved as a data constructor name. We count on the FreeVariableFinder
        /// to have done the resolution already. So we simply fetch the result it obtained via the custom attribute
        /// on the refNode).
        //
        QualifiedName resolvedAsDataConsNameOrNull = refNode.getCALDocCrossReferenceResolvedAsDataConstructor();

        final boolean suppressErrorMessageLogging = true;
        
        /// Check to see if the name can be resolved as a type constructor name. We perform the resolution attempt here.
        //
        ParseTreeNode copyOfQualifiedConsForTypeConsResolution = qualifiedConsNode.copyParseTree();
        TypeConstructor resolvedAsTypeConsOrNull = DataDeclarationChecker.resolveTypeConsName(copyOfQualifiedConsForTypeConsResolution, currentModuleTypeInfo, compiler, suppressErrorMessageLogging);
        QualifiedName resolvedAsTypeConsNameOrNull = (resolvedAsTypeConsOrNull == null) ? null : resolvedAsTypeConsOrNull.getName();

        /// Check to see if the name can be resolved as a type class name. We perform the resolution attempt here.
        //
        ParseTreeNode copyOfQualifiedConsNodeForTypeClassResolution = qualifiedConsNode.copyParseTree();
        TypeClass resolvedAsTypeClassOrNull = TypeClassChecker.resolveClassName(copyOfQualifiedConsNodeForTypeClassResolution, currentModuleTypeInfo, compiler, suppressErrorMessageLogging);
        QualifiedName resolvedAsTypeClassNameOrNull = (resolvedAsTypeClassOrNull == null) ? null : resolvedAsTypeClassOrNull.getName();

        // If the reference can be resolved as more than one kind of entity, then it does not matter
        // whether the reference is a checked or unchecked reference - it is an error because it is ambiguous.
        
        int numWaysToResolveName = 0;
        
        if (resolvedAsModuleNameOrNull != null) {
            numWaysToResolveName++;
        }
        
        if (resolvedAsTypeClassNameOrNull != null) {
            numWaysToResolveName++;
        }
        
        if (resolvedAsTypeConsNameOrNull != null) {
            numWaysToResolveName++;
        }
        
        if (resolvedAsDataConsNameOrNull != null) {
            numWaysToResolveName++;
        }
        
        if (numWaysToResolveName > 1) {
            /// It is a compile-time error for the name to be resolvable to more than one entity.
            //
            
            // Construct the error message and log it.
            
            List<String> alternativesList = new ArrayList<String>();
            if (resolvedAsModuleNameOrNull != null) {
                alternativesList.add(makeDisambiguatedCALDocCrossReferenceSourceText(refName, "module", isUnchecked, isLinkBlock));
            }
            
            if (resolvedAsTypeClassNameOrNull != null) {
                alternativesList.add(makeDisambiguatedCALDocCrossReferenceSourceText(refName, "typeClass", isUnchecked, isLinkBlock));
            }
            
            if (resolvedAsTypeConsNameOrNull != null) {
                alternativesList.add(makeDisambiguatedCALDocCrossReferenceSourceText(refName, "typeConstructor", isUnchecked, isLinkBlock));
            }
            
            if (resolvedAsDataConsNameOrNull != null) {
                alternativesList.add(makeDisambiguatedCALDocCrossReferenceSourceText(refName, "dataConstructor", isUnchecked, isLinkBlock));
            }
            
            String[] alternatives = alternativesList.toArray(new String[0]);
            
            compiler.logMessage(
                new CompilerMessage(
                    refNode,
                    new MessageKind.Error.CrossReferenceCanBeResolvedToMoreThanOneEntityInCALDocComment(refName, alternatives)));
            
            throw new InvalidCALDocException();
            
        } else if (numWaysToResolveName == 1) {
            /// This is the success case, where the name is resolvable to exactly one entity.
            //
            
            if (resolvedAsModuleNameOrNull != null) {
                // now that we have actually resolved the reference, run the deprecation check on it.
                SourceRange sourceRange = CALCompiler.getSourceRangeForCompilerMessage(qualifiedConsNode, currentModuleTypeInfo.getModuleName());
                compiler.checkResolvedModuleReference(resolvedAsModuleNameOrNull, sourceRange);
                
                ModuleNameUtilities.setModuleNameIntoParseTree(nameBeforeDotNode, resolvedAsModuleNameOrNull);
                
                CategoryForCALDocConsNameWithoutContextCrossReference category = CategoryForCALDocConsNameWithoutContextCrossReference.MODULE_NAME;
                refNode.setCategoryForCALDocConsNameWithoutContextCrossReference(category);
                return new Pair<CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference>(category, new CALDocComment.ModuleReference(resolvedAsModuleNameOrNull, isUnchecked, refName));
                
            } else if (resolvedAsDataConsNameOrNull != null) {
                // now that we have actually resolved the reference, run the deprecation check on it.
                FreeVariableFinder.checkResolvedDataConsReference(compiler, qualifiedConsNode, resolvedAsDataConsNameOrNull, false);
                
                ModuleNameUtilities.setModuleNameIntoParseTree(nameBeforeDotNode, resolvedAsDataConsNameOrNull.getModuleName());
                
                CategoryForCALDocConsNameWithoutContextCrossReference category = CategoryForCALDocConsNameWithoutContextCrossReference.DATA_CONS_NAME;
                refNode.setCategoryForCALDocConsNameWithoutContextCrossReference(category);
                return new Pair<CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference>(category, new CALDocComment.ScopedEntityReference(resolvedAsDataConsNameOrNull, isUnchecked, nameBeforeDot));
                
            } else if (resolvedAsTypeConsNameOrNull != null) {
                // now that we have actually resolved the reference, run the deprecation check on it.
                DataDeclarationChecker.checkResolvedTypeConsReference(compiler, qualifiedConsNode, resolvedAsTypeConsNameOrNull, currentModuleTypeInfo.getModuleName(), false);
                
                ModuleNameUtilities.setModuleNameIntoParseTree(nameBeforeDotNode, resolvedAsTypeConsNameOrNull.getModuleName());
                
                CategoryForCALDocConsNameWithoutContextCrossReference category = CategoryForCALDocConsNameWithoutContextCrossReference.TYPE_CONS_NAME;
                refNode.setCategoryForCALDocConsNameWithoutContextCrossReference(category);
                return new Pair<CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference>(category, new CALDocComment.ScopedEntityReference(resolvedAsTypeConsNameOrNull, isUnchecked, nameBeforeDot));
                
            } else if (resolvedAsTypeClassNameOrNull != null) {
                // now that we have actually resolved the reference, run the deprecation check on it.
                TypeClassChecker.checkResolvedTypeClassReference(compiler, qualifiedConsNode, resolvedAsTypeClassNameOrNull, false);
                
                ModuleNameUtilities.setModuleNameIntoParseTree(nameBeforeDotNode, resolvedAsTypeClassNameOrNull.getModuleName());
                
                CategoryForCALDocConsNameWithoutContextCrossReference category = CategoryForCALDocConsNameWithoutContextCrossReference.TYPE_CLASS_NAME;
                refNode.setCategoryForCALDocConsNameWithoutContextCrossReference(category);
                return new Pair<CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference>(category, new CALDocComment.ScopedEntityReference(resolvedAsTypeClassNameOrNull, isUnchecked, nameBeforeDot));
                
            } else {
                throw new IllegalStateException();
            }
            
        } else {
            /// The reference cannot be resolved as any visible module/type constructor/data constructor/type class.
            /// This is a compile time error.
            //
            
            if (isUnqualified) {
                ////
                /// Check to see if there are any alternatives had the unqualified name been fully qualified.
                //
                SortedSet<QualifiedName> candidateQualifiedNames = new TreeSet<QualifiedName>();
                
                int nImportedModules = currentModuleTypeInfo.getNImportedModules();
                
                for (int i = 0; i < nImportedModules; ++i) {
                    
                    ModuleTypeInfo importedModule = currentModuleTypeInfo.getNthImportedModule(i);
                    
                    TypeConstructor typeConsCandidate = importedModule.getTypeConstructor(nameAfterDot);
                    if (typeConsCandidate != null && currentModuleTypeInfo.isEntityVisible(typeConsCandidate)) {                
                        candidateQualifiedNames.add(typeConsCandidate.getName());
                    }
                    
                    DataConstructor dataConsCandidate = importedModule.getDataConstructor(nameAfterDot);
                    if (dataConsCandidate != null && currentModuleTypeInfo.isEntityVisible(dataConsCandidate)) {                
                        candidateQualifiedNames.add(dataConsCandidate.getName());
                    }
                    
                    TypeClass typeClassCandidate = importedModule.getTypeClass(nameAfterDot);
                    if (typeClassCandidate != null && currentModuleTypeInfo.isEntityVisible(typeClassCandidate)) {                
                        candidateQualifiedNames.add(typeClassCandidate.getName());
                    }
                }
                
                int nCandidates = candidateQualifiedNames.size();
                
                /// We choose an error message to use depending on the number of candidates we were able to identify.
                //
                if (nCandidates == 0) {
                    compiler.logMessage(
                        new CompilerMessage(
                            refNode,
                            new MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocComment(nameAfterDot)));
                    
                } else if (nCandidates == 1) {
                    QualifiedName candidate = candidateQualifiedNames.first();
                    
                    compiler.logMessage(
                        new CompilerMessage(
                            refNode,
                            new MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithSingleSuggestion(nameAfterDot, candidate)));
                    
                } else {
                    compiler.logMessage(
                        new CompilerMessage(
                            refNode,
                            new MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocCommentWithMultipleSuggestions(
                                nameAfterDot, candidateQualifiedNames.toArray(new QualifiedName[nCandidates]))));
                }
                
            } else {
                compiler.logMessage(
                    new CompilerMessage(
                        refNode,
                        new MessageKind.Error.CheckedCrossReferenceCannotBeResolvedInCALDocComment(nameBeforeDot + "." + nameAfterDot)));
            }
            
            throw new InvalidCALDocException();
        }
    }
    
    /**
     * Constructs the source text form of a disambiguated CALDoc cross reference, either in an "@link" or an "@see" block.
     * @param refName the (qualified/unqualified) name of the reference.
     * @param contextKeyword the context keyword used for disambiguation.
     * @param isUnchecked true if the reference is a quoted, unchecked reference.
     * @param isLinkBlock true if this reference is in a "@link" block, false if this reference is in an "@see" block.
     * @return the source text form of the requested CALDoc cross reference.
     */
    private String makeDisambiguatedCALDocCrossReferenceSourceText(String refName, String contextKeyword, boolean isUnchecked, boolean isLinkBlock) {
        String reference;
        if (isUnchecked) {
            reference = '\"' + refName + '\"';
        } else {
            reference = refName;
        }
        
        if (isLinkBlock) {
            return "{@link " + contextKeyword + " = " + reference + "@}";
        } else {
            return "@see " + contextKeyword + " = " + reference;
        }
    }
    
    /**
     * Constructs a CALDocComment.EmphasizedSegment from a parse tree node representing an emphasized piece of text in a CALDoc comment.
     * @param emNode the ParseTreeNode representing an emphasized piece of text in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.EmphasizedSegment object representing the emphasized piece of text.
     */
    private CALDocComment.EmphasizedSegment buildCALDocInlineEmTagSegment(ParseTreeNode emNode, boolean inPreformattedContext, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        emNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_EMPHASIZED_TEXT);
        
        ParseTreeNode contentNode = emNode.firstChild();
        CALDocComment.TextParagraph content = checkAndBuildCALDocSingleTextParagraph(contentNode, inPreformattedContext, "{@em}", summaryCollector);
        
        return new CALDocComment.EmphasizedSegment(content);
    }
    
    /**
     * Constructs a CALDocComment.StronglyEmphasizedSegment from a parse tree node representing a strongly emphasized piece of text in a CALDoc comment.
     * @param strongNode the ParseTreeNode representing a strongly emphasized piece of text in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.StronglyEmphasizedSegment object representing the strongly emphasized piece of text.
     */
    private CALDocComment.StronglyEmphasizedSegment buildCALDocInlineStrongTagSegment(ParseTreeNode strongNode, boolean inPreformattedContext, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        strongNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT);
        
        ParseTreeNode contentNode = strongNode.firstChild();
        CALDocComment.TextParagraph content = checkAndBuildCALDocSingleTextParagraph(contentNode, inPreformattedContext, "{@strong}", summaryCollector);
        
        return new CALDocComment.StronglyEmphasizedSegment(content);
    }
    
    /**
     * Constructs a CALDocComment.SuperscriptSegment from a parse tree node representing a superscripted piece of text in a CALDoc comment.
     * @param supNode the ParseTreeNode representing a superscripted piece of text in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.SuperscriptSegment object representing the superscripted piece of text.
     */
    private CALDocComment.SuperscriptSegment buildCALDocInlineSupTagSegment(ParseTreeNode supNode, boolean inPreformattedContext, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        supNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUPERSCRIPT_TEXT);
        
        ParseTreeNode contentNode = supNode.firstChild();
        CALDocComment.TextParagraph content = checkAndBuildCALDocSingleTextParagraph(contentNode, inPreformattedContext, "{@sup}", summaryCollector);
        
        return new CALDocComment.SuperscriptSegment(content);
    }
    
    /**
     * Constructs a CALDocComment.SubscriptSegment from a parse tree node representing a subscripted piece of text in a CALDoc comment.
     * @param subNode the ParseTreeNode representing a subscripted piece of text in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.SubscriptSegment object representing the subscripted piece of text.
     */
    private CALDocComment.SubscriptSegment buildCALDocInlineSubTagSegment(ParseTreeNode subNode, boolean inPreformattedContext, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        subNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUBSCRIPT_TEXT);
        
        ParseTreeNode contentNode = subNode.firstChild();
        CALDocComment.TextParagraph content = checkAndBuildCALDocSingleTextParagraph(contentNode, inPreformattedContext, "{@sub}", summaryCollector);
        
        return new CALDocComment.SubscriptSegment(content);
    }
    
    /**
     * Gathers the segments constructed from a parse tree node representing a "@summary" inline tag segment in a CALDoc comment,
     * and adds them to the given list while submitting them to the given SummaryCollector.
     * 
     * @param summaryNode the ParseTreeNode representing a "@summary" inline tag segment in a CALDoc comment.
     * @param inPreformattedContext whether the text is in a preformatted context, and to be dealt with accordingly.
     * @param segmentsList the list to which the new CALDocComment.Segment is to be added.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     */
    private void addCALDocInlineSummaryTagSegmentToList(ParseTreeNode summaryNode, boolean inPreformattedContext, List<CALDocComment.Segment> segmentsList, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        summaryNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUMMARY);
        
        ParseTreeNode contentNode = summaryNode.firstChild();
        CALDocComment.TextParagraph content = checkAndBuildCALDocSingleTextParagraph(contentNode, inPreformattedContext, "{@summary}", summaryCollector);
        
        for (int i = 0, n = content.getNSegments(); i < n; i++) {
            segmentsList.add(content.getNthSegment(i));
        }
        
        // associate the summary with the comment via the builder
        summaryCollector.addSummaryParagraph(content);
    }
    
    /**
     * Constructs a CALDocComment.CodeSegment from a parse tree node representing a code block in a CALDoc comment.
     * @param codeNode the ParseTreeNode representing a code block in a CALDoc comment.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.CodeSegment object representing the code block.
     */
    private CALDocComment.CodeSegment buildCALDocInlineCodeTagSegment(ParseTreeNode codeNode, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        codeNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_CODE_BLOCK);
        
        ParseTreeNode contentNode = codeNode.firstChild();
        CALDocComment.TextBlock content = buildCALDocPreformattedBlock(contentNode, summaryCollector);
        
        return new CALDocComment.CodeSegment(content);
    }
    
    /**
     * Constructs a CALDocComment.ListParagraph from a parse tree node representing a list in a CALDoc comment.
     * @param listNode the ParseTreeNode representing a list in a CALDoc comment.
     * @param summaryCollector the SummaryCollector to use in building up the comment's summary.
     * @return a new CALDocComment.ListParagraph object representing the list.
     */
    private CALDocComment.ListParagraph buildCALDocInlineListTagSegment(ParseTreeNode listNode, CALDocComment.SummaryCollector summaryCollector) throws InvalidCALDocException {
        listNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST, CALTreeParserTokenTypes.CALDOC_TEXT_UNORDERED_LIST);
        
        List<CALDocComment.ListItem> items = new ArrayList<CALDocComment.ListItem>();
        
        for (final ParseTreeNode itemNode : listNode) {

            itemNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_LIST_ITEM);
            
            ParseTreeNode itemContentNode = itemNode.firstChild();
            CALDocComment.TextBlock content = buildCALDocTextBlock(itemContentNode, false, summaryCollector);
            
            CALDocComment.ListItem item = new CALDocComment.ListItem(content);
            
            items.add(item);
        }
        
        CALDocComment.ListItem[] itemsArray = items.toArray(CALDocComment.ListParagraph.NO_ITEMS);
        
        boolean isOrdered = listNode.getType() == CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST;
        
        return new CALDocComment.ListParagraph(isOrdered, itemsArray);
    }
    
    /**
     * Checks whether the CALDoc associated with a particular source element is
     * valid - that all names referenced can be resolved and that it contains
     * only the allowed tags.
     * 
     * @param optionalCALDocNode
     *            the optionalCALDoc node, with a type of
     *            OPTIONAL_CALDOC_COMMENT.
     * @param paramListNode
     *            the node whose children are the declared parameters, or null
     *            if the source element being commented does not have
     *            parameters.
     * @param functionTypeExpr
     *            the type expression of the function/class method/instance
     *            method, or null if the source element being commented is not
     *            associated with a type expression.
     * @param allowsArgTag
     *            whether the "@arg" tag is allowed in the comment.
     * @param allowsReturnTag
     *            whether the "@return" tag is allowed in the comment.
     * @param allowsOrdinalArgName
     *            whether the name appearing in an "@arg" tag is allowed to be an ordinal field name (e.g. #3).
     * @return a new instance of CALDocComment representing the comment.
     */
    private CALDocComment checkAndBuildCALDocComment(ParseTreeNode optionalCALDocNode, ParseTreeNode paramListNode, TypeExpr functionTypeExpr, boolean allowsArgTag, boolean allowsReturnTag, boolean allowsOrdinalArgName) {
        
        int nTopLevelArrowsInType;
        
        if (functionTypeExpr != null) {
            nTopLevelArrowsInType = functionTypeExpr.getArity();
        } else {
            if (!allowsArgTag) {
                nTopLevelArrowsInType = -1;
            } else {
                // if the "@arg" tag is allowed but information about the function/method's type is not supplied,
                // then that's an internal programming error.
                throw new IllegalArgumentException();
            }
        }
        
        return checkAndBuildCALDocComment(optionalCALDocNode, paramListNode, nTopLevelArrowsInType, allowsArgTag, allowsReturnTag, allowsOrdinalArgName);
    }

    /**
     * Checks whether the CALDoc associated with a particular source element is
     * valid - that all names referenced can be resolved and that it contains
     * only the allowed tags.
     * 
     * @param optionalCALDocNode
     *            the optionalCALDoc node, with a type of
     *            OPTIONAL_CALDOC_COMMENT.
     * @param paramListNode
     *            the node whose children are the declared parameters, or null
     *            if the source element being commented does not have
     *            parameters.
     * @param nTopLevelArrowsInType
     *            the number of top-level arrows in the type expression of the
     *            function/class method/instance method, or -1 if the source
     *            element being commented is not associated with a type
     *            expression.
     * @param allowsArgTag
     *            whether the "@arg" tag is allowed in the comment.
     * @param allowsReturnTag
     *            whether the "@return" tag is allowed in the comment.
     * @param allowsOrdinalArgName
     *            whether the name appearing in an "@arg" tag is allowed to be an ordinal field name (e.g. #3).
     * @return a new instance of CALDocComment representing the comment, or null if there is no comment.
     */
    private CALDocComment checkAndBuildCALDocComment(ParseTreeNode optionalCALDocNode, ParseTreeNode paramListNode, int nTopLevelArrowsInType, boolean allowsArgTag, boolean allowsReturnTag, boolean allowsOrdinalArgName) {
        
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        ParseTreeNode calDocNode = optionalCALDocNode.firstChild();
        if (calDocNode == null) {
            return null;
        }
    
        // if the "@arg" tag is allowed but information about the function/method's type is not supplied,
        // then that's an internal programming error.
        if (nTopLevelArrowsInType < 0 && allowsArgTag) {
            throw new IllegalArgumentException();
        }
        
        boolean errorInCALDoc = false;
        CALDocComment.Builder builder = new CALDocComment.Builder();
        
        calDocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
        
        ParseTreeNode descriptionNode = calDocNode.firstChild();
        descriptionNode.verifyType(CALTreeParserTokenTypes.CALDOC_DESCRIPTION_BLOCK);
        
        try {
            builder.setDescriptionBlock(buildCALDocTextBlock(descriptionNode.firstChild(), false, builder));
        } catch (InvalidCALDocException e) {
            errorInCALDoc = true;
        }
        
        ParseTreeNode taggedBlockListNode = descriptionNode.nextSibling();
        taggedBlockListNode.verifyType(CALTreeParserTokenTypes.CALDOC_TAGGED_BLOCKS);
        
        ParseTreeNode nextParamToCheck;
        if (paramListNode == null) {
            nextParamToCheck = null;
        } else {
            nextParamToCheck = paramListNode.firstChild();
            while (nextParamToCheck != null && nextParamToCheck.getText().startsWith("$dictvar")) {
                nextParamToCheck = nextParamToCheck.nextSibling();
            }
        }
        
        int nArgsSeen = 0;
        
        // loop through the tagged blocks to perform checking on the identifiers
        // contained within them.
        for (final ParseTreeNode taggedBlockNode : taggedBlockListNode) {
            
            // check to see that the argument names match the parameter list, and are
            // declared in order
            
            switch (taggedBlockNode.getType()) {
            
                case CALTreeParserTokenTypes.CALDOC_AUTHOR_BLOCK:
                    // nothing to check, since there can be any number of @author blocks in a CALDoc comment
                    // so just add it to the builder
                    try {
                        builder.addAuthorBlock(buildCALDocTextBlock(taggedBlockNode.firstChild(), false, builder));
                    } catch (InvalidCALDocException e) {
                        errorInCALDoc = true;
                    }
                    break;
            
                case CALTreeParserTokenTypes.CALDOC_ARG_BLOCK:
                {
                    nArgsSeen++;
                    
                    if (allowsArgTag) {
                        // if there are more @arg tags then there are top level arrows in the type expression
                        // then there are too many @arg tags.
                        if (nArgsSeen > nTopLevelArrowsInType) {
                            compiler.logMessage(
                                new CompilerMessage(
                                    taggedBlockNode,
                                    new MessageKind.Error.TooManyArgTagsInCALDocComment()));
                            
                            errorInCALDoc = true;
                        }
                        
                        ParseTreeNode argNameNode = taggedBlockNode.firstChild();
                        argNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
                        String argName = argNameNode.getText();
                                                
                        if (nextParamToCheck != null) {
                            
                            nextParamToCheck.verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAMED_ARG);
                            String paramName;
                            if (nextParamToCheck.getType() == CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAMED_ARG) {
                                paramName = nextParamToCheck.firstChild().getText();
                            } else {
                                while (nextParamToCheck != null && nextParamToCheck.getText().startsWith("$dictvar")) {
                                    nextParamToCheck = nextParamToCheck.nextSibling();
                                    nextParamToCheck.verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);
                                }
                                paramName = FreeVariableFinder.getDisplayName(nextParamToCheck.getText());
                            }
                            
                            if (!allowsOrdinalArgName && argNameNode.getType() == CALTreeParserTokenTypes.ORDINAL_FIELD_NAME) {
                                // if the param name appearing after the @arg tag is an ordinal field name but ordinal argument
                                // names are not allowed (e.g. for functions/class methods/instance methods), then it is an error.
                                compiler.logMessage(
                                    new CompilerMessage(
                                        argNameNode,
                                        new MessageKind.Error.InvalidArgNameInCALDocComment(argName)));
                                
                                errorInCALDoc = true;
                                
                            } else if (!argName.equals(paramName)) {
                                // if the param name appearing after the @arg tag is not the same as the param name
                                // in the param list, then it is an error.
                                compiler.logMessage(
                                    new CompilerMessage(
                                        argNameNode,
                                        new MessageKind.Error.ArgNameDoesNotMatchDeclaredNameInCALDocComment(argName, paramName)));                        
                                
                                errorInCALDoc = true;
                            }
                            
                            // advance to the next param for the next check
                            nextParamToCheck = nextParamToCheck.nextSibling();
                        }
                        
                        try {
                            builder.addArgBlock(new CALDocComment.ArgBlock(FieldName.make(argName), buildCALDocTextBlock(argNameNode.nextSibling(), false, builder)));
                        } catch (InvalidCALDocException e) {
                            errorInCALDoc = true;
                        }
                        
                    } else {
                        // the "@arg" tag is not allowed in this comment
                        compiler.logMessage(
                            new CompilerMessage(
                                taggedBlockNode,
                                new MessageKind.Error.DisallowedTagInCALDocComment("@arg")));
                        
                        errorInCALDoc = true;
                    }
                    
                    break;
                }
                
                case CALTreeParserTokenTypes.CALDOC_RETURN_BLOCK:
                {
                    if (allowsReturnTag) {
                        if (builder.hasReturnBlock()) {
                            compiler.logMessage(
                                new CompilerMessage(
                                    taggedBlockNode,
                                    new MessageKind.Error.SingletonTagAppearsMoreThanOnceInCALDocComment("@return")));                        
                            
                            errorInCALDoc = true;
                            
                        } else {
                            try {
                                builder.setReturnBlock(buildCALDocTextBlock(taggedBlockNode.firstChild(), false, builder));
                            } catch (InvalidCALDocException e) {
                                errorInCALDoc = true;
                            }
                        }
                    } else {
                        // the "@return" tag is not allowed in this comment
                        compiler.logMessage(
                            new CompilerMessage(
                                taggedBlockNode,
                                new MessageKind.Error.DisallowedTagInCALDocComment("@return")));
                        
                        errorInCALDoc = true;
                    }
                    
                    break;
                }
                
                case CALTreeParserTokenTypes.CALDOC_DEPRECATED_BLOCK:
                {
                    if (builder.hasDeprecatedBlock()) {
                        compiler.logMessage(
                            new CompilerMessage(
                                taggedBlockNode,
                                new MessageKind.Error.SingletonTagAppearsMoreThanOnceInCALDocComment("@deprecated")));                        
                        
                        errorInCALDoc = true;
                        
                    } else {
                        try {
                            builder.setDeprecatedBlock(buildCALDocTextBlock(taggedBlockNode.firstChild(), false, builder));
                        } catch (InvalidCALDocException e) {
                            errorInCALDoc = true;
                        }
                    }
                    
                    break;
                }
                
                case CALTreeParserTokenTypes.CALDOC_VERSION_BLOCK:
                {
                    if (builder.hasVersionBlock()) {
                        compiler.logMessage(
                            new CompilerMessage(
                                taggedBlockNode,
                                new MessageKind.Error.SingletonTagAppearsMoreThanOnceInCALDocComment("@version")));                        
                        
                        errorInCALDoc = true;
                        
                    } else {
                        try {
                            builder.setVersionBlock(buildCALDocTextBlock(taggedBlockNode.firstChild(), false, builder));
                        } catch (InvalidCALDocException e) {
                            errorInCALDoc = true;
                        }
                    }
                    
                    break;
                }
                
                case CALTreeParserTokenTypes.CALDOC_SEE_BLOCK:
                {
                    if (!checkAndBuildCALDocSeeBlock(taggedBlockNode.firstChild(), builder)) {
                        errorInCALDoc = true;
                    }
                    break;
                }
                
                default:
                {
                    taggedBlockNode.unexpectedParseTreeNode();                    
                    errorInCALDoc = true;
                    break;
                }            
            }
        }
        
        if (!errorInCALDoc) {
            return builder.toComment();
        } else {
            return null;
        }
    }

    /**
     * Constructs a QualifiedName from a parse tree node that represents an unchecked reference to
     * a function or class method. A resolution attempt is made to resolve the name using the module
     * type info and the import using clauses. If the name is resolvable then the module name of an
     * unqualified reference can be obtained. Otherwise we default to using the current module's name,
     * since an unqualified reference can only refer to local entities unless the name appears in an
     * import using clause.
     * 
     * @param nameNode the parse tree node representing a qualified name.
     * @return a new QualifiedName.
     */
    private QualifiedName buildCALDocUncheckedSeeReferenceFunctionOrClassMethodName(ParseTreeNode nameNode) {
        
        nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
    
        ParseTreeNode moduleNameNode = nameNode.firstChild();
        String moduleName = ModuleNameUtilities.getMaybeModuleNameStringFromParseTree(moduleNameNode);
        
        if (moduleName.length() == 0) {
            ParseTreeNode unqualifiedNameNode = moduleNameNode.nextSibling();
            String unqualifiedName = unqualifiedNameNode.getText();

            // if the name is already resolvable, then simply use the fully qualified name of the entity
            FunctionalAgent entity = currentModuleTypeInfo.getFunctionOrClassMethod(unqualifiedName);
            
            if (entity != null) {
                return entity.getName();
            } else {
                // check the import using clauses
                ModuleName moduleNameToUse = currentModuleTypeInfo.getModuleOfUsingFunctionOrClassMethod(unqualifiedName);
                
                if (moduleNameToUse == null) {
                    // the name cannot currently be resolved, so default to using the current module name
                    moduleNameToUse = currentModuleTypeInfo.getModuleName();
                }
                
                return QualifiedName.make(moduleNameToUse, unqualifiedName);
            }
        } else {
            return nameNode.toQualifiedName();
        }
    }

    /**
     * Constructs a QualifiedName from a parse tree node that represents an unchecked reference to
     * a data constructor. A resolution attempt is made to resolve the name using the module
     * type info and the import using clauses. If the name is resolvable then the module name of an
     * unqualified reference can be obtained. Otherwise we default to using the current module's name,
     * since an unqualified reference can only refer to local entities unless the name appears in an
     * import using clause.
     * 
     * @param nameNode the parse tree node representing a qualified name.
     * @return a new QualifiedName.
     */
    private QualifiedName buildCALDocUncheckedSeeReferenceDataConsName(ParseTreeNode nameNode) {
        
        nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
    
        ParseTreeNode moduleNameNode = nameNode.firstChild();
        String moduleName = ModuleNameUtilities.getMaybeModuleNameStringFromParseTree(moduleNameNode);
        
        if (moduleName.length() == 0) {
            ParseTreeNode unqualifiedNameNode = moduleNameNode.nextSibling();
            String unqualifiedName = unqualifiedNameNode.getText();

            // if the name is already resolvable, then simply use the fully qualified name of the entity
            FunctionalAgent entity = currentModuleTypeInfo.getDataConstructor(unqualifiedName);
            
            if (entity != null) {
                return entity.getName();
            } else {
                // check the import using clauses
                ModuleName moduleNameToUse = currentModuleTypeInfo.getModuleOfUsingDataConstructor(unqualifiedName);
                
                if (moduleNameToUse == null) {
                    // the name cannot currently be resolved, so default to using the current module name
                    moduleNameToUse = currentModuleTypeInfo.getModuleName();
                }
                
                return QualifiedName.make(moduleNameToUse, unqualifiedName);
            }
        } else {
            return nameNode.toQualifiedName();
        }
    }

    /**
     * Constructs a QualifiedName from a parse tree node that represents an unchecked reference to
     * a type constructor. A resolution attempt is made to resolve the name using the module
     * type info and the import using clauses. If the name is resolvable then the module name of an
     * unqualified reference can be obtained. Otherwise we default to using the current module's name,
     * since an unqualified reference can only refer to local entities unless the name appears in an
     * import using clause.
     * 
     * @param nameNode the parse tree node representing a qualified name.
     * @return a new QualifiedName.
     */
    private QualifiedName buildCALDocUncheckedSeeReferenceTypeConsName(ParseTreeNode nameNode) {
        
        nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
    
        ParseTreeNode moduleNameNode = nameNode.firstChild();
        String moduleName = ModuleNameUtilities.getMaybeModuleNameStringFromParseTree(moduleNameNode);
        
        if (moduleName.length() == 0) {
            ParseTreeNode unqualifiedNameNode = moduleNameNode.nextSibling();
            String unqualifiedName = unqualifiedNameNode.getText();

            // if the name is already resolvable, then simply use the fully qualified name of the entity
            TypeConstructor typeCons = currentModuleTypeInfo.getTypeConstructor(unqualifiedName);
            
            if (typeCons != null) {
                return typeCons.getName();
            } else {
                // check the import using clauses
                ModuleName moduleNameToUse = currentModuleTypeInfo.getModuleOfUsingTypeConstructor(unqualifiedName);
                
                if (moduleNameToUse == null) {
                    // the name cannot currently be resolved, so default to using the current module name
                    moduleNameToUse = currentModuleTypeInfo.getModuleName();
                }
                
                return QualifiedName.make(moduleNameToUse, unqualifiedName);
            }
        } else {
            return nameNode.toQualifiedName();
        }
    }

    /**
     * Constructs a QualifiedName from a parse tree node that represents an unchecked reference to
     * a type class. A resolution attempt is made to resolve the name using the module
     * type info and the import using clauses. If the name is resolvable then the module name of an
     * unqualified reference can be obtained. Otherwise we default to using the current module's name,
     * since an unqualified reference can only refer to local entities unless the name appears in an
     * import using clause.
     * 
     * @param nameNode the parse tree node representing a qualified name.
     * @return a new QualifiedName.
     */
    private QualifiedName buildCALDocUncheckedSeeReferenceTypeClassName(ParseTreeNode nameNode) {
        
        nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
    
        ParseTreeNode moduleNameNode = nameNode.firstChild();
        String moduleName = ModuleNameUtilities.getMaybeModuleNameStringFromParseTree(moduleNameNode);
        
        if (moduleName.length() == 0) {
            ParseTreeNode unqualifiedNameNode = moduleNameNode.nextSibling();
            String unqualifiedName = unqualifiedNameNode.getText();

            // if the name is already resolvable, then simply use the fully qualified name of the type class
            TypeClass typeClass = currentModuleTypeInfo.getTypeClass(unqualifiedName);
            
            if (typeClass != null) {
                return typeClass.getName();
            } else {
                // check the import using clauses
                ModuleName moduleNameToUse = currentModuleTypeInfo.getModuleOfUsingTypeClass(unqualifiedName);
                
                if (moduleNameToUse == null) {
                    // the name cannot currently be resolved, so default to using the current module name
                    moduleNameToUse = currentModuleTypeInfo.getModuleName();
                }
                
                return QualifiedName.make(moduleNameToUse, unqualifiedName);
            }
        } else {
            return nameNode.toQualifiedName();
        }
    }

    /**
     * Check the contents of a CALDoc '@see' block to make sure that
     * all the identifiers contained within it could be resolved.
     * 
     * @param seeBlockNode
     * @param builder the CALDocComment.Builder to use in building the CALDocComment instance.
     * @return true iff there are no errors in the '@see' block.
     */
    private boolean checkAndBuildCALDocSeeBlock(ParseTreeNode seeBlockNode, CALDocComment.Builder builder) {
        
        boolean hasError = false;
        
        // first make sure that function names and data constructor names are resolved and fully qualified
        finder.findFreeVariablesInCALDocSeeBlock(seeBlockNode);
        
        switch (seeBlockNode.getType()) {
        
            case CALTreeParserTokenTypes.CALDOC_SEE_FUNCTION_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    
                    try {
                        builder.addFunctionOrClassMethodReference(makeCALDocFunctionOrClassMethodCrossReference(refNode));
                    } catch (InvalidCALDocException e) {
                        hasError = true;
                    }
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_MODULE_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    
                    try {
                        builder.addModuleReference(makeCALDocModuleCrossReference(refNode));
                    } catch (InvalidCALDocException e) {
                        hasError = true;
                    }
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_DATACONS_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    
                    try {
                        builder.addDataConstructorReference(makeCALDocDataConsCrossReference(refNode));
                    } catch (InvalidCALDocException e) {
                        hasError = true;
                    }
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_TYPECONS_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    
                    try {
                        builder.addTypeConstructorReference(makeCALDocTypeConsCrossReference(refNode));
                    } catch (InvalidCALDocException e) {
                        hasError = true;
                    }
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_TYPECLASS_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    
                    try {
                        builder.addTypeClassReference(makeCALDocTypeClassCrossReference(refNode));
                    } catch (InvalidCALDocException e) {
                        hasError = true;
                    }
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_BLOCK_WITHOUT_CONTEXT:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    
                    try {
                        switch (refNode.getType()) {
                        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR:
                        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR:
                        {
                            builder.addFunctionOrClassMethodReference(makeCALDocFunctionOrClassMethodCrossReference(refNode));
                            break;
                        }
                        
                        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS:
                        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS:
                        {
                            Pair<CategoryForCALDocConsNameWithoutContextCrossReference, CALDocComment.Reference> pair = buildCALDocConsNameWithoutContextCrossReference(refNode, false);
                            
                            CategoryForCALDocConsNameWithoutContextCrossReference category = pair.fst();
                            CALDocComment.Reference reference = pair.snd();
                            
                            if (category == CategoryForCALDocConsNameWithoutContextCrossReference.MODULE_NAME) {
                                builder.addModuleReference((CALDocComment.ModuleReference)reference);
                                
                            } else if (category == CategoryForCALDocConsNameWithoutContextCrossReference.DATA_CONS_NAME) {
                                builder.addDataConstructorReference((CALDocComment.ScopedEntityReference)reference);
                                
                            } else if (category == CategoryForCALDocConsNameWithoutContextCrossReference.TYPE_CONS_NAME) {
                                builder.addTypeConstructorReference((CALDocComment.ScopedEntityReference)reference);
                                
                            } else if (category == CategoryForCALDocConsNameWithoutContextCrossReference.TYPE_CLASS_NAME) {
                                builder.addTypeClassReference((CALDocComment.ScopedEntityReference)reference);
                                
                            } else {
                                throw new IllegalStateException();
                            }
                            break;
                        }
                            
                        default:
                            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
                        }
                    } catch (InvalidCALDocException e) {
                        hasError = true;
                    }
                }
                break;
            }
            
            default:
            {
                seeBlockNode.unexpectedParseTreeNode();                
                hasError = true;
                break;
            }            
        }
        
        return !hasError;
    }

    /**
     * Creates a CALDocComment.ScopedEntityReference from a parse tree node representing
     * a function name appearing in a CALDoc "@see"/"@link" block.
     * 
     * @param refNode the ParseTreeNode representing a function name appearing in a CALDoc "@see"/"@link" block.
     * @return a new CALDocComment.ScopedEntityReference object representing the cross reference.
     */
    private CALDocComment.ScopedEntityReference makeCALDocFunctionOrClassMethodCrossReference(ParseTreeNode refNode) throws InvalidCALDocException {
        refNode.verifyType(
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR,
            CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR);
        
        ParseTreeNode nameNode = refNode.firstChild();
        nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
        
        if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR) {
            
            FunctionalAgent entity = retrieveQualifiedVar(nameNode);
            if (entity == null) {
                String displayName = getQualifiedNameDisplayString(nameNode);
                // TypeChecker: unknown function or variable {displayName}.
                compiler.logMessage(new CompilerMessage(nameNode.getChild(1), new MessageKind.Error.UnknownFunctionOrVariable(displayName)));
                
                throw new InvalidCALDocException();
                
            } else {
                ParseTreeNode moduleNameNode = nameNode.firstChild();
                String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(moduleNameNode);
                
                return new CALDocComment.ScopedEntityReference(entity.getName(), true, moduleNameInSource);
            }
        } else {
            QualifiedName name = buildCALDocUncheckedSeeReferenceFunctionOrClassMethodName(nameNode);
            
            // the previous method call would have set the data field of the moduleNameNode appropriately if the name is unqualified
            ParseTreeNode moduleNameNode = nameNode.firstChild();
            String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(moduleNameNode);
            
            return new CALDocComment.ScopedEntityReference(name, false, moduleNameInSource);
        }
    }

    /**
     * Creates a CALDoc.ModuleReference from a parse tree node representing
     * a module name appearing in a CALDoc "@see"/"@link" block.
     * 
     * @param refNode the ParseTreeNode representing a module name appearing in a CALDoc "@see"/"@link" block.
     * @return a new CALDocComment.ModuleReference object representing the cross reference.
     */
    private CALDocComment.ModuleReference makeCALDocModuleCrossReference(ParseTreeNode refNode) throws InvalidCALDocException {
        refNode.verifyType(
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_MODULE_NAME,
            CALTreeParserTokenTypes.CALDOC_CHECKED_MODULE_NAME);
        
        ParseTreeNode nameNode = refNode.firstChild();
        nameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        
        ModuleName moduleName = resolveModuleName(nameNode);
        String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(nameNode);
        
        if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_MODULE_NAME) {
            
            if (!moduleName.equals(currentModuleTypeInfo.getModuleName())) {
                ModuleTypeInfo moduleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);
                if (moduleTypeInfo == null) {
                    compiler.logMessage(new CompilerMessage(nameNode, new MessageKind.Error.ModuleHasNotBeenImported(moduleName, currentModuleTypeInfo.getModuleName())));
                    
                    throw new InvalidCALDocException();
                } else {
                    // Now we need to run a deprecation check on the checked module reference
                    SourceRange sourceRange = CALCompiler.getSourceRangeForCompilerMessage(nameNode, currentModuleTypeInfo.getModuleName());
                    compiler.checkResolvedModuleReference(moduleName, sourceRange);
                    
                    return new CALDocComment.ModuleReference(moduleName, true, moduleNameInSource);
                }
            } else {
                // Now we need to run a deprecation check on the checked module reference
                SourceRange sourceRange = CALCompiler.getSourceRangeForCompilerMessage(nameNode, currentModuleTypeInfo.getModuleName());
                compiler.checkResolvedModuleReference(moduleName, sourceRange);
                
                return new CALDocComment.ModuleReference(moduleName, true, moduleNameInSource);
            }
        } else {
            return new CALDocComment.ModuleReference(moduleName, false, moduleNameInSource);
        }
    }

    /**
     * Creates a CALDocComment.ScopedEntityReference from a parse tree node representing
     * a data constructor name appearing in a CALDoc "@see"/"@link" block.
     * 
     * @param refNode the ParseTreeNode representing a data constructor name appearing in a CALDoc "@see"/"@link" block.
     * @return a new CALDocComment.ScopedEntityReference object representing the cross reference.
     */
    private CALDocComment.ScopedEntityReference makeCALDocDataConsCrossReference(ParseTreeNode refNode) throws InvalidCALDocException {
        refNode.verifyType(
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
            CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
        
        ParseTreeNode nameNode = refNode.firstChild();
        nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS) {
            
            FunctionalAgent entity = retrieveQualifiedDataConstructor(nameNode);
            if (entity == null) {
                String displayName = getQualifiedNameDisplayString(nameNode);
                // TypeChecker: unknown data constructor {displayName}.
                compiler.logMessage(new CompilerMessage(nameNode.getChild(1), new MessageKind.Error.UnknownDataConstructor(displayName)));
                
                throw new InvalidCALDocException();
            } else {
                ParseTreeNode moduleNameNode = nameNode.firstChild();
                String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(moduleNameNode);
                
                return new CALDocComment.ScopedEntityReference(entity.getName(), true, moduleNameInSource);
            }
        } else {
            QualifiedName name = buildCALDocUncheckedSeeReferenceDataConsName(nameNode);

            // the previous method call would have set the data field of the moduleNameNode appropriately if the name is unqualified
            ParseTreeNode moduleNameNode = nameNode.firstChild();
            String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(moduleNameNode);
            
            return new CALDocComment.ScopedEntityReference(name, false, moduleNameInSource);
        }
    }

    /**
     * Creates a CALDocComment.ScopedEntityReference from a parse tree node representing
     * a type constructor name appearing in a CALDoc "@see"/"@link" block.
     * 
     * @param refNode the ParseTreeNode representing a type constructor name appearing in a CALDoc "@see"/"@link" block.
     * @return a new CALDocComment.ScopedEntityReference object representing the cross reference.
     */
    private CALDocComment.ScopedEntityReference makeCALDocTypeConsCrossReference(ParseTreeNode refNode) throws InvalidCALDocException {
        refNode.verifyType(
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
            CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
        
        ParseTreeNode nameNode = refNode.firstChild();
        nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS) {
            
            TypeConstructor typeCons = DataDeclarationChecker.resolveTypeConsName(nameNode, currentModuleTypeInfo, compiler);
            if (typeCons == null) {
                // the previous call to resolveTypeConsName would have logged an error message
                // to the logger already.
                
                throw new InvalidCALDocException();
            } else {
                ParseTreeNode moduleNameNode = nameNode.firstChild();
                String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(moduleNameNode);
                
                return new CALDocComment.ScopedEntityReference(typeCons.getName(), true, moduleNameInSource);
            }
        } else {
            QualifiedName name = buildCALDocUncheckedSeeReferenceTypeConsName(nameNode);
            
            // the previous method call would have set the data field of the moduleNameNode appropriately if the name is unqualified
            ParseTreeNode moduleNameNode = nameNode.firstChild();
            String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(moduleNameNode);
            
            return new CALDocComment.ScopedEntityReference(name, false, moduleNameInSource);
        }
    }

    /**
     * Creates a CALDocComment.ScopedEntityReference from a parse tree node representing
     * a type class name appearing in a CALDoc "@see"/"@link" block.
     * 
     * @param refNode the ParseTreeNode representing a type class name appearing in a CALDoc "@see"/"@link" block.
     * @return a new CALDocComment.ScopedEntityReference object representing the cross reference.
     */
    private CALDocComment.ScopedEntityReference makeCALDocTypeClassCrossReference(ParseTreeNode refNode) throws InvalidCALDocException {
        refNode.verifyType(
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
            CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
        
        ParseTreeNode nameNode = refNode.firstChild();
        nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS) {
            
            TypeClass typeClass = TypeClassChecker.resolveClassName(nameNode, currentModuleTypeInfo, compiler);
            if (typeClass == null) {
                String displayName = getQualifiedNameDisplayString(nameNode);
                compiler.logMessage(new CompilerMessage(nameNode, new MessageKind.Error.UndefinedTypeClass(displayName)));
                
                throw new InvalidCALDocException();
            } else {
                ParseTreeNode moduleNameNode = nameNode.firstChild();
                String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(moduleNameNode);
                
                return new CALDocComment.ScopedEntityReference(typeClass.getName(), true, moduleNameInSource);
            }
        } else {
            QualifiedName name = buildCALDocUncheckedSeeReferenceTypeClassName(nameNode);
            
            // the previous method call would have set the data field of the moduleNameNode appropriately if the name is unqualified
            ParseTreeNode moduleNameNode = nameNode.firstChild();
            String moduleNameInSource = ModuleNameUtilities.getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(moduleNameNode);
            
            return new CALDocComment.ScopedEntityReference(name, false, moduleNameInSource);
        }
    }

    /**
     * Retrieves the DataConstructor named by the specified qualified data constructor
     * reference.
     * 
     * @param qualifiedDataConsNode
     *            the qualified data constructor reference.
     * @return the DataConstructor named by the specified qualified data constructor
     *         reference, or null if there is no such data constructor.
     */
    private DataConstructor retrieveQualifiedDataConstructor(ParseTreeNode qualifiedDataConsNode) {

        qualifiedDataConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        QualifiedName dataConsName = qualifiedDataConsNode.toQualifiedName();

        ModuleName moduleName = dataConsName.getModuleName();

        String unqualifiedDataConsName = dataConsName.getUnqualifiedName();
       
        if (moduleName.equals(currentModuleTypeInfo.getModuleName())) {
            return currentModuleTypeInfo.getDataConstructor(unqualifiedDataConsName);
        }

        ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);
        if (importedModuleTypeInfo == null) {
            return null;
        }
        
        DataConstructor dataCons = importedModuleTypeInfo.getDataConstructor(unqualifiedDataConsName);
        if (dataCons == null) {
            return null;
        }
        
        if (currentModuleTypeInfo.isEntityVisible(dataCons)) {
            return dataCons;
        }
            
        return null;              
    }
    
    /**
     * Retrieves the FunctionalAgent named by the specified qualified var reference.
     * 
     * @param qualifiedVarNode
     *            the qualified var reference.
     * @return the FunctionalAgent named by the specified qualified var reference, or
     *         null if there is no such function or class method.
     */
    private FunctionalAgent retrieveQualifiedVar(ParseTreeNode qualifiedVarNode) {

        qualifiedVarNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
        QualifiedName varName = qualifiedVarNode.toQualifiedName();

        ModuleName moduleName = varName.getModuleName();

        String unqualifiedName = varName.getUnqualifiedName();    
        
        if (moduleName.equals(currentModuleTypeInfo.getModuleName())) {
            return currentModuleTypeInfo.getFunctionOrClassMethod(unqualifiedName);
        }

        ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);
        if (importedModuleTypeInfo == null) {
            return null;
        }
        
        FunctionalAgent entity = importedModuleTypeInfo.getFunctionOrClassMethod(unqualifiedName);
        if (entity == null) {
            return null;
        }
        
        if (currentModuleTypeInfo.isEntityVisible(entity)) {
            return entity;
        }
            
        return null;
    }
    
    /**
     * Returns a string representation of the qualified name as represented by the parse tree.
     * @param nameNode the parse tree representing a qualified name.
     * @return the string representation of the qualified name.
     */
    private String getQualifiedNameDisplayString(ParseTreeNode nameNode) {
        return nameNode.toQualifiedName().getUnqualifiedName();
    }

    /**
     * Resolves the module name represented by the given parse tree, and modifies it to represent the resolved name
     * (if it differs from the original name). This differs from {@link ModuleNameUtilities#resolveMaybeModuleNameInParseTree}
     * in that an InvalidCALDocException is thrown if the name is ambiguous.
     * 
     * @param moduleNameNode the root of the parse tree representing a module name.
     * @return the resolved name for the given module name.
     * @throws InvalidCALDocException when the given module name cannot be unambiguously resolved.
     */
    private ModuleName resolveModuleName(ParseTreeNode moduleNameNode) throws InvalidCALDocException {
        ModuleName moduleName = ModuleNameUtilities.getModuleNameFromParseTree(moduleNameNode);
        
        ModuleNameResolver.ResolutionResult resolution = currentModuleTypeInfo.getModuleNameResolver().resolve(moduleName);
        
        if (!resolution.isResolvedModuleNameEqualToOriginalModuleName()) {
            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, resolution.getResolvedModuleName());
        }
        
        if (resolution.isAmbiguous()) {
            compiler.logMessage(new CompilerMessage(moduleNameNode, new MessageKind.Error.AmbiguousPartiallyQualifiedFormModuleName(moduleName, resolution.getPotentialMatches())));
            throw new InvalidCALDocException();
        }
        
        return resolution.getResolvedModuleName();
    }
}
