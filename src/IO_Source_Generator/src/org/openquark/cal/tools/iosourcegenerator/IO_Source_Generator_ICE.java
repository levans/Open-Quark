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
 * IO_Source_Generator_ICE.java 
 *
 * Creation date: May 18, 2007
 * By: Raymond Cypher
 */
package org.openquark.cal.tools.iosourcegenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.openquark.cal.ICE;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.foreignsupport.module.Prelude.MaybeValue;
import org.openquark.cal.foreignsupport.module.Prelude.OrderingValue;
import org.openquark.cal.foreignsupport.module.Prelude.UnitValue;
import org.openquark.cal.internal.javamodel.JavaClassRep;
import org.openquark.cal.internal.javamodel.JavaGenerationException;
import org.openquark.cal.internal.javamodel.JavaSourceGenerator;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.module.Cal.Collections.CAL_Array;
import org.openquark.cal.module.Cal.Collections.CAL_Map;
import org.openquark.cal.module.Cal.Collections.CAL_Set;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.services.DefaultWorkspaceDeclarationProvider;
import org.openquark.cal.services.WorkspaceConfiguration;
import org.openquark.cal.services.WorkspaceDeclaration;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.TextEncodingUtilities;

/**
 * This class is an extension of the CAL command line utility ICE.  It extends ICE
 * by adding new commands which allow the user to access the I/O source code generator.
 * 
 * When working on a project which combines CAL and Java code there is often a need to 
 * marshal data types between CAL and Java.  The mechanism which is generally used for 
 * this is to make a CAL data type an instance of the Inputable and Outputable type classes.
 *
 * To make a user defined type inputable and outputable, you would first decide what the 
 * Java representation of the Quark type will be. The input and output methods will convert 
 * between the Quark type and an appropriate instance of this Java class.
 * A common approach is to define a foreign type for the target Java class, write CAL 
 * functions that convert to and from that type, using foreign functions, then convert 
 * to or from JObject using a foreign function that merely casts between the target Java 
 * class and Object.  For more information please see the two documents ‘CAL User’s Guide’ 
 * and ‘Java Meets Quark’.
 *
 * The Input/Output code generation tool seeks to streamline the process of making a CAL 
 * data type an instance of the Inputable and Outputable type classes by generating the 
 * majority of the necessary CAL and Java code.
 *
 * The code generation tool takes an existing CAL data type and will generate a corresponding 
 * Java class.  It will then generate the necessary CAL code to refer to the generated Java 
 * class as a CAL foreign type, generate the CAL functions needed to convert between the original 
 * CAL data type and the foreign type, and generate the type class instance declarations to make the 
 * original data type an instance of the Inputable and Outputable type classes.
 *
 * @author rcypher
 *
 */
public class IO_Source_Generator_ICE extends ICE {

    private static final String ICE_DEFAULT_WORKSPACE_FILENAME = "ice.default.cws";

    Map<QualifiedName, JavaTypeName> builtIn_typeToClassMappings = new HashMap<QualifiedName, JavaTypeName>();
    Map<QualifiedName, JavaTypeName> userDefined_typeToClassMappings = new HashMap<QualifiedName, JavaTypeName>();
    Map<ModuleName, String>          userDefined_moduleToPackageMappings = new HashMap<ModuleName, String>();
    
    /**
     * Run ice, with fallback settings for the workspace declaration, client id, and target module.
     * @param args the command line arguments to pass to ICE.
     * @param fallbackWorkspaceDeclaration the fallback workspace declaration, used if non-null and the system property is not set.
     * @param fallbackClientID the fallback client id., used if non-null and the system property is not set.
     * @param fallbackTargetModule the fallback target module, used if non-null and the system property is not set.
     */
    public static void appMain(String[] args, String fallbackWorkspaceDeclaration, String fallbackClientID, ModuleName fallbackTargetModule) {

        String workingModule = System.getProperty(ICE.ICE_PROP_MODULE);
        if (isNullOrEmptyString(workingModule) && fallbackTargetModule != null) {
            System.setProperty(ICE.ICE_PROP_MODULE, fallbackTargetModule.toSourceText());
        }

        String workspaceClientID = System.getProperty(WorkspaceConfiguration.WORKSPACE_PROP_CLIENT_ID);
        if (isNullOrEmptyString(workspaceClientID) && !isNullOrEmptyString(fallbackClientID)) {
            System.setProperty(WorkspaceConfiguration.WORKSPACE_PROP_CLIENT_ID, fallbackClientID);
        }

        IO_Source_Generator_ICE.appMain(args, fallbackWorkspaceDeclaration);
    }

