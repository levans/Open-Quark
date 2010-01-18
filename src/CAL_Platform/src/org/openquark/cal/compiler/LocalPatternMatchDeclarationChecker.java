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
 * LocalPatternMatchDeclarationChecker.java
 * Created: Feb 21, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.compiler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openquark.cal.module.Cal.Core.CAL_Prelude;

/**
 * This class encapsulates logic for checking pattern match declarations that appear within
 * let expressions, as well as logic for desugaring them into regular local function definitions.
 * <p>
 * These are the checks that are done on the declarations:
 * <ul>
 * <li>that the pattern declares at least one pattern-bound variable
 * <li>that the pattern is not the unit pattern <code>()</code>
 * <li>that a data constructor pattern with positional argument patterns contains the right number of such argument patterns
 * <li>that a data constructor pattern with field-pattern pairs contains field names that appear in the data constructor
 * <li>that a data constructor pattern refers to a data constructor with positive arity
 * <li>that a polymorphic record pattern does not contain a variable (instead of a wildcard) for its base record pattern
 * <li>that a pattern does not declare a pattern-bound variable more than once
 * <li>that the pattern-bound variables declared do not conflict with other local definitions already encountered in the encapsulating let definition
 * </ul>
 * <p>
 * When desugaring a tuple pattern or a non-polymorphic record pattern, we capture the field names into a set stored as an attribute
 * on the parse tree node. When it comes time for {@link CALTypeChecker} to type check the desugared definitions, the set is checked
 * against to verify that the type of the defining expression is a record type containing <strong>all and only the declared fields</strong>.
 * <p>
 * Note that local type declarations on the pattern-bound variables are allowed, and these type declarations have associated CALDoc comments.
 * On the other hand, the actual local pattern match declaration itself cannot have a type declaration nor a CALDoc comment.
 * <p>
 * The following demonstrates the way in which local pattern match declarations are desugared into regular
 * local function definitions:
 * <ul>
 * <li>Example 1 - a data constructor pattern with positional args:
 * <pre>
 * let Prelude.Just x = foo; in ...
 * </pre>
 * is desugared into:
 * <pre>
 * let
 *     $pattern_x = foo;
 *     x = $pattern_x.Prelude.Just.value;
 * in ...
 * </pre>
 * <li>Example 2 - a data constructor pattern with field-pattern pairs:
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
 * <li>Example 3 - a list constructor (:) pattern
 * <pre>
 * let x:_ = foo; in ...
 * </pre>
 * is desugared into:
 * <pre>
 * let
 *     $pattern_x = foo;
 *     x = $pattern_x.Prelude.Cons.head;
 * in ...
 * </pre>
 * <li>Example 4 - a tuple pattern
 * <pre>
 * let (_, x, _, z) = foo; in ...
 * </pre>
 * is desugared into:
 * <pre>
 * let
 *     $pattern_x_z = foo;
 *     x = $pattern_x_z.#2;
 *     z = $pattern_x_z.#4;
 * in ...
 * </pre>
 * <li>Example 5 - a non-polymorphic record pattern semantically identical to the above tuple pattern
 * <pre>
 * let {#1, #2=x, #3, #4=z} = foo; in ...
 * </pre>
 * is desugared into:
 * <pre>
 * let
 *     $pattern_x_z = foo;
 *     x = $pattern_x_z.#2;
 *     z = $pattern_x_z.#4;
 * in ...
 * </pre>
 * <li>Example 6 - a non-polymorphic record pattern with mixed ordinal/textual field names
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
 * <li>Example 7 - a polymorphic record pattern that desugars to the same definitions as the above non-polymorphic record pattern
 *     (modulo the fact that the desugared definition for the non-polymorphic record pattern would contain the set of declared
 *     field names as an attribute on the parse tree node) 
 * <pre>
 * let {_ | #3=x, y, z=alpha} = foo; in ...
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
 * </ul>
 *
 * @author Joseph Wong
 */
final class LocalPatternMatchDeclarationChecker {
    
    /**
     * The CALCompiler associated with the calling FreeVariableFinder instance.
     */
    private final CALCompiler compiler;
    
    /**
     * The calling FreeVariableFinder instance.
     */
    private final FreeVariableFinder finder;
    
    /**
     * A LinkedHashMap mapping pattern-bound variable names to the corresponding VAR_ID nodes that end up in the
     * desugared parse tree, in source order.
     */
    private final LinkedHashMap<String, ParseTreeNode> namesToDesugaredVarNodes = new LinkedHashMap<String, ParseTreeNode>();
    
    /**
     * A LinkedHashMap mapping pattern-bound variable names to the corresponding LET_DEFN nodes that end up in the
     * desugared parse tree, in source order.
     */
    private final LinkedHashMap<String, ParseTreeNode> namesToDesugaredDefnNodes = new LinkedHashMap<String, ParseTreeNode>();
    
    /**
     * The PatternVariablesCollector to use for gathering up the pattern variables in the pattern match declaration.
     */
    private final LocalPatternMatchDeclarationChecker.PatternVariablesCollector patternVariablesCollector;
    
    /**
     * This exception is meant to be thrown when the processing of a local pattern match declaration encounters an error in
     * the declaration and cannot continue. 
     *
     * @author Joseph Wong
     */
    private static final class CannotContinuePatternMatchProcessingException extends Exception {
        
        private static final long serialVersionUID = -4487923085595329558L;
        
        /**
         * The compiler error message to be reported for this error.
         */
        private final CompilerMessage compilerMessage;
        
