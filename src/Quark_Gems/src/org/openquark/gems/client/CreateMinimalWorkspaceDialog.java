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
 * CreateMinimalWorkspaceDialog.java
 * Creation date: May 29, 2006.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.WorkspaceDeclarationPathMapper;
import org.openquark.util.ui.ExtensionFileFilter;


/**
 * This class implements the dialog box for creating a minimal workspace.
 *
 * @author Joseph Wong
 */
final class CreateMinimalWorkspaceDialog extends JDialog {
    private static final long serialVersionUID = 764318930713854450L;
    ////
    /// Preference Keys
    //
    private static final String CREATE_MINIMAL_WORKSPACE_OUTPUT_FILE_PREF_KEY = "createMinimalWorkspaceOutputFile";
    private static final String CREATE_MINIMAL_WORKSPACE_SWITCH_AFTER_PREF_KEY = "createMinimalWorkspaceSwitchAfter";
    
    // default values for preferences
    private static final String CREATE_MINIMAL_WORKSPACE_OUTPUT_FILE_DEFAULT = "minimalWorkspace.cws";
    
    /** Label for the output file field. */
    private final JLabel outputFileLabel = new JLabel(GemCutter.getResourceString("CreateMinimalWorkspaceOutputFileDescription"));
    /** Text field for specifying the output file. */
    private final JTextField outputFileField = new JTextField();
    /** Button for launching a directory chooser for the output file. */
    private final JButton browseOutputFileButton = new JButton(GemCutter.getResourceString("CreateMinimalWorkspaceBrowseOutputFile"));
    
    /** Check box for specifying that GemCutter should switch to the new minimal workspace. */
    private final JCheckBox switchAfterCheckBox = new JCheckBox(GemCutter.getResourceString("CreateMinimalWorkspaceSwitchAfterDescription"));
    
    /** The OK button. */
    private final JButton okButton = new JButton(GemCutter.getResourceString("LOC_OK"));
    /** The Cancel button. */
    private final JButton cancelButton = new JButton(GemCutter.getResourceString("LOC_Cancel"));
    
    /** Whether OK was selected at the closing of the dialog. */
    private boolean okSelected = false;
    
    /** The panel containing controls for specifying the modules for the minimal workspace. */
    private final CreateMinimalWorkspacePanel createMinimalWorkspacePanel;
    
    /**
     * Constructs a CreateMinimalWorkspaceDialog
     * @param owner the owner of the dialog.
     * @param workspace the current workspace.
     */
    CreateMinimalWorkspaceDialog(Frame owner, CALWorkspace workspace) {
        super(owner);
        this.createMinimalWorkspacePanel = new CreateMinimalWorkspacePanel(workspace);
        
        initialize();
        loadExistingValuesAndMakeEditable();
        setupUI();
    }
    
    /**
     * Loads the existing values of the fields from the GemCutter preferences and make the fields editable.
     */
    private void loadExistingValuesAndMakeEditable() {
        
        outputFileField.setText(GemCutter.getPreferences().get(CREATE_MINIMAL_WORKSPACE_OUTPUT_FILE_PREF_KEY, CREATE_MINIMAL_WORKSPACE_OUTPUT_FILE_DEFAULT));
        
        boolean switchAfter = GemCutter.getPreferences().getBoolean(CREATE_MINIMAL_WORKSPACE_SWITCH_AFTER_PREF_KEY, false);
        switchAfterCheckBox.setSelected(switchAfter);
        
        // update the fields based on the radio buttons' new values
        updateFieldsBasedOnCurrentState();
    }
    
    /**
     * Saves the values of the dialog fields into GemCutter's preferences.
     */
    private void saveValuesToPreferences() {
        GemCutter.getPreferences().put(CREATE_MINIMAL_WORKSPACE_OUTPUT_FILE_PREF_KEY, getOutputFile().toString());
        GemCutter.getPreferences().putBoolean(CREATE_MINIMAL_WORKSPACE_SWITCH_AFTER_PREF_KEY, shouldSwitchAfter());
    }
    
