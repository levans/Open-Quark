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
 * GemGraphArgumentManager.java
 * Creation date: Jun 22, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.CALSourceGenerator;
import org.openquark.cal.compiler.CompositionNode;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.GemGraph.InputCollectMode;
import org.openquark.gems.client.GemGraph.TraversalScope;
import org.openquark.util.Pair;


/**
 * This class is responsible for managing argument targets and retargeting inputs in response to gem model events.
 * @author Edward Lam
 */
public class GemGraphArgumentManager {
    
    /** The target collector.  Arguments will often be retargeted at this collector gem. */
    private final CollectorGem targetCollector;
    
    /** A flag to disable argument updating. */
    private boolean disableArgumentUpdating = false;

    /**
     * Constructor for an GemGraphArgumentManager.
     * @param targetCollector the target collector.
     */
    GemGraphArgumentManager(CollectorGem targetCollector) {
        this.targetCollector = targetCollector;
    }
    
    /**
     * Sets the disableArgumentUpdating flag.
     *   This causes automatic argument updating by the retargeter to be disabled.
     * @param newValue The new value.  If false, argument updating is enabled.  However, affected reflectors will not have been updated.
     * @return the old value.
     */
    boolean setArgumentUpdatingDisabled(boolean newValue) {
        boolean oldSetting = this.disableArgumentUpdating;
        this.disableArgumentUpdating = newValue;

        return oldSetting;
    }
    
    /**
     * Retarget arguments after a connection has taken place.
     * @param conn the connection which was made.
     */
    public void retargetForConnect(Connection conn) {
        
        if (disableArgumentUpdating) {
            return;
        }
        
        PartInput destInput = conn.getDestination();
        Gem destGem = destInput.getGem();
        
        // Get the input argument which is disappearing.  Note the affected collector.
        CollectorGem replacedArgumentTarget = GemGraph.getInputArgumentTarget(destInput);

        // Make sure inputs from the connected subtree make sense.
        reconcileArgumentTargetsOnConnect(conn);
        
        // Update the corresponding collectors.
        Set<CollectorGem> affectedCollectorSet = new HashSet<CollectorGem>();
        
        // Add the collector targeted by the input being connected.
        if (replacedArgumentTarget != null) {
            affectedCollectorSet.add(replacedArgumentTarget);
        }

        // Find collectors targeted by free inputs in the descendant gem forest 
        // (when considering the gem graph after connection)
        List<PartInput> freeInputsInDescendantForestList = 
            GemGraph.obtainUnboundDescendantInputs(destGem, TraversalScope.FOREST, InputCollectMode.UNBURNT_ONLY);

        for (final PartInput partInput : freeInputsInDescendantForestList) {
            CollectorGem inputArgumentTarget = GemGraph.getInputArgumentTarget(partInput);

            if (inputArgumentTarget != null) {
                affectedCollectorSet.add(inputArgumentTarget);
            }
        }

        // Now update the gem graph for affected collectors.
        updateForArgumentChange(affectedCollectorSet);
    }
    
    /**
     * Retarget arguments after a disconnection has taken place.
     * @param conn the connection which was disconnected.
     */
    public void retargetForDisconnect(Connection conn) {

        if (disableArgumentUpdating) {
            return;
        }
        
        // Make sure inputs from the disconnected subtree make sense.
        Set<CollectorGem> affectedCollectorSet = new HashSet<CollectorGem>(reconcileArgumentTargetsOnDisconnect(conn));
        
        {
            PartInput freedInput = conn.getDestination();
            Gem.PartOutput disconnectedOutput = conn.getSource();

            // (HACK!?) Temporarily re-bind a connection so that argument target resolution can take place for the connected tree.
            // Collectors targeted by arguments on trees rooted at collectors at inner scopes of the current tree will be affected.
            freedInput.bindConnection(conn);
            disconnectedOutput.bindConnection(conn);

            // Find collectors targeted by free inputs in the descendant gem forest 
            // (when considering the gem graph after connection)
            List<PartInput> freeInputsInDescendantForestList = 
                GemGraph.obtainUnboundDescendantInputs(freedInput.getGem(), TraversalScope.FOREST, InputCollectMode.UNBURNT_ONLY);

            for (final PartInput partInput : freeInputsInDescendantForestList) {
                CollectorGem inputArgumentTarget = GemGraph.getInputArgumentTarget(partInput);

                if (inputArgumentTarget != null) {
                    affectedCollectorSet.add(inputArgumentTarget);
                }
            }

            // Unbind the connection.
            freedInput.bindConnection(null);
            disconnectedOutput.bindConnection(null);
        }
        
        // Update the corresponding collectors.
        updateForArgumentChange(affectedCollectorSet);
    }
    
    /**
     * Retarget an input argument from one collector to another.
     * @param collectorArgument the argument to retarget.
     * @param newTarget the collector to which the input will be retargeted.
     * @param addIndex the index at which the retargeted argument will be placed, or -1 to add to the end.
     * @return the old argument index, or -1 if there was no old target.
     */
    public int retargetInputArgument(PartInput collectorArgument, CollectorGem newTarget, int addIndex) {
        
        CollectorGem oldTarget = GemGraph.getInputArgumentTarget(collectorArgument);
        
        Set<CollectorGem> affectedCollectors = new HashSet<CollectorGem>();
        
        // Remove the argument from its old target if any.
        int oldArgIndex = -1;
        if (oldTarget != null) {
            oldArgIndex = oldTarget.getTargetArguments().indexOf(collectorArgument);
            oldTarget.removeArgument(collectorArgument);
            
            affectedCollectors.add(oldTarget);
        }
        
        // Add the argument to its new target.
        if (addIndex < 0) {
            newTarget.addArgument(collectorArgument);
        } else {
            newTarget.addArguments(addIndex, Collections.singleton(collectorArgument));
        }
        affectedCollectors.add(newTarget);

        // Update collectors and reflectors..
        updateForArgumentChange(affectedCollectors);

        return oldArgIndex;
    }
    
