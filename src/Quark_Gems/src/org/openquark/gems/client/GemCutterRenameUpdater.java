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
 * DesignRenameUpdater.java
 * Created: Mar 7, 2005
 * By: Peter Cardwell
 */

package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALPersistenceHelper;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.GemDesign;
import org.openquark.cal.services.GemDesignManager;
import org.openquark.cal.services.GemDesignStore;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.cal.services.WorkspaceResource;
import org.openquark.cal.valuenode.Target;
import org.openquark.cal.valuenode.TargetRunner;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.Argument.NameTypePair;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.util.Pair;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Provides functionality to update GemCutter entities to reflect a renaming.
 * 
 * This class can be used to update either a design document or the gems on a tabletop whenever a gem, type class, 
 * or type constructor is renamed.
 * 
 * @author Peter Cardwell
 */
public class GemCutterRenameUpdater {
    
    /**
     * Used for errors that occur during the updateDesigns operation.
     * @author Peter Cardwell
     */
    private static class RenamingException extends Exception {
        private static final long serialVersionUID = -7382673467540795708L;
        private final String errorMessage;
        
        RenamingException (String errorMessage) {
            super(errorMessage);
            this.errorMessage = errorMessage;
        }
        
        String getErrorMessage() {
            return errorMessage;
        }
    }     
    
    private final Status status;
    private final TypeChecker typeChecker;
    private final QualifiedName newName;
    private final QualifiedName oldName;
    private final SourceIdentifier.Category category;
    
    /**
     * Constructs a new DesignRenameUpdater
     * @param status A status object to keep track of any error messages.
     * @param typeChecker The type checker to use for the renaming of code expressions (ie. in code gems).
     * @param newName The new name of the identifier to rename.
     * @param oldName The old name of the identifier.
     * @param category The category of the identifier.
     */
    GemCutterRenameUpdater(Status status, TypeChecker typeChecker, QualifiedName newName, QualifiedName oldName, SourceIdentifier.Category category) {
        this.status = status;
        this.typeChecker = typeChecker;
        this.newName = newName;
        this.oldName = oldName;
        this.category = category;
    }
    
    /**
     * Replace the children nodes of the destination document with copies of the nodes of the source document.
     * @param destinationDocument The document for which to replace the nodes
     * @param sourceDocument The document to copy the nodes from
     */
    private void replaceDocumentChildren(Document destinationDocument, Document sourceDocument) {
        // First remove all children of the destination document
        while(destinationDocument.hasChildNodes()) {
            destinationDocument.removeChild(destinationDocument.getFirstChild());
        }
        
        // Now make a copy of each child node from the source document and append it to the destination document.
        for (Node childNode = sourceDocument.getFirstChild(); 
            childNode.getNextSibling() != null; 
            childNode = childNode.getNextSibling()){
            destinationDocument.appendChild(childNode.cloneNode(true));
        }
    }
    
    
    
