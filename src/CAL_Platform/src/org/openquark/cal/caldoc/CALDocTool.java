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
 * CALDocTool.java
 * Creation date: Oct 7, 2005.
 * By: Joseph Wong
 */
package org.openquark.cal.caldoc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.filter.CompositeModuleFilter;
import org.openquark.cal.filter.CompositeScopedEntityFilter;
import org.openquark.cal.filter.ExcludeTestModulesFilter;
import org.openquark.cal.filter.ModuleFilter;
import org.openquark.cal.filter.PublicEntitiesOnlyFilter;
import org.openquark.cal.filter.QualifiedNameBasedScopedEntityFilter;
import org.openquark.cal.filter.RegExpBasedModuleFilter;
import org.openquark.cal.filter.RegExpBasedQualifiedNameFilter;
import org.openquark.cal.filter.ScopedEntityFilter;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.LocaleUtilities;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.util.SimpleConsoleHandler;


/**
 * This class represents the public interface to the HTML documentation generator. It provides facilities
 * for parsing command line arguments into a configuration object for the generator.
 * 
 * This class is not meant to be instantiated or subclassed.
 *
 * @author Joseph Wong
 */
public final class CALDocTool {
    /** The name, version and copyright string to be displayed on each invocation. */
    private static final String TOOL_NAME_VERSION_AND_COPYRIGHT = CALDocMessages.getString("CALDocToolNameVersionAndCopyright");
    
    /**
     * This class encapsulates a running instance of the CALDocTool command line interface.
     *
     * @author Joseph Wong
     */
    public static final class Launcher {
        
        /** Run the CALDoc tool with the specified command line arguments and the specified workspace. */
        public void runWithCommandLine(String[] args, String launchCommand, String workspaceName) {
            
            ////
            /// Set up a logger for printing status messages to standard output
            //
            
            final Logger logger = Logger.getLogger(getClass().getName());
            logger.setLevel(Level.FINEST);
            logger.setUseParentHandlers(false);
            
            SimpleConsoleHandler handler = new SimpleConsoleHandler();
            handler.setLevel(Level.FINE);
            logger.addHandler(handler);
           
            ////
            /// Create and compile a workspace through a BasicCALServices
            //
            
            BasicCALServices calServices = BasicCALServices.make(workspaceName);
            WorkspaceManager workspaceManager = calServices.getWorkspaceManager();
            
            try {
                HTMLDocumentationGeneratorConfiguration config = CALDocTool.makeConfiguration(workspaceManager, args, logger);
                
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
                
                logger.info("Compiling CAL workspace..");
                calServices.compileWorkspace(statusListener, msgLogger);
                
                if (msgLogger.getNErrors() == 0) {
                    logger.info("Compilation successful");
                    
                    ////
                    /// We only generate the documentation if everything compiled properly without errors
                    //
                    CALDocTool.run(workspaceManager, config);
                    
                } else {
                    logger.severe("Compilation failed:");
                    List<CompilerMessage> messages = msgLogger.getCompilerMessages();
                    for (int i = 0, n = messages.size(); i < n; i++) {
                        logger.info("  " + messages.get(i).toString());
                    }
                }
                
            } catch (CommandLineException.HelpRequested e) {
                logger.info(CALDocTool.getUsage(launchCommand));
            } catch (CommandLineException.MissingOptionArgument e) {
                CALDocTool.logMissingOptionArgumentMessage(logger, e);
                logger.info(CALDocTool.getUsage(launchCommand));
            } catch (CommandLineException e) {
                CALDocTool.logInvalidOptionMessage(logger, e);
                logger.info(CALDocTool.getUsage(launchCommand));
            }
        }
    }
    
    /**
     * This exception class represents a problem parsing the command line arguments.
     *
     * @author Joseph Wong
     */
    private static class CommandLineException extends Exception {
        
        private static final long serialVersionUID = -2211496055848942039L;

        /**
         * Package-scoped constructor.
         * This exception is only meant to be created and thrown by the documentation generator.
         */
        CommandLineException() {
            super();
        }
    
        /**
         * Package-scoped constructor.
         * This exception is only meant to be created and thrown by the documentation generator.
         * @param string the message string.
         */
        CommandLineException(String string) {
            super(string);
        }
        
        /**
         * This exception class represents the user having specified the "-help" option to request
         * the tool to just display the usage page and exit.
         *
         * @author Joseph Wong
         */
        private static final class HelpRequested extends CommandLineException {

