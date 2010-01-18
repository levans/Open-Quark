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
 * CALSourceGenerator.java
 * Creation date: Nov 20, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.openquark.cal.compiler.IdentifierResolver.LocalBindingsProcessor;
import org.openquark.cal.compiler.CompositionNode.Collector;
import org.openquark.cal.compiler.CompositionNode.CompositionArgument;
import org.openquark.cal.compiler.CompositionNode.Emitter;
import org.openquark.cal.compiler.SourceModel.Expr.Record.FieldModification;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.util.Pair;


/**
 * Given a connected set of nodes representing a composition tree, CALSourceGenerator can be used
 * to generate the corresponding CAL source code for that tree.
 * @author Edward Lam
 */
public final class CALSourceGenerator {

    /** A source model call to the error function.
     *  This is used while generating source for type checking a composition node graph,
     *  when extra arguments are used in a collector definition which don't appear as emitter arguments.
     */
    private static final SourceModel.Expr EXTRA_ARG_ERROR_CALL_EXPR = 
        CAL_Prelude.Functions.error(SourceModel.Expr.Literal.StringLit.make("extraArg"));
    
    /**
     * A helper class to represent the results of source code analysis and generation on a given subtree.
     * @author Edward Lam
     */
    private static class SourceModelGenerationResult {

        /** The expression for the analyzed tree */
        private final SourceModel.Expr expression;
        
        /** The untraversed collectors in the analyzed tree. */
        private final Set<Collector> untraversedCollectors;
        
        /** The collectors used (by Emitters or Reflectors) in descendant aggregations, 
         *  but not defined in that aggregation. */
        private final Set<Collector> unaggregatedCollectorSet;

        /**
         * Constructor for a SourceModelGenerationResult.
         * @param expression the source model of the expression generated.
         * @param untraversedCollectors the untraversed collectors in the analyzed tree.
         * @param unaggregatedCollectorSet the collectors used (by Emitters or Reflectors) 
         *   in descendant aggregations, but not defined in that aggregation
         */
        SourceModelGenerationResult(SourceModel.Expr expression, Set<Collector> untraversedCollectors, Set<Collector> unaggregatedCollectorSet) {
            this.expression = expression;
            this.untraversedCollectors = new HashSet<Collector>(untraversedCollectors);
            this.unaggregatedCollectorSet = new HashSet<Collector>(unaggregatedCollectorSet);
        }

        /**
         * Get the expression.
         * @return the expression.
         */
        SourceModel.Expr getExpression() {
            return expression;
        }

        /**
         * Get the untraversed collectors.
         * @return Set the untraversed collectors in this subtree.
         */
        Set<Collector> getUntraversedCollectors() {
            return new HashSet<Collector>(untraversedCollectors);
        }
        
        /**
         * Returns the unaggregated collectors.
         * @return Set the collectors used (by Emitters) in child aggregations, 
         *   but not defined in that aggregation.
         */
        Set<Collector> getUnaggregatedCollectorSet() {
            return unaggregatedCollectorSet;
        }
    }


    /**
     * A helper class to encapsulate the result of getCheckGraphSource().
     * @author Edward Lam
     */
    static class CheckGraphSource {

        /** The source model used to generate the types in the graph. */
        private final SourceModel.FunctionDefn graphFunction;
        
        /**
         * Ordered map from CompositionNode root to the arguments targeting that root.
         * An iterator over the keys of the map will return the roots in the order in which they appear as args in the graph text.
         */
        private final Map<CompositionNode, List<CompositionArgument>> rootToArgumentsMap;

        /** The arguments which are not used by the collector which they target. */
        private final List<CompositionArgument> unusedArgumentList;
        
        /** Map from recursive emitter arg to the arg which it reflects. */
        private final Map<CompositionArgument, CompositionArgument> recursiveEmitterArgumentToReflectedInputMap;
        
        /**
         * Trivial constructor for this class.
         * @param graphFunction the source model used to generate types in the graph.
         * @param rootToArgumentsMap 
         *   Ordered map from CompositionNode root to the arguments targeting that root.
         * @param unusedArgumentList the arguments which are not used by the collector which they target.
         * @param recursiveEmitterArgumentToReflectedInputMap  
         *   map from recursive emitter arg to the arg which it reflects.
         */
        CheckGraphSource(
                SourceModel.FunctionDefn graphFunction,
                Map<CompositionNode, List<CompositionArgument>> rootToArgumentsMap,
                List<CompositionArgument> unusedArgumentList,
                Map<CompositionArgument, CompositionArgument> recursiveEmitterArgumentToReflectedInputMap) {
            this.graphFunction = graphFunction;
            this.rootToArgumentsMap = rootToArgumentsMap;
            this.unusedArgumentList = unusedArgumentList;
            this.recursiveEmitterArgumentToReflectedInputMap = recursiveEmitterArgumentToReflectedInputMap;
        }
        
        /**
         * Returns the generated source model of the sc that represents a typecheck-able representation of the graph.
         * @return String
         */
        SourceModel.FunctionDefn getGraphFunction() {
            return graphFunction;
        }
        
        /**
         * Returns the rootToArgumentsMap.
         * @return 
         *   Ordered map from CompositionNode root to the arguments targeting that root.
         *   An iterator over the keys of the map will return the roots in the order in which they appear as args in the graph text.
         */
        Map<CompositionNode, List<CompositionArgument>> getRootToArgumentsMap() {
            return new LinkedHashMap<CompositionNode, List<CompositionArgument>>(rootToArgumentsMap);
        }
        
        /**
         * @return The arguments which are not used by the collector which they target.
         */
        List<CompositionArgument> getUnusedArgumentList() {
            return new ArrayList<CompositionArgument>(unusedArgumentList);
        }

        /**
         * @return map from recursive emitter arg to the arg which it reflects.
         */
        public Map<CompositionArgument, CompositionArgument> getRecursiveEmitterArgumentToReflectedInputMap() {
            return new HashMap<CompositionArgument, CompositionArgument>(recursiveEmitterArgumentToReflectedInputMap);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return super.toString() + "\nGraph text: " + graphFunction.toSourceText() + "\n";
        }
    }

    /**
     * A class to conveniently encapsulate some information about the CompositionNode graph.
     * During source generation, the GraphInfo object is created to pre-calculate some info needed during actual text generation.
     * 
     * GraphInfo construction occurs in three steps:
     *  - calculation of collector-target info.
     *     Maps collectors to targets and vice versa.
     *  - calculation of non-target related collector info.
     *     Calculates collector names and collector arguments.
     *  - calculation of argument names.
     * 
     * @author Edward Lam
     */
    private static class GraphInfo {
        
        /** 
         * Map to unconnected collector from its argument, if such collector definitions are elided.
         * Null if such definitions are not elided.
         */
        private final Map<CompositionArgument, Collector> inputToNakedCollectorMap;
        
        /** Calculated info about collector targets. */
        private final CollectorTargetInfo collectorTargetInfo;
        
        /** Map from recursive emitter arg to the arg which it reflects. */
        private final Map<CompositionArgument, CompositionArgument> recursiveEmitterArgumentToReflectedInputMap;
        
        /** Map from collector to name. */
        private final Map<Collector, String> collectorToNameMap = new HashMap<Collector, String>();

        /** 
         *   Map from collector to the arguments which appear in that collector's definition.
         *   Unused arguments are mapped to a null key.
         */
        private final Map<Collector, List<CompositionArgument>> collectorToArgumentsMap = new HashMap<Collector, List<CompositionArgument>>();
        
        /** Map from argument to its name. */
        private final Map<CompositionArgument, String> argumentToNameMap = new HashMap<CompositionArgument, String>();
        
        /**
         * Map from collector to corresponding root node
         * Because all roots are required to be rooted by a collector during type check source generation,
         * collectors are attached to non-collected roots, and the original root is tracked using this map.
         */
        private final Map<Collector, CompositionNode> collectedRootToRootNodeMap = new HashMap<Collector, CompositionNode>();
        
        /** The list of arguments which are unused by the collector they target. */
        private final List<CompositionArgument> unusedArgumentList = new ArrayList<CompositionArgument>();

        /** If non-null, map from collector to its replacement, for all collectors targeted by 
         *   collectors known to this info object, but not actually in this object. 
         *  This is needed because, during generation of source for type checking composition node graphs, a group of root nodes
         *    will be provided, but some of the nodes targeted by these nodes will not be included.  */
        private final Map<Collector, SyntheticCollector> excludedCollectorReplacementMap;
        
        /** Map caching the result of calls to getCollectorArguments() 
         *  Maps the argument to the result. */
        private Map<Collector, List<CompositionArgument>> cachedCollectorArgumentsMap = new HashMap<Collector, List<CompositionArgument>>();
        
        /**
         * This class encapsulates information about collector targets calculated during source generation.
         * @author Edward Lam
         */
        private static class CollectorTargetInfo {

            /** The target of the graph. */
            private final Collector graphTarget;
            
            /** Map from collector to the collectors which target that collector. */
            private final Map<Collector, Set<Collector>> collectorToTargetingCollectorsMap = new HashMap<Collector, Set<Collector>>();
            
            /** Map from collector to its target. */
            private final Map<Collector, Collector> collectorToTargetMap = new HashMap<Collector, Collector>();
            
            
            /**
             * Trivial constructor for this class..
             * @param graphTarget the graph target.
             */
            CollectorTargetInfo(Collector graphTarget) {
                this.graphTarget = graphTarget;
            }
            
            /**
             * Map a collector to its target.
             * @param collector the collector in question.
             * @param excludedCollectorReplacementMap  
             *   if non-null, map from excluded collector to the collector with which it should be considered to be replaced.
             */
            void mapCollectorToTarget(Collector collector, Map<Collector, SyntheticCollector> excludedCollectorReplacementMap) {
                
                // Add the collector-target mappings.
                Collector targetCollector = collector.getTargetCollector();
                
                // If it's supposed to be an excluded collector, set the replacement as the target.
                if (excludedCollectorReplacementMap != null && excludedCollectorReplacementMap.containsKey(targetCollector)) {
                    targetCollector = excludedCollectorReplacementMap.get(targetCollector);
                }
                // Special case: sometimes a synthetic collector is created to encapsulate all other collectors.
                //  In this case, the formerly top-level target should be treated as targeting the synthetic collector.
                if (targetCollector == null && collector != graphTarget) {
                    targetCollector = graphTarget;
                }
                
                collectorToTargetMap.put(collector, targetCollector);
                
                Set<Collector> collectorsTargetingTarget = collectorToTargetingCollectorsMap.get(targetCollector);
                if (collectorsTargetingTarget == null) {
                    collectorsTargetingTarget = new HashSet<Collector>();
                    collectorToTargetingCollectorsMap.put(targetCollector, collectorsTargetingTarget);
                }
                collectorsTargetingTarget.add(collector);
            }
            
            /**
             * Reconcile the info collected by this info object to reflect any collector visibility constraints
             *  eg. because graph target is not actually the target.
             * This method should be called after all collectors have been mapped.
             */
            void reconcileCollectorTargets() {
                // First off, ensure the graph target has a null target.
                collectorToTargetMap.put(graphTarget, null);
                
                // (Set of Collector) Collectors which are targeted, but not mapped.
                Set<Collector> unmappedTargetedCollectors = new HashSet<Collector>();

                // Populate the map..
                for (final Collector targetedCollector : collectorToTargetingCollectorsMap.keySet()) {                    
                    if (targetedCollector != null && !isCollectorMapped(targetedCollector)) {
                        unmappedTargetedCollectors.add(targetedCollector);
                    }
                }
                
                // Unmapped collectors are collectors which aren't used in subtrees descending from the graph target.
                // Any mapped collectors which target an unmapped collector will be set instead to target the graph target.
                //   Below, this is referred to as "detargeting" the targeted unmapped collector.
                
                if (!unmappedTargetedCollectors.isEmpty()) {
                    // Get the graph target's targeting collectors.
                    Set<Collector> collectorsTargetingGraphTarget = collectorToTargetingCollectorsMap.get(graphTarget);
                    if (collectorsTargetingGraphTarget == null) {
                        collectorsTargetingGraphTarget = new HashSet<Collector>();
                        collectorToTargetingCollectorsMap.put(graphTarget, collectorsTargetingGraphTarget);
                    }
                    
                    // Retarget any collectors targeting a detargeted collector to the graph target.
                    for (final Collector collectorToDetarget : unmappedTargetedCollectors) {                        

                        Set<Collector> collectorsTargetingCollectorToDetarget = collectorToTargetingCollectorsMap.get(collectorToDetarget);
                        if (collectorsTargetingCollectorToDetarget != null) {

                            // Iterate over the collectors targeting the collector to detarget.
                            for (final Collector collectorTargetingCollectorToDetarget : collectorsTargetingCollectorToDetarget) {
                               
                                // Do not retarget the graph target to itself!!
                                if (collectorTargetingCollectorToDetarget == graphTarget) {
                                    continue;
                                }
                                
                                // Don't add entries for other unmapped collectors
                                if (unmappedTargetedCollectors.contains(collectorTargetingCollectorToDetarget)) {
                                    continue;
                                }
                                
                                collectorToTargetMap.put(collectorTargetingCollectorToDetarget, graphTarget);   // overwrites the old mapping.
                                collectorsTargetingGraphTarget.add(collectorTargetingCollectorToDetarget);
                            }
                        }
                        
                        // Remove any mappings for collectorToDetarget.
                        collectorToTargetMap.remove(collectorToDetarget);
                        Set<Collector> collectorsTargetingDetargetTarget = collectorToTargetingCollectorsMap.get(collectorToDetarget.getTargetCollector());
                        if (collectorsTargetingDetargetTarget != null) {
                            collectorsTargetingDetargetTarget.remove(collectorToDetarget);
                        }
                    }
                }
            }

            /**
             * Return whether the given collector has been mapped.
             * @param collector the collector in question.
             * @return whether the given collector was mapped via call to mapCollectorToTarget().
             */
            boolean isCollectorMapped(Collector collector) {
                return collectorToTargetMap.containsKey(collector);
            }
            
            /**
             * Get the collectors mapped by this info object.
             * @return (Set of Collector)
             */
            Set<Collector> getCollectors() {
                return Collections.unmodifiableSet(collectorToTargetMap.keySet());
            }
            
            /**
             * Get the target of the graph, as calculated by this info object.
             * @return the graph target.
             */
            public Collector getGraphTarget() {
                return graphTarget;
            }
            
            /**
             * Get a collector's target, as calculated by this info object.
             * @param collector the collector in question.
             * @return the collector's target, according to this info object.
             */
            Collector getCollectorTarget(Collector collector) {
                return collectorToTargetMap.get(collector);
            }
            
            /**
             * Get the collectors targeting a given collector, as calculated by this info object.
             * @param collector the collector in question.
             * @return the collectors targeting a given collector, as calculated by this info object.
             */
            List<Collector> getTargetingCollectors(Collector collector) {

                // Create the list.
                List<Collector> targetingCollectorList = new ArrayList<Collector>();
                
                // Add all the collectors found to target this collector from the map.
                Set<Collector> targetingCollectorSet = collectorToTargetingCollectorsMap.get(collector);
                if (targetingCollectorSet != null) {
                    targetingCollectorList.addAll(targetingCollectorSet);
                }
                
                // Return the list.
                return targetingCollectorList;
            }
        }
        
        /**
         * Constructor for an info considering a given collector to be the root.
         * 
         * Notes:
         * 1) Even though in theory, only top-level targets should be executed, in practice a client will often find 
         *    it more convenient to execute non-top level gems, 
         * 2) A Collector "knows" that arguments are pointing to it, but we need to traverse the composition node graph
         *    to find whether the argument should appear in the source (it may not because its subtree may be unused).
         * Unbound arguments will be targeted to the root
         * 
         * @param collector the collector which is considered to be the root.
         * @param scName the name to use for the root collector, or null to use the collector's name.
         */
        GraphInfo(Collector collector, String scName) {
            excludedCollectorReplacementMap = null;
            inputToNakedCollectorMap = new HashMap<CompositionArgument, Collector>();
            if (scName != null) {
                collectorToNameMap.put(collector, scName);
            }
            
            collectorTargetInfo = new CollectorTargetInfo(collector);
            calculateCollectorTargetInfo(collectorTargetInfo);
            recursiveEmitterArgumentToReflectedInputMap = calculateFreeRecursiveEmitterArguments(collectorTargetInfo.getCollectors());
            calculateCollectorNonTargetInfo(collector, Collections.<String>emptySet(), null);
            calculateArgumentNames(collector, Collections.<String>emptySet(), getCollectorNames(), collectorTargetInfo);
        }

