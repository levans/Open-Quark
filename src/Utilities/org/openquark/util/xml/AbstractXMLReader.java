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
 * AbstractXMLReader.java
 * Created: Mar 24, 2006
 * By: Doug Janzen
 */
package org.openquark.util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Provides some handy methods for XML readers.
 * 
 * @author Doug Janzen
 */
public abstract class AbstractXMLReader {

    protected Document document;
    
    public AbstractXMLReader(Document document) {
        this.document = document;
    }
    

    /**
     * Returns the child element of parent with the given name.  If the element does not exist
     * then a BadXMLDocumentException is thrown.
     * 
     * @param parent
     * @param tag 
     * 
     * @return Returns the child element with the given name.
     * 
     * @throws BadXMLDocumentException if the given tag doesn't exist as a child of parent.
     */
    protected Element getMandatoryChildElement (Element parent, String tag) throws BadXMLDocumentException {
        Element childElement = XMLPersistenceHelper.getChildElement (parent, tag);
        checkElement (childElement, tag);
        return childElement;
    }
    

    /**
     * Checks that the given element is not null and has the given tag name.
     * 
     * @param element
     * @param tag
     * 
     * @throws BadXMLDocumentException
     */
    protected static void checkElement (Element element, String tag) throws BadXMLDocumentException {
        if (element == null)
            throw new BadXMLDocumentException (null, "Missing element: <" + tag + ">"); //$NON-NLS-1$ //$NON-NLS-2$
        
        if (!element.getTagName ().equals (tag))
            throw new BadXMLDocumentException (element, "Expected <" + tag + ">, found <" + element.getTagName () + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    /**
     * Returns the attribute with the given name, or null if it doesn't exist.
     * 
     * @param element
     * @param attrName
     * 
     * @return Returns the attribute, or null if it doesn't exist
     */
    protected String getOptionalAttribute (Element element, String attrName) {
        if (element.hasAttribute (attrName))
            return element.getAttribute (attrName);
        else
            return null;
    }
    
    /**
     * Returns the attribute with the given name, or throws a BadXMLDocumentException if
     * it doesn't exist.
     * 
     * @param element
     * @param attrName
     * 
     * @return Returns the attribute, or throws an exception if it doesn't exist
     * @throws BadXMLDocumentException 
     */
    protected String getMandatoryAttribute (Element element, String attrName) throws BadXMLDocumentException {
        if (element.hasAttribute (attrName))
            return element.getAttribute (attrName);
        else
            throw new BadXMLDocumentException (element, "Expected attribute '"+attrName+"' not found on element "+element.getTagName()); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
