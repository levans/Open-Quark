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
 * LambdaLifter.java
 * Created: Feb 2, 2001
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.util.ArrayStack;

/**
 * This class implements a lambda lifter for CAL. Lambda lifting is the process in which lambda
 * expressions and local function definitions that bind arguments are removed from the
 * program. This is done by rewriting the original expressions using certain new auxiliary
 * global functions. The main reference is Peyton-Jones and Lester, chapter 6.
 * <p>
 * The lambda lifter also (optionally) lifts case expressions. What this means is that every
 * case expression will occur as the first thing on the rhs of a top level function e.g.
 * function x1 ... x2 = case e of ... 
 * This is needed by the g and lecc machines in order to be lazy about evaluating the expression e
 * under certain circumstances.
 * <p>
 *
 * Creation date: (2/2/01 4:40:54 PM)
 * @author Bo Ilic
 */
final class LambdaLifter {

    /** Set to true to have debug info printed while running the lambda lifter. */
    private static final boolean DEBUG_INFO = false;   

    private final CALCompiler compiler;

    /** The module in which the lifted entities belong to */
    private final ModuleName currentModuleName;

    private final FreeVariableFinder freeVariableFinder;
   
    private final List<ParseTreeNode> liftedFunctionList;
    
    /** 
     * information about the local functions, case expressions and lambda
     * expressions that are lifted from a given function definition. Used to give meaningful
     * names to the lifted functions.
     */    
    private LambdaLiftInfo lambdaLiftInfo;

    /** true if you want case expressions to be lifted, otherwise case expressions can occur at any depth. */
    private static final boolean LIFT_CASES = true;

    private static int totalLambdaLiftCount;
    private static int totalLocalFunctionLiftCount;
    private static int totalCaseLiftCount; 
    private static int totalDCSelectionLiftCount; 
       
    /**
     * Constructs a LambdaLifter from a Compiler and a FreeVariableFinder.
     * 
     * @param compiler
     * @param freeVariableFinder
     * @param currentModuleName
     */
    LambdaLifter(CALCompiler compiler, FreeVariableFinder freeVariableFinder, ModuleName currentModuleName) {
       
        if (compiler == null || freeVariableFinder == null || currentModuleName == null) {
            throw new NullPointerException();
        }
       
        this.compiler = compiler;
        this.freeVariableFinder = freeVariableFinder;
        this.currentModuleName = currentModuleName;
     
        liftedFunctionList = new ArrayList<ParseTreeNode>();
    }
    
    /**
     * A helper function that creates a new global function.
     * If freeVariablesSet = y1 ... ym, varListNode = x1 ... xn then the top-level function
     * functionName y1 ... ym x1 ... xn = rhsExpr
     * is created. 
     *
     * Creation date: (2/7/01 5:50:24 PM)
     * @param functionName name of the new function. Should start with a $.
     * @param freeVariablesSet the variables y1, ... ym
     * @param paramListNode holds the variables x1,...,xn
     */
    private void addLiftedFunction(String functionName, Set<String> freeVariablesSet, ParseTreeNode paramListNode) {
        
        ParseTreeNode liftedSCNode = new ParseTreeNode(CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN, "TOP_LEVEL_FUNCTION_DEFN");

        ParseTreeNode optionalCALDocNode = new ParseTreeNode(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT, "OPTIONAL_CALDOC_COMMENT");
        liftedSCNode.setFirstChild(optionalCALDocNode);

        ParseTreeNode accessModifierNode = new ParseTreeNode(CALTreeParserTokenTypes.ACCESS_MODIFIER, "ACCESS_MODIFIER");
        optionalCALDocNode.setNextSibling(accessModifierNode);

        ParseTreeNode privateNode = new ParseTreeNode(CALTreeParserTokenTypes.LITERAL_private, "private");
        accessModifierNode.setFirstChild(privateNode);

        ParseTreeNode liftedSCNameNode = new ParseTreeNode(CALTreeParserTokenTypes.VAR_ID, functionName);
        accessModifierNode.setNextSibling(liftedSCNameNode);

        ParseTreeNode liftedParamListNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST");
        liftedSCNameNode.setNextSibling(liftedParamListNode);
            // This can contain the type expression (for lambda and case expressions) so copy it over.
        liftedParamListNode.setTypeExprForFunctionParamList(paramListNode.getTypeExprForFunctionParamList());

        ParseTreeNode rhsExprNode = paramListNode.nextSibling();
        liftedParamListNode.setNextSibling(rhsExprNode);

        //Note: the logic could be simplified by using addChild instead of setFirstChild and setNextSibling.
        //However, then this algorithm would be O(n2) in the number of children instead of O(n). 

        ParseTreeNode previousVarNode = null;
        Iterator<String> it = freeVariablesSet.iterator();
        if (it.hasNext()) {

            ParseTreeNode freeVarNode = getArgVarNode(it.next());
            freeVarNode.setIsLiftedArgument(true);
            liftedParamListNode.setFirstChild(freeVarNode);
            previousVarNode = freeVarNode;

            while (it.hasNext()) {
                freeVarNode = getArgVarNode(it.next());
                freeVarNode.setIsLiftedArgument(true);
                previousVarNode.setNextSibling(freeVarNode);
                previousVarNode = freeVarNode;
            }
        }

        ParseTreeNode varNode = paramListNode.firstChild();
        if (varNode != null) {

            if (previousVarNode != null) {
                previousVarNode.setNextSibling(varNode);
            } else {
                liftedParamListNode.setFirstChild(varNode);
            }

            previousVarNode = varNode;

            for (varNode = varNode.nextSibling(); varNode != null; varNode = varNode.nextSibling()) {

                previousVarNode.setNextSibling(varNode);
                previousVarNode = varNode;
            }
        }

        liftedFunctionList.add(liftedSCNode);
    }
    
