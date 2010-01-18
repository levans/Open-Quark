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
 * LECCModule.java
 * Created: Feb 28, 2005
 * By: Raymond Cypher
 */

package org.openquark.cal.internal.machine.lecc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.DeserializationHelper;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.Packager;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.MachineFunctionImpl;
import org.openquark.cal.internal.machine.lecc.JavaPackager.LECCMachineFunction;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;
import org.openquark.cal.machine.GeneratedCodeInfo;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.services.ResourcePath;


/**
 * The version of Module specific to the lecc machine.
 * @author RCypher
 * Created: May 14, 2004
 */
public final class LECCModule extends Module {
    
    private static final int serializationSchema = 0;
    
    /** A lock for access to the {@link #classLoader} field. */
    // Note: this is a separate lock from the one in the superclass. In fact, if the classLoader field is guarded by
    // the same lock as the one used for guarding the function name maps in the superclass, deadlock may occur,
    // as follows:
    // Thread 1:
    //   LECCModule.resetCachedResults() [acquires lock for the LECCModule (classloader access)]
    //   -> CALClassLoader.resetCachedResults()
    //   -> Class.getMethod()
    //   -> ...
    //   -> Class.getDeclaredMethods0() [needs lock for the CALClassLoader, for classloading purposes]
    // Thread 2:
    //   ClassLoader.loadClassInternal() [acquires lock for the CALClassLoader]
    //   -> CALClassLoader.loadClass()
    //   -> ...
    //   -> CALClassLoader.getBytecodeForClassInternal()
    //   -> ...
    //   -> LECCModule.getFunction()
    //   -> Module.getFunction() [needs lock for the LECCModule (function name maps access)]
    private final int[] classLoaderLock = new int[0];
    
    /** The class loader associated with this module. */
    /* @GuardedBy("classLoaderLock") */
    private CALClassLoader classLoader;
    
    private ProgramResourceRepository resourceRepository;
    
    /** Whether class data should be retrieved from the repository.  
     * If false, class data will be dynamically generated.  If null, this value hasn't been calculated. */
    private Boolean shouldLookupClassData;

    /**
     * The instance of ClassNameMapper used for mapping an unqualified
     * function or type name to a name which is suitable for use in Java source
     * or the file system.
     */
    private final ClassNameMapper classNameMapper = new ClassNameMapper();
       
    private final Map<String, FunctionGroupInfo> functionNameToFunctionGroup = new HashMap<String, FunctionGroupInfo>();
    
    private boolean coarseGrouping = false;
    
    /** (String -> MachineFunction) the lifted functions created from
     * let variable definitions.*/
    private final Map<String, MachineFunction> liftedFunctionMap = new HashMap<String, MachineFunction>();
    

    /**
     * Constructor for a LECCModule.
     * Note that setGeneratedCodeInfo() should be called when this module has been compiled/loaded.
     * 
     * @param moduleName
     * @param foreignClassLoader
     * @param resourceRepository
     */
    LECCModule (ModuleName moduleName, ClassLoader foreignClassLoader, ProgramResourceRepository resourceRepository) {
        super (moduleName, foreignClassLoader);
        this.resourceRepository = resourceRepository;
    }
    
    /**
     * Private constructor used by deserialization code.
     * @param moduleName
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     * @param generatedCodeInfo The GeneratedCodeInfo for the persisted form of this module.
     */
    private LECCModule (ModuleName moduleName, ClassLoader foreignClassLoader, GeneratedCodeInfo generatedCodeInfo) {
        super (moduleName, foreignClassLoader, generatedCodeInfo);
    }
    
    /**
     * Build up a set of all the modules upon which this Module depends, both directly and indirectly, 
     * including the current module.
     * @return the set of modules upon which this Module depends, including the current module.
     */
    private Set<LECCModule> getDependeeModuleSet() {
        Set<LECCModule> dependeeModuleSet = new HashSet<LECCModule>();
        buildDependeeModuleSet(getModuleTypeInfo(), new HashSet<ModuleName>(), dependeeModuleSet);
        
        return dependeeModuleSet;
    }
    
