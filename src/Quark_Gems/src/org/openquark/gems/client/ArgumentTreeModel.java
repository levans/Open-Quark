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
 * ArgumentTreeModel.java
 * Creation date: May 25, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.openquark.gems.client.ArgumentTreeNode.ArgumentNode;
import org.openquark.gems.client.ArgumentTreeNode.CollectorNode;
import org.openquark.gems.client.ArgumentTreeNode.RootNode;
import org.openquark.gems.client.Gem.PartInput;



/**
 * The TreeModel class used by the ArguementTree.
 * @author Edward Lam
 */
public class ArgumentTreeModel extends DefaultTreeModel {
    
    private static final long serialVersionUID = 8818055654152948899L;

    /** A comparator used to sort collector gems by unqualified name. */
    private static final Comparator<CollectorGem> collectorComparatorByName = new Comparator<CollectorGem>() {
        public int compare(CollectorGem o1, CollectorGem o2) {
            return o1.getUnqualifiedName().compareTo(o2.getUnqualifiedName());
        }
    };

    /** A comparator used to sort arguments by name. */
    private static final Comparator<Gem.PartInput> argumentComparatorByName = new Comparator<Gem.PartInput>() {
        public int compare(Gem.PartInput o1, Gem.PartInput o2) {
            return o1.getNameString().compareTo(o2.getNameString());
        }
    };
    
    /** The gem graph backing this model. */
    private final GemGraph gemGraph;
    
    /** map from collector gem to the collector node representing it. */
    private final Map<CollectorGem, CollectorNode> collectorToNodeMap = new WeakHashMap<CollectorGem, CollectorNode>();

    /** map from gem input to the argument node representing it. */
    private final Map<PartInput, ArgumentNode> argumentToNodeMap = new WeakHashMap<PartInput, ArgumentNode>();

    /** The collector to be displayed by the tree model.  If null, all collectors are displayed.  */
    private CollectorGem collectorToDisplay = null;

    /** Whether unused arguments are to be displayed. */
    private boolean displayUnusedArguments = false;
    
    // A couple of listeners to update the model when the gem graph changes.
    private final CollectorNameChangeListener collectorNameChangeListener = new CollectorNameChangeListener();
    private final ModelReflectedInputListener modelReflectedInputListener = new ModelReflectedInputListener();
    
    /** A listener to update on argument name changes. */
    private final InputNameListener inputNameListener = new InputNameListener() {
        public void inputNameChanged(InputNameEvent e) {
            nodeChanged(getArgumentNode(e.getInputChanged()));
        }
    };

    /**
     * The listener class for this model to handle collector name changes.
     * @author Edward Lam
     */
    private class CollectorNameChangeListener implements NameChangeListener {
        /**
         * {@inheritDoc}
         */
        public void nameChanged(NameChangeEvent e) {
            CollectorGem changedCollector = (CollectorGem)e.getSource();
            
            // Ignore if the changed collector gem is not displayed.
            if (!(collectorToDisplay == null || collectorToDisplay == changedCollector)) {
                return;
            }
            
            CollectorNode collectorNode = collectorToNodeMap.get(changedCollector);
            nodeChanged(collectorNode);
            
            // If we are displaying multiple collectors, ensure that they remain in order.
            // Don't reposition the target though.
            CollectorGem targetCollectorGem = gemGraph.getTargetCollector();
            if (collectorToDisplay == null && changedCollector != targetCollectorGem) {
                // Check vs. siblings.
                
                ArgumentTreeNode rootNode = (ArgumentTreeNode)getRoot();
                CollectorNode targetCollectorNode = getCollectorNode(targetCollectorGem);

                // Get the collector node which should precede this collector's node.
                CollectorNode precedingNode = (CollectorNode)collectorNode.getPreviousSibling();
                while (precedingNode != null && precedingNode != targetCollectorNode && 
                        collectorComparatorByName.compare(changedCollector, precedingNode.getCollectorGem()) < 0) {
                
                    precedingNode = (CollectorNode)precedingNode.getPreviousSibling();
                }
                
                // If it's not equal to the node which currently precedes it, reposition.
                if (precedingNode != collectorNode.getPreviousSibling()) {
                    removeNodeFromParent(collectorNode);
                    int insertIndex = (precedingNode == null) ? 0 : rootNode.getIndex(precedingNode) + 1;
                    insertNodeInto(collectorNode, rootNode, insertIndex);
                    
                    // We can skip the check against the following node, if the nodes are always sorted.
                    return;
                }
                
                // Get the collector node which should follow this collector's node.
                CollectorNode followingNode = (CollectorNode)collectorNode.getNextSibling();
                while (followingNode != null && collectorComparatorByName.compare(followingNode.getCollectorGem(), changedCollector) < 0) {
                    followingNode = (CollectorNode)followingNode.getNextSibling();
                }
                
                // If it's not equal to the node which currently follows it, reposition.
                if (followingNode != collectorNode.getNextSibling()) {
                    removeNodeFromParent(collectorNode);
                    int insertIndex = (followingNode == null) ? rootNode.getChildCount() : rootNode.getIndex(followingNode);
                    insertNodeInto(collectorNode, rootNode, insertIndex);
                }
            }
        }
    }
    
