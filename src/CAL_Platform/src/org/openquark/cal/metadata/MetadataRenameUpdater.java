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
 * MetadataRenameUpdater.java
 * Created: Apr 4, 2005
 * By: Peter Cardwell
 */

package org.openquark.cal.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleContainer;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.CodeAnalyser.QualificationResults;
import org.openquark.cal.compiler.CompilerMessage.Severity;
import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.LocalizedResourceName;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceResource;


/**
 * Provides functionality to update metadata resources to reflect a renaming.
 * 
 * This class can be used to update metadata whenever a gem, type class, or type constructor
 * is renamed.
 * 
 * @author Peter Cardwell
 */
public class MetadataRenameUpdater {
    
    /**
     * Interface for listeners wishing to receive status updates from this updater.
     * @author Peter Cardwell
     */
    public static interface StatusListener {
        /**
         * Refactorer is about to use the specified resource.
         * @param resource module to be accessed
         */
        public void willUseResource(WorkspaceResource resource);
        
        /**
         * Refactorer has finished using the specified resource.
         * @param resource module which has been accessed
         */
        public void doneUsingResource(WorkspaceResource resource);
    }
    
    /**
     * Used for errors that occur during the updateMetadata operation.
     * @author Peter Cardwell
     */
    private static class RenamingException extends Exception {
        
        private static final long serialVersionUID = -6565271241591817292L;
        
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
    
    /** Status listener, if any */
    private StatusListener statusListener = null;
    
    /**
     * Constructs a new MetadataRenameUpdater
     * @param status A status object to keep track of any error messages.
     * @param typeChecker The type checker to use for the renaming of code expressions (ie. in code gems).
     * @param newName The new name of the identifier after it was renamed.
     * @param oldName The old name of the identifier before it was renamed.
     * @param category The category of the identifier.
     */
    public MetadataRenameUpdater(Status status, TypeChecker typeChecker, QualifiedName newName, QualifiedName oldName, SourceIdentifier.Category category) {
        this.status = status;
        this.typeChecker = typeChecker;
        this.newName = newName;
        this.oldName = oldName;
        this.category = category;
    }
    
    /**
     * Returns the new CALFeatureName that the given metadata should be updated with, or
     * if updating is not necessary, the old metadata's featureName.
     */
    private CALFeatureName getNewMetadataFeatureName(CALFeatureMetadata metadata) {
        // If we are renaming a module, we have to update the module name of metadata for all CALFeatures within that module.
        if (category == SourceIdentifier.Category.MODULE_NAME) {
            if (metadata.getFeatureName().getType() == CALFeatureName.MODULE) {
                if (oldName.getUnqualifiedName().equals(metadata.getFeatureName().getName())) {
                    return CALFeatureName.getModuleFeatureName(newName.getModuleName());
                } 
            } else {
                QualifiedName oldMetadataName = QualifiedName.makeFromCompoundName(metadata.getFeatureName().getName());
                if (oldName.getModuleName().equals(oldMetadataName.getModuleName())) {
                    QualifiedName newMetadataName = QualifiedName.make(newName.getModuleName(), oldMetadataName.getUnqualifiedName());
                    return new CALFeatureName(metadata.getFeatureName().getType(), newMetadataName.getQualifiedName());
                }
            }
        // If we are not renaming a module, we only need to rename the metadata if is corresponds to the entity which we are renaming. 
        } else {
            if (metadata.getFeatureName().getType() != CALFeatureName.MODULE) {
                QualifiedName oldMetadataName = QualifiedName.makeFromCompoundName(metadata.getFeatureName().getName());
                if (oldName.equals(oldMetadataName)) {
                   return new CALFeatureName(metadata.getFeatureName().getType(), newName.getQualifiedName());
                }
            }
        }
        return metadata.getFeatureName();
    }
    