    /**
     * Retarget the inputs on the given gem for a definition change.
     * @param changedGem the gem whose definition changed.
     * @param oldInputs the inputs from before the change.
     */
    public void retargetArgumentsForDefinitionChange(Gem changedGem, PartInput[] oldInputs) {
        if (disableArgumentUpdating) {
            return;
        }

        Set<CollectorGem> affectedCollectorSet = reconcileArgumentsOnDefinitionChange(changedGem, oldInputs);
        updateForArgumentChange(affectedCollectorSet);
    }
    
    /**
     * Ensure that a collector's input is targeted when it is added to the gem graph.
     * @param addedCollector the collector which was added.
     */
    public void retargetArgumentsOnAdd(CollectorGem addedCollector) {

        if (disableArgumentUpdating) {
            return;
        }
        
        // get the input argument.
        PartInput newPartInput = addedCollector.getCollectingPart();

        // Add the arg to the target collector, if it's not already targeted.
        // TODOEL: this assumes that the target is (or will be) in the graph.
        CollectorGem inputTarget = GemGraph.getInputArgumentTarget(newPartInput);
        if (inputTarget == null) {
            addTargetArgsInSourceOrder(targetCollector, Collections.singleton(newPartInput));
        }
        
        // Update its reflected inputs.
        updateForArgumentChange(Collections.singleton(addedCollector));
    }
    
    /**
     * Ensure that a collector's input and targeting inputs are properly (un)targeted when it is removed from the gem graph.
     * @param removedCollector the collector which was removed.
     * @param retargetCollector the collector to which any arguments targeting the removed collector will be retargeted.
     */
    public void retargetArgumentsOnRemove(CollectorGem removedCollector, CollectorGem retargetCollector) {
        if (disableArgumentUpdating) {
            return;
        }
        
        PartInput collectingPart = removedCollector.getCollectingPart();
        CollectorGem collectingPartArgTarget = GemGraph.getInputArgumentTarget(collectingPart);

        // Remove the arg from its target, if any.
        if (collectingPartArgTarget != null) {
            collectingPartArgTarget.removeArgument(collectingPart);
        }
        
        // Update its reflected inputs.
        updateForArgumentChange(Collections.singleton(removedCollector));
        
        // Reassign all other arguments targeting the collector to the retarget collector.
        List<PartInput> targetArguments = removedCollector.getTargetArguments();
        removedCollector.removeArguments(targetArguments);
        retargetCollector.addArguments(targetArguments);
    }
    
    /**
     * When a connection takes place, call this method to reconcile the arguments on the connected subtree
     *   to make sure that their targets still make sense.
     * The old argument will disappear from its target, and new arguments will appear in appropriate target(s).
     * @param conn the connection which was made.
     */
    private void reconcileArgumentTargetsOnConnect(Connection conn) {
        
        // Get the arguments on the connecting subtree.
        Gem.PartOutput outputPart = conn.getSource();
        List<PartInput> connectedInputList = GemGraph.obtainUnboundDescendantInputs(outputPart.getGem(), TraversalScope.TREE, InputCollectMode.UNBURNT_ONLY);
        
        PartInput replacedInputArgument = conn.getDestination();
        
        // Get the input argument which is disappearing.  Note the affected collector.
        CollectorGem replacedArgumentTarget = GemGraph.getInputArgumentTarget(replacedInputArgument);
        
        // The best collector for the new input arguments to target is whatever collector was targeted by the argument being connected.
        // The exception is where any of the new inputs belong to reflectors, in which case the best collector to target is the target itself.
        boolean reflectorInputsPresent = false;
        for (final PartInput input : connectedInputList) {
            if (input.getGem() instanceof ReflectorGem) {
                reflectorInputsPresent = true;
                break;
            }
        }
        CollectorGem collectorToTarget = reflectorInputsPresent ? targetCollector : GemGraph.getInputArgumentTarget(replacedInputArgument);
        
        // Gather the new args, and affected collectors.
        Set<PartInput> newArgSet = new LinkedHashSet<PartInput>();

        for (final PartInput newInput: connectedInputList) {
            newArgSet.add(newInput);
        }
        
        // Try to put the arguments in the right place.
        if (collectorToTarget == null) {
            // Remove the replaced argument (if any) from its target.
            if (replacedArgumentTarget != null) {
                replacedArgumentTarget.removeArgument(replacedInputArgument);
            }
            
        } else if (replacedArgumentTarget == collectorToTarget) {
            // Replace the argument with the new arguments.
            replacedArgumentTarget.replaceArgument(replacedInputArgument, newArgSet);
            
        } else {
            // Remove the replaced argument (if any) from its target.
            if (replacedArgumentTarget != null) {
                replacedArgumentTarget.removeArgument(replacedInputArgument);
            }

            // Retarget any new arguments.
            if (!newArgSet.isEmpty()) {
                // Get the inputs on the tree when connected.
                List<PartInput> joinedInputList = GemGraph.obtainUnboundDescendantInputs(replacedInputArgument.getGem().getRootGem(), TraversalScope.TREE, 
                                                                              InputCollectMode.UNBURNT_ONLY);
                
                // Get the first and last indices of the new inputs in the list.
                int firstInputIndex = joinedInputList.indexOf(connectedInputList.get(0));    // if empty, should have been caught by the "if".
                int lastInputIndex = firstInputIndex + connectedInputList.size() - 1;
                
                if (firstInputIndex < 0) {
                    throw new IllegalStateException("Programming error.");
                }
    
                // retarget according to the connected tree.
                retargetInputArgumentsForTree(joinedInputList, firstInputIndex, lastInputIndex);  // TODOEL: this doesn't use "collectorToTarget".
            }
        }
    }
    
