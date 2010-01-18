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
 * CALWorkspace.java
 * Creation date: Apr 28, 2003.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassInstanceIdentifier;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.ModuleContainer;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleSourceDefinitionGroup;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.SourceMetrics;
import org.openquark.cal.compiler.SourceMetricsManager;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.ProgramModifier.ProgramChangeListener;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.MetadataManager;
import org.openquark.cal.metadata.ScopedEntityMetadata;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.ResourceAccess;
import org.openquark.cal.services.Vault.VaultProvider;
import org.openquark.cal.services.WorkspaceResource.SyncTime;
import org.openquark.util.FileSystemHelper;
import org.w3c.dom.Document;


/**
 * A CALWorkspace represents a set of modules from which a program can be compiled.
 * A metaprogram encapsulates a compiled program and all related metadata.  It holds a bunch of metamodules.
 * <p>
 * A particular workspace within an environment can be located by its discrete workspace id.
 * ie. a workspace instantiated with a particular id will point to the location of any previous workspace instantiated with that id.
 * A special case is where the id is null, in which case the workspace is considered "nullary".
 * This means that the workspace location coincides with the StandardVault.
 * 
 * @author Edward Lam
 */
public abstract class CALWorkspace implements ResourceAccess, ProgramChangeListener{
    /*
     * TODOEL:
     *   1) add a workspace listener to listen to changes in the workspace composition? (ie. add / remove modules).
     *   2) revert a modification (add module, revert..)
     */
    
    /*
     * Logging
     */
    
    /** The namespace for log messages from this package. */
    public static final String SERVICES_LOGGER_NAMESPACE = CALWorkspace.class.getPackage().getName();
    
    /** An instance of a Logger for messages from this package. */
    static final Logger SERVICES_LOGGER = Logger.getLogger(SERVICES_LOGGER_NAMESPACE);
    
    /*
     * Members
     */
    
    /** The registry which contains the registered vault providers. */
    private final VaultRegistry vaultRegistry = new VaultRegistry();
    
    /** Map from module name to module */
    private final Map<ModuleName, MetaModule> nameToMetaModuleMap;

    /** the list of metamodules in this program,
     *  in the order in which they were originally added. */
    private final List<MetaModule> metaModuleList = new ArrayList<MetaModule>();
    
    /**
     * the first module name encountered when the workspace declaration file are loaded.
     */
    private ModuleName firstModule;

    /** The ModuleSourceDefinitionGroup for the current Workspace. */
    private ModuleSourceDefinitionGroup sourceDefinitionGroup;
    
    /** The resource path to the workspace description file. */
    private final ResourcePath.FilePath workspaceDescriptionFile;
    
    /** The virtual resource manager. */
    private final VirtualResourceManager virtualResourceManager;
    
    /** Map from module name to the vault info for that module. */
    private final Map<ModuleName, VaultElementInfo> moduleNameToVaultInfoMap = new HashMap<ModuleName, VaultElementInfo>();

    /** The revision info for the resources for modules in the workspace.
     *  Does not contain info for StandardVault modules if workspace is nullary. */
    private final ResourceRevision.Info resourceRevisionInfo = new ResourceRevision.Info();

    /** Sync time info for resources managed by this manager. */
    private final SyncTimeInfo resourceSyncTimeInfo = new SyncTimeInfo();
    
    /** The persistence manager for the workspace. */
    private final WorkspacePersistenceManager persistenceManager;

    /** The source metrics for the workspace */
    private final SourceMetricsManager sourceMetrics;
    
    /** The module container for this workspace */
    private final ModuleContainer moduleContainer;
    
    
    /**
     * A simple container class to hold info about a managed resource.
     * @author Edward Lam
     */
    static class ResourceInfo {
        private final ResourceRevision.Info resourceRevisionInfo;
        private final SyncTimeInfo resourceSyncTimeInfo;
    
        ResourceInfo(ResourceRevision.Info resourceRevisionInfo, SyncTimeInfo resourceSyncTimeInfo) {
            this.resourceRevisionInfo = resourceRevisionInfo;
            this.resourceSyncTimeInfo = resourceSyncTimeInfo;
        }
        
        /**
         * @return the resource RevisionInfo.
         */
        public ResourceRevision.Info getResourceRevisionInfo() {
            return resourceRevisionInfo;
        }
        /**
         * @return Returns the resourceSyncTimeInfo.
         */
        public SyncTimeInfo getResourceSyncTimeInfo() {
            return resourceSyncTimeInfo;
        }
    }

    /**
     * A class to track sync time for managed module resources.
     * @author Edward Lam
     */
    static class SyncTimeInfo {
    
        /**
         * The map which tracks resource sync times.
         * Map from resource type to a map for that type.
         * The map for the type maps ResourceName to SyncTime for that type.
         */
        private final Map<String, Map<ResourceName,SyncTime>> resourceSyncTimeMap = new HashMap<String, Map<ResourceName,SyncTime>>();
        
        /**
         * Constructor for an empty SyncTimeInfo.
         */
        public SyncTimeInfo() {
        }
    
        /**
         * Constructor for a SyncTimeInfo.
         * @param syncTimeList the associated SyncTimes
         */
        public SyncTimeInfo(List<SyncTime> syncTimeList) {
            updateResourceSyncTimeInfo(syncTimeList);
        }
    
        /**
         * Update some sync time info in this object.
         * @param resourceSyncTime an updated sync time.
         */
        void updateResourceSyncTime(WorkspaceResource.SyncTime resourceSyncTime) {
            String resourceType = resourceSyncTime.getResourceType();
            ResourceName resourceName = resourceSyncTime.getIdentifier().getResourceName();
            
            Map<ResourceName, SyncTime> resourceTypeMap = resourceSyncTimeMap.get(resourceType);
            if (resourceTypeMap == null) {
                resourceTypeMap = new HashMap<ResourceName, SyncTime>();
                resourceSyncTimeMap.put(resourceType, resourceTypeMap);
            }
            
            resourceTypeMap.put(resourceName, resourceSyncTime);
        }
        
        /**
         * Update some sync time info in this object.
         * @param updatedInfo some updated sync time info.
         */
        void updateResourceSyncTimeInfo(SyncTimeInfo updatedInfo) {
            for (final String updatedFeatureType : updatedInfo.resourceSyncTimeMap.keySet()) {
                Map<ResourceName,SyncTime> updatedFeatureNameToSyncTimeMap = updatedInfo.resourceSyncTimeMap.get(updatedFeatureType);
    
                for (final WorkspaceResource.SyncTime updatedSyncTime : updatedFeatureNameToSyncTimeMap.values()) {
                    updateResourceSyncTime(updatedSyncTime);
                }
            }
        }
        
        /**
         * Update some sync time info in this object.
         * @param updatedInfoList the info with which to update.
         */
        void updateResourceSyncTimeInfo(List<SyncTime> updatedInfoList) {
            for (final SyncTime resourceSyncTime : updatedInfoList) {
                updateResourceSyncTime(resourceSyncTime);
            }
        }
    
        /**
         * @return the resource types in this object.
         */
        public String[] getResourceTypes() {
            Set<String> resourceTypeSet = resourceSyncTimeMap.keySet();
            String[] returnVal = new String[resourceTypeSet.size()];
            return resourceTypeSet.toArray(returnVal);
        }
    
        /**
         * Get the resource sync time for a given module resource.
         * @param resourceIdentifier the identifier of the resource.
         * @return the sync time of that resource, or 0L if that resource is not in this object.
         */
        public long getResourceSyncTime(ResourceIdentifier resourceIdentifier) {
            Map<ResourceName,SyncTime> resourceTypeMap = resourceSyncTimeMap.get(resourceIdentifier.getResourceType());
            if (resourceTypeMap == null) {
                return 0L;
            }
            
            WorkspaceResource.SyncTime resourceSyncTime = resourceTypeMap.get(resourceIdentifier.getResourceName());
            if (resourceSyncTime == null) {
                return 0L;
            }
            
            return resourceSyncTime.getSyncTime();
        }
        
        /**
         * Get the resource sync times for a given resource type.
         * @param resourceType the type for which the resource sync times should be returned.
         * @return the resource sync times for that type.
         */
        public Set<SyncTime> getResourceSyncTimes(String resourceType) {
            Map<ResourceName,SyncTime> resourceTypeMap = resourceSyncTimeMap.get(resourceType);
            if (resourceTypeMap == null) {
                return null;
            }
            return new HashSet<SyncTime>(resourceTypeMap.values());
        }
    
        /**
         * @param resourceIdentifier the identifier of the resource whose sync info should be removed.
         * @return true if there was a sync info to remove.
         */
        public boolean removeResourceSyncTime(ResourceIdentifier resourceIdentifier) {
            Map<ResourceName,SyncTime> resourceTypeMap = resourceSyncTimeMap.get(resourceIdentifier.getResourceType());
            if (resourceTypeMap != null) {
                return resourceTypeMap.remove(resourceIdentifier.getResourceName()) != null;
            }
            return false;
            
        }
        
        /**
         * @param moduleName the name of the module whose info to remove
         */
        public void removeModule(ModuleName moduleName) {
            
            // Iterate over the maps for the resource types.
            for (final Map<ResourceName,SyncTime> resourceNameToSyncTimeMap : resourceSyncTimeMap.values()) {
                // Iterate over the mappings in the map for that resource type.
                for (Iterator<ResourceName> it = resourceNameToSyncTimeMap.keySet().iterator(); it.hasNext(); ) {
                    ResourceName resourceName = it.next();
                    CALFeatureName featureName = (CALFeatureName)resourceName.getFeatureName();
    
                    // Check that the feature name has a module name which is the same as the one we're looking for.
                    if (featureName.hasModuleName() && featureName.toModuleName().equals(moduleName)) {
                        it.remove();
                    }
                }
            }
        }
    
        /**
         * Clear all sync time info from this object.
         */
        void clear() {
            resourceSyncTimeMap.clear();
        }
    }

    /**
     * Warning- this class should only be used by the CAL services implementation. It is not part of the
     * external API of the CAL platform.
     * <p>
     * A class to represent the results of a sync operation.
     * @author Edward Lam
     */
    public static class SyncInfo {

        /** Module resources which were updated. */
        private final Set<ResourceIdentifier> updatedResourceIdentifierSet = new HashSet<ResourceIdentifier>();
        
        /** Module resources which were not updated because the workspace resource was modified.
         *   Merge is required.*/
        private final Set<ResourceIdentifier> conflictSet = new HashSet<ResourceIdentifier>();
        
        /** Module resources which were not updated because the import operation failed. */
        private final Set<ResourceIdentifier> importFailureSet = new HashSet<ResourceIdentifier>();
        
        /** Module resources which were deleted . */
        private final Set<ResourceIdentifier> deletedResourceSet = new HashSet<ResourceIdentifier>();
        
        /**
         * Constructor for a SyncInfo.
         */
        public SyncInfo() {
        }
        
        /**
         * Augment the sync info with other sync info.
         * @param info the other sync info object with which this object will be updated.
         */
        public void addInfo(SyncInfo info) {
            updatedResourceIdentifierSet.addAll(info.updatedResourceIdentifierSet);
            conflictSet.addAll(info.conflictSet);
            importFailureSet.addAll(info.importFailureSet);
            deletedResourceSet.addAll(info.deletedResourceSet);
        }

        void addUpdatedResource(ResourceIdentifier identifier) {
            updatedResourceIdentifierSet.add(identifier);
        }
        
        void addSyncConflict(ResourceIdentifier identifier) {
            conflictSet.add(identifier);
        }
        
        void addImportFailure(ResourceIdentifier identifier) {
            importFailureSet.add(identifier);
        }
        
        void addDeletedResource(ResourceIdentifier identifier) {
            deletedResourceSet.add(identifier);
        }

        /**
         * @return Module resources which were updated.
         */
        public Set<ResourceIdentifier> getUpdatedResourceIdentifiers() {
            return new HashSet<ResourceIdentifier>(updatedResourceIdentifierSet);
        }
        
        /**
         * @return Module resources which were not updated because the workspace resource was modified.
         *   Merge is required.
         */
        public Set<ResourceIdentifier> getSyncConflictIdentifiers() {
            return new HashSet<ResourceIdentifier>(conflictSet);
        }
        
        /**
         * @return Module resources which were not updated because the import operation failed. */
        public Set<ResourceIdentifier> getResourceImportFailures() {
            return new HashSet<ResourceIdentifier>(importFailureSet);
        }
        
        /**
         * @return Module resources which were deleted. */
        public Set<ResourceIdentifier> getDeletedResourceIdentifiers() {
            return new HashSet<ResourceIdentifier>(deletedResourceSet);
        }
    }
    