            private static final long serialVersionUID = -4292078944760976595L;
        }
        
        /**
         * This exception class represents the user forgetting to specify enough additional arguments
         * to an option that requires one or more additional arguments.
         *
         * @author Joseph Wong
         */
        private static final class MissingOptionArgument extends CommandLineException {
            
            private static final long serialVersionUID = -7574178785736364836L;

            /**
             * Package-scoped constructor.
             * This exception is only meant to be created and thrown by the documentation generator.
             * @param string the message string.
             */
            MissingOptionArgument(String string) {
                super(string);
            }
        }
    }

    /** Private constructor. */
    private CALDocTool() {}
    
    /**
     * The command line interface to the CALDocTool.
     * @param args the command line arguments.
     */
    public static void main(String args[]) {
        String launchCommand = "java " + CALDocTool.class.getName() + " <workspace name>";
        
        if (args.length == 0) {
            System.out.println(CALDocTool.getUsage(launchCommand));
        } else {
            String workspaceName = args[0];
            String[] remainder = new String[args.length - 1];
            System.arraycopy(args, 1, remainder, 0, remainder.length);
            
            new Launcher().runWithCommandLine(remainder, launchCommand, workspaceName);
        }
    }
    
    /**
     * Runs the documentation generator with the given workspace manager and configuration.
     * @param workspaceManager the workspace manager to be used during documentation generation.
     * @param config the configuration object.
     */
    public static void run(WorkspaceManager workspaceManager, HTMLDocumentationGeneratorConfiguration config) {
        showToolName(config.logger);
        HTMLDocumentationGenerator docGenerator = new HTMLDocumentationGenerator(workspaceManager, config);
        docGenerator.generateDoc();
    }
    
    /**
     * Runs the documentation generator with the given workspace manager and configuration as specified by command line arguments.
     * If parsing of the command line arguments fails, then the command line usage info is logged to the logger.
     * 
     * @param workspaceManager the workspace manager to be used during documentation generation.
     * @param cmdLine the command line arguments.
     * @param logger the logger to use for logging status messages.
     * @param launchCommand the command to launch the tool.
     */
    public static void runWithCommandLine(WorkspaceManager workspaceManager, String cmdLine, Logger logger, String launchCommand) {
        try {
            run(workspaceManager, makeConfiguration(workspaceManager, cmdLine, logger));
        } catch (CommandLineException.HelpRequested e) {
            logger.info(getUsage(launchCommand));
        } catch (CommandLineException.MissingOptionArgument e) {
            logMissingOptionArgumentMessage(logger, e);
            logger.info(getUsage(launchCommand));
        } catch (CommandLineException e) {
            logInvalidOptionMessage(logger, e);
            logger.info(getUsage(launchCommand));
        }
    }
    
    /**
     * Runs the documentation generator with the given workspace manager and configuration as specified by command line arguments.
     * If parsing of the command line arguments fails, then the command line usage info is logged to the logger.
     * 
     * @param workspaceManager the workspace manager to be used during documentation generation.
     * @param args the command line arguments.
     * @param logger the logger to use for logging status messages.
     * @param launchCommand the command to launch the tool.
     */
    public static void runWithCommandLine(WorkspaceManager workspaceManager, String[] args, Logger logger, String launchCommand) {
        try {
            run(workspaceManager, makeConfiguration(workspaceManager, args, logger));
        } catch (CommandLineException.HelpRequested e) {
            logger.info(getUsage(launchCommand));
        } catch (CommandLineException.MissingOptionArgument e) {
            logMissingOptionArgumentMessage(logger, e);
            logger.info(getUsage(launchCommand));
        } catch (CommandLineException e) {
            logInvalidOptionMessage(logger, e);
            logger.info(getUsage(launchCommand));
        }
    }

    /**
     * Logs a message about a missing option argument to the given logger.
     * @param logger the logger.
     * @param e the exception about the missing option argument.
     */
    private static void logMissingOptionArgumentMessage(Logger logger, CommandLineException.MissingOptionArgument e) {
        logger.info(CALDocMessages.getString("STATUS.missingArgumentForOption", e.getMessage()));
    }
    
    /**
     * Logs a message about an invalid option to the given logger.
     * @param logger the logger.
     * @param e the exception about the invalid option.
     */
    private static void logInvalidOptionMessage(Logger logger, CommandLineException e) {
        logger.info(CALDocMessages.getString("STATUS.invalidOption", e.getMessage()));
    }

