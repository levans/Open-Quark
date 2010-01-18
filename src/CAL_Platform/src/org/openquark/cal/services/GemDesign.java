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
 * GemDesign.java
 * Creation date: Jul 16, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.openquark.util.xml.XMLPersistenceHelper.DocumentConstructionException;
import org.w3c.dom.Document;


/**
 * A GemDesign represents a client design for a GemEntity.
 * @author Edward Lam
 */
public class GemDesign {
    
    /** The qualified name of the gem entity represented in the design. */
    private final QualifiedName designName;

    /** The XML document representing the design. */
    private final Document designDocument;
    
    /**
     * Constructor for a GemDesign.
     * @param designName The qualified name of the gem entity represented in the design.
     * @param designDocument the XML document representing the design.
     */
    GemDesign(QualifiedName designName, Document designDocument) {
        if (designName == null || designDocument == null) {
            throw new NullPointerException("A GemDesign must have a name and a design document.");
        }
        
        this.designName = designName;
        this.designDocument = designDocument;
    }
    
    /**
     * @return the name of the design.
     */
    public QualifiedName getDesignName() {
        return designName;
    }

    /**
     * @return the XML document representing the design.
     */
    public Document getDesignDocument() {
        return designDocument;
    }

    /**
     * Loads a gem design from the given file.
     * @param designFile the file describing the design.
     * @param loadStatus a Status object for storing the status of the load operation
     * @return The GemDesign object loaded from the given file
     */
    public static GemDesign loadGemDesign(File designFile, Status loadStatus) {
    
        QualifiedName gemName = getDesignName(designFile);
        if (gemName == null) {
            loadStatus.add(new Status(Status.Severity.ERROR, "Cannot determine the name of the gem described by the design.", null));
        }
        
        try {
            InputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(designFile));
    
            Document designDocument = XMLPersistenceHelper.documentFromXML(bufferedInputStream);
    
            return new GemDesign(gemName, designDocument);

        } catch (FileNotFoundException ex) {
            loadStatus.add(new Status(Status.Severity.ERROR, "The design file could not be found.", ex));
        
        } catch (DocumentConstructionException ex) {
            loadStatus.add(new Status(Status.Severity.ERROR, "The design file XML is invalid.", ex));
            
        } catch (Exception ex) {
            loadStatus.add(new Status(Status.Severity.ERROR, "Exception while loading design.", ex));
        }
        
        return null;
    }
    
    /**
     * Loads a gem design from the given definition.
     * @param gemDefinition the definition from which to load the design
     * @param loadStatus a Status object for storing the status of the load operation
     */
    public static GemDesign loadGemDesign(WorkspaceResource gemDefinition, Status loadStatus) {
        InputStream inputStream = null;
        try {
            // Get the name of the gem.
            QualifiedName gemName = QualifiedName.makeFromCompoundName(gemDefinition.getIdentifier().getFeatureName().getName());
            
            // Get the input stream on the gem definition.
            inputStream = gemDefinition.getInputStream(loadStatus);
            if (inputStream == null) {
                return null;
            }
            inputStream = new BufferedInputStream(inputStream);
            
            // Create and return the gem design.
            Document designDocument = XMLPersistenceHelper.documentFromXML(inputStream);
            
            return new GemDesign(gemName, designDocument);
        
        } catch (DocumentConstructionException ex) {
            loadStatus.add(new Status(Status.Severity.ERROR, "The design file XML is invalid.", ex));
            return null;
        
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Extract the QualifiedName of a design from the file name of the design file.
     * @param file the file to get the design name for
     * @return the QualifiedName of the design or null if the given file is not a valid design file.
     */
    private static QualifiedName getDesignName(File file) {
        String nameWithoutHyphens =
                FileSystemResourceHelper.stripFileExtension(file.getName(), GemDesignPathMapper.INSTANCE.getFileExtension(), true);
        
        if (QualifiedName.isValidCompoundName(nameWithoutHyphens)) {
            return QualifiedName.makeFromCompoundName(nameWithoutHyphens);
        }
        
        return null;
    }

}