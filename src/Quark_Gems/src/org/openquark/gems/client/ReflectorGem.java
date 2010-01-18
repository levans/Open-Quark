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
 * ReflectorGem.java
 * Creation date: Mar 21, 2003.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.undo.StateEditable;

import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.gems.client.GemGraph.GemVisitor;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A ReflectorGem represents a local function definition - 
 *   the part of the expression where the CAL definition of the let is actually used (the let function in the "in" part of the let definition).
 * Because Reflectors are defined by the gem tree connected to the associated collector,
 *   we maintain the invariant that a reflector cannot exist without an associated collector.
 * However, multiple reflectors may exist to use the definition of the let in their associated collector.
 * 
 * @author Edward Lam
 */
public final class ReflectorGem extends Gem implements CompositionNode.Emitter, NamedGem, StateEditable {

    /** The CollectorGem to which this reflector refers. */
    private CollectorGem collectorGem;                 // should be final once assigned

    /** Gem broken state. */
    private boolean broken = false;

    /** Map from input in this gem to the corresponding input within the associated collector's definition. */
    private final transient Map<PartInput, PartInput> inputToReflectedInputMap = new WeakHashMap<PartInput, PartInput>();

    /* 
     * Keys for fields in map used with state editable interface 
     */
    private static final String BROKEN_KEY = "BrokenStateKey";
    private static final String INPUT_PARTS_KEY = "InputPartsStateKey";
    private static final String REFLECTED_INPUT_MAP_KEY = "InputToReflectedInputMapStateKey";

    /**
     * Construct an empty reflector gem.
     * A gem created using this constructor is appropriate for use in deserialization.
     */
    private ReflectorGem() {
        // Initialize with no input parts.
        super(0);
    }

    /**
     * Construct a ReflectorGem from a collector.
     * @param collectorGem the Collector with which this reflector is associated.
     */
    public ReflectorGem(CollectorGem collectorGem) {
        // Initialize with no input parts.
        super(0);

        // Set the collector associated with this reflector.
        this.collectorGem = collectorGem;

        // Add a listener to reflect collector name changes.
        addCollectorNameChangeListener();

        // Set the output type.
        getOutputPart().setType(collectorGem.getResultType());

        // Update for collector definition.  This creates the inputParts
        updateForCollector(getCollector().getReflectedInputs());
    }
    
    /**
     * Construct a ReflectorGem from a collector, having a certain number of inputs.
     * The resulting gem will be in an incomplete/inconsistent state -- 
     *   a gem created using this constructor is appropriate for use in deserialization.
     * @param collectorGem the Collector with which this reflector is associated.
     * @param nInputs the number of inputs on this reflector.
     */
    ReflectorGem(CollectorGem collectorGem, int nInputs) {
        // Initialize with the appropriate number of input parts.
        super(nInputs);

        // Set the collector associated with this reflector.
        this.collectorGem = collectorGem;

        // Add a listener to reflect collector name changes.
        addCollectorNameChangeListener();

        // Missing: metadata, part types, reflected input correspondence map.
    }
    
    /**
     * Add a listener to reflect collector name changes.
     *   This should be called on instantiation, after the collector has been set.
     */
    private void addCollectorNameChangeListener() {
        collectorGem.addNameChangeListener(new NameChangeListener() {
            public void nameChanged(NameChangeEvent e) {
                if (nameChangeListener != null) {
                    nameChangeListener.nameChanged(new NameChangeEvent(ReflectorGem.this, e.getOldName()));
                }
            }
        });
    }

