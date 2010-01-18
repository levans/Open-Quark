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
 * ParseTreeNode.java
 * Creation date: (Dec 5, 2000)
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import org.openquark.cal.compiler.Expression.ErrorInfo;
import org.openquark.util.EmptyIterator;
import org.openquark.util.UnsafeCast;

import antlr.Token;
import antlr.collections.AST;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * ParseTreeNode is the class used for nodes in the abstract syntax tree within CAL. It augments
 * antlr's default abstract syntax tree node class, CommonAST with positional information to help
 * with error reporting during type checking.
 * Creation date: (Dec 5, 2000)
 * @author Bo Ilic
 */
public final class ParseTreeNode extends antlr.CommonAST implements Iterable<ParseTreeNode> {
       
    private static final long serialVersionUID = -301432238203275995L;

    /** identifies the range in the CAL source stream that this node occupies.  May be null. */
    private SourceRange sourceRange;
    
    /**
     * The map containing custom attribute key-value pairs associated with this node.
     * 
     * It is not recommended that additional fields be added to this class unless the fields are
     * applicable to all parse tree nodes. Instead, this attribute mechanism should be used instead
     * to associate additional data with nodes of particular types.
     */
    private Map<AttributeKey, Object> attributes;
    
    /**
     * A typesafe enumeration of the attribute keys that can be used with the attributes map.
     *
     * @author Joseph Wong
     */
    private static final class AttributeKey {
        /**
         * The key for the attribute storing the module name as appearing in the source.
         * The node itself may contain a fully-qualified module name introduced by the name resolution process.
         */
        private static final AttributeKey MODULE_NAME_IN_SOURCE = new AttributeKey("ModuleNameInSource");
        /**
         * The key for the attribute storing the function type for a local function's CALDoc
         * comment, which is subsequently retrieved in the process of verifying the validity of
         * the CALDoc comment.
         */
        private static final AttributeKey FUNCTION_TYPE_FOR_LOCAL_FUNCTION_CALDOC_COMMENT = new AttributeKey("FunctionTypeForLocalFunctionCALDocComment");
        /**
         * The key for the attribute storing the QualifiedName of the data constructor as which
         * the CALDoc cross reference can be resolved. (The cross reference would be the kind that lacks the 'context' keyword,
         * e.g. {at-link Nothing at-}.)
         */
        private static final AttributeKey CALDOC_CROSS_REFERENCE_RESOLVED_AS_DATA_CONSTRUCTOR = new AttributeKey("CALDocCrossReferenceResolvedAsDataConstructor");
        /**
         * The key for the attribute storing the category of a CALDoc cross reference that consists of an uppercase identifier
         * and that lacks an associated 'context' keyword, e.g. {at-link Nothing at-}.
         */
        private static final AttributeKey CATEGORY_FOR_CALDOC_CONS_NAME_WITHOUT_CONTEXT_CROSS_REFERENCE = new AttributeKey("CategoryForCALDocConsNameWithoutContextCrossReference");
        /**
         * The key for the attribute storing the ErrorInfo object for a node representing an error call.
         */
        private static final AttributeKey ERROR_INFO_FOR_ERROR_CALL = new AttributeKey("ErrorInfoForErrorCall");
        /**
         * The key for the attribute storing the unused variable name for a wildcard pattern variable (i.e. the underscore).
         */
        private static final AttributeKey UNUSED_VAR_NAME_FOR_WILDCARD_PATTERN_VAR = new AttributeKey("UnusedVarNameForWildcardPatternVar");
        /**
         * The key for the attribute storing a (Byte/Short/Integer/Long) value for an
         * INTEGER_LITERAL node or a MINUS node representing a negated integer.
         */
        private static final AttributeKey LITERAL_VALUE_FOR_MAYBE_MINUS_INT_LITERAL = new AttributeKey("LiteralValueForMaybeMinusIntLiteral");
        /**
         * The key for the attribute storing a (Float/Double) value for a FLOAT_LITERAL node.
         */
        private static final AttributeKey LITERAL_VALUE_FOR_FLOAT_LITERAL = new AttributeKey("LiteralValueForFloatLiteral");
        /**
         * The key for the attribute storing a Character value for a CHAR_LITERAL node.
         */
        private static final AttributeKey CHARACTER_VALUE_FOR_CHAR_LITERAL = new AttributeKey("CharacterValueForCharLiteral");
        
        /**
         * The key for the attribute to mark this node as internally generated, as opposed to read from source
         * this is used to mark function etc that are generated by the deriving clause etc.
         */
        private static final AttributeKey IS_INTERNALLY_GENERATED = new AttributeKey("IsInternallyGenerated");
 
        /**
         * The key for the attribute storing a TypeExpr for a FUNCTION_PARAM_LIST node.
         */
        private static final AttributeKey TYPE_EXPR_FOR_FUNCTION_PARAM_LIST = new AttributeKey("TypeExprForFunctionParamList");
        /**
         * The key for the attribute marking a parameter as being a lifed argument or not.
         */
        private static final AttributeKey IS_LIFTED_ARGUMENT = new AttributeKey("IsLiftedArgument");
        /**
         * The key for the attribute storing a TypeExpr for a case expression node (VIRTUAL_DATA_CONSTRUCTOR_CASE,
         * VIRTUAL_RECORD_CASE, VIRTUAL_TUPLE_CASE).
         */
        private static final AttributeKey TYPE_EXPR_FOR_CASE_EXPR = new AttributeKey("TypeExprForCaseExpr");
        /**
         * The key for the attribute storing a boolean flag marking a node as synthetic with a VAR_ID or SELECT_RECORD_FIELD node.
         * <p>
         * For example:
         * <pre>
         * let Prelude.Cons {head, tail=xs} = foo; in ...
         * </pre>
         * is desugared into:
         * <pre>
         * let
         *     $pattern_head_xs = foo;
         *     head = $pattern_head_xs.Prelude.Cons.head;
         *     xs = $pattern_head_xs.Prelude.Cons.tail;
         * in ...
         * </pre>
         * And all occurences of the VAR_ID <code>$pattern_head_xs</code> will be marked with this attribute set to true.
         * <p>
         * For desugared record and tuple patterns, it is the SELECT_RECORD_FIELD node that will be marked.
         */
        private static final AttributeKey IS_SYNTHETIC_VAR_OR_RECORD_FIELD_SELECTION = new AttributeKey("IsSyntheticVarOrRecordFieldSelection");
        /**
         * The key for the attribute storing a boolean flag marking a LET_DEFN node as being a desugared definition for a local pattern match declaration.
         * <p>
         * For example:
         * <pre>
         * let Prelude.Cons {head, tail=xs} = foo; in ...
         * </pre>
         * is desugared into:
         * <pre>
         * let
         *     $pattern_head_xs = foo;
         *     head = $pattern_head_xs.Prelude.Cons.head;
         *     xs = $pattern_head_xs.Prelude.Cons.tail;
         * in ...
         * </pre>
         * And each of the desugared definitions (for <code>$pattern_head_xs</code>, <code>head</code> and <code>xs</code>) will be marked with this attribute set to true.
         */
        private static final AttributeKey IS_DESUGARED_PATTERN_MATCH_FOR_LET_DEFN = new AttributeKey("IsDesugaredPatternMatchForLetDefn");
        /**
         * The key for the attribute storing a SortedSet of {@link FieldName}s corresponding to non-polymorphic record patterns appearing in a local pattern match declaration.
         * <p>
         * For example:
         * <pre>
         * let {#3=x, y, z=alpha} = foo; in ...
         * </pre>
         * is desugared into:
         * <pre>
         * let
         *     $pattern_x_y_alpha = foo;
         *     x = $pattern_x_y_alpha.#3;
         *     y = $pattern_x_y_alpha.y;
         *     alpha = $pattern_x_y_alpha.z;
         * in ...
         * </pre>
         * And the LET_DEFN node for <code>$pattern_head_xs</code> is associated with the set of the declared field names {#3, y, z}.
         */
        private static final AttributeKey DECLARED_FIELDS_IN_NON_POLYMORPHIC_RECORD_PATTERN_MATCH_FOR_LET_DEFN = new AttributeKey("DeclaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn");
        /**
         * The key for the attribute storing a SortedSet of {@link FieldName}s corresponding to polymorphic record patterns appearing in a local pattern match declaration.
         * <p>
         * For example:
         * <pre>
         * let {_ | #8, #9, #3=x, y, z=alpha} = foo; in ...
         * </pre>
         * is desugared into:
         * <pre>
         * let
         *     $pattern_x_y_alpha = foo;
         *     x = $pattern_x_y_alpha.#3;
         *     y = $pattern_x_y_alpha.y;
         *     alpha = $pattern_x_y_alpha.z;
         * in ...
         * </pre>
         * And the LET_DEFN node for <code>$pattern_head_xs</code> is associated with the set of the declared field names {#3, #8, #9, y, z}.
         */
        private static final AttributeKey DECLARED_FIELDS_IN_POLYMORPHIC_RECORD_PATTERN_MATCH_FOR_LET_DEFN = new AttributeKey("DeclaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn");
        
