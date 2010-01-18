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
 * BasicCALServices.java
 * Creation date: Feb 23, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openquark.cal.compiler.Compiler;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelModuleSource;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.ProgramModelManager.CompilationOptions;



/**
 * This is a helper class to assist CAL clients with creating and using some of the basic CAL services.
 * It handles initialization and compilation of a workspace, provision of some basic CAL service objects, and contains
 * some helper methods for filtering gems and running code.
 * 
 * @author Edward Lam
 * @author Rick Cameron
 * @author mbyne
 */
public final class BasicCALServices {

    /** this is the default BasicCALServices configuration*/
    private static final Config DEFAULT_CONFIG = makeConfig();

    /** The associated workspace manager.*/
    private final WorkspaceManager workspaceManager;
               
    /** handy TypeExpr constants for common Prelude types. */
    private PreludeTypeConstants preludeTypeConstants;
           
    /** The provider from which the workspace manager will be initialized. */
    private final WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider;
    
    /** the default compilation options */
    private CompilationOptions compilerOptions = makeDefaultCompilationOptions();
    
    /** used to cache entry points to reduce recompilation */
    private final EntryPointCache entryPointCache;


    /**
     * This class is used to encapsulate BasicCALServices configuration properties.
     *
     * @author Magnus Byne
     */
    public final static class Config {
        
        /**this defines the machine type used by the basic cal services*/
        private MachineType machineType = MachineType.LECC; 

        /** this defines the root provider - directory where lecc files are output*/
        private WorkspaceManager.SourceGenerationRootOverrideProvider rootProvider = WorkspaceManager.DEFAULT_ROOT_OVERRIDER;

        /** this limits the number of entry points that can be cached per module*/
        private int entryPointCacheSize = 10;
        
        /** this limits the number of modules for which entry points are cached*/
        private int moduleCacheSize = 5;
        
        /** 
         * Config cannot be instantiated directly - the factory method makeConfig in the containing 
         * class must be used
         */
        private Config() {}
        
        /**
         * Set the machine type of the configuration.
         * This option is limited to the package scope.
         * @param machineType
         */
        void setMachineType(MachineType machineType) {
            this.machineType = machineType;
        }
        
        /**
         * Set the maximum number of entry points that can be cached for a particular module
         * @param entryPointCacheSize
         */
        public void setEntryPointCacheSize(int entryPointCacheSize) {
            this.entryPointCacheSize = entryPointCacheSize;
        }
        /**
         * Set the maximum number of modules for which entry points will be cached
         * @param moduleCacheSize the number of modules to cache entry points for
         */
        public void setModuleCacheSize(int moduleCacheSize) {
            this.moduleCacheSize = moduleCacheSize;
        }
           
        /**
         * Set the root directory provider - this controls where compiled modules are stored.
         * @param rootProvider
         */
        public void setSourceRootProvider(WorkspaceManager.SourceGenerationRootOverrideProvider rootProvider) {
            this.rootProvider = rootProvider;
        }
        
    }
        
    /**
     * Constructor for a BasicCALServices object
     * @param workspaceManager The workspace manager to be used.
     * @param workspaceFileProperty 
     *   If non-null, the value of this system property represents the file system pathname of the workspace file
     *     to be used to instantiate the workspace.
     *   If null, defaultWorkspaceFileName will be used.
     * @param defaultWorkspaceFileName This workspace file from the StandardVault will be used if the workspaceFileProperty 
     *   system property is not set.
     */
    private BasicCALServices (WorkspaceManager workspaceManager, 
        String workspaceFileProperty,
        String defaultWorkspaceFileName,
        Config config) {

        if (workspaceManager == null) {
            throw new NullPointerException("Argument workspaceManager may not be null.");
        }
       
        this.workspaceDeclarationProvider =  DefaultWorkspaceDeclarationProvider.getDefaultWorkspaceDeclarationProvider(workspaceFileProperty, defaultWorkspaceFileName);
        this.workspaceManager = workspaceManager;
        
        workspaceManager.getProgramManager();
        workspaceManager.getProgramManager();
        
        this.entryPointCache = new EntryPointCache(workspaceManager, config.entryPointCacheSize, config.moduleCacheSize);
    }

