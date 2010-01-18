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
 * CALClassLoader.java
 * Created: Dec. 13, 2004
 * By: RCypher
 */
package org.openquark.cal.internal.machine.lecc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.internal.javamodel.JavaClassRep;
import org.openquark.cal.internal.machine.CodeGenerationException;
import org.openquark.cal.internal.machine.lecc.LECCModule.FunctionGroupInfo;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.internal.runtime.lecc.RTCAF;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.runtime.ExecutionContext;


/**
 * A class loader for loading generated class files for a module from specific directories.
 * This class can function in two modes.  1) loading classes for the adjunct associated with
 * the module, 2) loaded regular module classes.
 * The factory method for creating an instance of CALClassLoader forces the creation of a pair
 * of class loaders.  One for module classes and the other for adjunct classes, with the module
 * loader being the parent of the adjunct loader.
 * Class loading by the runtime should always be initialized via the adjunct loader.
 * 
 * <P>This classloader deviates from the regular delegation model where parents are checked for a class before children.
 * CALClassLoader.loadClass() checks for a class itself before delegating to its parent's loadClass() method.  The parent
 * may or may not follow the regular delegation model (depending mostly on whether or not the parent is another instance
 * of CALClassLoader).
 */
final class CALClassLoader extends ClassLoader {
    private static final boolean DEBUG_OUTPUT = false;
    
    static final boolean PERFORM_TIMING = false;
    
    private static final boolean DUMP_LOADED_CLASS_NAMES = false;

    /** The file extension for generated class files. */
    private static final String CLASS_FILE_EXTENSION = ".class";
    
    /** The file extension for compressed class files. */
    private static final String COMPRESSED_CLASS_FILE_EXTENSION = ".lc";
    
    
    /** A map of String (module name) -> CALClassLoader.  This holds class loaders for any modules on which this module depends. 
     * Should always be empty for an adjunct loader. 
     * */
    private final Map<ModuleName, CALClassLoader> dependeeModuleLoaders;

    /** The locator for the module class file parent.  This locator will point to either the module base folder, or a module .jar file. */
    private ProgramResourceLocator moduleParentLocator;

    /** The repository in which the class files are found. */
    private ProgramResourceRepository resourceRepository;
    
    /** The module for which the classloader is responsible for loading classes. */ 
    private final LECCModule module;
    
    /** classes which are generated for an adjunct to the module. 
     * This should always be empty for a module loader. */
    private final Set<String> adjunctClasses;
    
    /** A list used to track the classes already loaded by this class loader. 
     * Note: We use a vector because it is a synchronized list. */
    private final List<Class<?>> loadedClasses = new Vector<Class<?>>();

    /** Flag indicating if this class loader is responsible for adjunct classes. */
    private final boolean adjunctLoader;

    /** The inflater object.  
     * Do not create and free as needed, as this may lead to OutOfMemoryErrors if too many are created
     * (Sun bug id 4797189). */
    private final Inflater inflater = new Inflater();
    
    /** The number of classes loaded by this class loader. */
    private int nClassesLoaded = 0;
    
    /** The number of bytes loaded by this class loader. */
    private int nBytesLoaded = 0;
    
    /** The number of milliseconds spent in generating class file data. */
    private long generateClassDataTimeMS = 0;
    
    /** The number of milliseconds spent in looking up class file data. */
    private long lookupClassDataTimeMS = 0;
    
    /** The number of milliseconds spent in findClass(). */
    private long findClassTimeMS = 0;

    /*
     * The following are objects which are relatively expensive to construct, are not small, and cannot be accessed synchronously.
     */

    /**
     * A thread-local ByteArrayOutputStream.
     * Used by lookupClassData() and loadFileData().
     */
    private static ThreadLocal<ByteArrayOutputStream> threadLocalByteArrayOutputStream = new ThreadLocal<ByteArrayOutputStream>() {
        @Override
        protected synchronized ByteArrayOutputStream initialValue() {
            // Most classes are <4K.
            return new ByteArrayOutputStream(4096);
        }
    };
    /**
     * A thread-local byte array.
     * Used by lookupClassData() and loadFileData().
     */
    private static ThreadLocal<byte[]> threadLocalByteArray = new ThreadLocal<byte[]>() {
        @Override
        protected synchronized byte[] initialValue() {
            // Most classes are <4K.
            return new byte[4096];
        }
    };

