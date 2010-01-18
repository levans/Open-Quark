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
 * DerivedInstanceFunctionGenerator.java
 * Created: May 26, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.SourceModel.LocalDefn;
import org.openquark.cal.compiler.SourceModel.Expr.Case.Alt;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_QuickCheck;



/**
 * Warning- this class should only be used by the CAL compiler implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * Contains various helper functions to generate the source models for 
 * derived instance functions i.e. the hidden functions implementing the instance methods
 * for the hidden instance declaration generating by a "deriving" clause added to
 * a data declaration.
 * <p>
 * Also contains a public helper function {@link #makeAlgebraicTypeInstanceFunctions}
 * for generating instance definitions for a particular type for the classes Eq, Ord and Show, such
 * that these definitions can be copy-and-pasted into a regular module and then compiled. 
 * 
 * @author Bo Ilic
 */
public final class DerivedInstanceFunctionGenerator {
    
    /**
     * Enable this flag to print out the generated function definitions.
     */
    private static final boolean SHOW_FUNCTION_DEFN = false;
    
    static private final SourceModel.Name.DataCons PRELUDE_LT_DATACONS = SourceModel.Name.DataCons.make(CAL_Prelude.DataConstructors.LT);
    static private final SourceModel.Name.DataCons PRELUDE_EQ_DATACONS = SourceModel.Name.DataCons.make(CAL_Prelude.DataConstructors.EQ);
    static private final SourceModel.Name.DataCons PRELUDE_GT_DATACONS = SourceModel.Name.DataCons.make(CAL_Prelude.DataConstructors.GT);
    

    
    /**name of helper function for type safe conversion from enum to int*/
    static final String toIntHelper = "toIntHelper";
    
    /**name of helper function for type safe conversion from int to enum*/    
    static final String fromIntHelper = "fromIntHelper";    
    
    /**name of helper function used in the upfromthen instance*/
    static final String upFromThenToHelperUp = "upFromThenToHelperUp";
 
    /**name of helper function used in the upfromthen instance*/
    static final String upFromThenToHelperDown = "upFromThenToHelperDown";

    /**
     * Whether to use internal names for the names of the constructed instance methods. Note that
     * an internal name is not a valid CAL identifier (as per the grammar), but is allowed as part of the name
     * of internally generated functions.
     */
    private final boolean shouldUseInternalNames;
    
    /**
     * Private constructor for DerivedInstanceFunctionGenerator.
     * @param shouldUseInternalNames whether to use internal names for the names of the constructed instance methods.
     */
    private DerivedInstanceFunctionGenerator(boolean shouldUseInternalNames) {
        this.shouldUseInternalNames = shouldUseInternalNames;
    }
    
    /**
     * Factory method for constructing a DerivedInstanceFunctionGenerator that is for internal use (e.g. by
     * the ClassInstanceChecker).
     * @return a new instance of DerivedInstanceFunctionGenerator.
     */
    static DerivedInstanceFunctionGenerator makeInternalUse() {
        return new DerivedInstanceFunctionGenerator(true);
    }
    
    /**
     * Private factory method for constructing a DerivedInstanceFunctionGenerator that is for external use (i.e.
     * it will not create internal names).
     * @return an external-use instance of DerivedInstanceFunctionGenerator.
     */
    private static DerivedInstanceFunctionGenerator makeExternalUse() {
        return new DerivedInstanceFunctionGenerator(false);
    }
    