    /**
     * Updates all the designs stored in the given workspace to reflect the renaming.
     * @param workspaceManager The workspace manager containing all the design the user wishes to update
     * @return True if any designs were updated
     */
    public boolean updateDesigns(WorkspaceManager workspaceManager) {
        CALWorkspace workspace = workspaceManager.getWorkspace();
        List<Pair<GemDesign, Document>> undoList = new ArrayList<Pair<GemDesign, Document>>();    // A list of designs that have been updated and should be undone if an error is encountered.
        try {
            ModuleName[] workspaceModuleNames = workspaceManager.getModuleNamesInProgram();
            
            // loop through each module in the workspace
            for (final ModuleName moduleName : workspaceModuleNames) {
                
                if (workspace.getMetaModule(moduleName) == null) {
                    // the program contains the module but the workspace doesn't, so we cannot process it
                    continue;
                }
                
                ModuleNameResolver moduleNameResolver = workspaceManager.getModuleTypeInfo(moduleName).getModuleNameResolver();
                GemDesignManager designManager = (GemDesignManager)workspace.getResourceManager(moduleName, WorkspaceResource.GEM_DESIGN_RESOURCE_TYPE);
                
                // loop through the resources for this module
                for (Iterator<WorkspaceResource> it = ((GemDesignStore)designManager.getResourceStore()).getResourceIterator(moduleName); it.hasNext(); ) {
                    WorkspaceResource designResource = it.next();
                    
                    GemDesign gemDesign = GemDesign.loadGemDesign(designResource, new Status("Load Design Status"));
                    
                    Document designDoc = gemDesign.getDesignDocument();
                    Document oldDesignDoc = (Document)designDoc.cloneNode(true);
                    
                    boolean changesMade = updateDesignDocument(designDoc, gemDesign.getDesignName(), moduleNameResolver);
                    
                    if (changesMade) {                    
                        if (!designManager.getResourceStore().isWriteable(new ResourceName(CALFeatureName.getFunctionFeatureName(gemDesign.getDesignName())))) {
                            throw new RenamingException("Can not update the design for " + gemDesign.getDesignName().getQualifiedName() + " because it is not writeable."); 
                            
                        }
                        
                        Status saveGemStatus = new Status("Save gem design status");
                        workspace.saveDesign(gemDesign, saveGemStatus);
                        if (saveGemStatus.getSeverity().equals(Status.Severity.ERROR)) {
                            throw new RenamingException("Error saving the updated design for " + gemDesign.getDesignName().getQualifiedName() + ".");
                        }
                        
                        undoList.add(new Pair<GemDesign, Document>(gemDesign, oldDesignDoc));
                    }
                }
            }
            
            // Return true if the undoList has contents (ie. changes have been made)
            return !undoList.isEmpty();
            
        } catch (RenamingException e) {
            status.add(new Status(Status.Severity.ERROR, e.getErrorMessage()));
            
            for (int i = undoList.size() - 1; i >= 0; i--) {
                Pair<GemDesign, Document> designDocPair = undoList.get(i);
                GemDesign gemDesign = designDocPair.fst();
                Document oldDesignDoc = designDocPair.snd();
                replaceDocumentChildren(gemDesign.getDesignDocument(), oldDesignDoc);
                
                Status undoStatus = new Status("Undo design update status");
                workspace.saveDesign(gemDesign, undoStatus); 
                
                if (undoStatus.getSeverity().equals(Status.Severity.ERROR)) {
                    String msg = "FATAL: Error undoing the design update for " + gemDesign.getDesignName().getQualifiedName();
                    status.add(new Status(Status.Severity.ERROR, msg));
                }
            }
            return false;
        }
    }
    
     
    /**
     * Updates all references to the renamed entity in the given design document.
     * @param document The document to update. 
     * @param designName The qualifiedName of the gem that this design represents. 
     * @param moduleNameResolver the module name resolver for the module containing the document.
     * @return True if changes were made to the document.
     */
    private boolean updateDesignDocument (Document document, QualifiedName designName, ModuleNameResolver moduleNameResolver) throws RenamingException {
        boolean changesMade = false;
        try {
            // Get the document element
            Element documentElement = document.getDocumentElement();
            XMLPersistenceHelper.checkTag(documentElement, GemPersistenceConstants.GEMCUTTER_TAG);  
            
            // Get the table top element
            Node tableTopNode = documentElement.getFirstChild();
            XMLPersistenceHelper.checkIsElement(tableTopNode);           
            Element tableTopElement = (Element)tableTopNode;
            XMLPersistenceHelper.checkTag(tableTopElement, GemPersistenceConstants.TABLETOP_TAG);
            
            // Loop over all the children of the tabletop node
            Node tableToChildNode;
            for (tableToChildNode = tableTopElement.getFirstChild();
                GemCutterPersistenceHelper.isDisplayedGemElement(tableToChildNode) ||
                    GemCutterPersistenceHelper.isDisplayedGemElement(tableToChildNode.getFirstChild()); // old save format
                tableToChildNode = tableToChildNode.getNextSibling()) {
                
                Element displayedGemElement = (Element)  tableToChildNode;
                
                // Do a check for the old save format
                Node firstChild = displayedGemElement.getFirstChild();
                if (firstChild instanceof Element && firstChild.getLocalName().equals(GemPersistenceConstants.DISPLAYED_GEM_TAG)) {
                    displayedGemElement = (Element)firstChild;
                }

                // Get the gem node and dispatch to the appropriate helper method based on the tag name.
                Node gemNode = displayedGemElement.getFirstChild();
                XMLPersistenceHelper.checkIsElement(gemNode);
                String tagName = gemNode.getLocalName();
                if (tagName.equals(GemPersistenceConstants.COLLECTOR_GEM_TAG)) {
                    changesMade |= updateCollectorGemElement(document, designName.getModuleName(), gemNode); 
                } else if (tagName.equals(GemPersistenceConstants.FUNCTIONAL_AGENT_GEM_TAG)) {
                    changesMade |= updateFunctionalAgentElement(document, moduleNameResolver, gemNode); 
                } else if (tagName.equals(GemPersistenceConstants.VALUE_GEM_TAG)) {
                    changesMade |= updateValueGemElement(document, designName.getModuleName(), moduleNameResolver, gemNode);
                } else if (tagName.equals(GemPersistenceConstants.CODE_GEM_TAG)) {
                    changesMade |= updateCodeGemElement(document, designName.getModuleName(), moduleNameResolver, gemNode);
                }
            }
            
            return changesMade;
            
        } catch (BadXMLDocumentException ex) {
            throw new RenamingException("The design document for " + designName.getQualifiedName() + " is formatted incorrectly.");
        }
    }

    

