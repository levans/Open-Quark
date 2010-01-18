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
 * GemGraph.java
 * Creation date: (10/18/00 1:00:12 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.openquark.cal.compiler.CALSourceGenerator;
import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.PolymorphicVarContext;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.valuenode.DefaultValueNodeTransformer;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;
import org.openquark.cal.valuenode.ValueNodeTransformer;
import org.openquark.gems.client.Gem.PartConnectable;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;


/**
 * A graph of gems (the supercombinator we're building).
 * Terminology:
 *   Tree - a set of gems connected to each other (visually)
 *   Forest - a set of trees which are connected to each other via collector-emitter pairs.
 *   Root - the ancestor of all other gems in a tree.  It's either a collector or a
 *          functional agent gem with an unconnected output.
 *   GemGraph - the set of all gems we're working with.  Forms a partition set on forests present.
 * 
 * @author Luke Evans
 */
public class GemGraph implements TypeStringProvider {
   
    /** The target collector for this gem graph. */
    private final CollectorGem targetCollector;
    
    /** Used for creating new ValueNodes */
    private ValueNodeBuilderHelper valueNodeBuilderHelper;
    
    /** Used for transforming the ValueNodes (again for the validation of the ValueGem) */
    private final ValueNodeTransformer valueNodeTransformer;

    /** The set of gems that we are currently working with. */
    private Set<Gem> gemSet;
    
    /** The connections between gems. */
    private final Set<Connection> connectionSet;
    
    private final Set<CollectorGem> collectorSet;

    /** Used to track type and row variables in the types represented in all gems in the GemGraph  */
    private PolymorphicVarContext polymorphicVarContext = PolymorphicVarContext.make();

    /** the index of the next collector to create */
    private int nextCollectorIndex = 1;

    /** The index of the next code gem to create. */
    private int nextCodeGemIndex = 1;

    /** Listeners for gem connection events. */
    private GemConnectionListener gemConnectionListener;

    private final List<GemGraphChangeListener> gemGraphChangeListeners;

    /** The object responsible for retargeting arguments in response to certain gem model events. */
    private final GemGraphArgumentManager argumentManager;

    /** A StateEditable used to undo argument changes resulting from intermediate states used during type checking. */
    private final CollectorArgumentStateEditable collectorArgumentStateEditable;
    
    /**
     * A visitor that visits connected gems in a gem graph.
     * Creation date: (01/22/02 4:52:22 PM)
     * @author Edward Lam
     */
    public static abstract class GemVisitor {
        
        /** The scope of visitor.*/
        private final TraversalScope traversalScope;
        
        /**
         * The target collector for the scope to explore.
         *   Gems outside of the scope defined by the target collector will not be traversed.
         *   If null, all gems will be traversed.
         */
        private final CollectorGem targetCollector;
        
        /**
         * Constructor for a GemVisitor
         * 
         * @param traversalScope the scope of this visitor.
         * @param targetCollector the target collector for the scope to explore.
         *   Gems outside of the scope defined by the target collector will not be traversed.
         *   If null, all gems will be traversed.
         */
        public GemVisitor(TraversalScope traversalScope, CollectorGem targetCollector) {
            this.traversalScope = traversalScope;
            this.targetCollector = targetCollector;
        }
    
        /**
         * Notify the visitor that we have visited a gem.
         * @param gemVisited Gem the visited gem
         * @return boolean true if we can stop traversing
         */
        public abstract boolean visitGem(Gem gemVisited);
        
        /**
         * Get the scope of this visitor.
         * @return the scope of this visitor.
         */
        public final TraversalScope getTraversalScope() {
            return traversalScope;
        }
        
        /**
         * Get the target collector for this visitor.
         * @return The target collector for the scope to explore.
         *   Gems outside of the scope defined by the target collector will not be traversed.
         *   If null, all gems will be traversed.
         */
        public final CollectorGem getTargetCollector() {
            return targetCollector;
        }
    }
    
    /**
     * Traversal scope enum pattern.
     * @author Edward Lam
     */
    public static final class TraversalScope {

        private final String typeString;

        /**
         * Constructor for a traversal scope.
         * @param typeString user-readable name for the type.
         */
        private TraversalScope(String typeString) {
            this.typeString = typeString;
        }

        @Override
        public String toString() {
            return typeString;
        }

        /** Traverses the tree only. */
        public static final TraversalScope TREE         = new TraversalScope("Tree");

        /** Traverses the tree plus other trees connected by collector-emitter pairs, within a given scope. */
        public static final TraversalScope FOREST       = new TraversalScope("Forest");  
    }

    /**
     * Input collect mode enum pattern.
     * Creation date: (02/08/02 2:07:00 PM)
     * @author Edward Lam
     */
    public static final class InputCollectMode {
        public static final InputCollectMode ALL          = new InputCollectMode ();
        public static final InputCollectMode BURNT_ONLY   = new InputCollectMode ();
        public static final InputCollectMode UNBURNT_ONLY = new InputCollectMode ();
    }

    /**
     * Constructor for a GemGraph.
     */
    public GemGraph() {

        // Create the Sets and Maps for the gems and connections
        gemSet = new HashSet<Gem>();
        connectionSet = new HashSet<Connection>();
        collectorSet = new HashSet<CollectorGem>();        
        valueNodeTransformer = new DefaultValueNodeTransformer();
        gemGraphChangeListeners = new ArrayList<GemGraphChangeListener>();
        collectorArgumentStateEditable = new CollectorArgumentStateEditable(this);
        

        // Create the target.
        this.targetCollector = new CollectorGem(null);
        
        // Instantiate the argument manager
        argumentManager = new GemGraphArgumentManager(targetCollector);

        // Add the target to the gem graph.
        addGem(targetCollector);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder ("GemGraph:\n");
        for (final Gem rootGem : getRoots()) {
            displayGemSubtreeText (rootGem, sb, 0);
        }

        return sb.toString();
    }

    /**
     * Display a gem tree to the console
     * @param gem the root of the gem tree to display.
     */
    public static void displayGemSubtree(Gem gem) {

        StringBuilder sb = new StringBuilder();
        if (gem instanceof CollectorGem) {
            CollectorGem collector = (CollectorGem) gem;
            sb.append(collector.getUnqualifiedName() + ":\n");
        }
        
        displayGemSubtreeText(gem, sb, 0);
        System.out.println(sb.toString());
    }

    /**
     * A helper function for showing the gem graph as a string.
     */
    public static void displayGemSubtreeText(Gem gem, StringBuilder sb, int indentLevel) {
        final String indentString = "    ";
        for (int indentN = 0; indentN < indentLevel; ++indentN) {
            sb.append(indentString);
        }

        sb.append(gem.toString());
        sb.append('\n');

        ++indentLevel;

        Gem.PartInput [] inputs = gem.getInputParts();

        for (int i = 0; i < inputs.length; ++i) {
            Gem.PartInput input = inputs[i];
            if (input.isBurnt()) {
                for (int indentN = 0; indentN < indentLevel; ++indentN) {
                    sb.append(indentString);
                }

                sb.append(input.getArgumentName().getCompositeName());
                sb.append(" :: ");
                sb.append(input.getType());
                sb.append("  <burnt>\n");

            } else if (!input.isConnected()) {
                for (int indentN = 0; indentN < indentLevel; ++indentN) {
                    sb.append(indentString);
                }

                sb.append(input.getArgumentName().getCompositeName());
                sb.append(" :: ");
                sb.append(input.getType().toString());
                sb.append("  <free>\n");

            } else {
                displayGemSubtreeText(input.getConnection().getSource().getGem(), sb, indentLevel);
            }
        }
    }

    /**
     * Insert a Gem into this GemGraph
     * @param gem Gem the Gem to insert
     * @return Gem the Gem we just added (for convenience)
     * @throws IllegalArgumentException if the gem to be added is already connected to other gems (since
     * these other gems will not be in the gem graph) or if the gem is already in the gem graph.
     */
    public Gem addGem(Gem gem) {
        if (gem == null) {
            throw new NullPointerException();
        }

        // Gems must be unconnected if you want to add them.
        if (gem.isConnected()) {
            throw new IllegalArgumentException("Attempt to add an already connected gem to the gem graph");
        }
        
        // Check that the gem name (if any) doesn't conflict with any gems already in the gem graph.
        if (gem instanceof CodeGem) {
            CodeGem codeGem = (CodeGem)gem;
            if (codeGem.isNameInitialized() && getCodeGemNames().contains(((CodeGem)gem).getUnqualifiedName())) {
                throw new IllegalArgumentException("Attempt to add a code gem to a gem graph which already contains a code gem with the same name.");
            }
        } else if (gem instanceof CollectorGem) {
            CollectorGem collectorGem = (CollectorGem)gem;
            if (collectorGem.isNameInitialized() && getCollectorNames().contains(((CollectorGem)gem).getUnqualifiedName())) {
                throw new IllegalArgumentException("Attempt to add a collector gem to a gem graph which already contains a collector gem with the same name.");
            }
        }

        // Add the Gem.  If the gem was already in the set then throw illegal argument exception.
        if (!gemSet.add(gem)) {
            throw new IllegalArgumentException("Attempt to add the gem twice to the gem graph");
        }
    
        // special handling for various gems
        if (gem instanceof CollectorGem) {
    
            CollectorGem cGem = (CollectorGem)gem;
            
            // Try to assign the default name here.  The function will make sure that this only gets done once so
            // we don't need to worry about the counter getting thrown off if we do this more than once.
            assignDefaultName(cGem);    

            // add this collector to the collectorSet
            collectorSet.add(cGem);
            
            // re-target the input argument.
            argumentManager.retargetArgumentsOnAdd(cGem);
            
            // Ensure that the collector has a target.
            if (cGem != targetCollector && cGem.getTargetCollector() == null) {
                cGem.setTargetCollector(targetCollector);
            }

            // Verify that the collector doesn't have any emitters yet
            if (!cGem.getReflectors().isEmpty()) {
                throw new IllegalArgumentException("A collector can only be added to the gem graph if it has no emitters.");
            }
    
        } else if (gem instanceof ReflectorGem) {
    
            ReflectorGem rGem = (ReflectorGem)gem;
            CollectorGem cGem = rGem.getCollector();
            cGem.addReflector(rGem);

            // Verify that the collector for this reflector is already in the gem graph
            if (!gemSet.contains(cGem) || !collectorSet.contains(cGem)) {
                throw new IllegalArgumentException("Attempt to add a reflector to the gem graph without first adding the collector");
            }

        } else if (gem instanceof CodeGem) {
            // Assign the default name here.
            assignDefaultName((CodeGem)gem);
        }
    
        // notify listeners.
        for (final GemGraphChangeListener listener : gemGraphChangeListeners) {
            listener.gemAdded(new GemGraphAdditionEvent(gem));
        }
        
        return gem;
    }
    
    /**
     * Remove a Gem from this GemGraph.  Also invokes its cleanup code.
     * Not "exceptional" if the gem is already removed.
     * @param gem the Gem to remove
     * @throws IllegalArgumentException if the gem to be removed is still connected to other gems or
     * if it does not exist in this gem graph.
     */
    public void removeGem(Gem gem) {
        
        // Disallow deletion of the target gem.
        if (gem == targetCollector) {
            GemCutter.CLIENT_LOGGER.log(Level.WARNING, "Attempt to delete the target gem from the gem graph.");
            return;
        }
        
        // Gems must be unconnected if you want to remove them
        if (gem.getOutputPart() != null && gem.getOutputPart().isConnected()) {
            throw new IllegalArgumentException("Attempt to remove a gem that is still connected");
        }
                
        PartInput[] inputs = gem.getInputParts();
        for (int i=0; i<inputs.length; ++i) {
            if (inputs[i].isConnected()) {
                throw new IllegalArgumentException("Attempt to remove a gem that is still connected");
            }
        }
        
        if (gem instanceof CollectorGem) {
            CollectorGem collector = (CollectorGem)gem;

            // Remove the collector's argument from its target.
            // We must do this before removing the collector, since the gem needs to be in the gem graph for argument target
            //   resolution to happen properly.
            argumentManager.retargetArgumentsOnRemove(collector, targetCollector);
        }

        // Remove the Gem (if the gem was not in the set then throw illegal argument exception)
        if (!gemSet.remove(gem)) {
            throw new IllegalArgumentException("Attempt to remove a gem that does not exist in the gem graph");
        }

        if (gem instanceof ReflectorGem) {
            // Remove this gem from the collector
            ReflectorGem rGem = (ReflectorGem)gem;
            CollectorGem cGem = rGem.getCollector();
            cGem.removeReflector(rGem);

        } else if (gem instanceof CollectorGem) {
            CollectorGem collector = (CollectorGem)gem;

            // Do some error checking first
            if (!collectorSet.contains(collector)) {
                throw new IllegalStateException("Attempt to remove a collector from the gem graph that is not in the collector set");
            }
            if (!collector.getReflectors().isEmpty()) {
                throw new IllegalArgumentException("All emitters for a collector must be removed from the gem graph before removing the collector");
            }
            
            // Remove this gem from the collectors set
            collectorSet.remove(collector);
        }
    
        for (final GemGraphChangeListener listener : gemGraphChangeListeners) {
            listener.gemRemoved(new GemGraphRemovalEvent(gem));
        }
    }
    
    /**
     * Make a new connection between two gems.
     * 
     * @param partOutput
     * @param partInput
     */
    public void connectGems (Gem.PartOutput partOutput, Gem.PartInput partInput) {
        connectGems(new Connection(partOutput, partInput));
    }
    
    /**
     * Make a connection using an existing connection.  In order to facilitate proper undo and redo we
     * need to be able to add back an existing connection object rather than create a new connection.  
     * Emitters will also be updated as appropriate
     * 
     * @param conn the existing connection
     * @throws IllegalArgumentException if either of the input or output parts are already connected or
     * are burnt.  Also throws IllegalArgumentException if the gems to which the input or output parts
     * belong are not in the gem graph. 
     */
    public void connectGems(Connection conn) {
        // Do some error checking here
        Gem.PartOutput from = conn.getSource();
        Gem.PartInput to = conn.getDestination();

        if (from.isConnected()
                || to.isConnected()
                || to.isBurnt()) {
            throw new IllegalArgumentException("Attempt to make an illegal gem connection");
        }

        if (!gemSet.contains(from.getGem())) {
            throw new IllegalArgumentException("Attempt to make an illegal gem connection");
        }

        if (!gemSet.contains(to.getGem())) {
            throw new IllegalArgumentException("Attempt to make an illegal gem connection");
        }

        if (!to.isValid()) {
            throw new IllegalArgumentException("Attempt to connect to an invalid input");
        }
        
        // Add the connection to the graph
        connectionSet.add(conn);
    
        // Let the Gems know that their interfaces are bound
        from.bindConnection(conn);
        to.bindConnection(conn);

        // Handle argument retargeting.
        argumentManager.retargetForConnect(conn);
        
        // Send all interested listeners the notification of connection 
        if (gemConnectionListener != null) {
            gemConnectionListener.connectionOccurred(new GemConnectionEvent(conn, GemConnectionEvent.EventType.CONNECTION));
        }
        
        for (final GemGraphChangeListener listener : gemGraphChangeListeners) {
            listener.gemConnected(new GemGraphConnectionEvent(conn));
        }
    }
    
    /**
     * Return whether making a given connection will result in a valid gem graph.
     * @param conn the connection to make.
     * @param typeCheckInfo the type check info to use to validate the gem graph.
     * @return whether making a given connection will result in a valid gem graph.
     */
    public boolean canConnect(Connection conn, TypeCheckInfo typeCheckInfo) {
        
        Gem.PartOutput from = conn.getSource();
        Gem.PartInput to = conn.getDestination();

        // Check that the connecting parts aren't connected or burnt
        if (from.isConnected() || to.isConnected() || to.isBurnt()) {
            return false;
        }

        // Check that both gems are in the gem graph.
        if (!(gemSet.contains(from.getGem()) && gemSet.contains(to.getGem()))) {
            return false;
        }

        // Check that the destination is valid.
        if (!to.isValid()) {
            return false;
        }
        
        // Temporarily bind the parts to test.
        from.bindConnection(conn);
        to.bindConnection(conn);
        
        // Calculate whether any collector arguments were affected.
        boolean argumentsAffected = getInputArgumentTarget(to) != null;

        // Grab the gem graph's current collector and argument state.
        Hashtable<Object, Object> state = null;
        if (argumentsAffected) {
            state = new Hashtable<Object, Object>();
            collectorArgumentStateEditable.storeState(state);
        }

        // Handle argument retargeting.
        argumentManager.retargetForConnect(conn);
        
        // Check for graph validity..
        boolean canConnect = checkGraphValid(typeCheckInfo);

        // Revert any changes we made.
        from.bindConnection(null);
        to.bindConnection(null);
        if (argumentsAffected) {
            collectorArgumentStateEditable.restoreState(state);
        }

        // Return the result.
        return canConnect;
    }
    
    /**
     * Return the collector which is targeted by the given input argument.
     *   This method simply asks all enclosing collectors whether it contains a given input as a targeting argument.
     * Note: when disconnecting, this method should be called *before* the disconnection takes place.
     * @param inputArgument the argument in question.
     * @return the collector targeted by this argument, or null if no target can be found.
     */
    public static CollectorGem getInputArgumentTarget(Gem.PartInput inputArgument) {
        for (CollectorGem enclosingCollector = inputArgument.getGem().getRootCollectorGem(); 
             enclosingCollector != null; enclosingCollector = enclosingCollector.getTargetCollectorGem()) {
            
            if (enclosingCollector.isTargetedBy(inputArgument)) {
                return enclosingCollector;
            }
        }
        
        return null;
    }
    
    /**
     * Analyze the current gem graph for inconsistencies in arguments targeting its collectors,
     *   and try to fix them up.
     * An example of an inconsistency is when a collector holds onto a particular argument as a targeting argument,
     *   but that argument's input is not enclosed by that collector.
     */
    void validateInputTargets() {
        
        // Remove any arguments which collectors think target that collector, but whose inputs are not enclosed by it.
        // Also remove any arguments which are connected.
        for (final CollectorGem collectorGem : getCollectors()) {
            for (final PartInput targetingArgument : collectorGem.getTargetArguments()) {
                if (targetingArgument.isConnected() || getInputArgumentTarget(targetingArgument) != collectorGem) {
                    collectorGem.removeArgument(targetingArgument);
                }
            }
        }

        // Create a list of arguments whose inputs are rooted by collectors, but don't have targets.
        List<PartInput> untargetedArguments = new ArrayList<PartInput>();
        
        for (final Gem rootGem : getRoots()) {
            if (rootGem instanceof CollectorGem) {
                // Get the unburnt inputs on the descendant subtree.
                List<PartInput> unburntTreeInputs = 
                        obtainUnboundDescendantInputs(rootGem, TraversalScope.TREE, InputCollectMode.UNBURNT_ONLY);
    
                // Iterate over the inputs.
                for (final Gem.PartInput unburntTreeInput : unburntTreeInputs) {
    
                    // If the argument is unbound, add it to the list.
                    if (getInputArgumentTarget(unburntTreeInput) == null) {
                        untargetedArguments.add(unburntTreeInput);
                    }
                }
            }
        }
        
        // Now retarget the untargeted inputs to the target collector.
        // TODOEL: update any reflectors reflecting the target collector.
        getTargetCollector().addArguments(getTargetCollector().getTargetArguments().size(), untargetedArguments);
    }
    
    /**
     * Disconnect the connection.
     * Emitters will also be updated as appropriate.
     * @param conn the connection to disconnect
     */
    public void disconnectGems(Connection conn) {
    
        Gem.PartConnectable from = conn.getSource();
        Gem.PartConnectable to = conn.getDestination();

        if (!gemSet.contains(from.getGem())) {
            throw new IllegalArgumentException("Attempt to make an illegal gem connection");
        }

        if (!gemSet.contains(to.getGem())) {
            throw new IllegalArgumentException("Attempt to make an illegal gem connection");
        }

        if (!connectionSet.contains(conn)) {
            throw new IllegalArgumentException("Attempt to make an illegal gem connection");
        }

        // Remove from the graph
        connectionSet.remove(conn);
    
        // Let the Gems know that their interfaces are unbound (null connection).
        from.bindConnection(null);
        to.bindConnection(null);
    
        argumentManager.retargetForDisconnect(conn);
        
        // Send all interested listeners the notification of disconnection  
        if (gemConnectionListener != null) {
            gemConnectionListener.disconnectionOccurred(new GemConnectionEvent(conn, GemConnectionEvent.EventType.DISCONNECTION));
        }
        for (final GemGraphChangeListener listener : gemGraphChangeListeners) {
            listener.gemDisconnected(new GemGraphDisconnectionEvent(conn));
        }
    }
    
    /**
     * Retarget an input argument from one collector to another.
     * @param collectorArgument the argument to retarget.
     * @param newTarget the collector to which the input will be retargeted.
     * @param addIndex the index at which the retargeted argument will be placed, or -1 to add to the end.
     * @return the old argument index, or -1 if there was no old target.
     */
    public int retargetInputArgument(PartInput collectorArgument, CollectorGem newTarget, int addIndex) {
        // Defer to the method in the argument retargeter.
        return argumentManager.retargetInputArgument(collectorArgument, newTarget, addIndex);
    }
    
    /**
     * Retarget the inputs on the given gem for a definition change.
     * @param changedGem the gem whose definition changed.
     * @param oldInputs the inputs from before the change.
     */
    public void retargetArgumentsForDefinitionChange(Gem changedGem, PartInput[] oldInputs) {
        argumentManager.retargetArgumentsForDefinitionChange(changedGem, oldInputs);
    }
    
    /**
     * Determine a composite argument name (unique to the forest) for each free input in the 
     *   GemGraph and save the names into the appropriate PartInput.
     * TODOEL: update for scoping.  It is not necessary for names of all free inputs to be unique within the forest,
     *   only free inputs which show up as arguments at their scoping level.
     */
    private void checkArgNamesDisambiguated() {

        // Iterate through the roots and remember the ones that have been looked at already.
        Set<Gem> checkedRoots = new HashSet<Gem>();
        for (final Gem root : getRoots()) {
            
            // Check to see if this root has been "checked" already - if so then continue on to the next root.
            if (checkedRoots.contains(root)) {
                continue;
            }
            
            // Get the forest roots for this Gem and add them to the set of "checked" roots
            Set<Gem> forestRoots = obtainForestRoots(root, true);
            checkedRoots.addAll(forestRoots);
            
            // Iterate through the forest roots.
            // Collect all free inputs into a big input list which is to be disambiguated wrt each other.
            // Also build a set of all the CollectorGem names.
            List<PartInput> inputList = new ArrayList<PartInput>();
            Set<String> collectorNames = new HashSet<String>();
            for (final Gem treeRoot : forestRoots) {

                if (treeRoot instanceof CollectorGem) {
                    collectorNames.add(((CollectorGem)treeRoot).getUnqualifiedName());
                }
                inputList.addAll(obtainUnboundDescendantInputs(treeRoot, TraversalScope.TREE, InputCollectMode.ALL));
            }

            disambiguateCompositeArgNames(inputList, collectorNames);
        }
    }
    
    /**
     * Helper for disambiguateCompositeArgNames().
     * Determines the composite names for a collection of PartInputs all belonging to the same forest.
     * The names will be saved to the appropriate PartInput.
     * @param inputList list of PartInputs for which to calculate composite arg names
     * @param existingNames list of names that generated names must not conflict with (ie: collector names)
     */
    private static void disambiguateCompositeArgNames(List<PartInput> inputList, Set<String> existingNames) {

        Map<String, Integer> nameToFrequency = new HashMap<String, Integer>();
        Map<String, Integer> savedNameToFrequency = new HashMap<String, Integer>();
        Set<String> savedNames = new HashSet<String>();
        Set<String> nakedCollectorNames = new HashSet<String>();
        
        // Seed the savedNames set and the savedNameToFrequency and nameToFrequency maps
        // with the elements of the existingNames set - this ensures that generated names/suffixes
        // will not collide with any existing names.
        savedNames.addAll(existingNames);
        for (final String currentName : existingNames) {
            
            int freq = 1;
            if (nameToFrequency.containsKey(currentName)) {
                freq = nameToFrequency.get(currentName).intValue() + 1;
            }
            
            nameToFrequency.put(currentName, Integer.valueOf(freq));
            savedNameToFrequency.put(currentName, Integer.valueOf(freq));
        }

        // Iterate through the inputs and determine the frequency of each name.
        // At the same time add the names that were saved by users to the set of saved names and
        // also build a set of naked collector names.
        for (final PartInput currInput : inputList) {
            
            boolean baseNameSavedByUser = true;
            String baseName = currInput.getDesignMetadata().getDisplayName();
            
            if (baseName == null) {
                baseName = currInput.getOriginalInputName();
                baseNameSavedByUser = false;
            }
            
            // Update the set of names of unconnected ("naked") collectors.
            if (currInput.getGem() instanceof CollectorGem) {
                nakedCollectorNames.add(baseName);
            }

            // If the name was saved by a user or is from a naked collector then add it to the
            // saved names set. Also update the frequency count for saved names.
            //
            // NOTE: We treat naked collector input names as though they were user saved so that
            // other args with the same saved base name will have suffixes.
            if (baseNameSavedByUser || currInput.getGem() instanceof CollectorGem) {
                
                savedNames.add(baseName);
                
                int freq = 1;
                if (savedNameToFrequency.containsKey(baseName)) {
                    freq = savedNameToFrequency.get(baseName).intValue() + 1;
                }
                
                savedNameToFrequency.put(baseName, Integer.valueOf(freq));
            }
            
            // Update the frequency count.           
            int freq = 1;
            if (nameToFrequency.containsKey(baseName)) {
                freq = nameToFrequency.get(baseName).intValue() + 1;
            }
            
            nameToFrequency.put(baseName, Integer.valueOf(freq));            
        }
        
        // A map to keep track of the next ordinal to used as a suffix for a given base name.
        Map<String, Integer> nameToSuffixMap = new HashMap<String, Integer>();
        
        // Pre-seed with the names of the naked collectors.
        // Naked collector names should never have a suffix, so we handle them first.
        for (final String nakedCollectorName : nakedCollectorNames) {
            nameToSuffixMap.put(nakedCollectorName, Integer.valueOf(0));
        }
        
        // Iterate through the inputs again but this time generate a composite name for each.
        for (final PartInput currInput : inputList) {
            
            boolean baseNameSavedByUser = true;
            String baseName = currInput.getDesignMetadata().getDisplayName();
            String suffix = "";
            
            if (baseName == null) {
                baseName = currInput.getOriginalInputName();
                baseNameSavedByUser = false;
            }
            
            // Only give the name a suffix if it doesn't belong to a naked collector.
            // Naked collector names never get changed.
            if (!(currInput.getGem() instanceof CollectorGem)) { 

                // If more than one input shares this name, a disambiguating suffix may be needed.
                if (nameToFrequency.get(baseName).intValue() > 1) {

                    // Figure out how often the user saved the same basename. If more than once,
                    // then we need to disambiguate the saved name. Otherwise it can stay the same
                    // and we only need to disambiguate the not user-saved basenames.
                    int saveFreq = 0;
                    if (baseNameSavedByUser) {
                        saveFreq = savedNameToFrequency.get(baseName).intValue();
                    } 
                    
                    // If the name is not user-saved or it is saved more than once, then disambiguate it.
                    if (!baseNameSavedByUser || saveFreq != 1) {

                        // Get the last suffix we used.
                        int ordinal = 0;
                        if (nameToSuffixMap.containsKey(baseName)) {
                            ordinal = nameToSuffixMap.get(baseName).intValue();
                        }

                        String generatedName = baseName;
                        
                        do {
                            ordinal++;
                            generatedName = baseName + "_" + ordinal;
                            
                            // Make sure the new generated name isn't one of the saved names and
                            // also doesn't exist as any other name. For example, there might
                            // be the names a_1 and a_2 as argument names for a gem in the forest.
                            // If the users saves two names as 'a', then a_1 and a_2 are not valid
                            // disambiguated names for those inputs. Instead they become a_3 and a_4.
                        } while (savedNames.contains(generatedName) || nameToFrequency.containsKey(generatedName));
                        
                        // Save the suffix we found.
                        suffix = "_" + ordinal;
                        nameToSuffixMap.put(baseName, Integer.valueOf(ordinal));
                    }
                }

                // Set the new name for the input.
                ArgumentName inputName = new ArgumentName(baseName);
                inputName.setDisambiguatingSuffix(suffix);
                inputName.setBaseNameSavedByUser(baseNameSavedByUser);
                
                currInput.setArgumentName(inputName);
            }
        }
    }       

    /**
     * Return a set of all the runnable gems in the graph.
     * @return the set of all runnable gems
     */
    public Set<Gem> getRunnableGems() {
        Set<Gem> runnableGems = new HashSet<Gem>();

        for (final Gem gem : gemSet) {
            if (gem.isRunnable()) {
                runnableGems.add(gem);
            }
        }
        
        return runnableGems;
    }
    
    /**
     * @param gem a gem
     * @return whether the gem graph contains the gem.
     */
    public boolean hasGem(Gem gem) {
        return gemSet.contains(gem);
    }
    
    /**
     * Return the set of Gems in this GemGraph.
     * @return the (unmodifiable) set of gems in the gem graph
     */
    public final Set<Gem> getGems() {
        return Collections.unmodifiableSet(gemSet);
    }

    /**
     * Return the set of Connections in this GemGraph.
     * @return the set of connections in the gem graph
     */
    public final Set<Connection> getConnections() {
        return new HashSet<Connection>(connectionSet);
    }

    /**
     * Get all rootGems in the GemGraph
     * @return all root gems in the GemGraph.
     */ 
    public Set<Gem> getRoots() {
        Set<Gem> rootSet = new HashSet<Gem>();
        for (final Gem nextGem : gemSet) {  
            Gem nextRoot = nextGem.getRootGem();
            if (nextRoot != null) {             // for unconnected target
                rootSet.add(nextGem.getRootGem());
            }
        }
        return rootSet;
    }

    /**
     * Get all value rootGems in the GemGraph
     * @return all value root gems in the GemGraph.
     */ 
    private Set<Gem> getValueRoots() {
        Set<Gem> rootSet = new HashSet<Gem>();
        for (final Gem nextGem : gemSet) {
            Gem nextRoot = nextGem.getRootGem();
            if (nextRoot instanceof ValueGem) {
                rootSet.add(nextGem.getRootGem());
            }
        }
        return rootSet;
    }

    /**
     * Returns the CAL source corresponding to the gem graph
     * @return SourceModel.FunctionDefn the corresponding source model.
     */
    public SourceModel.FunctionDefn.Algebraic getCALSource() {
        return CALSourceGenerator.getFunctionSourceModel(getTargetCollector().getUnqualifiedName(), getTargetCollector(), Scope.PUBLIC);
    }
    
    /**
     * Get all collectors in the GemGraph
     * @return all collector gems in the GemGraph.
     */
    public Set<CollectorGem> getCollectors(){
        return new HashSet<CollectorGem>(collectorSet);
    }
    
    /**
     * Get this gem graph's target collector.
     * @return the target collector for this gem graph.
     */
    public CollectorGem getTargetCollector() {
        return targetCollector;
    }
    
    /**
     * Organize the collectors so that outer collectors come before inner ones.
     * @param collectorsToOrder the collectors to be ordered.
     */
    public static Set<CollectorGem> getOrderedCollectorSet(Collection<CollectorGem> collectorsToOrder) {

        Set<CollectorGem> orderedCollectorSet = new LinkedHashSet<CollectorGem>();
        Set<CollectorGem> collectorsToAdd = new HashSet<CollectorGem>(collectorsToOrder);

        while (!collectorsToAdd.isEmpty()) {
        
            // Create a set with the collectors not yet added from this iteration.
            Set<CollectorGem> collectorsToAddUpdated = new HashSet<CollectorGem>();
            
            // Iterate over the collectors to add.
            for (final CollectorGem collectorToAdd : collectorsToAdd) {
                CollectorGem collectorTarget = collectorToAdd.getTargetCollectorGem();

                if (collectorTarget == null || orderedCollectorSet.contains(collectorTarget)) {
                    // Add the collector if its target is already added.
                    orderedCollectorSet.add(collectorToAdd);

                } else if (!collectorsToAdd.contains(collectorTarget)) {
                    // The gem graph does not know about this collector's target.  What to do??
                    GemCutter.CLIENT_LOGGER.log(Level.WARNING, "Attempt to save a collector whose target is not in the gem graph.");
                    
                } else {
                    // If the target has not been added, save for the next iteration.
                    collectorsToAddUpdated.add(collectorToAdd);
                }
            }
            collectorsToAdd = collectorsToAddUpdated;
        }
        
        return orderedCollectorSet;
    }
    
    /**
     * Get all code gem (unqualified) names in the GemGraph.
     * @return all (unqualified) code gem names in the GemGraph.
     */
    public Set<String> getCodeGemNames(){
        Set<String> codeGemNames = new HashSet<String>();
    
        // iterate over the code gems.
        for (final Gem gem : gemSet) {
            if (gem instanceof CodeGem) {
                codeGemNames.add(((CodeGem)gem).getUnqualifiedName());
            }
        }
    
        return codeGemNames;
    }
    
    /**
     * Get all collector (unqualified) names in the GemGraph.
     * @return all (unqualified) collector names in the GemGraph.
     */
    public Set<String> getCollectorNames() {
        Set<String> collectorNames = new HashSet<String>();
    
        // iterate over the collectors
        Set<CollectorGem> collectorSet = getCollectors();
        for (final CollectorGem collectorGem : collectorSet) {
            collectorNames.add(collectorGem.getUnqualifiedName());
        }
    
        return collectorNames;
    }
    
    /**
     * Get all collectors in the GemGraph with a given name.
     * Note that we can temporarily have more than one collector with a given name
     * if we are changing it - this is just an invalid permanent state.
     * @param name String the name to check for.
     * @return all collector gems in the GemGraph with a given name
     */
    public Set<CollectorGem> getCollectorsForName(String name) {
        // set of collectors whose names match
        Set<CollectorGem> matchingCollectorSet = new HashSet<CollectorGem>();
    
        // iterate over the collectors
        Set<CollectorGem> collectorSet = getCollectors();
        for (final CollectorGem collectorGem: collectorSet) {
            if (name.equals(collectorGem.getUnqualifiedName())){
                matchingCollectorSet.add(collectorGem);
            }
        }
        return matchingCollectorSet;
    }

    /**
     * If the code gem hasn't had a name assigned yet then generate a default name and assign it.
     * @param codeGem the gem to which to assign a name
     * @return boolean - true if the name is set here, false if the name has already been set.
     */
    boolean assignDefaultName(CodeGem codeGem) {
        
        // Check that the code gem's name has not already been assigned
        if (!codeGem.isNameInitialized()) {
            
            // we must take care not to assign a pre-existing name
            Set<String> invalidNames = getCodeGemNames();
            
            // code gems and collector gems share a common namespace
            invalidNames.addAll(getCollectorNames());
            String defaultName;
            do {
                // As no name has yet been supplied, we make one up
                defaultName = "newCodeGem" + nextCodeGemIndex;
                nextCodeGemIndex++;
            } while (invalidNames.contains(defaultName));
    
            // assign the name
            codeGem.setName(defaultName);
            return true;
        }
        
        return false;
    }

    /**
     * If the collector hasn't had a name assigned yet then generate a default name for the let 
     * variable associated with a collector, and update the relevant associations.
     * @param collectorGem CollectorGem the gem to which to assign a name
     * @return boolean - true if the name is set here, false if the name has already been set.
     */
    boolean assignDefaultName(CollectorGem collectorGem) {
        
        // Check that the collector's name has not already been assigned
        if (!collectorGem.isNameInitialized()) {
            
            // we must take care not to assign a pre-existing name
            Set<String> invalidNames = getCollectorNames();
            
            // collectors and code gems share a common namespace
            invalidNames.addAll(getCodeGemNames());
            String defaultName;
            do {
                defaultName = "value" + nextCollectorIndex;
                nextCollectorIndex++;
            } while (invalidNames.contains(defaultName));
    
            // assign the name
            collectorGem.setName(defaultName);
            return true;
        }
        
        return false;
    }

    /**
     * Sort a collection of Gems that implement the NamedNode interface alphabetically by unqualified name
     * If tobeSorted items are not of type NamedGem, an Error is thrown
     * @param toBeSorted the collection of Gems that implement the NamedNode interface
     * that need sorting
     * @return a list of Gems from toBeSorted in alphabetical order by unqualified name
     */
    static List<Gem> sortNamedGemsInAlphabeticalOrder(Collection<Gem> toBeSorted) {
    
        //  A comparator for the NamedNode Interface that will
        //  compare the names of the nodes for sorting purposes
        final class NamedNodeComparator implements Comparator<Object> {
            
            public int compare(Object o1, Object o2) {
                // make sure o1 and o2 are really NamedNodes
                if (o1 instanceof NamedGem && o2 instanceof NamedGem) {
                    String qName1 = ((NamedGem)o1).getUnqualifiedName();
                    String qName2 = ((NamedGem)o2).getUnqualifiedName();
    
                    return qName1.compareTo(qName2);
    
                } else {
    
                    throw new Error("GemGraph.sortTargetsInAlphabeticalOrder(): Items are not NamedNodes.");
                }
            }
        }
    
        // create a list from toBeSorted, sort the list and return it
        NamedNodeComparator nodeComparator = new NamedNodeComparator(); 
        List<Gem> namedGemList = new ArrayList<Gem>(toBeSorted);  
        Collections.sort(namedGemList, nodeComparator);
        return namedGemList;
    }
    
    /**
     * Update the references to functional agents in the GemGraph.
     * @param workspace the workspace from which to obtain functional agent references
     */
    void updateFunctionalAgentReferences(CALWorkspace workspace) throws GemEntityNotPresentException {

        for (final Gem gem : gemSet) {
            if (gem instanceof FunctionalAgentGem) {
                ((FunctionalAgentGem)gem).updateGemEntity(workspace);
            }
        }
    }
    

    /**
     * Tells you if this gem forms the root of a gem tree/forest that is broken.
     * @param gem the gem whose descendants we should look at.
     * @param traversalScope the visitor scope.
     * @return boolean if this gem roots a gem tree that is broken
     */
    private static boolean isAncestorOfBrokenGem(Gem gem, TraversalScope traversalScope){
    
        // declare a visitor that can detect broken descendants
        class BrokenDescendantDetector extends GemVisitor {
            boolean brokenDescendantFound = false;
        
            BrokenDescendantDetector(TraversalScope traversalScope) {
                super(traversalScope, null);
            }

            @Override
            public boolean visitGem(Gem gemVisited){
    
                if (brokenDescendantFound) {
                    return true;
                }
                
                if (gemVisited.isBroken()) {
                    brokenDescendantFound = true;
                    return true;
                }
    
                return false;
            }
            
            boolean foundBrokenDescendant() {
                return brokenDescendantFound;
            }
        }
    
        // create a visitor of appropriate type
        BrokenDescendantDetector brokenDescendantDetector = new BrokenDescendantDetector(traversalScope);
    
        // visit and detect
        gem.visitDescendants(brokenDescendantDetector);
    
        return brokenDescendantDetector.foundBrokenDescendant();
    }
    
    /**
     * Tells you if this gem forms the root of a gem tree that is broken, or any
     * child trees are broken
     * @param gem the gem whose descendants we should look at.
     * @return boolean if this gem roots a gem forest that is broken
     */
    static boolean isAncestorOfBrokenGemForest(Gem gem){
        return isAncestorOfBrokenGem(gem, TraversalScope.FOREST);
    }
    
    /**
     * Tells you if this gem forms the root of a gem tree (not forest) that is broken.
     * @param gem the gem whose descendants we should look at.
     * @return boolean if this gem roots a gem tree that is broken
     */
    static boolean isAncestorOfBrokenGemTree(Gem gem){
        return isAncestorOfBrokenGem(gem, TraversalScope.TREE);
    }
    
    /**
     * Returns whether this gem and its descendants are all let gems (collectors and emitters and 0-argument reflectors).
     * @param gem Gem the gem whose descendants we should look at.
     * @return boolean true if this is the ancestor of a let chain
     */
    static boolean isLetChainAncestor(Gem gem){
        // declare a visitor that detects non-let gems
        class LetGemDetector extends GemVisitor {
            boolean foundNonLet = false;
            
            LetGemDetector() {
                super(TraversalScope.FOREST, null);                 // it's a forest visitor
            }
        
            @Override
            public boolean visitGem(Gem gemVisited){
                
                if (foundNonLet) {
                    return true;
                }
    
                // detect let
                if (!(gemVisited instanceof CompositionNode.Collector || 
                     (gemVisited instanceof CompositionNode.Emitter && gemVisited.getNInputs() == 0))) {

                    foundNonLet = true;
                    return true;
                }
                
                return false;
            }
            
            boolean foundLetChain() {
                return !foundNonLet;
            }
        }
    
        // detect chain of let gems
        LetGemDetector letGemDetector = new LetGemDetector();
        gem.visitDescendants(letGemDetector);
    
        return letGemDetector.foundLetChain();
    }
    
    
    /**
     * Obtain the root of a gem's tree, as well as the roots of its "ancestors".
     * @param gem Gem the gem whose ancestors to obtain.
     * @param excludeSet a set of roots to exclude from gathering
     * ie. if we find one of these, we ignore it and its ancestors.
     * @return the set of roots gathered
     */
    private static Set<Gem> obtainAncestorRoots(Gem gem, final Set<Gem> excludeSet){
    
        // declare a visitor that can collect root gems
        class AncestorRootObtainer extends GemVisitor {
            Set <Gem> rootSet = new HashSet<Gem>();
            
            AncestorRootObtainer() {
                super(TraversalScope.FOREST, null);                // it's a forest visitor
            }
        
            @Override
            public boolean visitGem(Gem gemVisited){
    
                if (excludeSet.contains(gemVisited)) {
                    return true;
                }
    
                // add if its a root
                if (gemVisited.isRootGem()) {
                    rootSet.add(gemVisited);
                }
                
                return false;
            }
            
            Set<Gem> getRoots() {
                return rootSet;
            }
        }
    
        // collect the root gems
        AncestorRootObtainer ancestorRootObtainer = new AncestorRootObtainer();
        gem.visitAncestors(ancestorRootObtainer);
    
        return ancestorRootObtainer.getRoots();
    }

    /**
     * Calculate the depth of a collector gem.
     * A collector with no target has depth 0.  
     * A collector which targets a collector with no target has depth 1.
     * A collector which targets a collector with depth 1 has depth 2.
     * etc.
     * 
     * @param collectorGem the collector gem for which to calculate the depth.
     * @return the "depth" of the collector gem.
     */
    static int getCollectorDepth(CollectorGem collectorGem) {
        int collectorDepth = 0;
        for (CollectorGem collectorTarget = collectorGem.getTargetCollectorGem(); 
             collectorTarget != null; 
             collectorTarget = collectorTarget.getTargetCollectorGem()) {
            
            collectorDepth++;
        }
        return collectorDepth;
    }

    /**
     * Determine if a set of collectors form a single line of targeting "ancestry".
     * What this means is that for any two collectors in the set, one of the collectors is enclosed by the other.
     * 
     * @param collectorSet the collectors to analyze.
     * @return true if the collectors in the set form a single line of targeting "ancestry".
     * Also true if the set is empty.
     */
    public static boolean formsAncestorChain(Set<CollectorGem> collectorSet) {
        
        // Create a map, sorted by key, from collector depth to collector gem.
        Map<Integer, CollectorGem> depthToAncestorMap = new TreeMap<Integer, CollectorGem>();
        for (final CollectorGem collectorGem : collectorSet) {
            // Add the mapping.
            int collectorDepth = getCollectorDepth(collectorGem);
            CollectorGem oldValue = depthToAncestorMap.put(Integer.valueOf(collectorDepth), collectorGem);
            
            // If there are duplicate mappings with different values, this can't be an ancestor chain.
            if (oldValue != null && oldValue != collectorGem) {
                return false;
            }
        }
        
        // Check that the collector at a given depth encloses the collector at the next depth.
        CollectorGem lastAncestor = null;
        for (final Integer depth : depthToAncestorMap.keySet()) {
            CollectorGem ancestor = depthToAncestorMap.get(depth);
            if (lastAncestor != null && !lastAncestor.enclosesCollector(ancestor)) {
                return false;
            }
            lastAncestor = ancestor;
        }
        
        return true;
    }
    
    /**
     * Returns all of the collectors enclosing a given gem.
     *   ie. the collector gem at the head of the tree, its target, its target's target, ...
     *   The iterator on this set will return the collectors in reverse scope order (ie. innermost collector first, outermost last).
     * @param gem the gem in question.
     * @return the enclosing collectors.  An iterator on this set will return the collectors in 
     *   reverse scope order.
     */
    public static Set<CollectorGem> obtainEnclosingCollectors(Gem gem) {
        Set<CollectorGem> enclosingCollectorSet = new LinkedHashSet<CollectorGem>();
        for (CollectorGem enclosingRoot = gem.getRootCollectorGem(); enclosingRoot != null; enclosingRoot = enclosingRoot.getTargetCollectorGem()) {
            enclosingCollectorSet.add(enclosingRoot);
        }
        
        return enclosingCollectorSet;
    }

    /**
     * Returns the outermost collector enclosing a given gem.
     * @param gem the gem in question.
     * @return the outermost enclosing collector.
     */
    public static CollectorGem obtainOutermostCollector(Gem gem) {
        CollectorGem outermostCollector = gem.getRootCollectorGem();
        if (outermostCollector != null) {
            
            for (CollectorGem enclosingCollector = outermostCollector.getTargetCollectorGem(); 
                 enclosingCollector != null; enclosingCollector = outermostCollector.getTargetCollectorGem()) {
                outermostCollector = enclosingCollector;
            }
        }
        
        return outermostCollector;
    }

    /**
     * Returns all of the descendents gems connected to this gem (returns the tree it resides in).
     * @param gem
     * @return the descendant gems.  
     *   An iterator on this set will return the gems in the order that they would be encountered 
     *   in a pre-order traversal of the descendant tree.
     */
    public static Set<Gem> obtainSubTree(Gem gem){
        return obtainSubTreeGems(gem, null);
    }

    /**
     * Returns all of the descendent gems connected to this gem (returns the tree it resides in).
     * If a gemClass is specified, then only gems of this type will be returned.
     * If gemClass is null, then all gems in the subtree will be returned.
     */
    public static Set<Gem> obtainSubTreeGems(Gem gem, final Class<?> gemClass) {
        class TreeObtainer extends GemVisitor {
                        
            // the set of connected gems
            Set<Gem> connectedGems = new LinkedHashSet<Gem>();
            
            /**
             * Constructor for the TreeObtainer
             */
            TreeObtainer () {
                super(TraversalScope.TREE, null);
            }
            
            /**
             * Implementation of the visit Gem Method
             * @param gemVisited
             * @return boolean
             */
            @Override
            public boolean visitGem(Gem gemVisited){
                if (gemClass == null || gemClass.isInstance(gemVisited)) {
                    connectedGems.add(gemVisited);    
                }
                return false;
            }
            
            /**
             * @return set of connected gems
             */
            public Set<Gem> getConnectedGems() {
                return connectedGems;
            }
        }
        
        // Create a new obtainer object
        TreeObtainer treeObtainer = new TreeObtainer();
        
        gem.visitDescendants(treeObtainer);
        return treeObtainer.getConnectedGems();
    }

    /**
     * Obtain roots of broken trees and their ancestor roots.
     * @param rootSet the set of roots on which to operate
     * @return the set of roots of broken trees and their ancestor roots.
     */
    static Set<Gem> obtainBrokenAncestorRoots(Set<Gem> rootSet) {
        // keep track of the roots we remove
        Set<Gem> brokenSet = new HashSet<Gem>();
    
        // iterate over the roots
        for (final Gem nextRoot : rootSet) {
            // skip this root if we already found it.
            if (brokenSet.contains(nextRoot)) {
                continue;
            }
            
            // add ancestors and anything it may define (ie. ancestors) to the broken set if the tree is broken
            if (isAncestorOfBrokenGemTree(nextRoot)) {
                brokenSet.addAll(obtainAncestorRoots(nextRoot, brokenSet));
            }
        }
        
        return brokenSet;
    }

    /**
     * Obtain all the roots of the trees of the forest of which a gem is part, including outer scopes.
     * 
     * @param gemFromForest a gem in the forest.
     * @param includeBroken boolean whether to include broken trees and their ancestors.
     * @return the set of roots gathered.
     */
    public static Set<Gem> obtainForestRoots(Gem gemFromForest, final boolean includeBroken){
        return obtainForestRoots(Collections.singleton(gemFromForest), includeBroken);
    }
    
    /**
     * Obtain all the roots of the trees of the forest of which a set of gems is part, including outer scopes.
     * 
     * @param gemsInForest gems in the forest.
     * @param includeBroken boolean whether to include broken trees and their ancestors.
     * @return the set of roots gathered.
     */
    public static Set<Gem> obtainForestRoots(Set<Gem> gemsInForest, final boolean includeBroken){
        // declare a visitor that can collect root gems
        class ForestRootObtainer extends GemVisitor {
            Set <Gem> rootSet = new HashSet<Gem>();
        
            ForestRootObtainer() {
                super(TraversalScope.FOREST, null);
            }

            @Override
            public boolean visitGem(Gem gemVisited){
                // add if its a root, and (if we care) not broken
                if (gemVisited.isRootGem()) {
                    if (includeBroken || !isAncestorOfBrokenGemTree(gemVisited)){
                        rootSet.add(gemVisited);
                    }
                }
                
                // never done early
                return false;
            }
            
            Set<Gem> getRoots() {
                return rootSet;
            }
        }
        // collect the root gems
        ForestRootObtainer forestRootObtainer = new ForestRootObtainer();
        for (final Gem gem : gemsInForest) {
            gem.visitGraph(forestRootObtainer);
        }
        return forestRootObtainer.getRoots();
    }
    
    /**
     * Obtain unconnected inputs of the Gem tree of which this Gem is the root as well as (optionally) in descendant subtrees at the
     *   same scoping level.
     * Note: outer (enclosing) scopes will not be traversed.
     * 
     * @param gem the gem whose descendants we should look at.
     * @param traversalScope the scope of descendant inputs to collect.
     * @param inputCollectMode Which inputs to collect - all, burnt only, or unburnt only
     * @return the List of unbound parts
     */
    public static List<PartInput> obtainUnboundDescendantInputs(Gem gem, TraversalScope traversalScope, final InputCollectMode inputCollectMode) {
    
        /*
         * A visitor that can collect descendant parts
         * How this works:
         *   When we iterate over the inputs, the unconnected ones that first appear are added to the unbound parts
         *   After a connected input, the inputs which appear after are saved, to appear after the inputs on the connected gem.
         *   After the last input is processed on a gem, we process any inputs saved to appear after the gem.
         *   If there are, we "pull them down" to appear after the last gem connected to an input on this gem.
         *   If there are no connected gems on this gem, then we can add all the saved inputs.
         */
        class DescendantPartObtainer extends GemVisitor {
    
            // the list of unbound parts
            List<PartInput> unboundParts = new ArrayList<PartInput>();
            
            // a map from a gem to a list of parts that should be added immediately after the gem's parts are added
            Map<Gem, List<PartInput>> addPartsAfterGemMap = new HashMap<Gem, List<PartInput>>();

            // the set of collectors whose inputs have been spoken for (but not necessarily traversed)
            Set<CollectorGem> inputCollectors = new HashSet<CollectorGem>();
    
            public DescendantPartObtainer(TraversalScope traversalScope, CollectorGem targetCollector) {
                super(traversalScope, targetCollector);
            }

            @Override
            public boolean visitGem(Gem gemVisited){
                Gem addAfterGem = null;

                // ignore if not at the target scoping level.
                CollectorGem targetCollector = getTargetCollector();
                if (targetCollector != null && !targetCollector.enclosesCollector(gemVisited.getRootCollectorGem())) {
                    return false;   // never done early.
                }

                // iterate over the inputs
                int numArgs = gemVisited.getNInputs();
                for (int i = 0; i < numArgs; i++) {
                    Gem.PartInput input = gemVisited.getInputPart(i);

                    // if input is connected, further parts are added after the connected gem's inputs.
                    if (input.isConnected()) {
                        addAfterGem = input.getConnection().getSource().getGem();

                    // otherwise, add inputs if burn status is ok.
                    } else if (shouldCollectInput(inputCollectMode, input)) {
                        // input not connected.  If there were no previous connected inputs, just add it.
                        if (addAfterGem == null) {
                            unboundParts.add(input);
    
                        // otherwise, save the inputs until the inputs of the previous connected input's gem are added
                        } else {
                            List<PartInput> addAfterPartsList = addPartsAfterGemMap.get(addAfterGem);
                            if (addAfterPartsList == null) {
                                addAfterPartsList = new ArrayList<PartInput>();
                                addPartsAfterGemMap.put(addAfterGem, addAfterPartsList);
                            }
                            addAfterPartsList.add(input);
                        }
                    }
                }
                
                // Special case for 0-input emitters while traversing subtrees
                //   Pretend the emitter has an input connected to a collector, unless we accounted for the collector's inputs already..
                if (getTraversalScope() == TraversalScope.FOREST && gemVisited.getNInputs() == 0 && gemVisited instanceof ReflectorGem) {
                    CollectorGem collectorGem = ((ReflectorGem)gemVisited).getCollector();
                    if (!inputCollectors.contains(collectorGem) && (targetCollector != null && targetCollector.enclosesCollector(collectorGem))) {
                        inputCollectors.add(collectorGem);
                        addAfterGem = collectorGem;
                    }
                }

                List<PartInput> partsAfter = addPartsAfterGemMap.get(gemVisited);
                if (partsAfter != null) {
                    if (addAfterGem == null) {
                        // there are no gems connected to inputs - we can add them to the unbound parts list as well now
                        unboundParts.addAll(partsAfter);
        
                    } else {
                        // add the parts to the list of parts waiting for the last connected gem's parts to be added
                        List<PartInput> addAfterPartsList = addPartsAfterGemMap.get(addAfterGem);
                        if (addAfterPartsList == null) {
                            addAfterPartsList = new ArrayList<PartInput>();
                            addPartsAfterGemMap.put(addAfterGem, addAfterPartsList);
                        }
                        addAfterPartsList.addAll(partsAfter);
                    }
                }
    
                // never done early
                return false;
            }
            
            private boolean shouldCollectInput(InputCollectMode icm, Gem.PartInput input) {
                return ((icm == InputCollectMode.ALL) || 
                        (icm == InputCollectMode.BURNT_ONLY && input.isBurnt()) ||
                        (icm == InputCollectMode.UNBURNT_ONLY && !input.isBurnt()));
            }
    
            List<PartInput> getUnboundParts() {
                return unboundParts;
            }
        }
        
        // create a visitor of appropriate type
        CollectorGem targetCollector = gem.getRootCollectorGem();
        DescendantPartObtainer descendantPartObtainer = new DescendantPartObtainer(traversalScope, targetCollector);

        // visit and detect
        gem.visitDescendants(descendantPartObtainer);
    
        return descendantPartObtainer.getUnboundParts();
    
    }
    
    /**
     * Obtain all the unconnected parts in all associated trees (including outer scopes) of which this gem is part.
     * @param forestGem Gem a gem in the forest.
     * @return the set of unbound parts
     */
    static Set<PartConnectable> obtainUnboundGemForestParts(Gem forestGem){
    
        // declare a visitor that can collect unbound parts
        class UnboundPartObtainer extends GemVisitor {
            Set <PartConnectable> partSet = new HashSet<PartConnectable>();
        
            UnboundPartObtainer() {
                super(TraversalScope.FOREST, null);
            }
        
            @Override
            public boolean visitGem(Gem gemVisited){
                List<PartConnectable> connectableParts = gemVisited.getConnectableParts();
                
                // add unconnected parts if any
                for (final PartConnectable part : connectableParts) {
                    if (!part.isConnected()) {
                        partSet.add(part);
                    }
                }
                
                // never done early
                return false;
            }
            
            Set<PartConnectable> getParts() {
                return partSet;
            }
        }
    
        // collect the root gems
        UnboundPartObtainer unboundPartObtainer = new UnboundPartObtainer();
        forestGem.visitGraph(unboundPartObtainer);
    
        return unboundPartObtainer.getParts();
    }
    
    /**
     * Disconnects all the valueGems, (this is a helper method that should be used in conjunction with reconnectValueGems()
     * @param parametricsOnly whether to disconnect only parametric value gems, or all value gems.
     * @return the connections which were disconnected. 
     *   The iteration order remains constant with time.
     */
    private Set<Connection> disconnectValueGems(boolean parametricsOnly) {
        Set<Connection> oldConnections = new LinkedHashSet<Connection>();

        // Go through all the gems in the graph
        for (final Gem gem : getGems()) {
            // we're only interested in value gems
            if (gem instanceof ValueGem) {
                ValueGem valueGem = (ValueGem)gem;
        
                // Disconnect.
                if (gem.isConnected() && (!parametricsOnly || valueGem.getValueNode().containsParametricValue())) {
                    Connection connection = gem.getOutputPart().getConnection();
                    oldConnections.add(connection);
                    connection.getSource().bindConnection(null);
                    connection.getDestination().bindConnection(null);
                } 
            }
        }
        
        return oldConnections;
    }
    
    /**
     * Infer the types of the all value gems outputs.
     *   ie. return what the types of the inputs would be if all value gems were disconnected.
     * @param parametricsOnly whether to calculate on the basis of all value gems being disconnected, or only parametric value gems.
     * @param info the info to use for typing the tree. 
     * @return map from value gem to its inferred type.
     *   null if the value gem types could not be inferred.
     */
    private Map<ValueGem, TypeExpr> inferValueGemTypes(boolean parametricsOnly, TypeCheckInfo info) {

        // We must update/switch value gem values according to the types of the inputs to which they are connected.
        // To do this, we disconnect them, type the tree, and then reconnect each valuegem, retyping them as appropriate. 
        Set<Connection> oldConnections = disconnectValueGems(parametricsOnly);
        
        // the map which will be returned.
        Map<ValueGem, TypeExpr> resultMap = new HashMap<ValueGem, TypeExpr>();
        
        // Check for nothing to do..
        if (oldConnections.isEmpty()) {
            return resultMap;
        }
        
        // Create the dummy collector, set it as the target for the gem graph's target.
        // It's the enclosing collector, so the name doesn't have to be unique..
        CollectorGem tempTarget = new CollectorGem();
        tempTarget.setName("valueGemArgumentCollector");
        tempTarget.addArguments(0, Collections.singleton((PartInput)tempTarget.getCollectingPart()));
        targetCollector.setTargetCollector(tempTarget);
        this.gemSet = new HashSet<Gem>(gemSet);       // prevents a ConcurrentModificationException..
        this.gemSet.add(tempTarget);
        this.collectorSet.add(tempTarget);
        
        try {
            // Add the newly-freed arguments to the dummy collector.
            Set<PartInput> freedArgumentSet = new LinkedHashSet<PartInput>();
            for (final Connection conn : oldConnections) {
                PartInput freedArgument = conn.getDestination();
                freedArgumentSet.add(freedArgument);
            }
            tempTarget.addArguments(0, freedArgumentSet);

            // Type the resulting graph..
            Map<Object, TypeExpr> unboundPartTypeMap = null;
            try {
                unboundPartTypeMap = getUnboundPartTypes(info);
            } catch (TypeException e) {
                // Can't do much about this..
                GemCutter.CLIENT_LOGGER.log(Level.WARNING, "Unable to infer value gem types.", e);
                return null;
            }

            // Populate the result map..
            for (final Connection conn : oldConnections) {
                Gem.PartInput freedInput = conn.getDestination();
                ValueGem valueGem = (ValueGem)conn.getSource().getGem();
                resultMap.put(valueGem, unboundPartTypeMap.get(freedInput));
            }

        } finally {
            targetCollector.setTargetCollector(null);
            gemSet.remove(tempTarget);
            collectorSet.remove(tempTarget);
            
            reconnectConnections(oldConnections);
        }
        
        return resultMap;
    }
    
    /**
     * Reconnects all the connections.
     *   This is a helper method to be used in conjunction with some methods that perform temporary disconnections.
     * @param oldConnections the connections to reconnect
     */
    static void reconnectConnections(Set<Connection> oldConnections) {
        for (final Connection connection : oldConnections) {
            connection.getDestination().bindConnection(connection);
            connection.getSource().bindConnection(connection);
        }
    }
    
    /**
     * Returns the least constrained value of this valueGem as dictated by its connections.
     * This method ignores the type constraints placed by this and other value gems. The
     * returned value will be null if the value gem is connected to a broken gem graph.
     * 
     * Example: add 1.0 2.0. 
     *   The least constrained type of the 1.0 value is Num a => a -> a  
     * 
     * @param valueGem
     * @param info
     * @return TypeExpr
     */
    public TypeExpr getLeastConstrainedValueType(ValueGem valueGem, TypeCheckInfo info) {
        // If a valueGem is disconnected, it has no constraints!
        if (!valueGem.isConnected()) {
            return TypeExpr.makeParametricType();
        }

        Map<ValueGem, TypeExpr> inferredValueGemTypeMap = inferValueGemTypes(false, info);
        return inferredValueGemTypeMap.get(valueGem);
    }
    
    /**
     * Determines what the types of value gems would be if a specified value gem were to commit its value
     *   to a given value node.
     * This may cause other value gems to switch their data types as well.
     * @param valueGemToSwitch The value gem switching value.
     * @param oldValueNode the value before the change.
     * @param newValueNode the new value for the value gem
     * @param info a TypeCheckInfo object to use for type checking.
     * @return Map map from value gem to new value.
     */
    public Map<ValueGem, ValueNode> getValueGemSwitchValues(ValueGem valueGemToSwitch, ValueNode oldValueNode, ValueNode newValueNode, TypeCheckInfo info) {

        Map<ValueGem, ValueNode> valueGemToNewValueNodeMap = new HashMap<ValueGem, ValueNode>();

        // If it's unconnected, all we have to do is set the valueNode of the destination valueGem! 
        // Otherwise... it's an tiny bit trickier...
        if (!valueGemToSwitch.isConnected()) {
            valueGemToNewValueNodeMap.put(valueGemToSwitch, newValueNode);
            return valueGemToNewValueNodeMap;
        }
        
        Map<ValueGem, TypeExpr> valueGemTypeMap = inferValueGemTypes(false, info);

        Map<PartInput, ValueNode> inputToValueNodeMap = new HashMap<PartInput, ValueNode>();
        Map<PartInput, ValueGem> inputToValueGemMap = new HashMap<PartInput, ValueGem>();
        Map<PartInput, TypeExpr> inputToUnconstrainedTypeMap = new HashMap<PartInput, TypeExpr>();

        for (final Map.Entry<ValueGem, TypeExpr> mapEntry: valueGemTypeMap.entrySet()) {
            ValueGem valueGem = mapEntry.getKey();
            TypeExpr valueGemUnconstrainedType = mapEntry.getValue();

            Gem.PartOutput valueGemOutput = valueGem.getOutputPart();
            if (!valueGemOutput.isConnected()) {
                // Don't have to worry about switching values for unconnected value gems.
                continue;
            }
            
            Gem.PartInput connectedInput = valueGemOutput.getConnection().getDestination();

            inputToValueGemMap.put(connectedInput, valueGem);
            inputToUnconstrainedTypeMap.put(connectedInput, valueGemUnconstrainedType);
            
            if (valueGem == valueGemToSwitch) {
                inputToValueNodeMap.put(connectedInput, oldValueNode);
            } else {
                inputToValueNodeMap.put(connectedInput, valueGem.getValueNode());
            }
        }

        // Get the switched values
        InputValueTypeSwitchHelper switcher = new InputValueTypeSwitchHelper(valueNodeBuilderHelper, valueNodeTransformer);
        Map<PartInput, ValueNode> inputToNewValueNodeMap = switcher.getInputSwitchValues(oldValueNode, newValueNode, inputToValueNodeMap, inputToUnconstrainedTypeMap);

        // Populate the return map.
        for (final Map.Entry<PartInput, ValueNode> mapEntry : inputToNewValueNodeMap.entrySet()) {
            Gem.PartInput input = mapEntry.getKey();

            ValueGem connectedValueGem = inputToValueGemMap.get(input);
            ValueNode newInputValue = mapEntry.getValue();
             
            valueGemToNewValueNodeMap.put(connectedValueGem, newInputValue);
        }
        valueGemToNewValueNodeMap.put(valueGemToSwitch, newValueNode);  // necessary..?

        return valueGemToNewValueNodeMap;
    }

    /**
     * Update all the ValueGems with new types.
     * Note: definition changes will be posted for the value gems that change type.
     * @param valueGemTypeMap map from value gem to its updated type.
     */
    private void updateValueGemsWithNewTypes(Map<ValueGem, TypeExpr> valueGemTypeMap) {
       
        for (final Map.Entry<ValueGem, TypeExpr> mapEntry : valueGemTypeMap.entrySet()) {
            
            // we get the type of the valueGem
            ValueGem valueGem = mapEntry.getKey();
            TypeExpr originalSourceExpr = valueGem.getValueNode().getTypeExpr();
            
            // and we get the newType that we want
            TypeExpr newSourceType = mapEntry.getValue();
                     
            // So if the type was changed, we want to swap the nodes
            if (!newSourceType.sameType(originalSourceExpr)) {
                // we transmute the node to come up with a valueNode of our desired type
                ValueNode valueNode = valueGem.getValueNode().transmuteValueNode(valueNodeBuilderHelper, valueNodeTransformer, newSourceType);
                
                // post definition changes if desired when swapping the nodes
                valueGem.changeValue(valueNode);
            }
        }
    }
    
    /**
     * Copy a set of gems and their associated forest.
     *   The gems will be copied, plus any associated gem trees from the gems' forest.
     * Notes: 
     *  - copied arguments will be not be assigned to any targets if their corresponding targets are not copied.
     *  - metadata are not copied.
     *  - if a collector gem to copy has no target, it will not be copied; instead, its copy will be set to the gemGraph target.
     * @param gemsInForest the gems to copy.
     * @param gemGraph  the gem graph into which the copied gems will be placed
     * @param includeBroken whether to include broken roots.
     * @return Pair of map from roots in the original to the roots in the copy and map from gems in the original to the gems in the copy.
     *              
     */
    public static Pair<Map<Gem, Gem>, Map<Gem, Gem>> copyGemForest(Set<Gem> gemsInForest, GemGraph gemGraph, boolean includeBroken) {
        
        // Disable argument updating while copying is going on.
        boolean argUpdatingWasDisabled = gemGraph.setArgumentUpdatingDisabled(true);
        
        // The procedure is:
        // Copy all the value gems, functional agents, code gems, collectors (without setting targets).
        //  This should leave only emitters and reflectors uncopied.
        // Assign copied collector targets.
        // Create all emitters.
        // Create all reflectors (but uninitialized).
        // Update input state, including connections.
        // Set all argument targets.
        
        Set<Gem> forestRootSet = obtainForestRoots(gemsInForest, includeBroken);
        CollectorGem copiedTargetCollector = null;

        // Get all the gems in the relevant forest.
        Set<Gem> allGemSet = new HashSet<Gem>();
        for (final Gem nextRoot : forestRootSet) {
            allGemSet.addAll(obtainSubTree(nextRoot));
        }
        
        // map from gem in original to gem in the gem tree copy being created.
        Map<Gem, Gem> oldToNewGemMap = new HashMap<Gem, Gem>();
        
        // Create sets to keep track of gems we need later.
        Set<CollectorGem> oldCollectorSet = new HashSet<CollectorGem>();
        Set<ReflectorGem> oldReflectorSet = new HashSet<ReflectorGem>();
        
        // Do a first pass over all the gems.
        //  Copy all functional agents, value gems, code gems, collectors.  Track collectors, emitters, reflectors.
        for (final Gem nextGem : allGemSet) {
            if (nextGem instanceof FunctionalAgentGem) {
                FunctionalAgentGem faGem = (FunctionalAgentGem)nextGem;
                Gem gemCopy = new FunctionalAgentGem(faGem.getGemEntity());
                gemGraph.addGem(gemCopy);
                oldToNewGemMap.put(nextGem, gemCopy);
            
            } else if (nextGem instanceof ValueGem) {
                ValueGem valueGem = (ValueGem)nextGem;
                Gem gemCopy = new ValueGem(valueGem.getValueNode());
                gemGraph.addGem(gemCopy);
                oldToNewGemMap.put(nextGem, gemCopy);
            
            } else if (nextGem instanceof CodeGem) {
                CodeGem codeGem = (CodeGem)nextGem;
                Gem gemCopy = codeGem.makeCopy();
                gemGraph.addGem(gemCopy);
                oldToNewGemMap.put(nextGem, gemCopy);
            
            } else if (nextGem instanceof CollectorGem) {
                CollectorGem collectorGem = (CollectorGem)nextGem;
                CollectorGem gemCopy;
                
                // if a collector gem to copy has no target, set its copy to the gemGraph target instead of copying it.
                boolean isTargetCollector = collectorGem.getTargetCollector() == null;
                if (isTargetCollector) {
                    if (copiedTargetCollector != null) {
                        throw new IllegalStateException("Attempt to copy a gem forest with multiple outermost targets.");
                    }
                    copiedTargetCollector = collectorGem;
                    gemCopy = gemGraph.getTargetCollector();
                    
                } else {
                    gemCopy = new CollectorGem(null);
                }
                
                gemCopy.setName(collectorGem.getUnqualifiedName());
                gemCopy.setDeclaredType(collectorGem.getDeclaredType());

                // Don't add if it's the target, as it will already exist in the gem graph.
                // Note: the collector's input will be added as an argument to the gemGraph's target collector.
                if (!isTargetCollector) {
                    gemGraph.addGem(gemCopy);
                }
                oldToNewGemMap.put(nextGem, gemCopy);
                oldCollectorSet.add((CollectorGem)nextGem);
            
            } else if (nextGem instanceof ReflectorGem) {
                oldReflectorSet.add((ReflectorGem)nextGem);

            } else {
                throw new IllegalArgumentException("Unexpected gem type: " + nextGem);
            }
        }
        
        // Set targets for new collectors.
        for (final CollectorGem oldCollector : oldCollectorSet) {
            CollectorGem newCollector = (CollectorGem)oldToNewGemMap.get(oldCollector);
            
            // Create the gem, add the mapping.
            CollectorGem newTargetCollector = (CollectorGem)oldToNewGemMap.get(oldCollector.getTargetCollector());
            if (newTargetCollector != null) {
                newCollector.setTargetCollector(newTargetCollector);
            }
        }
        
        // Create all the reflectors.
        for (final ReflectorGem oldReflector : oldReflectorSet) {
            CollectorGem oldCollector = oldReflector.getCollector();
            
            // Create the gem, add the mapping.
            ReflectorGem newReflector = new ReflectorGem((CollectorGem)oldToNewGemMap.get(oldCollector), oldReflector.getNInputs());
            gemGraph.addGem(newReflector);
            oldToNewGemMap.put(oldReflector, newReflector);
        }
        
        // copy reflector argument state.
        for (final ReflectorGem oldReflector : oldReflectorSet) {
            ReflectorGem newReflector = (ReflectorGem)oldToNewGemMap.get(oldReflector);
            newReflector.copyArgumentState(oldReflector, oldToNewGemMap);
        }            

        // Now iterate over all old gem inputs.
        // Burn, connect, or name the corresponding new gem inputs as necessary.
        for (final Gem nextOldGem : allGemSet) {
            Gem gemCopy = oldToNewGemMap.get(nextOldGem);
            
            for (int i = 0, nInputs = nextOldGem.getNInputs(); i < nInputs; i++) {
                Gem.PartInput oldInput = nextOldGem.getInputPart(i);

                if (oldInput.isBurnt()) {
                    gemCopy.getInputPart(i).setBurnt(true);
                
                } else if (oldInput.isConnected()) {
                    Gem connectedGem = oldToNewGemMap.get(oldInput.getConnectedGem());
                    gemGraph.connectGems(connectedGem.getOutputPart(), gemCopy.getInputPart(i));
                
                } else {
                    Gem.PartInput newInput = gemCopy.getInputPart(i);
                    newInput.setArgumentName(oldInput.getArgumentName());
                }
            }
        }
        
        // Set all the collector arguments.
        for (final CollectorGem oldCollector: oldCollectorSet) {
            CollectorGem newCollector = (CollectorGem)oldToNewGemMap.get(oldCollector);
            
            List<PartInput> newTargetingArguments = new ArrayList<PartInput>();
            
            // Iterate over the old collector's target arguments.
            for (final PartInput oldPartInput : oldCollector.getTargetArguments()) {
                // Get the corresponding new argument.
                Gem newGem = oldToNewGemMap.get(oldPartInput.getGem());

                if (newGem != null) {
                    PartInput newArgument = newGem.getInputPart(oldPartInput.getInputNum());
                    newTargetingArguments.add(newArgument);
                }
            }
            
            // Remove the arguments from their old targets.
            for (final PartInput targetingArgument : newTargetingArguments) {
                CollectorGem argumentTarget = getInputArgumentTarget(targetingArgument);
                if (argumentTarget != null) {
                    argumentTarget.removeArgument(targetingArgument);
                }
            }
            
            // Set the arguments on the collector.
            newCollector.addArguments(0, newTargetingArguments);
        }
        
        // Now gather the new forest roots so we can return them.
        Map<Gem, Gem> newForestRootMap = new HashMap<Gem, Gem>();
        for (final Gem oldRoot : forestRootSet) {
            newForestRootMap.put(oldRoot, oldToNewGemMap.get(oldRoot));
        }
        
        // Restore argument updating.
        gemGraph.setArgumentUpdatingDisabled(argUpdatingWasDisabled);
        
        return new Pair<Map<Gem, Gem>, Map<Gem, Gem>>(newForestRootMap, oldToNewGemMap);
    }

    /**
     * Copy a gem and its associated forest.
     *   The gem will be copied, plus any associated gem trees from the gem's forest.
     * Notes: 
     *  - copied arguments will be not be assigned to any targets if their corresponding targets are not copied.
     *  - metadata are not copied.
     *  - if a collector gem to copy has no target, it will not be copied; instead, its copy will be set to the gemGraph target.
     * @param gem the gem to copy.
     * @param gemGraph  the gem graph into which the copied gems will be placed
     * @param includeBroken whether to include broken roots.
     * @return Pair of map from roots in the original to the roots in the copy and map from gems in the original to the gems in the copy.
     */
    public static Pair<Map<Gem, Gem>, Map<Gem, Gem>> copyGemForest(Gem gem, GemGraph gemGraph, boolean includeBroken) {
        
        return copyGemForest(Collections.singleton(gem), gemGraph, includeBroken);
    }

    /**
     * Determine whether the gem graph is valid as it is currently defined.
     * @param info the type check info to use to validate the gem graph.
     * @return whether the gem graph is valid as it is currently defined.
     */
    public boolean checkGraphValid(TypeCheckInfo info) {
        
        // value gem connections temporarily disconnected from their destination.
        Set<Connection> disconnectedValueGemConnections = new HashSet<Connection>();
        try {
            // TODOEL: This checking only has to happen on connect / disconnect (/ burn?) (switching does its own value gem updating..).
            // We must update/switch value gem values according to the types of the inputs to which they are connected (eg. update on connect).
            // To do this, we infer their types, and check according to the newly-inferred types.
            Map<ValueGem, TypeExpr> valueGemToInferredTypeMap = inferValueGemTypes(true, info);
            if (valueGemToInferredTypeMap != null) {
                // Get the updated value gem types.
                Map<ValueGem, TypeExpr> valueGemTypeMap = getUnifiedValueGemTypes(valueGemToInferredTypeMap, info);

                for (final Map.Entry<ValueGem, TypeExpr> mapEntry : valueGemTypeMap.entrySet()) {
                    ValueGem valueGem = mapEntry.getKey();
                    
                    // We can skip the next bit if the value gem is not connected..
                    Connection outputConnection = valueGem.getOutputPart().getConnection();
                    if (outputConnection == null) {
                        continue;
                    }
                    
                    // we get the type of the valueGem
                    TypeExpr originalSourceExpr = valueGem.getValueNode().getTypeExpr();
                    
                    // and we get the newType that we want
                    TypeExpr newSourceType = mapEntry.getValue();
                             
                    // If the type was changed, attach a temporary value gem of the correct type.
                    if (!newSourceType.sameType(originalSourceExpr)) {
                        // Transmute the node to come up with a valueNode of our desired type
                        ValueNode valueNode = valueGem.getValueNode().transmuteValueNode(valueNodeBuilderHelper, valueNodeTransformer, newSourceType);
                        
                        // Create a temporary value gem and connect it.
                        ValueGem tempValueGem = new ValueGem(valueNode);
                        PartInput connectedInput = outputConnection.getDestination();
                        connectedInput.bindConnection(new Connection(tempValueGem.getOutputPart(), connectedInput));
                        
                        // Add to the set of temporarily disconnected value gem connections.
                        disconnectedValueGemConnections.add(outputConnection);
                    }
                }
            }
    
            // Test whether the unbound parts can be typed.
            getUnboundPartTypes(info);
        
        } catch (TypeException te) {
            // If there's a type exception, the resulting gem graph can't be typed.
            return false;

        } finally {
            // re-bind any connections which were temporarily disconnected.
            for (final Connection connection : disconnectedValueGemConnections) {
                connection.getDestination().bindConnection(connection);
            }
        }
        
        return true;
    }

    /**
     * Updates the input and output types for all gems in the GemGraph.
     * Also updates the types of ValueGems.
     * @param info the info to use for typing the tree. 
     * @throws TypeException if the gem graph is not in a valid state.
     */
    public void typeGemGraph(TypeCheckInfo info) throws TypeException {
        
        // refresh the polymorphicVarContext
        polymorphicVarContext = PolymorphicVarContext.make();
        
        // disambiguate arg names.
        checkArgNamesDisambiguated();
        
        // TODOEL: This updating only has to happen on connect / disconnect (/ burn?) (switching does its own value gem updating..).
        // We must update/switch value gem values according to the types of the inputs to which they are connected (eg. update on connect).
        // To do this, we infer their types, and update according to the newly-inferred types.
        Map<ValueGem, TypeExpr> valueGemToInferredTypeMap = inferValueGemTypes(true, info);
        if (valueGemToInferredTypeMap != null && !valueGemToInferredTypeMap.isEmpty()) {
            Map<ValueGem, TypeExpr> valueGemToNewTypeMap = getUnifiedValueGemTypes(valueGemToInferredTypeMap, info);
            updateValueGemsWithNewTypes(valueGemToNewTypeMap);
        }

        // Get the types of the parts to be typed.
        Map<Object, TypeExpr> unboundPartToTypeMap = getUnboundPartTypes(info);

        // Set the types on the updated graph.
        for (final Map.Entry<Object, TypeExpr> mapEntry : unboundPartToTypeMap.entrySet()) {
            Object part = mapEntry.getKey();
            TypeExpr partType = mapEntry.getValue();

            if (part instanceof Gem.PartConnectable) {
                // Set the part type.
                ((Gem.PartConnectable)part).setType(partType);

            } else {
                // Set the root output type
                ((Gem)part).setRootOutputType(partType);
            }
        }
    }

    /**
     * Get the types of all the unbound parts in the gem graph as it is currently defined, except for value gem output parts.
     * @param info
     * @return a Map which contains two types of mappings to associated type expr. Map from the associated input or map from the associated root. 
     *         This corresponds to the output type. In the case of collectors, it will be the collector type.
     * @throws TypeException
     */
    private Map<Object, TypeExpr> getUnboundPartTypes(TypeCheckInfo info) throws TypeException {
        
        Map<Object, TypeExpr> unboundPartToTypeMap = new HashMap<Object, TypeExpr>();
        
        Set<Gem> rootSet = getRoots();
        Set<Gem> valueRoots = getValueRoots();
        
        // we only want to get new types for non-value roots
        rootSet.removeAll(valueRoots);
        
        // we also don't want to try to type anything that's broken, 
        // so remove all the broken roots and their ancestors from the sets we actually type
        Set<Gem> brokenSet = obtainBrokenAncestorRoots(rootSet);
        rootSet.removeAll(brokenSet);
        
        // Broken trees' parts are type null.  Set root output types null, and gather sinks
        Set<PartInput> noTypeSinks = new HashSet<PartInput>();
        for (final Gem brokenRoot : brokenSet) {
            noTypeSinks.addAll(obtainUnboundDescendantInputs(brokenRoot, TraversalScope.TREE, InputCollectMode.ALL));
            
            unboundPartToTypeMap.put(brokenRoot, null);
        }
        // type the sinks null
        for (final Gem.PartConnectable part : noTypeSinks){
            unboundPartToTypeMap.put(part, null);
        }
    
        // Grab all the good types.  This may throw a TypeException.
        final Pair<Map<CompositionNode.CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>> resultPair = info.getTypeChecker().checkGraph(rootSet, info.getModuleName(), new MessageLogger());
        Map<CompositionNode.CompositionArgument, TypeExpr> argMap = resultPair.fst();
        Map<CompositionNode, List<TypeExpr>> rootNodeMap = resultPair.snd();
        // TODO Display any error messages from logger?

        // Populate the map.
        for (final Entry<CompositionNode, List<TypeExpr>> mapEntry : rootNodeMap.entrySet()) {
            CompositionNode key = mapEntry.getKey();

            if (!brokenSet.contains(key)) {
                Gem rootGem = (Gem)key;
                List<TypeExpr> rootTypes = mapEntry.getValue();
                TypeExpr rootType = rootTypes.get(rootTypes.size() - 1);
                unboundPartToTypeMap.put(rootGem, rootType);
            }
        }
        
        for (final Entry<CompositionNode.CompositionArgument, TypeExpr> mapEntry : argMap.entrySet()) {
            CompositionNode.CompositionArgument key = mapEntry.getKey();
            
            // must be an input argument.
            PartInput inputArgument = (PartInput)key;
            TypeExpr inputType = mapEntry.getValue();
            unboundPartToTypeMap.put(inputArgument, inputType);
        }
        
        return unboundPartToTypeMap;
    }


    /**
     * Given a map from value gem to its inferred type, calculate what the value gem types would be if all value gem types
     *   were unified with their inferred types.
     * @param valueGemToInferredTypeMap map from value gem to its inferred type, with which it should be unified.
     * @param typeCheckInfo the info to use to revalidate the graph.
     * @return map from value gem to the type corresponding to the value gem's type subject to the constraint of
     *      unification with it inferred type. 
     * @throws TypeException
     */
    private Map<ValueGem, TypeExpr> getUnifiedValueGemTypes(Map<ValueGem, TypeExpr> valueGemToInferredTypeMap, TypeCheckInfo typeCheckInfo) throws TypeException {
             
        int nTypes = valueGemToInferredTypeMap.size();
        
        TypeExpr[] sourceTypePieces = new TypeExpr[nTypes];
        TypeExpr[] destTypePieces = new TypeExpr[nTypes];
        
        int index = 0;
      
        // Populate the arrays of type pieces..
        for (final Map.Entry<ValueGem, TypeExpr> mapEntry : valueGemToInferredTypeMap.entrySet()) {
            ValueGem valueGem = mapEntry.getKey();
            TypeExpr inferredType = mapEntry.getValue();
                  
            TypeExpr sourceExpr = valueGem.getValueNode().getTypeExpr();
            sourceTypePieces[index] = sourceExpr;
           
            //note destType may be null in case the connected tree is broken.                 
            destTypePieces[index] = inferredType;
                    
            ++index;
        }
        
       
        TypeExpr[] unifiedTypes = TypeExpr.unifyTypePieces(destTypePieces, sourceTypePieces, typeCheckInfo.getModuleTypeInfo());
        
        // The result map..
        Map<ValueGem, TypeExpr> valueGemToUnifiedConnectionTypeMap = new HashMap<ValueGem, TypeExpr>();

        index = 0;
        for (final ValueGem valueGem : valueGemToInferredTypeMap.keySet()) {
            valueGemToUnifiedConnectionTypeMap.put(valueGem, unifiedTypes[index]);
            index++;
        }

        return valueGemToUnifiedConnectionTypeMap;
    }    

    /**
     * Get the String representation of a type.
     * @param type TypeExpr the type
     * @param namingPolicy rules to use in determining the type string     
     * @return the string representation for the type expression, or null if type undefined
     */
    public final String getTypeString(TypeExpr type, ScopedEntityNamingPolicy namingPolicy) {
        return type.toString(polymorphicVarContext, namingPolicy);
    }

    /**
     * Determine if two types will unify.
     * @param fromType TypeExpr the first type
     * @param toType TypeExpr the second type
     * @param typeCheckInfo the typeCheckInfo to use.
     * @return boolean true if types are unifiable
     */
    static boolean typesWillUnify(TypeExpr fromType, TypeExpr toType, TypeCheckInfo typeCheckInfo) {
        if (fromType == null || toType == null) {
            return false;
        }
        // Check for unification
        return TypeExpr.canUnifyType(fromType, toType, typeCheckInfo.getModuleTypeInfo());
    }

    /**
     * Check two parts for basic connectivity, ignoring their current types.
     * This includes seeing if they are already connected, checking for brokenness, and seeing
     * if a cyclic tree would result.
     * @param from Gem.PartConnectable the part from which the connection is made
     * @param to Gem.PartConnectable the part to which the connection is made 
     * @return boolean true if valid, false otherwise
     */
    static boolean arePartsConnectable(Gem.PartConnectable from, Gem.PartConnectable to) {
        // Not valid if the destination or source are already connected!
        if (from.isConnected() || to.isConnected()) {
            return false;
        }
        
        // Also not valid if either of them is burnt.
        if (from instanceof Gem.PartInput && ((Gem.PartInput) from).isBurnt()) {
            return false;
        }
        if (to instanceof Gem.PartInput && ((Gem.PartInput) to).isBurnt()) {
            return false;
        }
        
        // Defer to the other method..
        if (!arePartsConnectableIfDisconnected(from, to)) {
            return false;
        }
        
        // Check types for null -- canUnifyType() can't handle nulls.
        TypeExpr toGemInType = to.getType();
        TypeExpr fromGemOutType = from.getType();   

        return toGemInType != null && fromGemOutType != null;
    }
    
    /**
     * Check two parts for basic connectivity, ignoring their current types.
     * This includes seeing if they checking for brokenness, and seeing if a cyclic tree would result.
     * Does not check whether the parts are connected. So they may not be connectable now, but the result
     * of this method may still return true in the future.
     * @param from the part from which the connection is made
     * @param to the part to which the connection is made 
     * @return boolean true if valid, false otherwise
     */
    public static boolean arePartsConnectableIfDisconnected(Gem.PartConnectable from, Gem.PartConnectable to) {
    
        // to must be a sink and from must be a source
        if (!(to instanceof Gem.PartInput && from instanceof Gem.PartOutput)) {
            return false;
        }
    
        // still invalid if the input is burnt
        if (((Gem.PartInput)to).isBurnt()){
            return false;
        }
    
        Gem fromGem = from.getGem();
        Gem toGem = to.getGem();
        
        // don't allow cyclic trees
        if (obtainSubTree(fromGem).contains(toGem)) {
            return false;
        }
        
        // can't connect anything that's broken
        if (isAncestorOfBrokenGemForest(fromGem) || isAncestorOfBrokenGemForest(toGem.getRootGem())) {
            return false;
        }
        
        // Check for visibility constraints due to reflectors connected to the gem subtrees.
        
        Set<ReflectorGem> fromSubtreeReflectors = UnsafeCast.<Set<ReflectorGem>>unsafeCast(obtainSubTreeGems(fromGem, ReflectorGem.class));
        
        if (!fromSubtreeReflectors.isEmpty()) {
            
            CollectorGem toRootCollectorGem = toGem.getRootCollectorGem();
            if (toRootCollectorGem != null) {
                // There is a CollectorGem at the root of the gem tree.
                // It must be able to see all collectors for reflectors in the tree.
                
                // Iterate over the subtree reflectors.
                for (final ReflectorGem subtreeReflector : fromSubtreeReflectors) {
                    CollectorGem reflectorCollector = subtreeReflector.getCollector();
                    
                    // If the subtree reflector collector isn't visible to the root collector, can't connect.
                    if (!reflectorCollector.isVisibleTo(toRootCollectorGem)) {
                        return false;
                    }
                }
                
            } else {
                // There isn't a CollectorGem at the root of the gem tree,.
                // We must be able to attach a collector which can see all collectors for reflectors in the tree.
                
                // For one collector to be visible to another, it must be the same collector, a sibling, an ancestor, 
                //   a sibling of an ancestor, or a child
                // This can be simplified to: its target must enclose the other collector.
                // So, in order to be able to define a collector which can see all the reflector definitions in an unrooted subtree,
                //   the targets of the reflectors' collectors must form an ancestor (enclosement) chain.
                
                Set<ReflectorGem> toSubtreeReflectors = UnsafeCast.<Set<ReflectorGem>>unsafeCast(obtainSubTreeGems(toGem, ReflectorGem.class));

                // Don't have to check if target subtree doesn't have reflectors.
                if (!toSubtreeReflectors.isEmpty()) {
                    
                    Set<CollectorGem> collectorTargetsToCheck = new HashSet<CollectorGem>();
                    
                    // Iterate over the reflectors in both subtrees.
                    for ( final ReflectorGem subtreeReflector : toSubtreeReflectors) {
                        CollectorGem subtreeReflectorCollector = subtreeReflector.getCollector();
                        
                        // Add the reflector collector's target (if any) to the set of collector targets to check.
                        CollectorGem subtreeReflectorCollectorTarget = subtreeReflectorCollector.getTargetCollectorGem();
                        if (subtreeReflectorCollectorTarget != null) {
                            collectorTargetsToCheck.add(subtreeReflectorCollectorTarget);
                        }
                    }
                    
                    // Check that they form an ancestor chain.
                    if (!GemGraph.formsAncestorChain(collectorTargetsToCheck)) {
                        return false;
                    }
                }
            }
        }
    
        return true;
    }

    /**
     * Check if the connection binding the output of a Gem (from) to an input of a Gem (to) is valid.  
     * This is known as a function composition.  This includes checking if 'to' in currently unbound (which it must be).
     * @param from the part from which the connection is made
     * @param to the part to which the connection is made 
     * @param currentModuleTypeInfo the ModuleTypeInfo for the current module.
     * @return boolean true if valid, false otherwise
     */
    public static boolean isCompositionConnectionValid(Gem.PartConnectable from, Gem.PartConnectable to, ModuleTypeInfo currentModuleTypeInfo) {

        // check for connectivity constraints
        if (!arePartsConnectable(from, to) || !isConnectionValid(from,to)) {
            return false;
        }
        
        // see if types will unify.
        try {
            // Get types for canUnifyType.  Shouldn't be null since we already checked if they're connectable!
            TypeExpr toInputType = to.getType();
            TypeExpr fromOutputType = from.getType();   
    
            return TypeExpr.canUnifyType (toInputType, fromOutputType, currentModuleTypeInfo);
    
        } catch (ClassCastException cce) {
        } catch (NullPointerException npe) {
        }
    

        return false;
    }
    
    
    /**
     * Return true if the connection is possible between a record and record field selection gem
     * with a free field name - if it is one of the possible filed names are returned, otherwise false
     * @param from the source part of the connection - expected to be a record, otherwise false will be returned
     * @param to the to destination of the connection - expected to be a record field selection gem, otherwise false will be returned
     * @param currentModuleTypeInfo
     * @return fieldName if connection can be made, otherwise null
     */
    public static FieldName isValidConnectionToRecordFieldSelection(Gem.PartConnectable from, Gem.PartConnectable to, ModuleTypeInfo currentModuleTypeInfo) {

        // check for connectivity constraints
        if (!arePartsConnectable(from, to) || !isConnectionValid(from,to)) {
            return null;
        }
        
        if (! (from.getType() instanceof RecordType)) {
            return null;
        }
                
        if (!(to.getGem() instanceof RecordFieldSelectionGem)) {
            return null;
        }
        
        RecordFieldSelectionGem toGem = (RecordFieldSelectionGem) to.getGem();
        
        if (toGem.isFieldFixed()) {
            return null;
        }
                
        RecordType toType = (RecordType) to.getType();
        FieldName fieldName = toGem.getFieldName();
        
        TypeExpr requiredFieldType= toType.getHasFieldType(fieldName);
        
        return RecordFieldSelectionGem.pickFieldName(from.getType(), requiredFieldType, currentModuleTypeInfo);
        
    }
    
    /**
     * This checks to see if a connection from record field selection gem is possible if the input is burnt 
     * if the connection is possible, the first viable field name is returned, otherwise null
     * @param from the source of the connection, expected the output part of a record field selection gem, otherwise false will be returned
     * @param to the destination of the connection, expected to be something that requires an arity 1 function with a record as the input, otherwise false will be returned
     * @param currentModuleTypeInfo
     * @return FieldName returns a field name or null iff the connection is not possible
     */
    public static FieldName isValidConnectionFromBurntRecordFieldSelection(Gem.PartConnectable from, Gem.PartConnectable to, ModuleTypeInfo currentModuleTypeInfo) {

        // check for connectivity constraints
        if (!arePartsConnectable(from, to) || !isConnectionValid(from,to)) {
            return null;
        }
   
        if (!(from.getGem() instanceof RecordFieldSelectionGem)) {
            return null;
        }
        
        if (!from.getGem().getNodeArgument(0).isBurnt()) {
            return null;
        }
        
        if (to.getType().getArity() < 1) {
            return null;
        }
        
        if (((RecordFieldSelectionGem)from.getGem()).isFieldFixed()) {
            return null;
        }
        
        TypeExpr[] typePieces = to.getType().getTypePieces(1);
        
        FieldName fieldName = RecordFieldSelectionGem.pickFieldName(typePieces[0], typePieces[1], currentModuleTypeInfo);
        
        return fieldName;

    }
    
    /**
     * This method checks whether the connection between the specified inputs and outputs are valid
     * if the inputs and outputs were disconnected from their current connections. 
     * 
     * @param from Gem.PartConnectable the part from which the connection is made
     * @param to Gem.PartConnectable the part to which the connection is made 
     * @return boolean true if valid, false otherwise
     */
    public static boolean isCompositionConnectionValidIfDisconnected(Gem.PartConnectable from, Gem.PartConnectable to, TypeCheckInfo info) {

        // check for connectivity constraints
        if (!arePartsConnectableIfDisconnected(from, to) || !isConnectionValid(from,to)) {
            return false;
        }
        
        // see if types will unify.
        try {
            // Get types for canUnifyType.  Shouldn't be null since we already checked if they're connectable!
            TypeExpr toInputType = to.getType();
            
            TypeExpr fromOutputType;
            if (from.isConnected()) {
                // If the output was connected, we want to infer the type of this output by inferring it from its connected input.
                fromOutputType = from.getConnection().getDestination().inferType(info);
            } else {
                fromOutputType = ((Gem.PartOutput) from).getType();
            }
    
            return TypeExpr.canUnifyType (toInputType, fromOutputType, info.getModuleTypeInfo());
    
        } catch (ClassCastException cce) {
        } catch (NullPointerException npe) {
        }
    

        return false;
    }

    /**
     * Check for any additional constraints on connections, aside from basic connectivity.
     * For now, this just checks that value gems with parametric values can't connect.
     * 
     * @param from Gem.PartConnectable the part from which the connection is made
     * @param to Gem.PartConnectable the part to which the connection is made 
     * @return boolean true if valid, false otherwise
     */
    public static boolean isConnectionValid(Gem.PartConnectable from, Gem.PartConnectable to) {

        // Can't connect ValueGems that contain parametric values.
        if (from.getGem() instanceof ValueGem) {
            ValueGem valueGem = ((ValueGem)from.getGem());
            if (valueGem.containsParametricValues()) {
                return false;
            }
        }
    
        return true;
    }

    /**
     * Returns all of the unqualified names of the codegems and collectors
     * @return the unqualified names of codegems and collectors.
     */
    Set<String> getCodeGemsCollectorsNames() {
        Set<String> codeGemsCollectorsNames = getCodeGemNames();
        codeGemsCollectorsNames.addAll(getCollectorNames());
        return codeGemsCollectorsNames;
    }

    /**
     * Sets the disableArgumentUpdating flag.
     *   This causes automatic argument updating by the gem graph to be disabled.
     * @param newValue The new value.  If false, argument updating is enabled.  However, affected reflectors will not have been updated.
     * @return the old value.
     */
    boolean setArgumentUpdatingDisabled(boolean newValue) {
        return argumentManager.setArgumentUpdatingDisabled(newValue);
    }
    
    /**
     * Sets the valueNodeBuilderHelper used for validating the ValueGems
     * @param valueNodeBuilderHelper
     */
    public void setValueNodeBuilderHelper(ValueNodeBuilderHelper valueNodeBuilderHelper) {
        this.valueNodeBuilderHelper = valueNodeBuilderHelper;      
    }

    /**
     * Adds the contents of the specified gem graph into the current one.
     * Collectors and code gems will be renamed so that they don't confict with existing names.
     * If the specified gem graph's target is unconnected, it will be assumed that the gem graph is merely a container for the 
     *   gems contained therein.  The target will not be copied, and collectors targeting the target will be retargeted to this
     *   gem graph's target.
     * @param otherGemGraph the gem graph which will be merged into this one.  Its target will not be copied if it is unconnected.
     */
    public void addGemGraph(GemGraph otherGemGraph) {
        Set<Gem> rootsToAdd = otherGemGraph.getRoots();
        CollectorGem thisTargetCollector = getTargetCollector();
        CollectorGem otherTargetCollector = otherGemGraph.getTargetCollector();
        
        // Don't add the target if it's not connected.  
        if (!otherTargetCollector.isConnected()) {

            rootsToAdd.remove(otherTargetCollector);

            // Set any collectors targeting the old target to the new target gem.
            for (final Gem root : rootsToAdd) {
                if (root instanceof CollectorGem && ((CollectorGem)root).getTargetCollector() == otherTargetCollector) {
                    ((CollectorGem)root).setTargetCollector(thisTargetCollector);
                }
            }
        }
        
        // Defer to the other method.
        addSubtrees(rootsToAdd);
    }

    /**
     * Merges the contents of the specified gem trees into the current one.
     * Collectors and code gems will be renamed so that they don't conflict with existing names.
     * Inputs targeted at unadded collectors will be retargeted to the target gem.
     * Note: unsafe.
     * @param newRoots the roots of the gem trees which will be added to this gem graph.
     */
    public void addSubtrees(Collection<Gem> newRoots) {
        Set<Gem> gemsToAdd = new HashSet<Gem>();
        Set<CollectorGem> collectorsToAdd = new HashSet<CollectorGem>();
        Set<Connection> connectionsToAdd = new HashSet<Connection>();
        
        // Get the items that need to be added.
        for (final Gem nextRoot : newRoots) {
            gemsToAdd.addAll(obtainSubTree(nextRoot));

            if (nextRoot instanceof CollectorGem) {
                collectorsToAdd.add((CollectorGem)nextRoot);
            }
            
            connectionsToAdd.addAll(getConnectionsInSubtree(nextRoot));
        }
        
        Set<String> existingCollectorNames = getCollectorNames();
        Set<String> existingCodeGemNames = getCodeGemNames();

        // Make a pass through the other gem graph and rename any collectors and code gems that would
        // conflict with the naming in the existing gem graph
        for (final Gem gem : gemsToAdd) {
            if (gem instanceof CollectorGem) {
                CollectorGem collector = (CollectorGem)gem;
                setUniqueCollectorName(collector, existingCollectorNames);
                existingCollectorNames.add(collector.getUnqualifiedName());

            } else if (gem instanceof CodeGem) {
                CodeGem codeGem = (CodeGem)gem;
                setUniqueCodeGemName(codeGem, existingCodeGemNames);
                existingCodeGemNames.add(codeGem.getUnqualifiedName());

                // TODOEL: remove other code gem update listener, replace with this one.
                //  Maybe make the listener add and remove itself??
            
            } else if (gem instanceof ReflectorGem) {
                ReflectorGem reflectorGem = (ReflectorGem)gem;
                reflectorGem.getCollector().addReflector(reflectorGem);
            }
        }

        // Bulk copy all of the gems and connections into the current gem graph
        gemSet.addAll(gemsToAdd);
        connectionSet.addAll(connectionsToAdd);
        collectorSet.addAll(collectorsToAdd);
        
        // Ensure argument and collector targets are valid.
        validateInputTargets();

        // Ensure that reflected inputs are updated.
        if (!collectorsToAdd.isEmpty()) {
            for (final Gem gem : getCollectors()) {
                CollectorGem collectorGem = (CollectorGem)gem;
                collectorGem.updateReflectedInputs();
            }
        }

        // Notify gem add listeners
        for (final Gem addedGem : gemsToAdd) {
            for (final GemGraphChangeListener listener : gemGraphChangeListeners) {
                listener.gemAdded(new GemGraphAdditionEvent(addedGem));
            }
        }

        // Notify connection listeners
        for (final Gem addedRoot : newRoots) {
            for (final Connection addedConnection : getConnectionsInSubtree(addedRoot)) {
                for (final GemGraphChangeListener listener : gemGraphChangeListeners) {
                    listener.gemConnected(new GemGraphConnectionEvent(addedConnection));
                }
            }
        }
    }
    
    /**
     * Returns the set of connections referred to by the subtree starting at the specified gem.
     * @param gem the gem at the head of the subtree.
     * @return the connections in the subtree headed by the specified gem.
     */
    static Set<Connection> getConnectionsInSubtree(Gem gem) {
        Set<Connection> connections = new HashSet<Connection>();
        getConnectionsInSubtreeHelper(gem, connections);
        return connections;
    }

    /**
     * Helper method for getConnectionsInSubtree().
     * @param gem
     * @param connections 
     */
    private static void getConnectionsInSubtreeHelper(Gem gem, Set<Connection> connections) {
        // Add the connection for the gem's output, if any.
        if (gem.getOutputPart() != null && gem.getOutputPart().isConnected()) {
            connections.add(gem.getOutputPart().getConnection());
        }

        // Call this recursively for any connected inputs.
        Gem.PartInput[] inputs = gem.getInputParts();
        for (final PartInput input : inputs) {
            if (input.isConnected()) {
                getConnectionsInSubtreeHelper(input.getConnectedGem(), connections);
            }
        }
    }

    /**
     * Sets a unique name for the collector.
     * A suffix "_i" will be added to the current collector name, if necessary, to make
     * the name unique from any collectors in the specified set.
     * @param collector
     * @param existingNames
     */
    private static void setUniqueCollectorName(CollectorGem collector, Set<String> existingNames) { 
        // Find a unique name for the collector.
        String collectorName = collector.getUnqualifiedName();

        for (int i = 1; existingNames.contains(collectorName); ++i) {
            collectorName = collector.getUnqualifiedName() + '_' + i;
        }

        collector.setName(collectorName);
    }

    /**
     * Sets a unique name for the code gem.
     * A suffix "_i" will be added to the current code gem name, if necessary, to make
     * the name unique from any code gems in the specified set.
     * @param codeGem
     * @param existingNames
     */
    private static void setUniqueCodeGemName(CodeGem codeGem, Set<String> existingNames) { 
        // Find a unique name for the code gem.
        String codeGemName = codeGem.getUnqualifiedName();

        for (int i = 1; existingNames.contains (codeGemName); ++i) {
            codeGemName = codeGem.getUnqualifiedName() + '_' + i;
        }

        codeGem.setName(codeGemName);
    }

    /**
     * Removes the specified gems and connections from the current gem graph.
     * This assumes that nothing remaining in the gem graph will refer to these objects.
     * @param gemsToRemove
     * @param connectionsToRemove
     */
    public void removeFromGemGraph(Collection<Gem> gemsToRemove, Collection<Connection> connectionsToRemove) {
        // Bulk remove all of the gems and connections from the current gem graph
        gemSet.removeAll(gemsToRemove);
        connectionSet.removeAll(connectionsToRemove);
        this.collectorSet.removeAll(gemsToRemove);

        // Disallow deletion of the target gem.
        if (!gemSet.contains(targetCollector)) {
            // Warn and add the gem back.
            GemCutter.CLIENT_LOGGER.log(Level.WARNING, "Attempt to delete the target gem from the gem graph.");
            gemSet.add(targetCollector);
            this.collectorSet.add(targetCollector);
        }
        
        // For every removed gem, notify the listeners
        for (final Gem gem : gemsToRemove) {
            for (final GemGraphChangeListener listener : gemGraphChangeListeners) {
                listener.gemRemoved(new GemGraphRemovalEvent(gem));
            }
        }
        
        // For every removed connection, notify the listeners
        for (final Connection conn : connectionsToRemove) {
            if (gemConnectionListener != null) {
                gemConnectionListener.disconnectionOccurred(new GemConnectionEvent(conn, GemConnectionEvent.EventType.DISCONNECTION));
            }
            
            for (final GemGraphChangeListener listener : gemGraphChangeListeners) {
                listener.gemDisconnected(new GemGraphDisconnectionEvent(conn));
            }
        }
    }
    
    /**
     * Check whether an output part can be autoconnected to another gem.
     * @param toGem the gem to which to connect
     * @param fromPart the part from which to connect
     * @param context the context in which the connection should be made
     * @return PartInput the input part to which the toGem can connect unambiguously, if any.
     */
    static public PartInput isAutoConnectable(Gem toGem, Gem.PartOutput fromPart, ConnectionContext context) {
    
        // get the list of unbound, unburnt parts in toGem's tree
        List<PartInput> inputList = obtainUnboundDescendantInputs(toGem, TraversalScope.TREE, InputCollectMode.UNBURNT_ONLY);
    
        // iterate through the parts and look for a match
        PartInput connectableInput = null;       
        
        for (int i = 0, numParts = inputList.size(); i < numParts; i++) {
        
            PartInput input = inputList.get(i);
    
            // check for parts which come from this gem, and can connect
            if (input.getGem() == toGem && 
                (isCompositionConnectionValid(fromPart, input, context.getContextModuleTypeInfo()) || 
                 isDefaultableValueGemSource(fromPart, input, context) ||
                 (isValidConnectionToRecordFieldSelection(fromPart, input, context.getContextModuleTypeInfo()) != null))) {

                // Do we have more than one match?
                if (connectableInput != null) {
                    return null;
                }
                connectableInput = input;
            }
        }
    
        // unambiguous match
        return connectableInput;
    }

    /**
     * Return whether a source part belongs to a value gem that can be defaulted to a sink part's type.
     * @param sourcePart the source part to check
     * @param sinkPart the sink part against which to attempt defaulting
     * @param context the context in which the connection should be made
     * @return boolean true if the gem source is a value gem can be defaulted to the sink part's type
     */
    public static boolean isDefaultableValueGemSource(Gem.PartConnectable sourcePart, Gem.PartInput sinkPart, ConnectionContext context) {

        if (sinkPart.isBurnt() || sinkPart.isConnected()) {
            return false;
        }
    
        TypeExpr sourceType = sourcePart.getType();
        TypeExpr destType = sinkPart.getType();
        
        if (destType == null || destType.isFunctionType()) {
            return false;
        }
    
        Gem sourceGem = sourcePart.getGem();
    
        // the source gem must be a value gem that contains parametric values.
        if (sourceGem instanceof ValueGem && ((ValueGem)sourceGem).containsParametricValues()) {
    
            // see if we can automatically specialize the source type to the destination type,
            // and if value editors can handle the destination type
            
            try {
                if (TypeExpr.canUnifyType(destType, sourceType, context.getContextModuleTypeInfo()) &&
                    context.getValueEditorManager().canInputDefaultValue(TypeExpr.unify(destType, sourceType, context.getContextModuleTypeInfo()))) {

                    return true;
                }
            } catch (TypeException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return false;
    }

    /**
     * Adds the specified gem connection listener to receive gem connection events from this gem .
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the gem connection listener.
     */
    public synchronized void addGemConnectionListener(GemConnectionListener l) {
        if (l == null) {
            return;
        }
        gemConnectionListener = GemEventMulticaster.add(gemConnectionListener, l);
    }

    /**
     * Removes the specified gem connection listener so that it no longer receives gem connection events from this gem. 
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param l the gem connection listener.
     */
    public synchronized void removeGemConnectionListener(GemConnectionListener l) {
        if (l == null) {
            return;
        }
        gemConnectionListener = GemEventMulticaster.remove(gemConnectionListener, l);
    }
    
    /**
     * Adds a new gem GraphChangeListener to the list
     * @param gemGraphChangeListener
     */
    public void addGraphChangeListener(GemGraphChangeListener gemGraphChangeListener) {
        if (!gemGraphChangeListeners.contains(gemGraphChangeListener)) {
            gemGraphChangeListeners.add(gemGraphChangeListener);            
        }
    }
}
