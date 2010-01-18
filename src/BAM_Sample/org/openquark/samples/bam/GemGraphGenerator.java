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
 * GemGraphGenerator.java
 * Created: 15-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.gems.client.CodeGem;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.GemGraph;
import org.openquark.gems.client.ReflectorGem;
import org.openquark.samples.bam.model.ActionDescription;
import org.openquark.samples.bam.model.BoundGemDescription;
import org.openquark.samples.bam.model.InputBinding;
import org.openquark.samples.bam.model.MetricDescription;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.model.TriggerDescription;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.DurationLogger;




/**
 * The GemGraphGenerator is used to assemble and compile the gem graph based
 * on a job description. 
 */
class GemGraphGenerator {

    private final BasicCALServices calServices;
    private final MonitorJobDescription jobDescription;
    
    //the names of gems used to 'zip' 0, 1, ... lists together
    private final QualifiedName[] zippingGems = {
        CAL_List.Functions.repeat, //constant value - repeat
        CAL_List.Functions.map,
        CAL_List.Functions.zipWith,
        CAL_List.Functions.zipWith3,
        CAL_List.Functions.zipWith4,
        CAL_List.Functions.zipWith5,
        CAL_List.Functions.zipWith6,
        CAL_List.Functions.zipWith7
        }; 
    
    private GemGraph gemGraph = null;
    private CodeAnalyser codeAnalyser = null;
        
    private CollectorGem messageGem = null;
    
    /**
     * this class is used to store and retrieve the collector gems for 
     * binding trigger and actions to metrics and message properties.
     */
    private class GeneratorBindingContext implements BindingContext {
        private final Map<Object, CollectorGem> collectorMap = new HashMap<Object, CollectorGem>(); 
        /**
         * @see org.openquark.samples.bam.BindingContext#getCollector(java.lang.Object)
         */
        public CollectorGem getCollector (Object id) {
            if (collectorMap != null) {
                CollectorGem gem = collectorMap.get (id);
                
                if (gem != null) {
                    return gem;
                } else {
                    throw new IllegalArgumentException ("There is no collector for " + id);
                }
            }
            
            throw new IllegalStateException ("The collector map has not been built");
        }
        
        /**
         * Add a new collector to the binding context
         * @param id
         * @param gem
         */
        public void addCollector(Object id, CollectorGem gem) {
            collectorMap.put(id, gem);
        }
    }
    
    private final GeneratorBindingContext bindingContext = new GeneratorBindingContext ();
    
    /**
     * constructs the GemGraphGenerator with the job description and the calServices
     */
    public GemGraphGenerator (BasicCALServices calServices, MonitorJobDescription jobDescription) {
        this.calServices = calServices;
        this.jobDescription = jobDescription;
        
        codeAnalyser =new CodeAnalyser(
            calServices.getTypeChecker(),
            calServices.getCALWorkspace().getMetaModule(MonitorApp.TARGET_MODULE).getTypeInfo(),
            true,
            false);
    }
    
    
    /**
     * get the source representing the gem
     * @return the source function definition that represents the gem
     */
    public SourceModel.FunctionDefn getCalSource() {
        GemGraph gem = getGemGraph ();
        
        return gem.getCALSource();
    }
    
    /**
     * Gets the gem representing the JobDescription 
     * builds the gem graph if it has not already been built.
     * 
     * @return Returns the completed gem graph
     */
    private GemGraph getGemGraph () {
        if (gemGraph == null) {
            if (!buildGemGraph()) {
                return null;
            }
        }
        
        return gemGraph;
    }

    /**
     * Builds the gem graph
     * @return true if the graph is created successfully
     */
    private boolean buildGemGraph () {
        try {
            gemGraph = new GemGraph ();
            DurationLogger logger = new DurationLogger (); 
            
            addCollectorForMessage();
            addCollectorsForMessageProperties ();
            addCollectorsForMetrics();
            
            logger.report("Time to add collectors: ", true);
            
            Gem triggerResultGem = addTriggerGems ();           
            logger.report("Time to add triggers: ", true);
            
            Gem actionResultGem = addActionGems ();            
            logger.report("Time to add actions: ", true);
            
            finishGemGraph (triggerResultGem, actionResultGem);
            
            logger.report("Time to finish graph: ", true);
            
            assert graphIsValid() : gemGraph.toString();
                       
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            
            return false;
        }
    }
    