    /**
     * Factory method to create a new CALClassLoader.
     * In actuality a pair of class loaders will be created.  One for the module and one for the adjunct, with
     * the module loader being the parent of the adjunct loader.  The adjunct loader is then returned.
     * @param resourceRepository
     * @param moduleParentLocator
     * @param parent
     * @param module
     * @param dependeeModules
     * @return and instance of CALClassLoader.
     */
    static CALClassLoader makeCALClassLoader (
            ProgramResourceRepository resourceRepository,
            ProgramResourceLocator moduleParentLocator, 
            ClassLoader parent,
            LECCModule module,
            List<LECCModule> dependeeModules) {//throws IOException {
        CALClassLoader moduleLoader = new CALClassLoader (resourceRepository, moduleParentLocator, parent, module, dependeeModules);
        return new CALClassLoader (resourceRepository, moduleParentLocator, moduleLoader, module);
    }
    
    /**
     * Create a new adjunct loader based on an existing adjunct loader.
     * @param loader - the current adjunct loader
     * @return a new adjunct loader
     */
    static CALClassLoader resetAdjunctClasses (CALClassLoader loader) {
        if (!loader.adjunctLoader) {
            throw new IllegalArgumentException("Cannot reset adjunct classes on a non-adjunct classloader.");
        }
        loader.adjunctClasses.clear();
        return new CALClassLoader(loader.resourceRepository, loader.moduleParentLocator, (CALClassLoader)loader.getParent(), loader.module);
    }
    
    private CALClassLoader (
            ProgramResourceRepository resourceRepository,
            ProgramResourceLocator moduleParentLocator,
            ClassLoader parent,
            LECCModule module,
            List<LECCModule> dependeeModules) {
        super (parent);
        this.moduleParentLocator = moduleParentLocator;
        this.resourceRepository = resourceRepository;
        this.module = module;
        
        // Note: synchronization is unnecessary, as the table is only modified in the constructor.
        dependeeModuleLoaders = new HashMap<ModuleName, CALClassLoader>();
        
        ModuleName moduleName = module.getName();
        for (int i = 0, nDependeeModules = dependeeModules.size(); i < nDependeeModules; ++i) {
            LECCModule m = dependeeModules.get(i);
            if (m.getName().equals(moduleName)) {
                continue;
            }
            
            // m.getClassLoader() returns the adjunct classloader...
            CALClassLoader mcl = (CALClassLoader)m.getClassLoader().getParent();
            dependeeModuleLoaders.put (mcl.getModuleName(), mcl);
        }
        
        this.adjunctLoader = false;
        adjunctClasses = Collections.<String>emptySet();
    }
    
    private CALClassLoader (ProgramResourceRepository resourceRepository, ProgramResourceLocator moduleParentLocator, CALClassLoader parent, LECCModule module) {
        super (parent);
        this.moduleParentLocator = moduleParentLocator;
        this.resourceRepository = resourceRepository;
        this.adjunctLoader = true;
        this.module = module;
        
        dependeeModuleLoaders = Collections.emptyMap();
        adjunctClasses = Collections.synchronizedSet(new HashSet<String>());
    }

    /**
     * @return the name of the module for which this classloader is responsible.
     */
    public ModuleName getModuleName() {
        return module.getName();
    }

