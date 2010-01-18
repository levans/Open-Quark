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
 * LocalizedResourceName.java
 * Creation date: Sep 13, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.util.Iterator;
import java.util.Locale;

import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A LocalizedResourceName identifies a localized resource, not taking into account the resource's type.
 * @author Joseph Wong
 */
public class LocalizedResourceName extends ResourceName {

    /** The string representation of the type of resource name this class represents. For use in XML serialization. */
    protected static final String LOCALIZED_RESOURCE_NAME_TYPE = "LocalizedResourceName";

    /** The locale associated with the resource. */
    private final Locale locale;
    
    /**
     * Constructs a LocalizedResourceName.
     * @param featureName the feature name.
     * @param locale the locale associated with the resource.
     */
    public LocalizedResourceName(FeatureName featureName, Locale locale) {
        super(featureName);
        if (locale == null) {
            throw new NullPointerException();
        }
        this.locale = locale;
    }
    
    /**
     * @return the locale associated with the resource.
     */
    public Locale getLocale() {
        return locale;
    }
    
    /**
     * Static helper method for obtaining the locale of a general ResourceName.
     * A ResourceName that is not a LocalizedResourceName is taken to be associated with
     * the neutral locale.
     * 
     * @param resourceName the ResourceName whose associated locale is to be obtained.
     * @return the locale associated with the resource.
     */
    public static Locale localeOf(ResourceName resourceName) {
        if (resourceName instanceof LocalizedResourceName) {
            return ((LocalizedResourceName)resourceName).getLocale();
        } else {
            return LocaleUtilities.INVARIANT_LOCALE;
        }
    }
    
    /**
     * @return the string representation of the type of resource name this class represents.
     */
    @Override
    protected String getTypeString() {
        return LOCALIZED_RESOURCE_NAME_TYPE;
    }
    
    /**
     * Serialize the custom content of this resource name to an XML representation that will be attached to the given parent node.
     * Override this method in subclasses so that the subclass's content can be saved out properly.
     * 
     * @param document the XML document
     * @param parentNode the parent node to attach the XML representation to
     */
    @Override
    protected void saveCustomContentXML(Document document, Element parentNode) {
        super.saveCustomContentXML(document, parentNode);
        if (!locale.equals(LocaleUtilities.INVARIANT_LOCALE)) {
            XMLPersistenceHelper.addTextElement(parentNode, WorkspacePersistenceConstants.RESOURCE_NAME_LOCALE_LANGUAGE_TAG, locale.getLanguage());
            XMLPersistenceHelper.addTextElement(parentNode, WorkspacePersistenceConstants.RESOURCE_NAME_LOCALE_COUNTRY_TAG, locale.getCountry());
            XMLPersistenceHelper.addTextElement(parentNode, WorkspacePersistenceConstants.RESOURCE_NAME_LOCALE_VARIANT_TAG, locale.getVariant());
        }
    }
    
    /**
     * Deserialize a LocalizedResourceName from its XML definition.
     * @param featureName the feature name, already deserialized.
     * @param parentNodeIt an iterator pointing at the child at which the deserialization is to begin.
     * @return the corresponding LocalizedResourceName.
     * @throws BadXMLDocumentException
     */
    protected static LocalizedResourceName makeFromXML(FeatureName featureName, Iterator<Element> parentNodeIt) throws BadXMLDocumentException {
        if (parentNodeIt.hasNext()) {
            Node element = parentNodeIt.next();
            XMLPersistenceHelper.checkIsTagElement(element, WorkspacePersistenceConstants.RESOURCE_NAME_LOCALE_LANGUAGE_TAG);
            String localeLanguage = XMLPersistenceHelper.getElementStringValue(element);
            if (localeLanguage == null) {
                localeLanguage = "";
            }
            
            element = parentNodeIt.next();
            XMLPersistenceHelper.checkIsTagElement(element, WorkspacePersistenceConstants.RESOURCE_NAME_LOCALE_COUNTRY_TAG);
            String localeCountry = XMLPersistenceHelper.getElementStringValue(element);
            if (localeCountry == null) {
                localeCountry = "";
            }
            
            element = parentNodeIt.next();
            XMLPersistenceHelper.checkIsTagElement(element, WorkspacePersistenceConstants.RESOURCE_NAME_LOCALE_VARIANT_TAG);
            String localeVariant = XMLPersistenceHelper.getElementStringValue(element);
            if (localeVariant == null) {
                localeVariant = "";
            }
            
            Locale locale = new Locale(localeLanguage, localeCountry, localeVariant);
            
            return new LocalizedResourceName(featureName, locale);
        } else {
            return new LocalizedResourceName(featureName, LocaleUtilities.INVARIANT_LOCALE); // there is no locale saved in the XML, so that means the locale is the neutral locale
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "(FeatureName=" + getFeatureName() + ", Locale=" + getLocale() + ")";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        
        LocalizedResourceName otherName = (LocalizedResourceName)obj;
        return otherName.locale.equals(locale);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 37 * super.hashCode() + 77 * locale.hashCode();
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ResourceName otherResourceName) {
        int result = getFeatureName().getName().compareTo(otherResourceName.getFeatureName().getName());
        if (result != 0) {
            return result;
        } else {
            Locale myLocale = getLocale();
            Locale otherLocale = ((LocalizedResourceName)otherResourceName).getLocale();
            return myLocale.toString().compareTo(otherLocale.toString());
        }
    }
}
