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
 * CAL.java
 * Creation date: Sep 12, 2006.
 * By: Edward Lam
 */
package org.openquark.cal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.services.ProgramModelManager;
import org.openquark.cal.services.StandardVault;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.VaultRegistry;
import org.openquark.cal.services.WorkspaceDeclaration;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.TextEncodingUtilities;



/**
 * A command-line application launcher for CAL.
 * 
 * For now, this app behaves in the following way:
 * <ul>
 * <li>All modules in the standard vault are compiled to the workspace.
 * <li>The expression defined by the app's args is run in the Prelude module.
 * </ul>
 * 
 * @author Edward Lam
 */
public class CAL extends ConsoleRunner {

    /** The boilerplate string which gets displayed as part of the usage string. */
    private static final String toolBoilerPlate = "CAL Application Launcher      Version 0.0.1     (c) 2006 Business Objects.";
    
    /** The default level at which logged messages will be output to the console. */
    private static Level defaultConsoleLevel = Level.INFO;
    
    /** The namespace for CAL log messages. */
    private static final String calLoggerNamespace = "org.openquark.cal.cal";

    /** The workspace manager for the workspace in which the app will be launched. */
    private final WorkspaceManager workspaceManager;
    
    /** The command line options passed to the application launcher. */
    private final CommandLineOptions options;


    /**
     * A StreamHandler which simply outputs log records to the given output stream.
     * @author Edward Lam
     */
    private static class OutputStreamStreamHandler extends StreamHandler {
        
        /**
         * Constructor for an OutputStreamStreamHandler
         */
        public OutputStreamStreamHandler(OutputStream outputStream) {
            super(outputStream, new ConsoleFormatter());
        }
        
        /** Override this to always flush the stream. */
        @Override
        public void publish(LogRecord record) {
            super.publish(record);
            flush();
        }

        /** Override to just flush the stream, we don't want to close System.out. */
        @Override
        public void close() {
            flush();
        }
    }

    /**
     * A log message formatter that simply outputs the message of the log record, 
     *   plus the text of any throwable.
     * Used to print messages to the console.
     */
    private static class ConsoleFormatter extends Formatter {