        /** The display name of the attribute key. */
        private final String displayName;
        
        /**
         * Private constructor.
         * @param displayName the display name of the attribute key. 
         */
        private AttributeKey(String displayName) {
            this.displayName = displayName;
        }
        
        /** @return the display name of the attribute key. */
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    /**
     * An Iterator over the children of a ParseTreeNode so that ParseTreeNode can be used
     * with the for-each loops in Java 5. 
     * Does not support modification while doing the iteration of the number and order of the children
     * of the constructor ParseTreeNode, although the children itself may have their children modified. 
     * @author Bo Ilic
     */
    static private class ParseTreeNodeIterator implements Iterator<ParseTreeNode> {

        private ParseTreeNode nextNode;

        private ParseTreeNodeIterator(final ParseTreeNode nextNode) {             
            this.nextNode = nextNode;
        }
        
        /**
         * Makes an iterator over the children of the argument parseTreeNode.         
         */
        static Iterator<ParseTreeNode> make(final ParseTreeNode parseTreeNode) {
            final ParseTreeNode nextNode = parseTreeNode.firstChild();
            if (nextNode == null) {
                return EmptyIterator.<ParseTreeNode>emptyIterator();
            }
           
            return new ParseTreeNodeIterator(nextNode);
        }
        
        /**       
         * Makes an iterator over the subsequent siblings of the argument parseTreeNode 
         * (not including the parseTreeNode itself).
         */
        static Iterator<ParseTreeNode> nextSiblings(final ParseTreeNode parseTreeNode) {
            final ParseTreeNode nextNode = parseTreeNode.nextSibling();
            if (nextNode == null) {
                return EmptyIterator.<ParseTreeNode>emptyIterator();
            }
            
            return new ParseTreeNodeIterator(nextNode);
        }

        /** {@inheritDoc} */
        public boolean hasNext() {                    
            return nextNode != null;
        }

        /** {@inheritDoc} */
        public ParseTreeNode next() {            
            if (nextNode == null) {
                throw new NoSuchElementException();
            }
            final ParseTreeNode returnNode = nextNode;
            nextNode = nextNode.nextSibling();
            return returnNode;           
        }

        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException();                   
        }
    }    
    
    /**     
     * ASTNode constructor.
     * Antlr invokes ParseTreeNode constructors via reflection, so must be public scope. 
     */
    public ParseTreeNode() {
        super();
    }
    
    /**
     * Constructs a ParseTreeNode from a tokenType and node text.
     * Antlr invokes ParseTreeNode constructors via reflection, so must be public scope.
     * 
     *
     * @param tokenType the token type as in CALTreeParserTokenTypes
     * @param text textual name associated with the node
     */
    public ParseTreeNode(int tokenType, String text) {
        this(tokenType, text, null);
    }
    
    /**
     * Constructs a ParseTreeNode from a tokenType, node text and SourcePosition.
     * Antlr invokes ParseTreeNode constructors via reflection, so must be public scope.    
     *
     * Creation date: (4/17/01 9:08:09 AM)
     * @param tokenType
     * @param text
     * @param sourcePosition
     */
    public ParseTreeNode(int tokenType, String text, SourcePosition sourcePosition) {
        super();

        if(sourcePosition == null) {
            sourceRange = null;
        
        } else if(text == null) {
            sourceRange = new SourceRange(sourcePosition, sourcePosition);
        
        } else {
            sourceRange = new SourceRange(sourcePosition, text);
        }
        
        initialize(tokenType, text);
    }
    
    /**
     * ParseTreeNode constructor.
     * Antlr invokes ParseTreeNode constructors via reflection, so must be public scope.
     * 
     * @param arg1 token from which to create a node
     */
    public ParseTreeNode(Token arg1) {
        super(arg1);
    }
    
    /**
     * A helper function that creates the parseTree for a qualifiedVar expression.
     * Creation date: (5/31/01 7:43:14 AM)    
     * @param varName
     * @param sourcePosition
     * @return ParseTreeNode
     */
    static ParseTreeNode makeQualifiedVarNode(QualifiedName varName, SourcePosition sourcePosition) {

        return makeQualifiedVarNode(varName.getModuleName(), varName.getUnqualifiedName(), sourcePosition);
    }       
    
    /**
     * A helper function that creates the parseTree for a qualifiedVar expression.
     * @param moduleName
     * @param varName
     * @param sourcePosition
     * @return ParseTreeNode
     */
    static ParseTreeNode makeQualifiedVarNode(ModuleName moduleName, String varName, SourcePosition sourcePosition) {

        return makeQualifiedVarNode(moduleName.toSourceText(), varName, sourcePosition);
    }
    
    /**
     * A helper function that creates the parseTree for an unqualified qualifiedVar expression.
     * @param varName
     * @param sourcePosition
     * @return ParseTreeNode
     */
    static ParseTreeNode makeUnqualifiedVarNode(String varName, SourcePosition sourcePosition) {

        return makeQualifiedVarNode("", varName, sourcePosition);
    }
    
