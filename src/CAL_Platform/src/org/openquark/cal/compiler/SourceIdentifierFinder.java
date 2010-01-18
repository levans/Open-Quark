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
 * SourceIdentifierFinder.java
 * Creation date: (February 23, 2004)
 * By: Iulian Radu
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessage.AbortCompilation;
import org.openquark.cal.util.ArrayStack;


/**
 * Used for finding identifiers in module sources, global functions, lambda definitions, 
 * local function definitions, import and instance declarations for the purposes of various
 * pre-compilation services, such as auto-qualification for code gems and ICE command line expressions,
 * and renaming.
 * 
 * This superclass is responsible for traversing the expression parse trees and invoking its abstract 
 * methods on each identifier reference found. The class also keeps track of any local variable
 * bindings, and identifiers bound to modules via "import using" statements.
 * 
 * Any errors encountered while performing visitation will be logged to a message logger specific to this finder.
 * This logger is available by calling getLogger().
 * 
 * Note that the visitation code is largely copied from FreeVariableFinder. This code has more of 
 * a tendency to get out of date since it is only used for code gems and ICE command line expressions.
 * So, if there is a bug, please update from the corresponding places in FreeVariableFinder.
 * 
 * <P>Creation date: (February 23, 2004)
 * 
 * @param <I> identifier type. The type of the identifier being found e.g. SourceIdentifier, SourceModification
 * @param <V> variable type. The type of the variables being places on the bound variables stack e.g. String, SourceIdentifier
 * 
 * @author Iulian Radu
 */
abstract class SourceIdentifierFinder<I, V> {

    /** Logger for parse error messages */
    private final CompilerMessageLogger logger;
    
    /** Name of the module containing the expressions checked */
    private ModuleName currentModuleName;
    
    /** A Set (of ModuleNames) of the names of modules imported by the current module. */
    private final Set<ModuleName> importedModuleNames = new HashSet<ModuleName>();
    
    /** The module name resolver corresponding to the module containing the expressions to be checked. */
    private ModuleNameResolver moduleNameResolver;
    
    // Mappings (String entity name -> ModuleName module name) of entities imported from 
    // other modules via "import using" statements
    
    private final Map<String, ModuleName> usingFunctionOrClassMethodMap = new HashMap<String, ModuleName>();     
    private final Map<String, ModuleName> usingDataConstructorMap = new HashMap<String, ModuleName>();    
    private final Map<String, ModuleName> usingTypeConstructorMap = new HashMap<String, ModuleName>();      
    private final Map<String, ModuleName> usingTypeClassMap       = new HashMap<String, ModuleName>();
    
    /**
     * Constructor for a SourceIdentifierFinder.
     */
    SourceIdentifierFinder() {
        // Abort finding on fatal.  TODO: Note in javadoc.
        this.logger = new MessageLogger(true);
    }
    
    ModuleName getCurrentModuleName() {
        return currentModuleName;
    }
    
    void setCurrentModuleName(ModuleName currentModuleName) {
        this.currentModuleName = currentModuleName;        
    }
       
    /**
     * @return the logger used to log errors during visitation.
     */
    CompilerMessageLogger getLogger() {
        return logger;
    }
    
    /** @return true if the given name is the name of an imported module. */
    boolean isModuleImported(ModuleName moduleName) {
        return importedModuleNames.contains(moduleName);
    }
    
    ModuleName getModuleForImportedFunctionOrClassMethod(String unqualifiedName) {
        return usingFunctionOrClassMethodMap.get(unqualifiedName);
    }
    
    ModuleName getModuleForImportedDataConstructor(String unqualifiedName) {
        return usingDataConstructorMap.get(unqualifiedName);
    }
    
    ModuleName getModuleForImportedTypeConstructor(String unqualifiedName) {
        return usingTypeConstructorMap.get(unqualifiedName);
    }
    
    ModuleName getModuleForImportedTypeClass(String unqualifiedName) {
        return usingTypeClassMap.get(unqualifiedName);
    }
    
    /**
     * @return the module name resolver corresponding to the module containing the expressions to be checked.
     */
    ModuleNameResolver getModuleNameResolver() {
        return moduleNameResolver;
    }
    
    /**
     * Sets the module name resolver corresponding to the module containing the expressions to be checked.
     * @param moduleNameResolver the module name resolver.
     */
    void setModuleNameResolver(ModuleNameResolver moduleNameResolver) {
        this.moduleNameResolver = moduleNameResolver;
    }

    /**
     * Traverse the passed code expression and return the list of identifiers that occur within it. 
     * The list is ordered as dictated by the getIdentifierListComparator().
     * 
     * This method is intended to be the entry point for traversing parsed code expressions.
     * 
     * @param exprNode ParseTree node defining the code expression
     * @param moduleName name of the module this expression belongs to
     * @param moduleNameResolver the module name resolver for the module to which this expression belongs.
     * @return List of identifiers encountered in the expression. 
     */
    List<I> findIdentifiersInCodeExpression(ParseTreeNode exprNode, ModuleName moduleName, ModuleNameResolver moduleNameResolver) {

        ArrayStack<V> boundVariablesStack = ArrayStack.make();
        List<I> identifierList = new ArrayList<I>();
        setCurrentModuleName(moduleName);
        setModuleNameResolver(moduleNameResolver);
        
        findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);
        
        //it may not be necessary to sort, but it is safe
        Collections.sort(identifierList, getIdentifierListComparator());
        
