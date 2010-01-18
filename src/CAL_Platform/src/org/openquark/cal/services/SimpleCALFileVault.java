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
 * SimpleCALFileVault.java
 * Creation date: Jul 30, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.metadata.MetadataStore;
import org.openquark.util.EmptyIterator;


/**
 * A Vault which wraps a module specified by a simple .cal file.
 * @author Edward Lam
 */
public class SimpleCALFileVault extends StoreBasedVault {
    
    /** The name of the module. */
    private final ModuleName moduleName;
    
    /** The path to the module. */
    private final String locationString;
    
    /** The stored module returned by the vault. */
    private final StoredVaultElement.Module storedModule;

    /** The vault provider for this class. */
    private static final VaultProvider vaultProvider = new VaultProvider() {
        
        /**
         * {@inheritDoc}
         */
        public String getVaultDescriptor() {
            return "SimpleCALFile";
        }

        /**
         * {@inheritDoc}
         */
        public Vault getVault(String locationString, VaultAuthenticationManager authenticationManager) {
            if (locationString == null) {
                return null;
            }
            
            File file = WorkspaceLoader.fileFromPath(locationString);
            if (file == null) {
                return null;
            }

            ModuleName moduleName = null;
            
            // figure out the module name by reading the file, parsing it, and finding out through the source model
            try {
                FileReader reader = new FileReader(file);
                try {
                    StringBuilder builder = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line).append('\n');
                    }
                    
                    String source = builder.toString();
                    
                    SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(source);
                    
                    if (moduleDefn != null) {
                        moduleName = SourceModel.Name.Module.toModuleName(moduleDefn.getModuleName());
                    }

                } finally {
                    reader.close();
                }
            } catch (IOException e) {
                return null;
            }

            if (moduleName == null) {
                return null;
            }
            
            CALSourceStore sourceStore = getSourceStore(locationString, moduleName);
            if (sourceStore == null) {
                return null;
            }