    /**
     * A helper function that creates the parseTree for a qualifiedVar expression.
     * @param moduleName
     * @param varName
     * @param sourcePosition
     * @return ParseTreeNode
     */
    private static ParseTreeNode makeQualifiedVarNode(String moduleName, String varName, SourcePosition sourcePosition) {

        ParseTreeNode qualifiedVarNode = new ParseTreeNode(CALTreeParserTokenTypes.QUALIFIED_VAR, "QUALIFIED_VAR", sourcePosition);

        ParseTreeNode moduleNameNode = ModuleNameUtilities.makeParseTreeForMaybeModuleName(moduleName.toString(), sourcePosition);
        qualifiedVarNode.setFirstChild(moduleNameNode);

        ParseTreeNode varNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, varName, sourcePosition);
        moduleNameNode.setNextSibling(varNameNode);

        return qualifiedVarNode;
    }
    
    /**
     * A helper function that creates the parseTree for a qualifiedCons expression.
     * @param qualifiedConsName the qualified name.
     * @param sourcePosition the associated source position.
     * @return a new ParseTreeNode representing the qualifiedCons expression.
     */
    static ParseTreeNode makeQualifiedConsNode(QualifiedName qualifiedConsName, SourcePosition sourcePosition) {
               
        ParseTreeNode qualifiedConsNode = new ParseTreeNode(CALTreeParserTokenTypes.QUALIFIED_CONS, "QUALIFIED_CONS", sourcePosition);

        ParseTreeNode moduleNameNode = ModuleNameUtilities.makeParseTreeForMaybeModuleName(qualifiedConsName.getModuleName().toSourceText(), sourcePosition);
        qualifiedConsNode.setFirstChild(moduleNameNode);

        ParseTreeNode varNameNode = new ParseTreeNode(CALTreeParserTokenTypes.CONS_ID, qualifiedConsName.getUnqualifiedName(), sourcePosition);
        moduleNameNode.setNextSibling(varNameNode);

        return qualifiedConsNode;
    }
    
    /**
     * Creates a ParseTreeNode representing an integer literal.
     * @param intValue the int value to be represented as an integer literal ParseTreeNode.
     * @return a new ParseTreeNode representing an integer literal.
     */
    static ParseTreeNode makeIntLiteralNodeWithIntegerValue(int intValue) {
        ParseTreeNode intLiteralNode = new ParseTreeNode(CALTreeParserTokenTypes.INTEGER_LITERAL, String.valueOf(intValue));
        intLiteralNode.setIntegerValueForMaybeMinusIntLiteral(Integer.valueOf(intValue));
        return intLiteralNode;
    }
    
    /**
     * Set all the fields from another parse tree node. However, pointers to this node
     * are still retained.
     * Creation date: (4/19/01 4:32:46 PM)
     * @param node
     */
    void copyContentsFrom(ParseTreeNode node) {
        setType(node.getType());
        setText(node.getText());
        setFirstChild(node.getFirstChild());
        setNextSibling(node.getNextSibling());
        sourceRange = node.sourceRange;      
        attributes = node.attributes;
    }

    /**
     * @return a deep copy of the parse tree rooted at this node.
     */
    ParseTreeNode copyParseTree() {
        ParseTreeNode parseTreeCopy = new ParseTreeNode(getType(), getText(), getSourcePosition());
        parseTreeCopy.attributes = makeAttributesCopyFrom(attributes);
        
        ParseTreeNode firstChild = firstChild();
        if (firstChild != null) {
            parseTreeCopy.setFirstChild(firstChild.copyParseTree());
        }
        
        ParseTreeNode nextSibling = nextSibling();
        if (nextSibling != null) {
            parseTreeCopy.setNextSibling(nextSibling.copyParseTree());
        }
        
        return parseTreeCopy;
    }
    
    /**
     * @return the first child of this node.
     */
    ParseTreeNode firstChild() {
        return (ParseTreeNode)getFirstChild();
    }
    
    /**
     * @return the next sibling in line after this one.
     */
    ParseTreeNode nextSibling() {
        return (ParseTreeNode)getNextSibling();
    }
    
    /**     
     * This is more efficient than calling getNChildren and checking that it is == 0.
     * @return true if this ParseTreeNode has 0 children.
     */
    boolean hasNoChildren() {
        return firstChild() == null;
    }
    
    /**
     * This is more efficient than calling getNChildren and checking that it is == 1.
     * @return true if this ParseTreeNode has exactly 1 child.
     */
    boolean hasExactlyOneChild() {
        ParseTreeNode firstChild = firstChild();
        if (firstChild == null) {
            return false;
        }
        
        return firstChild.nextSibling() == null;
    }
    
    /**
     * Get the child of this ParseTreeNode with the given 0-based index.
     * Creation date: (5/29/01 3:05:03 PM)
     * @return ParseTreeNode
     * @param zeroBasedChildN 0-based index to the child of this ParseTreeNode
     */
    ParseTreeNode getChild(int zeroBasedChildN) {

        AST childNode = getFirstChild();
        for (int i = 0; i < zeroBasedChildN; ++i) {
            childNode = childNode.getNextSibling();
        }

        return (ParseTreeNode) childNode;
    }
    
    /**     
     * @return the last child of this ParseTreeNode, or null if the node has no children.
     */
    ParseTreeNode lastChild() {
        
        AST lastChild = getFirstChild();
        if (lastChild == null) {
            return null;
        }
        
        while (true) {
            AST nextSibling = lastChild.getNextSibling();
            
            if (nextSibling == null) {
                return (ParseTreeNode)lastChild;
            }

            lastChild = nextSibling;
        }
    }
    
    /** 
     * Equivalent to calling addChild sequentially over the children, but more efficient since repeated
     * traversals of the children are avoided.     
     * @param children     
     */
    void addChildren(ParseTreeNode[] children) {
        
        int nChildrenToAdd = children.length;        
        if (nChildrenToAdd == 0) {
            return;
        }
        
        ParseTreeNode lastChild = lastChild();
        if (lastChild == null) {
            
            //the case where this ParseTreeNode has no children        
                                 
            ParseTreeNode currentNode = children[0];
            setFirstChild(currentNode);
                      
            for (int i = 1; i < nChildrenToAdd; ++i) {
                    
                ParseTreeNode nextNode = children[i];
                currentNode.setNextSibling(nextNode);
                currentNode = nextNode;     
            }                            
            
        } else {
            
            //if there are already children, append to the end.
            
            ParseTreeNode currentNode = lastChild;           
                      
            for (int i = 0; i < nChildrenToAdd; ++i) {
                    
                ParseTreeNode nextNode = children[i];
                currentNode.setNextSibling(nextNode);
                currentNode = nextNode;     
            }                          
        }
    }
    
    /**
     * @return the CAL source position corresponding to the AST node (could be null, if this can't be traced).
     * @see #getAssemblySourcePosition
     */
    SourcePosition getSourcePosition() {
        if(sourceRange != null) {
            return sourceRange.getStartSourcePosition();
        } else {
            return null;
        }
    }
    
    /** 
     * @return the source range corresponding to the AST node (may be null).
     * @see #getAssemblySourceRange
     */
    SourceRange getSourceRange() {
        return sourceRange;
    }
    
    /**
     * Returns the source position of the node, if it is non-null, otherwise, recursively searches child nodes
     * for a non-null source position, and returns it. The idea here is that in general nodes not corresponding
     * directly to source tokens will have null sourcePositions, but they are essentially assemblies of nodes,
     * so that the first non-null sourcePosition encountered in a child can be thought of as the position 
     * in the CAL source of the assembly.
     * @return the CAL source position corresponding to the assembly (could be null, if this can't be traced).
     * @see #getSourcePosition
     */
    SourcePosition getAssemblySourcePosition() {
        
        if (sourceRange != null) {
            return sourceRange.getStartSourcePosition();
        }
            
        for (final ParseTreeNode node : this) {
                
            SourcePosition pos = node.getAssemblySourcePosition();
            if (pos != null) {
                return pos;
            }
        }
        
        return null;
    }
       
    /**
     * Returns the SourceRange of the node, if it is non-null, otherwise recursively searches child nodes
     * for non-null source ranges, and returns a new range consisting of the leftmost start position to 
     * the rightmost end position.
     */
    SourceRange getAssemblySourceRange() {
        
        SourcePosition leftEdge = null;
        SourcePosition rightEdge = null;
        
        if(sourceRange != null) {
            leftEdge = sourceRange.getStartSourcePosition();
            rightEdge = sourceRange.getEndSourcePosition();
        }
        
        for(ParseTreeNode node = firstChild(); node != null; node = node.nextSibling()) {
            
            SourceRange range = node.getAssemblySourceRange();
            if(range != null) {

                if(leftEdge == null || 
                   SourcePosition.compareByPosition.compare(range.getStartSourcePosition(), leftEdge) < 0) {
                    leftEdge = range.getStartSourcePosition();
                }
            
                if(rightEdge == null || 
                   SourcePosition.compareByPosition.compare(range.getEndSourcePosition(), rightEdge) > 0) {
                    rightEdge = range.getEndSourcePosition();
                }
            }
        }
        
        if(leftEdge != null && rightEdge != null) {
            return new SourceRange(leftEdge, rightEdge);
        } else {
            return null;
        }
    }
    
    /**
     * Associates a TypeExpr value with a FUNCTION_PARAM_LIST node, storing it in the attributes map.
     * @param typeExpr the TypeExpr value.
     */
    void setTypeExprForFunctionParamList(TypeExpr typeExpr) {
        verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
        putAttributeValue(AttributeKey.TYPE_EXPR_FOR_FUNCTION_PARAM_LIST, typeExpr);
    }
    
    /**
     * Marks this parse tree node (and children) as internally generated
     * @param range in the source code which caused the nodes to be generated, or null.
     */
    void setInternallyGenerated(SourceRange range) {
        putAttributeValue(AttributeKey.IS_INTERNALLY_GENERATED, Boolean.TRUE);
        
        if (range != null) {
            this.sourceRange = range;
        }
        
        for (final ParseTreeNode child : this) {
            child.setInternallyGenerated(range);
        }
    }

    
    /**
     * @return true if the parse node was internally generated.
     */
    boolean isInternallyGenerated() {
        return (Boolean)getAttributeValue(AttributeKey.IS_INTERNALLY_GENERATED) == Boolean.TRUE;
    }
    
    /**
     * Retrieves the associated TypeExpr value of a FUNCTION_PARAM_LIST node, fetching it from the attributes map.
     * @return the associated TypeExpr value.
     */
    TypeExpr getTypeExprForFunctionParamList() {
        verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);
        return (TypeExpr)getAttributeValue(AttributeKey.TYPE_EXPR_FOR_FUNCTION_PARAM_LIST);
    }
    
    /**
     * Marks the parameter as being a lifed argument or not.
     * @param isLifted The flag indicating if the argument is lifted.
     */
    void setIsLiftedArgument(boolean isLifted) {
        verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);
        putAttributeValue(AttributeKey.IS_LIFTED_ARGUMENT, Boolean.valueOf(isLifted));
    }
    
    /**
     * Retrieves the flag indicating if the parameter is was lifted.
     * @return the associated TypeExpr value.
     */
    boolean getIsLiftedArgument() {
        verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);
        Boolean isLiftedArgument = (Boolean) getAttributeValue(AttributeKey.IS_LIFTED_ARGUMENT);
        if (isLiftedArgument == null){
            return false;
        }
        else{
            return isLiftedArgument.booleanValue();
        }
        
    }
    
    /**
     * Associates a TypeExpr value with a case expression node (VIRTUAL_DATA_CONSTRUCTOR_CASE, VIRTUAL_RECORD_CASE, VIRTUAL_TUPLE_CASE),
     * storing it in the attributes map.
     * @param typeExpr the TypeExpr value.
     */
    void setTypeExprForCaseExpr(TypeExpr typeExpr) {
        verifyType(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE,
            CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE, 
            CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE);
        putAttributeValue(AttributeKey.TYPE_EXPR_FOR_CASE_EXPR, typeExpr);
    }
    
    /**
     * Retrieves the associated TypeExpr value of a case expression node (VIRTUAL_DATA_CONSTRUCTOR_CASE, VIRTUAL_RECORD_CASE, VIRTUAL_TUPLE_CASE),
     * fetching it from the attributes map.
     * @return the associated TypeExpr value.
     */
    TypeExpr getTypeExprForCaseExpr() {
        verifyType(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE,
            CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE, 
            CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE);
        return (TypeExpr)getAttributeValue(AttributeKey.TYPE_EXPR_FOR_CASE_EXPR);
    }
    
    /**
     * Sets the module name as appearing in the source. This node may contain a fully-qualified module name introduced by the name resolution process. 
     * @param moduleName the module name as appearing in source.
     */
    void setModuleNameInSource(String moduleName) {
        verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        putAttributeValue(AttributeKey.MODULE_NAME_IN_SOURCE, moduleName);
    }
    
    /**
     * Returns the module name as appearing in the source. This node may contain a fully-qualified module name introduced by the name resolution process.
     * @return the module name as appearing in source.the module name as appearing in source, or null if this node is not modified by the name resolution process.
     */
    String getModuleNameInSource() {
        verifyType(CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME, CALTreeParserTokenTypes.HIERARCHICAL_MODULE_NAME_EMPTY_QUALIFIER);
        return (String)getAttributeValue(AttributeKey.MODULE_NAME_IN_SOURCE);
    }
    
    /**
     * Associates the type of a local function with an OPTIONAL_CALDOC_COMMENT node representing the CALDoc comment
     * for that local function, which is subsequently retrieved in the process of verifying the validity of
     * the CALDoc comment.
     * @param localFunctionType the type of the local function.
     */
    void setFunctionTypeForLocalFunctionCALDocComment(TypeExpr localFunctionType) {
        verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        putAttributeValue(AttributeKey.FUNCTION_TYPE_FOR_LOCAL_FUNCTION_CALDOC_COMMENT, localFunctionType);
    }
    
    /**
     * Retrieves the type of a local function associated with an OPTIONAL_CALDOC_COMMENT node representing the CALDoc comment
     * for that local function, for use in the process of verifying the validity of the CALDoc comment.
     * @return the type of the local function.
     */
    TypeExpr getFunctionTypeForLocalFunctionCALDocComment() {
        verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        return (TypeExpr)getAttributeValue(AttributeKey.FUNCTION_TYPE_FOR_LOCAL_FUNCTION_CALDOC_COMMENT);
    }
    
    /**
     * Associates the QualifiedName of the data constructor which this CALDoc cross reference (appearing without a
     * 'context' keyword) resolves to.
     * @param dataConsName the QualifiedName of the data constructor to which this reference resolves.
     */
    void setCALDocCrossReferenceResolvedAsDataConstructor(QualifiedName dataConsName) {
        verifyType(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS, CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS);
        putAttributeValue(AttributeKey.CALDOC_CROSS_REFERENCE_RESOLVED_AS_DATA_CONSTRUCTOR, dataConsName);
    }
    
    /**
     * Retrieves the QualifiedName of the data constructor which this CALDoc cross reference (appearing without a
     * 'context' keyword) resolves to.
     * @return the QualifiedName of the data constructor to which this reference resolves, or null if the reference
     *         was not processed by a name resolution pass or could not be unambiguously resolved by such a pass.
     */
    QualifiedName getCALDocCrossReferenceResolvedAsDataConstructor() {
        verifyType(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS, CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS);
        return (QualifiedName)getAttributeValue(AttributeKey.CALDOC_CROSS_REFERENCE_RESOLVED_AS_DATA_CONSTRUCTOR);
    }
    
    /**
     * Associates a category of a CALDoc cross reference that consists of an uppercase identifier
     * and that lacks an associated 'context' keyword, e.g. {at-link Nothing at-}.
     * @param category the resolved category of the cross reference.
     */
    void setCategoryForCALDocConsNameWithoutContextCrossReference(CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference category) {
        verifyType(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS, CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS);
        putAttributeValue(AttributeKey.CATEGORY_FOR_CALDOC_CONS_NAME_WITHOUT_CONTEXT_CROSS_REFERENCE, category);
    }
    
    /**
     * Retrieves the associated category of a CALDoc cross reference that consists of an uppercase identifier
     * and that lacks an associated 'context' keyword, e.g. {at-link Nothing at-}.
     * @return the resolved category of the cross reference, or null if the reference was not processed by a name resolution
     *         pass or could not be unambiguously resolved by such a pass.
     */
    CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference getCategoryForCALDocConsNameWithoutContextCrossReference() {
        verifyType(CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS, CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS);
        return (CALDocChecker.CategoryForCALDocConsNameWithoutContextCrossReference)getAttributeValue(AttributeKey.CATEGORY_FOR_CALDOC_CONS_NAME_WITHOUT_CONTEXT_CROSS_REFERENCE);
    }
    
    /**
     * Associates an ErrorInfo object with a node representing an error call, storing it in the attributes map.
     * @param errorInfo the object containing information about the error call.
     */
    void setErrorInfoForErrorCall(ErrorInfo errorInfo) {
        verifyType(
            CALTreeParserTokenTypes.QUALIFIED_VAR,
            CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD,
            CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE,
            CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE,
            CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE);
        putAttributeValue(AttributeKey.ERROR_INFO_FOR_ERROR_CALL, errorInfo);
    }
    
    /**
     * Retrieves the ErrorInfo object associated with a node representing an error call, fetching it from the attributes map.
     * @return the object containing information about the error call.
     */
    ErrorInfo getErrorInfoForErrorCall() {
        verifyType(
            CALTreeParserTokenTypes.QUALIFIED_VAR,
            CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD,
            CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE,
            CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE,
            CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE);
        return (ErrorInfo)getAttributeValue(AttributeKey.ERROR_INFO_FOR_ERROR_CALL);
    }
    
    /**
     * Associates an unused variable name with a node representing a wildcard pattern variable, storing it in the attributes map.
     * @param unusedVarName the unused variable name.
     */
    void setUnusedVarNameForWildcardPatternVar(String unusedVarName) {
        verifyType(CALTreeParserTokenTypes.UNDERSCORE);
        putAttributeValue(AttributeKey.UNUSED_VAR_NAME_FOR_WILDCARD_PATTERN_VAR, unusedVarName);
    }
    
    /**
     * Returns the unused variable name associated with a node representing a wildcard pattern variable, fetching it from the attributes map.
     * @return the unused variable name.
     */
    String getUnusedVarNameForWildcardPatternVar() {
        verifyType(CALTreeParserTokenTypes.UNDERSCORE);
        return (String)getAttributeValue(AttributeKey.UNUSED_VAR_NAME_FOR_WILDCARD_PATTERN_VAR);
    }
    
    /**
     * Verifies this node is an INTEGER_LITERAL node or a MINUS node representing a negated integer.
     */
    private void verifyTypeAsMaybeMinusIntLiteral() {
        if (getType() == CALTreeParserTokenTypes.MINUS) {
            firstChild().verifyType(CALTreeParserTokenTypes.INTEGER_LITERAL);
        } else {
            verifyType(CALTreeParserTokenTypes.INTEGER_LITERAL);    
        }
    }
    
    /**
     * Associates an Integer value with an INTEGER_LITERAL node or a MINUS node representing a negated integer.
     * @param integerValue the Integer value to be associated as the literal representation.
     */
    void setIntegerValueForMaybeMinusIntLiteral(Integer integerValue) {
        verifyTypeAsMaybeMinusIntLiteral();
        putAttributeValue(AttributeKey.LITERAL_VALUE_FOR_MAYBE_MINUS_INT_LITERAL, integerValue);
    }
    
    /**
     * Associates a Byte value with an INTEGER_LITERAL node or a MINUS node representing a negated integer.
     * @param byteValue the Byte value to be associated as the literal representation.
     */
    void setByteValueForMaybeMinusIntLiteral(Byte byteValue) {
        verifyTypeAsMaybeMinusIntLiteral();
        putAttributeValue(AttributeKey.LITERAL_VALUE_FOR_MAYBE_MINUS_INT_LITERAL, byteValue);
    }
    
    /**
     * Associates a Short value with an INTEGER_LITERAL node or a MINUS node representing a negated integer.
     * @param shortValue the Short value to be associated as the literal representation.
     */
    void setShortValueForMaybeMinusIntLiteral(Short shortValue) {
        verifyTypeAsMaybeMinusIntLiteral();
        putAttributeValue(AttributeKey.LITERAL_VALUE_FOR_MAYBE_MINUS_INT_LITERAL, shortValue);
    }
    
    /**
     * Associates a Long value with an INTEGER_LITERAL node or a MINUS node representing a negated integer.
     * @param longValue the Long value to be associated as the literal representation.
     */
    void setLongValueForMaybeMinusIntLiteral(Long longValue) {
        verifyTypeAsMaybeMinusIntLiteral();
        putAttributeValue(AttributeKey.LITERAL_VALUE_FOR_MAYBE_MINUS_INT_LITERAL, longValue);
    }
    
    /**
     * Associates a Long value with an INTEGER_LITERAL node or a MINUS node representing a negated integer.
     * @param bigIntegerValue the BigInteger value to be associated as the literal representation.
     */
    void setBigIntegerValueForMaybeMinusIntLiteral(BigInteger bigIntegerValue) {
        verifyTypeAsMaybeMinusIntLiteral();
        putAttributeValue(AttributeKey.LITERAL_VALUE_FOR_MAYBE_MINUS_INT_LITERAL, bigIntegerValue);
    }    
    
    /**
     * Retrieves the (Byte/Short/Integer/Long/BigInteger) value associated with an INTEGER_LITERAL node or
     * a MINUS node representing a negated integer.
     * @return the associated literal value.
     */
    Number getLiteralValueForMaybeMinusIntLiteral() {
        verifyTypeAsMaybeMinusIntLiteral();
        return (Number)getAttributeValue(AttributeKey.LITERAL_VALUE_FOR_MAYBE_MINUS_INT_LITERAL);
    }
    
    /**
     * Associates a Float value with a FLOAT_LITERAL node, storing it in the attributes map.
     * @param floatValue the Float value to be associated as the literal representation.
     */
    void setFloatValueForFloatLiteral(Float floatValue) {
        verifyType(CALTreeParserTokenTypes.FLOAT_LITERAL);
        putAttributeValue(AttributeKey.LITERAL_VALUE_FOR_FLOAT_LITERAL, floatValue);
    }
    
    /**
     * Associates a Double value with a FLOAT_LITERAL node, storing it in the attributes map.
     * @param doubleValue the Double value to be associated as the literal representation.
     */
    void setDoubleValueForFloatLiteral(Double doubleValue) {
        verifyType(CALTreeParserTokenTypes.FLOAT_LITERAL);
        putAttributeValue(AttributeKey.LITERAL_VALUE_FOR_FLOAT_LITERAL, doubleValue);
    }
    
    /**
     * Retrieves the (Float/Double) value associated with a FLOAT_LITERAL node, fetching it from the attributes map.
     * @return the associated literal value.
     */
    Number getLiteralValueForFloatLiteral() {
        verifyType(CALTreeParserTokenTypes.FLOAT_LITERAL);
        return (Number)getAttributeValue(AttributeKey.LITERAL_VALUE_FOR_FLOAT_LITERAL);
    }
    
    /**
     * Associates a Character value with a CHAR_LITERAL node, storing it in the attributes map.
     * @param characterValue the Character value to be associated as the literal representation.
     */
    void setCharacterValueForCharLiteral(Character characterValue) {
        verifyType(CALTreeParserTokenTypes.CHAR_LITERAL);
        putAttributeValue(AttributeKey.CHARACTER_VALUE_FOR_CHAR_LITERAL, characterValue);
    }
    
    /**
     * Retrieves the Character value associated with a FLOAT_LITERAL node, fetching it from the attributes map.
     * @return the associated Character value.
     */
    Character getCharacterValueForCharLiteral() {
        verifyType(CALTreeParserTokenTypes.CHAR_LITERAL);
        return (Character)getAttributeValue(AttributeKey.CHARACTER_VALUE_FOR_CHAR_LITERAL);
    }
    
    /**
     * Associates a boolean flag marking a node as synthetic with a VAR_ID or SELECT_RECORD_FIELD node, storing it in the attributes map.
     * @param isSynthetic boolean flag marking a node as synthetic.
     */
    void setIsSyntheticVarOrRecordFieldSelection(boolean isSynthetic) {
        verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.SELECT_RECORD_FIELD);
        putAttributeValue(AttributeKey.IS_SYNTHETIC_VAR_OR_RECORD_FIELD_SELECTION, Boolean.valueOf(isSynthetic));
    }
    
    /**
     * Retrieves the boolean flag marking a node as synthetic with a VAR_ID or SELECT_RECORD_FIELD node, fetching it from the attributes map.
     * @return the boolean flag marking a node as synthetic.
     */
    boolean getIsSyntheticVarOrRecordFieldSelection() {
        verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.SELECT_RECORD_FIELD);
        Boolean flag = (Boolean)getAttributeValue(AttributeKey.IS_SYNTHETIC_VAR_OR_RECORD_FIELD_SELECTION);
        return (flag != null) && flag.booleanValue();
    }
    
    /**
     * Associates a boolean flag marking a LET_DEFN node as being a desugared definition for a local pattern match declaration, storing it in the attributes map.
     * @param isDesugaredPatternMatch the boolean flag marking a LET_DEFN node as being a desugared definition for a local pattern match declaration.
     */
    void setIsDesugaredPatternMatchForLetDefn(boolean isDesugaredPatternMatch) {
        verifyType(CALTreeParserTokenTypes.LET_DEFN);
        putAttributeValue(AttributeKey.IS_DESUGARED_PATTERN_MATCH_FOR_LET_DEFN, Boolean.valueOf(isDesugaredPatternMatch));
    }
    
    /**
     * Retrieves the boolean flag marking a LET_DEFN node as being a desugared definition for a local pattern match declaration, fetching it from the attributes map.
     * @return the boolean flag marking a LET_DEFN node as being a desugared definition for a local pattern match declaration.
     */
    boolean getIsDesugaredPatternMatchForLetDefn() {
        verifyType(CALTreeParserTokenTypes.LET_DEFN);
        Boolean flag = (Boolean)getAttributeValue(AttributeKey.IS_DESUGARED_PATTERN_MATCH_FOR_LET_DEFN);
        return (flag != null) && flag.booleanValue();
    }
    
    /**
     * Associates a SortedSet of {@link FieldName}s corresponding to non-polymorphic record patterns appearing in a local pattern match declaration, storing it in the attributes map.
     * @param fieldNames the SortedSet of {@link FieldName}s.
     */
    void setDeclaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn(final SortedSet<FieldName> fieldNames) {
        verifyType(CALTreeParserTokenTypes.LET_DEFN);
        putAttributeValue(AttributeKey.DECLARED_FIELDS_IN_NON_POLYMORPHIC_RECORD_PATTERN_MATCH_FOR_LET_DEFN, fieldNames);
    }
    
    /**
     * Retrieves the SortedSet of {@link FieldName}s corresponding to non-polymorphic record patterns appearing in a local pattern match declaration, fetching it from the attributes map.
     * @return the SortedSet of {@link FieldName}s.
     */
    SortedSet<FieldName> getDeclaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn() {
        verifyType(CALTreeParserTokenTypes.LET_DEFN);
        return UnsafeCast.unsafeCast(getAttributeValue(AttributeKey.DECLARED_FIELDS_IN_NON_POLYMORPHIC_RECORD_PATTERN_MATCH_FOR_LET_DEFN));
    }
    
    /**
     * Associates a SortedSet of {@link FieldName}s corresponding to polymorphic record patterns appearing in a local pattern match declaration, storing it in the attributes map.
     * @param fieldNames the SortedSet of {@link FieldName}s.
     */
    void setDeclaredFieldsInPolymorphicRecordPatternMatchForLetDefn(final SortedSet<FieldName> fieldNames) {
        verifyType(CALTreeParserTokenTypes.LET_DEFN);
        putAttributeValue(AttributeKey.DECLARED_FIELDS_IN_POLYMORPHIC_RECORD_PATTERN_MATCH_FOR_LET_DEFN, fieldNames);
    }
    
    /**
     * Retrieves the SortedSet of {@link FieldName}s corresponding to polymorphic record patterns appearing in a local pattern match declaration, fetching it from the attributes map.
     * @return the SortedSet of {@link FieldName}s.
     */
    SortedSet<FieldName> getDeclaredFieldsInPolymorphicRecordPatternMatchForLetDefn() {
        verifyType(CALTreeParserTokenTypes.LET_DEFN);
        return UnsafeCast.unsafeCast(getAttributeValue(AttributeKey.DECLARED_FIELDS_IN_POLYMORPHIC_RECORD_PATTERN_MATCH_FOR_LET_DEFN));
    }
    
    /**
     * Associates the specified attribute value with the specified key, performing lazy construction of the map on the first call.
     * @param key the attribute key.
     * @param value the attribute value to be associated with the key.
     */
    private void putAttributeValue(AttributeKey key, Object value) {
        if (attributes == null) {
            attributes = new HashMap<AttributeKey, Object>();
        }
        attributes.put(key, value);
    }
    
    /**
     * Retrieves the value associated with the specified attribute key. If the attributes map has not been instantiated, then
     * null is returned.
     * @param key the attribute key.
     * @return the attribute value associated with the key, which could be null if the attributes map 1) is null itself,
     *         2) does not contain the mapping, or 3) has stored null as the value associated with they key.
     */
    private Object getAttributeValue(AttributeKey key) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(key);
    }
    
    /**
     * Copies an attributes map into a new map, performing deep copies of attribute values when necessary.
     * If the original map is null, then null is returned.
     * @param original the map to be copied. Can be null.
     * @return a copy of the original map, or null if the original map is null.
     */
    private static Map<AttributeKey, Object> makeAttributesCopyFrom(Map<AttributeKey, Object> original) {
        if (original == null) {
            return null;
        }
        
        Map<AttributeKey, Object> result = new HashMap<AttributeKey, Object>();
        for (final Map.Entry<AttributeKey, Object> entry : original.entrySet()) {          
            AttributeKey key = entry.getKey();
            Object value = entry.getValue();
            
            // copy the value deeply, if required
            Object copyOfValue;
            if (value instanceof TypeExpr) {
                copyOfValue = ((TypeExpr)value).copyTypeExpr();
            } else {
                copyOfValue = value;
            }
            
            // put the copy of the value in the result map with the original key
            result.put(key, copyOfValue);
        }
        
        return result;
    }
            
    /**
     * Initialize the node from a token.
     * Get the node's CAL source position (line and column) from the token as well.
     * Creation date: (12/6/00 8:29:56 AM)
     */
    @Override
    public void initialize(Token tok) {
        super.initialize(tok);
        
        SourcePosition sourcePosition =  new SourcePosition(tok.getLine(), tok.getColumn(), tok.getFilename());

        if(tok.getText() != null) {
            sourceRange = new SourceRange(sourcePosition, tok.getText());
        } else {
            sourceRange = new SourceRange(sourcePosition, sourcePosition);
        }
    }
    
    /**
     * For SourceRange handling of "paired" lexemes (eg, '(' and ')').
     * We usually retain only the first such lexeme; however, the SourceRange for
     * the retained lexeme should extend until the end of the closing lexeme in
     * the pair.
     * 
     * This method is called by the parser to allow us to update the source range
     * for the retained lexeme to include the extents of omitted lexemes (such as
     * the close delimiter in the example above).
     * @param omittedDelimiter Node representing an omitted delimiter that should
     *         be included in the SourceRange for this node.
     */
    void addOmittedDelimiter(AST omittedDelimiter) {
        ParseTreeNode omittedDelimiterNode = (ParseTreeNode)omittedDelimiter;
        SourceRange omittedRange = omittedDelimiterNode.getSourceRange();
        
        if(sourceRange == null) {
            sourceRange = omittedRange;
        
        } else {
            
            SourcePosition startPos = sourceRange.getStartSourcePosition();
            SourcePosition endPos = sourceRange.getEndSourcePosition();
            
            if(SourcePosition.compareByPosition.compare(omittedRange.getStartSourcePosition(), startPos) < 0) {
                startPos = omittedRange.getStartSourcePosition();
            }
            
            if(SourcePosition.compareByPosition.compare(omittedRange.getEndSourcePosition(), endPos) > 0) {
                endPos = omittedRange.getEndSourcePosition();
            }
            
            sourceRange = new SourceRange(startPos, endPos);
        }
    }
    
    /**
     * Throws an exception for an unexpected parse tree node. Note that the various overloads
     * of verifyType are preferred to using this method since they will give a better
     * error message. This is mainly used when there are too many possible excepted nodes.
     */
    final void unexpectedParseTreeNode() {      
        throw makeExceptionForUnexpectedParseTreeNode();      
    }

    /**
     * @return an exception that can be thrown for an unexpected parse tree node.
     */
    final IllegalStateException makeExceptionForUnexpectedParseTreeNode() {
        return new IllegalStateException("Unexpected parse tree node " + toDebugString() + ".");
    }        
        
    /**
     * Verifies that this node has the specified nodeType. This error should never
     * occur in production code and is mostly intended as a form of documentation and to
     * catch errors during the development of CAL.
     * @param nodeType selected from the constants in CALTreeParserTokenTypes
     * @throws IllegalStateException if this ParseTreeNode does not have the node type given by the argument.    
     */
    final void verifyType(int nodeType) {        
        if (getType() != nodeType) {
            throw new IllegalStateException("Unexpected parse tree node " + toDebugString() +
                ". Expecting node type " + getTypeInfo(nodeType) + ".");
        }               
    }

    /**
     * Verifies that this node has one of the specified nodeType. This error should never
     * occur in production code and is mostly intended as a form of documentation and to
     * catch errors during the development of CAL.
     * @param nodeType1 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType2 selected from the constants in CALTreeParserTokenTypes
     * @throws IllegalStateException if this ParseTreeNode does not have the node type given by the argument.   
     */
    final void verifyType(int nodeType1, int nodeType2) {      
        int nodeType = getType();
        if (nodeType != nodeType1 && nodeType != nodeType2) {
            throw new IllegalStateException("Unexpected parse tree node " + toDebugString() + 
                ". Expecting node type " + getTypeInfo(nodeType1) + " or " + getTypeInfo(nodeType2) + ".");
        }               
    }
    
    /**
     * Verifies that this node has one of the specified nodeType. This error should never
     * occur in production code and is mostly intended as a form of documentation and to
     * catch errors during the development of CAL.
     * @param nodeType1 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType2 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType3 selected from the constants in CALTreeParserTokenTypes
     * @throws IllegalStateException if this ParseTreeNode does not have the node type given by the argument.   
     */
    final void verifyType(int nodeType1, int nodeType2, int nodeType3) {
        int nodeType = getType();
        if (nodeType != nodeType1 && nodeType != nodeType2 && nodeType != nodeType3) {
            throw new IllegalStateException("Unexpected parse tree node " + toDebugString() + 
                ". Expecting node type " + getTypeInfo(nodeType1) + ", " + getTypeInfo(nodeType2) + " or " + getTypeInfo(nodeType3) + ".");
        }            
    }      
    
    /**
     * Verifies that this node has one of the specified nodeType. This error should never
     * occur in production code and is mostly intended as a form of documentation and to
     * catch errors during the development of CAL.
     * @param nodeType1 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType2 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType3 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType4 selected from the constants in CALTreeParserTokenTypes
     * @throws IllegalStateException if this ParseTreeNode does not have the node type given by the argument.   
     */
    final void verifyType(int nodeType1, int nodeType2, int nodeType3, int nodeType4) {
        int nodeType = getType();
        if (nodeType != nodeType1 && nodeType != nodeType2 && nodeType != nodeType3 && nodeType != nodeType4) {
            throw new IllegalStateException("Unexpected parse tree node " + toDebugString() + 
                ". Expecting node type " + getTypeInfo(nodeType1) + ", " + getTypeInfo(nodeType2) + " or " + getTypeInfo(nodeType3) + " or " + getTypeInfo(nodeType4) + ".");
        }            
    }      
    
    /**
     * Verifies that this node has one of the specified nodeType. This error should never
     * occur in production code and is mostly intended as a form of documentation and to
     * catch errors during the development of CAL.
     * @param nodeType1 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType2 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType3 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType4 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType5 selected from the constants in CALTreeParserTokenTypes
     * @throws IllegalStateException if this ParseTreeNode does not have the node type given by the argument.   
     */
    final void verifyType(int nodeType1, int nodeType2, int nodeType3, int nodeType4, int nodeType5) {
        int nodeType = getType();
        if (nodeType != nodeType1 && nodeType != nodeType2 && nodeType != nodeType3 && nodeType != nodeType4 && nodeType != nodeType5) {
            throw new IllegalStateException("Unexpected parse tree node " + toDebugString() + 
                ". Expecting node type " + getTypeInfo(nodeType1) + ", " + getTypeInfo(nodeType2) + " or " + getTypeInfo(nodeType3) + " or " + getTypeInfo(nodeType4) + " or " + getTypeInfo(nodeType5) + ".");
        }            
    }      
    
    /**
     * Verifies that this node has one of the specified nodeType. This error should never
     * occur in production code and is mostly intended as a form of documentation and to
     * catch errors during the development of CAL.
     * @param nodeType1 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType2 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType3 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType4 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType5 selected from the constants in CALTreeParserTokenTypes
     * @param nodeType6 selected from the constants in CALTreeParserTokenTypes
     * @throws IllegalStateException if this ParseTreeNode does not have the node type given by the argument.   
     */
    final void verifyType(int nodeType1, int nodeType2, int nodeType3, int nodeType4, int nodeType5, int nodeType6) {
        int nodeType = getType();
        if (nodeType != nodeType1 && nodeType != nodeType2 && nodeType != nodeType3 && nodeType != nodeType4 && nodeType != nodeType5 && nodeType != nodeType6) {
            throw new IllegalStateException("Unexpected parse tree node " + toDebugString() + 
                ". Expecting node type " + getTypeInfo(nodeType1) + ", " + getTypeInfo(nodeType2) + " or " + getTypeInfo(nodeType3) + " or " + getTypeInfo(nodeType4) + " or " + getTypeInfo(nodeType5)  + " or " + getTypeInfo(nodeType6)+ ".");
        }            
    }      
   
    /**
     * Method getTypeInfo.
     * @param nodeType selected from the constants in CALTreeParserTokenTypes
     * @return a textual description of the nodeType
     */
    private static String getTypeInfo(int nodeType) {
        
        String result;
        try {
            result = CALTreeParser._tokenNames[nodeType];
        } catch (IndexOutOfBoundsException e) {
            result = "" + nodeType;   
        }
        
        return result;
    }
    
    /**
     * Returns a longer textual display of debug info for this node. We don't override toString since
     * this has the undesirable side effect of changing the behavior of toStringTree.
     * Creation date: (6/18/01 5:22:38 PM)
     * @return String
     */
    String toDebugString() {
        StringBuilder result = new StringBuilder();
        result.append('[');
        result.append("type = ").append(getTypeInfo(getType()));
        result.append(", text = ").append(getText());
        result.append(", sourceRange = ").append(sourceRange);
        result.append(']');
        return result.toString();
    } 
    
    /**
     * A useful alternative to toDebugString when debugging the CAL compiler.
     * For example, it can be useful to put a breakpoint in ExpressionGenerator.generateSCDefn,
     * and then examine the parse tree for a particular sc to see that overload resolution and 
     * lambda lifting have proceeded as planned.
     * @return a deep (i.e. includes children) XML representation of the ParseTreeNode suitable
     *      for convenient viewing in an XML viewer such as Internet Explorer.
     */
    String toXMLString() {
        Writer writer = new StringWriter();
        try {        
            xmlDump(writer);
        } catch (IOException e) {
            return null;
        }
        return writer.toString();
    }
    
    /**
     * See the comment for toXMLString.
     * @param fileName
     * @throws IOException
     */
    void xmlDumpToFile(String fileName) throws IOException{
        File file = new File(fileName);
        Writer writer = new FileWriter(file);
        xmlDump(writer);
        writer.close();       
    }
    
    private void xmlDump(Writer out) throws IOException{        
        // print out this node and all children   
            
        if (firstChild() == null) {
            // print node
            xmlDumpNode(out);
        } else {
            xmlDumpRootOpen(out);

            // print children            
            for (final ParseTreeNode child : this) {
                child.xmlDump(out);
            }

            // print end tag
            xmlDumpRootClose(out);
        }
        
    }
    
    private void xmlDumpNode(Writer out) throws IOException {
        StringBuilder buf = new StringBuilder(100);
        buf.append("<node ");       
        buf.append("t=\"" + encode(getText()) + "\"/>");
        out.write(buf.toString());
    }

    private void xmlDumpRootOpen(Writer out) throws IOException {
        StringBuilder buf = new StringBuilder(100);
        buf.append("<node ");      
        buf.append("t=\"" + encode(getText()) + "\">\n");
        out.write(buf.toString());
    }

    private void xmlDumpRootClose(Writer out) throws IOException {
        out.write("</node>\n");
    }

    /** 
     * @return an iterator over the children of this ParseTreeNode.
     * 
     * {@inheritDoc}
     */
    public Iterator<ParseTreeNode> iterator() {                               
        return ParseTreeNodeIterator.make(this);            
    }
    
    /**     
     * @return can be used to iterate over the next siblings of this node
     *    (not including this node) in a for-each loop.
     */
    public Iterable<ParseTreeNode> nextSiblings() {
               
        return 
            new Iterable<ParseTreeNode> () {
                public Iterator<ParseTreeNode> iterator() {                    
                    return ParseTreeNodeIterator.nextSiblings(ParseTreeNode.this);
                }
            };
    }

    /**     
     * @return constructs a QualifiedName from a ParseTreeNode of type QUALIFIED_CONS or QUALIFIED_VAR.
     */
    QualifiedName toQualifiedName() {
        if (getType() != CALTreeParserTokenTypes.QUALIFIED_CONS && getType() != CALTreeParserTokenTypes.QUALIFIED_VAR) {
            throw new IllegalArgumentException("QualifiedName constructor: unexpected parse tree node " + toDebugString());
        }
    
        ParseTreeNode moduleNameNode = firstChild();
        String moduleName = ModuleNameUtilities.getMaybeModuleNameStringFromParseTree(moduleNameNode);
    
        ParseTreeNode nameNode = moduleNameNode.nextSibling();
        String unqualifiedName = nameNode.getText();
    
        return QualifiedName.make(ModuleName.make(moduleName), unqualifiedName);
    }
}
