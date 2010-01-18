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
 * RenameRefactoringDialog.java
 * Creation date: Jun 18, 2004
 * By: Iulian Radu
 */
package org.openquark.gems.client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleSourceDefinition;
import org.openquark.cal.compiler.ModuleSourceDefinitionGroup;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Refactorer;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.Refactorer.CALRefactoringException;
import org.openquark.cal.machine.StatusListener;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.IdentifierUtils;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.services.ResourceStore;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.cal.services.WorkspaceResource;


/**
 * A dialog box used for renaming Gems (ie functions, class methods or data constructors), type classes, and type constructors.
 * 
 * @author Peter Cardwell
 */
public final class RenameRefactoringDialog extends JDialog {
    
    private static final long serialVersionUID = -1471936909236566691L;

    /** The icon to use if everything is ok. */
    static final Icon OK_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/checkmark.gif"));
    
    /** The icon to use for error messages. */
    static final Icon ERROR_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/error.gif"));
    
    /** The icon to use for warning messages. */
    static final Icon WARNING_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/warning.gif"));
    
    /** The icon to use to indicate modules. */
    static final Icon MODULE_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/nav_module.png"));
    
    /** The icon to use to indicate functions. */
    static final Icon FUNCTION_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/Gem_Red.gif"));
    
    /** The icon to use to indicate data constructors. */
    static final Icon DATACONS_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/Gem_Yellow.gif"));
    
    /** The icon to use to indicate type classes. */
    static final Icon TYPECLASS_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/nav_typeclass.gif"));
    
    /** The icon to use to indicate type constructors. */
    static final Icon TYPECONS_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/nav_typeconstructor.gif"));
    
    /**
     * @author Peter Cardwell
     *
     * A class representing the result of the refactor operation.
     * Contains the original name of the feature that was refactored,
     * the name it was changed to, and its category.
     */
    public static class Result {
       
        /** The original name of the item that was renamed. If applicable, this should be a fully qualified name. */
        private final String fromName;
        
        /** The new name of the item that was renamed. If applicable, this should be a fully qualified name. */
        private final String toName;        
        
        /** The category of the item that was renamed. */
        private final SourceIdentifier.Category category;

        Result (String fromName, String toName, SourceIdentifier.Category category) {
            this.fromName = fromName;
            this.toName = toName;
            this.category = category;
        }
        
        /**
         * @return The original name of the item that was renamed. If applicable, this should be a fully qualified name.
         */
        public String getFromName() {
            return fromName;
        }
        
        /**
         * @return The new name of the item that was renamed. If applicable, this should be a fully qualified name.
         */
        public String getToName() {
            return toName;
        }
        
        /**
         * @return The category of the item that was renamed.
         */
        public SourceIdentifier.Category getCategory() {
            return category;
        }
        
        /**
         * @return The type of the item that was renamed.
         */
        public EntityType getEntityType() {
            if (category == SourceIdentifier.Category.MODULE_NAME) {
                return EntityType.Module;
            } else if (category == SourceIdentifier.Category.TYPE_CLASS) {
                return EntityType.TypeClass;
            } else if (category == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
                return EntityType.TypeConstructor;
            } else if (category == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD ||
                       category == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
                return EntityType.Gem;
            } else {
                throw new IllegalStateException("Invalid category in rename result.");
            }
        }
    }
    
    /**
     * Generic item renderer for lists used in the dialog box. Displays names and appropriate icons.
     * 
     * @author Peter
     */
    static class RenameListRenderer extends JLabel implements ListCellRenderer {
        
        private static final long serialVersionUID = 6522948562916719460L;
        private final Icon itemIcon;
        private final boolean showSelections;
        
        /**
         * Constructor
         * @param icon icon to use for each item
         * @param showSelections true if the user selections should be made visible. 
         */
        public RenameListRenderer(Icon icon, boolean showSelections) {
            this.itemIcon = icon;
            this.showSelections = showSelections;
            setOpaque(true);
        }
        
        /**
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
            if (isSelected && showSelections) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            String text = value.toString(); // value could be a String or a ModuleName
            Icon icon = itemIcon;
            setIcon(icon);
            setText(text);
            setFont(list.getFont());
            
            return this;
        }
    }

    
    /**
     * A thread to perform the actual refactorings and recompilations.
     * @author Peter Cardwell
     */
    class RefactoringThread extends Thread {
        
        @Override
        public void run() {
           if (doRefactorings()) {
               dispose();
           } else {
               setVisibleButtons(new JButton[]{backButton, retryButton, cancelButton});
               setEnabledButtons(new JButton[]{backButton, retryButton, cancelButton});
               exitProgressMode();
               
               statusLabel.setText(GemCutter.getResourceString("RNRD_ErrorsEncountered"));
               statusLabel.setIcon(ERROR_ICON);
               
               // We're done with the status listener..
               refactorer.setStatusListener(null);
           }
        }
    }
    
    /** The currently selected entityType */
    private EntityType entityType;
    
    /** The currently selected module (only used when renaming a qualified entity) */
    private ModuleName moduleName;
    
    /** A list of modules that are affected by the rename operation */
    private List<ModuleName> affectedModuleNames;
    
    /** True if the currently selected module is empty. */
    private boolean emptyModule = false;
    
    
    // GUI components
    private final JLabel sourceModuleLabel = new JLabel(GemCutter.getResourceString("RNRD_Module"));
    private final JLabel oldNameLabel = new JLabel(GemCutter.getResourceString("RNRD_CurrentName"));    
    private final JComboBox oldNameCombo = new JComboBox(new DefaultComboBoxModel());
    private final JLabel newNameLabel = new JLabel(GemCutter.getResourceString("RNRD_NewName"));
    private final JTextField newNameField = new JTextField();
    private final ButtonGroup entityTypeButtonGroup = new ButtonGroup();
    private final JRadioButton gemRadioButton = new JRadioButton(GemCutter.getResourceString("RNRD_GemEntityType"));
    private final JRadioButton typeClassRadioButton = new JRadioButton(GemCutter.getResourceString("RNRD_TypeClassEntityType"));
    private final JRadioButton typeConsRadioButton = new JRadioButton(GemCutter.getResourceString("RNRD_TypeConsEntityType"));
    private final JRadioButton moduleRadioButton = new JRadioButton(GemCutter.getResourceString("RNRD_ModuleEntityType"));
    private final JComboBox sourceModuleList = new JComboBox(new DefaultComboBoxModel());
    private final JLabel titleLabel = new JLabel(GemCutter.getResourceString("RNRD_Rename"));
    private JPanel mainPanel;
    
    /** If false, the user should not be allowed to make any changes that affect the Prelude module. */
    private final boolean allowPreludeRenaming;
    /** Indicates whether the from name was passed into the constructor */
    private final boolean fromNameIsPreset;
    /** Indicates whether the category was passed into the constructor */
    private final boolean entityTypeIsPreset;
  
    /** If false, the user should be forbidden to rename gems to names that already exist. */
    private final boolean allowDuplicateRenaming;
    
    /** If true, then we are allowing an "unsafe" operation to go forward (i.e., one for which the user may need to manually
     * perform some cleanup before we recompile) */
    private boolean proposedFactoringUnsafe;
    
