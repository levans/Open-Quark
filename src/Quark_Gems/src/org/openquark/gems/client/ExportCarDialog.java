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
 * ExportCarDialog.java
 * Creation date: Feb 20, 2006.
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
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.openquark.cal.services.CarBuilder;


/**
 * This class implements the dialog box for specifying the options for the CAL Archive (Car) file builder.
 *
 * @author Joseph Wong
 */
final class ExportCarDialog extends JDialog {
    private static final long serialVersionUID = -5187523654821149349L;
    ////
    /// Preference Keys
    //
    private static final String EXPORT_CAR_SINGLE_CAR_PREF_KEY = "exportCarSingleCar";
    private static final String EXPORT_CAR_MULTI_CAR_DIRECTORY_PREF_KEY = "exportCarMultiCarOutputDirectory";
    private static final String EXPORT_CAR_SINGLE_CAR_DIRECTORY_PREF_KEY = "exportCarSingleCarOutputDirectory";
    private static final String EXPORT_CAR_SKIP_MODULES_ALREADY_IN_CARS_PREF_KEY = "exportCarSkipModulesAlreadyInCars";
    private static final String EXPORT_CAR_GENERATE_CORRESP_WORKSPACE_DECL_PREF_KEY = "exportCarGenerateCorrespWorkspaceDecl";
    private static final String EXPORT_CAR_NO_CAR_SUFFIX_IN_CORRESP_WORKSPACE_DECL_NAME_PREF_KEY = "exportCarNoCarSuffixInCorrespWorkspaceDeclName";
    private static final String EXPORT_CAR_BUILD_SOURCELESS_MODULES_PREF_KEY = "exportCarBuildSourcelessModules";
    private static final String EXPORT_CAR_GENERATE_CAR_JAR_SUFFIX_PREF_KEY = "exportCarGenerateCarJarSuffix";
    
    // default values for preferences
    private static final String EXPORT_CAR_MULTI_CAR_DIRECTORY_DEFAULT = ".";
    private static final String EXPORT_CAR_SINGLE_CAR_DIRECTORY_DEFAULT = ".";
    
    /** The button group for the two radio buttons for choosing how Cars should be generated. */
    private final ButtonGroup carBuildingOptionGroup = new ButtonGroup();
    /** Radio button for specifying that a single Car should be built. */
    private final JRadioButton singleCarRadioButton = new JRadioButton(wrapWithHTMLTag(GemCutter.getResourceString("ExportCarSingleCarDescriptionHTML")));
    /** Radio button for specifying that a set of Cars should be built, one file per workspace declaration file. */
    private final JRadioButton oneCarPerWorkspaceDeclRadioButton = new JRadioButton(wrapWithHTMLTag(GemCutter.getResourceString("ExportCarOneCarPerWorkspaceDeclDescriptionHTML")));
    
    /** Check box for specifying that modules that already come from Cars should be skipped over. */
    private final JCheckBox skipModulesAlreadyInCarsCheckBox = new JCheckBox(GemCutter.getResourceString("ExportCarSkipModulesAlreadyInCarsDescription"));
    
    /** Check box for specifying that a new workspace declaration file will be generated for each output Car. */
    private final JCheckBox generateCorrespWorkspaceDeclCheckBox = new JCheckBox(GemCutter.getResourceString("ExportCarGenerateCorrespWorkspaceDeclDescription"));
    
    /** Check box for specifying that sourceless modules should be built. */
    private final JCheckBox buildSourcelessModulesCheckBox = new JCheckBox(wrapWithHTMLTag(GemCutter.getResourceString("ExportCarBuildSourcelessModulesDescriptionHTML")));
    
    /** Check box for specifying that Car-jars should be built. */
    private final JCheckBox generateCarJarSuffixCheckBox = new JCheckBox(wrapWithHTMLTag(GemCutter.getResourceString("ExportCarGenerateCarJarSuffixDescriptionHTML")));
    