    /**
     * If a type M.T has Prelude.Eq in the deriving clause, then this is the definition of the M.$equals$T
     * instance function which implements the Prelude.equals class method for the type T.
     *      
     * @param typeCons
     * @return the source model for the equals instance function. Will be an internal function (i.e. its text
     *   is not parseable as a CAL function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeEqualsInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeEqualsInstanceFunction does not work for foreign types.");
        }
        
        /*
        For example, given
        data Either a b = Left value :: a | Right value :: b deriving Eq;
        the following would get generated: 
        
        //$equals$Either :: (Eq a, Eq b) => Either a b -> Either a b -> Boolean;
        private $equals$Either !x !y =
            case x of
            Left {value = value1} ->
                case y of
                Left {value = value2} -> value1 == value2;
                _ -> False;
                ;
            Right {value = value1} ->
                case y of
                Right {value = value2} -> value1 == value2;
                _ -> False;
                ;
            ;
        */
        return makeEqualityComparisonFunction(typeCons, false);
    }

    /**
     * If a type M.T has Prelude.Eq in the deriving clause, then this is the definition of the M.$notEquals$T instance function
     * which implements the Prelude.notEquals class method for the type T.
     *      
     * @param typeCons
     * @return the source model for the notEquals instance function. Will be an internal function (i.e. its text
     *   is not parseable as a CAL function by the parser).
     */    
    SourceModel.FunctionDefn.Algebraic makeNotEqualsInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeNotEqualsInstanceFunction does not work for foreign types.");
        }
        
        /*
        For example, given
        data Either a b = Left value :: a | Right value :: b deriving Eq;
        the following would get generated:

        //$notEquals$Either :: (Eq a, Eq b) => Either a b -> Either a b -> Boolean;
        private $notEquals$Either !x !y =
            case x of
            Left {value = value1} ->
                case y of
                Left {value = value2} -> value1 != value2;
                _ -> False;
                ;
            Right {value = value1} ->
                case y of
                Right {value = value2} -> value1 != value2;
                _ -> False;
                ;
            ;
        */
        return makeEqualityComparisonFunction(typeCons, true);
    } 
        
    /**
     * For a type M.T has Prelude.Eq in the deriving clause, this method returns
     * either the definition of the M.$equals$T instance function which
     * implements the Prelude.equals class method for the type T, or the
     * definition of the M.$notEquals$T instance function which implements the
     * Prelude.notEquals class method for the type T.
     * 
     * @param typeCons
     * @param makeNotEquals
     *            if true, this method returns the definition for M.$notEquals$T.
     *            Otherwise, it returns the definition for M.$equals$T.
     * @return the source model for the instance function. Will be an internal
     *         function (i.e. its text is not parseable as a CAL function by the
     *         parser).
     */
    private SourceModel.FunctionDefn.Algebraic makeEqualityComparisonFunction(TypeConstructor typeCons, boolean makeNotEquals) {
        //we need to include a type declaration since in certain cases the inferred type of the instance function
        //will be too general (i.e. the case of phantom type variables such as data Foo a b = MakeFoo a; then the inferred
        //type of the equalsFoo/notEqualsFoo function will not involve the type variable b which will result in a compilation error later).
        
        //also note that the arguments of the instance functions are strict.
        
        /*
        For example, given
        data Either a b = Left value :: a | Right value :: b deriving Eq;
        the following would get generated if makeNotEquals is false: 
        
        //$equals$Either :: (Eq a, Eq b) => Either a b -> Either a b -> Boolean;
        private $equals$Either !x !y =
            case x of
            Left {value = value1} ->
                case y of
                Left {value = value2} -> value1 == value2;
                _ -> False;
                ;
            Right {value = value1} ->
                case y of
                Right {value = value2} -> value1 == value2;
                _ -> False;
                ;
            ;

        if makeNotEquals is true, then this would be generated:

        //$notEquals$Either :: (Eq a, Eq b) => Either a b -> Either a b -> Boolean;
        private $notEquals$Either !x !y =
            case x of
            Left {value = value1} ->
                case y of
                Left {value = value2} -> value1 != value2;
                _ -> False;
                ;
            Right {value = value1} ->
                case y of
                Right {value = value2} -> value1 != value2;
                _ -> False;
                ;
            ;

        */
        
        String functionName = makeInstanceFunctionUnqualifiedName(makeNotEquals ? "notEquals" : "equals", typeCons);
        
        SourceModel.Parameter[] parameters = makeTwoStrictParameters();
        
        SourceModel.Expr definingExpr;
        
        final int nDataCons = typeCons.getNDataConstructors();
        
        if (nDataCons == 1 && typeCons.getNthDataConstructor(0).getArity() == 0) {
            
            // Optimization:
            //
            // For the special case where there is only one data constructor and
            // its arity is 0, (e.g. a type like Prelude.Unit), there is only
            // one unique value for the entire type, and hence any two values of
            // such a type will always be equal.
            //
            // In this case, the function should simply return the result value
            // associated with the two arguments being equal.
            //
            // For example, given:
            //
            //  data Unit = Unit deriving Prelude.Eq, Prelude.Ord;
            //
            // we generate these two equality comparison functions:
            //
            //  private $equals$Unit !x !y = Prelude.True;
            //  private $notEquals$Unit !x !y = Prelude.False;
            //
            // This optimization is valid because the arguments x and y are strict.
            //
            
            definingExpr = SourceModel.Expr.makeBooleanValue(!makeNotEquals);
            
        } else {
            SourceModel.Expr.Case.Alt[] outerCaseAlts = new SourceModel.Expr.Case.Alt[nDataCons];
            
            for (int i = 0; i < nDataCons; ++i) {
                
                DataConstructor dataCons = typeCons.getNthDataConstructor(i);
                final int dataConsArity = dataCons.getArity();
                
                SourceModel.Name.DataCons dataConsName = SourceModel.Name.DataCons.make(dataCons.getName());
                
                SourceModel.FieldPattern[] outerPatterns = makeFieldPatterns(dataCons, "1");
                
                SourceModel.Expr.Case innerCaseExpr;
                {
                    final int nInnerAlts = nDataCons == 1? 1 : 2;
                    SourceModel.Expr.Case.Alt[] innerCaseAlts = new SourceModel.Expr.Case.Alt[nInnerAlts];
                    
                    //initialize innerCaseAlts[0]
                    
                    SourceModel.FieldPattern[] innerPatterns = makeFieldPatterns(dataCons, "2");
                    
                    SourceModel.Expr conditionExpr;
                    {
                        //for equals, generates:
                        //"True" if dataConsArity == 0
                        //"u1 == v1" if dataConsArity == 1
                        //"u1 == v1 && u2 == v2 && ... && un == vn" if dataConsArity > 1
                        
                        //for not equals, generates:
                        //"False" if dataConsArity == 0
                        //"u1 != v1" if dataConsArity == 1
                        //"u1 != v1 || u2 != v2 || ... || un != vn" if dataConsArity > 1
                        if (dataConsArity == 0) {
                            conditionExpr = SourceModel.Expr.makeBooleanValue(!makeNotEquals);
                        } else {
                            conditionExpr = null;
                            for (int j = 0; j < dataConsArity; ++j) {
                                //u_j+1 == v_j+1
                                SourceModel.Expr leftExpr = SourceModel.Expr.Var.makeUnqualified(getPatternNameFromFieldPattern(outerPatterns[j]));
                                SourceModel.Expr rightExpr = SourceModel.Expr.Var.makeUnqualified(getPatternNameFromFieldPattern(innerPatterns[j]));
                                SourceModel.Expr eqExpr;
                                if (makeNotEquals) {
                                    eqExpr = SourceModel.Expr.BinaryOp.NotEquals.make(leftExpr, rightExpr);
                                } else { // make equals
                                    eqExpr = SourceModel.Expr.BinaryOp.Equals.make(leftExpr, rightExpr);
                                }
                                
                                if (conditionExpr == null) {
                                    conditionExpr = eqExpr;
                                } else {
                                    if (makeNotEquals) {
                                        conditionExpr = SourceModel.Expr.BinaryOp.Or.make(conditionExpr, eqExpr);
                                    } else { // make equals
                                        conditionExpr = SourceModel.Expr.BinaryOp.And.make(conditionExpr, eqExpr);
                                    }
                                }
                            }
                        }
                    }
                    
                    innerCaseAlts[0] = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, innerPatterns, conditionExpr);
                    
                    if (nInnerAlts == 2) {
                        innerCaseAlts[1] = SourceModel.Expr.Case.Alt.Default.make(SourceModel.Expr.makeBooleanValue(makeNotEquals));
                    }
                                                                    
                    innerCaseExpr = SourceModel.Expr.Case.make(SourceModel.Expr.Var.makeUnqualified(parameters[1].getName()), innerCaseAlts);
                }
                         
                outerCaseAlts[i] = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, outerPatterns, innerCaseExpr);
            }
            
            definingExpr = SourceModel.Expr.Case.make(SourceModel.Expr.Var.makeUnqualified(parameters[0].getName()), outerCaseAlts);
        }
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                definingExpr
                );
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
        
        return functionDefn;
    }

   /**
     * If a type M.T has Debug.Show in the deriving clause, then this is the definition of the M.$show$T
     * instance function which implements the Debug.show class method for the type T.
     *      
     * @param typeCons
     * @return the source model for the equals instance function. Will be an internal function (i.e. its text
     *   is not parseable as a CAL function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeShowInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeShowInstanceFunction does not work for foreign types.");
        }        
        
        //we need to include a type declaration since in certain cases the inferred type of the instance function
        //will be too general (i.e. the case of phantom type variables such as data Foo a b = MakeFoo a; then the inferred
        //type of the equalsFoo function will not involve the type variable b which will result in a compilation error later).
        
        //also note that the arguments of the instance functions are strict.
                            
        /*
        For example, given        
        data Triple a b c =
            Zero |
            One a :: a |
            Two
                a :: a
                b :: b |
            Three
                a :: a
                b :: b
                c :: c
            deriving Debug.Show;
        defined in module M
        the following would get generated: 
        
        //$show$Triple :: (Show a, Show b, Show c) => Triple a b c -> String;
        private $show$Triple !x =
            case x of
            Zero -> "M2.Zero";
            One {a} -> "(M2.One " ++ show a ++ ")";
            Two {a, b} -> concat ["(M2.Two ", show a, " ", show b, ")"];
            Three {a, b, c} -> concat ["(M2.Three ", show a, " ", show b, " ", show c, ")"];
            ;                               
        */
                
        String functionName = makeInstanceFunctionUnqualifiedName("show", typeCons);
        
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {
            SourceModel.Parameter.make("x", true)};
        
        SourceModel.Expr.Case caseExpr;        
        {        
            final int nDataCons = typeCons.getNDataConstructors();        
            SourceModel.Expr.Case.Alt[] caseAlts = new SourceModel.Expr.Case.Alt[nDataCons];
            
            for (int i = 0; i < nDataCons; ++i) {
                
                DataConstructor dataCons = typeCons.getNthDataConstructor(i);
                final int dataConsArity = dataCons.getArity();
                
                QualifiedName dataConsName = dataCons.getName();
                String dataConsStringName = dataConsName.getQualifiedName();
                SourceModel.Name.DataCons dataConsSourceModelName = SourceModel.Name.DataCons.make(dataConsName);
                
                SourceModel.FieldPattern[] patterns = makeFieldPatterns(dataCons, "");
                
                SourceModel.Expr altExpr; 
                
                if (dataConsArity == 0) {                    
                    
                    altExpr = SourceModel.Expr.makeStringValue(dataConsStringName);
                    
                } else {
                    
                    final int nLoopElems = dataConsArity * 2;                    
                    SourceModel.Expr[] listElements = new SourceModel.Expr[nLoopElems + 1];
                    for (int j = 0; j < nLoopElems; j += 2) {
                        if (j == 0) {
                            listElements[0] = SourceModel.Expr.makeStringValue("(" + dataConsStringName + " ");
                        } else {
                            listElements[j] = SourceModel.Expr.makeStringValue(" ");
                        }
                        
                        listElements[j+1] = SourceModel.Expr.makeGemCall(CAL_Debug.Functions.show, SourceModel.Expr.Var.makeUnqualified(getPatternNameFromFieldPattern(patterns[j/2])));
                    }
                    listElements[nLoopElems] = SourceModel.Expr.makeStringValue(")");
                    
                    altExpr = SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.concat, SourceModel.Expr.List.make(listElements));                    
                }                
                                
                caseAlts[i] = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsSourceModelName, patterns, altExpr);
            }
            
            caseExpr = SourceModel.Expr.Case.make(SourceModel.Expr.Var.makeUnqualified("x"), caseAlts);
        }
        
        SourceModel.FunctionDefn.Algebraic showInstanceFunction =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE, 
                parameters,
                caseExpr
                );
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(showInstanceFunction);
        }
        
        return showInstanceFunction;
    }
    
    /**
     * A Typesafe enumeration of the seven order comparison operations associated
     * with the typeclass Prelude.Ord. They are: lessThan (&lt;), lessThanEquals
     * (&lt;=), greaterThanEquals (&gt;=), greaterThan (&gt;), compare, max and min.
     * 
     * These private enumeration constants are designed to work in conjunction
     * with the private method <code>makeOrderComparisonFunction</code> in
     * generating the function defintions for the seven operations. Whereas
     * <code>makeOrderComparisonFunction</code> encapsulates the similarity in
     * the algorithms for generating the seven operations, this enumeration and
     * its constants encapsulate the differences among them.
     * 
     * @author Joseph Wong
     */
    private static abstract class OrderComparisonOperation {
        /**
         * The name of the class method represented by this enum constant.
         */
        private final String classMethodName;
        
        /**
         * Private constructor for this typesafe enum class.
         * 
         * @param classMethodName
         *            the name of the class method represented by the
         *            constructed instance.
         */
        private OrderComparisonOperation(String classMethodName) {
            this.classMethodName = classMethodName;
        }
        
        /**
         * Returns the name of the class method represented by this enum
         * constant.
         * 
         * @return the name of the class method
         */
        private String getClassMethodName() {
            return classMethodName;
        }
        
        /**
         * Constructs the source model representation of an application of the
         * order comparison operation represented by this instance. For example,
         * if this instance represents the lessThan operation, this method
         * should return <code>leftExpr &lt; rightExpr</code>.
         * 
         * @param leftExpr
         *            the first argument, or left-hand-side operand.
         * @param rightExpr
         *            the second argument, or right-hand-side operand.
         * @param functionParameters
         *            the parameters of the order comparison function being generated.
         * @return a SourceModel.Expr representing an application of the order
         *         comparison operation represented by this instance.
         */
        abstract SourceModel.Expr makeFundamentalCall(SourceModel.Expr leftExpr, SourceModel.Expr rightExpr, SourceModel.Parameter[] functionParameters);
        
        /**
         * Constructs the source model representation of the result value this
         * operation returns when the first argument is known to be less than
         * the second argument.
         * 
         * @param functionParameters
         *            the parameters of the order comparison function being generated.
         * @return a SourceModel.Expr representing the desired result value.
         */
        abstract SourceModel.Expr makeResultValueForLessThan(SourceModel.Parameter[] functionParameters);
        
        /**
         * Constructs the source model representation of the result value this
         * operation returns when the first argument is known to be equal to
         * the second argument.
         * 
         * @param functionParameters
         *            the parameters of the order comparison function being generated.
         * @return a SourceModel.Expr representing the desired result value.
         */
        abstract SourceModel.Expr makeResultValueForEquals(SourceModel.Parameter[] functionParameters);
        
        /**
         * Constructs the source model representation of the result value this
         * operation returns when the first argument is known to be greater than
         * the second argument.
         * 
         * @param functionParameters
         *            the parameters of the order comparison function being generated.
         * @return a SourceModel.Expr representing the desired result value.
         */
        abstract SourceModel.Expr makeResultValueForGreaterThan(SourceModel.Parameter[] functionParameters);
        
        /**
         * Returns whether the result value this operation returns is the same
         * for the cases 1) when the first argument is less than the second, and
         * 2) when the first argument is equal to the second.
         * 
         * @return true iff this operation returns the same value for the cases
         *         1) when the first argument is less than the second, and 2)
         *         when the first argument is equal to the second.
         */
        abstract boolean isResultValueForEqualsSameAsLessThan();
        
        /**
         * Returns whether the result value this operation returns is the same
         * for the cases 1) when the first argument is greater than the second,
         * and 2) when the first argument is equal to the second.
         * 
         * @return true iff this operation returns the same value for the cases
         *         1) when the first argument is greater than the second, and 2)
         *         when the first argument is equal to the second.
         */
        abstract boolean isResultValueForEqualsSameAsGreaterThan();
        
        /**
         * The OrderComparisonOperation instance representing the lessThan (&lt;) operation.
         */
        private static final OrderComparisonOperation LESS_THAN = new OrderComparisonOperation("lessThan") {
            /**
             * Constructs the source model representation of an application of the
             * order comparison operation represented by this instance, i.e. it returns
             * <code>leftExpr &lt; rightExpr</code>.
             * 
             * @param leftExpr
             *            the first argument, or left-hand-side operand.
             * @param rightExpr
             *            the second argument, or right-hand-side operand. 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing an application of the order
             *         comparison operation represented by this instance.
             */
            @Override
            SourceModel.Expr makeFundamentalCall(SourceModel.Expr leftExpr, SourceModel.Expr rightExpr, SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.BinaryOp.LessThan.make(leftExpr, rightExpr);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be less than
             * the second argument. In other words, it returns the source model
             * for True.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForLessThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(true);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be equal to
             * the second argument. In other words, it returns the source model
             * for False.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForEquals(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(false);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be greater than
             * the second argument. In other words, it returns the source model
             * for False.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForGreaterThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(false);
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is less than the second, and
             * 2) when the first argument is equal to the second.
             * 
             * @return Since (using integers as example) (1 &lt; 2) != (2 &lt; 2), this method returns false.
             */
            @Override
            boolean isResultValueForEqualsSameAsLessThan() {
                return false;
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is greater than the second,
             * and 2) when the first argument is equal to the second.
             * 
             * @return Since (using integers as example) (3 &lt; 2) == (2 &lt; 2), this method returns true.
             */
            @Override
            boolean isResultValueForEqualsSameAsGreaterThan() {
                return true;
            }
        };
        
        /**
         * The OrderComparisonOperation instance representing the lessThanEquals (&lt;=) operation.
         */
        private static final OrderComparisonOperation LESS_THAN_EQUALS = new OrderComparisonOperation("lessThanEquals") {
            /**
             * Constructs the source model representation of an application of the
             * order comparison operation represented by this instance, i.e. it returns
             * <code>leftExpr &lt;= rightExpr</code>.
             * 
             * @param leftExpr
             *            the first argument, or left-hand-side operand.
             * @param rightExpr
             *            the second argument, or right-hand-side operand.
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing an application of the order
             *         comparison operation represented by this instance.
             */
            @Override
            SourceModel.Expr makeFundamentalCall(SourceModel.Expr leftExpr, SourceModel.Expr rightExpr, SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.BinaryOp.LessThanEquals.make(leftExpr, rightExpr);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be less than
             * the second argument. In other words, it returns the source model
             * for True.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForLessThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(true);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be equal to
             * the second argument. In other words, it returns the source model
             * for True.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForEquals(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(true);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be greater than
             * the second argument. In other words, it returns the source model
             * for False.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForGreaterThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(false);
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is less than the second, and
             * 2) when the first argument is equal to the second.
             * 
             * @return Since (using integers as example) (1 &lt;= 2) == (2 &lt;= 2), this method returns true.
             */
            @Override
            boolean isResultValueForEqualsSameAsLessThan() {
                return true;
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is greater than the second,
             * and 2) when the first argument is equal to the second.
             * 
             * @return Since (using integers as example) (3 &lt;= 2) != (2 &lt;= 2), this method returns false.
             */
            @Override
            boolean isResultValueForEqualsSameAsGreaterThan() {
                return false;
            }
        };
        
        /**
         * The OrderComparisonOperation instance representing the greaterThanEquals (&gt;=) operation.
         */
        private static final OrderComparisonOperation GREATER_THAN_EQUALS = new OrderComparisonOperation("greaterThanEquals") {
            /**
             * Constructs the source model representation of an application of the
             * order comparison operation represented by this instance, i.e. it returns
             * <code>leftExpr &gt;= rightExpr</code>.
             * 
             * @param leftExpr
             *            the first argument, or left-hand-side operand.
             * @param rightExpr
             *            the second argument, or right-hand-side operand.
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing an application of the order
             *         comparison operation represented by this instance.
             */
            @Override
            SourceModel.Expr makeFundamentalCall(SourceModel.Expr leftExpr, SourceModel.Expr rightExpr, SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.BinaryOp.GreaterThanEquals.make(leftExpr, rightExpr);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be less than
             * the second argument. In other words, it returns the source model
             * for False.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForLessThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(false);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be equal to
             * the second argument. In other words, it returns the source model
             * for True.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForEquals(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(true);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be greater than
             * the second argument. In other words, it returns the source model
             * for True.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForGreaterThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(true);
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is less than the second, and
             * 2) when the first argument is equal to the second.
             * 
             * @return Since (using integers as example) (1 &gt;= 2) != (2 &gt;= 2), this method returns false.
             */
            @Override
            boolean isResultValueForEqualsSameAsLessThan() {
                return false;
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is greater than the second,
             * and 2) when the first argument is equal to the second.
             * 
             * @return Since (using integers as example) (3 &gt;= 2) == (2 &gt;= 2), this method returns true.
             */
            @Override
            boolean isResultValueForEqualsSameAsGreaterThan() {
                return true;
            }
        };
        
        /**
         * The OrderComparisonOperation instance representing the greaterThan (&gt;) operation.
         */
        private static final OrderComparisonOperation GREATER_THAN = new OrderComparisonOperation("greaterThan") {
            /**
             * Constructs the source model representation of an application of the
             * order comparison operation represented by this instance, i.e. it returns
             * <code>leftExpr &gt; rightExpr</code>.
             * 
             * @param leftExpr
             *            the first argument, or left-hand-side operand.
             * @param rightExpr
             *            the second argument, or right-hand-side operand.
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing an application of the order
             *         comparison operation represented by this instance.
             */
            @Override
            SourceModel.Expr makeFundamentalCall(SourceModel.Expr leftExpr, SourceModel.Expr rightExpr, SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.BinaryOp.GreaterThan.make(leftExpr, rightExpr);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be less than
             * the second argument. In other words, it returns the source model
             * for False.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForLessThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(false);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be equal to
             * the second argument. In other words, it returns the source model
             * for False.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForEquals(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(false);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be greater than
             * the second argument. In other words, it returns the source model
             * for True.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForGreaterThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeBooleanValue(true);
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is less than the second, and
             * 2) when the first argument is equal to the second.
             * 
             * @return Since (using integers as example) (1 &gt; 2) == (2 &gt; 2), this method returns true.
             */
            @Override
            boolean isResultValueForEqualsSameAsLessThan() {
                return true;
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is greater than the second,
             * and 2) when the first argument is equal to the second.
             * 
             * @return Since (using integers as example) (3 &gt; 2) != (2 &gt; 2), this method returns false.
             */
            @Override
            boolean isResultValueForEqualsSameAsGreaterThan() {
                return false;
            }
        };
        
        /**
         * The OrderComparisonOperation instance representing the compare operation.
         */
        private static final OrderComparisonOperation COMPARE = new OrderComparisonOperation("compare") {
            /**
             * Constructs the source model representation of an application of the
             * order comparison operation represented by this instance, i.e. it returns
             * <code>Prelude.compare leftExpr rightExpr</code>.
             * 
             * @param leftExpr
             *            the first argument, or left-hand-side operand.
             * @param rightExpr
             *            the second argument, or right-hand-side operand.
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing an application of the order
             *         comparison operation represented by this instance.
             */
            @Override
            SourceModel.Expr makeFundamentalCall(SourceModel.Expr leftExpr, SourceModel.Expr rightExpr, SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.compare, leftExpr, rightExpr);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be less than
             * the second argument. In other words, this returns the source model
             * for Prelude.LT.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForLessThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.DataCons.make(CAL_Prelude.DataConstructors.LT);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be equal to
             * the second argument. In other words, this returns the source model
             * for Prelude.EQ.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForEquals(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.DataCons.make(CAL_Prelude.DataConstructors.EQ);
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be greater than
             * the second argument. In other words, this returns the source model
             * for Prelude.GT.
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForGreaterThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.DataCons.make(CAL_Prelude.DataConstructors.GT);
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is less than the second, and
             * 2) when the first argument is equal to the second.
             * 
             * @return Since Prelude.LT != Prelude.EQ, this method returns false.
             */
            @Override
            boolean isResultValueForEqualsSameAsLessThan() {
                return false;
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is greater than the second,
             * and 2) when the first argument is equal to the second.
             * 
             * @return Since Prelude.GT != Prelude.EQ, this method returns false.
             */
            @Override
            boolean isResultValueForEqualsSameAsGreaterThan() {
                return false;
            }
        };

        /**
         * The OrderComparisonOperation instance representing the max operation.
         */
        private static final OrderComparisonOperation MAX = new OrderComparisonOperation("max") {
            /**
             * Constructs the source model representation of an application of the
             * order comparison operation represented by this instance, i.e. it returns
             * <code>if leftExpr &lt;= rightExpr then params[1] else params[0]</code>.
             * 
             * @param leftExpr
             *            the first argument, or left-hand-side operand.
             * @param rightExpr
             *            the second argument, or right-hand-side operand.
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing an application of the order
             *         comparison operation represented by this instance.
             */
            @Override
            SourceModel.Expr makeFundamentalCall(SourceModel.Expr leftExpr, SourceModel.Expr rightExpr, SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.If.make(
                    SourceModel.Expr.BinaryOp.LessThanEquals.make(leftExpr, rightExpr),
                    SourceModel.Expr.Var.makeUnqualified(functionParameters[1].getName()),
                    SourceModel.Expr.Var.makeUnqualified(functionParameters[0].getName()));
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be less than
             * the second argument. In other words, this returns params[1].
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForLessThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.Var.makeUnqualified(functionParameters[1].getName());
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be equal to
             * the second argument. In other words, this returns params[1].
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForEquals(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.Var.makeUnqualified(functionParameters[1].getName());
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be greater than
             * the second argument. In other words, this returns params[0].
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForGreaterThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.Var.makeUnqualified(functionParameters[0].getName());
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is less than the second, and
             * 2) when the first argument is equal to the second.
             * 
             * @return Since when both arguments are equal, the maximum is equal to both
             *         of them, so therefore the result value for equals is the same as
             *         that for less than.
             */
            @Override
            boolean isResultValueForEqualsSameAsLessThan() {
                return true;
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is greater than the second,
             * and 2) when the first argument is equal to the second.
             * 
             * @return Since when both arguments are equal, the maximum is equal to both
             *         of them, so therefore the result value for equals is the same as
             *         that for greater than.
             */
            @Override
            boolean isResultValueForEqualsSameAsGreaterThan() {
                return true;
            }
        };

        /**
         * The OrderComparisonOperation instance representing the min operation.
         */
        private static final OrderComparisonOperation MIN = new OrderComparisonOperation("min") {
            /**
             * Constructs the source model representation of an application of the
             * order comparison operation represented by this instance, i.e. it returns
             * <code>if leftExpr &lt;= rightExpr then params[0] else params[1]</code>.
             * 
             * @param leftExpr
             *            the first argument, or left-hand-side operand.
             * @param rightExpr
             *            the second argument, or right-hand-side operand.
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing an application of the order
             *         comparison operation represented by this instance.
             */
            @Override
            SourceModel.Expr makeFundamentalCall(SourceModel.Expr leftExpr, SourceModel.Expr rightExpr, SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.If.make(
                    SourceModel.Expr.BinaryOp.LessThanEquals.make(leftExpr, rightExpr),
                    SourceModel.Expr.Var.makeUnqualified(functionParameters[0].getName()),
                    SourceModel.Expr.Var.makeUnqualified(functionParameters[1].getName()));
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be less than
             * the second argument. In other words, this returns params[0].
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForLessThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.Var.makeUnqualified(functionParameters[0].getName());
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be equal to
             * the second argument. In other words, this returns params[0].
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForEquals(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.Var.makeUnqualified(functionParameters[0].getName());
            }
            
            /**
             * Constructs the source model representation of the result value this
             * operation returns when the first argument is known to be greater than
             * the second argument. In other words, this returns params[1].
             * 
             * @param functionParameters
             *            the parameters of the order comparison function being generated.
             * @return a SourceModel.Expr representing the desired result value.
             */
            @Override
            SourceModel.Expr makeResultValueForGreaterThan(SourceModel.Parameter[] functionParameters) {
                return SourceModel.Expr.Var.makeUnqualified(functionParameters[1].getName());
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is less than the second, and
             * 2) when the first argument is equal to the second.
             * 
             * @return Since when both arguments are equal, the minimum is equal to both
             *         of them, so therefore the result value for equals is the same as
             *         that for less than.
             */
            @Override
            boolean isResultValueForEqualsSameAsLessThan() {
                return true;
            }
            
            /**
             * Returns whether the result value this operation returns is the same
             * for the cases 1) when the first argument is greater than the second,
             * and 2) when the first argument is equal to the second.
             * 
             * @return Since when both arguments are equal, the minimum is equal to both
             *         of them, so therefore the result value for equals is the same as
             *         that for greater than.
             */
            @Override
            boolean isResultValueForEqualsSameAsGreaterThan() {
                return true;
            }
        };
    }
    
    /**
     * Constructs a case alternative for unpacking a general data constructor,
     * with an empty list of field patterns.
     * 
     * @param dataCons
     *            the data constructor.
     * @param altExpr
     *            the expression to appear on the right hand side of the "->" in
     *            the case alternative.
     * @return the source model representation of the case alternative.
     */
    private SourceModel.Expr.Case.Alt.UnpackDataCons makeUnpackDataConsAltWithEmptyFieldPatterns(DataConstructor dataCons, SourceModel.Expr altExpr) {
        SourceModel.Name.DataCons dataConsName = SourceModel.Name.DataCons.make(dataCons.getName());
        
        return SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, new SourceModel.FieldPattern[0], altExpr);
    }
    
    /**
     * For a type M.T has Prelude.Ord in the deriving clause, this method
     * returns, depending on the value of the argument <code>operation</code>,
     * the definition of an instance function implementing one of the seven order
     * comparison class methods for the type T (namely: lessThan,
     * lessThanEquals, greaterThanEquals, greaterThan, compare, max, min).
     * 
     * @param typeCons
     * @param operation
     *            one of the seven enumeration constants representing the order
     *            comparison class methods of the Ord typeclass (lessThan,
     *            lessThanEquals, greaterThanEquals, greaterThan, compare, max, min).
     * @return the source model for the instance function. Will be an internal
     *         function (i.e. its text is not parseable as a CAL function by the
     *         parser).
     */
    private SourceModel.FunctionDefn.Algebraic makeOrderComparisonFunction(TypeConstructor typeCons, OrderComparisonOperation operation) {
        
        //we need to include a type declaration since in certain cases the inferred type of the instance function
        //will be too general (i.e. the case of phantom type variables such as data Foo a b = MakeFoo a; then the inferred
        //type of the instance function will not involve the type variable b which will result in a compilation error later).
        
        //also note that the arguments of the instance functions are strict.
                            
        String functionName = makeInstanceFunctionUnqualifiedName(operation.getClassMethodName(), typeCons);
        
        SourceModel.Parameter[] parameters = makeTwoStrictParameters();

        final int nDataCons = typeCons.getNDataConstructors();

        SourceModel.Expr definingExpr;
        
        if (nDataCons == 1 && typeCons.getNthDataConstructor(0).getArity() == 0) {
            
            // Optimization:
            //
            // For the special case where there is only one data constructor and
            // its arity is 0, (e.g. a type like Prelude.Unit), there is only
            // one unique value for the entire type, and hence any two values of
            // such a type will always be equal.
            //
            // In this case, the function should simply return the result value
            // associated with the two arguments being equal.
            //
            // For example, given:
            //
            //  data Unit = Unit deriving Prelude.Eq, Prelude.Ord;
            //
            // we generate these seven order comparison functions:
            //
            //  private $lessThan$Unit !x !y = Prelude.False;
            //  private $lessThanEquals$Unit !x !y = Prelude.True;
            //  private $greaterThanEquals$Unit !x !y = Prelude.True;
            //  private $greaterThan$Unit !x !y = Prelude.False;
            //  private $compare$Unit !x !y = Prelude.EQ;
            //  private $max$Unit !x !y = y;
            //  private $min$Unit !x !y = x;
            //
            // This optimization is valid because the arguments x and y are strict.
            //
            
            definingExpr = operation.makeResultValueForEquals(parameters);
            
        } else {
            
            // General case:
            //
            // Since either
            //  1) there is more than one data constructor, or
            //  2) the only data costructor of the type has one or more arguments,
            // we will need to have a case expression to unpack the data
            // constructor(s).
            
            SourceModel.Expr.Case.Alt[] outerCaseAlts = new SourceModel.Expr.Case.Alt[nDataCons];
            
            for (int i = 0; i < nDataCons; ++i) {
                
                // Setup the various pieces of the case alternative for this, the i-th data constructor:
                // - the arity, the name, and the constructor variable patterns
                //
                // In the scope of this case alternative, the first argument of the function is known to be
                // a value constructed by the i-th data constructor.
                
                DataConstructor dataCons = typeCons.getNthDataConstructor(i);
                final int dataConsArity = dataCons.getArity();
                
                SourceModel.Name.DataCons dataConsName = SourceModel.Name.DataCons.make(dataCons.getName());
                
                SourceModel.FieldPattern[] outerPatterns = makeFieldPatterns(dataCons, "1");
                
                // Construct the expression on the right hand side of "->" for the "outer" case alternative:
                //
                // One can divide the set of all data constructors for this type into three categories:
                // 1) those that come before the i-th data constructor,
                // 2) the i-th data constructor
                // 3) those that come after the i-th constructor
                //
                // Depending on the value of i, category 1) or 3) may be empty. In the case where there is only
                // one data constructor in the type, both categories 1) and 3) would be empty.
                
                SourceModel.Expr outerCaseAltExpr = null;
                {
                    // Given the sequence of data constructors, numbered 0 through n-1,
                    // we know that:
                    // - the data constructors numbered 0 through i-1 belong to category 1).
                    //   If the second argument is a value constructed by one of these constructors,
                    //   then this order comparison function should return the result value
                    //   associated with the first argument being *greater than* the second argument.
                    
                    int lastCaseWithResultValueForGreaterThan = i - 1;
                    
                    // We also know that:
                    // - the data constructors numbered i+1 through n-1 belong to category 3).
                    //   If the second argument is a value constructed by one of these constructors,
                    //   then this order comparison function should return the result value
                    //   associated with the first argument being *less than* the second argument.
                    
                    int firstCaseWithResultValueForLessThan = i + 1;
                    
                    // We use a List to aggregate the "inner" case alternatives we generate, for
                    // inclusion in a case expression that forms the right hand side of "->" for
                    // the "outer" case alternative.
                    
                    List<SourceModel.Expr.Case.Alt> innerCaseAlts = new ArrayList<SourceModel.Expr.Case.Alt>();
                    
                    // Optimization:
                    //
                    // In the case where the i-th data constructor has arity 0, and where we are
                    // generating one of the four functions (<, <=, >=, >) which return a boolean
                    // value, we know that the right hand side of the "->" of the *inner* case
                    // alternative is either Prelude.True or Prelude.False.
                    //
                    // Therefore, we can lump the i-th "inner" case alternative with either
                    // category 1) (i.e. those data constructors that come before the i-th one), or
                    // category 3) (i.e. those data constructors that come after the i-th one),
                    // depending on whether the intended result value of this alternative
                    // is the same result value as the cases in category 1) or category 3).
                    //
                    // For example, given:
                    //
                    //  data ABC = A a :: Prelude.Int | B | C c :: Prelude.String deriving Prelude.Eq, Prelude.Ord;
                    //
                    // let us focus on the implementation of lessThan (<), and the case where the
                    // first argument, x, is the value B:
                    //
                    // private $lessThan$ABC !x !y =
                    //    case x of
                    //    ...
                    //    B {} ->
                    //       case y of
                    //       A {} -> Prelude.False;
                    //       B {} -> Prelude.False;
                    //       C {} -> Prelude.True;
                    //       ;
                    //    ...
                    //    ;
                    // 
                    // Here, the inner case alternative for B can be grouped with that for A, which would lead
                    // to a slightly more optimized implementation:
                    //
                    // private $lessThan$ABC !x !y =
                    //    case x of
                    //    ...
                    //    B {} ->
                    //       case y of
                    //       C {} -> Prelude.True;
                    //       _    -> Prelude.False;
                    //       ;
                    //    ...
                    //    ;
                    //
                    // In the code immediately following this comment, we merely calculate whether such lumping
                    // is possible. The generation of the default case alternative is embodied in a separate
                    // optimization further down.
                    
                    
                    // We use the variable 'caseForSameDataConstructor' to represent the case alternative to be
                    // generated for the "inner" case alternative for the i-th data constructor (note that we are in
                    // the scope of the *outer* case alternative for the i-th data constructor).
                    //
                    // If we can legitimately lump this particular case in with other cases (see above), then we leave
                    // this variable null.
                    
                    SourceModel.Expr.Case.Alt caseForSameDataConstructor = null;
                    {
                        SourceModel.FieldPattern[] innerPatterns = makeFieldPatterns(dataCons, "2");
                        
                        if (dataConsArity > 0) {
                            
                            // the arity of the i-th data constructor is > 0, so must generate a separate case
                            // alternative for it
                            //
                            // Suppose this data constructor (say, DC) has n arguments, we then generate:
                            //
                            // case x of
                            // ...
                            // DC u0 a1 ... u{n-1} ->
                            //    case y of
                            //    ...
                            //    DC v0 b1 ... v{n-1} ->
                            //       case Prelude.compare u0 v0 of
                            //       Prelude.LT -> {resultValueForLessThan};
                            //       Prelude.EQ ->
                            //          compare Prelude.compare u1 v1 of
                            //          ...
                            //             ...
                            //                compare Prelude.compare u{n-2} v{n-2} of
                            //                Prelude.LT -> {resultValueForLessThan};
                            //                Prelude.EQ -> {fundamentalCall} u{n-1} v{n-1};
                            //                Prelude.GT -> {resultValueForGreaterThan};
                            //                ;
                            //             ...
                            //          ...
                            //          ;
                            //       Prelude.GT -> {resultValueForGreaterThan};
                            //    ;
                            // ...
                            // ;
                            //
                            // where
                            //   {resutlValueForLessThan} is the result value associated with the
                            //      first argument being *less than* the second argument,
                            //
                            //   {resultValueForGreaterThan} is the result value associated with the
                            //      first argument being *greater than* the second argument, and
                            //
                            //   {fundamentalCall} is the name of the class method whose implementation is
                            //      currently being generated. For example, when we are generating the
                            //      compare method, {fundamentalCall} is Prelude.compare, and when we are
                            //      generating the lessThan method, {fundamentalCall} is Prelude.lessThan
                            //      (in which case the inner most expression would take the more natural
                            //      form "u{n-1} < v{n-1}").
                            
                            
                            // We start building the right hand side of the "->" with the last argument.
                            SourceModel.Expr conditionExpr = operation.makeFundamentalCall(
                                SourceModel.Expr.Var.makeUnqualified(getPatternNameFromFieldPattern(outerPatterns[dataConsArity - 1])),
                                SourceModel.Expr.Var.makeUnqualified(getPatternNameFromFieldPattern(innerPatterns[dataConsArity - 1])),
                                parameters);
                            
                            // Then from the second-last argument back to the first argument, we
                            // wrap the expression with an enclosing case expression, one for each argument.
                            for (int m = dataConsArity - 2; m >= 0; m--) {
                                conditionExpr = SourceModel.Expr.Case.make(
                                    SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.compare,
                                        SourceModel.Expr.Var.makeUnqualified(getPatternNameFromFieldPattern(outerPatterns[m])),
                                        SourceModel.Expr.Var.makeUnqualified(getPatternNameFromFieldPattern(innerPatterns[m]))),
                                    new SourceModel.Expr.Case.Alt[] {
                                        SourceModel.Expr.Case.Alt.UnpackDataCons.make(PRELUDE_LT_DATACONS, operation.makeResultValueForLessThan(parameters)),
                                        SourceModel.Expr.Case.Alt.UnpackDataCons.make(PRELUDE_EQ_DATACONS, conditionExpr),
                                        SourceModel.Expr.Case.Alt.UnpackDataCons.make(PRELUDE_GT_DATACONS, operation.makeResultValueForGreaterThan(parameters))
                                    });
                            }
                            
                            // From these pieces, now create the actual case alternative
                            caseForSameDataConstructor = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, innerPatterns, conditionExpr);
                            
                        } else {
                            
                            // the arity of the i-th data constructor is 0, so we need to determine
                            // whether the "lumping" as described above is applicable:
                            
                            boolean canLumpWithGreaterThanCases = operation.isResultValueForEqualsSameAsGreaterThan();
                            boolean canLumpWithLessThanCases = operation.isResultValueForEqualsSameAsLessThan();
                            
                            if (canLumpWithGreaterThanCases) {
                                
                                if (canLumpWithLessThanCases) {
                                    // this is the case for max and min, where the result value should be lumpable
                                    // with either the less than or the greater than cases, so we pick the larger
                                    // of the two sets and lump with that
                                    
                                    int nCasesWithResultValueForGreaterThan = lastCaseWithResultValueForGreaterThan + 1;
                                    int nCasesWithResultValueForLessThan = nDataCons - firstCaseWithResultValueForLessThan;
                                    
                                    if (nCasesWithResultValueForGreaterThan <= nCasesWithResultValueForLessThan) {
                                        // the result value is the same as that for the category 3 cases (i.e. the cases for
                                        // data constructors i+1 through n-1), so we can lump this case with them by
                                        // including it in their ranks.
                                        firstCaseWithResultValueForLessThan = i;
                                        
                                    } else {
                                        // the result value is the same as that for the category 1 cases (i.e. the cases for
                                        // data constructors 0 through i-1), so we can lump this case with them by
                                        // including it in their ranks.
                                        lastCaseWithResultValueForGreaterThan = i;
                                    }
                                } else {
                                    // the result value is the same as that for the category 1 cases (i.e. the cases for
                                    // data constructors 0 through i-1), so we can lump this case with them by
                                    // including it in their ranks.
                                    lastCaseWithResultValueForGreaterThan = i;
                                }
                                
                            } else if (canLumpWithLessThanCases) {
                                // the result value is the same as that for the category 3 cases (i.e. the cases for
                                // data constructors i+1 through n-1), so we can lump this case with them by
                                // including it in their ranks.
                                firstCaseWithResultValueForLessThan = i;
                                
                            } else {
                                // no lumping can be done because the result value is different from the other cases
                                // (in other words we are generating the compare method, where the result value is Prelude.EQ),
                                // so create a case alternative with the appropriate result value.
                                caseForSameDataConstructor = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, innerPatterns, operation.makeResultValueForEquals(parameters));
                            }
                        }
                    }
                    
                    // Optimization:
                    //
                    // We now have two equivalent classes of cases: those cases whose result value is the one associated
                    // with the first argument being *greater than* the second argument (call it "Class GT"), and those
                    // whose result value is the one associated with the first argument being *less than* the
                    // second argument (call it "Class LT").
                    //
                    // We want to pick the larger of these two classes, and use a default case alternative
                    // as a catch-all to handle the cases within.
                    //
                    // This optimization also completes the previous optimization of "lumping" the middle
                    // case (i.e. the one where the "inner" case's data constructor is the same as the "outer" case's),
                    // as this middle case would have been added to the ranks of either "Class GT" or "Class LT"
                    // by this time (through the manipulation of the index variables
                    // lastCaseWithResultValueForGreaterThan and firstCaseWithResultValueForLessThan) if it is applicable.
                    
                    
                    // Calculate the sizes of the two equivalence classes
                    final int nCasesWithResultValueForGreaterThan = lastCaseWithResultValueForGreaterThan + 1;
                    final int nCasesWithResultValueForLessThan = nDataCons - firstCaseWithResultValueForLessThan;

                    // The result value to be returned by the default case alternative. What the result value
                    // is depends on which of the two equivalence classes is the one covered by the default
                    // case alternative.
                    SourceModel.Expr defaultResultValue;
                    
                    // We also keep a flag indicating whether to generate a default case alternative at all.
                    // For example, if the type has only one constructor, then we don't need to have
                    // a default case alternative *in addition* to the alternative unpacking the one and onlyh
                    // data constructor.
                    boolean shouldGenerateDefaultCase;
                    
                    
                    if (nCasesWithResultValueForGreaterThan <= nCasesWithResultValueForLessThan) {
                        
                        // "Class LT" is the larger of the two equivalence classes.
                        
                        // So first, we construct explicit case alternatives for the cases in "Class GT" (the class with fewer cases).
                        for (int k = 0; k <= lastCaseWithResultValueForGreaterThan; k++) {
                            innerCaseAlts.add(makeUnpackDataConsAltWithEmptyFieldPatterns(typeCons.getNthDataConstructor(k), operation.makeResultValueForGreaterThan(parameters)));
                        }
                        
                        // If we had previously decided to construct a separate case alternative for the case
                        // where the data constructor matches, add that case alternative to the list now.
                        if (caseForSameDataConstructor != null) {
                            innerCaseAlts.add(caseForSameDataConstructor);
                        }
                        
                        // Finally, if "Class LT" is non-empty, make a note to construct the default case alternative for them.
                        defaultResultValue = operation.makeResultValueForLessThan(parameters);
                        shouldGenerateDefaultCase = (nCasesWithResultValueForLessThan > 0);
                        
                    } else {
                        
                        // "Class GT" is the larger of the two equivalence classes.
                        
                        // So first, if "Class GT" is non-empty, make a note to construct the default case alternative for them.
                        defaultResultValue = operation.makeResultValueForGreaterThan(parameters);
                        shouldGenerateDefaultCase = (nCasesWithResultValueForGreaterThan > 0);
                        
                        // If we had previously decided to construct a separate case alternative for the case
                        // where the data constructor matches, add that case alternative to the list now.
                        if (caseForSameDataConstructor != null) {
                            innerCaseAlts.add(caseForSameDataConstructor);
                        }
                        
                        // Finally, we construct explicit case alternatives for the cases in "Class LT" (the class with fewer cases).
                        for (int k = firstCaseWithResultValueForLessThan; k < nDataCons; k++) {
                            innerCaseAlts.add(makeUnpackDataConsAltWithEmptyFieldPatterns(typeCons.getNthDataConstructor(k), operation.makeResultValueForLessThan(parameters)));
                        }
                    }
                    
                    // Optimization:
                    //
                    // By now, the variable innerCaseAlts should have contained all the case alternatives to be generated,
                    // except for the default case. If innerCaseAlts is empty, that means there is only one possible
                    // result value, namely the default result value, associated with the "outer" case alternative
                    // for the i-th data constructor.
                    //
                    // Rather than creating a case expression with only a default case in it, we simply place the
                    // result value directly on the right hand side of the "->" for the "outer" case alternative.
                    //
                    // For example, given:
                    //
                    //  data XYZ = X | Y | Z deriving Prelude.Eq, Prelude.Ord;
                    //
                    // rather than generating this for greaterThan:
                    //
                    // private $greaterThan$XYZ !x !y =
                    //    case x of
                    //    X {} ->
                    //       case y of
                    //       _ -> Prelude.False;
                    //       ;
                    //    ...
                    //    ;
                    //
                    // we would want to generate this instead:
                    //
                    // private $lessThan$XYZ !x !y =
                    //    case x of
                    //    X {} -> Prelude.False;
                    //    ...
                    //    ;
                    //
                    // This optimization is valid because the argument y is strict.
                    //
                    
                    if (innerCaseAlts.isEmpty()) {
                        // innerCaseAlts is indeed empty, so perform the optimization described above.
                        outerCaseAltExpr = defaultResultValue;
                        
                    } else {
                        // We need to check whether we need to generate the default case, since it may
                        // turn out to be redundant for the case where the type has only a single
                        // data constructor.
                        if (shouldGenerateDefaultCase) {
                            innerCaseAlts.add(SourceModel.Expr.Case.Alt.Default.make(defaultResultValue));
                        }
                        
                        // Finally, the right hand side of "->" of the "outer" case alternative is
                        // another (the "inner") case expression.
                        outerCaseAltExpr = SourceModel.Expr.Case.make(
                            SourceModel.Expr.Var.makeUnqualified(parameters[1].getName()),
                            innerCaseAlts.toArray(new SourceModel.Expr.Case.Alt[0]));
                    }
                    
                }
                
                outerCaseAlts[i] = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, outerPatterns, outerCaseAltExpr);
            }
            
            definingExpr = SourceModel.Expr.Case.make(SourceModel.Expr.Var.makeUnqualified(parameters[0].getName()), outerCaseAlts);
        }
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                definingExpr
            );
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
        
        return functionDefn;
    }
       
    /**
     * If a type M.T has Prelude.Ord in the deriving clause, then this is the
     * definition of the M.$lessThan$T instance function which implements the
     * Prelude.lessThan class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the lessThan instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeLessThanInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeLessThanInstanceFunction does not work for foreign types.");
        }
        
        return makeOrderComparisonFunction(typeCons, OrderComparisonOperation.LESS_THAN);
    }
    
    /**
     * If a type M.T has Prelude.Ord in the deriving clause, then this is the
     * definition of the M.$lessThanEquals$T instance function which implements the
     * Prelude.lessThanEquals class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the lessThanEquals instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeLessThanEqualsInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeLessThanEqualsInstanceFunction does not work for foreign types.");
        }
        
        return makeOrderComparisonFunction(typeCons, OrderComparisonOperation.LESS_THAN_EQUALS);
    }

    /**
     * If a type M.T has Prelude.Ord in the deriving clause, then this is the
     * definition of the M.$greaterThanEquals$T instance function which implements the
     * Prelude.greaterThanEquals class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the greaterThanEquals instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeGreaterThanEqualsInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeGreaterThanEqualsInstanceFunction does not work for foreign types.");
        }
        
        return makeOrderComparisonFunction(typeCons, OrderComparisonOperation.GREATER_THAN_EQUALS);
    }
    
    /**
     * If a type M.T has Prelude.Ord in the deriving clause, then this is the
     * definition of the M.$greaterThan$T instance function which implements the
     * Prelude.greaterThan class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the greaterThan instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeGreaterThanInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeGreaterThanInstanceFunction does not work for foreign types.");
        }
        
        return makeOrderComparisonFunction(typeCons, OrderComparisonOperation.GREATER_THAN);
    }

    /**
     * If a type M.T has Prelude.Ord in the deriving clause, then this is the
     * definition of the M.$compare$T instance function which implements the
     * Prelude.compare class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the compare instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeCompareInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeCompareInstanceFunction does not work for foreign types.");
        }
        
        return makeOrderComparisonFunction(typeCons, OrderComparisonOperation.COMPARE);
    }

    /**
     * If a type M.T has Prelude.Ord in the deriving clause, then this is the
     * definition of the M.$max$T instance function which implements the
     * Prelude.max class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the max instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeMaxInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeMaxInstanceFunction does not work for foreign types.");
        }
        
        return makeOrderComparisonFunction(typeCons, OrderComparisonOperation.MAX);
    }
    
    /**
     * If a type M.T has Prelude.Ord in the deriving clause, then this is the
     * definition of the M.$min$T instance function which implements the
     * Prelude.min class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the min instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeMinInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeMinInstanceFunction does not work for foreign types.");
        }
        
        return makeOrderComparisonFunction(typeCons, OrderComparisonOperation.MIN);
    }
    
    /**
     * Constructs a source model representation of the minimum value of an
     * algebraic type deriving the Bounded type class.
     * 
     * @param typeCons
     * @return the source model of the minimum value.
     */
    private SourceModel.Expr makeMinBoundDefiningExpr(TypeConstructor typeCons) {
        DataConstructor minDataCons = typeCons.getNthDataConstructor(0);
        
        if (minDataCons.getArity() == 0) {
            return SourceModel.Expr.DataCons.make(SourceModel.Name.DataCons.make(minDataCons.getName()));
        } else {
            throw new IllegalArgumentException("makeMinBoundDefiningExpr only works for types with only data constructors that take no arguments.");
        }   
    }
    
    /**
     * If a type M.T has Prelude.Bounded in the deriving clause, then this is the
     * definition of the M.$minBound$T instance function which implements the
     * Prelude.minBound class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the minBound instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeMinBoundInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeMinBoundInstanceFunction does not work for foreign types.");
        }
        
        //we need to include a type declaration since in certain cases the inferred type of the instance function
        //will be too general (i.e. the case of phantom type variables such as data Foo a b = MakeFoo a; then the inferred
        //type of the equalsFoo/notEqualsFoo function will not involve the type variable b which will result in a compilation error later).
        
        //also note that the arguments of the instance functions are strict.
        
        /*
        For example, given
        data Ordering = LT | EQ | GT deriving Enum;
        the following would get generated: 
        
        //$minBound$Ordering :: Ordering;
        private $minBound$Ordering = Prelude.LT;
        */
        
        String functionName = makeInstanceFunctionUnqualifiedName("minBound", typeCons);
        
        SourceModel.FunctionDefn.Algebraic functionDefn = makeAlgebraicFunctionDefn(
            functionName,
            Scope.PRIVATE,
            SourceModel.FunctionDefn.Algebraic.NO_PARAMETERS,
            makeMinBoundDefiningExpr(typeCons));
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
        
        return functionDefn;
    }

    
    /**
     * Constructs a source model representation of the maximum value of an
     * algebraic type deriving the Bounded type class.
     * 
     * @param typeCons
     * @return the source model of the maximum value.
     */
    private SourceModel.Expr makeMaxBoundDefiningExpr(TypeConstructor typeCons) {
        DataConstructor maxDataCons = typeCons.getNthDataConstructor(typeCons.getNDataConstructors() - 1);
        
        if (maxDataCons.getArity() == 0) {
            return SourceModel.Expr.DataCons.make(SourceModel.Name.DataCons.make(maxDataCons.getName()));
        } else {
            throw new IllegalArgumentException("makeMaxBoundDefiningExpr only works for types with only data constructors that take no arguments.");
        }   
    }
    
    /**
     * If a type M.T has Prelude.Bounded in the deriving clause, then this is the
     * definition of the M.$maxBound$T instance function which implements the
     * Prelude.maxBound class method for the type T.
     * 
     * @param typeCons
     * @return the source model for the maxBound instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeMaxBoundInstanceFunction(TypeConstructor typeCons) {
        
        if (typeCons.getForeignTypeInfo() != null) {
            throw new IllegalArgumentException("makeMaxBoundInstanceFunction does not work for foreign types.");
        }
        
        //we need to include a type declaration since in certain cases the inferred type of the instance function
        //will be too general (i.e. the case of phantom type variables such as data Foo a b = MakeFoo a; then the inferred
        //type of the equalsFoo/notEqualsFoo function will not involve the type variable b which will result in a compilation error later).
        
        //also note that the arguments of the instance functions are strict.
        
        /*
        For example, given
        data Ordering = LT | EQ | GT deriving Enum;
        the following would get generated: 
        
        //$maxBound$Ordering :: Ordering;
        private $maxBound$Ordering = Prelude.GT;
        */
        
        String functionName = makeInstanceFunctionUnqualifiedName("maxBound", typeCons);
        
        SourceModel.FunctionDefn.Algebraic functionDefn = makeAlgebraicFunctionDefn(
            functionName,
            Scope.PRIVATE,
            SourceModel.FunctionDefn.Algebraic.NO_PARAMETERS,
            makeMaxBoundDefiningExpr(typeCons));
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
        
        return functionDefn;
    }
    
    /**
     * If a type M.T has Prelude.Inputable in the deriving clause, then this is the
     * definition of the M.$input$T instance function which implements the
     * Prelude.input class method for the type T.
     * 
     * @param typeCons TypeConstructor of the type to construct an instance function for
     * @return the source model for the input instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */    
    SourceModel.FunctionDefn.Algebraic makeAlgebraicInputInstanceMethod(TypeConstructor typeCons) {
        
        if (typeCons.getNDataConstructors() == 0) {
            throw new IllegalArgumentException("makeAlgebraicInputInstanceMethod does not work for non-algebraic types.");
        }
        
        /*
         * For the type
         *  
         *  data public Maybe a = Nothing | Just a deriving Inputable;
         * 
         * we will generate the following:
         * 
         *  private $input$Maybe !object = 
         *      let
         *          nativeObject :: Prelude.AlgebraicValue;
         *          nativeObject = Prelude.input object;
         *          ordinal :: Prelude.Int;
         *          ordinal = Prelude.algebraicValue_getDataConstructorOrdinal nativeObject;
         *      in
         *          case ordinal of
         *          0 -> 
         *              if Prelude.algebraicValue_getDataConstructorName nativeObject == "Prelude.Nothing" then
         *                  if Prelude.algebraicValue_getNArguments nativeObject == (0 :: Prelude.Int) then
         *                      Prelude.Nothing
         *                  else
         *                      Prelude.error "$input$Maybe: Wrong number of data constructor arguments"
         *              else
         *                  Prelude.error "$input$Maybe: Unrecognized data constructor name"
         *              ;
         *          1 -> 
         *              if Prelude.algebraicValue_getDataConstructorName nativeObject == "Prelude.Just" then
         *                  if Prelude.algebraicValue_getNArguments nativeObject == (1 :: Prelude.Int) then
         *                      Prelude.Just (Prelude.input (Prelude.algebraicValue_getNthArgument nativeObject (0 :: Prelude.Int)))
         *                  else
         *                      Prelude.error "$input$Maybe: Wrong number of data constructor arguments"
         *              else
         *                  Prelude.error "$input$Maybe: Unrecognized data constructor name"
         *              ;
         *          _ -> Prelude.error "$input$Maybe: Unrecognized data constructor index";
         *      ;
         */
        
        String functionName = makeInstanceFunctionUnqualifiedName("input", typeCons);
        
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {
                SourceModel.Parameter.make("object", true)
        };
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                makeAlgebraicInputFunctionExpr(typeCons, parameters[0], functionName));
                
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }

        return functionDefn;
    }
    
    /**
     * Builds the defining expression part of a generated algebraic-type input function
     * @param typeCons The TypeConstructor of the type to build an input function for
     * @param parameter The name of the parameter to the function
     * @param functionName The name of the function being generated
     * @return A SourceModel suitable for use as the defining expression of an input function for an algebraic type
     */
    private SourceModel.Expr makeAlgebraicInputFunctionExpr(TypeConstructor typeCons, SourceModel.Parameter parameter, String functionName) {
        final int nDataConses = typeCons.getNDataConstructors();
        SourceModel.Expr[] clauseExprs = new SourceModel.Expr[nDataConses];
        
        SourceModel.Expr nativeParameter = 
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.input,                   
                SourceModel.Expr.Var.makeUnqualified(parameter.getName()));
        
        SourceModel.Expr.Var nativeObjectVar = SourceModel.Expr.Var.makeUnqualified("nativeObject");
        SourceModel.Expr.Var ordinalVar = SourceModel.Expr.Var.makeUnqualified("ordinal");
        
        LocalDefn.Function[] localDefns = new LocalDefn.Function[] {
                LocalDefn.Function.TypeDeclaration.make("nativeObject", SourceModel.TypeSignature.make(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.AlgebraicValue))),
                
                LocalDefn.Function.Definition.make(
                        "nativeObject", 
                        LocalDefn.Function.Definition.NO_PARAMETERS,
                        nativeParameter),
                
                LocalDefn.Function.TypeDeclaration.make("ordinal", SourceModel.TypeSignature.make(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Int))),

                LocalDefn.Function.Definition.make(
                        "ordinal",
                        LocalDefn.Function.Definition.NO_PARAMETERS,
                        SourceModel.Expr.makeGemCall(
                                CAL_Prelude_internal.Functions.internal_algebraicValue_getDataConstructorOrdinal, 
                                nativeObjectVar))
        };
        
        
        // Build the contents of each case alternative
        for(int i = 0; i < nDataConses; i++) {
            DataConstructor dataCons = typeCons.getNthDataConstructor(i);
            SourceModel.Name.DataCons dataConsName = SourceModel.Name.DataCons.make(dataCons.getName());
            final int nDataConsArgs = dataCons.getArity();
            
            if (nDataConsArgs == 0) {
                clauseExprs[i] = 
                    SourceModel.Expr.If.make(
                            SourceModel.Expr.BinaryOp.Equals.make(
                                    SourceModel.Expr.makeGemCall(CAL_Prelude_internal.Functions.internal_algebraicValue_getDataConstructorName, nativeObjectVar),
                                    SourceModel.Expr.makeStringValue(typeCons.getNthDataConstructor(i).getName().getQualifiedName())),
                            SourceModel.Expr.If.make(
                                    SourceModel.Expr.BinaryOp.Equals.make(
                                            SourceModel.Expr.makeGemCall(CAL_Prelude_internal.Functions.internal_algebraicValue_getNArguments, nativeObjectVar),
                                            SourceModel.Expr.makeIntValue(nDataConsArgs)),
                                    SourceModel.Expr.DataCons.make(dataConsName),
                                    SourceModel.Expr.makeErrorCall(functionName + ": Wrong number of data constructor arguments")),
                            SourceModel.Expr.makeErrorCall(functionName + ": Unrecognized data constructor name"));

            } else {
            
                SourceModel.Expr[] dataConsApplicationExprs = new SourceModel.Expr[nDataConsArgs + 1];
                
                dataConsApplicationExprs[0] = SourceModel.Expr.DataCons.make(dataConsName);
                
                for (int j = 0; j < nDataConsArgs; j++) {
                    dataConsApplicationExprs[j + 1] =
                        SourceModel.Expr.makeGemCall(
                            CAL_Prelude.Functions.input,    
                            SourceModel.Expr.makeGemCall(
                                CAL_Prelude_internal.Functions.internal_algebraicValue_getNthArgument,
                                nativeObjectVar,
                                SourceModel.Expr.makeIntValue(j))
                        );
                }
                
                clauseExprs[i] = 
                    SourceModel.Expr.If.make(
                            SourceModel.Expr.BinaryOp.Equals.make(
                                    SourceModel.Expr.makeGemCall(CAL_Prelude_internal.Functions.internal_algebraicValue_getDataConstructorName, nativeObjectVar),
                                    SourceModel.Expr.makeStringValue(typeCons.getNthDataConstructor(i).getName().getQualifiedName())),
                            SourceModel.Expr.If.make(
                                    SourceModel.Expr.BinaryOp.Equals.make(
                                            SourceModel.Expr.makeGemCall(CAL_Prelude_internal.Functions.internal_algebraicValue_getNArguments, nativeObjectVar),
                                            SourceModel.Expr.makeIntValue(nDataConsArgs)),
                                    SourceModel.Expr.Application.make(dataConsApplicationExprs),
                                    SourceModel.Expr.makeErrorCall(functionName + ": Wrong number of data constructor arguments")),
                            SourceModel.Expr.makeErrorCall(functionName + ": Unrecognized data constructor name"));
            }
        }
        
        SourceModel.Expr terminatingElse = SourceModel.Expr.makeErrorCall(functionName + ": Unrecognized data constructor index");
        
        SourceModel.Expr inPart;
        if (nDataConses == 0) {
            // This should only be the case for foreign types.
            throw new IllegalArgumentException("Cannot generate an algebraic input instance method for a type with no data constructors.");
        
        } else if (nDataConses == 1) {
            // if (ordinalVar == 0) 
            // then dc0Expr 
            // else error;
            inPart = SourceModel.Expr.If.make( 
                    SourceModel.Expr.BinaryOp.Equals.make(ordinalVar, SourceModel.Expr.makeIntValue(0)),
                    clauseExprs[0],
                    terminatingElse);
            
        } else {
            // case ordinalVar of
            // 0 -> dc0Expr;
            // 1 -> dc1Expr;
            // ...
            // _ -> error;
            SourceModel.Expr.Case.Alt[] caseAlts = new SourceModel.Expr.Case.Alt[nDataConses + 1];
            
            // The int cases.
            for (int i = 0; i < nDataConses; i++) {
                caseAlts[i] = SourceModel.Expr.Case.Alt.UnpackInt.make(new BigInteger[]{BigInteger.valueOf(i)}, clauseExprs[i]);
            }
            
            // The default case.
            caseAlts[nDataConses] = SourceModel.Expr.Case.Alt.Default.make(terminatingElse);
            
            // Create the in part..
            inPart = SourceModel.Expr.Case.make(ordinalVar, caseAlts);
        }
        
        return SourceModel.Expr.Let.make(localDefns, inPart);
    }
    
    /**
     * If a type M.T has Prelude.Outputable in the deriving clause, then this is the
     * definition of the M.$output$T instance function which implements the
     * Prelude.output class method for the type T.
     * 
     * @param typeCons TypeConstructor of the type to construct an instance function for
     * @return the source model for the output instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */        
    SourceModel.FunctionDefn.Algebraic makeAlgebraicOutputInstanceMethod(TypeConstructor typeCons) {
        
        if (typeCons.getNDataConstructors() == 0) {
            throw new IllegalArgumentException("makeAlgebraicOutputInstanceMethod does not work for non-algebraic types.");
        }
        
        /*
         * For the data type
         *  
         *  data public Maybe a = Nothing | Just value :: a deriving Outputable;
         * 
         * we will generate
         * 
         *  private $output$Maybe !value = 
         *      case value of
         *      Prelude.Nothing -> Prelude.output (Prelude.internal_algebraicValue_new0 "Prelude.Nothing" (0 :: Prelude.Int));
         *      Prelude.Just {value} -> Prelude.output (Prelude.internal_algebraicValue_new1 "Prelude.Just" (1 :: Prelude.Int) (Prelude.output value));
         *      ;
         *      
         * data public Tuple2 a b = Tuple2 field1::a field2::b deriving Outputable; //in the LegacyTuple module      
         *      
         * private $output$Tuple2 !value = 
         *      case value of
         *      LegacyTupe.Tuple2 {field1, field2} ->
         *          Prelude.output
         *              (Prelude.internal_algebraicValue_new
         *                   "LegacyTuple.Tuple2"
         *                    (0 :: Prelude.Int)
         *                    (Prelude.input (Prelude.output (field1, field2))));                    
         *      ;     
         */
        
        String functionName = makeInstanceFunctionUnqualifiedName("output", typeCons);
        
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {
                SourceModel.Parameter.make("value", true)
        };
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                makeAlgebraicOutputFunctionExpr(typeCons, parameters[0]));
                
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }

        return functionDefn;
    }
    
    /**
     * Builds the defining expression part of a generated algebraic-type output function
     * @param typeCons The TypeConstructor of the type to build an output function for
     * @param parameter The name of the parameter to the function
     * @return A SourceModel suitable for use as the defining expression of an output function for an algebraic type
     */
    private SourceModel.Expr makeAlgebraicOutputFunctionExpr(TypeConstructor typeCons, SourceModel.Parameter parameter) {
        
        //see the comment in makeAlgebraicOutputInstanceMethod for examples of the generated code.
                
        SourceModel.Expr.Case.Alt[] alternatives = new SourceModel.Expr.Case.Alt[typeCons.getNDataConstructors()];
        
        for(int i = 0; i < typeCons.getNDataConstructors(); i++) {
            
            DataConstructor dataCons = typeCons.getNthDataConstructor(i); 
            SourceModel.Name.DataCons dataConsName = SourceModel.Name.DataCons.make(dataCons.getName());
            
            final int dataConsArity = dataCons.getArity();
                                              
            switch (dataConsArity) {
                case 0:
                {
                    SourceModel.Expr altExpr = 
                        SourceModel.Expr.makeGemCall(
                            CAL_Prelude.Functions.output,                            
                            SourceModel.Expr.makeGemCall(
                                CAL_Prelude_internal.Functions.internal_algebraicValue_new0,                                                     
                                SourceModel.Expr.makeStringValue(dataCons.getName().getQualifiedName()),
                                SourceModel.Expr.makeIntValue(i)));  
                    
                    alternatives[i] = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, new SourceModel.FieldPattern[0], altExpr);
                    break;                    
                }
                
                case 1:
                {                     
                    SourceModel.FieldPattern[] patterns = makeFieldPatterns(dataCons, ""); 

                    SourceModel.Expr altExpr =
                        SourceModel.Expr.makeGemCall(
                            CAL_Prelude.Functions.output,                          
                            SourceModel.Expr.makeGemCall(
                                CAL_Prelude_internal.Functions.internal_algebraicValue_new1,
                                SourceModel.Expr.makeStringValue(dataCons.getName().getQualifiedName()),
                                SourceModel.Expr.makeIntValue(i),
                                SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.output, SourceModel.Expr.Var.makeUnqualified(getPatternNameFromFieldPattern(patterns[0])))));  
                    
                    alternatives[i] = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, patterns, altExpr);
                    break;
                }
                
                default:
                {                                 
                    SourceModel.FieldPattern[] patterns = makeFieldPatterns(dataCons, "");
                    SourceModel.Expr[] components = new SourceModel.Expr[dataConsArity];
                    
                    for(int j = 0; j < dataConsArity; j++) { 
                        String varName = getPatternNameFromFieldPattern(patterns[j]);
                        components[j] = SourceModel.Expr.Var.makeUnqualified(varName);                    
                    }
                    
                    //we are doing Prelude.output (v0, v1, ..., vk), which will produce a JObject of actual type java.util.List
                    //and then inputting this back as a JList to downcast so that the typechecker will accept it as an argument
                    //for algebraicValue_new
                    SourceModel.Expr javaList =  
                        SourceModel.Expr.makeGemCall(
                            CAL_Prelude.Functions.input,
                            SourceModel.Expr.makeGemCall(
                                CAL_Prelude.Functions.output,
                                SourceModel.Expr.Tuple.make(components)));
                                                   
                    SourceModel.Expr altExpr = 
                        SourceModel.Expr.makeGemCall(                        
                            CAL_Prelude.Functions.output,
                            SourceModel.Expr.makeGemCall(
                                CAL_Prelude_internal.Functions.internal_algebraicValue_new,
                                SourceModel.Expr.makeStringValue(dataCons.getName().getQualifiedName()),
                                SourceModel.Expr.makeIntValue(i),
                                javaList));
                    
                    alternatives[i] = SourceModel.Expr.Case.Alt.UnpackDataCons.make(dataConsName, patterns, altExpr);                    
                    break;
                }
            }                       
        }
        
        return SourceModel.Expr.Case.make(SourceModel.Expr.Var.makeUnqualified(parameter.getName()), alternatives);
    }

    /**
     * This creates a helper function for Enum instances -
     * it is a type safe conversion from an integer to a Enum value, it uses a case statement
     * 
     * data TestEnum = One | Two | Three | Four;
     * 
     * $fromIntHelper$TestEnum :: Int -> TestEnum;
     * $fromIntHelper$TestEnum !x = 
     * case x of
     *   0 -> One;
     *   1 -> Two;
     *   2 -> Three;
     *   3 -> Four;
     *   ;
     * @param typeCons
     * @return fromIntHelper function
     */
    SourceModel.FunctionDefn.Algebraic makeFromIntHelper(TypeConstructor typeCons) {
 
        if (!TypeExpr.isEnumType(typeCons)) {
            throw new IllegalArgumentException("makeEnumUpFromThenInstanceFunction only works for enumeration types");
        }
 
        String functionName = makeInstanceFunctionUnqualifiedName(fromIntHelper, typeCons);
        
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
            SourceModel.Parameter.make("value", true)
        };
        
        SourceModel.Expr.Var value = SourceModel.Expr.Var.makeUnqualified("value");

        
        Alt[] caseAlts = new Alt[typeCons.getNDataConstructors()];
        for(int i=0; i< typeCons.getNDataConstructors(); i++) {
            BigInteger[] x = { BigInteger.valueOf(i) };
            caseAlts[i] = Alt.UnpackInt.make( x, SourceModel.Expr.DataCons.make(typeCons.getNthDataConstructor(i).getName()));
        }
        
        SourceModel.Expr inExpr =
            SourceModel.Expr.Case.make(value, caseAlts);
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                    functionName,
                    Scope.PRIVATE,
                    parameters,
                    inExpr);
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }

        return functionDefn;
        
    }
    
    /**
     * This creates a helper function for Enum instances -
     * it is a type safe conversion from an Enum to an integer value, it uses a case statement, e.g.:
     * 
     * data TestEnum = One | Two | Three | Four;
     * 
     * $toIntHelper$TestEnum :: TestEnum -> Int;
     * $toIntHelper$TestEnum !x =
     *   case x of
     *   One -> 0;
     *   Two -> 1;
     *   Three -> 2;
     *   Four -> 3;
     *   ;
     * @param typeCons
     * @return toIntHelper function
     */
    SourceModel.FunctionDefn.Algebraic makeToIntHelper(TypeConstructor typeCons) {
 
        if (!TypeExpr.isEnumType(typeCons)) {
            throw new IllegalArgumentException("makeEnumUpFromThenInstanceFunction only works for enumeration types");
        }
 
        String functionName = makeInstanceFunctionUnqualifiedName(toIntHelper, typeCons);
        
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
            SourceModel.Parameter.make("value", true)
        };
        
        SourceModel.Expr.Var value = SourceModel.Expr.Var.makeUnqualified("value");

        
        Alt[] caseAlts = new Alt[typeCons.getNDataConstructors()];
        for(int i=0; i< typeCons.getNDataConstructors(); i++) {
            caseAlts[i] = Alt.UnpackDataCons.make(SourceModel.Name.DataCons.make(typeCons.getNthDataConstructor(i).getName()), SourceModel.Expr.makeIntValue(i));
        }
        
        SourceModel.Expr inExpr =
            SourceModel.Expr.Case.make(value, caseAlts);
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                    functionName,
                    Scope.PRIVATE,
                    parameters,
                    inExpr);
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }

        return functionDefn;        
    }
    
    /**
     * This creates a helper function for the upFromThenTo instance.
     * 
     * The function has the following form:
     * 
     * data TestEnum = One | Two | Three | Four;
     * 
     * helperDown :: Int -> Int -> Int -> [TestEnum];
     * helperDown !start !end !step =
     *   if (start < end) then
     *      []
     *   else
     *      (fromIntHelper start) : helperUp (start + step) end step;
     *      
     * @param typeCons
     * @return the upFromThenToGelperDown function
     */
    SourceModel.FunctionDefn.Algebraic makeUpFromThenToHelperDown(TypeConstructor typeCons) {
        String functionName = makeInstanceFunctionUnqualifiedName(upFromThenToHelperDown, typeCons);
        String fromIntName = makeInstanceFunctionUnqualifiedName(fromIntHelper, typeCons);
    
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
            SourceModel.Parameter.make("start", true), 
            SourceModel.Parameter.make("end", true),
            SourceModel.Parameter.make("step", true) 
        };
    
        SourceModel.Expr.Var startVar = SourceModel.Expr.Var.makeUnqualified("start");
        SourceModel.Expr.Var endVar = SourceModel.Expr.Var.makeUnqualified("end");
        SourceModel.Expr.Var stepVar = SourceModel.Expr.Var.makeUnqualified("step");
    
        SourceModel.Expr helperFunc = 
            SourceModel.Expr.If.make(
                SourceModel.Expr.BinaryOp.LessThan.make(startVar, endVar), 
                SourceModel.Expr.List.EMPTY_LIST, 
                SourceModel.Expr.BinaryOp.Cons.make(
                    CAL_Prelude.Functions.eager(
                        SourceModel.Expr.Application.make(
                            new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, fromIntName), startVar} )), 
                        SourceModel.Expr.Application.make(
                            new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, functionName), 
                                SourceModel.Expr.BinaryOp.Add.make(startVar, stepVar), 
                                endVar, stepVar })));
    
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                helperFunc);
    
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
    
        return functionDefn;
    }

    /**
     * This creates a helper function for the upFromThenTo instance.
     * 
     * The function has the following form:
     * 
     * data TestEnum = One | Two | Three | Four;
     * 
     * helperUp :: Int -> Int -> Int -> [TestEnum];
     * helperUp !start !end !step =
     *   if (start > end) then
     *      []
     *   else
     *      (eager $ myFromInt start) : helperUp (start + step) end step;
     *      
     * @param typeCons
     * @return the upFromThenToHelper function
     */
    SourceModel.FunctionDefn.Algebraic makeUpFromThenToHelperUp(TypeConstructor typeCons) {
        String functionName = makeInstanceFunctionUnqualifiedName(upFromThenToHelperUp, typeCons);
        String fromIntName = makeInstanceFunctionUnqualifiedName(fromIntHelper, typeCons);
    
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
            SourceModel.Parameter.make("start", true), 
            SourceModel.Parameter.make("end", true),
            SourceModel.Parameter.make("step", true) 
        };
    
        SourceModel.Expr.Var startVar = SourceModel.Expr.Var.makeUnqualified("start");
        SourceModel.Expr.Var endVar = SourceModel.Expr.Var.makeUnqualified("end");
        SourceModel.Expr.Var stepVar = SourceModel.Expr.Var.makeUnqualified("step");
    
        SourceModel.Expr helperFunc = 
            SourceModel.Expr.If.make(
                SourceModel.Expr.BinaryOp.GreaterThan.make(startVar, endVar), 
                SourceModel.Expr.List.EMPTY_LIST, 
                SourceModel.Expr.BinaryOp.Cons.make(
                    SourceModel.Expr.Application.make(
                        new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, fromIntName), startVar} ), 
                        SourceModel.Expr.Application.make(
                            new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, functionName), 
                                SourceModel.Expr.BinaryOp.Add.make(startVar, stepVar), 
                                endVar, stepVar })));
    
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                helperFunc);
    
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
    
        return functionDefn;
    }

    /**
     * This creates a helper function for upFromTo. It has the following form:
     * 
     * upFromToHelper !start !end = 
     * if (start > end) then 
     *   []
     * else
     *   fromIntHelper start : upFromToHelper (start + 1) end;
     *   
     * @param typeCons
     * @return instance function
     */
    SourceModel.FunctionDefn.Algebraic makeUpFromToHelper(TypeConstructor typeCons) {
        String functionName = makeInstanceFunctionUnqualifiedName("upFromToHelper", typeCons);
        String fromIntName = makeInstanceFunctionUnqualifiedName(fromIntHelper, typeCons);

        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
            SourceModel.Parameter.make("start", true), 
            SourceModel.Parameter.make("end", true) 
        };

        SourceModel.Expr.Var startVar = SourceModel.Expr.Var.makeUnqualified("start");
        SourceModel.Expr.Var endVar = SourceModel.Expr.Var.makeUnqualified("end");
        SourceModel.Expr helperFunc = 
            SourceModel.Expr.If.make(
                SourceModel.Expr.BinaryOp.GreaterThan.make(startVar, endVar), 
                SourceModel.Expr.List.EMPTY_LIST, 
                SourceModel.Expr.BinaryOp.Cons.make(
                    CAL_Prelude.Functions.eager(
                        SourceModel.Expr.Application.make(
                            new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, fromIntName), startVar} )), 
                        SourceModel.Expr.Application.make(
                            new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, functionName), 
                                SourceModel.Expr.BinaryOp.Add.make(startVar, SourceModel.Expr.makeIntValue(1)), 
                                endVar })));
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                helperFunc);

        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }

        return functionDefn;
    }
    
    
    /**
     * This create a the upFrom function for the enum instance. It has the following form:
     * 
     *  data TestEnum = One | Two | Three | Four;
     * 
     *  $upFrom$TestEnum !item = 
     *    $upFromToHelper$TestEnum (toIntHelper$TestEnum item) 3;
     *    
     * @param typeCons
     * @return the instance function
     */
    SourceModel.FunctionDefn.Algebraic makeEnumUpFromInstanceFunction(TypeConstructor typeCons) {
        if (!TypeExpr.isEnumType(typeCons)) {
            throw new IllegalArgumentException("makeSafeEnumUpFromToInstanceFunction only works for enumeration types");
        }
        
        String toIntName = makeInstanceFunctionUnqualifiedName(toIntHelper, typeCons);
        String upFromToHelper = makeInstanceFunctionUnqualifiedName("upFromToHelper", typeCons);
        
        String functionName = makeInstanceFunctionUnqualifiedName(CAL_Prelude.Functions.upFrom.getUnqualifiedName(), typeCons);
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
                SourceModel.Parameter.make("start", true)
                };
        
        SourceModel.Expr.Var startVar = SourceModel.Expr.Var.makeUnqualified("start");
        
        SourceModel.Expr bodyExpr = SourceModel.Expr.Application.make(
            new SourceModel.Expr[] { 
                SourceModel.Expr.Var.makeInternal(null, upFromToHelper),
                SourceModel.Expr.Application.make(
                    new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, toIntName), startVar} ),
                SourceModel.Expr.makeIntValue(typeCons.getNDataConstructors() -1)  
            }
        );  
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                    functionName,
                    Scope.PRIVATE,
                    parameters,
                    bodyExpr);
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
    
        return functionDefn;        
    }

    /**
     * This builds the upFromTo instance function. It has the following form:
     * 
     * data TestEnum = One | Two | Three | Four;
     * 
     * upFromTo :: TestEnum -> TestEnum -> [TestEnum];
     * upFromTo !start !end =
     *   upFromToHelper (toInt start) (toInt end);
     *     
     * @param typeCons
     * @return the instance function
     */
    SourceModel.FunctionDefn.Algebraic makeEnumUpFromToInstanceFunction(TypeConstructor typeCons) {
        
        if (!TypeExpr.isEnumType(typeCons)) {
            throw new IllegalArgumentException("makeSafeEnumUpFromToInstanceFunction only works for enumeration types");
        }
        
        String toIntName = makeInstanceFunctionUnqualifiedName(toIntHelper, typeCons);
        String upFromToHelper = makeInstanceFunctionUnqualifiedName("upFromToHelper", typeCons);
        
        String functionName = makeInstanceFunctionUnqualifiedName(CAL_Prelude.Functions.upFromTo.getUnqualifiedName(), typeCons);
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
                SourceModel.Parameter.make("start", true), 
                SourceModel.Parameter.make("end", true) 
                };
        
        SourceModel.Expr.Var startVar = SourceModel.Expr.Var.makeUnqualified("start");
        SourceModel.Expr.Var endVar = SourceModel.Expr.Var.makeUnqualified("end");
        
        SourceModel.Expr bodyExpr = SourceModel.Expr.Application.make(
            new SourceModel.Expr[] { 
                SourceModel.Expr.Var.makeInternal(null, upFromToHelper),
                SourceModel.Expr.Application.make(
                    new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, toIntName), startVar} ),
                SourceModel.Expr.Application.make(
                    new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, toIntName), endVar} )  
            }
        );  
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                    functionName,
                    Scope.PRIVATE,
                    parameters,
                    bodyExpr);
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
    
        return functionDefn;
    }

    /**
     * this creates the upFromThen instance function. It has the following form:
     * 
     * data TestEnum = One | Two | Three | Four;
     * 
     * upFromThen :: TestEnum -> TestEnum -> TestEnum -> [TestEnum];
     * upFromThen !start !next =
     * let
     *   i :: Int;
     *   i = Prelude.eager $ toIntHelper start;
     *   
     *   step :: Int;
     *   step = Prelude.eager $ myToInt next - i;
     *   
     * in
     *    if (step >= 0) then 
     *        helperUp i 3 step
     *    else
     *        helperDown i 0 step;
     *
     * @param typeCons
     * @return the upFtomThenTo instance function defn.
     */
    SourceModel.FunctionDefn.Algebraic makeEnumUpFromThenInstanceFunction(TypeConstructor typeCons) {
        String functionName = makeInstanceFunctionUnqualifiedName(CAL_Prelude.Functions.upFromThen.getUnqualifiedName(), typeCons);
        String toIntName = makeInstanceFunctionUnqualifiedName(toIntHelper, typeCons);
        String upHelperName = makeInstanceFunctionUnqualifiedName(upFromThenToHelperUp, typeCons);
        String downHelperName = makeInstanceFunctionUnqualifiedName(upFromThenToHelperDown, typeCons);
    
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
            SourceModel.Parameter.make("start", true), 
            SourceModel.Parameter.make("next", true)
        };
    
        SourceModel.Expr.Var startVar = SourceModel.Expr.Var.makeUnqualified("start");
        SourceModel.Expr.Var nextVar = SourceModel.Expr.Var.makeUnqualified("next");
    
        SourceModel.Expr.Var iVar = SourceModel.Expr.Var.makeUnqualified("i");
        SourceModel.Expr.Var stepVar = SourceModel.Expr.Var.makeUnqualified("step");
    
        SourceModel.LocalDefn[] localDefns = {     
            SourceModel.LocalDefn.Function.Definition.make(
                "i", 
                SourceModel.LocalDefn.Function.Definition.NO_PARAMETERS,
                CAL_Prelude.Functions.eager(SourceModel.Expr.Application.make(
                    new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, toIntName), startVar } ))),
    
            SourceModel.LocalDefn.Function.Definition.make(
                "step", 
                SourceModel.LocalDefn.Function.Definition.NO_PARAMETERS,
                CAL_Prelude.Functions.eager(
                    SourceModel.Expr.BinaryOp.Subtract.make(
                            SourceModel.Expr.Application.make(
                                new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, toIntName), nextVar} ), 
                                iVar) )) };
    
        //define the function body
        SourceModel.Expr body = SourceModel.Expr.If.make(
            //if
            SourceModel.Expr.BinaryOp.GreaterThanEquals.make(
                stepVar, 
                SourceModel.Expr.makeIntValue(0)), 
            //then
            SourceModel.Expr.Application.make(
                new SourceModel.Expr[] { 
                    SourceModel.Expr.Var.makeInternal(null, upHelperName), 
                    iVar, 
                    SourceModel.Expr.makeIntValue(typeCons.getNDataConstructors() -1), 
                    stepVar}),
            //else
            SourceModel.Expr.Application.make(
                new SourceModel.Expr[] { 
                    SourceModel.Expr.Var.makeInternal(null, downHelperName), 
                    iVar, 
                    SourceModel.Expr.makeIntValue(0), 
                    stepVar}));
    
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                SourceModel.Expr.Let.make(localDefns, body));
    
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
    
        return functionDefn;
    
    }
    /**
     * this creates the upFromThenTo instance function. It has the following form:
     * 
     * data TestEnum = One | Two | Three | Four;
     * 
     * myUpFromThenTo :: TestEnum -> TestEnum -> TestEnum -> [TestEnum];
     * myUpFromThenTo !start !next !end=
     * let
     *   i :: Int;
     *   i = Prelude.eager $ myToInt start;
     *   
     *   step :: Int;
     *   step = Prelude.eager $ myToInt next - i;
     *   
     *   j :: Int;
     *   j = Prelude.eager $ myToInt end;
     * in
     *    if (step >= 0) then 
     *        helperUp i j step
     *    else
     *        helperDown i j step;
     *
     * @param typeCons
     * @return the upFtomThenTo instance function defn.
     */
    SourceModel.FunctionDefn.Algebraic makeEnumUpFromThenToInstanceFunction(TypeConstructor typeCons) {
        String functionName = makeInstanceFunctionUnqualifiedName(CAL_Prelude.Functions.upFromThenTo.getUnqualifiedName(), typeCons);
        String toIntName = makeInstanceFunctionUnqualifiedName(toIntHelper, typeCons);
        String upHelperName = makeInstanceFunctionUnqualifiedName(upFromThenToHelperUp, typeCons);
        String downHelperName = makeInstanceFunctionUnqualifiedName(upFromThenToHelperDown, typeCons);

        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] { 
            SourceModel.Parameter.make("start", true), 
            SourceModel.Parameter.make("next", true),
            SourceModel.Parameter.make("end", true) 
        };

        SourceModel.Expr.Var startVar = SourceModel.Expr.Var.makeUnqualified("start");
        SourceModel.Expr.Var nextVar = SourceModel.Expr.Var.makeUnqualified("next");
        SourceModel.Expr.Var endVar = SourceModel.Expr.Var.makeUnqualified("end");

        SourceModel.Expr.Var iVar = SourceModel.Expr.Var.makeUnqualified("i");
        SourceModel.Expr.Var jVar = SourceModel.Expr.Var.makeUnqualified("j");
        SourceModel.Expr.Var stepVar = SourceModel.Expr.Var.makeUnqualified("step");

        //define the let expressions
        SourceModel.LocalDefn[] localDefns = {     
            SourceModel.LocalDefn.Function.Definition.make(
                "i", 
                SourceModel.LocalDefn.Function.Definition.NO_PARAMETERS,
                CAL_Prelude.Functions.eager(SourceModel.Expr.Application.make(
                    new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, toIntName), startVar } ))),

            SourceModel.LocalDefn.Function.Definition.make(
                "step", 
                SourceModel.LocalDefn.Function.Definition.NO_PARAMETERS,
                CAL_Prelude.Functions.eager(
                    SourceModel.Expr.BinaryOp.Subtract.make(
                            SourceModel.Expr.Application.make(
                                new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, toIntName), nextVar} ), 
                                iVar) )),
                                
            SourceModel.LocalDefn.Function.Definition.make(
                "j", 
                SourceModel.LocalDefn.Function.Definition.NO_PARAMETERS,
                CAL_Prelude.Functions.eager(SourceModel.Expr.Application.make(
                    new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, toIntName), endVar } )))
        };
        
        //define the function body
        SourceModel.Expr body = SourceModel.Expr.If.make(
            SourceModel.Expr.BinaryOp.GreaterThanEquals.make(
                stepVar, 
                SourceModel.Expr.makeIntValue(0)), 
            SourceModel.Expr.Application.make(
                new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, upHelperName), iVar, jVar, stepVar}),
            SourceModel.Expr.Application.make(
                new SourceModel.Expr[] { SourceModel.Expr.Var.makeInternal(null, downHelperName), iVar, jVar, stepVar}));
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                functionName,
                Scope.PRIVATE,
                parameters,
                SourceModel.Expr.Let.make(localDefns, body));

        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }

        return functionDefn;
    }
    
    
    /**
     * Builds an intToEnumChecked instance function for enumerated data types.
     * For example, for the type
     * 
     * data Numeric = One | Two | Three | Four deriving IntEnum;
     * 
     * we will generate: 
     * 
     *  private $intToEnumChecked$Numeric !intVal = 
     *      case intValue of
     *      0 -> Just One;
     *      1 -> Just Two;
     *      2 -> Just Three;
     *      _ -> Nothing
     *      ;
     * 
     * @param typeCons TypeConstructor of the type to build an intToEnumChecked function for
     * @return the source model for the intToEnumChecked instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeEnumIntToEnumCheckedFunction(TypeConstructor typeCons) {
        
        if (!TypeExpr.isEnumType(typeCons)) {
            throw new IllegalArgumentException("makeEnumIntToEnumFunction only works for enumeration types");
        }
        
        String fromIntName = makeInstanceFunctionUnqualifiedName(fromIntHelper, typeCons);        
        String functionName = makeInstanceFunctionUnqualifiedName(CAL_Prelude.Functions.intToEnumChecked.getUnqualifiedName(), typeCons);
        
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {
                SourceModel.Parameter.make("intVal", true)
                };
        
        SourceModel.Expr.Var intValVar = SourceModel.Expr.Var.makeUnqualified("intVal");
        
        SourceModel.Expr inExpr =
            SourceModel.Expr.If.make(
                    SourceModel.Expr.BinaryOp.Or.make(
                            SourceModel.Expr.BinaryOp.LessThan.make(intValVar, SourceModel.Expr.makeIntValue(0)),
                            SourceModel.Expr.BinaryOp.GreaterThan.make(intValVar, SourceModel.Expr.makeIntValue(typeCons.getNDataConstructors() - 1))),
                    SourceModel.Expr.DataCons.make(CAL_Prelude.DataConstructors.Nothing),
                    SourceModel.Expr.makeGemCall(
                            CAL_Prelude.DataConstructors.Just,
                            SourceModel.Expr.Application.make(
                                new SourceModel.Expr[] { 
                                         SourceModel.Expr.Var.makeInternal(null, fromIntName),
                                         intValVar})));
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                    functionName,
                    Scope.PRIVATE,
                    parameters,
                    inExpr);
        
        if(SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
        
        return functionDefn;
    }

    /**
     * Builds an intToEnum instance function for enumerated data types.
     * For example, for the type
     * 
     *  data Numeric = One | Two | Three | Four deriving IntEnum;
     * 
     * we will generate:
     *  
     *  private $intToEnum$Numeric !intVal = 
     *      if (intVale >= 0 && intValue < 4) then
     *          intToEnum
     *      _ -> Prelude.error (Prelude.concat ["argument (", Prelude.intToString intVal, ") does not correspond to a value of type Numeric"])
     *      ;
     *      
     * @param typeCons TypeConstructor of the type to build an intToEnum function for
     * @return the source model for the intToEnum instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeEnumIntToEnumFunction(TypeConstructor typeCons) {
        if (!TypeExpr.isEnumType(typeCons)) {
            throw new IllegalArgumentException("makeEnumIntToEnumFunction only works for enumeration types");
        }
        
        String fromIntName = makeInstanceFunctionUnqualifiedName(fromIntHelper, typeCons);        
        String functionName = makeInstanceFunctionUnqualifiedName(CAL_Prelude.Functions.intToEnum.getUnqualifiedName(), typeCons);
        
        SourceModel.Parameter[] parameters = new SourceModel.Parameter[] {
                SourceModel.Parameter.make("intVal", true)
                };
        
        SourceModel.Expr.Var intValVar = SourceModel.Expr.Var.makeUnqualified("intVal");
        
        SourceModel.Expr inExpr =
            SourceModel.Expr.If.make(
                    SourceModel.Expr.BinaryOp.Or.make(
                            SourceModel.Expr.BinaryOp.LessThan.make(intValVar, SourceModel.Expr.makeIntValue(0)),
                            SourceModel.Expr.BinaryOp.GreaterThan.make(intValVar, SourceModel.Expr.makeIntValue(typeCons.getNDataConstructors() - 1))),
                    SourceModel.Expr.makeGemCall(
                            //TODO This call to Prelude.error may need to be localized at some point 
                            CAL_Prelude.Functions.error,
                            SourceModel.Expr.makeGemCall(
                                    CAL_Prelude.Functions.concat,
                                    SourceModel.Expr.List.make(new SourceModel.Expr[] {
                                        SourceModel.Expr.Literal.StringLit.make("argument ("),
                                        SourceModel.Expr.makeGemCall(
                                                CAL_Prelude.Functions.intToString,
                                                intValVar),
                                        SourceModel.Expr.Literal.StringLit.make(") does not correspond to a value of type " + typeCons.getName())
                                    }))),
                    SourceModel.Expr.Application.make(
                             new SourceModel.Expr[] { 
                                      SourceModel.Expr.Var.makeInternal(null, fromIntName),
                                      intValVar}));
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                    functionName,
                    Scope.PRIVATE,
                    parameters,
                    inExpr);
        
        if(SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
        
        return functionDefn;
    }

    /**
     * Builds the definition of Arbitrary for an enumeration type
     * example, for the type
     * 
     * data Numeric = One | Two | Three | Four deriving Arbitrary;
     * 
     *  private $arbitrary$Numeric = 
     *      mapGen fromIntHelper (makeBoundedIntGen 0 4)
     *      
     * @param typeCons
     *            TypeConstructor of the type to build an intToEnum function for
     * @return the source model for the arbitrary instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeArbitraryFunction(TypeConstructor typeCons) {
        
        if(!TypeExpr.isEnumType(typeCons)) {
            throw new IllegalArgumentException("makeArbitraryFunction only works for enumeration types");
        }

        String functionName = makeInstanceFunctionUnqualifiedName(CAL_QuickCheck.Functions.arbitrary.getUnqualifiedName(), typeCons);
        String fromIntName = makeInstanceFunctionUnqualifiedName(fromIntHelper, typeCons);

        SourceModel.Expr inExpr = CAL_QuickCheck.Functions.mapGen(
            SourceModel.Expr.Var.makeInternal(null, fromIntName), 
            CAL_QuickCheck.Functions.makeBoundedIntGen(
                SourceModel.Expr.makeIntValue(0), 
                SourceModel.Expr.makeIntValue(typeCons.getNDataConstructors())));
        
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                    functionName,
                    Scope.PRIVATE,
                    null,
                    inExpr);
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }
        
        return functionDefn;
    }

    /**
     * Builds the definition of CoArbitrary for an enumeration type
     * example, for the type
     * 
     * data Numeric = One | Two | Three | Four deriving Arbitrary;
     * private $coarbitrary$Numeric value gen = 
     *      coarbitrary (toIntHelper value) gen;
     * 
     * @param typeCons
     *            TypeConstructor of the type to build a coarbitrary function for
     * @return the source model for the coarbitrary instance function. Will be an
     *         internal function (i.e. its text is not parseable as a CAL
     *         function by the parser).
     */
    SourceModel.FunctionDefn.Algebraic makeCoArbitraryFunction(TypeConstructor typeCons) {
        
        if(!TypeExpr.isEnumType(typeCons)) {
            throw new IllegalArgumentException("makeCoArbitraryFunction only works for enumeration types");
        }

        String functionName = makeInstanceFunctionUnqualifiedName(CAL_QuickCheck.Functions.coarbitrary.getUnqualifiedName(), typeCons);
        String toIntName = makeInstanceFunctionUnqualifiedName(toIntHelper, typeCons);

        
        SourceModel.Parameter[] parameters = 
            new SourceModel.Parameter[] { 
                SourceModel.Parameter.make("value", true),
                SourceModel.Parameter.make("gen", false) 
            };
        
        SourceModel.Expr.Var inputValue = SourceModel.Expr.Var.makeUnqualified("value");
        SourceModel.Expr.Var inputGen = SourceModel.Expr.Var.makeUnqualified("gen");

        //an integer value representing the input value
        SourceModel.Expr intRep = 
            SourceModel.Expr.Application.make(
                new SourceModel.Expr[] { 
                    SourceModel.Expr.Var.makeInternal(null, toIntName), inputValue} );
                    
        SourceModel.Expr inExpr = 
            CAL_QuickCheck.Functions.coarbitrary(intRep, inputGen);
   
        SourceModel.FunctionDefn.Algebraic functionDefn =
            makeAlgebraicFunctionDefn(
                    functionName,
                    Scope.PRIVATE,
                    parameters,
                    inExpr);
        
        if (SHOW_FUNCTION_DEFN) {
            System.out.println(functionDefn);
        }

        return functionDefn;
    }


    /**
     * @return an array of two {@link SourceModel.Parameter}s that are strict.
     */
    private static SourceModel.Parameter[] makeTwoStrictParameters() {
        return new SourceModel.Parameter[] {
            SourceModel.Parameter.make("x", true),
            SourceModel.Parameter.make("y", true)};
    }
    
    /**
     * Returns an array of case expression field-matching patterns constructed according to the given data constructor.
     * @param dataCons the data constructor for the case alternative.
     * @param patternSuffix the suffix to include with each pattern.
     * @return the array of field-matching patterns.
     */
    private static SourceModel.FieldPattern[] makeFieldPatterns(DataConstructor dataCons, String patternSuffix) {
        
        final int dataConsArity = dataCons.getArity();
        
        SourceModel.FieldPattern[] innerPatterns = new SourceModel.FieldPattern[dataConsArity];
        for (int j = 0; j < dataConsArity; ++j) {
            final FieldName fieldName = dataCons.getNthFieldName(j);
            String varName;
            
            if (fieldName instanceof FieldName.Ordinal) {
                // For an ordinal field #1, we try to use a pattern named field1. If another field
                // named field1 is already in the data constructor, we try field1_1, field1_2, field1_3, etc.
                // until a pattern is found that does not collide with another field name.
                String baseName = "field" + ((FieldName.Ordinal)fieldName).getOrdinal();
                varName = baseName;
                int disambiguator = 1;
                while (dataCons.getFieldIndex(FieldName.makeTextualField(varName)) != -1) {
                    varName = baseName + "_" + disambiguator;
                    disambiguator++;
                }
                varName += patternSuffix;
            } else if (fieldName instanceof FieldName.Textual) {
                varName = ((FieldName.Textual)fieldName).getCalSourceForm() + patternSuffix;
            } else {
                throw new IllegalStateException("The name " + fieldName + " is neither an Ordinal or a Textual field name.");
            }
            
            SourceModel.Pattern.Var.Var pattern;
            if (varName.equals(fieldName.getCalSourceForm())) {
                // the varName is the same as the fieldName in source, so it can simply be a punned field
                // represented by a null pattern
                pattern = null;
            } else {
                pattern = SourceModel.Pattern.Var.make(varName);
            }
            
            innerPatterns[j] = SourceModel.FieldPattern.make(SourceModel.Name.Field.make(fieldName), pattern);
        }
        return innerPatterns;
    }
    
    /**
     * Returns the pattern name from a field pattern. This handles punned textual fields, and throws an InvalidArgumentException
     * on punned ordinal fields and wildcard patterns.
     * @param fieldPattern the field pattern.
     * @return the pattern name.
     */
    private static String getPatternNameFromFieldPattern(SourceModel.FieldPattern fieldPattern) {
        SourceModel.Pattern pattern = fieldPattern.getPattern();
        
        if (pattern == null) {
            // the field is punned
            FieldName fieldName = fieldPattern.getFieldName().getName();
            
            if (fieldName instanceof FieldName.Ordinal) {
                throw new IllegalArgumentException("No valid pattern name can be extracted from a punned ordinal field: " + fieldPattern);
            } else if (fieldName instanceof FieldName.Textual) {
                return ((FieldName.Textual)fieldName).getCalSourceForm();
            } else {
                throw new IllegalStateException("The name " + fieldName + " is neither an Ordinal or a Textual field name.");
            }
            
        } else if (pattern instanceof SourceModel.Pattern.Var) {
            return ((SourceModel.Pattern.Var)pattern).getName();
        } else {
            throw new IllegalArgumentException("No valid pattern name can be extracted from a wildcard pattern: " + fieldPattern);
        }
    }

    /**
     * Returns the source model of the specified function, which may have an internal name (i.e. starts with '$').
     * 
     * @param functionName the name of the CAL function.
     * @param scope the scope of the function.
     * @param parameters the parameters of the function.
     * @param definingExpr the defining expression of the function.
     * @return the source model for the function.
     */
    private SourceModel.FunctionDefn.Algebraic makeAlgebraicFunctionDefn(
        String functionName, Scope scope, SourceModel.Parameter[] parameters, SourceModel.Expr definingExpr) {
        
        if (shouldUseInternalNames) {
            return SourceModel.FunctionDefn.Algebraic.makeInternal(functionName, scope, parameters, definingExpr);
        } else {
            return SourceModel.FunctionDefn.Algebraic.make(functionName, scope, parameters, definingExpr);
        }
    }
    
    /**
     * Returns the unqualified name of the instance method for an internally defined class instance. The use of the '$' character
     * as a name separator is specified via the parameter useDollarCharAsNameSeparator.
     *   
     * @param classMethodName for example "equals" or "typeOf"
     * @param typeCons the type constructor of the instance
     * @return for example, "Prelude.$equals$Either" or "LegacyTuple.$typeOf$Tuple2"
     */
    private String makeInstanceFunctionUnqualifiedName(String classMethodName, TypeConstructor typeCons) {
        return makeInstanceMethodName(classMethodName, typeCons.getName()).getUnqualifiedName();
    }
    
    /**
     * Returns the name of the instance method for an internally defined class instance. The use of the '$' character
     * as a name separator is specified via the parameter useDollarCharAsNameSeparator.
     *   
     * @param classMethodName for example "equals" or "typeOf"
     * @param typeConsName for example, "Prelude.Either" or "LegacyTuple.Tuple2"
     * @return for example, "Prelude.$equals$Either" or "LegacyTuple.$typeOf$Tuple2"
     */
    private QualifiedName makeInstanceMethodName(String classMethodName, QualifiedName typeConsName) {
        if (shouldUseInternalNames) {
            return ClassInstance.makeInternalInstanceMethodName(classMethodName, typeConsName);
        } else {
            return makeExternalUseInstanceMethodName(classMethodName, typeConsName);
        }
    }

    /**
     * Returns the name of the instance method meant for external use. In particular this name will not contain the '$' character.
     *   
     * @param classMethodName for example "equals" or "typeOf"
     * @param typeConsName for example, "Prelude.Either" or "LegacyTuple.Tuple2"
     * @return for example, "Prelude.equalsEither" or "LegacyTuple.typeOfTuple2"
     */
    private static QualifiedName makeExternalUseInstanceMethodName(String classMethodName, QualifiedName typeConsName) {
        String instanceMethodName = classMethodName + typeConsName.getUnqualifiedName();
        return QualifiedName.make(typeConsName.getModuleName(), instanceMethodName);
    }

    ////--------------------------------------------------------------------------------------------------------------- 
    /// Externally accessible utility methods
    //
        
    /**
     * Returns an array of instance functions for an algebraic type.<p>
     * 
     * Note: This method is meant to be called by external clients only, and <b>not</b> by the compiler internals.<p>
     * 
     * Note that the definitions generated by this method are not the ones that are generated internally for
     * derived instances. Notable differences include:
     * <ul>
     * <li>Instance functions for derived instances all have names beginning with the '$' character, whereas the names of
     *     the functions generated by this method do not contain '$', as these functions are meant to be copy-and-pasted
     *     into a regular module and compiled as such.
     *     
     * <li>The Eq and Ord instances generated for enumeration types do not use unsafeCoerce 
     * but are implemented using case expressions.  
     * </ul>
     * 
     * Via the generateCompactImpl parameter, the caller can specify whether <i>compact</i> implementations are generated.
     * Compact implementations differ in the following aspects:
     * <ul>
     * <li>notEquals is implemented in terms of equals
     * <li>a full implementation is generated for compare, while the other Ord instance methods are implemented in terms of compare  
     * </ul>
     * 
     * @param moduleTypeInfo the module type info for the type's module.
     * @param typeCons the type constructor for which instance functions should be generated.
     * @param generateCompactImpl whether to generate the compact implementation or the full implementation
     * @param debugModuleTypeInfo the module type info for the Debug module.
     * @return an array of top level declarations for the instance functions for the type classes: Eq, Ord, Show
     */
    public static SourceModel.TopLevelSourceElement[] makeAlgebraicTypeInstanceFunctions(ModuleTypeInfo moduleTypeInfo, TypeConstructor typeCons, boolean generateCompactImpl, ModuleTypeInfo debugModuleTypeInfo) {
        
        QualifiedName typeConsName = typeCons.getName();
        
        if (typeCons.getNDataConstructors() == 0) {
            throw new IllegalArgumentException("The type " + typeConsName + " is not an algebraic type.");
        }
        
        DerivedInstanceFunctionGenerator generator = DerivedInstanceFunctionGenerator.makeExternalUse();
        
        boolean isUnitLikeType = typeCons.getNDataConstructors() == 1 && typeCons.getNthDataConstructor(0).getArity() == 0;
        
        final SourceModel.Name.TypeVar[] instanceTypeVars = makeTypeVars(typeCons.getTypeArity());

        TypeExpr booleanType = TypeExpr.makeNonParametricType(moduleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Boolean));
        TypeExpr orderingType = TypeExpr.makeNonParametricType(moduleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.Ordering));
        TypeExpr stringType = TypeExpr.makeNonParametricType(moduleTypeInfo.getVisibleTypeConstructor(CAL_Prelude.TypeConstructors.String));
        
        ////===========================================================================================
        /// Eq
        //
        TypeExpr eqInstanceType = ClassInstanceChecker.makeInstanceType(typeCons, moduleTypeInfo.getVisibleTypeClass(CAL_Prelude.TypeClasses.Eq));
        TypeExpr equalsTypeExpr = TypeExpr.makeFunType(eqInstanceType, TypeExpr.makeFunType(eqInstanceType, booleanType));
        
        final SourceModel.FunctionDefn.Algebraic equalsImpl;
        final SourceModel.FunctionDefn.Algebraic notEqualsImpl;
        
        equalsImpl = generator.makeEqualsInstanceFunction(typeCons);
        
        if (generateCompactImpl && !isUnitLikeType) {
            notEqualsImpl = generator.makeCompactNotEqualsImpl(typeCons);
        } else {
            notEqualsImpl = generator.makeNotEqualsInstanceFunction(typeCons);
        }
        
        SourceModel.InstanceDefn eqInstance = SourceModel.InstanceDefn.make(
            SourceModel.Name.TypeClass.make(CAL_Prelude.TypeClasses.Eq),
            makeInstanceTypeCons(typeCons, instanceTypeVars),
            makeInstanceConstraints(CAL_Prelude.TypeClasses.Eq, instanceTypeVars),
            new SourceModel.InstanceDefn.InstanceMethod[] {
                SourceModel.InstanceDefn.InstanceMethod.make("equals", SourceModel.Name.Function.makeUnqualified(equalsImpl.getName())),
                SourceModel.InstanceDefn.InstanceMethod.make("notEquals", SourceModel.Name.Function.makeUnqualified(notEqualsImpl.getName()))
            });

        ////===========================================================================================
        /// Ord
        //
        TypeExpr ordInstanceType = ClassInstanceChecker.makeInstanceType(typeCons, moduleTypeInfo.getVisibleTypeClass(CAL_Prelude.TypeClasses.Ord));
        TypeExpr compareTypeExpr = TypeExpr.makeFunType(ordInstanceType, TypeExpr.makeFunType(ordInstanceType, orderingType));
        TypeExpr lessThanTypeExpr = TypeExpr.makeFunType(ordInstanceType, TypeExpr.makeFunType(ordInstanceType, booleanType));
        TypeExpr maxTypeExpr = TypeExpr.makeFunType(ordInstanceType, TypeExpr.makeFunType(ordInstanceType, ordInstanceType));
        
        final SourceModel.FunctionDefn.Algebraic compareImpl;
        
        final SourceModel.FunctionDefn.Algebraic lessThanImpl;
        final SourceModel.FunctionDefn.Algebraic lessThanEqualsImpl;
        final SourceModel.FunctionDefn.Algebraic greaterThanEqualsImpl;
        final SourceModel.FunctionDefn.Algebraic greaterThanImpl;
        final SourceModel.FunctionDefn.Algebraic maxImpl;
        final SourceModel.FunctionDefn.Algebraic minImpl;
        
        compareImpl = generator.makeCompareInstanceFunction(typeCons);
        
        if (generateCompactImpl && !isUnitLikeType) {
            lessThanImpl = generator.makeCompactComparisonFunctionImpl(typeCons, OrderComparisonOperation.LESS_THAN);
            lessThanEqualsImpl = generator.makeCompactComparisonFunctionImpl(typeCons, OrderComparisonOperation.LESS_THAN_EQUALS);
            greaterThanEqualsImpl = generator.makeCompactComparisonFunctionImpl(typeCons, OrderComparisonOperation.GREATER_THAN_EQUALS);
            greaterThanImpl = generator.makeCompactComparisonFunctionImpl(typeCons, OrderComparisonOperation.GREATER_THAN);
            
            maxImpl = generator.makeCompactExtremumFunctionImpl(typeCons, true);
            minImpl = generator.makeCompactExtremumFunctionImpl(typeCons, false);
            
        } else {
            lessThanImpl = generator.makeLessThanInstanceFunction(typeCons);
            lessThanEqualsImpl = generator.makeLessThanEqualsInstanceFunction(typeCons);
            greaterThanEqualsImpl = generator.makeGreaterThanEqualsInstanceFunction(typeCons);
            greaterThanImpl = generator.makeGreaterThanInstanceFunction(typeCons);
            
            maxImpl = generator.makeMaxInstanceFunction(typeCons);
            minImpl = generator.makeMinInstanceFunction(typeCons);
        }
        
        SourceModel.InstanceDefn ordInstance = SourceModel.InstanceDefn.make(
            SourceModel.Name.TypeClass.make(CAL_Prelude.TypeClasses.Ord),
            makeInstanceTypeCons(typeCons, instanceTypeVars),
            makeInstanceConstraints(CAL_Prelude.TypeClasses.Ord, instanceTypeVars),
            new SourceModel.InstanceDefn.InstanceMethod[] {
                SourceModel.InstanceDefn.InstanceMethod.make("lessThan", SourceModel.Name.Function.makeUnqualified(lessThanImpl.getName())),
                SourceModel.InstanceDefn.InstanceMethod.make("lessThanEquals", SourceModel.Name.Function.makeUnqualified(lessThanEqualsImpl.getName())),
                SourceModel.InstanceDefn.InstanceMethod.make("greaterThanEquals", SourceModel.Name.Function.makeUnqualified(greaterThanEqualsImpl.getName())),
                SourceModel.InstanceDefn.InstanceMethod.make("greaterThan", SourceModel.Name.Function.makeUnqualified(greaterThanImpl.getName())),
                SourceModel.InstanceDefn.InstanceMethod.make("compare", SourceModel.Name.Function.makeUnqualified(compareImpl.getName())),
                SourceModel.InstanceDefn.InstanceMethod.make("max", SourceModel.Name.Function.makeUnqualified(maxImpl.getName())),
                SourceModel.InstanceDefn.InstanceMethod.make("min", SourceModel.Name.Function.makeUnqualified(minImpl.getName()))
            });

        ////===========================================================================================
        /// Show
        //
        TypeExpr showInstanceType = ClassInstanceChecker.makeInstanceType(typeCons, debugModuleTypeInfo.getTypeClass("Show"));
        TypeExpr showTypeExpr = TypeExpr.makeFunType(showInstanceType, stringType);
        
        final SourceModel.FunctionDefn.Algebraic showImpl = generator.makeShowInstanceFunction(typeCons);
        
        SourceModel.InstanceDefn showInstance = SourceModel.InstanceDefn.make(
            SourceModel.Name.TypeClass.make(CAL_Debug.TypeClasses.Show),
            makeInstanceTypeCons(typeCons, instanceTypeVars),
            makeInstanceConstraints(CAL_Debug.TypeClasses.Show, instanceTypeVars),
            new SourceModel.InstanceDefn.InstanceMethod[] {
                SourceModel.InstanceDefn.InstanceMethod.make("show", SourceModel.Name.Function.makeUnqualified(showImpl.getName()))
            });
        
        ////
        /// Construct the array containing the instance definitions and the instance function implementations 
        //
        SourceModel.TopLevelSourceElement[] result = new SourceModel.TopLevelSourceElement[] {
            // Eq
            eqInstance,
            makeInstanceFunctionTypeDecl(equalsImpl, equalsTypeExpr),
            equalsImpl,
            makeInstanceFunctionTypeDecl(notEqualsImpl, equalsTypeExpr),
            notEqualsImpl,
            // Ord
            ordInstance,
            makeInstanceFunctionTypeDecl(compareImpl, compareTypeExpr),
            compareImpl,
            makeInstanceFunctionTypeDecl(lessThanImpl, lessThanTypeExpr),
            lessThanImpl,
            makeInstanceFunctionTypeDecl(lessThanEqualsImpl, lessThanTypeExpr),
            lessThanEqualsImpl,
            makeInstanceFunctionTypeDecl(greaterThanEqualsImpl, lessThanTypeExpr),
            greaterThanEqualsImpl,
            makeInstanceFunctionTypeDecl(greaterThanImpl, lessThanTypeExpr),
            greaterThanImpl,
            makeInstanceFunctionTypeDecl(maxImpl, maxTypeExpr),
            maxImpl,
            makeInstanceFunctionTypeDecl(minImpl, maxTypeExpr),
            minImpl,
            // Show
            showInstance,
            makeInstanceFunctionTypeDecl(showImpl, showTypeExpr),
            showImpl
        };
        
        return result;
    }
    
    /**
     * Generates the source model for an instance type constructor.
     * @param typeCons the type constructor.
     * @param typeVars the type variables.
     * @return the instance type constructor.
     */
    private static SourceModel.InstanceDefn.InstanceTypeCons makeInstanceTypeCons(TypeConstructor typeCons, SourceModel.Name.TypeVar[] typeVars) {
        return SourceModel.InstanceDefn.InstanceTypeCons.TypeCons.make(
            SourceModel.Name.TypeCons.make(typeCons.getName()), typeVars);
    }
    
    /**
     * Generates an array of the type class constraints for an instance.
     * @param typeClassQualifiedName the qualified name of the type class.
     * @param typeVars the type variables.
     * @return an array representing a set of type class constraints involving the specified type class and type variables.
     */
    private static SourceModel.Constraint.TypeClass[] makeInstanceConstraints(QualifiedName typeClassQualifiedName, SourceModel.Name.TypeVar[] typeVars) {
        
        SourceModel.Name.TypeClass typeClassName = SourceModel.Name.TypeClass.make(typeClassQualifiedName);
        
        SourceModel.Constraint.TypeClass[] constraints = new SourceModel.Constraint.TypeClass[typeVars.length];
        
        for (int i = 0; i < typeVars.length; i++) {
            constraints[i] = SourceModel.Constraint.TypeClass.make(typeClassName, typeVars[i]);
        }
        
        return constraints;
    }
    
    /**
     * Generates an array of type variable names.
     * @param n the number of variables needed.
     * @return an array of type variable names.
     */
    private static SourceModel.Name.TypeVar[] makeTypeVars(int n) {
        SourceModel.Name.TypeVar[] vars = new SourceModel.Name.TypeVar[n];
        
        for (int i = 0; i < n; i++) {
            vars[i] = SourceModel.Name.TypeVar.make(PolymorphicVarContext.indexToVarName(i+1));
        }
        
        return vars;
    }

    /**
     * Generates the source model for the type declaration for an instance function.
     * @param functionImpl the instance function.
     * @param typeExpr the function's type.
     * @return the type declaration for the instance function.
     */
    private static SourceModel.FunctionTypeDeclaration makeInstanceFunctionTypeDecl(SourceModel.FunctionDefn.Algebraic functionImpl, TypeExpr typeExpr) {
        return SourceModel.FunctionTypeDeclaration.make(functionImpl.getName(), typeExpr.toSourceModel());
    }

    /**
     * Generates a compact version of the instance function for Prelude.notEquals.
     * @param typeCons the type constructor of the instance.
     * @return the compact version of the function.
     */
    private SourceModel.FunctionDefn.Algebraic makeCompactNotEqualsImpl(TypeConstructor typeCons) {
        String functionName = makeInstanceFunctionUnqualifiedName("notEquals", typeCons);
        
        SourceModel.Parameter[] params = makeTwoStrictParameters();
        
        return SourceModel.FunctionDefn.Algebraic.make(
            functionName,
            Scope.PRIVATE,
            params,
            SourceModel.Expr.makeGemCall(
                    CAL_Prelude.Functions.not,
                SourceModel.Expr.BinaryOp.Equals.make(
                    SourceModel.Expr.Var.makeUnqualified(params[0].getName()),
                    SourceModel.Expr.Var.makeUnqualified(params[1].getName()))));
    }

    /**
     * Generates a compact version of the instance function for one of the order comparison functions.
     * @param typeCons the type constructor of the instance.
     * @param operation the order comparison operation.
     * @return the compact version of the function.
     */
    private SourceModel.FunctionDefn.Algebraic makeCompactComparisonFunctionImpl(TypeConstructor typeCons, OrderComparisonOperation operation) {
        String functionName = makeInstanceFunctionUnqualifiedName(operation.getClassMethodName(), typeCons);
        
        SourceModel.Parameter[] params = makeTwoStrictParameters();
        
        SourceModel.Expr firstParamExpr = SourceModel.Expr.Var.makeUnqualified(params[0].getName());
        SourceModel.Expr secondParamExpr = SourceModel.Expr.Var.makeUnqualified(params[1].getName());
        
        SourceModel.Expr compareResult = SourceModel.Expr.Application.make(
            new SourceModel.Expr[] {
                SourceModel.Expr.Var.make(CAL_Prelude.Functions.compare), firstParamExpr, secondParamExpr});
        
        SourceModel.Expr definingExpr;
        
        if (operation == OrderComparisonOperation.LESS_THAN) {
            definingExpr = SourceModel.Expr.BinaryOp.Equals.make(compareResult, SourceModel.Expr.DataCons.make(PRELUDE_LT_DATACONS));
            
        } else if (operation == OrderComparisonOperation.LESS_THAN_EQUALS) {
            definingExpr = SourceModel.Expr.BinaryOp.NotEquals.make(compareResult, SourceModel.Expr.DataCons.make(PRELUDE_GT_DATACONS));
            
        } else if (operation == OrderComparisonOperation.GREATER_THAN_EQUALS) {
            definingExpr = SourceModel.Expr.BinaryOp.NotEquals.make(compareResult, SourceModel.Expr.DataCons.make(PRELUDE_LT_DATACONS));
            
        } else if (operation == OrderComparisonOperation.GREATER_THAN) {
            definingExpr = SourceModel.Expr.BinaryOp.Equals.make(compareResult, SourceModel.Expr.DataCons.make(PRELUDE_GT_DATACONS));
            
        } else {
            throw new IllegalArgumentException("The operation must be one of: <, <=, >=, >");
        }
        
        return SourceModel.FunctionDefn.Algebraic.make(
            functionName,
            Scope.PRIVATE,
            params,
            definingExpr);
    }

    /**
     * Generates a compact version of the instance function for either max or min.
     * @param typeCons the type constructor of the instance.
     * @param makeMaxFunction if true, this method returns the instance function for max; if false, then that of min.
     * @return the compact version of the function.
     */
    private SourceModel.FunctionDefn.Algebraic makeCompactExtremumFunctionImpl(TypeConstructor typeCons, boolean makeMaxFunction) {
        OrderComparisonOperation operation = makeMaxFunction ? OrderComparisonOperation.MAX : OrderComparisonOperation.MIN;
        
        String functionName = makeInstanceFunctionUnqualifiedName(operation.getClassMethodName(), typeCons);
        
        SourceModel.Parameter[] params = makeTwoStrictParameters();
        
        SourceModel.Expr firstParamExpr = SourceModel.Expr.Var.makeUnqualified(params[0].getName());
        SourceModel.Expr secondParamExpr = SourceModel.Expr.Var.makeUnqualified(params[1].getName());
        
        return SourceModel.FunctionDefn.Algebraic.make(
            functionName,
            Scope.PRIVATE,
            params,
            operation.makeFundamentalCall(firstParamExpr, secondParamExpr, params));
    }
}
