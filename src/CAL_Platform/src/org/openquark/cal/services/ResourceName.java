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
 * ResourceName.java
 * Creation date: Sep 13, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.Iterator;
import java.util.List;

import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A ResourceName identifies a resource, not taking into account the resource's
 * type.
 * 
 * This class is meant to be subclassed in cases where a resouce cannot simply
 * be identified by just the resource type and feature name. One example is with
 * CAL metadata which are locale-specific, and hence the full identifier for a
 * metadata resource needs to be (type, feature name, locale), meaning that the
 * resource name should be a (feature name, locale) pair.
 * 
 * @see ResourceIdentifier
 * 
 * @author Joseph Wong
 */
public class ResourceName implements Comparable<ResourceName> {
    
    /** The string representation of the type of resource name this class represents. For use in XML serialization. */
    protected static final String RESOURCE_NAME_TYPE = "ResourceName";

    /** The feature name. */
    private final FeatureName featureName;
    
    /**
     * Constructs a ResourceName.
     * @param featureName the feature name.
     */
    public ResourceName(FeatureName featureName) {
        if (featureName == null) {
            throw new NullPointerException();
        }
        
        this.featureName = featureName;
    }
    
    /**
     * @return the feature name.
     */
    public FeatureName getFeatureName() {
        return featureName;
    }
    
    /**
     * @return the string representation of the type of resource name this class represents.
     */
    protected String getTypeString() {
        return RESOURCE_NAME_TYPE;
    }
    
    /**
     * Serialize this resource name to an XML representation that will be attached to the given parent node.
     * @param parentNode the parent node to attach the XML representation to
     */
    public void saveXML(Node parentNode) {
        
        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();
        
        Element nameElement = document.createElementNS(WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.RESOURCE_NAME_TAG);
        parentNode.appendChild(nameElement);
        
        XMLPersistenceHelper.addTextElement(nameElement, WorkspacePersistenceConstants.RESOURCE_NAME_TYPE_TAG, getTypeString());
        
        if (featureName instanceof CALFeatureName) {
            ((CALFeatureName)featureName).saveXML(nameElement);
        } else {
            throw new UnsupportedOperationException();
        }
        
        saveCustomContentXML(document, nameElement);
    }
    
    /**
     * Serialize the custom content of this resource name to an XML representation that will be attached to the given parent node.
     * Override this method in subclasses so that the subclass's content can be saved out properly.
     * 
     * @param document the XML document
     * @param parentNode the parent node to attach the XML representation to
     */
    protected void saveCustomContentXML(Document document, Element parentNode) {
        // nothing to do here in the base class
    }
    
    /**
     * Deserialize a ResourceName from its XML definition.
     * @param parentNode the node which contains the serialized xml definition, or null if the node does not correspond
     *   to the definition of a ResourceName.
     * @return the corresponding ResourceName.
     */
    public static ResourceName getResourceNameWithCALFeatureNameFromXML(Node parentNode) {
        try {
            
            XMLPersistenceHelper.checkIsTagElement(parentNode, WorkspacePersistenceConstants.RESOURCE_NAME_TAG);
            
            List<Element> elements = XMLPersistenceHelper.getChildElements(parentNode);
            Iterator<Element> it = elements.iterator();
            
            Node element = it.next();
            XMLPersistenceHelper.checkIsTagElement(element, WorkspacePersistenceConstants.RESOURCE_NAME_TYPE_TAG);
            String typeString = XMLPersistenceHelper.getElementStringValue(element);

            element = it.next();
            CALFeatureName name = CALFeatureName.getFromXML(element);
            
            if (typeString.equals(ResourceName.RESOURCE_NAME_TYPE)) {
                return new ResourceName(name);
                
            } else if (typeString.equals(LocalizedResourceName.LOCALIZED_RESOURCE_NAME_TYPE)) {
                return LocalizedResourceName.makeFromXML(name, it);

            } else {
                throw new BadXMLDocumentException(parentNode, "unknown resource name type: " + typeString);
            }

        } catch (BadXMLDocumentException e) {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "(FeatureName=" + getFeatureName() + ")";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        ResourceName otherName = (ResourceName)obj;
        return otherName.featureName.equals(featureName);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 17 * featureName.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(ResourceName otherResourceName) {
        return getFeatureName().getName().compareTo(otherResourceName.getFeatureName().getName());
    }
    
    /**
     * A filter for resource names.
     *
     * @author Joseph Wong
     */
    public interface Filter {
        /**
         * Tests whether or not the specified resource name should be included
         * in a list.
         * 
         * @param resourceName the resource name to be tested.
         * @return true if and only if resourceName should be included.
         */
        public boolean accept(ResourceName resourceName);
    }
}