    /** The button group for the two radio buttons for choosing how workspace declaration files should be named. */
    private final ButtonGroup cwsSuffixOptionGroup = new ButtonGroup();
    /** Radio button for specifying that a workspace declaration file should end with .car.cws. */
    private final JRadioButton cwsKeepCarSuffixRadioButton = new JRadioButton(); // we add the label in setupUI() because the intended text has a parameter.
    /** Radio button for specifying that a workspace declaration file should end simply with .car. */
    private final JRadioButton cwsNoCarSuffixRadioButton = new JRadioButton(); // we add the label in setupUI() because the intended text has a parameter.
    
    /** Text field for specifying the output directory (for building a single Car). */
    private final JTextField outputSingleCarDirectoryField = new JTextField();
    /** Button for launching a directory chooser for the output directoryy (for building one Car per workspace declaration). */
    private final JButton browseSingleCarOutputDirectoryButton = new JButton(GemCutter.getResourceString("ExportCarBrowseOutputDirectoryButton"));
    /** Text field for specifying the output directory (for building one Car per workspace declaration). */
    private final JTextField outputMultiCarDirectoryField = new JTextField();
    /** Button for launching a directory chooser for the output directory (for building one Car per workspace declaration). */
    private final JButton browseMultiCarOutputDirectoryButton = new JButton(GemCutter.getResourceString("ExportCarBrowseOutputDirectoryButton"));
    
    /** The OK button. */
    private final JButton okButton = new JButton(GemCutter.getResourceString("LOC_OK"));
    /** The Cancel button. */
    private final JButton cancelButton = new JButton(GemCutter.getResourceString("LOC_Cancel"));
    
    /** Whether OK was selected at the closing of the dialog. */
    private boolean okSelected = false;
    
    /** The name of the workspace declaration used to initialize the current workspace. */
    private final String currentWorkspaceDeclName;
    
    /**
     * Constructs an ExportCarDialog.
     * @param owner the owner of the dialog.
     * @param currentWorkspaceDeclName the name of the workspace declaration used to initialize the current workspace.
     */
    ExportCarDialog(Frame owner, String currentWorkspaceDeclName) {
        super(owner);
        
        if (currentWorkspaceDeclName == null) {
            throw new NullPointerException();
        }
        this.currentWorkspaceDeclName = currentWorkspaceDeclName;
        
        initialize();
        loadExistingValuesAndMakeEditable();
        setupUI();
    }
    
    /**
     * Loads the existing values of the fields from the GemCutter preferences and make the fields editable.
     */
    private void loadExistingValuesAndMakeEditable() {
        
        boolean singleCar = GemCutter.getPreferences().getBoolean(EXPORT_CAR_SINGLE_CAR_PREF_KEY, true);
        if (singleCar) {
            singleCarRadioButton.setSelected(true);
        } else {
            oneCarPerWorkspaceDeclRadioButton.setSelected(true);
        }
        
        outputSingleCarDirectoryField.setText(GemCutter.getPreferences().get(EXPORT_CAR_SINGLE_CAR_DIRECTORY_PREF_KEY, EXPORT_CAR_SINGLE_CAR_DIRECTORY_DEFAULT));
        outputMultiCarDirectoryField.setText(GemCutter.getPreferences().get(EXPORT_CAR_MULTI_CAR_DIRECTORY_PREF_KEY, EXPORT_CAR_MULTI_CAR_DIRECTORY_DEFAULT));
        
        boolean skipModulesAlreadyInCars = GemCutter.getPreferences().getBoolean(EXPORT_CAR_SKIP_MODULES_ALREADY_IN_CARS_PREF_KEY, false);
        skipModulesAlreadyInCarsCheckBox.setSelected(skipModulesAlreadyInCars);
         
        boolean generateCorrespWorkspaceDecl = GemCutter.getPreferences().getBoolean(EXPORT_CAR_GENERATE_CORRESP_WORKSPACE_DECL_PREF_KEY, true);
        generateCorrespWorkspaceDeclCheckBox.setSelected(generateCorrespWorkspaceDecl);
        
        boolean buildSourcelessModules = GemCutter.getPreferences().getBoolean(EXPORT_CAR_BUILD_SOURCELESS_MODULES_PREF_KEY, true);
        buildSourcelessModulesCheckBox.setSelected(buildSourcelessModules);
        
        boolean generateCarJarSuffix = GemCutter.getPreferences().getBoolean(EXPORT_CAR_GENERATE_CAR_JAR_SUFFIX_PREF_KEY, false);
        generateCarJarSuffixCheckBox.setSelected(generateCarJarSuffix);
        
        boolean noCarSuffixInWorkspaceDeclName = GemCutter.getPreferences().getBoolean(EXPORT_CAR_NO_CAR_SUFFIX_IN_CORRESP_WORKSPACE_DECL_NAME_PREF_KEY, false);
        if (noCarSuffixInWorkspaceDeclName) {
            cwsNoCarSuffixRadioButton.setSelected(true);
        } else {
            cwsKeepCarSuffixRadioButton.setSelected(true);
        }
        
        // update the fields based on the radio buttons' new values
        updateFieldsBasedOnCurrentState();
    }
    
