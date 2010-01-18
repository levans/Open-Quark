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
 * ExpressionAnalyzer.java
 * Created: Jun 4, 2004Sep 12, 2003 5:42:14 PM
 * By: rcypher
 */

package org.openquark.cal.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.openquark.cal.compiler.Expression.LetNonRec;
import org.openquark.cal.compiler.Expression.Let.LetDefn;
import org.openquark.cal.compiler.Expression.Switch.SwitchAlt;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Module;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.util.ArrayStack;
import org.openquark.cal.util.Graph;
import org.openquark.cal.util.Vertex;
import org.openquark.cal.util.VertexBuilder;
import org.openquark.cal.util.VertexBuilderList;


/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * A class for doing analysis and transformations on expressions. 
 * @author rcypher
 */
public final class ExpressionAnalyzer {
    
    // Flags used to run optimizations on/off for debugging purposes.
    private static final boolean CONVERT_TAIL_RECURSION_TO_LOOPS = true;
    private static final boolean INLINE_LETVARS = true;
    
    private static final QualifiedName ifName = QualifiedName.make(CAL_Prelude.MODULE_NAME, "if");
    
    private boolean hadUnsafeCoerce;
    
    /** Flag indicating that the expression being analyzed has changed. */
    private boolean changed; 
    
    private final ModuleTypeInfo currentModuleTypeInfo;
    
    /** The MachineFunction instance corresponding to the function containing the expression being analyzed. */
    private final MachineFunction function;

    ExpressionAnalyzer (ModuleTypeInfo currentModuleTypeInfo, MachineFunction coreFunction) {
        if (currentModuleTypeInfo == null || coreFunction == null) {
            throw new NullPointerException();
        }
        this.currentModuleTypeInfo = currentModuleTypeInfo;
        this.function = coreFunction;
    }
    
    /**
     * If the expression is a let expression try to inline any letvariables.
     * A let variable can be inlined if it is in one of the following categories:
     *    1) the let variable is defined as a literal
     *    2) the let variable is defined as a non-reducible variable
     * @param e
     * @return the transformed expression.
     */
    private Expression inlineLetVars (Expression e) throws UnableToResolveForeignEntityException {
        
        // We can only inline let variables for a non recursive let.
        if (e.asLetNonRec() == null) {
            return e;
        }
        
        // If the let is never used we can simply transform to the body.
        if (e.asLetNonRec().getDefn().getUseCount() == 0) {
            changed = true;
            return e.asLetNonRec().getBody();
        }
        
        // Class to walk the expression tree substituting variable definitions for
        // variable references wherever possible.
        final class VarInliner extends PreOrderTransformVisitor {
            // Name of the variable being inlined.
            private final String varNameToInline;
            
            // Definition of the variable being inlined.
            private final Expression VarDefToInline;
            
            VarInliner (String varNameToInline, Expression varDefToInline) {
                this.varNameToInline = varNameToInline;
                this.VarDefToInline = varDefToInline;
            }
            
            // If the expression is the variable being inlined return the
            // definition.  Otherwise return the original expression.
            @Override
            Expression transform (Expression expr) {
                if (expr.asVar() != null) {
                    Expression.Var var = expr.asVar();
                    if (varNameToInline.equals(var.getName().getUnqualifiedName())) {
                        expr = VarDefToInline;
                    }
                }
                
                return expr;
            }
        }


        // Determine if the variable in question can be inlined.
        
        // Get the let variable definition.
        Expression.Let.LetDefn def = e.asLetNonRec().getDefn();


        // If the let var can't be inlined simply return.
        // We inline letvars that are literals, simply defined as another local variable, or
        // are defined as a 'safe' supercombinator.  (by safe we mean SCs that are not zero arity
        // foreign functions).
        Expression eDef = def.getExpr();
        if (!(eDef.asVar() != null && (eDef.asVar().getForeignFunctionInfo() == null || eDef.asVar().getForeignFunctionInfo().getNArguments() > 0)) && 
            eDef.asLiteral() == null) {
            return e;
        }

        // We're going to inline the let variable so set changed to true.
        changed = true;
        
        // Create the inliner and walk it over the expression tree.
        VarInliner inliner = new VarInliner (def.getVar(), def.getExpr());
        e.walk(inliner);
        
        // Since we've just inlined the letvar definition into the letvar body we want to simply return
        // the body, thus eliminating the declaration/definition of the letvar.
        return e.asLetNonRec().getBody();
    }
   
    /**
     * Apply the different transforms to the expression 'e' and all
     * its parts.
     * @param e
     * @return the transformed expression.
     */
    Expression transformExpression (Expression e) throws UnableToResolveForeignEntityException {
        changed = true;
        
        /**         
         * A RuntimeException subclass that wraps an UnableToResolveForeignEntityException.
         *
         * This is done so that the checked UnableToResolveForeignEntityException can tunnel through
         * the visitation framework without forcing all visit methods to have a throws declaration.
         * This wrapper exception is unwrapped by the try-catch block surrounding the walk() method call.
         */
        final class WrapperExceptionForUnableToResolveForeignEntityException extends RuntimeException {
           
            private static final long serialVersionUID = -8232282501855879381L;
            
            WrapperExceptionForUnableToResolveForeignEntityException(UnableToResolveForeignEntityException cause) {
                super(cause);
            }
            UnableToResolveForeignEntityException getUnderlyingException() {
                return (UnableToResolveForeignEntityException)getCause();
            }
        }
        
        // Visitor which simply calls doTransforms on each part of the expression.
        PreOrderTransformVisitor basicTransformsVisitor = new PreOrderTransformVisitor() {
            @Override
            Expression transform (Expression expressionToTransform) {        
                // Inline let variables where possible. 
                if (ExpressionAnalyzer.INLINE_LETVARS) {
                    try {
                        return inlineLetVars(expressionToTransform);
                    } catch (UnableToResolveForeignEntityException e) {
                        // Wrap the UnableToResolveForeignEntityException so that it may bubble up the
                        // visitor's call stack wrapped in a RuntimeException subclass.
                        throw new WrapperExceptionForUnableToResolveForeignEntityException(e);
                    }
                } else {
                    return expressionToTransform;
                }
            }           
        };
            
        e = inlineSpecialPreludeFunctions( e );

        while (changed) {
            changed = false;

            // Recalculate the let variable use counts since the expression may have
            // changed since the last time this was done.  This information is
            // used in the various transformations.
            letVariableUse(e);
                
            // Inline let variables where possible.
            e = ExpressionAnalyzer.INLINE_LETVARS ?  inlineLetVars(e) : e;

            
            // Apply the transforms to the parts of the expression.
            try {
                e.walk(basicTransformsVisitor);
            } catch (WrapperExceptionForUnableToResolveForeignEntityException ex) {
                // unwrap the wrapper exception, and throw the underlying UnableToResolveForeignEntityException
                throw ex.getUnderlyingException();
            }
    
            // Change top level and/or to if-then-else if it exposes a recursive tail call 
            // or the right hand side calls a strongly connected component.
            e = changeTopLevelAndOr(e);
            
            // Look for tail recursive calls (i.e. a top level recursive calls an transform them.
            // We don't want to do this for primitive functions since they always take the form fn x y = fn x y;
            if (function.isCALFunction() && ExpressionAnalyzer.CONVERT_TAIL_RECURSION_TO_LOOPS) {
                TailRecursionTransformer rt = new TailRecursionTransformer (function);
                e = rt.transformTailRecursionCalls(e);
                if (rt.changed) {
                    changed = true;
                }
            }
        }
        
        LetVarRescoper lvr = new LetVarRescoper();
        e = lvr.rescopeLetVariables(e);
        
        return e;
    }
    
    /**
     * This function is idempotent assuming that for each sub-tree t of e that 
     * doTransformsOnlyOnce has been applied in a post-order traversal of t.
     *   
     * The proof outline that this function is idempotent under the given assumption.
     * 
     * For Case 1 and 2 this can be seen using the theorems.
     * For Case 3 and 4 this can be seen by observing that the expression
     * created is a two argument function call but Case 3 and 4 expect a three 
     * argument function call so the transformation could not apply again.
     * 
     * @param e The expression to simplify
     * @return The expression after simplification.
     * 
     */
    private Expression doTransformsOnlyOnce (Expression e) {

        // Case 1: Remove any functions that are operationally the identity.
        e = inlineSimpleBodiedFunctions(e);
        
        // Case 2: Change applications of negate to a literal to be the negated literal value.
        e = collapseNegate(e);

        // Case 3: Inline flip function
        
        if (e.asAppl() != null){
            // e = (a1 ...) 
            // a1 = (a11 a12)
            // a11 = (v111 ...)
            Expression.Appl a1 = e.asAppl().getE1().asAppl();
            if (a1 != null){
                Expression.Appl a11 = a1.asAppl().getE1().asAppl();
                if (a11 != null){
                    Expression.Var v111 = a11.getE1().asVar();
                    if (v111 != null){
                        if (v111.getName().equals(CAL_Prelude.Functions.flip)){
                            e = doTransformsOnlyOnce(new Expression.Appl(
                                    doTransformsOnlyOnce(new Expression.Appl(a11.getE2(), e.asAppl().getE2())),
                                    a1.getE2()
                            ));
                        }
                    }
                }   
            }
        }
        
        // Case 4: Inline the compose function if possible.
        
        e = inlineComposeFunction(e);
            
        return e;
    }
    

    /**
     * Converts top level and/or into and if-then-else under certain conditions.  
     * The conversion occurs if the right hand side of the and/or is  tail recursive
     * or contains a call to a closely connected component.  This will expose tail recusive calls
     * for optimization and avoid space usage issues.
     * 
     * @param e
     * @return the transformed expression.
     */
    private Expression changeTopLevelAndOr (Expression e) {
        if (e.asAppl() != null) {
            Expression[] chain = appChain (e);
            if (chain == null) {
                return e;
            }
            
            QualifiedName varName = chain[0].asVar().getName();
    
            if (chain.length == 4 && varName.equals(ifName)) {
                Expression.Appl app = e.asAppl ();
                app.setE2 (changeTopLevelAndOr (app.getE2 ()));
                app = app.getE1().asAppl ();
                if (app != null) {
                    app.setE2 (changeTopLevelAndOr (app.getE2 ()));
                }
                return e;
            }
            
            // If this is a fully saturated application of Prelude.and or Prelude.or and the right hand side
            // depends on a strongly connected component we want to change it to an if-then-else.  
            // In the case of a tail recursive call the call is then eligible for optimization by being 
            // transformed to a loop.  In the case of indirect recursion the this avoids an issue with using
            // too much call stack space while still allowing for a more efficient representation than a call
            // to Prelude.and or Prelude.or.
            if (chain.length == 3 && 
                (varName.equals(CAL_Prelude.Functions.or) || varName.equals(CAL_Prelude.Functions.and)) && 
                dependsOnStronglyConnectedComponent(chain[2])) {
                
                Expression.Var ifVar = new Expression.Var (ifName);
                if (varName.equals(CAL_Prelude.Functions.and)) {
                   Expression.Appl app = new Expression.Appl (ifVar, chain[1]);
                   app = new Expression.Appl (app, chain[2]);
                   app = new Expression.Appl (app, new Expression.Literal (Boolean.FALSE));
                   changed = true;
                   return app;
                } else
                if (varName.equals(CAL_Prelude.Functions.or)) {
                    Expression.Appl app = new Expression.Appl (ifVar, chain[1]);
                    app = new Expression.Appl (app, new Expression.Literal (Boolean.TRUE));
                    app = new Expression.Appl (app, chain[2]);
                    changed = true;
                    return app;
                }
            }
        } else if (e.asLet() != null) {
            Expression.Let let = e.asLet();
            let.setBody(changeTopLevelAndOr (let.getBody()));
        } else if (e.asSwitch() != null) {
            Expression.Switch sw = e.asSwitch();
            Expression.Switch.SwitchAlt alts[] = sw.getAlts();
            for (int i = 0; i < alts.length; ++i) {
                alts[i].setAltExpr(changeTopLevelAndOr(alts[i].getAltExpr()));
            }
        }
        
        return e;
    }
    
