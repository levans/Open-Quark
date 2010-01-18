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
 * CALPersistenceHelper.java
 * Creation date: Aug 5, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * A static helper class to facilitate conversions between CAL objects and their XML representations.
 * @author Edward Lam
 */
public class CALPersistenceHelper {

    /**
     * Create a QualifiedName from an XML element
     *
     * @param nameElement Element the Element representing the name to create
     * @return QualifiedName the name that results from the element.
     * Exception if no module or unqualified name is provided.
     */
    public static QualifiedName elementToQualifiedName(Element nameElement) throws BadXMLDocumentException {
    
        String moduleName = nameElement.getAttribute(WorkspacePersistenceConstants.QUALIFIED_NAME_MODULE_ATTR);
        String unqualifiedName = nameElement.getAttribute(WorkspacePersistenceConstants.QUALIFIED_NAME_UNQUALIFIED_ATTR);
    
        if (moduleName.length() == 0) {
            XMLPersistenceHelper.handleBadDocument(nameElement, "No module name given.");
    
        } else if (unqualifiedName.length() == 0) {
            XMLPersistenceHelper.handleBadDocument(nameElement, "No unqualified name given.");
        }

        QualifiedName qualifiedName = null;
        try {
            qualifiedName = QualifiedName.make(ModuleName.make(moduleName), unqualifiedName);

        } catch (Exception e) {
            XMLPersistenceHelper.handleBadDocument(nameElement, "Invalid name:" + e.getMessage());
        }
        
        return qualifiedName;
    }

    /**
     * Create an XML element from a QualifiedName.
     *
     * @param name QualifiedName the QualifiedName to convert.
     * @param document Document the document in which the element is created
     * @return Element the Element representing the name.
     */
    public static Element qualifiedNameToElement(QualifiedName name, Document document) {
    
        Element nameElement = document.createElement(WorkspacePersistenceConstants.QUALIFIED_NAME_TAG);
        nameElement.setAttribute(WorkspacePersistenceConstants.QUALIFIED_NAME_MODULE_ATTR, name.getModuleName().toSourceText());
        nameElement.setAttribute(WorkspacePersistenceConstants.QUALIFIED_NAME_UNQUALIFIED_ATTR, name.getUnqualifiedName());
        return nameElement;
    }

    /**
     * Create an unqualifiedName from an XML element
     *
     * @param nameElement the Element representing the name to create
     * @return the name that results from the element.
     * Exception if no unqualified name is provided.
     */
    public static String elementToUnqualifiedName(Element nameElement) throws BadXMLDocumentException {
    
        // TODOEL: carry out more validation
        String unqualifiedName = nameElement.getAttribute(WorkspacePersistenceConstants.QUALIFIED_NAME_UNQUALIFIED_ATTR);
    
        if (unqualifiedName.length() == 0) {
            XMLPersistenceHelper.handleBadDocument(nameElement, "No unqualified name given.");
        }
    
        return unqualifiedName;
    }

    /**
     * Create an XML element from an unqualifiedName.
     *
     * @param unqualifiedName the name to convert.
     * @param document the document in which the element is created
     * @return Element the Element representing the name.
     */
    public static Element unqualifiedNameToElement(String unqualifiedName, Document document) {
    
        Element nameElement = document.createElement(WorkspacePersistenceConstants.QUALIFIED_NAME_TAG);
        nameElement.setAttribute(WorkspacePersistenceConstants.QUALIFIED_NAME_UNQUALIFIED_ATTR, unqualifiedName);
        return nameElement;
    }
}
