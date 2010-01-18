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
 * ICE.java
 * Created: Aug 19, 2002 at 2:16:12 PM
 * By: rcypher
 */
package org.openquark.cal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.openquark.cal.caldoc.CALDocToTextUtilities;
import org.openquark.cal.caldoc.CALDocTool;
import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.CALDocComment;
import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassInstanceIdentifier;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.Compiler;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.DerivedInstanceFunctionGenerator;
import org.openquark.cal.compiler.EntryPointGenerator;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.Packager;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.RecordType;
import org.openquark.cal.compiler.Refactorer;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SearchResult;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.SourcePosition;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConsApp;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.Version;
import org.openquark.cal.compiler.ClassInstanceIdentifier.TypeConstructorInstance;
import org.openquark.cal.compiler.ClassInstanceIdentifier.UniversalRecordInstance;
import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.compiler.io.EntryPoint;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.internal.javamodel.JavaBindingGenerator;
import org.openquark.cal.internal.machine.EntryPointImpl;
import org.openquark.cal.internal.machine.g.GMachineConfiguration;
import org.openquark.cal.internal.machine.lecc.LECCMachineStatistics;
import org.openquark.cal.internal.machine.lecc.LECCModule;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.JavaScope;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.LibraryClassSpec;
import org.openquark.cal.internal.machine.lecc.StandaloneJarBuilder.MainClassSpec;
import org.openquark.cal.internal.module.Cal.Core.CAL_Exception_internal;
import org.openquark.cal.internal.runtime.ExecutionContextImpl;
import org.openquark.cal.internal.runtime.ExecutionContextImpl.SuspensionState;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;
import org.openquark.cal.machine.CALExecutor;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.MachineStatistics;
import org.openquark.cal.machine.Module;
import org.openquark.cal.machine.ProgramResourceLocator;
import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.machine.StatsGenerator;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.machine.Program.ProgramException;
import org.openquark.cal.module.Cal.Core.CAL_Debug;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.DebugSupport;
import org.openquark.cal.runtime.MachineConfiguration;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.CarBuilder;
import org.openquark.cal.services.DefaultWorkspaceDeclarationProvider;
import org.openquark.cal.services.NullaryEnvironment;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.ResourceNullaryStore;
import org.openquark.cal.services.ResourcePath;
import org.openquark.cal.services.ResourcePathStore;
import org.openquark.cal.services.ResourceStore;
import org.openquark.cal.services.StandardVault;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.StoredVaultElement;
import org.openquark.cal.services.VaultElementInfo;
import org.openquark.cal.services.VaultRegistry;
import org.openquark.cal.services.WorkspaceConfiguration;
import org.openquark.cal.services.WorkspaceDeclaration;
import org.openquark.cal.services.WorkspaceDeclarationManager;
import org.openquark.cal.services.WorkspaceLoader;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.cal.services.WorkspaceResource;
import org.openquark.util.TextEncodingUtilities;


/** 
 * Interactive CAL Environment - this is command line environment for executing CAL code.
 * <p>
 * Type a CAL expression on the command line and press 'Enter' to execute the expression.
 * <p>
 * ICE commands start with a ':'.
 * <ul>
 *   <li>The ':h' command will list all available commands and environment variables.
 *   <li>The ':ss' command will show the current settings.
 * </ul>
 * 
 * Some environment variables that affect the behaviour of ICE.
 * <dl>
 * <dt>org.openquark.cal.show_adjunct
 *   <dd>if this is defined the generated I/O code for the executing expression will be displayed.
 *   
 * <dt>org.openquark.cal.machine.lecc.source
 *   <dd>If this is defined the lecc machine will generate Java source code, otherwise Java byte-code is directly generated.");
 * 
 * <dt>org.openquark.cal.machine.lecc.runtime_statistics
 *   <dd>Defining this generates runtime statistics for the lecc machine.  Reduction count, function count, etc.");
 * 
 * <dt>org.openquark.cal.machine.lecc.runtime_call_counts
 *   <dd>Defining this generates runtime statistics for function call frequency.");
 * 
 * <dt>org.openquark.cal.machine.debug_capable
 *   <dd>Defining this causes a trace statement to be put at the beginning of each generated function.\nIt also enabled the ability to set breakpoints on CAL functions.");
 * 
 * <dt>org.openquark.cal.machine.lecc.output_directory
 *   <dd>The value of this variable determines the destination of generated source/byte code.");
 * 
 * <dt>org.openquark.cal.machine.lecc.code_generation_stats
 *   <dd>Defining this displays information about generated code.  e.g. # of optimized applications, # of switches, etc.");
 * 
 * <dt>org.openquark.cal.machine.g.runtime_statistics
 *   <dd>Defining this generates runtime statistics for the g machine.  Reduction count, function count, etc.");
 * 
 * <dt>org.openquark.cal.machine.g.call_counts
 *   <dd>Defining this generates runtime statistics for function call frequency.");
 * 
 * <dt>org.openquark.cal.machine.lecc.static_runtime 
 *   <dd>if this is defined, the lecc runtime will generate bytecode statically to disk, and load runtime classes from disk.
 *   <dd>if not defined, runtime classes will be generated and provided on demand.
 * 
 * <dt>org.openquark.cal.machine.lecc.non_interruptible
 *   <dd>if this is defined, the lecc runtime will not check whether a quit has been requested.
 * 
 * <dt>org.openquark.cal.machine.lecc.strict_foreign_entity_loading
 *   <dd>if this is defined, foreign entities corresponding to foreign types and foreign functions will be loaded eagerly during deserialization.
 * </dl>
 * 
 * Creation: Aug 19, 2002 at 2:16:12 PM
 * @author rcypher
 */
public class ICE implements Runnable {
    
    /**
     * A log message formatter that simply outputs the message of the log record.
     * Used by ICE to print message to the console without any additional info.
     * @author Frank Worsley
     */
    private static class ConsoleFormatter extends Formatter {

        /**
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        @Override
        public String format(LogRecord record) {
            return record.getMessage() + "\n";
        }
    }
    
    /** The module name to use when there is no module. */
    private static final ModuleName NO_MODULE = ModuleName.make("NO_MODULE");
    
    /** Map Maps command text to command info. */
    private final Map<String, ICECommand> commandToCommandInfo = new HashMap<String, ICECommand>();
    
    /** Info for all ICE commands. */
    private final Set<ICECommand> commands = new LinkedHashSet<ICECommand>();
    
    
    /** The namespace for ICE log messages. */
    private String iceLoggerNamespace = "org.openquark.cal.ice";
    
    /** An instance of a Logger for ICE messages. */
    private final Logger iceLogger;
    
    /** Whether or not to use a nullary workspace. */
    private static final boolean USE_NULLARY_WORKSPACE = true;
    
    /** The default workspace client id. */
    private static final String DEFAULT_WORKSPACE_CLIENT_ID = USE_NULLARY_WORKSPACE ? null : "ice";
    
    
    /**
     * Setting this system property causes ICE to load its workspace from the file on the classpath named by the value 
     * (unqualified file name - searches within folders called "Workspace Declarations" on the class path)
     */
    public static final String ICE_PROP_WORKSPACE_FILENAME = "org.openquark.cal.ice.workspace.filename";

    private static final String ICE_DEFAULT_WORKSPACE_FILENAME = "ice.default.cws";

    /** Setting this system property causes ICE to load its workspace from the file system path named by the value 
     * (does not search the class path) */
    public static final String ICE_PROP_WORKSPACE = "org.openquark.cal.ice.workspace";
    
    public static final String ICE_PROP_MODULE = "org.openquark.cal.ice.module";
    
    
    
    /** The provider for the input stream on the workspace declaration. */
    private WorkspaceDeclaration.StreamProvider streamProvider;

    private WorkspaceManager workspaceManager = null;
    private FunctionRunThread runThread = null;
    private final ModuleName targetModuleFromProperty = ModuleName.maybeMake(System.getProperty(ICE_PROP_MODULE));
    private ModuleName targetModule;
    private final String targetName = "iceruntarget";
    
    /** The last CAL expression which was evaluated. */
    private String lastCode = null;
    
    /** Remembered previous commands. */
    private String[] commandHistory = new String[10];
    
    /** The number of remembered previous commands. */
    private int nPreviousCommands = 0;
    
    /** The name of the preferred working module.
     *  In the event of a compile failure, the current module may no longer exist if it is a dependent of the module with the failure.
     *  In this case, the current module is changed to another module.
     *  If the failure is later fixed, and recompilation occurs, we attempt to change back. */
    private ModuleName preferredWorkingModuleName = null;
    
    // Flags and generators for statistics.
    private final StatsGenerator executionTimeGenerator = new StatsGenerator();

    private boolean doExecDiag = false;

    /** 
     * the default number of performance runs is 10. Because garbage collection in Java is not under direct user control,
     * we've found quite a lot of variance between individual runs of a performance test. Thus many runs are needed to
     * get an average measurement of sufficiently low standard error of the mean.
     */
    private int nPerformanceRuns = 10;

    private boolean suppressOutput = false;
    
    /** Whether to suppress the display of incremental machine stats. */
    private boolean suppressIncrementalMachineStats = true;
    
    /** Location of startup script. */
    private String scriptLocation = "";
    
    // Location of output file.
    private FileHandler fileHandler = null;
    private String outputFileName = "";
        
    /** Name of file for benchmark results.
     * The file consists of new-line separated values where for each 
     * performance test the results are written as:
     * <benchmark text>
     * <number of result times/labels>
     * <result label>
     * <result time>
     * ...
     */
    private String benchmarkResultFileName = "";
    /** File object for the benchmark results file. */
    private File benchmarkResultFile = null;
    /** A writer for sending results to the benchmark results file. */
    private Writer benchmarkResultWriter= null;
    /** A label to be associated with runtimes placed in the benchmark results file.*/
    private String benchmarkResultLabel = "";
    
    /** Create a buffered reader for the input stream */
    private final BufferedReader inBuff;
    
    private boolean busy = false;

    private PrintStream outputStream = System.out;
    
    /** The status listener that displays progress info when compiling. */
    private ICEStatusListener statusListener;
    
    /** List of trace output filters in effect */
    private final List<Pattern> filters = new LinkedList<Pattern>();    
    
    /** Maximum width of the display console - used as the limit for console line wrap. */
    private static final int MAX_WIDTH = 100;
    
    /** The indentation to format command descriptions*/ 
    private static final int COMMAND_DESCRIPTION_INDENT = 35;
    
    /** The indentation for environment setting help descriptions */
    private static final int ENVIRONMENT_SETTING_HELP_DESCRIPTION_INDENT = 20;

    /** List of available CommandTypes */
    private static List<CommandType> commandTypesList = CommandType.makeCommandTypeList();
    
    /** The command line arguments passed to 'main' when this instance of ICE was created. */
    private String[] commandLineArgs = new String[0];
    
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
    public ICE (String instanceName, 
                String[] args, 
                WorkspaceManager workspaceManager, 
                InputStream inputStream, 
                PrintStream outputStream,
                WorkspaceDeclaration.StreamProvider streamProvider) {
        
        if (streamProvider == null) {
            throw new NullPointerException("The argument streamProvider must not be null.");
        }
        this.streamProvider = streamProvider;
        
        this.outputStream = outputStream;
        
        // Initialize the logger.
        if (instanceName != null && instanceName.length() > 0) {
            iceLoggerNamespace = "org.openquark.cal.ice." + instanceName;

        }
        iceLogger = Logger.getLogger(iceLoggerNamespace);
        iceLogger.setLevel(Level.FINEST);
        iceLogger.setUseParentHandlers(false);
        
        StreamHandler consoleHandler = new StreamHandler(outputStream, new ConsoleFormatter()) {
            
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
        };

        consoleHandler.setLevel(Level.INFO);
        iceLogger.addHandler(consoleHandler);
        
        this.inBuff = new BufferedReader(new BufferedReader(new java.io.InputStreamReader(inputStream)));
        
        // Cache the command line args in an instance field.  These will be used later in 
        // initialize.  Note: initialize is no longer called in this constructor because doing
        // so does not allow sub-classes to override initialize.
        this.commandLineArgs = args;
        
//        try {
//            FileHandler fileHandler = new FileHandler("c:\\temp\\icelog.txt");
//            fileHandler.setLevel(Level.FINEST);
//            fileHandler.setFormatter(new ConsoleFormatter());
//            ICE_LOGGER.addHandler(fileHandler);
//        } catch (IOException ex) {
//            ICE_LOGGER.log(Level.INFO, "Error installing file output handler.");
//        }
    }
    
    protected final ModuleName getTargetModule () {
        return targetModule;
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
     * Create a new ICE object and start its input loop.
     * @param args
     * @param fallbackWorkspaceDeclaration
     */
    public static void appMain(String[] args, String fallbackWorkspaceDeclaration) {
        ICE newIce = new ICE (null, args, null, System.in, System.out, DefaultWorkspaceDeclarationProvider.getDefaultWorkspaceDeclarationProvider(ICE_PROP_WORKSPACE, fallbackWorkspaceDeclaration));
        newIce.run ();
    }
    
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

        ICE.appMain(args, fallbackWorkspaceDeclaration);
    }
    
    /**
     * @param stringToCheck the string to check
     * @return true if the string to check is null or has a trimmed length of 0.
     */
    private static boolean isNullOrEmptyString(String stringToCheck) {
        return (stringToCheck == null || stringToCheck.trim().length() == 0);
    }

    /**
     * Initialize this instance of ICE.
     * @param wkMgr - the current WorkspaceManager
     */
    private void initialize (WorkspaceManager wkMgr) {
        // Banner
        iceLogger.log(Level.INFO, "Interactive CAL Environment, version " + Version.CURRENT);
        iceLogger.log(Level.INFO, "Copyright (c) 2007 Business Objects Software Limited. All rights reserved.");
        iceLogger.log(Level.INFO, "Enter ':h' for help.");
        iceLogger.log(Level.INFO, " ");

        parseCommandLine (commandLineArgs);
        
        if (wkMgr != null) {
            this.workspaceManager = wkMgr;
            statusListener = new ICEStatusListener();
            workspaceManager.addStatusListener(statusListener);
        } else {
            createWorkspaceManager();
        }
        
        command_showSettings ();
        
        iceLogger.log(Level.INFO, " ");
        
        // Load default workspace.
        if (wkMgr == null) {
            compileWorkspace(true, false, false);
        }
        
        if (targetModuleFromProperty != null) {

            if (getWorkspaceManager().hasModuleInProgram(targetModuleFromProperty)) {
                targetModule = targetModuleFromProperty;
            } else {
                targetModule = getDefaultWorkingModuleName();
            }
            preferredWorkingModuleName = targetModuleFromProperty;
            
        } else {
            targetModule = getDefaultWorkingModuleName();
            preferredWorkingModuleName = targetModule;
        }
        
        if (NO_MODULE.equals(targetModule)) {
            iceLogger.log(Level.INFO, "\nUnable to find valid target module.");
            
        } else if (targetModuleFromProperty != null && !targetModule.equals(targetModuleFromProperty)) {
            iceLogger.log(Level.INFO, "\nSpecified module " + targetModuleFromProperty + " is unavailable. Module set to: " + targetModule);
            
        } else {
            iceLogger.log(Level.INFO, "\nModule set to: " + targetModule);
        }
        
        initializeCommands ();
    }

