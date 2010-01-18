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
 * JFit.java
 * Creation date: Sep 14, 2005.
 * By: Edward Lam
 */
package org.openquark.gems.client.jfit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.compiler.SourceModel.TopLevelSourceElement;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.services.CALSourcePathMapper;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.DefaultWorkspaceDeclarationProvider;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceConfiguration;
import org.openquark.cal.services.WorkspaceDeclaration;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.SimpleConsoleHandler;
import org.openquark.util.WildcardPatternMatcher;



/**
 * Java Foreign Import Tool (JFit).
 * A command-line tool for generating foreign imports for Java.
 * 
 * To use:
 * Invoke main() with the appropriate command-line options.
 * Alternatively, create an instance of a JFit tool, with JFit options and a cal workspace, and call autoFit().
 * 
 * @author Edward Lam
 */
public class JFit {

    /** The boilerplate string which always gets displayed. */
    private static final String toolBoilerPlate = "Java Foreign Import Tool(JFit)     Version 0.0.1     (c) 2005 Business Objects.";

    /** All class files end with this string. */
    public static final String CLASS_EXTENSION = ".class";
    
    /** Whether or not to use a nullary workspace for the command-line tool. */
    private static final boolean USE_NULLARY_WORKSPACE = true;
    
    /** The default workspace client id for the command-line tool. */
    private static final String DEFAULT_WORKSPACE_CLIENT_ID = USE_NULLARY_WORKSPACE ? null : "ice";

    /** The default workspace file to use in the absence of a specified workspace file.*/
    private static final String DEFAULT_WORKSPACE_NAME = "gemcutter.default.cws";
    
    /** The provider for the input stream on the workspace declaration.  Null if the workspace is provided. */
    private final WorkspaceDeclaration.StreamProvider streamProvider;
    
    /** The cal workspace.   If not provided in the constructor, this will be set on a call to compileWorkspace() using the stream provider. */
    private CALWorkspace calWorkspace;
    
    /** An instance of a Logger for messages. */
    private final Logger jfitLogger;

    /** The tool options. */
    private final Options options;
    
    /**
     * A class to encapsulate the generation options to the JFit tool.
     * 
     * @author Edward Lam
     */
    public static class Options {
        private final ModuleName moduleName;
        private final File[] inputFiles;
        private final File[] inputDirectories;
        private final String[] classPath;
        private final JFit.Pattern[] patterns;
        private final ForeignImportGenerator.GenerationScope generationScope;
        private final String[] excludeMethods;
        
        /**
         * Constructor for an Options object.
         * Private -- to create, call makeOptions().
         * 
         * @param moduleName
         * @param inputFiles
         * @param classPath
         * @param patterns
         * @param generationScope
         * @param excludeMethods
         */
        private Options(ModuleName moduleName, File[] inputFiles, File[] inputDirectories, String[] classPath, JFit.Pattern[] patterns, 
                ForeignImportGenerator.GenerationScope generationScope, String[] excludeMethods) {
            this.moduleName = moduleName;
            this.inputFiles = inputFiles;               // may not be null.
            this.inputDirectories = inputDirectories;   // may not be null.
            this.classPath = classPath;                 // null == empty array.
            this.patterns = patterns;
            this.generationScope = generationScope;
            this.excludeMethods = excludeMethods;
            
            if (inputFiles == null) {
                throw new NullPointerException("Argument 'inputFiles' ,may not be null.");
            }
            if (inputDirectories == null) {
                throw new NullPointerException("Argument 'inputDirectories' ,may not be null.");
            }
            if (classPath == null) {
                throw new NullPointerException("Argument 'classPath' ,may not be null.");
            }
            if (generationScope == null) {
                throw new NullPointerException("Argument 'generationScope Path' ,may not be null.");
            }
            if (excludeMethods == null) {
                throw new NullPointerException("Argument 'excludeMethods' ,may not be null.");
            }
        }
        
        /**
         * Create an options object given the arguments.
         * 
         * @param moduleNameString 
         *              the module name.  May not be null.
         * @param inputFiles 
         *              the input .jar files.  May be null.
         * @param inputDirectories 
         *              the input source folders.  Maybe be null.
         * @param classPath 
         *              the extra class path entries to use.  May be null.
         * @param patterns 
         *              the class name patterns to match.  If null, all classes are matched.
         * @param generationScope 
         *              if null, default to all private.
         * @param excludeMethods 
         *              the methods to exclude.  If null, none are excluded.
         * @param logger 
         *              the logger to which to log any errors.
         * 
         * @return the options object, or null if there were errors in the provided option arguments.
         * If null, errors will have been logged to the logger.
         */
        public static Options makeOptions(String moduleNameString, File[] inputFiles, File[] inputDirectories, String[] classPath, JFit.Pattern[] patterns, 
                ForeignImportGenerator.GenerationScope generationScope, String[] excludeMethods, Logger logger) {
            
            if (moduleNameString == null) {
                logger.severe("No module name provided.");
                return null;
            }
            
            ModuleName moduleName = ModuleName.maybeMake(moduleNameString);
            if (moduleName == null) {
                logger.severe("Invalid module name: " + moduleName);
                return null;
            }
            
            // If no patterns are provided, we assume everything on the classpath.
            if (patterns == null) {
                patterns = new JFit.Pattern[] {JFit.Pattern.MATCH_ALL};
            }
            
            if (inputFiles == null) {
                inputFiles = new File[] {};
                
            } else {
                for (final File inputFile : inputFiles) {
                    if (inputFile == null) {
                        logger.severe("Null input file encountered.");
                        return null;
                    }
                    if (!FileSystemHelper.fileExists(inputFile)) {
                        logger.severe("Input file \'" + inputFile.getPath() + "\' does not exist.");
                        return null;
                    }
                }
            }
            
            if (inputDirectories == null) {
                inputDirectories = new File[] {};
                
            } else {
                for (final File inputDirectory : inputDirectories) {
                    if (inputDirectory == null) {
                        logger.severe("Null input directory encountered.");
                        return null;
                    }
                    if (!inputDirectory.isDirectory()) {
                        logger.severe("Input directory \'" + inputDirectory.getPath() + "\' does not exist.");
                        return null;
                    }
                }
            }
            
            if (classPath == null) {
                classPath = new String[] {};
            }
            
            if (generationScope == null) {
                generationScope = ForeignImportGenerator.GenerationScope.ALL_PRIVATE;
            }
            
            if (excludeMethods == null) {
                excludeMethods = new String[] {};
                
            } else {
                // Check for nulls.
                for (final String excludeMethod : excludeMethods) {
                    if (excludeMethod == null) {
                        logger.severe("Null exclude method encountered.");
                    }
                }
            }
            
            return new Options(moduleName, inputFiles, inputDirectories, classPath, patterns, generationScope, excludeMethods);
        }
        
        /**
         * @return the module name.  Always valid.
         */
        public ModuleName getModuleName() {
            return this.moduleName;
        }
        
        /**
         * @return the input directories.  Existence of files in the array has been checked.
         */
        public File[] getInputFiles() {
            return this.inputFiles;
        }
        
        /**
         * @return the input directories.  Existence of directories in the array has been checked.
         */
        public File[] getInputDirectories() {
            return this.inputDirectories;
        }
        
        /**
         * @return the classpath elements.  Never null.  May be empty.
         */
        public String[] getClassPath() {
            return this.classPath;
        }
        
        /**
         * @return the patterns.  Never null.  May be empty.
         */
        public JFit.Pattern[] getPatterns() {
            return this.patterns;
        }

