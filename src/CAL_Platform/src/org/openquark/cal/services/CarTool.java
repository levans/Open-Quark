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
 * CarTool.java
 * Creation date: Jan 24, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.Version;
import org.openquark.cal.machine.StatusListener;
import org.openquark.util.FileSystemHelper;
import org.openquark.util.SimpleConsoleHandler;


/**
 * This class provides a command line interface to the CAL Archive (Car) building tool.
 * 
 * This class is not meant to be instantiated or subclassed.
 *
 * @author Joseph Wong
 */
public final class CarTool {
    
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
        private ArgumentList(String[] args) {
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
         * @return an Iterator for the remaining arguments.
         */
        private Iterator<String> getRemainingArgumentsIterator() {
            return argsList.iterator();
        }
        
        /**
         * @return a duplicate of this argument list.
         */
        private ArgumentList duplicate() {
            return new ArgumentList(argsList.toArray(new String[argsList.size()]));
        }
        
        /**
         * Verifies that there are no more arguments in the list, or throws an exception otherwise.
         * Don't use this if {@link #getRemainingArgumentsIterator} is used to fetch the trailing
         * variadic argument list.
         * 
         * @throws TooManyArgumentsException
         */
        private void noMoreArgumentsAllowed() throws TooManyArgumentsException {
            if (!argsList.isEmpty()) {
                throw new TooManyArgumentsException();
            }
        }
    }
    
    /**
     * Exception for representing that the operation of building Cars has failed.
     *
     * @author Joseph Wong
     */
    private static class OperationFailedException extends Exception {
        
        private static final long serialVersionUID = 1248202291878880653L;

        /** Constructs an instance of this exception. */
        private OperationFailedException() {}
    }
    
    /** Private constructor. */
    private CarTool() {}

    /**
     * Runs the Car building tool with the given command line arguments.
     * @param args the command line arguments.
     * @return true if the operation succeeds; false otherwise.
     */
    private boolean runWithCommandLine(String[] args) {
        
        ////
        /// Set up a logger for printing status messages to standard output
        //
        
        Logger logger = Logger.getLogger(getClass().getName());
        logger.setLevel(Level.FINEST);
        logger.setUseParentHandlers(false);
        
        SimpleConsoleHandler handler = new SimpleConsoleHandler();
        handler.setLevel(Level.FINE);
        logger.addHandler(handler);

        logger.info("Car Tool      Version " + Version.CURRENT + "      (c) 2006 Business Objects");

        try {
            return runWithCommandLineAndLogger(new ArgumentList(args), logger);
        } catch (ArgumentList.NotEnoughArgumentsException e) {
            logger.info("Not enough arguments.");
            logger.info(getUsage(getClass()));
            return false;
        } catch (ArgumentList.TooManyArgumentsException e) {
            logger.info("Too many arguments.");
            logger.info(getUsage(getClass()));
            return false;
        }
    }

