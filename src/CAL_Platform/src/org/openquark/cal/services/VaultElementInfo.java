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
 * VaultElementInfo.java
 * Creation date: Mar 7, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.Iterator;
import java.util.List;

/**
 * A simple container class for the vault-related info about a stored vault element.
 * Note that the granularity of storage with respect to a vault is a stored vault element.
 * @author Edward Lam
 */
public abstract class VaultElementInfo {
    
    /**
     * A VaultElementInfo.Basic instance represents the vault-related info of an element stored in a
     * "top-level" vault, e.g. the standard vault or the enterprise vault.
     *
     * @author Edward Lam
     * @author Joseph Wong
     */
    public static class Basic extends VaultElementInfo {

        /** A string which describes the type of the vault. e.g. "StandardVault" */
        private final String vaultDescriptor;
        /** The name of the element. */
        private final String elementName;
        /** A string representation of the location of this element. The format is vault-dependent. Can be null. */
        private final String locationString;
        /** The element's revision number with respect to the vault in which it is stored. */
        private final int revisionNum;
        
        /**
         * Constructor for a VaultElementInfo.Basic.
         * @param vaultDescriptor a string which describes the type of the vault. e.g. "StandardVault"
         * @param elementName the name of the element.
         * @param locationString a string representation of the location of this element. The format is vault-dependent. Can be null.
         * @param revisionNum the element's revision number with respect to the vault in which it is stored.
         */
        public Basic(String vaultDescriptor, String elementName, String locationString, int revisionNum) {
            
            if (vaultDescriptor == null || elementName == null) {
                throw new NullPointerException();
            }
            
            this.vaultDescriptor = vaultDescriptor;
            this.elementName = elementName;
            this.locationString = locationString;
            this.revisionNum = revisionNum;
        }

        /**
         * @return a String which describes the type of the vault.
         * eg. "StandardVault" for the standard vault.
         */
        @Override
        public String getVaultDescriptor() {
            return vaultDescriptor;
        }

        /**
         * @return the name of the element.
         */
        @Override
        public String getElementName() {
            return elementName;
        }

        /**
         * Get the location of this element.
         *   The format of this string is vault-dependent.
         * @return a String specifying the location of this module.
         *   This string can be used by a vault's VaultProvider to indicate a module within a specific vault.
         */
        @Override
        public String getLocationString() {
            return locationString;
        }

        /**
         * Get the revision.
         * @return the element's revision, with respect to the vault in which it is stored.
         */
        @Override
        public int getRevision() {
            return revisionNum;
        }

        /**
         * @param otherVaultInfo a vault info object.
         * @return whether this info's vault is the same as that of another vault info.
         */
        @Override
        public boolean sameVault(VaultElementInfo otherVaultInfo) {
            if (otherVaultInfo instanceof Basic) {
                return sameVault((Basic)otherVaultInfo);
            } else {
                return false;
            }
        }

        /**
         * @param otherVaultInfo a vault info object.
         * @return whether this info's vault is the same as that of another vault info.
         */
        public boolean sameVault(VaultElementInfo.Basic otherVaultInfo) {
            return vaultDescriptor.equals(otherVaultInfo.vaultDescriptor) && sameLocationString(otherVaultInfo);
        }
        
        /**
         * @param otherVaultInfo a vault info object.
         * @return whether this info's location string is the same as that of another vault info.
         */
        private boolean sameLocationString(VaultElementInfo.Basic otherVaultInfo) {
            if (locationString == null) {
                return otherVaultInfo.locationString == null;
            } else {
                return locationString.equals(otherVaultInfo.locationString);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Basic) {
                Basic otherVaultInfo = (Basic)obj;
                return sameVault(otherVaultInfo) && revisionNum == otherVaultInfo.revisionNum;
            }
            
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return vaultDescriptor.hashCode() * 37 +
            (locationString == null ? 0 : locationString.hashCode() * 17) +
            revisionNum;
        }

        /**
         * Factory method for a VaultElementInfo.Basic.
         * Translate a list of tokens into a locator for a vault element.
         * 
         * @param elementTokens the tokens which represent the location of the element.
         * @param status the tracking status object.
         * @return a locator for the corresponding vault element.
         */
        public static VaultElementInfo.Basic makeFromDeclarationString(List<String> elementTokens, Status status) {
            // Check for null list.
            if (elementTokens == null) {
                return null;
            }
            // Check for correct number of tokens.
            int nElementTokens = elementTokens.size();
            if (nElementTokens < 2 || nElementTokens > 4) {
                return null;
            }
            
            // Create an iterator over the token list.
            Iterator<String> elementTokensIterator = elementTokens.iterator();
            
            // Get the vault descriptor and the element name.
            String vaultDescriptor = elementTokensIterator.next();
            String elementName = elementTokensIterator.next();
            
            // Get the location string if any.
            String locationString = null;
            if (elementTokensIterator.hasNext()) {
                locationString = elementTokensIterator.next();
            }
            
            // Get the revision num if any.
            int revisionNum = -1;
            if (elementTokensIterator.hasNext()) {
                String revisionString = elementTokensIterator.next();
                try {
                    revisionNum = Integer.parseInt(revisionString);
                    
                } catch (NumberFormatException nfe) {
                    String errorString = "\"" + revisionString + " \" is not a valid revision number.";
                    status.add(new Status(Status.Severity.WARNING, errorString));
                    return null;
                }
            }
            
            return new VaultElementInfo.Basic(vaultDescriptor, elementName, locationString, revisionNum);
        }
        
        /**
         * @return the equivalent workspace declaration line for this vault element.
         */
        public String toDeclarationString() {
            String decl = vaultDescriptor + " " + elementName;
            if (locationString != null) {
                decl += " " + locationString + " " + revisionNum;
            }
            
            return decl;
        }
    }
    
