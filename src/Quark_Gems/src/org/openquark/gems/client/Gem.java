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
 * Gem.java
 * Creation date: (12/11/00 8:21:31 AM)
 * By: Luke Evans
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

import javax.swing.event.EventListenerList;

import org.openquark.cal.compiler.CALSourceGenerator;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.TypeException;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.gems.client.GemGraph.GemVisitor;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.NamespaceInfo;
import org.openquark.util.xml.XMLPersistenceConstants;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * The Gem abstract base class.
 * Creation date: (12/11/00 8:21:31 AM)
 * @author Luke Evans
 */
public abstract class Gem implements CompositionNode {

    /** The output of the Gem */
    private final PartOutput outputPart;

    /** The array of input parts */
    private PartInput[] inputParts;

    /** Listener for state changes on this gem. */
    GemStateListener gemStateListener;
    
    /** Listener for input burning changes on this gem. */
    private BurnListener burnListener;

    /** Listener for changes in the name of the gem.*/
    NameChangeListener nameChangeListener;

    /** A list of event listeners for this component. */
    protected final EventListenerList listenerList = new EventListenerList();
    
    /** An empty array of input parts. */
    public static final PartInput[] EMPTY_INPUT_PART_ARRAY = new PartInput[0];

    /**
     * Default constructor for a Gem.
     * @param nArgs the number of initial input parts.
     */
    Gem(int nArgs) {
       
        // Create the output part.
        this.outputPart = createOutputPart();

        // Create the input parts.
        PartInput[] newInputParts = new Gem.PartInput[nArgs];
        for (int i = 0; i < nArgs; i++) {
            newInputParts[i] = createInputPart(i);
        }

        this.inputParts = newInputParts;
    }
    
    /**
     * Create an input part for this gem.
     * @param inputNum the index of the input to be created.
     * @return an input part to be used for this gem.
     */
    protected PartInput createInputPart(int inputNum) {
        return new PartInput(inputNum);
    }

    /**
     * Create the output part for this gem.
     *   This method will be called when the gem is created, to initialize the gem's output part.
     * @return the output part which will be used for this gem.
     */
    protected PartOutput createOutputPart() {
        return new PartOutput();
    }

    /**
     * Get the nth input associated with the gem.  An exception is thrown if n is out of range.
     * @param n int the index of the input to return
     * @return PartInput the nth input in the gem.
     */
    public final PartInput getInputPart(int n) {
        return inputParts[n]; 
    }

    /**
     * Get the inputs for this gem.
     * @return PartInput[] the inputs for the gem
     */
    public final PartInput[] getInputParts() {
        return (inputParts == null) ? EMPTY_INPUT_PART_ARRAY : (PartInput[])inputParts.clone();
    }

    /**
     * Set the inputs for this gem.
     * @param newInputs the new inputs for the gem
     * @return whether the inputs changed.  ie. whether the new inputs are different from the old ones.
     */
    boolean setInputParts(PartInput[] newInputs){
        PartInput[] oldInputs = inputParts;
        this.inputParts = newInputs;

        // notify listeners of any input change
        boolean inputsChanged = !Arrays.equals(oldInputs, this.inputParts);
        if (inputsChanged) {
            fireInputChangeEvent(oldInputs);
        }
        
        return inputsChanged;
    }
    
    /**
     * Returns the number of arguments accepted by this Gem
     * @return int the number of arguments
     */
    public final int getNInputs() {
        return getInputParts().length;
    }

    /**
     * {@inheritDoc}
     */
    public final int getNArguments() {
        return getNInputs();
    }

    /**
     * {@inheritDoc}
     */
    public CompositionNode.CompositionArgument getNodeArgument(final int i) {
        return getInputPart(i);
    }

    /**
     * Return the arguments as would be required by current definition of the Gem tree rooted at this gem.
     * @return the list of inputs required by the target.
     */ 
    public final List<PartInput> getTargetInputs() {
        // Return the argument names in the order they would appear in the target sc.
        List<CompositionArgument> functionArgumentList = Arrays.asList(CALSourceGenerator.getFunctionArguments(this));
        return new ArrayList<PartInput>(UnsafeCast.<List<PartInput>>unsafeCast(functionArgumentList));  // ~ unsafe
    }

    /**
     * Returns the output part.
     * @return PartOutput
     */
    public final PartOutput getOutputPart() {
        return outputPart;
    }
    
    /**
     * Returns whether this Gem is at the root of the tree.  
     * In dealing with collectors and emitters, this method returns the closest collector ancestor.
     * @return boolean true if this gem is at the root of the true
     */
    public final boolean isRootGem() {
        return (getRootGem() == this);
    }    

    /**
     * Returns the Gem at the root of the tree.
     * @return Gem the Gem at the root of the tree
     */
    public Gem getRootGem() {
        // Get the output part
        Connection connectionToParent = outputPart.getConnection();

        // If there is no connection from the output, the root Gem is this one
        if (connectionToParent == null) {
            return this;
        }    

        // Follow the connection
        return connectionToParent.getDestination().getGem().getRootGem();
    }

    /**
     * Returns the root collector for this gem.
     *   The root collector is the collector at the root of the tree to which this gem is connected.
     *   Note that unconnected emitters will return null, not their corresponding collectors.
     * @return CollectorGem the root collector for this gem, or null if the gem at the root is not a collector.
     */
    public CollectorGem getRootCollectorGem() {
        if (this instanceof CollectorGem) {
            return (CollectorGem)this;
        }
        
        Gem outputGem = getOutputPart().getConnectedGem();
        if (outputGem == null) {
            return null;
        }
        
        Gem outputRoot = outputGem.getRootGem();
        if (outputRoot instanceof CollectorGem) {
            return (CollectorGem)outputRoot;
        }

        return null;
    }

