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
 * ArgumentMetadata.java
 * Created: Apr 15, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.metadata;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openquark.cal.services.CALFeatureName;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * ArgumentMetadata models an argument to a CAL functional agent. It provides support for UI clients
 * that want to assist the user in interactively providing a value for the argument.
 * 
 * @author Bo Ilic
 */
public class ArgumentMetadata extends CALFeatureMetadata {
    
    /** The default argument name for arguments. */
    public static final String DEFAULT_ARGUMENT_NAME = "arg";
        
    /**
     * A CALExpression which evaluates to a value of type String. It is intended to be used as the prompting
     * text for the user to enter a value for the argument.
     */  
    private CALExpression promptingTextExpression;
    
    /** Whether to restrict an argument's value to one from the list of default values. */
    private boolean defaultValuesOnly;
    
    /**
     * If the argument has type a, then this is a CAL expression that evaluates to a value of type [a],
     * which is interpreted as an ordered list of default values for the argument.
     */        
    private CALExpression defaultValuesExpression;
    
    /**
     * Used to validate values entered for an argument. If the argument has type a then this
     * CALExpression should define a function of type a -> Maybe String. When the user enters the value
     * of an argument, it is validated by calling the value validation expression on the value. A return
     * value of Nothing indicates acceptance. A return value of Just String indicates the value was not
     * validated successfully, and the String holds a textual explanation as to why. 
     */ 
    private CALExpression valueValidationExpression;

    /**
     * Constructor for a new ArgumentMetadata object.
     * @param featureName the feature name of the argument
     */
    public ArgumentMetadata(CALFeatureName featureName, Locale locale) {
        super(featureName, locale);
        
        if (featureName.getType() != CALFeatureName.ARGUMENT) {
            throw new IllegalArgumentException("invalid feature name: " + featureName);
        }
    }

    /**
     * @return a CALExpression which evaluates to a value of type String. It is intended to be used as the prompting
     *    text for the user to enter a value for the argument.
     */
    public CALExpression getPromptingTextExpression() {
        return promptingTextExpression;
    } 
    /**
     * @param expression the expression that returns the prompting text if the argument value is invalid
     */
    public void setPromptingTextExpression(CALExpression expression) {
        promptingTextExpression = expression;
    }
         
    /**
     * @return If the argument has type a, then this is a CAL expression that evaluates to a value of type [a],
     *    which is interpreted as an ordered list of default values for the argument. 
     */
    public CALExpression getDefaultValuesExpression() {
        return defaultValuesExpression;
    }
    
    /**
     * @param expression the expression that gives the list of default values
     */
    public void setDefaultValuesExpression(CALExpression expression) {
        defaultValuesExpression = expression;
    }    

    /**
     * @return used to validate values entered for an argument. If the argument has type a then this
     *    CALExpression should define a function of type a -> Maybe String. When the user enters the value
     *    of an argument, it is validated by calling the value validation expression on the value. A return
     *    value of Nothing indicates acceptance. A return value of Just String indicates the value was not
     *    validated successfully, and the String holds a textual explanation as to why. 
     */
    public CALExpression getValueValidationExpression() {
        return valueValidationExpression;
    }
    /**
     * @param expression the expression to use to validate values entered for this argument
     */
    public void setValueValidationExpression(CALExpression expression) {
        valueValidationExpression = expression;
    }
    
    /**
     * @return whether to restrict an argument's value to one from the list of default values. 
     */
    public boolean useDefaultValuesOnly() {
        return defaultValuesOnly;
    }
    