        /**
         * @return the generation scope.  Never null.
         */
        public ForeignImportGenerator.GenerationScope getGenerationScope() {
            return this.generationScope;
        }

        /**
         * @return the methods to exclude.  Never null.  May be empty.
         * These should be of the form (fully-qualified class name).(method name).  eg. java.lang.Object.equals
         */
        public String[] getExcludeMethods() {
            return this.excludeMethods;
        }
    }
    
    /**
     * A class to encapsulate the command-line options passed to the JFit tool.
     * 
     * When this class is instantiated, the following are true:
     *   moduleName is a valid module name.
     *   workspaceName is provided
     *   patterns are specified.
     *   inputFile is non-null (for now) and exists.
     *   outputFolder exists if non-null.
     * 
     * @author Edward Lam
     */
    private static class CommandLineOptions {
        
        private static final String pathSeparator = System.getProperty("path.separator");
        
        private static final String[] usageString = {
            toolBoilerPlate,
            "Usage: java " + JFit.class.getName() + " [options] cal_moduleName",
            "   -cp classPath      additional classpath entries",
            "   -cpf classPathFile specify a file with additional classpath entries, one per line",
            "   -p classPatterns   specify class name patterns",
            "                      eg. \"my.desired.Clazz" + pathSeparator + "my.desired.classes.*_Test" + pathSeparator + "-my.desired.ExcludeClass\"",
            "   -pf patFile        file specifying desired class name patterns, one per line",
            "   -xm excludeMethods specify methods to exclude, qualified by class name",
            "                      eg. java.lang.Object.wait" + pathSeparator + "java.lang.Object.notify",
            "   -xmf exMethodFile  file specifying desired methods to exclude, one per line", 
            "   -o outputDir       specify folder in which to generate output",
            "   -f jarfile         specify one or more jar files which contains desired classes",
            "                      eg. \"lib\\classes1.jar" + pathSeparator + "lib\\classes2.jar\"",
            "   -d directory       specify one or more directories as the classpath root of desired classes",
            "                      eg. \".." + pathSeparator + "lib\"",
            "   -ws workspaceName  specify the name of the workspace",
            "   -public            specify public scope for generated types and functions",
            "   -private           specify private scope for generated types and functions (default)",
            "   -privateTypeImpl   specify public scope for generated types and functions, but private scope type implementations",
            "   -quiet, -q         be extra quiet",
            "   -verbose, -v       be extra verbose",
            "   -h                 this help message"
        };

        private final String workspaceName;
        private final File outputFolder;
        private final Level verbosity;
        private final Options options;

        /**
         * An internal exception which is thrown if there was a problem parsing the command line arguments.
         * @author Edward Lam
         */
        private static class ParseException extends Exception {
            private static final long serialVersionUID = -8214168472347843070L;

            ParseException() {
            }
        }
        
        /**
         * Private constructor for an Options object.
         * To instantiate, call parseOptions() or makeOptions().
         * 
         * @param workspaceName
         * @param outputFolder
         * @param verbosity the specified verbosity of the logger
         * @param options
         */
        private CommandLineOptions(String workspaceName, File outputFolder, Level verbosity, Options options) {
            this.workspaceName = workspaceName;
            this.outputFolder = outputFolder;  // may be null
            this.verbosity = verbosity;
            this.options = options;
        }

        /**
         * Dump the usage string to the logger.
         */
        private static void logUsageString(Logger logger) {
            for (final String usageLine : usageString) {
                logger.info(usageLine);
            }
        }
        
        /**
         * Parse command line arguments into options for this tool.
         * If there are problems parsing the options, null is returned and the usage string is logged.
         * 
         * @param args the command line arguments for this tool.
         * @param logger the logger to which to log any error messages.
         * @return the corresponding options object.  Null if there are problems with the command line options.
         */
        static CommandLineOptions parseOptions(String[] args, Logger logger) {
            
            if (args.length < 1) {
                logUsageString(logger);
                return null;
            }
            List<String> argList = new ArrayList<String>(Arrays.asList(args));
            
            // flag to indicate whether the usage string has already been logged via -h option.
            boolean loggedUsageStringForOption = false;
            
            String moduleName = null;
            String workspaceName = null;
            List<String> inputFileStrings = new ArrayList<String>();            // (List of String)
            List<String> inputDirectoryStrings = new ArrayList<String>();       // (List of String);
            File outputFolder = null;
            String[] classPath = null;
            String[] patterns = null;
            Level verbosity = null;
            ForeignImportGenerator.GenerationScope generationScope = null;
            String[] excludeMethods = null;
            
            try {
                while (!argList.isEmpty()) {
                    String nextArg = argList.get(0);
                    
                    // Handle non-dash argument.
                    // This should give the module name and the workspace name.
                    if (!nextArg.startsWith("-")) {
                        // Check that there is one arg remaining.
                        if (argList.size() != 1) {
                            throw new ParseException();
                        }
                        
                        moduleName = nextArg;
                        argList = Collections.emptyList();
                        
                    } else {
                        
                        // Save the option text.
                        String option = nextArg;
                        
                        // Shorten argList by one arg.
                        argList = argList.subList(1, argList.size());
                        
                        //
                        // Zero-argument options.
                        //
                        if (option.equals("-h")) {
                            if (!loggedUsageStringForOption) {
                                loggedUsageStringForOption = true;
                                logUsageString(logger);
                            }
                            
                        } else if (option.equals("-v") || option.equals("-verbose")) {
                            if (verbosity != null) {
                                logger.severe("Multiple verbosity options provided.");
                                throw new ParseException();
                            }
                            verbosity = Level.FINEST;
                            
                        } else if (option.equals("-q") || option.equals("-quiet")) {
                            if (verbosity != null) {
                                logger.severe("Multiple verbosity options provided.");
                                throw new ParseException();
                            }
                            verbosity = Level.SEVERE;
                            
                        } else if (option.equals("-public")) {
                            if (generationScope != null) {
                                logger.severe("Multiple generation scope options provided.");
                                throw new ParseException();
                            }
                            generationScope = ForeignImportGenerator.GenerationScope.ALL_PUBLIC;
                            
                        } else if (option.equals("-private")) {
                            if (generationScope != null) {
                                logger.severe("Multiple generation scope options provided.");
                                throw new ParseException();
                            }
                            generationScope = ForeignImportGenerator.GenerationScope.ALL_PRIVATE;
                            
                        } else if (option.equals("-privateTypeImpl")) {
                            if (generationScope != null) {
                                logger.severe("Multiple generation scope options provided.");
                                throw new ParseException();
                            }
                            generationScope = ForeignImportGenerator.GenerationScope.PARTIAL_PUBLIC;

                            //
                            // One-argument options.
                            //
                        } else {
                            // Make sure there's another argument
                            if (argList.size() < 1) {
                                throw new ParseException();
                            }
                            
                            // Get the argument to the option.
                            String optionArg = argList.get(0);
                            
                            // Shorten argList by another arg.
                            argList = argList.subList(1, argList.size());
                            
                            if (option.equals("-ws")) {
                                // The name of the workspace.
                                if (workspaceName != null) {
                                    logger.severe("Multiple workspace names provided.");
                                    throw new ParseException();
                                }
                                workspaceName = optionArg;
                                
                            } else if (option.equals("-cp")) {
                                // Classpath provided on the command line.
                                if (classPath != null) {
                                    logger.severe("Multiple classpath options provided.");
                                    throw new ParseException();
                                }
                                
                                classPath = pathsToStrings(optionArg);
                                
                            } else if (option.equals("-cpf")) {
                                // Read the classpath from a file.
                                if (classPath != null) {
                                    logger.severe("Multiple classpath options provided.");
                                    throw new ParseException();
                                }
                                
                                classPath = fileToStrings(optionArg, logger);
                                if (classPath == null) {
                                    // There was a problem reading the file.
                                    return null;
                                }
                                
                            } else if (option.equals("-p")) {
                                // Patterns provided on the command line.
                                if (patterns != null) {
                                    logger.severe("Multiple pattern options provided.");
                                    throw new ParseException();
                                }
                                
                                patterns = pathsToStrings(optionArg);
                                
                            } else if (option.equals("-pf")) {
                                // Patterns provided from a file.
                                if (patterns != null) {
                                    logger.severe("Multiple pattern options provided.");
                                    throw new ParseException();
                                }
                                
                                patterns = fileToStrings(optionArg, logger);
                                if (patterns == null) {
                                    // There was a problem reading the file.
                                    return null;
                                }
                                
                            } else if (option.equals("-xm")) {
                                // Exclude methods provided on the command line.
                                if (excludeMethods != null) {
                                    logger.severe("Multiple exclude method options provided.");
                                    throw new ParseException();
                                }
                                excludeMethods = pathsToStrings(optionArg);
                                
                            } else if (option.equals("-xmf")) {
                                // Exclude methods provided from a file.
                                if (excludeMethods != null) {
                                    logger.severe("Multiple exclude method options provided.");
                                    throw new ParseException();
                                }
                                excludeMethods = fileToStrings(optionArg, logger);
                                if (excludeMethods == null) {
                                    // There was a problem reading the file.
                                    return null;
                                }
                                
                            } else if (option.equals("-f")) {
                                // Input jar file.
                                inputFileStrings.addAll(Arrays.asList(pathsToStrings(optionArg)));
                                
                            } else if (option.equals("-d")) {
                                // Input directory.
                                inputDirectoryStrings.addAll(Arrays.asList(pathsToStrings(optionArg)));
                                
                            } else if (option.equals("-o")) {
                                // output folder
                                if (outputFolder != null) {
                                    logger.severe("Multiple output folders specified.");
                                    throw new ParseException();
                                }
                                outputFolder = new File(optionArg);
                                
                            } else {
                                logger.severe("Unknown option: " + option);
                                throw new ParseException();
                            }
                        }
                    }
                }
            
            } catch (ParseException pe) {
                // Some problem occurred parsing the arguments.
                // Log the usage string and return null.
                if (!loggedUsageStringForOption) {
                    logUsageString(logger);
                }
                return null;
            }
            
            File[] inputFiles = new File[inputFileStrings.size()];
            {
                int index = 0;
                for (final String inputFileString : inputFileStrings) {
                    inputFiles[index] = new File(inputFileString);
                    index++;
                }
            }

            File[] inputDirectories = new File[inputDirectoryStrings.size()];
            {
                int index = 0;
                for (final String inputDirString : inputDirectoryStrings) {
                    inputDirectories[index] = new File(inputDirString);
                    index++;
                }
            }

            return makeOptions(moduleName, workspaceName, inputFiles, inputDirectories, outputFolder, 
                    classPath, patterns, generationScope, excludeMethods, verbosity, logger);
        }
        