    /**
     * Build up a set of all the modules upon which this Module depends, both directly and indirectly.
     * @param currentModuleTypeInfo 
     * @param moduleNames (Set of ModuleName) the set of module names already traversed (including the current one).
     * @param moduleSet
     */
    private void buildDependeeModuleSet (ModuleTypeInfo currentModuleTypeInfo, Set<ModuleName> moduleNames, Set<LECCModule> moduleSet) {
        // Check whether this module has already been traversed..
        if (!moduleNames.add(currentModuleTypeInfo.getModuleName())) {
            return;
        }
        moduleSet.add((LECCModule)currentModuleTypeInfo.getModule());
        
        // Call recursively on imported modules.
        for (int i = 0; i < currentModuleTypeInfo.getNImportedModules(); ++i) {
            ModuleTypeInfo importedModuleTypeInfo = currentModuleTypeInfo.getNthImportedModule(i);
            buildDependeeModuleSet (importedModuleTypeInfo, moduleNames, moduleSet);
        }
    }
    
    CALClassLoader getClassLoader() {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                if (resourceRepository == null) {
                    return null;
                }
                Set<LECCModule> dependeeModuleSet = getDependeeModuleSet();
                List<LECCModule> dependeeModuleList = new ArrayList<LECCModule>(dependeeModuleSet);

                // Use the CALClassLoader factory method to create a class loader instance.
                ProgramResourceLocator moduleFolderLocator = new ProgramResourceLocator.Folder(getName(), ResourcePath.EMPTY_PATH);
                classLoader = CALClassLoader.makeCALClassLoader(resourceRepository, moduleFolderLocator, getForeignClassLoader(), this, dependeeModuleList);
            }