    /**
     * Constructor for a CALWorkspace
     * @param workspaceDescriptionFile the file containing the workspace description.
     *   This will be used with the workspace is saved and loaded.
     */
    protected CALWorkspace(ResourcePath.FilePath workspaceDescriptionFile) {
        this.nameToMetaModuleMap = new HashMap<ModuleName, MetaModule>();
        this.persistenceManager = new WorkspacePersistenceManager(this);
        this.moduleContainer = new ModuleContainer () {
            @Override
            public int getNModules() {
                return CALWorkspace.this.getNMetaModules();
            }
            
            @Override
            public ModuleTypeInfo getNthModuleTypeInfo(int i) {
                return CALWorkspace.this.getNthMetaModule(i).getModule().getModuleTypeInfo();
            }
            
            @Override
            public ModuleTypeInfo getModuleTypeInfo(ModuleName moduleName) {
                return CALWorkspace.this.getMetaModule(moduleName).getModule().getModuleTypeInfo();
            }

            @Override
            public ModuleSourceDefinition getSourceDefinition(ModuleName moduleName) {
                return CALWorkspace.this.getSourceDefinition(moduleName);
            }

            @Override
            public boolean containsModule(ModuleName moduleName) {
                return CALWorkspace.this.containsModule(moduleName);
            }

            @Override
            public ResourceManager getResourceManager(ModuleName moduleName, String resourceType) {
                return CALWorkspace.this.getResourceManager(moduleName, resourceType);
            }

            @Override
            public boolean saveMetadata(CALFeatureMetadata metadata, Status saveStatus) {
                return CALWorkspace.this.saveMetadata(metadata, saveStatus);
            }

            @Override
            public boolean renameFeature(CALFeatureName oldFeatureName, CALFeatureName newFeatureName, Status renameStatus) {
                return CALWorkspace.this.renameFeature(oldFeatureName, newFeatureName, renameStatus);
            }

            @Override
            public ISourceManager getSourceManager(final ModuleName moduleName) {
                return
                new ISourceManager(){
                    private final CALSourceManager sourceManager = CALWorkspace.this.getSourceManager(moduleName);

                    public boolean isWriteable(ModuleName moduleName) {
                        return sourceManager.isWriteable(moduleName);
                    }
                    
                    public String getSource(ModuleName moduleName, CompilerMessageLogger messageLogger){
                        ModuleSourceDefinition moduleSourceDefinition = sourceManager.getSource(moduleName, new Status("getting module source"));
                        return moduleSourceDefinition.getSourceText(new Status("reading source for refactoring"), messageLogger);
                    }

                    public boolean saveSource(ModuleName moduleName, String moduleDefinition, Status saveStatus) {
                        return sourceManager.saveSource(moduleName, moduleDefinition, saveStatus);
                    }
                };
            }
        };

        this.sourceMetrics = new SourceMetricsManager(moduleContainer);
        
        this.workspaceDescriptionFile = workspaceDescriptionFile;

        this.virtualResourceManager = new VirtualResourceManager();
    }
    
    /**
     * @return the workspace id.  Null for a nullary workspace.
     */
    public abstract String getWorkspaceID();
    
    /**
     * @return a readable string identifying the location of the workspace.
     */
    public abstract String getWorkspaceLocationString();

    /**
     * @return whether this is the "nullary" workspace.
     * What this means is that the workspace points to the same location as the StandardVault.
     */
    private boolean isNullary() {
        return getWorkspaceID() == null;
    }

    public ModuleContainer asModuleContainer(){
        return moduleContainer;
    }
    
    /**
     * Get the ModuleSourceDefinitionGroup for the current workspace.
     * @return ModuleSourceDefinitionGroup the source definitions for this workspace.
     */
    synchronized final ModuleSourceDefinitionGroup getSourceDefinitionGroup() {
        return sourceDefinitionGroup;
    }
    
    /**
     * Returns a ProgramResourceRepository.Provider that provides Car-aware ProgramResourceRepository instances.
     * 
     * @param baseProvider the base repository for use by the Car-aware ProgramResourceRepository when
     *                     a module name is not found in the Car mapping.
     * 
     * @return the provider for Car-aware ProgramResourceRepository instances.
     */
    ProgramResourceRepository.Provider getProviderForCarAwareProgramResourceRepository(ProgramResourceRepository.Provider baseProvider) {
        return virtualResourceManager.getProviderForCarAwareProgramResourceRepository(baseProvider);
    }

    /**
     * Add a module to this program.
     * @param module the module to add.
     */
    synchronized private final void addMetaModule(MetaModule module) {

        Assert.isNotNullArgument(module, "module");

        nameToMetaModuleMap.put(module.getName(), module);
        metaModuleList.add(module);
    }

    /**
     * Remove a named module from this program.
     * @param moduleName the name of the module to remove
     */
    synchronized private final MetaModule removeMetaModule(ModuleName moduleName) {
        MetaModule module = nameToMetaModuleMap.remove(moduleName);
        metaModuleList.remove(module);
        return module;
    }

    /**
     * Gets the module with a given moduleName.
     * @param moduleName the module name
     * @return the module with a given module name
     */
    synchronized public final MetaModule getMetaModule(ModuleName moduleName) {
        // Get the module object with the given the module name
        return nameToMetaModuleMap.get(moduleName);
    }

    /**
     * Get the number of modules in this program
     * @return int the number of modules held by this program.
     */
    synchronized public final int getNMetaModules() {
        return metaModuleList.size();
    }
    
    /**
     * Get the nth metamodule.
     * The index n is based on the order in which modules are originally added to the program.
     * @param n 0-based index of the module to return.
     * @return MetaModule the module.
     */
    synchronized public final MetaModule getNthMetaModule(int n) {
        return metaModuleList.get(n);
    }

    /**
     * Get an entity by name.
     * @param entityName the name of the entity to retrieve.
     * @return the corresponding entity, or null if the entity or module do not exist.
     */
    public GemEntity getGemEntity(QualifiedName entityName) {
        Assert.isNotNullArgument(entityName, "entityName");

        MetaModule module = getMetaModule(entityName.getModuleName());
        
        if (module == null) {
            return null;
        }
        
        return module.getGemEntity(entityName.getUnqualifiedName());
    }
    
    /**
     * Get a scoped entity by name.
     * @param featureName the name to get a scoped entity for
     * @return the scoped entity for the name or null if no such entity
     * @throws IllegalArgumentException if the feature name is not for a scoped entity
     */
    public ScopedEntity getScopedEntity(CALFeatureName featureName) {
        
        QualifiedName name = featureName.toQualifiedName();
        MetaModule metaModule = getMetaModule(name.getModuleName());
        
        if (metaModule == null) {
            return null;
        }
        
        ModuleTypeInfo moduleInfo = metaModule.getTypeInfo();
        CALFeatureName.FeatureType type = featureName.getType();
        
        if (type == CALFeatureName.FUNCTION) {
            return moduleInfo.getFunctionalAgent(name.getUnqualifiedName());
            
        } else if (type == CALFeatureName.TYPE_CONSTRUCTOR) {
            return moduleInfo.getTypeConstructor(name.getUnqualifiedName());
            
        } else if (type == CALFeatureName.TYPE_CLASS) {
            return moduleInfo.getTypeClass(name.getUnqualifiedName());
            
        } else if (type == CALFeatureName.DATA_CONSTRUCTOR) {
            return moduleInfo.getDataConstructor(name.getUnqualifiedName());
            
        } else if (type == CALFeatureName.CLASS_METHOD) {
            return moduleInfo.getClassMethod(name.getUnqualifiedName());
        }
        
        throw new IllegalArgumentException("feature type is not a for a scoped entity: " + type);
    }
    
    /**
     * Get a class instance by name.
     * @param featureName the name to get a class instance for
     * @return the class instance for the name or null if the name does not represent a class instance, or if there is no such instance
     * @throws IllegalArgumentException if the feature name is not for a class instance
     */
    public ClassInstance getClassInstance(CALFeatureName featureName) {
        
        ModuleName moduleName = featureName.toModuleName();
        ClassInstanceIdentifier identifier = featureName.toInstanceIdentifier();
        
        MetaModule metaModule = getMetaModule(moduleName);
        
        if (metaModule == null) {
            return null;
        }
        
        return metaModule.getTypeInfo().getClassInstance(identifier);
    }

    /**
     * Saves the given source definition to the manager.
     * @param sourceDefinition the source definition to save.
     * @param saveStatus the tracking status object.
     * @return whether the source was successfully saved.
     */
    private boolean saveSource(ModuleSourceDefinition sourceDefinition, Status saveStatus) {
        CALSourceManager sourceManager = virtualResourceManager.getSourceManager(sourceDefinition.getModuleName());
        if (sourceManager == null) {
            saveStatus.add(new Status(Status.Severity.WARNING, "No registered source manager."));
            return false;
        }
        return sourceManager.importResource(sourceDefinition, saveStatus) != null;
    }

    /**
     * @param moduleName the name of the module whose resource manager is to be returned.
     * @param resourceType the resource type identifier.
     * @return the manager for that resource type and for the given module in the workspace.
     */
    public ResourceManager getResourceManager(ModuleName moduleName, String resourceType) {
        return virtualResourceManager.getModuleSpecificResourceManager(moduleName, resourceType);
    }
    
    /**
     * @return the registered workspace manager, if any.
     */
    public WorkspaceDeclarationManager getWorkspaceDeclarationManager() {
        return virtualResourceManager.getWorkspaceDeclarationManager();
    }
    
    /**
     * Get the metadata for a scoped entity.
     * @param entity the entity to get metadata for
     * @param locale the locale associated with the metadata.
     * @return the metadata for the entity. If the entity has no metadata, then default metadata is returned.
     */
    public ScopedEntityMetadata getMetadata(ScopedEntity entity, Locale locale) {
        if (entity instanceof FunctionalAgent) {
            GemEntity gemEntity = getGemEntity(entity.getName());
            if (gemEntity == null) {
                throw new IllegalStateException("The given entity does not exist in the workspace.");
            }
            return gemEntity.getMetadata(locale);

        } else if (entity instanceof TypeClass) {
            return (ScopedEntityMetadata)getMetadata(CALFeatureName.getTypeClassFeatureName(entity.getName()), locale);

        } else if (entity instanceof TypeConstructor) {
            return (ScopedEntityMetadata)getMetadata(CALFeatureName.getTypeConstructorFeatureName(entity.getName()), locale);

        } else {
            throw new UnsupportedOperationException("Unknown entity class: " + entity.getClass());
        }
    }

    /**
     * @param featureName the name of the feature to get metadata for.  This feature must live in a module in this workspace.
     * @param locale the locale associated with the metadata.
     * @return the metadata for the feature. If the feature has no metadata, then default
     * metadata is returned.
     */
    public CALFeatureMetadata getMetadata(CALFeatureName featureName, Locale locale) {
        MetaModule metaModule = getMetaModule(featureName.toModuleName());
        if (metaModule == null) {
            throw new IllegalStateException("The module for the given entity does not exist in the workspace.");
        }

        MetadataManager metadataManager = virtualResourceManager.getMetadataManager(featureName.toModuleName());
        if (metadataManager == null) {
            return MetadataManager.getEmptyMetadata(featureName, locale);
        }
        
        return metadataManager.getMetadata(featureName, locale);
    }
    
    /**
     * {@inheritDoc}
     */
    public InputStream getUserResource(String moduleNameAsString, String name, String extension, Locale locale) {
        ModuleName moduleName = ModuleName.make(moduleNameAsString);
        UserResourceManager userResourceManager = virtualResourceManager.getUserResourceManager(moduleName);
        return userResourceManager.getUserResource(moduleName, name, extension, locale);
    }
    
    /**
     * Returns the resource manager for user resources of the specified module.
     * @param moduleName the module name.
     * @return the resource manager for user resources of the specified module.
     */
    UserResourceManager getUserResourceManager(final ModuleName moduleName) {
        return virtualResourceManager.getUserResourceManager(moduleName);
    }
    
    /**
     * Used for errors that occur during the renameFeature() operation.
     * @author Peter Cardwell
     */
    private static final class RenamingException extends Exception {
        
        private static final long serialVersionUID = 3985993651784449695L;
               
        RenamingException (String errorMsg) {
            super(errorMsg);
        }
        
        String getErrorMessage() {
            return getMessage();
        }
    }
    
    /**
     * A helper method to collect a list of design resources that should be renamed.
     * @param renamings
     * @param oldFeatureName
     * @param newFeatureName
     * @param renameStatus
     */
    private void collectDesignRenamings(List<Renaming> renamings, CALFeatureName oldFeatureName, CALFeatureName newFeatureName, Status renameStatus) {
        GemDesignManager oldDesignManager = virtualResourceManager.getDesignManager(oldFeatureName.toModuleName());
        if (oldDesignManager == null) {
            renameStatus.add(new Status(Status.Severity.WARNING, "The design manager for the old design does not exist."));
            return;
        }
        
        GemDesignManager newDesignManager = virtualResourceManager.getDesignManager(newFeatureName.toModuleName());
        if (newDesignManager == null) {
            renameStatus.add(new Status(Status.Severity.WARNING, "The design manager for the new design does not exist."));
            return;
        }
        
        ResourceName oldResourceName = new ResourceName(oldFeatureName);
        if (oldFeatureName.getType().equals(CALFeatureName.MODULE)) {
            MetaModule metaModule = getMetaModule(oldFeatureName.toModuleName());
            if (metaModule == null) {
                return; //throw new IllegalStateException("The module for the given entity does not exist in the workspace.");
            }
            
            for (int i = 0, n = metaModule.getNGemEntities(); i < n; i++) {
                GemEntity nextGemEntity = metaModule.getNthGemEntity(i);
                
                if (oldDesignManager.hasGemDesign(nextGemEntity)) {
                    QualifiedName nextEntityName = nextGemEntity.getName();
                    if (!nextEntityName.getModuleName().equals(oldFeatureName.toModuleName())) {
                        throw new IllegalStateException("Feature name associated with, but not contained in, the renamed module.");
                    }
                    QualifiedName nextEntityNameRenamed = QualifiedName.make(newFeatureName.toModuleName(), nextEntityName.getUnqualifiedName());
                    
                    CALFeatureName nextFeatureName = CALFeatureName.getFunctionFeatureName(nextEntityName);
                    CALFeatureName nextFeatureNameRenamed = CALFeatureName.getFunctionFeatureName(nextEntityNameRenamed);
                    
                    renamings.add(new Renaming(new ResourceName(nextFeatureName), new ResourceName(nextFeatureNameRenamed), "design", oldDesignManager, newDesignManager));
                }
            }
        } else if (oldFeatureName.getType() == CALFeatureName.FUNCTION && oldDesignManager.getResourceStore().hasFeature(oldResourceName)) {
            renamings.add(new Renaming(oldResourceName, new ResourceName(newFeatureName), "design", oldDesignManager, newDesignManager));
        }
    }
    
