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
 * CALDocGenerationDialog.java
 * Creation date: Oct 17, 2005.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import org.openquark.cal.caldoc.FileSystemFileGenerator;
import org.openquark.cal.caldoc.HTMLDocumentationGeneratorConfiguration;
import org.openquark.cal.filter.CompositeModuleFilter;
import org.openquark.cal.filter.CompositeScopedEntityFilter;
import org.openquark.cal.filter.ExcludeTestModulesFilter;
import org.openquark.cal.filter.ModuleFilter;
import org.openquark.cal.filter.PublicEntitiesOnlyFilter;
import org.openquark.cal.filter.QualifiedNameBasedScopedEntityFilter;
import org.openquark.cal.filter.RegExpBasedModuleFilter;
import org.openquark.cal.filter.RegExpBasedQualifiedNameFilter;
import org.openquark.cal.filter.ScopedEntityFilter;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.WorkspaceManager;


/**
 * This class implements the dialog box for specifying the options for the CALDoc HTML Documentation Generator.
 *
 * @author Joseph Wong
 */
final class CALDocGenerationDialog extends JDialog {
    private static final long serialVersionUID = -7063140314098167541L;

    ////
    /// Preference Keys
    //
    private static final String CALDOC_GENERATION_BOTTOM_PREF_KEY = "CALDocGenerationBottom";
    private static final String CALDOC_GENERATION_FOOTER_PREF_KEY = "CALDocGenerationFooter";
    private static final String CALDOC_GENERATION_HEADER_PREF_KEY = "CALDocGenerationHeader";
    private static final String CALDOC_GENERATION_DOC_TITLE_PREF_KEY = "CALDocGenerationDocTitle";
    private static final String CALDOC_GENERATION_WINDOW_TITLE_PREF_KEY = "CALDocGenerationWindowTitle";
    private static final String CALDOC_GENERATION_GENERATE_USAGE_INDICES_PREF_KEY = "CALDocGenerationGenerateUsageIndices";
    private static final String CALDOC_GENERATION_SEPARATE_INSTANCE_DOC_PREF_KEY = "CALDocGenerationSeparateInstanceDoc";
    private static final String CALDOC_GENERATION_DISPLAY_PRELUDE_NAMES_AS_UNQUALIFIED_PREF_KEY = "CALDocGenerationDisplayPreludeNamesAsUnqualified";
    private static final String CALDOC_GENERATION_GENERATE_VERSION_INFO_PREF_KEY = "CALDocGenerationGenerateVersionInfo";
    private static final String CALDOC_GENERATION_GENERATE_AUTHOR_INFO_PREF_KEY = "CALDocGenerationGenerateAuthorInfo";
    private static final String CALDOC_GENERATION_HIDE_OVERRIDEN_CALDOC_PREF_KEY = "CALDocGenerationHideOverridenCALDoc";
    private static final String CALDOC_GENERATION_GENERATE_METADATA_PREF_KEY = "CALDocGenerationGenerateMetadata";
    private static final String CALDOC_GENERATION_EXCLUDE_TEST_MODULES_PREF_KEY = "CALDocGenerationExcludeTestModules";
    private static final String CALDOC_GENERATION_EXCLUDE_SCOPED_ENTITY_FILTER_REG_EXP_PREF_KEY = "CALDocGenerationExcludeScopedEntityFilterRegExp";
    private static final String CALDOC_GENERATION_INCLUDE_ONLY_SCOPED_ENTITY_FILTER_REG_EXP_PREF_KEY = "CALDocGenerationIncludeOnlyScopedEntityFilterRegExp";
    private static final String CALDOC_GENERATION_EXCLUDE_MODULE_FILTER_REG_EXP_PREF_KEY = "CALDocGenerationExcludeModuleFilterRegExp";
    private static final String CALDOC_GENERATION_INCLUDE_ONLY_MODULE_FILTER_REG_EXP_PREF_KEY = "CALDocGenerationIncludeOnlyModuleFilterRegExp";
    private static final String CALDOC_GENERATION_EXCLUDE_SCOPED_ENTITY_FILTER_PREF_KEY = "CALDocGenerationExcludeScopedEntityFilter";
    private static final String CALDOC_GENERATION_INCLUDE_ONLY_SCOPED_ENTITY_FILTER_PREF_KEY = "CALDocGenerationIncludeOnlyScopedEntityFilter";
    private static final String CALDOC_GENERATION_EXCLUDE_MODULE_FILTER_PREF_KEY = "CALDocGenerationExcludeModuleFilter";
    private static final String CALDOC_GENERATION_INCLUDE_ONLY_MODULE_FILTER_PREF_KEY = "CALDocGenerationIncludeOnlyModuleFilter";
    private static final String CALDOC_GENERATION_PUBLIC_ENTRIES_ONLY_PREF_KEY = "CALDocGenerationPublicEntriesOnly";
    private static final String CALDOC_GENERATION_OUTPUT_DIRECTORY_PREF_KEY = "CALDocGenerationOutputDirectory";