    /**
     * When a disconnection takes place, call this method to reconcile the arguments on the connected subtree
     *   to make sure that their targets still make sense.
     * The old arguments (on the disconnected tree) will disappear from their targets, 
     *   and the new argument (the connection destination) will appear in an appropriate target.
     * @param conn the connection which was broken.
     * @return  the collectors which were directly affected (ie. had arguments added or removed).
     */
    private Set<CollectorGem> reconcileArgumentTargetsOnDisconnect(Connection conn) {
        
        // The set of affected collectors.
        // Note that in the body of this method we freely add nulls to this set.  Null is removed before returning the set to the caller.
        Set<CollectorGem> affectedCollectorSet = new HashSet<CollectorGem>();
        
        PartInput freedInput = conn.getDestination();
        Gem.PartOutput disconnectedOutput = conn.getSource();

        // Determine whether the disconnected input was originally orphaned (ie. only present because of the connection) when connected.
        // We really only have to check for orphaned reflector inputs, since code gem panels check for orphaned code gem inputs.
        boolean wasOrphanedReflectorInput = false;
        if (freedInput.getGem() instanceof ReflectorGem) {
            PartInput reflectedInput = ((ReflectorGem)freedInput.getGem()).getReflectedInput(freedInput);
            if (reflectedInput == null || reflectedInput.isConnected()) {
                wasOrphanedReflectorInput = true;

            } else {
                Gem reflectedInputGem = reflectedInput.getGem();
                int reflectedInputNum = reflectedInput.getInputNum();
                wasOrphanedReflectorInput = reflectedInputNum >= reflectedInputGem.getNInputs() ||
                                            reflectedInputGem.getInputPart(reflectedInputNum) != reflectedInput;
            }
        }
        
        // Get the inputs on the disconnecting subtree.
        List<PartInput> disconnectedTreeInputList = GemGraph.obtainUnboundDescendantInputs(disconnectedOutput.getGem(), TraversalScope.TREE, 
                                                                                InputCollectMode.UNBURNT_ONLY);
        
        // (HACK!?) Temporarily re-bind a connection so that argument target resolution can take place.
        freedInput.bindConnection(conn);
        disconnectedOutput.bindConnection(conn);
        
        Map<PartInput, CollectorGem> disconnectedInputToTargetMap = new HashMap<PartInput, CollectorGem>();
        for (final PartInput disconnectedTreeInput : disconnectedTreeInputList) {
            CollectorGem affectedCollector = GemGraph.getInputArgumentTarget(disconnectedTreeInput);
            disconnectedInputToTargetMap.put(disconnectedTreeInput, affectedCollector);
        }
    
        // Figure out the collector to target.
        CollectorGem collectorToTarget = getCollectorToTarget(disconnectedTreeInputList);
        if (collectorToTarget == null) {
            collectorToTarget = GemGraph.obtainOutermostCollector(freedInput.getGem());
        }
        affectedCollectorSet.add(collectorToTarget);
          
        // Unbind the connection.
        freedInput.bindConnection(null);
        disconnectedOutput.bindConnection(null);

        // First the case of an orphaned input (just remove the input from its target).
        if (wasOrphanedReflectorInput) {
            CollectorGem argumentCollectorTarget = disconnectedInputToTargetMap.get(freedInput);
            if (argumentCollectorTarget != null) {
                argumentCollectorTarget.removeArgument(freedInput);
                
                affectedCollectorSet.add(argumentCollectorTarget);
            }
        }
        
        // Iterate over the inputs of the tree which was disconnected, removing them from their target collector.
        // Also, if necessary retarget the new free argument if any of the disconnecting inputs targeted the desired target.
        boolean needToRetargetFreedArgument = !wasOrphanedReflectorInput;
        
        for (final Map.Entry<PartInput, CollectorGem> mapEntry: disconnectedInputToTargetMap.entrySet()) {
            PartInput disconnectedTreeInput = mapEntry.getKey();
            CollectorGem affectedCollector = mapEntry.getValue();
            
            if (affectedCollector != null) {
                
                // Retarget the new free argument if this argument targets the target collector.
                if (needToRetargetFreedArgument && collectorToTarget != null && collectorToTarget.isTargetedBy(disconnectedTreeInput)) {
                    collectorToTarget.replaceArgument(disconnectedTreeInput, Collections.singleton(freedInput));
                    needToRetargetFreedArgument = false;

                } else {
                    affectedCollector.removeArgument(disconnectedTreeInput);
                }
                
                affectedCollectorSet.add(affectedCollector);
            }
        }
        
        // re-target the new input's argument if necessary, and we haven't already.
        if (needToRetargetFreedArgument && collectorToTarget != null) {

            // Retarget according to inputs from the new input's tree.
            Gem inputRoot = freedInput.getGem().getRootGem();
            List<PartInput> destinationInputList = GemGraph.obtainUnboundDescendantInputs(inputRoot, TraversalScope.TREE, InputCollectMode.UNBURNT_ONLY);

            int newInputIndex = destinationInputList.indexOf(freedInput);
            if (newInputIndex < 0) {
                throw new IllegalStateException("Programming Error.");
            }
    
            CollectorGem collectorTargeted = retargetInputArgumentsForTree(destinationInputList, newInputIndex, newInputIndex);
            affectedCollectorSet.add(collectorTargeted);
        }

        // Remove any nulls from the set (for any args above which didn't have a target).
        affectedCollectorSet.remove(null);
        
        return affectedCollectorSet;
    }