    /**
     * Determine if the given expression is dependent on a strongly connected component of the containing
     * function.
     * @param e
     * @return true or false
     */
    private boolean dependsOnStronglyConnectedComponent(Expression e) {
        return dependsOnStronglyConnectedComponent(e, function.getStronglyConnectedComponents(), function.getQualifiedName().getModuleName());
    }

    /**
     * @param e
     * @param stronglyConnectedComponents
     * @param moduleName
     * @return true if the expression depends on a function in the set of strongly connected components.
     */
    public static boolean dependsOnStronglyConnectedComponent (Expression e, Set<String> stronglyConnectedComponents, ModuleName moduleName) {
        if (stronglyConnectedComponents == null) {
            return false;
        }
      
        for (final String name : stronglyConnectedComponents) {
           
            if (ExpressionAnalyzer.isDependentOn(e, QualifiedName.make(moduleName, name))) {
                return true;
            }
        }
        return false;
    }

    /**
     * If e is an application of the negateInt, negateDouble, negateLong, negateInteger to a
     * literal value we change it to a literal of the negative value.
     * @param e
     * @return the collapsed expression.
     */
    private Expression collapseNegate (Expression e) {
        
        if (e instanceof Expression.Appl) {
            
            Expression.Appl app = (Expression.Appl)e;
            
            if (app.getE1().asVar() != null && app.getE2().asLiteral() != null) {
                
                Expression.Var var = app.getE1().asVar();
                Expression.Literal lit = app.getE2().asLiteral();
                
                QualifiedName functionName = var.getName();
                
                if (functionName.getModuleName().equals(CAL_Prelude.MODULE_NAME)) {
                    
                    String unqualifiedFunctionName = functionName.getUnqualifiedName();
                    
                    if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.negateInt.getUnqualifiedName())) {
                        e = new Expression.Literal(Integer.valueOf(-((Integer)lit.getLiteral()).intValue()));
                        changed = true;
                    } else if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.negateDouble.getUnqualifiedName())) {
                        e = new Expression.Literal(new Double (-((Double)lit.getLiteral()).doubleValue()));
                        changed = true;
                    } else if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.negateLong.getUnqualifiedName())) {
                        e = new Expression.Literal(Long.valueOf(-((Long)lit.getLiteral()).longValue()));
                        changed = true;
                    } else if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.negateInteger.getUnqualifiedName())) {
                        e = new Expression.Literal(((BigInteger)lit.getLiteral()).negate());
                        changed = true;
                    } else if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.negateShort.getUnqualifiedName())) {
                        e = new Expression.Literal(Short.valueOf(((short)-((Short)lit.getLiteral()).shortValue())));
                        changed = true;
                    } else if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.negateFloat.getUnqualifiedName())) {
                        e = new Expression.Literal( new Float(-((Float)lit.getLiteral()).floatValue()));
                        changed = true;
                    } else if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.negateByte.getUnqualifiedName())) {
                        e = new Expression.Literal(Byte.valueOf(((byte)-((Byte)lit.getLiteral()).byteValue())));
                        changed = true;
                    }                       
                }
            }
        }
        
        return e;
    }
    
    /**
     * Inline a few special functions in the Prelude.
     * 
     * In general, there are a few special functions whose defining expressions involve less graph than writing the
     * application of the function itself. 
     * 
     * Some of these functions (such as unsafeCoerce and asTypeOf) are used primarily to persuade the type-checker to accept 
     * certain constructs, and it is important that they have no run-time impact for this sort of usage.
     * 
     * (id, emptyString, emptyList) are instance method functions and so occur through the compile-time instance
     * resolution optimization even though they don't naturally occur in user-written code.
     * 
     * (fst, snd, field1, ... field7, list0) are just handy small optimizations.
     * 
     * (compose, apply, flip, const) occur sometimes in fully saturated form when writing gems in the GemCutter. compose also
     * can occur fully saturated as a translation of the "#" operator.
     *            
     * 0 argument functions:
     *    emptyString :: String;
     *    emptyList :: [a];
     *    list0 :: [a];
     * 
     * 1 argument functions: (note that id, unsafeCoerce and apply are all operationally the identity function).
     *    id :: a -> a;
     *    public id !x = x;
     *    
     *    primitive public unsafeCoerce :: a -> b;
     *    
     *    apply :: (a -> b) -> a -> b;
     *    public apply !functionToApply = functionToApply;
     *    
     *    public fst !pair = pair.#1;
     *    public snd !pair = pair.#2;
     *    public field1 !r = r.#1;
     *    ...
     *    public field7 !r = r.#7;
     *       
     * 2 argument functions:
     *    asTypeOf :: a -> a -> a;
     *    public asTypeOf !valueToReturn valueToIgnore = valueToReturn;
     *    
     *    const :: a -> b -> a;
     *    public const !valueToReturn valueToIgnore = valueToReturn;
     *    
     * 3 argument functions:
     *    compose :: (b -> c) -> (a -> b) -> (a -> c);
     *    public compose !f g x = f (g x);
     *
     *    flip :: (a -> b -> c) -> b -> a -> c;
     *    public flip !f x y = f y x;
     * 
     * @param e
     * @return expression with inlining done of the top level application node
     */
    
    /**
     * Theorems about traversing a tree in post order
     * 
     * p is a tree of nodes, n.
     * f is a function from p to p.
     * 
     * Tf(p) - T is a function that traverses p in post order and applies f to each subtree of p.
     * 
     * Tf(p) is idempotent iff Tf(Tf(p)) = Tf(p).
     * 
     * Theorem 1. If f and g are independent (if the subtrees of p have been traversed) 
     *     and Tf and Tg are idempotent then T(f.g) is idempotent.
     * 
     *         Tf(p) = Tf(Tf(p)) AND Tg(p) = Tg(Tg(p)) AND g(f(p)) == f(g(p)) then T(f.g) == T(f.g) . T(f.g)
     * 
     * Theorem 2. If f(p) returns a subtree of p, a leaf, or p for internal nodes and p for leaf nodes then Tf is idempotent.
     * 
     * inlineOperationalIdentityFunction satisfies Theorem 2.
     * collapseNegate satisfies Theorem 2.
     * Theorem 1 applies to inlineOperationalIdentityFunction and collapseNegate.
     *   
     *   @param e - the expression to be transformed
     *   @return the transformed expression.
     */
    
    private Expression inlineSpecialPreludeFunctions(Expression e) { 
        PostOrderTransformVisitor basicTransformsVisitor = new PostOrderTransformVisitor() {
            @Override
            Expression transform (Expression e) {return doTransformsOnlyOnce(e);}
        };
        
        e.walk(basicTransformsVisitor);
        e = doTransformsOnlyOnce (e);           

        return e;
    }

    /**
     * In general, there are a few special functions whose defining expressions involve less graph than writing the
     * application of the function itself. 
     * 
     * Some of these functions (such as unsafeCoerce and asTypeOf) are used primarily to persuade the type-checker to accept 
     * certain constructs, and it is important that they have no run-time impact for this sort of usage.
     * 
     * (id, emptyString, emptyList) are instance method functions and so occur through the compile-time instance
     * resolution optimization even though they don't naturally occur in user-written code.
     * 
     * (fst, snd, field1, ... field7, list0) are just handy small optimizations.
     * 
     * (apply, const) occur sometimes in fully saturated form when writing gems in the GemCutter. compose also
     * can occur fully saturated as a translation of the "#" operator.
     *            
     * 0 argument functions:
     *    emptyString :: String;
     *    emptyList :: [a];
     *    list0 :: [a];
     * 
     * 1 argument functions: (note that id, unsafeCoerce, outputJObject and apply are all operationally the identity function).
     *    id :: a -> a;
     *    public id !x = x;
     *    
     *    primitive public unsafeCoerce :: a -> b;
     *    
     *    outputJObject :: JObject -> JObject;
     *    private outputJObject !x = x;
     *    
     *    apply :: (a -> b) -> a -> b;
     *    public apply !functionToApply = functionToApply;
     *    
     *    public fst !pair = pair.#1;
     *    public snd !pair = pair.#2;
     *    public field1 !r = r.#1;
     *    ...
     *    public field7 !r = r.#7;
     *       
     * 2 argument functions:
     *    asTypeOf :: a -> a -> a;
     *    public asTypeOf !valueToReturn valueToIgnore = valueToReturn;
     *    
     *    const :: a -> b -> a;
     *    public const !valueToReturn valueToIgnore = valueToReturn;
     *    
     * @param e The expression to try the inlining operation on.
     * @return expression with inlining done of the top level application node
     */

    private Expression inlineSimpleBodiedFunctions(Expression e) {
        if (e instanceof Expression.Var){
            Expression.Var var = e.asVar();
            QualifiedName functionName = var.getName();
            if (functionName.getModuleName().equals(CAL_Prelude.MODULE_NAME)){
                String unqualifiedFunctionName = functionName.getUnqualifiedName();
                if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.emptyList.getUnqualifiedName())){ 
                    e = new Expression.Var(currentModuleTypeInfo.getVisibleDataConstructor(CAL_Prelude.DataConstructors.Nil));
                }
                else if (unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.emptyString.getUnqualifiedName())){
                    e = new Expression.Literal("");
                }
            } else if (functionName.equals(CAL_List.Functions.list0)) {
                e = new Expression.Var(currentModuleTypeInfo.getVisibleDataConstructor(CAL_Prelude.DataConstructors.Nil));
            }
        }
        else if (e instanceof Expression.Appl) {
            
            Expression.Appl app = (Expression.Appl)e;
            
            if (app.getE1().asVar() != null) {
                
                Expression.Var var = app.getE1().asVar();              
                
                QualifiedName functionName = var.getName();
                
                ForeignFunctionInfo foreignFunctionInfo; 
                if ((foreignFunctionInfo = var.getForeignFunctionInfo()) != null
                    && foreignFunctionInfo.getJavaKind() == ForeignFunctionInfo.JavaKind.IDENTITY_CAST) {
                    
                    //these functions are operationally the identity                        
                    e = app.getE2();                   
                    
                } else  if (functionName.getModuleName().equals(CAL_Prelude.MODULE_NAME)) {
                    
                    final String unqualifiedFunctionName = functionName.getUnqualifiedName();
                    
                    if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.id.getUnqualifiedName()) ||
                        unqualifiedFunctionName.equals(CAL_Prelude.Functions.unsafeCoerce.getUnqualifiedName()) ||
                        unqualifiedFunctionName.equals(CAL_Prelude.Functions.toCalValue.getUnqualifiedName()) ||
                        unqualifiedFunctionName.equals(CAL_Prelude.Functions.unsafeFromCalValue.getUnqualifiedName()) ||
                        unqualifiedFunctionName.equals(CAL_Prelude_internal.Functions.outputJObject.getUnqualifiedName()) ||
                        unqualifiedFunctionName.equals(CAL_Prelude.Functions.apply.getUnqualifiedName())) {
                        
                        if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.unsafeCoerce.getUnqualifiedName())){
                            hadUnsafeCoerce = true;
                        }
                        
                        //these functions are all operationally the identity function
                        
                        e = app.getE2();
                    }
                    else{
                        int ordinal = 0; // 0 is not a valid value and is used as a flag
                        
                        if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.fst.getUnqualifiedName())){
                            ordinal = 1;
                        }
                        else if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.snd.getUnqualifiedName())){
                            ordinal = 2;
                        }
                        else if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.field1.getUnqualifiedName())){
                            ordinal = 1;
                        }
                        else if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.field2.getUnqualifiedName())){
                            ordinal = 2;
                        }
                        else if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.field3.getUnqualifiedName())){
                            ordinal = 3;
                        }
                        else if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.field4.getUnqualifiedName())){
                            ordinal = 4;
                        }
                        else if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.field5.getUnqualifiedName())){
                            ordinal = 5;
                        }
                        else if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.field6.getUnqualifiedName())){
                            ordinal = 6;
                        }
                        else if (unqualifiedFunctionName.equals(CAL_Prelude.Functions.field7.getUnqualifiedName())){
                            ordinal = 7;
                        }
                        
                        if (ordinal > 0){               
                            e = new Expression.RecordSelection(e.asAppl().getE2(), FieldName.makeOrdinalField(ordinal));
                        }
                    }
                }
            }
            else if (e.asAppl().getE1() instanceof Expression.Appl){
                Expression.Appl a1 = e.asAppl().getE1().asAppl();
                if (a1 != null){
                    Expression.Var v1_1 = a1.asAppl().getE1().asVar();
                    if (v1_1 != null){
                        // public asTypeOf !valueToReturn valueToIgnore = valueToReturn;
                        if (v1_1.asVar().getName().equals(CAL_Prelude.Functions.asTypeOf)){
                            e = a1.getE2();
                        }
                        // public const !valueToReturn valueToIgnore = valueToReturn;                       
                        else if (v1_1.asVar().getName().equals(CAL_Prelude.Functions.const_)){
                            e = a1.getE2();
                        }
                    }
                }
            }
        }
        
        return e;        
    }

    /**
     * If the expression is of the form 
     * 
     * (((Prelude.compose expr1) expr2) expr3)     
     * 
     * then replace by (expr1 (expr2 expr3)).
     * 
     * @param e the input expression
     * @return the optimized expression
     */
    
    private Expression inlineComposeFunction(Expression e) {
        
        if (e instanceof Expression.Appl) {
            
            Expression.Appl app = (Expression.Appl)e;
            
            Expression e1 = app.getE1();
            
            if (e1 instanceof Expression.Appl) {
                
                Expression.Appl app1 = (Expression.Appl)e1;
                
                Expression e11 = app1.getE1();
                
                if (e11 instanceof Expression.Appl) {
                    
                    Expression.Appl app11 = (Expression.Appl)e11;
                    
                    if (app11.getE1().asVar() != null) {
                        
                        Expression.Var var = app11.getE1().asVar();
                        
                        QualifiedName functionName = var.getName();                        
                        if (functionName.equals(CAL_Prelude.Functions.compose)) {                                                       
                            e = new Expression.Appl(app11.getE2(), doTransformsOnlyOnce(new Expression.Appl(app1.getE2(), app.getE2())));
                            e = doTransformsOnlyOnce( e );                                          
                        }
                    }
                }
            }
        }
        
        return e;        
        
    }

    /**
     * This function lifts the definitions of non-recursive let variables
     * into their own functions.  
     * Lifting let variable definitions into their own functions is required by
     * the LECC machine to work around a limitation on the size of individual 
     * methods in the Java bytecode format.
     * 
     * @param mf
     * @param module
     * @return the transformed Expression and the lifted let variable definition functions.
     */
    public static LiftedLetVarResults liftLetVariables (MachineFunction mf, Module module) {
        // Create the LetVarLifter
        LetVarLifter lvl = new LetVarLifter();
        return lvl.liftLetVariables(mf, module);
    }
    
    /**
     * Encapsulate the results of lifting let variable definitions.
     * This class holds the transformed Expression and the
     * information about the lifted let variables. 
     * @author rcypher
     */
    public static final class LiftedLetVarResults {
        /** The transformed Expression. */
        Expression modifiedExpression;
        
        /** Collection of LiftedLetVarInfo */
        Collection<LiftedLetVarInfo> liftedVarInfo;
        
        LiftedLetVarResults (Expression modifiedExpression, Collection<LiftedLetVarInfo> liftedVarInfo) {
            this.modifiedExpression = modifiedExpression;
            this.liftedVarInfo = liftedVarInfo;
        }
        
        public Expression getExpression() {return modifiedExpression;}
        public Collection<LiftedLetVarInfo> getLiftedVarInfo() {return liftedVarInfo;}
    }
    
    /**
     * This class encapsulates the functionality which does lifts
     * the definitions of non-recursive let variables into their own
     * functions.  
     * This is accomplished through a series of transformations.
     * 
     * @author rcypher
     *
     */
    private static class LetVarLifter {
        
        /**
         * Transform expression e such that the definitions of non-recursive let
         * variables are lifted out as separate functions.
         * The original expression is not modified, instead a new Expression
         * is produced.
         * @param mf
         * @param module
         * @return the transformed expression and a Collection of LiftedLetVarInfo describing the lifted let variable functions
         */
        LiftedLetVarResults liftLetVariables (MachineFunction mf, Module module) {
            
            // Get the Expression for the function and make a copy.
            // All transformations will be performed on the copy, leaving
            // the original Expression untouched.
            Expression e = mf.getExpressionForm();
            ExpressionCopier<Void> copier = new ExpressionCopier<Void>();
            e = (Expression)e.accept(copier, null);

            // First we want to collect all the let variables in the expression.
            // We use an extension of Visitor which simply builds up a Map
            // of String -> Expression.Let.LetDefn.
            final class LetVarFinder extends Visitor {
                /** String -> Expression.Let.LetDefn */
                Map<String, LetDefn> letVars = new HashMap<String, LetDefn>();
                
                @Override
                void enterLetNonRec (Expression.LetNonRec letNonRec) {
                    letVars.put(letNonRec.getDefn().getVar(), letNonRec.getDefn());
                }
            }
            LetVarFinder letVarFinder = new LetVarFinder();
            e.walk(letVarFinder);
            Map<String, LetDefn> letVars = letVarFinder.letVars;
            
            
            // Next we want to do the lifting of the let variables, which will generate the list of
            // LiftedLetVarInfo.  We use a PostOrderTransformVisitor.
            
            /**
             * The LetVarLiftTransformer is a post order transformer which lifts 
             * non-recursive let variable definitions into their own functions.
             * For example:
             * 
             * foo a b = let x = a + b; y = x + b; in if a > b then x else y;
             * 
             * Would be transformed to:
             * 
             * foo a b = let x = xDef a b; y = yDef x b; in if a > b then x else y;
             * xDef a b = a + b;
             * yDef x b = x + g;
             * 
             */
            final class LetVarLiftTransformer extends PostOrderTransformVisitor {
                /** The module containing the function being transformed. */
                private final Module module;
                
                /** The information describing the lifted letvar definition functions. */
                private final List<LiftedLetVarInfo> liftedVarInfo = new ArrayList<LiftedLetVarInfo>();

                /** 
                 * Map of String -> StrictnessAndType.  Used to track and hold
                 * information about local variables that are defined in
                 * this expression.
                 */
                Map<String, StrictnessAndType> definedVars = new HashMap<String, StrictnessAndType>();
                
                /**
                 * Class used to record the strictness and type of local variables
                 * defined in the expression being transformed. 
                 */
                final class StrictnessAndType {
                    boolean strictness;
                    TypeExpr type;
                    StrictnessAndType(boolean strictness, TypeExpr type) {
                        this.strictness = strictness;
                        this.type = type;
                    }
                    boolean getStrictness() {return strictness;}
                    TypeExpr getType() {return type;}
                }
                
                /** 
                 * Map of String -> Expression.Let.LetDefn, for all the let variables in the
                 * function.
                 * */
                private final Map<String, LetDefn> allLetVars;
                
                LetVarLiftTransformer (Module module, Map<String, LetDefn> allLetVars) {
                    this.module = module;
                    this.allLetVars = allLetVars;
                }
                
                @Override
                void enterLetRec(Expression.LetRec letRec) {
                    // Add the variables into the defined variables map.
                    for (int i = 0; i < letRec.getDefns().length; ++i) {
                        definedVars.put(letRec.getDefns()[i].getVar(), new StrictnessAndType(false, letRec.getDefns()[i].getVarType()));
                    }
                }
                
                @Override
                void enterLetNonRec (Expression.LetNonRec letNonRec) {
                    // Add the variable into the definedVariables map.
                    // We can get the variable type from the definition.
                    // We can consider the variable strict if the defining
                    // expression is an application of Prelude.eager.
                    Expression varDef = letNonRec.getDefn().getExpr();
                    Expression[] chain = appChain(varDef);
                    boolean strict = false;
                    if (chain != null && chain.length == 2 && chain[0].asVar() != null && chain[0].asVar().getName().equals(CAL_Prelude.Functions.eager)) {
                        strict = true;
                    }
                    definedVars.put(letNonRec.getDefn().getVar(), new StrictnessAndType(strict, letNonRec.getDefn().getVarType()));
                }
                @Override
                void enterSwitchAlt (Expression.Switch.SwitchAlt alt) {
                    if (alt.hasVars()) {
                        // Add the variables to the defined variables map.
                        // We want to mark as strict variables which correspond
                        // to a plinged field in the data constructor.
                        // NOTE: we know that we are dealing with a data constructor
                        // because the switch alt has variables.
                        // NOTE2: Because a switch alt can have multiple tags (i.e. data
                        // constructors) the field must be plinged in all the data 
                        // constructors for the corresponding var to be considered strict.
                        String[] names = alt.getVarNames();
                        for (int i = 0; i < names.length; ++i) {
                            boolean strict = true;
                            TypeExpr type = null;
                            if (alt instanceof Expression.Switch.SwitchAlt.Matching) {
                                Map<FieldName, String> fieldNameToVarNameMap = ((SwitchAlt.Matching)alt).getFieldNameToVarNameMap();
                                for (final Map.Entry<FieldName, String> entry : fieldNameToVarNameMap.entrySet()) {
                                    FieldName fn = entry.getKey();
                                    String varName = entry.getValue();
                                    if (varName.equals(names[i])) {
                                        for(int j = 0, nTags = alt.getAltTags().size(); j < nTags; ++j) {
                                            DataConstructor dc = (DataConstructor)alt.getAltTags().get(j);
                                            if (!dc.isArgStrict(dc.getFieldIndex(fn))) {
                                                strict = false;
                                            }
                                            if (type == null) {
                                                TypeExpr dcType = dc.getTypeExpr();
                                                TypeExpr[] tft = dcType.getTypePieces(dc.getArity());
                                                type = tft[dc.getFieldIndex(fn)]; 
                                            }
                                        }                                        
                                        break;
                                    }
                                }
                                
                            } else {
                                SortedMap<Integer, String> positionToVarNameMap = ((SwitchAlt.Positional)alt).getPositionToVarNameMap();
                                for (final Map.Entry<Integer, String> entry :  positionToVarNameMap.entrySet()) {
                                    Integer indexInteger = entry.getKey();
                                    String altVar = entry.getValue();
                                    if (altVar.equals(names[i])) {
                                        for(int j = 0, nTags = alt.getAltTags().size(); j < nTags; ++j) {
                                            DataConstructor dc = (DataConstructor)alt.getAltTags().get(j);
                                            if (!dc.isArgStrict(indexInteger.intValue())) {
                                                strict =false;
                                                break;
                                            }
                                            if (type == null) {
                                                TypeExpr dcType = dc.getTypeExpr();
                                                TypeExpr[] tft = dcType.getTypePieces(dc.getArity());
                                                type = tft[indexInteger.intValue()]; 
                                            }
                                            
                                        }
                                        break;
                                    }
                                }
                                
                            }
                            definedVars.put(names[i], new StrictnessAndType(strict, type));
                        }
                    }
                }
               
                @Override
                void enterRecordCase(Expression.RecordCase recordCase){
                    // Add the variables to the definedVariables map.
                    if (recordCase.getFieldBindingVarMap().size() > 0) {
                        Map<FieldName, String> bindingMap = recordCase.getFieldBindingVarMap();
                        for (final Map.Entry<FieldName, String> entry : bindingMap.entrySet()) {                           
                            definedVars.put(entry.getValue(), new StrictnessAndType(false, null));
                        }
                    }
                }
                
                /**
                 * Do the post-order transformation.  In this case we only 
                 * transform non-recursive let expressions.
                 */
                @Override
                Expression transform (Expression e) {
                    // If e is an Expression.LetNonRec we want to do the modification.
                    Expression.LetNonRec lnr = e.asLetNonRec();
                    if (lnr != null) {
                        
                        // There are some cases where there is no point in
                        // lifting the let variable definition.
                        // If the letvar definition is a var there is
                        // no point in lifting.
                        if (lnr.getDefn().getExpr().asVar() != null) {
                            return e;
                        }
                        
                        // Determine the free variables in the RHS of the let binding.
                        // These will be the arguments of the lifted function.
                        Set<QualifiedName> freeVarNameSet = ExpressionAnalyzer.findReferencedFreeVariables(lnr.getDefn().getExpr(), module);
                        
                        // Some of the free variables found by the previous step may actually be 
                        // Expression.Var instances referring to other lifted let variable definitions.
                        // The show up as free variables because there is no information about them
                        // in the module.  We need to remove these.
                        for (int i = 0; i < liftedVarInfo.size(); i++) {
                            LiftedLetVarInfo lifted = liftedVarInfo.get(i);
                            freeVarNameSet.remove(lifted.getFunctionQualifiedName());
                        }
                        
                        // Build up the names and types of the arguments to the lifted function.
                        String[] argNames = new String[freeVarNameSet.size()];
                        TypeExpr[] argTypes = new TypeExpr[argNames.length];
                        boolean[] argStrictness = new boolean[argNames.length];
                        
                        // We can fill in the arg type for any args that are other let bindings.
                        int i = 0;
                        for (final QualifiedName qualifiedArgName : freeVarNameSet) {
                            String argName = qualifiedArgName.getUnqualifiedName();
                            TypeExpr argType = null;
                            Expression.Let.LetDefn letDef = allLetVars.get(argName);
                            if (letDef != null) {
                                argType = letDef.getVarType();
                            }
                            
                            argNames[i] = argName;
                            argTypes[i] = argType;
                            
                            StrictnessAndType st = definedVars.get(argName);
                            if (st != null) {
                                if (st.getStrictness()) {
                                    argStrictness[i] = true;
                                }
                                if (st.getType() != null) {
                                    argTypes[i] = st.getType();
                                }
                            }
                            
                            i++;
                        }
                        
                        String functionName = lnr.getDefn().getVar() + "$def";
                        
                        // Create the lifted letvar def info.
                        liftedVarInfo.add(
                                new LiftedLetVarInfo(
                                        module.getName(),
                                        functionName, 
                                        argNames.length, 
                                        argNames, 
                                        argTypes,
                                        argStrictness,
                                        lnr.getDefn().getVarType(),
                                        lnr.getDefn().getExpr()));
                        
                        // Transform the LetNonRec expression.
                        // Basically we replace the right hand side of the let binding with an
                        // application of the lifted definition function.
                        Expression newRHS = new Expression.Var(QualifiedName.make(module.getName(), functionName));
                        for (i = 0; i < argNames.length; ++i) {
                            newRHS = new Expression.Appl(newRHS, new Expression.Var(QualifiedName.make(module.getName(), argNames[i])));
                        }
                        
                        lnr.getDefn().setExpr(newRHS);
                    }
                    
                    return e;
                }
            }
            
            // Create the let variable lifter and transform the expression.
            LetVarLiftTransformer lvlt = new LetVarLiftTransformer(module, letVars);
            e.walk(lvlt);
            e = lvlt.transform(e);
            
            // Grab the information about the lifted variable definitions.
            List<LiftedLetVarInfo> liftedLetVarInfo = lvlt.liftedVarInfo;
            
            // Now that we've lifted the let variable definitions we want to float the 
            // let variables inward.  Yet another post order transform visitor comes
            // into play.

            // Change top level if-then-else to switch.
            // The transformation to re-scope let variables
            // inward only applies to switch alts, since we
            // know that these will always correspond to a 
            // scope in the Java code generated by the LECC
            // machine.
            // Top level if-then-else constructs will also 
            // translate to a Java scope.  However, it is 
            // difficult to track which if-then-else is top
            // level at the same time as floating the let variables.
            // We solve this by transforming top level if-then-else
            // into boolean switches.  This is safe as the generated
            // code will still generate a Java if-then-else.
            e = changeTopLevelIfThenElse(e);
            
            /**
             * This class does an inward floating let variable transformation.
             * For example:
             * foo a b = let x = xDef a b; y = yDef x b; in if a < b then x else y;
             * 
             * Can be transformed to:
             * foo a b = let x = xDef a b; in if a < b then x else let y = yDef x b; in y;
             * 
             *  This transformation can result in more efficient code as the definition of a 
             *  let variable will only be constructed if the path of execution requires it.
             */
            final class LetVarRescoper2 extends PostOrderTransformVisitor {
            
                /** Stack of Set of String.  Tracks variables encountered for
                 * the first time in each scope.
                 */
                ArrayStack<Set<String>> scopes = ArrayStack.make();
                        
                /** Map of String -> Expression.LetNonRec.  Tracks the set of
                 * currently visible let variables.
                 */
                Map<String, LetNonRec> letVariableDefs = new HashMap<String, LetNonRec>();
                
                /** 
                 * Names of defined let variables in the order they
                 * were encountered. 
                 */
                Set<String> letVariables = new LinkedHashSet<String>();
                
                /** Set of String. Names of moved non-recursive let variables. */
                Set<String> movedLetVars = new HashSet<String>(); 
                
                LetVarRescoper2 () {
                    scopes.push(new HashSet<String>());
                }
                
                /**
                 * We are entering a scope.
                 */
                @Override
                public void enterSwitchAlt(Expression.Switch.SwitchAlt alt) {
                    scopes.push(new HashSet<String>());
                }
                
                /** At this point we are leaving a scope.  We want to
                 * move the let variable declaration for any lets first
                 * encountered in this scope inwards.
                 * The transformation only considers switch alts as scopes
                 * since a switch alt will always translate into a scope
                 * in the Java code generated by the LECC machine.
                 */
                @Override
                public void exitSwitchAlt(Expression.Switch.SwitchAlt alt) {
                    super.exitSwitchAlt(alt);
                    
                    // Pop the scope and build up the set of let
                    // variables first encountered in this scope.
                    Set<String> scope = scopes.pop();
                    List<String> varsInScope = new ArrayList<String>();
                    for (final String varName : letVariables) {                        
                        if (scope.contains(varName)) {
                            varsInScope.add(varName);
                        }
                    }
                    
                    // At this point we want to change the body of
                    // the switch alt to contain let bindings for the
                    // let variables first encountered in this scope.
                    // For example:
                    // let x = A; y = B;
                    // in
                    // case C of 
                    //    D -> ... x ... y ...;
                    // ;
                    // Would change to.
                    // let x = A; y = B;
                    // in
                    // case C of 
                    //    D -> let x = A; y = B; in ... x ... y ...;
                    // ;
                    Expression body = alt.getAltExpr();
                    for (int i = varsInScope.size()-1; i >= 0; i--) {
                        String varName = varsInScope.get(i);
                        Expression.LetNonRec lnr = letVariableDefs.get(varName);
                        
                        body = new Expression.LetNonRec(lnr.getDefn(), body);
                        movedLetVars.add(lnr.getDefn().getVar());
                    }
                    
                    alt.setAltExpr(body);
                }
                
                /**
                 * When we enter a LetNonRec we want to check the let
                 * variable usage info to determine if this is one of the
                 * let variables to be moved.  If it is we should add it
                 * to the map 'letVarsToMove'.
                 */
                @Override
                public void enterLetNonRec (Expression.LetNonRec lnr) {
                    letVariableDefs.put(lnr.getDefn().getVar(), lnr);
                    letVariables.add(lnr.getDefn().getVar());
                }
                
                /** 
                 * Upon exiting a LetNonRec we apply the transformations in the usual
                 * post-order fashion the remove the information about this let variable
                 * from the Map and Set tracking the currently visible let variables.
                 */
                @Override
                public void exitLetNonRec(Expression.LetNonRec let){
                    super.exitLetNonRec(let);
                    letVariableDefs.remove(let.getDefn().getVar());
                    letVariables.remove(let.getDefn().getVar());
                }
    
                /**
                 * If this var is a let var and this is the first time
                 * it has been encountered in this scope we want to 
                 * add it to the current scope.
                 */
                @Override
                public void enterVar(Expression.Var var) {
                    if (letVariableDefs.containsKey(var.getName().getUnqualifiedName())) {
                        for (final Set<String> s : scopes) {                            
                            if (s.contains(var.getName().getUnqualifiedName())) {
                                return;
                            }
                        }
                        Set<String> scope = scopes.peek();
                        scope.add(var.getName().getUnqualifiedName());
                    }
                }
                
                /**
                 * The transformation is quite simple.
                 * It only applies to LetNonRec expressions.  
                 * Since we are doing the transformation post-order
                 * we can look at the moved variable set.  If this
                 * variable has been moved we want to simply return the body.
                 * @param expressionToTransform
                 * @return the transformed Expression
                 */
                @Override
                Expression transform (Expression expressionToTransform) {
                    Expression.LetNonRec lnr = expressionToTransform.asLetNonRec();
                    if (lnr == null) {
                        return expressionToTransform;
                    }
                    
                    if (movedLetVars.contains(lnr.getDefn().getVar())) {
                        movedLetVars.remove(lnr.getDefn().getVar());
                        return lnr.getBody();
                    }
                    
                    return lnr;
                    
                }
            }
            
            // Do the inward floating of let variables for the main function and all the lifted
            // let variable definition functions.
            boolean change = true;
            while (change) {
                LetVarRescoper2 lvr2 = new LetVarRescoper2();
                e.walk(lvr2);
                e = lvr2.transform(e);
                change = lvr2.movedLetVars.size() > 0;
            }

            for (final LiftedLetVarInfo llvi : liftedLetVarInfo) {               
                change = true;
                while (change) {
                    LetVarRescoper2 lvr2 = new LetVarRescoper2();
                    llvi.expression.walk(lvr2);
                    llvi.expression = lvr2.transform(llvi.expression);
                    change = lvr2.movedLetVars.size() > 0;
                }
                
            }
            
            
            // Now we can in-line the definition of any single-use let variables.
            
            /**
             * This class will in-line the definitions of single-use let variables.
             */
            final class LetVarInLiner extends PostOrderTransformVisitor {
                private final ReferenceCounter referenceCounter = new ReferenceCounter();
                private final ModuleName moduleName;
                
                LetVarInLiner (ModuleName moduleName) {
                    this.moduleName = moduleName;
                }
                
                /**
                 * The transformation is quite simple.
                 * It only applies to LetNonRec expressions.  
                 * Determine if the let var is referenced only
                 * once.  If so we transform the let body and
                 * return it.
                 * @param expressionToTransform
                 * @return the transformed Expression
                 */
                @Override
                Expression transform (Expression expressionToTransform) {
                    Expression.LetNonRec lnr = expressionToTransform.asLetNonRec();
                    if (lnr != null) {
                
                        QualifiedName varName = QualifiedName.make(moduleName, lnr.getDefn().getVar());
                        referenceCounter.reset(varName);
                        lnr.getBody().walk(referenceCounter);
                        
                        if (referenceCounter.count == 1) {
                            // Since this variable is only referenced once in the body
                            // we can in-line its definition.
                            Expression letVarDef = lnr.getDefn().getExpr();
                            VarInliner varInliner = new VarInliner(varName, letVarDef);
                            Expression body = lnr.getBody();
                            body.walk(varInliner);
                            body = varInliner.transform(body);
                            
                            return body;
                        }
                    } 
                    
                    return expressionToTransform;
                    
                }
                
                /**
                 * Visitor which walks an Expression counting references to 
                 * a named variable.
                 * @author rcypher
                 */
                final class ReferenceCounter extends Visitor {
                    private QualifiedName varName;
                    private int count;
                    
                    void reset (QualifiedName varName) {
                        this.varName = varName;
                        count = 0;
                    }
                    
                    @Override
                    void enterVar (Expression.Var var) {
                        if (var.getName().equals(varName)) {
                            count++;
                        }
                    }
                    
                }
                
                /**
                 * Class to walk the expression tree substituting variable definitions for
                 * variable references wherever possible.
                 */
                final class VarInliner extends PreOrderTransformVisitor {
                    // Name of the variable being inlined.
                    private final QualifiedName varNameToInline;
                    
                    // Definition of the variable being inlined.
                    private final Expression VarDefToInline;
                    
                    VarInliner (QualifiedName varNameToInline, Expression varDefToInline) {
                        this.varNameToInline = varNameToInline;
                        this.VarDefToInline = varDefToInline;
                    }
                    
                    // If the expression is the variable being inlined return the
                    // definition.  Otherwise return the original expression.
                    @Override
                    Expression transform (Expression expr) {
                        if (expr.asVar() != null) {
                            Expression.Var var = expr.asVar();
                            if (varNameToInline.equals(var.getName())) {
                                expr = VarDefToInline;
                            }
                        }
                        
                        return expr;
                    }
                }

            }
            
            // Apply the let variable in-lining transformation to the main
            // function and all the lifted let variable definition functions.
            LetVarInLiner letVarInliner = new LetVarInLiner(module.getName());
            e.walk(letVarInliner);
            e = letVarInliner.transform(e);
            
            for (final LiftedLetVarInfo llvi : liftedLetVarInfo) {                
                letVarInliner = new LetVarInLiner(module.getName());
                llvi.expression.walk(letVarInliner);
                llvi.expression = letVarInliner.transform(llvi.expression);
            }            
            
            // Encapsulate the transformed main function expression and the
            // lifted let variable definition functions and return.
            return new LiftedLetVarResults(e, liftedLetVarInfo);
        }
        
        /**
         * Converts top level and/or into and if-then-else under certain conditions.  
         * The conversion occurs if the right hand side of the and/or is  tail recursive
         * or contains a call to a closely connected component.  This will expose tail recursive calls
         * for optimization and avoid space usage issues.
         * 
         * @param e
         * @return the transformed expression.
         */
        private Expression changeTopLevelIfThenElse (Expression e) {
            if (e.asAppl() != null) {
                
                // Check to see if this is a fully saturate application of
                // Prelude.if
                Expression[] chain = appChain (e);
                if (chain == null) {
                    return e;
                }
                
                QualifiedName varName = chain[0].asVar().getName();
        
                if (chain.length == 4 && varName.equals(ifName)) {
                    Expression condition = chain[1];
                    Expression thenPart = changeTopLevelIfThenElse(chain[2]);
                    Expression elsePart = changeTopLevelIfThenElse(chain[3]);

                    Expression.Switch.SwitchAlt alts[] = new Expression.Switch.SwitchAlt.Matching[2];
                    alts[0] = new Expression.Switch.SwitchAlt.Matching(Boolean.TRUE, Collections.<FieldName, String>emptyMap(), thenPart);
                    alts[1] = new Expression.Switch.SwitchAlt.Matching(Boolean.FALSE, Collections.<FieldName, String>emptyMap(), elsePart);
                    Expression.Switch newSwitch = new Expression.Switch(condition, alts, null);
                    return newSwitch;
                }
                
            } else if (e.asLet() != null) {
                Expression.Let let = e.asLet();
                let.setBody(changeTopLevelIfThenElse (let.getBody()));
            } else if (e.asSwitch() != null) {
                Expression.Switch sw = e.asSwitch();
                Expression.Switch.SwitchAlt alts[] = sw.getAlts();
                for (int i = 0; i < alts.length; ++i) {
                    alts[i].setAltExpr(changeTopLevelIfThenElse(alts[i].getAltExpr()));
                }
            }
            
            return e;
        }        
    }
    
    /**
     * Structure used to describe a lifted let variable
     * definition function.
     * @author rcypher
     */
    public static class LiftedLetVarInfo {
        /** Name of the lifted function. */
        private final String functionName;
        
        /** Qualified name of the lifted function. */
        private final QualifiedName functionQualifiedName;
        
        /** Arity of the lifted function. */ 
        private final int arity;
        
        /** Paramter names of the lifted function. */
        private final String[] parameterNames;
        
        /** Parameter types of the lifted function. */
        private final TypeExpr[] parameterTypes;
        
        /** Parameter strictness for the lifted function. */
        private final boolean[] parameterStrictness;
        
        /** Result type of the lifted function. */
        private final TypeExpr resultType;
        
        /** Body of the lifted function. */
        private Expression expression;
        
        LiftedLetVarInfo (
                ModuleName moduleName,
                String functionName,
                int arity,
                String[] parameterNames,
                TypeExpr[] parameterTypes,
                boolean[] parameterStrictness,
                TypeExpr resultType,
                Expression expression) {
            this.functionName = functionName;
            this.functionQualifiedName = QualifiedName.make(moduleName, functionName);
            this.arity = arity;
            this.parameterNames = parameterNames;
            this.parameterTypes = parameterTypes;
            this.expression = expression;
            this.resultType = resultType;
            this.parameterStrictness = parameterStrictness;
        }

        public String getFunctionName() {
            return functionName;
        }
        
        public QualifiedName getFunctionQualifiedName () {
            return functionQualifiedName;
        }

        public int getArity() {
            return arity;
        }

        public String[] getParameterNames() {
            return parameterNames;
        }

        public Expression getExpression() {
            return expression;
        }

        public TypeExpr[] getParameterTypes() {
            return parameterTypes;
        }

        public boolean[] getParameterStrictness () {
            return parameterStrictness;
        }
        
        public TypeExpr getResultType() {
            return resultType;
        }
    }
    
    /**
     * This class is used to move let variable bindings into the RHS of 
     * other let variable bindings.
     * @author rcypher
     */
    private static class LetVarRescoper {

        /** 
         * A class used to hold information about the usage of a let variable.
         * @author rcypher
         */
        private static final class LetVarUseInfo {
            /** The name of the let variable. */
            private String varName;
            
            /** The total number of times the let variable is referenced. */
            private int useCount = 0;
            
            /** The number of times the let variable is referenced in the definition of a let variable. */
            private int useInLetNonRecDefinition = 0;
            
            /** The number of references in the definitions of the named letNonRec variables. */
            private Map /* String -> Integer */<String, Integer> letNonRecVarNameUseCounts = new HashMap<String, Integer>();
            
            /** 
             * If the let variable named in 'varName' is defined in the definition of a 
             * LetNonRec variable value this field will hold the name of that LetNonRec variable.
             * Otherwise it will be null.
             */
            private String definedInLetNonRec;
            
            LetVarUseInfo (String varName, String definedIn) {
                if (varName == null) {
                    throw new NullPointerException ("Null varName value in FreeVariableInfo constructor.");
                }
                this.varName = varName;
                this.definedInLetNonRec = definedIn;
            }
            
            void incrementUseCount (String containingLetVar) {
                useCount++;
                if (containingLetVar != null) {
                    useInLetNonRecDefinition++;
                    Integer vCount = letNonRecVarNameUseCounts.get(containingLetVar);
                    if (vCount == null) {
                        vCount = Integer.valueOf(1);
                    } else {
                        vCount = Integer.valueOf(vCount.intValue() + 1);
                    }
                    
                    letNonRecVarNameUseCounts.put(containingLetVar, vCount);
                }
            }
            
            String getVarName()  {
                return varName;
            }
            int getUseCount() {
                return useCount;
            }
            int getUseCountInLetNonRecVarDefs() {
                return useInLetNonRecDefinition;
            }
            Map<String, Integer> useCountsInLetNonRecVarDefs() {
                return Collections.unmodifiableMap(letNonRecVarNameUseCounts);
            }
        }
        
        
        /**
         * A post order transform visitor derivative used specifically
         * to move letNonRec variables.  This performs the transformation:
         *     let
         *         x = A;
         *         y = B;
         *     in
         *         C;
         * To:
         *     let
         *         y = let x = A; in B;
         *     in
         *         C;
         * Where the only reference to x is in B.
         */
        static class LetVarBindingToRHSofLetVarBinding extends PostOrderTransformVisitor {

            /** Map of (String -> LetVarUseInfo).  Usage info for all letvars in the expression. */
            Map<String, LetVarUseInfo> letVarUseInfo; 
            
            /** String (variable name) -> Expression.LetNonRec.  LetNonRec variables to be relocated. */
            Map<String, LetNonRec> letVarsToMove = new HashMap<String, LetNonRec>();  
            
            /** Set of String. Names of moved non-recursive let variables. */
            Set<String> movedLetVars = new HashSet<String>(); 
            
            LetVarBindingToRHSofLetVarBinding (Map<String, LetVarUseInfo> letVarUseInfo) {
                this.letVarUseInfo = letVarUseInfo;
            }
            
            /**
             * When we enter a LetNonRec we want to check the let
             * variable usage info to determine if this is one of the
             * let variables to be moved.  If it is we should add it
             * to the map 'letVarsToMove'.
             * @param lnr
             */
            @Override
            public void enterLetNonRec (Expression.LetNonRec lnr) {
                // If this let var needs to be shifted add it to the
                // movedLetVars.
                LetVarUseInfo lvui = letVarUseInfo.get(lnr.getDefn().getVar());
                if (lvui.getUseCount() == lvui.getUseCountInLetNonRecVarDefs() &&
                    lvui.useCountsInLetNonRecVarDefs().size() == 1) {
                    letVarsToMove.put(lnr.getDefn().getVar(), lnr);
                }
                super.enterLetNonRec(lnr);
            }
            
            /** 
             * Upon exiting a LetNonRec we apply the transformations in the usual
             * post-order fashion.  Then we do an additional change which is to
             * move into the let variable value definition any let variables
             * that are only used in that definition.
             * @param let
             */
            @Override
            public void exitLetNonRec(Expression.LetNonRec let){
                Expression.Let.LetDefn letDef = let.getDefn();
                letDef.setExpr(transform (letDef.getExpr()));
                
                let.setBody(transform(let.getBody()));
                
                // If there are any vars in the movedLetVars that need to be moved
                // to this vars definition.
                for (final String varToMoveName : letVarsToMove.keySet()) {
                    
                    LetVarUseInfo lvui = letVarUseInfo.get(varToMoveName);
                    if (lvui.useCountsInLetNonRecVarDefs().get(let.getDefn().getVar()) != null &&
                        !let.getDefn().getVar().equals(lvui.definedInLetNonRec)) {
                        Expression.LetNonRec mlnr = letVarsToMove.get(varToMoveName);
                        Expression.LetNonRec newLet = new Expression.LetNonRec(mlnr.getDefn(), let.getDefn().getExpr());
                        let.getDefn().setExpr(newLet);
                        // Be sure to add the name of the moved variable to the set of moved variables
                        movedLetVars.add(mlnr.getDefn().getVar());
                    }
                }
            }
            
            /**
             * The transformation is quite simple.
             * It only applies to LetNonRec expressions.  
             * Since we are doing the transformation post-order
             * we can look at the moved variable set.  If this
             * variable has been moved we want to simply return the body.
             * @param expressionToTransform
             * @return the transformed expression
             */
            @Override
            Expression transform (Expression expressionToTransform) {
                Expression.LetNonRec lnr = expressionToTransform.asLetNonRec();
                if (lnr == null) {
                    return expressionToTransform;
                }
                
                if (movedLetVars.contains(lnr.getDefn().getVar())) {
                    return lnr.getBody();
                }
                
                return lnr;
                
            }
        }
        
        /**
         * Finds the referenced variables in the expression and returns
         * a set of qualified names. 
         * @param e
         * @return a Map (String -> LetVarUseInfo).
         */
         private Map<String, LetVarUseInfo> getLetVarUseInfo (Expression e) {
            
            /**
             * A class for determining usage of locally defined
             * let variables.
             */ 
            final class LetVarUseInfoFinder extends Visitor {
                
                Set<String> definedVars = new HashSet<String>();
                Map<String, LetVarUseInfo> referencedVars = new HashMap<String, LetVarUseInfo>();
                ArrayStack<String> containingLetVar = ArrayStack.make();
                
                LetVarUseInfoFinder () {
                    containingLetVar.push(null);
                }
                
                @Override
                void enterVar(Expression.Var var) {
                    String varName = var.getName().getUnqualifiedName();
                    if (definedVars.contains(varName)) {
                        LetVarUseInfo lvui = referencedVars.get(varName);
                        lvui.incrementUseCount(containingLetVar.peek());
                    }
                }
                
                @Override
                void enterLetNonRec(Expression.LetNonRec let){
                    definedVars.add(let.getDefn().getVar());
                    referencedVars.put(let.getDefn().getVar(), new LetVarUseInfo(let.getDefn().getVar(), containingLetVar.peek()));
                }
                
                @Override
                void enterLetRec(Expression.LetRec let){
                    Expression.Let.LetDefn defns[] = let.getDefns();
                    for (int i = 0; i < defns.length; ++i) {
                        definedVars.add(defns[i].getVar());
                        referencedVars.put(defns[i].getVar(), new LetVarUseInfo(defns[i].getVar(), containingLetVar.peek()));
                    }
                }

                @Override
                void enterLetNonRecDef(Expression.Let.LetDefn letDef) {
                    containingLetVar.push(letDef.getVar());
                }
                @Override
                void exitLetNonRecDef(Expression.Let.LetDefn letDef) {
                    containingLetVar.pop();
                }
            }
             
             LetVarUseInfoFinder vrf = new LetVarUseInfoFinder();
             e.walk(vrf);
             return vrf.referencedVars;
         }                
         
        /**
         * This function transforms an expression by moving letNonRec variables.
         * For example:
         *     let
         *         x = ...;
         *     in
         *         let
         *             y = ... x ...;
         *         in
         *             body;
         * Will be transformed to:
         *     let
         *         y = let x = ...; in ...x...;
         *     in
         *         body;
         * This transformation occurs when all references to x are in the value definition 
         * of the let variable y and both x and y are non-recursive let variables.                    
         * @param e
         * @return the transformed expression.
         */
        private Expression rescopeLetVariables (Expression e) {
            
            Map<String, LetVarUseInfo> letVarUseInfo = getLetVarUseInfo(e);
            LetVarBindingToRHSofLetVarBinding lvr = new LetVarBindingToRHSofLetVarBinding(letVarUseInfo);
            e.walk(lvr);
            e = lvr.transform(e);
            
            
            return e;
        }
    }    
    /**
     * Determine if the given expression contains a tail recursive call.
     * Note: This simply looks for an instance of Expression.TailRecursiveCall which
     * has been placed in the expression graph by previous transformations.
     * @param e
     * @return true if e contains tail recursive call
     */
    static boolean isTailRecursive (Expression e) {
        
        class TailRecursionFinder extends Visitor {
            boolean tailRecursive = false;
            @Override
            public void enterTailRecursiveCall (Expression.TailRecursiveCall rc) {
                tailRecursive = true;
            }
        }
        TailRecursionFinder rf = new TailRecursionFinder();
        e.walk(rf);
        return rf.tailRecursive;
    }    
    
    /**
     * Determines if the given expression (e) is dependent on
     * the named variable.
     * @param e
     * @param varName
     * @return true if e references var
     */
    static boolean isDependentOn (Expression e, QualifiedName varName) {
        class IsDependent extends Visitor {
            private final QualifiedName varName;
            boolean dependent = false;
            IsDependent (QualifiedName varName) {
                this.varName = varName;
            }
            
            @Override
            void enterVar (Expression.Var var) {
                if (varName.equals(var.getName())) {
                    dependent = true;
                }
            }
        }
        
        IsDependent vc = new IsDependent (varName);        
        e.walk(vc);
        
        return vc.dependent;
    }

    /**
     * For each function in the module determine the strongly connected components
     * and update the information in the MachineFunction.
     * @param moduleName
     * @param nameToExpressionMap
     * @return a list of Set objects where each set represents a set of string connected components.
     */
    static List<Set<String>> determineStronglyConnectedComponents (ModuleName moduleName, Map<String, Expression> nameToExpressionMap) {
        class DependsOn extends Visitor {
            private final Set<String> dependees = new HashSet<String>();
            private final ModuleName moduleName;
            private final String thisFunctionName;
            DependsOn (ModuleName moduleName, String thisFunctionName) {
                this.moduleName = moduleName;
                this.thisFunctionName = thisFunctionName;
            }
            
            Set<String> getDependees() {
                return dependees;
            }
            @Override
            void enterVar (Expression.Var var) {
                QualifiedName varName = var.getName();
                if (varName.getModuleName().equals(moduleName) 
                    && !varName.getUnqualifiedName().equals(thisFunctionName)) {
                    dependees.add(varName.getUnqualifiedName());
                }
            }
        }

        VertexBuilderList<String> vbs = new VertexBuilderList<String>();       
        
        for (final Map.Entry<String, Expression> entry : nameToExpressionMap.entrySet()) {
            String name = entry.getKey();
            Expression e = entry.getValue();
            if (e == null) {
                continue;
            }
            DependsOn dd = new DependsOn (moduleName, name);
            e.walk(dd);            
            vbs.add(new VertexBuilder<String>(name, dd.getDependees()));
        }
        
        Graph<String> g = new Graph<String>(vbs);
        g = g.calculateStronglyConnectedComponents();
        
        List<Set<String>> connectionSets  = new ArrayList<Set<String>>();
        for (int i = 0; i < g.getNStronglyConnectedComponents(); ++i) {
            Graph<String>.Component component = g.getStronglyConnectedComponent(i);
            
            // Use a LinkedHashSet so that the order of iteration will
            // always be consistent.
            Set<String> s = new LinkedHashSet<String>();
            for (int j = 0; j < component.size(); ++j) {
                Vertex<String> v = component.getVertex(j);
                s.add(v.getName());
            }
            connectionSets.add(s);
        }
        
        return connectionSets;
    }
           
    static class Visitor {

        void enterAppl(Expression.Appl appl){}
        void exitAppl(Expression.Appl appl){}
        void enterLetNonRec(Expression.LetNonRec let){}
        void exitLetNonRec(Expression.LetNonRec let){}
        void enterLetRec(Expression.LetRec let){}
        void exitLetRec(Expression.LetRec let){}
        void enterLetNonRecDef(Expression.Let.LetDefn letDef){}
        void exitLetNonRecDef(Expression.Let.LetDefn letDef){}
        void enterLetRecDef(Expression.Let.LetDefn letDef){}
        void exitLetRecDef(Expression.Let.LetDefn letDef){}
        void enterLiteral(Expression.Literal lit){}
        void exitLiteral(Expression.Literal lit){}
        void enterPackCons(Expression.PackCons packCons){}
        void exitPackCons(Expression.PackCons packCons){}
        void enterRecordCase(Expression.RecordCase recordCase){}
        void exitRecordCase(Expression.RecordCase recordCase){}
        void enterRecordUpdate(Expression.RecordUpdate recordUpdate){}
        void exitRecordUpdate(Expression.RecordUpdate recordUpdate){}        
        void enterRecordExtension(Expression.RecordExtension recordExtension){}
        void exitRecordExtension(Expression.RecordExtension recordExtension){}
        void enterRecordSelection(Expression.RecordSelection recordSelection){}
        void exitRecordSelection(Expression.RecordSelection recordSelection){}
        void enterTailRecursiveCall (Expression.TailRecursiveCall rc) {}
        void exitTailRecursiveCall (Expression.TailRecursiveCall rc) {}
        void enterSwitch(Expression.Switch s){}
        void exitSwitch(Expression.Switch s){}
        void enterVar(Expression.Var var){}
        void exitVar(Expression.Var var){}
        void enterSwitchAlt (Expression.Switch.SwitchAlt alt){}
        void exitSwitchAlt (Expression.Switch.SwitchAlt alt){}
        void enterDataConsSelection(Expression.DataConsSelection dataConsSelection){}
        void exitDataConsSelection(Expression.DataConsSelection dataConsSelection){}
        void enterCast(Expression.Cast cast) {}
        void exitCast(Expression.Cast cast) {}
    }
    
    private static abstract class PreOrderTransformVisitor extends Visitor {
        abstract Expression transform (Expression e);
        
        @Override
        public void enterAppl(Expression.Appl appl){
            appl.setE1 (transform (appl.getE1()));
            appl.setE2 (transform (appl.getE2()));
        }
        @Override
        public void enterLetNonRec(Expression.LetNonRec let){
            Expression.Let.LetDefn letDef = let.getDefn();
            letDef.setExpr(transform (letDef.getExpr()));
            
            let.setBody(transform(let.getBody()));
        }
        @Override
        public void enterLetRec(Expression.LetRec let){
            Expression.Let.LetDefn[] defs = let.getDefns();
            for (int i = 0; i < defs.length; ++i) {
                defs[i].setExpr (transform (defs[i].getExpr()));
            }
            let.setBody(transform(let.getBody()));
        }
        @Override
        public void enterRecordCase(Expression.RecordCase recordCase){
            recordCase.setConditionExpr (transform (recordCase.getConditionExpr()));
            recordCase.setResultExpr (transform (recordCase.getResultExpr()));
        }
        @Override
        public void enterRecordUpdate(Expression.RecordUpdate recordUpdate){
            Expression e = recordUpdate.getBaseRecordExpr();           
            recordUpdate.setBaseRecordExpr(transform (e));
                       
            for (final Map.Entry<FieldName, Expression> entry : recordUpdate.getUpdateFieldValuesMap().entrySet()) {
                
                FieldName fieldName = entry.getKey();
                Expression valueExpr = entry.getValue();
                recordUpdate.setFieldValueUpdate(fieldName, transform (valueExpr));
            }
        }        
        @Override
        public void enterRecordExtension(Expression.RecordExtension recordExtension){
            Expression e = recordExtension.getBaseRecordExpr();
            if (e != null) {
                recordExtension.setBaseRecordExpr(transform (e));
            }
           
            for (final Map.Entry<FieldName, Expression> entry : recordExtension.getExtensionFieldsMap().entrySet()) {
                
                FieldName fieldName = entry.getKey();
                Expression valueExpr = entry.getValue();
                recordExtension.setFieldExtension(fieldName, transform (valueExpr));
            }
        }
        @Override
        public void enterRecordSelection(Expression.RecordSelection recordSelection){
            recordSelection.setRecordExpr (transform (recordSelection.getRecordExpr()));
        }
        @Override
        public void enterSwitch(Expression.Switch s){
            s.setSwitchExpr (transform (s.getSwitchExpr()));
        }
        @Override
        public void enterSwitchAlt(Expression.Switch.SwitchAlt alt) {
            alt.setAltExpr(transform(alt.getAltExpr()));
        }
        @Override
        public void enterDataConsSelection(Expression.DataConsSelection dataConsSelection){
            dataConsSelection.setDCValueExpr(transform(dataConsSelection.getDCValueExpr()));
        }
        @Override
        public void enterTailRecursiveCall(Expression.TailRecursiveCall trc) {
            Expression arguments[] = trc.getArguments();
            for (int i = 0; i < arguments.length; ++i) {
                arguments[i] = transform (arguments[i]);
            }
            trc.setArguments(arguments);
        }
    }

    private static abstract class PostOrderTransformVisitor extends Visitor {
        abstract Expression transform (Expression e);
        
        @Override
        public void exitAppl(Expression.Appl appl){
            appl.setE1 (transform (appl.getE1()));
            appl.setE2 (transform (appl.getE2()));
        }
        @Override
        public void exitLetNonRec(Expression.LetNonRec let){
            Expression.Let.LetDefn letDef = let.getDefn();
            letDef.setExpr(transform (letDef.getExpr()));
            
            let.setBody(transform(let.getBody()));
        }
        @Override
        public void exitLetRec(Expression.LetRec let){
            Expression.Let.LetDefn[] defs = let.getDefns();
            for (int i = 0; i < defs.length; ++i) {
                defs[i].setExpr (transform (defs[i].getExpr()));
            }
            let.setBody(transform(let.getBody()));
        }
        @Override
        public void exitRecordCase(Expression.RecordCase recordCase){
            recordCase.setConditionExpr (transform (recordCase.getConditionExpr()));
            recordCase.setResultExpr (transform (recordCase.getResultExpr()));
        }
        @Override
        public void exitRecordUpdate(Expression.RecordUpdate recordUpdate){
            Expression e = recordUpdate.getBaseRecordExpr();                                       
            recordUpdate.setBaseRecordExpr(transform (e));            
           
            for (final Map.Entry<FieldName, Expression> entry : recordUpdate.getUpdateFieldValuesMap().entrySet()) {
                
                FieldName fieldName = entry.getKey();
                Expression valueExpr = entry.getValue();
                recordUpdate.setFieldValueUpdate(fieldName, transform (valueExpr));
            }
        }        
        @Override
        public void exitRecordExtension(Expression.RecordExtension recordExtension){
            Expression e = recordExtension.getBaseRecordExpr();
            if (e != null) {
                recordExtension.setBaseRecordExpr(transform (e));
            }
           
            for (final Map.Entry<FieldName, Expression> entry : recordExtension.getExtensionFieldsMap().entrySet()) {
                
                FieldName fieldName = entry.getKey();
                Expression valueExpr = entry.getValue();
                recordExtension.setFieldExtension(fieldName, transform (valueExpr));
            }
        }
        @Override
        public void exitRecordSelection(Expression.RecordSelection recordSelection){
            recordSelection.setRecordExpr (transform (recordSelection.getRecordExpr()));
        }
        @Override
        public void exitSwitch(Expression.Switch s){
            s.setSwitchExpr (transform (s.getSwitchExpr()));
        }
        @Override
        public void exitSwitchAlt(Expression.Switch.SwitchAlt alt) {
            alt.setAltExpr(transform(alt.getAltExpr()));
        }
        @Override
        public void exitDataConsSelection(Expression.DataConsSelection dataConsSelection){
            dataConsSelection.setDCValueExpr(transform(dataConsSelection.getDCValueExpr()));
        }
        @Override
        public void exitTailRecursiveCall(Expression.TailRecursiveCall trc) {
            Expression arguments[] = trc.getArguments();
            for (int i = 0; i < arguments.length; ++i) {
                arguments[i] = transform (arguments[i]);
            }
            trc.setArguments(arguments);
        }
    }
    
    
    private static class TailRecursionTransformer {

        /** The MachineFunction object corresponding to the function containing the expression being transformed. */
        MachineFunction function;
        
        // Indicates whether a transformation has occurred.
        boolean changed = false;
        
        TailRecursionTransformer (MachineFunction coreFunction) {
            this.function = coreFunction;
        }
    
    /**
     * Determine if the given expression contains a top level 
     * fully saturated call to the given function.
     * @param e
     * @return true if a tail recursive call
     */
    private boolean isTailRecursive (Expression e) {
        return doTailRecursiveCheck (e);
    }
    
    private boolean doTailRecursiveCheck (Expression e) {
        if (e.asAppl() != null) {
            Expression[] chain = appChain (e);
            if (chain == null) {
                return false;
            }
            
            Expression.Var var = chain[0].asVar ();

            if (var.getForeignFunctionInfo() != null) {
                return false;
            }
            
            if (chain.length == function.getNFormalParameters() + 1 &&
                var.getName().equals(function.getQualifiedName())) {
                return true;
            }
            
            QualifiedName varName = var.getName();
            
            // Check to see if this is a conditional.
            if (chain.length == 4 && varName.equals(ifName)) {
                return doTailRecursiveCheck (chain[2]) || doTailRecursiveCheck (chain[3]);
            }
            
            // Check to see if this is 'a' && tail recursive, 'a' || tail recursive, seq a tail recursive.
            if (chain.length == 3 && 
                (varName.equals(CAL_Prelude.Functions.and) || 
                 varName.equals(CAL_Prelude.Functions.or) || 
                 varName.equals(CAL_Prelude.Functions.seq))) {
                return doTailRecursiveCheck (chain[2]);
            }
        } else if (e.asLetNonRec() != null) {
            Expression.LetNonRec let = e.asLetNonRec();
            return doTailRecursiveCheck (let.getBody());
        } else if (e.asLetRec() != null) {
            Expression.LetRec let = e.asLetRec();
            return doTailRecursiveCheck (let.getBody());
        } else if (e.asSwitch() != null) {
            Expression.Switch sw = e.asSwitch();
            Expression.Switch.SwitchAlt alts[] = sw.getAlts();
            for (int i = 0; i < alts.length; ++i) {
                if (doTailRecursiveCheck (alts[i].getAltExpr())) {
                    return true;
                }
            }
        } else if (e.asRecordCase() != null) {
            Expression.RecordCase rc = e.asRecordCase();
            return doTailRecursiveCheck(rc.getResultExpr());
        }
        
        return false;
    }
    
    Expression transformTailRecursionCalls (Expression e) {
        changed = false;
        return doTailRecursionTransforms (e);
    }
    
    private Expression doTailRecursionTransforms (Expression e) {
        if (e.asAppl() != null) {
            Expression[] chain = appChain (e);
            if (chain == null) {
                return e;
            }

            Expression.Appl app = e.asAppl ();
            QualifiedName varName = chain[0].asVar().getName();
            
            // Check to see if this is an if-then-else.
            if (chain.length == 4 && varName.equals(ifName)) {
                app.setE2 (doTailRecursionTransforms (app.getE2 ()));
                Expression.Appl prev = app.getE1().asAppl();
                if (prev != null) {
                    prev.setE2 (doTailRecursionTransforms (prev.getE2 ()));
                }
            } else
            if (chain.length == 3 && varName.equals(CAL_Prelude.Functions.seq)) {
                app.setE2 (doTailRecursionTransforms (app.getE2 ()));
            } else                
            if (varName.equals(function.getQualifiedName()) && isTailRecursive (e)) {
                // This is a tail recursive call.
                Expression[] arguments = new Expression [function.getNFormalParameters()];
                System.arraycopy (chain, 1, arguments, 0, function.getNFormalParameters());
                Expression.TailRecursiveCall rc = new Expression.TailRecursiveCall (chain[0].asVar(), arguments);
                changed = true;
                return rc;
            }
        
        } else if (e.asLetRec () != null) {
            Expression.LetRec let = e.asLetRec();
            let.setBody(doTailRecursionTransforms (let.getBody()));
        
        } else if (e.asLetNonRec () != null) {
            Expression.LetNonRec let = e.asLetNonRec();
            let.setBody(doTailRecursionTransforms (let.getBody()));
        
        } else if (e.asSwitch() != null) {
            Expression.Switch sw = e.asSwitch();
            Expression.Switch.SwitchAlt alts[] = sw.getAlts();
            for (int i = 0; i < alts.length; ++i) {
                Expression.Switch.SwitchAlt alt = alts[i];
                alt.setAltExpr(doTailRecursionTransforms(alt.getAltExpr()));
            }
        
        } else if (e.asRecordCase() != null) {
            Expression.RecordCase rc = e.asRecordCase();
            rc.setResultExpr(doTailRecursionTransforms(rc.getResultExpr()));
        }
        
        return e;
    }
}



    /**
     * Convert an application chain to an array.
     * @param root
     * @return null if this is not a fully saturated SC application, array of Expression otherwise.
     */
    static private Expression[] appChain (Expression root) {
        // Walk down the left branch.
        Expression c = root;
        int nArgs = 0;
        while (c instanceof Expression.Appl) {
            nArgs++;
            c = ((Expression.Appl)c).getE1();
        }
        
        // At this point c should be an Expression.Var
        if (!(c instanceof Expression.Var)) {
            return null;
        }
        
        Expression[] chain = new Expression [nArgs + 1];
        chain[0] = c;
        c = root;
        for (int i = nArgs; i >= 1; i--) {
            chain[i] = ((Expression.Appl)c).getE2();
            c = ((Expression.Appl)c).getE1();
        }

        return chain;
    }

    /**
     * Build a list of all the Expression.Literal objects in the given expression.
     * @param e
     * @return a list of Expression.Literal objects.
     */
    static List<Expression.Literal> literals (Expression e) {
        class LiteralCollector extends Visitor {
            private List<Expression.Literal> literals = new ArrayList<Expression.Literal>();
            @Override
            void enterLiteral(Expression.Literal lit){
                literals.add (lit);
            }
            List<Expression.Literal> getLiterals () {
                return literals;
            }
        }
        
        LiteralCollector collector = new LiteralCollector();        
        e.walk(collector);
        
        return collector.getLiterals();
    }
    
    /**
     * This method takes an expression and walks across the
     * expression setting the appropriate use count into any let variable
     * definitions. 
     * @param e
     */
    private static void letVariableUse(Expression e) {
        
        // A visitor class for counting let variable usage.  The compiler guarantees that
        // let variables will have unique names.
        class VarUseCounter extends Visitor {
            
            // Map of String -> Integer.  Let variable name -> reference count.
            Map<String, LetDefn> varNamToDef = new HashMap<String, LetDefn>();
            
            @Override
            public void enterVar(Expression.Var var) {
                // If this is a let variable in the counts map increment the reference count.
                String varName = var.getName().getUnqualifiedName();
                LetDefn letDef = varNamToDef.get(varName);
                if (letDef != null) { 
                    letDef.incrementUseCount();
                }
            }
            @Override
            public void enterLetNonRec(Expression.LetNonRec let) {
                // Add the let variable to the counts map with a reference count of zero.
                varNamToDef.put (let.getDefn().getVar(), let.getDefn());
                let.getDefn().setUseCount(0);
            }
            
            @Override
            public void enterLetRec(Expression.LetRec let) {
                // Add the let variables to the counts map with a reference count of zero.
                Expression.Let.LetDefn defs[] = let.getDefns();
                for (int i = 0; i < defs.length; ++i) {
                    varNamToDef.put(defs[i].getVar(), defs[i]);
                    defs[i].setUseCount(0);
                }
            }
            
        }
    
        // Create a VarUseCounter and walk it across the expression.
        VarUseCounter vc = new VarUseCounter();        
        e.walk(vc);
        
    }

    /**
     * Determine if an application chain is an application of a dictionary function to the correct number of
     * arguments to fully resolve the returned class method.  
     * Ex. Prelude.add x y z is oversaturated since add takes a single argument and resolves to addInt, addDouble, etc.  
     * If we know that the returned function is of arity two we see that it will be fully saturated and we can
     * optimize the reduction/representation of the oversaturated application.   
     * @param e
     * @param module
     * @return number of arguments extra to the dictionary, or -1 if e is not an oversaturated dictionary application.
     */
    public static int getNOversaturatedDictionaryArgs (Expression e, ModuleTypeInfo module) {
        if (e.asAppl () == null) {
            return -1;
        }
        
        // First we need to determine if the left hand side of the application is dictionary
        Expression[] chain = appChain (e);
        if (chain[0].asVar() == null) {
            return -1;
        }

        Expression.Var var = chain[0].asVar();
        if (var.getName().getUnqualifiedName().startsWith("$dictvar")) {

            // Parse out the type class name.
            // Variable name will be in the form $dictvarModule.Class#N where N is an integer
            // number.
            String name = var.getName().getUnqualifiedName();
            name = name.substring(8);
            
            QualifiedName qualifiedClassName = QualifiedName.makeFromCompoundName(name.substring(0, name.indexOf("#")));
            
            ModuleName moduleName = qualifiedClassName.getModuleName();
            String className = qualifiedClassName.getUnqualifiedName(); 

            // Switch to the correct module for the type class.
            if (!module.getModuleName().equals(moduleName)) {
                module = module.getImportedModule(moduleName);
            }
            
            if (module == null) {
                return -1;
            }
            
            // Get the type class.
            TypeClass typeClass = module.getTypeClass(className);
            if (typeClass == null) {
                return -1;
            }

            // Get the class method in question.
            ClassMethod cm = null;
            int nExtraArgs = chain.length - 1;
            if (typeClass.internal_isSingleMethodRootClass()) {
                cm = typeClass.getNthClassMethod(0);
            } else {
                if (chain[1].asLiteral() == null || !(chain[1].asLiteral().getLiteral() instanceof Integer)) {
                    return -1;
                }
                nExtraArgs--;
                int dictIndex = ((Integer)chain[1].asLiteral().getLiteral()).intValue();
                for (int i = 0; i < typeClass.getNClassMethods(); ++i) {
                    if (typeClass.getNthClassMethod(i).internal_getDictionaryIndex() == dictIndex) {
                        cm = typeClass.getNthClassMethod(i);
                        break;
                    }
                }
            }
            
            if (cm == null) {
                return -1;
            }
            
            // return the expected number of extra arguments.
            TypeExpr te = cm.getTypeExpr();
            int nExpectedExtraArgs = te.getArity();
            if (nExtraArgs == nExpectedExtraArgs) {
                return nExpectedExtraArgs;
            }
            
        }
        
        return -1;
    }

    /**
     * Transform an expression by removing any function references which are
     * tagged as being an alias of another function and replacing them with
     * a reference to the other function.
     * Also transorm function references which are tagged as being equivalent to 
     * a literal value.
     * @param function the function to transform
     * @param m the containing module
     * @param logger
     */
    static void antiAlias (MachineFunction function, Module m, CompilerMessageLogger logger) {
        
        // This transform visitor simply substitues one Expression.Var for another if 
        // the first var is an alias of something else.
        class AATransformVisitor extends PreOrderTransformVisitor {
            private final Module module;
            private final MachineFunction function;
            private final CompilerMessageLogger logger;
            
            AATransformVisitor (MachineFunction function, Module m, CompilerMessageLogger logger) {
                this.module = m;
                this.function = function;
                this.logger = logger;
            }
            
            @Override
            Expression transform (Expression e) {
                if (e.asVar() != null) {
                    Expression.Var var = e.asVar();
                    MachineFunction mf = module.getFunction(var.getName());
                    // Determine if the var is an alias for another function.
                    if (mf != null) {
                        if (mf.getAliasOf() != null) {
                            QualifiedName aliasOf = mf.getAliasOf();
                            Module aliasModule = module.findModule(aliasOf.getModuleName());
                            
                            if (aliasModule != null) {
                                ModuleTypeInfo aliasModuleTypeInfo = aliasModule.getModuleTypeInfo();
                                
                                // Need to create a new Expression.Var to substitute.
                                // NOTE: We have to handle Prelude.if as a special case because there is no entity for it
                                // in the type info.
                                if (aliasModuleTypeInfo != null) {
                                    FunctionalAgent functionalAgent = aliasModuleTypeInfo.getFunctionalAgent(aliasOf.getUnqualifiedName());
                                    if (functionalAgent != null) {
                                        //System.out.println ("substituting " + aliasOf + " for " + var.getName());                        
                                        return new Expression.Var (functionalAgent);
                                    } else {
                                        return new Expression.Var (aliasOf);
                                    }
                                } else {
                                    logger.logMessage(new CompilerMessage(new MessageKind.Error.UnableToResolveFunctionAlias(function.getQualifiedName().getQualifiedName(), var.getName().getQualifiedName(), aliasOf.getQualifiedName())));
                                }
                            } else {
                                logger.logMessage(new CompilerMessage(new MessageKind.Error.UnableToResolveFunctionAlias(function.getQualifiedName().getQualifiedName(), var.getName().getQualifiedName(), aliasOf.getQualifiedName())));
                            }
                        } else
                        if (mf.getLiteralValue() != null) {
                            return new Expression.Literal(mf.getLiteralValue());
                        }
                    }
                }
                return e;
            }
        }
        
        Expression e = function.getExpressionForm();
        
        // Walk the expression doing any substitution for aliased functions.
        AATransformVisitor tv = new AATransformVisitor(function, m, logger);
        e = tv.transform(e);
        e.walk(tv);        
        
        function.setExpression(e);
    }
    
    /**
     * Finds the referenced variables in the expression which are
     * not locally defined and returns a set of qualified names. 
     * @param e
     * @param module
     * @return a set of QualifiedName.
     */
     public static Set<QualifiedName> findReferencedFreeVariables (Expression e, final Module module) {
         if (e == null || module == null) {
             throw new NullPointerException ("Invalid argument value in ExpressionAnalyzer.findReferencedVariables");
         }
         
         final class VariableReferenceFinder extends Visitor {
             /** Set of QualifiedName */             
             Set<QualifiedName> definedVars = new HashSet<QualifiedName>();
             
             /** Set of QualifiedName */
             Set<QualifiedName> referencedVars = new LinkedHashSet<QualifiedName>();
             
             @Override
            void enterVar(Expression.Var var) {
                 // If this is not in the defined vars it is a free var.
                 QualifiedName varName = var.getName();
                 if (!definedVars.contains(varName) &&
                      module.getFunction(varName) == null) {
                     referencedVars.add(varName);
                 }
             }
             @Override
            void enterLetRec(Expression.LetRec letRec) {
                 for (int i = 0; i < letRec.getDefns().length; ++i) {
                     definedVars.add(QualifiedName.make(module.getName(), letRec.getDefns()[i].getVar()));
                 }
             }
             @Override
            void enterLetNonRec (Expression.LetNonRec letNonRec) {
                 definedVars.add(QualifiedName.make(module.getName(), letNonRec.getDefn().getVar()));
             }
             @Override
            void enterSwitchAlt (Expression.Switch.SwitchAlt alt) {
                 if (alt.hasVars()) {
                     String[] names = alt.getVarNames();
                     for (int i = 0; i < names.length; ++i) {
                         definedVars.add(QualifiedName.make(module.getName(), names[i]));
                     }
                 }
             }
            
             @Override
            void enterRecordCase(Expression.RecordCase recordCase){
                 
                 final SortedMap<FieldName, String> fieldBindingVarMap = recordCase.getFieldBindingVarMap();
                 
                 if (fieldBindingVarMap.size() > 0) {

                     for (final Map.Entry<FieldName, String> entry : fieldBindingVarMap.entrySet()) {

                         definedVars.add(QualifiedName.make(module.getName(), entry.getValue()));
                     }
                 }
             }
         }
         
         // A visitor class for finding free variables.  The compiler guarantees that
         // all variables will have unique names.
         VariableReferenceFinder vrf = new VariableReferenceFinder();
         e.walk(vrf);
         return vrf.referencedVars;
     }

     /**
     * @return whether or not the given expression had an unsafe coerce call.
     */
    public boolean getHadUnsafeCoerce(){
         return hadUnsafeCoerce;
     }

 }