        /**
         * Constructor for an info suitable for type checking the given root nodes.
         * @param inputRootNodes the root nodes in question.
         */
        GraphInfo(Set<? extends CompositionNode> inputRootNodes) {
            inputToNakedCollectorMap = null;
            
            // Calculate info for excluded collectors (ie. collectors targeted by rootNodes).
            excludedCollectorReplacementMap = getExcludedCollectors(inputRootNodes);
            Set<CompositionNode> rootNodesToAnalyze = new HashSet<CompositionNode>(inputRootNodes);
            rootNodesToAnalyze.addAll(excludedCollectorReplacementMap.values());
            
            // Calculate the target for the CompositionNode graph.
            Collector rootNodeTarget = getGraphTarget(rootNodesToAnalyze, excludedCollectorReplacementMap);
            
            // Ensure the target is a member of the root nodes set. 
            rootNodesToAnalyze.add(rootNodeTarget);
            
            // Create a dummy collector for which the body will be the check graph expression.  Set this as the graph target.
            CompositionNode checkGraphConnectedNode = new CompositionNode.Value() {
                public String getStringValue() {
                    return "0.0";
                }
                public SourceModel.Expr getSourceModel() {
                    return SourceModel.Expr.Literal.Double.make(0.0);
                }
                public int getNArguments() {
                    return 0;
                }
                public CompositionArgument getNodeArgument(int i) {
                    return null;
                }
            };
            Collector graphTarget = new SyntheticCollector("dummyCollector", Collections.<CompositionArgument>emptyList(), checkGraphConnectedNode);
            rootNodesToAnalyze.add(graphTarget);
            
            // The set of all collector names.
            Set<String> collectorNames = new HashSet<String>();

            // Gather collector names, collected and uncollected roots.
            Set<CompositionNode> uncollectedRoots = new HashSet<CompositionNode>();
            for (final CompositionNode nextRoot : rootNodesToAnalyze) {                
                if (!(nextRoot instanceof Collector)) {
                    uncollectedRoots.add(nextRoot);
                    
                } else {
                    collectedRootToRootNodeMap.put((Collector)nextRoot, nextRoot);
                    collectorNames.add(((Collector)nextRoot).getUnqualifiedName());
                }
            }
            
            // Attach collectors to uncollected roots.  Add these to the root nodes..
            Map<Collector, CompositionNode> collectedToUncollectedRootMap = attachCollectorsToUncollectedRoots(uncollectedRoots, rootNodeTarget, 
                                                                                   collectorNames, excludedCollectorReplacementMap);
            collectedRootToRootNodeMap.putAll(collectedToUncollectedRootMap);
            
            // Get the set of collectors.
            Set<Collector> collectorSet = new HashSet<Collector>(collectedRootToRootNodeMap.keySet());

            // Calculate collector target info.
            collectorTargetInfo = new CollectorTargetInfo(graphTarget);
            calculateCollectorTargetInfo(collectorSet, collectorTargetInfo, excludedCollectorReplacementMap);
            
            // Calculate free recursive emitter arguments.
            recursiveEmitterArgumentToReflectedInputMap = calculateFreeRecursiveEmitterArguments(collectorTargetInfo.getCollectors());
            
            // Get info on what arguments are targeting each collector (as targetCollectorTargetInfo).
            CollectorTargetInfo targetCollectorTargetInfo = new CollectorTargetInfo(rootNodeTarget);
            for (final CompositionNode rootNode : rootNodesToAnalyze) {               
                if (rootNode instanceof Collector && !targetCollectorTargetInfo.isCollectorMapped((Collector)rootNode)) {
                    calculateCollectorTargetInfoHelper((Collector)rootNode, targetCollectorTargetInfo);
                }
            }

            // Calculate collector non-target info.
            calculateCollectorNonTargetInfo(graphTarget, Collections.<String>emptySet(), targetCollectorTargetInfo);

            // Calculate names for all the arguments.
            calculateArgumentNames(graphTarget, Collections.<String>emptySet(), collectorNames, collectorTargetInfo);
        }
        
        /**
         * Get the target from a given group of root nodes.
         * @param rootNodes the roots in question.
         * @param excludedCollectorReplacementMap map from excluded collector to its replacement.
         *   This may be modified if the graph target is not already found to be in either the set or the map.
         * @return the first collector root in the set which has a null target.
         *   If there are no such collectors, a new temporary collector will be returned.
         */
        private static Collector getGraphTarget(Set<CompositionNode> rootNodes, Map<Collector, SyntheticCollector> excludedCollectorReplacementMap) {
            Collector graphTarget = null;
            
            // Calculate the target for the CompositionNode graph..
            for (final CompositionNode nextRoot : rootNodes) {              
                
                // When we find the first collector, find its outermost enclosing collector.  This should be the target.
                if (nextRoot instanceof Collector) {
                    graphTarget = (Collector)nextRoot;

                    Collector enclosingCollector = graphTarget.getTargetCollector();
                    while (enclosingCollector != null) {
                        graphTarget = enclosingCollector;
                        enclosingCollector = graphTarget.getTargetCollector();
                    }
                    break;
                }
            }
            if (graphTarget == null) {
                // If there are no collectors in the root nodes set, create a temporary one to set as the target.
                graphTarget = getAsCollectorRoot(null, "tempGraphTarget");
            } else {
                // Ensure the graph target is either in the set of root nodes or in the map.
                graphTarget = getCollector(graphTarget, rootNodes, excludedCollectorReplacementMap);
            }
            
            return graphTarget;
        }

        /**
         * Calculate collector target info over a group of collectors.
         * 
         * @param collectorTargetInfo the info object which will be populated.
         */
        private static void calculateCollectorTargetInfo(CollectorTargetInfo collectorTargetInfo) {
            calculateCollectorTargetInfoHelper(collectorTargetInfo.getGraphTarget(), collectorTargetInfo);
            collectorTargetInfo.reconcileCollectorTargets();
        }

        /**
         * Helper for calculateCollectorTargetInfo().
         * Calls itself recursively to calculate collector target info.
         * 
         * @param collector the collector under consideration.
         * @param collectorTargetInfo the info object which will be populated.
         */

        private static void calculateCollectorTargetInfoHelper(Collector collector, CollectorTargetInfo collectorTargetInfo) {
            collectorTargetInfo.mapCollectorToTarget(collector, null);
            
            // Get the collectors used by the subtree.
            SubtreeNodeInfo collectorTreeInfo = getSubtreeNodeInfo(collector);
            Set<Collector> collectorsUsed = collectorTreeInfo.getCollectorsUsed();
            
            // Call recursively on the collectors which weren't already analysed.
            for (final Collector usedCollector : collectorsUsed) {               
                if (!collectorTargetInfo.isCollectorMapped(usedCollector)) {
                    calculateCollectorTargetInfoHelper(usedCollector, collectorTargetInfo);
                }
            }
        }

        /**
         * Calculate collector target info over a group of collectors.
         * 
         * @param collectorSet (Set of Collector) the collectors involved.
         * @param collectorTargetInfo the info object which will be populated.
         * @param excludedCollectorReplacementMap (Collector->SyntheticCollector) map from excluded collector to its replacement.
         */
        private static void calculateCollectorTargetInfo(Set<Collector> collectorSet, CollectorTargetInfo collectorTargetInfo, Map<Collector, SyntheticCollector> excludedCollectorReplacementMap) {
            calculateCollectorTargetInfoHelper(collectorSet, collectorTargetInfo, excludedCollectorReplacementMap);
        }
        
        /**
         * Calculate collector target info over a group of collectors.
         * 
         * @param collectorSet the collectors involved.
         * @param collectorTargetInfo the info object which will be populated.
         * @param excludedCollectorReplacementMap map from excluded collector to its replacement.
         */
        private static void calculateCollectorTargetInfoHelper(Set<Collector> collectorSet, CollectorTargetInfo collectorTargetInfo, Map<Collector, SyntheticCollector> excludedCollectorReplacementMap) {
            // Iterate over the collectors in the graph, mapping them to their targets.
            for (final Collector collector : collectorSet) {               
                collectorTargetInfo.mapCollectorToTarget(collector, excludedCollectorReplacementMap);
            }
        }
        
        /**
         * Calculate all recursive emitter arguments for the collectors in the given collectorTargetInfo.
         * @param collectors  the collectors for which recursive emitter arguments should be calculated.
         * @return  map from recursive emitter arg to the arg which it reflects.
         */
        private static Map<CompositionArgument, CompositionArgument> calculateFreeRecursiveEmitterArguments(Set<Collector> collectors) {
            
            Map<CompositionArgument, CompositionArgument> recursiveEmitterArgumentToReflectedInputMap = new HashMap<CompositionArgument, CompositionArgument>();
            
            // Iterate over all the collectors..
            for (final Collector collector : collectors) {
                
                // Get summary info for all descendant subtrees.
                SubtreeNodeInfo subtreeNodeInfo = getDescendantSubtreeNodeInfo(collector);
                
                // If the collectors used does not contain the collector itself, it will not have a recursive emitter argument.
                Set<Collector> collectorsUsed = subtreeNodeInfo.getCollectorsUsed();
                if (!collectorsUsed.contains(collector)) {
                    continue;
                }
                
                // Map from free unburnt arguments for any emitters emitting the collector's def to the arg index.
                Map<CompositionArgument, Integer> collectorEmitterArgumentToIndexMap = new HashMap<CompositionArgument, Integer>();      

                int nEmitterArgs = -1;
                for (final CompositionNode.Emitter emitter : subtreeNodeInfo.getEmitters()) {
                   
                    if (emitter.getCollectorNode() == collector) {
                        nEmitterArgs = emitter.getNArguments();
                        for (int i = 0; i < nEmitterArgs; i++) {
                            CompositionArgument emitterArgument = emitter.getNodeArgument(i);

                            if (emitterArgument.getConnectedNode() == null && !emitterArgument.isBurnt()) {
                                collectorEmitterArgumentToIndexMap.put(emitterArgument, Integer.valueOf(i));
                            }
                        }
                    }
                }
                
                // Another check for nothing to do..
                if (nEmitterArgs < 0 || collectorEmitterArgumentToIndexMap.isEmpty()) {
                    continue;
                }
                
                // Calculate the reflected inputs for the collector.
                // These are the target arguments which are also descendant free unburnt args, but which are not recursive emitter args.
                List<CompositionArgument> descendantFreeUnburntArguments = subtreeNodeInfo.getFreeUnburntArguments();
                Set<CompositionArgument> reflectedArguments = new LinkedHashSet<CompositionArgument>(collector.getTargetArguments());
                reflectedArguments.retainAll(descendantFreeUnburntArguments);
                reflectedArguments.removeAll(collectorEmitterArgumentToIndexMap.keySet());
                
                if (nEmitterArgs != reflectedArguments.size()) {
                    throw new IllegalStateException("The number of reflected arguments must be equal to the number of emitter args.  "
                                                    + nEmitterArgs + " != " + reflectedArguments.size());
                }
                
                // Map from emitter arg index to the argument reflected at that index.
                Map<Integer, CompositionArgument> indexToReflectedArgumentMap = new HashMap<Integer, CompositionArgument>();
                int index = 0;
                for (final CompositionArgument arg : reflectedArguments) {
                    indexToReflectedArgumentMap.put(Integer.valueOf(index), arg);
                    index++;
                }

                // Now add all recursive args to the recursiveEmitterArgumentToReflectedInputMap.
                for (final Map.Entry<CompositionArgument, Integer> entry : collectorEmitterArgumentToIndexMap.entrySet()) {                   
                    final CompositionArgument collectorEmitterArgument = entry.getKey();
                    Integer argIndex = entry.getValue();
                    recursiveEmitterArgumentToReflectedInputMap.put(collectorEmitterArgument, indexToReflectedArgumentMap.get(argIndex));
                }
            }
            
            return recursiveEmitterArgumentToReflectedInputMap;
        }

        /**
         * Calls itself recursively to calculate non-target collector-related graph info.
         * This includes collector-name map and collector-arguments map.
         * Collector names will not be generated for collectors which already have mappings in the collectorToNameMap.
         * 
         * @param collector the collector under consideration.
         * @param visibleCollectorNames the names of collectors visible from the current collector.
         * @param targetCollectorTargetInfo if non-null, the collectorTargetInfo for the definition of the graph target.
         *   If null, this object's collector target info object will be used.
         */
        private void calculateCollectorNonTargetInfo(Collector collector, Set<String> visibleCollectorNames, CollectorTargetInfo targetCollectorTargetInfo) {
            if (targetCollectorTargetInfo == null) {
                targetCollectorTargetInfo = collectorTargetInfo;
            }
            
            visibleCollectorNames = new HashSet<String>(visibleCollectorNames);
            
            // See if the collector is "naked".
            // This is true if it's a) unconnected and b) has no arguments.  
            //   b) should be true if the collector is not targeted by its input, and has no inner collectors.  
            //   In practice, we only have to perform the first check, since if it has inner collectors, 
            //     we will be generating check graph source, which does not optimize out naked collectors.
            if (inputToNakedCollectorMap != null) {
                CompositionArgument collectorInput = collector.getNodeArgument(0);
                if (collector.getDeclaredType() == null && collectorInput.getConnectedNode() == null && 
                        getArgumentCollectorTarget(collectorInput, collector, collectorTargetInfo) != collector) {
                    
                    inputToNakedCollectorMap.put(collectorInput, collector);
                }
            }
            
            if (!collectorToNameMap.containsKey(collector)) {
                // Calculate the collector name.
                String baseCollectorName = collector.getUnqualifiedName();
                String candidateCollectorName = baseCollectorName;
                int nextIndex = 2;
                while (visibleCollectorNames.contains(candidateCollectorName)) {
                    candidateCollectorName = baseCollectorName + "_" + nextIndex;
                    nextIndex++;
                }
                collectorToNameMap.put(collector, candidateCollectorName);
                visibleCollectorNames.add(candidateCollectorName);
            }
            
            // Get the collectors used by the subtree.
            List<Collector> targetingCollectors = collectorTargetInfo.getTargetingCollectors(collector);
            
            // Calculate args.
            SubtreeNodeInfo collectorSubtreeNodeInfo = getSubtreeNodeInfo(collector);
            
            for (final CompositionArgument freeUnburntArgument : collectorSubtreeNodeInfo.getFreeUnburntArguments()) {               
                Collector argumentTarget = getArgumentCollectorTarget(freeUnburntArgument, collector, collectorTargetInfo);
                
                // Check for the special case where an emitter argument target's the emitter's collector.
                //  In this case, the argument should not appear an an argument on the collector.
                if (recursiveEmitterArgumentToReflectedInputMap.containsKey(freeUnburntArgument)) {
                    continue;
                }

                if (collectorTargetInfo != targetCollectorTargetInfo) {
                    Collector realArgumentTarget = getArgumentCollectorTarget(freeUnburntArgument, collector, targetCollectorTargetInfo);

                    // Check for the case of an argument which isn't used by the collector which it targets.
                    if (argumentTarget != null && realArgumentTarget == null) {
                        unusedArgumentList.add(freeUnburntArgument);
                        continue;
                    }

                    // Check for the case of an argument whose real target isn't mapped.
                    if (!collectorTargetInfo.isCollectorMapped(realArgumentTarget)) {
                        unusedArgumentList.add(freeUnburntArgument);
                        continue;
                    }
                    
                }
                
                // Add to the target (which may be null).
                List<CompositionArgument> argumentList = collectorToArgumentsMap.get(argumentTarget);
                if (argumentList == null) {
                    argumentList = new ArrayList<CompositionArgument>();
                    collectorToArgumentsMap.put(argumentTarget, argumentList);
                }
                argumentList.add(freeUnburntArgument);
            }
            
            // Call recursively on targeting collectors.
            for (final Collector targetingCollector : targetingCollectors) {                
                calculateCollectorNonTargetInfo(targetingCollector, visibleCollectorNames, targetCollectorTargetInfo);
            }
        }
        