    /**
     * Remove inputs from their targets.
     * @param inputsToRemove the inputs to remove from their targets.
     * @return the collectors from which the inputs were removed.
     */
    private static Set<CollectorGem> removeFromTargets(Set<PartInput> inputsToRemove) {
        Set<CollectorGem> affectedCollectorSet = new HashSet<CollectorGem>();
        
        // Now remove from their targets any arguments for inputs which are going away.
        for (final PartInput inputToRemove : inputsToRemove) {
            CollectorGem targetedCollector = GemGraph.getInputArgumentTarget(inputToRemove);
            
            if (targetedCollector != null) {
                affectedCollectorSet.add(targetedCollector);
                targetedCollector.removeArgument(inputToRemove);
            }
        }
        
        return affectedCollectorSet;
    }
    
    /**
     * Get the inputs on the tree before a gem's inputs changed.
     * @param changedGem the gem whose inputs changed.
     * @param oldInputs the inputs before the gem changed.
     * @return the inputs on the connected gem tree before the change.
     */
    private static List<PartInput> getOldUnburntTreeInputList(Gem changedGem, PartInput[] oldInputs) {
        // Create a code gem with the right number of inputs.
        CodeGem codeGem = new CodeGem(oldInputs.length);

        // Replace the changed gem connections with the code gem connections.
        Set<Connection> oldConnections = new HashSet<Connection>();

        for (int i = 0, nOldInputs = oldInputs.length; i < nOldInputs; i++) {
            PartInput oldInput = oldInputs[i];
            
            Connection oldInputConnection = oldInput.getConnection();
            
            if (oldInputConnection != null) {
                oldConnections.add(oldInputConnection);
                
                Gem.PartOutput source = oldInputConnection.getSource();
                PartInput dest = codeGem.getInputPart(i);

                Connection newConnection = new Connection(source, dest);
                source.bindConnection(newConnection);
                dest.bindConnection(newConnection);
            }
        }

        Connection oldOutputConnection = changedGem.getOutputPart().getConnection();
        if (oldOutputConnection != null) {
            oldConnections.add(oldOutputConnection);
            
            Gem.PartOutput source = codeGem.getOutputPart();
            PartInput dest = oldOutputConnection.getDestination();

            Connection newConnection = new Connection(source, dest);
            source.bindConnection(newConnection);
            dest.bindConnection(newConnection);
        }        

        // Grab the old inputs.
        List<PartInput> oldUnburntTreeInputList = 
            new ArrayList<PartInput>(GemGraph.obtainUnboundDescendantInputs(changedGem.getRootGem(), TraversalScope.TREE, InputCollectMode.UNBURNT_ONLY));
        
        // Replace code gem inputs with old inputs.
        int index = 0;
        for (final PartInput oldUnburntTreeInput : oldUnburntTreeInputList) {
            if (oldUnburntTreeInput.getGem() == codeGem) {
                PartInput oldInput = oldInputs[oldUnburntTreeInput.getInputNum()];
                oldUnburntTreeInputList.set(index, oldInput);
            }
            index++;
        }
        
        // Revert all the connections.
        for (final Connection oldConnection : oldConnections) {
            oldConnection.getSource().bindConnection(oldConnection);
            oldConnection.getDestination().bindConnection(oldConnection);
        }
        
        return oldUnburntTreeInputList;
    }
    
