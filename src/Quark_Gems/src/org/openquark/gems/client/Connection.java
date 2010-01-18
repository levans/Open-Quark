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
 * Connection.java
 * Creation date: (12/7/00 10:50:56 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.util.List;
import java.util.Set;

import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A Connection links a part of one Gem to a part of another.
 * Creation date: (12/7/00 10:50:56 AM)
 * @author Luke Evans
 */
public class Connection {
    private final Gem.PartOutput from;
    private final Gem.PartInput to;

    /**
     * Construct a Connection from the source and destination parts.
     * Creation date: (12/7/00 10:53:08 AM)
     * @param source Gem.PartOutput the source part
     * @param destination Gem.PartInput the destination part
     */
    public Connection(Gem.PartOutput source, Gem.PartInput destination) {
        from = source;
        to = destination;
    }

    /**
     * Return the source (from) part.
     * Creation date: (12/8/00 9:18:02 AM)
     * @return Gem.PartOutput the source part
     */
    public Gem.PartOutput getSource() {
        // Return the source part
        return from;
    }   

    /**
     * Return the destination (to) part.
     * Creation date: (12/8/00 9:18:02 AM)
     * @return Gem.PartInput the destination part
     */
    public Gem.PartInput getDestination() {
        // Return the destination part
        return to;
    }

    /**
     * Return whether the element given corresponds to a connection.
     * @param element Element the element to check
     * @return boolean true if the element corresponds to a connection.
     */
    public static boolean isConnectionElement(Element element) {
        // Check the tag
        return element.getTagName().equals(GemPersistenceConstants.CONNECTION_TAG);
    }
    
    /**
     * Get a new connection represented by a given XML element.
     * @param element Element the element representing the connection to make.
     * @param gemContext the context in which the connection is being instantiated.
     * @param unknownGemIds the ids of gems that couldn't be instantiated because
     *   their types were unknown (eg. perhaps from a "future" version).
     * @return Connection a new connection corresponding to the data represented by the element.
     * Returns null if either of the connected gems represented is an "unknown" gem.
     */
    public static Connection elementToConnection(Element element, GemContext gemContext, Set<String> unknownGemIds) 
    throws BadXMLDocumentException {

        if (!isConnectionElement(element)) {
            XMLPersistenceHelper.handleBadDocument(element, "Expecting a connection element.");
        }
        
        List<Element> childElements = XMLPersistenceHelper.getChildElements (element);

        // Get source gem
        Element fromElement = childElements.get(0);
        String fromGemId = fromElement.getAttribute(GemPersistenceConstants.CONNECTION_GEM_ATTR);
        if (unknownGemIds.contains(fromGemId)) {
            return null;                                // unknown gem
        } 
        
        Gem fromGem = gemContext.getGem(fromGemId);
        if (fromGem == null) {
            XMLPersistenceHelper.handleBadDocument(fromElement, "Bad gem identifier: " + fromGemId);
        }
        

        // Get destination gem
        Element toElement = childElements.get(1);
        String toGemId = toElement.getAttribute(GemPersistenceConstants.CONNECTION_GEM_ATTR);
        if (unknownGemIds.contains(toGemId)) {
            return null;                                // unknown gem
        }

        Gem toGem = gemContext.getGem(toGemId);
        if (toGem == null) {
            XMLPersistenceHelper.handleBadDocument(toElement, "Bad gem identifier: " + toGemId);
        }
        

        // Get destination input index       
        Integer toGemInputIndex = XMLPersistenceHelper.getIntegerAttribute(toElement, GemPersistenceConstants.INPUT_TAG);
        int toGemIntInputIndex = toGemInputIndex.intValue();
        if (toGemIntInputIndex >= toGem.getNInputs() || toGemIntInputIndex < 0) {
            XMLPersistenceHelper.handleBadDocument(toElement, "Bad input index: " + toGemIntInputIndex);
        }

        Gem.PartOutput fromPart = fromGem.getOutputPart();
        Gem.PartInput toPart = toGem.getInputPart(toGemIntInputIndex);

        // To make reloading more robust, ignore whether the parts are actually capable of connecting.
        /*        Gem.PartOutput fromPart = fromDisplayedPart.getPartOutput();
         Gem.PartInput toPart = toDisplayedPart.getPartInput();

         if (!GemGraph.isCompositionConnectionValid(fromPart, toPart)) {
         XMLPersistenceHelper.handleBadDocument(element, "Can't make connection.");
         }
         */
        return new Connection(fromPart, toPart);
    }

    /**
     * Attach the saved form of this object as a child XML node.
     * @param parentNode the node that will be the parent of the generated XML.  
     *   The generated XML will be appended as a subtree of this node.  
     *   Note: parentNode must be a node type that can accept children (eg. an Element or a DocumentFragment)
     * @param gemContext the context in which the connection is saved.
     */
    public void saveXML(Node parentNode, GemContext gemContext) {

        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the connection element
        Element connectionElement = document.createElement(GemPersistenceConstants.CONNECTION_TAG);
        parentNode.appendChild(connectionElement);

        Gem fromGem = getSource().getGem();
        Gem toGem = getDestination().getGem();

        // Hopefully, the gems should be known to the context!
        String fromGemIndex = gemContext.getIdentifier(fromGem, false);
        String toGemIndex = gemContext.getIdentifier(toGem, false);
        int toInputIndex = getDestination().getInputNum();

        Element fromElement = document.createElement(GemPersistenceConstants.CONNECTION_FROM_TAG);
        fromElement.setAttribute(GemPersistenceConstants.CONNECTION_GEM_ATTR, fromGemIndex);
        connectionElement.appendChild(fromElement);

        Element toElement = document.createElement(GemPersistenceConstants.CONNECTION_TO_TAG);
        toElement.setAttribute(GemPersistenceConstants.CONNECTION_GEM_ATTR, toGemIndex);
        toElement.setAttribute(GemPersistenceConstants.CONNECTION_INPUT_ATTR, Integer.toString(toInputIndex));
        connectionElement.appendChild(toElement);
    }
}