        /**
         * Calls itself recursively to calculate the names of free arguments in the graph.
         * @param collector the collector for which targeting argument names should be calculated.
         * @param visibleArgumentNames (Set of String) the names of arguments from enclosing scopes.
         * @param reservedNames (Set of String) names which cannot be used for arguments.
         */
        private void calculateArgumentNames(Collector collector, Set<String> visibleArgumentNames, 
                                            Set<String> reservedNames, CollectorTargetInfo collectorTargetInfo) {
            
            // Copy the visible arg names Set.  This will be the set of arg names visible at this level.
            visibleArgumentNames = new HashSet<String>(visibleArgumentNames);
            
            // Get the arguments which appear on the collector definition.
            List<CompositionArgument> argumentList = getCollectorArguments(collector);
            
            // If this is the top-level target, also calculate argument names for unused arguments.
            if (collector == collectorTargetInfo.getGraphTarget()) {
                argumentList = new ArrayList<CompositionArgument>(argumentList);
                argumentList.addAll(getUnusedArguments());
            }
            
            if (!argumentList.isEmpty()) {
            
                // Iterate over the arguments on this collector, calculating the arg names.
                for (final CompositionArgument argument : argumentList) {                   
                    
                    // Calculate the argument name.
                    String argumentName;
                    if (inputToNakedCollectorMap != null && inputToNakedCollectorMap.containsKey(argument)) {
                        argumentName = getCollectorName(inputToNakedCollectorMap.get(argument));
                        
                    } else {
                        String baseArgumentName = argument.getNameString();
                        argumentName = baseArgumentName;
                        int nextIndex = 2;
                        while (reservedNames.contains(argumentName) || visibleArgumentNames.contains(argumentName)) {
                            argumentName = baseArgumentName + "_" + nextIndex;
                            nextIndex++;
                        }
                    }
                    argumentToNameMap.put(argument, argumentName);
                    visibleArgumentNames.add(argumentName);
                }
            }
            
            // Call recursively on the targeting collectors.
            List<Collector> targetingCollectors = collectorTargetInfo.getTargetingCollectors(collector);
            for (final Collector targetingCollector : targetingCollectors) {               
                calculateArgumentNames(targetingCollector, visibleArgumentNames, reservedNames, collectorTargetInfo);
            }
        }

        /**
         * Get the collectors enclosing a given collector, including the collector itself.
         * @param collector the collector in question.
         * @return the enclosing collectors, including the collector itself, according to the target info calculated so far.
         */
        private Set<Collector> getEnclosingCollectors(Collector collector) {
            Set<Collector> enclosingCollectors = new HashSet<Collector>();
            for (Collector enclosingCollector = collector; enclosingCollector != null; 
                 enclosingCollector = collectorTargetInfo.getCollectorTarget(enclosingCollector)) {
            
                enclosingCollectors.add(enclosingCollector);
            }
            return enclosingCollectors;
        }
        
        /**
         * Replace all missing collectors targeted by the given set of root nodes.
         * Note: some targeting arguments may as a result be rooted in collectors which are not in the rootNodes set or missing collector map.
         * 
         * @param rootNodes 
         * @return map from collector to the collector with which it is replaced, for every collector
         *   targeted from but not appearing in rootNodes (and their target's targets, and so on..).
         */
        private static Map<Collector, SyntheticCollector> getExcludedCollectors(Set<? extends CompositionNode> rootNodes) {
            
            Map<Collector, SyntheticCollector> excludedCollectorReplacementMap = new HashMap<Collector, SyntheticCollector>();
            
            // Iterate over the root nodes, checking targets for any collectors.
            for (final CompositionNode rootNode : rootNodes) {
              
                if (rootNode instanceof Collector) {
                    // If the target isn't in the set of root nodes, call getCollector(), which should create it.
                    Collector targetCollector = ((Collector)rootNode).getTargetCollector();
                    if (targetCollector != null && !rootNodes.contains(targetCollector)) {
                        getCollector(targetCollector, rootNodes, excludedCollectorReplacementMap);
                    }
                }
            }
            
            return excludedCollectorReplacementMap;
        }

        /**
         * If generating for a group of root nodes, get a collector which can be used by this source generator.
         *   If the collector does not exist within the given set of root nodes, a synthetic one is generated and returned.
         *   The generated collectors will have their arguments targeted to themselves.
         * 
         * @param collector the collector to get.
         * @param rootNodes 
         * @param excludedCollectorReplacementMap this will be populated with any mappings.
         */
        private static Collector getCollector(Collector collector, Set<? extends CompositionNode> rootNodes, Map<Collector, SyntheticCollector> excludedCollectorReplacementMap) {
            
            // If the collector is in the set of root nodes, return the collector.
            if (rootNodes.contains(collector)) {
                return collector;
            }
            
            // If the collector has already had a replacement, return the replacement.
            SyntheticCollector replacementCollector = excludedCollectorReplacementMap.get(collector);
            if (replacementCollector != null) {
                return replacementCollector;
            }
            
            // Create a replacement.
            String unqualifiedName = collector.getUnqualifiedName();
            Collector originalTarget = collector.getTargetCollector();
            Collector targetCollector = originalTarget == null ? null : getCollector(originalTarget, rootNodes, excludedCollectorReplacementMap);
            
            replacementCollector = new SyntheticCollector(unqualifiedName, targetCollector, null, null);
            
            // Add to the map.
            excludedCollectorReplacementMap.put(collector, replacementCollector);
            
            return replacementCollector;
        }

        /**
         * Return the collector which is targeted by the given argument.
         *   This method simply asks all enclosing collectors whether it contains a given argument as a targeting argument.
         * @param compositionArgument the argument in question.
         * @return the collector targeted by this argument, or null if no target can be found.
         */
        private static Collector getArgumentCollectorTarget(CompositionArgument compositionArgument, 
                                                            Collector rootCollector, CollectorTargetInfo collectorTargetInfo) {
            
            for (Collector enclosingCollector = rootCollector; enclosingCollector != null; 
                 enclosingCollector = collectorTargetInfo.getCollectorTarget(enclosingCollector)) {
                
                if (enclosingCollector.getTargetArguments().contains(compositionArgument)) {
                    return enclosingCollector;
                }
            }
            
            return null;
        }

        /**
         * Attach appropriately-scoped collectors to the given composition node roots which are not collectors.
         * This method assumes that the root of the graph is the "real" root (ie. we aren't trying to consider as the root
         *   some collector which is used in another's definition).
         * 
         * @param uncollectedRoots  the nodes which are roots, but not collectors.
         * @param rootNodeTarget the target of the enclosed graph (immediately enclosed by the graph target).
         * @param collectorNames the names of all known collectors.  This set will be augmented with the names
         *   of any generated collectors.
         * @param excludedCollectorReplacementMap map from collector to its replacement, for any
         *   collectors which are excluded.
         * @return Map map from generated collector to its corresponding uncollected root.
         */
        private static Map<Collector, CompositionNode> attachCollectorsToUncollectedRoots(Set<CompositionNode> uncollectedRoots, Collector rootNodeTarget, 
                                                              Set<String> collectorNames, Map<Collector, SyntheticCollector> excludedCollectorReplacementMap) {

            // The map that will be returned..
            Map<Collector, CompositionNode> collectorToUncollectedRootMap = new HashMap<Collector, CompositionNode>();
            
            int nextAnonymousNameIndex = 1;
            
            // Iterate over the uncollected roots.
            for (final CompositionNode uncollectedRoot : uncollectedRoots) {               
                
                // Get the free arguments and collectors in the root's subtree..
                SubtreeNodeInfo subtreeNodeInfo = getSubtreeNodeInfo(uncollectedRoot);

                // Get the arguments for the anonymous root:
                List<CompositionArgument> freeArguments = subtreeNodeInfo.getFreeUnburntArguments();
                
                // Iterate over the collectors, finding the parent of the deepest one (ie. the scope which encloses the smallest scope).
                // This will be the maximal scope at which the root can be validly defined.
                Collector deepestCollectorParent = rootNodeTarget;

                // Ensure that the deepest collector parent is an original collector..
                // Note: iterates over the whole excludedCollectorReplacementMap, so potentially slow.
                if (deepestCollectorParent instanceof SyntheticCollector) {
                    for (final Map.Entry<Collector, SyntheticCollector> entry : excludedCollectorReplacementMap.entrySet()) {                       
                        final Collector excludedCollector = entry.getKey();
                        if (deepestCollectorParent == entry.getValue()) {
                            deepestCollectorParent = excludedCollector;
                            break;
                        }
                    }
                }
                
                for (final Collector collector : subtreeNodeInfo.getCollectorsUsed()) {                   

                    if (enclosesCollector(deepestCollectorParent, collector)) {
                        if (collector != deepestCollectorParent && collector.getTargetCollector() != deepestCollectorParent) {
                            deepestCollectorParent = collector.getTargetCollector();
                        }

                    } else if (!enclosesCollector(collector.getTargetCollector(), deepestCollectorParent)) {
                        CALCompiler.COMPILER_LOGGER.log(Level.SEVERE, "Inconsistent hierarchy detected for anonymous gem tree.");
                        return null;
                    }
                }
                
                // Replace with replacement if necessary.
                if (excludedCollectorReplacementMap.containsKey(deepestCollectorParent)) {
                    deepestCollectorParent = excludedCollectorReplacementMap.get(deepestCollectorParent);
                }
                
                // Get an anonymous name which doesn't conflict with any other names.
                String candidateCollectorName = "anonymousCollector";
                while (collectorNames.contains(candidateCollectorName)) {
                    candidateCollectorName = "anonymousCollector" + nextAnonymousNameIndex;
                    nextAnonymousNameIndex++;
                }
                collectorNames.add(candidateCollectorName);
                
                // Create the collector with that name.
                Collector collectorForRoot = new SyntheticCollector(candidateCollectorName, deepestCollectorParent, freeArguments, uncollectedRoot);
                
                // Add to the map.
                collectorToUncollectedRootMap.put(collectorForRoot, uncollectedRoot);
            }
            
            return collectorToUncollectedRootMap;
        }
        
        /**
         * Return whether one collector encloses another.
         *   This means that the first gem is an outer scope of the second.
         * @return whether the first collector encloses the second.
         *   Also returns true if both collectors are the same, or outerCollector is null.
         */
        private static boolean enclosesCollector(Collector outerCollector, Collector innerCollector) {
            if (outerCollector == null) {
                return true;
            }
            for (Collector collector = innerCollector; collector != null; collector = collector.getTargetCollector()) {
                if (collector == outerCollector) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Get the target of the graph, as calculated by this info object.
         * @return the graph target.
         */
        public Collector getGraphTarget() {
            return collectorTargetInfo.getGraphTarget();
        }
        
        /**
         * Get a collector's target, as calculated by this info object.
         * @param collector the collector in question.
         * @return the collector's target, according to this info object.
         */
        public Collector getCollectorTarget(Collector collector) {
            return collectorTargetInfo.getCollectorTarget(collector);
        }
        
        /**
         * Get the collectors targeting a given collector, as calculated by this info object.
         * @param collector the collector in question.
         * @return the collectors targeting a given collector, as calculated by this info object.
         */
        public List<Collector> getTargetingCollectors(Collector collector) {
            return collectorTargetInfo.getTargetingCollectors(collector);
        }

        /**
         * Get a collector's name, disambiguated in a manner appropriate for its subsequent use according to this info object.
         * @param collector the collector in question.
         * @return the collector name.
         */
        public String getCollectorName(Collector collector) {
            return collectorToNameMap.get(collector);
        }
        
        /**
         * Get the names of all the collectors known to this info object.
         * @return the names calculated for use by the collectors known to this info object.
         */
        public Set<String> getCollectorNames() {
            return new HashSet<String>(collectorToNameMap.values());
        }
        
        /**
         * Get the arguments which appear on a collector's definition, in the order in which they should appear.
         * @param collector the collector in question.
         * @return the arguments which appear in the collector's definition, in the order in which they should appear.
         *   If the collector is the top-level collector (ie. the nominated target), untargeted arguments will also be added to the list.
         */
        public List<CompositionArgument> getCollectorArguments(Collector collector) {
            
            // The arguments targeting the collector, in the order in which they should appear.
            List<CompositionArgument> targetArguments = cachedCollectorArgumentsMap.get(collector);
            
            if (targetArguments == null) {
                // Get the arguments calculated to be on this collector.
                List<CompositionArgument> unorderedArgumentsList = collectorToArgumentsMap.get(collector);
                if (unorderedArgumentsList == null) {
                    targetArguments = new ArrayList<CompositionArgument>();
                    
                } else {
                    // copy the list..
                    unorderedArgumentsList = new ArrayList<CompositionArgument>(unorderedArgumentsList);
                    
                    // Get the arguments which target this collector.
                    targetArguments = new ArrayList<CompositionArgument>(collector.getTargetArguments());
                    
                    // Keep all arguments targeting this collector, and also calculated to be on the collector.
                    targetArguments.retainAll(unorderedArgumentsList);
                    
                    // Add all arguments which are calculated to be on the collector, but not actually targeting the collector.
                    unorderedArgumentsList.removeAll(targetArguments);
                    targetArguments.addAll(unorderedArgumentsList);
                }
                
                // Add untargeted arguments if the collector is the root definition.
                if (getCollectorTarget(collector) == null) {
                    List<CompositionArgument> untargetedArguments = collectorToArgumentsMap.get(null);
                    if (untargetedArguments != null) {
                        targetArguments.addAll(untargetedArguments);
                    }
                }
                
                cachedCollectorArgumentsMap.put(collector, targetArguments);
            }
            
            return targetArguments;
        }
        
        /**
         * Get the arguments which are unused, if any.
         * @return the arguments which are unused, if any.
         */
        public List<CompositionArgument> getUnusedArguments() {
            return new ArrayList<CompositionArgument>(unusedArgumentList);
        }
        
        /**
         * Get the original root corresponding to a synthetic collector generated for that root during source generation.
         * @param collectedRoot the generated collector .
         * @return the original root.
         */
        CompositionNode getOriginalRoot(Collector collectedRoot) {
            return collectedRootToRootNodeMap.get(collectedRoot);
        }
        
        /**
         * Get the name which was calculated for the given argument.
         * @param argument the argument in question.
         * @return the name calculated for the given argument.
         */
        public String getArgumentName(CompositionArgument argument) {
            // Check for the case where it's a recursive emitter argument.
            CompositionArgument recursivelyReflectedArgument = recursiveEmitterArgumentToReflectedInputMap.get(argument);
            if (recursivelyReflectedArgument != null) {
                argument = recursivelyReflectedArgument;
            }
            
            return argumentToNameMap.get(argument);
        }
        
        /**
         * Return whether the given collector will be treated as a "naked" collector.
         * These are unconnected collectors with no targeting arguments.
         * Such collectors will have their definitions elided from the text (ie. they will not appear in the let block).
         * @param collector the collector in question.
         * @return whether the given collector will be treated as a "naked" collector.
         */
        public boolean isNakedCollector(Collector collector) {
            return inputToNakedCollectorMap != null && inputToNakedCollectorMap.containsValue(collector);
        }
        
        /**
         * @param collector the collector in question.  This is assumed to be a collector mapped by this info object.
         * @return whether this collector is the replacement for an excluded collector.
         */
        boolean isExcludedCollectorReplacement(Collector collector) {
            return excludedCollectorReplacementMap != null && excludedCollectorReplacementMap.containsValue(collector);
        }

        /**
         * @return map from recursive emitter arg to the arg which it reflects.
         */
        public Map<CompositionArgument, CompositionArgument> getRecursiveEmitterArgumentToReflectedInputMap() {
            return new HashMap<CompositionArgument, CompositionArgument>(recursiveEmitterArgumentToReflectedInputMap);
        }
    }

    /**
     * A simple class to encapsulate the results from getSubtreeNodeInfo().
     * @author Edward Lam
     */
    private static class SubtreeNodeInfo {
        private final List<CompositionArgument> freeArguments = new ArrayList<CompositionArgument>();
        
        private final Set<Collector> collectorsUsed = new HashSet<Collector>();
        
        private final Set<Emitter> emitterSet = new HashSet<Emitter>();
        
        /**
         * @return the collectors used by emitters in the subtree.
         */
        public Set<Collector> getCollectorsUsed() {
            return collectorsUsed;
        }

        /**
         * @return the free arguments in the subtree.
         */
        public List<CompositionArgument> getFreeUnburntArguments() {
            return freeArguments;
        }
        
        /**
         * @return the emitters in the subtree.
         */
        public Set<Emitter> getEmitters() {
            return emitterSet;
        }
    }
    
    /**
     * A Collector generated internally by this source generator.
     * @author Edward Lam
     */
    private static class SyntheticCollector implements Collector {

        /** The name of the collector. */
        private final String unqualifiedName;
        
        /** The arguments targeting this collector. */
        private final List<CompositionArgument> targetArguments;
        
        /** The argument to this collector. */
        private final SyntheticArgument argument;

        /** This collector's target collector*/
        private final Collector targetCollector;

        /**
         * An argument on a SyntheticCollector.
         * @author Edward Lam
         */
        private class SyntheticArgument implements CompositionNode.CompositionArgument {
            
            /** The composition node connected to this argument, if any.  Null if not connected. */
            private final CompositionNode connectedNode;
            
            /**
             * Constructor for a SyntheticArgument.
             * @param connectedNode the node connected to this argument, if any.  Null if not connected.
             */
            SyntheticArgument(CompositionNode connectedNode) {
                this.connectedNode = connectedNode;
            }

            /**
             * {@inheritDoc}
             */
            public CompositionNode getConnectedNode() {
                return connectedNode;
            }

            /**
             * {@inheritDoc}
             */
            public boolean isBurnt() {
                return false;
            }

            /**
             * {@inheritDoc}
             */
            public String getNameString() {
                return unqualifiedName;
            }
        }
        
        /**
         * Constructor for a SyntheticCollector.
         * @param unqualifiedName the name of the collector.
         * @param targetArguments the arguments targeting this collector.
         * @param connectedNode the composition node connected to the argument on this collector, or null if not connected.
         *   If null, the argument will be added to this collector's target arguments.
         */
        SyntheticCollector(String unqualifiedName, List<CompositionArgument> targetArguments, CompositionNode connectedNode) {
            this(unqualifiedName, null, targetArguments, connectedNode);
            if (connectedNode == null) {
                this.targetArguments.add(argument);
            }
        }

        /**
         * Constructor for a SyntheticCollector.
         * @param unqualifiedName the name of the collector.
         * @param targetCollector the target collector for this collector.
         * @param targetArguments the arguments targeting this collector.
         *   If null, the argument list will have this collector's target argument as its only argument.
         * @param connectedNode the composition node connected to the argument on this collector, or null if not connected.
         */
        SyntheticCollector(String unqualifiedName, Collector targetCollector, List<CompositionArgument> targetArguments, CompositionNode connectedNode) {
            this.unqualifiedName = unqualifiedName;
            this.targetCollector = targetCollector;
            this.argument = new SyntheticArgument(connectedNode);
            this.targetArguments = targetArguments == null ? Collections.<CompositionArgument>singletonList(argument) : new ArrayList<CompositionArgument>(targetArguments);
        }
        
        /**
         * {@inheritDoc}
         */
        public String getUnqualifiedName() {
            return unqualifiedName;
        }

        /**
         * {@inheritDoc}
         */
        public TypeExpr getDeclaredType() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public Collector getTargetCollector() {
            return targetCollector;
        }

        /**
         * {@inheritDoc}
         */
        public List<CompositionArgument> getTargetArguments() {
            return new ArrayList<CompositionArgument>(targetArguments);
        }

        /**
         * {@inheritDoc}
         */
        public int getNArguments() {
            return 1;
        }

        /**
         * {@inheritDoc}
         */
        public CompositionArgument getNodeArgument(int i) {
            if (i != 0) {
                throw new IndexOutOfBoundsException("Collectors have exactly one input.");
            }
            return argument;
        }
        
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Synthetic collector " + unqualifiedName + ". Target = " + targetCollector + ". Connected node = " + argument.connectedNode;
        }
    }
    