    /**
     * Initialize the commands that can be executed in ICE.
     */
    protected void initializeCommands () {
        
        // General ICE commands
        addCommand(new ICECommand(new String[] { "q", "quit" }, true, true,
                CommandType.GENERAL, new String[] { ":q[uit]" },
                new String[] { "End ICE session." }) {
            @Override
            protected void performCommand(String info) throws QuitException {
                command_quit(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "v", "version" },
                true,
                true,
                CommandType.GENERAL,
                new String[] { ":v[ersion]" },
                new String[] { "Display the version of the CAL platform in use." }) {
            @Override
            protected void performCommand(String info) {
                command_version(info);
            }
        });
      
        addCommand(new ICECommand(
                new String[] { "rs" },
                false,
                true,
                CommandType.GENERAL,
                new String[] { ":rs  [module name]" },
                new String[] { "Reset any cached CAFs in the named module, or all modules if none specified." }) {
            @Override
            protected void performCommand(String info) {
                command_resetCachedResults(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "rsm" },
                false,
                true,
                CommandType.GENERAL,
                new String[] { ":rsm [module name]" },
                new String[] { "Reset machine state associated with the named module, or all modules if none specified." }) {
            @Override
            protected void performCommand(String info) {
                command_resetMachineState(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "rc" },
                false,
                true,
                CommandType.GENERAL,
                new String[] { ":rc" },
                new String[] { "Recompile: This will only compile modified modules and will only regenerate code for modified CAL entities." }) {
            @Override
            protected void performCommand(String info) {
                command_recompileWorkspace(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "rca" },
                false,
                true,
                CommandType.GENERAL,
                new String[] { ":rca" },
                new String[] { "Recompile all: This will compile all modules, but only regenerate code for modified CAL entities." }) {
            @Override
            protected void performCommand(String info) {
                command_recompileAll(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "rl" },
                false,
                true,
                CommandType.GENERAL,
                new String[] { ":rl" },
                new String[] { "Reload the program.  This will recompile/regenerate all CAL entities." }) {
            @Override
            protected void performCommand(String info) {
                command_reloadWorkspace(info);
            }
        });
       
        addCommand(new ICECommand(new String[] { "sm" }, false, true,
                CommandType.GENERAL,
                new String[] { ":sm  <target_module_name>" },
                new String[] { "Set the module to operate in." }) {
            @Override
            protected void performCommand(String info) {
                command_setModule(info);
            }
        });
       
       
        addCommand(new ICECommand(new String[] { "rr" }, false, true,
                CommandType.GENERAL, new String[] { ":rr" },
                new String[] { "Re-evaluate the last entered CAL expression." }) {
            @Override
            protected void performCommand(String info) {
                command_runLastExpression(info);
            }
        });
        addCommand(new ICECommand(new String[] { "script" }, false, true,
                CommandType.GENERAL,
                new String[] { ":script <script file name>" },
                new String[] { "Run the specified script." }) {
            @Override
            protected void performCommand(String info) throws QuitException {
                command_script(info);
            }
        });
       
        addCommand(new ICECommand(
                new String[] { "of" },
                true,
                true,
                CommandType.GENERAL,
                new String[] { ":of <output file name>" },
                new String[] { "Set a file location where subsequent output will be mirrored." }) {
            @Override
            protected void performCommand(String info) {
                command_outputFile(info);
            }
        });
               addCommand(new ICECommand(new String[] { "h", "help" }, true, true,
                CommandType.GENERAL, new String[] { ":h[elp] [<topic>]" },
                new String[] { "Show the ICE help."}) {
                   @Override
                protected void performCommand(String info) {
                command_help(info);
            }
        });
        
        // Program and work space manipulation ICE commands 
        addCommand(new ICECommand(new String[] { "lmw" },
                false,
                true,
                CommandType.WORKSPACE,
                new String[] { ":lmw <dependent_module_names>" },
                new String[] { "Load the mimimal workspace that contains all of the named modules." }) {
            @Override
            protected void performCommand(String info) {
                command_loadMinimalWorkspace(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "adm" },
                false,
                true,
                CommandType.WORKSPACE,
                new String[] { ":adm <module_name>" },
                new String[] { "Load a module from the current environment." }) {
            @Override
            protected void performCommand(String info) {
                command_addDiscoverableModule(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "ldw" },
                false,
                true,
                CommandType.WORKSPACE,
                new String[] { ":ldw <workspace_file_name>" },
                new String[] { "Load a workspace file from the current environment." }) {
            @Override
            protected void performCommand(String info) {
                command_loadDiscoverableWorkspace(info);
            }
        });
        addCommand(new ICECommand(new String[] { "lw" }, false, true,
                CommandType.WORKSPACE,
                new String[] { ":lw  <workspace_file_name>" },
                new String[] { "Load from the specified workspace file." }) {
            @Override
            protected void performCommand(String info) {
                command_loadWorkspace(info);
            }
        });
        addCommand(new ICECommand(new String[] { "lm" }, true, true,
                CommandType.WORKSPACE, new String[] { ":lm" },
                new String[] { "List the available modules." }) {
            @Override
            protected void performCommand(String info) {
                command_listModules(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "sdw" },
                false,
                true,
                CommandType.WORKSPACE,
                new String[] { ":sdw" },
                new String[] { "Show the names of the workspace files found in the current environment." }) {
            @Override
            protected void performCommand(String info) {
                command_showDiscoverableWorkspaces(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "sdm" },
                false,
                true,
                CommandType.WORKSPACE,
                new String[] { ":sdm" },
                new String[] { "Show the names of the modules found in the current environment, but not in the current program." }) {
            @Override
            protected void performCommand(String info) {
                command_showDiscoverableModules(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "rm" },
                false,
                true,
                CommandType.WORKSPACE,
                new String[] { ":rm  <target_module_name>" },
                new String[] { "Remove the named module and any dependent modules." }) {
            @Override
            protected void performCommand(String info) {
                command_removeModule(info);
            }
        });
        
               
        // info ICE commands
        addCommand(new ICECommand(
                new String[] { "smd" },
                true,
                true,
                CommandType.INFO,
                new String[] { ":smd <dependee_module_name>" },
                new String[] { "Show all modules which depend on the named module." }) {
            @Override
            protected void performCommand(String info) {
                command_showModuleDependents(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "si" },
                true,
                true,
                CommandType.INFO,
                new String[] { ":si  <dependent_module_names>" },
                new String[] { "Show all modules that the named modules import (either directly or indirectly) with some statistics. " }) {
            @Override
            protected void performCommand(String info) {
                command_showModuleImports(info);
            }
        });
        addCommand(new ICECommand(new String[] { "t" }, true, true,
                CommandType.INFO, new String[] { ":t <expression>" },
                new String[] { "Display the type of the given expression." }) {
            @Override
            protected void performCommand(String info) {
                command_type(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "sps" },
                true,
                true,
                CommandType.INFO,
                new String[] { ":sps [module names]" },
                new String[] { "Show summarized program statistics for the named module(s), or all modules if none specified." }) {
            @Override
            protected void performCommand(String info) {
                command_showProgramStatistics(info);
            }
        });
        addCommand(new ICECommand(new String[] { "ss" }, true, true,
                CommandType.INFO, new String[] { ":ss" },
                new String[] { "Show the current ICE settings." }) {
            @Override
            protected void performCommand(String info) {
                command_showSettings();
            }
        });
        addCommand(new ICECommand(
                new String[] { "sts" },
                true,
                true,
                CommandType.INFO,
                new String[] { ":sts" },
                new String[] { "Show the current settings for function tracing." }) {
            @Override
            protected void performCommand(String info) {
                command_showTraceSettings();
            }
        });
        addCommand(new ICECommand(
                new String[] { "swi" },
                false,
                true,
                CommandType.INFO,
                new String[] { ":swi" },
                new String[] { "Show debug information about the workspace, including the actual location of the workspace declaration file and the modules." }) {
            @Override
            protected void performCommand(String info) {
                command_showWorkspaceDebugInfo(info);
            }
        });
        addCommand(new ICECommand(
                new String[]{"lcfs"},
                true,
                true,
                CommandType.INFO,
                new String[]{":lcfs"},
                new String[]{"Loaded Class File Size: Show the number and size of classes loaded by the CAL class loader."}) {
            @Override
            protected void performCommand (String info) {
                command_classFileSize(false, true);
            }
        });
        addCommand(new ICECommand(
                new String[] { "cfs" },
                true,
                true,
                CommandType.INFO,
                new String[] { ":cfs" },
                new String[] { "Class File Size: Show the total size of all generated class files." }) {
            @Override
            protected void performCommand(String info) {
                command_classFileSize(true, true);
            }
        });
        addCommand(new ICECommand(
                new String[] { "lcafs" },
                true,
                true,
                CommandType.INFO,
                new String[] { ":lcafs [<module_name>] [<includeDependees>]" },
                new String[] { "Lists all the constant applicative forms (CAFs) in the given module and any dependee modules if indicated" +
                               "(with true or false). Defaults to the target module and false." }) {
            @Override
            protected void performCommand(String info) {
                command_listCafs(info);
            }
        });
      
        
        // ICE Debug Commands
        addCommand(new ICECommand(new String[] { "step" }, true, true,
                CommandType.DEBUG, new String[] { ":step", ":step <CAL expression>" },
                new String[] { "Step through a suspended CAL program (on the current thread if multithread enabled).",
                               "Step through the provided CAL expression."}) {
            @Override
            protected void performCommand(String info) {
                command_step(info);
            }
        });
        addCommand(new ICECommand(new String[] { "re" }, true, true,
                CommandType.DEBUG, new String[] { ":re" },
                new String[] { "Resume execution of suspended CAL program." }) {
            @Override
            protected void performCommand(String info) {
                ExecutionContextImpl ec = ICE.this.getExecutionContext();
                if (ec.hasSuspendedThreads()) {
                    ec.setStepping(false);
                    command_resumeExecution();
                } else {
                    iceLogger.log(Level.INFO, "There is no CAL program currently suspended.");
                }
            }
        });
        addCommand(new ICECommand(new String[] { "ret" }, true, true,
                CommandType.DEBUG, new String[] { ":ret" },
                new String[] { "Resume execution of the current suspended thread." }) {
            @Override
            protected void performCommand(String info) {
                ExecutionContextImpl ec = ICE.this.getExecutionContext();
                if (ec.hasSuspendedThreads()) {
                    ec.setStepping(false);
                    command_resumeCurrentThread(false);
                } else {
                    iceLogger.log(Level.INFO, "There is no CAL program currently suspended.");
                }
            }
        });
        addCommand(new ICECommand(new String[] { "sus", "suspend" }, true, true,
                CommandType.DEBUG, new String[] { ":sus[pend]" },
                new String[] { "Suspend all threads in the executing CAL program." }) {
            @Override
            protected void performCommand(String info) {
                if (runThread != null) {
                    runThread.requestSuspend();
                }
            }
        });
        addCommand(new ICECommand(new String[] { "threads" }, true, true,
                CommandType.DEBUG, new String[] { ":threads" },
                new String[] { "Show the current suspended threads." }) {
            @Override
            protected void performCommand(String info) {
                ExecutionContextImpl ec = ICE.this.getExecutionContext();
                if (ec.hasSuspendedThreads()) {
                    command_showSuspendedThreads();
                } else {
                    iceLogger.log(Level.INFO, "There is no CAL program currently suspended.");
                }
            }
        });
        addCommand(new ICECommand(new String[] { "thread" }, true, true,
                CommandType.DEBUG, new String[] { ":thread <thread id>" },
                new String[] { "Set the current suspended thread." }) {
            @Override
            protected void performCommand(String info) {
                ExecutionContextImpl ec = ICE.this.getExecutionContext();
                if (ec.hasSuspendedThreads()) {
                    command_setSuspendedThread(info);
                } else {
                    iceLogger.log(Level.INFO, "There is no CAL program currently suspended.");
                }
            }
        });
        addCommand(new ICECommand(new String[] { "te" }, true, true,
                CommandType.DEBUG, new String[] { ":te" },
                new String[] { "Terminate execution of suspended CAL program." }) {
            @Override
            protected void performCommand(String info) {
                ExecutionContextImpl ec = ICE.this.getExecutionContext();
                if (ec.hasSuspendedThreads()) {
                    command_terminateExecution(info);
                    ec.setStepping(false);
                } else {
                    iceLogger.log(Level.INFO, "There is no CAL program currently suspended.");
                }
            }
        });
        addCommand(new ICECommand(
                new String[] { "show" },
                true,
                true,
                CommandType.DEBUG,
                new String[] { ":show",
                        ":show function",
                        ":show argnames",
                        ":show argTypes",
                        ":show <argument name>",
                        ":show stack [expanded]"},
                new String[] {
                        "Show the state of the current CAL suspension (associated with the current thread if multithread enabled). This displays the function name and the names and values of any arguments.",
                        "Show the name of the suspended function.",
                        "Show the names of the arguments to the suspended function.",
                        "Show the types of the arguments to the suspended function.",
                        "Show the value for the named argument.",
                        "Show the call stack of CAL functions. If expanded is specified the call stack will include non-CAL functions."}) {
            @Override
            protected void performCommand(String info) {
                command_show(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "bp" },
                true,
                true,
                CommandType.DEBUG,
                new String[] { ":bp <qualified name>", 
                        ":bp show",
                        ":bp clear"},
                new String[] {
                        "Toggles a breakpoint for the named CAL function. The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for the breakpoints to have an effect.",
                        "Show the current set of breakpoints.",
                        "Clear all breakpoints."}) {
            @Override
            protected void performCommand(String info) {
                command_breakpoint(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "trace" },
                true,
                true,
                CommandType.DEBUG,
                new String[] { ":trace on", ":trace off",
                        ":trace <qualified name>",
                        ":trace clear",
                        ":trace arguments on",
                        ":trace arguments off",
                        ":trace threadname on",
                        ":trace threadname off"},
                new String[] {
                        "Enable tracing for all CAL functions. The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur.",
                        "Disable tracing for all CAL functions. The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur.",
                        "Toggle tracing for the named CAL function. This overrides the general trace setting. The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur.",
                        "Clear all CAL functions for which tracing had been specifically enabled.  The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur.",
                        "Enable tracing of function arguments.  The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur.",
                        "Disable tracing of function arguments.  The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur.",
                        "Include the thread name in trace messages.  The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur.",
                        "Don't include the thread name in trace messages.  The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur."}) {
            @Override
            protected void performCommand(String info) {
                command_trace(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "fa" },
                true,
                true,
                CommandType.DEBUG,
                new String[] { ":fa <regExpr>" },
                new String[] { "Add the given filter to the list of filters in effect for the function trace output. The " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +" flag must be set in order for function tracing to occur." }) {
            @Override
            protected void performCommand(String info) {
                command_filterAdd(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "fr" },
                true,
                true,
                CommandType.DEBUG,
                new String[] { ":fr <index>" },
                new String[] { "Remove filter number n from the list of filters for function tracing." }) {
            @Override
            protected void performCommand(String info) {
                command_filterRemove(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "fs" },
                true,
                true,
                CommandType.DEBUG,
                new String[] { ":fs" },
                new String[] { "List the filters for the function trace output currently in effect." }) {
            @Override
            protected void performCommand(String info) {
                command_filterShow(info);
            }
        });
        addCommand(new ICECommand(new String[] { "stt" }, false, true,
                CommandType.DEBUG, new String[] { ":stt" },
                new String[] { "Display the stack trace for an error result." }) {
            @Override
            protected void performCommand(String info) {
                command_showStackTrace(info);
            }
        });
        addCommand(new ICECommand(new String[] { "d" }, false, true,
                CommandType.DEBUG, new String[] { ":d <function_name>" },
                new String[] { "Disassemble the named function." }) {
            @Override
            protected void performCommand(String info) {
                command_disassemble(info);
            }
        });
        addCommand(new ICECommand(new String[] { "da" }, false, true,
                CommandType.DEBUG, new String[] { ":da" },
                new String[] { "Disassemble all functions." }) {
            @Override
            protected void performCommand(String info) {
                command_disassembleAll(info);
            }
        });
        addCommand(new ICECommand(new String[] { "ded" }, true, true,
                CommandType.DEBUG, new String[] { ":ded" },
                new String[] { "Toggle execution diagnostics." }) {
            @Override
            protected void performCommand(String info) {
                doExecDiag = !doExecDiag;
            }
        });

        // ICE benchmark commands
        addCommand(new ICECommand(
                new String[] { "pt" },
                false,
                true,
                CommandType.BENCHMARK,
                new String[] { ":pt <cal code>" },
                new String[] { "Run a performance test on the specified CAL code." }) {
            @Override
            protected void performCommand(String info) {
                command_performanceTest(info, false);
            }
        });
        addCommand(new ICECommand(
                new String[] { "ptm" },
                false,
                true,
                CommandType.BENCHMARK,
                new String[] { ":ptm <cal code>" },
                new String[] { "Run a performance test on the specified CAL code where the machine state is reset prior to each run in the test." }) {
            @Override
            protected void performCommand(String info) {
                command_performanceTest(info, true);
            }
        });
        addCommand(new ICECommand(
                new String[] { "brf" },
                true,
                true,
                CommandType.BENCHMARK,
                new String[] { ":brf [benchmark file name]" },
                new String[] { "Set a file location where benchmark results will be written.  If no name is provided the current file is simply closed.  The file content can be manipulated by a set of CAL functions in the Benchmarks module." }) {
            @Override
            protected void performCommand(String info) {
                command_benchmarksResultFile(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "brl" },
                true,
                true,
                CommandType.BENCHMARK,
                new String[] { ":brl <benchmark label>" },
                new String[] { "Set a label for runtimes recorded in the benchmark results file." }) {
            @Override
            protected void performCommand(String info) {
                command_benchmarksResultLabel(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "sptr" },
                true,
                true,
                CommandType.BENCHMARK,
                new String[] { ":sptr <number of runs>" },
                new String[] { "Set the number of runs used in performance testing." }) {
            @Override
            protected void performCommand(String info) {
                command_setPerformanceTestRuns(info);
            }
        });

        // ICE CALDoc commands
        addCommand(new ICECommand(
                new String[] { "docgen" },
                true,
                true,
                CommandType.CALDOC,
                new String[] { ":docgen <options>" },
                new String[] { "Run the CALDoc HTML documentation generator. For more usage details, type :docgen -help" }) {
            @Override
            protected void performCommand(String info) {
                command_docGen(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "docm" },
                true,
                true,
                CommandType.CALDOC,
                new String[] { ":docm <module_name>" },
                new String[] { "Show the CALDoc comment associated with the named module." }) {
            @Override
            protected void performCommand(String info) {
                command_docModule(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "docf" },
                true,
                true,
                CommandType.CALDOC,
                new String[] { ":docf <function_or_class_method_name>" },
                new String[] { "Show the CALDoc comment associated with the named function or class method." }) {
            @Override
            protected void performCommand(String info) {
                command_docFunctionOrClassMethod(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "doct" },
                true,
                true,
                CommandType.CALDOC,
                new String[] { ":doct <type_name>" },
                new String[] { "Show the CALDoc comment associated with the named type constructor." }) {
            @Override
            protected void performCommand(String info) {
                command_docType(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "docd" },
                true,
                true,
                CommandType.CALDOC,
                new String[] { ":docd <data_constructor_name>" },
                new String[] { "Show the CALDoc comment associated with the named data constructor." }) {
            @Override
            protected void performCommand(String info) {
                command_docDataConstructor(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "docc" },
                true,
                true,
                CommandType.CALDOC,
                new String[] { ":docc <type_class_name>" },
                new String[] { "Show the CALDoc comment associated with the named type class." }) {
            @Override
            protected void performCommand(String info) {
                command_docTypeClass(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "doci" },
                true,
                true,
                CommandType.CALDOC,
                new String[] { ":doci <type_class_name> <instance_type_constructor>" },
                new String[] { "Show the CALDoc comment associated with the named instance." }) {
            @Override
            protected void performCommand(String info) {
                command_docTypeClassInstance(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "docim" },
                true,
                true,
                CommandType.CALDOC,
                new String[] { ":docim <method_name> <type_class_name> <instance_type_constructor>" },
                new String[] { "Show the CALDoc comment associated with the named instance method." }) {
            @Override
            protected void performCommand(String info) {
                command_docTypeClassInstanceMethod(info);
            }
        });

        // ICE Find commands
        addCommand(new ICECommand(
                new String[] { "f", "find" },
                true,
                true,
                CommandType.FIND,
                new String[] { ":f[ind] [all] <entity_name>",
                        ":f[ind] ref <gem_name>",
                        ":f[ind] defn <gem_or_type_class_name>",
                        ":f[ind] instByType <type_name>",
                        ":f[ind] instByClass <type_class_name>", 
                        ":f[ind] constructions <type_or_constructor_name>" 
                        },
                new String[] {
                        "Display the locations of each occurrence of the specified function, method, or data constructor",
                        "Display the locations of each reference to the specified function, method, or data constructor",
                        "Display the location where the specified function, method, data constructor, type, or type class is defined",
                        "Display the locations of each instance definition for every class that the specified type is an instance of",
                        "Display the locations of each instance of the specified type class",
                        "Display the locations where values are constructed. If a type constructor is specified then any construction using a data constructor of that type is found."
                        }) {
            @Override
            protected void performCommand(String info) {
                command_find(info);
            }
        });

        // ICE Refactor commands
        addCommand(new ICECommand(
                new String[] { "rf", "refactor" },
                false,
                true,
                CommandType.REFACTOR,
                new String[] { ":rf imports [<module_name>] [nogroup]",
                        ":rf typeDecls [<module_name>]" },
                new String[] {
                        "Clean the import declarations in the specified module, or in the current module if module_name is omitted.  If nogroup is specified, using items will not be combined.",
                        "Add type declarations to functions and local definitions that currently lack them in the specified module, or in the current module if module_name is omitted." }) {
            @Override
            protected void performCommand(String info) {
                command_refactor(info);
            }
        });
        // Pretty print
        addCommand(new ICECommand(
                new String[] { "pp", "prettyPrint" },
                false,
                true,
                CommandType.REFACTOR,
                new String[] { ":pp [<module_name>] [<function_name> [<function_name> ...]] [startLine endLine]"},
                new String[] {
                        "Pretty print module." }) {
            @Override
            protected void performCommand(String info) {
                command_prettyPrint(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "ren", "rename" },
                false,
                true,
                CommandType.REFACTOR,
                new String[] { ":ren[ame] [<category>] <old_identifier> <new_identifier>" },
                new String[] { "Rename <old_identifier> to <new_identifier>.  <category> is one of function, dataConstructor, typeConstructor, typeClass, or module." }) {
            @Override
            protected void performCommand(String info) {
                command_rename(info);
            }
        });

        // ICE utilities commands
        addCommand(new ICECommand(
                new String[] { "jar" },
                false,
                true,
                CommandType.UTILITIES,
                new String[] { ":jar [-verbose] (-main <functionName> <mainClassName> | -lib <moduleName> <libClassScope> <libClassName>)+ <outputJarName> [-src <outputSrcZipName>]" },
                new String[] { "Create a standalone JAR from a given entry point."
                              + "-verbose is an option flag for displaying more detailed status information"
                              + "-main specifies a CAL application to be included with the standalone JAR"
                              + "   functionName: should be fully qualified CAL function name which is the main entry point for the application"
                              + "   mainClassName: is the fully qualified name of the Java main class to be generated"
                              + "-lib specifies a CAL library to be included with the standalone JAR"
                              + "   moduleName: fully-qualified name of the CAL library module"
                              + "   libClassScope: scope of the generated library class. Can be one of: public, protected, package, or private"
                              + "   libClassName: fully-qualified name of the Java main class to be generated"
                              + "outputJarName is the name of the output JAR file or a path."
                              + " -src <outputSrcZipName> is optional for specifying the name of the output zip file containing source files for the generated classes."
                        
                }) {
            @Override
            protected void performCommand(String info) {
                command_buildStandaloneJar(info);
            }
        });
    
        addCommand(new ICECommand(
                new String[] { "car" },
                false,
                true,
                CommandType.UTILITIES,
                new String[] { ":car [-keepsource] [-nocws | -nosuffix] [-s] [-jar] [-d] <output directory>" },
                new String[] { "Create a CAL Archive (Car) file from the current workspace in the given directory.\n"
                        + "If -keepsource is specified, then the generated Cars will contain the full source of the modules (versus having sourceless modules with empty CAL file stubs).\n"
                        + "If -nocws is specified, then a new workspace declaration file will not be generated for each output Car.\n"
                        + "If -nosuffix is specified, then the workspace declaration files generated will end simply with .cws rather than .car.cws.\n"
                        + "If -s is specified, then the modules that come from Cars will be skipped over in the output Cars.\n"
                        + "If -jar is specified, then the output will be in the form of Car-jars.\n"
                        + "If -d is specified, then a Car file is created in the given directory for every workspace declaration file imported by the current workspace." }) {
            @Override
            protected void performCommand(String info) {
                command_buildCar(info);
            }
        });
        
        addCommand(new ICECommand(
                new String[] { "wsi" },
                false,
                true,
                CommandType.UTILITIES,
                new String[] { ":wsi [-l] <dependent_module_names> \"<workspace_file_name>\"" },
                new String[] { "Create a minimal workspace that contains " +
                        "all of the named modules and save it to workspace_file_name. " +
                        "If -l is specified, then the newly created minimal workspace will be loaded." }) {
            @Override
            protected void performCommand(String info) {
                command_makeMinimalWorkspace(info);
            }
        });
        
        addCommand(new ICECommand(
                new String[] { "generate", "gen" },
                true,
                true,
                CommandType.UTILITIES,
                new String[] { ":gen[erate] deriving [compact] <type_name>",
                        ":gen[erate] deriving efficient <type_name>" },
                new String[] {
                        "Generate and display Eq, Ord and Show instances for the given type. The implementation is compact, in that notEquals is defined in terms of equals, and the Ord methods are defined in terms of compare.",
                        "Generate and display Eq, Ord and Show instances for the given type. The implementation is efficient, in that the Eq and Ord methods are all implemented directly using case expressions." }) {
            @Override
            protected void performCommand(String info) {
                command_generate(info);
            }
        });

        // ICE Command History commands
        addCommand(new ICECommand(
                new String[] { "spc" },
                true,
                false,
                CommandType.HISTORY,
                new String[] { ":spc" },
                new String[] { "Show previously executed commands in a numbered list" }) {
            @Override
            protected void performCommand(String info) {
                command_showPreviousCommands(info);
            }
        });
        addCommand(new ICECommand(
                new String[] { "pc" },
                false,
                false,
                CommandType.HISTORY,
                new String[] { ":pc <command number> " },
                new String[] { "Execute a previous command indicated by the command number" }) {
            @Override
            protected void performCommand(String info) throws QuitException {
                command_previousCommand(info);
            }
        });
        addCommand(new ICECommand(new String[] { "npc" }, true, true,
                CommandType.HISTORY,
                new String[] { ":npc <number of commands> " },
                new String[] { "Set the size of the command history" }) {
            @Override
            protected void performCommand(String info) {
                command_nPreviousCommands(info);
            }
        });

        addCommand(
                new ICECommand(
                        new String[]{"javabinding", "jb"}, 
                        true, 
                        true, 
                        CommandType.UTILITIES,
                        new String[]{":javaBinding <module name pattern> <target package> [internal] [test]"},
                        new String[]{"Generate the Java binding class for the modules which match the given regular expression.\nThe target package specifies the package which will contain the generated binding class.\nBy default bindings are generated only for public members of the module.\nSpecifying the 'internal' parameter will generate bindings for only the non-public members of the module.\r\nSpecifying the 'test' parameter will check that a valid and up-to-date binding class exists, without actually generating the class."}) {
                    @Override
                    protected void performCommand(String arguments) {
                       command_javaBinding(arguments); 
                    }
                });
        
        // ICe Other commands
        addCommand(new ICECommand(
                new String[] { "caf" },
                false,
                true,
                CommandType.OTHER,
                new String[] { ":caf <CAF name>" },
                new String[] { "Directly execute the named constant applicative form." }) {
            @Override
            protected void performCommand(String info) {
                runCAF(info);
            }
        });
        addCommand(new ICECommand(new String[] { "mt" }, false, true,
                CommandType.OTHER, new String[] { ":mt <machine_type>" },
                new String[] { "Set the machine type." }) {
            @Override
            protected void performCommand(String info) {
                command_machineType(info);
            }
        });
        addCommand(new ICECommand(new String[] { "sms" }, true, true,
                CommandType.OTHER, new String[] { ":sms" },
                new String[] { "Toggle suppression of the incremental machine statistics." }) {
            @Override
            protected void performCommand(String info) {
                command_suppressIncrementalMachineStats(info);
            }
        });
        addCommand(new ICECommand(new String[] { "so" }, true, true,
                CommandType.OTHER, new String[] { ":so" },
                new String[] { "Toggle suppression of the output." }) {
            @Override
            protected void performCommand(String info) {
                command_suppressOutput(info);
            }
        });
        
    }

    /**
     * Execute the 'javaBinding' command.
     * @param arguments - the arguments passed to the java binding command.
     */
    private void command_javaBinding (String arguments) {
        if (arguments == null || arguments.trim().length() == 0) {
            iceLogger.log(Level.INFO, "Must specify the name of the Module for which a Java binding will be generated.");
            return;
        }

        if (ICE.this.workspaceManager != null) {
            synchronized (ICE.this.workspaceManager) {

                StringTokenizer stk = new StringTokenizer(arguments);
                List<String> argValues = new ArrayList<String>();
                while (stk.hasMoreTokens()) {
                    argValues.add(stk.nextToken());
                }
                
                if (argValues.size() < 2 || argValues.size() > 4) {
                    iceLogger.log(Level.INFO, "Invalid number of arguments for java binding command.");
                    return;
                }
                
                // First argument will be the module name pattern.
                String moduleNamePattern = argValues.get(0);
                
                // Second argument will be the target package.
                String targetPackage = argValues.get(1);
                
                boolean publicEntities = true;
                boolean testOnly = false;
                
                if (argValues.size() > 2) {
                    for (int i = 2; i < argValues.size(); ++i) {
                        String nextArgRaw = argValues.get(i);
                        String nextArg = nextArgRaw.trim().toLowerCase();
                        if (nextArg.equals("internal")) {
                            publicEntities = false;
                        } else
                        if (nextArg.equals("test")){
                            testOnly = true;
                        } else {
                            iceLogger.log(Level.INFO, "Invalid argument for java binding command: " + nextArgRaw);
                            return;
                        }
                    }
                }
                
                ModuleTypeInfo typeInfo;
                ModuleName resolvedModuleName = resolveModuleNameInWorkspace(moduleNamePattern, false); // false so that no warning is logged if the module does not exist - it may be a regular expression
                if (resolvedModuleName == null) {
                    typeInfo = null;
                } else {
                    typeInfo = workspaceManager.getModuleTypeInfo(resolvedModuleName);
                }
                
                if (typeInfo == null) {
                    // This could be a regular expression.
                    try {
                        Pattern regexPattern = Pattern.compile(moduleNamePattern);
                        List<ModuleName> moduleNames = new ArrayList<ModuleName>(Arrays.asList(getWorkspaceManager().getModuleNamesInProgram()));
                        Collections.sort (moduleNames);

                        boolean foundMatch = false;
                        for (final ModuleName possibleModuleName : moduleNames) {
                            if (regexPattern.matcher(possibleModuleName.toSourceText()).matches()) {
                                foundMatch = true;
                                generateJavaBinding(possibleModuleName, targetPackage, publicEntities, testOnly);
                            }
                        }
                        
                        if (!foundMatch) {
                            iceLogger.log(Level.INFO, "No module in the current workspace matches the pattern " + moduleNamePattern + ".");
                        }
                    } catch (PatternSyntaxException e) {
                        iceLogger.log(Level.INFO, "The module " + moduleNamePattern + " does not exist in the current workspace.");
                    }
                } else {
                    generateJavaBinding(resolvedModuleName, targetPackage, publicEntities, testOnly);
                }
            }
        }    

    }
        
    /**
     * Generate a java binding class for the named module.
     * @param moduleName
     * @param targetPackage
     * @param publicEntities
     * @param testOnly
     */
    private void generateJavaBinding (
            ModuleName moduleName, 
            String targetPackage, 
            boolean publicEntities,
            boolean testOnly) {
        
        ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(moduleName);
        if (typeInfo == null) {
            iceLogger.log(Level.INFO, "The module " + moduleName + " does not exist in the current workspace.");
            return;
        } 
        
        if (testOnly) {
            String fullClassName = JavaBindingGenerator.getPackageName(targetPackage, moduleName) + '.' + JavaBindingGenerator.getClassName(moduleName, publicEntities);
            try {
                if(JavaBindingGenerator.checkJavaBindingClass(typeInfo, targetPackage, publicEntities, false)) {
                    iceLogger.log(Level.INFO, "The binding class " + fullClassName + (publicEntities ? " for the public members of " : " for the non-public members of ") + moduleName + " is up to date.");
                } else {
                    iceLogger.log(Level.INFO, "WARNING:");
                    iceLogger.log(Level.INFO, "The binding class " + fullClassName + (publicEntities ? " for the public members of " : " for the non-public members of ") + moduleName + " is not up to date.");
                }
            } catch (UnableToResolveForeignEntityException e) {
                iceLogger.log(Level.INFO, "Unable to check the java binding file for module " + moduleName + ":");
                iceLogger.log(Level.INFO, "  " + e.getCompilerMessage());
            }
            return;
        }
        
        // Generate the source for the Java binding class.
        final String text;
        try {
            text = JavaBindingGenerator.getJavaBinding(typeInfo, targetPackage, publicEntities);
            
        } catch (UnableToResolveForeignEntityException e) {
            iceLogger.log(Level.INFO, "Unable to generate the java binding file for module " + moduleName + ":");
            iceLogger.log(Level.INFO, "  " + e.getCompilerMessage());
            return;
        }
        
        // Need to find where to write the file.
        String location = getModulePath(moduleName);
        if (location == null) {
            iceLogger.log(Level.INFO, "Unable to determine location for java binding file for module " + moduleName + ".");
            return;
        }
        
        // Generally this location will follow the pattern of project_root\src\cal\module.cal or
        // project_root\test\cal\module.cal.  However, if the Eclipse project has its output
        // set to a different directory, such as 'bin', the CAL source file will be in
        // project_root\bin\module.cal.
        
        File root = null;
        while (root == null) {
            if (location.indexOf(File.separator + "src" + File.separator) != -1) {
                root = new File(location.substring(0,location.indexOf(File.separator + "src" + File.separator) + 5));
            } else 
            if (location.indexOf(File.separator + "test" + File.separator) != -1) {
                root = new File(location.substring(0,location.indexOf(File.separator + "test" + File.separator) + 6));
            } else
            if (location.indexOf(File.separator + "bin" + File.separator) != -1) {
                root = new File(location.substring(0,location.indexOf(File.separator + "bin" + File.separator)));
                // This should be the project root.  Try to find the location of the CAL
                // source file.
                File[] files = root.listFiles();
                root = null;
                
                for (int i = 0; i < files.length; ++i) {
                    File f = files[i];
                    if (f.isDirectory() && !f.getName().equals("bin")) {
                        root = findFile(f, moduleName + ".cal");
                        if (root != null) {
                            break;
                        }
                    }
                }
                
                if (root != null) {
                    // At this point root is the location of the CAL source other than the bin directory.
                    // Reset the location string and keep going.
                    location = root.getAbsolutePath();
                    root = null;
                } else {
                    // We couldn't find a location other than the one in the bin directory.
                    // Exit the loop and try moving relative to the CAL source location.
                    root = null;
                    break;
                }
            } else {
                break;
            }
        }
        
        if (root == null) {
            // Go up one directory from the CAL source location
            // and then add on org\openquark\cal\module
            File f = new File (location);
            f = f.getParentFile();
            if (f.getName().equals("CAL")) {
                f = f.getParentFile();
            }
            root = f;
        }    
        
        String actualPackage;
        if (targetPackage != null) {
            actualPackage = JavaBindingGenerator.getPackageName(targetPackage.trim(), typeInfo.getModuleName());
        } else {
            actualPackage = JavaBindingGenerator.getPackageName(targetPackage, typeInfo.getModuleName());
        }
        
        String packageDirs = actualPackage.replace('.', File.separatorChar);
        
        File bindingLocation = new File (root, packageDirs);
        if (!bindingLocation.exists()) {
            // Try to create the containing directory.
            if (!bindingLocation.mkdirs()) {
                iceLogger.log(Level.INFO, "Unable to create necessary directory " + bindingLocation.getPath() + ".");
                return;
            } else {
                iceLogger.log(Level.INFO, "Directory " + bindingLocation.getPath() + " created.");
            }
        }
        
        // We differentiate the generated classes for public vs. private module elements.
        bindingLocation = new File(bindingLocation, JavaBindingGenerator.getClassName(typeInfo.getModuleName(), publicEntities) + ".java");
        
        if (bindingLocation.exists()) {
            try {
                // Check to see if there is a difference between the new source
                // and the existing source.
                FileInputStream fis = new FileInputStream(bindingLocation);
                boolean changed = sourceChanged(text, fis);
                fis.close();
                if (!changed) {
                    iceLogger.log(Level.INFO, "There is no change in the Java binding class for module " + moduleName + ".");
                    return;
                } else {
                    iceLogger.log(Level.INFO, "The Java binding class for module " + moduleName + " has changed since the last time it was updated.");
                }
            } catch (FileNotFoundException e) {
                iceLogger.log(Level.INFO, "Unable to access file " + bindingLocation.getPath() + ".  " + e.getLocalizedMessage());
                return;
            } catch (IOException e) {
                iceLogger.log(Level.INFO, "Unable to access file " + bindingLocation.getPath() + ".  " + e.getLocalizedMessage());
                return;
            }
        }
        
        try {
            // Try to open an output stream
            FileOutputStream fos = new FileOutputStream (bindingLocation);
            byte[] bytes = TextEncodingUtilities.getUTF8Bytes(text);
            fos.write(bytes, 0, bytes.length);
            fos.close();
            iceLogger.log(Level.INFO, "File " + bindingLocation.getPath() + " succesfully updated.");
        } catch (FileNotFoundException e) {
            iceLogger.log(Level.INFO, "Error writing file " + bindingLocation.getPath() + ".  " + e.getLocalizedMessage());
        } catch (IOException e) {
            iceLogger.log(Level.INFO, "Error writing file " + bindingLocation.getPath() + ".  " + e.getLocalizedMessage());
        }
    }
    
    /**
     * Recursively search for the named file/directory under the
     * given root.
     * @param root
     * @param name
     * @return a File if a match is found, null otherwise.
     */
    protected File findFile(File root, String name) {
        if (!root.isDirectory()) {
            return null;
        }
        
        File[] files = root.listFiles();
        for (int i = 0; i < files.length; ++i) {
            File f = files[i];
            if (f.getName().equals(name)) {
                return f;
            }
            
            if (f.isDirectory()) {
                f = findFile(f, name);
                if (f != null) {
                    return f;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Determine whether the source code for a given file has changed in a meaningful way.
     * @param newSource the new source code for the give file.
     * @param sourceInputStream the source file.
     * @return boolean whether the source code has changed.
     */
    protected boolean sourceChanged(String newSource, InputStream sourceInputStream) {
        try {
            Reader frOld = TextEncodingUtilities.makeUTF8Reader(sourceInputStream);

            BufferedReader brOld = new BufferedReader (frOld);
            BufferedReader brNew = new BufferedReader (new StringReader (newSource));
            boolean difference = false;
            while (!difference) {
                String sOld = nextLineOfInterest(brOld);
                String sNew = nextLineOfInterest(brNew);
                
                if ((sOld == null && sNew != null) || (sNew == null && sOld != null)) {
                    return true;
                }
                
                if (sOld == null) {
                    break;
                }
                
                if (!sOld.equals (sNew)) {
                    return true;
                }
            }
            brOld.close();
            frOld.close();
        
        } catch (IOException e) {
            return true;
        
        } finally {
            if (sourceInputStream != null) {
                try {
                    sourceInputStream.close();
                } catch (IOException e) {
                }
            }
        }
        
        return false;
    }
    
    /**
     * Helper method for sourceChanged().
     *   Get the next line which isn't a comment.
     * @param br the reader from which to get the line.
     * @return the next line, or null if there is no next line.
     * @throws IOException
     */
    private String nextLineOfInterest (BufferedReader br) throws IOException {
        String line = null;
        boolean readAgain = true;
        boolean inComment = false;
        
        while (readAgain) {
            readAgain = false;
            line = br.readLine ();
            if (line == null) {
                return null;
            }
            
            line = line.trim();
            if (inComment) {
                readAgain = true;
                if (line.endsWith("*/")) {
                    inComment = false;
                }
            } else
            if (line.startsWith("/*") && !line.startsWith("/**")) {
                // We do want to compare CALDoc comments.
                inComment = true;
                readAgain = true;
            } else
            if (line.equals("")) {
                readAgain = true;
            } else
            if (line.startsWith("//")) {
                readAgain = true;
            }
        }
        
        return line;

    } 
    
    /**
     * @param moduleName
     * @return that describes the location of the module.  This will normally
     *          be an absolute path to the file in the filesystem.
     */
    protected String getModulePath(ModuleName moduleName) {
        
        CALFeatureName moduleFeatureName = CALFeatureName.getModuleFeatureName(moduleName);
        ResourceName moduleResourceName = new ResourceName(moduleFeatureName);
        
        ResourceStore sourceStore = getWorkspaceManager().getWorkspace().getSourceManager(moduleName).getResourceStore();
        
        // If the source store doesn't know about this module, then we don't know how to find its file
        if (!sourceStore.hasFeature(moduleResourceName)) {
            return null;
        }
        
        if (sourceStore instanceof ResourcePathStore) {
            ResourcePathStore sourcePathStore = (ResourcePathStore)sourceStore;
            
            if (sourcePathStore instanceof ResourceNullaryStore) {
                File currentFile = NullaryEnvironment.getNullaryEnvironment().getFile(sourcePathStore.getResourcePath(moduleResourceName), false);
                if(currentFile != null) {
                    return currentFile.getAbsolutePath();
                }
            }
        
        }
        
        return null;
    }
    
    
    protected void addCommand (ICECommand info) {
        for (final String commandText : info.getCommandText()) {
            // Check that there isn't an already existing command which
            // uses the same command text.
            // This is most likely to happen in situations where ICE has
            // been extended by sub-classing and the sub-class is adding
            // its own commands.
            ICECommand existingCommand = commandToCommandInfo.get(commandText);
            if (existingCommand != null) {
                // Generate warning message that a newer command is overriding an existing one.
                getIceLogger().log (Level.WARNING, "WARNING: Duplicate command added.");
                getIceLogger().log (Level.WARNING, "Command :" + commandText + " is overriding an already existing version of the same command.");
            }
            
            commandToCommandInfo.put(commandText, info);
            commands.add(info);
        }
    }
    
    /**
     * Loop to fetch user input and take appropriate action.
     */
    public void run () {
        
        initialize (workspaceManager);
        
        busy = false;
        String nextCommand = "";

        if (scriptLocation.length () > 0) {
            nextCommand = ":script " + scriptLocation;
        }

        for (;;) {
            try {
                if (nextCommand.length() == 0) {
                    
                    // Emit the prompt
                    outputStream.println(" ");
                    ExecutionContextImpl ec = getExecutionContext();
                    Map<Thread, SuspensionState> suspensions = ec.getThreadSuspensions();
                    
                    if (!suspensions.isEmpty()) {
                        Thread currentThread = getDebugController().getCurrentSuspendedThread();
                        SuspensionState suspension = suspensions.get(currentThread);
                        if (suspension == null) {
                            // the current thread is no longer suspended, so choose another one
                            final Map.Entry<Thread, SuspensionState> next = suspensions.entrySet().iterator().next();
                            currentThread = next.getKey();
                            suspension = next.getValue();
                            getDebugController().setCurrentSuspendedThread(currentThread);
                        }
                        
                        if (suspensions.size() > 1) {
                            // more than one thread suspended, so also display thread name
                            outputStream.print("(" + currentThread.getName() + ") " + suspension.getFunctionName() + "#>");
                        } else {
                            outputStream.print(suspension.getFunctionName() + "#>");
                        }
                    } else {
                        outputStream.print(targetModule + ">");
                    }
    
                    // Get the command
                    try {
                        nextCommand = inBuff.readLine();
                        if (nextCommand == null) {
                            outputStream.println("Input stream closed. Terminating ICE");
                            break;
                        }
                    } catch (java.io.IOException e) {
                        nextCommand = "";
                    }
                }
                
                busy = true;
                nextCommand = nextCommand.trim();
                //System.out.println (this.iceLoggerNamespace + " -> command = " + command);
    
                String tempC = nextCommand;
                nextCommand = "";
                executeCommand (tempC, true);
            } catch (QuitException e) {
                busy = false;
                break;
            } catch (Exception e) {
                outputStream.println("Exception encountered: " + e + ": " + e.getMessage());
            } catch (Error e) {
                outputStream.println("Error encountered: " + e + ": " + e.getMessage());
                outputStream.println("Terminating ICE");
                break;
            } finally {
                busy = false;
            }
        }   
        
        runThread = null;
    }

    /**
     * Load a workspace.
     * @param newWorkspaceLocation
     */
    private void command_loadWorkspace (String newWorkspaceLocation) {
        // Load a different workspace.
        setWorkspaceLocation(newWorkspaceLocation);
        createWorkspaceManager();
        compileWorkspace (true, false, false);
        if (targetModule == null || !getWorkspaceManager().hasModuleInProgram(targetModule)) {
            changeToPreferredWorkingModule();
        }
    }
    
    /**
     * Show the "discoverable" workspaces in the current environment (e.g. the nullary environment).
     * @param info
     */
    private void command_showDiscoverableWorkspaces(String info) {
        List<String> cwsNames = new ArrayList<String>();
        
        CALWorkspace workspace = workspaceManager.getWorkspace();
        
        WorkspaceDeclarationManager workspaceDeclarationManager = workspace.getWorkspaceDeclarationManager();
        
        if (workspaceDeclarationManager != null) {
            ResourceStore resourceStore = workspaceDeclarationManager.getResourceStore();
            
            
            for (Iterator<WorkspaceResource> it = resourceStore.getResourceIterator(); it.hasNext(); ) {
                final WorkspaceResource decl = it.next();
                cwsNames.add(decl.getIdentifier().getFeatureName().getName());
            }
        }
        
        Collections.sort(cwsNames);
        
        for (int i = 0, n = cwsNames.size(); i < n; i++) {
            iceLogger.log(Level.INFO, cwsNames.get(i));
        }
    }

    /**
     * Load a "discoverable" workspace from the current environment (e.g. the nullary environment).
     * @param info
     */
    private void command_loadDiscoverableWorkspace(String info) {
        
        String workspaceName = info.trim();
        
        WorkspaceDeclaration.StreamProvider newStreamProvider = null;
        
        CALWorkspace workspace = workspaceManager.getWorkspace();
        
        final String workspaceLocation = workspace.getWorkspaceLocationString();
        
        WorkspaceDeclarationManager workspaceDeclarationManager = workspace.getWorkspaceDeclarationManager();
        
        if (workspaceDeclarationManager != null) {
            ResourceStore resourceStore = workspaceDeclarationManager.getResourceStore();
            
            for (Iterator<WorkspaceResource> it = resourceStore.getResourceIterator(); it.hasNext(); ) {
                final WorkspaceResource decl = it.next();
                
                if (workspaceName.equals(decl.getIdentifier().getFeatureName().getName())) {
                    newStreamProvider = new WorkspaceDeclaration.StreamProvider() {
                        
                        public String getName() {
                            return decl.getIdentifier().getFeatureName().getName();
                        }
                        
                        public String getLocation() {
                            return workspaceLocation;
                        }
                        
                        public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
                            return decl.getInputStream(status);
                        }
                        
                        public String getDebugInfo(VaultRegistry vaultRegistry) {
                            return decl.getDebugInfo();
                        }

                        @Override
                        public String toString() {
                            return getName();
                        }
                    };
                }
            }
        }
        
        if (newStreamProvider == null) {
            iceLogger.log(Level.INFO, "Cannot find a workspace file named: " + workspaceName);
        } else {
            streamProvider = newStreamProvider;
            createWorkspaceManager();
            compileWorkspace (true, false, false);
            if (targetModule == null || !getWorkspaceManager().hasModuleInProgram(targetModule)) {
                changeToPreferredWorkingModule();
            }
        }
    }
    
    /**
     * Discard any cached CAF results in the program.
     * @param moduleToResetString
     */
    private void command_resetCachedResults (String moduleToResetString) {
        if (moduleToResetString.length() == 0) {
            // Reset cached results in all modules.
            resetCachedResults (); 
        } else {
            ModuleName moduleToReset = resolveModuleNameInProgram(moduleToResetString, true);
            if (moduleToReset != null) {
                resetCachedResults(moduleToReset);
            }
        } 
    }
    
    /**
     * Discard the machine state.
     * @param moduleToResetString
     */
    private void command_resetMachineState(String moduleToResetString) {
        if (moduleToResetString.length() == 0) {
            // Reset cached results in all modules.
            resetMachineState(); 
        } else {
            ModuleName moduleToReset = resolveModuleNameInProgram(moduleToResetString, true);
            if (moduleToReset != null) {
                resetMachineState(moduleToReset);
            }
        } 
    }
    
    /**
     * Display the help message.
     * @param info
     */
    private void command_help (String info) {

        if (info == null || info.trim().length() == 0) {
            showICECommands(null);
        }
        else {
            List<CommandType> matches = getPartialNameMatch(info);
            int numMatches = matches.size();

            // ambiguous if there is more than 1 match to input
            if (numMatches > 1){
                //preparing the string of possible choices
                StringBuilder ambiguousNames = new StringBuilder();
                for (int i = 0; i < numMatches ; i++){
                    if (i != 0) {
                        ambiguousNames.append(", ");
                    }
                    String matchedName = matches.get(i).getName();
                    ambiguousNames.append(matchedName);
                }
                iceLogger.log(Level.INFO, "Possible help topics: " + ambiguousNames.toString());   

            } else if (numMatches == 1) {
                showICECommands(matches.get(0));

            } else if (numMatches == 0) {
                iceLogger.log(Level.INFO, "Unable to find help for: " + info);  
            }
        }
    }

    /** 
     * helper method to compare the user input with prefixes of all command names   
     * @param info argument of the help command
     * @return CommandTypes with names that match info
     */
    private List<CommandType> getPartialNameMatch (String info) {
        // to store the matching types
        List<CommandType> matches = new ArrayList<CommandType>();
        String upperCaseInfo = info.toUpperCase();
      
        //go through all command names
        for (final CommandType command : commandTypesList) {
            String commandName = command.getName().toUpperCase();
            
            if (commandName.startsWith(upperCaseInfo)){
                matches.add(command);
            }
        }
        return matches;
    }


    /**
     * Rename gems, type classes, modules
     * @param info
     */
    private void command_rename(String info) {
        String[] args = info.split(" ");
        
        // If less than 2 argument given, cant rename
        if (args.length < 2) {
            return;
        }
        
        final Category category;
        
        String oldNameString;
        QualifiedName oldName;
        
        String newNameString;
        QualifiedName newName;
        
        if(args.length >= 3) {
            String categoryString = args[0];
            if(categoryString.equals("function") || categoryString.equals("method")) {
                category = Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD; 
            } else if(categoryString.equals("dataCons") || categoryString.equals("dataConstructor")) {
                category = Category.DATA_CONSTRUCTOR;
            } else if(categoryString.equals("typeCons") || categoryString.equals("typeConstructor")) {
                category = Category.TYPE_CONSTRUCTOR;
            } else if(categoryString.equals("typeClass")) {
                category = Category.TYPE_CLASS;
            } else if(categoryString.equals("module")) {
                category = Category.MODULE_NAME;
            } else {
                iceLogger.log(Level.INFO, "Error: Unrecognized category '" + categoryString + "'");
                return;
            }
        
            oldNameString = args[1];
            newNameString = args[2];
            if(category == Category.MODULE_NAME) {
                
                ModuleName oldNameAsModuleName = ModuleName.maybeMake(oldNameString);
                if(oldNameAsModuleName == null) {
                    iceLogger.log(Level.INFO, "Error: " + oldNameString + " is not a valid module name");
                    return;
                }
                
                oldName = QualifiedName.make(oldNameAsModuleName, Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
            
            } else if(QualifiedName.isValidCompoundName(oldNameString)) {
                oldName = QualifiedName.makeFromCompoundName(oldNameString);
            
            } else {
                iceLogger.log(Level.INFO, "Error: " + oldNameString + " is not a valid qualified name");
                return;
            }
        
        } else {
            
            oldNameString = args[0];
            newNameString = args[1];

            ModuleName oldNameAsModuleName = ModuleName.maybeMake(oldNameString);
            // the null check for oldNameAsModuleName comes a bit later (a few lines down)
            
            QualifiedName oldNameAsQualifiedName;
            if (QualifiedName.isValidCompoundName(oldNameString)) {
                oldNameAsQualifiedName = QualifiedName.makeFromCompoundName(oldNameString);
            } else {
                oldNameAsQualifiedName = null;
            }
            
            List<Category> entityCategories = new ArrayList<Category>();
            if (oldNameAsModuleName != null && getWorkspaceManager().getModuleTypeInfo(oldNameAsModuleName) != null) {
                entityCategories.add(Category.MODULE_NAME);
            }
            
            if (oldNameAsQualifiedName != null) {
                ModuleName moduleName = oldNameAsQualifiedName.getModuleName();
                String unqualifiedName = oldNameAsQualifiedName.getUnqualifiedName();
                
                ModuleTypeInfo moduleTypeInfo = getWorkspaceManager().getModuleTypeInfo(moduleName);
                if(moduleTypeInfo != null) {

                    if(moduleTypeInfo.getFunctionOrClassMethod(unqualifiedName) != null) {
                        entityCategories.add(Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD); 
                    }

                    if(moduleTypeInfo.getDataConstructor(unqualifiedName) != null) {
                        entityCategories.add(Category.DATA_CONSTRUCTOR);
                    }

                    if(moduleTypeInfo.getTypeConstructor(unqualifiedName) != null) {
                        entityCategories.add(Category.TYPE_CONSTRUCTOR);
                    }

                    if(moduleTypeInfo.getTypeClass(unqualifiedName) != null) {
                        entityCategories.add(Category.TYPE_CLASS);
                    }
                }
            }
            
            if(entityCategories.size() == 0) {
                iceLogger.log(Level.INFO, "Error: " + oldNameString + " does not exist");
                return;
            }

            if(entityCategories.size() > 1) {
                iceLogger.log(Level.INFO, "Error: " + oldNameString + " is ambiguous; please specify a context");
                return;
            }
            
            category = entityCategories.get(0);
            
            if (category == Category.MODULE_NAME) {
                oldName = QualifiedName.make(oldNameAsModuleName, Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
            } else {
                oldName = oldNameAsQualifiedName;
            }
        }
        
        if (category == Category.MODULE_NAME) {
            ModuleName newNameAsModuleName = ModuleName.make(newNameString);
            newName = QualifiedName.make(newNameAsModuleName, Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
        } else if(QualifiedName.isValidCompoundName(newNameString)) {
            newName = QualifiedName.makeFromCompoundName(newNameString);
        } else {
            newName = QualifiedName.make(oldName.getModuleName(), newNameString);
            newNameString = newName.getQualifiedName();
        }

        // Error checks
        if(!entityExists(oldName, category)) {
            iceLogger.log(Level.INFO, "Error: " + oldNameString + " does not exist");
            return;
        }
        
        if(entityExists(newName, category)) {
            iceLogger.log(Level.INFO, "Error: " + newNameString + " already exists");
            return;
        }        
        
        if((Character.isUpperCase(oldName.getUnqualifiedName().charAt(0)) && !Character.isUpperCase(newName.getUnqualifiedName().charAt(0))) ||
           (!Character.isUpperCase(oldName.getUnqualifiedName().charAt(0)) && Character.isUpperCase(newName.getUnqualifiedName().charAt(0)))) {
               
            iceLogger.log(Level.INFO, "Error: Old and new names must be of the same type (ie, if one starts with an uppercase letter, so must the other)");
            return;
        }
        
        if(category != Category.MODULE_NAME && !oldName.getModuleName().equals(newName.getModuleName())) {
            iceLogger.log(Level.INFO, "Error: Renamed entities must remain in the same module");
            return;
        }
        
        // Okay, the basics check out.
        System.out.print("Renaming " + oldNameString + " to " + newNameString + "...");
        CompilerMessageLogger messageLogger = new MessageLogger();
        Refactorer refactorer = new Refactorer.Rename(getWorkspaceManager().getWorkspace().asModuleContainer(), getWorkspaceManager().getTypeChecker(), oldName, newName, category);

        refactorer.setStatusListener(new Refactorer.StatusListener() {
            public void willUseResource(ModuleSourceDefinition resource) {
                System.out.print(".");
            }
            public void doneUsingResource(ModuleSourceDefinition resource) {
            }
        });

        refactorer.calculateModifications(messageLogger);

        if(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
            System.out.println("");
            dumpCompilerMessages(messageLogger);
            return;
        }

        refactorer.apply(messageLogger);
        System.out.println("");

        if(messageLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
            dumpCompilerMessages(messageLogger);
            return;
        }

        if(!compileWorkspace(false, true, false)) {
            System.out.print("Compile failed, rolling back changes...");
            refactorer.undo(messageLogger);
            System.out.println("");
            return;
        }

        if(category == Category.MODULE_NAME) {
            ModuleName oldNameAsModuleName = ModuleName.maybeMake(oldNameString);
            if(oldNameAsModuleName == null) {
                iceLogger.log(Level.INFO, "Invalid module name '" + oldNameString + "'");
                return;
            }
            
            getWorkspaceManager().removeModule(oldNameAsModuleName, false, new Status("removing old module"));
        }
            
        iceLogger.log(Level.INFO, "done");
    }
    
    /**
     * @param qualifiedName QualifiedName of an entity (if the entity is a module, it should be of the form "ModuleName.ModuleName")
     * @param category Category of the entity
     * @return true if the specified entity exists, or false otherwise.
     */
    private boolean entityExists(QualifiedName qualifiedName, Category category) {
        
        if(category == Category.MODULE_NAME) {
            return getWorkspaceManager().hasModuleInProgram(qualifiedName.getModuleName());
        }

        ModuleTypeInfo moduleTypeInfo = getWorkspaceManager().getModuleTypeInfo(qualifiedName.getModuleName());
        if(category == Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            return (moduleTypeInfo.getFunctionOrClassMethod(qualifiedName.getUnqualifiedName()) != null);

        } else if(category == Category.DATA_CONSTRUCTOR) {
            return (moduleTypeInfo.getDataConstructor(qualifiedName.getUnqualifiedName()) != null);

        } else if(category == Category.TYPE_CONSTRUCTOR) {
            return (moduleTypeInfo.getTypeConstructor(qualifiedName.getUnqualifiedName()) != null);
            
        } else if(category == Category.TYPE_CLASS) {
            return (moduleTypeInfo.getDataConstructor(qualifiedName.getUnqualifiedName()) != null);
        
        } else {
            throw new IllegalArgumentException("invalid category " + category);
        }
    }
    
    /** this implements the pretty printing command*/
    private void command_prettyPrint(String info) {
        String[] args = info.split(" ");

        int argi = 0, startLine = -1, endLine = -1;

        // get the module name if specified or use current module
        final ModuleName moduleName;
        if (info.length() > 0 && args.length > 0
                && ModuleName.maybeMake(args[0]) != null) {
            moduleName = resolveModuleNameInWorkspace(args[0], true);
            if (moduleName == null) {
                return;
            }
            argi++;
        } else {
            moduleName = targetModule;
        }

        ModuleTypeInfo moduleTypeInfo = getWorkspaceManager().getModuleTypeInfo(moduleName);      

        
        //are optional function names present
        final HashSet<String> functionNames = new HashSet<String>();
        while (args.length > argi && LanguageInfo.isValidFunctionName(args[argi])) {

            //make sure the function exists
            if (moduleTypeInfo.getFunction(args[argi]) == null) {
                iceLogger
                .log(Level.INFO,
                        "Invalid arguments:  function " + args[argi]+" does not exist in module " + moduleName);
                return;
            }
                  
            functionNames.add(args[argi++]);
        }
        
        // is the optional line range present
        if (args.length == argi + 2) {
            try {
                startLine = Integer.parseInt(args[argi++]);
                endLine = Integer.parseInt(args[argi++]);
            } catch (NumberFormatException ex) {
                iceLogger
                        .log(Level.INFO,
                                "Invalid arguments:  line number is not a valid integer");
                return;
            }

        }

        // are there some unused arguments...
        if (info.length() > 0 && args.length != argi) {
            iceLogger.log(Level.INFO,
                    "Invalid arguments:  :pp [module] [function [function ...]] [start end]");
            return;

        }

        iceLogger.log(Level.INFO, "Pretty printing " + moduleName + "...");

        Refactorer refactorer = new Refactorer.PrettyPrint(
                getWorkspaceManager().getWorkspace().asModuleContainer(),
                moduleName, startLine, 0, endLine, 0, functionNames);

        CompilerMessageLogger messageLogger = new MessageLogger();

        refactorer.calculateModifications(messageLogger);

        if (messageLogger.getNErrors() == 0) {
            // apply the changes, unless we encountered errors while calculating them
            refactorer.apply(messageLogger);
        }

        if (messageLogger.getNErrors() > 0) {
            dumpCompilerMessages(messageLogger);
            return;
        }

        iceLogger.log(Level.INFO, "Pretty printing done.");
    }
    
    /**
     * Perform a simple (ie, non-renaming) factoring
     * @param info
     */
    private void command_refactor(String info) {
        Refactorer refactorer = null;
        boolean noGrouping = false;
        
        String[] args = info.split(" ");
        if(args[0].equals("import") || args[0].equals("imports")) {
            ModuleName moduleName = targetModule;
            if(args.length >= 3) {
                moduleName = ModuleName.maybeMake(args[1]);
                if(moduleName == null) {
                    iceLogger.log(Level.INFO, "Invalid module name '" + args[1] + "'");
                    return;
                }
                
                if(args[2].equals("nogroup")) {
                    noGrouping = true;
                
                } else {
                    iceLogger.log(Level.INFO, "Unrecognized option '" + args[2]);
                    return;
                }
                
            } else if(args.length >= 2) {
                moduleName = ModuleName.maybeMake(args[1]);
                if (moduleName == null) {
                    if(args[1].equals("nogroup")){
                        moduleName = targetModule;
                        noGrouping = true;
                    } else {
                        iceLogger.log(Level.INFO, "Unrecognized option '" + args[1]);
                        return;
                    }
                }
            }
            
            moduleName = resolveModuleNameInWorkspace(moduleName.toSourceText(), true);
            if (moduleName == null) {
                return;
            }
            
            refactorer = new Refactorer.CleanImports(getWorkspaceManager().getWorkspace().asModuleContainer(), moduleName, noGrouping);
            iceLogger.log(Level.INFO, "Cleaning imports in module " + moduleName + "...");
            
        } else if(args[0].equals("typeDecls")) {
            final ModuleName moduleName;
            if(args.length > 1) {
                moduleName = resolveModuleNameInWorkspace(args[1], true);
                if (moduleName == null) {
                    return;
                }
            } else {
                moduleName = targetModule;
            }
            
            refactorer = new Refactorer.InsertTypeDeclarations(getWorkspaceManager().getWorkspace().asModuleContainer(), moduleName, -1, -1, -1, -1);
            iceLogger.log(Level.INFO, "Adding type declarations to module " + moduleName + "...");
            
        } else {
            iceLogger.log(Level.INFO, "unrecognized refactoring type '" + args[0] + "'");
            return;
        }
        
        CompilerMessageLogger messageLogger = new MessageLogger();
        
        refactorer.calculateModifications(messageLogger);

        if (messageLogger.getNErrors() == 0) {
            // apply the changes, unless we encountered errors while calculating them
            refactorer.apply(messageLogger);
        }
            
        if (messageLogger.getNErrors() > 0) {
            dumpCompilerMessages(messageLogger);
            return;
        }

        // Output statistics, if any
        if(args[0].equals("typeDecls")) {
            Refactorer.InsertTypeDeclarations typeDeclInserter = (Refactorer.InsertTypeDeclarations)refactorer;
            int topLevelWithConstraints = typeDeclInserter.getTopLevelTypeDeclarationsAddedWithClassConstraints();
            int topLevel = topLevelWithConstraints + typeDeclInserter.getTopLevelTypeDeclarationsAddedWithoutClassConstraints();
            int localWithConstraints = typeDeclInserter.getLocalTypeDeclarationsAddedWithClassConstraints();
            int local = localWithConstraints + typeDeclInserter.getLocalTypeDeclarationsAddedWithoutClassConstraints();
            iceLogger.log(Level.INFO, "    " + topLevel + " type declarations added to top-level functions (" + topLevelWithConstraints + " with type-class constraints)"); 
            iceLogger.log(Level.INFO, "    " + local + " type declarations added to local functions (" + localWithConstraints + " with type-class constraints)");
            iceLogger.log(Level.INFO, "    " + typeDeclInserter.getLocalTypeDeclarationsNotAdded() + " local functions could not have type declarations added");
            iceLogger.log(Level.INFO, "    " + typeDeclInserter.getImportsAdded() + " import declarations added");
            iceLogger.log(Level.INFO, "    " + typeDeclInserter.getTypeDeclarationsNotAddedDueToPotentialImportConflict() + " type declarations could not be added because they require additional imports that potentially introduce name resolution conflicts");
            iceLogger.log(Level.INFO, "    -- imports not added: " + typeDeclInserter.getImportsNotAddedDueToPotentialConfict());
        }

        iceLogger.log(Level.INFO, "done refactoring.");
    }
    
    /**
     * Exit ICE
     * @param info
     * @throws QuitException
     */
    private void command_quit (String info) throws QuitException {
        // We need to tell the CAL run thread we want it to stop and
        // let it terminate gracefully.
        command_terminateExecution("");

        iceLogger.log(Level.FINER, "Exiting");
        throw new QuitException();
    }
    
    /**
     * Show the version of CAL.
     * @param info
     */
    private void command_version (String info) {
        iceLogger.log(Level.INFO, "CAL version " + Version.CURRENT);        
    }
    
    /**
     * Show the version of CAL.
     * @param info
     */
    private void command_showWorkspaceDebugInfo (String info) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                iceLogger.log(Level.INFO, workspaceManager.getDebugInfo());
            }
        } else {
            iceLogger.log(Level.INFO, "Workspace not initialized.");   
        }
    }
    
    /**
     * Reload the current workspace.  This forces all 
     * modules to be recompiled and new code generated for each module.
     * @param info
     */
    private void command_reloadWorkspace (String info) {
        compileWorkspace (false, false, true);        
    }
    
    /**
     * Recompile any changed modules and generate code
     * for any changed entities within the modules.
     * @param info
     */
    private void command_recompileWorkspace (String info) {
        compileWorkspace (false, true, false);
    }
    
    /**
     * Recompile all modules and generate code
     * for any changed entities within the modules.
     * @param info
     */
    private void command_recompileAll (String info) {
        compileWorkspace(false, false, false);
    }
    
    /**
     * Resolves the given qualified name in the context of the current program. If the given name cannot be unambiguously resolved, null is returned.
     * @param qualifiedName the qualified name to resolve.
     * @return the corresponding fully qualified name, or null if the given name cannot be unambiguously resolved.
     */
    private QualifiedName resolveQualifiedNameInProgram(QualifiedName qualifiedName) {
        ModuleName resolvedModuleName = resolveModuleNameInProgram(qualifiedName.getModuleName().toSourceText(), true);
        if (resolvedModuleName == null) {
            return null;
        } else {
            return QualifiedName.make(resolvedModuleName, qualifiedName.getUnqualifiedName());
        }
    }
    
    /**
     * Resolves the given module name in the context of the current program. If the given name cannot be unambiguously resolved, null is returned.
     * @param moduleName the module name to resolve.
     * @param logWarningIfNotResolvable whether to log warnings if the name is not resolvable.
     * @return the corresponding fully qualified module name, or null if the given name cannot be unambiguously resolved.
     */
    protected ModuleName resolveModuleNameInProgram(String moduleName, boolean logWarningIfNotResolvable) {
        
        // We check the program rather than the workspace manager here since the
        // workspace manager will report that it contains modules that failed to compile.
        
        ModuleName[] moduleNamesInProgram = getWorkspaceManager().getModuleNamesInProgram();
        return resolveModuleName(moduleName, moduleNamesInProgram, logWarningIfNotResolvable, ":lm");
    }
    
    /**
     * Resolves the given module name in the context of the current workspace. If the given name cannot be unambiguously resolved, null is returned.
     * @param moduleName the module name to resolve.
     * @param logWarningIfNotResolvable whether to log warnings if the name is not resolvable.
     * @return the corresponding fully qualified module name, or null if the given name cannot be unambiguously resolved.
     */
    private ModuleName resolveModuleNameInWorkspace(String moduleName, boolean logWarningIfNotResolvable) {
        
        ModuleName[] moduleNamesInWorkspace = getWorkspaceManager().getWorkspace().getModuleNames();
        return resolveModuleName(moduleName, moduleNamesInWorkspace, logWarningIfNotResolvable, ":lm");
    }

    /**
     * Resolves the given module name in the context of the given set of visible module names.
     * If the given name cannot be unambiguously resolved, null is returned.
     * @param moduleNameString the module name to resolve.
     * @param visibleModuleNames the names of visible modules.
     * @param logWarningIfNotResolvable whether to log warnings if the name is not resolvable.
     * @param listModuleCommand the command to suggest to the user to display a list of appropriate modules.
     * @return the corresponding fully qualified module name, or null if the given name cannot be unambiguously resolved.
     */
    private ModuleName resolveModuleName(String moduleNameString, ModuleName[] visibleModuleNames, boolean logWarningIfNotResolvable, String listModuleCommand) {
        
        ModuleNameResolver moduleNameResolver = ModuleNameResolver.make(new HashSet<ModuleName>(Arrays.asList(visibleModuleNames)));
        return resolveModuleName(moduleNameString, moduleNameResolver, logWarningIfNotResolvable, listModuleCommand);
    }

    /**
     * Resolves the given module name in the context of the given module name resolver.
     * If the given name cannot be unambiguously resolved, null is returned.
     * @param moduleNameString the module name to resolve.
     * @param moduleNameResolver the module name resolver initialized with the names of visible modules.
     * @param logWarningIfNotResolvable whether to log warnings if the name is not resolvable.
     * @param listModuleCommand the command to suggest to the user to display a list of appropriate modules.
     * @return the corresponding fully qualified module name, or null if the given name cannot be unambiguously resolved.
     */
    private ModuleName resolveModuleName(String moduleNameString, ModuleNameResolver moduleNameResolver, boolean logWarningIfNotResolvable, String listModuleCommand) {

        final ModuleName result;
        
        ModuleName moduleName = ModuleName.maybeMake(moduleNameString);

        if (moduleName == null) {
            iceLogger.log(Level.INFO, moduleNameString + " is not a valid module.  Use " + listModuleCommand + " to display a list of available modules.");
            result = null;
            
        } else {
            ModuleNameResolver.ResolutionResult resolution = moduleNameResolver.resolve(moduleName);

            if (resolution.isKnownUnambiguous()) {
                // the module name can be resolved, so return it.
                result = resolution.getResolvedModuleName();

            } else {
                if (logWarningIfNotResolvable) {
                    if (resolution.isAmbiguous()) {
                        // The partially qualified name is ambiguous, so show the potential matches
                        iceLogger.log(Level.INFO, "The module name " + moduleName + " is ambiguous. Do you mean one of:");
                        ModuleName[] potentialMatches = resolution.getPotentialMatches();
                        for (final ModuleName element : potentialMatches) {
                            iceLogger.log(Level.INFO, "    " + element);
                        }

                    } else {
                        iceLogger.log(Level.INFO, moduleName + " is not a valid module.  Use " + listModuleCommand + " to display a list of available modules.");
                    }
                }

                result = null;
            }
        }
        
        return result;
    }

    /**
     * Set the current working module.
     * @param newTargetModuleString
     */
    protected void command_setModule (String newTargetModuleString) {
        // We use a module name resolver to resolve partially qualified module names.
        // For example, it is okay to use ":sm M2" to change to the Cal.Test.General.M2 module.
        
        ModuleName newTargetModule = resolveModuleNameInProgram(newTargetModuleString, true);
        if (newTargetModule != null) {
            targetModule = newTargetModule;
            preferredWorkingModuleName = targetModule;
        }        
    }

    /**
     * Show dependents of the named module.
     * @param moduleNameString
     */
    private void command_showModuleDependents (String moduleNameString) {
        
        ModuleName moduleName = resolveModuleNameInProgram(moduleNameString, true);
        
        if (moduleName != null) {
            Set<ModuleName> moduleNameSet = getWorkspaceManager().getDependentModuleNames(moduleName);
            if (!moduleNameSet.isEmpty()) {
                List<ModuleName> moduleNameList = new ArrayList<ModuleName>(moduleNameSet);
                Collections.sort(moduleNameList);
                iceLogger.log(Level.INFO, "These modules depend on module: " + moduleName);
                for (final ModuleName name : moduleNameList) {
                    iceLogger.log(Level.INFO, "    " + name);
                }
            } else {
                iceLogger.log(Level.INFO, "There are no modules which depend on module: " + moduleName);
            }
        }
    }

    /**
     * List all modules in the current workspace.
     * @param info
     */
    private void command_listModules (String info) {
        // modules in program
        Set<ModuleName> moduleNamesInProgram = new TreeSet<ModuleName>(Arrays.asList(getWorkspaceManager().getModuleNamesInProgram()));
        
        // modules in the workspace but not in the program
        Set<ModuleName> workspaceOnlyModuleNames = new TreeSet<ModuleName>(Arrays.asList(getWorkspaceManager().getWorkspace().getModuleNames()));
        workspaceOnlyModuleNames.removeAll(moduleNamesInProgram);

        for (final ModuleName moduleName : moduleNamesInProgram) {
            iceLogger.log(Level.INFO, moduleName.toSourceText());
        }
        
        if (!workspaceOnlyModuleNames.isEmpty()) {
            iceLogger.log(Level.INFO, "");
        }
        for (final ModuleName moduleName : workspaceOnlyModuleNames) {
            iceLogger.log(Level.INFO, moduleName.toSourceText() + " (broken)");
        }
    }

    /**
     * Show the "discoverable" modules in the current environment (i.e. the nullary environment).
     * @param info
     */
    private void command_showDiscoverableModules(String info) {
        SortedSet<ModuleName> moduleSet = getDiscoverableModules();
        
        for (final ModuleName moduleName : moduleSet) {
            iceLogger.log(Level.INFO, moduleName.toSourceText());
        }
    }

    /**
     * @return a Set of the names of the "discoverable" modules.
     */
    private SortedSet<ModuleName> getDiscoverableModules() {
        // Get the available modules in the standard vault.
        // Use a sorted set to that the modules will be sorted alphabetically.
        SortedSet<ModuleName> moduleSet = new TreeSet<ModuleName>(Arrays.asList(StandardVault.getInstance().getAvailableModules(new Status("Add Status."))));
        
        // Remove the names of modules already in the program.
        moduleSet.removeAll(Arrays.asList(getWorkspaceManager().getModuleNamesInProgram()));
        return moduleSet;
    }

    /**
     * Remove a module, and any dependents, from the current workspace.
     * @param moduleToRemoveString
     */
    private void command_removeModule (String moduleToRemoveString) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                
                ModuleName moduleToRemove = resolveModuleNameInWorkspace(moduleToRemoveString, true);
                
                if (moduleToRemove != null) {
                    CALWorkspace wkspc = workspaceManager.getWorkspace();
                    if (wkspc.containsModule(moduleToRemove)) {
                        Status s = new Status("Remove Status");
                        workspaceManager.removeModule(moduleToRemove, s);
                        if (s.getSeverity().getLevel() > Status.Severity.INFO.getLevel()) {
                            iceLogger.log(Level.INFO, "Failure removing module: " + moduleToRemove);
                            Status[] msgs = s.getChildren();
                            for (int u = 0; u < msgs.length; ++u) {
                                iceLogger.log(Level.INFO, msgs[u].getMessage());
                            }
                        }
                    } else {
                        Status removeStatus = new Status("Remove Status");
                        workspaceManager.removeModule(moduleToRemove, true, removeStatus);
                    }
                    if (!getWorkspaceManager().hasModuleInProgram(targetModule)) {
                        targetModule = getDefaultWorkingModuleName();
                        preferredWorkingModuleName = targetModule;
                    }
                }
            }
        }
    }

    /**
     * Add a "discoverable" module from the current environment (e.g. the nullary environment).
     * @param info
     */
    private void command_addDiscoverableModule(String info) {
        
        String moduleNameString = info.trim();
        if (moduleNameString.length() == 0) {
            iceLogger.log(Level.INFO, "No module name provided.");
            return;
        }
        
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                
                ModuleName unresolvedModuleName = ModuleName.maybeMake(moduleNameString);
                
                ModuleName[] moduleNamesInWorkspace = getWorkspaceManager().getWorkspace().getModuleNames();
                ModuleNameResolver workspaceModulesResolver = ModuleNameResolver.make(new HashSet<ModuleName>(Arrays.asList(moduleNamesInWorkspace)));
                ModuleNameResolver.ResolutionResult workspaceResolution = workspaceModulesResolver.resolve(unresolvedModuleName);
                
                ModuleNameResolver discoverableModulesResolver = ModuleNameResolver.make(getDiscoverableModules());

                ModuleName moduleName = resolveModuleName(moduleNameString, discoverableModulesResolver, workspaceResolution.isUnknown(), ":sdm");

                if (moduleName == null) {

                    if (workspaceResolution.isKnownUnambiguous()) {
                        iceLogger.log(Level.INFO, "The module " + workspaceResolution.getResolvedModuleName().toSourceText() + " is already in the workspace.");

                    } else if (workspaceResolution.isAmbiguous()) {
                        iceLogger.log(Level.INFO, "The following modules are already in the workspace:");
                        ModuleName[] potentialMatches = workspaceResolution.getPotentialMatches();
                        for (final ModuleName element : potentialMatches) {
                            iceLogger.log(Level.INFO, "    " + element);
                        }
                    } else {
                        // workspaceResolution.isUnknown() should be true
                        // an error message would have been logged already by resolveModuleName
                        // so just return
                    }

                    return;
                }

                Status addStatus = new Status("Add status");

                StoredVaultElement.Module moduleToAdd = StandardVault.getInstance().getStoredModule(moduleName, -1, addStatus);

                if (moduleToAdd == null) {
                    iceLogger.log(Level.INFO, "Cannot find a module named: " + moduleName);
                    if (!addStatus.isOK()) {
                        iceLogger.log(Level.INFO, addStatus.getDebugMessage());
                    }
                    return;
                }

                iceLogger.log(Level.INFO, "Adding module: " + moduleName);
                
                Status addModuleStatus = new Status("Add module status");

                if (!workspaceManager.getWorkspace().addModule(moduleToAdd, true, addModuleStatus)) {
                    iceLogger.log(Level.INFO, "Unable to compile module: " + moduleName);
                    if (!addModuleStatus.isOK()) {
                        iceLogger.log(Level.INFO, addModuleStatus.getDebugMessage());
                    }
                    return;
                }

                compileWorkspace(false, true, false);
                iceLogger.log(Level.INFO, "done");
            }
        }
    }
    
    /**
     * Change the runtime machine type.
     * @param newMachineType
     */
    private void command_machineType (String newMachineType) {
        if (newMachineType.length() == 0 || (!newMachineType.equals("g") && !newMachineType.equals("lecc"))) {
            iceLogger.log(Level.INFO, "Must provide a machine type.");
            iceLogger.log(Level.INFO, "    Current valid types are:");
            iceLogger.log(Level.INFO, "    g");
            iceLogger.log(Level.INFO, "    lecc");
        } else if (!newMachineType.equals (getMachineTypeAsString())) {
            System.setProperty(MachineConfiguration.MACHINE_TYPE_PROP, newMachineType);
            createWorkspaceManager();
            compileWorkspace (true, false, false);
            runThread = null;
        }
    }

    /**
     * Run a performance test.
     * This involves evaluating an expression n times.  Where
     * n is set via the :sptr command.  The individual time for each run,
     * an average after discarding outliers, and the std deviation are
     * displayed.
     * @param newCode
     * @param resetMachineStateBetweenRuns whether to reset the machine state between runs
     */
    private void command_performanceTest (String newCode, boolean resetMachineStateBetweenRuns) {
        if (newCode.length() == 0 && lastCode != null) {
            performanceTest (lastCode, resetMachineStateBetweenRuns);
        } else {
           lastCode = newCode;
            performanceTest (lastCode, resetMachineStateBetweenRuns);
        }
    }
    
    /**
     * Runs a script in the specified file.
     * @param script_location
     * @throws QuitException
     */
    private void command_script (String script_location) throws QuitException {
        runScript(script_location);
    }

    /**
     * Sets a file to which all output is echoed.
     * @param newFileName
     */
    private void command_outputFile (String newFileName) {
        changeOutputFile(newFileName);                
    }

    /**
     * Sets a file to which benchmark results from the :pt command are dumped.
     * @param newFileName
     */
    private void command_benchmarksResultFile (String newFileName) {
        // Update the benchmark results file.
        changeBenchmarkOutputFile(newFileName);      
    }

    /**
     * Sets a label to be associated with any benchmark results.
     * @param newLabel
     */
    private void command_benchmarksResultLabel (String newLabel) {
        // Set the label for benchmark run times in the benchmark results file.
        benchmarkResultLabel = newLabel;
        iceLogger.log(Level.INFO, "Benchmark results label set to: " + benchmarkResultLabel);
    }

    /**
     * Re-evaluate the last evaluated CAL expression.
     * @param info
     */
    private void command_runLastExpression (String info) {
        if (lastCode == null) {
            iceLogger.log(Level.INFO, "No expressions have previously been evaluated.");
        } else {
            command_runCode (lastCode);
        }
    }
    
    /**
     * Add a filter to use on the function trace output.
     * @param regExpr
     */
    private void command_filterAdd (String regExpr) {
        // Add a filter to the list of filters. (FilterAdd)
        try{
            if (regExpr.length() == 0){
                iceLogger.log(Level.INFO, "Usage: fa <RegExpression>.");
            }
            else{
                Pattern pattern = Pattern.compile( regExpr );
                filters.add( pattern );
            }
        }
        catch(PatternSyntaxException e){
            iceLogger.log(Level.INFO, "Unable to set pattern " + e);  
        }
    }
    
    /**
     * Remove a function trace filter.
     * @param indexString
     */
    private void command_filterRemove (String indexString) {
        // Remove a filter from the list of filters. (FilterRemove)
        // Provide the index number of the filter to remove.
        try{
            int index = new Integer(indexString).intValue();
            if (index < 1 || index > filters.size()){
                System.out.println("Index is out of range");
            }
            else{
                filters.remove(index-1);
            }
        }
        catch(NumberFormatException e){
            iceLogger.log(Level.INFO, "Usage: fr <RegExpressionIndex>. Use the fs command to see a list of filters in effect. " + e.toString());
        }
    }
    
    /**
     * Show the function trace filters.
     * @param info
     */
    private void command_filterShow (String info) {
        // List the filters currently in used. (FilterShow)
        int n = 1;
        for (final Pattern pattern : filters) {
            System.out.println( n++ + ": " + pattern.pattern() );
        }
    }

    /**
     * Show the disassembly of the named function.
     * @param function
     */
    private void command_disassemble (String function) {
        disassemble (function);
    }

    /**
     * Show the disassembly of all functions.
     * @param info
     */
    private void command_disassembleAll (String info) {
        disassembleAll ();
    }

    /**
     * Set the number of runs in a performance test.
     * @param nRuns
     */
    private void command_setPerformanceTestRuns (String nRuns) {
        try {
            Integer n = Integer.decode (nRuns);
            nPerformanceRuns = n.intValue();
            iceLogger.log(Level.INFO, "# of performance runs set to " + nPerformanceRuns);
        } catch (NumberFormatException e) {
            iceLogger.log(Level.INFO, "Unable to set # of performance runs to " + nRuns);
        }
    }

    /**
     * Toggle displaying output from CAL expressions.
     * @param info
     */
    private void command_suppressOutput (String info) {
        suppressOutput = !suppressOutput;
        iceLogger.log(Level.INFO, "Suppression of output is " + (suppressOutput ? "on." : "off."));
    }

    /**
     * Toggle displaying incremental machine stats from evaluating CAL expressions.
     * @param info
     */
    private void command_suppressIncrementalMachineStats(String info) {
        suppressIncrementalMachineStats = !suppressIncrementalMachineStats;
        iceLogger.log(Level.INFO, "Suppression of incremental machine stats is " + (suppressIncrementalMachineStats ? "on." : "off."));
    }
    
    /**
     * Show the total size of the generated class files
     * for the workspace.
     * Note: only works with lecc machine. 
     * @param includeStatic
     * @param includeDynamic
     */
    private void command_classFileSize (boolean includeStatic, boolean includeDynamic) {
        if (getMachineType() == MachineType.LECC) {

            if (includeDynamic) {
                LECCMachineStatistics stats = (LECCMachineStatistics)getWorkspaceManager().getMachineStatistics();
                iceLogger.log(Level.INFO, "Total class file size  (dynamically loaded): " + stats.getNClassBytesLoaded() + " bytes");
                iceLogger.log(Level.INFO, "Total class file count (dynamically loaded): " + stats.getNClassesLoaded() + " classes");
            }
            
            if (LECCMachineConfiguration.isLeccRuntimeStatic() && includeStatic) {
                // Try to determine the total size of the generated class files on disk.
                SizeAndCount sizeAndCount = calcRepositorySize();
                iceLogger.log(Level.INFO, "Total class file size  (on disk): " + sizeAndCount.size + " bytes");
                iceLogger.log(Level.INFO, "Total class file count (on disk): " + sizeAndCount.count + " classes");
            }
            
        } else {
            iceLogger.log(Level.INFO, "The :cfs command is only supported for the lecc machine.  The current machine is: " + getMachineTypeAsString()); 
        }
    }

    /**
     * Display the CAL type for the given expression.
     * @param expression
     */
    private void command_type (String expression) {
        displayType (expression);
    }

    /**
     * Show the stack trace from the last error.
     * @param info
     */
    private void command_showStackTrace (String info) {
        if (runThread == null || runThread.getError() == null) {
            iceLogger.log(Level.INFO, "There is no error stack trace to display.");
        } else {
            CALExecutorException e = runThread.getError ();
            if (e.getCause() != null) {
                e.getCause().printStackTrace(outputStream);
            } else {
                e.printStackTrace(outputStream);
            }
            iceLogger.log(Level.INFO, "");
        }
    }

    /**
     * Generate CALDoc HTML documentation.
     * @param cmdLine command line to pass to the CALDocTool.
     */
    private void command_docGen(String cmdLine) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                final String launchCommand = ":docgen";
                CALDocTool.runWithCommandLine(workspaceManager, cmdLine, iceLogger, launchCommand);
            }
        }
    }
    
    /**
     * Show the CAL doc for a module.
     * @param moduleNameString
     */
    private void command_docModule (String moduleNameString) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                
                ModuleName moduleName = resolveModuleNameInProgram(moduleNameString, true);
                if (moduleName == null) {
                    return;
                }
                
                ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(moduleName);
                if (typeInfo == null) {
                    iceLogger.log(Level.INFO, "The module " + moduleName + " does not exist.");
                } else {
                    CALDocComment comment = typeInfo.getCALDocComment();
                    if (comment != null) {
                        iceLogger.log(Level.INFO, "CALDoc for the " + moduleName + " module:\n" + CALDocToTextUtilities.getTextFromCALDocComment(comment));
                    } else {
                        iceLogger.log(Level.INFO, "There is no CALDoc for the " + moduleName + " module.");
                    }
                }
            }
        }    
    }

    /**
     * Show the CALDoc for a function or class method.
     * @param functionOrClassMethodName
     */
    private void command_docFunctionOrClassMethod(String functionOrClassMethodName) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                QualifiedName qualifiedFunctionName = resolveFunctionOrClassMethodName(functionOrClassMethodName);
                if (qualifiedFunctionName != null) {
                    ModuleName moduleName = qualifiedFunctionName.getModuleName();
                    ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(moduleName);
                    if (typeInfo == null) {
                        iceLogger.log(Level.INFO, "The module " + moduleName + " does not exist.");
                    } else {
                        String functionName = qualifiedFunctionName.getUnqualifiedName();
                        FunctionalAgent function = typeInfo.getFunctionOrClassMethod(functionName);
                        if (function == null) {
                            iceLogger.log(Level.INFO, "The function " + qualifiedFunctionName + " does not exist.");
                        } else {                            
                            ScopedEntityNamingPolicy scopedEntityNamingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(typeInfo);
                            
                            iceLogger.log(Level.INFO, qualifiedFunctionName + " :: " + function.getTypeExpr().toString(true, scopedEntityNamingPolicy) + "\n");
                            
                            CALDocComment comment;
                            String kind;
                            if (function instanceof Function) {
                                comment = ((Function)function).getCALDocComment();
                                kind = "function";
                            } else {
                                comment = ((ClassMethod)function).getCALDocComment();
                                kind = "class method";
                            }
                            if (comment != null) {
                                iceLogger.log(Level.INFO, "CALDoc for the " + qualifiedFunctionName + " " + kind + ":\n" + CALDocToTextUtilities.getTextFromCALDocComment(comment, function, scopedEntityNamingPolicy));
                            } else {
                                iceLogger.log(Level.INFO, "There is no CALDoc for the " + qualifiedFunctionName + " " + kind + ".\n" + CALDocToTextUtilities.getTextForArgumentsAndReturnValue(null, function, scopedEntityNamingPolicy));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Show the CALDoc for a data type.
     * @param typeConsName
     */
    private void command_docType(String typeConsName) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                QualifiedName qualifiedTypeName = resolveTypeConsName(typeConsName);
                if (qualifiedTypeName != null) {
                    ModuleName moduleName = qualifiedTypeName.getModuleName();
                    ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(moduleName);
                    if (typeInfo == null) {
                        iceLogger.log(Level.INFO, "The module " + moduleName + " does not exist.");
                    } else {
                        String typeName = qualifiedTypeName.getUnqualifiedName();
                        TypeConstructor typeCons = typeInfo.getTypeConstructor(typeName);
                        if (typeCons == null) {
                            iceLogger.log(Level.INFO, "The type constructor " + qualifiedTypeName + " does not exist.");
                        } else {
                            CALDocComment comment = typeCons.getCALDocComment();
                            if (comment != null) {
                                iceLogger.log(Level.INFO, "CALDoc for the " + qualifiedTypeName + " type constructor:\n" + CALDocToTextUtilities.getTextFromCALDocComment(comment));
                            } else {
                                iceLogger.log(Level.INFO, "There is no CALDoc for the " + qualifiedTypeName + " type constructor.");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Show the CALDoc for a data constructor.
     * @param expression
     */
    private void command_docDataConstructor (String expression) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                QualifiedName qualifiedDataConsName = resolveDataConsName(expression);
                if (qualifiedDataConsName != null) {
                    ModuleName moduleName = qualifiedDataConsName.getModuleName();
                    ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(moduleName);
                    if (typeInfo == null) {
                        iceLogger.log(Level.INFO, "The module " + moduleName + " does not exist.");
                    } else {
                        String dataConsName = qualifiedDataConsName.getUnqualifiedName();
                        DataConstructor dataCons = typeInfo.getDataConstructor(dataConsName);
                        if (dataCons == null) {
                            iceLogger.log(Level.INFO, "The data constructor " + qualifiedDataConsName + " does not exist.");
                        } else {
                            ScopedEntityNamingPolicy scopedEntityNamingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(typeInfo);
                            
                            iceLogger.log(Level.INFO, qualifiedDataConsName + " :: " + dataCons.getTypeExpr().toString(scopedEntityNamingPolicy) + "\n");
                            
                            CALDocComment comment = dataCons.getCALDocComment();
                            if (comment != null) {
                                iceLogger.log(Level.INFO, "CALDoc for the " + qualifiedDataConsName + " data constructor:\n" + CALDocToTextUtilities.getTextFromCALDocComment(comment, dataCons, scopedEntityNamingPolicy));
                            } else {
                                iceLogger.log(Level.INFO, "There is no CALDoc for the " + qualifiedDataConsName + " data constructor.\n" + CALDocToTextUtilities.getTextForArgumentsAndReturnValue(null, dataCons, scopedEntityNamingPolicy));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Show the CALDoc for a type class.
     * @param typeClassString
     */
    private void command_docTypeClass (String typeClassString) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                QualifiedName qualifiedTypeClassName = resolveTypeClassName(typeClassString);
                if (qualifiedTypeClassName != null) {
                    ModuleName moduleName = qualifiedTypeClassName.getModuleName();
                    ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(moduleName);
                    if (typeInfo == null) {
                        iceLogger.log(Level.INFO, "The module " + moduleName + " does not exist.");
                    } else {
                        String typeClassName = qualifiedTypeClassName.getUnqualifiedName();
                        TypeClass typeClass = typeInfo.getTypeClass(typeClassName);
                        if (typeClass == null) {
                            iceLogger.log(Level.INFO, "The type class " + qualifiedTypeClassName + " does not exist.");
                        } else {
                            CALDocComment comment = typeClass.getCALDocComment();
                            if (comment != null) {
                                iceLogger.log(Level.INFO, "CALDoc for the " + qualifiedTypeClassName + " type class:\n" + CALDocToTextUtilities.getTextFromCALDocComment(comment));
                            } else {
                                iceLogger.log(Level.INFO, "There is no CALDoc for the " + qualifiedTypeClassName + " type class.");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Show the CALDoc for a type class instance.
     * @param args
     */
    private void command_docTypeClassInstance (String args) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                
                int nextSpace = args.indexOf(' ');
                String typeClassName = args.substring(0, nextSpace);
                String instanceTypeString = args.substring(nextSpace).trim();
                
                QualifiedName qualifiedTypeClassName = resolveTypeClassName(typeClassName);
                CompilerMessageLogger logger = new MessageLogger();
                TypeExpr instanceTypeExpr = getTypeChecker().getTypeFromString(instanceTypeString, targetModule, logger);
                
                if (logger.getNErrors() > 0) {
                    dumpCompilerMessages(logger);
                } else if (qualifiedTypeClassName != null) {
                    ClassInstanceIdentifier id;
                    if (instanceTypeExpr instanceof RecordType) {
                        
                        RecordType recordType = (RecordType)instanceTypeExpr;
                        if (recordType.isRecordPolymorphic()) {
                            id = new UniversalRecordInstance(qualifiedTypeClassName);
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } else if (instanceTypeExpr instanceof TypeConsApp) {
                        id = new TypeConstructorInstance(qualifiedTypeClassName, ((TypeConsApp)instanceTypeExpr).getName());
                    } else {
                        throw new IllegalArgumentException();        
                    }
                    
                    ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(targetModule);
                    if (typeInfo == null) {
                        iceLogger.log(Level.INFO, "The module " + targetModule + " does not exist.");
                    } else {
                        ClassInstance instance = typeInfo.getVisibleClassInstance(id);
                        
                        if (instance == null) {
                            iceLogger.log(Level.INFO, "The instance " + id + " does not exist.");
                        } else {
                            CALDocComment comment = instance.getCALDocComment();
                            if (comment != null) {
                                iceLogger.log(Level.INFO, "CALDoc for the " + id + " instance:\n" + CALDocToTextUtilities.getTextFromCALDocComment(comment));
                            } else {
                                iceLogger.log(Level.INFO, "There is no CALDoc for the " + id + " instance.");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Show the CALDoc for a type class instance method.
     * @param args
     */
    private void command_docTypeClassInstanceMethod (String args) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                
                int nextSpace = args.indexOf(' ');
                String methodName = args.substring(0, nextSpace);
                args = args.substring(nextSpace).trim();
                nextSpace = args.indexOf(' ');
                String typeClassName = args.substring(0, nextSpace);
                String instanceTypeString = args.substring(nextSpace).trim();
                
                QualifiedName qualifiedTypeClassName = resolveTypeClassName(typeClassName);
                CompilerMessageLogger logger = new MessageLogger();
                TypeExpr instanceTypeExpr = getTypeChecker().getTypeFromString(instanceTypeString, targetModule, logger);
                
                if (logger.getNErrors() > 0) {
                    dumpCompilerMessages(logger);
                } else if (qualifiedTypeClassName != null) {
                    ClassInstanceIdentifier id;
                    if (instanceTypeExpr instanceof RecordType) {
                        
                        RecordType recordType = (RecordType)instanceTypeExpr;
                        if (recordType.isRecordPolymorphic()) {
                            id = new UniversalRecordInstance(qualifiedTypeClassName);
                        } else {
                            throw new IllegalArgumentException();
                        }
                    } else if (instanceTypeExpr instanceof TypeConsApp) {
                        id = new TypeConstructorInstance(qualifiedTypeClassName, ((TypeConsApp)instanceTypeExpr).getName());
                    } else {
                        throw new IllegalArgumentException();        
                    }
                    
                    ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(targetModule);
                    if (typeInfo == null) {
                        iceLogger.log(Level.INFO, "The module " + targetModule + " does not exist.");
                    } else {
                        ClassInstance instance = typeInfo.getVisibleClassInstance(id);
                        
                        if (instance == null) {
                            iceLogger.log(Level.INFO, "The instance " + id + " does not exist.");
                        } else {
                            ScopedEntityNamingPolicy scopedEntityNamingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(typeInfo);
                            
                            TypeExpr instanceMethodTypeExpr = instance.getInstanceMethodType(methodName);
                            
                            iceLogger.log(Level.INFO, methodName + " :: " + instanceMethodTypeExpr.toString(scopedEntityNamingPolicy) + "\n");
                            
                            ClassMethod classMethod = instance.getTypeClass().getClassMethod(methodName);
                            
                            try {
                                CALDocComment comment = instance.getMethodCALDocComment(methodName);
                                if (comment != null) {
                                    iceLogger.log(Level.INFO, "CALDoc for the " + methodName + " method in the " + id + " instance:\n" + CALDocToTextUtilities.getTextFromCALDocComment(comment, classMethod, instanceMethodTypeExpr, scopedEntityNamingPolicy));
                                } else {
                                    CALDocComment classMethodCALDoc = classMethod.getCALDocComment();
                                    String classMethodDoc;
                                    if (classMethodCALDoc != null) {
                                        classMethodDoc = CALDocToTextUtilities.getTextFromCALDocComment(classMethodCALDoc, classMethod, instanceMethodTypeExpr, scopedEntityNamingPolicy);
                                    } else {
                                        classMethodDoc = CALDocToTextUtilities.getTextForArgumentsAndReturnValue(classMethodCALDoc, classMethod, instanceMethodTypeExpr, scopedEntityNamingPolicy);
                                    }
                                    iceLogger.log(Level.INFO, "There is no CALDoc for the " + methodName + " method in the " + id +" instance.\n\nDocumentation for the class method " + classMethod.getName() + ":\n" + classMethodDoc);
                                }
                            } catch (IllegalArgumentException e) {
                                iceLogger.log(Level.INFO, "The instance " + id + " does not have a method named " + methodName + ".");
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Find a CAL entity.
     * @param argString
     */
    private void command_find (String argString) {
        String[] args = argString.split(" ");
        String targetArg = args[args.length - 1];

        List<SearchResult> searchResults = null;
        MessageLogger searchLogger = new MessageLogger();
        
        long searchStart = System.currentTimeMillis();
        if(args.length <= 1 || args[0].equals("all")) {
            iceLogger.log(Level.INFO, "Searching workspace for all occurrences of " + targetArg + "...");
            searchResults = workspaceManager.getWorkspace().getSourceMetrics().findAllOccurrences(targetArg, searchLogger);
        } else if(args[0].equals("ref")) {
            iceLogger.log(Level.INFO, "Searching workspace for references to " + targetArg + "...");
            searchResults = workspaceManager.getWorkspace().getSourceMetrics().findReferences(targetArg, searchLogger);
        } else if(args[0].equals("instByClass")) {
            iceLogger.log(Level.INFO, "Searching workspace for instances of class " + targetArg + "...");
            searchResults = workspaceManager.getWorkspace().getSourceMetrics().findInstancesOfClass(targetArg, searchLogger);
        } else if(args[0].equals("instByType")) {
            iceLogger.log(Level.INFO, "Searching workspace for classes that type " + targetArg + " is an instance of...");
            searchResults = workspaceManager.getWorkspace().getSourceMetrics().findTypeInstances(targetArg, searchLogger);
        } else if(args[0].equals("defn")) {
            iceLogger.log(Level.INFO, "Searching workspace for definition of " + targetArg + "...");
            searchResults = workspaceManager.getWorkspace().getSourceMetrics().findDefinition(targetArg, searchLogger);
        } else if(args[0].equals("constructions")) {
            iceLogger.log(Level.INFO, "Searching workspace for constructions of " + targetArg + "...");
            searchResults = workspaceManager.getWorkspace().getSourceMetrics().findConstructions(targetArg, searchLogger);
        } else {
            iceLogger.log(Level.INFO, "unrecognized search type '" + args[0] + "'");
        }
        
        if(searchResults != null) {
            for(final SearchResult searchResult : searchResults) {
                if(searchResult != null) {
                    
                    if (searchResult instanceof SearchResult.Precise) {
                        
                        SearchResult.Precise preciseSearchResult = (SearchResult.Precise)searchResult;
                        
                        StringBuilder searchResultBuffer = new StringBuilder();
                        searchResultBuffer.append(preciseSearchResult.getSourceRange().getSourceName());
                        searchResultBuffer.append(": (line ");
                        searchResultBuffer.append(preciseSearchResult.getSourceRange().getStartLine());
                        searchResultBuffer.append(", column ");
                        searchResultBuffer.append(preciseSearchResult.getSourceRange().getStartColumn());
                        searchResultBuffer.append(") - ");
                        searchResultBuffer.append(preciseSearchResult.getName().toSourceText());
                        
                        int pointerPos = 0;
                        String contextLine = preciseSearchResult.getContextLine();
                        String trimmedContextLine = null;
                        if(contextLine != null) {
                            trimmedContextLine = contextLine.trim();
                            searchResultBuffer.append(" - ");
                            pointerPos = searchResultBuffer.length();
                            searchResultBuffer.append(trimmedContextLine);
                        }
                        
                        iceLogger.log(Level.INFO, searchResultBuffer.toString());
                        if(pointerPos != 0) {
                            StringBuilder pointerBuffer = new StringBuilder(pointerPos + 1);
                            
                            for(int i = 0; i < pointerPos; i++) {
                                pointerBuffer.append(' ');
                            }
                            
                            int printableStartIndex = countLeftWhitespace(contextLine);
                            for(int i = printableStartIndex; i < preciseSearchResult.getContextLineIndex() ; i++) {
                                if(contextLine.charAt(i) == '\t') {
                                    pointerBuffer.append('\t');
                                } else {
                                    pointerBuffer.append(' ');
                                }
                            }
                            
                            int currentPos = pointerBuffer.length() + 1;
                            for(int i = preciseSearchResult.getContextLineIndex(); i < preciseSearchResult.getContextLineEndIndex(); i++) {
                                int width = SourcePosition.columnWidth(currentPos, contextLine.charAt(i));
                                
                                for(int j = 0; j < width; j++) {
                                    pointerBuffer.append('^');
                                    currentPos++;
                                }
                            }
                            iceLogger.log(Level.INFO, pointerBuffer.toString());
                        }
                    } else if (searchResult instanceof SearchResult.Frequency) {
                        SearchResult.Frequency freqSearchResult = (SearchResult.Frequency)searchResult;
                        int frequency = freqSearchResult.getFrequency();
                        SearchResult.Frequency.Type resultType = freqSearchResult.getType();
                        
                        String optionalS = (frequency == 1 ? "" : "s");
                        
                        String resultTypeString;
                        if (resultType == SearchResult.Frequency.Type.FUNCTION_REFERENCES) {
                            resultTypeString = "reference" + optionalS + " to ";
                        } else if (resultType == SearchResult.Frequency.Type.INSTANCE_METHOD_REFERENCES) {
                            resultTypeString = "instance method" + optionalS + " referencing ";
                        } else if (resultType == SearchResult.Frequency.Type.CLASS_CONSTRAINTS) {
                            resultTypeString = "class constraint" + optionalS + " referencing ";
                        } else if (resultType == SearchResult.Frequency.Type.CLASSES) {
                            resultTypeString = "instance" + optionalS + " for the type ";
                        } else if (resultType == SearchResult.Frequency.Type.INSTANCES) {
                            resultTypeString = "instance" + optionalS + " for the class ";
                        } else if (resultType == SearchResult.Frequency.Type.DEFINITION) {
                            resultTypeString = "definition" + optionalS + " of ";
                        } else if (resultType == SearchResult.Frequency.Type.IMPORT) {
                            resultTypeString = "import statement" + optionalS + " referencing ";
                        } else {
                            throw new IllegalStateException("Unknown search result type: " + resultType);
                        }
                        
                        String searchResultString = freqSearchResult.getModuleName() + ": " + frequency + " " + resultTypeString + freqSearchResult.getName();
                        
                        iceLogger.log(Level.INFO, "(Sourceless result) " + searchResultString);
                    } else {
                        iceLogger.log(Level.INFO, "Unknown search result: " + searchResult);
                    }
                }
            }
            
            long searchTime = System.currentTimeMillis() - searchStart;
            
            int nHits = 0;
            for (int i = 0, n = searchResults.size(); i < n; i++) {
                SearchResult result = searchResults.get(i);
                if (result instanceof SearchResult.Frequency) {
                    nHits += ((SearchResult.Frequency)result).getFrequency();
                } else {
                    nHits++;
                }
            }
            
            if(nHits == 1) {
                iceLogger.log(Level.INFO, "1 hit found in " + searchTime + "ms");
            } else {
                iceLogger.log(Level.INFO, nHits + " hits found in " + searchTime + "ms");
            }
        
            if(searchLogger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                iceLogger.log(Level.WARNING, "Search results may be incomplete due to errors during search:");
                dumpCompilerMessages(searchLogger);
            }
        }
    }

    /**
     * Generates some user requested code fragment.
     * 
     * Currently supported subcommands:
     * :generate deriving ...
     *  
     * @param argument
     */
    private void command_generate(String argument) {
        
        if (argument.startsWith("deriving ")) {
            argument = argument.substring(8).trim();
            generateDerivedInstanceFunctions(argument);
        } else {
            iceLogger.log(Level.INFO, "Unrecognized argument to generate: " + argument);
        }
    }

    /**
     * Generates the derived instance implementation of instance methods.
     * @param argument
     */
    private void generateDerivedInstanceFunctions(String argument) {
        boolean compact;
        
        if (argument.startsWith("compact ")) {
            compact = true;
            argument = argument.substring(7).trim();
        } else if (argument.startsWith("efficient ")) {
            compact = false;
            argument = argument.substring(9).trim();
        } else {
            compact = true; // default to compact
        }
        
        String type = argument.trim();
        
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                QualifiedName qualifiedTypeName = resolveTypeConsName(type);
                if (qualifiedTypeName != null) {
                    ModuleName moduleName = qualifiedTypeName.getModuleName();
                    ModuleTypeInfo typeInfo = workspaceManager.getModuleTypeInfo(moduleName);
                    if (typeInfo == null) {
                        iceLogger.log(Level.INFO, "The module " + moduleName + " does not exist.");
                    } else {
                        String typeName = qualifiedTypeName.getUnqualifiedName();
                        TypeConstructor typeCons = typeInfo.getTypeConstructor(typeName);
                        if (typeCons == null) {
                            iceLogger.log(Level.INFO, "The type constructor " + qualifiedTypeName + " does not exist.");
                        } else if (typeCons.getNDataConstructors() == 0) {
                            iceLogger.log(Level.INFO, "The type " + typeCons.getName() + " is not an algebraic type.");
                        } else {
                            
                            SourceModel.TopLevelSourceElement[] decls =
                                DerivedInstanceFunctionGenerator.makeAlgebraicTypeInstanceFunctions(
                                    typeInfo, typeCons, compact, workspaceManager.getModuleTypeInfo(CAL_Debug.MODULE_NAME));
                            
                            for (final SourceModel.TopLevelSourceElement element : decls) {
                                // remove any unnecessary qualifications based on the visibility rules of the module
                                // where the type is defined
                                SourceModel.SourceElement decl =
                                    SourceModelUtilities.UnnecessaryQualificationRemover.removeUnnecessaryQualifications(typeInfo, element);
                                
                                // separate the instances with comments
                                if (decl instanceof SourceModel.InstanceDefn) {
                                    SourceModel.InstanceDefn instanceDefn = (SourceModel.InstanceDefn)decl;
                                    System.out.println("/**");
                                    System.out.println(" * {@code " + instanceDefn.getTypeClassName().getUnqualifiedName() + "@} instance for {@code " + typeCons.getName().getUnqualifiedName() + "@}.");
                                    System.out.println(" */");
                                }
                                
                                System.out.println(decl.toSourceText());
                                
                                // add an extra newline to separate the decls if this is not a function type declaration
                                if (!(decl instanceof SourceModel.FunctionTypeDeclaration)) {
                                    System.out.println();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Show the previously executed commands in a numbered list.
     * @param info
     */
    private void command_showPreviousCommands (String info) {
        if (commandHistory.length <= 0) {
            iceLogger.log(Level.INFO, "Command history size is set to zero.");            
        } else {
            for (int i = 0; i < nPreviousCommands; ++i) {
                iceLogger.log(Level.INFO, (i+1) + "> " + commandHistory[i]);
            }
        }
    }
    
    /**
     * Re-execute a previous command.
     * @param command_number
     * @throws QuitException
     */
    private void command_previousCommand (String command_number) throws QuitException {
        try {
            Integer n = Integer.decode (command_number);
            if (n.intValue() <= 0 || n.intValue() > nPreviousCommands) {
                iceLogger.log(Level.INFO, command_number + " is not a valid command number.");
            } else {
                executeCommand (commandHistory[n.intValue()-1], false);
            }
        } catch (NumberFormatException e) {
            iceLogger.log(Level.INFO, command_number + " is not a valid command number.");
        }
    }
    
    /**
     * Set the number of previous commands to remember.
     * @param nCommands
     */
    private void command_nPreviousCommands(String nCommands) {
        try {
            int n = Integer.decode (nCommands).intValue();
            if (n < 0) {
                n = 0;
            }
            
            String[] nch = new String[n];
            
            if (n >= nPreviousCommands) {
                
                System.arraycopy(commandHistory, 0, nch, 0, nPreviousCommands);
                commandHistory = nch;
                iceLogger.log(Level.INFO, "Command history size set to " + commandHistory.length);
                return;
            }
            
            for (int i = nPreviousCommands-1, i2 = nch.length - 1; i >= 0 && i2 >= 0;) {
                nch[i2--] = commandHistory[i--];
            }

            commandHistory = nch;
            nPreviousCommands = nch.length;
            
            iceLogger.log(Level.INFO, "Command history size set to " + commandHistory.length);
        } catch (NumberFormatException e) {
            iceLogger.log(Level.INFO, nCommands + " is not a valid value for size of command history.");
        }
    }
    
    /**
     * Builds a standalone jar with the specified command line.
     * @param argument
     */
    private void command_buildStandaloneJar(String argument) {
        if (this.workspaceManager != null) {
            synchronized (this.workspaceManager) {
                
                final String[] args = argument.split(" ");
                try {
                    final ArgumentList arguments = new ArgumentList(args);
                    
                    ////
                    /// Parsing the command line arguments
                    //
                    
                    final boolean verbose;
                    if (arguments.getArgument().equals("-verbose")) {
                        arguments.consumeArgument();
                        verbose = true;
                    } else {
                        verbose = false;
                    }
                    
                    final List<MainClassSpec> mainClassSpecs = new ArrayList<MainClassSpec>();
                    final List<LibraryClassSpec> libClassSpecs = new ArrayList<LibraryClassSpec>();
                    
                    while (true) {
                        final String command = arguments.getArgument();
                        if (command.equals("-main")) {
                            arguments.consumeArgument();
                            
                            final String entryPointNameString = arguments.getAndConsumeArgument();
                            final String mainClassName = arguments.getAndConsumeArgument();

                            QualifiedName entryPointName = resolveFunctionOrClassMethodName(entryPointNameString);
                            if (entryPointName == null) {
                                iceLogger.info("Invalid entry point: " + entryPointNameString);
                                return;
                            }
                            
                            // Try to resolve the module name
                            final ModuleName unresolvedModuleName = entryPointName.getModuleName();
                            final ModuleName resolvedModuleName = resolveModuleNameInProgram(unresolvedModuleName.toSourceText(), true);
                            
                            if (resolvedModuleName == null) {
                                return; // a message was already logged about this
                            }
                            
                            // Put in the resolved module name
                            entryPointName = QualifiedName.make(resolvedModuleName, entryPointName.getUnqualifiedName());
                            
                            try {
                                mainClassSpecs.add(MainClassSpec.make(mainClassName, entryPointName));
                                
                            } catch (final StandaloneJarBuilder.InvalidConfigurationException e) {
                                iceLogger.log(Level.INFO, "Error: " + e.getMessage());
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
                            
                            } catch (final IllegalArgumentException e) {
                                iceLogger.info("Invalid scope: " + libClassScope);
                                return;
                            }
                            
                            final ModuleName moduleName = resolveModuleNameInProgram(moduleNameString, true);
                            if (moduleName == null) {
                                // the module name cannot be resolved, so we simply return (resolveModuleNameInWorkspace would have printed an error message already).
                                return;
                            }
                            
                            try {
                                libClassSpecs.add(LibraryClassSpec.make(scope, libClassName, moduleName));
                                
                            } catch (final StandaloneJarBuilder.InvalidConfigurationException e) {
                                iceLogger.log(Level.INFO, "Error: " + e.getMessage());
                                return;
                            }
                            
                        } else if (command.equals("-XX:all")) { // unsupported internal use argument - adds all modules in the program
                            arguments.consumeArgument();

                            for (final ModuleName moduleName : getWorkspaceManager().getModuleNamesInProgram()) {
                                try {
                                    libClassSpecs.add(LibraryClassSpec.make(JavaScope.PUBLIC, "cal.lib." + moduleName.toSourceText(), moduleName));

                                } catch (final StandaloneJarBuilder.InvalidConfigurationException e) {
                                    iceLogger.log(Level.INFO, "Error: " + e.getMessage());
                                    return;
                                }
                            }
                            
                        } else {
                            // not a command... so break out of the loop to process the remainder
                            break;
                        }
                    }
                    
                    if (mainClassSpecs.isEmpty() && libClassSpecs.isEmpty()) {
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
                            iceLogger.log(Level.INFO, "Invalid option: " + command);
                            return;
                        }
                    } else {
                        outputSrcZipFile = null;
                    }
                    
                    arguments.noMoreArgumentsAllowed();

                    ////
                    /// Set up the progress monitor for displaying status messages to the user.
                    //

                    final StandaloneJarBuilder.Monitor monitor = new StandaloneJarBuilder.Monitor() {
                        public void jarBuildingStarted(final String jarName) {
                            iceLogger.info("Building standalone JAR: " + jarName);
                        }
                        public void addingFile(final String fileName) {
                            if (verbose) {
                                iceLogger.info("Adding file: " + fileName);
                            }
                        }
                        public void skippingFunctionWithTypeClassConstraints(final QualifiedName name) {
                            // always display regardless of the verbose flag
                            iceLogger.info("Skipping function with type class constraints: " + name);
                        }
                        public void skippingClassMethod(final QualifiedName name) {
                            // always display regardless of the verbose flag
                            iceLogger.info("Skipping class method: " + name);
                        }
                        public void addingSourceFile(final String fileName) {
                            if (verbose) {
                                iceLogger.info("Adding file to source zip file: " + fileName);
                            }
                        }
                        public void jarBuildingDone(final String jarName, boolean success) {
                            if (success) {
                                iceLogger.info("Done building standalone JAR: " + jarName);
                            } else {
                                iceLogger.info("Failed building standalone JAR: " + jarName);
                            }
                        }
                    };

                    ////
                    /// Check the arguments and build the jar
                    //

                    try {
                        StandaloneJarBuilder.buildStandaloneJar(outputFile, outputSrcZipFile, mainClassSpecs, libClassSpecs, workspaceManager, monitor);

                    } catch (final IOException e) {
                        iceLogger.log(Level.INFO, "The operation failed due to an I/O error: " + e.getMessage());

                    } catch (final StandaloneJarBuilder.InvalidConfigurationException e) {
                        iceLogger.log(Level.INFO, "Error: " + e.getMessage());

                    } catch (final UnableToResolveForeignEntityException e) {
                        iceLogger.log(Level.INFO, "Error resolving foreign entities in CAL: " + e.getMessage());
                        
                    } catch (final StandaloneJarBuilder.InternalProblemException e) {
                        iceLogger.log(Level.SEVERE, "An internal error occurred: " + e.getMessage());
                    }
                    
                } catch (final ArgumentList.NotEnoughArgumentsException e) {
                    iceLogger.info("Not enough arguments");
                    
                } catch (final ArgumentList.TooManyArgumentsException e) {
                    iceLogger.info("Too many arguments");
                }
            }
        }
    }

    /**
     * Builds a CAL Archive (Car) out of the current workspace.
     * @param argument
     */
    private void command_buildCar(String argument) {
        
        CarBuilder.Monitor monitor = new CarBuilder.Monitor() {
            public boolean isCanceled() {
                return false;
            }
            public void showMessages(String[] messages) {
                for (final String element : messages) {
                    iceLogger.log(Level.INFO, element);
                }
            }
            public void operationStarted(int nCars, int nTotalModules) {}
            public void carBuildingStarted(String carName, int nModules) {
                iceLogger.log(Level.INFO, "Creating the Car file " + carName);
            }
            public void processingModule(String carName, ModuleName moduleName) {
                iceLogger.log(Level.INFO, "  Adding module " + moduleName + " to the Car...");
            }
            public void carBuildingDone(String carName) {
                iceLogger.log(Level.INFO, "Done creating the Car file " + carName);
            }
            public void operationDone() {}
        };
        
        ///
        // Command line syntax:
        //
        // [-keepsource] [-nocws | -nosuffix] [-s] [-jar] outputDirectory
        // OR
        // [-keepsource] [-nocws | -nosuffix] [-s] [-jar] -d outputDirectory
        
        boolean shouldBuildSourcelessModules = true; // default: generate sourceless modules
        if (argument.startsWith("-keepsource ")) {
            shouldBuildSourcelessModules = false;
            argument = argument.substring(11).trim();
        }
        
        boolean shouldGenerateCorrespWorspaceDecl = true; // default: generated the cws files
        boolean noCarSuffixInWorkspaceDeclName = false;   // default: generated cws files end with .car.cws 
        
        if (argument.startsWith("-nocws ")) {
            shouldGenerateCorrespWorspaceDecl = false;
            argument = argument.substring(6).trim();
        } else if (argument.startsWith("-nosuffix ")) {
            noCarSuffixInWorkspaceDeclName = true;
            argument = argument.substring(9).trim();
        }
        
        boolean shouldSkipModulesAlreadyInCars = false; // default: do not skip modules already in Cars
        if (argument.startsWith("-s ")) {
            shouldSkipModulesAlreadyInCars = true;
            argument = argument.substring(2).trim();
        }
        
        Set<String> carsToExclude = Collections.emptySet();
        
        boolean shouldGenerateCarJarSuffix = false; // default: generate Cars and not Car-jars.
        if (argument.startsWith("-jar ")) {
            shouldGenerateCarJarSuffix = true;
            argument = argument.substring(4).trim();
        }
        
        CarBuilder.BuilderOptions options = new CarBuilder.BuilderOptions(
            shouldSkipModulesAlreadyInCars,
            shouldGenerateCorrespWorspaceDecl,
            noCarSuffixInWorkspaceDeclName,
            shouldBuildSourcelessModules,
            carsToExclude,
            shouldGenerateCarJarSuffix);
            
        if (argument.startsWith("-d ")) {
            // the user wants to build one Car per workspace declaration file
            argument = argument.substring(2).trim();
            
            String outputDirectory = argument;
            
            try {
                long startTime = System.currentTimeMillis();
                CarBuilder.buildOneCarPerWorkspaceDeclaration(workspaceManager, monitor, new File(outputDirectory), options);
                long endTime = System.currentTimeMillis();
                iceLogger.log(Level.INFO, "Generation of the Car files took " + ((endTime - startTime) / 1000.0) + " seconds.");
            } catch (IOException e) {
                iceLogger.log(Level.INFO, "Error writing to a Car file: " + e.getMessage(), e);
            }
            
        } else {
            // the user wants to build just a single Car for everything in the workspace
            CarBuilder.Configuration config =
                CarBuilder.Configuration.makeConfigOptionallySkippingModulesAlreadyInCars(workspaceManager, options);
            config.setMonitor(monitor);
            
            String outputDirectory = argument;
            
            String carName = CarBuilder.makeCarNameFromSourceWorkspaceDeclName(workspaceManager.getInitialWorkspaceDeclarationName());
            
            try {
                long startTime = System.currentTimeMillis();
                CarBuilder.buildCar(config, new File(outputDirectory), carName, options);
                long endTime = System.currentTimeMillis();
                iceLogger.log(Level.INFO, "Generation of the Car file took " + ((endTime - startTime) / 1000.0) + " seconds.");
            } catch (IOException e) {
                iceLogger.log(Level.INFO, "Error writing to the Car file: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Show all of the modules imported by (or imported by modules imported by, etc.)
     * a set of modules specified by the user. 
     * @param argument
     */
    private void command_showModuleImports(String argument) {
        String[] moduleNameStrings = argument.split(" ");
        CALWorkspace workspace = getWorkspaceManager().getWorkspace();

        List<ModuleName> moduleNames = new ArrayList<ModuleName>();
        
        // Let's just do a sanity check before we start processing
        for (final String moduleNameString : moduleNameStrings) {
            
            ModuleName moduleName = resolveModuleNameInWorkspace(moduleNameString, true);
            
            if(moduleName == null) {
                // the module name cannot be resolved, so we simply return (resolveModuleNameInWorkspace would have printed an error message already).
                return;
            }
            
            moduleNames.add(moduleName);
        }
        
        CALWorkspace.DependencyFinder finder = workspace.getDependencyFinder(moduleNames);
        SortedSet<ModuleName> rootModules = finder.getRootSet();
        SortedSet<ModuleName> importedModules = finder.getImportedModulesSet();
        
        int rootLines = 0;
        int importedLines = 0;
        
        try {
            iceLogger.log(Level.INFO, "These modules ");
            for (final ModuleName moduleName : rootModules) {
                int lines = getLineCount(moduleName, workspace);
                rootLines += lines;
                iceLogger.log(Level.INFO, "    " + moduleName + " = " + lines + " lines");
            }
            iceLogger.log(Level.INFO, "depend on these modules: ");
            for (final ModuleName moduleName : importedModules) {
                int lines = getLineCount(moduleName, workspace);
                importedLines += lines;
                iceLogger.log(Level.INFO, "    " + moduleName + " = " + lines + " lines");
            }
            iceLogger.log(Level.INFO, ""); 

            NumberFormat formatter = NumberFormat.getInstance();
            formatter.setMinimumFractionDigits(1);
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumIntegerDigits(1);
            
            iceLogger.log(Level.INFO, "# of dependent modules = " + rootModules.size());
            iceLogger.log(Level.INFO, "# of depended-upon modules = " + importedModules.size());
            iceLogger.log(Level.INFO, ""); 
            iceLogger.log(Level.INFO, "total dependent module lines = " + rootLines);
            iceLogger.log(Level.INFO, "total depended-upon module lines = " + importedLines);
            iceLogger.log(Level.INFO, "total lines = " + (rootLines + importedLines));
            iceLogger.log(Level.INFO, "ratio of depended-upon lines to dependent lines = " + formatter.format(((double)importedLines) / ((double)rootLines))); 

        } catch(IOException e) {
            iceLogger.log(Level.INFO, e.toString());
        }
    }
    
    /**
     * Implements the :lcaf [<module_name>] [<includeDependees>] command.
     * @param argument
     */
    private void command_listCafs(String argument) {
                              
        final String[] args = argument.split(" ");
        
        final ModuleName moduleName;
        final boolean includeDependees;
               
        if (argument.trim().length() == 0) { //args will always be of length 1 or more.
            if (targetModule == null) {
                 iceLogger.log(Level.INFO, "null target module.");
                 return;
            }
            moduleName = targetModule;                       
            includeDependees = false;
            
        } else if (args.length == 1 || args.length == 2) {
            
            moduleName = resolveModuleNameInWorkspace(args[0], true);
            if (moduleName == null) {
                //will have already been logged.
                return;
            }
            
            if (args.length == 1) {
                includeDependees = false;
            } else {
                final String arg = args[1].toLowerCase();
                if (arg.equals("true") || arg.equals("t")) {
                    includeDependees = true;
                } else if (arg.equals("false") || arg.equals("f")){
                    includeDependees = false;
                } else {
                    iceLogger.log(Level.INFO, "invalid <includeDependees> argument (should be true or false).");
                    return;
                }
            }
            
            
        } else {
            
            iceLogger.log(Level.INFO, "Too many arguments provided.");
            return;
        }
        
        List<QualifiedName> cafs = listCAFs(moduleName, includeDependees);
        
        iceLogger.log(Level.INFO, "The " + cafs.size() + " CAFs in module " + moduleName +
            (includeDependees ? " (including" : " (not including") + " dependees):");
        for (final QualifiedName caf : cafs) {            
            iceLogger.log(Level.INFO, caf.getQualifiedName());
        }                             
    }
    
    /**
     * Generate a list of CAFs.  
     * The list will include all CAFs in the named module, and dependee modules
     * if so indicated, except the following, which are excluded:
     * <ol>
     *   <li> CAFs which are defined as a literal value.
     *   <li> CAFs which are an alias for another function.
     *   <li> CAFs which are generated due to a deriving clause in a type declaration
     * </ol>
     * Ultimately this functionality is intended to aid in debugging CAL space behaviour
     * so we only list entities which can actually consume space.    
     * <p>
     * CAFs can be a source of space consumption in CAL programs.  This occurs because CAF
     * values are cached for repeated use and thus can exist across multiple executions and
     * compilations within a workspace. This functions allow a user to determine the CAFs in
     * the current workspace. The user can the use functions like Debug.internalValueStats to
     * display the amount of space that a particular CAF holds. This lets a user check that the
     * expected space usage of a CAF is in line with the actual usage.     
     *   
     * @param moduleName
     * @param includeDependees
     * @return List<QualifiedName> list of cafs, sorted by name
     */
    private List<QualifiedName> listCAFs (final ModuleName moduleName, final boolean includeDependees) {
        if (moduleName == null) {
            throw new NullPointerException ("Null module reference in ExecutionContext.listCAFs().");
        }       
                               
        Module module = workspaceManager.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException ("The module " + moduleName + " does not exist in the current workspace.");
        }
        
        Set<ModuleName> moduleNames;
        if (includeDependees) {
            moduleNames = module.getDependeeModuleNames();
        } else {
            moduleNames = new HashSet<ModuleName> ();
        }
        moduleNames.add(moduleName);
        

        List<QualifiedName> cafNames = new ArrayList<QualifiedName>();
        
        for (final ModuleName currentModuleName : moduleNames) {
            
            Module currentModule = workspaceManager.getModule(currentModuleName);
            
            for (final MachineFunction mf : currentModule.getFunctions()) {
                
                if (mf.isCAF() &&  // Only include CAFs
                    mf.getAliasOf() == null &&  // exclude CAFs which are an alias for another function 
                    mf.getLiteralValue() == null &&  // exclude CAFs which are simply a literal value
                    mf.getName().indexOf('$') < 0){ // exclude CAFs which are automatically generated by a deriving clause
                    cafNames.add(mf.getQualifiedName());
                }
            }
        }
        
        Collections.sort(cafNames);
                        
        return cafNames;
    }    

    /**
     * Show summarized program statistics for the named module(s), or all modules if none specified.
     * @param argument modules for which stats will be shown.
     *  If empty, stats will be shown for the entire program.
     */
    private void command_showProgramStatistics(String argument) {

        // Parse out the module names from argument if any.
        String[] args = argument.trim().split(" ");
        List<ModuleName> moduleNames = new ArrayList<ModuleName>(args.length);
        
        boolean providedModuleNames = false;
        
        for (final String arg : args) {
            String trimmedArg = arg.trim();
            
            if (trimmedArg.length() > 0) {
                providedModuleNames = true;
                
                ModuleName maybeModuleName = ModuleName.maybeMake(trimmedArg);
                if (maybeModuleName == null) {
                    iceLogger.log(Level.WARNING, "Invalid module name: " + trimmedArg);
                } else {
                    moduleNames.add(maybeModuleName);
                }
            }
        }
        
        // If no module names are provided, collect stats for all modules in the program.
        if (!providedModuleNames) {
            ModuleName[] moduleNamesInProgram = workspaceManager.getModuleNamesInProgram();
            moduleNames = Arrays.asList(moduleNamesInProgram);
        }
        
        // Create the TypeInfoStats object which will collect the stats.
        TypeInfoStats typeInfoStats = new TypeInfoStats(iceLogger);
        
        // Collect stats for all modules in the list.
        for (final ModuleName moduleName : moduleNames) {
            Module module = workspaceManager.getModule(moduleName);
            if (module == null) {
                iceLogger.log(Level.WARNING, "Module not in program: " + moduleName);
                continue;
            }
            
            ModuleTypeInfo moduleTypeInfo = module.getModuleTypeInfo();
            typeInfoStats.collectTypeInfoStats(moduleTypeInfo);
        }
        
        if (typeInfoStats.totalModules == 0) {
            // nothing to do.
            return;
        }
        
        // Dump stats to the logger.
        typeInfoStats.dumpStats();
    }
    
    /**
     * Generate a workspace file containing the smallest set of modules that includes
     * a "root set" of modules specified by the user. 
     * @param argument
     */
    private void command_makeMinimalWorkspace(String argument) {
        
        boolean loadNewWorkspace = false;
        if (argument.startsWith("-l ")) {
            loadNewWorkspace = true;
            argument = argument.substring(2).trim();
        }
    
        int workspaceStartIndex = argument.indexOf('"');
        int workspaceEndIndex = argument.lastIndexOf('"');
        
        if(workspaceStartIndex <= 0 || workspaceEndIndex <= 0 || workspaceEndIndex == workspaceStartIndex) {
            iceLogger.log(Level.INFO, "The final argument should be a filename enclosed in double-quotes");
            return;
        }
    
        String workspaceFilename = argument.substring(workspaceStartIndex + 1, workspaceEndIndex);
        String spaceSeparatedModuleNames = argument.substring(0, workspaceStartIndex);
        String[] moduleNameStrings = spaceSeparatedModuleNames.split(" ");
        
        if(moduleNameStrings.length == 0) {
            iceLogger.log(Level.INFO, "At least one module name should be provided");
            return;
        }
        
        ModuleName[] moduleNames = new ModuleName[moduleNameStrings.length];

        CALWorkspace workspace = getWorkspaceManager().getWorkspace();
        for(int i = 0, nModules = moduleNameStrings.length; i < nModules; i++) {
            moduleNames[i] = ModuleName.maybeMake(moduleNameStrings[i]);
            if (moduleNames[i] == null) {
                iceLogger.log(Level.INFO, "Invalid module name '" + moduleNameStrings[i] + "'");
                return;
            }
            
            if(!workspace.containsModule(moduleNames[i])) {
                iceLogger.log(Level.INFO, moduleNames[i] + " is not a valid module.  Use :lm to display a list of available modules.");
                return;
            }
        }

        String content = generateMinimalWorkspaceFileContent(workspaceFilename, moduleNames, workspace);
        
        FileWriter fw = null;
        try {
            fw = new FileWriter(workspaceFilename, false);
            fw.write(content);
            
            iceLogger.log(Level.INFO, "done.");
            
        } catch(IOException e) {
            iceLogger.log(Level.INFO, "Error: " + e.toString());

        } finally {
            try {
                if(fw != null) {
                    fw.close();
                }
            } catch(IOException e) {
            }
        }
        
        if (loadNewWorkspace) {
            command_loadWorkspace(workspaceFilename);
        }
    }

    /**
     * Generate the content for a minimal workspace file.
     * @param workspaceFilename For display purposes only. Can be null.
     * @param moduleNames
     * @param workspace
     * @return String which is the definition of a minimal workspace
     */
    private String generateMinimalWorkspaceFileContent(String workspaceFilename, ModuleName[] moduleNames, CALWorkspace workspace) {
        
        CALWorkspace.DependencyFinder finder = workspace.getDependencyFinder(Arrays.asList(moduleNames));
        
        return generateMinimalWorkspaceFileContent(workspaceFilename, workspace, finder);
    }

    /**
     * Generate the content for a minimal workspace file.
     * @param workspaceFilename For display purposes only. Can be null.
     * @param workspace
     * @param finder
     * @return String which is the defintion of a minimal workspace
     */
    private String generateMinimalWorkspaceFileContent(String workspaceFilename, CALWorkspace workspace, CALWorkspace.DependencyFinder finder) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        SortedSet<ModuleName> rootModules = finder.getRootSet();
        SortedSet<ModuleName> importedModules = finder.getImportedModulesSet();
   
        if (workspaceFilename != null) {
            iceLogger.log(Level.INFO, "Creating a minimal workspace for modules " + rootModules.toString() + " at " + workspaceFilename);
        } else {
            iceLogger.log(Level.INFO, "Creating a minimal workspace for modules " + rootModules.toString());
        }
        
        pw.println("// This CAL workspace declaration file is automatically generated at " + new Date());
        
        pw.println();
        pw.println("// imported modules");
        for (final ModuleName moduleName : importedModules) {
            generateWorkspaceDeclarationLine(pw, workspace, moduleName);
        }
        
        pw.println();
        pw.println("// root modules");
        for (final ModuleName moduleName : rootModules) {
            generateWorkspaceDeclarationLine(pw, workspace, moduleName);
        }
        
        pw.flush();
        
        return sw.toString();
    }

    /**
     * Generate a line for a workspace declaration.
     * @param pw the PrintWriter to output to.
     * @param workspace the source workspace.
     * @param moduleName
     */
    private void generateWorkspaceDeclarationLine(PrintWriter pw, CALWorkspace workspace, ModuleName moduleName) {
        VaultElementInfo vaultInfo = workspace.getVaultInfo(moduleName);
        if (vaultInfo instanceof VaultElementInfo.Basic) {
            pw.println(((VaultElementInfo.Basic)vaultInfo).toDeclarationString());
        } else {
            iceLogger.log(Level.INFO, "The module " + moduleName + " comes from the " + vaultInfo.getVaultDescriptor() + ", which is not handled by this command. Defaulting to use the StandardVault for this module.");
            pw.println("StandardVault " + moduleName);
        }
    }
    
    /**
     * Load a minimal workspace containing the smallest set of modules that includes
     * a "root set" of modules specified by the user.
     * @param argument 
     */
    private void command_loadMinimalWorkspace(String argument) {
        
        String[] moduleNameStrings = argument.split(" ");
        
        if(moduleNameStrings.length == 0) {
            iceLogger.log(Level.INFO, "At least one module name should be provided");
            return;
        }

        CALWorkspace workspace = getWorkspaceManager().getWorkspace();
        
        ModuleName[] moduleNames = new ModuleName[moduleNameStrings.length];
        
        // Let's just do a sanity check before we start processing
        for(int i = 0, nModules = moduleNameStrings.length; i < nModules; i++) {
            moduleNames[i] = resolveModuleNameInWorkspace(moduleNameStrings[i], true);
            
            if(moduleNames[i] == null) {
                // the module name cannot be resolved, so we simply return (resolveModuleNameInWorkspace would have printed an error message already).
                return;
            }
        }

        final String content = generateMinimalWorkspaceFileContent(null, moduleNames, workspace);
        
        streamProvider = new WorkspaceDeclaration.StreamProvider() {

            public String getName() {
                return "minimal-workspace";
            }

            public String getLocation() {
                return "minimal-workspace";
            }

            public String getDebugInfo(VaultRegistry vaultRegistry) {
                return "constructed programmatically";
            }

            public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
                return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(content));
            }
        };
        
        createWorkspaceManager();
        compileWorkspace (true, false, false);
        if (targetModule == null || !getWorkspaceManager().hasModuleInProgram(targetModule)) {
            // Set the preferred working module to be the first specified module.
            preferredWorkingModuleName = moduleNames[0];
            
            changeToPreferredWorkingModule();
        }
    }
    
    /**
     * @param moduleName
     * @param workspace the workspace from which the module definition is to be fetched.
     * @return the number of lines in module named moduleName.  Note that we're not
     *          doing any sort of clever filtering; it is literally the number of
     *          newline-terminated lines, including blank lines, comments, etc.
     * @throws IOException
     */
    static int getLineCount(ModuleName moduleName, CALWorkspace workspace) throws IOException {
        int count = 1;
        Reader reader = workspace.getSourceDefinition(moduleName).getSourceReader(new Status("reading source for refactoring"));
        if (reader == null) {
            throw new IllegalArgumentException("module " + moduleName + " is not in the workspace");
        }
        
        BufferedReader sourceReader = new BufferedReader(reader);
        try {
            while(sourceReader.readLine() != null) {
                count ++;
            }
        } finally {
            sourceReader.close();
        }
        
        return count;
    }
    
    /**
     * Execute a command.  A command can be an ICE directive, indicated by a leading ':', or a CAL expression
     * to be executed.
     * @param commandString
     * @param rememberCommand
     * @throws QuitException
     */
    private void executeCommand (String commandString, boolean rememberCommand) throws QuitException {
        if (commandString.startsWith (":")) {
            if (commandString.length() <= 1) {
                return;
            }
            
            String command = commandString.substring(1);
            String info = "";
            StringTokenizer tokenizer = new StringTokenizer(command);
            command = tokenizer.nextToken().trim().toLowerCase();
            if (tokenizer.hasMoreTokens()) {
                info = commandString.substring(command.length() + 1).trim();
            }
            ICECommand commandInfo = commandToCommandInfo.get(command);
            if (commandInfo != null) {
                if (getExecutionContext().hasSuspendedThreads() && !commandInfo.isValidWhileCALExecutionSuspended()) {
                    iceLogger.log(Level.INFO, commandString + "  Is not valid while CAL execution is suspended.");
                } else {
                    commandInfo.performCommand(info);
                    if (rememberCommand && commandInfo.addToCommandHistory()) {
                        rememberCommand(commandString);
                    }
                }
            } else {
                iceLogger.log(Level.INFO, "Unrecognized command.");
            }
        } else {
            // User is trying to execute some code.
            // This is not allowed if CAL execution is currently
            // suspended.
            if (commandString.length () > 0) {
                if (getExecutionContext().hasSuspendedThreads()) {
                    iceLogger.log(Level.INFO, "Running CAL code is not allowed while a CAL execution is suspended.");
                } else { 
                    lastCode = commandString;
                    command_runCode (commandString);
                    if (rememberCommand) {
                        rememberCommand(commandString);
                    }
                }
            }
        }
        
    }

    /**
     * Add a command to the command history.
     * We only add the command if it is not already in the history.
     * @param command
     */
    private void rememberCommand (String command) {
        if (commandHistory.length == 0) {
            // We aren't remembering any commands.
            return;
        }
        command = command.trim();
        
        // Check to see if the command already is in the history.
        for (int i = 0; i < nPreviousCommands; ++i) {
            if (command.equals(commandHistory[i])) {
                // command is already in history.
                return;
            }
        }
        
        if (nPreviousCommands >= commandHistory.length) {
            System.arraycopy(commandHistory, 1, commandHistory, 0, commandHistory.length-1);
            commandHistory[commandHistory.length-1] = command;
        } else {
            commandHistory[nPreviousCommands++] = command;
        }
    }

    /**
     * Dump the messages contained in logger to the console
     * @param logger A CompilerMessageLogger
     */
    private void dumpCompilerMessages(CompilerMessageLogger logger) {
        for (final CompilerMessage message : logger.getCompilerMessages()) {
            iceLogger.log(Level.INFO, "  " + message.toString());
        }
    }
    
    
    /**
     * Helper function for displaying trimmed context lines.
     * @param string String to count leading whitespace in 
     * @return The number of leading non-whitespace characters; this method treats tab
     *          characters as a single character.
     */
    private int countLeftWhitespace(String string) {
        int i = 0;
        while (i < string.length() && Character.isWhitespace(string.charAt(i))) {
            i++;
        }
        return i;
    }
    
    private void displayType (String expressionText) {
        if (workspaceManager == null) {
            compileWorkspace(true, false, false);
        }

        synchronized (this.workspaceManager) {
            
            // Qualify unqualified symbols in code, if unambiguous
            CompilerMessageLogger logger = new MessageLogger();
            CodeAnalyser analyser = new CodeAnalyser(getTypeChecker(), getWorkspaceManager().getModuleTypeInfo(targetModule), false, false);
            CodeAnalyser.QualificationResults qualificationResults = analyser.qualifyExpression(expressionText, null, null, logger, true);
            if (qualificationResults == null){
                iceLogger.log(Level.INFO, "Attempt to qualify expression has failed because of errors: ");
                dumpCompilerMessages(logger);
                return;
            }
            
            String scDef = qualificationResults.getQualifiedCode();
            scDef = targetName + " = \n" + scDef + "\n;";
            
            TypeExpr te = getTypeChecker().checkFunction(new AdjunctSource.FromText(scDef), targetModule, logger);
            if (te == null) {
                iceLogger.log(Level.INFO, "Attempt to type expression has failed because of errors: ");
                dumpCompilerMessages(logger);
                return;
            } 
            
            //we want to display the type using fully qualified type constructor names, but also making use of 
            //preferred names of type are record variables
            iceLogger.log(Level.INFO, "  " + te.toString(true, ScopedEntityNamingPolicy.FULLY_QUALIFIED) + "\n");
        }
    }
    
    /**
     * Resolves the given name as a function or class method given the current target module.
     * @param name the name to be resolved.
     * @return a QualifiedName for the resolved function, or null if the name cannot be resolved.
     */
    private QualifiedName resolveFunctionOrClassMethodName(String name) {
        ModuleTypeInfo moduleTypeInfo = getWorkspaceManager().getModuleTypeInfo(targetModule);
        
        QualifiedName resolvedName = CodeAnalyser.resolveEntityNameAccordingToImportUsingClauses(name, Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, moduleTypeInfo);
        
        if (resolvedName != null) {
            return resolvedName;
        } else {
            return qualifyExpression(name);
        }
    }
    
    /**
     * Resolves the given name as a data constructor given the current target module.
     * @param name the name to be resolved.
     * @return a QualifiedName for the resolved data constructor, or null if the name cannot be resolved.
     */
    private QualifiedName resolveDataConsName(String name) {
        ModuleTypeInfo moduleTypeInfo = getWorkspaceManager().getModuleTypeInfo(targetModule);
        
        QualifiedName resolvedName = CodeAnalyser.resolveEntityNameAccordingToImportUsingClauses(name, Category.DATA_CONSTRUCTOR, moduleTypeInfo);
        
        if (resolvedName != null) {
            return resolvedName;
        } else {
            return qualifyExpression(name);
        }
    }
    
    /**
     * Resolves the given name as a type constructor given the current target module.
     * @param name the name to be resolved.
     * @return a QualifiedName for the resolved type constructor, or null if the name cannot be resolved.
     */
    private QualifiedName resolveTypeConsName(String name) {
        ModuleTypeInfo moduleTypeInfo = getWorkspaceManager().getModuleTypeInfo(targetModule);
        
        QualifiedName resolvedName = CodeAnalyser.resolveEntityNameAccordingToImportUsingClauses(name, Category.TYPE_CONSTRUCTOR, moduleTypeInfo);
        
        if (resolvedName != null) {
            return resolvedName;
        } else {
            return qualifyType(name);
        }
    }
    
    /**
     * Resolves the given name as a type class given the current target module.
     * @param name the name to be resolved.
     * @return a QualifiedName for the resolved type class, or null if the name cannot be resolved.
     */
    private QualifiedName resolveTypeClassName(String name) {
        ModuleTypeInfo moduleTypeInfo = getWorkspaceManager().getModuleTypeInfo(targetModule);
        
        QualifiedName resolvedName = CodeAnalyser.resolveEntityNameAccordingToImportUsingClauses(name, Category.TYPE_CLASS, moduleTypeInfo);
        
        if (resolvedName != null) {
            return resolvedName;
        } else {
            return qualifyTypeClass(name);
        }
    }
    
    private QualifiedName qualifyExpression(String nameAsText) {
        // Qualify unqualified symbols in code, if unambiguous
        CompilerMessageLogger logger = new MessageLogger();
        CodeAnalyser analyser = new CodeAnalyser(getTypeChecker(), getWorkspaceManager().getModuleTypeInfo(targetModule), false, false);
        CodeAnalyser.QualificationResults qualificationResults = analyser.qualifyExpression(nameAsText, null, null, logger, true);
        if (qualificationResults == null){
            iceLogger.log(Level.INFO, "Attempt to qualify expression has failed because of errors: ");
            dumpCompilerMessages(logger);
            return null;
        }
        
        String qualifiedCode = qualificationResults.getQualifiedCode();
        if (qualifiedCode.indexOf('.') != -1) {
            return QualifiedName.makeFromCompoundName(qualifiedCode);
        } else {
            return QualifiedName.make(targetModule, nameAsText);
        }
    }
    
    private QualifiedName qualifyType(String typeAsText) {
        // Qualify unqualified symbols in code, if unambiguous
        CompilerMessageLogger logger = new MessageLogger();
        CodeAnalyser analyser = new CodeAnalyser(getTypeChecker(), getWorkspaceManager().getModuleTypeInfo(targetModule), false, false);
        CodeAnalyser.QualificationResults qualificationResults = analyser.qualifyExpression(CAL_Prelude.Functions.undefined.getQualifiedName() + "::" + typeAsText, null, null, logger, true);
        if (qualificationResults == null){
            iceLogger.log(Level.INFO, "Attempt to qualify expression has failed because of errors: ");
            dumpCompilerMessages(logger);
            return null;
        }
        
        String qualifiedCode = qualificationResults.getQualifiedCode();
        qualifiedCode = qualifiedCode.substring(qualifiedCode.lastIndexOf(':') + 1);
        if (qualifiedCode.indexOf('.') != -1) {
            return QualifiedName.makeFromCompoundName(qualifiedCode);
        } else {
            return QualifiedName.make(targetModule, typeAsText);
        }
    }
    
    private QualifiedName qualifyTypeClass(String typeClassName) {
        CompilerMessageLogger logger = new MessageLogger();
        TypeExpr typeExpr = getTypeChecker().getTypeFromString(typeClassName + " a => a", targetModule, logger);
        
        if (logger.getNErrors() > 0) {
            dumpCompilerMessages(logger);
            return null;
        }
        
        String typeString = typeExpr.toString(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
        
        String qualifiedTypeClassName = typeString.substring(0, typeString.indexOf(' '));
        
        if (qualifiedTypeClassName.indexOf('.') != -1) {
            return QualifiedName.makeFromCompoundName(qualifiedTypeClassName);
        } else {
            return QualifiedName.make(targetModule, typeClassName);
        }
    }
    
    /**
     * Get the name of the module to use as the default - this will be the first module encountered when the workspace declaration files are read
     * @return the name of default working module, or null if there are no modules.
     */
    private ModuleName getDefaultWorkingModuleName() {
        
        CALWorkspace workspace = getWorkspaceManager().getWorkspace();        
        
        ModuleName firstModule = workspace.getFirstModuleName();
        if (firstModule != null)
            return firstModule;
        else
            return NO_MODULE;
    }

    /**
     * Run the named CAF.
     * 
     * Original Author: rcypher
     * @param cafName the name of the caf to run.
     */
    private void runCAF (String cafName) {
        if (cafName == null || cafName.length () == 0) {
            return;
        }
        
        CompilerMessageLogger logger = new MessageLogger();
        EntryPoint entryPoint = getCompiler().getEntryPoint(
            EntryPointSpec.make(EntryPointImpl.makeEntryPointName (cafName)), targetModule, logger);
        if (entryPoint == null) {
            iceLogger.log(Level.INFO, "CAL: Compilation unsuccessful because of errors:");
            dumpCompilerMessages(logger);
        }
        
        lastCode = cafName;
        
        runFunction(entryPoint, true, true, false);
    }
    
    /**
     * Run the given piece of code and print out performance stats.
     * @param code String The code to be run.
     * @return true if execution was successful, false otherwise.
     */
    protected boolean command_runCode(String code) {
        executionTimeGenerator.reset();
        EntryPoint entryPoint = makeRunTarget(code, true);
        boolean runResult = runFunction(entryPoint, !suppressOutput, true, !suppressIncrementalMachineStats);

        return runResult;
    }
    
    private void makeRunThread () {
        if (runThread == null) {
            runThread = new FunctionRunThread(getWorkspaceManager(), "MainIceRunThread", iceLogger);
        }
    }
    
    /**
     * Run the given function and store the result in the current executionTimeGenerator.
     * @param entryPoint
     * @param showOutput whether to show the output.
     * @param showRuntimes whether to show the runtimes
     * @param showIncrementalMachineStats whether to show incremental machine stats
     * @return true if run was successful, false if there is an error.
     */
    private boolean runFunction(EntryPoint entryPoint,
                                boolean showOutput, 
                                boolean showRuntimes, 
                                boolean showIncrementalMachineStats) {
        // Start a new run thread.
        // Note that we start a new thread instead of running in the existing thread, to take advantage of any
        //   increased stack space that may be allocated to new threads.
        makeRunThread();
        runThread.setTraceFilters( filters );
        runThread.setShowOutput(showOutput);
        runThread.setShowRuntimes(showRuntimes);
        runThread.setShowIncrementalMachineStats(showIncrementalMachineStats);
        runThread.setEntryPoint(entryPoint);
        runThread.setTargetModule(targetModule);
        runThread.setDoExecDiag(doExecDiag);
        runThread.setRunningCode(lastCode);

        // Start an interrupt monitor.  This checks the input stream and sets
        // the runThread halt flag if any input is detected.
        InterruptMonitor im = new InterruptMonitor (runThread);
        im.start();

        runThread.getDebugController().setShouldBlockUI(true);
        runThread.start(executionTimeGenerator);
        runThread.getDebugController().blockUntilCompletionOrInterruption();
        
        // Shut down the interrupt monitor.
        im.end();
        im.interrupt();

        
        if (getExecutionContext().hasSuspendedThreads()) {
            command_showSuspensionState();
        }
        
        return runThread.getError() == null;
    }
    
    private boolean command_resumeExecution () {
        ExecutionContextImpl ec = getExecutionContext();
        
        // If we're not suspended we can't resume.
        if (!ec.hasSuspendedThreads()) {
            iceLogger.log(Level.INFO, "There is no CAL program currently suspended.");
            return true;
        }
        
        iceLogger.log(Level.INFO, "Resuming execution of: " + lastCode);
        
        // Start an interrupt monitor.  This checks the input stream and sets
        // the runThread halt flag if any input is detected while the CAL
        // program is executing.
        InterruptMonitor im = new InterruptMonitor (runThread);
        im.start();

        // Tell the execution context that it is time to resume.
        getDebugController().setShouldBlockUI(true);
        ec.resumeAll();
        getDebugController().blockUntilCompletionOrInterruption();
        
        // Shut down the interrupt monitor.
        im.end();
        im.interrupt();

        // Check whether execution has terminated or suspended.
        if (getExecutionContext().hasSuspendedThreads()) {
            command_showSuspensionState();
        }
        
        return runThread.getError() == null;
    }
    
    private boolean command_resumeCurrentThread(boolean shouldStepThread) {
        ExecutionContextImpl ec = getExecutionContext();
        
        // If we're not suspended we can't resume.
        if (!ec.hasSuspendedThreads()) {
            iceLogger.log(Level.INFO, "There is no CAL program currently suspended.");
            return true;
        }
        
        final Set<Thread> suspendedThreads = ec.getThreadSuspensions().keySet();
        final Thread currentSuspendedThread = getDebugController().getCurrentSuspendedThread();
        if (!suspendedThreads.contains(currentSuspendedThread)) {
            iceLogger.log(Level.INFO, "The current thread is no longer suspended. Change to another thread.");
            return true;
        }
        
        if (shouldStepThread) {
            ec.addThreadToBeStepped(currentSuspendedThread);
        } else {
            ec.removeThreadToBeStepped(currentSuspendedThread);            
        }
        
        iceLogger.log(Level.INFO, "Resuming execution of: " + lastCode + " on thread: " + currentSuspendedThread.getName());
        
        // Start an interrupt monitor.  This checks the input stream and sets
        // the runThread halt flag if any input is detected while the CAL
        // program is executing.
        InterruptMonitor im = new InterruptMonitor (runThread);
        im.start();

        // Figure out whether we should block, or switch to another thread as the current suspended thread
        suspendedThreads.remove(currentSuspendedThread);
        
        // Tell the execution context that it is time to resume.
        if (suspendedThreads.isEmpty()) {
            getDebugController().setShouldBlockUI(true);
            ec.resumeThread(currentSuspendedThread);
            getDebugController().blockUntilCompletionOrInterruption();
            
        } else if (shouldStepThread) {
            // since we are stepping, we would like to block the UI until the next interruption (hopefully it's the thread being stepped)
            getDebugController().setShouldBlockUI(true);
            ec.resumeThread(currentSuspendedThread);
            getDebugController().blockUntilCompletionOrInterruption();
            
        } else {
            ec.resumeThread(currentSuspendedThread);
            // if the thread is getting resumed without stepping, choose another current thread
            getDebugController().setCurrentSuspendedThread(suspendedThreads.iterator().next());
        }
        
        // Shut down the interrupt monitor.
        im.end();
        im.interrupt();

        // Check whether execution has terminated or suspended.
        if (getExecutionContext().hasSuspendedThreads()) {
            command_showSuspensionState();
        }
        
        return runThread.getError() == null;
    }
    
    /**
     * Shows the currently suspended threads.
     */
    private void command_showSuspendedThreads() {
        ExecutionContextImpl ec = getExecutionContext();
        
        Map<Thread, SuspensionState> suspensions = ec.getThreadSuspensions();
        
        SortedMap<Long, Thread> threads = new TreeMap<Long, Thread>();
        for (final Thread thread : suspensions.keySet()) {
            threads.put(Long.valueOf(thread.getId()), thread);
        }
        
        Thread currentThread = getDebugController().getCurrentSuspendedThread();
        
        iceLogger.log(Level.INFO, "Currently suspended threads:");
        for (final Thread thread : threads.values()) {
            String item = thread.getId() + ": " + thread.getName();
            if (thread == currentThread) {
                item += " <- current thread";
            }
            iceLogger.log(Level.INFO, item);
        }
    }
    
    /**
     * Allows the user to choose the "current" thread.
     */
    private void command_setSuspendedThread(String arg) {
        final long threadID;
        try {
            threadID = Long.parseLong(arg);
            
        } catch (NumberFormatException e) {
            iceLogger.log(Level.INFO, "Invalid thread ID: " + arg);
            return;
        }
        
        ExecutionContextImpl ec = getExecutionContext();
        
        Map<Thread, SuspensionState> suspensions = ec.getThreadSuspensions();
        
        Thread match = null;
        for (final Thread thread : suspensions.keySet()) {
            if (thread.getId() == threadID) {
                match = thread;
                break;
            }
        }
        if (match == null) {
            iceLogger.log(Level.INFO, "There is no suspended thread with the ID: " + arg);
            return;
        }
        
        getDebugController().setCurrentSuspendedThread(match);
    }
    
    /**
     * Take the given code string and compile it into the run-target.
     * Then run the run-target 10 times recording the execution times
     * for running and unwinding.  After executing 10 times the fastest and
     * slowest times are discarded and the remaining 8 are averaged and 
     * displayed.
     * @param code String
     * @param resetMachineStateBetweenRuns whether to reset the machine state between runs
     */
    private void performanceTest (String code, boolean resetMachineStateBetweenRuns) {
        iceLogger.log(Level.INFO, "Running performance test.  Press 'Enter' to interrupt.");

        PerformanceTestResults summarizedResults = runPerformanceTest(code, resetMachineStateBetweenRuns);
        displayPerformanceTestResults(summarizedResults);
    }

    /**
     * Run a benchmark script.
     * @param fileName
     * @return true if safe to continue
     * @throws QuitException
     */
    private boolean runScript (String fileName) throws QuitException {
        String location = null;
        try {
            location = java.net.URLDecoder.decode(fileName, "UTF-8");    
        } catch (UnsupportedEncodingException e) {
            iceLogger.log(Level.INFO, "CAL: Unable to decode " + location + ": " + e.toString());
            return true;
        }

        iceLogger.log(Level.INFO, "\nRunning script: " + location + " ...");
        
        File file = new File (location);
        
        if (!file.exists()) {
            iceLogger.log(Level.INFO, "Benchmark script file " + fileName + " does not exist");
            return true;
        }
        
        try {
            FileInputStream fis = new FileInputStream (file);
            Reader r1 = TextEncodingUtilities.makeUTF8Reader(fis);
            BufferedReader bf = new BufferedReader (r1);
            boolean inComment = false;

            while (true) {
                String nextCommand = bf.readLine();
                if (nextCommand == null) {
                    break;
                }
                nextCommand = nextCommand.trim();
                if (nextCommand.length() == 0 || nextCommand.startsWith("//")) {
                    continue;
                }
                if (nextCommand.startsWith("/*")) {
                    inComment = true;
                }
                
                if (!inComment) {
                    executeCommand(nextCommand, true);
                }
                
                if (nextCommand.endsWith("*/")) {
                    inComment = false;
                }
            }            
        } catch (QuitException e) {
            // Do nothing.  This is normal termination so just re-throw.
            throw e;
        } catch (FileNotFoundException e) {
            iceLogger.log(Level.INFO, "File " + fileName + " does not exist.");
        } catch (IOException e1) {
            iceLogger.log(Level.INFO, "Error loading file: " + fileName);
        } catch (Exception e) {
            iceLogger.log(Level.INFO, "Error running script " + fileName + ": " + e.getLocalizedMessage());
        }
        
        return true;
    }
        
    /**
     * Take the given code, make it into a super combinator
     * and compile it into our program.
     * Original Author: rcypher
     * @param expressionText String A string containing the CAL expression to be run.
     * @param wrapExpression
     * @return the EntryPoint for the given expression
     */    
    protected EntryPoint makeRunTarget (String expressionText, boolean wrapExpression) {
        
        synchronized (this.workspaceManager) {
            
            String scDef = makeRunTargetDefinition(expressionText, wrapExpression);
            
            if (scDef == null) {
                return null;
            }
            
            statusListener.setSilent(true);
            EntryPoint entryPoint = compileAdjunct(scDef);
            statusListener.setSilent(false);
            return entryPoint;
        }
    }

    /**
     * Take the given code, make it into a super combinator
     * and compile it into our program.
     * Original Author: rcypher
     * @param expressionText String A string containing the CAL expression to be run.
     * @param wrapExpression
     * @return the definition of the run target
     */
    private String makeRunTargetDefinition(String expressionText, boolean wrapExpression) {
        
        synchronized (this.workspaceManager) {
            
            
            String scDef = qualifyCodeExpression(expressionText);
            if (scDef == null) {
                return null;
            }
            
            lastCode = scDef;
            
            if (wrapExpression) {
                //add newlines to get column positions in the error messages correctly                
                scDef = targetName + " = \n" + scDef + "\n;";
            }
            return scDef;
        }
    }
    
    protected String qualifyCodeExpression (String expressionText) {
        // Qualify unqualified symbols in code, if unambiguous
        CompilerMessageLogger logger = new MessageLogger();
        CodeAnalyser analyser = new CodeAnalyser(getTypeChecker(), getWorkspaceManager().getModuleTypeInfo(targetModule), false, false);
        CodeAnalyser.QualificationResults qualificationResults = analyser.qualifyExpression(expressionText, null, null, logger, true);
        if (qualificationResults == null){
            outputStream.println("Attempt to qualify expression has failed because of errors: ");
            dumpCompilerMessages(logger);
            return null;
        }
        String scDef = qualificationResults.getQualifiedCode();
        return scDef;
    }
    
    private EntryPoint compileAdjunct (String scDef) {
        if (this.workspaceManager == null) {
            return null;
        }
        
        synchronized (this.workspaceManager) {
            CompilerMessageLogger ml = new MessageLogger ();
            
            // Compile it, indicating that this is an adjunct
            EntryPoint targetEntryPoint = 
                getCompiler().getEntryPoint(
                    new AdjunctSource.FromText(scDef),
                    EntryPointSpec.make(QualifiedName.make(targetModule, targetName), new InputPolicy[]{}, OutputPolicy.DEFAULT_OUTPUT_POLICY), 
                        targetModule,
                        ml);
                    
            if (targetEntryPoint == null) {        
                iceLogger.log(Level.INFO, "CAL: Compilation unsuccessful because of errors:");
                // Write out compiler messages
                List<CompilerMessage> compilerMessages = ml.getCompilerMessages();
                for (final CompilerMessage message : compilerMessages) {
                    iceLogger.log(Level.INFO, "  " + message.toString());
                }
                return null;
            } 
            
            return targetEntryPoint;
        }
    }   
    
    /**
     * Instantiate this object's workspace manager member.
     * @return whether the member was successfully instantiated with the current settings.
     */
    private boolean createWorkspaceManager () {
        
        String clientID = WorkspaceConfiguration.getDiscreteWorkspaceID(DEFAULT_WORKSPACE_CLIENT_ID);
        
        workspaceManager = WorkspaceManager.getWorkspaceManager(clientID);
        statusListener = new ICEStatusListener();
        workspaceManager.addStatusListener(statusListener);
        
        return true;
    }
    
    /**
     * Set the workspace location.
     *   This object's stream provided will be set according to the given location.
     *   If invalid, it will be set to null.
     * 
     * @param newWorkspaceLocation a string representing the workspace location.
     */
    private void setWorkspaceLocation(String newWorkspaceLocation) {
        // Get the workspace definition file.
        File workspaceDefinitionFile = new File(newWorkspaceLocation);
        if (!workspaceDefinitionFile.exists()) {
            workspaceDefinitionFile = WorkspaceLoader.fileFromPath(newWorkspaceLocation);
        }
        
        if (workspaceDefinitionFile != null) {
            final File file = workspaceDefinitionFile;
            
            // Set the stream provider.
            streamProvider = new WorkspaceDeclaration.StreamProvider() {

                public String getName() {
                    return file.getName();
                }

                public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
                    try {
                        return new FileInputStream(file);
                        
                    } catch (FileNotFoundException e) {
                        status.add(new Status(Status.Severity.ERROR, "Could not access file: " + getName(), e));
                        return null;
                    }
                }

                public String getLocation() {
                    return file.getAbsolutePath();
                }
                
                public String getDebugInfo(VaultRegistry vaultRegistry) {
                    return "from file: " + file.getAbsolutePath();
                }
            };
            
        } else {
            // Couldn't get an appropriate location.
            iceLogger.log(Level.INFO, "CAL: Unable to create access workspace at: " + newWorkspaceLocation);
            streamProvider = null;
        }
        
    }
    
    /**
     * Builds the program object using the CAL file at the current location.
     * Create a new workspace if one does not already exist.  Does nothing if this fails.
     * @param initialize
     * @param dirtyOnly
     * @param forceCodeRegen
     * @return true if compilation succeeded, false if it failed
     */    
    private boolean compileWorkspace(boolean initialize, boolean dirtyOnly, boolean forceCodeRegen) {
        // The workspace manager must exist..
        if (getWorkspaceManager() == null || streamProvider == null) {
            return false;
        }
        
        // Calculate the new class size if:
        // - there are class files generated, 
        // - there is a repository, and
        // - either force code regen is on, or the repository starts off empty.
        ProgramResourceRepository repository = workspaceManager.getRepository();
        boolean calcClassSize = 
            getMachineType() == MachineType.LECC &&
            repository != null && (forceCodeRegen || repository.isEmpty());
        
        synchronized (this.workspaceManager) {
            CompilerMessageLogger ml = new MessageLogger();
            
            if (initialize) {
                // Init and compile the workspace.
                Status initStatus = new Status("Init status.");
                workspaceManager.initWorkspace(streamProvider, initStatus);
                
                if (initStatus.getSeverity() != Status.Severity.OK) {
                    ml.logMessage(initStatus.asCompilerMessage());
                }
            }
            
            ModuleLoadListener moduleLoadListener = new ModuleLoadListener();
            
            long startCompile = System.currentTimeMillis();
            
            // If there are no errors go ahead and compile the workspace.
            if (ml.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0) {
                WorkspaceManager.CompilationOptions options = new WorkspaceManager.CompilationOptions();
                options.setForceCodeRegeneration(forceCodeRegen);
                if (!forceCodeRegen && !dirtyOnly && !initialize) {
                    options.setIgnoreCompiledModuleInfo(true);
                }

                workspaceManager.compile(ml, dirtyOnly, moduleLoadListener, options);
            }
            
            long compileTime = System.currentTimeMillis() - startCompile;
            
            if (ml.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                // Errors
                iceLogger.log(Level.INFO, "CAL: Compilation unsuccessful because of errors:");
                calcClassSize = false;
            } else {
                // Compilation successful
                iceLogger.log(Level.INFO, "CAL: Compilation successful");
            }
            
            // Write out compiler messages
            List<CompilerMessage> errs = ml.getCompilerMessages();
            for (final CompilerMessage err : errs) {
                iceLogger.log(Level.INFO, "  " + err.toString());
            }
            
            // DIAG
            iceLogger.log(Level.INFO, "CAL: Finished compiling in " + compileTime + "ms");
            
            if (!initialize) {
                changeToPreferredWorkingModule();
            }
            
            displayModulesLoadedStats(moduleLoadListener.getLoadedModuleNames());
            
            if (calcClassSize && getMachineType() == MachineType.LECC) {
                command_classFileSize(true, false);
            }

            // If we have initialized a new workspace we need to reset the run state.
            // i.e. we need to discard the current execution context, executor, etc.
            if (initialize) {
                resetRunState();
            }
            
            
            return (ml.getNErrors() == 0);
        }
    }

    /**
     * Reset any state that ICE is holding that is specific
     * to the state of the runtime.
     * This is necessary when we switch workspaces as the held
     * state is referring to a program which is no longer valid.
     */
    private void resetRunState () {
        
        // We want to preserve the settings in the execution context
        // which relate to tracing and breakpoints.
        
        // Get the current execution context.
        ExecutionContextImpl oldExecutionContext = getExecutionContext();
        
        // Discard the FunctionRunThread.  This holds the executor, execution
        // context, etc.
        runThread  = null;
        
        // Get a new execution context.
        ExecutionContextImpl newExecutionContext = getExecutionContext();
        
        // Update the new execution context with settings from the old one.
        newExecutionContext.setBreakpoints(oldExecutionContext.getBreakpoints());       
        
        newExecutionContext.setTracedFunctions(oldExecutionContext.getTracedFunctions());        
        
        newExecutionContext.setTracingEnabled(oldExecutionContext.isTracingEnabled());
        newExecutionContext.setTraceShowsFunctionArgs(oldExecutionContext.traceShowsFunctionArgs());
        newExecutionContext.setTraceShowsThreadName(oldExecutionContext.traceShowsThreadName());
    }

    /**
     * Change to the preferred working module if it's not the current module, and it exists.
     */
    private void changeToPreferredWorkingModule() {
        if (preferredWorkingModuleName != null && 
                !preferredWorkingModuleName.equals(targetModule) && 
                getWorkspaceManager().hasModuleInProgram(preferredWorkingModuleName)) {
            
            targetModule = preferredWorkingModuleName;
            iceLogger.log(Level.INFO, "\nModule set to: " + targetModule);
            
            // update the working module if it no longer exists (eg. because of compile failure..)
        } else if (!getWorkspaceManager().hasModuleInProgram(targetModule) && !NO_MODULE.equals(targetModule)) {
            ModuleName oldTargetModule = targetModule;
            targetModule = getDefaultWorkingModuleName();
            iceLogger.log(Level.INFO, "\nSpecified module " + oldTargetModule + " is unavailable. Module set to: " + targetModule);
        }
    }
    
    /**
     * Display summary info about a set of modules which have been loaded.
     * @param loadedModuleNames the names of the loaded modules.
     */
    private void displayModulesLoadedStats(Set<ModuleName> loadedModuleNames) {
        int nLoadedModules = loadedModuleNames.size();
        
        if (getMachineType() == MachineType.LECC) {
            int nStatic = 0;
            for (final ModuleName moduleName : loadedModuleNames) {
                if (((LECCModule)getWorkspaceManager().getModuleTypeInfo(moduleName).getModule()).shouldLookupClassData()) {
                    nStatic++;
                }
            }
            iceLogger.log(Level.INFO, "CAL: " + nLoadedModules + " modules loaded (" + (nLoadedModules - nStatic) + " as dynamic, " + nStatic + " as static).");
            
        } else {
            iceLogger.log(Level.INFO, "CAL: " + nLoadedModules + " modules loaded.");
        }
    }
    
    /**
     * @return the size and count of all the class files in the program resource repository.
     */
    private SizeAndCount calcRepositorySize() {
        long size = 0;
        long count = 0;
        
        ProgramResourceRepository resourceRepository = getWorkspaceManager().getRepository();
        ModuleName[] repositoryModuleNames = resourceRepository.getModules();
        for (final ModuleName moduleName : repositoryModuleNames) {
            SizeAndCount moduleSizeAndCount = calcClassFileSize(new ProgramResourceLocator.Folder(moduleName, ResourcePath.EMPTY_PATH));
            
            size += moduleSizeAndCount.size;
            count += moduleSizeAndCount.count;
        }
        
        return new SizeAndCount(size, count);
    }
    
    /**
     * Recursively walk through the directory tree adding up the size and count of all .class files contained therein.
     * 
     * @param resourceLocator the resource locator to walk.
     * @return the the number of class files descending from the resource, and the size of the class files in the resource, in bytes
     */    
    private SizeAndCount calcClassFileSize (ProgramResourceLocator resourceLocator) {
        long size = 0;
        long count = 0;
        
        if (resourceLocator instanceof ProgramResourceLocator.File) {
            String resourceName = resourceLocator.getName();
            if (resourceName.endsWith(".lc") || resourceName.endsWith(".class")) {
                size += getWorkspaceManager().getRepository().getSize((ProgramResourceLocator.File)resourceLocator);
                count++;
            }
        } else {
            ProgramResourceLocator.Folder folder = (ProgramResourceLocator.Folder)resourceLocator;
            ProgramResourceLocator[] members = getWorkspaceManager().getRepository().getMembers(folder);

            if (members != null) {
                for (int i = 0; i < members.length; ++i) {
                    SizeAndCount contentsSizeAndCount = calcClassFileSize(members[i]);
                    size += contentsSizeAndCount.size;
                    count += contentsSizeAndCount.count;
                }
            }
        }
        
        return new SizeAndCount(size, count);
    }

    /**
     * A simple class to encapsulate numbers for size and count.
     */
    private static final class SizeAndCount {
        final long size;
        final long count;
        
        SizeAndCount(long size, long count) {
            this.size = size;
            this.count = count;
        }
    }
    
    private void showCommandLineOptions () {
        iceLogger.log(Level.INFO, " ");
        iceLogger.log(Level.INFO, "ICE command line options:");
        iceLogger.log(Level.INFO, "-mt <machine type>");
        iceLogger.log(Level.INFO, "-workspace <workspace file location>");
        iceLogger.log(Level.INFO, "-script <script file location>");
        iceLogger.log(Level.INFO, "-output <file location to copy output to>");
        iceLogger.log(Level.INFO, " ");
    }
    
    
    /**
     * helper method for displaying syntax and description of each command
     * @param command
     */
    private void showICECommandsHelper (ICECommand command) {
        if (getExecutionContext().hasSuspendedThreads() && !command.isValidWhileCALExecutionSuspended()) {
            return;
        }
        logInfo(command.showCommand());              
    }
    
    /**
     * display the syntax and description of all commands in the given group. 
     * @param command - the group to be displayed (should match the Command types regardless of case)
     */
    private void showICEGroupsHelper (CommandType command){

        if (commandTypesList.contains(command)){
            iceLogger.log(Level.INFO, "");

            // handle special commandTypes here
            // for 'OPTIONS' case display settings help and command line options, 
            // for 'ALL' case display help for all command topics except all itself
            // for all other groups display commands
            if (command == CommandType.OPTIONS){
                showCommandLineOptions ();
                showEnvironmentSettingsHelp();

            } else if (command == CommandType.ALL){
                for (final CommandType nextCommandType : commandTypesList) {
                    if (nextCommandType != CommandType.ALL) {
                        showICEGroupsHelper(nextCommandType);
                    }
                }   

            } else { 
                String groupName = command.getName();
                
                // The display name is the group name with the first character upper cased.
                String groupDisplayName = groupName.substring(0, 1).toUpperCase() + groupName.substring(1);
                logInfo(groupDisplayName + " Commands: ");

                for (final ICECommand ci : commands) {
                    if (ci.getCommandType() == command){
                        showICECommandsHelper(ci);
                    }
                }
                logInfo("");
            }
        }
    }

    /**
     * Display the descriptions for the the command group based on the argument. 
     * @param argument  either "all" or one of the command group names. If no argument, show only the General command and history group.
     */
    private void showICECommands (CommandType argument) {

        //if no argument was given, display general and history commands plus addition help info
        if (argument == null){  
            showICEGroupsHelper(CommandType.GENERAL);
            showICEGroupsHelper(CommandType.HISTORY);

            iceLogger.log(Level.INFO, "");
            logInfo("For more help, try: ");
            logInfo(":h all             show all commands ");
            logInfo(":h general         general commands");
            logInfo(":h info            commands to describe the current environment");
            logInfo(":h find            find entities in the workspace");
            logInfo(":h workspace       program and workspace manipulation");
            logInfo(":h debug           debugging commands");
            logInfo(":h benchmark       benchmarking commands");
            logInfo(":h CALDoc          generate and show CALDoc");
            logInfo(":h refactor        commands to refactor the source code");
            logInfo(":h utilities       utilities to generate code, cal archives, etc."); 
            logInfo(":h history         commands to show or execute previous commands");
            logInfo(":h options         command line options and environment setting help");
            logInfo(":h other           other commands"); 

        } else {
            showICEGroupsHelper (argument);
        }
    }

    private void showEnvironmentSettingsHelp () {
        logWrappedEnvironmentSettingsName ("ICE Environment settings (set using -D VM arguments):");

        logWrappedEnvironmentSettingsName (ICE_PROP_WORKSPACE);
        logWrappedEnvironmentSettingsDescription ("Specify the location of the workspace definition as a fully-qualified pathname.");


        logWrappedEnvironmentSettingsName (ICE_PROP_WORKSPACE_FILENAME);
        logWrappedEnvironmentSettingsDescription ("Specify the location of the workspace definition as an unqualified filename.");

        logWrappedEnvironmentSettingsName (ICE_PROP_MODULE);
        logWrappedEnvironmentSettingsDescription ("Specify the initial working module. ");

        logWrappedEnvironmentSettingsName (MachineConfiguration.MACHINE_TYPE_PROP);
        logWrappedEnvironmentSettingsDescription ("Specify the machine type to use. i.e. g or lecc. ");

        logWrappedEnvironmentSettingsName (EntryPointGenerator.SHOW_ADJUNCT_PROP);
        logWrappedEnvironmentSettingsDescription ("If this is defined the generated I/O code for the executing expression will be displayed. ");

        logWrappedEnvironmentSettingsName (MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP);
        logWrappedEnvironmentSettingsDescription ("Defining this causes a trace statement to be put at the beginning of each generated function. " +                   
        "Also this enables the ability to set breakpoints on CAL functions and examine the program state when suspended.");

        logWrappedEnvironmentSettingsName (Packager.OPTIMIZER_LEVEL);
        logWrappedEnvironmentSettingsDescription ("If this is defined as level 1, the CAL Global Optimizer is enabled. To disable the optimizer set the level to 0. By default the optimizer is off");

        if (getMachineType() == MachineType.LECC) {

            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.GEN_BYTECODE_PROP);
            logWrappedEnvironmentSettingsDescription ("If this is defined the lecc machine will generate Java source code, otherwise Java byte-code is directly generated.");

            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.GEN_STATISTICS_PROP);
            logWrappedEnvironmentSettingsDescription ("Defining this generates runtime statistics for the lecc machine. Reduction count, function count, etc.");

            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.GEN_CALLCOUNTS_PROP);
            logWrappedEnvironmentSettingsDescription ("Defining this generates runtime statistics for function call frequency.");

            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.OUTPUT_DIR_PROP);
            logWrappedEnvironmentSettingsDescription ("The value of this variable determines the destination of generated lecc files (e.g. source or byte code).");

            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.CODE_GENERATION_STATS_PROP + " [module name]");
            logWrappedEnvironmentSettingsDescription ("Defining this displays information about generated code e.g. # of optimized applications, # of switches, etc.");

            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.DEBUG_INFO_PROP);
            logWrappedEnvironmentSettingsDescription ("Defining this will cause a stack trace to be dumped to the console when Prelude.error is called.");

            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.STATIC_BYTECODE_RUNTIME_PROP);
            logWrappedEnvironmentSettingsDescription ("If this is defined and bytecode generation enabled, the lecc machine will have runtime class files generated to and loaded from disk.");

            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.NON_INTERRUPTIBLE_RUNTIME_PROP);
            logWrappedEnvironmentSettingsDescription ("If this is defined, the lecc runtime will not check whether a quit has been requested.");
            
            logWrappedEnvironmentSettingsName (LECCMachineConfiguration.USE_LAZY_FOREIGN_ENTITY_LOADING_PROP);
            logWrappedEnvironmentSettingsDescription ("If this is defined, foreign entities corresponding to foreign types and foreign functions will be loaded eagerly during deserialization.");

            logWrappedEnvironmentSettingsName(LECCMachineConfiguration.CONCURRENT_RUNTIME_PROP);
            logWrappedEnvironmentSettingsDescription("If this is defined, LECC runtime supports concurrent reduction of CAL programs on a single execution context.");

        } else if (getMachineType() == MachineType.G) {
            logWrappedEnvironmentSettingsName (GMachineConfiguration.RUNTIME_STATISTICS_PROP);
            logWrappedEnvironmentSettingsDescription ("Defining this generates runtime statistics for the G-machine. e.g. instruction count.");

            logWrappedEnvironmentSettingsName (GMachineConfiguration.CALL_COUNTS_PROP);
            logWrappedEnvironmentSettingsDescription ("Defining this generates runtime statistics for function call frequency.");

        }
    }
    
    /**
     * @param str - String to wrap
     * @param max - the maximum width of the console
     * @param prefix  - number of spaces for the indentation
     * @return input string broken down into a block of substrings with indentations and each string
     *         length is less than max minus prefix; words are wrapped. 
     */
    private String getWrappedString(String str, int max, int prefix){
        int allowedLength = max - prefix;
        String inputStr = str;
        StringBuilder buf = new StringBuilder();

        //generates padding for indentation
        StringBuilder makePadding = new StringBuilder();
        for (int i = 0; i < prefix; i++ ){
            makePadding.append(" ");
        }
        String padding = makePadding.toString();

        //if no wrapping is necessary, return the string with padding
        if (inputStr.length() <= allowedLength){
            buf.append(padding);
            buf.append(inputStr);
            return buf.toString();
        }

        int charCount = inputStr.length();
        int begIndex = 0;
        int endIndex = allowedLength - 1;

        // to iterate through the length of the string until length of all substrings  
        // are less than MAX. Substrings are separated with end of line and padding
        while (charCount > allowedLength){
            //finding white space to wrap to the next line
            while (inputStr.charAt(endIndex) != ' ' && endIndex > begIndex) {
                endIndex --;
            }
            buf.append(padding);
            buf.append(inputStr.subSequence(begIndex, endIndex));
            buf.append("\n");

            charCount -= endIndex - begIndex +1;
            begIndex = endIndex +1;
            endIndex = begIndex + allowedLength;
        }
        //to add indentation to the last line
        if (charCount != allowedLength){
            buf.append(padding);
            buf.append(inputStr.substring(begIndex));
        }
        return buf.toString();
    } 
    
    private void logInfo(final String logString) {
        iceLogger.log(Level.INFO, logString);
    }
    
    /** helper method to log wrapped line for environment settings name
     * 
     * @param noIndentLine
     */
    private void logWrappedEnvironmentSettingsName(String noIndentLine){
        logInfo(getWrappedString (noIndentLine, MAX_WIDTH, 0));
    }
    
    /** helper method to log wrapped line for environment settings description
     * 
     * @param indentedLine
     */
    private void logWrappedEnvironmentSettingsDescription(String indentedLine){
        logInfo(getWrappedString (indentedLine, MAX_WIDTH, ENVIRONMENT_SETTING_HELP_DESCRIPTION_INDENT));
    }

    /**
     * Display the machine code for all super combinators.
     */
    private void disassembleAll () {
        if (getMachineType() != MachineType.G) {
            logInfo("Disassembly is not supported for the " + getMachineTypeAsString() + " machine.");
            return;
        }

        ModuleName[] moduleNames = getWorkspaceManager().getModuleNamesInProgram();
        for (final ModuleName element : moduleNames) {
            Module module = getWorkspaceManager().getModuleTypeInfo(element).getModule();                         

            Collection<MachineFunction> functions = module.getFunctions();
            for (final MachineFunction function : functions) {                
                disassemble (function);
                logInfo("\n");
            }
        }
    }

    /**
     * Display the machine code for the given named function.
     * If no module is specified in the name the prelude module
     * is used.
     * @param function String containing the, possibly fully qualified, function name.
     */
    private void disassemble (String function) {
        if (function == null || 
            function.length() == 0) {
            return;
        }
        
        MachineFunction cl;
        try {
            cl = getWorkspaceManager().getMachineFunction(EntryPointImpl.makeEntryPointName(function));
        } catch (Exception e) {
            logInfo(function + " is not a valid function name.");
            return;
        }
        
        disassemble (cl);
    }
    
    private void disassemble (MachineFunction label) {
        if (getMachineType() != MachineType.G) {
            logInfo("Disassembly is not supported for the " + getMachineTypeAsString() + "machine.");
            return;
        }
        
        try {
            logInfo("Disassembly of " + label + ":");
            logInfo(label.getDisassembly());
        } catch (NullPointerException e) {
            // Label lookup failed (offset was null) - but we don't much care!
            logInfo("Unable to find function: " + label);
            return;
        }

    }    
    
    /** 
     * Show the state of the suspended CAL program.
     * Currently this displays the function name and the 
     * names and values of any arguments. 
     */
    private void command_showSuspensionState () {
        ExecutionContextImpl ec = getExecutionContext();
        Map<Thread, SuspensionState> suspensions = ec.getThreadSuspensions();
        if (suspensions.isEmpty()) {
            logInfo("There are no currently suspended threads.");
            return;
        }
        
        Thread currentThread = getDebugController().getCurrentSuspendedThread();
        
        SuspensionState suspension = suspensions.get(currentThread);
        if (suspension == null) {
            logInfo("The current thread is no longer suspended. Change to another thread.");
            return;
        }
        logInfo("Execution suspended in: " + suspension.getFunctionName());
        String[] argNames = suspension.getArgNames();
        CalValue[] argValues = suspension.getArgValues();
        
        if(argNames.length > 0) {
            String argValueStrings[] = DebugSupport.showInternal(argValues); 
            logInfo("Argument values:");
            for (int i = 0, n = argNames.length; i < n; ++i) {
                logInfo("    " + argNames[i] + " - " + argValueStrings[i]);
            }
        }
        logInfo("");
    }
    
    private void command_terminateExecution (String info) {
        // Set the quit flag for the CAL execution thread and then 
        // let it resume execution. This lets the CAL program terminate
        // gracefully.
        ExecutionContextImpl ec = getExecutionContext();
        
        // Remember the state of tracing and then turn it off.
        boolean traceOn = ec.isTracingEnabled();
        ec.setTracingEnabled(false);
        
        Set<String> breakpoints = ec.getBreakpoints();
        ec.clearBreakpoints();       
        
        Set<String> tracedFunctions = ec.getTracedFunctions();
        
        // We want to go into a loop here in case
        // we hit another breakpoint before the machine
        // notices that a quit has been requested.
        while (ec.hasSuspendedThreads()) {
            ec.setStepping(false);
            ICE.this.runThread.requestQuit();
            getDebugController().setShouldBlockUI(true);
            ec.resumeAll();
            getDebugController().blockUntilCompletionOrInterruption();
        }
        
        ec.setTracingEnabled(traceOn);
        ec.setTracedFunctions(tracedFunctions);  
        ec.setBreakpoints(breakpoints);             
    }
    
    /**
     * Step to the next CAL function for data constructor.
     * This will only be called if the system property 
     * org.openquark.cal.machine.debug_capable is defined.
     * @param info
     */
    private void command_step (String info) {

        ExecutionContextImpl ec = getExecutionContext();

        String expression = info.trim();
        if (expression.length() > 0) {
            // We want to start execution of the expression the step flag set.
            if (ec.hasSuspendedThreads()) {
                logInfo("There is a CAL program currently suspended.");
                logInfo("Terminate the suspended program with :te before stepping into a new program.");
            } else {
                ec.setStepAllThreads(true);
                ec.setStepping(true);
                command_runCode(info);
            }
        } else {
            // There is nothing following 'step'.  If we
            // are suspended tell the execution context to step
            // and then resume execution.
            if (ec.hasSuspendedThreads()) {
                ec.setStepAllThreads(false);
                ec.setStepping(true);
                command_resumeCurrentThread(true);
            } else {
                logInfo("There is no CAL program currently suspended.");
            }
        }
    }
    
    /**
     * Show different pieces of information about a suspended CAL execution.
     * @param info
     */
    private void command_show (String info) {
        ExecutionContextImpl ec = getExecutionContext();
        if (!ec.hasSuspendedThreads()) {
            logInfo("There is no CAL program currently suspended.");
            return;
        }

        final Map<Thread, SuspensionState> suspendedThreads = ec.getThreadSuspensions();
        final Thread currentSuspendedThread = getDebugController().getCurrentSuspendedThread();
        if (!suspendedThreads.containsKey(currentSuspendedThread)) {
            iceLogger.log(Level.INFO, "The current thread is no longer suspended. Change to another thread.");
            return;
        }
        
        SuspensionState suspension = suspendedThreads.get(currentSuspendedThread);
        
        // Get first word from info.
        StringTokenizer tokenizer = new StringTokenizer(info);
        try {
            String firstWord = tokenizer.nextToken();
            String action = firstWord.toLowerCase();
            
            // :show function
            if (action.equals("function")) {
                // Show the name of the suspended function.
                logInfo("    " + suspension.getFunctionName());
            } else
            // :show argnames  or  :show argtypes
            if (action.equals("argnames") || action.equals("argtypes")) {
                // Display the argument names/types
                String[] argNames = suspension.getArgNames();
                String[] argTypes = suspension.getArgTypes();
                if (argNames.length == 0) {
                    logInfo("    " + suspension.getFunctionName() + " has no arguments.");
                } else {
                    for (int i = 0; i < argNames.length; ++i) {
                        logInfo("    " + argNames[i]  + " - " + argTypes[i]);
                    }
                }
            } else
            // :show stack [expanded]
            if (action.equals("stack")) {
                // Show the suspended call stack.
                // By default this is limited to only showing generated CAL functions.
                // Specifying expanded shows everything in the suspended call stack.
                try {
                    String d = tokenizer.nextToken().toLowerCase();
                    if (d.equals("expanded")) {
                        StackTraceElement[] stackTrace = suspension.getStackTrace();
                        for (int i = 0; i < stackTrace.length; ++i) {
                            logInfo(stackTrace[i].toString());
                        }
                    } else {
                        iceLogger.log (Level.INFO, "Unrecognized directive: " + d);
                    }
                } catch (NoSuchElementException e) {
                    // There were no tokens after 'stack' so do the default behaviour.
                    StackTraceElement[] stackTrace = suspension.getStackTrace();
                    for (int i = 0; i < stackTrace.length; ++i) {
                        StackTraceElement ste = stackTrace[i];
                        if (ste.getClassName().indexOf("cal_") >= 0) {
                            logInfo(stackTrace[i].toString());
                        }
                    }
                    
                }
            } else {
                // :show <argument name>
                // Determine which argument and show the name, type, and value.
                String[] argNames = suspension.getArgNames();
                CalValue[] argValues = suspension.getArgValues();
                String[] argTypes = suspension.getArgTypes();
                
                for (int i = 0; i < argNames.length; ++i) {
                    if (argNames[i].equals(firstWord)) {
                        logInfo("    " + argNames[i] + " - " + argTypes[i] + " - " + DebugSupport.showInternal(argValues[i]));
                        return;
                    }
                }
                iceLogger.log (Level.INFO, firstWord + " is not a valid argument name.");
            }
            
        } catch (NoSuchElementException e) {
            // There is nothing following 'show'.
            command_showSuspensionState();
        }
        
        
    }

    /**
     * Manage breakpoints in the CAL program.
     * @param info
     */
    private void command_breakpoint (String info) {
        ExecutionContextImpl ec = getExecutionContext();
        
        // Get first word from info.
        StringTokenizer tokenizer = new StringTokenizer(info);

        try {
            String firstWord = tokenizer.nextToken();
            
            // :bp show
            // Display the set breakpoints.
            if (firstWord.toLowerCase().equals("show")) {
                Set<String> bps = ec.getBreakpoints();
                if (bps.isEmpty()) {
                    logInfo("No breakpoints currently set.");
                    return;
                }
                logInfo("Breakpoints: ");
                for (final String name : bps) {
                    logInfo("    " + name);
                }
                return;
            } else
            if (firstWord.toLowerCase().equals("clear")) {
                ec.clearBreakpoints();
                logInfo("All breakpoints removed.");
                return;
            }
            
            String functionName = firstWord;
            
            // Determine if function name is a valid CAL function or data constructor. 
            try {
                QualifiedName qn = resolveQualifiedNameInProgram(QualifiedName.makeFromCompoundName(functionName));
                if (qn == null) {
                    return;
                }
                functionName = qn.getQualifiedName();
                
                ModuleTypeInfo mt = getWorkspaceManager().getModuleTypeInfo(qn.getModuleName());
                
                DataConstructor dc = mt.getDataConstructor(qn.getUnqualifiedName());
                if (dc != null) {
                    if (dc.getArity() == 0) {
                        logInfo("Cannot set a breakpoint on a zero arity data constructor.");
                        return;
                    }
                } else  
                if (mt.getFunction(qn.getUnqualifiedName()) == null) {
                    logInfo(qn + " is not a valid function name.");
                    return;
                }
                    
            } catch (IllegalArgumentException e) {
                logInfo(functionName + " is not a valid compound name.");
                return;
            }
            
            // functionName is valid so we want to toggle its breakpoint.
            Set<String> breakpoints = new HashSet<String>(ec.getBreakpoints());
            if (breakpoints.contains(functionName)) {
                breakpoints.remove(functionName);
                ec.setBreakpoints(breakpoints);
                logInfo("Breakpoint disabled for " + functionName);
            } else {
                breakpoints.add(functionName);                
                ec.setBreakpoints(breakpoints);
                logInfo("Breakpoint enabled for " + functionName);
            }
                
            
        } catch (NoSuchElementException e) {
            logInfo("Unrecognized breakpoint command  :bp " + info);
        }
        
    }
    
    /**
     * 
     * @return the execution context that CAL programs will execute in.
     */
    private ExecutionContextImpl getExecutionContext () {
        makeRunThread();
        return this.runThread.getExecutionContext();
    }
    
    /**
     * @return the debug controller.
     */
    private FunctionRunThreadBase.DebugController getDebugController() {
        makeRunThread();
        return this.runThread.getDebugController();
    }
    
    /**
     * Change the settings which control the behaviour of function tracing.
     * Note: for these changes to have effect the system property
     * org.openquark.cal.machine.debug_capable must be defined.
     * @param info - the content of the command line after the command text is stripped.
     */
    private void command_trace (String info) {
        ExecutionContextImpl ec = getExecutionContext();
        
        // Get first word from info.
        StringTokenizer tokenizer = new StringTokenizer(info);

        try {
            String firstWordRaw = tokenizer.nextToken();
            String firstWord = firstWordRaw.toLowerCase();
            
            // :trace on
            if (firstWord.equals("on")) {
                ec.setTracingEnabled(true);
                logInfo("General function tracing enabled.");
            } else
            // :trace off
            if (firstWord.equals("off")) {
                ec.setTracingEnabled(false);
                logInfo("General function tracing disabled.");
            } else
            // :trace threadname on/off
            if (firstWord.equals("threadname")) {
                String tf = tokenizer.nextToken().toLowerCase();
                if (tf.equals("on")) {
                    ec.setTraceShowsThreadName(true);
                    logInfo("Tracing of thread name enabled.");
                }else if (tf.equals("off")) {
                    ec.setTraceShowsThreadName(false);
                    logInfo("Tracing of thread name disabled.");
                }else {
                    logInfo("Unrecognized trace command  :trace " + info);
                }
            } else
            // :trace arguments on/off
            if (firstWord.equals("arguments")) {
                String tf = tokenizer.nextToken().toLowerCase();
                if (tf.equals("on")) {
                    ec.setTraceShowsFunctionArgs(true);
                    logInfo("Tracing of arguments enabled.");
                }else if (tf.equals("off")) {
                    ec.setTraceShowsFunctionArgs(false);
                    logInfo("Tracing of arguments disabled.");
                }else {
                    logInfo("Unrecognized trace command  :trace " + info);
                }
            } else
            if (firstWord.equals("clear")) {
                ec.clearTracedFunctions();
                logInfo("All traced functions removed.");
            } else {
                // :trace Module.function
                String functionName = firstWordRaw;
                try {
                    // Check that the function name is valid.
                    QualifiedName qn = resolveQualifiedNameInProgram(QualifiedName.makeFromCompoundName(functionName));
                    if (qn == null) {
                        return;
                    }
                    functionName = qn.getQualifiedName();
                    
                    try {
                        if(getWorkspaceManager().getMachineFunction(qn) == null) {
                            logInfo(qn.getQualifiedName() + " is not a valid function.");
                            return;
                        }
                    } catch (ProgramException e) {
                        logInfo(qn.getQualifiedName() + " is not a valid function.");
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    logInfo(functionName + " is not a valid compound name.");
                    return;
                }
                
                // The function name is valid so we want to toggle the trace state for it.               
                Set<String> tracedFunctions = new HashSet<String>(ec.getTracedFunctions());
                if (!tracedFunctions.contains(functionName)) {                    
                    tracedFunctions.add(functionName);                   
                    ec.setTracedFunctions(tracedFunctions);
                    logInfo("Tracing enabled for " + functionName);
                }else {
                    tracedFunctions.remove(functionName);
                    ec.setTracedFunctions(tracedFunctions);
                    logInfo("Tracing disabled for " + functionName);
                }
            }            
            
        } catch (NoSuchElementException e) {
            // We hit the end of the info when we were expecting another token.
            logInfo("Unrecognized trace command  :trace " + info);
        }
    }
    
    /**
     * Displays the current state of the function tracing settings.
     */
    private void command_showTraceSettings () {
        // First check to see if the build is trace capable.
        if (System.getProperty(MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP) == null) {
            logInfo("This build does not have tracing enabled.");
            logInfo("Please re-start with the system property  " + MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP +"  defined.");
            return;
        }
        
        
        // If we don't have the runThread yet we need to create it. 
        makeRunThread();
        ExecutionContextImpl ec = runThread.getExecutionContext();
        
        logInfo("");
        logInfo("Current trace settings:");
        logInfo("overall tracing is " + (ec.isTracingEnabled() ? "Enabled" : "Disabled"));
        logInfo("trace shows function args - " + ec.traceShowsFunctionArgs());
        logInfo("trace shows thread name   - " + ec.traceShowsThreadName());

        Set<String> tracedFunctions = ec.getTracedFunctions();
        logInfo(tracedFunctions.size() + " functions have tracing enabled.");
        for (final String tracedFunction : tracedFunctions) {
            logInfo("    " + tracedFunction);
        }
        
        List<Pattern> traceFilters = ec.getTraceFilters();
        logInfo(traceFilters.size() + " trace filters are set.");
        for (int i = 0, n = traceFilters.size(); i < n; ++i) {
            logInfo("    " + (i+1) + " :   " + traceFilters.get(i).toString());
        }
    }

    /**
     * Displays the current state of the various settings for ICE.
     */    
    private void command_showSettings () {
        logInfo("");
        logInfo("Current ICE settings:");
        logInfo("workspace =    " + streamProvider.getName());
        logInfo("module =       " + targetModule);
        logInfo("machine type = " + getMachineTypeAsString());
        logInfo("command history size = " + commandHistory.length);
        logInfo("# of runs for performance tests = " + nPerformanceRuns);
        logInfo("Suppression of output is " + (suppressOutput ? "on." : "off."));
        logInfo("Suppression of incremental machine stats is " + (suppressIncrementalMachineStats ? "on." : "off."));
        if (outputFileName.length () > 0) {
            logInfo("Output is being copied to: " + outputFileName);
        }
        if (benchmarkResultFileName != null && benchmarkResultFileName.length() > 0) {
            logInfo("Benchmark results file set to: " + benchmarkResultFileName);
            logInfo("Benchmark results label set to: " + benchmarkResultLabel);
        }
        
        logInfo("Environment settings: ");
        logInfo(definedStatusString(EntryPointGenerator.SHOW_ADJUNCT_PROP));
        logInfo(definedStatusString(MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP));            
        logInfo(Packager.OPTIMIZER_LEVEL + " => " + 
                (System.getProperty(Packager.OPTIMIZER_LEVEL) != null ?
                        System.getProperty(Packager.OPTIMIZER_LEVEL) : "not defined"));

        if (getMachineType() == MachineType.LECC) {
            logInfo(definedStatusString(LECCMachineConfiguration.GEN_BYTECODE_PROP));
            logInfo(definedStatusString(LECCMachineConfiguration.GEN_STATISTICS_PROP));
            logInfo(definedStatusString(LECCMachineConfiguration.GEN_CALLCOUNTS_PROP));
            logInfo(definedStatusString(LECCMachineConfiguration.CODE_GENERATION_STATS_PROP));
            logInfo(definedStatusString(LECCMachineConfiguration.DEBUG_INFO_PROP));
            logInfo(definedStatusString(LECCMachineConfiguration.STATIC_BYTECODE_RUNTIME_PROP));            
            logInfo(definedStatusString(LECCMachineConfiguration.NON_INTERRUPTIBLE_RUNTIME_PROP));
            logInfo(definedStatusString(LECCMachineConfiguration.USE_LAZY_FOREIGN_ENTITY_LOADING_PROP));
            logInfo(definedStatusString(LECCMachineConfiguration.CONCURRENT_RUNTIME_PROP));
            logInfo(LECCMachineConfiguration.OUTPUT_DIR_PROP+ " => " + 
                (System.getProperty(LECCMachineConfiguration.OUTPUT_DIR_PROP) != null ?
                    System.getProperty(LECCMachineConfiguration.OUTPUT_DIR_PROP) : "not defined"));
            logInfo("    Using output directory: " + getWorkspaceManager().getRepository().getLocationString());

        } else
        if (getMachineType() == MachineType.G) {
            logInfo(definedStatusString(GMachineConfiguration.RUNTIME_STATISTICS_PROP));
            logInfo(definedStatusString(GMachineConfiguration.CALL_COUNTS_PROP));
        }
    }
    
    /**     
     * @param propertyName 
     * @return a status string showing the property name, and whether it is defined.
     */
    private static String definedStatusString(String propertyName) {
        StringBuilder sb = new StringBuilder(propertyName);
        sb.append(" => ");
        if (System.getProperty(propertyName) != null) {
            sb.append("defined");
        } else {
            sb.append("not defined");
        }
        
        return sb.toString();                
    }

    /**
     * Parses the ICE command line.  This currently does a simple "if-then-else"
     * parse, nothing too fancy.  We could use a 'real' parser here someday.
     * @param args String[] - input -the command line arguments
     */
    private void parseCommandLine(final String[] args) {

        //iterate through the command-line args and extract info       
        for (int i = 0, nArgs = args.length; i < nArgs; i++) {
            final String thisArg = args[i].toUpperCase();

            if (!thisArg.startsWith("-")) {
                logInfo("Unrecognized command line option: " + thisArg + "\n");
                continue;
            }         
          
            if (i >= nArgs) {
                logInfo("Missing argument for command line option: " + thisArg + "\n");
                return;
            } 
            
            ++i;

            if (thisArg.equals("-MT")) {
                // Machine type setting                  
                System.setProperty(MachineConfiguration.MACHINE_TYPE_PROP, args [i]);
                               
            } else if (thisArg.equals("-WORKSPACE")) {
                // workspace to build                                               
                String newWorkspaceLocation = args [i];
                setWorkspaceLocation(newWorkspaceLocation); 
                
            } else if (thisArg.equals("-SCRIPT")) {
                // location of script to run on startup                                              
                scriptLocation = args [i];       
                
            } else if (thisArg.equals("-OUTPUT")) {
                // output file                                         
                changeOutputFile(args [i]);
                             
            } else {
                logInfo("Unrecognized command line option: " + thisArg + "\n");
                continue;
            }
                     
        }
    }

    /**
     * Take the given code string and compile it into the run-target.
     * Then run the run-target n times recording the execution times
     * for running and unwinding.  After executing 10 times the first run is
     * discarded due to potential class loading and code generation and
     * the remaining 9 are averaged.
     * @param code String
     * @param resetMachineStateBetweenRuns whether to reset the machine state between runs
     * @return PerformanceTestResults
     */
    private PerformanceTestResults runPerformanceTest (String code, boolean resetMachineStateBetweenRuns) {

        // Reset the execution time generator.
        executionTimeGenerator.reset();
        
        int nRuns = nPerformanceRuns;
                
        if (code == null || code.length () == 0) {
            return new PerformanceTestResults(Collections.<StatsGenerator.StatsObject>emptyList(), Collections.<MachineStatistics>emptyList());
        }

        String runTargetDef = makeRunTargetDefinition(code, true);
        if (runTargetDef == null) {
            return new PerformanceTestResults(Collections.<StatsGenerator.StatsObject>emptyList(), Collections.<MachineStatistics>emptyList());
        }
        
        statusListener.setSilent(true);
        EntryPoint entryPoint = compileAdjunct(runTargetDef);
        statusListener.setSilent(false);
        if (entryPoint == null) {
            return new PerformanceTestResults(Collections.<StatsGenerator.StatsObject>emptyList(), Collections.<MachineStatistics>emptyList());
        }

        List<MachineStatistics> machineStats = new ArrayList<MachineStatistics>();
        
        outputStream.println ("Executing " + nPerformanceRuns + " runs of: \"" + code + "\"");
        for (int i = 0; i < nRuns; ++i) {
            outputStream.println ("run: " + (i+1));

            // Need to reset program to remove any CAF results.
            if (resetMachineStateBetweenRuns) {
                
                resetMachineState();
                
                // we need to recreate the entry point because it would have been invalidated by resetting machine state
                statusListener.setSilent(true);
                entryPoint = compileAdjunct(runTargetDef);
                statusListener.setSilent(false);
                if (entryPoint == null) {
                    return new PerformanceTestResults(Collections.<StatsGenerator.StatsObject>emptyList(), Collections.<MachineStatistics>emptyList());
                }
            } else {
                resetCachedResults();
            }

            //we do not call System.runFinalization() or System.gc() within a performance test
            //rather, by taking the number of runs to be sufficiently large, garbage collection is
            //amortized over all runs. 
            //It is a fairer to include garbage collection time within the run since that is truly 
            //part of the cost of running the program. 
            //On my machine (P4 2GB 3MhZ running java 1.4.0_08 client) it also tends to produce lower
            //average benchmark times. Probably this is because explicit calls to System.runFinalization()
            //and System.gc() interfere with the normal operation and optimizations in the Java garbage
            //collector.
            //The main drawback is that not poking the garbage collector tends to produce more variability
            //in the times of each individual run within a single performance test. However, the *average*
            //of a sufficient number of runs shows good regularity.
                                
//            // Poke the jvm to try and proactively garbage collected any
//            // cached/memoised results that are now free.
//            logInfo("Forcing garbage collection.");
//            for (int j = 0; j < 10; ++j) {
//                //outputStream.println ("trying to free memory " + i);
//                System.runFinalization();
//                System.gc();
//            }           
            
            // Run the code, but return if it wasn't successful.
            if (!runFunction(entryPoint, false, false, false)) {
                return new PerformanceTestResults(Collections.<StatsGenerator.StatsObject>emptyList(), Collections.<MachineStatistics>emptyList());
            }
            
            try {
                if (inBuff.ready()) {
                    break;
                }
            } catch (Exception e) {
                /* Do nothing we simply interrupt evaluation*/
            }
                
            executionTimeGenerator.cacheRun();
            
            if (resetMachineStateBetweenRuns) {
                machineStats.add(workspaceManager.getMachineStatistics());
            }
        }

        List<StatsGenerator.StatsObject> summarizedStats = executionTimeGenerator.summarizeCachedRuns();

        if (benchmarkResultWriter != null) {
            try {
                benchmarkResultWriter.write(targetModule + ": " + code + "\r\n");
                benchmarkResultWriter.write(String.valueOf(1) + "\r\n");
                benchmarkResultWriter.flush();
            } catch (IOException e) {
                logInfo("Unable to write benchmark results to benchmark result stream: " + benchmarkResultFileName);
            }
        }
        
        return new PerformanceTestResults(summarizedStats, machineStats);
    }
    
    /** 
     * Display the results of a performance test to the output stream.
     * Also, if a benchmark results file is set, write the results to the
     * file.
     * @param performanceTestResults
     */
    private void displayPerformanceTestResults (PerformanceTestResults performanceTestResults) {
        for (int i = 0, n = performanceTestResults.getNExecutionStatsObjects(); i < n; i++) {
            StatsGenerator.StatsObject element = performanceTestResults.getNthExecutionStatsObject(i);
            String message = element.generateLongMessage();
            logInfo(message);
            
            if (element instanceof StatsGenerator.PerformanceStatsObject &&
                benchmarkResultWriter != null) {
                try {
                    StatsGenerator.PerformanceStatsObject perf = (StatsGenerator.PerformanceStatsObject)element;
                    
                    benchmarkResultWriter.write(String.valueOf(perf.getAverageRunTime()) + "\r\n");
                    benchmarkResultWriter.write(String.valueOf(perf.getMedianRunTime()) + "\r\n");
                    benchmarkResultWriter.write(String.valueOf(perf.getStdDeviation()) + "\r\n");
                    benchmarkResultWriter.write(String.valueOf(perf.getNRuns()) + "\r\n");
                    benchmarkResultWriter.write("<min>" + String.valueOf(perf.getMinTime()) + "</min>\r\n");
                    
                    for(int j=0; j< perf.getNRuns(); j++ ) {
                        benchmarkResultWriter.write("<sample>" + String.valueOf(perf.getRunTimes()[j]) + "</sample>\r\n");
                    }
                    
                    benchmarkResultWriter.write(benchmarkResultLabel + "\r\n");
                    benchmarkResultWriter.flush();
                } catch (IOException e) {
                    logInfo("Unable to write results to benchmark result file " + benchmarkResultFileName + ": " + e.getLocalizedMessage());
                }
            }
        }
        
        if (getMachineType() == MachineType.LECC) {
            int nMachineStatsObjects = performanceTestResults.getNMachineStatsObjects();
            for (int i = 0, n = nMachineStatsObjects; i < n; i++) {
                LECCMachineStatistics element = (LECCMachineStatistics)performanceTestResults.getNthMachineStatsObject(i);
                logInfo("Machine statistics for run " + i + ":\n" + element.toString());
            }
            
            if (nMachineStatsObjects > 1) {
                LECCMachineStatistics average = LECCMachineStatistics.getAverage(performanceTestResults.machineStats.subList(1, nMachineStatsObjects));
                logInfo("Average machine statistics (first run discarded):\n" + average.toString());
            }
        }
    }
    
//    private void displayPerformanceTestResultsBrief (List summarizedResults) {
//        for (Iterator it = summarizedResults.iterator(); it.hasNext();) {
//            StatsGenerator.StatsObject element = (StatsGenerator.StatsObject) it.next();
//            String message = element.generateShortMessage();
//            logInfo(message);
//        }
//    }
    
    private MachineType getMachineType() {
        return workspaceManager.getMachineType();
    }
    
    private String getMachineTypeAsString() {
        return getMachineType().toString();
    }
    
    /**
     * @return the compiler from the workspace manager.  Null if no workspace manager.
     */
    private Compiler getCompiler() {
        if (workspaceManager == null) {
            return null;
        }
        return workspaceManager.getCompiler();
    }
    
    /**
     * @return the type checker from the workspace manager.  Null if no workspace manager.
     */
    private TypeChecker getTypeChecker() {
        if (workspaceManager == null) {
            return null;
        }
        return workspaceManager.getTypeChecker();
    }
    
    public WorkspaceManager getWorkspaceManager () {
        return this.workspaceManager;
    }
    
    /**
     * Update the benchmark results file to the indicated new location.
     * @param fileName
     */
    private void changeBenchmarkOutputFile (String fileName) {
        if (benchmarkResultWriter != null) {
            try {
                benchmarkResultWriter.close();
            } catch (IOException e) {
                logInfo("CAL: Unable to close " + benchmarkResultFileName + ": " + e.toString());            }
            benchmarkResultWriter = null;
            benchmarkResultFile = null;
            benchmarkResultFileName = "";
            benchmarkResultLabel = "";
        }

        if (fileName == null || fileName.length() == 0) {
            logInfo("Benchmark results file closed.");
            return;
        }
        
        String decodedFileName;
        // Decode the URL in case it was encoded (specifically spaces encoded as '%20').
        try {
            decodedFileName = java.net.URLDecoder.decode(fileName, "UTF-8");    
        } catch (UnsupportedEncodingException e) {
            logInfo("CAL: Unable to decode " + outputFileName + ": " + e.toString());
            return;
        }
        
        try {
            // Create the new file.
            benchmarkResultFile = new File (decodedFileName);
            // Create the new writer.
            benchmarkResultWriter = new BufferedWriter(new FileWriter(benchmarkResultFile));
            // Update the file name.
            benchmarkResultFileName = fileName;
            // By default the label is set to the name of the file.
            benchmarkResultLabel = benchmarkResultFileName;
            logInfo("Benchmark results file set to: " + benchmarkResultFileName);
            
        } catch (IOException e) {
            logInfo("CAL: Unable to set benchmark result file to " + fileName + ": " + e.toString());
            benchmarkResultWriter = null;
            benchmarkResultFile = null;
            benchmarkResultFileName = "";
        }
    }
    
    private void changeOutputFile (String fileName) {
        if (fileHandler != null) {
            iceLogger.removeHandler(fileHandler);
            fileHandler = null;
            outputFileName = "";
        }
        
        if (fileName == null || fileName.length() == 0) {
            return;
        }
        
        // Decode the URL in case it was encoded (specifically spaces encoded as '%20').
        try {
            fileName = java.net.URLDecoder.decode(fileName, "UTF-8");    
        } catch (UnsupportedEncodingException e) {
            logInfo("CAL: Unable to decode " + outputFileName + ": " + e.toString());
        }
            
        try {
            outputFileName = fileName;
            fileHandler = new FileHandler(fileName);
            fileHandler.setLevel(Level.FINEST);
            fileHandler.setFormatter(new ConsoleFormatter());
            iceLogger.addHandler(fileHandler);
        } catch (IOException e) {            
            logInfo("CAL: Unable to create log file message stream at: " + outputFileName + ": " + e.toString());
            outputFileName = "";
        }
    }
    
    /**
     * Reset any cached CAF results in the program.
     *
     */
    private void resetCachedResults () {
        if (runThread != null && runThread.runtime != null) {
            logInfo("Resetting cached CAF results in program.");
            getWorkspaceManager().resetCachedResults(runThread.runtime.getContext());
        }
    }
    
    private void resetCachedResults (ModuleName moduleName) {
        if (runThread != null && runThread.runtime != null) {
            logInfo("Resetting cached CAF results in module " + moduleName + ".");
            getWorkspaceManager().resetCachedResults(moduleName, runThread.runtime.getContext());
        }
    }
    
    
    /**
     * Reset the machine state.
     */
    private void resetMachineState() {
        if (runThread != null && runThread.runtime != null) {
            logInfo("Resetting machine state (including CAFs).");
            getWorkspaceManager().resetMachineState(runThread.runtime.getContext());
        }
    }
    
    private void resetMachineState(ModuleName moduleName) {
        if (runThread != null && runThread.runtime != null) {
            logInfo("Resetting machine state (including CAFs) associated with module " + moduleName + ".");
            getWorkspaceManager().resetMachineState(moduleName, runThread.runtime.getContext());
        }
    }
    
    /**
     * A StatusListener which listens for compilation events and gathers the names of the modules which were loaded.
     * @author Edward Lam
     */
    private static class ModuleLoadListener extends StatusListener.StatusListenerAdapter {
        /** (Set of ModuleName) the names of modules for which SM_LOADED module status events have been received by this listener. */
        private final Set<ModuleName> loadedModuleNames = new HashSet<ModuleName>();
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setModuleStatus(StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
            if (moduleStatus == StatusListener.SM_LOADED) {
                loadedModuleNames.add(moduleName);
            }
        }
        
        /**
         * @return the names of modules for which SM_LOADED module status events have been received by this listener.
         */
        public Set<ModuleName> getLoadedModuleNames() {
            return loadedModuleNames;
        }
    }
    
    
    private class ICEStatusListener implements StatusListener {
        private double symbolCount = 1.0;
        private final double range = 50.0;
        private double nToPrint = 0.0;
        private int changedCount = 0;
        private int unchangedCount = 0;
        private boolean silent;
        
        /**
         * {@inheritDoc}
         */
        public void setModuleStatus(StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
            if (isSilent()) {
                return;
            }

            if (!(getMachineType() == MachineType.LECC && LECCMachineConfiguration.isLeccRuntimeStatic())) {
                // Only SM_GENCODE are emitted.
                if (moduleStatus == StatusListener.SM_GENCODE) {
                    outputStream.println("Generating: " + moduleName);
                }
            } else {
                // Lecc, statically generated bytecode.
                if (moduleStatus == StatusListener.SM_GENCODE) {
                    outputStream.print(moduleName + ":");
                    int nLabels = getWorkspaceManager().getNFunctionsInModule(moduleName);
                    if (nLabels > 0) {
                        symbolCount = range / nLabels;
                    } else {
                        symbolCount = 1.0;
                    }
                    changedCount = 0;
                    unchangedCount = 0;
                    
                } else if (moduleStatus == StatusListener.SM_GENCODE_DONE) {
                    outputStream.println(" Changed: " + changedCount + ", Unchanged: " + unchangedCount);
                    
                } else if (moduleStatus == StatusListener.SM_START_COMPILING_GENERATED_SOURCE) {
                    outputStream.println ("Compiling generated source for: " + moduleName);
                }
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public void setEntityStatus(StatusListener.Status.Entity entityStatus, String entityName) {
            if (isSilent()) {
                return;
            }
            
            if (entityStatus == StatusListener.SM_ENTITY_GENERATED) {
                nToPrint += symbolCount;
                while (nToPrint >= 1.0) {
                    outputStream.print (".");
                    nToPrint -= 1.0;
                }
                unchangedCount++;
            } else
            if (entityStatus == StatusListener.SM_ENTITY_GENERATED_FILE_WRITTEN) {
                nToPrint += symbolCount;
                while (nToPrint >= 1.0) {
                    outputStream.print (".");
                    nToPrint -= 1.0;
                }
                changedCount++;
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public void incrementCompleted(double d) {
            // do nothing.
        }
        
        /**
         * @return Returns the silent.
         */
        protected boolean isSilent() {
            return silent;
        }
        
        /**
         * @param silent The silent to set.
         */
        protected void setSilent(boolean silent) {
            this.silent = silent;
        }
    }
    
    /**
     * @return Returns the busy.
     */
    public boolean isBusy() {
        return busy;
    }

    private abstract static class FunctionRunThreadBase implements Runnable {
        private boolean showOutput;
        private boolean showRuntimes;
        private boolean showIncrementalMachineStats;
        private Thread th;
        final WorkspaceManager wkspcMgr;
        private boolean doExecDiag;
        private final String name;
        protected List<Pattern> filters = new LinkedList<Pattern>();
        
        CALExecutor runtime;
        Logger logger;
        
        final DebugController debugController = new DebugController();
        
        /**
         * A controller for coordinating the debugging activities of suspending and
         * resuming execution with the command line UI.
         *
         * @author Joseph Wong
         */
        private final class DebugController implements ExecutionContextImpl.DebugListener {
            
            /** The status indicating whether the UI thread should block. */
            private boolean shouldBlockUI = false;
            
            /** The "current" suspended thread for UI purposes. */
            private Thread currentSuspendedThread = null;
            
            /**
             * {@inheritDoc}
             */
            public synchronized void threadSuspended(final Thread thread, final SuspensionState suspensionState) {
                if (currentSuspendedThread == null || !getExecutionContext().isThreadSuspended(currentSuspendedThread)) {
                    currentSuspendedThread = thread;
                }
                setShouldBlockUI(false);
            }
            
            /**
             * Blocks the caller thread for as long as the {@link #shouldBlockUI} flag is set to true.
             */
            synchronized void blockUntilCompletionOrInterruption() {
                try {
                    while (shouldBlockUI) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    // just fall out
                }
            }
            
            /**
             * Sets the flag controlling whether the UI thread should be blocked. If the flag is
             * changed to false, the blocked UI thread would be unblocked.
             * @param value the new value for the flag.
             */
            synchronized void setShouldBlockUI(boolean value) {
                shouldBlockUI = value;
                notifyAll();
            }
            
            /**
             * @return the "current" suspended thread for user interactions.
             */
            synchronized Thread getCurrentSuspendedThread() {
                return currentSuspendedThread;
            }
            
            /**
             * Sets the "current" suspended thread for user interactions.
             * @param thread the new "current" thread.
             */
            synchronized void setCurrentSuspendedThread(final Thread thread) {
                currentSuspendedThread = thread;
            }
        }

        FunctionRunThreadBase (WorkspaceManager wkspcMgr, String name, Logger logger) {
            this.wkspcMgr = wkspcMgr;
            this.name = name;
            this.logger = logger;
        }
        
        DebugController getDebugController() {
            return debugController;
        }
        
        void start () {
            th = new Thread (this, name);
            th.start();
        }
        
        void join () throws InterruptedException {
            if (th != null) {
                th.join();
            }
        }
        
        String getName() {return name;}
        
        /**
         * @return Returns the showRuntimes.
         */
        boolean isShowRuntimes() {
            return showRuntimes;
        }
        /**
         * @param showRuntimes The showRuntimes to set.
         */
        void setShowRuntimes(boolean showRuntimes) {
            this.showRuntimes = showRuntimes;
        }
        /**
         * @return Returns the showOutput.
         */
        boolean isShowOutput() {
            return showOutput;
        }
        /**
         * @param showOutput The showOutput to set.
         */
        void setShowOutput(boolean showOutput) {
            this.showOutput = showOutput;
        }

        /**
         * @return Returns the showIncrementalMachineStats.
         */
        boolean isShowIncrementalMachineStats() {
            return showIncrementalMachineStats;
        }

        /**
         * @param showIncrementalMachineStats The showIncrementalMachineStats to set.
         */
        void setShowIncrementalMachineStats(boolean showIncrementalMachineStats) {
            this.showIncrementalMachineStats = showIncrementalMachineStats;
        }

        void requestQuit () {
            if (runtime != null) {
                runtime.requestQuit();
            }
        }
        
        void requestSuspend () {
            if (runtime != null) {
                runtime.requestSuspend();
            }
        }
        
        void setTraceFilters(List<Pattern> filters){
            this.filters = filters;
        }
        
        /**
         * @return the execution context for this run session.
         */
        ExecutionContextImpl getExecutionContext () {
            makeRuntime();
            return (ExecutionContextImpl)runtime.getContext();
        }
        
        /**
         * Creates a new Executor of the appropriate type and sets the internal member 'runtime'.
         */
        void makeRuntime () {
            if (runtime != null) {
                return; 
            }
            runtime = wkspcMgr.makeExecutorWithNewContextAndDefaultProperties();
            
            if (runtime instanceof org.openquark.cal.internal.machine.g.Executor) {
                org.openquark.cal.internal.machine.g.Executor.setExecDiag (doExecDiag);
            }

            // Set the debug controller up with the execution context
            ((ExecutionContextImpl)runtime.getContext()).addDebugListener(debugController);
        }
        
        /**
         * @param doExecDiag The doExecDiag to set.
         */
        void setDoExecDiag(boolean doExecDiag) {
            this.doExecDiag = doExecDiag;
        }
        
        /**
         * Display time and fips information.
         * @param statistics
         */
        void displayRunTimes (List<StatsGenerator.StatsObject> statistics) {
            for (final StatsGenerator.StatsObject stats : statistics) {               
                String s = stats.generateLongMessage();
                if (s.trim().length() > 0) {
                    logger.log(Level.INFO, s);
                }
            }
        } 
        
        /**
         * Display the output results.
         * Creation date: (4/2/01 12:24:26 PM)
         * @param result
         * @param error
         */
        public void showOutput(Object result, CALExecutorException error) {
            try {
                // Determine the message
                String messageTitle;
                String message = "No results to display.";

                if (error == null) {
                    messageTitle = "Output:";
                    // The main return is the 0th output value
                    message = (result != null) ? result.toString() : "No results to display!";
                } else {
                    CALExecutorException.Type resultStatus = error.getExceptionType();
                    if (resultStatus == CALExecutorException.Type.ERROR_FUNCTION_CALL) {
                        messageTitle = CAL_Prelude.Functions.error.getQualifiedName() + " called (Use :stt to see the stack trace):";
                        message = error.getLocalizedMessage();
                    } else
                    if (resultStatus == CALExecutorException.Type.PATTERN_MATCH_FAILURE) {
                        messageTitle = "Error during pattern matching (Use :stt to see the stack trace):";
                        message = error.getLocalizedMessage();
                    } else                                               
                    if (resultStatus == CALExecutorException.Type.PRIM_THROW_FUNCTION_CALLED) {
                        messageTitle = CAL_Exception_internal.Functions.primThrow.getQualifiedName() + " called.  Use :stt to see the stack trace.";
                        message = error.getLocalizedMessage();
                    } else
                    if (resultStatus == CALExecutorException.Type.FOREIGN_OR_PRIMITIVE_FUNCTION_EXCEPTION) {
                        messageTitle = "Error during foreign or primitive function call.  Use :stt to see the stack trace";
                        message = error.getLocalizedMessage();
                    } else
                    if (resultStatus == CALExecutorException.Type.INTERNAL_RUNTIME_ERROR) {
                        messageTitle = "Internal runtime error.  Use :stt to see the stack trace";
                        message = error.getLocalizedMessage();
                    } else 
                    if (resultStatus == CALExecutorException.Type.USER_TERMINATED) {
                        messageTitle = "User terminated.";
                    } else {
                        messageTitle = "Unknown result state.";
                    }
                }
                
                // Do the actual output
                logger.log(Level.INFO, messageTitle);
                logger.log(Level.INFO, message);
                logger.log(Level.INFO, " ");

            } catch (NullPointerException e) {
                // No results!
                logger.log(Level.INFO, "No results to display!\n");
            }
        }
        
        
    }
    
    /**
     * A helper class to run a function.
     * @author Edward Lam
     */
    private static class FunctionRunThread extends FunctionRunThreadBase {
        private EntryPoint entryPoint;
        private ModuleName targetModule;
        private StatsGenerator executionTimeGenerator = new StatsGenerator();
        private String runningCode = null;
        private CALExecutorException error;
        private Object result;
        
        
        /**
         * Constructor for an SCRunner
         * @param wkspcMgr
         * @param name
         * @param logger
         */
        private FunctionRunThread(WorkspaceManager wkspcMgr, String name, Logger logger) {
            super (wkspcMgr, name, logger);
        }

        void start (StatsGenerator newExecutionTimeGenerator) {
            this.executionTimeGenerator = newExecutionTimeGenerator;
            super.start();
        }
        
        /**
         * {@inheritDoc}
         */
        public void run() {
            try {
                if (entryPoint == null) {
                    return;
                }
                makeRuntime();

                runtime.addStatsGenerator (executionTimeGenerator);
                runtime.setTraceFilters( filters );

                result = null;
                error = null;

                MachineStatistics statsBefore = wkspcMgr.getMachineStatistics();
                MachineStatistics statsBeforeForModule = wkspcMgr.getMachineStatisticsForModule(targetModule);

                try {
                    logger.log(Level.INFO, "\nrunning: " + runningCode);

                    result = runtime.exec (entryPoint, new Object[]{});
                } catch (CALExecutorException e) {
                    logger.log(Level.INFO, "Error while executing: " + e.getMessage());
                    error = e;
                } catch (Exception e) {
                    logger.log(Level.INFO, "Unable to execute due to an internal error.  Please contact Business Objects.  " + e.toString ());
                    return;

                } catch (Error e){
                    logger.log(Level.INFO, "Unable to execute due to an internal error.  Please contact Business Objects.  " + e.toString ());
                    return;
                }

                if (isShowRuntimes()) {
                    displayRunTimes (executionTimeGenerator.getStatistics());
                }

                if (isShowIncrementalMachineStats()) {
                    MachineStatistics statsAfter = wkspcMgr.getMachineStatistics();
                    if (wkspcMgr.getMachineType() == MachineType.LECC) {
                        LECCMachineStatistics incrementalStats = LECCMachineStatistics.getIncrementalStatistics((LECCMachineStatistics)statsBefore, (LECCMachineStatistics)statsAfter, (LECCMachineStatistics)statsBeforeForModule);
                        logger.log(Level.INFO, "Incremental machine statistics (after - before):\n" + incrementalStats);
                    } else {
                        logger.log(Level.INFO, "Machine statistics:\n" + statsAfter);
                    }
                }

                if (isShowOutput()) {
                    showOutput (result, error);
                }
            } finally {
                // We want to go into a loop here in case
                // we hit another breakpoint before the machine
                // notices that a quit has been requested.
                while (getExecutionContext().hasSuspendedThreads()) {
                    getExecutionContext().setStepping(false);
                    getExecutionContext().resumeAll();
                    Thread.yield();
                }
                
                debugController.setShouldBlockUI(false);
            }
        }
        
        /**
         * @param entryPoint
         */
        public void setEntryPoint(EntryPoint entryPoint) {
            this.entryPoint = entryPoint;
        }
        /**
         * @param targetModule the target module associated with the run policy.
         */
        public void setTargetModule(ModuleName targetModule) {
            this.targetModule = targetModule;
        }
        /**
         * @param runningCode The runningCode to set.
         */
        public void setRunningCode(String runningCode) {
            this.runningCode = runningCode;
        }
        public CALExecutorException getError() {
            return error;
        }
    }

    private class InterruptMonitor extends Thread {
        private final boolean debugging = System.getProperty(MachineConfiguration.MACHINE_DEBUG_CAPABLE_PROP) != null;
        private final FunctionRunThread runThreadToInterrupt;
        private boolean go = true;
        
        InterruptMonitor (FunctionRunThread runThreadToInterrupt) {
            super("InterruptMonitor");
            this.runThreadToInterrupt = runThreadToInterrupt;
            this.runThreadToInterrupt.makeRuntime();
        }
        
        @Override
        public void run () {
            while (go) {
                try {
                    sleep (300);
                    if (inBuff.ready()) {
                        inBuff.readLine();
                        if (debugging) {
                            runThreadToInterrupt.requestSuspend();
                            // if there are already suspended threads, it may be the case that
                            // in fact all threads are already suspended, hence it is up to us
                            // to send a signal to the UI thread to wake up
                            if (getExecutionContext().hasSuspendedThreads()) {
                                getDebugController().setShouldBlockUI(false);
                            }
                        } else {
                            runThreadToInterrupt.requestQuit();
                        }
                        return;
                    }
                } catch (java.io.IOException e) {
                    return;
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
        
        public void end() {
            go = false;
        }
    }
    
    private static class QuitException extends Exception {
       
        private static final long serialVersionUID = 3743135754528323427L;
        
        // This block left intentionally empty.
    }
    
    protected static final class CommandType {
        //special cases since there are no actual commands of these types
        static final CommandType OPTIONS = new CommandType("options");
        static final CommandType ALL = new CommandType("all");
        // regular CommandTypes
        public static final CommandType GENERAL = new CommandType("general");
        public static final CommandType DEBUG = new CommandType("debug");
        public static final CommandType BENCHMARK = new CommandType("benchmark");
        public static final CommandType CALDOC = new CommandType("CALDoc");
        public static final CommandType FIND = new CommandType("find");
        public static final CommandType REFACTOR = new CommandType("refactor");
        public static final CommandType UTILITIES = new CommandType("utilities");
        public static final CommandType HISTORY = new CommandType("history");
        public static final CommandType WORKSPACE = new CommandType("workspace");
        public static final CommandType INFO = new CommandType("info");
        public static final CommandType OTHER = new CommandType("other");

        // the displayed name
        final String commandName;

        private CommandType(String name){/* constructor made private for creation control*/
            commandName = name;
        }

        private String getName() {
            return commandName;
        }

        /*
         * Make a list of all CommandTypes
         * needs to update if add/remove command type from the class
         */
        private static List<CommandType> makeCommandTypeList() {
            List<CommandType> commandTypes = new ArrayList<CommandType>();
            commandTypes.add(CommandType.ALL);
            commandTypes.add(CommandType.GENERAL);
            commandTypes.add(CommandType.DEBUG);
            commandTypes.add(CommandType.BENCHMARK);
            commandTypes.add(CommandType.CALDOC);
            commandTypes.add(CommandType.FIND);
            commandTypes.add(CommandType.REFACTOR);
            commandTypes.add(CommandType.UTILITIES);
            commandTypes.add(CommandType.HISTORY);
            commandTypes.add(CommandType.WORKSPACE);
            commandTypes.add(CommandType.INFO);
            commandTypes.add(CommandType.OPTIONS);
            commandTypes.add(CommandType.OTHER);
            return commandTypes;
        }
    
    }


    /**
     * This class encapsulates a command which can be executed from the 
     * ICE command line.
     * New commands are created be deriving a class from ICECommand and
     * adding it to the set of commands in initializeCommands().
     * @author RCypher
     *
     */
    protected abstract class ICECommand {
        /**
         * The different strings that correspond to this command without any arguments. 
         * For example the find command can be 'f' or 'find'.
         */
        private final Set<String> commandText;
        
        /** Flag indicating that this command can be executed while CAL 
         * execution is in a suspended state.  A rule of thumb is that this
         * should be false for anything that can modify the workspace.
         */
        private final boolean validWhileCALExecutionSuspended;
        
        /** Flag indicating that this command should be added to the command history. */
        private final boolean addToCommandHistory;
        
        /** Commands are grouped into types. This field indicates which type this command belongs to. */
        private final CommandType commandType;
        
        /**
         * The different syntaxes that go with this command.  This is used in generating help messages.
         * Note this must be the same length as helpCommandDescription.
         * For example:  ":f[ind] [all] <entity_name>",
                        ":f[ind] ref <gem_name>",
                        ":f[ind] defn <gem_or_type_class_name>",
                        ":f[ind] instByType <type_name>", 
                        ":f[ind] instByClass <type_class_name>"
         */
        private final String[] helpCommandSyntax;
        
        /**
         * Descriptions of the command behaviour for the different syntaxes.  This is used
         * in generating help messages.  Note this must be the same length as helpCommandSyntax.
         */
        private final String[] helpCommandDescription;
        
        protected ICECommand (String[] commandText, 
                    boolean validWhileCALExecutionSuspended, 
                    boolean addToCommandHistory,
                    CommandType commandType,
                    String[] helpCommandSyntax,
                    String[] helpCommandDescription) {
            assert (commandText != null && commandType != null && helpCommandSyntax != null && helpCommandDescription != null) : "Invalid argument in ICECommandInfo constructor.";
            assert (helpCommandSyntax.length == helpCommandDescription.length) : "Mismatch between command syntax and descriptions in ICECommandInfo constructor.";
            
            this.commandText = new HashSet<String>();
            for (int i = 0, n = commandText.length; i < n; ++i) {
                this.commandText.add(commandText[i]);
            }
            this.validWhileCALExecutionSuspended = validWhileCALExecutionSuspended;
            this.addToCommandHistory = addToCommandHistory;
            this.commandType = commandType;
            this.helpCommandDescription = helpCommandDescription;
            this.helpCommandSyntax = helpCommandSyntax;
        }
        
        /**
         * @return Strings corresponding to the different forms this command can take.
         */
        Set<String> getCommandText () {
            return commandText;
        }
        
        /**
         * 
         * @return - true if this command can be executed while a CAL program is suspended.
         */
        boolean isValidWhileCALExecutionSuspended () {
            return validWhileCALExecutionSuspended;
        }
        
        /**
         * 
         * @return true if this command should be added to the ICE command history.
         */
        boolean addToCommandHistory () {
            return this.addToCommandHistory;
        }
        
        /**
         * 
         * @return the type of the command (e.g. General, Debugging, CALDoc, etc.)
         */
        CommandType getCommandType () {
            return this.commandType;
        }
        
        /**
         * 
         * @return The different command syntaxes to display in a help message.
         */
        String[] getHelpCommandSyntax() {
            return helpCommandSyntax;
        }
        
        /**        
         * @return the different command descriptions to display in a help message.
         */
        String[] getHelpCommandDescription() {
            return helpCommandDescription;
        }
       
      
  
        
        /**         
         * @return the documentation text displayed for the command when the :h command is run in ICE
         */
        String showCommand() {

            final StringBuilder sb = new StringBuilder();
            final String syntaxStrings[] = helpCommandSyntax;
            final String descriptionStrings[] = helpCommandDescription;

            for (int k = 0; k < syntaxStrings.length; ++k) {
                final String commandSyntax = syntaxStrings[k];
                final String description = descriptionStrings[k];

                sb.append(commandSyntax);
                sb.append(" ");

                final StringTokenizer stk = new StringTokenizer(description, "\r\n");
                final int nLinesInDescription = stk.countTokens(); 

                //if the command description is in 1 line, display as a single line
                //otherwise, use multi-lines where each description line is indented with specified # of spaces
                if (nLinesInDescription == 1) {
                    String wrappedString = getWrappedString (description, MAX_WIDTH, COMMAND_DESCRIPTION_INDENT);
                    if (commandSyntax.length() < COMMAND_DESCRIPTION_INDENT ) { 
                        wrappedString = wrappedString.substring(commandSyntax.length() + 1);

                    } else {
                        sb.append("\n");
                    }
                    sb.append(wrappedString);

                } else {  
                    // If the command short description fits under the indent limit, the description can go on the same line.
                    // Otherwise start the long description on the next line.
                    if (commandSyntax.length() < COMMAND_DESCRIPTION_INDENT - 1) { 
                        String wrappedString = getWrappedString (stk.nextToken(), MAX_WIDTH, COMMAND_DESCRIPTION_INDENT);
                        sb.append(wrappedString.substring(commandSyntax.length() + 1));
                    } 
                    while (stk.hasMoreTokens()) {
                        sb.append("\n");
                        final String line = stk.nextToken();                        
                        sb.append(getWrappedString(line, MAX_WIDTH, COMMAND_DESCRIPTION_INDENT));
                    }
                }
                if (k < syntaxStrings.length - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return showCommand();
        }

        /** 
         * Perform the command.
         * @param info - the content of the command line after the command text has been stripped. 
         * @throws QuitException
         */
        protected abstract void performCommand(String info) throws QuitException;
    }
    
    /**
     * Encapsulates the results of a performance test run for display.
     *
     * @author Joseph Wong
     */
    private static final class PerformanceTestResults {
        /** The stats from the test runs. */
        private final List<StatsGenerator.StatsObject> executionStats;
        /** The corresponding machine stats. */
        private final List<MachineStatistics> machineStats;
        
        /**
         * @param summarizedStats the stats from the test runs.
         * @param machineStats the corresponding machine stats.
         */
        PerformanceTestResults(List<StatsGenerator.StatsObject> summarizedStats, List<MachineStatistics> machineStats) {
            if (summarizedStats == null || machineStats == null) {
                throw new NullPointerException();
            }
            this.executionStats = summarizedStats;
            this.machineStats = machineStats;
        }
        
        /**
         * @return the number of StatsObject collected.
         */
        int getNExecutionStatsObjects() {
            return executionStats.size();
        }
        
        /**
         * @return the number of MachineStatistics collected.
         */
        int getNMachineStatsObjects() {
            return machineStats.size();
        }
        
        /**
         * @param n
         * @return the nth StatsObject collected.
         */
        StatsGenerator.StatsObject getNthExecutionStatsObject(int n) {
            return executionStats.get(n);
        }
        
        /**
         * @param n
         * @return the nth MachineStatistics object collected, or null if none were collected.
         */
        MachineStatistics getNthMachineStatsObject(int n) {
            return machineStats.get(n);
        }
    }
    
    /**
     * Helper class to collect ModuleTypeInfo stats for one or more modules in a program.
     * @author Edward Lam
     */
    private static class TypeInfoStats {

        /** An instance of a Logger for ICE messages. */
        private final Logger iceLogger;
        
        private int totalModules = 0;
        private int totalImportedModules = 0;
        private int totalFriendModules = 0;
        
        private int totalTopLevelFunctions = 0;
        private int totalLocalFunctions = 0;
        private int totalForeignFunctions = 0;
        private int totalPrimitiveFunctions = 0;
        
        private int totalTypeConstructors = 0;
        private int totalAlgebraicTypes = 0;
        private int totalForeignTypes = 0;
        private int totalDataConstructors = 0;
        
        private int totalTypeClasses = 0;
        private int totalClassMethods = 0;
        private int totalDefaultClassMethods = 0;
        
        private int totalClassInstances = 0;
        private int totalExplicitClassInstances = 0;
        private int totalDerivedClassInstances = 0;
        private int totalDefaultOnlyClassInstances = 0;
        private int totalSemiDerivedClassInstances = 0;
        private int totalTypeableClassInstances = 0;
        private int totalDefaultInstanceMethods = 0;
        private int totalNonDefaultInstanceMethods = 0;
        private int totalNonDefaultGeneratedTypeOfInstanceMethods = 0;
        private int totalNonDefaultGeneratedNonTypeOfInstanceMethods = 0;
        private int totalNonDefaultNonGeneratedInstanceMethods = 0;

        private final int totalNonTypeableTypeConstructorNames = 0;
        
        private final Set<QualifiedName> typeConstructorNameSet = new HashSet<QualifiedName>();
        private final Set<QualifiedName> typeableConstructorNameSet = new HashSet<QualifiedName>();

        /**
         * @param iceLogger the logger to which to log messages.
         */
        public TypeInfoStats(Logger iceLogger) {
            this.iceLogger = iceLogger;
        }
        
        /**
         * Collect type info stats for the given module and add them to this stats collector.
         * @param moduleTypeInfo the type info for the module for which stats should be added to this stats collector.
         */
        public void collectTypeInfoStats(ModuleTypeInfo moduleTypeInfo) {
            
            ModuleName moduleName = moduleTypeInfo.getModuleName();
            
            /*
             * Module-level stats
             */
            totalModules++;

            totalImportedModules += moduleTypeInfo.getNImportedModules();
            totalFriendModules += moduleTypeInfo.getNFriendModules();

            /*
             * Functions
             */
            int nModuleFunctions = moduleTypeInfo.getNFunctions();
            totalTopLevelFunctions += nModuleFunctions;
            for (int i = 0; i < nModuleFunctions; i++) {
                Function nthFunction = moduleTypeInfo.getNthFunction(i);
                int nFunctionLocalFunctions = nthFunction.getNLocalFunctions();
                totalLocalFunctions += nFunctionLocalFunctions;

                if (nthFunction.getForeignFunctionInfo() != null) {
                    totalForeignFunctions++;
                }
                if (nthFunction.isPrimitive()) {
                    totalPrimitiveFunctions++;
                }
                
                // See if any of the local functions are foreign..
                // (Is this possible?)
//                for (int j = 0; j < nFunctionLocalFunctions; j++) {
//                    if (nthFunction.getNthLocalFunction(j).getForeignFunctionInfo() != null) {
//                        totalForeignFunctions++;
//                    }
//                }
            }
            
            /*
             * Types and data constructors
             */
            int nModuleTypeConstructors = moduleTypeInfo.getNTypeConstructors();
            totalTypeConstructors += nModuleTypeConstructors;
            
            for (int i = 0; i < nModuleTypeConstructors; i++) {
                TypeConstructor nthTypeConstructor = moduleTypeInfo.getNthTypeConstructor(i);
                int nDataConstructorsInType = nthTypeConstructor.getNDataConstructors();
                totalDataConstructors += nDataConstructorsInType;
                
                if (nthTypeConstructor.getForeignTypeInfo() != null) {
                    totalForeignTypes++; 
                }
                
                typeConstructorNameSet.add(nthTypeConstructor.getName());
            }
            
            /*
             * Type classes and class methods
             */
            int nModuleTypeClasses = moduleTypeInfo.getNTypeClasses();
            totalTypeClasses += nModuleTypeClasses;
            for (int i = 0; i < nModuleTypeClasses; i++) {
                TypeClass nthTypeClass = moduleTypeInfo.getNthTypeClass(i);
                int nClassMethods = nthTypeClass.getNClassMethods();
                totalClassMethods += nClassMethods;
                
                for (int j = 0; j < nClassMethods; j++) {
                    ClassMethod nthClassMethod = nthTypeClass.getNthClassMethod(j);
                    if (nthClassMethod.hasDefaultClassMethod()) {
                        totalDefaultClassMethods++;
                    }
                }
            }
            
            /*
             * Class instances
             */
            int nClassInstances = moduleTypeInfo.getNClassInstances();
            totalClassInstances += nClassInstances;
            
            for (int i = 0; i < nClassInstances; i++) {
                ClassInstance nthClassInstance = moduleTypeInfo.getNthClassInstance(i);
                int nInstanceMethods = nthClassInstance.getNInstanceMethods();
                
                boolean encounteredInternalNonTypeOfClassMethod = false;
                boolean encounteredTypeOfClassMethod = false;
                boolean nonInternalInstanceMethodEncountered = false;
                boolean defaultInstanceMethodEncountered = false;
                
                /*
                 * count the different styles of instances
                 */
                if (nthClassInstance.getInstanceStyle() == ClassInstance.InstanceStyle.DERIVING) {
                    totalDerivedClassInstances++;
                } else if (nthClassInstance.getInstanceStyle() == ClassInstance.InstanceStyle.INTERNAL) {
                    totalTypeableClassInstances++;
                } else if (nthClassInstance.getInstanceStyle() == ClassInstance.InstanceStyle.EXPLICIT) {
                    totalExplicitClassInstances++;
                }
                
                /*
                 * count the instance methods
                 */
                for (int j = 0; j < nInstanceMethods; j++) {
                    QualifiedName instanceMethod = nthClassInstance.getInstanceMethod(j);
                    if (instanceMethod == null) {
                        totalDefaultInstanceMethods++;
                        defaultInstanceMethodEncountered = true;
                    
                    } else {
                        totalNonDefaultInstanceMethods++;
                        
                        if (instanceMethod.getUnqualifiedName().startsWith("$")) {
                            
                            if (instanceMethod.getUnqualifiedName().startsWith("$typeOf$")) {
                                encounteredTypeOfClassMethod = true;
                                totalNonDefaultGeneratedTypeOfInstanceMethods++;
                                
                                String unqualifiedInstanceMethodName = instanceMethod.getUnqualifiedName();
                                typeableConstructorNameSet.add(
                                        QualifiedName.make(moduleName, unqualifiedInstanceMethodName.substring(unqualifiedInstanceMethodName.lastIndexOf('$') + 1))
                                        );
                                
                            } else {
                                encounteredInternalNonTypeOfClassMethod = true;
                                totalNonDefaultGeneratedNonTypeOfInstanceMethods++;
                            }
                        
                        } else {
                            nonInternalInstanceMethodEncountered = true;
                            totalNonDefaultNonGeneratedInstanceMethods++;
                        }
                    }
                }

                if (defaultInstanceMethodEncountered && 
                    !(encounteredTypeOfClassMethod || nonInternalInstanceMethodEncountered || encounteredInternalNonTypeOfClassMethod)) {
                    // Only default instance methods in this type class.
                    // eg. instance Show (StrictTuple2 a b) where;
                    totalDefaultOnlyClassInstances++;                    
                } else if (encounteredInternalNonTypeOfClassMethod) {
                    // A class instance with a class method "$(something)" which isn't "$typeOf"
                    if (nonInternalInstanceMethodEncountered) {
                        // semi-derived
                        // also has a class method which isn't "$(something)".
                        totalSemiDerivedClassInstances++;
                    
                    } 
                }
            }
        }

        /**
         * Dump the current stats to the logger.
         */
        public void dumpStats() {
            iceLogger.log(Level.INFO, "");
            iceLogger.log(Level.INFO, "# of modules:                " + formatToLengthFive(totalModules));
            iceLogger.log(Level.INFO, "# of imported modules:       " + formatToLengthFive(totalImportedModules));
            iceLogger.log(Level.INFO, "# of friend modules:         " + formatToLengthFive(totalFriendModules));
            iceLogger.log(Level.INFO, "");

            iceLogger.log(Level.INFO, "# of functions:              " + formatToLengthFive(totalTopLevelFunctions + totalLocalFunctions));
            iceLogger.log(Level.INFO, "  # of top-level functions:    " + formatToLengthFive(totalTopLevelFunctions));
            iceLogger.log(Level.INFO, "    # which are foreign:         " + formatToLengthFive(totalForeignFunctions));
            iceLogger.log(Level.INFO, "    # which are primitive:       " + formatToLengthFive(totalPrimitiveFunctions));
            iceLogger.log(Level.INFO, "  # of local functions:        " + formatToLengthFive(totalLocalFunctions));
            iceLogger.log(Level.INFO, "");

            totalAlgebraicTypes = totalTypeConstructors - totalForeignTypes;
            iceLogger.log(Level.INFO, "# of type constructors:      " + formatToLengthFive(totalTypeConstructors));
            iceLogger.log(Level.INFO, "  # of algebraic types:        " + formatToLengthFive(totalAlgebraicTypes));
            iceLogger.log(Level.INFO, "  # of foreign types:          " + formatToLengthFive(totalForeignTypes));
            iceLogger.log(Level.INFO, "");
            iceLogger.log(Level.INFO, "  # with no Typeable instance: " + formatToLengthFive(totalNonTypeableTypeConstructorNames));
            iceLogger.log(Level.INFO, "");

            iceLogger.log(Level.INFO, "# of data constructors:      " + formatToLengthFive(totalDataConstructors));
            iceLogger.log(Level.INFO, "");

            iceLogger.log(Level.INFO, "# of type classes:           " + formatToLengthFive(totalTypeClasses));
            iceLogger.log(Level.INFO, "# of class methods:          " + formatToLengthFive(totalClassMethods));
            iceLogger.log(Level.INFO, "  # which are defaults:        " + formatToLengthFive(totalDefaultClassMethods));
            iceLogger.log(Level.INFO, "");

            iceLogger.log(Level.INFO, "# of class instances:        " + formatToLengthFive(totalClassInstances));
            iceLogger.log(Level.INFO, "  # of explicit instances:     " + formatToLengthFive(totalExplicitClassInstances));
            iceLogger.log(Level.INFO, "    # with defaults only:        " + formatToLengthFive(totalDefaultOnlyClassInstances));
            iceLogger.log(Level.INFO, "  # of derived instances:      " + formatToLengthFive(totalDerivedClassInstances));
            iceLogger.log(Level.INFO, "    # with non-generated class methods: " + formatToLengthFive(totalSemiDerivedClassInstances));
            iceLogger.log(Level.INFO, "  # of Typeable instances:     " + formatToLengthFive(totalTypeableClassInstances));
            iceLogger.log(Level.INFO, "");

            iceLogger.log(Level.INFO, "# of instance methods:               " + formatToLengthFive(totalDefaultInstanceMethods + totalNonDefaultInstanceMethods));
            iceLogger.log(Level.INFO, "  # of default instance methods:       " + formatToLengthFive(totalDefaultInstanceMethods));
            iceLogger.log(Level.INFO, "  # of non-default instance methods:   " + formatToLengthFive(totalNonDefaultInstanceMethods));
            iceLogger.log(Level.INFO, "    # non-generated methods:             " + formatToLengthFive(totalNonDefaultNonGeneratedInstanceMethods));
            iceLogger.log(Level.INFO, "    # generated for Typeable instance:   " + formatToLengthFive(totalNonDefaultGeneratedTypeOfInstanceMethods));
            iceLogger.log(Level.INFO, "    # generated for other instances:     " + formatToLengthFive(totalNonDefaultGeneratedNonTypeOfInstanceMethods));

        }
        
        /**
         * Helper field for formatToLengthFive().
         * The string at index i contains i spaces.
         */
        private static final String[] blankSpaceStrings = {
            "", " ", "  ", "   ", "    ", "     "
        };

        /**
         * Convert the given int to a string of length 5.  Padded with leading spaces if necessary.
         * @param intToFormat the int to format.
         * @return a length-five string (or longer if the int is too big).
         */
        private static String formatToLengthFive(int intToFormat) {
            StringBuilder sb = new StringBuilder();
            sb.append(intToFormat);

            int nSpacesToPrepend = 5 - sb.length();
            if (nSpacesToPrepend <= 0) {
                return sb.toString();
            }
            return blankSpaceStrings[nSpacesToPrepend] + sb.toString();
        }
    }

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

    /**
     * @return the iceLogger
     */
    protected Logger getIceLogger() {
        return iceLogger;
    }

 }
