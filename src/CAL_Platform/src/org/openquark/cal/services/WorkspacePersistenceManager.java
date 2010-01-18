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
 * WorkspacePersistenceManager.java
 * Creation date: Oct 13, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.NamespaceInfo;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.openquark.util.xml.XMLPersistenceHelper.DocumentConstructionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



/**
 * This class takes care of saving and loading workspace-related resources.
 * @author Edward Lam
 */
class WorkspacePersistenceManager {

    /** The workspace with which this manager is associated. */
    private final CALWorkspace workspace;

    /**
     * A simple wrapper around a vault descriptor and a string which identifies a vault type and its location.
     * @author Edward Lam
     */
    private static class VaultLocation {
        
        private final String vaultDescriptor;
        private final String locationString;

        /**
         * Constructor for a VaultLocation.
         */
        public VaultLocation(String vaultDescriptor, String locationString) {
            this.vaultDescriptor = vaultDescriptor;
            this.locationString = locationString;
        }
        /**
         * @return Returns the vaultDescriptor.
         */
        public String getVaultDescriptor() {
            return vaultDescriptor;
        }
        /**
         * @return Returns the locationString.
         */
        public String getLocationString() {
            return locationString;
        }
    }
    
    /**
     * Constructor for a WorkspacePersistenceManager.
     * @param workspace the workspace with which this manager is associated.
     */
    WorkspacePersistenceManager(CALWorkspace workspace) {
        this.workspace = workspace;
    }
    
    /**
     * Saves the workspace description to a stream.
     * @param descriptionOutputStream the stream to which to save the workspace description.
     * @param resourceInfo the info about resources in the workspace.
     */
    void saveWorkspaceDescription(OutputStream descriptionOutputStream, CALWorkspace.ResourceInfo resourceInfo) {
    
        // Convert the description into an XML document
        Document document = XMLPersistenceHelper.getEmptyDocument();

        // Attach the root document element.
        Element resultElement = document.createElementNS(WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.WORKSPACE_DESCRIPTION_TAG);
        document.appendChild(resultElement);

        
        // Just attach the component module descriptions to the top-level document root.
        ModuleName[] moduleNames = workspace.getModuleNames();
        int nModules = moduleNames.length;
        for (int i = 0; i < nModules; i++) {
            ModuleName moduleName = moduleNames[i];
            saveWorkspaceModuleDescription(resultElement, moduleName, resourceInfo);
        }
        
        
        // Attach namespace and schema info.
        XMLPersistenceHelper.attachNamespaceAndSchema(document, getNamespaceInfo(), WorkspacePersistenceConstants.WORKSPACE_DESCRIPTION_SCHEMA_LOCATION, WorkspacePersistenceConstants.WORKSPACE_NS);
    
        // Write the document to the metadata file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(descriptionOutputStream);
        XMLPersistenceHelper.documentToXML(document, bufferedOutputStream, false);
    }
    
    /**
     * Save the workspace-related info for a module as xml.
     * @param parentNode the node to which to save the info.
     * @param moduleName the name of the module.
     * @param resourceInfo the info about resources in the workspace.
     */
    void saveWorkspaceModuleDescription(Node parentNode, ModuleName moduleName, CALWorkspace.ResourceInfo resourceInfo) {
        Document document = (parentNode instanceof Document) ? (Document) parentNode : parentNode.getOwnerDocument();

        // Attach an element for the module.
        Element moduleElement = document.createElementNS(WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.MODULE_TAG);
        parentNode.appendChild(moduleElement);
        
        // Set the name of the module as an attribute.
        moduleElement.setAttribute(WorkspacePersistenceConstants.MODULE_NAME_ATTR, moduleName.toSourceText());
        
        // Save vault info for the module.
        VaultElementInfo info = workspace.getVaultInfo(moduleName);
        saveVaultInfo(moduleElement, info);
        
        // Add module revision.
        long moduleRevision = info.getRevision();
        XMLPersistenceHelper.addLongElementNS(moduleElement, WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.MODULE_REVISION_TAG, moduleRevision);
        
        // Save info for the individual module resources.
        saveResourceInfo(moduleElement, moduleName, resourceInfo);
    }
    
    /**
     * @return the namespace info for the workspace.
     */
    static NamespaceInfo getNamespaceInfo() {
        return new NamespaceInfo(WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.WORKSPACE_NS_PREFIX);
    }
    