    /**
     * If a function, lambda or case is lifted, any free variables in its defining expression become
     * argument variables of the lifted function. If these free variables are known to be evaluated
     * to weak-head normal form, then they can be added as plinged arguments to the lifted function.
     * 
     * This is a run-time performance optimization. For example, the lifted argument could represent the
     * length of an array (:: Int) and the lifted function could be a tail-recursive local function. In that
     * case the length argument will be passed to successive recursive calls as a primitive "int".  
     *    
     * @param varName
     * @return ParseTreeNode parameter node for the lifted function.
     */
    private ParseTreeNode getArgVarNode (String varName) {
        int tokenType;
        if (compiler.getTypeChecker().isEvaluatedLocalVariable(varName)) {
            tokenType = CALTreeParserTokenTypes.STRICT_PARAM;
        } else {
            tokenType = CALTreeParserTokenTypes.LAZY_PARAM;
        }
        
        return new ParseTreeNode(tokenType, varName);
    }
    
    /**
     * The entry method for actually doing the lambda lifting. This method assumes that
     * the program has been successfully type checked. It adds the newly lifted functions
     * as children of outerDefnListNode. 
     * Creation date: (2/2/01 4:58:46 PM)
     * @param outerDefnListNode root parse tree node of the top level definitions
     */
    void lift(ParseTreeNode outerDefnListNode) {

        outerDefnListNode.verifyType(CALTreeParserTokenTypes.OUTER_DEFN_LIST);

        if (outerDefnListNode.firstChild() == null) {
            return;
        }

        liftedFunctionList.clear();
        lambdaLiftInfo = null;           
        ArrayStack<String> boundVariablesStack = ArrayStack.make();
        
        int lambdaLiftCount = 0;
        int localSCLiftCount = 0;
        int caseLiftCount = 0;
        int dcSelectionLiftCount = 0;

        ParseTreeNode previousSibling = null;
        for (final ParseTreeNode parseTree : outerDefnListNode) {

            if (parseTree.getType() == CALTreeParserTokenTypes.TOP_LEVEL_FUNCTION_DEFN) {                
                if (!boundVariablesStack.isEmpty()) {
                    throw new IllegalStateException("LambdaLifter: Programming error. Non empty bound variables stack.");
                }
                    
                ParseTreeNode functionNameNode = parseTree.getChild(2);
                functionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                lambdaLiftInfo = new LambdaLiftInfo (functionNameNode.getText());

                ParseTreeNode varListNode = functionNameNode.nextSibling();
                //we do not need to lift cases that occur immediately after a top level function definition
                //e.g. f xs = case e of ... 
                //     f xs = let ... in case e of                
                final boolean caseNeedsLifting = false;
                liftExpressionPartOfBoundExpr(boundVariablesStack, varListNode, caseNeedsLifting);
                
                if (DEBUG_INFO) {
                    lambdaLiftCount += lambdaLiftInfo.getLambdaLiftCount();
                    localSCLiftCount += lambdaLiftInfo.getLocalFunctionLiftCount();
                    caseLiftCount += lambdaLiftInfo.getCaseLiftCount();
                    dcSelectionLiftCount += lambdaLiftInfo.getDCSelectionLiftCount();
                }
                
                lambdaLiftInfo = null;
            }

            previousSibling = parseTree;
        }

        //Now add the newly created lifted functions to the original program parse tree.

        int nLifts = liftedFunctionList.size();
        
        if (DEBUG_INFO) {
            LambdaLifter.totalLambdaLiftCount += lambdaLiftCount;
            LambdaLifter.totalLocalFunctionLiftCount += localSCLiftCount;
            LambdaLifter.totalCaseLiftCount += caseLiftCount;    
            LambdaLifter.totalDCSelectionLiftCount += dcSelectionLiftCount;    
             
            System.out.println("total number of lifts for module " + currentModuleName + " = " + nLifts);
            
            System.out.println("\tnumber of lambdas lifted = " + lambdaLiftCount);
            System.out.println("\tnumber of local functions lifted = " + localSCLiftCount);
            System.out.println("\tnumber of case expressions lifted = " + caseLiftCount);
            System.out.println("\tnumber of data cons field selections lifted = " + dcSelectionLiftCount);
            
            System.out.println("\ttotal of lambda definitions lifted = " + LambdaLifter.totalLambdaLiftCount); 
            System.out.println("\ttotal of local functions lifted = " + LambdaLifter.totalLocalFunctionLiftCount);                   
            System.out.println("\ttotal of case expressions lifted = " + LambdaLifter.totalCaseLiftCount); 
            System.out.println("\ttotal of data cons field selections lifted = " + LambdaLifter.totalDCSelectionLiftCount); 
            System.out.println("");
        }
                
        for (int i = 0; i < nLifts; ++i) {

            ParseTreeNode nextSibling = liftedFunctionList.get(i);

//            if (DEBUG_INFO) {
//                System.out.println(nextSibling.toStringTree());
//                System.out.println("");
//            }

            previousSibling.setNextSibling(nextSibling);
            previousSibling = nextSibling;
        }
    }
    
