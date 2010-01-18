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
 * CarBuilder.java
 * Creation date: Jan 18, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.Messages;
import org.openquark.util.Pair;
import org.openquark.util.TextEncodingUtilities;


/**
 * This class implements a CAL Archive (Car) builder that will go through the
 * resources in a supplied workspace and add those resources to a new Car.
 * 
 * This class is not meant to be instantiated or subclassed.
 * 
 * @author Joseph Wong
 */
public final class CarBuilder {
    
    private static final String DOT_CWS = "." + WorkspaceDeclarationPathMapper.INSTANCE.getFileExtension();

    private static final String DOT_CAR = "." + CarPathMapper.INSTANCE.getFileExtension();
    
    private static final String DOT_JAR = ".jar";

    public static final String DOT_CAR_DOT_JAR = DOT_CAR + DOT_JAR;
        
    private static final String WORKSPACE_DECLARATIONS_FOLDER_NAME = WorkspaceDeclarationPathMapper.INSTANCE.getBaseResourceFolder().getPathStringMinusSlash();

    private static final String CAR_FOLDER_NAME = CarPathMapper.INSTANCE.getBaseResourceFolder().getPathStringMinusSlash();

    /**
     * The default name to use for the workspace spec file that contains all the modules in the Car.
     */
    private static final String DEFAULT_MAIN_SPEC_NAME = "main.carspec";
    
    /**
     * The singleton instance for accessing the message bundle.
     */
    private static final Messages MESSAGES = new Messages(CarBuilder.class, "carbuilder");

    /**
     * The marker file to be added to all Cars that identifies a file as a Car.
     */
    public static final String CAR_MARKER_NAME = "Car.marker";
    
    /**
     * The name of the special file that contains a list of the additional files in the Car.
     */
    public static final String CAR_ADDITIONAL_FILES_NAME = "Car.additionalFiles";

    /**
     * This class encapsulates the configuration options for the CarBuilder.
     *
     * @author Joseph Wong
     */
    public final static class Configuration {
        
        /**
         * The workspace manager that will provide the resources to be added to the Car.
         */
        private final WorkspaceManager workspaceManager;
        
        /**
         * The names of the modules to be included in the Car.
         */
        private final ModuleName[] moduleNames;
        
        /**
         * A Map mapping a Car Workspace Spec file name to the corresponding CarSpec instance.
         */
        private final Map<String, CarSpec> carSpecs;
        
        /**
         * The name of the main CarSpec.
         */
        private final String mainSpecName;
        
        /**
         * The monitor to be used for monitoring the progress of the Car building operation.
         */
        private Monitor monitor;
        
        /**
         * Whether to build the Car with sourceless modules (i.e. the CAL files will be empty stubs).
         */
        private boolean buildSourcelessModules;
        
        /**
         * A map from relative paths to the contents of additional files to be included in the Car.
         */
        private final Map<String, byte[]> additionalFiles;
        
        /**
         * Constructs an instance of CarBuilder.Configuration where all the modules in the workspace manager
         * will be added to the Car.
         * 
         * @param workspaceManager the workspace manager that will provide the resources to be added to the Car.
         */
        public Configuration(WorkspaceManager workspaceManager) {
            this(workspaceManager, workspaceManager.getWorkspace().getModuleNames());
        }
        
        /**
         * Factory method for constructing an instance of CarBuilder.Configuration where modules that are
         * imported from existing Cars can optionally be excluded.
         * 
         * @param workspaceManager the workspace manager that will provide the resources to be added to the Car.
         * @param options configuration options for the Car builder.
         * @return an instance of this class.
         */
        public static Configuration makeConfigOptionallySkippingModulesAlreadyInCars(WorkspaceManager workspaceManager, BuilderOptions options) {
            
            Configuration config;
            
            if (!options.shouldSkipModulesAlreadyInCars) {
                config = new Configuration(workspaceManager);
                
            } else {
                // modules in Cars should be skipped...
                
                List<ModuleName> nonCarModulesList = new ArrayList<ModuleName>();
                
                CALWorkspace workspace = workspaceManager.getWorkspace();
                ModuleName[] allModuleNames = workspace.getModuleNames();
                
                for (final ModuleName moduleName : allModuleNames) {
                    VaultElementInfo vaultInfo = workspace.getVaultInfo(moduleName);
                    
                    if (!isVaultElementFromCar(vaultInfo)) {
                        nonCarModulesList.add(moduleName);
                    }
                }
                
                ModuleName[] nonCarModules = nonCarModulesList.toArray(new ModuleName[nonCarModulesList.size()]);
                
                config = new Configuration(workspaceManager, nonCarModules);
            }
            
            config.setBuildSourcelessModule(options.buildSourcelessModules);
            
            return config;
        }
        
        /**
         * Constructs an instance of CarBuilder.Configuration where only the named modules will be added to the Car.
         * 
         * @param workspaceManager the workspace manager that will provide the resources to be added to the Car.
         * @param moduleNames the names of the modules to be included in the Car.
         */
        public Configuration(WorkspaceManager workspaceManager, ModuleName[] moduleNames) {
            this(workspaceManager, moduleNames, DEFAULT_MAIN_SPEC_NAME);
        }

        /**
         * Constructs an instance of CarBuilder.Configuration where only the named modules will be added to the Car.
         * 
         * @param workspaceManager the workspace manager that will provide the resources to be added to the Car.
         * @param moduleNames the names of the modules to be included in the Car.
         * @param mainSpecName the name to use for the workspace spec file that contains all the modules in the Car.
         */
        public Configuration(WorkspaceManager workspaceManager, ModuleName[] moduleNames, String mainSpecName) {
            
            if (workspaceManager == null || moduleNames == null || mainSpecName == null) {
                throw new NullPointerException();
            }
            
            this.workspaceManager = workspaceManager;
            this.moduleNames = moduleNames.clone();
            this.carSpecs = new HashMap<String, CarSpec>();
            this.mainSpecName = mainSpecName;
            this.monitor = DefaultMonitor.INSTANCE;
            this.buildSourcelessModules = false;
            this.additionalFiles = new HashMap<String, byte[]>();
            
            // sort the module names in alphabetical order
            Arrays.sort(this.moduleNames);
            
            // add the main spec to the car specs map
            carSpecs.put(mainSpecName, new CarSpec.ModuleList(mainSpecName, this.moduleNames));
        }
        
        /**
         * Specifies the monitor to be used for monitoring the progress of the Car building operation.
         * 
         * @param monitor the monitor to be used by the CarBuilder. If this argument is null, then a {@link
         *                CarBuilder.DefaultMonitor DefaultMonitor} will be used as the monitor.
         */
        public void setMonitor(Monitor monitor) {
            if (monitor == null) {
                this.monitor = DefaultMonitor.INSTANCE;
            } else {
                this.monitor = monitor;
            }
        }
        
        /**
         * Specifies whether to build the Car with sourceless modules (i.e. the CAL files will be empty stubs).
         * @param buildSourcelessModules the setting.
         */
        public void setBuildSourcelessModule(boolean buildSourcelessModules) {
            this.buildSourcelessModules = buildSourcelessModules;
        }
        