    /**
     * Create a new ICE object and start its input loop.
     * @param args
     * @param fallbackWorkspaceDeclaration
     */
    public static void appMain(String[] args, String fallbackWorkspaceDeclaration) {
        ICE newIce = new IO_Source_Generator_ICE (null, args, null, System.in, System.out, DefaultWorkspaceDeclarationProvider.getDefaultWorkspaceDeclarationProvider(ICE_PROP_WORKSPACE, fallbackWorkspaceDeclaration));
        newIce.run ();
    }
    
    /**
     * Create a new ICE object and start its input loop.
     * @param args
     */
    public static void main(String[] args) {
        String workspaceFilename = System.getProperty (ICE_PROP_WORKSPACE_FILENAME, ICE_DEFAULT_WORKSPACE_FILENAME);
        
        appMain(args, workspaceFilename);
    }
    
    /**
     * Create a new ICE object.
     * Original Author: rcypher
     * @param instanceName
     * @param args
     * @param workspaceManager
     * @param inputStream
     * @param outputStream
     * @param streamProvider
     */
    public IO_Source_Generator_ICE (String instanceName, 
                String[] args, 
                WorkspaceManager workspaceManager, 
                InputStream inputStream, 
                PrintStream outputStream,
                WorkspaceDeclaration.StreamProvider streamProvider) {
        super (instanceName, args, workspaceManager, inputStream, outputStream, streamProvider);
        
        builtIn_typeToClassMappings.put(CAL_Prelude.TypeConstructors.List, JavaTypeName.make(java.util.List.class));
        builtIn_typeToClassMappings.put(CAL_Prelude.TypeConstructors.Boolean, JavaTypeName.BOOLEAN);
        builtIn_typeToClassMappings.put(CAL_Prelude.TypeConstructors.Ordering, JavaTypeName.make(OrderingValue.class));
        builtIn_typeToClassMappings.put(CAL_Prelude.TypeConstructors.Maybe, JavaTypeName.make(MaybeValue.class));
        builtIn_typeToClassMappings.put(CAL_Prelude.TypeConstructors.Unit, JavaTypeName.make(UnitValue.class));
        builtIn_typeToClassMappings.put(CAL_Prelude.TypeConstructors.CalValue, JavaTypeName.make(CalValue.class));
        
        
        builtIn_typeToClassMappings.put(CAL_Map.TypeConstructors.Map, JavaTypeName.make(java.util.List.class));
        builtIn_typeToClassMappings.put(CAL_Array.TypeConstructors.Array, JavaTypeName.make(java.util.List.class));
        builtIn_typeToClassMappings.put(CAL_Set.TypeConstructors.Set, JavaTypeName.make(java.util.List.class));
        
    }    
    