    /** The (unqualified) original name of the item to rename. */
    private String fromName;
    /** The (unqualified) name that the item should be renamed to. */
    private String toName;
    /** The type of renaming to perform */
    private SourceIdentifier.Category category;
    /** True if the refactoring successfully completed, false otherwise. */
    private boolean refactorSuccessful = false;
    private Action okAction = getOkAction();
    private Action retryAction = getRetryAction();
    private Action cancelAction = getCancelAction();
    private Action backAction = getBackAction();
    private Refactorer.Rename refactorer;
    private final Perspective perspective;
    private RefactoringThread refactoringThread;
    private Dimension minimumSize;
    private final WorkspaceManager workspaceManager;
    //private RenameRefactorer.StatusListener refactoringStatusListener;
    /** 
     * Indicates if old module/name or new name is dirty 
     * (ie: the current refactoring operation does not validly represent the ui fields)
     */
    private boolean dirtyFields = true;
    /** Indicates that this dialog is being automatically driven by an undo/redo command. */
    private final boolean automatedMode;
    private final JList errorsList = new JList(new DefaultListModel());
    private final JScrollPane informationScrollPane = new JScrollPane(errorsList);
    private final JButton okButton = new JButton(okAction);
    private final JButton retryButton = new JButton(retryAction);
    private final JButton backButton = new JButton(backAction);
    private final JButton cancelButton = new JButton(cancelAction);
    private final JLabel statusLabel = new JLabel("                         ");
    private final JLabel entityTypeLabel = new JLabel(GemCutter.getResourceString("RNRD_EntityType"));
    private final JLabel progressLabel = new JLabel(GemCutter.getResourceString("RNRD_Progress"));
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    
    /** An array containing all action buttons on the dialog. This is used by setEnabledButtons() and setVisibleButtons() to know which
      * buttons to disable/hide. */
    private final JButton[] dialogButtons = new JButton[]{okButton, retryButton, cancelButton, backButton};
    
    /** An array containing all components in the dialog used for value entry. This is used by lockValueEntry() and unlockValueEntry() to
     * know which components to enable and disable. */
    private final Component[] valueEntryComponents = new Component[] { 
        sourceModuleList,
        oldNameCombo,
        newNameField,
        gemRadioButton,
        typeClassRadioButton,
        typeConsRadioButton,
        moduleRadioButton,
    };
    
    /** Type safe-enum pattern enumerating the various entity types */
    static class EntityType {
        private final String description;
        
        private EntityType (String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return description;
        }
        
        public static final EntityType Gem =
            new EntityType("Gem");
        
        public static final EntityType TypeConstructor =
            new EntityType("Type Constructor");
        
        public static final EntityType TypeClass =
            new EntityType("Type Class");
        
        public static final EntityType Module =
            new EntityType("Module");
    }
        
    /**
     * Constructs a RenameRefactoringDialog.
     * @param parent The parent frame of this dialog
     * @param workspaceManager The workspace manager for the CAL sources.
     * @param perspective The perspective containing the current working context
     * @param fromName If not null, this means the from name should be pre-selected. entityType must be non-null if this value is non-null.
     * @param toName If not null, this means the to name should be pre-selected and the dialog operation should proceed without user input
     *  (for undoing and redoing). fromName must be non-null for this value to be non-null.
     * @param entityType If not null, this means the entity type should be pre-selected.
     * @param allowPreludeRenaming If false, the user will be prevented from renaming entities in the prelude module.
     */
    RenameRefactoringDialog(JFrame parent, WorkspaceManager workspaceManager, Perspective perspective, String fromName, String toName, EntityType entityType, boolean allowPreludeRenaming, boolean allowDuplicateRenaming) {
        super(parent, true);

        if ((workspaceManager == null) || (perspective == null)) {
            throw new NullPointerException();
        }

        if (fromName != null) {
            if (entityType == null) {
                throw new IllegalArgumentException("For the from name to be preset, the entity type must be specified");
            } else if (entityType == EntityType.Module) {
                this.moduleName = ModuleName.make(fromName);
                this.fromName = fromName;
            } else {
                QualifiedName qualifiedFromName = QualifiedName.makeFromCompoundName(fromName);
                this.moduleName = qualifiedFromName.getModuleName();
                this.fromName = qualifiedFromName.getUnqualifiedName();
            }
        }

        this.workspaceManager = workspaceManager;
        this.perspective = perspective;
        this.allowPreludeRenaming = allowPreludeRenaming;
        this.allowDuplicateRenaming = allowDuplicateRenaming;
        this.toName = toName;
        this.entityType = entityType;

        this.proposedFactoringUnsafe = false;

        this.fromNameIsPreset = (this.fromName != null);
        this.entityTypeIsPreset = (this.entityType != null);
        this.automatedMode = (this.toName != null);

        if (automatedMode && !fromNameIsPreset) {
            throw new IllegalArgumentException("If newEntityName is non-null, entityName must be non-null");
        }

        if (automatedMode) {
            // Create a listener to begin refactoring as soon as the dialog is
            // shown.
            ComponentListener componentListener = new ComponentListener() {
                public void componentHidden(ComponentEvent e) {
                }

                public void componentMoved(ComponentEvent e) {
                }

                public void componentResized(ComponentEvent e) {
                }

                public void componentShown(ComponentEvent e) {
                    // Begin refactoring
                    statusLabel.setText(GemCutter.getResourceString("RNRD_Inspecting"));
                    statusLabel.setIcon(null);
                    if ((refactoringThread == null) || (!refactoringThread.isAlive())) {
                        refactoringThread = new RefactoringThread();
                        refactoringThread.start();
                    } else {
                        throw new IllegalStateException("Tried to start new refactoring thread while another was running.");
                    }
                    removeComponentListener(this);
                }
            };

            addComponentListener(componentListener);
        }

        setLocationRelativeTo(parent);       
    }
    
    /**
     * @return the panel used for selecting which entity type to rename.
     */
    private JPanel getEntityTypeSelectPanel() {
        
        entityTypeButtonGroup.add(gemRadioButton);
        entityTypeButtonGroup.add(typeClassRadioButton);
        entityTypeButtonGroup.add(typeConsRadioButton);
        entityTypeButtonGroup.add(moduleRadioButton);
        
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
        
        radioPanel.add(Box.createHorizontalGlue());
        radioPanel.add(entityTypeLabel);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(gemRadioButton);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(typeConsRadioButton);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(typeClassRadioButton);
        radioPanel.add(Box.createHorizontalStrut(5));
        radioPanel.add(moduleRadioButton);
        radioPanel.add(Box.createHorizontalGlue());
        
        return radioPanel;
    }
    
    /**
     * @return the panel that contains the buttons at the bottom of the dialog
     */
    private JPanel getButtonPanel() {
        
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(backButton);
        buttonPanel.add(okButton);
        buttonPanel.add(retryButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(cancelButton);
        
        return buttonPanel;
    }
        
    
    
    /**
     * Set appropriate listeners, layout the dialog, and set the dialog to its initial state.
     */
    private void initialize() {
        
        gemRadioButton.setSelected(true);
        
        // Populate the module name combo box with the workspace modules
        
        List<ModuleName> moduleList = new ArrayList<ModuleName>();
        ModuleName[] moduleNames = getWorkspace().getModuleNames();
        for (final ModuleName module : moduleNames) {
            moduleList.add(module);
        }
        Collections.sort(moduleList);
        for (int i = 0, n = moduleList.size(); i < n; i++) {
            ((DefaultComboBoxModel)sourceModuleList.getModel()).addElement(moduleList.get(i));
        }
        
        if (moduleName == null) {
            moduleName = perspective.getWorkingModuleName();
        }
        sourceModuleList.setSelectedItem(moduleName);
        sourceModuleList.setRenderer(new RenameListRenderer(MODULE_ICON, true));
        
        // Populate gem combo box with module gems
        
        oldNameCombo.setEditable(true);
        oldNameCombo.setRenderer(new NameComboListRenderer(perspective.getMetaModule((ModuleName)sourceModuleList.getSelectedItem()).getTypeInfo(), entityType));
        populateOldNameCombo();
        
        updateFromName();
        
        if (!automatedMode) {            
            toName = fromName;
        }
        
        newNameField.setText(toName);
        newNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {                
                toName = newNameField.getText();
                updateUIState();                
                dirtyFields = true;
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
        
        newNameField.getDocument().addDocumentListener(new DocumentListener() {
            public void changeOccurred() {
                toName = newNameField.getText();
                updateUIState();                
                dirtyFields = true;
            }

            public void changedUpdate(DocumentEvent e) {
                changeOccurred(); 
            }

            public void insertUpdate(DocumentEvent e) {
                changeOccurred();
            }

            public void removeUpdate(DocumentEvent e) {
                changeOccurred();    
            }
        });
        
        if (!fromNameIsPreset) {
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dirtyFields = true;
                    
                    if (!entityTypeIsPreset) {
                        // Check if the gemType has changed
                        if (entityType != EntityType.Gem && gemRadioButton.isSelected()) {
                            entityType = EntityType.Gem;
                            populateOldNameCombo();
                        } else if (entityType != EntityType.TypeClass && typeClassRadioButton.isSelected()) {
                            entityType = EntityType.TypeClass;
                            populateOldNameCombo();
                        } else if (entityType != EntityType.TypeConstructor && typeConsRadioButton.isSelected()) {
                            entityType = EntityType.TypeConstructor;
                            populateOldNameCombo();
                        }
                    }
                    updateUIState();
                    newNameField.requestFocus();
                    newNameField.selectAll();
                }
            };
            
            gemRadioButton.addActionListener(actionListener);
            typeClassRadioButton.addActionListener(actionListener);
            typeConsRadioButton.addActionListener(actionListener);
            
            oldNameCombo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    dirtyFields = true;
                    updateFromName();
                    if(oldNameCombo.getSelectedItem() != null) {
                        newNameField.setText(fromName);
                    }
                    updateUIState();
                }
            });
            