    /* This class is not intended to be instantiated. */
    private CALSourceGenerator() {
    }
    
    /**
     * Get the text of the function that is defined by the tree of CompositionNodes headed by rootNode.
     * @param functionName the name to use for the new function.
     *   This can be null if scRoot is a collector, in which case the collector's name will be used.
     * @param functionRoot the root of the Composition tree from which to generate the definition.
     * @param scope the scope of the function.
     * @return text of the resulting function.
     */
    public static String getFunctionText(String functionName, CompositionNode functionRoot, Scope scope) {

        return getFunctionSourceModel(functionName, functionRoot, scope).toSourceText();
    }
    
    /**
     * Get the source model of the function that is defined by the tree of
     * CompositionNodes headed by rootNode.
     * 
     * @param functionName
     *            the name to use for the new function. This can be null if
     *            functionRoot is a collector, in which case the collector's
     *            name will be used.
     * @param functionRoot
     *            the root of the Composition tree from which to generate the
     *            definition.
     * @param scope
     *            the scope of the function.
     * @return source model of the resulting function.
     */
    public static SourceModel.FunctionDefn.Algebraic getFunctionSourceModel(
        String functionName, CompositionNode functionRoot, Scope scope) {

        // Get as a collector root.
        Collector rootNode = getAsCollectorRoot(functionRoot, functionName);

        GraphInfo graphInfo = new GraphInfo(rootNode, functionName);
        SourceModel.Expr functionBody = getFunctionBodySourceModel(rootNode, graphInfo, null);

        // The name of the function is the collector name.
        functionName = graphInfo.getCollectorName(rootNode);

        List<CompositionArgument> args = graphInfo.getCollectorArguments(rootNode);
        SourceModel.Parameter[] freeParams = new SourceModel.Parameter[args.size()];
        int counter = 0;

        for (final CompositionArgument targetingArg : args) {            
            freeParams[counter++] = SourceModel.Parameter.make(graphInfo.getArgumentName(targetingArg), false);
        }

        return SourceModel.FunctionDefn.Algebraic.make(functionName, scope, freeParams, functionBody);
    }
    
    /**
     * Get the arguments in the order that they would appear if the given node were used as the source generation target.
     * @param rootNode the node in question
     * @return the arguments in the order that they would appear in the source.
     */
    public static CompositionArgument[] getFunctionArguments(CompositionNode rootNode) {
        // Get as a collector root.  This is necessary so that unbound arguments that don't have a target
        // set properly get picked up as arguments on the root
        Collector collectorRoot = getAsCollectorRoot(rootNode, "temp");

        // calculate the graph info, get the args for the node.
        GraphInfo graphInfo = new GraphInfo(collectorRoot, "temp");
        List<CompositionArgument> nodeArgList = graphInfo.getCollectorArguments(collectorRoot);
        
        // Convert to an array and return.
        CompositionArgument[] args = new CompositionArgument[nodeArgList.size()];
        return nodeArgList.toArray(args);
    }

    /**
     * Generates source for the tree of CompositionNodes headed by rootNode in a form which
     * is suitable for use in a code gem.
     * This will not include the function name, scope, arguments, or a trailing semi-colon.
     * @param rootNode root of the Composition tree.
     * @return the resulting CAL source.
     */
    public static String getSourceForCodeGem(CompositionNode rootNode) {
        // Note: prevent eta-reduction of the root gem..
        
        Collector collectorRoot = getAsCollectorRoot(rootNode, "tempCollector");
        SourceModel.Expr sourceBody = getFunctionBodySourceModel(collectorRoot, new GraphInfo(collectorRoot, "tempCollector"), null);
        
        return sourceBody.toSourceText();
    }

    /**
     * Invoke the other getCheckGraphSource()
     * 
     * ** FOR DEBUG PURPOSES
     * 
     * @param rootNodes (Set of CompositionNode)
     * @return the generated source
     */
    public static String getDebugCheckGraphSource(Set<? extends CompositionNode> rootNodes) {
        return getCheckGraphSource(rootNodes).getGraphFunction().toSourceText();
    }
        
    /**
     * This generates a let expression which generates all free argument and output types simultaneously when evaluated.
     * 
     * The expression generated is of the form:
     *   checkGraph root1_rootVar root2_rootVar ... rootn_rootVar unusedVar1 ... unusedVarn =
     *     let
     *      (transformed target definition);
     *     in
     *       0.0;
     * 
     * where the transformed target definition is the definition of the target of the composition node graph, transformed in the following ways:
     * 
     *   1) Anonymous collectors are attached to roots which are not collectors.  These collectors will have their targets set 
     *      to an appropriate scope, and unbound arguments on the composition tree will be targeted to this collector.
     *   2) Collectors which do not appear in the definition of the target are added anyway.
     *   3) The "in" part of any let definition is augmented with its own "let" definition to bind checkGraph arguments
     *      to the types of their corresponding roots.
     * 
     * eg. The graph defines the target as: collector1 collector2 y = collector2 + y;
     *     There are three roots:
     *       1) The target collector, collector1, connected to Prelude.add, which in turn is connected to a collector2 emitter as its first arg.
     *       2) A collector, collector2, used in the definition of collector1.
     *       3) A collector, collector3, not used in the definition of collector1.
     *       4) An unconnected Prelude.add gem.
     * This method generates:
     *   checkGraph collector1_rootVar collector2_rootVar collector3_rootVar anonymousCollector_rootVar collector3_arg =
     *     let
     *       collector1 y collector2_arg = 
     *         let
     *           collector2 = collector2_arg;
     *           collector3 = collector3_arg;
     *           anonymousCollector x y_2 = x + y_2;
     *         in
     *           let
     *             collector2_tempList = [collector2, collector2_rootVar];
     *             collector3_tempList = [collector3, collector3_rootVar];
     *             anonymousCollector_tempList = [anonymousCollector, anonymousCollector_rootVar];
     *           in
     *             collector2 + y;
     *     in
     *       let
     *         collector1_tempList = [collector1, collector1_rootVar];
     *       in
     *         0.0;
     *
     * Note: eta-reduction is NOT applied to the graph text.
     * eg. let foo x y = (foo2 x y); in ..   is NOT reduced to    let foo = foo2; in ..
     * 
     * @param rootNodes the root nodes of trees for which to generate the type checking text
     * @return the generated source for the sc that represents a typecheck-able representation of the graph.
     */
    static CheckGraphSource getCheckGraphSource(Set<? extends CompositionNode> rootNodes) {
        
        Map<String, Collector> rootTypeCollectorNameToCollectorMap = new LinkedHashMap<String, Collector>();
        GraphInfo graphInfo = new GraphInfo(rootNodes);
        Collector graphTarget = graphInfo.getGraphTarget();
        SourceModel.Expr graphTargetSourceModel = getFunctionBodySourceModel(graphTarget, graphInfo, rootTypeCollectorNameToCollectorMap);

        // The root->arguments map which is returned.
        Map<CompositionNode, List<CompositionArgument>> rootToArgumentsMap = new LinkedHashMap<CompositionNode, List<CompositionArgument>>();
        
        List<SourceModel.Parameter> parameters = new ArrayList<SourceModel.Parameter>();
                
        // The root arguments..
        for (final Map.Entry<String, Collector> entry : rootTypeCollectorNameToCollectorMap.entrySet()) {
            final String rootTypeCollectorName = entry.getKey();
            Collector correspondingCollector = entry.getValue();
            
            // Get the original root corresponding to the collected root.
            CompositionNode correspondingRoot = graphInfo.getOriginalRoot(correspondingCollector);
            
            // Get the arguments, add to the map (but only if it's not one of the synthetically created enclosing collectors..).
            if (!(correspondingRoot instanceof SyntheticCollector)) {
                List<CompositionArgument> collectorArguments = graphInfo.getCollectorArguments(correspondingCollector);
                rootToArgumentsMap.put(correspondingRoot, collectorArguments);
            }
            
            // Add the root type collector's name as an argument.
            parameters.add(SourceModel.Parameter.make(rootTypeCollectorName, false));
        }
        
        // The uncollected arguments get stuck on the outermost collector.
        for (final CompositionArgument unusedArgument : graphInfo.getCollectorArguments(graphTarget)) {            
            String uncollectedArgumentName = graphInfo.getArgumentName(unusedArgument);

            // Add the uncollected argument name.
            parameters.add(SourceModel.Parameter.make(uncollectedArgumentName, false));
        }
        
        // Add unused targeting arguments to the outermost collector
        for (final CompositionArgument unusedArgument : graphInfo.getUnusedArguments()) {
            
            String unusedArgumentName = graphInfo.getArgumentName(unusedArgument);

            // Add the unused targeting argument name.
            parameters.add(SourceModel.Parameter.make(unusedArgumentName, false));
        }
        
        // Create the check graph source model.
        SourceModel.FunctionDefn.Algebraic checkGraph =
            SourceModel.FunctionDefn.Algebraic.make(
                "checkGraph", Scope.PRIVATE,
                parameters.toArray(new SourceModel.Parameter[0]),
                graphTargetSourceModel);
        
        return new CheckGraphSource(
            checkGraph, rootToArgumentsMap, graphInfo.getUnusedArguments(),
            graphInfo.getRecursiveEmitterArgumentToReflectedInputMap());
    }    
    
    /**
     * If the given collector has a declared type, return its type signature.
     * 
     * @param collector
     *            the collector in question.
     * @return the corresponding type signature, or null if the collector's type
     *         is not declared.
     */
    private static SourceModel.TypeSignature maybeGetTypeSignatureSourceModel(Collector collector) {

        TypeExpr declaredType = collector.getDeclaredType();
        if (declaredType == null) {
            return null;
        }

        return declaredType.toSourceModel();
    }

    /**
     * Generate the function body used for generating the type of the graph
     * rooted at the given collector. See getCheckGraphSource() for details.
     * 
     * @param sourceCollector
     *            the collector for which the text should be generated.
     * @param graphInfo
     *            information about the CompositionNode graph
     * @param rootTypeCollectorNameToCollectorMap
     *            Map to collector from the name of the root
     *            type variable used to generate its type. This will be
     *            modified.
     * @return the resulting source model.
     */
    private static SourceModel.Expr getFunctionBodySourceModel(
        Collector sourceCollector, GraphInfo graphInfo, Map<String, Collector> rootTypeCollectorNameToCollectorMap) {
        
        // Get the collectors targeting the source collector
        List<Collector> targetingCollectors = graphInfo.getTargetingCollectors(sourceCollector);
        
        // The "let" definitions.
        List<SourceModel.LocalDefn.Function> letDefinition = new ArrayList<SourceModel.LocalDefn.Function>();
        Map<String, Collector> targetingCollectorNameToCollectorMap = new LinkedHashMap<String, Collector>();
        
        for (final Collector targetingCollector : targetingCollectors) {
                      
            // Skip, if it's a naked collector.
            if (graphInfo.isNakedCollector(targetingCollector)) {
                continue;
            }   
            
            String targetingCollectorName = graphInfo.getCollectorName(targetingCollector);
            
            // Add the declared type signature if any.
            SourceModel.TypeSignature typeSignatureSourceModel = maybeGetTypeSignatureSourceModel(targetingCollector);
            if (typeSignatureSourceModel != null) {
                letDefinition.add(SourceModel.LocalDefn.Function.TypeDeclaration.make(targetingCollectorName, typeSignatureSourceModel));
            }
            
            // Get the arguments.
            List<CompositionArgument> sourceCollectorArguments = graphInfo.getCollectorArguments(targetingCollector);
            SourceModel.Parameter[] params = new SourceModel.Parameter[sourceCollectorArguments.size()];
            int counter = 0;
            
            for (final CompositionArgument compositionArgument : sourceCollectorArguments) {                
                params[counter++] = SourceModel.Parameter.make(graphInfo.getArgumentName(compositionArgument), false);
            }
            
            // Get the body of the definition.
            SourceModel.Expr body = getFunctionBodySourceModel(targetingCollector, graphInfo, rootTypeCollectorNameToCollectorMap);
            
            // Add the collector's definition.
            letDefinition.add(SourceModel.LocalDefn.Function.Definition.make(targetingCollectorName, params, body));
            
            targetingCollectorNameToCollectorMap.put(targetingCollectorName, targetingCollector);
        }
        
        // The source root definition.. (the "in" part).
        SourceModel.Expr nakedInPart = getSubtreeExpressionSourceModel(sourceCollector, graphInfo, false);
        
        SourceModel.Expr inDefinition;
        if (rootTypeCollectorNameToCollectorMap == null) {
            // Just the in part.
            inDefinition = nakedInPart;
            
        } else {
            // Augment with an inner "let".
            // Create a list to hold the let definitions for the "in" part, which are used for type checking.
            List<SourceModel.LocalDefn.Function.Definition> inLetDefinitions = new ArrayList<SourceModel.LocalDefn.Function.Definition>();
            
            // Calculate the names which shouldn't be re-used for generated inner let definitions.
            Set<String> reservedNames = new HashSet<String>();

            // First calculate the collectors which are in scope, and add their argument names.
            Set<Collector> enclosingCollectors = graphInfo.getEnclosingCollectors(sourceCollector);
            Set<Collector> collectorsInScope = new HashSet<Collector>(enclosingCollectors);
            for (final Collector enclosingCollector : enclosingCollectors) {                
                collectorsInScope.addAll(graphInfo.getTargetingCollectors(enclosingCollector));

                // Add the arguments.
                for (final CompositionArgument visibleArgument : graphInfo.getCollectorArguments(enclosingCollector)) {                    
                    reservedNames.add(graphInfo.getArgumentName(visibleArgument));
                }
            }

            // Now add the names of all collectors targeting the enclosing collectors.
            for (final Collector visibleCollector : collectorsInScope) {               
                reservedNames.add(graphInfo.getCollectorName(visibleCollector));
            }
            
            // Iterate over the targeting collectors, generating the inner let definitions used for type checking.
            for (final Map.Entry<String, Collector> entry : targetingCollectorNameToCollectorMap.entrySet()) {
                
                final String targetingCollectorName = entry.getKey();
                Collector targetingCollector = entry.getValue();
                
                // Skip if it's an excluded collector.
                if (graphInfo.isExcludedCollectorReplacement(targetingCollector)) {
                    continue;
                }
                
                // Generate a name for the temporary list collector.
                String baseListCollectorName = targetingCollectorName + "_tempList";
                String listCollectorName = baseListCollectorName;
                int nextIndex = 2;
                while (reservedNames.contains(listCollectorName)) {
                    listCollectorName = baseListCollectorName + nextIndex;
                    nextIndex++;
                }
                reservedNames.add(listCollectorName);
                
                // Generate a name for the root type variable
                String baseRootVarName = targetingCollectorName + "_rootVar";
                String rootVarName = baseRootVarName;
                nextIndex = 2;
                while (reservedNames.contains(rootVarName)) {
                    
                    rootVarName = baseRootVarName + nextIndex;
                    nextIndex++;
                }
                rootTypeCollectorNameToCollectorMap.put(rootVarName, targetingCollector);
                reservedNames.add(rootVarName);
                
                // Now add the source model for the temporary list collector.
                // This will be of the form: listCollector = [targetingCollector, rootVarName];
                
                SourceModel.Expr.List listCollectorExpr =
                    SourceModel.Expr.List.make(new SourceModel.Expr[]{
                        SourceModel.Expr.Var.makeUnqualified(targetingCollectorName),
                        SourceModel.Expr.Var.makeUnqualified(rootVarName) });
                
                inLetDefinitions.add(SourceModel.LocalDefn.Function.Definition.make(listCollectorName, null, listCollectorExpr));
            }
            
            if (inLetDefinitions.size() == 0) {
                inDefinition = nakedInPart;               
            } else {
                inDefinition = SourceModel.Expr.Let.make(inLetDefinitions.toArray(new SourceModel.LocalDefn.Function[0]), nakedInPart);
            }
        }

        if (letDefinition.size() == 0) {
            // Just return the "in" part.
            return inDefinition;
            
        } else {
            // Construct the "let .. in .."
            return SourceModel.Expr.Let.make(letDefinition.toArray(new SourceModel.LocalDefn.Function[0]), inDefinition);
        }
    }
    