    /**
     * Update the types and number of inputs on this reflector to reflect the state of the associated collector gem's subtree.
     * If deserializing, this should be called only when the entire reflector definition subtree is created.
     *   Internal bookkeeping structures will be brought up to date.
     * @param reflectedInputList the inputs for which arguments appear on the corresponding collector.
     * @return whether the inputs changed.  ie. whether the new inputs are different from the old ones.
     */
    boolean updateForCollector(List<PartInput> reflectedInputList) {
        // TODO: figure out what kind of argument metadata to use.

        // Create a set to keep track of which inputs are orphaned - ie. connected, but whose inputs should no longer
        // appear on this reflector.  First get all of the connected reflector inputs.
        Set<PartInput> orphanedInputs = new LinkedHashSet<PartInput>();
        for (int i = 0, nArgs = getNInputs(); i < nArgs; i++) {
            PartInput input = getInputPart(i);
            if (input.isConnected()) {
                orphanedInputs.add(input);
            }
        }
        
        List<PartInput> newInputParts = new ArrayList<PartInput>(reflectedInputList.size());

        int index = 0;
        for (final PartInput partInput : reflectedInputList) {
            PartInput reflectedInput = partInput;
            
            // reuse any existing inputs if possible, otherwise create a new one.
            PartInput input = getReflectingInput(reflectedInput);
            if (input == null) {
                input = createInputPart(index);
                ArgumentMetadata inputMetadata = reflectedInput.getDesignMetadata();
                inputMetadata.setDisplayName(reflectedInput.getArgumentName().getCompositeName());
                input.setDesignMetadata(inputMetadata);
                input.setArgumentName(new ArgumentName(reflectedInput.getArgumentName()));
            } else {
                // update the input num.
                input.setInputNum(index);
                
                // Remove this input from the set of orphaned inputs.
                orphanedInputs.remove(input);
            }
            newInputParts.add(input);
            
            // update the input type.
            TypeExpr inputType = reflectedInput.getType();
            input.setType(inputType);
            
            // update the reflection map
            inputToReflectedInputMap.put(input, reflectedInput);
            
            index++;
        }
        
        // Now add any orphaned inputs.
        broken = !orphanedInputs.isEmpty();
        if (broken) {
            for (final PartInput orphanedInput : orphanedInputs) {
                orphanedInput.setInputNum(index);
                newInputParts.add(orphanedInput);
                
                index++;
            }
        } 
        
        // update the input parts
        boolean inputsChanged = setInputParts(newInputParts.toArray(new PartInput[newInputParts.size()]));
        
        return inputsChanged;
    }
    
    /**
     * Get the input on the collector subtree reflected by an input on this reflector.
     * @param reflectingInput the input on this reflector
     * @return the reflected input corresponding to reflectingInput
     */
    public PartInput getReflectedInput(PartInput reflectingInput) {
        return inputToReflectedInputMap.get(reflectingInput);
    }

    /**
     * Get the input on this reflector which reflects an input on the reflected collector's subtree.
     * @param reflectedInput the input on the reflected collector's subtree.
     * @return the reflecting input corresponding to reflectedInput.  Null if reflectedInput is not actually a reflected input.
     */
    public PartInput getReflectingInput(PartInput reflectedInput) {
        for (int partN = 0, nParts = getNInputs(); partN < nParts; ++partN) {
            PartInput inputPart = getInputPart(partN);
            if (reflectedInput.equals(getReflectedInput(inputPart))) {
                return inputPart;
            }
        }

        return null;
    }

    /**
     * Get the name of the Gem (the name of the underlying let)
     * @return QualifiedName the name of the gem
     */
    public String getUnqualifiedName() {
        return getCollector().getUnqualifiedName();
    }

    /**
     * Get the collector associated with this reflector.
     * @return CollectorGem the collector associated with this reflector.
     */
    public final CollectorGem getCollector() {
        return collectorGem;
    }

    /**
     * {@inheritDoc}
     */
    public CompositionNode.Collector getCollectorNode(){
        // Return the collector.
        return getCollector();
    }

    /**
     * Returns whether this gem is broken
     * @return boolean true if this gem is broken
     */
    @Override
    public boolean isBroken() {
        return broken;
    }

    /**
     * Set the broken state of this Gem
     * @param newBroken boolean the new broken state of the Gem
     */    
    void setBroken(boolean newBroken) {
        if (broken != newBroken) {
            broken = newBroken;
            if (gemStateListener != null) {
                gemStateListener.brokenStateChanged(new GemStateEvent(this, GemStateEvent.EventType.BROKEN));
            }
        }
    }
    