    /**
     * Runs the Car building tool with the given command line arguments and logger.
     * @param arguments the command line arguments.
     * @param logger the logger to use for logging output.
     * @return true if the operation succeeds; false otherwise.
     */
    private boolean runWithCommandLineAndLogger(ArgumentList arguments, final Logger logger) throws ArgumentList.NotEnoughArgumentsException, ArgumentList.TooManyArgumentsException {
        
        boolean success = true;
        
        if (arguments.getArgument().equals("-unsource")) {
            arguments.consumeArgument();
            
            List<String> carOrCarJarsList = new ArrayList<String>();
            
            if (arguments.getArgument().equals("-incars")) {
                arguments.consumeArgument();
                
                while (!arguments.getArgument().equals("--")) {
                    carOrCarJarsList.add(arguments.getAndConsumeArgument());
                }
                arguments.consumeArgument(); // consume the -- delimiter
            
            } else {
                carOrCarJarsList.add(arguments.getAndConsumeArgument());
            }
        
            if (arguments.getArgument().equals("-d")) {
                arguments.consumeArgument();
                
                String outputFolderString = arguments.getAndConsumeArgument();
                
                arguments.noMoreArgumentsAllowed();

                for (final String carOrCarJarPath : carOrCarJarsList) {
                    try {
                        unsourceCar(carOrCarJarPath, outputFolderString, logger);
                    } catch (OperationFailedException e) {
                        success = false;
                    }
                }
                
            } else {
                success = false;
            }
            
        } else if (arguments.getArgument().equals("-multi")) {
            arguments.consumeArgument();

            List<String> cwsNames = new ArrayList<String>();
            
            while (!arguments.getArgument().equals("--")) {
                cwsNames.add(arguments.getAndConsumeArgument());
            }
            arguments.consumeArgument(); // consume the -- delimiter

            ArgumentList origArgs = arguments;
            
            Set<String> carsAlreadyBuilt = new HashSet<String>();
            
            for (int i = 0, n = cwsNames.size(); i < n; i++) {
                final String workspaceDeclarationName = cwsNames.get(i);
                
                final BasicCALServices calServices = BasicCALServices.make(workspaceDeclarationName);
                
                arguments = origArgs.duplicate();
                
                try {
                    String[] carsBuilt = helpRun(arguments, logger, workspaceDeclarationName, calServices, carsAlreadyBuilt, true);

                    carsAlreadyBuilt.addAll(Arrays.asList(carsBuilt));
                    
                } catch (OperationFailedException e) {
                    success = false;
                }
            }
            
        } else {
            final String workspaceDeclarationName = arguments.getAndConsumeArgument();
            
            final BasicCALServices calServices = BasicCALServices.make(workspaceDeclarationName);

            try {
                helpRun(arguments, logger, workspaceDeclarationName, calServices, Collections.<String>emptySet(), false);
            } catch (OperationFailedException e) {
                success = false;
            }
        }
        
        return success;
    }

    /**
     * Helper method for generating a car (or car jar) without source, given the car with source.
     * @param carOrCarJarPath the path to the car or car jar.
     * @param outputFolderString the path to the output folder
     */
    private void unsourceCar(String carOrCarJarPath, String outputFolderString, Logger logger) throws OperationFailedException {
        File outputFolder = new File(outputFolderString);
        
        if (!outputFolder.isDirectory()) {
            if (!FileSystemHelper.ensureDirectoryExists(outputFolder)) {
                logger.info("Could not create folder: " + outputFolder);
                throw new OperationFailedException();
            }
        }
        
        try {
            CarUnsourcer.unsourceCar(carOrCarJarPath, outputFolderString, logger);
        
        } catch (IOException e) {
            logger.info("Error occurred generating output file: " + e.getMessage());
            throw new OperationFailedException();
        }
        
    }

    /**
     * Helper method for running the Car building tool.
     * 
     * @param arguments the remaining command line arguments.
     * @param logger the logger to use for logging output.
     * @param workspaceDeclarationName the name of the workspace declaration to process.
     * @param calServices the associated copy of BasicCALServices initialized with the named workspace.
     * @param additionalCarsToExclude names of Car files to exclude from being generated.
     * @param isMulti whether the -multi option specified.
     * @return an array of the names of the Car files built.
     * @throws ArgumentList.TooManyArgumentsException
     */
    private String[] helpRun(ArgumentList arguments, final Logger logger, final String workspaceDeclarationName, final BasicCALServices calServices, Set<String> additionalCarsToExclude, boolean isMulti) throws ArgumentList.NotEnoughArgumentsException, ArgumentList.TooManyArgumentsException, OperationFailedException {
        ////
        /// Create and compile a workspace through a BasicCALServices
        //
        
        WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
        
        MessageLogger msgLogger = new MessageLogger();
        
        StatusListener.StatusListenerAdapter statusListener = new StatusListener.StatusListenerAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void setModuleStatus(StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
                if (moduleStatus == StatusListener.SM_GENCODE) {
                    logger.fine("Compiling " + moduleName);
                }
            }
        };
        
