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
 * VirtualResourceManager.java
 * Creation date: Jan 12, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.machine.AsynchronousFileWriter;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.metadata.MetadataManager;
import org.openquark.cal.runtime.MachineType;


/**
 * This class encapsulates the notion that there can be multiple resource
 * managers for each kind of module resource, and that there is a mapping from
 * module name to the set of resource managers for it. This is an extension of
 * the concept represented by the {@link ResourceManagerRegistry}, which maps a
 * resource type to a resource manager.
 * <p>
 * This logic is meant to handle working with CAL Archives (Cars), where a
 * module can either be found in the main location (e.g. on the file system) or
 * in some Car, which has its own managers for the resources found inside the
 * Car.
 * 
 * @author Joseph Wong
 */
public final class VirtualResourceManager {
    
    /**
     * The resource manager registry for resource types that are not module oriented (and hence not Car-aware).
     */
    private final ResourceManagerRegistry mainRegistry;
    
    /**
     * A Map mapping a Car name to the corresponding Car accessor. This map contains as values all the Cars that
     * are managed by this manager.
     */
    private final Map<ModuleName, Car.Accessor> carAccessors;
    
    /**
     * A Set of CarAwareResourceManagerProvider - the providers for Car-aware resource managers, which will
     * be called upon to create new resource managers when a Car is added to this manager.
     */
    private final Set<CarAwareResourceManagerProvider> carAwareResourceManagerProviders;
    
    /**
     * This class encapsulates a module name to resource manager mapping. It is intended to be used as
     * return values for API methods that need to return a copy of the underlying module name to Car mapping.
     * 
     * While this class is public, it is not intended to be instantiated by clients - only by methods of
     * {@link VirtualResourceManager}.
     *
     * @author Joseph Wong
     */
    // TODOEL: This is here for the RenameRefactorer. We may want to remove this when we refactor the renamer.
    public static class ModuleNameToResourceManagerMapping {
        /**
         * The resource type of the resource managers in this mapping.
         */
        private final String resourceType;
        /**
         * The Map mapping a module name to the corresponding resource manager.
         */
        private final Map<ModuleName, ResourceManager> mapping;
        /**
         * The default manager to return when a module name is not contained in the mapping. Can be null.
         */
        private final ResourceManager defaultManager;
        
        /**
         * Private constructor for ModuleNameToResourceManagerMapping.
         * @param resourceType the resource type of the resource managers in this mapping.
         * @param mapping the mapping from module names to resource managers.
         * @param defaultManager the default manager to return when a module name is not contained in the mapping. Can be null.
         */
        private ModuleNameToResourceManagerMapping(final String resourceType, final Map<ModuleName, ResourceManager> mapping, final ResourceManager defaultManager) {
            
            if (resourceType == null || mapping == null) {
                throw new NullPointerException();
            }
            
            this.resourceType = resourceType;
            this.mapping = mapping;
            this.defaultManager = defaultManager;
        }
        
        /**
         * @return the resource type of the resource managers in this mapping.
         */
        public String getResourceType() {
            return resourceType;
        }
        
        /**
         * Returns the resource manager for the specified module.
         * @param moduleName the name of the module.
         * @return the corresponding resource manager. Can be null if there is no appropriate resource manager for the module.
         */
        public ResourceManager getManager(final ModuleName moduleName) {
            final ResourceManager resourceManager = mapping.get(moduleName);
            if (resourceManager != null) {
                return resourceManager;
            } else {
                return defaultManager;
            }
        }
    }
    
    /**
     * This inner class implements a Car-aware ProgramResourceRepository that relies on the module name to Car mapping
     * of the enclosing instance to properly dispatch the various operations to the correct ProgramResourceRepository
     * based on the module name in the {@link ProgramResourceLocator}.
     *
     * @author Joseph Wong
     */
    private class CarAwareProgramResourceRepository implements ProgramResourceRepository {
        
        /**
         * The base repository to use when a module name is not found in the Car mapping.
         */
        private final ProgramResourceRepository baseRepository;
        /**
         * The path mapper to use for the program resources.
         */
        private final ProgramResourcePathMapper pathMapper;
        