        /**
         * Adds a Car Workspace Spec file to the Car.
         * @param specName the name of the spec file.
         * @param modulesInSpec the names of the modules to be included in the spec file.
         */
        public void addCarSpec(String specName, ModuleName[] modulesInSpec) {
            
            if (specName == null || modulesInSpec == null) {
                throw new NullPointerException();
            }
            
            carSpecs.put(specName, new CarSpec.ModuleList(specName, modulesInSpec));
        }
        
        /**
         * Adds a preexisting Car Workspace Spec file from the file system to the Car.
         * @param specName the name of the spec file when added to the Car.
         * @param specFile the preexisting spec file.
         */
        public void addFileBasedCarSpec(String specName, File specFile) {
            
            if (specName == null || specFile == null) {
                throw new NullPointerException();
            }
            
            carSpecs.put(specName, new CarSpec.FileBased(specName, specFile));
        }
        
        /**
         * Adds an additional file to be included with the Car.
         * @param relativePath the relative path of the file.
         * @param contents the contents of the file.
         */
        public void addAdditionalFile(String relativePath, byte[] contents) {
            additionalFiles.put(relativePath, contents);
        }
        
        /**
         * Returns the import declaration corresponding to the Car being built, if the Car were to be
         * found in the StandardVault.
         * 
         * @param carName the name of the Car.
         */
        private String getWorkspaceDeclarationImportLineForStandardVaultCar(String carName) {
            return "import car StandardVault " + carName + " " + mainSpecName;
        }
    }
    
    /**
     * This abstract class represents the contents of a Car Workspace Spec file.
     *
     * @author Joseph Wong
     */
    private static abstract class CarSpec {
        
        /**
         * The name of the spec file.
         */
        private final String specName;
        
        /**
         * This class represents the contents of a spec file as an array of module names to be included in the file.
         *
         * @author Joseph Wong
         */
        private static final class ModuleList extends CarSpec {
            
            /**
             * The names of the modules to be included in the spec file.
             */
            private final ModuleName[] modulesInSpec;

            /**
             * Constructs a CarSpec.ModuleList instance.
             * @param specName the name of the spec file.
             * @param modulesInSpec the names of the modules to be included in the spec file.
             */
            private ModuleList(String specName, ModuleName[] modulesInSpec) {
                super(specName);
                
                if (modulesInSpec == null) {
                    throw new NullPointerException();
                }
                
                this.modulesInSpec = modulesInSpec.clone();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            void writeTo(OutputStream outputStream) throws IOException {
                
                String spec = getWorkspaceSpec(getName(), modulesInSpec);
                
                writeStringToStream(outputStream, spec);
            }

            /**
             * Returns the string contents of the spec file given the file name and the array of module names.
             * @param specName the name of the spec file.
             * @param moduleNames the array of module names to be included in the file.
             * @return the string contents of the spec file.
             */
            private static String getWorkspaceSpec(String specName, ModuleName[] moduleNames) {
                StringBuilder builder = new StringBuilder();
                
                builder.append("// ").append(specName).append("\n// Created at " + new Date() + "\n\n");
                
                for (final ModuleName element : moduleNames) {
                    builder.append(element).append("\n");
                }
                
                return builder.toString();
            }
        }
        
        /**
         * This class represents the contents of a spec file as a preexisting file on the file system.
         *
         * @author Joseph Wong
         */
        private static final class FileBased extends CarSpec {
            
            /**
             * The preexisting spec file on the file system.
             */
            private final File specFile;
            
            /**
             * Constructs an instance of CarSpec.FileBased.
             * @param specName the name of the spec file.
             * @param specFile the preexisting spec file on the file system.
             */
            private FileBased(String specName, File specFile) {
                super(specName);
                
                if (specFile == null) {
                    throw new NullPointerException();
                }
                
                this.specFile = specFile;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            void writeTo(OutputStream outputStream) throws IOException {
                FileInputStream fis = new FileInputStream(specFile);
                try {
                    FileSystemResourceHelper.transferData(fis, outputStream);
                } finally {
                    fis.close();
                }
            }
        }
        
        /**
         * Private constructor for subclasses.
         * @param specName the name of the spec file.
         */
        private CarSpec(String specName) {
            
            if (specName == null) {
                throw new NullPointerException();
            }
            
            this.specName = specName;
        }
        
        /**
         * @return the name of the spec file.
         */
        String getName() {
            return specName;
        }
        
        /**
         * Writes the string contents of the spec file to the specified output stream.
         * @param outputStream the stream to be written to.
         * @throws IOException
         */
        abstract void writeTo(OutputStream outputStream) throws IOException;
    }
    
    /**
     * This interface specifies a progress monitor to be used with a {@link CarBuilder} for
     * monitoring the progress of the Car building operation.
     *
     * @author Joseph Wong
     */
    public static interface Monitor {
        
        /**
         * Returns whether the operation has been canceled by the user. This method is
         * meant to be queried by the {@link CarBuilder} in determining whether to abort the operation.
         * 
         * @return true if the operation has been canceled, false otherwise.
         */
        public boolean isCanceled();
        
        /**
         * Displays the specified messages to the user (if appropriate).
         * @param messages the messages to be shown.
         */
        public void showMessages(String[] messages);
        
        /**
         * Invoked when the CarBuilder has started its operation.
         * @param nCars the number of Cars to be built.
         * @param nTotalModules the total number of modules across all Cars.
         */
        public void operationStarted(int nCars, int nTotalModules);
        
        /**
         * Invoked when the CarBuilder has started processing the modules and adding them to a Car.
         * @param carName the name of the Car being built.
         * @param nModules the number of modules that will be added to the Car.
         */
        public void carBuildingStarted(String carName, int nModules);
        
        /**
         * Invoked when the CarBuilder has started processing the named module and adding it to the current Car.
         * @param carName the name of the Car being built.
         * @param moduleName the name of the module being processed by the CarBuilder.
         */
        public void processingModule(String carName, ModuleName moduleName);
        
        /**
         * Invoked when the CarBuilder has finished building a Car.
         * @param carName the name of the Car just built.
         */
        public void carBuildingDone(String carName);
        
        /**
         * Invoked when the CarBuilder has finished its operation.
         */
        public void operationDone();
    }
    
    /**
     * This is a default implementation of the Monitor interface which does nothing, and which always returns true
     * for isCanceled().
     * 
     * This class is meant to be used through its singleton instance.
     *
     * @author Joseph Wong
     */
    public static final class DefaultMonitor implements Monitor {
        /** Private constructor. */
        private DefaultMonitor() {}
        
        // default implementation
        public boolean isCanceled() {
            return false;
        }
        public void showMessages(String[] messages) {}
        public void operationStarted(int nCars, int nTotalModules) {}
        public void carBuildingStarted(String carName, int nModules) {}
        public void processingModule(String carName, ModuleName moduleName) {}
        public void carBuildingDone(String carName) {}
        public void operationDone() {}
        