    /**
     * This adds a collector for the message input
     */
    private void addCollectorForMessage () {
        messageGem = new CollectorGem ();
        messageGem.setName ("messages");
        
        // We must set the type of the message collector gem to ensure that the type of the 
        // gem graph will not be ambiguous. To see why the type might be ambiguous, 
        // consider the average metric (BAM.average),
        // which has a type signature of Num a => [a] -> [Double]. Using this metric
        // is not sufficient to unambiguously constrain the type of a message property.
        // This is true of most of the metrics. Once the type of the message is defined, it
        // will ensure all the other types in the gem graph are unambiguous.
        ArrayList<TypeExpr> types=new ArrayList<TypeExpr>();
        for (final MessagePropertyDescription propertyDescription : jobDescription.getMessagePropertyDescriptions()) {
            types.add(propertyDescription.getCalType(calServices));
        }
        
        messageGem.setDeclaredType(
            TypeExpr.makeListType(
                TypeExpr.makeTupleType(types),
                calServices.getCALWorkspace().getMetaModule(MonitorApp.TARGET_MODULE).getTypeInfo())
        );

        gemGraph.addGem(messageGem);
                
        assert graphIsValid() : gemGraph.toString();
    }
    
    
    /**
     * This adds collector gems for each of the message properties
     */
    private void addCollectorsForMessageProperties () {
        Collection<MessagePropertyDescription> propertyInfos = jobDescription.getMessagePropertyDescriptions();
        
        int i=0;
        for (final MessagePropertyDescription propertyInfo : propertyInfos) {
            //create the message reflector
            ReflectorGem messageReflector = new ReflectorGem (messageGem);
            gemGraph.addGem(messageReflector);
            
            // Create a code gem to extract the ith property from the message list input
            // e.g. the code gem for the 5th message property would be:
            // List.map (\msg -> msg.#5) msg
            // which takes the input list of message tuples as input, and outputs a new list 
            // containing the 5th element of every message.
            Gem propertyExtractorGem = new CodeGem(codeAnalyser, CAL_List.Functions.map.getQualifiedName() + " (\\msg -> msg.#" + ++i +") msg", Collections.<String>emptySet());
            gemGraph.addGem(propertyExtractorGem);
                     
            // create the collector gem for this message property
            CollectorGem propertyGem = new CollectorGem ();
            propertyGem.setName (makeCollectorName (propertyInfo.name));   
            gemGraph.addGem(propertyGem);
            
            gemGraph.connectGems(messageReflector.getOutputPart(), propertyExtractorGem.getInputPart(0));    
            gemGraph.connectGems(propertyExtractorGem.getOutputPart(), propertyGem.getInputPart(0));    

            bindingContext.addCollector(propertyInfo, propertyGem);
            
            assert graphIsValid() : gemGraph.toString();
        }
    }
    
    /**
     * This adds collector gems for all of the required metrics
     */
    private void addCollectorsForMetrics () {
        Collection<MetricDescription> metrics = jobDescription.getMetricDescriptions(); 
        for (final MetricDescription md : metrics) {
            //create the message reflector
            ReflectorGem messagePropertyReflector = new ReflectorGem (bindingContext.getCollector(md.getPropertyDescription()));
            gemGraph.addGem(messagePropertyReflector);

            //create the gem to compute this metric
            FunctionalAgentGem computeMetricGem = new FunctionalAgentGem(calServices.getGemEntity(md.getGemName()));
            gemGraph.addGem(computeMetricGem);
                
            //create the collector gem for this metric
            CollectorGem metricGem = new CollectorGem ();
            metricGem.setName(makeCollectorName(md.getInternalName()));
            gemGraph.addGem(metricGem);
            
            gemGraph.connectGems( messagePropertyReflector.getOutputPart(), computeMetricGem.getInputPart(0));
            gemGraph.connectGems( computeMetricGem.getOutputPart(), metricGem.getInputPart(0));
            
            bindingContext.addCollector(md, metricGem);
            
            assert graphIsValid() : gemGraph.toString();
        }
    }

    /**
     * Converts a name to a form suitable for use as the name of a CollectorGem
     * 
     * @param name
     * @return collector name
     */
    private String makeCollectorName (String name) {
        return name.toLowerCase().replace('.', '_');
    }


    /**
     * Adds all of the trigger gems to the graph and connects them to the message property/metric collectors
     */
    private Gem addTriggerGems () {
        List<TriggerDescription> boundGemList = jobDescription.getTriggerDescriptions();
        
        return addGems (boundGemList);
    }

    /**
     * Adds all of the action gems to the graph and connects them to the message property/metric collectors
     */
    private Gem addActionGems () {
        List<ActionDescription> boundGemList = jobDescription.getActionDescriptions();
        
        return addGems (boundGemList);
    }

