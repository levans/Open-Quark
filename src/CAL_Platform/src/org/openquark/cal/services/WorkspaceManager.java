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
 * WorkspaceManager.java
 * Creation date: Oct 2, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinitionGroup;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ProgramModifier;
import org.openquark.cal.machine.ProgramManager;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.CALWorkspace.SyncInfo;


/**
 * A manager for CAL source and compiled forms of modules.
 * Note: compile() compiles source into the current program.
 * We may want the flexibility to compile to a new program without modifying the existing one.
 * @author Edward Lam
 */
public class WorkspaceManager extends ProgramModelManager {

    /** The default workspace root overrider. */
    static final SourceGenerationRootOverrideProvider DEFAULT_ROOT_OVERRIDER = new SourceGenerationRootOverrideProvider() {
        public File getSourceGenerationRootOverride(String clientID) {
            // Use the system property.
            return WorkspaceConfiguration.getOutputDirectoryFromProperty();
        }
    };
    
    /** The workspace to manage. */
    private final CALWorkspace workspace;
    
    /**
     * The stream provider for the workspace declaration used for initializing
     * the workspace. Can be null if the
     * {@link #initWorkspace(WorkspaceDeclaration.StreamProvider, Status)}
     * method has not been called.
     */
    private WorkspaceDeclaration.StreamProvider initialWorkspaceDeclarationProvider;

    /**
     * This provider interface allows the client to override where the root of the workspace for source generation
     *   will be located when constructing the workspace maanger.
     * 
     * @author Edward Lam
     */
    public interface SourceGenerationRootOverrideProvider {
        /**
         * @param clientID the discrete workspace id, or null for a nullary workspace
         * @return the file folder into which program resources will be generated, or null to generate to the default location.
         */
        public File getSourceGenerationRootOverride(String clientID);
    }
    
    /**
     * Private constructor for a workspace manager.
     * @param programManager the program manager used by this manager.
     * @param workspace the workspace managed by this manager.
     */
    private WorkspaceManager(ProgramManager programManager, CALWorkspace workspace) {
        super(programManager);
        
        this.workspace = workspace;
        if (workspace == null) {
            throw new NullPointerException("Argument 'workspace' must not be null. ");
        }
    }

    /**
     * Factory method for a workspace manager.
     * @param clientID a string identifying the client for this workspace.
     *   Client id's are used to refer to specific workspaces.
     */
    public static WorkspaceManager getWorkspaceManager(String clientID) {
        return getWorkspaceManager(clientID, null, DEFAULT_ROOT_OVERRIDER);
    }
    
    /**
     * Package-scoped factory method for a workspace manager -- DO NOT EXPOSE.
     * This is intended to be called by the unit testing infrastructure to enable designation of a specific machine type
     * and source generation root.
     * 
     * @param clientID
     *            a string identifying the client for this workspace. Client
     *            id's are used to refer to specific workspaces.
     * @param machineTypeOverride the machine type to use, or null to use the default machine type.
     * @param rootOverrideProvider
     *            the WorkspaceRootOverrideProvider instance to use for overriding (if necessary) the root of the
     *            Workspace for source generation for this WorkspaceManager.
     */
    static WorkspaceManager getWorkspaceManager(String clientID, MachineType machineTypeOverride, SourceGenerationRootOverrideProvider rootOverrideProvider) {
        
        // Get the workspace provider factory.
        CALWorkspaceEnvironmentProvider.Factory providerFactory = WorkspaceLoader.getWorkspaceProviderFactory();
        
        // Create the workspace provider, and use this to get the workspace.
        CALWorkspaceEnvironmentProvider workspaceProvider = providerFactory.createCALWorkspaceProvider(clientID);
        CALWorkspace workspace = workspaceProvider.getCALWorkspace();
        
        // Instantiate the repository provider.  Override default if the source generation root is overridden.
        File sourceGenerationRootOverride = rootOverrideProvider.getSourceGenerationRootOverride(clientID);

        ProgramResourceRepository.Provider programResourceRepositoryProvider;
        if (sourceGenerationRootOverride != null) {
            programResourceRepositoryProvider = BasicProgramResourcePathRepository.getResourceRepositoryProvider(sourceGenerationRootOverride);
        } else {
            programResourceRepositoryProvider = workspaceProvider.getDefaultProgramResourceRepositoryProvider();
        }
        if (programResourceRepositoryProvider == null) {
            return null;
        }

        // Create the program manager using the repository provider.
        // Override the machine type if given.
        ProgramResourceRepository.Provider carAwareProgramResourceRepositoryProvider =
            workspace.getProviderForCarAwareProgramResourceRepository(programResourceRepositoryProvider);
        
        ProgramManager programManager = machineTypeOverride == null ?
                ProgramManager.getProgramManager(carAwareProgramResourceRepositoryProvider, workspace) :
                    ProgramManager.getProgramManager(machineTypeOverride, carAwareProgramResourceRepositoryProvider, workspace);

                
        // Note: if the g-machine also uses a resource manager, we can remove the if test.
        final MachineType realMachineType = programManager.getMachineType();
        if (realMachineType == MachineType.LECC) {
            // Register the program resource manager so that the resources can be managed by the workspace.
            // Override default if the source generation root is overridden.
            final ResourceManager fileSystemBasedProgramResourceManager;
            if (sourceGenerationRootOverride != null) {
                fileSystemBasedProgramResourceManager = ProgramResourceManager.FileSystemBased.make(realMachineType, sourceGenerationRootOverride);
            } else {
                fileSystemBasedProgramResourceManager = workspaceProvider.getDefaultProgramResourceManager(realMachineType);
            }
            
            workspace.registerCarAwareResourceManager(new VirtualResourceManager.CarAwareResourceManagerProvider() {
                public String getResourceType() {
                    return fileSystemBasedProgramResourceManager.getResourceType();
                }
                public ResourceManager getDefaultResourceManager() {
                    return fileSystemBasedProgramResourceManager;
                }
                public ResourceManager getResourceManagerForCar(Car.Accessor carAccessor) {
                    return ProgramResourceManager.CarBased.make(realMachineType, carAccessor);
                }
            });
        }
        
        return new WorkspaceManager(programManager, workspace);
    }
    