    /**
     * Initialize the commands that can be executed in ICE.
     */
    protected void initializeCommands () {
        // Let the base class initialize first.
        super.initializeCommands();
        
        // Add in commands specific to this extension of ICE.
        addCommand(new ICECommand(new String[] { "gio"}, false, true,
                ICE.CommandType.UTILITIES, new String[] { ":gio <module name> <target package> [-rp<root project> | -rd<root directory>] [excluded type constructors] [-v | -verbose]" },
                new String[] { "Generate code to allow the type constructors in the named module to be input/output from/to Java.\nA set of Java classes corresponding to the type constructors in the named module is generated in the provided target package.  A CAL module is also generated.  It is named by extending the provided module name with '_JavaIO'.  This module contains the functions and class instances necessary to make the type constructors in the provided module instances of Prelude.Inputable and Prelude.Outputable.  The generated CAL can be copied into the original module or the original module can be modified to name the generated module as a 'friend'.\nThe generated Java and CAL will be placed in a directory named 'gen'.  By default the gen directory is created as a top level directory of the project containing the target module.  The optional -rp parameter can be used to specify the root project where the generated code is to be placed.  Alternatively the optional -rd parameter can be used to specify a root directory in which to place the generated code.\nAdditionaly type constructor name arguments can be provided.  Code generation will skip these type constructors." }) {
            protected void performCommand(String info) {
                command_generate_IO_code(info);
            }
        });
        
        addCommand(new ICECommand(new String[] { "tm"}, false, true,
                ICE.CommandType.UTILITIES, new String[] { ":tm <CAL type constructor name> <Java class name>", ":tm -show", ":tm -r <CAL type constructor name>" },
                new String[] { "Set the association between a CAL type and a Java class.", "Show the existing mappings.", "Remove the named mapping." }) {
            protected void performCommand(String info) {
                command_setTypeMapping(info);
            }
        });

        addCommand(new ICECommand(new String[] {"mm"}, false, true,
                ICE.CommandType.UTILITIES, new String[] { ":mm <CAL module name> <Java package name>", ":mm -show", ":mm -r <CAL module name>" },
                new String[] { "Set the association between a CAL module and a Java package.  This is used to indicate to the I/O generator what package contains previously generated classes for the named module.", "Show the existing mappings.", "Remove the named mapping." }) {
            protected void performCommand(String info) {
                command_setModuleMapping(info);
            }
        });

    }

    /**
     * Specify a mapping from a CAL module to a Java class.
     * This specifies that the I/O classes for the given CAL module 
     * are in the specified package.
     * This information is used when generating the code for references to
     * CAL data types not defined in the module for which code is being
     * generated.
     * @param arguments
     */
    private void command_setModuleMapping (String arguments) {
        arguments = arguments.trim();

        StringTokenizer stk = new StringTokenizer(arguments);
        List<String> argValues = new ArrayList<String>();
        while (stk.hasMoreTokens()) {
            argValues.add(stk.nextToken());
        }
        
        if (argValues.size() == 0 || argValues.size() > 2) {
            getIceLogger().log(Level.INFO, "Invalid number of arguments for set type association command.");
            return;
        }
        
        if (argValues.size() == 1) {
            // Argument should be -show
            
            if (!argValues.get(0).equals("-show")) {
                getIceLogger().log(
                        Level.INFO, 
                        argValues.get(0) + " is not a valid argument for the :mm command.");
            }
            
            if (userDefined_moduleToPackageMappings.size() == 0) {
                getIceLogger().log(Level.INFO, "There are currently no mappings from CAL modules to Java packages.");
                return;
            }
            
            int maxTypeNameLength = 0;
            for (ModuleName moduleName : userDefined_moduleToPackageMappings.keySet()) {
                int nameLength = moduleName.toSourceText().length();
                if (nameLength > maxTypeNameLength) {
                    maxTypeNameLength = nameLength;
                }
            }
            
            for (ModuleName moduleName : userDefined_moduleToPackageMappings.keySet()) {
                String packageName = userDefined_moduleToPackageMappings.get(moduleName);
                
                StringBuffer sb = new StringBuffer();
                sb.append (moduleName.toSourceText());
                for (int i = sb.length(); i < maxTypeNameLength; ++i) {
                    sb.append (" ");
                }
                
                sb.append (" => ");
                sb.append (packageName);
                getIceLogger().log(Level.INFO, sb.toString());
            }            
        } else {
            // A mapping is either being added or removed.
            
            if (argValues.get(0).trim().startsWith("-r")) {
                // Remove an existing mapping.
                ModuleName moduleName = resolveModuleNameInProgram(argValues.get(1), true);
                if (moduleName != null) {
                    userDefined_moduleToPackageMappings.remove(moduleName);
                }
            } else {
                // Add a new mapping.
                ModuleName resolvedModuleName = resolveModuleNameInProgram(argValues.get(0), true);
                if (resolvedModuleName == null) {
                    getIceLogger().log(
                            Level.INFO, 
                            "Unable to resolve CAL module name " + resolvedModuleName + ".");
                    return;
                } 
        
                String javaPackageName = argValues.get(1);
                
                
                userDefined_moduleToPackageMappings.put(resolvedModuleName, javaPackageName);
            }
        }        
    }

