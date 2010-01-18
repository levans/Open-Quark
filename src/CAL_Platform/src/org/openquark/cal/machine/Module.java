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
 * Module.java
 * Creation date: (March 6, 2000)
 * By: Luke Evans
 */
package org.openquark.cal.machine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.MachineFunctionImpl;
import org.openquark.cal.internal.machine.lecc.LECCModule;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;


/**
 * Warning- this class should only be used by the CAL runtime implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * Module collects together machine specific information about the contents of 
 * a module and associates it with the MoudleTypeInfo for that module.
 * Modules contain the executable program elements and have a name.
 * Creation date: (3/6/00 9:48:37 AM)
 * @author LWE
 */
abstract public class Module {
    
    public static final String COMPILED_MODULE_SUFFIX = "cmi";
    
    /**
     * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
     * the {@link #loadGeneratedCodeInfo} method.
     */
    private static final short[] GENERATED_CODE_INFO_RECORD_TAGS = new short[] {
        ModuleSerializationTags.LECC_GENERATED_CODE_INFO,
        ModuleSerializationTags.G_GENERATED_CODE_INFO
    };

    private static final int moduleSerializationSchema = 0;
    private static final int importSerializationSchema = 1;
    private static final int generatedCodeInfoSerializationSchema = 0;
    
    /**
     * The array of possible record tags used in calls to {@link RecordInputStream#findRecord(short[])} by
     * the {@link #load} method.
     */
    private static final short[] MODULE_RECORD_TAGS = new short[] {
        ModuleSerializationTags.LECC_MODULE,
        ModuleSerializationTags.G_MODULE
    };
    
    /** A lock for guarding access to {@link #functionNameToFunctionMap} and {@link #adjunctFunctionNameToFunctionMap}. */
    // Note: this lock is private, and is not meant to be used by subclasses for their own locking needs.
    private final int[] lock = new int[0];

    /** Information about the public entities exported by the module */
    private final ModuleTypeInfo moduleTypeInfo;
  
    /** (String -> MachineFunction) the program entities */
    /* @GuardedBy("lock") */
    private final Map<String, MachineFunction> functionNameToFunctionMap;

    /** (String -> MachineFunction) any adjunct entities in this module. */
    /* @GuardedBy("lock") */
    private transient Map<String, MachineFunction> adjunctFunctionNameToFunctionMap;
    
    /** The classloader to use to resolve foreign classes for this module. */
    private final ClassLoader foreignClassLoader;

    /** The GeneratedCodeInfo for the persisted form of this module. 
     *  This is set under these circumstances:
     *  1) During deserialization, this is set directly in the constructor.
     *  2) During compilation, this is set when CodeGenerator.GeneratedCodeInfo.write() is called.
     *  
     *  ie. if this module has been loaded or compiled to a persisted form, this member should always be set.
     *  Otherwise null.
     */
    private GeneratedCodeInfo generatedCodeInfo;
    
    /**
     * Constructor for a Module.
     *
     * @param name the name of the module
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     */
    protected Module(ModuleName name, ClassLoader foreignClassLoader) {
        if (name == null || foreignClassLoader == null) {
            throw new NullPointerException();
        }
        
        this.foreignClassLoader = foreignClassLoader;
        functionNameToFunctionMap = new HashMap<String, MachineFunction>();
        adjunctFunctionNameToFunctionMap = new HashMap<String, MachineFunction>();
        moduleTypeInfo = new ModuleTypeInfo(name, this);
    }

    /**
     * Constructor for a Module.
     *
     * @param name the name of the module
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     * @param generatedCodeInfo the CodeGenerator.GeneratedCodeInfo associated with this module, or null if none.
     */
    protected Module(ModuleName name, ClassLoader foreignClassLoader, GeneratedCodeInfo generatedCodeInfo) {
        this(name, foreignClassLoader);
        this.generatedCodeInfo = generatedCodeInfo;
    }
    
    public final void clearAdjunctFunctions () {
        synchronized (lock) {
            adjunctFunctionNameToFunctionMap = new HashMap<String, MachineFunction>();
        }
    }
    
    /**
     * Add a MachineFunction to this Module.
     * New function objects replace old ones with the same name.
     * @param machineFunction the label to add
     */
    public final void addFunction(MachineFunction machineFunction) {        
        synchronized (lock) {
            if (machineFunction.isForAdjunct()) {
                adjunctFunctionNameToFunctionMap.put(machineFunction.getName(), machineFunction);
            } else {
                functionNameToFunctionMap.put(machineFunction.getName(), machineFunction);
            }
        }
    }
    