    /**
     * Initialize the workspace manager with the given workspace contents.
     * If a previously-persisted workspace exists, that workspace will be loaded, unless the system property to force regeneration is set.
     * Otherwise, a new workspace will be created from the given stream provider on the workspace declaration.
     * 
     * No compilation will take place.
     * 
     * @param workspaceDeclarationProvider the provider for the input stream containing the workspace declaration.
     * The input stream will only be read if a previously-persisted workspace does not exist.
     * @param initStatus the tracking status object.
     */
    public void initWorkspace(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider, Status initStatus) {
        
        // Get the system property which indicates whether the workspace should be regenerated.
        boolean forceRegen = System.getProperty(WorkspaceConfiguration.WORKSPACE_PROP_REGENERATE) != null;
        
        initWorkspace(workspaceDeclarationProvider, forceRegen, initStatus);
    }

    /**
     * Initialize the workspace manager with the given workspace contents.
     * If a previously-persisted workspace exists, that workspace can be optionally loaded.
     * Otherwise, a new workspace will be created from the given stream provider on the workspace declaration.
     * 
     * No compilation will take place.
     * 
     * @param workspaceDeclarationProvider the provider for the input stream containing the workspace declaration.
     * The input stream will only be read if a previously-persisted workspace does not exist.
     * @param forceRegen whether to force regeneration of the workspace, even in the presence of the previously-existing workspace.
     * @param initStatus the tracking status object.
     */
    public void initWorkspace(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider, boolean forceRegen, Status initStatus) {
        
        initialWorkspaceDeclarationProvider = workspaceDeclarationProvider;
        
        // Use any previously-persisted workspace to initialize.
        if (!forceRegen && loadPersistedWorkspace(initStatus)) {
            return;
        }
        
        if (initStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
            String message = "Problems were encountered loading the previous workspace: \n" + initStatus.getDebugMessage();
            initStatus.add(new Status(Status.Severity.ERROR, message, null));
            
            return;
        }
        
        
        // No previously-persisted workspace.
        // Use the provided provider
        if (workspaceDeclarationProvider == null) {
            String message = "A previous workspace does not exist, and a new Workspace definition cannot be found.";
            initStatus.add(new Status(Status.Severity.ERROR, message, null));
            
            return;
        }
        
        // Create a new workspace from the workspace definition, replacing any existing definition.
        StoredVaultElement.Module[] storedModules =
                WorkspaceLoader.getStoredModules(workspaceDeclarationProvider, workspace.getVaultRegistry(), initStatus);
        
        if (storedModules != null) {
            workspace.initializeWorkspace(storedModules, initStatus);
        }

        if (initStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
            String message = "Problems encountered while constructing the workspace:\n" + initStatus.getDebugMessage();
            initStatus.add(new Status(Status.Severity.ERROR, message, null));
        }
    }
    
    /**
     * Package-scoped helper for getting the workspace declaration used for initializing the workspace.
     * Intended only for use by {@link CarBuilder#makeConfigurationsForOneCarPerWorkspaceDeclaration}.
     * 
     * @return the stream provider for the workspace declaration used for initializing
     * the workspace. Can be null if the
     * {@link #initWorkspace(WorkspaceDeclaration.StreamProvider, Status)}
     * method has not been called.
     */
    WorkspaceDeclaration.StreamProvider getInitialWorkspaceDeclarationProvider() {
        return initialWorkspaceDeclarationProvider;
    }
    