    /**
     * {@inheritDoc}
     * This classloader implementation calls findClass() on the current instance, before calling loadClass() on the parent.
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        
        final long beforeFindClass;
        if (PERFORM_TIMING) {
            beforeFindClass = System.currentTimeMillis();
        } else {
            beforeFindClass = 0;
        }
        
        if (DEBUG_OUTPUT) {
            if (adjunctLoader) {
                System.out.println ("adjunct loader for " + getModuleName() + ": findClass - " + name + " => thread = " + getCurrentThreadName());
            } else {
                System.out.println ("loader for " + getModuleName() + ": findClass - " + name + " => thread = " + getCurrentThreadName());
            }
        }
        
        // The adjunct loader should only return the class if it's in the set of adjunct classes.
        if (adjunctLoader && !adjunctClasses.contains(name) && (name.indexOf('$') < 0 || !adjunctClasses.contains(name.substring(0, name.indexOf('$'))))) {
            throw new ClassNotFoundException();
        }
        
        // Get the module package component of the request class.
        ModuleName classModuleName = CALToJavaNames.getModuleNameFromPackageName(name);
        if (classModuleName == null) {
            throw new ClassNotFoundException();
        }
        
        // Check if we handle this package.
        if (classModuleName.equals(getModuleName())) {

            byte[] data = getBytecodeForClassInternal(name);
            
            // Instantiate the class.
            if (data == null) {
                // TODOEL: perhaps this should throw a NoClassDefFoundError.
                //  This would only make sense if we can ensure that this classloader can only be asked for a class by our code --
                //  it wouldn't do for a client to encounter a LinkageError if it were to ask the classloader for such a class.
                throw new ClassNotFoundException("Unable to find class: " + name);
            }
            // Increment the number of classes and bytes loaded

            nClassesLoaded++;
            nBytesLoaded += data.length;
            
            Class<?> c = defineClass(name, data, 0, data.length);
            
            // Add to the list of loaded classes.  This is used when resetting cached CAF results.
            loadedClasses.add(c);
            
            if (PERFORM_TIMING) {
                long afterFindClass = System.currentTimeMillis();
                findClassTimeMS += (afterFindClass - beforeFindClass);
            }
            
            if (DUMP_LOADED_CLASS_NAMES) {
                System.out.println(name);
                //System.out.println(name + " " + data.length);
            }
            
            if (DEBUG_OUTPUT) {
                System.out.println("    Found");
            } 
            return c;
        }
        
        // Classes in other modules will always be loaded by the module loader, never by the adjunct
        // loader, so we only need to check for the class being in another module if this loader
        // is not an adjunct loader.
        if (!adjunctLoader) {
            CALClassLoader moduleLoader = dependeeModuleLoaders.get(classModuleName);
            if (moduleLoader != null) {
                return moduleLoader.loadClass(name);
            }
        }
        throw new ClassNotFoundException ("Unable to find class: " + name + " with CALClassLoader for module " + getModuleName());
    }

    /**
     * Returns the bytecode for the specified class.
     * @param className the class for which bytecode is to be fetched/generated.
     * @return the bytecode for the specified class.
     * @throws ClassNotFoundException
     */
    private byte[] getBytecodeForClassInternal(final String className) throws ClassNotFoundException {
        
        final byte[] data;
        if (shouldLookupClassData()) {

            // Lookup the class data.
            if (PERFORM_TIMING) {
                final long before = System.currentTimeMillis();
                data = lookupClassData(className);
                final long after = System.currentTimeMillis();
                lookupClassDataTimeMS += (after - before);
                
            } else {
                data = lookupClassData(className);
            }

            
        } else {
            
            // Generate the class data.
            final int lastPeriodIndex = className.lastIndexOf('.');
            if (lastPeriodIndex < 0) {
                throw new ClassNotFoundException("Unable to find class: " + className);
            }
            final String unqualifiedClassName = className.substring(lastPeriodIndex + 1);
            try {
                
                if (PERFORM_TIMING) {
                    final long before = System.currentTimeMillis();
                    data = LECCJavaBytecodeGenerator.generateClassData(module, unqualifiedClassName);
                    final long after = System.currentTimeMillis();
                    generateClassDataTimeMS += (after - before);
                    
                } else {
                    data = LECCJavaBytecodeGenerator.generateClassData(module, unqualifiedClassName);
                }
            
            } catch (final CodeGenerationException e) {
                // Badness occurred.  This is a runtime exception.
                // -- OR --
                // The code generation failed because a foreign type or a foreign function's corresponding Java entity
                // could not be resolved. (In this case the CodeGenerationException would be wrapping an
                // UnableToResolveForeignEntityException)
                
                // By throwing a NoClassDefFoundError instead of a ClassNotFoundException,
                //  we ensure that classes in the namespace for cal module packages are not found by other classloaders.
                // A ClassNotFoundException will be caught by the calling loadClass() method, which will try again with the parent classloader.

                // Imitate behaviour where NoClassDefFoundErrors thrown by the VM have the slashified class name as the message.
                final Error error = new NoClassDefFoundError(className.replace('.','/'));
                error.initCause(e);
                throw error;
            }
        }
        
        return data;
    }
    
    /**
     * Returns the bytecode for the specified class. This is meant to be called by the {@link StandaloneJarBuilder}
     * to generate bytecode for classes required for building a standalone jar.
     * 
     * @param className the class for which bytecode is to be fetched/generated.
     * @return the bytecode for the specified class.
     * @throws ClassNotFoundException
     */
    byte[] getBytecodeForClass(final String className) throws ClassNotFoundException {
        if (adjunctLoader) {
            // We want to use the module class loader, and not the adjunct class loader, for fetching the bytecode.
            return ((CALClassLoader)getParent()).getBytecodeForClass(className);
        } else {
            return getBytecodeForClassInternal(className);
        }
    }
    
