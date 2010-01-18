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
 * ModuleLevelParseTrees.java
 * Creation date: (Dec 4, 2002)
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class to split the top level declarations/definitions in a module into arrays of like entities
 * (e.g. group all the type class definitions together into an array of ParseTreeNodes).
 * This is helpful for clarity in the subsequent analysis, especially since the compiler must pass through
 * definitions repeatedly.
 * 
 * Creation date (Dec 4, 2002).
 * @author Bo Ilic
 */
final class ModuleLevelParseTrees {
    
    private final ParseTreeNode moduleDefnNode;

    private final List<ParseTreeNode> functionTypeDeclarationNodes;
    private final List<ParseTreeNode> functionDefnNodes;
    private final List<ParseTreeNode> foreignFunctionDefnNodes;
    private final List<ParseTreeNode> primitiveFunctionDeclarationNodes;
    private final List<ParseTreeNode> dataDeclarationNodes;
    private final List<ParseTreeNode> foreignDataDeclarationNodes;
    private final List<ParseTreeNode> typeClassDefnNodes;
    private final List<ParseTreeNode> instanceDefnNodes;

    ModuleLevelParseTrees(ParseTreeNode moduleDefnNode, ParseTreeNode outerDefnListNode) {
        
        moduleDefnNode.verifyType(CALTreeParserTokenTypes.MODULE_DEFN);
        this.moduleDefnNode = moduleDefnNode;      
        
        functionTypeDeclarationNodes = new ArrayList<ParseTreeNode>();
        functionDefnNodes = new ArrayList<ParseTreeNode>();
        foreignFunctionDefnNodes = new ArrayList<ParseTreeNode>();
        primitiveFunctionDeclarationNodes = new ArrayList<ParseTreeNode>();

        dataDeclarationNodes = new ArrayList<ParseTreeNode>();
        foreignDataDeclarationNodes = new ArrayList<ParseTreeNode>();

        typeClassDefnNodes = new ArrayList<ParseTreeNode>();
        instanceDefnNodes = new ArrayList<ParseTreeNode>();
        
        initialize(outerDefnListNode);
    }

    private void initialize(ParseTreeNode outerDefnListNode) {

        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);    

        for (final ParseTreeNode outerDefnNode : outerDefnListNode) {

            switch (outerDefnNode.getType()) {        

                case CALTreeParserTokenTypes.TOP_LEVEL_TYPE_DECLARATION :
                {
                    functionTypeDeclarationNodes.add(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN :
                {
                    functionDefnNodes.add(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.FOREIGN_FUNCTION_DECLARATION :
                {
                    foreignFunctionDefnNodes.add(outerDefnNode);
                    break;
                }
                
                case CALTreeParserTokenTypes.PRIMITIVE_FUNCTION_DECLARATION:
                {
                    primitiveFunctionDeclarationNodes.add(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.DATA_DECLARATION :
                {
                    dataDeclarationNodes.add(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.FOREIGN_DATA_DECLARATION :
                {
                    foreignDataDeclarationNodes.add(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.TYPE_CLASS_DEFN :
                {
                    typeClassDefnNodes.add(outerDefnNode);
                    break;
                }

                case CALTreeParserTokenTypes.INSTANCE_DEFN :
                {
                    instanceDefnNodes.add(outerDefnNode);
                    break;
                }

                default :
                {                    
                    throw new IllegalArgumentException("unexpected module-level node type.");
                }
            }

        }               
    }
           
    /**     
     * @return ParseTreeNode the parse tree for the module's definition.
     */ 
    ParseTreeNode getModuleDefnNode() {
        return moduleDefnNode;
    }

    /**
     * Returns the dataDeclarationNodes.
     * @return List<ParseTreeNode>
     */
    List<ParseTreeNode> getDataDeclarationNodes() {
        return dataDeclarationNodes;
    }

    /**
     * Returns the foreignDataDeclarationNodes.
     * @return List<ParseTreeNode>
     */
    List<ParseTreeNode> getForeignDataDeclarationNodes() {
        return foreignDataDeclarationNodes;
    }

    /**
     * Returns the foreignSCDefnNodes.
     * @return List<ParseTreeNode>
     */
    List<ParseTreeNode> getForeignFunctionDefnNodes() {
        return foreignFunctionDefnNodes;
    }
    
    /**    
     * @return List<ParseTreeNode>
     */
    List<ParseTreeNode> getPrimitiveFunctionDeclarationNodes() {
        return primitiveFunctionDeclarationNodes;
    }    

    /**
     * Returns the instanceDefnNodes.
     * @return List<ParseTreeNode>
     */
    List<ParseTreeNode> getInstanceDefnNodes() {
        return instanceDefnNodes;
    }

    /**
     * Returns the functionDefnNodes.
     * @return List<ParseTreeNode>
     */
    List<ParseTreeNode> getFunctionDefnNodes() {
        return functionDefnNodes;
    }

    /**
     * Returns the functionTypeDeclarationNodes.
     * @return List<ParseTreeNode>
     */
    List<ParseTreeNode> getFunctionTypeDeclarationNodes() {
        return functionTypeDeclarationNodes;
    }

    /**
     * Returns the typeClassDefnNodes.
     * @return List<ParseTreeNode>
     */
    List<ParseTreeNode> getTypeClassDefnNodes() {
        return typeClassDefnNodes;
    }
}
