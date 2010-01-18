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
 * RenamedIdentifierFinder.java
 * Creation date: (June 22, 2004)
 * By: Iulian Radu
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.util.ArrayStack;

/**
 * Traverses the specified AST with the purpose of renaming a specified identifier. 
 * 
 * This class is deprecated; it's only used by the GemCutter's code gem editing 
 * capabilities, and we'd like to remove it entirely at some point in the near
 * future.  For renaming toplevel elements (functions, classes, type constructors, 
 * and data constructors), this class has been superseded by the IdentifierRenamer
 * class.
 * 
 * This class still contains logical for "collateral damage" handling (where conflicting
 * bindings are renamed to be unique), but that no longer reflects the way that we handle
 * binding conflicts.
 * 
 * @author Iulian Radu
 */
final class RenamedIdentifierFinder extends SourceIdentifierFinder<SourceModification, String> {
    
    
    /**
     * Finds occurrences of identifiers which, when renamed, will conflict with locally 
     * bound variable names.
     * 
     * ex: within the expression "let r = 1.0; in s + r", identifier "s" will cause
     *     a conflict when renamed to "r"
     * 
     * @author Iulian Radu
     */
    private static final class RenameConflictFinder extends SourceIdentifierFinder<SourceIdentifier, String> {
        
        /** Category of identifier being renamed */ 
        private final SourceIdentifier.Category renameCategory;
        
        /** Old name of the identifier */
        private final QualifiedName renameOldName;
        
        /** New name of the identifier */
        private final QualifiedName renameNewName;
        
        /** Constructor */
        RenameConflictFinder(ModuleName currentModuleName, QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category) {
            setCurrentModuleName(currentModuleName);
            this.renameOldName = oldName;
            this.renameNewName = newName;
            this.renameCategory = category;
        }

        /**
         * Adds the current identifier to the list if it is to be renamed and its new name conflicts
         * with a locally bound variable.               
         */
        @Override
        void visitFunctionOrClassMethodNameNode(List<SourceIdentifier> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode qualifiedNode) {

            qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
            ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
            visitModuleNameNode(identifierList, moduleNameNode);
            ModuleName rawModuleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
            ModuleName moduleName = getModuleNameResolver().resolve(rawModuleName).getResolvedModuleName();
            ParseTreeNode varNameNode = moduleNameNode.nextSibling();
            String varName = varNameNode.getText();

            if (
                ((moduleName == null) || (moduleName.equals(getCurrentModuleName()))) &&
                boundVariablesStack.contains(varName)) {

                // Variable is pattern bound (i.e. an argument variable, a lambda bound 
                // variable or a variable bound by a case alternative). Its module is the current module.
                return;
            }
            
            if ( varName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) &&
                boundVariablesStack.contains(renameNewName)) {

                // We are renaming this variable, but we are renaming it to something which
                // already exists on the stack (bound by argument, let, lambda, or case). 
            
                // So mark this as impossible to change
                ModuleName minimallyQualifiedModuleName = getModuleNameResolver().getMinimallyQualifiedModuleName(moduleName);
                identifierList.add(new SourceIdentifier.Qualifiable(varName, varNameNode.getSourceRange(), SourceIdentifier.Category.LOCAL_VARIABLE, rawModuleName, moduleName, minimallyQualifiedModuleName, moduleNameNode.getAssemblySourceRange()));
            }
            
            return;
        }
        
