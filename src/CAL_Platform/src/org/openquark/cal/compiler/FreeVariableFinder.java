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
 * FreeVariableFinder.java
 * Creation date: (February 6, 2001)
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.util.ArrayStack;
import org.openquark.cal.util.Graph;
import org.openquark.cal.util.Vertex;
import org.openquark.cal.util.VertexBuilder;
import org.openquark.cal.util.VertexBuilderList;



/**
 * Used for finding free variables in global function, lambda definition or local
 * function definitions. Free variables, in general, are the variables upon which
 * the given expression or definition actually depends on.
 * <p>
 * For each function in a module, an identifier appearing within its defining 
 * body expression is a:
 * <ul>
 * <li>(non built-in, non foreign) function defined within the same module
 * <li>(reference to a) function defined within a different module
 * <li>built-in function 
 * <li>class method name
 * <li>local function defined using a let
 * <li>pattern variable i.e. an argument variable, a variable from a case pattern binding, or
 *     a lambda bound variable.
 * <li>data constructor defined in the same module
 * <li>data constructor defined within a different module
 * <li>foreign function.
 * </ul>
 * <p>
 * This class is responsible for augmenting the parse tree of unqualified functions and
 * data constructors to explicitly fill in the module name, so that further analysis can assume
 * that all identifier names are fully qualified.
 * <p>
 * It is also responsible for reporting an error if an identifier does not exist in one of the 
 * categories above.
 * <p>
 * It isolates the non built-in, non foreign defined within the same module,
 * which is needed for dependency analysis. 
 * <p>
 * It augments data constructor field selection expression nodes with the qualified var which would 
 * appear in the rhs of the alt for a corresponding case expression.
 * <p>
 * It converts unused pattern variables in case expressions to wildcard (_) patterns.
 * This is primarily an optimization for the runtime.
 * <p>
 * It checks and desugars local pattern match declarations (e.g. let (x, y, z) = foo; in ...) via the helper class
 * {@link LocalPatternMatchDeclarationChecker}.
 * <p>
 * It provides module unique names for all the local bound variables appearing in the function's definition.
 * For example, f x = 2.0 + x would be converted to f f$x$1 = 2.0 + f$x$1. This makes latter compiler passes,
 * in particular Johnsson style lambda lifting much easier.
 * <p>
 * In some ways this class could more generally be called the "StaticAnalyzer" because it detects certain
 * simple "static" errors that can be reported in the first pass of a the parse-tree, such as
 * repeated symbol definitions.
 * <p>
 * Creation date: (February 6, 2001)
 * @author Bo Ilic
 */
class FreeVariableFinder {

    private final CALCompiler compiler;

    private final ModuleTypeInfo currentModuleTypeInfo;
        
    /** Names of all functions defined in the current module at the top level, not including built-ins and foreign functions.*/
    private final Set<String> topLevelFunctionNamesSet;
   
    /**
     *  If true, then resolve unqualified names that occur in the parse tree, or report an error if this can not be done.
     *  Also, do some extra error checking on the correctness of the parse trees.
     *  Since free variable finding is done on the first full traversal of the parse tree of function
     *  definitions, this class is a convenient place to put certain error checking.
     */
    private boolean firstPass;
   
    /** 
     * The names of all possible dependee var names from the current module.
     * The sets returned by the public methods of this class will be a subset of this set.
     */
    private Collection<String> possibleDependeeNamesSet;

    /** 
     * The names of built-in and foreign functions will never be returned as a dependee (unless cloaked by
     * scoping). Here are some additional names that should not be returned.  These names are all
     * assumed to belong to the current module.
     */
    private Set<String> ignorableFunctionNamesSet;        
     
    /**
     * Statistics on the number of pattern vars that are unused and then converted to
     * wildcard pattern variables (_). 
     */
    private static final boolean GENERATE_CONVERTED_PATTERN_VAR_STATISTICS = false;
    private int nUnusedPatternVars;
    private static int totalNUnusedPatternVars;
    
    /**
     * A stack for the use of bound variables. Its special feature is that it keeps track of how
     * often a given bound variable is used in its defining expression.
     * 
     * It is also used to rename the bound variables in a function's definition so that they are 
     * unique within a given module.
     * 
     * @author Bo Ilic
     */
    private static final class BoundVariablesStack {
        
        private final ArrayStack<BoundVariable> stack;
        
        /**
         * name of the top-level function in which we are doing symbol resolution.
         * null if we are working on a subexpression of the function's definition, and
         * don't want to assign unique names to the bound variables.
         */
        private final String topLevelFunctionName;
        
        /**
         * every time a BoundVariable is pushed onto the BoundVariableStack, the varCount is
         * incremented by 1. This allows us to assign a unique int to each bound variable within
         * a function, and thus have an unambiguous name for each local symbol defined within
         * the function
         */        
        private int varCount;
        
        private static final class BoundVariable {
        
            private final ParseTreeNode varNameNode;
            private int useCount;
            
            /**
             * name to prepend to the variable's name             
             */ 
            private final String prefix;
            private final int varNumber;
        
            private BoundVariable(ParseTreeNode varNameNode, int varNumber, String prefix) {
                this.varNameNode = varNameNode;
                this.varNumber = varNumber;
                this.prefix = prefix;                
            }
            
            private BoundVariable(String varName) {
                this (new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, varName), -1, null);
            }
        
            @Override
            public boolean equals(Object other) {
                return getVarName().equals(((BoundVariable)other).getVarName());
            }
        
            @Override
            public int hashCode() {
                return getVarName().hashCode();
            }
        
            int getUseCount() {
                return useCount;
            }
        
            void incrementUseCount() {
                ++useCount;
            }
            
            String getVarName () {
                return varNameNode.getText();
            }
            
            int getVarNumber () {
                return varNumber;
            }
            
            String getUniqueName() {
                if (prefix != null) {
                    return prefix + "$" + getVarName() + "$" + varNumber;
                }
                
                //names have already been made unique by an earlier pass.
                return getVarName();
            }
            
            @Override
            public String toString() {
                return getUniqueName();                    
            }
        }      
        
        BoundVariablesStack(String topLevelFunctionName) {
            stack = ArrayStack.make();          
            this.topLevelFunctionName = topLevelFunctionName;
        }
        
        void push(ParseTreeNode varNameNode) {
            stack.push(new BoundVariable(varNameNode, ++varCount, topLevelFunctionName));            
        }

        void popN(int n, boolean updateWithUniqueNames) {
            if (updateWithUniqueNames) {
                for (int i = 0; i < n; ++i) {
                    BoundVariable boundVar = stack.pop();
                    boundVar.varNameNode.setText(boundVar.getUniqueName());
                }               
                return;
            }
            
            stack.popN(n);
        }
        
        /**
         * Determine if the stack contains the given varName.
         * @param varName
         * @param incrementUseCount if the stack contains varName, and this flag is true, then
         *   increment the use count of varName. Note: it is the most recently pushed varName whose
         *    use count will be incremented.
         * @return BoundVar non-null if the BoundVariables stack contains a varName with this name. 
         */
        BoundVariable contains(String varName, boolean incrementUseCount) {
            int index = stack.lastIndexOf(new BoundVariable(varName));
            if (index == -1) {
                return null;
            }
            
            BoundVariable var = stack.get(index);
            if (incrementUseCount) {
                var.incrementUseCount();
            }
                       
            return var;
        }
        
        /**         
         * @param varName
         * @return the use count (>= 0) if varName is in the stack and -1 if not. The use count is for
         *     the last added symbol in the stack.
         */
        int getUseCount(String varName) {
            int index = stack.lastIndexOf(new BoundVariable(varName));
            if (index == -1) {
                return -1;
            }
            return stack.get(index).getUseCount();            
        }
                            
        /**         
         * @param nElements number of elements to get from the top of the stack
         * @return (String Set) the var names in the stack, with the uniqueness transform applied.         
         */
        Set<String> getUniqueVarNamesSet(int nElements) { 
            if (topLevelFunctionName == null) {
                //should only be doing this if we are disambiguating names
                throw new IllegalStateException();
            }
            Set<String> varNamesSet = new HashSet<String>();            
            for (int i = stack.size() - nElements, n = stack.size(); i < n; ++i) {
                varNamesSet.add(stack.get(i).getUniqueName());           
            }
            return varNamesSet;           
        }
        
        Set<String> getUniqueVarNamesSet() { 
            return getUniqueVarNamesSet(stack.size());           
        }
        