    /**
     * Loads a workspace description from a stream.
     * @param descriptionInputStream the stream from which to obtain the workspace description.
     */
    boolean loadWorkspaceDescription(InputStream descriptionInputStream, Status loadStatus) {
    
        try {
            InputStream bufferedInputStream = new BufferedInputStream(descriptionInputStream);
            Document descriptionDocument = XMLPersistenceHelper.documentFromXML(bufferedInputStream);
            Element documentElement = descriptionDocument.getDocumentElement();
            
            XMLPersistenceHelper.checkTag(documentElement, WorkspacePersistenceConstants.WORKSPACE_DESCRIPTION_TAG);
            
            List<Element> moduleDescriptionNodes = XMLPersistenceHelper.getChildElements(documentElement);
            
            for (final Element moduleDescriptionElement : moduleDescriptionNodes) {
                loadWorkspaceModuleDescription(moduleDescriptionElement);
            }
            
            // TODOEL: write a schema and validate against it
            
            return true;

        } catch (BadXMLDocumentException bxde) {
            loadStatus.add(new Status(Status.Severity.ERROR, "Workspace description load failure.", bxde));
        
        } catch (DocumentConstructionException ex) {
            loadStatus.add(new Status(Status.Severity.ERROR, "The workspace description file XML is invalid.", ex));
            
        } catch (Exception ex) {
            loadStatus.add(new Status(Status.Severity.ERROR, "Exception while loading workspace description.", ex));
        }
        
        return false;
    }
    
    /**
     * Load the workspace-related info for a module as xml.
     * @param moduleDescriptionElement the element describing the module's workspace-related info.
     * @throws BadXMLDocumentException
     */
    void loadWorkspaceModuleDescription(Element moduleDescriptionElement) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(moduleDescriptionElement, WorkspacePersistenceConstants.MODULE_TAG);
        
        String moduleNameString = moduleDescriptionElement.getAttribute(WorkspacePersistenceConstants.MODULE_NAME_ATTR);
        if (moduleNameString.equals("")) {
            String errorString = "Could not obtain module from workspace description.";
            throw new BadXMLDocumentException(moduleDescriptionElement, errorString);
        }
        
        ModuleName moduleName = ModuleName.make(moduleNameString);
        
        List<Element> childElems = XMLPersistenceHelper.getChildElements(moduleDescriptionElement);
        int nChildElems = childElems.size();

        // Get the vault info element.
        Element vaultElement = (nChildElems < 1) ? null : childElems.get(0);
        XMLPersistenceHelper.checkIsElement(vaultElement);

        // Get the module revision.
        Element moduleRevisionElement = (nChildElems < 2) ? null : childElems.get(1);
        XMLPersistenceHelper.checkTag(moduleRevisionElement, WorkspacePersistenceConstants.MODULE_REVISION_TAG);
        Integer moduleRevisionNum = XMLPersistenceHelper.getElementIntegerValue(moduleRevisionElement);
        
        if (moduleRevisionNum == null) {
            throw new BadXMLDocumentException(moduleRevisionElement, "No module revision found.");
        }
        
        // Get resource info.
        Element revisionInfoElement = (nChildElems < 3) ? null : childElems.get(2);
        XMLPersistenceHelper.checkIsElement(revisionInfoElement);
        CALWorkspace.ResourceInfo resourceInfo = loadResourceInfo(revisionInfoElement);
        
        // Update the resource info.
        workspace.updateResourceInfo(resourceInfo);

        // Get the module location.
        VaultLocation storedModuleLocation = getVaultLocation(vaultElement);
        
        // Create the vault info.
        VaultElementInfo vaultInfo = VaultElementInfo.makeBasic(storedModuleLocation.getVaultDescriptor(),
                                                          moduleName.toSourceText(),
                                                          storedModuleLocation.getLocationString(),
                                                          moduleRevisionNum.intValue());
        