        /**
         * Constructs an instance of this class.
         * @param compilerMessage the compiler error message to be reported for this error.
         */
        CannotContinuePatternMatchProcessingException(final CompilerMessage compilerMessage) {
            this.compilerMessage = compilerMessage;
        }
        
        /**
         * @return the compiler error message to be reported for this error.
         */
        CompilerMessage getCompilerMessage() {
            return compilerMessage;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "[CannotContinuePatternMatchProcessingException: " + compilerMessage + "]";
        }
    }
    
    /**
     * A utility class for collecting the pattern variables in a pattern match declaration. This class
     * also handles the detection of repeated pattern variable names and also collision with the names
     * of other local functions defined in the same let definition.
     *
     * @author Joseph Wong
     */
    private static final class PatternVariablesCollector {
        
        /**
         * A LinkedHashSet of the pattern variable names, in source order.
         */
        private final LinkedHashSet<String> patternVarNames = new LinkedHashSet<String>();
        
        /**
         * The source position associated with the LHS pattern in the pattern match declaration - for error reporting purposes.
         */
        private final SourceRange patternMatchPatternNodeSourceRange;
        
        /**
         * The set of other local function names declared in the same let definition.
         */
        private final Set<String> localFunctionNamesSet;
        
        /**
         * Constructs an instance of this class.
         * @param patternMatchPatternNodeSourceRange  the source range associated with the LHS pattern in the pattern match declaration - for error reporting purposes.
         * @param localFunctionNamesSet the set of other local function names declared in the same let definition.
         */
        PatternVariablesCollector(final SourceRange patternMatchPatternNodeSourceRange, final Set<String> localFunctionNamesSet) {
            this.patternMatchPatternNodeSourceRange = patternMatchPatternNodeSourceRange;
            this.localFunctionNamesSet = Collections.unmodifiableSet(localFunctionNamesSet);
        }
        
        /**
         * Records the pattern-bound variable in a pattern node, if it is a VAR_ID node. If the variable is repeated
         * in the pattern match declaration, or if the name has been defined elsewhere in the same let definition, a
         * CannotContinuePatternMatchProcessingException is thrown.
         * 
         * @param patternVarNode the pattern node (must be a VAR_ID or an UNDERSCORE node).
         * @throws CannotContinuePatternMatchProcessingException
         *    if the variable is repeated in the pattern match declaration, or
         *    if the name has been defined elsewhere in the same let definition.
         */
        void recordPatternVarNode(final ParseTreeNode patternVarNode) throws CannotContinuePatternMatchProcessingException {
            patternVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.UNDERSCORE);
            
            if (patternVarNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
                final String varName = patternVarNode.getText();
                
                if (localFunctionNamesSet.contains(varName)) {
                    // Repeated definition of {functionName} in let declaration.
                    throw new CannotContinuePatternMatchProcessingException(
                        new CompilerMessage(patternVarNode, new MessageKind.Error.RepeatedDefinitionInLetDeclaration(varName)));
                }
                
                if (patternVarNames.contains(varName)) {
                    // Repeated variable {varName} used in binding.
                    throw new CannotContinuePatternMatchProcessingException(
                        new CompilerMessage(patternVarNode, new MessageKind.Error.RepeatedVariableUsedInBinding(varName)));
                }
                
                patternVarNames.add(varName);
            }
        }
        