    /**
     * A VaultElementInfo.Nested instance represents the vault-related info of an element stored in a
     * "nested" vault, e.g. a Car vault. The Car vault is a nested vault because it itself is an element
     * within an outer vault.
     *
     * @author Joseph Wong
     */
    public static class Nested extends VaultElementInfo {

        /** A string which describes the type of the vault. e.g. "CarVault" */
        private final String vaultDescriptor;
        /** The name of the element. */
        private final String elementName;
        /** The VaultElementInfo of the nested vault as an element of the outer vault which contains it. */
        private final VaultElementInfo.Basic outerVaultElementInfo;
        /** The element's revision number with respect to the vault in which it is stored. */
        private final int revisionNum;

        /**
         * Constructor for a VaultElementInfo.Nested.
         * @param vaultDescriptor a string which describes the type of the vault. e.g. "StandardVault"
         * @param elementName the name of the element.
         * @param outerVaultElementInfo the VaultElementInfo of the nested vault as an element of the outer vault which contains it.
         * @param revisionNum the element's revision number with respect to the vault in which it is stored.
         */
        public Nested(String vaultDescriptor, String elementName, VaultElementInfo.Basic outerVaultElementInfo, int revisionNum) {
            
            if (vaultDescriptor == null || elementName == null || outerVaultElementInfo == null) {
                throw new NullPointerException();
            }
            
            this.vaultDescriptor = vaultDescriptor;
            this.elementName = elementName;
            this.outerVaultElementInfo = outerVaultElementInfo;
            this.revisionNum = revisionNum;
        }
        
        /**
         * @return the VaultElementInfo of the nested vault as an element of the outer vault which contains it.
         */
        public VaultElementInfo.Basic getOuterVaultElementInfo() {
            return outerVaultElementInfo;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getVaultDescriptor() {
            return vaultDescriptor;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getElementName() {
            return elementName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getLocationString() {
            return outerVaultElementInfo.toString();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getRevision() {
            return revisionNum;
        }

        /**
         * @param otherVaultInfo a vault info object.
         * @return whether this info's vault is the same as that of another vault info.
         */
        @Override
        public boolean sameVault(VaultElementInfo otherVaultInfo) {
            if (otherVaultInfo instanceof Nested) {
                return sameVault((Nested)otherVaultInfo);
            } else {
                return false;
            }
        }

        /**
         * @param otherVaultInfo a vault info object.
         * @return whether this info's vault is the same as that of another vault info.
         */
        public boolean sameVault(VaultElementInfo.Nested otherVaultInfo) {
            return vaultDescriptor.equals(otherVaultInfo.vaultDescriptor) && outerVaultElementInfo.equals(otherVaultInfo.outerVaultElementInfo);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Nested) {
                Nested otherVaultInfo = (Nested)obj;
                return sameVault(otherVaultInfo) && revisionNum == otherVaultInfo.revisionNum;
            }
            
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return vaultDescriptor.hashCode() * 37 +
            outerVaultElementInfo.hashCode() * 17 +
            revisionNum;
        }
    }
    
    /**
     * Private constructor to be called by subclasses.
     */
    private VaultElementInfo() {}
    
    /**
     * Factory method for constructing a VaultElementInfo.Basic instance.
     * @param vaultDescriptor a string which describes the type of the vault. e.g. "StandardVault"
     * @param elementName the name of the element.
     * @param locationString a string representation of the location of this element. The format is vault-dependent. Can be null.
     * @param revisionNum the element's revision number with respect to the vault in which it is stored.
     * @return the VaultElementInfo.Basic instance.
     */
    public static VaultElementInfo.Basic makeBasic(String vaultDescriptor, String elementName, String locationString, int revisionNum) {
        return new VaultElementInfo.Basic(vaultDescriptor, elementName, locationString, revisionNum);
    }
    
    /**
     * Factory method for constructing a VaultElementInfo.Nested instance.
     * @param vaultDescriptor a string which describes the type of the vault. e.g. "StandardVault"
     * @param elementName the name of the element.
     * @param outerVaultElementInfo the VaultElementInfo of the nested vault as an element of the outer vault which contains it.
     * @param revisionNum the element's revision number with respect to the vault in which it is stored.
     * @return the VaultElementInfo.Nested instance.
     */
    public static VaultElementInfo.Nested makeNested(String vaultDescriptor, String elementName, VaultElementInfo.Basic outerVaultElementInfo, int revisionNum) {
        return new VaultElementInfo.Nested(vaultDescriptor, elementName, outerVaultElementInfo, revisionNum);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("VaultElementInfo: [");
        sb.append(getVaultDescriptor() + ", ");
        sb.append(getElementName() + ", ");
        sb.append(getLocationString() + ", ");
        sb.append(getRevision());
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * @return a String which describes the type of the vault.
     * eg. "StandardVault" for the standard vault.
     */
    public abstract String getVaultDescriptor();

    /**
     * @return the name of the element.
     */
    public abstract String getElementName();

    /**
     * Get the location of this module.
     *   The format of this string is vault-dependent.
     * @return a String specifying the location of this module.
     *   This string can be used by a vault's VaultProvider to indicate a module within a specific vault.
     */
    public abstract String getLocationString();

    /**
     * Get the revision.
     * @return the element's revision, with respect to the vault in which it is stored.
     */
    public abstract int getRevision();

    /**
     * @param otherVaultInfo a vault info object.
     * @return whether this info's vault is the same as that of another vault info.
     */
    public abstract boolean sameVault(VaultElementInfo otherVaultInfo);
    
//        String getVersion();
//        String getLabel();
//        long getLastModifiedTime();
    
}