    /**
     * @return the name of the workspace declaration used for initializing the workspace.
     */
    public String getInitialWorkspaceDeclarationName() {
        return getInitialWorkspaceDeclarationProvider().getName();
    }
    
    /**
     * @return the debug info for the workspace declaration used for initializing the workspace.
     */
    public String getInitialWorkspaceDeclarationDebugInfo() {
        return getInitialWorkspaceDeclarationProvider().getDebugInfo(workspace.getVaultRegistry());
    }

    /**
     * Compile the modules in this manager.
     * @param logger the logger used to log error messages.
     * @param dirtyModulesOnly if true, only dirty (uncompiled or changed) modules will be compiled.
     * @param statusListener a listener, possibly null, for the status of the compilation process
     */
    public void compile(CompilerMessageLogger logger, boolean dirtyModulesOnly, StatusListener statusListener) {
        compile(logger, dirtyModulesOnly, statusListener, new CompilationOptions());
    }
    
    /**
     * Compile the modules in this manager.
     * @param logger the logger used to log error messages.
     * @param dirtyModulesOnly if true, only dirty (uncompiled or changed) modules will be compiled.
     * @param statusListener a listener, possibly null, for the status of the compilation process
     * @param options - values for compilation options
     */
    public void compile(CompilerMessageLogger logger, boolean dirtyModulesOnly, StatusListener statusListener, CompilationOptions options) {
        compile(getSourceDefinitionGroup(), logger, dirtyModulesOnly, statusListener, options);
    }

    /**
     * The CAL based optimizer will be used.
     */
    public void useOptimizer(boolean useOptimizer){
        getProgramManager().useOptimizer(useOptimizer);
    }
    
    /**
     * Sync the workspace with a given workspace declaration.
     * 
     * No compilation will take place.
     * 
     * @param workspaceDeclarationProvider the provider for the workspace spec.
     * @param syncStatus the tracking status object.
     */
    public SyncInfo syncWorkspaceToDeclaration(WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider, Status syncStatus) {
        
        // Get the stored modules from the workspace declaration.
        StoredVaultElement.Module[] storedModules =
                WorkspaceLoader.getStoredModules(workspaceDeclarationProvider, workspace.getVaultRegistry(), syncStatus);
        
        CALWorkspace.SyncInfo syncInfo = new CALWorkspace.SyncInfo();
        
        if (storedModules != null) {
            // Iterate over the stored modules.
            for (final StoredVaultElement.Module storedModule : storedModules) {
                // Sync to the stored module.
                CALWorkspace.SyncInfo newSyncInfo = workspace.syncModuleToStoredModule(storedModule, false, syncStatus);
                syncInfo.addInfo(newSyncInfo);
            }
        }
        
        return syncInfo;
    }
    
    /**
     * Initialize the workspace manager from any previously-persisted workspace.
     * @param loadStatus the tracking status object.
     * @return true if there was a previously-persisted workspace, and it was successfully loaded.
     */
    private boolean loadPersistedWorkspace(Status loadStatus) {
        return workspace.loadPersistedWorkspace(loadStatus);
    }

