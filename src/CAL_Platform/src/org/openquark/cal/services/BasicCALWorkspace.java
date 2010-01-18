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
 * BasicCALWorkspace.java
 * Creation date: Dec 12, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;

import org.openquark.cal.machine.ProgramResourceRepository.Provider;
import org.openquark.cal.metadata.MetadataFileStore;
import org.openquark.cal.metadata.MetadataManager;
import org.openquark.cal.metadata.MetadataNullaryStore;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.ResourcePath.FilePath;
import org.openquark.util.FileSystemHelper;


/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * The common case of a cal workspace, where the workspace exists in an unencumbered environment.
 * @author Edward Lam
 */
public abstract class BasicCALWorkspace extends CALWorkspace {

    /**
     * The name of the subdirectory to go under the system temp directory if no suitable source generation root directory can
     * be found using regular means.
     */
    private static final String TEMPORARY_SOURCE_GENERATION_ROOT_SUBDIRECTORY_NAME = "openquark.cal";
    
    /** The shared singleton workspace provider factory instance for BasicCALWorkspaces. */
    public static CALWorkspaceEnvironmentProvider.Factory PROVIDER_FACTORY = new CALWorkspaceEnvironmentProvider.Factory() {
        public CALWorkspaceEnvironmentProvider createCALWorkspaceProvider(String workspaceID) {
            return new BasicWorkspaceEnvironmentProvider(workspaceID);
        }
    };

    /**
     * The "nullary" CAL workspace.
     * What this means is that the workspace exists in the same location as the StandardVault.
     * @author Edward Lam
     */
    public static class Nullary extends BasicCALWorkspace {

        /**
         * Constructor for a nullary workspace.
         */
        private Nullary() {
            super(null);
        }
        