    /**
     * Fetch a function by name.
     * @param functionName
     * @return the MachineFunction instance for the named function.  Null if it doesn't exist.
     */
    public MachineFunction getFunction (String functionName) {
        // Check functions in the module and adjunct both.
        synchronized (lock) {
            MachineFunction mf = functionNameToFunctionMap.get(functionName);
            if (mf == null) {
                mf = adjunctFunctionNameToFunctionMap.get(functionName);
            }

            return mf;
        }
    }

    /**
     * Fetch a label by name.
     * The label can be in this module, in an imported module, in an import of an imported module, etc.
     * @param functionName - the qualified name of a function
     * @return the MachineFunction instance for the named function.  Null if it doesn't exist.
     */
    public MachineFunction getFunction (QualifiedName functionName) {
        synchronized (lock) {
            if (functionName.getModuleName().equals(getName())) {
                return getFunction(functionName.getUnqualifiedName());
            } else {
                Module m = findModule (functionName.getModuleName());
                if (m != null) {
                    return m.getFunction(functionName);
                }
            }

            // No label or other horridness
            return null;
        }
    }
    
    /**
     * 
     * @param moduleName
     * @return The named module, or null if it cannot be referenced from this module 
     *   (ie. it is not this module, one of its imports, an import of its imports, etc..)
     */
    public final Module findModule (ModuleName moduleName) {
        if (this.getName().equals (moduleName)) {
            return this;
        }
        
        ModuleTypeInfo foundTypeInfo = getModuleTypeInfo().getDependeeModuleTypeInfo(moduleName);
        return foundTypeInfo == null ? null : foundTypeInfo.getModule();
    }
    
    /**
     * Return a <b>copy</b> of the functions in this module.
     * 
     * @return a copy of the functions in this module in a new Collection object.
     */
    public final Collection<MachineFunction> getFunctions () {
        /*
         * THREAD-SAFETY ISSUE:
         * this needs to be a copy, and not an unmodifiable view based on the underlying
         * collection (e.g. via Collections.unmodifiableCollection()), because iterating through
         * such a list is incompatible with concurrent modification (it would result in a
         * java.util.ConcurrentModificationException). Such a scenario will occur
         * with compilations taking place simultaneously on multiple threads.
         */
        
        synchronized (lock) {
            List<MachineFunction> l =  new ArrayList<MachineFunction> (functionNameToFunctionMap.values());
            if(adjunctFunctionNameToFunctionMap.size() > 0) {
                l.addAll(adjunctFunctionNameToFunctionMap.values());
            }

            return l;
        }
    }
    
    /**
     * @return The number of functions currently in this module.
     */
    public final int getNFunctions() {
        synchronized (lock) {
            return functionNameToFunctionMap.size() + adjunctFunctionNameToFunctionMap.size();
        }
    }

    /**
     * Get the name of the module.
     * @return the name of the module
     */
    public final ModuleName getName() {
        return moduleTypeInfo.getModuleName();
    }
   
    public final ModuleTypeInfo getModuleTypeInfo() {
        return moduleTypeInfo;
    }

    /**
     * Returns true if this module depends on the named module.
     * @param moduleName
     * @return boolean
     */
    public final boolean dependsOn (ModuleName moduleName) {
        return (moduleTypeInfo.getImportedModule(moduleName) != null);
    }
    
    /**
     * Disassemble the code in the Program.
     * @return a string representation of the code
     */
    @Override
    public final String toString() {

        StringBuilder sb = new StringBuilder("module ");
        sb.append(getName());
        sb.append('\n');

        // For each code contribution
        synchronized (lock) {
           
            for (final Map.Entry<String, MachineFunction> entry : functionNameToFunctionMap.entrySet()) {
                MachineFunction label = entry.getValue();               
                sb.append (label.toString() + "\n");
            }
        }

        return sb.toString();
    }
    