    /**
     * If the given node is a collector, return it.
     * Otherwise, return a collector node whose argument is connected to the given node.
     * @param node the node in question.  Null to return an unconnected collector root.
     * @param defaultSCName the default name for the new function.  
     *   This name will be used unless rootNode is a collector, in which case the collector's name will be used.
     * @return the corresponding collector.
     */
    private static Collector getAsCollectorRoot(CompositionNode node, String defaultSCName) {
        
        if (node instanceof Collector) {
            return (Collector)node;
        } else {
            return new SyntheticCollector(defaultSCName, Collections.<CompositionArgument>emptyList(), node);
        }
    }
    
    /**
     * A helper method to find, descending from a given gem:
     *  1) all free arguments in the gem's subtree.
     *  2) all collectors for emitters used in that subtree.
     * @param subtreeRoot the root for the subtree in question.
     * @return the free arguments and collectors for emitters in that subtree.
     */
    private static SubtreeNodeInfo getSubtreeNodeInfo(CompositionNode subtreeRoot) {

        SubtreeNodeInfo resultInfo = new SubtreeNodeInfo();
        
        if (subtreeRoot instanceof CompositionNode.Emitter) {
            resultInfo.getCollectorsUsed().add(((CompositionNode.Emitter)subtreeRoot).getCollectorNode());
        }
        
        for (int i = 0, nArguments = subtreeRoot.getNArguments(); i < nArguments; i++) {
            CompositionNode.CompositionArgument argument = subtreeRoot.getNodeArgument(i);
            CompositionNode connectedNode = argument.getConnectedNode();
            
            // Check the connected node.
            if (connectedNode == null) {
                // Unconnected node.  Add to the list if it's not burnt.
                if (!argument.isBurnt()) {
                    resultInfo.getFreeUnburntArguments().add(argument);
                    
                    if (subtreeRoot instanceof CompositionNode.Emitter) {
                        resultInfo.getEmitters().add((Emitter)subtreeRoot);
                    }
                }
        
            } else {
                // Connected node.  Call recursively and combine the result.
                SubtreeNodeInfo connectedSubtreeNodeInfo = getSubtreeNodeInfo(connectedNode);
                resultInfo.getCollectorsUsed().addAll(connectedSubtreeNodeInfo.getCollectorsUsed());
                resultInfo.getFreeUnburntArguments().addAll(connectedSubtreeNodeInfo.getFreeUnburntArguments());
                resultInfo.getEmitters().addAll(connectedSubtreeNodeInfo.getEmitters());
            }
        }

        return resultInfo;
    }
    
    /**
     * Get SubtreeNodeInfo for a given CompositionNode, and all descendant CompositionNode subtrees..
     * @param subtreeRoot the node from which to start.
     * @return a SubtreeNodeInfo object which contains info for CompositionNode subtrees descendant from the given node.
     */
    private static SubtreeNodeInfo getDescendantSubtreeNodeInfo(CompositionNode subtreeRoot) {
        
        SubtreeNodeInfo resultInfo = new SubtreeNodeInfo();     // the info object to return.
        Set<CompositionNode> traversedRoots = new HashSet<CompositionNode>();                     // (Set of CompositionNode) the set of roots which have been traversed.
        
        Set<CompositionNode> newRoots = Collections.singleton(subtreeRoot);
        do {
            Set<CompositionNode> newRootsUpdated = new HashSet<CompositionNode>();
            
            // Iterate over the new collectors, building up the result info object, and getting an updated collection of new collectors.
            for (final CompositionNode newRoot : newRoots) {               
                if (!traversedRoots.contains(newRoot)) {
                    traversedRoots.add(newRoot);
                    
                    SubtreeNodeInfo subtreeNodeInfo = getSubtreeNodeInfo(newRoot);
                    resultInfo.getEmitters().addAll(subtreeNodeInfo.getEmitters());
                    resultInfo.getFreeUnburntArguments().addAll(subtreeNodeInfo.getFreeUnburntArguments());
                    
                    Set<Collector> collectorsUsed = subtreeNodeInfo.getCollectorsUsed();
                    resultInfo.getCollectorsUsed().addAll(collectorsUsed);
                    newRootsUpdated.addAll(collectorsUsed);
                }
            }
            
            newRootsUpdated.removeAll(traversedRoots);
            newRoots = newRootsUpdated;
        
        } while (!newRoots.isEmpty());
        
        return resultInfo;
    }

    /**
     * Get the expression for the subtree rooted at the given node.
     * 
     * @param node
     *            the node for which the expression should be obtained.
     * @param graphInfo
     *            the calculated graph info.
     * @param isEtaReductionRoot
     *            Whether this is the root node at which to apply eta-reduction
     *            on free params. This will cause final free params on the
     *            reduction root to be dropped from both the returned leaf list
     *            and the returned expression. Note that in the case of let
     *            definitions, the eta-reduction root applies to the connected
     *            node.
     * @return the result of the analysis.
     */
    private static SourceModel.Expr getSubtreeExpressionSourceModel(
        CompositionNode node, GraphInfo graphInfo, boolean isEtaReductionRoot) {

        if (node instanceof CompositionNode.Value) {
            return getSubtreeExpressionSourceModel((CompositionNode.Value)node).getExpression();
        }
        if (node instanceof CompositionNode.Application) {
            return getSubtreeExpressionSourceModel((CompositionNode.Application)node, graphInfo,
                                                   isEtaReductionRoot);
        }
        if (node instanceof Collector) {
            return getSubtreeExpressionSourceModel((Collector)node, graphInfo, isEtaReductionRoot);
        }
        if (node instanceof CompositionNode.RecordFieldSelectionNode) {
            return getSubtreeExpressionSourceModel((CompositionNode.RecordFieldSelectionNode)node, graphInfo);
        }
        if (node instanceof CompositionNode.RecordCreationNode){
            return getSubtreeExpressionSourceModel((CompositionNode.RecordCreationNode)node, graphInfo);
        }

        // something bad happened
        throw new IllegalArgumentException(
            "Can't handle this type of node: " + (node == null ? null : node.getClass()));
    }

    /**
     * Get the expression for the subtree rooted at the given value node.
     * 
     * @param node
     *            the node for which the expression should be obtained.
     * @return the resulting expression.
     */
    private static SourceModelGenerationResult getSubtreeExpressionSourceModel(CompositionNode.Value node) {
        // This node directly represents a value
        return new SourceModelGenerationResult(
            node.getSourceModel(), Collections.<Collector>emptySet(), Collections.<Collector>emptySet());
    }   

    /**
     * Get the source model of the expression that is defined by the tree of
     * CompositionNodes headed by rootNode.
     * 
     * @param rootNode
     *            root of the definition.
     * @param graphInfo
     *            the calculated graph info.
     * @param isEtaReductionRoot
     *            Whether this is the root node at which to apply eta-reduction
     *            on free params. This will cause final free params on the
     *            reduction root to be dropped from both the returned leaf list
     *            and the returned expression. Note that in the case of let
     *            definitions, the eta-reduction root applies to the connected
     *            node.
     * @return the result of traversing the roots defined by the aggregation
     *         rooted at rootNode..
     */
    private static SourceModel.Expr getSubtreeExpressionSourceModel(
        Collector rootNode, GraphInfo graphInfo, boolean isEtaReductionRoot) {

        CompositionNode connectedNode = rootNode.getNodeArgument(0).getConnectedNode();

        if (connectedNode == null) {
            // Not connected - equivalent to a free argument.
            // The equivalent subtree expression is just the name of the argument.
            return SourceModel.Expr.Var.makeUnqualified(graphInfo.getArgumentName(rootNode.getNodeArgument(0)));

        } else {
            // Get the result of analyzing the expression for the subtree rooted
            // at this node.
            return getSubtreeExpressionSourceModel(connectedNode, graphInfo, isEtaReductionRoot);
        }
    }    

    /**
     * Gets the source model for the subtree at the given Extractor node.
     * 
     * @param node Extractor node
     * @param graphInfo the calculated graph info
     * @return the resulting expression
     */
    private static SourceModel.Expr getSubtreeExpressionSourceModel(CompositionNode.RecordFieldSelectionNode node, GraphInfo graphInfo) {
        // RecordFieldSelectionNode has only one node argument
        Pair<List<SourceModel.Expr>, List<String>> argExprAndBurntArgLists = getArgExprsAndBurntArgs(node, graphInfo);

        SourceModel.Expr expression = SourceModel.Expr.SelectRecordField.make(argExprAndBurntArgLists.fst().get(0), SourceModel.Name.Field.make(node.getFieldName()));        
        return getMaybeLambdaSourceModel(expression, argExprAndBurntArgLists.snd());
    }
    
    
    /**
     * Gets the source model for the subtree at the given Extractor node.
     * 
     * @param node Extractor node
     * @param graphInfo the calculated graph info
     * @return the resulting expression
     */    
    private static SourceModel.Expr getSubtreeExpressionSourceModel(CompositionNode.RecordCreationNode node, GraphInfo graphInfo) {
        
        Pair<List<SourceModel.Expr>, List<String>> argExprAndBurntArgLists = getArgExprsAndBurntArgs(node, graphInfo);
        List<SourceModel.Expr> argExpressionList = argExprAndBurntArgLists.fst();
        
        // Create the record fields
        int numArgExprs = argExpressionList.size();
        FieldModification[] fieldMods = new FieldModification[numArgExprs];
        Iterator<SourceModel.Expr> it = argExpressionList.listIterator();
        for (int i = 0; i < numArgExprs; i++) {
            SourceModel.Expr connectedExpr = it.next();
            FieldModification fieldMod = FieldModification.Extension.make(SourceModel.Name.Field.make(node.getFieldName(i)), connectedExpr);
            fieldMods[i] = fieldMod;
        }
        
        // Create the record
        SourceModel.Expr expression = SourceModel.Expr.Record.make(null, fieldMods);
        
        return getMaybeLambdaSourceModel(expression, argExprAndBurntArgLists.snd());
    }

    
    /**
     * Get the expression for the subtree rooted at the given application node.
     * 
     * There are three readability/efficiency special cases which impact the complexity of this method:
     * 1) Operator textual pseudonyms are replaced by their operator form, and infixed.
     *    eg. Prelude.add x y    becomes    x + y
     * 2) Eta-reduction for final argument burnings.
     *    eg. \w x y z -> foo w (bar x) y z   becomes    \w x -> foo w (bar x)
     * 3) Eta-reduction for the final free arguments on the root node of the tree for which an sc is obtained.
     *    eg. __runTarget w x y z = foo w (bar x) y z    becomes    __runTarget w x = foo w (bar x)
     *
     * TODOEL: #3 does not completely follow contract (since it removes leaves
     * from the result), and should probably instead be applied via source
     * transform on whatever comes out at the end (using the parser). Such a
     * facility could be more generally used (eg. when internalizing CAL code as
     * a runtime optimization), and would allow us to remove the extra boolean
     * argument.
     * 
     * @param node
     *            the node for which the expression should be obtained.
     * @param graphInfo
     *            the calculated graph info.
     * @param isEtaReductionRoot
     *            Whether this is the root node at which to apply eta-reduction
     *            on free params. This will cause final free params on the
     *            reduction root to be dropped from both the returned leaf list
     *            and the returned expression. Note that in the case of let
     *            definitions, the eta-reduction root applies to the connected
     *            node.
     * @return the resulting expression.
     */
    private static SourceModel.Expr getSubtreeExpressionSourceModel(CompositionNode.Application node, GraphInfo graphInfo, boolean isEtaReductionRoot) {

        // The operator for the applyable text if there is an operator.
        String operatorName = null;          
        
        // The number of arguments on this node.
        int nArguments = node.getNArguments();
        
        // Get the applyable text - the text of the "thing" which can be applied to arguments.
        SourceModel.Expr applyable;
        
        // If the node is a lambda, then store its code qualification map.
        // Otherwise, this value is set to null.
        CodeQualificationMap codeQualificationMap = null;
        
        if (node instanceof CompositionNode.Lambda) {

            // This node contributes code directly
            applyable = SourceModelUtilities.TextParsing.parseExprIntoSourceModel(((CompositionNode.Lambda) node).getLambda());
            codeQualificationMap = ((CompositionNode.Lambda)node).getQualificationMap();

        } else if (node instanceof CompositionNode.GemEntityNode) {

            // This node invokes some function
            QualifiedName functionName = ((CompositionNode.GemEntityNode) node).getName();
            applyable = SourceModel.Expr.makeGemCall(functionName);

            // See if this is an alias for an operator.
            operatorName = OperatorInfo.getOperatorName(functionName);
            
            //todoBI in the future we can handle Prelude.tuple2, Prelude.tuple3, ... by converting to operator form
            //we used to do this for the Tuple2, Tuple3, etc data constructors, but they no longer exist.
            // See if this is an alias for a tuple data constructor.
//            tupleDimension = TypeExpr.getTupleDimension(functionName);

        } else if (node instanceof CompositionNode.Emitter) {

            // This node invokes a local function
            Collector collector = ((CompositionNode.Emitter)node).getCollectorNode();
            applyable = SourceModel.Expr.Var.makeUnqualified(graphInfo.getCollectorName(collector));
            
        } else {
            // something bad happened
            throw new IllegalArgumentException("Can't handle this type of node: " + node.getClass());
        }

        // Iterate through the arguments of this node and get the expressions for the connected subtrees.
      
        Pair<List<SourceModel.Expr>, List<String>> argExprAndBurntArgLists = getArgExprsAndBurntArgs(node, graphInfo);
        List<SourceModel.Expr> argExpressionList = argExprAndBurntArgLists.fst();
        List<String> burntArgNames = argExprAndBurntArgLists.snd();
        
        // If we are type checking a composition node graph, collectors will have all targeting arguments as arguments, even those 
        //   which would normally be unused in its definition.  However, emitters will still only have a number of inputs equal to the
        //   number of used arguments.  Therefore, we have extra arguments if an emitter is used in this situation.

        if (node instanceof CompositionNode.Emitter) {
            
            CompositionNode.Collector emitterCollector = ((CompositionNode.Emitter)node).getCollectorNode();
            
            // Get the number of arguments on the collector as calculated by the source generator.
            List<CompositionArgument> collectorArguments = graphInfo.getCollectorArguments(emitterCollector);
            
            // Calculate the number of extra arguments by subtracting the number of arguments on the emitter.
            // This number should not be different if not generating source for type checking a composition node graph.
            int nExtraArgs = collectorArguments.size() - node.getNArguments();
            
            // Add a call to the error function for each extra arg.
            for (int i = 0; i < nExtraArgs; i++) {
                argExpressionList.add(EXTRA_ARG_ERROR_CALL_EXPR);
            }
        }
        
        // Apply eta-reduction for final argument burnings on this node.
        // eg. \w x y z -> foo w (bar x) y z   becomes    \w x -> foo w (bar x)
        if (!(node instanceof CompositionNode.Lambda)) {
            while (!burntArgNames.isEmpty()) {
                String lastBurntArg = burntArgNames.get(burntArgNames.size() - 1);
                SourceModel.Expr lastArgExpression = argExpressionList.get(argExpressionList.size() - 1);
                
                if (lastBurntArg.equals(lastArgExpression.toSourceText())) {
                    // Final arg burning.  Shrink the lists of burnt arg names and arg expressions.
                    burntArgNames = burntArgNames.subList(0, burntArgNames.size() - 1);
                    argExpressionList = argExpressionList.subList(0, argExpressionList.size() - 1);
                    
                } else {
                    // Not a final arg burning.  None of the rest of the args can be final arg burnings either.
                    break;
                }
            }
        }

        // Apply eta-reduction for final free unburnt params on the graph if this is the eta-reduction root
        if (isEtaReductionRoot) {
            // TODOEL
//
//            for (int i = nArguments - 1; i > -1; i--) {
//                if (nodeArgument.getConnectedNode() == null && !nodeArgument.isBurnt()) {
//                    // Final free unburnt root param.  Shrink the lists of leaves and the arg expressions by 1.
//                    leaves = leaves.subList(0, leaves.size() - 1);
//                    argExpressionList = argExpressionList.subList(0, argExpressionList.size() - 1);
//
//                } else {
//                    // Not a final free unburnt root param.  None of the rest of the args can be final free root param either.
//                    break;
//                }
//            }
        }


        // Attempt to translate the subexpression into an operator expression.
        SourceModel.Expr operatorExpression;
        
        if (operatorName != null) {
            // the call to translateToOperatorExpression would return null if
            // the expression cannot in fact be translated into an operator
            // expression.
            operatorExpression = getOperatorExpressionSourceModel(operatorName, nArguments, argExpressionList);
        } else {
            operatorExpression = null;
        }

        // Create the subexpression text.  
        SourceModel.Expr subExpression;
        
        // If the subExpression can be translated as an operator expression, use the operator expression 
        if (operatorExpression != null) {
            subExpression = operatorExpression;
            
        } else {
            int nArgExpressions = argExpressionList.size();
            if (nArgExpressions == 0) {
                subExpression = applyable;
            } else {
                SourceModel.Expr[] appExprs = new SourceModel.Expr[nArgExpressions + 1];
                appExprs[0] = applyable;
                for (int i = 0; i < nArgExpressions; i++) {
                    appExprs[i+1] = argExpressionList.get(i);
                }
                
                subExpression = SourceModel.Expr.Application.make(appExprs);
            }
        }

        if (codeQualificationMap != null) {
            subExpression = removeRedundantLambda(subExpression, codeQualificationMap);
        }
        
        // Generate lambda for any burnt arguments.
        SourceModel.Expr expression = getMaybeLambdaSourceModel(subExpression, burntArgNames);

        return expression;
    }