        /**
         * Private constructor for CarAwareProgramResourceRepository. Instances should be created via
         * {@link VirtualResourceManager#getProviderForCarAwareProgramResourceRepository}.
         * 
         * @param baseRepository the base repository to use when a module name is not found in the Car mapping.
         * @param pathMapper the path mapper to use for the program resources.
         */
        private CarAwareProgramResourceRepository(final ProgramResourceRepository baseRepository, final ProgramResourcePathMapper pathMapper) {
            if (baseRepository == null || pathMapper == null) {
                throw new NullPointerException();
            }
            
            this.baseRepository = baseRepository;
            this.pathMapper = pathMapper;
        }

        /**
         * {@inheritDoc}
         */
        public ModuleName[] getModules() {
            final ModuleName[] modulesFromBaseRepository = baseRepository.getModules();
            final Set<ModuleName> modulesFromCars = carAccessors.keySet();
            
            final Set<ModuleName> combinedModules = new HashSet<ModuleName>(Arrays.asList(modulesFromBaseRepository));
            combinedModules.addAll(modulesFromCars);
            return combinedModules.toArray(new ModuleName[combinedModules.size()]);
        }

        /**
         * {@inheritDoc}
         */
        public ProgramResourceLocator[] getMembers(final ProgramResourceLocator.Folder folder) {
            final Car.Accessor carAccessor = carAccessors.get(folder.getModuleName());
            if (carAccessor == null) {
                return baseRepository.getMembers(folder);
            } else {
                return carAccessor.getFolderMembers(folder, ProgramResourcePathMapper.getResourcePath(folder, pathMapper));
            }
        }

        /**
         * {@inheritDoc}
         */
        public InputStream getContents(final ProgramResourceLocator.File fileLocator) throws IOException {
            final Car.Accessor carAccessor = carAccessors.get(fileLocator.getModuleName());
            if (carAccessor == null) {
                return baseRepository.getContents(fileLocator);
            } else {
                return carAccessor.getResource(ProgramResourcePathMapper.getResourcePath(fileLocator, pathMapper));
            }
        }

        /**
         * {@inheritDoc}
         */
        public void setContents(final ProgramResourceLocator.File fileLocator, final InputStream source) throws IOException {
            final Car.Accessor carAccessor = carAccessors.get(fileLocator.getModuleName());
            if (carAccessor == null) {
                baseRepository.setContents(fileLocator, source);
            } else {
                throw new IOException("A Car file is read-only and cannot be modified.");
            }
        }