        /**
         * Create a command-line options object given the arguments.
         * 
         * @param moduleName
         * @param workspaceName the name of the workspace, or null for the default workspace.
         * @param inputFiles
         * @param inputDirectories
         * @param outputFolder
         * @param classPath
         * @param patternStrings
         * @param generationScope
         * @param excludeMethods
         * @param verbosity
         * @param logger the logger to which to log any errors.
         * 
         * @return the options object, or null if there were errors in the provided option arguments.
         * If null, errors will have been logged to the logger.
         */
        public static CommandLineOptions makeOptions(String moduleName, String workspaceName, File[] inputFiles, File[] inputDirectories, File outputFolder, String[] classPath, String[] patternStrings, ForeignImportGenerator.GenerationScope generationScope, String[] excludeMethods, Level verbosity, Logger logger) {
           
            JFit.Pattern[] patterns = getPatterns(patternStrings);
            
            Options jfitOptions = Options.makeOptions(moduleName, inputFiles, inputDirectories, classPath, patterns, generationScope, excludeMethods, logger);
            
            if (jfitOptions == null) {
                return null;
            }
            
            return new CommandLineOptions(workspaceName, outputFolder, verbosity, jfitOptions);
        }
        
        /**
         * Convert the command line patterns to Pattern objects.
         * @param commandLinePats the strings specified on the command line for patterns.
         * @return the corresponding Pattern objects.
         */
        private static JFit.Pattern[] getPatterns(String[] commandLinePats) {
            
            if (commandLinePats == null) {
                return new JFit.Pattern[] {};
            }
            
            int nPats = commandLinePats.length;
            JFit.Pattern[] patterns = new JFit.Pattern[nPats];
            
            for (int i = 0; i < nPats; i++) {
                String commandLinePat = commandLinePats[i];
                
                boolean isInclude;
                String patternString;
                if (commandLinePat.startsWith("-")) {
                    isInclude = false;
                    patternString = commandLinePat.substring(1);
                } else {
                    isInclude = true;
                    patternString = commandLinePat;
                }
                
                patterns[i] = new Pattern(patternString, isInclude);
            }
            
            return patterns;
        }
        
        /**
         * Split up a string delimited with path separators into its component paths.
         * eg. if ";" is the path separator, 
         *     "foo;bar;baz" -> {"foo", "bar", "baz"}
         * 
         * @param stringWithPaths the string with path separators.
         * @return the component paths.
         */
        private static String[] pathsToStrings(String stringWithPaths) {
            // Read the classpath
            List<String> classPathList = new ArrayList<String>();

            while (true) {
                int pathSeparatorIndex = stringWithPaths.indexOf(pathSeparator);
                
                if (pathSeparatorIndex < 0) { 
                    // The last entry -- no more separators.
                    stringWithPaths = stringWithPaths.trim();
                    if (stringWithPaths.length() > 0) {
                        classPathList.add(stringWithPaths);
                    }
                    break;
                }
                
                // The next class path entry is everything up to before the next separator.
                String classPathEntry = stringWithPaths.substring(0, pathSeparatorIndex).trim();
                if (classPathEntry.length() > 0) {
                    classPathList.add(classPathEntry);
                }
                stringWithPaths = stringWithPaths.substring(pathSeparatorIndex + 1);

            }
            
            return classPathList.toArray(new String[classPathList.size()]);
        }
        
        /**
         * Read a file, and return the lines as an array of strings.
         * Leading and trailing whitespace are ignored.
         * 
         * TODOEL: accept comments.
         * 
         * @param fileName the name of the file to read.
         * @param logger the logger to which to log any error messages.
         * @return The lines from the file. If there was a problem reading the file, null (messages will be logged).
         */
        private static String[] fileToStrings(String fileName, Logger logger) {
            BufferedReader classPathReader = null;
            try {
                classPathReader = new BufferedReader(new FileReader(fileName));
                
                List<String> classPathList = new ArrayList<String>();
                
                for (String lineText = classPathReader.readLine(); lineText != null; lineText = classPathReader.readLine()) {
                    lineText = lineText.trim();
                    
                    if (lineText.length() != 0) {
                        classPathList.add(lineText);
                    }
                }
                
                return classPathList.toArray(new String[classPathList.size()]);
                
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Can't read file: " + fileName, e);
                return null;
                
            } finally {
                if (classPathReader != null) {
                    try {
                        classPathReader.close();
                    } catch (IOException e) {
                        // can't do much about this..
                    }
                }
            }
        }
        