        /**
         * Factory for a "nullary" CALWorkspace.
         * @return a new nullary workspace.
         */
        public static CALWorkspace makeNullaryWorkspace() {

            Nullary workspace = new Nullary();
            
            // Register the resource managers.
            workspace.registerNonCarAwareResourceManager(new CALSourceManager(new CALSourceNullaryStore()));
            workspace.registerNonCarAwareResourceManager(new MetadataManager(new MetadataNullaryStore()));
            workspace.registerNonCarAwareResourceManager(new GemDesignManager(new GemDesignNullaryStore()));
            workspace.registerNonCarAwareResourceManager(new CarManager(new CarNullaryStore()));
            workspace.registerNonCarAwareResourceManager(new WorkspaceDeclarationManager(new WorkspaceDeclarationNullaryStore()));
            workspace.registerNonCarAwareResourceManager(new UserResourceManager(new UserResourceNullaryStore()));
            
            return workspace;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getWorkspaceLocationString() {
            return "(nullary)";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getWorkspaceID() {
            return null;
        }
    }
    
    /**
     * A discrete workspace represents a single folder (plus subfolders) in the file system.
     * Each discrete workspace lives independently of other discrete workspaces having a different client ID.
     * @author Edward Lam
     */
    public static class Discrete extends BasicCALWorkspace {

        private final String workspaceID;
        private final File rootDirectory;

        /**
         * Constructor for a discrete CALWorkspace.
         * @param workspaceID
         * @param rootDirectory
         * @param workspaceDescriptionFile
         */
        private Discrete(String workspaceID, File rootDirectory, FilePath workspaceDescriptionFile) {
            super(workspaceDescriptionFile);
            
            Assert.isNotNull(rootDirectory);
            this.rootDirectory = rootDirectory;
            
            Assert.isNotNull(workspaceID);
            this.workspaceID = workspaceID;
        }
        
        /**
         * Factory method for a discrete CALWorkspace.
         * 
         * @param workspaceID a string identifying the discrete workspace.
         *   Workspace id's are used to identify and refer to specific workspaces.
         * @return a new CALWorkspace with the given workspace ID.
         */
        public static CALWorkspace makeDiscreteWorkspace(String workspaceID) {
            Assert.isNotNull(workspaceID);
            
            ResourcePath.Folder workspaceFolder = new ResourcePath.Folder(new String[] {WorkspaceConfiguration.WORKSPACE_FOLDER_BASE_NAME + "." + workspaceID});
            File rootDirectory = NullaryEnvironment.getNullaryEnvironment().getFile(workspaceFolder, true);
            ResourcePath.FilePath workspaceDescriptionFile = workspaceFolder.extendFile(workspaceID + "." + WorkspaceConfiguration.WORKSPACE_DESCRIPTION_FILE_SUFFIX);
            
            Discrete workspace = new Discrete(workspaceID, rootDirectory, workspaceDescriptionFile);
            
            // Register the resource managers.
            workspace.registerNonCarAwareResourceManager(new CALSourceManager(new CALSourceFileStore(rootDirectory)));
            workspace.registerNonCarAwareResourceManager(new MetadataManager(new MetadataFileStore(rootDirectory)));
            workspace.registerNonCarAwareResourceManager(new GemDesignManager(new GemDesignFileStore(rootDirectory)));
            workspace.registerNonCarAwareResourceManager(new CarManager(new CarFileStore(rootDirectory)));
            workspace.registerNonCarAwareResourceManager(new UserResourceManager(new UserResourceFileStore(rootDirectory)));
            
            return workspace;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String getWorkspaceID() {
            return workspaceID;
        }
        
        /**
         * @return the folder representing the root of this discrete workspace.
         */
        public File getRootDirectory() {
            return rootDirectory;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getWorkspaceLocationString() {
            return rootDirectory.getAbsolutePath();
        }
    }
    
    /**
     * A workspace provider which provides environments for CALWorkspaces of type BasicCALWorkspace.
     * @author Edward Lam
     */
    private static class BasicWorkspaceEnvironmentProvider implements CALWorkspaceEnvironmentProvider {

        /** The discrete workspace id, or null for a nullary workspace. */
        private final String workspaceID;
        
        /**
         * A cal workspace with the given workspace id, or null if getCALWorkspace() has not been called.
         * Lazily instantiated by getCALWorkspace().
         */
        private CALWorkspace calWorkspace = null;

        /**
         * Constructor for a BasicWorkspaceProvider.
         * @param workspaceID the discrete workspace id, or null for a nullary workspace.
         */
        BasicWorkspaceEnvironmentProvider(String workspaceID) {
            this.workspaceID = workspaceID;
        }
        
        /**
         * {@inheritDoc}
         */
        public CALWorkspace getCALWorkspace() {
            if (calWorkspace == null) {
                if (workspaceID == null) {
                    calWorkspace = BasicCALWorkspace.Nullary.makeNullaryWorkspace();
                } else {
                    calWorkspace = BasicCALWorkspace.Discrete.makeDiscreteWorkspace(workspaceID);
                }
            }
            return calWorkspace;
        }

        /**
         * {@inheritDoc}
         */
        public Provider getDefaultProgramResourceRepositoryProvider() {
            File defaultSourceGenerationRoot = getDefaultSourceGenerationRoot();
            if (defaultSourceGenerationRoot == null) {
                defaultSourceGenerationRoot = getTemporarySourceGenerationRoot();
            }
            return BasicProgramResourcePathRepository.getResourceRepositoryProvider(defaultSourceGenerationRoot);
        }

        /**
         * {@inheritDoc}
         */
        public ResourceManager getDefaultProgramResourceManager(MachineType machineType) {
            File defaultSourceGenerationRoot = getDefaultSourceGenerationRoot();
            if (defaultSourceGenerationRoot == null) {
                defaultSourceGenerationRoot = getTemporarySourceGenerationRoot();
            }
            return ProgramResourceManager.FileSystemBased.make(machineType, defaultSourceGenerationRoot);
        }
        
        /**
         * @return the default root directory for source generation, or null if there was a problem determining this.
         */
        private File getDefaultSourceGenerationRoot() {
            if (workspaceID == null) {
                return getNullaryEnvironment().getFile(ResourcePath.EMPTY_PATH, false);
            } else {
                return ((BasicCALWorkspace.Discrete)getCALWorkspace()).getRootDirectory();
            }
        }

        /**
         * {@inheritDoc}
         */
        public NullaryEnvironment getNullaryEnvironment() {
            return new BasicNullaryEnvironment();
        }
    }
    
    /**
     * Constructor for a BasicCALWorkspace.
     * @param workspaceDescriptionFile
     */
    private BasicCALWorkspace(ResourcePath.FilePath workspaceDescriptionFile) {
        super(workspaceDescriptionFile);
    }
    
    /**
     * @return a temporary directory for the source generation root.
     */
    private static File getTemporarySourceGenerationRoot() {
        File root = new File(System.getProperty("java.io.tmpdir"), TEMPORARY_SOURCE_GENERATION_ROOT_SUBDIRECTORY_NAME);
        FileSystemHelper.ensureDirectoryExists(root);
        return root;
    }
}