    /**
     * Retarget the inputs on the given gem for a definition change.
     * @param changedGem the gem whose definition changed.
     * @param oldInputs the inputs from before the change.
     * @return the collectors which were affected by argument targeting changes.
     */
    private static Set<CollectorGem> reconcileArgumentsOnDefinitionChange(Gem changedGem, PartInput[] oldInputs) {
        
        // TODOEL: make use of cached input targeting info.
        // TODOEL: break this method up into smaller pieces.
        // TODOEL: handle better the case where the new inputs are in natural order, but tree inputs are not.
        //          (eg. if one of the branch's had inputs reorganized).
        
        Set<CollectorGem> affectedCollectorSet = new HashSet<CollectorGem>();
        
        // Check whether there is a collector to target.
        Set<CollectorGem> enclosingCollectorSet = GemGraph.obtainEnclosingCollectors(changedGem);
        if (enclosingCollectorSet.isEmpty()) {
            return affectedCollectorSet;
        }
        
        // Get the inputs on the tree to which the gem is attached.
        List<PartInput> unburntTreeInputList = GemGraph.obtainUnboundDescendantInputs(changedGem.getRootGem(), TraversalScope.TREE, 
                                                                           InputCollectMode.UNBURNT_ONLY);
        Set<PartInput> unburntTreeInputSet = new LinkedHashSet<PartInput>(unburntTreeInputList);

        // Get the old tree inputs.
        List<PartInput> oldUnburntTreeInputList = getOldUnburntTreeInputList(changedGem, oldInputs);

        // Get the old targetable inputs.
        List<PartInput> oldTargetableGemInputs = new ArrayList<PartInput>();
        for (final PartInput oldInput : oldInputs) {
            if (!oldInput.isConnected() && !oldInput.isBurnt()) {
                oldTargetableGemInputs.add(oldInput);
            }
        }
        
        // Get the updated targetable inputs.
        Set<PartInput> updatedTargetableInputSet = new LinkedHashSet<PartInput>();
        int nArgs = changedGem.getNInputs();
        for (int i = 0; i < nArgs; i++) {
            PartInput updatedGemInput = changedGem.getInputPart(i);
            if (!updatedGemInput.isConnected() && !updatedGemInput.isBurnt()) {
                updatedTargetableInputSet.add(updatedGemInput);
            }
        }
        
        // Get the new (untargeted) arguments.
        List<PartInput> untargetedNewArguments = new ArrayList<PartInput>(updatedTargetableInputSet);
        untargetedNewArguments.removeAll(oldTargetableGemInputs);
        
        // Find the best collector to target.
        // This should only be null if there were no targetable inputs on the tree or the gem which changed.
        CollectorGem collectorToTarget = oldTargetableGemInputs.isEmpty() ? getCollectorToTarget(oldUnburntTreeInputList) 
                                                                          : getCollectorToTarget(oldTargetableGemInputs);
        if (collectorToTarget == null) {
            collectorToTarget = GemGraph.obtainOutermostCollector(changedGem);
        }

        // Get the collector's old targeting arguments.
        List<PartInput> targetArgs = collectorToTarget.getTargetArguments();
        int nTargetArgs = targetArgs.size();

        // Get the tree arguments which used to target the collector to target, in order.
        List<PartInput> oldTargetingTreeInputs = new ArrayList<PartInput>();
        for (final PartInput unburntTreeInput : oldUnburntTreeInputList) {
            if (GemGraph.getInputArgumentTarget(unburntTreeInput) == collectorToTarget) {
                oldTargetingTreeInputs.add(unburntTreeInput);
            }
        }

        // Calculate characteristics of the previous tree inputs - whether they were together, and/or in natural order.
        Pair<Boolean, Boolean> oldTreeInputsTogetherOrInNaturalOrder = isTogetherOrInNaturalOrder(oldTargetingTreeInputs, targetArgs);
        boolean oldTreeInputsTogether = oldTreeInputsTogetherOrInNaturalOrder.fst().booleanValue();
        boolean oldTreeInputsInNaturalOrder = oldTreeInputsTogetherOrInNaturalOrder.snd().booleanValue();
        
        // Gem the gem inputs which carry over, in order.
        List<PartInput> remainingOldGemInputList = new ArrayList<PartInput>(unburntTreeInputSet);
        remainingOldGemInputList.retainAll(oldTargetableGemInputs);
        
        // Determine if they occur together in natural order (or just together) in the targeted collector's target arguments.
        Pair<Boolean, Boolean> isTogetherOrInNaturalOrder = isTogetherOrInNaturalOrder(oldTargetableGemInputs, targetArgs);
        boolean isTogether = isTogetherOrInNaturalOrder.fst().booleanValue();
        boolean isInNaturalOrder = isTogetherOrInNaturalOrder.snd().booleanValue();
        
        // If the old inputs (on the gem or on the tree) were in natural order, ensure that they remain so.
        if (oldTreeInputsInNaturalOrder) {
            collectorToTarget.removeArguments(targetArgs);
            targetArgs = getTargetArgsInOrder(targetArgs, unburntTreeInputList);
            collectorToTarget.addArguments(targetArgs);

            // The collector to target will also be affected..
            affectedCollectorSet.add(collectorToTarget);

        } else if (isInNaturalOrder) {
            collectorToTarget.removeArguments(targetArgs);
            targetArgs = getTargetArgsInOrder(targetArgs, Arrays.asList(changedGem.getInputParts()));
            collectorToTarget.addArguments(targetArgs);

            // The collector to target will also be affected..
            affectedCollectorSet.add(collectorToTarget);
        }

        // Calculate the inputs which are going away.
        Set<PartInput> departingInputs = new HashSet<PartInput>(oldTargetableGemInputs);
        departingInputs.removeAll(updatedTargetableInputSet);

        // If there aren't any untargeted new arguments, we're done.
        if (untargetedNewArguments.isEmpty()) {
            // Remove from their targets any arguments for inputs which are going away.
            affectedCollectorSet.addAll(removeFromTargets(departingInputs));

            return affectedCollectorSet;
        }
        
        //
        // The rest of this method deals with where to place untargeted new arguments.
        //
        
        // The collector to target will also be affected..
        affectedCollectorSet.add(collectorToTarget);
        
        if (isTogether && isInNaturalOrder) {
            // This is the argument on collectorToTarget before which this gem's args appear.
            //   Null means add to the beginning.
            PartInput argBefore;
            
            // Add the new args to wherever they appear in the tree input set.
            PartInput firstUpdatedInput = updatedTargetableInputSet.iterator().next();
            int firstUpdatedInputIndex = unburntTreeInputList.indexOf(firstUpdatedInput);
            
            if (firstUpdatedInputIndex < 1) {
                argBefore = null;
                
            } else {
                // Get the immediately preceding input on the tree which targets the same collector.
                List<PartInput> precedingTreeInputs = unburntTreeInputList.subList(0, firstUpdatedInputIndex);
                PartInput precedingTargetingInput = getTargetingArg(precedingTreeInputs, collectorToTarget, false);
                
                if (precedingTargetingInput != null) {
                    // Add after the preceding tree input which targets the same collector.
                    argBefore = precedingTargetingInput;

                } else {
                    // No preceding tree inputs target the same collector.

                    // Determine if any inputs which follow on the tree target the same collector.
                    PartInput lastUpdatedInput = (new ArrayList<PartInput>(updatedTargetableInputSet)).get(updatedTargetableInputSet.size() - 1);
                    int lastUpdatedInputIndex = unburntTreeInputList.indexOf(lastUpdatedInput);
                    
                    List<PartInput> followingTreeInputs = unburntTreeInputList.subList(lastUpdatedInputIndex + 1, unburntTreeInputList.size());
                    PartInput followingTargetingInput = getTargetingArg(followingTreeInputs, collectorToTarget, false);
                    
                    if (followingTargetingInput != null) {
                        // Add before the following tree input which targets the same collector.
                        int followingArgTargetIndex = targetArgs.indexOf(followingTargetingInput);
                        
                        // Construct the reverse list of preceding targeting args.
                        List<PartInput> reversedPrecedingTargetArgs = new ArrayList<PartInput>(targetArgs.subList(0, followingArgTargetIndex));
                        Collections.reverse(reversedPrecedingTargetArgs);
                        
                        // Set the arg before to the last preceding arg which isn't an input to the changed gem.
                        // If there isn't any such arg, add to the beginning.
                        argBefore = null;
                        for (final PartInput precedingTargetArg : reversedPrecedingTargetArgs) {
                            if (precedingTargetArg.getGem() != changedGem) {
                                argBefore = precedingTargetArg;
                                break;
                            }
                        }
                        
                    } else {
                        // Add to the end of the collector's args.
                        argBefore = targetArgs.get(nTargetArgs - 1);
                    }
                }
            }

            // remove all the old args..
            collectorToTarget.removeArguments(remainingOldGemInputList);
            
            // add all the updated args back..
            collectorToTarget.addArguments(argBefore, updatedTargetableInputSet, argBefore != null);

        } else if (isInNaturalOrder) {
            // The old inputs appear in natural order in the target arg list.
            // Maintain natural order among new inputs.
            
            // Get the tree arguments which will target the collector to target.
            List<PartInput> newTargetingTreeInputs = new ArrayList<PartInput>();
            for (final PartInput unburntTreeInput : unburntTreeInputList) {
                if (GemGraph.getInputArgumentTarget(unburntTreeInput) == collectorToTarget || unburntTreeInput.getGem() == changedGem) {
                    newTargetingTreeInputs.add(unburntTreeInput);
                }
            }
            
            // If the old tree inputs appeared together in natural order, replace with the new tree inputs.
            if (oldTreeInputsTogether && oldTreeInputsInNaturalOrder) {

                // Remove the arguments, add them back at the appropriate location.
                int addIndex = targetArgs.indexOf(oldTargetingTreeInputs.get(0));
                collectorToTarget.removeArguments(oldTargetingTreeInputs);
                collectorToTarget.addArguments(addIndex, newTargetingTreeInputs);
                

            } else if (oldTreeInputsInNaturalOrder) {
                // If the tree inputs just appear in natural order but not together, insert semi-smartly.
                for (final PartInput newInput : untargetedNewArguments) {
                    // If no preceding tree args, add before the first of the args which targeted the collector.
                    if (newInput == newTargetingTreeInputs.get(0)) {
                        PartInput firstTargetingArg = oldTargetingTreeInputs.get(0);
                        collectorToTarget.addArguments(firstTargetingArg, Collections.singleton(newInput), false);

                    } else {
                        // Add after the arg which precedes it.
                        PartInput precedingTargetingTreeInput = newTargetingTreeInputs.get(newTargetingTreeInputs.indexOf(newInput) - 1);
                        collectorToTarget.addArguments(precedingTargetingTreeInput, Collections.singleton(newInput), true);
                    }
                }
                
            } else {
                // Otherwise, the tree inputs are not in natural order, but the old gem inputs are.
                // Add to the end.
                // Note: it would be better to maintain order where this makes sense 
                //   (eg. if intervening gem tree inputs appear together but are reordered wrt each other).
                
                // Now add the arguments to the collector, at the end of the target arg list.
                collectorToTarget.addArguments(untargetedNewArguments);
            }
            
        } else {
            // The remaining args are not in natural order on target args.
            
            // This is the argument on collectorToTarget before which the new args appear.
            //   Null means add to the end.
            PartInput argBefore;    

            if (isTogether) {
                // Add after the last of the target's arguments that belongs to an input on this gem. 
                argBefore = null;
                for (final PartInput targetArg : targetArgs) {
                    if (targetArg.getGem() == changedGem) {
                        argBefore = targetArg;
                    }
                }

            } else {
                // Add to the end.
                argBefore = null;
            }
            
            // Now add the untargeted new arguments to the collector, at the appropriate location.
            collectorToTarget.addArguments(argBefore, untargetedNewArguments, true);
        }
        
        // Remove from their targets any arguments for inputs which are going away.
        affectedCollectorSet.addAll(removeFromTargets(departingInputs));
        
        return affectedCollectorSet;
    }