        /** Singleton instance. */
        public static final DefaultMonitor INSTANCE = new DefaultMonitor();
    }
    
    /**
     * Encapsulates the options for the Car builder. Note that this is different from
     * the per-Car configuration encapsulated by the {@link CarBuilder.Configuration} class.
     *
     * @author Joseph Wong
     */
    public static final class BuilderOptions {
        
        /**
         * Specifies whether modules already in Cars should be skipped over.
         */
        private final boolean shouldSkipModulesAlreadyInCars;
        
        /**
         * Specifies whether a corresponding workspace declaration should be generated also.
         */
        private final boolean shouldGenerateCorrespWorkspaceDecl;
        
        /**
         * Specifies whether the generated workspace declaration should end simply with '.cws' instead of '.car.cws'.
         */
        private final boolean noCarSuffixInWorkspaceDeclName;
        
        /**
         * Specifies whether to build the Car with sourceless modules (i.e. the CAL files will be empty stubs).
         */
        private final boolean buildSourcelessModules;
        
        /**
         * Specifies the names of Cars to be explicitly excluded.
         */
        private final Set<String> carsToExclude;
        
        /**
         * Specifies whether to generate Cars with the suffix .car.jar.
         */
        private final boolean shouldGenerateCarJarSuffix;

        /**
         * Constructs an instance of BuilderOptions.
         * @param shouldSkipModulesAlreadyInCars whether modules already in Cars should be skipped over.
         * @param shouldGenerateCorrespWorkspaceDecl whether a corresponding workspace declaration should be generated also.
         * @param noCarSuffixInWorkspaceDeclName whether the generated workspace declaration should end simply with '.cws' instead of '.car.cws'.
         * @param buildSourcelessModules whether to build the Car with sourceless modules (i.e. the CAL files will be empty stubs).
         * @param carsToExclude the names of Cars to be explicitly excluded.
         * @param shouldGenerateCarJarSuffix whether to generate Cars with the suffix .car.jar.
         */
        public BuilderOptions(
            boolean shouldSkipModulesAlreadyInCars,
            boolean shouldGenerateCorrespWorkspaceDecl,
            boolean noCarSuffixInWorkspaceDeclName,
            boolean buildSourcelessModules,
            Collection<String> carsToExclude,
            boolean shouldGenerateCarJarSuffix) {
            
            this.shouldSkipModulesAlreadyInCars = shouldSkipModulesAlreadyInCars;
            this.shouldGenerateCorrespWorkspaceDecl = shouldGenerateCorrespWorkspaceDecl;
            this.noCarSuffixInWorkspaceDeclName = noCarSuffixInWorkspaceDeclName;
            this.buildSourcelessModules = buildSourcelessModules;
            this.carsToExclude = Collections.unmodifiableSet(new HashSet<String>(carsToExclude));
            this.shouldGenerateCarJarSuffix = shouldGenerateCarJarSuffix;
        }
    }
    
    /**
     * Encapsulates the configuration for building a Car and saving it to the file system,
     * including in particular the {@link CarBuilder.Configuration} for the Car.
     *
     * @author Joseph Wong
     */
    public static final class FileOutputConfiguration {
        
        /**
         * The basic Car building configuration.
         */
        private final Configuration config;
        
        /**
         * The location of the output Car file.
         */
        private final File carFile;
        
        /**
         * The name of the output Car. Can be different from the name of the Car file, e.g. when the Car file ends with a
         * .car.jar suffix.
         */
        private final String carName;
        
        /**
         * The location of the corresponding workspace declaration file. Can be null if none is to be generated.
         */
        private final File cwsFile;
        
        /**
         * A Collection containing the names of additional declarations to be added as imports to the workspace declaration,
         * if it is to be generated.
         */
        private Collection<String> additionalImportsForWorkspaceDecl;

        /**
         * Constructs an instance of FileOutputConfiguration.
         * @param config the basic Car building configuration.
         * @param carFile the location of the output Car file.
         * @param carName the name of the output Car.
         * @param cwsFile the corresponding workspace declaration file. Can be null if none is to be generated.
         */
        public FileOutputConfiguration(Configuration config, File carFile, String carName, File cwsFile) {
            
            if (config == null || carFile == null || carName == null) {
                throw new NullPointerException();
            }
            
            this.carFile = carFile;
            this.carName = carName;
            this.config = config;
            this.cwsFile = cwsFile; // can be null
            this.additionalImportsForWorkspaceDecl = Collections.emptySet();
        }

        /**
         * @return the basic Car building configuration.
         */
        public Configuration getConfig() {
            return config;
        }

        /**
         * @return the location of the output Car file.
         */
        public File getCarFile() {
            return carFile;
        }
        
        /**
         * @return the name of the output Car.
         */
        public String getCarName() {
            return carName;
        }

        /**
         * @return the location of the corresponding workspace declaration file, or null if none is to be generated.
         */
        public File getWorkspaceDeclarationFile() {
            return cwsFile;
        }
        
        /**
         * Sets the Collection containing the names of additional declarations to be added as imports to the workspace declaration,
         * if it is to be generated.
         * @param additionalImportsForWorkspaceDecl
         */
        private void setAdditionalImportsForWorkspaceDecl(Collection<String> additionalImportsForWorkspaceDecl) {
            this.additionalImportsForWorkspaceDecl = Collections.unmodifiableCollection(additionalImportsForWorkspaceDecl);
        }
        
        /**
         * @return the Collection containing the names of additional declarations to be added as imports to the workspace declaration,
         *         if it is to be generated.
         */
        public Collection<String> getAdditionalImportsForWorkspaceDecl() {
            return additionalImportsForWorkspaceDecl;
        }
        
        /**
         * @return whether to generate Cars with the suffix .car.jar.
         */
        public boolean shouldGenerateCarJarSuffix() {
            return carFile.getName().endsWith(DOT_JAR);
        }
    }
    
    /**
     * Encapsulates a set of {@link CarBuilder.Configuration}s that are constructed for building one
     * Car per workspace declaration.
     *
     * @author Joseph Wong
     */
    public static final class ConfigurationsForOneCarPerWorkspaceDeclaration {
        
        /**
         * The map (workspace decl name -> {@link CarBuilder.FileOutputConfiguration}) that contains all the configurations.
         */
        private final Map<String, FileOutputConfiguration> workspaceDeclToConfigMap;
        
        /**
         * The map (module name -> LinkedHashSet of (Pair of (workspace decl name, VaultElementInfo))) that contains
         * information about conflicting entries in workspace declaration files:
         * 
         * A module may appear in more than one declaration, and these declaration entries may be for different vaults/revisions.
         * Only one declaration entry correspond to the resources in the workspace. The remainder are conflicts. So we
         * store information about each conflicting entry (a VaultElementInfo) and its corresponding workspace declaration file name.
         */
        private final Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToConflictingWorkspaceEntriesMap;
        