        @Override
        public String toString() {
            if (stack.size() == 0) {
                return "<empty stack>";
            }
            
            StringBuilder result = new StringBuilder();
            for (int i = stack.size() - 1; i >= 0; --i) {
                result.append(i).append(' ').append(stack.get(i).toString()).append('\n');
            }
            return result.toString();
        }
    }
    
    /**
     * FreeVariableFinder constructor comment.
     *
     * @param compiler    
     * @param topLevelFunctionNamesSet names of all functions defined in the current module at the top level, not including built-in or foreign functions.
     * @param currentModuleTypeInfo
     */
    FreeVariableFinder(CALCompiler compiler, Set<String> topLevelFunctionNamesSet, ModuleTypeInfo currentModuleTypeInfo) {
    
        if (compiler == null ||           
            topLevelFunctionNamesSet == null ||
            currentModuleTypeInfo == null) {
                
            throw new NullPointerException();
        }

        this.compiler = compiler;        
        this.topLevelFunctionNamesSet = topLevelFunctionNamesSet;
        this.currentModuleTypeInfo = currentModuleTypeInfo;
    }
    
    /**
     * Perform dependency analysis to divide up the top-level functions defined in a module into
     * a topologically ordered set of strongly connected components.
     * 
     * This function does the first full traversal of all function definition expression trees.
     * It does certain static checking and updates the parse-trees as a side effect.
     * -all symbols that are not fully qualified are fully qualified, or an error if the symbol
     *  doesn't exist or is not visible.
     * -static checks that certain symbols such as case pattern variables are not duplicated
     *  e.g. "case xs of x : x" will give an error
     * -local dependency order transformations of let blocks
     * 
     * Creation date: (1/18/01 6:11:23 PM)
     * @param functionNameToDefinitionNodeMap
     * @return Graph 
     */
    Graph<String> performFunctionDependencyAnalysis(Map<String, ParseTreeNode> functionNameToDefinitionNodeMap) {
      
        //Makes the dependency graph for the top level functions defined in a module.
        VertexBuilderList<String> vertexBuilderList = new VertexBuilderList<String>();
                        
        for (final Map.Entry<String, ParseTreeNode> entry : functionNameToDefinitionNodeMap.entrySet()) {
           
            String functionName = entry.getKey();
            ParseTreeNode functionParseTree = entry.getValue();
            Set<String> freeVariablesSet = findDependeeFunctionNames(functionParseTree);

            vertexBuilderList.add(new VertexBuilder<String>(functionName, freeVariablesSet));
        }
        
        if (FreeVariableFinder.GENERATE_CONVERTED_PATTERN_VAR_STATISTICS) {
            FreeVariableFinder.totalNUnusedPatternVars += nUnusedPatternVars;
            
            System.out.println("module " + currentModuleTypeInfo.getModuleName());
            System.out.println("number of unused pattern vars converted = " + nUnusedPatternVars);
            System.out.println("total unused pattern vars converted =" + FreeVariableFinder.totalNUnusedPatternVars);
        }        
       
        // should never fail. It is a redundant check since makeSCDependencyGraph should throw an exception otherwise.
        if (!vertexBuilderList.makesValidGraph(topLevelFunctionNamesSet)) {
            throw new IllegalStateException("Internal coding error during dependency analysis."); 
        }
        
        Graph<String> g = new Graph<String>(vertexBuilderList);

        g = g.calculateStronglyConnectedComponents();

        //Previously we gave an error if there were no functions defined within the module.
        //However, we might want to have a module consisting only of data declarations so this
        //is now OK.

        return g;
    }        
    
    /**
     * Returns the names of the top level non built-in functions defined within the current
     * module upon which the definition of the function defined by functionParseTree depends.
     * As a side effect, unqualified names occurring in functionParseTree are augmented with their correct
     * qualification.
     *
     * Creation date: (2/6/01 9:16:32 AM)
     * @return Set of global non built-in or foreign function names upon which the function definition depends
     *      ordered by their first occurrence in the expression text.     
     * @param functionParseTree parse tree of the function definition
     */
    private Set<String> findDependeeFunctionNames(ParseTreeNode functionParseTree) {

        functionParseTree.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);
       
        initState(true, Collections.<String>emptySet(), topLevelFunctionNamesSet);
        
        ParseTreeNode functionNameNode = functionParseTree.getChild(2);        

        ParseTreeNode paramListNode = functionNameNode.nextSibling();
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

        BoundVariablesStack boundVariablesStack = new BoundVariablesStack(functionNameNode.getText());
        Set<String> freeVariablesSet = new LinkedHashSet<String>();

        findFreeVariablesInBoundExpression(freeVariablesSet, boundVariablesStack, paramListNode);

        return freeVariablesSet;
    }

    /**
     * Resolve unqualified identifiers within a function definition.
     * This method can be used on an adjunct to an existing module.
     * @param functionParseTree parse tree of the function definition
     */
    void resolveUnqualifiedIdentifiers(ParseTreeNode functionParseTree) {

        functionParseTree.verifyType(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN);

        Set<String> dependeeNamesSet = new HashSet<String>();
        int nFunctions = currentModuleTypeInfo.getNFunctions();
        for (int i = 0; i < nFunctions; i++) {
            Function function = currentModuleTypeInfo.getNthFunction(i);
            dependeeNamesSet.add(function.getName().getUnqualifiedName());
        }
        topLevelFunctionNamesSet.addAll(dependeeNamesSet);
        
        // Add the name from the function itself, in case it's recursive.
        ParseTreeNode varNode = functionParseTree.getChild(2);
        varNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
        String functionName = varNode.getText();
        topLevelFunctionNamesSet.add(functionName);
                 
        initState(true, Collections.<String>emptySet(), topLevelFunctionNamesSet);

        ParseTreeNode paramListNode = functionParseTree.getChild(3);
        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

        BoundVariablesStack boundVariablesStack = new BoundVariablesStack(functionName);
        Set<String> freeVariablesSet = new HashSet<String>();

        // called for its side effect of resolving unqualified identifiers.
        findFreeVariablesInBoundExpression(freeVariablesSet, boundVariablesStack, paramListNode);
    }

    /**
     * Returns the names of the lambda bound variables, local function names (i.e. not
     * defined within a let or letrec) and function argument names that occur free
     * in the parseTree defining the given lambda or local function definition. Note that normally,
     * the returned set will not contain the names of top level functions. However, because of variable
     * hiding, it may. e.g.
     * x = 2;
     * y = 3;
     * f y = 2 + \z -> x + y + z;
     * Then only y is free in \z -> x + y + z, in the sense of this method. 
     *
     * Creation date: (2/6/01 1:42:51 PM)    
     * @param possibleDependeeNamesSet Set names of the function argument and lambda bound
     *        variables encountered so far in the parse of the global functions that this lambda or local
     *      let definition is a part of. The set returned by this function will be a subset of
     *      this set.
     * @param paramListNode variables for the lambda definition or local let definition.
     * @return Set the free variables encountered ordered by their first occurrence in the expression text. 
     */
    Set<String> findFreeNamesInLambdaExpr(Collection<String> possibleDependeeNamesSet, ParseTreeNode paramListNode) {

        paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

        initState(false, topLevelFunctionNamesSet, possibleDependeeNamesSet);

        BoundVariablesStack boundVariablesStack = new BoundVariablesStack(null);
        Set<String> freeVariablesSet = new LinkedHashSet<String>();

        findFreeVariablesInBoundExpression(freeVariablesSet, boundVariablesStack, paramListNode);

        return freeVariablesSet;
    }
    
    Set<String> findFreeNamesInExpr(Collection<String> possibleDependeeNamesSet, ParseTreeNode exprNode) {

        initState(false, topLevelFunctionNamesSet, possibleDependeeNamesSet);

        BoundVariablesStack boundVariablesStack = new BoundVariablesStack(null);
        Set<String> freeVariablesSet = new LinkedHashSet<String>();

        findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, exprNode);

        return freeVariablesSet;
    }
    
    /**
     * Finds the free variables used in expressions of the form x1,x2,...xn -> e where x1,.., xn are
     * bound variables over the expression's body.
     *
     * Creation date: (9/12/00 10:14:54 AM)
     * @param freeVariablesSet
     * @param boundVariablesStack
     * @param parseTree parent of x1,x2,...xn and preceding sibling of e
     */
    private void findFreeVariablesInBoundExpression(Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack, ParseTreeNode parseTree) {
    
        Set<String> varNamesSet = (firstPass ? new HashSet<String>() : null);

        int nVars = 0;              

        for (final ParseTreeNode patternVarNode : parseTree) {
                 
            patternVarNode.verifyType(CALTreeParserTokenTypes.LAZY_PARAM, CALTreeParserTokenTypes.STRICT_PARAM);                            
            String varName = patternVarNode.getText();

            if (firstPass && !varNamesSet.add(varName)) {
                // Repeated variable {varName} used in binding.
                compiler.logMessage(new CompilerMessage(patternVarNode, new MessageKind.Error.RepeatedVariableUsedInBinding(varName)));
            }
            
            ++nVars;

            // varName is now a bound variable for the body of the lambda
            boundVariablesStack.push(patternVarNode);                                              
        }

        findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, parseTree.nextSibling());

        boundVariablesStack.popN(nVars, firstPass);        
    }
    
    /**
     * Finds the free variables used in expressions of the form x1,x2,...xn -> e where x1,.., xn are
     * bound variables over the expression's body as defined by a case pattern.
     * For example: Cons x xs -> (length xs + 1, x)  
     * <p>
     * Case patterns are special at the moment because case pattern variables are allowed to be wildcards.
     * <p>
     * Creation date: (9/12/00 10:14:54 AM)
     * @param freeVariablesSet
     * @param boundVariablesStack
     * @param patternVarListNode the parent of the variables x1, x2, ..., xn (can include the wildcard pattern _)
     * @param boundExprNode the expression e   
     */
    private void findFreeVariablesInCasePatternVarListExpression(
        Set<String> freeVariablesSet,
        BoundVariablesStack boundVariablesStack, 
        ParseTreeNode patternVarListNode,
        ParseTreeNode boundExprNode) {
                
        Set<String> varNamesSet = (firstPass ? new HashSet<String>() : null);        
        int nVars = 0;

        for (final ParseTreeNode patternVarNode : patternVarListNode) {
            
            switch (patternVarNode.getType()) {
                
                case CALTreeParserTokenTypes.VAR_ID:
                {           
                    String varName = patternVarNode.getText();
        
                    if (firstPass && !varNamesSet.add(varName)) {
                        // Repeated variable {varName} used in binding.
                        compiler.logMessage(new CompilerMessage(patternVarNode, new MessageKind.Error.RepeatedVariableUsedInBinding(varName)));
                    }
                    
                    ++nVars;

                    // varName is now a bound variable for the body of the lambda
                    boundVariablesStack.push(patternVarNode);
                    
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

        findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, boundExprNode);
               
        if (firstPass) {     
            //now we convert unused pattern bound variables to wildcards.
            //This saves time in later compilation stages, but it is primarily an optimization
            //put in for the runtime.
            //For example:
            //MyDataCons x y z w -> y
            //is converted to
            //MyDataCons _ y _ _ -> y                                   
            for (final ParseTreeNode patternVarNode : patternVarListNode) {
                                                                          
                convertUnusedVar(boundVariablesStack, patternVarNode);                                                                              
            }
        }        

        boundVariablesStack.popN(nVars, firstPass);
    }
    
    /**
     * Find the free variables occurring in a case pattern expression where the argument bindings are
     * provided using field bindings (ie. matching notation).
     * 
     * Also, if firstPass is true, unused field bindings are removed and the parse tree is patched up so that 
     * punned field names are unpunned.
     *
     * @param freeVariablesSet
     * @param boundVariablesStack
     * @param dcArgBindingsNode the parent node of the arg bindings.
     * @param boundExprNode
     * @param patternDataConstructors (Set of DataConstructor objects) The data constructors used in this particular pattern in declaration order.      
     */
    private void findFreeVariablesInCasePatternMatchingExpression(
        Set<String> freeVariablesSet,
        BoundVariablesStack boundVariablesStack,
        ParseTreeNode dcArgBindingsNode,
        ParseTreeNode boundExprNode,
        Set<DataConstructor> patternDataConstructors) {
        
        if (firstPass) {
            // Perform the first pass verification and patching-up on the parse tree for the list of field bindings.
            firstPassProcessFieldBindingVarAssignmentListNode(dcArgBindingsNode, null, patternDataConstructors);
        } else {
            // Just verify the type.
            dcArgBindingsNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);
        }
        
        Set<String> varNamesSet = (firstPass ? new HashSet<String>() : null);
        
        int nVars = 0;
        
        for (final ParseTreeNode fieldBindingVarAssignmentNode : dcArgBindingsNode) {
            
            ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
            ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
            
            switch (patternVarNode.getType()) {
                
                case CALTreeParserTokenTypes.VAR_ID:
                {           
                    String varName = patternVarNode.getText();
                    
                    if (firstPass && !varNamesSet.add(varName)) {
                        // Repeated variable {varName} used in binding.
                        compiler.logMessage(new CompilerMessage(patternVarNode, new MessageKind.Error.RepeatedVariableUsedInBinding(varName)));
                    }
                    
                    ++nVars;
                    
                    // varName is now a bound variable for the body of the lambda
                    boundVariablesStack.push(patternVarNode);
                    
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
        
        findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, boundExprNode);
        
        if (firstPass) {
            // Now convert pattern vars for unused field bindings to wildcards.
            //This saves some work and simplifies assumptions in later compilation stages, 
            //For example:
            //MyDataCons {x=x, y=y, z=z, w=w} -> y
            //is converted to
            //MyDataCons {x=_, y=y, z=_, w=_} -> y
            
            for (final ParseTreeNode fieldBindingVarAssignmentNode : dcArgBindingsNode) {

                ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
                ParseTreeNode patternVarNode = fieldNameNode.nextSibling();
                
                if (patternVarNode.getType() == CALTreeParserTokenTypes.VAR_ID) {  
                    
                    if (boundVariablesStack.getUseCount(patternVarNode.getText()) == 0) {
                        // An unused var.
                        convertUnusedVar(boundVariablesStack, patternVarNode);
                        
                        if (FreeVariableFinder.GENERATE_CONVERTED_PATTERN_VAR_STATISTICS) {
                            ++nUnusedPatternVars;
                        }
                    } 
                }      
            }
        }        
        
        boundVariablesStack.popN(nVars, firstPass);
    }

    /**
     * A helper function for findFreeVariablesInCase.
     * Finds the free vars in a record pattern expression.
     * 
     * @param freeVariablesSet
     * @param boundVariablesStack
     * @param patternNode
     * @return false iff an unexpected parse tree node were encountered.
     */
    private boolean findFreeVarsInRecordPatternExpression(Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack, ParseTreeNode patternNode) {
        
        ParseTreeNode baseRecordPatternNode = patternNode.firstChild();
        baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
        
        ParseTreeNode baseRecordPatternVarNode = baseRecordPatternNode.firstChild();
        String baseRecordVarName = null;
        if (baseRecordPatternVarNode != null) 
        {                    
            switch (baseRecordPatternVarNode.getType())
            {
                case CALTreeParserTokenTypes.VAR_ID:
                    baseRecordVarName = baseRecordPatternVarNode.getText();
                    break;
                    
                case CALTreeParserTokenTypes.UNDERSCORE:                              
                    break;
                    
                default:
                {  
                    baseRecordPatternVarNode.unexpectedParseTreeNode();                             
                    return false;
                }                            
            }
        }                                                           
       
        ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();                    
        
        if (firstPass) {
            // Perform the first pass verification and patching-up on the parse tree for a list of field bindings.            
            firstPassProcessFieldBindingVarAssignmentListNode(fieldBindingVarAssignmentListNode, baseRecordVarName, Collections.<DataConstructor>emptySet());
        }
        
        int nVars;
        if (baseRecordVarName != null) {
            nVars = 1;
            boundVariablesStack.push(baseRecordPatternVarNode);                                                                       
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
                    //String patternVar = patternVarNode.getText();                        
                                        
                    ++nVars;                                                    
                    boundVariablesStack.push(patternVarNode);
                    break;
                }
                
                                               
                case CALTreeParserTokenTypes.UNDERSCORE:
                    break;
                        
                default:
                {
                    patternVarNode.unexpectedParseTreeNode();                               
                    return false;                                                              
                }
            }                                                                                          
        }

        findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, patternNode.nextSibling());
           
        if (firstPass) {
            //now we convert unused pattern bound variables to wildcards.
            //This saves time in latter compilation stages, but it is primarily an optimization
            //put in for the runtime.
            //For example:
            //case r of {s | field1 = f1, field2 = f2, field3 = f3} -> e
            //is converted to {_ | field1 = _, field2 = f2, field3 = _}
            //if only f2 is used in e and s, f1 and f3 are not used. 
                               
            if (baseRecordPatternVarNode != null) {
                convertUnusedVar(boundVariablesStack, baseRecordPatternVarNode);                                                                               
            }  
                              
            for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {
                                            
                ParseTreeNode patternVarNode = fieldBindingVarAssignmentNode.getChild(1);                       
                convertUnusedVar(boundVariablesStack, patternVarNode);                                                                              
            }   
        }
                            
        boundVariablesStack.popN(nVars, firstPass);
        
        return true;
    }

    /**
     * A helper function that finds the free variables in each of the child expressions of parseTree.
     *
     * Creation date: (2/28/01 11:31:54 AM)
     * @param freeVariablesSet 
     * @param boundVariablesStack 
     * @param parseTree
     */
    private void findFreeVariablesInChildExpressions(Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack, ParseTreeNode parseTree) {

        for (final ParseTreeNode exprNode : parseTree) {

            findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, exprNode);
        }
    }
    
    /**
     * Find the free variables occurring in an expression Node. Intuitively, the free variables
     * are the subset of possibleDependeeNamesSet on which the parseTree actually depends on.
     *
     * For example in: 
     * f x = y + x + (let y = 2 in y)
     * the first occurence of y is the only free variable in the defining expression of f.
     *
     * Creation date: (8/31/00 12:26:07 PM)
     * @param freeVariablesSet The free variables encountered while traversing the parse tree.
     * @param boundVariablesStack The function is not dependent on bound variables appearing in
     *      its definition. These are its argument variables, or variables introduced in internal let
     *      declarations or binder variables in a lambda declaration. This stack varies depending on where 
     *      we are in the definition. The same variable name can occur more than once because of scoping.
     * @param parseTree expression parse tree
     */
    private void findFreeVariablesInExpr(Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack, ParseTreeNode parseTree) {

        int nodeType = parseTree.getType();

        switch (nodeType) {

            case CALTreeParserTokenTypes.LITERAL_let :
            case CALTreeParserTokenTypes.VIRTUAL_LET_NONREC:
            case CALTreeParserTokenTypes.VIRTUAL_LET_REC:            
            {
                findFreeVariablesInLet(freeVariablesSet, boundVariablesStack, parseTree);               
                return;
            }

            case CALTreeParserTokenTypes.LAMBDA_DEFN :
            {
                ParseTreeNode paramListNode = parseTree.firstChild();
                paramListNode.verifyType(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST);

                findFreeVariablesInBoundExpression(freeVariablesSet, boundVariablesStack, paramListNode);

                return;
            }

            case CALTreeParserTokenTypes.LITERAL_case :
            case CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE:
            case CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE:
            case CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE:
            {
                findFreeVariablesInCase(freeVariablesSet, boundVariablesStack, parseTree);                
                return;
            }

            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD:
            {
                findFreeVariablesInDataConsFieldSelection(freeVariablesSet, boundVariablesStack, parseTree);
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
                findFreeVariablesInChildExpressions(freeVariablesSet, boundVariablesStack, parseTree);
                return;
            }

            case CALTreeParserTokenTypes.APPLICATION :
            {
                for (final ParseTreeNode exprNode : parseTree) {

                    findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, exprNode);
                }

                return;
            }

            // function names, class method names and variables
            case CALTreeParserTokenTypes.QUALIFIED_VAR :

                findFreeVariablesInQualifiedVar(freeVariablesSet, boundVariablesStack, parseTree);
                return;

            //data constructors
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            {
                if (firstPass) {
                    resolveDataConsName(parseTree);
                }
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
                findFreeVariablesInChildExpressions(freeVariablesSet, boundVariablesStack, parseTree);
                return;
            }
            
            //A list data value    
            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
                findFreeVariablesInChildExpressions(freeVariablesSet, boundVariablesStack, parseTree);
                return;
                
            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR:
            {
                ParseTreeNode baseRecordNode = parseTree.firstChild();
                baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);
                ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();
                if (baseRecordExprNode != null) {
                    findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, baseRecordExprNode);            
                }
                
                ParseTreeNode fieldModificationListNode = baseRecordNode.nextSibling();
                fieldModificationListNode.verifyType(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST);
                
                if (firstPass) {
                    
                    //check that there are no duplicate field names e.g. {colour = "red", height = 2.0, colour = "blue"}
                    //is not allowed at this stage because of the duplicate field name colour.
                    
                    //if this is a record literal rather than a record extension, we cannot have a field update
                    //i.e. {colour := "red", height = 2.0} is a static error flagged at this point, whereas
                    //{{} | colour := "red", height = 2.0} will be a type-check error flagged at a latter stage of
                    //compilation.
                    
                    Set<FieldName> fieldNamesSet = new HashSet<FieldName>();
                    
                    for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
                    
                        fieldModificationNode.verifyType(CALTreeParserTokenTypes.FIELD_EXTENSION, CALTreeParserTokenTypes.FIELD_VALUE_UPDATE);
                        
                        if (fieldModificationNode.getType() == CALTreeParserTokenTypes.FIELD_VALUE_UPDATE && baseRecordExprNode == null) {
                            //"The field value update operator ':=' can not be used in a record literal value."
                            compiler.logMessage(new CompilerMessage(fieldModificationNode,
                                new MessageKind.Error.FieldValueUpdateOperatorUsedInRecordLiteralValue()));
                        }
                        
                        ParseTreeNode fieldNameNode = fieldModificationNode.firstChild(); 
                        FieldName fieldName = compiler.getTypeChecker().getFieldName(fieldNameNode);                        
                                           
                        if (!fieldNamesSet.add(fieldName)) {
                            // Repeated field name {fieldName.getCalSourceForm()} in record literal value. 
                            compiler.logMessage(new CompilerMessage(fieldNameNode, new MessageKind.Error.RepeatedFieldNameInRecordLiteralValue(fieldName)));
                        }                              
                    }
                    
                }
                
                for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
                                             
                    findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, fieldModificationNode.getChild(1));         
                }
                
                return;
            }
            
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD:
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:                
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, exprNode);
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
     * Find the free variables occurring in a data constructor field selection expression. 
     * 
     * Also, if firstPass is true, and the expression is a data constructor field selection (dot operator - selection from a 
     * data constructor valued-expression, eg. [1.0].Cons.head), the parse tree for the select node is augmented by addition 
     * of a child node for the qualified var which would appear in the rhs of the alt for a corresponding case expression.
     * The transformation which makes use of this additional node comes in a later in the compilation stage.
     *
     * @param freeVariablesSet
     * @param boundVariablesStack 
     * @param selectNode parse tree for the select expression.
     */
    private void findFreeVariablesInDataConsFieldSelection(Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack, ParseTreeNode selectNode) {
        
        selectNode.verifyType(CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD);
        
        ParseTreeNode exprNode = selectNode.firstChild();
        
        // Find in the expr from which to select.
        findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, exprNode);
        
        ParseTreeNode dcNameNode = exprNode.nextSibling();
        dcNameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
        
        ParseTreeNode fieldNameNode = dcNameNode.nextSibling();
        fieldNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);
        
        DataConstructor dataConstructor = resolveDataConsName(dcNameNode);
        FieldName fieldName = compiler.getTypeChecker().getFieldName(fieldNameNode);
        
        // Check that the field name exists.
        boolean fieldNameExists = (dataConstructor.getFieldIndex(fieldName) > -1);
        
        if (!fieldNameExists) {
            MessageKind messageKind;
            if (dataConstructor.getArity() == 0) {
                messageKind = new MessageKind.Error.ZeroFieldDataConstructorFieldReference(dataConstructor, fieldName);
            } else {
                messageKind = new MessageKind.Error.UnknownDataConstructorField(dataConstructor, fieldName);
            }
            compiler.logMessage(new CompilerMessage(fieldNameNode, messageKind));
        }

        // Simulate finding in the bound part of a case expr.
        String patternVarName;
        if (fieldNameNode.getType() == CALTreeParserTokenTypes.VAR_ID) {
            // textual field.
            patternVarName = fieldNameNode.getText();
        
        } else {
            // ordinal field.
            
            /* 
             * Don't convert the field name to a valid pattern var if the field name is not the name of a field in the data constructor.
             * This is to guard against subsequent analysis turning up errors against an identifier which doesn't appear in the code.
             * For instance, 
             *   "[1.0].Cons.#1" 
             * 
             *  eventually is transformed to 
             *   case [1.0] of 
             *   Cons {#1=field1} -> field1;
             *   
             * Since #1 is not a field of Cons, the field1 pattern var (on the left) is not bound to it.
             * Further analysis would attempt to bind the field1 identifier (on the right) to something else.
             * This might result in an error using the undefined identifier field1 (if it's not defined), 
             * or an ambiguous reference error (if there were multiple field1's visible).
             */
            if (fieldNameExists) {
                patternVarName = "field" + ((FieldName.Ordinal)fieldName).getOrdinal();
            } else {
                patternVarName = fieldNameNode.getText();
            }
        }
        
        SourcePosition fieldNameSourcePosition = fieldNameNode.getSourcePosition();
        
        ParseTreeNode qualifiedVarNode;
        if (firstPass) {
            // The bound expr node is a node of type QUALIFIED_VAR with the same name as the pattern var.
            // ie. moduleName is unspecified, unqualified name is the pattern var name.
            qualifiedVarNode = ParseTreeNode.makeUnqualifiedVarNode(patternVarName, fieldNameSourcePosition);
            
            //
            // Set the qualified var node for the alt expression as the next sibling.
            // selectNode is "augmented" with this node, and children become: expr qualifiedCons fieldName qualifiedVar
            //
            fieldNameNode.setNextSibling(qualifiedVarNode);
            
        } else {
            qualifiedVarNode = fieldNameNode.nextSibling();
        }
        ParseTreeNode varIdNodeCopy = new ParseTreeNode();
        varIdNodeCopy.copyContentsFrom(qualifiedVarNode.getChild(1));
        
        // Find in the qualified var.
        boundVariablesStack.push(varIdNodeCopy);
        findFreeVariablesInQualifiedVar(freeVariablesSet, boundVariablesStack, qualifiedVarNode);
        boundVariablesStack.popN(1, firstPass);
    }
    
    /**
     * A helper function for findFreeVariables. 
     * 
     * If firstPass is true:
     * It also checks that there are no duplicate patterns in the case expression. Haskell allows for repeated patterns. 
     * The reason for this is that their pattern matching syntax is more complicated, allowing for guards and nested patterns, 
     * and so it becomes more difficult to enforce the constraint that patterns should not overlap. So in Haskell, patterns 
     * are matched sequentially, with the first pattern that matches being taken. In CAL, we can't have duplicate patterns,
     * and the wildcard pattern must be last if present. This hopefully will give users the right intuition 
     * as to how the pattern matching syntax works.
     * 
     * @param freeVariablesSet
     * @param boundVariablesStack
     * @param caseExprNode
     */
    private void findFreeVariablesInCase (Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack, ParseTreeNode caseExprNode) {
       
        if (firstPass) {
            caseExprNode.verifyType(CALTreeParserTokenTypes.LITERAL_case);
        } else {
            caseExprNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE,
                CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE,
                CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE);
        }
        
        ParseTreeNode exprNode = caseExprNode.firstChild();
        findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, exprNode);

        ParseTreeNode altListNode = exprNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);
                        
        final Set<Object> consNamesSet;
        if (firstPass) {
            consNamesSet = new HashSet<Object>();
            //assume the data constructor case, and correct that assumption later if incorrect
            caseExprNode.setType(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE);
        } else {
            consNamesSet = null;
        }
        
        for (final ParseTreeNode altNode : altListNode) {

            altNode.verifyType(CALTreeParserTokenTypes.ALT);

            ParseTreeNode patternNode = altNode.firstChild();

            int nodeKind = patternNode.getType();
            switch (nodeKind) {
                
                case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR :
                {
                    ParseTreeNode dcNameListNode = patternNode.firstChild();
                    dcNameListNode.verifyType(CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_LIST, CALTreeParserTokenTypes.DATA_CONSTRUCTOR_NAME_SINGLETON);
                    
                    ParseTreeNode dcArgBindingsNode = dcNameListNode.nextSibling();  
                    
                    //(Set of DataConstructor objects) The data constructors used in this particular pattern in declaration order.                   
                    final Set<DataConstructor> patternDataConstructors = firstPass ? new LinkedHashSet<DataConstructor>() : null;
                    
                    if (firstPass) {
                        
                        final boolean checkArity = dcArgBindingsNode.getType() == CALTreeParserTokenTypes.PATTERN_VAR_LIST;
                        final int impliedArity = checkArity ? dcArgBindingsNode.getNumberOfChildren() : -1;
                                                                        
                        for (final ParseTreeNode dcNameNode : dcNameListNode) {
                            
                            DataConstructor dataConstructor = resolveDataConsName(dcNameNode);
                            QualifiedName dataConsName = dataConstructor.getName();
                            if (!consNamesSet.add(dataConsName)) {
                                // Repeated pattern {dataConstructor} in case expression. 
                                compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.RepeatedPatternInCaseExpression(dataConsName.getQualifiedName())));
                                break;                                           
                            }
                                                                                   
                            //we check that the implied arity is correct for pattern var list based case unpackings.
                            //we do this here in order to provide a reasonable error position (based on dcNameNode) in the case
                            //that something is wrong. This is because the patternVarList may be empty, in which case it will not
                            //have a source position.
                            if (checkArity) {
                                if (dataConstructor.getArity() != impliedArity) {
                                    // Check that the number of variables expected by the data constructor corresponds to the number actually supplied in the pattern.                            
                                    //"The data constructor {0} must have exactly {1} pattern argument(s) in its case alternative."
                                    compiler.logMessage(new CompilerMessage(dcNameNode, 
                                        new MessageKind.Error.ConstructorMustHaveExactlyNArgsInPattern(dataConstructor)));
                                 }
                            } else {
                                 patternDataConstructors.add(dataConstructor);
                            }                            
                        }                                                                                
                    }
                                                           
                    switch (dcArgBindingsNode.getType()) {
                        case CALTreeParserTokenTypes.PATTERN_VAR_LIST:
                        {
                            findFreeVariablesInCasePatternVarListExpression(freeVariablesSet, boundVariablesStack, dcArgBindingsNode, patternNode.nextSibling());
                            break;
                        }
                        case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST:
                        {                   
                            findFreeVariablesInCasePatternMatchingExpression(freeVariablesSet, boundVariablesStack, dcArgBindingsNode, patternNode.nextSibling(), patternDataConstructors);
                            break;                        
                        }
                        default:
                        {
                            dcArgBindingsNode.unexpectedParseTreeNode();
                            return;
                        }
                    }
                    
                    break;
                }
                
                case CALTreeParserTokenTypes.INT_PATTERN :
                {
                    ParseTreeNode intListNode = patternNode.firstChild();
                    intListNode.verifyType(CALTreeParserTokenTypes.MAYBE_MINUS_INT_LIST);
                    
                    if (firstPass) {
                        
                        for (final ParseTreeNode maybeMinusIntNode : intListNode) {

                            final boolean minus;
                            final ParseTreeNode intLiteralNode;
                            
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

                            Integer integerValue;
                            try {
                                integerValue = Integer.valueOf(symbolText);
                                
                            } catch (NumberFormatException nfe) {
                                boolean parseableAsBigInteger = true;
                                try {
                                    new BigInteger(symbolText);
                                
                                } catch (NumberFormatException nfe2) {
                                    parseableAsBigInteger = false;
                                }
                                
                                if (parseableAsBigInteger) {
                                    // {symbolText} is outside of range for the Int type.  The valid range is -2147483648 to 2147483647 (inclusive).
                                    compiler.logMessage(new CompilerMessage(maybeMinusIntNode, new MessageKind.Error.IntLiteralOutOfRange(symbolText)));
                                    break;
                                } else {
                                    // Unable to parse {symbolText} to an integer literal.
                                    compiler.logMessage(new CompilerMessage(maybeMinusIntNode, new MessageKind.Error.UnableToParseToIntegerLiteral(symbolText)));
                                    break;  
                                }
                            }
                            
                            // Set the data on the maybeMinusIntNode to be the Integer object.
                            maybeMinusIntNode.setIntegerValueForMaybeMinusIntLiteral(integerValue);
                            
                            // Check against the Integer, not the symbol, to catch adding both -0 and 0.
                            if (!consNamesSet.add(integerValue)) {
                                // Repeated pattern value {value} in case expression.
                                compiler.logMessage(new CompilerMessage(maybeMinusIntNode, new MessageKind.Error.RepeatedPatternValueInCaseExpression(symbolText)));
                                break;                                           
                            }
                        }
                    }
                    
                    ParseTreeNode boundExprNode = patternNode.nextSibling();
                    findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, boundExprNode);

                    break;
                }
                
                case CALTreeParserTokenTypes.CHAR_PATTERN :
                {
                    ParseTreeNode charListNode = patternNode.firstChild();
                    charListNode.verifyType(CALTreeParserTokenTypes.CHAR_LIST);
                    
                    if (firstPass) {
                        
                        for (final ParseTreeNode charLiteralNode : charListNode) {

                            charLiteralNode.verifyType(CALTreeParserTokenTypes.CHAR_LITERAL);
                            
                            String symbolText = charLiteralNode.getText();
                            Character charValue = null;
                            try {
                                char c = StringEncoder.unencodeChar(symbolText);
                                charValue = Character.valueOf(c);
                                
                            } catch (IllegalArgumentException e) {
                                // The encoded character did not have a valid format. 
                                // This should never happen for nodes created by the parser.

                                // Unable to parse {symbolText} to a character literal.
                                compiler.logMessage(new CompilerMessage(charLiteralNode, new MessageKind.Error.UnableToParseToCharacterLiteral(symbolText)));
                            }

                            // Set the data on the charLiteralNode to be the Character object.
                            charLiteralNode.setCharacterValueForCharLiteral(charValue);
                            
                            // Check against the Character, not the symbol, to catch adding unescaped and escaped forms..
                            if (!consNamesSet.add(charValue)) {
                                // Repeated pattern value {value} in case expression.
                                compiler.logMessage(new CompilerMessage(charLiteralNode, new MessageKind.Error.RepeatedPatternValueInCaseExpression(symbolText)));
                                break;                                           
                            }
                        }
                    }
                    
                    ParseTreeNode boundExprNode = patternNode.nextSibling();
                    findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, boundExprNode);

                    break;
                }
                
                case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
                case CALTreeParserTokenTypes.COLON :
                case CALTreeParserTokenTypes.UNDERSCORE :                
                {
                    if (firstPass) {
                                                                                              
                        String symbolText = patternNode.getText();
                        if (!consNamesSet.add(symbolText)) {
                            // Repeated pattern {symbolText} in case expression. 
                            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.RepeatedPatternInCaseExpression(symbolText)));
                            break;                                                                   
                        }   
                        
                        if (nodeKind == CALTreeParserTokenTypes.UNDERSCORE && altNode.nextSibling() != null) {
                            // The wildcard pattern '_' must be the final pattern in a case expression.
                            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.WildcardPatternMustBeFinalInCaseExpression()));
                            break;
                        }                                                                                                                
                    }
                    
                    findFreeVariablesInCasePatternVarListExpression(freeVariablesSet, boundVariablesStack, patternNode, patternNode.nextSibling());
                    break;
                } 
                
                case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
                case CALTreeParserTokenTypes.VIRTUAL_UNIT_DATA_CONSTRUCTOR:
                {
                    if (firstPass) {
                                                
                        if (patternNode.hasExactlyOneChild()) { 
                            //a one variable tuple pattern is illegal                          
                            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.Illegal1TuplePattern()));
                            break ;                               
                        }
                        
                        if (patternNode.hasNoChildren()) {
                            patternNode.setType(CALTreeParserTokenTypes.VIRTUAL_UNIT_DATA_CONSTRUCTOR);

                            //a unit-case expression can only have one pattern- the one that matches ().
                            if (!altListNode.hasExactlyOneChild()) {
                                compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.OnlyOnePatternAllowedInCaseExpressionWithUnitPattern()));                            
                            }
                        } else {
                            //this is a tuple case expression, not a data constructor case expression
                            caseExprNode.setType(CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE);
                        
                            //a tuple-case expression can only have one pattern- the one that decomposes the tuple.
                            if (!altListNode.hasExactlyOneChild()) {
                                compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.OnlyOnePatternAllowedInCaseExpressionWithTuplePattern()));                            
                            }
                        }
                                                                                
                        String symbolText = patternNode.getText();
                        if(!consNamesSet.add(symbolText)) {
                            // Repeated pattern {symbolText} in case expression. 
                            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.RepeatedPatternInCaseExpression(symbolText)));
                            break;                                                                   
                        }                                                                   
                    }
    
                    findFreeVariablesInCasePatternVarListExpression(freeVariablesSet, boundVariablesStack, patternNode, patternNode.nextSibling());
                    break;
                }
                
                case CALTreeParserTokenTypes.RECORD_PATTERN:
                {
                    if (firstPass) {
                        //a record-case expression can only have one pattern- the one that decomposes the record.
                        if (!altListNode.hasExactlyOneChild()) {
                            compiler.logMessage(new CompilerMessage(patternNode, new MessageKind.Error.OnlyOnePatternAllowedInCaseExpressionWithRecordPattern()));                            
                        }
                        
                        //this is a record case expression, not a data constructor case expression
                        caseExprNode.setType(CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE);
                    }
                    
                    boolean wasOK = findFreeVarsInRecordPatternExpression(freeVariablesSet, boundVariablesStack, patternNode);
                    
                    if (!wasOK) {
                        return;
                    }
                    
                    break;                    
                }
                             

                default : 
                {
                    patternNode.unexpectedParseTreeNode();                                 
                    return;
                }
            }
        }        
    }
    
    /**
     * Perform the first pass verification and patching-up on the parse tree for a list of field bindings.
     * 
     * 1. check that each field name used in the pattern is actually a field of the data constructor in question.
     * 
     * 2. check that there are no duplicate fieldnames.
     *  
     * 3. check that there are no duplicate pattern variable names, taking into account  punning
     * (which is where instead of writing "fieldName = patternVar" we just write "fieldName" and 
     * take the fieldName's name as the patternVar). For example: in the case of
     * {r | field1, field2 = x} the pattern variables are {r, field1, x}.
     * 
     * In the case of punning, the parse tree is patched up so that subsequent analysis can assume that punning doesn't occur.
     * 
     * @param fieldBindingVarAssignmentListNode the parse tree for a list of field bindings.
     * @param visiblePatternVarName if non-null, the pattern variable (eg. base record var) inherited by this list of bindings.
     * @param patternDataConstructors (Set of DataConstructor objects) The data constructors used in this particular pattern in declaration order. 
     *      Will be an empty set in the case of a record pattern.
     */
    void firstPassProcessFieldBindingVarAssignmentListNode(
        ParseTreeNode fieldBindingVarAssignmentListNode,
        String visiblePatternVarName,
        Set<DataConstructor> patternDataConstructors) {
        
        fieldBindingVarAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);
        
        Set<FieldName> fieldNamesSet = new HashSet<FieldName>();                      
        
        Set<String> patternVarNamesSet = new HashSet<String>();
        if (visiblePatternVarName != null) {
            patternVarNamesSet.add(visiblePatternVarName);                        
        }                                               
        
        for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {
            
            fieldBindingVarAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT);
            
            ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();                          
            FieldName fieldName = compiler.getTypeChecker().getFieldName(fieldNameNode);
                        
            //verify that the field name actually *is* a field for all the data constructors                        
            for (final DataConstructor dataConstructor : patternDataConstructors) {
                                 
                if (dataConstructor.getFieldIndex(fieldName) == -1) {
                    
                    MessageKind messageKind;
                    if (dataConstructor.getArity() == 0) {
                        messageKind = new MessageKind.Error.ZeroFieldDataConstructorFieldReference(dataConstructor, fieldName);
                    } else {
                        messageKind = new MessageKind.Error.UnknownDataConstructorField(dataConstructor, fieldName);
                    }
                    
                    //Called when a data constructor case alt using matching notation attempts to bind a field which doesn't exist.
                    compiler.logMessage(new CompilerMessage(fieldNameNode, messageKind));
                }                           
            }                       
            
            if (!fieldNamesSet.add(fieldName)) {
                // Repeated field name {fieldName} in field binding pattern.
                compiler.logMessage(new CompilerMessage(fieldNameNode, new MessageKind.Error.RepeatedFieldNameInFieldBindingPattern(fieldName)));
            }
            
            //handle the case of punning. At this point we patch up the parse tree so that in subsequent analysis
            //we assume that punning doesn't occur.
            //In the case of textual field names, punning means: fieldName ---> fieldName = fieldName
            //In the case of numeric field names, punning means: fieldName ---> fieldName = _
            //this is because something like #2 is a valid numeric field name but not a valid CAL variable name.                            
            
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
            
            switch (patternVarNode.getType())
            {                                                   
                case CALTreeParserTokenTypes.VAR_ID:
                {
                    String patternVarName = patternVarNode.getText();                            
                    if (!patternVarNamesSet.add(patternVarName)) {
                        // Repeated pattern variable {patternVarName} in field binding pattern. 
                        compiler.logMessage(new CompilerMessage(patternVarNode, new MessageKind.Error.RepeatedPatternVariableInFieldBindingPattern(patternVarName)));
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
    }
    
    /**
     * A helper function to convert an unused pattern var node to a wildcard node.
     * @param boundVariablesStack
     * @param patternVarNode
     */
    private void convertUnusedVar(BoundVariablesStack boundVariablesStack, ParseTreeNode patternVarNode) {
        if (patternVarNode.getType() == CALTreeParserTokenTypes.VAR_ID) {  

            String patternVarNodeText = patternVarNode.getText();
            if (boundVariablesStack.getUseCount(patternVarNodeText) == 0) {
                
                patternVarNode.setType(CALTreeParserTokenTypes.UNDERSCORE);
                patternVarNode.setText("_");
                patternVarNode.setUnusedVarNameForWildcardPatternVar(patternVarNodeText); // Another option is just to leave the text.
                
                if (FreeVariableFinder.GENERATE_CONVERTED_PATTERN_VAR_STATISTICS) {
                    ++nUnusedPatternVars;
                }
            } 
        }      
    }
       
    /**
     * A helper function for findFreeVariables.
     * 
     * Also, reorders the let definitions (introducing additional ones if needed as a side-effect)
     * into minimal dependency groups.
     *
     * Creation date: (8/31/00 2:29:29 PM)     
     * @param freeVariablesSet
     * @param boundVariablesStack  
     * @param letNode
     */
    private void findFreeVariablesInLet(Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack, ParseTreeNode letNode) {
        
        if (firstPass) {           
            letNode.verifyType(CALTreeParserTokenTypes.LITERAL_let);
        } else {
            letNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_LET_NONREC, CALTreeParserTokenTypes.VIRTUAL_LET_REC);
        }        

        ParseTreeNode defnListNode = letNode.firstChild();
        defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);

        Set<String> localFunctionNamesSet = (firstPass ? new HashSet<String>() : null);
        int nLocalFunctions = 0;
        
        for (ParseTreeNode defnNode = defnListNode.firstChild(); defnNode != null; defnNode = defnNode.nextSibling()) {
                 
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                                               
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                localFunctionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                ++nLocalFunctions;
    
                // Having two local functions of the same name in a single letrec is an error.
                String functionName = localFunctionNameNode.getText();
                if (firstPass && !localFunctionNamesSet.add(functionName)) {
                    // Repeated definition of {functionName} in let declaration.
                    compiler.logMessage(new CompilerMessage(localFunctionNameNode, new MessageKind.Error.RepeatedDefinitionInLetDeclaration(functionName)));
                }
    
                // functionName is a bound variable for all declarations in the 'let' and for the expression
                // following the 'in'.
                boundVariablesStack.push(localFunctionNameNode);
                
            } else if (defnNode.getType() == CALTreeParserTokenTypes.LET_PATTERN_MATCH_DECL) {
                
                /// Desugar the pattern match declarations
                //
                if (firstPass) {
                    final LocalPatternMatchDeclarationChecker.Result desugaringResult = LocalPatternMatchDeclarationChecker.processPatternMatchDeclNode(this, compiler, defnNode, localFunctionNamesSet);
                    
                    final LinkedHashMap<String, ParseTreeNode> desugaredVarsMap = desugaringResult.getNamesToDesugaredVarNodes();
                    final ParseTreeNode lastDesugaredDefn = desugaringResult.getLastDesugaredDefn();

                    for (final Map.Entry<String, ParseTreeNode> entry : desugaredVarsMap.entrySet()) {
                       
                        final String functionName = entry.getKey();
                        final ParseTreeNode functionNameNode = entry.getValue();
                        
                        // Having two local functions of the same name in a single letrec is an error.
                        // But this should have been caught by the desugaring step already, so this is impossible
                        if (!localFunctionNamesSet.add(functionName)) {
                            throw new IllegalStateException("The duplicate names declared in a pattern match declaration should have been caught before this point.");
                        }
            
                        ++nLocalFunctions;
                        
                        // functionName is a bound variable for all declarations in the 'let' and for the expression
                        // following the 'in'.
                        boundVariablesStack.push(functionNameNode);
                    }
                    
                    // for the outer loop iteration to proceed properly, we set defnNode to the last desugared definition node
                    defnNode = lastDesugaredDefn;
                }
            }
        }
               
        //check that there is at most 1 local type declaration for each local function definition.
        //check that a local function is not defined more than once.           
        if (firstPass) {

            final Set<String> declaredLocals = new HashSet<String>();

            for (final ParseTreeNode defnNode : defnListNode) {

                if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION) {

                    ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                    optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                    
                    ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                    typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                    
                    ParseTreeNode localSCNameNode = typeDeclNode.firstChild();
                    String localFunctionName = localSCNameNode.getText();

                    if (!localFunctionNamesSet.contains(localFunctionName)) {
                        // the local function is declared but not defined. This is illegal in Haskell and CAL.
                        compiler.logMessage(new CompilerMessage(typeDeclNode, new MessageKind.Error.DefinitionMissing(localFunctionName)));
                    }

                    if (!declaredLocals.add(localFunctionName)) {
                        // the local function has already been declared. This is an attempted redeclaration.
                        compiler.logMessage(new CompilerMessage(typeDeclNode, new MessageKind.Error.RepeatedTypeDeclaration(localFunctionName)));
                    }
                    
                    //update the name of the local function used in the type declaration to its unique name
                    BoundVariablesStack.BoundVariable boundVar = boundVariablesStack.contains(localFunctionName, false);
                    localSCNameNode.setText(boundVar.getUniqueName());
                }
            }
        }

        /// Go through the bodies of the local function definitions
        //
        for (final ParseTreeNode defnNode : defnListNode) {
                 
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {            
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                ParseTreeNode varListNode = localFunctionNameNode.nextSibling();
                findFreeVariablesInBoundExpression(freeVariablesSet, boundVariablesStack, varListNode);
            }
        }
        
        ParseTreeNode exprNode = defnListNode.nextSibling();
        findFreeVariablesInExpr(freeVariablesSet, boundVariablesStack, exprNode);
        if (firstPass) {
            //update with the uniqueness transformed local names
            localFunctionNamesSet = boundVariablesStack.getUniqueVarNamesSet(nLocalFunctions);
        }
        boundVariablesStack.popN(nLocalFunctions, firstPass);
        
        
        //now do local dependency order transforms. What this means is that we reorder the let group into minimal
        //mutually dependent sets of symbols, and distinguish recursive lets from non-recursive lets.
        //e.g. let f1 = e1; f2 = e2; f3 = e3; f4 = e4; in e
        //could be transformed into
        //letNonRec f3 = e3; in letRec f1 = e1; f4 = e4; in letRec f2 = e2; in e
        //This implies in this case that:
        //   the definition of f3 does not depend on f1, f2, f4
        //   f1 and f4 depend recursively on each other but not on f2.
        //   f2 depends recursively on itself.
                                         
        if (firstPass) {
            //we don't want to look at top level functions and any local symbols bound in
            //an enclosing scope of this let block.
            Set<String> ignorableNamesSet = new HashSet<String>(topLevelFunctionNamesSet);
            ignorableNamesSet.addAll(boundVariablesStack.getUniqueVarNamesSet());
                                   
            //essentially this is a map from the names of the functions defined in this let block to the
            //list of the functions defined in this let block upon which their definition depends. i.e. [(String, [String])].            
            VertexBuilderList<String> vertexBuilderList = new VertexBuilderList<String>();
            
            Map<String, ParseTreeNode> functionNameToDefinitionNode = new HashMap<String, ParseTreeNode>(); //String -> ParseTreeNode
            Map<String, ParseTreeNode> functionNameToDeclarationNode = new HashMap<String, ParseTreeNode>(); //String -> ParseTreeNode  
                   
            FreeVariableFinder localDependeesFinder = new FreeVariableFinder(compiler, topLevelFunctionNamesSet, currentModuleTypeInfo);
            localDependeesFinder.initState(false, ignorableNamesSet, localFunctionNamesSet);
            
            for (final ParseTreeNode defnNode : defnListNode) {
                
                switch (defnNode.getType())
                {                   
                    case CALTreeParserTokenTypes.LET_DEFN:
                    {                                                       
                        ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                        
                        ParseTreeNode localSCNameNode = optionalCALDocNode.nextSibling();
                        String localSCName = localSCNameNode.getText();
                        
                        functionNameToDefinitionNode.put(localSCName, defnNode);
                        
                        ParseTreeNode varListNode = localSCNameNode.nextSibling();                    
                        Set<String> letDependeesSet = new LinkedHashSet<String>();                   
                        localDependeesFinder.findFreeVariablesInBoundExpression(letDependeesSet, new BoundVariablesStack(null), varListNode);
                                                   
                        vertexBuilderList.add(new VertexBuilder<String>(localSCName, letDependeesSet));
                        break;
                    }
                    
                    case CALTreeParserTokenTypes.LET_DEFN_TYPE_DECLARATION:
                    {
                        ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                        
                        ParseTreeNode typeDeclNode = optionalCALDocNode.nextSibling();
                        typeDeclNode.verifyType(CALTreeParserTokenTypes.TYPE_DECLARATION);
                        
                        String localSCName = typeDeclNode.firstChild().getText();
                        functionNameToDeclarationNode.put(localSCName, defnNode);
                        break;
                    }
                    
                    default:
                    {
                        defnNode.unexpectedParseTreeNode();
                        break;
                    }
                }                       
            }  
            
            //modify the parseTree structure based on the connected component analysis                  
            
            ParseTreeNode currentLetNode = letNode;            
            
            Graph<String> g = new Graph<String>(vertexBuilderList);
            g = g.calculateStronglyConnectedComponents();
            for (int i = 0, nComponents = g.getNStronglyConnectedComponents(); i < nComponents; ++i) {

                Graph<String>.Component component = g.getStronglyConnectedComponent(i);
                
                ParseTreeNode currentLetDefnListNode = new ParseTreeNode(CALTreeParserTokenTypes.LET_DEFN_LIST, "LET_DEFN_LIST");
                for (int j = 0, componentSize = component.size(); j < componentSize; ++j) {
                    Vertex<String> functionVertex = component.getVertex(j);
                    String functionName = functionVertex.getName();
                    
                    //determine if a letNonRec or a letRec. Note that even if the component size is 1 it could
                    //be a recursive definition e.g. "let f x = if x < 0 then 0 else f (x - 1) + x in ..."                   
                    if (componentSize == 1 && !functionVertex.getAdjacentVertices().contains(functionVertex)) {
                        currentLetNode.setType(CALTreeParserTokenTypes.VIRTUAL_LET_NONREC);
                        currentLetNode.setText("letNonRec");
                    } else {                        
                        currentLetNode.setType(CALTreeParserTokenTypes.VIRTUAL_LET_REC);
                        currentLetNode.setText("letRec");                        
                    }                    
                    
                    ParseTreeNode functionDeclarationNode = functionNameToDeclarationNode.get(functionName);                    
                    //we group the function declaration, if one is given, immediately before the function that it is a declaration for
                    if (functionDeclarationNode != null) {
                        functionDeclarationNode.setNextSibling(null); //clear out the previously set next sibling
                        currentLetDefnListNode.addChild(functionDeclarationNode);
                        
                    }
                    ParseTreeNode functionDefinitionNode = functionNameToDefinitionNode.get(functionName);
                    functionDefinitionNode.setNextSibling(null); //clear out the previously set next sibling
                    currentLetDefnListNode.addChild(functionDefinitionNode);
                }
                
                currentLetNode.setFirstChild(currentLetDefnListNode);
                
                if (i < nComponents - 1){                
                    ParseTreeNode newLetLiteralNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_let, "let");
                    currentLetDefnListNode.setNextSibling(newLetLiteralNode);
                    currentLetNode = newLetLiteralNode;
                } else {
                    currentLetDefnListNode.setNextSibling(exprNode);        
                }                   
            }                                  
        }
    }

    /**
     * Constructs a name, based on the pattern-bound variable names, for the synthetic local function for
     * hosting the defining expression of the pattern match declaration, which is to be added to the desugared tree.
     * 
     * @param patternVarNames a Collection of the pattern variable names, in source order.
     * @return a name for the synthetic local function.
     */
    static String makeTempVarNameForDesugaredLocalPatternMatchDecl(Collection<String> patternVarNames) {
        final StringBuilder nameBuffer = new StringBuilder("$pattern");
        for (final String patternVarName : patternVarNames) {
           
            nameBuffer.append('_').append(patternVarName);
        }
        return nameBuffer.toString();
    }
    
    /**
     * Helper function for findFreeVariables. 
     * Look up the data constructor corresponding to the parse tree for a qualified cons which names it.
     * 
     * The main purpose of this method is its side effect.
     * It explicitly sets the module name for unqualified data constructors so that further analysis can assume
     * that all data constructor names are fully qualified. It also reports an error if the data constructor
     * cannot be resolved.
     *
     * Creation date: (8/31/00 2:46:34 PM)
     * @param qualifiedConsNode ParseTreeNode 
     * @return DataConstructor the data constructor that the qualifiedConsNode sub-tree can be resolved to refer to
     *                         or null if an error message is logged .
     */
    DataConstructor resolveDataConsName(ParseTreeNode qualifiedConsNode) {
        return resolveDataConsName(qualifiedConsNode, false);
    }

    /**
     * Helper function for findFreeVariables. 
     * Look up the data constructor corresponding to the parse tree for a qualified cons which names it.
     * 
     * The main purpose of this method is its side effect.
     * It explicitly sets the module name for unqualified data constructors so that further analysis can assume
     * that all data constructor names are fully qualified. It also reports an error if the data constructor
     * cannot be resolved.
     *
     * @param qualifiedConsNode ParseTreeNode 
     * @param suppressErrorMessageLogging whether to suppress the logging of error messages to the CALCompiler instance
     * @return DataConstructor the data constructor that the qualifiedConsNode sub-tree can be resolved to refer to
     *                         or null if an error message is logged .
     */
    private DataConstructor resolveDataConsName(ParseTreeNode qualifiedConsNode, boolean suppressErrorMessageLogging) {

        ParseTreeNode moduleNameNode = qualifiedConsNode.firstChild();
        String maybeModuleName = ModuleNameUtilities.resolveMaybeModuleNameInParseTree(moduleNameNode, currentModuleTypeInfo, compiler, suppressErrorMessageLogging);
        ParseTreeNode dataConsNameNode = moduleNameNode.nextSibling();
        String dataConsName = dataConsNameNode.getText();

        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();

        if (maybeModuleName.length() > 0) {

            //an explicitly qualified data constructor
            
            ModuleName moduleName = ModuleName.make(maybeModuleName);
            QualifiedName qualifiedDataConsName = QualifiedName.make(moduleName, dataConsName);

            if (moduleName.equals(currentModuleName)) {

                //the data constructor must be in the current module
                
                DataConstructor dataCons = currentModuleTypeInfo.getDataConstructor(dataConsName);

                if (dataCons == null) {
                    if (!suppressErrorMessageLogging) {
                        // The data constructor {qualifiedDataConsName} does not exist in {currentModuleName}.
                        compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.DataConstructorDoesNotExistInModule(qualifiedDataConsName, currentModuleName)));
                    }
                    return null;
                }

                checkResolvedDataConsReference(compiler, qualifiedConsNode, dataCons.getName(), suppressErrorMessageLogging);
                
                //Success- the data constructor was found in the current module's environment
                return dataCons;
            }

            //the data constructor must be in an imported module

            ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);

            if (importedModuleTypeInfo == null) {
                if (!suppressErrorMessageLogging) {
                    // The module {moduleName} has not been imported into {currentModuleName}.
                    compiler.logMessage(new CompilerMessage(moduleNameNode, new MessageKind.Error.ModuleHasNotBeenImported(moduleName, currentModuleName)));
                }
                return null;
            }

            DataConstructor dataCons = importedModuleTypeInfo.getDataConstructor(dataConsName);
            if (dataCons == null) {
                if (!suppressErrorMessageLogging) {
                    // The data constructor does not exist.
                    compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.DataConstructorDoesNotExist(qualifiedDataConsName)));
                }
                return null;
                
            } else if (!currentModuleTypeInfo.isEntityVisible(dataCons)) {
                if (!suppressErrorMessageLogging) {
                    // The data constructor {qualifiedDataConsName} is not visible in {currentModuleName}. 
                    //"The data constructor {0} is not visible in module {1}." 
                    compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.DataConstructorNotVisible(dataCons, currentModuleName)));
                }
                return null;
            }
            
            checkResolvedDataConsReference(compiler, qualifiedConsNode, dataCons.getName(), suppressErrorMessageLogging);
            
            //Success- the module is indeed imported and has a visible data constructor of the specified name.
            return dataCons;
        }

        //An unqualified data cons

        if (!firstPass) {
            //names should already have been resolved, and so calling this method is redundant.
            throw new IllegalStateException();
        }

        DataConstructor dataCons = currentModuleTypeInfo.getDataConstructor(dataConsName);     
        if (dataCons != null) {

            checkResolvedDataConsReference(compiler, qualifiedConsNode, dataCons.getName(), suppressErrorMessageLogging);
            
            //The data constructor is defined in the current module            
            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, currentModuleName);
            return dataCons;
        }
        
        //We now know that the data constructor can't be defined within the current module.
        //check if it is a "using dataConstructor" and then patch up the module name.
        
        ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingDataConstructor(dataConsName);
        if (usingModuleName != null) {
            final QualifiedName qualifiedDataConsName = QualifiedName.make(usingModuleName, dataConsName);
            
            checkResolvedDataConsReference(compiler, qualifiedConsNode, qualifiedDataConsName, suppressErrorMessageLogging);
            
            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, usingModuleName);
            //this call is guaranteed to return a data constructor due to earlier static checks on the using clause            
            return currentModuleTypeInfo.getVisibleDataConstructor(qualifiedDataConsName);
        }
        
        if (!suppressErrorMessageLogging) {
            //We now know that the data constructor can't be defined within the current module and is not a "using dataConstructor".
            //Check if it is defined in another module.
            //This will be an error since the user must supply a module qualification, but we
            //can attempt to give a good error message.                  
            
            List<QualifiedName> candidateDataConsNames = new ArrayList<QualifiedName>();
            int nImportedModules = currentModuleTypeInfo.getNImportedModules();
            
            for (int i = 0; i < nImportedModules; ++i) {
                
                DataConstructor candidate = currentModuleTypeInfo.getNthImportedModule(i).getDataConstructor(dataConsName);
                
                if (candidate != null && currentModuleTypeInfo.isEntityVisible(candidate)) {                
                    candidateDataConsNames.add(candidate.getName());
                }
            }
            
            int numCandidates = candidateDataConsNames.size();
            if(numCandidates == 0) {
                // Attempt to use undefined data constructor {dataConsName}.
                compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.AttemptToUseUndefinedDataConstructor(dataConsName)));
                
            } else if(numCandidates == 1) {
                
                QualifiedName candidateDataConsName = candidateDataConsNames.get(0);
                
                // Attempt to use undefined data constructor {dataConsName}. Was {candidateDataConsName} intended?
                compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.AttemptToUseUndefinedDataConstructorSuggestion(dataConsName, candidateDataConsName)));
                
            } else {
                
                // The reference to the data constructor {dataConsName} is ambiguous. It could mean any of {candidateDataConsNames.toString()}.
                compiler.logMessage(new CompilerMessage(dataConsNameNode, new MessageKind.Error.AmbiguousDataConstructorReference(dataConsName, candidateDataConsNames)));
            }
        }
        
        return null;
    }
    
    /**
     * Performs late static checks on a data constructor reference that has already been resolved.
     * 
     * Currently, this performs a deprecation check on a data constructor reference, logging a warning message if the data constructor is deprecated.
     * @param compiler the compiler instance.
     * @param nameNode the parse tree node representing the reference.
     * @param qualifiedName the qualified name of the reference.
     * @param suppressCompilerMessageLogging whether to suppress message logging.
     */
    static void checkResolvedDataConsReference(final CALCompiler compiler, final ParseTreeNode nameNode, final QualifiedName qualifiedName, final boolean suppressCompilerMessageLogging) {
        if (!suppressCompilerMessageLogging) {
            if (compiler.getDeprecationScanner().isDataConsDeprecated(qualifiedName)) {
                compiler.logMessage(new CompilerMessage(nameNode, new MessageKind.Warning.DeprecatedDataCons(qualifiedName)));
            }
        }
    }
    
    /**
     * Adds the given variable to the freeVariablesSet if this is appropriate.
     * As a side effect, if this.firstPass is true, it explicitly sets the module name
     * for unqualified variable (so that further analysis can assume that all variable names are
     * fully qualified) and reports an error if the identifier cannot be resolved.
     *
     * Creation date: (8/31/00 2:46:34 PM)
     * @param freeVariablesSet
     * @param boundVariablesStack 
     * @param qualifiedVarNode ParseTreeNode 
     */
    private void findFreeVariablesInQualifiedVar(Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack, ParseTreeNode qualifiedVarNode) {

        qualifiedVarNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);

        ParseTreeNode moduleNameNode = qualifiedVarNode.firstChild();
        String maybeModuleName = ModuleNameUtilities.resolveMaybeModuleNameInParseTree(moduleNameNode, currentModuleTypeInfo, compiler, false);
        ParseTreeNode varNameNode = moduleNameNode.nextSibling();
        String varName = varNameNode.getText();
        
        ModuleName currentModuleName = currentModuleTypeInfo.getModuleName();

        if (maybeModuleName.length() > 0) {

            //an explicitly qualified variable

            ModuleName moduleName = ModuleName.make(maybeModuleName);
            if (moduleName.equals(currentModuleName)) {

                //variable belongs to the current module
                
                if (!firstPass) {
                    //bound variable, such as the x in "f x = x;" cannot be qualified in CAL source i.e.
                    //f x = CurrentModule.x;
                    //is illegal. However, they are qualified as part of internal analysis with the current module name.
                    
                    BoundVariablesStack.BoundVariable boundVar = boundVariablesStack.contains(varName, true);
                    if (boundVar != null) {
                        //variable is pattern bound (i.e. an argument variable, a lambda bound variable or a variable
                        //bound by a case alternative).                                                
                        return;
                    }
                }

                if (possibleDependeeNamesSet.contains(varName)) {
                    //variable is a top-level non built-in function defined in the current module
                    freeVariablesSet.add(varName);
                    checkResolvedFunctionOrClassMethodReference(compiler, qualifiedVarNode, QualifiedName.make(moduleName, varName), false, currentModuleName);
                    return;
                }

                if (compiler.getTypeChecker().isPrimitiveOrForeignFunctionName(varName)) {
                    //use of a built-in or foreign function in qualified form is OK                    
                    checkResolvedFunctionOrClassMethodReference(compiler, qualifiedVarNode, QualifiedName.make(moduleName, varName), false, currentModuleName);
                    return;
                } 
                
                if (ignorableFunctionNamesSet.contains(varName) ||
                    isHiddenFunction(varName) ||
                    currentModuleTypeInfo.getClassMethod(varName) != null) {
                    //condition 1: used when finding the free variables within a lambda bound expression to do lambda lifting
                    //condition 2: ignore hidden dictionary variables
                    //condition 3: ignore class methods
                    return;
                } 
                    
                if (!firstPass && varName.indexOf('$') != -1) {
                    //ignore the names that have been made unique
                    return;
                }

                QualifiedName qualifiedVarName = QualifiedName.make(moduleName, varName);
                // Attempt to use undefined identifier {qualifiedVarName}.
                compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.AttemptToUseUndefinedIdentifier(qualifiedVarName.getQualifiedName())));

                return;
            }

            //variable belongs to a different module

            if (isHiddenFunction(varName) || !this.firstPass) {
                return;
            }

            ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getImportedModule(moduleName);
            if (importedModuleTypeInfo == null) {
                // The module {moduleName} has not been imported into {currentModuleName}.
                compiler.logMessage(new CompilerMessage(moduleNameNode, new MessageKind.Error.ModuleHasNotBeenImported(moduleName, currentModuleName)));
            }

            FunctionalAgent entity = importedModuleTypeInfo.getFunctionOrClassMethod(varName);
            if (entity == null) {
                QualifiedName qualifiedVarName = QualifiedName.make(moduleName, varName);
                // The identifier {qualifiedVarName} does not exist.
                compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.IdentifierDoesNotExist(qualifiedVarName.getQualifiedName())));
                
            } else if (!currentModuleTypeInfo.isEntityVisible(entity) &&
                    !qualifiedVarNode.isInternallyGenerated() ) {               

                // The identifier {qualifiedVarName} is not visible in {currentModuleName}.
                MessageKind message;
                if (entity instanceof Function) {
                    //"The function {0} is not visible in module {1}."
                    message = new MessageKind.Error.FunctionNotVisible((Function)entity, currentModuleName);
                } else {
                    //"The class method {0} is not visible in module {1}."
                    message = new MessageKind.Error.ClassMethodNotVisible((ClassMethod)entity, currentModuleName);
                }
                compiler.logMessage(new CompilerMessage(varNameNode, message));
            }

            final boolean isClassMethod = (entity instanceof ClassMethod);
            checkResolvedFunctionOrClassMethodReference(compiler, qualifiedVarNode, entity.getName(), isClassMethod, currentModuleName);

            //Success- the module is indeed imported and has a visible variable of the specified name.

            return;
        }

        //An unqualified variable

        if (!firstPass) {
            // Should not encounter an unqualified name at this point.
            throw new IllegalStateException("Should not encounter an unqualified name at this point.");            
        }
        
        //At this point we need to decide if the unqualified variable is
        //a. a top-level function defined in the current module (and thus should be added to the freeVariablesSet)
        //b. a pattern bound variable
        //c. a visible function defined in another module
        //We patch up the parse tree to supply the missing module name.

        BoundVariablesStack.BoundVariable boundVar = boundVariablesStack.contains(varName, true);
        if (boundVar != null) {   

            //variable is pattern bound (i.e. an argument variable, a lambda bound variable or a variable
            //bound by a case alternative). Its module is the current module.

            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, currentModuleName);
            
            //disambiguate the name
            //the local symbol x defined in top level function f will be replaced by
            //f$x$n
            //where n is the nth local symbol defined within f.
            varNameNode.setText(boundVar.getUniqueName());
            
            return;
        }

        if (possibleDependeeNamesSet.contains(varName)) {

            checkResolvedFunctionOrClassMethodReference(compiler, qualifiedVarNode, QualifiedName.make(currentModuleName, varName), false, currentModuleName);

            //variable is a top-level function defined in the current module
            freeVariablesSet.add(varName);
            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, currentModuleName);
            return;
        }

        if (compiler.getTypeChecker().isPrimitiveOrForeignFunctionName(varName) ||
            currentModuleTypeInfo.getClassMethod(varName) != null) {
                              
            //1. use of an unqualified built-in or foreign function
            //2. use of an unqualified class method

            final boolean isClassMethod = (currentModuleTypeInfo.getClassMethod(varName) != null);
            checkResolvedFunctionOrClassMethodReference(compiler, qualifiedVarNode, QualifiedName.make(currentModuleName, varName), isClassMethod, currentModuleName);

            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, currentModuleName);
            return;
        }
        
        //We now know that the variable can't be defined within the current module.
        //check if it is a "using function" and then patch up the module name.
        
        ModuleName usingModuleName = currentModuleTypeInfo.getModuleOfUsingFunctionOrClassMethod(varName);
        if (usingModuleName != null) {
            final boolean isClassMethod = (currentModuleTypeInfo.getImportedModule(usingModuleName).getClassMethod(varName) != null);
            checkResolvedFunctionOrClassMethodReference(compiler, qualifiedVarNode, QualifiedName.make(usingModuleName, varName), isClassMethod, currentModuleName);

            ModuleNameUtilities.setModuleNameIntoParseTree(moduleNameNode, usingModuleName);
            return;
        }
        
        //We now know that the variable can't be defined within the current module and is not a "using function".
        //Check if it is defined in another module.
        //This will be an error since the user must supply a module qualification, but we
        //can attempt to give a good error message.          

        List<QualifiedName> candidateEntityNames = new ArrayList<QualifiedName>();
        int nImportedModules = currentModuleTypeInfo.getNImportedModules();
        for (int i = 0; i < nImportedModules; ++i) {

            FunctionalAgent candidate = currentModuleTypeInfo.getNthImportedModule(i).getFunctionOrClassMethod(varName);
            if (candidate != null && currentModuleTypeInfo.isEntityVisible(candidate)) {
                candidateEntityNames.add(candidate.getName());
            }
        }

        int numCandidates = candidateEntityNames.size();
        if(numCandidates == 0) {
            // Attempt to use undefined function {varName}.
            compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.AttemptToUseUndefinedFunction(varName)));
            
        } else if(numCandidates == 1) {
            
            QualifiedName candidateName = candidateEntityNames.get(0);
            
            // Attempt to use undefined function {varName}. Was {candidateName} intended?
            compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.AttemptToUseUndefinedFunctionSuggestion(varName, candidateName)));                
            
        } else {
            // The reference to the function {varName} is ambiguous. It could mean any of {candidateEntityNames}. 
            compiler.logMessage(new CompilerMessage(varNameNode, new MessageKind.Error.AmbiguousFunctionReference(varName, candidateEntityNames)));
        }
    }
    
    /**
     * Performs late static checks on a function or class method reference that has already been resolved.
     * 
     * Currently, this performs 1) a deprecation check on a function or class method reference, logging a warning message if the function or method is deprecated, and
     * 2) a check that if the reference is to a foreign function, then the corresponding Java method/field/constructor should be resolvable and loadable, logging
     * an error message if it cannot be loaded.
     * @param compiler the compiler instance.
     * @param nameNode the parse tree node representing the reference.
     * @param qualifiedName the qualified name of the reference.
     * @param isClassMethod true if the reference is to a class method, false if it is a function reference.
     * @param currentModuleName the name of the current module.
     */
    private static void checkResolvedFunctionOrClassMethodReference(final CALCompiler compiler, final ParseTreeNode nameNode, final QualifiedName qualifiedName, final boolean isClassMethod, final ModuleName currentModuleName) {
        // we only do the check when the function name is not an internal name
        if (qualifiedName.getUnqualifiedName().indexOf('$') < 0) {
            
            // 1) deprecation check on a function or class method reference, logging a warning message if the function or method is deprecated.
            if (compiler.getDeprecationScanner().isFunctionOrClassMethodDeprecated(qualifiedName)) {
                
                final MessageKind.Warning messageKind;
                if (isClassMethod) {
                    messageKind = new MessageKind.Warning.DeprecatedClassMethod(qualifiedName);
                } else {
                    messageKind = new MessageKind.Warning.DeprecatedFunction(qualifiedName);
                }
                
                compiler.logMessage(new CompilerMessage(nameNode, messageKind));
            }
            
            if (!isClassMethod) {
                
                // 2) check that if the reference is to a foreign function, then the corresponding Java method/field/constructor should be resolvable and loadable.

                // only perform the check on references to entities outside the current module
                if (!qualifiedName.getModuleName().equals(currentModuleName)) {
                    
                    // Check if the function is a foreign function
                    final ModuleTypeInfo moduleTypeInfo = compiler.getPackager().getModuleTypeInfo(qualifiedName.getModuleName());
                    final Function function = moduleTypeInfo.getFunction(qualifiedName.getUnqualifiedName());
                    final ForeignFunctionInfo foreignFunctionInfo = function.getForeignFunctionInfo();
                    
                    if (foreignFunctionInfo != null) {

                        // Force resolution to occur
                        try {
                            foreignFunctionInfo.resolveForeignEntities();

                        } catch (UnableToResolveForeignEntityException e) {
                            compiler.logMessage(new CompilerMessage(
                                nameNode,
                                new MessageKind.Error.AttemptToUseForeignFunctionThatFailedToLoad(qualifiedName.toSourceText()),
                                e));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Initialize the state.
     * Creation date: (8/16/01 12:48:22 PM)
     */
    private void initState(boolean firstPass, Set<String> ignorableSCNamesSet, Collection<String> possibleDependeeNamesSet) {

        this.firstPass = firstPass;
        this.ignorableFunctionNamesSet = ignorableSCNamesSet;
        this.possibleDependeeNamesSet = possibleDependeeNamesSet;
    }
    
    /**
     * Find the free variables occurring within a CALDoc "@link" inline cross-reference.
     * 
     * For the case where the "@link" segment lacks the 'context' keyword, attempt to resolve it as a
     * data constructor and store the result of the resolution as an attribute on the refNode, i.e. linkNode's first grandchild.
     * 
     * @param linkNode the ParseTreeNode representing an inline cross-reference in a CALDoc comment.
     */
    void findFreeVariablesInCALDocInlineLinkTagSegment(ParseTreeNode linkNode) {
                
        initState(true, Collections.<String>emptySet(), topLevelFunctionNamesSet);
        
        // since we're not interested in pushing bound variables onto the stack,
        // we could specify null for the top level function name.
        BoundVariablesStack boundVariablesStack = new BoundVariablesStack(null);
        
        Set<String> freeVariablesSet = new LinkedHashSet<String>();

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
            
            if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR) {
                findFreeVariablesInQualifiedVar(freeVariablesSet, boundVariablesStack, nameNode);
            }
            
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
            
            if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS) {
                resolveDataConsName(nameNode);
            }
            
            break;
        }
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_MODULE:
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECONS:
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_TYPECLASS:
            // these kinds of references are not processed by the FreeVariableFinder.
            break;
        
        case CALTreeParserTokenTypes.CALDOC_TEXT_LINK_WITHOUT_CONTEXT:
        {
            ParseTreeNode refNode = linkContextNode.firstChild();
            findFreeVariablesInCALDocCrossReferenceWithoutContext(refNode, freeVariablesSet, boundVariablesStack);
            break;
        }
            
        default:
        {
            linkContextNode.unexpectedParseTreeNode();
            break;
        }
        }
    }
    
    /*
     * todo-jowong: look into refactoring the following name resolution code. 
     */
    /**
     * Find the free variables occurring within a CALDoc cross-reference that appears without a 'context' keyword.
     * 
     * Such a reference may appear in a CALDoc comment, as in:
     * {at-link Prelude at-} - a module reference without context
     * {at-link Nothing at-} - a data constructor reference without context
     * at-see Prelude.Int - a type constructor reference without context
     * at-see Eq - a type class reference without context
     * 
     * @param refNode the ParseTreeNode representing a cross-reference in a CALDoc comment.
     */
    private void findFreeVariablesInCALDocCrossReferenceWithoutContext(ParseTreeNode refNode, Set<String> freeVariablesSet, BoundVariablesStack boundVariablesStack) {
        
        // The refNode can represent a checked or unchecked qualified var or cons name.
        refNode.verifyType(
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR,
            CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR,
            CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS,
            CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS);
        
        if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR) {
            
            // Proceed regularly if refNode contains a checked qualified var.
            ParseTreeNode nameNode = refNode.firstChild();
            nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
            
            findFreeVariablesInQualifiedVar(freeVariablesSet, boundVariablesStack, nameNode);
            
        } else if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS ||
                   refNode.getType() == CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_CONS) {
            
            // If refNode contains a qualified cons, either checked or unchecked, then it may represent a data constructor
            // (or not.. it could also be a type constructor, a type class, or a module).
            
            // We proceed here by cloning the subtree, and then attempting to resolved the cloned subtree
            // as a data constructor. If the resolution succeeds, then mark the original refNode as
            // "resolvable as a data constructor".
            
            ParseTreeNode nameNode = refNode.firstChild();
            nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_CONS);
            
            ParseTreeNode copyOfNameNode = nameNode.copyParseTree();
            
            // perform the resolution step, which would modify the cloned subtree
            final boolean suppressErrorMessageLogging = true;
            DataConstructor dataCons = resolveDataConsName(copyOfNameNode, suppressErrorMessageLogging);
            
            if (dataCons != null) {
                refNode.setCALDocCrossReferenceResolvedAsDataConstructor(dataCons.getName());
            }
        }
    }
    
    /**
     * Find the free variables occurring within a CALDoc "@see" block.
     * @param seeBlockNode the node representing the "@see" block.
     */
    void findFreeVariablesInCALDocSeeBlock(ParseTreeNode seeBlockNode) {
                
        initState(true, Collections.<String>emptySet(), topLevelFunctionNamesSet);
        
        // since we're not interested in pushing bound variables onto the stack,
        // we could specify null for the top level function name.
        BoundVariablesStack boundVariablesStack = new BoundVariablesStack(null);
        
        Set<String> freeVariablesSet = new LinkedHashSet<String>();

        switch (seeBlockNode.getType()) {
        
            case CALTreeParserTokenTypes.CALDOC_SEE_FUNCTION_BLOCK:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
                    
                    refNode.verifyType(
                        CALTreeParserTokenTypes.CALDOC_UNCHECKED_QUALIFIED_VAR,
                        CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR);
                    
                    ParseTreeNode nameNode = refNode.firstChild();
                    nameNode.verifyType(CALTreeParserTokenTypes.QUALIFIED_VAR);
                    
                    if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_VAR) {
                        findFreeVariablesInQualifiedVar(freeVariablesSet, boundVariablesStack, nameNode);
                    }
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
                    
                    if (refNode.getType() == CALTreeParserTokenTypes.CALDOC_CHECKED_QUALIFIED_CONS) {
                        resolveDataConsName(nameNode);
                    }
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_BLOCK_WITHOUT_CONTEXT:
            {
                for (final ParseTreeNode refNode : seeBlockNode) {
               
                    findFreeVariablesInCALDocCrossReferenceWithoutContext(refNode, freeVariablesSet, boundVariablesStack);
                }
                break;
            }
            
            case CALTreeParserTokenTypes.CALDOC_SEE_MODULE_BLOCK:
            case CALTreeParserTokenTypes.CALDOC_SEE_TYPECONS_BLOCK:
            case CALTreeParserTokenTypes.CALDOC_SEE_TYPECLASS_BLOCK:
                // these kinds of references are not processed by the FreeVariableFinder.
                break;
                
            default:
            {
                seeBlockNode.unexpectedParseTreeNode();
                break;
            }            
        }
    }
    
    /**
     * Returns true if the function is internally created.
     * @return boolean
     * @param functionName name of the function
     */
    private static boolean isHiddenFunction(String functionName) {

        return functionName.length() > 0 && functionName.charAt(0) == '$';
    }  
    
    /**    
     * The FreeVariableFinder makes local names unique by changing a local name such as x occurring in the definition 
     * of f to something like f$x$9. We want to get back to x for the purpose of error messages.  
     * @param uniqueName
     * @return the name without uniqueness markup via $ signs.
     */
    static String getDisplayName(String uniqueName) {
        final int firstDollar = uniqueName.indexOf('$');
        if (firstDollar == -1) {
            return uniqueName;
        }
        
        final int secondDollar = uniqueName.indexOf('$', firstDollar + 1);
        if (secondDollar == -1) {
            return uniqueName;
        }
        
        if (firstDollar + 1 == secondDollar) {
            // if the first and second dollar signs are adjacent, then this is a
            // synthetically generated name...
            return "<internal synthetic variable>";
        }
        
        return uniqueName.substring(firstDollar + 1, secondDollar);
    }
    
    /**
     * Returns whether a uniquified variable name correspond to an internally generated synthetic name.
     * @param uniqueName the uniquified variable name to check.
     * @return true if the uniquified variable name correspond to an internally generated synthetic name.
     */
    static boolean isInternalSyntheticVariable(final String uniqueName) {
        final int firstDollar = uniqueName.indexOf('$');
        if (firstDollar == -1) {
            return false;
        }
        
        final int secondDollar = uniqueName.indexOf('$', firstDollar + 1);
        if (secondDollar == -1) {
            return false;
        }
        
        // if the first and second dollar signs are adjacent, then this is a
        // synthetically generated name...
        return (firstDollar + 1 == secondDollar);
    }
}
