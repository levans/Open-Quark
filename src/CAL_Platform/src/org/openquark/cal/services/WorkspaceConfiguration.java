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
 * WorkspaceConfiguration.java
 * Creation date: Nov 10, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;

import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.General;



/**
 * This class holds configuration information for the workspace.
 * @author Edward Lam
 */
public final class WorkspaceConfiguration {
    
    /** The name of the system property to specify the workspace declaration location. */
    public static final String WORKSPACE_SPEC_PROP = "org.openquark.cal.workspace.spec";
    
    /*
     * Workspace environment variables/properties.
     */
    
    /**
     * A property identifying the default client for this workspace.
     *   Client id's are used to refer to specific named workspaces.  If the client id is null, the workspace is "nullary".
     **/
    public static final String WORKSPACE_PROP_CLIENT_ID = "org.openquark.cal.workspace.client";
    
    /** Only applicable to non-nullary (discrete) workspaces.
     * If set, the workspace will be re-generated from the indicated originating modules.
     * */
    public static final String WORKSPACE_PROP_REGENERATE = "org.openquark.cal.workspace.regenerate";

    /** The system property which can be set to specify the full class name for a workspace provider factory other than the default. */
    public static final String PROVIDER_FACTORY_PROPERTY = "org.openquark.cal.services.CALWorkspaceEnvironmentProvider.Factory";
    
    /*
     * File/folder info
     */
    
    /** The base name for the workspace directory. */
    public static final String WORKSPACE_FOLDER_BASE_NAME = "workspace";
    
    /** The name of the workspace description file. */
    public static final String WORKSPACE_DESCRIPTION_FILE_SUFFIX = "description.xml";

    /**
     * Not intended to be instantiated.
     */
    private WorkspaceConfiguration() {
    }
    
    /**
     * Get the root directory for all source generation (whether for java source files, class files or otherwise).
     * @return File the workspace root to use, as specified by the relevant system property.
     * null if the property is not set, or the property specifies an invalid file name
     */
    static File getOutputDirectoryFromProperty() {

        // Use the system property if any.
        String rootFromProperty = System.getProperty(LECCMachineConfiguration.OUTPUT_DIR_PROP);
        if (rootFromProperty != null) {
            File sourceRootDirectory = new File(rootFromProperty);
            if (FileSystemHelper.ensureDirectoryExists(sourceRootDirectory)) {
                return sourceRootDirectory;
            }
        }

        return null;
    }
    
    /**
     * Get a discrete workspace id, as defined by the workspace id system property.
     * 
     * @param defaultWorkspaceID a default value for the discrete workspace id.
     * @return if the workspace id system property is set, the value of the property.
     * Otherwise, the default workspace id is returned.
     */
    public static String getDiscreteWorkspaceID(String defaultWorkspaceID) {
        return General.getDefaultOrSystemProperty(WORKSPACE_PROP_CLIENT_ID, defaultWorkspaceID);
    }

}