    /**
     * Describe this Gem
     * @return the description
     */
    @Override
    public String toString() {
        return "Emitter " + getUnqualifiedName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void visitDescendants(GemVisitor gemVisitor, Set<Gem> gemsVisited){

        if (gemsVisited.contains(this)) {
            return;
        }

        super.visitDescendants(gemVisitor, gemsVisited);

        if (gemVisitor.getTraversalScope() == GemGraph.TraversalScope.FOREST) {
            
            // also check whether we would escape the target scope of the visitor
            CollectorGem visitorTargetCollector = gemVisitor.getTargetCollector();
            CollectorGem collectorGem = getCollector();
  
            if (visitorTargetCollector != null && !visitorTargetCollector.enclosesCollector(collectorGem)) {
                return;
            }

            collectorGem.visitDescendants(gemVisitor, gemsVisited);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void visitConnectedTrees(GemVisitor gemVisitor, Set<Gem> gemsVisited){

        if (gemsVisited.contains(this)) {
            return;
        }

        super.visitConnectedTrees(gemVisitor, gemsVisited);

        if (gemVisitor.getTraversalScope() == GemGraph.TraversalScope.FOREST) {
            
            // also check whether we would escape the target scope level of the visitor
            CollectorGem visitorTargetCollector = gemVisitor.getTargetCollector();
            CollectorGem collectorGem = getCollector();

            if (visitorTargetCollector != null && !visitorTargetCollector.enclosesCollector(collectorGem)) {
                return;
            }

            collectorGem.visitConnectedTrees(gemVisitor, gemsVisited);
        }
    }


    /*
     * Methods supporting javax.swing.undo.StateEditable ********************************************
     */

    /**
     * Restore the stored gem state.
     * @param state the stored state
     */
    public void restoreState(Hashtable<?, ?> state) {

        Object stateValue;
        
        stateValue = state.get(new Pair<ReflectorGem, String>(ReflectorGem.this, INPUT_PARTS_KEY));
        if (stateValue != null) {
            PartInput[] inputParts = (PartInput[])stateValue;
            for (int i = 0; i < inputParts.length; i++) {
                inputParts[i].setInputNum(i);
            }
            setInputParts((PartInput[])stateValue);
        }

        stateValue = state.get(new Pair<ReflectorGem, String>(ReflectorGem.this, REFLECTED_INPUT_MAP_KEY));
        if (stateValue != null) {
            inputToReflectedInputMap.clear();
            inputToReflectedInputMap.putAll(UnsafeCast.<Map<PartInput, PartInput>>unsafeCast(stateValue));
        }
        
        stateValue = state.get(new Pair<ReflectorGem, String>(ReflectorGem.this, BROKEN_KEY));
        if (stateValue != null) {
            broken = ((Boolean)stateValue).booleanValue();
        }
    }

    /**
     * Save the current gem state.
     * @param state the table in which to store the current gem state
     */
    public void storeState(Hashtable<Object, Object> state) {
        
        // Now store all the states.  The state key we use is a pair: this code gem and the field key.
        state.put(new Pair<ReflectorGem, String>(ReflectorGem.this, INPUT_PARTS_KEY), getInputParts());
        state.put(new Pair<ReflectorGem, String>(ReflectorGem.this, BROKEN_KEY), Boolean.valueOf(isBroken()));
        state.put(new Pair<ReflectorGem, String>(ReflectorGem.this, REFLECTED_INPUT_MAP_KEY), new HashMap<PartInput, PartInput>(inputToReflectedInputMap));
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

        // Create the reflector gem element
        Element resultElement = document.createElementNS(GemPersistenceConstants.GEM_NS, GemPersistenceConstants.EMITTER_GEM_TAG);
        resultElement.setPrefix(GemPersistenceConstants.GEM_NS_PREFIX);
        parentNode.appendChild(resultElement);

        // Add info for the superclass gem.
        super.saveXML(resultElement, gemContext);


        // Now add ReflectorGem-specific info

        // Add an attribute for the name.
        resultElement.setAttribute(GemPersistenceConstants.REFLECTOR_GEM_COLLECTOR_ID_ATTR, gemContext.getIdentifier(collectorGem, true));

        // Add an element for the orphaned inputs.
        Element orphanedInputsElement = document.createElement(GemPersistenceConstants.REFLECTOR_GEM_ORPHANED_INPUTS_TAG);
        resultElement.appendChild(orphanedInputsElement);
        
        for (int i = 0, nInputs = getNInputs(); i < nInputs; i++) {
            Gem.PartInput input = getInputPart(i);
            Gem.PartInput reflectedInput = inputToReflectedInputMap.get(input);
            
            // orphaned if connected.
            if (reflectedInput.isConnected()) {
                orphanedInputsElement.appendChild(GemCutterPersistenceHelper.inputToArgumentElement(reflectedInput, document, gemContext));
            }
        }
    }

    /**
     * Create a new ReflectorGem and loads its state from the specified XML element.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo the argument info for this load session.
     * @return ReflectorGem
     * @throws BadXMLDocumentException
     */
    public static ReflectorGem getFromXML(Element gemElement, GemContext gemContext, Argument.LoadInfo loadInfo) throws BadXMLDocumentException {
        ReflectorGem gem = new ReflectorGem();
        gem.loadXML(gemElement, gemContext, loadInfo);
        return gem;
    }

    /**
     * Load this object's state.
     * Note: serialization will be incomplete until the first call to updateForCollector() is called.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param loadInfo the argument info for this load session.
     */
    void loadXML(Element gemElement, GemContext gemContext, Argument.LoadInfo loadInfo) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(gemElement, GemPersistenceConstants.EMITTER_GEM_TAG);
        XMLPersistenceHelper.checkPrefix(gemElement, GemPersistenceConstants.GEM_NS_PREFIX);

        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemElement);
        int nChildElems = childElems.size();

        // Get info for the underlying gem.
        Element superGemElem = (nChildElems < 1) ? null : (Element) childElems.get(0);
        XMLPersistenceHelper.checkIsElement(superGemElem);
        super.loadXML(superGemElem, gemContext);

        // Get the name
        String collectorId = gemElement.getAttribute(GemPersistenceConstants.REFLECTOR_GEM_COLLECTOR_ID_ATTR);
        
        // Get the collector associated with this reflector
        Gem gemFromCollectorId = gemContext.getGem(collectorId);
        if (gemFromCollectorId == null) {
            XMLPersistenceHelper.handleBadDocument(gemElement, "No corresponding collector found for reflector.");

        } else if (!(gemFromCollectorId instanceof CollectorGem)) {
            XMLPersistenceHelper.handleBadDocument(gemElement, "Collector id for reflector does not correspond to a collector.");
        }
        
        this.collectorGem = (CollectorGem)gemFromCollectorId;
        
        // Add a listener to reflect collector name changes.
        addCollectorNameChangeListener();

        // Now get the orphaned inputs.
        Element orphanedInputsElement = (nChildElems < 2) ? null : (Element) childElems.get(1);
        XMLPersistenceHelper.checkIsElement(orphanedInputsElement);

        List<Element> orphanedInputChildElems = XMLPersistenceHelper.getChildElements(orphanedInputsElement);
        int nOrphanedInputChildElems = orphanedInputChildElems.size();
        
        for (int i = 0; i < nOrphanedInputChildElems; i++) {
            Element orphanedInputChildElem = orphanedInputChildElems.get(i);
            Pair<String, Integer> orphanedInputInfoPair = GemCutterPersistenceHelper.argumentElementToInputInfo(orphanedInputChildElem);
            
            loadInfo.addArgument(this, orphanedInputInfoPair.fst(), orphanedInputInfoPair.snd());
        }

        // Take care of orphaned inputs.

    }

    /**
     * Populate internal argument state during loading.
     * @param loadInfo
     * @param gemContext
     * @throws BadXMLDocumentException
     */
    public void loadArguments(Argument.LoadInfo loadInfo, GemContext gemContext) throws BadXMLDocumentException {

        // Create a list to represent the inputs reflected by this reflector.
        //   Start off with the reflected inputs dictated by the target.
        List<PartInput> reflectedInputs = new ArrayList<PartInput>(collectorGem.getReflectedInputs());
        
        // Now add orphaned inputs.
        int nOrphanedInputs = loadInfo.getNArguments(this);
        for (int i = 0; i < nOrphanedInputs; i++) {
            Pair<String, Integer> inputInfo = loadInfo.getInputInfo(this, i);
            Gem inputGem = gemContext.getGem(inputInfo.fst());
            int inputIndex = inputInfo.snd().intValue();
            
            reflectedInputs.add(inputGem.getInputPart(inputIndex));
        }
        
        // Check that the number of reflected inputs is the same as the number of reflector inputs.
        int nReflectedInputs = reflectedInputs.size();
        if (nReflectedInputs != getNInputs()) {
            // What to do?
            throw new BadXMLDocumentException(null, 
                                              "Number of loaded reflector arguments is inconsistent: " + nReflectedInputs + " != " + getNInputs());
        }
        
        // Populate the inputToReflectedInput map
        for (int i = 0; i < nReflectedInputs; i++) {
            inputToReflectedInputMap.put(getInputPart(i), reflectedInputs.get(i));
        }
    }

    /**
     * Copy a reflector's argument mappings onto this one.  Used during gem copying.
     * This method should only be called when all gems referred to by the original gem have been copied,
     *   and therefore exist in the map that's passed in.
     * 
     * @param originalReflector the original reflector, of which this is a copy.
     * @param oldToNewGemMap map from original gem to gem copy.
     */
    void copyArgumentState(ReflectorGem originalReflector, Map<Gem, Gem> oldToNewGemMap) {
        inputToReflectedInputMap.clear();
        
        Map<PartInput, PartInput> originalInputToReflectedInputMap = originalReflector.inputToReflectedInputMap;
        for (final Map.Entry<PartInput, PartInput> mapEntry : originalInputToReflectedInputMap.entrySet()) {
            
            Gem.PartInput originalReflectorInput = mapEntry.getKey();
            int originalReflectorInputIndex = originalReflectorInput.getInputNum();
            
            Gem.PartInput originalReflectedInput =  mapEntry.getValue();
            Gem originalReflectedInputGem = originalReflectedInput.getGem();
            int originalReflectedInputIndex = originalReflectedInput.getInputNum();
            
            Gem newReflectedInputGem = oldToNewGemMap.get(originalReflectedInputGem);
            if (newReflectedInputGem == null) {
                throw new IllegalArgumentException("Mapping not found for copied gem.");
            }
            
            Gem.PartInput newReflectedInput = newReflectedInputGem.getInputPart(originalReflectedInputIndex);
            inputToReflectedInputMap.put(getInputPart(originalReflectorInputIndex), newReflectedInput);
        }
    }

    
}
