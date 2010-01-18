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
 * GemModelDemo.java
 * Created: Jul 29, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.samples;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.compiler.CALSourceGenerator;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Utilities.CAL_Summary;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemCompilationException;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.GemGraph;
import org.openquark.gems.client.ReflectorGem;
import org.openquark.gems.client.ValueGem;
import org.openquark.gems.client.services.GemFactory;


/**
 * Contains a variety of "demo" gems expressed programmatically as {@link GemGraph}s.
 * <p>
 * The methods {@link #factorialGemGraph}, {@link #positiveOutlierDetectorGemGraph}, and
 * {@link #demoMapGemGraph} demonstrate the construction of gem graphs.
 * <p>
 * The {@link #main} method of this class demonstrates the conversion of these gem graphs into CAL source text,
 * as well as the evaluation of these gems with arguments passed into the CAL runtime from Java.
 * <p>
 * The steps for creating a GemGraph are:
 * <ol>
 * <li> Create an empty GemGraph, and set the name of its target collector
 * <li> Create a new Gem or obtain an existing Gem
 * <li> Add the Gem to the GemGraph
 * <li> Repeat steps 2-3 to add other Gems
 * <li> Connect the Gems together using the connectGems() method on the GemGraph
 * <li> Repeat steps 2-5 as necessary to add all the Gems and connections for the GemGraph
 * <li> Typecheck the GemGraph to ensure that it is consistent
 * </ol>
 * <p>
 * See SourceModelDemo.java for a demo of the same gems expressed programmatically as SourceModels.
 * See GemModelDemo.cal for the expression using CAL.
 * 
 * @author Bo Ilic
 * @author Joseph Wong
 * 
 * @see SourceModelDemo
 */
public class GemModelDemo {
    
    private static final ModuleName GEM_GRAPH_TYPE_CHECKING_MODULE = ModuleName.make("GemCutterSaveModule");
    private static final ModuleName RUN_MODULE = ModuleName.make("GemModelDemo.RunModule");
    private static final String WORKSPACE_FILE_NAME = "gemcutter.default.cws";   
    
    /**
     * This demo consists of generating the CAL source of the demo gems defined using gem graphs,
     * as well as evaluating these gems with some arguments.
     * 
     * @see #dumpAllGemGraphSourceDefinitions
     * @see #runGemGraphDemo
     */
    public static void main(String[] args) {
        
        System.out.println("Gem Model Demo");
        System.out.println("-------------------------------------------------------------------------------------");
        
        MessageLogger messageLogger = new MessageLogger();        
        
        // We need to set up the calServices instance that is required for the demo.
        BasicCALServices calServices =
            BasicCALServices.makeCompiled(WORKSPACE_FILE_NAME, messageLogger);
        
        if (calServices == null) {
            System.err.println(messageLogger.toString());
            System.exit(1);
        }
        
        System.out.println();
        System.out.println("The CAL source of the demo gems defined using gem graphs:");
        System.out.println("=====================================================================================");
        dumpAllGemGraphSourceDefinitions(calServices);
        
        System.out.println();
        System.out.println("Evaluating some gems defined using gem graphs:");
        System.out.println("=====================================================================================");
        runGemGraphDemo(calServices);
    }

    /**
     * Evaluates some demo gems. Here, we test out the factorial and the positiveOutlierDetector gems.
     * 
     * @param calServices The calServices instance to be used for constructing and running the GemGraphs.
     * 
     * @see #runFactorialDemo
     * @see #runPositiveOutlierDetectorDemo
     */
    public static void runGemGraphDemo(BasicCALServices calServices) {
        
        // First, we will run the factorial gem.
        runFactorialDemo(calServices);
    
        // Next, we will run the positiveOutlierDetector gem.
        runPositiveOutlierDetectorDemo(calServices);
    }

