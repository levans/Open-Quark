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
 * DefaultWorkspaceDeclarationProvider.java
 * Creation date: Dec 21, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A Workspace declaration provider which chooses the source of its declaration based on provided system properties and defaults.
 *   The workspace declaration will be referenced using a given system property, if set.
 *   Otherwise, if set, the declaration will be referenced using the system property for the workspace spec.
 *   Otherwise the declaration will have used the default file name.
 * 
 * @author Edward Lam
 */
public class DefaultWorkspaceDeclarationProvider implements WorkspaceDeclaration.StreamProvider {
    
    // The following two members point to the location of the workspace.
    //   If the first is null, the second is used.
    private final File workspaceDeclarationFile;
    private final VaultElementInfo vaultElementInfo;
    
    
    /**
     * Constructor for a DefaultWorkspaceDeclarationProvider.
     * Only one of the arguments may be non-null.
     * 
     * @param workspaceDeclarationFile the file from which the workspace declaration will be read.
     * @param vaultElementInfo the vault element info for the workspace declaration.
     */
    private DefaultWorkspaceDeclarationProvider(File workspaceDeclarationFile, VaultElementInfo vaultElementInfo) {
        if (workspaceDeclarationFile != null && vaultElementInfo != null) {
            throw new IllegalArgumentException("Both workspaceDeclarationFile and storedWorkspaceDeclarationDescription cannot be non-null.");
        }
        
        this.workspaceDeclarationFile = workspaceDeclarationFile;
        this.vaultElementInfo = vaultElementInfo;
    }
    
    /**
     * Factory method for a DefaultWorkspaceDeclarationProvider.
     * @param vaultElementInfo the vault element info for the workspace declaration.
     */
    public static DefaultWorkspaceDeclarationProvider getDefaultWorkspaceDeclarationProvider(VaultElementInfo vaultElementInfo) {
        return new DefaultWorkspaceDeclarationProvider(null, vaultElementInfo);
    }
    
    /**
     * Factory method for a DefaultWorkspaceDeclarationProvider.
     * @param workspaceDeclarationFile the file from which the workspace declaration will be read.
     */
    public static DefaultWorkspaceDeclarationProvider getDefaultWorkspaceDeclarationProvider(File workspaceDeclarationFile) {
        
        if (workspaceDeclarationFile != null && workspaceDeclarationFile.exists()) {
            return new DefaultWorkspaceDeclarationProvider(workspaceDeclarationFile, null);

        } else {
            // Couldn't find anything suitable..
            return new DefaultWorkspaceDeclarationProvider(null, null);
        }
    }
    
    /**
     * Factory method for a DefaultWorkspaceDeclarationProvider.
     * @param workspaceFileString the name of the workspace file.
     *   This should exist in the base resource folder for workspace declarations.
     */
    public static DefaultWorkspaceDeclarationProvider getDefaultWorkspaceDeclarationProvider(String workspaceFileString) {
        String vaultDescriptor = StandardVault.getVaultClassProvider().getVaultDescriptor();
        VaultElementInfo vaultElementInfo = VaultElementInfo.makeBasic(vaultDescriptor, workspaceFileString, null, -1);

        return getDefaultWorkspaceDeclarationProvider(vaultElementInfo);
    }
    
    /**
     * Factory method for a DefaultWorkspaceDeclarationProvider.
     *   The returned file will be instantiated using the given system property, if set.
     *   Otherwise, if set, the returned file will be instantiated using the system property for the workspace spec.
     *   Otherwise the file will be the provided default.
     * 
     * @param workspaceDeclarationPropertyName the system property which may be set to the file (file system pathname)
     *   containing a workspace declaration.  If null, the default file will be used.
     * @param defaultWorkspaceFile the default file.
     */
    public static DefaultWorkspaceDeclarationProvider
    getDefaultWorkspaceDeclarationProvider(String workspaceDeclarationPropertyName, String defaultWorkspaceFile) {
        if (workspaceDeclarationPropertyName == null) {
            return getDefaultWorkspaceDeclarationProvider(defaultWorkspaceFile);
        }
        
        // Check for a workspace file defined by a system property.
        String workspaceSystemProperty = System.getProperty(workspaceDeclarationPropertyName);
        if (workspaceSystemProperty != null) {
            File workspaceDeclarationFile = new File(workspaceSystemProperty);
            return new DefaultWorkspaceDeclarationProvider(workspaceDeclarationFile, null);
        }
        
        // Check for a workspace spec from a vault defined by the system property.
        workspaceSystemProperty = System.getProperty(WorkspaceConfiguration.WORKSPACE_SPEC_PROP);
        if (workspaceSystemProperty != null && workspaceSystemProperty.length() > 0) {
            // Break the line into tokens..
            List<String> tokens = new ArrayList<String>();
            for (StringTokenizer st = new StringTokenizer(workspaceSystemProperty); st.hasMoreTokens(); ) {
                tokens.add(st.nextToken());
            }
            
            // Get the vault element info from the property tokens..
            // TODOEL: should we do something with the status messages?
            VaultElementInfo vaultElementInfo = VaultElementInfo.Basic.makeFromDeclarationString(tokens, new Status("Status"));
            
            if (vaultElementInfo != null) {
                return new DefaultWorkspaceDeclarationProvider(null, vaultElementInfo);
            }
        }
        
        // Use the default workspace declaration file.
        return getDefaultWorkspaceDeclarationProvider(defaultWorkspaceFile);
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        if (workspaceDeclarationFile != null) {
            return workspaceDeclarationFile.getName();
            
        } else if (vaultElementInfo != null) {
            return vaultElementInfo.getElementName();
            
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getLocation() {
        if (workspaceDeclarationFile != null) {
            return workspaceDeclarationFile.getAbsolutePath();
            
        } else if (vaultElementInfo != null) {
            return vaultElementInfo.toString();
            
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
        
        if (workspaceDeclarationFile != null) {
            // It's a file..
            try {
                return new FileInputStream(workspaceDeclarationFile);
                
            } catch (FileNotFoundException e) {
                String message = "File \"" + workspaceDeclarationFile + "\" could not be accessed.";
                status.add(new Status(Status.Severity.ERROR, message, e));
                return null;
            }
        
        } else if (vaultElementInfo != null) {
            // It's a stored vault element.
            StoredVaultElement.WorkspaceDeclaration workspaceDeclaration =
                vaultRegistry.getStoredWorkspaceDeclaration(vaultElementInfo, status);
            return workspaceDeclaration == null ? null : workspaceDeclaration.getInputStream();
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDebugInfo(VaultRegistry vaultRegistry) {
        if (workspaceDeclarationFile != null) {
            return "from file: " + workspaceDeclarationFile.getAbsolutePath();
            
        } else if (vaultElementInfo != null) {
            Vault vault = vaultRegistry.getVault(vaultElementInfo.getVaultDescriptor(), vaultElementInfo.getLocationString());
            
            if (vault != null) {
                return vault.getWorkspaceDeclarationDebugInfo(vaultElementInfo.getElementName(), vaultElementInfo.getRevision());
            } else {
                return "from vault element: " + vaultElementInfo;
            }
            
        } else {
            return null;
        }
    }
}