    /**
     * Returns the representation for the specified class. This is meant to be called by the {@link StandaloneJarBuilder}
     * to generate the source for classes required for building a standalone jar.
     * 
     * @param className the class for which representation is to be generated.
     * @return the representation for the specified class.
     * @throws ClassNotFoundException 
     */
    JavaClassRep getClassRepWithInnerClasses(final String className) throws ClassNotFoundException {
        if (adjunctLoader) {
            // We want to use the module class loader, and not the adjunct class loader, for fetching the class.
            return ((CALClassLoader)getParent()).getClassRepWithInnerClasses(className);
            
        } else {
            final int lastPeriodIndex = className.lastIndexOf('.');
            if (lastPeriodIndex < 0) {
                throw new ClassNotFoundException("Unable to find class: " + className);
            }

            final String unqualifiedClassName = className.substring(lastPeriodIndex + 1);
            try {
                return JavaDefinitionBuilder.getClassRepWithInnerClasses(module, unqualifiedClassName);

            } catch (final CodeGenerationException e) {
                // We don't need to use a NoClassDefFoundError, because for the purposes of StandaloneJarBuilder
                // a ClassNotFoundException is sufficient for reporting the error.
                throw new ClassNotFoundException("Unable to find class: " + className, e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * Override to look for classes in this class loader first.
     * This should be safe, since this classloader should never define classes which are already defined by other parts of the system.
     * In fact, this is safer, since we don't want other parts of the system to define lecc classes.
     */
    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // First, check if the class has already been loaded
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                // Look in this classloader first..
                c = findClass(name);
                
            } catch (ClassNotFoundException e) {
                // Not found by this classloader.  Delegate to parent's loadClass().
                c = getParent().loadClass(name);
            }
        }
        
        if (resolve) {
            resolveClass(c);
        }
        
        return c;
    }

    /**
     * @return whether class data should be looked up from the repository.
     */
    private boolean shouldLookupClassData() {
        // If we're using a dynamic runtime, never for the adjunct loader.
        // Otherwise, whether module.shouldLookupClassData() returns true.
        if (adjunctLoader && !LECCMachineConfiguration.isLeccRuntimeStatic()) {
            return false;
        }
        
        return module.shouldLookupClassData();
    }
    
    /**
     * Look up the byte code definition of the named class.
     * 
     * Created: Mar 30, 2004
     * @param className
     * @return - byte code for the class definition.
     */
    private byte[] lookupClassData(String className) {
        
        if (isJar(moduleParentLocator)) {
            String fileName;
            if (LECCMachineConfiguration.generateBytecode()) {
                fileName = className.replace('.', '/') + COMPRESSED_CLASS_FILE_EXTENSION;
            } else {
                fileName = className.replace('.', '/') + CLASS_FILE_EXTENSION;
            }
            return loadJarData((ProgramResourceLocator.File)moduleParentLocator, fileName);
            
        } else {
            
            ProgramResourceLocator.File fileLocator;
            if (LECCMachineConfiguration.generateBytecode()) {
                String extension = className.substring(className.lastIndexOf('.')+1) + CALClassLoader.COMPRESSED_CLASS_FILE_EXTENSION;
                fileLocator = ((ProgramResourceLocator.Folder)moduleParentLocator).extendFile(extension);
            } else {
                String extension = className.substring(className.lastIndexOf('.')+1) + CALClassLoader.CLASS_FILE_EXTENSION;
                fileLocator = ((ProgramResourceLocator.Folder)moduleParentLocator).extendFile(extension);
            }
            
            byte[] fileData = loadFileData(fileLocator);
            if (LECCMachineConfiguration.generateBytecode()) {
                if (fileData == null) {
                    return null;
                }
                
                // uncompress the data..
                inflater.reset();
                inflater.setInput(fileData);

                // Reusing this baos should be ok, since its only other use is in loadFileData(), which we've finished calling.
                ByteArrayOutputStream baos = threadLocalByteArrayOutputStream.get();
                baos.reset();
                
                try {
                    // Reusing this bytearray should be ok, since its only other use is in loadFileData(), which we've finished calling.
                    byte[] buf = threadLocalByteArray.get();
                    while (!inflater.finished()) {
                        int len = inflater.inflate(buf);
                        baos.write(buf, 0, len);
                    }
                    
                } catch (DataFormatException e) {
                    // Can't read the compressed class..
                    
                    // return null;
                    throw new IllegalStateException("Can't read compressed class: " + className);
                }
                
                return baos.toByteArray();
                
                // Not necessary to close the ByteArrayOutputStream.
                
            } else {
                // Source generator.
                return fileData;
            }
        }
    }
      
    /**
     * Returns true if the class path for this loader is a jar file.
     * 
     * Created: Mar 30, 2004
     * @param resourceLocator
     * @return - true if the classpath is a jar file
     */
    private static boolean isJar(ProgramResourceLocator resourceLocator) {
        if (resourceLocator instanceof ProgramResourceLocator.File) {
            String name = resourceLocator.getName();
            return name.endsWith(".jar") || name.endsWith(".zip");
        }
        return false;
    }

    /**
     * Load the byte code for a class from a file based on the path and class name.
     * Created: Mar 30, 2004
     * @param fileLocator
     * @return - the byte code for the class.
     */
    private byte[] loadFileData(ProgramResourceLocator.File fileLocator) {

        InputStream fileContents = null;
        try {
            fileContents = resourceRepository.getContents(fileLocator);     // throws IOException if the file doesn't exist.
            ReadableByteChannel channel = Channels.newChannel(fileContents);
            
            // Note: Another way to do this is to determine the channel size, and allocate an array of the correct size.
            //       However, this involves additional file system access.
            
            // Init the output buffer for holding the input stream
            ByteArrayOutputStream baos = threadLocalByteArrayOutputStream.get();
            baos.reset();
            
            byte[] classData = threadLocalByteArray.get();
            ByteBuffer buffer = ByteBuffer.wrap(classData);
            
            // Read the input stream to a buffer and write the buffer to the output buffer
            while (true) {
                int bytesInBuffer = channel.read(buffer);
                if (bytesInBuffer < 0) {
                    break;
                }
                baos.write(classData, 0, bytesInBuffer);
                buffer.rewind(); // rewind the buffer so that the next read starts at position 0 again
            }
            
            // Convert the output stream to an byte array and create an input stream
            // based on this array
            return baos.toByteArray();
            
        } catch (IOException e) {
            // Simply fall through to the finally block.
            
        } finally {
            if (fileContents != null) {
                try {
                    fileContents.close();
                } catch (IOException e) {
                    // Not really anything to do at this point.  Fall through to return null.
                }
            }
        }
        
        return null;
    }

    /**
     * Load the byte code for a class from a jar file.
     * 
     * Created: Mar 30, 2004
     * @param jarLocator
     * @param fileName
     * @return - the byte code for the class. Can be null if the class cannot be found in the jar, or the jar cannot be found.
     */
    private byte[] loadJarData(ProgramResourceLocator.File jarLocator, String fileName) {
        
        ZipFile zipFile;
        try {
            File file = resourceRepository.getFile(jarLocator);
            if (file == null) {
                // the jarLocator refers to a resource that is not backed by the file system (e.g. in a Car)
                // We do not support jars inside Cars, so we return null for the bytes for the requested class.
                return null;
            }
            zipFile = new ZipFile(file);
        
        } catch (IOException io) {
            return null;
        }
        
        ZipEntry entry = zipFile.getEntry(fileName);
        if (entry == null) {
            return null;
        }
        int size = (int)entry.getSize();
        
        InputStream stream = null;
        try {
            stream = zipFile.getInputStream(entry);
            byte[] data = new byte[size];
            int pos = 0;
            while (pos < size) {
                int n = stream.read(data, pos, data.length - pos);
                pos += n;
            }
            zipFile.close();
            return data;
        } catch (IOException e) {
            // Fall through to finally block.
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                // Not really anything to do at this point.  Fall through to return null.
            }
        }
        return null;
    }
    