    /** The CAL workspace associated with the owner of this dialog. */
    private final CALWorkspace workspace;

    /** Text field for specifying the output directory. (command line: -d) */
    private final JTextField outputDirectoryField = new JTextField();
    /** Button for launching a directory chooser for the output directory. */
    private final JButton browseOutputDirectoryButton = new JButton(GemCutter.getResourceString("CALDoc_BrowseOutputDirectoryButton"));
    
    /** The button group for the two radio buttons for choosing the generation scope. */
    private final ButtonGroup scopeOptionGroup = new ButtonGroup();
    /** Radio button for specifying the generation of public entities only. (command line: -public) */
    private final JRadioButton publicEntitiesOnlyRadioButton = new JRadioButton(GemCutter.getResourceString("CALDoc_PublicEntitiesOnlyOption"));
    /** Radio button for specifying the generation of all entities. (command line: -private) */
    private final JRadioButton allEntitiesRadioButton = new JRadioButton(GemCutter.getResourceString("CALDoc_AllEntitiesOption"));
    
    /** Check box for enabling the include-only module filter. */
    private final JCheckBox includeOnlyModuleFilterCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_IncludeOnlyModuleFilterOption"));
    /** Combo box for specifying that only the modules matching the given regexp should have documentation generated. (command line: -modules) */ 
    private final JComboBox includeOnlyModuleFilterField = new JComboBox();
    /** Check box for enabling the exclude module filter. */
    private final JCheckBox excludeModuleFilterCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_ExcludeModuleFilterOption"));
    /** Combo box for specifying that the modules matching the given regexp should not have documentation generated. (command line: -excludeModules) */
    private final JComboBox excludeModuleFilterField = new JComboBox();

    /** Check box for enabling the include-only scoped entity filter. */
    private final JCheckBox includeOnlyScopedEntityFilterCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_IncludeOnlyScopedEntityFilterOption"));
    /** Combo box for specifying that only the scoped entities matching the given regexp should have documentation generated. (command line: -entities) */ 
    private final JComboBox includeOnlyScopedEntityFilterField = new JComboBox();
    /** Check box for enabling the exclude scoped entity filter. */
    private final JCheckBox excludeScopedEntityFilterCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_ExcludeScopedEntityFilterOption"));
    /** Combo box for specifying that the scoped entities matching the given regexp should not have documentation generated. (command line: -excludeEntities) */
    private final JComboBox excludeScopedEntityFilterField = new JComboBox();
    
    /** Check box for enabling the exclude test modules filter. (command line: -XexcludeTestModules) */
    private final JCheckBox excludeTestModulesCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_ExcludeTestModulesOption"));
    
    /** Check box for including metadata in the generated documentation (command line: -metadata). */
    private final JCheckBox generateMetadataCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_GenerateMetadataOption"));
    /** Check box for excluding CALDoc for which there is metadata (command line: -metadata override). */
    private final JCheckBox hideOverridenCALDocCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_HideOverridenCALDocOption"));
    
    /** Check box for enabling the generation of author info. (command line: -author) */
    private final JCheckBox generateAuthorInfoCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_GenerateAuthorInfoOption"));
    /** Check box for enabling the generation of version info. (command line: -version) */
    private final JCheckBox generateVersionInfoCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_GenerateVersionInfoOption"));
    /** Check box for specifying that Prelude names should always be unqualified. (command line [for disabling]: -qualifyPreludeNames) */
    private final JCheckBox displayPreludeNamesAsUnqualifiedCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_DisplayPreludeNamesAsUnqualifiedOption"));
    
    /** Check box for enabling the generation of usage indices. (command line: -use) */
    private final JCheckBox generateUsageIndicesCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_GenerateUsageIndicesOption"));

