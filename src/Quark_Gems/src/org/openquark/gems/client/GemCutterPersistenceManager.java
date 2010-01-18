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
 * GemCutterPersistenceManager.java
 * Creation date: Nov 7, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client;

import java.util.Set;

import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.CALSourceGenerator;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.GemDesign;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.Status;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.NamespaceInfo;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This class takes care of loading and saving GemCutter gem designs and definitions.
 * @author Frank Worsley
 */
public class GemCutterPersistenceManager {

    /** The GemCutter this manager is for. */
    private final GemCutter gemCutter;
    
    /**
     * Constructor for a new GemCutterPersistenceManager.
     * @param gemCutter the GemCutter this manager is for
     */
    GemCutterPersistenceManager(GemCutter gemCutter) {
        
        if (gemCutter == null) {
            throw new NullPointerException();
        }
        
        this.gemCutter = gemCutter;
    }

    /**
     * Attach the saved form of this object as a child XML node.
     * @param parentNode the node that will be the parent of the generated XML.  
     *   The generated XML will be appended as a subtree of this node.  
     *   Note: parentNode must be a node type that can accept children (eg. an Element or a DocumentFragment)
     */
    private void saveXML(Node parentNode) {
        
        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the GemCutter element, with the appropriate default namespace
        Element resultElement = document.createElementNS(GemPersistenceConstants.GEMS_NS, GemPersistenceConstants.GEMCUTTER_TAG);
        parentNode.appendChild(resultElement);

        // Declare gem namespace and schema.
        NamespaceInfo gemNSInfo = Gem.getNamespaceInfo();
        XMLPersistenceHelper.attachNamespaceAndSchema(document, gemNSInfo, GemPersistenceConstants.GEMS_SCHEMA_LOCATION, GemPersistenceConstants.GEMS_NS);

        // Add info for the tabletop
        gemCutter.getTableTop().saveXML(resultElement);

        // Now add GemCutter-specific info
        // For now, there isn't any.
    }

    /**
     * Restore the contents of this object from an element.
     * @param element the element at the head of the subtree which represents the object to reconstruct.
     * @param loadStatus the ongoing status of the load.
     */
    private void loadXML(Element element, Status loadStatus){

        try {
            XMLPersistenceHelper.checkTag(element, GemPersistenceConstants.GEMCUTTER_TAG);
            
            Node tableTopNode = element.getFirstChild();
            XMLPersistenceHelper.checkIsElement(tableTopNode);
    
            // Just call the tabletop to restore itself.
            gemCutter.getTableTop().loadXML((Element) tableTopNode, gemCutter.getPerspective(), loadStatus);
            
            // TODOEL: write a schema and validate against it

        } catch (BadXMLDocumentException bxde) {
            loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_GCLoadFailure"), bxde));
        }
    }

    /**
     * Save a specific Gem.
     * @param gemToSave the Gem to save
     * @param scope the visibility of the gem to save
     * @return the status of the save
     */
    Status saveGem(CollectorGem gemToSave, Scope scope) {

        if (gemToSave == null) {
            throw new NullPointerException();
        }
        
        // TODO
        // Right now this either replaces the gem text if the gem is defined and the appropriate markers 
        // can be found, or fails. If save succeeds, the design view is also written out to an XML file.

        // Determine the name of the gem being saved
        QualifiedName gemName = QualifiedName.make(gemCutter.getWorkingModuleName(), gemToSave.getUnqualifiedName());

        // Get the gem definition.
        String gemDefinition = getGemDefinition(gemToSave, scope);
        
        // Get the gem metadata as functional agent metadata.
        CALFeatureName gemEntityFeatureName = CALFeatureName.getFunctionFeatureName(gemName);

        FunctionMetadata collectorMetadata = gemToSave.getDesignMetadata();
        FunctionMetadata newGemMetadata = new FunctionMetadata(gemEntityFeatureName, collectorMetadata.getLocale());
        
        collectorMetadata.copyTo(newGemMetadata);
        
        // Get the gem design.
        Document designDocument = XMLPersistenceHelper.getEmptyDocument();
        saveXML(designDocument);
        
        Status saveStatus = gemCutter.getWorkspace().saveEntity(gemName, gemDefinition, newGemMetadata, designDocument);
        
        if (saveStatus.getSeverity() != Status.Severity.ERROR) {
            // TEMP: this is a re-entrancy hack
            gemCutter.recompileWorkspace(true);
        }
        
        return saveStatus;
    }
    
