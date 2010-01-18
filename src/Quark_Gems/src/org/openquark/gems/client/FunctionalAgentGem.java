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
 * FunctionalAgentGem.java
 * Creation date: (10/18/00 1:03:35 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.util.List;

import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.services.CALPersistenceHelper;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.GemEntity;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A FunctionalAgentGem is a named gem which can represent a supercombinator, data constructor, or class method.
 * Creation date: (10/18/00 1:03:35 PM)
 * @author Luke Evans
 */
public class FunctionalAgentGem extends Gem implements CompositionNode.GemEntityNode, NamedGem {

    /** The gemEntity that this Gem represents */
    private GemEntity gemEntity;

    /**
     * Construct a FunctionalAgentGem from a Supercombinator and its type.
     * @param gemEntity the GemEntity from which to construct the gem.
     */
    public FunctionalAgentGem(GemEntity gemEntity) {
        super(gemEntity.getTypeArity());
        this.gemEntity = gemEntity;

        // Some of the supplied argument names might be null, but that's OK because
        // the input metadata will automatically use a default name if it's given a null value.
        // NOTE: We want any gems created with names from code or from metadata to consider those names
        //       as NOT being user saved names. This works fine here as a new input name object will be
        //       created with an initial name of whatever we supply.
        FunctionalAgentMetadata metadata = gemEntity.getMetadata(GemCutter.getLocaleFromPreferences());
        ArgumentMetadata[] argMetadata = metadata.getArguments();
        int numNamedArgs = gemEntity.getNNamedArguments();
        CALDocComment caldoc = gemEntity.getFunctionalAgent().getCALDocComment();
        
        for (int i = 0, numInputs = getNInputs(); i < numInputs; i++) {
            PartInput inputPart = getInputPart(i);
            
            String nameFromEntity = i < numNamedArgs ? gemEntity.getNamedArgument(i) : null;
            String displayName = i < argMetadata.length ? argMetadata[i].getDisplayName() : null;
            FieldName caldocName = (caldoc != null && i < caldoc.getNArgBlocks()) ? caldoc.getNthArgBlock(i).getArgName() : null; 
            
            String originalName;
            if (displayName != null) {
                originalName = displayName;
            } else if (caldocName != null && caldocName instanceof FieldName.Textual) {
                // if the CALDoc name is present, we will use it instead of the name from the entity
                // since the CALDoc name is specified by the user, where the name from the entity
                // may have come from an automatically extracted parameter name for a foreign function
                originalName = caldocName.getCalSourceForm();
            } else if (nameFromEntity != null) {
                originalName = nameFromEntity;
            } else {
                originalName = null;
            }
            
            inputPart.setOriginalInputName(originalName);
            inputPart.setArgumentName(new ArgumentName(originalName));
        }
        
        setPartTypes(gemEntity.getTypeExpr().getTypePieces());
    }   
    
    /**
     * Construct an empty gem.
     * A gem created using this constructor is appropriate for use in deserialization.
     */
    private FunctionalAgentGem() {
        // Initialize with no input parts.
        super(0);
    }

    /**
     * Get the qualified name of the Gem (the name of the underlying supercombinator)
     * @return QualifiedName the name
     */
    public QualifiedName getName() {
        return gemEntity.getName();
    }

    /**
     * Get the unqualified name of the Gem (the name of the underlying supercombinator)
     * @return The node's unqualified name.
     */
    public String getUnqualifiedName(){
        return gemEntity.getName().getUnqualifiedName();
    }
    
    /**
     * Return the entity represented by this gem
     * @return GemEntity the entity represented by this gem
     */
    public final GemEntity getGemEntity() {
        return gemEntity;
    }

    /**
     * Update the entity represented by this FunctionalAgentGem.
     * This method is used when the program is recompiled, and entities are regenerated.
     * @param workspace the workspace from which to reload the entity.
     */
    void updateGemEntity(CALWorkspace workspace) throws GemEntityNotPresentException {
        updateGemEntity(workspace, gemEntity.getName());        
    }
    
    /**
     * Update the entity represented by this FunctionalAgentGem.
     * This method is used when the program is recompiled, and entities are regenerated.
     * @param workspace the workspace from which to reload the entity.
     * @param entityName If the gem has been renamed, this should be the new name of the gem.
     */
    void updateGemEntity(CALWorkspace workspace, QualifiedName entityName) throws GemEntityNotPresentException  {        
        // TODO: we should also check/morph for sc arity and type changes, and revalidate any connections.
        GemEntity newEntity = workspace.getGemEntity(entityName);
        QualifiedName oldName = gemEntity.getName();
        if (newEntity == null) {
            throw new GemEntityNotPresentException(gemEntity.getName());
        }
        this.gemEntity = newEntity;
        
        if (oldName == entityName){
            nameChangeListener.nameChanged(new NameChangeEvent(this, oldName.getQualifiedName()));
        }
    }

    /**
     * Store the local type information for this Gem's parts.  
     * The order is: inputs first (in order), then output (if any).
     * @param tes the types
     */
    private final void setPartTypes(TypeExpr[] tes) {
        // The output
        PartOutput outputPart = getOutputPart();
        if (outputPart != null) {
            int last = tes.length - 1;
            getOutputPart().setType(tes[last]);
        }

        // The inputs (if we have any)
        int numInputs = getNInputs();
        for (int i = 0; i < numInputs; i++) {
            getInputPart(i).setType(tes[i]);
        }    
    }

    /**
     * Describe this Gem
     * @return the description
     */
    @Override
    public String toString() {
        return getName().getQualifiedName();
    }

    /*
     * Methods supporting XMLPersistable                 ********************************************
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveXML(Node parentNode, GemContext gemContext) {
        
        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the functional agent gem element
        Element resultElement = document.createElementNS(GemPersistenceConstants.GEM_NS, GemPersistenceConstants.FUNCTIONAL_AGENT_GEM_TAG);
        resultElement.setPrefix(GemPersistenceConstants.GEM_NS_PREFIX);
        parentNode.appendChild(resultElement);

        // Add info for the superclass gem.
        super.saveXML(resultElement, gemContext);


        // Now add FunctionalAgentGem-specific info

        // Add an element for the name.
        Element nameElement = CALPersistenceHelper.qualifiedNameToElement(getName(), document);
        resultElement.appendChild(nameElement);
    }

    /**
     * Create a new FunctionalAgentGem and loads its state from the specified XML element.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param workspace the workspace from which to retrieve functional agents by name
     * @return FunctionalAgentGem
     * @throws BadXMLDocumentException
     */
    public static FunctionalAgentGem getFromXML(Element gemElement, GemContext gemContext, CALWorkspace workspace) throws BadXMLDocumentException {
        FunctionalAgentGem gem = new FunctionalAgentGem();
        gem.loadXML(gemElement, gemContext, workspace);
        return gem;
    }

    /**
     * Load this object's state.
     * @param gemElement the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param workspace the program from which to retrieve functional agents by name
     */
    void loadXML(Element gemElement, GemContext gemContext, CALWorkspace workspace) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(gemElement, GemPersistenceConstants.FUNCTIONAL_AGENT_GEM_TAG);
        XMLPersistenceHelper.checkPrefix(gemElement, GemPersistenceConstants.GEM_NS_PREFIX);

        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemElement);

        // Get info for the underlying gem.
        Element superGemElem = (childElems.size() < 1) ? null : (Element) childElems.get(0);
        XMLPersistenceHelper.checkIsElement(superGemElem);
        super.loadXML(superGemElem, gemContext);

        // Get the name
        Element nameElem = (childElems.size() < 2) ? null : (Element) childElems.get(1);
        XMLPersistenceHelper.checkIsElement(nameElem);
        QualifiedName gemName = CALPersistenceHelper.elementToQualifiedName(nameElem);
        
        // Get the supercombinator and its type
        this.gemEntity = workspace.getGemEntity(gemName);
        if (this.gemEntity == null) {
            XMLPersistenceHelper.handleBadDocument(nameElem, "No functional agent found for " + gemName);
        }

        // Set the part types if any
        TypeExpr scType = gemEntity.getTypeExpr();
        if (scType != null) {
            
            // Check that the entity has the right number of applications.
            if (scType.getArity() != getNInputs()) {
                XMLPersistenceHelper.handleBadDocument(nameElem, 
                                                             "Error loading " + gemName + ".  Functional agent has " + 
                                                               scType.getArity() + " inputs, saved with " + getNInputs() + ".");
            }
            
            setPartTypes(scType.getTypePieces());
        }
    }
}