    /**
     * A helper method to collect a list of user resources that should be renamed.
     * @param renamings
     * @param oldFeatureName
     * @param newFeatureName
     * @param renameStatus
     */
    private void collectUserResourceRenamings(List<Renaming> renamings, CALFeatureName oldFeatureName, CALFeatureName newFeatureName, Status renameStatus) {
        ModuleName oldFeatureModuleName = oldFeatureName.toModuleName();
        UserResourceManager oldUserResourceManager = virtualResourceManager.getUserResourceManager(oldFeatureModuleName);
        if (oldUserResourceManager == null) {
            renameStatus.add(new Status(Status.Severity.WARNING, "The user resource manager for the old feature's module does not exist."));
            return;
        }
        
        ModuleName newFeatureModuleName = newFeatureName.toModuleName();
        UserResourceManager newUserResourceManager = virtualResourceManager.getUserResourceManager(newFeatureModuleName);
        if (newUserResourceManager == null) {
            renameStatus.add(new Status(Status.Severity.WARNING, "The user resource manager for the new feature's module does not exist."));
            return;
        }
        
        if (oldFeatureName.getType().equals(CALFeatureName.MODULE)) {
            
            List<ResourceName> oldUserResourceNames = ((UserResourceStore)oldUserResourceManager.getResourceStore()).getModuleResourceNameList(oldFeatureModuleName);
            
            for (int i = 0, n = oldUserResourceNames.size(); i < n; i++) {
                ResourceName oldUserResourceName = oldUserResourceNames.get(i);
                Locale locale = LocalizedResourceName.localeOf(oldUserResourceName);
                UserResourceFeatureName oldUserResourceFeatureName = (UserResourceFeatureName)oldUserResourceName.getFeatureName();
                UserResourceFeatureName newUserResourceFeatureName = new UserResourceFeatureName(newFeatureModuleName, oldUserResourceFeatureName.getName(), oldUserResourceFeatureName.getExtension());
                ResourceName newUserResourceName = new LocalizedResourceName(newUserResourceFeatureName, locale);
                
                renamings.add(new Renaming(oldUserResourceName, newUserResourceName, "userResource", oldUserResourceManager, newUserResourceManager));
            }
        }
    }
    
    /**
     * A helper method to collect a list of metadata resources that should be renamed.
     * @param renamings
     * @param oldFeatureName
     * @param newFeatureName
     * @param renameStatus
     */
    private void collectMetadataRenamings(List<Renaming> renamings, CALFeatureName oldFeatureName, CALFeatureName newFeatureName, Status renameStatus) {
        MetadataManager oldMetadataManager = virtualResourceManager.getMetadataManager(oldFeatureName.toModuleName());
        if (oldMetadataManager == null) {
            renameStatus.add(new Status(Status.Severity.WARNING, "The metadata manager for the old metadata resource does not exist."));
            return;
        }
        
        MetadataManager newMetadataManager = virtualResourceManager.getMetadataManager(newFeatureName.toModuleName());
        if (newMetadataManager == null) {
            renameStatus.add(new Status(Status.Severity.WARNING, "The metadata manager for the new metadata resource does not exist."));
            return;
        }
        
        // If this is a module, we need to update the metadata for every entity contained by it.
        if (oldFeatureName.getType().equals(CALFeatureName.MODULE)) {
            MetaModule metaModule = getMetaModule(oldFeatureName.toModuleName());
            if (metaModule == null) {
                return;
            }
            
            Set<CALFeatureName> featureNames = metaModule.getFeatureNames();
            for (final CALFeatureName nextFeatureName : featureNames) {
                List<ResourceName> metadataResourceNames = metaModule.getMetadataResourceNamesForAllLocales(nextFeatureName);
                
                if (!metadataResourceNames.isEmpty()) {
                    // Calculate the new name for this feature
                    CALFeatureName nextFeatureNameRenamed;
                    if (nextFeatureName.getType().equals(CALFeatureName.MODULE)) {
                        nextFeatureNameRenamed = newFeatureName;
                    } else if (nextFeatureName.getType().equals(CALFeatureName.CLASS_INSTANCE)) {
                        throw new IllegalStateException("Class instances shouldn't have metadata associated with them.");
                    } else {
                        QualifiedName qualifiedNextFeatureName = QualifiedName.makeFromCompoundName(nextFeatureName.getName());
                        if (!qualifiedNextFeatureName.getModuleName().equals(oldFeatureName.toModuleName())) {
                            throw new IllegalStateException("Feature name associated with, but not contained in, the renamed module.");
                        }
                        QualifiedName qualifiedNextFeatureNameRenamed = QualifiedName.make(newFeatureName.toModuleName(), qualifiedNextFeatureName.getUnqualifiedName());
                        nextFeatureNameRenamed = new CALFeatureName(nextFeatureName.getType(), qualifiedNextFeatureNameRenamed.getQualifiedName());
                    }
                    
                    for (int i = 0, n = metadataResourceNames.size(); i < n; i++) {
                        ResourceName metadataResourceName = metadataResourceNames.get(i);
                        Locale locale = LocalizedResourceName.localeOf(metadataResourceName);
                        
                        renamings.add(new Renaming(new LocalizedResourceName(nextFeatureName, locale), new LocalizedResourceName(nextFeatureNameRenamed, locale), "metadata", oldMetadataManager, newMetadataManager));
                    }
                }
            }
        } else {
            if (oldFeatureName.hasModuleName()) {
                MetaModule metaModule = getMetaModule(oldFeatureName.toModuleName());
                if (metaModule == null) {
                    return;
                }
                
                List<ResourceName> metadataResourceNames = metaModule.getMetadataResourceNamesForAllLocales(oldFeatureName);
                
                for (int i = 0, n = metadataResourceNames.size(); i < n; i++) {
                    ResourceName metadataResourceName = metadataResourceNames.get(i);
                    Locale locale = LocalizedResourceName.localeOf(metadataResourceName);
                    
                    renamings.add(new Renaming(new LocalizedResourceName(oldFeatureName, locale), new LocalizedResourceName(newFeatureName, locale), "metadata", oldMetadataManager, newMetadataManager));
                }
            }
        }
    }
    
    /**
     * A helper class representing a renaming operation. It encapsulates all the information that is needed by the workspace
     * to actually perform the renaming.
     * @author Peter Cardwell
     */
    private class Renaming {
        final ResourceName fromName;
        final ResourceName toName;
        final String renamingType;
        final ResourceManager fromResourceManager;
        final ResourceManager toResourceManager;
        
        Renaming(ResourceName fromName, ResourceName toName, String renamingType, ResourceManager fromResourceManager, ResourceManager toResourceManager) {
            this.fromName = fromName;
            this.toName = toName;
            this.renamingType = renamingType;
            this.fromResourceManager = fromResourceManager;
            this.toResourceManager = toResourceManager;
        }
    }
    
    /**
     * Rename a module in the workspace. This will only change the name of any related design,
     * metadata, and module resources and update any internal references to it in the workspace.
     * It will NOT update references to the module within any resources themselves
     * (ie. The contents of CAL sources, designs, metadata, etc. will not be updated).
     * @param oldFeatureName The name of the module to rename
     * @param newFeatureName The new name to rename the module to
     * @param renameStatus the tracking status object.
     * @return boolean whether the module was successfully renamed.
     */
    synchronized public final boolean renameFeature(CALFeatureName oldFeatureName, CALFeatureName newFeatureName, Status renameStatus) {
        
        if (!oldFeatureName.getType().equals(newFeatureName.getType())) {
            throw new IllegalArgumentException("oldFeatureName type is not the same as newFeatureName type");
        }
        
        // Collect a list of renamings to perform.
        List<Renaming> renamings = new ArrayList<Renaming>();
        collectMetadataRenamings(renamings, oldFeatureName, newFeatureName, renameStatus);
        collectDesignRenamings(renamings, oldFeatureName, newFeatureName, renameStatus);
        collectUserResourceRenamings(renamings, oldFeatureName, newFeatureName, renameStatus);
        
        if (oldFeatureName.getType().equals(CALFeatureName.MODULE)) {
            renamings.add(new Renaming(
                new ResourceName(oldFeatureName), new ResourceName(newFeatureName), "CAL source",
                virtualResourceManager.getSourceManager(oldFeatureName.toModuleName()),
                virtualResourceManager.getSourceManager(newFeatureName.toModuleName())));
        }
        
        List<Renaming> undoList = new ArrayList<Renaming>();
        for (final Renaming renaming : renamings) {
            try {
                if (!renaming.fromResourceManager.getResourceStore().isWriteable(renaming.fromName)) {
                    throw new RenamingException("The "+ renaming.renamingType + " resource for " + renaming.fromName.getFeatureName().getName() + " is not writeable.");
                }
                if (!renaming.fromResourceManager.getResourceStore().renameResource(renaming.fromName, renaming.toName, renaming.toResourceManager.getResourceStore(), new Status("Resource renaming"))) {
                    throw new RenamingException("Error renaming the " + renaming.renamingType + " resource for " + renaming.fromName.getFeatureName().getName() + ".");
                }
                
                undoList.add(renaming);
            } catch (RenamingException e) {
                renameStatus.add(new Status(Status.Severity.ERROR, e.getErrorMessage()));
                
                // Renaming failed, undo all renamings done so far and return
                for (int i = undoList.size() - 1; i >= 0; i--) {
                    Renaming undoRenaming = undoList.get(i);
                    
                    if (!undoRenaming.toResourceManager.getResourceStore().renameResource(undoRenaming.toName, undoRenaming.fromName, undoRenaming.fromResourceManager.getResourceStore(), new Status("Undo rename status"))) {
                        String msg = "FATAL: Error undoing the renaming of the " + undoRenaming.renamingType + " resource for " + undoRenaming.fromName + ".";
                        renameStatus.add(new Status(Status.Severity.ERROR, msg));
                    }
                }
                
                return false;
            }
        }

        // If the entity being renamed is a module, we need to rename the cal source itself.
        if (oldFeatureName.getType().equals(CALFeatureName.MODULE)) {
            ModuleSourceDefinition sourceToRename = sourceDefinitionGroup.getModuleSource(oldFeatureName.toModuleName());
            ModuleSourceDefinition renamedSource = virtualResourceManager.getSourceManager(newFeatureName.toModuleName()).getSource(newFeatureName.toModuleName(), renameStatus);
            
            // Now remove the reference to the old module from the source definition group and add the new module
            List<ModuleSourceDefinition> newModuleSources = new ArrayList<ModuleSourceDefinition>(Arrays.asList(sourceDefinitionGroup.getModuleSources()));
            newModuleSources.remove(sourceToRename);
            newModuleSources.add(renamedSource);
            ModuleSourceDefinition[] newModuleSourceArray = newModuleSources.toArray(ModuleSourceDefinition.EMPTY_ARRAY);
            this.sourceDefinitionGroup = new ModuleSourceDefinitionGroup(newModuleSourceArray);
            
            // Update the vault info map..
            VaultElementInfo newVaultInfo = VaultElementInfo.makeBasic("NonExistent", newFeatureName.getName(), null, 0);
            updateVaultInfo(newFeatureName.toModuleName(), newVaultInfo);
            
            ModuleName oldFeatureNameAsModuleName = oldFeatureName.toModuleName();
            moduleNameToVaultInfoMap.remove(oldFeatureNameAsModuleName);
            resourceRevisionInfo.removeModule(oldFeatureNameAsModuleName);
            resourceSyncTimeInfo.removeModule(oldFeatureNameAsModuleName);
            
            // Finally, remove the old module from the meta module list.
            removeMetaModule(oldFeatureNameAsModuleName);
        }
        
        return true;
    }
        
    /**
     * Saves the given metadata object to the workspace.
     * @param metadata the metadata object to save
     * @return true if metadata was saved, false otherwise
     */
    public boolean saveMetadata(CALFeatureMetadata metadata) {
        return saveMetadata(metadata, new Status("Save status"));
    }