    /**
     * Lifts the lambda expressions occurring within this expression.
     *
     * Creation date: (2/8/01 10:36:13 AM)
     * @param boundVariablesStack ArrayStack of all variable names that are visible at this point of
     *        parseTree, excluding top-level function names. In other words, this stack may contain the names
     *      of local or top-level function argument variables, local function names or binder variables in a lambda
     *      declaration. The same variable name can occur more than once because of scoping.
     * @param parseTree expression parse tree
     * @param caseNeedsLifting
     */
    private void liftExpr(ArrayStack<String> boundVariablesStack, ParseTreeNode parseTree, boolean caseNeedsLifting) {

        int nodeType = parseTree.getType();

        switch (nodeType) {

            case CALTreeParserTokenTypes.VIRTUAL_LET_NONREC:
            case CALTreeParserTokenTypes.VIRTUAL_LET_REC:    
            {
                liftLetExprJohnssonStyle(boundVariablesStack, parseTree, caseNeedsLifting);               
                break;
            }

            case CALTreeParserTokenTypes.LAMBDA_DEFN :
                liftLambdaExpr (boundVariablesStack, parseTree);
                break;

            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD: 
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                liftExpr(boundVariablesStack, exprNode, true);
                break;
            }
                
            case CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE :
            case CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE:
            case CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE:
                liftCaseExpr (boundVariablesStack, parseTree, caseNeedsLifting);
                break;           

            case CALTreeParserTokenTypes.LITERAL_if :
            {
                //we do not need to lift cases appearing as the top-level expressions in the true and false parts
                //of an if-then-else if the if-then-else occurs in a context where if instead it were a case, then
                //it would not need lifting.
                
                ParseTreeNode condExprNode = parseTree.firstChild();
                liftExpr(boundVariablesStack, condExprNode, true);
                
                ParseTreeNode ifTrueNode = condExprNode.nextSibling();
                liftExpr(boundVariablesStack, ifTrueNode, caseNeedsLifting);
                
                ParseTreeNode ifFalseNode = ifTrueNode.nextSibling();
                liftExpr(boundVariablesStack, ifFalseNode, caseNeedsLifting);
                               
                return;
            }
                        
            case CALTreeParserTokenTypes.APPLICATION :
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
            {
                // Application:
                // Data constructor field selection is parsed as an application, the first child being of type SELECT_DC.._FIELD.
                // There may or may not be any arguments.  If none, we can just go right to the selection.
                
                // Tuple constructor:
                // In the case of one child, we are really dealing with a parenthesized expression.
               
                if (parseTree.hasExactlyOneChild()) {
                    liftExpr(boundVariablesStack, parseTree.firstChild(), caseNeedsLifting);
                    return;
                    
                } else {
                    // Application: an application of an expr to zero arguments.
                    // Tuple: really a parenthesized expression.
                    
                    // Fall through.
                }
            }
            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
            {
                for (final ParseTreeNode exprNode : parseTree) {

                    liftExpr(boundVariablesStack, exprNode, true);
                }

                return;
            }

            //function names and variables
            case CALTreeParserTokenTypes.QUALIFIED_VAR :
            //data constructors
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            //literals
            case CALTreeParserTokenTypes.INTEGER_LITERAL :
            case CALTreeParserTokenTypes.FLOAT_LITERAL :
            case CALTreeParserTokenTypes.CHAR_LITERAL :
            case CALTreeParserTokenTypes.STRING_LITERAL :
                return;
                
            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR: 
            {
                ParseTreeNode baseRecordNode = parseTree.firstChild();
                baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);
                
                ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();
                if (baseRecordExprNode != null) {
                    liftExpr(boundVariablesStack, baseRecordExprNode, true);
                }
                
                ParseTreeNode fieldModificationListNode = baseRecordNode.nextSibling();
                fieldModificationListNode.verifyType(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST);
                
                for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
                         
                    fieldModificationNode.verifyType(CALTreeParserTokenTypes.FIELD_EXTENSION,
                        CALTreeParserTokenTypes.FIELD_VALUE_UPDATE);
                    
                    ParseTreeNode valueExprNode = fieldModificationNode.getChild(1);
                    liftExpr(boundVariablesStack, valueExprNode, true);                      
                }   
                                           