            return new SimpleCALFileVault(moduleName, locationString, sourceStore);
        }
    };

    
    /**
     * A CALSourceStore based on a single file.
     * @author Edward Lam
     */
    private static final class CALFileSourceStore implements CALSourceStore {

        /** The name of the module represented by the file.*/
        private final ModuleName moduleName;
        
        /** The file itself. */
        private final File calSourceFile;
        
        /**
         * Constructor for a CALFileSourceStore.
         * @param moduleName the name of the module represented by the file.
         * @param calSourceFile the file containing the source definition.
         */
        private CALFileSourceStore(ModuleName moduleName, File calSourceFile) {
            this.moduleName = moduleName;
            this.calSourceFile = calSourceFile;
        }

        /**
         * Get the module name from the resource identifier for a module.
         * @param resourceName the resource name whose feature name corresponds to a module name.
         * @return the corresponding module name.
         * @throws IllegalArgumentException if the feature name does not correspond to a module name.
         */
        private ModuleName checkModuleName(ResourceName resourceName) {
            FeatureName featureName = resourceName.getFeatureName();
            
            if (featureName.getType() != CALFeatureName.MODULE) {
                throw new IllegalArgumentException("The given feature does not correspond to a module name.");
            }
            return ((CALFeatureName)featureName).toModuleName();
        }
        
        /**
         * {@inheritDoc}
         */
        public InputStream getInputStream(ResourceName resourceName) {
            ModuleName moduleName = checkModuleName(resourceName);
            
            if (calSourceFile == null || !this.moduleName.equals(moduleName)) {
                return null;
            }

            try {
                return new FileInputStream(calSourceFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public OutputStream getOutputStream(ResourceName resourceName, Status status) {
            ModuleName moduleName = checkModuleName(resourceName);
            
            if (calSourceFile == null || !this.moduleName.equals(moduleName)) {
                return null;
            }
            try {
                return new FileOutputStream(calSourceFile);

            } catch (FileNotFoundException e) {
                status.add(new Status(Status.Severity.ERROR, "CAL source for module " + moduleName + " not writeable.", e));
                return null;
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public String getDebugInfo(ResourceName resourceName) {
            ModuleName moduleName = checkModuleName(resourceName);
            
            if (calSourceFile == null || !this.moduleName.equals(moduleName)) {
                return null;
            }

            return "from SimpleCALFile: " + calSourceFile.getAbsolutePath();
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasFeature(ResourceName resourceName) {
            ModuleName moduleName = checkModuleName(resourceName);
            return calSourceFile != null && calSourceFile.exists() && this.moduleName.equals(moduleName);
        }

        /**
         * {@inheritDoc}
         */
        public Set<ModuleName> getModuleNames() {
            return Collections.singleton(moduleName);
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<WorkspaceResource> getResourceIterator() {
            return EmptyIterator.emptyIterator();
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<WorkspaceResource> getResourceIterator(ModuleName moduleName) {
            return EmptyIterator.emptyIterator();
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeResource(ResourceName resourceName, Status removeStatus) {
            removeStatus.add(new Status(Status.Severity.ERROR, "Cannot remove source from this location."));
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeModuleResources(ModuleName moduleName, Status removeStatus) {
            removeStatus.add(new Status(Status.Severity.ERROR, "Cannot remove source for module " + moduleName + " from this location."));
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeAllResources(Status removeStatus) {
            // What to do..?
            removeStatus.add(new Status(Status.Severity.ERROR, "Cannot remove source from this location."));
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status renameStatus) {
            // Renaming is not supported with this class.
            renameStatus.add(new Status(Status.Severity.ERROR, "Cannot rename a source at this location."));
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public long getTimeStamp(ResourceName resourceName) {
            ModuleName moduleName = checkModuleName(resourceName);
            
            if (calSourceFile != null && this.moduleName.equals(moduleName)) {
                return calSourceFile.lastModified();
            }
            
            return -1;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isWriteable() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isWriteable(ResourceName resourceName) {
            ModuleName moduleName = checkModuleName(resourceName);
            
            if (calSourceFile != null && this.moduleName.equals(moduleName)) {
                return calSourceFile.canWrite();
            }
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean isRemovable(ResourceName resourceName) {
            return isWriteable(resourceName);
        }

        /**
         * {@inheritDoc}
         */
        public String getResourceType() {
            return ModuleSourceDefinition.RESOURCE_TYPE;
        }

        /**
         * {@inheritDoc}
         */
        public List<ResourceName> getModuleResourceNameList(ModuleName moduleName) {
            if (this.moduleName.equals(moduleName)) {
                return Collections.singletonList(new ResourceName(CALFeatureName.getModuleFeatureName(moduleName)));
            }
            return Collections.emptyList();
        }
    }

    /**
     * A stored module containing a single source definition.
     * @author Edward Lam
     */
    private final class StoredModule implements StoredVaultElement.Module {

        private final ModuleName moduleName;
        private final ModuleSourceDefinition sourceDefinition;

        /**
         * Constructor for a StoredModule.
         * @param moduleName the name of the module.
         * @param sourceDefinition the source definition for the module.
         */
        private StoredModule(ModuleName moduleName, ModuleSourceDefinition sourceDefinition) {
            this.moduleName = moduleName;
            this.sourceDefinition = sourceDefinition;
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
            
            String resourceType = ModuleSourceDefinition.RESOURCE_TYPE;
            CALFeatureName featureName = CALFeatureName.getModuleFeatureName(moduleName);
            ResourceIdentifier identifier = ResourceIdentifier.make(resourceType, featureName);
            long revision = sourceDefinition.getTimeStamp();
            
            ResourceRevision resourceRevision = new ResourceRevision(identifier, revision);
            
            return new ResourceRevision.Info(Collections.singletonList(resourceRevision));
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<WorkspaceResource> getResourceIterator() {
            return Collections.<WorkspaceResource>singletonList(sourceDefinition).iterator();
        }

        /**
         * {@inheritDoc}
         */
        public VaultElementInfo getVaultInfo() {
            return VaultElementInfo.makeBasic(getVaultProvider().getVaultDescriptor(),
                                        getName(),
                                        SimpleCALFileVault.this.locationString,
                                        0);
        }
    }

    /**
     * An empty module resource store for metadata.
     * @author Edward Lam
     */
    private static class EmptyMetadataStore extends EmptyResourceStore.Module implements MetadataStore {

        /**
         * Constructor for an EmptyMetadataStore.
         */
        EmptyMetadataStore() {
            super(WorkspaceResource.METADATA_RESOURCE_TYPE);
        }

        /**
         * {@inheritDoc}
         */
        public List<ResourceName> getMetadataResourceNamesForAllLocales(CALFeatureName featureName) {
            return Collections.emptyList();
        }
    }
    
    /**
     * An empty module resource store for gem designs.
     * @author Edward Lam
     */
    private static class EmptyGemDesignStore extends EmptyResourceStore.Module implements GemDesignStore {

        /**
         * Constructor for an EmptyGemDesignStore.
         */
        EmptyGemDesignStore() {
            super(WorkspaceResource.GEM_DESIGN_RESOURCE_TYPE);
        }
    }
    
    /**
     * An empty resource store for workspace declarations.
     * @author Edward Lam
     */
    private static class EmptyWorkspaceDeclarationStore extends EmptyResourceStore.Workspace implements WorkspaceDeclarationStore {

        /**
         * Constructor for a EmptyWorkspaceDeclarationStore.
         */
        EmptyWorkspaceDeclarationStore() {
            super(WorkspaceResource.WORKSPACE_DECLARATION_RESOURCE_TYPE);
        }
    }
    
    /**
     * An empty resource store for CAL Archives (Cars).
     *
     * @author Joseph Wong
     */
    private static class EmptyCarStore extends EmptyResourceStore.Car implements CarStore {
        
        /**
         * Constructor for a EmptyCarStore.
         */
        EmptyCarStore() {
            super(WorkspaceResource.CAR_RESOURCE_TYPE);
        }
    }
    
    /**
     * An empty resource store for user resources.
     *
     * @author Joseph Wong
     */
    private static class EmptyUserResourceStore extends EmptyResourceStore.Module implements UserResourceStore {
        
        /**
         * Constructor for a EmptyUserResourceStore.
         */
        EmptyUserResourceStore() {
            super(WorkspaceResource.USER_RESOURCE_TYPE);
        }
    }
    
    
    /** A metadata store which doesn't contain any metadata. */
    private static final MetadataStore metadataStore = new EmptyMetadataStore();
    
    /** A design store which doesn't contain any designs. */
    private static final GemDesignStore designStore = new EmptyGemDesignStore();
    
    /** A workspace declaration store which doesn't contain any workspace declarations. */
    private static final WorkspaceDeclarationStore workspaceDeclarationStore = new EmptyWorkspaceDeclarationStore();
    
    /** A Car store which doesn't contain any Cars. */
    private static final CarStore carStore = new EmptyCarStore();
    
    /** A user resource store which doesn't contain any user resources. */
    private static final UserResourceStore userResourceStore = new EmptyUserResourceStore();
    
    /**
     * Constructor for a SimpleCALFileVault.
     * Obtain instances via the provider.
     */
    private SimpleCALFileVault(final ModuleName moduleName, String storedModulePath, CALSourceStore sourceStore) {
        super(sourceStore, metadataStore, designStore, workspaceDeclarationStore, carStore, userResourceStore);

        this.locationString = storedModulePath;
        this.moduleName = moduleName;

        ModuleSourceDefinition sourceDefinition = new FilePathModuleSourceDefinition(storedModulePath, moduleName);
        this.storedModule = new StoredModule(moduleName, sourceDefinition);
    }
    
    /**
     * Get the source store for a module.
     * @param storedModulePath the path to the module.
     * @param aModuleName the name of the module.
     * @return the source store for the module, or null of the path is invalid.
     */
    private static CALSourceStore getSourceStore(String storedModulePath, ModuleName aModuleName) {
        File fileFromLine = WorkspaceLoader.fileFromPath(storedModulePath);
        if (fileFromLine == null) {
            String errorString = "WorkspaceLoader: \"" + storedModulePath + "\" is not a valid file path.\n" +
                                 "File paths must be specified either as a path or as a URL.";
            CALWorkspace.SERVICES_LOGGER.log(Level.WARNING, errorString);
            return null;
        }
                
        return new CALFileSourceStore(aModuleName, fileFromLine);
    }

    /**
     * Get an instance of this class by path.
     * @param filePath the path to the file.
     * @return a Vault which wraps the module specified by the given file.
     */
    public static SimpleCALFileVault getSimpleCALFileVault(String filePath) {
        return (SimpleCALFileVault)getVaultClassProvider().getVault(filePath, null);
    }

    /**
     * Get an instance of this class by file.
     * @param file the file.
     * @return a Vault which wraps the module specified by the given file.
     */
    public static SimpleCALFileVault getSimpleCALFileVault(File file) {
        String pathString = file.toURI().toASCIIString();
        return getSimpleCALFileVault(pathString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int putStoredModule(ModuleName moduleName, CALWorkspace workspace, Status putStatus) {

        // Check that the module is the one we accept.
        if (!moduleName.equals(this.moduleName)) {
            String message = "Cannot export module " + moduleName + " to this vault.\n" +
                             "This vault only accepts the " + this.moduleName + " module.";

            putStatus.add(new Status(Status.Severity.ERROR, message, null));
        }
        
        // Warn that designs and metadata aren't saved.
        putStatus.add(new Status(Status.Severity.WARNING, "Designs and metadata not saved.", null));
        
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoredVaultElement.Module getStoredModule(ModuleName moduleName, int revisionNumber, Status status) {
        if (!moduleName.equals(this.moduleName)) {
            return null;
        }
        
        return storedModule;
    }

    /**
     * {@inheritDoc}
     */
    public VaultProvider getVaultProvider() {
        return getVaultClassProvider();
    }
    
    /**
     * Get the VaultProvider for this class.
     * @return the VaultProvider for this class.
     */
    public static VaultProvider getVaultClassProvider() {
        return vaultProvider;
    }

    /**
     * @return the name of the module specified by the file wrapped by this vault.
     */
    public ModuleName getModuleName() {
        return moduleName;
    }

    /**
     * {@inheritDoc}
     */
    public String getLocationString() {
        return locationString;
    }
}