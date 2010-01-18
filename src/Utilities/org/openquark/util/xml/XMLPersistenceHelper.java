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
 * XMLPersistenceHelper.java
 * Creation date: (30-Apr-02 12:19:35 PM)
 * By: Edward Lam
 */
package org.openquark.util.xml;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openquark.util.Messages;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
/**
 * A non-instantiable helper class to facilitate conversions between objects and their XML representations.
 * @author Edward Lam
 */
public final class XMLPersistenceHelper {

    /** Use this message bundle to dig up localized messages */
    private static final Messages messages = PackageMessages.instance;
    
    /** All output will use this encoding */
    private static final String OUTPUT_ENCODING = "UTF-8"; //$NON-NLS-1$

    /** The DocumentBuilderFactory object that can be used to build DocumentsBuilders */
    private static final DocumentBuilderFactory defaultDocumentBuilderFactory = DocumentBuilderFactory.newInstance();

    /**
     * A thread-local DocumentBuilder object that can be used to build Documents.  
     * This builder is created by the default document builder factory. 
     * We use thread-local because they are not thread-safe.
     */
    private static final ThreadLocal<DocumentBuilder> threadLocalDocumentBuilder = new ThreadLocal<DocumentBuilder>() {
        protected synchronized DocumentBuilder initialValue() {
            // Create the document builder
            DocumentBuilder builder = null;
            try {
                // Note that because this in initialValue(), this should happen after the static initializer, 
                // where the factory is configured.
                builder = defaultDocumentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            }
            return builder;
        }
    };
    
    /** The TransformerFactory is used to create XML transformers */ 
    private static final TransformerFactory transformerFactory;

    /*
     * Static initializer - set document builder factory options
     */
    static {
        // Set namespaceAware to true to get a DOM Level 2 tree with nodes containing namespace information.
        // This is necessary because the default value from JAXP 1.0 was defined to be false.
        defaultDocumentBuilderFactory.setNamespaceAware(true);
        
        // For JDK 5.0 (different from name for JDK 1.4)
        Class<?> transformerFactoryClass = classForName ("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"); //$NON-NLS-1$

        TransformerFactory factory = null;
        
        if (transformerFactoryClass != null)
            try {
                factory = (TransformerFactory)transformerFactoryClass.newInstance ();
                
                // Set the indent amount for 5.0
                // Note that for 1.4 it is set when the transformer is created
                factory.setAttribute("indent-number", Integer.valueOf(2)); //$NON-NLS-1$
            } catch (Exception e) {
                e.printStackTrace ();
            } 
            
        transformerFactory = factory;
    }
    