    /**
     * Write out the imported modules to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    private void writeImports (RecordOutputStream s) throws IOException {
        // Write out a record that contains the module name
        // and the names of all the modules it depends on.
        s.startRecord(ModuleSerializationTags.MODULE_IMPORTS, importSerializationSchema);
        s.writeModuleName(getName());
        int nImports = moduleTypeInfo.getNImportedModules();
        s.writeInt(nImports);
        for (int i = 0; i < nImports; ++i) {
            ModuleTypeInfo mti = moduleTypeInfo.getNthImportedModule(i);
            s.writeModuleName(mti.getModuleName());
        }
        s.endRecord();
        
    }
    
    /**
     * Write meta information about the serialized module.
     * Timestamp of serialization, information about the generated code (e.g. version), etc.
     * @param s
     * @throws IOException
     */
    private void writeSerializationInfo (RecordOutputStream s) throws IOException {
        // The usual pattern is to write the record for the subclass first, followed by the
        // record for the superclass.  However, in this case we write the superclass record
        // first (ie, SERIALIZATION_INFO before LECC_GENERATED_CODE_INFO or G_GENERATED_CODE_INFO)
        // because CompiledModuleSourceDefinition.getSerializedTimestamp needs to be able to skip
        // straight to the SERIALIZATION_INFO record.
        //
        // So, if you change the format of the SERIALIZATION_INFO record, you will probably need to change
        // CompiledModuleSourceDefinition.getSerializedTimestamp and LECCModule.readLeccSpecificSerializationInfo
        // as well.
        s.startRecord(ModuleSerializationTags.SERIALIZATION_INFO, generatedCodeInfoSerializationSchema);
        s.writeLong(System.currentTimeMillis());
        writeMachineSpecificSerializationInfo(s);
        s.endRecord();
    }

    /** 
     * Write out machine-specific meta information about the serialized module
     * @param s
     * @throws IOException
     */
    abstract protected void writeMachineSpecificSerializationInfo(RecordOutputStream s) throws IOException;
    
    /**
     * Write out the content specific to the Module class.
     * @param s
     * @throws IOException
     */
    protected void writeContent (RecordOutputStream s) throws IOException {
        s.startRecord (ModuleSerializationTags.MODULE, moduleSerializationSchema);
        moduleTypeInfo.write (s);
        
        synchronized (lock) {
            s.writeInt(functionNameToFunctionMap.size());
           
            for (final Map.Entry<String, MachineFunction> entry : functionNameToFunctionMap.entrySet()) { 
                
                MachineFunctionImpl mf = (MachineFunctionImpl)entry.getValue();
                mf.write (s);
            }
        }
        s.endRecord ();
    }
    
    /**
     * Write this Module instance out to the RecordOutputStream.
     * @param s
     * @throws IOException
     */
    public void write (RecordOutputStream s) throws IOException {
        // Write out the information about the generated code in
        // this module.
        writeSerializationInfo(s);
        
        // Write out the imports information.
        writeImports(s);
        
        // Now delegate to the derived class.
        writeActual(s);
    }
    
    /**
     * Write out the Module instance.
     * @param s
     * @throws IOException
     */
    abstract protected void writeActual (RecordOutputStream s) throws IOException;
    
    /**
     * Read the contents of this Module, specific to the Module class, from the RecordInputStream.
     * The read position will be before the Module record header.
     * @param s
     * @param otherModules
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    protected void readContent (RecordInputStream s, Map<ModuleName, Module> otherModules, CompilerMessageLogger msgLogger) throws IOException {
        
        // At this point we should be at the beginning of the Module record.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.MODULE);
        if (rhi == null) {
            throw new IOException ("Unable to find Module record.");
        }
        if (rhi.getSchema() > moduleSerializationSchema) {
            throw new IOException("Saved schema is greather than current schema in Module.");
        }
        
        
        moduleTypeInfo.readContent(s, otherModules, msgLogger);
        
        synchronized (lock) {
            int nFunctions = s.readInt();
            for (int i = 0; i < nFunctions; ++i) {
                MachineFunction mf = loadMachineFunction(s, getModuleTypeInfo(), msgLogger);
                functionNameToFunctionMap.put(mf.getName(), mf);
            }
        }
        
        s.skipRestOfRecord();
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
    protected abstract MachineFunction loadMachineFunction(RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException;
        
    
    /**
     * Read module import information and return a Set of dependee module names.
     * @param s
     * @return Set of ModuleName representing the imported modules.
     * @throws IOException
     */
    public static Set<ModuleName> readDependencies (RecordInputStream s) throws IOException {
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.MODULE_IMPORTS);
        if (rhi == null) {
            throw new IOException ("Unable to find module dependencies record.");
        }
        if (rhi.getSchema() > importSerializationSchema) {
            throw new IOException("Saved schema is greather than current schema in Module.");
        }
        if (rhi.getSchema() < importSerializationSchema) {
            throw new IOException("Saved schema is less than current schema in Module. Earlier import serialization schemas are not supported.");
        }
        /* ModuleName moduleName = */ s.readModuleName();
        int nImports = s.readInt();
        Set<ModuleName> set = new HashSet<ModuleName>();
        for (int i = 0; i < nImports; ++i) {
            ModuleName imported = s.readModuleName();
            set.add(imported);
        }
        