        return identifierList;
    }
    
    /**
     * Traverse the parsed module source and return the list of identifiers that occur within it. 
     * The list is ordered as dictated by the getIdentifierListComparator().
     * 
     * This method is intended to be the entry point for traversing parsed module sources.
     * 
     * @param moduleDefNode ParseTree node defining the code expression
     * @return List of identifiers encountered in the expression. 
     */
    List<I> findIdentifiersInModule(ParseTreeNode moduleDefNode) {

        List<I> identifierList = new ArrayList<I>();
        setCurrentModuleName(ModuleNameUtilities.getModuleNameFromParseTree(moduleDefNode.getChild(1)));
        
        findIdentifiersInModule(identifierList, moduleDefNode);
        
        // The retrieved identifiers most likely do not appear in order, since parse
        // trees within the module are inspected by category
        Collections.sort(identifierList, getIdentifierListComparator());
        
        return identifierList;
    }
    
    private void findIdentifiersInModule(List<I> identifierList, ParseTreeNode moduleDefnNode) {
        moduleDefnNode.verifyType(CALTreeParserTokenTypes.MODULE_DEFN);

        ParseTreeNode optionalCALDocNode = moduleDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode moduleNameNode = optionalCALDocNode.nextSibling();
        visitModuleNameNode(identifierList, moduleNameNode);
        
        // Check 'import' declarations
        
        ParseTreeNode importDeclarationListNode = moduleNameNode.nextSibling();
        findIdentifiersInImportDeclarations(identifierList, importDeclarationListNode);
        
        //skip over friends. 
        ParseTreeNode friendDeclarationListNode = importDeclarationListNode.nextSibling();
        friendDeclarationListNode.verifyType(CALTreeParserTokenTypes.FRIEND_DECLARATION_LIST);
        
        // Categorize parse trees found in this module for easy access
        
        ParseTreeNode outerDefnListNode = friendDeclarationListNode.nextSibling();
        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);
        ModuleLevelParseTrees moduleLevelParseTrees = new ModuleLevelParseTrees(moduleDefnNode, outerDefnListNode);
        
        // Check SC declarations and definitions
                        
        for (final ParseTreeNode node : moduleLevelParseTrees.getFunctionTypeDeclarationNodes()) {
            findIdentifiersInFunctionDeclaration(identifierList, node);
        }
               
        for (final ParseTreeNode node : moduleLevelParseTrees.getFunctionDefnNodes()) {
            findIdentifiersInFunctionDefinition(identifierList, node);
        }
        
        // Check type class definitions
             
        for (final ParseTreeNode node : moduleLevelParseTrees.getTypeClassDefnNodes()) {
            findIdentifiersInTypeClassDefinition(identifierList, node);
        }
        
        // Check class instance declarations
                
        for (final ParseTreeNode node : moduleLevelParseTrees.getInstanceDefnNodes()) {
            findIdentifiersInInstanceDefinition(identifierList, node);
        }
        
        // Check data declarations       
        for (final ParseTreeNode node : moduleLevelParseTrees.getDataDeclarationNodes()) {
            findIdentifiersInDataDeclaration(identifierList, node);
        }
        
        // Check foreign SC declarations
               
        for (final ParseTreeNode node : moduleLevelParseTrees.getForeignFunctionDefnNodes()) {
            findIdentifiersInForeignFunctionDeclaration(identifierList, node);
        }
                
        for (final ParseTreeNode node : moduleLevelParseTrees.getForeignDataDeclarationNodes()) {
            findIdentifiersInForeignDataDeclaration(identifierList, node);
        }
        
        return;
    }
    
    /**
     * Find the identifiers occurring in an expression Node. 
     * 
     * For example in: f x = y + x + (let y = 2 in y) the first occurrence of y
     * is the only identifier returned by the AllIdentifierFinder.
     * 
     * @param identifierList
     *            The identifiers encountered while traversing the parse
     *            tree.
     * @param boundVariablesStack
     *            The function is not dependent on bound variables
     *            appearing in its definition. These are its argument
     *            variables, or variables introduced in internal let
     *            declarations or binder variables in a lambda declaration.
     *            This stack varies depending on where we are in the
     *            definition. The same variable name can occur more than once
     *            because of scoping.
     * @param parseTree
     *            expression parse tree 
     */
    void findIdentifiersInExpr(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode parseTree) {

        int nodeType = parseTree.getType();
        
        switch (nodeType) {

            case CALTreeParserTokenTypes.LITERAL_let :
            {
                findIdentifiersInLet(identifierList, boundVariablesStack, parseTree);
                return;
            }

            case CALTreeParserTokenTypes.LAMBDA_DEFN :
            {
                ParseTreeNode paramListNode = parseTree.firstChild();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

                findIdentifiersInLambda(identifierList, boundVariablesStack, paramListNode, paramListNode.nextSibling());
                return;
            }

            case CALTreeParserTokenTypes.LITERAL_case :
            {
                findIdentifiersInCase(identifierList, boundVariablesStack, parseTree);
                return;
            }

            case CALTreeParserTokenTypes.LITERAL_if :
            case CALTreeParserTokenTypes.BARBAR :
            case CALTreeParserTokenTypes.AMPERSANDAMPERSAND :
            case CALTreeParserTokenTypes.PLUSPLUS :
            case CALTreeParserTokenTypes.LESS_THAN :
            case CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS :
            case CALTreeParserTokenTypes.EQUALSEQUALS :
            case CALTreeParserTokenTypes.NOT_EQUALS :
            case CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS :
            case CALTreeParserTokenTypes.GREATER_THAN :
            case CALTreeParserTokenTypes.PLUS :
            case CALTreeParserTokenTypes.MINUS :
            case CALTreeParserTokenTypes.ASTERISK :
            case CALTreeParserTokenTypes.SOLIDUS :
            case CALTreeParserTokenTypes.PERCENT:
            case CALTreeParserTokenTypes.COLON :
            case CALTreeParserTokenTypes.UNARY_MINUS:
            case CALTreeParserTokenTypes.POUND:
            case CALTreeParserTokenTypes.DOLLAR:
            case CALTreeParserTokenTypes.BACKQUOTE:
            {
                findIdentifiersInChildExpressions(identifierList, boundVariablesStack, parseTree);
                return;
            }

            case CALTreeParserTokenTypes.APPLICATION :
            {
                for (final ParseTreeNode exprNode : parseTree) {

                    findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);
                }

                return;
            }

            // function names, class method names and variables
            case CALTreeParserTokenTypes.QUALIFIED_VAR :
            {
                visitFunctionOrClassMethodNameNode(identifierList, boundVariablesStack, parseTree);
                return;
            }

            //data constructors
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            {
                visitDataConsNameNode(identifierList, parseTree);
                return;
            }

            // literals
            case CALTreeParserTokenTypes.INTEGER_LITERAL :
            case CALTreeParserTokenTypes.FLOAT_LITERAL :
            case CALTreeParserTokenTypes.CHAR_LITERAL :
            case CALTreeParserTokenTypes.STRING_LITERAL :
                return;

            //A parenthesized expression, a tuple or the trivial type
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
            {
                findIdentifiersInChildExpressions(identifierList, boundVariablesStack, parseTree);
                return;
            }

            //A list data value
            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
                findIdentifiersInChildExpressions(identifierList, boundVariablesStack, parseTree);
                return;

            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR:
            {
                ParseTreeNode baseRecordNode = parseTree.firstChild();
                baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);
                ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();
                if (baseRecordExprNode != null) {
                    findIdentifiersInExpr(identifierList, boundVariablesStack, baseRecordExprNode);            
                }
                
                ParseTreeNode fieldModificationListNode = baseRecordNode.nextSibling();
                fieldModificationListNode.verifyType(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST);
                
                for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
                    
                    fieldModificationNode.verifyType(CALTreeParserTokenTypes.FIELD_EXTENSION,
                        CALTreeParserTokenTypes.FIELD_VALUE_UPDATE);
                                             
                    findIdentifiersInExpr(identifierList, boundVariablesStack, fieldModificationNode.getChild(1));         
                }
                
                return;
            }
            
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD:
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);
                return;
            }
            
            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD:
            {
                // Simulate a case expr where
                // expr.DCName.fieldName    is converted to   case expr of DCName {fieldName} -> fieldName;
                
                // The expression.
                ParseTreeNode exprNode = parseTree.firstChild();
                findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);
                
                // The name of the DC.
                ParseTreeNode dcNameNode = exprNode.nextSibling();
                dcNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                visitDataConsNameNode(identifierList, dcNameNode);
                
                // The name of the field.
                ParseTreeNode fieldNameNode = dcNameNode.nextSibling();
                
                // If it's a textual (not an ordinal) field, simulate the case as above.
                if (fieldNameNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
                    
                    String calSourceForm = fieldNameNode.getText();
                    SourcePosition fieldNameSourcePosition = fieldNameNode.getSourcePosition();
                    ParseTreeNode patternVarNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, calSourceForm, fieldNameSourcePosition);
                    
                    visitLocalVarDeclarationNode(identifierList, boundVariablesStack, patternVarNode, false);
                    
                    // varName is now a bound variable for the body of the lambda
                    pushLocalVar(boundVariablesStack, patternVarNode, identifierList);
                    
                    // The bound expr node is a node of type QUALIFIED_VAR with the same name as the pattern var (~punning).
                    // ie. moduleName is unspecified, unqualified name is the pattern var name.
                    ParseTreeNode qualifiedVarNode = ParseTreeNode.makeUnqualifiedVarNode(calSourceForm, fieldNameSourcePosition);
                    
                    // Find in the expr node..
                    findIdentifiersInExpr(identifierList, boundVariablesStack, qualifiedVarNode);
                    
                    // pop varName..
                    boundVariablesStack.pop();
                }

                return;
            }
            
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);
                
                ParseTreeNode typeSignatureNode = exprNode.nextSibling();
                typeSignatureNode.verifyType(CALTreeParserTokenTypes.TYPE_SIGNATURE);
                
                ParseTreeNode contextListNode = typeSignatureNode.firstChild();
                ParseTreeNode declarationNode = contextListNode.nextSibling();
                
                findIdentifiersInDeclaredTypeContext(identifierList, contextListNode);
                findIdentifiersInDeclaredTypeExpr(identifierList, declarationNode);                
                                
                return;                
            }
                
            default :
            {                
                parseTree.unexpectedParseTreeNode();
                return;
            }
        }
    }   
    
    /**
     * A helper function to find identifiers in case expressions.
     * 
     * @param identifierList 
     * @param boundVariablesStack 
     * @param parseTree 
     *  
     */
    void findIdentifiersInCase(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode parseTree) {

        ParseTreeNode exprNode = parseTree.firstChild();
        findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);

        ParseTreeNode altListNode = exprNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);

        for (final ParseTreeNode altNode : altListNode) {

            altNode.verifyType(CALTreeParserTokenTypes.ALT);
            ParseTreeNode patternNode = altNode.firstChild();
            int nodeKind = patternNode.getType();
            switch (nodeKind) {

                case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR :
                {
                    ParseTreeNode dataConsNameListNode = patternNode.firstChild();
                    ParseTreeNode argBindingsNode = dataConsNameListNode.nextSibling();
                    
                    for (final ParseTreeNode qualifiedConsNode : dataConsNameListNode) {
                        
                        visitDataConsNameNode(identifierList, qualifiedConsNode);
                    }
                    
                    switch (argBindingsNode.getType()) {
                        
                        case CALTreeParserTokenTypes.PATTERN_VAR_LIST :
                        {
                            // positional notation
                            findIdentifiersInLambda(identifierList, boundVariablesStack, argBindingsNode, patternNode.nextSibling());
                            break;
                        }
                        case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST :
                        {
                            // matching notation
                            findIdentifiersInFieldBindingCase(identifierList, boundVariablesStack, argBindingsNode, patternNode.nextSibling(), null);
                            break;
                        }
                        default :
                        {                            
                            patternNode.unexpectedParseTreeNode();
                            return;
                        }
                    }
                    break;
                }

                case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :         // null list constructor []
                case CALTreeParserTokenTypes.VIRTUAL_UNIT_DATA_CONSTRUCTOR: // Unit constructor ()
                case CALTreeParserTokenTypes.UNDERSCORE :               // _
                case CALTreeParserTokenTypes.INT_PATTERN :
                case CALTreeParserTokenTypes.CHAR_PATTERN :
                {
                    findIdentifiersInLambda(identifierList, boundVariablesStack, null, patternNode.nextSibling());
                    break;
                }
                
                case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
                case CALTreeParserTokenTypes.COLON :
                {
                    findIdentifiersInLambda(identifierList, boundVariablesStack, patternNode, patternNode.nextSibling());
                    break;
                }
                    
                case CALTreeParserTokenTypes.RECORD_PATTERN:
                {
                    findIdentifiersInRecordCase(identifierList, boundVariablesStack, patternNode);
                    break;                       
                }

                default :
                {                   
                    patternNode.unexpectedParseTreeNode();
                    return;
                }
            }
        }
        
        return;
    }

    /**
     * Finds identifiers in case expression containing a record pattern
     * 
     * @param identifierList
     * @param boundVariablesStack
     * @param patternNode
     */
    void findIdentifiersInRecordCase(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode patternNode) {
        ParseTreeNode baseRecordPatternNode = patternNode.firstChild();
        baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
        
        // Find the name of the base record variable
        ParseTreeNode baseRecordPatternVarNode = baseRecordPatternNode.firstChild();
        ParseTreeNode basePatternVarNameNode = null;
        if (baseRecordPatternVarNode != null) 
        {                    
            switch (baseRecordPatternVarNode.getType())
            {
                case CALTreeParserTokenTypes.VAR_ID:
                    basePatternVarNameNode = baseRecordPatternVarNode;
                    break;
                    
                case CALTreeParserTokenTypes.UNDERSCORE:                              
                    break;
                    
                default:
                {                                                
                    baseRecordPatternVarNode.unexpectedParseTreeNode();
                    return;
                }                            
            }
        }                                                           
       
        ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();
        
        findIdentifiersInFieldBindingCase(identifierList, boundVariablesStack, fieldBindingVarAssignmentListNode, patternNode.nextSibling(), basePatternVarNameNode);
    }

    /**
     * Finds identifiers in a case expression containing a field binding pattern.
     * 
     * @param identifierList
     * @param boundVariablesStack
     * @param fieldBindingVarAssignmentListNode
     * @param boundExprNode
     * @param basePatternVarNameNode if non-null, the parse tree node for the named variable forming the base pattern for the field binding.
     */
    void findIdentifiersInFieldBindingCase(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode fieldBindingVarAssignmentListNode, ParseTreeNode boundExprNode, ParseTreeNode basePatternVarNameNode) {
        // Set pattern variables for punned fields
        unpunPunnedFields(fieldBindingVarAssignmentListNode);

        int nVars;
        if (basePatternVarNameNode != null) {
            nVars = 1;
            visitLocalVarDeclarationNode(identifierList, boundVariablesStack, basePatternVarNameNode, false);
            pushLocalVar(boundVariablesStack, basePatternVarNameNode, identifierList);                                                                       
        } else {
            nVars = 0;
        }
        
        for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {
            
            ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();  
            ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
            
            switch (patternVarNode.getType())
            {
                
                case CALTreeParserTokenTypes.VAR_ID:   
                {                     
                    ++nVars;
                    visitLocalVarDeclarationNode(identifierList, boundVariablesStack, patternVarNode, false);
                    pushLocalVar(boundVariablesStack, patternVarNode, identifierList);
                    break;
                }
                
                
                case CALTreeParserTokenTypes.UNDERSCORE:
                    break;
                    
                default:
                {                  
                    patternVarNode.unexpectedParseTreeNode();
                    return;                                                              
                }
            }                                                                                          
        }
        
        findIdentifiersInExpr(identifierList, boundVariablesStack, boundExprNode);
        boundVariablesStack.popN(nVars);
    }

    /**
     * Patch up the parse tree so that in subsequent analysis we can assume that punning doesn't occur.
     * 
     * In the case of textual field names, punning means: fieldName ---> fieldName = fieldName
     * In the case of numeric field names, punning means: fieldName ---> fieldName = _
     * This is because something like #2 is a valid numeric field name but not a valid CAL variable name.
     * 
     * @param fieldBindingVarAssignmentListNode the parse tree node for the list of field binding var assignments.
     */
    static void unpunPunnedFields(ParseTreeNode fieldBindingVarAssignmentListNode) {
        fieldBindingVarAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);
        for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {
            
            fieldBindingVarAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT);
            ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
            fieldNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
            
            ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
            if (patternVarNode == null) {
                
                if (fieldNameNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
                    //textual field names
                    patternVarNode = new ParseTreeNode();
                    patternVarNode.copyContentsFrom(fieldNameNode);
                } else {
                    //numeric field names
                    patternVarNode = new ParseTreeNode(CALTreeParserTokenTypes.UNDERSCORE, "_");
                }
                
                fieldNameNode.setNextSibling(patternVarNode);
            }                                                                                   
        }                                                                               
    }
    
    /**
     * A helper function that finds the names in each of the child expressions of parseTree.
     * 
     * @param identifierList
     * @param boundVariablesStack
     * @param parseTree
     * 
     */
    void findIdentifiersInChildExpressions(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode parseTree) {

        for (final ParseTreeNode exprNode : parseTree) {

            findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);
        }
        return;
    }

    /**
     * Helper function to find names in lambda expressions.
     * 
     * @param identifierList
     * @param boundVariablesStack
     * @param patternVarListNode if non-null, the parent node of the parse tree nodes for pattern vars.
     * @param boundExprNode 
     *  
     */
    final void findIdentifiersInLambda(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode patternVarListNode, ParseTreeNode boundExprNode) {
        findIdentifiersInLambda(identifierList, boundVariablesStack, patternVarListNode, boundExprNode, null);
    }
    
    /**
     * Helper function to find names in lambda expressions and local function definitions.
     * 
     * @param identifierList
     * @param boundVariablesStack
     * @param patternVarListNode if non-null, the parent node of the parse tree nodes for pattern vars.
     * @param boundExprNode
     * @param optionalCALDocNodeForLetDefn
     */
    void findIdentifiersInLambda(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode patternVarListNode, ParseTreeNode boundExprNode, ParseTreeNode optionalCALDocNodeForLetDefn) {

        int nVars = 0;
        
        if (patternVarListNode != null) {
            
            for (final ParseTreeNode patternVarNode : patternVarListNode) {
                
                switch (patternVarNode.getType()) {
                    
                    case CALTreeParserTokenTypes.VAR_ID:
                    case CALTreeParserTokenTypes.LAZY_PARAM:
                    case CALTreeParserTokenTypes.STRICT_PARAM:                
                    {
                        ++nVars;
                        
                        // varName is now a bound variable for the body of the
                        // lambda
                        visitLocalVarDeclarationNode(identifierList, boundVariablesStack, patternVarNode, false);
                        pushLocalVar(boundVariablesStack, patternVarNode, identifierList);
                        break;
                    }
                    
                    case CALTreeParserTokenTypes.UNDERSCORE :
                        break;
                        
                    default :
                    {
                        // Unexpected type
                        boundVariablesStack.popN(nVars);                        
                        patternVarNode.unexpectedParseTreeNode();
                        return;
                    }
                }
            }
        }

        // if there is a CALDoc comment that needs to be checked with the parameters in scope, do it now
        if (optionalCALDocNodeForLetDefn != null) {
            findIdentifiersInCALDocComment(identifierList, boundVariablesStack, optionalCALDocNodeForLetDefn);
        }
        
        findIdentifiersInExpr(identifierList, boundVariablesStack, boundExprNode);
        
        boundVariablesStack.popN(nVars);
        return;
    }

    /**
     * A helper function for finding identifiers in Let expressions.
     * 
     * @param identifierList
     * @param boundVariablesStack
     * @param parseTree
     */
    void findIdentifiersInLet(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode parseTree) {

        ParseTreeNode defnListNode = parseTree.firstChild();
        defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);
        int nLocalFunctions = 0;
        
        List<ParseTreeNode> typeDeclNodes = new ArrayList<ParseTreeNode>();
        List<ParseTreeNode> letDefnAndLocalPatternMatchDeclNodes = new ArrayList<ParseTreeNode>();
        
        // First, collect all the optional CALDoc nodes (for later checking)
        Map/*String, ParseTreeNode*/<String, ParseTreeNode> funcNamesToOptionalCALDocNodes = new HashMap<String, ParseTreeNode>();
        
        for (final ParseTreeNode defnNode : defnListNode) {
            
            defnNode.verifyType(CALTreeParserTokenTypes.LET_DEFN, CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION, CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL);
            
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localSCNameNode = optionalCALDocNode.nextSibling();
                localSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String scName = localSCNameNode.getText();
                
                if (optionalCALDocNode.firstChild() != null) {
                    funcNamesToOptionalCALDocNodes.put(scName, optionalCALDocNode);
                }
                
            } else if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION) {
                
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                 
                ParseTreeNode localSCNameNode = typeDeclNode.firstChild();
                localSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String scName = localSCNameNode.getText();
                
                if (optionalCALDocNode.firstChild() != null) {
                    funcNamesToOptionalCALDocNodes.put(scName, optionalCALDocNode);
                }
                
            } else { // must be a LET_PATTERN_MATCH_DECL
                // there is no CALDoc associated with a local pattern mathc declaration
            }
        }
        
        // Then, collect all the local variable bindings
    
        for (final ParseTreeNode defnNode : defnListNode) {

            switch (defnNode.getType()) {
                case (CALTreeParserTokenTypes.LET_DEFN): 
                {
                    ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                    optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                    
                    ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                    localFunctionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                    ++nLocalFunctions;
                    visitLocalVarDeclarationNode(identifierList, boundVariablesStack, localFunctionNameNode, false);
    
                    // functionName is a bound variable for all declarations in the 'let'
                    // and for the expression following the 'in'.
                    pushLocalVar(boundVariablesStack, localFunctionNameNode, identifierList);
                    
                    letDefnAndLocalPatternMatchDeclNodes.add(defnNode);
                    break;
                }
            
                case (CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION):
                {
                    ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                    optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                    
                    ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                    typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                     
                    typeDeclNodes.add(typeDeclNode); 
                    break;
                }
                
                case CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL:
                {
                    // A local pattern match declaration can declare one or more locally bound variables
                    // We will loop through the pattern to process each one
                    
                    letDefnAndLocalPatternMatchDeclNodes.add(defnNode);
                    
                    final ParseTreeNode patternMatchPatternNode = defnNode.firstChild();

                    switch (patternMatchPatternNode.getType()) {
                        case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR:
                        {
                            // a data cons pattern
                            // e.g. let Cons x y = foo; ...
                            
                            ParseTreeNode dcNameListNode = patternMatchPatternNode.firstChild();
                            dcNameListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON);
                            
                            ParseTreeNode dcArgBindingsNode = dcNameListNode.nextSibling();  
                            
                            final ParseTreeNode dcNameNode = dcNameListNode.firstChild();
    
                            visitDataConsNameNode(identifierList, dcNameNode);

                            switch (dcArgBindingsNode.getType()) {
                            case CALTreeParserTokenTypes.PATTERN_VAR_LIST:
                                // a data cons pattern with positional patterns
                                // e.g. let Cons x y = foo; ...
                                
                                for (final ParseTreeNode patternVarNode : dcArgBindingsNode) {

                                    if (patternVarNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
                                        
                                        ++nLocalFunctions;
                                        visitLocalVarDeclarationNode(identifierList, boundVariablesStack, patternVarNode, false);

                                        // the pattern variable is a bound variable for all declarations in the 'let'
                                        // and for the expression following the 'in'.
                                        pushLocalVar(boundVariablesStack, patternVarNode, identifierList);
                                    }
                                }
                                break;
    
                            case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST:
                                // a data cons pattern with field-pattern pairs
                                // e.g. let Cons {head=x, tail} = foo; ...
                                
                                // Set pattern variables for punned fields
                                unpunPunnedFields(dcArgBindingsNode);
                                
                                for (final ParseTreeNode fieldBindingVarAssignmentNode : dcArgBindingsNode) {

                                    final ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
                                    final ParseTreeNode patternVarNode = fieldNameNode.nextSibling();

                                    if (patternVarNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
                                        
                                        ++nLocalFunctions;
                                        visitLocalVarDeclarationNode(identifierList, boundVariablesStack, patternVarNode, false);

                                        // the pattern variable is a bound variable for all declarations in the 'let'
                                        // and for the expression following the 'in'.
                                        pushLocalVar(boundVariablesStack, patternVarNode, identifierList);
                                    }
                                }
                                break;
    
                            default:
                                dcArgBindingsNode.unexpectedParseTreeNode();
                                break;
                            }
                            
                            break;
                        }
    
                        case CALTreeParserTokenTypes.COLON:
                        case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR:
                        {
                            // a list cons pattern and a tuple pattern can be treated in a similar way, because
                            // in both cases the node's children is the list of patterns
                            
                            // list cons pattern, e.g. let a:b = foo; ...
                            // tuple pattern, e.g. let (a, b, c) = foo; ...
                            
                            for (final ParseTreeNode patternVarNode : patternMatchPatternNode) {

                                if (patternVarNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
                                    
                                    ++nLocalFunctions;
                                    visitLocalVarDeclarationNode(identifierList, boundVariablesStack, patternVarNode, false);

                                    // the pattern variable is a bound variable for all declarations in the 'let'
                                    // and for the expression following the 'in'.
                                    pushLocalVar(boundVariablesStack, patternVarNode, identifierList);
                                }
                            }
                            break;
                        }
    
                        case CALTreeParserTokenTypes.RECORD_PATTERN:
                        {
                            // a record pattern
                            // e.g. let {_ | a, b=y} = foo; ...
                            
                            final ParseTreeNode baseRecordPatternNode = patternMatchPatternNode.firstChild();
                            baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
                            
                            final ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();                    
                            
                            // Set pattern variables for punned fields
                            unpunPunnedFields(fieldBindingVarAssignmentListNode);
                            
                            for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {

                                final ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
                                final ParseTreeNode patternVarNode = fieldNameNode.nextSibling();

                                if (patternVarNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
                                    
                                    ++nLocalFunctions;
                                    visitLocalVarDeclarationNode(identifierList, boundVariablesStack, patternVarNode, false);

                                    // the pattern variable is a bound variable for all declarations in the 'let'
                                    // and for the expression following the 'in'.
                                    pushLocalVar(boundVariablesStack, patternVarNode, identifierList);
                                }
                            }
                            break;
                        }
    
                        default:
                        {
                            patternMatchPatternNode.unexpectedParseTreeNode();
                            break;
                        }
                    }
                    break;
                }
                
                default:
                {
                    defnListNode.unexpectedParseTreeNode();
                    break;
                }
            }
        }
        
        // Then scan the type declarations
        
        for (final ParseTreeNode defnNode : typeDeclNodes) {

            ParseTreeNode localSCNameNode = defnNode.firstChild();

            localSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            visitLocalVarDeclarationNode(identifierList, boundVariablesStack, localSCNameNode, true);
        
            findIdentifiersInTypeDecl(identifierList, defnNode);
        }
        
        // Now visit the definitions of each variable 

        for (final ParseTreeNode defnNode : letDefnAndLocalPatternMatchDeclNodes) {

            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
            
                ParseTreeNode localFunctionNameNode = defnNode.getChild(1);
                localFunctionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String scName = localFunctionNameNode.getText();

                ParseTreeNode varListNode = localFunctionNameNode.nextSibling();

                // get the optional CALDoc node stored in the map.
                // this may be the CALDoc associated with the corresponding function type declaration, or if
                // there is none, the one associated with this function definition itself.
                ParseTreeNode optionalCALDocNodeFromMap = funcNamesToOptionalCALDocNodes.get(scName);

                findIdentifiersInLambda(identifierList, boundVariablesStack, varListNode, varListNode.nextSibling(), optionalCALDocNodeFromMap);
                
            } else {
                
                defnNode.verifyType(CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL);

                final ParseTreeNode patternMatchPatternNode = defnNode.firstChild();
                final ParseTreeNode patternMatchExprNode = patternMatchPatternNode.nextSibling();
                
                findIdentifiersInLambda(identifierList, boundVariablesStack, null, patternMatchExprNode);
            }
        }

        ParseTreeNode exprNode = defnListNode.nextSibling();
        findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);

        boundVariablesStack.popN(nLocalFunctions);
        return;
    }
    
    /**
     * Determines the identifiers in a parseTree describing a type expression. 
     * 
     * @param identifierList
     * @param parseTree 
     */
    void findIdentifiersInDeclaredTypeExpr(List<I> identifierList, ParseTreeNode parseTree) {
    
        switch (parseTree.getType()) {
            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR :
            {
                ParseTreeNode domainNode = parseTree.firstChild();
                findIdentifiersInDeclaredTypeExpr(identifierList, domainNode);
                findIdentifiersInDeclaredTypeExpr(identifierList, domainNode.nextSibling());
                return;
            }

            case CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR :
            {               
                if (parseTree.hasNoChildren()) {
                    return;
                }

                if (parseTree.hasExactlyOneChild()) {
                    // the type (t) is equivalent to the type t.
                    findIdentifiersInDeclaredTypeExpr(identifierList, parseTree.firstChild());
                    return;
                }

                for (final ParseTreeNode componentNode : parseTree) {

                    findIdentifiersInDeclaredTypeExpr(identifierList, componentNode);
                }
                return;
            }

            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR :
            {
                findIdentifiersInDeclaredTypeExpr(identifierList, parseTree.firstChild());
                return;
            }
    
            case CALTreeParserTokenTypes.TYPE_APPLICATION :
            {
                for (final ParseTreeNode argNode : parseTree) {
                    findIdentifiersInDeclaredTypeExpr(identifierList, argNode);
                }

                return;
            }
    
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            {
                visitTypeConsNameNode(identifierList, parseTree);
                return;
            }
    
            case CALTreeParserTokenTypes.VAR_ID :
            {
                return;
            }
                
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR :
            {
                ParseTreeNode recordVarNode = parseTree.firstChild();
                recordVarNode.verifyType(CALTreeParserTokenTypes.RECORD_VAR);
                
                ParseTreeNode fieldTypeAssignmentListNode = recordVarNode.nextSibling();
                fieldTypeAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT_LIST);
                
                for (final ParseTreeNode fieldTypeAssignmentNode : fieldTypeAssignmentListNode) {
                         
                    fieldTypeAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT);
                    ParseTreeNode fieldNameNode = fieldTypeAssignmentNode.firstChild();
                    
                    ParseTreeNode typeNode = fieldNameNode.nextSibling();
                    findIdentifiersInDeclaredTypeExpr(identifierList, typeNode);                   
                }
                                
                return;           
            }
    
            default :
            {                
                parseTree.unexpectedParseTreeNode();
            }
        }
    }
    
    /**
     * Finds identifiers in a type declaration.
     * 
     * @param identifierList
     * @param typeDeclarationNode
     * 
     */
    void findIdentifiersInTypeDecl(List<I> identifierList, ParseTreeNode typeDeclarationNode) {

        typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);

        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        ParseTreeNode typeSignatureNode = functionNameNode.nextSibling();
        findIdentifiersInTypeSignature(identifierList, typeSignatureNode);        
        
        return;
    }
    
    /**
     * Finds identifiers froma a class context node.
     * 
     * The context specifies the type variables that are qualified by type class constraints.
     * 
     * @param identifierList
     * @param contextListNode
     */
    void findIdentifiersInClassContext(List<I> identifierList, ParseTreeNode contextListNode) {
        contextListNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);
        
        for (final ParseTreeNode contextNode : contextListNode) {
            contextNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT);
            ParseTreeNode typeClassNameNode = contextNode.firstChild();
            typeClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            visitClassNameNode(identifierList, typeClassNameNode);
        }
    }
    
    /**
     * Finds identifiers from a type context node.
     *  
     * The context specifies
     * a. the type variables that are qualified by type class constraints.
     * b. the row variables that have lacks fields constraints.
     *
     * @param identifierList
     * @param contextListNode
     */
    void findIdentifiersInDeclaredTypeContext(List<I> identifierList, ParseTreeNode contextListNode) {
        
        contextListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONTEXT_LIST, CALTreeParserTokenTypes.TYPE_CONTEXT_NOTHING, CALTreeParserTokenTypes.TYPE_CONTEXT_SINGLETON);
       
        for (final ParseTreeNode contextNode : contextListNode) {

            switch (contextNode.getType())
            { 
                case CALTreeParserTokenTypes.CLASS_CONTEXT :
                {
                    ParseTreeNode typeClassNameNode = contextNode.firstChild();
                    typeClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                    visitClassNameNode(identifierList, typeClassNameNode);
                    break;
                }
                    
                case CALTreeParserTokenTypes.LACKS_FIELD_CONTEXT:
                {                   
                   break;
                }
               
                default:
                {                    
                    contextNode.unexpectedParseTreeNode();
                }
            }
        }
        
        return;
    }
    
    void findIdentifiersInFunctionDefinition(List<I> identifierList, ParseTreeNode functionNode) {
        functionNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);

        ParseTreeNode optionalCALDocNode = functionNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        
        ParseTreeNode functionNameNode = accessModifierNode.nextSibling();
        visitFunctionOrClassMethodDefinitionNameNode(identifierList, functionNameNode);
                
        ParseTreeNode paramListNode = accessModifierNode.nextSibling().nextSibling();
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
        
        ArrayStack<V> namedArgumentsStack = ArrayStack.<V>make();        
        for (final ParseTreeNode varNode : paramListNode) {

            varNode.verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);
            //todoBI 
            //todo-jowong this doesn't work for the AllIdentifierFinder, but this method is never called in that case
            //since the AllIndentifier finder happens to only be used for code-gems, and code gems do not have
            //top level functions.
            pushLocalVar(namedArgumentsStack, varNode, null);          
        }
                
        findIdentifiersInExpr(identifierList, namedArgumentsStack, paramListNode.nextSibling());
    }
    
    void findIdentifiersInFunctionDeclaration(List<I> identifierList, ParseTreeNode topLevelTypeDeclarationNode) {
        topLevelTypeDeclarationNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION);
                  
        ParseTreeNode optionalCALDocNode = topLevelTypeDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode functionTypeDeclarationNode = optionalCALDocNode.nextSibling();
        ParseTreeNode functionNameNode = functionTypeDeclarationNode.firstChild();
        visitFunctionOrClassMethodDefinitionNameNode(identifierList, functionNameNode);
        
        ParseTreeNode typeSignatureNode = functionNameNode.nextSibling();
        findIdentifiersInTypeSignature(identifierList, typeSignatureNode);
    }
    
    void findIdentifiersInTypeSignature(List<I> identifierList, ParseTreeNode typeSignatureNode) {
        typeSignatureNode.verifyType(CALTreeParserTokenTypes.TYPE_SIGNATURE);
        
        ParseTreeNode contextListNode = typeSignatureNode.firstChild();
        ParseTreeNode declarationNode = contextListNode.nextSibling();
        
        findIdentifiersInDeclaredTypeContext(identifierList, contextListNode);
        findIdentifiersInDeclaredTypeExpr(identifierList, declarationNode);
    }
    
    void findIdentifiersInDataDeclaration(List<I> identifierList, ParseTreeNode dataDeclarationNode) {
        dataDeclarationNode.verifyType(CALTreeParserTokenTypes.DATA_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = dataDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        ParseTreeNode typeConsNameNode = accessModifierNode.nextSibling();
        visitTypeConsDefnNameNode(identifierList, typeConsNameNode);
        
        ParseTreeNode typeConsParamListNode = typeConsNameNode.nextSibling();
        ParseTreeNode dataConsDefnListNode = typeConsParamListNode.nextSibling();
        dataConsDefnListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST);
        
        for (final ParseTreeNode dataConsDefnNode : dataConsDefnListNode) {
            
            dataConsDefnNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);
            
            ParseTreeNode dataConsOptionalCALDocNode = dataConsDefnNode.firstChild();
            dataConsOptionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            findIdentifiersInCALDocComment(identifierList, dataConsOptionalCALDocNode);

            ParseTreeNode dataConsAccessModifierNode = dataConsOptionalCALDocNode.nextSibling();
            ParseTreeNode dataConsNameNode = dataConsAccessModifierNode.nextSibling();
            visitDataConsDefnNameNode(identifierList, dataConsNameNode);
            
            ParseTreeNode dataConsArgListNode = dataConsNameNode.nextSibling();
            for(ParseTreeNode dataConsArgNode = dataConsArgListNode.firstChild();
                dataConsArgNode != null;
                dataConsArgNode = dataConsArgNode.nextSibling()) {
                
                dataConsArgNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAMED_ARG);
                
                // Get the arg name node.
                ParseTreeNode dataConsArgNameNode = dataConsArgNode.firstChild();
                
                // the arg name doesn't become an identifier..
                
                // Get the type node.
                ParseTreeNode maybePlingTypeExprNode = dataConsArgNameNode.nextSibling();
                
                ParseTreeNode dataConsArgTypeNode;
                if (maybePlingTypeExprNode.getType() == CALTreeParserTokenTypes.STRICT_ARG) {
                    dataConsArgTypeNode = maybePlingTypeExprNode.firstChild();
                } else {
                    dataConsArgTypeNode = maybePlingTypeExprNode;                        
                }

                findIdentifiersInDeclaredTypeExpr(identifierList, dataConsArgTypeNode);                
            }
        }
        
        ParseTreeNode derivingClauseNode = dataConsDefnListNode.nextSibling();
        findIndentifiersInDerivingClauseNode(identifierList, derivingClauseNode);
    }
    
    void findIdentifiersInTypeClassDefinition(List<I> identifierList, ParseTreeNode typeClassNode) {
        typeClassNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);
        
        ParseTreeNode optionalCALDocNode = typeClassNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        ParseTreeNode classContextListNode = accessModifierNode.nextSibling();       
        ParseTreeNode typeClassNameNode = classContextListNode.nextSibling();
        findIdentifiersInClassContext(identifierList, classContextListNode);
        visitClassDefnNameNode(identifierList, typeClassNameNode);
        
        ParseTreeNode typeVarNode = typeClassNameNode.nextSibling();
        typeVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
        ParseTreeNode classMethodListNode = typeVarNode.nextSibling();
        classMethodListNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD_LIST);
        for (final ParseTreeNode classMethodNode : classMethodListNode) {
            
            classMethodNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD);
            
            ParseTreeNode classMethodOptionalCALDocNode = classMethodNode.firstChild();
            classMethodOptionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            findIdentifiersInCALDocComment(identifierList, classMethodOptionalCALDocNode);

            ParseTreeNode classMethodAccessModifierNode = classMethodOptionalCALDocNode.nextSibling();
            ParseTreeNode classMethodNameNode = classMethodAccessModifierNode.nextSibling();
            visitFunctionOrClassMethodDefinitionNameNode(identifierList, classMethodNameNode);
            
            ParseTreeNode typeSignatureNode = classMethodNameNode.nextSibling();
            findIdentifiersInTypeSignature(identifierList, typeSignatureNode);
        }
    }
    
    void findIdentifiersInInstanceDefinition(List<I> identifierList, ParseTreeNode instanceNode) {
        instanceNode.verifyType(CALTreeParserTokenTypes.INSTANCE_DEFN);
        
        ParseTreeNode optionalCALDocNode = instanceNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode instanceNameNode = optionalCALDocNode.nextSibling();
        instanceNameNode.verifyType(CALTreeParserTokenTypes.INSTANCE_NAME);
        
        ParseTreeNode classContextListNode = instanceNameNode.firstChild();
        findIdentifiersInClassContext(identifierList, classContextListNode);
        
        ParseTreeNode qualifiedClassNameNode = classContextListNode.nextSibling();
        
        visitClassNameNode(identifierList, qualifiedClassNameNode);
        ParseTreeNode instanceTypeConsNameNode = qualifiedClassNameNode.nextSibling();
        if (instanceTypeConsNameNode.getType() == CALTreeParserTokenTypes.GENERAL_TYPE_CONSTRUCTOR ||
                instanceTypeConsNameNode.getType() == CALTreeParserTokenTypes.UNPARENTHESIZED_TYPE_CONSTRUCTOR) {
            ParseTreeNode qualifiedTypeConsNameNode = instanceTypeConsNameNode.firstChild();
            visitTypeConsNameNode(identifierList, qualifiedTypeConsNameNode);
        }
        
        ParseTreeNode instanceMethodListNode = instanceNameNode.nextSibling();
        instanceMethodListNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD_LIST);
        
        for (final ParseTreeNode instanceMethodNode : instanceMethodListNode) {
               
           instanceMethodNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD);
          
           ParseTreeNode optionalInstanceMethodCALDocNode = instanceMethodNode.firstChild();
           optionalInstanceMethodCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
           findIdentifiersInCALDocComment(identifierList, optionalInstanceMethodCALDocNode);
           
           ParseTreeNode instanceMethodNameNode = optionalInstanceMethodCALDocNode.nextSibling();
           visitInstanceMethodNameNode(identifierList, instanceMethodNameNode, qualifiedClassNameNode);
           
           ParseTreeNode resolvingFunctionNameNode = instanceMethodNameNode.nextSibling();
           visitFunctionOrClassMethodNameNode(identifierList, ArrayStack.<V>make(), resolvingFunctionNameNode);
        }
    }
    
    void findIdentifiersInForeignDataDeclaration(List<I> identifierList, ParseTreeNode foreignNode) {
        foreignNode.verifyType(CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = foreignNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode internalAccessModifierNode = optionalCALDocNode.nextSibling();
        ParseTreeNode externalNameNode = internalAccessModifierNode.nextSibling();
        ParseTreeNode externalAccessModifierNode = externalNameNode.nextSibling(); 
        ParseTreeNode consNameNode = externalAccessModifierNode.nextSibling();
        visitTypeConsDefnNameNode(identifierList, consNameNode);
        
        ParseTreeNode derivingClauseNode = consNameNode.nextSibling();
        findIndentifiersInDerivingClauseNode(identifierList, derivingClauseNode);
    }
    
    void findIndentifiersInDerivingClauseNode(List<I> identifierList, ParseTreeNode derivingClauseNode) {
        if (derivingClauseNode == null) {
            return;
        }
        
        derivingClauseNode.verifyType(CALTreeParserTokenTypes.LITERAL_deriving);
        for (final ParseTreeNode derivingTypeClassNameNode : derivingClauseNode) {
            
            visitClassNameNode(identifierList, derivingTypeClassNameNode);
        }
    }    
    
    void findIdentifiersInForeignFunctionDeclaration(List<I> identifierList, ParseTreeNode foreignSCNode) {
        foreignSCNode.verifyType(CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = foreignSCNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode externalNameNode = optionalCALDocNode.nextSibling();
        externalNameNode.verifyType(CALTreeParserTokenTypes.STRING_LITERAL);
        ParseTreeNode accessModifierNode = externalNameNode.nextSibling();
        ParseTreeNode typeDeclarationNode = accessModifierNode.nextSibling();
        findIdentifiersInTypeDecl(identifierList, typeDeclarationNode);
        
        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        visitFunctionOrClassMethodDefinitionNameNode(identifierList, functionNameNode);                
    }
           
    void findIdentifiersInImportDeclarations(List<I> identifierList, ParseTreeNode importDeclarationListNode) {
        importDeclarationListNode.verifyType(CALTreeParserTokenTypes.IMPORT_DECLARATION_LIST);
        
        // Look through each import statement
        
        for (final ParseTreeNode importDeclarationNode : importDeclarationListNode) {
    
           importDeclarationNode.verifyType(CALTreeParserTokenTypes.LITERAL_import);
    
           ParseTreeNode importedModuleNameNode = importDeclarationNode.firstChild();
           visitModuleNameNode(identifierList, importedModuleNameNode);
           
           ModuleName fullyQualifiedImportedModuleName = ModuleNameUtilities.getModuleNameFromParseTree(importedModuleNameNode);
           
           importedModuleNames.add(fullyQualifiedImportedModuleName);

           // Examine 'import using' clauses
           ParseTreeNode usingClauseNode = importedModuleNameNode.nextSibling();
           if (usingClauseNode != null) {
               
               usingClauseNode.verifyType(CALTreeParserTokenTypes.LITERAL_using);
               
               for (final ParseTreeNode usingItemNode : usingClauseNode) {
                   
                   switch (usingItemNode.getType()) {
                   
                       case CALTreeParserTokenTypes.LITERAL_function:
                       {
                           for (final ParseTreeNode functionNameNode : usingItemNode) {
                               
                               functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                               usingFunctionOrClassMethodMap.put(functionNameNode.getText(), fullyQualifiedImportedModuleName);
                               
                               visitUnqualifiedFunctionOrClassMethodNameNode(identifierList, functionNameNode, importedModuleNameNode);
                           }                           
                           break;
                       }
                       case CALTreeParserTokenTypes.LITERAL_dataConstructor:
                       {
                           for (final ParseTreeNode dataConstructorNameNode : usingItemNode) {

                               dataConstructorNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
                               usingDataConstructorMap.put(dataConstructorNameNode.getText(), fullyQualifiedImportedModuleName);
                               
                               visitUnqualifiedDataConsNameNode(identifierList, dataConstructorNameNode, importedModuleNameNode);
                           }
                           break;
                       }
                       case CALTreeParserTokenTypes.LITERAL_typeConstructor:
                       {
                           for (final ParseTreeNode typeConstructorNameNode : usingItemNode) {

                               typeConstructorNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
                               usingTypeConstructorMap.put(typeConstructorNameNode.getText(), fullyQualifiedImportedModuleName);
                               
                               visitUnqualifiedTypeConsNameNode(identifierList, typeConstructorNameNode, importedModuleNameNode);
                           }
                           break;
                       }                       
                       case CALTreeParserTokenTypes.LITERAL_typeClass:
                       {
                           for (final ParseTreeNode typeClassNameNode : usingItemNode) {

                               typeClassNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
                               usingTypeClassMap.put(typeClassNameNode.getText(), fullyQualifiedImportedModuleName);
                               
                               visitUnqualifiedClassNameNode(identifierList, typeClassNameNode, importedModuleNameNode);
                           }
                           break;
                       }
                       default :
                       {                                                       
                           usingItemNode.unexpectedParseTreeNode();
                           return;
                       }                    
                   } // end switch
               } // end for using
           } // end if
        } // end for imports
        
        // set up the module name resolver
        moduleNameResolver = ModuleNameResolver.make(currentModuleName, importedModuleNames);
    }
    
    /**
     * Find the identifiers in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param optionalCALDocNode the node whose optional child represents a CALDoc comment.
     */
    void findIdentifiersInCALDocComment(List<I> identifierList, ParseTreeNode optionalCALDocNode) {
        findIdentifiersInCALDocComment(identifierList, ArrayStack.<V>make(), optionalCALDocNode);
    }
    
    /**
     * Find the identifiers in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param boundVariablesStack the stack of bound variables at this point.
     * @param optionalCALDocNode the node whose optional child represents a CALDoc comment.
     */
    void findIdentifiersInCALDocComment(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode optionalCALDocNode) {
        
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        ParseTreeNode calDocNode = optionalCALDocNode.firstChild();
        if (calDocNode == null) {
            return;
        }

        calDocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
        
        ParseTreeNode descriptionNode = calDocNode.firstChild();
        descriptionNode.verifyType(CALTreeParserTokenTypes.CALDOC_DESCRIPTION_BLOCK);
        findIdentifiersInCALDocTextBlock(identifierList, descriptionNode.firstChild());
        
        ParseTreeNode taggedBlockListNode = descriptionNode.nextSibling();
        taggedBlockListNode.verifyType(CALTreeParserTokenTypes.CALDOC_TAGGED_BLOCKS);
        
        // loop through the tagged blocks to find the identifiers contained within them.
        for (final ParseTreeNode taggedBlockNode : taggedBlockListNode) {
            
            switch (taggedBlockNode.getType()) {
            
                case CALTreeParserTokenTypes.CALDOC_AUTHOR_BLOCK:
                case CALTreeParserTokenTypes.CALDOC_RETURN_BLOCK:
                case CALTreeParserTokenTypes.CALDOC_DEPRECATED_BLOCK:
                case CALTreeParserTokenTypes.CALDOC_VERSION_BLOCK:
                {
                    findIdentifiersInCALDocTextBlock(identifierList, taggedBlockNode.firstChild());
                    break;
                }
                    
                case CALTreeParserTokenTypes.CALDOC_ARG_BLOCK:
                {
                    ParseTreeNode argNameNode = taggedBlockNode.firstChild();
                    argNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
                    visitCALDocArgNameNode(identifierList, boundVariablesStack, argNameNode);
                    findIdentifiersInCALDocTextBlock(identifierList, argNameNode.nextSibling());
                    break;
                }
                
                case CALTreeParserTokenTypes.CALDOC_SEE_BLOCK:
                {
                    findIdentifiersInCALDocSeeBlock(identifierList, taggedBlockNode.firstChild());
                    break;    
                }
                
                default:
                    throw new IllegalStateException("Unexpected parse tree node " + taggedBlockNode.toDebugString() + ".");
            }
        }
    }
    
    /**
     * Find the identifiers in a CALDoc text block.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param blockNode the ParseTreeNode representing a CALDoc text block.
     */
    void findIdentifiersInCALDocTextBlock(List<I> identifierList, ParseTreeNode blockNode) {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT);
        
        findIdentifiersInCALDocTopLevelTextSegments(identifierList, blockNode);
    }
    
    /**
     * Find the identifiers in a preformatted text segment in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param blockNode the ParseTreeNode representing a CALDoc preformatted text segment.
     */
    void findIdentifiersInCALDocPreformattedBlock(List<I> identifierList, ParseTreeNode blockNode) {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_PREFORMATTED_BLOCK);
        
        findIdentifiersInCALDocTopLevelTextSegments(identifierList, blockNode);
    }
    
    /**
     * Find the identifiers in a CALDoc plain text segment from the given ParseTreeNode of the type
     * CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS. There should be none.
     * 
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param blockNode the ParseTreeNode representing a CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS.
     */
    void findIdentifiersInCALDocTextBlockWithoutInlineTags(List<I> identifierList, ParseTreeNode blockNode) {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS);
        
        for (final ParseTreeNode contentNode : blockNode) {
            
            switch (contentNode.getType()) {
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE:
            case CALTreeParserTokenTypes.CALDOC_BLANK_TEXT_LINE:
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE_BREAK:
                break;
            
            default:
                throw new IllegalStateException("Unexpected parse tree node " + contentNode.toDebugString() + ".");
            }
        }
    }
    
    /**
     * Find the identifiers in the children of the specified ParseTreeNode, where these child nodes represent text segments in
     * a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param parentNodeOfSegments the ParseTreeNode whose children represent text segments in a CALDoc comment.
     */
    void findIdentifiersInCALDocTopLevelTextSegments(List<I> identifierList, ParseTreeNode parentNodeOfSegments) {
        for (final ParseTreeNode contentNode : parentNodeOfSegments) {
            
            switch (contentNode.getType()) {
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE:
            case CALTreeParserTokenTypes.CALDOC_BLANK_TEXT_LINE:
            case CALTreeParserTokenTypes.CALDOC_TEXT_LINE_BREAK:
                break;
            
            case CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK:
            {
                findIdentifiersInCALDocInlineTagSegment(identifierList, contentNode);
                break;
            }
            
            default:
                throw new IllegalStateException("Unexpected parse tree node " + contentNode.toDebugString() + ".");
            }
        }
    }
    
    /**
     * Find the identifiers in an inline tag segment in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param inlineBlockNode the ParseTreeNode representing an inline tag segment in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineTagSegment(List<I> identifierList, ParseTreeNode inlineBlockNode) {
        inlineBlockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK);
        
        ParseTreeNode inlineTagNode = inlineBlockNode.firstChild();
        
        switch (inlineTagNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_TEXT_URL:
        {
            findIdentifiersInCALDocInlineURLTagSegment(identifierList, inlineTagNode);
            break;
        }
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK:
        {
            findIdentifiersInCALDocInlineLinkTagSegment(identifierList, inlineTagNode);
            break;
        }
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_EMPHASIZED_TEXT:
        {
            findIdentifiersInCALDocInlineEmTagSegment(identifierList, inlineTagNode);
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT:
        {
            findIdentifiersInCALDocInlineStrongTagSegment(identifierList, inlineTagNode);
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUPERSCRIPT_TEXT:
        {
            findIdentifiersInCALDocInlineSupTagSegment(identifierList, inlineTagNode);
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUBSCRIPT_TEXT:
        {
            findIdentifiersInCALDocInlineSubTagSegment(identifierList, inlineTagNode);
            break;
        }
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUMMARY:
        {
            findIdentifiersInCALDocInlineSummaryTagSegment(identifierList, inlineTagNode);
            break;
        }
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_CODE_BLOCK:
        {
            findIdentifiersInCALDocInlineCodeTagSegment(identifierList, inlineTagNode);
            break;
        }
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST:
        {
            findIdentifiersInCALDocInlineListTagSegment(identifierList, inlineTagNode);
            break;
        }
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_UNORDERED_LIST:
        {
            findIdentifiersInCALDocInlineListTagSegment(identifierList, inlineTagNode);
            break;
        }
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + inlineTagNode.toDebugString() + ".");
        }
    }
    
    /**
     * Find the identifiers in a hyperlinkable URL in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param urlNode the ParseTreeNode representing a hyperlinkable URL in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineURLTagSegment(List<I> identifierList, ParseTreeNode urlNode) {
        urlNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_URL);
        
        ParseTreeNode contentNode = urlNode.firstChild();
        findIdentifiersInCALDocTextBlockWithoutInlineTags(identifierList, contentNode);
    }
    
    /**
     * Find the identifiers in an emphasized piece of text in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param emNode the ParseTreeNode representing an emphasized piece of text in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineEmTagSegment(List<I> identifierList, ParseTreeNode emNode) {
        emNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_EMPHASIZED_TEXT);
        
        ParseTreeNode contentNode = emNode.firstChild();
        findIdentifiersInCALDocTextBlock(identifierList, contentNode);
    }
    
    /**
     * Find the identifiers in a strongly emphasized piece of text in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param strongNode the ParseTreeNode representing a strongly emphasized piece of text in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineStrongTagSegment(List<I> identifierList, ParseTreeNode strongNode) {
        strongNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT);
        
        ParseTreeNode contentNode = strongNode.firstChild();
        findIdentifiersInCALDocTextBlock(identifierList, contentNode);
    }
    
    /**
     * Find the identifiers in a superscripted piece of text in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param supNode the ParseTreeNode representing a superscripted piece of text in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineSupTagSegment(List<I> identifierList, ParseTreeNode supNode) {
        supNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUPERSCRIPT_TEXT);
        
        ParseTreeNode contentNode = supNode.firstChild();
        findIdentifiersInCALDocTextBlock(identifierList, contentNode);
    }
    
    /**
     * Find the identifiers in a subscripted piece of text in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param subNode the ParseTreeNode representing a subscripted piece of text in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineSubTagSegment(List<I> identifierList, ParseTreeNode subNode) {
        subNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUBSCRIPT_TEXT);
        
        ParseTreeNode contentNode = subNode.firstChild();
        findIdentifiersInCALDocTextBlock(identifierList, contentNode);
    }
    
    /**
     * Find the identifiers in a "@summary" inline tag segment in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param summaryNode the ParseTreeNode representing a "@summary" inline tag segment in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineSummaryTagSegment(List<I> identifierList, ParseTreeNode summaryNode) {
        summaryNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUMMARY);
        
        ParseTreeNode contentNode = summaryNode.firstChild();
        findIdentifiersInCALDocTextBlock(identifierList, contentNode);
    }
    
    /**
     * Find the identifiers in a code block in a CALDoc comment from the given ParseTreeNode.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param codeNode the ParseTreeNode representing a code block in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineCodeTagSegment(List<I> identifierList, ParseTreeNode codeNode) {
        codeNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_CODE_BLOCK);
        
        ParseTreeNode contentNode = codeNode.firstChild();
        findIdentifiersInCALDocPreformattedBlock(identifierList, contentNode);
    }
    
    /**
     * Find the identifiers in a list in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param listNode the ParseTreeNode representing a list in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineListTagSegment(List<I> identifierList, ParseTreeNode listNode) {
        listNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST, CALTreeParserTokenTypes.CALDOC_TEXT_UNORDERED_LIST);
        
        for (final ParseTreeNode itemNode : listNode) {

            itemNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_LIST_ITEM);
            
            ParseTreeNode itemContentNode = itemNode.firstChild();
            findIdentifiersInCALDocTextBlock(identifierList, itemContentNode);
        }
    }
    
    /**
     * Find the identifiers in an inline cross-reference in a CALDoc comment.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param linkNode the ParseTreeNode representing an inline cross-reference in a CALDoc comment.
     */
    void findIdentifiersInCALDocInlineLinkTagSegment(List<I> identifierList, ParseTreeNode linkNode) {
        linkNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_LINK);
        
        ParseTreeNode linkContextNode = linkNode.firstChild();
        
        switch (linkContextNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_FUNCTION:
        {
            ParseTreeNode refNode = linkContextNode.firstChild();
            
            refNode.verifyType(
                CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR,
                CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR);
            
            ParseTreeNode nameNode = refNode.firstChild();
            nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
            
            visitFunctionOrClassMethodNameNode(identifierList, ArrayStack.<V>make(), nameNode);
            
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_MODULE:
        {
            ParseTreeNode refNode = linkContextNode.firstChild();
            
            refNode.verifyType(
                CALTreeParserTokenTypes.CALDOC_UNCHECKED_MODULE_NAME,
                CALTreeParserTokenTypes.CALDOC_CHECKED_MODULE_NAME);
            
            ParseTreeNode nameNode = refNode.firstChild();
            nameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
            
            visitModuleNameNode(identifierList, nameNode);
            
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_DATACONS:
        {
            ParseTreeNode refNode = linkContextNode.firstChild();
            
            refNode.verifyType(
                CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
                CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
            
            ParseTreeNode nameNode = refNode.firstChild();
            nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            
            visitDataConsNameNode(identifierList, nameNode);
            
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECONS:
        {
            ParseTreeNode refNode = linkContextNode.firstChild();
            
            refNode.verifyType(
                CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
                CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
            
            ParseTreeNode nameNode = refNode.firstChild();
            nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            
            visitTypeConsNameNode(identifierList, nameNode);
            
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECLASS:
        {
            ParseTreeNode refNode = linkContextNode.firstChild();
            
            refNode.verifyType(
                CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
                CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
            
            ParseTreeNode nameNode = refNode.firstChild();
            nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            
            visitClassNameNode(identifierList, nameNode);
            
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_WITHOUT_CONTEXT:
        {
            ParseTreeNode refNode = linkContextNode.firstChild();
            findIdentifiersInCALDocCrossReferenceWithoutContext(identifierList, refNode);
            break;
        }
        
        default:
        {
            throw new IllegalStateException("Unexpected parse tree node " + linkNode.toDebugString() + ".");
        }
        }
    }

    /**
     * Find the identifiers in a cross-reference in a CALDoc comment that appears without a 'context' keyword.
     * 
     * Such a reference may appear in a CALDoc comment, as in:
     * {at-link Prelude at-} - a module reference without context
     * {at-link Nothing at-} - a data constructor reference without context
     * at-see Prelude.Int - a type constructor reference without context
     * at-see Eq - a type class reference without context
     * 
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param refNode the ParseTreeNode representing a cross-reference in a CALDoc comment.
     */
    void findIdentifiersInCALDocCrossReferenceWithoutContext(List<I> identifierList, ParseTreeNode refNode) {
        refNode.verifyType(
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
            CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS,
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR,
            CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR);
        
        // if the cross reference contains a qualified/unqualified variable name (i.e. one that starts with a lowercase
        // character), than it can only be a reference to a function/class method, and we treat it as such
        if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR ||
            refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR) {
            
            ParseTreeNode nameNode = refNode.firstChild();
            nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
            
            visitFunctionOrClassMethodNameNode(identifierList, ArrayStack.<V>make(), nameNode);
            
        } else {
            // the cross reference's name starts with an uppercase character. In this case we don't know
            // what kind of reference it is (with respect to type constructor vs. data constructor vs. type class
            // vs. module name), so unless the parse tree has previously gone through name resolution and its nodes
            // annotated, we would have to handle the node separately as an unresolved reference without 'context'.
            
            // if the parse tree has gone through name resolution successfully, then the node would have been
            // annotated with information about what kind of identifier this represents.
            CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference category =
                refNode.getCategoryForCALDocConsNameWithoutContextCrossReference();
            
            // only visit the name if the node has been previously analysed and properly resolved (i.e. category
            // is not null), otherwise we do not know enough about it to assign a proper SourceIdentifier.Category
            // to the resulting source identifier.
            if (category != null) {
                ParseTreeNode qualifiedConsNode = refNode.firstChild();
                qualifiedConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                
                ParseTreeNode nameBeforeDotNode = qualifiedConsNode.firstChild();
                nameBeforeDotNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
                
                ParseTreeNode nameAfterDotNode = nameBeforeDotNode.nextSibling();
                nameAfterDotNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
                
                if (category == CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference.MODULE_NAME) {
                    
                    // we make a shallow copy of the qualified cons node, and modify the type to be HIERARCHICAL_MODULE_NAME
                    ParseTreeNode moduleNameNode = new ParseTreeNode();
                    moduleNameNode.copyContentsFrom(qualifiedConsNode);
                    moduleNameNode.initialize(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME");
                    
                    visitModuleNameNode(identifierList, moduleNameNode);
                    
                } else if (category == CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference.DATA_CONS_NAME) {
                    visitDataConsNameNode(identifierList, qualifiedConsNode);
                } else if (category == CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference.TYPE_CONS_NAME) {
                    visitTypeConsNameNode(identifierList, qualifiedConsNode);
                } else if (category == CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference.TYPE_CLASS_NAME) {
                    visitClassNameNode(identifierList, qualifiedConsNode);
                } else {
                    // there are only 4 enumerated constants in CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference
                    throw new IllegalStateException();
                }
            } else {
                // the category is null, and therefore we treat the node as an unresolved reference without 'context'.
                visitCALDocCrossReferenceWithoutContextConsNameNode(identifierList, refNode);
            }
        }
    }
    
    /**
     * Find the identifiers in a CALDoc "@see" block.
     * @param identifierList the mutable list of identifiers used to accumulate the found identifiers.
     * @param seeBlockNode the node corresponding to the "@see" block.
     */
    void findIdentifiersInCALDocSeeBlock(List<I> identifierList, ParseTreeNode seeBlockNode) {
        
        switch (seeBlockNode.getType()) {
        
            case CALTreeParserTokenTypes.CALDOC_SEE_FUNCTION_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    refNode.verifyType(
                        CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR,
                        CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR);
                    
                    ParseTreeNode nameNode = refNode.firstChild();
                    nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
                    
                    visitFunctionOrClassMethodNameNode(identifierList, ArrayStack.<V>make(), nameNode);
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_MODULE_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    refNode.verifyType(
                        CALTreeParserTokenTypes.CALDOC_UNCHECKED_MODULE_NAME,
                        CALTreeParserTokenTypes.CALDOC_CHECKED_MODULE_NAME);
                    
                    ParseTreeNode nameNode = refNode.firstChild();
                    nameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
                    
                    visitModuleNameNode(identifierList, nameNode);
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_DATACONS_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    refNode.verifyType(
                        CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
                        CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
                    
                    ParseTreeNode nameNode = refNode.firstChild();
                    nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                    
                    visitDataConsNameNode(identifierList, nameNode);
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_TYPECONS_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    refNode.verifyType(
                        CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
                        CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
                    
                    ParseTreeNode nameNode = refNode.firstChild();
                    nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                    
                    visitTypeConsNameNode(identifierList, nameNode);
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_TYPECLASS_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    refNode.verifyType(
                        CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
                        CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
                    
                    ParseTreeNode nameNode = refNode.firstChild();
                    nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
                    
                    visitClassNameNode(identifierList, nameNode);
                }
                break;
            }
                
            case CALTreeParserTokenTypes.CALDOC_SEE_BLOCK_WITHOUT_CONTEXT:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    findIdentifiersInCALDocCrossReferenceWithoutContext(identifierList, refNode);
                }
                break;
            }
                
            default:
                throw new IllegalStateException("Unexpected parse tree node " + seeBlockNode.toDebugString() + ".");
        }
    }
    
    /**
     * The traversal encountered a local variable declaration.
     * @param identifierList list of identifiers
     * @param boundVariablesStack
     * @param node variable declaration node
     * @param isTypeExpr whether the declaration is prior to a type expression (eg: myVar :: Double)
     */
    void visitLocalVarDeclarationNode(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode node, boolean isTypeExpr) {
        // The superclass behavior is to do nothing in this case.
        return;
    }
    
    /**
     * Push the declared local variable to the stack.
     * This method is called when a local variable binding is declared in the parsed code.
     * i.e. within:
     *     - lambda declaration: "(\ arg1 -> ... )"
     *     - let declaration:    "let arg1 = ... in ..."
     *     - case statement:     "case r of arg1 -> ...; {rec | arg2 = ...} -> ...;"
     *     - function declaration:     "public testFn arg1 = ... "
     * 
     * @param boundVariablesStack stack holding bound variable nodes
     * @param varNameNode node identifying the local variable
     * @param identifierList list of identifiers; this be appended with the local variable identifier
     */
    abstract void pushLocalVar(ArrayStack<V> boundVariablesStack, ParseTreeNode varNameNode, List<I> identifierList);    
    
    /**
     * @return comparator for sorting the returned list elements
     */
    abstract Comparator<I> getIdentifierListComparator();
    
    /**
     * Reference from the code to a module name
     * ex: "List.head"  
     */
    abstract void visitModuleNameNode(List<I> identifierList, ParseTreeNode moduleNameNode);
    
    /** 
     * Reference from code to unbound top level symbol (function or class method)
     * ex: "add 1 2" 
     **/
    abstract void visitFunctionOrClassMethodNameNode(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode qualifiedNode);
    
    /** 
     * Reference to class name 
     * ex: "x :: Num a -> a;" 
     */
    abstract void visitClassNameNode(List<I> identifierList, ParseTreeNode qualifiedNode);
    
    /**
     * Reference to a class definition name
     * ex: "public class Bounded a where"
     */
    abstract void visitClassDefnNameNode(List<I> identifierList, ParseTreeNode qualifiedNode);
    
    /**
     * Reference to a type constructor definition name
     * ex: "data public Range a"
     */
    abstract void visitTypeConsDefnNameNode(List<I> identifierList, ParseTreeNode typeConsNameNode);
    
    /**
     * Reference to a data constructor definition name
     * ex: "Public LT" 
     */
    abstract void visitDataConsDefnNameNode(List<I> identifierList, ParseTreeNode dataConsNameNode);
    
    /** 
     *  Reference to data constructor name
     *  ex: "True" 
     */
    abstract void visitDataConsNameNode(List<I> identifierList, ParseTreeNode qualifiedNode);
    
    /**
     * Reference to type constructor name
     * ex: "x :: Boolean"
     */
    abstract void visitTypeConsNameNode(List<I> identifierList, ParseTreeNode qualifiedNode);
    
    /**
     * Reference to instance method name
     * ex: "instanceMethodName = resolvingFunctionName;"
     */
    abstract void visitInstanceMethodNameNode(List<I> identifierList, ParseTreeNode instanceMethodNameNode, ParseTreeNode qualifiedClassNameNode);
    
    /**
     * Reference to function name in function or class method definition
     * ex: both occurrences of mySC in "mySC :: Boolean; public mySC = 5.0"
     */
    abstract void visitFunctionOrClassMethodDefinitionNameNode(List<I> identifierList, ParseTreeNode qualifiedNode);
    
    
    /** 
     * Reference from code specifically to unqualified function (ie: from import declaration) 
     */
    abstract void visitUnqualifiedFunctionOrClassMethodNameNode(List<I> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode);
    
    /** 
     * Reference from code specifically to unqualified class name (ie: from import declaration) 
     */
    abstract void visitUnqualifiedClassNameNode(List<I> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode);
    
    /** 
     * Reference from code specifically to unqualified data constructor (ie: from import declaration) 
     */
    abstract void visitUnqualifiedDataConsNameNode(List<I> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode);
    
    /** 
     * Reference from code specifically to unqualified type constructor (ie: from import declaration) 
     */
    abstract void visitUnqualifiedTypeConsNameNode(List<I> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode);
    
    /**
     * Reference to an argument from a CALDoc "@arg" block. 
     */
    abstract void visitCALDocArgNameNode(List<I> identifierList, ArrayStack<V> boundVariablesStack, ParseTreeNode argNameNode);
    
    /**
     * A cross reference in a CALDoc comment that starts with an uppercase character and appears without a context keyword.
     * ex: "{at-link Nothing at-}" 
     */
    abstract void visitCALDocCrossReferenceWithoutContextConsNameNode(List <I>identifierList, ParseTreeNode refNode);

    /**
     * Parse the code text and return the list of identifiers that occur within it. 
     * The list is ordered as dictated by the getIdentifierListComparator().
     * 
     * This method is intended to be the entry point for traversing unparsed code expressions.
     * 
     * @param parser token parser with stream set to the expression text 
     * @param treeParser tree parser to validate the parsed stream
     * @param compilerLogger the message logger associated with the parser and the treeParser
     * @param moduleName name of the module this expression belongs to
     * @param moduleNameResolver the module name resolver for the module to which this expression belongs.
     * @return List of identifiers encountered in the expression; null if problem occurs during parsing 
     */
    List<I> findIdentifiersInUnparsedExpression(CALParser parser, CALTreeParser treeParser, CompilerMessageLogger compilerLogger, ModuleName moduleName, ModuleNameResolver moduleNameResolver) {
        if ((parser == null) || (treeParser == null) || (moduleName == null)) {
            throw new NullPointerException();
        }
        
        final int oldNErrorsLogged = logger.getNErrors();
        
        try {
            ParseTreeNode exprNode = parseExpression(parser, treeParser, compilerLogger);
            if (exprNode == null) {
                return null;
            }
            return findIdentifiersInCodeExpression(exprNode, moduleName, moduleNameResolver);

        } catch (Exception e) {
            // An error has occurred. This is likely due to SourceIdentifierFinder 
            // traversal algorithm being out of sync with current CAL grammar. 
          
            try {
                if (logger.getNErrors() <= oldNErrorsLogged) {                  
                    //internal coding error
                    logger.logMessage(new CompilerMessage(new MessageKind.Fatal.InternalCodingError(), e));
                }
            } catch (AbortCompilation ace) {
                // Yeah, yeah, we know
                //raiseError will throw a AbortCompilation since a FATAL message was sent.
            }
            
            return null;
            
        } catch (AbortCompilation e) {

            // An error has occurred. This is likely due to SourceIdentifierFinder 
            // traversal algorithm being out of sync with current CAL grammar.                        
            return null;
        }
    }
    
    /**
     * Parse the module source and return the list of identifiers that occur within it. 
     * The list is ordered as dictated by the getIdentifierListComparator().
     * 
     * This method is intended to be the entry point for traversing unparsed module sources.
     * Note: Any messages held by the compiler will be cleared by this method.
     * 
     * @param parser token parser with stream set to the module source text 
     * @param treeParser tree parser to validate the parsed stream
     * @return List of identifiers encountered in the expression; null if problem occurs during parsing 
     */
    List<I> findIdentifiersInUnparsedModule(CALParser parser, CALTreeParser treeParser) {
        
        if ((parser == null) || (treeParser == null)) {
            throw new NullPointerException();
        }
        
       final int oldNErrorsLogged = logger.getNErrors();
        
        try {
            ParseTreeNode moduleDefNode = parseModule(parser, treeParser);
            if (moduleDefNode == null) {
                return null;
            }
            return findIdentifiersInModule(moduleDefNode);

        } catch (Exception e) {
            // An error has occurred. This is likely due to SourceIdentifierFinder 
            // traversal algorithm being out of sync with current CAL grammar. 
          
            try {
                if (logger.getNErrors() <= oldNErrorsLogged) {                  
                    //internal coding error
                    logger.logMessage(new CompilerMessage(new MessageKind.Fatal.InternalCodingError(), e));
                }
            } catch (AbortCompilation ace) {
                // Yeah, yeah, we know
                //raiseError will throw a AbortCompilation since a FATAL message was sent.
            }
            
            return null;
            
        } catch (AbortCompilation e) {

            // An error has occurred. This is likely due to SourceIdentifierFinder 
            // traversal algorithm being out of sync with current CAL grammar.                             
            return null;
        }
    }
    
    /**
     * Parse the specified expression text and return the parset tree node representing it.
     * 
     * @param parser
     * @param treeParser
     * @param compilerLogger the message logger associated with the parser and the treeParser
     * @return parse tree node representing expression, or null if error
     */
    private ParseTreeNode parseExpression(CALParser parser, CALTreeParser treeParser, CompilerMessageLogger compilerLogger) {

        if ((parser == null) || (treeParser == null)) {
            throw new NullPointerException();
        }

        int nOldErrors = logger.getNErrors();
        int nOldCompilerErrors = compilerLogger.getNErrors();
        
        // Make an AST for the expression
        // Call the parser to parse just an expression for us

        try {
            try {
                parser.codeGemExpr();
            } catch (antlr.RecognitionException e) {
                // Recognition (syntax) error
                final SourceRange sourceRange = CALParser.makeSourceRangeFromException(e);
                // SourceIdentifierFinder: Syntax error
                logger.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), e));
                return null;
            
            } catch (antlr.TokenStreamException e) {
                // Failed to lex
                // Bad token stream
                // SourceIdentifierFinder: Bad token stream
                logger.logMessage(new CompilerMessage(new MessageKind.Error.BadTokenStream(), e));
                return null;
            }
        } catch (AbortCompilation ace) {
            //raiseError may throw a AbortCompilation if more than the maximum number of errors has been generated,
            return null;
        }

        // If there are any compiler messages at error level or greater, there was a parse error
        if (compilerLogger.getNErrors() > nOldCompilerErrors || logger.getNErrors() > nOldErrors) {
            return null;
        }
        
        // Walk the parse tree as a sanity check on the generated AST and of the tree parser
        ParseTreeNode exprNode = (ParseTreeNode) parser.getAST();
        if (exprNode == null) {
            // SourceIdentifierFinder: Unable to generate expression tree            
            logger.logMessage(new CompilerMessage(new MessageKind.Error.UnableToGenerateExpressionTree()));
            return null;
        }

        try {
            treeParser.codeGemExpr(exprNode);
        } catch (antlr.RecognitionException e) {
            // SourceIdentifierFinder: Failed validation AST parse: {e.toString()}
            logger.logMessage(new CompilerMessage(new MessageKind.Error.FailedValidationASTParse(e.toString()), e));
            return null;
        } catch (NullPointerException e) {
            // Something nasty happened, possibly a bad/nonexistent AST
            // So, we failed to parse
            // SourceIdentifierFinder: Bad AST tree:
            logger.logMessage(new CompilerMessage(new MessageKind.Error.BadASTTree()));
            return null;
        } 
        
        return exprNode;
    }
    
    /**
     * Parse the specified module source and return a reference to the parse tree node representing it
     * Note: Any messages held by the logger will be cleared.
     * 
     * @param parser
     * @param treeParser
     * @return parse tree node representing module, or null if error
     */
    private ParseTreeNode parseModule(CALParser parser, CALTreeParser treeParser) {

        if ((parser == null) || (treeParser == null)) {
            throw new NullPointerException();
        }
        
        int nOldErrors = logger.getNErrors();
        
        // Make an AST for the expression
        // Call the parser to parse just an expression for us

        try {
            try {
                parser.module();
            } catch (antlr.RecognitionException e) {
                // Recognition (syntax) error
                final SourceRange sourceRange = CALParser.makeSourceRangeFromException(e);
                // SourceIdentifierFinder: Syntax error
                logger.logMessage(new CompilerMessage(sourceRange, new MessageKind.Error.SyntaxError(), e));
                return null;
            } catch (antlr.TokenStreamException e) {
                // Failed to lex
                // SourceIdentifierFinder: Bad token stream
                logger.logMessage(new CompilerMessage(new MessageKind.Error.BadTokenStream(), e));
                return null;
            }
        } catch (AbortCompilation ace) {
            //raiseError may throw a AbortCompilation if more than the maximum number of errors has been generated,
            return null;
        }

        // If there are any compiler messages at error level or greater, there was a parse error
        if (logger.getNErrors() > nOldErrors) {
            return null;
        }

        // Walk the parse tree as a sanity check on the generated AST and of the tree parser
        ParseTreeNode moduleDefNode = (ParseTreeNode) parser.getAST();
        if (moduleDefNode == null) {
            logger.logMessage(new CompilerMessage(new MessageKind.Error.UnableToGenerateExpressionTree()));
            return null;
        }

        try {
            treeParser.module(moduleDefNode);
        } catch (antlr.RecognitionException e) {
            // Failed to parse AST
            logger.logMessage(new CompilerMessage(new MessageKind.Error.FailedValidationASTParse(e.toString()), e));
            return null;
        } catch (NullPointerException e) {
            // Something nasty happened, possibly a bad/nonexistent AST
            // So, we failed to parse
            logger.logMessage(new CompilerMessage(new MessageKind.Error.BadASTTree()));
            return null;
        }
        
        return moduleDefNode;
    }    
    
    
    
    /**
     * This class is responsible for locating all references to identifiers which may be local 
     * arguments, functions, data constructors, type classes and type constructors defined 
     * within an external or the local module.
     * 
     * For each function in a module, an identifier appearing within its
     * defining body expression is a: 
     *  -(non built-in, non foreign) function defined within the same module 
     *  -(reference to a) function defined within a different module 
     *  -built-in function 
     *  -class method name
     *  -local function defined using a let 
     *  -pattern variable i.e. an argument variable, a variable from a case 
     *   pattern binding, or a lambda bound variable. 
     *  -data constructor, type constructor, or type class defined in the same module 
     *  -data constructor, type constructor, or type class defined within a different module
     *  -foreign function.  
     * 
     * LIMITATION (jowong): This class will not create SourceIdentifiers for @see/@link references that
     *   are "constructor names" appearing without the 'context' keyword, because no name resolution is
     *   performed and thus the targets of these references remain ambiguous.
     *
     * @author Iulian Radu
     */
    static final class AllIdentifierFinder extends SourceIdentifierFinder<SourceIdentifier, SourceIdentifier> {
        
        AllIdentifierFinder() {
            super();
        }

        /**
         * Since a module name is not really considered an identifier, we don't add it to the identifierList       
         */
        @Override
        void visitModuleNameNode(List<SourceIdentifier> identifierList, ParseTreeNode moduleNameNode) {
            moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        }
        
        /**
         * Adds the data constructor name to the identifierList.      
         */
        @Override
        void visitDataConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode qualifiedNode) {

            qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
            visitModuleNameNode(identifierList, moduleNameNode);
            ModuleName rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
            ModuleName moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            ParseTreeNode dataConsNameNode = moduleNameNode.nextSibling();
            String dataConsName = dataConsNameNode.getText();
            
            if ((moduleName == null) && (getModuleForImportedFunctionOrClassMethod(dataConsName) != null)) {
                moduleName = getModuleForImportedFunctionOrClassMethod(dataConsName);
            }
            
            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(dataConsName, dataConsNameNode.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR, rawModuleName, moduleName, minimallyQualifiedModuleName, (rawModuleName == null ? null : moduleNameNode.getAssemblySourceRange())));
            return;
        }
        
        /**
         * Adds the instance method name to the identifierList.     
         */
        @Override
        void visitInstanceMethodNameNode(List<SourceIdentifier> identifierList, ParseTreeNode instanceMethodNameNode, ParseTreeNode qualifiedClassNameNode) {
            
            qualifiedClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
            ParseTreeNode moduleNameNode = qualifiedClassNameNode.firstChild();
            visitModuleNameNode(identifierList, moduleNameNode);
            ModuleName rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
            ModuleName moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            ParseTreeNode classNameNode = moduleNameNode.nextSibling();
            String className = classNameNode.getText();
            instanceMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            String instanceMethodName = instanceMethodNameNode.getText();
            
            if ((moduleName == null) && (getModuleForImportedTypeClass(className) != null)) {
                moduleName = getModuleForImportedTypeClass(className);
            }
            
            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(instanceMethodName, instanceMethodNameNode.getSourceRange(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, rawModuleName, moduleName, minimallyQualifiedModuleName, (rawModuleName == null ? null : moduleNameNode.getAssemblySourceRange())));
            return;
        }
        
        /**
         * Adds the class name to the identifierList.
         */
        @Override
        void visitClassNameNode(List<SourceIdentifier> identifierList, ParseTreeNode qualifiedNode) {

            qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
            visitModuleNameNode(identifierList, moduleNameNode);
            ModuleName rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
            ModuleName moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            ParseTreeNode classNameNode = moduleNameNode.nextSibling();
            String className = classNameNode.getText();
            
            if ((moduleName == null) && (getModuleForImportedTypeClass(className) != null)) {
                moduleName = getModuleForImportedTypeClass(className);
            } 
            
            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(className, classNameNode.getSourceRange(), SourceIdentifier.Category.TYPE_CLASS, rawModuleName, moduleName, minimallyQualifiedModuleName, (rawModuleName == null ? null : moduleNameNode.getAssemblySourceRange())));
            return;
        }
        
        /**
         * Adds the type name to the identifierList.        
         */
        @Override
        void visitTypeConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode qualifiedNode) {

            qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
            visitModuleNameNode(identifierList, moduleNameNode);
            ModuleName rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
            ModuleName moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            ParseTreeNode typeNameNode = moduleNameNode.nextSibling();
            String typeName = typeNameNode.getText();
            
            if ((moduleName == null) && (getModuleForImportedTypeConstructor(typeName) != null)) {
                moduleName = getModuleForImportedTypeConstructor(typeName);
            } 

            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(typeName, typeNameNode.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR, rawModuleName, moduleName, minimallyQualifiedModuleName, (rawModuleName == null ? null : moduleNameNode.getAssemblySourceRange())));
            return;
        }
        
        
        /**
         * Adds the function/class method name to the identifierList if it is not defined in the bound
         * variables stack.      
         */
        @Override
        void visitFunctionOrClassMethodNameNode(List<SourceIdentifier> identifierList, ArrayStack<SourceIdentifier> boundVariablesStack, ParseTreeNode qualifiedNode) {

            qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
            ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
            visitModuleNameNode(identifierList, moduleNameNode);
            ModuleName rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
            ModuleName moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            ParseTreeNode varNameNode = moduleNameNode.nextSibling();
            String varName = varNameNode.getText();

            if ((moduleName == null) || (moduleName.equals(getCurrentModuleName()))) {
                
                SourceIdentifier definitionIdentifier = getDefinitionFromStack(varName, boundVariablesStack);
                if (definitionIdentifier != null) {

                    // Variable is pattern bound (i.e. an argument variable, a lambda bound 
                    // variable or a variable bound by a case alternative). Its module is the current module.
                    
                    identifierList.add(new SourceIdentifier.Qualifiable(varName, varNameNode.getSourceRange(), SourceIdentifier.Category.LOCAL_VARIABLE, rawModuleName, getCurrentModuleName(), getModuleNameResolver().getMinimallyQualifiedModuleName(getCurrentModuleName()), (rawModuleName == null ? null : moduleNameNode.getSourceRange()), definitionIdentifier));
                    return;
                }
            }
            
            if ((moduleName == null) && (getModuleForImportedFunctionOrClassMethod(varName) != null)) {
                moduleName = getModuleForImportedFunctionOrClassMethod(varName);
            } 
            
            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(varName, varNameNode.getSourceRange(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, rawModuleName, moduleName, minimallyQualifiedModuleName, (rawModuleName == null ? null : moduleNameNode.getAssemblySourceRange())));
            return;

        }
                
        @Override
        void visitLocalVarDeclarationNode(List<SourceIdentifier> identifierList, ArrayStack<SourceIdentifier> boundVariablesStack, ParseTreeNode varNameNode, boolean isTypeExpr) {
            
            if (isTypeExpr) {
                // Declarations of types of local variables are treated as reference the local variable declaration
                
                String varName = varNameNode.getText();
                SourceIdentifier definitionIdentifier = getDefinitionFromStack(varName, boundVariablesStack);
                if (definitionIdentifier != null) {
    
                    identifierList.add(new SourceIdentifier.Qualifiable(varName, varNameNode.getSourceRange(), SourceIdentifier.Category.LOCAL_VARIABLE, null, getCurrentModuleName(), getModuleNameResolver().getMinimallyQualifiedModuleName(getCurrentModuleName()), null, definitionIdentifier));
                    return;
                }
            }
        }
        
        /**
         * Adds the unqualified function/class method name to the identifierList.      
         */
        @Override
        void visitUnqualifiedFunctionOrClassMethodNameNode(List<SourceIdentifier> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {
            unqualifiedNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            String functionName = unqualifiedNameNode.getText();
            
            ModuleName rawModuleName = null;
            ModuleName moduleName = null;
            if (moduleNameNode != null) {
                rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
                moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            }
            
            if (moduleName == null) {
                rawModuleName = null;
                if (getModuleForImportedFunctionOrClassMethod(functionName) != null) {
                    moduleName = getModuleForImportedFunctionOrClassMethod(functionName);
                } else {
                    moduleName = getCurrentModuleName();
                }
            }
            
            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(functionName, unqualifiedNameNode.getSourceRange(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, rawModuleName, moduleName, minimallyQualifiedModuleName, (moduleNameNode == null ? null : moduleNameNode.getAssemblySourceRange())));
        }
        
        /**
         * Adds the unqualified type class name to the identifierList.      
         */
        @Override
        void visitUnqualifiedClassNameNode(List<SourceIdentifier> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {
            unqualifiedNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String className = unqualifiedNameNode.getText();
            
            ModuleName rawModuleName = null;
            ModuleName moduleName = null;
            if (moduleNameNode != null) {
                rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
                moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            }
            
            if (moduleName == null) {
                rawModuleName = null;
                if (getModuleForImportedTypeClass(className) != null) {
                    moduleName = getModuleForImportedTypeClass(className);
                } else {
                    moduleName = getCurrentModuleName();
                }
            }
            
            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(className, unqualifiedNameNode.getSourceRange(), SourceIdentifier.Category.TYPE_CLASS, rawModuleName, moduleName, minimallyQualifiedModuleName, (moduleNameNode == null ? null : moduleNameNode.getAssemblySourceRange())));
        }
        
        /**
         * Adds the unqualified data constructor name to the identifierList.      
         */
        @Override
        void visitUnqualifiedDataConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {
            unqualifiedNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String dataConsName = unqualifiedNameNode.getText();
            
            ModuleName rawModuleName = null;
            ModuleName moduleName = null;
            if (moduleNameNode != null) {
                rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
                moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            }
            
            if (moduleName == null) {
                rawModuleName = null;
                if (getModuleForImportedDataConstructor(dataConsName) != null) {
                    moduleName = getModuleForImportedDataConstructor(dataConsName);
                } else {
                    moduleName = getCurrentModuleName();
                }
            }
            
            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(dataConsName, unqualifiedNameNode.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR, rawModuleName, moduleName, minimallyQualifiedModuleName, (moduleNameNode == null ? null : moduleNameNode.getAssemblySourceRange())));
        }
        
        /**
         * Adds the unqualified type constructor method name to the identifierList.        
         */
        @Override
        void visitUnqualifiedTypeConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {
            unqualifiedNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String typeConsName = unqualifiedNameNode.getText();
            
            ModuleName rawModuleName = null;
            ModuleName moduleName = null;
            if (moduleNameNode != null) {
                rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
                moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            }
            
            if (moduleName == null) {
                rawModuleName = null;
                if (getModuleForImportedTypeConstructor(typeConsName) != null) {
                    moduleName = getModuleForImportedTypeConstructor(typeConsName);
                } else {
                    moduleName = getCurrentModuleName();
                }
            }
            
            ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
            identifierList.add(new SourceIdentifier.Qualifiable(typeConsName, unqualifiedNameNode.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR, rawModuleName, moduleName, minimallyQualifiedModuleName, (moduleNameNode == null ? null : moduleNameNode.getAssemblySourceRange())));
        }
        
        /**
         * Adds the function name from its definition to the identifierList.        
         */
        @Override
        void visitFunctionOrClassMethodDefinitionNameNode(List<SourceIdentifier> identifierList, ParseTreeNode functionNameNode) {
            functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            String functionName = functionNameNode.getText();
            identifierList.add(new SourceIdentifier.Qualifiable(functionName, functionNameNode.getSourceRange(), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, null, getCurrentModuleName(), getModuleNameResolver().getMinimallyQualifiedModuleName(getCurrentModuleName()), null));
        }

        /**
         * Adds the class name from its definition to the identifierList.      
         */
        @Override
        void visitClassDefnNameNode(List<SourceIdentifier> identifierList, ParseTreeNode typeClassNameNode) {
            typeClassNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String className = typeClassNameNode.getText();
            identifierList.add(new SourceIdentifier.Qualifiable(className, typeClassNameNode.getSourceRange(), SourceIdentifier.Category.TYPE_CLASS, null, getCurrentModuleName(), getModuleNameResolver().getMinimallyQualifiedModuleName(getCurrentModuleName()), null));
        }
        
        /**
         * Adds the type constructor name from its definition to the identifierList.       
         */
        @Override
        void visitTypeConsDefnNameNode(List<SourceIdentifier> identifierList, ParseTreeNode typeConsNameNode) {
            typeConsNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String typeConsName = typeConsNameNode.getText();
            identifierList.add(new SourceIdentifier.Qualifiable(typeConsName, typeConsNameNode.getSourceRange(), SourceIdentifier.Category.TYPE_CONSTRUCTOR, null, getCurrentModuleName(), getModuleNameResolver().getMinimallyQualifiedModuleName(getCurrentModuleName()), null));
        }

        /** 
         * Adds the data constructor name from its definition to the identifierList.        
         */
        @Override
        void visitDataConsDefnNameNode(List<SourceIdentifier> identifierList, ParseTreeNode dataConsNameNode) {
            dataConsNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String dataConsName = dataConsNameNode.getText();
            identifierList.add(new SourceIdentifier.Qualifiable(dataConsName, dataConsNameNode.getSourceRange(), SourceIdentifier.Category.DATA_CONSTRUCTOR, null, getCurrentModuleName(), getModuleNameResolver().getMinimallyQualifiedModuleName(getCurrentModuleName()), null));
        }
        
        /**
         * Adds the argument name in a CALDoc "@arg" block to the identifierList.
         */
        @Override
        void visitCALDocArgNameNode(List<SourceIdentifier> identifierList, ArrayStack<SourceIdentifier> boundVariablesStack, ParseTreeNode argNameNode) {
            argNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
            
            if (argNameNode.getType() == CALTreeParserTokenTypes.ORDINAL_FIELD_NAME) {
                return;
            }
            
            String varName = argNameNode.getText();

            SourceIdentifier definitionIdentifier = getDefinitionFromStack(varName, boundVariablesStack);
            if (definitionIdentifier != null) {

                // Variable is pattern bound (i.e. an argument variable, a lambda bound 
                // variable or a variable bound by a case alternative). Its module is the current module.
                
                identifierList.add(new SourceIdentifier.Qualifiable(varName, argNameNode.getSourceRange(), SourceIdentifier.Category.LOCAL_VARIABLE, null, getCurrentModuleName(), getModuleNameResolver().getMinimallyQualifiedModuleName(getCurrentModuleName()), null, definitionIdentifier));
            }
        }
        
        /**
         * For a CALDoc cross reference that is unresolved, we cannot tell what SourceIdentifier.Category it belongs to.
         * For this reason, we cannot do anything here without further information, and thus we skip processing this node.
         * 
         * todo-jowong This is an area that can be improved upon: the proper handling of these cross references by
         * subclasses of SourceIdentifierFinder as well as their clients.
         */
        @Override
        void visitCALDocCrossReferenceWithoutContextConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode refNode) {}
        
        /**
         * The local variable stack contains identifiers for local variable definitions.        
         */
        @Override
        void pushLocalVar(ArrayStack<SourceIdentifier> boundVariablesStack, ParseTreeNode varNameNode, List<SourceIdentifier> identifierList) {
            String varName = varNameNode.getText();
            SourceIdentifier definition = new SourceIdentifier.Qualifiable(varName, varNameNode.getSourceRange(), SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION, null, getCurrentModuleName(), getModuleNameResolver().getMinimallyQualifiedModuleName(getCurrentModuleName()), null);
            boundVariablesStack.push(definition);
            identifierList.add(definition);
        }
        
        /** The identifier list contains SourceIdentifier objects */
        @Override
        Comparator<SourceIdentifier> getIdentifierListComparator() {
            return SourceIdentifier.compareByStartPosition;
        }
        
        /** 
         * Returns the definition of the specified variable, if it is defined on stack
         * 
         * @param varName variable name to search on the stack
         * @param boundVariablesStack stack to search
         * @return parse tree node of the definition, or null if this is not locally bound
         */
        SourceIdentifier getDefinitionFromStack(String varName, ArrayStack<SourceIdentifier> boundVariablesStack) {
            for (int i = boundVariablesStack.size() - 1; i >= 0; i--) {
                SourceIdentifier boundVarNode = boundVariablesStack.get(i);
                if (boundVarNode.getName().equals(varName)) {
                    return boundVarNode;
                }
            }
            return null;
        }
    }
}