        public String getWorkspaceName() {
            return this.workspaceName;
        }
        
        public File getOutputFolder() {
            return this.outputFolder;
        }
        
        /**
         * @return the specified verbosity (Logging) level.  May be null if not specified.
         */
        public Level getVerbosity() {
            return verbosity;
        }
        
        public Options getJFitOptions() {
            return options;
        }
    }

    /**
     * A simple container class to encapsulate a pattern to match and whether it is an include or an exclude pattern.
     * @author Edward Lam
     */
    public static class Pattern {
        /** The pattern to match everything. */
        public static final Pattern MATCH_ALL = new JFit.Pattern("*", true);
        
        private final String pattern;
        private final boolean include;
        
        /**
         * Constructor for a JFit.Pattern.
         * @param pattern the pattern.
         * @param include true for an include pattern, false for an exclude pattern.
         */
        public Pattern(String pattern, boolean include) {
            this.pattern = pattern;
            this.include = include;
        }
        
        /**
         * @return true for an include pattern, false for an exclude pattern.
         */
        public boolean isInclude() {
            return this.include;
        }
        
        /**
         * @return the pattern.
         */
        public String getPattern() {
            return this.pattern;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean equals(Object obj) {
            if (obj instanceof Pattern) {
                Pattern otherPattern = (Pattern)obj;
                return include == otherPattern.include && pattern.equals(otherPattern.pattern);
            }
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        public int hashCode() {
            return pattern.hashCode() + (include ? 17 : 37);
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            return "(" + (include ? "include: " : "exclude: ") + pattern + ")";
        }
    }

    /**
     * An exception to signal that a class found to be required by JFit is invalid or not present on the classpath.
     * @author Edward Lam
     */
    public static class MissingClassException extends Exception {
        private static final long serialVersionUID = 6846885687084625419L;

        /** The name of the missing class. */
        private final String missingClassName;
        
        /** The name of the class which requires the class which is missing. */
        private final String requiringClassName;
        
        /**
         * Constructor for a MissingClassException.
         * @param requiringClassName the name of the class requiring the missing class.
         * @param e the exception signalling the missing class.
         */
        MissingClassException(String requiringClassName, ClassNotFoundException e) {
            this.requiringClassName = requiringClassName;
            this.missingClassName = e.getMessage().replace('/','.');
        }
        
        /**
         * Constructor for a MissingClassException.
         * @param requiringClassName the name of the class requiring the missing class.
         * @param e the exception signalling the missing class.
         */
        MissingClassException(String requiringClassName, NoClassDefFoundError e) {
            this.requiringClassName = requiringClassName;
            String message = e.getMessage();
            this.missingClassName = message == null ? null : message.replace('/','.');
        }
        
        /**
         * Constructor for a MissingClassException.
         * @param requiringClassName the name of the class requiring the missing class.
         * @param e the exception signalling the missing class.
         */
        MissingClassException(String requiringClassName, LinkageError e) {
            this.requiringClassName = requiringClassName;
            this.missingClassName = null;
        }
        
        /**
         * @return the name of the class requiring the missing class.
         */
        String getRequiringClassName() {
            return requiringClassName;
        }
        
        /**
         * @return the name of the missing class.
         * This may return null if this could not be determined.
         */
        String getMissingClassName() {
            return missingClassName;
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            if (missingClassName == null) {
                return "MissingClassException - could not load definition of class: " + requiringClassName;
            } else {
                return "MissingClassException - missing: " + missingClassName + ", required by: " + requiringClassName;
            }
        }
    }
    
    /**
     * The command-line entry point.
     * @param args the command-line args.
     */
    public static void main(String[] args) {
        
        Logger commandLineLogger = Logger.getLogger(JFit.class.getPackage().getName());
        commandLineLogger.setLevel(Level.FINEST);
        commandLineLogger.setUseParentHandlers(false);
        
        StreamHandler consoleHandler = new SimpleConsoleHandler();

        consoleHandler.setLevel(Level.INFO);
        commandLineLogger.addHandler(consoleHandler);
        
        CommandLineOptions commandLineOptions = CommandLineOptions.parseOptions(args, commandLineLogger);

        if (commandLineOptions == null) {
            // There was an error in the provided command-line args.
            return;
        }
        Level verbosity = commandLineOptions.getVerbosity();
        if (verbosity != null) {
            consoleHandler.setLevel(verbosity);
        }
        
        // Log the boilerplate message.
        commandLineLogger.info(toolBoilerPlate);
        commandLineLogger.info("  '-h' for help.");

        
        // Log the specified options.
        String notSpecifiedString = "(not specified)";
        String noneSpecifiedString = "(none specified)";
        
        Options jFitOptions = commandLineOptions.getJFitOptions();
        
        commandLineLogger.config("Specified options: ");
        
        ModuleName moduleName = jFitOptions.getModuleName();
        commandLineLogger.config("  Module name:    " + (moduleName == null ? notSpecifiedString : moduleName.toSourceText()));
        
        String workspaceName = commandLineOptions.getWorkspaceName();
        commandLineLogger.config("  Workspace name: " + (workspaceName == null ? notSpecifiedString : workspaceName));
        
        File[] inputFiles = jFitOptions.getInputFiles();
        if (inputFiles.length == 0) {
            commandLineLogger.config("  Input file:     " + noneSpecifiedString);
        } else {
            for (final File inputFile : inputFiles) {
                commandLineLogger.config("  Input file:     " + inputFile.getPath());
            }
        }
        
        File[] inputDirectories = jFitOptions.getInputDirectories();
        if (inputDirectories.length == 0) {
            commandLineLogger.config("  Input dir:      " + noneSpecifiedString);
        } else {
            for (final File inputDirectory : inputDirectories) {
                commandLineLogger.config("  Input dir:      " + inputDirectory.getPath());
            }
        }
        
        File outputFolder = commandLineOptions.getOutputFolder();
        commandLineLogger.config("  Output folder:  " + (outputFolder == null ? notSpecifiedString : outputFolder.getPath()));
        
        String[] classPath = jFitOptions.getClassPath();
        if (classPath.length == 0) {
            commandLineLogger.config("  Class path:     " + noneSpecifiedString);

        } else {
            for (final String classPathEntry : classPath) {
                commandLineLogger.config("  Class path entry: " + classPathEntry);
            }
        }
        Pattern[] patterns = jFitOptions.getPatterns();
        if (patterns.length == 0) {
            commandLineLogger.config("  Patterns:       " + noneSpecifiedString);

        } else {
            commandLineLogger.config("  Patterns: ");
            for (final Pattern nthPattern : patterns) {
                String patternDescription = (nthPattern.isInclude() ? "include" : "exclude") + ": \"" + nthPattern.getPattern() + "\"";
                commandLineLogger.config("    " + patternDescription);
            }
        }

        // If there is an output folder, ensure it exists.
        if (outputFolder != null && !outputFolder.isDirectory()) {
            if (!FileSystemHelper.ensureDirectoryExists(outputFolder)) {
                commandLineLogger.severe("Output folder \'" + outputFolder.getPath() + "\' does not exist, and could not be created.");
                return;
            }
        }
        
        JFit jfit = new JFit(commandLineOptions, commandLineLogger);
        jfit.fitToFile(outputFolder);
    }
    
    /**
     * Constructor for a JFit.
     * @param options
     * @param workspace
     * @param logger
     */
    public JFit(Options options, CALWorkspace workspace, Logger logger) {
        
        if (logger == null) {
            throw new NullPointerException("Argument 'logger' cannot be null.");
        }
        this.jfitLogger = logger;
        
        if (options == null) {
            // No provided options.
            throw new NullPointerException("Argument 'options' cannot be null.");
        }
        this.options = options;
        
        this.streamProvider = null;
        this.calWorkspace = workspace;
    }
    
    /**
     * Constructor for a JFit.
     * @param commandLineOptions
     * @param logger
     */
    private JFit(CommandLineOptions commandLineOptions, Logger logger) {
        
        if (logger == null) {
            throw new NullPointerException("Argument 'logger' cannot be null.");
        }
        this.jfitLogger = logger;
        
        if (commandLineOptions == null) {
            // No provided options.
            throw new NullPointerException("Argument 'commandLineOptions' cannot be null.");
        }
        this.options = commandLineOptions.getJFitOptions();
        
        String workspaceName = commandLineOptions.getWorkspaceName();
        if (workspaceName == null) {
            workspaceName = DEFAULT_WORKSPACE_NAME;
        }
        
        streamProvider = DefaultWorkspaceDeclarationProvider.getDefaultWorkspaceDeclarationProvider(workspaceName);
        if (streamProvider == null) {
            logger.severe("Workspace not found: " + commandLineOptions.getWorkspaceName());
        }
        
        // the workspace will be set on a call to compileWorkspace().
        this.calWorkspace = null;
    }
    
    /**
     * Automatically figure out what the required classes and functions are, generate them, output to a file.
     * Does nothing if there was a problem with the command-line options.
     */
    private void fitToFile(File outputFolder) {
        
        ModuleDefn defn = autoFit();
        if (defn == null) {
            jfitLogger.info("");
            jfitLogger.info("Generation failed.");
            return;
        }
        
        String sourceText = defn.toSourceText();
        
        String[] moduleNameQualifier = defn.getModuleName().getQualifier().getComponents();
        String unqualifiedModuleName = defn.getModuleName().getUnqualifiedModuleName();
        
        File fileFolder = outputFolder;
        for (final String component : moduleNameQualifier) {
            if (fileFolder == null) {
                fileFolder = new File(component);
            } else {
                fileFolder = new File(fileFolder, component);
            }
        }
        
        if (fileFolder != null) {
            if (!FileSystemHelper.ensureDirectoryExists(fileFolder)) {
                jfitLogger.severe("The folder \'" + fileFolder.getPath() + "\' does not exist, and could not be created.");
                return;
            }
        }
        
        String fileName = unqualifiedModuleName + "." + CALSourcePathMapper.INSTANCE.getFileExtension();
        File outputFile = fileFolder == null ? new File(fileName) : new File(fileFolder, fileName);
        
        jfitLogger.info("Writing file: " + outputFile.getAbsolutePath() + "..");
        
        boolean wroteFile;
        
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(sourceText);
            wroteFile = true;
            
        } catch (IOException e) {
            jfitLogger.log(Level.SEVERE, "Error writing file: " + outputFile.getPath(), e);
            wroteFile = false;

        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    // Not much we can do about this.
                }
            }
        }
        
