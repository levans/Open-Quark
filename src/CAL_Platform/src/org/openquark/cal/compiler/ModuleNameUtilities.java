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
 * ModuleNameUtilities.java
 * Creation date: Nov 3, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.compiler;

/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * This class encapsulates compiler-specific logic for working with module names, especially in the form of
 * parse tree nodes. See also the {@link ModuleNameResolver} for the logic for resolving module names.
 *
 * @author Joseph Wong
 */
final class ModuleNameUtilities {

    /** This class is not meant to be instantiated. */
    private ModuleNameUtilities() {}
    
    /**
     * Constructs a parse tree for a module name.
     * @param moduleName a module name.
     * @return a parse tree for the given module name.
     */
    static ParseTreeNode makeParseTreeForModuleName(ModuleName moduleName) {
        return makeParseTreeForMaybeModuleName(moduleName.toSourceText(), null);
    }
    
    /**
     * Constructs a parse tree for a module name.
     * @param moduleName a module name.
     * @return a parse tree for the given module name.
     */
    static ParseTreeNode makeParseTreeForMaybeModuleName(String moduleName) {
        return makeParseTreeForMaybeModuleName(moduleName, null);
    }
    
    /**
     * Constructs a parse tree for a module name with the given source position to be used as the start position
     * of all components in the module name.
     * 
     * @param moduleName a module name.
     * @param sourcePos the source position that is to be used as the start position of all the components.
     * @return a parse tree for the given module name using the given source position.
     */
    static ParseTreeNode makeParseTreeForMaybeModuleName(String moduleName, SourcePosition sourcePos) {
        
        if (moduleName.length() == 0) {
            return new ParseTreeNode(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER, "HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER", sourcePos);
            
        } else {
            String[] components = LanguageInfo.getVerifiedModuleNameComponents(moduleName);
            return makeParseTreeForModuleNameComponents(components, components.length, sourcePos);
        }
    }

    /**
     * Constructs a parse tree from an array of module name components with the given source position to be used as
     * the start position of all components. The number of components actually used in the conversion to parse tree
     * is specified by the <code>upToNotIncludingIndex</code> parameter.
     * 
     * @param components the components of a module name.
     * @param upToNotIncludingIndex an index <em>n</em> such that the parse tree will be generated for components[0..n-1]
     * @param sourcePos the source position that is to be used as the start position of all the components.
     * @return a parse tree for the given module name using the given source position.
     */
    private static ParseTreeNode makeParseTreeForModuleNameComponents(String[] components, final int upToNotIncludingIndex, SourcePosition sourcePos) {
        
        ParseTreeNode hierarchicalModuleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER, "HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER", sourcePos);
        
