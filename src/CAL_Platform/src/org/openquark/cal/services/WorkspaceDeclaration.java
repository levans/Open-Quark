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
 * WorkspaceDeclaration.java
 * Creation date: Dec 8, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;


/**
 * A simple class which contains the abstract representation of a workspace declaration.
 * @author Edward Lam
 */
public class WorkspaceDeclaration {
    
    /** The bulk of the text to write as a comment for saved workspace declarations. */
    private static final String[] declarationHeader = {
            "Enter one line for each file to be loaded into the workspace.",
            "Modules are specified as: VaultType ModuleName [LocationString [RevisionNumber]].",
            "Workspaces specified as:  import VaultType WorkspaceName [LocationString [RevisionNumber]].",
            "   where LocationString is a vault-specific identifier",
            "",
            " Module precedence is textual order.",
            " eg. suppose this declaration contains the line \"VaultA ModuleName\", and an imported declaration contains the line \"VaultB ModuleName\"",
            "     If \"VaultA ModuleName\" appears before the import, the VaultA module is used.  If it appears after the import, the VaultB module is used.",
            "",
            "Java-style comments (slash-slash and slash-star) are treated as such.",
            "Use single- and double-quotes to enclose strings with whitespace and comment tokens.",
            "The usual escape characters (such as \"\\n\") are recognized and converted within strings."
    };
    
    /** The module declarations comprising this workspace declaration. */
    private final VaultElementInfo[] vaultModuleInfo;
    
    
    /**
     * Constructor for a WorkspaceDeclaration.
     * @param moduleDeclarations the module declarations comprising this workspace declaration.
     */
    public WorkspaceDeclaration(VaultElementInfo[] moduleDeclarations) {
        this.vaultModuleInfo = moduleDeclarations.clone();
    }
    
    /**
     * @return the module declarations comprising this workspace declaration.
     */
    public VaultElementInfo[] getModuleDeclarations() {
        return vaultModuleInfo.clone();
    }
    
    /**
     * Check that all modules specified in this workspace declaration are from the given vault.
     * @param vaultDescriptor the descriptor against which to check.
     * @param locationString the location string against which to check.
     * @return true if all module declarations within this workspace declaration have the same vault
     *   descriptor and location as those supplied.  False otherwise.
     */
    public boolean checkVault(String vaultDescriptor, String locationString) {
        // Iterate over the module declarations.
        for (final VaultElementInfo moduleDeclaration : vaultModuleInfo) {
            // Check the vault descriptor.
            if (!vaultDescriptor.equals(moduleDeclaration.getVaultDescriptor())) {
                return false;
            }
            
            // Check the location.
            String moduleDeclarationLocation = moduleDeclaration.getLocationString();
            
            if (locationString == null && moduleDeclarationLocation != null) {
                return false;
            }
            if (locationString != null && !locationString.equals(moduleDeclarationLocation)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check that all modules specified in this workspace declaration have positive non-zero revision numbers.
     * @return true if all module declarations within this workspace declaration have positive non-zero revision numbers.
     *   False otherwise.
     */
    public boolean checkAbsoluteRevisions() {
        // Iterate over the module declarations.
        for (final VaultElementInfo moduleDeclaration : vaultModuleInfo) {
            // Check for negative or zero revision number
            if (moduleDeclaration.getRevision() <= 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Save this workspace declaration.
     * @param declarationName the name of the declaration.
     * @param outputStream the output stream to which this workspace declaration should be written.
     * @param status the tracking status object.
     */
    public void saveDeclaration(String declarationName, OutputStream outputStream, Status status) {
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
        
        // Write the comment start.
        out.println("/**");
        
        // Add the workspace name.
        out.println(" * " + declarationName + "");
        out.println(" *");
        
        // Add the declaration header.
        for (final String element : declarationHeader) {
            out.println(" * " + element + "");
        }
        
        // Add an empty line, and the current date / time.
        out.println(" *");
        out.println(" * Created: " + DateFormat.getDateTimeInstance().format(new Date()));
        
        // End the comment.
        out.println(" */");
        
        out.println();
        
        for (final VaultElementInfo moduleDeclaration : vaultModuleInfo) {
            // Write the vault descriptor.
            out.print(moduleDeclaration.getVaultDescriptor() + " ");
            
            // Write the module name.
            out.print(moduleDeclaration.getElementName() + " ");
            
            // Write the location string if any.
            String locationString = moduleDeclaration.getLocationString();
            if (locationString == null) {
                continue;
            }
            out.print(locationString + " ");
            
            // Write the revision number, and end the line.
            out.println(moduleDeclaration.getRevision());
        }

        // Check for errors (which also flushes the stream).
        if (out.checkError()) {
            status.add(new Status(Status.Severity.ERROR, "Error saving workspace declaration."));

        } else {
            // Close the stream.
            out.close();
        }
    }
    
    /**
     * A simple wrapper around an input stream on a workspace declaration, and a name for that stream.
     * @author Edward Lam
     */
    public interface StreamProvider {
        /**
         * @return the name of the workspace declaration.
         */
        public String getName();
        
        /**
         * @return a user-readable name for the workspace declaration location.
         */
        public String getLocation();
        
        /**
         * @param vaultRegistry the vault registry to use to get the workspace declaration from a vault if necessary.
         * @return debugging information about the workspace declaration, e.g. its actual location.
         */
        public String getDebugInfo(VaultRegistry vaultRegistry);
        
        /**
         * @param vaultRegistry the vault registry to use to get the workspace declaration from a vault if necessary.
         * @param status the tracking status object.
         * @return an input stream on the wrapped workspace declaration, or null if such a stream could not be created.
         */
        public InputStream getInputStream(VaultRegistry vaultRegistry, Status status);
    }


}
