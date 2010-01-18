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
 * TriggerDescription.java
 * Created: 16-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.Collection;
import java.util.List;

import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * A trigger description represents a Gem and it's bindings 
 * that is used as a trigger.
 */
public class TriggerDescription extends BoundGemDescription {

    /**
     * Constructor TriggerDescription
     * 
     * @param gemName
     * @param inputBindings
     */
    public TriggerDescription (String gemName, List<InputBinding> inputBindings) {
        super (gemName, inputBindings);
    }

    /**
     * Method store
     * 
     * @param parentElem
     */
    public void store (Element parentElem) {
        Document document = parentElem.getOwnerDocument();
        
        Element triggerDescriptionElem = document.createElement(MonitorSaveConstants.TriggerDescription);
        parentElem.appendChild(triggerDescriptionElem);
        
        storeContents (triggerDescriptionElem);
    }

    /**
     * Method Load
     * 
     * @param triggerElem
     * @return Returns a TriggerDescription loaded from the given XML element
     */
    public static TriggerDescription Load (Element triggerElem, Collection<MessagePropertyDescription> messagePropertyInfos) throws BadXMLDocumentException, InvalidFileFormat {
        XMLPersistenceHelper.checkTag(triggerElem, MonitorSaveConstants.TriggerDescription);
        
        String gemName = loadGemName (triggerElem);
        List<InputBinding> inputBindings = loadInputBindings (triggerElem, messagePropertyInfos);
        
        return new TriggerDescription (gemName, inputBindings);
    }
}