        /**
         * {@inheritDoc}
         */
        @Override
        public String format(LogRecord record) {

            StringBuilder sb = new StringBuilder();

            // Append the log message
            sb.append(record.getMessage() + "\n");

            // Append the throwable if there is one
            if (record.getThrown() != null) {
                try {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    pw.close();
                    sb.append(sw.toString());
                } catch (Exception ex) {
                    sb.append("Failed to generate a stack trace for the throwable");
                }
            }
            
            return sb.toString();
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
        
        private static final String[] usageString = {
            toolBoilerPlate,
            "Usage:",
            "       java " + CAL.class.getName() + " [options] cal_expression",
            "           - run a cal expression (must specify -m)",
            "",
            "       java " + CAL.class.getName() + " [options] ModuleName.unqualifiedName",
            "           - run a cal expression in module ModuleName with no arguments",
            "",
            "Options:",
            "   -m moduleName      specify the module in which to run the expression",
            "   -quiet, -q         be extra quiet",
            "   -verbose, -v       be extra verbose",
            "   -h                 this help message"
        };

        private final String calExpression;
        private final ModuleName moduleName;
        private final Level verbosity;

        /**
         * An internal exception which is thrown if there was a problem parsing the command line arguments.
         * @author Edward Lam
         */
        private static class ParseException extends Exception {
            private static final long serialVersionUID = 6129252467685138327L;

            ParseException() {
            }
        }
        
        /**
         * Private constructor for an Options object.
         * To instantiate, call parseOptions() or makeOptions().
         * 
         * @param calExpression
         * @param moduleName
         * @param verbosity the specified verbosity of the logger
         */
        private CommandLineOptions(String calExpression, ModuleName moduleName, Level verbosity) {
            this.calExpression = calExpression;
            this.moduleName = moduleName;
            this.verbosity = verbosity;
        }

        /**
         * Dump the usage string to the logger.
         */
        private static void logUsageString(Logger logger) {
            for (final String element : usageString) {
                logger.info(element);
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
            
            ModuleName moduleName = null;
            Level verbosity = null;
            String calExpression = null;
            
            try {
                while (!argList.isEmpty()) {
                    String nextArg = argList.get(0);
                    
                    // Handle non-dash argument.
                    // The remaining args are the cal expression itself.
                    if (!nextArg.startsWith("-")) {
                        
                        StringBuilder sb = new StringBuilder();
                        boolean firstIteration = true;
                        for (final String arg : argList) {
                            if (!firstIteration) {
                                sb.append(' ');
                            }
                            sb.append(arg);
                            firstIteration = false;
                        }
                        calExpression = sb.toString();
                        
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
                                logger.severe("Error: Multiple verbosity options provided.");
                                throw new ParseException();
                            }
                            verbosity = Level.ALL;
                            
                        } else if (option.equals("-q") || option.equals("-quiet")) {
                            if (verbosity != null) {
                                logger.severe("Error: Multiple verbosity options provided.");
                                throw new ParseException();
                            }
                            verbosity = Level.SEVERE;
                            
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
                            
                            if (option.equals("-m")) {
                                // The name of the workspace.
                                if (moduleName != null) {
                                    logger.severe("Error: Multiple module name options provided.");
                                    throw new ParseException();
                                }
                                moduleName = ModuleName.maybeMake(optionArg);
                                
                                if (moduleName == null) {
                                    logger.severe("Error: Bad module name provided: " + optionArg);
                                    throw new ParseException();
                                }
                                
                            } else {
                                logger.severe("Error: Unknown option: " + option);
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
            
            if (loggedUsageStringForOption && calExpression == null) {
                return null;
            }
            
            return makeOptions(calExpression, moduleName, verbosity, logger);
        }
        
        /**
         * Create a command-line options object given the arguments.
         * 
         * @param calExpression
         * @param moduleName
         * @param verbosity
         * @param logger the logger to which to log any errors.
         * 
         * @return the options object, or null if there were errors in the provided option arguments.
         * If null, errors will have been logged to the logger.
         */
        public static CommandLineOptions makeOptions(String calExpression, ModuleName moduleName, Level verbosity, Logger logger) {
            
            if (calExpression == null) {
                logger.severe("Error: No cal expression provided.");
                return null;
            }
            
            if (moduleName == null) {

                // Attempt to catch invalid expressions early.
                SourceModel.Expr expr = SourceModelUtilities.TextParsing.parseExprIntoSourceModel(calExpression);
                if (expr == null) {
                    logger.severe("Error: \"" + calExpression + "\"" + " is not a valid CAL expression.");
                    return null;
                }
                
                // Pick out the module name if the remaining expression is a dc or var expression.
                if (expr instanceof SourceModel.Expr.DataCons) {
                    SourceModel.Expr.DataCons dataCons = (SourceModel.Expr.DataCons)expr;
                    moduleName = SourceModel.Name.Module.maybeToModuleName(dataCons.getDataConsName().getModuleName());  // may be null
                
                } else if (expr instanceof SourceModel.Expr.Var) {
                    SourceModel.Expr.Var var = (SourceModel.Expr.Var)expr;
                    moduleName = SourceModel.Name.Module.maybeToModuleName(var.getVarName().getModuleName());  // may be null.
                }
                
                // if the module name is still null, we can't continue.
                if (moduleName == null) {
                    logger.severe("Error: No module name provided.");
                    return null;
                }
            }
            
            return new CommandLineOptions(calExpression, moduleName, verbosity);
        }

        public String getCalExpression() {
            return this.calExpression;
        }
        
        public ModuleName getModuleName() {
            return this.moduleName;
        }
        
        /**
         * @return the specified verbosity (Logging) level.  May be null if not specified.
         */
        public Level getVerbosity() {
            return verbosity;
        }
        
        /**
         * Log the current command-line options to a logger.
         * @param logger the logger to which to log the current command-line options.
         */
        public void logConfig(Logger logger) {
            logger.config("Configuration: ");
            logger.config("  CAL expression: " + this.calExpression);
            logger.config("  Module name:    " + (this.moduleName == null ? "(null)" : moduleName.toSourceText()));
            logger.config("  Verbosity:      " + (this.verbosity == null ? "(null)" : verbosity.toString()));
        }
    }

    /**
     * The main entry point.
     * @param args
     */
    public static void main(String[] args) {
        Logger commandLineLogger = Logger.getLogger(CAL.class.getPackage().getName());
        commandLineLogger.setLevel(Level.FINEST);
        commandLineLogger.setUseParentHandlers(false);
        
        StreamHandler optionsOutputStreamHandler = new OutputStreamStreamHandler(System.out);

        optionsOutputStreamHandler.setLevel(Level.INFO);
        commandLineLogger.addHandler(optionsOutputStreamHandler);
        
        CommandLineOptions options = CommandLineOptions.parseOptions(args, commandLineLogger);
        if (options == null) {
            System.exit(1);
        }
        
        CAL cal = new CAL(options);
        boolean success = cal.compileAndRunExpression();
        if (!success) {
            System.exit(1);
        }
    }
    
    /**
     * Constructor for this class.
     * @param options 
     */
    private CAL(CommandLineOptions options) {
        super(Logger.getLogger(calLoggerNamespace));
        this.options = options;
        this.workspaceManager = WorkspaceManager.getWorkspaceManager(null);
        
        calLogger.setLevel(Level.FINEST);
        calLogger.setUseParentHandlers(false);
        
        StreamHandler consoleHandler = new OutputStreamStreamHandler(System.out);

        // Note that we can add other handlers which log more about what's happening.
        Level verbosity = options.getVerbosity();
        if (verbosity != null) {
            consoleHandler.setLevel(verbosity);
        } else {
            consoleHandler.setLevel(defaultConsoleLevel);
        }
        calLogger.addHandler(consoleHandler);
        
        // Log set configuration options to the logger.
        options.logConfig(calLogger);
    }

    /**
     * {@inheritDoc}
     */
    public ProgramModelManager getProgramModelManager() {
        return workspaceManager;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasModuleSource(ModuleName moduleName) {
        return Arrays.asList(workspaceManager.getWorkspace().getModuleNames()).contains(moduleName);
    }

    /**
     * Compile the workspace and run the given expression.
     * @return whether execution terminated normally.
     */
    private boolean compileAndRunExpression() {

        // Init and compile the workspace.
        boolean compileSucceeded = compileWorkspace();

        if (!compileSucceeded) {
            return false;
        }
        return runExpression(options.getModuleName(), options.getCalExpression(), false);
    }

    /**
     * Compile the workspace.
     * @return whether the workspace compiled successfully.
     */
    private boolean compileWorkspace() {
        CompilerMessageLogger ml = new MessageLogger();

        calLogger.fine("Compiling workspace..");
        
        // Init and compile the workspace.
        Status initStatus = new Status("Init status.");
        workspaceManager.initWorkspace(getStreamProvider(), false, initStatus);

        if (initStatus.getSeverity() != Status.Severity.OK) {
            ml.logMessage(initStatus.asCompilerMessage());
        }

        long startCompile = System.currentTimeMillis();

        // If there are no errors go ahead and compile the workspace.
        if (ml.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0) {
            workspaceManager.compile(ml, true, null);
        }

        long compileTime = System.currentTimeMillis() - startCompile;

        // Write out compiler messages
        writeOutCompileResult(ml);

        int nModules = workspaceManager.getModuleNamesInProgram().length;
        calLogger.fine("CAL: Finished compiling " + nModules + " modules in " + compileTime + "ms\n");

        return ml.getNErrors() == 0;
    }

    /**
     * @return a generated WorkspaceDeclaration.StreamProvider which encompasses all modules in the standard vault.
     */
    private static WorkspaceDeclaration.StreamProvider getStreamProvider() {
        ModuleName[] availableModules = StandardVault.getInstance().getAvailableModules(new Status("Get Status"));
        final StringBuilder sb = new StringBuilder();
        for (final ModuleName element : availableModules) {
            sb.append("StandardVault " + element + "\n");
        }
        
        return new WorkspaceDeclaration.StreamProvider() {
            public String getName() {
                return "generatedWorkspace.cws";
            }
            public String getLocation() {
                return "(temporary)";
            }
            public String getDebugInfo(VaultRegistry vaultRegistry) {
                return "(generated)";
            }
            public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
                return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(sb.toString()));
            }
        };
    }

}