    /**
     * Initializes the various member fields, e.g. hooking up listeners and actions,
     * but does not setup or layout the UI.
     */
    private void initialize() {
        // set up the action for the browse button
        browseOutputFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File initialDir;
                File absoluteFile = getOutputFile().getAbsoluteFile();
                if (absoluteFile.isDirectory()) {
                    initialDir = absoluteFile;
                } else {
                    initialDir = absoluteFile.getParentFile();
                }
                JFileChooser fileChooser = new JFileChooser(initialDir);
                fileChooser.setDialogTitle(GemCutter.getResourceString("CreateMinimalWorkspaceSelectOutputFile"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                
                // Set up other customizations
                FileFilter filter = new ExtensionFileFilter(WorkspaceDeclarationPathMapper.INSTANCE.getFileExtension(), GemCutter.getResourceString("CWSFilesFilterName"));
              
                fileChooser.setAcceptAllFileFilterUsed(true);
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);            
                
                int result = fileChooser.showDialog(CreateMinimalWorkspaceDialog.this, GemCutter.getResourceString("CreateMinimalWorkspaceFileChooserApproveButtonText"));
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    outputFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }});
        
        // set up the ok and cancel buttons' actions
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (inputProvided()) {
                    okSelected = true;
                    saveValuesToPreferences();
                    CreateMinimalWorkspaceDialog.this.dispose();
                } else {
                    showMissingInputMessage();
                }
            }});
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okSelected = false;
                CreateMinimalWorkspaceDialog.this.dispose();
            }});
    }
    
    /**
     * Setup and layout the UI.
     */
    private void setupUI() {
        setTitle(GemCutter.getResourceString("CreateMinimalWorkspaceDialogTitle"));
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        JComponent contentPane = (JComponent)this.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints2.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1;
        gridBagConstraints2.weighty = 1;
        
        JPanel outputFileFieldPanel = new JPanel(new BorderLayout());
        {
            outputFileLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            outputFileFieldPanel.add(outputFileLabel, BorderLayout.NORTH);
            
            outputFileFieldPanel.add(outputFileField, BorderLayout.CENTER);
            Box browseButtonBox = Box.createHorizontalBox();
            browseButtonBox.add(Box.createHorizontalStrut(12));
            browseButtonBox.add(browseOutputFileButton);
            outputFileFieldPanel.add(browseButtonBox, BorderLayout.EAST);
            outputFileFieldPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 11));
        }
        contentPane.add(outputFileFieldPanel, gridBagConstraints);
        
        JPanel createMinimalWorkspaceWrapperPanel = new JPanel(new BorderLayout());
        {
            createMinimalWorkspaceWrapperPanel.add(createMinimalWorkspacePanel, BorderLayout.CENTER);
            createMinimalWorkspaceWrapperPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        }
        contentPane.add(createMinimalWorkspaceWrapperPanel, gridBagConstraints2);
        
        contentPane.add(new JSeparator(), gridBagConstraints);
        
        JPanel switchAfterCheckBoxWrapperPanel = new JPanel(new BorderLayout());
        {
            switchAfterCheckBoxWrapperPanel.add(switchAfterCheckBox, BorderLayout.CENTER);
            switchAfterCheckBoxWrapperPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        }
        contentPane.add(switchAfterCheckBoxWrapperPanel, gridBagConstraints);
        
        contentPane.add(new JSeparator(), gridBagConstraints);
        
        // the box for the OK and cancel buttons
        contentPane.add(Box.createGlue(), gridBagConstraints);
        
        Box okCancelBox = Box.createHorizontalBox();
        okCancelBox.add(Box.createGlue());
        okCancelBox.add(okButton);
        okCancelBox.add(Box.createHorizontalStrut(5));
        okCancelBox.add(cancelButton);
        okCancelBox.setBorder(BorderFactory.createEmptyBorder(17, 12, 11, 11));
        
        contentPane.add(okCancelBox, gridBagConstraints);
        
        Dimension dimension = new Dimension(420, 535);
        contentPane.setMinimumSize(dimension);
        contentPane.setPreferredSize(dimension);
        
        setModal(true);
        pack();
    }
    
    /**
     * Updates which fields should be enabled/disabled based on the current state of the various fields.
     */
    private void updateFieldsBasedOnCurrentState() {
        // nothing to do
    }
    
    /**
     * @return true if the user has provided appropriate input values.
     */
    private boolean inputProvided() {
        return outputFileField.getText().trim().length() > 0 && hasRootSet();
    }

    /**
     * @return true if the user has specified one or more modules for the root set.
     */
    private boolean hasRootSet() {
        return !createMinimalWorkspacePanel.getDependencyFinder().getRootSet().isEmpty();
    }
    
    /**
     * Displays a message box stating that some input is missing.
     */
    private void showMissingInputMessage() {
        if (hasRootSet()) {
            JOptionPane.showMessageDialog(
                this, GemCutter.getResourceString("CreateMinimalWorkspaceMissingOutputFileMessage"),
                GemCutter.getResourceString("CreateMinimalWorkspaceMissingInputDialogTitle"), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                this, GemCutter.getResourceString("CreateMinimalWorkspaceMissingMinimalRootSetMessage"),
                GemCutter.getResourceString("CreateMinimalWorkspaceMissingInputDialogTitle"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * @return whether OK was selected at the closing of the dialog.
     */
    boolean isOKSelected() {
        return okSelected;
    }
    
    /**
     * @return true if the user has specified that GemCutter should switch to the new minimal workspace.
     */
    boolean shouldSwitchAfter() {
        return switchAfterCheckBox.isSelected();
    }
    
    /**
     * @return the output file.
     */
    File getOutputFile() {
        String file = outputFileField.getText().trim();
        if (file.length() > 0) {
            return new File(file);
        } else {
            return new File(CREATE_MINIMAL_WORKSPACE_OUTPUT_FILE_DEFAULT);
        }
    }
    
    /**
     * @return the content of the minimal workspace declaration.
     */
    String getMinimalWorkspaceDeclaration() {
        return createMinimalWorkspacePanel.getMinimalWorkspaceDeclaration();
    }
}