    /**
     * Add a list of gems to the gem graph. Each gem and it's inputs are defined by a BoundGemDescription
     * 
     * @param boundGemList
     * @return Returns the top-level gem that results from conjoining all the gems described by the boundGemList
     */
    private Gem addGems (List<? extends BoundGemDescription> boundGemList) {
        if (boundGemList == null || boundGemList.size() == 0) {
            throw new IllegalArgumentException ("The list of bound gems must not be empty");
        }
        
        Gem result = null;
        
        for (int gemN = 0; gemN < boundGemList.size(); ++gemN) {
            BoundGemDescription gemDescription = boundGemList.get(gemN);
                       
            FunctionalAgentGem zipGem = new FunctionalAgentGem(calServices.getGemEntity(zippingGems[gemDescription.getVariableBindingCount()]));
            FunctionalAgentGem zippingFunctionGem = new FunctionalAgentGem(calServices.getGemEntity(gemDescription.getQualifiedName()));
                  
            gemGraph.addGem(zipGem);
            gemGraph.addGem(zippingFunctionGem);

            connectInputs (zipGem, zippingFunctionGem, gemDescription, gemGraph);
            
            if (result == null) {
                result = zipGem;
            } else {
                FunctionalAgentGem zipGem2 = new FunctionalAgentGem(calServices.getGemEntity(CAL_List.Functions.zipWith));
                gemGraph.addGem (zipGem2);
                
                FunctionalAgentGem andGem = new FunctionalAgentGem(calServices.getGemEntity(CAL_Prelude.Functions.and));
                gemGraph.addGem (andGem);
                andGem.getInputPart(0).setBurnt(true);
                andGem.getInputPart(1).setBurnt(true);
                
                gemGraph.connectGems(andGem.getOutputPart(), zipGem2.getInputPart(0));
                gemGraph.connectGems(zipGem.getOutputPart(), zipGem2.getInputPart(1));
                gemGraph.connectGems(result.getOutputPart(), zipGem2.getInputPart(2));
                
                result = zipGem2;
                
         
            
            }
        }
        
        
        return result;
    }


    /**
     * Connect the inputs to a gem. The inputs are defined by InputBindings in the BoundGemDescription.
     * 
     * @param zipGem
     * @param zippingFunctionGem
     * @param boundGemDescription
     * @param gemGraph
     */
    private void connectInputs (FunctionalAgentGem zipGem, FunctionalAgentGem zippingFunctionGem, BoundGemDescription boundGemDescription, GemGraph gemGraph) {
        
        //connect the zipping function
        int zipInput=0;
        gemGraph.connectGems(zippingFunctionGem.getOutputPart(),zipGem.getInputPart(zipInput++));
        
        //connect the parameters
        for (int inputN = 0; inputN < boundGemDescription.getBindingCount(); ++inputN) {
            InputBinding binding = boundGemDescription.getNthBinding(inputN);
            Gem outputGem = binding.getOutputGem(calServices, gemGraph, bindingContext);

            if (binding.isConstant()) {
                gemGraph.connectGems(outputGem.getOutputPart(), zippingFunctionGem.getInputPart(inputN));
            } else {
                zippingFunctionGem.getInputPart(inputN).setBurnt(true);
                gemGraph.connectGems(outputGem.getOutputPart(), zipGem.getInputPart(zipInput++));
            }
        }
        
        assert graphIsValid() : gemGraph.toString();
    }

    /**
     * Check that the graph is valid
     * @return true if the graph is valid
     */
    private boolean graphIsValid() {
        TypeCheckInfo typeCheckInfo = calServices.getTypeCheckInfo(MonitorApp.TARGET_MODULE);
        return (gemGraph.checkGraphValid(typeCheckInfo));
    }

    /**
     * This combines the output of the trigger gems with the action gems and connects the output
     * of the action gems to the result gem.
     * 
     * @param triggerResultGem
     * @param actionResultGem
     */
    private void finishGemGraph (Gem triggerResultGem, Gem actionResultGem) {
        FunctionalAgentGem zipGem = new FunctionalAgentGem(calServices.getGemEntity(CAL_List.Functions.zipWith));
        gemGraph.addGem (zipGem);
        
        FunctionalAgentGem andGem = new FunctionalAgentGem(calServices.getGemEntity(CAL_Prelude.Functions.and));
        gemGraph.addGem (andGem);
        andGem.getInputPart(0).setBurnt(true);
        andGem.getInputPart(1).setBurnt(true);
        
        gemGraph.connectGems(andGem.getOutputPart(), zipGem.getInputPart(0));
        gemGraph.connectGems(triggerResultGem.getOutputPart(), zipGem.getInputPart(1));
        gemGraph.connectGems(actionResultGem.getOutputPart(), zipGem.getInputPart(2));
        
        Gem toIteratorGem = new FunctionalAgentGem(calServices.getGemEntity(CAL_List.Functions.toJIterator));
        gemGraph.addGem(toIteratorGem);
        
        gemGraph.connectGems(zipGem.getOutputPart(), toIteratorGem.getInputPart(0));           
        gemGraph.connectGems( toIteratorGem.getOutputPart(), gemGraph.getTargetCollector().getInputPart(0));
    }

}