    /**
     * @param oldTargetArgs the old target arguments
     * @param updatedTargetingInputs arguments, in the order in which they appear as updated.
     * @return a list of arguments from targetArgs, reordered such that args also appearing in updatedTargetingInputs
     *   appear in their new order.
     */
    private static List<PartInput> getTargetArgsInOrder(List<PartInput> oldTargetArgs, List<PartInput> updatedTargetingInputs) {
        
        // make a copy of the target args array.
        List<PartInput> newTargetArgs = new ArrayList<PartInput>(oldTargetArgs);
        
        // Get the args which appear in both sets, in their old order.
        List<PartInput> oldIntersection = new ArrayList<PartInput>(oldTargetArgs);
        oldIntersection.retainAll(updatedTargetingInputs);
        
        if (!oldIntersection.isEmpty()) {
            // Get the index of the first of the targeting inputs in the old target args list.
            int firstIndex = oldTargetArgs.indexOf(oldIntersection.get(0));
    
            // Get the intersection, in the new order.
            List<PartInput> newIntersection = new ArrayList<PartInput>(updatedTargetingInputs);
            newIntersection.retainAll(oldTargetArgs);
            
            if (!newIntersection.equals(oldIntersection)) {
                // For now, just put all the intersecting args together, and put anything that might lie among those at the end.
                newTargetArgs.removeAll(newIntersection);
                newTargetArgs.addAll(firstIndex, newIntersection);
            }
        }
        
        return newTargetArgs;
    }