    /**
     * Iterate through the arguments of the node and get the expressions for the connected subtrees.
     * @param node the current node which its arguments are being iterated
     * @param graphInfo the calculated graph info
     * @return a list of expressions and a list of burnt arguments
     */
    private static Pair<List<SourceModel.Expr>, List<String>> getArgExprsAndBurntArgs(CompositionNode node, GraphInfo graphInfo) {
        
        List<String> burntArgNames = new ArrayList<String>();
        List<SourceModel.Expr> argExpressionList = new ArrayList<SourceModel.Expr>();
        
        for(int i = 0, nArgs = node.getNArguments(); i < nArgs; i++){
    
            CompositionNode.CompositionArgument nodeArgument = node.getNodeArgument(i);
            CompositionNode connectedNode = nodeArgument.getConnectedNode();
    
            if (connectedNode != null) {
                // argument is bound
                argExpressionList.add(getSubtreeExpressionSourceModel(connectedNode, graphInfo, false));
            } else {
                // argument is free
                String argName = nodeArgument.isBurnt() ? nodeArgument.getNameString() : graphInfo.getArgumentName(nodeArgument);
                argExpressionList.add(SourceModel.Expr.Var.makeUnqualified(argName));
    
                // If the argument is burnt, add its name to the list of burnt arg names
                if (nodeArgument.isBurnt()) {
                    burntArgNames.add(argName);
                } 
            }
        }
        
        return new Pair<List<SourceModel.Expr>, List<String>>(argExpressionList, burntArgNames);
    }

    /**
     * Translate a function application expression into an operator expression,
     * if it is possible.
     * 
     * @param operatorName
     *            the name of the operator.
     * @param nArguments
     *            the number of formal parameters of the function to be applied.
     * @param argExpressionList
     *            the list of actual arguments to the function application.
     * @return a SourceModel.Expr instance representing the operator expression
     *         if the input is translatable as one, otherwise null.
     */
    private static SourceModel.Expr getOperatorExpressionSourceModel(String operatorName, int nArguments, List<SourceModel.Expr> argExpressionList) {
        
        int nArgExpressions = argExpressionList.size();
        
        if (nArguments == 0 && nArgExpressions == 0) {
            // zero argument operator.
            return getNullaryOpSourceModel(operatorName);
            
        } else if (nArguments == 2 && nArgExpressions == 2) {
            // two argument operator.
            return getBinaryExprSourceModel(
                operatorName, argExpressionList.get(0), argExpressionList.get(1));
            
        } else if (operatorName.equals("$") && nArguments == 2 && nArgExpressions == 1) {
            // apply operator - one argument: just return the argument, since (Prelude.apply x) == x
            return argExpressionList.get(0);
            
        } else if (operatorName.equals("#") && nArguments == 3) {
            if (nArgExpressions == 2) {
                // compose operator - two arguments: just return (firstArg # secondArg)
                return SourceModel.Expr.BinaryOp.Compose.make(argExpressionList.get(0), argExpressionList.get(1));
                
            } else if (nArgExpressions == 3) {
                // compose operator - three arguments: just return ((firstArg # secondArg) thirdArg)
                return SourceModel.Expr.Application.make(
                    new SourceModel.Expr[] {
                        SourceModel.Expr.BinaryOp.Compose.make(argExpressionList.get(0), argExpressionList.get(1)),
                        argExpressionList.get(2) });
            }
        }
        
        // the expression is not translatable to an operator expression, so return null.
        return null;
    }
    
    /**
     * Analyzes the defining expression of a lambda expression, and determine
     * which occurences of identifiers within the expression refer to top-level
     * functions, which refer to parameters, and which refer to locally defined
     * values.
     * <p>
     * 
     * This functionality is used in performing argument substitution for
     * lambdas generated for code gems. In cases where a parameter of a lambda
     * differs lexically from that of the argument substituting for it, it is
     * necessary to scan through the defining expression of the lambda to
     * identify which variables refer to the about-to-be-renamed parameters.
     * Also, it is possible that the arguments to be substituted in collide with
     * unqualified references to top level functions. Therefore such references
     * will also need to be identified in the scan, so that they may be
     * disambiguated lated on. Finally, name bindings that are local to the
     * defining expression of the lambda need to be identified, because
     * they may need to be renamed so that they would not collide with the
     * arguments substitution.
     * <p>
     * 
     * @author Joseph Wong
     */
    private static final class LambdaDefiningExprScopeAnalyzer extends SourceModelTraverser<LambdaDefiningExprScopeAnalyzer.Scope, Void> {
        
        /**
         * Represents a scope for name bindings. Each scope, except for the
         * outermost scope, has a parent scope.
         * 
         * @author Joseph Wong
         */
        private static final class Scope {
            /** The parent scope of this scope. */
            private final Scope parent;

            /** The set of names bound in this scope. */
            private final Set/*String*/<String> boundNames;
            
            /**
             * Creates a new scope with the specified parent.
             * 
             * @param parentScope
             *            the parent scope.
             */
            private Scope(Scope parentScope) {
                parent = parentScope;
                boundNames = new HashSet<String>();
            }
            
            /**
             * Creates a new outermost scope, initialized with a list of
             * parameters bound in this scope.
             * 
             * @param params
             */
            private Scope(SourceModel.Parameter[] params) {
                this((Scope)null);
                
                if (params != null) {
                    for (final SourceModel.Parameter param : params) {
                        boundNames.add(param.getName());
                    }
                }                    
            }
            
            /**
             * Using this scope as the parent, create a new scope and
             * initialized it with a list of function names bound within the
             * scope.
             * 
             * @param defns
             *            the local definitions bound in the new scope.
             * @return the new inner scope.
             */
            private Scope newInnerScope(SourceModel.LocalDefn[] defns) {
                final Scope result = new Scope(this);
                
                if (defns != null) {
                    
                    final LocalBindingsProcessor<Object, Void> localBindingsProcessor = new LocalBindingsProcessor<Object, Void> () {
                        @Override
                        void processLocalDefinitionBinding(final String name, final SourceModel.SourceElement localDefinition, final Object arg) {
                            result.boundNames.add(name);
                        }
                    };
                    
                    for (final SourceModel.LocalDefn defn : defns) {
                        defn.accept(localBindingsProcessor, null);
                    }
                }
                
                return result;
            }
            
            /**
             * Using this scope as the parent, create a new scope and
             * initialized it with a list of parameters bound within the
             * scope.
             * 
             * @param params
             *            the parameters bound in the new scope.
             * @return the new inner scope.
             */
            private Scope newInnerScope(SourceModel.Parameter[] params) {
                Scope result = new Scope(this);
                
                if (params != null) {
                    for (final SourceModel.Parameter param : params) {
                        result.boundNames.add(param.getName());
                    }
                }
                
                return result;
            }

            /**
             * Using this scope as the parent, create a new scope and
             * initialized it with a list of case expression patterns bound
             * within the scope.
             * 
             * @param patterns
             *            the case expression patterns bound in the new
             *            scope.
             * @return the new inner scope.
             */
            private Scope newInnerScope(SourceModel.Pattern[] patterns) {
                Scope result = new Scope(this);
                
                if (patterns != null) {
                    for (final SourceModel.Pattern pattern : patterns) {
                        if (pattern instanceof SourceModel.Pattern.Var) {
                            
                            SourceModel.Pattern.Var var =
                                (SourceModel.Pattern.Var)pattern;
                            
                            result.boundNames.add(var.getName());
                        }
                    }
                }
                
                return result;
            }
            
            /**
             * Using this scope as the parent, create a new scope and
             * initialized it with a list of field patterns bound within the
             * scope.
             * 
             * @param fieldPatterns
             *            the field patterns bound in the new scope.
             * @return the new inner scope.
             */
            private Scope newInnerScope(SourceModel.FieldPattern[] fieldPatterns) {
                Scope result = new Scope(this);
                
                if (fieldPatterns != null) {
                    for (final SourceModel.FieldPattern fieldPattern : fieldPatterns) {
                        
                        SourceModel.Pattern pattern = fieldPattern.getPattern();
                        
                        if (pattern != null
                            && pattern instanceof SourceModel.Pattern.Var) {
                            
                            SourceModel.Pattern.Var var =
                                (SourceModel.Pattern.Var)pattern;
                            
                            result.boundNames.add(var.getName());
                            
                        } else {
                            FieldName fieldName = fieldPattern.getFieldName().getName();
                            
                            if (fieldName instanceof FieldName.Textual) {
                                result.boundNames.add(fieldName.getCalSourceForm());
                            }
                        }
                    }
                }
                
                return result;
            }

            /**
             * @param name
             *            the name to be checked.
             * @return whether the name is bound in this scope or any or the
             *         ancestor scopes.
             */
            private boolean isNameBound(String name) {
                
                Scope current = this;
                do {
                    if (current.boundNames.contains(name)) {
                        return true;
                    }
                    
                    current = current.parent;
                    
                } while (current != null);
                
                return false;
            }
            
            /**
             * @param name
             *            the name to be checked.
             * @return true iff the name is bound in the outermost scope but
             *         not in this scope nor in any other ancestor scopes.
             */
            private boolean isNameBoundOnlyInOutermostScope(String name) {
                
                Scope current = this;
                while (current.parent != null) {
                    if (current.boundNames.contains(name)) {
                        return false;
                    }
                    
                    current = current.parent;
                }
                
                return current.boundNames.contains(name);
            }
        }
        
        /**
         * Encapsulates the result of the scope analyzer.
         * 
         * @author Joseph Wong
         */
        private static final class Result {
            /**
             * A set of the variable references which do not resolve to any
             * local name bindings.
             */
            private final Set<SourceModel.Expr.Var> unqualifiedNonLocalReferences = new HashSet<SourceModel.Expr.Var>();

            /**
             * A set of the names which do not resolve to any local name
             * bindings.
             */
            private final Set<String> unqualifiedNonLocalNames = new HashSet<String>();

            /** A set of the names that are bound locally. */
            private final Set<String> locallyDefinedNames = new HashSet<String>();

            /**
             * A set of the variable references resolving to the parameters
             * of the outer lambda expression.
             */
            private final Set<SourceModel.Expr.Var> referencesToParameters = new HashSet<SourceModel.Expr.Var>();
        }
        
        /** The result of this analyzer. */
        private final Result result = new Result();
        
        /**
         * @return the result of this analyzer.
         */
        private Result getResult() {
            return result;
        }
        
        /**
         * Analyze the defining expression of a lambda expression, given the
         * parameters of the lambda expression.
         * 
         * @param params
         *            the parameters of the lambda expression.
         * @param inExpr
         *            the defining expression of the lambda expression.
         */
        private void analyzeLambdaDefiningExpr(SourceModel.Parameter[] params, SourceModel.Expr inExpr) {
            inExpr.accept(this, new Scope(params));
        }
        
