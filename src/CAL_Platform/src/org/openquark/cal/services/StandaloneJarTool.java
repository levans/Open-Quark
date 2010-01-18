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
 * StandaloneJarTool.java
 * Created: May 17, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.Version;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.JavaScope;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.LibraryClassSpec;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.MainClassSpec;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.util.SimpleConsoleHandler;

/**
 * This class implements a command-line utility for constructing a <em>standalone JAR</em>, which may contain
 * application and library classes.
 * <p>
 * A standalone JAR may package up a CAL application by gathering together all the generated runtime
 * classes necessary for running a specific CAL function, and a class containing a
 * <code>public static void main(String[] args)</code> method which runs the CAL function directly
 * (without having to first initialize a CAL workspace). This makes it possible to package up a CAL
 * function into a command-line application.
 * <p>
 * To run the CAL function using the standalone JAR, simply include the standalone JAR, the CAL
 * platform jars, and any required external jars on the classpath, and specify the name of the
 * generated class as the one to run. Command line arguments will be passed into the CAL function.
 * <p>
 * A standalone JAR may also package up one or more library classes - a library class is a non-instatiatable class
 * containing static methods corresponding to the functions and data constructors defined in a particular
 * CAL module. This makes it possible to expose CAL libraries in Java, by defining API modules in CAL (whose
 * functions contain appropriate marshalling from/unmarshalling to foreign types), from which library classes are
 * generated.
 * <p>
 * This utility only works with the LECC machine.
 * <p>
 * In this version, we support generating standalone applications for CAL functions with the type
 * <code>[String] -> ()</code>. In this case, the command line arguments array will be marshalled
 * into a CAL list of Strings.
 *
 * @author Joseph Wong
 */
public final class StandaloneJarTool {

    /** The launch command displayed in the usage help. */
    private static final String LAUNCH_COMMAND = "quarkc";
    
    /** The name of the non-interruptible property. */
    private static final String NON_INTERRUPTIBLE_PROPERTY = "org.openquark.cal.machine.lecc.non_interruptible";

    /**
     * Encapsulates the command line argument list.
     *
     * @author Joseph Wong
     */
    private static class ArgumentList {
        
        /**
         * Exception for representing that there are not enough arguments supplied.
         *
         * @author Joseph Wong
         */
        private static class NotEnoughArgumentsException extends Exception {
            
            private static final long serialVersionUID = -5402072553834237273L;

            /** Constructs an instance of this exception. */
            private NotEnoughArgumentsException() {}
        }
        
        /**
         * Exception for representing that there are too many arguments supplied.
         *
         * @author Joseph Wong
         */
        private static class TooManyArgumentsException extends Exception {
            
            private static final long serialVersionUID = 1154558440221248070L;

            /** Constructs an instance of this exception. */
            private TooManyArgumentsException() {}
        }
        
        /** The arguments in a List. */
        private final List<String> argsList;
        
        /**
         * Constructs an ArgumentList.
         * @param args the arguments to be encapsulated.
         */
        private ArgumentList(final String[] args) {
            argsList = new ArrayList<String>();
            
            // we drop all empty string command-line arguments
            // (these can be supplied on unix platforms via quoted empty strings e.g. '')
            for (final String element : args) {
                if (element.length() > 0) {
                    argsList.add(element);
                }
            }
        }
        
        /**
         * @return the current argument being examined.
         * @throws NotEnoughArgumentsException
         */
        private String getArgument() throws NotEnoughArgumentsException {
            if (argsList.isEmpty()) {
                throw new NotEnoughArgumentsException();
            } else {
                return argsList.get(0);
            }
        }
        
        /**
         * Removes the current argument from the list.
         * @throws NotEnoughArgumentsException
         */
        private void consumeArgument() throws NotEnoughArgumentsException {
            if (argsList.isEmpty()) {
                throw new NotEnoughArgumentsException();
            } else {
                argsList.remove(0);
            }
        }
        
        /**
         * Returns the current argument and removes it from the list.
         * @return the current argument, which is removed.
         * @throws NotEnoughArgumentsException
         */
        private String getAndConsumeArgument() throws NotEnoughArgumentsException {
            if (argsList.isEmpty()) {
                throw new NotEnoughArgumentsException();
            } else {
                return argsList.remove(0);
            }
        }
        
        /**
         * Verifies that there are no more arguments in the list, or throws an exception otherwise.
         * 
         * @throws TooManyArgumentsException
         */
        private void noMoreArgumentsAllowed() throws TooManyArgumentsException {
            if (!argsList.isEmpty()) {
                throw new TooManyArgumentsException();
            }
        }
        