        s.skipRestOfRecord();
        
        return set;
    }

    /**
     * @return whether Expressions must be loaded during deserialization.
     * For instance, this will be false if usable generated code exists in the resource repository.
     */
    public abstract boolean mustLoadExpressions();
    
    /**
     * Read the generated code info from the RecordInputStream.
     * @param s
     * @return the module-specific GeneratedCodeInfo.
     * @throws IOException
     */
    public static final GeneratedCodeInfo loadGeneratedCodeInfo(RecordInputStream s) throws IOException {
        // Load the record header and determine which class this is.
        RecordHeaderInfo rhi = s.findRecord(GENERATED_CODE_INFO_RECORD_TAGS);
        if (rhi == null) {
            throw new IOException ("Unable to find module record.");
        }

        if (rhi.getRecordTag() == ModuleSerializationTags.LECC_GENERATED_CODE_INFO) {
            return LECCModule.loadCodeInfo(s, rhi.getSchema());  // read the module name and the generated code info.
        } else 
            if (rhi.getRecordTag() == ModuleSerializationTags.G_GENERATED_CODE_INFO) {
                throw new IOException ("Loading of g-machine specific compiled modules is not yet supported.");
            } else {
                throw new IOException ("Unexpected record tag found in Module.read: " + rhi.getRecordTag());
            }
    }
    
    /**
     * Load one of the concrete subclasses of Module.
     * @param s
     * @param loadedModules - map of module names to loaded modules.
     * @param foreignClassLoader the classloader to use to resolve foreign classes for the module.
     * @param generatedCodeInfo The GeneratedCodeInfo for the persisted form of this module.
     * @param msgLogger the logger to which to log deserialization messages.
     * This should be used to log user (non-internal) errors only.
     * @return the new Module instance.
     * If there was a problem reading the module, null is returned and errors will be logged to the msg logger.
     * 
     * @throws IOException if any internal errors occurred while loading the module.
     */
    public final static Module load (RecordInputStream s, Map<ModuleName, Module> loadedModules, ClassLoader foreignClassLoader, 
            GeneratedCodeInfo generatedCodeInfo, CompilerMessageLogger msgLogger) throws IOException {
        
        // Load the record header and determine which class this is.
        RecordHeaderInfo rhi = s.findRecord(MODULE_RECORD_TAGS);
        if (rhi == null) {
            throw new IOException ("Unable to find module record.");
        }
        
        if (rhi.getRecordTag() == ModuleSerializationTags.LECC_MODULE) {
            return LECCModule.load(s, rhi.getSchema(), loadedModules, foreignClassLoader, generatedCodeInfo, msgLogger);
        } else 
        if (rhi.getRecordTag() == ModuleSerializationTags.G_MODULE) {
            throw new IOException ("Loading of g-machine specific compiled modules is not yet supported.");
        } else {
            throw new IOException ("Unexpected record tag found in Module.read: " + rhi.getRecordTag());
        }
    }
    
    /**
     * @return a Set of String which contains the names of all direct
     *     and indirect dependee modules.
     */
    public final Set<ModuleName> getDependeeModuleNames () {
        return getDependeeModuleNames (new HashSet<ModuleName>());
    }
    
    /**
     * Build up a list of module names for all direct and 
     * indirect dependee modules.
     * @param names - The set of dependee module names thus far
     * @return Set of String, all direct and indirect dependee module names.
     */
    private final Set<ModuleName> getDependeeModuleNames (Set<ModuleName> names) {
        for (int i = 0, n = getModuleTypeInfo().getNImportedModules(); i < n; ++i) {
            ModuleTypeInfo importedMTI = getModuleTypeInfo().getNthImportedModule(i);
            if (!names.contains(importedMTI.getModuleName())) {
                names.add(importedMTI.getModuleName());
                importedMTI.getModule().getDependeeModuleNames(names);
            }
        }
        return names;
    }

    
    /**
     * @return Returns the classloader to use to resolve foreign classes for this module.
     * Never null.
     */
    public final ClassLoader getForeignClassLoader() {
        return foreignClassLoader;
    }
    
    /**
     * @return the generatedCodeInfo, or null if this module has not been persisted to or loaded from the repository.
     */
    public final GeneratedCodeInfo getGeneratedCodeInfo() {
        return generatedCodeInfo;
    }
    
    /**
     * @param generatedCodeInfo the generatedCodeInfo to set
     */
    protected final void setGeneratedCodeInfo(GeneratedCodeInfo generatedCodeInfo) {
        this.generatedCodeInfo = generatedCodeInfo;
    }
}