        logger.info("Compiling CAL workspace...");
        calServices.compileWorkspace(statusListener, msgLogger);
        
        if (msgLogger.getNErrors() == 0) {
            logger.info("Compilation successful");
            
            ////
            /// We only run the CarBuilder if everything compiled properly without errors
            //
            
            CarBuilder.Monitor monitor = new CarBuilder.Monitor() {
                public boolean isCanceled() {
                    return false;
                }
                public void operationStarted(int nCars, int nTotalModules) {}
                public void showMessages(String[] messages) {
                    for (final String element : messages) {
                        logger.info(element);
                    }
                }
                public void carBuildingStarted(String carName, int nModules) {
                    logger.info("Creating the Car file " + carName);
                }
                public void processingModule(String carName, ModuleName moduleName) {
                    logger.info("Adding module " + moduleName + " to the Car...");
                }
                public void carBuildingDone(String carName) {
                    logger.info("Done creating the Car file " + carName);
                }
                public void operationDone() {
                    logger.info("All done");
                }
            };
            
            ///
            // Command line syntax:
            //
            // [-notVerbose] [-keepsource] [-nocws | -nosuffix] [-s] [-excludeCarsInDirs ... --] [-excludeCarJarsInDirs ... --] [-jar] outputDirectory [carSpecName1 carSpecName2...]
            // OR
            // [-notVerbose] [-keepsource] [-nocws | -nosuffix] [-s] [-excludeCarsInDirs ... --] [-excludeCarJarsInDirs ... --] [-jar] -d outputDirectory
            
            boolean verboseMode = true; // default: verbose
            if (arguments.getArgument().equals("-notVerbose")) {
                verboseMode = false;
                arguments.consumeArgument();
            }
            
            boolean shouldBuildSourcelessModules = true; // default: generate sourceless modules
            if (arguments.getArgument().equals("-keepsource")) {
                shouldBuildSourcelessModules = false;
                arguments.consumeArgument();
            }
            
            boolean shouldGenerateCorrespWorspaceDecl = true; // default: generated the cws files
            boolean noCarSuffixInWorkspaceDeclName = false;   // default: generated cws files end with .car.cws
            
            if (arguments.getArgument().equals("-nocws")) {
                shouldGenerateCorrespWorspaceDecl = false;
                arguments.consumeArgument();
            } else if (arguments.getArgument().equals("-nosuffix")) {
                noCarSuffixInWorkspaceDeclName = true;
                arguments.consumeArgument();
            }
            
            boolean shouldSkipModulesAlreadyInCars = false; // default: do not skip modules already in Cars
            if (arguments.getArgument().equals("-s")) {
                shouldSkipModulesAlreadyInCars = true;
                arguments.consumeArgument();
            }
            
            Set<String> carsToExclude = new HashSet<String>(additionalCarsToExclude);
            if (arguments.getArgument().equals("-excludeCarsInDirs")) {
                arguments.consumeArgument();
                
                while (!arguments.getArgument().equals("--")) {
                    File directory = new File(arguments.getAndConsumeArgument(), "Car");
                    
                    File[] files = directory.listFiles();
                    if (files == null) {
                        logger.warning("Folder does not exist: " + directory);
                    } else {
                        for (final File element : files) {
                            carsToExclude.add(element.getName());
                        }
                    }
                }
                arguments.consumeArgument(); // consume the -- delimiter
            }
            
            if (arguments.getArgument().equals("-excludeCarJarsInDirs")) {
                arguments.consumeArgument();
                
                while (!arguments.getArgument().equals("--")) {
                    File directory = new File(arguments.getAndConsumeArgument());
                    
                    File[] files = directory.listFiles();
                    if (files == null) {
                        logger.warning("Folder does not exist: " + directory);
                    } else {
                        for (final File element : files) {
                            String fileName = element.getName();
                            if (fileName.endsWith(CarBuilder.DOT_CAR_DOT_JAR)) {
                                String carName = fileName.substring(0, fileName.length() - 4); // chop ".jar" off the end
                                carsToExclude.add(carName);
                            }
                        }
                    }
                }
                arguments.consumeArgument(); // consume the -- delimiter
            }
            
            boolean shouldGenerateCarJarSuffix = false; // default: generate Cars and not Car-jars.
            if (arguments.getArgument().equals("-jar")) {
                shouldGenerateCarJarSuffix = true;
                arguments.consumeArgument();
            }
            
            if (verboseMode) {
                System.out.println();
                System.out.println(workspaceManager.getDebugInfo());
                System.out.println();
            }
            
            CarBuilder.BuilderOptions options = new CarBuilder.BuilderOptions(
                shouldSkipModulesAlreadyInCars,
                shouldGenerateCorrespWorspaceDecl,
                noCarSuffixInWorkspaceDeclName,
                shouldBuildSourcelessModules,
                carsToExclude,
                shouldGenerateCarJarSuffix);
                
            if (arguments.getArgument().equals("-d")) {
                // the user wants one Car per workspace declaration file
                
                arguments.consumeArgument();
                
                final String outputDir = arguments.getAndConsumeArgument();
                
                arguments.noMoreArgumentsAllowed();
                
                final File outputDirectory = new File(outputDir);
                
                try {
                    String[] carsBuilt = CarBuilder.buildOneCarPerWorkspaceDeclaration(workspaceManager, monitor, outputDirectory, options);
                    return carsBuilt;
                    
                } catch (IOException e) {
                    logger.info("Error occurred generating the Car files: " + e.getMessage());
                    throw new OperationFailedException();
                }
                
                
            } else {
                // the user wants just one Car for all modules in the workspace
                if (!carsToExclude.equals(additionalCarsToExclude)) {
                    logger.info("The option -excludeCarsInDirs does not apply for building just one Car. Ignoring...");
                }
                
                final String carName = CarBuilder.makeCarNameFromSourceWorkspaceDeclName(workspaceDeclarationName);
                    
                final String outputDir = arguments.getAndConsumeArgument();
                
                final File outputDirectory = new File(outputDir);
                
                CarBuilder.Configuration config =
                    CarBuilder.Configuration.makeConfigOptionallySkippingModulesAlreadyInCars(workspaceManager, options);
                
                for (Iterator<String> it = arguments.getRemainingArgumentsIterator(); it.hasNext(); ) {
                    String carSpecName = it.next();
                    File specFile = new File(carSpecName);
                    config.addFileBasedCarSpec(specFile.getName(), specFile);
                }
                
                config.setMonitor(monitor);
                
                try {
                    boolean notCanceled = CarBuilder.buildCar(config, outputDirectory, carName, options);
                    if (notCanceled) {
                        return new String[] {carName};
                    }
                    
                } catch (IOException e) {
                    logger.info("Error occurred generating the Car file: " + e.getMessage());
                    throw new OperationFailedException();
                }
            }
            
        } else {
            logger.severe("Compilation failed:");
            List<CompilerMessage> messages = msgLogger.getCompilerMessages();
            for (int i = 0, n = messages.size(); i < n; i++) {
                logger.info("  " + messages.get(i).toString());
            }
            
            throw new OperationFailedException();
        }
        