            // Add keyboard listener to the text fields 
            
            oldNameCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    updateUIState();
                    dirtyFields = true;                    
                    newNameField.setText(fromName);
                    if ((statusLabel.getIcon() == ERROR_ICON) && (e.getKeyCode() == KeyEvent.VK_ENTER)) {
                        newNameField.requestFocus();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        dispose();
                    }
                }
            });            
            
            // Create a popup listener that selects the new name field text whenever an item is selected.
            PopupMenuListener popupMenuListener = new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    newNameField.requestFocus();
                    newNameField.selectAll();
                }
                public void popupMenuCanceled(PopupMenuEvent e) {}
            }; 
            oldNameCombo.addPopupMenuListener(popupMenuListener);
            sourceModuleList.addPopupMenuListener(popupMenuListener);
            
            sourceModuleList.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    moduleName = (ModuleName)sourceModuleList.getSelectedItem();
                    updateFromName();
                    if (entityType == EntityType.Module) {
                        newNameField.setText(fromName); 
                    }
                    dirtyFields = true;                    
                    populateOldNameCombo();
                    updateUIState();
                }
            });
            sourceModuleList.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if ((statusLabel.getIcon() == ERROR_ICON) && (e.getKeyCode() == KeyEvent.VK_ENTER)) {
                        oldNameCombo.requestFocus();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        dispose();
                    }
                }
            });
        }
        
        // Initialize affected module list component
        informationScrollPane.setVisible(false);
        informationScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Make the type name field have default focus
        
        if (fromNameIsPreset) {
            setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
                private static final long serialVersionUID = 5993228846759302688L;

                @Override
                public Component getDefaultComponent(Container c) { return newNameField; }
            });
        } else {
            setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
                private static final long serialVersionUID = -6709077930385639859L;

                @Override
                public Component getDefaultComponent(Container c) { return oldNameCombo; }
            });
        }
        
        // Set the title
        
        String titleText;
        if (automatedMode) {
            titleText = getTitle();
        } else if (fromNameIsPreset) {
            titleText = GemCutterMessages.getString("RNRD_RenamePreset", entityType.toString(), fromName);            
            sourceModuleList.setSelectedItem(moduleName);
            oldNameCombo.setSelectedItem(fromName);
        } else if (entityTypeIsPreset) {
            titleText = GemCutterMessages.getString("RNRD_Rename", entityType.toString());
        } else {
            titleText = GemCutter.getResourceString("RNRD_RenameGem"); 
        }            
        this.setTitle(titleText);
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                if(cancelButton.isEnabled()) {
                    cancelButton.doClick(0);
                }
            }
        });        
        addComponentListener(new ComponentAdapter() {
            // Listener called whenever dialog size changes 
            // (eg: due to manual window resized, or setSize call)
            @Override
            public void componentResized(ComponentEvent e) {
                enforceMinimumSize(minimumSize);
                validate();
            }
        });
        
        layoutDialog();
        
        // Temporarily make the progress bar visible so that the
        // dialog box will be properly sized when in automated mode
        if (automatedMode) {
            statusLabel.setText(GemCutter.getResourceString("RNRD_Inspecting"));        
            statusLabel.setIcon(null);
            progressLabel.setVisible(true);            
            progressBar.setVisible(true);
        }
    
        errorsList.setCellRenderer(new RenameListRenderer(ERROR_ICON, false));
        
        setVisibleButtons(new JButton[] {okButton, cancelButton});
        setEnabledButtons(new JButton[] {okButton, cancelButton});
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        pack();
        mainPanel.setMinimumSize(mainPanel.getSize());
        
        minimumSize = getSize();
        
        progressBar.setVisible(false);
        progressLabel.setVisible(false);            
        
        setResizable(false);        
        
        // Default button is OK
        
        getRootPane().setDefaultButton(okButton);
        
        updateUIState();
    }
    
    /**
     * Updates the fromName and category fields to reflect the contents of the from name field, if necessary.
     */
    private void updateFromName() {
        if (!fromNameIsPreset) {
            if (entityType == EntityType.Module) {
                fromName = ((ModuleName)sourceModuleList.getSelectedItem()).toSourceText();
            } else {
                fromName = (String) oldNameCombo.getSelectedItem();        
                if (fromName == null) {
                    fromName = "";
                }
            }
        }
        
        if (entityType == EntityType.Gem) {
            if (LanguageInfo.isValidDataConstructorName(fromName)) {
                category = SourceIdentifier.Category.DATA_CONSTRUCTOR;
            } else {
                category = SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD;
            }
        } else if (entityType == EntityType.TypeClass) {
            category = SourceIdentifier.Category.TYPE_CLASS;
        } else if (entityType == EntityType.TypeConstructor) {
            category = SourceIdentifier.Category.TYPE_CONSTRUCTOR;
        } else if (entityType == EntityType.Module) {
            category = SourceIdentifier.Category.MODULE_NAME;
        } else {
            throw new IllegalStateException("Illegal entity type");
        }
    }  
    
    /**
     * @return True if the values in the value entry fields are valid, false otherwise.
     */
    private boolean fieldsAreValid() {
        proposedFactoringUnsafe = false; // Will be set in the checks below if necessary 
        
        return !emptyModule &&  
            checkValidModule() &&
            checkValidOldName() &&
            checkValidNewName();
    }
    
    /**
     * Checks if the user-enetered module name is valid, and if not, disables the OK buttton and displays an error message.
     * @return True if the user-entered module name is valid (or if the module name field is not applicable), false otherwise.
     */
    private boolean checkValidModule() {
        if (entityType != EntityType.Module) {
            ModuleName selectedModuleName = (ModuleName)sourceModuleList.getSelectedItem();
            if (selectedModuleName.equals(CAL_Prelude.MODULE_NAME) && !allowPreludeRenaming) {
                statusLabel.setText(GemCutter.getResourceString("RNRD_UnmodifiableModule"));
                statusLabel.setIcon(ERROR_ICON);
                okButton.setEnabled(false);
                return false;
            }
        }
        return true;
    }    
   
    /**
     * Checks if the user-entered old name is valid, and if not, disables the OK button and displays an error message.
     * @return True if the user-entered old name is valid, false otherwise.
     */
    private boolean checkValidOldName() {
        if (fromName.length() == 0) {
            statusLabel.setText(GemCutter.getResourceString("RNRD_NameExistingEntity"));
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return false;
        }
        
        ModuleTypeInfo typeInfo = null;
        if (category != SourceIdentifier.Category.MODULE_NAME) {
            typeInfo = perspective.getMetaModule((ModuleName)sourceModuleList.getSelectedItem()).getTypeInfo();
        }
        
        boolean entityExists;
        if (category == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            entityExists = typeInfo.getFunctionOrClassMethod(fromName) != null;
            
        } else if (category == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
            entityExists = typeInfo.getDataConstructor(fromName) != null;
            
        } else if (category == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
            entityExists = typeInfo.getTypeConstructor(fromName) != null;
            
        } else if (category == SourceIdentifier.Category.TYPE_CLASS) {
            entityExists = typeInfo.getTypeClass(fromName) != null;
            
        } else if (category == SourceIdentifier.Category.MODULE_NAME) {
            entityExists = (perspective.getMetaModule(ModuleName.make(fromName)) != null);
            
            if (fromName.equals(CAL_Prelude.MODULE_NAME) && !allowPreludeRenaming) {
                statusLabel.setText(GemCutter.getResourceString("RNRD_UnmodifiableModule"));
                statusLabel.setIcon(ERROR_ICON);
                okButton.setEnabled(false);
                return false;
            }
            
        } else {
            throw new IllegalStateException("TODOPC");
        }
         
        if (!entityExists) {
            statusLabel.setText(GemCutter.getResourceString("RNRD_NonexistentEntity"));
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return false;
        }
        return true;
    }

    /**
     * Checks if the user-entered new name is valid, and if not, disables the OK button and displays an error message.
     * @return True if the user-entered new name is valid, false otherwise.
     */
    private boolean checkValidNewName() {
        if (toName.length() == 0) {
            statusLabel.setText(GemCutter.getResourceString("RNRD_SpecifyNewName"));
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
        }
       
        ModuleTypeInfo typeInfo = null;
        if (category != SourceIdentifier.Category.MODULE_NAME) {
            typeInfo = perspective.getMetaModule((ModuleName)sourceModuleList.getSelectedItem()).getTypeInfo();
        }
        
        boolean entityExists;
        IdentifierUtils.ValidatedIdentifier validatedIdentifier; 
        if (category == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
            validatedIdentifier = IdentifierUtils.makeValidTypeConstructorName(toName);
            entityExists = typeInfo.getTypeConstructor(toName) != null;
        } else if (category == SourceIdentifier.Category.TYPE_CLASS) {
            validatedIdentifier = IdentifierUtils.makeValidTypeClassName(toName);
            entityExists = typeInfo.getTypeClass(toName) != null;
        } else if (category == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
            validatedIdentifier = IdentifierUtils.makeValidDataConstructorName(toName);
            entityExists = typeInfo.getDataConstructor(toName) != null;
        } else if (category == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
            validatedIdentifier = IdentifierUtils.makeValidFunctionName(toName); 
            entityExists = typeInfo.getFunctionOrClassMethod(toName) != null;
        } else if (category == SourceIdentifier.Category.MODULE_NAME) {
            validatedIdentifier = IdentifierUtils.makeValidModuleName(toName);
            ModuleName maybeModuleName = ModuleName.maybeMake(toName);
            entityExists = maybeModuleName != null && perspective.getMetaModule(maybeModuleName) != null;
        } else {
            throw new IllegalStateException("Illegal gem type");
        }
        
        if (!validatedIdentifier.isValid()) {
            IdentifierUtils.ValidationStatus error = validatedIdentifier.getNthError(0);
            String errorKey;
            if (error == IdentifierUtils.ValidationStatus.INVALID_CONTENT) {
                errorKey = "RNRD_InvalidName_InvalidContent";                
            } else if (error == IdentifierUtils.ValidationStatus.INVALID_START) {
                errorKey = "RNRD_InvalidName_InvalidStart";
            } else if (error == IdentifierUtils.ValidationStatus.NEED_UPPER) {
                errorKey = "RNRD_InvalidName_NeedUpper";
            } else if (error == IdentifierUtils.ValidationStatus.NEED_LOWER) {
                errorKey = "RNRD_InvalidName_NeedLower";
            } else if (error == IdentifierUtils.ValidationStatus.EXISTING_KEYWORD) {
                errorKey = "RNRD_InvalidName_ExistingKeyword";
            } else if (error == IdentifierUtils.ValidationStatus.WAS_EMPTY) {
                errorKey = "RNRD_InvalidName_WasEmpty";
            } else {
                errorKey = "RNRD_InvalidName_Unknown";
            }
            
            statusLabel.setText(GemCutter.getResourceString(errorKey));
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return false;
        }
        
        if(entityExists && !fromName.equals(toName)) {
            if (allowDuplicateRenaming) {
               proposedFactoringUnsafe = true;
               statusLabel.setText(GemCutter.getResourceString("RNRD_EntityExistsWarning"));
               statusLabel.setIcon(WARNING_ICON);
            } else {
                statusLabel.setText(GemCutter.getResourceString("RNRD_EntityExists"));
                statusLabel.setIcon(ERROR_ICON);
                okButton.setEnabled(false);
                return false;
            }
        }
        
        okButton.setEnabled(true);
        return true;
    }
    
    /** 
     * @param moduleName base dependee module
     * @return list of ModuleNames of all module names dependent on the specified module 
     */
    private List<ModuleName> findDependentModules(ModuleName moduleName) {
        CALWorkspace workspace = perspective.getWorkspace();
        
        List<MetaModule> allModules = new ArrayList<MetaModule>();
        allModules.addAll(perspective.getInvisibleMetaModules());
        allModules.addAll(perspective.getVisibleMetaModules());
        
        List<ModuleName> dependentModules = new ArrayList<ModuleName>();
        dependentModules.add(moduleName);

        // Find direct dependents on the modules we are looking for
        for (int i = 0; i < dependentModules.size(); i++) {
            ModuleName importedName = dependentModules.get(i);
            
            // Run through all modules and find ones that import this module
            for (final MetaModule m : allModules) {
                if (m.getImportedModule(workspace, importedName) != null) {
                    
                    // This module imports our searched module, so add it to the list
                    if (!dependentModules.contains(m.getName())) {
                        dependentModules.add(m.getName());
                    }
                }
            }
        }
        
        return dependentModules;
    }
    
    /**
     * Initialize refactorer with appropriate resources and renamings as specified by the UI.
     * @return number of modules in refactorer list
     */
    private int initializeRefactorer() {
        List<ModuleName> dependentModules = findDependentModules(moduleName);
        List<ModuleSourceDefinition> dependentModuleSources = new ArrayList<ModuleSourceDefinition>();
        for (final ModuleName moduleName2 : dependentModules) {
            dependentModuleSources.add(getWorkspace().getSourceDefinition(moduleName2));
        }

        QualifiedName qualifiedFromName;
        QualifiedName qualifiedToName;
        if (entityType == EntityType.Module) {
            qualifiedFromName = QualifiedName.make(ModuleName.make(fromName), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
            qualifiedToName = QualifiedName.make(ModuleName.make(toName), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
        } else {
            qualifiedFromName = QualifiedName.make(moduleName, fromName);
            qualifiedToName = QualifiedName.make(moduleName, toName);
        }
        refactorer = new Refactorer.Rename(workspaceManager.getWorkspace().asModuleContainer(), workspaceManager.getTypeChecker(), qualifiedFromName, qualifiedToName, category);
        return dependentModules.size();
    }
    
    /**
     * Calculates the affected resources and populates the affectedModuleNames list.
     * @return True if the operation completed successfully
     */
    private boolean calcResources() {
        CompilerMessageLogger logger = new MessageLogger();

        if (affectedModuleNames == null) {
            affectedModuleNames = new ArrayList<ModuleName>();
        } else {
            affectedModuleNames.clear();
        }
        List<ModuleSourceDefinition> affectedResources = refactorer.calculateModifications(logger);
        
        if (logger.getNErrors() == 0) {
            for (final ModuleSourceDefinition module : affectedResources) {
                affectedModuleNames.add(module.getModuleName());
            }
        }
        
        return !setErrorStatus(logger);
    }
    
    /**
     * Updates the contents of the source files to reflect the renaming.
     * @return True if the operation completed successfully, false otherwise.
     */
    private boolean updateSourcesMetadataAndResources() {
        CompilerMessageLogger logger = new MessageLogger();
        
        // Tie the progress bar to show refactorer progress on resources
        
        Refactorer.StatusListener refactoringStatusListener = new Refactorer.StatusListener() {
            public void willUseResource(ModuleSourceDefinition resource) {}
            public void doneUsingResource(ModuleSourceDefinition resource) {
                incrementProgressBar();
            }
        };
        refactorer.setStatusListener(refactoringStatusListener);
        refactorer.apply(logger);
        refactorer.setStatusListener(null);
        
        return !setErrorStatus(logger);        
    }
    
    /**
     * Undo the updateSources() operation. This should be used if another operation later on in the refactoring sequence fails.
     */
    private void undoSourcesMetadataAndResources() {
        CompilerMessageLogger logger = new MessageLogger();
        Refactorer.StatusListener refactoringStatusListener = new Refactorer.StatusListener() {
            public void willUseResource(ModuleSourceDefinition resource) {}
            public void doneUsingResource(ModuleSourceDefinition resource) {
                incrementProgressBar();
            }
        };
        refactorer.setStatusListener(refactoringStatusListener);
        refactorer.undo(logger);
        refactorer.setStatusListener(null);
        
        if (logger.getNErrors() > 0) {
            DefaultListModel listModel = (DefaultListModel) errorsList.getModel();
            listModel.addElement(GemCutterMessages.getString("RNRD_ErrorUndoingSourceUpdate"));
        }
    }

    /**
     * Updates the contents of the design files to reflect the renaming.
     * @return True if the operation completed successfully, false otherwise.
     */
    private boolean updateDesigns() {
        QualifiedName qualifiedFromName;
        QualifiedName qualifiedToName;
        if (entityType == EntityType.Module) {
            qualifiedFromName = QualifiedName.make(ModuleName.make(fromName), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
            qualifiedToName = QualifiedName.make(ModuleName.make(toName), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
        } else {
            qualifiedFromName = QualifiedName.make(moduleName, fromName);
            qualifiedToName = QualifiedName.make(moduleName, toName);
        }
        
        Status updateDesignStatus = new Status("Update design status");
        GemCutterRenameUpdater designRenamer = new GemCutterRenameUpdater(updateDesignStatus, workspaceManager.getTypeChecker(), qualifiedToName, qualifiedFromName, category);
        designRenamer.updateDesigns(workspaceManager);
        
        return !setErrorStatus(updateDesignStatus);
    }
    
    /**
     * Undo the updateDesigns() operation. This should be used if another operation later on in the refactoring sequence fails.
     */
    private void undoUpdateDesigns() {
        QualifiedName qualifiedFromName; // initialized with "to" name (because this is undo)
        QualifiedName qualifiedToName; // initialized with "from" name (because this is undo)
        if (entityType == EntityType.Module) {
            qualifiedFromName = QualifiedName.make(ModuleName.make(toName), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
            qualifiedToName = QualifiedName.make(ModuleName.make(fromName), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
        } else {
            qualifiedFromName = QualifiedName.make(moduleName, toName);
            qualifiedToName = QualifiedName.make(moduleName, fromName);
        }
        
        Status updateDesignStatus = new Status("Update design status");
        GemCutterRenameUpdater designRenamer = new GemCutterRenameUpdater(updateDesignStatus, workspaceManager.getTypeChecker(), qualifiedToName, qualifiedFromName, category);
        designRenamer.updateDesigns(workspaceManager);
        
        if (updateDesignStatus.getSeverity().compareTo(Status.Severity.ERROR) >= 0) { 
            DefaultListModel listModel = (DefaultListModel) errorsList.getModel();
            listModel.addElement(GemCutterMessages.getString("RNRD_ErrorUndoingDesignUpdating"));
        }
    }
    
    /**
     * @return The set of sources that are dependent on the "affected" (ie. changed) sources.
     */
    private Set<ModuleSourceDefinition> calculateDependentSources() {
        Set<ModuleSourceDefinition> dependentModules = new HashSet<ModuleSourceDefinition>();
        
        for (int i = 0, n = affectedModuleNames.size(); i < n; i++) {
            ModuleName moduleName = affectedModuleNames.get(i);
            
            for (final ModuleName dependentModuleName : workspaceManager.getDependentModuleNames(moduleName)) {
                dependentModules.add(getWorkspace().getSourceDefinition(dependentModuleName));
            }
            dependentModules.add(getWorkspace().getSourceDefinition(moduleName));
        }
        
        return dependentModules;
    }
    
    /**
     * Update the list of affected modules to reflect the renaming.
     */
    private void updateAffectedModules() {
        if (entityType == EntityType.Module) {
            final ModuleName fromNameAsModuleName = ModuleName.make(fromName);
            final ModuleName toNameAsModuleName = ModuleName.make(toName);
            
            for (int i = 0, n = affectedModuleNames.size(); i < n; i++) {
                ModuleName moduleName = affectedModuleNames.get(i);
                if (moduleName.equals(fromNameAsModuleName)) {
                    affectedModuleNames.remove(i);
                    affectedModuleNames.add(i, toNameAsModuleName);
                }
            }
        }
    }

    /**
     * Recompiles the changed modules in the workspace. 
     * @return True if the operation completed successfully, false otherwise.
     */
    private boolean recompile() {
        CompilerMessageLogger logger = new MessageLogger();
        StatusListener recompileStatusListener = new StatusListener.StatusListenerAdapter() {
            @Override
            public void setModuleStatus(StatusListener.Status.Module moduleStatus, ModuleName moduleName) {
                if (moduleStatus == StatusListener.SM_LOADED) {
                    incrementProgressBar();
                }
            }
        };
        workspaceManager.addStatusListener(recompileStatusListener);        
        
        Set<ModuleSourceDefinition> dependentSources = calculateDependentSources();
        ModuleName[] moduleNames = affectedModuleNames.toArray(new ModuleName[0]); 
        ModuleSourceDefinition[] moduleSourceDefintionArray = dependentSources.toArray(new ModuleSourceDefinition[0]);
        ModuleSourceDefinitionGroup sourceDefinitionGroup = new ModuleSourceDefinitionGroup(moduleSourceDefintionArray);
        workspaceManager.makeModules(moduleNames, sourceDefinitionGroup, logger);
        workspaceManager.removeStatusListener(recompileStatusListener);
        
        return !setErrorStatus(logger);
    }
    
    /**
     * Get the number of metadata files in the resource manager. 
     * The value lazily calculated the first time this method is called, and then a cached value is used
     * every time thereafter.
     * @return The number of metadata files in the resource manager.
     */
    private int getMetadataCount() {
        if (metadataCount == -1) {
            metadataCount = 0;
            ModuleName[] workspaceModuleNames = getWorkspace().getModuleNames();
            
            // loop through each module in the workspace
            for (final ModuleName moduleName : workspaceModuleNames) {
                
                ResourceStore.Module resourceStore = (ResourceStore.Module)getWorkspace().getResourceManager(moduleName, WorkspaceResource.METADATA_RESOURCE_TYPE).getResourceStore();
                
                Iterator<WorkspaceResource> it = resourceStore.getResourceIterator(moduleName);
                while (it.hasNext()) {
                    metadataCount++;
                    it.next();
                }
            }
        }
        
        return metadataCount;
    }
    private int metadataCount = -1;
       
    /**
     * Perform the rename operation and recompile the workspace. If errors are encountered, attempt to undo any changes
     * that have been made so far and then display the errors in the error list pane.
     * @return True if the refactorings were successful, false otherwise
     */
    private boolean doRefactorings() {
        
        try {
            
           // Perform refactorings
           
           lockValueEntry();
           setEnabledButtons(new JButton[]{}); // Disable all buttons
           enterProgressMode();
           clearErrorStatus();
           informationScrollPane.setVisible(false);
           pack();
           initializeRefactorer();
           
           
           statusLabel.setText(GemCutter.getResourceString("RNRD_CalculatingAffectedResources"));
           statusLabel.setIcon(null);
           
           if (!calcResources()) {
            return false;
        }
           
           // Now that we know how many resources there are, we can set the maximum value for the progress bar.
           // There should be two steps for each affected module (one for the updating, one for the recompilation),
           // plus one for design updating and one for the calcResources step we just completed.
           progressBar.setMaximum(affectedModuleNames.size() + calculateDependentSources().size() + getMetadataCount() + 2);
           
           // Set the value to one since we've already completed one step at this point.
           progressBar.setValue(1);
           
           statusLabel.setText(GemCutter.getResourceString("RNRD_UpdatingSourceFiles"));
           statusLabel.setIcon(null);
           if (!updateSourcesMetadataAndResources()) {
            return false;
        }
           
           statusLabel.setText(GemCutter.getResourceString("RNRD_UpdatingDesigns"));
           statusLabel.setIcon(null);
           if (!updateDesigns()) {
               statusLabel.setText(GemCutter.getResourceString("RNRD_BackingOut"));
               statusLabel.setIcon(ERROR_ICON);
               progressBar.setMaximum(affectedModuleNames.size());
               progressBar.setValue(0);
               
               undoSourcesMetadataAndResources();
               return false;
           }
           incrementProgressBar();
           
           updateAffectedModules();
           
           // If we are allowing duplicate renamings, give the user a chance to fix the errors that will have been introduced
           if (proposedFactoringUnsafe) {
               JOptionPane.showMessageDialog(getParent(), GemCutter.getResourceString("RNRD_ManualFixPrompt"), GemCutter.getResourceString("RNRD_ManualFixPromptTitle"), JOptionPane.PLAIN_MESSAGE);
           }
           
           statusLabel.setText(GemCutter.getResourceString("RNRD_Recompiling"));
           statusLabel.setIcon(null);
           if(!recompile()) {
               statusLabel.setText(GemCutter.getResourceString("RNRD_BackingOut"));
               statusLabel.setIcon(ERROR_ICON);
               enterProgressMode();
               progressBar.setMaximum(affectedModuleNames.size() + calculateDependentSources().size() + 3);
               
               undoUpdateDesigns();
               incrementProgressBar();
               undoSourcesMetadataAndResources();
               recompile();
               return false;
           }
           
           statusLabel.setText(GemCutter.getResourceString("RNRD_RefactoringComplete"));
           statusLabel.setIcon(OK_ICON);     
           
           refactorSuccessful = true;
           
           return true;

        } catch (Exception e) {
            DefaultListModel listModel = (DefaultListModel) errorsList.getModel();
            listModel.addElement(GemCutterMessages.getString("RNRD_ExceptionThrown"));
            informationScrollPane.setVisible(true);
            pack();
            System.out.println(e.toString());
            return false;
        }
    }
    
    /**
     * Item renderer for the combo box list used to choose a gem name. Displays names and appropriate icons.
     * 
     * @author Peter Cardwell
     */
    private static class NameComboListRenderer extends JLabel implements ListCellRenderer {
        
        private static final long serialVersionUID = 561510951582323314L;
        private ModuleTypeInfo typeInfo;
        private EntityType entityType;
        
        /**
         * Constructor
         * @param typeInfo
         * @param entityType
         */
        public NameComboListRenderer(ModuleTypeInfo typeInfo, EntityType entityType) {
            this.typeInfo = typeInfo;
            this.entityType = entityType;
            setOpaque(true);
        }
        
        public void setTypeInfo(ModuleTypeInfo typeInfo) {
            this.typeInfo = typeInfo;
        }
        
        public void setEntityType(EntityType entityType) {
            this.entityType = entityType;
        }
        
        /**
         * This method finds the image and text corresponding
         * to the selected value and returns the label, set up
         * to display the text and image.
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            String text = (String)value;
            
            Icon icon = null;
            if (entityType == EntityType.Gem) {
                if (typeInfo.getFunctionOrClassMethod(text) != null) {
                    icon = FUNCTION_ICON;
                } else if (typeInfo.getDataConstructor(text) != null) {
                    icon = DATACONS_ICON;
                } 
            } else if (entityType == EntityType.TypeClass) {
                icon = TYPECLASS_ICON;
            } else if (entityType == EntityType.TypeConstructor) {
                icon = TYPECONS_ICON;
            }
            setIcon(icon);
            setText(text);
            setFont(list.getFont());
            
            return this;
        }
    }
    
    /**
     * Populates the old name combo box with entities of the relevant type from the current module.
     */
    private void populateOldNameCombo() {
        List<String> entityList = new ArrayList<String>();
        MetaModule module = perspective.getMetaModule((ModuleName)sourceModuleList.getSelectedItem());
        String emptyModuleComboStr = "";    // The string that will be displayed in the name combo box if the module is empty
        String emptyModuleStatusStr = "";   // The string that will be displayed in the status text if the module is empty
        
        if( entityType == EntityType.Gem ) {
            
            for (int i = 0, n = module.getNGemEntities(); i < n; i++) {
                GemEntity entity = module.getNthGemEntity(i);            
                entityList.add(entity.getName().getUnqualifiedName());
            }
            emptyModuleComboStr = GemCutter.getResourceString("RNRD_EmptyModuleComboGem");
            emptyModuleStatusStr = GemCutter.getResourceString("RNRD_EmptyModuleStatusGem");
            
        } else if ( entityType == EntityType.TypeConstructor ) {
            
            TypeConstructor[] typeConstructors = module.getTypeConstructors();
            for (final TypeConstructor typeCons : typeConstructors) {
                if(typeCons.getName().getModuleName().equals(module.getName())) {
                    entityList.add(typeCons.getName().getUnqualifiedName());
                }
            }
            emptyModuleComboStr = GemCutter.getResourceString("RNRD_EmptyModuleComboTypeCons");
            emptyModuleStatusStr = GemCutter.getResourceString("RNRD_EmptyModuleStatusTypeCons");
            
        } else if ( entityType == EntityType.TypeClass ) {
            
            for (int i = 0, n = module.getTypeInfo().getNTypeClasses(); i < n; i++) {
                TypeClass typeClass = module.getTypeInfo().getNthTypeClass(i);
                if(typeClass.getName().getModuleName().equals(module.getName())) {
                    entityList.add(typeClass.getName().getUnqualifiedName());
                }
            }
            emptyModuleComboStr = GemCutter.getResourceString("RNRD_EmptyModuleComboTypeClass");
            emptyModuleStatusStr = GemCutter.getResourceString("RNRD_EmptyModuleStatusTypeClass");
        }
        Collections.sort(entityList);
        ((DefaultComboBoxModel)oldNameCombo.getModel()).removeAllElements();
        for (int i = 0, n = entityList.size(); i < n; i++) {
            ((DefaultComboBoxModel)oldNameCombo.getModel()).addElement(entityList.get(i));
        }
        if (entityType != EntityType.Module) {
            if (oldNameCombo.getItemCount() > 0) {
                oldNameCombo.setSelectedIndex(0);
                oldNameCombo.setEnabled(true);
                newNameField.setEnabled(true);
                emptyModule = false;            
            } else {
                oldNameCombo.setEnabled(false);
                newNameField.setEnabled(false);
                ((DefaultComboBoxModel)oldNameCombo.getModel()).addElement (emptyModuleComboStr);
                newNameField.setText("");
                statusLabel.setText(emptyModuleStatusStr);
                statusLabel.setIcon(ERROR_ICON);
                emptyModule = true;
            }
            NameComboListRenderer renderer = (NameComboListRenderer)oldNameCombo.getRenderer();
        
            renderer.setTypeInfo(perspective.getMetaModule((ModuleName)sourceModuleList.getSelectedItem()).getTypeInfo());
            renderer.setEntityType(entityType);
        }
    }

    /**
     * Displays the dialog, and waits for it to disappear.
     * @return The result of the operation, or null if no refactoring occurred
     */
    public Result display() {
        initialize();
        super.setVisible(true);
        if(refactorSuccessful) {
            if (entityType == EntityType.Module) {
                return new Result(fromName, toName, category); 
            } else {
                QualifiedName qualifiedFromName = QualifiedName.make(moduleName, fromName);
                QualifiedName qualifiedToName = QualifiedName.make(moduleName, toName);
                return new Result(qualifiedFromName.getQualifiedName(), qualifiedToName.getQualifiedName(), category);
            }
        } else {
            return null;
        }
    }

    /**
     * If the window is smaller then its minimum size then the
     * size is set to the minimum size.
     * 
     * @return true if minimum dialog size has been enforced; false if not
     */
    private boolean enforceMinimumSize(Dimension minSize) {
    
        boolean wasBadSize = false;
        Dimension size = getSize();
        
        if (size.height < minSize.height) {
            size.height = minSize.height;
            wasBadSize = true;
        }
        
        if (!informationScrollPane.isVisible() && size.height > minSize.height) {
            size.height = minSize.height;
            wasBadSize = true;
        }
        
        if (size.width  < minSize.width) {
            size.width = minSize.width;
            wasBadSize = true;
        }
        
        setSize(size);
        return wasBadSize;
    }

    /**
     * @return The action associated with the back button (which unlocks the value entry boxes and re-enables the ok and cancel buttons). 
     */
    private Action getBackAction() {
        if (backAction == null) {
            backAction = new AbstractAction(GemCutter.getResourceString("RNRD_Back")) {
                
                private static final long serialVersionUID = -6520848746650896219L;

                public void actionPerformed(ActionEvent evt) {
                    informationScrollPane.setVisible(false);
                    setVisibleButtons(new JButton[] {okButton, cancelButton});
                    setEnabledButtons(new JButton[] {okButton, cancelButton});
                    unlockValueEntry();
                    updateUIState();
                    pack();
                }
            };
        }

        return backAction;
    }
    
    /**
     * @return The action associated with the retry button, which does the same thing as the OK button.
     */
    private Action getRetryAction() {
        if (retryAction == null) {
            retryAction = new AbstractAction(GemCutter.getResourceString("RNRD_Retry")) { 
                
                private static final long serialVersionUID = -4987680767287672031L;

                public void actionPerformed(ActionEvent evt) {
                    getOkAction().actionPerformed(evt);
                }
            };
        }
        
        return retryAction;
    }
    
    /**
     * @return The action associated with the OK button, which creates a refactoring thread and runs it.
     */
    private Action getOkAction() {
        if (okAction == null) {
            okAction = new AbstractAction(GemCutter.getResourceString("RNRD_OK")) {
                                                        
                private static final long serialVersionUID = 1157482755328668668L;

                public void actionPerformed(ActionEvent evt) {
                    
                    if (!fieldsAreValid()) {
                        return;
                    }
                
                    if (fromName.equals(toName)) {
                        // Nothing to do, so exit
                        dispose();
                    }
                    
                    if (dirtyFields) {
    
                        statusLabel.setText(GemCutter.getResourceString("RNRD_Inspecting"));
                        statusLabel.setIcon(null);
                        if ((refactoringThread == null) || (!refactoringThread.isAlive())) {
                            refactoringThread = new RefactoringThread();
                            refactoringThread.start();
                        } else {
                            throw new IllegalStateException("Tried to start new refactoring thread while another was running.");
                        }
                        
                    } else {
                        
                        if ((refactoringThread == null) || (!refactoringThread.isAlive())) {
                            refactoringThread = new RefactoringThread();
                            refactoringThread.start();
                        } else {
                            throw new IllegalStateException("Tried to start new refactoring thread while another was running.");
                        }
                    }
                }
            };
        }
        
        return okAction;
    }

    /**
     * @return The action associated with the cancel button, which closes the dialog as long as a refactoring is not occurring.
     */
    private Action getCancelAction() {
        if (cancelAction == null) {
            cancelAction = new AbstractAction(GemCutter.getResourceString("RNRD_Cancel")) {
                                                        
                private static final long serialVersionUID = 1531004724388023830L;

                synchronized public void actionPerformed(ActionEvent evt) {
                   
                    if ((refactoringThread != null) && (refactoringThread.isAlive())) {
                        throw new IllegalStateException("Cancel action occurred while refactoring was still in progress");
                    }
                    dispose();
                }
            };
        }
        
        return cancelAction;
    }

    /**
     * Ensures that all the elements of the given array are enabled, and that ONLY the elements
     * of the given array are enabled. That is, if a button is a member of dialogButtons but not 
     * a member of enabledButtons, it will be disabled.
     * @param enabledButtons  The array of buttons to be enabled. Each element of this array should
     * also be an element of dialogButtons.
     */
    private void setEnabledButtons(JButton[] enabledButtons) {
        Set<JButton> enabledButtonSet = new HashSet<JButton>(Arrays.asList(enabledButtons));
        
        for (final JButton btn : dialogButtons) {
            // For each dialog button b, set b to be enabled if and only if enabledButtonSet contains b.
            btn.setEnabled (enabledButtonSet.contains(btn));
        }
        
    }

    /**
     * Ensures that all the elements of the given array are made visible, and that ONLY the elements
     * of the given array are visible. That is, if a button is a member of dialogButtons but not 
     * a member of visibleButtons, it will be made invisible.
     * @param visibleButtons The array of buttons to be made visible. Each element of this array should
     * also be an element of dialogButtons.
     */
    private void setVisibleButtons(JButton[] visibleButtons) {
        Set<JButton> visibleButtonSet = new HashSet<JButton>(Arrays.asList(visibleButtons));
        
        for (final JButton btn : dialogButtons) {
            // For each dialog button b, set b to be visible if and only if visibleButtonSet contains b.
            btn.setVisible (visibleButtonSet.contains(btn));
        }
    }

    /** Set all the components returned by getValueEntryComponents() to be disabled. */
    private void lockValueEntry() {
        for (final Component valueEntryComp : valueEntryComponents) {
            valueEntryComp.setEnabled(false);
        }
    }

    /** Set all the components returned by getValueEntryComponents() to be enabled. */
    private void unlockValueEntry() {
        for (final Component valueEntryComp : valueEntryComponents) {
            valueEntryComp.setEnabled(true);
        }
    }

    /** Actions to perform by the refactoring worker thread when it completes */
    private void exitProgressMode() {
        progressBar.setValue(0);
        progressLabel.setVisible(false);
        progressBar.setVisible(false);
    }

    /** Lock the user interface and make the progress bar visible */
    private void enterProgressMode() {
        progressBar.setValue(0);
        progressLabel.setVisible(true);
        progressBar.setVisible(true);
    }
    
    /** Adds one to the value of the progress bar and repaints the dialog. */
    private void incrementProgressBar() {
        progressBar.setValue(progressBar.getValue() + 1);
        RenameRefactoringDialog.this.repaint();
    }

    /** Perform validation on the entry fields and set the status to OK if there are no problems. */
    private void updateUIState() {
        
        if (!fieldsAreValid()) {            
            return;
        }
        
        if (!proposedFactoringUnsafe) {
            // Clear errors and warnings from previous checks
            statusLabel.setText(GemCutter.getResourceString("RNRD_OkMessage"));
            statusLabel.setIcon(null);
        }
    }
    
    /** Clears the error list. */
    private void clearErrorStatus() {
        DefaultListModel listModel = (DefaultListModel) errorsList.getModel();
        listModel.clear();
    }
     
    /**
     * If the given status object contains errors, display them in the error list and return true.
     * @param status The Status object to check for errors
     * @return True of the Status object contained errors, false otherwise.
     */
    private boolean setErrorStatus(Status status) {
        if (status.getSeverity().compareTo(Status.Severity.ERROR) < 0) {
            return false;
        }
        
        DefaultListModel listModel = (DefaultListModel) errorsList.getModel();
        Status[] msgs = status.getChildren();
        for (int i = 0; i < msgs.length; ++i) {
            listModel.addElement(msgs[i].getMessage());
        }
        
        informationScrollPane.setVisible(true);
        
        setSize(getPreferredSize());
        
        return true;
    }

    /**
     * If the given logger contains errors, display them in the informationScrollPane and return true.
     * @param logger The logger to check for errors.  Cannot be null.
     * @return True if there were errors, false otherwise
     */
    private boolean setErrorStatus(CompilerMessageLogger logger) {
        
        if(logger.getNErrors() == 0) {
            return false;
        }
        
        List<CompilerMessage> messages = logger.getCompilerMessages();
    
        DefaultListModel listModel = (DefaultListModel) errorsList.getModel();
        for (int i = 0, n = messages.size(); i < n; i++) {
            CompilerMessage message = messages.get(i);
            if(message.getSeverity().compareTo(CompilerMessage.Severity.ERROR) < 0) {
                continue;
            }
            if (message.getException() instanceof CALRefactoringException) {
                CALRefactoringException refactoringException = (CALRefactoringException) message.getException();
                listModel.addElement(refactoringException.getDescription());
            } else {
                listModel.addElement(message.getMessage());
            }
        }
        
        informationScrollPane.setVisible(true);
        setSize(getPreferredSize());
        
        return true;
    }

    /**
     * @return the CALWorkspace on which the refactorer operates.
     */
    private CALWorkspace getWorkspace() {
        return perspective.getWorkspace();
    }
    
    /**
     * Implement this if we want to put an image in the top right corner of the dialog.
     * @return The image that should be displayed, or null if nothing should be displayed.
     */
    private ImageIcon getDialogIcon() {
        return null;
    }
    
    /**
     * @return the white title panel that appears at the top of the dialog
     */
    private JPanel getTitlePanel() {
        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        
        Border compoundBorder = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
                                                                   BorderFactory.createEmptyBorder(5, 5, 5, 5));
        titlePanel.setBorder(compoundBorder);
        titlePanel.setLayout(new BorderLayout(5, 5));
        
        JPanel mainTitlePanel = new JPanel();
        mainTitlePanel.setBackground(Color.WHITE);
        mainTitlePanel.setLayout(new BorderLayout(5, 5));
        titlePanel.add(mainTitlePanel, BorderLayout.NORTH);
        
        // Add the title
        titleLabel.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 2));
        mainTitlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Add the icon in the top right corner
        mainTitlePanel.add(new JLabel(getDialogIcon()), BorderLayout.EAST);
        
        // Add the status text
        titlePanel.add(statusLabel, BorderLayout.SOUTH);
        
        return titlePanel;
    }
    
    /**
     * @return the main panel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            getContentPane().add(mainPanel);
            mainPanel.setLayout(new GridBagLayout());
            
            int gy = -1;
            
            if (!entityTypeIsPreset) {
                gy++;
                JPanel radioPanel = getEntityTypeSelectPanel();
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = gy;
                constraints.gridwidth = 4;
                constraints.weightx = 1.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(0,5,2,2);
                mainPanel.add(radioPanel, constraints);
            }          
                   
            if(!automatedMode) {
                gy++;
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = gy;
                constraints.gridwidth = 5;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(0,5,2,5);
                mainPanel.add(getValueEntryPanel(), constraints);            
            }      
            
            {
                gy++;
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = gy;
                constraints.gridwidth = 5;
                constraints.weightx = 1.0;
                constraints.weighty = 1.0;
                constraints.insets = new Insets(5,5,5,5);
                constraints.fill = GridBagConstraints.BOTH;
                mainPanel.add(informationScrollPane, constraints);
            }
    
            {
                gy++;
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = gy;
                constraints.gridwidth = 1;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(5,5,5,5);
                mainPanel.add(progressLabel, constraints);
            }
            
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = gy;
                constraints.gridwidth = 4;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(5,5,5,5);
                mainPanel.add(progressBar, constraints);
            }
            
            {
                gy++;
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = gy;
                constraints.gridwidth = 5;
                constraints.weightx = 1.0;
                constraints.weighty = 1.0;
                constraints.fill = GridBagConstraints.VERTICAL;
                mainPanel.add(new JLabel(""), constraints);
            }
            
            mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        }
        return mainPanel;
    }
    
    /**
     *  Returns a JPanel containing a radio button group for selecting the entity type, a drop down list for selecting the module name,
     *  a combo box for selecting the "from" name, and a text box for entering the "to" name. Note that all but the "to" name field are 
     *  not visible if the from name value is preset. 
     */
    private JPanel getValueEntryPanel() {
        
        JPanel fromFieldPanel = new JPanel();
        fromFieldPanel.setLayout(new GridBagLayout());
        
        int gy = 0;
        if (!fromNameIsPreset) { 
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = gy;
                constraints.gridwidth = 1;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(2,5,2,5);
                fromFieldPanel.add(sourceModuleLabel, constraints);
            }
            
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = gy;
                constraints.gridwidth = 4;
                constraints.weightx = 0.0;
                constraints.weighty = 0.0;
                constraints.insets = new Insets(2,5,2,5);
                constraints.fill = GridBagConstraints.HORIZONTAL;
                fromFieldPanel.add(sourceModuleList, constraints);
            }
            
            if (entityType != EntityType.Module) { 
                gy++;
                {
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridx = 0;
                    constraints.gridy = gy;
                    constraints.gridwidth = 1;
                    constraints.weightx = 0.0;
                    constraints.weighty = 0.0;
                    constraints.insets = new Insets(5,5,5,5);
                    constraints.fill = GridBagConstraints.HORIZONTAL;
                    fromFieldPanel.add(oldNameLabel, constraints);
                }
                
                {
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridx = 1;
                    constraints.gridy = gy;
                    constraints.gridwidth = 4;
                    constraints.weightx = 1.0;
                    constraints.weighty = 0.0;
                    constraints.insets = new Insets(5,5,5,5);
                    constraints.fill = GridBagConstraints.HORIZONTAL;
                    fromFieldPanel.add(oldNameCombo, constraints);
                }
            }
        }
        
        gy++;
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = gy;
            constraints.gridwidth = 1;
            constraints.weightx = 0.0;
            constraints.weighty = 0.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(5,5,5,5);
            fromFieldPanel.add(newNameLabel, constraints);
        }
        
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 1;
            constraints.gridy = gy;
            constraints.gridwidth = 4;
            constraints.weightx = 1.0;
            constraints.weighty = 0.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(5,5,5,5);
            Dimension minSize = newNameField.getMinimumSize();
            minSize.width = 250;
            newNameField.setPreferredSize(minSize);
            fromFieldPanel.add(newNameField, constraints);
        }
        
        return fromFieldPanel;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this.titleLabel.setText(title);
    }
    
   
    /**
     * Lay out the components in the dialog. This method should only be called once from the initialize method.
     */
    private void layoutDialog() {
        
        // Add components to the dialog
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getTitlePanel(), BorderLayout.NORTH);
        getContentPane().add(getMainPanel(), BorderLayout.CENTER);
        if (!automatedMode) {
            getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        }
        
    }
}
