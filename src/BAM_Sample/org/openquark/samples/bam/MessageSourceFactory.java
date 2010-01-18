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
 * MessageSourceFactory.java
 * Created: 19-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import org.openquark.samples.bam.model.InvalidFileFormat;
import org.openquark.samples.bam.model.MessageSourceDescription;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.model.MonitorSaveConstants;
import org.openquark.samples.bam.model.RandomMessageSourceDescription;
import org.openquark.samples.bam.model.TextFileMessageSourceDescription;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Element;


/**
 * This is a factory class for creating MessageSources from
 * MessageSourceDescripions.
 */
public final class MessageSourceFactory {

    /**
     * Method createMessageSource
     * 
     * @param jobDescription
     * @return MessageSource
     */
    public static MessageSource createMessageSource (MonitorJobDescription jobDescription) {
        MessageSourceDescription messageSourceDescription = jobDescription
                .getMessageSourceDescription ();

        if (messageSourceDescription instanceof TextFileMessageSourceDescription) {
            return TextFileMessageSource.createInstance (messageSourceDescription);
        } else if (messageSourceDescription instanceof RandomMessageSourceDescription) {
            return RandomMessageSource.createInstance(messageSourceDescription);
        } else {
            return null;
        }
    }

    //
    // MessageSourceFactory is not instantiated
    //
    private MessageSourceFactory () {
    }

    /**
     * Method Load
     * 
     * @param messageSourceElem
     * @return Returns a message source description loaded from the XML element
     */
    public static MessageSourceDescription Load (Element messageSourceElem)
            throws InvalidFileFormat {
        Element textFileMessageSourceElem = XMLPersistenceHelper.getChildElement (
                messageSourceElem, MonitorSaveConstants.TextFileMessageSource);

        if (textFileMessageSourceElem != null) {
            return TextFileMessageSourceDescription.Load (textFileMessageSourceElem);
        }

        Element randomMessageSourceElem = XMLPersistenceHelper.getChildElement (messageSourceElem,
                MonitorSaveConstants.RandomMessageSource);

        if (randomMessageSourceElem != null) {
            return RandomMessageSourceDescription.Load (randomMessageSourceElem);
        }

        // etc. for other supported message source types...

        throw new InvalidFileFormat ("Did not find a supported message source kind");
    }

}