    /**
     * Updates the examples for the given metadata object to reflect the renaming.
     * @return true if any examples were changed, false otherwise
     */
    private boolean updateExamples(FunctionalAgentMetadata functionalAgentMetadata, ModuleContainer moduleContainer) {
        // Get the array of examples and then loop through them and update them if necessary
        CALExample[] examples = functionalAgentMetadata.getExamples();
        boolean examplesChanged = false;
        for (int i = 0, n = examples.length; i < n; i++) {
            CALExample curExample = examples[i];
            CALExpression oldExpression = curExample.getExpression();
            
            // Get the qualified version of the new string
            ModuleTypeInfo moduleTypeInfo = moduleContainer.getModuleTypeInfo(oldExpression.getModuleContext());
            if (moduleTypeInfo == null) {
                // The module is not in the workspace.
                String featureName = functionalAgentMetadata.getFeatureName().getName();
                ModuleName moduleContext = oldExpression.getModuleContext();
                String errorMessage = "Can not update the metadata for " + featureName + ", example " + (i + 1) + 
                                      ", since its module context " + moduleContext + " is not in the workspace.";
                
                // Add a warning.
                // Unfortunately, warnings are ignored.  
                // However, throwing a RenamingException will cause renaming to fail, which is not what we want.
                status.add(new Status(Status.Severity.WARNING, errorMessage));
                continue;
                
//                throw new RenamingException(errorMessage);
            }
            
            ModuleNameResolver oldExpressionContextModuleNameResolver = moduleTypeInfo.getModuleNameResolver();
            String newExpressionString = typeChecker.calculateUpdatedCodeExpression(oldExpression.getExpressionText(), oldExpression.getModuleContext(), oldExpressionContextModuleNameResolver, oldExpression.getQualificationMap(), oldName, newName, category, null);
            
            CodeAnalyser codeAnalyser = new CodeAnalyser(typeChecker, moduleTypeInfo, false, false);
            MessageLogger logger = new MessageLogger();
            QualificationResults qualificationResults = codeAnalyser.qualifyExpression(newExpressionString, null, oldExpression.getQualificationMap(), logger); 
            if (qualificationResults == null) {
                // failed to parse.
                String featureName = functionalAgentMetadata.getFeatureName().getName();
                String errorMessage = 
                    "Can not update the metadata for " + featureName + ", example " + (i + 1) + ", since it could not be parsed.\n" +
                    "Compiler messages (if any): " + logger.getCompilerMessages(Severity.ERROR);
                
                status.add(new Status(Status.Severity.WARNING, errorMessage));
                continue;
            }
            
            String qualifiedNewExpressionString = qualificationResults.getQualifiedCode();
            
            // Update the qualification map
            CodeQualificationMap newQualifiedMap = oldExpression.getQualificationMap();                        
            boolean qualificationMapWasUpdated = updateQualificationMap(newQualifiedMap);
            boolean moduleContextNeedsUpdating =
                (category == Category.MODULE_NAME && oldName.getModuleName().equals(oldExpression.getModuleContext()));
            
            if (!newExpressionString.equals(oldExpression.getExpressionText()) || qualificationMapWasUpdated || moduleContextNeedsUpdating) {
                // If changes were made, create a new expression and save the example.
                
                ModuleName newModuleContext;
                if (moduleContextNeedsUpdating) {
                    newModuleContext = newName.getModuleName();
                } else {
                    newModuleContext = oldExpression.getModuleContext();
                }
                CALExpression newExpression = new CALExpression(newModuleContext, newExpressionString, newQualifiedMap, qualifiedNewExpressionString);
                CALExample newExample = new CALExample(newExpression, curExample.getDescription(), curExample.evaluateExample());
                examples[i] = newExample;
                examplesChanged = true;
                
            }
        }
        if (examplesChanged) {
            // Replace the metadata's examples with the new contents of the examples array and saave the metadata
            functionalAgentMetadata.setExamples(examples);
            return true;
        }
        return false;
    }
    