        /**
         * Constructs a name, based on the pattern-bound variable names, for the synthetic local function for
         * hosting the defining expression of the pattern match declaration, which is to be added to the desugared tree.
         * 
         * @return a name for the synthetic local function.
         * @throws CannotContinuePatternMatchProcessingException
         *    if the pattern match declaration declares no pattern-bound variables.
         */
        String makeSyntheticLocalFunctionName() throws CannotContinuePatternMatchProcessingException {
            if (patternVarNames.isEmpty()) {
                throw new CannotContinuePatternMatchProcessingException(
                    new CompilerMessage(patternMatchPatternNodeSourceRange, new MessageKind.Error.LocalPatternMatchDeclMustContainAtLeastOnePatternVar()));
            }
            
            return FreeVariableFinder.makeTempVarNameForDesugaredLocalPatternMatchDecl(patternVarNames);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "[PatternVariablesCollector: " + patternVarNames + "]";
        }
    }
    
    /**
     * Encapsulates the result of running the LocalPatternMatchDeclarationChecker.
     *
     * @author Joseph Wong
     */
    static final class Result {
        /**
         * A LinkedHashMap mapping pattern-bound variable names to the corresponding VAR_ID nodes that end up in the
         * desugared parse tree, in source order.
         */
        private final LinkedHashMap/*String, ParseTreeNode*/<String, ParseTreeNode> namesToDesugaredVarNodes;
        
        /**
         * The last desugared definition that was stitched into the tree.
         */
        private final ParseTreeNode lastDesugaredDefn;
        
        /**
         * Constructs an instance of this class.
         * @param namesToDesugaredVarNodes a LinkedHashMap mapping pattern-bound variable names to the corresponding VAR_ID nodes
         *          that end up in the desugared parse tree, in source order.
         * @param lastDesugaredDefn the last desugared definition that was stitched into the tree.
         */
        Result(final LinkedHashMap<String, ParseTreeNode> namesToDesugaredVarNodes, final ParseTreeNode lastDesugaredDefn) {
            this.namesToDesugaredVarNodes = namesToDesugaredVarNodes;
            this.lastDesugaredDefn = lastDesugaredDefn;
        }
        
        /**
         * @return a LinkedHashMap mapping pattern-bound variable names to the corresponding VAR_ID nodes that end up in the
         * desugared parse tree, in source order.
         */
        LinkedHashMap<String, ParseTreeNode> getNamesToDesugaredVarNodes() {
            return namesToDesugaredVarNodes;
        }
        
        /**
         * @return the last desugared definition that was stitched into the tree.
         */
        ParseTreeNode getLastDesugaredDefn() {
            return lastDesugaredDefn;
        }
    }
    
    /**
     * Constructs an instance of this class.
     * @param finder the encapsulating FreeVariableFinder instance.
     * @param compiler the CALCompiler instance of the FreeVariableFinder.
     * @param patternVariablesCollector the PatternVariablesCollector to use for gathering up the pattern variables in the pattern match declaration. 
     */
    private LocalPatternMatchDeclarationChecker(final FreeVariableFinder finder, CALCompiler compiler, final LocalPatternMatchDeclarationChecker.PatternVariablesCollector patternVariablesCollector) {
        this.finder = finder;
        this.compiler = compiler;
        this.patternVariablesCollector = patternVariablesCollector;
    }
    
    /**
     * Processes a local pattern match declaration, checking the declaration for errors as well as desugaring it
     * into regular local function definitions.
     * <p>
     * This is the main entry point of this class.
     * 
     * @param finder the encapsulating FreeVariableFinder instance.
     * @param compiler the CALCompiler instance of the FreeVariableFinder.
     * @param patternMatchDeclNode the root of the subtree corresponding to a pattern match declaration.
     * @param localFunctionNamesSet the set of other local function names declared in the same let definition.
     * @return a Result object encapsulating the result of the processing.
     */
    static LocalPatternMatchDeclarationChecker.Result processPatternMatchDeclNode(final FreeVariableFinder finder, final CALCompiler compiler, final ParseTreeNode patternMatchDeclNode, final Set<String> localFunctionNamesSet) {
        final SourceRange assemblySourceRange = patternMatchDeclNode.getAssemblySourceRange();
        final LocalPatternMatchDeclarationChecker.PatternVariablesCollector patternVariablesCollector = new PatternVariablesCollector(assemblySourceRange, localFunctionNamesSet);
        return new LocalPatternMatchDeclarationChecker(finder, compiler, patternVariablesCollector).processPatternMatchDeclNode(patternMatchDeclNode);
    }
    
    /**
     * Processes a local pattern match declaration, checking the declaration for errors as well as desugaring it
     * into regular local function definitions.
     * @param patternMatchDeclNode the root of the subtree corresponding to a pattern match declaration.
     * @return a Result object encapsulating the result of the processing.
     */
    private LocalPatternMatchDeclarationChecker.Result processPatternMatchDeclNode(final ParseTreeNode patternMatchDeclNode) {

        patternMatchDeclNode.verifyType(CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL);

        final ParseTreeNode patternMatchPatternNode = patternMatchDeclNode.firstChild();
        final ParseTreeNode patternMatchExprNode = patternMatchPatternNode.nextSibling();

        try {
            switch (patternMatchPatternNode.getType()) {
            case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR:
                processPatternContructorNode(patternMatchDeclNode, patternMatchPatternNode, patternMatchExprNode);
                break;

            case CALTreeParserTokenTypes.COLON:
                processColonNode(patternMatchDeclNode, patternMatchPatternNode, patternMatchExprNode);
                break;

            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR:
                processTupleConstructorNode(patternMatchDeclNode, patternMatchPatternNode, patternMatchExprNode);
                break;

            case CALTreeParserTokenTypes.RECORD_PATTERN:
                processRecordPatternNode(patternMatchDeclNode, patternMatchPatternNode, patternMatchExprNode);
                break;

            default:
                throw patternMatchPatternNode.makeExceptionForUnexpectedParseTreeNode();
            }
            
        } catch (final LocalPatternMatchDeclarationChecker.CannotContinuePatternMatchProcessingException e) {
            // an error has been detected in the pattern match declaration, so report it without modifying the parse tree
            compiler.logMessage(e.getCompilerMessage());
            return new Result(new LinkedHashMap<String, ParseTreeNode>(), patternMatchDeclNode);
        }
        
        // Finally, if everything checks out, stitch in the new desugared definitions to replace the pattern match decl node
        final ParseTreeNode lastDesugaredDefn = replacePatternMatchDeclNodeWithDesugaredDefns(patternMatchDeclNode);
        
        return new Result(namesToDesugaredVarNodes, lastDesugaredDefn);
    }
    
    /**
     * Stitches in the new desugared definitions to replace the pattern match declaration node.
     * @param patternMatchDeclNode the root of the subtree corresponding to a pattern match declaration, to be
     *          replaced with the new desugared definitions.
     */
    private ParseTreeNode replacePatternMatchDeclNodeWithDesugaredDefns(final ParseTreeNode patternMatchDeclNode) {
        
        final ParseTreeNode origDefnNodeSibling = patternMatchDeclNode.nextSibling();
        
        boolean firstReplacement = true;
        ParseTreeNode previousSibling = patternMatchDeclNode;
        for (final ParseTreeNode desugaredDefnNode : namesToDesugaredDefnNodes.values()) {
            if (firstReplacement) {
                // if this is the first defn, replace the original pattern match decl with this
                patternMatchDeclNode.copyContentsFrom(desugaredDefnNode);
                previousSibling = patternMatchDeclNode;
                firstReplacement = false;
            } else {
                // not the first defn, so hook it up to the previous sibling
                previousSibling.setNextSibling(desugaredDefnNode);
                previousSibling = desugaredDefnNode;
            }
        }
        
        // finally, hook up the sibling of the original pattern match decl
        final ParseTreeNode lastDesugaredDefn = previousSibling;
        
        lastDesugaredDefn.setNextSibling(origDefnNodeSibling);
        
        return lastDesugaredDefn;
    }
    
    /**
     * Processes a data cons pattern in a pattern match declaration. An CannotContinuePatternMatchProcessingException is
     * thrown if the pattern contains semantic errors.
     * 
     * @param patternMatchDeclNode the root of the subtree corresponding to the pattern match declaration.
     * @param patternMatchPatternNode the root of the subtree corresponding to a data cons pattern.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @throws CannotContinuePatternMatchProcessingException
     *    if the pattern contains semantic errors.
     */
    private void processPatternContructorNode(final ParseTreeNode patternMatchDeclNode, final ParseTreeNode patternMatchPatternNode, final ParseTreeNode patternMatchExprNode) throws CannotContinuePatternMatchProcessingException {

        patternMatchPatternNode.verifyType(CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR);
        
        ParseTreeNode dcNameListNode = patternMatchPatternNode.firstChild();
        dcNameListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON);
        
        ParseTreeNode dcArgBindingsNode = dcNameListNode.nextSibling();  
        
        final DataConstructor dataConstructor;

        final boolean checkArity = dcArgBindingsNode.getType() == CALTreeParserTokenTypes.PATTERN_VAR_LIST;
        final int impliedArity = checkArity ? dcArgBindingsNode.getNumberOfChildren() : -1;

        final ParseTreeNode dcNameNode = dcNameListNode.firstChild();

        dataConstructor = finder.resolveDataConsName(dcNameNode);

        if (dataConstructor.getArity() == 0) {
            // 0-ary data constructors cannot be used in a pattern match decl
            throw new CannotContinuePatternMatchProcessingException(
                new CompilerMessage(dcNameNode, new MessageKind.Error.ConstructorMustHavePositiveArityInLocalPatternMatchDecl(dataConstructor)));

        } else {

            //we check that the implied arity is correct for pattern var list based case unpackings.
            //we do this here in order to provide a reasonable error position (based on dcNameNode) in the case
            //that something is wrong. This is because the patternVarList may be empty, in which case it will not
            //have a source position.
            if (checkArity && dataConstructor.getArity() != impliedArity) {
                // Check that the number of variables expected by the data constructor corresponds to the number actually supplied in the pattern.                            
                //"The data constructor {0} must have exactly {1} pattern argument(s)."
                throw new CannotContinuePatternMatchProcessingException(
                    new CompilerMessage(dcNameNode, new MessageKind.Error.ConstructorMustHaveExactlyNArgsInLocalPatternMatchDecl(dataConstructor)));
            }
        }
        
        switch (dcArgBindingsNode.getType()) {
        case CALTreeParserTokenTypes.PATTERN_VAR_LIST:
            desugarPatternVarListInLocalDataConsPatternMatchDecl(dcArgBindingsNode, dcNameNode, patternMatchExprNode, dataConstructor);
            break;

        case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST:
            // Perform the first pass verification and patching-up on the parse tree for the list of field bindings.
            finder.firstPassProcessFieldBindingVarAssignmentListNode(dcArgBindingsNode, null, Collections.singleton(dataConstructor));
            desugarFieldBindingVarAssignmentListInLocalDataConsPatternMatchDecl(dcArgBindingsNode, dcNameNode, patternMatchExprNode);
            break;

        default:
            dcArgBindingsNode.unexpectedParseTreeNode();
            break;
        }
    }
    
    /**
     * Processes a list cons (:) pattern in a pattern match declaration. An CannotContinuePatternMatchProcessingException is
     * thrown if the pattern contains semantic errors.
     * 
     * @param patternMatchDeclNode the root of the subtree corresponding to the pattern match declaration.
     * @param patternMatchPatternNode the root of the subtree corresponding to a list cons (:) pattern.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @throws CannotContinuePatternMatchProcessingException
     *    if the pattern contains semantic errors.
     */
    private void processColonNode(final ParseTreeNode patternMatchDeclNode, final ParseTreeNode patternMatchPatternNode, final ParseTreeNode patternMatchExprNode) throws CannotContinuePatternMatchProcessingException {
        
        patternMatchPatternNode.verifyType(CALTreeParserTokenTypes.COLON);
        
        final ParseTreeNode dcNameNode = ParseTreeNode.makeQualifiedConsNode(CAL_Prelude.DataConstructors.Cons, patternMatchPatternNode.getSourcePosition());
        final DataConstructor dataConstructor = finder.resolveDataConsName(dcNameNode);
        
        desugarPatternVarListInLocalDataConsPatternMatchDecl(patternMatchPatternNode, dcNameNode, patternMatchExprNode, dataConstructor);
    }
    
    /**
     * Processes a tuple pattern in a pattern match declaration. An CannotContinuePatternMatchProcessingException is
     * thrown if the pattern contains semantic errors.
     * 
     * @param patternMatchDeclNode the root of the subtree corresponding to the pattern match declaration.
     * @param patternMatchPatternNode the root of the subtree corresponding to a tuple pattern.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @throws CannotContinuePatternMatchProcessingException
     *    if the pattern contains semantic errors.
     */
    private void processTupleConstructorNode(final ParseTreeNode patternMatchDeclNode, final ParseTreeNode patternMatchPatternNode, final ParseTreeNode patternMatchExprNode) throws CannotContinuePatternMatchProcessingException {

        patternMatchPatternNode.verifyType(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR);

        if (patternMatchPatternNode.hasExactlyOneChild()) { 
            //a one variable tuple pattern is illegal                          
            compiler.logMessage(new CompilerMessage(patternMatchPatternNode, new MessageKind.Error.Illegal1TuplePattern()));
            return;

        } else if (patternMatchPatternNode.hasNoChildren()) {
            // the unit pattern () cannot be used in a local pattern match decl
            compiler.logMessage(new CompilerMessage(patternMatchPatternNode, new MessageKind.Error.InvalidLocalPatternMatchUnitPattern()));
            return;
        }
        
        desugarLocalTuplePatternMatchDecl(patternMatchPatternNode, patternMatchExprNode);
    }
    
    /**
     * Processes a record pattern in a pattern match declaration. An CannotContinuePatternMatchProcessingException is
     * thrown if the pattern contains semantic errors.
     * 
     * @param patternMatchDeclNode the root of the subtree corresponding to the pattern match declaration.
     * @param patternMatchPatternNode the root of the subtree corresponding to a record pattern.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @throws CannotContinuePatternMatchProcessingException
     *    if the pattern contains semantic errors.
     */
    private void processRecordPatternNode(final ParseTreeNode patternMatchDeclNode, final ParseTreeNode patternMatchPatternNode, final ParseTreeNode patternMatchExprNode) throws CannotContinuePatternMatchProcessingException {
        
        patternMatchPatternNode.verifyType(CALTreeParserTokenTypes.RECORD_PATTERN);
        
        final ParseTreeNode baseRecordPatternNode = patternMatchPatternNode.firstChild();
        baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
        
        final ParseTreeNode baseRecordPatternVarNode = baseRecordPatternNode.firstChild();
        final String baseRecordVarName = null; // we do not currently support the use of non-wildcard base record patterns, so the base record var is always null
        
        final boolean hasWildcardBaseRecordPattern;
        
        if (baseRecordPatternVarNode != null) 
        {
            // the base record pattern should be a variable or '_', but we only accept the '_' case.
            baseRecordPatternVarNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.UNDERSCORE);
            
            if (baseRecordPatternVarNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
                // we do not support having a pattern variable for the base record pattern
                throw new CannotContinuePatternMatchProcessingException(
                    new CompilerMessage(baseRecordPatternVarNode, new MessageKind.Error.NonWildcardBaseRecordPatternNotSupportedInLocalPatternMatchDecl()));
            }
            
            hasWildcardBaseRecordPattern = true;
        } else {
            hasWildcardBaseRecordPattern = false;
        }
    
        final ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();                    
        
        // Perform the first pass verification and patching-up on the parse tree for a list of field bindings.
        finder.firstPassProcessFieldBindingVarAssignmentListNode(fieldBindingVarAssignmentListNode, baseRecordVarName, Collections.<DataConstructor>emptySet());
        
        desugarLocalRecordPatternMatchDecl(patternMatchPatternNode, patternMatchExprNode, hasWildcardBaseRecordPattern);
    }

    /**
     * Constructs the name of the synthetic local function associated with the defining expression from a flat list of pattern nodes.
     * 
     * @param patternVarListNode a flat list of pattern nodes.
     * @return the name of the synthetic local function associated with the defining expression.
     * @throws CannotContinuePatternMatchProcessingException
     *    if a variable is repeated in the pattern match declaration, or
     *    if a name has been defined elsewhere in the same let definition, or
     *    if the pattern match declaration declares no pattern-bound variables.
     */
    private String makeTempVarNameForDesugaredLocalPatternMatchDeclFromPatternVars(final ParseTreeNode patternVarListNode) throws CannotContinuePatternMatchProcessingException {
        
        patternVarListNode.verifyType(CALTreeParserTokenTypes.PATTERN_VAR_LIST, CALTreeParserTokenTypes.COLON, CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR);
        
        for (final ParseTreeNode patternVarNode : patternVarListNode) {

            patternVariablesCollector.recordPatternVarNode(patternVarNode);
        }

        return patternVariablesCollector.makeSyntheticLocalFunctionName();
    }

    /**
     * Constructs the name of the synthetic local function associated with the defining expression from a FIELD_BINDING_VAR_ASSIGNMENT_LIST nodes.
     * 
     * @param fieldBindingVarAssignmentListNode a FIELD_BINDING_VAR_ASSIGNMENT_LIST representing field-pattern pairs.
     * @return the name of the synthetic local function associated with the defining expression.
     * @throws CannotContinuePatternMatchProcessingException
     *    if a variable is repeated in the pattern match declaration, or
     *    if a name has been defined elsewhere in the same let definition, or
     *    if the pattern match declaration declares no pattern-bound variables.
     */
    private String makeTempVarNameForDesugaredLocalPatternMatchDeclFromFieldBindingVarAssignmentList(final ParseTreeNode fieldBindingVarAssignmentListNode) throws CannotContinuePatternMatchProcessingException {

        fieldBindingVarAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);

        for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {

            final ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
            final ParseTreeNode patternVarNode = fieldNameNode.nextSibling();

            patternVariablesCollector.recordPatternVarNode(patternVarNode);
        }

        return patternVariablesCollector.makeSyntheticLocalFunctionName();
    }

    /**
     * Constructs a regular local function definition for the desugared representation.
     * 
     * @param nameNode the node to use for the local function name.
     * @param desugaredExprNode the associated defining expression for the local function.
     * @return the parse tree for the local function definition.
     */
    private ParseTreeNode makeDesugaredLetDefnNodeForLocalPatternMatchDecl(final ParseTreeNode nameNode, final ParseTreeNode desugaredExprNode) {
        
        nameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        final String localFunctionName = nameNode.getText();
        
        // It is important that the desugared definition have the source position of the pattern var name,
        // so that any subsequent type checking error messages can refer to the original pattern var (for which this defn is created)
        final ParseTreeNode localFunctionNode = new ParseTreeNode(
            CALTreeParserTokenTypes.LET_DEFN, "LET_DEFN",
            nameNode.getSourcePosition());
        localFunctionNode.setIsDesugaredPatternMatchForLetDefn(true); // it's important to specially mark this LET_DEFN so that the type checker can emit appropriate error messages
        
        final ParseTreeNode optionalCALDocNode = SourceModel.makeOptionalCALDocNode(null);
        final ParseTreeNode functionNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, localFunctionName, nameNode.getSourcePosition()); // we make a copy of the name node because we don't want it to be tangled with the original parse tree
        functionNameNode.setIsSyntheticVarOrRecordFieldSelection(nameNode.getIsSyntheticVarOrRecordFieldSelection()); // keep the synthetic marker as well
        final ParseTreeNode paramListNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST");
        final ParseTreeNode exprNode = desugaredExprNode;
        
        localFunctionNode.setFirstChild(optionalCALDocNode);
        optionalCALDocNode.setNextSibling(functionNameNode);
        functionNameNode.setNextSibling(paramListNode);
        paramListNode.setNextSibling(exprNode);
        
        namesToDesugaredVarNodes.put(localFunctionName, functionNameNode);
        namesToDesugaredDefnNodes.put(localFunctionName, localFunctionNode);
        
        return localFunctionNode;
    }

    /**
     * Desugars a data cons pattern or a list cons pattern into a series of local function definitions - one for hosting
     * the defining expression of the pattern match declaration, and one for each pattern-bound variable.
     * 
     * @param patternVarListNode a flat list of pattern nodes.
     * @param dcNameNode the node representing the name of the data constructor.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @param dataConstructor the associated DataConstructor entity.
     * @throws CannotContinuePatternMatchProcessingException
     *    if a variable is repeated in the pattern match declaration, or
     *    if a name has been defined elsewhere in the same let definition, or
     *    if the pattern match declaration declares no pattern-bound variables.
     */
    private void desugarPatternVarListInLocalDataConsPatternMatchDecl(final ParseTreeNode patternVarListNode, final ParseTreeNode dcNameNode, final ParseTreeNode patternMatchExprNode, final DataConstructor dataConstructor) throws CannotContinuePatternMatchProcessingException {
        
        patternVarListNode.verifyType(CALTreeParserTokenTypes.PATTERN_VAR_LIST, CALTreeParserTokenTypes.COLON);
        
        final String tempVarName = makeTempVarNameForDesugaredLocalPatternMatchDeclFromPatternVars(patternVarListNode);
        
        ////
        /// Create the desugared definitions in source order of the patterns. (The order is important for the LocalFunctionIdentifierGenerator)
        //
        int fieldIndex = 0;
        for (final ParseTreeNode patternVarNode : patternVarListNode) {
            
            final FieldName fieldName = dataConstructor.getNthFieldName(fieldIndex);
            
            desugarPatternVarNodeInLocalDataConsPatternMatchDecl(patternVarNode, dcNameNode, fieldName, tempVarName);
            
            fieldIndex++;
        }
        
        ////
        /// Add the defining expression last. (The order is important for the LocalFunctionIdentifierGenerator)
        //
        desugarExprNodeInLocalPatternMatchDecl(tempVarName, patternMatchExprNode, null, true);
    }

    /**
     * Desugars a data cons pattern into a series of local function definitions - one for hosting
     * the defining expression of the pattern match declaration, and one for each pattern-bound variable.
     * 
     * @param dcArgBindingsNode a FIELD_BINDING_VAR_ASSIGNMENT_LIST representing field-pattern pairs.
     * @param dcNameNode the node representing the name of the data constructor.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @throws CannotContinuePatternMatchProcessingException
     *    if a variable is repeated in the pattern match declaration, or
     *    if a name has been defined elsewhere in the same let definition, or
     *    if the pattern match declaration declares no pattern-bound variables.
     */
    private void desugarFieldBindingVarAssignmentListInLocalDataConsPatternMatchDecl(final ParseTreeNode dcArgBindingsNode, final ParseTreeNode dcNameNode, final ParseTreeNode patternMatchExprNode) throws CannotContinuePatternMatchProcessingException {
        
        dcArgBindingsNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);
        
        final String tempVarName = makeTempVarNameForDesugaredLocalPatternMatchDeclFromFieldBindingVarAssignmentList(dcArgBindingsNode);
        
        ////
        /// Create the desugared definitions in source order of the patterns. (The order is important for the LocalFunctionIdentifierGenerator)
        //
        for (final ParseTreeNode fieldBindingVarAssignmentNode : dcArgBindingsNode) {
            
            final ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
            final ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
            
            final FieldName fieldName = FieldName.make(fieldNameNode.getText());
            
            desugarPatternVarNodeInLocalDataConsPatternMatchDecl(patternVarNode, dcNameNode, fieldName, tempVarName);
        }
        
        ////
        /// Add the defining expression last. (The order is important for the LocalFunctionIdentifierGenerator)
        //
        desugarExprNodeInLocalPatternMatchDecl(tempVarName, patternMatchExprNode, null, true);
    }

    /**
     * Desugars a tuple pattern into a series of local function definitions - one for hosting
     * the defining expression of the pattern match declaration, and one for each pattern-bound variable.
     * 
     * @param tuplePatternNode the root of the subtree corresponding to a tuple pattern.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @throws CannotContinuePatternMatchProcessingException
     *    if a variable is repeated in the pattern match declaration, or
     *    if a name has been defined elsewhere in the same let definition, or
     *    if the pattern match declaration declares no pattern-bound variables.
     */
    private void desugarLocalTuplePatternMatchDecl(final ParseTreeNode tuplePatternNode, final ParseTreeNode patternMatchExprNode) throws CannotContinuePatternMatchProcessingException {
        
        tuplePatternNode.verifyType(CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR);
        
        final String tempVarName = makeTempVarNameForDesugaredLocalPatternMatchDeclFromPatternVars(tuplePatternNode);
        
        final SortedSet/*FieldName*/<FieldName> fieldNames = new TreeSet<FieldName>(); 
        
        ////
        /// Create the desugared definitions in source order of the patterns. (The order is important for the LocalFunctionIdentifierGenerator)
        //
        int ordinal = 1; // the first ordinal field name is #1
        for (final ParseTreeNode patternVarNode : tuplePatternNode) {
            
            final FieldName fieldName = FieldName.makeOrdinalField(ordinal);
            fieldNames.add(fieldName);
            
            desugarPatternVarNodeInLocalRecordPatternMatchDecl(patternVarNode, fieldName, patternVarNode.getSourcePosition(), tempVarName);
            
            ordinal++;
        }
        
        ////
        /// Add the defining expression last. (The order is important for the LocalFunctionIdentifierGenerator)
        //
        desugarExprNodeInLocalPatternMatchDecl(tempVarName, patternMatchExprNode, fieldNames, true);
    }

    /**
     * Desugars a record pattern into a series of local function definitions - one for hosting
     * the defining expression of the pattern match declaration, and one for each pattern-bound variable.
     * 
     * @param recordPatternNode the root of the subtree corresponding to a record pattern.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @param hasWildcardBaseRecordPattern whether the record pattern has a wildcard (_) base record pattern.
     * @throws CannotContinuePatternMatchProcessingException
     *    if a variable is repeated in the pattern match declaration, or
     *    if a name has been defined elsewhere in the same let definition, or
     *    if the pattern match declaration declares no pattern-bound variables.
     */
    private void desugarLocalRecordPatternMatchDecl(final ParseTreeNode recordPatternNode, final ParseTreeNode patternMatchExprNode, final boolean hasWildcardBaseRecordPattern) throws CannotContinuePatternMatchProcessingException {
        
        final ParseTreeNode baseRecordPatternNode = recordPatternNode.firstChild();
        baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
        final ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();                    
        
        final String tempVarName = makeTempVarNameForDesugaredLocalPatternMatchDeclFromFieldBindingVarAssignmentList(fieldBindingVarAssignmentListNode);
        
        final SortedSet<FieldName> fieldNames = new TreeSet<FieldName>(); 
        
        ////
        /// Create the desugared definitions in source order of the patterns. (The order is important for the LocalFunctionIdentifierGenerator)
        //
        for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {
    
            final ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
            final ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
    
            final FieldName fieldName = FieldName.make(fieldNameNode.getText());
            fieldNames.add(fieldName);
    
            desugarPatternVarNodeInLocalRecordPatternMatchDecl(patternVarNode, fieldName, fieldNameNode.getSourcePosition(), tempVarName);
        }
        
        ////
        /// Add the defining expression last. (The order is important for the LocalFunctionIdentifierGenerator)
        //
        desugarExprNodeInLocalPatternMatchDecl(tempVarName, patternMatchExprNode, fieldNames, !hasWildcardBaseRecordPattern);
    }

    /**
     * Constructs the synthetic local function for hosting the defining expression of the original pattern match declaration.
     * 
     * @param tempVarName the name for the synthetic local function which is to host the defining expression of the original pattern match declaration.
     * @param patternMatchExprNode the root of the subtree corresponding to the defining expression of the pattern match declaration.
     * @param constrainingFieldNames a SortedSet of {@link FieldName}s corresponding to a non-polymorphic record pattern.
     *          Can be null if the pattern match declaration is not associated with a non-polymorphic record pattern.
     * @param isNonPolymorphic whether the record pattern is non-polymorphic. This parameter is ignored if <code>constrainingFieldNames</code> is null.
     */
    private void desugarExprNodeInLocalPatternMatchDecl(final String tempVarName, final ParseTreeNode patternMatchExprNode, final SortedSet/*FieldName*/<FieldName> constrainingFieldNames, boolean isNonPolymorphic) {
        final ParseTreeNode tempVarNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, tempVarName);
        tempVarNameNode.setIsSyntheticVarOrRecordFieldSelection(true);
        
        final ParseTreeNode desugaredDefnNode = makeDesugaredLetDefnNodeForLocalPatternMatchDecl(tempVarNameNode, patternMatchExprNode);
        if (isNonPolymorphic) {
            desugaredDefnNode.setDeclaredFieldsInNonPolymorphicRecordPatternMatchForLetDefn(constrainingFieldNames);
        } else {
            desugaredDefnNode.setDeclaredFieldsInPolymorphicRecordPatternMatchForLetDefn(constrainingFieldNames);
        }
    }

    /**
     * Constructs a local function definition corresponding to a pattern-bound variable in a data cons or list cons pattern.
     * 
     * @param patternVarNode the representing the pattern-bound variable.
     * @param dcNameNode the node representing the name of the data constructor. 
     * @param fieldName the name of the data constructor field which is bound to the pattern-bound variable.
     * @param tempVarName the name for the synthetic local function which is to host the defining expression of the original pattern match declaration.
     */
    private void desugarPatternVarNodeInLocalDataConsPatternMatchDecl(final ParseTreeNode patternVarNode, final ParseTreeNode dcNameNode, final FieldName fieldName, final String tempVarName) {
        
        switch (patternVarNode.getType()) {
            
            case CALTreeParserTokenTypes.VAR_ID:
            {           
                // It is important that the desugared expression have the source position of the data cons name,
                // so that the ErrorInfo object for reporting data cons mismatch would point at the original data cons
                // in the pattern match declaration
                final ParseTreeNode desugaredExprNode = new ParseTreeNode(
                    CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD, "SELECT_DATA_CONSTRUCTOR_FIELD",
                    dcNameNode.getSourcePosition());
                
                final ParseTreeNode exprNode = ParseTreeNode.makeUnqualifiedVarNode(tempVarName, null);
                exprNode.firstChild().nextSibling().setIsSyntheticVarOrRecordFieldSelection(true); // mark the tempVar's VAR_ID node as synthetic

                final ParseTreeNode dataConsNameNode = dcNameNode.copyParseTree(); // we make a copy because the data cons name may need to appear multiple times, once per desugared pattern defn.
                final ParseTreeNode fieldNameNode = SourceModel.makeFieldNameExpr(fieldName);
                
                desugaredExprNode.setFirstChild(exprNode);
                exprNode.setNextSibling(dataConsNameNode);
                dataConsNameNode.setNextSibling(fieldNameNode);
                
                makeDesugaredLetDefnNodeForLocalPatternMatchDecl(patternVarNode, desugaredExprNode);
                break;
            }
            
            case CALTreeParserTokenTypes.UNDERSCORE:
                break;
                
            default:
                throw patternVarNode.makeExceptionForUnexpectedParseTreeNode();                   
        }
    }

    /**
     * Constructs a local function definition corresponding to a pattern-bound variable in a record pattern.
     * 
     * @param patternVarNode the representing the pattern-bound variable.
     * @param fieldName the name of the record field which is bound to the pattern-bound variable.
     * @param fieldNameSourcePosition the source position of the occurence of the field name in the original source.
     * @param tempVarName the name for the synthetic local function which is to host the defining expression of the original pattern match declaration.
     */
    private void desugarPatternVarNodeInLocalRecordPatternMatchDecl(final ParseTreeNode patternVarNode, final FieldName fieldName, final SourcePosition fieldNameSourcePosition, final String tempVarName) {
        
        switch (patternVarNode.getType()) {
            
            case CALTreeParserTokenTypes.VAR_ID:
            {           
                // This expression node does not need a source position because it cannot have an ErrorInfo object
                // associated with it (because unlike data cons selection, there will not be cases of runtime data cons mismatch
                // that need to be reported as runtime errors)
                final ParseTreeNode desugaredExprNode = new ParseTreeNode(
                    CALTreeParserTokenTypes.SELECT_RECORD_FIELD, "SELECT_RECORD_FIELD");
                desugaredExprNode.setIsSyntheticVarOrRecordFieldSelection(true); // mark the node as synthetic, so that type-checking errors can be reported appropriately
    
                // It is important that the tempVar expression have the source position of the var name,
                // so that type-checking errors for the expression can be associated with the pattern-bound var.
                // e.g. the error for the ambiguous type of x in:
                //
                // public foo8 = let (x, y) = (1, 2); in (x + 10.0, x + 20 :: Prelude.Int);
                //
                final ParseTreeNode exprNode = ParseTreeNode.makeUnqualifiedVarNode(tempVarName, patternVarNode.getSourcePosition());
                exprNode.firstChild().nextSibling().setIsSyntheticVarOrRecordFieldSelection(true); // mark the tempVar's VAR_ID node as synthetic
                
                // It is important that the field name has the original source position,
                // so that type-checking errors for the field name can be associated with the pattern.
                // e.g. the error for missing fields:
                //
                // public foo6a = let {x} = {a = "foo", b = "bar"}; in x;
                // public foo6b = let {_|y} = {a = "foo", b = "bar"}; in y;
                // public foo7c = let (a, b, c, d) = ('a', 'b', 'c'); in a;
                //
                final ParseTreeNode fieldNameNode;
                if (fieldName instanceof FieldName.Ordinal) {
                    fieldNameNode = new ParseTreeNode(CALTreeParserTokenTypes.ORDINAL_FIELD_NAME, fieldName.getCalSourceForm(), fieldNameSourcePosition);
                } else {
                    fieldNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, fieldName.getCalSourceForm(), fieldNameSourcePosition);
                }
                
                desugaredExprNode.setFirstChild(exprNode);
                exprNode.setNextSibling(fieldNameNode);
                
                makeDesugaredLetDefnNodeForLocalPatternMatchDecl(patternVarNode, desugaredExprNode);
                break;
            }
            
            case CALTreeParserTokenTypes.UNDERSCORE:
                break;
                
            default:
                throw patternVarNode.makeExceptionForUnexpectedParseTreeNode();                   
        }
    }
}