    /** Check box for enabling the separation of instance documentation from the main documentation pages. (command line [for disabling]: -doNotSeparateInstanceDoc) */
    private final JCheckBox separateInstanceDocCheckBox = new JCheckBox(GemCutter.getResourceString("CALDoc_SeparateInstanceDocOption"));

    /** Text field for specifying the window title text. (command line: -windowTitle) */
    private final JTextField windowTitleField = new JTextField();
    /** Text field for specifying the documentation title in HTML. (command line: -docTitle) */
    private final JTextPane docTitleField = new JTextPane();
    /** Text field for specifying the header in HTML. (command line: -header) */
    private final JTextPane headerField = new JTextPane();
    /** Text field for specifying the footer in HTML. (command line: -footer) */
    private final JTextPane footerField = new JTextPane();
    /** Text field for specifying the fine print at the bottom of a page in HTML. (command line: -bottom) */
    private final JTextPane bottomField = new JTextPane();
    
    /** The OK button. */
    private final JButton okButton = new JButton(GemCutter.getResourceString("LOC_OK"));
    /** The Cancel button. */
    private final JButton cancelButton = new JButton(GemCutter.getResourceString("LOC_Cancel"));
    
    /** Whether OK was selected at the closing of the dialog. */
    private boolean okSelected = false;
    
    /**
     * Constructs a CALDocGenerationDialog.
     * @param owner the owner of the dialog.
     * @param workspace the CAL workspace of the owner.
     * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
     */
    CALDocGenerationDialog(Frame owner, CALWorkspace workspace) throws HeadlessException {
        super(owner);
        this.workspace = workspace;
        
        loadExistingValuesAndMakeEditable();
        initialize();
        setupUI();
    }

    /**
     * Loads the existing values of the fields from the GemCutter preferences and make the fields editable.
     */
    private void loadExistingValuesAndMakeEditable() {
        loadStringToTextComponent(outputDirectoryField, CALDOC_GENERATION_OUTPUT_DIRECTORY_PREF_KEY, "");
        
        boolean publicEntriesOnly = GemCutter.getPreferences().getBoolean(CALDOC_GENERATION_PUBLIC_ENTRIES_ONLY_PREF_KEY, true);
        if (publicEntriesOnly) {
            publicEntitiesOnlyRadioButton.setSelected(true);
        } else {
            allEntitiesRadioButton.setSelected(true);
        }
        
        loadBooleanToCheckBox(includeOnlyModuleFilterCheckBox, CALDOC_GENERATION_INCLUDE_ONLY_MODULE_FILTER_PREF_KEY, false);
        loadBooleanToCheckBox(excludeModuleFilterCheckBox, CALDOC_GENERATION_EXCLUDE_MODULE_FILTER_PREF_KEY, false);
        loadBooleanToCheckBox(includeOnlyScopedEntityFilterCheckBox, CALDOC_GENERATION_INCLUDE_ONLY_SCOPED_ENTITY_FILTER_PREF_KEY, false);
        loadBooleanToCheckBox(excludeScopedEntityFilterCheckBox, CALDOC_GENERATION_EXCLUDE_SCOPED_ENTITY_FILTER_PREF_KEY, false);

        loadStringToComboBoxAndMakeEditable(includeOnlyModuleFilterField, CALDOC_GENERATION_INCLUDE_ONLY_MODULE_FILTER_REG_EXP_PREF_KEY, "");
        loadStringToComboBoxAndMakeEditable(excludeModuleFilterField, CALDOC_GENERATION_EXCLUDE_MODULE_FILTER_REG_EXP_PREF_KEY, "");
        loadStringToComboBoxAndMakeEditable(includeOnlyScopedEntityFilterField, CALDOC_GENERATION_INCLUDE_ONLY_SCOPED_ENTITY_FILTER_REG_EXP_PREF_KEY, "");
        loadStringToComboBoxAndMakeEditable(excludeScopedEntityFilterField, CALDOC_GENERATION_EXCLUDE_SCOPED_ENTITY_FILTER_REG_EXP_PREF_KEY, "");
        
        loadBooleanToCheckBox(excludeTestModulesCheckBox, CALDOC_GENERATION_EXCLUDE_TEST_MODULES_PREF_KEY, false);
        
        loadBooleanToCheckBox(generateMetadataCheckBox, CALDOC_GENERATION_GENERATE_METADATA_PREF_KEY, false);
        loadBooleanToCheckBox(hideOverridenCALDocCheckBox, CALDOC_GENERATION_HIDE_OVERRIDEN_CALDOC_PREF_KEY, false);
        loadBooleanToCheckBox(generateAuthorInfoCheckBox, CALDOC_GENERATION_GENERATE_AUTHOR_INFO_PREF_KEY, false);
        loadBooleanToCheckBox(generateVersionInfoCheckBox, CALDOC_GENERATION_GENERATE_VERSION_INFO_PREF_KEY, false);
        loadBooleanToCheckBox(displayPreludeNamesAsUnqualifiedCheckBox, CALDOC_GENERATION_DISPLAY_PRELUDE_NAMES_AS_UNQUALIFIED_PREF_KEY, true);
        loadBooleanToCheckBox(generateUsageIndicesCheckBox, CALDOC_GENERATION_GENERATE_USAGE_INDICES_PREF_KEY, false);
        loadBooleanToCheckBox(separateInstanceDocCheckBox, CALDOC_GENERATION_SEPARATE_INSTANCE_DOC_PREF_KEY, true);
        
        loadStringToTextComponent(windowTitleField, CALDOC_GENERATION_WINDOW_TITLE_PREF_KEY, "");
        loadStringToTextComponent(docTitleField, CALDOC_GENERATION_DOC_TITLE_PREF_KEY, "");
        loadStringToTextComponent(headerField, CALDOC_GENERATION_HEADER_PREF_KEY, "");
        loadStringToTextComponent(footerField, CALDOC_GENERATION_FOOTER_PREF_KEY, "");
        loadStringToTextComponent(bottomField, CALDOC_GENERATION_BOTTOM_PREF_KEY, "");
    }