    /**
     * Runs some tests with the factorial gem defined by {@link #factorialGemGraph}.
     * 
     * @param calServices The calServices instance to be used for constructing and running the GemGraphs.
     * 
     * @see #factorialGemGraph
     */
    public static void runFactorialDemo(BasicCALServices calServices) {
        
        ////
        /// We can use the runCode method on BasicCALServices to evaluate gems defined by gem graphs.
        //

        try {
            GemFactory gemFactory = new GemFactory(calServices);
            SourceModel.FunctionDefn.Algebraic gemSource = factorialGemGraph(gemFactory, calServices).getCALSource();
    
            EntryPointSpec entryPointSpec = calServices.addNewModuleWithFunction(RUN_MODULE, gemSource);
            
            try {
                BigInteger result;
    
                result = (BigInteger)calServices.runFunction(entryPointSpec, new Object[] { BigInteger.valueOf(7) });
                System.out.println("(factorial 7) => " + result);
    
                result = (BigInteger)calServices.runFunction(entryPointSpec, new Object[] { BigInteger.valueOf(37) });
                System.out.println("(factorial 37) => " + result);
    
            } catch (CALExecutorException e) {
                e.printStackTrace(System.err);
            }
            
        } catch (TypeException e) {
            e.printStackTrace(System.err);
        } catch (GemCompilationException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Runs some tests with the positiveOutlierDetector gem defined by {@link #positiveOutlierDetectorGemGraph}.
     * 
     * @param calServices The GemGenerator instance to be used for constructing and running the GemGraphs.
     * 
     * @see #positiveOutlierDetectorGemGraph
     */
    public static void runPositiveOutlierDetectorDemo(BasicCALServices calServices) {
        
        ////
        /// We can use the runCode method on BasicCALServices and GemGenerator to evaluate gems defined by gem graphs.
        //

        try {
            GemFactory gemFactory = new GemFactory(calServices);

            SourceModel.FunctionDefn.Algebraic gemSource = positiveOutlierDetectorGemGraph(gemFactory, calServices).getCALSource();
            EntryPointSpec entryPointSpec = calServices.addNewModuleWithFunction(RUN_MODULE, gemSource);

            List<?> result;

            ////
            /// A Java java.util.List can be passed in as an argument to a CAL function that expects a
            /// CAL list as an argument.
            //
            List<Double> oneToFive = new ArrayList<Double>();
            for (int i = 1; i <= 5; i++) {
                oneToFive.add(new Double(i));
            }

            result = (List<?>)calServices.runFunction(entryPointSpec, new Object[] { oneToFive, new Double(2.0) });
            System.out.println("(positiveOutlierDetector [1, 2, 3, 4, 5] 2.0) => " + result);

            result = (List<?>)calServices.runFunction(entryPointSpec, new Object[] { oneToFive, new Double(1.0) });
            System.out.println("(positiveOutlierDetector [1, 2, 3, 4, 5] 1.0) => " + result);

            result = (List<?>)calServices.runFunction(entryPointSpec, new Object[] { oneToFive, new Double(0.5) });
            System.out.println("(positiveOutlierDetector [1, 2, 3, 4, 5] 0.5) => " + result);

        } catch (CALExecutorException e) {
            e.printStackTrace(System.err);
        } catch (GemCompilationException e) {
            e.printStackTrace(System.err);
        } catch (TypeException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Prints out the CAL source text generated by the various demo GemGraphs.
     * 
     * @param calServices 
     * 
     * @see #factorialGemGraph
     * @see #positiveOutlierDetectorGemGraph
     * @see #demoMapGemGraph
     */
    public static void dumpAllGemGraphSourceDefinitions(BasicCALServices calServices) {
        GemFactory gemFactory = new GemFactory(calServices);

        try {
            System.out.println(getGemGraphSourceDefinition(factorialGemGraph(gemFactory, calServices)));
            System.out.println(getGemGraphSourceDefinition(positiveOutlierDetectorGemGraph(gemFactory, calServices)));
            System.out.println(getGemGraphSourceDefinition(demoMapGemGraph(gemFactory, calServices)));
        } catch (TypeException e) {
            e.printStackTrace(System.err);
        }
    }

    /** Generates CAL source text from a GemGraph. */ 
    private static String getGemGraphSourceDefinition(GemGraph gemGraph) {
        return CALSourceGenerator.getFunctionText(
            gemGraph.getTargetCollector().getUnqualifiedName(),
            gemGraph.getTargetCollector(),
            Scope.PUBLIC);
    }

    /**
     * Creates a GemGraph that defines the factorial function.
     * <p>
     * Corresponding CAL source for the gem graph:
     * <pre>
     * public factorial end = List.product (Prelude.upFromTo (1 :: Prelude.Integer) end);
     * </pre>
     * @param calServices 
     */
    public static GemGraph factorialGemGraph(GemFactory gemFactory, BasicCALServices calServices) throws TypeException {
        
        // This demonstrates the creation of a simple gem graph. The steps required to build a gem graph are:
        // 1) Create an empty GemGraph, and set the name of its target collector
        // 2) Create a new Gem or obtain an existing Gem
        // 3) Add the Gem to the GemGraph
        // 4) Repeat steps 2-3 to add other Gems
        // 5) Connect the Gems together using the connectGems() method on the GemGraph
        // 6) Repeat steps 2-5 as necessary to add all the Gems and connections for the GemGraph
        // 7) Typecheck the GemGraph to ensure that it is consistent
        
        GemGraph gemGraph = new GemGraph();
        gemGraph.getTargetCollector().setName("factorial");
        
        Gem productGem = gemFactory.makeFunctionalAgentGem(CAL_List.Functions.product);
        gemGraph.addGem(productGem);
        
        Gem upFromToGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.upFromTo);
        gemGraph.addGem(upFromToGem);

        // Create a Value gem to represent a BigInteger (Prelude.Integer) value for the number 1.
        Gem oneGem = new ValueGem(new LiteralValueNode(BigInteger.valueOf(1), calServices.getPreludeTypeConstants().getIntegerType()));
            
        gemGraph.addGem(oneGem);
        
        gemGraph.connectGems(oneGem.getOutputPart(), upFromToGem.getInputPart(0));
        
        gemGraph.connectGems(upFromToGem.getOutputPart(), productGem.getInputPart(0));
        
        gemGraph.connectGems(productGem.getOutputPart(), gemGraph.getTargetCollector().getInputPart(0));
        
        gemGraph.typeGemGraph(calServices.getTypeCheckInfo(GEM_GRAPH_TYPE_CHECKING_MODULE));
        
        return gemGraph;
    }
    
    /**
     * Creates a GemGraph that defines the positiveOutlierDetector function.
     * <p>
     * Corresponding CAL source for the gem graph:
     * <pre>
     * public positiveOutlierDetector sourceData x_2 = 
     *     let
     *         isPositiveOutlier x_1 = x_1 - avg >= x_2 * stdDev;
     *         stdDev = Summary.populationStandardDeviation sourceData;
     *         avg = Summary.average sourceData;
     *     in
     *         List.filter isPositiveOutlier sourceData
     *     ;
     * </pre>
     * @param gemFactory used for creating value and function gems
     * @param calServices 
     */
    public static GemGraph positiveOutlierDetectorGemGraph(GemFactory gemFactory, BasicCALServices calServices) throws TypeException {

        GemGraph gemGraph = new GemGraph();
        gemGraph.getTargetCollector().setName("positiveOutlierDetector");
        
        // A collector targeting the target collector. It will be an argument of the positiveOutlierDetector gem.
        CollectorGem sourceDataCollector = new CollectorGem();
        sourceDataCollector.setName("sourceData");
        gemGraph.addGem(sourceDataCollector);
        
        // Local collector: avg
        // Corresponding source: avg = Summary.average sourceData;
        CollectorGem avgCollector = new CollectorGem();
        avgCollector.setName("avg");
        gemGraph.addGem(avgCollector);
        
        // a ReflectorGem provides as output the value that is collected by the corresponding CollectorGem
        ReflectorGem sourceDataReflector1 = new ReflectorGem(sourceDataCollector);
        gemGraph.addGem(sourceDataReflector1);
        
        Gem averageGem = gemFactory.makeFunctionalAgentGem(CAL_Summary.Functions.average);
        gemGraph.addGem(averageGem);
        
        gemGraph.connectGems(sourceDataReflector1.getOutputPart(), averageGem.getInputPart(0));
        
        gemGraph.connectGems(averageGem.getOutputPart(), avgCollector.getInputPart(0));
        
        // Local collector: stdDev
        // Corresponding source: stdDev = Summary.populationStandardDeviation sourceData;
        CollectorGem stdDevCollector = new CollectorGem();
        stdDevCollector.setName("stdDev");
        gemGraph.addGem(stdDevCollector);
        
        ReflectorGem sourceDataReflector2 = new ReflectorGem(sourceDataCollector);
        gemGraph.addGem(sourceDataReflector2);
        
        Gem populationStdDevGem = gemFactory.makeFunctionalAgentGem(CAL_Summary.Functions.populationStandardDeviation);
        gemGraph.addGem(populationStdDevGem);
        
        gemGraph.connectGems(sourceDataReflector2.getOutputPart(), populationStdDevGem.getInputPart(0));
        
        gemGraph.connectGems(populationStdDevGem.getOutputPart(), stdDevCollector.getInputPart(0));
        
        // Local collector: isPositiveOutlier
        // Corresponding source: isPositiveOutlier x_1 = x_1 - avg >= x_2 * stdDev;
        CollectorGem isPositiveOutlierCollector = new CollectorGem();
        isPositiveOutlierCollector.setName("isPositiveOutlier");
        gemGraph.addGem(isPositiveOutlierCollector);
        
        Gem subtractGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.subtract);
        gemGraph.addGem(subtractGem);
        
        ReflectorGem avgReflector = new ReflectorGem(avgCollector);
        gemGraph.addGem(avgReflector);
        
        // Retarget the first input of subtract to the isPositiveOutlier collector
        //
        // This means that the first input of subtract is no longer an argument for the overall Gem defined by the
        // GemGraph's target collector, but rather a local argument for the isPositiveOutlier collector.
        gemGraph.retargetInputArgument(subtractGem.getInputPart(0), isPositiveOutlierCollector, -1);
        gemGraph.connectGems(avgReflector.getOutputPart(), subtractGem.getInputPart(1));
        
        Gem multiplyGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.multiply);
        gemGraph.addGem(multiplyGem);
        
        ReflectorGem stdDevReflector = new ReflectorGem(stdDevCollector);
        gemGraph.addGem(stdDevReflector);
        
        // Leave the first input of multiply targeting the target collector (it will be an argument of the positiveOutlierDetector gem),
        // but hook up the second input of multiply to the stdDev reflector
        gemGraph.connectGems(stdDevReflector.getOutputPart(), multiplyGem.getInputPart(1));
        
        Gem greaterThanEqualsGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.greaterThanEquals);
        gemGraph.addGem(greaterThanEqualsGem);
        
        gemGraph.connectGems(subtractGem.getOutputPart(), greaterThanEqualsGem.getInputPart(0));
        gemGraph.connectGems(multiplyGem.getOutputPart(), greaterThanEqualsGem.getInputPart(1));
        
        gemGraph.connectGems(greaterThanEqualsGem.getOutputPart(), isPositiveOutlierCollector.getInputPart(0));
        
        // Update the reflected inputs of the collector because one of the inputs in the gem tree was retargeted
        isPositiveOutlierCollector.updateReflectedInputs();
        
        // Construct the gem tree connected to the target collector
        ReflectorGem isPositiveOutlierReflector = new ReflectorGem(isPositiveOutlierCollector);
        gemGraph.addGem(isPositiveOutlierReflector);

        isPositiveOutlierReflector.getInputPart(0).setBurnt(true);
        
        ReflectorGem sourceDataReflector3 = new ReflectorGem(sourceDataCollector);
        gemGraph.addGem(sourceDataReflector3);
        
        Gem filterGem = gemFactory.makeFunctionalAgentGem(CAL_List.Functions.filter);
        gemGraph.addGem(filterGem);
        
        gemGraph.connectGems(isPositiveOutlierReflector.getOutputPart(), filterGem.getInputPart(0));
        gemGraph.connectGems(sourceDataReflector3.getOutputPart(), filterGem.getInputPart(1));
        
        gemGraph.connectGems(filterGem.getOutputPart(), gemGraph.getTargetCollector().getInputPart(0));
        
        gemGraph.typeGemGraph(calServices.getTypeCheckInfo(GEM_GRAPH_TYPE_CHECKING_MODULE));
        
        return gemGraph;
    }
    
    /**
     * Creates a GemGraph that defines the demoMap function.
     * <p>
     * Corresponding CAL source for the gem graph:
     * <pre>
     * public demoMap mapFunction list = Prelude.iff (Prelude.isEmpty list) [] (Prelude.Cons (mapFunction (List.head list)) (demoMap mapFunction (List.tail list)));
     * </pre>
     * @param gemFactory 
     * @param calServices 
     */
    public static GemGraph demoMapGemGraph(GemFactory gemFactory, BasicCALServices calServices) throws TypeException {

        GemGraph gemGraph = new GemGraph();
        gemGraph.getTargetCollector().setName("demoMap");
        
        // Two collectors forming the two arguments of the demoMap gem.
        CollectorGem mapFunctionCollector = new CollectorGem();
        mapFunctionCollector.setName("mapFunction");
        gemGraph.addGem(mapFunctionCollector);
        
        CollectorGem listCollector = new CollectorGem();
        listCollector.setName("list");
        gemGraph.addGem(listCollector);
        
        // Construct the test for checking whether the 'list' value is the empty list.
        Gem isEmptyGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.isEmpty);
        gemGraph.addGem(isEmptyGem);
        
        ReflectorGem listReflector1 = new ReflectorGem(listCollector);
        gemGraph.addGem(listReflector1);

        // Construct the gem tree for the case when 'list' is not empty.
        
        // The head of the returned Cons is: mapFunction (List.head list)
        // which uses the apply gem to apply the mapFunction to its argument.
        gemGraph.connectGems(listReflector1.getOutputPart(), isEmptyGem.getInputPart(0));

        Gem headGem = gemFactory.makeFunctionalAgentGem(CAL_List.Functions.head);
        gemGraph.addGem(headGem);
        
        ReflectorGem listReflector2 = new ReflectorGem(listCollector);
        gemGraph.addGem(listReflector2);
        
        gemGraph.connectGems(listReflector2.getOutputPart(), headGem.getInputPart(0));
        
        Gem applyGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.apply);
        gemGraph.addGem(applyGem);
        