    /**
     * Saves the given metadata object to the workspace.
     * @param metadata the metadata object to save
     * @param saveStatus the tracking status object.
     * @return true if metadata was saved, false otherwise
     */
    public boolean saveMetadata(CALFeatureMetadata metadata, Status saveStatus) {
        MetadataManager metadataManager = virtualResourceManager.getMetadataManager(metadata.getFeatureName().toModuleName());
        if (metadataManager == null) {
            saveStatus.add(new Status(Status.Severity.WARNING, "No registered metadata manager."));
            return false;
        }
        return metadataManager.saveMetadata(metadata, saveStatus);
    }

    /**
     * Save a design to the workspace.
     * @param design the design to save.
     * @param saveStatus the tracking status object
     */
    public void saveDesign(GemDesign design, Status saveStatus) {
        GemDesignManager designManager = virtualResourceManager.getDesignManager(design.getDesignName().getModuleName());
        if (designManager == null) {
            saveStatus.add(new Status(Status.Severity.WARNING, "No registered design manager."));
            return;
        }
        designManager.saveGemDesign(design, saveStatus);
    }
    
    /**
     * @param typeConsName the name of the type constructor.
     * @return the type constructor entity with the given name.
     */
    public TypeConstructor getTypeConstructor(QualifiedName typeConsName) {
        MetaModule metaModule = getMetaModule(typeConsName.getModuleName());
        if (metaModule == null) {
            return null;
        }
        
        return metaModule.getTypeInfo().getTypeConstructor(typeConsName.getUnqualifiedName());
    }
    
    /**
     * {@inheritDoc}
     */
    public void moduleLoaded (Module module) {
        addMetaModule (new MetaModule (module, virtualResourceManager));
        sourceMetrics.addModuleMetrics(module.getModuleTypeInfo());
    }
    
    /**
     * {@inheritDoc}
     */
    public void moduleRemoved (Module module) {
        removeMetaModule (module.getName ());
        sourceMetrics.removeModuleMetrics(module.getModuleTypeInfo());
    }
    
    /**
     * returns the name of the first module specified in the workspace definition.
     * this may return null if the workspace is not initialized, or there are no 
     * modules.
     * @return the first module in the workspace, or null if the workspace is not
     * properly initialized 
     */
    synchronized public final ModuleName getFirstModuleName() {
        return firstModule;
    }
    
    /**
     * Initialize the workspace from the given workspace definition.
     * Any current workspace definition will be replaced with the new one.
     * @param storedModules the modules which comprise the initial workspace definition.
     */
    synchronized public final Status initializeWorkspace(StoredVaultElement.Module[] storedModules, Status initStatus) {

        // Clear existing modules from the resource manager..
        removeAllStoredModules(initStatus);

        // record the name of the first module in the stored module list        
        if (storedModules != null && storedModules.length > 0) {
            firstModule = ModuleName.make(storedModules[0].getName());
        } else {
            firstModule = null;
        }
        // Reset the source definition group.
        this.sourceDefinitionGroup = new ModuleSourceDefinitionGroup(ModuleSourceDefinition.EMPTY_ARRAY);


        // Add new modules..
        
        // Import the stored module resources.
        List<StoredVaultElement.Module> storedModulesList = new ArrayList<StoredVaultElement.Module>(Arrays.asList(storedModules));
        
        for (Iterator<StoredVaultElement.Module> it = storedModulesList.iterator(); it.hasNext(); ) {
            
            StoredVaultElement.Module storedModule = it.next();

            if (isNullary() && !isTopLevelStoredModuleElementInStandardVault(storedModule) && !isCarBasedStoredModuleElementInStandardVault(storedModule)) {
                String warningString = "Module " + storedModule.getName() +
                        " was not imported.  Only Modules from the StandardVault or from a Car file may be used to initialize a nullary workspace.";
                initStatus.add(new Status(Status.Severity.WARNING, warningString));
                
                it.remove();
                continue;
            }
            
            importStoredModule(storedModule, initStatus);
        }
        
        // Create the module sources, and the source provider.
        ModuleSourceDefinition[] sourceDefinitions = new ModuleSourceDefinition[storedModulesList.size()];
        int index = 0;
        for (final StoredVaultElement.Module storedModule : storedModulesList) {
            ModuleName moduleName = ModuleName.make(storedModule.getName());
            
            CALSourceManager sourceManager = virtualResourceManager.getSourceManager(moduleName);
            if (sourceManager == null) {
                initStatus.add(new Status(Status.Severity.WARNING, "No registered source manager."));
                return initStatus;
            }
            
            sourceDefinitions[index] = sourceManager.getSource(moduleName, initStatus);
            index++;
        }

        this.sourceDefinitionGroup = new ModuleSourceDefinitionGroup(sourceDefinitions);
        
        // Persist the workspace description.
        if (!isNullary() && sourceDefinitionGroup.getNModules() > 0) {
            saveWorkspaceDescription(initStatus);
        }
        
        // Return the import status.
        return initStatus;
    }
    
    /**
     * Load the persisted workspace, if any.
     * 
     * @param loadStatus the tracking status object.
     *   If a previously-persisted workspace exists but could not be successfully loaded, there will be errors.
     *   Otherwise, there may be info or warnings.
     * @return true if there was a previously-persisted workspace, and it was successfully loaded.
     */
    public boolean loadPersistedWorkspace(Status loadStatus) {
        // Do not load persisted workspace if nullary.
        if (isNullary()) {
            return false;
        }
        
        // workspace description..
        boolean workspaceLoaded = loadWorkspaceDescription(loadStatus);
        
        // source definition group..
        calculateSourceDefinitionGroup();

        return workspaceLoaded;
    }

    /**
     * Calculate the source definition group managed by the workspace.
     * The sourceDefinitionGroup member will be set based on the module names in the moduleNameToVaultInfo map.
     */
    synchronized private final void calculateSourceDefinitionGroup() {
        // Get the names of the modules for which resources currently managed by this manager.
        Set<ModuleName> moduleNameSet = moduleNameToVaultInfoMap.keySet();

        List<ModuleSourceDefinition> sourceDefinitionList = new ArrayList<ModuleSourceDefinition>();
        
        // Add the source definitions for the given module names.
        for (final ModuleName moduleName : moduleNameSet) {
            CALSourceManager sourceManager = virtualResourceManager.getSourceManager(moduleName);
            if (sourceManager != null) {
                
                ModuleSourceDefinition sourceDefinition = sourceManager.getSource(moduleName, new Status("Source Status"));
                
                if (sourceDefinition != null) {
                    sourceDefinitionList.add(sourceDefinition);
                }
            }
        }
        
        // Convert the list of source definitions to an array.
        ModuleSourceDefinition[] sourceDefinitions = new ModuleSourceDefinition[sourceDefinitionList.size()];
        sourceDefinitionList.toArray(sourceDefinitions);

        this.sourceDefinitionGroup = new ModuleSourceDefinitionGroup(sourceDefinitions);
    }