    /**  
     * The simplest factory method to create a BasicCALServices instance. 
     * @param workspaceFileName name of the workspace from the StandardVault e.g. "cal.platform.test.cws"
     *    
     * @return the BasicCALServices instance, or null if initialization failed
     */
    public static BasicCALServices make (String workspaceFileName) {

        return make(null, workspaceFileName, null);
    }         
    
    /**
     * The simplest factory method to create a BasicCALServices instance. 
     * It ensures that the CAL workspace is compiled and ready to use.
     * This is all that is needed in most cases.
     * @param workspaceFileName
     * @param messageLogger all messages created during the compilation of the workspace are appended to the messageLogger
     * @return the compiled CAL services, or null if initialization or compilation failed
     */
    public static BasicCALServices makeCompiled (String workspaceFileName, CompilerMessageLogger messageLogger) {

        BasicCALServices res =  make(workspaceFileName);        
        if (res == null) {
            return null;
        }
        
        boolean compiled = res.compileWorkspace(null, messageLogger);
        
        if (!compiled) {
            return null;
        }
        
        return res;
    }
    
    /**
     * Factory method for this class.
     * @param workspaceFileProperty 
     *   If non-null, the value of this system property represents the file system pathname of the workspace file
     *     to be used to instantiate the workspace.
     *   If null, defaultWorkspaceFileName will be used.
     * @param defaultWorkspaceFileName This workspace file from the StandardVault will be used if the workspaceFileProperty 
     *   system property is not set.
     * @param defaultWorkspaceClientID a string identifying the default client for this workspace.
     *   Client id's are used to refer to specific workspaces.  This id will be used, unless the property
     *   indicated by CALWorkspace.WORKSPACE_PROP_CLIENT_ID is set, in which case the property value will be used.
     *   If the client id is null, the workspace is nullary.
     * 
     * @return the BasicCALServices instance, or null if initialization failed
     */
    public static BasicCALServices make (
        String workspaceFileProperty,
        String defaultWorkspaceFileName,
        String defaultWorkspaceClientID) {
        
        return make(workspaceFileProperty, defaultWorkspaceFileName, defaultWorkspaceClientID, DEFAULT_CONFIG);
    }    
    
    /**
     * Factory method for this class.
     * @param workspaceFileProperty 
     *   If non-null, the value of this system property represents the file system pathname of the workspace file
     *     to be used to instantiate the workspace.
     *   If null, defaultWorkspaceFileName will be used.
     * @param defaultWorkspaceFileName This workspace file from the StandardVault will be used if the workspaceFileProperty 
     *   system property is not set.
     * @param defaultWorkspaceClientID a string identifying the default client for this workspace.
     *   Client id's are used to refer to specific workspaces.  This id will be used, unless the property
     *   indicated by CALWorkspace.WORKSPACE_PROP_CLIENT_ID is set, in which case the property value will be used.
     *   If the client id is null, the workspace is nullary.
     * @param config this parameter controls the configuration of the BasicCALServices that is created
     * @return the BasicCALServices instance, or null if initialization failed
     */
    public static BasicCALServices make (
        String workspaceFileProperty,
        String defaultWorkspaceFileName,
        String defaultWorkspaceClientID,
        Config config) {
        
        
        String clientID = WorkspaceConfiguration.getDiscreteWorkspaceID(defaultWorkspaceClientID);

        WorkspaceManager workspaceManager = WorkspaceManager.getWorkspaceManager(clientID, config.machineType, config.rootProvider);

        if (workspaceManager ==null) {
            return null;
        } else {
            return new BasicCALServices(workspaceManager, workspaceFileProperty, defaultWorkspaceFileName, config);
        }
    }
    
    /** makes a new BasicCALServices configuarion with default properties
     * @return a new default config
     */
    public static Config makeConfig() {
        return new Config();
    }