    /**
     * Logs the tool name and copyright to the given logger.
     * @param logger the logger.
     */
    private static void showToolName(Logger logger) {
        logger.info(TOOL_NAME_VERSION_AND_COPYRIGHT);
    }

    /**
     * @param launchCommand the command to launch the tool.
     * @return the usage string.
     */
    private static String getUsage(String launchCommand) {
        String usage =
            TOOL_NAME_VERSION_AND_COPYRIGHT + "\n" +
            "Usage: " + launchCommand + " [options]\n" +
            "\n" +
            "Options:\n" + 
            "-d <directory>             Destination directory for output files\n" + 
            "\n" + 
            "-public                    Show only public entities (default)\n" + 
            "-private                   Show all entities - public, protected and private\n" + 
            "\n" + 
            "-modules <regexp>          Include only the modules matched by the regular expression\n" + 
            "-excludeModules <regexp>   Exclude the modules matched by the regular expression\n" + 
            "-entities <regexp>         Include only the entities (functions, types and data constructors, classes and class methods) matched by the regular expression\n" + 
            "-excludeEntities <regexp>  Exclude the entities (functions, types and data constructors, classes and class methods) matched by the regular expression\n" + 
            "\n" + 
            "-metadata [override]       Include metadata in documentation (use the \'override\' option to hide CALDoc that is superceded by metadata)\n" + 
            "-author                    Include author information\n" + 
            "-version                   Include version information\n" + 
            "-qualifyPreludeNames       Qualify the names of Prelude entities outside the Prelude module\n" +
            "\n" +
            "-use                       Create usage indices\n" +
            "\n" +
            "-doNotSeparateInstanceDoc  Do not separate instance documentation from the main documentation pages\n" +
            "\n" +
            "-locale <locale>           Locale to be used, e.g. en_US or fr\n" + 
            "\n" + 
            "-windowTitle <text>        Browser window title for documentation\n" + 
            "-docTitle <html-code>      Include title for overview page\n" + 
            "-header <html-code>        Include header text for each page\n" + 
            "-footer <html-code>        Include footer text for each page\n" + 
            "-bottom <html-code>        Include bottom text for each page\n" + 
            "\n" +
            "-verbose                   Output detailed messages about what the tool is doing\n" +
            "-quiet                     Do not display status messages to screen\n" +
            "\n" + 
            "-help                      Display command line options and exit\n" + 
            "\n" + 
            "Non-standard -X options:\n" + 
            "-XexcludeTestModules       Exclude 'test' modules\n" + 
            "\n";
        
        return usage;
    }

    /**
     * Parses the given command line arguments and constructs a configuration object from them.
     * @param workspaceManager the workspace manager to be used during documentation generation.
     * @param cmdLine the command line arguments.
     * @param logger the logger to use for logging status messages.
     * @return the configuration object 
     * @throws CommandLineException if the command line arguments cannot be parsed properly.
     */
    public static HTMLDocumentationGeneratorConfiguration makeConfiguration(WorkspaceManager workspaceManager, String cmdLine, Logger logger) throws CommandLineException {
        
        // split the command line with the quote character, so that we can handle quoted strings differently
        String[] quoteSeparatedChunks = cmdLine.split("\"");
        
        List<String> argList = new ArrayList<String>();
        
        // we start out being outside the quotes
        boolean inQuotes = false;
        for (final String quoteSeparatedChunk : quoteSeparatedChunks) {
            
            if (inQuotes) {
                // if inside quotes, then add the quoted chunk as one argument
                argList.add(quoteSeparatedChunk);
            } else {
                // if outside quotes, then split the chunk by whitespace, and add each bit as an argument
                String[] bits = quoteSeparatedChunk.split("\\s");
                for (final String bit : bits) {
                    if (bit.length() > 0) {
                        argList.add(bit);
                    }
                }
            }
            
            // each time we cross a delimiter (quote) boundary, we flip between inside quotes and outside quotes
            inQuotes = !inQuotes;
        }

        String[] args = argList.toArray(new String[argList.size()]);
        
        return makeConfiguration(workspaceManager, args, logger);
    }
    