    /**
     * Load the internal state of the workspace from its saved description.
     * 
     * @param loadStatus the tracking status object.
     *   If a previously-persisted workspace exists but could not be successfully loaded, there will be errors.
     *   Otherwise, there may be info or warnings.
     * @return true if there was a previously-persisted workspace, and it was successfully loaded.
     */
    private boolean loadWorkspaceDescription(Status loadStatus) {

        ResourcePath.FilePath workspaceDescriptionFilePath = getWorkspaceDescriptionFile();
        InputStream inputStream = NullaryEnvironment.getNullaryEnvironment().getInputStream(workspaceDescriptionFilePath, loadStatus);
        if (inputStream == null) {
            return false;
        }

        try {
            return persistenceManager.loadWorkspaceDescription(inputStream, loadStatus);

        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Persist the workspace description.
     * @param saveStatus the tracking status object.
     * @return whether the description was successfully saved.
     */
    private boolean saveWorkspaceDescription(Status saveStatus) {
        
        // Get the workspace file..
        ResourcePath.FilePath workspaceDescriptionFilePath = getWorkspaceDescriptionFile();
        File workspaceFile = NullaryEnvironment.getNullaryEnvironment().getFile(workspaceDescriptionFilePath, true);      // assumes a file system file.
        
        // The file (if any) temporarily containing the workspace file being replaced.
        File oldDescriptionFile;
        
        if (FileSystemHelper.fileExists(workspaceFile)) {
            // Get a temp file for the old description.
            try {
                // TODOEL: create then delete?!
                oldDescriptionFile = File.createTempFile("workspace", "old");
                
                if (!oldDescriptionFile.delete()) {
                    saveStatus.add(new Status(Status.Severity.ERROR, "Could not save workspace description."));
                    return false;
                }
                
            } catch (IOException e) {
                saveStatus.add(new Status(Status.Severity.ERROR, "Could not save workspace description.", e));
                return false;
            }
            
            // Rename to the temporary file.
            workspaceFile.renameTo(oldDescriptionFile);
            
        } else {
            oldDescriptionFile = null;
        }
        
        boolean newWorkspaceCreated = false;
        
        // Create an output stream on the workspace file.
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(workspaceFile);

            // Save to the new file..
            ResourceInfo resourceInfo = new ResourceInfo(resourceRevisionInfo, resourceSyncTimeInfo);
            persistenceManager.saveWorkspaceDescription(outputStream, resourceInfo);
            
            newWorkspaceCreated = true;
            
//        } catch (FileNotFoundException e) {
//            saveStatus.add(new Status(Status.Severity.ERROR, "Could not create file for workspace description.", e));
//
        } catch (Exception e) {
            saveStatus.add(new Status(Status.Severity.ERROR, "Could not create file for workspace description.", e));
            
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
        
        if (!newWorkspaceCreated) {
            // Delete the failed workspace file.
            if (workspaceFile.exists()) {
                workspaceFile.delete();
            }
            // Restore the old workspace file.
            if (oldDescriptionFile != null && !oldDescriptionFile.renameTo(workspaceFile)) {
                // This is really bad.
                saveStatus.add(new Status(Status.Severity.ERROR, "(FATAL) Could not restore old workspace description."));
            }
            return false;
        }
        
        // Delete the old file.
        if (oldDescriptionFile != null) {
            oldDescriptionFile.delete();
        }
        
        return true;
    }

    /**
     * Get the file which describes the contents of the workspace.
     * @return the file which contains the description of the workspace.
     */
    private ResourcePath.FilePath getWorkspaceDescriptionFile() {
        return workspaceDescriptionFile;
    }

    /**
     * Add a module to the workspace.
     * @param storedModule the module to add.
     * @param checkExisting if true, this operation will fail (with appropriate Status) if the module already exists in the workspace.
     * If false, the added module will replace any existing module resources in the workspace.
     * @param addStatus the tracking status object.
     * @return boolean whether the module was successfully added.  This will fail if the module already exists.
     */
    synchronized public final boolean addModule(StoredVaultElement.Module storedModule, boolean checkExisting, Status addStatus) {
        
        // check that the module does not already exist in the workspace.
        ModuleName moduleName = ModuleName.make(storedModule.getName());
        if (moduleName == null) {
            addStatus.add(new Status(Status.Severity.ERROR, "Could not determine module name.", null));
            return false;
        }

        if (checkExisting) {
            if (containsModule(moduleName)) {
                addStatus.add(new Status(Status.Severity.ERROR, "Module " + moduleName + " already exists in the current workspace.", null));
                return false;
            }
            
            // If this is a nullary workspace, check that we are not importing something into the StandardVault which already exists.
            if (isNullary() && !isTopLevelStoredModuleElementInStandardVault(storedModule) && !isCarBasedStoredModuleElementInStandardVault(storedModule) &&
                    StandardVault.getInstance().getSourceStore().hasFeature(new ResourceName(CALFeatureName.getModuleFeatureName(moduleName)))) {   // The get shouldn't fail.
                
                String errorString = "Module " + moduleName + " conflicts with a module which exists in the StandardVault.";
                addStatus.add(new Status(Status.Severity.ERROR, errorString, null));
                return false;
            }
        }
        
        // Import the resources.
        importStoredModule(storedModule, addStatus);

        CALSourceManager sourceManager = virtualResourceManager.getSourceManager(moduleName);
        if (sourceManager == null) {
            addStatus.add(new Status(Status.Severity.WARNING, "No registered source manager."));
            return true;            // true or false??
        }
        
        // Update the module source provider.
        ModuleSourceDefinition newModuleSourceDefinition = sourceManager.getSource(moduleName, addStatus);
        List<ModuleSourceDefinition> newModuleSources = new ArrayList<ModuleSourceDefinition>(Arrays.asList(sourceDefinitionGroup.getModuleSources()));
        
        ModuleSourceDefinition existingModuleSource = sourceDefinitionGroup.getModuleSource(moduleName);
        if (existingModuleSource != null) {
            newModuleSources.remove(existingModuleSource);
        }
        newModuleSources.add(newModuleSourceDefinition);

        ModuleSourceDefinition[] newModuleSourceArray = newModuleSources.toArray(ModuleSourceDefinition.EMPTY_ARRAY);
        this.sourceDefinitionGroup = new ModuleSourceDefinitionGroup(newModuleSourceArray);

        // Persist the workspace description if not nullary.
        if (!isNullary()) {
            saveWorkspaceDescription(addStatus);
        }
        
        return true;
    }
    
    /**
     * Remove a set of modules from the workspace.
     * @param moduleNames
     * @param removeStatus
     * @return boolean whether the modules were successfully removed from the workspace.
     */
    synchronized public final boolean removeModules (Set<ModuleName> moduleNames, Status removeStatus) {
    
        List<ModuleSourceDefinition> sourcesToRemove = new ArrayList<ModuleSourceDefinition> ();
        
        // Check that all listed modules exist.
        for (final ModuleName moduleName : moduleNames) {
            if (moduleName.equals(CAL_Prelude.MODULE_NAME)) {
                removeStatus.add(new Status(Status.Severity.ERROR, "Cannot remove Cal.Core.Prelude module.", null));
                return false;
            }
            ModuleSourceDefinition sourceToRemove = sourceDefinitionGroup.getModuleSource(moduleName);
            if (sourceToRemove == null) {
                return false;
            }
            sourcesToRemove.add (sourceToRemove);
        }
        
        List<ModuleSourceDefinition> newModuleSources = new ArrayList<ModuleSourceDefinition>(Arrays.asList(sourceDefinitionGroup.getModuleSources()));
        
        for (final ModuleSourceDefinition sourceToRemove : sourcesToRemove) {
            newModuleSources.remove(sourceToRemove);
            ModuleName moduleName = sourceToRemove.getModuleName();

            // Remove module resources from the resource manager if not nullary.
            // We can also remove if nullary, if we imported (during this session) from another vault.
            VaultElementInfo vaultInfo = getVaultInfo(moduleName);
            if (!isNullary() || vaultInfo == null || !isStandardVaultDescriptor(vaultInfo.getVaultDescriptor())) {
                removeStoredModule(moduleName, removeStatus);
            }
        }
        
        ModuleSourceDefinition[] newModuleSourceArray = newModuleSources.toArray(ModuleSourceDefinition.EMPTY_ARRAY);
            
        this.sourceDefinitionGroup = new ModuleSourceDefinitionGroup(newModuleSourceArray);

        // Persist the workspace description (if not nullary).
        if (!isNullary()) {
            boolean descriptionSaved = saveWorkspaceDescription(removeStatus);
            if (!descriptionSaved) {
                return false;
            }
        }
            
        return true;
    }
    
    /**
     * Remove a module from the workspace.
     * @param moduleName the name of the module to remove.
     * @param removeStatus the tracking status object.
     * @return boolean whether the module was successfully removed from the workspace.
     */
    public boolean removeModule(ModuleName moduleName, Status removeStatus) {
        Set<ModuleName> moduleNames = new HashSet<ModuleName>();
        moduleNames.add (moduleName);
        return removeModules (moduleNames, removeStatus);
    }

    /**
     * Remove the stored resources for a module from the workspace.
     * @param moduleName the name of the module.
     * @param removeStatus the tracking status object.
     */
    private void removeStoredModule(ModuleName moduleName, Status removeStatus) {
        for (final ResourceManager resourceManager : virtualResourceManager.getResourceManagersForModule(moduleName)) {
            if (resourceManager instanceof ModuleResourceManager) {
                ((ModuleResourceManager)resourceManager).removeModuleResources(moduleName, removeStatus);
            }
        }
        
        virtualResourceManager.unlinkModuleFromCars(moduleName);
        moduleNameToVaultInfoMap.remove(moduleName);
        resourceRevisionInfo.removeModule(moduleName);
        resourceSyncTimeInfo.removeModule(moduleName);
    }
    
    /**
     * Remove all stored module resources from the manager.
     * @param removeStatus the tracking status object.
     */
    private void removeAllStoredModules(Status removeStatus) {
        if (!isNullary()) {
            // Remove all resources from the respective managers.
            virtualResourceManager.removeAllResources(removeStatus);
        }
        
        // Clear the info maps.
        moduleNameToVaultInfoMap.clear();
        resourceRevisionInfo.clear();
        resourceSyncTimeInfo.clear();
    }

    /**
     * @return The WorkspaceSourceMetricsManager associated with this workspace.  This return value is guaranteed to be non-null.
     */
    public SourceMetrics getSourceMetrics() {
        return sourceMetrics;
    }
    
    /**
     * @param moduleName the name of a module.
     * @return the source definition for the specified module, or null if there is no source definition for that module.
     */
    public ModuleSourceDefinition getSourceDefinition(ModuleName moduleName) {
        return getSourceDefinitionGroup().getModuleSource(moduleName);
    }
    
    /**
     * @param moduleName the name of a module.
     * @return whether a module with the given name exists in the workspace.
     */
    synchronized public final boolean containsModule(ModuleName moduleName) {
        return getSourceDefinition(moduleName) != null;
    }
    
    /**
     * @param moduleName the name of a module.
     * @return debugging information about the module, e.g. the actual location of the module's resources. Can be null if the module does not exist.
     */
    public String getDebugInfoForModule(ModuleName moduleName) {
        CALSourceManager sourceManager = virtualResourceManager.getSourceManager(moduleName);
        if (sourceManager == null) {
            return null;
        } else {
            CALFeatureName moduleFeatureName = CALFeatureName.getModuleFeatureName(moduleName);
            ResourceName moduleResourceName = new ResourceName(moduleFeatureName);
            return sourceManager.getDebugInfo(moduleResourceName);
        }
    }
    
    /**
     * @return debugging information about the modules, e.g. the actual location of each module's resources.
     */
    public String getDebugInfo() {
        
        StringBuilder details = new StringBuilder();
        
        ModuleName[] moduleNames = getModuleNames();
        Arrays.sort(moduleNames);
        
        int nModules = moduleNames.length;
        if (nModules > 0) {
            for (int i = 0; i < nModules; i++) {
                details.append(moduleNames[i]);
                
                VaultElementInfo vaultInfo = getVaultInfo(moduleNames[i]);
                
                if (vaultInfo != null) {
                    long moduleRevision = vaultInfo.getRevision();
                    
                    String descriptor = vaultInfo.getVaultDescriptor();
                    String locationString = vaultInfo.getLocationString();
                    
                    details.append("\n   - " + descriptor);
                    if (locationString != null) {
                        details.append("(" + locationString + ")");
                    }
                    details.append("  revision " + moduleRevision);
                }
                
                details.append("\n");
                
                String debugInfo = getDebugInfoForModule(moduleNames[i]);
                
                if (debugInfo != null) {
                    details.append("   - ").append(debugInfo).append("\n");
                }
            }
        } else {
            details.append("No modules in workspace");
        }
        
        return details.toString();
    }
    
    /**
     * Get the vault associated with a module.
     * @param moduleName the name of the module.
     * @return the vault associated with the module, or null if there is no such vault.
     */
    public Vault getVault(ModuleName moduleName) {
        VaultElementInfo currentVaultInfo = getVaultInfo(moduleName);
        
        if (currentVaultInfo instanceof VaultElementInfo.Nested) {
            
            if (currentVaultInfo.getVaultDescriptor().equals(CarVault.getVaultClassDescriptor())) {
                VaultElementInfo.Nested nestedVaultInfo = (VaultElementInfo.Nested)currentVaultInfo;
                
                VaultElementInfo outerVaultElementInfo = nestedVaultInfo.getOuterVaultElementInfo();
                
                String outerVaultDescriptor = outerVaultElementInfo.getVaultDescriptor();
                String outerVaultLocationString = outerVaultElementInfo.getLocationString();
                
                Vault outerVault = vaultRegistry.getVault(outerVaultDescriptor, outerVaultLocationString);
                
                Status status = new Status("Getting a vault");
                StoredVaultElement.Car car = outerVault.getCar(outerVaultElementInfo.getElementName(), outerVaultElementInfo.getRevision(), status);
                
                if (car == null) {
                    return null;
                } else {
                    return car.getCarVault();
                }
                
            } else {
                return null;
            }
            
        } else { // currentVaultInfo is a VaultElementInfo.Basic instance
            
            String vaultDescriptor = currentVaultInfo.getVaultDescriptor();
            String locationString = currentVaultInfo.getLocationString();
            
            return vaultRegistry.getVault(vaultDescriptor, locationString);
        }
    }
    
    /**
     * Get the storage info for a module.
     * @param moduleName the name of a module.
     * @return the storage info for the given module, or null if there is no such info known to this manager.
     */
    public VaultElementInfo getVaultInfo(ModuleName moduleName) {
        return moduleNameToVaultInfoMap.get(moduleName);
    }

    /**
     * Update the vault info associated with a module.
     * @param moduleName the name of the module whose vault info should be updated.
     * @param vaultInfo the updated vault info for the module.
     */
    void updateVaultInfo(ModuleName moduleName, VaultElementInfo vaultInfo) {
        moduleNameToVaultInfoMap.put(moduleName, vaultInfo);
    }
    
    /**
     * Update the resource info for resources in the workspace.
     * @param resourceInfo updated resource info.
     */
    void updateResourceInfo(ResourceInfo resourceInfo) {
        ResourceRevision.Info revisionInfo = resourceInfo.getResourceRevisionInfo();
        resourceRevisionInfo.updateResourceRevisionInfo(revisionInfo);
        
        SyncTimeInfo syncTimeInfo = resourceInfo.getResourceSyncTimeInfo();
        resourceSyncTimeInfo.updateResourceSyncTimeInfo(syncTimeInfo);
    }

    /**
     * @return Returns the vaultRegistry.
     */
    public VaultRegistry getVaultRegistry() {
        return vaultRegistry;
    }

    /**
     * Register a Vault Provider with this registry.
     * @param newProvider the provider to register.
     */
    public void registerVaultProvider(VaultProvider newProvider) {
        vaultRegistry.registerVaultProvider(newProvider);
    }
    
    /**
     * Register a Vault Authenticator with this registry.
     * @param newAuthenticator the authenticator to register.
     */
    public void registerVaultAuthenticator(VaultAuthenticator newAuthenticator) {
        vaultRegistry.registerVaultAuthenticator(newAuthenticator);
    }
    
    /**
     * Register a non-Car-aware resource manager with the workspace.
     * @param resourceManager the manager to register.
     */
    public void registerNonCarAwareResourceManager(ResourceManager resourceManager) {
        virtualResourceManager.registerNonCarAwareResourceManager(resourceManager);
    }
    
    /**
     * Register a Car-aware resource manager with the workspace through a provider.
     * @param provider the provider for the Car-aware resource manager.
     */
    public void registerCarAwareResourceManager(VirtualResourceManager.CarAwareResourceManagerProvider provider) {
        virtualResourceManager.registerCarAwareResourceManager(provider);
    }
    
    /**
     * Get the names of the modules in the workspace.
     * @return the names of the modules in the workspace.
     */
    synchronized public final ModuleName[] getModuleNames() {
        int nModules = sourceDefinitionGroup.getNModules();
        ModuleName[] moduleNames = new ModuleName[nModules];

        for (int i = 0; i < nModules; i++) {
            ModuleSourceDefinition sourceDefinition = sourceDefinitionGroup.getModuleSource(i);
            moduleNames[i] = sourceDefinition.getModuleName();
        }
        
        return moduleNames;
    }
    
    /**
     * Check whether the file definition of a CAL entity contains a specified string.
     * @param moduleName the module name of the entity.
     * @param unqualifiedName the unqualified name of the entity.
     * @param searchText the text to match within the CAL definition.
     * 
     * @return true if search string is found; false if not
     */
    public boolean checkDefinitionContent(ModuleName moduleName, String unqualifiedName, String searchText) throws IOException {
    
        QualifiedName qualifiedScName = QualifiedName.make(moduleName, unqualifiedName);
        String beginString = "// @@@begin ";
        String endString = "// @@@end ";
        String beginNameString = beginString + qualifiedScName;
        String endNameString = endString + qualifiedScName;
    
        // Flag indicating whether search string was found
        boolean found = false;
        // Flag indicating whether currently searching inside proper section
        boolean insideSection = false;

        Reader sourceReader = null;
        try {
            ModuleSourceDefinition sourceDefinition = getSourceDefinition(moduleName);
            if (sourceDefinition == null) {
                return false;
            }

            sourceReader = sourceDefinition.getSourceReader(new Status("Reader status"));
            if (sourceReader == null) {
                return false;
            }
            
            BufferedReader bufferedReader = new BufferedReader(sourceReader);
    
            for (String lineString = bufferedReader.readLine(); lineString != null; lineString = bufferedReader.readLine()) {
                
                if (!insideSection) {
                    // We are looking for section start
                    if (lineString.startsWith(beginNameString)) {
                        // Make sure that we didn't find part of another name
                        // Grab the part of the line string that starts with the gem name
                        StringTokenizer tokenizer =
                            new StringTokenizer(lineString.substring(beginString.length()));
                        if (tokenizer.hasMoreElements() && tokenizer.nextToken().equals(qualifiedScName.getQualifiedName())) {
                            insideSection = true;
                        }
                    }
    
                } else {
                    // We are inside section, looking for match or section end
                    if (lineString.indexOf(searchText) >= 0) {
                        found = true;
                        break;
                    }
                        
                    if (lineString.startsWith(endNameString)) {
    
                        // Make sure that we didn't find part of another name
                        // Grab the part of the line string that starts with the gem name
                        StringTokenizer tokenizer =
                            new StringTokenizer(lineString.substring(endString.length()));
                        if (tokenizer.hasMoreElements() && tokenizer.nextToken().equals(qualifiedScName.getQualifiedName())) {
                            insideSection = false;
                            break;
                        }
                    }
                }
            }
        } catch (FileNotFoundException fnfe) {
            
            // Couldn't find the file for the given module.
            // Re-throw the exception with a proper error message.
            throw new FileNotFoundException("Couldn't find the file for module " + moduleName + ".");
    
        } finally {
            // Close the file now
            try {
                if (sourceReader != null){
                    sourceReader.close();
                }
            } catch (IOException e) { // do nothing
            }
        }
        
        return found;
    }

    /**
     * Save an entity to the workspace.
     * @param entityName the name of the entity to save.
     * @param definitionText the text of the definition.
     * @param metadata any metadata to save with the entity.  If null, no metadata will be associated with the entity.
     * @param gemDesignDocument any design to associate with the saved entity.  If null, no design will be associated with the entity.
     * @return Status the status of the save.  If the definition could not be saved, this will be level Status.Severity.ERROR.
     *   Other failures will result in WARNING.  Otherwise, they will be Status.Severity.OK.
     */
    public Status saveEntity(QualifiedName entityName, String definitionText, ScopedEntityMetadata metadata, Document gemDesignDocument) {
        
        boolean scExists = getGemEntity(entityName) != null;
        if (LanguageInfo.isValidFunctionName(entityName.getUnqualifiedName())) {
            scExists = getGemEntity(entityName) != null;
        } else {
            scExists = getTypeConstructor(entityName) != null;
        }
    
        Status saveStatus = new Status("Save status");
        
        // Save it out.
        boolean saveSucceeded = saveDefinition(entityName, definitionText, scExists, saveStatus);
        if (!saveSucceeded) {
            saveStatus.add(new Status(Status.Severity.ERROR, "Failed to save gem definition.", null));
            return saveStatus;
        }
        
        if (metadata != null) {
            boolean metadataSaveSucceeded = saveMetadata(metadata, saveStatus);
            if (!metadataSaveSucceeded) {
                saveStatus.add(new Status(Status.Severity.WARNING, "Failed to save gem metadata.", null));
            }
        }
    
        if (gemDesignDocument != null) {
            saveDesign(new GemDesign(entityName, gemDesignDocument), saveStatus);
        }
        
        return saveStatus;
    }

    /**
     * Save out the definition for a CAL entity.
     * @param entityName the name of the entity.
     * @param definitionText the text of the CAL definition.
     * @param scExists whether the entity already exists in this module.
     * @param saveStatus the tracking status object.
     * @return whether the definition was sucessfully saved.
     */
    private boolean saveDefinition(QualifiedName entityName, String definitionText, boolean scExists, Status saveStatus) {
        
        final ModuleName moduleName = entityName.getModuleName();
        
        String beginString = "// @@@begin ";
        String endString = "// @@@end ";
        String beginNameString = beginString + entityName;
        String endNameString = endString + entityName;
        
        ModuleSourceDefinition sourceDefinition = getSourceDefinition(moduleName);
        if (sourceDefinition == null) {
            return false;
        }
        
        Reader sourceReader = sourceDefinition.getSourceReader(null);
        if (sourceReader == null) {
            String message = "Couldn't read definition for \"" + entityName + "\".";
            saveStatus.add(new Status(Status.Severity.ERROR, message));
            return false;
        }
        BufferedReader bufferedReader = new BufferedReader(sourceReader);
        
        try {
            // The new file text..
            StringBuilder newFileTextBuilder = new StringBuilder();
            
            if (!scExists) {
                // The gem does not exist.  Simply append its text to the end of the file.
                
                try {
                    // Add all the file text
                    for (String lineString = bufferedReader.readLine(); lineString != null; lineString = bufferedReader.readLine()) {
                        newFileTextBuilder.append(lineString + "\n");
                    }
                } catch (IOException ioe) {
                    String message = "Couldn't read definition for \"" + entityName + "\".";
                    saveStatus.add(new Status(Status.Severity.ERROR, message, ioe));
                    return false;
                }
                
                // Add the gem definition.
                newFileTextBuilder.append(getDefinition(definitionText, beginNameString, endNameString));
                
            } else {
                // The gem exists.  Attempt to replace its text in the file.
                
                // Read in all the file text
                List<String> fileText = new ArrayList<String>();
                int beginIndex = -1;
                int endIndex = -1;
                boolean shouldEatOneMoreLine = false;
                try {
                    for (String lineString = bufferedReader.readLine(); lineString != null; lineString = bufferedReader.readLine()) {
                        
                        // Add the text to the List
                        fileText.add(lineString);
                        int lineIndex = fileText.size() - 1;            // ArrayList size is a (faster) variable lookup
                        
                        // Keep track of where to replace the gem text
                        if (beginIndex < 0){
                            if (lineString.startsWith(beginNameString)) {
                                
                                // Make sure that we didn't find part of another name
                                // Grab the part of the line string that starts with the gem name
                                String endLineString = lineString.substring(beginString.length());
                                
                                // the first token should be the gem's name
                                StringTokenizer tokenizer = new StringTokenizer(endLineString);
                                if (tokenizer.hasMoreElements() && tokenizer.nextToken().equals(entityName.getQualifiedName())) {
                                    beginIndex = lineIndex;
                                }
                            }
                            
                        } else if (endIndex < 0) {
                            if (lineString.startsWith(endNameString)) {
                                
                                // Make sure that we didn't find part of another name
                                // Grab the part of the line string that starts with the gem name
                                String endLineString = lineString.substring(endString.length());
                                
                                // the first token should be the gem's name
                                StringTokenizer tokenizer = new StringTokenizer(endLineString);
                                if (tokenizer.hasMoreElements() && tokenizer.nextToken().equals(entityName.getQualifiedName())) {
                                    endIndex = lineIndex;
                                }
                            }
                        }
                        
                        // See if the line following the line with the end marker is blank.
                        if (endIndex >= 0 && lineIndex == endIndex + 1) {
                            
                            // Convert to a char array
                            char[] charArray = lineString.toCharArray();
                            
                            // Now check each character (if any) to see if they're whitespace
                            boolean isBlank = true;
                            for (int i = 0; i < charArray.length; i++) {
                                if (!Character.isWhitespace(charArray[i])) {
                                    isBlank = false;
                                    break;
                                }
                            }
                            shouldEatOneMoreLine = isBlank;
                        }
                    }
                } catch (IOException ioe) {
                    String message = "Couldn't read definition for \"" + entityName + "\".";
                    saveStatus.add(new Status(Status.Severity.ERROR, message, ioe));
                    return false;
                }
                
                // Replace the line following the line with the end marker, if appropriate
                if (shouldEatOneMoreLine) {
                    endIndex++;
                }
                
                if (beginIndex < 0 || endIndex < 0) {
                    // couldn't find where to replace the lines..
                    // the finally clause should take care of closing everything
                    String markersString;
                    if (beginIndex < 0 && endIndex < 0) {
                        markersString = "begin or end";
                    } else if (beginIndex < 0) {
                        markersString = "begin";
                    } else {
                        markersString = "end";
                    }
                    
                    String message = "Couldn't find " + markersString + " marker for \"" + entityName + "\".";
                    saveStatus.add(new Status(Status.Severity.ERROR, message, null));
                    return false;
                }
                
                //
                // Add all the file text
                //
                
                List<String> beforeText = fileText.subList(0, beginIndex);
                List<String> afterText = fileText.subList(endIndex + 1, fileText.size());
                
                // Add text appearing before the gem definition
                for (final String writeString : beforeText) {
                    newFileTextBuilder.append(writeString + "\n");
                }
                
                // Add the gem definition
                newFileTextBuilder.append(getDefinition(definitionText, beginNameString, endNameString));
                
                // Add text appearing after the gem definition
                for (final String writeString : afterText) {
                    newFileTextBuilder.append(writeString + "\n");
                }
            }
        
            // Convert to a moduleSource.
            final String newFileTextString = newFileTextBuilder.toString();
            
            ModuleSourceDefinition sourceToSave = new StringModuleSourceDefinition(moduleName, newFileTextString);
            
            // Save..
            return saveSource(sourceToSave, saveStatus);
            
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
            }
        }
        
    }

    /**
     * Get CAL definition text..
     * 
     * @param definitionText the text of a CAL definition.
     * @param beginString the string with which to start the definition.  Should be a CAL comment.
     * @param endString the string with which to end the definition.  Should be a CAL comment.
     * @return the string definition.
     */
    private static String getDefinition(String definitionText, String beginString, String endString) {
    
        StringBuilder sb = new StringBuilder();
        
        Date date = new Date();
    
        sb.append(beginString + " saved " + date + ".\n");
        sb.append("// Warning: this section may be automatically regenerated by the GemCutter.\n");
        
        sb.append(definitionText);
    
        sb.append(endString + "\n");
        sb.append("\n");
        
        return sb.toString();
    }

    /**
     * @param moduleName the name of the module whose associated source manager is to be returned.
     * @return a source manager for the specified module.
     */
    public CALSourceManager getSourceManager(ModuleName moduleName) {
        // TODOEL: reduce scope.  This is here for hacks in the GemCutter, the ExcludeTestModulesFilter, and JFit.
        // todo-jowong edit the note above when ExcludeTestModulesFilter is refactored
        return virtualResourceManager.getSourceManager(moduleName);
    }

    /**
     * @return a ModuleNameToResourceManagerMapping that can be used to fetch the
     *         source manager for a particular module.
     */
    public VirtualResourceManager.ModuleNameToResourceManagerMapping getModuleNameToSourceManagerMapping() {
        // TODOEL: reduce scope.  This is here for the RenameRefactorer, which is in the compiler package.
        return virtualResourceManager.getModuleNameToSourceManagerMapping();
    }

    /**
     * Import stored module resources into the workspace.
     * @param storedModule the stored module resource.
     * @param importStatus the tracking status object.
     */
    private void importStoredModule(StoredVaultElement.Module storedModule, Status importStatus) {
        
        ModuleName moduleName = ModuleName.make(storedModule.getName());
        VaultElementInfo storedModuleVaultInfo = storedModule.getVaultInfo();
        
        if (storedModuleVaultInfo instanceof VaultElementInfo.Nested) {
            importStoredModuleFromNestedVault(moduleName, storedModuleVaultInfo, importStatus);
        } else {
            importStoredModuleFromTopLevelVault(storedModule, moduleName, storedModuleVaultInfo, importStatus);
        }
    }

    /**
     * Import the resources of a stored module that resides in a top-level vault (e.g. the StandardVault or the EnterpriseVault).
     * @param storedModule the stored module.
     * @param moduleName the name of the stored module.
     * @param storedModuleVaultInfo the vault element info for the stored module.
     * @param importStatus the tracking status object.
     */
    private void importStoredModuleFromTopLevelVault(StoredVaultElement.Module storedModule, ModuleName moduleName, VaultElementInfo storedModuleVaultInfo, Status importStatus) {
        // Check for an import from the StandardVault into a nullary workspace.
        if (!(isNullary() && isTopLevelStoredModuleElementInStandardVault(storedModule))) {

            for (Iterator<WorkspaceResource> it = storedModule.getResourceIterator(); it.hasNext(); ) {
                WorkspaceResource storedModuleResource = it.next();
                String resourceType = storedModuleResource.getResourceType();
                
                ResourceManager resourceManager = virtualResourceManager.getModuleSpecificResourceManager(moduleName, resourceType);
                if (resourceManager == null) {
                    importStatus.add(new Status(Status.Severity.ERROR, "Could not find resource manager for resource type " + resourceType + " for module " + moduleName));
                    continue;
                }
                
                WorkspaceResource.SyncTime syncTime = resourceManager.importResource(storedModuleResource, importStatus);
                if (syncTime == null) {
                    importStatus.add(new Status(Status.Severity.ERROR, "Could not import resource " + storedModuleResource.getIdentifier()));
                    continue;
                }
                
                resourceSyncTimeInfo.updateResourceSyncTime(syncTime);
            }
            
            // Note: only update the resource revision info if necessary, as this can be expensive to calculate.
            resourceRevisionInfo.updateResourceRevisionInfo(storedModule.getResourceRevisionInfo());
        }
        
        // disassociate this module from any Cars
        virtualResourceManager.unlinkModuleFromCars(moduleName);
        
        // Update the stored module info maps.
        updateVaultInfo(moduleName, storedModuleVaultInfo);
    }

    /**
     * Import the resources of a stored module that resides in a nested vault (e.g. from a Car).
     * @param moduleName the name of the stored module.
     * @param storedModuleVaultInfo the vault element info for the stored module.
     * @param importStatus the tracking status object.
     */
    private void importStoredModuleFromNestedVault(ModuleName moduleName, VaultElementInfo storedModuleVaultInfo, Status importStatus) {
        if (!storedModuleVaultInfo.getVaultDescriptor().equals(CarVault.getVaultClassDescriptor())) {
            importStatus.add(new Status(Status.Severity.ERROR, "The nested vault " + storedModuleVaultInfo.toString() + " is not supported"));
            return;
        }
        
        VaultElementInfo.Nested nestedElementInfo = (VaultElementInfo.Nested)storedModuleVaultInfo;
        VaultElementInfo.Basic outerElementInfo = nestedElementInfo.getOuterVaultElementInfo();
        
        String carName = outerElementInfo.getElementName();
        
        CarManager carManager = virtualResourceManager.getCarManager();
        
        // Check for an import from the StandardVault into a nullary workspace.
        if (!(isNullary() && isStandardVaultDescriptor(outerElementInfo.getVaultDescriptor()))) {
            
            boolean carAlreadyImported = carManager.getResourceStore().hasFeature(new ResourceName(CarFeatureName.getCarFeatureName(carName)));
            
            // import the Car if it is not already imported
            if (!carAlreadyImported) {
                
                Vault vaultContainingCar = vaultRegistry.getVault(outerElementInfo.getVaultDescriptor(), outerElementInfo.getLocationString());
                if (vaultContainingCar == null) {
                    importStatus.add(new Status(Status.Severity.ERROR, "Error getting the " + outerElementInfo.getVaultDescriptor() + " containing the Car"));
                    return;
                }
                
                Car car = vaultContainingCar.getCarAsResource(carName, outerElementInfo.getRevision(), importStatus);
                if (car == null) {
                    importStatus.add(new Status(Status.Severity.ERROR, "Error getting the Car " + carName + " from the " + outerElementInfo.getVaultDescriptor()));
                    return;
                }
                
                WorkspaceResource.SyncTime syncTime = carManager.importResource(car, importStatus);
                if (syncTime == null) {
                    importStatus.add(new Status(Status.Severity.ERROR, "Could not import resource " + car.getIdentifier()));
                    return;
                }
                
                resourceSyncTimeInfo.updateResourceSyncTime(syncTime);
                
                // update the resource revision info.
                ResourceRevision.Info resourceRevisionInfo = new ResourceRevision.Info(
                    Collections.singletonList(
                        new ResourceRevision(car.getIdentifier(), outerElementInfo.getRevision())));
                
                resourceRevisionInfo.updateResourceRevisionInfo(resourceRevisionInfo);
                
                // associate this module with the Car
                virtualResourceManager.linkModuleWithCar(moduleName, car.getAccessor(importStatus));
                
            } else {
                CarStore carStore = (CarStore)carManager.getResourceStore();
                
                Car car = carStore.getCar(carName);
                if (car == null) {
                    importStatus.add(new Status(Status.Severity.ERROR, "Error getting the Car " + carName + " from the CarStore"));
                    return;
                }
                
                // associate this module with the Car
                virtualResourceManager.linkModuleWithCar(moduleName, car.getAccessor(importStatus));
            }
        } else {
            CarStore carStore = (CarStore)carManager.getResourceStore();
            
            Car car = carStore.getCar(carName);
            if (car == null) {
                importStatus.add(new Status(Status.Severity.ERROR, "Error getting the Car " + carName + " from the CarStore"));
                return;
            }
            
            // associate this module with the Car
            virtualResourceManager.linkModuleWithCar(moduleName, car.getAccessor(importStatus));
        }
        
        // now import the stored module
        updateVaultInfo(moduleName, storedModuleVaultInfo);
    }
    
    /**
     * @param storedModule a stored module.
     * @return whether the given stored module is from the StandardVault
     */
    private boolean isTopLevelStoredModuleElementInStandardVault(StoredVaultElement.Module storedModule) {
        return isStandardVaultDescriptor(storedModule.getVaultInfo().getVaultDescriptor());
    }
    
    /**
     * @param descriptorString a vault descriptor string.
     * @return whether the given string is the descriptor for the StandardVault.
     */
    private boolean isStandardVaultDescriptor(String descriptorString) {
        return descriptorString.equals(StandardVault.getVaultClassProvider().getVaultDescriptor());
    }
    
    /**
     * @param storedModule a stored module.
     * @return whether the given stored module is from a Car in the StandardVault.
     */
    private boolean isCarBasedStoredModuleElementInStandardVault(StoredVaultElement.Module storedModule) {
        
        VaultElementInfo vaultInfo = storedModule.getVaultInfo();
        
        if (vaultInfo instanceof VaultElementInfo.Nested) {
            VaultElementInfo.Nested nestedVaultInfo = (VaultElementInfo.Nested)vaultInfo;
            
            return nestedVaultInfo.getVaultDescriptor().equals(CarVault.getVaultClassDescriptor()) &&
                isStandardVaultDescriptor(nestedVaultInfo.getOuterVaultElementInfo().getVaultDescriptor());
        }
        return false;
    }
    
    /**
     * Synchronize a module with the vault with which it is associated.
     * 
     * @param moduleName the name of the module to synchronize.
     * @param syncStatus the tracking status object.
     * @return the result of the sync.
     */
    public SyncInfo syncModule(ModuleName moduleName, Status syncStatus) {
        return syncModuleToRevision(moduleName, -1, false, syncStatus);
    }
    
    /**
     * Synchronize a module with a given revision, with respect to the vault with which it is associated.
     * 
     * @param moduleName the name of the module to synchronize.
     * @param revisionNum the revision number of the module.
     *   If <0, will sync to the latest revision.
     *   Otherwise, will sync to the given revision, even if that revision is less than the current revision.
     * @param syncStatus the tracking status object.
     * @param force if true, merge / conflicts will be ignored.  Any changes to resources will be clobbered.
     * @return the result of the sync.
     */
    public SyncInfo syncModuleToRevision(ModuleName moduleName, int revisionNum, boolean force, Status syncStatus) {
        
        SyncInfo syncInfo = new SyncInfo();
        
        // Get the vault for the module.
        Vault vault = getVault(moduleName);
        
        if (vault == null) {
            syncStatus.add(new Status(Status.Severity.ERROR, "The vault for module " + moduleName + " is not accessible."));
            return syncInfo;
        }
        
        // Check for a sync from the StandardVault to the nullary workspace, or a sync from the CarVault (which is an unmodifiable vault)
        String vaultDescriptor = vault.getVaultProvider().getVaultDescriptor();
        if ((isNullary() && isStandardVaultDescriptor(vaultDescriptor)) || CarVault.getVaultClassDescriptor().equals(vaultDescriptor)) {
            // Always sync'd.
            return syncInfo;
        }
        
        if (!(vault instanceof NonExistentVault)) {
            // Get the stored module from the vault.
            StoredVaultElement.Module vaultStoredModule = vault.getStoredModule(moduleName, revisionNum, syncStatus);
            if (vaultStoredModule == null) {
                syncStatus.add(new Status(Status.Severity.ERROR, "Could not retrieve info for module " + moduleName + "."));
                return syncInfo;
            }
            
            syncModuleToStoredModule(vaultStoredModule, force, syncStatus, syncInfo);
        }
        
        return syncInfo;
    }
    
    /**
     * Synchronize a module with a given stored module.
     * 
     * @param storedModule the stored module to with which to synchronize.
     * @param syncStatus the tracking status object.
     * @param force if true, merge / conflicts will be ignored.  Any changes to resources will be clobbered.
     *   if false, merge / conflicts will be noted, but affected resources will not be updated.
     * @return the result of the sync.
     */
    public SyncInfo syncModuleToStoredModule(StoredVaultElement.Module storedModule, boolean force, Status syncStatus) {
        SyncInfo syncInfo = new SyncInfo();
        syncModuleToStoredModule(storedModule, force, syncStatus, syncInfo);
        return syncInfo;
    }
    
    /**
     * Synchronize a module with a given stored module.
     * 
     * @param storedModule the stored module to with which to synchronize.
     * @param syncStatus the tracking status object.
     * @param force if true, merge / conflicts will be ignored.  Any changes to resources will be clobbered.
     *   if false, merge / conflicts will be noted, but affected resources will not be updated.
     * @param syncInfo the object to which to add the result of the sync.
     */
    synchronized private final void syncModuleToStoredModule(StoredVaultElement.Module storedModule, boolean force, Status syncStatus, SyncInfo syncInfo) {
        // TODOEL: syncing to a non-existent module is equivalent to addModule().
        //  However, in the case of a sync, the results should be recorded in the sync info object.
        
        ModuleName moduleName = ModuleName.make(storedModule.getName());
        int revisionNum = storedModule.getVaultInfo().getRevision();
        
        VaultElementInfo currentVaultInfo = getVaultInfo(moduleName);
        if (!force && currentVaultInfo != null && !currentVaultInfo.sameVault(storedModule.getVaultInfo())) {
            String statusMessage = "Module \"" + moduleName + "\" not synced." +
                                   "  Attempt to sync to a vault different from the module's originating vault.";
            syncStatus.add(new Status(Status.Severity.WARNING, statusMessage));
            return;
        }
        
        // Get the resource revision info for the vault store module.
        ResourceRevision.Info vaultResourceRevisionInfo = storedModule.getResourceRevisionInfo();

        // Map from resource type to the resources of that type from the module revision from the vault.
        Map<String, Set<WorkspaceResource>> resourceTypeToResourcesFromVaultMap = new HashMap<String, Set<WorkspaceResource>>();

        // Populate the map.
        for (Iterator<WorkspaceResource> it = storedModule.getResourceIterator(); it.hasNext(); ) {
            WorkspaceResource vaultStoredModuleResource = it.next();
            String resourceType = vaultStoredModuleResource.getResourceType();

            Set<WorkspaceResource> resourceTypeSet = resourceTypeToResourcesFromVaultMap.get(resourceType);
            if (resourceTypeSet == null) {
                resourceTypeSet = new HashSet<WorkspaceResource>();
                resourceTypeToResourcesFromVaultMap.put(resourceType, resourceTypeSet);
            }
            
            resourceTypeSet.add(vaultStoredModuleResource);
        }
        
        // To calculate deleted resources, first get all the resources.
        //   Later, we'll remove the resources which sync'd.
        Set<ResourceIdentifier> deletedResourceIdentifierSet = new HashSet<ResourceIdentifier>();
        for (final ResourceManager resourceManager : virtualResourceManager.getResourceManagersForModule(moduleName)) {
            // Iterate over the resources managed by the manager.  Add the identifier.
            for (Iterator<WorkspaceResource> it = resourceManager.getResourceStore().getResourceIterator(); it.hasNext(); ) {
                WorkspaceResource workspaceResource = it.next();
                FeatureName featureName = workspaceResource.getIdentifier().getFeatureName();
                
                if (featureName instanceof CALFeatureName) {
                    CALFeatureName calFeatureName = (CALFeatureName)featureName;
                    if (calFeatureName.hasModuleName() && calFeatureName.toModuleName().equals(moduleName)) {
                        deletedResourceIdentifierSet.add(workspaceResource.getIdentifier());
                    }
                }
            }
        }

        // Whether this is an absolute revision num, ie. sync to a given revision num.  -1 would mean sync to latest.
        boolean syncToSpecificRevision = revisionNum > 0;
        
        // Now populate each of the affected managers.
        for (final Map.Entry<String, Set<WorkspaceResource>> entry : resourceTypeToResourcesFromVaultMap.entrySet()) {
            
            final String resourceType = entry.getKey();
            
            ResourceManager resourceManager = virtualResourceManager.getModuleSpecificResourceManager(moduleName, resourceType);
            if (resourceManager == null) {
                String statusMessage = "Resources could not be imported for resources of type " + resourceType + " -- no registered resource manager.";
                syncStatus.add(new Status(Status.Severity.WARNING, statusMessage));
                continue;
            }
            
            // Either / both of: out of date, modified.
            // If not a forced sync, bring up to date ONLY if out of date and not modified.
            
            Set<WorkspaceResource> vaultResourceSet = entry.getValue();
            
            for (final WorkspaceResource moduleResource : vaultResourceSet) {
                ResourceIdentifier resourceIdentifier = moduleResource.getIdentifier();
                
                long currentRevision = resourceRevisionInfo.getResourceRevision(resourceIdentifier);
                long revisionToWhichToSync = syncToSpecificRevision ?
                        revisionNum : vaultResourceRevisionInfo.getResourceRevision(resourceIdentifier);
                
                long resourceSyncTime = resourceSyncTimeInfo.getResourceSyncTime(resourceIdentifier);
                long lastModified = resourceManager.getTimeStamp(resourceIdentifier.getResourceName());
                
                // Check for eligibility for import.
                boolean resourceWantsUpdate;
                if (force) {
                    resourceWantsUpdate = resourceSyncTime != lastModified;
                } else {
                    // This includes the case where current revision == 0 (ie. does not exist).
                    resourceWantsUpdate = currentRevision != revisionToWhichToSync;
                }
                
                if (resourceWantsUpdate) {
                    
                    // Check that the resource is not modified, such that it requires merging.
                    
                    if (force || resourceSyncTime == 0 || resourceSyncTime == lastModified) {
                        
                        // Import the updated resource.
                        WorkspaceResource.SyncTime newSyncTime = resourceManager.importResource(moduleResource, syncStatus);
                        
                        // Update the sync time and add to the set of imported resources.
                        if (newSyncTime == null) {
                            syncInfo.addImportFailure(moduleResource.getIdentifier());
                            
                        } else {
                            resourceRevisionInfo.updateResourceRevision(resourceIdentifier, revisionToWhichToSync);
                            resourceSyncTimeInfo.updateResourceSyncTime(newSyncTime);
                            syncInfo.addUpdatedResource(moduleResource.getIdentifier());
                        }
                        
                        // Update the stored module info map.
                        
                    } else {
                        // Merge / conflict
                        syncInfo.addSyncConflict(moduleResource.getIdentifier());
                    }
                }
                
                // Remove from the deleted resources set.
                deletedResourceIdentifierSet.remove(resourceIdentifier);
            }
        }
        
        // Delete deleted resources.
        // If a resource exists in the workspace but not in the repository, it is either
        //   1) Added by the workspace session, or
        //   2) Deleted from the repository.
        // We can distinguish these, because if a resource comes from a vault, it has a sync time
        //   (at least, for non-nullary std vault case) and we are in case 1.
        for (final ResourceIdentifier deletedResourceIdentifier : deletedResourceIdentifierSet) {
            String resourceType = deletedResourceIdentifier.getResourceType();
            ResourceName resourceName = deletedResourceIdentifier.getResourceName();
            
            // Remove the resource from the manager.
            ResourceManager resourceManager = virtualResourceManager.getModuleSpecificResourceManager(moduleName, resourceType);
            resourceManager.removeResource(resourceName, syncStatus);
            
            // If removed, update the sync info.
            if (!resourceManager.getResourceStore().hasFeature(resourceName)) {
                syncInfo.addDeletedResource(deletedResourceIdentifier);
                resourceRevisionInfo.removeResourceRevision(deletedResourceIdentifier);
                resourceSyncTimeInfo.removeResourceSyncTime(deletedResourceIdentifier);
            }
        }
        
        // Update the stored module info map.
        updateVaultInfo(moduleName, storedModule.getVaultInfo());
        
        CALSourceManager sourceManager = virtualResourceManager.getSourceManager(moduleName);
        if (sourceManager == null) {
            syncStatus.add(new Status(Status.Severity.WARNING, "No registered source manager."));

        } else {
            // Get the old module sources, remove the old source definition (if any) and add the new one.
            List<ModuleSourceDefinition> newModuleSources = new ArrayList<ModuleSourceDefinition>(Arrays.asList(sourceDefinitionGroup.getModuleSources()));
            for (Iterator<ModuleSourceDefinition> it = newModuleSources.iterator(); it.hasNext(); ) {
                ModuleSourceDefinition moduleSourceDefinition = it.next();
                if (moduleSourceDefinition.getModuleName().equals(moduleName)) {
                    it.remove();
                    break;
                }
            }
            ModuleSourceDefinition newModuleSourceDefinition = sourceManager.getSource(moduleName, syncStatus);
            newModuleSources.add(newModuleSourceDefinition);

            // Create the new source provider.
            ModuleSourceDefinition[] newModuleSourceArray = newModuleSources.toArray(ModuleSourceDefinition.EMPTY_ARRAY);
            this.sourceDefinitionGroup = new ModuleSourceDefinitionGroup(newModuleSourceArray);
            
            // Persist the workspace description.
            if (!isNullary() && sourceDefinitionGroup.getNModules() > 0) {
                saveWorkspaceDescription(syncStatus);
            }
        }
    }
    
    /**
     * Get the vault status of module resources in the workspace.
     * @return the vault-related status of the module resources in the workspace.
     */
    public VaultStatus getVaultStatus() {
        
        VaultStatus vaultStatus = new VaultStatus();

        // Iterate over the modules in the workspace which have any vault info.
        for (final ModuleName moduleName : moduleNameToVaultInfoMap.keySet()) {
            getVaultStatus(moduleName, vaultStatus);
        }
        
        return vaultStatus;
    }
    
    /**
     * Get the vault status for a given module.
     * @param moduleName the name of the module.
     * @return the vault-related status of the module's resources in the workspace.
     */
    public VaultStatus getVaultStatus(ModuleName moduleName) {
        VaultStatus vaultStatus = new VaultStatus();
        getVaultStatus(moduleName, vaultStatus);

        return vaultStatus;
    }
    
    /**
     * Get the vault status for a given module.
     * @param moduleName the name of the module.
     * @param vaultStatus the vault status object into which the module's vault status should be stored.
     */
    private void getVaultStatus(ModuleName moduleName, VaultStatus vaultStatus) {
        
        VaultElementInfo currentVaultInfo = moduleNameToVaultInfoMap.get(moduleName);
        String vaultDescriptor = currentVaultInfo.getVaultDescriptor();
        
        boolean isNestedVault = (currentVaultInfo instanceof VaultElementInfo.Nested);
        
        // Get the vault associated with the module.
        Vault vault = getVault(moduleName);
        if (vault == null) {
            // The vault is inaccessible.
            vaultStatus.addInaccessibleVaultInfo(moduleName, currentVaultInfo);
            return;
        }
        
        
        // Get the latest module revision.
        Status getStatus = new Status("Get status");
        StoredVaultElement.Module latestStoredModule = vault.getStoredModule(moduleName, -1, getStatus);
        
        // Check whether module exists in the vault.
        if (latestStoredModule == null) {
            if (getStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
                // An error occurs when retrieving the stored module info.
                vaultStatus.addInaccessibleModule(moduleName);
                
            } else {
                // The module does not exist in the vault.
                // Note: this means either that the module is newly-created, or that it has been deleted from the vault.
                vaultStatus.addWorkspaceOnlyModule(moduleName);
            }
            
            return;
        }
        
        
        // The rest is not applicable if nullary and this is a standard vault module.
        if (isNullary() && isStandardVaultDescriptor(vaultDescriptor)) {
            return;
        }
        
        
        // Check for out-of-date module.
        long currentModuleRevision = currentVaultInfo.getRevision();
        long latestModuleRevision = latestStoredModule.getVaultInfo().getRevision();
        
        if (latestModuleRevision > currentModuleRevision) {
            vaultStatus.addOutOfDateModule(moduleName);
        }
        
        
        // Get the revision info for the latest module revision.
        ResourceRevision.Info latestResourceRevisionInfo = latestStoredModule.getResourceRevisionInfo();
        
        
        // Resources: modified, out of date, workspace only, vault only, unmanaged (no resource manager).
        
        // Map from resource type to the resources of that type from the latest module revision.
        Map<String, Set<WorkspaceResource>> resourceTypeToLatestResourcesMap = new HashMap<String, Set<WorkspaceResource>>();
        
        // Populate the map.
        for (Iterator<WorkspaceResource> it = latestStoredModule.getResourceIterator(); it.hasNext(); ) {
            WorkspaceResource latestStoredModuleResource = it.next();
            String resourceType = latestStoredModuleResource.getResourceType();
            
            Set<WorkspaceResource> resourceTypeSet = resourceTypeToLatestResourcesMap.get(resourceType);
            if (resourceTypeSet == null) {
                resourceTypeSet = new HashSet<WorkspaceResource>();
                resourceTypeToLatestResourcesMap.put(resourceType, resourceTypeSet);
            }
            
            resourceTypeSet.add(latestStoredModuleResource);
        }
        
        // Now iterate over each of the resource types, if the module is not contained a nested vault (e.g. a Car vault).
        // We omit nested vaults because currently, resources contained in such vaults are not synchronized independently,
        // and a nested vault cannot contain multiple revisions of the same resource.
        if (!isNestedVault) {
            for (final Map.Entry<String, Set<WorkspaceResource>> entry : resourceTypeToLatestResourcesMap.entrySet()) {
                final String resourceType = entry.getKey();
                Set<WorkspaceResource> latestTypeResourceSet = entry.getValue();
                
                // Get the resource manager for this resource type and for the resource's module.
                ResourceManager resourceManager = virtualResourceManager.getModuleSpecificResourceManager(moduleName, resourceType);
                
                // Iterate over the resources for this type.
                for (final WorkspaceResource moduleResource : latestTypeResourceSet) {
                    ResourceIdentifier resourceIdentifier = moduleResource.getIdentifier();
                    
                    if (resourceManager == null) {
                        vaultStatus.addUnmanagedResource(resourceIdentifier);
                        
                    } else {
                        // Get the revisions.
                        long currentRevision = resourceRevisionInfo.getResourceRevision(resourceIdentifier);
                        long latestRevision = latestResourceRevisionInfo.getResourceRevision(resourceIdentifier);
                        
                        // Compare revisions.
                        if (latestRevision > currentRevision) {
                            vaultStatus.addOutOfDateResource(resourceIdentifier);
                        }
                        
                        long resourceSyncTime = resourceSyncTimeInfo.getResourceSyncTime(resourceIdentifier);
                        long lastModified = resourceManager.getTimeStamp(resourceIdentifier.getResourceName());
                        
                        if (resourceSyncTime == 0) {
                            // Never synced (vault only).
                            vaultStatus.addVaultOnlyResource(resourceIdentifier);
                            
                        } else if (resourceSyncTime < lastModified) {
                            vaultStatus.addModifiedResource(resourceIdentifier);
                        }
                    }
                }
            }
        }
        
        
        // Create the set of identifiers for resource in the latest stored module.
        Set<ResourceIdentifier> latestStoredModuleResourceIdentifierSet = new HashSet<ResourceIdentifier>();
        
        for (final Set<WorkspaceResource> latestTypeResourceSet : resourceTypeToLatestResourcesMap.values()) {
            for (final WorkspaceResource moduleResource : latestTypeResourceSet) {
                latestStoredModuleResourceIdentifierSet.add(moduleResource.getIdentifier());
            }
        }
        
        // Get the workspace-only resources.
        for (final ResourceManager resourceManager : virtualResourceManager.getResourceManagersForModule(moduleName)) {
            ResourceStore resourceStore = resourceManager.getResourceStore();
            
            if (!(resourceStore instanceof ResourceStore.Module)) {
                continue;
            }
            
            for (Iterator<WorkspaceResource> it = ((ResourceStore.Module)resourceStore).getResourceIterator(moduleName); it.hasNext(); ) {
                WorkspaceResource moduleResource = it.next();
                ResourceIdentifier resourceIdentifier = moduleResource.getIdentifier();
                if (!latestStoredModuleResourceIdentifierSet.contains(resourceIdentifier)) {
                    vaultStatus.addWorkspaceOnlyResource(resourceIdentifier);
                }
            }
        }
    }

    /**
     * @return the resource managers for the given module.
     */
    public Set<ResourceManager> getResourceManagersForModule(ModuleName moduleName) {
        return virtualResourceManager.getResourceManagersForModule(moduleName);
    }
    
    /**
     * Get the abstract path to a given resource.
     * @param identifier the identifier of the resource.
     * @return the path to that resource, or null if there is no such path
     *   (eg. a resource manager is not registered for that resource type).
     */
    public ResourcePath.FilePath getResourcePath(ResourceIdentifier identifier) {
        ResourceManager resourceManager = virtualResourceManager.getResourceManagerForResource(identifier);
        if (resourceManager == null) {
            return null;
        }
        // Note that the workspace uses path-based resource stores.
        return ((ResourcePathStore)resourceManager.getResourceStore()).getResourcePath(identifier.getResourceName());
    }
    
    /**
     * Returns a DependencyFinder that can be used for finding module dependencies.
     * @param rootModuleNames the names of the modules whose dependencies are to be collected.
     * @return a new DependencyFinder instance.
     */
    public DependencyFinder getDependencyFinder(Collection<ModuleName> rootModuleNames) {
        return new DependencyFinder(this, rootModuleNames);
    }
    
    /**
     * A helper class for finding module dependencies.
     * 
     * @author James Wright
     */
    public static final class DependencyFinder {

        /** workspace to search */
        private final CALWorkspace workspace;
        
        /** Set of module names that we are checking the dependencies of */
        private final SortedSet<ModuleName> rootModuleSet;
        
        /** Set of module names that are imported by rootModuleSet */
        private final SortedSet<ModuleName> importedModuleSet = new TreeSet<ModuleName>();
        
        private DependencyFinder(CALWorkspace workspace, Collection<ModuleName> rootModules) {
            if(workspace == null || rootModules == null) {
                throw new NullPointerException();
            }
            this.workspace = workspace;
            this.rootModuleSet = new TreeSet<ModuleName>(rootModules);
            
            for(final ModuleName moduleName: rootModuleSet) {
                findModuleDependencies(moduleName);
            }
        }
        
        /**
         * Add all the dependencies for module moduleName to importedModuleSet.
         * @param moduleName name of module
         */
        private void findModuleDependencies(ModuleName moduleName) {
            MetaModule metaModule = workspace.getMetaModule(moduleName);
            ModuleTypeInfo moduleTypeInfo = metaModule.getTypeInfo();
            
            for(int i = 0, nImports = moduleTypeInfo.getNImportedModules(); i < nImports; i++) {
                ModuleTypeInfo importedModule = moduleTypeInfo.getNthImportedModule(i);
                ModuleName importedModuleName = importedModule.getModuleName();
                if(rootModuleSet.contains(importedModuleName) || importedModuleSet.contains(importedModuleName)) {
                    continue;
                }
                importedModuleSet.add(importedModuleName);
                findModuleDependencies(importedModuleName);
            }
        }
        
        /**
         * @return SortedSet of root module names
         */
        public final SortedSet<ModuleName> getRootSet() {
            return Collections.unmodifiableSortedSet(rootModuleSet);
        }
        
        /**
         * @return SortedSet of names of modules imported (directly or indirectly) by
         *          the modules in the root set.
         */
        public final SortedSet<ModuleName> getImportedModulesSet() {
            return Collections.unmodifiableSortedSet(importedModuleSet);
        }
    }
}