    /**
     * @return the workspace manager.
     */
    public WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }

    /**  
     * Note: must have a compiled workspace in hand before these will be available.  
     * @return handy TypeExpr constants for common Prelude types.
     */
    public PreludeTypeConstants getPreludeTypeConstants() {
        
        if (preludeTypeConstants == null) {
            
            ModuleTypeInfo preludeModuleTypeInfo = workspaceManager.getModuleTypeInfo(CAL_Prelude.MODULE_NAME);
            if (preludeModuleTypeInfo == null) {
                throw new IllegalStateException("Must have a successfully compiled Cal.Core.Prelude module before getPreludeTypeConstants can be called.");
            }
            
            preludeTypeConstants = new PreludeTypeConstants(preludeModuleTypeInfo);           
        }
        
        return preludeTypeConstants;
    }    

    /**
     * Compile the workspace.
     * 
     * @param programStatusListener if non-null, the listener on the status of the compilation.
     * @param logger the logger used to log error messages during compilation.
     * @return true if the compilation was successful
     */
    public boolean compileWorkspace(StatusListener programStatusListener, CompilerMessageLogger logger) {
        
        entryPointCache.lockCache();
        try {
            entryPointCache.flush();

            // Load default prelude.
            if (logger == null) {
                logger = new MessageLogger ();
            }

            Status compileStatus = new Status("Compile status.");

            // Initialize the workspace.
            workspaceManager.initWorkspace(workspaceDeclarationProvider, compileStatus);

            if (compileStatus.getSeverity() != Status.Severity.OK) {
                logger.logMessage(compileStatus.asCompilerMessage());
                return false;
            }

            boolean programCompilationSuccessful = makeProgram (this.workspaceManager, logger, programStatusListener);

            return programCompilationSuccessful;
        } finally {
            entryPointCache.unlockCache();
        }
    }
    
    /**
     * Recompile the workspace.
     * @return true if the compilation was successful, false otherwise.
     */
    public boolean recompileWorkspace (CompilerMessageLogger logger) {
        entryPointCache.lockCache();
        try {
            entryPointCache.flush();
            return makeProgram (this.workspaceManager, logger, null);
        } finally {
            entryPointCache.unlockCache();
        }
    }

    /**
     * Builds the program object using the CAL file at the given location.
     * Sets the internal program member.
     * @return true if compilation was successful, false otherwise
     */
    private boolean makeProgram(WorkspaceManager workspaceManager, CompilerMessageLogger logger, StatusListener statusListener) {
    
        if (logger == null) {
            logger = new MessageLogger ();
        }
        int nErrorsBefore = logger.getNErrors();
    
        // Compile.
        workspaceManager.compile(logger, false, statusListener);

        return logger.getNErrors() == nErrorsBefore;
    }
    
    /**
     * @return the CAL workspace.
     */
    public CALWorkspace getCALWorkspace () {
        return getWorkspaceManager().getWorkspace ();
    }

    /**
     * @return an associated Compiler
     */
    public Compiler getCompiler() {
        return getWorkspaceManager().getCompiler();
    }
    
    /**
     * @return the associated TypeChecker
     */
    public TypeChecker getTypeChecker() {
        return getWorkspaceManager().getTypeChecker();
    }
    
    /**
     * Get the type info for a module
     * @param moduleName the name of the module
     * @return the type info for the module
     */
    public ModuleTypeInfo getModuleTypeInfo(ModuleName moduleName) {
        CALWorkspace workspace = getCALWorkspace();
        if (workspace == null) {
            return null;
        }
        return workspace.getMetaModule(moduleName).getTypeInfo();
    }

    /**
     * Get the type checker info for a module
     * @return the TypeCheckInfo for a module
     */
    public TypeCheckInfo getTypeCheckInfo(ModuleName moduleName) {
        return getTypeChecker ().getTypeCheckInfo (moduleName);
    }

    /**
     * Returns the entity corresponding to the specified qualified name.
     * @param gemName the name of the entity to retrieve.
     * @return the corresponding gem entity, or null if it is not found.
     */
    public GemEntity getGemEntity(QualifiedName gemName) {
        return getWorkspaceManager().getWorkspace().getGemEntity(gemName);
    }
    
    /**
     * This returns a type expression for the type represented by the qualified name
     * the type expression is evaluated within the scope of the type's defining module
     * @param name the types qualified name
     * @return type expression representing the qualified type name 
     */
    public TypeExpr getTypeFromQualifiedName(QualifiedName name) {
        return getTypeFromString(name.getModuleName(), name.getQualifiedName());
    }
    
    /**
     * Returns the type expression corresponding to the specified string.
     * @param module the type string is compiled w.r.t to this module
     * @param typeString a string representation a CAL type.
     * @return a type expression object corresponding to the string, or null if the string couldn't be converted to a type.
     */
    public TypeExpr getTypeFromString(ModuleName module, String typeString) {    
        return getTypeChecker().getTypeFromString(typeString, module, null);   
    }    

    /**
     * Returns public gems in the workspace that match the given filter
     * 
     * @param filter a gem filter.
     * @return public gems in the workspace which pass the filter.
     */
    public Set<GemEntity> getMatchingGems(GemFilter filter) {
        return getMatchingGems(filter, true);
    }

    /**
     * Returns public gems in the workspace that match the given filter.
     * 
     * @param filter a gem filter.
     * @param sortGems If true, then the gems will be sorted alphabetically.
     * @return public gems in the workspace which pass the filter.
     */
    public Set<GemEntity> getMatchingGems(GemFilter filter, boolean sortGems) {
        GemViewer gemViewer = new GemViewer();

        // Sort the gems alphabetically, if specified.
        if (sortGems) {
            gemViewer.setSorter (new GemQualifiedNameCaseInsensitiveSorter ());
        }
    
        gemViewer.addFilter(filter);
    
        return getVisibleGemEntities(gemViewer); //perspective.getVisibleGemEntities();
    }

    
    /**
     * Get public gems in the workspace.
     * @param gemEntityViewer the gem viewer used to filter the gems returned
     * @return public gems in the workspace which visible by the given viewer
     */
    private Set<GemEntity> getVisibleGemEntities(GemViewer gemEntityViewer) {

        CALWorkspace workspace = getCALWorkspace();
        
        Set<GemEntity> visibleEntitySet = new LinkedHashSet<GemEntity>();

        // add all visible entities from visible modules
        int nVisibleModules = workspace.getNMetaModules(); //  getNVisibleMetaModules();
        for (int i = 0; i < nVisibleModules; i++) {
            MetaModule nthModule = workspace.getNthMetaModule(i);
            visibleEntitySet.addAll(getVisibleGemEntities(nthModule, gemEntityViewer));//   getVisibleGemEntities(nthModule));
        }

        // Apply the view policy if any.
        if (gemEntityViewer != null) {
            visibleEntitySet = new LinkedHashSet<GemEntity>(gemEntityViewer.view(visibleEntitySet));
        }
        return visibleEntitySet;
    }
    
    /**
     * Get the public gems in a given module.
     * @param module the module in which to look.
     * @return Set (GemEntity) the set of visible entities.
     */
    private Set<GemEntity> getVisibleGemEntities(MetaModule module, GemViewer gemEntityViewer) {
        
        // Check that module != null
        Assert.isNotNullArgument(module, "module");

        int nEntities = module.getNGemEntities();

        // Add all entities from the working module, or only public entities from non-working modules.
        Set<GemEntity> visibleEntitySet = new LinkedHashSet<GemEntity>();
        for (int i = 0; i < nEntities; i++) {

            GemEntity gemEntity = module.getNthGemEntity(i);
            
            if (gemEntity.getScope() == Scope.PUBLIC) {
                visibleEntitySet.add(gemEntity);
            }
        }

        // Apply the view policy if any.
        if (gemEntityViewer != null) {
            visibleEntitySet = new LinkedHashSet<GemEntity>(gemEntityViewer.view(visibleEntitySet));
        }

        return visibleEntitySet;
    }
    
    /**
     * Returns a Set containing the public gems in the workspace 
     * which have the specified type signature. 
     * @param scopeModule this is the module that is used for scope 
     * @param typeName  the type signature of the gems to be found
     * @param sortGems  If true, then the gems will be sorted alphabetically.
     * @return a set of the matching gems
     */
    public Set<GemEntity> findGemsOfType(ModuleName scopeModule, String typeName, boolean sortGems) {
        // Get the type expression for the type name.
        final TypeExpr typeExpr = getTypeFromString(scopeModule, typeName);
        if (typeExpr == null) {
            return Collections.emptySet();
        }

        // Find all gems matching this type.
        final ModuleTypeInfo targetModuleTypeInfo = getModuleTypeInfo(scopeModule);
        GemFilter filter = new GemFilter() {
            @Override
            public boolean select(GemEntity gemEntity) {
                TypeExpr gemType = gemEntity.getTypeExpr();
                return TypeExpr.canPatternMatch(gemType, typeExpr, targetModuleTypeInfo);
            }
        };
        return getMatchingGems(filter, sortGems);
    }

    /**
     * Returns a set containing the public gems in the workspace which have 
     * the specified return type. 
     * @param scopeModule     this is the module that is used for compiling the type
     * @param returnTypeName  the type name of the return type for the gems to be found
     * @param sortGems        If true, then the gems will be sorted alphabetically.
     * @return (Set of GemEntity) a set of the matching gems
     */
    public Set<GemEntity> findGemsByReturnType(ModuleName scopeModule, String returnTypeName, boolean sortGems) {
        // Get the type expression for the type name.
        final TypeExpr returnTypeExpr = getTypeFromString(scopeModule, returnTypeName);
        if (returnTypeExpr == null) {
            return Collections.emptySet();
        }

        // Find all gems matching this type.
        final ModuleTypeInfo targetModuleTypeInfo = getModuleTypeInfo(scopeModule);
        GemFilter filter = new GemFilter() {
            @Override
            public boolean select(GemEntity gemEntity) {
                TypeExpr gemResultType = gemEntity.getTypeExpr().getResultType();
                return TypeExpr.canPatternMatch(gemResultType, returnTypeExpr, targetModuleTypeInfo);
            }
        };
        return getMatchingGems(filter, sortGems);
    }

    /**
     * Compile the source definition for a module and add it to the program
     * @param sourceDefinition the module's source definition.
     * @param logger the logger used to log error messages during compilation.
     * @return the max error severity from the compile.
     */
    public CompilerMessage.Severity addNewModule(ModuleSourceDefinition sourceDefinition, 
                                                  CompilerMessageLogger logger) {

        entryPointCache.lockCache();
        try {
            entryPointCache.flush();
            return getWorkspaceManager().makeModule(sourceDefinition, logger, compilerOptions);
        } finally {
            entryPointCache.unlockCache();
        }
    }
    
    /**
     * Add a new module containing the specified function to the program.
     * Imports are computed automatically based on the function.
     * If the module already exists its' contents are replaced by the funcDefn.
     * @param newModuleName
     * @param functionDefintion
     * @return an EntryPointSpec with default input/output policies for the new function.
     * @throws GemCompilationException
     */
    public EntryPointSpec addNewModuleWithFunction(ModuleName newModuleName, SourceModel.FunctionDefn functionDefintion) throws GemCompilationException {
        
        return addNewModuleWithFunction(newModuleName, null, functionDefintion, null);
    }
    
    /**
     * Add a new module containing the specified function, type declaration and imports to the program.
     * If the module already exists its' contents are replaced by the funcDefn.  
     * @param newModuleName
     * @param imports - if this is null the imports are computed based on the function definition
     * @param functionDefinition
     * @param functionTypeDeclaration the type declaration for the function, this may be null
     * @return an EntryPointSpec, with default input/output policies, for the new function.
     * @throws GemCompilationException
     */
    public EntryPointSpec addNewModuleWithFunction(ModuleName newModuleName, SourceModel.Import[] imports, SourceModel.FunctionDefn functionDefinition, SourceModel.FunctionTypeDeclaration functionTypeDeclaration) throws GemCompilationException {
        if (newModuleName == null) {
            throw new NullPointerException("Argument newModuleName must not be null.");
        }
        if (functionDefinition == null) {
            throw new NullPointerException("Argument functionDefinition must not be null.");
        }
        
        //create the module containing the function
        SourceModel.ModuleDefn newModule = SourceModel.ModuleDefn.make(
            newModuleName,
            imports,
            functionTypeDeclaration == null ?
                    new SourceModel.TopLevelSourceElement[] { functionDefinition } :
                    new SourceModel.TopLevelSourceElement[] { functionTypeDeclaration, functionDefinition }   );
            
        //compute the necessary imports
        if (imports == null) {
            newModule = SourceModelUtilities.ImportAugmenter.augmentWithImports(newModule);
        }
        
        CompilerMessageLogger logger = new MessageLogger();
        
        //add the module to the workspace
        CompilerMessage.Severity errorLevel = addNewModule(new SourceModelModuleSource(newModule), logger);
        
        if (errorLevel.compareTo(CompilerMessage.Severity.ERROR) >= 0) {
            throw new GemCompilationException("Error adding new module", logger);
        }
                
        return EntryPointSpec.make(QualifiedName.make(newModuleName, functionDefinition.getName()));
    }

    /**
     * Runs an existing CAL function and returns the result. 
     * A new default execution context is created to run the function, and then destroyed. 
     * It is worthwhile to consider
     * creating an execution context separately and using it for all calls to runFunction, as this
     * can be more efficient, allowing certain results to be cached between executions. 
     * @see #runFunction(EntryPointSpec, ExecutionContext, Object[])
     *  
     * @param entryPointSpec defines the function to run and the input and output policies to use.
     * @param args the arguments to pass to the function - may be null if there are no arguments
     * @return the result of running the function.
     * @throws CALExecutorException
     * @throws GemCompilationException
     */
    public Object runFunction(EntryPointSpec entryPointSpec, Object[] args) throws CALExecutorException, GemCompilationException {

        ExecutionContext executionContext = workspaceManager.makeExecutionContextWithDefaultProperties();
        
        try {            
            return runFunction(entryPointSpec, executionContext, args);
        } finally {
            workspaceManager.resetCachedResults(executionContext);
        }
    }
    
    /**
     * runs an existing function and returns the result. It uses the supplied execution context.
     * It is generally much more efficient to use this version of runFunction, as it allows 
     * CAFs can be cached between calls. 
     * @param entryPointSpec defines the function to run and the input and output policies to use.
     * @param args the arguments to pass to the function - may be null if there are no arguments.
     * @return the result of running the function.
     * @throws CALExecutorException
     * @throws GemCompilationException
     */
    public Object runFunction(EntryPointSpec entryPointSpec, ExecutionContext executionContext, Object[] args) throws CALExecutorException, GemCompilationException {
        if (entryPointSpec == null) {
            throw new NullPointerException("Argument entryPointSpec must not be null.");
        }
        if (executionContext == null) {
            throw new NullPointerException("Argument executionContext must not be null."); 
        }
        
        return entryPointCache.runFunction(entryPointSpec, args == null ? new Object[0] : args, executionContext);
        
    }
    
    /**
     * This method pre-caches a list of entryPointSpecs for invocation with runFunction.
     * It is not necessary to use this method before using run function, but in some
     * circumstances it can improve performance. When runFunction is invoked on an
     * entryPointSpec for the first time, the CAL compiler must do some work in the target module.
     * If you intend to use many entryPointSpecs for the same module, invoking this function
     * with a list of the entryPointSpecs allows the CAL compiler to prepare them all
     * in a single step, which can be more efficient. If you run only a single function (even many times) 
     * in any given module, or are not concerned by the cost of the first call to runFunction 
     * for each entryPointSpec, there is no need to use this function.
     * 
     * @param entryPointSpecs a list of entry point specs that will be used later by runFunction.
     * @throws GemCompilationException
     */
    public void prepareFunctions(List<EntryPointSpec> entryPointSpecs) throws GemCompilationException { 
        entryPointCache.cacheEntryPoints(entryPointSpecs);
    }
    
    /**
     * Get compiler options
     */
    public CompilationOptions getCompilationOptions() {
        return new CompilationOptions(compilerOptions);
    }
    
    /**
     * set the compilation options - if they are not set default compilation options will be used
     * @param options the compilation options to use when running code.
     */
    public void setCompilationOptions(CompilationOptions options) {
        compilerOptions = new CompilationOptions(options);
    }

    /**
     * @return the default compilation options, with the for immediate use flag set to true.
     */
    private static CompilationOptions makeDefaultCompilationOptions() {
        CompilationOptions options = new CompilationOptions();
        options.setForImmediateUse(true);
        return options;
    }
}