        /**
         * A map containing the names of the modules included by the workspace declaration and
         * their associated entries in all the workspace declarations in the tree.
         */
        private final Map<String, LinkedHashSet<String>> importsMap;
        
        /**
         * Private constructor. Intended only to be called by {@link CarBuilder#makeConfigurationsForOneCarPerWorkspaceDeclaration}.
         * @param workspaceDeclToConfigMap
         * @param moduleNameToConflictingWorkspaceEntriesMap
         */
        private ConfigurationsForOneCarPerWorkspaceDeclaration(Map<String, FileOutputConfiguration> workspaceDeclToConfigMap, Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToConflictingWorkspaceEntriesMap, Map<String, LinkedHashSet<String>> importsMap) {
            if (moduleNameToConflictingWorkspaceEntriesMap == null || workspaceDeclToConfigMap == null || importsMap == null) {
                throw new NullPointerException();
            }
            
            // wrap the maps with unmodifiable wrappers since they're returned directly by accessors
            this.moduleNameToConflictingWorkspaceEntriesMap = Collections.unmodifiableMap(moduleNameToConflictingWorkspaceEntriesMap);
            this.workspaceDeclToConfigMap = Collections.unmodifiableMap(workspaceDeclToConfigMap);
            this.importsMap = Collections.unmodifiableMap(importsMap);
        }
        
        /**
         * @return the map (workspace decl name -> {@link CarBuilder.FileOutputConfiguration}) that contains all the configurations.
         */
        public Map<String, FileOutputConfiguration> getWorkspaceDeclToConfigMap() {
            return workspaceDeclToConfigMap;
        }
        
        /**
         * @return the map (module name -> LinkedHashSet of (Pair of (workspace decl name, VaultElementInfo))) that contains
         * information about conflicting entries in workspace declaration files.
         */
        public Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> getModuleNameToConflictingWorkspaceEntriesMap() {
            return moduleNameToConflictingWorkspaceEntriesMap;
        }
        
        /**
         * @return a map containing the names of the modules included by the workspace declaration and
         * their associated entries in all the workspace declarations in the tree.
         */
        public Map<String, LinkedHashSet<String>> getImportsMap() {
            return importsMap;
        }
    }

    /** Private constructor. This class is not meant to be instantiated. */
    private CarBuilder() {}
    
    /**
     * Constructs a set of {@link CarBuilder.FileOutputConfiguration}s that are constructed for building one
     * Car per workspace declaration.
     * 
     * @param workspaceManager the workspace manager that will provide the resources to be added to the Cars.
     * @param monitor the {@link CarBuilder.Monitor} to be used.
     * @param outputDirectory the output directory to which the Car files will be written.
     * @param options configuration options for the builder.
     * @return a {@link CarBuilder.ConfigurationsForOneCarPerWorkspaceDeclaration} encapsulating the configurations and information on
     *         any identified conflicts.
     */
    public static ConfigurationsForOneCarPerWorkspaceDeclaration makeConfigurationsForOneCarPerWorkspaceDeclaration(WorkspaceManager workspaceManager, Monitor monitor, File outputDirectory, BuilderOptions options) {
        WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider = workspaceManager.getInitialWorkspaceDeclarationProvider();
        CALWorkspace workspace = workspaceManager.getWorkspace();
        Status status = new Status("Reading workspace declarations");
        
        // fetch information about all imported workspace declaration files using the WorkspaceLoader
        Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToWorkspaceDeclInfoMap = WorkspaceLoader.getStoredModuleNameToWorkspaceDeclNamesMap(workspaceDeclarationProvider, workspace, status);
        
        Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToConflictingWorkspaceEntriesMap = new HashMap<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>>();
        
        ////
        /// Invert the moduleNameToWorkspaceDeclNamesMap to (workspace decl name -> Set of module names), and build up
        /// the map containing conflict information
        //
        Map<String, Set<ModuleName>> workspaceDeclToModuleNamesMap = new HashMap<String, Set<ModuleName>>();
        
        for (final Map.Entry<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> entry : moduleNameToWorkspaceDeclInfoMap.entrySet()) {
            
            ModuleName moduleName = entry.getKey();
            LinkedHashSet<Pair<String, VaultElementInfo>> workspaceDeclInfo = entry.getValue();
            
            VaultElementInfo actualVaultInfo = null;
            for (final Pair<String, VaultElementInfo> workspaceDeclNameAndVaultElementInfoPair : workspaceDeclInfo) {
                
                String workspaceDeclName = workspaceDeclNameAndVaultElementInfoPair.fst();
                VaultElementInfo vaultInfo = workspaceDeclNameAndVaultElementInfoPair.snd();
                
                // we know that the workspaceDeclInfo is ordered in such a way that the first entry
                // correspond to the vault info that is actually used by the workspace, and that the subsequent entries
                // correspond to "shadowed" declaration entries that may in fact conflict with the "actualVaultInfo"
                
                // detect if there were conflicting entries for the same module in different workspace declaration files
                if (actualVaultInfo == null) {
                    actualVaultInfo = vaultInfo;
                    
                    // if the module comes from a Car, then skip it if shouldSkipModulesAlreadyInCars is specified
                    if (options.shouldSkipModulesAlreadyInCars && isVaultElementFromCar(actualVaultInfo)) {
                        
                        break; // break out of the inner loop
                    }
                    
                } else {
                    if (!actualVaultInfo.equals(vaultInfo)) {
                        // insert the conflicting vault info into the conflict map under the module name
                        LinkedHashSet<Pair<String, VaultElementInfo>> conflictingWorkspaceEntries = moduleNameToConflictingWorkspaceEntriesMap.get(moduleName);
                        
                        if (conflictingWorkspaceEntries == null) {
                            conflictingWorkspaceEntries = new LinkedHashSet<Pair<String, VaultElementInfo>>();
                            moduleNameToConflictingWorkspaceEntriesMap.put(moduleName, conflictingWorkspaceEntries);
                        }
                        conflictingWorkspaceEntries.add(new Pair<String, VaultElementInfo>(workspaceDeclName, vaultInfo));
                    }
                }
                
                Set<ModuleName> modulesInWorkspaceDecl = workspaceDeclToModuleNamesMap.get(workspaceDeclName);
                
                if (modulesInWorkspaceDecl == null) {
                    modulesInWorkspaceDecl = new HashSet<ModuleName>();
                    workspaceDeclToModuleNamesMap.put(workspaceDeclName, modulesInWorkspaceDecl);
                }
                modulesInWorkspaceDecl.add(moduleName);
            }
        }
        
        // fetch information about the tree of imports using the WorkspaceLoader
        Map<String, LinkedHashSet<String>> importsMap = WorkspaceLoader.getWorkspaceDeclarationImportsMap(workspaceDeclarationProvider, workspace, status);
        
        ////
        /// Build the result map (workspace decl name -> Configuration) of the Configuration objects
        ///
        /// NOTE: We produce empty Cars too (so that their cws files also get generated, in case any client code refers to them explicitly)
        //
        Map<String, FileOutputConfiguration> workspaceDeclToConfigMap = new HashMap<String, FileOutputConfiguration>();
        
        // We first gather the names of the all the workspace declarations involved.
        Set<String> allWorkspaceDeclsInvolved = importsMap.keySet();
        
        // For each workspace declaration, we construct a Configuration object and add it to the result map
        for (final String workspaceDeclName : allWorkspaceDeclsInvolved) {
            Set<ModuleName> modulesInWorkspaceDecl = workspaceDeclToModuleNamesMap.get(workspaceDeclName);

            final int nModules;
            
            if (modulesInWorkspaceDecl == null) {
                modulesInWorkspaceDecl = Collections.emptySet();
                nModules = 0;
            } else {
                nModules = modulesInWorkspaceDecl.size();
            }
            
            Configuration config = new Configuration(workspaceManager, modulesInWorkspaceDecl.toArray(new ModuleName[nModules]));
            config.setMonitor(monitor);
            config.setBuildSourcelessModule(options.buildSourcelessModules);
            
            String carName = makeCarNameFromSourceWorkspaceDeclName(workspaceDeclName);
            File carFile = makeOutputCarFile(outputDirectory, carName, options);
            
            File cwsFile;
            if (options.shouldGenerateCorrespWorkspaceDecl) {
                cwsFile = makeOutputCorrespWorkspaceDeclFile(outputDirectory, carName, options.noCarSuffixInWorkspaceDeclName);
            } else {
                cwsFile = null;
            }
            
            FileOutputConfiguration fileOutputConfig = new FileOutputConfiguration(config, carFile, carName, cwsFile);
            
            workspaceDeclToConfigMap.put(workspaceDeclName, fileOutputConfig);
        }
        
        ConfigurationsForOneCarPerWorkspaceDeclaration configs = new ConfigurationsForOneCarPerWorkspaceDeclaration(workspaceDeclToConfigMap, moduleNameToConflictingWorkspaceEntriesMap, importsMap);
        
        addImportsToConfigurationsForOneCarPerWorkspaceDeclaration(configs);
        
        return configs;
    }

