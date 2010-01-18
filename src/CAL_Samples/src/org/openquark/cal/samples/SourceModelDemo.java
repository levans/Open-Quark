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
 * SourceModelDemo.java
 * Created: Oct 23, 2005
 * By: Joseph Wong
 */

package org.openquark.cal.samples;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelModuleSource;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_Summary;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemCompilationException;


/**
 * Contains a variety of "demo" gems expressed programmatically as SourceModels.
 * <p>
 * The methods {@link #factorialDefn()}, {@link #positiveOutlierDetectorDefn()}, and
 * {@link #demoMapDefn()} demonstrate the construction of source models for CAL function definitions.
 * <p>
 * The {@link #main} method of this class demonstrates the conversion of these source models into CAL source text,
 * as well as the evaluation of these gems with arguments passed into the CAL runtime from Java.
 * <p>
 * See GemModelDemo.java for a demo of the same gems expressed programmatically as GemGraphs.
 * See GemModel.cal for the expression using CAL.
 * 
 * @author Bo Ilic
 * @author Joseph Wong
 * 
 * @see GemModelDemo
 */
public class SourceModelDemo {
    
    /** this is the module in which new functions are defined */
    private static final ModuleName TEST_MODULE_NAME = ModuleName.make("SourceModelDemo");
    
    /**this is the module used to run the functions */
    private static final ModuleName RUN_MODULE_NAME = ModuleName.make("SourceModelDemo.RunModule"); 
    
    private static final String WORKSPACE_FILE_NAME = "gemcutter.default.cws";    
    
    /**
     * This demo consists of generating the CAL source of the demo gems defined using the source model,
     * as well as evaluating these gems with some arguments.
     * 
     * @see #makeGemDemoModule
     * @see #runSourceModelDemo
     */
    public static void main(String[] args) {
        
        System.out.println("Source Model Demo");
        System.out.println("-------------------------------------------------------------------------------------");
        
        MessageLogger messageLogger = new MessageLogger();        
        
        // We need to set up the BasicCALServices instance that is required for the demo.
        // We get an instance through the getInstance() method, and compile the modules in the workspace via
        // the compileWorkspace() method.
        
        BasicCALServices calServices = BasicCALServices.make(
            WORKSPACE_FILE_NAME);
        
        if (!calServices.compileWorkspace(null, messageLogger)) {
            System.err.println(messageLogger.toString());
        }
        
        System.out.println();
        System.out.println("The CAL source of the demo gems defined using the source model:");
        System.out.println("=====================================================================================");
        System.out.println(makeGemDemoModule().toSourceText());
        
        System.out.println();
        System.out.println("Evaluating some gems defined using the source model:");
        System.out.println("=====================================================================================");
        runSourceModelDemo(calServices);
    }

    /**
     * Compiles the source-model based demo module, and evaluates some demo gems.
     * Here, we test out the factorial, the positiveOutlierDetector, and the demoMap gems.
     * 
     * @param services The BasicCALServices instance to be used for running the gems.
     * 
     * @see #makeGemDemoModule
     * @see #runFactorialDemo
     * @see #runPositiveOutlierDetectorDemo
     * @see #runDemoMapDemo
     */
    public static void runSourceModelDemo(BasicCALServices services) {
        
        // First, we add the source-model based module definition as a new module to the workspace
        MessageLogger logger = new MessageLogger();
        
        //Create functions for factorial, outlier detection and map and add them to the program
        services.addNewModule(new SourceModelModuleSource(makeGemDemoModule()), logger);
            
        //run the factorial function
        runFactorialDemo(services);
        
        //run the positive outlier detector
        runPositiveOutlierDetectorDemo(services);

        // Finally, we will demo the demoMap gem, mapping the factorial gem over a list of Integers.
        runDemoMapDemo(services);
    }

