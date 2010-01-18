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
 * CarVault.java
 * Creation date: Jan 11, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import org.openquark.cal.metadata.MetadataStore;

/**
 * A CarVault is a read-only vault backed by a CAL Archive (Car) containing the
 * resources for one or more modules.
 * 
 * The CarVault is a "nested vault", i.e. a Car is at the same time a vault (a
 * container of resources) and an element of an outer vault (as a Car file can
 * be treated as a resource).
 * 
 * @author Joseph Wong
 */
public class CarVault extends StoreBasedVault {
    /**
     * The descriptor string for the CarVault.
     */
    private static final String CAR_VAULT_DESCRIPTOR = "CarVault";
    /**
     * The outer vault which contains this Car as an element.
     */
    private final Vault outerVault;
    /**
     * The VaultElementInfo of this Car vault as an element of the outer vault which contains it.
     */
    private final VaultElementInfo.Basic outerVaultElementInfo;
    /**
     * The FeatureName representation of this Car's name.
     */
    private final CarFeatureName carFeatureName;
    /**
     * The revision number of the Car with respect to the outer vault in which it is stored.
     */
    private final int revisionNumber;
    /**
     * The Car accessor for reading the resources inside the Car.
     */
    private final org.openquark.cal.services.Car.Accessor carAccessor;

    /** The vault provider for this instance. */
    private final VaultProvider vaultProvider = new VaultProvider() {

        public String getVaultDescriptor() {
            return CAR_VAULT_DESCRIPTOR;
        }

        public Vault getVault(String locationString, VaultAuthenticationManager authenticationManager) {
            if (locationString == null) {
                return null;
            }
            
            Status status = new Status("getting a Car vault");
            
            // Here, the locationString is treated as the name of the Car.
            return make(outerVault, outerVaultElementInfo, locationString, revisionNumber, status);
        }
    };
    
    /**
     * Factory method for constructing an instance of CarVault.
     * 
     * @param outerVault the outer vault which contains this Car as an element.
     * @param outerVaultElementInfo the VaultElementInfo of this Car vault as an element of the outer vault which contains it.
     * @param carName the name of the Car.
     * @param revisionNumber the revision number of the Car with respect to the outer vault in which it is stored.
     * @param status the tracking status object.
     * @return a CarVault instance, or null if there are problems with getting an accessor for the Car.
     */
    public static CarVault make(Vault outerVault, VaultElementInfo.Basic outerVaultElementInfo, String carName, int revisionNumber, Status status) {
        
        org.openquark.cal.services.Car.Accessor carAccessor = makeCarAccessor(outerVault, carName, revisionNumber, status);
        
        if (carAccessor == null) {
            return null;
        } else {
            return new CarVault(outerVault, outerVaultElementInfo, carName, revisionNumber, carAccessor);
        }
    }
    
    /**
     * Private constructor for CarVault.
     * 
     * @param outerVault the outer vault which contains this Car as an element.
     * @param outerVaultElementInfo the VaultElementInfo of this Car vault as an element of the outer vault which contains it.
     * @param carName the name of the Car.
     * @param revisionNumber the revision number of the Car with respect to the outer vault in which it is stored.
     * @param carAccessor the Car accessor for reading the resources inside the Car.
     */
    private CarVault(Vault outerVault, VaultElementInfo.Basic outerVaultElementInfo, String carName, int revisionNumber, org.openquark.cal.services.Car.Accessor carAccessor) {
        
        super((CALSourceStore)carAccessor.getSourceManager().getResourceStore(),
            (MetadataStore)carAccessor.getMetadataManager().getResourceStore(),
            (GemDesignStore)carAccessor.getDesignManager().getResourceStore(),
            (WorkspaceDeclarationStore)carAccessor.getWorkspaceDeclarationManager().getResourceStore(),
            new EmptyResourceStore.Car(WorkspaceResource.CAR_RESOURCE_TYPE), // Cars cannot be nested inside another Car.
            (UserResourceStore)carAccessor.getUserResourceManager().getResourceStore());
        
        if (outerVault == null || outerVaultElementInfo == null || carAccessor == null) {
            throw new NullPointerException("outerVault, outerVaultElementInfo and carAccessor cannot be null.");
        }
        this.outerVault = outerVault;
        this.outerVaultElementInfo = outerVaultElementInfo;
        this.revisionNumber = revisionNumber;
        this.carFeatureName = CarFeatureName.getCarFeatureName(carName);
        this.carAccessor = carAccessor;
    }
    
    ////====================
    /// Vault implementation
    //

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
        return carFeatureName.getName();
    }
    
    ////=========================
    /// CarVault specific methods
    //
    
    /**
     * @return the FeatureName representation of this Car's name.
     */
    public CarFeatureName getCarFeatureName() {
        return carFeatureName;
    }
    
    /**
     * @return the revision number of the Car with respect to the outer vault in which it is stored.
     */
    public int getCarRevisionNumber() {
        return revisionNumber;
    }
    
    /**
     * @return the outer vault which contains this Car as an element.
     */
    public Vault getOuterVault() {
        return outerVault;
    }
    
    /**
     * @return the descriptor string for the CarVault.
     */
    public static String getVaultClassDescriptor() {
        return CAR_VAULT_DESCRIPTOR;
    }
    
    /**
     * @return the Car accessor for reading the resources inside the Car.
     */
    public org.openquark.cal.services.Car.Accessor getCarAccessor() {
        return carAccessor;
    }
    
    /**
     * Gets a Car accessor for the specified Car.
     * @param outerVault the outer vault which contains the Car as an element.
     * @param carName the name of the Car.
     * @param revisionNumber the revision number of the Car with respect to the outer vault in which it is stored.
     * @param status the tracking status object.
     * @return an accessor for the Car, or null if there is a problem reading the Car.
     */
    private static org.openquark.cal.services.Car.Accessor makeCarAccessor(Vault outerVault, String carName, int revisionNumber, Status status) {
        
        org.openquark.cal.services.Car car = outerVault.getCarAsResource(carName, revisionNumber, status);
        if (car == null) {
            return null;
        }
        
        return car.getAccessor(status);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected VaultElementInfo getVaultInfo(String elementName, int revisionNumber) {
        return VaultElementInfo.makeNested(CAR_VAULT_DESCRIPTOR, elementName, outerVaultElementInfo, revisionNumber);
    }
}