    /**
     * @return the name of the current thread.
     */
    private static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * Immediately define a class in this classloader.
     * 
     * Created: Mar 30, 2004
     * @param className
     * @param data
     * @param forAdjunct
     */
    void defineClass (String className, byte[] data, boolean forAdjunct) {
        if (DEBUG_OUTPUT) {
            System.out.println ("defining: " + className + " in " + getModuleName() + " => thread = " + getCurrentThreadName());
        }

        if (forAdjunct) {
            // Define an adjunct.
            if (!adjunctLoader) {
                throw new IllegalStateException("Attempt to define adjunct class on non-adjunct class loader.");
            }
        
        } else {
            // Define a non-adjunct.
            if (adjunctLoader) {
                ((CALClassLoader)getParent()).defineClass(className, data, forAdjunct);
                return;
            }
        }
        
        // Define the class.
        Class<?> c = defineClass(className, data, 0, data.length, makeProtectionDomain());
        nClassesLoaded++;
        nBytesLoaded += data.length;
        
        // Add to the list of loaded classes.  This is used when resetting cached CAF results.
        loadedClasses.add(c);
    }
    
    /**
     * Creates the ProtectionDomain to be used for creating new classes.
     * 
     * TODO: Using the ProtectionDomain from the CALClassLoader is probably not
     * the ideal thing to do here. Ultimately we will want to implement some
     * kind of strategy for ensuring that the cal/cmi/car/etc. files that we're
     * using to create a new Java class are themselves trusted. For example if
     * we load a Car file via HTTP we might want to ensure that the Car file is
     * signed and create a ProtectionDomain based on the context in which the
     * Car is loaded.
     * 
     * @return The ProtectionDomain of the CALClassLoader.
     */
    private ProtectionDomain makeProtectionDomain() {
        return this.getClass().getProtectionDomain();
    }
    
