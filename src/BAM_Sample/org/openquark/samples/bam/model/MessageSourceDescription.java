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
 * MessageSourceDescription.java
 * Created: 16-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.Collection;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.samples.bam.Message;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * This class is used to describe a message source
 * It provides descriptions for all of the properties that the messages support.
 */
public abstract class MessageSourceDescription {
    
    /**
     * This class is used to describe a single message property
     */
    public static class MessagePropertyDescription  {

        public final String name;

        public final int dataType; // one of the constants in interface Message

        public MessagePropertyDescription (String name, int dataType) {
            this.name = name;
            this.dataType = dataType;
        }
        
        /**
         * get the CAL type representing this property type
         * @param calServices
         * @return the CAL type expression
         */
        public TypeExpr getCalType(BasicCALServices calServices) {
            switch (dataType) {
            case Message.STRING:
                return calServices.getTypeFromQualifiedName(CAL_Prelude.TypeConstructors.String);   
            case Message.DOUBLE:
                return calServices.getTypeFromQualifiedName(CAL_Prelude.TypeConstructors.Double);
            case Message.INT:
                return calServices.getTypeFromQualifiedName(CAL_Prelude.TypeConstructors.Int);
            case Message.LONG:
                return calServices.getTypeFromQualifiedName(CAL_Prelude.TypeConstructors.Long);
            }
            return null;
        }
        
        //
        // Serialisation
        //

        /**
         * Method store
         * 
         * @param parentElem
         */
        public void store (Element parentElem) {
            Document document = parentElem.getOwnerDocument();
            
            Element propertyInfoElem = document.createElement(MonitorSaveConstants.PropertyInfo);
            parentElem.appendChild(propertyInfoElem);
            
            propertyInfoElem.setAttribute(MonitorSaveConstants.NameAttr, name);
            propertyInfoElem.setAttribute(MonitorSaveConstants.DataTypeAttr, Integer.toString(dataType));
        }

        /**
         * Method Load
         * 
         * @param propertyInfoElem
         * @return Returns a MessagePropertyInfo loaded from the XML element
         */
        public static MessagePropertyDescription Load (Element propertyInfoElem) throws BadXMLDocumentException {
            XMLPersistenceHelper.checkTag(propertyInfoElem, MonitorSaveConstants.PropertyInfo);
            
            String name = propertyInfoElem.getAttribute(MonitorSaveConstants.NameAttr);
            int dataType = XMLPersistenceHelper.getIntegerAttribute(propertyInfoElem, MonitorSaveConstants.DataTypeAttr).intValue();
            
            return new MessagePropertyDescription (name, dataType);
        }
    }

    private final String name;
    
    /**
     * Constructor MessageSourceDescription
     * 
     * @param name
     */
    MessageSourceDescription (final String name) {
        this.name = name;
    }
    
    public String getName () {
        return name;
    }

    /**
     * Gets a collection containing all the properties that this message source provides.
     */
    abstract public Collection<MessagePropertyDescription> getMessagePropertyDescriptions();

    //
    // Serialisation
    //

    /**
     * Method store
     * 
     * @param parentElem
     */
    public void store (Element parentElem) {
        Document document = parentElem.getOwnerDocument();
        
        Element messageSourceElem = makeElement (document);
        
        parentElem.appendChild(messageSourceElem);
    }
    
    /**
     * Method makeElement
     * 
     * @param document
     * @return Returns an XML element that represents the message source
     */
    protected abstract Element makeElement (Document document);

}