    /**
     * Saves the values of the dialog fields into GemCutter's preferences.
     */
    private void saveValuesToPreferences() {
        GemCutter.getPreferences().putBoolean(EXPORT_CAR_SINGLE_CAR_PREF_KEY, shouldBuildSingleCar());
        
        if (shouldBuildSingleCar()) {
            GemCutter.getPreferences().put(EXPORT_CAR_SINGLE_CAR_DIRECTORY_PREF_KEY, getSingleCarOutputDirectory().toString());
        }
        
        if (shouldBuildOneCarPerWorkspaceDecl()) {
            GemCutter.getPreferences().put(EXPORT_CAR_MULTI_CAR_DIRECTORY_PREF_KEY, getOneCarPerWorkspaceDeclOutputDirectory().toString());
        }
        
        GemCutter.getPreferences().putBoolean(EXPORT_CAR_SKIP_MODULES_ALREADY_IN_CARS_PREF_KEY, shouldSkipModulesAlreadyInCars());
        GemCutter.getPreferences().putBoolean(EXPORT_CAR_GENERATE_CORRESP_WORKSPACE_DECL_PREF_KEY, shouldGenerateCorrespWorkspaceDecl());
        GemCutter.getPreferences().putBoolean(EXPORT_CAR_BUILD_SOURCELESS_MODULES_PREF_KEY, shouldBuildSourcelessModules());
        GemCutter.getPreferences().putBoolean(EXPORT_CAR_GENERATE_CAR_JAR_SUFFIX_PREF_KEY, shouldGenerateCarJarSuffix());
        
        GemCutter.getPreferences().putBoolean(EXPORT_CAR_NO_CAR_SUFFIX_IN_CORRESP_WORKSPACE_DECL_NAME_PREF_KEY, shouldOmitCarSuffixInWorkspaceDeclName());
    }
    