        /**
         * @return whether there are more arguments left.
         */
        private boolean hasMoreArguments() {
            return !argsList.isEmpty();
        }
    }
    
    /** Private constructor. This class is not meant to be instantiated. */
    private StandaloneJarTool() {}
    
    /**
     * The main method for the tool.
     * @param args
     */
    public static void main(final String[] args) {

        ////
        /// Set up a logger for printing status messages to standard output
        //
        
        final Logger logger = Logger.getLogger(StandaloneJarTool.class.getName());
        logger.setLevel(Level.FINEST);
        logger.setUseParentHandlers(false);
        
        final SimpleConsoleHandler handler = new SimpleConsoleHandler();
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);

        logger.info(StandaloneJarToolMessages.getString("versionBanner", Version.CURRENT));

        ////
        /// Parsing the command line arguments
        //
        
        try {
            final ArgumentList arguments = new ArgumentList(args);
            
            final String cwsName = arguments.getAndConsumeArgument();
            
            final boolean verbose;
            if (arguments.getArgument().equals("-verbose")) {
                arguments.consumeArgument();
                verbose = true;
            } else {
                verbose = false;
            }
            
            final List<MainClassSpec> mainClassSpecs = new ArrayList<MainClassSpec>();
            final List<LibraryClassSpec> libClassSpecs = new ArrayList<LibraryClassSpec>();
            
            // unsupported option for adding all modules as libraries via -XX:all
            boolean addAllModulesAsLibraries = false;
            
            while (true) {
                final String command = arguments.getArgument();
                if (command.equals("-main")) {
                    arguments.consumeArgument();
                    
                    final String entryPointNameString = arguments.getAndConsumeArgument();
                    final String mainClassName = arguments.getAndConsumeArgument();

                    final QualifiedName entryPointName;
                    try {
                        entryPointName = QualifiedName.makeFromCompoundName(entryPointNameString);

                    } catch (final IllegalArgumentException e) {
                        logger.info(StandaloneJarToolMessages.getString("invalidEntryPointName", entryPointNameString));
                        return;
                    }
                    
                    try {
                        mainClassSpecs.add(MainClassSpec.make(mainClassName, entryPointName));
                        
                    } catch (final StandaloneJarBuilder.InvalidConfigurationException e) {
                        logger.log(Level.INFO, StandaloneJarToolMessages.getString("errorPrefix", e.getMessage()));
                        return;
                    }
                    
                } else if (command.equals("-lib")) {
                    arguments.consumeArgument();
                    
                    final String moduleNameString = arguments.getAndConsumeArgument();
                    final String libClassScope = arguments.getAndConsumeArgument();
                    final String libClassName = arguments.getAndConsumeArgument();
                    
                    final JavaScope scope;
                    try {
                        scope = JavaScope.valueOf(libClassScope.toUpperCase());
                        if (scope == JavaScope.PROTECTED || scope == JavaScope.PRIVATE) {
                            //package and private scopes are not allowed for top-level Java classes.
                            logger.info(StandaloneJarToolMessages.getString("invalidScope", libClassScope));
                            return;
                        }                    
                    } catch (final IllegalArgumentException e) {
                        logger.info(StandaloneJarToolMessages.getString("invalidScope", libClassScope));
                        return;
                    }
                    
                    final ModuleName moduleName = ModuleName.maybeMake(moduleNameString);
                    if (moduleName == null) {
                        logger.info(StandaloneJarToolMessages.getString("invalidModuleName", moduleNameString));
                        return;
                    }
                    
                    try {
                        libClassSpecs.add(LibraryClassSpec.make(scope, libClassName, moduleName));
                        
                    } catch (final StandaloneJarBuilder.InvalidConfigurationException e) {
                        logger.log(Level.INFO, StandaloneJarToolMessages.getString("errorPrefix", e.getMessage()));
                        return;
                    }
                    
                } else if (command.equals("-XX:all")) { // unsupported internal use argument - adds all modules in the program
                    arguments.consumeArgument();

                    addAllModulesAsLibraries = true;
                    
                } else {
                    // not a command... so break out of the loop to process the remainder
                    break;
                }
            }
            
            if (mainClassSpecs.isEmpty() && libClassSpecs.isEmpty() && !addAllModulesAsLibraries) {
                // there must be at least one -main or -lib command specified (or -XX:all)
                throw new ArgumentList.NotEnoughArgumentsException();
            }
            
            final File outputFile = new File(arguments.getAndConsumeArgument());
            
            final File outputSrcZipFile;
            if (arguments.hasMoreArguments()) {
                
                final String command = arguments.getAndConsumeArgument();
                if (command.equals("-src")) {
                    outputSrcZipFile = new File(arguments.getAndConsumeArgument());
                    
                } else {
                    showUsage(logger);
                    return;
                }
            } else {
                outputSrcZipFile = null;
            }
            
            arguments.noMoreArgumentsAllowed();

            if (!LECCMachineConfiguration.nonInterruptibleRuntime()) {
                logger.info(StandaloneJarToolMessages.getString("nonInterruptiblePropertyNotSet", NON_INTERRUPTIBLE_PROPERTY));
            }

            ////
            /// Compile the CAL workspace
            //

            logger.info(StandaloneJarToolMessages.getString("initializingCalWorkspace"));

            final MessageLogger msgLogger = new MessageLogger();

            final BasicCALServices calServices = BasicCALServices.makeCompiled(cwsName, msgLogger);

            if (calServices == null) {
                logger.info(msgLogger.toString());
                return;
            }

            logger.info(StandaloneJarToolMessages.getString("calWorkspaceInitialized"));

            final WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
            
            // process the -XX:all argument
            if (addAllModulesAsLibraries) {
                for (final ModuleName moduleName : workspaceManager.getModuleNamesInProgram()) {
                    try {
                        libClassSpecs.add(LibraryClassSpec.make(JavaScope.PUBLIC, "cal.lib." + moduleName.toSourceText(), moduleName));

                    } catch (final StandaloneJarBuilder.InvalidConfigurationException e) {
                        logger.log(Level.INFO, StandaloneJarToolMessages.getString("errorPrefix", e.getMessage()));
                        return;
                    }
                }
            }

            ////
            /// Set up the progress monitor for displaying status messages to the user.
            //

            final StandaloneJarBuilder.Monitor monitor = new StandaloneJarBuilder.Monitor() {
                public void jarBuildingStarted(final String jarName) {
                    logger.info(StandaloneJarToolMessages.getString("buildingStandaloneJar", jarName));
                }
                public void addingFile(final String fileName) {
                    if (verbose) {
                        logger.info(StandaloneJarToolMessages.getString("addingFile", fileName));
                    }
                }
                public void skippingFunctionWithTypeClassConstraints(final QualifiedName name) {
                    // always display regardless of the verbose flag
                    logger.info(StandaloneJarToolMessages.getString("skippingFunctionWithTypeClassConstraints", name.toSourceText()));
                }
                public void skippingClassMethod(final QualifiedName name) {
                    // always display regardless of the verbose flag
                    logger.info(StandaloneJarToolMessages.getString("skippingClassMethod", name.toSourceText()));
                }
                public void addingSourceFile(final String fileName) {
                    if (verbose) {
                        logger.info(StandaloneJarToolMessages.getString("addingSourceFile", fileName));
                    }
                }
                public void jarBuildingDone(final String jarName, boolean success) {
                    if (success) {
                        logger.info(StandaloneJarToolMessages.getString("doneBuildingStandaloneJar", jarName));
                    } else {
                        logger.info(StandaloneJarToolMessages.getString("failedBuildingStandaloneJar", jarName));
                    }
                }
            };

            ////
            /// Check the arguments and build the JAR
            //

            try {
                StandaloneJarBuilder.buildStandaloneJar(outputFile, outputSrcZipFile, mainClassSpecs, libClassSpecs, workspaceManager, monitor);

            } catch (final IOException e) {
                logger.log(Level.INFO, StandaloneJarToolMessages.getString("ioError"), e);

            } catch (final StandaloneJarBuilder.InvalidConfigurationException e) {
                logger.log(Level.INFO, StandaloneJarToolMessages.getString("errorPrefix", e.getMessage()));

            } catch (final UnableToResolveForeignEntityException e) {
                logger.log(Level.INFO, StandaloneJarToolMessages.getString("foreignResolutionError"), e);
                
            } catch (final StandaloneJarBuilder.InternalProblemException e) {
                logger.log(Level.SEVERE, StandaloneJarToolMessages.getString("internalError"), e);
            }
            
        } catch (final ArgumentList.NotEnoughArgumentsException e) {
            logger.log(Level.INFO, StandaloneJarToolMessages.getString("notEnoughArguments"));
            showUsage(logger);
            
        } catch (final ArgumentList.TooManyArgumentsException e) {
            logger.log(Level.INFO, StandaloneJarToolMessages.getString("tooManyArguments"));
            showUsage(logger);
        }
    }

    /**
     * Shows the usage help.
     * @param logger the Logger to use for showing the usage help.
     */
    private static void showUsage(final Logger logger) {
        logger.info(StandaloneJarToolMessages.getString("usage", LAUNCH_COMMAND));
    }
}
