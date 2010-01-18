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
 * SimpleCarFileVault.java
 * Creation date: Feb 3, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.metadata.MetadataStore;
import org.openquark.util.EmptyIterator;


/**
 * A Vault which wraps a CAL Archive (Car) file specified by a URL.
 *
 * @author Joseph Wong
 */
public class SimpleCarFileVault extends StoreBasedVault {
    
    /** The path of the Car file's location. */
    private final String locationString;
    
    /** A CAL source store which doesn't contain any CAL source files. */
    private static final CALSourceStore sourceStore = new EmptyCALSourceStore();
    
    /** A metadata store which doesn't contain any metadata. */
    private static final MetadataStore metadataStore = new EmptyMetadataStore();
    
    /** A design store which doesn't contain any designs. */
    private static final GemDesignStore gemDesignStore = new EmptyGemDesignStore();
    
    /** A workspace declaration store which doesn't contain any workspace declarations. */
    private static final WorkspaceDeclarationStore workspaceDeclarationStore = new EmptyWorkspaceDeclarationStore();
    
    /** A user resource store which doesn't contain any user resources. */
    private static final UserResourceStore userResourceStore = new EmptyUserResourceStore();
    
    /** The vault provider for this class. */
    private static final VaultProvider vaultProvider = new VaultProvider() {
        
        /**
         * {@inheritDoc}
         */
        public String getVaultDescriptor() {
            return "SimpleCarFile";
        }

        /**
         * {@inheritDoc}
         */
        public Vault getVault(String locationString, VaultAuthenticationManager authenticationManager) {
            if (locationString == null) {
                return null;
            }
            
            File carFile = WorkspaceLoader.fileFromPath(locationString);
            if (carFile == null) {
                return null;
            }
            
            CarStore carStore = new SingleFileCarStore(carFile.getName(), carFile);

            return new SimpleCarFileVault(locationString, carStore);
        }
    };
    
    /**
     * A CarStore based on a single file.
     *
     * @author Joseph Wong
     */
    private static class SingleFileCarStore implements CarStore {

        /** The name of the Car file. */
        private final String carName;
        
        /** The file itself. */
        private final File carFile;

        /** The factory used for creating the Car as a workspace resource. */
        private final Car.Factory carFactory;
        
        /**
         * Constructs a SingleFileCarStore.
         * @param carName the name of the Car file.
         * @param carFile the file itself.
         */
        private SingleFileCarStore(String carName, File carFile) {
            if (carName == null || carFile == null) {
                throw new NullPointerException();
            }
            
            this.carName = carName;
            this.carFile = carFile;
            this.carFactory = new Car.Factory();
        }

        /**
         * {@inheritDoc}
         */
        public Set<ResourceName> getCarNames() {
            return Collections.singleton(new ResourceName(CarFeatureName.getCarFeatureName(carName)));
        }

        /**
         * {@inheritDoc}
         */
        public Car getCar(String carName) {
            if (this.carName.equals(carName)) {
                return carFactory.getCar(carName, carFile);
            } else {
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        public String getResourceType() {
            return WorkspaceResource.CAR_RESOURCE_TYPE;
        }

        /**
         * {@inheritDoc}
         */
        public Iterator<WorkspaceResource> getResourceIterator() {
            Car car = getCar(carName);
            if (car == null) {
                return EmptyIterator.emptyIterator();
            } else {
                return Collections.<WorkspaceResource>singleton(car).iterator();
            }
        }

        /**
         * {@inheritDoc}
         */
        public InputStream getInputStream(ResourceName resourceName) {
            if (hasFeature(resourceName)) {
                try {
                    return new FileInputStream(carFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public OutputStream getOutputStream(ResourceName resourceName, Status status) {
            if (hasFeature(resourceName)) {
                try {
                    return new FileOutputStream(carFile);
                } catch (FileNotFoundException e) {
                    status.add(new Status(Status.Severity.ERROR, "Car file " + carFile + " not writeable.", e));
                }
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public String getDebugInfo(ResourceName resourceName) {
            if (hasFeature(resourceName)) {
                return "from SimpleCarFile: " + carFile.getAbsolutePath();
            }
            
            return null;
        }

        /**
         * {@inheritDoc}
         */
        public long getTimeStamp(ResourceName resourceName) {
            if (hasFeature(resourceName)) {
                return carFile.lastModified();
            } else {
                return 0L;
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean hasFeature(ResourceName resourceName) {
            return resourceName.getFeatureName().equals(CarFeatureName.getCarFeatureName(carName));
        }

        /**
         * {@inheritDoc}
         */
        public boolean isWriteable() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isWriteable(ResourceName resourceName) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isRemovable(ResourceName resourceName) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public void removeResource(ResourceName resourceName, Status removeStatus) {
            removeStatus.add(new Status(Status.Severity.ERROR, "Cannot remove resources in the SimpleCarFileVault."));
        }

        /**
         * {@inheritDoc}
         */
        public void removeAllResources(Status removeStatus) {
            removeStatus.add(new Status(Status.Severity.ERROR, "Cannot remove resources in the SimpleCarFileVault."));
        }

        /**
         * {@inheritDoc}
         */
        public boolean renameResource(ResourceName oldResourceName, ResourceName newResourceName, ResourceStore newResourceStore, Status renameStatus) {
            renameStatus.add(new Status(Status.Severity.ERROR, "Cannot rename resources in the SimpleCarFileVault."));
            return false;
        }
        
    }

    /**
     * An empty module resource store for CAL source.
     * @author Edward Lam
     */
    private static class EmptyCALSourceStore extends EmptyResourceStore.Module implements CALSourceStore {

        /**
         * Constructor for an EmptyCALSourceStore.
         */
        EmptyCALSourceStore() {
            super(ModuleSourceDefinition.RESOURCE_TYPE);
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
    
    /**
     * Private constructor. Instances should be created via the vault provider.
     * @param locationString the path of the Car file's location.
     * @param carStore the Car store responsible for encapsulating the Car file.
     */
    private SimpleCarFileVault(String locationString, CarStore carStore) {
        super(sourceStore, metadataStore, gemDesignStore, workspaceDeclarationStore, carStore, userResourceStore);
        
        if (locationString == null) {
            throw new NullPointerException();
        }
        
        this.locationString = locationString;
    }

    /**
     * {@inheritDoc}
     */
    public VaultProvider getVaultProvider() {
        return vaultProvider;
    }

    /**
     * {@inheritDoc}
     */
    public String getLocationString() {
        return locationString;
    }
    
    /**
     * Get the VaultProvider for this class.
     * @return the VaultProvider for this class.
     */
    public static VaultProvider getVaultClassProvider() {
        return vaultProvider;
    }

    /**
     * Get an instance of this class by path.
     * @param filePath the path to the file.
     * @return a Vault which wraps the Car specified by the given file.
     */
    public static SimpleCarFileVault getSimpleCarFileVault(String filePath) {
        return (SimpleCarFileVault)getVaultClassProvider().getVault(filePath, null);
    }

    /**
     * Get an instance of this class by file.
     * @param file the file.
     * @return a Vault which wraps the Car specified by the given file.
     */
    public static SimpleCarFileVault getSimpleCarFileVault(File file) {
        String pathString = file.toURI().toASCIIString();
        return getSimpleCarFileVault(pathString);
    }
}
