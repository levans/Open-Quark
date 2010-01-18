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
 * PropertyBinding.java
 * Created: 19-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.Collection;
import java.util.HashSet;

import org.openquark.cal.services.BasicCALServices;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.GemGraph;
import org.openquark.gems.client.ReflectorGem;
import org.openquark.samples.bam.BindingContext;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * A Property binding represents a gem input that is bound to a message property.
 * 
 */
public class PropertyBinding extends InputBinding {
    
    private final MessagePropertyDescription propertyInfo;
    
    public PropertyBinding (MessagePropertyDescription propertyInfo) {
        this.propertyInfo = propertyInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<MetricDescription> getRequiredMetrics() {
        return new HashSet<MetricDescription>();
    }
    
    /**
     * @see org.openquark.samples.bam.model.InputBinding#getOutputGem(BasicCALServices, org.openquark.gems.client.GemGraph, org.openquark.samples.bam.BindingContext)
     */
    @Override
    public Gem getOutputGem (BasicCALServices calServices, GemGraph gemGraph, BindingContext bindingContext) {
        CollectorGem collectorGem = bindingContext.getCollector(propertyInfo);
        
        ReflectorGem reflectorGem = new ReflectorGem (collectorGem);
        
        gemGraph.addGem(reflectorGem);
        
        return reflectorGem;
    }
    
    /**
     * @return Returns the propertyDescription.
     */
    public MessagePropertyDescription getPropertyInfo () {
        return propertyInfo;
    }
    
    
    /**
     * @see org.openquark.samples.bam.model.InputBinding#getPresentation()
     */
    @Override
    public String getPresentation () {
        return propertyInfo.name;
    }

    /**
     * @see org.openquark.samples.bam.model.InputBinding#store(org.w3c.dom.Element)
     */
    @Override
    public void store (Element parentElem) {
        Document document = parentElem.getOwnerDocument();
        
        Element bindingElement = document.createElement(getXmlTag());
        parentElem.appendChild(bindingElement);
        
        bindingElement.setAttribute(MonitorSaveConstants.PropertyNameAttr, propertyInfo.name);
    }

    /**
     * Method getXmlTag
     * 
     * @return The XML tag used to store this class
     */
    public static String getXmlTag () {
        return MonitorSaveConstants.PropertyBinding;
    }

    /**
     * Method Load
     * 
     * @param bindingElem
     * @return Returns a PropertyBinding loaded from the given XML element
     */
    public static PropertyBinding Load (Element bindingElem, Collection<MessagePropertyDescription> messagePropertyInfos) throws BadXMLDocumentException, InvalidFileFormat {
        XMLPersistenceHelper.checkTag(bindingElem, getXmlTag());
        
        String propertyName = bindingElem.getAttribute(MonitorSaveConstants.PropertyNameAttr);
        
        MessagePropertyDescription propertyInfo = lookupProperty (propertyName, messagePropertyInfos);
        
        if (propertyInfo != null) {
            return new PropertyBinding (propertyInfo);
        } else {
            throw new InvalidFileFormat ("Property bindings refers to unknown message property :" + propertyName);
        }
    }

 
    @Override
    public boolean isConstant() {
        return false;   
    }
}
