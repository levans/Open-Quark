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
 * GemCutterPersistenceHelper.java
 * Creation date: (30-Apr-02 12:19:35 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Point;

import org.openquark.util.Pair;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A static (non-instantiable) helper class to facilitate conversions between objects and their XML representations.
 * This class contains GemCutter specific helpers. More general helper facilities are in XMLPersistenceHelper.
 * @author Edward Lam
 */
final class GemCutterPersistenceHelper {

    /**
     * Create a Point from an XML element
     * @param pointElement Element the Element representing the point to create
     * @return Point the Point that results from the element. 
     * Exception if the element cannot be converted to a Point.
     */
    public static Point elementToPoint(Element pointElement) throws BadXMLDocumentException {

        Point gemLocation = new Point();

        String xStringLocation = pointElement.getAttribute(GemPersistenceConstants.LOCATION_X_ATTR);
        String yStringLocation = pointElement.getAttribute(GemPersistenceConstants.LOCATION_Y_ATTR);

        try {
            gemLocation.x = (int)Double.parseDouble(xStringLocation);
            gemLocation.y = (int)Double.parseDouble(yStringLocation);

        } catch (NumberFormatException nfe) {
            XMLPersistenceHelper.handleBadDocument(pointElement, "Can't convert to a point.");
        }
        
        return gemLocation;
    }

    /**
     * Create an XML element from a Point.
     * @param point Point the Point to convert
     * @param document Document the document in which the element is created
     * @return Element an XML Element representing the point
     */
    public static Element pointToElement(Point point, Document document) {

        Element pointElement = document.createElement(GemPersistenceConstants.LOCATION_TAG);
        pointElement.setAttribute(GemPersistenceConstants.LOCATION_X_ATTR, Integer.toString(point.x));
        pointElement.setAttribute(GemPersistenceConstants.LOCATION_Y_ATTR, Integer.toString(point.y));
        return pointElement;
    }

    /**
     * Create an unqualifiedName from an XML element
     *
     * @param argumentElement the Element representing the argument to create
     * @return Pair of (gem id, input index).
     * Exception if no gem id or input index are provided.
     */
    public static Pair<String, Integer> argumentElementToInputInfo(Element argumentElement) throws BadXMLDocumentException {

        String gemAttribute = argumentElement.getAttribute(GemPersistenceConstants.ARGUMENT_GEM_ATTR);
        String inputAttribute = argumentElement.getAttribute(GemPersistenceConstants.ARGUMENT_INPUT_ATTR);

        if (gemAttribute.length() == 0) {
            XMLPersistenceHelper.handleBadDocument(argumentElement, "No gem id given.");

        } else if (inputAttribute.length() == 0) {
            XMLPersistenceHelper.handleBadDocument(argumentElement, "No input index given.");
        }

        Integer inputIndex = null;
        try {
            inputIndex = Integer.valueOf(inputAttribute);

        } catch (NumberFormatException nfe) {
            XMLPersistenceHelper.handleBadDocument(argumentElement, "Can't convert input index.");
        }

        return new Pair<String, Integer>(gemAttribute, inputIndex);
    }
    
    /**
     * Create an XML element from a collector argument.
     *
     * @param partInput the input to convert
     * @param document the document in which the element is created
     * @param gemContext the context in which the gem is saved.
     * @return Element the Element representing the argument.
     */
    public static Element inputToArgumentElement(Gem.PartInput partInput, Document document, GemContext gemContext) {
        
        Element argumentElement = document.createElement(GemPersistenceConstants.ARGUMENT_TAG);
        argumentElement.setAttribute(GemPersistenceConstants.ARGUMENT_GEM_ATTR, gemContext.getIdentifier(partInput.getGem(), true));
        argumentElement.setAttribute(GemPersistenceConstants.ARGUMENT_INPUT_ATTR, Integer.toString(partInput.getInputNum()));
        return argumentElement;
    }
    
    /**
     * Return whether the element given corresponds to a displayed gem.
     * @param node the node to check
     * @return boolean true if the node corresponds to a gem element
     */
    public static boolean isDisplayedGemElement(Node node) {
        // Check that it's an element, and check its name.
        return node instanceof Element && node.getLocalName().equals(GemPersistenceConstants.DISPLAYED_GEM_TAG);
    }
}