    /**
     * Modify the given configurations with additional import declarations for the workspace declaration files that are to be generated.
     * @param configs the configurations to be modified.
     */
    private static void addImportsToConfigurationsForOneCarPerWorkspaceDeclaration(ConfigurationsForOneCarPerWorkspaceDeclaration configs) {
        
        final Map<String, FileOutputConfiguration> workspaceDeclToConfigMap = configs.workspaceDeclToConfigMap;
        
        for (final Map.Entry<String,FileOutputConfiguration> entry : workspaceDeclToConfigMap.entrySet()) {
            
            String workspaceDeclName = entry.getKey();
            FileOutputConfiguration fileOutputConfig = entry.getValue();
            
            LinkedHashSet<String> imports = getListOfImports(configs, workspaceDeclName);
            
            fileOutputConfig.setAdditionalImportsForWorkspaceDecl(imports);
        }
    }

    /**
     * Returns whether the specified vault element info refers to a vault element stored in a Car.
     * @param vaultInfo the vault element info.
     * @return true if the vault element is stored in a Car.
     */
    private static boolean isVaultElementFromCar(VaultElementInfo vaultInfo) {
        return vaultInfo instanceof VaultElementInfo.Nested &&
            vaultInfo.getVaultDescriptor().equals(CarVault.getVaultClassDescriptor());
    }
    
    /**
     * Builds one Car file per workspace declaration file that constitutes the initial declaration of the CAL workspace.
     * @param workspaceManager the workspace manager that will provide the resources to be added to the Cars.
     * @param monitor the {@link CarBuilder.Monitor} to be used.
     * @param outputDirectory the output directory to which the Car files will be written.
     * @param options configuration options for the builder.
     * @return an array of the names of the Car files built.
     * @throws FileNotFoundException if the output file is not a valid location.
     * @throws IOException
     */
    public static String[] buildOneCarPerWorkspaceDeclaration(WorkspaceManager workspaceManager, Monitor monitor, File outputDirectory, BuilderOptions options) throws FileNotFoundException, IOException {
        boolean directoryExists = FileSystemHelper.ensureDirectoryExists(outputDirectory);
        if (!directoryExists) {
            throw new FileNotFoundException("Output directory does not exist: " + outputDirectory);
        }
        
        ConfigurationsForOneCarPerWorkspaceDeclaration configs = makeConfigurationsForOneCarPerWorkspaceDeclaration(workspaceManager, monitor, outputDirectory, options);
        final Map<String, FileOutputConfiguration> workspaceDeclToConfigMap = configs.workspaceDeclToConfigMap;
        final Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToConflictingWorkspaceEntriesMap = configs.moduleNameToConflictingWorkspaceEntriesMap;
        
        ////
        /// If there are conflicting workspace declaration entries for any module, display a warning
        //
        if (!moduleNameToConflictingWorkspaceEntriesMap.isEmpty()) {
            showMessagesAboutConflictingWorkspaceEntries(monitor, moduleNameToConflictingWorkspaceEntriesMap);
        }
        
        ////
        /// Now proceed to build the Cars, one at a time using the supplied configurations
        //
        
        // identify the total number of Cars to be built, as well as the total number of modules across all Cars
        int nCars = workspaceDeclToConfigMap.size();
        
        int nTotalModules = 0;
        for (final FileOutputConfiguration fileOutputConfig : workspaceDeclToConfigMap.values()) {
            nTotalModules += fileOutputConfig.getConfig().moduleNames.length;
        }
        
        // run the build helper, one per Car
        monitor.operationStarted(nCars, nTotalModules);
        try {
            List<String> carsBuilt = new ArrayList<String>();
            
            for (final Map.Entry<String, FileOutputConfiguration> entry : workspaceDeclToConfigMap.entrySet()) {
                
                // break out of the loop and quit if the user canceled the operation
                if (monitor.isCanceled()) {
                    break;
                }
                
                FileOutputConfiguration fileOutputConfig = entry.getValue();
                
                String carName = fileOutputConfig.getCarName();
                if (!options.carsToExclude.contains(carName)) {
                    boolean notCanceled = buildCarHelper(fileOutputConfig);
                    if (notCanceled) {
                        carsBuilt.add(carName);
                    }
                    
                } else {
                    // skip the module which is specified to be excluded
                    monitor.carBuildingStarted(carName, 0);
                    monitor.carBuildingDone(carName);
                }
            }
            
            return carsBuilt.toArray(new String[carsBuilt.size()]);
            
        } finally {
            monitor.operationDone();
        }
    }