    /**
     * Specify a mapping from a CAL data type to a Java class.
     * This specifies that the given CAL type inputs/outputs from/to the 
     * given Java class.
     * This information is used when generating the code for references to
     * CAL data types not defined in the module for which code is being
     * generated.
     * @param arguments
     */
    private void command_setTypeMapping (String arguments) {
        arguments = arguments.trim();

        StringTokenizer stk = new StringTokenizer(arguments);
        List<String> argValues = new ArrayList<String>();
        while (stk.hasMoreTokens()) {
            argValues.add(stk.nextToken());
        }
        
        if (argValues.size() == 0 || argValues.size() > 2) {
            getIceLogger().log(Level.INFO, "Invalid number of arguments for set type association command.");
            return;
        }
        
        if (argValues.size() == 1) {
            // Argument should be -show
            
            if (!argValues.get(0).equals("-show")) {
                getIceLogger().log(
                        Level.INFO, 
                        argValues.get(0) + " is not a valid argument for the :tm command.");
            }
            
            if (userDefined_typeToClassMappings.size() == 0) {
                getIceLogger().log(Level.INFO, "There are currently no mappings from CAL type to Java class.");
                return;
            }
            
            int maxTypeNameLength = 0;
            for (QualifiedName typeName : userDefined_typeToClassMappings.keySet()) {
                int nameLength = typeName.toSourceText().length();
                if (nameLength > maxTypeNameLength) {
                    maxTypeNameLength = nameLength;
                }
            }
            
            for (QualifiedName typeName : userDefined_typeToClassMappings.keySet()) {
                JavaTypeName jtn = userDefined_typeToClassMappings.get(typeName);
                
                StringBuffer sb = new StringBuffer();
                sb.append (typeName.toSourceText());
                for (int i = sb.length(); i < maxTypeNameLength; ++i) {
                    sb.append (" ");
                }
                
                sb.append (" => ");
                sb.append (jtn.getFullJavaSourceName());
                getIceLogger().log(Level.INFO, sb.toString());
            }            
        } else {
            // A mapping is either being added or removed.
            
            if (argValues.get(0).trim().startsWith("-r")) {
                // Remove an existing mapping.
                QualifiedName calTypeName = QualifiedName.makeFromCompoundName(argValues.get(1));
                userDefined_typeToClassMappings.remove(calTypeName);
            } else {
                // Add a new mapping.
                QualifiedName calTypeName = QualifiedName.makeFromCompoundName(argValues.get(0));
                ModuleName resolvedModuleName = resolveModuleNameInProgram(calTypeName.getModuleName().toSourceText(), true);
                if (resolvedModuleName == null) {
                    getIceLogger().log(
                            Level.INFO, 
                            "Unable to resolve CAL type name " + calTypeName + ".");
                    return;
                } else {
                    calTypeName = QualifiedName.make(resolvedModuleName, calTypeName.getUnqualifiedName());
                }
        
                String javaClassName = argValues.get(1);
                
                // Try to find the java class.
                Class javaClass = findJavaClass(javaClassName);
                if (javaClass == null) {
                    getIceLogger().log(
                            Level.INFO, 
                            "Unable to locate class " + javaClassName + " specified in class mapping.");
                    return;
                }
                
                userDefined_typeToClassMappings.put(calTypeName, JavaTypeName.make(javaClass));
            }
        }        
    }
    