    /**
     * Method classForName
     * 
     * @param className
     * 
     * @return Returns the Class for the given name, or null
     */
    private static Class<?> classForName (String className) {
        try {
            return Class.forName (className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Error handler to report errors and warnings
     * Creation date: (30-Apr-02 1:46:52 PM)
     * @author Edward Lam
     */
    private static class SaxErrorHandler implements ErrorHandler {

        /** Error handler output goes here */
        private PrintWriter out;

        /**
         * Default constructor
         * @param out where the errors should go.
         */
        SaxErrorHandler(PrintWriter out) {
            this.out = out;
        }

        /**
         * Returns a string describing parse exception details
         * @param spe the relevant exception
         * @return the string with the exception info.
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null"; //$NON-NLS-1$
            }
            String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return info;
        }

        /*
         * Methods implementing ErrorHandler       ************************************************************
         */

        /**
         * Handle a warning.
         * @param spe the relevant exception
         * @throws SAXException
         */
        public void warning(SAXParseException spe) throws SAXException {
            out.println(messages.getString("Warning", getParseExceptionInfo(spe))); //$NON-NLS-1$
        }
        
        /**
         * Handle an error.
         * @param spe the relevant exception
         * @throws SAXException
         */
        public void error(SAXParseException spe) throws SAXException {
            String message = messages.getString("Error", getParseExceptionInfo(spe)); //$NON-NLS-1$
            throw new SAXException(message);
        }

        /**
         * Handle a fatal error.
         * @param spe the relevant exception
         * @throws SAXException
         */
        public void fatalError(SAXParseException spe) throws SAXException {
            String message = messages.getString("FatalError", getParseExceptionInfo(spe)); //$NON-NLS-1$
            throw new SAXException(message);
        }
    }

    /**
     * An exception class representing a failure to construct a document from its input.
     * Creation date: (Sep 16, 2002 2:54:04 PM)
     * @author Edward Lam
     */
    public static final class DocumentConstructionException extends Exception {
              
        private static final long serialVersionUID = -7815248783833112814L;

        /**
         * Default constructor for a DocumentConstructionException
         * @param cause
         */
        private DocumentConstructionException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * A StateNotPersistableException is thrown when an attempt is made to persist a persistable object that
     * is not in a persistable state.
     * @author Edward Lam
     */
    public static final class StateNotPersistableException extends IllegalStateException {
       
        private static final long serialVersionUID = -3423092696888926367L;

        /**
         * Constructor for a StateNotPersistableException.
         * @param message String the exception message.
         */
        public StateNotPersistableException(String message) {
            super(message);
        }
    }

    /** This class is not intended to be instantiated. */
    private XMLPersistenceHelper() {
    }

    /**
     * @return a document builder that can be used to build documents.
     */
    private static DocumentBuilder getDocumentBuilder() {
        return threadLocalDocumentBuilder.get();
    }
    
    /**
     * Get an empty, rootless document.
     * @return Document an empty, rootless document.
     */
    public static Document getEmptyDocument() {
        return getEmptyDocument(getDocumentBuilder());
    }
    
    /**
     * Get an empty, rootless document using the document builder factory provided
     * @param builder
     * @return Document an empty, rootless document.
     */
    public static Document getEmptyDocument(DocumentBuilder builder) {
        // Create a new document.
        return builder.newDocument();
    }
    
    /**
     * Converts a DOM Tree to a string.  This method can be used to do a proper
     * comparison of two xml documents.  Helper method that converts the document
     * to xml using the StringWriter.  
     * @param document Document the XML document in the form of a DOM Tree to convert
     * @param indentOutput
     * @return a string containing the written XML for the document
     */
    public static String documentToString(Node document, boolean indentOutput) {
        StringWriter writer = new StringWriter();
        documentToXML(document, writer, indentOutput);
        return writer.toString();
    }
    
    /**
     * Convert a DOM Tree to an XML document.
     * This method can be used, for example, to write a DOM tree to a file.
     * @param document Document the XML document in the form of a DOM Tree to convert
     * @param outputStream OutputStream the OutputStream into which to write the document
     * @param indentOutput
     */
    public static void documentToXML(Node document, OutputStream outputStream, boolean indentOutput) {
        // Create a StreamResult object to take the results of the transformation
        StreamResult resultHolder = new StreamResult(outputStream);
        
        documentToXML(document, resultHolder, indentOutput);
    }

    /**
     * Convert a DOM Tree to an XML document.
     * This method can be used, for example, to write a DOM tree to a file.
     * @param document Document the XML document in the form of a DOM Tree to convert
     * @param writer Writer the Writer into which to write the document
     * @param indentOutput
     */
    public static void documentToXML(Node document, Writer writer, boolean indentOutput) {
        // Create a StreamResult object to take the results of the transformation
        StreamResult resultHolder = new StreamResult(writer);
        
        documentToXML(document, resultHolder, indentOutput);
    }

    /**
     * Convert a DOM Tree to an XML document. This method can be used, for example, to write a DOM tree to a String.
     * 
     * @param document
     *            Document the XML document in the form of a DOM Tree to convert
     * @param resultHolder
     *            StreamResult receives he results of the transformation
     * @param indentOutput
     */
    private static void documentToXML(Node document, StreamResult resultHolder, boolean indentOutput) {
        try {
            // Create a transformer object to perform the transformation
            Transformer transformer = null;
            synchronized (transformerFactory) {
                transformer = transformerFactory.newTransformer();
            }

            if (indentOutput) {
                // Set the indent property
                transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

                // Try to change the default indent amount using an undocumented option supported by
                // the apache XML parser (for JDK 1.4)
                try {
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$
                } catch (IllegalArgumentException iae) {
                    // The indent option is not standardized and is not available in all XML parsers.
                    // Specifically, the Oracle parser doesn't like it.
                System.out.println(messages.getString("IndentFailedOnSave")); //$NON-NLS-1$
                }
            }

            // Construct a DOMSource to act as the source of the transformation
            DOMSource source = new DOMSource(document);

            // Transform the tree to XML
            transformer.transform(source, resultHolder);

        } catch (TransformerException te) {
            // shouldn't happen
        throw new Error(messages.getString("ProgrammingError"), te); //$NON-NLS-1$
        }
    }

    /**
     * Convert an XML document into its DOM Tree representation
     *
     * @param inputStream InputStream the stream from which to read the XML document.
     * @return Document the resulting document.  Null if the parse failed.
     * @throws DocumentConstructionException
     */
    public static Document documentFromXML(InputStream inputStream) throws DocumentConstructionException {
        DocumentBuilder builder = getDocumentBuilder();
        return documentFromXML(builder, inputStream);
    }
    
    /**
     * Convert an XML document into its DOM Tree representation
     * @param documentBuilder
     * @param inputStream InputStream the stream from which to read the XML document.
     * @return Document the resulting document.  Null if the parse failed.
     * @throws DocumentConstructionException
     */
    public static Document documentFromXML(DocumentBuilder documentBuilder,
                                           InputStream inputStream) throws DocumentConstructionException {

        try {
            // Set an ErrorHandler before parsing
            OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, OUTPUT_ENCODING);
            documentBuilder.setErrorHandler(new SaxErrorHandler(new PrintWriter(errorWriter, true)));
    
            // Parse the input file
            Document document = documentBuilder.parse(inputStream);
    
            return document;

        } catch (IOException ioe) {
            throw new DocumentConstructionException(ioe);

        } catch (SAXException se) {
            throw new DocumentConstructionException(se);
        }
    }

    /**
     * Loads the specified file as an XML DOM document.
     * 
     * @param pathname
     * @return Document
     * @throws DocumentConstructionException
     */
    public static Document documentFromXMLFile(String pathname) throws DocumentConstructionException {
        InputStream inputStream = null;

        // Create a DOM document from the XML data.
        final Document document;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(pathname));
            document = documentFromXML(inputStream);
        }
        catch (IOException ioe) {
            throw new DocumentConstructionException(ioe);
        }
        finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (Exception e) {
                }
            }
        }

