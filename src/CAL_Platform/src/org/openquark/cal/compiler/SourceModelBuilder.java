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
 * SourceModelBuilder.java
 * Created: Nov 19, 2004
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.SourceModel.CALDoc.TextSegment.TopLevel;
import org.openquark.cal.compiler.SourceModel.Import.UsingItem;
import org.openquark.cal.compiler.SourceModel.Name.DataCons;

/**
 * Builds an appropriate SourceModel from a ParseTreeNode. The ParseTreeNode is assumed to be of
 * the form defined by the antlr file CALTreeParser.g (the tree grammar for CAL).
 * 
 * @author Bo Ilic
 */
final class SourceModelBuilder {
    
    static SourceModel.ModuleDefn buildModuleDefn(ParseTreeNode moduleDefnNode) {
        
        moduleDefnNode.verifyType(CALTreeParserTokenTypes.MODULE_DEFN);
        
        ParseTreeNode optionalCALDocNode = moduleDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        SourceModel.CALDoc.Comment.Module caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.Module.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode moduleNameNode = optionalCALDocNode.nextSibling();
        SourceModel.Name.Module moduleName = buildModuleName(moduleNameNode);
      
        ParseTreeNode importDeclarationListNode = moduleNameNode.nextSibling();
        importDeclarationListNode.verifyType(CALTreeParserTokenTypes.IMPORT_DECLARATION_LIST);
        
        int numberOfImports = importDeclarationListNode.getNumberOfChildren();
        SourceModel.Import[] importedModules = new SourceModel.Import[numberOfImports];
        
        ParseTreeNode importDeclarationNode = importDeclarationListNode.firstChild();        
        for (int i = 0; i < numberOfImports; i++) {
            importedModules[i] = buildImport(importDeclarationNode);
            importDeclarationNode = importDeclarationNode.nextSibling();
        }
        
        
        ParseTreeNode friendDeclarationListNode = importDeclarationListNode.nextSibling();
        friendDeclarationListNode.verifyType(CALTreeParserTokenTypes.FRIEND_DECLARATION_LIST);
        
        int numberOfFriends = friendDeclarationListNode.getNumberOfChildren();
        SourceModel.Friend[] friendModules = new SourceModel.Friend[numberOfFriends];
        
        ParseTreeNode friendDeclarationNode = friendDeclarationListNode.firstChild();        
        for (int i = 0; i < numberOfFriends; i++) {
            friendModules[i] = buildFriend(friendDeclarationNode);
            friendDeclarationNode = friendDeclarationNode.nextSibling();
        }        
           
        
        ParseTreeNode outerDefnListNode = friendDeclarationListNode.nextSibling();
        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);
        
        int numberOfOuterDefns = outerDefnListNode.getNumberOfChildren();
        SourceModel.TopLevelSourceElement[] topLevelDefns = new SourceModel.TopLevelSourceElement[numberOfOuterDefns];
        
        int i = 0;
        for (final ParseTreeNode outerDefnNode : outerDefnListNode) {
            
            switch (outerDefnNode.getType()) {
              
                case CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION :
                {
                    topLevelDefns[i] = buildFunctionTypeDeclaration(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN :
                {
                    topLevelDefns[i] = buildAlgebraicFunctionDefn(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION :
                {
                    topLevelDefns[i] = buildForeignFunctionDefn(outerDefnNode);
                    break;
                }
                
                case CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION:
                {
                    topLevelDefns[i] = buildPrimitiveFunctionDefn(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.DATA_DECLARATION :
                {                   
                    topLevelDefns[i] = buildAlgebraicTypeDefn(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION :
                {                     
                    topLevelDefns[i] = buildForeignTypeDefn(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.TYPE_CLASS_DEFN :
                {                    
                    topLevelDefns[i] = buildTypeClassDefn(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.INSTANCE_DEFN :
                {                   
                    topLevelDefns[i] = buildInstanceDefn(outerDefnNode);
                    break;
                }

                default :
                {                    
                    throw new IllegalStateException("Unexpected parse tree node " + outerDefnNode.toDebugString() + ".");
                }
            }

            ++i;
        }
                  
       return SourceModel.ModuleDefn.makeAnnotated(caldocComment, moduleName, importedModules, friendModules, topLevelDefns, moduleName.getSourceRange());                  
    }   
        
    static SourceModel.Import.UsingItem buildImportUsingItem(ParseTreeNode usingItemNode) {
        
        int nodeType = usingItemNode.getType();
        int nameType = (nodeType == CALTreeParserTokenTypes.LITERAL_function) ? 
                CALTreeParserTokenTypes.VAR_ID : CALTreeParserTokenTypes.CONS_ID;
        
        List<String> usingNames = new ArrayList<String>();
        List<SourceRange> usingNameSourceRanges = new ArrayList<SourceRange>();
        
        for (final ParseTreeNode nameNode : usingItemNode) {
            
            nameNode.verifyType(nameType);            
            usingNames.add(nameNode.getText());
            usingNameSourceRanges.add(nameNode.getAssemblySourceRange());
        }
        
        final String[] emptyStrings = new String[0];
        final SourceRange[] emptySourceRanges = new SourceRange[0];

        switch(nodeType) {
        
            case CALTreeParserTokenTypes.LITERAL_function:
                return SourceModel.Import.UsingItem.Function.makeAnnotated(
                    usingNames.toArray(emptyStrings),
                    usingItemNode.getAssemblySourceRange(),
                    usingNameSourceRanges.toArray(emptySourceRanges));
                
            case CALTreeParserTokenTypes.LITERAL_dataConstructor:
                return SourceModel.Import.UsingItem.DataConstructor.makeAnnotated(
                    usingNames.toArray(emptyStrings),
                    usingItemNode.getAssemblySourceRange(),
                    usingNameSourceRanges.toArray(emptySourceRanges));
            
            case CALTreeParserTokenTypes.LITERAL_typeConstructor:
                return SourceModel.Import.UsingItem.TypeConstructor.makeAnnotated(
                    usingNames.toArray(emptyStrings),
                    usingItemNode.getAssemblySourceRange(),
                    usingNameSourceRanges.toArray(emptySourceRanges));
                
            case CALTreeParserTokenTypes.LITERAL_typeClass:
                return SourceModel.Import.UsingItem.TypeClass.makeAnnotated(
                    usingNames.toArray(emptyStrings),
                    usingItemNode.getAssemblySourceRange(),
                    usingNameSourceRanges.toArray(emptySourceRanges));
                
            default:
                throw new IllegalStateException("Unexpected parse tree node " + usingItemNode.toDebugString() + ".");
        }
    }
    
    static SourceModel.Import buildImport(ParseTreeNode importDeclarationNode) {
        
        importDeclarationNode.verifyType(CALTreeParserTokenTypes.LITERAL_import);
        
        ParseTreeNode importedModuleNameNode = importDeclarationNode.firstChild();
        SourceModel.Name.Module importedModuleName = buildModuleName(importedModuleNameNode);    
        
        List<UsingItem> usingItems = new ArrayList<UsingItem>();

        ParseTreeNode usingClauseNode = importedModuleNameNode.nextSibling();               
        if (usingClauseNode != null) {
            
            usingClauseNode.verifyType(CALTreeParserTokenTypes.LITERAL_using);
            
            for (final ParseTreeNode usingItemNode : usingClauseNode) {
                
                usingItems.add(buildImportUsingItem(usingItemNode));
            }
        }

        final SourceModel.Import.UsingItem[] emptyUsingItems = new SourceModel.Import.UsingItem[0];
        
        SourceModel.Import importStatement = SourceModel.Import.makeAnnotated(importedModuleName, 
                usingItems.toArray(emptyUsingItems),
                importDeclarationNode.getAssemblySourceRange());
        
        return importStatement;         
    } 
    
    static SourceModel.Friend buildFriend(ParseTreeNode friendDeclarationNode) {
        
        friendDeclarationNode.verifyType(CALTreeParserTokenTypes.LITERAL_friend);
        
        ParseTreeNode friendModuleNameNode = friendDeclarationNode.firstChild();
        SourceModel.Name.Module friendModuleName = buildModuleName(friendModuleNameNode);    
        
        return SourceModel.Friend.makeAnnotated(friendModuleName, friendDeclarationNode.getAssemblySourceRange());         
    }     
    
    static SourceModel.TypeClassDefn.ClassMethodDefn buildClassMethodDefn(ParseTreeNode classMethodNode) {
        classMethodNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD);  
        
        ParseTreeNode optionalCALDocNode = classMethodNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        SourceModel.CALDoc.Comment.ClassMethod caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.ClassMethod.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode scopeNode = optionalCALDocNode.nextSibling();
        Scope scope = CALTypeChecker.getScopeModifier(scopeNode);
        boolean isScopeExplicitlySpecified = isScopeExplicitlySpecified(scopeNode);
        
        ParseTreeNode classMethodNameNode = scopeNode.nextSibling();                     
        classMethodNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);        
        
        ParseTreeNode typeSignatureNode = classMethodNameNode.nextSibling();
        
        ParseTreeNode defaultClassMethodNameNode = typeSignatureNode.nextSibling();
        final SourceModel.Name.Function defaultClassMethodName;
        if (defaultClassMethodNameNode != null) {
            defaultClassMethodName = buildFunctionName(defaultClassMethodNameNode);
        } else {
            defaultClassMethodName = null;
        }
        
        SourceRange bodySourceRange;
        {
            final SourceRange wholeExpression = classMethodNode.getAssemblySourceRange();
            final SourceRange classMethodNameSourceRange = classMethodNameNode.getAssemblySourceRange();
            if (wholeExpression != null && classMethodNameSourceRange != null){
                final SourcePosition startOfLine = new SourcePosition(classMethodNameSourceRange.getStartLine(), 1, classMethodNameSourceRange.getSourceName());
                bodySourceRange = new SourceRange(startOfLine, wholeExpression.getEndSourcePosition());
            }
            else{
                bodySourceRange = null;
            }
        }

        return SourceModel.TypeClassDefn.ClassMethodDefn.makeAnnotated(caldocComment, classMethodNameNode.getText(), scope, isScopeExplicitlySpecified, buildTypeSignature(typeSignatureNode), defaultClassMethodName, classMethodNameNode.getAssemblySourceRange(), bodySourceRange);
    }
    
    static SourceModel.TypeClassDefn buildTypeClassDefn(ParseTreeNode typeClassDefnNode) {
        
        typeClassDefnNode.verifyType(CALTreeParserTokenTypes.TYPE_CLASS_DEFN);              
        ParseTreeNode optionalCALDocNode = typeClassDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.TypeClass caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.TypeClass.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode scopeNode = optionalCALDocNode.nextSibling();
        Scope scope = CALTypeChecker.getScopeModifier(scopeNode);
        boolean isScopeExplicitlySpecified = isScopeExplicitlySpecified(scopeNode);
        
        ParseTreeNode contextListNode = scopeNode.nextSibling();
        contextListNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);
        SourceModel.Constraint.TypeClass[] parentClassConstraints = new SourceModel.Constraint.TypeClass[contextListNode.getNumberOfChildren()];
        int parentN = 0;
        for (final ParseTreeNode contextNode : contextListNode) {
            
            parentClassConstraints[parentN] = buildTypeClassConstraint(contextNode);
            ++parentN;
        }
        
        ParseTreeNode typeClassNameNode = contextListNode.nextSibling();
        typeClassNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);              
        String typeClassName = typeClassNameNode.getText();
        
        ParseTreeNode typeVarNode = typeClassNameNode.nextSibling();
        typeVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
        ParseTreeNode classMethodListNode = typeVarNode.nextSibling();
        classMethodListNode.verifyType(CALTreeParserTokenTypes.CLASS_METHOD_LIST);
        SourceModel.TypeClassDefn.ClassMethodDefn[] classMethods = new SourceModel.TypeClassDefn.ClassMethodDefn[classMethodListNode.getNumberOfChildren()];
        int methodN = 0;
        for (final ParseTreeNode classMethodNode : classMethodListNode) {
            
            classMethods[methodN] = buildClassMethodDefn(classMethodNode);
            ++methodN;                                                       
        }                                               

        SourceRange bodySourceRange;
        {
            final SourceRange wholeExpression = typeClassDefnNode.getAssemblySourceRange();
            final SourceRange typeClassNameSourceRange = typeClassNameNode.getAssemblySourceRange();
            if (wholeExpression != null && typeClassNameSourceRange != null){
                final SourcePosition startOfLine = new SourcePosition(typeClassNameSourceRange.getStartLine(), 1, typeClassNameSourceRange.getSourceName());
                bodySourceRange = new SourceRange(startOfLine, wholeExpression.getEndSourcePosition());
            }
            else{
                bodySourceRange = null;
            }
        }
        return SourceModel.TypeClassDefn.makeAnnotated(caldocComment, typeClassName, buildTypeVarName(typeVarNode), scope, isScopeExplicitlySpecified, parentClassConstraints ,classMethods, typeClassNameNode.getAssemblySourceRange(), bodySourceRange, contextListNode.getType() == CALTreeParserTokenTypes.CLASS_CONTEXT_LIST);
    }
    
    static SourceModel.InstanceDefn.InstanceTypeCons buildInstanceTypeCons(ParseTreeNode instanceTypeConsNode) {
        
        switch(instanceTypeConsNode.getType()) {
            case CALTreeParserTokenTypes.GENERAL_TYPE_CONSTRUCTOR:
            case CALTreeParserTokenTypes.UNPARENTHESIZED_TYPE_CONSTRUCTOR: 
            {
                ParseTreeNode typeConsNameNode = instanceTypeConsNode.firstChild();
                
                final int nTypeVars = instanceTypeConsNode.getNumberOfChildren() - 1;
                
                SourceModel.Name.TypeVar[] typeVarNames = new SourceModel.Name.TypeVar[nTypeVars];
                int i = 0;
                for (final ParseTreeNode typeVarNode : typeConsNameNode.nextSiblings()) {
                    typeVarNames[i] = buildTypeVarName(typeVarNode);
                    ++i;
                }

                return SourceModel.InstanceDefn.InstanceTypeCons.TypeCons.makeAnnotated(buildTypeConsName(typeConsNameNode), typeVarNames, typeConsNameNode.getAssemblySourceRange(), instanceTypeConsNode.getAssemblySourceRange(), instanceTypeConsNode.getType() == CALTreeParserTokenTypes.GENERAL_TYPE_CONSTRUCTOR);
            }
                
            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR:            
            {
                ParseTreeNode domainNameNode = instanceTypeConsNode.firstChild();
                ParseTreeNode codomainNameNode = instanceTypeConsNode.getChild(1);
                return SourceModel.InstanceDefn.InstanceTypeCons.Function.makeAnnotated(buildTypeVarName(domainNameNode), buildTypeVarName(codomainNameNode), instanceTypeConsNode.getAssemblySourceRange(), instanceTypeConsNode.getSourceRange());
            }
            
            case CALTreeParserTokenTypes.UNIT_TYPE_CONSTRUCTOR:
            {               
                return SourceModel.InstanceDefn.InstanceTypeCons.Unit.makeAnnotated(instanceTypeConsNode.getAssemblySourceRange());                          
            }
            
            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR:           
            {
                return SourceModel.InstanceDefn.InstanceTypeCons.List.makeAnnotated(buildTypeVarName(instanceTypeConsNode.firstChild()), instanceTypeConsNode.getAssemblySourceRange());
            } 
            
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR:
            {
                return SourceModel.InstanceDefn.InstanceTypeCons.Record.makeAnnotated(buildTypeVarName(instanceTypeConsNode.firstChild()), instanceTypeConsNode.firstChild().getAssemblySourceRange(), instanceTypeConsNode.getSourceRange());
            }
            
            default: {
                throw new IllegalStateException("Unexpected parse tree node " + instanceTypeConsNode.toDebugString() + ".");
            }
        }
    }
    
    static SourceModel.InstanceDefn buildInstanceDefn(ParseTreeNode instanceDefnNode) {
        instanceDefnNode.verifyType(CALTreeParserTokenTypes.INSTANCE_DEFN);
                
        ParseTreeNode optionalCALDocNode = instanceDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.Instance caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.Instance.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode instanceNameNode = optionalCALDocNode.nextSibling();
        instanceNameNode.verifyType(CALTreeParserTokenTypes.INSTANCE_NAME);
                
        ParseTreeNode contextListNode = instanceNameNode.firstChild();
        contextListNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT_LIST, CALTreeParserTokenTypes.CLASS_CONTEXT_SINGLETON, CALTreeParserTokenTypes.CLASS_CONTEXT_NOTHING);
        SourceModel.Constraint.TypeClass[] constraints = new SourceModel.Constraint.TypeClass[contextListNode.getNumberOfChildren()];
        int i = 0;
        for (final ParseTreeNode contextNode : contextListNode) {
            
            constraints[i] = buildTypeClassConstraint(contextNode);
            ++i;
        }
        
        ParseTreeNode typeClassNameNode  = contextListNode.nextSibling();
        SourceModel.Name.TypeClass typeClassName = buildTypeClassName(typeClassNameNode);
        
        SourceModel.InstanceDefn.InstanceTypeCons instanceTypeCons = buildInstanceTypeCons(typeClassNameNode.nextSibling());
        
        ParseTreeNode instanceMethodListNode = instanceNameNode.nextSibling();
        instanceMethodListNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD_LIST);
        SourceModel.InstanceDefn.InstanceMethod[] instanceMethods = new SourceModel.InstanceDefn.InstanceMethod[instanceMethodListNode.getNumberOfChildren()];
        i = 0;
        for (final ParseTreeNode instanceMethodNode : instanceMethodListNode) {
            
            instanceMethodNode.verifyType(CALTreeParserTokenTypes.INSTANCE_METHOD);
            
            ParseTreeNode optionalInstanceMethodCALDocNode = instanceMethodNode.firstChild();
            optionalInstanceMethodCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
            
            SourceModel.CALDoc.Comment.InstanceMethod instanceMethodCALDocComment = null;
            ParseTreeNode instanceMethodCALDocNode = optionalInstanceMethodCALDocNode.firstChild();
            
            if (instanceMethodCALDocNode != null) {
                instanceMethodCALDocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
                ParseTreeNode descNode = instanceMethodCALDocNode.firstChild();
                SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
                ParseTreeNode taggedBlocksNode = descNode.nextSibling();
                SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
                
                instanceMethodCALDocComment = SourceModel.CALDoc.Comment.InstanceMethod.makeAnnotated(desc, blocks, instanceMethodCALDocNode.getAssemblySourceRange());
            }

            ParseTreeNode classMethodNameNode = instanceMethodNode.getChild(1);
            ParseTreeNode resolvingFunctionNameNode = instanceMethodNode.getChild(2); 
            instanceMethods[i] = SourceModel.InstanceDefn.InstanceMethod.makeAnnotated(instanceMethodCALDocComment, classMethodNameNode.getText(), buildFunctionName(resolvingFunctionNameNode), resolvingFunctionNameNode.getAssemblySourceRange(), classMethodNameNode.getAssemblySourceRange());
            ++i;
        }
                      
        return SourceModel.InstanceDefn.makeAnnotated(caldocComment, typeClassName, instanceTypeCons, constraints, instanceMethods, instanceDefnNode.getAssemblySourceRange(), contextListNode.getType() == CALTreeParserTokenTypes.CLASS_CONTEXT_LIST);
    }
    
    static SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn buildDataConsDefn(ParseTreeNode dataConsDefnNode) {
       
        dataConsDefnNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN);

        ParseTreeNode optionalCALDocNode = dataConsDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.DataCons caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.DataCons.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode scopeNode = optionalCALDocNode.nextSibling();
        Scope scope = CALTypeChecker.getScopeModifier(scopeNode);
        boolean isScopeExplicitlySpecified = isScopeExplicitlySpecified(scopeNode);
              
        ParseTreeNode dataConsNameNode = scopeNode.nextSibling();
        String dataConsName = dataConsNameNode.getText();

        ParseTreeNode dataConsArgListNode = dataConsNameNode.nextSibling();
        dataConsArgListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_ARG_LIST);
              
        SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument[] argTypes =
            new SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument[dataConsArgListNode.getNumberOfChildren()];       
        int argN = 0;
        
        for (final ParseTreeNode dataConsArgNode : dataConsArgListNode) {

            dataConsArgNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAMED_ARG);
            
            // Get the name.
            ParseTreeNode dataConsArgNameNode = dataConsArgNode.firstChild();
            SourceModel.Name.Field fieldName = buildFieldName(dataConsArgNameNode);
            
            // Get the type node.
            ParseTreeNode maybePlingTypeExprNode = dataConsArgNameNode.nextSibling();
            
            ParseTreeNode typeExprNode;
            boolean isStrict;
            if (maybePlingTypeExprNode.getType() == CALTreeParserTokenTypes.STRICT_ARG) {
                typeExprNode = maybePlingTypeExprNode.firstChild();
                isStrict = true;
            } else {
                typeExprNode = maybePlingTypeExprNode;                        
                isStrict = false;
            }
            
            argTypes[argN] = SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn.TypeArgument.makeAnnotated(fieldName, buildTypeExprDefn(typeExprNode), isStrict,dataConsArgNode.getAssemblySourceRange());
            
            ++argN;
        }

        SourceRange bodySourceRange;
        {
            final SourceRange wholeExpression = dataConsDefnNode.getAssemblySourceRange();
            final SourceRange dataConsNameSourceRange = dataConsNameNode.getAssemblySourceRange();
            if (wholeExpression != null && dataConsNameSourceRange != null){
                final SourcePosition startOfLine = new SourcePosition(dataConsNameSourceRange.getStartLine(), 1, dataConsNameSourceRange.getSourceName());
                bodySourceRange = new SourceRange(startOfLine, wholeExpression.getEndSourcePosition());
            }
            else{
                bodySourceRange = null;
            }
        }

        return SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn.makeAnnotated(caldocComment, dataConsName, scope, isScopeExplicitlySpecified, argTypes, dataConsNameNode.getAssemblySourceRange(), bodySourceRange);        
    }
    
    static SourceModel.TypeConstructorDefn.AlgebraicType buildAlgebraicTypeDefn(ParseTreeNode algebraicTypeDefnNode) {
        algebraicTypeDefnNode.verifyType(CALTreeParserTokenTypes.DATA_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = algebraicTypeDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.TypeCons caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.TypeCons.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode); 
        boolean isScopeExplicitlySpecified = isScopeExplicitlySpecified(accessModifierNode);
        
        ParseTreeNode typeNameNode = accessModifierNode.nextSibling();
        typeNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);                    
        String typeName = typeNameNode.getText();
        
        ParseTreeNode typeParamListNode = typeNameNode.nextSibling();
        typeParamListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONS_PARAM_LIST);
        
        SourceModel.Name.TypeVar[] typeParameters = new SourceModel.Name.TypeVar[typeParamListNode.getNumberOfChildren()];
        int paramN = 0;
        for (final ParseTreeNode typeVarNode : typeParamListNode) {
            typeVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
            typeParameters[paramN] = buildTypeVarName(typeVarNode);                           
            ++paramN;   
        }
                
        //Data constructor names must be unique within the entire module. 
        
        ParseTreeNode dataConsDefnListNode = typeNameNode.nextSibling().nextSibling();
        dataConsDefnListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_DEFN_LIST);
        SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn[] dataConstructors =
            new SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn[dataConsDefnListNode.getNumberOfChildren()];
        int dcN = 0;
        for (final ParseTreeNode dataConsDefnNode : dataConsDefnListNode) {
            
            dataConstructors[dcN] = buildDataConsDefn(dataConsDefnNode);            
            ++dcN;                      
        }  
        