    /**
     * @param defaultValuesOnly whether to restrict an argument's value to one from the list of default values.
     */
    public void setDefaultValuesOnly(boolean defaultValuesOnly) {
        this.defaultValuesOnly = defaultValuesOnly;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public CALFeatureMetadata copy(CALFeatureName featureName, Locale locale) {
        return copyTo(new ArgumentMetadata(featureName, locale));
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#copyTo(org.openquark.cal.metadata.CALFeatureMetadata)
     */
    @Override
    public CALFeatureMetadata copyTo(CALFeatureMetadata metadata) {
        
        super.copyTo(metadata);
        
        if (metadata instanceof ArgumentMetadata) {
            ArgumentMetadata argMetadata = (ArgumentMetadata) metadata;
            argMetadata.setDefaultValuesOnly(defaultValuesOnly);
            
            CALExpression expr = defaultValuesExpression;
            argMetadata.setDefaultValuesExpression(expr != null ? expr.copy() : null);

            expr = promptingTextExpression;
            argMetadata.setPromptingTextExpression(expr != null ? expr.copy() : null);
        }
        
        return metadata;
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#loadXML(org.w3c.dom.Node)
     */
    @Override
    public void loadXML(Node metadataNode) throws BadXMLDocumentException {
        
        XMLPersistenceHelper.checkIsTagElement(metadataNode, MetadataPersistenceConstants.ARGUMENT_METADATA_TAG);
        
        List<Element> elements = XMLPersistenceHelper.getChildElements(metadataNode);
        Iterator<Element> it = elements.iterator();
        
        super.loadXML(it.next());
        
        // Get the defaults only element
        Element defaultsElement = it.next(); 
        XMLPersistenceHelper.checkTag(defaultsElement, MetadataPersistenceConstants.ARGUMENT_DEFAULTS_ONLY_TAG);
        defaultValuesOnly = XMLPersistenceHelper.getElementBooleanValue(defaultsElement);

        // Get the defaults expression element
        Element defaultsExpressionElement = it.next();
        XMLPersistenceHelper.checkTag(defaultsExpressionElement, MetadataPersistenceConstants.ARGUMENT_DEFAULTS_EXPRESSION_TAG);
        Element expressionElement = XMLPersistenceHelper.getChildElement(defaultsExpressionElement, MetadataPersistenceConstants.EXPRESSION_SECTION_TAG);
        defaultValuesExpression = MetadataPersistenceHelper.getElementExpressionValue(expressionElement);

        XMLPersistenceHelper.checkTag(defaultsElement, MetadataPersistenceConstants.ARGUMENT_DEFAULTS_ONLY_TAG);
        defaultValuesOnly = XMLPersistenceHelper.getElementBooleanValue(defaultsElement);

        // Get the prompting expression element
        Element promptingExpressionElement = it.next();
        XMLPersistenceHelper.checkTag(promptingExpressionElement, MetadataPersistenceConstants.ARGUMENT_PROMPTING_EXPRESSION_TAG);
        expressionElement = XMLPersistenceHelper.getChildElement(promptingExpressionElement, MetadataPersistenceConstants.EXPRESSION_SECTION_TAG);
        promptingTextExpression = MetadataPersistenceHelper.getElementExpressionValue(expressionElement);
    }
    
    /**
     * @see org.openquark.cal.metadata.CALFeatureMetadata#saveXML(org.w3c.dom.Node)
     */
    @Override
    public void saveXML(Node parentNode) {
        
        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();
        Element metadataElement = document.createElementNS(MetadataPersistenceConstants.METADATA_NS, MetadataPersistenceConstants.ARGUMENT_METADATA_TAG);
        parentNode.appendChild(metadataElement);
        
        super.saveXML(metadataElement);

        XMLPersistenceHelper.addBooleanElement(metadataElement, MetadataPersistenceConstants.ARGUMENT_DEFAULTS_ONLY_TAG, defaultValuesOnly);
    
        // Add an element for the defaults expression
        Element expressionElement = document.createElement(MetadataPersistenceConstants.ARGUMENT_DEFAULTS_EXPRESSION_TAG);
        metadataElement.appendChild(expressionElement);
        MetadataPersistenceHelper.addExpressionElement(expressionElement, defaultValuesExpression);
    
        // Add an element for the prompting expression
        expressionElement = document.createElement(MetadataPersistenceConstants.ARGUMENT_PROMPTING_EXPRESSION_TAG);
        metadataElement.appendChild(expressionElement);
        MetadataPersistenceHelper.addExpressionElement(expressionElement, promptingTextExpression);
    }
}