        // Update the info map.
        workspace.updateVaultInfo(moduleName, vaultInfo);
    }

   /**
    * Save vault info as xml.
    * @param parentElement the element to which the info should be saved.
    * @param vaultInfo the info to save.
    */
   static void saveVaultInfo(Element parentElement, VaultElementInfo vaultInfo) {
       Document document = (parentElement instanceof Document) ? (Document) parentElement : parentElement.getOwnerDocument();

       Element vaultInfoElement = document.createElementNS(WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.VAULT_INFO_TAG);
       parentElement.appendChild(vaultInfoElement);

       // Add vault type.
       String vaultDescriptor = vaultInfo.getVaultDescriptor();
       XMLPersistenceHelper.addTextElementNS(vaultInfoElement, WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.VAULT_TYPE_TAG, vaultDescriptor);


       // Add a text element with the location.  (Note: the element will have not text if the location string is null..).
       String locationString = vaultInfo.getLocationString();
       XMLPersistenceHelper.addTextElementNS(vaultInfoElement, WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.VAULT_LOCATION_TAG, locationString);
       
   }
   
   /**
    * Load a vault location from xml.
    * @param vaultInfoElement the xml element describing the vault location.
    * @return the corresponding location, or null if there isn't any.
    * @throws BadXMLDocumentException
    */
   private static VaultLocation getVaultLocation(Element vaultInfoElement) throws BadXMLDocumentException {

       XMLPersistenceHelper.checkTag(vaultInfoElement, WorkspacePersistenceConstants.VAULT_INFO_TAG);
       
       List<Element> childElems = XMLPersistenceHelper.getChildElements(vaultInfoElement);
       int nChildElems = childElems.size();

       // Get the vault descriptor string.
       Element vaultDescriptorElement = (nChildElems < 1) ? null : childElems.get(0);
       XMLPersistenceHelper.checkIsElement(vaultDescriptorElement);
       final String vaultDescriptor = XMLPersistenceHelper.getElementStringValue(vaultDescriptorElement);
       
       // Get the vault location string.
       Element locationElement = (nChildElems < 2) ? null : childElems.get(1);
       XMLPersistenceHelper.checkIsElement(locationElement);
       final String locationString = XMLPersistenceHelper.getElementStringValue(locationElement);
       
       return new VaultLocation(vaultDescriptor, locationString);
   }
   
   /**
    * Save resource info for individual module resources.
    * @param parentElement the element to which the info should be saved.
    * @param moduleName the name of the module whose info should be saved.
    * @param resourceInfo the info to save.
    */
   static void saveResourceInfo(Element parentElement, ModuleName moduleName, CALWorkspace.ResourceInfo resourceInfo) {
       Document document = (parentElement instanceof Document) ? (Document) parentElement : parentElement.getOwnerDocument();
       
       // Attach an element to hold all the resource info.
       Element resourceInfoParentElement = document.createElementNS(WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.RESOURCE_INFO_TAG);
       parentElement.appendChild(resourceInfoParentElement);

       // Get the revision and sync time info.
       ResourceRevision.Info revisionInfo = resourceInfo.getResourceRevisionInfo();
       CALWorkspace.SyncTimeInfo syncTimeInfo = resourceInfo.getResourceSyncTimeInfo();
       
       // Get the resource types.
       Set<String> resourceTypes = new LinkedHashSet<String>();
       resourceTypes.addAll(Arrays.asList(revisionInfo.getResourceTypes()));
       resourceTypes.addAll(Arrays.asList(syncTimeInfo.getResourceTypes()));
       
       // Iterate over the resource types.
       for (final String resourceType : resourceTypes) {
           Set<ResourceRevision> resourceRevisions = revisionInfo.getResourceRevisions(resourceType);
//           Set syncTimes = syncTimeInfo.getResourceSyncTimes(resourceType);
           // TODOEL: for now, we assume that anything that has a revision also has a sync time, and vice versa.
           
           // Create the individual resource info element
           Element resourceTypeElement = document.createElementNS(WorkspacePersistenceConstants.WORKSPACE_NS, resourceType);
           resourceInfoParentElement.appendChild(resourceTypeElement);
           
           for (final ResourceRevision resourceRevision : resourceRevisions) {

               long resourceRevisionNum = resourceRevision.getRevisionNumber();
               ResourceIdentifier identifier = resourceRevision.getIdentifier();
               
               // Convert to a cal feature name..
               if (!(identifier.getFeatureName() instanceof CALFeatureName)) {
                   continue;
               }
               CALFeatureName calFeatureName = (CALFeatureName)identifier.getFeatureName();
               
               if (calFeatureName.hasModuleName() && calFeatureName.toModuleName().equals(moduleName)) {

                   // Add an element to hold the info for this feature.
                   Element featureElement = document.createElementNS(WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.RESOURCE_FEATURE_TAG);
                   resourceTypeElement.appendChild(featureElement);
    
                   // Add info for the feature.
                   // Resource name.
                   identifier.getResourceName().saveXML(featureElement);
                   
                   // Revision.
                   XMLPersistenceHelper.addLongElementNS(featureElement, WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.RESOURCE_REVISION_TAG, resourceRevisionNum);
    
                   // Sync time.
                   long resourceSyncTime = syncTimeInfo.getResourceSyncTime(identifier);
                   XMLPersistenceHelper.addLongElementNS(featureElement, WorkspacePersistenceConstants.WORKSPACE_NS, WorkspacePersistenceConstants.SYNC_TIME_TAG, resourceSyncTime);
               }
           }

       }
   }
   
   /**
    * Load the resource info from the given xml element.
    * @param resourceInfoParentElement the element from which to load resource info.
    * @return resource info from the given element.
    * @throws BadXMLDocumentException if there was an error obtaining resource info from the given element.
    */
   private static CALWorkspace.ResourceInfo loadResourceInfo(Element resourceInfoParentElement)
           throws BadXMLDocumentException {

       // Check that the parent element carries the expected tag.
       XMLPersistenceHelper.checkTag(resourceInfoParentElement, WorkspacePersistenceConstants.RESOURCE_INFO_TAG);
       
       // The list of revisions.
       List<ResourceRevision> revisionList = new ArrayList<ResourceRevision>();
       
       // The list of sync times.
       List<WorkspaceResource.SyncTime> syncTimeList = new ArrayList<WorkspaceResource.SyncTime>();
       
       // Iterate through the children.
       // There will be one for each resource type.
       List<Element> revisionInfoElems = XMLPersistenceHelper.getChildElements(resourceInfoParentElement);
       for (final Element resourceTypeElement : revisionInfoElems) {
           
           // Get the name of the element.  This will be the resource type.
           String resourceType = resourceTypeElement.getLocalName();
           
           // Get its children.  These will be the features of that type.
           List<Element> resourceTypeFeatureElems = XMLPersistenceHelper.getChildElements(resourceTypeElement);

           for (final Element featureElement : resourceTypeFeatureElems) {
               XMLPersistenceHelper.checkTag(featureElement, WorkspacePersistenceConstants.RESOURCE_FEATURE_TAG);
               
               List<Element> featureChildElems = XMLPersistenceHelper.getChildElements(featureElement);
               int nFeatureChildElems = featureChildElems.size();
               
               Element featureNameElement = (nFeatureChildElems < 1) ? null : featureChildElems.get(0);
               XMLPersistenceHelper.checkIsElement(featureNameElement);
               ResourceName resourceName = ResourceName.getResourceNameWithCALFeatureNameFromXML(featureNameElement);

               Element featureRevisionNumElement = (nFeatureChildElems < 2) ? null : featureChildElems.get(1);
               XMLPersistenceHelper.checkIsElement(featureRevisionNumElement);
               final Long featureRevisionNum = XMLPersistenceHelper.getElementLongValue(featureRevisionNumElement);
               
               if (featureRevisionNum == null) {
                   throw new BadXMLDocumentException(featureRevisionNumElement, "No feature revision found.");
               }
               
               ResourceRevision metadataRevision =
                       new ResourceRevision(new ResourceIdentifier(resourceType, resourceName), featureRevisionNum.longValue());
               revisionList.add(metadataRevision);
               
               // sync time
               Element featureSyncTimeElement = (nFeatureChildElems < 3) ? null : featureChildElems.get(2);
               XMLPersistenceHelper.checkIsElement(featureSyncTimeElement);
               final Long featureSyncTimeNum = XMLPersistenceHelper.getElementLongValue(featureSyncTimeElement);
               
               if (featureSyncTimeNum == null) {
                   throw new BadXMLDocumentException(featureSyncTimeElement, "No feature sync time found.");
               }

               WorkspaceResource.SyncTime syncTime = new WorkspaceResource.SyncTime(new ResourceIdentifier(resourceType, resourceName), featureSyncTimeNum.longValue());
               syncTimeList.add(syncTime);
           }
       }

       // Create the resource info.
       ResourceRevision.Info resourceRevisionInfo = new ResourceRevision.Info(revisionList);
       CALWorkspace.SyncTimeInfo syncTimeInfo = new CALWorkspace.SyncTimeInfo(syncTimeList);
       
       return new CALWorkspace.ResourceInfo(resourceRevisionInfo, syncTimeInfo);
       
   }
   

}
