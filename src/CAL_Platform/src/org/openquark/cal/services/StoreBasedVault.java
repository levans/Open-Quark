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
 * StoreBasedVault.java
 * Creation date: Jul 30, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.metadata.MetadataManager;
import org.openquark.cal.metadata.MetadataStore;
import org.openquark.util.EmptyIterator;
import org.openquark.util.IteratorChain;



/**
 * A StoreBasedVault stores and retrieves module resources using resource stores for each type of resource.
 * @author Edward Lam
 */
public abstract class StoreBasedVault implements Vault {
    
    /** The Resource manager registry */
    private final ResourceManagerRegistry resourceManagerRegistry;
    
    /**
     * A stored module from this Vault.
     * @author Edward Lam
     */
    private class StoreBasedStoredModule implements StoredVaultElement.Module {
        private final ModuleName moduleName;
        private final int revisionNumber;

        /**
         * Constructor for a StoreBasedStoredModule.
         * @param moduleName the name of the module represented by this stored module.
         * @param revisionNumber the revision number assigned to this stored module.
         */
        StoreBasedStoredModule(ModuleName moduleName, int revisionNumber) {
            this.moduleName = moduleName;
            this.revisionNumber = revisionNumber;
        }
        
        /**
         * {@inheritDoc}
         */
        public String getName() {
            return moduleName.toSourceText();
        }

        /**
         * {@inheritDoc}
         */
        public ResourceRevision.Info getResourceRevisionInfo() {
            List<ResourceRevision> revisionList = new ArrayList<ResourceRevision>();

            for (Iterator<WorkspaceResource> it = getResourceIterator(); it.hasNext(); ) {
                WorkspaceResource workspaceResource = it.next();
                
                ResourceIdentifier resourceIdentifier = workspaceResource.getIdentifier();
                long revisionNum = workspaceResource.getTimeStamp();
                
                ResourceRevision resourceRevision = new ResourceRevision(resourceIdentifier, revisionNum);
                revisionList.add(resourceRevision);
            }

            return new ResourceRevision.Info(revisionList);
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<WorkspaceResource> getResourceIterator() {
            return StoreBasedVault.this.getModuleResourceIterator(moduleName);
        }

        /**
         * {@inheritDoc}
         */
        public VaultElementInfo getVaultInfo() {
            return StoreBasedVault.this.getVaultInfo(getName(), revisionNumber);
        }
    }
    

    /**
     * A stored workspace declaration from this Vault.
     * @author Edward Lam
     */
    private class StoreBasedWorkspaceDeclaration implements StoredVaultElement.WorkspaceDeclaration {
        private final WorkspaceDeclarationFeatureName workspaceDeclarationName;
        private final int revisionNumber;

        /**
         * Constructor for a StoreBasedWorkspaceDeclaration.
         * @param workspaceDeclarationName
         * @param revisionNumber the revision number assigned to this stored module.
         */
        StoreBasedWorkspaceDeclaration(String workspaceDeclarationName, int revisionNumber) {
            this.revisionNumber = revisionNumber;
            this.workspaceDeclarationName = WorkspaceDeclarationFeatureName.getWorkspaceDeclarationFeatureName(workspaceDeclarationName);
        }
        
        /**
         * {@inheritDoc}
         */
        public InputStream getInputStream() {
            WorkspaceDeclarationManager workspaceDeclarationManager = resourceManagerRegistry.getWorkspaceDeclarationManager();
            return workspaceDeclarationManager.getResourceStore().getInputStream(new ResourceName(workspaceDeclarationName));
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return workspaceDeclarationName.getName();
        }

        /**
         * {@inheritDoc}
         */
        public VaultElementInfo getVaultInfo() {
            return StoreBasedVault.this.getVaultInfo(getName(), revisionNumber);
        }
    }
    
    /**
     * A stored CAL Archive (Car) from this Vault.
     *
     * @author Joseph Wong
     */
    private static class StoreBasedCar implements StoredVaultElement.Car {
        
        /**
         * The CarVault that corresponds to this Car. A Car file is at the same time both a resource and a vault (i.e.
         * it is a container of other resources).
         */
        private final CarVault carVault;
        