    /**
     * Loads the preference value specified by its key into a combo box, and make it editable.
     * @param comboBox the combo box.
     * @param prefKey the preference key.
     * @param defaultValue the default value to use if the key is not found in the preferences.
     */
    private void loadStringToComboBoxAndMakeEditable(JComboBox comboBox, String prefKey, String defaultValue) {
        comboBox.setEditable(true);
        comboBox.getEditor().setItem(GemCutter.getPreferences().get(prefKey, defaultValue));
    }

    /**
     * Loads the preference value specified by its key into a text component.
     * @param textComponent the text component.
     * @param prefKey the preference key.
     * @param defaultValue the default value to use if the key is not found in the preferences.
     */
    private void loadStringToTextComponent(JTextComponent textComponent, String prefKey, String defaultValue) {
        textComponent.setText(GemCutter.getPreferences().get(prefKey, defaultValue));
    }

    /**
     * Loads the preference value specified by its key into a check box.
     * @param checkBox the check box.
     * @param prefKey the preference key.
     * @param defaultValue the default value to use if the key is not found in the preferences.
     */
    private void loadBooleanToCheckBox(JCheckBox checkBox, String prefKey, boolean defaultValue) {
        checkBox.setSelected(GemCutter.getPreferences().getBoolean(prefKey, defaultValue));
    }
    