    /**
     * Show messages via the given monitor about conflicting workspace entries.
     * @param monitor the monitor through which the messages will be shown.
     * @param moduleNameToConflictingWorkspaceEntriesMap
     * 
     * a map (module name -> LinkedHashSet of (Pair of (workspace decl name, VaultElementInfo))) that contains
     * information about conflicting entries in workspace declaration files:
     * 
     * A module may appear in more than one declaration, and these declaration entries may be for different vaults/revisions.
     * Only one declaration entry correspond to the resources in the workspace. The remainder are conflicts. So we
     * store information about each conflicting entry (a VaultElementInfo) and its corresponding workspace declaration file name.
     */
    private static void showMessagesAboutConflictingWorkspaceEntries(Monitor monitor, Map<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> moduleNameToConflictingWorkspaceEntriesMap) {
        List<String> messages = new ArrayList<String>();
        
        for (final Map.Entry<ModuleName, LinkedHashSet<Pair<String, VaultElementInfo>>> entry : moduleNameToConflictingWorkspaceEntriesMap.entrySet()) {
            
            ModuleName moduleName = entry.getKey();
            LinkedHashSet<Pair<String, VaultElementInfo>> conflictingWorkspaceEntries = entry.getValue();
            
            messages.add(MESSAGES.getString("moduleHasVaultInfoConflictsHeader", moduleName));
            
            for (final Pair<String, VaultElementInfo> workspaceDeclNameAndVaultElementPair : conflictingWorkspaceEntries) {
                
                String workspaceDeclName = workspaceDeclNameAndVaultElementPair.fst();
                VaultElementInfo vaultInfo = workspaceDeclNameAndVaultElementPair.snd();
                
                StringBuilder vaultInfoString = new StringBuilder(vaultInfo.getVaultDescriptor());
                
                String locationString = vaultInfo.getLocationString();
                if (locationString != null) {
                    vaultInfoString.append(' ').append(locationString);
                }
                
                vaultInfoString.append(' ').append(MESSAGES.getString("revisionNumber", Integer.valueOf(vaultInfo.getRevision())));
                
                messages.add(MESSAGES.getString("conflictingVaultInfo", workspaceDeclName, vaultInfoString.toString()));
            }
        }
        
        monitor.showMessages(messages.toArray(new String[0]));
    }

    /**
     * Calculates the workspace declarations that should be imported by the given workspace declaration.
     * Note that we elide any declaration that does not have a corresponding Car generated.
     * 
     * @param configs the set of configurations for the various Cars being generated.
     * @param workspaceDeclName the name of the workspace declaration to look up.
     * @return the LinkedHashSet of declarations to be imported.
     */
    private static LinkedHashSet<String> getListOfImports(ConfigurationsForOneCarPerWorkspaceDeclaration configs, String workspaceDeclName) {
        LinkedHashSet<String> imports = new LinkedHashSet<String>();
        Set<String> alreadyVisitedDecls = new HashSet<String>();
        addToListOfImports(imports, alreadyVisitedDecls, configs, workspaceDeclName);
        return imports;
    }

    /**
     * Calculates the workspace declarations that should be imported by the given workspace declaration.
     * Note that we elide any declaration that does not have a corresponding Car generated.
     * 
     * @param imports the LinkedHashSet to be added to.
     * @param alreadyVisitedDecls the workspace declarations that have already been visited.
     * @param configs the set of configurations for the various Cars being generated.
     * @param workspaceDeclName the name of the workspace declaration to look up.
     */
    private static void addToListOfImports(Set<String> imports, Set<String> alreadyVisitedDecls, ConfigurationsForOneCarPerWorkspaceDeclaration configs, String workspaceDeclName) {
        
        if (alreadyVisitedDecls.contains(workspaceDeclName)) {
            return;
        }
        
        alreadyVisitedDecls.add(workspaceDeclName);
        
        LinkedHashSet<String> origImports = configs.importsMap.get(workspaceDeclName);
        if (origImports == null) {
            return;
        }
        
        for (final String importedDecl : origImports) {
            
            FileOutputConfiguration fileOutputConfigForImportedDecl = configs.workspaceDeclToConfigMap.get(importedDecl);
            
            if (fileOutputConfigForImportedDecl != null) {
                
                File cwsFile = fileOutputConfigForImportedDecl.cwsFile;
                if (cwsFile != null) {
                    String correspWorkspaceDecl = cwsFile.getName();
                    
                    if (correspWorkspaceDecl != null) {
                        imports.add(correspWorkspaceDecl);
                    }
                }
                
            } else {
                // recursive call to extract the imports of this declaration which does not have a corresponding
                // Car being generated.
                addToListOfImports(imports, alreadyVisitedDecls, configs, importedDecl);
            }
        }
    }

    /**
     * Strips the trailing '.cws' from the given string.
     * @param workspaceDeclName the workspace declaration name.
     * @return the given string with the trailing '.cws' stripped.
     */
    private static String stripTrailingDotCWS(String workspaceDeclName) {
        return workspaceDeclName.replaceAll("\\" + DOT_CWS + "$", "");
    }

    /**
     * Strips the trailing '.car' from the given string.
     * @param carName the Car name.
     * @return the given string with the trailing '.car' stripped.
     */
    private static String stripTrailingDotCAR(String carName) {
        return carName.replaceAll("\\" + DOT_CAR + "$", "");
    }
    
    /**
     * Builds a Car according to the given configuration and writes it out to the specified output file.
     * @param config the configuration for building the Car.
     * @param outputDir the output directory to be written to.
     * @param options configuration options for the Car builder.
     * @throws FileNotFoundException if the output file is not a valid location.
     * @return true if, at the end, the operation is not canceled by the user.
     * @throws IOException
     */
    public static boolean buildCar(Configuration config, File outputDir, String carName, BuilderOptions options) throws FileNotFoundException, IOException {
        File cwsFile;
        
        if (options.shouldGenerateCorrespWorkspaceDecl) {
            cwsFile = makeOutputCorrespWorkspaceDeclFile(outputDir, carName, options.noCarSuffixInWorkspaceDeclName);
        } else {
            cwsFile = null;
        }
        
        FileOutputConfiguration fileOutputConfig = new FileOutputConfiguration(config, makeOutputCarFile(outputDir, carName, options), carName, cwsFile);
        
        return buildCar(fileOutputConfig);
    }
    
    /**
     * Constructs a Car name from a source workspace declaration name. For example
     * everything.default.cws -> everything.default.car
     * 
     * @param sourceWorkspaceDeclName the source workspace declaration name.
     * @return the corresponding Car name.
     */
    public static String makeCarNameFromSourceWorkspaceDeclName(String sourceWorkspaceDeclName) {
        return stripTrailingDotCWS(sourceWorkspaceDeclName) + DOT_CAR;
    }
    
    /**
     * Constructs a {@link File} for the named Car to be generated.
     * @param rootOutputDir the root output directory.
     * @param carName the name of the Car.
     * @param options the associated BuilderOptions object for this Car building operation.
     * @return the corresponding File object.
     */
    private static File makeOutputCarFile(File rootOutputDir, String carName, BuilderOptions options) {
        if (options.shouldGenerateCarJarSuffix) {
            return new File(rootOutputDir, carName + DOT_JAR);
        } else {
            File carFolder = new File(rootOutputDir, CAR_FOLDER_NAME);
            return new File(carFolder, carName);
        }
    }
    
