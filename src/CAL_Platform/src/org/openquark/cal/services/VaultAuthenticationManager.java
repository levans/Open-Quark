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
 * VaultAuthenticationManager.java
 * Creation date: Nov 24, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.HashMap;
import java.util.Map;


/**
 * A VaultAuthenticationManager manages the mappings from vault type to the VaultAuthenticator registered for those types.
 * @author Edward Lam
 */
public class VaultAuthenticationManager {
    
    /** map from vault descriptor to the VaultAuthenticator registered for vaults with that descriptor. */
    private final Map<String, VaultAuthenticator> descriptorToAuthenticatorMap = new HashMap<String, VaultAuthenticator>();
    
    /**
     * Register a VaultAuthenticator with this manager.
     * @param vaultAuthenticator the authenticator to register.
     */
    public void registerAuthenticator(VaultAuthenticator vaultAuthenticator) {
        String vaultDescriptor = vaultAuthenticator.getVaultDescriptor();
        descriptorToAuthenticatorMap.put(vaultDescriptor, vaultAuthenticator);
    }
    
    /**
     * @param vaultDescriptor a vault descriptor.
     * @return the VaultAuthenticator registered for vaults with that descriptor type, or null if there is none.
     */
    private VaultAuthenticator getAuthenticator(String vaultDescriptor) {
        return descriptorToAuthenticatorMap.get(vaultDescriptor);
    }
    
    /**
     * Perform a vault-specific authentication.
     * @param vaultDescriptor the descriptor for the vault for which to perform authentication.
     * @param locationString the locationString for the vault for which the authentication will take place.
     * @return the result of the authentication.
     */
    public Object authenticate(String vaultDescriptor, String locationString) {
        VaultAuthenticator authenticator = getAuthenticator(vaultDescriptor);
        if (authenticator == null) {
            return null;
        }
        return authenticator.authenticate(locationString);
    }
}
