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
 * InstanceMethodMetadata.java
 * Creation date: Aug 9, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.metadata;

import java.util.List;
import java.util.Locale;

import org.openquark.cal.services.CALFeatureName;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Metadata for instance methods in CAL such as the association of Prelude.equals with
 * Prelude.notEqualsInt for the Prelude.Eq Prelude.Int instance.
 * 
 * @author Joseph Wong
 */
public class InstanceMethodMetadata extends CALFeatureMetadata {
    
    /** Metadata objects associated with the arguments of this instance method. */
    private ArgumentMetadata[] arguments = new ArgumentMetadata[0];
    
    /** A description of the return value of the class method. */
    private String returnValueDescription;
    
    /** An ordered list of examples illustrating the use of this instance method. */
    private CALExample[] examples = new CALExample[0];
    
    /**
     * An ordered list of categories to which this instance method belongs. This is intended for tools to allow for "cross-cutting"
     * classification of a instance method e.g. by concerns other than what module it happens to be defined in. For example, a functional
     * agent can belong to a "List" category and a "Math" category. The ordering is from more important to less important aspects. 
     */
    private String[] categories = new String[0];

    /**
     * Constructs a new InstanceMethodMetadata object.
     * @param featureName the name of the feature this metadata object is for
     * @param locale the locale associated with this metadata.
     */
    public InstanceMethodMetadata(CALFeatureName featureName, Locale locale) {
        super(featureName, locale);
        
        if (featureName.getType() != CALFeatureName.INSTANCE_METHOD) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @return the argument metadata for the arguments of the instance method. The returned
     * array may not contain metadata for all arguments. This method can return a zero-length array.
     * 
     * NOTE: Some of the metadata in the returned array may have a null display name if it does not have
     * a name assigned to it.
     */
    public ArgumentMetadata[] getArguments() {
        ArgumentMetadata[] arguments = new ArgumentMetadata[this.arguments.length];
        
        for (int i = 0; i < arguments.length; i++) {
            arguments[i] = (ArgumentMetadata) this.arguments[i].copy();
        }

        return arguments;
    }

    /**
     * Sets the argument metadata for the arguments of this instance method. The provided array
     * may not be null or contain null values. The array does not have to contain argument metadata
     * for each actual argument of the instance method, it can even be zero-length.
     * @param arguments the array of argument metadata
     */
    public void setArguments(ArgumentMetadata[] arguments) {
        
        ensureArray(arguments);
        
        this.arguments = new ArgumentMetadata[arguments.length];
        
        for (int i = 0; i < arguments.length; i++) {
            this.arguments[i] = (ArgumentMetadata) arguments[i].copy(); 
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
     * @return the examples for this instance method.
     */    
    public CALExample[] getExamples() {
        CALExample[] examplesCopy = new CALExample[examples.length];
        System.arraycopy(examples, 0, examplesCopy, 0, examples.length);
        return examplesCopy;
    }

    /**
     * Sets the examples for this instance methods.
     * @param examples the array of examples
     */    
    public void setExamples(CALExample[] examples) {
        ensureArray(examples);
        this.examples = new CALExample[examples.length];
        System.arraycopy(examples, 0, this.examples, 0, examples.length);
    }

    /**
     * @return the categories for this instance method. The returned array is a sorted list of categories
     * this instance method belongs to. The array is sorted from most-relevant to least-relevant.
     */
    public String[] getCategories() {
        String[] categoriesCopy = new String[categories.length];
        System.arraycopy(categories, 0, categoriesCopy, 0, categories.length);
        return categoriesCopy;
    }    

    /**
     * Set the categories for this instance method.
     * @param categories the array of categories sorted from most to least relevant
     */    
    public void setCategories(String[] categories) {
        ensureArray(categories);
        this.categories = new String[categories.length];
        System.arraycopy(categories, 0, this.categories, 0, categories.length);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CALFeatureMetadata copy(CALFeatureName featureName, Locale locale) {
        return copyTo(new InstanceMethodMetadata(featureName, locale));
    }

    /**
     * Copies this metadata object into the given metadata object.
     * @param metadata the metadata object to copy into
     */
    @Override
    public CALFeatureMetadata copyTo(CALFeatureMetadata metadata) {
        
        super.copyTo(metadata);
        
        if (metadata instanceof InstanceMethodMetadata) {
            InstanceMethodMetadata imMetadata = (InstanceMethodMetadata) metadata;
            imMetadata.setCategories(getCategories());

            CALExample[] examples = new CALExample[this.examples.length];
            for (int i = 0; i < examples.length; i++) {
                examples[i] = this.examples[i].copy();
            }
            imMetadata.setExamples(examples);
            
            imMetadata.setReturnValueDescription(getReturnValueDescription());
            
            ArgumentMetadata[] arguments = new ArgumentMetadata[this.arguments.length];
            
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = (ArgumentMetadata) this.arguments[i].copy();
            }
            
            imMetadata.setArguments(arguments);
        }
        
        return metadata;
    }
        
    /**
     * Ensures that the specified array is non-null and does not contain null elements.
     * @param array the array to check
     */
    private void ensureArray(Object[] array) {
        
        if (array == null) {
            throw new IllegalArgumentException("Can't set values to a null array, use a zero-length array instead.");
        }
        
        for (final Object element : array) {
            if (element == null) {
                throw new IllegalArgumentException("Arrays for values may not contain null elements.");
            }
        }
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#saveXML(org.w3c.dom.Node)
     */
    @Override
    public void saveXML(Node parentNode) {
        
        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();
        Element metadataElement = document.createElementNS(MetadataPersistenceConstants.METADATA_NS, MetadataPersistenceConstants.INSTANCE_METHOD_METADATA_TAG);
        parentNode.appendChild(metadataElement);
        
        super.saveXML(metadataElement);
        
        saveCategoriesSectionXML(metadataElement);
        saveArgumentsSectionXML(metadataElement);
        saveExamplesSectionXML(metadataElement);
        
        XMLPersistenceHelper.addTextElement(metadataElement, MetadataPersistenceConstants.RETURN_VALUE_TAG, returnValueDescription);
    }
    
    /**
     * Creates the categories metadata section at the given XML element.
     * @param parentElement the XML element to add the section to
     */    
    private void saveCategoriesSectionXML(Element parentElement) {
        
        // Add a categories section element
        Document document = parentElement.getOwnerDocument();
        Element sectionElement = document.createElement(MetadataPersistenceConstants.CATEGORIES_SECTION_TAG);
        parentElement.appendChild(sectionElement);
        
        // Add a CDATA section for each category
        for (final String element : categories) {
            CDATASection cdata = XMLPersistenceHelper.createCDATASection(document, element);
            sectionElement.appendChild(cdata);
        }
    }

    /**
     * Creates the arguments metadata section at the given XML element.
     * @param parentElement the XML element to add the section to
     */    
    private void saveArgumentsSectionXML(Element parentElement) {

        // Add an arguments section element
        Document document = parentElement.getOwnerDocument();
        Element sectionElement = document.createElement(MetadataPersistenceConstants.ARGUMENTS_SECTION_TAG);
        parentElement.appendChild(sectionElement);
        
        // Add an arguments node for each argument
        for (final ArgumentMetadata element : arguments) {
            element.saveXML(sectionElement);
        }
    }

    /**
     * Creates the examples metadata section at the given XML element.
     * @param parentElement the XML element to add the section to
     */
    private void saveExamplesSectionXML(Element parentElement) {
        
        // Add an examples section element
        Document document = parentElement.getOwnerDocument();
        Element sectionElement = document.createElement(MetadataPersistenceConstants.EXAMPLES_SECTION_TAG);
        parentElement.appendChild(sectionElement);
        
        // Add an arguments node for each argument
        for (final CALExample element : examples) {
            MetadataPersistenceHelper.addExampleElement(sectionElement, element);
        }
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#loadXML(org.w3c.dom.Node)
     */
    @Override
    public void loadXML(Node metadataNode) throws BadXMLDocumentException {
        
        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.INSTANCE_METHOD_METADATA_TAG);
        
        List<Element> elements = XMLPersistenceHelper.getChildElements(metadataNode);
        
        super.loadXML(elements.get(0));
        
        loadCategoriesSectionXML(elements.get(1));
        loadArgumentsSectionXML(elements.get(2));
        loadExamplesSectionXML(elements.get(3));
        
        Node element = elements.get(4);
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.RETURN_VALUE_TAG);
        returnValueDescription = XMLPersistenceHelper.getElementStringValue(element);
    }
    
    /**
     * Loads the arguments metadata from the instance method metadata section.
     * @param metadataNode the node from which to start loading
     * @throws BadXMLDocumentException
     */
    private void loadArgumentsSectionXML(Node metadataNode) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.ARGUMENTS_SECTION_TAG);
        
        // There should be one element for each argument.
        List<Element> argumentElements = XMLPersistenceHelper.getChildElements(metadataNode);
        int numArgNodes = argumentElements.size();
        
        arguments = new ArgumentMetadata[numArgNodes];
        
        for (int i = 0; i < numArgNodes; i++) {
            Element argElement = argumentElements.get(i);
            arguments[i] = new ArgumentMetadata(CALFeatureName.getArgumentFeatureName(i), getLocale());
            arguments[i].loadXML(argElement);
        }
    }
    
    /**
     * Loads the examples metadata from the instance method metadata section.
     * @param metadataNode the node from which to start loading
     * @throws BadXMLDocumentException
     */
    private void loadExamplesSectionXML(Node metadataNode) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.EXAMPLES_SECTION_TAG);
        
        List<Element> exampleElements = XMLPersistenceHelper.getChildElements(metadataNode);
        int numExamples = exampleElements.size();
        
        examples = new CALExample[numExamples];
        
        for (int i = 0; i < numExamples; i++) {
            
            Element exampleElement = exampleElements.get(i);
            XMLPersistenceHelper.checkTag(exampleElement, MetadataPersistenceConstants.EXAMPLE_SECTION_TAG);

            examples[i] = MetadataPersistenceHelper.getElementExampleValue(exampleElement);
        }
    }
    
    /**
     * Loads the categories metadata from the instance method metadata section.
     * @param metadataNode the node from which to start loading
     * @throws BadXMLDocumentException
     */
    private void loadCategoriesSectionXML(Node metadataNode) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.CATEGORIES_SECTION_TAG);
        
        NodeList categoryNodes = metadataNode.getChildNodes();
        int numCategories = categoryNodes.getLength();
        
        categories = new String[numCategories];
        
        for (int i = 0; i < numCategories; i++) {
            
            Node categoryNode = categoryNodes.item(i);
            
            if (categoryNode instanceof CharacterData) {
                categories[i] = ((CharacterData) categoryNode).getData();                
            }
        }
    }
}