    /**
     * Constructs a {@link File} for the corresponding workspace declaration to be generated for the given Car.
     * @param rootOutputDir the root output directory.
     * @param carName the name of the Car.
     * @param noCarSuffixInWorkspaceDeclName whether the generated workspace declaration should end simply with '.cws' instead of '.car.cws'.
     * @return the corresponding File object.
     */
    private static File makeOutputCorrespWorkspaceDeclFile(File rootOutputDir, String carName, boolean noCarSuffixInWorkspaceDeclName) {
        String cwsName = makeOutputCorrespWorkspaceDeclName(carName, noCarSuffixInWorkspaceDeclName);
        
        File workspaceDeclFolder = new File(rootOutputDir, WORKSPACE_DECLARATIONS_FOLDER_NAME);
        return new File(workspaceDeclFolder, cwsName);
    }

    /**
     * Constructs a name for the corresponding workspace declaration to be generated for the given Car. For example
     * everything.default.car -> everything.default.car.cws (if noCarSuffixInWorkspaceDeclName is false), OR
     * everything.default.car -> everything.default.cws     (if noCarSuffixInWorkspaceDeclName is true)
     * 
     * @param carName the name of the Car.
     * @param noCarSuffixInWorkspaceDeclName whether the generated workspace declaration should end simply with '.cws' instead of '.car.cws'.
     * @return the name of the workspace declaration file.
     */
    public static String makeOutputCorrespWorkspaceDeclName(String carName, boolean noCarSuffixInWorkspaceDeclName) {
        if (noCarSuffixInWorkspaceDeclName) {
            return stripTrailingDotCAR(carName) + DOT_CWS;
        } else {
            return carName + DOT_CWS;
        }
    }
    
    /**
     * Builds a Car according to the given configuration and writes it out to the specified output file.
     * @param fileOutputConfig the configuration for building the Car.
     * @throws FileNotFoundException if the output file is not a valid location.
     * @return true if, at the end, the operation is not canceled by the user.
     * @throws IOException
     */
    public static boolean buildCar(FileOutputConfiguration fileOutputConfig) throws FileNotFoundException, IOException {
        final Configuration config = fileOutputConfig.getConfig();
        
        config.monitor.operationStarted(1, config.moduleNames.length);
        try {
            return buildCarHelper(fileOutputConfig);
        } finally {
            config.monitor.operationDone();
        }
    }

    /**
     * Builds a Car according to the given configuration and writes it out to the specified output file.
     * @param fileOutputConfig the configuration for building the Car.
     * @throws FileNotFoundException if the output file is not a valid location.
     * @return true if, at the end, the operation is not canceled by the user.
     * @throws IOException
     */
    private static boolean buildCarHelper(FileOutputConfiguration fileOutputConfig) throws FileNotFoundException, IOException {
        final Configuration config = fileOutputConfig.getConfig();
        final File outputFile = fileOutputConfig.getCarFile();
        final String carName = fileOutputConfig.getCarName();
        final boolean shouldGenerateCarJarSuffix = fileOutputConfig.shouldGenerateCarJarSuffix();
        
        // generate the corresponding cws file if requested
        final File cwsFile = fileOutputConfig.getWorkspaceDeclarationFile();
        if (cwsFile != null) {
            final String cwsName = cwsFile.getName();
            final Collection<String> additionalImportsForWorkspaceDecl = fileOutputConfig.getAdditionalImportsForWorkspaceDecl();
            
            if (shouldGenerateCarJarSuffix) {
                StringWriter writer = new StringWriter();
                
                writeCorrespondingWorkspaceDeclaration(config, carName, additionalImportsForWorkspaceDecl, writer, cwsName);
                
                byte[] cwsContents = TextEncodingUtilities.getUTF8Bytes(writer.toString());
                
                config.addAdditionalFile(WorkspaceDeclarationNullaryStore.WORKSPACE_DECLARATION_IN_JAR_BASE_FOLDER.extendFile(cwsName).getPathStringMinusSlash(), cwsContents);
                
            } else {
                FileSystemHelper.ensureDirectoryExists(cwsFile.getParentFile());
                Writer writer = new FileWriter(cwsFile);
                
                writeCorrespondingWorkspaceDeclaration(config, carName, additionalImportsForWorkspaceDecl, writer, cwsName);
            }
        }
        
        return buildCarHelper(config, carName, outputFile);
    }
    
    /**
     * Writes the corresponding workspace declaration that references the Car being built to the specified writer.
     * @param config the Car's configuration.
     * @param carName the name of the Car.
     * @param additionalImportsForWorkspaceDecl additional imports to be added to the workspace declaration.
     * @param writer the Writer to write to.
     * @param cwsName the name of the workspace declaration file to generate.
     * @throws IOException
     */
    private static void writeCorrespondingWorkspaceDeclaration(Configuration config, String carName, Collection<String> additionalImportsForWorkspaceDecl, Writer writer, String cwsName) throws IOException {
        try {
            PrintWriter printWriter = new PrintWriter(writer);
            
            String comment = MESSAGES.getString("generatedCWSFileComment", cwsName, new Date());
            String[] commentLines = comment.split("\n");
            
            for (final String element : commentLines) {
                printWriter.println(element);
            }
            
            printWriter.println();
            
            printWriter.println(config.getWorkspaceDeclarationImportLineForStandardVaultCar(carName));
            
            for (final String additionalImport : additionalImportsForWorkspaceDecl) {
                printWriter.println("import StandardVault " + additionalImport);
            }
            
            printWriter.flush();
        } finally {
            writer.close();
        }
    }
    
    ////===========================================================================================
    /// Fundamental routines for building a single Car.
    //

    /**
     * Builds a Car according to the given configuration and writes it out to the specified output file.
     * @param config the configuration for building the Car.
     * @param carName the canonical name of the Car to be built.
     * @param outputFile the output file to be written to.
     * @throws FileNotFoundException if the output file is not a valid location.
     * @return true if, at the end, the operation is not canceled by the user.
     * @throws IOException
     */
    public static boolean buildCar(Configuration config, String carName, File outputFile) throws FileNotFoundException, IOException {
        config.monitor.operationStarted(1, config.moduleNames.length);
        try {
            return buildCarHelper(config, carName, outputFile);
        } finally {
            config.monitor.operationDone();
        }
    }
    
    /**
     * Builds a Car according to the given configuration and writes it out to the specified output file.
     * @param config the configuration for building the Car.
     * @param carName the canonical name of the Car to be built.
     * @param outputFile the output file to be written to.
     * @throws FileNotFoundException if the output file is not a valid location.
     * @return true if, at the end, the operation is not canceled by the user.
     * @throws IOException
     */
    private static boolean buildCarHelper(Configuration config, String carName, File outputFile) throws FileNotFoundException, IOException {
        FileSystemHelper.ensureDirectoryExists(outputFile.getParentFile());
        FileOutputStream fos = new FileOutputStream(outputFile);
        
        boolean notCanceled = false;
        
        try {
            notCanceled = buildCarHelper(config, carName, outputFile.getName(), fos);
            
            return notCanceled;
            
        } finally {
            fos.close();
            
            // delete the incomplete Car if the operation was canceled or the operation failed with an exception
            if (!notCanceled) {
                outputFile.delete();
            }
        }
    }