        /**
         * {@inheritDoc}
         */
        public void ensureFolderExists(final ProgramResourceLocator.Folder resourceFolder) throws IOException {
            final Car.Accessor carAccessor = carAccessors.get(resourceFolder.getModuleName());
            if (carAccessor == null) {
                baseRepository.ensureFolderExists(resourceFolder);
            } else {
                throw new IOException("A Car file is read-only and cannot be modified.");
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean exists(final ProgramResourceLocator resourceLocator) {
            final Car.Accessor carAccessor = carAccessors.get(resourceLocator.getModuleName());
            if (carAccessor == null) {
                return baseRepository.exists(resourceLocator);
            } else {
                return carAccessor.doesResourceExist(ProgramResourcePathMapper.getResourcePath(resourceLocator, pathMapper));
            }
        }

        /**
         * {@inheritDoc}
         */
        public void delete(final ProgramResourceLocator resourceLocator) throws IOException {
            final Car.Accessor carAccessor = carAccessors.get(resourceLocator.getModuleName());
            if (carAccessor == null) {
                baseRepository.delete(resourceLocator);
            } else {
                throw new IOException("A Car file is read-only and cannot be modified.");
            }
        }

        /**
         * {@inheritDoc}
         */
        public void delete(final ProgramResourceLocator[] resourceLocators) throws IOException {
            for (final ProgramResourceLocator element : resourceLocators) {
                delete(element);
            }
        }

        /**
         * {@inheritDoc}
         */
        public long lastModified(final ProgramResourceLocator resourceLocator) {
            final Car.Accessor carAccessor = carAccessors.get(resourceLocator.getModuleName());
            if (carAccessor == null) {
                return baseRepository.lastModified(resourceLocator);
            } else {
                return carAccessor.getResourceLastModifiedTime(ProgramResourcePathMapper.getResourcePath(resourceLocator, pathMapper));
            }
        }

        /**
         * {@inheritDoc}
         */
        public long getSize(final ProgramResourceLocator.File fileLocator) {
            final Car.Accessor carAccessor = carAccessors.get(fileLocator.getModuleName());
            if (carAccessor == null) {
                return baseRepository.getSize(fileLocator);
            } else {
                return carAccessor.getResourceSize(ProgramResourcePathMapper.getResourcePath(fileLocator, pathMapper));
            }
        }

        /**
         * {@inheritDoc}
         */
        public File getFile(final ProgramResourceLocator resourceLocator) {
            final Car.Accessor carAccessor = carAccessors.get(resourceLocator.getModuleName());
            if (carAccessor == null) {
                return baseRepository.getFile(resourceLocator);
            } else {
                // the contract says that null should be returned if the resource is not backed by the file system and does not have a java.io.File representation
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        public boolean isEmpty() {
            return baseRepository.isEmpty() && carAccessors.isEmpty();
        }

        /**
         * {@inheritDoc}
         */
        public String getLocationString() {
            final StringBuilder location = new StringBuilder(baseRepository.getLocationString());
            
            for (final Map.Entry<ModuleName, Car.Accessor> entry : carAccessors.entrySet()) {
                final ModuleName moduleName = entry.getKey();
                final Car.Accessor carAccessor = entry.getValue();
                
                location.append("\n[Car: ").append(carAccessor.getCarFileName()).append(", module: ").append(moduleName).append("]");
            }
            
            return location.toString();
        }

        /**
         * {@inheritDoc}
         */
        public String getDebugInfo(final ProgramResourceLocator.File fileLocator) {
            final Car.Accessor carAccessor = carAccessors.get(fileLocator.getModuleName());
            if (carAccessor == null) {
                return baseRepository.getDebugInfo(fileLocator);
            } else {
                return carAccessor.getDebugInfo(ProgramResourcePathMapper.getResourcePath(fileLocator, pathMapper));
            }
        }

        /**
         * {@inheritDoc}
         */
        public AsynchronousFileWriter getAsynchronousFileWriter() {
            return new BasicAsynchronousFileWriter(this);
        }
    }
    
    /**
     * This interface specifies a provider that is capable of creating a new resource manager
     * of a particular resource type for each Car in the system.
     *
     * @author Joseph Wong
     */
    public static interface CarAwareResourceManagerProvider {
        
        /**
         * @return the resource type handled by this provider.
         */
        public String getResourceType();
        
        /**
         * @return the default manager to use when a module name is not contained in the module name to Car mapping. Can be null.
         */
        public ResourceManager getDefaultResourceManager();
        
        /**
         * Creates a resource manager for the given Car.
         * @param carAccessor the accessor for the Car.
         * @return the resource manager created for used with that Car.
         */
        public ResourceManager getResourceManagerForCar(Car.Accessor carAccessor);
    }

    /**
     * Constructs an instance of VirtualResourceManager.
     */
    public VirtualResourceManager() {
        this.mainRegistry = new ResourceManagerRegistry();
        this.carAccessors = new HashMap<ModuleName, Car.Accessor>();
        this.carAwareResourceManagerProviders = new HashSet<CarAwareResourceManagerProvider>();
    }

    /**
     * Returns a ProgramResourceRepository.Provider that provides Car-aware ProgramResourceRepository instances.
     * 
     * @param baseProvider the base repository for use by the Car-aware ProgramResourceRepository when
     *                     a module name is not found in the Car mapping.
     * 
     * @return the provider for Car-aware ProgramResourceRepository instances.
     */
    ProgramResourceRepository.Provider getProviderForCarAwareProgramResourceRepository(final ProgramResourceRepository.Provider baseProvider) {

        return new ProgramResourceRepository.Provider() {

            public ProgramResourceRepository getRepository(final MachineType machineType) {
                final ProgramResourceRepository baseRepository = baseProvider.getRepository(machineType);
                return new CarAwareProgramResourceRepository(baseRepository, new ProgramResourcePathMapper(machineType));
            }
        };
    }
    
    /**
     * Registers a non-Car-aware resource manager.
     * @param resourceManager the resource manager to be registered.
     */
    public void registerNonCarAwareResourceManager(final ResourceManager resourceManager) {
        mainRegistry.registerResourceManager(resourceManager);
    }
    
    /**
     * Registers a Car-aware resource manager (via registering the affiliated CarAwareResourceManagerProvider).
     * @param provider the provider for the Car-aware resource manager.
     */
    public void registerCarAwareResourceManager(final CarAwareResourceManagerProvider provider) {
        mainRegistry.registerResourceManager(provider.getDefaultResourceManager());
        
        // it is often the case that many modules map to the same Car, so we want to
        // use a set so that we do not have to iterate over the same Car over and over again
        // (if we iterate through carAccessors.values() directly).
        final Set<Car.Accessor> carAccessorSet = new HashSet<Car.Accessor>(carAccessors.values());
        
        for (final Car.Accessor carAccessor : carAccessorSet) {
            carAccessor.registerResourceManager(provider.getResourceManagerForCar(carAccessor));
        }
    }
    
    /**
     * Associates a module name with the given Car.
     * @param moduleName the name of the module.
     * @param carAccessor the accessor for the Car.
     */
    void linkModuleWithCar(final ModuleName moduleName, final Car.Accessor carAccessor) {
        carAccessors.put(moduleName, carAccessor);
        
        for (final CarAwareResourceManagerProvider provider : carAwareResourceManagerProviders) {
            
            // if the Car accessor has been linked with another module before, it would have
            // the additional resource managers already, so we do not register the resource manager again in that case
            if (carAccessor.getResourceManager(provider.getResourceType()) == null) {
                carAccessor.registerResourceManager(provider.getResourceManagerForCar(carAccessor));
            }
        }
    }
    
    /**
     * Disassociates the module name from any Cars.
     * @param moduleName the name of the module.
     */
    void unlinkModuleFromCars(final ModuleName moduleName) {
        carAccessors.remove(moduleName);
    }

    /**
     * @return the registered source manager, if any.
     */
    public CALSourceManager getSourceManager(final ModuleName moduleName) {
        final Car.Accessor carAccessor = carAccessors.get(moduleName);
        if (carAccessor == null) {
            return mainRegistry.getSourceManager();
        } else {
            return carAccessor.getSourceManager();
        }
    }
    
    /**
     * @return a mapping from module name to the source manager responsible for that module.
     */
    public ModuleNameToResourceManagerMapping getModuleNameToSourceManagerMapping() {
        return getModuleNameToResourceManagerMapping(ModuleSourceDefinition.RESOURCE_TYPE);
    }
    
    /**
     * @return the registered metadataManager, if any.
     */
    public MetadataManager getMetadataManager(final ModuleName moduleName) {
        final Car.Accessor carAccessor = carAccessors.get(moduleName);
        if (carAccessor == null) {
            return mainRegistry.getMetadataManager();
        } else {
            return carAccessor.getMetadataManager();
        }
    }
    
    /**
     * @return a mapping from module name to the metadata manager responsible for that module.
     */
    public ModuleNameToResourceManagerMapping getModuleNameToMetadataManagerMapping() {
        return getModuleNameToResourceManagerMapping(WorkspaceResource.METADATA_RESOURCE_TYPE);
    }
    
    /**
     * @return the registered gem design manager, if any.
     */
    public GemDesignManager getDesignManager(final ModuleName moduleName) {
        final Car.Accessor carAccessor = carAccessors.get(moduleName);
        if (carAccessor == null) {
            return mainRegistry.getDesignManager();
        } else {
            return carAccessor.getDesignManager();
        }
    }
    
    /**
     * @return a mapping from module name to the gem design manager responsible for that module.
     */
    public ModuleNameToResourceManagerMapping getModuleNameToDesignManagerMapping() {
        return getModuleNameToResourceManagerMapping(WorkspaceResource.GEM_DESIGN_RESOURCE_TYPE);
    }
    
    /**
     * @return the registered user resource manager, if any.
     */
    public UserResourceManager getUserResourceManager(final ModuleName moduleName) {
        final Car.Accessor carAccessor = carAccessors.get(moduleName);
        if (carAccessor == null) {
            return mainRegistry.getUserResourceManager();
        } else {
            return carAccessor.getUserResourceManager();
        }
    }
    
    /**
     * @return a mapping from module name to the user resource manager responsible for that module.
     */
    public ModuleNameToResourceManagerMapping getModuleNameToUserResourceManagerMapping() {
        return getModuleNameToResourceManagerMapping(WorkspaceResource.USER_RESOURCE_TYPE);
    }
    
    /**
     * Get the resource manager which handles ModuleResources of a given type for the given module.
     * @param moduleName the name of the module whose resource manager is to be returned.
     * @param resourceType the String which identifies the type of resource.
     * @return the resource manager which handles resource of that type for that module, or null if there isn't any.
     */
    public ResourceManager getModuleSpecificResourceManager(final ModuleName moduleName, final String resourceType) {
        final Car.Accessor carAccessor = carAccessors.get(moduleName);
        if (carAccessor == null) {
            return mainRegistry.getResourceManager(resourceType);
        } else {
            return carAccessor.getResourceManager(resourceType);
        }
    }
    
    /**
     * Returns a mapping from module name to the resource manager responsible for that module for the given resource type.
     * @param resourceType the String which identifies the type of resource.
     * @return a mapping from module name to the resource manager responsible for that module for the given resource type.
     *         This will not be null.
     */
    public ModuleNameToResourceManagerMapping getModuleNameToResourceManagerMapping(final String resourceType) {
        final Map<ModuleName, ResourceManager> mapping = new HashMap<ModuleName, ResourceManager>();
        
        for (final Map.Entry<ModuleName, Car.Accessor> entry : carAccessors.entrySet()) {
            final ModuleName moduleName = entry.getKey();
            final Car.Accessor carAccessor = entry.getValue();
            
            mapping.put(moduleName, carAccessor.getResourceManager(resourceType));
        }
        
        return new ModuleNameToResourceManagerMapping(resourceType, mapping, mainRegistry.getResourceManager(resourceType));
    }

    /**
     * @return the registered workspace manager, if any.
     */
    public WorkspaceDeclarationManager getWorkspaceDeclarationManager() {
        return (WorkspaceDeclarationManager)mainRegistry.getResourceManager(WorkspaceResource.WORKSPACE_DECLARATION_RESOURCE_TYPE);
    }
    
    /**
     * @return the registered Car manager, if any.
     */
    public CarManager getCarManager() {
        return (CarManager)mainRegistry.getResourceManager(WorkspaceResource.CAR_RESOURCE_TYPE);
    }
    
    /**
     * @param moduleName the name of the module whose resource managers are to be fetched
     * @return the resource managers for the resources of the given module.
     */
    public Set<ResourceManager> getResourceManagersForModule(final ModuleName moduleName) {
        final Car.Accessor carAccessor = carAccessors.get(moduleName);
        if (carAccessor == null) {
            return mainRegistry.getResourceManagers();
        } else {
            return carAccessor.getResourceManagers();
        }
    }
    
    /**
     * Retrieves the resource manager that manages the specified resource.
     * @param identifier the identifier of the resource whose manager is to be returned.
     * @return the corresponding resource manager, or null if there is none.
     */
    public ResourceManager getResourceManagerForResource(final ResourceIdentifier identifier) {
        final FeatureName featureName = identifier.getFeatureName();
        final String resourceType = identifier.getResourceType();
        
        if (featureName instanceof CALFeatureName) {
            final CALFeatureName calFeatureName = (CALFeatureName)featureName;
            return getModuleSpecificResourceManager(calFeatureName.toModuleName(), resourceType);
        } else {
            return mainRegistry.getResourceManager(resourceType);
        }
    }
    
    /**
     * Removes all modifiable resources from this manager.
     * @param removeStatus the tracking status object.
     */
    void removeAllResources(final Status removeStatus) {
        // the resources in Cars do not need to be removed, since their enclosing Cars would be
        // removed by the CarManager in the main registry.
        
        for (final ResourceManager resourceManager : mainRegistry.getResourceManagers()) {
            resourceManager.removeAllResources(removeStatus);
        }
    }
}
