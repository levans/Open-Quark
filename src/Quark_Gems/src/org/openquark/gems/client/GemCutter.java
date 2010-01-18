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
 * GemCutter.java
 * Creation date: ?
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.openquark.cal.caldoc.CALDocTool;
import org.openquark.cal.caldoc.HTMLDocumentationGeneratorConfiguration;
import org.openquark.cal.compiler.CALSourceGenerator;
import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.Compiler;
import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Refactorer;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceMetrics;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.filter.AcceptAllModulesFilter;
import org.openquark.cal.filter.AcceptAllQualifiedNamesFilter;
import org.openquark.cal.filter.ExcludeTestModulesFilter;
import org.openquark.cal.filter.ModuleFilter;
import org.openquark.cal.filter.QualifiedNameFilter;
import org.openquark.cal.filter.RegExpBasedUnqualifiedNameFilter;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALFeatureMetadata;
import org.openquark.cal.metadata.FunctionalAgentMetadata;
import org.openquark.cal.metadata.MetadataStore;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALSourcePathMapper;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.CarBuilder;
import org.openquark.cal.services.DefaultWorkspaceDeclarationProvider;
import org.openquark.cal.services.GemDesign;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.LocaleUtilities;
import org.openquark.cal.services.LocalizedResourceName;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.ModulePackager;
import org.openquark.cal.services.ModuleRevision;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.services.ResourceIdentifier;
import org.openquark.cal.services.ResourceManager;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.RevisionHistory;
import org.openquark.cal.services.SimpleCALFileVault;
import org.openquark.cal.services.StandardVault;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.StoredVaultElement;
import org.openquark.cal.services.Vault;
import org.openquark.cal.services.VaultElementInfo;
import org.openquark.cal.services.VaultRegistry;
import org.openquark.cal.services.VaultStatus;
import org.openquark.cal.services.VaultWorkspaceDeclarationProvider;
import org.openquark.cal.services.WorkspaceConfiguration;
import org.openquark.cal.services.WorkspaceDeclaration;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.cal.services.WorkspaceResource;
import org.openquark.cal.valuenode.Target;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.ValueNodeBuilderHelper;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.browser.BrowserTree;
import org.openquark.gems.client.browser.GemBrowser;
import org.openquark.gems.client.explorer.TableTopExplorer;
import org.openquark.gems.client.generators.GemGenerator;
import org.openquark.gems.client.internal.EnterpriseSupport;
import org.openquark.gems.client.internal.EnterpriseSupportFactory;
import org.openquark.gems.client.utilities.ExtendedUndoManager;
import org.openquark.gems.client.utilities.ExtendedUndoableEditSupport;
import org.openquark.gems.client.utilities.PreferencesHelper;
import org.openquark.gems.client.valueentry.ValueEditor;
import org.openquark.gems.client.valueentry.ValueEditorHierarchyManager;
import org.openquark.gems.client.valueentry.ValueEditorManager;
import org.openquark.gems.client.valueentry.ValueEntryException;
import org.openquark.util.Pair;
import org.openquark.util.SimpleConsoleHandler;
import org.openquark.util.TextEncodingUtilities;
import org.openquark.util.UnsafeCast;
import org.openquark.util.ui.DetailsDialog;
import org.openquark.util.ui.ExtensionFileFilter;
import org.openquark.util.ui.SwingWorker;
import org.openquark.util.ui.UIUtilities;
import org.openquark.util.xml.XMLPersistenceConstants;


/**
 * This type was generated by a SmartGuide.
 */
public final class GemCutter extends JFrame {

    private static final long serialVersionUID = 8549446034063034475L;

    /** The namespace for log messages from the gems client packages. */
    public static final String CLIENT_LOGGER_NAMESPACE = "org.openquark.gems.client";
    
    /** An instance of a Logger for gems client messages. */
    static final Logger CLIENT_LOGGER = Logger.getLogger(CLIENT_LOGGER_NAMESPACE);

    static {
        CLIENT_LOGGER.setLevel(Level.FINEST);
    }
    
    /** System property which causes GemCutter to load its workspace from the file system path named by the value */
    public static final String GEMCUTTER_PROP_WORKSPACE_FILE = "org.openquark.gems.client.gemcutter.workspace";
    
    /** System property which causes GemCutter to load its workspace from the file on the classpath named by the value 
     * (unqualified file name - searches within folders called "Workspace Declarations" on the class path)
     * */
    public static final String GEMCUTTER_PROP_DEFAULT_STANDARD_VAULT_WORKSPACE = "org.openquark.gems.client.gemcutter.default.standardvault.workspace";
    
    /** System property which causes GemCutter to set its initial current module to the value */
    public static final String GEMCUTTER_PROP_MODULE = "org.openquark.gems.client.gemcutter.module";

    /** The default StandardVault workspace file. */
    private static final String DEFAULT_WORKSPACE_FILE = "gemcutter.default.cws";
    

    /** The default file extension for exported .jar files. */
    public static final String JAR_FILE_EXTENSION = "jar";

    /** The default file extension for exported .car files. */
    public static final String CAR_FILE_EXTENSION = "car";

    /** Whether or not to use a nullary workspace. */
    private static final boolean USE_NULLARY_WORKSPACE = true;
    
    /** The default workspace client id. */
    public static final String DEFAULT_WORKSPACE_CLIENT_ID = USE_NULLARY_WORKSPACE ? null : "gemcutter";
    
    /** List of pairs containing the name of the background(fst) and the corresponding file name (snd). */
    static List<Pair<String, String>> backgrounds = new ArrayList<Pair<String, String>>(4);
    static{
        backgrounds.add(new Pair<String, String>((getResourceString("CherryBackground") + " " + getResourceString("Photolook")), "/Resources/Cherry.jpg"));
        backgrounds.add(new Pair<String, String>((getResourceString("MapleBackground") + " " + getResourceString("Photolook")), "/Resources/Maple.jpg"));
        backgrounds.add(new Pair<String, String>((getResourceString("BirchBackground") + " " + getResourceString("Photolook")), "/Resources/Birch.jpg"));
        backgrounds.add(new Pair<String, String>(getResourceString("NoBackground"), ""));
    }

    /** Map from compile status to the property key to look up the localized status string for that status. 
     *  Used by the status message displayer */
    private static final Map<StatusListener.Status, String> statusToPropertyKeyMap = new HashMap<StatusListener.Status, String>();
    static {
        statusToPropertyKeyMap.put(StatusListener.SM_COMPILED, "SM_Compiled");
        statusToPropertyKeyMap.put(StatusListener.SM_NEWMODULE, "SM_NewModule");
        statusToPropertyKeyMap.put(StatusListener.SM_GENCODE, "SM_GenCode");
        statusToPropertyKeyMap.put(StatusListener.SM_GENCODE_DONE, "SM_GenCode_Done");
        statusToPropertyKeyMap.put(StatusListener.SM_ENTITY_GENERATED, "SM_Entity_Generated");
        statusToPropertyKeyMap.put(StatusListener.SM_ENTITY_GENERATED_FILE_WRITTEN, "SM_Entity_Generated_File_Written");
        statusToPropertyKeyMap.put(StatusListener.SM_START_COMPILING_GENERATED_SOURCE, "SM_Start_Compiling_Generated_Source");
        statusToPropertyKeyMap.put(StatusListener.SM_END_COMPILING_GENERATED_SOURCE, "SM_End_Compiling_Generated_Source");
        statusToPropertyKeyMap.put(StatusListener.SM_LOADED, "SM_Loaded");
    }

    /* Preference key names. */
    public static final String BACKGROUND_FILE_NAME_PREF_KEY    = "backgroundFileName";
    public static final String ADD_MODULE_DIRECTORY_PREF_KEY    = "addModuleDirectory";
    public static final String EXPORT_MODULE_DIRECTORY_PREF_KEY = "exportModuleDirectory";
    public static final String OPEN_DESIGN_DIRECTORY_PREF_KEY   = "openDesignDirectory";
    public static final String WINDOW_PROPERTIES_PREF_KEY       = "windowProperties";
    public static final String FILTER_TEST_MODULES_PREF_KEY     = "filterTestModules";
    public static final String EXCLUDE_FUNCTIONS_BY_REGEXP_PREF_KEY = "excludeFunctionsByRegexp";
    public static final String EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_PREF_KEY = "excludeFunctionsByRegexpArgument";
    public static final String INCLUDE_REDUNDANT_LAMBDAS_PREF_KEY = "includeRedundantLambdas";
    public static final String INCLUDE_UNPLINGED_PRIMITIVE_ARGS_PREF_KEY = "includeUnplingedPrimitiveArgs";
    public static final String INCLUDE_MISMATCHED_WRAPPER_PLINGS_PREF_KEY = "includeMismatchedWrapperPlings";
    public static final String INCLUDE_UNUSED_PRIVATE_FUNCTIONS_PREF_KEY = "includeUnusedPrivateFunctions";
    public static final String INCLUDE_REFERENCED_LET_VARIABLES_PREF_KEY = "includeUnreferenceLetVariables";
    public static final String TRACE_SKIPPED_PREF_KEY           = "traceSkipped";
    
    // this key is private since all access to the value should go through the get/set methods in this class
    private static final String LOCALE_PREF_KEY                 = "locale";
    
    /* Default preference values. */
    public static final String BACKGROUND_FILE_NAME_DEFAULT  = backgrounds.get(0).snd(); 
    public static final String ADD_MODULE_DIRECTORY_DEFAULT  = CALSourcePathMapper.INSTANCE.getBaseResourceFolder().getPathElements()[0];
    public static final String OPEN_DESIGN_DIRECTORY_DEFAULT = "Designs";
    public static final boolean FILTER_TEST_MODULES_DEFAULT  = true;
    public static final boolean EXCLUDE_FUNCTIONS_BY_REGEXP_DEFAULT = false;
    public static final String EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT = "(.*Example.*)|(.*example.*)|(.*test.*)|(.*Test.*)";
    public static final boolean INCLUDE_REDUNDANT_LAMBDAS_DEFAULT = true;
    public static final boolean INCLUDE_UNPLINGED_PRIMITIVE_ARGS_DEFAULT = true;
    public static final boolean INCLUDE_MISMATCHED_WRAPPER_PLINGS_DEFAULT = true;
    public static final boolean INCLUDE_UNUSED_PRIVATE_FUNCTIONS_DEFAULT = true;
    public static final boolean INCLUDE_REFERENCED_LET_VARIABLES_DEFAULT = true;
    public static final boolean TRACE_SKIPPED_DEFAULT = false;
    
    /** The maximum number of undo's to show in undo/redo drop downs */
    private static final int MAX_DISPLAYED_UNDOS = 10;

    /** The intellicut manager */
    private IntellicutManager intellicutManager;

    /** The dialog that handles all of the preferences settings */
    private PreferencesDialog preferencesDialog;
    
    /** The design manager responsible for loading/saving gems. */
    private GemCutterPersistenceManager persistenceManager;

    /** The support class for connecting to enterprise. */
    private final EnterpriseSupport enterpriseSupport = EnterpriseSupportFactory.getInstance(CLIENT_LOGGER);
    
    /** 
     * If an action has this value set a button for this action is assumed to be
     * the drop down button in a drop down button arrangement. This will cause the
     * toolbar to raise the border of the parent button on mouse over.
     */
    public static final String ACTION_BUTTON_IS_DROP_CHILD_KEY = "ButtonIsDropChild";
    
    /** 
     * If an action has this value set a button for this action is assumed to be
     * the main button in a drop down button arrangement. This will cause the toolbar
     * to raise the border of the child button on mouse over.
     */
    public static final String ACTION_BUTTON_IS_DROP_PARENT_KEY = "ButtonIsDropParent";
    
    /** Time that a timed status message will last */
    private static final int STATUS_MESSAGE_TIME = 5000;

    /** The Navigation ToolBar. */
    private NavigationToolBar navigationToolBar = null;
    
    /** This field is used to store tab selection state when entering run mode.
     *   If the target is run, the currently-selected tab is stored in this field and the currently-selected tab is set to the arguments tab.
     *   If a non-target is run, this field is set to null. */
    private Component explorerArgumentsPaneEditModeSelectedTab;

    /** This is the gem that should be run if the run button is clicked */
    private Gem currentGemToRun;
    
    /** Listener for the current gem to run that updates it if the gem graph or table top change. */
    private final CurrentGemToRunListener currentGemToRunListener = new CurrentGemToRunListener();
    
    /** Listener that updates the debug menu if the target becomes runnable or not runnable. */
    private final TargetRunnableListener targetRunnableListener = new TargetRunnableListener();

    /** 
     * This is the collector gem for which more reflectors should be added
     * when the add reflector button is pressed.
     */
    private CollectorGem currentCollectorForAddingReflector;
    
    /** Listener for the current collector for adding emitters that updates it if the gem graph or table top change. */
    private final CurrentCollectorListener currentCollectorListener = new CurrentCollectorListener(); 

    /** The undoable edit to be undone when the GemCutter is not in the dirty state.  
     *  If null, the GemCutter is not in the dirty state if there are no edits to undo. */
    private UndoableEdit editToUndoWhenNonDirty = null;
    
    /** Splash screen for Gem Cutter. */
    // This is a member because it needs to be accessible by the StatusListener in the 
    // compileWorkspace method that listens for messages from the CAL compiler.
    private GemCutterSplashScreen gemCutterSplashScreen = null;
    
    // Other members
    private TypeColourManager typeColours;
    private GUIState guiState = GUIState.EDIT;
    private DisplayedGem displayedGemToAdd;
    private StatusMessageManager statusMessageManager;
    
    /** the current background image (null if none) */
    private BufferedImage backgroundImage;
    
    /** The undo manager for the GemCutter. */
    private ExtendedUndoManager extendedUndoManager;

    /** The displayed gem runner performs the work of actually executing the gem. */
    private DisplayedGemRunner displayedGemRunner;

    /** The valueRunner is used to convert a CAL definition to a value (in the form of a value node). */
    private ValueRunner valueRunner;

    /** The ValueEditorManager manages the creation and consistency of the value editors in a given session. */
    private ValueEditorManager valueEditorManager;
    
    /** A manager for hierarchies spawned by tabletop VEPs. */
    private ValueEditorHierarchyManager tableTopEditorHierarchyManager;
    
    /** A manager for hierarchies spawned by output panels. */
    private ValueEditorHierarchyManager outputPanelHierarchyManager;

    /** The output display manager*/
    private OutputDisplayManager outputDisplayManager;

    /** The owner of the CAL navigator and metadata viewer/editor. */
    private NavigatorAdapter navigatorOwner;

    /** The clipboard used to store the cut/copied gems in the tabletop */
    private Clipboard clipboard;

    /** The workspace manager to manage the workspace we're building. */
    private WorkspaceManager workspaceManager;

    /** The perspective that represents the current point of view. */
    private Perspective perspective;
    
    /** The name of the preferred working module.
     *  In the event of a compile failure, the current module may no longer exist if it is a dependent of the module with the failure.
     *  In this case, the current module is changed to another module.
     *  If the failure is later fixed, and recompilation occurs, we attempt to change back. */
    private ModuleName preferredWorkingModuleName = null;
    
    private TableTop tableTop = null;   
    private TableTopExplorerAdapter tableTopExplorerAdapter = null;

    // GUI Elements
    private JPanel jFrameContentPane = null;
    private JPanel statusBarPane = null;
    private JLabel statusMsgLabel1 = null;
    private JLabel statusMsgLabel2 = null;
    private JToolBar toolBarPane = null;
    private JScrollPane tableTopScrollPane = null;
    private GemBrowser gemBrowser = null;    
    private JPanel gemBrowserPanel = null;
    private JTabbedPane explorerArgumentsPane = null;
    private JSplitPane explorerBrowserSplit = null;
    private JSplitPane browserOverviewSplit = null;
    private OverviewPanel overviewPanel = null;
    private ArgumentExplorer argumentExplorer = null;
    

    private JMenuBar gemCutterJMenuBar = null;

    private JMenu editMenu = null;
    private JMenu fileMenu = null;
    private JMenu debugMenu = null;
    private JMenu helpMenu = null;
    private JMenu viewMenu = null;
    private JMenu insertMenu = null;
    private JMenu generateMenu = null;
    private JMenu workspaceMenu = null;
    private JMenu runMenu = null;

    // It is important to have access to these buttons so that the associated popup menus
    // can be placed correctly on the screen
    private JButton addReflectorGemDropDownButton = null;
    private JButton runDropDownButton = null;
    private JButton undoButton = null;
    private JButton redoButton = null;

    // Keep these around so that we can enable or disable depending on context
    private JButton undoDropDownButton = null;
    private JButton redoDropDownButton = null;
    
    // Keep these around so we can change the text/tooltip depending on context
    private JMenuItem redoMenuItem = null;
    private JMenuItem undoMenuItem = null;
    private JMenu copySpecialMenu = null;
    private JButton addReflectorGemButton = null;
    private JMenuItem addReflectorGemMenuItem = null;
    
    // Hopefully a temporary thing - allows swapping of Run and Resume menu items and buttons
    private JMenuItem runMenuItem = null;
    private JMenu runSubMenu = null;
    private JMenuItem resumeMenuItem = null;
    private JButton resumeRunButton = null;
    private JButton runButton = null;
    
    /** A reference to the Play button popup menu so that we can close it if necessary. */
    private JPopupMenu runDropDownMenu = null;
    
    // Actions used by the menubar and toolbar
    private Action newAction = null;
    private Action openAction = null;
    private Action saveGemAction = null;
    private Action exitAction = null;
    
    private Action undoAction = null;
    private Action undoDropDownAction = null;
    private Action redoAction = null;
    private Action redoDropDownAction = null;
    private Action cutAction = null;
    private Action copyAction = null;
    private Action copySpecialImageAction = null;
    private Action copySpecialTargetSourceAction = null;
    private Action copySpecialTextAction = null;
    private Action pasteAction = null;
    private Action deleteAction = null;
    private Action selectAllAction = null;
    private Action searchAction = null;
    
    private Action viewToolbarAction = null;
    private Action viewStatusbarAction = null;
    private Action viewOverviewAction = null;
    private Action viewArgumentExplorerAction = null;
    private Action viewExplorerAction = null;
    private Action targetDockingAction = null;
    private Action arrangeGraphAction = null;
    private Action fitTableTopAction = null;
    private Action debugOutputAction = null;
    
    private Action dumpDefinitionAction = null;
    private Action allowPreludeRenamingAction = null;
    private Action allowDuplicateRenamingAction = null;
    
    private Action addGemAction = null;
    private Action addValueGemAction = null;
    private Action addCodeGemAction = null;
    private Action addCollectorGemAction = null;
    private Action addReflectorGemAction = null;
    private Action addReflectorGemDropDownAction = null;
    private Action addRecordCreationGemAction = null;
    private Action addRecordSelectionGemAction = null;
    
    // Add module submenu
    private JMenu addModuleSubMenu = null;
    private Action addModuleFromStdVaultAction = null;
    private Action addModuleFromSourceFileAction = null;
    private Action addEnterpriseModuleAction = null;

    // Export module submenu
    private JMenu exportModuleSubMenu = null;
    private Action exportModuleToCEAction = null;
    private Action exportModuleToJarAction = null;
    
    // Sync submenu
    private JMenu syncSubMenu = null;
    private Action syncToHeadAction = null;
    private Action syncToEnterpriseDeclarationAction = null;
    
    private Action removeModuleAction = null;
    private Action workspaceVaultStatusAction = null;
    private Action switchWorkspaceAction = null;
    private Action deployWorkspaceToEnterpriseAction = null;
    private Action exportWorkspaceToCarsAction = null;
    private JMenu renameSubMenu = null;    
    private Action renameGemAction = null;
    private Action renameTypeAction = null;
    private Action renameClassAction = null;
    private Action renameModuleAction = null;
    private Action recompileAction = null;
    private Action compileModifiedAction = null;
    private Action createMinimalWorkspaceAction = null;
    private Action workspaceInfoAction = null;

    private Action runAction = null;
    private Action runDropDownAction = null;
    private Action resumeRunAction = null;
    private Action stopAction = null;
    private Action resetAction = null;
    private final JProgressBar progressBar = new JProgressBar();
    
    private Action helpTopicsAction = null;
    private Action aboutBoxAction = null;
    private Action preferencesAction = null;
    
    /** The action listener that launches the JavaHelp help system. */
    private ActionListener helpSystemLauncher = null;
        
    /** The search dialog currently being displayed */
    private SearchDialog searchDialog = null;
    
    // Cursors for use when adding gems using menu items or buttons
    static final Cursor addValueGemCursor;
    static final Cursor addFunctionGemCursor;
    static final Cursor addCodeGemCursor;
    static final Cursor addCollectorGemCursor;
    static final Cursor addTriangularReflectorGemCursor;
    static final Cursor addOvalReflectorGemCursor;
    static final Cursor addRecordFieldSelectionGemCursor;
    static final Cursor addRecordCreationGemCursor;
    
    // Initialize the cursors
    static {
        // First get some useful reference objects.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension bestsize = toolkit.getBestCursorSize(1,1);

        // Do we support custom cursors?
        if (bestsize.width > 1){
            // Get the images.  Use ImageIcon to ensure that they're loaded.
            ImageIcon addValueCursorImage = new ImageIcon(GemCutter.class.getResource("/Resources/cursorAddValue.gif"));
            ImageIcon addFunctionCursorImage = new ImageIcon(GemCutter.class.getResource("/Resources/cursorAddFunction.gif"));
            ImageIcon addCodeCursorImage = new ImageIcon(GemCutter.class.getResource("/Resources/cursorAddCode.gif"));
            ImageIcon addCollectorCursorImage = new ImageIcon(GemCutter.class.getResource("/Resources/cursorAddCollector.gif"));
            ImageIcon addTriangularReflectorCursorImage = new ImageIcon(GemCutter.class.getResource("/Resources/cursorAddReflector.gif"));
            ImageIcon addOvalReflectorCursorImage = new ImageIcon(GemCutter.class.getResource("/Resources/cursorAddEmitter.gif"));
            ImageIcon addRecordFieldSelectionCursorImage = new ImageIcon(GemCutter.class.getResource("/Resources/cursorAddRecordSelectionGem.gif"));
            ImageIcon addRecordCreationCursorImage = new ImageIcon(GemCutter.class.getResource("/Resources/cursorAddRecordCreationGem.gif"));
            
            // Scale the images to the cursor size.
            BufferedImage scaledValueCursorImage = GemCutterPaintHelper.getResizedImage(addValueCursorImage.getImage(), bestsize);
            BufferedImage scaledFunctionCursorImage = GemCutterPaintHelper.getResizedImage(addFunctionCursorImage.getImage(), bestsize);
            BufferedImage scaledCodeCursorImage = GemCutterPaintHelper.getResizedImage(addCodeCursorImage.getImage(), bestsize);
            BufferedImage scaledCollectorCursorImage = GemCutterPaintHelper.getResizedImage(addCollectorCursorImage.getImage(), bestsize);
            BufferedImage scaledTriangularReflectorCursorImage = GemCutterPaintHelper.getResizedImage(addTriangularReflectorCursorImage.getImage(), bestsize);
            BufferedImage scaledOvalReflectorCursorImage = GemCutterPaintHelper.getResizedImage(addOvalReflectorCursorImage.getImage(), bestsize);
            BufferedImage scaledRecordFieldSelectionGemCursorImage = GemCutterPaintHelper.getResizedImage(addRecordFieldSelectionCursorImage.getImage(), bestsize);
            BufferedImage scaledRecordCreationGemCursorImage = GemCutterPaintHelper.getResizedImage(addRecordCreationCursorImage.getImage(), bestsize);
            
            // Define the cursors so that their hot spots are at (0, 0) of the image.
            Point hotSpot = new Point(0, 0);
            addValueGemCursor = toolkit.createCustomCursor(scaledValueCursorImage, hotSpot, "AddValueGemCursor");
            addFunctionGemCursor = toolkit.createCustomCursor(scaledFunctionCursorImage, hotSpot, "AddFunctionGemCursor");
            addCodeGemCursor = toolkit.createCustomCursor(scaledCodeCursorImage, hotSpot, "AddCodeGemCursor");
            addCollectorGemCursor = toolkit.createCustomCursor(scaledCollectorCursorImage, hotSpot, "AddCollectorGemCursor");
            addTriangularReflectorGemCursor = toolkit.createCustomCursor(scaledTriangularReflectorCursorImage, hotSpot, "AddTriangularReflectorGemCursor");
            addOvalReflectorGemCursor = toolkit.createCustomCursor(scaledOvalReflectorCursorImage, hotSpot, "AddOvalReflectorGemCursor");
            addRecordFieldSelectionGemCursor = toolkit.createCustomCursor(scaledRecordFieldSelectionGemCursorImage, hotSpot, "AddRecordFieldSelectionGemCursor");
            addRecordCreationGemCursor = toolkit.createCustomCursor(scaledRecordCreationGemCursorImage, hotSpot, "AddRecordCreationGemCursor");
                            
        } else {
            // We don't support custom cursors so use the plain crosshair cursor instead.
            Cursor crosshairs = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            addValueGemCursor = crosshairs;
            addFunctionGemCursor = crosshairs;
            addCodeGemCursor = crosshairs;
            addCollectorGemCursor = crosshairs;
            addTriangularReflectorGemCursor = crosshairs;
            addOvalReflectorGemCursor = crosshairs;
            addRecordFieldSelectionGemCursor = crosshairs;
            addRecordCreationGemCursor = crosshairs;
        }
    }

    /**
     * Customized JToolBar for handling the Navigation buttons.
     * @author Michael Cheng?
     */
    private static class NavigationToolBar extends JToolBar {
        private static final long serialVersionUID = -6026389712592950262L;
        protected JFrame frame = null;

        private NavigationToolBar() {
                
            setRollover(true);
            
            setUI(new BasicToolBarUI() {
                
                protected JFrame createFloatingFrame(JToolBar tb) {
                    
                    frame = super.createFloatingFrame(tb);
                    frame.setTitle("Nav:");
                    
                    // Give the frame the GemCutter icon                
                    frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Resources/gemcutter_16.gif")));
                    return frame;
                }
            });
        }   
    }

    /**
     * The action listener on the navigation buttons on the navigation toolbar.
     * @author Michael Cheng (probably)
     */
    private class ParameterNavigationActionListener implements ActionListener {
    
        /** The editor on which this listener will act. */
        private final ValueEditor editor;

        /**
         * Constructor for a ParameterNavigationActionListener.
         * @param editor the editor on which this listener will act.
         */
        public ParameterNavigationActionListener(ValueEditor editor) {
            this.editor = editor;
        }
    
        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent evt) {
    
            // Scroll the TableTopPanel such that the assigned ValueEntryPanel is as center as possible.
            TableTopPanel tableTopPanel = getTableTopPanel();
    
            Rectangle vepBounds = editor.getBounds();
    
            int centerX = vepBounds.getLocation().x + vepBounds.width/2;
            int centerY = vepBounds.getLocation().y + vepBounds.height/2;
        
            Point centerPoint = new Point(centerX, centerY);
    
            JViewport viewPort = getTableTopScrollPane().getViewport();
    
            Point rectPoint = new Point(centerPoint.x - viewPort.getWidth() / 2, centerPoint.y - viewPort.getHeight() / 2);
        
            Rectangle scrollRect = new Rectangle(rectPoint, new Dimension(viewPort.getWidth(), viewPort.getHeight()));
            
            tableTopPanel.scrollRectToVisible(scrollRect);
            
            tableTopPanel.scrollRectToVisible(editor.getBounds());
    
            // Only activate the editor if the focus is somewhere in GemCutter.
            // Else, no activation (the Focus could be in the NavToolBar, and messiness occurs if we try to activate,
            //  since the NavToolBar will not relinquish the focus).
            Component focusedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            if (isAncestorOf(focusedComponent)) {
                editor.getValueEditorHierarchyManager().activateEditor(editor);
            }
        }
    }
    
    /**
     * A listener that updates the current collector for adding emitters if the gem graph or the
     * table top change. Also updates widget labels & tooltips if the name changes.
     */
    private class CurrentCollectorListener implements DisplayedGemStateListener, GemGraphChangeListener, NameChangeListener {
         
        /** Update the widgets if the gem name changes. */          
        public void nameChanged(NameChangeEvent e) {
            updateReflectorWidgets();
        }
        
        /**
         * {@inheritDoc}
         * If there is no collector for which we should add emitters and the user selects a collector 
         * then make that the new collector for which to add emitters and enable the add emitter button.
         */
        public void selectionStateChanged(DisplayedGemStateEvent e) {

            DisplayedGem displayedGem = (DisplayedGem)e.getSource();
            Gem gem = displayedGem.getGem();
            
            if (gem instanceof CollectorGem && tableTop.isSelected(displayedGem)) {

                if (currentCollectorForAddingReflector == null) {
                    setReflectorCollector((CollectorGem)gem);
                }
            }
        }

        /** 
         * {@inheritDoc}
         * Reset the widgets if the run state changes since they might become disabled. 
         */
        public void runStateChanged(DisplayedGemStateEvent e) {
            updateReflectorWidgets();
        }

        /**
         * If the collector for which we're supposed to add emitters
         * is removed then disable the add emitter button. If there is
         * one other collector then automatically use it as the new collector
         * for adding emitters, otherwise make the user select one.
         */
        public void gemRemoved(GemGraphRemovalEvent e) {
            Gem removedGem = (Gem) e.getSource ();

            if (removedGem.equals(currentCollectorForAddingReflector)) {
                selectNewReflectorCollector();
            }
        }

        /**
         * Get a collector from the GemGraph (if any)
         * @return a reflecting collector from the GemGraph, or null if there are no reflecting collectors in the GemGraph.
         */
        private CollectorGem getNextCollector() {
            Set<CollectorGem> tableTopCollectorSet = getTableTop().getGemGraph().getCollectors();
            return tableTopCollectorSet.isEmpty() ? null : tableTopCollectorSet.iterator().next();
        }

        /**
         * If there is no current collector for adding emitters then make the newly added 
         * one the default. But only if there are no other collectors on the table top, 
         * otherwise make the user specifically select the collector they want.
         * Also if a new emitter is added make the current collector the one that the 
         * emitter is from.
         */
        public void gemAdded(GemGraphAdditionEvent e) {
            Gem addedGem = (Gem) e.getSource();

            if (addedGem instanceof CollectorGem && currentCollectorForAddingReflector == null) {
                selectNewReflectorCollector();
                
            } else if (addedGem instanceof ReflectorGem) {
                setReflectorCollector(((ReflectorGem)addedGem).getCollector());
            }
        }

        public void gemConnected(GemGraphConnectionEvent e) {
        }
                
        public void gemDisconnected(GemGraphDisconnectionEvent e) {
        }
        
        /**
         * Set a new collector as the current collector for adding reflectors.
         * @param newCollector the new collector
         */
        public void setReflectorCollector(CollectorGem newCollector) {
            
            if (currentCollectorForAddingReflector != null) {
                currentCollectorForAddingReflector.removeNameChangeListener(this);
            }
            
            currentCollectorForAddingReflector = newCollector;
            if (currentCollectorForAddingReflector != null) {
                currentCollectorForAddingReflector.addNameChangeListener(this);
            }
            updateReflectorWidgets();
        }
        
        /**
         * Selects a new collector for adding reflectors if a default selection can be made.
         */
        public void selectNewReflectorCollector() {
            setReflectorCollector(getNextCollector());
        }            

        /**
         * Updates the action and tooltips for the add reflector button and menu items.
         */
        private void updateReflectorWidgets() {
            
            // Check if there are any more reflectors to select from. If there are then
            // keep the popup menu enabled. Otherwise disable it.
            CollectorGem collectorGem = getNextCollector();
            boolean reflectingCollectorsRemain = collectorGem != null;
            getAddReflectorGemAction().setEnabled(reflectingCollectorsRemain);
            getAddReflectorGemDropDownAction().setEnabled(reflectingCollectorsRemain);
            
            // Now update the tooltips and the menu item. The menu item is
            // only enabled if a specific collector for adding reflectors it selected.
            if (currentCollectorForAddingReflector != null) {           
                getAddReflectorGemMenuItem().setEnabled(true);
                getAddReflectorGemMenuItem().setText(getResourceString("AddReflectorGemForMenu") + " " + currentCollectorForAddingReflector.getUnqualifiedName());
                getAddReflectorGemButton().setToolTipText(getResourceString("AddReflectorGemForToolTip") + " " + currentCollectorForAddingReflector.getUnqualifiedName());            
            } else {
                getAddReflectorGemMenuItem().setEnabled(false);
                getAddReflectorGemMenuItem().setText(getResourceString("AddReflectorGemMenu"));
                getAddReflectorGemButton().setToolTipText(getResourceString("AddReflectorGemToolTip"));
            }
        }      
        
    }
    
    /** A listener that listens for when the target becomes runnable or non-runnable and
     *  updates the debug menu accordingly.
     */
    private class TargetRunnableListener implements GemConnectionListener, GemStateListener {
        
        /**
         * {@inheritDoc}
         * A broken gem can potentially cause the target to be non-runnable, so we
         *  need to watch for it.         
         */
        public void brokenStateChanged(GemStateEvent e) {
            updateDumpDefinitionAction();
        }
        
        /** Update the dumpDefinitionAction in case the target has now become runnable */
        public void connectionOccurred(GemConnectionEvent e) {
            updateDumpDefinitionAction();
        }
        
        /** Update the dumpDefinitionAction in case the target has now become non-runnable */
        public void disconnectionOccurred(GemConnectionEvent e) {
            updateDumpDefinitionAction();
        }
        
        /** Enable or disable the dumpDefinition action according to whether
         *  the target is runnable or not.
         */
        private void updateDumpDefinitionAction() {
            CollectorGem targetGem = getTableTop().getTargetCollector();            
            getDumpDefinitionAction().setEnabled(targetGem.isRunnable());            
        }
}

    /**
     * A listener that updates the current gem to run if the gem graph or the table top selection changes. 
     * Also takes care of updating widget labels & tooltips if the gem name changes.
     */
    private class CurrentGemToRunListener implements DisplayedGemStateListener, GemGraphChangeListener, NameChangeListener {
        
        /** Update widgets if the gem name changes. */
        public void nameChanged(NameChangeEvent e) {
            updateRunWidgets();
        }
        
        /**
         * If there is no current gem to run then make the selected gem
         * the one to run. This will also make any newly added gem the
         * one to run since newly added gems become selected immediately.
         */
        public void selectionStateChanged(DisplayedGemStateEvent e) {
            DisplayedGem changedGem = (DisplayedGem) e.getSource ();
                    
            if (currentGemToRun == null && 
                changedGem.getGem().isRunnable() &&
                tableTop.isSelected(changedGem)) {
                            
                currentGemToRun = changedGem.getGem();
                currentGemToRun.addNameChangeListener(this);
                    
                updateRunWidgets ();
            }
        }

        public void runStateChanged(DisplayedGemStateEvent e) {
        }

        /**
         * If the gem we are supposed to run is removed then automatically move 
         * to the next runnable gem if there is only one runnable gem on the table top.
         */                    
        public void gemRemoved(GemGraphRemovalEvent e) {
            
            Gem removedGem = (Gem) e.getSource ();

            if (removedGem.equals (currentGemToRun)) {
                selectNewGem();
            } else {
                updateRunWidgets();
            }
        }

        /**
         * If a new gem is added and there are no other gems to run then make 
         * it the default gem to run.
         */
        public void gemAdded(GemGraphAdditionEvent e) {
            Gem addedGem = (Gem) e.getSource ();

            if (currentGemToRun == null && addedGem.isRunnable() &&
                getTableTop().getGemGraph().getRunnableGems().size() == 1) {

                currentGemToRun = addedGem;
                currentGemToRun.addNameChangeListener(this);                            
                        
                // updateRunWidgets depends on the DisplayedGem of the currentGemToRun
                // to be available. The gemAdded event gets delivered before the DisplayedGem
                // is ready. Therefore we invoke updateRunWidgets later, once the DisplayedGem
                // is actually on the TableTop.
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateRunWidgets ();
                    }
                });
            }
        }

        /**
         * Check if the current gem is still runnable.
         * Or if another gem became runnable then make it the current gem.
         */
        public void gemConnected(GemGraphConnectionEvent e) {
                    
            if ((currentGemToRun != null && !currentGemToRun.isRunnable()) || currentGemToRun == null) {
                selectNewGem();
            }
        }

        /**
         * Check if the current gem is still runnable.
         * Or if another gem became runnable then make it the current gem.
         */                    
        public void gemDisconnected(GemGraphDisconnectionEvent e) {
            
            if ((currentGemToRun != null && !currentGemToRun.isRunnable()) || currentGemToRun == null) {
                selectNewGem();
            }
        }
        
        /**
         * Selects a new Gem to run if a default selection can be made.
         */
        public void selectNewGem() {
            
            if (currentGemToRun != null) {
                currentGemToRun.removeNameChangeListener(this);
                currentGemToRun = null;
            }
            
            GemGraph gemGraph = getTableTop().getGemGraph();                   
            if (gemGraph.getRunnableGems().size() == 1) {
                currentGemToRun = gemGraph.getRunnableGems().iterator().next();
                currentGemToRun.addNameChangeListener(this);
            }
            
            updateRunWidgets();
        }
        
        /**
         * Sets a new gem as the current gem to run.
         * @param newGemToRun the new gem
         */
        public void setGem(Gem newGemToRun) {
            if (currentGemToRun != null) {
                currentGemToRun.removeNameChangeListener(this);
            }
            
            currentGemToRun = newGemToRun;
            currentGemToRun.addNameChangeListener(this);
            updateRunWidgets();
        }
        
        /**
         * Updates the actions and tooltips for the run button and menu items 
         * depending on which gem should be run.
         */
        private void updateRunWidgets() {

            // Enable the drop down action if there is any runnable gem or collector.        
            GemGraph gemGraph = getTableTop().getGemGraph();
            boolean runnableGemExists = gemGraph.getRunnableGems().size() > 0 || gemGraph.getCollectors().size() > 0;
            getRunAction().setEnabled(runnableGemExists);
            getRunDropDownAction().setEnabled(runnableGemExists);
            getRunSubMenu().setEnabled(runnableGemExists);
        
            // Update the tooltips and the menu item. The menu item is only
            // enabled if a specific gem to run is selected.
            DisplayedGem displayedGem = getTableTop().getDisplayedGem(currentGemToRun);
            if (displayedGem != null && currentGemToRun.isRunnable()) {
                getRunMenuItem().setEnabled(true);
                getRunMenuItem().setText(getResourceString("RunGem") + " " + displayedGem.getDisplayText());
                getRunMenuItem().setToolTipText(getResourceString("RunGemToolTip") + " " + displayedGem.getDisplayText());
                getRunButton().setToolTipText(getResourceString("RunGemToolTip") + " " + displayedGem.getDisplayText());
            } else {
                getRunMenuItem().setEnabled(false);
                getRunMenuItem().setText(getResourceString("RunGem"));
                getRunMenuItem().setToolTipText(getResourceString("RunGemToolTip"));
                getRunButton().setToolTipText(getResourceString("RunGemToolTip"));                    
            }
            
        }        
    }        

    /**
     * Mouse listener for toolbar buttons that makes sure that both buttons in
     * a drop down button group receive the raised border during the rollover effect.
     */
    private class ToolBarButtonMouseListener extends MouseAdapter {

        /** The toolbar button this listener is attached to. */
        private final JButton toolBarButton;
        
        /** True if the toolbar button is the child in a drop down button arrangement. */
        private boolean isDropChild = false;
        
        /** True if the toolbar button is the parent in a drop down button arrangement. */
        private boolean isDropParent = false;


        public ToolBarButtonMouseListener (JButton toolBarButton) {
            this.toolBarButton = toolBarButton;
            
            //check if this button is a drop parent or child.             
            
            Object value = toolBarButton.getAction().getValue(ACTION_BUTTON_IS_DROP_CHILD_KEY);
            if (value != null) {
                isDropChild = ((Boolean)value).booleanValue();
            }
            
            value = toolBarButton.getAction().getValue(ACTION_BUTTON_IS_DROP_PARENT_KEY);
            if (value != null) {
                isDropParent = ((Boolean)value).booleanValue();
            }           
        }
            
        public void mouseEntered(MouseEvent e) {

            if (!toolBarButton.isEnabled()) {
                return;
            }
               
            if (isDropChild) {            
                // If this button is a drop child make sure the parent button to
                // its left gets the raised border.
                
                int index = getToolBarPane().getComponentIndex(toolBarButton);
                JButton parent = (JButton) getToolBarPane().getComponentAtIndex(index - 1);
                parent.getModel().setRollover(true);

            } else if (isDropParent) {
                // If this button is a drop parent make sure the child button to
                // its right gets the raised border.
                
                int index = getToolBarPane().getComponentIndex(toolBarButton);
                JButton child = (JButton) getToolBarPane().getComponentAtIndex(index + 1);
                child.getModel().setRollover(true);
            }     
        }

        public void mouseExited(MouseEvent e) {

            if (!toolBarButton.isEnabled()) {
                return;
            }
        
            if (isDropChild) {
                // Undo the raised border for the parent.

                int index = getToolBarPane().getComponentIndex(toolBarButton);
                JButton parent = (JButton) getToolBarPane().getComponentAtIndex(index - 1);
                parent.getModel().setRollover(false);

            } else if (isDropParent) {
                // Undo the raised border for the child.

                int index = getToolBarPane().getComponentIndex(toolBarButton);
                JButton child = (JButton) getToolBarPane().getComponentAtIndex(index + 1);
                child.getModel().setRollover(false);
            }
        } 
        
        public void mouseClicked(MouseEvent e) {
            
            if (!toolBarButton.isEnabled()) {
                return;
            }
        
            // If this button is clicked make sure it looses its rollover
            // raised border. Sometimes Swing doesn't do this right.
            toolBarButton.getModel().setRollover(false);
        
            if (isDropChild) {
                // Make sure the parent also looses its border.

                int index = getToolBarPane().getComponentIndex(toolBarButton);
                JButton parent = (JButton) getToolBarPane().getComponentAtIndex(index - 1);
                parent.getModel().setRollover(false);

            } else if (isDropParent) {
                // Make sure the child also looses its border.

                int index = getToolBarPane().getComponentIndex(toolBarButton);
                JButton child = (JButton) getToolBarPane().getComponentAtIndex(index + 1);
                child.getModel().setRollover(false);
            }
        }
    }

    /**
     * A listener for a popup menu with run gem items.  The listener handles both menu item and popup menu
     * events.  The existing selected gems will be cleared when the PlayButton popup is opened and then reset 
     * when the popup closes.  As menu items are highlighted by the user, the respective Gem will be selected and
     * brought into view on the Table Top.
     * @author Steve Norton
     */
    private class RunDropDownMenuListener implements ActionListener, ChangeListener, PopupMenuListener {
        
        /* Due to unfortunate timing when the popup menu is created, the first menu item does not
         * automatically cause its associated Gem to be selected and brought into view.
         * 
         * What Happens:  The first menu item is made the default selection for the menu (so the appropriate 
         * gem is highlighted and brought into view) and then popupMenuWillBecomeVisible() is called 
         * (which clears all selections from the table top).  
         * 
         * Workaround:  With knowledge of the default gem, popupMenuWillBecomeVisible() can reselect the appropriate 
         * gem after clearing all the selections.
         */
         
        /** Gems that were selected when the menu was opened. */
        private DisplayedGem[] selectedGems;
        
        /** Gem that had focus when the menu was opened. */
        private DisplayedGem focusedGem;
        
        /** View before the menu was opened - restore this view when menu is canceled. */
        private Rectangle originalView;
        
        /** Automatically selects this gem when menu is opened. */
        private DisplayedGem defaultGem;
        
        /** Keep track of the gems to run when a menu item is clicked. */
        private final Map<JMenuItem, DisplayedGem> menuItemToGemMap = new HashMap<JMenuItem, DisplayedGem>();
        
        /** If true the default gem will be focused as soon as the menu becomes visible. */
        private boolean focusOnPopup;

        /** 
         * Constructor for the RunDropDownMenuListener.
         */
        RunDropDownMenuListener() {
            defaultGem = null;
            focusOnPopup = true;
        }

        public void setFocusOnPopup (boolean focusOnPopup) {
            this.focusOnPopup = focusOnPopup;
        }
        
        /**
         * Tell the listener which Gem to run when the JMenuItem is selected.
         * NOTE: this should be called whenever the listener is added to a JMenuItem.
         * @param menuItem JMenuItem - a menu item in the Popup Menu
         * @param gem DisplayedGem - the Gem to run when the associated menu item is selected
         */
        public void addMenuItem(JMenuItem menuItem, DisplayedGem gem) {
            // If the menuItemToGemMap is empty (this is the first item in the menu) and the menu item is
            // enabled then set the default gem to be this gem
            if (menuItemToGemMap.isEmpty() && menuItem.isEnabled()) {
                defaultGem = gem;
            }
            
            // Add the menuItem and gem combo to the map
            menuItemToGemMap.put(menuItem, gem);
        }
        
        /**
         * Determines if the popup menu is being closed because of a cancellation.
         * This is a BIG TIME HACK (that seems to work!) to work around the problem of 
         * popupMenuCanceled() not being called.
         * @return boolean
         */
        private boolean isPopupCanceled() {
            EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
            AWTEvent event = queue.peekEvent();
            
            if (event == null) {
                return true;
            } else {
                if (event instanceof InvocationEvent) {
                    return true;
                } else if (event instanceof KeyEvent) {
                    if (((KeyEvent)event).getKeyChar() == 27) {
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        /*
         * The following functions satisfy the PopupMenuListener interface
         */
        
        /**
         * Called just before the popup menu is displayed to the user
         * @param e PopupMenuEvent
         */
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            TableTop tableTop = getTableTop();

            // Remember which gem had focus, which were selected and which part of the TableTop was viewable
            // when the popup opened
            selectedGems = tableTop.getSelectedDisplayedGems();
            focusedGem = tableTop.getFocusedDisplayedGem();
            tableTop.setFocusedDisplayedGem(null);
            originalView = getTableTopPanel().getVisibleRect();

            // Clear any gems that are currently selected
            tableTop.selectDisplayedGem(null, false);

            // If a default gem was provided then select it and make sure the user can see it
            if (defaultGem != null && focusOnPopup) {
                tableTop.selectDisplayedGem(defaultGem, true);
                getTableTopPanel().scrollRectToVisible(defaultGem.getBounds());   
            }
        }
        
        /**
         * Called just before the popup menu is removed from the screen, whether a menu item
         * was selected or the menu was canceled.
         * @param e PopupMenuEvent
         */ 
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            TableTop tableTop = getTableTop();

            // Restore the focused gem and re-select all the gems that were selected
            // before the popup was opened
            for (int i = 0; selectedGems != null && i < selectedGems.length; i++) {
                tableTop.selectDisplayedGem(selectedGems[i], false);
            }

            tableTop.setFocusedDisplayedGem(focusedGem);

            // Restore the original view if the popup menu was canceled
            if (isPopupCanceled() && originalView != null) {
                getTableTopPanel().scrollRectToVisible(originalView);
            }           
        }
        
        /**
         * Called when the popup menu has been canceled and is being removed from the screen
         * NOTE: this function does not ACTUALLY get called in java 1.3.1
         * @param e PopupMenuEvent
         */ 
        public void popupMenuCanceled(PopupMenuEvent e) {
            // nothing
        }
        
        /*
         * The following function satisfies the ActionListener interface
         */
         
        /**
         * Called when a popup menu item is clicked and runs the appropriate gem
         * @param e ActionEvent
         */ 
        public void actionPerformed(ActionEvent e){
            runTarget(menuItemToGemMap.get(e.getSource()));
        }
        
        /*
         * The following function satisfies the ChangeListener interface
         */
         
        /**
         * Called whenever the state of a menu item changes.  Selects/highlights the appropriate
         * gem on the TableTop and brings it into view when a menu item is highlighted.
         * @param e ChangeEvent
         */ 
        public void stateChanged(ChangeEvent e) {
            JMenuItem source = (JMenuItem)e.getSource();
            
            // Only perform the selections when the popup menu is actually 
            // visible to the user
            if (source.isShowing()) {
                if (source.isArmed()) {
                    // Select the gem whose menu item has been highlighted and make sure it is visible to the user
                    DisplayedGem gem = menuItemToGemMap.get(e.getSource());
                    if (gem != null) {
                        getTableTop().selectDisplayedGem(gem, true);
                        getTableTopPanel().scrollRectToVisible(gem.getBounds());
                    }
                } else {
                    // Deselect the gem since its menu item lost highlighting
                    getTableTop().selectDisplayedGem(null,false);    
                }
            }
        }
    }
    
    /**
     * A class to handle where and how the output frame is displayed.
     * @author Edward Lam
     */
    private class OutputDisplayManager {
        private final Map<DisplayedGem, GemResultDisplayer> displayedGemToResultDisplayerMap;

        /**
         * A class to handle result display for individual displayed gems.
         * @author Edward Lam
         */
        private class GemResultDisplayer extends ResultValueDisplayer {
            /** The displayed gem for which this manager displays output */
            private final DisplayedGem displayedGem;

            /**
             * Constructor for a GemResultDisplayer.
             * @param displayedGem the displayed gem for which this manager displays output
             */
            GemResultDisplayer(DisplayedGem displayedGem) {
                super(GemCutter.this);
                this.displayedGem = displayedGem;
            }

            /**
             * @see ResultValueDisplayer#showResultFrame(ValueEditor, String, String)
             */
            public JInternalFrame showResultFrame(ValueEditor resultEditor,
                                                  String messageTitle,
                                                  String errorMessage) {
                // Put the target name at the start of the title.
                StringBuilder frameTitleBuffer = new StringBuilder();
                frameTitleBuffer.append(displayedGem.getDisplayText());
                frameTitleBuffer.append(":");
                if (messageTitle != null && !messageTitle.trim().equals("")) {
                    frameTitleBuffer.append(" " + messageTitle);
                }

                return super.showResultFrame(resultEditor, frameTitleBuffer.toString(), errorMessage);
            }

            /**
             * @see javax.swing.event.InternalFrameListener#internalFrameClosed(javax.swing.event.InternalFrameEvent)
             */
            public void internalFrameClosed(InternalFrameEvent e) {
                super.internalFrameClosed(e);

                // if this is the last open frame for this target, remove this manager from the targetToDisplayManager map
                // (to prevent holding on to a reference to a deleted target)
                if (getOpenFrameSet().isEmpty()) {
                    displayedGemToResultDisplayerMap.remove(displayedGem);
                }
            }

            /**
             * @see ResultValueDisplayer#getTargetBounds()
             */
            protected Rectangle getTargetBounds() {
                // Convert the tabletop coordinates to frame coordinates.
                return SwingUtilities.convertRectangle(getTableTopPanel(), displayedGem.getBounds(), getLayeredPane());
            }
        }

        /**
         * Constructor for an OutputDisplayManager.
         */
        OutputDisplayManager() {
            displayedGemToResultDisplayerMap = new HashMap<DisplayedGem, GemResultDisplayer>();
        }
        /**
         * Close all open result frames.
         */
        private void closeAllResultFrames() {
            // iterate over all result displayers.
            // Note that closing result frames will cause the displayer to remove itself from the map
            // and thus would cause a concurrent modification exception if iterating over values() directly.
            Set<GemResultDisplayer> displayManagers = new HashSet<GemResultDisplayer>(displayedGemToResultDisplayerMap.values());
            for (final GemResultDisplayer trm : displayManagers) {
                trm.closeAllResultFrames();
            }
        }
        /**
         * Show the result frame for a target.
         * @param resultEditor the value editor to display
         * @param displayedGem the displayed gem  for which to show the result
         * @param messageTitle the title of the message to display
         * @param errorMessage an error message to display 
         */
        JInternalFrame showResultFrame(ValueEditor resultEditor, DisplayedGem displayedGem, String messageTitle, String errorMessage){
            GemResultDisplayer trm = displayedGemToResultDisplayerMap.get(displayedGem);
            if (trm == null) {
                trm = new GemResultDisplayer(displayedGem);
                displayedGemToResultDisplayerMap.put(displayedGem, trm);
            }
            
            return trm.showResultFrame(resultEditor, messageTitle, errorMessage);
            
        }
    }
    /**
     * Class to handle displaying status messages.
     * @author Edward Lam
     */
    final class StatusMessageManager implements StatusMessageDisplayer {

        /** List of messages requested, ordered from earlier to later */
        private final List<String> requestedMessageList;

        /** Map from requestor to the message it is currently requesting be shown */
        private final Map<Object, String> requestorToMessageMap;
        
        /** The message expiry handler.  Non-null if a deferential or transient message is currently displayed. */
        private MessageExpiryHandler messageExpiryHandler;

        /**
         * Class to handle expiry of status messages.
         * This class holds info for deferential or transient messages.
         * @author Edward Lam
         */
        private class MessageExpiryHandler implements ActionListener {
            /** The object requesting the message to be displayed */
            private final Object requestor;

            /** The message held by this handler */
            private final String message;
            
            /** The timer that fires when the message should be expired. */
            private final Timer timer = new Timer(STATUS_MESSAGE_TIME, this);

            /**
             * Constructor for a message expiry handler
             * @param requestor Object the object requesting the message to clear.
             * @param message String the message to expire
             * @param timed boolean whether the message expires with a timer
             */
            MessageExpiryHandler(Object requestor, String message, boolean timed) {
                this.requestor = requestor;
                this.message = message;

                if (timed) {
                    // start the expiry timer
                    timer.setRepeats(false);
                    timer.start();
                }
            }
            /**
             * Get the message that's expiring.
             * @return the message to expire
             */
            String getMessage() {
                return message;
            }
            /**
             * Get the requestor for the message that's expiring.
             * @return Object the object requesting the message to be displayed
             */
            Object getRequestor() {
                return requestor;
            }

            /** Get the timer that fires when the message should be expired.
             * @return Timer
             */            
            Timer getTimer() {
                return timer;
            }
            
            /**
             * Called when the timer expires
             * @param ae ActionEvent the event fired upon timer expiry
             */
            public void actionPerformed(ActionEvent ae) {
                // expire the message
                displayMessage(requestor, message, null, false);
            }
        }

        /**
         * Constructor for a Status Message Manager
         */
        StatusMessageManager() {
            // use a nice synchronized vector
            requestedMessageList = new Vector<String>();

            requestorToMessageMap = new HashMap<Object, String>();
            
            messageExpiryHandler = null;
        }
        /**
         * {@inheritDoc}
         */
        public void clearMessage(Object requestor){
            displayMessage(requestor, null, null, false);
        }
        /**
         * {@inheritDoc}
         */
        public void setMessage(Object requestor, String message, MessageType messageType) {
            // display the string..
            displayMessage(requestor, message, messageType, true);
        }
        /**
         * {@inheritDoc}
         */
        public void setMessageFromResource(Object requestor, String resourceName, MessageType messageType){

            setMessageFromResource(requestor, resourceName, null, messageType);
        }
        /**
         * {@inheritDoc}
         */
        public void setMessageFromResource(Object requestor, String resourceName, String resourceArgument, MessageType messageType){

            String displayString = resourceArgument == null ? getResourceString(resourceName) : getResourceString(resourceName, resourceArgument);

            // display the string..
            displayMessage(requestor, displayString, messageType, true);
        }
        /**
         * Set or clear the status message on the status bar.
         * @param requestor Object the object requesting the message to clear.
         * @param messageString String the message to set or clear (Null always clears the message)
         * @param messageType the message type.  Ignored if clearing the message.
         * @param set boolean true to set the message, false to clear it.
         */
        private synchronized void displayMessage(Object requestor, String messageString, MessageType messageType, boolean set) {
            JLabel statusLabel = getStatusMsg2();
        
            if (!set || messageString == null) {
                // Clear the message

                // get the previous message requested by this object
                String oldMessage = requestorToMessageMap.get(requestor);

                // check for clearing a message held by the expiry handler
                if (messageExpiryHandler != null && messageExpiryHandler.getMessage() == oldMessage) {
                    // the handler can go away now
                    messageExpiryHandler = null;
                }

                // check for the case where a message to be removed is gone already
                if (messageString != null && !messageString.equals(oldMessage)) {
                    return;
                }
                
                // remove the message
                requestorToMessageMap.remove(requestor);
                requestedMessageList.remove(oldMessage);

                // display the next best message
                if (requestedMessageList.isEmpty()) {
                    // No status left.  Clear the status message.
                    statusLabel.setText("");

                } else {
                    // Display the new latest message
                    statusLabel.setText(requestedMessageList.get(0));
                }

            } else {
                // Set the message

                // get the previous message requested by this object
                String oldMessage = requestorToMessageMap.remove(requestor);

                // if there's a message held by the expiry handler, the message should no longer be displayed
                if (messageExpiryHandler != null) {
                    requestorToMessageMap.remove(messageExpiryHandler.getRequestor());
                    requestedMessageList.remove(messageExpiryHandler.getMessage());
                    messageExpiryHandler.getTimer().stop();
                }

                // if there's an old message, remove it from the list of messages to display
                if (oldMessage != null) {
                    requestedMessageList.remove(oldMessage);
                }

                // update new message info
                requestorToMessageMap.put(requestor, messageString);
                requestedMessageList.add(0, messageString);

                // display the message
                statusLabel.setText(messageString);
                
                // if timed, set a handler to expire the message
                if (messageType == MessageType.TRANSIENT) {
                    messageExpiryHandler = new MessageExpiryHandler(requestor, messageString, true);

                } else if (messageType == MessageType.DEFERENTIAL) {
                    messageExpiryHandler = new MessageExpiryHandler(requestor, messageString, false);
                }
            }
        }
    }

    /**
     * A progress monitor for the export to CAL archive actions.
     *
     * @author Joseph Wong
     */
    private final class ExportCarProgressMonitor implements CarBuilder.Monitor {
        
        /** The modal progress monitor dialog that pops up. */
        private final ModalProgressMonitor progressMonitor; 

        /** The timestamp at the start of the operation, in milliseconds. */
        private long startTime;

        /** The index of the current Car out of all the Cars in the job. */
        private int currentCar;

        /** The total number of Cars in the job. */
        private int nCarsInJob;
        
        /** Constructs a ExportCarProgressMonitor. */
        private ExportCarProgressMonitor() {
            progressMonitor = new ModalProgressMonitor(GemCutter.this, null, 3, 0, 10);
            progressMonitor.setPreferredWidth(480);
            currentCar = 0;
            nCarsInJob = 1;
        }

        /**
         * {@inheritDoc}
         */
        public boolean isCanceled() {
            return progressMonitor.isCanceled();
        }

        /**
         * {@inheritDoc}
         */
        public void showMessages(String[] messages) {
            
            StringBuilder details = new StringBuilder();
            for (final String message : messages) {
                details.append(message).append('\n');
            }
            
            DetailsDialog dialog = new DetailsDialog(
                GemCutter.this,
                getResourceString("ExportCarMessagesDialogTitle"),
                getResourceString("ExportCarMessagesDialogMessage"),
                details.toString(),
                DetailsDialog.MessageType.INFORMATION);
            
            dialog.doModal();
        }
        
        /**
         * {@inheritDoc}
         */
        public void operationStarted(int nCars, final int nTotalModules) {
            startTime = System.currentTimeMillis();
            nCarsInJob = nCars;
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressMonitor.setMaximum(nTotalModules);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        public void carBuildingStarted(String carName, int nModules) {
            currentCar++;
            if (nCarsInJob > 1) {
                progressMonitor.setTitle(GemCutterMessages.getString("ExportingCarXOfY", new Integer(currentCar), new Integer(nCarsInJob)));
            } else {
                progressMonitor.setTitle(getResourceString("ExportingCar", carName));
            }
        }

        /**
         * {@inheritDoc}
         */
        public void processingModule(final String carName, final ModuleName moduleName) {
            final long elapsedTime = System.currentTimeMillis() - startTime;
            
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // Update the progress bar
                    progressMonitor.incrementProgress();
                    
                    // Estimate the time remaining given the elapsed time and the progress made does far
                    int currentProgress = progressMonitor.getProgress();
                    int progressToGo = Math.max(0, progressMonitor.getMaximum() - currentProgress);
                    
                    long estimatedTimeLeft = (elapsedTime * progressToGo) / currentProgress;
                    
                    progressMonitor.setMessage(0, GemCutterMessages.getString("ExportingModuleToCar", moduleName, carName));
                    progressMonitor.setMessage(1, GemCutterMessages.getString("ExportWorkspaceToCarElapsedTime", getTimeString(elapsedTime)));
                    progressMonitor.setMessage(2, GemCutterMessages.getString("ExportWorkspaceToCarEstimatedTimeLeft", getTimeString(estimatedTimeLeft)));
                }
            });
        }

        /**
         * Formats the duration, given in milliseconds, into a nice string.
         * @param durationInMilliseconds
         * @return a formatted string for the time duration.
         */
        private String getTimeString(long durationInMilliseconds) {
            // Formats the given duration as either "x min y sec" or just "y sec". Values over
            // 1 hour are displayed with x > 60.
            
            long durationInSeconds = durationInMilliseconds / 1000L;
            long secondsComponent = durationInSeconds % 60L;
            long minutesComponent = durationInSeconds / 60L;
            
            if (minutesComponent == 0L) {
                return GemCutterMessages.getString("ExportWorkspaceToCarSecondsOnly", new Long(secondsComponent));
            } else {
                return GemCutterMessages.getString("ExportWorkspaceToCarMinutesAndSeconds", new Long(minutesComponent), new Long(secondsComponent));
            }
        }

        /**
         * {@inheritDoc}
         */
        public void carBuildingDone(String carName) {}

        /**
         * {@inheritDoc}
         */
        public void operationDone() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressMonitor.done();
                }
            });
        }

        /**
         * Shows the modal progress monitor dialog.
         */
        void showDialog() {
            progressMonitor.showDialog();
        }
    }

    /**
     * GUI state enum pattern.
     * @author Edward Lam
     */
    public static final class GUIState {
        private final String typeString;

        private GUIState(String s) {
            typeString = s;
        }
        public String toString() {
            return typeString;
        }

        /** Edit mode. */
        public static final GUIState EDIT = new GUIState("EDIT");
        
        /** Run mode. */
        public static final GUIState RUN = new GUIState("RUN");
        
        /** Lock down mode. All controls that modify the workspace/tabletop are disabled. */
        public static final GUIState LOCKED_DOWN = new GUIState("LOCKED_DOWN");
        
        /** Add gem mode. */
        public static final GUIState ADD_GEM = new GUIState("ADD_GEM");
    }

    /**
     * Starts the application.
     * The following system properties can be used:
     *   org.openquark.gems.client.gemcutter.workspace - the path to a workspace file to use
     * @param args an array of command-line arguments.
     */
    public static void main(String[] args) {
        appMain(args, DEFAULT_WORKSPACE_FILE);
    }
    
    /**
     * Starts the application.
     * The following system properties can be used:
     *   org.openquark.gems.client.gemcutter.workspace - the path to a workspace file to use
     * @param args an array of command-line arguments.
     * @param defaultWorkspaceFile the name of the workspace file to fallback onto if no stream provider is supplied and no system properties are defined.
     */
    public static void appMain(String[] args, String defaultWorkspaceFile) {
        try {
            // Setup a logger to intercept messages from our code.
            // and prevent them from being sent to the root logger.
            Logger bobjLogger = Logger.getLogger("org.openquark");
            bobjLogger.setLevel(Level.FINE);
            bobjLogger.setUseParentHandlers(false);
            bobjLogger.addHandler(new SimpleConsoleHandler());

            // WORKAROUND: The following is a hack around the behaviour of the ToolTipManager for Java 1.4 (and potentially earlier)
            // where a tooltip is forcefully repositioned to not extend pass the lower-right corner of its parent.
            // This may cause an intellicut panel entry tooltip to partially/completely cover the entry itself.
            //
            // todo-jowong remove this when the codebase moves to Java 5
            {
                final ToolTipManager tooltipManager = ToolTipManager.sharedInstance();
                
                try {
                    // Note that this field is declared as protected in ToolTipManager, but has been left unused
                    // since Java 5. In Java 1.4, the erroneous bounds calculation code is executed in showTipWindow()
                    // if and only if this field is left in its default value of 'false'. Besides this, the field
                    // controls no other piece of logic in the class.
                    
                    // We change its value to 'true' via reflection.
                    final Field field = ToolTipManager.class.getDeclaredField("heavyWeightPopupEnabled");
                    final boolean isAccessible = field.isAccessible(); // remember the accessibility of the field before the changes
                    field.setAccessible(true);
                    field.set(tooltipManager, Boolean.TRUE);
                    field.setAccessible(isAccessible); // restore the accessibility of the field
                    
                } catch (SecurityException e) {
                } catch (NoSuchFieldException e) {
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }

            // Set native look and feel ...
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    
            // Make stuff even more like Windows
            if (UIManager.getSystemLookAndFeelClassName().endsWith("WindowsLookAndFeel")) {
                Font systemPlain11font = new Font("Dialog", Font.PLAIN, 11);
                //set menu fonts
                UIManager.put("Menu.font", systemPlain11font);
                UIManager.put("MenuItem.font", systemPlain11font);
                UIManager.put("CheckBoxMenuItem.font", systemPlain11font);
                UIManager.put("RadioButtonMenuItem.font", systemPlain11font);
    
                //set fonts for buttons & check boxes
                UIManager.put("Button.font", systemPlain11font);
                UIManager.put("RadioButton.font", systemPlain11font);
                UIManager.put("ToggleButton.font", systemPlain11font);
                UIManager.put("CheckBox.font", systemPlain11font);
    
                //set fonts for text
                UIManager.put("Label.font", systemPlain11font);
                UIManager.put("TabbedPane.font", systemPlain11font);
                UIManager.put("List.font", systemPlain11font);
                UIManager.put("Tree.font", systemPlain11font);
                UIManager.put("TextField.font", systemPlain11font);
                UIManager.put("TextArea.font", systemPlain11font);
                UIManager.put("PasswordField.font", systemPlain11font);
                UIManager.put("ComboBox.font", systemPlain11font);
                UIManager.put("Slider.font", systemPlain11font);
                UIManager.put("ToolTip.font", systemPlain11font);
                
                // Avoid the "Grey fog" which appears when painting is delayed.
                //   Instead, the window painting will appear stuck (like regular Windows windows).
                //   Also, the frame is resized dynamically (also like Windows windows).
                // Ref: http://forums.java.sun.com/thread.jspa?forumID=57&messageID=2387354&threadID=503874
                System.setProperty("sun.awt.noerasebackground", "true");
                Toolkit.getDefaultToolkit().setDynamicLayout(true);
            }
            
            /* Create the frame */
            GemCutter aGemCutter = new GemCutter(null, defaultWorkspaceFile);
            
            // set workspace name in Gem Browser to include name of workspace file
            aGemCutter.getGemBrowser().setWorkspaceNodeName(defaultWorkspaceFile);
   
            /* Create and center the splash screen */
            aGemCutter.gemCutterSplashScreen = new GemCutterSplashScreen(aGemCutter);
            aGemCutter.gemCutterSplashScreen.pack();
            centerWindow(aGemCutter.gemCutterSplashScreen);
            
            // Find number of modules to be loaded and pass this information to progress bar
            int nModules = aGemCutter.getWorkspace().getModuleNames().length;
            aGemCutter.gemCutterSplashScreen.setProgressBarMaxValue(nModules);
   
            // the splash screen is displayed on top of the gem cutter
            aGemCutter.gemCutterSplashScreen.toFront();
   
            // Setup the window size from the preferences.
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
            Dimension defaultSize = new Dimension((int) (screenSize.width * 0.8),
                                                  (int) (screenSize.height * 0.8));
            
            Point defaultLocation = new Point((screenSize.width - defaultSize.width) / 2,
                                              (screenSize.height - defaultSize.height) / 2);
            
            PreferencesHelper.getFrameProperties(getPreferences(), WINDOW_PROPERTIES_PREF_KEY, aGemCutter, defaultSize, defaultLocation);
   
            // get the display time
            long splashScreenAppearTime = System.currentTimeMillis();
   
            // disable input to the gemcutter while splashing
            aGemCutter.setEnabled(false);
   
            try {
                // set the screens to be visible    
                aGemCutter.gemCutterSplashScreen.setVisible(true);
                aGemCutter.setVisible(true);
     
                // We want the repaint to be done before we continue. or else the repaints may not occur 
                // until the compile is already done - and we won't see the (quasi)real-time compile status!
                // Note how we still have to provide an empty run method - the default Thread constructor
                // doesn't create a runnable object
                SwingUtilities.invokeAndWait(new Thread() {public void run() {}});
     
                // Initialize the CAL compiler
                aGemCutter.initCompile();

                // Set up the runners
                try{
                    aGemCutter.initRunners();
                } catch (ValueEntryException exception) {
                    JOptionPane.showMessageDialog(aGemCutter, exception.getMessage() + "\nCause: " + exception.getCause().getMessage() + "\n\nGemCutter will shut-down.", 
                            "Error in initializing the value entry handlers:", JOptionPane.ERROR_MESSAGE);
                    return;
                }   

                // splash for a minimum of 3 seconds
                long currentTime = System.currentTimeMillis();
                long timeDif = currentTime - splashScreenAppearTime;
                if (timeDif < 3000) {
                    try {
                        Thread.sleep(3000 - timeDif);
                    } catch (InterruptedException ie) {}
                }
   
            } catch (Throwable exception) {
                JOptionPane.showMessageDialog(aGemCutter, "An error was encountered during initialization.\nSee console for details.", 
                        "Initialization Error.", JOptionPane.ERROR_MESSAGE);
                throw exception;

            } finally {
                // make the splashing stop, and re-enable the gemCutter
                aGemCutter.setEnabled(true);
                aGemCutter.gemCutterSplashScreen.dispose();
                aGemCutter.gemCutterSplashScreen = null;
            }
   
            aGemCutter.getTableTop().resetTargetForNewTableTop();
            aGemCutter.updateWindowTitle();
            
            // (Java bug workaround) ensure JFileChooser will load..
            ensureJFileChooserLoadable();
                        
        } catch (Throwable exception) {
            System.err.println("Exception occurred in main() of GemCutter");
            exception.printStackTrace(System.out);
        }
    }

    /**
     * GemCutter constructor.
     * @param workspaceDeclarationStreamProvider the stream provider of the workspace declaration to use, or
     *          null to proceed with standard processing (using system properties).
     * @param defaultWorkspaceFile the name of the workspace file to fallback onto if no stream provider is supplied and no system properties are defined.
     */
    public GemCutter(WorkspaceDeclaration.StreamProvider workspaceDeclarationStreamProvider, String defaultWorkspaceFile) {
        try {
            // Save the window size if the GemCutter is closed.
            addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {

                    // Do our best to ensure that any connection to CE is logged off.
                    if (enterpriseSupport != null) {
                        enterpriseSupport.ensureLoggedOff();
                    }

                    // Save the frame properties.
                    PreferencesHelper.putFrameProperties(getPreferences(), WINDOW_PROPERTIES_PREF_KEY, GemCutter.this);
                }
                
                public void windowClosing (WindowEvent e) {
                    // Prompt the user to save the tabletop when the window is being closed
                    if (promptSaveCurrentTableTopIfNonEmpty(null, null)) {
                        dispose();
                    }
                }
            });

            // Set the icon
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/Resources/gemcutter_16.gif")));
    
            // Create the type colour manager
            typeColours = new TypeColourManager();
            
            // Create an intellicut manager
            intellicutManager = new IntellicutManager(this);
            
            // Create the gem design manager
            persistenceManager = new GemCutterPersistenceManager(this);
            
            // Create a workspace manager
            String clientID = WorkspaceConfiguration.getDiscreteWorkspaceID(DEFAULT_WORKSPACE_CLIENT_ID);
            this.workspaceManager = WorkspaceManager.getWorkspaceManager(clientID);
            initWorkspace(workspaceDeclarationStreamProvider, defaultWorkspaceFile);

            // Reset the background from the preferences.
            resetBackground();

            // Set some window properties.
            setName("GemCutter");
            setJMenuBar(getGemCutterJMenuBar());
            setSize(460, 300);
            setTitle(getResourceString("WindowTitle"));
            setContentPane(getJFrameContentPane());
            // The close operation is handled by window listener
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); 

            // Enable running
            setTargetRunningButtons(false);
    
            // set the output display manager
            outputDisplayManager = new OutputDisplayManager();
    
            // set the message display manager
            statusMessageManager = new StatusMessageManager();
            
            navigationToolBar = new NavigationToolBar();    
                
            // set up the undo manager
            extendedUndoManager = new ExtendedUndoManager();

            extendedUndoManager.addUndoableEditListener(new UndoableEditListener() {

                public void undoableEditHappened(UndoableEditEvent uee) {
                    // update the undo buttons whenever a new undoable event is added to the manager.
                    updateUndoWidgets();
                }
            });
            
            // enable/disable undo/redo buttons
            updateUndoWidgets();
            
            // add the undo manager as a listener for undo events on the tabletop
            getTableTop().addUndoableEditListener(extendedUndoManager);

            // Set up the glass pane to handle mouse events when we're in "add gem" mode.  Intercepted mouse 
            // presses that are not over the TableTop will cancel "add gem" mode.  
            // NOTE: mouse events aimed at a code editor are not aimed at the TableTop so "add gem" mode
            //       will be canceled.
            // NOTE: the glass pane should only be visible (and catching mouse events) when we're in
            //       "add gem" mode
            getGlassPane().addMouseListener(new MouseAdapter() {
                
                public void mousePressed(MouseEvent evt) {

                    // Make sure we're really in "add gem" mode
                    if (getGUIState() != GUIState.ADD_GEM) {
                        throw new Error("Programming error: glass pane should not be visible when in gui state " + getGUIState());
                    }
                    
                    // If the left mouse button was used then figure out which component the mouse was aimed at.
                    if (SwingUtilities.isLeftMouseButton(evt)) {
                        Point pressedPoint = SwingUtilities.convertPoint(getGlassPane(), evt.getPoint(), getLayeredPane());
                        Component pressedComp = SwingUtilities.getDeepestComponentAt(getLayeredPane(),
                                                                                    pressedPoint.x, pressedPoint.y);
                                                                                    
                        // If the mouse was aimed at the table top then forward the event
                        // to it so that the gem can be added.
                        if (pressedComp == getTableTopPanel() || pressedComp == getTableTopExplorer().getExplorerTree()) {
                            pressedComp.dispatchEvent(SwingUtilities.convertMouseEvent((Component)evt.getSource(), evt, pressedComp));
                            return;
                        }
                    }
                    
                    // If we get here then either the mouse press was with the right mouse button or
                    // the press was not on the TableTop so cancel "add gem" mode.
                    enterGUIState(GUIState.EDIT);
                }
            });
            
            // Set up the glass pane to handle mouse events when we're in "add gem" mode.  Intercepted mouse 
            // presses that are not over the TableTop will cancel "add gem" mode.  
            // NOTE: mouse events aimed at a code editor are not aimed at the TableTop so "add gem" mode
            //       will be canceled.
            // NOTE: the glass pane should only be visible (and catching mouse events) when we're in
            //       "add gem" mode
            getGlassPane().addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseMoved(MouseEvent e) {
                    TableTopExplorer tableTopExplorer = getTableTopExplorer();
                    Point pressedPoint = SwingUtilities.convertPoint(getGlassPane(), e.getPoint(), getLayeredPane());
                    
                    if (tableTopExplorer.getBounds().contains(SwingUtilities.convertPoint(getLayeredPane(), pressedPoint, getTableTopExplorer()))) {
                        tableTopExplorer.dispatchEvent(SwingUtilities.convertMouseEvent((Component)e.getSource(), e, tableTopExplorer));              
                    }
                }
            });

            // Create the adapter class for the navigator owner
            navigatorOwner = new NavigatorAdapter(this);
            navigatorOwner.addUndoableEditListener(extendedUndoManager);
            getGemBrowser().getBrowserTree().setNavigatorOwner(navigatorOwner);
            
            // Alter the timing so that tool tips stay open for a longer period of time (100 sec).
            ToolTipManager.sharedInstance().setDismissDelay(100000);
            
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    
        // Ensure the status flag is set to 'ready'
        setStatusFlag(null);
        
        setFocusTraversalPolicy(new FocusTraversalPolicy() {
            public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
                
                if (aComponent == getTableTopPanel()) {
                    return getGemBrowserPanel();
                }
                return getTableTopPanel(); 
            }

            public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
                
                if (aComponent == getTableTopPanel()) {
                    return getGemBrowserPanel();
                }
                return getTableTopPanel();
            }

            public Component getDefaultComponent(Container focusCycleRoot) {
                return getTableTopPanel();
            }

            public Component getFirstComponent(Container focusCycleRoot) {
                return getTableTopPanel();
            }

            public Component getLastComponent(Container focusCycleRoot) {
                return getTableTopPanel();
            }
        });
    }

    /**
     * Initialize the workspace manager with the initial workspace contents.
     * No compilation will take place.
     * @param workspaceDeclarationStreamProvider the stream provider of the workspace declaration to use, or
     *          null to proceed with standard processing (using system properties).
     */
    private void initWorkspace(WorkspaceDeclaration.StreamProvider workspaceDeclarationStreamProvider) {
        initWorkspace(workspaceDeclarationStreamProvider, DEFAULT_WORKSPACE_FILE);
    }
    
    /**
     * Initialize the workspace manager with the initial workspace contents.
     * No compilation will take place.
     * @param workspaceDeclarationStreamProvider the stream provider of the workspace declaration to use, or
     *          null to proceed with standard processing (using system properties).
     * @param defaultWorkspaceFile the name of the workspace file (from the StandardVault) to fallback onto if 
     *   no stream provider is supplied and no system properties are defined.
     */
    private void initWorkspace(WorkspaceDeclaration.StreamProvider workspaceDeclarationStreamProvider, String defaultWorkspaceFile) {
        
        // Register the EnterpriseVault.
        enterpriseSupport.registerEnterpriseVaultProvider(workspaceManager.getWorkspace());
        
        // Register the CE vault authenticator.
        enterpriseSupport.registerEnterpriseVaultAuthenticator(getWorkspace(), GemCutter.this);
        
        Status initStatus = new Status("Init status.");
        
        String defaultStandardVaultWorkspaceName = System.getProperty (GEMCUTTER_PROP_DEFAULT_STANDARD_VAULT_WORKSPACE, defaultWorkspaceFile);

        // Create a default workspace declaration provider if none is provided.
        if (workspaceDeclarationStreamProvider == null) {
            workspaceDeclarationStreamProvider = 
                DefaultWorkspaceDeclarationProvider.getDefaultWorkspaceDeclarationProvider(GEMCUTTER_PROP_WORKSPACE_FILE, defaultStandardVaultWorkspaceName);
        }
        
        // Init the workspace.
        workspaceManager.initWorkspace(workspaceDeclarationStreamProvider, initStatus);
        
        // TODOEL how to handle this?
        // Ask the user if they would like to load a default workspace, or something..
        if (initStatus.getSeverity().compareTo(Status.Severity.WARNING) >= 0) {
            String title = getResourceString("WindowTitle");
            String message = "Problems were encountered loading the workspace.";
            String details = initStatus.getDebugMessage();
            
            DetailsDialog.MessageType messageType = initStatus.getSeverity() == Status.Severity.WARNING ? 
                        DetailsDialog.MessageType.WARNING : DetailsDialog.MessageType.ERROR;
            
            DetailsDialog dialog = new DetailsDialog(this, title, message, details, messageType);
            dialog.doModal();
            
            if (initStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
                System.exit(-1);
            }
        }
    }
    
    /**
     * Initializes the CAL compiler by recompiling the specified module or all modules from 
     * the workspace source provider. Compilation fails, an option dialog will be displayed
     * indicating the error.
     * @param dirtyModulesOnly if True, when compiling all modules will compile only modules modified since last successful compilation 
     * @return True if errors were encountered; False otherwise
     */
    private boolean compileWorkspace(boolean dirtyModulesOnly) {
        
        // Create a listener that just sets status messages.
        // We no longer want detailed compilation status messages in status bar - these go in the splash screen now
        StatusListener listener = new StatusListener.StatusListenerAdapter() {
            public void setEntityStatus(StatusListener.Status.Entity entityStatus, String entityName) {
                
            }
            public void setModuleStatus(StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
                String resourceName = statusToPropertyKeyMap.get(moduleStatus);
                
                // update splash screen progress bar if module was loaded
                if (gemCutterSplashScreen != null && moduleStatus.equals(StatusListener.SM_LOADED)) {
                    gemCutterSplashScreen.increaseProgressBar(moduleName == null ? getResourceString(resourceName) : getResourceString(resourceName, moduleName));
                }
            }
        };

        // Set the status label to show that the GemCutter is initializing
        setStatusFlag(getResourceString("InitializingFlag"));

        // Compile, capturing the max error severity and the error to be displayed, if any
        boolean foundErrors = false;
        CompilerMessageLogger logger = new MessageLogger ();

        // Set to busy cursor, since compilation may take a while.
        Cursor oldCursor = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            workspaceManager.compile(logger, dirtyModulesOnly, listener);

        } finally {
            // Reset the old cursor.
            setCursor(oldCursor);
        }
    
        CompilerMessage.Severity errSev = logger.getMaxSeverity();
        if (errSev.compareTo(CompilerMessage.Severity.ERROR) >= 0) {
            foundErrors = true;
            
            // A problem occurred

            // Notify displayed functional agent gems that their size may have changed 
            // (displayed text may change, since the naming policy may decide to show(/hide?) module name)
            for (final DisplayedGem displayedGem : getTableTop().getDisplayedGems()) {
                if (displayedGem.getGem() instanceof FunctionalAgentGem) {
                    Rectangle oldBounds = displayedGem.getBounds();
                    displayedGem.sizeChanged();
                    getTableTopPanel().repaint(oldBounds);
                    getTableTopPanel().repaint(displayedGem.getBounds());
                }
            }

            // Tell the user something bad occurred.
            showCompilationErrors(logger, getResourceString("WorkspaceHadErrors"));
            
        } else if (errSev.compareTo(CompilerMessage.Severity.WARNING) >= 0) {
            
            // Warnings occurred
            // Tell the user something about the warnings.
            showCompilationErrors(logger, getResourceString("WorkspaceHadWarnings"));
        }

        getStatusMessageDisplayer().clearMessage(this);

        // Set status back to ready
        setStatusFlag(null);
        
        return foundErrors;
    }
    
    /**
     * Recompile one or all modules from the current source provider and adjust the GemCutter to reflect the new state 
     * @param dirtyModulesOnly if True, when compiling all modules will compile only modules modified since last successful compilation
     */
    void recompileWorkspace(final boolean dirtyModulesOnly) {
     
        // This may take a while, so set the cursor.
        final Cursor oldCursor = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getTableTopPanel().enableMouseEvents(false);

        // Keep the GUI locked while compiling.
        final GUIState oldGUIState = getGUIState();
        enterGUIState(GUIState.LOCKED_DOWN);
        
        
//        // Run the compile on its own thread, and then call into the AWT thread to update the UI.
//        Thread compileThread = new Thread("compile thread") {
//            public void run() {
                try {
                    long compileStartTime = System.currentTimeMillis();
                    boolean compileErrors = reinitCompile(dirtyModulesOnly);
                    String statusMessage;
                    if (!compileErrors) {
                        statusMessage = GemCutterMessages.getString("SM_RecompilationFinished", Double.toString((System.currentTimeMillis() - compileStartTime)/1000.0));                
                    } else {                
                        statusMessage = GemCutterMessages.getString("SM_RecompilationErrors");                
                    }
                    statusMessageManager.displayMessage(this, statusMessage, StatusMessageDisplayer.MessageType.TRANSIENT, true);
                    
                    try {
                        // Now that the supercombinators are regenerated, we should update the gem references.
                        getTableTop().getGemGraph().updateFunctionalAgentReferences(getWorkspace());
                        
                    } catch (GemEntityNotPresentException fanpe) {
                        // Couldn't reload the functional agent for one of the gems.
                        // Oh well, this is a reentrancy hack anyways - just display a dialog.
                        JOptionPane.showMessageDialog(GemCutter.this, "Error reloading functional agent: " + fanpe.getEntityName(), 
                                "Reload error", JOptionPane.WARNING_MESSAGE);
                    }
                    
                    // TODOEL: remove all non-reloaded gems from the gem graph / tabletop.
                    
                } finally {
                    // Go back to our old cursor
                    setCursor(oldCursor);
                    getTableTopPanel().enableMouseEvents(true);
                    
                    enterGUIState(oldGUIState);
                } 
//            }
//        };
//        
//        compileThread.start();
//        
//        // TEMP: some callers rely on the compilation having finished before returning.
//        try {
//            compileThread.join();
//        } catch (InterruptedException e) {
//        }
    }
    
    /**
     * Perform the reinitialization the CAL compiler.
     * This will update the compiler for the new state of the workspace from the current source provider.
     * @param dirtyModulesOnly if True, when compiling all modules will compile only modules modified since last successful compilation
     * @return True if errors were encountered; false otherwise
     */
    private boolean reinitCompile(boolean dirtyModulesOnly) {

        // Update the preferred working module if the user is working on something.
        if (getTableTop().getGemGraph().getGems().size() > 1) {
            preferredWorkingModuleName = getWorkingModuleName();
        }
        
        // Compile specified module or all modules in workspace
        boolean foundErrors = compileWorkspace(dirtyModulesOnly);

        // The new working module, if any.
        ModuleName newWorkingModuleName = null;
        
        // Change to the preferred working module if it's not the current module, and it exists.
        if (preferredWorkingModuleName != null && 
                !preferredWorkingModuleName.equals(getWorkingModuleName()) && 
                perspective.getMetaModule(preferredWorkingModuleName) != null) {
            
            newWorkingModuleName = preferredWorkingModuleName;
            
        // update the perspective if the working module no longer exists (eg. because of compile failure..)
        } else if (perspective.getWorkingModule() == null) {
            newWorkingModuleName = getInitialWorkingModuleName(workspaceManager.getWorkspace());
        }

        // If there is a new working module, change to it now.
        if (newWorkingModuleName != null) {
            perspective.setWorkingModule(newWorkingModuleName);

            // Also update the window title.
            updateWindowTitle();
        }
        
        // Refresh the gem browser and navigator to show any new gems
        getGemBrowser().refresh();
        getNavigatorOwner().refresh();
        
        return foundErrors;
    }

    /**
     * Perform the initial compilation for the CAL compiler.
     * This will compile the workspace and prepare the compiler for use in the GemCutter
     */
    private void initCompile() {

        // Compile all modules in workspace
        compileWorkspace(false);

        // Create the perspective
        CALWorkspace workspace = workspaceManager.getWorkspace();
        ModuleName workingModuleName = getInitialWorkingModuleName(workspace);
        MetaModule initialWorkingModule = workspace.getMetaModule(workingModuleName);
        perspective = new Perspective(workspace, initialWorkingModule);

        // Set the initial preferred working module name.
        setInitialPreferredWorkingModuleName();
        
        // Populate the gem browser and navigator.
        getGemBrowser().initialize(perspective, true, true);
        getNavigatorOwner().refresh();
    }

    /**
     * Initialize the various CALRunners.
     * @throws ValueEntryException thrown if there is an error creating ValueNodeBuilderHelpers.
     */
    private void initRunners() throws ValueEntryException {

        valueRunner = new ValueRunner(workspaceManager);

        // TODOEL: the various runners should create their own builder helpers.
        //   Unfortunately we still rely on the value editor manager for vnbh initialization.
        ValueNodeBuilderHelper valueBuilderHelper = new ValueNodeBuilderHelper(perspective);
        ValueNodeBuilderHelper displayedGemBuilderHelper = new ValueNodeBuilderHelper(perspective);
        
        valueEditorManager = new ValueEditorManager(displayedGemBuilderHelper, getWorkspace(), typeColours, getTypeCheckInfo());
        displayedGemRunner = new DisplayedGemRunner(workspaceManager, this);
        tableTopEditorHierarchyManager = new ValueEditorHierarchyManager(valueEditorManager, getTableTopPanel());
        outputPanelHierarchyManager = new ValueEditorHierarchyManager(valueEditorManager, getLayeredPane());

        valueRunner.setValueNodeBuilderHelper(valueBuilderHelper);
        displayedGemRunner.setValueNodeBuilderHelper(displayedGemBuilderHelper);
        tableTop.getGemGraph().setValueNodeBuilderHelper(valueEditorManager.getValueNodeBuilderHelper());
        
        // Init builder helpers, include the node helper from the registry.
        valueEditorManager.initValueNodeBuilderHelper(valueBuilderHelper);
        valueEditorManager.initValueNodeBuilderHelper(displayedGemBuilderHelper);
    }
    
    /**
     * Place a given window in the center of the screen.
     * @param w Window the window to center on the screen.
     */
    static void centerWindow(Window w) {
        /* Calculate the screen size */
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
        /* Center splash screen */
        Dimension windowSize = w.getSize();
        if (windowSize.height > screenSize.height){
            windowSize.height = screenSize.height;
        }
        if (windowSize.width > screenSize.width){
            windowSize.width = screenSize.width;
        }
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        w.setLocation(x, y);
    }

    /**
     * Using the output/result TypeExpr, and the data contained in the ValueNode, dataVN, we display the
     * resulting output in a ValueEntryPanel contained in an internal frame.
     * @param dataVN the value node with the data to display
     * @param target the target whose result to display
     * @param messageTitle the title of the message to display
     * @param errorString boolean flag to indicate whether result is an error condition
     */
    public void displayOutput(ValueNode dataVN, DisplayedGem target, String messageTitle, String errorString) {
    
        final ValueEditor editor;
        if (dataVN != null) {
            
            // Create the resultVEP.  (Remember, the resultVEP should not be editable).
            editor = valueEditorManager.getValueEditorDirector().getRootValueEditor(outputPanelHierarchyManager, dataVN, null, 0, null);
            editor.setEditable(false);
        
            // This ensures that code pertaining to the ValueEditorHierarchy is not confused by this ValueEntryPanel.
            outputPanelHierarchyManager.addTopValueEditor(editor);
        } else {
            editor = null;
        }
        
        // Display an internal frame generated from resultVEP.
        JInternalFrame internalFrame = outputDisplayManager.showResultFrame(editor, target, messageTitle, errorString);
    
        if (editor != null) {
            // This bit of code ensures that some 'clean-up' code is done when the result internal frame is closed.
            // (ie. Need to update the ValueEditorHierarchy)
            internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
        
                private final ValueEditor resultEditor = editor;
            
                public void internalFrameClosing(InternalFrameEvent e) {
                    outputPanelHierarchyManager.removeValueEditor(resultEditor, true);
                }
            }); 
        }
    }
    
    /**
     * Return the ExplorerArgumentsPane property value.
     * @return JTabbedPane
     */
    private JTabbedPane getExplorerArgumentsPane() {
        if (explorerArgumentsPane == null) {
            try {
                explorerArgumentsPane = new JTabbedPane(SwingConstants.BOTTOM, JTabbedPane.WRAP_TAB_LAYOUT);
                setExplorerTabVisible(true);
                setArgumentsTabVisible(true);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return explorerArgumentsPane;
    }

    /**
     * Set whether the explorer is visible.
     * @param visible whether the explorer is visible.
     */
    private void setExplorerTabVisible(boolean visible) {
        if (visible) {
            String text = getResourceString("Tab_Explorer");
            String toolTip = getResourceString("Tab_ExplorerToolTip");
            Icon tabIcon = null;
            getExplorerArgumentsPane().insertTab(text, tabIcon, getTableTopExplorer(), toolTip, 0);
        } else {
            int tabIndex = getExplorerArgumentsPane().indexOfComponent(getTableTopExplorer());
            if (tabIndex > -1) {
                getExplorerArgumentsPane().removeTabAt(tabIndex);
            }
        }
    }
    
    /**
     * Get the argument explorer component.
     * @return the argument explorer component.
     */
    private ArgumentExplorer getArgumentExplorer() {
        if (argumentExplorer == null) {
            // Create an owner for the argument explorer.
            ArgumentExplorerOwner owner = new ArgumentExplorerOwner() {

                public String getHTMLFormattedMetadata(PartInput input) {
                    ScopedEntityNamingPolicy namingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(getTableTop().getCurrentModuleTypeInfo());
                    return ToolTipHelpers.getPartToolTip(input, tableTop.getGemGraph(), namingPolicy, argumentExplorer);
                }

                public void retargetInputArgument(PartInput argument, CollectorGem newTarget, int addIndex) {
                    getTableTop().handleRetargetInputArgumentGesture(argument, newTarget, addIndex);
                }

                public ValueEditorManager getValueEditorManager() {
                    return valueEditorManager;
                }
                

                public String getTypeString(final TypeExpr typeExpr) {
                    final ScopedEntityNamingPolicy namingPolicy;
                    final ModuleTypeInfo currentModuleTypeInfo = tableTop.getCurrentModuleTypeInfo();
                    if (currentModuleTypeInfo == null) {
                        namingPolicy = ScopedEntityNamingPolicy.FULLY_QUALIFIED;
                    } else {
                        namingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(currentModuleTypeInfo);
                    }
                    
                    return tableTop.getGemGraph().getTypeString(typeExpr, namingPolicy);
                }
            };
            
            argumentExplorer = new ArgumentExplorer(getTableTop().getGemGraph(), owner);
        }
        return argumentExplorer;
    }
    
    /**
     * Set whether the argument explorer is visible.
     * @param visible whether the argument explorer is visible.
     */
    private void setArgumentsTabVisible(boolean visible) {
        if (visible) {
            String text = getResourceString("Tab_Arguments");
            String toolTip = getResourceString("Tab_ArgumentsToolTip");
            Icon tabIcon = null;
            getExplorerArgumentsPane().addTab(text, tabIcon, getArgumentExplorer(), toolTip);   // add to the end.
        } else {
            int tabIndex = getExplorerArgumentsPane().indexOfComponent(getArgumentExplorer());
            if (tabIndex > -1) {
                getExplorerArgumentsPane().removeTabAt(tabIndex);
            }
        }
    }

    /**
     * Return the ExplorerBrowserSplit property value.
     * @return JSplitPane
     */
    private JSplitPane getExplorerBrowserSplit() {
        if (explorerBrowserSplit == null) {
            try {
                explorerBrowserSplit = new JSplitPane();
                
                explorerBrowserSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);  
                explorerBrowserSplit.setBottomComponent(getBrowserOverviewSplit());              
                explorerBrowserSplit.setTopComponent(getExplorerArgumentsPane());
                explorerBrowserSplit.setBorder(null);
                explorerBrowserSplit.setDividerSize(3);
                explorerBrowserSplit.setDividerLocation(300);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return explorerBrowserSplit;
    }

    /**
     * Return the BrowserOverviewSplit property value.
     * @return JSplitPane
     */
    private JSplitPane getBrowserOverviewSplit() {
        if (browserOverviewSplit == null) {
            try {
                browserOverviewSplit = new JSplitPane();
                
                browserOverviewSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
                browserOverviewSplit.setTopComponent(getGemBrowserPanel());
                browserOverviewSplit.setBottomComponent(null);  // Off to start with
                browserOverviewSplit.setDividerSize(3);
                browserOverviewSplit.setDividerLocation(0.5);
                browserOverviewSplit.setBorder(null); 
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return browserOverviewSplit;
    }
    
    /**
     * Returns the GemBrowser object
     * @return GemBrowser
     */
    private GemBrowser getGemBrowser() {
        if (gemBrowser == null) {
            gemBrowser = new GemBrowser();

            // Customize the browser tree for the gem cutter
            BrowserTree browserTree = gemBrowser.getBrowserTree();
            browserTree.setCellRenderer(new GemCutterBrowserTreeExtensions.CellRenderer(this));
            browserTree.setPopupMenuProvider(new GemCutterBrowserTreeExtensions.PopupMenuProvider(this));
            browserTree.addMouseMotionListener(new GemCutterBrowserTreeExtensions.MouseListener(this));
            browserTree.addLeafNodeTriggeredListener(new GemCutterBrowserTreeExtensions.LeafNodeListener(this));
        }
        return gemBrowser;
    }
    
    /**
     * Return the GemBrowserPanel property value.
     * @return JPanel
     */
    private JPanel getGemBrowserPanel() {
        if (gemBrowserPanel == null) {
            try {
                gemBrowserPanel = getGemBrowser().getGemBrowserPanel();
                gemBrowserPanel.setFocusTraversalKeysEnabled(true);
                gemBrowserPanel.setFocusCycleRoot(true);
                gemBrowserPanel.setName("GemBrowserPanel");
                gemBrowserPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
                gemBrowserPanel.setPreferredSize(gemBrowserPanel.getMaximumSize());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return gemBrowserPanel;
    }

    /**
     * Return the BrowserTree
     * @return BrowserTree
     */
    BrowserTree getBrowserTree() {
        return getGemBrowser().getBrowserTree();
    }
    
    /**
     * @return the GemCutterPersistenceManager used by this GemCutter
     */
    GemCutterPersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    /**
     * Return the GemCutterJMenuBar property value.
     * @return JMenuBar
     */
    private JMenuBar getGemCutterJMenuBar() {
        if (gemCutterJMenuBar == null) {
            try {
                
                gemCutterJMenuBar = new JMenuBar();
                gemCutterJMenuBar.setName("GemCutterJMenuBar");
                gemCutterJMenuBar.add(getFileMenu());
                gemCutterJMenuBar.add(getEditMenu());                
                gemCutterJMenuBar.add(getViewMenu());
                gemCutterJMenuBar.add(getInsertMenu());
                gemCutterJMenuBar.add(getGenerateMenu());
                gemCutterJMenuBar.add(getWorkspaceMenu());
                gemCutterJMenuBar.add(getRunMenu());
                gemCutterJMenuBar.add(getDebugMenu());
                gemCutterJMenuBar.add(getHelpMenu());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        } 
        return gemCutterJMenuBar;
    }

    /**
     * Return the JFrameContentPane property value.
     * @return JPanel
     */
    private JPanel getJFrameContentPane() {
        if (jFrameContentPane == null) {
            try {
                jFrameContentPane = new JPanel();
                jFrameContentPane.setName("JFrameContentPane");
                jFrameContentPane.setLayout(new BorderLayout());
                jFrameContentPane.add(getToolBarPane(), "North");
                jFrameContentPane.add(getStatusBarPane(), "South");
                
                // Add the GemCutter pane.
                JSplitPane gemCutterPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                gemCutterPane.setName("GemCutterPane");
                gemCutterPane.setDividerSize(4);

                // Initialize the divider position between browser and table top
                gemCutterPane.setDividerLocation(280);
                gemCutterPane.add(getTableTopScrollPane(), "right");
                gemCutterPane.add(getExplorerBrowserSplit(), "left");                      
                jFrameContentPane.add(gemCutterPane, "Center");
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return jFrameContentPane;
    }

    /**
     * Return the StatusBarPane property value.
     * @return JPanel
     */
    private JPanel getStatusBarPane() {
        if (statusBarPane == null) {
            try {
                statusBarPane = new JPanel();
                statusBarPane.setName("StatusBarPane");
                statusBarPane.setLayout(new BorderLayout());
                getStatusBarPane().add(getStatusMsg1(), "West");
                getStatusBarPane().add(getStatusMsg2(), "Center");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return statusBarPane;
    }
    
    /**
     * Return the StatusMsg1 property value.
     * @return JLabel
     */
    private JLabel getStatusMsg1() {
        if (statusMsgLabel1 == null) {
            try {
                statusMsgLabel1 = new JLabel();
                statusMsgLabel1.setName("StatusMsg1");
                statusMsgLabel1.setBorder(new EtchedBorder());
                statusMsgLabel1.setText(getResourceString("ReadyFlag"));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return statusMsgLabel1;
    }

    /**
     * Return the StatusMsg2 property value.
     * @return JLabel
     */
    private JLabel getStatusMsg2() {
        if (statusMsgLabel2 == null) {
            try {
                statusMsgLabel2 = new JLabel();
                statusMsgLabel2.setName("StatusMsg2");
                statusMsgLabel2.setBorder(new EtchedBorder());
                statusMsgLabel2.setText("");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return statusMsgLabel2;
    }

    /**
     * Returns the TableTop
     * @return TableTop
     */
    TableTop getTableTop() {
        if (tableTop == null) {
            try {
                tableTop = new TableTop(this);
                
                // adds the explorer as a displayed gem state listener
                tableTop.addStateChangeListener(getTableTopExplorerAdapter());
                
                // add listeners for updating the current gem to run and the current collector
                tableTop.addStateChangeListener(currentGemToRunListener);
                tableTop.addStateChangeListener(currentCollectorListener);
                tableTop.addGemGraphChangeListener(currentGemToRunListener);
                tableTop.addGemGraphChangeListener(currentCollectorListener);
                tableTop.getGemGraph().addGemConnectionListener(targetRunnableListener);                
                
                // Make the target gem selectable.
                currentCollectorListener.selectNewReflectorCollector();

                // Change the window title when the target collector's name changes.
                tableTop.getGemGraph().getTargetCollector().addNameChangeListener(new NameChangeListener() {
                    public void nameChanged(NameChangeEvent e) {
                        updateWindowTitle();
                    }
                });
                
                TableTopPanel tableTopPanel = tableTop.getTableTopPanel();
                tableTopPanel.setName("TableTop");
                tableTopPanel.setLayout(null);
                tableTopPanel.setBackground(SystemColor.window);
                tableTopPanel.setBounds(0, 0, 160, 120);
                ToolTipManager.sharedInstance().registerComponent(tableTopPanel);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return tableTop;
    }
    
    /**
     * Returns the reference to the tableTopPanel
     * @return TableTopPanel
     */
    TableTopPanel getTableTopPanel() {
        return getTableTop().getTableTopPanel();
    }
    
    /**
     * Return the TableTopScrollPane property value.
     * @return JScrollPane
     */
    JScrollPane getTableTopScrollPane() {
        if (tableTopScrollPane == null) {
            try {
                tableTopScrollPane = new JScrollPane();
                tableTopScrollPane.setName("TableTopScrollPane");
                getTableTopScrollPane().setViewportView(getTableTopPanel());

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return tableTopScrollPane;
    }

    /**
     * Return the ToolBarPane property value.
     * @return JToolBar
     */
    JToolBar getToolBarPane() {
        if (toolBarPane == null) {
            try {
                toolBarPane = new JToolBar();
                toolBarPane.setName(getResourceString("ViewToolbar"));
                toolBarPane.setRollover(true);
                
                toolBarPane.add(makeNewButton(getNewAction()));
                toolBarPane.add(makeNewButton(getOpenAction()));
                toolBarPane.add(makeNewButton(getSaveGemAction()));
                
                toolBarPane.addSeparator();
                toolBarPane.add(makeNewButton(getCutAction()));
                toolBarPane.add(makeNewButton(getCopyAction()));
                toolBarPane.add(makeNewButton(getPasteAction()));
                
                toolBarPane.addSeparator();
                toolBarPane.add(getUndoButton());
                toolBarPane.add(getUndoDropDownButton());
                toolBarPane.add(getRedoButton());
                toolBarPane.add(getRedoDropDownButton());
                
                toolBarPane.addSeparator();
                toolBarPane.add(makeNewButton(getSearchAction()));
                
                toolBarPane.addSeparator();
                toolBarPane.add(makeNewButton(getAddGemAction()));
                toolBarPane.add(makeNewButton(getAddValueGemAction()));
                toolBarPane.add(makeNewButton(getAddCodeGemAction()));
                toolBarPane.add(makeNewButton(getAddCollectorGemAction()));
                toolBarPane.add(getAddReflectorGemButton());
                toolBarPane.add(getAddReflectorGemDropDownButton());
                toolBarPane.add(makeNewButton(getAddRecordCreationGemAction()));
                toolBarPane.add(makeNewButton(getAddRecordFieldSelectionGemAction()));
                
                toolBarPane.addSeparator();
                toolBarPane.add(getRunButton());
                toolBarPane.add(getRunDropDownButton());
                toolBarPane.add(makeNewButton(getStopAction()));
                
                // Space for the Parameter navigation buttons.
                toolBarPane.addSeparator();

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return toolBarPane;
    }

    /** 
     * Creates an instance of ViewPreferencesDialog if null
     * @return PreferencesDialog
     */
    private PreferencesDialog getPreferencesDialog() {
        if (preferencesDialog == null) {
            preferencesDialog = new PreferencesDialog(this);
            centerWindow(preferencesDialog);
        }
        return preferencesDialog;
    }

    /**
     * Return the FileMenu property value.
     * @return JMenu
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            try {
                fileMenu = new JMenu();
                fileMenu.setName("FileMenu");
                fileMenu.setText(getResourceString("FileMenu"));
                fileMenu.setMargin(new Insets(2, 0, 2, 0));
                fileMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_FILE_MENU);
                fileMenu.add(makeNewMenuItem(getNewAction()));
                fileMenu.add(makeNewMenuItem(getOpenAction()));
                fileMenu.add(makeNewMenuItem(getSaveGemAction()));                
                fileMenu.addSeparator();
                fileMenu.add(makeNewMenuItem(getSwitchWorkspaceAction()));
                fileMenu.addSeparator();
                fileMenu.add(makeNewMenuItem(getExitAction()));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return fileMenu;
    }  


    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
     */
    private static void handleException(Throwable exception) {
    
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }
    
    /**
     * Return the OverviewPanel property value.
     * @return OverviewPanel
     */
    OverviewPanel getOverviewPanel() {
        if (overviewPanel == null) {
            try {
                overviewPanel = new OverviewPanel(getTableTopPanel());
                getOverviewPanel().setBackground(Color.white);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return overviewPanel;
    }
    
    /**
     * Return the action that creates a "new" TableTop.
     * @return Action
     */
    private Action getNewAction() {
        
        if (newAction == null) {
            try {
                newAction = new AbstractAction (getResourceString("New"),
                        new ImageIcon(GemCutter.class.getResource("/Resources/new.gif"))) {

                    private static final long serialVersionUID = -8609323621425336729L;

                    public void actionPerformed(ActionEvent evt) {
                        if (promptSaveCurrentTableTopIfNonEmpty(null, null)) {
                            newTableTop();
                        }
                    }
                };
                
                newAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_NEW));
                newAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_NEW);
                newAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("NewToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return newAction;
    }

    /**
     * Reset the table top to a new state.
     */
    private void newTableTop() {

        // Close all open result frames
        outputDisplayManager.closeAllResultFrames();

        // Clear the tabletop
        getTableTop().doNewTableTopUserAction();
    
        // Clear the undo stack and dirty edit whenever a new table top is created
        extendedUndoManager.discardAllEdits();
        editToUndoWhenNonDirty = null;
        updateUndoWidgets();
                
        // Reset the runnable state
        setTargetRunningButtons(false);

        // Reset the TableTop size to match the Viewable size.
        Dimension size = new Dimension(1, 1);
        TableTop tableTop = getTableTop();
        tableTop.getTableTopPanel().setSize(size);
        tableTop.getTableTopPanel().setPreferredSize(size);
        tableTop.getTableTopPanel().revalidate();
        
        // Update the explorer.
        tableTop.addGemGraphChangeListener(getTableTopExplorer());
        getTableTopExplorerAdapter().getTableTopExplorer().rebuildTree();

        // Add listeners for updating the current gem to run and the current collector
        tableTop.addGemGraphChangeListener(currentGemToRunListener);
        tableTop.addGemGraphChangeListener(currentCollectorListener);
        //tableTop.add
        tableTop.getGemGraph().addGemConnectionListener(targetRunnableListener);        
        
        // Update the current gem to run and the current collector
        currentGemToRunListener.selectNewGem();
        currentCollectorListener.selectNewReflectorCollector();
    }

    /**
     * @return the Action for opening a gem design
     */
    private Action getOpenAction() {
        
        if (openAction == null) {
            try {
                openAction = new AbstractAction (getResourceString("Open"),
                        new ImageIcon(getClass().getResource("/Resources/open.gif"))) {

                    private static final long serialVersionUID = -4225999206287072243L;

                    public void actionPerformed(ActionEvent evt) {
                        openGemDesign(null);
                    }
                };
                
                openAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_OPEN));
                openAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_OPEN);
                openAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("OpenToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return openAction;
    }
    
    /**
     * Return the action that handles saving the current target gem.
     * @return Action 
     */
    private Action getSaveGemAction() {
        
        if (saveGemAction == null) {
            try {
                saveGemAction = new AbstractAction (getResourceString("SaveGemFromList"), 
                        new ImageIcon(getClass().getResource("/Resources/save.gif"))) {

                    private static final long serialVersionUID = 5568345421791750368L;

                    public void actionPerformed(ActionEvent evt) {
                        saveGem();
                    }
                };
                
                saveGemAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_SAVE_GEM));
                saveGemAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_SAVE_GEM);
                saveGemAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("SaveGemFromListToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return saveGemAction;
    }

    /**
     * Return the action that handles the closing of the GemCutter.
     * @return Action
     */
    private Action getExitAction() {
        
        if (exitAction == null) {
            try {
                exitAction = new AbstractAction (getResourceString("Exit")) {
                                                            
                    private static final long serialVersionUID = -6645421153668615170L;

                    public void actionPerformed(ActionEvent evt) {
                        if (promptSaveCurrentTableTopIfNonEmpty(null, null)){
                            dispose();
                        }
                    }
                };
                
                exitAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_EXIT));
                exitAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ExitToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return exitAction;
    }
    
    /**
     * Return the DebugMenu property value.
     * @return JMenu
     */
    private JMenu getDebugMenu() {
        if (debugMenu == null) {
            try {
                debugMenu = new JMenu();
                debugMenu.setName("DebugMenu");
                debugMenu.setText(getResourceString("DebugMenu"));
                debugMenu.setMargin(new Insets(2, 0, 2, 0));
                debugMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_DEBUG_MENU);
                
                debugMenu.add(makeNewMenuItem(getCheckGraphAction()));
                debugMenu.add(makeNewMenuItem(getDumpDefinitionAction()));                
                debugMenu.add(makeNewMenuItem(getDumpGraphAction()));
                debugMenu.addSeparator();

                debugMenu.add(makeNewMenuItem(dumpOrphanedMetadataAction()));
                debugMenu.add(makeNewMenuItem(getLoadSaveAllMetadataAction()));
                debugMenu.add(makeNewMenuItem(getTestExamplesAction()));
                debugMenu.add(makeNewMenuItem(getDumpInconsistententArgumentMetadataAction()));
                debugMenu.addSeparator();

                debugMenu.add(makeNewMenuItem(getDumpReferenceFrequenciesAction()));
                debugMenu.add(makeNewMenuItem(getDumpCompositionalFrequenciesAction()));
                debugMenu.add(makeNewMenuItem(getDumpLintWarningsAction()));
                debugMenu.addSeparator();

                JCheckBoxMenuItem allowPreludeRenamingMenuItem = makeNewCheckBoxMenuItem(getAllowPreludeRenamingAction());
                allowPreludeRenamingMenuItem.setSelected(isAllowPreludeRenamingMode());
                debugMenu.add(allowPreludeRenamingMenuItem);

                JCheckBoxMenuItem allowDuplicateRenamingMenuItem = makeNewCheckBoxMenuItem(getAllowDuplicateRenamingAction());
                allowDuplicateRenamingMenuItem.setSelected(isAllowDuplicateRenamingMode());
                debugMenu.add(allowDuplicateRenamingMenuItem);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return debugMenu;
    }
   
   /** Get an action to dump the gem definition associated with the target collector. */
    private Action getDumpDefinitionAction() {         
        if(dumpDefinitionAction == null) {
            dumpDefinitionAction = new AbstractAction(getResourceString("DumpGemDefinition")) {
                private static final long serialVersionUID = -9075454446137881013L;

                /** Get the target collector and dump it's definition to the console */
                public void actionPerformed(ActionEvent evt) {
                    CollectorGem targetCollector = tableTop.getTargetCollector();
                    Target targetGem = tableTop.getDisplayedGem(targetCollector).getTarget();                    
                    MetaModule currentMetaModule = getWorkspace().getMetaModule(getWorkingModuleName());
                    ModuleTypeInfo currentModuleTypeInfo = currentMetaModule.getTypeInfo();
                    
                    System.out.println("Gem Definition:");
                    System.out.println(targetGem.getTargetDef(null, currentModuleTypeInfo)+"\n");
                    
                }
            };                
            dumpDefinitionAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DUMPDEFINITION));
            
            // Set this action to disabled for now
            dumpDefinitionAction.setEnabled(false);            
        }
        return dumpDefinitionAction;
    }
    
    /**
     * @return the action which toggles whether renaming gems in the Prelude module is allowed
     */
    private Action getAllowPreludeRenamingAction() {
        
        if (allowPreludeRenamingAction == null) {
            try {
                allowPreludeRenamingAction = new AbstractAction (getResourceString("AllowPreludeRenaming")) {
                                                            
                    private static final long serialVersionUID = 3990990055098390968L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        // This just toggles the debug output mode
                        boolean oldValue = ((Boolean)getValue("InAllowRenamingMode")).booleanValue();
                        putValue("InAllowRenamingMode", Boolean.valueOf(!oldValue));
                    }
                };
                
                //allowPreludeRenamingAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DEBUG_OUTPUT));
                allowPreludeRenamingAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AllowPreludeRenamingToolTip"));
                
                // Get the action to remember if we are allowing renaming
                allowPreludeRenamingAction.putValue("InAllowRenamingMode", Boolean.FALSE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return allowPreludeRenamingAction;
    }
    
    /**
     * @return the action which toggles whether renaming gems to an already-existing name is allowed
     */
    private Action getAllowDuplicateRenamingAction() {
        
        if (allowDuplicateRenamingAction == null) {
            try {
                allowDuplicateRenamingAction = new AbstractAction (getResourceString("AllowDuplicateRenaming")) {

                    private static final long serialVersionUID = -7687196097621043422L;

                    public void actionPerformed(ActionEvent evt) {

                        // This just toggles the debug output mode
                        boolean oldValue = ((Boolean)getValue("InAllowDuplicateRenamingMode")).booleanValue();
                        putValue("InAllowDuplicateRenamingMode", Boolean.valueOf(!oldValue));
                    }
                };
                
                allowDuplicateRenamingAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AllowDuplicateRenamingToolTip"));
                
                // Get the action to remember if we are allowing renaming
                allowDuplicateRenamingAction.putValue("InAllowDuplicateRenamingMode", Boolean.FALSE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return allowDuplicateRenamingAction;
    }
    
    /**
     * @return the action which tests metadata examples.
     */
    private Action getTestExamplesAction() {
        
        Action testAction = new AbstractAction(getResourceString("TestMetadataExamples")) {
            private static final long serialVersionUID = -2124039687691058698L;

            public void actionPerformed(ActionEvent evt) {
                getNavigatorOwner().testMetadataExamples();
            }
        };
        
        testAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_TESTMETADATA));                
        testAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("TestMetadataExamplesToolTip"));

        return testAction;
    }
    
    private Action getCheckGraphAction() {
        
        Action checkAction = new AbstractAction(getResourceString("CheckGraphText")) {
            private static final long serialVersionUID = 4810094313633625167L;

            public void actionPerformed(ActionEvent evt) {
                TableTop tTop = getTableTop();
                Set<Gem> nodeSet = tTop.getGemGraph().getRoots();
                System.out.println("Check Graph Source Text:");
                System.out.println(CALSourceGenerator.getDebugCheckGraphSource(nodeSet));                
            }
        };
        
        checkAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_CHECKGRAPH));
        return checkAction;
    }
    
    /**
     * @return the action which dumps the gem graph for the current tabletop.
     */
    private Action getDumpGraphAction() {
        
        Action dumpAction = new AbstractAction(getResourceString("DumpGemGraph")) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent evt) {
                TableTop tTop = getTableTop();
                String graphStr = tTop.getGemGraph().toString();
                System.out.println(graphStr);
            }
        };
        
        dumpAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DUMPGRAPH));
        return dumpAction;
    }
    
    /**
     * A customized FileChooser for dumping CSV files that includes some checkboxes for various
     * options (such as grouping by module, etc). 
     * 
     * Creation date: (Jun 29, 2005)
     * @author Jawright
     */
    private static class DumpOptionChooser extends JFileChooser {
        private static final long serialVersionUID = -5334307235048868783L;

        /** UI element for the filter out test modules option */
        private final JCheckBox filterTestModulesCheckbox = new JCheckBox(getResourceString("FilterTestModulesOption"));

        /** UI element (checkbox) for the exclude functions by regexp option */
        private final JCheckBox excludeFunctionsCheckbox = new JCheckBox();
        
        /** UI element (text field) for the exclude functions by regexp option */
        private final JComboBox excludeFunctionsField = new JComboBox();
        
        /**
         * Construct a DumpOptionChooser.
         */
        DumpOptionChooser() {
            super();
            
            // Restore options from preferences
            filterTestModulesCheckbox.setSelected(getPreferences().getBoolean(FILTER_TEST_MODULES_PREF_KEY, FILTER_TEST_MODULES_DEFAULT));
            excludeFunctionsCheckbox.setSelected(getPreferences().getBoolean(EXCLUDE_FUNCTIONS_BY_REGEXP_PREF_KEY, EXCLUDE_FUNCTIONS_BY_REGEXP_DEFAULT));
            excludeFunctionsCheckbox.setAction(
                    new AbstractAction(getResourceString("ExcludeFunctionsOption")) {
                        private static final long serialVersionUID = -7700111529077716606L;

                        public void actionPerformed(final ActionEvent e) {
                            excludeFunctionsField.setEnabled(shouldExcludeFunctionsByRegexp());
                        }
                    });
            
            final String prefExcludeRegexp = getPreferences().get(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_PREF_KEY, EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT);
            excludeFunctionsField.addItem(prefExcludeRegexp);

            if(!prefExcludeRegexp.equals(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT)) {
                excludeFunctionsField.addItem(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT);
            }
            excludeFunctionsField.setEditable(true);
            excludeFunctionsField.getEditor().setItem(getPreferences().get(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_PREF_KEY, EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT));
            excludeFunctionsField.setEnabled(shouldExcludeFunctionsByRegexp());
            
            // Set up other customizations
            final FileFilter filter = new ExtensionFileFilter("csv", getResourceString("CSVFileFilterName")); 
          
            setAcceptAllFileFilterUsed(true);
            addChoosableFileFilter(filter);
            setFileFilter(filter);            
        }
        
        /**
         * We override the createDialog method of JFileChooser in order to add the extra preference
         * UI elements.
         */
        protected JDialog createDialog(final Component parent) {
            final JDialog dialog = super.createDialog(parent);
            
            // The default dialog places the file chooser in the center; we want it
            // elsewhere.
            dialog.getContentPane().remove(this);
            
            final Box optionBox = Box.createVerticalBox();
            
            final Box filterTestModulesBox = Box.createHorizontalBox();
            filterTestModulesBox.add(filterTestModulesCheckbox);
            filterTestModulesBox.add(Box.createGlue());
            optionBox.add(filterTestModulesBox);
            
            final Box excludeFunctionsByRegexpBox = Box.createHorizontalBox();
            
            excludeFunctionsByRegexpBox.add(excludeFunctionsCheckbox);
            excludeFunctionsByRegexpBox.add(excludeFunctionsField);
            excludeFunctionsByRegexpBox.add(Box.createGlue());
            optionBox.add(excludeFunctionsByRegexpBox);
            
            optionBox.setBorder(BorderFactory.createTitledBorder(getResourceString("OutputPreferencesBorderTitle")));
            
            dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
            dialog.getContentPane().add(this);
            dialog.getContentPane().add(optionBox);
            
            dialog.setModal(true);
            dialog.pack();
            
            return dialog;
        }
        
        /**
         * @return true when the filter-out-test-modules option has been selected.
         */
        boolean shouldFilterTestModules() {
            return filterTestModulesCheckbox.isSelected();
        }
    
        /**
         * @return true when the "exclude functions matching regexp" option has been selected
         */
        boolean shouldExcludeFunctionsByRegexp() {
            return excludeFunctionsCheckbox.isSelected();
        }
        
        /**
         * @return The regexp to use to filter functions 
         */
        String getExcludeFunctionsRegexp() {
            return (String)excludeFunctionsField.getEditor().getItem();
        }
        
        /**
         * Saves the current options state to preferences.
         */
        void savePreferences() {
            getPreferences().putBoolean(EXCLUDE_FUNCTIONS_BY_REGEXP_PREF_KEY, shouldExcludeFunctionsByRegexp());
            getPreferences().put(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_PREF_KEY, getExcludeFunctionsRegexp());
            getPreferences().putBoolean(FILTER_TEST_MODULES_PREF_KEY, shouldFilterTestModules());
        }
    }
    
    /**
     * @return the Action that dumps the reference frequency of each gem in the workspace
     *          to a user-specified file.
     */
    private Action getDumpReferenceFrequenciesAction() {
        
        Action dumpAction = new AbstractAction(getResourceString("DumpReferenceFrequencies")) {
            private static final long serialVersionUID = -3222695625267056652L;

            public void actionPerformed(ActionEvent evt) {
                SourceMetrics workspaceSourceMetrics = perspective.getWorkspace().getSourceMetrics();

              DumpOptionChooser chooser = new DumpOptionChooser();
              int fileResponse = chooser.showSaveDialog(GemCutter.this);
              
              // If the user closed the dialog or hit cancel, do nothing
              // If they didn't, then remember their option selections for next time.
              if (fileResponse == JFileChooser.CANCEL_OPTION || fileResponse == JFileChooser.ERROR_OPTION) {
                  return;
              }
              chooser.savePreferences();
              
              ModuleFilter moduleFilter;
              if (chooser.shouldFilterTestModules()) {
                  moduleFilter = new ExcludeTestModulesFilter(getWorkspace());
              } else {
                  moduleFilter = new AcceptAllModulesFilter();
              }
              
              QualifiedNameFilter functionFilter;
              if (chooser.shouldExcludeFunctionsByRegexp()) {
                  functionFilter = new RegExpBasedUnqualifiedNameFilter(chooser.getExcludeFunctionsRegexp(), true);
              } else {
                  functionFilter = new AcceptAllQualifiedNamesFilter();
              }
              
              try {
                  FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
                  PrintStream ps = new PrintStream(fos);
                  ps.print(workspaceSourceMetrics.dumpReferenceFrequencies(moduleFilter, functionFilter, true));
                  ps.close();
                  fos.close();
              } catch(FileNotFoundException e) {
                  String errTitle = getResourceString("DumpFrequenciesErrorDialogTitle");
                  String errMessage = GemCutterMessages.getString("DumpFrequenciesErrorDialogMessage", chooser.getSelectedFile().toString());
                  JOptionPane.showMessageDialog(GemCutter.this, errMessage, errTitle, JOptionPane.ERROR_MESSAGE);
              } catch(IOException e) {
                  String errTitle = getResourceString("DumpFrequenciesErrorDialogTitle");
                  String errMessage = GemCutterMessages.getString("DumpFrequenciesErrorDialogTitle", chooser.getSelectedFile().toString());
                  JOptionPane.showMessageDialog(GemCutter.this, errMessage, errTitle, JOptionPane.ERROR_MESSAGE);
              }
          }
        };
        
        dumpAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DUMPFREQUENCIES));
        return dumpAction;
    }
    
    /**
     * @return the Action that dumps the reference frequency of each gem in the workspace
     *          to a user-specified file.
     */
    private Action getDumpCompositionalFrequenciesAction() {
        
        Action dumpAction = new AbstractAction(getResourceString("DumpCompositionalFrequencies")) {
          private static final long serialVersionUID = -8849198411020849911L;

        public void actionPerformed(ActionEvent evt) {
              SourceMetrics workspaceSourceMetrics = perspective.getWorkspace().getSourceMetrics();

              DumpOptionChooser chooser = new DumpOptionChooser();
              int fileResponse = chooser.showSaveDialog(GemCutter.this);
            
              // If the user closed the dialog or hit cancel, do nothing
              if (fileResponse == JFileChooser.CANCEL_OPTION || fileResponse == JFileChooser.ERROR_OPTION) {
                  return;
              }
              chooser.savePreferences();
              
              ModuleFilter moduleFilter;
              if (chooser.shouldFilterTestModules()) {
                  moduleFilter = new ExcludeTestModulesFilter(getWorkspace());
              } else {
                  moduleFilter = new AcceptAllModulesFilter();
              }
              
              QualifiedNameFilter functionFilter;
              if (chooser.shouldExcludeFunctionsByRegexp()) {
                  functionFilter = new RegExpBasedUnqualifiedNameFilter(chooser.getExcludeFunctionsRegexp(), true);
              } else {
                  functionFilter = new AcceptAllQualifiedNamesFilter();
              }
              
              try {
                  FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile());
                  PrintStream ps = new PrintStream(fos);
                  ps.print(workspaceSourceMetrics.dumpCompositionalFrequencies(moduleFilter, functionFilter, true));
                  ps.close();
                  fos.close();
              } catch(FileNotFoundException e) {
                  String errTitle = getResourceString("DumpFrequenciesErrorDialogTitle");
                  String errMessage = GemCutterMessages.getString("DumpFrequenciesErrorDialogMessage", chooser.getSelectedFile().toString());
                  JOptionPane.showMessageDialog(GemCutter.this, errMessage, errTitle, JOptionPane.ERROR_MESSAGE);
              } catch(IOException e) {
                  String errTitle = getResourceString("DumpFrequenciesErrorDialogTitle");
                  String errMessage = GemCutterMessages.getString("DumpFrequenciesErrorDialogTitle", chooser.getSelectedFile().toString());
                  JOptionPane.showMessageDialog(GemCutter.this, errMessage, errTitle, JOptionPane.ERROR_MESSAGE);
              }
          }
        };
        
        dumpAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DUMPCOMPOSITIONALFREQUENCIES));
        return dumpAction;
    }
    
    /**
     * A custom dialogue for choosing the various options associated with running a 
     * lint check.
     * 
     * @author Jawright
     */
    private static class LintOptionChooser extends JDialog {
        
        private static final long serialVersionUID = -3249297142628860383L;

        /** UI element (checkbox) for the filter out test modules option */
        private final JCheckBox filterTestModulesCheckbox = new JCheckBox(getResourceString("FilterTestModulesOption"));

        /** UI element (checkbox) for the exclude functions by regexp option */
        private final JCheckBox excludeFunctionsCheckbox = new JCheckBox();
        
        /** UI element (text field) for the exclude functions by regexp option */
        private final JComboBox excludeFunctionsField = new JComboBox();
        
        /** UI element (checkbox) for the trace skipped modules & functions option */
        private final JCheckBox traceSkippedCheckbox = new JCheckBox(getResourceString("TraceSkippedOption"));
        
        /** UI element (checkbox) for the redundant lambdas check */
        private final JCheckBox includeRedundantLambdasCheckbox = new JCheckBox(getResourceString("IncludeRedundantLambdasOption"));
        
        /** UI element (checkbox) for the unplinged primitive args check */
        private final JCheckBox includeUnplingedArgsCheckbox = new JCheckBox(getResourceString("IncludeUnplingedArgsOption"));
        
        /** UI element (checkbox) for the unused private functions check */
        private final JCheckBox includeUnusedPrivates = new JCheckBox(getResourceString("IncludeUnusedPrivates"));
        
        /** UI element (checkbox) for the mismatched plinging of alias function parameters check */
        private final JCheckBox includeMismatchedAliasPlings = new JCheckBox(getResourceString("MismatchedAliasPlings"));
        
        /** UI element (checkbox) for the unreferenced let variables check */
        private final JCheckBox includeUnreferencedLetVariables = new JCheckBox(getResourceString("IncludeUnreferencedLetVariables"));
        
        private final JButton okButton = new JButton(getResourceString("LOC_OK"));
        private final JButton cancelButton = new JButton(getResourceString("LOC_Cancel"));
        
        /** true if the user selected the OK button */
        private boolean okSelected = false;
        
        /** Construct a new option dialog for the lint checks*/
        LintOptionChooser(JFrame parentFrame) {
            super(parentFrame);
            initialize();
        }
        
        /**
         * Initialize the class.
         */
        private void initialize() {
            
            // Basic window properties
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle(getResourceString("LintOptionDialog"));
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setContentPane(mainPanel);
            
            // Restore options from preferences
            includeRedundantLambdasCheckbox.setSelected(getPreferences().getBoolean(INCLUDE_REDUNDANT_LAMBDAS_PREF_KEY, INCLUDE_UNPLINGED_PRIMITIVE_ARGS_DEFAULT));
            includeUnplingedArgsCheckbox.setSelected(getPreferences().getBoolean(INCLUDE_UNPLINGED_PRIMITIVE_ARGS_PREF_KEY, INCLUDE_UNPLINGED_PRIMITIVE_ARGS_DEFAULT));
            includeMismatchedAliasPlings.setSelected(getPreferences().getBoolean(INCLUDE_MISMATCHED_WRAPPER_PLINGS_PREF_KEY, INCLUDE_MISMATCHED_WRAPPER_PLINGS_DEFAULT));
            includeUnusedPrivates.setSelected(getPreferences().getBoolean(INCLUDE_UNUSED_PRIVATE_FUNCTIONS_PREF_KEY, INCLUDE_UNUSED_PRIVATE_FUNCTIONS_DEFAULT));
            includeUnreferencedLetVariables.setSelected(getPreferences().getBoolean(INCLUDE_REFERENCED_LET_VARIABLES_PREF_KEY, INCLUDE_REFERENCED_LET_VARIABLES_DEFAULT));
            
            traceSkippedCheckbox.setSelected(getPreferences().getBoolean(TRACE_SKIPPED_PREF_KEY, TRACE_SKIPPED_DEFAULT));
            filterTestModulesCheckbox.setSelected(getPreferences().getBoolean(FILTER_TEST_MODULES_PREF_KEY, FILTER_TEST_MODULES_DEFAULT));
            excludeFunctionsCheckbox.setSelected(getPreferences().getBoolean(EXCLUDE_FUNCTIONS_BY_REGEXP_PREF_KEY, EXCLUDE_FUNCTIONS_BY_REGEXP_DEFAULT));
            excludeFunctionsCheckbox.setAction(
                    new AbstractAction(getResourceString("ExcludeFunctionsOption")) {
                        private static final long serialVersionUID = 2004612193586063898L;

                        public void actionPerformed(ActionEvent e) {
                            excludeFunctionsField.setEnabled(shouldExcludeFunctionsByRegexp());
                        }
                    });
            
            String prefExcludeRegexp = getPreferences().get(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_PREF_KEY, EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT);
            excludeFunctionsField.addItem(prefExcludeRegexp);
            if(!prefExcludeRegexp.equals(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT)) {
                excludeFunctionsField.addItem(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT);
            }
            excludeFunctionsField.setEditable(true);
            excludeFunctionsField.getEditor().setItem(getPreferences().get(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_PREF_KEY, EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT));
            excludeFunctionsField.setEnabled(shouldExcludeFunctionsByRegexp());

            // Layout
            Box excludeFunctionsBox = Box.createHorizontalBox();
            excludeFunctionsBox.add(excludeFunctionsCheckbox);
            excludeFunctionsBox.add(excludeFunctionsField);
            excludeFunctionsBox.add(Box.createHorizontalGlue());
            excludeFunctionsField.setMinimumSize(new Dimension(235, 23));
            excludeFunctionsField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 23));
            
            Box filterTestModulesBox = Box.createHorizontalBox();
            filterTestModulesBox.add(filterTestModulesCheckbox);
            filterTestModulesBox.add(Box.createHorizontalGlue());
            
            Box traceSkippedBox = Box.createHorizontalBox();
            traceSkippedBox.add(traceSkippedCheckbox);
            traceSkippedBox.add(Box.createHorizontalGlue());
            
            Box optionsBox = Box.createVerticalBox();
            optionsBox.add(filterTestModulesBox);
            optionsBox.add(excludeFunctionsBox);
            optionsBox.add(traceSkippedBox);
            optionsBox.setBorder(BorderFactory.createTitledBorder(getResourceString("OutputPreferencesBorderTitle")));

            Box checkTypesInnerBox = Box.createVerticalBox();
            checkTypesInnerBox.add(includeRedundantLambdasCheckbox);
            checkTypesInnerBox.add(includeUnplingedArgsCheckbox);
            checkTypesInnerBox.add(includeUnusedPrivates);
            checkTypesInnerBox.add(includeMismatchedAliasPlings);
            checkTypesInnerBox.add(includeUnreferencedLetVariables);
            checkTypesInnerBox.setBorder(BorderFactory.createTitledBorder(getResourceString("LintCheckTypesBorderTitle")));

            Box checkTypesBox = Box.createHorizontalBox();
            checkTypesBox.add(checkTypesInnerBox);
            checkTypesBox.add(Box.createHorizontalGlue());
            
            Box buttonBox = Box.createHorizontalBox();
            buttonBox.add(Box.createHorizontalGlue());
            buttonBox.add(okButton);
            buttonBox.add(Box.createHorizontalStrut(10));
            buttonBox.add(cancelButton);
            buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 2, 2));
            
            Box uberBox = Box.createVerticalBox();
            uberBox.add(checkTypesBox);
            uberBox.add(optionsBox);
            
            getContentPane().add(uberBox, "Center");
            getContentPane().add(buttonBox, "South");
            
            // Actions
            okButton.setAction(
                    new AbstractAction(getResourceString("LOC_OK")) {
                        private static final long serialVersionUID = 5544185100443091371L;

                        public void actionPerformed(ActionEvent e) {
                            okSelected = true;
                            LintOptionChooser.this.dispose();
                        }
                    });

            cancelButton.setAction(
                    new AbstractAction(getResourceString("LOC_Cancel")) {
                        private static final long serialVersionUID = -3915109617604551485L;

                        public void actionPerformed(ActionEvent e) {
                            okSelected = false;
                            LintOptionChooser.this.dispose();
                        }
                    });

            // Commit
            setModal(true);
            pack();
        }        

        /**
         * @return true when the filter-out-test-modules option has been selected.
         */
        boolean shouldFilterTestModules() {
            return filterTestModulesCheckbox.isSelected();
        }
    
        /**
         * @return true when the "exclude functions matching regexp" option has been selected
         */
        boolean shouldExcludeFunctionsByRegexp() {
            return excludeFunctionsCheckbox.isSelected();
        }
        
        /**
         * @return The regexp to use to filter functions 
         */
        String getExcludeFunctionsRegexp() {
            return (String)excludeFunctionsField.getSelectedItem();
        }
        
        /**
         * @return true if the "warn about redundant lambdas" option has been selected
         */
        boolean shouldIncludeRedundantLambdas() {
            return includeRedundantLambdasCheckbox.isSelected();
        }
        
        /**
         * @return true if the "warn about unplinged primitive args" option has been selected
         */
        boolean shouldIncludeUnplingedPrimitiveArgs() {
            return includeUnplingedArgsCheckbox.isSelected();
        }
        
        /**
         * @return true if the "dump skipped functions/modules" option has been selected
         */
        boolean shouldTraceSkipped() {
            return traceSkippedCheckbox.isSelected();
        }
        
        /**
         * @return true if the "warn about unused private functions" option has been selected
         */
        boolean shouldIncludeUnusedPrivates() {
            return includeUnusedPrivates.isSelected();
        }
        
        /**
         * @return true if the "warn about alias functions with mismatched parameter plings" option has been selected
         */
        boolean shouldIncludeMismatchedAliasPlings() {
            return includeMismatchedAliasPlings.isSelected();
        }
        
        /**
         * @return true if the "warn about unreferenced let variables" option has been selected
         */
        boolean shouldIncludeUnreferencedLetVariables() {
            return includeUnreferencedLetVariables.isSelected();
        }
        
        /**
         * @return true if the user pushed the OK button, or false otherwise
         */
        boolean getOkSelected() {
            return okSelected;
        }
        
        /**
         * Saves the current options state to preferences.
         */
        void savePreferences() {
            getPreferences().putBoolean(EXCLUDE_FUNCTIONS_BY_REGEXP_PREF_KEY, shouldExcludeFunctionsByRegexp());
            getPreferences().put(EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_PREF_KEY, getExcludeFunctionsRegexp());
            getPreferences().putBoolean(FILTER_TEST_MODULES_PREF_KEY, shouldFilterTestModules());
            getPreferences().putBoolean(INCLUDE_REDUNDANT_LAMBDAS_PREF_KEY, shouldIncludeRedundantLambdas());
            getPreferences().putBoolean(INCLUDE_UNPLINGED_PRIMITIVE_ARGS_PREF_KEY, shouldIncludeUnplingedPrimitiveArgs());
            getPreferences().putBoolean(INCLUDE_UNUSED_PRIVATE_FUNCTIONS_PREF_KEY, shouldIncludeUnusedPrivates());
            getPreferences().putBoolean(INCLUDE_MISMATCHED_WRAPPER_PLINGS_PREF_KEY, shouldIncludeMismatchedAliasPlings());
            getPreferences().putBoolean(TRACE_SKIPPED_PREF_KEY, shouldTraceSkipped());
        }
    }
    
    /**
     * @return the Action that dumps lint warnings for every module in the current workspace to
     *          the system console.
     */
    private Action getDumpLintWarningsAction() {
        
        Action lintAction = new AbstractAction(getResourceString("DumpLintWarnings")) {
            private static final long serialVersionUID = -8628525045109137685L;

            public void actionPerformed(ActionEvent evt) {
                
                LintOptionChooser chooser = new LintOptionChooser(GemCutter.this);
                centerWindow(chooser);
                chooser.setVisible(true);
                
                ModuleFilter moduleFilter;
                if (chooser.shouldFilterTestModules()) {
                    moduleFilter = new ExcludeTestModulesFilter(getWorkspace());
                } else {
                    moduleFilter = new AcceptAllModulesFilter();
                }
                
                QualifiedNameFilter functionFilter;
                if (chooser.shouldExcludeFunctionsByRegexp()) {
                    functionFilter = new RegExpBasedUnqualifiedNameFilter(chooser.getExcludeFunctionsRegexp(), true);
                } else {
                    functionFilter = new AcceptAllQualifiedNamesFilter();
                }
                
                if(chooser.getOkSelected()) {
                    chooser.savePreferences();
                    System.out.println("Dumping lint warnings:");
                    SourceMetrics workspaceSourceMetrics = perspective.getWorkspace().getSourceMetrics();
                    workspaceSourceMetrics.dumpLintWarnings(moduleFilter, functionFilter, 
                                                            chooser.shouldTraceSkipped(),
                                                            chooser.shouldIncludeUnplingedPrimitiveArgs(), chooser.shouldIncludeRedundantLambdas(), chooser.shouldIncludeUnusedPrivates(), chooser.shouldIncludeMismatchedAliasPlings(), chooser.shouldIncludeUnreferencedLetVariables());
                    System.out.println("done.");
                }
            }
        };
    
        lintAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DUMPLINTWARNINGS));
        return lintAction;
    }
    
    /**
     * @return the action which attempts to load and save all metadata entries in the metadata manager
     */
    private Action getLoadSaveAllMetadataAction() {
        
        Action loadSaveAction = new AbstractAction(getResourceString("LoadSaveAllMetadata")) {
            private static final long serialVersionUID = 7037711280147379093L;

            public void actionPerformed(ActionEvent evt) {
                System.out.println("Loading and saving all metadata resources in the workspace...");
                
                ModuleName[] workspaceModuleNames = getWorkspace().getModuleNames();
                
                // loop through each module in the workspace
                for (final ModuleName moduleName : workspaceModuleNames) {
                    
                    ResourceManager metadataResourceManager = getWorkspace().getResourceManager(moduleName, WorkspaceResource.METADATA_RESOURCE_TYPE);
                    
                    MetadataStore metadataStore = (MetadataStore)metadataResourceManager.getResourceStore();
                    if (metadataStore.isWriteable()) {
                        for (Iterator<WorkspaceResource> it = metadataStore.getResourceIterator(moduleName); it.hasNext(); ) {
                            WorkspaceResource metadataResource = it.next();
                            ResourceIdentifier identifier = metadataResource.getIdentifier();
                            CALFeatureName featureName = (CALFeatureName)identifier.getFeatureName();
                            Locale locale = LocalizedResourceName.localeOf(identifier.getResourceName());
                            if (getWorkspace().getMetaModule(featureName.toModuleName()) != null) {
                                CALFeatureMetadata metadata = getWorkspace().getMetadata(featureName, locale);
                                if (!getWorkspace().saveMetadata(metadata)) {
                                    System.out.println("Error saving metadata for " + featureName + " for " + (locale == null ? "default" : locale.toString()) + " locale.");
                                } 
                            }
                        }
                    } else {
                        System.out.println("Module " + moduleName + " comes from a read-only store.");
                    }
                }
                System.out.println("Finished.");
            }
        };
        
        loadSaveAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_LOADSAVEMETADATA));
        return loadSaveAction;
    }

    /**
     * @return the Action which dumps a report of each gem whose argument names (in metadata)
     *          are not consistent with its 
     */
    private Action getDumpInconsistententArgumentMetadataAction() {
        Action dumpAction = new AbstractAction(getResourceString("DumpInconsistentArgumentMetadata")) {
            private static final long serialVersionUID = 2301791099324811915L;

            public void actionPerformed(ActionEvent evt) {
                System.out.println("Dumping gems in the "+perspective.getWorkingModule().getName() +" module w/inconsistent metadata argument names...");
                
                Set<GemEntity> gemSet = perspective.getVisibleGemEntities(perspective.getWorkingModule());
                for (final GemEntity gemEntity : gemSet) {
                    FunctionalAgentMetadata[] metadataArray = gemEntity.getMetadataForAllLocales();
                    
                    for (final FunctionalAgentMetadata metadata : metadataArray) {
                        ArgumentMetadata[] argMetadata = metadata.getArguments();
                        int numNamedArgs = gemEntity.getNNamedArguments();
                        boolean inconsistent = false;
                        
                        // Step through the arguments until we find an inconsistency 
                        for (int i = 0; i < numNamedArgs && i < argMetadata.length && inconsistent==false; i++) {
                            String nameFromCode = gemEntity.getNamedArgument(i);
                            String nameFromMetadata = argMetadata[i].getDisplayName();
                            
                            if (nameFromCode != null && nameFromMetadata != null && !nameFromCode.equals(nameFromMetadata)) {
                                inconsistent = true;
                            }
                        }
                        
                        // If we found an inconsistency, step through again to display the names of each arg
                        if (inconsistent) {
                            System.out.println(gemEntity.getName().getQualifiedName() + ":");
                            
                            for(int i = 0; i < Math.max(numNamedArgs, argMetadata.length); i++) {
                                String nameFromCode = i < numNamedArgs? gemEntity.getNamedArgument(i) : null;
                                String nameFromMetadata = i < argMetadata.length? argMetadata[i].getDisplayName() : null;
                                
                                System.out.println("    "+nameFromCode + " vs. " + nameFromMetadata);    
                            }
                        }
                    }
                }
            
                System.out.println("Finished");
            }
        };
        
        dumpAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DUMPINCONSISTENTARGUMENTMETADATA));
        return dumpAction;
    }
    
    /**
     * @return the Action which generates HTML documentation from CALDoc (and metadata).
     */
    private Action getGenerateCALDocDocumentationAction() {
        
        Action lintAction = new AbstractAction(getResourceString("GenerateCALDocDocumentation")) {
            private static final long serialVersionUID = 6391638158996429084L;

            public void actionPerformed(ActionEvent evt) {
                
                CALDocGenerationDialog dialog = new CALDocGenerationDialog(GemCutter.this, workspaceManager.getWorkspace());
                centerWindow(dialog);
                dialog.setVisible(true);
                
                if(dialog.isOKSelected()) {
                    Logger logger = Logger.getAnonymousLogger();
                    logger.setLevel(Level.FINEST);
                    logger.setUseParentHandlers(false);
                    
                    SimpleConsoleHandler handler = new SimpleConsoleHandler();
                    handler.setLevel(Level.FINE);
                    logger.addHandler(handler);
                    
                    doGenerateCALDocDocumentationAction(dialog.getConfiguration(logger, workspaceManager));
                }
            }
        };
        
        lintAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DUMPLINTWARNINGS));
        return lintAction;
    }
    
    /**
     * Generates CALDoc documentation based on the specified configuration.
     * @param configuration options for the documentation generator.
     */
    private void doGenerateCALDocDocumentationAction(final HTMLDocumentationGeneratorConfiguration configuration) {
        
        // Since this is a long-running operation, we handle it this way:
        // - we launch a separate thread to perform the actual documentation generation
        // - we use a modal dialog that cannot be closed to indicate to the user that the GemCutter is busy
        //
        // The documentation generation thread would then close the modal dialog when it is done, and unblocking
        // the UI in the process.
        
        // Setup the modal dialog
        String message = getResourceString("CALDoc_PleaseWaitDialogMessage");
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[0]);
        
        String dialogTitle = getResourceString("CALDoc_PleaseWaitDialogTitle");
        final JDialog dialog = optionPane.createDialog(this, dialogTitle);
        
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        centerWindow(dialog);
        
        // Create the documentation generation thread
        Thread thread = new Thread() {
            public void run() {
                try {
                    System.out.println("Generating CALDoc documentation.");
                    long before = System.currentTimeMillis();
                    CALDocTool.run(workspaceManager, configuration);
                    long after = System.currentTimeMillis();
                    System.out.println("finished in " + ((after - before) / 1000.0) + " seconds");
                    System.out.println("done.");
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            dialog.dispose();
                        }
                    });
                }
            }
        };
        
        // start the thread then launch the modal dialog (which will be closed by the thread when it is done)
        thread.start();
        dialog.setVisible(true);
    }
    
    /**
     * @return the action which dumps orphaned metadata to the console.
     */
    private Action dumpOrphanedMetadataAction() {
        Action dumpOrphanedMetadataAction = new AbstractAction(getResourceString("FindOrphans")) {
            private static final long serialVersionUID = 1670196195751082059L;

            public void actionPerformed(ActionEvent evt) {
                getWorkspaceManager().dumpOrphanedMetadata();
            }
        };
        
        dumpOrphanedMetadataAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_FINDORPHANS));
        return dumpOrphanedMetadataAction;
    }
    
    /**
     * Return the EditMenu property value.
     * @return JMenu
     */
    private JMenu getEditMenu() {
        if (editMenu == null) {
            try {
                editMenu = new JMenu();
                editMenu.setName("EditMenu");
                editMenu.setText(getResourceString("EditMenu"));
                editMenu.setMargin(new Insets(2, 0, 2, 0));
                editMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_EDIT_MENU);
                
                editMenu.add(getUndoMenuItem());
                editMenu.add(getRedoMenuItem());
                editMenu.addSeparator();
                editMenu.add(makeNewMenuItem(getCutAction()));
                editMenu.add(makeNewMenuItem(getCopyAction()));
                JMenu copySpecialMenu = getCopySpecialMenu(); 
                copySpecialMenu.add(makeNewMenuItem(getCopySpecialImageAction()));
                copySpecialMenu.add(makeNewMenuItem(getCopySpecialTextAction()));
                copySpecialMenu.add(makeNewMenuItem(getCopySpecialTargetSourceAction()));
                editMenu.add(copySpecialMenu);
                editMenu.add(makeNewMenuItem(getPasteAction()));
                
                editMenu.addSeparator();
                editMenu.add(makeNewMenuItem(getDeleteAction()));
                editMenu.add(makeNewMenuItem(getSelectAllAction()));
                editMenu.add(makeNewMenuItem(getSearchAction()));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return editMenu;
    }
    
    /**
     * Return the action that handles undo
     * @return Action
     */
    private Action getUndoAction() {
        
        if (undoAction == null) {
            try {
                undoAction = new AbstractAction(getResourceString("Undo"), 
                                                new ImageIcon(getClass().getResource("/Resources/undo.gif"))) {
                                                            
                    private static final long serialVersionUID = 3825977644698848717L;

                    public void actionPerformed(ActionEvent evt) {
                        undo();                        
                    }
                };
                
                undoAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_UNDO));
                undoAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_UNDO);
                undoAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("UndoToolTip"));
                undoAction.putValue(ACTION_BUTTON_IS_DROP_PARENT_KEY, Boolean.TRUE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return undoAction;
    }

    /**
     * Return the undo button.
     * We need access to this button in order to know where to place the undo drop down menu
     * @return JButton the undo button
     */
    private JButton getUndoButton() {
        if (undoButton == null) {
            try {
                undoButton = makeNewButton(getUndoAction());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return undoButton;
    }
    
    /**
     * Return the action that handles the undo drop down menu.
     * @return Action
     */
    private Action getUndoDropDownAction() {
        
        if (undoDropDownAction == null) {
            try {
                undoDropDownAction = new AbstractAction (getResourceString("Undo"), 
                                                new ImageIcon(getClass().getResource("/Resources/dropdownarrow.gif"))) {
                                                            
                    private static final long serialVersionUID = -9048917705735426064L;

                    public void actionPerformed(ActionEvent evt) {

                        displayUndoDropDownMenu();                        
                    }
                };
                
                undoDropDownAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("UndoDropDownToolTip"));
                undoDropDownAction.putValue(ACTION_BUTTON_IS_DROP_CHILD_KEY, Boolean.TRUE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return undoDropDownAction;
    }

    /**
     * Return the undo drop down button.
     * We need access to this button in order to enable/disable it when the associated undo action is enabled/disabled.
     * @return JButton the undo drop down button
     */
    private JButton getUndoDropDownButton() {
        if (undoDropDownButton == null) {
            try {
                undoDropDownButton = makeNewButton(getUndoDropDownAction());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return undoDropDownButton;
    }
    
    /**
     * Return the undo menu item.
     * We need access to this menu item in order to change its text according to the current undoable action.
     * @return JMenuItem the undo menu item
     */
    private JMenuItem getUndoMenuItem() {
        if (undoMenuItem == null) {
            try {
                undoMenuItem = makeNewMenuItem(getUndoAction());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return undoMenuItem;
    }
    
    /**
     * Return the action that handles redo
     * @return Action
     */
    private Action getRedoAction() {
        
        if (redoAction == null) {
            try {
                redoAction = new AbstractAction(getResourceString("Redo"), 
                                                new ImageIcon(getClass().getResource("/Resources/redo.gif"))) {
                                                            
                    private static final long serialVersionUID = 2949676218178316616L;

                    public void actionPerformed(ActionEvent evt) {
                            
                        redo();                            
                        getTableTopPanel().revalidateValueGemPanels();     
                    }
                };
                
                redoAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_REDO));
                redoAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_REDO);
                redoAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("RedoToolTip"));
                redoAction.putValue(ACTION_BUTTON_IS_DROP_PARENT_KEY, Boolean.TRUE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return redoAction;
    }

    /**
     * Return the redo button.
     * We need access to this button in order to know where to place the redo drop down menu
     * @return JButton the redo button
     */
    private JButton getRedoButton() {
        if (redoButton == null) {
            try {
                redoButton = makeNewButton(getRedoAction());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return redoButton;
    }
    
    /**
     * Return the action that handles the redo drop down menu.
     * @return Action
     */
    private Action getRedoDropDownAction() {
        
        if (redoDropDownAction == null) {
            try {
                redoDropDownAction = new AbstractAction (getResourceString("Redo"), 
                                                new ImageIcon(getClass().getResource("/Resources/dropdownarrow.gif"))) {
                                                            
                    private static final long serialVersionUID = -1753196873058783562L;

                    public void actionPerformed(ActionEvent evt) {

                        displayRedoDropDownMenu();                        
                    }
                };
                
                redoDropDownAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("RedoDropDownToolTip"));
                redoDropDownAction.putValue(ACTION_BUTTON_IS_DROP_CHILD_KEY, Boolean.TRUE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return redoDropDownAction;
    }

    /**
     * Return the redo drop down button.
     * We need access to this button in order to enable/disable it when the associated redo action is enabled/disabled.
     * @return JButton the redo drop down button
     */
    private JButton getRedoDropDownButton() {
        if (redoDropDownButton == null) {
            try {
                redoDropDownButton = makeNewButton(getRedoDropDownAction());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return redoDropDownButton;
    }
    
    /**
     * Return the redo menu item.
     * We need access to this menu item in order to change its text according to the current redoable action.
     * @return JMenuItem the redo menu item
     */
    private JMenuItem getRedoMenuItem() {
        if (redoMenuItem == null) {
            try {
                redoMenuItem = makeNewMenuItem(getRedoAction());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return redoMenuItem;
    }
    
    /**
     * Return the Copy As menu item.
     * We need access to this menu item in order to change its text according to the current selection.
     * @return JMenu
     */
    JMenu getCopySpecialMenu() {
        if (copySpecialMenu == null) {
            try {
                copySpecialMenu = (JMenu)UIUtilities.fixMenuItem(new JMenu(getResourceString("CopySpecial")));
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return copySpecialMenu;
    }

    /**
     * Returns the action that handles the "Cut" edit functionality.
     * @return Action
     */
    Action getCutAction() {
        
        if (cutAction == null) {
            try {
                cutAction = new AbstractAction(getResourceString("Cut"),
                                            new ImageIcon(getClass().getResource("/Resources/cut.gif"))) {
                                                            
                    private static final long serialVersionUID = -3423091075403099772L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        // defer to the table top to do the grunt work
                        getTableTop().doCutUserAction();
                    }
                };
                
                cutAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_CUT));
                cutAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_CUT);
                cutAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("CutToolTip"));
                
                // For now this is disabled, but it will be enabled as necessary
                cutAction.setEnabled(false);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return cutAction;
    }

    /**
     * Returns the action that handles the "Copy" edit functionality.
     * @return Action
     */
    Action getCopyAction() {
        
        if (copyAction == null) {
            try {
                copyAction = new AbstractAction(getResourceString("Copy"),
                                            new ImageIcon(getClass().getResource("/Resources/copy.gif"))) {
                                                            
                    private static final long serialVersionUID = -7627599000753845358L;

                    public void actionPerformed(ActionEvent evt) {
                        // The table top will do the hard part
                        getTableTop().doCopyUserAction();
                    }
                };
                
                copyAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_COPY));
                copyAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_COPY);
                copyAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("CopyToolTip"));
                
                // For now this is disabled
                copyAction.setEnabled(false);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return copyAction;
    }
    
    /**
     * Returns the action that handles the "Copy Special - Image" edit functionality.
     * @return Action
     */
    public Action getCopySpecialImageAction() {
        if (copySpecialImageAction == null) {
            try {
                copySpecialImageAction = new AbstractAction(getResourceString("CopySpecialTableTopImage")) {
                                                            
                    private static final long serialVersionUID = -1565594567048032517L;

                    public void actionPerformed(ActionEvent evt) {
                        // The table top will do the hard part
                        getTableTop().doCopySpecialImageAction();
                    }
                };
                
                copySpecialImageAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_COPY_AS_IMAGE));
                copySpecialImageAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_COPY_AS_IMAGE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return copySpecialImageAction;
    }
    
    /**
     * Returns the action that handles the "Copy Special - CodeGem" edit functionality.
     * @return Action
     */
    public Action getCopySpecialTextAction() {
        if (copySpecialTextAction == null) {
            try {
                copySpecialTextAction = new AbstractAction(getResourceString("CopySpecialCodeGems")) {
                                                            
                    private static final long serialVersionUID = -3867200478494748905L;

                    public void actionPerformed(ActionEvent evt) {
                        // The table top will do the hard part
                        getTableTop().doCopySpecialTextAction();
                    }
                };
                
                copySpecialTextAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_COPY_AS_TEXT));
                copySpecialTextAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_COPY_AS_TEXT);
                
                // For now this is disabled, but it will be enabled as necessary
                copySpecialTextAction.setEnabled(false);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return copySpecialTextAction;
    }
    
    /**
     * Returns the action that handles the "Copy Special - Target Source" edit functionality.
     * @return Action
     */
    public Action getCopySpecialTargetSourceAction() {
        if (copySpecialTargetSourceAction == null) {
            try {
                copySpecialTargetSourceAction = new AbstractAction(getResourceString("CopySpecialTargetSource")) {
                                                            
                    private static final long serialVersionUID = 2828553746957528006L;

                    public void actionPerformed(ActionEvent evt) {
                        // The table top will do the hard part
                        getTableTop().doCopySpecialTargetSourceAction();
                    }
                };
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return copySpecialTargetSourceAction;
    }

    /**
     * Simply calls the tabletop paste function with the items currently on the clipboard
     */
    void pasteFromClipboard() {
        Transferable displayedGemSelection = getClipboard().getContents(this);
        getTableTop().doPasteUserAction(displayedGemSelection);
    }

    /**
     * Returns that action that handles the "Paste" edit functionality
     * @return Action
     */
    Action getPasteAction() {
        
        if (pasteAction == null) {
            try {
                pasteAction = new AbstractAction(getResourceString("Paste"),
                                            new ImageIcon(getClass().getResource("/Resources/paste.gif"))) {
                                                            
                    private static final long serialVersionUID = -7322078839392049685L;

                    public void actionPerformed(ActionEvent evt) {
                        pasteFromClipboard();
                    }
                };
                
                pasteAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_PASTE));
                pasteAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_PASTE);
                pasteAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("PasteToolTip"));
                
                // For now this is disabled
                pasteAction.setEnabled(false);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return pasteAction;
    }

    /**
     * Returns the action that is responsible for deleting gems.
     * Intellicut will be stopped and any selected gems will be deleted.
     * @return Action
     */
    Action getDeleteAction() {
        
        if (deleteAction == null) {
            try {
                deleteAction = new AbstractAction(getResourceString("Delete")) {
                                                            
                    private static final long serialVersionUID = 3597419894568891083L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        getTableTop().handleDeleteSelectedGemsGesture();
                    }
                };
                
                deleteAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DELETE));
                deleteAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_DELETE);
                deleteAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("DeleteToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return deleteAction;
    }

    /**
     * Returns the action that is responsible for selecting all the gems on the TableTop.
     * @return Action
     */
    private Action getSelectAllAction() {
        
        if (selectAllAction == null) {
            try {
                selectAllAction = new AbstractAction(getResourceString("SelectAll")) {
                                                            
                    private static final long serialVersionUID = 2696834229302548654L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        getTableTop().selectAllGems();
                    }
                };
                
                selectAllAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_SELECT_ALL));
                selectAllAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_SELECT_ALL);
                selectAllAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("SelectAllToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return selectAllAction;
    }

    /**
     * Returns the action that handles searching for qualified names
     * @return Action 
     */
    private Action getSearchAction() {
        
        if (searchAction == null) {
            try {
                searchAction = new AbstractAction(getResourceString("Search"),
                                            new ImageIcon(getClass().getResource("/Resources/find.gif"))) {
                                                            
                    private static final long serialVersionUID = 2462653760837686957L;

                    public void actionPerformed(ActionEvent evt) {
                        showSearchDialog();
                    }
                };
                
                searchAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_SEARCH));
                searchAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_SEARCH);
                searchAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("SearchToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return searchAction;
    }

    /**
     * Return the ViewMenu property value.
     * @return JMenu
     */
    private JMenu getViewMenu() {
        if (viewMenu == null) {
            try {
                viewMenu = new JMenu();
                viewMenu.setName("ViewMenu");
                viewMenu.setText(getResourceString("ViewMenu"));
                viewMenu.setMargin(new Insets(2, 0, 2, 0));
                viewMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_VIEW_MENU);
                
                JCheckBoxMenuItem toolbarItem = makeNewCheckBoxMenuItem(getViewToolbarAction());
                toolbarItem.setSelected(getToolBarPane().isVisible());
                viewMenu.add(toolbarItem);
                
                JCheckBoxMenuItem statusbarItem = makeNewCheckBoxMenuItem(getViewStatusbarAction());
                statusbarItem.setSelected(getStatusBarPane().isVisible());
                viewMenu.add(statusbarItem);

                JCheckBoxMenuItem overviewItem = makeNewCheckBoxMenuItem(getViewOverviewAction());
                overviewItem.setSelected(getBrowserOverviewSplit().getBottomComponent() != null);
                viewMenu.add(overviewItem);
                
                JCheckBoxMenuItem explorerItem = makeNewCheckBoxMenuItem(getViewExplorerAction());
                explorerItem.setSelected(getTableTopExplorer().isEnabled());
                viewMenu.add(explorerItem);

                JCheckBoxMenuItem argumentExplorerItem = makeNewCheckBoxMenuItem(getViewArgumentExplorerAction());
                argumentExplorerItem.setSelected(getArgumentExplorer().isEnabled());
                viewMenu.add(argumentExplorerItem);

                JCheckBoxMenuItem dockingItem = makeNewCheckBoxMenuItem(getTargetDockingAction());
                dockingItem.setSelected(false);
                viewMenu.add(dockingItem);

                viewMenu.addSeparator();
                JCheckBoxMenuItem debugOutputMenuItem = makeNewCheckBoxMenuItem(getDebugOutputAction());
                debugOutputMenuItem.setSelected(((Boolean)getDebugOutputAction().getValue("InDebugOutputMode")).booleanValue());
                viewMenu.add(debugOutputMenuItem);
                viewMenu.add(getViewPropertiesBrowserMenuItem());                
                   
                viewMenu.addSeparator();
                viewMenu.add(makeNewMenuItem(getArrangeGraphAction()));
                viewMenu.add(makeNewMenuItem(getFitTableTopAction()));

                viewMenu.addSeparator();
                viewMenu.add(makeNewMenuItem(getPreferencesAction()));

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return viewMenu;
    }
    
    /**
     * @return the menuitem for viewing the properties browser (aka CAL Navigator).
     */
    private JMenuItem getViewPropertiesBrowserMenuItem() {

        Action viewAction = new AbstractAction(getResourceString("ViewPropertiesBrowser")) {
            
            private static final long serialVersionUID = 5999355987664811572L;

            public void actionPerformed(ActionEvent e) {
                navigatorOwner.displayNavigator(true);
            }
        };
        
        viewAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ViewPropertiesBrowserToolTip"));
        
        return makeNewMenuItem(viewAction);
    }

    /**
     * Return the action that handles showing/hiding the toolbar.
     * @return Action
     */
    private Action getViewToolbarAction() {
        
        if (viewToolbarAction == null) {
            try {
                viewToolbarAction = new AbstractAction(getResourceString("ViewToolbar")) {
                                                            
                    private static final long serialVersionUID = 2265840065539915678L;

                    public void actionPerformed(ActionEvent evt) {
                        viewToolBar();
                    }
                };
                
                viewToolbarAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_VIEW_TOOLBAR));
                viewToolbarAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ViewToolbarToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return viewToolbarAction;
    }

    /**
     * Return the action that handles showing/hiding the Statusbar
     * @return Action
     */
    private Action getViewStatusbarAction() {
        
        if (viewStatusbarAction == null) {
            try {
                viewStatusbarAction = new AbstractAction(getResourceString("ViewStatusbar")) {
                                                            
                    private static final long serialVersionUID = 2212859268617591676L;

                    public void actionPerformed(ActionEvent evt) {
                        viewStatusBar();
                    }
                };
                
                viewStatusbarAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_VIEW_STATUSBAR));
                viewStatusbarAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ViewStatusbarToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return viewStatusbarAction;     
    }

    /**
     * Return the action that handles showing/hiding the Overview view.
     * @return Action 
     */
    private Action getViewOverviewAction() {
        
        if (viewOverviewAction == null) {
            try {
                viewOverviewAction = new AbstractAction(getResourceString("ViewOverview")) {
                    private static final long serialVersionUID = -4767355716120627259L;

                    public void actionPerformed(ActionEvent evt) {
                        toggleOverview();
                    }
                };
                
                viewOverviewAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_VIEW_OVERVIEW));
                viewOverviewAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ViewOverviewToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return viewOverviewAction;
    }
    
    /**
     * Toggle whether the overview is visible.
     */
    private void toggleOverview() {
        
        JSplitPane browserOverviewSplit = getBrowserOverviewSplit();
        boolean show = browserOverviewSplit.getBottomComponent() == null;
        if (show) {
            browserOverviewSplit.setBottomComponent(getOverviewPanel());
            browserOverviewSplit.setDividerLocation(.5);
        } else {
            browserOverviewSplit.setBottomComponent(null);
            browserOverviewSplit.setDividerLocation(.8);
        }
        
        browserOverviewSplit.validate();
    }
    
    /**
     * Return the action that handles showing/hiding the Explorer view.
     * @return Action 
     */
    private Action getViewExplorerAction() {
        
        if (viewExplorerAction == null) {
            try {
                viewExplorerAction = new AbstractAction(getResourceString("ViewExplorer")) {
                                                            
                    private static final long serialVersionUID = -5558386400807002054L;

                    public void actionPerformed(ActionEvent evt) {
                        toggleExplorer();
                    }
                };
                
                viewExplorerAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ViewExplorerToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return viewExplorerAction;
    }

    /**
     * Return the action that handles showing/hiding the Argument Explorer view.
     * @return Action 
     */
    private Action getViewArgumentExplorerAction() {
        
        if (viewArgumentExplorerAction == null) {
            try {
                viewArgumentExplorerAction = new AbstractAction(getResourceString("ViewArgumentExplorer")) {
                                                            
                    private static final long serialVersionUID = 5447972693154054572L;

                    public void actionPerformed(ActionEvent evt) {
                        toggleArgumentExplorer();
                    }
                };
                
                viewArgumentExplorerAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ViewArgumentExplorerToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return viewArgumentExplorerAction;
    }
    
    /**
     * Toggle whether the arguments explorer is visible.
     */
    private void toggleArgumentExplorer() {
        if (getArgumentExplorer().isEnabled()) {
            getArgumentExplorer().setEnabled(false);
            setArgumentsTabVisible(false);
            
        } else {
            getArgumentExplorer().setEnabled(true);
            setArgumentsTabVisible(true);
        }
        
        checkBrowserOverviewDivider();
    }
    
    /**
     * Toggle whether the explorer is visible.
     */
    private void toggleExplorer() {
        if (getTableTopExplorer().isEnabled()) {
            getTableTopExplorer().setEnabled(false);
            setExplorerTabVisible(false);
            
        } else {
            getTableTopExplorer().setEnabled(true);
            setExplorerTabVisible(true);
        }
        checkBrowserOverviewDivider();
    }

    /**
     * Check that the divider between the browser and the overview panel is properly positioned.
     */
    private void checkBrowserOverviewDivider() {
        JSplitPane browserExplorerSplit = getExplorerBrowserSplit();
        int dividerSize = browserExplorerSplit.getDividerSize();
        if (dividerSize == 0) {
            // The divider is not showing. Show the divider if there are tabs.
            if (getExplorerArgumentsPane().getTabCount() > 0) {
                browserExplorerSplit.setDividerSize(3);
                browserExplorerSplit.setDividerLocation(0.5);
            }
        } else {
            // The divider is showing.  Hide the divider if there are no tabs.
            if (getExplorerArgumentsPane().getTabCount() == 0) {
                browserExplorerSplit.setDividerSize(0);
                browserExplorerSplit.setDividerLocation(0.0);
            }
        }
        getBrowserOverviewSplit().validate();
    }

    /**
     * Returns the action responsible for docking/undocking the target.
     * @return Action
     */
    private Action getTargetDockingAction() {
        
        if (targetDockingAction == null) {
            try {
                targetDockingAction = new AbstractAction (getResourceString("TargetDocking")) {
                                                            
                    private static final long serialVersionUID = 6373002342851298749L;

                    public void actionPerformed(ActionEvent evt) {
                        throw new UnsupportedOperationException("this feature is not yet implemented");
                    }
                };
                
                targetDockingAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_TARGET_DOCKING));
                targetDockingAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("TargetDockingToolTip"));
                
                // Disable this for now
                targetDockingAction.setEnabled(false);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return targetDockingAction;
    }

    /**
     * Return the action that handles the Arrange Graph functionality.
     * @return Action
     */
    private Action getArrangeGraphAction() {
        
        if (arrangeGraphAction == null) {
            try {
                arrangeGraphAction = new AbstractAction (getResourceString("ArrangeGraph")) {
                                                            
                    private static final long serialVersionUID = -8694933314486819543L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        TableTop tTop = getTableTop();
                        tTop.doTidyTableTopAction();
                        getTableTopPanel().repaint ();
                    }
                };
                
                arrangeGraphAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ARRANGE_GRAPH));
                arrangeGraphAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_ARRANGE_GRAPH);
                arrangeGraphAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ArrangeGraphToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return arrangeGraphAction;  
    }
    
    /**
     * Return the action that handles the Fit TableTop functionality.
     * @return Action
     */
    private Action getFitTableTopAction() {
        // Define it only if it was previously undefined
        if (fitTableTopAction == null) {
            
            fitTableTopAction = new AbstractAction (getResourceString("FitTableTop")) {
                private static final long serialVersionUID = 7646412345327942710L;

                public void actionPerformed(ActionEvent evt) {
                    TableTop tableTop = getTableTop();
                    tableTop.doShrinkTableTopUserAction();
                    // have to refresh afterwards!
                    getTableTopPanel().repaint();
                }
                
            };
            fitTableTopAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_FIT_TABLETOP);
            fitTableTopAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_FIT_TABLETOP));
            fitTableTopAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("FitTableTopToolTip"));
            
        }
        return fitTableTopAction;
    }
            
    /**
     * Return the action that handles that pops up the view preferences dialog
     * @return Action
     */
    private Action getPreferencesAction() {
        // Define it only if it was previously undefined
        if (preferencesAction == null) {
            
            preferencesAction = new AbstractAction (getResourceString("Preferences")) {
                private static final long serialVersionUID = 1931027116634034114L;

                public void actionPerformed(ActionEvent evt) {
                    getPreferencesDialog().setVisible(true);
                }
                
            };
            
            preferencesAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_PREFERENCES);
            preferencesAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_PREFERENCES));
            preferencesAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("Preferences"));
            
        }
        return preferencesAction;
    }    

    /**
     * Return the action that handles the Debug Output functionality.
     * @return Action
     */
    private Action getDebugOutputAction() {
        
        if (debugOutputAction == null) {
            try {
                debugOutputAction = new AbstractAction (getResourceString("DebugOutput")) {
                                                            
                    private static final long serialVersionUID = 587492833164610681L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        // This just toggles the debug output mode
                        boolean oldValue = ((Boolean)getValue("InDebugOutputMode")).booleanValue();
                        putValue("InDebugOutputMode", Boolean.valueOf(!oldValue));
                    }
                };
                
                debugOutputAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DEBUG_OUTPUT));
                debugOutputAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("DebugOutputToolTip"));
                
                // Get the action to remember if we are in debug output mode
                debugOutputAction.putValue("InDebugOutputMode", Boolean.FALSE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return debugOutputAction;   
    }
    
    /**
     * Returns the menu item for adding new reflectors.
     * @return JMenuItem
     */
    private JMenuItem getAddReflectorGemMenuItem() {
        if (addReflectorGemMenuItem == null) {
            addReflectorGemMenuItem = makeNewMenuItem(getAddReflectorGemAction());
            addReflectorGemMenuItem.setEnabled(false);
        }
        
        return addReflectorGemMenuItem;
    }
    
    /** 
     * Return the Insert menu property value.
     * @return JMenu
     */
    private JMenu getInsertMenu() {
        
        if (insertMenu == null) {
            try {
                insertMenu = new JMenu();
                insertMenu.setName("InsertMenu");
                insertMenu.setText(getResourceString("InsertMenu"));
                insertMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_INSERT_MENU);
                
                insertMenu.add(makeNewMenuItem(getAddGemAction()));
                insertMenu.add(makeNewMenuItem(getAddValueGemAction()));
                insertMenu.add(makeNewMenuItem(getAddCodeGemAction()));
                
                insertMenu.addSeparator();
                
                insertMenu.add(makeNewMenuItem(getAddCollectorGemAction()));
                insertMenu.add(getAddReflectorGemMenuItem());
                
                // Create a JMenu using the reflector action and add a menu listener that will
                // update the menu items in the menu just before it is displayed
                JMenu reflectorMenu = makeNewMenu(null);
                reflectorMenu.setAction(getAddReflectorGemDropDownAction());
                reflectorMenu.setToolTipText(null);
                reflectorMenu.setIcon(null);
                reflectorMenu.addMenuListener(new MenuListener() {
                    
                    public void menuSelected(MenuEvent evt) {
                        JMenu menu = (JMenu)evt.getSource();
                        prepareAddReflectorPopup(menu.getPopupMenu());
                    }
                    
                    public void menuDeselected(MenuEvent evt) {}
                    
                    public void menuCanceled(MenuEvent evt) {}
                });
                
                insertMenu.add(reflectorMenu);
                
                insertMenu.addSeparator();
                
                insertMenu.add(makeNewMenuItem(getAddRecordCreationGemAction()));
                insertMenu.add(makeNewMenuItem(getAddRecordFieldSelectionGemAction()));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        } 
        return insertMenu;
    }
    
    /**
     * @return the generate menu with an entry for each installed gem factory
     */
    private JMenu getGenerateMenu() {
        
        if (generateMenu == null) {
            
            generateMenu = new JMenu();
            generateMenu.setText(getResourceString("GenerateMenu"));
            generateMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_GENERATE_MENU);
            
            List<Class<GemGenerator>> factoryClasses = getFactoryClasses();
            
            for (final Class<GemGenerator> factoryClass : factoryClasses) {

                try {                
                    GemGenerator factoryInstance = factoryClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                    generateMenu.add(makeNewMenuItem(getFactoryAction(factoryInstance)));
                    
                } catch (NoSuchMethodException ex) {
                    System.out.println("Warning: factory class does not define default constructor: " + factoryClass);
                    
                } catch (IllegalAccessException ex) {
                    System.out.println("Warning: factory class does not have visible default constructor: " + factoryClass);
                    
                } catch (Exception ex) {
                    System.out.println("Warning: exception instantiating factory class: " + factoryClass + " - " + ex);
                }
            }
            
            if (generateMenu.getMenuComponentCount() > 0) {
                generateMenu.addSeparator();
            }
            
            generateMenu.add(makeNewMenuItem(getGenerateCALDocDocumentationAction()));
        }
        
        return generateMenu;
    }
    
    /**
     * @param factory the factory this action is for
     * @return the action that launches the given gem factory
     */
    private Action getFactoryAction(final GemGenerator factory) {

        Action factoryAction = new AbstractAction(factory.getGeneratorMenuName(), factory.getGeneratorIcon()) {
            
            private static final long serialVersionUID = -1100808467474770879L;

            public void actionPerformed(ActionEvent e) {

                GemGenerator.GeneratedDefinitions definitions = null;
                
                // Launch the factory. Keep the GUI locked while it's running.
                enterGUIState(GUIState.LOCKED_DOWN);
                
                try {
                    definitions = factory.launchGenerator(GemCutter.this,
                                                        getPerspective(),
                                                        getValueRunner(),
                                                        getValueEditorManager(),
                                                        getTypeChecker());
                } finally {
                    enterGUIState(GUIState.EDIT);
                }
                
                if (definitions == null) {
                    return;
                }
                                
                String lastGemName = null;

                Map<String, String> sourceElementMap = definitions.getSourceElementMap();
                if (sourceElementMap != null && !sourceElementMap.isEmpty()) {

                    // Save all gems to the current module.
                    for (final Map.Entry<String, String> mapEntry : sourceElementMap.entrySet()) {
                        String unqualifiedGemName = mapEntry.getKey();
                        String gemSource = mapEntry.getValue();

                        QualifiedName qualifiedGemName = QualifiedName.make(getWorkingModuleName(), unqualifiedGemName);
                        Status saveStatus = getWorkspace().saveEntity(qualifiedGemName, gemSource, null, null);
                        lastGemName = unqualifiedGemName;

                        if (saveStatus.getSeverity() == Status.Severity.ERROR) {
                            String errTitle = getResourceString("CannotSaveDialogTitle");
                            String errMessage = getResourceString("ErrorSavingGemDefinition");

                            DetailsDialog dialog = new DetailsDialog(GemCutter.this, errTitle, errMessage, 
                                    saveStatus.getDebugMessage(), DetailsDialog.MessageType.ERROR);

                            dialog.doModal();
    
                            // Don't bother trying to save the other gems.
                            break;
                        }
                    }
                } 

                Status generationStatus = new Status("Generation status.");
                
                SourceModel.ModuleDefn moduleDefn = definitions.getModuleDefn();
                if (moduleDefn != null) {
                    // There was a generated module defn.
                    
                    ModuleName generatedModuleName = SourceModel.Name.Module.toModuleName(moduleDefn.getModuleName());

                    // Can't overwrite the prelude.
                    if (generatedModuleName.equals(CAL_Prelude.MODULE_NAME)) {
                        String errTitle = getResourceString("CannotSaveDialogTitle");
                        String errMessage = getResourceString("CannotClobberPrelude");
                        JOptionPane.showMessageDialog(GemCutter.this, errMessage, errTitle, JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Check whether the module name exists, and if so confirm that the user wished to clobber.
                    // This goes to the workspace source manager -- so it's a bit hacky.
                    // We can't just ask the workspace if it has the module though, since it might be the case where the workspace isn't
                    //   using a module in the nullary case.
                    if (getWorkspace().getSourceManager(generatedModuleName).getResourceStore().hasFeature(
                            new ResourceName(CALFeatureName.getModuleFeatureName(generatedModuleName)))) {
                        
                        // TODOEL: Ideally, if the user does not want to clobber the existing module, we should 
                        //   go back to the dialog with the existing inputs.
                        
                        String warningTitle = getResourceString("WarningDialogTitle");
                        String errMessage = getResourceString("ModuleExistsWarning");
                        int continueChoice = JOptionPane.showConfirmDialog(GemCutter.this, errMessage, warningTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (continueChoice != JOptionPane.OK_OPTION) {
                            return;
                        }
                    }
                    
                    
                    // *** HACK ***
                    // *** Fix me when we can handle modules without vault info.
                    
                    // Ok, now we have the generated module defn.  Where do we put it?
                    // It should go into the workspace, but the workspace can't handle modules without any vault info.
                    // So for now we create a temp file first, and add from there.

                    // We could use NonExistentVault instead, but that requires some work to implement StoredVaultElement.Module.
                    
                    String tmpDir = System.getProperty("java.io.tmpdir");
                    String fileName = generatedModuleName + ".cal";
                    
                    File tempFile = new File(tmpDir, fileName);
                    String sourceText = moduleDefn.toSourceText();
                    Writer writer = null;
                    try {
                        writer = new BufferedWriter(new FileWriter(tempFile));
                        writer.write(sourceText);
                    } catch (IOException ioe) {
                        generationStatus.add(new Status(Status.Severity.ERROR, "Error writing file: " + tempFile, ioe));

                    } finally {
                        if (writer != null) {
                            try {
                                writer.flush();
                                writer.close();
                            } catch (IOException ioe) {
                                // Not much we can do about this.
                            }
                        }
                    }
                    
                    // Add the module to the workspace.
                    // Note: this can result in errors in cases where the module is erroneous.
                    //  For instance, if generating a foreign import module, this can result in errors if the module refers to
                    //    foreign classes which are not on the classpath.
                    boolean addModuleAttemptSuccessful = false;
                    if (generationStatus.getSeverity().compareTo(Status.Severity.ERROR) < 0) {

                        SimpleCALFileVault simpleCALFileVault = SimpleCALFileVault.getSimpleCALFileVault(tempFile);
                        
                        // This call also calls recompileWorkspace(true).
                        if (simpleCALFileVault != null) {
                            handleAddModuleAttempt(simpleCALFileVault, generatedModuleName, -1, false);
                            addModuleAttemptSuccessful = true;
                        } else {
                            String details = getResourceString("CannotCreateSimpleCALFileVault");
                            showProblemsGeneratingModuleDialog(DetailsDialog.MessageType.ERROR, details);
                        }
                    }
                    
                    // Delete the tempFile.  Don't worry too much about if this fails..
                    tempFile.delete();
                    
                    // Select the module which was compiled, if any.
                    if (addModuleAttemptSuccessful && generationStatus.getSeverity().compareTo(Status.Severity.ERROR) < 0) {
                        getGemBrowser().getBrowserTree().selectDrawerNode(generatedModuleName);
                        
                        // Also display a message.
                        String statusMessage = GemCutterMessages.getString("SM_ModuleGenerated", generatedModuleName);
                        statusMessageManager.displayMessage(this, statusMessage, StatusMessageDisplayer.MessageType.TRANSIENT, true);

                    }
                    
                    if (generationStatus.getSeverity().compareTo(Status.Severity.WARNING) >= 0) {
                        String details = generationStatus.getDebugMessage();
                        DetailsDialog.MessageType messageType = generationStatus.getSeverity() == Status.Severity.WARNING ? 
                                DetailsDialog.MessageType.WARNING : DetailsDialog.MessageType.ERROR;
                        
                        showProblemsGeneratingModuleDialog(messageType, details);
                    }
                    
                } else {
                    // No generated module defn.
                
                    // Recompile the workspace.
                    recompileWorkspace(true);
                    
                    if (lastGemName != null) {
                        
                        // Select the last gem that was saved in the GemBrowser.
                        GemEntity newEntity = perspective.resolveGemEntity(lastGemName);
                        
                        if (newEntity != null) {
                            
                            // The entity may be null if the recompile failed 
                            // or the generated source contained some sort of error.
                            getGemBrowser().getBrowserTree().selectGemNode(newEntity);
                        }                    
                    }
                }
            }

            private void showProblemsGeneratingModuleDialog(DetailsDialog.MessageType messageType, String details) {
                String title = getResourceString("WindowTitle");
                String message = getResourceString("ProblemsGeneratingModule");
                
                DetailsDialog dialog = new DetailsDialog(GemCutter.this, title, message, details, messageType);
                dialog.doModal();
            }
        };
        
        return factoryAction;
    }
    /**
     * Reads all installed factory class names from the factory class file and returns
     * an array of Class objects for each class that can be found.
     * @return an array of factory class objects
     */
    private static List<Class<GemGenerator>> getFactoryClasses() {
        
        BufferedReader reader = null;
        
        try {
            
            List<Class<GemGenerator>> factoryClasses = new ArrayList<Class<GemGenerator>>();
            
            InputStream inputStream = GemCutter.class.getResourceAsStream("/gemGenerators.ini");
            reader = new BufferedReader(TextEncodingUtilities.makeUTF8Reader(inputStream));
            
            String className = reader.readLine();
            
            while (className != null) {
                
                try {
                    Class<GemGenerator> classForName = UnsafeCast.<Class<GemGenerator>>unsafeCast(Class.forName(className));
                    factoryClasses.add(classForName);
                
                } catch (ClassNotFoundException ex) {
                    System.out.println("Warning: factory class not found: " + className);
                }
                
                className = reader.readLine();
            }
            
            return factoryClasses;
        
        } catch (Exception ex) {
            System.out.println("Warning: exception reading factory class listing file: " + ex.getLocalizedMessage());
        
        } finally {
            
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
        
        return new ArrayList<Class<GemGenerator>>(0);
    }

    /**
     * Returns the action for adding a CodeGem.
     * @return Action
     */
    private Action getAddCodeGemAction() {
        
        if (addCodeGemAction == null) {
            try {
                addCodeGemAction = new AbstractAction (getResourceString("AddCodeGem"), 
                                                new ImageIcon(getClass().getResource("/Resources/code.gif"))) {
                                                            
                    private static final long serialVersionUID = -6816895231478591066L;

                    public void actionPerformed(ActionEvent evt) {

                        // Add a Green (Code) Gem
                        DisplayedGem dGem = tableTop.createDisplayedCodeGem(new Point(10,10));
                        
                        // Set this as the Gem we're in the process of adding, and change GUI state
                        setAddingDisplayedGem(dGem);
                        enterGUIState(GUIState.ADD_GEM);
                    }
                };
                
                addCodeGemAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_CODE_GEM));
                addCodeGemAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddCodeGemToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return addCodeGemAction;
    }
    
    /**
     * Returns the action for adding an Record Field Selection Gem.
     * @return Action
     */
    private Action getAddRecordFieldSelectionGemAction() {
        
        if (addRecordSelectionGemAction == null) {
            try {
                addRecordSelectionGemAction = new AbstractAction (getResourceString("AddRecordFieldSelectionGem"),
                                                    new ImageIcon(getClass().getResource("/Resources/recordFieldSelectionGem.gif"))) {
                    
                    private static final long serialVersionUID = 8305024624011682911L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        // create and display Record Field Selection Gem
                        DisplayedGem dGem = tableTop.createDisplayedRecordFieldSelectionGem(new Point(10,10));
                        
                        // Set this as the Gem we're in the process of adding, and change GUI state
                        setAddingDisplayedGem(dGem);
                        enterGUIState(GUIState.ADD_GEM);
                    }
                };
                
                addRecordSelectionGemAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_RECORD_FIELD_SELECTION_GEM));
                addRecordSelectionGemAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddRecordFieldSelectionGemToolTip"));

            } catch (Throwable exc) {
                handleException(exc);
            }
        }
        
        return addRecordSelectionGemAction;
    }
    
    
    /**
     * Returns the action for adding a RecordCreationGem 
     * @return the action
     */   
    private Action getAddRecordCreationGemAction() {
        
        if (addRecordCreationGemAction  == null) {
            try {
                addRecordCreationGemAction = new AbstractAction (getResourceString("AddRecordCreationGem"),
                                                    new ImageIcon(getClass().getResource("/Resources/recordCreationGem.gif"))) {
                    

                    private static final long serialVersionUID = 6010375395496990909L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        // create and display Record Field Selection Gem
                        DisplayedGem dGem = tableTop.createDisplayedRecordCreationGem(new Point(10,10));
                        
                        // Set this as the Gem we're in the process of adding, and change GUI state
                        setAddingDisplayedGem(dGem);
                        enterGUIState(GUIState.ADD_GEM);
                    }
                };
                
                addRecordCreationGemAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_RECORD_CREATION_GEM));
                addRecordCreationGemAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddRecordCreationGemToolTip"));

            } catch (Throwable exc) {
                handleException(exc);
            }
        }
        
        return addRecordCreationGemAction;
    }

    
    /**
     * Returns the action for adding a ValueGem.
     * @return Action
     */
    private Action getAddValueGemAction() {
        
        if (addValueGemAction == null) {
            try {
                addValueGemAction = new AbstractAction (getResourceString("AddValueGem"), 
                                                new ImageIcon(getClass().getResource("/Resources/constant.gif"))) {
                                                            
                    private static final long serialVersionUID = 6010375395496990909L;

                    public void actionPerformed(ActionEvent evt) {

                        // Add a Blue (Value) Gem
                        DisplayedGem dGem = tableTop.createDisplayedValueGem(new Point(10,10));
                        
                        // Set this as the Gem we're in the process of adding, and change GUI state
                        setAddingDisplayedGem(dGem);
                        enterGUIState(GUIState.ADD_GEM);
                    }
                };
                
                addValueGemAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_VALUE_GEM));
                addValueGemAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddValueGemToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return addValueGemAction;
    }

    /**
     * Return the action for adding a CollectorGem
     * @return Action
     */
    private Action getAddCollectorGemAction() {
        
        if (addCollectorGemAction == null) {
            try {
                addCollectorGemAction = new AbstractAction (getResourceString("AddCollectorGem"), 
                                                new ImageIcon(getClass().getResource("/Resources/collector.gif"))) {
                                                            
                    private static final long serialVersionUID = 6732017043606208724L;

                    public void actionPerformed(ActionEvent evt) {

                        // Add a Collector Gem
                        DisplayedGem dGem = getTableTop().createDisplayedCollectorGem(new Point(10,10), tableTop.getTargetCollector());
                        
                        // Set this as the Gem we're in the process of adding, and change GUI state
                        setAddingDisplayedGem(dGem);
                        enterGUIState(GUIState.ADD_GEM);
                    }
                };
                
                addCollectorGemAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_COLLECTOR_GEM));
                addCollectorGemAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddCollectorGemToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return addCollectorGemAction;       
    }
    
    /**
     * Return the action for adding a new gem.
     * @return Action
     */
    private Action getAddGemAction() {
        
        if (addGemAction == null) {
        
            addGemAction = new AbstractAction (getResourceString("AddGem"), 
                                                      new ImageIcon(getClass().getResource("/Resources/addNewGem.gif"))) {
                                                                
                private static final long serialVersionUID = 1517812122164096209L;

                public void actionPerformed(ActionEvent evt) {
    
                    // If the table top explorer has focus and can display intellicut, then
                    // display intellicut there. If that's not the case check if the table top
                    // panel has focus and let it display intellicut. Otherwise it means the
                    // user must have clicked on the button or menu item, so go into add gem mode.
                    
                    boolean displayed = getTableTopExplorerAdapter().maybeDisplayIntellicut();
                    
                    if (!displayed && getTableTopPanel().isFocusOwner() && !(evt.getSource() instanceof JButton)) {
                        getTableTopPanel().displayIntellicut();
                    
                    } else if (!displayed) {
                        setAddingDisplayedGem(null);
                        enterGUIState(GUIState.ADD_GEM);
                    }
                }
            };
                    
            addGemAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_INTELLICUT));
            addGemAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_INTELLICUT);
            addGemAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddGemToolTip"));
        }

        return addGemAction;       
    }    

    /**
     * Return the add reflector button.
     * Access to this button is needed to change its text according to context.
     * @return JButton the add reflector button
     */
    private JButton getAddReflectorGemButton() {
        if (addReflectorGemButton == null) {
            try {
                addReflectorGemButton = makeNewButton(getAddReflectorGemAction());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return addReflectorGemButton;
    }    

    /**
     * Return the add reflector drop down button.
     * Access to this button is important because its position is needed to display
     * the associated popup menu in the correct location.
     * @return JButton the add reflector drop down button
     */
    private JButton getAddReflectorGemDropDownButton() {
        if (addReflectorGemDropDownButton == null) {
            try {
                addReflectorGemDropDownButton = makeNewButton(getAddReflectorGemDropDownAction());
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return addReflectorGemDropDownButton;
    }
    
    /**
     * Returns the action that handles adding an ReflectorGem
     * @return Action
     */
    private Action getAddReflectorGemAction() {
        
        if (addReflectorGemAction == null) {
            try {
                addReflectorGemAction = new AbstractAction (getResourceString("AddReflectorGemMenu"), 
                        new ImageIcon(getClass().getResource("/Resources/reflector.gif"))) {
                    
                    private static final long serialVersionUID = -7572765024493266843L;

                    public void actionPerformed(ActionEvent evt) {
                        if (currentCollectorForAddingReflector != null) {
                            // Add an Emitter Gem
                            DisplayedGem eGem = tableTop.createDisplayedReflectorGem(new Point(10,10), currentCollectorForAddingReflector);
                            
                            // Set this as the Gem we're in the process of adding, and change GUI state
                            setAddingDisplayedGem(eGem);
                            enterGUIState(GUIState.ADD_GEM);
                        } else {
                            addReflectorGemByDropDown();
                        }
                    }
                };

                addReflectorGemAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_REFLECTOR_GEM));
                addReflectorGemAction.putValue(ACTION_BUTTON_IS_DROP_PARENT_KEY, Boolean.TRUE); 
                
                currentCollectorListener.updateReflectorWidgets();
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return addReflectorGemAction; 
    }

    /**
     * Returns the action that handles adding an ReflectorGem
     * @return Action
     */
    private Action getAddReflectorGemDropDownAction() {
        
        if (addReflectorGemDropDownAction == null) {
            try {
                addReflectorGemDropDownAction = new AbstractAction (getResourceString("AddReflectorGemDropDown"), 
                        new ImageIcon(getClass().getResource("/Resources/dropdownarrow.gif"))) {
                    
                    private static final long serialVersionUID = 3689499397433899552L;

                    public void actionPerformed(ActionEvent evt) {
                        addReflectorGemByDropDown();
                    }
                };
                
                addReflectorGemDropDownAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_REFLECTOR_GEM));
                addReflectorGemDropDownAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddReflectorGemDropDownToolTip"));
                addReflectorGemDropDownAction.putValue(ACTION_BUTTON_IS_DROP_CHILD_KEY, Boolean.TRUE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return addReflectorGemDropDownAction; 
    }

    /**
     * Display a popup with available collectors.  If a collector is selected, 
     * add the corresponding ReflectorGem wherever the user clicks.
     */
    private void addReflectorGemByDropDown() {
        
        // Set up a new popup menu with the appropriate menu items and listeners
        JPopupMenu pop = new JPopupMenu();
        prepareAddReflectorPopup(pop);

        // show the popup directly underneath the button, left-aligned with it
        JButton reflectorButton = getAddReflectorGemDropDownButton();
        Rectangle bounds = reflectorButton.getBounds();
        pop.show(getToolBarPane(), bounds.x, bounds.y + bounds.height);
    }
    
    /**
     * Prepares the popup menu for adding ReflectorGems for display.  Menu items and listeners will be
     * added to the specified menu.
     * CAUTION: any existing menu items in the specified menu will be removed!
     * @param menu JPopupMenu - the menu that is to be prepared for display
     */
    private void prepareAddReflectorPopup(JPopupMenu menu) {
        
        // A listener for selection of menu items
        class ReflectorMenuSelectionListener implements ActionListener {
            CollectorGem collector;
            ReflectorMenuSelectionListener(CollectorGem collector){
                this.collector = collector;
            }
            public void actionPerformed(ActionEvent evt){
                // Add an Emitter Gem
                DisplayedGem eGem = tableTop.createDisplayedReflectorGem(new Point(10,10), collector);
                
                // Set this as the Gem we're in the process of adding, and change GUI state
                setAddingDisplayedGem(eGem);
                enterGUIState(GUIState.ADD_GEM);
                
                // Save this so the add reflector button can add more reflectors
                currentCollectorListener.setReflectorCollector(collector);
            }
        }
        
        // Create listeners to listen for selection of individual menu items.
        List<Gem> collectorList = new ArrayList<Gem>(getTableTop().getGemGraph().getCollectors());
        List<JComponent> menuItems = getGemMenuItems(collectorList);
        
        // Clear the menu of any existing menu items and then add the new ones
        menu.removeAll();
        int numItems = menuItems.size();
        for (int i = 0; i < numItems; i++) {
            
            // Watch out for JSeparators and disabled menu items!
            JComponent item = menuItems.get(i);

            if (item instanceof JMenuItem && ((JMenuItem)item).isEnabled()) {
                CollectorGem cGem = (CollectorGem)collectorList.get(i);
                ((JMenuItem)item).addActionListener(new ReflectorMenuSelectionListener(cGem));
            }
            
            menu.add(item);
        }
    }
    
    /**
     * Return the WorkspaceMenu property value.
     * @return JMenu
     */
    private JMenu getWorkspaceMenu() {

        if (workspaceMenu == null) {
            workspaceMenu = new JMenu();
            workspaceMenu.setName("WorkspaceMenu");
            workspaceMenu.setText(getResourceString("WorkspaceMenu"));
            workspaceMenu.setMargin(new Insets(2, 0, 2, 0));
            workspaceMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_WORKSPACE_MENU);
            
            workspaceMenu.add(getAddModuleSubMenu());
            workspaceMenu.add(getExportModuleSubMenu());
            workspaceMenu.add(makeNewMenuItem(getRemoveModuleAction()));
            workspaceMenu.add(makeNewMenuItem(getWorkspaceVaultStatusAction()));
            workspaceMenu.add(getSyncSubMenu());
            workspaceMenu.addSeparator();
            if (enterpriseSupport.isEnterpriseSupported()) {
                workspaceMenu.add(makeNewMenuItem(getDeployWorkspaceToEnterpriseAction()));
            }
            workspaceMenu.add(makeNewMenuItem(getExportWorkspaceToCarsAction()));
            workspaceMenu.addSeparator();
            workspaceMenu.add(getRenameSubMenu());
            workspaceMenu.addSeparator();
            workspaceMenu.add(makeNewMenuItem(getCompileModifiedAction()));
            workspaceMenu.add(makeNewMenuItem(getRecompileAction()));
            workspaceMenu.addSeparator();
            workspaceMenu.add(makeNewMenuItem(getCreateMinimalWorkspaceAction()));
            workspaceMenu.addSeparator();
            workspaceMenu.add(makeNewMenuItem(getWorkspaceInfoAction()));
        }

        return workspaceMenu;
    }
    
    /**
     * @return the submenu item to add a module to the workspace.
     */
    private JMenuItem getAddModuleSubMenu() {
        if (addModuleSubMenu == null) {
            addModuleSubMenu = makeNewMenu(getResourceString("AddModule"));
            addModuleSubMenu.add(makeNewMenuItem(getAddModuleFromStandardVaultAction()));
            addModuleSubMenu.addSeparator();
            addModuleSubMenu.add(makeNewMenuItem(getAddModuleFromSourceFileAction()));
            if (enterpriseSupport.isEnterpriseSupported()) {
                addModuleSubMenu.add(makeNewMenuItem(getAddEnterpriseModuleAction()));
            }
            
            addModuleSubMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_ADD_MODULE);
            addModuleSubMenu.setToolTipText(getResourceString("AddModuleToolTip"));
        }
        
        return addModuleSubMenu;
    }
    
    /**
     * Return the action for the menu item to add a module from the Standard Vault to the current workspace.
     * @return Action
     */
    private Action getAddModuleFromStandardVaultAction() {

        if (addModuleFromStdVaultAction == null) {
            addModuleFromStdVaultAction = new AbstractAction(getResourceString("AddModuleFromStdVault")) {
                                                        
                private static final long serialVersionUID = 7638468785140208128L;

                public void actionPerformed(ActionEvent evt) {
                    handleAddModuleFromStandardVaultAction();
                }
            };
            
            addModuleFromStdVaultAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_STD_VAULT_MODULE));
            addModuleFromStdVaultAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddModuleFromStdVaultToolTip"));
        }

        return addModuleFromStdVaultAction;
    }

    /**
     * Return the action for the menu item to add a new module to the current workspace using a simple .cal source file.
     * @return Action
     */
    private Action getAddModuleFromSourceFileAction() {

        if (addModuleFromSourceFileAction == null) {
            addModuleFromSourceFileAction = new AbstractAction(getResourceString("AddModuleFromSource")) {
                                                        
                private static final long serialVersionUID = -8550053556955691191L;

                public void actionPerformed(ActionEvent evt) {
                    handleAddModuleFromSourceFileAction();
                }
            };
            
            addModuleFromSourceFileAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_LOCAL_FILE_MODULE));
            addModuleFromSourceFileAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddModuleFromSourceToolTip"));
        }
        return addModuleFromSourceFileAction;
    }

    /**
     * Return the action for the menu item to add a new module to the current workspace from Business Objects Enterprise.
     * @return Action
     */
    private Action getAddEnterpriseModuleAction() {

        if (addEnterpriseModuleAction == null) {
            addEnterpriseModuleAction = new AbstractAction(getResourceString("AddModuleFromEnterprise")) {
                                                        
                private static final long serialVersionUID = 6786374520668468031L;

                public void actionPerformed(ActionEvent evt) {
                    handleAddEnterpriseModuleAction();
                }
            };
            
            addEnterpriseModuleAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ADD_ENTERPRISE_MODULE));
            addEnterpriseModuleAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AddModuleFromEnterpriseToolTip"));
        }
        return addEnterpriseModuleAction;
    }

    /**
     * Handle the situation where the user has indicated that they would like to add a module from Enterprise to the current workspace.
     */
    private void handleAddEnterpriseModuleAction() {
        // Attempt to ensure that the user is connected to Enterprise.
        if (!ensureEnterpriseConnectionIsReady()) {
            return;
        }
        
        Vault vault = enterpriseSupport.getEnterpriseVault();
        if (vault == null) {
            return;
        }
        
        ModuleName[] moduleNamesToExclude = getWorkspace().getModuleNames();
        
        // Display the dialog from which the user can select the module to add.
        String dialogTitle = getResourceString("VMCD_AddModuleTitle");
        String dialogMessage = GemCutter.getResourceString("VMCD_ChooseModuleForImportMessage");
        VaultResourceChooserDialog moduleChooserDialog = 
                VaultResourceChooserDialog.getModuleChooserDialog(GemCutter.this, dialogTitle, dialogMessage, vault, moduleNamesToExclude);
        
        if (moduleChooserDialog == null) {
            showActionFailureDialog(getResourceString("ErrorDialogTitle"), getResourceString("CannotObtainResourcesFromVault"), null);
            return;
        }
        
        boolean accepted = moduleChooserDialog.doModal();

        // Get the selected module version.
        VaultResourceChooserDialog.SelectedResourceVersion selectedModuleVersion = moduleChooserDialog.getSelectedResourceVersion();
        if (!accepted || selectedModuleVersion == null) {
            return;
        }
        
        // Try to add the module.
        handleAddModuleAttempt(vault, ModuleName.make(selectedModuleVersion.getResourceName()), selectedModuleVersion.getRevisionNumber(), true);
    }

    /**
     * Attempt to instantiate a CEConnectionManager if one does not already exist.
     * If the instantiation fails, inform the user.
     * One or more dialogs may be displayed.
     * 
     * TODOEL: This allows only one connection to ce to be present.
     * 
     * @return whether the connection to Enterprise is ready.
     */
    private boolean ensureEnterpriseConnectionIsReady() {
        
        // Attempt to ensure that the user is connected to Enterprise.
        try {
            enterpriseSupport.ensureConnected(GemCutter.this);
            
        } catch (Exception e) {
            // Inform the user.
            showActionFailureDialog(getResourceString("ErrorDialogTitle"), getResourceString("CannotInitConnectionToCEMessage"), e);
            return false;
        }
        
        return enterpriseSupport != null && enterpriseSupport.isConnected();
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to add a module from the Standard Vault to the current workspace.
     */
    private void handleAddModuleFromStandardVaultAction() {
        // Get the available modules not already in the workspace.   (The get avail modules operation shouldn't fail on the standard vault).
        Set<ModuleName> availableModulesSet = new HashSet<ModuleName>(Arrays.asList(StandardVault.getInstance().getAvailableModules(new Status("Add Status."))));
        availableModulesSet.removeAll(Arrays.asList(getWorkspace().getModuleNames()));
        
        // Convert to a String array.
        ModuleName[] availableModulesArray = availableModulesSet.toArray(new ModuleName[availableModulesSet.size()]);
        Arrays.sort(availableModulesArray);
        
        // Ask the user which module to remove.
        String title = getResourceString("AddModuleFromStdVaultDialogTitle");
        String message = getResourceString("AddModuleFromStdVaultDialogMessage");        
        ModuleName selectedModuleName = (ModuleName)JOptionPane.showInputDialog(GemCutter.this, message, title, JOptionPane.PLAIN_MESSAGE, 
                                                                        null, availableModulesArray, availableModulesArray[0]);

        if (selectedModuleName == null) {
            return;
        }
        
        // Try to add the module.
        handleAddModuleAttempt(StandardVault.getInstance(), selectedModuleName, -1, true);
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to add a module to the current workspace
     *   using a simple .cal source file.
     */
    private void handleAddModuleFromSourceFileAction() {

        FileFilter fileFilter = new ExtensionFileFilter(CALSourcePathMapper.INSTANCE.getFileExtension(), getResourceString("CALFileDescription"));

        String initialDir = getPreferences().get(ADD_MODULE_DIRECTORY_PREF_KEY, ADD_MODULE_DIRECTORY_DEFAULT);
        
        JFileChooser fileChooser = new JFileChooser(initialDir);
        fileChooser.setFileFilter(fileFilter);
        
        int chooserOption = fileChooser.showOpenDialog(this);
        if (chooserOption != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        File selectedFile = fileChooser.getSelectedFile();
        
        // A path to a file, which for now we assume contains only the .cal source definition.
        SimpleCALFileVault fileVault = SimpleCALFileVault.getSimpleCALFileVault(selectedFile);
        if (fileVault == null) {            
            String title2 = getResourceString("AddModuleFailedTitle");
            String message2 = GemCutterMessages.getString("AddModuleFromSourceFailedMessage", selectedFile.getName());            
            JOptionPane.showMessageDialog(GemCutter.this, message2, title2, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Try to add the module.
        handleAddModuleAttempt(fileVault, fileVault.getModuleName(), 0, true);
    }

    /**
     * Handle the addition of a module to the workspace.
     * This method may display a modal error dialog if errors are encountered.
     * 
     * TODOEL: reevaluate all code gems (and later, value gems).
     * 
     * @param vault
     * @param moduleName
     * @param revisionNumber
     * @param checkExisting if true, this operation will fail (with appropriate message) if the module already exists in the workspace.
     * If false, the added module will replace any existing module resources in the workspace.
     */
    private void handleAddModuleAttempt(Vault vault, ModuleName moduleName, int revisionNumber, boolean checkExisting) {
        Status addStatus = new Status("Add status");
        
        StoredVaultElement.Module moduleToAdd = vault.getStoredModule(moduleName, revisionNumber, addStatus);

        if (moduleToAdd == null) {
                        
            String title = getResourceString("AddModuleFailedTitle");
            String message;
            if (addStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
                message = getResourceString("GetModuleErrorMessage") + addStatus.getDebugMessage();
            } else {
                message = getResourceString("AddModuleNotFoundMessage") + addStatus.getDebugMessage();
            }
            JOptionPane.showMessageDialog(GemCutter.this, message, title, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Status addModuleStatus = new Status(getResourceString("AddModuleStatus"));
        
        if (!getWorkspace().addModule(moduleToAdd, checkExisting, addModuleStatus)) {
            String title = getResourceString("AddModuleFailedTitle");
            
            String message = getResourceString("AddModuleFailedMessage");
            String debugMessage = addModuleStatus.getDebugMessage();
            if (debugMessage != null) {
                message += "\n" + debugMessage;
            }
            
            JOptionPane.showMessageDialog(GemCutter.this, message, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If we are here, the file was successfully added.
        // Note that any gems that were on the tabletop will still exist in the program (so we don't have to handle the case that they don't..)
        
        // mark workspace as changed in Gem Browser
        getGemBrowser().markWorkspaceDirty();        
        
        // recompile the program from the updated workspace
        recompileWorkspace(true);
    }
    
    private JMenuItem getRenameSubMenu() {
        if (renameSubMenu == null) {
            renameSubMenu = makeNewMenu(getResourceString("Rename")); 
            renameSubMenu.add(makeNewMenuItem(getRenameGemAction()));
            renameSubMenu.add(makeNewMenuItem(getRenameTypeAction()));
            renameSubMenu.add(makeNewMenuItem(getRenameClassAction()));
            renameSubMenu.add(makeNewMenuItem(getRenameModuleAction()));
        }
        
        return renameSubMenu;
    }
    
    /**
     * @return the submenu item to export a module from the workspace.
     */
    private JMenuItem getExportModuleSubMenu() {
        if (exportModuleSubMenu == null) {
            exportModuleSubMenu = makeNewMenu(getResourceString("ExportModule"));
            exportModuleSubMenu.add(makeNewMenuItem(getExportModuleToJarAction()));
            if (enterpriseSupport.isEnterpriseSupported()) {
                exportModuleSubMenu.add(makeNewMenuItem(getExportModuleToCEAction()));
            }
            
            exportModuleSubMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_EXPORT_MODULE);
            exportModuleSubMenu.setToolTipText(getResourceString("ExportModuleToolTip"));
        }
        
        return exportModuleSubMenu;
    }
    
    /**
     * Return the action for the menu item to export a module from the current workspace to CE.
     * @return Action
     */
    private Action getExportModuleToCEAction() {
        if (exportModuleToCEAction == null) {
            exportModuleToCEAction = new AbstractAction(getResourceString("ExportModuleToCE")) {
                                                        
                private static final long serialVersionUID = 4888254343776966036L;

                public void actionPerformed(ActionEvent evt) {
                    handleExportModuleToCEAction();
                }
            };
            
            exportModuleToCEAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_EXPORT_MODULE_TO_CE));
            exportModuleToCEAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ExportModuleToCEToolTip"));
        }
        return exportModuleToCEAction;
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to export a module from the current workspace to CE.
     */
    private void handleExportModuleToCEAction() {
        // Attempt to ensure that the user is connected to CE.
        if (!ensureEnterpriseConnectionIsReady()) {
            return;
        }
        
        // Ask the user which module to export.
        ModuleName selectedModuleName = showExportModuleChooserDialog();
        
        if (selectedModuleName == null) {
            return;
        }
        
        MetaModule metaModule = getWorkspaceManager().getWorkspace().getMetaModule(selectedModuleName);
        if (metaModule == null) {
            String title = getResourceString("ExportModuleFailedDialogTitle");
            String message = getResourceString("GetModulesErrorMessage");
            JOptionPane.showMessageDialog(GemCutter.this, message, title, JOptionPane.ERROR_MESSAGE);
        }
        
        Status status = new Status("Put status.");
        int addedRevisionNum = enterpriseSupport.getEnterpriseVault().putStoredModule(selectedModuleName, getWorkspace(), status);
        if (status.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
            // Inform the user.
            showActionFailureDialog(getResourceString("ExportModuleFailedDialogTitle"), 
                                    getResourceString("ExportModuleFailedDialogMessage") + "\n" + status.getDebugMessage(), 
                                    null);
            return;

        } else if (status.getSeverity().compareTo(Status.Severity.WARNING) >= 0) {
            String title = getResourceString("ExportModuleWarningsDialogTitle");
            String message = getResourceString("ExportModuleWarningsDialogMessage") + status.getDebugMessage();
            JOptionPane.showMessageDialog(GemCutter.this, message, title, JOptionPane.ERROR_MESSAGE);
            
        }
        
        // Display a status message.
        String statusMessage = GemCutterMessages.getString("SM_ModuleExported", selectedModuleName, new Integer(addedRevisionNum));
        statusMessageManager.displayMessage(this, statusMessage, StatusMessageDisplayer.MessageType.TRANSIENT, true);

    }
    
    /**
     * Return the action for the menu item to export a module from the current workspace to a .jar file.
     * @return Action
     */
    private Action getExportModuleToJarAction() {
        if (exportModuleToJarAction == null) {
            exportModuleToJarAction = new AbstractAction(getResourceString("ExportModuleToJar")) {
                                                        
                private static final long serialVersionUID = 9186654570603119228L;

                public void actionPerformed(ActionEvent evt) {
                    handleExportModuleToJarAction();
                }
            };
            
            exportModuleToJarAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_EXPORT_MODULE_TO_JAR));
            exportModuleToJarAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ExportModuleToJarToolTip"));
        }
        return exportModuleToJarAction;
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to export a module from the current workspace to a .jar file.
     */
    private void handleExportModuleToJarAction() {
        // Ask the user which module to export.
        ModuleName selectedModuleName = showExportModuleChooserDialog();
        
        if (selectedModuleName == null) {
            return;
        }

        // Create the file chooser at a default initial directory.
        String initialDir = getPreferences().get(EXPORT_MODULE_DIRECTORY_PREF_KEY, ADD_MODULE_DIRECTORY_DEFAULT);
        JFileChooser fileChooser = new JFileChooser(initialDir);
        
        // Create and set the file filter.
        FileFilter fileFilter = new ExtensionFileFilter(JAR_FILE_EXTENSION, getResourceString("JarFiles"));

        fileChooser.setFileFilter(fileFilter);

        // Also accept the accept all filter.
        fileChooser.setAcceptAllFileFilterUsed(true);
        
        // The default file name is the name of the module + ".jar".
        fileChooser.setSelectedFile(new File(initialDir, selectedModuleName + "." + JAR_FILE_EXTENSION));
        
        // Show the dialog.
        int chooserOption = fileChooser.showSaveDialog(this);
        
        // Do nothing if dialog isn't approved.
        if (chooserOption != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        // Get the file which was selected.
        File selectedFile = fileChooser.getSelectedFile();
        
        // Save the directory the user browsed to for next time
        String lastDir = selectedFile.getParentFile().getAbsolutePath();
        getPreferences().put(EXPORT_MODULE_DIRECTORY_PREF_KEY, lastDir);
        
        // Confirm overwrite if the file exists.
        if (selectedFile.exists()) {
            String title = getResourceString("ConfirmOverwriteTitle");
            String message = GemCutterMessages.getString("ConfirmOverwriteMessage", selectedModuleName);            
            int continueChoice = JOptionPane.showConfirmDialog(GemCutter.this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (continueChoice != JOptionPane.OK_OPTION) {
                return;
            }
        }

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(selectedFile);
            ModulePackager.writeModuleToJar(getWorkspace(), selectedModuleName, outputStream);
            outputStream.close();
            
            // Display a status message.
            String statusMessage = GemCutterMessages.getString("SM_ModuleExported", selectedModuleName, new Integer(0));
            statusMessageManager.displayMessage(this, statusMessage, StatusMessageDisplayer.MessageType.TRANSIENT, true);

        } catch (IOException e) {
            // Inform the user.
            showActionFailureDialog(getResourceString("ExportModuleFailedDialogTitle"), getResourceString("ExportModuleFailedDialogMessage"), e);

        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e1) {
                }
            }
        }

    }
    
    /**
     * Display the export module dialog.
     * @return the module selected by the user for export.
     */
    private ModuleName showExportModuleChooserDialog() {
        // Get the modules which are already in the workspace.
        ModuleName[] availableModulesArray = getWorkspace().getModuleNames();
        Arrays.sort(availableModulesArray);
        
        // Ask the user which module to export.
        String title = getResourceString("ExportModuleDialogTitle");
        String message = getResourceString("ExportModuleDialogMessage");        
        ModuleName selectedModuleName = (ModuleName)JOptionPane.showInputDialog(GemCutter.this, message, title, JOptionPane.PLAIN_MESSAGE, 
                                                                        null, availableModulesArray, availableModulesArray[0]);

        return selectedModuleName;
    }
    
    /**
     * Display a dialog notifying the user of a failure to perform some action.
     * @param title the title of the dialog.
     * @param message the dialog message.
     * @param throwable the throwable which caused the failure, if any.  May be null.
     */
    private void showActionFailureDialog(String title, String message, Throwable throwable) {
        if (throwable != null) {
            message += "\n" + throwable.getLocalizedMessage();
        }
        JOptionPane.showMessageDialog(GemCutter.this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Return the action for the menu item to remove a module from the current workspace.
     * @return Action
     */
    private Action getRemoveModuleAction() {

        if (removeModuleAction == null) {
            removeModuleAction = new AbstractAction (getResourceString("RemoveModule")) {
                                                        
                private static final long serialVersionUID = 9032351347191560711L;

                public void actionPerformed(ActionEvent evt) {
                    handleRemoveModuleAction();
                }
            };
            
            removeModuleAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_REMOVE_MODULE));
            removeModuleAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("RemoveModuleToolTip"));
        }
        return removeModuleAction;
    }

    /**
     * Handle the situation where the user has indicated that they would like to remove a module from the current workspace.
     * TODO: make some of these actions undoable.
     */
    private void handleRemoveModuleAction() {

        // Get the list of modules, and sort.
        ModuleName[] moduleNames = getWorkspace().getModuleNames();
        Arrays.sort(moduleNames);
        
        // Remove "Prelude" from the list, as CAL programs assume this exists.
        List<ModuleName> moduleNameList = new ArrayList<ModuleName>(Arrays.asList(moduleNames));
        moduleNameList.remove(CAL_Prelude.MODULE_NAME);
        moduleNames = moduleNameList.toArray(new ModuleName[moduleNameList.size()]);
        
        String dialogTitle = getResourceString("RemoveModuleDialogTitle");
        if (moduleNameList.isEmpty()) {            
            String message = getResourceString("RemoveModuleNoModulesMessage");
            JOptionPane.showMessageDialog(GemCutter.this, message, dialogTitle, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ask the user which module to remove.
        
        String message = getResourceString("RemoveModuleDialogMessage");
        
        ModuleName selectedModuleName = (ModuleName)JOptionPane.showInputDialog(GemCutter.this, message, dialogTitle, JOptionPane.PLAIN_MESSAGE, 
                                                                        null, moduleNames, moduleNames[0]);

        if (selectedModuleName == null) {
            return;
        }
        
        // Actually remove the module.
        doRemoveModuleUserAction(selectedModuleName);
    }
    
    /**
     * Do the work necessary to carry out a user-initiated action to remove a module from the GemCutter workspace.
     * @param moduleToRemove the name of the module to remove.
     */
    void doRemoveModuleUserAction(ModuleName moduleToRemove) {
        
        // Check if we are removing the current module
        // Note that the second check is necessary in the case that a program has been modified in such a way as to make it invalid
        //   (in which case, the working module name will be pointing to a module which does not exist).
        ModuleName currentModule = getWorkingModuleName();
        boolean removingCurrentModule = currentModule.equals(moduleToRemove) && 
                                        workspaceManager.getWorkspace().getNMetaModules() > 0;
        
        if (removingCurrentModule) {            
            String dialogMessage = getResourceString("RemoveModuleRemovingCurrentModule");
            String dialogTitle = getResourceString("RemoveModuleDialogTitle");
            
            boolean okToRemove = promptSaveCurrentTableTopIfNonEmpty(dialogMessage, dialogTitle);
            if (!okToRemove){
                // If cannot save or user cancelled
                return;
            }

        } else {
            
            // Check if removing the module will cause the current module to be removed as well.
            for (final ModuleName dependantModule : getWorkspaceManager().getDependentModuleNames(moduleToRemove)) {
                if (dependantModule.equals(currentModule)){
                    removingCurrentModule = true;
                    break;
                }
            }

            if (removingCurrentModule) {

                // If tabletop is not empty, prompt user to save, else prompt to confirm removing current module
                if (tableTop.getDisplayedGems().size() > 1) {
                    String dialogMessage = getResourceString("RemoveModuleRemovingModuleWillRemoveCurrentModule");
                    String dialogTitle = getResourceString ("RemoveModuleDialogTitle");

                    boolean okToRemove = promptSaveCurrentTableTopIfNonEmpty(dialogMessage, dialogTitle);
                    if (!okToRemove){
                        return;
                    }

                } else {
                    String dialogMessage = getResourceString ("RemoveModuleRemovingModuleWillRemoveCurrentModule_emptyTabletop");
                    String dialogTitle = getResourceString ("RemoveModuleDialogTitle");

                    int confirmOkToRemove = JOptionPane.showConfirmDialog(GemCutter.this, dialogMessage, dialogTitle, 
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (confirmOkToRemove != JOptionPane.OK_OPTION) {
                        return;
                    }
                }
            } 
        }

        Status removeStatus = new Status("Remove status");
        getWorkspaceManager().removeModule(moduleToRemove, false, removeStatus);
        
        // update the perspective if the working module no longer exists (eg. because of compile failure..)
        if (perspective.getWorkingModule() == null) {
            ModuleName newWorkingModuleName = getInitialWorkingModuleName(workspaceManager.getWorkspace());
            if (newWorkingModuleName != null) {
                if (removingCurrentModule) {
                    changeModuleAndNewTableTop(newWorkingModuleName, true);
                } else {
                    perspective.setWorkingModule(newWorkingModuleName);                    
                }
            }

            // Change the current module if we just removed it.
            if (removingCurrentModule) {
                // clear the undo stack and dirty edit.
                extendedUndoManager.discardAllEdits();
                editToUndoWhenNonDirty = null;
                updateUndoWidgets();
            }
            // Also update the window title.
            updateWindowTitle();
        }

        // mark workspace as changed in Gem Browser
        getGemBrowser().markWorkspaceDirty();  
        
        // Refresh the gem browser and navigator to show any new gems
        getGemBrowser().refresh();
        getNavigatorOwner().refresh();
        
        // Show any problems which were encountered during module resource removal.
        if (removeStatus.getSeverity().compareTo(Status.Severity.WARNING) >= 0 ) {
            String dialogTitle = getResourceString("RemoveModuleDialogTitle");
            String message = "Problems were encountered:\n"  + removeStatus.getDebugMessage();
            JOptionPane.showMessageDialog(GemCutter.this, message, dialogTitle, JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Return the action for the menu item to dump the current vault status of modules in the workspace
     * TODOEL: TEMP - this is somewhat of a debug menu item!
     * @return Action
     */
    private Action getWorkspaceVaultStatusAction() {

        if (workspaceVaultStatusAction == null) {
            workspaceVaultStatusAction = new AbstractAction (getResourceString("WorkspaceVaultStatus")) {
                                                        
                private static final long serialVersionUID = -5941578304595235616L;

                public void actionPerformed(ActionEvent evt) {
                    handleWorkspaceVaultStatusAction();
                }
            };
            
            workspaceVaultStatusAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_V));
            workspaceVaultStatusAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("WorkspaceVaultStatusToolTip"));
        }
        return workspaceVaultStatusAction;
    }

    /**
     * Handle the situation where the user has indicated that they would like to see the vault status of modules
     *   in the workspace.
     */
    private void handleWorkspaceVaultStatusAction() {

        // Just dump the text to a details dialog.
        VaultStatus vaultStatus = getWorkspace().getVaultStatus();
        DetailsDialog detailsDialog = new DetailsDialog(GemCutter.this, getResourceString("WorkspaceVaultStatusDialogTitle"), getResourceString("WorkspaceCurrentVaultStatus"), 
                                                        vaultStatus.getStatusString(), DetailsDialog.MessageType.PLAIN);
        detailsDialog.setDetailsVisible(true);
        detailsDialog.doModal();
    }

    /**
     * @return the submenu item to sync the workspace.
     */
    private JMenuItem getSyncSubMenu() {
        if (syncSubMenu == null) {
            syncSubMenu = makeNewMenu(getResourceString("Sync"));
            syncSubMenu.add(makeNewMenuItem(getSyncWorkspaceToHeadAction()));
            if (enterpriseSupport.isEnterpriseSupported()) {
                syncSubMenu.add(makeNewMenuItem(getSyncWorkspaceToEnterpriseDeclarationAction()));
            }
            
            syncSubMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_SYNC);
            syncSubMenu.setToolTipText(getResourceString("SyncToolTip"));
        }
        
        return syncSubMenu;
    }
    
    /**
     * @return the action for the menu item to sync the workspace with the head revisions of the modules in their associated vaults.
     */
    private Action getSyncWorkspaceToHeadAction() {
        if (syncToHeadAction == null) {
            syncToHeadAction = new AbstractAction(getResourceString("SyncWorkspaceToHead")) {
                                                        
                private static final long serialVersionUID = -3139343996877603543L;

                public void actionPerformed(ActionEvent evt) {
                    handleSyncWorkspaceToHeadAction();
                }
            };
            
            syncToHeadAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_H));
            syncToHeadAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("SyncWorkspaceToHeadToolTip"));
        }
        return syncToHeadAction;
    }

    /**
     * @return the action for the menu item to sync the workspace with the head revisions of the modules in their associated vaults.
     */
    private Action getSyncWorkspaceToEnterpriseDeclarationAction() {
        if (syncToEnterpriseDeclarationAction == null) {
            syncToEnterpriseDeclarationAction = new AbstractAction(getResourceString("SyncWorkspaceToEnterpriseDeclaration")) {
                                                        
                private static final long serialVersionUID = 6036990146764617078L;

                public void actionPerformed(ActionEvent evt) {
                    handleSyncWorkspaceToEnterpriseDeclarationAction();
                }
            };
            
            syncToEnterpriseDeclarationAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
            syncToEnterpriseDeclarationAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("SyncWorkspaceToEnterpriseDeclarationToolTip"));
        }
        return syncToEnterpriseDeclarationAction;
    }

    /**
     * Handle the situation where the user has indicated that they would like to sync the workspace to the head revisions of its modules 
     *   in the associated vaults.
     */
    private void handleSyncWorkspaceToHeadAction() {
        List<ModuleName> moduleRevisionList = Arrays.asList(getWorkspace().getModuleNames());
        doSyncModulesToLatestUserAction(moduleRevisionList);
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to sync the workspace to a workspace declaration in Enterprise.
     */
    private void handleSyncWorkspaceToEnterpriseDeclarationAction() {
        // Attempt to ensure that the user is connected to CE.
        if (!ensureEnterpriseConnectionIsReady()) {
            return;
        }
        
        Vault ceVault = enterpriseSupport.getEnterpriseVault();
        if (ceVault != null) {
            handleSyncWorkspaceToDeclarationAction(ceVault);
        }
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to sync a number of modules in the workspace
     * with the latest revisions available in their associated vaults.
     * @param moduleNames the names of the modules to sync.
     */
    void doSyncModulesToLatestUserAction(List<ModuleName> moduleNames) {
        
        // This may take a while, so set the cursor.
        Cursor oldCursor = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            Status syncStatus = new Status(getResourceString("SyncWorkspaceStatus"));
            CALWorkspace workspace = getWorkspace();
            CALWorkspace.SyncInfo syncInfo = new CALWorkspace.SyncInfo();
            
            for (final ModuleName moduleName : moduleNames) {
                CALWorkspace.SyncInfo newSyncInfo = workspace.syncModuleToRevision(moduleName, -1, false, syncStatus);
                syncInfo.addInfo(newSyncInfo);
            }
            
            handleUserSyncPerformed(syncInfo, syncStatus);
            
        } finally {
            setCursor(oldCursor);
        }
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to sync the workspace to a declaration
     *   which is contained within an indicated vault.
     */
    private void handleSyncWorkspaceToDeclarationAttempt(Vault vault, String declarationName, int revisionNumber) {
        Status syncStatus = new Status(getResourceString("SyncWorkspaceStatus"));
        
        // Instantiate a provider.
        WorkspaceDeclaration.StreamProvider workspaceDeclarationProvider = 
                new VaultWorkspaceDeclarationProvider(vault, declarationName, revisionNumber);

        // This may take a while, so set the cursor.
        Cursor oldCursor = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            // Sync to the workspace declaration.
            CALWorkspace.SyncInfo syncInfo = getWorkspaceManager().syncWorkspaceToDeclaration(workspaceDeclarationProvider, syncStatus);
            if (syncInfo != null) {
                handleUserSyncPerformed(syncInfo, syncStatus);
            }
        
        } finally {
            // Reset the cursor.
            setCursor(oldCursor);
        }
        
        // Deal with errors.
        if (syncStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
            String message = "Problems encountered while constructing the workspace:\n" + syncStatus.getDebugMessage();
            syncStatus.add(new Status(Status.Severity.ERROR, message, null));
        }
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to sync the workspace with a declaration in a vault.
     * @param vault the vault from which to retrieve the workspace declaration
     */
    private void handleSyncWorkspaceToDeclarationAction(Vault vault) {

        // Display the dialog from which the user can select the workspace declaration.
        String dialogTitle = getResourceString("VMCD_SelectWorkspaceTitle");
        String dialogMessage = GemCutter.getResourceString("VMCD_ChooseWorkspaceForSyncMessage");
        VaultResourceChooserDialog workspaceChooserDialog = 
                VaultResourceChooserDialog.getWorkspaceDeclarationChooserDialog(GemCutter.this, dialogTitle, dialogMessage, vault);
        
        if (workspaceChooserDialog == null) {
            showActionFailureDialog(getResourceString("ErrorDialogTitle"), getResourceString("CannotObtainResourcesFromVault"), null);
            return;
        }
        
        boolean accepted = workspaceChooserDialog.doModal();

        // Get the selected module version.
        VaultResourceChooserDialog.SelectedResourceVersion selectedWorkspaceVersion = workspaceChooserDialog.getSelectedResourceVersion();
        if (!accepted || selectedWorkspaceVersion == null) {
            return;
        }
        
        // Try to sync the workspace.
        handleSyncWorkspaceToDeclarationAttempt(vault, selectedWorkspaceVersion.getResourceName(), selectedWorkspaceVersion.getRevisionNumber());
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to sync a number of modules in the workspace 
     * with revisions available in their associated vaults.
     * @param moduleRevisions the names and revisions of the modules to sync.
     * @param force whether the sync should be forced.  If true, any user changes will be clobbered.
     */
    void doSyncModulesUserAction(List<ModuleRevision> moduleRevisions, boolean force) {
        
        // This may take a while, so set the cursor.
        Cursor oldCursor = getCursor();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try{
            Status syncStatus = new Status(getResourceString("SyncWorkspaceStatus"));
            CALWorkspace workspace = getWorkspace();
            CALWorkspace.SyncInfo syncInfo = new CALWorkspace.SyncInfo();
            
            for (final ModuleRevision moduleRevision : moduleRevisions) {
                CALWorkspace.SyncInfo newSyncInfo = 
                    workspace.syncModuleToRevision(moduleRevision.getModuleName(), moduleRevision.getRevisionNumber(), force, syncStatus);
                syncInfo.addInfo(newSyncInfo);
            }
            
            handleUserSyncPerformed(syncInfo, syncStatus);
            
        } finally {
            setCursor(oldCursor);
        }
    }
    
    /**
     * Handle the interaction with the user when the sync is performed.
     * @param syncInfo the info about the sync which was performed.
     * @param syncStatus the status object which was tracking the sync operation.
     */
    private void handleUserSyncPerformed(CALWorkspace.SyncInfo syncInfo, Status syncStatus) {
        
        if (syncStatus.getSeverity().compareTo(Status.Severity.WARNING) >= 0) {
            String title = getResourceString("SyncWorkspaceWarningTitle");
            String message = getResourceString("SyncWorkspaceWarningMessage");
            DetailsDialog detailsDialog = new DetailsDialog(GemCutter.this, title, message, 
                                                            syncStatus.getDebugMessage(), DetailsDialog.MessageType.WARNING);
            detailsDialog.setDetailsVisible(true);
            detailsDialog.doModal();
        }
        
        // Display the result of the sync.
        Set<ResourceIdentifier> updatedResourceIdentifierSet = syncInfo.getUpdatedResourceIdentifiers();
        Set<ResourceIdentifier> resourceImportFailureSet = syncInfo.getResourceImportFailures();
        Set<ResourceIdentifier> syncConflictIdentifierSet = syncInfo.getSyncConflictIdentifiers();
        Set<ResourceIdentifier> deletedResourceIdentifierSet = syncInfo.getDeletedResourceIdentifiers();
        
        StringBuilder sb = new StringBuilder();
        sb.append(getResourceString("SyncWorkspaceUpdatedResources"));
        dumpIdentifierSet(updatedResourceIdentifierSet, sb);
        
        if (!deletedResourceIdentifierSet.isEmpty()) {
            sb.append(getResourceString("SyncWorkspaceDeletedResources"));
            dumpIdentifierSet(deletedResourceIdentifierSet, sb);
        }
        
        if (!syncConflictIdentifierSet.isEmpty()) {
            sb.append(getResourceString("SyncWorkspaceConflicts"));
            dumpIdentifierSet(syncConflictIdentifierSet, sb);
        }
        
        if (!resourceImportFailureSet.isEmpty()) {
            sb.append(getResourceString("SyncWorkspaceFailed"));
            dumpIdentifierSet(resourceImportFailureSet, sb);
        }
        
        String syncResultMessage = sb.toString();
        
        String title = getResourceString("SyncWorkspaceResultTitle");
        String message = getResourceString("SyncWorkspaceResultMessage");
        DetailsDialog detailsDialog = new DetailsDialog(GemCutter.this, title, message, 
                                                        syncResultMessage, DetailsDialog.MessageType.PLAIN);
        
        detailsDialog.setDetailsVisible(true);
        detailsDialog.doModal();
        
        // Compile modified modules.
        recompileWorkspace(true);
    }

    /**
     * A helper method to syncWorkspaceAction() to dump the contents of an identifier set into a string builder.
     * @param identifierSet the identifiers to dump.
     * @param sb the stringBuilder into which the identifiers will be dumped.
     */
    private void dumpIdentifierSet(Set<ResourceIdentifier> identifierSet, StringBuilder sb) {
        
        if (identifierSet.isEmpty()) {
            sb.append(getResourceString("SyncWorkspaceNone"));

        } else {
            // Convert to an array and sort.
            List<ResourceIdentifier> identiferList = new ArrayList<ResourceIdentifier>(identifierSet);
            String[] identifierStrings = new String[identiferList.size()];
            
            int index = 0;
            for (final ResourceIdentifier resourceIdentifier : identifierSet) {
                identifierStrings[index] = resourceIdentifier.toString();
                index++;
            }
            Arrays.sort(identifierStrings);
            
            for (final String identifierString : identifierStrings) {
                sb.append("  " + identifierString + "\n");
            }

        }
    }
    
    /**
     * Return the action for the switch workspace menu item
     * @return Action
     */
    private Action getSwitchWorkspaceAction() {
        
        if (switchWorkspaceAction == null) {
            switchWorkspaceAction = new AbstractAction (getResourceString("SwitchWorkspace")) {
                                                        
                private static final long serialVersionUID = 7632815811866012148L;

                public void actionPerformed(ActionEvent evt) {
                    handleSwitchWorkspaceAction();
                }
            };
            
            switchWorkspaceAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_SWITCH_WORKSPACE));
            switchWorkspaceAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("SwitchWorkspaceToolTip"));
        }
        return switchWorkspaceAction;
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to switch to a different workspace.
     */
    private void handleSwitchWorkspaceAction() {
        
        SwitchWorkspaceDialog dialog = new SwitchWorkspaceDialog(this, workspaceManager);
        centerWindow(dialog);
        dialog.setVisible(true);

        if (!dialog.isOKSelected()) {
            return;
        }
        
        handleSwitchWorkspaceAction(dialog.getNextWorkspaceDeclarationStreamProvider());
    }

    /**
     * Handle the situation where the user has indicated that they would like to switch to a different workspace.
     * Switches GemCutter to the specified workspace declaration except when user decides to cancel or if saving the current tabletop failed
     * @param nextWorkspaceDeclarationStreamProvider
     */
    private void handleSwitchWorkspaceAction(final WorkspaceDeclaration.StreamProvider nextWorkspaceDeclarationStreamProvider) {
        
        if (!promptSaveCurrentTableTopIfNonEmpty(null, null)){
            return;
        }
        
        gemCutterSplashScreen = new GemCutterSplashScreen(this);
        gemCutterSplashScreen.pack();
        
        gemCutterSplashScreen.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        centerWindow(gemCutterSplashScreen);
        
        getTableTopPanel().enableMouseEvents(false);

        // Keep the GUI locked while compiling.
        final GUIState oldGUIState = getGUIState();
        setEnabled(false);
        enterGUIState(GUIState.LOCKED_DOWN);
        
        // Create the workspace loading thread
        Thread thread = new Thread() {
            public void run() {
                try {
                    // Update the preferred working module if the user is working on something.
                    if (getTableTop().getGemGraph().getGems().size() > 1) {
                        preferredWorkingModuleName = getWorkingModuleName();
                    }
                    
                    String clientID = WorkspaceConfiguration.getDiscreteWorkspaceID(DEFAULT_WORKSPACE_CLIENT_ID);
                    GemCutter.this.workspaceManager = WorkspaceManager.getWorkspaceManager(clientID);
                    initWorkspace(nextWorkspaceDeclarationStreamProvider);

                    final CALWorkspace workspace = workspaceManager.getWorkspace();
                    
                    
                    // Find number of modules to be loaded and pass this information to progress bar
                    int nModules = workspace.getModuleNames().length;
                    gemCutterSplashScreen.setProgressBarMaxValue(nModules);
                    
                    // Compile specified module or all modules in workspace
                    long compileStartTime = System.currentTimeMillis();
                    boolean foundErrors = compileWorkspace(false);
                    final String statusMessage;
                    if (!foundErrors) {
                        statusMessage = GemCutterMessages.getString("SM_RecompilationFinished", Double.toString((System.currentTimeMillis() - compileStartTime)/1000.0));                
                    } else {                
                        statusMessage = GemCutterMessages.getString("SM_RecompilationErrors");                
                    }
                    
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            statusMessageManager.displayMessage(this, statusMessage, StatusMessageDisplayer.MessageType.TRANSIENT, true);
                            
                            // The new working module, if any.
                            ModuleName newWorkingModuleName;
                            
                            // Change to the preferred working module if it's not the current module, and it exists.
                            if (preferredWorkingModuleName != null && workspace.getMetaModule(preferredWorkingModuleName) != null) {
                                newWorkingModuleName = preferredWorkingModuleName;
                            } else {
                                newWorkingModuleName = getInitialWorkingModuleName(workspace);
                            }
                            
                            // Create the perspective
                            MetaModule initialWorkingModule = workspace.getMetaModule(newWorkingModuleName);
                            perspective = new Perspective(workspace, initialWorkingModule);
                            
                            // Clear the TableTop before resetting the runners, so that value editors can be closed
                            // by the original ValueEditorHierarchyManager.
                            newTableTop();
                            
                            // Reset the runners
                            try{
                                initRunners();
                            } catch (ValueEntryException exception) {
                                JOptionPane.showMessageDialog(GemCutter.this, exception.getMessage() + "\nCause: " + exception.getCause().getMessage() + "\n\nGemCutter will shut-down.", 
                                        "Error in initializing the value entry handlers:", JOptionPane.ERROR_MESSAGE);
                            }   

                            // the perspective has changed, so anything that caches a perspective must be reset
                            forgetSearchDialog();
                            
                            // Update the preferred working module.
                            preferredWorkingModuleName = newWorkingModuleName;
                            
                            // Now, create a new the TableTop.
                            newTableTop();
                            
                            // clear the undo stack and dirty edit.
                            extendedUndoManager.discardAllEdits();
                            editToUndoWhenNonDirty = null;
                            updateUndoWidgets();
                            
                            // Also update the window title.
                            updateWindowTitle();
                            
                            // set workspace name in Gem Browser to include name of workspace file
                            getGemBrowser().setWorkspaceNodeName(nextWorkspaceDeclarationStreamProvider.getName());
                            
                            // Refresh the gem browser and navigator to show any new gems
                            getGemBrowser().reinitialize(perspective);
                            getNavigatorOwner().refresh();
                            
                            // Ensure the module is visible.
                            getGemBrowser().getBrowserTree().selectDrawerNode(newWorkingModuleName);
                        }
                    });
                    
                } catch (InterruptedException e) {
                    IllegalStateException ex = new IllegalStateException("This thread should not be interrupted by anything.");
                    ex.initCause(e);
                    throw ex;
                } catch (InvocationTargetException e) {
                    IllegalStateException ex = new IllegalStateException("The invokeAndWait call should always succeed.");
                    ex.initCause(e);
                    throw ex;
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            getTableTopPanel().enableMouseEvents(true);
                            enterGUIState(oldGUIState);
                            GemCutter.this.setEnabled(true);
                            gemCutterSplashScreen.dispose();
                            gemCutterSplashScreen = null;
                        }
                    });
                }
            }
        };
        
        // start the thread then launch the splash screen (which will be closed by the thread when it is done)
        thread.start();
        gemCutterSplashScreen.setVisible(true);
    }

    /**
     * Return the action for the deploy workspace menu item
     * @return Action
     */
    private Action getDeployWorkspaceToEnterpriseAction() {

        if (deployWorkspaceToEnterpriseAction == null) {
            deployWorkspaceToEnterpriseAction = new AbstractAction (getResourceString("DeployWorkspaceToEnterprise")) {
                                                        
                private static final long serialVersionUID = -3305904595279750182L;

                public void actionPerformed(ActionEvent evt) {
                    handleDeployWorkspaceToEnterpriseAction();
                }
            };
            
            deployWorkspaceToEnterpriseAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_DEPLOY_WORKSPACE));
            deployWorkspaceToEnterpriseAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("DeployWorkspaceToEnterpriseToolTip"));
        }
        return deployWorkspaceToEnterpriseAction;
    }

    /**
     * Handle the situation where the user has indicated that they would like to deploy the current workspace.
     */
    private void handleDeployWorkspaceToEnterpriseAction() {

        // Attempt to ensure that the user is connected to CE.
        if (!ensureEnterpriseConnectionIsReady()) {
            return;
        }
        
        Vault ceVault = enterpriseSupport.getEnterpriseVault();
        if (ceVault != null) {
            handleDeployWorkspaceAction(ceVault);
        }
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to deploy the current workspace.
     * @param vault the vault to which to deploy the new workspace.
     */
    private void handleDeployWorkspaceAction(Vault vault) {

        Status deployStatus = new Status("Deploy status");

        // Map from module name to the latest revision identical to the current form of that module, if any.
        Map<ModuleName, Integer> moduleNameToExistingRevisionMap = new HashMap<ModuleName, Integer>();
        
        // Iterate over the modules in the workspace.
        ModuleName[] moduleNames = getWorkspace().getModuleNames();
        
        DeployWorkspaceDialog.ModuleRevisionInfo[] moduleRevisions = new DeployWorkspaceDialog.ModuleRevisionInfo[moduleNames.length];
        for (int i = 0; i < moduleNames.length; i++) {
            ModuleName moduleName = moduleNames[i];
            
            // TODOEL: should we try to add getLatestIdenticalRevision() to the vault interface?
            int latestIdenticalRevision = 
                enterpriseSupport.getLatestIdenticalRevisionFromMaybeEnterpriseVault(vault, moduleName, getWorkspace(), deployStatus);

            if (latestIdenticalRevision > 0) {
                // An identical revision already exists.
                moduleRevisions[i] = new DeployWorkspaceDialog.ModuleRevisionInfo(moduleName, latestIdenticalRevision, false);
                moduleNameToExistingRevisionMap.put(moduleName, new Integer(latestIdenticalRevision));
            
            } else {
                // We must deploy a new revision of the module.
                RevisionHistory revisionHistory = vault.getModuleRevisionHistory(moduleName);
                int latestRevision = revisionHistory.getLatestRevision();
                int revisionToDeploy = (latestRevision < 0) ? 1 : latestRevision + 1;
                moduleRevisions[i] = new DeployWorkspaceDialog.ModuleRevisionInfo(moduleName, revisionToDeploy, true);
            }
        }
        
        // Handle any errors (in vaultStatus) getting the latest revision info.
        if (deployStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
            JOptionPane.showMessageDialog(this, deployStatus.getDebugMessage(), 
                                          getResourceString("DeployWorkspaceFailedTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        // Display the dialog for deploying the workspace.
        String[] availableWorkspaceDeclarations = vault.getAvailableWorkspaceDeclarations(deployStatus);
        
        DeployWorkspaceDialog deployWorkspaceDialog = new DeployWorkspaceDialog(GemCutter.this, moduleRevisions, availableWorkspaceDeclarations);
        boolean accepted = deployWorkspaceDialog.doModal();
        
        if (!accepted) {
            return;
        }

        // Map from module name to the deployed revision for that module.
        Map<ModuleName, Integer> moduleNameToDeployedRevisionMap = new TreeMap<ModuleName, Integer>();    

        // Get the result, and deploy...
        DeployWorkspaceDialog.DeployDialogResult result = deployWorkspaceDialog.getResult();

        ModuleName[] modulesToDeploy = result.getModulesToDeploy();
        for (final ModuleName moduleToDeploy : modulesToDeploy) {
            Integer existingRevisionInteger = moduleNameToExistingRevisionMap.get(moduleToDeploy);
            if (existingRevisionInteger != null) {
                // Don't need to add the module to the vault.  Just reuse the revision number
                moduleNameToDeployedRevisionMap.put(moduleToDeploy, existingRevisionInteger);
                
            } else {
                // Actually add the module to the vault.
                int addedRevisionNumber = vault.putStoredModule(moduleToDeploy, getWorkspace(), deployStatus);
                
                // Remember the revision number which was added, so we can use it in the workspace declaration.
                moduleNameToDeployedRevisionMap.put(moduleToDeploy, new Integer(addedRevisionNumber));
            }
        }
        
        // Handle any errors (in vaultStatus) adding the module revisions.
        if (deployStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
            JOptionPane.showMessageDialog(this, deployStatus.getDebugMessage(), 
                                          getResourceString("DeployWorkspaceFailedTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Now deploy the workspace file..
        String workspaceDeclarationName = result.getWorkspaceName();
        
        String vaultDescriptor = vault.getVaultProvider().getVaultDescriptor();
        String vaultLocation = vault.getLocationString();
        
        VaultElementInfo[] vaultModuleInfo = new VaultElementInfo[moduleNameToDeployedRevisionMap.size()];
        
        int index = 0;
        for (final Map.Entry<ModuleName, Integer> mapEntry : moduleNameToDeployedRevisionMap.entrySet()) {
            String moduleName = mapEntry.getKey().toString();
            int deployedRevisionNum = mapEntry.getValue().intValue();
            
            vaultModuleInfo[index] = VaultElementInfo.makeBasic(vaultDescriptor, moduleName, vaultLocation, deployedRevisionNum);
            index++;
        }
        
        // Create the workspace declaration.
        WorkspaceDeclaration workspaceDeclaration = new WorkspaceDeclaration(vaultModuleInfo);
        
        // Put the declaration in the vault.
        vault.putWorkspaceDeclaration(workspaceDeclarationName, workspaceDeclaration, deployStatus);

        // Handle any errors (in vaultStatus) adding the workspace deployment.
        if (deployStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) {
            JOptionPane.showMessageDialog(this, deployStatus.getDebugMessage(), 
                                          getResourceString("DeployWorkspaceFailedTitle"), JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    /**
     * @return the action for the export to Cars menu item
     */
    private Action getExportWorkspaceToCarsAction() {
        
        if (exportWorkspaceToCarsAction == null) {
            exportWorkspaceToCarsAction = new AbstractAction(getResourceString("ExportWorkspaceToCars")) {
                                                        
                private static final long serialVersionUID = -3900731741152188465L;

                public void actionPerformed(ActionEvent evt) {
                    handleExportWorkspaceToCarsAction();
                }
            };
            
            exportWorkspaceToCarsAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_EXPORT_WORKSPACE_TO_CARS));
            exportWorkspaceToCarsAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ExportWorkspaceToCarsToolTip"));
        }
        return exportWorkspaceToCarsAction;
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to export the current workspace to one or more Cars.
     */
    private void handleExportWorkspaceToCarsAction() {
        
        ExportCarDialog dialog = new ExportCarDialog(this, workspaceManager.getInitialWorkspaceDeclarationName());
        centerWindow(dialog);
        dialog.setVisible(true);

        if (!dialog.isOKSelected()) {
            return;
        }
        
        Set<String> carsToExclude = Collections.emptySet();
        CarBuilder.BuilderOptions options = new CarBuilder.BuilderOptions(
            dialog.shouldSkipModulesAlreadyInCars(),
            dialog.shouldGenerateCorrespWorkspaceDecl(),
            dialog.shouldOmitCarSuffixInWorkspaceDeclName(),
            dialog.shouldBuildSourcelessModules(),
            carsToExclude,
            dialog.shouldGenerateCarJarSuffix());
        
        if (dialog.shouldBuildSingleCar()) {
            File selectedFile = dialog.getSingleCarOutputDirectory();
            
            // Confirm overwrite if the file exists.
            if (selectedFile.exists()) {
                String title = getResourceString("ConfirmOverwriteTitle");
                String message = getResourceString("ConfirmOverwriteCarMessage");            
                int continueChoice = JOptionPane.showConfirmDialog(GemCutter.this, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (continueChoice != JOptionPane.OK_OPTION) {
                    return;
                }
            }
            
            buildSingleCar(selectedFile, options);
            
        } else if (dialog.shouldBuildOneCarPerWorkspaceDecl()) {
            buildOneCarPerWorkspaceDeclaration(dialog.getOneCarPerWorkspaceDeclOutputDirectory(), options);
        }
    }

    /**
     * Builds a Car from the current workspace and writes it out to the specified output file.
     * @param outputDirectory the directory to which Car files will be written.
     * @param options configuration options for the Car builder.
     */
    private void buildSingleCar(final File outputDirectory, final CarBuilder.BuilderOptions options) {
        // Set up the progress monitor for the potentially length export process
        ExportCarProgressMonitor monitor = new ExportCarProgressMonitor();
        
        // Configure the CarBuilder
        final CarBuilder.Configuration config =
            CarBuilder.Configuration.makeConfigOptionallySkippingModulesAlreadyInCars(workspaceManager, options);
        config.setMonitor(monitor);
        
        final String carName = CarBuilder.makeCarNameFromSourceWorkspaceDeclName(workspaceManager.getInitialWorkspaceDeclarationName());
        
        // Set up a SwingWorker to run the export process in a separate thread.
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    CarBuilder.buildCar(config, outputDirectory, carName, options);
                    
                    // Display a status message.
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            String statusMessage = GemCutterMessages.getString("SM_CarExported", carName);
                            statusMessageManager.displayMessage(GemCutter.this, statusMessage, StatusMessageDisplayer.MessageType.TRANSIENT, true);
                        }
                    });
                    
                } catch (final IOException e) {
                    // Inform the user.
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showActionFailureDialog(getResourceString("ExportWorkspaceToCarFailedDialogTitle"), getResourceString("ExportWorkspaceToCarFailedDialogMessage"), e);
                        }
                    });
                }
                return null;
            }
        };
        
        // Start the CarBuilder worker and then launch the progress monitor (which is modal and would block the UI until
        // either the progress reaches 100% or the monitor is canceled)
        worker.start();
        monitor.showDialog();
        // there is no interesting value to get from the worker, but a call to get() effectively does a join
        // on the worker thread
        worker.get();
    }
    
    /**
     * Builds one Car file per workspace declaration file that constitutes the initial declaration of the CAL workspace.
     * @param outputDirectory the directory to which Car files will be written.
     * @param options configuration options for the Car builder.
     */
    private void buildOneCarPerWorkspaceDeclaration(final File outputDirectory, final CarBuilder.BuilderOptions options) {
        // Set up the progress monitor for the potentially length export process
        final ExportCarProgressMonitor monitor = new ExportCarProgressMonitor();
        
        // Set up a SwingWorker to run the export process in a separate thread.
        SwingWorker worker = new SwingWorker() {
            public Object construct() {
                try {
                    CarBuilder.buildOneCarPerWorkspaceDeclaration(workspaceManager, monitor, outputDirectory, options);
                    
                    // Display a status message.
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            String statusMessage = GemCutterMessages.getString("SM_CarExported", outputDirectory.getAbsolutePath());
                            statusMessageManager.displayMessage(GemCutter.this, statusMessage, StatusMessageDisplayer.MessageType.TRANSIENT, true);
                        }
                    });
                    
                } catch (final IOException e) {
                    // Inform the user.
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showActionFailureDialog(getResourceString("ExportWorkspaceToCarFailedDialogTitle"), getResourceString("ExportWorkspaceToCarFailedDialogMessage"), e);
                        }
                    });
                }
                return null;
            }
        };
        
        // Start the CarBuilder worker and then launch the progress monitor (which is modal and would block the UI until
        // either the progress reaches 100% or the monitor is canceled)
        worker.start();
        monitor.showDialog();
        // there is no interesting value to get from the worker, but a call to get() effectively does a join
        // on the worker thread
        worker.get();
    }
    
    /**
     * @return Action the action that handles the "Rename Gem" functionality
     */
    private Action getRenameGemAction() {
        
        if (renameGemAction == null) {
            try {
                renameGemAction = new AbstractAction(getResourceString("RenameGemAction"), RenameRefactoringDialog.FUNCTION_ICON) {                    
                    private static final long serialVersionUID = 3320148498474606760L;

                    public void actionPerformed(ActionEvent evt) {
                        showRenameEntityDialog(RenameRefactoringDialog.EntityType.Gem, null);
                    }
                };
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return renameGemAction;
    }
    
    /**
     * @return Action the action that handles the "Rename Type" functionality
     */
    private Action getRenameTypeAction() {
        
        if (renameTypeAction == null) {
            try {
                renameTypeAction = new AbstractAction(getResourceString("RenameTypeAction"), RenameRefactoringDialog.TYPECONS_ICON) {
                    private static final long serialVersionUID = -417842886714530380L;

                    public void actionPerformed(ActionEvent evt) {
                        showRenameEntityDialog(RenameRefactoringDialog.EntityType.TypeConstructor, null);
                    }
                };
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return renameTypeAction;
    }
    
    /**
     * @return Action the action that handles the "Rename Type Class" functionality
     */
    private Action getRenameClassAction() {
        
        if (renameClassAction == null) {
            try {
                renameClassAction = new AbstractAction(getResourceString("RenameClassAction"), RenameRefactoringDialog.TYPECLASS_ICON) {
                    private static final long serialVersionUID = 685835542162469962L;

                    public void actionPerformed(ActionEvent evt) {
                        showRenameEntityDialog(RenameRefactoringDialog.EntityType.TypeClass, null);
                    }
                };
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return renameClassAction;
    }
    
    /**
     * @return Action the action that handles the "Rename Module" functionality
     */
    private Action getRenameModuleAction() {
        
        if (renameModuleAction == null) {
            try { 
                renameModuleAction = new AbstractAction(getResourceString("RenameModuleAction"), RenameRefactoringDialog.MODULE_ICON) {
                    
                    private static final long serialVersionUID = -6072806933029026625L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        showRenameModuleDialog(null);
                    }
                };
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return renameModuleAction;
    }
    
    /**
     * Shows the "Rename Gem" refactoring dialog, then waits for it to close before recompiling
     * (if any refactorings are made)
     * @param fromName entity to rename; null if the dialog is to provide ability to choose
     */
    void showRenameEntityDialog(RenameRefactoringDialog.EntityType entityType, String fromName) {
        tableTop.getUndoableEditSupport().beginUpdate();
        RenameRefactoringDialog renameDialog = 
            new RenameRefactoringDialog(GemCutter.this, getWorkspaceManager(), perspective, fromName, null, entityType, isAllowPreludeRenamingMode(), isAllowDuplicateRenamingMode());        
        
        // Display refactoring dialog and recompile dirty modules if any changes are made        
        RenameRefactoringDialog.Result renameResult = renameDialog.display();
        if (renameResult != null) {
            QualifiedName qualifiedFromName = QualifiedName.makeFromCompoundName(renameResult.getFromName());
            QualifiedName qualifiedToName = QualifiedName.makeFromCompoundName(renameResult.getToName());
            try {
                GemCutterRenameUpdater designUpdater = new GemCutterRenameUpdater(new Status("GemGraph update status"), getTypeChecker(), qualifiedToName, qualifiedFromName, renameResult.getCategory());
                designUpdater.updateTableTop(this);
            } catch (GemEntityNotPresentException fanpe) {
                // Couldn't reload the functional agent for one of the gems.               
                JOptionPane.showMessageDialog(this, "Error reloading functional agent: " + fanpe.getEntityName(), 
                        "Reload error", JOptionPane.WARNING_MESSAGE);
            }           
            tableTop.getUndoableEditSupport().postEdit(
                    new UndoableRenameGemEdit(qualifiedFromName.getUnqualifiedName(), qualifiedToName.getUnqualifiedName(), qualifiedFromName.getModuleName(), renameResult.getEntityType(), this));
            tableTop.getUndoableEditSupport().endUpdate();
            recompileWorkspace(true);            
        } else {
            tableTop.getUndoableEditSupport().endUpdateNoPost();
        }
    }
    
    /**
     * Shows the "Rename Module" dialog
     * @param moduleName  module to rename; null if the dialog is to provide the ability to choose
     */
    void showRenameModuleDialog(ModuleName moduleName) {
        final String moduleNameString = (moduleName == null) ? null : moduleName.toSourceText();
        RenameRefactoringDialog renameDialog =
            new RenameRefactoringDialog(GemCutter.this, getWorkspaceManager(), perspective, moduleNameString, null, RenameRefactoringDialog.EntityType.Module, isAllowPreludeRenamingMode(), false);
        
        // Display refactoring dialog and recompile dirty modules if any changes are made
        RenameRefactoringDialog.Result renameResult = renameDialog.display();
        if (renameResult != null) {
            QualifiedName qualifiedFromName = QualifiedName.make(ModuleName.make(renameResult.getFromName()), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
            QualifiedName qualifiedToName = QualifiedName.make(ModuleName.make(renameResult.getToName()), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
            try {
                GemCutterRenameUpdater designUpdater = new GemCutterRenameUpdater(new Status("GemGraph update status"), getTypeChecker(), qualifiedToName, qualifiedFromName, renameResult.getCategory());
                designUpdater.updateTableTop(this);
            } catch (GemEntityNotPresentException fanpe) {
                // Couldn't reload the functional agent for one of the gems.
                JOptionPane.showMessageDialog(this, "Error reloading functional agent: " + fanpe.getEntityName(),
                        "Reload error", JOptionPane.WARNING_MESSAGE);
            }
            tableTop.getUndoableEditSupport().postEdit(
                    new UndoableRenameModuleEdit(renameResult.getFromName(), renameResult.getToName(), this));
            
            // If the working module was renamed, update the perspective's reference to it.
            if (perspective.getWorkingModuleName().equals(ModuleName.make(renameResult.getFromName()))) {
                perspective.setWorkingModule(ModuleName.make(renameResult.getToName()));
            }
            recompileWorkspace(true);
        }
    }

    /**
     * Return the action for the recompile menu item
     * @return Action
     */
    private Action getRecompileAction() {
        
        if (recompileAction == null) {
            recompileAction = new AbstractAction (getResourceString("RecompileWorkspace")) {
                                                        
                private static final long serialVersionUID = -5508747529637517065L;

                public void actionPerformed(ActionEvent evt) {
                    // recompile all modules from the source provider.
                    recompileWorkspace(false);
                }
            };
            
            recompileAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_RECOMPILE));
            recompileAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("RecompileWorkspaceToolTip"));
        }
        return recompileAction;
    }
    
    /**
     * Return the action for the recompile menu item
     * @return Action
     */
    private Action getCompileModifiedAction() {
        
        if (compileModifiedAction == null) {
            compileModifiedAction = new AbstractAction (getResourceString("CompileModifiedModules")) {
                                                        
                private static final long serialVersionUID = 2551065637046377474L;

                public void actionPerformed(ActionEvent evt) {
                    // recompile dirty modules from the source provider.
                    recompileWorkspace(true);
                }
            };
            
            compileModifiedAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_COMPILE_MODIFIED));
        }
        return compileModifiedAction;
    }
    
    /**
     * Return the action for the create minimal workspace menu item
     */
    private Action getCreateMinimalWorkspaceAction() {
        
        if (createMinimalWorkspaceAction == null) {
            createMinimalWorkspaceAction = new AbstractAction (getResourceString("CreateMinimalWorkspace")) {
                                                        
                private static final long serialVersionUID = -8571845374052703275L;

                public void actionPerformed(ActionEvent evt) {
                    handleCreateMinimalWorkspaceAction();
                }
            };
            
            createMinimalWorkspaceAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_CREATE_MINIMAL_WORKSPACE));
            createMinimalWorkspaceAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("CreateMinimalWorkspaceToolTip"));
        }
        return createMinimalWorkspaceAction;
    }
    
    /**
     * Handle the situation where the user has indicated that they would like to create a minimal workspace.
     */
    private void handleCreateMinimalWorkspaceAction() {
        
        CreateMinimalWorkspaceDialog dialog = new CreateMinimalWorkspaceDialog(this, workspaceManager.getWorkspace());
        centerWindow(dialog);
        dialog.setVisible(true);

        if (!dialog.isOKSelected()) {
            return;
        }
        
        final File outputFile = dialog.getOutputFile();
        final String workspaceDeclaration = dialog.getMinimalWorkspaceDeclaration();
        
        IOException ex = null;
        
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputFile);
            fw.write(workspaceDeclaration);
            fw.flush();
        } catch (IOException e) {
            ex = e;
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    ex = e;
                }
            }
        }
        
        if (ex != null) {
            showActionFailureDialog(getResourceString("CreateMinimalWorkspaceFailedDialogTitle"), getResourceString("CreateMinimalWorkspaceFailedDialogMessage"), ex);
            return;
        }
        
        if (dialog.shouldSwitchAfter()) {
            handleSwitchWorkspaceAction(new WorkspaceDeclaration.StreamProvider() {
                public String getName() {
                    return outputFile.getName();
                }
                public String getLocation() {
                    String parentDir = outputFile.getParent();
                    if (parentDir == null) {
                        return "(temporary)";
                    } else {
                        return parentDir;
                    }
                }
                public String getDebugInfo(VaultRegistry vaultRegistry) {
                    return "from file: " + outputFile.getAbsolutePath();
                }
                public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
                    return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(workspaceDeclaration));
                }
            });
        }
    }

    /**
     * Return the action for the workspace info menu item
     * @return Action
     */
    private Action getWorkspaceInfoAction() {
        
        if (workspaceInfoAction == null) {
            workspaceInfoAction = new AbstractAction (getResourceString("WorkspaceInfo")) {
                                                        
                private static final long serialVersionUID = -1981432576429432927L;

                public void actionPerformed(ActionEvent evt) {
                    handleWorkspaceInfoAction();
                }
            };
            
            workspaceInfoAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_WORKSPACE_INFO));
            workspaceInfoAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("WorkspaceInfoToolTip"));
        }
        return workspaceInfoAction;
    }

    /**
     * Handle the situation where the user has indicated that they would like to see more info about the current workspace.
     */
    private void handleWorkspaceInfoAction() {

        String title = getResourceString("WorkspaceInfo_Title");
        String message = getResourceString("WorkspaceInfo_Message");

        // Current workspace
        
        StringBuilder details = new StringBuilder();

        details.append(getResourceString("WorkspaceInfo_Declaration"));
        details.append("     " + getWorkspaceManager().getInitialWorkspaceDeclarationName() + "\n");
        details.append("        - " + getWorkspaceManager().getInitialWorkspaceDeclarationDebugInfo() + "\n");
        
        CALWorkspace workspace = getWorkspace();
        details.append(getResourceString("WorkspaceInfo_Location") + workspace.getWorkspaceLocationString() + "\n");

        // Modules loaded        
        details.append(getResourceString("WorkspaceInfo_ModulesLoaded"));
        ModuleName[] moduleNames = workspace.getModuleNames();
        Arrays.sort(moduleNames);
        
        int nModules = moduleNames.length;
        if (nModules > 0) {
            for (int i = 0; i < nModules; i++) {
                details.append("     " + moduleNames[i]);
                
                VaultElementInfo vaultInfo = workspace.getVaultInfo(moduleNames[i]);

                if (vaultInfo != null) {
                    long moduleRevision = vaultInfo.getRevision();

                    String descriptor = vaultInfo.getVaultDescriptor();
                    String locationString = vaultInfo.getLocationString();
                    
                    details.append("     " + descriptor);
                    if (locationString != null) {
                        details.append("(" + locationString + ")");
                    }
                    details.append("  " + GemCutterMessages.getString("WorkspaceInfo_Revision", new Long(moduleRevision)));
                }

                details.append("\n");
                
                String debugInfo = workspace.getDebugInfoForModule(moduleNames[i]);
                
                if (debugInfo != null) {
                    details.append("        - ").append(debugInfo).append("\n");
                }
            }
        } else {            
            details.append(getResourceString("WorkspaceInfo_None"));
        }
        
        DetailsDialog dialog = new DetailsDialog(GemCutter.this, title, message, details.toString(), (Icon)null);
        dialog.setDetailsVisible(true);
        dialog.doModal();
    }

    /**
     * Do the work necessary to carry out a user-initiated action to change the current working module in the GemCutter
     * @param newWorkingModule the name of the new working module.
     */
    void doChangeModuleUserAction(ModuleName newWorkingModule) {
        
        // Prompt the user if the current tabletop needs be to saved
        if (!promptSaveCurrentTableTopIfNonEmpty(null, null)){
            return;
        }
        
        // Undo edits are not needed because creating a new tabletop will clear the undo stack
        
        // Change the module.
        changeModuleAndNewTableTop(newWorkingModule, true);

        // Update the GemCutter window title.
        updateWindowTitle();
    }

    /**
     * Performs the Add Type Declarations refactoring on the module named targetModule.
     * The operation happens on its own thread with a status display.
     * @param targetModule String name of the module to refactor
     */
    void doAddTypeDeclsUserAction(final ModuleName targetModule) {

        Thread refactoringThread = new AbstractThreadWithSimpleModalProgressDialog("type-decl adder thread", this, GemCutterMessages.getString("AddTypedeclsTitle"), 0, 3) {
            public void run() {
                
                Refactorer typeDeclsAdder = new Refactorer.InsertTypeDeclarations(perspective.getWorkspace().asModuleContainer(), targetModule, -1, -1, -1, -1);
                
                showMonitor();
                setStatus(GemCutterMessages.getString("CalculatingModificationsStatus"));
                
                try {
                    ExtendedUndoableEditSupport undoableEditSupport = tableTop.getUndoableEditSupport();
                    undoableEditSupport.beginUpdate();
            
                    CompilerMessageLogger logger = new MessageLogger();
                    typeDeclsAdder.calculateModifications(logger);
                    
                    if(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        showCompilationErrors(logger, getResourceString("ErrorWhileCalculatingModifications"));
                        return;
                    }
                    
                    incrementProgress();
                    setStatus(GemCutterMessages.getString("ApplyingModificationsStatus"));
                    
                    typeDeclsAdder.apply(logger);
                    
                    if(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        showCompilationErrors(logger, getResourceString("ErrorWhileApplyingModifications"));
                        return;
                    }
                    
                    incrementProgress();
                    setStatus(GemCutterMessages.getString("RecompilingStatus"));
    
                    recompileWorkspace(true);
                    
                    if(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        showCompilationErrors(logger, getResourceString("ErrorWhileRecompiling"));
                        return;
                    }
                                        
                    incrementProgress();
                    setStatus(GemCutterMessages.getString("SuccessStatus"));
    
                    UndoableRefactoringEdit addTypeDeclsEdit = new UndoableRefactoringEdit(GemCutter.this, typeDeclsAdder, GemCutterMessages.getString("AddTypedeclsPresentationName"));
                    undoableEditSupport.setEditName(addTypeDeclsEdit.getPresentationName());
                    undoableEditSupport.postEdit(addTypeDeclsEdit);
            
                    // End the update.
                    undoableEditSupport.endUpdate();
                    
                    // Make sure that the dialog is active for long enough to be visible
                    try {
                        sleep(750);
                    } catch(InterruptedException e) {
                         // who cares, really
                    }
                } finally {
                
                    closeMonitor();
                }
            }
        };
        
        refactoringThread.start();    
    }
    
    /**
     * Helper function to show the messages contained in logger, with some explanatory
     * text.
     * @param logger CompilerMessageLogger containing the messages to show
     * @param localizedDescriptionText String to show above the list of messages
     */
    void showCompilationErrors(CompilerMessageLogger logger, String localizedDescriptionText) {
        
        // If this logger contains no warnings/errors, then there's nothing to show
        if(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.WARNING) < 0) {
            return;
        }
        
        final boolean warningsOnly = logger.getMaxSeverity() == CompilerMessage.Severity.WARNING;
        CompilerMessageDialog messageDialog = new CompilerMessageDialog(this, warningsOnly);
        messageDialog.addMessages(logger);
        messageDialog.setDescriptionText(localizedDescriptionText);
        messageDialog.setVisible(true);
    }
    
    /**
     * Performs the Clean Imports refactoring on the module named targetModule.
     * The operation happens on its own thread with a status display.
     * @param targetModule String name of the module to refactor
     */
    void doCleanImportsUserAction(final ModuleName targetModule) {

        Thread refactoringThread = new AbstractThreadWithSimpleModalProgressDialog("import-cleaner thread", this, GemCutterMessages.getString("CleanImportsTitle"), 0, 3) {
            public void run() {
                
                Refactorer importCleaner = new Refactorer.CleanImports(perspective.getWorkspace().asModuleContainer(), targetModule, false);
                
                showMonitor();
                setStatus(GemCutterMessages.getString("CalculatingModificationsStatus"));
                
                try {
                    ExtendedUndoableEditSupport undoableEditSupport = tableTop.getUndoableEditSupport();
                    undoableEditSupport.beginUpdate();
            
                    CompilerMessageLogger logger = new MessageLogger();
                    importCleaner.calculateModifications(logger);
                    
                    if(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        showCompilationErrors(logger, getResourceString("ErrorWhileCalculatingModifications"));
                        return;
                    }
                    
                    incrementProgress();
                    setStatus(GemCutterMessages.getString("ApplyingModificationsStatus"));
                    
                    importCleaner.apply(logger);
                    
                    if(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        showCompilationErrors(logger, getResourceString("ErrorWhileApplyingModifications"));
                        return;
                    }
                    
                    incrementProgress();
                    setStatus(GemCutterMessages.getString("RecompilingStatus"));
    
                    recompileWorkspace(true);
                    
                    if(logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                        closeMonitor();
                        showCompilationErrors(logger, getResourceString("ErrorWhileRecompiling"));
                        return;
                    }
                    
                    incrementProgress();
                    setStatus(GemCutterMessages.getString("SuccessStatus"));
    
                    UndoableRefactoringEdit cleanImportsEdit = new UndoableRefactoringEdit(GemCutter.this, importCleaner, GemCutterMessages.getString("CleanImportsPresentationName"));
                    undoableEditSupport.setEditName(cleanImportsEdit.getPresentationName());
                    undoableEditSupport.postEdit(cleanImportsEdit);
            
                    // End the update.
                    undoableEditSupport.endUpdate();
                    
                    // Make sure that the dialog is active for long enough to be visible
                    try {
                        sleep(750);
                    } catch(InterruptedException e) {
                         // who cares, really
                    }
                } finally {
                
                    closeMonitor();
                }
            }
        };
        
        refactoringThread.start();
    }
    
    /**
     * Change to the given module, and create a new table top
     * @param targetModuleName the module to which to switch.
     * @param ensureVisibleInBrowser ensure the node for the module is visible in the browser.
     */
    void changeModuleAndNewTableTop(final ModuleName targetModuleName, boolean ensureVisibleInBrowser) {
        // Update the perspective
        perspective.setWorkingModule(targetModuleName);
        
        // Update the gem browser.
        getGemBrowser().refresh();
        
        // Update the preferred working module.
        preferredWorkingModuleName = targetModuleName;
        
        // Ensure the module is visible.
        // Note: the new module will be always be selected, disregarding the previous selection state
        if (ensureVisibleInBrowser) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getGemBrowser().getBrowserTree().selectDrawerNode(targetModuleName);
                }
            });
        }
        
        // New tableTop.
        newTableTop();
    }
    
    /**
     * Return the RunMenu property value.
     * @return JMenu
     */
    private JMenu getRunMenu() {
        
        if (runMenu == null) {
            try {
                runMenu = makeNewMenu(null);
                runMenu.setName("RunMenu");
                runMenu.setText(getResourceString("RunMenu"));
                runMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_RUN_MENU);
                
                runMenu.add(getRunMenuItem());
                runMenu.add(getRunSubMenu());
                runMenu.add(makeNewMenuItem(getStopAction()));
                runMenu.add(makeNewMenuItem(getResetAction()));

                runMenu.addMenuListener(new MenuListener() {
                
                    // The listener that the popup is currently using.
                    private RunDropDownMenuListener currentListener;
                
                    public void menuSelected(MenuEvent evt) {
                    
                        JPopupMenu popup = ((JMenu)evt.getSource()).getPopupMenu();
                        RunDropDownMenuListener menuListener = new RunDropDownMenuListener();
                        
                        // Add the new listener
                        popup.addPopupMenuListener(menuListener);
                        runMenuItem.addChangeListener(menuListener);

                        // Add the menu item to the listeners internal menuitem->gem map
                        DisplayedGem displayedGem = getTableTop().getDisplayedGem(currentGemToRun);
                        menuListener.addMenuItem(runMenuItem, displayedGem);
                        
                        // We don't want the default run gem to be focused whenever the menu is shown
                        menuListener.setFocusOnPopup(false);
                    
                        // Remove the old listener
                        popup.removePopupMenuListener(currentListener);
                        runMenuItem.removeChangeListener(currentListener);

                        currentListener = menuListener;
                    }
                
                    public void menuDeselected(MenuEvent evt) {}
                
                    public void menuCanceled(MenuEvent evt) {}
                });
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return runMenu;
    }
    
    /**
     * Returns the run menu item to run the last run gem. Access to this item is
     * important so it can re replaced by the resume menu item while a gem is running.
     * @return JMenu
     */
    private JMenuItem getRunMenuItem() {
        
        if (runMenuItem == null) {
            runMenuItem = makeNewMenuItem (getRunAction());
            runMenuItem.setEnabled(false);
        }
        
        return runMenuItem;
    }
    
    /**
     * Returns the run menu drop down item to run any runnable gem.
     * @return JMenu
     */
    private JMenu getRunSubMenu() {
        
        if (runSubMenu == null) {
            
            runSubMenu = makeNewMenu(getResourceString("RunGemDropDown"));

            // Add a listener that will update the popup menu items whenever the menu is selected
            runSubMenu.addMenuListener(new MenuListener() {
                
                // The listener that the popup is currently using.
                private PopupMenuListener currentListener;
                
                public void menuSelected(MenuEvent evt) {
                    
                    JMenu menu = (JMenu) evt.getSource();
                    JPopupMenu popup = menu.getPopupMenu();
                    
                    // Remove the old listener
                    popup.removePopupMenuListener(currentListener);
                    
                    // Update the menu's popup menu with the latest list of menu items.
                    currentListener = prepareRunDropDownMenu(popup);
                }
                
                public void menuDeselected(MenuEvent evt) {
                }
                
                public void menuCanceled(MenuEvent evt) {
                }
            });
        }
        
        return runSubMenu;
    }
    
    /**
     * Returns the run button property value.
     * It is important to have access to this button because its location is necessary in displaying
     * the associated popup menu in the correct location.
     * @return JButton
     */
    private JButton getRunButton() {
        if (runButton == null) {
            try {
                // Build the button from the Run action
                runButton = makeNewButton(getRunAction());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return runButton;
    }

    /**
     * Returns the Play drop down button property value.
     * It is important to have access to this button to enable/disable it if Gems can be run.
     */
    private JButton getRunDropDownButton() {
        if (runDropDownButton == null) {
            try {
                runDropDownButton = makeNewButton(getRunDropDownAction());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return runDropDownButton;
    }
        
    /**
     * Return the action that handles running a gem.
     * @return Action
     */
    private Action getRunAction() {
        
        if (runAction == null) {
            try {
                runAction = new AbstractAction (getResourceString("RunGem"), 
                                            new ImageIcon(getClass().getResource("/Resources/play.gif"))) {
                                                            
                    private static final long serialVersionUID = 5000876884559109717L;

                    public void actionPerformed(ActionEvent evt) {
                        DisplayedGem displayedGem = getTableTop().getDisplayedGem(currentGemToRun);
                        if (displayedGem != null) {
                            runTarget(displayedGem);
                        } else {
                            displayRunDropDownMenu();
                        }
                    }
                };
                
                runAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_RUN));
                runAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_RUN);
                runAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("RunGemToolTip"));
                runAction.putValue(ACTION_BUTTON_IS_DROP_PARENT_KEY, Boolean.TRUE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return runAction;
    }
    
    /**
     * Return the action that handles popping up the run gem menu when the
     * run gem drop down button is clicked.
     *
     */
    private Action getRunDropDownAction() {
        
        if (runDropDownAction == null) {
            try {
                runDropDownAction = new AbstractAction (getResourceString("RunGemDropDown"), 
                                            new ImageIcon(getClass().getResource("/Resources/dropdownarrow.gif"))) {
                                                            
                    private static final long serialVersionUID = 4602841745380963356L;

                    public void actionPerformed(ActionEvent evt) {
                        displayRunDropDownMenu();
                    }
                };
                
                runDropDownAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("RunGemDropDownToolTip"));
                runDropDownAction.putValue(ACTION_BUTTON_IS_DROP_CHILD_KEY, Boolean.TRUE);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return runDropDownAction;
    }
    
    /**
     * Displays the pop up menu listing all roots when the play button is pressed.
     * Names of Collector Gems appear first in alphabetical order.  Names of anonymous gems come
     * second in alphabetical order (multiple occurrences of names will have an ordinal appended to 
     * the name - assignment of ordinal is arbitrary).  ValueGems appear third.
     */
    public void displayRunDropDownMenu() {

        // Build a new popup menu and remember it in case we need to close it later.
        runDropDownMenu = new JPopupMenu();
        
        prepareRunDropDownMenu(runDropDownMenu);

        // Show the popup directly underneath the button, left-aligned with it
        Rectangle bounds = getRunDropDownButton().getBounds();
        runDropDownMenu.show(getToolBarPane(), bounds.x, bounds.y + bounds.height);
    }
    
    /**
     * Prepares the run button popup menu for display.  Appropriate listeners and menu items
     * are added to the menu.  
     * @param menu JPopupMenu - the popup menu that needs to be prepared
     * @return PopupMenuListener
     */
    private PopupMenuListener prepareRunDropDownMenu(JPopupMenu menu) {

        // Get a list of all roots and get the list of menu items for the roots
        // getGemMenuItems() will sort rootList so that the order matches the ordering of the menu items
        List<Gem> rootList = new ArrayList<Gem>(getTableTop().getGemGraph().getRoots());
        List<JComponent> menuItems = getGemMenuItems(rootList);
        
        // Remove any existing menu items
        menu.removeAll();

        // Create the listener to listen for menu or menu item events and add it to the popup menu
        RunDropDownMenuListener menuListener = new RunDropDownMenuListener();
        menu.addPopupMenuListener(menuListener);

        if (rootList.isEmpty()) {
            // There will be a menu item indicating that there are no gems to run
            menu.add((JMenuItem)menuItems.get(0));
            
        } else {
            // Now loop thru the menu items and set up the menu listener with each one
            Iterator<Gem> rootIterator = rootList.iterator();
            
            for (final JComponent component : menuItems) {
                // There may be some JSeperators in the list of menu items so watch out!

                if (component instanceof JMenuItem) {
                    
                    Gem gem = rootIterator.next();

                    if (!gem.isRunnable()) {
                        component.setEnabled(false);
                    }

                    DisplayedGem dGem = getTableTop().getDisplayedGem(gem);
                    menuListener.addMenuItem((JMenuItem)component, dGem);
                    ((JMenuItem)component).addActionListener(menuListener);
                    ((JMenuItem)component).addChangeListener(menuListener);
                }
                
                menu.add(component);
            }
        }
        
        return menuListener;
    }

    /**
     * Returns a list of menu items (and separators) for the Gem names.  Collectors will be first,
     * FunctionalAgents and CodeGems will be second, and Values will be third.  Each section will 
     * be in alphabetical order and separated by a JSeparator.  Names that occur more than once 
     * will have an ordinal appended after the name. 
     * CAUTION: A JSeparator will be used to separate the different sections of the list.
     * @param gemList the list of gems that need menu items.  
     *   This list will will be sorted to match the order of the menu items.
     * @return List list of menu items (and separators) for the supplied gems
     */
    private List<JComponent> getGemMenuItems(List<Gem> gemList) {
        
        int numGems = gemList.size();
        ArrayList<JComponent> menuItems = new ArrayList<JComponent>();
            
        // if there are no gems then return a menu item labeled "none"
        if (numGems == 0) {
            JMenuItem emptyItem = new JMenuItem(getResourceString("EmptyListLabel"));
            emptyItem.setEnabled(false);
            menuItems.add(emptyItem);
            return menuItems;
        }
        
        // split the gemList into 3 lists - collectorGems, other named gems, valueGems
        List<Gem> collectorGems = new ArrayList<Gem>();
        List<Gem> otherNamedGems = new ArrayList<Gem>();
        List<Gem> valueGems = new ArrayList<Gem>();
        for (final Gem gem : gemList) {
            if (gem instanceof CollectorGem) {
                collectorGems.add(gem);
            } else if (gem instanceof FunctionalAgentGem || gem instanceof CodeGem || gem instanceof ReflectorGem || gem instanceof RecordFieldSelectionGem || gem instanceof RecordCreationGem) {
                otherNamedGems.add(gem);
            } else if (gem instanceof ValueGem) {
                valueGems.add(gem);
            } else {
                throw new IllegalArgumentException("TableTop.getGemMenuItems: gem not a Collector, FunctionalAgent, Emitter, Code, or Value");
            }
        }
        
        // clear the gemList and rebuild it with gems in the same order as the menu items
        gemList.clear();
        
        // sort collectors and add menu items for them
        if (!collectorGems.isEmpty()) {
            collectorGems = GemGraph.sortNamedGemsInAlphabeticalOrder(collectorGems);
            
            CollectorGem targetCollector = tableTop.getTargetCollector();
            
            for (final Gem gem : collectorGems) {
                CollectorGem collectorGem = (CollectorGem)gem;
                JMenuItem menuItem = new JMenuItem((collectorGem).getUnqualifiedName());
                
                if (collectorGem == tableTop.getTargetCollector()) {
                    // Set an icon.
                    menuItem.setIcon(new ImageIcon(getClass().getResource("/Resources/targetCollector.gif")));
                    
                    // add to the beginning.
                    menuItems.add(0, UIUtilities.fixMenuItem(menuItem));

                } else {
                    menuItems.add(UIUtilities.fixMenuItem(menuItem));
                }
            }
            
            // add the collectorGems to the empty gemList.  Recall that the target collector should come first.
            gemList.addAll(collectorGems);
            if (gemList.remove(targetCollector)) {
                gemList.add(0, targetCollector);
            }
            
        }
        
        // sort the functional agent and code gems and add menu items for them
        int numOtherNamedGems = otherNamedGems.size();
        if (numOtherNamedGems > 0) {
            // if there are any items in gemList then put a separator in the pop up menu
            if (gemList.size() > 0) {
                menuItems.add(new JSeparator());
            }
            
            // sort the other named gems into alphabetical order
            otherNamedGems = GemGraph.sortNamedGemsInAlphabeticalOrder(otherNamedGems);
            
            // set up some variables for looping thru the named gems
            String currName = ((NamedGem)otherNamedGems.get(0)).getUnqualifiedName();
            String nextName, addName;
            int ordinal = 0;
            for (int i = 0; i < numOtherNamedGems; i++){
    
                // the last gem name must be compared against a nextName of NULL so take
                // care of this here
                if (i < (numOtherNamedGems - 1)) {
                    nextName = ((NamedGem)otherNamedGems.get(i+1)).getUnqualifiedName();
                } else {
                    nextName = null;
                }
        
                if (currName.equals(nextName)) {
                    // current and next names are the same so increment ordinal and set addName
                    ordinal++;
                    addName = currName + " (" + ordinal + ")";
                } else {
                    // current and next names are different so set addName and clear ordinal
                    if (ordinal == 0) {
                        addName = currName;
                    } else {
                        ordinal++;
                        addName = currName + " (" + ordinal + ")";
                    }
            
                    ordinal = 0;
                }
            
                // set the currName equal to nextName and make a menu item for addName
                currName = nextName;
                menuItems.add(UIUtilities.fixMenuItem(new JMenuItem(addName)));
            }
    
            // add the named gems to gemList
            gemList.addAll(otherNamedGems);
        }
        
        // make menu items for the value gems
        if (!valueGems.isEmpty()) {
            // if there are any items in gemList then put a separator in the pop up menu
            if (gemList.size() > 0) {
                menuItems.add(new JSeparator());
            }
            
            int numValues = valueGems.size();
            for (int i = 1; i <= numValues; i++) {
                menuItems.add(UIUtilities.fixMenuItem(new JMenuItem(getResourceString("ValueLabel") + " (" + i + ")")));
            }
            
            // add the valueGems to gemList
            gemList.addAll(valueGems);
        }
        
        // return the menu items
        return menuItems;
    }
    
    /**
     * todoSN - hopefully this is only temporary so that the Run menu item is shown
     *          in edit mode and the Resume menu item is shown in run mode.
     * 
     * Returns the Resume menu item.
     * @return JMenuItem
     */
    private JMenuItem getResumeMenuItem() {
        
        if (resumeMenuItem == null) {
            resumeMenuItem = makeNewMenuItem(getResumeRunAction());
        }
        return resumeMenuItem;
    }
    
    /**
     * todoSN - hopefully this is only temporary so that the Run menu item is shown
     *          in edit mode and the Resume menu item is shown in run mode.
     * 
     * Returns the Resume button.
     * @return JButton
     */
    private JButton getResumeRunButton() {
        
        if (resumeRunButton == null) {
            resumeRunButton = makeNewButton(getResumeRunAction());
        }
        return resumeRunButton;
    }
    
    /**
     * Returns the action that resumes running after values have been entered for arguments in run mode.
     * @return Action
     */
    Action getResumeRunAction() {
        
        if (resumeRunAction == null) {
            try {
                resumeRunAction = new AbstractAction(getResourceString("ResumeRunGem"), 
                                                new ImageIcon(getClass().getResource("/Resources/play.gif"))) {
                                                            
                    private static final long serialVersionUID = -4069447251180282590L;

                    public void actionPerformed(ActionEvent evt) {
                        // Disable the reset action after resume
                        getResetAction().setEnabled(false);
                        progressBar.setIndeterminate(true);
                        getStatusBarPane().add(progressBar, "East");
                        getStatusBarPane().revalidate();
                        runTarget(null);                        
                        getStatusMessageDisplayer().setMessageFromResource(this, "SM_Executing", StatusMessageDisplayer.MessageType.DEFERENTIAL);
                    }
                };
                
                resumeRunAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_RESUME));
                resumeRunAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_RESUME);
                resumeRunAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ResumeRunGemToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return resumeRunAction;
    }

    /**
     * Return the action for the Stop button and menu item
     * @return Action
     */
    Action getStopAction() {
        
        if (stopAction == null) {
            try {
                stopAction = new AbstractAction(getResourceString("StopRunGem"), 
                                                new ImageIcon(getClass().getResource("/Resources/stop.gif"))) {
                                                            
                    private static final long serialVersionUID = -5572265564782672623L;

                    public void actionPerformed(ActionEvent evt) {
                        stopExecution();
                    }
                };
                
                stopAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_STOP));
                stopAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_STOP);
                stopAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("StopRunGemToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return stopAction;
    }

    /**
     * Terminate an execution.
     */
    private void stopExecution() {
        // Notify the execute lock to perform a quit
        displayedGemRunner.stopExecution();
    }

    /**
     * Return the action for reseting the argument values when in run mode.
     * @return Action
     */
    Action getResetAction() {
        
        if (resetAction == null) {
            try {
                resetAction = new AbstractAction (getResourceString("ResetArgs"), 
                                                new ImageIcon(getClass().getResource("/Resources/reset.gif"))) {
                                                            
                    private static final long serialVersionUID = 8274405610098956489L;

                    public void actionPerformed(ActionEvent evt) {
                        displayedGemRunner.resetArgumentValues();
                        argumentExplorer.resetArgumentValues();
                    }
                };
                
                resetAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_RESET));
                resetAction.putValue(Action.ACCELERATOR_KEY, GemCutterActionKeys.ACCELERATOR_RESET);
                resetAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("ResetArgsToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
            resetAction.setEnabled(false);
        }
        return resetAction;
    }

    /**
     * Return the HelpMenu property value.
     * @return JMenu
     */
    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            try {
                helpMenu = new JMenu();
                helpMenu.setName("HelpMenu");
                helpMenu.setText(getResourceString("HelpMenu"));
                helpMenu.setMargin(new Insets(2, 0, 2, 0));
                helpMenu.setMnemonic(GemCutterActionKeys.MNEMONIC_HELP_MENU);
                
                helpMenu.add(makeNewMenuItem(getHelpTopicsAction()));
                helpMenu.add(makeNewMenuItem(getAboutBoxAction()));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return helpMenu;
    }
    
    /**
     * Return the action that handles the Help Topics.
     * @return Action
     */
    private Action getHelpTopicsAction() {
        
        if (helpTopicsAction == null) {
            try {
                helpTopicsAction = new AbstractAction (getResourceString("HelpTopics")) {
                                                            
                    private static final long serialVersionUID = 2196764468259315329L;

                    public void actionPerformed(ActionEvent evt) {
                        
                        // Pass the event off to the help system launcher
                        getHelpSystemLauncher().actionPerformed(evt);
                    }
                };
                
                helpTopicsAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_HELP_TOPICS));
                helpTopicsAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("HelpTopicsToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return helpTopicsAction;
    }
    
    /**
     * Get the action listener that launches the online help system.
     * @return ActionListener
     */
    private ActionListener getHelpSystemLauncher() {
        
        if (helpSystemLauncher == null) {
            try {
                // Load the help set and get a help broker for it.
                java.net.URL helpSetURL = GemCutter.class.getResource("/JavaHelp/gemcutterhelp.hs");
                javax.help.HelpSet helpSet = new javax.help.HelpSet(null, helpSetURL);
                javax.help.HelpBroker helpBroker = helpSet.createHelpBroker();

                helpSystemLauncher = new javax.help.CSH.DisplayHelpFromSource(helpBroker);
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return helpSystemLauncher;
    }
    
    /**
     * Return the action that opens the "About" dialog.
     * @return Action
     */
    private Action getAboutBoxAction() {
        
        if (aboutBoxAction == null) {
            try {
                aboutBoxAction = new AbstractAction (getResourceString("AboutBox")) {
                                                            
                    private static final long serialVersionUID = -6456685392775005139L;

                    public void actionPerformed(ActionEvent evt) {
                        showAboutBox();
                    }
                };
                
                aboutBoxAction.putValue(Action.MNEMONIC_KEY, new Integer(GemCutterActionKeys.MNEMONIC_ABOUT_BOX));
                aboutBoxAction.putValue(Action.SHORT_DESCRIPTION, getResourceString("AboutBoxToolTip"));
                
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return aboutBoxAction;
    }
    
    public void showAboutBox() {
        /* Create the AboutBox dialog */
        GemCutterAboutBox aGemCutterAboutBox = new GemCutterAboutBox(this, true);
        Dimension dialogSize = aGemCutterAboutBox.getPreferredSize();
        Dimension frameSize = getSize();
        Point loc = getLocation();
        aGemCutterAboutBox.setLocation((frameSize.width - dialogSize.width) / 2 + loc.x, (frameSize.height - dialogSize.height) / 2 + loc.y);
        aGemCutterAboutBox.setVisible(true);
    }

    /**
     * Creates a new JButton and configures it for use in the GemCutter tool bar.
     * @param action Action - the action used to configure the JButton
     * @return JButton
     */
    private JButton makeNewButton(Action action) {
        
        JButton newButton = new JButton();
        
        // Specify some of the customized characteristics here.
        newButton.setAction(action);
        newButton.setText(null);
        newButton.setMargin(new Insets(0, 0, 0, 0));
        
        // Clear the mnemonic so that the buttons are activated by them.
        newButton.setMnemonic(KeyEvent.KEY_LOCATION_UNKNOWN);
        
        /* This takes care of making the drop down buttons look
         * like one button by raising the border of both of them at
         * the same time.
         */
        newButton.addMouseListener(new ToolBarButtonMouseListener (newButton));
        
        return newButton;
    }

    /**
     * Creates a new JCheckBoxMenuItem and configures it for use in the GemCutter menu bar.
     * @param action Action - the action used to configure the JCheckBoxMenuItem
     * @return JCheckBoxMenuItem
     */
    static private JCheckBoxMenuItem makeNewCheckBoxMenuItem(Action action) {
        return (JCheckBoxMenuItem)UIUtilities.fixMenuItem(new JCheckBoxMenuItem(action));
    }
    
    /**
     * Creates a new JMenuItem and configures it for use in the GemCutter menu bar.
     * @param action Action - the action used to configure the JMenuItem
     * @return JMenuItem
     */
    static JMenuItem makeNewMenuItem(Action action) {
        return UIUtilities.fixMenuItem(new JMenuItem(action));
    }
    
    /**
     * This method should only be called from the AWT thread.
     * @return true if a search is currently being performed, or false otherwise.
     */
    boolean isSearchPending() {
        if(searchDialog == null) {
            return false;
        }
        
        return searchDialog.isSearchPending();
    }
    
    /**
     * If no search dialog is currently being displayed, then create one and pop it up
     * @return The SearchDialog object that is being displayed
     */
    SearchDialog showSearchDialog() {
        
        if(searchDialog == null) {
            searchDialog = new SearchDialog(GemCutter.this, perspective);
            
            if(searchDialog.hasSavedPosition()) {
                searchDialog.setPositionToSaved();
            } else {
                searchDialog.setLocation(getX(), getY());
            }
            searchDialog.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    forgetSearchDialog();
                }
            });
            
            searchDialog.setVisible(true);
        }
        
        return searchDialog;
    }

    /**
     * Called by the searchDialog to signal that it has been closed.
     */
    private void forgetSearchDialog() {
        if (searchDialog != null) {
            searchDialog.savePosition();
            searchDialog = null;
        }
    }
    
    /**
     * Creates a new JMenu and configures it for use in the GemCutter menu bar.
     * @param text the label of the menu item
     * @return JMenu
     */
    static JMenu makeNewMenu(String text) {
        return (JMenu)UIUtilities.fixMenuItem(new JMenu(text));
    }

    /**
     * Given the name of a resource, return the associated string.
     * @param resourceName String the name of the resource
     * @return the associated string.
     */
    public static String getResourceString(String resourceName) {
        return GemCutterMessages.getString(resourceName);
    }
    
    /**
     * Given the name of a resource, return the associated string.
     * @param resourceName String the name of the resource
     * @param resourceArgument the argument to the resource string.
     * @return the associated string.
     */
    public static String getResourceString(String resourceName, Object resourceArgument) {
        return GemCutterMessages.getString(resourceName, resourceArgument);
    }

    /**
     * Gets the appropriate cursor given the gem
     * @param gemToAdd
     * @return Cursor
     */
    static Cursor getCursorForAddGem(Gem gemToAdd) {
        if (gemToAdd instanceof ValueGem) {
            return addValueGemCursor;
            
        } else if (gemToAdd instanceof CodeGem) {
            return addCodeGemCursor;
    
        } else if (gemToAdd instanceof CollectorGem) {
            return addCollectorGemCursor;
    
        } else if (gemToAdd instanceof ReflectorGem) {
            if (gemToAdd.getNInputs() == 0) {
                return addOvalReflectorGemCursor;
            } else {
                return addTriangularReflectorGemCursor;
            }
        
        } else if (gemToAdd instanceof RecordFieldSelectionGem) {
            return addRecordFieldSelectionGemCursor;    
        
        } else if (gemToAdd instanceof RecordCreationGem) {
            return addRecordCreationGemCursor;    
            
        } else if (gemToAdd instanceof FunctionalAgentGem) {
            return addFunctionGemCursor;

        } else {
            // The default cursor (?)
            return addFunctionGemCursor;
        }
    }

    /**
     * Returns a reference to the clipboard used to store the gems that
     * are cut/copied on the tabletop
     * @return clipBoard
     */
    Clipboard getClipboard() {
        if (clipboard == null) { 
            clipboard = new Clipboard("GemCutter");
        }
        return clipboard;
    }
    
    /**
     * Return the OverviewPanel property value.
     * @return OverviewPanel
     */
    private TableTopExplorerAdapter getTableTopExplorerAdapter() {
        if (tableTopExplorerAdapter == null) {
            try {
                tableTopExplorerAdapter = new TableTopExplorerAdapter(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return tableTopExplorerAdapter;
    }
    
    /**
     * Return the OverviewPanel property value.
     * @return OverviewPanel
     */
    TableTopExplorer getTableTopExplorer() {
        return getTableTopExplorerAdapter().getTableTopExplorer();
    }

    /**
     * @return the intellicut manager for the gem cutter.
     */
    IntellicutManager getIntellicutManager() {
        return intellicutManager;
    }

    /**
     * Returns whether or not the output should be in debug mode (string output) or
     * use the value entry mechanism.
     * @return boolean
     */
    public boolean isDebugOutputMode() {
        return ((Boolean)getDebugOutputAction().getValue("InDebugOutputMode")).booleanValue();
    }
    
    /**
     * Returns whether or not we should allow renaming of entities in the Prelude module.
     * @return boolean
     */
    public boolean isAllowPreludeRenamingMode() {
        return ((Boolean)getAllowPreludeRenamingAction().getValue("InAllowRenamingMode")).booleanValue();
    }

    /**
     * Returns whether or not we should allow renaming of entities to names that already exist
     * @return boolean
     */
    public boolean isAllowDuplicateRenamingMode() {
        return ((Boolean)getAllowDuplicateRenamingAction().getValue("InAllowDuplicateRenamingMode")).booleanValue();
    }

    /**
     * Handle necessary effects of GUI state changes.
     * @param newGUIstate the new GUI state to change to
     */
    public void enterGUIState(GUIState newGUIstate) {

        if (guiState == newGUIstate) {
            // Nothing to do
            return;
        }
    
        // Firstly, handle things we always do when leaving the old state.
        
        if (guiState == GUIState.ADD_GEM) {
                
            // Not adding a gem anymore, so enable the edit controls.
                
            // Reset the cursor and the status message.
            setCursor(null);
            getGlassPane().setCursor(null);
            setStatusFlag(null);
            statusMessageManager.clearMessage(this);

            // Enable drag and drop from the browser and hide the glass pane.
            getBrowserTree().setEnabledDragAndDrop(true);
            getGlassPane().setVisible(false);
            
            // Loop through any open code editors and reset their cursors and hide their glass panes.
            for (final DisplayedGem dGem : getTableTop().getDisplayedGems()) {
                Gem gem = dGem.getGem();
                
                if (gem instanceof CodeGem && getTableTop().isCodeEditorVisible((CodeGem)gem)) {
                    CodeGemEditor editor = getTableTop().getCodeGemEditor((CodeGem)gem);
                    editor.setCursor(null);
                    editor.getGlassPane().setVisible(false);
                }
            }       
            
    
        } else if (guiState == GUIState.RUN) {
            
            // Not running anymore, so re-enable edit controls.
            enableEditActions(true);

            getTableTop().checkSelectionButtons();

            // Update run controls.
            setTargetRunningButtons(false);
            getResetAction().setEnabled(false);
            getTableTop().setRunning(false);

            // Indicate that we're not running anymore.
            setStatusFlag(null);
            statusBarPane.remove(progressBar);
            statusMessageManager.clearMessage(getDisplayedGemRunner());
            statusMessageManager.clearMessage(this);
            
        } else if (guiState == GUIState.LOCKED_DOWN) {

            // Not locked down anymore, so re-enable all controls.
            enableEditActions(true);
            getRunAction().setEnabled(true);            

            getTableTop().checkSelectionButtons();
        }
    
        // Now handle things we always do when entering a specific state.
        
        if (newGUIstate == GUIState.ADD_GEM) {
            
            // Adding a new gem. Determine the correct cursor to use and disable edit controls.
            Gem gemToAdd = displayedGemToAdd == null ? null : displayedGemToAdd.getGem();
        
            if (gemToAdd instanceof ValueGem) {
                statusMessageManager.setMessageFromResource(this, "SM_AddValueGem", StatusMessageDisplayer.MessageType.PERSISTENT);

            } else if (gemToAdd instanceof CodeGem) {
                statusMessageManager.setMessageFromResource(this, "SM_AddCodeGem", StatusMessageDisplayer.MessageType.PERSISTENT);
                
            } else if (gemToAdd instanceof CollectorGem) {
                statusMessageManager.setMessageFromResource(this, "SM_AddCollectorGem", StatusMessageDisplayer.MessageType.PERSISTENT);
                
            } else if (gemToAdd instanceof ReflectorGem) {
                String name = ((ReflectorGem)gemToAdd).getUnqualifiedName();
                statusMessageManager.setMessageFromResource(this, "SM_AddReflectorGem", name + ".", StatusMessageDisplayer.MessageType.PERSISTENT);
 
            } else {
                statusMessageManager.setMessageFromResource(this, "SM_AddFunctionGem", StatusMessageDisplayer.MessageType.PERSISTENT);
            }
            
            Cursor cursor = getCursorForAddGem(gemToAdd);
            setCursor(cursor);
            getGlassPane().setCursor(cursor);

            // Disable drag and drop from the gem browser and turn on the glass pane so 
            // that we can cancel "add gem" mode whenever the user clicks outside the TableTop.
            getBrowserTree().setEnabledDragAndDrop(false);
            getGlassPane().setVisible(true);

            // Loop through any open code editors and make their glass panes visible.
            // This is so the 'add gem' cursor works everywhere.
            for (final DisplayedGem dGem : getTableTop().getDisplayedGems()) {
                Gem gem = dGem.getGem();
                
                if (gem instanceof CodeGem && getTableTop().isCodeEditorVisible((CodeGem)gem)) {
                    CodeGemEditor editor = getTableTop().getCodeGemEditor((CodeGem)dGem.getGem());
                    editor.setCursor(cursor);
                    editor.getGlassPane().setVisible(true);
                }
            }                

            // Let the user know we are adding a gem.
            setStatusFlag(getResourceString("AddGem"));

        } else if (newGUIstate == GUIState.RUN) {

            // Disable the editing controls and enable the run controls.
            enableEditActions(false);
            getTableTop().setRunning(true);

            setStatusFlag(getResourceString("RunningFlag"));

        } else if (newGUIstate == GUIState.LOCKED_DOWN) {
            
            // Disable all controls if we are locked down.
            enableEditActions(false);
            getRunAction().setEnabled(false);            
        }
    
        // Set the new state
        guiState = newGUIstate;
    }

    /**
     * Enable or disable GemCutter edit actions.
     * @param enable true to enable, false to disable.
     */
    private void enableEditActions(boolean enable) {
        getNewAction().setEnabled(enable);
        getOpenAction().setEnabled(enable);
        getSaveGemAction().setEnabled(enable);
        getSelectAllAction().setEnabled(enable);
        getArrangeGraphAction().setEnabled(enable);                                               
        getFitTableTopAction().setEnabled(enable);
        getAddGemAction().setEnabled(enable);
        getAddValueGemAction().setEnabled(enable);
        getAddCodeGemAction().setEnabled(enable);
        getAddRecordCreationGemAction().setEnabled(enable);
        getAddRecordFieldSelectionGemAction().setEnabled(enable);
        getAddCollectorGemAction().setEnabled(enable);
        getAddReflectorGemAction().setEnabled(enable);
        getAddReflectorGemDropDownAction().setEnabled(enable);
        getRemoveModuleAction().setEnabled(enable);
        getSwitchWorkspaceAction().setEnabled(enable);
        getDeployWorkspaceToEnterpriseAction().setEnabled(enable);
        getRecompileAction().setEnabled(enable);
        getCompileModifiedAction().setEnabled(enable);
        getDeleteAction().setEnabled(enable);

        if (enable) {
            if (extendedUndoManager.canUndo()) {
                getUndoAction().setEnabled(true);
                getUndoDropDownAction().setEnabled(true);
            }
            
            if (extendedUndoManager.canRedo()) {
                getRedoAction().setEnabled(true);
                getRedoDropDownAction().setEnabled(true);
            }

        } else {
            getUndoAction().setEnabled(false);
            getUndoDropDownAction().setEnabled(false);
            getRedoAction().setEnabled(false);
            getRedoDropDownAction().setEnabled(false);
        }
        
        if (enable) {
            getPasteAction().setEnabled(getTableTop().canPasteToGemGraph(getClipboard().getContents(this)));
            getTableTop().checkSelectionButtons();
            getCopySpecialMenu().setEnabled(true);
            getCopySpecialImageAction().setEnabled(true);
            getCopySpecialTargetSourceAction().setEnabled(true);
            
        } else {
            getCutAction().setEnabled(false);
            getCopyAction().setEnabled(false);
            getPasteAction().setEnabled(false);
            getCopySpecialMenu().setEnabled(false);
            getCopySpecialImageAction().setEnabled(false);
            getCopySpecialTextAction().setEnabled(false);
            getCopySpecialTargetSourceAction().setEnabled(false);
        }
        
        Component[] menuItems = generateMenu.getMenuComponents();
        for (final Component menuItem : menuItems) {
            menuItem.setEnabled(enable);
        }

        getBrowserTree().setEnabledDragAndDrop(enable);
        getTableTopExplorer().enableMouseInputs(enable);
        getArgumentExplorer().enableMouseInputs(enable);
        getSearchAction().setEnabled(enable);
    }
    
    /**
     * Get the current GUI State.
     * @return GUIState the current state
     */
    public GUIState getGUIState() {
        return guiState;
    }

    /** 
     * true if we are using photolook, false otherwise
     * @return boolean
     */
    boolean isPhotoLook() {
        return (backgroundImage != null);
    }
    
    /** 
     * Resets the current background to what is stored in the preferences value.
     */
    void resetBackground() {
        
        String fileName = getPreferences().get(BACKGROUND_FILE_NAME_PREF_KEY, backgrounds.get(0).snd());
        
        try {
            URL url = GemCutter.class.getResource(fileName);
            if (url != null) {
                backgroundImage = ImageIO.read(url);
            } else {
                backgroundImage = null;
            }
        } catch (IOException e) {
            backgroundImage = null;
        }

        getTableTopPanel().setBackground(backgroundImage);
        getTableTopExplorer().getExplorerTree().setBackgroundImage(backgroundImage);
        getOverviewPanel().repaint();
    }
    
    /**
     * Returns the displayed Gem (Value, Code, etc.) that is being added.
     * @return DisplayedGem the (displayed) gem being added
     */
    DisplayedGem getAddingDisplayedGem() {
        return displayedGemToAdd;    
    }

    /**
     * Set the DisplayedGem (Value, Code, etc.) that is being added.
     * @param newAddingGem DisplayedGem
     */
    void setAddingDisplayedGem(DisplayedGem newAddingGem) {
        // update the adding gem
        displayedGemToAdd = newAddingGem;
    }

    /**
     * Get the GemCutter's instance of the CALCompiler.
     * @return CALCompiler the GemCutter's instance of the CALCompiler.
     */
    Compiler getCompiler() {
        return workspaceManager.getCompiler();
    }

    /**
     * Get the TypeChecker from the GemCutter's compiler.
     * @return TypeChecker the type checker
     */ 
    public TypeChecker getTypeChecker() {
        return workspaceManager.getTypeChecker();
    }

    /**
     * Get TypeCheckInfo for the GemCutter.
     * @return TypeCheckInfo the typecheck info for the current state of the tabletop
     */
    TypeCheckInfo getTypeCheckInfo() {
        return getTypeChecker().getTypeCheckInfo(getWorkingModuleName());
    }
    
    /**
     * Get the TypeColourManager in use in this GemCutter.
     * @return TypeColourManager the colour manager
     */ 
    public TypeColourManager getTypeColourManager() {
        return typeColours;
    }
    
    /**
     * @return the current workspace manager.
     */
    public WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }

    /**
     * Get the current workspace.
     * @return the current workspace
     */
    public CALWorkspace getWorkspace() {
        return workspaceManager.getWorkspace();
    }

    /**
     * Get the current GemCutter perspective.
     * @return Perspective the current program.
     */
    public Perspective getPerspective() {
        return perspective;
    }
    
    /**
     * @return the code analyzer for code gems in the GemCutter.
     */
    CodeAnalyser getCodeGemAnalyser() {
        return new CodeAnalyser(getTypeChecker(), getPerspective().getWorkingModuleTypeInfo(), true, false);
    }
    
    /**
     * @return the GemCutter's connection context
     */
    public ConnectionContext getConnectionContext() {
        return new ConnectionContext(getPerspective().getWorkingModuleTypeInfo(), getValueEditorManager());
    }
    
    /**
     * Get the name of the current working module.
     * @return the name of the current working module, or null if there is no current working module.
     */
    public ModuleName getWorkingModuleName() {
        if (perspective == null) {
            return null;
        }
        return perspective.getWorkingModuleName();
    }
 
    /**
     * Get the name of the initial working module.
     * @param workspace the workspace from which the module should be obtained.
     * @return the name of the initial working module or null if there are no valid modules
     */
    private ModuleName getInitialWorkingModuleName(CALWorkspace workspace) {

        // First check whether a system property has been set for this.
        ModuleName workingModuleName = ModuleName.maybeMake(System.getProperty(GEMCUTTER_PROP_MODULE));
        if (workingModuleName == null || workspace.getMetaModule(workingModuleName) == null) {
            workingModuleName = null;
            
            // For now, return the name of the last metamodule in the program that starts with "GemCutter".
            int nModules = workspace.getNMetaModules();
            for (int i = nModules - 1; i > -1; i--) {
                ModuleName moduleName = workspace.getNthMetaModule(i).getName();
                if (moduleName.toSourceText().startsWith("GemCutter")) {
                    workingModuleName = moduleName;
                    break;
                }
            }
        }
        
        if (workingModuleName == null) {
            int nModules = workspace.getNMetaModules();
            
            // Pick the module with the most imports.
            // In the event of a tie, pick the later one, as it will tend to depend on earlier ones.
            int maxImportedModules = 0;
            for (int i = 0; i < nModules; i++) {
                MetaModule nthMetaModule = workspace.getNthMetaModule(i);
                int nImportedModules = nthMetaModule.getNImportedModules();
                
                if (nImportedModules >= maxImportedModules) {
                    maxImportedModules = nImportedModules;
                    workingModuleName = nthMetaModule.getName();
                }
            }
        }
        
        return workingModuleName;            
    }
    
    /**
     * Set the preferred working module, assuming that the GemCutter has just started up, but the current module has already been set.
     */
    private void setInitialPreferredWorkingModuleName() {
        ModuleName moduleProp = ModuleName.maybeMake(System.getProperty(GEMCUTTER_PROP_MODULE));
        if (moduleProp != null) {
            preferredWorkingModuleName = moduleProp;
        } else {
            preferredWorkingModuleName = getWorkingModuleName();
        }
    }

    /**
     * Get the CALRunner for displayed gems used by this instance of the GemCutter.
     * @return DisplayedGemRunner the displayed gem runner
     */
    DisplayedGemRunner getDisplayedGemRunner() {
        return displayedGemRunner;
    }

    /**
     * Returns the ValueRunner used for instantiating value nodes..
     * @return ValueRunner
     */
    ValueRunner getValueRunner() {
        return valueRunner;
    }
    
    /**
     * Returns the ValueEditorManager
     * @return ValueEditorManager
     */
    ValueEditorManager getValueEditorManager() {
        return valueEditorManager;
    }
    
    /**
     * Returns the ValueEditorHierarchyManager responsible for maintaining the main ValueEditorHiearchy (notably that of the TableTop
     * @return ValueEditorHierarchyManager
     */
    ValueEditorHierarchyManager getValueEditorHierarchyManager() {
        return tableTopEditorHierarchyManager;
    }
    
    NavigatorAdapter getNavigatorOwner() {
        return navigatorOwner;
    }
    
    static Preferences getPreferences() {
        return Preferences.userNodeForPackage(GemCutter.class);
    }

    /**
     * Enable/Disable the target running buttons. Note that the buttons are in alphabetic order.
     * @param running whether the buttons should be setup for the Run state
     */
    void setTargetRunningButtons (boolean running) {
    
        getStopAction().setEnabled(running);
        
        // Hopefully this is temporary - if stop is disabled (its false) we want the Run menu item and button 
        // displayed rather than the resume menu item and button
        if (running) {
            
            JPopupMenu runPop = getRunMenu().getPopupMenu();
            int index = runPop.getComponentIndex(getRunMenuItem());
            if (index >= 0) {
                runPop.remove(index);
                runPop.add(getResumeMenuItem(), index);
            }
            
            index = runPop.getComponentIndex(getRunSubMenu());
            if (index >= 0) {
                runPop.remove(index);
            }
            
            JToolBar toolbar = getToolBarPane();
            index = toolbar.getComponentIndex(getRunButton());
            if (index >= 0) {
                toolbar.remove(index);
                toolbar.add(getResumeRunButton(), index);
                
                // The toolbar doesn't properly set the button border if you add/remove
                // a button at this point. Therefore we manually set the correct border
                // from a toolbar button that is already in the toolbar.
                getResumeRunButton().setBorder(getAddReflectorGemButton().getBorder());
            }
            
            index = toolbar.getComponentIndex(getRunDropDownButton());
            if (index >= 0) {
                toolbar.remove (index);
            }
            
        } else {

            JPopupMenu runPop = getRunMenu().getPopupMenu();
            int index = runPop.getComponentIndex(getResumeMenuItem());
            if (index >= 0) {
                runPop.remove(index);
                runPop.add(getRunMenuItem(), index);
                runPop.add(getRunSubMenu(), index + 1);
            }
            
            JToolBar toolbar = getToolBarPane();
            index = toolbar.getComponentIndex(getResumeRunButton());
            if (index >= 0) {
                toolbar.remove(index);
                toolbar.add(getRunButton(), index);
                toolbar.add(getRunDropDownButton(), index + 1);
                
                // See above.
                getRunButton().setBorder(getAddReflectorGemButton().getBorder());
                getRunDropDownButton().setBorder(getAddReflectorGemButton().getBorder());
            }
        }
    }
    
    /**
     * Execute the supercombinator defined by the Target.
     * @param targetDisplayedGem the target to be run.
     */
    void runTarget(DisplayedGem targetDisplayedGem) {
        
        DisplayedGem displayedGem = getTableTop().getDisplayedGem(currentGemToRun);
        
        // Update the currentGemToRun accordingly
        if (targetDisplayedGem != null && displayedGem != targetDisplayedGem) {
            currentGemToRunListener.setGem(targetDisplayedGem.getGem());
        }

        displayedGemRunner.runTargetDisplayedGem(targetDisplayedGem);
    }

    /**
     * @return Returns the targetRunnableListener.
     */
    public TargetRunnableListener getTargetRunnableListener() {
        return targetRunnableListener;
    }
    
    /**
     * Sets the visibility of the Play button popup menu and the Run menu item
     * to false.
     */
    void closeRunPopupMenus() {
        
        getRunSubMenu().getPopupMenu().setVisible(false);
        
        if (runDropDownMenu != null) {
            runDropDownMenu.setVisible(false);
        }
    }
    
    /**
     * Return the status message manager for the GemCutter
     * @return StatusMessageManager the status message manager for the GemCutter
     */
    StatusMessageDisplayer getStatusMessageDisplayer() {
        return statusMessageManager;
    }

    /**
     * Set the status flag (message) on the status bar.
     * @return the old flag
     * @param flag String the new flag (Null clears the message)
     */
    String setStatusFlag(String flag) {
        // 'Swap' labels
        JLabel statusLabel = getStatusMsg1();
        String oldFlag = statusLabel.getText();
    
        // A null flag is short for 'ready', otherwise just set the given text
        if (flag == null) {
            flag = getResourceString("ReadyFlag");
        }   
        
        statusLabel.setText(flag);
        return oldFlag;
    }

    /**
     * Toggle whether the Status bar is present.
     */
    public void viewStatusBar() {
        getStatusBarPane().setVisible(!(getStatusBarPane().isVisible()));
    }

    /**
     * Display the controls for argument entry.
     * This included a Navigation Tool Bar containing JButtons for arguments, and the value panels in the arguments pane.
     * @param inputToEditorMap map from input to editor, for those inputs for which the currently
     *  running gem requires arguments.  An iterator on this map should return the inputs in the correct order.
     */
    void showArgumentControls(Map<PartInput, ValueEditor> inputToEditorMap) {

        // Add a navigation toolbar.
        
        // get the main ToolBar and figure out how tall the nav buttons need to be
        JToolBar toolBar = getToolBarPane();
        Insets toolBorder = toolBar.getInsets();
        Insets navBorder = navigationToolBar.getInsets();
        Border buttonBorder = getAddReflectorGemButton().getBorder();
        int buttonHeight = toolBar.getHeight() - toolBorder.top - toolBorder.bottom - navBorder.top - navBorder.bottom;
    
        // Set up the navigation buttons.
        int argIndex = 0;
        for (final Map.Entry<PartInput, ValueEditor> mapEntry : inputToEditorMap.entrySet()) {
            Gem.PartInput inputPart = mapEntry.getKey();
            ValueEditor editor = mapEntry.getValue();
            
            // Create a navigation button, and hook it up to its action.
            JButton navigationButton = new JButton(String.valueOf(argIndex + 1));
            navigationButton.addActionListener(new ParameterNavigationActionListener(editor));
            navigationButton.setMargin(new Insets(0, 0, 0, 0));
    
            // Set its tooltip.
            DisplayedGem displayedGem = getTableTop().getDisplayedGem(inputPart.getGem());
            String tooltipMessage = GemCutterMessages.getString("GemArgumentNavToolTip", Integer.toString(inputPart.getInputNum() + 1), displayedGem.getDisplayText());
            navigationButton.setToolTipText(tooltipMessage);

            // Constrain the button's height.
            Dimension dim = navigationButton.getPreferredSize();
            navigationButton.setPreferredSize(new Dimension(dim.width, buttonHeight));
            
            // Add the button to the toolbar.
            navigationToolBar.add(navigationButton);
            
            // For some reason the JToolbar doesn't get the button borders right. 
            // So here we steal the border from another button and set it.
            navigationButton.setBorder(buttonBorder);

            argIndex++;
        }
        
        // make the Reset button's height the same as the nav buttons and add it at the end of the nav bar
        JButton resetButton = makeNewButton(getResetAction());
        Dimension dim = resetButton.getPreferredSize();
        resetButton.setPreferredSize(new Dimension(dim.width, buttonHeight));
        navigationToolBar.add(resetButton);
        resetButton.setBorder(buttonBorder);
        
        // Add the toolbar.
        toolBar.add(navigationToolBar);
        toolBar.validate();
        
        // if it's the target that's running, put the argument explorer into run mode.
        if (getTableTop().isRunning(getTableTop().getTargetDisplayedCollector())) {
            getArgumentExplorer().showArgumentControls(inputToEditorMap);
            
            // If the argument explorer is available, show it so that argument entry is facilitated.
            if (getExplorerArgumentsPane().indexOfComponent(getArgumentExplorer()) >= 0) {
                explorerArgumentsPaneEditModeSelectedTab = getExplorerArgumentsPane().getSelectedComponent();
                getExplorerArgumentsPane().setSelectedComponent(getArgumentExplorer());
          
            } else {
                explorerArgumentsPaneEditModeSelectedTab = null;
            }
        } else {
            explorerArgumentsPaneEditModeSelectedTab = null;
        }
    }
    
    /**
     * Hide the controls for argument entry.
     */
    void hideArgumentControls() {
    
        getToolBarPane().remove(navigationToolBar);
        navigationToolBar.removeAll();
        getToolBarPane().repaint();
        
        // put the argument explorer into edit mode.
        getArgumentExplorer().hideArgumentControls();
        
        // Restore any previous tab selection state, if the previously-selected tab is still available.
        if (explorerArgumentsPaneEditModeSelectedTab != null) {
            int componentIndex = getExplorerArgumentsPane().indexOfComponent(explorerArgumentsPaneEditModeSelectedTab);
            if (componentIndex >= 0) {
                getExplorerArgumentsPane().setSelectedIndex(componentIndex);
            }
        }
    }

    /**
     * Toggle whether the Navigation toolbar is present.
     */
    public void viewToolBar() {
        getToolBarPane().setVisible(!getToolBarPane().isVisible());
    }

    /**
     * Set up highlighting for undo and redo drop down menus.  
     * When a menu item is "armed" (under the mouse pointer) all items appearing before it also have a 
     * selected appearance to indicate that the first n items will be undone.
     * @param popupMenu JPopupMenu the menu to which to listen for arming changes.
     */
    private void setupUndoRedoDropDownMenuHighlighting(final JPopupMenu popupMenu) {
        final int nMenuItems = popupMenu.getComponentCount();

        // Create an item change armer
        // a local class that (dis)arms all the items up to the changed item.
        final ChangeListener itemChangeArmer = new ChangeListener() {

            public void stateChanged(ChangeEvent e) {

                JMenuItem changedItem = (JMenuItem)e.getSource();
                boolean armed = changedItem.isArmed();

                // arm the menu items up to the item whose state changed
                for (int i = 0; i < nMenuItems; i++) {

                    JMenuItem someItem = (JMenuItem)popupMenu.getComponent(i);

                    if (someItem == changedItem) {
                        break;
                    }
                    someItem.setArmed(armed);
                }
            }
        };

        // Add listener to the menu items to be notified when they're armed or disarmed
        // Note that disarming always occurs before any arming, so here arming will always be 
        // for items {0..n} for n >= 0.
        for (int i = 0; i < nMenuItems; i++) {
            JMenuItem menuItem = (JMenuItem)popupMenu.getComponent(i);
            menuItem.addChangeListener(itemChangeArmer);
        }
        
        // Add a popup menu listener that will strip off the item change armer as a listener
        // when the popup menu disappears
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e){}
            public void popupMenuWillBecomeVisible(PopupMenuEvent e){}

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e){
                for (int i = 0; i < nMenuItems; i++) {
                    JMenuItem menuItem = (JMenuItem)popupMenu.getComponent(i);
                    menuItem.removeChangeListener(itemChangeArmer);
                }
            }
        });
    }
    
    /**
     * Displays the pop up menu listing all redo edits available when the redo drop down button is pressed.
     * Edits are displayed in reverse chronological order.  When an edit is selected, all edits to that point
     * are redone.
     */
    public void displayRedoDropDownMenu() {

        UndoableEdit[] redoableEdits = extendedUndoManager.getRedoableEdits();

        // a listener for selection for popup menu items
        class RedoPopupSelectionListener implements ActionListener {

            private final int numRedos; // the number of redos to redo

            RedoPopupSelectionListener(int numRedos){
                this.numRedos = numRedos;
            }

            public void actionPerformed(ActionEvent e){
                // redo (numRedos) redos
                for (int i = 0; i < numRedos; i++) {
                    redo();
                }
            }
        }

        // figure out how many edits to display
        int numDisplayedEdits = Math.min(redoableEdits.length, MAX_DISPLAYED_UNDOS);

        // create a popup menu with redoable edits
        JPopupMenu popupMenu = new JPopupMenu();
        for (int i = 0; i < numDisplayedEdits; i++) {
            UndoableEdit edit = redoableEdits[i];
            JMenuItem menuItem = new JMenuItem(edit.getPresentationName());
            popupMenu.add(menuItem);
            menuItem.addActionListener(new RedoPopupSelectionListener(i + 1));
        }

        // The popup menu should highlight the items that will be undone
        setupUndoRedoDropDownMenuHighlighting(popupMenu);

        // show the popup directly underneath the redo button, left-aligned with it
        Rectangle bounds = getRedoButton().getBounds();
        popupMenu.show(getToolBarPane(), bounds.x, bounds.y + bounds.height);
    }

    /**
     * Displays the pop up menu listing all undo edits available when the undo drop down button is pressed.
     * Edits are displayed in reverse chronological order.  When an edit is selected, all edits to that point
     * are undone.
     */
    public void displayUndoDropDownMenu() {

        UndoableEdit[] undoableEdits = extendedUndoManager.getUndoableEdits();

        // a listener for selection for popup menu items
        class UndoPopupSelectionListener implements ActionListener {

            private final int numUndos; // the number of undos to undo

            UndoPopupSelectionListener(int numUndos){
                this.numUndos = numUndos;
            }

            public void actionPerformed(ActionEvent e){
                // undo (numUndos) undos
                for (int i = 0; i < numUndos; i++) {
                    undo();
                }
            }
        }

        // figure out how many edits to display
        int numDisplayedEdits = Math.min(undoableEdits.length, MAX_DISPLAYED_UNDOS);

        // create a popup menu with undoable edits
        JPopupMenu popupMenu = new JPopupMenu();

        for (int i = 0; i < numDisplayedEdits; i++) {

            UndoableEdit edit = undoableEdits[i];
            JMenuItem menuItem = new JMenuItem(edit.getPresentationName());
            popupMenu.add(menuItem);
            menuItem.addActionListener(new UndoPopupSelectionListener(i + 1));
        }

        // The popup menu should highlight the items that will be undone
        setupUndoRedoDropDownMenuHighlighting(popupMenu);

        // show the popup directly underneath the undo button, left-aligned with it
        Rectangle bounds = getUndoButton().getBounds();
        popupMenu.show(getToolBarPane(), bounds.x, bounds.y + bounds.height);
    }
    
    /**
     * Redo.
     */
    private void redo() {
        
        try {
            // attempt redo
            extendedUndoManager.redo();
            getTableTop().updateCodeGemEditors();
            getTableTop().updateForGemGraph();
        } catch(CannotRedoException e) {
            extendedUndoManager.discardAllEdits();
        } finally {
            // make sure the undo widgets are updated
            updateUndoWidgets();
        }
    }

    /**
     * Undo.
     */
    private void undo() {

        try {
            // attempt undo
            extendedUndoManager.undo();
            getTableTop().resizeForGems();
            getTableTop().updateCodeGemEditors();
            getTableTop().updateForGemGraph();

        } catch(CannotUndoException e) {
            extendedUndoManager.discardAllEdits();            
        } finally {
            // make sure the undo widgets are updated
            updateUndoWidgets();
        }
    }

    /**
     * Update the enabled/disabled state of the undo and redo buttons and menu items.
     */
    private void updateUndoWidgets() {

        boolean canUndo = extendedUndoManager.canUndo();
        boolean canRedo = extendedUndoManager.canRedo();
        String undoText = extendedUndoManager.getUndoPresentationName();
        String redoText = extendedUndoManager.getRedoPresentationName();

        getUndoAction().setEnabled(canUndo);
        getRedoAction().setEnabled(canRedo);

        getUndoDropDownButton().setEnabled(canUndo);
        getRedoDropDownButton().setEnabled(canRedo);

        getUndoMenuItem().setText(undoText);
        getRedoMenuItem().setText(redoText);

        getUndoAction().putValue(Action.SHORT_DESCRIPTION, undoText);
        getRedoAction().putValue(Action.SHORT_DESCRIPTION, redoText);

        // Update the state of the paste action.
        // TODOEL: the "updateUndoWidgets" method isn't the right place for this.
        getPasteAction().setEnabled(getTableTop().canPasteToGemGraph(getClipboard().getContents(this)));
        
        // Update the window title in case the dirty flag changed.
        updateWindowTitle();
    }
    
    /**
     * @return whether the GemCutter is considered to be in the "dirty" state.
     * This will cause the GemCutter to prompt the user to save if losing work might happen.
     */
    private boolean isDirty() {
        return extendedUndoManager.editToBeUndone() != editToUndoWhenNonDirty;
    }

    /**
     * Update the window title to show the name of the gem being currently edited.
     */
    private void updateWindowTitle() {
        String windowTitle = getResourceString("WindowTitle");
        
        // Check for the case where there isn't a working module
        // This can happen on startup, before compilation has occurred.
        ModuleName workingModuleName = getWorkingModuleName();
        if (workingModuleName != null) {
        
            windowTitle += " - " + QualifiedName.make(workingModuleName, getTableTop().getTargetCollector().getUnqualifiedName()).getQualifiedName();
            if (isDirty()) {
                windowTitle += "*";
            }
        }
        setTitle(windowTitle);
    }

    /**
     * Returns the current Collector which should be used for adding new reflectors.
     * @return CollectorGem
     */
    CollectorGem getCollectorForAddingReflector() {
        return currentCollectorForAddingReflector;
    }

    /**
     * @return whether the GemCutter is currently in a saveable state.
     */
    private boolean inSaveableState() {
        // First get all the collectors in order
        Set<CollectorGem> collectorSet = getTableTop().getGemGraph().getCollectors();
            
        // Check for collectors we can save.
        for (final CollectorGem cGem : collectorSet) {
            if (cGem.isRunnable()) {
                return true;
            }
        }

        // We can't save any collectors.
        return false;
    }
    
    /**
     * Save the given gem. This delegates to the persistence manager to do the actual saving
     * and simply takes care of the UI part. The user will be presented with a dialog for
     * selecting the gem to save and its visibility
     * @return true if gem has been saved successfully; false if cancel, close or error in saving
     */
    boolean saveGem() {

        // If we can't save any collectors, display an appropriate dialog and exit
        if (!inSaveableState()) {
            String titleString = getResourceString("CannotSaveDialogTitle");
            String message = getResourceString("NonSaveableGemError");
            JOptionPane.showMessageDialog(this, message, titleString, JOptionPane.ERROR_MESSAGE);
            return false;
        }
    
        CollectorGem targetCollector = getTableTop().getTargetCollector();
        String unqualifiedGemName = targetCollector.getUnqualifiedName();
        QualifiedName gemName = QualifiedName.make(getWorkingModuleName(), unqualifiedGemName);
        
        GemCutterSaveDialog saveDialog = new GemCutterSaveDialog(this, gemName);
        centerWindow(saveDialog);

        // This will block until the user closes the dialog.
        saveDialog.setVisible(true);
    
        if (!saveDialog.isDialogAccepted()) {
            return false;
        }

        Status saveStatus = persistenceManager.saveGem(targetCollector, saveDialog.getScope());
        
        if (saveStatus.getSeverity() != Status.Severity.ERROR) {
            // Get the entity for the newly saved gem.
            GemEntity newGemEntity = getWorkspace().getGemEntity(gemName);
            
            BrowserTree browserTree = getGemBrowser().getBrowserTree();
            //BrowserTreeModel browserTreeModel = (BrowserTreeModel) browserTree.getModel();
            
            // Select the newly saved gem in the gem browser.
            browserTree.selectGemNode(newGemEntity);
            
            // Refresh the tree node associated with this gem entity
            //browserTreeModel.getTreeNode(newGemEntity).refreshNode();
            
            if (saveStatus.getSeverity() == Status.Severity.WARNING) {
                JOptionPane.showMessageDialog(this, saveStatus.getDebugMessage(), getResourceString("SaveGem_SavedWithWarnings"), JOptionPane.WARNING_MESSAGE);
            } else {
                statusMessageManager.displayMessage(this, getResourceString("SaveGem_SaveSuccessful"), StatusMessageDisplayer.MessageType.TRANSIENT, true);
            }

            // Update the dirty edit
            editToUndoWhenNonDirty = extendedUndoManager.editToBeUndone();
            updateWindowTitle();
        
        } else {
            JOptionPane.showMessageDialog(this, saveStatus.getDebugMessage(), getResourceString("SaveGem_SaveFailed"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    
    }
    
    /**
     * Workaround for sun bug id 4711700
     * A bug in the native image loading code sometimes causes the Windows file chooser to throw a 
     *   NullPointerException when instantiated in jdk 1.4.2.
     */
    public static void ensureJFileChooserLoadable() {

        if (UIManager.getLookAndFeel().getName().startsWith("Windows")) {
            JFileChooser fileChooser = null;
            
            while (fileChooser == null) {
                try {
                    fileChooser = new JFileChooser();
                } catch (NullPointerException e) {
                }
            }
        }
    }
    
    /**
     * Loads the gem design for the given entity and puts it on the table top.
     * If the given entity is null it will present a dialog with a list of all available designs.
     * @param gemEntity the entity whose design to load (null to prompt the user for it)
     * @throws IllegalArgumentException if there is no design for the given entity
     */
    void openGemDesign(GemEntity gemEntity) {
        
        if (!promptSaveCurrentTableTopIfNonEmpty(null, null)){
            return;
        }
        
        Status loadStatus = new Status("GemDesignLoadStatus");
        
        if (gemEntity != null) {
            persistenceManager.loadGemDesign(gemEntity, loadStatus);
            if (loadStatus.isOK()) {
                // select the gem in the browser
                getGemBrowser().getBrowserTree().selectGemNode(gemEntity);
            }
        } else {
            
            // Display a dialog for the user to pick a design to load.
            FileFilter fileFilter = new ExtensionFileFilter(XMLPersistenceConstants.XML_FILE_EXTENSION, getResourceString("OpenDesign_FileDescription"));
        
            String initialDir = getPreferences().get(OPEN_DESIGN_DIRECTORY_PREF_KEY, OPEN_DESIGN_DIRECTORY_DEFAULT);
            
            JFileChooser fileChooser = new JFileChooser(initialDir);
            fileChooser.setFileFilter(fileFilter);
        
            int chooserOption = fileChooser.showOpenDialog(this);
            if (chooserOption != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            // Save the directory the user browsed to for next time
            String lastDir = fileChooser.getSelectedFile().getParentFile().getAbsolutePath();
            getPreferences().put(OPEN_DESIGN_DIRECTORY_PREF_KEY, lastDir);
    
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            File designFile = new File(fileName);
            
            GemDesign gemDesign = GemDesign.loadGemDesign(designFile, loadStatus);
            QualifiedName designName = gemDesign.getDesignName();
            
            if (designName != null && getWorkspace().getMetaModule(designName.getModuleName()) == null) {

                // If the module the design is from does not exist, ask the user if he wants
                // to load the design in the current module.
                
                int result = JOptionPane.showConfirmDialog(this, 
                                                           getResourceString("OpenDesign_ModuleNotOpen"),                                                           
                                                           getResourceString("OpenDesign_ConfirmDialogTitle"),
                                                           JOptionPane.OK_CANCEL_OPTION);
                
                if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            
            persistenceManager.loadGemDesign(gemDesign, loadStatus);
        }
        
        if (!loadStatus.isOK()) {
            String title = getResourceString("WarningDialogTitle");
            String message = getResourceString("OpenErrorsWarning");
            String details = loadStatus.getDebugMessage();
            DetailsDialog dialog = new DetailsDialog(this, title, message, details, DetailsDialog.MessageType.WARNING);
            dialog.doModal();
        }
            
        // reset the table top explorer
        getTableTop().addGemGraphChangeListener(getTableTopExplorer());
        getTableTopExplorerAdapter().getTableTopExplorer().rebuildTree();
            
        // clear the undo stack and dirty edit.
        extendedUndoManager.discardAllEdits();
        editToUndoWhenNonDirty = null;
        updateUndoWidgets();
    }
    
    /**
     * Sets the given locale into GemCutter's preferences. 
     * @param locale the locale to be set into the preferences.
     */
    static void setLocaleToPreferences(Locale locale) {
        getPreferences().put(LOCALE_PREF_KEY, LocaleUtilities.localeToCanonicalString(locale));
    }
    
    /**
     * @return the locale preference from GemCutter's preferences.
     */
    public static Locale getLocaleFromPreferences() {
        String localeString = getPreferences().get(LOCALE_PREF_KEY, "");
        if (localeString.equals("")) {
            return LocaleUtilities.INVARIANT_LOCALE;
        } else {
            return LocaleUtilities.localeFromCanonicalString(localeString);
        }
    }
    
    /**
     * Prompts the user with a dialog to save the current table top if it is not empty. Do nothing otherwise.
     * Should be called before any action that will erase the current table top. 
     * (Exit, New, Change module, Remove module...etc)
     * 
     * @param message the message to display; could be null to use default message
     * @param title title of the dialog window,; could be null to use default title
     * @return true if tabletop is saved successfully or the tabletop is empty;
     * false if cancel, don't save, close or save failed
     * 
     */
    private boolean promptSaveCurrentTableTopIfNonEmpty(String message, String title) {
        
        boolean toContinue = true;

        // If the table top has any changes or gems.
        if (isDirty() && tableTop.getDisplayedGems().size() > 1) {
            
            // 3 methods to capture user action are implemented in the JOptionPane below. 
            // Button & Action listener (mouse), mnemonics for buttons (<Alt-letter>) and keyEventDispatcher (<letter>)
            
            
            String dialogMessage = message;
            String dialogTitle = title;
            // Use default strings if the arguments are null
            if (dialogMessage == null) {
                dialogMessage = getResourceString("SaveTabletopDialogMessage");
            }
            if (dialogTitle == null) {
                dialogTitle = getResourceString("SaveTabletopDialogTitle");
            }

            final JOptionPane pane = new JOptionPane(dialogMessage, JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION);
            JDialog dialog = pane.createDialog(GemCutter.this, dialogTitle);
            
            // Display strings for the options
            final String save = getResourceString("SaveTabletopDialogSave");
            final String dontSave = getResourceString("SaveTabletopDialogNoSave");
            final String cancel = getResourceString("SaveTabletopDialogCancel");
            final String[] optionNames = { save, dontSave, cancel };
            
            final int saveMnemonic = GemCutterActionKeys.MNEMONIC_DIALOG_OPTION_SAVE;
            final int dontSaveMnemonic = GemCutterActionKeys.MNEMONIC_DIALOG_OPTION_DONTSAVE;
            final int cancelMnemonic = GemCutterActionKeys.MNEMONIC_DIALOG_OPTION_CANCEL;
            
            // ActionListener for mouse action on the buttons
            ActionListener setPaneValueWhenClickedListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String actionCommand = e.getActionCommand();
                    if (actionCommand.equals(save)) {
                        pane.setValue(save);

                    } else if (actionCommand.equals(dontSave)) {
                        pane.setValue(dontSave);

                    } else if (actionCommand.equals(cancel)) {
                        pane.setValue(cancel);
                    }
                }
            };
            // Options to pass into the dialog
            JButton saveBtn = new JButton(save);
            saveBtn.setMnemonic(saveMnemonic);
            saveBtn.addActionListener(setPaneValueWhenClickedListener);

            JButton dontSaveBtn = new JButton(dontSave);
            dontSaveBtn.setMnemonic(dontSaveMnemonic);
            dontSaveBtn.addActionListener(setPaneValueWhenClickedListener);
            
            JButton cancelBtn = new JButton (cancel);
            cancelBtn.setMnemonic(cancelMnemonic);
            cancelBtn.addActionListener(setPaneValueWhenClickedListener);        

            JButton[] optionBtns = { saveBtn, dontSaveBtn, cancelBtn };
            pane.setOptions(optionBtns);

            // Set the default on the Don's Save button
            pane.setInitialValue(dontSaveBtn);
            pane.selectInitialValue();
            
            // The key event dispatcher used to catch the mnemonics for the options (without ALT)
            final KeyEventDispatcher mnemonicKeyEventDispatcher = new KeyEventDispatcher() {
                
                public boolean dispatchKeyEvent(KeyEvent evt) {
                    // Only process the KEY_PRESSED events and ignore KEY_RELEASED and KEY_TYPED events for the same keystroke 
                    // If the type check is not implemented, the GemcutterSaveDialog's dispatchKeyEvent will process a 
                    // different event from the same key stroke.
                    if (evt.getID() == KeyEvent.KEY_PRESSED) {
                        if (evt.getKeyCode() == saveMnemonic) {
                            evt.consume();
                            pane.setValue(save);
                            return true;
                       
                        } else if (evt.getKeyCode() == dontSaveMnemonic) {
                            evt.consume();
                            pane.setValue(dontSave);
                            return true;
                        
                        } else if (evt.getKeyCode() == cancelMnemonic) {
                            evt.consume();
                            pane.setValue(cancel);
                            return true;
                        }
                    }
                    return false;
                }
            };
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(mnemonicKeyEventDispatcher);

            dialog.setVisible(true);

            // Remove the key event dispatcher
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(mnemonicKeyEventDispatcher);
            
            // Get the user selected value from the option pane
            Object selectedOption = pane.getValue();
            int userChoice = JOptionPane.CANCEL_OPTION;
            if (selectedOption != null) {
                for (int i = 0; i < optionNames.length; i++) {
                    if (optionNames[i].equals(selectedOption)) {
                        userChoice = i;
                        break;
                    }
                }
            }

            // Determine the result based on the selected value
            if (userChoice == JOptionPane.YES_OPTION) {
                // If unable to save the gem or the save action is cancelled
                if (!saveGem()) {
                    return false;
                }

            } else if (userChoice == JOptionPane.CANCEL_OPTION || userChoice == JOptionPane.CLOSED_OPTION) {
                return false;
            }
        }
        return toContinue;
    }

}
