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
 * MonitorDocument.java
 * Created: 15-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.NamespaceInfo;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.openquark.util.xml.XMLPersistenceHelper.DocumentConstructionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * The monitor document represents the complete set of job descriptions 
 * in the application. It supports load and saving.
 */
public class MonitorDocument {

    public static interface MonitorDocumentListener {

        void jobDescriptionAdded (MonitorJobDescription jobDescription);

        void jobDescriptionRemoved (MonitorJobDescription jobDescription);

    }
    
    /** Namespace URI reserved for BAM. */
    static final String BAM_NS =          "http://www.businessobjects.com/bam";
    static final String BAM_NS_PREFIX =   "bam";


    private final List<MonitorJobDescription> jobDescriptions = new ArrayList<MonitorJobDescription> (); // of MonitorJobDescriptions

    private final List<MonitorDocumentListener> listeners = new ArrayList<MonitorDocumentListener> ();

    private String pathname = "";
    

    //
    // Job description management
    //

    public int getJobDescriptionCount () {
        return jobDescriptions.size ();
    }

    public MonitorJobDescription getNthJobDescription (int n) {
        return jobDescriptions.get (n);
    }

    public void addJobDescription (MonitorJobDescription jobDescription) {
        jobDescriptions.add (jobDescription);

        fireJobDescriptionAdded (jobDescription);
    }

    public void removeJobDescription (MonitorJobDescription jobDescription) {
        jobDescriptions.remove (jobDescription);

        fireJobDescriptionRemoved (jobDescription);
    }
    
    
    //
    // Listener management
    //

    public void addDocumentListener (MonitorDocumentListener listener) {
        listeners.add (listener);
    }

    public void removeDocumentListener (MonitorDocumentListener listener) {
        listeners.remove (listener);
    }

    private void fireJobDescriptionAdded (MonitorJobDescription jobDescription) {
        List<MonitorDocumentListener> tempList = new ArrayList<MonitorDocumentListener> (listeners);

        for (final MonitorDocumentListener listener : tempList) {
            listener.jobDescriptionAdded (jobDescription);
        }
    }

    private void fireJobDescriptionRemoved (MonitorJobDescription jobDescription) {
        List<MonitorDocumentListener> tempList = new ArrayList<MonitorDocumentListener> (listeners);

        for (final MonitorDocumentListener listener : tempList) {
            listener.jobDescriptionRemoved (jobDescription);
        }
    }
    
    /**
     * Method setPathname
     * 
     * @param pathname
     */
    private void setPathname (String pathname) {
        this.pathname = pathname;
    }
    
    /**
     * @return Returns the pathname.
     */
    public String getPathname () {
        return pathname;
    }

    
    //
    // Serialisation
    //

    /**
     * Method save
     * 
     * @param file
     */
    public void save (File file) {
        try {
            // Save the output to the specified file.
            // TODO: first save to a temp file and then replace the original.
            OutputStream outputStream = new FileOutputStream (file);
            outputStream = new BufferedOutputStream (outputStream);

            save (outputStream);

            outputStream.close ();
            
            pathname = file.getPath();
        }
        catch (Exception e) {
            System.out.println ("Failed to save monitor document."); //$NON-NLS-1$
            e.printStackTrace ();
        }
    }


    /**
     * Method save
     * 
     * @param outputStream
     */
    private void save (OutputStream outputStream) {
        Document document = XMLPersistenceHelper.getEmptyDocument ();
        save (document);

        XMLPersistenceHelper.documentToXML (document, outputStream, true);
    }

    /**
     * Method save
     * 
     * @param document
     */
    private void save (Document document) {
        // Create a root element.
        Element rootElement = document.createElement (MonitorSaveConstants.BAM);
        document.appendChild (rootElement);

        declareNamespace (rootElement);

        // Save the workbook definition.
        store (rootElement);
    }

    /**
     * Attach an attribute declaring the gem namespace: "xmlns:gem=..."
     * Creation date: (Jun 12, 2002 11:22:20 AM)
     * @param element Element the element to which to attach the namespace declaration.
     */
    private static void declareNamespace(Element element) {
        NamespaceInfo namespaceInfo = new NamespaceInfo (BAM_NS, BAM_NS_PREFIX);
        
        XMLPersistenceHelper.attachNamespaceAndSchema(element, namespaceInfo, null, null);
    }


