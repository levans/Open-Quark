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
 * CALFeatureMetadata.java
 * Created: Apr 14, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.LocalizedResourceName;
import org.openquark.cal.services.ResourceName;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.NamespaceInfo;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * CALFeatureMetadata is a common base class for features in CAL to which some metadata 
 * can be attached.
 * <p>
 * It also includes a mechanism by which arbitrary name-value pairs can be associated with a feature.
 * <p>
 * The CALFeatureMetadata hierarchy provides extra information about various CAL entities created by the CAL
 * compiler such as those in the ScopedEntity hierarchy as well as ModuleTypeInfo. This is analogous
 * to the relationship between the classes in the java.beans.FeatureDescriptor hierarchy and the
 * java.lang.reflect.AccessibleObject hierarchy. In particular, most of the information in the metadata classes
 * is optional, and null values may be returned if the information has not been supplied. In that case, a UI
 * tool can use the lower-level information supplied by the ScopedEntity and ModuleTypeInfo classes to provide
 * reasonable "default" behavior.
 * 
 * @author Bo Ilic
 */
abstract public class CALFeatureMetadata {
    
    /** The feature name and locale of the metadata. */
    private final LocalizedResourceName resourceName;
    
    /** localized name for this feature for use in UI displays. */
    private String displayName;
    
    /** localized short description of this feature. Generally a short phrase or single sentence. */
    private String shortDescription;
    
    /** localized long description of this feature. */
    private String longDescription;
    
    /** true if this feature is intended for expert users rather than normal users. */
    private boolean expert = false;
    
    /** true if this feature is mainly for tool use and should not be exposed to humans. */
    private boolean hidden = false;
    
    /** true if this feature is particularly important for presenting to humans. */
    private boolean preferred = false;
    
    /** a mechanism by which arbitrary name-value pairs can be associated with this feature. */
    private final Map<String, String> attributes = new HashMap<String, String>();
    
    /** 
     * List of CAL names that identify features related to this metadata object. This is useful for
     * UI clients where we want to display a see-also list. Since related features are shared between metadata
     * object we simply store the CAL name of the related feature. If a client request an actual metadata 
     * object for a related feature we fetch a fresh instance from the MetadataManager.
     */      
    private final List<CALFeatureName> relatedFeatures = new ArrayList<CALFeatureName>();
    
    /** the date-time at which this feature was created. */
    private Date creationDate;
    
    /** the date-time at which this feature or its associated metadata was last modified. */
    private Date modificationDate;
    
    /** the version number or version identifier of this feature. */
    private String version; //todoBI should this have a more structured format?
    
    /** the name of the author, organization or group associated with this feature. */
    private String author; 
    
    /**
     * Constructor for a new CALFeatureMetadata object.
     * @param featureName the feature name of the feature the metadata belongs to
     * @param locale the locale associated with the metadata.
     */
    public CALFeatureMetadata(CALFeatureName featureName, Locale locale) {
        
        if (featureName == null || locale == null) {
            throw new NullPointerException();
        }
        
        this.resourceName = new LocalizedResourceName(featureName, locale);
    }
    
    /**
     * @return the ResourceName naming this metadata resource.
     */
    public LocalizedResourceName getResourceName() {
        return resourceName;
    }
    
    /**
     * @return the name of the feature the metadata belongs to
     */
    public CALFeatureName getFeatureName() {
        return (CALFeatureName)resourceName.getFeatureName();
    }
    
    /**
     * @return the locale associated with this metadata.
     */
    public Locale getLocale() {
        return resourceName.getLocale();
    }
    
    /**
     * @return gets the localized name for this feature for use in UI displays. 
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = (displayName != null && displayName.trim().length() > 0) ? displayName : null;
    }

    /**
     * @return gets the localized short description of this feature. Generally a short phrase or single sentence.
     */
    public String getShortDescription() {
        return shortDescription;
    }
    
    /**
     * @param shortDescription
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = (shortDescription != null && shortDescription.trim().length() > 0) ? shortDescription : null;
    }

    /**
     * @return gets the localized long description of this feature.
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * @param longDescription
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = (longDescription != null && longDescription.trim().length() > 0) ? longDescription : null;
    }    

    /**
     * @return true if this feature is intended for expert users rather than normal users.
     */
    public boolean isExpert() {
        return expert;
    }