        return document;
    }

    /**
     * Convert an XML document into its DOM Tree representation
     *
     * @param xmlString String the XML string data.
     * @return Document the resulting document.  Null if the parse failed.
     * @throws DocumentConstructionException
     */
    public static Document documentFromXML(String xmlString) throws DocumentConstructionException {
        DocumentBuilder builder = getDocumentBuilder();
        return documentFromXML(builder, xmlString);
    }
    
    /**
     * Convert an XML document into its DOM Tree representation
     * @param documentBuilder
     * @param xmlString String the XML string data.
     * @return Document the resulting document.  Null if the parse failed.
     * @throws DocumentConstructionException
     */
    public static Document documentFromXML(DocumentBuilder documentBuilder,
                                           String xmlString) throws DocumentConstructionException {

        try {
            // Set an ErrorHandler before parsing
            OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, OUTPUT_ENCODING);
            documentBuilder.setErrorHandler(new SaxErrorHandler(new PrintWriter(errorWriter, true)));
    
            // Parse the input file
            Document document = documentBuilder.parse(new InputSource (new StringReader (xmlString)));
    
            return document;

        } catch (IOException ioe) {
            throw new DocumentConstructionException(ioe);

        } catch (SAXException se) {
            throw new DocumentConstructionException(se);
        }
    }

    /**
     * Check that a given element has the expected tag name.
     * Note: only the local part of the element name will be checked.
     * @param element Element the element to check.
     * @param expectedTag the tag that the element is expected to have.
     * @throws BadXMLDocumentException
     */
    public static void checkTag(Element element, String expectedTag) throws BadXMLDocumentException {
        
        if (element == null || !element.getLocalName().equals(expectedTag)) {
            String string = "Null"; //$NON-NLS-1$
            handleBadDocument(element,
                    messages.getString("XMLStructureError", expectedTag,  //$NON-NLS-1$
                                       (element == null ? 
                                               messages.getString(string) : 
                                               element.getTagName())));
        }
    }

    /**
     * Check that a given node has the expected prefix.
     * @param node Node the node to check.
     * @param expectedPrefix String the prefix that the element is expected to have.
     * @throws BadXMLDocumentException
     */
    public static void checkPrefix(Node node, String expectedPrefix) throws BadXMLDocumentException {

        String prefix = node.getPrefix();

        if (prefix == null) {
            if (expectedPrefix != null && expectedPrefix.length() != 0) {
                handleBadDocument(node, messages.getString("ExpectingPrefix", expectedPrefix)); //$NON-NLS-1$
            }

        } else {
            if (!prefix.equals(expectedPrefix)) {
                handleBadDocument(node, messages.getString("ExpectingPrefix2", expectedPrefix, prefix)); //$NON-NLS-1$
            }
        }
    }

    /**
     * Check that a given node is actually an element.
     * @param node Node the node to check.
     * @throws BadXMLDocumentException
     */
    public static void checkIsElement(Node node) throws BadXMLDocumentException {
        if (!(node instanceof Element)) {
            handleBadDocument(node,
                              (node == null ? 
                                      messages.getString("XMLStructureError2") :  //$NON-NLS-1$
                                      messages.getString("XMLStructureError3", node.getClass().toString()))); //$NON-NLS-1$
        }
    }

    /**
     * Check that a given node is actually an element of the specified tag.
     * @param node Node the node to check.
     * @param tag
     * @throws BadXMLDocumentException
     */
    public static void checkIsTagElement(Node node, String tag) throws BadXMLDocumentException {
        checkIsElement(node);
        checkTag((Element) node, tag);
    }
    
    /**
     * Convert instances of a newline in a string to '\n'.
     * This is particularly necessary when creating a CDATA node from Windows, as the newline separator on this
     * platform is "\r\n", which (unbelievably) is serialized to "\r\r\n" as a "feature"!  Of course, when this is
     * read back in, it is converted to "\n\n", meaning that the text was converted from single to double spaced.
     * To guard against this, all instances of "\r\n" and "\r" are converted to "\n".
     * @param stringToConvert the string to convert.
     * @return the converted string.
     */
    private static String convertNewLines(String stringToConvert) {
        return stringToConvert.replaceAll("\r\n", "\n").replaceAll("\r", "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }
    
    /**
     * Creates a <code>CDATASection</code> node whose value is the specified string.  This is different from 
     * Document.createCDATASection() in that this method first converts all "\r\n" and "\r" to "\n" to guard 
     * against XML "features" that transform "\r\n" to "\r\r\n" on output.
     * @param document
     * @param data The data for the <code>CDATASection</code> contents.
     * @return The new <code>CDATASection</code> object.
     * @exception org.w3c.dom.DOMException
     *   NOT_SUPPORTED_ERR: Raised if this document is an HTML document.
     */
    public static CDATASection createCDATASection(Document document, String data) {
        String convertedData = convertNewLines(data);
        return document.createCDATASection(convertedData);
    }

    /**
     * Get the text from adjacent CharacterData nodes.  This will take the text from the passed in node, 
     * and merge it with the text from the next siblings until it reaches a node which is not CharacterData.
     * 
     * Note that this is necessary during loading, as the result of saving a CDATA node may in fact 
     * be more than one CDATA node in the output.  This will happen because any time the substring "]]>" 
     * (the substring to end a CDATA node) appears while outputting the CDATA text, the node transformer 
     * will end the current node at "]]" and start a new one CDATA node to avoid screwing up the output.
     * 
     * Note: CDATA extends CharacterData (so CDATA != CharacterData)
     * 
     * @param firstNode the first of [0..n] CharacterData nodes whose text to merge and return.
     * @param resultBuffer the buffer into which to add the result of merging the CharacterData text.
     * Warning: anything in this buffer will be overwritten!
     * @return the first sibling node which is not a CharacterData node, or null is there isn't any.
     */
    public static Node getAdjacentCharacterData(Node firstNode, StringBuilder resultBuffer) {
        // Clear the result buffer
        resultBuffer.setLength(0);

        // merge adjacent CDATA nodes
        Node codeNode = firstNode;
        while (codeNode != null && codeNode instanceof CharacterData) {
            resultBuffer.append(((CharacterData)codeNode).getData());
            codeNode = codeNode.getNextSibling();
        }
        return codeNode;
    }
    
    /**
     * Method setIntegerAttribute
     * 
     * @param element
     * @param attributeName
     * @param value
     */
    public static void setIntegerAttribute (Element element, String attributeName, int value) {
        element.setAttribute(attributeName, Integer.toString(value));
    }

    
    /**
     * Get the Integer representation of an attribute value.
     * Throw an appropriate throwable if the string doesn't have a good integer representation.
     * @param element Element the element whose attribute to convert to an Integer value
     * @param attributeName String the name of the attribute whose value represents an Integer.
     * @return Integer the Integer representation
     * @throws BadXMLDocumentException
     */
    public static Integer getIntegerAttribute(Element element, String attributeName) throws BadXMLDocumentException {

        String intString = element.getAttribute(attributeName);
        try {
            Integer result = new Integer(intString);
            return result;
        } catch (NumberFormatException nfe) {
            handleBadDocument(element, messages.getString("IntExpectedForAttr", attributeName, intString)); //$NON-NLS-1$
        }
        
        return null;
    }

    /**
     * Get the Integer representation of an attribute value.
     * Throw an appropriate throwable if the string doesn't have a good integer representation.
     * @param element Element the element whose attribute to convert to an Integer value
     * @param attributeName String the name of the attribute whose value represents an Integer.
     * @param defaultValue  the default value to be returned if the attribute is missing
     * @return the integer representation of the attribute value, or the default value
     * @throws BadXMLDocumentException
     */
    public static int getIntegerAttributeWithDefault(Element element, String attributeName, int defaultValue) throws BadXMLDocumentException {
        String intString = element.getAttribute(attributeName);
        if (intString == null || intString.length() == 0) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(intString);
        }
        catch (NumberFormatException nfe) {
            handleBadDocument(element, messages.getString("IntExpectedForAttr", attributeName, intString)); //$NON-NLS-1$
            return defaultValue;
        }
    }

    /**
     * Adds an attribute with the list of integer values encoded as a string.
     * @param element        the element to which the attribute will be added
     * @param attributeName  the name of the attribute
     * @param intValues      the list of Integer values to set
     */
    public static void setIntegerListAttribute(Element element,
                                               String attributeName,
                                               List<Integer> intValues) {
        StringBuilder sb = new StringBuilder();
        for (Integer intValue : intValues) {
            if (sb.length() == 0) {
                sb.append(" "); //$NON-NLS-1$
            }
            sb.append(intValue);
        }

        element.setAttribute(attributeName, sb.toString());
    }

    /**
     * Extracts a list of integer values from the specified attribute.
     * @param element        the element to fetch the integer list from
     * @param attributeName  the name of the attribute containing the integer list
     * @return a list of integer values from the specified attribute
     */
    public static List<Integer> getIntegerListAttribute(Element element, String attributeName) {
        String intListString = element.getAttribute(attributeName);
        if (intListString == null || intListString.length() == 0) {
            return Collections.emptyList();
        }

        String [] intStrings = intListString.split (" "); //$NON-NLS-1$

        List<Integer> intValues = new ArrayList<Integer>(intStrings.length);
        for (String intString : intStrings) { 
            Integer intValue = Integer.valueOf(intString);
            intValues.add(intValue);
        }
        return intValues;
    }
    
    /**
     * Method setDoubleAttribute
     * 
     * @param element
     * @param attributeName
     * @param value
     */
    public static void setDoubleAttribute (Element element, String attributeName, double value) {
        element.setAttribute (attributeName, Double.toString(value));
    }

    /**
     * Method getDoubleAttribute
     * 
     * @param element
     * @param attributeName
     * @return The Double representation of an attribute value
     * @throws BadXMLDocumentException
     */
    public static Double getDoubleAttribute(Element element, String attributeName) throws BadXMLDocumentException {

        String doubleString = (element).getAttribute(attributeName);
        try {
            Double result = new Double(doubleString);
            return result;
        } catch (NumberFormatException nfe) {
            handleBadDocument(element, messages.getString("DoubleExpectedForAttr", attributeName, doubleString)); //$NON-NLS-1$
        }
        
        return null;

    }

    /**
     * Get the double representation of an attribute value.
     * Throw an appropriate throwable if the string doesn't have a good double representation.
     * @param element Element the element whose attribute to convert to an double value
     * @param attributeName String the name of the attribute whose value represents an double.
     * @param defaultValue  the default value to be returned if the attribute is missing
     * @return the double representation of the value, or the default value
     * @throws BadXMLDocumentException
     */
    public static double getDoubleAttributeWithDefault(Element element, String attributeName, double defaultValue) throws BadXMLDocumentException {
        String doubleString = element.getAttribute(attributeName);
        if (doubleString == null || doubleString.length() == 0) {
            return defaultValue;
        }

        try {
            return Double.parseDouble(doubleString);
        }
        catch (NumberFormatException nfe) {
            handleBadDocument(element, messages.getString("DoubleExpectedForAttr", attributeName, doubleString)); //$NON-NLS-1$
            return defaultValue;
        }
    }

    /**
     * Add a Boolean attribute using the boolean representation of 'true' and 'false' as defined by this class.  
     * @param element Element the element whose attribute to convert to a boolean value
     * @param attributeName String the name of the attribute whose value represents a boolean.
     * @param value Boolean the boolean value
     */
    public static void setBooleanAttribute(Element element, String attributeName, boolean value) {
        if (value)
            element.setAttribute(attributeName, XMLPersistenceConstants.TRUE_STRING);
        else
            element.setAttribute(attributeName, XMLPersistenceConstants.FALSE_STRING);
    }

    /**
     * Get a Boolean representation of an attribute value.  
     * Throw an appropriate throwable if the string doesn't have a good boolean representation.
     * @param element Element the element whose attribute to convert to a boolean value
     * @param attributeName String the name of the attribute whose value represents a boolean.
     * @return boolean the boolean representation.
     * @throws BadXMLDocumentException
     */
    public static boolean getBooleanAttribute(Element element, String attributeName) throws BadXMLDocumentException {

        final String booleanString = element.getAttribute(attributeName);
        final String errorMessage; //if we can't parse the String
        if (booleanString != null) {
            if (booleanString.equalsIgnoreCase(XMLPersistenceConstants.TRUE_STRING)) {
                return true;
            } else if (booleanString.equalsIgnoreCase(XMLPersistenceConstants.FALSE_STRING)) {
                return false;
            }
            errorMessage = messages.getString("CouldNotParseBool", booleanString); //$NON-NLS-1$
        } else {
            errorMessage = messages.getString("NoBoolVal"); //$NON-NLS-1$
            
        }
        
        handleBadDocument(element, errorMessage);

        return false;
    }

    /**
     * Get a Boolean representation of an attribute value.  If the value doesn't exist (or can't be
     * parsed) then return the default value.  
     * @param element Element the element whose attribute to convert to a boolean value
     * @param attributeName String the name of the attribute whose value represents a boolean.
     * @param defaultValue Boolean the default value if the attribute isn't found
     * @return boolean the boolean representation.
     */
    public static boolean getBooleanAttributeWithDefault(Element element,
                                                         String attributeName,
                                                         boolean defaultValue) {
        // If the attribute doesn't exist then return the default value.
        if (!element.hasAttribute(attributeName))
            return defaultValue;
        
        String booleanString = element.getAttribute(attributeName);
        if (booleanString.equalsIgnoreCase(XMLPersistenceConstants.TRUE_STRING)) {
            return true;
        } else if (booleanString.equalsIgnoreCase(XMLPersistenceConstants.FALSE_STRING)) {
            return false;
        }

        // Couldn't parse the string, return the default value.
        return defaultValue;
    }

    /**
     * Adds a new element containing a CDATA section to the parent element
     * @param parent the parent element to add the new element to
     * @param tag the tag name of the new element
     * @param text the text of the CDATA section (if text is null or empty no CDATA section will be added).
     */
    public static void addTextElement(Element parent, String tag, String text) {
        addTextElementNS(parent, null, tag, text);
    }
    
    /**
     * Adds a new element containing a CDATA section to the parent element
     * @param parent the parent element to add the new element to
     * @param namespaceURI the namespace of the added element's tag name, or null if there isn't any..
     * @param tag the tag name of the new element
     * @param text the text of the CDATA section (if text is null or empty no CDATA section will be added).
     */
    public static void addTextElementNS(Element parent, String namespaceURI, String tag, String text) {

        // Add an element with the given tag name.
        Document document = parent.getOwnerDocument();
        Element textElement = namespaceURI != null ? document.createElementNS(namespaceURI, tag) : document.createElement(tag);
        parent.appendChild(textElement);

        if (text != null && text.length() > 0) {
            CDATASection cdata = createCDATASection(document, text);
            textElement.appendChild(cdata);
        }
    }
    
    /**
     * Adds a new element containing a CDATA section with the specified date to the parent element.
     * @param parent the parent element to add the new element to
     * @param tag the tag name of the new element
     * @param date the date to use for the CDATA section (if date is null no CDATA section will be added).
     */
    public static void addDateElement(Element parent, String tag, Date date) {
        addDateElementNS(parent, null, tag, date);
    }
    
    /**
     * Adds a new element containing a CDATA section with the specified date to the parent element.
     * @param parent the parent element to add the new element to
     * @param namespaceURI the namespace of the added element's tag name, or null if there isn't any..
     * @param tag the tag name of the new element
     * @param date the date to use for the CDATA section (if date is null no CDATA section will be added).
     */
    public static void addDateElementNS(Element parent, String namespaceURI, String tag, Date date) {
        
        String dateText = null;
        
        if (date != null) {
            dateText = XMLPersistenceConstants.formatDateAsISO8601(date);
        }

        addTextElementNS(parent, namespaceURI, tag, dateText);
    }
    
    /**
     * Adds a new element containing a CDATA section with the specified boolean value to the parent element.
     * @param parent the parent element to add the new element to
     * @param tag the tag name of the new element
     * @param value the boolean value to store in the CDATA section of the new element
     */
    public static void addBooleanElement(Element parent, String tag, boolean value) {
        addBooleanElementNS(parent, null, tag, value);
    }
    
    /**
     * Adds a new element containing a CDATA section with the specified boolean value to the parent element.
     * @param parent the parent element to add the new element to
     * @param namespaceURI the namespace of the added element's tag name, or null if there isn't any..
     * @param tag the tag name of the new element
     * @param value the boolean value to store in the CDATA section of the new element
     */
    public static void addBooleanElementNS(Element parent, String namespaceURI, String tag, boolean value) {
        
        String valueText = (value) ? XMLPersistenceConstants.TRUE_STRING : XMLPersistenceConstants.FALSE_STRING;
                                   
        addTextElementNS(parent, namespaceURI, tag, valueText);
    }
    
    /**
     * Adds a new element containing a CDATA section with the specified value to the parent element.
     * @param parent the parent element to add the new element to
     * @param tag the tag name of the new element
     * @param value the value to store in the CDATA section of the new element
     */
    public static void addIntegerElement(Element parent, String tag, int value) {
        addIntegerElementNS(parent, null, tag, value);
    }
    
    /**
     * Adds a new element containing a CDATA section with the specified value to the parent element.
     * @param parent the parent element to add the new element to
     * @param namespaceURI the namespace of the added element's tag name, or null if there isn't any..
     * @param tag the tag name of the new element
     * @param value the value to store in the CDATA section of the new element
     */
    public static void addIntegerElementNS(Element parent, String namespaceURI, String tag, int value) {
        addTextElementNS(parent, namespaceURI, tag, Integer.toString(value));
    }
    
    /**
     * Adds a new element containing a CDATA section with the specified value to the parent element.
     * @param parent the parent element to add the new element to
     * @param namespaceURI the namespace of the added element's tag name, or null if there isn't any..
     * @param tag the tag name of the new element
     * @param value the value to store in the CDATA section of the new element
     */
    public static void addLongElementNS(Element parent, String namespaceURI, String tag, long value) {
        addTextElementNS(parent, namespaceURI, tag, Long.toString(value));
    }
    
    /**
     * @param element the element whose String value to get
     * @return the String value of the first child of the specific element
     * @throws BadXMLDocumentException
     */    
    public static String getElementStringValue(Node element) throws BadXMLDocumentException {
    
        checkIsElement(element);
        
        if (element.hasChildNodes()) {
            StringBuilder elementText = new StringBuilder();
            Node elementValueNode = element.getFirstChild();
            getAdjacentCharacterData(elementValueNode, elementText);
            return elementText.toString();
        }
        
        return null;
    }
    
    /**
     * @param element the element whose boolean value to get
     * @return the boolean value of the first child of the specified element. The return value is
     * true iff the child's text value equals TRUE_STRING (ignoring case). Otherwise this returns false.
     * 
     * @throws BadXMLDocumentException
     */
    public static boolean getElementBooleanValue(Node element) throws BadXMLDocumentException {
        
        String stringValue = getElementStringValue(element);
        
        if (stringValue == null) {
            return false;
        }
        
        if (stringValue.equalsIgnoreCase(XMLPersistenceConstants.TRUE_STRING)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * @param element the element whose Date value to get
     * @return the Date value of the first child of the specified element. Null if there is no valid
     * Date string stored by the first child.
     * 
     */
    public static Date getElementDateValue(Node element) throws BadXMLDocumentException {

        String stringValue = getElementStringValue(element);

        if (stringValue == null) {
            return null;
        }

        try {
            return XMLPersistenceConstants.unformatDateFromISO8601(stringValue);

        } catch (ParseException ex) {
            // This means the string value does not represent a valid date.
        }
        
        return null;
    }

    /**
     * Get the Integer representation of an attribute value.
     * Throw an appropriate throwable if the string doesn't have a good integer representation.
     * @param element the element whose Integer value to get.
     * @return the Integer value of the first child of the specified element. Null if there is no valid
     * Integer stored by the first child.
     * @throws BadXMLDocumentException
     */
    public static Integer getElementIntegerValue(Element element) throws BadXMLDocumentException {

        String intString = getElementStringValue(element);

        if (intString == null) {
            return null;
        }

        try {
            return new Integer(intString);

        } catch (NumberFormatException nfe) {
            // The string value does not represent a valid integer.
        }
        
        return null;
    }

    /**
     * Get the Long representation of an attribute value.
     * Throw an appropriate throwable if the string doesn't have a good long representation.
     * 
     * @param element the element whose value to get.
     * @return the value of the first child of the specified element. 
     *   Null if there is no valid Long value stored by the first child.
     * @throws BadXMLDocumentException
     */
    public static Long getElementLongValue(Element element) throws BadXMLDocumentException {

        String longString = getElementStringValue(element);

        if (longString == null) {
            return null;
        }

        try {
            return new Long(longString);

        } catch (NumberFormatException nfe) {
            // The string value does not represent a valid long value.
        }
        
        return null;
    }

    /**
     * Handle an error in disassembling an XML document.
     * @param errorNode Node the node being processed when the error occurred.
     * @param errorMessage String the error message associated with the error.
     * @throws BadXMLDocumentException
     */
    public static void handleBadDocument(Node errorNode, String errorMessage) throws BadXMLDocumentException {
        throw new BadXMLDocumentException(errorNode, errorMessage);
    }

    /**
     * Returns the element name of the given element.
     * @param element
     * @return String
     */
    public static String getElementName(Element element) {
        // When loading from disk the local name will be setup correctly, but if the local name
        // is fetched directly after calling Document.createElement() then the value will be null.
        // See the JavaDoc for more info.  To workaround this, use the complete node name.
        String elemName = element.getLocalName ();
        if (elemName == null) {
            elemName = element.getNodeName();
        }
        return elemName;
    }
    
    /**
     * Returns the first child element, or null if none exists.
     * @param element
     * @return Element
     */
    public static Element getFirstChildElement(Element element) {
        for (Node node = element.getFirstChild (); node != null; node = node.getNextSibling ()) {
            if (node instanceof Element) {
                return (Element)node;
            }
        }
        
        return null;
    }    
    
    /**
     * Returns the first child element with the specified name, or null if none exists.
     * @param element
     * @param elementName
     * @return Element
     */
    public static Element getChildElement (Element element, String elementName) {
        for (Node node = element.getFirstChild (); node != null; node = node.getNextSibling ()) {
            if (node instanceof Element) {
                Element childElem = (Element) node;
                String elemName = getElementName(childElem);

                if (elementName.equals (elemName))
                    return childElem;
            }
        }

        return null;
    }

    /**
     * @param element
     * @return all child elements of the specified node.
     */
    public static List<Element> getChildElements (Node element) {
        List<Element> childElems = new ArrayList<Element>();

        for (Node node = element.getFirstChild (); node != null; node = node.getNextSibling ()) {
            if (node instanceof Element) {
                childElems.add((Element)node);
            }
        }

        return childElems;
    }

    /**
     * Returns all child elements of the specified node with the specified name.
     * @param element
     * @param elementName
     * @return the list of child elements
     */
    public static List<Element> getChildElements (Node element, String elementName) {
        List<Element> childElems = new ArrayList<Element>();

        for (Node node = element.getFirstChild (); node != null; node = node.getNextSibling ()) {
            if (node instanceof Element) {
                Element childElem = (Element)node;
                String elemName = getElementName(childElem);
                
                if (elementName.equals (elemName))
                    childElems.add (childElem);
            }
        }

        return childElems;
    }

    /**
     * Returns the child text of this element, or null if there is none.
     * @param element
     * @return String
     */
    public static String getChildText (Element element) {
        return getChildText(element, null);
    }

    /**
     * Returns the child text of this element, or the default value if there is none.
     * @param element
     * @param defaultValue
     * @return Either the child text of the element or the default value if there is not child text.
     */
    public static String getChildText (Element element, String defaultValue) {
        Node childNode = element.getFirstChild ();
        if (childNode == null || !(childNode instanceof Text))
            return defaultValue;

        return childNode.getNodeValue ();
    }

    
    /**
     * A convenience method for return a string from an element.  First, this
     * method checks to see if the the specified name exists as an attribute
     * in the element.  If it does, simply returns the attribute value.
     * Then this method checks to see if the specified element has a child
     * element that matches the given name.  If it does, attempts to read
     * the text from the child node.  Otherwise, returns <code>null</code>.
     * @param element
     * @param name
     * @return String
     */
    public static String getAttributeOrChildText(Element element, String name) {
        if (element.hasAttribute(name)) {
            return element.getAttribute(name);
        }
        Element childElem = getChildElement(element, name);
        return childElem == null ? null : getChildText(childElem);
    }

    /**
     * Attach namespace and optionally schema info to the given document.
     * @param document the document to which to attach the info.
     * @param documentNSInfo the namespace info for the document.
     * @param schemaLocation the schema location.  If null, no schema info will be attached.
     * @param schemaLocationNS the namespace URI for the schema location.  
     * If null, and the schema location is non-null, the schema location will not have a namespace.
     */
    public static void attachNamespaceAndSchema(Document document, NamespaceInfo documentNSInfo, String schemaLocation, String schemaLocationNS) {
        Element rootElement = document.getDocumentElement();
        attachNamespaceAndSchema(rootElement, documentNSInfo, schemaLocation, schemaLocationNS);
    }
    
    /**
     * Attach namespace and optionally schema info to the given element.
     * @param rootElement the element to which to attach the info.
     * @param documentNSInfo the namespace info for the document.
     * @param schemaLocation the schema location.  If null, no schema info will be attached.
     * @param schemaLocationNS the namespace URI for the schema location.  
     * If null, and the schema location is non-null, the schema location will not have a namespace.
     */
    public static void attachNamespaceAndSchema(Element rootElement, NamespaceInfo documentNSInfo, String schemaLocation, String schemaLocationNS) {
    
        Document document = rootElement.getOwnerDocument();
        String documentNS = documentNSInfo.getNamespaceURI();
        String documentNSPrefix = documentNSInfo.getNamespacePrefix();
        String qualifiedName = XMLPersistenceConstants.XML_NS_PREFIX + ":" + documentNSPrefix; //$NON-NLS-1$

        Attr nsAttr = document.createAttributeNS(XMLPersistenceConstants.XML_NS, qualifiedName);
        nsAttr.setPrefix(XMLPersistenceConstants.XML_NS_PREFIX);
        nsAttr.setValue(documentNS);
        rootElement.setAttributeNode(nsAttr);

        // Attach schema information if available
        if (schemaLocation != null) {
    
            // Specify the schema instance namespace: 
            //    "xmlns:xsi=http://www.w3.org/2001/XMLSchema-instance"
            qualifiedName = XMLPersistenceConstants.XML_NS_PREFIX + ":" + XMLPersistenceConstants.XSI_NS_PREFIX; //$NON-NLS-1$
            Attr xsiAttr = document.createAttributeNS(XMLPersistenceConstants.XML_NS, qualifiedName);
            xsiAttr.setPrefix(XMLPersistenceConstants.XML_NS_PREFIX);
            xsiAttr.setValue(XMLPersistenceConstants.XSI_NS);
            rootElement.setAttributeNode(xsiAttr);
    
            Attr schemaLocationAttr;
            if (schemaLocationNS == null) {
    
                // Specify the schema location: "xsi:noNameSpaceSchemaLocation=..."
                schemaLocationAttr = document.createAttributeNS(XMLPersistenceConstants.XSI_NS, "noNamespaceSchemaLocation"); //$NON-NLS-1$
                schemaLocationAttr.setValue(schemaLocation);
    
            } else {
    
                // Specify the namespace attribute:
                //    "xmlns=http://www.businessobjects.com/cal/metadata"
                rootElement.setAttribute(XMLPersistenceConstants.XML_NS_PREFIX, schemaLocationNS);
    
                // Specify the schema location: "xsi:schemaLocation=..."
                schemaLocationAttr = document.createAttributeNS(XMLPersistenceConstants.XSI_NS, "schemaLocation"); //$NON-NLS-1$
                schemaLocationAttr.setValue(schemaLocationNS + " " + schemaLocation); //$NON-NLS-1$
            }
    
            schemaLocationAttr.setPrefix(XMLPersistenceConstants.XSI_NS_PREFIX);
            rootElement.setAttributeNode(schemaLocationAttr);
        }
    }

}