    /**
     * Return whether this target is runnable
     * @return boolean whether this target is runnable
     */
    public boolean isRunnable(){
        // runnable if root gem, doesn't root a broken gem forest, and doesn't root a chain of only let's
        return isRootGem() && !GemGraph.isAncestorOfBrokenGemForest(this) && !GemGraph.isLetChainAncestor(this);
    }

    /**
     * Returns whether this gem is broken.
     * @return boolean true if this gem is broken
     */
    public boolean isBroken() {
        // not broken by default
        return false;
    }

    /**
     * Returns whether this Gem is connected.
     * @return boolean true Gem is connected
     */
    public final boolean isConnected(){
        List<PartConnectable> connectableParts = getConnectableParts();
        
        // check all the connectable parts to see if they are connected
        for (final PartConnectable part : connectableParts) {
            if (part.isConnected()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the connectable parts on this gem.
     * 
     * @return List the list of connectable parts on this gem.
     * The order is: input parts first (in order), then output part.
     */
    public final List<PartConnectable> getConnectableParts() {
        List<PartConnectable> connectableParts = new ArrayList<PartConnectable>();

        connectableParts.addAll(Arrays.asList(getInputParts()));

        if (outputPart != null) {
            connectableParts.add(outputPart);
        }
        return connectableParts;
    }

    /**
     * Get the type information from the parts of this Gem
     * @return TypeExpr[] the types.  The order is: input first (in order), then output (if any).
     */
    public final TypeExpr[] getPartTypes() {
        int numInputs = getNInputs();
        boolean hasOutput = (outputPart != null);
        int numTypes = hasOutput ? numInputs + 1 : numInputs;

        TypeExpr[] tes = new TypeExpr[numTypes];

        // Get the inputs
        for (int i = 0; i < numInputs; i++) {
            tes[i] = getInputPart(i).getType();
        }

        // Add the output if any
        if (hasOutput) {
            tes[numInputs] = getOutputPart().getType();
        }
        return tes;
    }

    /**
     * Set the output type of this root gem
     * @param outType TypeExpr the new output type of this gem (which is root)
     */
    void setRootOutputType(TypeExpr outType){
        if (getRootGem() != this) {
            throw new IllegalStateException("Can't set the root type on a non-root gem");
        }
        getOutputPart().setType(outType);
    }
    
    /**
     * Get the result type of this gem.
     * @return TypeExpr the type of the result associated with this gem.
     */
    public TypeExpr getResultType() {
        if (outputPart != null) {
            return outputPart.getType();
        }
        
        return null;
    }

    /*
     * Methods implementing XMLPersistable ************************************************************
     */

    /**
     * Attach the saved form of this object as a child XML node.
     * @param parentNode the node that will be the parent of the generated XML.  
     *   The generated XML will be appended as a subtree of this node.  
     *   Note: parentNode must be a node type that can accept children (eg. an Element or a DocumentFragment)
     * @param gemContext the context in which the gem is saved.
     */
    public void saveXML(Node parentNode, GemContext gemContext) {
        
        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the gem element
        Element resultElement = document.createElement(GemPersistenceConstants.GEM_TAG);
        parentNode.appendChild(resultElement);
        
        // Attach an attribute for the unique gem identifier.
        String gemIdentifier = gemContext.getIdentifier(this, true);
        resultElement.setAttribute(GemPersistenceConstants.GEM_ID_ATTR, gemIdentifier);

        // Add an element for inputs (if the gem can have inputs).
        if (inputParts != null) {

            Element inputsElement = document.createElement(GemPersistenceConstants.INPUTS_TAG);
            resultElement.appendChild(inputsElement);

            // Add child elements for each input
            int numChildren = getNInputs();
            for (int i = 0; i < numChildren; i++) {
                PartInput input = getInputPart(i);
                input.saveXML(inputsElement);
            }
        }
    }

    /**
     * Load this object's state.
     * @param element Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     */
    void loadXML(Element element, GemContext gemContext) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(element, GemPersistenceConstants.GEM_TAG);
        
        // Add to the context, if an id attribute is present (otherwise, it's in the old save format..).
        String gemId = element.getAttribute(GemPersistenceConstants.GEM_ID_ATTR);
        if (gemId.equals("")) {
            throw new BadXMLDocumentException(element, "Missing gem id.");
        }
        gemContext.addGem(this, gemId);

        // Create inputs if any.
        Element childNode = XMLPersistenceHelper.getChildElement(element, GemPersistenceConstants.INPUTS_TAG);

        if (childNode != null) {
            List<Element> inputNodes = XMLPersistenceHelper.getChildElements(childNode);

            int numInputNodes = inputNodes.size();
            PartInput[] newInputs = new PartInput[numInputNodes];

            for (int i = 0; i < numInputNodes; i++) {
                Element inputNode = inputNodes.get(i);

                PartInput input = createInputPart(i);
                input.loadXML(inputNode);
                newInputs[i] = input;
            }
            
            inputParts = newInputs;

        } else {
            // TODOEL: TEMP: until clients update their save code.
            if (!(this instanceof ValueGem) && inputParts == null) {
                inputParts = EMPTY_INPUT_PART_ARRAY;
            }
        }
    }

    /**
     * Get the identifier for the first gem element (by preorder traversal) descending from a given element.
     * @param gemAncestorElement the ancestor element of the gem to id.
     * @return the gem's identifier, or null if a gem descendant cannot be found with an appropriate attribute.
     */
    public static String getGemId(Element gemAncestorElement) {

        // Get the descendant nodes with the gem tag.
        NodeList nodeList = gemAncestorElement.getElementsByTagName(GemPersistenceConstants.GEM_TAG);
        
        // Iterate over them, looking for an element with the id attribute.
        int nNodes = nodeList.getLength();
        for (int i = 0; i < nNodes; i++) {

            Node node = nodeList.item(i);
            if (node instanceof Element) {

                String gemId = ((Element)node).getAttribute(GemPersistenceConstants.GEM_ID_ATTR);
                if (!gemId.equals("")) {
                    return gemId;
                }
            }
        }
        return null;
    }

    /**
     * Obtain the XML namespace info for gems.
     * @return gem XML namespace info..
     */
    public static NamespaceInfo getNamespaceInfo() {
        return new NamespaceInfo(GemPersistenceConstants.GEM_NS, GemPersistenceConstants.GEM_NS_PREFIX);
    }

    /**
     * A utility function that finds the next available name for gems that must have exclusive names
     * For examples, if the desired name is value, but value, value1 and value2 is invalid (specified in
     * the invalidNames collection), then this method would return value3, if it is valid.
     * @param oldName the original name
     * @param invalidNames the collection of invalid names.
     */
    static String getNextValidName(String oldName, Collection<String> invalidNames) {
        
        int i;
        String newName = oldName;
        
        // We figure out what the 'baseName' is.
        // For example, if the name is "value2", then the 'baseName' is "value"
        for (i = oldName.length() - 1; i >= 0; i--) {
            char currentChar = oldName.charAt(i);
            if (!Character.isDigit(currentChar)) {
                break;
            }
        }
        
        String intPart = oldName.substring(i+1);
        
        // If the old name was value2, we don't want to insert value1, so we start counting from 3;
        int j = intPart.length() > 0 ? Integer.parseInt(intPart) + 1 : 1;
        
        String baseName = oldName.substring(0, i + 1);
        
        while (invalidNames.contains(newName)) {
            newName = baseName + j;
            j++;
        }
        return newName;
    }

    /*
     * Methods to handle gem visitation ***************************************************************
     */

    /**
     * Use a GemVisitor to visit this gem and its ancestors.
     *   The order of immediate ancestor visitation is not guaranteed if there are multiple immediate ancestors.
     * @param gemVisitor GemVisitor the visitor to operate on this gem and its ancestors.
     */
    public final void visitAncestors(GemVisitor gemVisitor){
        visitAncestors(gemVisitor, new HashSet<Gem>());
    }
    /**
     * Helper method for visitAncestors.
     * @param gemVisitor GemVisitor the visitor to operate on this gem and its ancestors.
     * @param gemsVisited Set the set of gems already visited
     */
    void visitAncestors(GemVisitor gemVisitor, Set<Gem> gemsVisited){
        // visit
        if (seeVisitor(gemVisitor, gemsVisited)) {
            return;
        }

        // Get the output connection, if any
        Connection outputConnection = (outputPart == null) ? null : outputPart.getConnection();

        // If there is a connection from the output, follow it
        if (outputConnection != null) {
            outputConnection.getDestination().getGem().visitAncestors(gemVisitor, gemsVisited);
        }
    }

    /**
     * Use a GemVisitor to visit this gem and its descendants.
     * Algorithm is pre-order, first-to-last.
     *   ie. this gem is visited first, followed by its first descendant.  On this descendant, repeat the algo, then
     *   move on to the other descendants in order.
     * Emitters' descendants are their corresponding collectors.
     * Reflectors' descendants are both gems connected to their inputs (considered first), then their corresponding collectors.
     * @param gemVisitor GemVisitor the visitor to operate on this gem and its descendants.
     */
    public final void visitDescendants(GemVisitor gemVisitor){
        visitDescendants(gemVisitor, new HashSet<Gem>());
    }

    /**
     * Helper method for visitDescendants.
     * @param gemVisitor GemVisitor the visitor to operate on this gem and its descendants.
     * @param gemsVisited Set the set of gems already visited
     */
    void visitDescendants(GemVisitor gemVisitor, Set<Gem> gemsVisited){
        // visit
        if (seeVisitor(gemVisitor, gemsVisited)) {
            return;
        }

        // Follow any connections to descendants
        int numArgs = getNInputs();
        for (int i = 0; i < numArgs; i++) {
            Connection inputConnection = getInputPart(i).getConnection();

            if (inputConnection != null) {
                // Follow the connection
                inputConnection.getSource().getGem().visitDescendants(gemVisitor, gemsVisited);
            }
        }
    }

    /**
     * Use a GemVisitor to visit every gem in this tree or forest.
     * @param gemVisitor GemVisitor the visitor to operate on the gems in this tree or forest.
     */
    public final void visitGraph(GemVisitor gemVisitor){

        if (gemVisitor.getTraversalScope() == GemGraph.TraversalScope.FOREST) {
            visitConnectedTrees(gemVisitor, new HashSet<Gem>());

        } else {
            // if it's a tree visitor, just descend from the root of the tree
            getRootGem().visitDescendants(gemVisitor, new HashSet<Gem>());
        }
    }

    /**
     * Helper method for visitGraph - visit every Gem in the forest.
     * The algorithm: 
     *   Starting from a root, visit descendants.  This will result in a call on any descendant roots.
     *   Then call upon ancestor roots.
     *   Don't forget about unconnected emitters (which aren't roots).
     * The order in which ancestor roots are visited is not guaranteed.
     * @param gemVisitor GemVisitor the visitor to operate on the gems in this forest.
     * @param gemsVisited Set the set of gems already visited
     */
    void visitConnectedTrees(GemVisitor gemVisitor, Set<Gem> gemsVisited){

        if (gemVisitor.getTraversalScope() != GemGraph.TraversalScope.FOREST) {
            throw new IllegalArgumentException("Expecting a forest visitor.");
        }

        // make sure we start from a root gem if this is the initial call
        Gem rootGem;
        if (gemsVisited.isEmpty() && ((rootGem = getRootGem()) != this)) {
            rootGem.visitConnectedTrees(gemVisitor, gemsVisited);
            return;
        }

        // visit
        if (seeVisitor(gemVisitor, gemsVisited)) {
            return;
        }

        // Follow input connections.
        int numArgs = getNInputs();
        for (int i = 0; i < numArgs; i++) {
            Connection inputConnection = getInputPart(i).getConnection();

            if (inputConnection != null) {
                inputConnection.getSource().getGem().visitConnectedTrees(gemVisitor, gemsVisited);
            }
        }
     }

    /**
     * Get the visitor to visit this gem if appropriate.  Tell us if we shouldn't pass the
     * visitor on to other gems.
     * @param gemVisitor GemVisitor the visitor to operate on the gems in this forest.
     * @param gemsVisited Set the set of gems already visited
     * @return boolean true if the visitor is done with visiting
     */
    final boolean seeVisitor(GemVisitor gemVisitor, Set<Gem> gemsVisited) {
        // check if we already visited this gem
        if (gemsVisited.contains(this)) {
            return true;
        }
        gemsVisited.add(this);

        // visit
        return gemVisitor.visitGem(this);
    }

    /*
     * Methods to handle listeners     ****************************************************************
     */

    /**
     * Adds the specified burn listener to receive burn events from this gem.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the burn listener.
     */
    public synchronized void addBurnListener(BurnListener l) {
        if (l == null) {
            return;
        }
        burnListener = GemEventMulticaster.add(burnListener, l);
    }

    /**
     * Removes the specified burn listener so that it no longer receives burn events from this gem. 
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the burn listener.
     */
    public synchronized void removeBurnListener(BurnListener l) {
        if (l == null) {
            return;
        }
        burnListener = GemEventMulticaster.remove(burnListener, l);
    }

    /**
     * Adds the specified name change listener to receive name change events from this input.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the name change listener.
     */
    public synchronized void addNameChangeListener(NameChangeListener l) {
        if (l == null) {
            return;
        }
        nameChangeListener = GemEventMulticaster.add(nameChangeListener, l);
    }

    /**
     * Removes the specified name change listener so that it no longer receives name change events from this gem. 
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the name changelistener.
     */
    public synchronized void removeNameChangeListener(NameChangeListener l) {
        if (l == null) {
            return;
        }
        nameChangeListener = GemEventMulticaster.remove(nameChangeListener, l);
    }

    /**
     * Adds the specified state change listener to receive state change events from this gem .
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the state change listener.
     */
    public synchronized void addStateChangeListener(GemStateListener l) {
        if (l == null) {
            return;
        }
        gemStateListener = GemEventMulticaster.add(gemStateListener, l);
    }

    /**
     * Removes the specified state change listener so that it no longer receives state change events from this gem. 
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     *
     * @param    l   the state change listener.
     */
    public synchronized void removeStateChangeListener(GemStateListener l) {
        if (l == null) {
            return;
        }
        gemStateListener = GemEventMulticaster.remove(gemStateListener, l);
    }

    /**
     * Adds the specified listener to receive input change events from this gem.
     * @param l the input change listener
     */ 
    public void addInputChangeListener(InputChangeListener l) { 
        listenerList.add(InputChangeListener.class, l);
    }

    /**
     * Removes the specified input change listener so that it no longer receives input change events from this gem.
     * @param l the input change listener
     */ 
    public void removeInputChangeListener(InputChangeListener l) {
        listenerList.remove(InputChangeListener.class, l);
    }

    /**
     * Fires an input change event.
     * @param oldParts the old input parts.
     */
    private void fireInputChangeEvent(PartInput[] oldParts) {
        Object[] listeners = listenerList.getListenerList();
        
        InputChangeEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == InputChangeListener.class) {
                if (e == null) {
                    // Lazily create the event:
                    e = new InputChangeEvent(this, oldParts);
                }
                ((InputChangeListener) listeners[i + 1]).inputsChanged(e);
            }
        }
    }