        /**
         * Analyzes the variable reference with the supplied scoping
         * information.
         * 
         * @param var
         *            the variable reference.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Expr_Var(
            SourceModel.Expr.Var var, Scope scope) {
            
            SourceModel.Name.Function varName = var.getVarName();
            if (!varName.isQualified()) {
                
                if (!scope.isNameBound(varName.getUnqualifiedName())) {
                    // the name cannot be resolved to bindings in the local scopes,
                    // therefore it must be referring to a top-level binding
                    
                    result.unqualifiedNonLocalReferences.add(var);
                    result.unqualifiedNonLocalNames.add(varName.getUnqualifiedName());
                    
                } else {
                    // the name can be resolved, check to see if it refers to bindings
                    // in the outermost scope, i.e. the parameters of the lambda expression
                    
                    if (scope.isNameBoundOnlyInOutermostScope(varName.getUnqualifiedName())) {
                        result.referencesToParameters.add(var);
                    }
                }
            }
            
            return super.visit_Expr_Var(var, scope);
        }
        
        /**
         * Creates a new inner scope from the names bound in the let
         * expression and uses it to traverse the subexpressions.
         * 
         * @param let
         *            the let expression.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Expr_Let(
            SourceModel.Expr.Let let, Scope scope) {
            
            return super.visit_Expr_Let(let, scope.newInnerScope(let.getLocalDefinitions()));
        }
        
        /**
         * Creates a new inner scope from the parameters bound in the lambda
         * expression and uses it to traverse the defining expression.
         * 
         * @param lambda
         *            the lambda expression.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Expr_Lambda(
            SourceModel.Expr.Lambda lambda, Scope scope) {
            
            return super.visit_Expr_Lambda(lambda, scope.newInnerScope(lambda.getParameters()));
        }
        
        /**
         * Creates a new inner scope from the parameters bound in the function
         * definition and uses it to traverse the subexpressions.
         * 
         * @param function
         *            the function definition.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_FunctionDefn_Algebraic(
            SourceModel.FunctionDefn.Algebraic function, Scope scope) {
            
            return super.visit_FunctionDefn_Algebraic(function, scope.newInnerScope(function.getParameters()));
        }
        
        /**
         * Creates a new inner scope from the parameters bound in the local
         * function definition and uses it to traverse the subexpressions.
         * 
         * @param function
         *            the local function definition.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_LocalDefn_Function_Definition(
            SourceModel.LocalDefn.Function.Definition function, Scope scope) {
            
            result.locallyDefinedNames.add(function.getName());
            
            return super.visit_LocalDefn_Function_Definition(function, scope.newInnerScope(function.getParameters()));
        }

        /**
         * Adds the name of the declared function to the set of locally
         * defined names.
         * 
         * @param decl
         *            the local function type definition.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_LocalDefn_Function_TypeDeclaration(
            SourceModel.LocalDefn.Function.TypeDeclaration decl, Scope scope) {
        
            result.locallyDefinedNames.add(decl.getName());
            return super.visit_LocalDefn_Function_TypeDeclaration(decl, scope);
        }
        
        /**
         * Adds the name of the parameter to the set of locally defined
         * names.
         * 
         * @param parameter
         *            the parameter.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Parameter(
            SourceModel.Parameter parameter, Scope scope) {
            
            result.locallyDefinedNames.add(parameter.getName());
            return super.visit_Parameter(parameter, scope);
        }
        
        /**
         * Adds the name of the variable pattern to the set of locally
         * defined names.
         * 
         * @param var
         *            the variable pattern.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Pattern_Var(
            SourceModel.Pattern.Var var, Scope scope) {
            
            result.locallyDefinedNames.add(var.getName());
            return super.visit_Pattern_Var(var, scope);
        }
        
        /**
         * Creates a new inner scope from the names bound in the case
         * expression alternative and uses it to traverse the
         * right-hand-side expression.
         * 
         * @param tuple
         *            the case alternative.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Expr_Case_Alt_UnpackTuple(
            SourceModel.Expr.Case.Alt.UnpackTuple tuple, Scope scope) {
            
            return super.visit_Expr_Case_Alt_UnpackTuple(tuple, scope.newInnerScope(tuple.getPatterns()));
        }
     
        /**
         * Creates a new inner scope from the names bound in the case
         * expression alternative and uses it to traverse the
         * right-hand-side expression.
         * 
         * @param dataCons
         *            the case alternative.
         * @param surroundingScope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Expr_Case_Alt_UnpackDataCons(
            SourceModel.Expr.Case.Alt.UnpackDataCons dataCons, Scope surroundingScope) {
            
            CaseExprUnpackDataConsAltArgBindingsScopeBuilder scopeBuilder = new CaseExprUnpackDataConsAltArgBindingsScopeBuilder();
            
            Scope newScope = dataCons.getArgBindings().accept(scopeBuilder, surroundingScope);
            
            return super.visit_Expr_Case_Alt_UnpackDataCons(dataCons, newScope);
        }
        
        /**
         * A helper class for constructing a new scope from the argument bindings
         * within a data constructor case expression alternative, so that the newly
         * constructed scope with the additional bindings can be used to analyze
         * the right-hand-side expression of the alternative.
         *
         * @author Joseph Wong
         */
        private static class CaseExprUnpackDataConsAltArgBindingsScopeBuilder extends SourceModelTraverser<Scope, Scope> {
            /**
             * Creates a new inner scope from the names bound in the case
             * expression alternative and returns it, so that it may be used
             * by the caller in traversing the right-hand-side expression.
             * 
             * @param argBindings
             *            the argument bindings.
             * @param scope
             *            the surrounding scope.
             * @return the new scope.
             */
            @Override
            public Scope visit_ArgBindings_Matching(
                SourceModel.ArgBindings.Matching argBindings, Scope scope) {
                
                return scope.newInnerScope(argBindings.getFieldPatterns());
            }
            
            /**
             * Creates a new inner scope from the names bound in the case
             * expression alternative and returns it, so that it may be used
             * by the caller in traversing the right-hand-side expression.
             * 
             * @param argBindings
             *            the argument bindings.
             * @param scope
             *            the surrounding scope.
             * @return the new scope.
             */
            @Override
            public Scope visit_ArgBindings_Positional(
                SourceModel.ArgBindings.Positional argBindings, Scope scope) {
                
                return scope.newInnerScope(argBindings.getPatterns());
            }            
        }

        /**
         * Creates a new inner scope from the names bound in the case
         * expression alternative and uses it to traverse the
         * right-hand-side expression.
         * 
         * @param listCons
         *            the case alternative.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Expr_Case_Alt_UnpackListCons(
            SourceModel.Expr.Case.Alt.UnpackListCons listCons, Scope scope) {
            
            return super.visit_Expr_Case_Alt_UnpackListCons(
                listCons, scope.newInnerScope(
                    new SourceModel.Pattern[] {
                        listCons.getHeadPattern(), listCons.getTailPattern() }));
        }
        
        /**
         * Creates a new inner scope from the names bound in the case
         * expression alternative and uses it to traverse the
         * right-hand-side expression.
         * 
         * @param record
         *            the case alternative.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_Expr_Case_Alt_UnpackRecord(
            SourceModel.Expr.Case.Alt.UnpackRecord record, Scope scope) {
            
            if (record.getBaseRecordPattern() != null) {
                
                // first, apply this visitor to the base record pattern
                // and add this binding to the scope
                record.getBaseRecordPattern().accept(this, scope);
                
                scope = scope.newInnerScope(
                    new SourceModel.Pattern[] {
                        record.getBaseRecordPattern() });
            }
            for (int i = 0; i < record.getNFieldPatterns(); i++) {
                record.getNthFieldPattern(i).accept(this, scope);
            }
            
            // finally, visit the alternative's expression with the base record pattern
            // and all the field patterns appearing as bound names in the new scope
            record.getAltExpr().accept(this, scope.newInnerScope(record.getFieldPatterns()));
            return null;
        }
        
        /**
         * If the supplied field is punned, add the name of the field to the
         * set of locally defined names.
         * 
         * @param pattern
         *            the field pattern.
         * @param scope
         *            the surrounding scope.
         */
        @Override
        public Void visit_FieldPattern(
            SourceModel.FieldPattern pattern, Scope scope) {
            
            if (pattern.getPattern() != null) {
                pattern.getPattern().accept(this, scope);
            } else {
                if (pattern.getFieldName().getName() instanceof FieldName.Textual) {
                    // this field is punned, so the textual field name stands for the pattern
                    result.locallyDefinedNames.add(pattern.getFieldName().getName().getCalSourceForm());
                }
            }
            return null;
        }
    }
    
    /**
     * Performs a parameter renaming operation following a set of before->after
     * mappings. Name disambiguation is performed during the renaming operation
     * to resolve any name collisions.
     * <p>
     * 
     * This renaming operation is part of the algorithm for minimizing or
     * completely eliminating the need for a lambda expression to wrap around a
     * code gem. This is achieved by substituting arguments for names bound to
     * parameters in the defining expression of the lambda. In cases where a
     * parameter and its corresponding argument differ lexically, the renaming
     * operation takes care of transforming the defining expression so that all
     * references to the parameter now refer directly to the argument.
     * <p>
     * 
     * This renaming operation utilizes information from the
     * LambdaDefiningExprScopeAnalyzer for identifying situations where name
     * disambiguation needs to occur. For example, given the code gem:
     * 
     * <pre><code>
     *   let
     *       a = x; a_2 = y;
     *   in
     *       (a, a_2, x)
     * </code></pre>
     * 
     * If the parameters <code>x</code> and <code>y</code> are to be
     * replaced by <code>a</code> and <code>a_1</code> respectively, the let
     * definition of <code>a = x</code> and the reference to <code>a</code>
     * in <code>(a, a_2, x)</code> will need to be disambiguated. In
     * particular, the new name cannot collide with either <code>a</code>,
     * <code>a_1</code>, or <code>a_2</code>. Therefore, the algorithm
     * chooses the name of <code>a_3</code> as the replacement. The resulting
     * transformed source model would therefore correspond to the CAL source:
     * 
     * <pre><code>
     *   let
     *       a_3 = a; a_2 = a_1;
     *   in
     *       (a_3, a_2, a)
     * </code></pre>
     * 
     * This algorithm takes care to ensure that the semantics of the code
     * is preserved. For example, it will "un-pun" a punned field in a record
     * pattern for a case alternative if it is deemed that the implicit
     * pattern needs to be disambiguated.
     * <p>
     * 
     * @see CALSourceGenerator.LambdaDefiningExprScopeAnalyzer
     * 
     * @author Joseph Wong
     */
    private static final class ParameterRenamer extends SourceModelCopier<Void> {
        
        /**
         * A mapping of the old names to the new names for those parameters that
         * need to be renamed/
         */
        private final Map<String, String> paramsRenameMapping = new HashMap<String, String>();
        
        /**
         * A set of all the new names for the parameters, whether they differ
         * from the old ones or not.
         */
        private final Set<String> allNewParamNames = new HashSet<String>();

        /**
         * A mapping of all the names affected by the renaming of the
         * parameters to their new names.
         */
        private final Map<String, String> collateralDamageRepairMapping = new HashMap<String, String>();

        /** The result of the scope analyzer. */
        private final LambdaDefiningExprScopeAnalyzer.Result scopeAnalysis;
        
        /**
         * The code qualification map to use for translating unqualified references
         * to top-level functions to fully qualified ones.
         */
        private final CodeQualificationMap codeQualificationMap;
        
        /**
         * Constructor for ParameterRenamer.
         * 
         * @param before
         *            the list of original parameter names.
         * @param after
         *            the list of new names for the variables refering to
         *            the parameters.
         * @param scopeAnalyzerResult
         *            the result of the scope analyzer.
         * @param qualificationMap
         *            the code qualification map
         */
        private ParameterRenamer(
            List/*String*/<String> before,
            List/*String*/<String> after,
            LambdaDefiningExprScopeAnalyzer.Result scopeAnalyzerResult,
            CodeQualificationMap qualificationMap) {
            
            scopeAnalysis = scopeAnalyzerResult;
            codeQualificationMap = qualificationMap;
            
            if (before.size() != after.size()
                || new HashSet<String>(before).size() != before.size()
                || new HashSet<String>(after).size() != after.size()) {
                
                // the list of original names must be of the same length
                // as the list of new names for the renaming operation
                // to work
                
                // if the list of original names or the list of new names
                // contains any duplicates, the renaming operation cannot
                // preserve the semantics of the code being transformed, and
                // is therefore an error condition
                
                throw new IllegalArgumentException();
            }
            
            // first, populate the mappings for parameter renaming
            int numParams = before.size();
            
            for (int i = 0; i < numParams; i++) {
                if (!LanguageInfo.isValidFunctionName(before.get(i)) ||
                    !LanguageInfo.isValidFunctionName(after.get(i))) {
                    
                    // each name in the before and after lists must be a
                    // valid CAL function name, otherwise it is an error
                    throw new IllegalArgumentException();
                }
                
                allNewParamNames.add(after.get(i));
                
                // populate the mapping only when the pair of before/after
                // names differ
                if (!before.get(i).equals(after.get(i))) {
                    paramsRenameMapping.put(before.get(i), after.get(i));
                }
            }
            
            // then, construct a mapping of names affected by the parameter
            // renaming to their new disambiguated names
            for (final String localName : scopeAnalysis.locallyDefinedNames) {
                                               
                if (paramsRenameMapping.containsValue(localName)) {
                    // only populate the mapping when there is a collision
                    // of an existing name defined within the expression
                    // with one of the new names to be substituted for the
                    // parameters
                    collateralDamageRepairMapping.put(localName, calcCollateralDamageRepair(localName));
                }
            }
        }
        
        /**
         * Constructs a new name for an existing name affected by the
         * renaming of parameters.
         * 
         * @param origName
         *            the original name affected by the parameter renaming
         * @return a new name guaranteed not to collide with any other name
         *         in use
         */
        private String calcCollateralDamageRepair(String origName) {
            
            int i = 1;
            String newName = origName + "_" + i;
            
            while (allNewParamNames.contains(newName)
                   || collateralDamageRepairMapping.containsValue(newName)
                   || scopeAnalysis.unqualifiedNonLocalNames.contains(newName)
                   || scopeAnalysis.locallyDefinedNames.contains(newName)) {
                
                i++;
                newName = origName + "_" + i;
            }
            
            return newName;
        }
        
        /**
         * Processes an argument reference in a CALDoc and rename it if necessary.
         * 
         * @param argBlock
         *            the argument reference.
         * @param arg
         *            (unused)
         * @return the new argument reference, appropriately renamed if
         *         necessary.
         */
        @Override
        public SourceModel.CALDoc.TaggedBlock visit_CALDoc_TaggedBlock_Arg(
            SourceModel.CALDoc.TaggedBlock.Arg argBlock, Void arg) {
            
            FieldName argName = argBlock.getArgName().getName();
            if (argName instanceof FieldName.Textual) {
                
                String unqualifiedName = argName.getCalSourceForm();
                
                if (collateralDamageRepairMapping.containsKey(unqualifiedName)) {
                    
                    // This is an unqualified name that is defined locally
                    // within the body of the lambda and is affected by the
                    // parameter renaming. Map this name to its
                    // disambiguated version.
                    
                    return SourceModel.CALDoc.TaggedBlock.Arg.make(
                        SourceModel.Name.Field.make(FieldName.makeTextualField(collateralDamageRepairMapping.get(unqualifiedName))),
                        argBlock.getTextBlock());
                    
                } else {
                    // This unqualified name does not require renaming.
                    return super.visit_CALDoc_TaggedBlock_Arg(argBlock, arg);
                }
                
            } else {
                // Only textual argument names need to be renamed.
                return super.visit_CALDoc_TaggedBlock_Arg(argBlock, arg);
            }
        }
        
        /**
         * Processes a variable reference and rename it if necessary.
         * 
         * @param var
         *            the variable reference.
         * @param arg
         *            (unused)
         * @return the new variable reference, appropriately renamed if
         *         necessary.
         */
        @Override
        public SourceModel.Expr visit_Expr_Var(
            SourceModel.Expr.Var var, Void arg) {
            
            if (!var.getVarName().isQualified()) {
                
                String unqualifiedName = var.getVarName().getUnqualifiedName();
                
                if (paramsRenameMapping.containsValue(unqualifiedName)
                    && scopeAnalysis.unqualifiedNonLocalReferences.contains(var)) {
                    
                    // This is an unqualified name that used to refer to a
                    // top-level function in the current module, and which
                    // will now be shadowed by one of the renamed
                    // parameters. Explicit qualification needs to be used
                    // to ensure that the this variable continues to refer
                    // to the top-level function
                    
                    return SourceModel.Expr.Var.make(
                        codeQualificationMap.getQualifiedName(
                            unqualifiedName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD));
                    
                } else if (paramsRenameMapping.containsKey(unqualifiedName)
                           && scopeAnalysis.referencesToParameters.contains(var)) {
                    
                    // This is an unqualified name that used to refer to a
                    // parameter. If the parameter is renamed, translate
                    // this name to the new name of the parameter.
                    
                    return SourceModel.Expr.Var.makeUnqualified(paramsRenameMapping.get(unqualifiedName));
                    
                } else if (collateralDamageRepairMapping.containsKey(unqualifiedName)) {
                    
                    // This is an unqualifed name that is defined locally
                    // within the body of the lambda and is affected by the
                    // parameter renaming. Map this name to its
                    // disambiguated version.
                    
                    return SourceModel.Expr.Var.makeUnqualified(collateralDamageRepairMapping.get(unqualifiedName));
                    
                } else {
                    
                    // This unqualifed name does not require renaming.
                    return super.visit_Expr_Var(var, arg);
                }
            } else {
                
                // All fully qualified names are spared from the renaming process.
                return super.visit_Expr_Var(var, arg);
            }
        }
        
