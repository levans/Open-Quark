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
 * BoundGemDescription.java
 * Created: 19-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * This represents a Gem and its input bindings.
 * 
 */
public class BoundGemDescription {
    
    private final String gemName;
    private final List<InputBinding> inputBindings;

    /**
     * Constructor BoundGemDescription
     * 
     * @param gemName
     * @param inputBindings
     */
    public BoundGemDescription (String gemName, List<InputBinding> inputBindings) {
        this.gemName = gemName;
        this.inputBindings = new ArrayList<InputBinding> (inputBindings);
    }

    /**
     * Method getGemName
     * 
     * @return the name of the gem
     */
    public String getGemName () {
        return gemName;
    }

    /**
     * gets the qualified name
     * @return the qualified name
     */
    public QualifiedName getQualifiedName() {
        return QualifiedName.makeFromCompoundName(gemName);
    }
    
    /**
     * Method getBindingCount
     * 
     * @return the number of bindings
     */
    public int getBindingCount () {
        return inputBindings.size();
    }

    /**
     * This returns the number of inputs that are bound to non constant values, e.g. message properties or metrics
     */
    public int getVariableBindingCount() {
        int variable = 0;
        for(int i = 0; i < getBindingCount(); i++ ) {
            if ( !getNthBinding(i).isConstant() ) {
                variable++;
            }
        }
        return variable;
    }
    
    /**
     * Method getNthBinding
     * 
     * @param n
     * @return the nth binding
     */
    public InputBinding getNthBinding (int n) {
        return inputBindings.get (n);
    }

    /**
     * Method getInputBindings
     * 
     * @return an unmodifiable list of the input bindings
     */
    public List<InputBinding> getInputBindings () {
        return Collections.unmodifiableList(inputBindings);
    }
    
    
    //
    // Serialisation
    //

    /**
     * Method storeContents
     * 
     * @param descriptionElement
     */
    protected void storeContents (Element descriptionElement) {
        descriptionElement.setAttribute(MonitorSaveConstants.GemNameAttr, gemName);
        
        storeBindings (descriptionElement);
    }

    /**
     * Method storeBindings
     * 
     * @param descriptionElement
     */
    private void storeBindings (Element descriptionElement) {
        Document document = descriptionElement.getOwnerDocument();
        
        Element bindingsElem = document.createElement(MonitorSaveConstants.InputBindings);
        descriptionElement.appendChild(bindingsElem);
        
        for (final InputBinding binding : inputBindings) {
            binding.store (bindingsElem);
        }
    }

    /**
     * Method loadGemName
     * 
     * @param descriptionElem
     * @return Returns the name of the gem used in this binding
     */
    protected static String loadGemName (Element descriptionElem) {
        return descriptionElem.getAttribute(MonitorSaveConstants.GemNameAttr);
    }

    /**
     * Method loadInputBindings
     * 
     * @param descriptionElem
     * @return Returns a List of InputBindings loaded from the children of the given XML element
     */
    protected static List<InputBinding> loadInputBindings (Element descriptionElem, Collection<MessagePropertyDescription> messagePropertyInfos) throws BadXMLDocumentException, InvalidFileFormat {
        Element bindingsElem = XMLPersistenceHelper.getChildElement(descriptionElem, MonitorSaveConstants.InputBindings);
        
        List<InputBinding> inputBindings = new ArrayList<InputBinding> ();
        
        List<Element> bindingElemList = XMLPersistenceHelper.getChildElements(bindingsElem);
        
        for (final Element bindingElem : bindingElemList) {
            inputBindings.add(loadBinding (bindingElem, messagePropertyInfos));
        }
        
        return inputBindings;
    }

    /**
     * Method loadBinding
     * 
     * @param bindingElem
     * @return Returns an InputBinding loaded from the given XML element
     */
    private static InputBinding loadBinding (Element bindingElem, Collection<MessagePropertyDescription> messagePropertyInfos) throws BadXMLDocumentException, InvalidFileFormat {
        String tag = bindingElem.getTagName();

        if (tag.equals(ConstantBinding.getXmlTag ())) {
            return ConstantBinding.Load (bindingElem);
        } else if (tag.equals(PropertyBinding.getXmlTag ())) {
            return PropertyBinding.Load (bindingElem, messagePropertyInfos);
        } else if (tag.equals(TemplateStringBinding.getXmlTag())) {
            return TemplateStringBinding.Load (bindingElem, messagePropertyInfos);
        } else if (tag.equals(MetricBinding.getXmlTag())) {
            return MetricBinding.Load (bindingElem, messagePropertyInfos);
        } else {
            throw new InvalidFileFormat ("Unexpected input binding tag: " + tag);
        }
    }

}