        if (wroteFile) {
            int nGeneratedFunctions = 0;
            int nGeneratedTypes = 0;
            
            int nTopLevelDefns = defn.getNTopLevelDefns();
            for (int i = 0; i < nTopLevelDefns; i++) {
                TopLevelSourceElement nthTopLevelDefn = defn.getNthTopLevelDefn(i);
                
                if (nthTopLevelDefn instanceof SourceModel.FunctionDefn.Foreign) {
                    nGeneratedFunctions++;
                
                } else if (nthTopLevelDefn instanceof SourceModel.TypeConstructorDefn.ForeignType) {
                    nGeneratedTypes++;
                } 
            }
            jfitLogger.info("");
            jfitLogger.info("Generation succeeded.");
            jfitLogger.info(nGeneratedTypes + " types, " + nGeneratedFunctions + " functions generated.");
            
        } else {
            jfitLogger.info("");
            jfitLogger.info("Generation failed.");
        }
    }
    
    /**
     * The function which automatically does all the work.
     * Figure out what the required classes and functions are, and generate them.
     * Does nothing if there was a problem with the options.
     * 
     * Note: the outputFolder option is ignored..
     */
    public ModuleDefn autoFit() {
        if (options == null) {
            return null;
        }
        ModuleName calModuleName = options.getModuleName();
        JFit.Pattern[] patterns = options.getPatterns();
        File[] inputFiles = options.getInputFiles();
        File[] inputDirectories = options.getInputDirectories();
        String[] classPath = options.getClassPath();
        ForeignImportGenerator.GenerationScope generationScope = options.getGenerationScope();
        String[] excludeMethods = options.getExcludeMethods();
        
        JarClassAnalyzer jarClassAnalyzer = JarClassAnalyzer.getAnalyzer(inputFiles, inputDirectories, classPath, jfitLogger);
        if (jarClassAnalyzer == null) {
            return null;
        }
        
        // (List of String) fully-qualified matching class names.
        Set<String> matchingClassNames = jarClassAnalyzer.getMatchingClassNames(patterns, jfitLogger);
        if (matchingClassNames == null) {
            return null;
        }
        
        if (matchingClassNames.isEmpty()) {
            jfitLogger.severe("No classes match the provided patterns.");
            return null;
        }
        
        Set<Class<?>> requiredClassSet;
        try {
            requiredClassSet = jarClassAnalyzer.calculateRequiredClasses(matchingClassNames, jfitLogger);
            
        } catch (MissingClassException e) {
            String missingClassName = e.getMissingClassName();
            if (missingClassName == null) {
                jfitLogger.log(Level.SEVERE, "Could not load definition of class " + e.getRequiringClassName(), e);
            } else {
                jfitLogger.log(Level.SEVERE, "Missing class " + e.getMissingClassName() + " required by class " + e.getRequiringClassName(), e);
            }
            return null;
        }
        
        if (requiredClassSet.isEmpty()) {
            jfitLogger.severe("No required classes to generate.");
            return null;
        }
        
        // Generate map (Class->Set of String) from class to method names for methods to exclude.
        Map<Class<?>, Set<String>> classToMethodExcludeNamesMap = new HashMap<Class<?>, Set<String>>();
        for (final String excludeMethodString : excludeMethods) {
            
            int lastDotIndex = excludeMethodString.lastIndexOf('.');

            if (lastDotIndex < 0) {
                jfitLogger.warning("Unqualified exclude method: \"" + excludeMethodString + "\"");
                jfitLogger.warning("Exclude patterns must be of the form: (fully-qualified-class name).(method name).  eg. java.lang.Object.equals");
                continue;
            }
            
            String qualifiedClassName = excludeMethodString.substring(0, lastDotIndex);
            String methodName = excludeMethodString.substring(lastDotIndex + 1);
            
            Class<?> excludeClass;
            try {
                excludeClass = jarClassAnalyzer.getClass(qualifiedClassName);
            
            } catch (MissingClassException e) {
                jfitLogger.warning("Exclude method: \"" + excludeMethodString + "\" - cannot find class \"" + qualifiedClassName + "\".");
                continue;
            }
            
            boolean hasMethod = false;
            Method[] methods = excludeClass.getMethods();
            for (final Method method : methods) {
                if (method.getName().equals(methodName)) {
                    hasMethod = true;
                    break;
                }
            }
            
            if (!hasMethod) {
                jfitLogger.warning("Exclude class \"" + qualifiedClassName + "\" does not have any method named \"" + methodName + "\".");
                continue;
            }
            
            // Add the mapping.
            Set<String> classExcludesSet = classToMethodExcludeNamesMap.get(excludeClass);
            if (classExcludesSet == null) {
                classExcludesSet = new HashSet<String>();
                classToMethodExcludeNamesMap.put(excludeClass, classExcludesSet);
            }
            classExcludesSet.add(methodName);
        }
        
        if (streamProvider != null) {
            // Compile the workspace.
            if (!compileWorkspace(true, false)) {
                // Compilation failed.
                return null;
            }
        }
        
        jfitLogger.info("Generating module definition..");
        
        try {
            return ForeignImportGenerator.makeDefaultModuleDefn(
                    calModuleName, matchingClassNames, requiredClassSet, generationScope, classToMethodExcludeNamesMap, calWorkspace);
            
        } catch (UnableToResolveForeignEntityException e) {
            jfitLogger.severe(e.getCompilerMessage().toString());
            return null;
        }
    }
    
    /**
     * Builds the program object using the CAL file at the current location.
     * Create a new workspace if one does not already exist.  Does nothing if this fails.
     * @param dirtyOnly true
     * @param forceCodeRegen false
     * @return whether compilation succeeded.
     */    
    private boolean compileWorkspace(boolean dirtyOnly, boolean forceCodeRegen) {

        jfitLogger.info("Compiling CAL workspace..");
        
        String clientID = WorkspaceConfiguration.getDiscreteWorkspaceID(DEFAULT_WORKSPACE_CLIENT_ID);
        WorkspaceManager workspaceManager = WorkspaceManager.getWorkspaceManager(clientID);
        
        // Add a status listener to log when modules are loaded.
        workspaceManager.addStatusListener(new StatusListener.StatusListenerAdapter() {
            public void setModuleStatus(StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
                if (moduleStatus == StatusListener.SM_LOADED) {
                    jfitLogger.fine("  Module loaded: " + moduleName);
                }
            }
        });

        
        CompilerMessageLogger ml = new MessageLogger();
        
        // Init and compile the workspace.
        Status initStatus = new Status("Init status.");
        workspaceManager.initWorkspace(streamProvider, initStatus);
        
        if (initStatus.getSeverity() != Status.Severity.OK) {
            ml.logMessage(initStatus.asCompilerMessage());
        }
        
        long startCompile = System.currentTimeMillis();
        
        // If there are no errors go ahead and compile the workspace.
        if (ml.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0) {
            WorkspaceManager.CompilationOptions options = new WorkspaceManager.CompilationOptions();
            options.setForceCodeRegeneration(forceCodeRegen);
            workspaceManager.compile(ml, dirtyOnly, null, options);
        }
        
        long compileTime = System.currentTimeMillis() - startCompile;
        
        boolean compilationSucceeded;
        if (ml.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
            // Errors
            jfitLogger.severe("Compilation unsuccessful because of errors:");
            compilationSucceeded = false;
        } else {
            // Compilation successful
            jfitLogger.fine("Compilation successful");
            compilationSucceeded = true;
        }
        
        // Write out compiler messages
        java.util.List<CompilerMessage> errs = ml.getCompilerMessages();
        int errsSize = errs.size();
        for (int i = 0; i < errsSize; i++) {
            CompilerMessage err = errs.get(i);
            jfitLogger.info("  " + err.toString());
        }
        
        jfitLogger.fine("CAL: Finished compiling in " + compileTime + "ms");
        
        this.calWorkspace = workspaceManager.getWorkspace();
        
        return compilationSucceeded;
    }
    
    /**
     * Analyzes a Jar file to calculate the classes required to import desired Java functions and type from the classes in the jar.
     * @author Edward Lam
     */
    private static class JarClassAnalyzer {
        
        /** The classloader with access to the classpath and the .jar file. */
        private final ClassLoader classLoader;
        
        private final File[] jars;
        private final File[] directoryRoots;
        
        /**
         * Constructor for a JarClassAnalyzer.
         * Not intended to be called by other classes.  To instantiate, call getAnalyzer().
         * @param classLoader the classLoader with access to the classpath and the .jar file.
         */
        private JarClassAnalyzer(ClassLoader classLoader, File[] jars, File[] directoryRoots) {
            this.classLoader = classLoader;
            this.jars = jars;
            this.directoryRoots = directoryRoots;
        }
        
        /**
         * Factory method for this class.
         * 
         * @param inputFiles the jar files to analyze.
         * @param inputDirectories the directory roots to analyze.
         * @param classPath additional class path entries.
         * @param logger the logger to which to log any messages.
         * @return an instance of this class.
         */
        public static JarClassAnalyzer getAnalyzer(File[] inputFiles, File[] inputDirectories, String[] classPath, Logger logger) {
            
            // If there are no analysis roots, add the current folder as the default
            int nAnalysisRoots = inputFiles.length + inputDirectories.length;
            if (nAnalysisRoots == 0) {
                File currentDir = new File(".");
                inputFiles = new File[] {currentDir};
                logger.fine("Implicit input dir: " + currentDir);
                nAnalysisRoots++;
            }

            final URL[] classPathURLs = new URL[classPath.length + nAnalysisRoots];
            for (int i = 0; i < classPath.length; i++) {
                File classPathFile = new File(classPath[i]);
                try {
                    if (classPathFile.isDirectory()) {
                        classPathURLs[i] = classPathFile.toURL();
                    } else {
                        classPathURLs[i] = new URL("jar", "", "file:/" + classPath[i].replace('\\', '/') + "!/");
                    }

                } catch (MalformedURLException e) {
                    logger.log(Level.SEVERE, "Invalid path entry: " + classPath[i], e);
                    return null;
                }
            }
            
            for (int i = 0; i < inputFiles.length; i++) {
                File inputFile = inputFiles[i];
                try {
                    if (FileSystemHelper.fileExists(inputFile)) {
                        // It's a file.
                        // TODOEL: Could we use File.toURL() here?
                        classPathURLs[classPath.length + i] = new URL("jar", "", "file:/" + inputFile.getPath().replace('\\', '/') + "!/");
                        
                    } else {
                        logger.severe("Invalid path entry: " + inputFile.getPath());
                        return null;
                    }
                    
                } catch (MalformedURLException e) {
                    logger.log(Level.SEVERE, "Invalid path entry: " + inputFile.getPath(), e);
                    return null;
                }
            }
            for (int i = 0; i < inputDirectories.length; i++) {
                File inputDirectory = inputDirectories[i];
                
                // Convert to a URI.
                URI uri;
                {
                    // Slashify.  See File.slashify() and File.toURI().
                    String slashifiedPath = inputDirectory.getAbsolutePath();
                    if (File.separatorChar != '/') {
                        slashifiedPath = slashifiedPath.replace(File.separatorChar, '/');
                    }
                    if (!slashifiedPath.startsWith("/")) {
                        slashifiedPath = "/" + slashifiedPath;
                    }
                    if (!slashifiedPath.endsWith("/")) {            // directories must end with a slash.
                        slashifiedPath = slashifiedPath + "/";
                    }
                    if (slashifiedPath.startsWith("//")) {
                        slashifiedPath = "//" + slashifiedPath;
                    }
                    
                    // Create the uri.
                    try {
                        uri = new URI("file", null, slashifiedPath, null);
                    } catch (URISyntaxException e) {
                        // The comment to File.toURI() says this can't happen.
                        logger.log(Level.SEVERE, "Error converting directory to uri.", e);
                        return null;
                    }
                }
                
                try {
                    if (inputDirectory.isDirectory()) {
                        // It's a folder.
                        classPathURLs[classPath.length + inputFiles.length + i] = uri.toURL();
                        
                    } else {
                        logger.severe("Invalid path entry: " + inputDirectory.getPath());
                        return null;
                    }
                    
                } catch (MalformedURLException e) {
                    logger.log(Level.SEVERE, "Invalid path entry: " + inputDirectory.getPath(), e);
                    return null;
                }
            }
            
            // Need a privileged block to create the class loader
            URLClassLoader ucl =
                AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
                    public URLClassLoader run() {
                        return new URLClassLoader(classPathURLs);
                    }
                });
            return new JarClassAnalyzer(ucl, inputFiles, inputDirectories);
        }
        
        /**
         * Get a class by name, using this analyzer's class path.
         * @param className the fully-qualified name of the class to retrieve.
         * @return the corresponding class.
         * @throws MissingClassException if the class does not exist.
         */
        Class<?> getClass(String className) throws MissingClassException {
            try {
                return classLoader.loadClass(className);
            
            } catch (ClassNotFoundException e) {
                // Wrap the exception.
                MissingClassException throwMe = new MissingClassException(className, e);
                throw throwMe;
            
            } catch (NoClassDefFoundError e) {
                // Wrap the exception.
                MissingClassException throwMe = new MissingClassException(className, e);
                throw throwMe;
            }
        }
        
        /**
         * Calculate the classes required to define all constructors, methods, and fields in the named classes.
         * @param desiredClassNames (Set of String) the fully-qualified names of the desired classes.
         * @return (Set of Class) the classes required.  Never null.
         * @throws MissingClassException 
         */
        Set<Class<?>> calculateRequiredClasses(Set<String> desiredClassNames, Logger logger) throws MissingClassException {
            
            Set<Class<?>> requiredClassesSet = new HashSet<Class<?>>();

            for (final String matchingClassName : desiredClassNames) {
                Class<?> matchingClass = null;
                try {
                    matchingClass = getClass(matchingClassName);
                
                } catch (MissingClassException e) {
                    // Log and rethrow.
                    logger.throwing(getClass().getName(), "calculateRequiredClasses", e);
                    throw e;
                }
                
                // These calls currently require referenced classes to be loaded.
                // This would cause a NoClassDefFoundError if the class is not on the classpath.
                // However, we can do something smarter if we don't have to load classes in order to analyze them.
                //   eg. Use asm instead of the ClassLoader, to inspect the bytecodes.
                // For instance, if we decide not to create a foreign function for a method, it won't be
                //   necessary to load the classes it refers to.
                try {
                    // Classes required by this particular matching class.
                    // We add this at the end, since in the meantime if we run across a NoClassDefFoundError, we will be
                    //   skipping adding any classes required by this class.
                    Set<Class<?>> newRequiredClassesSet = new HashSet<Class<?>>();
                    
                    newRequiredClassesSet.add(matchingClass);
                    
                    Constructor<?>[] constructors = matchingClass.getConstructors();
                    for (final Constructor<?> constructor : constructors) {
                        Class<?>[] parameterTypes = constructor.getParameterTypes();
                        newRequiredClassesSet.addAll(Arrays.asList(parameterTypes));
                        
                        // Don't add exceptions for now.
                    }
                    
                    Field[] fields = matchingClass.getFields();
                    for (final Field field : fields) {
                        // If necessary, we can add fields declared only in this class (ie. not in a superclass)
                        //  by checking whether fields[i].getDeclaringClass() == matchingClass.
                        
                        newRequiredClassesSet.add(field.getType());
                    }
                    
                    Method[] methods = matchingClass.getMethods();
                    for (final Method method : methods) {
                        newRequiredClassesSet.addAll(Arrays.asList(method.getParameterTypes()));
                        newRequiredClassesSet.add(method.getReturnType());
                        
                        // Don't add exceptions for now.
                    }
                    
                    requiredClassesSet.addAll(newRequiredClassesSet);
                    
                } catch (NoClassDefFoundError e) {
                    MissingClassException throwMe = new MissingClassException(matchingClassName, e);
                    logger.throwing(getClass().getName(), "calculateRequiredClasses", throwMe);
                    throw throwMe;
                } catch (LinkageError e) {
                    MissingClassException throwMe = new MissingClassException(matchingClassName, e);
                    logger.throwing(getClass().getName(), "calculateRequiredClasses", throwMe);
                    throw throwMe;
                }
                /*
                 Class[] interfaces = matchingClass.getInterfaces();
                 Package classPackage = matchingClass.getPackage();
                 Class superClass = matchingClass.getSuperclass();
                 */
            }
            
            // Log all required classes.
            logger.fine("Required classes:");
            
            if (requiredClassesSet.isEmpty()) {
                logger.fine("  (none)");
                
            } else {
                SortedSet<String> requiredClassNameSet = new TreeSet<String>();
                for (final Class<?> requiredClass : requiredClassesSet) {
                    requiredClassNameSet.add(requiredClass.getName());
                }
                for (final String requiredClassName : requiredClassNameSet) {
                    logger.fine("  " + requiredClassName);
                }       
            }
            
            return requiredClassesSet;
        }
        
        /**
         * Get the class names in a jar matching a given pattern.
         * @param patterns the patterns against which to match.
         * @param logger the logger to which to log any error messages.
         * @return (Set of String) fully-qualified matching class names.
         */
        Set<String> getMatchingClassNames(JFit.Pattern[] patterns, Logger logger) {
            
            Set<String> matchingClassNames = new HashSet<String>();
            
            // Split into includes and excludes.
            List<String> includePatternList = new ArrayList<String>();
            List<String> excludePatternList = new ArrayList<String>();
            for (final Pattern pattern : patterns) {
                if (pattern.isInclude()) {
                    // an include pattern
                    String patternString = pattern.getPattern();
                    includePatternList.add(patternString);
                    logger.fine("Include pattern: " + patternString);
                    
                } else {
                    // an exclude pattern
                    String patternString = pattern.getPattern();
                    excludePatternList.add(patternString);
                    logger.fine("Exclude pattern: " + patternString);
                }
            }
            
            // If there are no include patterns, add a wildcard "*" pattern.
            if (includePatternList.isEmpty()) {
                String patternString = JFit.Pattern.MATCH_ALL.getPattern();
                includePatternList.add(patternString);
                logger.fine("Implicit include pattern: " + patternString);
            }

            // Calculate matching classes from the jar files.
            for (final File jar : jars) {
                logger.info("Calculating matching classes from jar file " + jar.getName() + "..");
                
                // Create the jar file.
                JarFile jarFile;
                try {
                    jarFile = new JarFile(jar);
                    
                } catch (IOException e) {
                    // TODOEL: Does this get thrown if the path name is too long?
                    logger.log(Level.SEVERE, "Error reading jar file: " + jar.getPath(), e);
                    return null;
                }
                
                getMatchingClassNamesHelper(jarFile, includePatternList, excludePatternList, matchingClassNames, logger);
            }
            
            // Calculate matching classes from the directory roots.
            for (final File directoryRoot : directoryRoots) {
                logger.info("Calculating matching classes from directory \"" + directoryRoot.getName() + "\"..");

                getMatchingClassNamesHelper(directoryRoot, includePatternList, excludePatternList, matchingClassNames, null, new HashSet<File>(), logger);
            }
            
            return matchingClassNames;
        }

        /**
         * Helper method for getMatchingClassNames().
         * Get the class names starting from a directory root matching a given pattern.
         * 
         * @param directoryRoot the root of the directory to analyze.
         * @param includePatternList (List of String) the include patterns
         * @param excludePatternList (List of String) the exclude patterns
         * @param matchingClassNames (Set of String) the matching class names.  This collection will be populated by this method.
         * @param parentPackagePart A string indicating the current partial package name.
         * ie. if starting from the root, we have entered a directory named "com", and further entered a subdirectory of "com" named
         *   "businessobjects", this will be "com.businessobjects.".
         * On the first call to this method, this should be null.
         * @param visitedDirSet (Set of File) the files visited so far.  This collection will be populated by this method.
         * @param logger the logger to which to log any error messages.
         */
        private static void getMatchingClassNamesHelper(File directoryRoot, List<String> includePatternList, List<String> excludePatternList, Set<String> matchingClassNames, 
                String parentPackagePart, HashSet<File> visitedDirSet, Logger logger) {
            
            // Check if we've already visited this directory.
            // This guards against infinite loops in the presence of symlinks linking to an ancestor folder.
            if (!visitedDirSet.add(directoryRoot)) {
                return;
            }
            
            // Create the string representing the current package part.
            String currentPackagePart = (parentPackagePart == null) ? "" : parentPackagePart + directoryRoot.getName() + ".";
            
            // Iterate over the files and directories in the directory root.
            File[] files = directoryRoot.listFiles();
            for (final File ithFile : files) {
                if (ithFile.isDirectory()) {
                    // A directory.
                    
                    // Check whether it is possible for the classes in descendant folders to ever be included and not excluded.
                    if (!prefixMatch(currentPackagePart, includePatternList, excludePatternList)) {
                        return;
                    }
                    
                    // Recursive call.
                    getMatchingClassNamesHelper(ithFile, includePatternList, excludePatternList, matchingClassNames, currentPackagePart, visitedDirSet, logger);
                
                } else {
                    // Probably a file (unless it's a directory but the path name is too long).
                    // We'll probably be ok if we match against this file if it ends with ".class".
                    String ithFileName = ithFile.getName();
                    
                    if (ithFileName.endsWith(CLASS_EXTENSION)) {
                        String qualifiedClassName = currentPackagePart + ithFileName.substring(0, ithFileName.length() - CLASS_EXTENSION.length());

                        if (matchClassName(qualifiedClassName, includePatternList, excludePatternList, logger)) {
                            matchingClassNames.add(qualifiedClassName);
                        }
                    }
                }
            }
        }
        
        /**
         * Determine whether strings starting with a given string can possibly be matched given include and exclude patterns.
         * ie. if anything is appended to the string, it is possible for the composite string to match an 
         *   include pattern and not an exclude pattern.
         *   
         * Examples:
         * If prefixString is "com.businessobjects",
         * - this method can return true if includePatternList contains "com.*" or "com.*.something" or "com.bu?i?essobjects*", 
         *   unless an exclude pattern matches.  If includePatternList only contains "org.*", returns false.
         * - this method returns false if excludePatternList contains "com.*" or "com.bu?i?essobjects*", but not for "com.*.something"
         * 
         * @param prefixString the prefix string
         * @param includePatternList (List of String) the include patterns
         * @param excludePatternList (List of String) the exclude patterns
         * @return if anything is appended to the string, whether it is possible for the string to match an 
         *   include pattern and not an exclude pattern.
         */
        private static boolean prefixMatch(String prefixString, List<String> includePatternList, List<String> excludePatternList) {
            
            int partStringLen = prefixString.length();
            
            boolean canMatchInclude = false;
            
            // Iterate over the include patterns.
            for (final String includePattern : includePatternList) {
                String truncatedPattern;
                
                // The algorithm: truncate the pattern at partString.length or the first *, and match the truncated pattern.
                int starIndex = includePattern.indexOf('*');
                
                if (partStringLen < starIndex) {
                    // truncate at partString.length.  Add a "*" to match the rest.
                    truncatedPattern = includePattern.substring(0, partStringLen) + "*";
                    
                } else if (starIndex > -1) {
                    // truncate at starIndex
                    truncatedPattern = includePattern.substring(0, starIndex + 1);
                    
                } else {
                    // don't truncate
                    truncatedPattern = includePattern;
                }
                
                // Check for a match..
                if (WildcardPatternMatcher.match(prefixString, truncatedPattern)) {
                    canMatchInclude = true;
                    break;
                }
            }
            
            // If none of the include patterns can match, return false.
            if (!canMatchInclude) {
                return false;
            }
            
            
            boolean alwaysMatchExclude = false;
            
            // Iterate over the exclude patterns.
            for (final String excludePattern : excludePatternList) {
                
                // The algorithm: 
                // If pattern does not end with *, ignore.
                // If the pattern length is longer than the partString + 1 (for the trailing *), ignore.
                // Otherwise, match against the exclude pattern
                
                if (!excludePattern.endsWith("*")) {
                    continue;
                }
                
                if (excludePattern.length() > (partStringLen + 1)) {
                    continue;
                }
                
                if (WildcardPatternMatcher.match(prefixString, excludePattern)) {
                    alwaysMatchExclude = true;
                    break;
                }
            }
            
            return !alwaysMatchExclude;
        }
        
        /**
         * Helper method for getMatchingClassNames().
         * Get the class names starting from a jar matching a given pattern.
         * 
         * @param jarFile the jar file to analyze.
         * @param includePatternList (List of String) the include pattern strings.
         * @param excludePatternList (List of String) the exclude pattern strings.
         * @param matchingClassNames (Set of String) the matching class names.  This collection will be populated by this method.
         * @param logger the logger to which to log messages.
         */
        private static void getMatchingClassNamesHelper(JarFile jarFile, List<String> includePatternList, List<String> excludePatternList, Set<String> matchingClassNames, Logger logger) {
            // Iterate over the entries in the jar, looking for matches.
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry nextEntry = entries.nextElement();
                
                String entryName = nextEntry.getName();
                if (!entryName.endsWith(CLASS_EXTENSION)) {
                    continue;
                }
                
                // replace slashes in the entry name with dots.
                String qualifiedClassName = entryName.substring(0, entryName.length() - CLASS_EXTENSION.length()).replaceAll("[\\/]", ".");
                
                // Check for a match.
                if (matchClassName(qualifiedClassName, includePatternList, excludePatternList, logger)) {
                    matchingClassNames.add(qualifiedClassName);
                }
            }
        }

        /**
         * Calculate whether the name of a class is accepted with the given patterns.
         * 
         * @param qualifiedClassName the fully-qualified class name.  eg. "java.lang.String".
         * @param includePatternList (List of String) the include pattern strings.
         * @param excludePatternList (List of String) the exclude pattern strings.
         * @param logger the logger to which to log messages.
         * @return whether the given class name is accepted given the patterns.
         *   ie. if it matches an include pattern, but not an exclude pattern
         */
        private static boolean matchClassName(String qualifiedClassName, List<String> includePatternList, List<String> excludePatternList, Logger logger) {
            
            logger.finest("Matching class: " + qualifiedClassName);
            boolean match = false;
            
            for (final String includePattern : includePatternList) {
                // Check for a match with the include pattern
                if (WildcardPatternMatcher.match(qualifiedClassName, includePattern)) {
                    
                    logger.finest("  Matched include pattern: " + includePattern);
                    match = true;
                    
                    // Check that it doesn't match an exclude pattern
                    for (final String excludePattern : excludePatternList) {
                        if (WildcardPatternMatcher.match(qualifiedClassName, excludePattern)) {
                            logger.finest("  Matched exclude pattern: " + excludePattern);
                            match = false;
                        }                        
                    }
                    
                    if (match) {
                        break;
                    }
                }
            }
            
            logger.finest(match ? "  Matched." : "  Not matched.");
            
            return match;
        }
    }
}