    /**
     * The listener class for this model to handle changes in the inputs reflected by a collector.
     * @author Edward Lam
     */
    private class ModelReflectedInputListener implements ReflectedInputListener {
        /**
         * {@inheritDoc}
         */
        public void reflectedInputsChanged(ReflectedInputEvent e) {
            CollectorGem collectorGem = (CollectorGem)e.getSource();
            
            // Ignore if the changed collector gem is not displayed.
            if (!(collectorToDisplay == null || collectorToDisplay == collectorGem)) {
                return;
            }

            // Just repopulate the entire tree for now.
            // If we wanted to, later we could limit the change to just the changed node.
            repopulate();
        }
    }
    
    /**
     * Constructor for an ArgumentTreeModel.
     * @param gemGraph the gem graph which this model represents.
     * @param root the root of the tree model.
     */
    ArgumentTreeModel(GemGraph gemGraph, RootNode root) {
        super(root);
        this.gemGraph = gemGraph;
        
        // Focus on the target collector by default.
        this.collectorToDisplay = gemGraph.getTargetCollector();

        // Populate the model.
        repopulate();

        // Add collector listeners for the collectors already in the gem graph.
        for (final Gem gem : gemGraph.getCollectors()) {
            CollectorGem collectorGem = (CollectorGem)gem;

            collectorGem.addNameChangeListener(collectorNameChangeListener);
            collectorGem.addReflectedInputListener(modelReflectedInputListener);
        }
        
        // Add input listeners for gems already in the gem graph
        for (final Gem gem : gemGraph.getGems()) {
            gem.addInputNameListener(inputNameListener);
        }

        // Add change listeners to appropriately update the model on gem graph changes.
        gemGraph.addGraphChangeListener(new GemGraphChangeListener() {

            public void gemAdded(GemGraphAdditionEvent e) {
                Gem addedGem = (Gem)e.getSource();
                addedGem.addInputNameListener(inputNameListener);
                
                if (addedGem instanceof CollectorGem) {
                    CollectorGem addedCollectorGem = (CollectorGem)addedGem;
                    
                    // Add the collector listeners.
                    addedCollectorGem.addNameChangeListener(collectorNameChangeListener);
                    addedCollectorGem.addReflectedInputListener(modelReflectedInputListener);
                    
                    // If all collector gems are displayed, update the tree for unused arguments.
                    if (displayUnusedArguments && collectorToDisplay == null) {
                        repopulate();
                    }
                }
            }

            public void gemRemoved(GemGraphRemovalEvent e) {
                Gem removedGem = (Gem)e.getSource();
                removedGem.removeInputNameListener(inputNameListener);
                
                if (e.getSource() instanceof CollectorGem) {
                    CollectorGem collectorGem = (CollectorGem)e.getSource();

                    // Update the tree.
                    repopulate();
                    
                    // Remove listeners.
                    collectorGem.removeNameChangeListener(collectorNameChangeListener);
                    collectorGem.removeReflectedInputListener(modelReflectedInputListener);
                }
            }

            public void gemConnected(GemGraphConnectionEvent e) {
            }

            public void gemDisconnected(GemGraphDisconnectionEvent e) {
            }
        });
    }
    
