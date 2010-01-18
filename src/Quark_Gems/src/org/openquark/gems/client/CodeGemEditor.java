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
 * CodeGemEditor.java
 * Creation date: (1/26/01 11:16:20 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.undo.StateEdit;
import javax.swing.undo.StateEditable;
import javax.swing.undo.UndoableEdit;

import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.CodeAnalyser.AnalysedIdentifier;
import org.openquark.cal.compiler.CodeAnalyser.OffsetCompilerMessage;
import org.openquark.cal.compiler.CodeAnalyser.AnalysedIdentifier.QualificationType;
import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.Argument.NameTypePair;
import org.openquark.gems.client.Gem.PartConnectable;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.browser.GemDrawerSelection;
import org.openquark.gems.client.caleditor.AdvancedCALEditor;
import org.openquark.gems.client.caleditor.AdvancedCALEditor.SymbolHighlighter;
import org.openquark.gems.client.navigator.NavAddress;
import org.openquark.gems.client.navigator.NavFrameOwner;
import org.openquark.gems.client.valueentry.ValueEditorManager;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;


/**
 * The CodeGemEditor is a JDialog which contains a GemCodePanel, plus some
 * smarts needed to update the CodeGem state based upon user actions such as
 * code editing and input reordering. Most of these involve running passes of
 * the parser over the code to ascertain parts of the syntax which allows us to
 * provide a graphical view of variables and some hinting.
 * 
 * @author Luke Evans
 */
public class CodeGemEditor extends JDialog implements StateEditable, AutoCompleteManager.AutoCompleteEditor {

    private static final long serialVersionUID = -8650190926136188960L;

    /*
     * Keys for fields in map used with state editable interface
     */
    private static final String LAST_ARGUMENTS_KEY = "LastArgumentsStateKey";

    private static final String LAST_RESOLVED_QUALIFICATIONS_KEY = "LastResolvedQualificationsKey";

    private static final String OLD_NAME_TO_INPUT_MAP_KEY = "OldNameToInputMapStateKey";

    private static final String ARG_NAMED_VARS_KEY = "VarNamesWhichAreArgsStateKey";

    private static final String PRESERVE_ORDER_KEY = "ShouldPreserveNaturalInputOrderStateKey";

    /*
     * Icons used for popup menu items
     */
    private static final ImageIcon POPUP_NAVEDIT_ICON = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/nav_edit.gif"));

    private static final ImageIcon POPUP_NAVVIEW_ICON = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/nav_edit.gif"));

    private static final ImageIcon POPUP_CONSTRUCTOR_ICON = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/Gem_Yellow.gif"));

    private static final ImageIcon POPUP_FUNCTION_ICON = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/nav_function.gif"));

    private static final ImageIcon POPUP_CLASS_ICON = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/nav_typeclass.gif"));

    private static final ImageIcon POPUP_TYPE_ICON = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/nav_typeconstructor.gif"));

    private static final ImageIcon POPUP_MAKE_ARGUMENT_ICON = new ImageIcon(CodeGemEditor.class.getResource("/Resources/argument.gif"));

    /** The CodeGem edited by this component. */
    private final CodeGem codeGem;

    /** The code analyser object used to inspect the code */
    private CodeAnalyser codeAnalyser;

    /** The type string provider used to convert types to strings. */
    private final TypeStringProvider typeStringProvider;

    /** The perspective that keeps track of the current working module. */
    private final Perspective perspective;

    /** The frame owner of the metadata viewer/editor */
    private final NavFrameOwner navigatorOwner;

    /** Whether metadata editing is allowed */
    private final boolean metadataEditAllowed;

    /** Transfer handler for the code text editor component */
    private final EditorTextTransferHandler editorTransferHandler;

    /**
     * Popup menu provider for code editor and qualifications/variables
     * displays.
     */
    private final PopupMenuProvider popupMenuProvider = new PopupMenuProvider();

    /**
     * A variable to keep track of whether the syntax smarts are activated. For
     * example, we should set this to true whenever we are setting the text in
     * the code panel, so that a code edit-type event is not fired.
     */
    private transient boolean smartsActivated = true;

    /**
     * Indicates whether the editor is recording edit (undo/redo) states. For
     * example, these states are not recorded when a local variable is renamed.
     */
    private transient boolean recordingEditStates = true;

    //
    // Constituent components
    //
    private final JPanel dialogContentPane;

    private final GemCodePanel gemCodePanel;

    /** The handler for code gem edits. */
    private final EditHandler editHandler;

    //
    // Timer stuff
    //
    /**
     * The parse timer triggers a code panel parse a given amount of time after
     * an edit.
     */
    private final Timer parseTimer;

    /** The amount of time to wait (in ms) for user activity before parsing. */
    private static final int PARSE_TIMER_PERIOD = 1000;

    /** The syntax listener that handles all the syntax coloring */
    private final GemCodeSyntaxListener gemCodeSyntaxListener;

    //
    // Stuff to keep track of previous state.
    //
    /**
     * The list of arguments the last time the display had any
     */
    private List<NameTypePair> lastArguments = new ArrayList<NameTypePair>();

    /**
     * Map from name to input from the last time the codegem was not broken.
     */
    private Map<String, PartInput> oldNameToInputMap = new HashMap<String, PartInput>();

    /**
     * The whether to reorder the inputs upon, for instance, codeGem code
     * change. True means that inputs always appear in code order on code
     * change. False means that new inputs in changed code are added after
     * existing inputs.
     */
    private boolean keepInputsInNaturalOrder = true;

    //
    // Various identifier-related state.
    //

    /**
     * Identifiers which have been constrained to be arguments
     * Unqualified names of arguments which appear in either qualified or
     * unqualified form are stored in this set.
     */
    private Set<String> varNamesWhichAreArgs;

    /**
     * Qualification map of all identifiers which the user has assigned to
     * specific modules, regardless of whether they are still used within the
     * code. Resolved ambiguities and qualifications which have previously been
     * arguments are persistent in this map.
     * 
     * Note: The map stored within the code gem contains only necessary entries
     * from this map, as well as entries representing unambiguous qualifications
     * (such entries do not appear in the userQualifiedIdentifiers map if the
     * module was not explicitly specified by the user).
     */
    private CodeQualificationMap userQualifiedIdentifiers = new CodeQualificationMap();

    /**
     * Map from incompatibly-connected part to its inferred type.
     */
    private final Map<PartConnectable, TypeExpr> incompatiblePartToInferredTypeMap = new HashMap<PartConnectable, TypeExpr>();

    /** The names of arguments which are unused in the code. */
    private final Set<String> unusedArgNames = new HashSet<String>();

    /**
     * The names of arguments which are used in unqualified form in the code.
     */
    private final Set<String> unqualifiedArgNames = new HashSet<String>();
    
    /**
     * Indicates whether there should be a delay before calling updateGemForTextChange(). See also setDelayUpdatingForTextChanges().
     */
    private boolean delayUpdatingForTextChanges = true;

    /**
     * A listener interface for edits to a code gem made by via code gem editor.
     * Possible changes include code changes, input reordering, and argument
     * qualification changes. Typically, the handler will make context-related
     * changes such as argument updating.
     * 
     * @author Edward Lam
     */
    public interface EditHandler {
        /**
         * Notify the handler that the gem definition has been edited
         * 
         * @param codeGemEditor
         *            the code gem editor used to edit the code gem.
         * @param oldInputs
         *            the inputs from before the change.
         * @param codeGemEdit
         *            an edit representing the pre and post states of the code
         *            gem.
         */
        public void definitionEdited(CodeGemEditor codeGemEditor, PartInput[] oldInputs, UndoableEdit codeGemEdit);

    }

    /**
     * Transferable object used by drag and drop between Qualification and
     * Variable display panels.
     * 
     * This class represents an argument from a Variable Panel, and is created
     * by the Variable Display Transfer Handler.
     * 
     * Its flavours are String (represented by argument name), and
     * VariableStructure (which holds details about this variable).
     * 
     * @author Iulian Radu
     */
    static final class VariablePanelTransferable implements Transferable {

        /** Structure which holds this transferable's details */
        static class VariableStructure {

            private final String name;

            private final VariablesDisplay.VariablesDisplayList parentDisplayList;

            private final boolean transformationAllowed;

            /**
             * Constructor
             * 
             * @param varName
             *            name of the variable
             * @param parentDisplayList
             *            the variables list object which created this structure
             * @param transformationAllowed
             *            whether the variable can be transformed to a function
             *            (this is not allowed for fully qualified or connected
             *            arguments)
             */
            private VariableStructure(String varName, VariablesDisplay.VariablesDisplayList parentDisplayList, boolean transformationAllowed) {
                this.name = varName;
                this.parentDisplayList = parentDisplayList;
                this.transformationAllowed = transformationAllowed;
            }

            // Accessors

            String getName() {
                return name;
            }

            VariablesDisplay.VariablesDisplayList getParentDisplayList() {
                return parentDisplayList;
            }

            boolean isTransformationAllowed() {
                return transformationAllowed;
            }
        }

        /** Transferable data */
        private final VariableStructure variableStructure;

        // Transferable flavors

        protected static final DataFlavor variableStructureFlavor = new DataFlavor(VariableStructure.class, "Variable Structure");

        private final DataFlavor[] flavors = {DataFlavor.stringFlavor, variableStructureFlavor};

        /**
         * Creates a Transferable capable of transferring the specified
         * argument.
         */
        public VariablePanelTransferable(String name, VariablesDisplay.VariablesDisplayList parentDisplayList, boolean transformationAllowed) {
            variableStructure = new VariableStructure(name, parentDisplayList, transformationAllowed);
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         */
        public DataFlavor[] getTransferDataFlavors() {
            return flavors.clone();
        }

        /**
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
         */
        public boolean isDataFlavorSupported(DataFlavor flavor) {

            for (final DataFlavor dataFlavor : flavors) {
                if (flavor.equals(dataFlavor)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            
            if (flavor == null) {
                throw new NullPointerException();
            }

            if (flavor == flavors[0]) {
                return " " + variableStructure.getName();
            } else if (flavor == flavors[1]) {
                return variableStructure;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    /**
     * Transferable object used by drag and drop between Qualification and
     * Variable display panels.
     * 
     * This class represents a qualification from a Qualification Panel, and is
     * created by the Qualification Display Transfer Handler.
     * 
     * Its flavours are String (represented by a fully qualified name), and
     * QualificationStructure (which holds details about this qualification).
     * 
     * @author Iulian Radu
     */
    static final class QualificationPanelTransferable implements Transferable {

        /** Structure which holds this transferable's details */
        static class QualificationStructure {
            private final String name;

            private final ModuleName module;

            private final SourceIdentifier.Category type;

            private final QualificationsDisplay.QualificationsDisplayList parentDisplayList;

            /**
             * Constructor
             * 
             * @param qualificationName
             *            unqualified name of qualified entity
             * @param module
             *            module it belongs to
             * @param type
             *            entity type
             * @param parentDisplayList
             *            the qualifications display list which created this
             *            transferable
             */
            private QualificationStructure(
                    String qualificationName,
                    ModuleName module,
                    SourceIdentifier.Category type,
                    QualificationsDisplay.QualificationsDisplayList parentDisplayList) {
                this.name = qualificationName;
                this.module = module;
                this.type = type;
                this.parentDisplayList = parentDisplayList;
            }

            String getName() {
                return name;
            }

            ModuleName getModule() {
                return module;
            }

            SourceIdentifier.Category getType() {
                return type;
            }

            QualificationsDisplay.QualificationsDisplayList getParentDisplayList() {
                return parentDisplayList;
            }
        }

        /** Transferable data */
        private final QualificationStructure qualificationStructure;

        // Transferable flavors

        protected static final DataFlavor qualificationStructureFlavor = new DataFlavor(QualificationStructure.class, "Qualification Structure");

        private final DataFlavor[] flavors = {DataFlavor.stringFlavor, qualificationStructureFlavor};

        /**
         * Creates a Transferable capable of transferring the specified qualification
         */
        public QualificationPanelTransferable(
                String name,
                ModuleName module,
                SourceIdentifier.Category type,
                QualificationsDisplay.QualificationsDisplayList parentDisplayList) {
            qualificationStructure = new QualificationStructure(name, module,
                    type, parentDisplayList);
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         */
        public DataFlavor[] getTransferDataFlavors() {
            return flavors.clone();
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        public boolean isDataFlavorSupported(DataFlavor flavor) {

            for (final DataFlavor dataFlavor : flavors) {
                if (flavor.equals(dataFlavor)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (flavor == null) {
                throw new NullPointerException();
            }

            if (flavor == flavors[0]) {
                return " " + QualifiedName.make(qualificationStructure.getModule(), qualificationStructure.getName()).getQualifiedName();
            } else if (flavor == flavors[1]) {
                return qualificationStructure;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    /**
     * Drag and drop transfer handler for Qualification Display. Exports
     * functions as QualificationPanelTransferables on drag out. Imports
     * argument as VariablePanelTransferables, and converts to first available
     * function if possible.
     * 
     * @author Iulian Radu
     */
    private class QualificationsDisplayTransferHandler extends TransferHandler {

        private static final long serialVersionUID = -4050162641138759864L;

        /**
         * Creates a transferable for the component qualification panel.
         * 
         * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
         */
        @Override
        protected Transferable createTransferable(JComponent c) {
            QualificationPanel panel = ((QualificationsDisplay.QualificationsDisplayList)c).getClickedPanel();
            if (panel == null) {
                return null;
            }

            AdvancedCALEditor.PositionlessIdentifier identifier = panel.getIdentifier();
            if ((!identifier.getQualificationType().isCodeQualified())
                    && (identifier.getCategory() == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD)) {
                // Export function as qualification transferable to variables display
                return new QualificationPanelTransferable(identifier.getName(),
                        identifier.getResolvedModuleName(), identifier.getCategory(),
                        (QualificationsDisplay.QualificationsDisplayList) c);

            } else {
                // Anything else is exported as a simple fully qualified string,
                // since it should not be draggable to variables display
                return new StringSelection(" " + QualifiedName.make(identifier.getResolvedModuleName(), identifier.getName()).getQualifiedName());
            }
        }

        /**
         * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
         */
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }

        /**
         * Imports variables from a variable display located in this editor.
         * 
         * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
         *      java.awt.datatransfer.Transferable)
         */
        @Override
        public boolean importData(JComponent c, Transferable t) {
            if (canImport(c, t.getTransferDataFlavors())) {
                try {
                    // Can only import from variable panels
                    VariablePanelTransferable.VariableStructure variable = 
                        (VariablePanelTransferable.VariableStructure)t.getTransferData(VariablePanelTransferable.variableStructureFlavor);

                    if (variable.getParentDisplayList() != CodeGemEditor.this.getGemCodePanel().getVariablesDisplay().getListComponent()) {
                        // Drag originated in a different code gem editor;
                        // ignore it.
                        return false;
                    }

                    if (variable.isTransformationAllowed()) {
                        changeArgumentToDefaultFunction(variable.getName());
                    }
                    return true;

                } catch (UnsupportedFlavorException ufe) {
                } catch (IOException ioe) {
                }
            }

            return false;
        }

        /**
         * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent,
         *      java.awt.datatransfer.Transferable, int)
         */
        @Override
        protected void exportDone(JComponent c, Transferable data, int action) {
            ((QualificationsDisplay.QualificationsDisplayList)c).externalDropComplete();
        }

        /**
         * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
         *      java.awt.datatransfer.DataFlavor[])
         */
        @Override
        public boolean canImport(JComponent c, DataFlavor[] flavors) {
            for (final DataFlavor dataFlavor : flavors) {
                if (dataFlavor == VariablePanelTransferable.variableStructureFlavor) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * Drag and drop transfer handler for Variables Display. Exports arguments
     * as VariablePanelTransferables on drag out. Imports functions as
     * QualificationPanelTransferables, and converts them to arguments.
     * 
     * @author Iulian Radu
     */
    private class VariablesDisplayTransferHandler extends TransferHandler {

        private static final long serialVersionUID = -5327752640679311974L;

        /**
         * Creates a transferable with the data contained in the panel component
         * 
         * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
         */
        @Override
        protected Transferable createTransferable(JComponent c) {
            VariablePanel panel = ((VariablesDisplay.VariablesDisplayList)c).getClickedPanel();
            if (panel != null) {
                AdvancedCALEditor.PositionlessIdentifier identifier = panel.getIdentifier();
                return new VariablePanelTransferable(identifier.getName(), (VariablesDisplay.VariablesDisplayList)c, 
                        !identifier.getQualificationType().isCodeQualified() && isArgumentFormChangeAllowed(identifier.getName()));
            }
            return null;
        }

        /**
         * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
         */
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }

        /**
         * Imports a qualification from a qualification panel located in this editor
         * @see javax.swing.TransferHandler#importData(javax.swing.JComponent, java.awt.datatransfer.Transferable)
         */
        @Override
        public boolean importData(JComponent c, Transferable t) {
            if (canImport(c, t.getTransferDataFlavors())) {
                try {

                    // Can only import from qualification panels
                    QualificationPanelTransferable.QualificationStructure qualification = 
                        (QualificationPanelTransferable.QualificationStructure)t.getTransferData(QualificationPanelTransferable.qualificationStructureFlavor);

                    if (qualification.getParentDisplayList() != CodeGemEditor.this.getGemCodePanel().getQualificationsDisplay().getListComponent()) {
                        // Drag originated in a different code gem editor; ignore it.
                        return false;
                    }

                    changeQualificationToArgument(qualification.getName(), qualification.getModule());

                    return true;
                } catch (UnsupportedFlavorException e) {
                } catch (IOException e) {
                }
            }

            return false;
        }

        /**
         * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent, java.awt.datatransfer.Transferable, int)
         */
        @Override
        protected void exportDone(JComponent c, Transferable data, int action) {
            ((VariablesDisplay.VariablesDisplayList) c).externalDropComplete();
        }

        /**
         * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
         *      java.awt.datatransfer.DataFlavor[])
         */
        @Override
        public boolean canImport(JComponent c, DataFlavor[] flavors) {
            for (final DataFlavor dataFlavor : flavors) {
                if (dataFlavor == QualificationPanelTransferable.qualificationStructureFlavor) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Class providing custom popup menus for the CAL editor, qualification and variables displays.
     * @author Iulian Radu
     */
    public class PopupMenuProvider implements AdvancedCALEditor.IdentifierPopupMenuProvider {

        /**
         * Focus listener for editor popup menus. On focus lost, display
         * selections are cleared
         */
        private class EditorMenuFocusListener implements PopupMenuListener {
            public void popupMenuCanceled(PopupMenuEvent e) {
                gemCodePanel.getQualificationsDisplay().clearSelection();
                gemCodePanel.getVariablesDisplay().clearSelection();
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                gemCodePanel.getQualificationsDisplay().clearSelection();
                gemCodePanel.getVariablesDisplay().clearSelection();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        }

        private final EditorMenuFocusListener editorMenuFocusListener = new EditorMenuFocusListener();

        /**
         * Menu item for converting an argument or ambiguity to a proper qualification
         * 
         * @author Iulian Radu
         */
        private class ToQualificationMenuItem extends JCheckBoxMenuItem implements ActionListener {

            private static final long serialVersionUID = -8016370644205989220L;

            private final String unqualifiedName;

            private final ModuleName moduleName;

            private final SourceIdentifier.Category type;

            private final boolean isArgument;

            /**
             * Constructor
             * 
             * @param unqualifiedName
             *            unqualified name of the identifier
             * @param moduleName
             *            module which the identifier will belong to if menu
             *            clicked
             * @param type
             *            type of identifier
             * @param isArgument
             *            whether the identifier is an argument
             */
            ToQualificationMenuItem(String unqualifiedName, ModuleName moduleName,
                    SourceIdentifier.Category type, boolean isArgument) {
                super(QualifiedName.make(moduleName, unqualifiedName)
                        .getQualifiedName());
                this.unqualifiedName = unqualifiedName;
                this.moduleName = moduleName;
                this.setIcon(getTypeIcon(type));
                this.type = type;
                this.isArgument = isArgument;
                this.addActionListener(this);
            }

            /**
             * Converts the identifier to the proper qualification
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent evt) {

                Object eventSource = evt.getSource();
                if (eventSource == this) {
                    if (isArgument) {
                        changeArgumentToQualification(unqualifiedName, moduleName);
                    } else {
                        changeAmbiguityToQualification(unqualifiedName, moduleName, type);
                    }
                }
            }

            /**
             * Overwrite tooltip location to always be on the right side of the
             * menu.
             * 
             * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
             */
            @Override
            public Point getToolTipLocation(MouseEvent e) {
                return new Point(this.getWidth(), 0);
            }
        }

        /**
         * Menu item for converting a function to an argument
         * 
         * @author Iulian Radu
         */
        private class ToArgumentMenuItem extends JCheckBoxMenuItem implements ActionListener {

            private static final long serialVersionUID = 1891597337246051104L;

            private final String unqualifiedName;

            private final ModuleName moduleName;

            /**
             * Constructor
             * 
             * @param unqualifiedName
             *            name of the identifier
             * @param moduleName
             *            module name of the identifier. Can be null if the identifier is unqualified.
             * @param active
             *            whether this menu item represents the current form of
             *            the identifier
             */
            ToArgumentMenuItem(String unqualifiedName, ModuleName moduleName,
                    boolean active) {
                super(GemCutter.getResourceString("CGE_Argument"));
                this.unqualifiedName = unqualifiedName;
                this.moduleName = moduleName;
                this.setIcon(POPUP_MAKE_ARGUMENT_ICON);
                this.setState(active);
                if (!active) {
                    // Only act if not active
                    this.addActionListener(this);
                }
            }

            /**
             * Converts the qualification to an argument
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent evt) {

                Object eventSource = evt.getSource();
                if (eventSource == this) {
                    changeQualificationToArgument(unqualifiedName, moduleName);
                }
            }

            /**
             * Overwrite tooltip location to always be on the right side of the menu.
             * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
             */
            @Override
            public Point getToolTipLocation(MouseEvent e) {
                return new Point(this.getWidth(), 0);
            }
        }

        /**
         * Menu item for renaming a local variable
         * 
         * @author Iulian Radu
         */
        private class RenameLocalVarMenuItem extends JMenuItem implements ActionListener {
            private static final long serialVersionUID = 4520386950737427876L;
            
            private final CodeAnalyser.AnalysedIdentifier identifier;

            RenameLocalVarMenuItem(CodeAnalyser.AnalysedIdentifier identifier) {
                super(GemCutter.getResourceString("CGE_RenameVariable"));
                this.identifier = identifier;
                addActionListener(this);
            }

            public void actionPerformed(ActionEvent evt) {

                // Get the state of the recording edits flag
                final boolean recordingEdits = CodeGemEditor.this.recordingEditStates;

                // The following objects will hold the current editor state

                final StateEdit stateEdit = new StateEdit(CodeGemEditor.this, GemCutter.getResourceString("UndoText_CodeChange"));
                final PartInput[] oldInputs = codeGem.getInputParts();

                // We will disable the recording edits flag while the renaming occurs.
                // Once renaming completes, we commit an edit if the renaming is
                // not canceled, then reenable the recording edits flag.

                // Because the editor has a parseTimer checking code every PARSE_TIMER_PERIOD ms,
                // we cannot enable recording edits flag before this time (since an edit state may be
                // recorded). Thus we will enable the recording edit states flag only after the next
                // possible parseTimer tick, by use of the following timer.

                final Timer editEnableTimer = new Timer(PARSE_TIMER_PERIOD + 1,
                        new ActionListener() {
                            public void actionPerformed(ActionEvent e) {

                                CodeGemEditor.this.recordingEditStates = recordingEdits;
                            }
                        });
                editEnableTimer.setRepeats(false);

                // Prepare commit/cancel listener for the symbol renamer

                AdvancedCALEditor.SymbolRenamerListener renameListener = new AdvancedCALEditor.SymbolRenamerListener() {

                    public void renameCanceled(String oldName) {

                        // Abort the state edit
                        stateEdit.die();

                        // Enable recording edits after the next parse tick
                        editEnableTimer.start();
                    }

                    public void renameDone(String oldName, String newName) {

                        // Post the edit to the edit handler.
                        stateEdit.end();
                        if (CodeGemEditor.this.editHandler != null) {
                            CodeGemEditor.this.editHandler.definitionEdited(CodeGemEditor.this, oldInputs, stateEdit);
                        }

                        // Enable recording edits after the next parse tick
                        editEnableTimer.start();
                    }
                };

                CodeGemEditor.this.recordingEditStates = false;
                gemCodePanel.getCALEditorPane().enterRenameMode(identifier, renameListener);
            }
        }

        /**
         * Menu item for changing module of the identifier
         * 
         * @author Iulian Radu
         */
        private class ModuleChangeMenuItem extends JCheckBoxMenuItem implements ActionListener {
            private static final long serialVersionUID = -4045805968225327825L;

            private final String unqualifiedName;

            private final ModuleName newModuleName;

            private final SourceIdentifier.Category type;

            /**
             * Constructor
             * 
             * @param unqualifiedName
             *            unqualified name of the identifier
             * @param newModuleName
             *            module to which the identifier will be switched by the
             *            menu item. <em>Cannot</em> be null.
             * @param type
             *            type of identifier
             * @param active
             *            whether this menu item represents the current form of
             *            the identifier
             */
            ModuleChangeMenuItem(String unqualifiedName, ModuleName newModuleName,
                    SourceIdentifier.Category type, boolean active) {
                super(QualifiedName.make(newModuleName, unqualifiedName)
                        .getQualifiedName());
                this.unqualifiedName = unqualifiedName;
                this.newModuleName = newModuleName;
                this.type = type;
                this.setIcon(getTypeIcon(type));
                this.setState(active);
                if (!active) {
                    // Only act if not active
                    this.addActionListener(this);
                }
            }

            /**
             * Informs all the listeners that a module change is requested
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent evt) {

                Object eventSource = evt.getSource();
                if (eventSource == this) {
                    changeQualificationModule(unqualifiedName, newModuleName, type);
                }
            }

            /**
             * Overwrite tooltip location to always be on the right side of the
             * menu.
             * 
             * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
             */
            @Override
            public Point getToolTipLocation(MouseEvent e) {
                return new Point(this.getWidth(), 0);
            }
        }

        /**
         * Menu item for editing metadata of the identifier
         * 
         * @author Iulian Radu
         */
        private class EditMetadataMenuItem extends JMenuItem implements ActionListener {

            private static final long serialVersionUID = -8256211170440664282L;

            private final QualifiedName identifierName;

            private final SourceIdentifier.Category identifierType;

            EditMetadataMenuItem(QualifiedName identifierName, SourceIdentifier.Category identifierType) {
                super(GemCutter.getResourceString("PopItem_EditGemProperties"));
                this.identifierName = identifierName;
                this.identifierType = identifierType;
                this.addActionListener(this);
                this.setIcon(POPUP_NAVEDIT_ICON);
            }

            public void actionPerformed(ActionEvent evt) {

                Object eventSource = evt.getSource();
                if (eventSource == this) {
                    ScopedEntity entity = CodeAnalyser.getVisibleModuleEntity(identifierName, identifierType, CodeGemEditor.this.perspective.getWorkingModuleTypeInfo());
                    NavAddress address = NavAddress.getAddress(entity);
                    navigatorOwner.editMetadata(address);
                }
            }
        }

        /**
         * Menu item for viewing metadata of the identifier
         * 
         * @author Iulian Radu
         */
        private class ViewMetadataMenuItem extends JMenuItem implements ActionListener {
            private static final long serialVersionUID = 6152554485823491115L;

            private final QualifiedName identifierName;

            private final SourceIdentifier.Category identifierType;

            ViewMetadataMenuItem(QualifiedName identifierName, SourceIdentifier.Category identifierType) {
                super(GemCutter.getResourceString("PopItem_ViewGemProperties"));
                this.identifierName = identifierName;
                this.identifierType = identifierType;
                this.addActionListener(this);
                this.setIcon(POPUP_NAVVIEW_ICON);
            }

            public void actionPerformed(ActionEvent evt) {

                Object eventSource = evt.getSource();
                if (eventSource == this) {
                    ScopedEntity entity = CodeAnalyser.getVisibleModuleEntity(identifierName, identifierType, CodeGemEditor.this.perspective.getWorkingModuleTypeInfo());
                    NavAddress address = NavAddress.getAddress(entity);
                    navigatorOwner.displayMetadata(address, true);
                }
            }
        }

        /**
         * Create menu for a qualification. This is the menu for a qualification
         * panel, or for a CAL editor identifier which is not an argument.
         * 
         * @param identifier
         * @return qualification popup menu
         */
        private JPopupMenu getQualificationPopupMenu(AdvancedCALEditor.PositionlessIdentifier identifier) {
            String unqualifiedName = identifier.getName();
            ModuleName moduleName = identifier.getResolvedModuleName();
            SourceIdentifier.Category type = identifier.getCategory();
            boolean locallyResolved = (moduleName.equals(perspective.getWorkingModuleName()));
            boolean isCodeQualified = (identifier.getQualificationType() != QualificationType.UnqualifiedResolvedTopLevelSymbol);
            JPopupMenu menu = new JPopupMenu();

            // Add metadata view/edit items
            if (navigatorOwner != null) {
                addMetadataChangeMenuItems(menu, QualifiedName.make(moduleName, unqualifiedName), type);
                menu.addSeparator();
            }

            // Add module change items

            AdvancedCALEditor calEditor = gemCodePanel.getCALEditorPane();
            if (isCodeQualified) {

                if (locallyResolved) {
                    // We can switch local qualifications to arguments
                    if (isQualificationFormChangeAllowed(type)) {
                        JMenuItem toArgumentItem = new ToArgumentMenuItem(unqualifiedName, moduleName, false);
                        toArgumentItem.setToolTipText(GemCutter.getResourceString("CGE_To_Argument"));
                        menu.add(toArgumentItem);
                    }

                    // Or keep in the current form
                    {
                        JCheckBoxMenuItem newItem = new ModuleChangeMenuItem(unqualifiedName, moduleName, type, true);
                        newItem.setToolTipText(calEditor.getMetadataToolTipText(unqualifiedName, moduleName, type, perspective.getWorkingModuleTypeInfo()));
                        menu.add(newItem);
                    }

                } else {
                    // Cannot change form; just display grayed current form
                    JCheckBoxMenuItem newItem = new ModuleChangeMenuItem(unqualifiedName, moduleName, type, true);
                    newItem.setToolTipText(calEditor.getMetadataToolTipText(unqualifiedName, moduleName, type, perspective.getWorkingModuleTypeInfo()));
                    newItem.setEnabled(false);
                    menu.add(newItem);
                }

            } else {
                // If this is an unqualified symbol

                // Add to-argument change item
                if (isQualificationFormChangeAllowed(type)) {
                    JMenuItem toArgumentItem = new ToArgumentMenuItem(unqualifiedName, moduleName, false);
                    toArgumentItem.setToolTipText(GemCutter.getResourceString("CGE_To_Argument"));
                    menu.add(toArgumentItem);

                }

                // Add module change items
                List<ModuleName> candidateModules = CodeAnalyser.getModulesContainingIdentifier(unqualifiedName, type,
                                perspective.getWorkingModuleTypeInfo());
                for (final ModuleName newModule : candidateModules) {
                    JCheckBoxMenuItem newItem = new ModuleChangeMenuItem(
                            unqualifiedName, newModule, type, (newModule
                                    .equals(moduleName)));
                    newItem.setToolTipText(calEditor.getMetadataToolTipText(
                            unqualifiedName, newModule, type, perspective
                                    .getWorkingModuleTypeInfo()));
                    menu.add(newItem);
                }
            }

            return menu;
        }

        /**
         * Create menu for an argument. This is the menu for a variable panel,
         * or for a CAL editor identifier which is an argument.
         * 
         * @param identifier
         * @return argument popup menu
         */
        private JPopupMenu getArgumentPopupMenu(AdvancedCALEditor.PositionlessIdentifier identifier) {

            String unqualifiedName = identifier.getName();
            boolean isCodeQualified = false;
            JPopupMenu menu = new JPopupMenu();

            // If argument to function change is not allowed, we will still display
            // possible changes to modules, but disable them.

            boolean changeAllowed = isArgumentFormChangeAllowed(unqualifiedName);

            JMenuItem toArgumentItem = new ToArgumentMenuItem(unqualifiedName,
                    null, true);
            if (!changeAllowed) {
                toArgumentItem.setEnabled(false);
                toArgumentItem.setToolTipText(GemCutter.getResourceString("CGE_Cannot_Transform_Argument"));
            } else {
                toArgumentItem.setToolTipText(GemCutter.getResourceString("CGE_Is_Argument"));
            }
            menu.add(toArgumentItem);

            List<ModuleName> candidateModules = CodeAnalyser
                    .getModulesContainingIdentifier(
                            unqualifiedName,
                            SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD,
                            perspective.getWorkingModuleTypeInfo());
            AdvancedCALEditor calEditor = gemCodePanel.getCALEditorPane();
            if (isCodeQualified && (candidateModules.size() > 0)) {
                // If code qualified, can only be switched to the current module
                ModuleName moduleName = perspective.getWorkingModuleName();
                if ((candidateModules.iterator().next()).equals(moduleName)) {
                    JMenuItem newItem = new ToQualificationMenuItem(
                            unqualifiedName,
                            moduleName,
                            SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD,
                            true);

                    if (!changeAllowed) {
                        newItem.setToolTipText(GemCutter.getResourceString("CGE_Cannot_Transform_Argument"));
                        newItem.setEnabled(false);
                    } else {
                        newItem.setToolTipText(calEditor.getMetadataToolTipText(unqualifiedName, moduleName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD,
                                perspective.getWorkingModuleTypeInfo()));
                    }
                    menu.add(newItem);
                }

            } else {
                // Can be switched to any module
                for (final ModuleName moduleName : candidateModules) {
                    JMenuItem newItem = new ToQualificationMenuItem(unqualifiedName, moduleName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, true);

                    if (!changeAllowed) {
                        newItem.setToolTipText(GemCutter.getResourceString("CGE_Cannot_Transform_Argument"));
                        newItem.setEnabled(false);
                    } else {
                        newItem.setToolTipText(calEditor.getMetadataToolTipText(unqualifiedName, moduleName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD,
                                perspective.getWorkingModuleTypeInfo()));
                    }
                    menu.add(newItem);
                }
            }

            return menu;
        }

        /**
         * Create menu for an ambiguity. This is the menu for a text identifier
         * which could not be qualified; the options are to turn it into an
         * argument or qualify to a function.
         * 
         * @param identifier
         * @return ambiguity popup menu
         */
        private JPopupMenu getAmbiguityPopupMenu(AdvancedCALEditor.PositionlessIdentifier identifier) {
            String unqualifiedName = identifier.getName();
            SourceIdentifier.Category type = identifier.getCategory();
            JPopupMenu menu = new JPopupMenu();

            // If argument to function change is not allowed, we will still
            // display
            // possible changes to modules, but disable them.

            if (type == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
                JMenuItem toArgumentItem = new ToArgumentMenuItem(
                        unqualifiedName, null, false);
                toArgumentItem.setToolTipText(GemCutter
                        .getResourceString("CGE_To_Argument"));
                menu.add(toArgumentItem);
            }

            AdvancedCALEditor calEditor = gemCodePanel.getCALEditorPane();
            List<ModuleName> candidateModules = CodeAnalyser
                    .getModulesContainingIdentifier(unqualifiedName, type,
                            perspective.getWorkingModuleTypeInfo());
            for (final ModuleName moduleName : candidateModules) {
                JMenuItem newItem = new ToQualificationMenuItem(
                        unqualifiedName, moduleName, type, false);
                newItem.setToolTipText(calEditor.getMetadataToolTipText(
                        unqualifiedName, moduleName, type, perspective
                                .getWorkingModuleTypeInfo()));
                menu.add(newItem);
            }

            return menu;
        }

        /**
         * Popup menu listener used for highlighting a specified local variable
         * identifier when the menu is active.
         * 
         * @author Iulian Radu
         */
        private class HighlightVariablePopupListener implements PopupMenuListener {

            private final AdvancedCALEditor.SymbolHighlighter referenceHighlighter;

            private final AdvancedCALEditor.SymbolHighlighter definitionHighlighter;

            public HighlightVariablePopupListener(CodeAnalyser.AnalysedIdentifier identifier) {
                Pair<SymbolHighlighter, SymbolHighlighter> highlighters = gemCodePanel.getCALEditorPane().createLocalVariableHighlighters(identifier);
                this.referenceHighlighter = highlighters.fst();
                this.definitionHighlighter = highlighters.snd();
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                referenceHighlighter.removeHighlights();
                definitionHighlighter.removeHighlights();
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                referenceHighlighter.removeHighlights();
                definitionHighlighter.removeHighlights();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                referenceHighlighter.applyHighlights();
                definitionHighlighter.applyHighlights();
            }
        }

        /**
         * Get the popup menu for a clicked identifier
         * 
         * @param identifier
         *            the identifier selected
         * @return JPopupMenu the popup menu to be displayed; null if no menu
         *         for this item
         */
        public JPopupMenu getPopupMenu(AdvancedCALEditor.PositionlessIdentifier identifier) {

            // Determine if identifier is an argument, ambiguity or
            // qualification

            QualificationType qualificationType = identifier.getQualificationType();

            // Argument ?
            if (qualificationType == QualificationType.UnqualifiedArgument) {

                gemCodePanel.getVariablesDisplay().selectPanelForArgument(identifier.getName());
                gemCodePanel.getQualificationsDisplay().clearSelection();
                JPopupMenu menu = getArgumentPopupMenu(identifier);
                menu.addPopupMenuListener(editorMenuFocusListener);
                return menu;
            }

            // Qualified symbol ?
            if (qualificationType.isResolvedTopLevelSymbol()) {
                gemCodePanel.getQualificationsDisplay().selectPanelForIdentifier(identifier);
                gemCodePanel.getVariablesDisplay().clearSelection();
                JPopupMenu menu = getQualificationPopupMenu(identifier);
                menu.addPopupMenuListener(editorMenuFocusListener);
                return menu;
            }

            // Ambiguity ?
            if (qualificationType == QualificationType.UnqualifiedAmbiguousTopLevelSymbol) {
                gemCodePanel.getQualificationsDisplay().selectPanelForIdentifier(identifier);
                gemCodePanel.getVariablesDisplay().clearSelection();
                JPopupMenu menu = getAmbiguityPopupMenu(identifier);
                menu.addPopupMenuListener(editorMenuFocusListener);
                return menu;
            }

            // Local Variable ?
            if (qualificationType == QualificationType.UnqualifiedLocalVariable) {

                CodeAnalyser.AnalysedIdentifier analysedIdentifier = identifier.getReference();
                if (analysedIdentifier != null) {

                    JPopupMenu menu = new JPopupMenu();
                    menu.add(new RenameLocalVarMenuItem(analysedIdentifier));
                    menu.addPopupMenuListener(new HighlightVariablePopupListener(analysedIdentifier));
                    return menu;
                }
            }

            // This menu only handles ambiguities, arguments, or qualified
            // identifiers
            gemCodePanel.getQualificationsDisplay().clearSelection();
            gemCodePanel.getVariablesDisplay().clearSelection();
            return null;
        }

        /**
         * Displays a popup menu for interacting with the specified identifier.
         * 
         * @param identifier
         *            clicked identifier
         * @param p
         *            point where the menu should be displayed (relative to
         *            invoker)
         * @param invoker
         *            component in which to display the menu
         */
        public void showMenu(AdvancedCALEditor.PositionlessIdentifier identifier, Point p, Component invoker) {
            JPopupMenu menu = getPopupMenu(identifier);
            if (menu != null) {
                menu.show(invoker, p.x, p.y);
            }
        }

        /**
         * Retrieves the icon associated to a given type
         * 
         * @param type
         * @return icon associated to type
         */
        public ImageIcon getTypeIcon(SourceIdentifier.Category type) {

            if (type == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
                return POPUP_FUNCTION_ICON;

            } else if (type == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
                return POPUP_CONSTRUCTOR_ICON;

            } else if (type == SourceIdentifier.Category.TYPE_CLASS) {
                return POPUP_CLASS_ICON;

            } else if (type == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
                return POPUP_TYPE_ICON;

            } else {
                throw new IllegalArgumentException();
            }
        }

        /**
         * Generate menu for viewing/editing properties of an identifier
         * 
         * @param menu
         *            items will be added to this menu
         * @param qualifiedName
         *            qualified name of identifier
         * @param type
         *            identifier category
         */
        public void addMetadataChangeMenuItems(JPopupMenu menu, QualifiedName qualifiedName, SourceIdentifier.Category type) {
            if (metadataEditAllowed) {
                menu.add(new EditMetadataMenuItem(qualifiedName, type));
            }
            menu.add(new ViewMetadataMenuItem(qualifiedName, type));
        }
    }

    /**
     * This is a transfer handler for the code gem editor component.
     * 
     * It is able of importing: - simple text, as a regular text component - gem
     * entities and gem drawers, inserting the respective names in the code text -
     * code text from another code editor, along with qualifications and
     * arguments
     * 
     * It exports: - code gem text, mappings and arguments for other code gem
     * editors - simple text flavour of the code, in the form of fully qualified
     * code
     * 
     * Note: The class assumes that it is attached to an AdvancedCALEditor.
     * 
     * @author Iulian Radu
     */
    public static class EditorTextTransferHandler extends TransferHandler {
        private static final long serialVersionUID = -2949337896560340803L;

        /** Analyser of editor code */
        private final CodeAnalyser codeAnalyser;

        /**
         * Tracks names of identifiers which have been constrained to
         * arguments If this is left null, the code is assumed to not have
         * arguments, and import/export of arguments is not done.
         */
        private Set<String> varNamesWhichAreArgs = null;

        /**
         * Qualification map of identifiers which have been explicitly resolved
         * by the user If this is left null, the code is assumed to not manage
         * qualification mappings, thus import/export handles fully qualified
         * code
         */
        private CodeQualificationMap userQualifiedIdentifiers = null;

        /** Transfer handler for basic text */
        private final TransferHandler textTransferHandler;

        /**
         * Constructor
         * 
         * @param textTransferHandler
         * @param analyser
         */
        public EditorTextTransferHandler(TransferHandler textTransferHandler, CodeAnalyser analyser) {
            this.textTransferHandler = textTransferHandler;
            this.codeAnalyser = analyser;
        }

        /**
         * Sets the set of argument names to use
         * @param varNamesWhichAreArgs
         */
        public void setArgumentNames(Set<String> varNamesWhichAreArgs) {
            this.varNamesWhichAreArgs = varNamesWhichAreArgs;
        }

        /**
         * Sets the qualification map to use
         * @param userQualifiedIdentifiers
         */
        public void setUserQualifiedIdentifiers(CodeQualificationMap userQualifiedIdentifiers) {
            this.userQualifiedIdentifiers = userQualifiedIdentifiers;
        }

        /**
         * Creates transferable holding the selected text and a qualification
         * map filled with qualifications of the selected identifiers.
         * 
         * @see javax.swing.TransferHandler#createTransferable(javax.swing.JComponent)
         */
        @Override
        protected Transferable createTransferable(JComponent c) {
            AdvancedCALEditor editor = (AdvancedCALEditor) c;
            String visibleText = editor.getSelectedText();
            if (visibleText == null) {
                return null;
            }

            // Qualify the selected portion of text
            String fullyQualifiedText = editor.getQualifiedCodeText(editor.getSelectionStart(), editor.getSelectionEnd(), codeAnalyser);

            // Run through the selected identifiers, and build up a map of the
            // used qualifications and arguments.
            Set<String> argumentNames = new LinkedHashSet<String>();
            CodeQualificationMap qualificationMap = new CodeQualificationMap();
            List<AnalysedIdentifier> selectedIdentifiers = editor.getSelectedIdentifiers(editor.getSelectionStart(), editor.getSelectionEnd());
            for (final CodeAnalyser.AnalysedIdentifier identifier : selectedIdentifiers) {

                if (identifier.getQualificationType() == QualificationType.UnqualifiedResolvedTopLevelSymbol) {
                    qualificationMap.putQualification(identifier.getName(), identifier.getResolvedModuleName(), identifier.getCategory());

                } else if ((varNamesWhichAreArgs != null)
                        && (identifier.getQualificationType() == QualificationType.UnqualifiedArgument)
                        && (varNamesWhichAreArgs.contains(identifier.getName()))) {
                    argumentNames.add(identifier.getName());
                }
            }

            return new CodeTextTransferable(visibleText, fullyQualifiedText, qualificationMap, argumentNames, editor, editor.getSelectionStart(), editor.getSelectionEnd());
        }

        /**
         * @see javax.swing.TransferHandler#getSourceActions(javax.swing.JComponent)
         */
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        /**
         * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
         *      java.awt.datatransfer.Transferable)
         */
        @Override
        public boolean importData(JComponent c, Transferable t) {

            DataFlavor[] flavors = t.getTransferDataFlavors();
            if (canImport(c, flavors)) {
                try {
                    // Handle custom flavors
                    for (final DataFlavor dataFlavor : flavors) {

                        if (dataFlavor == CodeTextTransferable.codeStructureFlavor) {
                            // Code text from a code editor
                            return importCodeText(c, t);

                        } else if (SingleGemEntityDataFlavor.getSingleGemEntityDataFlavor().equals(dataFlavor)) {
                            // entity from the browser view
                            return importGemEntity(c, t);

                        } else if (GemDrawerSelection.getGemDrawerDataFlavor().equals(dataFlavor)) {
                            return importGemDrawer(c, t);
                        }
                    }

                    // Not one of our flavours, but can be imported via the original handler
                    return textTransferHandler.importData(c, t);

                } catch (UnsupportedFlavorException ufe) {
                    throw new IllegalStateException("editor transfer handler trying to import invalid flavor");

                } catch (IOException ioe) {
                    throw new IllegalStateException("editor transfer handler encountered exception: " + ioe);
                }
            }
            // Cannot import data
            return false;
        }

        /**
         * Imports data from a transferable containing pasted text from a code gem.
         * @param c editor which has produced this
         * @param t transferable
         * @throws UnsupportedFlavorException
         * @throws IOException
         */
        private boolean importCodeText(JComponent c, Transferable t) throws UnsupportedFlavorException, IOException {
            // Get the code text structure
            CodeTextTransferable.CodeTextStructure codeTextStructure = 
                (CodeTextTransferable.CodeTextStructure)t.getTransferData(CodeTextTransferable.codeStructureFlavor);

            // Combine the new map entries into our own

            if (userQualifiedIdentifiers != null) {
                CodeQualificationMap codeQualificationMap = codeTextStructure.getQualificationMap();
                appendToEditorMap(codeQualificationMap, SourceIdentifier.Category.DATA_CONSTRUCTOR);
                appendToEditorMap(codeQualificationMap, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
                appendToEditorMap(codeQualificationMap, SourceIdentifier.Category.TYPE_CLASS);
                appendToEditorMap(codeQualificationMap, SourceIdentifier.Category.TYPE_CONSTRUCTOR);
            }

            // Combine the argument names into our own

            if (varNamesWhichAreArgs != null) {
                for (final String argument : codeTextStructure.getArgumentNames()) {
                    // Only add arguments if they are not already mapped
                    if (userQualifiedIdentifiers.getQualifiedName(argument, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) == null) {
                        varNamesWhichAreArgs.add(argument);
                    }
                }
            }

            // Insert the text into the editor

            AdvancedCALEditor editor = (AdvancedCALEditor)c;
            if (userQualifiedIdentifiers != null) {
                editor.replaceSelection(codeTextStructure.getVisibleCode());
            } else {
                editor.replaceSelection(codeTextStructure.getFullyQualifiedCode());
            }

            return true;
        }

        /**
         * Imports data from a transferable containing a gem entity
         * 
         * @param c
         * @param t
         * @return whether the import succeeded
         * @throws UnsupportedFlavorException
         * @throws IOException
         * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
         *      java.awt.datatransfer.Transferable)
         */
        private boolean importGemEntity(JComponent c, Transferable t) throws UnsupportedFlavorException, IOException {
            GemEntity entity = (GemEntity)t.getTransferData(SingleGemEntityDataFlavor.getSingleGemEntityDataFlavor());

            AdvancedCALEditor editor = (AdvancedCALEditor)c;
            editor.getInputContext().endComposition();
            // TODO: If Gem Entities ever contain more than functional agents,
            // category needs to be updated
            SourceIdentifier.Category category = (entity.isDataConstructor()
                    ? SourceIdentifier.Category.DATA_CONSTRUCTOR
                    : SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
            editor.replaceSelection(" ");
            insertEditorQualification(editor, entity.getName(), category, userQualifiedIdentifiers, false);
            return true;

        }

        /**
         * Inserts the qualification into the editor text at the specified location.
         * 
         * If the qualification does not exist in the qualification map, or is
         * mapped to the same module, it can be inserted as unqualified and the
         * qualification map is updated with the corresponding module.
         * Otherwise, a fullly qualified name is inserted.
         * 
         * @param editor
         * @param completedName
         * @param identifierCategory
         */
        public static void insertEditorQualification(AdvancedCALEditor editor, QualifiedName completedName, SourceIdentifier.Category identifierCategory,
                CodeQualificationMap userQualifiedIdentifiers, boolean insertQualified) {
            // Do we have this in our map ?
            QualifiedName userMappedName = userQualifiedIdentifiers.getQualifiedName(completedName.getUnqualifiedName(), identifierCategory);
            if (userMappedName == null) {

                // No; add it to the map and insert unqualified name into code
                userQualifiedIdentifiers.putQualification(completedName.getUnqualifiedName(), completedName.getModuleName(), identifierCategory);
                if (insertQualified){
                    editor.replaceSelection(completedName.toString());
                }
                else{
                    editor.replaceSelection(completedName.getUnqualifiedName());
                }
            } else {

                // We have this identifier mapped already
                // If it's to the same module, enter as above; if not, enter
                // fully qualified name.
                if (!insertQualified && userMappedName.getModuleName().equals(completedName.getModuleName())) {
                    editor.replaceSelection(completedName.getUnqualifiedName());
                } else {
                    editor.replaceSelection(completedName.getQualifiedName());
                }
            }
        }

        /**
         * Imports data from a transferable containing a gem drawer
         * 
         * @param c
         * @param t
         * @return whether the import succeeded
         * @throws UnsupportedFlavorException
         * @throws IOException
         * @see javax.swing.TransferHandler#importData(javax.swing.JComponent,
         *      java.awt.datatransfer.Transferable)
         */
        private boolean importGemDrawer(JComponent c, Transferable t) throws UnsupportedFlavorException, IOException {
            String drawer = (String)t.getTransferData(GemDrawerSelection.getGemDrawerDataFlavor());

            // Add to code gem editor via the default transfer handler
            return textTransferHandler.importData(c, new StringSelection(" " + drawer + "."));
        }

        /**
         * @see javax.swing.TransferHandler#exportDone(javax.swing.JComponent,
         *      java.awt.datatransfer.Transferable, int)
         */
        @Override
        protected void exportDone(JComponent c, Transferable data, int action) {
            if (action == MOVE) {
                CodeTextTransferable.EditorInfo editorInfo = ((CodeTextTransferable)data).getEditorInfo();
                try {
                    editorInfo.getEditor().getDocument().remove(editorInfo.getOffsetStart(), editorInfo.getOffsetEnd() - editorInfo.getOffsetStart());
                } catch (BadLocationException e) {
                    throw new IllegalStateException("Cannot remove selected text because selection is invalid");
                }
            }
        }

        /**
         * @see javax.swing.TransferHandler#canImport(javax.swing.JComponent,
         *      java.awt.datatransfer.DataFlavor[])
         */
        @Override
        public boolean canImport(JComponent c, DataFlavor[] flavors) {
            for (final DataFlavor dataFlavor : flavors) {
                if (dataFlavor == CodeTextTransferable.codeStructureFlavor) {
                    return true;

                } else if (dataFlavor == SingleGemEntityDataFlavor.getSingleGemEntityDataFlavor()) {
                    return true;
                }
            }
            return textTransferHandler.canImport(c, flavors);
        }

        /**
         * Adds the entries from the specified map into the editor qualification
         * map, if these mappings do not already exists.
         * 
         * @param insertedMap
         *            map which is imported into our own
         * @param type
         *            type of entries to transfer
         */
        private void appendToEditorMap(CodeQualificationMap insertedMap, SourceIdentifier.Category type) {
            Set<String> names = insertedMap.getUnqualifiedNames(type);
            for (final String entityName : names) {
                if (userQualifiedIdentifiers.getQualifiedName(entityName, type) == null) {
                    userQualifiedIdentifiers.putQualification(entityName, insertedMap.getQualifiedName(entityName, type).getModuleName(), type);
                }
            }
        }
    }

    /**
     * Structure which holds this transferable's details
     */
    public static class CodeTextTransferable implements Transferable {

        /**
         * Structure holding the transfered data
         */
        static class CodeTextStructure {

            /** Visible code to transfer */
            private final String visibleCode;

            /** Fully qualified code */
            private final String fullyQualifiedCode;

            /** Qualification map for the visible code */
            private final CodeQualificationMap codeQualificationMap;

            /** Set of argument names appearing in code */
            private final Set<String> argumentNames;

            /** Constructor */
            CodeTextStructure(String visibleCode, String fullyQualifiedCode, CodeQualificationMap qualificationMap, Set<String> argumentNames) {
                this.fullyQualifiedCode = fullyQualifiedCode;
                this.visibleCode = visibleCode;
                this.codeQualificationMap = qualificationMap.makeCopy();
                this.argumentNames = new LinkedHashSet<String>(argumentNames);
            }

            // Accessors

            String getVisibleCode() {
                return visibleCode;
            }

            CodeQualificationMap getQualificationMap() {
                return codeQualificationMap;
            }

            Set<String> getArgumentNames() {
                return argumentNames;
            }

            String getFullyQualifiedCode() {
                return fullyQualifiedCode;
            }
        }

        /**
         * Information about the editor component which owns the text. This is
         * needed because in MOVE (ex: clipboard cut) operations, the transfer
         * handler must erase the transfered text from the editor
         * 
         * @author Iulian Radu
         */
        private static class EditorInfo {
            /** Actual component */
            private final JTextComponent editor;

            // Start and end offsets of text being transfered copied
            private final int offsetStart;

            private final int offsetEnd;

            /**
             * Constructor
             * 
             * @param editor
             * @param offsetStart
             * @param offsetEnd
             */
            public EditorInfo(JTextComponent editor, int offsetStart, int offsetEnd) {
                this.editor = editor;
                this.offsetStart = offsetStart;
                this.offsetEnd = offsetEnd;
            }

            /**
             * @return Returns the editor.
             */
            public JTextComponent getEditor() {
                return editor;
            }

            /**
             * @return Returns the offsetEnd.
             */
            public int getOffsetEnd() {
                return offsetEnd;
            }

            /**
             * @return Returns the offsetStart.
             */
            public int getOffsetStart() {
                return offsetStart;
            }
        }

        /** Transferable data */
        private final CodeTextStructure codeTextStructure;

        /** Editor information */
        private final EditorInfo editorInfoStructure;

        // Transferable flavors

        protected static final DataFlavor codeStructureFlavor = new DataFlavor(CodeTextStructure.class, "Code Text Structure");

        private final DataFlavor[] flavors = {DataFlavor.stringFlavor, codeStructureFlavor};

        /**
         * Creates a Transferable capable of transferring the specified code
         */
        public CodeTextTransferable(String codeText, String qualifiedText, CodeQualificationMap qualificationMap, 
                Set<String> argumentNames, JTextComponent editor, int offsetStart, int offsetEnd) {
            this.codeTextStructure = new CodeTextStructure(codeText, qualifiedText, qualificationMap, argumentNames);
            this.editorInfoStructure = new EditorInfo(editor, offsetStart, offsetEnd);
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
         */
        public DataFlavor[] getTransferDataFlavors() {
            return flavors.clone();
        }

        /**
         * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
         */
        public boolean isDataFlavorSupported(DataFlavor flavor) {

            for (final DataFlavor dataFlavor : flavors) {
                if (flavor.equals(dataFlavor)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
         */
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (flavor == null) {
                throw new NullPointerException();
            }

            if (flavor == flavors[0]) {
                return codeTextStructure.getFullyQualifiedCode();

            } else if (flavor == flavors[1]) {
                return codeTextStructure;

            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        EditorInfo getEditorInfo() {
            return editorInfoStructure;
        }
    }

    /**
     * CodeGemEditor constructor.
     * 
     * @param parent
     *            the parent of this code editor
     * @param codeGem
     *            the code gem to edit.
     * @param editHandler
     *            the handler for edits on the code gem.
     * @param perspective
     *            the current perspective.
     * @param typeStringProvider
     * @param navigatorOwner
     *            owner of metadata editor / viewer
     * @param metadataEditAllowed
     *            whether metadata editing is allowed
     * @param codeAnalyser
     *            object used to analyse the code
     */
    public CodeGemEditor(Frame parent, final CodeGem codeGem, EditHandler editHandler, Perspective perspective, 
            TypeStringProvider typeStringProvider, NavFrameOwner navigatorOwner, boolean metadataEditAllowed, CodeAnalyser codeAnalyser) {

        super(parent);
        this.codeGem = codeGem;
        this.perspective = perspective;
        this.codeAnalyser = codeAnalyser;
        this.typeStringProvider = typeStringProvider;
        this.editHandler = editHandler;

        Argument.NameTypePair[] args = codeGem.getArguments();
        if (args.length > 0) {
            keepInputsInNaturalOrder = false;
        }

        varNamesWhichAreArgs = new LinkedHashSet<String>();
        for (final NameTypePair arg : args) {
            varNamesWhichAreArgs.add(arg.getName());
        }

        userQualifiedIdentifiers = codeGem.getQualificationMap().makeCopy();

        // Add metadata view/edit support
        this.navigatorOwner = navigatorOwner;
        this.metadataEditAllowed = metadataEditAllowed;

        // Set up the gem code panel
        gemCodeSyntaxListener = new GemCodeSyntaxListener(perspective);
        gemCodePanel = new GemCodePanel(codeGem, gemCodeSyntaxListener, perspective);
        gemCodePanel.setName("GemCodePanel");

        // Set up the content pane
        dialogContentPane = new JPanel();
        dialogContentPane.setName("JDialogContentPane");
        dialogContentPane.setLayout(new CardLayout());
        dialogContentPane.add(gemCodePanel, gemCodePanel.getName());

        // Now set up this editor
        setName("CodeGemEditor");
        setSize(600, 380);
        setContentPane(dialogContentPane);

        final AutoCompleteManager autoCompleteManager = new AutoCompleteManager(this, perspective);

        // Update the variables display area, so that the type change listener
        // update of this area doesn't screw up on preconditions.
        updateVariablesDisplay();

        // Add a listener to the CAL editor that will close this code editor
        // when ESC is pressed
        //   and invoke autocomplete on ctrl-space.
        gemCodePanel.getCALEditorPane().addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    processWindowEvent(new WindowEvent(CodeGemEditor.this, WindowEvent.WINDOW_CLOSING));
                }
                // The gesture is control - space - a la Eclipse et al.
                if ((e.isControlDown()) && (e.getKeyCode() == KeyEvent.VK_SPACE)) {
                    // Show the autocomplete popup just underneath the cursor
                    try {
                        autoCompleteManager.showCodeEditorPopupMenu();
                    } catch (AutoCompletePopupMenu.AutoCompleteException ex) {
                        gemCodePanel.setErrorMessage("No Autocomplete Available");
                    }
                }
            }
        });

        // Add a listener to activate the gem code panel when we activate this
        // frame
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                // Pass focus to the GemCodePanel
                gemCodePanel.requestFocus();
            }
        });

        // add listeners for burn and definition events on the gem
        codeGem.addBurnListener(new BurnListener() {
            public void burntStateChanged(BurnEvent e) {
                // make sure the correct variable panel types show up
                if (smartsActivated) {
                    doSyntaxSmarts();
                }
            }
        });
        // Add a listener to update the type display when the code gem types change.
        codeGem.addTypeChangeListener(new TypeChangeListener() {
            public void typeChanged(TypeChangeEvent e) {
                Gem.PartConnectable partChanged = e.getPartChanged();

                if (partChanged instanceof Gem.PartInput) {
                    updateVariablesDisplay(((Gem.PartInput)partChanged).getInputNum());

                } else if (partChanged instanceof Gem.PartOutput) {
                    updateOutputTypePanel();
                }
            }
        });

        // Set up event listeners for Variable Display

        gemCodePanel.getVariablesDisplay().addPanelEventListener(new VariablesDisplay.PanelEventListener() {

            public void panelShifted(int argIndex, int shiftAmount) {

                StateEdit stateEdit = null;
                PartInput[] oldInputs = null;
                if (recordingEditStates) {
                    stateEdit = new StateEdit(CodeGemEditor.this, GemCutter.getResourceString("UndoText_ReorderInputs"));
                    oldInputs = codeGem.getInputParts();
                }

                keepInputsInNaturalOrder = false;
                shiftInput(argIndex, shiftAmount);
                updateVariablesDisplay();

                if (recordingEditStates) {
                    stateEdit.end();
                    if (CodeGemEditor.this.editHandler != null) {
                        CodeGemEditor.this.editHandler.definitionEdited(CodeGemEditor.this, oldInputs, stateEdit);
                    }
                }
            }

            public void panelTypeIconDoubleClicked(VariablePanel variablePanel) {
                AdvancedCALEditor.PositionlessIdentifier identifier = variablePanel.getIdentifier();
                if (!identifier.getQualificationType().isCodeQualified() && isArgumentFormChangeAllowed(identifier.getName())) {

                    // Switch to default module
                    changeArgumentToDefaultFunction(identifier.getName());
                }
            }
        });

        // Set up listeners for Qualifications Display
        gemCodePanel.getQualificationsDisplay().addPanelEventListener(new QualificationsDisplay.PanelEventListener() {

            public void panelTypeIconDoubleClicked(QualificationPanel panel) {
                AdvancedCALEditor.PositionlessIdentifier identifier = panel.getIdentifier();
                if (!identifier.getQualificationType().isCodeQualified() && isQualificationFormChangeAllowed(identifier.getCategory())) {

                    changeQualificationToArgument(identifier.getName(), identifier.getResolvedModuleName());
                }
            }

            public void panelModuleLabelDoubleClicked(QualificationPanel panel, Point mousePoint) {
                popupMenuProvider.showMenu(panel.getIdentifier(), mousePoint, gemCodePanel.getQualificationsDisplay().getListComponent());
            }

        });

        // Add popup menu providers
        PopupMenuProvider popupProvider = new PopupMenuProvider();
        gemCodePanel.getQualificationsDisplay().setPopupMenuProvider(popupProvider);
        gemCodePanel.getVariablesDisplay().setPopupMenuProvider(popupProvider);
        gemCodePanel.getCALEditorPane().setPopupMenuProvider(popupProvider);

        // Enable dragging between displays
        gemCodePanel.getQualificationsDisplay().setDragEnabled(true);
        gemCodePanel.getQualificationsDisplay().setListTransferHandler(new QualificationsDisplayTransferHandler());
        gemCodePanel.getVariablesDisplay().setDragEnabled(true);
        gemCodePanel.getVariablesDisplay().setListTransferHandler(new VariablesDisplayTransferHandler());

        // Enable special copy/paste
        editorTransferHandler = new EditorTextTransferHandler(gemCodePanel.getCALEditorPane().getTransferHandler(), codeAnalyser);
        gemCodePanel.getCALEditorPane().setTransferHandler(editorTransferHandler);
        editorTransferHandler.setArgumentNames(varNamesWhichAreArgs);
        editorTransferHandler.setUserQualifiedIdentifiers(userQualifiedIdentifiers);

        // Create a timer for the periodic parse, but only once when the timer elapses
        parseTimer = new Timer(PARSE_TIMER_PERIOD, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // Check it's from our friendly timer object
                if (e.getSource() == parseTimer) {
                    updateGemForTextChange();
                }
            }
        });
        parseTimer.setRepeats(false);

        // Add the listener for document change events in the Editor
        gemCodePanel.getCALEditorPane().getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            public void changedUpdate(DocumentEvent e) {
                textChanged();
            }
        });

        // Ignore the fact that the code gem isn't good, to update the "last good" state.
        updateLastGoodState();
    }

    /**
     * To be called when the text of the code gem changes. Records the change, performs
     * syntax smarts, and posts the edit to the edit handler.
     */
    private void updateGemForTextChange() {
        // Get the code gem change.
        StateEdit stateEdit = null;
        PartInput[] oldInputs = null;
        if (recordingEditStates) {
            stateEdit = new StateEdit(CodeGemEditor.this, GemCutter.getResourceString("UndoText_CodeChange"));
            oldInputs = codeGem.getInputParts();
        }

        // Update the code gem.
        doSyntaxSmarts();

        // Post edit state.
        if (recordingEditStates) {
            stateEdit.end();

            // Post the edit to the edit handler.
            if (CodeGemEditor.this.editHandler != null) {
                CodeGemEditor.this.editHandler.definitionEdited(CodeGemEditor.this, oldInputs, stateEdit);
            }
        }
    }

    /**
     * Sets whether there should be a delay after the text changes before updateGemForTextChange()
     * is called. Under normal conditions this should be true so that the method is not called for every keystroke
     * as a user interactively updates the text. However, this should be to false if the call to updateGemForTextChange()
     * needs to be called synchronously (ie. if the change is part of a programmatic compound edit).
     * 
     * @param shouldDelay True if there should be a delay, false otherwise
     */
    void setDelayUpdatingForTextChanges(boolean shouldDelay) {
        this.delayUpdatingForTextChanges = shouldDelay;
    }
    
    /**
     * What to do when the text is changed. If not restoring, reset the timer
     * which will result in us checking everything when the user rests for a
     * while
     */
    private void textChanged() {
        if (smartsActivated) {
            if (delayUpdatingForTextChanges) {
                parseTimer.restart();
            } else {
                updateGemForTextChange();
            }
        }
    }

    /**
     * Return the code gem for this editor.
     * 
     * @return CodeGem the code gem for this editor.
     */
    public final CodeGem getCodeGem() {
        return codeGem;
    }
    
    void setCodeAnalyser(CodeAnalyser codeAnalyser) {
        this.codeAnalyser = codeAnalyser;
    }

    /**
     * returns the code gem panel
     * 
     * @return GemCodePanel
     */
    GemCodePanel getGemCodePanel() {
        return gemCodePanel;
    }

    /**
     * Find out what the new arguments should be to reflect a definition change.
     * 
     * @param argNamesFromCode
     *            the argument names from the code, in code order
     * @param newCodeGemType
     *            the new type of the sc defined by the code gem
     * @return Argument[] the new arguments after the definition change.
     */
    private Argument.NameTypePair[] getNewCodeGemArgs(String[] argNamesFromCode, TypeExpr newCodeGemType) {

        // Make an argument list
        int argCount = argNamesFromCode.length;
        Argument.NameTypePair[] argsFromCode = new Argument.NameTypePair[argCount];
        Map <String, NameTypePair>newNameToArgMap = new HashMap<String, NameTypePair>();

        if (newCodeGemType != null) {
            TypeExpr[] typePieces = newCodeGemType.getTypePieces();
            for (int i = 0; i < argCount; i++) {
                argsFromCode[i] = new Argument.NameTypePair(argNamesFromCode[i], typePieces[i]);
                newNameToArgMap.put(argNamesFromCode[i], argsFromCode[i]);
            }

        } else {
            // No type - broken gem.
            for (int i = 0; i < argCount; i++) {
                argsFromCode[i] = new Argument.NameTypePair(argNamesFromCode[i], null);
                newNameToArgMap.put(argNamesFromCode[i], argsFromCode[i]);
            }
        }

        Argument.NameTypePair[] currentArgs = codeGem.getArguments();
        int numCurrentArgs = currentArgs.length;

        // Create a set of arg names
        final List<NameTypePair>argsInOrderList;
        if (keepInputsInNaturalOrder) {
            argsInOrderList = new ArrayList<NameTypePair>(Arrays.asList(argsFromCode));

            // Add connected arguments not in the code. The nth arg is
            // associated with the nth input
            for (int i = 0; i < numCurrentArgs; i++) {
                String inputArgName = currentArgs[i].getName();
                PartInput input = oldNameToInputMap.get(inputArgName);

                if (input != null && input.isConnected() && !newNameToArgMap.containsKey(inputArgName)) {
                    argsInOrderList.add(new Argument.NameTypePair(inputArgName, TypeExpr.makeParametricType()));
                }
            }

        } else {

            // Try to preserve the last order as much as possible, and add new inputs to the end.
            // Args in order = args present in new args + new args not present in old args
            argsInOrderList = new ArrayList<NameTypePair>();

            // We also want the new name to arg map to include names for connected args not in the code
            //   so that we don't consider it a new arg and add it to the end.
            for (int i = 0; i < numCurrentArgs; i++) {
                String inputArgName = currentArgs[i].getName();
                PartInput input = oldNameToInputMap.get(inputArgName);

                if (input != null && input.isConnected() && !newNameToArgMap.containsKey(inputArgName)) {
                    newNameToArgMap.put(inputArgName, new Argument.NameTypePair(inputArgName, TypeExpr.makeParametricType()));
                }
            }

            // The code args which are new. We'll remove old args from this.
            List<NameTypePair> newArgs = new ArrayList<NameTypePair>(Arrays.asList(argsFromCode));

            // Add args present from lastArgs. Also remove old args from the new args list.
            for (final NameTypePair lastArg : lastArguments) {

                Argument.NameTypePair newLastArg = newNameToArgMap.get(lastArg.getName());
                if (newLastArg != null) {
                    argsInOrderList.add(newLastArg);

                    // Search through the last args for the one to remove.
                    for (final Argument.NameTypePair newArg : newArgs){
                        if (newArg.getName().equals(lastArg.getName())) {
                            newArgs.remove(newArg); // not a new arg - remove from new args
                            break; // ConcurrentModificationException is avoided since the iterate
                        }
                    }
                }
            }

            // Now add new args to the end
            argsInOrderList.addAll(newArgs);
        }

        // Convert to an array
        Argument.NameTypePair[] argsInOrder = new Argument.NameTypePair[argsInOrderList.size()];
        argsInOrderList.toArray(argsInOrder);

        return argsInOrder;
    }

    /**
     * Check whether the current output type is compatible with its connection.
     * 
     * @param typeCheckInfo
     *            the info to use to check the connectivity of the output.
     * @return if non-null, the current output type is incompatible with the
     *         inferred type of the output. The inferred type is returned.
     */
    private TypeExpr checkOutputForConnectivity(TypeCheckInfo typeCheckInfo) {
        // Determine whether the output is compatibly-connected.
        if (!codeGem.isRootGem() && codeGem.getOutputPart().isConnected()) {

            TypeExpr inferredOutputType = codeGem.getOutputPart().inferType(typeCheckInfo);

            if (!GemGraph.typesWillUnify(inferredOutputType, getOutputType(), typeCheckInfo)) {
                return inferredOutputType;
            }
        }
        return null;
    }

    /**
     * Update the given code gem editor's state according to the validity of its
     * connections.
     * 
     * TODOEL: this type of bookkeeping should be handled by the code gem
     * (editor) owner. The next best call by clients should be to
     * updateIncompatibleParts().
     * 
     * @param typeCheckInfo
     *            the type check info object used to check connectivity of
     *            connections.
     * @param valueEditorManager
     *            the value editor manager used to check providability of
     *            arguments.
     */
    public void updateForConnectivity(TypeCheckInfo typeCheckInfo, ValueEditorManager valueEditorManager) {

        Map<PartConnectable, TypeExpr> incompatiblyConnectedPartToInferredTypeMap = new HashMap<PartConnectable, TypeExpr>();

        // Determine whether the output is compatibly-connected.
        TypeExpr incompatibleInferredOutputType = checkOutputForConnectivity(typeCheckInfo);
        if (incompatibleInferredOutputType != null) {
            incompatiblyConnectedPartToInferredTypeMap.put(codeGem.getOutputPart(), incompatibleInferredOutputType);
        }

        // Find incompatibly-connected inputs.
        Argument.NameTypePair[] args = codeGem.getArguments();
        for (int i = 0; i < args.length; i++) {

            // Skip if not connected.
            PartInput input = codeGem.getInputPart(i);
            if (!input.isConnected()) {
                continue;
            }

            // If there's no arg type, this must be an "orphaned" arg (ie.
            // doesn't appear in the source).
            TypeExpr argType = args[i].getType();
            if (argType == null) {
                continue;
            }

            // Broken if the arg type doesn't unify with its inferred type, or
            // if the attached gem
            // is a value gem and the value system is unable to handle the type
            // of the argument.
            TypeExpr inferredInputType = input.inferType(typeCheckInfo);
            if (!GemGraph.typesWillUnify(argType, inferredInputType, typeCheckInfo)
                    || (input.getConnectedGem() instanceof ValueGem && !valueEditorManager.canInputDefaultValue(argType))) {

                incompatiblyConnectedPartToInferredTypeMap.put(input, inferredInputType);
            }
        }

        // Update the code gem editor.
        updateIncompatibleParts(incompatiblyConnectedPartToInferredTypeMap);
    }

    /**
     * Update the internal set of connectable parts on the code gem whose
     * connections are incompatible with their definitions. TODOEL: remove. This
     * is requires a call back from the code gem editor's owner after an edit.
     * External brokenness info should really be retained/handled externally!
     * 
     * @param incompatiblePartToInferredTypeMap
     *            Map from incompatibly-connected part to its inferred type.
     */
    void updateIncompatibleParts(Map<PartConnectable, TypeExpr> incompatiblePartToInferredTypeMap) {
        // (slow) check for validity
        for (final Map.Entry<PartConnectable, TypeExpr> mapEntry : incompatiblePartToInferredTypeMap.entrySet()) {
            Gem.PartConnectable codeGemPart = mapEntry.getKey();
            if (codeGemPart.getGem() != codeGem) {
                throw new IllegalArgumentException("Incompatibly-connected parts must come from the edited code gem.");
            }
            if (!codeGem.isConnected()) {
                throw new IllegalArgumentException("Attempt to add an unconnected part to the set of incompatibly-connected parts .");
            }
            if (!(mapEntry.getValue() instanceof TypeExpr)) {
                throw new IllegalArgumentException("Parts must map to types.");
            }
        }

        // Update the set of parts.
        this.incompatiblePartToInferredTypeMap.clear();
        this.incompatiblePartToInferredTypeMap.putAll(incompatiblePartToInferredTypeMap);

        // Update whether the code gem should be broken, based on its current
        // brokenness and its connections.
        boolean shouldBreak = codeGem.isBroken() || !incompatiblePartToInferredTypeMap.isEmpty();
        codeGem.setBroken(shouldBreak);

        // Update the display.
        updateVariablesDisplay();
        updateOutputTypePanel();
    }

    /**
     * Return whether a given code gem input is unused.
     * 
     * @param codeGemInput
     *            the input on the code gem edited by this editor.
     * @return whether a given code gem input is unused.
     */
    public boolean isUnusedArg(Gem.PartInput codeGemInput) {
        if (codeGemInput.getGem() != codeGem || !codeGemInput.isValid()) {
            throw new IllegalArgumentException("Input must be from the edited code gem.");
        }
        return unusedArgNames.contains(codeGem.getArguments()[codeGemInput.getInputNum()].getName());
    }

    /**
     * Update the state of the code gem and this editor after a burn action
     * takes place.
     * 
     * @param typeCheckInfo
     */
    public void updateForBurn(TypeCheckInfo typeCheckInfo) {
        // We have to:
        //   recalculate whether an output connection is broken.
        //   refresh input and output panels.

        // Calculate the new output type.
        TypeExpr outputType = getOutputType();
        if (outputType == null) {
            // This is a broken code gem.  
            // Burning should be disallowed on the connected gem subtrees, and so shouldn't affect this gem.
            return;
        }

        // Create a new incompatibility map.
        Map<PartConnectable, TypeExpr> newIncompatiblePartToInferredTypeMap = new HashMap<PartConnectable, TypeExpr>(incompatiblePartToInferredTypeMap);
        TypeExpr incompatibleInferredOutputType = checkOutputForConnectivity(typeCheckInfo);
        if (incompatibleInferredOutputType != null) {
            newIncompatiblePartToInferredTypeMap.put(codeGem.getOutputPart(), incompatibleInferredOutputType);
        } else {
            newIncompatiblePartToInferredTypeMap.remove(codeGem.getOutputPart());
        }

        // Update for incompatibility. This call will also refresh the input panels.
        updateIncompatibleParts(newIncompatiblePartToInferredTypeMap);
    }

    /**
     * Get the output type from the code gem. This adds burned types to the code result type expression.
     * 
     * @return TypeExpr the derived output type
     */
    private TypeExpr getOutputType() {
        TypeExpr codeResultType = codeGem.getCodeResultType();

        // if there is no type, the output type is null!
        if (codeResultType == null) {
            return null;
        }

        // The result type is (burnt args + arrows) -> codeResultType
        TypeExpr outputType = codeResultType;

        // calculate which params are burnt
        Argument.NameTypePair[] args = codeGem.getArguments();
        for (int i = args.length - 1; i > -1; i--) {
            Gem.PartInput input = codeGem.getInputPart(i);
            if (input.isBurnt()) {
                TypeExpr inputType = args[i].getType();
                outputType = TypeExpr.makeFunType(inputType, outputType);
            }
        }

        // Return the output type
        return outputType;
    }

    /**
     * Perform all the syntax directed smarts.
     */
    public void doSyntaxSmarts() {

        // Get the body text, which will be our RHS SC expression
        String codeGemBodyText = gemCodePanel.getCode();

        // Analyze the code.

        // We analyze using the userQualifiedIdentifiers since symbols
        // which we have previously selected a module for may appear in the code.
        // The map in the qualification results will have unnecessary entries removed,
        // and this is what we store in the codegem.

        CodeQualificationMap currentQualificationMap = userQualifiedIdentifiers;        
        CodeAnalyser.AnalysisResults results = codeAnalyser.analyseCode(codeGemBodyText, varNamesWhichAreArgs, currentQualificationMap);

        // Add any new arguments to the set of existing argument names
        varNamesWhichAreArgs.addAll(Arrays.asList(results.getAllArgumentNames()));

        // Note: If the analysis is not successful (ie: parsing failed), the
        // code gem will be broken and appropriate errors will be displayed.

        TypeExpr codeType = results.getTypeExpr();

        // Find the new arguments in the appropriate order.
        Argument.NameTypePair[] newCodeGemArgs = getNewCodeGemArgs(results.getAllArgumentNames(), codeType);

        // Arguments which are unused in the analyzed code have been added; so
        // update the type expression accordingly.
        if (codeType != null) {
            for (int i = results.getAllArgumentNames().length; i < newCodeGemArgs.length; i++) {
                codeType = TypeExpr.makeFunType(TypeExpr.makeParametricType(), codeType);
            }
        }

        // Get the output type
        TypeExpr codeResultType = codeType == null ? null : codeType.dropFirstNArgs(newCodeGemArgs.length);

        // Update the code gem
        codeGem.definitionUpdate(results.getQualifiedCode(), newCodeGemArgs, codeResultType, oldNameToInputMap, results.getQualificationMap(), codeGemBodyText);

        // Update "last good" state if the code type is ok.
        if (codeType != null) {
            updateLastGoodState();
        }

        // Update internal argument info (arg names which unused, or appear in
        // unqualified form).
        updateArgumentInfo(results);

        // Update the qualifications display area
        updateQualificationsDisplay(results);

        // Update the editor panel
        updateEditorPanel(results);

        // Update the error message.
        updateErrorMessage(results);

        // Update the variable and output display areas.
        updateVariablesDisplay();
        updateOutputTypePanel();
    }

    /**
     * A helper function that displays a compiler message.
     * 
     * @param results
     */
    private void updateErrorMessage(CodeAnalyser.AnalysisResults results) {

        List<OffsetCompilerMessage> messages = results.getCompilerMessages();

        gemCodePanel.clearErrorIndicators();

        for (final OffsetCompilerMessage message : messages) {
            gemCodePanel.addErrorIndicator(message);
        }
    }

    /**
     * Update the code gem panel to show the output type.
     */
    private void updateOutputTypePanel() {

        TypeExpr outputType = getOutputType();

        // Do we have a code result type?
        if (outputType != null) {

            ScopedEntityNamingPolicy namingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(
                    perspective.getWorkingModuleTypeInfo());

            if (incompatiblePartToInferredTypeMap.containsKey(codeGem.getOutputPart())) {
                
                // Doesn't match with connected output
                String outputTypeString = typeStringProvider.getTypeString(outputType, namingPolicy);
                displayOutputType(outputTypeString, false);

            } else {
                // No constraint, or output matches

                // Update the type string of the output to use the new TypeVar
                // map generated during definitionUpdate
                TypeExpr outputPartType = codeGem.getOutputPart().getType();
                String outputTypeString = (outputPartType == null) ? ""
                        : "<i>" + typeStringProvider.getTypeString(outputPartType, namingPolicy) + "</i>";
                displayOutputType(outputTypeString, true);
            }
        } else {
            // no type
            displayOutputType("", true);
        }
    }

    /**
     * Display the type for the output.
     * 
     * @param typeOrMessage the text to display in the type label. This can contain html tags.
     * @param notBadOutputConnection
     *    false if there is an output type which doesn't match
     *    the connected output (else the connection breaks the gem)
     */
    private void displayOutputType(String typeOrMessage, boolean notBadOutputConnection) {
        
        // Is the output connected?
        boolean outputLocked = (codeGem.getOutputPart().isConnected());

        String typeText;
        String toolTipText;

        // Depends whether we're setting or unsetting
        if (notBadOutputConnection) {

            // for now this just means that the output is connected
            if (outputLocked && !codeGem.isRootGem()) {
                typeText = GemCutter.getResourceString("CGE_Connected");
            } else {
                if (typeOrMessage.length() == 0) {
                    typeOrMessage = GemCutter.getResourceString("CGE_Undefined_Type");
                }
                typeText = "-> " + typeOrMessage;
            }

            // Clear tooltip
            toolTipText = null;

        } else {
            // Set the label and its background colour
            typeText = "-> " + GemCutter.getResourceString("CGE_Type_Clash");

            // Set a tooltip to indicate the exact problem
            TypeExpr inferredType = incompatiblePartToInferredTypeMap.get(codeGem.getOutputPart());
            if (inferredType == null) {
                toolTipText = GemCutterMessages.getString("CGE_BrokenSubtreeToolTip");

            } else if (typeOrMessage.length() > 0) {
                toolTipText = GemCutterMessages.getString("CGE_WrongTypeToolTip", inferredType.toString(), typeOrMessage);

            } else {
                toolTipText = GemCutterMessages.getString("CGE_UndefinedTypeToolTip", inferredType.toString());
            }
        }

        // Wrap in html formatting tags.
        // Use a slightly smaller bold monospace font.
        typeText = "<html><body><TT><b><FONT SIZE=\"-1\">" + typeText + "</FONT></b></TT></body></html>";

        gemCodePanel.updateOutputTypeLabel(typeText, toolTipText, outputLocked, notBadOutputConnection);
    }

    /**
     * Update the internal argument info for the new code gem state.
     * 
     * @param results
     *            results of code analysis, for displaying fully qualified
     *            arguments.
     */
    private void updateArgumentInfo(CodeAnalyser.AnalysisResults results) {

        // Update unused args.
        this.unusedArgNames.clear();
        Set<String> usedArgNames = new HashSet<String>(Arrays.asList(results.getAllArgumentNames()));
        for (final String argName : varNamesWhichAreArgs) {
            if (!usedArgNames.contains(argName)) {
                unusedArgNames.add(argName);
            }
        }

        // Update the names of arguments which appear in unqualified form in code
        this.unqualifiedArgNames.clear();
        for (final AnalysedIdentifier identifier : results.getAnalysedIdentifiers()) {
            if (identifier.getQualificationType() == QualificationType.UnqualifiedArgument) {
                unqualifiedArgNames.add(identifier.getName());
            }
        }
    }

    /**
     * Update the variables display area according to the current state.
     */
    private void updateVariablesDisplay() {
        Argument.NameTypePair[] args = codeGem.getArguments();

        // Create argument variable panels
        int numArgVarPanels = args.length;
        VariablePanel[] argVarPanels = new VariablePanel[numArgVarPanels];
        for (int i = 0; i < numArgVarPanels; i++) {
            argVarPanels[i] = getVariablePanel(i);
        }

        // Reordering is allowed if the code gem's result type is ok.
        // Limiting reordering to instances where the code gem is not broken is
        // too constraining..
        boolean reorderingAllowed = (codeGem.getCodeResultType() != null);

        gemCodePanel.getVariablesDisplay().updateVariablePanels(argVarPanels, reorderingAllowed);
    }

    /**
     * Update the variable display to show the updated panel at the given panel index.
     * @param panelIndex the index of the panel to update.
     */
    private void updateVariablesDisplay(int panelIndex) {
        VariablePanel updatedVariablePanel = getVariablePanel(panelIndex);
        gemCodePanel.getVariablesDisplay().updateVariablePanel(panelIndex, updatedVariablePanel);
    }

    /**
     * Get the variable panel corresponding to a given variable index.
     * 
     * @param argIndex
     *            the index of the panel to return
     * @return the corresponding panel.
     */
    private VariablePanel getVariablePanel(int argIndex) {
        String varName = codeGem.getArguments()[argIndex].getName();
        QualificationType qualificationType = QualificationType.UnqualifiedArgument;

        Argument.Status argStatus = getArgStatus(argIndex);
        String typeText = getArgTypeText(codeGem.getInputPart(argIndex).getType(), argStatus);
        String toolTipText = getArgToolTipText(argIndex, argStatus);

        VariablePanel varPan = new VariablePanel(
                new AdvancedCALEditor.PositionlessIdentifier(
                        varName,
                        null,
                        null,
                        null,
                        SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD, qualificationType), typeText, toolTipText, argStatus);

        return varPan;
    }

    /**
     * Update the qualifications display area for the new code gem state.
     * @param results results of code analysis (null if none was performed)
     */
    private void updateQualificationsDisplay(
            CodeAnalyser.AnalysisResults results) {
        // Retrieve all mapped names from the code gem qualification map, sort the names
        // alphabetically in each category, and build the qualification panels.
        gemCodePanel.getQualificationsDisplay().generateQualificationPanels(
                codeGem.getQualificationMap(),
                (results == null ? null : results.getAnalysedIdentifiers()),
                perspective.getWorkingModuleTypeInfo());
    }

    /**
     * Update the editor area for the new code gem state. This updates the
     * qualification map used in the panel so that tooltips are displayed
     * properly, and updates the chromacoder if analysis was successful
     * 
     * @param results
     */
    private void updateEditorPanel(CodeAnalyser.AnalysisResults results) {
        gemCodePanel.setSourceIdentifiers(results.getAnalysedIdentifiers());
        gemCodePanel.updateAmbiguityIndicators();

        if (results.analysisSuccessful()) {
            updateChromacoding(results);
        }
        // Update the text editor colors after the analysis by repainting
        gemCodePanel.getCALEditorPane().repaint();
    }

    /**
     * Updates the chromacoder to color the proper arguments and local variables
     * 
     * @param analysisResults
     */
    private void updateChromacoding(CodeAnalyser.AnalysisResults analysisResults) {
        List<String> argumentNames = new ArrayList<String>(Arrays.asList(analysisResults.getAllArgumentNames()));
        Collections.sort(argumentNames);
        String[] sortedArgumentNamesArray = new String[argumentNames.size()];
        argumentNames.toArray(sortedArgumentNamesArray);

        gemCodeSyntaxListener.setArgumentNames(sortedArgumentNamesArray);
        gemCodeSyntaxListener.setLocalVariableNames(getLocalVariableNames(analysisResults));
    }

    /**
     * Scan the analysed identifiers and retrieve the names of all declared
     * local variables The resulting local variable names do not contain
     * duplicates and are sorted alphabetically.
     * 
     * @param analysisResults
     * @return list of variable names
     */
    private String[] getLocalVariableNames(
            CodeAnalyser.AnalysisResults analysisResults) {

        List<String> variableNames = new ArrayList<String>();
        for (final AnalysedIdentifier identifier : analysisResults.getAnalysedIdentifiers()) {
            if (identifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION && !variableNames.contains(identifier.getName())) {
                variableNames.add(identifier.getName());
            }
        }
        Collections.sort(variableNames);
        String[] variableNamesArray = new String[variableNames.size()];
        variableNames.toArray(variableNamesArray);

        return variableNamesArray;
    }

    /**
     * Get the status of a given argument.
     * 
     * @param argNum
     *            the index of the argument
     * @return Argument.Status the status of the given argument.
     */
    private Argument.Status getArgStatus(int argNum) {

        Argument.NameTypePair[] arguments = codeGem.getArguments();
        PartInput input = codeGem.getInputPart(argNum);

        if (incompatiblePartToInferredTypeMap.containsKey(input)) {
            // Check if it's an incompatible arg.
            return Argument.Status.TYPE_CLASH;

        } else if (input.isConnected()) {
            // Check whether the arg is used (appears in the code).
            if (unusedArgNames.contains(arguments[argNum].getName())) {
                return Argument.Status.CONNECTED_UNUSED;
            }
            return Argument.Status.CONNECTED;

        } else if (input.isBurnt()) {
            // also no type if the input is burnt
            return Argument.Status.BURNT;

        } else if (input.getType() != null) {
            // Return the type from the input
            return Argument.Status.NATURAL;

        } else {
            // Unconnected, unburnt input has no type.
            return Argument.Status.TYPE_UNDEFINED;
        }
    }

    /**
     * Get the type text for a given argument. This takes the state of the code
     * gem (eg. connection, burn info) into account.
     * 
     * @param argType
     *            the type of the argument
     * @param argStatus
     *            the status of the argument
     * @return the type text for the given argument.
     */
    private String getArgTypeText(TypeExpr argType, Argument.Status argStatus) {

        if (argStatus == Argument.Status.TYPE_UNDEFINED) {
            return GemCutter.getResourceString("CGE_Undefined_Type");

        } else if (argStatus == Argument.Status.BURNT) {
            return GemCutter.getResourceString("CGE_Burnt_Input");

        } else if (argStatus == Argument.Status.CONNECTED) {
            return GemCutter.getResourceString("CGE_Connected");

        } else if (argStatus == Argument.Status.CONNECTED_UNUSED) {
            return GemCutter.getResourceString("CGE_Unused_Connected");

        } else if (argStatus == Argument.Status.TYPE_CLASH) {
            return GemCutter.getResourceString("CGE_Type_Clash");
        }

        ScopedEntityNamingPolicy namingPolicy = new ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous(perspective.getWorkingModuleTypeInfo());

        // Must be in natural state. Check for (transiently) null type..
        if (argType == null) {
            return "null";
        }
        // Return the type string.
        return typeStringProvider.getTypeString(argType, namingPolicy);
    }

    /**
     * Get the tooltip text for a given argument. This takes the state of the
     * code gem (eg. connection, burn info) into account.
     * 
     * @param argNum
     *            the index of the argument
     * @param argStatus
     *            the status of the argument
     * @return the tooltip text for the given argument.
     */
    private String getArgToolTipText(int argNum, Argument.Status argStatus) {

        Argument.NameTypePair[] arguments = codeGem.getArguments();
        String argName = arguments[argNum].getName();
        
        StringBuilder text = new StringBuilder();
        text.append("<html><body>");
            
        if (argStatus != Argument.Status.TYPE_CLASH) {
            TypeExpr argType = codeGem.getInputPart(argNum).getType();
            String argTypeText = getArgTypeText(argType, argStatus);
            boolean separateText = false;
            
            // Check if argument name / type lines must be separated
            {
                String completeText = argName + " :: " + argTypeText;
                if (ToolTipHelpers.wrapTextToHTMLLines(completeText, gemCodePanel).length() != completeText.length()) {
                    // Text cut because name or type is over the tooltip limit;
                    // so display each on separate lines
                    separateText = true;
                }
            }
            
            text.append("<b>" + ToolTipHelpers.wrapTextToHTMLLines(argName, gemCodePanel) + "</b> :: ");
            if (separateText) {
                text.append("<br>");
            }
            text.append("<i>" + ToolTipHelpers.wrapTextToHTMLLines(argTypeText, gemCodePanel) + "</i>");

        } else {
            PartInput input = codeGem.getInputPart(argNum);
            TypeExpr argType = arguments[argNum].getType();
            TypeExpr inferredInputType = incompatiblePartToInferredTypeMap.get(input);
            
            text.append(GemCutterMessages.getString("CGE_WrongArgTypeToolTip", 
                        ToolTipHelpers.wrapTextToHTMLLines(inferredInputType.toString(), gemCodePanel), 
                        ToolTipHelpers.wrapTextToHTMLLines(argType.toString(), gemCodePanel)));
        }
        
        text.append("</body></html>");
        return text.toString();
    }
    /**
     * Update "last good" state if the code gem is now good. This updates
     * internal variables needed to keep track of input reorder info across
     * intermediate broken codegem states.
     */
    private void updateLastGoodState() {
        lastArguments = new ArrayList<NameTypePair>(Arrays.asList(codeGem.getArguments()));
        oldNameToInputMap = codeGem.getArgNameToInputMap();
    }

    /**
     * Return whether changing the form of an argument to a qualification is
     * allowed
     * 
     * @param name
     *            the name of the variable
     * @return True if argument change form is allowed; False if not
     */
    private boolean isArgumentFormChangeAllowed(String name) {

        // Disallow if the variable is connected.
        PartInput input = oldNameToInputMap.get(name);
        if (input != null && input.isConnected()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns whether changing the form of a qualification to an argument is allowed
     * 
     * @param currentForm
     *            current form of the qualification panel
     * @return True if form change is allowed; False if not
     */
    private boolean isQualificationFormChangeAllowed(SourceIdentifier.Category currentForm) {
        return (currentForm == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
    }

    /**
     * Change the form of an argument to a default function.
     * 
     * The argument form: - if it had been previously qualified to a function,
     * will become this function - if can be resolved to a function from am
     * imported module, will become the first such resolving function - if
     * cannot be resolved, will remain the same
     * 
     * This action occurs on double click of argument panel icons, or argument
     * drag into qualification panel.
     * 
     * @param argumentName
     * @return whether argument was transformed to function
     */
    boolean changeArgumentToDefaultFunction(String argumentName) {

        // Check if this was already mapped
        QualifiedName qualifiedName = userQualifiedIdentifiers.getQualifiedName(argumentName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
        if (qualifiedName != null) {
            // Yes, use previous qualification
            changeArgumentToQualification(argumentName, qualifiedName.getModuleName());
            return true;
        }

        // Put variable in first candidate module
        List<ModuleName> candidateModules = CodeAnalyser.getModulesContainingIdentifier(
                argumentName,
                SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD,
                perspective.getWorkingModuleTypeInfo());
        if (candidateModules.size() > 0) {
            changeArgumentToQualification(argumentName,
                    candidateModules.get(0));
            return true;
        }

        // Argument cannot resolve to a function
        return false;
    }

    /**
     * Change the form of an argument to a qualification. If the argument is
     * fully qualified in code, this means it is changed to a function in the
     * current module. If the argument is not fully qualified in code, then it
     * is changed to a function from an external module, but will not be removed
     * from the list of arguments.
     * 
     * @param argumentName
     * @param moduleName
     */
    void changeArgumentToQualification(String argumentName,
            ModuleName moduleName) {

        StateEdit stateEdit = null;
        PartInput[] oldInputs = null;
        if (recordingEditStates) {
            stateEdit = new StateEdit(CodeGemEditor.this, GemCutter.getResourceString("UndoText_CodeGemArgFormChange"));
            oldInputs = codeGem.getInputParts();
        }

        if (moduleName.equals(perspective.getWorkingModuleName())) {
            // Remove argument from free variables, and add to map
            varNamesWhichAreArgs.remove(argumentName);
        }
        userQualifiedIdentifiers.putQualification(argumentName, moduleName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);

        // re-evaluate the gem based on the new variable form
        doSyntaxSmarts();

        if (recordingEditStates) {
            stateEdit.end();
            if (editHandler != null) {
                editHandler.definitionEdited(this, oldInputs, stateEdit);
            }
        }
    }

    /**
     * Change the form of an ambiguity to a qualification.
     * 
     * @param argumentName
     * @param moduleName
     * @param type
     */
    void changeAmbiguityToQualification(String argumentName,
            ModuleName moduleName, SourceIdentifier.Category type) {

        StateEdit stateEdit = null;
        PartInput[] oldInputs = null;
        if (recordingEditStates) {
            stateEdit = new StateEdit(CodeGemEditor.this, GemCutter.getResourceString("UndoText_CodeGemArgFormChange"));
            oldInputs = codeGem.getInputParts();
        }

        userQualifiedIdentifiers.putQualification(argumentName, moduleName, type);

        // re-evaluate the gem based on the new variable form
        doSyntaxSmarts();

        if (recordingEditStates) {
            stateEdit.end();
            if (editHandler != null) {
                editHandler.definitionEdited(this, oldInputs, stateEdit);
            }
        }
    }

    /**
     * Change the form of a qualification (ie: an identifier which was not
     * qualified in code) to an argument of the codegem.
     * 
     * @param qualificationName
     *            the unqualified name of the qualification
     * @param qualificationModuleOrNull
     *            the module name, or null
     */
    void changeQualificationToArgument(String qualificationName,
            ModuleName qualificationModuleOrNull) {

        StateEdit stateEdit = null;
        PartInput[] oldInputs = null;
        if (recordingEditStates) {
            stateEdit = new StateEdit(CodeGemEditor.this, GemCutter.getResourceString("UndoText_CodeGemArgFormChange"));
            oldInputs = codeGem.getInputParts();
        }

        // Add qualification to free variables
        varNamesWhichAreArgs.add(qualificationName);
        if (qualificationModuleOrNull == null || !qualificationModuleOrNull.equals(perspective.getWorkingModuleName())) {
            userQualifiedIdentifiers.removeQualification(qualificationName, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD);
        }

        // re-evaluate the gem based on the new variable form
        doSyntaxSmarts();

        if (recordingEditStates) {
            stateEdit.end();
            if (editHandler != null) {
                editHandler.definitionEdited(this, oldInputs, stateEdit);
            }
        }
    }

    /**
     * Change the module of a qualification represented by a qualification panel
     * 
     * @param unqualifiedName
     *            unqualified name of identifier
     * @param newModule
     *            new module name. <em>Cannot</em> be null.
     * @param qualificationForm
     *            identifier form
     */
    void changeQualificationModule(String unqualifiedName,
            ModuleName newModule, SourceIdentifier.Category qualificationForm) {

        StateEdit stateEdit = null;
        PartInput[] oldInputs = null;
        if (recordingEditStates) {
            stateEdit = new StateEdit(CodeGemEditor.this, GemCutter.getResourceString("UndoText_CodeGemQualificationModuleChange"));
            oldInputs = codeGem.getInputParts();
        }

        // If we are switching to the current module, make sure all arguments
        // are switched also
        if (newModule.equals(perspective.getWorkingModuleName())) {
            varNamesWhichAreArgs.remove(unqualifiedName);
        }

        // Change the module mapping

        CodeQualificationMap qualificationMap = userQualifiedIdentifiers;
        qualificationMap.removeQualification(unqualifiedName, qualificationForm);
        qualificationMap.putQualification(unqualifiedName, newModule, qualificationForm);

        // re-evaluate the gem based on the new variable form
        doSyntaxSmarts();

        if (recordingEditStates) {
            stateEdit.end();
            if (editHandler != null) {
                editHandler.definitionEdited(this, oldInputs, stateEdit);
            }
        }
    }
    
    /**
     * Update the user qualification map to reflect the renaming of a (non module) entity.
     * If the qualification map contained the old name, it will be replaced by this new name. 
     * @param oldName
     * @param newName
     * @param category
     */
    void updateQualificationsForEntityRename(QualifiedName oldName, QualifiedName newName, SourceIdentifier.Category category) {
        QualifiedName storedQualification = userQualifiedIdentifiers.getQualifiedName(oldName.getUnqualifiedName(), category);
        if (oldName.equals(storedQualification)) {
            userQualifiedIdentifiers.removeQualification(oldName.getUnqualifiedName(), category);
            userQualifiedIdentifiers.putQualification(newName.getUnqualifiedName(), newName.getModuleName(), category);
        }
    }
    
    /**
     * Update the user qualification map to reflect the renaming of a module.
     * All qualifications that referred to the old module name will be replaced with a new
     * qualification referring to the new module name.
     * @param oldModuleName
     * @param newModuleName
     */
    void updateQualificationsForModuleRename(ModuleName oldModuleName, ModuleName newModuleName) {
       // We need to handle each of the following four categories separately, so keep them in an an array
       // and loop over them
       SourceIdentifier.Category[] categories = new SourceIdentifier.Category[] {
               SourceIdentifier.Category.DATA_CONSTRUCTOR,
               SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD,
               SourceIdentifier.Category.TYPE_CLASS,
               SourceIdentifier.Category.TYPE_CONSTRUCTOR
       };
       
       for (final Category category : categories) {
           // Get the set of unqualifiedNames associated with this category, and look up their respective qualifications.
           // If the module name associated with them is the old module name, replace the qualification with a new one
           // referring to the new module name.
           Set<String> unqualifiedNames = userQualifiedIdentifiers.getUnqualifiedNames(category);
           for (final String unqualifiedName : unqualifiedNames) {
               QualifiedName qualifiedName = userQualifiedIdentifiers.getQualifiedName(unqualifiedName, category);
               if (qualifiedName.getModuleName().equals(oldModuleName)) {
                   userQualifiedIdentifiers.removeQualification(unqualifiedName, category);
                   userQualifiedIdentifiers.putQualification(unqualifiedName, newModuleName, category);
               }
           }
       }
    }

    /**
     * Shift an argument to another position.
     * 
     * @param inputIndexToShift
     *            the index of the input to shift
     * @param shiftAmount
     *            the amount by which to shift the input. +ve numbers increase
     *            its index, -ve numbers decrease it.
     */
    private void shiftInput(int inputIndexToShift, int shiftAmount) {

        int nArgs = codeGem.getNInputs();

        int newArgPos = inputIndexToShift + shiftAmount;
        if (newArgPos < 0 || newArgPos >= nArgs) {
            throw new IllegalArgumentException("The new argument index must lie within 0 and " + (nArgs - 1) + " inclusive.");
        }

        // Fill in the new input nums array.
        int[] newInputNums = new int[nArgs];
        for (int i = 0; i < nArgs; i++) {

            // If we're looking at the new arg position, insert the new index.
            if (i == inputIndexToShift) {
                newInputNums[i] = newArgPos;

            } else {
                // Otherwise, adjust the value from i.
                int newArgIndex = i;
                if (inputIndexToShift < newArgPos) {
                    // shift down everything in between
                    if (i > inputIndexToShift && i <= newArgPos) {
                        newArgIndex--;
                    }
                } else {
                    // shift up everything in between
                    if (i < inputIndexToShift && i >= newArgPos) {
                        newArgIndex++;
                    }
                }

                newInputNums[i] = newArgIndex;
            }
        }

        reorderInputs(newInputNums);
    }

    /**
     * Reorder the inputs. Only call if this code gem's result type is ok (ie.
     * it's either not broken, or broken because of its connections).
     * 
     * @param newInputNums
     *            int[] An array of new input numbers. The number at index i is
     *            the new index of input i.
     */
    private void reorderInputs(int[] newInputNums) {

        Argument.NameTypePair[] currentArgs = codeGem.getArguments();

        if (codeGem.getCodeResultType() == null) {
            throw new IllegalStateException("Attempt to reorder inputs on a broken gem.");
        }

        // fill in an array that tracks which inputs are included in newInputNums
        boolean[] inputsGiven = new boolean[newInputNums.length];
        for (final int inputNum : newInputNums) {
            if (inputNum < 0 || inputNum >= newInputNums.length) {
                throw new IllegalArgumentException("Input nums must be in the range 0 to (numInputs-1) inclusive.");
            }
            inputsGiven[inputNum] = true;
        }

        // now check that all the inputs are accounted for
        for (final boolean inputGiven : inputsGiven) {
            if (inputGiven == false) {
                throw new IllegalArgumentException("Attempt to reorder inputs to a state without all the inputs.");
            }
        }

        // declare the new args array
        int numArgs = newInputNums.length;
        Argument.NameTypePair[] newArgs = new Argument.NameTypePair[numArgs];

        // fill in the new args array
        for (int i = 0; i < numArgs; i++) {
            // the number at index i is the new index of arg i
            int newInputNum = newInputNums[i];
            newArgs[newInputNum] = currentArgs[i];
        }

        // Everything else should be the same..
        boolean wasBroken = codeGem.isBroken();
        codeGem.definitionUpdate(codeGem.getCode(), newArgs, codeGem.getCodeResultType(), codeGem.getArgNameToInputMap(), codeGem.getQualificationMap(), codeGem.getVisibleCode());

        codeGem.setBroken(wasBroken);
        if (!wasBroken) {
            updateLastGoodState();
        }
    }

    /**
     * Carry out actions needed to insert the specified string into the editor
     * from the auto-complete manager.
     * 
     * @param backtrackLength
     * @param insertion
     */
    public void insertAutoCompleteString(int backtrackLength, String insertion) {

        try {
            // Remove text from editor
            AdvancedCALEditor editor = gemCodePanel.getCALEditorPane();
            int caretPosition = editor.getCaretPosition();
            CodeAnalyser.AnalysedIdentifier identifier = editor.getIdentifierAtPosition(caretPosition);
            editor.getDocument().remove(caretPosition - backtrackLength, backtrackLength);
            if ((identifier == null) || (insertion.indexOf('.') < 0)) {
                // Not a qualified (ie: ambiguous) insertion, or cannot locate identifier
                // Do regular insert
                editor.getDocument().insertString(caretPosition - backtrackLength, insertion, null);
                return;
            } else {
                // Qualified completion, and type of the identifier is known
                // So do a smart insert and update the qualification map
                SourceIdentifier.Category identifierCategory = identifier.getCategory();
                QualifiedName completedName = QualifiedName.makeFromCompoundName(insertion);
                EditorTextTransferHandler.insertEditorQualification(gemCodePanel.getCALEditorPane(), completedName, identifierCategory, userQualifiedIdentifiers, true);
            }

        } catch (BadLocationException e) {
            throw new IllegalStateException("bad location on auto-complete insert");
        }
    }

    /**
     * @see org.openquark.gems.client.AutoCompleteManager.AutoCompleteEditor#getEditorComponent()
     */
    public JTextComponent getEditorComponent() {
        return gemCodePanel.getCALEditorPane();
    }

    /*
     * Methods supporting javax.swing.undo.StateEditable
     * ********************************************
     */

    /**
     * Restore the stored editor state.
     * 
     * @param state
     *            Hashtable the stored state
     */
    public void restoreState(Hashtable<?, ?> state) {
        boolean oldSmartsActivated = smartsActivated;
        smartsActivated = false;

        codeGem.restoreDefinitionState(state);

        gemCodePanel.restoreState(state);
        gemCodePanel.getCALEditorPane().setText(codeGem.getVisibleCode());

        Object stateValue = state.get(new Pair<CodeGemEditor, String>(this, LAST_ARGUMENTS_KEY));
        if (stateValue != null) {
            lastArguments = new ArrayList<NameTypePair>(UnsafeCast.<ArrayList<NameTypePair>>unsafeCast(stateValue));
        }

        stateValue = state.get(new Pair<CodeGemEditor, String>(this, LAST_RESOLVED_QUALIFICATIONS_KEY));
        if (stateValue != null) {
            userQualifiedIdentifiers = ((CodeQualificationMap)stateValue).makeCopy();
            editorTransferHandler.setUserQualifiedIdentifiers(userQualifiedIdentifiers);
        }

        stateValue = state.get(new Pair<CodeGemEditor, String>(this, OLD_NAME_TO_INPUT_MAP_KEY));
        if (stateValue != null) {
            oldNameToInputMap = new HashMap<String, PartInput>(UnsafeCast.<Map<String, PartInput>>unsafeCast(stateValue));
        }

        stateValue = state.get(new Pair<CodeGemEditor, String>(this, ARG_NAMED_VARS_KEY));
        if (stateValue != null) {
            varNamesWhichAreArgs = new LinkedHashSet<String>(UnsafeCast.<Set<String>>unsafeCast(stateValue));
            editorTransferHandler.setArgumentNames(varNamesWhichAreArgs);
        }

        stateValue = state.get(new Pair<CodeGemEditor, String>(this, PRESERVE_ORDER_KEY));
        if (stateValue != null) {
            keepInputsInNaturalOrder = ((Boolean) stateValue).booleanValue();
        }

        smartsActivated = oldSmartsActivated;
    }

    /**
     * Save the editor state.
     * 
     * @param state
     *            Hashtable the table in which to store the editor state
     */
    public void storeState(Hashtable<Object, Object> state) {

        codeGem.storeDefinitionState(state);

        gemCodePanel.storeState(state);

        state.put(new Pair<CodeGemEditor, String>(this, LAST_ARGUMENTS_KEY), new ArrayList<NameTypePair>(lastArguments));
        state.put(new Pair<CodeGemEditor, String>(this, LAST_RESOLVED_QUALIFICATIONS_KEY), userQualifiedIdentifiers.makeCopy());
        state.put(new Pair<CodeGemEditor, String>(this, OLD_NAME_TO_INPUT_MAP_KEY), new HashMap<String, PartInput>(oldNameToInputMap));
        state.put(new Pair<CodeGemEditor, String>(this, ARG_NAMED_VARS_KEY), new LinkedHashSet<String>(varNamesWhichAreArgs));
        state.put(new Pair<CodeGemEditor, String>(this, PRESERVE_ORDER_KEY), Boolean.valueOf(keepInputsInNaturalOrder));
    }
}