    /**
     * Updates this collector gem design element if it represents the name of the gem being renamed.
     * @param document The document that this node is contained in.
     * @param moduleName The module that this gem design is contained in.
     * @param gemNode The node representing the collector gem to update.
     * @return True if changes were made to the element.
     */
    private boolean updateCollectorGemElement(Document document, ModuleName moduleName, Node gemNode) throws BadXMLDocumentException {
        boolean changesMade = false;
        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemNode);
        
        Element nameElem = (childElems.size() < 2) ? null : (Element) childElems.get(1);
        
        XMLPersistenceHelper.checkIsElement(nameElem);
        String gemName = CALPersistenceHelper.elementToUnqualifiedName(nameElem);
        if ((category == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) && 
                gemName.equals(oldName.getUnqualifiedName()) && moduleName.equals(oldName.getModuleName())) {
            gemNode.replaceChild(CALPersistenceHelper.unqualifiedNameToElement(newName.getUnqualifiedName(), document), nameElem);
            changesMade = true;
        }
        return changesMade;
    }

    /**
     * Updates the contents of this value gem design element if it references the entity being renamed at all.
     * @param document The document that this node is contained in.
     * @param moduleName The module that this gem design is contained in.
     * @param moduleNameResolver the module name resolver for the module containing the document.
     * @param gemNode The node representing the value gem to update.
     * @return True if changes were made to the element
     */
    private boolean updateValueGemElement(Document document, ModuleName moduleName, ModuleNameResolver moduleNameResolver, Node gemNode) throws BadXMLDocumentException {
        boolean changesMade = false;
        
        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemNode);
        
        Element valueChildElem = (childElems.size() < 2) ? null : (Element) childElems.get(1);
        XMLPersistenceHelper.checkIsElement(valueChildElem);
        XMLPersistenceHelper.checkTag(valueChildElem, GemPersistenceConstants.VALUE_GEM_VALUE_TAG);
        
        // Get the value text
        StringBuilder valueText = new StringBuilder();
        Node valueChild = valueChildElem.getFirstChild();
        XMLPersistenceHelper.getAdjacentCharacterData(valueChild, valueText);
        String valueString = new String(valueText);
       
        String updatedValueString = typeChecker.calculateUpdatedCodeExpression(valueString, moduleName, moduleNameResolver, null, oldName, newName, category, null);
        if (!updatedValueString.equals(valueString)) {
            CDATASection newValueChild = XMLPersistenceHelper.createCDATASection(document, updatedValueString);
            valueChildElem.replaceChild(newValueChild, valueChild);
            changesMade = true;
        }
        return changesMade;
    }

    /**
     * Updates the functional agent gem if it represents the gem being renamed.
     * @param document The document that this node is contained in.
     * @param moduleNameResolver the module name resolver for the module containing the document.
     * @param gemNode The node representing the functional agent gem to update.
     * @return True if changes were made to the element
     */
    private boolean updateFunctionalAgentElement(Document document, ModuleNameResolver moduleNameResolver, Node gemNode) throws BadXMLDocumentException {
        boolean changesMade = false;
        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemNode);

        Element nameElem = (childElems.size() < 2) ? null : (Element) childElems.get(1);
        
        XMLPersistenceHelper.checkIsElement(nameElem);
        QualifiedName gemName = CALPersistenceHelper.elementToQualifiedName(nameElem);
        if ((category == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD ||
             category == SourceIdentifier.Category.DATA_CONSTRUCTOR) &&
                gemName.equals(oldName)) {
            gemNode.replaceChild(CALPersistenceHelper.qualifiedNameToElement(newName, document), nameElem);
            changesMade = true;
        } else if ( (category == SourceIdentifier.Category.MODULE_NAME) &&
                moduleNameResolver.resolve(gemName.getModuleName()).getResolvedModuleName().equals(oldName.getModuleName()) ){
            QualifiedName newGemName = QualifiedName.make(newName.getModuleName(), gemName.getUnqualifiedName());
            gemNode.replaceChild(CALPersistenceHelper.qualifiedNameToElement(newGemName, document), nameElem);
            changesMade = true;
        }
        return changesMade;
    }
        
    /**
     * Updates the functional agent gem if it represents the gem being renamed.
     * This method will try to resolve any conflicts between the new name and any arguments to the code gem.
     * It will do this by modifying the name of any argument that matches the new name of renaming.
     * @param document The document that this node is contained in.
     * @param moduleNameResolver the module name resolver for the module containing the document.
     * @param gemNode The node representing the functional agent gem to update.
     * @return True if changes were made to the element.
     */
    private boolean updateCodeGemElement(Document document, ModuleName moduleName, ModuleNameResolver moduleNameResolver, Node gemNode) throws BadXMLDocumentException {
        boolean changesMade = false;
        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemNode);
        
        Element codeElement = (childElems.size() < 3) ? null : (Element) childElems.get(2);
        XMLPersistenceHelper.checkIsElement(codeElement);

        StringBuilder codeText = new StringBuilder();
        Node codeChild = codeElement.getFirstChild();
        XMLPersistenceHelper.getAdjacentCharacterData(codeChild, codeText);
        String codeString = new String(codeText);
        String updatedCodeString = codeString;
        
        // Load the qualification map
        Element qualificationMapElement = XMLPersistenceHelper.getChildElement(codeElement, CodeQualificationMap.QUALIFICATION_MAP_TAG);
        CodeQualificationMap qualificationMap = new CodeQualificationMap();
        if (qualificationMapElement != null) {
            qualificationMap.loadFromXML(codeElement);
        }
        
        if (category != SourceIdentifier.Category.MODULE_NAME) {
            QualifiedName qualificationMapEntry = qualificationMap.getQualifiedName(oldName.getUnqualifiedName(), category);
            if (oldName.equals(qualificationMapEntry)) {
                // The old name is in the qualification map. This means there might be an unqualified reference to the symbol that when
                // renamed might clash with one of the arguments. Let's check for that now.
                
                // First, load the arguments
                Element argsElement = (childElems.size() < 4) ? null : (Element) childElems.get(3);
                XMLPersistenceHelper.checkIsElement(argsElement);
                XMLPersistenceHelper.checkTag(argsElement, GemPersistenceConstants.CODE_GEM_ARGUMENTS_TAG);
                
                // We'll create both a list (for easy iteration) and a set (for easy membership checks).
                List<Element> argNodes = XMLPersistenceHelper.getChildElements(argsElement, GemPersistenceConstants.CODE_GEM_ARGUMENT_TAG);
                int numArgs = argNodes.size();
                String[] argNames = new String[numArgs];
                for (int i = 0; i < numArgs; i++) {
                    Element argNode = argNodes.get(i);                
                    argNames[i] = argNode.getAttribute(GemPersistenceConstants.CODE_GEM_ARGUMENT_NAME_ATTR);
                }
                List<String> argNamesList = Arrays.asList(argNames);
                Set<String> argNamesSet = new HashSet<String>(argNamesList);
                
                // Next, loop over the arguments and look for the conflict..
                for (int i = 0, n = argNamesList.size(); i < n; i++) {
                    String argName = argNamesList.get(i);
                    if (argName.equals(newName.getUnqualifiedName())) {
                        // Here we are. The new name is going to collide with this argument's name.
                        // We are going to have to rename this argument.
                        QualifiedName oldArgName = QualifiedName.make(moduleName, argName);
                        
                        // Figure out a suitable new name for the argument that does not conflict with anything else.
                        int suffix = 1;
                        QualifiedName newArgName;
                        while (true) {
                            String potentialName = argName + suffix;
                            
                            // Make sure that the new potential name is not the name of another argument and is not in the qualification map
                            if (!argNamesSet.contains(potentialName) && 
                                    (qualificationMap.getQualifiedName(
                                            potentialName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) == null)) {
                                newArgName = QualifiedName.make(moduleName, potentialName);
                                break;
                            }
                            suffix++;
                        }
                        
                        // Update the reference to the argument in the code expression
                        updatedCodeString = typeChecker.calculateUpdatedCodeExpression(codeString, moduleName, moduleNameResolver, qualificationMap, oldArgName, newArgName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, null);
                        
                        // Create a new argument node and replace the old one with it.
                        Element oldArgNode = argNodes.get(i);
                        Element newArgNode = document.createElement(GemPersistenceConstants.CODE_GEM_ARGUMENT_TAG);
                        newArgNode.setAttribute(GemPersistenceConstants.CODE_GEM_ARGUMENT_NAME_ATTR, newArgName.getUnqualifiedName());
                        argsElement.replaceChild(newArgNode, oldArgNode);
                        
                        changesMade = true;
                        break;
                        
                    }
                }
            }
        }
        
        // Update the code expression
        updatedCodeString = typeChecker.calculateUpdatedCodeExpression(updatedCodeString, moduleName, moduleNameResolver, qualificationMap, oldName, newName, category, null);
        if (!updatedCodeString.equals(codeString)) {
            CDATASection newCodeChild = XMLPersistenceHelper.createCDATASection(document, updatedCodeString);
            codeElement.replaceChild(newCodeChild, codeChild);
            changesMade = true;
        }         
        
        
        // Update the qualification map
        if (qualificationMap != null) {
            if (updateQualificationMap(qualificationMap)) {
                codeElement.removeChild(qualificationMapElement);
                qualificationMap.saveToXML(codeElement);
                changesMade = true;                
            }
        }
        return changesMade;
    }

    /**
     * Updates all references to the renamed entity in the given CodeQualificationMap. If the entity type is a module,
     * this can include any identifiers that use that module name. Otherwise, the qualification for the entity itself will be
     * updated if it is in the map.
     * @param qualificationMap The map to update
     * @return True if the qualification map is changed. 
     */
    private boolean updateQualificationMap(CodeQualificationMap qualificationMap) {
        boolean changesMade = false;
        
        SourceIdentifier.Category[] categories = new SourceIdentifier.Category[] {
                SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD,
                SourceIdentifier.Category.DATA_CONSTRUCTOR,
                SourceIdentifier.Category.TYPE_CONSTRUCTOR,
                SourceIdentifier.Category.TYPE_CLASS                    
        };
        
        for (final Category sectionCategory : categories) {
            Set<String> unqualifiedNames = qualificationMap.getUnqualifiedNames(sectionCategory);
            for (final String string : unqualifiedNames) {
                String unqualifiedName = string;
                QualifiedName qualifiedName = qualificationMap.getQualifiedName(unqualifiedName, sectionCategory);
                
                if (category == sectionCategory && oldName.equals(qualifiedName)) {
                    qualificationMap.removeQualification(unqualifiedName, sectionCategory);
                    qualificationMap.putQualification(newName.getUnqualifiedName(), newName.getModuleName(), sectionCategory);
                    changesMade = true;
                } else if (category == SourceIdentifier.Category.MODULE_NAME && oldName.getModuleName().equals(qualifiedName.getModuleName())) {
                    qualificationMap.removeQualification(unqualifiedName, sectionCategory);
                    qualificationMap.putQualification(unqualifiedName, newName.getModuleName(), sectionCategory);
                    changesMade = true;
                }
            }
        }
        
        return changesMade;
    }
    
    /**
     * Updates all references to the renamed entity on the current TableTop (Ie. in the gem graph and any on screen code editors).
     * This includes functional agent references, collector gems, code gems, and value nodes. 
     * @param gemCutter
     * @throws GemEntityNotPresentException
     */
    public void updateTableTop(GemCutter gemCutter) throws GemEntityNotPresentException {
        ModuleName workingModuleName = gemCutter.getPerspective().getWorkingModuleName();
        ModuleNameResolver workingModuleNameResolver = gemCutter.getPerspective().getWorkingModuleTypeInfo().getModuleNameResolver();
        
        for (final Gem gem : gemCutter.getTableTop().getGemGraph().getGems()) {
            if (gem instanceof FunctionalAgentGem) {
                FunctionalAgentGem fGem = (FunctionalAgentGem)gem;
                if (fGem.getName().equals(oldName)) {
                    fGem.updateGemEntity(gemCutter.getWorkspace(), newName);
                } else {
                    fGem.updateGemEntity(gemCutter.getWorkspace());
                }
            } else if (gem instanceof CollectorGem) {
                CollectorGem cGem = (CollectorGem)gem;
                if (cGem.getUnqualifiedName().equals(oldName.getUnqualifiedName())) {
                    cGem.setName(newName.getUnqualifiedName());
                }
            } else if (gem instanceof CodeGem) {
                CodeGem cGem = (CodeGem)gem;
                
                CodeGemEditor codeGemEditor = gemCutter.getTableTop().getCodeGemEditor(cGem);
                codeGemEditor.setDelayUpdatingForTextChanges(false);
                codeGemEditor.setCodeAnalyser(gemCutter.getCodeGemAnalyser());
                String visibleCode = codeGemEditor.getGemCodePanel().getCALEditorPane().getText();
                String updatedVisibleCode = visibleCode;
                if (category != SourceIdentifier.Category.MODULE_NAME) {
                    QualifiedName qualificationMapEntry = cGem.getQualificationMap().getQualifiedName(oldName.getUnqualifiedName(), category);
                    if (oldName.equals(qualificationMapEntry)) {
                        // The old name is in the qualification map. This means there might be an unqualified reference to the symbol that when
                        // renamed might clash with one of the arguments. Let's check for that now.
                        
                        NameTypePair[] arguments = cGem.getArguments();
                        for (int i = 0, n = arguments.length; i < n; i++) {
                            if (arguments[i].getName().equals(newName.getUnqualifiedName())) {
                                // Here we are. The new name is going to collide with this argument's name.
                                // We are going to have to rename this argument..
                                QualifiedName oldArgName = QualifiedName.make(workingModuleName, arguments[i].getName());
                                
                                // Append successive integers onto the end of the argument name until we no longer have a conflict.
                                Set<String> argNameSet = cGem.getArgNameToInputMap().keySet();
                                int suffix = 1;
                                QualifiedName newArgName;
                                while (true) {
                                    String potentialName = arguments[i].getName() + suffix;
                                    
                                    // Make sure that the new potential name is not the name of another argument and is not in the qualification map
                                    if (!argNameSet.contains(potentialName) && 
                                            (cGem.getQualificationMap().getQualifiedName(
                                                    potentialName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) == null)) {
                                        newArgName = QualifiedName.make(workingModuleName, potentialName);
                                        break;
                                    }
                                    suffix++;
                                }
                                
                                // Update references to the argument in the code itself
                                updatedVisibleCode = typeChecker.calculateUpdatedCodeExpression(visibleCode, workingModuleName, workingModuleNameResolver, cGem.getQualificationMap(), oldArgName, newArgName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, null);
                                
                                // Update the argument to input map and the argument names in the code gem
                                Map<String, PartInput> newArgNameToInputMap = cGem.getArgNameToInputMap();
                                newArgNameToInputMap.put(newArgName.getUnqualifiedName(), newArgNameToInputMap.get(oldArgName.getUnqualifiedName()));
                                newArgNameToInputMap.remove(oldArgName.getUnqualifiedName());
                                arguments[i] = new NameTypePair(newArgName.getUnqualifiedName(), arguments[i].getType());                          
                                cGem.definitionUpdate(cGem.getCode(), arguments, cGem.getCodeResultType(), newArgNameToInputMap, cGem.getQualificationMap(), cGem.getVisibleCode());
                                
                                // Replace the code in the editor and make sure the old argument name is no longer in the editor's list of arguments.
                                codeGemEditor.getGemCodePanel().getCALEditorPane().setText(updatedVisibleCode);
                                codeGemEditor.changeArgumentToQualification(oldArgName.getUnqualifiedName(), newName.getModuleName());
                                break;
                            }
                        }
                    }
                }
                
                // Update any references to the entity in the code
                updatedVisibleCode = typeChecker.calculateUpdatedCodeExpression(updatedVisibleCode, workingModuleName, workingModuleNameResolver, cGem.getQualificationMap(), oldName, newName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, null);
                if (category == SourceIdentifier.Category.MODULE_NAME) {
                    codeGemEditor.updateQualificationsForModuleRename(oldName.getModuleName(), newName.getModuleName());
                } else {
                    codeGemEditor.updateQualificationsForEntityRename(oldName, newName, category);
                }
                codeGemEditor.getGemCodePanel().getCALEditorPane().setText(updatedVisibleCode);
                
                
                codeGemEditor.setDelayUpdatingForTextChanges(true);
                
            } else if (gem instanceof ValueGem) {
                ValueGem vGem = (ValueGem) gem;          

                ValueNode vNode = vGem.getValueNode();
                String valueString = vNode.getCALValue();
                String updatedValueString = typeChecker.calculateUpdatedCodeExpression(valueString, workingModuleName, workingModuleNameResolver, null, oldName, newName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, null);
                
                if (!valueString.equals(updatedValueString)) {
                    // Create a target to be run by a value runner, and return the result
                    Target valueTarget = new Target.SimpleTarget(updatedValueString);
    
                    ValueNode targetValue = null;
                    try {
                        targetValue = gemCutter.getValueRunner().getValue(valueTarget, workingModuleName);
                    } catch (TargetRunner.ProgramCompileException pce) {
                        status.add(new Status(Status.Severity.ERROR, "Error updating value gem. Can't compile the following text: " + updatedValueString));
                    }
                    
                    if (targetValue == null) {
                        status.add(new Status(Status.Severity.ERROR, "Error updating value gem. Can't convert the following text to a value node: " + updatedValueString));
                    }
                    
                    vGem.changeValue(targetValue);
                }
            }
        }
    }
}