    /**
     * @param expert
     */
    public void setExpert(boolean expert) {
        this.expert = expert;
    }

    /**
     * @return true if this feature is mainly for tool use and should not be exposed to humans.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }    

    /**
     * @return true if this feature is particularly important for presenting to humans.
     */
    public boolean isPreferred() {
        return preferred;
    }

    /**
     * @param preferred
     */
    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }
    
    /**
     * @param attributeName
     * @return the value associated with the named attribute, or null if there is no such value.
     */
    public String getAttribute(String attributeName) {
        return attributes.get(attributeName);
    }    

    /**
     * @param attributeName
     * @param value
     */
    public void setAttribute(String attributeName, String value) {
        attributes.put (attributeName, value);
    }    
    
    /**
     * @param attributeName
     */
    public void clearAttribute(String attributeName) {
        attributes.remove(attributeName);
    }

    /**
     * The underlying collection cannot be modified through the iterator- this is for traversal purposes only.
     * @return an Iterator (with String values) over all the named attributes associated with this feature.
     */
    public Iterator<String> getAttributeNames(){
        return Collections.unmodifiableSet(attributes.keySet()).iterator();
    }

    /**
     * Clears all attributes of this metadata object
     */
    public void clearAttributes() {
        attributes.clear();
    }
     
    /**
     * @param relatedFeature the feature name of the related feature to add
     * @return boolean true if feature was added, false otherwise
     */
    public boolean addRelatedFeature(CALFeatureName relatedFeature) {
        return relatedFeatures.add(relatedFeature);
    }

    /**
     * @param relatedFeature the feature name of the related feature to remove
     * @return true if the feature was removed, false otherwise
     */
    public boolean removeRelatedFeature(CALFeatureName relatedFeature) {
        return relatedFeatures.remove(relatedFeature);
    }

    /**
     * Clears the list of related features.
     */
    public void clearRelatedFeatures() {
        relatedFeatures.clear();
    }

    /**
     * @return the number of related features
     */
    public int getNRelatedFeatures() {
        return relatedFeatures.size();
    }
    
    /**
     * @param n the index of the related feature to get
     * @return the feature name of the related feature
     */
    public CALFeatureName getNthRelatedFeature(int n) {
        return relatedFeatures.get(n);
    }

    /**
     * @return gets the date-time at which this feature was created.
     */
    public Date getCreationDate() {
        return creationDate != null ? (Date) creationDate.clone() : null;    
    }

    /**
     * @param creationDate
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate != null ? (Date) creationDate.clone() : null;   
    }

    /**
     * @return gets the date-time at which this feature or its associated metadata was last modified.
     */
    public Date getModificationDate() {
        return modificationDate != null ? (Date) modificationDate.clone() : null;            
    }

    /**
     * @param modificationDate
     */
    public void setModificationDate(Date modificationDate) { 
        this.modificationDate = modificationDate != null ? (Date) modificationDate.clone() : null;       
    }
    
    /**    
     * @return gets the version number or version identifier of this feature.
     */
    public String getVersion() {        
        return version;
    }

    /**   
     * @param version
     */
    public void setVersion(String version) {        
        this.version = (version != null && version.trim().length() > 0) ? version : null;
    }

    /**   
     * @return gets the name of the author, organization or group associated with this feature.
     */
    public String getAuthor() {        
        return author;
    }

    /**    
     * @param author
     */
    public void setAuthor(String author) {        
        this.author = (author != null && author.trim().length() > 0) ? author : null;
    }
    
    /**
     * @return a new copy of this metadata object.
     */
    public CALFeatureMetadata copy() {
        return copy(getFeatureName(), getLocale());
    }
    
    /**
     * @return a new copy of this metadata object with the given featureName and locale.
     */
    public abstract CALFeatureMetadata copy(CALFeatureName featureName, Locale locale);
    
    /**
     * Copies this metadata object into the given metadata object.
     * @param metadata the metadata object to copy to
     */
    public CALFeatureMetadata copyTo(CALFeatureMetadata metadata) {

        metadata.setAuthor(author);
        metadata.setVersion(version);
        metadata.setDisplayName(displayName);
        metadata.setShortDescription(shortDescription);
        metadata.setLongDescription(longDescription);
        
        metadata.setExpert(expert);
        metadata.setHidden(hidden);
        metadata.setPreferred(preferred);
        
        metadata.setCreationDate(creationDate);
        metadata.setModificationDate(modificationDate);
        
        metadata.clearAttributes();
        for (final String key : attributes.keySet()) {
              String attribute = attributes.get(key);
              metadata.setAttribute(key, attribute);
        }
        
        metadata.clearRelatedFeatures();
        for (final CALFeatureName featureName : relatedFeatures) {
            metadata.addRelatedFeature(featureName);
        }
        
        return metadata;
    }

    /**
     * Obtain the namespace info for metadata.
     * @return the metadata xml namespace info.
     */
    static NamespaceInfo getNamespaceInfo() {
        return new NamespaceInfo(MetadataPersistenceConstants.METADATA_NS, MetadataPersistenceConstants.METADATA_NS_PREFIX);
    }
    
    /**
     * Saves this metadata object as a child of the given node.
     * @param parentNode the node to save the metadata object to
     */
    public void saveXML(Node parentNode) {

        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();
        Element metadataElement = document.createElementNS(MetadataPersistenceConstants.METADATA_NS, MetadataPersistenceConstants.FEATURE_METADATA_TAG);
        parentNode.appendChild(metadataElement);
        
        // initialize the creation date if needed
        if (creationDate == null) {
            creationDate = new Date();
        }

        // update the modification date
        modificationDate = new Date();

        getResourceName().saveXML(metadataElement);
        XMLPersistenceHelper.addTextElement(metadataElement, MetadataPersistenceConstants.DISPLAY_NAME_TAG, displayName);
        XMLPersistenceHelper.addTextElement(metadataElement, MetadataPersistenceConstants.VERSION_TAG, version);
        XMLPersistenceHelper.addTextElement(metadataElement, MetadataPersistenceConstants.AUTHOR_TAG, author);
        XMLPersistenceHelper.addTextElement(metadataElement, MetadataPersistenceConstants.SHORT_DESCRIPTION_TAG, shortDescription);
        XMLPersistenceHelper.addTextElement(metadataElement, MetadataPersistenceConstants.LONG_DESCRIPTION_TAG, longDescription);
        XMLPersistenceHelper.addDateElement(metadataElement, MetadataPersistenceConstants.MODIFICATION_DATE_TAG, modificationDate);
        XMLPersistenceHelper.addDateElement(metadataElement, MetadataPersistenceConstants.CREATION_DATE_TAG, creationDate);
        XMLPersistenceHelper.addBooleanElement(metadataElement, MetadataPersistenceConstants.EXPERT_FEATURE_TAG, isExpert());
        XMLPersistenceHelper.addBooleanElement(metadataElement, MetadataPersistenceConstants.HIDDEN_FEATURE_TAG, isHidden());
        XMLPersistenceHelper.addBooleanElement(metadataElement, MetadataPersistenceConstants.PREFERRED_FEATURE_TAG, isPreferred());
        
        saveAttributesXML(metadataElement);
        saveRelatedFeaturesXML(metadataElement);
    }
    
    /**
     * Saves the attributes section of this metadata object as a child of the given element.
     * @param parentElement the element to save the attributes section to
     */
    private void saveAttributesXML(Node parentElement) {

        Document document = parentElement.getOwnerDocument();
        Element sectionElement = document.createElement(MetadataPersistenceConstants.ATTRIBUTES_SECTION_TAG);
        parentElement.appendChild(sectionElement);

        for (Iterator<String> attributeNames = getAttributeNames(); attributeNames.hasNext(); ) {
         
            String name = attributeNames.next();
            String value = getAttribute(name);
            
            // Add an attribute section element
            Element element = document.createElement(MetadataPersistenceConstants.ATTRIBUTE_SECTION_TAG);
            sectionElement.appendChild(element);
        
            XMLPersistenceHelper.addTextElement(element, MetadataPersistenceConstants.ATTRIBUTE_KEY_TAG, name);
            XMLPersistenceHelper.addTextElement(element, MetadataPersistenceConstants.ATTRIBUTE_VALUE_TAG, value);
        }
    }

    /**
     * Saves the related features section of this metadata object as a child of the given element.
     * @param parentElement the element to save the related features to
     */
    private void saveRelatedFeaturesXML(Node parentElement) {

        Document document = parentElement.getOwnerDocument();
        Element sectionElement = document.createElement(MetadataPersistenceConstants.RELATED_FEATURES_SECTION_TAG);
        parentElement.appendChild(sectionElement);

        Iterator<CALFeatureName> relatedFeatures = this.relatedFeatures.iterator();
        while (relatedFeatures.hasNext()) {
            CALFeatureName featureName = relatedFeatures.next();
            featureName.saveXML(sectionElement);
        }
    }
    
    /**
     * Loads this metadata object from the given node.
     * @param metadataNode the node from which to start loading
     * @throws BadXMLDocumentException
     */
    public void loadXML(Node metadataNode) throws BadXMLDocumentException {
        
        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.FEATURE_METADATA_TAG);
        
        List<Element> elements = XMLPersistenceHelper.getChildElements(metadataNode);
        Iterator<Element> it = elements.iterator();
        
        Node element = it.next();        
        ResourceName persistedResourceName = ResourceName.getResourceNameWithCALFeatureNameFromXML(element);
        if (!persistedResourceName.getFeatureName().equals(getFeatureName())) {
            throw new BadXMLDocumentException(element, "CAL feature name " + getFeatureName() + " does not match the persisted feature name");
        }
        
        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.DISPLAY_NAME_TAG);
        displayName = XMLPersistenceHelper.getElementStringValue(element);

        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.VERSION_TAG);
        version = XMLPersistenceHelper.getElementStringValue(element);
        
        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.AUTHOR_TAG);
        author = XMLPersistenceHelper.getElementStringValue(element);
        
        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.SHORT_DESCRIPTION_TAG);
        shortDescription = XMLPersistenceHelper.getElementStringValue(element);        

        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.LONG_DESCRIPTION_TAG);
        longDescription = XMLPersistenceHelper.getElementStringValue(element);

        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.MODIFICATION_DATE_TAG);
        modificationDate = XMLPersistenceHelper.getElementDateValue(element);

        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.CREATION_DATE_TAG);
        creationDate = XMLPersistenceHelper.getElementDateValue(element);

        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.EXPERT_FEATURE_TAG);
        expert = XMLPersistenceHelper.getElementBooleanValue(element);

        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.HIDDEN_FEATURE_TAG);
        hidden = XMLPersistenceHelper.getElementBooleanValue(element);

        element = it.next();
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.PREFERRED_FEATURE_TAG);
        preferred = XMLPersistenceHelper.getElementBooleanValue(element);

        // Load the attributes
        element = it.next();
        loadAttributesXML(element);
        
        // Load the related features
        element = it.next();
        loadRelatedFeaturesXML(element);
    }
    
    /**
     * Loads the attributes section from the given node.
     * @param metadataNode the node from which to start loading
     * @throws BadXMLDocumentException
     */
    private void loadAttributesXML(Node metadataNode) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.ATTRIBUTES_SECTION_TAG);
        
        List<Element> attributeNodes = XMLPersistenceHelper.getChildElements(metadataNode, MetadataPersistenceConstants.ATTRIBUTE_SECTION_TAG);
        
        for (final Element attributeNode : attributeNodes) {

            List<Element> elements = XMLPersistenceHelper.getChildElements(attributeNode);
            
            Element element = elements.get(0);
            XMLPersistenceHelper.checkTag(element, MetadataPersistenceConstants.ATTRIBUTE_KEY_TAG);
            String name = XMLPersistenceHelper.getElementStringValue(element);

            element = elements.get(1);
            XMLPersistenceHelper.checkTag(element, MetadataPersistenceConstants.ATTRIBUTE_VALUE_TAG);
            String value = XMLPersistenceHelper.getElementStringValue(element);
        
            setAttribute(name, value);
        }
    }
    
    /**
     * Loads the related features section from the given node.
     * @param metadataNode the node from which to start loading
     * @throws BadXMLDocumentException
     */    
    private void loadRelatedFeaturesXML(Node metadataNode) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.RELATED_FEATURES_SECTION_TAG);
        List<Element> featureNodes = XMLPersistenceHelper.getChildElements(metadataNode);
        
        for (final Element featureElement : featureNodes) {
            CALFeatureName featureName = CALFeatureName.getFromXML(featureElement);            
            relatedFeatures.add(featureName);
        }
    }
}