    /**
     * Initializes the various member fields, e.g. hooking up listeners and actions,
     * but does not setup or layout the UI.
     */
    private void initialize() {
        // group together the two car building option radio buttons
        carBuildingOptionGroup.add(singleCarRadioButton);
        carBuildingOptionGroup.add(oneCarPerWorkspaceDeclRadioButton);
        
        // link the radio buttons to the components they enable/disable
        singleCarRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldsBasedOnCurrentState();
            }});
        
        oneCarPerWorkspaceDeclRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldsBasedOnCurrentState();
            }});
        
        // group together the two cws naming option radio buttons
        cwsSuffixOptionGroup.add(cwsKeepCarSuffixRadioButton);
        cwsSuffixOptionGroup.add(cwsNoCarSuffixRadioButton);
        
        // link the check box for workspace declaration generation to the components it enable/disable
        generateCorrespWorkspaceDeclCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldsBasedOnCurrentState();
            }});
        
        // set up the action for the browse button
        browseSingleCarOutputDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(getOneCarPerWorkspaceDeclOutputDirectory());
                fileChooser.setDialogTitle(GemCutter.getResourceString("ExportCarSelectOutputDirectory"));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                // Don't use the accept all filter.
                fileChooser.setAcceptAllFileFilterUsed(false);
                
                int result = fileChooser.showDialog(ExportCarDialog.this, GemCutter.getResourceString("ExportCarFileChooserApproveButtonText"));
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    outputSingleCarDirectoryField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }});
        
        // set up the action for the browse button
        browseMultiCarOutputDirectoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(getOneCarPerWorkspaceDeclOutputDirectory());
                fileChooser.setDialogTitle(GemCutter.getResourceString("ExportCarSelectOutputDirectory"));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                // Don't use the accept all filter.
                fileChooser.setAcceptAllFileFilterUsed(false);
                
                int result = fileChooser.showDialog(ExportCarDialog.this, GemCutter.getResourceString("ExportCarFileChooserApproveButtonText"));
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    outputMultiCarDirectoryField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }});
        
        // set up the ok and cancel buttons' actions
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (inputProvided()) {
                    okSelected = true;
                    saveValuesToPreferences();
                    ExportCarDialog.this.dispose();
                } else {
                    showMissingInputMessage();
                }
            }});
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okSelected = false;
                ExportCarDialog.this.dispose();
            }});
    }
    
    /**
     * Setup and layout the UI.
     */
    private void setupUI() {
        setTitle(GemCutter.getResourceString("ExportCarDialogTitle"));
        
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
        
        // the file creation mode panel
        JPanel modePanel = new JPanel(new GridBagLayout());
        {
            singleCarRadioButton.setVerticalTextPosition(SwingConstants.TOP);
            oneCarPerWorkspaceDeclRadioButton.setVerticalTextPosition(SwingConstants.TOP);
            
            modePanel.add(singleCarRadioButton, gridBagConstraints);
            modePanel.add(makeFileNamePanel(outputSingleCarDirectoryField, browseSingleCarOutputDirectoryButton, GemCutter.getResourceString("ExportCarSelectOutputDirectoryPrompt")), gridBagConstraints);
            modePanel.add(Box.createVerticalStrut(11), gridBagConstraints);
            modePanel.add(oneCarPerWorkspaceDeclRadioButton, gridBagConstraints);
            modePanel.add(makeFileNamePanel(outputMultiCarDirectoryField, browseMultiCarOutputDirectoryButton, GemCutter.getResourceString("ExportCarSelectOutputDirectoryPrompt")), gridBagConstraints);
            
            modePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(12, 12, 0, 11),
                    BorderFactory.createTitledBorder(GemCutter.getResourceString("ExportCarCreationMode"))),
                BorderFactory.createEmptyBorder(12, 12, 11, 11)));
        }
        contentPane.add(modePanel, gridBagConstraints);
        
        // the other options panel
        JPanel otherOptionsPanel = new JPanel();
        otherOptionsPanel.setLayout(new BoxLayout(otherOptionsPanel, BoxLayout.Y_AXIS));
        
        otherOptionsPanel.add(generateCorrespWorkspaceDeclCheckBox);
        
        JPanel cwsSuffixOptionsPanel = new JPanel();
        {
            cwsSuffixOptionsPanel.setLayout(new BoxLayout(cwsSuffixOptionsPanel, BoxLayout.Y_AXIS));
            
            String cwsNameWithCarSuffix =
                CarBuilder.makeOutputCorrespWorkspaceDeclName(
                    CarBuilder.makeCarNameFromSourceWorkspaceDeclName(currentWorkspaceDeclName), false);
            String cwsKeepCarSuffixLabel = GemCutter.getResourceString("ExportCarCWSKeepCarSuffixDescriptionHTML", cwsNameWithCarSuffix);
            cwsKeepCarSuffixRadioButton.setText(wrapWithHTMLTag(cwsKeepCarSuffixLabel));
            cwsKeepCarSuffixRadioButton.setVerticalTextPosition(SwingConstants.TOP);
            
            String cwsNameWithNoCarSuffix =
                CarBuilder.makeOutputCorrespWorkspaceDeclName(
                    CarBuilder.makeCarNameFromSourceWorkspaceDeclName(currentWorkspaceDeclName), true);
            String cwsNoCarSuffixLabel = GemCutter.getResourceString("ExportCarCWSNoCarSuffixDescriptionHTML", cwsNameWithNoCarSuffix);
            cwsNoCarSuffixRadioButton.setText(wrapWithHTMLTag(cwsNoCarSuffixLabel));
            cwsNoCarSuffixRadioButton.setVerticalTextPosition(SwingConstants.TOP);

            cwsSuffixOptionsPanel.add(cwsKeepCarSuffixRadioButton);
            cwsSuffixOptionsPanel.add(Box.createVerticalStrut(6));
            cwsSuffixOptionsPanel.add(cwsNoCarSuffixRadioButton);
            
            cwsSuffixOptionsPanel.setBorder(BorderFactory.createEmptyBorder(6, 24, 5, 0));
        }
        
        otherOptionsPanel.add(cwsSuffixOptionsPanel);
        
        otherOptionsPanel.add(skipModulesAlreadyInCarsCheckBox);
        
        otherOptionsPanel.add(buildSourcelessModulesCheckBox);
        
        otherOptionsPanel.add(generateCarJarSuffixCheckBox);
        
        otherOptionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(12, 12, 0, 11),
                    BorderFactory.createTitledBorder(GemCutter.getResourceString("ExportCarOtherOptions"))),
            BorderFactory.createEmptyBorder(12, 12, 11, 11)));
            
        contentPane.add(otherOptionsPanel, gridBagConstraints);
        
        // the box for the OK and cancel buttons
        {
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.gridx = 0;
            gridBagConstraints2.gridwidth = GridBagConstraints.REMAINDER;
            gridBagConstraints2.anchor = GridBagConstraints.LINE_START;
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            gridBagConstraints2.weightx = 1;
            gridBagConstraints2.weighty = 1;
            
            contentPane.add(Box.createGlue(), gridBagConstraints2);
        }
        
        Box okCancelBox = Box.createHorizontalBox();
        okCancelBox.add(Box.createGlue());
        okCancelBox.add(okButton);
        okCancelBox.add(Box.createHorizontalStrut(5));
        okCancelBox.add(cancelButton);
        okCancelBox.setBorder(BorderFactory.createEmptyBorder(17, 12, 11, 11));
        
        contentPane.add(okCancelBox, gridBagConstraints);
        
        Dimension dimension = new Dimension(640, 550);
        contentPane.setMinimumSize(dimension);
        contentPane.setPreferredSize(dimension);
        
        setModal(true);
        pack();
    }
    
    /**
     * Wraps the specified text with a pair of &lt;html&gt; tags.
     * @param text the text to be wrapped.
     * @return the text wrapped with a pair of &lt;html&gt; tags.
     */
    private static String wrapWithHTMLTag(String text) {
        return "<html>" + text + "</html>";
    }
    
    /**
     * Creates a panel containing a text field and a browse button for obtaining a file/directory name.
     * @param nameField the text field for the file/directory name.
     * @param browseButton the browse button.
     * @param caption the caption for the panel.
     * @return a new panel with the provided contents.
     */
    private JPanel makeFileNamePanel(JTextField nameField, JButton browseButton, String caption) {
        JPanel panel = new JPanel(new BorderLayout());
        
        panel.add(nameField, BorderLayout.CENTER);
        Box browseButtonBox = Box.createHorizontalBox();
        browseButtonBox.add(Box.createHorizontalStrut(12));
        browseButtonBox.add(browseButton);
        panel.add(browseButtonBox, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(6, 36, 0, 6), caption));
        
        return panel;
    }
    
    /**
     * Updates which fields should be enabled/disabled based on the current state of the various fields.
     */
    private void updateFieldsBasedOnCurrentState() {
        boolean isSingleCar = shouldBuildSingleCar();
        outputSingleCarDirectoryField.setEnabled(isSingleCar);
        browseSingleCarOutputDirectoryButton.setEnabled(isSingleCar);

        boolean isOneCarPerWorkspaceDecl = shouldBuildOneCarPerWorkspaceDecl();
        outputMultiCarDirectoryField.setEnabled(isOneCarPerWorkspaceDecl);
        browseMultiCarOutputDirectoryButton.setEnabled(isOneCarPerWorkspaceDecl);
        
        boolean generateCorrespWorkspaceDecl = shouldGenerateCorrespWorkspaceDecl();
        cwsKeepCarSuffixRadioButton.setEnabled(generateCorrespWorkspaceDecl);
        cwsNoCarSuffixRadioButton.setEnabled(generateCorrespWorkspaceDecl);
    }
    
    /**
     * @return true if the user has provided appropriate input values.
     */
    private boolean inputProvided() {
        if (shouldBuildSingleCar()) {
            return outputSingleCarDirectoryField.getText().trim().length() > 0;
        } else if (shouldBuildOneCarPerWorkspaceDecl()) {
            return outputMultiCarDirectoryField.getText().trim().length() > 0;
        }
        
        return true;
    }
    
    /**
     * Displays a message box stating that some input is missing.
     */
    private void showMissingInputMessage() {
        String title = GemCutter.getResourceString("ExportCarMissingInputMessageDialogTitle");
        if (shouldBuildSingleCar()) {
            JOptionPane.showMessageDialog(
                this, GemCutter.getResourceString("ExportCarMissingOutputCarFile"), title, JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                this, GemCutter.getResourceString("ExportCarMissingOutputDirectory"), title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * @return true if the user has specified that one Car should be built for each workspace declaration file.
     */
    boolean shouldBuildOneCarPerWorkspaceDecl() {
        return oneCarPerWorkspaceDeclRadioButton.isSelected();
    }

    /**
     * @return true if the user has specified that a single Car should be built.
     */
    boolean shouldBuildSingleCar() {
        return singleCarRadioButton.isSelected();
    }
    
    /**
     * @return true if the user has specified that modules that already come from Cars should be skipped over.
     */
    boolean shouldSkipModulesAlreadyInCars() {
        return skipModulesAlreadyInCarsCheckBox.isSelected();
    }
    
    /**
     * @return true if the user has specified that a new workspace declaration file will be generated for each output Car.
     */
    boolean shouldGenerateCorrespWorkspaceDecl() {
        return generateCorrespWorkspaceDeclCheckBox.isSelected();
    }
    
    /**
     * @return true if the user has specified that a workspace declaration file should end simply with .car.
     */
    boolean shouldOmitCarSuffixInWorkspaceDeclName() {
        return cwsNoCarSuffixRadioButton.isSelected();
    }
    
    /**
     * @return true if the user has specified that sourceless modules should be built.
     */
    boolean shouldBuildSourcelessModules() {
        return buildSourcelessModulesCheckBox.isSelected();
    }
    
    /**
     * @return true if the user has specified that Car-jars should be built.
     */
    boolean shouldGenerateCarJarSuffix() {
        return generateCarJarSuffixCheckBox.isSelected();
    }
    
    /**
     * @return whether OK was selected at the closing of the dialog.
     */
    boolean isOKSelected() {
        return okSelected;
    }
    
    /**
     * @return the output directory (for building one Car per workspace declaration).
     */
    File getOneCarPerWorkspaceDeclOutputDirectory() {
        String directory = outputMultiCarDirectoryField.getText().trim();
        if (directory.length() > 0) {
            return new File(directory);
        } else {
            return new File(".");
        }
    }
    
    /**
     * @return the output directory (for building a single Car).
     */
    File getSingleCarOutputDirectory() {
        String directory = outputSingleCarDirectoryField.getText().trim();
        if (directory.length() > 0) {
            return new File(directory);
        } else {
            return new File(".");
        }
    }
}
