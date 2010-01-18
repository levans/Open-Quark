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
 * FunctionMetadata.java
 * Created: Apr 14, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.metadata;

import java.util.List;
import java.util.Locale;

import org.openquark.cal.services.CALFeatureName;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Metadata for CAL functions such as List.filter, Prelude.sin, etc.
 * 
 * @author Bo Ilic
 */
public class FunctionMetadata extends FunctionalAgentMetadata {
    
    /** A description of the return value of the function. */
    private String returnValueDescription;
    
    public FunctionMetadata(CALFeatureName featureName, Locale locale) {
        super(featureName, locale);
        
        if (featureName.getType() != CALFeatureName.FUNCTION) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * @return the description of the return value, or null if there is none.
     */
    public String getReturnValueDescription() {
        return returnValueDescription;
    }

    /**
     * Sets the description of the return value, or null if there is none.
     * @param returnValueDescription the description.
     */
    public void setReturnValueDescription(String returnValueDescription) {
        this.returnValueDescription = returnValueDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CALFeatureMetadata copyTo(CALFeatureMetadata metadata) {
        
        super.copyTo(metadata);
        
        if (metadata instanceof FunctionMetadata) {
            FunctionMetadata functionMetadata = (FunctionMetadata)metadata;
            functionMetadata.setReturnValueDescription(getReturnValueDescription());
        }
        
        return metadata;
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public CALFeatureMetadata copy(CALFeatureName featureName, Locale locale) {
        return copyTo(new FunctionMetadata(featureName, locale));
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#saveXML(org.w3c.dom.Node)
     */
    @Override
    public void saveXML(Node parentNode) {
        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();
        Element metadataElement = document.createElementNS(MetadataPersistenceConstants.METADATA_NS, MetadataPersistenceConstants.FUNCTION_METADATA_TAG);
        parentNode.appendChild(metadataElement);
        super.saveXML(metadataElement);
        
        XMLPersistenceHelper.addTextElement(metadataElement, MetadataPersistenceConstants.RETURN_VALUE_TAG, returnValueDescription);
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#loadXML(org.w3c.dom.Node)
     */
    @Override
    public void loadXML(Node metadataNode) throws BadXMLDocumentException {
        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.FUNCTION_METADATA_TAG);
        List<Element> elements = XMLPersistenceHelper.getChildElements(metadataNode);
        super.loadXML(elements.get(0));
        
        Node element = elements.get(1);
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.RETURN_VALUE_TAG);
        returnValueDescription = XMLPersistenceHelper.getElementStringValue(element);
    }
}