            return classLoader;
        }
    }
    
    
    void resetCachedResults (ExecutionContext context) {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                return;
            }
            classLoader.resetCachedResults(context);
        }
    }
    
    /**
     * Discard the cached CAFs and the class loader for this module.
     * @param context
     */
    void resetMachineState(ExecutionContext context) {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                return;
            }
            classLoader.resetCachedResults(context);
            // Discard the class loader. This will unload all classes in the module and adjunct.
            classLoader = null;
        }
    }
    
    /**
     * Discard previously loaded classes for this module.
     * @param forAdjunct - if true indicates that only classes for the adjunct should be discarded.
     */
    void resetClassLoader (boolean forAdjunct) {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                return;
            }
            if (forAdjunct) {
                classLoader = CALClassLoader.resetAdjunctClasses(classLoader);
            } else {
                // Simply discard the class loader.  This will unload all classes in the module
                // and adjunct.
                classLoader = null;
            }
        }
    }
    
    /**
     * @param forAdjunct
     * @return the number of classes loaded by the associated class loader.
     */
    int getNClassesLoaded(boolean forAdjunct) {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                return 0;
            } else {
                return classLoader.getNClassesLoaded(forAdjunct);
            }
        }
    }
    
    /**
     * @param forAdjunct
     * @return the number of bytes loaded by the associated class loader.
     */
    int getNClassBytesLoaded(boolean forAdjunct) {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                return 0;
            } else {
                return classLoader.getNClassBytesLoaded(forAdjunct);
            }
        }
    }
    
    /**
     * @param forAdjunct
     * @return number of milliseconds spent in generating class file data by the associated class loader.
     */
    long getGenerateClassDataTimeMS(boolean forAdjunct) {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                return 0;
            } else {
                return classLoader.getGenerateClassDataTimeMS(forAdjunct);
            }
        }
    }
    
    
    /**
     * @param forAdjunct
     * @return number of milliseconds spent in looking up class file data by the associated class loader.
     */
    long getLookupClassDataTimeMS(boolean forAdjunct) {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                return 0;
            } else {
                return classLoader.getLookupClassDataTimeMS(forAdjunct);
            }
        }
    }
    
    /**
     * @param forAdjunct
     * @return number of milliseconds spent in findClass() by the associated class loader.
     */
    long getFindClassTimeMS(boolean forAdjunct) {
        synchronized (classLoaderLock) {
            if (classLoader == null) {
                return 0;
            } else {
                return classLoader.getFindClassTimeMS(forAdjunct);
            }
        }
    }
    
    /**
     * Write out this instance of LECCModule to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    @Override
    protected void writeActual (RecordOutputStream s) throws IOException {
        s.startRecord (ModuleSerializationTags.LECC_MODULE, serializationSchema);
        s.writeModuleName(getName());
        classNameMapper.write(s);
        super.writeContent (s);
        s.endRecord ();
    }
    
    /** {@inheritDoc} */
    @Override
    protected void writeMachineSpecificSerializationInfo(RecordOutputStream s) throws IOException {
        CodeGenerator.GeneratedCodeInfo codeInfo = CodeGenerator.getNewCodeInfo(this);
        codeInfo.write(s);
        
        // Update the module's GeneratedCodeInfo.
        setGeneratedCodeInfo(codeInfo);
    }
    
    /**
     * Read meta information about the serialized module for the lecc machine 
     * @param s
     * @return GeneratedCodeInfo instance filled in from a SerializationInfo record
     *          read from s. 
     * @throws IOException
     */
    static CodeGenerator.GeneratedCodeInfo readLeccSpecificSerializationInfo(RecordInputStream s) throws IOException {
        RecordInputStream.RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.SERIALIZATION_INFO);
        if (rhi == null) {
            throw new IOException ("Unable to find serialization info");
        }
        
        // Skip the non-machine-specific data
        s.readLong(); // timestamp
        
        // read our own part of the record
        CodeGenerator.GeneratedCodeInfo codeInfo = CodeGenerator.GeneratedCodeInfo.load(s);
        
        s.skipRestOfRecord();
        return codeInfo;
    }
    
    /**
     * Read the contents of the LECCModule from the RecordInputStream.
     * The read position will be after the initial members needed to
     * construct this LECCModule instance have been read. 
     * @param s
     * @param schema
     * @param otherModules
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    protected void readContent (RecordInputStream s, int schema, Map<ModuleName, Module> otherModules, CompilerMessageLogger msgLogger) throws IOException {
        classNameMapper.readContent(s);
        
        // Read the base class content.
        super.readContent(s, otherModules, msgLogger);
        
        DeserializationHelper.checkSerializationSchema(schema, serializationSchema, getName(), "LECCModule", msgLogger);
        
        // There's no more members specific to LECCModule to be read.
        // Always skip to the end of the record, we might be reading a record produced
        // by a later version of the code which has additional info in it.
        s.skipRestOfRecord();
    }
    
    /**
     * Read the generated code info from the RecordInputStream.
     * The read position will be after the members needed to construct the generated code info for this module.
     * @param s
     * @param schema
     * @return the generated code info for this module.
     * @throws IOException if there was a problem read the generated code info from this module.
     */
    public static GeneratedCodeInfo loadCodeInfo(RecordInputStream s, short schema) 
    throws IOException {
        
        return CodeGenerator.GeneratedCodeInfo.load(s, schema);
    }
    
    /**
     * Load an instance of LECCModule from the RecordInputStream.
     * Assumes that the read position is after the LECCModule record header.
     * @param s
     * @param schema
     * @param otherModules
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     * @param generatedCodeInfo The GeneratedCodeInfo for the persisted form of this module.
     * @param msgLogger the logger to which to log deserialization messages.
     * This should be used to log user (non-internal) errors only.
     * @return the new LECCModule, or null if there was a problem reading the module.
     * @throws IOException if any internal errors occurred while loading the module.
     */
    public final static Module load (RecordInputStream s, int schema, Map<ModuleName, Module> otherModules, ClassLoader foreignClassLoader, 
            org.openquark.cal.machine.GeneratedCodeInfo generatedCodeInfo, CompilerMessageLogger msgLogger) throws IOException {
        
        int nErrorsBeforeLoad = msgLogger.getNErrors();
        
        // Read in the name and create the module instance.
        ModuleName moduleName = s.readModuleName();
        
        LECCModule newModule = new LECCModule (moduleName, foreignClassLoader, generatedCodeInfo);
        
        // Instruct the module to load the rest of its contents.
        newModule.readContent(s, schema, otherModules, msgLogger);
        
        // If there were any problems reading the module, don't return the module.
        if (msgLogger.getNErrors() != nErrorsBeforeLoad) {
            return null;
        }
        
        return newModule;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mustLoadExpressions() {
        // if we retrieve class data from the repository, we don't load expressions.
        return !shouldLookupClassData() || Packager.getOptimizerLevel() > 0;
    }
    
    /**
     * @return whether the class data should be retrieved from the repository.
     * If false, the class data should instead be dynamically generated on demand.
     * 
     * This value is calculated for the classloader the first time this method is called.  Subsequent calls return the same value.
     */
    public boolean shouldLookupClassData() {

        if (shouldLookupClassData == null) {

            /*
             * Lookup class data from disk if:
             *   Static runtime, and GeneratedCodeInfo for the module is compatible with current settings.
             *   Dynamic runtime, .lc files are available, other GeneratedCodeInfo for the module is compatible with current settings, and module is in a car.
             * 
             * Generate class data dynamically if:
             *   Static runtime, and GeneratedCodeInfo for the module is not compatible with current settings.
             *   Dynamic runtime, and one of:
             *     no car
             *     car with no .lc files (car module not static)
             *     car with .lc files, but GeneratedCodeInfo incompatible.
             */

            boolean boolValue;
            if (LECCMachineConfiguration.isLeccRuntimeStatic()) {
                // Static runtime
                // Lookup class data unless it's out of date.
                GeneratedCodeInfo gci = getGeneratedCodeInfo(); 
                boolValue = (gci == null) ? true : gci.isCompatibleWithCurrentConfiguration();
                
                if (!boolValue) {
                    // The generated code info indicates that the classes in the repository are incompatible with the current configuration.
                    // This should never happen for a static runtime.
                    throw new IllegalStateException("The existing runtime classes are not compatible with the current static configuration.");
                }

            } else {
                // Dynamic runtime.
                // Lookup class data if all of the following are true:
                //  1) Generated code info indicates that it's statically generated.
                //  2) Other Generated code info members are compatible with current settings.
                CodeGenerator.GeneratedCodeInfo generatedCodeInfo = (CodeGenerator.GeneratedCodeInfo)getGeneratedCodeInfo();

                boolValue = generatedCodeInfo != null && 
                    generatedCodeInfo.isStaticRuntime() 
                    && generatedCodeInfo.isCompatibleWithCurrentConfiguration() ;
            }

            this.shouldLookupClassData = Boolean.valueOf(boolValue);
        }

        return shouldLookupClassData.booleanValue();
    }

    /**
     * @return the instance of ClassNameMapper used for mapping an unnqualified
     * function or type name to a name which is suitable for use in Java source
     * or the file system.
     */
    ClassNameMapper getClassNameMapper() {
        return classNameMapper;
    }
    
    /**
     * @return Returns whether the resource repoository is set.
     */
    boolean hasRepository() {
        return resourceRepository != null;
    }

    /**
     * @param resourceRepository The resource repository to set.
     */
    void setRepository(ProgramResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }
    
    void addLiftedLetVarFunction(MachineFunction mf, FunctionGroupInfo fgi) {
        liftedFunctionMap.put(mf.getName(), mf);
        functionNameToFunctionGroup.put(mf.getName(), fgi);
    }
    
    /**
     * Fetch a function by name.
     * @param functionName
     * @return the MachineFunction instance for the named function.  Null if it doesn't exist.     
     */
    @Override
    public MachineFunction getFunction (String functionName) {
        MachineFunction mf = super.getFunction(functionName);
        
        // Check to see if the function is a lifted let variable function.
        if (mf == null) {
            mf = liftedFunctionMap.get(functionName);
        }
        return mf;
    }

    /**
     * Fetch a label by name.
     * The label can be in this module, in an imported module, in an import of an imported module, etc.
     * @param functionName - the qualified name of a function
     * @return the MachineFunction instance for the named function.  Null if it doesn't exist.
     */
    @Override
    public MachineFunction getFunction (QualifiedName functionName) {
        MachineFunction mf = super.getFunction(functionName);
        
        // Check to see if the function is a lifted let variable function.
        if (mf == null && functionName.getModuleName().equals(getName())) {
            mf = liftedFunctionMap.get(functionName.getUnqualifiedName());
        }

        return mf;
    }
    
    /**
     * This method is used to delegate loading of MachineFunciton instances
     * to concrete extensions of Module.  The sub-classes of module contain
     * the information about what class to actually load.
     * @param s
     * @param mti
     * @param msgLogger
     * @return an instance of MachineFunction
     * @throws IOException 
     */
    @Override
    protected MachineFunction loadMachineFunction(RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Load the record header and determine which actual class we are loading.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.LECC_MACHINE_FUNCTION);
        if (rhi == null) {
            throw new IOException ("Unable to find record header for LECCMachineFunction.");
        }
        
        LECCMachineFunction mf = new LECCMachineFunction();
        mf.readContent(s, rhi.getSchema(), mti, msgLogger);
        s.skipRestOfRecord();
        
        return mf;
    }
    
    /** 
     * Clear the entry in the functionNameToFunctionGroup map
     * associated with the provided name.
     * @param name
     */
    void clearFunctionGroupInfo (String name) {
        functionNameToFunctionGroup.remove(name);
    }
    
    /**
     * Associate a function group with a function.
     * @param functionName
     * @param fgi
     */
    void setFunctionGroupInfo (String functionName, FunctionGroupInfo fgi) {
        functionNameToFunctionGroup.put(functionName, fgi);
    }
    
    /**
     * Retrieve the function group info for the named function.
     * If the function is in a different module the call will
     * be passed to the appropriate module..
     * @param functionName
     * @return FunctionGroupInfo if successful, null otherwise.
     */
    FunctionGroupInfo getFunctionGroupInfo (QualifiedName functionName) {
        MachineFunction mf = getFunction(functionName);
        if (mf != null) {
            return getFunctionGroupInfo(mf);
        } 
        
        if (functionName.getModuleName().equals(getName())) {
            return functionNameToFunctionGroup.get(functionName.getUnqualifiedName());
        }
        return null;
    }

    
    /**
     * Retrieve the function group info for the provided Function.
     * If the function is in a different module the call will
     * be passed to the appropriate module..
     * @param mf - MachineFunction of interest.
     * @return FunctionGroupInfo if successful, null otherwise.
     */
    FunctionGroupInfo getFunctionGroupInfo (MachineFunction mf) {
        if (mf.getQualifiedName().getModuleName().equals(getName())) {
            
            FunctionGroupInfo fgi = functionNameToFunctionGroup.get(mf.getName());
            if (fgi == null) {
                if (coarseGrouping) {
                    
                    if (mf.isPrimitiveFunction() ||
                        mf.isDataConstructor() ||
                        mf.isForAdjunct()) {

                        List<MachineFunction> machineFunctions = new ArrayList<MachineFunction>();
                        machineFunctions.add(mf);
                        
                        fgi = new FunctionGroupInfo(this, machineFunctions);
                    } else {
                        // Group the non-primitive functions into a single group.
                         
                        List<MachineFunction> machineFunctions = new ArrayList<MachineFunction>();
                        
                        for (final MachineFunction mf2 : getFunctions()) {
                            
                            if (mf2.isDataConstructor()) {
                                continue;
                            }
    
                            if (mf2.getAliasOf() != null || mf2.getLiteralValue() != null) {
                                continue;
                            }
                            
                            // Check to see if this is a primitive function.
                            // Functions marked as primitive can be one of two things:
                            // 1) a primitive function implemented as a machine specific operator
                            // 2) a primitive function for which a hard coded machine specific implementation is provided.
                            // If the function falls into category two we don't want to generate anything for it.
                            if (mf2.isPrimitiveFunction()) {
                                continue;
                            }
                            
                            if (mf.isForAdjunct() != mf2.isForAdjunct()) {
                                continue;
                            }
                            
                            machineFunctions.add(mf2);
                        }
                        
                        fgi = new LECCModule.FunctionGroupInfo(this, machineFunctions);
                        for (final MachineFunction mf2 : machineFunctions) {                           
                            functionNameToFunctionGroup.put(mf2.getName(), fgi);
                        }
                    }
                } else {
                    List<MachineFunction> machineFunctions = new ArrayList<MachineFunction>();
                    Set<String> connectedComponents = mf.getStronglyConnectedComponents();
                    for (final String functionName : connectedComponents) {
                        MachineFunction mf2 = getFunction(functionName);
                        machineFunctions.add(mf2);
                    }
                    
                    fgi = new LECCModule.FunctionGroupInfo(this, machineFunctions);
                    for (final MachineFunction mf2 : machineFunctions) {                       
                        functionNameToFunctionGroup.put(mf2.getName(), fgi);
                    }
                }
            }
            
            return fgi;
        } else {
            LECCModule m = (LECCModule)findModule (mf.getQualifiedName().getModuleName());
            if (m != null) {
                return m.getFunctionGroupInfo(mf);
            }
        }

        // No label or other horridness
        return null;

    }

    /**
     * Class used to provide information about grouped functions.
     * Functions are grouped for optimal generation of java code.
     * Minimally, closely connected functions will always be in
     * a function group.  Beyond that clients can construct various
     * grouping scenarios based on function usage, etc.
     * @author rcypher
     *
     */
    static class FunctionGroupInfo {
        /** (String -> MachineFunction) the functions in this group. */
        private final Map<String, MachineFunction> machineFunctions = new LinkedHashMap<String, MachineFunction>();

        /** (String -> MachineFunction) the lifted letvar definition functions in this group. */
        private final Map<String, MachineFunction> liftedLetVarMachineFunctions = new LinkedHashMap<String, MachineFunction>();
        
        /** The module containing this function group. */
        private final LECCModule module;
        
        /** Number of CAFs in ths group. */
        private int nCAFs = 0;
        
        /** Number of zero arity functions which are not CAFs. */
        private int nZeroArityFunctions = 0;
        
        /** (Integer -> Integer) function arity to number of 
         * functions in the group with that arity. */
        private Map<Integer, Integer> arityToCount = new HashMap<Integer, Integer>();
        
        /** (String -> Integer) Map of function name to ordinal within the group. */
        private Map<String, Integer> nameToOrdinal = new HashMap<String, Integer>();

        /** (Integer -> String) Map of function ordinal within group to name. */
        private Map<Integer, String> ordinalToName = new HashMap<Integer, String>();
        
        /** Name of this function group.  Used to name the corresponding java class. */
        private String functionGroupName = null;
        
        /** Qualified name of this function group. Used to name the corresponding java class. */
        private QualifiedName functionGroupQualifiedName = null;
        
        /** String -> (Set of String).  Function name to let variable
         * functions lifted from the named function.
         */
        private final Map<String, Set<String>> functionNameToLiftedFunctions = new HashMap<String, Set<String>>();
        
        private FunctionGroupInfo (LECCModule module, List<MachineFunction> machineFunctions) {
            Collections.sort(machineFunctions, new MachineFunctionComparator());
            for (final MachineFunction mf : machineFunctions) {               
                addFunction(mf);
            }
            
            this.module = module;
        }
        
        boolean contains (String functionName) {
            return machineFunctions.get(functionName) != null ||
                   liftedLetVarMachineFunctions.get(functionName) != null; 
        }

        /** 
         * Add a lifted let variable definition function.
         * @param letVarFunction
         * @param originatingFunction
         */
        void addLiftedLetVarFunction (MachineFunction letVarFunction, String originatingFunction) {
            this.liftedLetVarMachineFunctions.put(letVarFunction.getName(), letVarFunction);
            Set<String> cl = this.functionNameToLiftedFunctions.get(originatingFunction);
            if (cl == null) {
                cl = new HashSet<String>();
                this.functionNameToLiftedFunctions.put(originatingFunction, cl);
            }
            cl.add(letVarFunction.getName());
        }
        
        /**
         * @param originatingFunction
         * @return Set of String or null
         */
        Set<String> getLiftedFunctionsFor(String originatingFunction) {
            return this.functionNameToLiftedFunctions.get(originatingFunction);
        }
        
        private void addFunction (MachineFunction mf) {
            this.machineFunctions.put(mf.getName(), mf);
            if (mf.isCAF()) {
                nCAFs++;
            } else
                if (mf.getArity() == 0) {
                    nZeroArityFunctions++;
                }

            Integer arity = Integer.valueOf(mf.getArity());
            Integer count = arityToCount.get(arity);
            if (count == null) {
                count = Integer.valueOf(1);
            } else {
                count = Integer.valueOf(count.intValue()+1);
            }
            arityToCount.put(arity, count);
            
            Integer index = Integer.valueOf(machineFunctions.size()-1);
            
            nameToOrdinal.put(mf.getName(), index);
            ordinalToName.put(index, mf.getName());
            
            if (index.intValue() == 0) {
                functionGroupName = mf.getName();
                functionGroupQualifiedName = mf.getQualifiedName();
            }
        }
        
        int getNFunctions() {
            return machineFunctions.size();
        }
        
        boolean includesCAFs () {
           return nCAFs > 0;
        }
        
        boolean includesZeroArityFunctions () {
            return nZeroArityFunctions > 0;
        }
        
        void setCodeGenerated (boolean b) {
            for (final MachineFunction machineFunction : machineFunctions.values()) {
                MachineFunctionImpl mf = (MachineFunctionImpl)machineFunction;
                mf.setCodeGenerated(b);
            }            
        }
        
        /**
         * @return Collection of MachineFunction
         */
        Collection<MachineFunction> getTopLevelCALFunctions () {
            return Collections.unmodifiableCollection(machineFunctions.values());
        }
        
        /**
         * @return Collection of MachineFunction
         */
        Collection<MachineFunction> getLiftedLetVarDefFunctions () {
            return Collections.unmodifiableCollection(liftedLetVarMachineFunctions.values());
        }
        
        MachineFunction getMachineFunction (String name) {
            MachineFunction mf =  machineFunctions.get(name);
            if (mf == null) {
                mf = liftedLetVarMachineFunctions.get(name);
            }
            return mf;
        }
        
        String getFunctionGroupName() {
            return functionGroupName;
        }
        
        QualifiedName getFunctionGroupQualifiedName() {
            return functionGroupQualifiedName;
        }
        
        /**
         * If this class represents just one CAL function we can just name the
         * function 'f...'. Otherwise we need to differentiate between the 'f'
         * methods for the different CAL functions by prefixing the CAL function
         * name. If this class represents more than one CAL function a 'f'
         * method will be generated which will switch to the appropriate ..._f
         * method based on the SC tag.
         */
        String getFNamePrefix(String scName) {

            if (machineFunctions.size() > 1) {
                return CALToJavaNames.cleanSCName(scName) + "_";
            }
            return "";
        }
        
        String getFnNamePrefix (String scName) {
            return getFNamePrefix(scName);   
        }
        
        
        /**         
         * @return function arity to number of functions in the group with that arity.
         */
        Map<Integer, Integer> getArityToCountMap () {
            return arityToCount;
        }
        
        int getNCAFs () {return nCAFs;}
        int getNZeroArityFunctions ()  {return nZeroArityFunctions;}
        
        int getFunctionIndex (String scName) {
            Integer index = nameToOrdinal.get(scName);
            if (index == null) {
                throw new NullPointerException("Invalid SC name " + scName + " in FunctionGroupInfo.getFunctionIndex().");
            }
            
            return index.intValue();
        }
        
        String getFunctionNameFromIndex (int index) {
            String name = ordinalToName.get(Integer.valueOf(index));
            if (name == null) {
                throw new NullPointerException("Invalid SC ordinal " + index + " in FunctionGroupInfo.getFunctionIndex().");
            }
            
            return name;
        }
        
        LECCModule getModule ()  {
            return module;
        }
        
        @Override
        public String toString () {
            StringBuilder sb = new StringBuilder();
            sb.append(getFunctionGroupName());
            sb.append(": ");
            for (final String functionName : machineFunctions.keySet()) {
                sb.append(functionName);
                sb.append(", ");
            }
            sb.append ("nCafs = " + nCAFs);
            return sb.toString();
        }
        
        private static class MachineFunctionComparator implements Comparator<MachineFunction> {
            /** {@inheritDoc}*/
            public int compare (MachineFunction mf1, MachineFunction mf2) {
                int compare = mf1.getArity() - mf2.getArity();
                if (compare == 0) {
                    compare = (mf1).getName().compareTo((mf2).getName());
                }
                return compare;
            }
                      
            public boolean equals (Object o1, Object o2) {
                if (((MachineFunction)o1).getArity() == ((MachineFunction)o2).getArity()) {
                    return ((MachineFunction)o1).getName().equals(((MachineFunction)o2).getName());
                }
                return false;
            }            
        }
    }

    /**
     * Encapsulates the one-to-one mapping from an unqualified function or type
     * name to a name which is suitable for use in Java source or the file
     * system.
     * 
     * Note that this class is thread-safe, with the exception that to atomically
     * query the existing mapping and then add a new mapping, the instance
     * must be explicitly synchronized, surrounding both the 
     * {@link LECCModule.ClassNameMapper#getOriginalName}/{@link LECCModule.ClassNameMapper#getFixedName}
     * call and the {@link LECCModule.ClassNameMapper#addMapping} call.
     * 
     * @author Joseph Wong
     * @author Edward Lam, Raymond Cypher (original code in CALToJavaNames)
     */
    static final class ClassNameMapper {
        
        /**
         * A Map<String, String> mapping original names to their corresponding fixed names.
         * This map is kept in sync with {@link LECCModule.ClassNameMapper#fixedNamesToOriginalNames}.
         */
        private final Map<String, String> originalNamesToFixedNames = new HashMap<String, String>();
        
        /**
         * A Map<String, String> mapping fixed names to their corresponding original names.
         * This map is kept in sync with {@link LECCModule.ClassNameMapper#originalNamesToFixedNames}.
         */
        private final Map<String, String> fixedNamesToOriginalNames = new HashMap<String, String>();
        
        /**
         * Returns the fixed name corresponding to the given original name.
         * @param originalName
         * @return the fixed name corresponding to originalName.
         */
        synchronized String getFixedName(String originalName) {
            return originalNamesToFixedNames.get(originalName);
        }
        
        /**
         * Returns the original name corresponding to the given fixed name.
         * @param fixedName
         * @return the original name corresponding to fixedName.
         */
        synchronized String getOriginalName(String fixedName) {
            return fixedNamesToOriginalNames.get(fixedName);
        }
        
        /**
         * Adds an original name to fixed name mapping. Note that to atomically query the mapping
         * and add a new one as necessary, one needs to synchronize on the instance of {@link LECCModule.ClassNameMapper}
         * explicitly.
         * 
         * @param originalName
         * @param fixedName
         */
        synchronized void addMapping(String originalName, String fixedName) {
            originalNamesToFixedNames.put(originalName, fixedName);
            fixedNamesToOriginalNames.put(fixedName, originalName);
        }
        
        /**
         * Writes out this instance to the RecordOutputStream.
         * @param s the RecordOutputStream.
         * @throws IOException 
         */
        private synchronized void write(RecordOutputStream s) throws IOException {
            s.writeIntCompressed(originalNamesToFixedNames.size());
            for (final Map.Entry<String, String> entry : originalNamesToFixedNames.entrySet()) {               
                s.writeUTF(entry.getKey());
                s.writeUTF(entry.getValue());
            }
        }
        
        /**
         * Replaces the state of this instance with the state read from the RecordInputStream.
         * @param s the RecordInputStream.
         * @throws IOException 
         */
        private synchronized void readContent(RecordInputStream s) throws IOException {
            originalNamesToFixedNames.clear();
            fixedNamesToOriginalNames.clear();
            
            int nEntries = s.readIntCompressed();
            for (int i = 0; i < nEntries; i++) {
                String originalName = s.readUTF();
                String fixedName = s.readUTF();
                originalNamesToFixedNames.put(originalName, fixedName);
                fixedNamesToOriginalNames.put(fixedName, originalName);
            }
        }
    }


}