    /**
     * Implementation of the :gio command.
     * This command generates the Java and CAL code necessary to make the CAL data
     * types in a module instances of Prelude.Inputable and Prelude.Outputable.
     * @param arguments
     */
    private void command_generate_IO_code (String arguments) {
        arguments = arguments.trim();

        StringTokenizer stk = new StringTokenizer(arguments);
        List<String> argValues = new ArrayList<String>();
        while (stk.hasMoreTokens()) {
            argValues.add(stk.nextToken());
        }
        
        if (argValues.size() < 2) {
            getIceLogger().log(Level.INFO, "Invalid number of arguments for java type generation command.");
            return;
        }
        
        // The first argument will be a module name.
        ModuleName moduleName = resolveModuleNameInProgram((String)argValues.get(0), true);
        if (moduleName == null) {
            throw new NullPointerException("Unable to find module " + (String)argValues.get(0)); 
        }        

        ModuleName origModule = getTargetModule();
        
        command_setModule(moduleName.toString());
        
        // Second argument is target package for generated Java classes. 
        String targetPackage = (String)argValues.get(1);
        // Update the module to package mappings
        userDefined_moduleToPackageMappings.put(moduleName, targetPackage);
        
        String targetRootProject = null;
        String targetRootDirectory = null;
        boolean verbose = false;

        // Non-specific arguments will be type constructors to ignore.
        Set<QualifiedName> typeConstructorsToIgnore = new HashSet<QualifiedName>();
        
        
        for (int i = 2, n = argValues.size(); i < n; ++i) {
            String arg = (String)argValues.get(i);
            if (arg.equals("-v") || arg.equals("-verbose")) {
                verbose = true;
            } else
            if (arg.startsWith("-rp")) {
                targetRootProject = arg.substring(3);
            } else if (arg.startsWith("-rd")) {
                targetRootDirectory = arg.substring(3);
            } else {
                // If arg appears to be a compound name try to make a qualified name.
                String typeName = (String)argValues.get(i);
                if (typeName.indexOf('.') == -1) {
                    typeName = qualifyCodeExpression(typeName);
                }
                
                if (typeName.indexOf('.') == -1) {
                    typeName = getTargetModule().toString() + "." + typeName;
                }
                
                QualifiedName typeConstructorName = QualifiedName.makeFromCompoundName(typeName);
                typeConstructorsToIgnore.add(typeConstructorName);
            }
        }        

        Level origLogLevel = getIceLogger().getLevel();
        if (!verbose) {
            getIceLogger().setLevel(Level.WARNING);
        }
        
        // Get the module type info for the specified module.
        ModuleTypeInfo moduleTypeInfo = getWorkspaceManager().getModuleTypeInfo(moduleName);
        if (moduleTypeInfo == null) {
            throw new NullPointerException("Unable to retrieve type information for module " + moduleName);
        }
        
        // Generate the i/o code.  This is returned in the form of a
        // SourceModel.ModuleDefn and a collection of JavaClassRep.
        IO_Source_Generator.GeneratedIO generatedIO;
        try {
            Map<QualifiedName, JavaTypeName> typeMappings = new HashMap<QualifiedName, JavaTypeName>(builtIn_typeToClassMappings);
            typeMappings.putAll(userDefined_typeToClassMappings);
            
            generatedIO = new IO_Source_Generator().generateIO(
                    moduleTypeInfo, 
                    targetPackage, 
                    typeConstructorsToIgnore, 
                    typeMappings,
                    userDefined_moduleToPackageMappings);

        } catch (UnableToResolveForeignEntityException e) {
            getIceLogger().log(Level.INFO, "Unable to generate java type for module " + moduleName + ":");
            getIceLogger().log(Level.INFO, "  " + e.getCompilerMessage());
            return;
        }
 
        // Map of QualifiedName -> JavaClassRep
        Map<QualifiedName, JavaClassRep> javaClassReps = 
            generatedIO.getJavaClasses();
        
        // The module containing the generated CAL code.
        SourceModel.ModuleDefn moduleDefn = 
            generatedIO.getIoModule();
        
        // Determine the root directory for writing out the CAL and Java source.
        File generatedCodeRoot = getGeneratedCodeRoot(moduleName, targetRootProject, targetRootDirectory);
        
        boolean firstWrite = true;
        
        // Write out each Java class.
        for (Map.Entry<QualifiedName, JavaClassRep> entry : javaClassReps.entrySet()) {
            JavaClassRep classRep = (JavaClassRep)entry.getValue();
            QualifiedName qn = (QualifiedName)entry.getKey();
            
            if (writeJavaClass(generatedCodeRoot, moduleName, targetPackage, classRep)) {
                if (firstWrite) {
                    firstWrite = false;
                    for (LogRecord record : generatedIO.getIoLogMessages()) {
                        getIceLogger().log(record);
                    }
                }
                for (LogRecord record : generatedIO.getJavaClassLogMessages().get(qn)) {
                    getIceLogger().log(record);
                }
            }
        }
        
        
        // Now write out the friend module.
        if (moduleDefn != null) {
            if (writeFriendModule (generatedCodeRoot, moduleName, moduleDefn)) {
                if (firstWrite) {
                    firstWrite = false;
                    for (LogRecord message : generatedIO.getIoLogMessages()) {
                        getIceLogger().log(message);
                    }
                }
                for (LogRecord message : generatedIO.getModuleLogMessages()) {
                    getIceLogger().log(message);
                }
            }
        }

        if (firstWrite) {
            for (LogRecord message : generatedIO.getIoLogMessages()) {
                getIceLogger().log(message);
            }
        }
        
        getIceLogger().setLevel(origLogLevel);
        
        if (firstWrite) {
            getIceLogger().log(Level.INFO, "No changes to the existing generated I/O code for module " + moduleName + " were found.");
        }
        command_setModule(origModule.toString());
        
    }
    