        /**
         * Factory method for constructing an instance of StoreBasedCar.
         * 
         * @param outerVault the outer vault which contains the Car.
         * @param carName the name of the Car.
         * @param revisionNumber the revision number of the Car.
         * @param status the tracking statuc object.
         * @return a StoreBasedCar instance representing the Car, or null if the Car file cannot be accessed.
         */
        private static StoreBasedCar make(StoreBasedVault outerVault, String carName, int revisionNumber, Status status) {
            
            CarVault carVault = CarVault.make(outerVault, getVaultInfo(outerVault, carName, revisionNumber), carName, revisionNumber, status);
            
            if (carVault == null) {
                return null;
            } else {
                return new StoreBasedCar(carVault);
            }
        }

        /**
         * Private constructor for StoreBasedCar.
         * @param carVault the CarVault that corresponds to this Car.
         */
        private StoreBasedCar(CarVault carVault) {
            if (carVault == null) {
                throw new NullPointerException();
            }
            
            this.carVault = carVault;
        }
        
        ////=====================================
        /// StoredVaultElement.Car implementation
        //

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return carVault.getCarFeatureName().getName();
        }

        /**
         * {@inheritDoc}
         */
        public VaultElementInfo getVaultInfo() {
            return getVaultInfo((StoreBasedVault)carVault.getOuterVault(), getName(), carVault.getCarRevisionNumber());
        }
        
        /**
         * Returns the vault info for the named Car in its containing vault.
         * @param outerVault the outer vault which contains the Car.
         * @param carName the name of the Car.
         * @param revisionNumber the revision number of the Car.
         * @return the vault info for the Car (as a resource in the outer vault).
         */
        private static VaultElementInfo.Basic getVaultInfo(StoreBasedVault outerVault, String carName, int revisionNumber) {
            return VaultElementInfo.makeBasic(outerVault.getVaultProvider().getVaultDescriptor(),
                carName,
                outerVault.getLocationString(),
                revisionNumber);
        }
        
        /**
         * {@inheritDoc}
         */
        public CarVault getCarVault() {
            return carVault;
        }
        
        /**
         * {@inheritDoc}
         */
        public InputStream getWorkspaceSpec(String specName, Status status) {
            return carVault.getCarAccessor().getWorkspaceSpec(specName, status);
        }
    }
    
    /**
     * Constructor for a StoreBasedVault.
     * @param sourceStore the source store to use.
     * @param metadataStore the metadata store to use.
     * @param gemDesignStore the design store to use.
     * @param workspaceDeclarationStore the workspace declaration store to use.
     * @param carStore the Car store to use.
     * @param userResourceStore the user resource store to use.
     */
    protected StoreBasedVault(CALSourceStore sourceStore, MetadataStore metadataStore,
                              GemDesignStore gemDesignStore, WorkspaceDeclarationStore workspaceDeclarationStore,
                              CarStore carStore, UserResourceStore userResourceStore) {

        CALSourceManager vaultSourceManager = new CALSourceManager(sourceStore);
        MetadataManager vaultMetadataManager = new MetadataManager(metadataStore);
        GemDesignManager vaultGemDesignManager = new GemDesignManager(gemDesignStore);
        WorkspaceDeclarationManager vaultWorkspaceDeclarationManager = new WorkspaceDeclarationManager(workspaceDeclarationStore);
        CarManager vaultCarManager = new CarManager(carStore);
        UserResourceManager vaultUserResourceManager = new UserResourceManager(userResourceStore);
        
        resourceManagerRegistry = new ResourceManagerRegistry();
        resourceManagerRegistry.registerResourceManager(vaultSourceManager);
        resourceManagerRegistry.registerResourceManager(vaultMetadataManager);
        resourceManagerRegistry.registerResourceManager(vaultGemDesignManager);
        resourceManagerRegistry.registerResourceManager(vaultWorkspaceDeclarationManager);
        resourceManagerRegistry.registerResourceManager(vaultCarManager);
        resourceManagerRegistry.registerResourceManager(vaultUserResourceManager);
    }

    /**
     * {@inheritDoc}
     */
    public int putStoredModule(ModuleName moduleName, CALWorkspace workspace, Status putStatus) {

        // Write out the sources.
        boolean sourceSaved = saveSource(moduleName, workspace, putStatus);
        
        // If the sources were successfully written, write out the designs and metadata.
        if (sourceSaved) {
            MetaModule metaModule = workspace.getMetaModule(moduleName);
            saveMetadata(metaModule, putStatus);
            saveDesigns(metaModule, putStatus);
        }
        
        return 0;
    }
    
