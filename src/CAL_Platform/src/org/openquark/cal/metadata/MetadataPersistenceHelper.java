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
 * MetadataPersistenceHelper.java
 * Creation date: (30-Apr-02 12:19:35 PM)
 * By: Edward Lam
 */
package org.openquark.cal.metadata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.Status;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.NamespaceInfo;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



/**
 * A non-instantiable helper class to facilitate conversions between metadata objects and their XML representations.
 * Note the class XMLPersistenceHelper provides more general helper facilities. The facilities
 * below are specific to metadata.
 *
 * @author Edward Lam
 */
final class MetadataPersistenceHelper {
    
    /*
     * Not intended to be instantiated.
     */
    private MetadataPersistenceHelper() {
    }
    
    /**
     * Creates the example metadata section at the given XML element.
     * @param example the CALExample to save
     * @param parentElement the XML element to add the section to
     */    
    public static void addExampleElement(Element parentElement, CALExample example) {

        // Add an example element
        Document document = parentElement.getOwnerDocument();
        Element element = document.createElement(MetadataPersistenceConstants.EXAMPLE_SECTION_TAG);
        parentElement.appendChild(element);

        XMLPersistenceHelper.addTextElement(element, MetadataPersistenceConstants.DESCRIPTION_TAG, example.getDescription());
        XMLPersistenceHelper.addBooleanElement(element, MetadataPersistenceConstants.EXAMPLE_EVALUATE_TAG, example.evaluateExample());

        addExpressionElement(element, example.getExpression());
    }

    /**
     * Creates the expression metadata section at the given XML element.
     * @param expression the CALExpression to save
     * @param parentElement the XML element to add the section to
     */
    public static void addExpressionElement(Element parentElement, CALExpression expression) {

        // Add an expression element
        Document document = parentElement.getOwnerDocument();
        Element element = document.createElement(MetadataPersistenceConstants.EXPRESSION_SECTION_TAG);
        parentElement.appendChild(element);

        if (expression == null) {
            return;
        }
        
        XMLPersistenceHelper.addTextElement(element, MetadataPersistenceConstants.EXPRESSION_MODULE_CONTEXT_TAG, expression.getModuleContext().toSourceText());
        XMLPersistenceHelper.addTextElement(element, MetadataPersistenceConstants.EXPRESSION_CAL_TEXT_TAG, expression.getExpressionText());
        XMLPersistenceHelper.addTextElement(element, MetadataPersistenceConstants.EXPRESSION_QUALIFIED_CAL_TEXT_TAG, expression.getQualifiedExpressionText());
        expression.getQualificationMap().saveToXML(element);
    }

    /**
     * Get the CALExample encoded in a given element.
     * @param element the root element of the example section
     * @return the CALExample represented by the example section
     * @throws BadXMLDocumentException
     */
    public static CALExample getElementExampleValue(Node element) throws BadXMLDocumentException {
        
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.EXAMPLE_SECTION_TAG);

        List<Element> elements = XMLPersistenceHelper.getChildElements(element);

        Element descriptionElement = elements.get(0);
        XMLPersistenceHelper.checkTag(descriptionElement, MetadataPersistenceConstants.DESCRIPTION_TAG);
        String description = XMLPersistenceHelper.getElementStringValue(descriptionElement);

        Element evaluateElement = elements.get(1);
        XMLPersistenceHelper.checkTag(evaluateElement, MetadataPersistenceConstants.EXAMPLE_EVALUATE_TAG);
        boolean evaluate = XMLPersistenceHelper.getElementBooleanValue(evaluateElement);

        Element expressionElement = elements.get(2);
        XMLPersistenceHelper.checkTag(expressionElement, MetadataPersistenceConstants.EXPRESSION_SECTION_TAG);
        CALExpression expression = getElementExpressionValue(expressionElement);
        
