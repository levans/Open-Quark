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
 * ValueGem.java
 * Creation date: (10/18/00 1:03:35 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.util.List;

import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.valuenode.ParametricValueNode;
import org.openquark.cal.valuenode.Target;
import org.openquark.cal.valuenode.TargetRunner;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A ValueGem which we are using to build our supercombinator (compound Gem).
 * Creation date: (10/18/00 1:03:35 PM)
 * @author Luke Evans
 */
public final class ValueGem extends Gem implements CompositionNode.Value {

    /** The value node representing the value of this value gem */
    private ValueNode valueNode;
    
    /**
     * Construct a default ValueGem.
     */
    public ValueGem() {
        // The default value is a parametric value node with an unconstrained type variable.
        // TODOEL: this assumes that parametric value nodes will always be present, and that  
        //   they are the best type to use.  This will be true for (at least) a while though..
        this(new ParametricValueNode(TypeExpr.makeParametricType()));
    }

    /**
     * Construct a ValueGem from a value node
     * @param valueNode ValueNode the initial value of this value gem
     */
    public ValueGem(ValueNode valueNode) {
        // Initialize with no input parts.
        super(0);
        this.valueNode = valueNode;
    }

    /**
    protected PartOutput createOutputPart() {
        return new PartOutput();
    }
     * {@inheritDoc}
     */
    @Override
    protected PartOutput createOutputPart() {
        return new ValuePart();
    }

    /**
     * Obtain the string value of this CompositionNode
     * @return the string value
     */
    public final String getStringValue() {
        return valueNode.getCALValue();
    }
    
    /**
     * Obtain the source model of this CompositionNode
     * @return the source model
     */
    public final SourceModel.Expr getSourceModel() {
        return valueNode.getCALSourceModel();
    }
    
    /**
     * Returns true if this ValueGem contains parametric values.
     * Otherwise, returns false.
     * @return boolean
     */
    public boolean containsParametricValues() {
        return valueNode.containsParametricValue();
    }

    /**
     * Change the value of this gem.
     * @param newValue the new value of this gem.
     */
    public void changeValue(ValueNode newValue) {
        ValueNode oldValue = valueNode;
        this.valueNode = newValue;
        
        fireValueChangeEvent(oldValue);
    }
    
    /**
     * Adds the specified listener to receive value change events from this gem.
     * @param l the value change listener
     */ 
    public void addValueChangeListener(ValueGemChangeListener l) { 
        listenerList.add(ValueGemChangeListener.class, l);
    }

    /**
     * Removes the specified value change listener so that it no longer receives value change events from this gem.
     * @param l the value change listener
     */ 
    public void removeValueChangeListener(ValueGemChangeListener l) {
        listenerList.remove(ValueGemChangeListener.class, l);
    }

    /**
     * Fires a value change event.
     * @param oldValue the old input parts.
     */
    private void fireValueChangeEvent(ValueNode oldValue) {
        
        Object[] listeners = listenerList.getListenerList();
        
        ValueGemChangeEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ValueGemChangeListener.class) {
                if (e == null) {
                    // Lazily create the event:
                    e = new ValueGemChangeEvent(this, oldValue, valueNode);
                }
                ((ValueGemChangeListener)listeners[i + 1]).valueChanged(e);
            }
        }
    }

    /**
     * Get the value of this gem.
     * @return ValueNode the value node represented by this gem.
     */
    public final ValueNode getValueNode() {
        return valueNode;
    }

    /**
     * Return whether this target is runnable
     * @return boolean whether this target is runnable
     */
    @Override
    public final boolean isRunnable(){
        // additional constraint: can't be parametric
        return (super.isRunnable());
    }

    /**
     * Describe this Gem
     * @return the description
     */
    @Override
    public String toString() {
        return "Value: " + getTextValue();
    }
    
    /**
     * Return the textual representation of the value that this gem is storing
     * @return String
     */
    public String getTextValue() {
        return getValueNode().getTextValue();
    }

    /*
     * Methods supporting XMLPersistable                 ********************************************
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveXML(Node parentNode, GemContext gemContext) throws XMLPersistenceHelper.StateNotPersistableException {
        
        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the value gem element
        Element resultElement = document.createElementNS(GemPersistenceConstants.GEM_NS, GemPersistenceConstants.VALUE_GEM_TAG);
        resultElement.setPrefix(GemPersistenceConstants.GEM_NS_PREFIX);
        parentNode.appendChild(resultElement);

        // Add info for the superclass gem.
        super.saveXML(resultElement, gemContext);


        // Now add ValueGem-specific info

        // Add info for the value

        // Add a value gem element
        Element valueElement = document.createElement(GemPersistenceConstants.VALUE_GEM_VALUE_TAG);
        resultElement.appendChild(valueElement);

//        // Add the value text to the value gem element
//        if (containsParametricValues()) {
//            throw new StateNotPersistableException("Value gem has parametric values.");
//        }
        String valueString = valueNode.getCALValue();
        CDATASection valueStringChild = XMLPersistenceHelper.createCDATASection(document, valueString);
        valueElement.appendChild(valueStringChild);
    }

    /**
     * Create a new ValueGem and loads its state from the specified XML element.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param valueRunner ValueRunner the ValueRunner used to get the value node from the value.
     * @param loadModuleName the name of the module in which this value node was saved
     * @return ValueGem
     * @throws BadXMLDocumentException
     */
    public static ValueGem getFromXML(Element gemElement, GemContext gemContext, 
                                      ValueRunner valueRunner, ModuleName loadModuleName) throws BadXMLDocumentException {
        ValueGem gem = new ValueGem();
        gem.loadXML(gemElement, gemContext, valueRunner, loadModuleName);
        return gem;
    }

    /**
     * Load this object's state.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param valueRunner ValueRunner the ValueRunner used to get the value node from the value.
     * @param loadModuleName the name of the module in which this value node was saved
     */
    void loadXML(Element gemElement, GemContext gemContext, 
                 ValueRunner valueRunner, ModuleName loadModuleName) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(gemElement, GemPersistenceConstants.VALUE_GEM_TAG);
        XMLPersistenceHelper.checkPrefix(gemElement, GemPersistenceConstants.GEM_NS_PREFIX);

        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemElement);

        // Get info for the underlying gem.        
        Element superGemElem = (childElems.size() < 1) ? null : (Element) childElems.get(0);
        XMLPersistenceHelper.checkIsElement(superGemElem);
        super.loadXML(superGemElem, gemContext);

        // figure out the value node
        Element valueChildElem = (childElems.size() < 2) ? null : (Element) childElems.get(1);
        XMLPersistenceHelper.checkIsElement(valueChildElem);
        this.valueNode = getValueNodeFromXML(valueChildElem, valueRunner, loadModuleName);
    }

    /**
     * Get a value node from its corresponding XML element.
     * @param valueElement the element representing the structure to deserialize.
     * @param valueRunner the ValueRunner used to get the value node from the value.
     * @param loadModuleName the name of the module in which this value node was saved
     * @return ValueNode a new value node corresponding the to XML element.  Null if evaluation fails, or
     * if the value node system can't handle the output.
     */
    public static ValueNode getValueNodeFromXML(Element valueElement, ValueRunner valueRunner, ModuleName loadModuleName) 
            throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(valueElement, GemPersistenceConstants.VALUE_GEM_VALUE_TAG);

        // Get the value text
        StringBuilder valueText = new StringBuilder();
        Node valueChild = valueElement.getFirstChild();
        XMLPersistenceHelper.getAdjacentCharacterData(valueChild, valueText);

        // Create a target to be run by a value runner, and return the result
        Target valueTarget = new Target.SimpleTarget(valueText.toString());

        ValueNode targetValue = null;
        try {
            targetValue = valueRunner.getValue(valueTarget, loadModuleName);
        } catch (TargetRunner.ProgramCompileException pce) {
            XMLPersistenceHelper.handleBadDocument(valueElement, "Can't compile program: \"" + valueText + "\".");
        }
        
        // What to do?
        if (targetValue == null) {
            XMLPersistenceHelper.handleBadDocument(valueElement, "Can't convert to a value node: \"" + valueText + "\".");
        }
        return targetValue;
    }

    /**
     * The PartConnectable representing the output part of a ValueGem
     * Creation date: (Jul 26, 2002 5:04:03 PM)
     * @author Edward Lam
     */
    class ValuePart extends PartOutput {
        /**
         * Default constructor for an emitting part
         */
        private ValuePart(){
            super();
        }

        /**
         * Get the type of this part
         * @return the type expression
         */
        @Override
        public final TypeExpr getType() {
            return valueNode.getTypeExpr();
        }

        /**
         * Set the type of this part
         * @param newType TypeExpr the type expression to set
         */
        @Override
        void setType(TypeExpr newType) {
            throw new UnsupportedOperationException("Can't set the type of a value gem.");
        }
    }
}