    /**
     * Obtain the CAL definition of a gem.
     * @param collectorGem the gem for which the definition should be obtained..
     * @param scope the scope of the gem.
     */
    private String getGemDefinition(CollectorGem collectorGem, Scope scope) {

        // Get the qualified name.
        String unqualifiedGemName = collectorGem.getUnqualifiedName();

        // Now get the text of the CAL definition for the gem.

        // The definition..
        SourceModel.FunctionDefn scDef = CALSourceGenerator.getFunctionSourceModel(unqualifiedGemName, collectorGem, scope);

        // TODO Display any error messages from logger?
        CompilerMessageLogger logger = new MessageLogger ();

        // The declaration..
        TypeExpr type = gemCutter.getTypeChecker().checkFunction(
            new AdjunctSource.FromSourceModel(scDef), gemCutter.getWorkingModuleName(), logger);
        String typeDeclaration = (type == null) ? null : unqualifiedGemName + " :: " + type.toString() + ";";

        StringBuilder sb = new StringBuilder();
        if (typeDeclaration != null) {
            sb.append(typeDeclaration + "\n");
        }
        sb.append(scDef + "\n");
        
        return sb.toString();
    }

    /**
     * Load a gem's design and put it on the table top.
     * @param gemEntity the entity whose design to load.
     * @param loadStatus a Status object for storing the status of the load operation
     */
    void loadGemDesign(GemEntity gemEntity, Status loadStatus) {
        GemDesign gemDesign = gemEntity.getDesign(loadStatus);
        if (gemDesign != null) {
            loadGemDesign(gemDesign, loadStatus);
        }
    }
    
    /**
     * Load a gem design and put it on the tabletop.
     * @param gemDesign the design to load.
     * @param loadStatus a Status object for storing the status of the load operation.
     */
    void loadGemDesign(GemDesign gemDesign, Status loadStatus) {
        // Switch to the module the design is from.
        QualifiedName designName = gemDesign.getDesignName();
        ModuleName moduleName = designName.getModuleName();
        if (gemCutter.getWorkspace().getMetaModule(moduleName) != null) {
            gemCutter.changeModuleAndNewTableTop(moduleName, false);
        }
        
        loadXML(gemDesign.getDesignDocument().getDocumentElement(), loadStatus);
        
        // Find the collector that was loaded on the table top.
        String unqualifiedDesignName = designName.getUnqualifiedName();
        Set<CollectorGem> collectors = gemCutter.getTableTop().getGemGraph().getCollectorsForName(unqualifiedDesignName);

        if (collectors.iterator().hasNext()) {
            CollectorGem collectorGem = collectors.iterator().next();
        
            // Find the gem in the working module with the same name
            GemEntity entity = gemCutter.getWorkspace().getGemEntity(designName);

            if (entity != null) {
                // Load the metadata for the gem and copy it to the collector
                FunctionMetadata gemMetadata = (FunctionMetadata)entity.getMetadata(GemCutter.getLocaleFromPreferences());
                collectorGem.setDesignMetadata(gemMetadata);
            } else {
                // Clear the metadata for the collector, since there is no gem entity for the design
                collectorGem.clearDesignMetadata();
            }

        } else {
            loadStatus.add(new Status(Status.Severity.ERROR, GemCutter.getResourceString("SOM_CouldntFindCollector"), null));
        }
    }
}