    /**
     * Get the program held by this manager.
     * @return Program the program held by this manager.
     */
    public CALWorkspace getWorkspace() {
        return workspace;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected ProgramModifier makeProgramModifier(CompilationOptions options) {
        ProgramModifier newProgramModifier = super.makeProgramModifier(options);
        
        // the workspace Program registers as a
        // listener so that it can update itself as the program changes.
        newProgramModifier.addChangeListener(workspace);
        
        return newProgramModifier;
    }

    /**
     * @return the source definition group for the current program.
     */
    protected ModuleSourceDefinitionGroup getSourceDefinitionGroup() {
        return workspace.getSourceDefinitionGroup();
    }
    
    /**
     * @return debugging information about the workspace, e.g. the actual location of each module's resources.
     */
    public String getDebugInfo() {
        String result;
        
        result = "Workspace declaration:\n"
            + initialWorkspaceDeclarationProvider.getName()
            + "\n"
            + "   - "
            + initialWorkspaceDeclarationProvider.getLocation()
            + "\n"
            + "   - "
            + initialWorkspaceDeclarationProvider.getDebugInfo(workspace.getVaultRegistry())
            + "\n\n"
            + "Workspace location:\n"
            + workspace.getWorkspaceLocationString()
            + "\n\n"
            + "Workspace contents:\n"
            + workspace.getDebugInfo()
            + "\n";
        
        return result;
    }
    
    /**
     * Dumps info to the console about all metadata files which do not
     * correspond to features in the featureNameList.
     * 
     * These "orphaned" files come about when a feature's name or type is
     * changed without updating the corresponding metadata file.
     */
    public void dumpOrphanedMetadata() {
        dumpOrphanedResources(WorkspaceResource.METADATA_RESOURCE_TYPE);
    }

    /**
     * Dumps info to the console about all resources files which do not
     * correspond to features in a cal program
     * 
     * These "orphaned" files come about when a feature's name or type is
     * changed without updating the corresponding resource file.
     */
    public void dumpOrphanedResources(String resourceType) {
        // names of all modules in the current program.
        Set<ModuleName> moduleNameSet = new HashSet<ModuleName>();

        // CALFeatureNames for all features in the current program.
        Set<CALFeatureName> programFeatureNameSet = new HashSet<CALFeatureName>();
        
        // Grab the module names and feature names.
        ModuleName[] compiledModuleNames = getProgramManager().getModuleNames();
        for (final ModuleName moduleName : compiledModuleNames) {
            MetaModule metaModule = workspace.getMetaModule(moduleName);
            if (metaModule != null) {
                moduleNameSet.add(moduleName);
                programFeatureNameSet.addAll(metaModule.getFeatureNames());
            }
        }

        System.out.println("\n-- Searching for Orphaned Resources -- \n");
        
        List<FeatureName> orphanedResourceNameList = new ArrayList<FeatureName>();
        Set<ModuleName> orphanedModuleNameSet = new HashSet<ModuleName>();

        // At this point, we have the names of all the modules and features.  Now check resource iterators..
        for (final ModuleName moduleName : moduleNameSet) {
            ResourceManager resourceManager = workspace.getResourceManager(moduleName, resourceType);
            if (resourceManager == null) {
                System.out.println("No registered resource manager for type: " + resourceType + " for module: " + moduleName);
                continue;
            }
            
            if (!(resourceManager instanceof ModuleResourceManager)) {
                String message = "There can be no orphaned resources for type \'" + resourceType + "\", since this is not a module resource type.";
                System.out.println(message);
                break;
            }
            
            ResourceStore.Module resourceStore = (ResourceStore.Module)resourceManager.getResourceStore();
            
            // add to the set of all module names managed by all resource stores
            orphanedModuleNameSet.addAll(resourceStore.getModuleNames());
            
            for (Iterator<WorkspaceResource> it = resourceStore.getResourceIterator(moduleName); it.hasNext(); ) {
            
                WorkspaceResource workspaceResource = it.next();
                FeatureName resourceFeatureName = workspaceResource.getIdentifier().getFeatureName();

                if (!programFeatureNameSet.contains(resourceFeatureName)) {
                    orphanedResourceNameList.add(resourceFeatureName);
                }
            }
        }
        
        if (!orphanedResourceNameList.isEmpty()) {
            System.out.println("The following features have resources of type '" + resourceType + "' but no longer exist:\n");
            for (final FeatureName featureName : orphanedResourceNameList) {
                System.out.println(featureName);
            }
            System.out.println();

        } else {
            System.out.println("No orphaned resource files were found for type '" + resourceType + "'.\n");
        }

        // (orphaned modules) = (all modules managed by all resource) - (modules in workspace)
        orphanedModuleNameSet.removeAll(moduleNameSet);
        
        if (!orphanedModuleNameSet.isEmpty()) {
            System.out.println("The following modules have resources of type '" + resourceType + "' but are not currently loaded:\n");
            for (final ModuleName moduleName : orphanedModuleNameSet) {
                System.out.println(moduleName);
            }
            System.out.println();
        }
        
        System.out.println("Finished.\n");
    }

    public boolean removeModule (ModuleName moduleName, Status status) {
        return removeModule (moduleName, true, status);
    }
    
    public boolean removeModule (ModuleName moduleName, boolean includeDependentsInWorkspace, Status status) {
        
        Set<ModuleName> moduleNames;
        if (includeDependentsInWorkspace) {
            moduleNames = getProgramManager().getSetOfDependentModuleNames(moduleName, true);
        } else {
            moduleNames = new HashSet<ModuleName>();
        }
        moduleNames.add (moduleName);
        
        if (workspace.containsModule(moduleName)) {
            if (!workspace.removeModules(moduleNames, status)) {
                return false;
            }
        }
        
        return super.removeModule(moduleName);
    }
    
    /**
     * 
     * @param moduleName
     * @return type info for the named module.  Null if module doesn't exist.
     */
    @Override
    public ModuleTypeInfo getModuleTypeInfo (ModuleName moduleName) {
        if (!workspace.containsModule(moduleName)) {
            return null;
        }
        
        return super.getModuleTypeInfo(moduleName);
    }

    /**
     * @return the repository for program resources, or null if none.
     */
    public ProgramResourceRepository getRepository() {
        // TODOEL: Should try and hide this method.  Exposure allows clients to screw up the runtime.
        return getProgramManager().getProgramResourceRepository();
    }
}
