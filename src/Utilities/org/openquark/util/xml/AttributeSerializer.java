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
 * AttributeSerializer.java
 * Created: Feb 11, 2005
 * By: ksit
 */
package org.openquark.util.xml;

import java.util.ArrayList;
import java.util.List;

import org.openquark.util.Messages;
import org.openquark.util.attributes.Attribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This serializer serializes an attribute as XML element.  An attribute can contain
 * one or more AttributeValueSerializer.
 */
public class AttributeSerializer implements XMLElementSerializer {
    
    /** Use this message bundle to dig up localized messages */
    private static final Messages messages = PackageMessages.instance;
    
    /** The "root" tag name of a color attribute node */
    public static final String ROOT_TAG = "Attribute"; //$NON-NLS-1$
    
    /** Name of the attribute */
    public static final String NAME_ATTRIBUTE = "Name"; //$NON-NLS-1$
    
    /** If there are multiple values associated with a single attribute, warp them
     *  with this tag */
    public static final String LIST_OF_VALUES = "ListOfValues";  //$NON-NLS-1$

    /* (non-Javadoc)
     * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializer, org.w3c.dom.Element)
     */
    public Object loadFromElement(XMLSerializationManager manager, Element element) {
        // Load the attribute name, or return null if it cannot be found
        String name = XMLPersistenceHelper.getAttributeOrChildText(element, NAME_ATTRIBUTE);
        if (name == null) {
            throw new IllegalArgumentException(messages.getString("AttributeNameNull")); //$NON-NLS-1$
        }
        
        // Check to see if there are multiple values first
        Element lovElement = XMLPersistenceHelper.getChildElement(element, LIST_OF_VALUES);
        if (lovElement != null) {
            List<Attribute> values = new ArrayList<Attribute>(); 
            manager.loadFromChildElements(lovElement, values);
            return new Attribute(name, values);
        } else {
            List<Attribute> values = new ArrayList<Attribute>(); 
            manager.loadFromChildElements(lovElement, values);
            if (!values.isEmpty()) {
                return new Attribute(name, values.iterator().next());
            } else {
                // TODO Just return null for now.  Returning null will exclude
                // this attribute from the parent attribute set.  But eventually
                // this case should generate an exception
                return null;
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializer, org.w3c.dom.Element, java.lang.Object)
     */
    public void storeToElement(XMLSerializationManager manager, Element element, Object value) {
        Attribute attribute = (Attribute) value;
        Document doc = element.getOwnerDocument();
        Element attrElement = doc.createElement(ROOT_TAG);
        element.appendChild(attrElement);
        
        // Name of the attribute is stored as an attribute of the XML element
        attrElement.setAttribute(NAME_ATTRIBUTE, attribute.getName());
        
        // The value(s) is/are stored as a child element(s).  If there is only
        // one value, just store it as is.  Otherwise, wrap the list of values
        // by a ListOfValues tag
        List<?> values = attribute.getValues();
        if (values.size() == 1) {
            manager.storeToElement(attrElement, values.iterator().next());
        } else {
            Element lovElement = doc.createElement(LIST_OF_VALUES);
            attrElement.appendChild(lovElement);
            manager.storeToElement(lovElement, attribute.getValues());
        }
    }
}

