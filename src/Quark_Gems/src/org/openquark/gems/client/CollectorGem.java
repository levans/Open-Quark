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
 * CollectorGem.java
 * Creation date: (09/19/01 6:17:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.swing.undo.StateEditable;

import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALPersistenceHelper;
import org.openquark.gems.client.GemGraph.GemVisitor;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceConstants;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A CollectorGem represents a "let" definition in CAL.  The gem tree connected to the collector defines the let.  
 * For a given target aggregator, there can only ever be one instance of a collector with a given name.
 * Upon creation, collectors start out with the name given by DEFAULT_NAME.
 * 
 * @author Edward Lam
 */
public final class CollectorGem extends Gem implements CompositionNode.Collector, NamedGem {

    /**
     * A default name for a collector.  
     * This is needed to pass validation on creation, but we shouldn't ever see this in the GemCutter.
     */
    private static final String DEFAULT_NAME = "unassignedCollector";

    /**
     * Collectors use function metadata to store their design time metadata. The function metadata
     * needs a CALFeatureName for a function. We use this module name as the module name for the function
     * and the default collector name as the function name. 
     */
    private static final ModuleName DEFAULT_MODULE_NAME = ModuleName.make("TableTopModule");
    
    /** The CALFeatureName all Collectors use for their FunctionMetadata. */
    public static final CALFeatureName COLLECTOR_FEATURE_NAME = CALFeatureName.getFunctionFeatureName(QualifiedName.make(DEFAULT_MODULE_NAME, DEFAULT_NAME));
    
    /** The name of the collector */
    private String collectorName = DEFAULT_NAME;

    /** The emitters associated with this collector. */
    private final Set<ReflectorGem> emitterSet;
    
    /** The metadata for this collector. */
    private FunctionMetadata metadata;

    /** The collector which defines the scope in which this collector is defined.  If null, the collector is defined at the top-level. */
    private CollectorGem targetCollector;
    
    /** The declared type for the (possibly local) function whose definition is rooted at this collector. */
    private TypeExpr declaredType = null;

    /** The set of arguments targeting this collector. 
     *  This contains all arguments which target this collector, even those which may not appear
     *  in the source generated for the definition this subtree represents (because the argument is unused..). */
    private Set<PartInput> targetingArgumentSet;

    /** The list of inputs whose collector arguments target this collector, such that they appear in the 
     *   corresponding definition. */
    private List<PartInput> reflectedInputList = new ArrayList<PartInput>();
    
    /** The ArgumentStateEditable for this collector. */
    private final ArgumentStateEditable argumentState = new ArgumentStateEditable();

    /**
     * This is a simple class to encapsulate a state editable for the collector's argument state.
     * @author Edward Lam
     */
    private class ArgumentStateEditable implements StateEditable {

        /* 
         * Keys for fields in map used with state editable interface 
         */
        private static final String TARGETING_ARGUMENTS_KEY = "TargetingArgumentStateKey";
        private static final String REFLECTED_INPUTS_KEY = "ReflectedInputsStateKey";

        /**
         * {@inheritDoc}
         */
        public void restoreState(Hashtable<?, ?> state) {
            Object stateValue;
            
            stateValue = state.get(new Pair<CollectorGem, String>(CollectorGem.this, TARGETING_ARGUMENTS_KEY));
            if (stateValue != null) {
                targetingArgumentSet.clear();
                targetingArgumentSet.addAll(UnsafeCast.<Set<PartInput>>unsafeCast(stateValue));
            }

            boolean changedTargetingArguments = (stateValue != null);
            
            stateValue = state.get(new Pair<CollectorGem, String>(CollectorGem.this, REFLECTED_INPUTS_KEY));
            if (stateValue != null) {
                setReflectedInputs(UnsafeCast.<List<PartInput>>unsafeCast(stateValue));

            } else if (changedTargetingArguments) {
                // TODOEL: (hackish) Even though the reflected inputs didn't change, fire a reflected input event.
                //  The argument explorer uses this event to update its display.
                // In the future, we may want to change it so that this is an "argument update" event.  This would fire 
                //  when clients make changes to the collector's targeting arguments, and say that the reflected inputs should update.
                Gem.PartInput[] oldParts = reflectedInputList.toArray(new Gem.PartInput[reflectedInputList.size()]);
                fireReflectedInputEvent(oldParts);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void storeState(Hashtable<Object, Object> state) {
            // Now store all the states.  The state key we use is a pair: this code gem and the field key.
            state.put(new Pair<CollectorGem, String>(CollectorGem.this, TARGETING_ARGUMENTS_KEY), new LinkedHashSet<PartInput>(targetingArgumentSet));
            state.put(new Pair<CollectorGem, String>(CollectorGem.this, REFLECTED_INPUTS_KEY), new ArrayList<PartInput>(reflectedInputList));
        }
    }
    
    /**
     * Constructor for a CollectorGem.
     * @param targetCollector the collector which defines the scope in which this collector is defined.  
     *   If null, the collector is defined at the top-level -- at least until the time when it is added to a gem graph, 
     *   at which time, it will be re-targeted to the gem graph's target.
     */
    public CollectorGem(CollectorGem targetCollector) {
        this();
        this.targetCollector = targetCollector;
    }

    /**
     * Constructor for a CollectorGem with no target collector.
     */
    public CollectorGem() {
        // Initialize with the collecting part.
        super(1);
        
        // Create the set to hold emitters
        emitterSet = new HashSet<ReflectorGem>();

        // Initialize the argument lists
        targetingArgumentSet = new LinkedHashSet<PartInput>();
        
        // Initialize the metadata object.
        metadata = new FunctionMetadata(COLLECTOR_FEATURE_NAME, GemCutter.getLocaleFromPreferences());
    }

    /**
     * Adds the specified listener to receive reflected input change events from this gem.
     * @param l the reflected input change listener
     */ 
    public void addReflectedInputListener(ReflectedInputListener l) { 
        listenerList.add(ReflectedInputListener.class, l);
    }

    /**
     * Removes the specified reflected input change listener so that it no longer receives reflected input change events from this gem.
     * @param l the reflected input change listener
     */ 
    public void removeReflectedInputListener(ReflectedInputListener l) {
        listenerList.remove(ReflectedInputListener.class, l);
    }

    /**
     * Fires an reflected input change event.
     * @param oldParts the old reflected input parts.
     */
    private void fireReflectedInputEvent(PartInput[] oldParts) {
        Object[] listeners = listenerList.getListenerList();
        
        ReflectedInputEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ReflectedInputListener.class) {
                if (e == null) {
                    // Lazily create the event:
                    e = new ReflectedInputEvent(this, oldParts);
                }
                ((ReflectedInputListener) listeners[i + 1]).reflectedInputsChanged(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PartInput createInputPart(int inputNum) {
        if (inputNum != 0) {
            throw new IllegalArgumentException("The input number of a collector input must be 0.");
        }
        return new CollectingPart();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected PartOutput createOutputPart() {
        // This gem does not have an output part.
        return null;
    }
    
    /**
     * Get the reflectors associated with this collector.
     * @return the (unmodifiable) set of reflectors associated with this collector.
     */
    public Set<ReflectorGem> getReflectors() {
        return Collections.unmodifiableSet(emitterSet);
    }

    /**
     * Associate a reflector with this collector.
     * @param reflectorGem the reflector to associate
     * @return boolean whether or not the element was added
     */
    boolean addReflector(ReflectorGem reflectorGem) {
        return emitterSet.add(reflectorGem);
    }

    /**
     * Dissociate a reflector from this collector.
     * @param reflectorGem the reflector to dissociate
     * @throws IllegalArgumentException if the reflector does not belong to this collector.
     */
    void removeReflector(ReflectorGem reflectorGem) {
        if (!emitterSet.remove(reflectorGem)) {
            throw new IllegalArgumentException("The specified reflector does not belong to this collector");
        }
    }
    
    /**
     * Get the name of the Gem (the name of the underlying let)
     * @return QualifiedName the name of the gem
     */
    public String getUnqualifiedName() {
        return collectorName;
    }

    /**
     * Set the name of the Gem (the name of the underlying let)
     * @param newName String the new (unqualified) name
     */
    public void setName(String newName) {
        String newUnqualifiedName = newName;
        
        // notify listeners
        if (collectorName == null || !collectorName.equals(newUnqualifiedName)) {

            String oldName = collectorName;
            collectorName = newUnqualifiedName;
            
            if (nameChangeListener != null) {
                nameChangeListener.nameChanged(new NameChangeEvent(CollectorGem.this, oldName));
            }
        }
        fireInputNameEvent(getCollectingPart());
    }
    
    /**
     * {@inheritDoc}
     */
    public CompositionNode.Collector getTargetCollector() {
        return targetCollector;
    }

    /**
     * Set the target collector for this collector.
     */
    public void setTargetCollector(CollectorGem targetCollector) {
        // Circular references for target collectors (a->b->c->a) are illegal and we could check and prevent
        // such an occurance.  As a quick safety check we'll prevent the degenerate case of a->a.
        if (targetCollector == this) {
            throw new IllegalArgumentException("The target for a collector can not be set to itself");
        }
            
        this.targetCollector = targetCollector;
    }

    /**
     * Returns the target collector for which this collector is defined.
     * @return the target collector for which this collector is defined, or null if this is a top-level collector.
     *   The target collector defines the scope of a collector -- the collector definition is visible for other subtrees
     *   targeting the same collector, or any collector definitions occurring within those subtrees.
     */
    public CollectorGem getTargetCollectorGem() {
        return targetCollector;
    }
    
    /**
     * Return whether a given collector gem is enclosed by this one.
     *   This means that the gem is an inner scope of this collector.
     * @param otherCollectorGem
     * @return whether the given collector gem is enclosed by this one.
     *   Also returns true if otherCollectorGem is the same as this one.
     * 
     */
    public boolean enclosesCollector(CollectorGem otherCollectorGem) {
        for (CollectorGem collectorGem = otherCollectorGem; collectorGem != null; collectorGem = collectorGem.getTargetCollectorGem()) {
            if (collectorGem == this) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the arguments targeting this collector.
     * Note that this will include any arguments targeting this collector which do not appear in its definition
     *   (ie. if they are unused).
     * @return the list of arguments targeting this collector.
     */
    public final List<PartInput> getTargetArguments() {
        return new ArrayList<PartInput>(targetingArgumentSet);
    }
    
    /**
     * Return whether a given argument is registered as a target on this collector.
     * @param collectorArgument the argument in question.
     * @return whether the given argument is registered as a target on this collector.
     */
    public final boolean isTargetedBy(PartInput collectorArgument) {
        return targetingArgumentSet.contains(collectorArgument);
    }
    
    /**
     * @param otherCollector another collector.
     * @return whether this collector's definition is visible to otherCollector.
     */
    public final boolean isVisibleTo(CollectorGem otherCollector) {
        // If this collector is the target collector, it's visible.
        if (targetCollector == null) {
            return true;
        }
        
        CollectorGem otherCollectorTarget = otherCollector.getTargetCollectorGem();
        
        // If the other collector is the target collector (and this one isn't), only true if other collector equals or is the target of this one.
        if (otherCollectorTarget == null) {
            return this == otherCollector || targetCollector == otherCollector;
        }
        
        // For one collector to be visible to another, it must be the same collector, a sibling, an ancestor, 
        //   a sibling of an ancestor, or a child.
        // This can be simplified to: its target must enclose the other collector.
        return targetCollector.enclosesCollector(otherCollector);
    }
    
    /**
     * Get the inputs for arguments targeting this collector, and which also appear in its definition.
     * @return the inputs whose arguments appear in this collector's definition.
     */
    public final List<PartInput> getReflectedInputs() {
        return new ArrayList<PartInput>(reflectedInputList);
    }

    /**
     * Set the inputs which target this collector, and also appear in its definition.
     * @param newReflectedInputs the inputs whose arguments should appear in this collector's definition.
     */
    private final void setReflectedInputs(List<PartInput> newReflectedInputs) {
        Gem.PartInput[] oldParts = reflectedInputList.toArray(new Gem.PartInput[reflectedInputList.size()]);
        reflectedInputList = new ArrayList<PartInput>(newReflectedInputs);
        fireReflectedInputEvent(oldParts);
    }
    
    /**
     * Return whether a given argument is reflected by this collector.
     * @param collectorArgument the argument in question.
     * @return whether the given argument is reflected by this collector.
     */
    public final boolean isReflected(PartInput collectorArgument) {
        return reflectedInputList.contains(collectorArgument);
    }
    
    /**
     * Update the reflected arguments on this collector, and associated reflectors.
     * This reconciles the current gem tree with the arguments which target this collector to determine
     *   what arguments appear on corresponding reflectors.
     * This method should be called whevever the arguments targeting this collector are changed.
     * @return Map from any reflectors which changed to their old input parts.
     */
    public Map<ReflectorGem, PartInput[]> updateReflectedInputs() {
        setReflectedInputs(calculateReflectedInputs());
        
        Map<ReflectorGem, PartInput[]> affectedReflectorsToOldInputsMap = new HashMap<ReflectorGem, PartInput[]>();
        
        // Update associated reflectors.
        for (final ReflectorGem reflector : getReflectors()) {
            PartInput[] oldInputs = reflector.getInputParts();
            if (reflector.updateForCollector(reflectedInputList)) {
                affectedReflectorsToOldInputsMap.put(reflector, oldInputs);
            }
        }
        
        return affectedReflectorsToOldInputsMap;
    }
    
    /**
     * Calculate the arguments which should be reflected on this collector, and associated reflectors.
     * This reconciles the current gem tree with the arguments which target this collector to determine
     *   what arguments appear on corresponding reflectors.
     * @return the calculated inputs which should be reflected on associated reflectors.
     */
    private List<PartInput> calculateReflectedInputs() {
        // Get all the descendant inputs.
        List<PartInput> forestInputs = 
                GemGraph.obtainUnboundDescendantInputs(this, GemGraph.TraversalScope.FOREST, GemGraph.InputCollectMode.UNBURNT_ONLY);

        // Get all the arguments on those inputs which actually target this gem.
        Set<PartInput> relevantTargetingArgs = new HashSet<PartInput>();
        for (final Gem.PartInput inputArgument : forestInputs) {

            if (this.targetingArgumentSet.contains(inputArgument)) {
                // Ignore recursive use.
                if (inputArgument.getGem() instanceof ReflectorGem && ((ReflectorGem)inputArgument.getGem()).getCollector() == this) {
                    continue;
                }
                
                relevantTargetingArgs.add(inputArgument);
            }
        }
        
        // The reflected argument list is simply the intersection of the arguments from this scope with the targeting argument list.
        Set<PartInput> reflectedArgumentSet = new LinkedHashSet<PartInput>(targetingArgumentSet);
        reflectedArgumentSet.retainAll(relevantTargetingArgs);
        
        // Construct the input list.
        List<PartInput> calculatedReflectedInputList = new ArrayList<PartInput>(reflectedArgumentSet.size());
        for (final PartInput partInput : reflectedArgumentSet) {
            calculatedReflectedInputList.add(partInput);
        }
        
        return calculatedReflectedInputList;
    }
    
    /**
     * Update this collector's argument list by adding an argument to the end of its current targeting arguments list.
     * @param newArg the argument to add.
     */
    public void addArgument(Gem.PartInput newArg) {
        addArguments(Collections.singletonList(newArg));
    }

    /**
     * Update this collector's argument list by adding a set of arguments to the end of its current targeting arguments list.
     * @param newArgs the arguments to add.
     */
    public void addArguments(Collection<PartInput> newArgs) {
        addArguments(null, newArgs, true);
    }
    
    /**
     * Update this collector's argument list by adding a set of arguments.
     * @param inputArgument the input argument defining where to add the arguments, 
     *   or null to add to the beginning/end of the set.
     * @param newArgs the arguments to add.
     * @param addAfter true to add after the provided argument, false to add before.
     */
    public void addArguments(PartInput inputArgument, Collection<PartInput> newArgs, boolean addAfter) {
        // Check for nothing to do.
        if (newArgs.isEmpty()) {
            return;
        }
        
        // (slow) sanity check.
        Set<PartInput> targetingArgumentSetCopy = new HashSet<PartInput>(targetingArgumentSet);
        targetingArgumentSetCopy.retainAll(newArgs);
        if (!targetingArgumentSetCopy.isEmpty()) {
            throw new IllegalArgumentException("Attempt to add a duplicate argument.");
        }
        for (final PartInput newArg : newArgs) {
            if (newArg.isConnected()) {
                throw new IllegalArgumentException("Attempt to add a connected input as an argument: " + newArg);
            }
        }
        
        List<PartInput> targetingArgumentList = new ArrayList<PartInput>(targetingArgumentSet);
        int addIndex;
        if (inputArgument == null) {
            addIndex = addAfter ? targetingArgumentList.size() - 1 : 0;
            
        } else {
            addIndex = targetingArgumentList.indexOf(inputArgument);
            if (addIndex < 0) {
                throw new IllegalArgumentException("Input argument not found.");
            }
        }
        
        if (addAfter) {
            addIndex++;
        }
        targetingArgumentList.addAll(addIndex, newArgs);
        
        this.targetingArgumentSet = new LinkedHashSet<PartInput>(targetingArgumentList);
    }

    /**
     * Update this collector's argument list by adding a set of arguments.
     * @param addIndex the index at which to add the arguments.
     * @param newArgs the arguments to add.
     */
    public void addArguments(int addIndex, Collection<PartInput> newArgs) {
        // Check for nothing to do.
        if (newArgs.isEmpty()) {
            return;
        }
        
        // (slow) sanity check.
        Set<PartInput> targetingArgumentSetCopy = new HashSet<PartInput>(targetingArgumentSet);
        targetingArgumentSetCopy.retainAll(newArgs);
        if (!targetingArgumentSetCopy.isEmpty()) {
            throw new IllegalArgumentException("Attempt to add a duplicate argument.");
        }
        for (final PartInput newArg : newArgs) {
            if (newArg.isConnected()) {
                GemCutter.CLIENT_LOGGER.log(Level.WARNING, "Attempt to add a connected input as an argument: " + newArg);
            }
        }
        
        List<PartInput> targetingArgumentList = new ArrayList<PartInput>(targetingArgumentSet);
        targetingArgumentList.addAll(addIndex, newArgs);
        
        this.targetingArgumentSet = new LinkedHashSet<PartInput>(targetingArgumentList);
    }

    /**
     * Update this collector's argument list by removing a given argument.
     * @param argumentToRemove the argument to remove from this collector.
     */
    public void removeArgument(PartInput argumentToRemove) {
        if (!targetingArgumentSet.remove(argumentToRemove)) {
            throw new IllegalArgumentException("Argument not found.");
        }
    }
    
    /**
     * Update this collector's argument list by removing the given arguments.
     * @param argumentsToRemove the arguments to remove from this collector.
     */
    public void removeArguments(Collection<PartInput> argumentsToRemove) {
        if (!targetingArgumentSet.containsAll(argumentsToRemove)) {
            throw new IllegalArgumentException("Some arguments not found.");
        }
        targetingArgumentSet.removeAll(argumentsToRemove);
    }

    /**
     * Update this collector's argument list by replacing a given argument with a set of arguments.
     * @param replacedArgument the argument to replace.
     * @param newArgs the arguments with which the argument should be replaced.
     */
    public void replaceArgument(PartInput replacedArgument, Set<PartInput> newArgs) {

        // Create a list of the targeting arguments, find the index of the replaced argument.
        List<PartInput> targetingArgumentList = new ArrayList<PartInput>(targetingArgumentSet);
        int replacementIndex = targetingArgumentList.indexOf(replacedArgument);
        if (replacementIndex < 0) {
            throw new IllegalArgumentException("Input argument not found.");
        }
        for (final PartInput newArg : newArgs) {
            if (newArg.isConnected()) {
                GemCutter.CLIENT_LOGGER.log(Level.WARNING, "Attempt to add a connected input as an argument: " + newArg);
            }
        }

        // Remove the replaced argument, add the new args.
        targetingArgumentList.remove(replacementIndex);
        targetingArgumentList.addAll(replacementIndex, newArgs);

        // Set this collector's targeting arguments.
        this.targetingArgumentSet = new LinkedHashSet<PartInput>(targetingArgumentList);
    }

    /**
     * @return a copy of the design metadata for this collector.
     */
    public FunctionMetadata getDesignMetadata() {
        return (FunctionMetadata) metadata.copy();
    }
    
    /**
     * Set the new metadata for this collector. A safe copy will be made.
     * @param metadata the new design metadata.
     */
    public void setDesignMetadata(FunctionMetadata metadata) {
        metadata.copyTo(this.metadata);
    }
    
    /**
     * Replaces the metadata for this collector with empty metadata.
     */
    public void clearDesignMetadata() {
        metadata = new FunctionMetadata(COLLECTOR_FEATURE_NAME, GemCutter.getLocaleFromPreferences());
    }

    /**
     * {@inheritDoc}
     */
    public TypeExpr getDeclaredType() {
        if (declaredType == null) {
            return null;
        }
        return declaredType.copyTypeExpr();
    }

    /**
     * Set the declared type for the (possibly local) function whose definition is rooted at this collector
     * @param declaredType the declared type, or null to clear.
     */
    public void setDeclaredType(TypeExpr declaredType) {
        this.declaredType = (declaredType == null) ? null : declaredType.copyTypeExpr();
    }

    /**
     * If the target is connected (and thus the result output has a TypeExpr),
     * then this method returns it. Else, returns null.
     * @return TypeExpr the type of the result associated with this collector.
     */
    @Override
    public TypeExpr getResultType() {
        return getCollectingPart().getType();
    }
        
    /**
     * Set the output type of this root gem
     * @param outType TypeExpr the new output type of this gem (which is root)
     */
    @Override
    void setRootOutputType(TypeExpr outType){
        // this gem is always the root.  Setting its type sets the type emitted.
        getCollectingPart().setType(outType);
    }

    /**
     * Assuming that this Gem is actually part of a tree rather than a graph, this method returns
     * the Gem at the root of the tree.
     * @return Gem the Gem at the root of the tree
     */
    @Override
    public Gem getRootGem() {
        // A CollectorGem's root is itself
        return this;
    }

    /**
     * Get the collecting part for the collector.
     * @return CollectingPart the collecting part for this CollectorGem
     */
    public CollectingPart getCollectingPart() {
        return (CollectingPart)getInputPart(0);
    }

    /**
     * Describe this Gem
     * @return the description
     */
    @Override
    public String toString() {
        String targetString = targetCollector == null ? "" : " (Target: " + targetCollector.getUnambiguousStringId() + ")";
        return "Collector " + getUnambiguousStringId() + targetString;
    }
    
    /**
     * Get a string identifier which unambiguously identifies this collector wrt the string id's of other collectors
     *  (especially other collectors with the same unqualified name).
     * @return an unambiguous string id for this collector.
     */
    private String getUnambiguousStringId() {
        return getUnqualifiedName() + " " + System.identityHashCode(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void visitAncestors(GemVisitor gemVisitor, Set<Gem> gemsVisited){
        // visit
        if (seeVisitor(gemVisitor, gemsVisited)) {
            return;
        }

        // traverse reflectors if appropriate
        GemGraph.TraversalScope visitorScope = gemVisitor.getTraversalScope();
        if (visitorScope == GemGraph.TraversalScope.FOREST) {
            Set<ReflectorGem> reflectors = getReflectors();
            for (final ReflectorGem rg : reflectors) {
                rg.visitAncestors(gemVisitor, gemsVisited);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void visitConnectedTrees(GemVisitor gemVisitor, Set<Gem> gemsVisited){

        if (gemVisitor.getTraversalScope() != GemGraph.TraversalScope.FOREST) {
            throw new IllegalArgumentException("Expecting a forest visitor.");
        }

        // visit
        if (seeVisitor(gemVisitor, gemsVisited)) {
            return;
        }

        // Follow the input connection
        Connection inputConnection = getInputPart(0).getConnection();
        if (inputConnection != null) {
            inputConnection.getSource().getGem().visitConnectedTrees(gemVisitor, gemsVisited);
        }

        if (gemVisitor.getTraversalScope() == GemGraph.TraversalScope.FOREST) {
            // traverse reflectors.
            Set<ReflectorGem> reflectors = getReflectors();
            for (final ReflectorGem reflectorGem : reflectors) {
                reflectorGem.getRootGem().visitConnectedTrees(gemVisitor, gemsVisited);
            }

            // traverse the target, if this doesn't escape the target scope level of the visitor
            CollectorGem targetCollectorGem = getTargetCollectorGem();
            if (targetCollectorGem != null) {
                CollectorGem visitorTargetCollector = gemVisitor.getTargetCollector();
                
                if (visitorTargetCollector != null && !visitorTargetCollector.enclosesCollector(targetCollectorGem)) {
                    return;
                }
                
                targetCollectorGem.visitConnectedTrees(gemVisitor, gemsVisited);
            }
        }
        
    }
    
    /**
     * Returns true if this Gem has been given a name.
     * @return boolean
     */
    boolean isNameInitialized() {
        // If the name is not the same object as the default name then it has been initialized.
        return DEFAULT_NAME != collectorName;
    }
    
    /**
     * @return a StateEditable for this collector's arguments.
     */
    StateEditable getArgumentStateEditable() {
        return argumentState;
    }
    
    /*
     * Methods supporting XMLPersistable                 ********************************************
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveXML(Node parentNode, GemContext gemContext) {
        
        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the collector gem element
        Element resultElement = document.createElementNS(GemPersistenceConstants.GEM_NS, GemPersistenceConstants.COLLECTOR_GEM_TAG);
        resultElement.setPrefix(GemPersistenceConstants.GEM_NS_PREFIX);
        parentNode.appendChild(resultElement);

        // Add info for the superclass gem.
        super.saveXML(resultElement, gemContext);


        // Now add CollectorGem-specific info

        // Target collector (if any).
        if (targetCollector != null) {
            resultElement.setAttribute(GemPersistenceConstants.COLLECTOR_GEM_TARGET_COLLECTOR_ATTR, gemContext.getIdentifier(targetCollector, true));
        }
        
        // Add an element for the name.
        Element nameElement = CALPersistenceHelper.unqualifiedNameToElement(getUnqualifiedName(), document);
        resultElement.appendChild(nameElement);
        
        // This was left in for several months, ending Oct 27/03.  We may want to re-institute for non-top-level collectors?
//        // Save the design metadata
//        metadata.saveXML(resultElement);
        
        // Add an element for the arguments.
        Element argumentsElement = document.createElement(GemPersistenceConstants.ARGUMENTS_TAG);
        resultElement.appendChild(argumentsElement);
        
        // Add elements for the arguments (if any)
        for (final PartInput targetingInput : targetingArgumentSet) {
            
            // Get the element.
            Element argumentElement = GemCutterPersistenceHelper.inputToArgumentElement(targetingInput, document, gemContext);
            
            // Add an attribute saying whether the argument is reflected.
            boolean reflected =  reflectedInputList.contains(targetingInput);
            argumentElement.setAttribute(GemPersistenceConstants.ARGUMENT_REFLECTED_ATTR, reflected ? XMLPersistenceConstants.TRUE_STRING : XMLPersistenceConstants.FALSE_STRING);
            
            // Add the element.
            argumentsElement.appendChild(argumentElement);
        }
    }

    /**
     * Create a new CollectorGem and loads its state from the specified XML element.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo the argument info for this load session.
     * @return CollectorGem
     * @throws BadXMLDocumentException
     */
    public static CollectorGem getFromXML(Element gemElement, GemContext gemContext, Argument.LoadInfo loadInfo) throws BadXMLDocumentException {
        CollectorGem gem = new CollectorGem();
        gem.loadXML(gemElement, gemContext, loadInfo);
        return gem;
    }

    /**
     * Load this object's state.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo the argument info for this load session.
     */
    void loadXML(Element gemElement, GemContext gemContext, Argument.LoadInfo loadInfo) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(gemElement, GemPersistenceConstants.COLLECTOR_GEM_TAG);
        XMLPersistenceHelper.checkPrefix(gemElement, GemPersistenceConstants.GEM_NS_PREFIX);

        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemElement);
        int nChildElems = childElems.size();

        // Get info for the underlying gem.        
        Element superGemElem = (nChildElems < 1) ? null : (Element) childElems.get(0);
        XMLPersistenceHelper.checkIsElement(superGemElem);
        super.loadXML(superGemElem, gemContext);

        // Get the name
        Element nameElem = (nChildElems < 2) ? null : (Element) childElems.get(1);
        XMLPersistenceHelper.checkIsElement(nameElem);
        String gemName = CALPersistenceHelper.elementToUnqualifiedName(nameElem);

        // Set the target aggregator if any.  If the attribute is not present, the target aggregator is null.
        String targetCollectorAttribute = gemElement.getAttribute(GemPersistenceConstants.COLLECTOR_GEM_TARGET_COLLECTOR_ATTR);
        if (!targetCollectorAttribute.equals("")) {
            Gem gem = gemContext.getGem(targetCollectorAttribute);
            if (gem == null) {
                // TODOEL: these messages really should go into a Status object.
                GemCutter.CLIENT_LOGGER.log(Level.WARNING, "The target collector could not be found for this collector.");
                targetCollector = null;
                
            } else if (!(gem instanceof CollectorGem)) {
                GemCutter.CLIENT_LOGGER.log(Level.WARNING, "The target collector id for this collector does not correspond to an collector gem.");
                targetCollector = null;
                
            } else {
                targetCollector = (CollectorGem)gem;
            }
            
        } else {
            targetCollector = null;
        }

        collectorName = gemName;
        emitterSet.clear();

        // Get the arguments
        Element argumentsElement = (nChildElems < 3) ? null : (Element) childElems.get(2);
        XMLPersistenceHelper.checkIsElement(argumentsElement);

        List<Element> argumentsChildElems = XMLPersistenceHelper.getChildElements(argumentsElement);
        int nArgumentsChildElems = argumentsChildElems.size();
        
        for (int i = 0; i < nArgumentsChildElems; i++) {
            Element orphanedInputChildElem = argumentsChildElems.get(i);
            Pair<String, Integer> orphanedInputInfoPair = GemCutterPersistenceHelper.argumentElementToInputInfo(orphanedInputChildElem);
            
            // Add the argument info the the load info.  Extra info is the boolean attribute saying whether this input is reflected.
            boolean isReflected = XMLPersistenceHelper.getBooleanAttribute(orphanedInputChildElem, GemPersistenceConstants.ARGUMENT_REFLECTED_ATTR);
            loadInfo.addArgument(this, orphanedInputInfoPair.fst(), orphanedInputInfoPair.snd(), Boolean.valueOf(isReflected));
        }
    }
    
    /**
     * Populate internal argument state during loading.
     * @param loadInfo
     * @param gemContext
     */
    public void loadArguments(Argument.LoadInfo loadInfo, GemContext gemContext) throws BadXMLDocumentException {

        List<PartInput> newReflectedInputList = new ArrayList<PartInput>();
        this.targetingArgumentSet = new LinkedHashSet<PartInput>();

        for (int i = 0, nArguments = loadInfo.getNArguments(this); i < nArguments; i++) {
            Pair<String, Integer> inputInfo = loadInfo.getInputInfo(this, i);
            boolean isReflected = ((Boolean)loadInfo.getOtherInfo(this, i)).booleanValue();

            Gem inputGem = gemContext.getGem(inputInfo.fst());
            int inputIndex = (inputInfo.snd()).intValue();
            
            if (inputGem == null) {
                throw new BadXMLDocumentException(null, "Argument gem not found.");
            }
            
            Gem.PartInput targetingInput = inputGem.getInputPart(inputIndex);
            
            targetingArgumentSet.add(targetingInput);
            if (isReflected) {
                newReflectedInputList.add(targetingInput);
            }
        }
        
        setReflectedInputs(newReflectedInputList);
    }
    
    /**
     * Makes a copy of the specified gem. This function will check if there
     * is a gem that has the same name. If so, it will add a disambiguating
     * suffix to the name, unless the keepOriginalName flag is set to true,
     * in which case a 'blind' copy is completed.
     * @return CollectorGem;
     */
    CollectorGem makeCopy(){
        
        // Create a new collector.
        CollectorGem collectorGem = new CollectorGem(targetCollector);
        String name = getUnqualifiedName();
        collectorGem.setName(name);
        collectorGem.setDesignMetadata(getDesignMetadata());
        
        return collectorGem;
     }
     
    /**
     * The PartConnectable representing the collecting part of the CollectorGem
     * Creation date: (09/20/01 11:31:00 AM)
     * @author Edward Lam
     */
    public class CollectingPart extends PartInput {
        /**
         * Default constructor for a collecting part.
         */
        private CollectingPart() {
            // always the first "input"
            super(0);

            // Collecting Parts start out with a parametric type 'a'.
            TypeExpr newType = TypeExpr.makeParametricType();
            setType(newType);
            
            // Ensure that this part's name stays in sync with the collector's name.
            setArgumentName(new ArgumentName(collectorName));
            addNameChangeListener(new NameChangeListener() {
                public void nameChanged(NameChangeEvent e) {
                    setArgumentName(new ArgumentName(collectorName));
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        void setInputNum(int inputNum) {
            if (inputNum != 0) {
                throw new IllegalArgumentException("Attempt to change the input number of a collector input.");
            }
        }    

        /**
         * Burn this input
         * @param burnt boolean True to burn, false to unburn.
         */
        @Override
        public void setBurnt(boolean burnt){
            throw new UnsupportedOperationException("Can't burn an input of a collector");
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final String getOriginalInputName() {
            return collectorName;
        }
        
        /**
         * {@inheritDoc}
         */        
        @Override
        final void setOriginalInputName(String originalName) {
            throw new UnsupportedOperationException();
        }
    }
}   