    /**
     * Tags the named class as being part of an adjunct.
     * 
     * Created: Mar 30, 2004
     * @param className
     */
    void markAdjunctClass (String className) {
        if (DEBUG_OUTPUT) {
            System.out.println ("marking adjunct class " + className + " in module " + getModuleName() + " => thread = " + getCurrentThreadName());
        }
        
        // Only the adjunct loader cares about this.
        // The module loader is the parent of the adjunct loader, so if this is called on the module loader, 
        //   the adjunct loader can't be accessed, and there's nothing we can do about it.
        if (adjunctLoader) {
            adjunctClasses.add (className);
        } else {
            // A module loader should never have adjunct class names in the adjunct classes set.
            throw new IllegalStateException("An module class loader was asked to mark an adjunct class.");
        }
    }
    
    /**
     * Discard the cached results for any loaded classes.
     * @param context - discard cached results associated with this execution context.
     */
    void resetCachedResults (ExecutionContext context) {
        if (DEBUG_OUTPUT) {
            System.out.println ("resetting cached results for module class loader: " + getModuleName() + " => thread = " + getCurrentThreadName());
        }

        if (adjunctLoader) {
            ((CALClassLoader)getParent()).resetCachedResults(context);
        }

        // use "i < vector.size()" to handle the off chance that findClass() is called while this method is running..
        for (int i = 0; i < loadedClasses.size(); ++i) {
            Class<?> c = loadedClasses.get(i);
            if (RTCAF.class.isAssignableFrom(c)) {
                try {
                    Method m = c.getMethod("resetCachedResults", new Class[]{context.getClass()});
                    m.invoke(null, new Object[]{context});
                } catch (NoSuchMethodException e) {
                    throw new IllegalStateException ("Unable to find method resetCachedResults() for" + c.getName());
                } catch (InvocationTargetException e) {
                    throw new IllegalStateException ("Unable to invoke resetCachedResults() for " + c.getName());
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException ("Unable to access resetCachedResults for " + c.getName());
                }
            }
        }
    }
    
    /**
     * Load the named class and create an instance by
     * invoking the factory method 'make'.
     * The named class must be derived from RTValue.
     * @param className
     * @param mf
     * @param executionContext
     * @return an instance of the named class.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws LinkageError
     * @throws NoSuchFieldException
     */
    /*
     * @implementation
     * If you modify this method, you should also modify the related method
     * StandaloneJarBuilder.getStartPointInstanceJavaExpression 
     */
    RTValue getStartPointInstance (String className, 
                                   MachineFunction mf, 
                                   RTExecutionContext executionContext) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        
        if (mf == null) {
            throw new NullPointerException ("Invalid MachineFunction in CALClassLoader.getStartPointInstance() for " + className + ".");
        }
        Class<?> c = loadClass(className);
        
        if (mf.isDataConstructor()) {
            // This is a data constructor.
            // Get the static 'make' method.
            Method m = c.getMethod("make", new Class[]{});
            return (RTValue)m.invoke(null, new Object[]{});
        }