    /**
     * Adds the specified listener to receive type change events from this gem.
     * @param l the type change listener
     */ 
    public void addTypeChangeListener(TypeChangeListener l) { 
        listenerList.add(TypeChangeListener.class, l);
    }

    /**
     * Removes the specified type change listener so that it no longer receives type change events from this gem.
     * @param l the type change listener
     */ 
    public void removeTypeChangeListener(TypeChangeListener l) {
        listenerList.remove(TypeChangeListener.class, l);
    }

    /**
     * Fires a type change event.
     * @param partChanged the part whose type changed
     */
    private void fireTypeChangeEvent(PartConnectable partChanged) {
        Object[] listeners = listenerList.getListenerList();
        
        TypeChangeEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TypeChangeListener.class) {
                if (e == null) {
                    // Lazily create the event:
                    e = new TypeChangeEvent(partChanged);
                }
                ((TypeChangeListener) listeners[i + 1]).typeChanged(e);
            }
        }
    }

    /**
     * Adds the specified listener to receive input name events from this gem.
     * @param l the input name listener
     */ 
    public void addInputNameListener(InputNameListener l) { 
        listenerList.add(InputNameListener.class, l);
    }

    /**
     * Removes the specified input name listener so that it no longer receives input name events from this gem.
     * @param l the input name listener
     */ 
    public void removeInputNameListener(InputNameListener l) {
        listenerList.remove(InputNameListener.class, l);
    }

    /**
     * Fires an input name event.
     * @param inputChanged the input whose name changed
     */
    void fireInputNameEvent(PartInput inputChanged) {
        Object[] listeners = listenerList.getListenerList();
        
        InputNameEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == InputNameListener.class) {
                if (e == null) {
                    // Lazily create the event:
                    e = new InputNameEvent(inputChanged);
                }
                ((InputNameListener) listeners[i + 1]).inputNameChanged(e);
            }
        }
    }

    /**
     * Infer the type of a part according to the constraints imposed on the part's gem by its connections.
     * @param gemPart the part in question.
     * @param info the info object to be used during type checking.
     * @return the inferred type of the part.
     */
    private TypeExpr inferPartType(Gem.PartConnectable gemPart, TypeCheckInfo info) {
        
        // if not connected - no types
        if (!gemPart.isConnected()) {
            return TypeExpr.makeParametricType();        
        }
        
        Gem gem = gemPart.getGem();
        Connection connection = gemPart.getConnection();
        
        // Special case for an input part connected to the output of a value gem.
        // The code after this will not properly calculate value gem output types with constrained typevars (eg. Num a => a).
        if (!(gemPart instanceof Gem.PartInput) && connection.getSource().getGem() instanceof ValueGem) {
            return connection.getSource().getType();
        }
        
        // The names of the collectors in the connected graph.
        Set<String> collectorNames = new HashSet<String>();
        
        // Iterate over the roots of the forest of which the gem is part (including broken ones..).
        // From this, populate the collector names set, and get a collector (any collector will do.).
        CollectorGem aCollector = null;
        for (final Gem rootGem : GemGraph.obtainForestRoots(gem, true)) {
            if (rootGem instanceof CollectorGem) {
                CollectorGem collectorGem = (CollectorGem)rootGem;

                collectorNames.add(collectorGem.getUnqualifiedName());
                
                if (aCollector == null) {
                    aCollector = collectorGem;
                }
            }
        }
        
        Set<Connection> tempConnectionsToDisconnect = new HashSet<Connection>();

        // If there are no collectors in the forest, paste in a collector, and connect it to the root.
        if (aCollector == null) {
            aCollector = new CollectorGem();
            aCollector.setName("anonymousCollector");
            Gem.PartInput aCollectorInput = aCollector.getCollectingPart();
            
            Gem rootGem = gem.getRootGem();
            Gem.PartOutput rootGemOutput = rootGem.getOutputPart();
            
            Connection tempConnection = new Connection(rootGemOutput, aCollectorInput);
            rootGemOutput.bindConnection(tempConnection);
            aCollectorInput.bindConnection(tempConnection);
            
            tempConnectionsToDisconnect.add(tempConnection);
        }
        
        // Get the outermost enclosing collector, which will be considered the target.
        CollectorGem graphTarget = GemGraph.obtainOutermostCollector(aCollector);
        
        // Create the dummy collector, set it as the target for the gem graph's target.
        // It's the enclosing collector, so the name doesn't have to be unique..
        CollectorGem tempTarget = new CollectorGem();
        tempTarget.setName("tempTarget");
        CollectorGem oldGraphTargetTarget = graphTarget.getTargetCollectorGem();
        graphTarget.setTargetCollector(tempTarget);
        
        // Create a code gem, with the same number of arguments as the gem for which types will be inferred.
        int nInputs = gem.getNInputs();
        CodeGem tempCodeGem = new CodeGem(nInputs);
        
        // Connect to the dummy collector.
        Connection tempCodeGemConnection = new Connection(tempCodeGem.getOutputPart(), tempTarget.getCollectingPart());
        tempCodeGem.getOutputPart().bindConnection(tempCodeGemConnection);
        tempTarget.getCollectingPart().bindConnection(tempCodeGemConnection);

        // Add the code gem's arguments to the dummy collector.
        Set<PartInput> codeGemInputArguments = new LinkedHashSet<PartInput>();
        for (int i = 0; i < nInputs; i++) {
            codeGemInputArguments.add(tempCodeGem.getInputPart(i));
        }
        tempTarget.addArguments(0, codeGemInputArguments);
        tempTarget.updateReflectedInputs();

        // Create reflector gems: one to substitute in place of the gem whose types to infer, one to actually get the inferred types.
        ReflectorGem reflectorToSubstitute = new ReflectorGem(tempTarget);
        ReflectorGem typeInferenceReflector = new ReflectorGem(tempTarget);
        tempTarget.addReflector(reflectorToSubstitute);
        tempTarget.addReflector(typeInferenceReflector);
        
        // Connections on the gem whose types to infer.
        Set<Connection> oldConnections = new HashSet<Connection>();

        // Maps temporary arguments to the collectors to which they were added, so they can be removed after.
        Map<Gem.PartInput, CollectorGem> tempArgumentToTargetCollectorMap = new HashMap<PartInput, CollectorGem>();
        
        try {
            // Replace the output connection..
            Connection outputConnection = gem.getOutputPart().getConnection();
            if (outputConnection != null) {
                Connection tempConn = new Connection(reflectorToSubstitute.getOutputPart(), outputConnection.getDestination());
                reflectorToSubstitute.getOutputPart().bindConnection(tempConn);
                outputConnection.getDestination().bindConnection(tempConn);
                
                // Check that it's not a connection from a temporary collector we created earlier,
                //  when dealing with the case of no collectors in the forest.
                if (!tempConnectionsToDisconnect.contains(outputConnection)) {
                    oldConnections.add(outputConnection);
                    tempConnectionsToDisconnect.add(tempConn);
                }
            }
            
            // Replace the input connections and arguments..
            for (int i = 0; i < nInputs; i++) {
                Gem.PartInput input = gem.getInputPart(i);
                Connection oldInputConnection = input.getConnection();
                if (oldInputConnection == null) {
                    // Add the code gem's input argument as an argument..
                    CollectorGem argumentTarget = GemGraph.getInputArgumentTarget(input);
                    if (argumentTarget != null) {
                        Gem.PartInput surrogateArgument = reflectorToSubstitute.getInputPart(i);
                        argumentTarget.addArguments(input, Collections.singleton(surrogateArgument), false);
                        tempArgumentToTargetCollectorMap.put(surrogateArgument, argumentTarget);
                    }
                    
                } else {
                    // Substitute the connection.
                    Gem.PartInput reflectorGemInput = reflectorToSubstitute.getInputPart(i);
                    Connection tempConn = new Connection(oldInputConnection.getSource(), reflectorGemInput);
                    reflectorGemInput.bindConnection(tempConn);
                    oldInputConnection.getSource().bindConnection(tempConn);
                    tempConnectionsToDisconnect.add(tempConn);
                    oldConnections.add(oldInputConnection);
                }
            }
            
            // Get all related gem graph roots.
            Set<Gem> typeCheckForestRoots = GemGraph.obtainForestRoots(tempTarget, false);
            typeCheckForestRoots.addAll(GemGraph.obtainForestRoots(reflectorToSubstitute, false));
            typeCheckForestRoots.add(typeInferenceReflector);
            
            Set<CollectorGem> enclosingCollectors = new HashSet<CollectorGem>();
            for (final Gem root : typeCheckForestRoots) {
                if (root instanceof CollectorGem && !enclosingCollectors.contains(root)) {
                    enclosingCollectors.addAll(GemGraph.obtainEnclosingCollectors(root));
                }
            }
            for (final CollectorGem enclosingCollector : enclosingCollectors) {
                typeCheckForestRoots.addAll(GemGraph.obtainForestRoots(enclosingCollector, false));
            }

            // Type check the forest.
            final Pair<Map<CompositionNode.CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>> resultPair = info.getTypeChecker().checkGraph(typeCheckForestRoots, info.getModuleName(), new MessageLogger());
            Map<CompositionNode, List<TypeExpr>> rootNodeMap = resultPair.snd();
            
            // Get the inferred type for the gem.  These will be the types on the reflector gem used for this purpose.
            List<TypeExpr> gemTypeList = rootNodeMap.get(typeInferenceReflector);

            // The inferred type will be null if the connected tree is broken.
            if (gemTypeList == null) {
                return null;
            }
            
            if (gemPart instanceof Gem.PartOutput) {
                // The output type.
                return gemTypeList.get(gemTypeList.size() - 1);

            } else {
                // The input type.
                int inputNum = ((Gem.PartInput)gemPart).getInputNum();
                return gemTypeList.get(inputNum);
            }

        } catch (TypeException te) {
            // checkGraph() failed (shouldn't happen..)
            te.printStackTrace();
            throw new IllegalStateException("inferPartType: Couldn't infer type.");
        
        } finally {
            for (final Connection conn : tempConnectionsToDisconnect) {
                conn.getSource().bindConnection(null);
                conn.getDestination().bindConnection(null);
            }
            
            GemGraph.reconnectConnections(oldConnections);
            graphTarget.setTargetCollector(oldGraphTargetTarget);

            for (final Map.Entry<PartInput, CollectorGem> mapEntry : tempArgumentToTargetCollectorMap.entrySet()) {
                Gem.PartInput surrogateArgument = mapEntry.getKey();
                CollectorGem argumentTarget = mapEntry.getValue();
                argumentTarget.removeArgument(surrogateArgument);
            }
        }
    }

    /**
     * A PartConnectable has a type and possibly a connection.
     * Creation date: (12/15/00 11:35:15 AM)
     * @author Luke Evans
     */
    public abstract class PartConnectable {

        /** The part's type. */
        private TypeExpr type;
        
        /** The part's connection, if any. */
        private Connection boundConnection;

        /**
         * Get the Gem to which this PartConnectable belongs.
         * @return Gem the gem
         */    
        public final Gem getGem() {
            return Gem.this;
        }        

        /**
         * Get the type of this part
         * @return the type expression
         */
        public TypeExpr getType() {
            return type;
        }

        /**
         * Set the type of this part
         * @param newType TypeExpr the type expression to set
         */
        void setType(TypeExpr newType) {
            this.type = newType;
            fireTypeChangeEvent(this);
        }

        /**
         * Check if this part is connected.
         * @return boolean whether it is already connected
         */
        public final boolean isConnected() {
            return (boundConnection != null);
        }
        
        /**
         * Get the connection at this part.
         * @return conn Connection the connection bound to this part
         */
        public final Connection getConnection() {
            return boundConnection;
        }    

        /**
         * Bind a connection to this part.
         * @param conn Connection the connection to connect 
         */
        public void bindConnection(Connection conn) {
            boundConnection = conn;
        }    

        /**
         * Returns the gem to which this part is connected, if any.
         * If unconnected, null is returned.
         */
        public abstract Gem getConnectedGem();
    }

    /**
     * A PartInput is a connectable part which is a sink (destination).
     * PartInputs are either burnt or free.  Free PartInputs are associated with an enclosing collector 
     * (via the CollectorArgument), which defines the function on which the associated argument appears.
     * Creation date: (12/11/00 10:55:53 AM)
     * @author Luke Evans
     */
    public class PartInput extends PartConnectable implements CompositionArgument {
        
        /** The index of this input on the gem on which it appears. */
        private int inputNum;
        
        /** Whether the input is burnt. */
        private boolean burntState;
        
        /**
         * The name of the input as assigned by its gem.
         * That means it is the name used either in CAL code or the display name from
         * the gem's metadata. The actual base input name of this input will be this
         * name if there is no display name set in the input's design metadata.
         * 
         * Note: This property does not need to be serialized. It is initialized by a gem
         * whenever it creates its inputs to match the latest information available.
         */
        private String originalInputName = ArgumentMetadata.DEFAULT_ARGUMENT_NAME;
        
        /** The name of the argument. */
        private ArgumentName argumentName = new ArgumentName(ArgumentMetadata.DEFAULT_ARGUMENT_NAME);
        
        /** The metadata for this input. */
        private ArgumentMetadata metadata;

        /**
         * Construct an InputPart for the given input number.
         * @param inputNum int the input number for this PartInput
         */
        PartInput(int inputNum) {
            this.inputNum = inputNum;
            this.burntState = false;
            this.metadata = new ArgumentMetadata(CALFeatureName.getArgumentFeatureName(inputNum), GemCutter.getLocaleFromPreferences());
        }

        /**
         * Determines whether the input is still a valid input to use.  Code gems and reflectors can 
         * all have their inputs dynamically changed leaving other objects holding onto
         * invalid input parts.  If the input no longer exists as part of the gem, or if it has been
         * reordered.  Technically reordering might be allowed, but the PartInput object holds onto its
         * input number which will no longer match the gems ordering so we consider this state to be
         * invalid as well.
         * @return This method will return true if this input part is still a valid input
         * on the gem and false if the input part is no longer part of the gem.
         */
        public boolean isValid() {
            return (inputNum < getNInputs() && this == Gem.this.getInputPart(inputNum));
        }
        
        /**
         * Return the input number
         * @return the input number
         */
        public final int getInputNum() {
            return inputNum;
        }
        /**
         * Set the input number
         * This should only be used in special cases where the input parts are being
         * reordered with respect to each other.
         * @param inputNum the input number
         */
        void setInputNum(int inputNum) {
            this.inputNum = inputNum;
            
            ArgumentMetadata newMetadata = new ArgumentMetadata(CALFeatureName.getArgumentFeatureName(inputNum), this.metadata.getLocale());
            this.metadata.copyTo(newMetadata);
            this.metadata = newMetadata;
        }

        /**
         * @return a copy of this input's name object
         */
        public ArgumentName getArgumentName() {
            return new ArgumentName(argumentName);
        }

        /**
         * Sets the name of this input as defined by the gem the input belongs to.
         * This should either be the name from CAL code or the display name from
         * the gem's metadata.
         * @param inputName the name to use as the input's base name. If this is
         * null the default input name will be used.
         */
        void setOriginalInputName(String inputName) {
            this.originalInputName = inputName != null ? inputName : ArgumentMetadata.DEFAULT_ARGUMENT_NAME;
        }
        
        /**
         * @return the original name of this input as defined by the gem the input belongs to
         */
        public String getOriginalInputName() {
            return originalInputName;
        }

        /**
         * @return ArgumentMetadata a copy of this input's metadata
         */
        public ArgumentMetadata getDesignMetadata() {
            return (ArgumentMetadata) metadata.copy();
        }
        
        /**
         * @param metadata the new metadata metadata for this input (a safe copy will be made)
         */
        public void setDesignMetadata(ArgumentMetadata metadata) {
            
            if (metadata == null) {
                throw new IllegalArgumentException("Cannot set metadata to null");
            }
            
            metadata.copyTo(this.metadata);
        }

        /**
         * Return whether or not this input is burnt.
         * @return boolean whether or not this input is burnt
         */
        public final boolean isBurnt(){
            return burntState;
        }

        /**
         * Burn this input
         * @param burnt true to burn, false to unburn.
         */
        public void setBurnt(boolean burnt){
            // Do a quick safety check
            if (!isValid()) {
                throw new IllegalArgumentException("Attempt to modify an invalid input");
            }
            
            if (burntState != burnt) {
                burntState = burnt;
                if (burnListener != null) {
                    burnListener.burntStateChanged(new BurnEvent(PartInput.this));
                }
            }
        }
        
        /**
         * See if toggling the burn state of this input would break the gem tree.
         * @param info the info to use for typing the tree.
         * @return boolean Returns true if burning the input will result in a valid gem graph and false
         * if burning the input will result in a broken gem graph 
         */
        public boolean burnBreaksGem(TypeCheckInfo info) {
            // Do a quick safety check
            if (!isValid()) {
                throw new IllegalArgumentException("Attempt to checking burning for an invalid input");
            }

            // first see if the gem's output is connected to an input
            Gem gem = getGem();
            PartOutput output = gem.getOutputPart();
            Connection conn = output.getConnection();
            
            if (conn == null) {
                return false;
            }
            
            PartInput destInput = conn.getDestination();
    
            // get the inferred output type
            TypeExpr inferredOutputType = output.inferType(info);
    
            // store the initial burn state and disconnect (temporarily)
            boolean wasBurnt = isBurnt();
            output.bindConnection(null);
            destInput.bindConnection(null);

            TypeExpr outputType;
            try {            
                // Change the burn state  
                // Note that we don't call setBurnt() since we don't want to fire events for this test
                burntState = !wasBurnt;

                // Get the type of the gem given it's new burnt state
                CompilerMessageLogger logger = new MessageLogger ();
                Set<Gem> forestRoots = GemGraph.obtainForestRoots(gem, false);
                final Pair<Map<CompositionArgument, TypeExpr>, Map<CompositionNode, List<TypeExpr>>> resultPair = info.getTypeChecker().checkGraph(forestRoots, info.getModuleName(), logger);
                Map<CompositionNode, List<TypeExpr>> rootNodeMap = resultPair.snd();
                List<TypeExpr> typePieces = rootNodeMap.get(gem);

                // Get the resulting output type
                outputType = typePieces.get(typePieces.size() - 1);
                
            } catch (TypeException te) {
                // Burning the input produced an invalid gem graph so we simply return false.  Note that
                // the finally block will reset the burnt state and connections for us
                return false;
                
            } finally {
                // reset to previous state
                burntState = wasBurnt;
                output.bindConnection(conn);
                destInput.bindConnection(conn);
            }

            // It's possible that the burnt input created a valid gem graph (no type exception thrown),
            // but the resulting type won't unify correctly so the burn is still not allowed
            return !GemGraph.typesWillUnify(outputType, inferredOutputType, info);
        }

        /**
         * Infer the type of the variable based on the gem tree connected to its input
         * @param info the info to use for typing the tree. 
         * @return TypeExpr the inferred type of this variable, or null if inference fails (connected to a broken subtree).
         */
        public TypeExpr inferType(TypeCheckInfo info) {
            // return the inferred output type.  This will be null if broken.
            return inferPartType(this, info);
        }

        /**
         * @see Gem.PartConnectable#getConnectedGem()
         */
        @Override
        public Gem getConnectedGem() {
            return super.isConnected() ? getConnection().getSource().getGem() : null;
        }
        
        /**
         * {@inheritDoc}
         */
        public CompositionNode getConnectedNode() {
            return getConnectedGem();
        }
        
        /**
         * Set the argument name object.
         * @param newArgumentName the new argument name
         */
        public void setArgumentName(ArgumentName newArgumentName) {
            argumentName = new ArgumentName(newArgumentName);
            fireInputNameEvent(this);
        }

        /**
         * {@inheritDoc}
         */
        public String getNameString() {
            return argumentName.getCompositeName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "PartInput#" + getInputNum() + ".  Name="  + getArgumentName().getCompositeName() + ".  Burnt=" + isBurnt() + ".  Gem=" + getGem();
        }

        /**
         * Methods implementing XMLPersistable ************************************************************
         */
    
        /**
         * Attached the saved form of this object as a child XML node.
         * @param parentNode Node the node that will be the parent of the generated XML.  The generated XML will 
         * be appended as a subtree of this node.  
         * Note: parentNode must be a node type that can accept children (eg. an Element or a DocumentFragment)
         */
        public void saveXML(Node parentNode) {
            
            Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();
            Element resultElement = document.createElement(GemPersistenceConstants.INPUT_TAG);
            parentNode.appendChild(resultElement);

            // Add an attribute for burn status
            resultElement.setAttribute(GemPersistenceConstants.INPUT_BURNT_ATTR, isBurnt() ? XMLPersistenceConstants.TRUE_STRING : XMLPersistenceConstants.FALSE_STRING);

            // Store the input name.
            argumentName.saveXML(resultElement);
        }

        /**
         * Load this object's state.
         * @param element Element the element representing the structure to deserialize.
         */
        void loadXML(Element element) throws BadXMLDocumentException {
    
            XMLPersistenceHelper.checkTag(element, GemPersistenceConstants.INPUT_TAG);

            burntState = XMLPersistenceHelper.getBooleanAttribute(element, GemPersistenceConstants.INPUT_BURNT_ATTR);

            List<Element> elements = XMLPersistenceHelper.getChildElements(element);
            Element child = elements.get(0);
            
            if (child.getLocalName().equals(GemPersistenceConstants.INPUT_NAME_TAG)) {
                // Load the input name.
                ArgumentName argName = getArgumentName();
                argName.loadXML(child);
                setArgumentName(argName);
                this.originalInputName = argName.getBaseName();
            }
        }
    }

    /**
     * A PartOutput is a connectable part which is a source/output.
     * Creation date: (12/11/00 10:54:46 AM)
     * @author Luke Evans
     */
    public class PartOutput extends PartConnectable {
        
        /**
         * Default constructor for a PartOutput.
         */
        protected PartOutput() {
        }
        
        /**
         * Infer the output type based on the gem tree connected to its output.
         * @param info the info to use for typing the tree. 
         * @return TypeExpr the inferred type of the output, or null if broken subtree
         */
        public TypeExpr inferType(TypeCheckInfo info) {
            // return the inferred input type.  This will be null if broken.
            return inferPartType(this, info);
        }

        /**
         * @see org.openquark.gems.client.Gem.PartConnectable#getConnectedGem()
         */
        @Override
        public Gem getConnectedGem() {
            return super.isConnected() ? getConnection().getDestination().getGem() : null;
        }
    }
}