    /**
     * Save the source for a given metamodule into the vault.
     * @param moduleName the name of the module whose source to save.
     * @param workspace the workspace containing the source.
     * @param status the tracking status object.
     * @return whether the source was successfully saved.
     */
    private boolean saveSource(ModuleName moduleName, CALWorkspace workspace, Status status) {
        CALSourceManager sourceManager = resourceManagerRegistry.getSourceManager();
        if (sourceManager == null) {
            status.add(new Status(Status.Severity.WARNING, "No registered source manager for this vault: " + toString(), null));
            return false;
        }
        
        ModuleSourceDefinition sourceDefinition = workspace.getSourceDefinition(moduleName);
        if (sourceManager.importResource(sourceDefinition, status) == null) {
            status.add(new Status(Status.Severity.WARNING, "Could not save source for module: " + moduleName, null));
        }
        
        return false;
    }

    /**
     * Save the metadata for a given metamodule into the vault.
     * @param metaModule the metamodule whose metadata to save.
     * @param status the tracking status object.
     */
    private void saveMetadata(MetaModule metaModule, Status status) {

        MetadataManager metadataManager = resourceManagerRegistry.getMetadataManager();
        if (metadataManager == null) {
            status.add(new Status(Status.Severity.WARNING, "No registered metadata manager for this vault: " + toString(), null));
            return;
        }
        MetadataStore metadataStore = (MetadataStore)metadataManager.getResourceStore();
        
        // Clear old metadata.
        metadataStore.removeModuleResources(metaModule.getName(), status);

        // Get the features in the module.
        Set<CALFeatureName> moduleFeatureNames = metaModule.getFeatureNames();

        // Save metadata for each of the features.
        for (final CALFeatureName featureName : moduleFeatureNames) {
            List<ResourceName> metadataResourceNames = metaModule.getMetadataResourceNamesForAllLocales(featureName);
            
            for (int i = 0, n = metadataResourceNames.size(); i < n; i++) {
                ResourceName resourceName = metadataResourceNames.get(i);
                Locale locale = LocalizedResourceName.localeOf(resourceName);
                
                if (!metadataManager.saveMetadata(metaModule.getMetadata(featureName, locale), status)) {
                    status.add(new Status(Status.Severity.WARNING, "Could not save metadata for feature: " + featureName.toString(), null));
                }
            }
        }
    }

    /**
     * Save the designs for a given metamodule into the vault.
     * @param metaModule the metamodule whose designs to save.
     * @param status the tracking status object.
     */
    private void saveDesigns(MetaModule metaModule, Status status) {
        GemDesignManager gemDesignManager = resourceManagerRegistry.getDesignManager();
        if (gemDesignManager == null) {
            status.add(new Status(Status.Severity.WARNING, "No registered gem design manager for this vault: " + toString(), null));
            return;
        }

        // For now, only functional agents have designs
        
        // Functional agents
        int nGemEntities = metaModule.getNGemEntities();
        for (int i = 0; i < nGemEntities; i++) {
            GemEntity gemEntity = metaModule.getNthGemEntity(i);
            GemDesign gemDesign = gemEntity.getDesign(status);
            
            if (gemDesign != null) {
                gemDesignManager.saveGemDesign(gemDesign, status);
            }
        }
    }

    /**
     * {@inheritDoc}
     * The revision number is unchecked..
     */
    public StoredVaultElement.Module getStoredModule(final ModuleName moduleName, int revisionNumber, Status status) {
        if (getSourceStore().hasFeature(new ResourceName(CALFeatureName.getModuleFeatureName(moduleName)))) {
            return new StoreBasedStoredModule(moduleName, 0);
        }
        return null;
    }
    
    /**
     * @return the backing source store, or null if there isn't one.
     */
    CALSourceStore getSourceStore() {
        CALSourceManager sourceManager = resourceManagerRegistry.getSourceManager();
        if (sourceManager == null) {
            return null;
        }
        return (CALSourceStore)sourceManager.getResourceStore();
    }

    /**
     * @return the associated WorkspaceDeclarationStore, or null if there isn't one.
     */
    private WorkspaceDeclarationStore getWorkspaceDeclarationStore() {
        WorkspaceDeclarationManager workspaceDeclarationManager = resourceManagerRegistry.getWorkspaceDeclarationManager();
        if (workspaceDeclarationManager == null) {
            return null;
        }
        
        return (WorkspaceDeclarationStore)workspaceDeclarationManager.getResourceStore();
    }
    