        return new String[0];
    }
    
    /** The main method */
    public static void main(String[] args) {
        boolean success = new CarTool().runWithCommandLine(args);
        if (!success) {
            System.err.println("Car Tool - Car building failed.");
            System.exit(1);
        }
    }
    
    /**
     * @return the usage string.
     */
    private static String getUsage(Class<? extends CarTool> mainClass) {
        String usage =
            "Usage 1 - to generate a single Car file per source workspace declaration specified:\n" +
            "java " + mainClass.getName() + " [workspaceDeclaration | -multi workspaceDeclaration ... --] [-notVerbose] [-keepsource] [-nocws | -nosuffix] [-s] [-excludeCarsInDirs dirName ... --] [-excludeCarJarsInDirs dirName ... --] [-jar] outputDirectory [specFileName ...]\n" +
            "\n" +
            "Where:\n" +
            "  workspaceDeclaration - the name of the workspace declaration file, e.g. everything.default.cws\n" +
            "  -multi               - if specified, then the generated Cars will be based on the set of workspace declaration files between -multi and --.\n" +
            "  -notVerbose          - if specified, then additional diagnostic information will not be displayed.\n" +
            "  -keepsource          - if specified, then the generated Car will contain the full source of the modules (versus having sourceless modules with empty CAL file stubs).\n" +
            "  -nocws               - if specified, then a new workspace declaration file referencing the output Car will not be generated.\n" +
            "  -nosuffix            - if specified, then the workspace declaration file generated will end simply with .cws rather than .car.cws.\n" +
            "  -s                   - if specified, then the modules that come from Cars will be skipped over in the output Car.\n" +
            "  -excludeCarsInDirs   - if specified, then the Cars found in the 'Car' subdirectories of the listed directories will not be generated.\n" +
            "  -excludeCarJarsInDirs- if specified, then the Car-jars found in the listed directories will not be generated.\n" +
            "  -jar                 - if specified, then the output will be in the form of Car-jars.\n" +
            "  outputDirectory      - the name of the output directory to which the single Car file will be generated (under a 'Car' subfolder).\n" +
            "                         and also the corresponding workspace declaration if requested (under a 'Workspace Declarations' subfolder).\n" +
            "  specFileName         - the name of an additional Car workspace spec file\n" +
            "\n" +
            "Usage 2 - to generate multiple Car files - one Car file per workspace declaration that is imported:\n" +
            "java " + mainClass.getName() + " [workspaceDeclaration | -multi workspaceDeclaration ... --] [-notVerbose] [-keepsource] [-nocws | -nosuffix] [-s] [-excludeCarsInDirs dirName ... --] [-excludeCarJarsInDirs dirName ... --] [-jar] -d outputDirectory\n" +
            "\n" +
            "Where:\n" +
            "  workspaceDeclaration - the name of the workspace declaration file, e.g. everything.default.cws\n" +
            "  -multi               - if specified, then the generated Cars will be based on the set of workspace declaration files between -multi and --.\n" +
            "  -notVerbose          - if specified, then additional diagnostic information will not be displayed.\n" +
            "  -keepsource          - if specified, then the generated Cars will contain the full source of the modules (versus having sourceless modules with empty CAL file stubs).\n" +
            "  -nocws               - if specified, then a new workspace declaration file will not be generated for each output Car.\n" +
            "  -nosuffix            - if specified, then the workspace declaration files generated will end simply with .cws rather than .car.cws.\n" +
            "  -s                   - if specified, then the modules that come from Cars will be skipped over in the output Cars.\n" +
            "  -excludeCarsInDirs   - if specified, then the Cars found in the 'Car' subdirectories of the listed directories will not be generated.\n" +
            "  -excludeCarJarsInDirs- if specified, then the Car-jars found in the listed directories will not be generated.\n" +
            "  -jar                 - if specified, then the output will be in the form of Car-jars.\n" +
            "  outputDirectory      - the name of the output directory to which one Car file per workspace declaration file will be generated (under a 'Car' subfolder)\n" +
            "                         and also the corresponding workspace declarations if requested (under a 'Workspace Declarations' subfolder).\n" +
            "\n" +
            "Usage 3 - to remove source from an existing car or carjar file. \n" +
            "java " + mainClass.getName() + " -unsource [carOrCarJar | -incars carOrCarJar ... --] -d outputDirectory\n" +
            "\n" +
            "Where:\n" +
            "  -incars              - if specified, then the generated Cars (or car jars) will be based on the set of input files between -incars and --.\n" +
            "  carOrCarJar          - the path to the car or carjar file, e.g. \"c:\\\\everything.default.car\"\n" +
            "  outputDirectory      - the name of the output directory to the output Car files will be generated\n" +
            "\n";
        return usage;
    }
}
