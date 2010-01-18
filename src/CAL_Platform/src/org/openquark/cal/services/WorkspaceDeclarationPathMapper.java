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
 * WorkspaceDeclarationPathMapper.java
 * Creation date: Dec 13, 2004.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.services.ResourcePath.FilePath;
import org.openquark.cal.services.ResourcePath.Folder;


/**
 * A ResourcePathMapper for WorkspaceDeclaration resources.
 * @author Edward Lam
 */
public class WorkspaceDeclarationPathMapper implements ResourcePathMapper {

    /** The file extension for workspace declarations. */
    private static final String WORKSPACE_DECLARATION_FILE_EXTENSION = "cws";

    /** The default base folder for saved workspace declarations. */
    private static final String WORKSPACE_DECLARATION_BASE_FOLDER = "Workspace Declarations";

    /** The base folder for this resource. */
    static final ResourcePath.Folder baseDeclarationFolder = new ResourcePath.Folder(new String[] {WORKSPACE_DECLARATION_BASE_FOLDER});
    
    /** Singleton instance. */
    public static final WorkspaceDeclarationPathMapper INSTANCE = new WorkspaceDeclarationPathMapper();
    
    /**
     * Constructor for a WorkspaceDeclarationPathMapper.
     */
    private WorkspaceDeclarationPathMapper() {
    }
    
    /**
     * {@inheritDoc}
     */
    public Folder getBaseResourceFolder() {
        return baseDeclarationFolder;
    }

    /**
     * {@inheritDoc}
     */
    public FilePath getResourcePath(ResourceName resourceName) {
        String workspaceDeclarationName = checkWorkspaceDeclarationName(resourceName);
        return getWorkspacePath(workspaceDeclarationName);
    }
    
    /**
     * {@inheritDoc}
     */
    public ResourceName getResourceNameFromPath(FilePath path) {
        return getResourceNameFromFileName(path.getFileName());
    }
    
    /**
     * {@inheritDoc}
     */
    public ResourceName getResourceNameFromFolderAndFileName(Folder folder, String fileName) {
        return getResourceNameFromFileName(fileName);
    }
    
    /**
     * Constructs a resource name corresponding to a workspace declaration at the given file name.
     * @param fileName the name of the workspace declaration file.
     * @return the corresponding resource name.
     */
    private ResourceName getResourceNameFromFileName(String fileName) {
        return new ResourceName(WorkspaceDeclarationFeatureName.getWorkspaceDeclarationFeatureName(fileName));
    }
    
    /**
     * Get the base path to a workspace declaration with the given name.
     * 
     * @param workspaceDeclarationName the name of the workspace declaration.
     * @return the path under which the workspace declaration should be located.
     */
    public FilePath getWorkspacePath(String workspaceDeclarationName) {
        return baseDeclarationFolder.extendFile(workspaceDeclarationName);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getFileExtension() {
        return WORKSPACE_DECLARATION_FILE_EXTENSION;
    }

    /**
     * Check that a feature name represents a workspace declaration.
     * @param resourceName the name of the workspace declaration resource.
     * @return the workspace declaration name if the feature represents a workspace declaration.
     * @throws IllegalArgumentException if the ResourceIdentifier does not represent a design.
     */
    protected String checkWorkspaceDeclarationName(ResourceName resourceName) {
        FeatureName featureName = resourceName.getFeatureName();
        if (featureName.getType() != WorkspaceDeclarationFeatureName.TYPE) {
            throw new IllegalArgumentException("The feature name must represent a workspace declaration, not the type " + featureName.getType());
        }
        return featureName.getName();
    }

}