                return;
            }
                              
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD:
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                liftExpr(boundVariablesStack, exprNode, true);
                return;
            }
            
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:
            {
                //in the definition
                //x = (case ...) :: Int
                //the case does not need lifting since the type signature does not have an
                //operation effect in the runtime.
                ParseTreeNode exprNode = parseTree.firstChild();
                liftExpr(boundVariablesStack, exprNode, caseNeedsLifting);
                return;
            }            

            //these operators should be replaced by their functional forms by this point.
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
            default :
            {
                parseTree.unexpectedParseTreeNode();                          
                return;
            }
        }
    }
    
    
    /**
     * Lifts (let f1 x1 = e1; f2 x2 = e2; ... fn xn = en) in e.
     * The reason this is needed is that most machines cannot handle having arguments to
     * local functions.
     * <p>
     * The details of Johnsson-style lambda lifting are described in Peyton Jones and Lester's book
     * on Implementing Functional Programming Languages pg.239.
     * <p>
     * Johnsson style lambda lifting is a special way of lifting local functions so that:
     * <ol>
     *  <li> in the case of tail-recursive local functions, the lifted functions are indeed
     *       tail recursive. This lets the runtime take advantage of the tail recursion optimization
     *       which implements a tail recursive call as a goto.
     *  <li> even for non-recursive local function definitions, the call to the lifted function in the 
     *       "in" part of the let is a direct call to a named function, which is more efficient
     *       in lecc.
     * </ol>
     * 
     * @param boundVariablesStack 
     * @param letExprNode
     */    
    private void liftLetExprJohnssonStyle(ArrayStack<String> boundVariablesStack, ParseTreeNode letExprNode, boolean caseNeedsLifting) { 
        
        //Here is an example of Johnsson style lambda lifting showing the main steps.
        //Note for all this to work without causing naming clashes we assume that all names are globally distinct.
        //Otherwise the lifted functions can clash with top-level names, and more subtly, added argument variables
        //can clash with existing argument variable names.    
        //
        //1. find the free variables in the let block excluding the functions to be lifted. These are x, y and k below. 
        //2. add these free variables as extra arguments to each of the functions defined in the let
        //   block. For example, we add to g and h but not to k, since k has 0 arguments and will not be lifted.
        //3. replace all occurences of the lifted functions in the 'in' part of the let, and in each of the defining
        //   expressions (including for the 0 argument bindings like k) with the argument augmented versions
        //   e.g. g is replaced by (g x y k).
        //4. remove the functions to be lifted to the top level. This may get rid of the let block entirely if there
        //   are no 0 argument assignments in the block.
        //
        //f x y =
        //    let
        //        g p = h p + x;
        //        h q = k + y + q;
        //        k = g y;
        //    in
        //        g 4.0;
        // 
        //after Johnsson style lambda lifting this is:
        //
        //f x y =
        //    let
        //        k = g x y k y;
        //    in
        //        g x y k 4.0;      
        //
        //g x y k p = h x y k p + x;
        //h x y k q = k + y + q;
                
        letExprNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_LET_NONREC, CALTreeParserTokenTypes.VIRTUAL_LET_REC);          
        
        //In the expression
        //let f1 xs1 = e1; f2 xs2 = e2; ... in e
        //First add those fi that have zero arity to the bound variables stack.       

        ParseTreeNode defnListNode = letExprNode.firstChild();
        defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST);

        int nLocalZeroAritySCs = 0;            
        
        for (final ParseTreeNode defnNode : defnListNode) {
                 
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN &&
                !hasPositiveArity(defnNode)) {           

                ++nLocalZeroAritySCs;
                  
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                localFunctionNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
    
                // functionName is now a bound variable for the other declarations in the 'let' and for the expression
                // following the 'in'.
                boundVariablesStack.push(localFunctionNameNode.getText());
            }
        }
        
        //Iterate over all the local functions (zero and positive arity) finding the free variables in
        //their defining expressions.

        Set<String> freeVariablesSet = new LinkedHashSet<String>();
        
        for (final ParseTreeNode defnNode : defnListNode) {
                 
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) { 
                
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                ParseTreeNode varListNode = localFunctionNameNode.nextSibling();                                
                freeVariablesSet.addAll(freeVariableFinder.findFreeNamesInLambdaExpr(boundVariablesStack, varListNode));               
            }
        }
        
        //Augment the arguments of the positive arity functions by the free variables set, and add to the list of lifted functions.
        
        freeVariablesSet = Collections.unmodifiableSet(freeVariablesSet); //for safety at this point...
        Set<String> positiveArityFunctionNamesSet = new LinkedHashSet<String>();
        
        for (final ParseTreeNode defnNode : defnListNode) {

            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN && 
                hasPositiveArity (defnNode)) {

                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localSCNameNode = optionalCALDocNode.nextSibling();
                localSCNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID);
                String localSCName = localSCNameNode.getText();
                
                ParseTreeNode varListNode = localSCNameNode.nextSibling();
                
                addLiftedFunction (localSCName, freeVariablesSet, varListNode);
                
                positiveArityFunctionNamesSet.add(localSCName);
            }
        }
        
        final int nFunctionsToLift = positiveArityFunctionNamesSet.size();       
        
        if (nFunctionsToLift > 0) {
            
            //Update the applications occurring in the defining expressions
            //e.g. f x y = ... f ... with free variables u v , then this stage will replace the ... f ... with ... (f u v) ...          
            //we don't need to do this for non-recursive lets, since the lifted functions are guaranteed not to occur
            //in their defining expressions by definition of being a non-recursive let!
            
            if (letExprNode.getType() == CALTreeParserTokenTypes.VIRTUAL_LET_REC) {
                            
                for (final ParseTreeNode defnNode : defnListNode) {
        
                    if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                        
                        ParseTreeNode definingExpressionNode = defnNode.getChild(3);
                        updateApplications(positiveArityFunctionNamesSet, freeVariablesSet, definingExpressionNode);                       
                    }
                } 
            }
            
            //update the applications occurring in the 'in' part of the let.                      
            
            ParseTreeNode inPartNode = letExprNode.getChild(1);
            updateApplications(positiveArityFunctionNamesSet, freeVariablesSet, inPartNode);
        }
        
        //now recursively lift within the defining expressions of each local function
        //an interesting point is whether we should recursively lift *before* or *after* doing the lift
        //of the current set of bindings (we are doing after). I think both work, but after is more consistent
        //with the Johnsson style in that a lifted local function used in a some inner scope will always refer
        //to the top-level lifted function, and never a variable passed as an argument.
        
        for (final ParseTreeNode defnNode : defnListNode) {

            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                                               
                ParseTreeNode optionalCALDocNode = defnNode.firstChild();
                optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
                
                ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
                ParseTreeNode varListNode = localFunctionNameNode.nextSibling();
                //we do not need to lift a case that appears immediately after a local function that is itself lifted.
                liftExpressionPartOfBoundExpr(boundVariablesStack, varListNode, !hasPositiveArity(defnNode));                               
            }
        } 
        
        //If "let f1 xs1 = e1; f2 xs2 = e2; ... in e", now lift any lambdas or local function definitions in e.  
        ParseTreeNode inPartNode = letExprNode.getChild(1);
        liftExpr(boundVariablesStack, inPartNode, caseNeedsLifting);   
       
        boundVariablesStack.popN(nLocalZeroAritySCs);
        
        //finally remove the lifted functions from the let defn list. If everything is removed, so there is 
        //effectively zero local bindings defined, then just replace with an application node.
        
        if (nFunctionsToLift == 0) {
            return;
        }
        
        if (nLocalZeroAritySCs == 0) {
            //lift everything so that "let f1 xs1 = e1; f2 xs2 = e2; ... in e" is 
            //changed to "e".            
            letExprNode.setType(CALTreeParserTokenTypes.APPLICATION);
            letExprNode.setText("@");
            letExprNode.setFirstChild(inPartNode);
            return;
        }
        
        List<ParseTreeNode> defnNodesNotToLift = new ArrayList<ParseTreeNode>();
        
        for (final ParseTreeNode defnNode : defnListNode) {

            //drop the type declaration nodes (not needed anymore) and the positive arity functions
            if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN && !hasPositiveArity(defnNode)) {               
                defnNodesNotToLift.add(defnNode);                                                                           
            }
        } 
        
        defnListNode.removeChildren();
        ParseTreeNode[] remainingDefnNodes = defnNodesNotToLift.toArray(new ParseTreeNode[]{});
        remainingDefnNodes[remainingDefnNodes.length - 1].setNextSibling(null);
        defnListNode.addChildren(remainingDefnNodes);        
    }
    
    /**     
     * @param liftedFunctionsSet (String Set) names of the functions that need extra arguments added to them when 
     *      used in an expression
     * @param extraArgumentsSet (String Set) the arguments that need to be added to each of the function applications
     *      involving a function from the liftedFunctionsSet
     * @param parseTree
     */
    private void updateApplications(Set<String> liftedFunctionsSet, Set<String> extraArgumentsSet, ParseTreeNode parseTree) {
        
        int nodeType = parseTree.getType();

        switch (nodeType) {

            case CALTreeParserTokenTypes.VIRTUAL_LET_NONREC:
            case CALTreeParserTokenTypes.VIRTUAL_LET_REC:
            {
                ParseTreeNode defnListNode = parseTree.firstChild();
                defnListNode.verifyType(CALTreeParserTokenTypes.LET_DEFN_LIST); 
        
                for (final ParseTreeNode defnNode : defnListNode) {
                    
                    if (defnNode.getType() == CALTreeParserTokenTypes.LET_DEFN) {
                    
                        ParseTreeNode definingExpressionNode = defnNode.getChild(3);
                        updateApplications(liftedFunctionsSet, extraArgumentsSet, definingExpressionNode); 
                    }
                }
                
                ParseTreeNode inPartNode = parseTree.getChild(1);
                updateApplications(liftedFunctionsSet, extraArgumentsSet, inPartNode);  
                return;
            }

            case CALTreeParserTokenTypes.LAMBDA_DEFN :
                updateApplications(liftedFunctionsSet, extraArgumentsSet, parseTree.getChild(1));
                return;

            case CALTreeParserTokenTypes.SELECT_DATA_CONSTRUCTOR_FIELD :
            {                
                ParseTreeNode exprNode = parseTree.firstChild();
                updateApplications(liftedFunctionsSet, extraArgumentsSet, exprNode);
                break;
            }   
            case CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE :
            case CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE: 
            case CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE:
            {                   
                ParseTreeNode exprNode = parseTree.firstChild();
                updateApplications(liftedFunctionsSet, extraArgumentsSet, exprNode);      
        
                ParseTreeNode altListNode = exprNode.nextSibling();
                altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);                           
        
                for (final ParseTreeNode altNode : altListNode) {
        
                    altNode.verifyType(CALTreeParserTokenTypes.ALT);        
                    ParseTreeNode patternNode = altNode.firstChild();
                    
                    ParseTreeNode rhsExprNode = patternNode.nextSibling();
                    
                    switch (patternNode.getType()) {
                        
                        case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR:
                        case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR:
                        case CALTreeParserTokenTypes.VIRTUAL_UNIT_DATA_CONSTRUCTOR:
                        case CALTreeParserTokenTypes.LIST_CONSTRUCTOR:
                        case CALTreeParserTokenTypes.INT_PATTERN:
                        case CALTreeParserTokenTypes.CHAR_PATTERN:
                        case CALTreeParserTokenTypes.COLON:
                        case CALTreeParserTokenTypes.UNDERSCORE: 
                        case CALTreeParserTokenTypes.RECORD_PATTERN:                            
                        {                                            
                            updateApplications(liftedFunctionsSet, extraArgumentsSet, rhsExprNode);                           
                            break;
                        }                                               
                        
                        default:
                        {                
                            patternNode.unexpectedParseTreeNode();                   
                            return;
                        }                                   
                    }                     
                }
                               
                return;
            }

            case CALTreeParserTokenTypes.LITERAL_if :
            {
                
                ParseTreeNode condExprNode = parseTree.firstChild();
                updateApplications(liftedFunctionsSet, extraArgumentsSet, condExprNode); 
                
                ParseTreeNode ifTrueNode = condExprNode.nextSibling();
                updateApplications(liftedFunctionsSet, extraArgumentsSet, ifTrueNode); 
                
                ParseTreeNode ifFalseNode = ifTrueNode.nextSibling();
                updateApplications(liftedFunctionsSet, extraArgumentsSet, ifFalseNode); 
                               
                return;
            }
                        
            case CALTreeParserTokenTypes.APPLICATION :
            case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR :
            case CALTreeParserTokenTypes.LIST_CONSTRUCTOR :
            {
                for (final ParseTreeNode exprNode : parseTree) {

                    updateApplications(liftedFunctionsSet, extraArgumentsSet, exprNode); 
                }

                return;
            }
            
            case CALTreeParserTokenTypes.QUALIFIED_VAR :                               
            {
                ParseTreeNode moduleNameNode = parseTree.firstChild();                
                ModuleName moduleName = ModuleNameUtilities.getModuleNameFromParseTree(moduleNameNode);
                
                if (!currentModuleName.equals(moduleName)) {
                    //local symbols will all belong to the current module
                    return;
                }

                ParseTreeNode nameNode = moduleNameNode.nextSibling();
                String functionName = nameNode.getText();                              
                
                if (liftedFunctionsSet.contains(functionName)) {
                    
                    //add arguments to this function application
                    
                    parseTree.setType(CALTreeParserTokenTypes.APPLICATION);
                    parseTree.setText("@");
                    parseTree.setFirstChild(ParseTreeNode.makeQualifiedVarNode(moduleName, functionName, null));                    
                    
                    for (final String extraArgName : extraArgumentsSet) {
                                              
                        parseTree.addChild(ParseTreeNode.makeQualifiedVarNode(moduleName, extraArgName, null));
                    }                   
                }
                
                return;
            }
            
            //data constructors
            case CALTreeParserTokenTypes.QUALIFIED_CONS :
            //literals
            case CALTreeParserTokenTypes.INTEGER_LITERAL :
            case CALTreeParserTokenTypes.FLOAT_LITERAL :
            case CALTreeParserTokenTypes.CHAR_LITERAL :
            case CALTreeParserTokenTypes.STRING_LITERAL :
                return;
                
            case CALTreeParserTokenTypes.RECORD_CONSTRUCTOR: 
            {
                ParseTreeNode baseRecordNode = parseTree.firstChild();
                baseRecordNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD);
                
                ParseTreeNode baseRecordExprNode = baseRecordNode.firstChild();
                if (baseRecordExprNode != null) {
                    updateApplications(liftedFunctionsSet, extraArgumentsSet, baseRecordExprNode);
                }
                
                ParseTreeNode fieldModificationListNode = baseRecordNode.nextSibling();
                fieldModificationListNode.verifyType(CALTreeParserTokenTypes.FIELD_MODIFICATION_LIST);
                
                for (final ParseTreeNode fieldModificationNode : fieldModificationListNode) {
                         
                    fieldModificationNode.verifyType(CALTreeParserTokenTypes.FIELD_EXTENSION,
                        CALTreeParserTokenTypes.FIELD_VALUE_UPDATE);
                    
                    ParseTreeNode valueExprNode = fieldModificationNode.getChild(1);
                    updateApplications(liftedFunctionsSet, extraArgumentsSet, valueExprNode);                      
                }   
                                           
                return;
            }
            
            case CALTreeParserTokenTypes.SELECT_RECORD_FIELD:
            case CALTreeParserTokenTypes.EXPRESSION_TYPE_SIGNATURE:
            {
                ParseTreeNode exprNode = parseTree.firstChild();
                updateApplications(liftedFunctionsSet, extraArgumentsSet, exprNode);
                return;
            }

            //these operators should be replaced by their functional forms by this point.
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
            default :
            {
                parseTree.unexpectedParseTreeNode();                          
                return;
            }
        }
    }            
    
    /**     
     * @param defnNode
     * @return true if defn node is a local function of positive arity (and thus will be lifted).
     */
    private static boolean hasPositiveArity(ParseTreeNode defnNode) {
        defnNode.verifyType(CALTreeParserTokenTypes.LET_DEFN);
        ParseTreeNode optionalCALDocNode = defnNode.firstChild();
        optionalCALDocNode.verifyType(CALTreeParserTokenTypes.OPTIONAL_CALDOC_COMMENT);
        
        ParseTreeNode localFunctionNameNode = optionalCALDocNode.nextSibling();
        ParseTreeNode paramListNode = localFunctionNameNode.nextSibling();
        return paramListNode.firstChild() != null;
    }       
    
    /**
     * Lifts \x1 ... xn -> e.
     * The reason this is needed is that most machines cannot lambda expressions 
     * directly. Instead, these must be converted to functions.    
     *    
     * @param boundVariablesStack 
     * @param lambdaExprNode
     */     
    private void liftLambdaExpr(ArrayStack<String> boundVariablesStack, ParseTreeNode lambdaExprNode) {
        
        lambdaExprNode.verifyType(CALTreeParserTokenTypes.LAMBDA_DEFN);
   
        ParseTreeNode varListNode = lambdaExprNode.firstChild();

        //If \xs -> e, then first lift as needed in e.
        //we do not need to lift a case in the situations:
        // \xs -> case e of
        // \xs -> let ... in case e of
        //this is because the lambda will be lifted, and so the case will find itself as a top level
        //case in the lifted lambda function.      
        final boolean caseNeedsLifting = false;      
        liftExpressionPartOfBoundExpr(boundVariablesStack, varListNode, caseNeedsLifting);

        //find the free variables on which the lambda depends. They will be a subset of
        //the names in boundVariablesStack.
        Set<String> freeVariablesSet = freeVariableFinder.findFreeNamesInLambdaExpr(boundVariablesStack, varListNode);

        //If the lambda expression is: \x1 ... xn -> e and it depends on free variables y1 ... ym,
        //we create a new global function
        //$lambda y1 ... ym x1 ... xn = e

        String lambdaSCName = lambdaLiftInfo.getNextLiftedLambdaName();
     
        addLiftedFunction(lambdaSCName, freeVariablesSet, varListNode);

        //Now replace the lambda in the parseTree by the application node
        //$lambda y1 ... ym

        lambdaExprNode.removeChildren();
        lambdaExprNode.initialize(CALTreeParserTokenTypes.APPLICATION, "APPLICATION");
        makeApplication(lambdaExprNode, lambdaSCName, freeVariablesSet);
    }
    
    /**
     * Lifts case e of ... so that the case is the immediate right hand side of 
     * a function.
     * The reason this is needed is that most machines cannot evaluate the expression 'e' lazily without
     * this change. Conceptually, it is because there is no place to store the unevaluated e.  
     * 
     * If the case expression is a data constructor unpacking expression using field name-based argument extraction
     * (ie. matching notation), this is converted to positional notation.  This is so that later analysis can assume 
     * that matching notation does not exist.
     * 
     * Also, case alt patterns for pattern groups are converted to the corresponding series of pattern constructors.
     *    
     * @param boundVariablesStack 
     * @param caseExprNode
     * @param caseNeedsLifting
     */             
    private void liftCaseExpr(ArrayStack<String> boundVariablesStack, ParseTreeNode caseExprNode, boolean caseNeedsLifting) {
        
        caseExprNode.verifyType(CALTreeParserTokenTypes.VIRTUAL_DATA_CONSTRUCTOR_CASE,
            CALTreeParserTokenTypes.VIRTUAL_RECORD_CASE, 
            CALTreeParserTokenTypes.VIRTUAL_TUPLE_CASE);

        ParseTreeNode exprNode = caseExprNode.firstChild();
        liftExpr(boundVariablesStack, exprNode, true);

        ParseTreeNode altListNode = exprNode.nextSibling();
        altListNode.verifyType(CALTreeParserTokenTypes.ALT_LIST);
        
        //we do not need to lift a case that occurs immediately after a pattern
        //e.g. case e of Just x -> case ..., then the second case does not need lifting.
        final boolean resultExprCaseNeedsLifting = false;

        for (final ParseTreeNode altNode : altListNode) {

            altNode.verifyType(CALTreeParserTokenTypes.ALT);

            ParseTreeNode patternNode = altNode.firstChild();
            
            switch (patternNode.getType()) {
                
                case CALTreeParserTokenTypes.PATTERN_CONSTRUCTOR:
                {
                    ParseTreeNode dcArgBindingsNode = patternNode.getChild(1);
                    ParseTreeNode boundExprNode = patternNode.nextSibling();                    
                    
                    switch (dcArgBindingsNode.getType()) {
                        case CALTreeParserTokenTypes.PATTERN_VAR_LIST: 
                        {
                            // Positional notation.
                            liftExpressionPartOfBoundExpr (boundVariablesStack, dcArgBindingsNode, boundExprNode, resultExprCaseNeedsLifting);
                            break;
                        }
                        case CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST:
                        {                            
                            // Matching notation.
                            int nVars = pushVarNamesFromBindings(boundVariablesStack, dcArgBindingsNode);    
                            liftExpr(boundVariablesStack, boundExprNode, caseNeedsLifting);
                            boundVariablesStack.popN(nVars);
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
                
                case CALTreeParserTokenTypes.TUPLE_CONSTRUCTOR:
                case CALTreeParserTokenTypes.VIRTUAL_UNIT_DATA_CONSTRUCTOR:
                case CALTreeParserTokenTypes.LIST_CONSTRUCTOR:
                case CALTreeParserTokenTypes.COLON:
                case CALTreeParserTokenTypes.UNDERSCORE:                    
                    liftExpressionPartOfBoundExpr (boundVariablesStack, patternNode, resultExprCaseNeedsLifting);
                    break;
                    
                case CALTreeParserTokenTypes.INT_PATTERN:
                case CALTreeParserTokenTypes.CHAR_PATTERN:
                {
                    ParseTreeNode boundExprNode = patternNode.nextSibling();
                    liftExpr(boundVariablesStack, boundExprNode, caseNeedsLifting);
                    break;
                }   
                case CALTreeParserTokenTypes.RECORD_PATTERN:
                {                    
                    ParseTreeNode baseRecordPatternNode = patternNode.firstChild();
                    baseRecordPatternNode.verifyType(CALTreeParserTokenTypes.BASE_RECORD_PATTERN);
                    
                    ParseTreeNode baseRecordPatternVarNode = baseRecordPatternNode.firstChild();
                    int nVars = 0;
                    if (baseRecordPatternVarNode != null) {       
                        if (pushVarName(boundVariablesStack, baseRecordPatternVarNode)) {
                            nVars++;
                        }
                    }
                   
                    ParseTreeNode fieldBindingVarAssignmentListNode = baseRecordPatternNode.nextSibling();
                    nVars += pushVarNamesFromBindings(boundVariablesStack, fieldBindingVarAssignmentListNode);
                                        
                    liftExpr(boundVariablesStack, patternNode.nextSibling(), resultExprCaseNeedsLifting);
                   
                    boundVariablesStack.popN(nVars);
                    
                    break;                                                         
                }
                
                default:
                {                
                    patternNode.unexpectedParseTreeNode();                   
                    return;
                }                                   
            }                     
        }

        if (LIFT_CASES) {
            
            //there is no point in lifting a case if it occurs in an expression of the form
            //f xs = case ... i.e. if it is at the immediate right hand side of a top level
            //function anyways. In other words, it is lifted already.
            //there are a few other situations as well, documented where the caseNeedsLifting flag is set.
            if (caseNeedsLifting) {
                
                //find the free variables that the case expression depends on. They will be a subset of the names
                //in boundVariablesStack.
                Set<String> freeVariablesSet = freeVariableFinder.findFreeNamesInExpr(boundVariablesStack, caseExprNode);
                String caseSCName = lambdaLiftInfo.getNextLiftedCaseName();               
    
                liftCaseToFunction(caseSCName, caseExprNode, freeVariablesSet);
            }          
        }       
    }
    
    /**
     * Do the actual work of lifting a case or data constructor field selection to a new function, 
     * and replace the expression with a call to that function.
     * @param liftedSCName the name of the sc to create.
     * @param caseLikeExprNode a case expression or a field selection node.
     * @param freeVariablesSet the free variables on which the case expression depends.
     */
    private void liftCaseToFunction(String liftedSCName, ParseTreeNode caseLikeExprNode, Set<String> freeVariablesSet) {
        
        // Create the function.
        ParseTreeNode paramListNode = new ParseTreeNode(CALTreeParserTokenTypes.FUNCTION_PARAM_LIST, "FUNCTION_PARAM_LIST");
        paramListNode.setTypeExprForFunctionParamList(caseLikeExprNode.getTypeExprForCaseExpr());
        ParseTreeNode liftedSelectionNode = new ParseTreeNode();
        liftedSelectionNode.copyContentsFrom(caseLikeExprNode);
        liftedSelectionNode.setNextSibling(null);
        paramListNode.setNextSibling(liftedSelectionNode);
        
        addLiftedFunction(liftedSCName, freeVariablesSet, paramListNode);
        
        // Replace the original expression in the program parse tree with a call to the top-level function we just created.
        ParseTreeNode applicationNode = new ParseTreeNode(CALTreeParserTokenTypes.APPLICATION, "@");
        makeApplication(applicationNode, liftedSCName, freeVariablesSet);
        ParseTreeNode nextSibling = caseLikeExprNode.nextSibling();
        caseLikeExprNode.copyContentsFrom(applicationNode);
        caseLikeExprNode.setNextSibling(nextSibling);                 
    }

    /**
     * A helper function that lifts any lambdas or local function defns found in the expression e where
     * e is part of a parseTree (varListRoot (x1 x2 ... xn) e).
     * Note that generalizedVarListNode can be a VAR_LIST, TUPLE_CONSTRUCTOR, COLON, etc node. The key property is
     * that it is the parent node of a list of vars, and its next sibling is a bound expression. 
     *
     * Creation date: (2/7/01 2:25:31 PM)
     * @param boundVariablesStack
     * @param generalizedVarListNode
     * @param caseNeedsLifting     
     */
    private void liftExpressionPartOfBoundExpr(ArrayStack<String> boundVariablesStack, ParseTreeNode generalizedVarListNode, boolean caseNeedsLifting) {        
       liftExpressionPartOfBoundExpr(boundVariablesStack, generalizedVarListNode, generalizedVarListNode.nextSibling(), caseNeedsLifting);    
    }
    
    private void liftExpressionPartOfBoundExpr(ArrayStack<String> boundVariablesStack, ParseTreeNode generalizedVarListNode, ParseTreeNode boundExprNode, boolean caseNeedsLifting) {
           
        int nVars = pushVarNames(boundVariablesStack, generalizedVarListNode);    
        liftExpr(boundVariablesStack, boundExprNode, caseNeedsLifting);
        boundVariablesStack.popN(nVars);
    }    
    
    /**
     * A helper method that adds the required child nodes to applicationNode so that
     * it becomes the parse tree node corresponding to the application
     * of the function named functionName to the variables in freeVariablesSet.
     *
     * Creation date: (2/8/01 9:52:05 AM)
     * @param applicationNode 
     * @param functionName an unqualified name
     * @param freeVariablesSet
     */
    private void makeApplication(ParseTreeNode applicationNode, String functionName, Set<String> freeVariablesSet) {

        applicationNode.verifyType(CALTreeParserTokenTypes.APPLICATION);

        ParseTreeNode functionNameNode = ParseTreeNode.makeQualifiedVarNode(currentModuleName, functionName, null);
        applicationNode.setFirstChild(functionNameNode);
        
        ParseTreeNode previousSiblingNode = functionNameNode;
               
        for (final String varName : freeVariablesSet) {
            ParseTreeNode varNode = ParseTreeNode.makeQualifiedVarNode(currentModuleName, varName, null);
            previousSiblingNode.setNextSibling(varNode);
            previousSiblingNode = varNode;
        }
    } 
    
    /**
     * Push the variable name for this node onto the stack.
     * @param stack
     * @param patternVarNode a parse tree node representing a pattern variable or parameter.
     * @return whether a variable name was pushed.  False if an underscore, true otherwise.
     */
    private static boolean pushVarName(final ArrayStack<String> stack, final ParseTreeNode patternVarNode) {
        switch (patternVarNode.getType())
        {
            case CALTreeParserTokenTypes.VAR_ID:
            case CALTreeParserTokenTypes.LAZY_PARAM:
            case CALTreeParserTokenTypes.STRICT_PARAM:
            {                            
                stack.push(patternVarNode.getText());
                return true;
            }
                
            case CALTreeParserTokenTypes.UNDERSCORE:
                return false;
                
            default:
            {    
                throw new IllegalArgumentException();
            }                            
        }
    }
    
    /**
     * Push the variable names that are child nodes of varListSubTree onto the stack.
     * @param stack
     * @param generalizedVarListNode
     * @return int the number of variable names (size of varListSubTree minus the number of _ pattern variables)     
     */
    private static int pushVarNames(final ArrayStack<String> stack, final ParseTreeNode generalizedVarListNode) {

        int nVars = 0;

        for (final ParseTreeNode varNode : generalizedVarListNode) {

            if (pushVarName(stack, varNode)) {
                ++nVars;
            }
        }

        return nVars;
    }
    
    /**
     * Push onto the stack the variable names that are bound to the field names in the field bindings list.
     * @param stack
     * @param fieldBindingVarAssignmentListNode
     * @return int the number of variable names (size of varListSubTree minus the number of _ pattern variables)
     */
    private static int pushVarNamesFromBindings(final ArrayStack<String> stack, final ParseTreeNode fieldBindingVarAssignmentListNode) {
        int nVars = 0;

        fieldBindingVarAssignmentListNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT_LIST);
        
        for (final ParseTreeNode fieldBindingVarAssignmentNode : fieldBindingVarAssignmentListNode) {
            
            fieldBindingVarAssignmentNode.verifyType(CALTreeParserTokenTypes.FIELD_BINDING_VAR_ASSIGNMENT);

            ParseTreeNode fieldNameNode = fieldBindingVarAssignmentNode.firstChild();
            fieldNameNode.verifyType(CALTreeParserTokenTypes.VAR_ID, CALTreeParserTokenTypes.ORDINAL_FIELD_NAME);

            ParseTreeNode varNode = fieldNameNode.nextSibling();

            if (pushVarName(stack, varNode)) {
                ++nVars;
            }
        }

        return nVars;
    }    
}
