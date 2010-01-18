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
 * AttributeSetSerializer.java
 * Created: Feb 11, 2005
 * By: ksit
 */
package org.openquark.util.xml;

import java.util.ArrayList;
import java.util.List;

import org.openquark.util.attributes.Attribute;
import org.openquark.util.attributes.AttributeSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This serializer serializes an AttributeSet as XML element.  This serializer
 * assumes that there exists another serializer for serializing and
 * deserializing attribute values.
 */
public class AttributeSetSerializer implements XMLElementSerializer {
    
    /** The "root" tag name of a color attribute node */
    public static final String ROOT_TAG = "AttributeSet"; //$NON-NLS-1$
    
    /**
     * @see org.openquark.util.xml.XMLElementSerializer#loadFromElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element)
     */
    public Object loadFromElement(XMLSerializationManager manager, Element element) {
        List<Attribute> attributes = new ArrayList<Attribute>(); 
        manager.loadFromChildElements(element, attributes);
        return new AttributeSet(attributes);
    }
    
    /**
     * @see org.openquark.util.xml.XMLElementSerializer#storeToElement(org.openquark.util.xml.XMLSerializationManager, org.w3c.dom.Element, java.lang.Object)
     */
    public void storeToElement(XMLSerializationManager manager, Element element, Object value) {
        AttributeSet attributes = (AttributeSet) value;
        Document doc = element.getOwnerDocument();
        Element attrElement = doc.createElement(ROOT_TAG);
        element.appendChild(attrElement);
        manager.storeToElement(attrElement, attributes.getAllAttributes());
    }
}