        ParseTreeNode derivingClauseNode = dataConsDefnListNode.nextSibling();
        SourceModel.Name.TypeClass[] derivingClauseTypeClassNames = buildDerivingClauseTypeClassNames(derivingClauseNode);

        SourceRange bodySourceRange;
        {
            final SourceRange wholeExpression = algebraicTypeDefnNode.getAssemblySourceRange();
            final SourceRange accessModifierSourceRange = accessModifierNode.getAssemblySourceRange();
            if (wholeExpression != null && accessModifierSourceRange != null){
                final SourcePosition startOfLine = new SourcePosition(accessModifierSourceRange.getStartLine(), 1, accessModifierSourceRange.getSourceName());
                bodySourceRange = new SourceRange(startOfLine, wholeExpression.getEndSourcePosition());
            }
            else if (wholeExpression != null){
                // not quite right but better than nothing. 
                // This will be cleaned up when we
                // fix up all the source position problems.
                bodySourceRange = wholeExpression;
            }
            else{
                bodySourceRange = null;
            }
        }
        
        return SourceModel.TypeConstructorDefn.AlgebraicType.makeAnnotated(caldocComment, typeName, scope, isScopeExplicitlySpecified, typeParameters, dataConstructors, derivingClauseTypeClassNames, typeNameNode.getAssemblySourceRange(), bodySourceRange);
    }   

    static SourceModel.TypeConstructorDefn.ForeignType buildForeignTypeDefn(ParseTreeNode foreignTypeDefnNode) {
        foreignTypeDefnNode.verifyType(CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = foreignTypeDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        SourceModel.CALDoc.Comment.TypeCons caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.TypeCons.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode implementationScopeNode = optionalCALDocNode.nextSibling();
        Scope implementationScope = CALTypeChecker.getScopeModifier(implementationScopeNode);  
        boolean isImplementationScopeExplicitlySpecified = isScopeExplicitlySpecified(implementationScopeNode);
  
        ParseTreeNode externalNameNode = implementationScopeNode.nextSibling();
        externalNameNode.verifyType(CALTreeParserTokenTypes.STRING_LITERAL);                                           
        String externalName = StringEncoder.unencodeString(externalNameNode.getText ());
        final SourceRange externalNameSourceRange = externalNameNode.getSourceRange();
                      
        ParseTreeNode accessModifierNode = externalNameNode.nextSibling();
        Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode);                 
        boolean isScopeExplicitlySpecified = isScopeExplicitlySpecified(accessModifierNode);
        
        ParseTreeNode typeNameNode = accessModifierNode.nextSibling();
        typeNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);                                   
        String typeName = typeNameNode.getText();
        
        ParseTreeNode derivingClauseNode = typeNameNode.nextSibling();
        SourceModel.Name.TypeClass[] derivingClauseTypeClassNames = buildDerivingClauseTypeClassNames(derivingClauseNode);        

        SourceRange bodySourceRange;
        {
            final SourceRange wholeExpressionSourceRange = foreignTypeDefnNode.getAssemblySourceRange();
            final SourceRange externalNameAssemblySourceRange = externalNameNode.getAssemblySourceRange();
            if (wholeExpressionSourceRange != null && externalNameAssemblySourceRange != null){
                final SourcePosition startOfLine = new SourcePosition(externalNameAssemblySourceRange.getStartLine(), 1, externalNameAssemblySourceRange.getSourceName());
                bodySourceRange = new SourceRange(startOfLine, wholeExpressionSourceRange.getEndSourcePosition());
            }
            else{
                bodySourceRange = null;
            }
        }
        
        return SourceModel.TypeConstructorDefn.ForeignType.makeAnnotated(caldocComment, typeName, scope, isScopeExplicitlySpecified, externalName, externalNameSourceRange, implementationScope, isImplementationScopeExplicitlySpecified, derivingClauseTypeClassNames, typeNameNode.getAssemblySourceRange(), bodySourceRange);        
    }
    
    static SourceModel.Name.TypeClass[] buildDerivingClauseTypeClassNames(ParseTreeNode derivingClauseNode) {
        
        if (derivingClauseNode == null) {
            return null;            
        }
        
        derivingClauseNode.verifyType(CALTreeParserTokenTypes.LITERAL_deriving);
        
        SourceModel.Name.TypeClass[] derivingClauseTypeClassNames =
            new SourceModel.Name.TypeClass[derivingClauseNode.getNumberOfChildren()];
        
        int derivingClauseN = 0;
        for (final ParseTreeNode derivingClauseNameNode : derivingClauseNode) {
            
            derivingClauseTypeClassNames[derivingClauseN] = buildTypeClassName(derivingClauseNameNode);
            
            ++derivingClauseN;
        }
        
        return derivingClauseTypeClassNames;
    }    
    
    static SourceModel.FunctionTypeDeclaration buildFunctionTypeDeclaration(ParseTreeNode topLevelTypeDeclarationNode) {
        topLevelTypeDeclarationNode.verifyType( CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION);
                                   
        ParseTreeNode optionalCALDocNode = topLevelTypeDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.Function caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.Function.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode typeDeclarationNode = optionalCALDocNode.nextSibling();
        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
        ParseTreeNode declaredTypeNode = functionNameNode.nextSibling();
                            
        SourceRange bodySourceRange;
        {
            final SourceRange wholeExpression = topLevelTypeDeclarationNode.getAssemblySourceRange();
            final SourceRange classMethodNameSourceRange = typeDeclarationNode.getAssemblySourceRange();
            if (wholeExpression != null && classMethodNameSourceRange != null){
                final SourcePosition startOfLine = new SourcePosition(classMethodNameSourceRange.getStartLine(), 1, classMethodNameSourceRange.getSourceName());
                bodySourceRange = new SourceRange(startOfLine, wholeExpression.getEndSourcePosition());
            }
            else{
                bodySourceRange = null;
            }
        }

        return SourceModel.FunctionTypeDeclaration.makeAnnotated(caldocComment, functionNameNode.getText(), buildTypeSignature(declaredTypeNode), functionNameNode.getAssemblySourceRange(), bodySourceRange);
    }
    
    static SourceModel.FunctionDefn.Algebraic buildAlgebraicFunctionDefn(ParseTreeNode topLevelFunctionDefnNode) {
        topLevelFunctionDefnNode.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);                

        ParseTreeNode optionalCALDocNode = topLevelFunctionDefnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.Function caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.Function.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode);
        boolean isScopeExplicitlySpecified = isScopeExplicitlySpecified(accessModifierNode);

        ParseTreeNode functionNameNode = accessModifierNode.nextSibling();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String functionName = functionNameNode.getText(); 

        ParseTreeNode paramListNode = functionNameNode.nextSibling();
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST); 
              
        final int nParams = paramListNode.getNumberOfChildren();
        int paramN = 0;
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[nParams];        
        
        for (final ParseTreeNode varNode : paramListNode) {
            
            parameters[paramN] = SourceModel.Parameter.makeAnnotated(varNode.getText(), varNode.getType() == CALTreeParserTokenTypes.STRICT_PARAM, varNode.getAssemblySourceRange());
                       
            ++paramN;
        }
        
        ParseTreeNode exprNode = paramListNode.nextSibling();
        
        SourceModel.Expr definingExpr = buildExpr(exprNode);

        ParseTreeNode accessModifierTokenNode = accessModifierNode.firstChild();
        final SourceRange sourceRange = topLevelFunctionDefnNode.getAssemblySourceRange();
        SourceRange sourceRangeExcludingCaldoc = null;
        
        SourceRange functionNameNodeRange = functionNameNode.getSourceRange();
        SourceRange accessModifierTokenNodeRange = null;
        
        if(accessModifierTokenNode != null) {
            accessModifierTokenNodeRange = accessModifierTokenNode.getAssemblySourceRange();
        }
        
        if(sourceRange != null) {
            if(accessModifierTokenNodeRange != null) {
                sourceRangeExcludingCaldoc = new SourceRange(accessModifierTokenNodeRange.getStartSourcePosition(), sourceRange.getEndSourcePosition());
            } else {
                sourceRangeExcludingCaldoc = new SourceRange(functionNameNodeRange.getStartSourcePosition(), sourceRange.getEndSourcePosition());
            }
        }
        
        return SourceModel.FunctionDefn.Algebraic.makeAnnotated(caldocComment, functionName, scope, isScopeExplicitlySpecified, parameters, definingExpr, sourceRange, sourceRangeExcludingCaldoc, functionNameNode.getAssemblySourceRange());
    }
    
    static SourceModel.FunctionDefn.Foreign buildForeignFunctionDefn(ParseTreeNode foreignFunctionDeclarationNode) {
        foreignFunctionDeclarationNode.verifyType(CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION);
                               
        final SourceRange sourceRange = foreignFunctionDeclarationNode.getAssemblySourceRange();
        SourceRange sourceRangeExcludingCaldoc = null;
        
        SourceRange declarationNodeRange = foreignFunctionDeclarationNode.getSourceRange();
        if(sourceRange != null && declarationNodeRange != null) {
            sourceRangeExcludingCaldoc = new SourceRange(declarationNodeRange.getStartSourcePosition(), sourceRange.getEndSourcePosition());
        }
        
        ParseTreeNode optionalCALDocNode = foreignFunctionDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.Function caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.Function.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode externalNameNode = optionalCALDocNode.nextSibling();
        externalNameNode.verifyType(CALTreeParserTokenTypes.STRING_LITERAL);
        String externalName = StringEncoder.unencodeString(externalNameNode.getText ());
        final SourceRange externalNameSourceRange = externalNameNode.getSourceRange();
                                                    
        ParseTreeNode accessModifierNode = externalNameNode.nextSibling();
        Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode);                 
        boolean isScopeExplicitlySpecified = isScopeExplicitlySpecified(accessModifierNode);
        
        ParseTreeNode typeDeclarationNode = accessModifierNode.nextSibling(); 
        typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                                                                                                                       
        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);             
        String functionName = functionNameNode.getText();
        
        SourceModel.TypeSignature declaredType = buildTypeSignature(functionNameNode.nextSibling());                      
        
        return SourceModel.FunctionDefn.Foreign.makeAnnotated(caldocComment, functionName, scope, isScopeExplicitlySpecified, externalName, externalNameSourceRange, declaredType, sourceRange, sourceRangeExcludingCaldoc, functionNameNode.getAssemblySourceRange());                                                                        
    }
    
    static SourceModel.FunctionDefn.Primitive buildPrimitiveFunctionDefn(ParseTreeNode primitiveFunctionNode) {
        primitiveFunctionNode.verifyType(CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION);
        
        ParseTreeNode optionalCALDocNode = primitiveFunctionNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.Function caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.Function.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode accessModifierNode = optionalCALDocNode.nextSibling();
        Scope scope = CALTypeChecker.getScopeModifier(accessModifierNode);                 
        boolean isScopeExplicitlySpecified = isScopeExplicitlySpecified(accessModifierNode);
        
        ParseTreeNode typeDeclarationNode = accessModifierNode.nextSibling();  
        typeDeclarationNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);                            
                                                                                                         
        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);             
        String functionName = functionNameNode.getText();
        
        SourceModel.TypeSignature declaredType = buildTypeSignature(functionNameNode.nextSibling()); 
        
        final SourceRange sourceRange = primitiveFunctionNode.getAssemblySourceRange();
        
        SourceRange primitiveFunctionNodeRange = primitiveFunctionNode.getSourceRange();
        SourceRange sourceRangeExcludingCaldoc = null;
        if(sourceRange != null && primitiveFunctionNodeRange != null) {
            sourceRangeExcludingCaldoc = new SourceRange(primitiveFunctionNodeRange.getStartSourcePosition(), sourceRange.getEndSourcePosition());
        }
        
        return SourceModel.FunctionDefn.Primitive.makeAnnotated(
                caldocComment, 
                functionName, 
                scope,
                isScopeExplicitlySpecified,
                declaredType,
                sourceRange,
                sourceRangeExcludingCaldoc,
                functionNameNode.getAssemblySourceRange()); 
    }
    
    static SourceModel.Constraint.TypeClass buildTypeClassConstraint(ParseTreeNode contextNode) {
        contextNode.verifyType(CALTreeParserTokenTypes.CLASS_CONTEXT);
        
        ParseTreeNode typeClassNameNode = contextNode.firstChild();
        typeClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode varNameNode = typeClassNameNode.nextSibling();
        varNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);                   
        
        return SourceModel.Constraint.TypeClass.makeAnnotated(buildTypeClassName(typeClassNameNode), buildTypeVarName(varNameNode), contextNode.getAssemblySourceRange());
    }
    
    static SourceModel.TypeSignature buildTypeSignature(ParseTreeNode typeSignatureNode) {
       
        typeSignatureNode.verifyType(CALTreeParserTokenTypes.TYPE_SIGNATURE);
        
        ParseTreeNode typeContextListNode = typeSignatureNode.firstChild();
        typeContextListNode.verifyType(CALTreeParserTokenTypes.TYPE_CONTEXT_LIST, CALTreeParserTokenTypes.TYPE_CONTEXT_NOTHING, CALTreeParserTokenTypes.TYPE_CONTEXT_SINGLETON);
        final int nConstraints = typeContextListNode.getNumberOfChildren();
        SourceModel.Constraint[] constraints = new SourceModel.Constraint[nConstraints];
        int i = 0;
        
        for (final ParseTreeNode contextNode : typeContextListNode) {
            
            switch (contextNode.getType())
            {                         
                case CALTreeParserTokenTypes.CLASS_CONTEXT :
                {                       
                    constraints[i] = buildTypeClassConstraint(contextNode);                                        
                    break;                        
                }
                
                case CALTreeParserTokenTypes.LACKS_FIELD_CONTEXT:
                {
                    ParseTreeNode recordVarNameNode = contextNode.firstChild();
                    recordVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                    
                    ParseTreeNode lacksFieldNameNode = recordVarNameNode.nextSibling();
                    SourceModel.Name.Field lacksFieldName = buildFieldName(lacksFieldNameNode);                    
                    
                    constraints[i] = SourceModel.Constraint.Lacks.makeAnnotated(buildTypeVarName(recordVarNameNode), lacksFieldName, contextNode.getAssemblySourceRange());                    
                    break;
                }
                
                default:
                {
                    throw new IllegalStateException("Unexpected parse tree node " + contextNode.toDebugString() + ".");             
                } 
            }
            
            ++i;
        }
        
        ParseTreeNode typeExprNode = typeContextListNode.nextSibling();
        return SourceModel.TypeSignature.make(constraints, buildTypeExprDefn(typeExprNode), typeContextListNode.getType() == CALTreeParserTokenTypes.TYPE_CONTEXT_LIST);
    }    
    
    static SourceModel.LocalDefn.Function.Definition buildLocalFunctionDefn(ParseTreeNode localFunctionNode) {
        localFunctionNode.verifyType(CALTreeParserTokenTypes.LET_DEFN);

        ParseTreeNode optionalCALDocNode = localFunctionNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.Function caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.Function.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode functionNameNode = optionalCALDocNode.nextSibling();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String functionName = functionNameNode.getText();
        SourceRange functionNameSourceRange = functionNameNode.getSourceRange();

        ParseTreeNode paramListNode = functionNameNode.nextSibling();
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST); 
              
        final int nParams = paramListNode.getNumberOfChildren();
        int paramN = 0;
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[nParams];        
        
        for (final ParseTreeNode varNode : paramListNode) {
            
            parameters[paramN] = SourceModel.Parameter.makeAnnotated(varNode.getText(), varNode.getType() == CALTreeParserTokenTypes.STRICT_PARAM, varNode.getAssemblySourceRange());
                       
            ++paramN;
        }
        
        ParseTreeNode exprNode = paramListNode.nextSibling();
        
        SourceModel.Expr definingExpr = buildExpr(exprNode);
        
        SourceRange sourceRange = localFunctionNode.getAssemblySourceRange();
        SourceRange sourceRangeExcludingCaldoc = null;

        SourceRange functionNameNodeRange = functionNameNode.getSourceRange();
        if(sourceRange != null && functionNameNodeRange != null) {
            sourceRangeExcludingCaldoc = new SourceRange(functionNameNodeRange.getStartSourcePosition(), sourceRange.getEndSourcePosition());
        }
        
        return SourceModel.LocalDefn.Function.Definition.makeAnnotated(caldocComment, functionName, functionNameSourceRange, parameters, definingExpr, sourceRange, sourceRangeExcludingCaldoc);
    }
    
    static SourceModel.LocalDefn.Function.TypeDeclaration buildLocalFunctionTypeDeclaration(ParseTreeNode localTypeDeclarationNode) {       
        localTypeDeclarationNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION);
                                   
        ParseTreeNode optionalCALDocNode = localTypeDeclarationNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);

        SourceModel.CALDoc.Comment.Function caldocComment = null;
        ParseTreeNode caldocNode = optionalCALDocNode.firstChild();
        
        if (caldocNode != null) {
            caldocNode.verifyType(CALTreeParserTokenTypes.CALDOC_COMMENT);
            ParseTreeNode descNode = caldocNode.firstChild();
            SourceModel.CALDoc.TextBlock desc = buildCALDocDescriptionBlock(descNode);
            ParseTreeNode taggedBlocksNode = descNode.nextSibling();
            SourceModel.CALDoc.TaggedBlock[] blocks = buildCALDocTaggedBlocks(taggedBlocksNode);
            
            caldocComment = SourceModel.CALDoc.Comment.Function.makeAnnotated(desc, blocks, caldocNode.getAssemblySourceRange());
        }
        
        ParseTreeNode typeDeclarationNode = optionalCALDocNode.nextSibling();
        typeDeclarationNode.verifyType( CALTreeParserTokenTypes.TYPE_DECLARATION);
                                   
        ParseTreeNode functionNameNode = typeDeclarationNode.firstChild();
        functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        
        ParseTreeNode declaredTypeNode = functionNameNode.nextSibling();
                            
        return SourceModel.LocalDefn.Function.TypeDeclaration.makeAnnotated(caldocComment, functionNameNode.getText(), functionNameNode.getSourceRange(), buildTypeSignature(declaredTypeNode), localTypeDeclarationNode.getAssemblySourceRange());        
    }
    
    /**
     * Builds a source model representation of a local pattern match declaration.
     * @param localPatternMatchDeclNode the root of the parse tree representing a local pattern match declaration.
     * @return a source model representation of a local pattern match declaration.
     */
    static SourceModel.LocalDefn.PatternMatch buildLocalPatternMatchDecl(final ParseTreeNode localPatternMatchDeclNode) {
        localPatternMatchDeclNode.verifyType(CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL);
        final SourceRange patternMatchDeclSourceRange = localPatternMatchDeclNode.getAssemblySourceRange();
        
        ParseTreeNode fullPatternNode = localPatternMatchDeclNode.firstChild();
        
        ParseTreeNode exprNode = fullPatternNode.nextSibling();
        SourceModel.Expr expr = buildExpr(exprNode);
               
        switch (fullPatternNode.getType()) {
        
            case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR :
            {
                ParseTreeNode dataConsNameListNode = fullPatternNode.firstChild();
                dataConsNameListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON);
                
                ParseTreeNode dataConsNameNode = dataConsNameListNode.firstChild();
                SourceModel.Name.DataCons dataConsName = buildDataConsName(dataConsNameNode);
                    
                ParseTreeNode argBindingsNode = dataConsNameListNode.nextSibling();
                
                switch (argBindingsNode.getType()) {
                    
                    case CALTreeParserTokenTypes.PATTERN_VAR_LIST :
                    {
                        return SourceModel.LocalDefn.PatternMatch.UnpackDataCons.makeAnnotated(dataConsName, SourceModel.ArgBindings.Positional.make(buildPatterns(argBindingsNode)), expr, patternMatchDeclSourceRange);
                    }
                    case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST :
                    {
                        return SourceModel.LocalDefn.PatternMatch.UnpackDataCons.makeAnnotated(dataConsName, SourceModel.ArgBindings.Matching.make(buildFieldPatterns(argBindingsNode)), expr, patternMatchDeclSourceRange);
                    }
                    default :
                    {
                        throw new IllegalStateException("Unexpected parse tree node " + argBindingsNode.toDebugString() + "."); 
                    }
                }
            }
            
            case CALTreeParserTokenTypes.COLON :
                return SourceModel.LocalDefn.PatternMatch.UnpackListCons.makeAnnotated(buildPattern(fullPatternNode.firstChild()), buildPattern(fullPatternNode.getChild(1)), expr, patternMatchDeclSourceRange, fullPatternNode.getSourceRange());
            
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
                return SourceModel.LocalDefn.PatternMatch.UnpackTuple.makeAnnotated(buildPatterns(fullPatternNode), expr, patternMatchDeclSourceRange);
           
            case CALTreeParserTokenTypes.RECORD_PATTERN:
            {
                ParseTreeNode baseRecordPatternNode = fullPatternNode.firstChild();
                baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
                SourceModel.Pattern baseRecordPattern = null;
                if (baseRecordPatternNode.firstChild() != null) {
                    baseRecordPattern = buildPattern(baseRecordPatternNode.firstChild());
                }
                
                ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();
                SourceModel.FieldPattern[] fieldPatterns = buildFieldPatterns(fieldBindingVarAssignmentListNode);
                
                return SourceModel.LocalDefn.PatternMatch.UnpackRecord.makeAnnotated(baseRecordPattern, fieldPatterns, expr, patternMatchDeclSourceRange);                
            }
            
            default:
            {
                throw new IllegalStateException("Unexpected parse tree node " + fullPatternNode.toDebugString() + "."); 
            }
        }
    }
    
    static SourceModel.Expr.Let buildLetExpr(ParseTreeNode letExprNode) {
        letExprNode.verifyType(CALTreeParserTokenTypes.LITERAL_let);
        
        ParseTreeNode defnListNode = letExprNode.firstChild();
        defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);
               
        //these are either local function definitions or local function type declarations
        final int nLocalDefns = defnListNode.getNumberOfChildren();
        SourceModel.LocalDefn[] localDefinitions = new SourceModel.LocalDefn[nLocalDefns];
        int i = 0;
        
        for (final ParseTreeNode defnNode : defnListNode) {
             
            switch (defnNode.getType()) {
                case CALTreeParserTokenTypes.LET_DEFN:                    
                {
                    localDefinitions[i] = buildLocalFunctionDefn(defnNode);
                    break;
                }
                
                case CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION:
                {
                    localDefinitions[i] = buildLocalFunctionTypeDeclaration(defnNode);
                    break;
                }
                
                case CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL:
                {
                    localDefinitions[i] = buildLocalPatternMatchDecl(defnNode);
                    break;
                }
                
                default: 
                {
                    throw new IllegalStateException("Unexpected parse tree node " + defnNode.toDebugString() + ".");
                }
            }
            
            ++i;
        }
                                          
        ParseTreeNode inExprNode = defnListNode.nextSibling();        
       
        return SourceModel.Expr.Let.make(localDefinitions, buildExpr(inExprNode));
    }
    
    static SourceModel.Expr.Lambda buildLambdaExpr(ParseTreeNode lambdaExprNode) {
        lambdaExprNode.verifyType(CALTreeParserTokenTypes.LAMBDA_DEFN);
        
        ParseTreeNode paramListNode = lambdaExprNode.firstChild();        
        ParseTreeNode exprNode = paramListNode.nextSibling();
        
        return SourceModel.Expr.Lambda.makeAnnotated(buildParameters(paramListNode), buildExpr(exprNode), lambdaExprNode.getAssemblySourceRange());
    }
    
    static SourceModel.Parameter[] buildParameters(ParseTreeNode paramListNode) {
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
        
        final int nParams = paramListNode.getNumberOfChildren();
        int paramN = 0;
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[nParams];
        for (final ParseTreeNode paramNode : paramListNode) {
            parameters[paramN] = buildParameter(paramNode);
            
            ++paramN;
        }        
        
        return parameters;
    }
    
    static SourceModel.Parameter buildParameter(ParseTreeNode parameterNode) {
        parameterNode.verifyType(CALTreeParserTokenTypes.STRICT_PARAM, CALTreeParserTokenTypes.LAZY_PARAM);
        
        boolean isStrict = parameterNode.getType() == CALTreeParserTokenTypes.STRICT_PARAM;
        return SourceModel.Parameter.makeAnnotated(parameterNode.getText(), isStrict, parameterNode.getAssemblySourceRange());
    }
    
    static SourceModel.FieldPattern[] buildFieldPatterns(ParseTreeNode fieldBindingVarAssignmentListNode) {
        
        fieldBindingVarAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST); 
        int nFieldPatterns = fieldBindingVarAssignmentListNode.getNumberOfChildren();                
        SourceModel.FieldPattern[] fieldPatterns = new SourceModel.FieldPattern[nFieldPatterns];
        int i = 0;
        
        for (final ParseTreeNode fieldPatternNode : fieldBindingVarAssignmentListNode) {
            
            fieldPatternNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT);
            ParseTreeNode fieldNameNode = fieldPatternNode.firstChild();
            SourceModel.Name.Field fieldName = buildFieldName(fieldNameNode);
            
            SourceModel.Pattern pattern = null;
            ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
            if (patternVarNode != null) {                        
                pattern = buildPattern(patternVarNode);                        
            }
            
            fieldPatterns[i] = SourceModel.FieldPattern.makeAnnotated(fieldName, pattern);
            
            ++i;
        }
        
        return fieldPatterns;
    }

    /**
     * Builds a source model representation of a field name from the given ParseTreeNode.
     * @param fieldNameNode the ParseTreeNode representing a field name.
     * @return a source model representation of the field name.
     */
    static SourceModel.Name.Field buildFieldName(final ParseTreeNode fieldNameNode) {
        return SourceModel.Name.Field.makeAnnotated(FieldName.make(fieldNameNode.getText()), fieldNameNode.getAssemblySourceRange());
    }
    
    static SourceModel.Pattern[] buildPatterns(ParseTreeNode patternListNode) {
               
        patternListNode.verifyType(CALTreeParserTokenTypes.PATTERN_VAR_LIST, CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR);
        final int nPatterns = patternListNode.getNumberOfChildren();
        SourceModel.Pattern[] patterns = new SourceModel.Pattern[nPatterns];
        int i = 0;
        
        for (final ParseTreeNode patternNode : patternListNode) {
            
            patterns[i] = buildPattern(patternNode);
            ++i;                
        }
        
        return patterns;        
    }
    
    static SourceModel.Pattern buildPattern(ParseTreeNode patternNode) {
        patternNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.UNDERSCORE);
        
        if (patternNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
            return SourceModel.Pattern.Var.makeAnnotated(patternNode.getText(), patternNode.getAssemblySourceRange());
        }
        
        return SourceModel.Pattern.Wildcard.make();        
    }
            
    static SourceModel.Expr.Case.Alt buildCaseAlt(ParseTreeNode altNode) {
        
        altNode.verifyType(CALTreeParserTokenTypes.ALT);
        
        ParseTreeNode fullPatternNode = altNode.firstChild();
        
        ParseTreeNode altExprNode = fullPatternNode.nextSibling();
        SourceModel.Expr altExpr = buildExpr(altExprNode);
               
        switch (fullPatternNode.getType()) {
        
            case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR :
            {
                ParseTreeNode dataConsNameListNode = fullPatternNode.firstChild();
                
                List<DataCons> dataConsNameList = new ArrayList<DataCons>();
                for (final ParseTreeNode dataConsNameNode : dataConsNameListNode) {
                    dataConsNameList.add(buildDataConsName(dataConsNameNode));
                }
                SourceModel.Name.DataCons[] dataConsNameArray = 
                    dataConsNameList.toArray(new SourceModel.Name.DataCons[dataConsNameList.size()]);
                
                ParseTreeNode argBindingsNode = dataConsNameListNode.nextSibling();
                
                switch (argBindingsNode.getType()) {
                    
                    case CALTreeParserTokenTypes.PATTERN_VAR_LIST :
                    {
                        return SourceModel.Expr.Case.Alt.UnpackDataCons.makeAnnotated(dataConsNameArray, buildPatterns(argBindingsNode), altExpr, altNode.getAssemblySourceRange(), dataConsNameListNode.getType() == CALTokenTypes.DATA_CONSTRUCTOR_NAME_LIST);
                    }
                    case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST :
                    {
                        return SourceModel.Expr.Case.Alt.UnpackDataCons.makeAnnotated(dataConsNameArray, buildFieldPatterns(argBindingsNode), altExpr, altNode.getAssemblySourceRange(), dataConsNameListNode.getType() == CALTokenTypes.DATA_CONSTRUCTOR_NAME_LIST);
                    }
                    default :
                    {
                        throw new IllegalStateException("Unexpected parse tree node " + argBindingsNode.toDebugString() + "."); 
                    }
                }
            }
            
            case CALTreeParserTokenTypes.INT_PATTERN :
            {
                ParseTreeNode intListNode = fullPatternNode.firstChild();

                BigInteger[] bigInts = new BigInteger[intListNode.getNumberOfChildren()];
                
                int index = 0;
                for (final ParseTreeNode maybeMinusIntNode : intListNode) {
                    
                    boolean minus;
                    ParseTreeNode intLiteralNode;
                    
                    if (maybeMinusIntNode.getType() == CALTreeParserTokenTypes.MINUS) {
                        minus = true;
                        intLiteralNode = maybeMinusIntNode.firstChild();
                    } else {
                        minus = false;
                        intLiteralNode = maybeMinusIntNode;
                    }
                    
                    intLiteralNode.verifyType(CALTreeParserTokenTypes.INTEGER_LITERAL);
                    
                    String symbolText = intLiteralNode.getText();
                    if (minus) {
                        symbolText = "-" + symbolText;
                    }

                    //may throw a NumberFormatException if unsuccessful in parsing the value. 
                    // This should never happen for nodes created by the parser.
                    bigInts[index] = new BigInteger(symbolText);
                    index++;
                }
                
                return SourceModel.Expr.Case.Alt.UnpackInt.make(bigInts, altExpr);
            }
            
            case CALTreeParserTokenTypes.CHAR_PATTERN :
            {
                ParseTreeNode charListNode = fullPatternNode.firstChild();
                int nCharsInGroup = charListNode.getNumberOfChildren();

                char[] chars = new char[nCharsInGroup];
                
                int index = 0;
                for (final ParseTreeNode charNode : charListNode) {
                    
                    charNode.verifyType(CALTreeParserTokenTypes.CHAR_LITERAL);
                    
                    chars[index] = StringEncoder.unencodeChar(charNode.getText());

                    index++;
                }
                return SourceModel.Expr.Case.Alt.UnpackChar.make(chars, altExpr);
            }
            
            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :            
                return SourceModel.Expr.Case.Alt.UnpackListNil.makeAnnotated(altExpr, fullPatternNode.getAssemblySourceRange());            
            
            case CALTreeParserTokenTypes.COLON :
                return SourceModel.Expr.Case.Alt.UnpackListCons.makeAnnotated(buildPattern(fullPatternNode.firstChild()), buildPattern(fullPatternNode.getChild(1)), altExpr, fullPatternNode.getAssemblySourceRange(), fullPatternNode.getSourceRange());
            
            case CALTreeParserTokenTypes.UNDERSCORE : 
                return SourceModel.Expr.Case.Alt.Default.makeAnnotated(altExpr, fullPatternNode.getAssemblySourceRange());
            
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
                return SourceModel.Expr.Case.Alt.UnpackTuple.make(buildPatterns(fullPatternNode), altExpr);
           
            case CALTreeParserTokenTypes.RECORD_PATTERN:
            {
                ParseTreeNode baseRecordPatternNode = fullPatternNode.firstChild();
                baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
                SourceModel.Pattern baseRecordPattern = null;
                if (baseRecordPatternNode.firstChild() != null) {
                    baseRecordPattern = buildPattern(baseRecordPatternNode.firstChild());
                }
                
                ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();
                SourceModel.FieldPattern[] fieldPatterns = buildFieldPatterns(fieldBindingVarAssignmentListNode);
                
                return SourceModel.Expr.Case.Alt.UnpackRecord.make(baseRecordPattern, fieldPatterns, altExpr);                
            }
            
            default:
            {
                throw new IllegalStateException("Unexpected parse tree node " + fullPatternNode.toDebugString() + "."); 
            }
        }
    } 
    
    static SourceModel.Expr.Case buildCaseExpr(ParseTreeNode caseExprNode) {
        
        caseExprNode.verifyType(CALTreeParserTokenTypes.LITERAL_case);
        
        ParseTreeNode conditionExprNode = caseExprNode.firstChild();
        
        ParseTreeNode altListNode = conditionExprNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);        
        final int nAlts = altListNode.getNumberOfChildren();        
        SourceModel.Expr.Case.Alt[] caseAlts = new SourceModel.Expr.Case.Alt[nAlts];
        int i = 0;
        
        for (final ParseTreeNode altNode : altListNode) {

            caseAlts[i] = buildCaseAlt(altNode);
            ++i;
        }
                              
        return SourceModel.Expr.Case.makeAnnotated(buildExpr(conditionExprNode), caseAlts, caseExprNode.getAssemblySourceRange());
    }
    
    static SourceModel.Expr.If buildIfExpr(ParseTreeNode ifExprNode) {
        return SourceModel.Expr.If.makeAnnotated(buildExpr(ifExprNode.firstChild()), buildExpr(ifExprNode.getChild(1)), buildExpr(ifExprNode.getChild(2)), ifExprNode.getAssemblySourceRange());
    }
    
    static SourceModel.Expr.BinaryOp.Or buildOrExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.BARBAR);
        return SourceModel.Expr.BinaryOp.Or.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
        
    static SourceModel.Expr.BinaryOp.And buildAndExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.AMPERSANDAMPERSAND);
        return SourceModel.Expr.BinaryOp.And.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
    
    static SourceModel.Expr.BinaryOp.Append buildAppendExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.PLUSPLUS);
        return SourceModel.Expr.BinaryOp.Append.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
    
    static SourceModel.Expr.BinaryOp.LessThan buildLessThanExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.LESS_THAN);
        return SourceModel.Expr.BinaryOp.LessThan.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    } 
    
    static SourceModel.Expr.BinaryOp.LessThanEquals buildLessThanEqualsExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS);
        return SourceModel.Expr.BinaryOp.LessThanEquals.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
   
    static SourceModel.Expr.BinaryOp.Equals buildEqualsExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.EQUALSEQUALS);
        return SourceModel.Expr.BinaryOp.Equals.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
    
    static SourceModel.Expr.BinaryOp.NotEquals buildNotEqualsExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.NOT_EQUALS);
        return SourceModel.Expr.BinaryOp.NotEquals.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    } 
    
    static SourceModel.Expr.BinaryOp.GreaterThanEquals buildGreaterThanEqualsExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS);
        return SourceModel.Expr.BinaryOp.GreaterThanEquals.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
    
    static SourceModel.Expr.BinaryOp.GreaterThan buildGreaterThanExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.GREATER_THAN);
        return SourceModel.Expr.BinaryOp.GreaterThan.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
    
    static SourceModel.Expr.BinaryOp.Add buildAddExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.PLUS);
        return SourceModel.Expr.BinaryOp.Add.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
    
    static SourceModel.Expr.BinaryOp.Subtract buildSubtractExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.MINUS);
        return SourceModel.Expr.BinaryOp.Subtract.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    } 
    
    static SourceModel.Expr.BinaryOp.Multiply buildMultiplyExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.ASTERISK);
        return SourceModel.Expr.BinaryOp.Multiply.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    } 
    
    static SourceModel.Expr.BinaryOp.Divide buildDivideExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.SOLIDUS);
        return SourceModel.Expr.BinaryOp.Divide.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
    
    static SourceModel.Expr.BinaryOp.Remainder buildRemainderExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.PERCENT);
        return SourceModel.Expr.BinaryOp.Remainder.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }    
    
    static SourceModel.Expr.BinaryOp.Compose buildComposeExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.POUND);
        return SourceModel.Expr.BinaryOp.Compose.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    } 
    
    static SourceModel.Expr.BinaryOp.Apply buildApplyExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.DOLLAR);
        return SourceModel.Expr.BinaryOp.Apply.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    } 
    
    static SourceModel.Expr.BinaryOp.Cons buildConsExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.COLON);
        return SourceModel.Expr.BinaryOp.Cons.makeAnnotated(buildExpr(exprNode.firstChild()), buildExpr(exprNode.getChild(1)), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }
    
    static SourceModel.Expr.UnaryOp.Negate buildNegateExpr(ParseTreeNode exprNode) {
        exprNode.verifyType(CALTreeParserTokenTypes.UNARY_MINUS);
        return SourceModel.Expr.UnaryOp.Negate.makeAnnotated(buildExpr(exprNode.firstChild()), exprNode.getAssemblySourceRange(), exprNode.getSourceRange());
    }     
    
    static SourceModel.Expr.BinaryOp.BackquotedOperator buildBackquotedOperatorExpr(ParseTreeNode exprNode) 
    {
        exprNode.verifyType(CALTreeParserTokenTypes.BACKQUOTE);
        ParseTreeNode applicationNode = exprNode.getChild(0);

        ParseTreeNode qualifiedNameNode = applicationNode.getChild(0);
        qualifiedNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR, CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        // Two cases: this is a function name being or this is a data constructor being backquoted.
        if (qualifiedNameNode.getType() == CALTreeParserTokenTypes.QUALIFIED_VAR){
            // function name
            return SourceModel.Expr.BinaryOp.BackquotedOperator.Var.makeAnnotated(
                buildVarExpr(qualifiedNameNode),
                buildExpr(applicationNode.getChild(1)),
                buildExpr(applicationNode.getChild(2)),
                exprNode.getAssemblySourceRange(),
                qualifiedNameNode.getAssemblySourceRange()
                );
        }
        else{           
            // data constructor
            return SourceModel.Expr.BinaryOp.BackquotedOperator.DataCons.makeAnnotated(
                buildDataConsExpr(qualifiedNameNode),
                buildExpr(applicationNode.getChild(1)),
                buildExpr(applicationNode.getChild(2)),
                exprNode.getAssemblySourceRange(),
                qualifiedNameNode.getAssemblySourceRange()
                );
        }

    }
    
    static SourceModel.Expr.Application buildApplicationExpr(ParseTreeNode applicationExprNode) 
    {
        applicationExprNode.verifyType(CALTreeParserTokenTypes.APPLICATION);
        
        int nChildren = applicationExprNode.getNumberOfChildren();
        if (nChildren < 2) {
            throw new IllegalArgumentException("an application must involve 2 or more expressions.");
        }
        
        int exprN = 0;
        SourceModel.Expr[] expressions = new SourceModel.Expr[nChildren];
        for (final ParseTreeNode exprNode : applicationExprNode) {
            expressions[exprN] = buildExpr(exprNode);
            ++exprN;
        }

        return SourceModel.Expr.Application.makeAnnotated(expressions, applicationExprNode.getAssemblySourceRange());               
    }
    
    static SourceModel.Expr.DataCons buildDataConsExpr(ParseTreeNode qualifiedConsNode) {
        qualifiedConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        SourceModel.Name.DataCons dataConsName = buildDataConsName(qualifiedConsNode);
     
        return SourceModel.Expr.DataCons.makeAnnotated(dataConsName, dataConsName.getSourceRange());
    }
    
    static SourceModel.Expr.Var buildVarExpr(ParseTreeNode qualifiedVarNode) {
        qualifiedVarNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
        
        SourceModel.Name.Function varName = buildFunctionName(qualifiedVarNode);
        
        return SourceModel.Expr.Var.makeAnnotated(varName, varName.getSourceRange());
    }
    
    static SourceModel.Expr.Literal.Num buildNumLiteralExpr(ParseTreeNode numLiteralNode) {
        
        numLiteralNode.verifyType(CALTreeParserTokenTypes.INTEGER_LITERAL);
        //may throw a NumberFormatException if unsuccessful in parsing the value. This should
        //never happen from nodes created by the parser.
        BigInteger value = new BigInteger(numLiteralNode.getText());       
        return SourceModel.Expr.Literal.Num.makeAnnotated(value, numLiteralNode.getAssemblySourceRange());               
    }
    
    static SourceModel.Expr.Literal.Double buildDoubleLiteralExpr(ParseTreeNode doubleLiteralNode) {
        doubleLiteralNode.verifyType(CALTreeParserTokenTypes.FLOAT_LITERAL);
        //may throw a NumberFormatException if unsuccessful in parsing the value. This should
        //never happen from nodes created by the parser.
        Double value = new Double(doubleLiteralNode.getText());
        return SourceModel.Expr.Literal.Double.makeAnnotated(value.doubleValue(), doubleLiteralNode.getAssemblySourceRange());
    }
    
    static SourceModel.Expr.Literal.Char buildCharLiteralExpr(ParseTreeNode charLiteralNode) {
        char c = StringEncoder.unencodeChar(charLiteralNode.getText());
        return SourceModel.Expr.Literal.Char.makeAnnotated(c, charLiteralNode.getSourceRange());
    }
    
    static SourceModel.Expr.Literal.StringLit buildStringLiteralExpr(ParseTreeNode stringLiteralNode) {
        String unencodedString = StringEncoder.unencodeString(stringLiteralNode.getText());
        return SourceModel.Expr.Literal.StringLit.makeAnnotated(unencodedString, stringLiteralNode.getAssemblySourceRange());
    }
    
    static SourceModel.Expr.Unit buildUnitExpr(ParseTreeNode unitNode) {
        return SourceModel.Expr.Unit.makeAnnotated(unitNode.getAssemblySourceRange());
    }
    
    static SourceModel.Expr.Tuple buildTupleExpr(ParseTreeNode tupleNode) {
        tupleNode.verifyType(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR);
        
        int size = tupleNode.getNumberOfChildren();
        if (size < 2) {
            throw new IllegalArgumentException("must have 2 or more components in a tuple.");
        }
        
        SourceModel.Expr[] components = new SourceModel.Expr[size];
        int i = 0;
        for (final ParseTreeNode componentNode : tupleNode) {
            
            components[i] = buildExpr(componentNode);
            ++i;            
        }
        
        return SourceModel.Expr.Tuple.makeAnnotated(components, tupleNode.getAssemblySourceRange());
    }
    
    static SourceModel.Expr.List buildListExpr(ParseTreeNode listNode) {       
        listNode.verifyType(CALTreeParserTokenTypes.LIST_CONSTRUCTOR);
        
        int nElements = listNode.getNumberOfChildren();
        SourceModel.Expr[] elements = new SourceModel.Expr[nElements];
        
        int i = 0;
        for (final ParseTreeNode elementNode : listNode) {
            
            elements[i] = buildExpr(elementNode);
            ++i;            
        }
        
        return SourceModel.Expr.List.makeAnnotated(elements, listNode.getAssemblySourceRange());        
    }
    
    static SourceModel.Expr.Record buildRecordExpr(ParseTreeNode recordExprNode) {
        recordExprNode.verifyType(CALTreeParserTokenTypes.RECORD_CONSTRUCTOR);
        
        ParseTreeNode baseRecordNode = recordExprNode.firstChild();
        baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);
        ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();
        SourceModel.Expr baseRecordExpr = null;
        if (baseRecordExprNode != null) {
            baseRecordExpr = buildExpr(baseRecordExprNode);
        }
        
        ParseTreeNode fieldModificationListNode = baseRecordNode.nextSibling();
        fieldModificationListNode.verifyType(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST);                              
        
        final int nFieldValuePairs = fieldModificationListNode.getNumberOfChildren();
        int i = 0;
        SourceModel.Expr.Record.FieldModification[] fieldValuePairs = new SourceModel.Expr.Record.FieldModification[nFieldValuePairs];
        
        for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
           
            fieldModificationNode.verifyType(CALTreeParserTokenTypes.FIELD_EXTENSION,
               CALTreeParserTokenTypes.FIELD_VALUE_UPDATE);
            ParseTreeNode fieldNameNode = fieldModificationNode.firstChild(); 
            SourceModel.Name.Field fieldName = buildFieldName(fieldNameNode);                        
            
            ParseTreeNode assignmentNode = fieldNameNode.nextSibling();
            SourceModel.Expr assignmentExpr = buildExpr(assignmentNode);
            
            switch (fieldModificationNode.getType()) {
                case CALTreeParserTokenTypes.FIELD_EXTENSION: 
                    fieldValuePairs[i] = SourceModel.Expr.Record.FieldModification.Extension.make(fieldName, assignmentExpr);
                    break;
                case CALTreeParserTokenTypes.FIELD_VALUE_UPDATE:
                    fieldValuePairs[i] = SourceModel.Expr.Record.FieldModification.Update.make(fieldName, assignmentExpr);
                    break;
                default:
                    throw new IllegalStateException("Unexpected parse tree node " + fieldModificationNode.toDebugString() + ".");            
            }
                
            ++i;
        }
                
        return SourceModel.Expr.Record.make(baseRecordExpr, fieldValuePairs);
    }
    
    static SourceModel.Expr.SelectRecordField buildSelectRecordFieldExpr(ParseTreeNode selectRecordFieldNode) {    
        selectRecordFieldNode.verifyType(CALTreeParserTokenTypes.SELECT_RECORD_FIELD);
        
        ParseTreeNode exprNode = selectRecordFieldNode.firstChild();                      
        ParseTreeNode fieldNameNode = exprNode.nextSibling(); 
        SourceModel.Name.Field fieldName = buildFieldName(fieldNameNode);
        return SourceModel.Expr.SelectRecordField.make(buildExpr(exprNode), fieldName);
    }
    
    static SourceModel.Expr.SelectDataConsField buildSelectDataConsFieldExpr(ParseTreeNode selectDataConsFieldNode) {    
        selectDataConsFieldNode.verifyType(CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD);
        
        ParseTreeNode exprNode = selectDataConsFieldNode.firstChild();
        ParseTreeNode qualifiedConsNode = exprNode.nextSibling();
        
        ParseTreeNode fieldNameNode = qualifiedConsNode.nextSibling(); 
        SourceModel.Name.Field fieldName = buildFieldName(fieldNameNode);
        
        return SourceModel.Expr.SelectDataConsField.makeAnnotated(buildExpr(exprNode), buildDataConsName(qualifiedConsNode), fieldName, qualifiedConsNode.getAssemblySourceRange());
    }
    
    static SourceModel.Expr.ExprTypeSignature buildExprTypeSignature(ParseTreeNode exprTypeSignatureNode) {
        exprTypeSignatureNode.verifyType(CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE);
        
        ParseTreeNode exprNode = exprTypeSignatureNode.firstChild();                
        ParseTreeNode signatureNode = exprNode.nextSibling();
        
        return SourceModel.Expr.ExprTypeSignature.make(buildExpr(exprNode), buildTypeSignature(signatureNode));       
    }
           
    static SourceModel.Expr buildParenExpr(ParseTreeNode parseTree) {
        return SourceModel.Expr.Parenthesized.makeAnnotated(buildExpr(parseTree.firstChild()), parseTree.getSourceRange());
    }
    
    static SourceModel.Expr buildExpr(ParseTreeNode parseTree) {
               
        switch (parseTree.getType()) {

            case CALTreeParserTokenTypes.LITERAL_let :                                 
                return buildLetExpr(parseTree);                                         

            case CALTreeParserTokenTypes.LAMBDA_DEFN :            
                return buildLambdaExpr(parseTree);            

            case CALTreeParserTokenTypes.LITERAL_case :                        
                return buildCaseExpr(parseTree);            

            case CALTreeParserTokenTypes.LITERAL_if :
                return buildIfExpr(parseTree);
            
            case CALTreeParserTokenTypes.BARBAR :
                return buildOrExpr(parseTree);
            
            case CALTreeParserTokenTypes.AMPERSANDAMPERSAND :
                return buildAndExpr(parseTree);
            
            case CALTreeParserTokenTypes.PLUSPLUS :
                return buildAppendExpr(parseTree);
            
            case CALTreeParserTokenTypes.LESS_THAN :
                return buildLessThanExpr(parseTree);
            
            case CALTreeParserTokenTypes.LESS_THAN_OR_EQUALS :
                return buildLessThanEqualsExpr(parseTree);
            
            case CALTreeParserTokenTypes.EQUALSEQUALS :
                return buildEqualsExpr(parseTree);
            
            case CALTreeParserTokenTypes.NOT_EQUALS :
                return buildNotEqualsExpr(parseTree);
            
            case CALTreeParserTokenTypes.GREATER_THAN_OR_EQUALS :
                return buildGreaterThanEqualsExpr(parseTree);
            
            case CALTreeParserTokenTypes.GREATER_THAN :
                return buildGreaterThanExpr(parseTree);
            
            case CALTreeParserTokenTypes.PLUS :
                return buildAddExpr(parseTree);
            
            case CALTreeParserTokenTypes.MINUS :
                return buildSubtractExpr(parseTree);
            
            case CALTreeParserTokenTypes.ASTERISK :
                return buildMultiplyExpr(parseTree);
            
            case CALTreeParserTokenTypes.SOLIDUS :
                return buildDivideExpr(parseTree);
            
            case CALTreeParserTokenTypes.PERCENT:
                return buildRemainderExpr(parseTree);
            
            case CALTreeParserTokenTypes.COLON :
                return buildConsExpr(parseTree);
            
            case CALTreeParserTokenTypes.UNARY_MINUS:
                return buildNegateExpr(parseTree);
                        
            case CALTreeParserTokenTypes.POUND:
                return buildComposeExpr(parseTree);
            
            case CALTreeParserTokenTypes.DOLLAR:
                return buildApplyExpr(parseTree);
            
            case CALTreeParserTokenTypes.BACKQUOTE:
                return buildBackquotedOperatorExpr(parseTree);
                        
            case CALTreeParserTokenTypes.APPLICATION :
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                if (exprNode.nextSibling() == null) {
                    //there are some "artificial" application nodes with only 1 child. 
                    return buildExpr(exprNode);
                }
               
                return buildApplicationExpr(parseTree);
            }
          
            case CALTreeParserTokenTypes.QUALIFIED_VAR :            
                return buildVarExpr(parseTree);
            
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
                return buildDataConsExpr(parseTree);
            
      
            case CALTreeParserTokenTypes.INTEGER_LITERAL :
                return buildNumLiteralExpr(parseTree);
            
            case CALTreeParserTokenTypes.FLOAT_LITERAL :
                return buildDoubleLiteralExpr(parseTree);
            
            case CALTreeParserTokenTypes.CHAR_LITERAL:
                return buildCharLiteralExpr(parseTree);
            
            case CALTreeParserTokenTypes.STRING_LITERAL:
                return buildStringLiteralExpr(parseTree);
            
            
            //A parenthesized expression, a tuple or the trivial type
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
            {                                
                if (parseTree.hasNoChildren()) {
                    return buildUnitExpr(parseTree);
                }
                
                if (parseTree.hasExactlyOneChild()) {
                    //a parenthesized expression
                    return buildParenExpr(parseTree);
                }
                
                return buildTupleExpr(parseTree);                             
            }
            
            //A list data value 
            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
                return buildListExpr(parseTree);
                                
            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR:
                return buildRecordExpr(parseTree);          
            
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD:         
                return buildSelectRecordFieldExpr(parseTree);

            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD:
                return buildSelectDataConsFieldExpr(parseTree);
           
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:   
                return buildExprTypeSignature(parseTree);
          
            default :
            {
                throw new IllegalStateException("Unexpected parse tree node " + parseTree.toDebugString() + ".");                                            
            }
        }              
    }
    
    static SourceModel.TypeExprDefn.Function buildFunctionTypeDefn(ParseTreeNode functionTypeDefnNode) {
        functionTypeDefnNode.verifyType(CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR);
        
        return SourceModel.TypeExprDefn.Function.makeAnnotated(buildTypeExprDefn(functionTypeDefnNode.firstChild()), buildTypeExprDefn(functionTypeDefnNode.getChild(1)), functionTypeDefnNode.getAssemblySourceRange(), functionTypeDefnNode.getSourceRange());        
    }
    
    static SourceModel.TypeExprDefn.Tuple buildTupleTypeDefn(ParseTreeNode tupleTypeDefnNode) {
        
        tupleTypeDefnNode.verifyType(CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR);
        int nChildren = tupleTypeDefnNode.getNumberOfChildren();
        if (nChildren < 2) {
            throw new IllegalArgumentException("must have 2 or more components in a tuple type.");
        }
        
        SourceModel.TypeExprDefn[] components =  new SourceModel.TypeExprDefn[nChildren];
        int i = 0;
        for (final ParseTreeNode componentNode : tupleTypeDefnNode) {
            
            components[i] = buildTypeExprDefn(componentNode);
            ++i;
        }
                
        return SourceModel.TypeExprDefn.Tuple.makeAnnotated(components, tupleTypeDefnNode.getAssemblySourceRange());
    }
    
    static SourceModel.TypeExprDefn.List buildListTypeExprDefn(ParseTreeNode listTypeNode) {       
        listTypeNode.verifyType(CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR);
        
        ParseTreeNode elementNode = listTypeNode.firstChild();
        SourceModel.TypeExprDefn element = buildTypeExprDefn(elementNode);
        
        return SourceModel.TypeExprDefn.List.makeAnnotated(element, listTypeNode.getAssemblySourceRange());        
    } 
    
    static SourceModel.TypeExprDefn.Application buildApplicationTypeExprDefn(ParseTreeNode applicationTypeNode) {
        applicationTypeNode.verifyType(CALTreeParserTokenTypes. TYPE_APPLICATION);
        
        int nChildren = applicationTypeNode.getNumberOfChildren();
        if (nChildren < 2) {
            throw new IllegalArgumentException("a type application must involve 2 or more type expressions.");
        }
        
        int exprN = 0;
        SourceModel.TypeExprDefn[] expressions = new SourceModel.TypeExprDefn[nChildren];
        for (final ParseTreeNode exprNode : applicationTypeNode) {
            expressions[exprN] = buildTypeExprDefn(exprNode);
            ++exprN;
        }
                
        return SourceModel.TypeExprDefn.Application.makeAnnotated(expressions, applicationTypeNode.getAssemblySourceRange());               
    } 
    
    static SourceModel.TypeExprDefn.TypeCons buildTypeCons(ParseTreeNode qualifiedTypeConsNameNode) {
        qualifiedTypeConsNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        SourceModel.Name.TypeCons typeConsName = buildTypeConsName(qualifiedTypeConsNameNode);
       
        return SourceModel.TypeExprDefn.TypeCons.makeAnnotated(typeConsName, qualifiedTypeConsNameNode.getAssemblySourceRange());                
    }
    
    /**
     * Builds a source model representation of a type variable name from the given ParseTreeNode.
     * @param typeVarNameNode the ParseTreeNode representing a type variable name.
     * @return a source model representation of the type variable name.
     */
    static SourceModel.Name.TypeVar buildTypeVarName(ParseTreeNode typeVarNameNode) {
        typeVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        return SourceModel.Name.TypeVar.makeAnnotated(typeVarNameNode.getText(), typeVarNameNode.getAssemblySourceRange());
    }
    
    static SourceModel.TypeExprDefn.TypeVar buildTypeVar(ParseTreeNode typeVarNameNode) {
        typeVarNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                     
        return SourceModel.TypeExprDefn.TypeVar.makeAnnotated(buildTypeVarName(typeVarNameNode), typeVarNameNode.getAssemblySourceRange());                
    } 
    
    static SourceModel.TypeExprDefn.Record buildRecordTypeExprDefn(ParseTreeNode recordTypeNode) {
                
        recordTypeNode.verifyType(CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR);
        
        ParseTreeNode recordVarNode = recordTypeNode.firstChild();
        recordVarNode.verifyType(CALTreeParserTokenTypes.RECORD_VAR);
        ParseTreeNode recordVarNameNode = recordVarNode.firstChild();
        SourceModel.TypeExprDefn.TypeVar recordVarName = null;
        if (recordVarNameNode != null) {
            recordVarName = buildTypeVar(recordVarNameNode);
        }
        
        ParseTreeNode fieldTypeAssignmentListNode = recordVarNode.nextSibling();
        fieldTypeAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT_LIST);                              
        
        final int nFieldTypePairs = fieldTypeAssignmentListNode.getNumberOfChildren();
        int i = 0;
        SourceModel.TypeExprDefn.Record.FieldTypePair[] fieldTypePairs = new SourceModel.TypeExprDefn.Record.FieldTypePair[nFieldTypePairs];
        
        for (final ParseTreeNode fieldTypeAssignmentNode : fieldTypeAssignmentListNode) {
            
            fieldTypeAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_TYPE_ASSIGNMENT);
            ParseTreeNode fieldNameNode = fieldTypeAssignmentNode.firstChild(); 
            SourceModel.Name.Field fieldName = buildFieldName(fieldNameNode);                        
            
            ParseTreeNode typeNode = fieldNameNode.nextSibling();
            SourceModel.TypeExprDefn typeExprDefn = buildTypeExprDefn(typeNode);
            
            fieldTypePairs[i] = SourceModel.TypeExprDefn.Record.FieldTypePair.make(fieldName, typeExprDefn);  
            ++i;
        }
                
        return SourceModel.TypeExprDefn.Record.makeAnnotated(recordVarName, fieldTypePairs, recordTypeNode.getAssemblySourceRange());               
    }
    
    static SourceModel.TypeExprDefn buildParenthesizedTypeExprDefn(ParseTreeNode parseTree) {
        return SourceModel.TypeExprDefn.Parenthesized.makeAnnotated(buildTypeExprDefn(parseTree.firstChild()), parseTree.getSourceRange());   
    }
    
    static SourceModel.TypeExprDefn buildTypeExprDefn(ParseTreeNode parseTree) {
        
        switch (parseTree.getType()) {
            case CALTreeParserTokenTypes.FUNCTION_TYPE_CONSTRUCTOR :            
                return buildFunctionTypeDefn(parseTree);            
            
            case CALTreeParserTokenTypes.TUPLE_TYPE_CONSTRUCTOR :
            {                                                    
                if (parseTree.hasNoChildren()) {
                    return SourceModel.TypeExprDefn.Unit.makeAnnotated(parseTree.getAssemblySourceRange());
                }
                
                if (parseTree.hasExactlyOneChild()) {
                    // the type (t) is equivalent to the type t.
                    return buildParenthesizedTypeExprDefn(parseTree);
                }
                
                return buildTupleTypeDefn(parseTree);              
            }
            
            case CALTreeParserTokenTypes.LIST_TYPE_CONSTRUCTOR :
                return buildListTypeExprDefn(parseTree);           
            
            case CALTreeParserTokenTypes.TYPE_APPLICATION :               
            {               
                if (parseTree.hasExactlyOneChild()) {
                    //not really an application node, but an artifact of parsing
                    return buildTypeExprDefn(parseTree.firstChild());
                }
                
                return buildApplicationTypeExprDefn(parseTree);
            }             
            
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
                return buildTypeCons(parseTree);            
            
            case CALTreeParserTokenTypes.VAR_ID :
                return buildTypeVar(parseTree);           
            
            case CALTreeParserTokenTypes.RECORD_TYPE_CONSTRUCTOR :
                return buildRecordTypeExprDefn(parseTree);          
            
            default :
            {
                throw new IllegalStateException("Unexpected parse tree node " + parseTree.toDebugString() + ".");                                     
            }
        }
    } 
    
    /**
     * Builds a source model representation of a module name in CAL source.
     * Returns null if the given node is the empty module name qualifier.
     * 
     * @param maybeEmptyModuleNameNode the root of the parse tree representing a module name or an empty module name qualifier.
     * @return a source model representation of a module name in CAL source, or null if the given node is the empty module name qualifier.
     */
    static SourceModel.Name.Module buildMaybeEmptyModuleName(ParseTreeNode maybeEmptyModuleNameNode) {
        maybeEmptyModuleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        
        if (maybeEmptyModuleNameNode.getType() == CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER) {
            return null;
        } else {
            return buildModuleName(maybeEmptyModuleNameNode);
        }
    }
    
    /**
     * Builds a source model representation of a module name in CAL source.
     * @param moduleNameNode the root of the parse tree representing a module name.
     * @return a source model representation of a module name in CAL source.
     */
    static SourceModel.Name.Module buildModuleName(ParseTreeNode moduleNameNode) {
        moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME);
        
        ParseTreeNode qualifierNode = moduleNameNode.firstChild();
        SourceModel.Name.Module.Qualifier qualifier = buildModuleNameQualifier(qualifierNode);
        
        ParseTreeNode unqualifiedModuleNameNode = qualifierNode.nextSibling();
        unqualifiedModuleNameNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
        String unqualifiedModuleName = unqualifiedModuleNameNode.getText();
        
        return SourceModel.Name.Module.makeAnnotated(qualifier, unqualifiedModuleName, moduleNameNode.getAssemblySourceRange());
    }
    
    /**
     * Builds a source model representation of a module name qualifier.
     * @param qualifierNode the root of the parse tree representing a module name qualifier.
     * @return a source model representation of a module name qualifier.
     */
    static SourceModel.Name.Module.Qualifier buildModuleNameQualifier(final ParseTreeNode qualifierNode) {
        
        qualifierNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        
        List/*String*/<String> components = new ArrayList<String>();
        
        ParseTreeNode currentQualifierNode = qualifierNode;
        
        // loop through the qualifier tree, extracting components in reverse order
        // e.g. for a qualifier A.B.C, the loop will go through the components in the order: C, B, A
        while (currentQualifierNode.getType() == CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME) {
            
            ParseTreeNode parentQualifierNode = currentQualifierNode.firstChild();
            parentQualifierNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
            
            ParseTreeNode trailingComponentNode = parentQualifierNode.nextSibling();
            trailingComponentNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String trailingComponent = trailingComponentNode.getText();
            
            // add the trailing component to the front (ahead of the components already added)
            // so that for the qualifier A.B.C and trailing component A, A will be added ahead of the list [B, C]
            components.add(0, trailingComponent);
            
            currentQualifierNode = parentQualifierNode;
        }
        
        // we should've reached the end of the chain - i.e. a HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER
        currentQualifierNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        
        return SourceModel.Name.Module.Qualifier.makeAnnotated(
            components.toArray(new String[components.size()]),
            qualifierNode.getAssemblySourceRange());
    }
    
    static SourceModel.Name.TypeClass buildTypeClassName(ParseTreeNode typeClassNameNode) {
        typeClassNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode moduleNameNode = typeClassNameNode.firstChild();
        SourceModel.Name.Module moduleName = buildMaybeEmptyModuleName(moduleNameNode);
        
        ParseTreeNode classNameNode = moduleNameNode.nextSibling();        
        return SourceModel.Name.TypeClass.makeAnnotated(moduleName, classNameNode.getText(), typeClassNameNode.getAssemblySourceRange());
    }
    
    static SourceModel.Name.TypeCons buildTypeConsName(ParseTreeNode qualifiedConsNode) {
        qualifiedConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode moduleNameNode = qualifiedConsNode.firstChild();
        SourceModel.Name.Module moduleName = buildMaybeEmptyModuleName(moduleNameNode);
        
        ParseTreeNode consNameNode = moduleNameNode.nextSibling();        
        return SourceModel.Name.TypeCons.makeAnnotated(moduleName, consNameNode.getText(), qualifiedConsNode.getAssemblySourceRange());            
    }
    
    static SourceModel.Name.Function buildFunctionName(ParseTreeNode qualifiedFunctionNode) {
        qualifiedFunctionNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
        
        ParseTreeNode moduleNameNode = qualifiedFunctionNode.firstChild();
        SourceModel.Name.Module moduleName = buildMaybeEmptyModuleName(moduleNameNode);
        
        ParseTreeNode functionNameNode = moduleNameNode.nextSibling();        
        return SourceModel.Name.Function.makeAnnotated(moduleName, functionNameNode.getText(), qualifiedFunctionNode.getAssemblySourceRange(), functionNameNode.getSourceRange());            
    }
    
    static SourceModel.Name.DataCons buildDataConsName(ParseTreeNode qualifiedConsNode) {
        qualifiedConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode moduleNameNode = qualifiedConsNode.firstChild();
        SourceModel.Name.Module moduleName = buildMaybeEmptyModuleName(moduleNameNode);
        
        ParseTreeNode consNameNode = moduleNameNode.nextSibling();        
        return SourceModel.Name.DataCons.makeAnnotated(moduleName, consNameNode.getText(), qualifiedConsNode.getAssemblySourceRange());        
    }
    
    /**
     * Builds a source model representation of a CALDoc text block from the given ParseTreeNode.
     * @param blockNode the ParseTreeNode representing a CALDoc text block.
     * @return a source model representation of the CALDoc text block.
     */
    static SourceModel.CALDoc.TextBlock buildCALDocTextBlock(ParseTreeNode blockNode) {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT);
        
        return SourceModel.CALDoc.TextBlock.make(buildCALDocTopLevelTextSegments(blockNode));
    }
    
    /**
     * Builds a source model representation of a preformatted text segment in a CALDoc comment from the given ParseTreeNode.
     * @param blockNode the ParseTreeNode representing a CALDoc preformatted text segment.
     * @return a source model representation of a preformatted text segment in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.Preformatted buildCALDocPreformattedBlock(ParseTreeNode blockNode) {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_PREFORMATTED_BLOCK);
        
        return SourceModel.CALDoc.TextSegment.Preformatted.make(buildCALDocTopLevelTextSegments(blockNode));
    }
    
    /**
     * Creates a source model representation of a CALDoc plain text segment from a string builder holding onto a piece of
     * escaped text.
     * @param plainSegmentBuffer the string builder holding onto a piece of escaped text.
     * @return a source model representation of a CALDoc plain text segment.
     */
    private static SourceModel.CALDoc.TextSegment.Plain makeUnescapedCALDocPlainTextSegment(StringBuilder plainSegmentBuffer) {
        String text = plainSegmentBuffer.toString();
        
        text = text.replaceAll("\\\\\\{@", "{@"); // '\{@' -> '{@'
        text = text.replaceAll("\\\\@", "@");     // '\@'  -> '@'
        
        return SourceModel.CALDoc.TextSegment.Plain.make(text);
    }
    
    /**
     * Builds a source model representation of a CALDoc plain text segment from the given ParseTreeNode of the type
     * CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS.
     * 
     * @param blockNode the ParseTreeNode representing a CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS.
     * @return a source model representation of a CALDoc plain text segment.
     */
    static SourceModel.CALDoc.TextSegment.Plain buildCALDocTextBlockWithoutInlineTags(ParseTreeNode blockNode) {
        blockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_BLOCK_WITHOUT_INLINE_TAGS);
        
        StringBuilder plainSegmentBuffer = new StringBuilder();
        
        for (final ParseTreeNode contentNode : blockNode) {
            
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
            
            default:
                throw new IllegalStateException("Unexpected parse tree node " + contentNode.toDebugString() + ".");
            }
        }
        
        return makeUnescapedCALDocPlainTextSegment(plainSegmentBuffer);
    }
    
    /**
     * Builds an array of SourceModel.CALDoc.TextSegment.TopLevel objects representing the text segments that are the children
     * of the supplied ParseTreeNode.
     * 
     * @param parentNodeOfSegments the ParseTreeNode whose children represent text segments in a CALDoc comment.
     * @return an array of SourceModel.CALDoc.TextSegment.TopLevel objects representing the text segments.
     */
    private static SourceModel.CALDoc.TextSegment.TopLevel[] buildCALDocTopLevelTextSegments(ParseTreeNode parentNodeOfSegments) {
        /// We keep a list of the segments that are built as we loop through the children nodes.
        //
        List<TopLevel> segments = new ArrayList<TopLevel>();
        
        ////
        /// A plain text segment is represented as one or more of CALDOC_TEXT_LINE, CALDOC_BLANK_TEXT_LINE and
        /// CALDOC_TEXT_LINE_BREAK. We loop through the children of the supplied node aggregating contiguous blocks
        /// of these nodes into plain text segments. A CALDOC_TEXT_INLINE_BLOCK node represents an inline block and is
        /// handled as an independent segment.
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
                    SourceModel.CALDoc.TextSegment.Plain plainTextSegment = makeUnescapedCALDocPlainTextSegment(plainSegmentBuffer);
                    segments.add(plainTextSegment);
                    // reset the buffer afterwards for future use
                    plainSegmentBuffer = new StringBuilder();
                }
                
                // then process the inline tag segment
                SourceModel.CALDoc.TextSegment.InlineTag inlineTagSegment = buildCALDocInlineTagSegment(contentNode);
                segments.add(inlineTagSegment);
                break;
            }
            
            default:
                throw new IllegalStateException("Unexpected parse tree node " + contentNode.toDebugString() + ".");
            }
        }
        
        // one final flush of the plain text buffer into a segment
        if (plainSegmentBuffer.length() > 0) {
            SourceModel.CALDoc.TextSegment.Plain plainTextSegment = makeUnescapedCALDocPlainTextSegment(plainSegmentBuffer);
            segments.add(plainTextSegment);
        }
        
        /// create the array of source model segments from the list we've been building up.
        //
        return segments.toArray(SourceModel.CALDoc.TextSegment.TopLevel.NO_SEGMENTS);
    }
    
    /**
     * Builds a source model representation of an inline tag segment in a CALDoc comment from the given ParseTreeNode.
     * @param inlineBlockNode the ParseTreeNode representing an inline tag segment in a CALDoc comment.
     * @return a source model representation of an inline tag segment in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag buildCALDocInlineTagSegment(ParseTreeNode inlineBlockNode) {
        inlineBlockNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_INLINE_BLOCK);
        
        ParseTreeNode inlineTagNode = inlineBlockNode.firstChild();
        
        switch (inlineTagNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_TEXT_URL:
            return buildCALDocInlineURLTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK:
            return buildCALDocInlineLinkTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_EMPHASIZED_TEXT:
            return buildCALDocInlineEmTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT:
            return buildCALDocInlineStrongTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUPERSCRIPT_TEXT:
            return buildCALDocInlineSupTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUBSCRIPT_TEXT:
            return buildCALDocInlineSubTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_SUMMARY:
            return buildCALDocInlineSummaryTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_CODE_BLOCK:
            return buildCALDocInlineCodeTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST:
            return buildCALDocInlineListTagSegment(inlineTagNode);
            
        case CALTreeParserTokenTypes.CALDOC_TEXT_UNORDERED_LIST:
            return buildCALDocInlineListTagSegment(inlineTagNode);
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + inlineTagNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of a hyperlinkable URL in a CALDoc comment from the given ParseTreeNode.
     * @param urlNode the ParseTreeNode representing a hyperlinkable URL in a CALDoc comment.
     * @return a source model representation of a hyperlinkable URL in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.URL buildCALDocInlineURLTagSegment(ParseTreeNode urlNode) {
        urlNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_URL);
        
        ParseTreeNode contentNode = urlNode.firstChild();
        SourceModel.CALDoc.TextSegment.Plain content = buildCALDocTextBlockWithoutInlineTags(contentNode);
        
        return SourceModel.CALDoc.TextSegment.InlineTag.URL.make(content);
    }
    
    /**
     * Builds a source model representation of an inline cross-reference in a CALDoc comment from the given ParseTreeNode.
     * @param linkNode the ParseTreeNode representing an inline cross-reference in a CALDoc comment.
     * @return a source model representation of an inline cross-reference in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.Link buildCALDocInlineLinkTagSegment(ParseTreeNode linkNode) {
        linkNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_LINK);
        
        ParseTreeNode linkContextNode = linkNode.firstChild();
        
        switch (linkContextNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_FUNCTION:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.Function.make(buildCALDocFunctionOrClassMethodCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_MODULE:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.Module.make(buildCALDocModuleCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_DATACONS:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.DataCons.make(buildCALDocDataConsCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECONS:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.TypeCons.make(buildCALDocTypeConsCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECLASS:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.TypeClass.make(buildCALDocTypeClassCrossReference(linkContextNode.firstChild()));
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_WITHOUT_CONTEXT:
            return buildCALDocInlineLinkTagWithoutContextSegment(linkNode);
        
        default:
            throw new IllegalStateException("Unexpected parse tree node " + linkNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of an inline cross-reference in a CALDoc comment that appears without
     * a 'context' keyword from the given ParseTreeNode.
     * @param linkNode the ParseTreeNode representing an inline cross-reference in a CALDoc comment.
     * @return a source model representation of the inline cross-reference in a CALDoc comment that appears without
     *         a 'context' keyword from the given ParseTreeNode.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.Link.WithoutContext buildCALDocInlineLinkTagWithoutContextSegment(ParseTreeNode linkNode) {
        
        ParseTreeNode linkContextNode = linkNode.firstChild();
        ParseTreeNode refNode = linkContextNode.firstChild();
        
        switch (refNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.FunctionWithoutContext.make(
                SourceModel.CALDoc.CrossReference.Function.make(buildFunctionName(refNode.firstChild()), false));
            
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.FunctionWithoutContext.make(
                SourceModel.CALDoc.CrossReference.Function.make(buildFunctionName(refNode.firstChild()), true));
            
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.ConsNameWithoutContext.makeAnnotated(
                SourceModel.CALDoc.CrossReference.WithoutContextCons.makeAnnotated(
                        buildWithoutContextConsName(refNode.firstChild()), false, refNode.firstChild().getAssemblySourceRange()),
                        linkNode.getAssemblySourceRange());
            
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.TextSegment.InlineTag.Link.ConsNameWithoutContext.makeAnnotated(
                SourceModel.CALDoc.CrossReference.WithoutContextCons.makeAnnotated(
                        buildWithoutContextConsName(refNode.firstChild()), true, refNode.firstChild().getAssemblySourceRange()),
                        linkNode.getAssemblySourceRange());
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of an identifier that starts with an uppercase character in a scenario
     * where the context of the identifier (i.e. whether it is a module name, a type constructor name, a data constructor
     * name or a type class name) is undetermined.
     * 
     * @param qualifiedConsNode the QUALIFIED_CONS node representing such an identifier.
     * @return the source model representation of such a name 'without context'.
     */
    static SourceModel.Name.WithoutContextCons buildWithoutContextConsName(ParseTreeNode qualifiedConsNode) {
        qualifiedConsNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode moduleNameNode = qualifiedConsNode.firstChild();
        SourceModel.Name.Module moduleName = buildMaybeEmptyModuleName(moduleNameNode);
        
        ParseTreeNode consNameNode = moduleNameNode.nextSibling();        
        return SourceModel.Name.WithoutContextCons.makeAnnotated(moduleName, consNameNode.getText(), qualifiedConsNode.getAssemblySourceRange());            
    }
    
    /**
     * Builds a source model representation of an emphasized piece of text in a CALDoc comment from the given ParseTreeNode.
     * @param emNode the ParseTreeNode representing an emphasized piece of text in a CALDoc comment.
     * @return a source model representation of an emphasized piece of text in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.TextFormatting.Emphasized buildCALDocInlineEmTagSegment(ParseTreeNode emNode) {
        emNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_EMPHASIZED_TEXT);
        
        ParseTreeNode contentNode = emNode.firstChild();
        SourceModel.CALDoc.TextBlock content = buildCALDocTextBlock(contentNode);
        
        return SourceModel.CALDoc.TextSegment.InlineTag.TextFormatting.Emphasized.make(content);
    }
    
    /**
     * Builds a source model representation of a strongly emphasized piece of text in a CALDoc comment from the given ParseTreeNode.
     * @param strongNode the ParseTreeNode representing a strongly emphasized piece of text in a CALDoc comment.
     * @return a source model representation of a strongly emphasized piece of text in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.TextFormatting.StronglyEmphasized buildCALDocInlineStrongTagSegment(ParseTreeNode strongNode) {
        strongNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_STRONGLY_EMPHASIZED_TEXT);
        
        ParseTreeNode contentNode = strongNode.firstChild();
        SourceModel.CALDoc.TextBlock content = buildCALDocTextBlock(contentNode);
        
        return SourceModel.CALDoc.TextSegment.InlineTag.TextFormatting.StronglyEmphasized.make(content);
    }
    
    /**
     * Builds a source model representation of a superscripted piece of text in a CALDoc comment from the given ParseTreeNode.
     * @param supNode the ParseTreeNode representing a superscripted piece of text in a CALDoc comment.
     * @return a source model representation of a superscripted piece of text in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.TextFormatting.Superscript buildCALDocInlineSupTagSegment(ParseTreeNode supNode) {
        supNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUPERSCRIPT_TEXT);
        
        ParseTreeNode contentNode = supNode.firstChild();
        SourceModel.CALDoc.TextBlock content = buildCALDocTextBlock(contentNode);
        
        return SourceModel.CALDoc.TextSegment.InlineTag.TextFormatting.Superscript.make(content);
    }
    
    /**
     * Builds a source model representation of a subscripted piece of text in a CALDoc comment from the given ParseTreeNode.
     * @param subNode the ParseTreeNode representing a subscripted piece of text in a CALDoc comment.
     * @return a source model representation of a subscripted piece of text in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.TextFormatting.Subscript buildCALDocInlineSubTagSegment(ParseTreeNode subNode) {
        subNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUBSCRIPT_TEXT);
        
        ParseTreeNode contentNode = subNode.firstChild();
        SourceModel.CALDoc.TextBlock content = buildCALDocTextBlock(contentNode);
        
        return SourceModel.CALDoc.TextSegment.InlineTag.TextFormatting.Subscript.make(content);
    }
    
    /**
     * Builds a source model representation of a "@summary" inline tag segment in a CALDoc comment from the given ParseTreeNode.
     * @param summaryNode the ParseTreeNode representing a "@summary" inline tag segment in a CALDoc comment.
     * @return a source model representation of a "@summary" inline tag segment in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.Summary buildCALDocInlineSummaryTagSegment(ParseTreeNode summaryNode) {
        summaryNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_SUMMARY);
        
        ParseTreeNode contentNode = summaryNode.firstChild();
        SourceModel.CALDoc.TextBlock content = buildCALDocTextBlock(contentNode);
        
        return SourceModel.CALDoc.TextSegment.InlineTag.Summary.make(content);
    }
    
    /**
     * Builds a source model representation of a code block in a CALDoc comment from the given ParseTreeNode.
     * @param codeNode the ParseTreeNode representing a code block in a CALDoc comment.
     * @return a source model representation of a code block in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.Code buildCALDocInlineCodeTagSegment(ParseTreeNode codeNode) {
        codeNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_CODE_BLOCK);
        
        ParseTreeNode contentNode = codeNode.firstChild();
        SourceModel.CALDoc.TextSegment.Preformatted content = buildCALDocPreformattedBlock(contentNode);
        
        return SourceModel.CALDoc.TextSegment.InlineTag.Code.make(content);
    }
    
    /**
     * Builds a source model representation of a list in a CALDoc comment from the given ParseTreeNode.
     * @param listNode the ParseTreeNode representing a list in a CALDoc comment.
     * @return a source model representation of a list in a CALDoc comment.
     */
    static SourceModel.CALDoc.TextSegment.InlineTag.List buildCALDocInlineListTagSegment(ParseTreeNode listNode) {
        listNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST, CALTreeParserTokenTypes.CALDOC_TEXT_UNORDERED_LIST);
        
        List<SourceModel.CALDoc.TextSegment.InlineTag.List.Item> items = new ArrayList<SourceModel.CALDoc.TextSegment.InlineTag.List.Item>();
        
        for (final ParseTreeNode itemNode : listNode) {

            itemNode.verifyType(CALTreeParserTokenTypes.CALDOC_TEXT_LIST_ITEM);
            
            ParseTreeNode itemContentNode = itemNode.firstChild();
            SourceModel.CALDoc.TextBlock content = buildCALDocTextBlock(itemContentNode);
            
            SourceModel.CALDoc.TextSegment.InlineTag.List.Item item = SourceModel.CALDoc.TextSegment.InlineTag.List.Item.make(content);
            
            items.add(item);
        }
        
        SourceModel.CALDoc.TextSegment.InlineTag.List.Item[] itemsArray = items.toArray(SourceModel.CALDoc.TextSegment.InlineTag.List.NO_ITEMS);
        
        if (listNode.getType() == CALTreeParserTokenTypes.CALDOC_TEXT_ORDERED_LIST) {
            return SourceModel.CALDoc.TextSegment.InlineTag.List.Ordered.make(itemsArray);
        } else {
            return SourceModel.CALDoc.TextSegment.InlineTag.List.Unordered.make(itemsArray);
        }
    }
    
    /**
     * Builds a source model representation of a CALDoc description block from the given ParseTreeNode.
     * @param descBlockNode the ParseTreeNode representing a CALDoc description block.
     * @return a source model representation of the CALDoc description block.
     */
    static SourceModel.CALDoc.TextBlock buildCALDocDescriptionBlock(ParseTreeNode descBlockNode) {
        descBlockNode.verifyType(CALTreeParserTokenTypes.CALDOC_DESCRIPTION_BLOCK);
        
        return buildCALDocTextBlock(descBlockNode.firstChild());
    }
    
    /**
     * Builds a source model representation of a CALDoc tagged block from the given ParseTreeNode.
     * @param taggedBlockNode the ParseTreeNode representing a CALDoc tagged block.
     * @return a source model representation of the CALDoc tagged block.
     */
    static SourceModel.CALDoc.TaggedBlock buildCALDocTaggedBlock(ParseTreeNode taggedBlockNode) {
        
        switch (taggedBlockNode.getType()) {
        
        case CALTreeParserTokenTypes.CALDOC_AUTHOR_BLOCK:
            return SourceModel.CALDoc.TaggedBlock.Author.make(buildCALDocTextBlock(taggedBlockNode.firstChild()));
            
        case CALTreeParserTokenTypes.CALDOC_DEPRECATED_BLOCK:
            return SourceModel.CALDoc.TaggedBlock.Deprecated.make(buildCALDocTextBlock(taggedBlockNode.firstChild()));
            
        case CALTreeParserTokenTypes.CALDOC_RETURN_BLOCK:
            return SourceModel.CALDoc.TaggedBlock.Return.make(buildCALDocTextBlock(taggedBlockNode.firstChild()));

        case CALTreeParserTokenTypes.CALDOC_VERSION_BLOCK:
            return SourceModel.CALDoc.TaggedBlock.Version.make(buildCALDocTextBlock(taggedBlockNode.firstChild()));

        case CALTreeParserTokenTypes.CALDOC_ARG_BLOCK:
        {
            ParseTreeNode argNameNode = taggedBlockNode.firstChild();
            argNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
            return SourceModel.CALDoc.TaggedBlock.Arg.make(buildFieldName(argNameNode), buildCALDocTextBlock(argNameNode.nextSibling()));
        }
            
        case CALTreeParserTokenTypes.CALDOC_SEE_BLOCK:
            return buildCALDocSeeBlock(taggedBlockNode);

        default:
            throw new IllegalStateException("Unexpected parse tree node " + taggedBlockNode.toDebugString() + ".");                                     
        }
    }
    
    /**
     * Builds a source model representation of a CALDoc "@see" block from the given ParseTreeNode.
     * @param taggedBlockNode the ParseTreeNode representing a CALDoc "@see" block.
     * @return a source model representation of the CALDoc "@see" block.
     */
    static SourceModel.CALDoc.TaggedBlock.See buildCALDocSeeBlock(ParseTreeNode taggedBlockNode) {
        
        ParseTreeNode seeBlockNode = taggedBlockNode.firstChild();
        
        switch (seeBlockNode.getType()) {
        
        case CALTreeParserTokenTypes.CALDOC_SEE_FUNCTION_BLOCK:                                           
        {
            SourceModel.CALDoc.CrossReference.Function[] names =
                new SourceModel.CALDoc.CrossReference.Function[seeBlockNode.getNumberOfChildren()];
            
            int i = 0;
            
            for (final ParseTreeNode nameNode : seeBlockNode) {
                names[i] = buildCALDocFunctionOrClassMethodCrossReference(nameNode);
                i++;
            }
            
            return SourceModel.CALDoc.TaggedBlock.See.Function.make(names);
        }
        
        case CALTreeParserTokenTypes.CALDOC_SEE_MODULE_BLOCK:
        {
            SourceModel.CALDoc.CrossReference.Module[] names =
                new SourceModel.CALDoc.CrossReference.Module[seeBlockNode.getNumberOfChildren()];
            
            int i = 0;
            
            for (final ParseTreeNode nameNode : seeBlockNode) {
                names[i] = buildCALDocModuleCrossReference(nameNode);
                i++;
            }
            
            return SourceModel.CALDoc.TaggedBlock.See.Module.make(names);
        }
                        
        case CALTreeParserTokenTypes.CALDOC_SEE_DATACONS_BLOCK:                                        
        {
            SourceModel.CALDoc.CrossReference.DataCons[] names =
                new SourceModel.CALDoc.CrossReference.DataCons[seeBlockNode.getNumberOfChildren()];
            
            int i = 0;
            
            for (final ParseTreeNode nameNode : seeBlockNode) {
                names[i] = buildCALDocDataConsCrossReference(nameNode);
                i++;
            }
            
            return SourceModel.CALDoc.TaggedBlock.See.DataCons.make(names);
        }
                       
        case CALTreeParserTokenTypes.CALDOC_SEE_TYPECONS_BLOCK:                                    
        {
            SourceModel.CALDoc.CrossReference.TypeCons[] names =
                new SourceModel.CALDoc.CrossReference.TypeCons[seeBlockNode.getNumberOfChildren()];
            
            int i = 0;
            
            for (final ParseTreeNode nameNode : seeBlockNode) {
                names[i] = buildCALDocTypeConsCrossReference(nameNode);
                i++;
            }
            
            return SourceModel.CALDoc.TaggedBlock.See.TypeCons.make(names);
        }
                      
        case CALTreeParserTokenTypes.CALDOC_SEE_TYPECLASS_BLOCK:                            
        {
            SourceModel.CALDoc.CrossReference.TypeClass[] names =
                new SourceModel.CALDoc.CrossReference.TypeClass[seeBlockNode.getNumberOfChildren()];
            
            int i = 0;
            
            for (final ParseTreeNode nameNode : seeBlockNode) {
                names[i] = buildCALDocTypeClassCrossReference(nameNode);
                i++;
            }
            
            return SourceModel.CALDoc.TaggedBlock.See.TypeClass.make(names);
        }                
        
        case CALTreeParserTokenTypes.CALDOC_SEE_BLOCK_WITHOUT_CONTEXT:                            
        {
            SourceModel.CALDoc.CrossReference.CanAppearWithoutContext[] names =
                new SourceModel.CALDoc.CrossReference.CanAppearWithoutContext[seeBlockNode.getNumberOfChildren()];
            
            int i = 0;
            
            for (final ParseTreeNode nameNode : seeBlockNode) {
                names[i] = buildCALDocCrossReferenceWithoutContext(nameNode);
                i++;
            }
            
            return SourceModel.CALDoc.TaggedBlock.See.WithoutContext.makeAnnotated(names, taggedBlockNode.getAssemblySourceRange());
        }                
        
        default:
            throw new IllegalStateException("Unexpected parse tree node " + seeBlockNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of a function name appearing in a CALDoc "@see"/"@link" block from the given ParseTreeNode.
     * @param refNode the ParseTreeNode representing a function name appearing in a CALDoc "@see"/"@link" block.
     * @return a source model representation of the function name appearing in a CALDoc "@see"/"@link" block.
     */
    static SourceModel.CALDoc.CrossReference.Function buildCALDocFunctionOrClassMethodCrossReference(ParseTreeNode refNode) {
        
        switch (refNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR:
            return SourceModel.CALDoc.CrossReference.Function.make(buildFunctionName(refNode.firstChild()), false);
            
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR:
            return SourceModel.CALDoc.CrossReference.Function.make(buildFunctionName(refNode.firstChild()), true);
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of a module name appearing in a CALDoc "@see"/"@link" block from the given ParseTreeNode.
     * @param refNode the ParseTreeNode representing a module name appearing in a CALDoc "@see"/"@link" block.
     * @return a source model representation of the module name appearing in a CALDoc "@see"/"@link" block.
     */
    static SourceModel.CALDoc.CrossReference.Module buildCALDocModuleCrossReference(ParseTreeNode refNode) {
        
        switch (refNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_MODULE_NAME:
        {
            ParseTreeNode nameNode = refNode.firstChild();
            SourceModel.Name.Module moduleName = buildModuleName(nameNode);
            return SourceModel.CALDoc.CrossReference.Module.make(moduleName, false);
        }   
        case CALTreeParserTokenTypes.CALDOC_CHECKED_MODULE_NAME:
        {
            ParseTreeNode nameNode = refNode.firstChild();
            SourceModel.Name.Module moduleName = buildModuleName(nameNode);
            return SourceModel.CALDoc.CrossReference.Module.make(moduleName, true);
        }   
        default:
            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of a data constructor name appearing in a CALDoc "@see"/"@link" block from the given ParseTreeNode.
     * @param refNode the ParseTreeNode representing a data constructor name appearing in a CALDoc "@see"/"@link" block.
     * @return a source model representation of the data constructor name appearing in a CALDoc "@see"/"@link" block.
     */
    static SourceModel.CALDoc.CrossReference.DataCons buildCALDocDataConsCrossReference(ParseTreeNode refNode) {
        
        switch (refNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.CrossReference.DataCons.make(buildDataConsName(refNode.firstChild()), false);
            
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.CrossReference.DataCons.make(buildDataConsName(refNode.firstChild()), true);
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of a type constructor name appearing in a CALDoc "@see"/"@link" block from the given ParseTreeNode.
     * @param refNode the ParseTreeNode representing a type constructor name appearing in a CALDoc "@see"/"@link" block.
     * @return a source model representation of the type constructor name appearing in a CALDoc "@see"/"@link" block.
     */
    static SourceModel.CALDoc.CrossReference.TypeCons buildCALDocTypeConsCrossReference(ParseTreeNode refNode) {
        
        switch (refNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.CrossReference.TypeCons.make(buildTypeConsName(refNode.firstChild()), false);
            
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.CrossReference.TypeCons.make(buildTypeConsName(refNode.firstChild()), true);
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of a type class name appearing in a CALDoc "@see"/"@link" block from the given ParseTreeNode.
     * @param refNode the ParseTreeNode representing a type class name appearing in a CALDoc "@see"/"@link" block.
     * @return a source model representation of the type class name appearing in a CALDoc "@see"/"@link" block.
     */
    static SourceModel.CALDoc.CrossReference.TypeClass buildCALDocTypeClassCrossReference(ParseTreeNode refNode) {
        
        switch (refNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.CrossReference.TypeClass.make(buildTypeClassName(refNode.firstChild()), false);
            
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.CrossReference.TypeClass.make(buildTypeClassName(refNode.firstChild()), true);
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of a name appearing in a CALDoc "@see"/"@link" block without context from the given ParseTreeNode.
     * @param refNode the ParseTreeNode representing a name appearing in a CALDoc "@see"/"@link" block without context.
     * @return a source model representation of the name appearing in a CALDoc "@see"/"@link" block without context.
     */
    static SourceModel.CALDoc.CrossReference.CanAppearWithoutContext buildCALDocCrossReferenceWithoutContext(ParseTreeNode refNode) {
        
        switch (refNode.getType()) {
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR:
            return SourceModel.CALDoc.CrossReference.Function.make(buildFunctionName(refNode.firstChild()), false);
            
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR:
            return SourceModel.CALDoc.CrossReference.Function.make(buildFunctionName(refNode.firstChild()), true);
            
        case CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.CrossReference.WithoutContextCons.makeAnnotated(buildWithoutContextConsName(refNode.firstChild()), false, refNode.firstChild().getSourceRange());
            
        case CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS:
            return SourceModel.CALDoc.CrossReference.WithoutContextCons.makeAnnotated(buildWithoutContextConsName(refNode.firstChild()), true, refNode.firstChild().getSourceRange());
            
        default:
            throw new IllegalStateException("Unexpected parse tree node " + refNode.toDebugString() + ".");
        }
    }
    
    /**
     * Builds a source model representation of the list of tagged blocks that form the trailing portion of a CALDoc comment.
     * @param taggedBlocksNode the ParseTreeNode whose children are nodes representing the tagged blocks of the CALDoc comment.
     * @return an array whose elements are the source model representations of the tagged blocks.
     */
    static SourceModel.CALDoc.TaggedBlock[] buildCALDocTaggedBlocks(ParseTreeNode taggedBlocksNode) {
        taggedBlocksNode.verifyType(CALTreeParserTokenTypes.CALDOC_TAGGED_BLOCKS);

        SourceModel.CALDoc.TaggedBlock[] blocks = new SourceModel.CALDoc.TaggedBlock[taggedBlocksNode.getNumberOfChildren()];
        int i = 0;
        
        for (final ParseTreeNode blockNode : taggedBlocksNode) {
            blocks[i] = buildCALDocTaggedBlock(blockNode);
            i++;
        }
        
        return blocks;
    }
    
    /**
     * A helper function that determines whether the scope is explicitly specified or is omitted.
     * @param accessModifierNode
     * @return whether the scope is explicitly specified in the source.
     */
    private static boolean isScopeExplicitlySpecified(ParseTreeNode accessModifierNode) {

        accessModifierNode.verifyType(CALTreeParserTokenTypes.ACCESS_MODIFIER);

        ParseTreeNode scopeNode = accessModifierNode.firstChild();

        return scopeNode != null;       
    }
}
