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
 * ModuleMetadata.java
 * Created: Apr 14, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.metadata;

import java.util.Locale;

import org.openquark.cal.services.CALFeatureName;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Metadata for modules in CAL such as Prelude, DataContext, Layout, Soap etc.
 * 
 * @author Bo Ilic
 */
public class ModuleMetadata extends CALFeatureMetadata {
    
    public ModuleMetadata(CALFeatureName featureName, Locale locale) {
        super(featureName, locale);
        
        if (featureName.getType() != CALFeatureName.MODULE) {
            throw new IllegalArgumentException("invalid feature name: " + featureName);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CALFeatureMetadata copy(CALFeatureName featureName, Locale locale) {
        return copyTo(new ModuleMetadata(featureName, locale));
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#saveXML(org.w3c.dom.Node)
     */
    @Override
    public void saveXML(Node parentNode) {
        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();
        Element metadataElement = document.createElementNS(MetadataPersistenceConstants.METADATA_NS, MetadataPersistenceConstants.MODULE_METADATA_TAG);
        parentNode.appendChild(metadataElement);
        super.saveXML(metadataElement);
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#loadXML(org.w3c.dom.Node)
     */
    @Override
    public void loadXML(Node metadataNode) throws BadXMLDocumentException {
        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.MODULE_METADATA_TAG);
        super.loadXML(metadataNode.getFirstChild());
    }
}