    /**
     * {@inheritDoc}
     */
    public int putWorkspaceDeclaration(String workspaceDeclarationName, WorkspaceDeclaration workspaceDeclaration, Status putStatus) {
        WorkspaceDeclarationManager workspaceDeclarationManager = resourceManagerRegistry.getWorkspaceDeclarationManager();
        if (workspaceDeclarationManager == null) {
            putStatus.add(new Status(Status.Severity.WARNING, "No registered workspace declaration manager for this vault: " + toString(), null));
            return 0;
        }

        // TODOEL: Clear the old workspace definition?
        
        // Save the definition.
        if (!workspaceDeclarationManager.saveDeclaration(workspaceDeclarationName, workspaceDeclaration, putStatus)) {
            putStatus.add(new Status(Status.Severity.WARNING, "Could not save workspace declaration: " + workspaceDeclarationName, null));
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     * The revision number is unchecked..
     */
    public StoredVaultElement.WorkspaceDeclaration getWorkspaceDeclaration(String workspaceDeclarationName, int revisionNumber, Status status) {
        WorkspaceDeclarationStore workspaceDeclarationStore = getWorkspaceDeclarationStore();
        if (workspaceDeclarationStore == null) {
            return null;
        }
        
        if (workspaceDeclarationStore.hasFeature(new ResourceName(WorkspaceDeclarationFeatureName.getWorkspaceDeclarationFeatureName(workspaceDeclarationName)))) {
            return new StoreBasedWorkspaceDeclaration(workspaceDeclarationName, 0);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getWorkspaceDeclarationDebugInfo(String workspaceDeclarationName, int revisionNumber) {
        WorkspaceDeclarationStore workspaceDeclarationStore = getWorkspaceDeclarationStore();
        if (workspaceDeclarationStore == null) {
            return null;
        }
        
        return workspaceDeclarationStore.getDebugInfo(
            new ResourceName(WorkspaceDeclarationFeatureName.getWorkspaceDeclarationFeatureName(workspaceDeclarationName)));
    }

    /**
     * {@inheritDoc}
     */
    public final ModuleName[] getAvailableModules(Status status) {
        Set<ModuleName> moduleNames = getSourceStore().getModuleNames();
        return moduleNames.toArray(new ModuleName[moduleNames.size()]);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String[] getAvailableWorkspaceDeclarations(Status status) {
        
        WorkspaceDeclarationStore workspaceDeclarationStore = getWorkspaceDeclarationStore();
        if (workspaceDeclarationStore == null) {
            return null;
        }
        
        Set<ResourceName> workspaceNames = workspaceDeclarationStore.getWorkspaceNames();
        String[] result = new String[workspaceNames.size()];
        
        int i = 0;
        for (final ResourceName resourceName : workspaceNames) {
            WorkspaceDeclarationFeatureName featureName = (WorkspaceDeclarationFeatureName)resourceName.getFeatureName();
            result[i] = featureName.getName();
            i++;
        }
        
        return result;
    }
    
    /**
     * @param moduleName the name of a module in this vault.
     * @return An iterator over the resources in this StoredModule.
     */
    public Iterator<WorkspaceResource> getModuleResourceIterator(ModuleName moduleName) {
        
        // A list of iterators over ModuleResources.
        final List<Iterator<WorkspaceResource>> iteratorList = new ArrayList<Iterator<WorkspaceResource>>();

        for (final ResourceManager resourceManager : resourceManagerRegistry.getResourceManagers()) {
            if (resourceManager instanceof ModuleResourceManager) {
                iteratorList.add(((ResourceStore.Module)resourceManager.getResourceStore()).getResourceIterator(moduleName));
            }
        }
        
        // Iterator composed of resource store iterators.
        return iteratorList.isEmpty() ? 
                EmptyIterator.<WorkspaceResource>emptyIterator() : 
                    new IteratorChain<WorkspaceResource>(iteratorList);
    }

    /**
     * {@inheritDoc}
     */
    public RevisionHistory getModuleRevisionHistory(ModuleName moduleName) {
        boolean moduleExists = getSourceStore().hasFeature(new ResourceName(CALFeatureName.getModuleFeatureName(moduleName)));
        return getRevisionHistory(moduleExists);
    }
    
    
    /**
     * {@inheritDoc}
     */
    public RevisionHistory getWorkspaceDeclarationRevisionHistory(String workspaceDeclarationName) {
        WorkspaceDeclarationStore workspaceDeclarationStore = getWorkspaceDeclarationStore();
        if (workspaceDeclarationStore == null) {
            return null;
        }
        
        boolean workspaceDeclarationExists =
            workspaceDeclarationStore.hasFeature(new ResourceName(WorkspaceDeclarationFeatureName.getWorkspaceDeclarationFeatureName(workspaceDeclarationName)));
        
        return getRevisionHistory(workspaceDeclarationExists);
    }
    
    /**
     * {@inheritDoc}
     */
    public int putCar(Car car, Status putStatus) {
        CarManager carManager = resourceManagerRegistry.getCarManager();
        if (carManager == null) {
            putStatus.add(new Status(Status.Severity.WARNING, "No registered Car manager for this vault: " + toString(), null));
            return 0;
        }
        
        if (carManager.importResource(car, putStatus) == null) {
            putStatus.add(new Status(Status.Severity.WARNING, "Could not save Car: " + car.getName(), null));
        }
        
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public StoredVaultElement.Car getCar(String carName, int revisionNumber, Status status) {
        CarStore carStore = getCarStore();
        if (carStore == null) {
            return null;
        }
        
        if (carStore.hasFeature(new ResourceName(CarFeatureName.getCarFeatureName(carName)))) {
            return StoreBasedCar.make(this, carName, 0, status);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getAvailableCars(Status status) {
        
        CarStore carStore = getCarStore();
        if (carStore == null) {
            return null;
        }
        
        Set<ResourceName> carNames = carStore.getCarNames();
        String[] result = new String[carNames.size()];
        
        int i = 0;
        for (final ResourceName resourceName : carNames) {
            CarFeatureName featureName = (CarFeatureName)resourceName.getFeatureName();
            result[i] = featureName.getName();
            i++;
        }
        
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public RevisionHistory getCarRevisionHistory(String carName) {
        
        CarStore carStore = getCarStore();
        if (carStore == null) {
            return null;
        }
        
        boolean carExists = carStore.hasFeature(new ResourceName(CarFeatureName.getCarFeatureName(carName)));
        
        return getRevisionHistory(carExists);
    }
    
    /**
     * {@inheritDoc}
     */
    public Car getCarAsResource(String carName, int revisionNumber, Status status) {
        
        CarStore carStore = getCarStore();
        if (carStore == null) {
            return null;
        }
        
        return carStore.getCar(carName);
    }
    
    /**
     * @return the associated CarStore, or null if there isn't one.
     */
    private CarStore getCarStore() {
        CarManager carManager = resourceManagerRegistry.getCarManager();
        if (carManager == null) {
            return null;
        }
        
        return (CarStore)carManager.getResourceStore();
    }

    /**
     * @param resourceExists whether the given resource exists in this vault.
     * @return the RevisionHistory for that resource.
     *   If the resource exists, there will be a single revision which will be 0.
     *   If the resource does not exist, the root and latest revisions will be -1 and there will be no available revisions.
     */
    private RevisionHistory getRevisionHistory(boolean resourceExists) {
        if (resourceExists) {
            return new RevisionHistory() {

                public int getRootRevision() {
                    return 0;
                }

                public int getLatestRevision() {
                    return 0;
                }

                public int[] getAvailableRevisions() {
                    return new int[] {0};
                }
            };
            
        } else {
            return new RevisionHistory() {

                public int getRootRevision() {
                    return -1;
                }

                public int getLatestRevision() {
                    return -1;
                }

                public int[] getAvailableRevisions() {
                    return new int[] {};
                }
            };
        }
    }
    
    /**
     * Get a vault info for an element in this vault.
     * @param elementName the name of the element.
     * @param revisionNumber the revision number for the vault element.
     * @return vault info for vault elements from this vault.
     */
    protected VaultElementInfo getVaultInfo(String elementName, int revisionNumber) {
        return VaultElementInfo.makeBasic(getVaultProvider().getVaultDescriptor(),
                                    elementName,
                                    StoreBasedVault.this.getLocationString(),
                                    revisionNumber);
    }
}