    /**
     * Return whether a given list's items appear together in another list, or appear in order in the other list.
     * @param subList the list in question.
     * @param superList the list which contains the first list.
     * @return Pair:
     *   The first item is whether the items appear together, the second is whether they appear in order.
     *   Both false if subList is not a sublist of superList, or if the intersection of subList and superList is empty.
     */
    private static Pair<Boolean, Boolean> isTogetherOrInNaturalOrder(List<PartInput> subList, List<PartInput> superList) {
        // Copy the superList, and cut it down to only those items in the sublist.
        List<PartInput> cutSuperList = new ArrayList<PartInput>(superList);
        cutSuperList.retainAll(subList);
        
        // Check for list precondition, non-intersecting lists.
        if (cutSuperList.isEmpty() || cutSuperList.size() != subList.size()) {
            return new Pair<Boolean, Boolean>(Boolean.FALSE, Boolean.FALSE);
        }

        // Figure out if the items are in natural order.
        boolean isInNaturalOrder = cutSuperList.equals(new ArrayList<PartInput>(subList));
        
        // Calculate the bounds of a sublist the length of subList, starting from the index of the first item of subList in superList.
        int firstIndex = subList.isEmpty() ? -1 : superList.indexOf(subList.get(0));
        int lastIndex = firstIndex + subList.size();
        
        // Figure out if the items are together.
        boolean isTogether = firstIndex >= 0 && lastIndex < superList.size() && superList.subList(firstIndex, lastIndex).containsAll(subList);

        // Return the result.        
        return new Pair<Boolean, Boolean>(Boolean.valueOf(isTogether), Boolean.valueOf(isInNaturalOrder));
    }
    
    /**
     * Get the first or last arg in the list which targets the given collector.
     * @param argList the inputs to check.
     * @param collectorTarget the target.
     * @param first if true, returns the first arg which targets collectorTarget.  If false, returns the last one.
     * @return the first or last arg in the list which targets the given collector.
     */
    private static PartInput getTargetingArg(List<PartInput> argList, CollectorGem collectorTarget, boolean first) {
        // If we are getting the last arg, reverse the list.
        if (!first) {
            argList = new ArrayList<PartInput>(argList);
            Collections.reverse(argList);
        }
        
        // Iterate over the list, returning the first arg which targets the collector.
        for (final PartInput arg : argList) {
            if (GemGraph.getInputArgumentTarget(arg) == collectorTarget) {
                return arg;
            }
        }
        
        // No args in the list target the collector.
        return null;
    }

    /**
     * Given a set of arguments which target some collector(s), determine the best collector for any new inputs to target.
     * Assumption: all the inputs in the list to consider are in the same gem tree, and have not been disconnected from their targets.
     * 
     * @param inputsToConsider the inputs to consider when calculating the best collector 
     *   for their arguments to target.  eg. if these all target a given collector, that collector will be returned.
     * @return CollectorGem the best collector for an input to target, considering the given inputs.  
     *   The target gem if the inputs target more than one collector.
     *   Null if the inputs have no enclosing collectors.
     */
    private static CollectorGem getCollectorToTarget(Collection<PartInput> inputsToConsider) {

        // If all the arguments target a collector, use that one.  Otherwise use the outermost collector.
        CollectorGem collectorToTarget = null;

        boolean firstIteration = true;
        for (final PartInput nextOldGemInput : inputsToConsider) {

            CollectorGem inputTargetedCollector = GemGraph.getInputArgumentTarget(nextOldGemInput);

            if (firstIteration) {
                // the first iteration..
                collectorToTarget = inputTargetedCollector;
                firstIteration = false;

            } else if (collectorToTarget != inputTargetedCollector) {
                // Input args target more than one collector.  Return the outermost collector (if any).
                return GemGraph.obtainOutermostCollector(nextOldGemInput.getGem());

            } else {
                // this arg targets the same collector as all the other ones.
                // Do nothing.
            }
        }
        
        return collectorToTarget;
    }
    