    /**
     * Find the java class corresponding to the given name.
     * @param javaClassName
     * @return a Class if the name matches a class, null otherwise.
     */
    private Class findJavaClass (String javaClassName) {
        try {
            return Class.forName(javaClassName);
        } catch (ClassNotFoundException e) {
        }
        
        return null;
    }

    /**
     * Write out the generated CAL module.
     * @param root
     * @param moduleName
     * @param moduleDefn
     * @return true if module is successfully written.
     */
    private boolean writeFriendModule (
            File root, 
            ModuleName moduleName, 
            SourceModel.ModuleDefn moduleDefn) {
        
        // Determine the directory under the specified root where the module 
        // will be written.
        String newModuleDirs = moduleDefn.getModuleName().getQualifier().toSourceText().replace('.', File.separatorChar);
        File newModuleDirectory = new File (root, "CAL" + File.separatorChar + newModuleDirs);
        if (!newModuleDirectory.exists()) {
            // Try to create the containing directory.
            if (!newModuleDirectory.mkdirs()) {
                getIceLogger().log(Level.WARNING, "Unable to create necessary directory " + newModuleDirectory.getPath() + ".");
                return false;
            } else {
                getIceLogger().log(Level.INFO, "Directory " + newModuleDirectory.getPath() + " created.");
            }
        }

        // The file for the new module.
        File friendModuleFile = new File(newModuleDirectory, moduleDefn.getModuleName().getUnqualifiedModuleName() + ".cal");

        // Get the source text for the new module.
        String moduleSource = moduleDefn.toSourceText();
        
        // If the file already exists, check to see if anything has changed.
        boolean needToWriteModule = true;
        if (friendModuleFile.exists()) {
            try {
                // Check to see if there is a difference between the new source
                // and the existing source.
                FileInputStream fis = new FileInputStream(friendModuleFile);
                boolean changed = sourceChanged(moduleSource, fis);
                fis.close();
                if (!changed) {
                    getIceLogger().log(Level.INFO, "There is no change in the CAL module for " + moduleDefn.getModuleName() + ".");
                    needToWriteModule = false;
                } else {
                    getIceLogger().log(Level.INFO, "The CAL module for " + moduleDefn.getModuleName() + " has changed since the last time it was updated.");
                }
            } catch (FileNotFoundException e) {
                getIceLogger().log(Level.WARNING, "Unable to access file " + friendModuleFile.getPath() + ".  " + e.getLocalizedMessage());
                return false;
            } catch (IOException e) {
                getIceLogger().log(Level.WARNING, "Unable to access file " + friendModuleFile.getPath() + ".  " + e.getLocalizedMessage());
                return false;
            }
        }

        // At this point we've determined whether or not the module needs to be written out.
        if (needToWriteModule) {
            try {
                // Try to open an output stream
                FileOutputStream fos = new FileOutputStream (friendModuleFile);
                byte[] bytes = TextEncodingUtilities.getUTF8Bytes(moduleDefn.toSourceText());
                fos.write(bytes, 0, bytes.length);
                fos.close();
                getIceLogger().log(Level.WARNING, "File " + friendModuleFile.getPath() + " succesfully updated.");
                return true;
            } catch (FileNotFoundException e) {
                getIceLogger().log(Level.WARNING, "Error writing file " + friendModuleFile.getPath() + ".  " + e.getLocalizedMessage());
            } catch (IOException e) {
                getIceLogger().log(Level.WARNING, "Error writing file " + friendModuleFile.getPath() + ".  " + e.getLocalizedMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Determine the root location for writing out the generated code.
     * @param moduleName
     * @param targetRootProject
     * @param targetRootDirectory
     * @return the root location for writing out the generated code.
     */
    private File getGeneratedCodeRoot (ModuleName moduleName, String targetRootProject, String targetRootDirectory) {
        if (targetRootDirectory != null) {
            File f = new File(targetRootDirectory);
            if (!targetRootDirectory.endsWith("gen") && !targetRootDirectory.endsWith("gen" + File.separator)) {
                f = new File (f, "gen");
            }
            return f;
        }
        
        if (targetRootProject != null) {
            ModuleName moduleNames[] = getWorkspaceManager().getModuleNamesInProgram();
            for (int i = 0, n = moduleNames.length; i < n; ++i) {
                String location = getModulePath(moduleNames[i]);
                if (location == null) {
                    getIceLogger().log(Level.INFO, "Unable to determine source location for module " + moduleNames[i] + ".");
                    return null;
                }
                
                if (location.indexOf(targetRootProject) > -1) {
                    return getGeneratedCodeRoot (moduleNames[i], null, null);
                }
            }
        }
        
        // Need to find where to write the files.
        String location = getModulePath(moduleName);
        if (location == null) {
            getIceLogger().log(Level.INFO, "Unable to determine source location for module " + moduleName + ".");
            return null;
        }
        
        // Generally this location will follow the pattern of project_root\gen\cal\module.cal.
        // However, if the Eclipse project has its output
        // set to a different directory, such as 'bin', the CAL source file will be in
        // project_root\bin\gen\module.cal.
        
        File root = null;
        while (root == null) {
            if (location.indexOf(File.separator + "src" + File.separator) != -1) {
               root = new File(location.substring(0,location.indexOf(File.separator + "src" + File.separator) + 1) + "gen");
            } else 
            if (location.indexOf(File.separator + "test" + File.separator) != -1) {
               root = new File(location.substring(0,location.indexOf(File.separator + "test" + File.separator) + 1) + "gen");
            } else
            if (location.indexOf(File.separator + "bin" + File.separator) != -1) {
               root = new File(location.substring(0,location.indexOf(File.separator + "bin" + File.separator) + 1) + "gen");
            } else {
               break;
            }
        }
        
        if (root == null) {
            // Go up one directory from the CAL source location
            File f = new File (location);
            f = f.getParentFile();
            if (f.getName().equals("CAL")) {
                f = f.getParentFile();
            }
            root = new File (f + "gen");
        }    
        
        return root;
    }
    
    /**
     * Write out the source for a Java class.
     * @param root
     * @param moduleName
     * @param targetPackage
     * @param outerTypeDefinition
     * @return true of write is successful.
     */
    private boolean writeJavaClass (File root, ModuleName moduleName, String targetPackage, JavaClassRep outerTypeDefinition) {
        String source = "";
        // Generate the source.
        try {
            source = JavaSourceGenerator.generateSourceCode(outerTypeDefinition);
        } catch (JavaGenerationException e) {
            throw new NullPointerException(e.getLocalizedMessage());
        }
        
        // Determine the directory under the root where the file will be written.
        String packageDirs = targetPackage.replace('.', File.separatorChar);
        
        File bindingLocation = new File (root, packageDirs);
        if (!bindingLocation.exists()) {
            // Try to create the containing directory.
            if (!bindingLocation.mkdirs()) {
                getIceLogger().log(Level.WARNING, "Unable to create necessary directory " + bindingLocation.getPath() + ".");
                return false;
            } else {
                getIceLogger().log(Level.INFO, "Directory " + bindingLocation.getPath() + " created.");
            }
        }
        
        bindingLocation = new File(bindingLocation, outerTypeDefinition.getClassName().getUnqualifiedJavaSourceName() + ".java");
        
        // Determine if the file needs to be written.
        // If the file already exists and is no different from the new version we
        // don't bother writing.
        boolean needToWriteJavaFile = true;
        if (bindingLocation.exists()) {
            try {
                // Check to see if there is a difference between the new source
                // and the existing source.
                FileInputStream fis = new FileInputStream(bindingLocation);
                boolean changed = sourceChanged(source, fis);
                fis.close();
                if (!changed) {
                    getIceLogger().log(Level.INFO, "There is no change in the Java data class " + outerTypeDefinition.getClassName().getUnqualifiedJavaSourceName() + " for " + moduleName + ".");
                    needToWriteJavaFile = false;
                    return false;
                } else {
                    getIceLogger().log(Level.INFO, "The Java data class " + outerTypeDefinition.getClassName().getUnqualifiedJavaSourceName() + " for " + moduleName + " has changed since the last time it was updated.");
                }
            } catch (FileNotFoundException e) {
                getIceLogger().log(Level.WARNING, "Unable to access file " + bindingLocation.getPath() + ".  " + e.getLocalizedMessage());
                return false;
            } catch (IOException e) {
                getIceLogger().log(Level.WARNING, "Unable to access file " + bindingLocation.getPath() + ".  " + e.getLocalizedMessage());
                return false;
            }
        }

        // At this point we know whether or no we need to write the file.
        if (needToWriteJavaFile) {
            try {
                // Try to open an output stream
                FileOutputStream fos = new FileOutputStream (bindingLocation);
                byte[] bytes = TextEncodingUtilities.getUTF8Bytes(source);
                fos.write(bytes, 0, bytes.length);
                fos.close();
                getIceLogger().log(Level.WARNING, "File " + bindingLocation.getPath() + " succesfully updated.");
                return true;
            } catch (FileNotFoundException e) {
                getIceLogger().log(Level.WARNING, "Error writing file " + bindingLocation.getPath() + ".  " + e.getLocalizedMessage());
            } catch (IOException e) {
                getIceLogger().log(Level.WARNING, "Error writing file " + bindingLocation.getPath() + ".  " + e.getLocalizedMessage());
            }
        }
        
        return false;
    }
    
    /**
     * @param stringToCheck the string to check
     * @return true if the string to check is null or has a trimmed length of 0.
     */
    private static boolean isNullOrEmptyString(String stringToCheck) {
        return (stringToCheck == null || stringToCheck.trim().length() == 0);
    }

}