    /**
     * @return the gem graph upon which this model is based.
     */
    public GemGraph getGemGraph() {
        return gemGraph;
    }

    /**
     * @return the target collector gem for the model's gem graph.
     */
    public CollectorGem getTargetCollectorGem() {
        return gemGraph.getTargetCollector();
    }

    /**
     * Return the ArgumentNode used to represent the given argument.
     * @param argument the argument to be represented.
     * @return an ArgumentNode to use for that argument.  
     *   If possible, any ArgumentNode previously used to represent the argument will be reused.
     */
    ArgumentNode getArgumentNode(Gem.PartInput argument) {
        ArgumentNode argumentNode = argumentToNodeMap.get(argument);
        if (argumentNode == null) {
            argumentNode = new ArgumentNode(argument);
            argumentToNodeMap.put(argument, argumentNode);
        }
        return argumentNode;
    }
    
    /**
     * Return the CollectorNode used to represent the given collector.
     * @param collectorGem the collector to be represented.
     * @return an CollectorNode to use for that argument.  
     *   If possible, any CollectorNode previously used to represent the collector will be reused.
     */
    CollectorNode getCollectorNode(CollectorGem collectorGem) {
        CollectorNode collectorNode = collectorToNodeMap.get(collectorGem);
        if (collectorNode == null) {
            collectorNode = new CollectorNode(collectorGem);
            collectorToNodeMap.put(collectorGem, collectorNode);
        }
        return collectorNode;
    }
    
    /**
     * Populate the tree model with all the collectors in the gem graph.
     * Precondition: the current tree model must be empty.
     */
    private void populateAllCollectors() {
        
        Set<CollectorGem> collectors = gemGraph.getCollectors();
        
        // Map collector to the collectors targeting that collector.
        Map<CollectorGem, Set<CollectorGem>> targetToCollectorMap = new HashMap<CollectorGem, Set<CollectorGem>>();
        for (final Gem gem : collectors) {
            CollectorGem collectorGem = (CollectorGem)gem;
            CollectorGem target = collectorGem.getTargetCollectorGem();

            // Add the collector to the set of collectors targeting its target.
            Set<CollectorGem> targetingCollectors = targetToCollectorMap.get(target);
            if (targetingCollectors == null) {
                targetingCollectors = new HashSet<CollectorGem>();
                targetToCollectorMap.put(target, targetingCollectors);
            }
            targetingCollectors.add(collectorGem);
        }
        
        populate(gemGraph.getTargetCollector(), targetToCollectorMap);
      
    }
    
