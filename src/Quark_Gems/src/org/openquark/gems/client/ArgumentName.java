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
 * ArgumentName.java
 * Creation date: Mar 31, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceConstants;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author Edward Lam
 */
/**
 * A class to manage the different names of an argument: base name and composite name.
 * <br><br>
 * Base name is the name used in calculation of a composite name. It starts out as one of the following:
 * <br><br>
 *      FunctionalAgentGem - the argument name in the CAL code or the default name if the argument is not named<br>
 *      CodeGem            - the argument name used in the code panel<br>
 *      Other              - the default name
 * <br><br>
 * The base name may be replaced with a name saved by a user. The composite name is the base name plus a
 * disambiguating suffix to make it unique to the input's forest. The composite name is displayed in tooltips
 * and the Gem Properties dialog.
 * 
 * @author Steve Norton
 */
public class ArgumentName {

    /** The base name of the input name. */
    private String baseName = null;
    
    /** The suffix used to make the name unique. */
    private String suffix = "";
    
    /** Whether or not the name was saved by the user. */
    private boolean isBaseNameSavedByUser = false;

    /**
     * ArgumentName constructor.
     * @param name the original name, a default will be used if null
     */
    public ArgumentName(String name) {
        baseName = name != null ? name : ArgumentMetadata.DEFAULT_ARGUMENT_NAME;
    }
    
    /**
     * ArgumentName copy constructor.
     * @param copy the InputName to copy
     */
    public ArgumentName(ArgumentName copy) {
        this.baseName = copy.getBaseName();
        this.suffix = copy.getDisambiguatingSuffix();
        this.isBaseNameSavedByUser = copy.isBaseNameSavedByUser();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ArgumentName: " + baseName + suffix;
    }
    
    /**
     * @return the base name of the InputName
     */
    public String getBaseName() {
        return baseName;
    }
    
    /**
     * Set the base name. Input names should be checked for name conflicts 
     * after using this method.
     * CAUTION: The new base name will be considered to be user saved.
     * @param baseName the new base name for the InputName
     */
    public void setBaseName(String baseName) {
        this.baseName = baseName;
        isBaseNameSavedByUser = true;
    }
    
    /**
     * @return the composite name of this input name
     */
    public String getCompositeName() {
        return baseName + suffix;
    }
    
    /**
     * @param suffix the suffix to use to make the composite name
     */
    public void setDisambiguatingSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    /**
     * @return the suffix used to make the composite name
     */
    public String getDisambiguatingSuffix() {
        return suffix;
    }
    
    /**
     * @return true if the name was saved by the user, false otherwise
     */
    public boolean isBaseNameSavedByUser() {
        return isBaseNameSavedByUser;
    }
    
    /**
     * @param savedByUser whether the base name was saved by the user
     */
    public void setBaseNameSavedByUser(boolean savedByUser) {
        isBaseNameSavedByUser = savedByUser;
    }
    
    /**
     * Attached the saved form of this object as a child XML node.
     * @param parentNode the node that will be the parent of the generated XML.
     */
    public void saveXML(Node parentNode) {
        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();
        Element resultElement = document.createElement(GemPersistenceConstants.INPUT_NAME_TAG);
        parentNode.appendChild(resultElement);
        
        resultElement.setAttribute(GemPersistenceConstants.INPUT_NAME_BASE_NAME_ATTR, baseName);
        resultElement.setAttribute(GemPersistenceConstants.INPUT_NAME_USER_SAVED_ATTR, isBaseNameSavedByUser ? XMLPersistenceConstants.TRUE_STRING : XMLPersistenceConstants.FALSE_STRING);
    }

    /**
     * Load this object's state.
     * @param parentNode the node representing the structure to deserialize.
     */
    public void loadXML(Node parentNode) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkIsTagElement(parentNode, GemPersistenceConstants.INPUT_NAME_TAG);
        Element parentElement = (Element) parentNode;

        baseName = parentElement.getAttribute(GemPersistenceConstants.INPUT_NAME_BASE_NAME_ATTR);
        isBaseNameSavedByUser = XMLPersistenceHelper.getBooleanAttribute(parentElement, GemPersistenceConstants.INPUT_NAME_USER_SAVED_ATTR);        

        // TODOEL: Need to do more validation here.
        // eg. if null, a default could be the name from the associated program/module object.
        if (baseName == null || baseName.length() == 0) {
            XMLPersistenceHelper.handleBadDocument(parentElement, "No input name provided.");
        }
    }
}