        @Override
        void visitCALDocArgNameNode(List<SourceIdentifier> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode argNameNode) {}
        @Override
        void visitDataConsDefnNameNode(List<SourceIdentifier> identifierList, ParseTreeNode dataConsNameNode) {}
        @Override
        void visitTypeConsDefnNameNode(List<SourceIdentifier> identifierList, ParseTreeNode typeConsNameNode) {}        
        @Override
        void visitClassDefnNameNode(List<SourceIdentifier> identifierList, ParseTreeNode classNameNode) {}
        @Override
        void visitClassNameNode(List<SourceIdentifier> identifierList, ParseTreeNode qualifiedNode) {}
        @Override
        void visitInstanceMethodNameNode(List<SourceIdentifier> identifierList, ParseTreeNode instanceMethodNameNode, ParseTreeNode qualifiedNameNode) {}
        @Override
        void visitDataConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode qualifiedNode) {}
        @Override
        void visitTypeConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode qualifiedNode) {}
        @Override
        void visitFunctionOrClassMethodDefinitionNameNode(List<SourceIdentifier> identifierList, ParseTreeNode qualifiedNode) {}
        @Override
        void visitUnqualifiedFunctionOrClassMethodNameNode(List<SourceIdentifier> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {}
        @Override
        void visitUnqualifiedClassNameNode(List<SourceIdentifier> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {}
        @Override
        void visitUnqualifiedDataConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {}
        @Override
        void visitUnqualifiedTypeConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {}
        @Override
        void visitModuleNameNode(List<SourceIdentifier> identifierList, ParseTreeNode moduleNameNode) {}
        @Override
        void visitCALDocCrossReferenceWithoutContextConsNameNode(List<SourceIdentifier> identifierList, ParseTreeNode refNode) {}
        
        /** The identifier list contains SourceIdentifier objects */
        @Override
        Comparator<SourceIdentifier> getIdentifierListComparator() {
            return SourceIdentifier.compareByStartPosition;
        }

        @Override
        void pushLocalVar(ArrayStack<String> boundVariablesStack, ParseTreeNode varNameNode, List<SourceIdentifier> identifierList) {        
            boundVariablesStack.push(varNameNode.getText());      
        }        
    }
    
    /**
     * Finds occurrences of CALDoc "@arg" tags that need to be renamed.
     * 
     * For example, in the module M, if M.x is renamed to M.a, then the let expression (in the same module M):
     * 
     * let
     *    /**
     *     * @arg a the argument.
     *     * /
     *    localFunction a = a + x;
     * in
     *    []
     * 
     * ...would need to be modified to:
     * 
     * let
     *    /**
     *     * @arg a2 the argument.
     *     * /
     *    localFunction a2 = a2 + a;
     * in
     *    []
     * 
     * @author Joseph Wong
     */
    private static final class CALDocArgNameRenamingFinder extends SourceIdentifierFinder<SourceModification, String> {
        /** Category of identifier being renamed */ 
        private final SourceIdentifier.Category renameCategory;
        
        /** Old name of the identifier */
        private final QualifiedName renameOldName;
        
        /** New name of the identifier */
        private final QualifiedName renameNewName;
        
        /** Constructor */
        CALDocArgNameRenamingFinder(ModuleName currentModuleName, QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category) {
            setCurrentModuleName(currentModuleName);
            this.renameOldName = oldName;
            this.renameNewName = newName;
            this.renameCategory = category;
        }
        
        /**
         * Adds a renaming (original to new name) if this node refers the SC or Class Method being renamed.
         * 
         * Note: Local bindings are not checked because the conflict resolution algorithms ensures
         * no conflicts occur (progressing outwards).
         */
        @Override
        void visitCALDocArgNameNode(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode argNameNode) {
            argNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
            
            if (argNameNode.getType() == CALTreeParserTokenTypes.ORDINAL_FIELD_NAME) {
                return;
            }
            
            String varName = argNameNode.getText();
            
            if (varName.equals(renameOldName.getUnqualifiedName()) &&
                renameCategory == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD &&
                renameOldName.getModuleName().equals(getCurrentModuleName())) {
                
                identifierList.add(new SourceModification.ReplaceText(varName, renameNewName.getUnqualifiedName(), argNameNode.getSourcePosition()));
            }
        }        
        
        @Override
        void visitFunctionOrClassMethodNameNode(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode qualifiedNode) {}
        @Override
        void visitDataConsDefnNameNode(List<SourceModification> identifierList, ParseTreeNode dataConsNameNode) {}
        @Override
        void visitTypeConsDefnNameNode(List<SourceModification> identifierList, ParseTreeNode typeConsNameNode) {}        
        @Override
        void visitClassDefnNameNode(List<SourceModification> identifierList, ParseTreeNode classNameNode) {}
        @Override
        void visitClassNameNode(List<SourceModification> identifierList, ParseTreeNode qualifiedNode) {}
        @Override
        void visitInstanceMethodNameNode(List<SourceModification> identifierList, ParseTreeNode instanceMethodNameNode, ParseTreeNode qualifiedNameNode) {}
        @Override
        void visitDataConsNameNode(List<SourceModification> identifierList, ParseTreeNode qualifiedNode) {}
        @Override
        void visitTypeConsNameNode(List<SourceModification> identifierList, ParseTreeNode qualifiedNode) {}
        @Override
        void visitFunctionOrClassMethodDefinitionNameNode(List<SourceModification> identifierList, ParseTreeNode qualifiedNode) {}
        @Override
        void visitUnqualifiedFunctionOrClassMethodNameNode(List<SourceModification> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {}
        @Override
        void visitUnqualifiedClassNameNode(List<SourceModification> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {}
        @Override
        void visitUnqualifiedDataConsNameNode(List<SourceModification> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {}
        @Override
        void visitUnqualifiedTypeConsNameNode(List<SourceModification> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {}
        @Override
        void visitModuleNameNode(List<SourceModification> identifierList, ParseTreeNode moduleNameNode) {}
        @Override
        void visitCALDocCrossReferenceWithoutContextConsNameNode(List<SourceModification> identifierList, ParseTreeNode refNode) {}
        
        /** The identifier list contains SourceIdentifier objects */
        @Override
        Comparator<SourceModification> getIdentifierListComparator() {
            return SourceModification.compareByPosition;
        }
        
        @Override
        void pushLocalVar(ArrayStack<String> boundVariablesStack, ParseTreeNode varNameNode, List<SourceModification> identifierList) {        
            boundVariablesStack.push(varNameNode.getText());      
        }        
    }
    
    /** Original name of identifier to rename */
    private final QualifiedName renameOldName;
    
    /** New name of identifier to rename*/
    private final QualifiedName renameNewName;
    
    /** Category of identifier renamed */
    private final SourceIdentifier.Category renameCategory;

    /** QualificationMap for use if we are working with a code gem. */
    private final CodeQualificationMap qualificationMap;
    
    /** Flag to indicate that a conflicting name has been found (so that we only report it once) */
    private boolean conflictingNameFound = false;
    
    /** 
     * Constructs a RenamedIdentifierFinder
     * @param oldName The original name of the entity to rename
     * @param newName The name to rename 
     * @param category The category of the entity being renamed
     */
    RenamedIdentifierFinder(QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category) {
        
        if (oldName == null || newName == null || category == null) {
            throw new NullPointerException();
        }
        this.renameOldName = oldName;
        this.renameNewName = newName;
        this.renameCategory = category;
        this.qualificationMap = null;
    }
    
    /** 
     * Constructs a RenamedIdentifierFinder
     * @param oldName The original name of the entity to rename
     * @param newName The name to rename 
     * @param category The category of the entity being renamed
     * @param qualificationMap The qualification map to use if one exists, or null if it does not. 
     */
    RenamedIdentifierFinder(QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category, CodeQualificationMap qualificationMap) {
        if (oldName == null || newName == null || category == null) {
            throw new NullPointerException();
        }
        this.renameOldName = oldName;
        this.renameNewName = newName;
        this.renameCategory = category;
        this.qualificationMap = qualificationMap;
        
    }

    /**
     * Collect renamings within a Let declaration. 
     * A form of the conflict resolution mechanism described above is implemented in this method. 
     * 
     */
    @Override
    void findIdentifiersInLet(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode parseTree) {

        // : Renaming S to R, resolving any conflicts that R may have with local vars
        ModuleName mS = renameOldName.getModuleName();
        String S = renameOldName.getUnqualifiedName(), R = renameNewName.getUnqualifiedName();
        
        ParseTreeNode defnListNode = parseTree.firstChild();
        defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);
    
        // Check that S is not on the stack; if it is, then it is a local binding so we don't care about 
        // it and so we have nothing to do in this let.
        if (mS.equals(getCurrentModuleName()) && boundVariablesStack.contains(S)) {
            findIdentifiersInCALDocCommentsForLocalDefnsAndNestedExprs(identifierList, boundVariablesStack, defnListNode);
            return;
        }
        
        // First, collect all the optional CALDoc nodes (for later checking)
        Map<String, ParseTreeNode> funcNamesToOptionalCALDocNodes = new HashMap<String, ParseTreeNode>();
        
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
        
        // Look at local binding declarations, to know the variables defined at this level
        
        int nLocalSCs = 0;
        ParseTreeNode rLocalDeclNameNode = null;
        ParseTreeNode rTypeDecl = null;
        int rStackPos = -1;
        for (final ParseTreeNode defnNode : defnListNode) {

            switch (defnNode.getType()) {
                case (CALTreeParserTokenTypes.LET_DEFN): 
                {
                    ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                    optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                    // we defer the checking of the CALDoc to "Part 1" below, as part of findIdentifiersInLambda
                    
                    ParseTreeNode localSCNameNode = optionalCALDocNode.nextSibling();
                    localSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                    String scName = localSCNameNode.getText();
                    
                    // scName is a bound variable for all declarations in the 'let' and for the expression following the 'in'.
                    boundVariablesStack.push(scName);
                    nLocalSCs++;
                    
                    // If this is defining local variable R, renaming of S to R will
                    // clash if S is present. So remember this location, for future use.
                    // Note: There is at most one TYPE_DECLARATION and LET_DEFN per variable
                    if (scName.equals(R)) {
                        rLocalDeclNameNode = localSCNameNode;
                        rStackPos = boundVariablesStack.size() - 1;
                    }
                    break;
                }
            
                case (CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION):
                {
                    // Check potential clash with variable named R again
                    ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                    optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                    // we defer the checking of the CALDoc to "Part 1" below, as part of findIdentifiersInLambda
                    
                    ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                    typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                     
                    ParseTreeNode localSCNameNode = typeDeclNode.firstChild();
                    localSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                    String scName = localSCNameNode.getText();
                    if (scName.equals(R)) {
                        rTypeDecl = localSCNameNode;
                    }
                    
                    findIdentifiersInTypeDecl(identifierList, typeDeclNode);
                    break;
                }
                
                case CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL:
                {
                    // A local pattern match declaration can declare one or more locally bound variables
                    // We will loop through the pattern to process each one
                    
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
                                        
                                        final String scName = patternVarNode.getText();
                                        
                                        // scName is a bound variable for all declarations in the 'let' and for the expression following the 'in'.
                                        boundVariablesStack.push(scName);
                                        nLocalSCs++;
                                        
                                        // If this is defining local variable R, renaming of S to R will
                                        // clash if S is present. So remember this location, for future use.
                                        // Note: There is at most one TYPE_DECLARATION and LET_DEFN per variable
                                        if (scName.equals(R)) {
                                            rLocalDeclNameNode = patternVarNode;
                                            rStackPos = boundVariablesStack.size() - 1;
                                        }
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
                                        
                                        final String scName = patternVarNode.getText();
                                        
                                        // scName is a bound variable for all declarations in the 'let' and for the expression following the 'in'.
                                        boundVariablesStack.push(scName);
                                        nLocalSCs++;
                                        
                                        // If this is defining local variable R, renaming of S to R will
                                        // clash if S is present. So remember this location, for future use.
                                        // Note: There is at most one TYPE_DECLARATION and LET_DEFN per variable
                                        if (scName.equals(R)) {
                                            rLocalDeclNameNode = patternVarNode;
                                            rStackPos = boundVariablesStack.size() - 1;
                                        }
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
                                    
                                    final String scName = patternVarNode.getText();
                                    
                                    // scName is a bound variable for all declarations in the 'let' and for the expression following the 'in'.
                                    boundVariablesStack.push(scName);
                                    nLocalSCs++;
                                    
                                    // If this is defining local variable R, renaming of S to R will
                                    // clash if S is present. So remember this location, for future use.
                                    // Note: There is at most one TYPE_DECLARATION and LET_DEFN per variable
                                    if (scName.equals(R)) {
                                        rLocalDeclNameNode = patternVarNode;
                                        rStackPos = boundVariablesStack.size() - 1;
                                    }
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
                                    
                                    final String scName = patternVarNode.getText();
                                    
                                    // scName is a bound variable for all declarations in the 'let' and for the expression following the 'in'.
                                    boundVariablesStack.push(scName);
                                    nLocalSCs++;
                                    
                                    // If this is defining local variable R, renaming of S to R will
                                    // clash if S is present. So remember this location, for future use.
                                    // Note: There is at most one TYPE_DECLARATION and LET_DEFN per variable
                                    if (scName.equals(R)) {
                                        rLocalDeclNameNode = patternVarNode;
                                        rStackPos = boundVariablesStack.size() - 1;
                                    }
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
                    // Unexpected node type
                    defnListNode.unexpectedParseTreeNode();
                    break;
                }
            }
        }
        
        // If S has just been bound, then any references to S will be to the local variable,
        // so there is nothing for us to do here, beside performing finding the renamings for the CALDoc 
        // comments which were previously skipped over.
        if (mS.equals(getCurrentModuleName()) && boundVariablesStack.contains(S)) {
            findIdentifiersInCALDocCommentsForLocalDefnsAndNestedExprs(identifierList, boundVariablesStack, defnListNode);
            return;
        }
        
        // Part 1: Find replacements at lower levels
        
        // Look in body of local variable declarations, and let declaration, and collect S-to-R replacements
        // (and other renamings if conflicts occur inside)
        
        int oldIdentifierCount = identifierList.size();
        for (final ParseTreeNode defnNode : defnListNode) {

            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                ParseTreeNode localFunctionNameNode = defnNode.getChild(1);
                localFunctionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String scName = localFunctionNameNode.getText();
                
                ParseTreeNode varListNode = localFunctionNameNode.nextSibling();
                
                // get the optional CALDoc node stored in the map.
                // this may be the CALDoc associated with the corresponding function type declaration, or if
                // there is none, the one associated with this function definition itself.
                ParseTreeNode optionalCALDocNodeFromMap = funcNamesToOptionalCALDocNodes.get(scName);
                    
                // here, we not only process the body of the local definition, but also its CALDoc comment,
                // since its @arg tags may be affected by the renaming of the parameters.
                findIdentifiersInLambda(identifierList, boundVariablesStack, varListNode, varListNode.nextSibling(), optionalCALDocNodeFromMap);
                
            } else if (defnNode.getType() == CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL) {
                
                final ParseTreeNode patternMatchPatternNode = defnNode.firstChild();
                final ParseTreeNode patternMatchExprNode = patternMatchPatternNode.nextSibling();
                
                findIdentifiersInLambda(identifierList, boundVariablesStack, null, patternMatchExprNode);
            }
        }
        ParseTreeNode exprNode = defnListNode.nextSibling();
        findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);

        // Part 2: Resolve conflicts at this level
        
        if (!(mS.equals(getCurrentModuleName()) || mS.equals(getModuleForImportedFunctionOrClassMethod(S))) || (rLocalDeclNameNode == null) || (oldIdentifierCount == identifierList.size())) {
            // We do not try to resolve conflicts if either:
            // 1. S cannot appear in unqualified form, and not thus conflicting with local variables
            // 2. the list of identifiers has not changed, thus no occurrences of S
            //    have been found and renamed
            // 3. R is not defined at our level so we have no conflicts to resolve here
            
            boundVariablesStack.popN(nLocalSCs); 
            return;
        }
        
        // Will rename the local variable R by appending a suffix
        
        int pad = 2;
        String R2 = "";
        
        // Repeat the following section until we find a variable name which
        // does not cause a conflict with others at this or inner levels
        boolean stillLooking = true; 
        while (stillLooking) {
            R2 = rLocalDeclNameNode.getText() + pad++;
            // Make sure we don't chose a name conflicting with something already defined
            // in this or outer scope
            while (boundVariablesStack.contains(R2)) {
                R2 = rLocalDeclNameNode.getText() + pad++;
            }
            
            // Now make sure this choice doesn't cause conflicts down the road (ie: renaming R to R2
            // will not conflict R2 with local variables in inner scopes)
            
            // Change to RenameConflict finding mode (R-R2)
            RenameConflictFinder rr2ConflictFinder = 
                new RenameConflictFinder(getCurrentModuleName(), QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
            
            // Declare new stack because otherwise RCF will think conflicting thoughts
            // Note: Bound variables at this and upper levels have already been checked 
            //       not to contain R2
            ArrayStack<String> newBoundVariablesStack = ArrayStack.make();  
            List<SourceIdentifier> conflictingIdentifiers = new ArrayList<SourceIdentifier>();
            
            for (final ParseTreeNode defnNode : defnListNode) {
                
                if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                    ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                    optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                    
                    ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                    ParseTreeNode varListNode = localFunctionNameNode.nextSibling();
                    rr2ConflictFinder.findIdentifiersInLambda(conflictingIdentifiers, newBoundVariablesStack, varListNode, varListNode.nextSibling());
                    
                } else if (defnNode.getType() == CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL) {
                    
                    defnNode.verifyType(CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL);

                    final ParseTreeNode patternMatchPatternNode = defnNode.firstChild();
                    final ParseTreeNode patternMatchExprNode = patternMatchPatternNode.nextSibling();

                    rr2ConflictFinder.findIdentifiersInLambda(conflictingIdentifiers, newBoundVariablesStack, null, patternMatchExprNode);
                }
            }
            rr2ConflictFinder.findIdentifiersInExpr(conflictingIdentifiers, newBoundVariablesStack, exprNode);
            getLogger().logMessages(rr2ConflictFinder.getLogger());
            
            // Continue looking for a new variable name until there are no conflicts
            stillLooking = !conflictingIdentifiers.isEmpty();
        }
        
        // Now, R2 is a non-conflicting name, so replace all occurrences of R with R2

        // Rename declaration
        identifierList.add(new SourceModification.ReplaceText(rLocalDeclNameNode.getText(), R2, rLocalDeclNameNode.getSourcePosition()));
        if (rTypeDecl != null) {
            identifierList.add(new SourceModification.ReplaceText(rTypeDecl.getText(), R2, rTypeDecl.getSourcePosition()));
        }
        
        // Rename R to R2 on the bindings stack 
        //   Note: (1) R2 does not exist in local vars already because we have checked
        //         (2) R2 as a local var from this or upper levels is not referenced from below because (1)
        //         (3) renaming R to R2 does not clash below with any local variable, because we checked
        boundVariablesStack.set(rStackPos, R2);
        
        // TODO: See if this can be improved to less hackish
        // Now, on the stack there may be other occurrences of R, which will not allow R-R2 renaming to
        // occur (since we check local bindings). These occurrences will be renamed as we move out, 
        // but for now we will rename them to R2 (this does nothing since we already have R2 on the stack)
        List<Integer> stackSpotsRenamed = new ArrayList<Integer>();
        for (int i = 0, n = boundVariablesStack.size(); i < n; i++) {
            if ((boundVariablesStack.get(i)).equals(R)) {
                stackSpotsRenamed.add(Integer.valueOf(i));
                boundVariablesStack.set(i, R2);
            }
        }
                
        // Rename R-R2 in lower levels
        // (Note: this should not go through any Part 2s, since there are no conflicts)
        RenamedIdentifierFinder rr2RenamingFinder = new RenamedIdentifierFinder(QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
        rr2RenamingFinder.setCurrentModuleName(getCurrentModuleName());
        for (final ParseTreeNode defnNode : defnListNode) {
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                ParseTreeNode varListNode = localFunctionNameNode.nextSibling();
                rr2RenamingFinder.findIdentifiersInLambda(identifierList, boundVariablesStack, varListNode, varListNode.nextSibling());
                
            } else if (defnNode.getType() == CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL) {
                
                defnNode.verifyType(CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL);

                final ParseTreeNode patternMatchPatternNode = defnNode.firstChild();
                final ParseTreeNode patternMatchExprNode = patternMatchPatternNode.nextSibling();

                rr2RenamingFinder.findIdentifiersInLambda(identifierList, boundVariablesStack, null, patternMatchExprNode);
            }
        }
        rr2RenamingFinder.findIdentifiersInExpr(identifierList, boundVariablesStack, exprNode);
        getLogger().logMessages(rr2RenamingFinder.getLogger());
        
        // Put back Rs on the stack
        for (int i = 0, n = stackSpotsRenamed.size(); i < n; i++) {
            boundVariablesStack.set(i, R);
        }
        boundVariablesStack.popN(nLocalSCs);
    }

    /**
     * Collect renamings within a CALDoc comment associated with a local definition. 
     */
    private void findIdentifiersInCALDocCommentAssociatedWithLocalDefn(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode defnNode) {
        ParseTreeNode optionalCALDocNode = defnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
        ParseTreeNode varListNode = localFunctionNameNode.nextSibling();
        
        int nVars = 0;
        if (varListNode != null) {
            for (final ParseTreeNode patternVarNode : varListNode) {
    
                switch (patternVarNode.getType()) {
    
                    case CALTreeParserTokenTypes.VAR_ID:
                    case CALTreeParserTokenTypes.LAZY_PARAM:
                    case CALTreeParserTokenTypes.STRICT_PARAM:                
                    {
                        String varName = patternVarNode.getText();
                        ++nVars;
                        
                        // varName is now a bound variable for the body of the
                        // lambda
                        boundVariablesStack.push(varName);
                        break;
                    }
                    
                    case CALTreeParserTokenTypes.UNDERSCORE:
                        break;
                        
                    default :
                        // Unexpected type
                        boundVariablesStack.popN(nVars);
                        patternVarNode.unexpectedParseTreeNode();                        
                        return;
                }
            }
        }
        
        findIdentifiersInCALDocComment(identifierList, boundVariablesStack, optionalCALDocNode);
        boundVariablesStack.popN(nVars);
    }

    /**
     * Collect renamings within the CALDoc comments associated with a local
     * function definitions and type declarations, as well as any CALDoc
     * comments contained in the defining expressions of these local functions.
     */
    void findIdentifiersInCALDocCommentsForLocalDefnsAndNestedExprs(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode defnListNode) {
        
        Map<String, ParseTreeNode> funcNamesToDefnNodes = new HashMap<String, ParseTreeNode>();
        
        // First pass: gather the function definition nodes
        for (final ParseTreeNode defnNode : defnListNode) {
            
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localSCNameNode = optionalCALDocNode.nextSibling();
                localSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String scName = localSCNameNode.getText();
                
                funcNamesToDefnNodes.put(scName, defnNode);
            }
        }
        
        // Second pass: perform the checks on CALDocs and also check into the defining expressions
        for (final ParseTreeNode defnNode : defnListNode) {
        
            defnNode.verifyType(CALTreeParserTokenTypes.LET_DEFN, CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION);
            
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                // check the CALDoc
                findIdentifiersInCALDocCommentAssociatedWithLocalDefn(identifierList, boundVariablesStack, defnNode);

                // check the bound expression for nested let blocks with CALDoc comments
                ParseTreeNode inExprNode = defnNode.getChild(3);
                findIdentifiersInExpr(identifierList, boundVariablesStack, inExprNode);
                
            } else { // must be a LET_DEFN_TYPE_DECLARATION
                
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                 
                ParseTreeNode localSCNameNode = typeDeclNode.firstChild();
                localSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String scName = localSCNameNode.getText();
                
                ParseTreeNode funcDefnNode = funcNamesToDefnNodes.get(scName);
                
                if (funcDefnNode != null) {
                    // check the CALDoc with the function's definition node
                    findIdentifiersInCALDocCommentAssociatedWithLocalDefn(identifierList, boundVariablesStack, funcDefnNode);
                } else {
                    getLogger().logMessage(new CompilerMessage(defnNode, new MessageKind.Error.DefinitionMissing(scName)));
                }
            }
        }
    }

    /**
     * Collect renamings within an SC declaration. There can be no conflicts here.
     */
    @Override
    void findIdentifiersInFunctionDeclaration(List<SourceModification> identifierList, ParseTreeNode scTopLevelTypeDeclarationNode) {

        scTopLevelTypeDeclarationNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION);
                  
        ParseTreeNode optionalCALDocNode = scTopLevelTypeDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode scTypeDeclarationNode = optionalCALDocNode.nextSibling();
        ParseTreeNode scNameNode = scTypeDeclarationNode.firstChild();
        visitFunctionOrClassMethodDefinitionNameNode(identifierList, scNameNode);
        
        ParseTreeNode typeSignatureNode = scNameNode.nextSibling();
        findIdentifiersInTypeSignature(identifierList, typeSignatureNode);
    }
    
    /**
     * Collect renamings within a SC definition. 
     * A form of the conflict resolution mechanism described above is implemented in this method.    
     */
    @Override
    void findIdentifiersInFunctionDefinition(List<SourceModification> identifierList, ParseTreeNode scNode) {
    
        // : Renaming S to R, in SC definition
        ModuleName mS = renameOldName.getModuleName();
        String S = renameOldName.getUnqualifiedName(), R = renameNewName.getUnqualifiedName();
        
        scNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);
        
        ParseTreeNode optionalCALDocNode = scNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        findIdentifiersInCALDocComment(identifierList, optionalCALDocNode);
        
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        
        ParseTreeNode scNameNode = accessModifierNode.nextSibling();
        visitFunctionOrClassMethodDefinitionNameNode(identifierList, scNameNode);
        
        // Find local argument bindings
        
        ParseTreeNode conflictingArgNode = null;
        int conflictingArgPos = -1;
        ArrayStack<String> namedArgumentsStack = ArrayStack.make();
        ParseTreeNode paramListNode = accessModifierNode.nextSibling().nextSibling();
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
        for (final ParseTreeNode varNode : paramListNode) {

            varNode.verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);
            String varName = varNode.getText();
            namedArgumentsStack.push(varName);
            
            // If an argument has the name R, it will conflict with the rename if the SC definition
            // contains S; thus, keep track of this argument.
            if (varName.equals(R)) {
                conflictingArgNode = varNode;
                conflictingArgPos = namedArgumentsStack.size() - 1;
            }
        }
        
        // S has just been defined as a sc argument, so will ignore any renamings
        if (mS.equals(getCurrentModuleName()) && namedArgumentsStack.contains(S)) {
            return;
        }
        
        // Part 1 : Collect renamings (S-R, and local conflict resolutions) from the SC declaration
         
        int oldIdentifierCount = identifierList.size();
        findIdentifiersInExpr(identifierList, namedArgumentsStack, paramListNode.nextSibling());
        
        // Part 2 : Resolve conflicts between R and local arguments
        
        if (!(mS.equals(getCurrentModuleName()) || mS.equals(getModuleForImportedFunctionOrClassMethod(S))) || (conflictingArgNode == null) || (oldIdentifierCount == identifierList.size())) {
            // We do not try to resolve conflicts if either:
            // 1. S cannot appear in unqualified form, and not conflicting with local variables
            // 2. the list of identifiers has not changed, thus no occurrences of S
            //    have been found and renamed
            // 3. R is not defined at our level so we have no conflicts to resolve here
            return;
        }
        
        // Will repeat the following section until we find a variable name which
        // does not cause a conflict with others at this or inner levels
        boolean keepSearching = true;
        int pad = 2;
        String R2 = "";
        while (keepSearching) {
            R2 = conflictingArgNode.getText() + pad++;
            
            // Make sure we don't chose a name conflicting with an argument defined
            // in this or outer scope
            while (namedArgumentsStack.contains(R2)) {
                R2 = conflictingArgNode.getText() + pad++;
            }
            
            // Now make sure this choice doesn't cause conflicts down the road (ie: renaming R to R2
            // will not conflict R2 with local variables in inner scopes)
            
            // Change mode to RenameConflictFinder (renaming R to R2)
            RenameConflictFinder rr2ConflictFinder = new RenameConflictFinder(getCurrentModuleName(), QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
            ArrayStack<String> newBoundVariablesStack = ArrayStack.make();
            List<SourceIdentifier> conflictingIdentifiers = new ArrayList<SourceIdentifier>();
            rr2ConflictFinder.findIdentifiersInExpr(conflictingIdentifiers, newBoundVariablesStack, paramListNode.nextSibling());
            getLogger().logMessages(rr2ConflictFinder.getLogger());
            
            // Continue looking for a new variable name until there are no conflicts
            keepSearching = !conflictingIdentifiers.isEmpty();
        } 
        
        // Now, R2 is a non-conflicting name, so replace all occurrences of R with R2

        // Mark argument definition to be renamed (R-R2)
        identifierList.add(new SourceModification.ReplaceText(conflictingArgNode.getText(), R2 ,conflictingArgNode.getSourcePosition())); 
        namedArgumentsStack.set(conflictingArgPos, R2);
                
        // Change mode to RF, R-R2
        // (Note: this should not go through any Part 2s, since there are no conflicts)
        RenamedIdentifierFinder rr2RenamingFinder = new RenamedIdentifierFinder(QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
        rr2RenamingFinder.setCurrentModuleName(getCurrentModuleName());
        rr2RenamingFinder.findIdentifiersInExpr(identifierList, namedArgumentsStack, paramListNode.nextSibling());
        getLogger().logMessages(rr2RenamingFinder.getLogger());
    }

    /**
     * Collect renamings within a Lambda declaration. 
     * A form of the conflict resolution mechanism described above is implemented in this method.
     * 
     * @param identifierList
     * @param boundVariablesStack
     * @param patternVarListNode
     * @param boundExprNode
     * @param optionalCALDocNodeForLetDefn
     */
    @Override
    void findIdentifiersInLambda(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode patternVarListNode, ParseTreeNode boundExprNode, ParseTreeNode optionalCALDocNodeForLetDefn) {

        // : Renaming S to R, resolving any conflicts that R may have with local vars
        ModuleName mS = renameOldName.getModuleName();
        String S = renameOldName.getUnqualifiedName(), R = renameNewName.getUnqualifiedName();
        
        ParseTreeNode conflictingVarNode = null;
        int conflictingVarPos = -1;
        int nVars = 0;
        if (patternVarListNode != null) {
            for (final ParseTreeNode patternVarNode : patternVarListNode) {
                
                switch (patternVarNode.getType()) {
                    
                    case CALTreeParserTokenTypes.VAR_ID:
                    case CALTreeParserTokenTypes.LAZY_PARAM:
                    case CALTreeParserTokenTypes.STRICT_PARAM:                
                    {
                        String varName = patternVarNode.getText();
                        ++nVars;
                        
                        // varName is now a bound variable for the body of the
                        // lambda
                        boundVariablesStack.push(varName);
                        
                        if (varName.equals(R)) {
                            // This variable is a potential conflict if we find any S in the body,
                            // so keep it in case we need to rename it
                            conflictingVarNode = patternVarNode;
                            conflictingVarPos = boundVariablesStack.size() - 1;
                        }
                        
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
        
        // If S was already on the stack, or has just been defined as a lambda argument, will ignore any renamings
        if (mS.equals(getCurrentModuleName()) && boundVariablesStack.contains(S)) {
            // check the CALDoc
            if (optionalCALDocNodeForLetDefn != null) {
                findIdentifiersInCALDocComment(identifierList, boundVariablesStack, optionalCALDocNodeForLetDefn);
            }
            // and also check the bound expression for nested let blocks with CALDoc comments
            findIdentifiersInExpr(identifierList, boundVariablesStack, boundExprNode);
            boundVariablesStack.popN(nVars);
            return;
        }

        // Part 1: Find replacements at lower levels
        
        int oldIdentifierListSize = identifierList.size();
        findIdentifiersInExpr(identifierList, boundVariablesStack, boundExprNode);
        
        // Part 2: Resolve conflicts at this level
        
        if (!(mS.equals(getCurrentModuleName()) || mS.equals(getModuleForImportedFunctionOrClassMethod(S))) || (conflictingVarNode == null) || (oldIdentifierListSize == identifierList.size())) {
            // We do not try to resolve conflicts if either:
            // 1. S cannot appear in unqualified form and not conflicting with local variables
            // 2. the list of identifiers has not changed, thus no occurrences of S
            //    have been found and renamed
            // 3. R is not defined at our level so we have no conflicts to resolve here
            
            if (optionalCALDocNodeForLetDefn != null) {
                // just check the CALDoc, since the bound expression has already been traversed
                findIdentifiersInCALDocComment(identifierList, boundVariablesStack, optionalCALDocNodeForLetDefn);
            }
            boundVariablesStack.popN(nVars); 
            return;
        }
        
        // Will rename the local variable R by appending a suffix
        
        int pad = 2;
        String R2 = "";
        
        // Will repeat the following section until we find a variable name which
        // does not cause a conflict with others at this or inner levels
        boolean stillLooking = true; 
        while (stillLooking) {
            R2 = conflictingVarNode.getText() + pad++;
            // Make sure we don't chose a name conflicting with something already defined
            // in this or outer scope
            while (boundVariablesStack.contains(R2)) {
                R2 = conflictingVarNode.getText() + pad++;
            }
            
            // Now make sure this choice doesn't cause conflicts down the road (ie: renaming R to R2
            // will not conflict R2 with local variables in inner scopes)
            
            // Change to RenameConflict finding mode (R-R2)
            RenameConflictFinder rr2ConflictFinder = new RenameConflictFinder(getCurrentModuleName(), QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
            ArrayStack<String> newBoundVariablesStack = ArrayStack.make();  
            List<SourceIdentifier> conflictingIdentifiers = new ArrayList<SourceIdentifier>();
            rr2ConflictFinder.findIdentifiersInExpr(conflictingIdentifiers, newBoundVariablesStack, boundExprNode);
            getLogger().logMessages(rr2ConflictFinder.getLogger());
            
            // Continue looking for a new variable name until there are no conflicts
            stillLooking = !conflictingIdentifiers.isEmpty();
        }
        
        // Now, R2 is a non-conflicting name, so replace all occurrences of R with R2

        identifierList.add(new SourceModification.ReplaceText(conflictingVarNode.getText(), R2, conflictingVarNode.getSourcePosition()));
        boundVariablesStack.set(conflictingVarPos, R2);
        
        // TODO: See if this can be improved to less hackish
        // Now, on the stack there may be other occurrences of R, which will not allow R-R2 renaming to
        // occur (since we check local bindings). These occurrences will be renamed as we move out, 
        // but for now we will rename them to R2 (this does nothing since we already have R2 on the stack)
        List<Integer> stackSpotsRenamed = new ArrayList<Integer>();
        for (int i = 0, n = boundVariablesStack.size(); i < n; i++) {
            if ((boundVariablesStack.get(i)).equals(R)) {
                stackSpotsRenamed.add(Integer.valueOf(i));
                boundVariablesStack.set(i, R2);
            }
        }
                
        // Change mode to FindRenamings (R-R2)
        // (Note: this should not go through any Part 2s, since there are no conflicts) 
        RenamedIdentifierFinder rr2RenamingFinder = new RenamedIdentifierFinder(QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
        rr2RenamingFinder.setCurrentModuleName(getCurrentModuleName());
        rr2RenamingFinder.findIdentifiersInExpr(identifierList, boundVariablesStack, boundExprNode);
        getLogger().logMessages(rr2RenamingFinder.getLogger());
        
        // Make the appropriate R-R2 renamings in the CALDoc for the let definition which is being
        // checked here (pretending to be a lambda).
        if (optionalCALDocNodeForLetDefn != null) {
            CALDocArgNameRenamingFinder caldocRR2RenamingFinder = new CALDocArgNameRenamingFinder(getCurrentModuleName(), QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
            caldocRR2RenamingFinder.findIdentifiersInCALDocComment(identifierList, boundVariablesStack, optionalCALDocNodeForLetDefn);
            getLogger().logMessages(caldocRR2RenamingFinder.getLogger());
        }
        
        // Put back Rs on the stack
        for (int i = 0, n = stackSpotsRenamed.size(); i < n; i++) {
            boundVariablesStack.set(i, R);
        }
        
        boundVariablesStack.popN(nVars);
        return;
    }
    
    /**
     * Finds identifiers in a case expression containing a field binding pattern.
     * A form of the conflict resolution mechanism described above is implemented in this method,
     * because local variables may be bound to field bindings
     * 
     * @param identifierList
     * @param boundVariablesStack
     * @param fieldBindingVarAssignmentListNode
     * @param boundExprNode
     * @param basePatternVarNameNode if non-null, the parse tree node for the named variable forming the base pattern for the field binding.   
     */
    @Override
    void findIdentifiersInFieldBindingCase(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode fieldBindingVarAssignmentListNode, ParseTreeNode boundExprNode, ParseTreeNode basePatternVarNameNode) {
        // : Renaming S to R, resolving any conflicts that R may have with local vars
        ModuleName mS = renameOldName.getModuleName();
        String S = renameOldName.getUnqualifiedName(), R = renameNewName.getUnqualifiedName();
        
        // Check that S is not on the stack; if it is, then it is a local 
        // binding so we don't care about it and so we have nothing to do in this
        // case.
        if (mS.equals(getCurrentModuleName()) && boundVariablesStack.contains(S)) {
            return;
        }
        
        ParseTreeNode conflictingVarNode = null;
        int conflictingVarPos = -1;
        boolean conflictingVarPunned = false;
        
        // Fill pattern variables for punned fields

        // Note that for an unpunned textual field name, we can still tell that this is a punned field 
        // since the fieldNameNode and patternVarNode have the same sourcePosition. 
        // We will use this fact later on if we need to un-pun the field to resolve a renaming conflict.
        
        unpunPunnedFields(fieldBindingVarAssignmentListNode);
        
        // Inspect variable bindings, and keep track if any may conflict with our renaming 
        
        int nVars;
        if (basePatternVarNameNode != null) {
            nVars = 1;
            String basePatternVarName = basePatternVarNameNode.getText();
            boundVariablesStack.push(basePatternVarName);
            if (basePatternVarName.equals(R)) {
                // This variable is a potential conflict if we find any S in the body,
                // so keep it in case we need to rename it
                conflictingVarNode = basePatternVarNameNode;
                conflictingVarPos = boundVariablesStack.size() - 1;
            }
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
                    String patternVar = patternVarNode.getText();                        
                                        
                    ++nVars;                                                    
                    boundVariablesStack.push(patternVar);
                    
                    if (patternVar.equals(R)) {
                        // This variable is a potential conflict if we find any S in the body,
                        // so keep it in case we need to rename it
                        conflictingVarNode = patternVarNode;
                        conflictingVarPos = boundVariablesStack.size() - 1;
                        
                        // Make a note of whether we manually unpunned this node. We check this by looking to see
                        // if the patternVar and fieldName share the same source position.
                        conflictingVarPunned = patternVarNode.getSourcePosition().equals(fieldNameNode.getSourcePosition());
                    }
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
        
        // If S has just been defined as a case variable, will ignore any renamings
        if (mS.equals(getCurrentModuleName()) && boundVariablesStack.contains(S)) {
            boundVariablesStack.popN(nVars);
            return;
        }

        // Part 1: Find replacements at lower levels
        
        int oldIdentifierListSize = identifierList.size();
        findIdentifiersInExpr(identifierList, boundVariablesStack, boundExprNode);
        
        // Part 2: Resolve conflicts at this level
        
        if (!(mS.equals(getCurrentModuleName()) || mS.equals(getModuleForImportedFunctionOrClassMethod(S))) || (conflictingVarNode == null) || (oldIdentifierListSize == identifierList.size())) {
            // We do not try to resolve conflicts if either:
            // 1. S cannot appear in unqualified form, conflicting with local variables
            // 2. the list of identifiers has not changed, thus no occurrences of S
            //    have been found and renamed
            // 3. R is not defined at our level so we have no conflicts to resolve here
            
            boundVariablesStack.popN(nVars); 
            return;
        }
        
        // Will rename the local variable R by appending a suffix
        
        int pad = 2;
        String R2 = "";
        
        // Will repeat the following section until we find a variable name which
        // does not cause a conflict with others at this or inner levels
        boolean stillLooking = true; 
        while (stillLooking) {
            R2 = conflictingVarNode.getText() + pad++;
            // Make sure we don't chose a name conflicting with something already defined
            // in this or outer scope
            while (boundVariablesStack.contains(R2)) {
                R2 = conflictingVarNode.getText() + pad++;
            }
            
            // Now make sure this choice doesn't cause conflicts down the road (ie: renaming R to R2
            // will not conflict R2 with local variables in inner scopes)
            
            // Change to RenameConflict finding mode (R-R2)
            RenameConflictFinder rr2ConflictFinder = new RenameConflictFinder(getCurrentModuleName(), QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
            ArrayStack<String> newBoundVariablesStack = ArrayStack.make();  
            List<SourceIdentifier> conflictingIdentifiers = new ArrayList<SourceIdentifier>();
            rr2ConflictFinder.findIdentifiersInExpr(conflictingIdentifiers, newBoundVariablesStack, boundExprNode);
            getLogger().logMessages(rr2ConflictFinder .getLogger());
            
            // Continue looking for a new variable name until there are no conflicts
            stillLooking = !conflictingIdentifiers.isEmpty();
        }
        
        // Now, R2 is a non-conflicting name, so replace all occurrences of R with R2
        if (conflictingVarPunned) {
            // If this was a punned field, we must now unpun it to resolve the conflict
            identifierList.add(new SourceModification.ReplaceText(conflictingVarNode.getText(), conflictingVarNode.getText() + " = " + R2, conflictingVarNode.getSourcePosition()));
        } else {
            identifierList.add(new SourceModification.ReplaceText(conflictingVarNode.getText(), R2, conflictingVarNode.getSourcePosition()));
        }
        boundVariablesStack.set(conflictingVarPos, R2);
        
        // TODO: See if this can be improved to less hackish
        // Now, on the stack there may be other occurrences of R, which will not allow R-R2 renaming to
        // occur (since we check local bindings). These occurrences will be renamed as we move out, 
        // but for now we will rename them to R2 (this does nothing since we already have R2 on the stack)
        List<Integer> stackSpotsRenamed = new ArrayList<Integer>();
        for (int i = 0, n = boundVariablesStack.size(); i < n; i++) {
            if ((boundVariablesStack.get(i)).equals(R)) {
                stackSpotsRenamed.add(Integer.valueOf(i));
                boundVariablesStack.set(i, R2);
            }
        }
                
        // Change mode to FindRenamings (R-R2)
        // (Note: this should not go through any Part 2s, since there are no conflicts) 
        RenamedIdentifierFinder rr2RenamingFinder = new RenamedIdentifierFinder(QualifiedName.make(getCurrentModuleName(), R), QualifiedName.make(getCurrentModuleName(), R2), renameCategory);
        rr2RenamingFinder.setCurrentModuleName(getCurrentModuleName());
        rr2RenamingFinder.findIdentifiersInExpr(identifierList, boundVariablesStack, boundExprNode);
        getLogger().logMessages(rr2RenamingFinder.getLogger());
        
        // Put back Rs on the stack
        for (int i = 0, n = stackSpotsRenamed.size(); i < n; i++) {
            boundVariablesStack.set(i, R);
        }
        
        boundVariablesStack.popN(nVars);
    }

    /**
     * Given an unqualified identifier, determine what module it is defined in.
     * The method checks the qualification map (if it exists), then looks at "import using" clauses.
     * If the identifier does not occur in either of these places, we conclude that it is from the current module.  
     * @param identifierName The unqualified identifier that we want to get the module for. 
     * @param identifierCategory The type of the identifier.
     * @return The module that the entity represented by this identifier is defined in.
     */
    private ModuleName getModuleForUnqualifiedIdentifier(String identifierName, SourceIdentifier.Category identifierCategory) {
        // First, check the qualification map, if one exists..
        if (qualificationMap != null) {
            QualifiedName qualificationMapName = qualificationMap.getQualifiedName(identifierName, identifierCategory); 
            if (qualificationMapName != null) {
                return qualificationMapName.getModuleName();
            }
        }
        
        // Otherwise, check to see if the symbol has been imported with an "import using..." clause
        ModuleName moduleName = getModuleForImportedIdentifier(identifierName, identifierCategory); 
        
        // If the symbol has not been imported and is not in the qualification map, then default to the current module.
        return (moduleName == null) ? getCurrentModuleName() : moduleName;
    }

    /**
     * Retrieves the module associated with the import using statement which imported the specified identifier.
     * Null is returned if the identifier does not appear in any import using statement.
     * @param identifierName the unqualified identifier that we want to get the module for. 
     * @param identifierCategory the type of the identifier.
     * @return the module associated with the import using statement which imported the specified identifier, or null
     *         if the identifier does not appear in any import using statement.
     */
    private ModuleName getModuleForImportedIdentifier(String identifierName, SourceIdentifier.Category identifierCategory) {
        if (identifierCategory == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            return getModuleForImportedFunctionOrClassMethod(identifierName) ;
        } else if (identifierCategory == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
            return getModuleForImportedDataConstructor(identifierName) ;
        } else if (identifierCategory == SourceIdentifier.Category.TYPE_CLASS) {
            return getModuleForImportedTypeClass(identifierName) ;
        } else if (identifierCategory == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
            return getModuleForImportedTypeConstructor(identifierName) ;
        } else {
            return null;
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node refers the SC or Class Method being renamed.
     * 
     * Note: Local bindings are not checked because the conflict resolution algorithms ensures
     * no conflicts occur (progressing outwards).  
     */
    @Override
    void visitFunctionOrClassMethodNameNode(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode qualifiedNode) {

        qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
        ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
        visitModuleNameNode(identifierList, moduleNameNode);
        ModuleName moduleName = getResolvedModuleName(moduleNameNode);
        ParseTreeNode varNameNode = moduleNameNode.nextSibling();
        String varName = varNameNode.getText();
        
        if (
            ((moduleName == null) || (moduleName.equals(getCurrentModuleName()))) &&
            boundVariablesStack.contains(varName)) {

            // Variable is pattern bound (i.e. an argument variable, a lambda bound 
            // variable or a variable bound by a case alternative). Its module is the current module.
            return;
        }

        if ( varName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) &&
            ( renameOldName.getModuleName().equals(moduleName) || 
             ((moduleName == null) && (renameOldName.getModuleName().equals(getModuleForUnqualifiedIdentifier(varName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD))))
            )) {

            identifierList.add(new SourceModification.ReplaceText(varName, renameNewName.getUnqualifiedName(), varNameNode.getSourcePosition()));
        }
        
        return;
    }
    
    /**
     * For a CALDoc cross reference that is unresolved, we cannot tell what SourceIdentifier.Category it belongs to.
     * For this reason, we go with a conservative approach where if the old or new name is equal to the cross reference
     * then we flag it as a conflict (because we do not know how to resolve it properly).
     * 
     * todo-jowong This is an area that can be improved upon: the proper handling of these cross references by
     * subclasses of SourceIdentifierFinder as well as their clients.
     */
    @Override
    void visitCALDocCrossReferenceWithoutContextConsNameNode(List<SourceModification> identifierList, ParseTreeNode refNode) {
        ParseTreeNode qualifiedConsNode = refNode.firstChild();
        qualifiedConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode nameBeforeDotNode = qualifiedConsNode.firstChild();
        nameBeforeDotNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        String rawNameBeforeDot = ModuleNameUtilities.getMaybeModuleNameStringFromParseTree(nameBeforeDotNode);
        
        ParseTreeNode nameAfterDotNode = nameBeforeDotNode.nextSibling();
        nameAfterDotNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String nameAfterDot = nameAfterDotNode.getText();
        
        boolean foundConflict = false;
        
        boolean isQualified = rawNameBeforeDot.length() > 0;
        
        // Regardless of whether the reference has a dot in it or not, this reference could be
        // a module reference.

        // If such is the case, then in fact it would be a conflict if the reference matches either the old name, or
        // if it matches the new name and the old name is visible.

        if (renameCategory == SourceIdentifier.Category.MODULE_NAME) {
            ModuleName oldModuleName = renameOldName.getModuleName();
            ModuleName newModuleName = renameNewName.getModuleName();

            boolean isOldModuleNameVisible = isModuleImported(oldModuleName) || getCurrentModuleName().equals(oldModuleName);

            // NB: the entire name is the potential module name here.
            
            // we make a shallow copy of the qualified cons node, and modify the type to be HIERARCHICAL_MODULE_NAME
            ParseTreeNode moduleNameNode = new ParseTreeNode();
            moduleNameNode.copyContentsFrom(qualifiedConsNode);
            moduleNameNode.initialize(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME");
            
            ModuleName nameResolvedAsModuleName = getResolvedModuleName(moduleNameNode);
            
            if (oldModuleName.equals(nameResolvedAsModuleName) ||
                    (newModuleName.equals(nameResolvedAsModuleName) && isOldModuleNameVisible)) {
                foundConflict = true;
            }
        }

        // Try to treat the reference as a class name, type constructor name and data constructor name.
        // If renamings are identified in any of these cases, then the reference is in conflict because
        // we are not sure whether any of the renamings is the "right" one due to the ambiguity of
        // lacking the 'context' keyword.
        final SourceIdentifier.Category[] categoriesToCheck = new SourceIdentifier.Category[] {
            SourceIdentifier.Category.TYPE_CLASS,
            SourceIdentifier.Category.TYPE_CONSTRUCTOR,
            SourceIdentifier.Category.DATA_CONSTRUCTOR
        };
        
        ModuleName rawNameBeforeDotAsModuleName = ModuleName.make(rawNameBeforeDot);
        
        for (final Category category : categoriesToCheck) {
            if (renameCategory == category) {
                ModuleName oldModuleName = renameOldName.getModuleName();
                String oldUnqualifiedName = renameOldName.getUnqualifiedName();
                
                ModuleName newModuleName = renameNewName.getModuleName();
                String newUnqualifiedName = renameNewName.getUnqualifiedName();
                
                // Sample setup:
                // In module M, which contains a 'import X using {category} = Y;;' statement, and definitions V and W:

                boolean conflictWithOldName;
                boolean conflictWithNewName;
                
                if (isQualified) {
                    ModuleName nameBeforeDotResolvedAsModuleName = getModuleNameResolver().resolve(rawNameBeforeDotAsModuleName).getResolvedModuleName();
                    
                    // - {@link M.W@} conflicts with renaming M.W
                    // - {@link X.Y@} conflicts with renaming X.Y
                    // - {@link X.Z@} conflicts with renaming X.Z
                    conflictWithOldName = nameAfterDot.equals(oldUnqualifiedName) && nameBeforeDotResolvedAsModuleName.equals(oldModuleName);
                    
                    // - {@link X.Y@} conflicts with renaming X.Z to X.Y
                    // - {@link X.Z@} conflicts with renaming X.Q to X.Z
                    conflictWithNewName = nameAfterDot.equals(newUnqualifiedName) && nameBeforeDotResolvedAsModuleName.equals(newModuleName);
                    
                } else {
                    ModuleName moduleForReference = getModuleForUnqualifiedIdentifier(nameAfterDot, category);
                    
                    // - {@link W@} conflicts with renaming M.W
                    // - {@link Y@} conflicts with renaming X.Y
                    conflictWithOldName = nameAfterDot.equals(oldUnqualifiedName) && moduleForReference.equals(oldModuleName);
                    
                    boolean isOldModuleNameVisible =
                        oldModuleName.equals(getCurrentModuleName()) ||
                        getModuleForImportedIdentifier(oldUnqualifiedName, category) != null;
                    
                    // - {@link Y@} conflicts with renaming M.V to M.Y
                    // - {@link W@} conflicts with renaming X.Y to X.W
                    conflictWithNewName = nameAfterDot.equals(newUnqualifiedName) && isOldModuleNameVisible;
                }
                
                if (conflictWithOldName || conflictWithNewName) {
                    foundConflict = true;
                }
            }
        }
        
        if (foundConflict) {
            if (!conflictingNameFound) {
                getLogger().logMessage(new CompilerMessage(qualifiedConsNode, new MessageKind.Error.ConflictingNameInModule(getCurrentModuleName())));
                conflictingNameFound = true;
            }
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node refers to the module name being renamed. 
     */
    @Override
    void visitModuleNameNode(List<SourceModification> identifierList, ParseTreeNode moduleNameNode) {
        moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        ModuleName moduleName = getResolvedModuleName(moduleNameNode);
        
        if( renameOldName.getModuleName().equals(moduleName) && (renameCategory == SourceIdentifier.Category.MODULE_NAME)) {
            identifierList.add(new SourceModification.ReplaceText(moduleName.toSourceText(), renameNewName.getModuleName().toSourceText(), moduleNameNode.getSourcePosition()));
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node refers to the type class being renamed.    
     */
    @Override
    void visitClassNameNode(List<SourceModification> identifierList, ParseTreeNode qualifiedNode) {
        qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
        visitModuleNameNode(identifierList, moduleNameNode);
        ModuleName moduleName = getResolvedModuleName(moduleNameNode);
        ParseTreeNode consNameNode = moduleNameNode.nextSibling();
        String consName = consNameNode.getText();
        
        if( consName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TYPE_CLASS) &&
           ( renameOldName.getModuleName().equals(moduleName) ||
             ((moduleName == null) && (renameOldName.getModuleName().equals(getModuleForUnqualifiedIdentifier(consName, SourceIdentifier.Category.TYPE_CLASS))))
          )) {
            
            identifierList.add(new SourceModification.ReplaceText(consName, renameNewName.getUnqualifiedName(), consNameNode.getSourcePosition()));
        }
    
        return;
    }
    
    /**
     * Adds a renaming (original to new name) if this node refers to the type constructor being renamed.  
     */
    @Override
    void visitTypeConsNameNode(List<SourceModification> identifierList, ParseTreeNode qualifiedNode) {
        qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
        visitModuleNameNode(identifierList, moduleNameNode);
        ModuleName moduleName = getResolvedModuleName(moduleNameNode);
        ParseTreeNode consNameNode = moduleNameNode.nextSibling();
        String consName = consNameNode.getText();

        if( consName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TYPE_CONSTRUCTOR) &&
           ( renameOldName.getModuleName().equals(moduleName) ||
            ((moduleName == null) && (renameOldName.getModuleName().equals(getModuleForUnqualifiedIdentifier(consName, SourceIdentifier.Category.TYPE_CONSTRUCTOR)))) 
          )) {
            
            identifierList.add(new SourceModification.ReplaceText(consName, renameNewName.getUnqualifiedName(), consNameNode.getSourcePosition()));
        }
        
        return;
    }
    
    /**
     * Adds a renaming (original to new name) if this node refers the data constructor being renamed.   
     */
    @Override
    void visitDataConsNameNode(List<SourceModification> identifierList, ParseTreeNode qualifiedNode) {
        qualifiedNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        ParseTreeNode moduleNameNode = qualifiedNode.firstChild();
        visitModuleNameNode(identifierList, moduleNameNode);
        ModuleName moduleName = getResolvedModuleName(moduleNameNode);
        ParseTreeNode consNameNode = moduleNameNode.nextSibling();
        String consName = consNameNode.getText();

        if ( consName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.DATA_CONSTRUCTOR) &&
            ( renameOldName.getModuleName().equals(moduleName) || 
             ((moduleName == null) && (renameOldName.getModuleName().equals(getModuleForUnqualifiedIdentifier(consName, SourceIdentifier.Category.DATA_CONSTRUCTOR))))
            )) {

            identifierList.add(new SourceModification.ReplaceText(consName, renameNewName.getUnqualifiedName(), consNameNode.getSourcePosition()));
        }
        
        return;
    }
    
    /**
     * Adds a renaming (original to new name) if this node refers to the class method being renamed.  
     */
    @Override
    void visitInstanceMethodNameNode(List<SourceModification> identifierList, ParseTreeNode instanceMethodNameNode, ParseTreeNode qualifiedClassNameNode) {
        qualifiedClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        ParseTreeNode moduleNameNode = qualifiedClassNameNode.firstChild();
        visitModuleNameNode(identifierList, moduleNameNode);
        ModuleName moduleName = getResolvedModuleName(moduleNameNode);
        ParseTreeNode classNameNode = moduleNameNode.nextSibling();
        String className = classNameNode.getText();
        
        instanceMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String instanceMethodName = instanceMethodNameNode.getText();
        
        
        if ( instanceMethodName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) &&
            ( renameOldName.getModuleName().equals(moduleName) ||
             ((moduleName == null) && (renameOldName.getModuleName().equals(getModuleForUnqualifiedIdentifier(className, SourceIdentifier.Category.TYPE_CLASS))))
           )) {
            
            identifierList.add(new SourceModification.ReplaceText(instanceMethodName, renameNewName.getUnqualifiedName(), instanceMethodNameNode.getSourcePosition()));
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node defines the SC being renamed.  
     */
    @Override
    void visitDataConsDefnNameNode(List<SourceModification> identifierList, ParseTreeNode dataConsNameNode) {
        dataConsNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String dataConsName = dataConsNameNode.getText();
        
        if (dataConsName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.DATA_CONSTRUCTOR) &&
            (getCurrentModuleName().equals(renameOldName.getModuleName()))) {
             
            identifierList.add(new SourceModification.ReplaceText(dataConsName, renameNewName.getUnqualifiedName(), dataConsNameNode.getSourcePosition()));
        }
        
        // Check for the following case (Renaming mod1.S to mod1.R):
        //   import mod1 using 
        //       dataConstructor = S;
        //   ...
        //   data public SomeOtherDataType a = R a;
        // This would cause a conflict between the new mod1.R and the locally defined R         
        if (dataConsName.equals(renameNewName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.DATA_CONSTRUCTOR)) {
            ModuleName importedModule = getModuleForImportedDataConstructor(renameOldName.getUnqualifiedName());
            if (renameOldName.getModuleName().equals(importedModule) && !conflictingNameFound) {
                getLogger().logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.ConflictingNameInModule(getCurrentModuleName())));
                conflictingNameFound = true;
            }
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node defines the type class being renamed.    
     */
    @Override
    void visitClassDefnNameNode(List<SourceModification> identifierList, ParseTreeNode classNameNode) {
        classNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String className = classNameNode.getText();
        
        if (className.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TYPE_CLASS) &&
            (getCurrentModuleName().equals(renameOldName.getModuleName()))) {
            
            identifierList.add(new SourceModification.ReplaceText(className, renameNewName.getUnqualifiedName(), classNameNode.getSourcePosition()));
        }
        
        // Check for the following case (Renaming mod1.S to mod1.R):
        //   import mod1 using
        //       typeClass = S;
        //   ...
        //   public class S a where
        //   ...
        // This would cause a conflict between the new mod1.R and the locally defined R
        if (className.equals(renameNewName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TYPE_CLASS)) {
            ModuleName importedModule = getModuleForImportedTypeClass(renameOldName.getUnqualifiedName());
            if (renameOldName.getModuleName().equals(importedModule) && !conflictingNameFound) {
                getLogger().logMessage(new CompilerMessage(classNameNode, new MessageKind.Error.ConflictingNameInModule(getCurrentModuleName())));
                conflictingNameFound = true;
            }
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node defines the data constructor being renamed.    
     */
    @Override
    void visitUnqualifiedDataConsNameNode(List<SourceModification> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {
        unqualifiedNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String dataConsName = unqualifiedNameNode.getText();
        
        ModuleName moduleName;
        if (moduleNameNode != null) {
            moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
            moduleName = getResolvedModuleName(moduleNameNode);
        } else {
            moduleName = getCurrentModuleName();
        }
        
        if (dataConsName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.DATA_CONSTRUCTOR) &&
                (renameOldName.getModuleName().equals(moduleName))) {
                        
            identifierList.add(new SourceModification.ReplaceText(dataConsName, renameNewName.getUnqualifiedName(), unqualifiedNameNode.getSourcePosition()));
        }
    }
    
    @Override
    void visitUnqualifiedTypeConsNameNode(List<SourceModification> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {
        unqualifiedNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String typeConsName = unqualifiedNameNode.getText();
        
        ModuleName moduleName;
        if (moduleNameNode != null) {
            moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
            moduleName = getResolvedModuleName(moduleNameNode);
        } else {
            moduleName = getCurrentModuleName();
        }
        
        if (typeConsName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TYPE_CONSTRUCTOR) &&
                (renameOldName.getModuleName().equals(moduleName))) {
            
            identifierList.add(new SourceModification.ReplaceText(typeConsName, renameNewName.getUnqualifiedName(), unqualifiedNameNode.getSourcePosition()));
        }
    }
    
    @Override
    void visitUnqualifiedClassNameNode(List<SourceModification> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {
        unqualifiedNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String className = unqualifiedNameNode.getText();
        
        ModuleName moduleName;
        if (moduleNameNode != null) {
            moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
            moduleName = getResolvedModuleName(moduleNameNode);
        } else {
            moduleName = getCurrentModuleName();
        }
        
        if (className.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TYPE_CLASS) &&
                (renameOldName.getModuleName().equals(moduleName))) {
                        
            identifierList.add(new SourceModification.ReplaceText(className, renameNewName.getUnqualifiedName(), unqualifiedNameNode.getSourcePosition()));
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node defines the type constructor being renamed.
     */
    @Override
    void visitTypeConsDefnNameNode(List<SourceModification> identifierList, ParseTreeNode typeConsNameNode) {
        typeConsNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String typeConsName = typeConsNameNode.getText();
        
        if (typeConsName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TYPE_CONSTRUCTOR) &&
            (getCurrentModuleName().equals(renameOldName.getModuleName()))) {
            
            identifierList.add(new SourceModification.ReplaceText(typeConsName, renameNewName.getUnqualifiedName(), typeConsNameNode.getSourcePosition()));
        }
        
        //   Check for the following case (Renaming mod1.S to mod1.R):
        //   import mod1 using 
        //       typeConstructor = S;
        //   ...
        //   data public R a = ...
        // This would cause a conflict between the new mod1.R and the locally defined R
        if (typeConsName.equals(renameNewName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TYPE_CONSTRUCTOR)) {
            ModuleName importedModule = getModuleForImportedTypeConstructor(renameOldName.getUnqualifiedName());
            if (renameOldName.getModuleName().equals(importedModule) && !conflictingNameFound) {
                getLogger().logMessage(new CompilerMessage(typeConsNameNode, new MessageKind.Error.ConflictingNameInModule(getCurrentModuleName())));
                conflictingNameFound = true;
            }            
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node defines the SC being renamed.   
     */
    @Override
    void visitFunctionOrClassMethodDefinitionNameNode(List<SourceModification> identifierList, ParseTreeNode scNameNode) {
        scNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String scName = scNameNode.getText();
        
        if (scName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) &&
            (getCurrentModuleName().equals(renameOldName.getModuleName()))) {
             
            identifierList.add(new SourceModification.ReplaceText(scName, renameNewName.getUnqualifiedName(), scNameNode.getSourcePosition()));
        }
        
        // Check for the following case (Renaming mod1.S to mod1.R):
        //   import mod1 using 
        //       function = S;
        //   ...
        //   R :: a -> a;
        // This would cause a conflict between the new mod1.R and the locally defined R         
        if (scName.equals(renameNewName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD)) {
            ModuleName importedModule = getModuleForImportedFunctionOrClassMethod(renameOldName.getUnqualifiedName());
            if (renameOldName.getModuleName().equals(importedModule) && !conflictingNameFound) {
                getLogger().logMessage(new CompilerMessage(scNameNode, new MessageKind.Error.ConflictingNameInModule(getCurrentModuleName())));
                conflictingNameFound = true;
            }
        }
    }
    
    /**
     * Adds a renaming (original to new name) if this node indicates an import of the SC or Class Method being renamed.   
     */
    @Override
    void visitUnqualifiedFunctionOrClassMethodNameNode(List<SourceModification> identifierList, ParseTreeNode unqualifiedNameNode, ParseTreeNode moduleNameNode) {
        unqualifiedNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String scName = unqualifiedNameNode.getText();
        
        ModuleName moduleName;
        if (moduleNameNode != null) {
            moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
            moduleName = getResolvedModuleName(moduleNameNode);
        } else {
            moduleName = getCurrentModuleName();
        }
        
        if (scName.equals(renameOldName.getUnqualifiedName()) && (renameCategory == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) &&
           (renameOldName.getModuleName().equals(moduleName))) {
            
            identifierList.add(new SourceModification.ReplaceText(scName, renameNewName.getUnqualifiedName(), unqualifiedNameNode.getSourcePosition()));
        }
    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    void visitCALDocArgNameNode(List<SourceModification> identifierList, ArrayStack<String> boundVariablesStack, ParseTreeNode argNameNode) {}
    
    /** The identifier list produced contains SourceModification.ReplaceText objects */
    @Override
    Comparator<SourceModification> getIdentifierListComparator() {
        return SourceModification.compareByPosition;
    }

    /**
     * Resolves the module name represented by the given parse tree, and but <em>does not</em> modify it in any way even if
     * the resolved name is different from the original name.
     * 
     * @param moduleNameNode the root of the parse tree representing a module name.
     * @return the resolved name for the given module name. Can be null if the given parse tree represents the empty string "".
     */
    private ModuleName getResolvedModuleName(ParseTreeNode moduleNameNode) {
        ModuleName moduleName = ModuleNameUtilities.getModuleNameOrNullFromParseTree(moduleNameNode);
        ModuleNameResolver.ResolutionResult resolution = getModuleNameResolver().resolve(moduleName);
        return resolution.getResolvedModuleName();
    }

    @Override
    void pushLocalVar(ArrayStack<String> boundVariablesStack, ParseTreeNode varNameNode, List<SourceModification> identifierList) {        
        boundVariablesStack.push(varNameNode.getText());      
    }
}

