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
 * ConstantBinding.java
 * Created: 19-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.openquark.cal.services.BasicCALServices;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.GemGraph;
import org.openquark.gems.client.ValueGem;
import org.openquark.gems.client.services.GemFactory;
import org.openquark.samples.bam.BindingContext;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * A constant binding represents an input bound to a constant value.
 * 
 */
public class ConstantBinding extends InputBinding {
    
    private final Object value;
    
    public ConstantBinding (Object value) {
        this.value = value;
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
        ValueGem constantGem = new GemFactory(calServices).makeValueGem(value);
        
        gemGraph.addGem(constantGem);
        
        return constantGem;
    }

    /**
     * @return Returns the value.
     */
    public Object getValue () {
        return value;
    }

    /**
     * @see org.openquark.samples.bam.model.InputBinding#getPresentation()
     */
    @Override
    public String getPresentation () {
        if (value instanceof String) {
            return '"' + (String)value + '"';
        } else {
            return value.toString();
        }
    }

    /**
     * @see org.openquark.samples.bam.model.InputBinding#store(org.w3c.dom.Element)
     */
    @Override
    public void store (Element parentElem) {
        Document document = parentElem.getOwnerDocument();
        
        Element bindingElement = document.createElement(getXmlTag());
        parentElem.appendChild(bindingElement);
        
        String tag;
        
        if (value instanceof Integer) {
            tag = MonitorSaveConstants.IntConstant;
        } else if (value instanceof Double) {
            tag = MonitorSaveConstants.DoubleConstant;
        } else if (value instanceof Long) {
            tag = MonitorSaveConstants.LongConstant;
        } else if (value instanceof String) {
            tag = MonitorSaveConstants.StringConstant;
        } else {
            throw new IllegalStateException ("The value is an instance of an unsupported class");
        }
        
        Element valueElement = document.createElement(tag);
        
        valueElement.setAttribute(MonitorSaveConstants.ValueAttr, value.toString());
        
        bindingElement.appendChild(valueElement);
    }

    /**
     * Method getXmlTag
     * 
     * @return Returns the XML tag used to store this class
     */
    public static String getXmlTag () {
        return MonitorSaveConstants.ConstantBinding;
    }

    /**
     * Method Load
     * 
     * @param bindingElem
     * @return Returns a ConstantBinding loaded from the given XML element 
     */
    public static ConstantBinding Load (Element bindingElem) throws BadXMLDocumentException, InvalidFileFormat {
        XMLPersistenceHelper.checkTag(bindingElem, getXmlTag());
        
        List<Element> childELements = XMLPersistenceHelper.getChildElements(bindingElem);
        
        for (final Element element : childELements) {
            Object value = null;
            
            if (element.getTagName().equals(MonitorSaveConstants.IntConstant)) {
                value = XMLPersistenceHelper.getIntegerAttribute(element, MonitorSaveConstants.ValueAttr);
            } else if (element.getTagName().equals(MonitorSaveConstants.DoubleConstant)) {
                value = XMLPersistenceHelper.getDoubleAttribute(element, MonitorSaveConstants.ValueAttr);
            } else if (element.getTagName().equals(MonitorSaveConstants.LongConstant)) {
                String longString = element.getAttribute(MonitorSaveConstants.ValueAttr);
                
                try {
                    value = Long.decode(longString);
                } catch (NumberFormatException e) {
                    XMLPersistenceHelper.handleBadDocument(element, 
                            "Long expected for attribute \"" + MonitorSaveConstants.ValueAttr + "\".  Got: " + longString);
                }
            }
            else if (element.getTagName().equals(MonitorSaveConstants.StringConstant)) {
                value = element.getAttribute(MonitorSaveConstants.ValueAttr);
            }
            
            if (value != null) {
                return new ConstantBinding (value);
            }
        }
        
        throw new InvalidFileFormat ("ConstantBinding is missing its value");
    }


    @Override
    public boolean isConstant() {
        return true;   
    }
}