    /**
     * Populate the tree model with a subset of the gem graph.
     * @param collectorGem the collector which encloses the collectors with which to populate the model.
     * @param targetToCollectorMap map from collector to the collectors which target that collector.
     *   After populating with the current collector gem, populate() will be called recursively on targeting collectors.
     */
    private void populate(CollectorGem collectorGem, Map<CollectorGem, Set<CollectorGem>> targetToCollectorMap) {
        
        // Create a node for the collector, and add it to the parent.
        CollectorNode collectorNode = getCollectorNode(collectorGem);
        ((ArgumentTreeNode)getRoot()).add(collectorNode);
        collectorNode.removeAllChildren();
        
        // Add node for the arguments.
        List<Gem.PartInput> reflectedInputs = collectorGem.getReflectedInputs();
        for (final Gem.PartInput reflectedInput : reflectedInputs) {
            ArgumentNode reflectedInputNode = getArgumentNode(reflectedInput);
            collectorNode.add(reflectedInputNode);
        }
        
        if (displayUnusedArguments) {
            // Get the unused arguments.
            Set<PartInput> unusedArgumentSet = new HashSet<PartInput>(collectorGem.getTargetArguments());
            unusedArgumentSet.removeAll(reflectedInputs);
            
            // Sort them.
            List<PartInput> unusedArgumentList = new ArrayList<PartInput>(unusedArgumentSet);
            Collections.sort(unusedArgumentList, argumentComparatorByName);
            
            // Add the nodes.
            for (final PartInput unusedArgument : unusedArgumentList) {
                ArgumentNode unusedInputArgumentNode = getArgumentNode(unusedArgument);
                collectorNode.add(unusedInputArgumentNode);
            }
        }
        
        // Call recursively on targeting collectors.
        Set<CollectorGem> targetingCollectors = targetToCollectorMap.get(collectorGem);
        if (targetingCollectors != null) {

            // Sort the targeting collectors by unqualified name.
            List<CollectorGem> targetingCollectorList = new ArrayList<CollectorGem>(targetingCollectors);
            Collections.sort(targetingCollectorList, collectorComparatorByName);
            
            // Call on the targeting collectors, in order.
            for (final CollectorGem targetingCollector : targetingCollectorList) {
                populate(targetingCollector, targetToCollectorMap);
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     * Override so that commits for arguments in run mode repopulate the tree.
     */
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        repopulate();
    }
    
    /**
     * Generate nodeChanged() events for every argument targeting the target collector node.
     */
    void targetArgumentNodesChanged() {
        
        ArgumentTreeNode targetCollectorNode = getCollectorNode(gemGraph.getTargetCollector());

        int nChildren = targetCollectorNode.getChildCount();
        int[] childIndices = new int[nChildren];
        for (int i = 0; i < nChildren; i++) {
            childIndices[i] = i;
        }

        nodesChanged(targetCollectorNode, childIndices);
    }
    
    /**
     * Sort a set of collectors by unqualified name.
     * @param collectorSet the set of collectors to sort.
     * @return the collectors in collectorSet, sorted by unqualified name.
     */
    public static List<CollectorGem> getSortedCollectorList(Set<CollectorGem> collectorSet) {
        List<CollectorGem> targetingCollectorList = new ArrayList<CollectorGem>(collectorSet);
        Collections.sort(targetingCollectorList, collectorComparatorByName);
        return targetingCollectorList;
    }

    /**
     * @return the currently displayed collector, or null if all collectors are currently being displayed.
     */
    public CollectorGem getDisplayedCollector() {
        return collectorToDisplay;
    }

    /**
     * Change the collector which is the focus of the argument tree.
     * @param collectorToDisplay the new collector on which the argument tree should focus.
     *   If null, all collectors are displayed.
     */
    public void setCollectorToDisplay(CollectorGem collectorToDisplay) {
        if (this.collectorToDisplay != collectorToDisplay) {
            this.collectorToDisplay = collectorToDisplay;
            repopulate();
        }
    }
    
    /**
     * @return whether unused arguments are displayed.
     */
    public boolean getDisplayUnusedArguments() {
        return displayUnusedArguments;
    }

    /**
     * @param displayUnusedArguments whether unused arguments should be displayed.
     */
    public void setDisplayUnusedArguments(boolean displayUnusedArguments) {
        if (this.displayUnusedArguments != displayUnusedArguments) {
            this.displayUnusedArguments = displayUnusedArguments;
            repopulate();
        }
    }
    
    /**
     * Repopulate the tree model based on the current state.
     */
    private void repopulate() {
        // Remove all current children from the root.
        ArgumentTreeNode rootNode = (ArgumentTreeNode)getRoot();
        rootNode.removeAllChildren();
        
        if (collectorToDisplay == null) {
            // Display all collectors
            populateAllCollectors();
            
        } else {
            // Focus on a single collector.
            populate(collectorToDisplay, Collections.<CollectorGem, Set<CollectorGem>>emptyMap());
        }
        
        nodeStructureChanged(rootNode);
    }

}