    /**
     * Saves the values of the dialog fields into GemCutter's preferences.
     */
    private void saveValuesToPreferences() {
        saveStringFromTextComponent(outputDirectoryField, CALDOC_GENERATION_OUTPUT_DIRECTORY_PREF_KEY);
        
        GemCutter.getPreferences().putBoolean(CALDOC_GENERATION_PUBLIC_ENTRIES_ONLY_PREF_KEY, shouldGeneratePublicEntriesOnly());
        
        saveBooleanFromCheckBox(includeOnlyModuleFilterCheckBox, CALDOC_GENERATION_INCLUDE_ONLY_MODULE_FILTER_PREF_KEY);
        saveBooleanFromCheckBox(excludeModuleFilterCheckBox, CALDOC_GENERATION_EXCLUDE_MODULE_FILTER_PREF_KEY);
        saveBooleanFromCheckBox(includeOnlyScopedEntityFilterCheckBox, CALDOC_GENERATION_INCLUDE_ONLY_SCOPED_ENTITY_FILTER_PREF_KEY);
        saveBooleanFromCheckBox(excludeScopedEntityFilterCheckBox, CALDOC_GENERATION_EXCLUDE_SCOPED_ENTITY_FILTER_PREF_KEY);

        saveStringFromComboBox(includeOnlyModuleFilterField, CALDOC_GENERATION_INCLUDE_ONLY_MODULE_FILTER_REG_EXP_PREF_KEY);
        saveStringFromComboBox(excludeModuleFilterField, CALDOC_GENERATION_EXCLUDE_MODULE_FILTER_REG_EXP_PREF_KEY);
        saveStringFromComboBox(includeOnlyScopedEntityFilterField, CALDOC_GENERATION_INCLUDE_ONLY_SCOPED_ENTITY_FILTER_REG_EXP_PREF_KEY);
        saveStringFromComboBox(excludeScopedEntityFilterField, CALDOC_GENERATION_EXCLUDE_SCOPED_ENTITY_FILTER_REG_EXP_PREF_KEY);
        
        saveBooleanFromCheckBox(excludeTestModulesCheckBox, CALDOC_GENERATION_EXCLUDE_TEST_MODULES_PREF_KEY);
        
        saveBooleanFromCheckBox(generateMetadataCheckBox, CALDOC_GENERATION_GENERATE_METADATA_PREF_KEY);
        saveBooleanFromCheckBox(hideOverridenCALDocCheckBox, CALDOC_GENERATION_HIDE_OVERRIDEN_CALDOC_PREF_KEY);
        saveBooleanFromCheckBox(generateAuthorInfoCheckBox, CALDOC_GENERATION_GENERATE_AUTHOR_INFO_PREF_KEY);
        saveBooleanFromCheckBox(generateVersionInfoCheckBox, CALDOC_GENERATION_GENERATE_VERSION_INFO_PREF_KEY);
        saveBooleanFromCheckBox(displayPreludeNamesAsUnqualifiedCheckBox, CALDOC_GENERATION_DISPLAY_PRELUDE_NAMES_AS_UNQUALIFIED_PREF_KEY);
        saveBooleanFromCheckBox(generateUsageIndicesCheckBox, CALDOC_GENERATION_GENERATE_USAGE_INDICES_PREF_KEY);
        saveBooleanFromCheckBox(separateInstanceDocCheckBox, CALDOC_GENERATION_SEPARATE_INSTANCE_DOC_PREF_KEY);
        
        saveStringFromTextComponent(windowTitleField, CALDOC_GENERATION_WINDOW_TITLE_PREF_KEY);
        saveStringFromTextComponent(docTitleField, CALDOC_GENERATION_DOC_TITLE_PREF_KEY);
        saveStringFromTextComponent(headerField, CALDOC_GENERATION_HEADER_PREF_KEY);
        saveStringFromTextComponent(footerField, CALDOC_GENERATION_FOOTER_PREF_KEY);
        saveStringFromTextComponent(bottomField, CALDOC_GENERATION_BOTTOM_PREF_KEY);
    }

    /**
     * Saves the value of the combo box using the given preference key into GemCutter's preferences.
     * @param comboBox the combo box.
     * @param prefKey the preference key.
     */
    private void saveStringFromComboBox(JComboBox comboBox, String prefKey) {
        String selectedItem = (String)comboBox.getSelectedItem();
        if (selectedItem != null) {
            GemCutter.getPreferences().put(prefKey, selectedItem.trim());
        }
    }

    /**
     * Saves the value of the text component using the given preference key into GemCutter's preferences.
     * @param textComponent the text component.
     * @param prefKey the preference key.
     */
    private void saveStringFromTextComponent(JTextComponent textComponent, String prefKey) {
        GemCutter.getPreferences().put(prefKey, textComponent.getText().trim());
    }

    /**
     * Saves the value of the check box using the given preference key into GemCutter's preferences.
     * @param checkBox the check box.
     * @param prefKey the preference key.
     */
    private void saveBooleanFromCheckBox(JCheckBox checkBox, String prefKey) {
        GemCutter.getPreferences().putBoolean(prefKey, checkBox.isSelected());
    }