    /**
     * Retarget the arguments for a set of inputs according to the tree on which they appear.
     *   When connecting or disconnecting, a gem tree will gain some inputs, which remain to be retargeted.
     *   This method attempts to infer argument information (eg. arg position) from the inputs around the new inputs.
     *   For instance, if an input coming before the set of added inputs (with respect to the tree) targets the target 
     *     collector, and the added inputs are determined to also target the target collector, the arguments for the added 
     *     inputs will be placed after the argument for the input which precedes it in the tree.
     * 
     * @param newInputList the list of inputs on the tree.
     * @param firstNewInputIndex the index of the first new input whose argument should be retargeted.
     * @param lastNewInputIndex the index of the last new input whose argument should be retargeted.
     * @return the CollectorGem to which the arguments were targeted, or null if no argument (re)targeting took place.
     */
    private CollectorGem retargetInputArgumentsForTree(List<PartInput> newInputList, int firstNewInputIndex, int lastNewInputIndex) {

        // Try to infer the position of the new arguments from the other inputs.
        // The new arguments.
        Set<PartInput> newArgSet = new LinkedHashSet<PartInput>();
        
        // Gather the new args.
        for (final PartInput newInput : newInputList.subList(firstNewInputIndex, lastNewInputIndex + 1)) {
            newArgSet.add(newInput);
        }
        
        if (newArgSet.isEmpty()) {
            return null;
        }

        // Determine the new arguments
        // TODOEL: If all the arguments in this subtree are targeting a collector, should that be the collectorToTarget?
        //   We should at least do this in the case where there is only one replacing input.
        Gem rootGem = newArgSet.iterator().next().getGem().getRootGem();
        CollectorGem collectorToTarget =  (!(rootGem instanceof CollectorGem)) ? null : targetCollector;  // Can this (sometimes) be rootGem?

        if (collectorToTarget == null) {
            return null;
        }
        
        // Now add the arguments to the target.

        // Walk backwards over the inputs coming before the replaced input, looking for an input which targets the target collector.
        // If we find one, add all the arguments after that input.
        List<PartInput> beforeInputs = newInputList.subList(0, firstNewInputIndex);
        PartInput beforeInputArray[] = (new ArrayList<PartInput>(beforeInputs)).toArray(new PartInput[firstNewInputIndex]);
        for (int i = firstNewInputIndex - 1; i > -1; i--) {
            PartInput inputArgument = beforeInputArray[i];
            if (GemGraph.getInputArgumentTarget(inputArgument) == collectorToTarget) {
                collectorToTarget.addArguments(inputArgument, newArgSet, true);
                return collectorToTarget;
            }
        }

        // Walk forward over the inputs coming after the replaced input, looking for an input which targets the target collector.
        // If we find one, add all the arguments before that input.
        if (lastNewInputIndex + 1 < newInputList.size()) {
            List<PartInput> afterInputs = newInputList.subList(lastNewInputIndex + 1, newInputList.size());
            for (final PartInput inputArgument : afterInputs) {
                if (GemGraph.getInputArgumentTarget(inputArgument) == collectorToTarget) {
                    collectorToTarget.addArguments(inputArgument, newArgSet, false);
                    return collectorToTarget;
                }
            }
        }
        
        // Just add the args wherever the CALSourceGenerator would naturally put them.
        addTargetArgsInSourceOrder(collectorToTarget, newArgSet);
        return collectorToTarget;
    }
    
    /**
     * Add the given arguments to the target, in the order in which they appear in the source. 
     *   If they do not appear in the source, they are simply added to the end of the target's argument list.
     * @param collectorToTarget the target to which the arguments will be added.
     * @param newArgSet the arguments which are retargeted to the target collector.
     *   Must not be empty.
     */
    private void addTargetArgsInSourceOrder(CollectorGem collectorToTarget, Set<PartInput> newArgSet) {

        // Get the arguments in the order returned by the CALSourceGenerator.
        CompositionNode.CompositionArgument[] argsFromSourceGen = CALSourceGenerator.getFunctionArguments(collectorToTarget);
        CompositionNode.CompositionArgument firstArg = newArgSet.iterator().next();
        
        // Find where the first argument appears.
        int firstArgPosition = Arrays.asList(argsFromSourceGen).indexOf(firstArg);
        if (firstArgPosition < 0) {
            // The args don't appear in the source.  Add to the end.
            collectorToTarget.addArguments(newArgSet);

        } else if (firstArgPosition == 0) {
            // Add to the beginning
            collectorToTarget.addArguments(0, newArgSet);

        } else {
            // Add after the argument which comes before it in source gen.
            List<PartInput> targetedCollectorArgList = collectorToTarget.getTargetArguments();
            PartInput argBefore = (PartInput)argsFromSourceGen[firstArgPosition - 1];
            
            if (targetedCollectorArgList.contains(argBefore)) {
                collectorToTarget.addArguments(argBefore, newArgSet, true);
            } else {
                // The "argBefore" doesn't appear in the list if it's an unaccounted-for arg.
                collectorToTarget.addArguments(newArgSet);
            }
        }
    }

    /**
     * Update the gem graph to reflect any changes in the inputs targeting the given collectors.
     * This includes updating their reflected inputs, updating their reflectors, and propagating new targeting changes
     *   which might come about from newly-created reflector inputs.
     * eg. If a reflector update would cause an argument to be added to a collector gem, that collector's reflectors
     *     will be updated as well (and so on..).
     * 
     * @param affectedCollectors the collectors for which reflectors will be updated.
     */
    void updateForArgumentChange(Set<CollectorGem> affectedCollectors) {
        
        if (disableArgumentUpdating) {
            return;
        }

        for (final CollectorGem affectedCollector : affectedCollectors) {
            Map<ReflectorGem, PartInput[]> affectedReflectorsToOldInputsMap = affectedCollector.updateReflectedInputs();
            
            // Take care of updating arguments for collectors affected by additional reflector inputs.
            // Note that an infinite loop shouldn't occur, since new reflector inputs would be targeted at the target gem.
            for (final Map.Entry<ReflectorGem, PartInput[]> mapEntry : affectedReflectorsToOldInputsMap.entrySet()) {
                ReflectorGem reflectorGem = mapEntry.getKey();
                retargetArgumentsForDefinitionChange(reflectorGem, mapEntry.getValue());
            }
        }
    }

}