        for (int i = 0; i < upToNotIncludingIndex; i++) {
            ParseTreeNode qualifierNode = hierarchicalModuleNameNode;
            hierarchicalModuleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, "HIERARCHICAL_MODULE_NAME", sourcePos);
            ParseTreeNode unqualifiedModuleNameNode = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, components[i], sourcePos);
            
            hierarchicalModuleNameNode.setFirstChild(qualifierNode);
            qualifierNode.setNextSibling(unqualifiedModuleNameNode);
        }
        
        return hierarchicalModuleNameNode;
    }
    
    /**
     * Sets the given module name into the given parse tree, adding new nodes and modifying existing nodes as necessary.
     * <p>
     * If the specified module name is the same as the name represented by the parse tree rooted at <code>moduleNameNode</code>,
     * then no modifications will be made.
     * 
     * @param moduleNameNode the root of the parse tree to be modified.
     * @param moduleName the module name to be set into the parse tree.
     */
    static void setModuleNameIntoParseTree(final ParseTreeNode moduleNameNode, ModuleName moduleName) {

        String moduleNameString = moduleName.toSourceText();
        
        String moduleNameFromParseTree = getMaybeModuleNameStringFromParseTree(moduleNameNode);
        
        // no need to proceed further if the name to be set is the same as the one represented by the parse tree
        if (moduleNameFromParseTree.equals(moduleNameString)) {
            return;
        }
        
        // obtain the original module name as appearing in source
        String moduleNameInSourceFromAttribute = moduleNameNode.getModuleNameInSource();
        
        String moduleNameInSource;
        if (moduleNameInSourceFromAttribute != null) {
            moduleNameInSource = moduleNameInSourceFromAttribute;
        } else {
            moduleNameInSource = moduleNameFromParseTree;
        }
        
        moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        
        String[] components = LanguageInfo.getVerifiedModuleNameComponents(moduleNameString);
        final int nComponents = components.length;

        SourcePosition leftmostPos = moduleNameNode.getAssemblySourcePosition();
        
        // We loop through the components of the module name starting from the end, because the parse tree for
        // a module name A.B.C has the structure: (((<empty> A) B) C)
        
        // The following loop goes through to make sure that components[0..componentIndex] corresponds to the subtree
        // rooted at currentModuleNameNode.
        
        int componentIndex = nComponents - 1;
        ParseTreeNode currentModuleNameNode = moduleNameNode;
        
        boolean done = false;
        
        while (componentIndex >= 0) {
            currentModuleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
            
            // If we hit the empty qualifier, we will need to add new nodes for all the remaining components not in tree.
            if (currentModuleNameNode.getType() == CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER) {

                // The parse tree <empty> should become (<parse tree for components[0..componentIndex-1]> components[componentIndex])
                
                ParseTreeNode leftChild = makeParseTreeForModuleNameComponents(components, componentIndex, leftmostPos);
                ParseTreeNode rightChild = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, components[componentIndex], leftmostPos);
        
                // We modify the type of the parse tree node and set in the children.
                currentModuleNameNode.setType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME);
                currentModuleNameNode.setText("HIERARCHICAL_MODULE_NAME");
                currentModuleNameNode.setFirstChild(leftChild);
                leftChild.setNextSibling(rightChild);
                
                // And we are completely done with modifying the parse tree.
                done = true;
                break;
            }
        
            // The parse tree rooted at currentModuleNameNode is of the form (<parent qualifier> <trailing component>)
            // We need to check the <trailing component> against components[componentIndex] now,
            // and <parent qualifier> against components[0..componentIndex-1] in the next iteration of the loop.
            
            ParseTreeNode parentQualifierNode = currentModuleNameNode.firstChild();
            parentQualifierNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        
            ParseTreeNode trailingComponentNode = parentQualifierNode.nextSibling();
            trailingComponentNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String trailingComponent = trailingComponentNode.getText();
        
            String specifiedComponent = components[componentIndex];
        
            if (!specifiedComponent.equals(trailingComponent)) {
        
                // The trailing component differs, so we need to drop both this component and the parent qualifier from the tree
                // and construct new ones (since all the source positions in the parent qualifier in the tree would be wrong)
        
                ParseTreeNode leftChild = makeParseTreeForModuleNameComponents(components, componentIndex, leftmostPos);
                ParseTreeNode rightChild = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, components[componentIndex], leftmostPos);
        
                // We do not need to modify the type of the parse tree node (since we know it is a HIERARCHICAL_MODULE_NAME).
                // We just need to change both its children.
                currentModuleNameNode.setFirstChild(leftChild);
                leftChild.setNextSibling(rightChild);
                
                // And we are completely done with modifying the parse tree.
                done = true;
                break;
            }
        
            // The trailing component matches, so process the parent qualifier in the next iteration.
            currentModuleNameNode = parentQualifierNode;
            componentIndex--;
        }

        // If we are still not done, that means the original parse tree contains at least as many nodes as
        // are needed with the new name.
        
        // So we truncate the remaining module name if needed.
        // (e.g. the parse tree represents A.B.C and we are trying to set just B.C into the tree)..
        if (!done) {
            if (currentModuleNameNode.getType() != CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER) {
                currentModuleNameNode.setType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
                currentModuleNameNode.setText("HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER");
                currentModuleNameNode.setFirstChild(null);
            }
        }
        
        // Finally, mark the moduleNameNode as being synthetically assigned by setting the original module name
        // as appearing in source into the root of the parse tree as a custom attribute. 
        moduleNameNode.setModuleNameInSource(moduleNameInSource);
    }
    
    /**
     * Returns the module name as it original appears in the source, unmodified by resolution steps.
     * If the original name was previously stored in the root of the given parse tree as a custom attribute, that
     * stored name is returned. Otherwise the value obtained via {@link #getMaybeModuleNameStringFromParseTree} is returned.
     * 
     * @param moduleNameNode the root of the parse tree representing a module name.
     * @return the module name as it original appears in the source, unmodified by resolution steps.
     */
    static String getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource(final ParseTreeNode moduleNameNode) {
        
        String moduleNameInSourceFromAttribute = moduleNameNode.getModuleNameInSource();
        
        if (moduleNameInSourceFromAttribute != null) {
            return moduleNameInSourceFromAttribute;
        } else {
            return getMaybeModuleNameStringFromParseTree(moduleNameNode);
        }
    }
    
    /**
     * Returns the module name as represented by the given parse tree, which may be the empty string "".
     * Unlike {@link #getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource}, no attention is paid to the custom attributes
     * stored in the parse tree nodes.
     * 
     * @param moduleNameNode the root of the parse tree representing a module name.
     * @return the module name represented by the given parse tree, which may be the empty string "".
     */
    static String getMaybeModuleNameStringFromParseTree(ParseTreeNode moduleNameNode) {
        
        // we return an empty string for a null parse tree node or a HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER
        if (moduleNameNode == null ||
            moduleNameNode.getType() == CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER) {
            
            return "";
        }
        
        moduleNameNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME);
        
        String moduleName = "";
        
        ParseTreeNode currentNode = moduleNameNode;
        
        int nComponents = 0;
        
        // loop through the parse tree, extracting components in reverse order
        // e.g. for a name A.B.C, the loop will go through the components in the order: C, B, A
        while (currentNode.getType() == CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME) {
            
            ParseTreeNode parentQualifierNode = currentNode.firstChild();
            parentQualifierNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
            
            ParseTreeNode trailingComponentNode = parentQualifierNode.nextSibling();
            trailingComponentNode.verifyType(CALTreeParserTokenTypes.CONS_ID);
            String trailingComponent = trailingComponentNode.getText();
            
            // add the trailing component to the front (ahead of the components already added)
            if (nComponents == 0) {
                moduleName = trailingComponent;
            } else {
                moduleName = trailingComponent + '.' + moduleName;
            }
            
            nComponents++;
            
            currentNode = parentQualifierNode;
        }
        
        // we should've reached the end of the chain - i.e. a HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER
        currentNode.verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        
        return moduleName;
    }
    
    /**
     * Returns the module name as represented by the given parse tree, which may be null if the module name represented is the empty string "".
     * Unlike {@link #getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource}, no attention is paid to the custom attributes
     * stored in the parse tree nodes.
     * 
     * @param moduleNameNode the root of the parse tree representing a module name.
     * @return the module name represented by the given parse tree, or <code>null</code> if the given parse tree represents the empty string "".
     */
    static ModuleName getModuleNameOrNullFromParseTree(ParseTreeNode moduleNameNode) {
        String maybeModuleName = getMaybeModuleNameStringFromParseTree(moduleNameNode);
        if (maybeModuleName.length() == 0) {
            return null;
        } else {
            return ModuleName.make(maybeModuleName);
        }
    }
    
    /**
     * Returns the module name as represented by the given parse tree, which may <strong>not</strong> be the empty string "".
     * Unlike {@link #getMaybeModuleNameStringUnmodifiedAccordingToOriginalSource}, no attention is paid to the custom attributes
     * stored in the parse tree nodes.
     * 
     * @param moduleNameNode the root of the parse tree representing a module name.
     * @return the module name represented by the given parse tree, which may <strong>not</strong> be the empty string "".
     */
    static ModuleName getModuleNameFromParseTree(ParseTreeNode moduleNameNode) {
        String maybeModuleName = getMaybeModuleNameStringFromParseTree(moduleNameNode);
        if (maybeModuleName.length() == 0) {
            throw new IllegalArgumentException("A non-empty module name is not found in the given node");
        }
        return ModuleName.make(maybeModuleName);
    }
    
    /**
     * Resolves the module name represented by the given parse tree, and modifies it to represent the resolved name
     * (if it differs from the original name).
     * 
     * @param moduleNameNode the root of the parse tree representing a module name.
     * @param currentModuleTypeInfo the type info of the current module under whose context module name resolution is to be performed.
     * @param compiler the compiler instance (used for error logging).
     * @param suppressErrorMessageLogging whether to suppress the logging of error messages.
     * @return the resolved name for the given module name.
     */
    static String resolveMaybeModuleNameInParseTree(ParseTreeNode moduleNameNode, ModuleTypeInfo currentModuleTypeInfo, CALCompiler compiler, boolean suppressErrorMessageLogging) {
        
        String maybeModuleName = getMaybeModuleNameStringFromParseTree(moduleNameNode);
        
        if (maybeModuleName.length() > 0) {
            ModuleName moduleName = ModuleName.make(maybeModuleName);
            ModuleNameResolver.ResolutionResult resolution = currentModuleTypeInfo.getModuleNameResolver().resolve(moduleName);

            if (!resolution.isResolvedModuleNameEqualToOriginalModuleName()) {
                setModuleNameIntoParseTree(moduleNameNode, resolution.getResolvedModuleName());
            }

            if (resolution.isAmbiguous()) {
                if (!suppressErrorMessageLogging) {
                    compiler.logMessage(new CompilerMessage(moduleNameNode, new MessageKind.Error.AmbiguousPartiallyQualifiedFormModuleName(moduleName, resolution.getPotentialMatches())));
                }
            }

            return resolution.getResolvedModuleName().toSourceText();
            
        } else {
            // if the module name node represents the empty string "", then an empty string is returned.
            return "";
        }
    }
}