    /**
     * Parses the given command line arguments and constructs a configuration object from them.
     * @param workspaceManager the workspace manager to be used during documentation generation.
     * @param args the command line arguments.
     * @param logger the logger to use for logging status messages.
     * @return the configuration object 
     * @throws CommandLineException if the command line arguments cannot be parsed properly.
     */
    public static HTMLDocumentationGeneratorConfiguration makeConfiguration(WorkspaceManager workspaceManager, String[] args, Logger logger) throws CommandLineException {
        
        File baseDirectory = new File(".");
        boolean publicEntitiesOnly = false;
        boolean shouldGenerateFromMetadata = false;
        boolean shouldAlwaysGenerateFromCALDoc = true;
        boolean shouldGenerateAuthorInfo = false;
        boolean shouldGenerateVersionInfo = false;
        boolean shouldDisplayPreludeNamesAsUnqualified = true;
        boolean shouldGenerateUsageIndices = false;
        boolean shouldSeparateInstanceDoc = true;
        String windowTitle = "";
        String docTitle = "";
        String header = "";
        String footer = "";
        String bottom = "";
        Locale locale = LocaleUtilities.INVARIANT_LOCALE;
        
        List<ModuleFilter> moduleFilterList = new ArrayList<ModuleFilter>();
        List<ScopedEntityFilter> scopedEntityFilterList = new ArrayList<ScopedEntityFilter>();
        
        ////
        /// Loop through each argument, checking for the option prefix '-'.
        /// If the option requires additional arguments, the cursor (curPos) is advanced appropriately.
        //
        for (int curPos = 0; curPos < args.length; curPos++) {
            String curArg = args[curPos];
            
            if (curArg.startsWith("-")) {
                /// the actual option is after the '-'
                //
                String option = curArg.substring(1);
                
                ////
                /// Check again each supported option
                //
                if (option.equalsIgnoreCase("help")) {
                    // -help was specified, so no generation should actually occur.
                    // we throw the exception here so that the caller can act on it
                    // and terminate after displaying the usage.
                    throw new CommandLineException.HelpRequested();
                    
                } else if (option.equalsIgnoreCase("d")) {
                    // -d <directory>
                    // specifies the output directory
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        baseDirectory = new File(args[curPos]);
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("windowTitle")) {
                    // -windowTitle <text>
                    // specifies the window title
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        windowTitle = args[curPos];
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("docTitle")) {
                    // -docTitle <html-code>
                    // specifies the documentation title
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        docTitle = args[curPos];
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("header")) {
                    // -header <html-code>
                    // specifies the header
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        header = args[curPos];
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("footer")) {
                    // -footer <html-code>
                    // specifies the footer
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        footer = args[curPos];
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("bottom")) {
                    // -bottom <html-code>
                    // specifies the fine print at the bottom of a page
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        bottom = args[curPos];
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("modules")) {
                    // -modules <regexp>
                    // include only the modules matched by the regular expression
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        String regexp = args[curPos];
                        moduleFilterList.add(new RegExpBasedModuleFilter(regexp, false));
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("excludeModules")) {
                    // -excludeModules <regexp>
                    // exclude the modules matched by the regular expression
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        String regexp = args[curPos];
                        moduleFilterList.add(new RegExpBasedModuleFilter(regexp, true));
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("entities")) {
                    // -entities <regexp>
                    // include only the scoped entities matched by the regular expression
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        String regexp = args[curPos];
                        scopedEntityFilterList.add(
                            new QualifiedNameBasedScopedEntityFilter(
                                new RegExpBasedQualifiedNameFilter(regexp, false)));
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("excludeEntities")) {
                    // -excludeEntities <regexp>
                    // exclude the scoped entities matched by the regular expression
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        String regexp = args[curPos];
                        scopedEntityFilterList.add(
                            new QualifiedNameBasedScopedEntityFilter(
                                new RegExpBasedQualifiedNameFilter(regexp, true)));
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("locale")) {
                    // -locale <locale>
                    // locale to be used for generating documentation
                    // 1 additional argument required
                    
                    curPos++;
                    if (curPos < args.length) {
                        locale = LocaleUtilities.localeFromCanonicalString(args[curPos]);
                    } else {
                        throw new CommandLineException.MissingOptionArgument(curArg);
                    }
                    
                } else if (option.equalsIgnoreCase("metadata")) {
                    // -metadata [override]
                    // include metadata in documentation
                    // 1 optional argument: override - hide CALDoc that is superceded by metadata
                    
                    shouldGenerateFromMetadata = true;
                    
                    int nextPos = curPos + 1;
                    if (nextPos < args.length) {
                        String optionalFlag = args[nextPos];
                        if (optionalFlag.equals("override")) {
                            shouldAlwaysGenerateFromCALDoc = false;
                            curPos = nextPos;
                        }
                    }
                    
                } else if (option.equalsIgnoreCase("public")) {
                    // -public
                    // include public entries only
                    // no additional arguments
                    
                    publicEntitiesOnly = true;
                    
                } else if (option.equalsIgnoreCase("private")) {
                    // -private
                    // include public, protected and private entries
                    // no additional arguments
                    
                    publicEntitiesOnly = false;
                    
                } else if (option.equalsIgnoreCase("author")) {
                    // -author
                    // include author info
                    // no additional arguments
                    
                    shouldGenerateAuthorInfo = true;
                    
                } else if (option.equalsIgnoreCase("version")) {
                    // -version
                    // include version info
                    // no additional arguments
                    
                    shouldGenerateVersionInfo = true;
                    
                } else if (option.equalsIgnoreCase("qualifyPreludeNames")) {
                    // -qualifyPreludeNames
                    // qualify the names of Prelude entities outside the Prelude module
                    // no additional arguments
                    
                    shouldDisplayPreludeNamesAsUnqualified = false;
                    
                } else if (option.equalsIgnoreCase("use")) {
                    // -use
                    // create usage indices
                    // no additional arguments
                    
                    shouldGenerateUsageIndices = true;
                    
                } else if (option.equalsIgnoreCase("doNotSeparateInstanceDoc")) {
                    // -doNotSeparateInstanceDoc
                    // do not separate instance documentation from the main documentation pages
                    // no additional arguments
                    
                    shouldSeparateInstanceDoc = false;
                    
                } else if (option.equalsIgnoreCase("verbose")) {
                    // -verbose
                    // output detailed messages about what the tool is doing
                    // no additional arguments
                    
                    logger.setLevel(Level.ALL);
                    
                    Handler[] handlers = logger.getHandlers();
                    for (final Handler handler : handlers) {
                        handler.setLevel(Level.ALL);
                    }
                
                } else if (option.equalsIgnoreCase("quiet")) {
                    // -quiet
                    // do not display status messages to screen
                    // no additional arguments
                    
                    Handler[] handlers = logger.getHandlers();
                    for (final Handler handler : handlers) {
                        handler.setLevel(Level.WARNING);
                    }
                    
                } else if (option.startsWith("X")) {
                    
                    // "hidden" X options
                    
                    // the actual name of the X option is after the X
                    option = option.substring(1);
                    
                    if (option.equalsIgnoreCase("excludeTestModules")) {
                        // -XexcludeTestModules
                        // exclude "test" modules
                        // no additional arguments
                        
                        moduleFilterList.add(new ExcludeTestModulesFilter(workspaceManager.getWorkspace()));
                    } else {
                        throw new CommandLineException(curArg);
                    }
                    
                } else {
                    throw new CommandLineException(curArg);
                }
            } else {
                throw new CommandLineException(curArg);
            }
        }
        
        if (publicEntitiesOnly) {
            // add the public-entries-only filter to the start of the list, since
            // this is a fast filter that has the potential to filter out a lot of entities
            // so it should be the first filter to be run
            scopedEntityFilterList.add(0, new PublicEntitiesOnlyFilter());
        }
        
        // create the module and scoped entity filters from the aggregated lists.
        ModuleFilter moduleFilter = CompositeModuleFilter.make(moduleFilterList);
        ScopedEntityFilter scopedEntityFilter = CompositeScopedEntityFilter.make(scopedEntityFilterList);
        
        FileSystemFileGenerator fileGenerator = new FileSystemFileGenerator(baseDirectory, logger);
        
        return new HTMLDocumentationGeneratorConfiguration(
            fileGenerator,
            moduleFilter,
            scopedEntityFilter,
            shouldGenerateFromMetadata,
            shouldAlwaysGenerateFromCALDoc,
            shouldGenerateAuthorInfo,
            shouldGenerateVersionInfo,
            shouldDisplayPreludeNamesAsUnqualified,
            shouldGenerateUsageIndices,
            shouldSeparateInstanceDoc,
            windowTitle,
            docTitle,
            header,
            footer,
            bottom,
            locale,
            logger);
    }
}