    /**
     * Initializes the various member fields, e.g. hooking up listeners and actions,
     * but does not setup or layout the UI.
     */
    private void initialize() {
        // group together the scope-related radio buttons
        scopeOptionGroup.add(publicEntitiesOnlyRadioButton);
        scopeOptionGroup.add(allEntitiesRadioButton);
        
        // link the two metadata-related check boxes
        linkToggleWithField(generateMetadataCheckBox, hideOverridenCALDocCheckBox);
        
        // link up check boxes to the fields they enable/disable
        linkToggleWithField(includeOnlyModuleFilterCheckBox, includeOnlyModuleFilterField);
        linkToggleWithField(excludeModuleFilterCheckBox, excludeModuleFilterField);
        linkToggleWithField(includeOnlyScopedEntityFilterCheckBox, includeOnlyScopedEntityFilterField);
        linkToggleWithField(excludeScopedEntityFilterCheckBox, excludeScopedEntityFilterField);

        // put in the standard exclude filter for entity filtering
        if (!excludeScopedEntityFilterField.getEditor().getItem().equals(GemCutter.EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT)) {
            excludeScopedEntityFilterField.addItem(GemCutter.EXCLUDE_FUNCTIONS_BY_REGEXP_ARGUMENT_DEFAULT);
        }
        
        // set up the action for the browse button
        browseOutputDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(getBaseDirectory());
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                int result = fileChooser.showDialog(CALDocGenerationDialog.this, GemCutter.getResourceString("CALDoc_SelectOutputDirectory"));
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    outputDirectoryField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }});
        
        // set up the ok and cancel buttons' actions
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okSelected = true;
                saveValuesToPreferences();
                CALDocGenerationDialog.this.dispose();
            }});
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okSelected = false;
                CALDocGenerationDialog.this.dispose();
            }});
    }
    
    /**
     * Setup and layout the UI.
     */
    private void setupUI() {
        setTitle(GemCutter.getResourceString("CALDocGenerationDialog"));
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        // the output directory
        JPanel outputDirPanel = new JPanel(new BorderLayout());
        {
            outputDirPanel.add(outputDirectoryField, BorderLayout.CENTER);
            outputDirPanel.add(browseOutputDirectoryButton, BorderLayout.EAST);
        }
        outputDirPanel.setBorder(BorderFactory.createTitledBorder(GemCutter.getResourceString("CALDoc_OutputDirectoryLabel")));
        
        // the scope options
        Box scopeBox = Box.createHorizontalBox();
        {
            scopeBox.add(publicEntitiesOnlyRadioButton);
            scopeBox.add(allEntitiesRadioButton);
            scopeBox.setBorder(BorderFactory.createTitledBorder(GemCutter.getResourceString("CALDoc_ScopeTitle")));
        }
        
        // the filter panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        {
            addGridBagRow(filterPanel, includeOnlyModuleFilterCheckBox, includeOnlyModuleFilterField);
            addGridBagRow(filterPanel, excludeModuleFilterCheckBox, excludeModuleFilterField);
            addGridBagRow(filterPanel, includeOnlyScopedEntityFilterCheckBox, includeOnlyScopedEntityFilterField);
            addGridBagRow(filterPanel, excludeScopedEntityFilterCheckBox, excludeScopedEntityFilterField);
            
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.LINE_START;
            filterPanel.add(excludeTestModulesCheckBox, constraints);
            
            filterPanel.setBorder(BorderFactory.createTitledBorder(GemCutter.getResourceString("CALDoc_FilterTitle")));
        }
        
        // the options panel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1;
            constraints.weighty = 0;
            
            optionsPanel.add(generateMetadataCheckBox, constraints);
            optionsPanel.add(hideOverridenCALDocCheckBox, constraints);
            
            optionsPanel.add(generateAuthorInfoCheckBox, constraints);
            optionsPanel.add(generateVersionInfoCheckBox, constraints);
            optionsPanel.add(displayPreludeNamesAsUnqualifiedCheckBox, constraints);
            optionsPanel.add(generateUsageIndicesCheckBox, constraints);
            optionsPanel.add(separateInstanceDocCheckBox, constraints);
            
            JPanel windowTitlePanel = new JPanel(new GridLayout(0, 1));
            windowTitlePanel.add(windowTitleField);
            windowTitlePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), GemCutter.getResourceString("CALDoc_WindowTitleLabel")));
            
            optionsPanel.add(windowTitlePanel, constraints);
        }
        
        // the main panel - contains: output directory, scope options, filter panel, options panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1;
            constraints.weighty = 0;
            
            mainPanel.add(outputDirPanel, constraints);
            mainPanel.add(scopeBox, constraints);
            mainPanel.add(filterPanel, constraints);
            mainPanel.add(optionsPanel, constraints);
        }
        
        // the panel for the text panes
        JPanel textPanesPanel = new JPanel(new GridLayout(0, 1));
        {
            textPanesPanel.add(titledScrollPaneForTextPane(docTitleField, GemCutter.getResourceString("CALDoc_DocTitleLabel")));
            textPanesPanel.add(titledScrollPaneForTextPane(headerField, GemCutter.getResourceString("CALDoc_HeaderLabel")));
            textPanesPanel.add(titledScrollPaneForTextPane(footerField, GemCutter.getResourceString("CALDoc_FooterLabel")));
            textPanesPanel.add(titledScrollPaneForTextPane(bottomField, GemCutter.getResourceString("CALDoc_BottomLabel")));
        }
        
        // the box for the OK and cancel buttons
        Box okCancelBox = Box.createHorizontalBox();
        okCancelBox.add(Box.createGlue());
        okCancelBox.add(okButton);
        okCancelBox.add(cancelButton);
        
        // the content pane: main panel in north, text panes in center (expands with the dialog), OK/Cancel buttons in south
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.NORTH);
        contentPane.add(textPanesPanel, BorderLayout.CENTER);
        contentPane.add(okCancelBox, BorderLayout.SOUTH);
        
        // set a good size for the dialog
        ((JComponent)contentPane).setMinimumSize(new Dimension(640, 700));
        ((JComponent)contentPane).setPreferredSize(new Dimension(640, 700));
        
        setModal(true);
        pack();
    }

    /**
     * Creates a scroll pane with a titled border for a text pane.
     * @param textPane the text pane.
     * @param title the title.
     * @return the scroll pane.
     */
    private JScrollPane titledScrollPaneForTextPane(JTextPane textPane, String title) {
        JScrollPane scrollPane = new JScrollPane(textPane);
        
        Border oldBorder = scrollPane.getBorder();
        scrollPane.setBorder(
            BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), title), oldBorder));
        
        return scrollPane;
    }

    /**
     * Adds a row of two cells into the filter panel.
     * @param filterPanel the filter panel.
     * @param cell1 the first cell.
     * @param cell2 the second cell.
     */
    private void addGridBagRow(JPanel filterPanel, JCheckBox cell1, JComboBox cell2) {
        GridBagConstraints constraints = new GridBagConstraints();
        
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        filterPanel.add(cell1, constraints);
        
        constraints.gridx = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        filterPanel.add(cell2, constraints);
    }

    /**
     * Links a toggle button with a field so that the toggle activates/deactivates the field.
     * @param toggle the toggle button.
     * @param field the field to be linked to the toggle button.
     */
    private void linkToggleWithField(final JToggleButton toggle, final JComponent field) {
        field.setEnabled(toggle.isSelected());
        
        toggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                field.setEnabled(toggle.isSelected());
            }});
    }
    
    /**
     * @return whether OK was selected at the closing of the dialog.
     */
    boolean isOKSelected() {
        return okSelected;
    }

    /**
     * Constructs a configuration object for the documentation generation based on the values of the dialog fields.
     * @param logger the logger to be included with the configuration object.
     * @param workspaceManager the workspace manager.
     * @return the configuration object.
     */
    HTMLDocumentationGeneratorConfiguration getConfiguration(Logger logger, WorkspaceManager workspaceManager) {
        
        return new HTMLDocumentationGeneratorConfiguration(
            new FileSystemFileGenerator(getBaseDirectory(), logger),
            getModuleFilter(),
            getScopedEntityFilter(),
            shouldGenerateFromMetadata(),
            shouldAlwaysGenerateFromCALDoc(),
            shouldGenerateAuthorInfo(),
            shouldGenerateVersionInfo(),
            shouldDisplayPreludeNamesAsUnqualified(),
            shouldGenerateUsageIndices(),
            shouldSeparateInstanceDoc(),
            getWindowTitle(),
            getDocTitle(),
            getHeader(),
            getFooter(),
            getBottom(),
            GemCutter.getLocaleFromPreferences(),
            logger);
    }

    /**
     * @return the base directory specified by the user.
     */
    private File getBaseDirectory() {
        String directory = outputDirectoryField.getText().trim();
        if (directory.length() > 0) {
            return new File(directory);
        } else {
            return new File(".");
        }
    }

    /**
     * @return the module filter specified by the user.
     */
    private ModuleFilter getModuleFilter() {
        List<ModuleFilter> moduleFilterList = new ArrayList<ModuleFilter>();
        
        // Include the include-only filter if specified
        if (includeOnlyModuleFilterCheckBox.isSelected()) {
            Object includeOnlyModuleFilterFieldValue = includeOnlyModuleFilterField.getEditor().getItem();
            if (includeOnlyModuleFilterFieldValue != null) {
                moduleFilterList.add(new RegExpBasedModuleFilter(includeOnlyModuleFilterFieldValue.toString().trim(), false));
            }
        }
        
        // Include the exclude filter if specified
        if (excludeModuleFilterCheckBox.isSelected()) {
            Object excludeModuleFilterFieldValue = excludeModuleFilterField.getEditor().getItem();
            if (excludeModuleFilterFieldValue != null) {
                moduleFilterList.add(new RegExpBasedModuleFilter(excludeModuleFilterFieldValue.toString().trim(), true));
            }
        }
        
        // Include the exclude test modules filter if specified
        if (excludeTestModulesCheckBox.isSelected()) {
            moduleFilterList.add(new ExcludeTestModulesFilter(workspace));
        }
        
        return CompositeModuleFilter.make(moduleFilterList);
    }

    /**
     * @return the scoped entity filter specified by the user.
     */
    private ScopedEntityFilter getScopedEntityFilter() {
        List<ScopedEntityFilter> scopedEntityFilterList = new ArrayList<ScopedEntityFilter>();
        
        if (shouldGeneratePublicEntriesOnly()) {
            scopedEntityFilterList.add(new PublicEntitiesOnlyFilter());
        }
        
        // Include the include-only filter if specified
        if (includeOnlyScopedEntityFilterCheckBox.isSelected()) {
            Object includeOnlyScopedEntityFilterFieldValue = includeOnlyScopedEntityFilterField.getEditor().getItem();
            if (includeOnlyScopedEntityFilterFieldValue != null) {
                scopedEntityFilterList.add(
                    new QualifiedNameBasedScopedEntityFilter(
                        new RegExpBasedQualifiedNameFilter(includeOnlyScopedEntityFilterFieldValue.toString().trim(), false)));
            }
        }
        
        // Include the exclude filter if specified
        if (excludeScopedEntityFilterCheckBox.isSelected()) {
            Object excludeScopedEntityFilterFieldValue = excludeScopedEntityFilterField.getEditor().getItem();
            if (excludeScopedEntityFilterFieldValue != null) {
                scopedEntityFilterList.add(
                    new QualifiedNameBasedScopedEntityFilter(
                        new RegExpBasedQualifiedNameFilter(excludeScopedEntityFilterFieldValue.toString().trim(), true)));
            }
        }
        
        return CompositeScopedEntityFilter.make(scopedEntityFilterList);
    }
    
    /**
     * @return whether documentation generation should be done for public entries only.
     */
    private boolean shouldGeneratePublicEntriesOnly() {
        return publicEntitiesOnlyRadioButton.isSelected();
    }

    /**
     * @return whether the documentation should include metadata.
     */
    private boolean shouldGenerateFromMetadata() {
        return generateMetadataCheckBox.isSelected();
    }

    /**
     * @return whether CALDoc should always be included in the documentation regardless of whether metadata is included or not.
     */
    private boolean shouldAlwaysGenerateFromCALDoc() {
        return !shouldGenerateFromMetadata() || !hideOverridenCALDocCheckBox.isSelected();
    }

    /**
     * @return whether author info should be generated.
     */
    private boolean shouldGenerateAuthorInfo() {
        return generateAuthorInfoCheckBox.isSelected();
    }

    /**
     * @return whether version info should be generated.
     */
    private boolean shouldGenerateVersionInfo() {
        return generateVersionInfoCheckBox.isSelected();
    }

    /**
     * @return whether Prelude names should always be displayed as unqualified.
     */
    private boolean shouldDisplayPreludeNamesAsUnqualified() {
        return displayPreludeNamesAsUnqualifiedCheckBox.isSelected();
    }
    
    /**
     * @return whether the usage indices should be generated.
     */
    private boolean shouldGenerateUsageIndices() {
        return generateUsageIndicesCheckBox.isSelected();
    }
    
    /**
     * @return whether instance documentation should be separated from the main documentation pages.
     */
    private boolean shouldSeparateInstanceDoc() {
        return separateInstanceDocCheckBox.isSelected();
    }

    /**
     * @return the window title text.
     */
    private String getWindowTitle() {
        return windowTitleField.getText().trim();
    }

    /**
     * @return the documentation title HTML.
     */
    private String getDocTitle() {
        return docTitleField.getText().trim();
    }

    /**
     * @return the header HTML.
     */
    private String getHeader() {
        return headerField.getText().trim();
    }

    /**
     * @return the footer HTML.
     */
    private String getFooter() {
        return footerField.getText().trim();
    }

    /**
     * @return the HTML for the fine print at the bottom of a page.
     */
    private String getBottom() {
        return bottomField.getText().trim();
    }
}
