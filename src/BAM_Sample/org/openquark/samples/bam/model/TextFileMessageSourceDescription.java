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
 * TextFileMessageSourceDescription.java
 * Created: 19-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.Collection;

import org.openquark.samples.bam.TextFileMessageSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;




/**
 * This describes a file based message source.
 */
public class TextFileMessageSourceDescription extends MessageSourceDescription {
    
    private final String textFileName;
    
    public TextFileMessageSourceDescription (String name, String textFileName) {
        super (name);
        
        this.textFileName = textFileName;
    }
    
    public String getFileName () {
        return textFileName;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<MessagePropertyDescription> getMessagePropertyDescriptions() {
        return TextFileMessageSource.getMessagePropertyInfos(this);
    }
    
    //
    // Serialisation
    //

    /**
     * @see org.openquark.samples.bam.model.MessageSourceDescription#makeElement(org.w3c.dom.Document)
     */
    @Override
    protected Element makeElement (Document document) {
        Element sourceDescriptionElem = document.createElement(MonitorSaveConstants.TextFileMessageSource);
        
        sourceDescriptionElem.setAttribute(MonitorSaveConstants.NameAttr, getName());
        sourceDescriptionElem.setAttribute(MonitorSaveConstants.TextFileNameAttr, textFileName);
        
        return sourceDescriptionElem;
    }

    /**
     * Method Load
     * 
     * @param messageSourceElem
     * @return Returns a TextFileMessageSourceDescription loaded from the given XML element
     */
    public static TextFileMessageSourceDescription Load (Element messageSourceElem) throws InvalidFileFormat {
        String name = messageSourceElem.getAttribute(MonitorSaveConstants.NameAttr);
        String textFileName = messageSourceElem.getAttribute(MonitorSaveConstants.TextFileNameAttr);
        
        checkNonEmpty (name, "Invalid message source name");
        checkNonEmpty (textFileName, "Invalid message source file name");
        
        return new TextFileMessageSourceDescription (name, textFileName);
    }

    /**
     * Method checkNonEmpty
     * 
     * Throws an InvalidFileFormat exception if the string is empty 
     * 
     * @param string
     * @param message
     */
    private static void checkNonEmpty (String string, String message) throws InvalidFileFormat {
        if (string == null || string.length() == 0) {
            throw new InvalidFileFormat (message);
        }
    }
}
