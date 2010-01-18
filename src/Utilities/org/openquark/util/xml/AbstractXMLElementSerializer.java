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
 * AbstractXMLElementSerializer.java
 * Created: Oct 8, 2004
 * By: Kevin Sit
 */
package org.openquark.util.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openquark.util.time.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This abstract implementation of the <code>XMLElementSerializer</code> interface
 * provides a set of commonly used methods for serializing and deserializing
 * 
 */
public abstract class AbstractXMLElementSerializer implements XMLElementSerializer {
    
    /**
     * Loads a boolean attribute that were stored as a CDATA element.
     * @param parentElement
     * @param name
     * @param defaultValue
     * @return boolean
     */
    protected boolean loadAttribute(Element parentElement, String name, boolean defaultValue) {
        String val = loadAttribute(parentElement, name, (String) null);
        return val == null ? defaultValue : Boolean.valueOf(val).booleanValue();
    }

    /**
     * Loads an int attribute that were stored as a CDATA element.
     * @param parentElement
     * @param name
     * @param defaultValue
     * @return int
     */
    protected int loadAttribute(Element parentElement, String name, int defaultValue) {
        String val = loadAttribute(parentElement, name, (String) null);
        if (val == null) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
    }

    /**
     * Loads a Time attribute that were stored as a CDATA element.
     * @param parentElement
     * @param name
     * @param defaultValue
     * @return Time
     */
    protected Time loadAttribute(Element parentElement, String name, Time defaultValue) {
        String val = loadAttribute(parentElement, name, (String) null);
        if (val == null) {
            return defaultValue;
        } else {
            return Time.fromSerializedForm(val);
        }
    }

    /**
     * Loads a string attribute that were stored as a CDATA element.
     * @param parentElement
     * @param name
     * @param defaultValue
     * @return String
     */
    protected String loadAttribute(Element parentElement, String name, String defaultValue) {
        Element elem = XMLPersistenceHelper.getChildElement(parentElement, name);
        if (elem != null) {
            String val = XMLPersistenceHelper.getChildText(elem);
            return val == null ? defaultValue : val;
        }
        return defaultValue;
    }
    
    /**
     * Loads a list of string attributes stored under the specified parent
     * element.
     * @param parentElement
     * @param listName
     * @param attributeName
     * @return List
     */
    protected List<String> loadStringAttributes(
            Element parentElement,
            String listName,
            String attributeName) {
        List<String> retval = new ArrayList<String>();
        Element listElement = XMLPersistenceHelper.getChildElement(parentElement, listName);
        if (listElement != null) {
            List<Element> elements = XMLPersistenceHelper.getChildElements(listElement, attributeName);
            for (Element element : elements) {
                String val = XMLPersistenceHelper.getChildText(element);
                if (val != null) {
                    retval.add(val);
                }
            }
        }
        
        return retval;
    }
    
    /**
     * Stores a boolean attribute value as CDATA element.
     * @param parentElement
     * @param name
     * @param flag
     */
    protected void storeAttribute(Element parentElement, String name, boolean flag) {
        String value = flag ? "true" : "false"; //$NON-NLS-1$ //$NON-NLS-2$
        storeAttribute(parentElement, name, value);
    }

    /**
     * Stores an int attribute value as CDATA element.
     * @param parentElement
     * @param name
     * @param integer
     */
    protected void storeAttribute(Element parentElement, String name, int integer) {
        storeAttribute(parentElement, name, String.valueOf(integer));
    }

    /**
     * Stores an int attribute value as CDATA element.
     * @param parentElement
     * @param name
     * @param time the time value to be persisted.  This value must not be <code>null</code>.
     */
    protected void storeAttribute(Element parentElement, String name, Time time) {
        storeAttribute(parentElement, name, time.toSerializedForm());
    }

    /**
     * Stores a string attribute value as CDATA element.
     * @param parentElement
     * @param name
     * @param value the string value to be persisted.  This value must not be <code>null</code>.
     */
    protected void storeAttribute(Element parentElement, String name, String value) {
        Document doc = parentElement.getOwnerDocument();
        Element elem = doc.createElement(name);
        elem.appendChild(XMLPersistenceHelper.createCDATASection(doc, value));
        parentElement.appendChild(elem);
    }
    
    /**
     * Stores a collection of string attributes under the given list name.  Each
     * string attribute is stored as a CDATA element, enclosed by an element 
     * of name <code>elementName</code>.
     * @param parentElement
     * @param listName
     * @param attributeName
     * @param strings
     */
    protected void storeStringAttributes(
            Element parentElement,
            String listName,
            String attributeName,
            Collection<String> strings) {
        Document doc = parentElement.getOwnerDocument();
        Element listElement = doc.createElement(listName);
        for (String string : strings) {
            Element attrElement = doc.createElement(attributeName);
            attrElement.appendChild(XMLPersistenceHelper.createCDATASection(doc, string));
            listElement.appendChild(attrElement);
        }
        parentElement.appendChild(listElement);
    }
    
}