    /**
     * Method store
     * 
     * @param parentElement
     */
    private void store (Element parentElement) {
        Document document = parentElement.getOwnerDocument ();

        // Construct a new element for the document.
        Element monitorDocElem = document.createElement (MonitorSaveConstants.MonitorDocument);
        parentElement.appendChild (monitorDocElem);

        storeJobDescriptions (monitorDocElem);
    }

    /**
     * Method storeJobDescriptions
     * 
     * @param parentElement
     */
    private void storeJobDescriptions (Element parentElement) {
        Document document = parentElement.getOwnerDocument ();

        // Construct a new element for the job descriptions.
        Element jobDescriptionsElem = document.createElement (MonitorSaveConstants.JobDescriptions);
        parentElement.appendChild (jobDescriptionsElem);

        for (final MonitorJobDescription jobDescription : jobDescriptions) {
            jobDescription.store (jobDescriptionsElem);
        }
    }

    /**
     * Method Load
     * 
     * @param file
     * @return Returns a MonitorDocument, if it can be loaded from the File, or null
     */
    public static MonitorDocument Load (File file) {
        try {
            // Load the workbook from the specified file.
            InputStream inputStream = new FileInputStream (file);
            inputStream = new BufferedInputStream (inputStream);

            MonitorDocument result;

            try {
                result = Load (inputStream);
            } finally {
                inputStream.close ();
            }
            
            if (result != null) {
                result.setPathname (file.getPath());
            }
                
            return result;
        }
        catch (Exception e) {
            System.out.println ("Failed to load monitor document.");
            e.printStackTrace ();
        }
        
        return null;
    }

    /**
     * Method Load
     * 
     * @param inputStream
     * @return Returns a MonitorDocument loaded from the given InputStream
     */
    private static MonitorDocument Load (InputStream inputStream) throws DocumentConstructionException, InvalidFileFormat, BadXMLDocumentException {
        // Create a DOM document from the XML data.
        final Document document = XMLPersistenceHelper.documentFromXML (inputStream);

        // Fetch the root element from the document.
        Element rootElement = document.getDocumentElement ();

        if (rootElement == null) {
            throw new InvalidFileFormat ("Root element missing");
        }
        
        if (!rootElement.getTagName().equals(MonitorSaveConstants.BAM)) {
            throw new InvalidFileFormat ("Incorrect root element");
        }

        // Load the monitor document.
        Element monitorDocElem = XMLPersistenceHelper.getChildElement (rootElement, MonitorSaveConstants.MonitorDocument);

        if (monitorDocElem == null) {
            throw new InvalidFileFormat ("MonitorDocument element missing");
        }

        return Load (monitorDocElem);
    }

    /**
     * Method Load
     * 
     * @param monitorDocElem
     * @return Returns a MonitorDocument loaded from the given XML element
     */
    private static MonitorDocument Load (Element monitorDocElem) throws InvalidFileFormat, BadXMLDocumentException {
        MonitorDocument document = new MonitorDocument ();
        
        document.load (monitorDocElem);
        
        return document;
    }

    /**
     * Method load
     * 
     * @param monitorDocElem
     */
    private void load (Element monitorDocElem) throws InvalidFileFormat, BadXMLDocumentException {
        Element jobDefinitionsElem = XMLPersistenceHelper.getChildElement(monitorDocElem, MonitorSaveConstants.JobDescriptions);
        
        if (jobDefinitionsElem == null) {
            throw new InvalidFileFormat ("JobDescriptions element missing");
        }
        
        List<Element> jobDefinitions = XMLPersistenceHelper.getChildElements(jobDefinitionsElem);
        
        for (final Element element : jobDefinitions) {
            MonitorJobDescription jobDescription = MonitorJobDescription.Load (element);
            
            if (jobDescription != null) {
                addJobDescription(jobDescription);
            }
        }
    }

}