    /**
     * Updates all the metadata files stored by the given workspace to reflect the renaming.
     * @param moduleContainer The workspace containing all the metadatas the user wishes to update
     * @return True if any metadata was updated 
     */
    public boolean updateMetadata(ModuleContainer moduleContainer) {
        List<CALFeatureMetadata> undoList = new ArrayList<CALFeatureMetadata>(); // A list of metadata files that have been updated and should be undone if an error is encountered.
        try {
            ModuleName[] workspaceModuleNames = moduleContainer.getModuleNames();
            
            // loop through each module in the workspace
            for (final ModuleName moduleName : workspaceModuleNames) {
                
                MetadataManager metadataManager = (MetadataManager) moduleContainer.getResourceManager(moduleName, WorkspaceResource.METADATA_RESOURCE_TYPE);
                
                // Loop over all metadata resources for the module in the workspace and get their corresponding metadata file.
                for (Iterator<WorkspaceResource> it = ((MetadataStore)metadataManager.getResourceStore()).getResourceIterator(moduleName); it.hasNext(); ) {
                    boolean metadataUpdated = false;
                    
                    WorkspaceResource metadataResource = it.next();
                    if (statusListener != null) {
                        statusListener.willUseResource(metadataResource);
                    }
                    
                    ResourceName oldMetadataResourceName = metadataResource.getIdentifier().getResourceName();
                    Locale metadataLocale = LocalizedResourceName.localeOf(oldMetadataResourceName);
                    
                    // We want to keep a copy of the old metadata without any changes made to it so we can restore it later if an error occurs.
                    CALFeatureMetadata oldMetadata = metadataManager.getMetadata((CALFeatureName)oldMetadataResourceName.getFeatureName(), metadataLocale);
                    
                    // Make sure the metadata's module is in the current workspace
                    ModuleName metadataModuleName = oldMetadata.getFeatureName().toModuleName();
                    if (moduleContainer.containsModule(metadataModuleName)) {
                        continue;
                    }
                    
                    CALFeatureName newMetadataFeatureName = getNewMetadataFeatureName(oldMetadata);               
                    CALFeatureMetadata newMetadata = oldMetadata.copy(newMetadataFeatureName, metadataLocale);
                    if (!newMetadataFeatureName.equals(oldMetadata.getFeatureName())) {
                        metadataUpdated = true;
                    }
                    
                    // Try to update the examples if the metadata is for a functional agent. 
                    if (newMetadata instanceof FunctionalAgentMetadata) {
                        if (updateExamples((FunctionalAgentMetadata)newMetadata, moduleContainer)) {
                            metadataUpdated = true;
                        }
                    }
                    
                    if (metadataUpdated) {
                        if (!metadataManager.getResourceStore().isWriteable(oldMetadataResourceName)) {
                            throw new RenamingException("Can not update the metadata for " + newMetadata.getFeatureName().getName() + " because it is not writeable.");
                        }
                        
                        Status saveMetadataStatus = new Status("Save metadata status");
                        metadataManager.saveMetadata(newMetadata, oldMetadata.getFeatureName(), metadataLocale, saveMetadataStatus);
                        if (saveMetadataStatus.getSeverity().equals(Status.Severity.ERROR)) {
                            throw new RenamingException("Error saving the updated metadata for " + newMetadata.getFeatureName().getName() + ".");
                        }
                        
                        undoList.add(oldMetadata);
                    }
                    
                    if (statusListener != null) {
                        statusListener.doneUsingResource(metadataResource);
                    }
                }
            }
            
            // Return true if the undoList has contents (ie. changes have been made)
            return !undoList.isEmpty();
        
        } catch (RenamingException e) {
            status.add(new Status(Status.Severity.ERROR, e.getErrorMessage()));
            
            for (int i = undoList.size() - 1; i >= 0; i--) {
                CALFeatureMetadata metadata = undoList.get(i);
                
                Status undoStatus = new Status("Undo metadata update status");
                moduleContainer.saveMetadata(metadata, undoStatus); 
                
                if (undoStatus.getSeverity().equals(Status.Severity.ERROR)) {
                    String msg = "FATAL: Error undoing the metadata update for " + metadata.getDisplayName();
                    status.add(new Status(Status.Severity.ERROR, msg));
                }
            }
            return false;
        }
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
            for (final String unqualifiedName : unqualifiedNames) {
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
    
    /** Set status listener to use */
    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }
}