        FunctionGroupInfo fgi = module.getFunctionGroupInfo(mf);
        if (fgi == null) {
            throw new NullPointerException ("Invalid FunctionGroupInfo in CALClassLoader.getStartPointInstance() for " + mf.getQualifiedName() + ".");
        }

        if (mf.getArity() == 0) {
            // Get the static 'make' method.
            if (fgi.getNCAFs() + fgi.getNZeroArityFunctions() <= 1) {
                Method m = c.getMethod("make", new Class[]{RTExecutionContext.class});
                return (RTValue)m.invoke(
                        null, 
                        new Object[]{executionContext});
            } else {
                Method m = c.getMethod("make", new Class[]{int.class, RTExecutionContext.class});
                int functionIndex = fgi.getFunctionIndex(mf.getName());
                return (RTValue)m.invoke(
                        null, 
                        new Object[]{Integer.valueOf(functionIndex), executionContext});
            }
        }
        
        // Access the static instance field.
        String instanceFieldName = CALToJavaNames.getInstanceFieldName(mf.getQualifiedName(), module);
        Field field = c.getField(instanceFieldName);
        
        return (RTValue)field.get(null);
    }
    
    /**
     * Load the named class and create an instance by
     * invoking the factory method 'getTagDC'.
     * The named class must be derived from RTValue.
     * @param className
     * @param ordinal
     * @return an instance of the named class.
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws LinkageError
     */
    /*
     * @implementation
     * If you modify this method, you should also modify the related method
     * StandaloneJarBuilder.getTagDCStartPointInstanceJavaExpression 
     */
    RTValue getTagDCStartPointInstance (String className, int ordinal) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> c = loadClass(className);
        
        // Get the static 'make' method.
        Method m = c.getMethod("getTagDC", new Class[]{int.class});
        
        //RTValue instanceToRun = (RTValue)mainClass.newInstance();
        return (RTValue)m.invoke(null, new Object[]{Integer.valueOf(ordinal)});
    }
    
    /**
     * @param forAdjunct
     * @return the number of classes loaded by either this class loader, or if this is an adjunct loader and forAdjunct is false, the parent class loader.
     */
    int getNClassesLoaded(boolean forAdjunct) {
        if (adjunctLoader && !forAdjunct) {
            return ((CALClassLoader)getParent()).getNClassesLoaded(forAdjunct);
        } else {
            return nClassesLoaded;
        }
    }
    
    /**
     * @param forAdjunct
     * @return the number of bytes loaded by either this class loader, or if this is an adjunct loader and forAdjunct is false, the parent class loader.
     */
    int getNClassBytesLoaded(boolean forAdjunct) {
        if (adjunctLoader && !forAdjunct) {
            return ((CALClassLoader)getParent()).getNClassBytesLoaded(forAdjunct);
        } else {
            return nBytesLoaded;
        }
    }
    
    /**
     * @param forAdjunct
     * @return number of milliseconds spent in generating class file data by either this class loader, or if this is an adjunct loader and forAdjunct is false, the parent class loader.
     */
    long getGenerateClassDataTimeMS(boolean forAdjunct) {
        if (adjunctLoader && !forAdjunct) {
            return ((CALClassLoader)getParent()).getGenerateClassDataTimeMS(forAdjunct);
        } else {
            return generateClassDataTimeMS;
        }
    }
    
    /**
     * @param forAdjunct
     * @return number of milliseconds spent in looking up class file data by either this class loader, or if this is an adjunct loader and forAdjunct is false, the parent class loader.
     */
    long getLookupClassDataTimeMS(boolean forAdjunct) {
        if (adjunctLoader && !forAdjunct) {
            return ((CALClassLoader)getParent()).getLookupClassDataTimeMS(forAdjunct);
        } else {
            return lookupClassDataTimeMS;
        }
    }
    
    /**
     * @param forAdjunct
     * @return number of milliseconds spent in findClass() by either this class loader, or if this is an adjunct loader and forAdjunct is false, the parent class loader.
     */
    long getFindClassTimeMS(boolean forAdjunct) {
        if (adjunctLoader && !forAdjunct) {
            return ((CALClassLoader)getParent()).getFindClassTimeMS(forAdjunct);
        } else {
            return findClassTimeMS;
        }
    }
    
    @Override
    public String toString () {
        return "CALClassLoader for " + module.getName() + (adjunctLoader ? " adjuncts" : "");
    }
}