        /**
         * Perform name substitution on the supplied local function
         * definition and return the transformed function definition.
         * 
         * @param function
         *            the local function definition.
         * @param arg
         *            (unused)
         * @return the transformed function definition.
         */
        @Override
        public SourceModel.LocalDefn visit_LocalDefn_Function_Definition(
            SourceModel.LocalDefn.Function.Definition function, Void arg) {
            
            String name = function.getName();

            if (collateralDamageRepairMapping.containsKey(name)) {
                
                SourceModel.CALDoc.Comment.Function newCALDocComment = null;
                if (function.getCALDocComment() != null) {
                    newCALDocComment = (SourceModel.CALDoc.Comment.Function)function.getCALDocComment().accept(this, arg);
                }

                SourceModel.Parameter[] newParameters = new SourceModel.Parameter[function.getNParameters()];            
                for (int i = 0; i < function.getNParameters(); i++) {
                    newParameters[i] = (SourceModel.Parameter)function.getNthParameter(i).accept(this, arg);
                }

                return SourceModel.LocalDefn.Function.Definition.make(
                    newCALDocComment,
                    collateralDamageRepairMapping.get(name),
                    newParameters,
                    (SourceModel.Expr)function.getDefiningExpr().accept(this, arg));
            } else {
                return super.visit_LocalDefn_Function_Definition(function, arg);
            }
        }
        
        /**
         * Perform name substitution on the supplied local function type
         * definition and return the transformed function type definition.
         * 
         * @param decl
         *            the local function type definition.
         * @param arg
         *            (unused)
         * @return the transformed function type definition.
         */
        @Override
        public SourceModel.LocalDefn visit_LocalDefn_Function_TypeDeclaration(
            SourceModel.LocalDefn.Function.TypeDeclaration decl, Void arg) {
        
            String name = decl.getName();

            if (collateralDamageRepairMapping.containsKey(name)) {
                SourceModel.CALDoc.Comment.Function newCALDocComment = null;
                if (decl.getCALDocComment() != null) {
                    newCALDocComment = (SourceModel.CALDoc.Comment.Function)decl.getCALDocComment().accept(this, arg);
                }

                return SourceModel.LocalDefn.Function.TypeDeclaration.make(
                    newCALDocComment,
                    collateralDamageRepairMapping.get(name),
                    (SourceModel.TypeSignature)decl.getDeclaredType().accept(this, arg));
            } else {
                return super.visit_LocalDefn_Function_TypeDeclaration(decl, arg);
            }                            
        }
        
        /**
         * Perform name substitution on the supplied parameter and return
         * the transformed parameter.
         * 
         * @param parameter
         *            the parameter.
         * @param arg
         *            (unused)
         * @return the transformed parameter.
         */
        @Override
        public SourceModel.Parameter visit_Parameter(
            SourceModel.Parameter parameter, Void arg) {
            
            String name = parameter.getName();

            if (collateralDamageRepairMapping.containsKey(name)) {
                return SourceModel.Parameter.make(collateralDamageRepairMapping.get(name), parameter.isStrict());
            } else {
                return super.visit_Parameter(parameter, arg);
            }                            
        }
        
        /**
         * Perform name substitution on the supplied case expression
         * alternative variable pattern and return the transformed pattern.
         * 
         * @param var
         *            the case expression alternative variable pattern.
         * @param arg
         *            (unused)
         * @return the transformed pattern.
         */
        @Override
        public SourceModel.Pattern visit_Pattern_Var(
            SourceModel.Pattern.Var var, Void arg) {
            
            String name = var.getName();

            if (collateralDamageRepairMapping.containsKey(name)) {
                return SourceModel.Pattern.Var.make(collateralDamageRepairMapping.get(name));
            } else {
                return super.visit_Pattern_Var(var, arg);
            }                            
        }
        
        /**
         * Perform name substitution on the supplied case expression
         * alternative field pattern and return the transformed
         * pattern.
         * 
         * @param fieldPattern
         *            the case expression alternative field pattern.
         * @param arg
         *            (unused)
         * @return the transformed pattern.
         */
        @Override
        public SourceModel.FieldPattern visit_FieldPattern(
            SourceModel.FieldPattern fieldPattern, Void arg) {

            // the name subtitution needs to be done when the field pattern
            // is punned, and the implicit pattern collides with one of the
            // new names being substituted for the parameters
            
            if (fieldPattern.getPattern() == null
                && fieldPattern.getFieldName().getName() instanceof FieldName.Textual
                && collateralDamageRepairMapping.containsKey(fieldPattern.getFieldName().getName().getCalSourceForm())) {
                
                return SourceModel.FieldPattern.make(
                    SourceModel.Name.Field.make(fieldPattern.getFieldName().getName()),
                    SourceModel.Pattern.Var.make(
                        collateralDamageRepairMapping.get(fieldPattern.getFieldName().getName().getCalSourceForm())));
            } else {
                return super.visit_FieldPattern(fieldPattern, arg);
            }
        }
    }

    /**
     * Minimize (or remove completely) the lambda associated with the
     * application of a code gem.
     * <p>
     * 
     * This method operates on expressions of the form:
     * 
     * <pre><code>
     * ((\p_1 .. p_n -> definingExpr) a_1 .. a_m)
     * </code></pre>
     * 
     * For each parameter/argument pair (p_i, a_i), if p_i
     * is lexically identical to a_i, then the parameter p_i is
     * simply eliminated from the list of parameters, and likewise
     * a_i is dropped from the list of arguments.
     * <p>
     * 
     * If a_i is a variable but not identical to p_i, an argument
     * substitution is performed by using the LambdaDefiningExprScopeAnalyzer
     * and ParameterRenamer visitors. The first visitor traverses the
     * source model of the lambda's defining expression to identify
     * the variable references appearing within it, while the second visitor
     * uses the results gathered by the first visitor as a guide in
     * renaming occurrences of p_i in the defining expression to a_i, while
     * at the same time renaming other local name bindings therein to
     * avoid name collisions.
     * <p>
     * 
     * If all of the lambda's parameters are eliminated in this fashion,
     * the lambda itself is replaced by its defining expression.
     * <p>
     * 
     * For example, given the code gem:
     * 
     * <pre><code>
     *   let
     *       a = x; a_2 = y;
     *   in
     *       (a, a_2, x)
     * </code></pre>
     * 
     * After substituting the arguments <code>a</code> for <code>x</code>
     * and <code>a_1</code> for <code>y</code>, the resulting source model
     * would correspond to the CAL source:
     * 
     * <pre><code>
     *   let
     *       a_3 = a; a_2 = a_1;
     *   in
     *       (a_3, a_2, a)
     * </code></pre>
     * 
     * In particular, the local definition of <code>a</code> is renamed to
     * <code>a_3</code> so as not to collide with the newly renamed
     * variables of <code>a</code> and <code>a_1</code> as well as the
     * pre-existing local definition of <code>a_2</code>.
     * <p>
     * 
     * Note that this algorithm does <i>not</i> substitute
     * non-variable-reference expressions or qualified references to top-level
     * functions for parameters. For example:
     * 
     * <pre><code>
     * (\f x y -> f x y) Prelude.add a (Prelude.add b c)
     * </code></pre>
     * 
     * is tranformed to:
     * 
     * <pre><code>
     * (\f y -> f a y) Prelude.add (Prelude.add b c)
     * </code></pre>
     * 
     * @see CALSourceGenerator.LambdaDefiningExprScopeAnalyzer
     * @see CALSourceGenerator.ParameterRenamer
     * 
     * @param origExpr
     *            the expression to be transformed
     * @return the transformed expression with the outer-most lambda minimized
     *         or removed (if there were any in the original expression)
     */
    private static SourceModel.Expr removeRedundantLambda(
        SourceModel.Expr origExpr,
        final CodeQualificationMap codeQualificationMap) {
        
        SourceModel.verifyArg(origExpr, "origExpr");
        SourceModel.verifyArg(codeQualificationMap, "codeQualificationMap");
        
        // first, identify the cases where this operation cannot be
        // performed (because origExpr is of the wrong form), and
        // simply return origExpr in these cases

        if (!(origExpr instanceof SourceModel.Expr.Application)) {
            return origExpr;
        }

        SourceModel.Expr.Application app = (SourceModel.Expr.Application)origExpr;

        SourceModel.Expr[] appExprs = app.getExpressions();

        if (appExprs.length <= 1) {
            return origExpr;
        }

        if (!(appExprs[0] instanceof SourceModel.Expr.Lambda)) {
            return origExpr;
        }
        
        // origExpr is now verified to be of the form
        //   ((\p_1 .. p_n -> definingExpr) a_1 .. a_m)
        // so proceed with the algorithm

        SourceModel.Expr.Lambda lambda = (SourceModel.Expr.Lambda)appExprs[0];

        SourceModel.Expr definingExpr = lambda.getDefiningExpr();
        SourceModel.Parameter[] allParams = lambda.getParameters();

        int numArgs = appExprs.length - 1;
        int numArgsToProcess = Math.min(allParams.length, numArgs);
        
        // obtain the list of parameters to be renamed and their associated
        // arguments (= their new names), and the lists of parameters and
        // arguments which will remain after the parameter elimination process
        
        List<String> paramsToRename = new ArrayList<String>();
        List<String> argsToRename = new ArrayList<String>();
        
        List<SourceModel.Parameter> remainingParams = new ArrayList<SourceModel.Parameter>();
        List<SourceModel.Expr> remainingArgs = new ArrayList<SourceModel.Expr>();
        
        for (int i = 0; i < numArgsToProcess; i++) {
            SourceModel.Parameter param = allParams[i];
            SourceModel.Expr arg = appExprs[i + 1];

            if (arg instanceof SourceModel.Expr.Var &&
                !((SourceModel.Expr.Var)arg).getVarName().isQualified()) {
                
                // the argument is a simple unqualified name, so we can substitute it
                // for the parameter
                SourceModel.Expr.Var var = (SourceModel.Expr.Var)arg;
                
                paramsToRename.add(param.getName());
                argsToRename.add(var.getVarName().getUnqualifiedName());
            } else {
    
                // the argument is not a simple unqualified name, so we cannot remove nor
                // rename this parameter
                remainingParams.add(param);
                remainingArgs.add(arg);
            }
        }
        
        // analyze the names within the defining expression of the lambda
        LambdaDefiningExprScopeAnalyzer scopeAnalyzer = new LambdaDefiningExprScopeAnalyzer();
        scopeAnalyzer.analyzeLambdaDefiningExpr(allParams, definingExpr);
        
        // perform the appropriate renaming operations
        ParameterRenamer renamer = new ParameterRenamer(paramsToRename, argsToRename, scopeAnalyzer.getResult(), codeQualificationMap);
        definingExpr = (SourceModel.Expr)definingExpr.accept(renamer, null);
        
        // now add the remaining params and args that were not looped over
        for (int i = numArgsToProcess; i < allParams.length; i++) {
            remainingParams.add(allParams[i]);
        }

        for (int i = numArgsToProcess; i < numArgs; i++) {
            remainingArgs.add(appExprs[i + 1]);
        }

        if (remainingParams.isEmpty()) {

            // all the parameters of this lambda have been eliminated, therefore
            // the lambda can be completely removed
            if (remainingArgs.isEmpty()) {

                // so there are no remaining arguments either, therefore just
                // return the defining expression
                return definingExpr;
            } else {

                // form a new application for the defining expression
                // and the remainder of the arguments
                int numRemainingArgs = remainingArgs.size();
                SourceModel.Expr[] newAppExprs = new SourceModel.Expr[numRemainingArgs + 1];

                newAppExprs[0] = definingExpr;
                for (int i = 0; i < numRemainingArgs; i++) {
                    newAppExprs[i + 1] = remainingArgs.get(i);
                }

                return SourceModel.Expr.Application.make(newAppExprs);
            }
        } else {

            // some parameters of the lambda still remains, so construct a new
            // lambda
            SourceModel.Expr.Lambda newLambda = SourceModel.Expr.Lambda.make(
                remainingParams.toArray(new SourceModel.Parameter[0]),
                definingExpr);

            if (remainingArgs.isEmpty()) {

                // so there are no remaining arguments either, therefore just
                // return the new lambda
                return newLambda;
            } else {

                // form a new application for the new lambda
                // and the remainder of the arguments
                int numRemainingArgs = remainingArgs.size();
                SourceModel.Expr[] newAppExprs = new SourceModel.Expr[numRemainingArgs + 1];

                newAppExprs[0] = newLambda;
                for (int i = 0; i < numRemainingArgs; i++) {
                    newAppExprs[i + 1] = remainingArgs.get(i);
                }

                return SourceModel.Expr.Application.make(newAppExprs);
            }
        }
    }

    /**
     * Helper method to return an expression for an expression involving the
     * "[]" and "()" data constructors
     * 
     * @param operatorName
     *            either "[]" or "()"
     * @return the resulting expression
     */
    private static SourceModel.Expr getNullaryOpSourceModel(String operatorName) {
        if (operatorName.equals("[]")) {
            return SourceModel.Expr.List.make((SourceModel.Expr[])null);
        } else if (operatorName.equals("()")) {
            return SourceModel.Expr.Unit.make();
        }

        throw new IllegalArgumentException(
            "CALSourceGenerator.getNullaryOpSourceModel: " + operatorName + " is not a known nullary operator");
    }

    /**
     * Helper method to return an expression for a binary expression involving
     * an operator
     * 
     * @param operatorName
     *            the operator
     * @param left
     *            the left-hand-side subexpression
     * @param right
     *            the right-hand-side subexpression
     * @return the resulting expression
     */
    private static SourceModel.Expr getBinaryExprSourceModel(
        String operatorName, SourceModel.Expr left, SourceModel.Expr right) {
        
        if (operatorName.equals("&&")) {
            return SourceModel.Expr.BinaryOp.And.make(left, right);
        } else if (operatorName.equals("||")) {
            return SourceModel.Expr.BinaryOp.Or.make(left, right);            
        } else if (operatorName.equals("++")) {
            return SourceModel.Expr.BinaryOp.Append.make(left, right);            
        } else if (operatorName.equals("==")) {
            return SourceModel.Expr.BinaryOp.Equals.make(left, right);            
        } else if (operatorName.equals("!=")) {
            return SourceModel.Expr.BinaryOp.NotEquals.make(left, right);            
        } else if (operatorName.equals(">")) {
            return SourceModel.Expr.BinaryOp.GreaterThan.make(left, right);            
        } else if (operatorName.equals(">=")) {
            return SourceModel.Expr.BinaryOp.GreaterThanEquals.make(left, right);            
        } else if (operatorName.equals("<")) {
            return SourceModel.Expr.BinaryOp.LessThan.make(left, right);            
        } else if (operatorName.equals("<=")) {
            return SourceModel.Expr.BinaryOp.LessThanEquals.make(left, right);            
        } else if (operatorName.equals("+")) {
            return SourceModel.Expr.BinaryOp.Add.make(left, right);            
        } else if (operatorName.equals("-")) {
            return SourceModel.Expr.BinaryOp.Subtract.make(left, right);            
        } else if (operatorName.equals("*")) {
            return SourceModel.Expr.BinaryOp.Multiply.make(left, right);            
        } else if (operatorName.equals("/")) {
            return SourceModel.Expr.BinaryOp.Divide.make(left, right);   
        } else if (operatorName.equals("%")) {
            return SourceModel.Expr.BinaryOp.Remainder.make(left, right);
        } else if (operatorName.equals("$")) {
            // instead of returning (left $ right), we simply return (left right)
            return SourceModel.Expr.Application.make(new SourceModel.Expr[] {left, right});            
        } else if (operatorName.equals(":")) {
            return SourceModel.Expr.BinaryOp.Cons.make(left, right);
        }

        throw new IllegalArgumentException(
            "CALSourceGenerator.getBinaryExprSourceModel: " + operatorName + " is not a known binary operator");
    }

    /**
     * Helper method to return an expression with lambda-bound arguments, if
     * necessary.
     * 
     * @param expression
     *            the base expression
     * @param lambdaArgNames
     *            the arguments to bound as lambda arguments.
     * @return if lambdaArgNames is empty, expression is returned. Otherwise, an
     *         expression is returned of the form \lambdaArg1 lambdaArg2 ...
     *         lambdaArgN -> expressionText
     */
    private static SourceModel.Expr getMaybeLambdaSourceModel(SourceModel.Expr expression, List<String> lambdaArgNames) {

        // Are there any burnt arguments?
        if (lambdaArgNames.isEmpty()) {
            // No burnt arguments so just return the expression itself
            return expression;

        } else {
            // One or more burnt arguments so start a lambda and add the burnt
            // parameter names, the arrow, and the expression
            int numBurntArgs = lambdaArgNames.size();
            SourceModel.Parameter[] burntArgs = new SourceModel.Parameter[numBurntArgs];

            for (int i = 0; i < numBurntArgs; i++) {
                burntArgs[i] = SourceModel.Parameter.make(lambdaArgNames.get(i), false);
            }

            return SourceModel.Expr.Lambda.make(burntArgs, expression);
        }
    }
}