    /**
     * Builds a Car according to the given configuration and writes it out to the specified output stream.
     * @param config the configuration for building the Car.
     * @param carName the name of the Car to be built.
     * @param outputStream the output stream to be written to.
     * @return true if, at the end, the operation is not canceled by the user.
     * @throws IOException
     */
    public static boolean buildCar(Configuration config, String carName, OutputStream outputStream) throws IOException {
        config.monitor.operationStarted(1, config.moduleNames.length);
        try {
            return buildCarHelper(config, carName, carName, outputStream);
        } finally {
            config.monitor.operationDone();
        }
    }
    
    /**
     * Builds a Car according to the given configuration and writes it out to the specified output stream.
     * @param config the configuration for building the Car.
     * @param carName the canonical name of the Car to be built.
     * @param fileName the file name of the Car to be built.
     * @param outputStream the output stream to be written to.
     * @return true if, at the end, the operation is not canceled by the user.
     * @throws IOException
     */
    private static boolean buildCarHelper(Configuration config, String carName, String fileName, OutputStream outputStream) throws IOException {
        // the configuration came with a canceled monitor, so we shouldn't do anything
        if (config.monitor.isCanceled()) {
            return false;
        }
        
        WorkspaceManager workspaceManager = config.workspaceManager;
        ModuleName[] moduleNames = config.moduleNames;
        Monitor monitor = config.monitor;
        
        CALWorkspace workspace = workspaceManager.getWorkspace();
        
        ProgramResourcePathMapper programResourcePathMapper = new ProgramResourcePathMapper(workspaceManager.getMachineType());
        
        BufferedOutputStream bos = new BufferedOutputStream(outputStream, 1024);
        JarOutputStream jos = new JarOutputStream(bos);
        
        try {
            // Write out a minimal manifest.
            ModulePackager.writeMinimalManifestToCar(jos, carName);
            
            // Set compression level.
            jos.setLevel(Deflater.DEFAULT_COMPRESSION);
            
            // Write out the Car marker
            ZipEntry markerEntry = new ZipEntry(CAR_MARKER_NAME);
            jos.putNextEntry(markerEntry);
            jos.closeEntry();
            
            // Write out the spec files
            for (final Map.Entry<String,CarSpec> entry : config.carSpecs.entrySet()) {
                String specName = entry.getKey();
                CarSpec carSpec = entry.getValue();
                
                ZipEntry zipEntry = new ZipEntry(Car.CAR_WORKSPACE_SPEC_FOLDER.extendFile(specName).getPathStringMinusSlash());
                
                jos.putNextEntry(zipEntry);
                try {
                    carSpec.writeTo(jos);
                } finally {
                    jos.closeEntry();
                }
            }

            // Add each module and all their resources to the Car
            monitor.carBuildingStarted(fileName, moduleNames.length);
            
            for (int i = 0; i < moduleNames.length && !monitor.isCanceled(); i++) {
                ModuleName moduleName = moduleNames[i];
                monitor.processingModule(fileName, moduleName);
                
                ModulePackager.writeModuleToJar(workspace, moduleName, jos, config.buildSourcelessModules);
                writeModuleProgramResourcesToCar(workspaceManager, moduleName, jos, programResourcePathMapper);
            }
            
            // Add the additional files to the Car.
            List<String> additionalFileRelativePaths = new ArrayList<String>();
            
            for (final Map.Entry<String, byte[]> entry : config.additionalFiles.entrySet()) {
                if (monitor.isCanceled()) {
                    break;
                }
                String relativePath = entry.getKey();
                byte[] bytes = entry.getValue();
                
                ZipEntry zipEntry = new ZipEntry(relativePath);
                
                jos.putNextEntry(zipEntry);
                try {
                    jos.write(bytes);
                } finally {
                    jos.closeEntry();
                }
                
                additionalFileRelativePaths.add(relativePath);
            }
            
            // Write out the list of additional files.
            ZipEntry additionalFileListEntry = new ZipEntry(CAR_ADDITIONAL_FILES_NAME);
            
            jos.putNextEntry(additionalFileListEntry);
            try {
                StringBuilder builder = new StringBuilder();
                for (int k = 0, n = additionalFileRelativePaths.size(); k < n; k++) {
                    if (k > 0) {
                        builder.append('\n');
                    }
                    builder.append(additionalFileRelativePaths.get(k));
                }
                
                writeStringToStream(jos, builder.toString());
                
            } finally {
                jos.closeEntry();
            }
            
            return !config.monitor.isCanceled();
            
        } finally {
            monitor.carBuildingDone(fileName);
            
            jos.flush();
            bos.flush();
            
            jos.close();
            bos.close();
        }
    }
    
    /**
     * Writes the program resources (e.g. cmi and lc files) of the specified module to the Car through the given JarOutputStream.
     * 
     * @param workspaceManager the workspace manager providing the resources.
     * @param moduleName the name of the module.
     * @param jos the JarOutputStream for the Car.
     * @param programResourcePathMapper the path mapper for program resources.
     * @throws IOException
     */
    private static void writeModuleProgramResourcesToCar(WorkspaceManager workspaceManager, ModuleName moduleName, JarOutputStream jos, ProgramResourcePathMapper programResourcePathMapper) throws IOException {
        
        ResourcePath.Folder moduleFolderPath = (ResourcePath.Folder)programResourcePathMapper.getModuleResourcePath(moduleName);
        ProgramResourceLocator.Folder folderLocator = new ProgramResourceLocator.Folder(moduleName, ResourcePath.EMPTY_PATH);
        
        ProgramResourceRepository programResourceRepository = workspaceManager.getRepository();
        ProgramResourceLocator[] resourceLocators = programResourceRepository.getMembers(folderLocator);
        
        for (final ProgramResourceLocator resourceLocator : resourceLocators) {
            if (resourceLocator instanceof ProgramResourceLocator.File) {
                
                ProgramResourceLocator.File fileLocator = (ProgramResourceLocator.File)resourceLocator;
                
                String relativePath = moduleFolderPath.extendFile(resourceLocator.getName()).getPathStringMinusSlash();
                
                ZipEntry entry = new ZipEntry(relativePath);
                
                jos.putNextEntry(entry);
                InputStream inputStream = programResourceRepository.getContents(fileLocator);  // this call throws IOException, and should not return null on error
                try {
                    FileSystemResourceHelper.transferData(inputStream, jos);
                } finally {
                    // We need to close the input stream explicitly or else we will be leaking file handles
                    inputStream.close();
                    jos.closeEntry();
                }
            }
        }
    }
    
    /**
     * Writes the given string to the specified stream.
     * @param outputStream
     * @param string
     * @throws IOException
     */
    private static void writeStringToStream(OutputStream outputStream, String string) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(baos);
        osw.write(string);
        osw.flush();
        baos.flush();
        byte[] bytes = baos.toByteArray();
        
        outputStream.write(bytes);
    }
}