        ReflectorGem mapFunctionReflector1 = new ReflectorGem(mapFunctionCollector);
        gemGraph.addGem(mapFunctionReflector1);
        
        gemGraph.connectGems(mapFunctionReflector1.getOutputPart(), applyGem.getInputPart(0));
        gemGraph.connectGems(headGem.getOutputPart(), applyGem.getInputPart(1));
        
        // The tail of the returned Cons is: demoMap mapFunction (List.tail list) 
        Gem tailGem = gemFactory.makeFunctionalAgentGem(CAL_List.Functions.tail);
        gemGraph.addGem(tailGem);

        ReflectorGem listReflector3 = new ReflectorGem(listCollector);
        gemGraph.addGem(listReflector3);
        
        gemGraph.connectGems(listReflector3.getOutputPart(), tailGem.getInputPart(0));
        
        ReflectorGem demoMapReflector = new ReflectorGem(gemGraph.getTargetCollector());
        gemGraph.addGem(demoMapReflector);
        
        ReflectorGem mapFunctionReflector2 = new ReflectorGem(mapFunctionCollector);
        gemGraph.addGem(mapFunctionReflector2);
        
        Gem consGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.DataConstructors.Cons);
        gemGraph.addGem(consGem);
        
        gemGraph.connectGems(applyGem.getOutputPart(), consGem.getInputPart(0));
        gemGraph.connectGems(demoMapReflector.getOutputPart(), consGem.getInputPart(1));
        
        // Construct the conditional branch (using the iff gem). The false case returns the value
        // generated by the gem tree defined above. In the true case, Nil (the empty list) is returned.
        Gem iffGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.Functions.iff);
        gemGraph.addGem(iffGem);
        
        Gem nilGem = gemFactory.makeFunctionalAgentGem(CAL_Prelude.DataConstructors.Nil);
        gemGraph.addGem(nilGem);
        
        gemGraph.connectGems(isEmptyGem.getOutputPart(), iffGem.getInputPart(0));
        gemGraph.connectGems(nilGem.getOutputPart(), iffGem.getInputPart(1));
        gemGraph.connectGems(consGem.getOutputPart(), iffGem.getInputPart(2));
        
        // Connect the gems to the target collector
        gemGraph.connectGems(iffGem.getOutputPart(), gemGraph.getTargetCollector().getInputPart(0));
        
        gemGraph.connectGems(mapFunctionReflector2.getOutputPart(), demoMapReflector.getInputPart(0));
        
        gemGraph.connectGems(tailGem.getOutputPart(), demoMapReflector.getInputPart(1));
        
        gemGraph.typeGemGraph(calServices.getTypeCheckInfo(GEM_GRAPH_TYPE_CHECKING_MODULE));
        
        return gemGraph;
    }
}