        return new CALExample(expression, description, evaluate);
    }
    
    /**
     * Gem the CALExpression encoded in a given element.
     * @param element the root element of the expression section
     * @return the CALExpression represented by the expression section
     * @throws BadXMLDocumentException
     */
    public static CALExpression getElementExpressionValue(Node element) throws BadXMLDocumentException {
        
        XMLPersistenceHelper.checkIsTagElement(element, MetadataPersistenceConstants.EXPRESSION_SECTION_TAG);
        
        List<Element> elements = XMLPersistenceHelper.getChildElements(element);
        
        if (elements.isEmpty()) {
            return null;
        }
        
        Element moduleElement = elements.get(0);
        XMLPersistenceHelper.checkTag(moduleElement, MetadataPersistenceConstants.EXPRESSION_MODULE_CONTEXT_TAG);
        String moduleContextString = XMLPersistenceHelper.getElementStringValue(moduleElement);

        Element textElement = elements.get(1);
        XMLPersistenceHelper.checkTag(textElement, MetadataPersistenceConstants.EXPRESSION_CAL_TEXT_TAG);
        String calText = XMLPersistenceHelper.getElementStringValue(textElement);
        
        String qualifiedCalText = null;
        CodeQualificationMap qualificationMap = new CodeQualificationMap();
        if (elements.size() > 2) {
            // This check is done for backwards compatibility; the following code 
            // is executed only for expressions which contain a qualification map. 
            
            Element textElement2 = elements.get(2);
            XMLPersistenceHelper.checkTag(textElement2, MetadataPersistenceConstants.EXPRESSION_QUALIFIED_CAL_TEXT_TAG);
            qualifiedCalText = XMLPersistenceHelper.getElementStringValue(textElement2);
            
            qualificationMap.loadFromXML((Element)element);
        }
        
        if (calText == null) {
            calText = "";
        }
        if (qualifiedCalText == null) {
            qualifiedCalText = "";
        }
        
        ModuleName moduleContext = ModuleName.make(moduleContextString);
        
        return new CALExpression(moduleContext, calText, qualificationMap, qualifiedCalText);
    }
    
    /**
     * Saves the given metadata object to the given file.
     * @param metadata the metadata object to save
     * @param metadataOutputStream the stream to save the metadata to
     */
    static void saveMetadata(CALFeatureMetadata metadata, OutputStream metadataOutputStream) {
    
        // Convert the metadata into an XML document
        Document document = XMLPersistenceHelper.getEmptyDocument();
        metadata.saveXML(document);
    
        // Attach namespace and schema information to the document element
        NamespaceInfo metadataNSInfo = CALFeatureMetadata.getNamespaceInfo();
        XMLPersistenceHelper.attachNamespaceAndSchema(document, metadataNSInfo, MetadataPersistenceConstants.METADATA_SCHEMA_LOCATION, MetadataPersistenceConstants.METADATA_NS);

        // Write the document to the metadata file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(metadataOutputStream);
        XMLPersistenceHelper.documentToXML(document, bufferedOutputStream, false);
    }

    /**
     * Load metadata from an input stream.
     * @param featureName the name of the feature associated with the metadata resource
     * @param locale the locale to be associated with the metadata object.
     * @param metadataInStream the stream to load the metadata from
     * @param importStatus the tracking status object
     * @return the metadata object the metadata was stored in (same as the passed in object).
     */
    static CALFeatureMetadata loadMetadata(CALFeatureName featureName, Locale locale, InputStream metadataInStream, Status importStatus) {
        
        try {
            BufferedInputStream bufferedInStream = new BufferedInputStream(metadataInStream);
            
            // Create an empty metadata for loading.
            CALFeatureMetadata metadata = MetadataManager.getEmptyMetadata(featureName, locale);
    
            // Create a document from the stream and load the metadata from it.
            Document document = XMLPersistenceHelper.documentFromXML((bufferedInStream));
            metadata.loadXML(document.getDocumentElement());
            
            return metadata;
    
            
        } catch (XMLPersistenceHelper.DocumentConstructionException ex) {
            // This can happen for invalid metadata files.
            importStatus.add(new Status(Status.Severity.WARNING, "Metadata file is invalid for: " + featureName, ex));
            
        } catch (BadXMLDocumentException ex) {
            // This can happen for invalid metadata files.
            importStatus.add(new Status(Status.Severity.WARNING, "Metadata file is invalid for: " + featureName, ex));
        
        } catch (Exception ex) {
            // Not sure what happened here.
            importStatus.add(new Status(Status.Severity.WARNING, "Unknown exception while loading metadata for: " + featureName, ex));
        }
        
        // An error occurred during loading so return the default empty metadata.
        return MetadataManager.getEmptyMetadata(featureName, locale);
    }
}
