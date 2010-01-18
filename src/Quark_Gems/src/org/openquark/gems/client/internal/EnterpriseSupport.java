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
 * EnterpriseSupport.java
 * Creation date: Oct 5, 2006.
 * By: Joseph Wong
 */
package org.openquark.gems.client.internal;

import java.awt.Frame;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.Vault;


/**
 * This interface encapsulates the functionalities required for connecting to Enterprise.
 *
 * @author Joseph Wong
 * @author Edward Lam
 */
public interface EnterpriseSupport {
    
    /**
     * @return true if the implementation actually provides Enterprise support; false if the
     * implementation is a stub.
     */
    public boolean isEnterpriseSupported();

    /**
     * Registers the vault provider for the EnterpriseVault with the registry of the given workspace.
     * @param workspace the workspace whose registry is to be used for registering the vault provider. 
     */
    public void registerEnterpriseVaultProvider(CALWorkspace workspace);
    
    /**
     * Registers the vault authenticator for the EnterpriseVault with the registry of the given workspace.
     * @param workspace the workspace whose registry is to be used for registering the vault authenticator. 
     * @param ownerFrame the owner frame for any dialogs that may be displayed.
     */
    public void registerEnterpriseVaultAuthenticator(CALWorkspace workspace, Frame ownerFrame);
    
    /**
     * @return the Enterprise vault for the connection manager. Can be null if enterprise support is not available.
     */
    public Vault getEnterpriseVault();

    /**
     * Attempts to ensure that the user is connected to Enterprise.
     * If the user is not already logged on, display a dialog and log on if possible.
     * @param ownerFrame the owner frame in which to display a log on dialog if necessary.
     * @throws Exception if there was an error instantiating the session manager, logging on, or initializing the connection manager.
     */
    public void ensureConnected(Frame ownerFrame) throws Exception;
    
    /**
     * Ensures that any Enterprise session is logged off.
     */
    public void ensureLoggedOff();

    /**
     * @return whether the connection manager is currently logged on to Enterprise and initialized as such.
     */
    public boolean isConnected();
    
    /**
     * Gets the latest revision of the module in the given vault which is identical to the module in the workspace.
     * @param maybeEnterpriseVault maybe an EnterpriseVault, or maybe some other kind of Vault.
     * @param moduleName the name of the module.
     * @param workspace the workspace for which the resources for the module should be compared.
     * @param status the tracking status object.
     * @return the latest revision of the module in the vault whose resources are identical to the module in the workspace.
     *   0 if connection to Enterprise is not supported.
     *   -1 if there is no such revision.
     */
    public int getLatestIdenticalRevisionFromMaybeEnterpriseVault(Vault maybeEnterpriseVault, ModuleName moduleName, CALWorkspace workspace, Status status);

}