    /**
     * Runs some tests with the factorial gem defined by {@link #factorialDefn()}.
     * 
     * @param calServices
     * 
     * @see #makeGemDemoModule
     * @see #factorialTypeDecl
     * @see #factorialDefn
     */
    public static void runFactorialDemo(BasicCALServices calServices) {
             
        /**This is the name of the factional gem defined in {@link #factorialDefn()}*/
        QualifiedName factorial = QualifiedName.make(TEST_MODULE_NAME, "factorial");
        
        try {
            //This calls the factorial function by name and passes a single BigInteger argument - 5.
            //The EntryPointSpec specifies the function to call. 
            //Other EntryPointSpec factory methods allow us to specify how values are marshaled between CAL and Java.
            BigInteger result1 = (BigInteger) calServices.runFunction(EntryPointSpec.make(factorial), new Object[] { BigInteger.valueOf(5) });
            System.out.println("(factorial 5) => " + result1);
    
            //this calls the factorial function by name again, and passes in 50.
            BigInteger result2 = (BigInteger) calServices.runFunction(EntryPointSpec.make(factorial), new Object[] { BigInteger.valueOf(50) });
            System.out.println("(factorial 50) => " + result2);
    
        } catch (CALExecutorException e) {
            e.printStackTrace(System.err);
        } catch (GemCompilationException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Runs some tests with the positiveOutlierDetector gem defined by {@link #positiveOutlierDetectorDefn()}.
     * 
     * @param calServices
     * 
     * @see #makeGemDemoModule
     * @see #positiveOutlierDetectorTypeDecl
     * @see #positiveOutlierDetectorDefn
     */
    public static void runPositiveOutlierDetectorDemo(BasicCALServices calServices) {

        /**This is the name of the positiveOutlierDetector gem defined in {@link #positiveOutlierDetectorDefn()}*/
        QualifiedName positiveOutlierDetector = QualifiedName.make(TEST_MODULE_NAME, "positiveOutlierDetector");
        
        try {
            ////
            /// A Java java.util.List can be passed in as an argument to a CAL function that expects a
            /// CAL list as an argument.
            //
            List<Double> oneToTen = new ArrayList<Double>();
            for (int i = 1; i <= 10; i++) {
                oneToTen.add(new Double(i));
            }
            
            //This runs the positiveOutlierDetector, with the list of integers 1 ..10, and a standard deviation of 2.
            List<?> result1 = (List<?>) calServices.runFunction(EntryPointSpec.make(positiveOutlierDetector), new Object[] { oneToTen, new Double(2.0) });
            System.out.println("(positiveOutlierDetector [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] 2.0) => " + result1);
           
            //This runs the positiveOutlierDetector, with the list of integers 1 ..10, and a standard deviation of 1.
            List<?> result2 = (List<?>) calServices.runFunction(EntryPointSpec.make(positiveOutlierDetector), new Object[] { oneToTen, new Double(1.0) });
            System.out.println("(positiveOutlierDetector [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] 1.0) => " + result2);

            //This runs the positiveOutlierDetector, with the list of integers 1 ..10, and a standard deviation of 0.5.
            List<?> result3 = (List<?>) calServices.runFunction(EntryPointSpec.make(positiveOutlierDetector), new Object[] { oneToTen, new Double(0.5) });
            System.out.println("(positiveOutlierDetector [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] 0.5) => " + result3);
    
        } catch (CALExecutorException e) {
            e.printStackTrace(System.err);
        } catch (GemCompilationException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Runs some tests with the demoMap gem defined by {@link #demoMapDefn()}.
     * 
     * @param calServices
     * 
     * @see #makeGemDemoModule
     * @see #demoMapTypeDecl
     * @see #demoMapDefn
     */
    public static void runDemoMapDemo(BasicCALServices calServices) {
        
        // We will construct a new module with a function using the demoMap gem that
        // takes a list of Integers as the only argument:
        //
        // test list = demoMap factorial list;
        //
        
        //this will be the name of the newly defined function.
        QualifiedName demoMapFactorial = QualifiedName.make(RUN_MODULE_NAME, "test");
        
        try {
            
        calServices.addNewModuleWithFunction(
            RUN_MODULE_NAME,
            SourceModel.FunctionDefn.Algebraic.make(
                demoMapFactorial.getUnqualifiedName(),
                Scope.PUBLIC,
                new SourceModel.Parameter[] { SourceModel.Parameter.make("list", false) },
                
                SourceModel.Expr.Application.make(
                    new SourceModel.Expr[] {
                        SourceModel.Expr.Var.make(TEST_MODULE_NAME, "demoMap"),
                        SourceModel.Expr.Var.make(TEST_MODULE_NAME, "factorial"),
                        SourceModel.Expr.Var.makeUnqualified("list") })));
        
        
       
            List<BigInteger> oneToTen = new ArrayList<BigInteger>();
            for (int i = 1; i <= 10; i++) {
                oneToTen.add(BigInteger.valueOf(i));
            }
        
            //runs the newly defined function with a list of integers 1 .. 10.
            List<?> result = (List<?>) calServices.runFunction(EntryPointSpec.make(demoMapFactorial), new Object[] { oneToTen });
            System.out.println("(demoMap factorial [1 :: Integer, 2, 3, 4, 5, 6, 7, 8, 9, 10]) => " + result);
    
        } catch (CALExecutorException e) {
            e.printStackTrace(System.err);
        }   catch (GemCompilationException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Creates a module containing the various source model demos.
     * @return a new module definition in source model form.
     * 
     * @see #factorialTypeDecl
     * @see #factorialDefn
     * @see #positiveOutlierDetectorTypeDecl
     * @see #positiveOutlierDetectorDefn
     * @see #demoMapTypeDecl
     * @see #demoMapDefn
     */
    public static SourceModel.ModuleDefn makeGemDemoModule() {
                
        SourceModel.ModuleDefn gemModelModule = SourceModel.ModuleDefn.make(
            TEST_MODULE_NAME,
            null,
            new SourceModel.TopLevelSourceElement[] {
                factorialTypeDecl(),
                factorialDefn(),
                
                positiveOutlierDetectorTypeDecl(),
                positiveOutlierDetectorDefn(),
                
                demoMapTypeDecl(),
                demoMapDefn()});
            
        return SourceModelUtilities.ImportAugmenter.augmentWithImports(gemModelModule);                    
    }

    /**
     * Creates the function type declaration for the factorial function as a SourceModel.
     * <p>
     * The resulting source model corresponds to the CAL type declaration:
     * <pre>
     * factorial :: Prelude.Integer -> Prelude.Integer;
     * </pre>
     * 
     * @see #factorialDefn()
     */
    public static SourceModel.FunctionTypeDeclaration factorialTypeDecl () {
        
        SourceModel.TypeExprDefn.TypeCons integerTypeCons =
            SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Integer);
        
        return SourceModel.FunctionTypeDeclaration.make(             
            "factorial", 
            SourceModel.TypeSignature.make(
                SourceModel.TypeExprDefn.Function.make(
                    integerTypeCons,
                    integerTypeCons)));                           
    }
   
    /**
     * Creates the function definition for the factorial function as a SourceModel.
     * <p>
     * The resulting source model corresponds to the CAL function definition:
     * <pre>
     * public factorial n = List.product (Prelude.upFromTo 1 n);
     * </pre>
     * 
     * @see #factorialTypeDecl()
     */
    public static SourceModel.FunctionDefn factorialDefn () {
        
        return SourceModel.FunctionDefn.Algebraic.make(
            "factorial",            
            Scope.PUBLIC,   
            
            // the parameter list
            new SourceModel.Parameter[] {
                SourceModel.Parameter.make("n", false)},     
                
            // the body of the function - a call to the function List.product
            CAL_List.Functions.product(
                CAL_Prelude.Functions.upFromTo(
                    SourceModel.Expr.Literal.Num.make(1),
                    SourceModel.Expr.Var.makeUnqualified("n"))));
    }
    
    /**
     * Creates the function type declaration for the positiveOutlierDetector function as a SourceModel.
     * <p>
     * The resulting source model corresponds to the CAL type declaration:
     * <pre>
     * positiveOutlierDetector :: [Double] -> Double -> [Double];
     * </pre>
     * 
     * @see #positiveOutlierDetectorDefn()
     */
    public static SourceModel.FunctionTypeDeclaration positiveOutlierDetectorTypeDecl() {
        
        SourceModel.TypeExprDefn.TypeCons doubleTypeCons =
            SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Double);
        
        SourceModel.TypeExprDefn.List listOfDoublesTypeCons =
            SourceModel.TypeExprDefn.List.make(doubleTypeCons);
        
        return SourceModel.FunctionTypeDeclaration.make(
            "positiveOutlierDetector",
            SourceModel.TypeSignature.make(
                SourceModel.TypeExprDefn.Function.make(
                    listOfDoublesTypeCons,
                    SourceModel.TypeExprDefn.Function.make(
                        doubleTypeCons,
                        listOfDoublesTypeCons))));
    }
    
    /**
     * Creates the function definition for the positiveOutlierDetector function as a SourceModel.
     * <p>
     * The resulting source model corresponds to the CAL function definition:
     * <pre>
     * public positiveOutlierDetector sourceData nStdDev =
     *     let  
     *         avg = Summary.average sourceData;
     *         stdDev = Summary.populationStandardDeviation sourceData;
     *         
     *         isPositiveOutlier :: Double -> Boolean;
     *         isPositiveOutlier !value = (value - avg) >= nStdDev * stdDev;
     *     in
     *         filter isPositiveOutlier sourceData;
     * </pre>
     * 
     * @see #positiveOutlierDetectorTypeDecl()
     */
    public static SourceModel.FunctionDefn positiveOutlierDetectorDefn() {
        
        return SourceModel.FunctionDefn.Algebraic.make(
            "positiveOutlierDetector",
            Scope.PUBLIC,
            
            // the parameter list
            new SourceModel.Parameter[] {
                SourceModel.Parameter.make("sourceData", false),
                SourceModel.Parameter.make("nStdDev", false)},
            
            // the body of the function - a let expression
            SourceModel.Expr.Let.make(
                new SourceModel.LocalDefn.Function[] {
                    // the local definition for avg
                    SourceModel.LocalDefn.Function.Definition.make(
                        "avg",
                        SourceModel.LocalDefn.Function.Definition.NO_PARAMETERS,
                        CAL_Summary.Functions.average(
                            SourceModel.Expr.Var.makeUnqualified("sourceData"))),
                            
                    // the local definition for stdDev
                    SourceModel.LocalDefn.Function.Definition.make(
                        "stdDev",
                        SourceModel.LocalDefn.Function.Definition.NO_PARAMETERS,
                        CAL_Summary.Functions.populationStandardDeviation(
                            SourceModel.Expr.Var.makeUnqualified("sourceData"))),
                            
                    // the type declaration for the local definition for isPositiveOutlier
                    SourceModel.LocalDefn.Function.TypeDeclaration.make(
                        "isPositiveOutlier",
                        SourceModel.TypeSignature.make(
                            SourceModel.TypeExprDefn.Function.make(
                                SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Double),
                                SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Boolean)))),
                                
                    // the local definition for isPositiveOutlier
                    SourceModel.LocalDefn.Function.Definition.make(
                        "isPositiveOutlier",
                        new SourceModel.Parameter[] {
                            SourceModel.Parameter.make("value", true)},
                        
                        SourceModel.Expr.BinaryOp.GreaterThanEquals.make(
                            SourceModel.Expr.BinaryOp.Subtract.make(
                                SourceModel.Expr.Var.makeUnqualified("value"),
                                SourceModel.Expr.Var.makeUnqualified("avg")),
                            SourceModel.Expr.BinaryOp.Multiply.make(
                                SourceModel.Expr.Var.makeUnqualified("nStdDev"),
                                SourceModel.Expr.Var.makeUnqualified("stdDev"))))},
                
                // the body of the let expression - a call to the function List.filter
                CAL_List.Functions.filter(
                    SourceModel.Expr.Var.makeUnqualified("isPositiveOutlier"),
                    SourceModel.Expr.Var.makeUnqualified("sourceData"))));
    }
    
    /**
     * Creates the function type declaration for the demoMap function as a SourceModel.
     * <p>
     * The resulting source model corresponds to the CAL type declaration:
     * <pre>
     * demoMap :: (a -> b) -> [a] -> [b];
     * </pre>
     * 
     * @see #demoMapDefn()
     */
    public static SourceModel.FunctionTypeDeclaration demoMapTypeDecl() {
        
        SourceModel.TypeExprDefn.TypeVar typeVarA =
            SourceModel.TypeExprDefn.TypeVar.make(SourceModel.Name.TypeVar.make("a"));
        
        SourceModel.TypeExprDefn.TypeVar typeVarB =
            SourceModel.TypeExprDefn.TypeVar.make(SourceModel.Name.TypeVar.make("b"));
        
        return SourceModel.FunctionTypeDeclaration.make(
            "demoMap",
            SourceModel.TypeSignature.make(
                SourceModel.TypeExprDefn.Function.make(
                    SourceModel.TypeExprDefn.Function.make(
                        typeVarA,
                        typeVarB),
                    SourceModel.TypeExprDefn.Function.make(
                        SourceModel.TypeExprDefn.List.make(typeVarA),
                        SourceModel.TypeExprDefn.List.make(typeVarB)))));
    }
    
    /**
     * Creates the function definition for the demoMap function as a SourceModel.
     * <p>
     * The resulting source model corresponds to the CAL function definition:
     * <pre>
     * public demoMap mapFunction !list =
     *     case list of
     *     []     -> [];
     *     listHead : listTail -> mapFunction listHead : demoMap mapFunction listTail;
     *     ;
     * </pre>
     * 
     * @see #demoMapTypeDecl()
     */
    public static SourceModel.FunctionDefn demoMapDefn() {
        
        return SourceModel.FunctionDefn.Algebraic.make(
            "demoMap",
            Scope.PUBLIC,
            
            // the parameter list
            new SourceModel.Parameter[] {
                SourceModel.Parameter.make("mapFunction", false),
                SourceModel.Parameter.make("list", true)},
                
            // the body of the function - a case expression
            SourceModel.Expr.Case.make(
                SourceModel.Expr.Var.makeUnqualified("list"),
                
                new SourceModel.Expr.Case.Alt[] {
                    // the first alternative: [] -> [];
                    SourceModel.Expr.Case.Alt.UnpackListNil.make(
                        SourceModel.Expr.List.EMPTY_LIST),
                    
                    // the second alternative: listHead : listTail -> ...
                    SourceModel.Expr.Case.Alt.UnpackListCons.make(
                        SourceModel.Pattern.Var.make("listHead"),
                        SourceModel.Pattern.Var.make("listTail"),
                        
                        // the right-hand-side of the case alternative
                        SourceModel.Expr.BinaryOp.Cons.make(
                            SourceModel.Expr.Application.make(
                                new SourceModel.Expr[] {
                                    SourceModel.Expr.Var.makeUnqualified("mapFunction"),
                                    SourceModel.Expr.Var.makeUnqualified("listHead")}),
                            SourceModel.Expr.Application.make(
                                new SourceModel.Expr[] {
                                    SourceModel.Expr.Var.makeUnqualified("demoMap"),
                                    SourceModel.Expr.Var.makeUnqualified("mapFunction"),
                                    SourceModel.Expr.Var.makeUnqualified("listTail")})))
                }));
    }
}
