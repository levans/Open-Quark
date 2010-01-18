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
 * SwitchWorkspaceDialog.java
 * Creation date: May 18, 2006.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.ResourceStore;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.VaultRegistry;
import org.openquark.cal.services.WorkspaceDeclaration;
import org.openquark.cal.services.WorkspaceDeclarationManager;
import org.openquark.cal.services.WorkspaceDeclarationPathMapper;
import org.openquark.cal.services.WorkspaceManager;
import org.openquark.cal.services.WorkspaceResource;
import org.openquark.cal.services.WorkspaceDeclaration.StreamProvider;
import org.openquark.util.TextEncodingUtilities;
import org.openquark.util.ui.ExtensionFileFilter;


/**
 * This class implements the dialog box for switching workspaces in the GemCutter. One of the functionalities
 * implemented here is the extraction of a list of workspace declaration stream providers that are
 * accessible from the current environment (e.g. the nullary environment) from the workspace manager.
 *
 * @author Joseph Wong
 */
final class SwitchWorkspaceDialog extends JDialog {
    
    private static final long serialVersionUID = 4574495053674302963L;

    /**
     * A workspace declaration stream provider for a minimal workspace.
     *
     * @author Joseph Wong
     */
    private static final class MinimalWorkspaceDeclarationStreamProvider implements WorkspaceDeclaration.StreamProvider {
        
        private final String decl;

        private MinimalWorkspaceDeclarationStreamProvider(String decl) {
            this.decl = decl;
        }

        /** {@inheritDoc} */
        public String getName() {
            return "(minimal)";
        }

        /** {@inheritDoc} */
        public String getLocation() {
            return "temporary";
        }

        /** {@inheritDoc} */
        public String getDebugInfo(VaultRegistry vaultRegistry) {
            return "constructed programmatically";
        }

        /** {@inheritDoc} */
        public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
            return new ByteArrayInputStream(TextEncodingUtilities.getUTF8Bytes(decl));
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * A workspace declaration stream provider that encapsulates a file.
     *
     * @author Joseph Wong
     */
    private static final class FileBasedWorkspaceDeclarationStreamProvider implements WorkspaceDeclaration.StreamProvider {
        
        private final File file;

        private FileBasedWorkspaceDeclarationStreamProvider(File file) {
            this.file = file;
        }

        /** {@inheritDoc} */
        public String getName() {
            return file.getName();
        }

        /** {@inheritDoc} */
        public String getLocation() {
            return file.getAbsolutePath();
        }

        /** {@inheritDoc} */
        public String getDebugInfo(VaultRegistry vaultRegistry) {
            return "from file: " + file.getAbsolutePath();
        }

        /** {@inheritDoc} */
        public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                status.add(new Status(Status.Severity.ERROR, "The specified workspace declaration file cannot be found.", e));
                return null;
            }
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return getLocation();
        }
    }

    /**
     * A workspace declaration stream provider that encapsulates a workspace resource.
     *
     * @author Joseph Wong
     */
    private static final class ResourceBasedWorkspaceDeclarationStreamProvider implements WorkspaceDeclaration.StreamProvider {
        
        private final WorkspaceResource decl;

        private final String location;

        private ResourceBasedWorkspaceDeclarationStreamProvider(WorkspaceResource decl, String location) {
            this.decl = decl;
            this.location = location;
        }

        /** {@inheritDoc} */
        public String getName() {
            return decl.getIdentifier().getFeatureName().getName();
        }

        /** {@inheritDoc} */
        public String getLocation() {
            return location;
        }

        /** {@inheritDoc} */
        public InputStream getInputStream(VaultRegistry vaultRegistry, Status status) {
            return decl.getInputStream(status);
        }

        /** {@inheritDoc} */
        public String getDebugInfo(VaultRegistry vaultRegistry) {
            return decl.getDebugInfo();
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return getName();
        }
    }

    /**
     * Typesafe enumeration for the kind of workspace to switch to.
     *
     * @author Joseph Wong
     */
    private static final class Kind {
        static final Kind KNOWN_DECL = new Kind("knownDecl");
        static final Kind FILE_BASED = new Kind("fileBased");
        static final Kind MINIMAL = new Kind("minimal");
        
        /** The name of the kind, which is used for persistence purposes. */
        private final String name;
        
        /** Private constructor. */
        private Kind(String name) {
            this.name = name;
        }
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
            return name;
        }
        
        /** @return the appropriate constant based on its string representation. */
        static Kind fromString(String name) {
            if (name.equals(KNOWN_DECL.name)) {
                return KNOWN_DECL;
            } else if (name.equals(FILE_BASED.name)) {
                return FILE_BASED;
            } else if (name.equals(MINIMAL.name)) {
                return MINIMAL;
            } else {
                throw new IllegalArgumentException("Unknown switch workspace kind: " + name);
            }
        }
    }
    
    ////
    /// Preference Keys
    //
    private static final String SWITCH_WORKSPACE_KIND_PREF_KEY = "switchWorkspaceKind";
    private static final String SWITCH_WORKSPACE_FILE_BASED_DECLARATION_FILE_NAME_PREF_KEY = "switchWorkspaceFileBasedDeclarationFileName";
    
    /** The button group for the radio buttons for choosing what kind of declaration to use. */
    private final ButtonGroup declarationKindOptionGroup = new ButtonGroup();
    /** Radio button for specifying that a known declaration from the environment should be used. */
    private final JRadioButton knownDeclarationRadioButton = new JRadioButton(GemCutter.getResourceString("SwitchWorkspaceKnownDeclarationDescription"));
    /** Radio button for specifying that a declaration file on the file system should be used. */
    private final JRadioButton fileBasedDeclarationRadioButton = new JRadioButton(GemCutter.getResourceString("SwitchWorkspaceFileBasedDeclarationDescription"));
    /** Radio button for specifying that a minimal workspace declaration should be used. */
    private final JRadioButton minimalDeclarationRadioButton = new JRadioButton(GemCutter.getResourceString("SwitchWorkspaceMinimalDeclarationDescription"));

    /** The combo box for specifying a known declaration from the environment. */
    private JComboBox knownDeclarationComboBox;
    
    /** Text field for specifying the declaration file on the file system. */
    private final JTextField fileBasedDeclarationFileNameField = new JTextField();
    /** Button for launching a directory chooser for the declaration file on the file system. */
    private final JButton browseFileBasedDeclarationFileNameButton = new JButton(GemCutter.getResourceString("SwitchWorkspaceBrowseFileBasedDeclarationFileNameButton"));

    /** The OK button. */
    private final JButton okButton = new JButton(GemCutter.getResourceString("LOC_OK"));
    /** The Cancel button. */
    private final JButton cancelButton = new JButton(GemCutter.getResourceString("LOC_Cancel"));
    
    /** Whether OK was selected at the closing of the dialog. */
    private boolean okSelected = false;

    /** The associated workspace manager. */
    private final WorkspaceManager workspaceManager;
    
    /** The panel containing controls for specifying the modules for the minimal workspace. */
    private final CreateMinimalWorkspacePanel createMinimalWorkspacePanel;

    /**
     * Constructs a SwitchWorkspaceDialog.
     * @param owner the owner of the dialog.
     * @param workspaceManager manager the associated workspace manager.
     */
    SwitchWorkspaceDialog(Frame owner, WorkspaceManager workspaceManager) throws HeadlessException {
        super(owner);
        
        if (workspaceManager == null) {
            throw new NullPointerException();
        }
        this.workspaceManager = workspaceManager;
        createMinimalWorkspacePanel = new CreateMinimalWorkspacePanel(workspaceManager.getWorkspace());
        
        initialize();
        loadExistingValuesAndMakeEditable();
        setupUI();
    }
    
    
    /**
     * Loads the existing values of the fields from the GemCutter preferences and make the fields editable.
     */
    private void loadExistingValuesAndMakeEditable() {
        
        String kindStr = GemCutter.getPreferences().get(SWITCH_WORKSPACE_KIND_PREF_KEY, Kind.KNOWN_DECL.toString());
        Kind kind = Kind.fromString(kindStr);
        if (kind == Kind.KNOWN_DECL) {
            knownDeclarationRadioButton.setSelected(true);
        } else if (kind == Kind.FILE_BASED){
            fileBasedDeclarationRadioButton.setSelected(true);
        } else if (kind == Kind.MINIMAL) {
            minimalDeclarationRadioButton.setSelected(true);
        } else {
            // default to known declaration
            knownDeclarationRadioButton.setSelected(true);
        }
        
        fileBasedDeclarationFileNameField.setText(GemCutter.getPreferences().get(SWITCH_WORKSPACE_FILE_BASED_DECLARATION_FILE_NAME_PREF_KEY, ""));
        
        // update the fields based on the radio buttons' new values
        updateFieldsBasedOnCurrentState();
    }
    
    /**
     * Saves the values of the dialog fields into GemCutter's preferences.
     */
    private void saveValuesToPreferences() {
        GemCutter.getPreferences().put(SWITCH_WORKSPACE_KIND_PREF_KEY, getKind().toString());
        
        if (shouldUseFileBasedDeclaration()) {
            GemCutter.getPreferences().put(SWITCH_WORKSPACE_FILE_BASED_DECLARATION_FILE_NAME_PREF_KEY, getFileBasedDeclarationFileName().toString());
        }
    }
    
    /**
     * Initializes the various member fields, e.g. hooking up listeners and actions,
     * but does not setup or layout the UI.
     */
    private void initialize() {
        WorkspaceDeclaration.StreamProvider[] workspaceDeclarationStreamProvidersFromEnvironment =
            getWorkspaceDeclarationStreamProvidersFromEnvironment();
        
        knownDeclarationComboBox = new JComboBox(workspaceDeclarationStreamProvidersFromEnvironment);
        
        String currentWorkspaceDeclarationName = getCurrentWorkspaceDeclarationName();
        for (final StreamProvider streamProvider : workspaceDeclarationStreamProvidersFromEnvironment) {
            if (streamProvider.getName().equals(currentWorkspaceDeclarationName)) {
                knownDeclarationComboBox.setSelectedItem(streamProvider);
            }
        }
        
        // group together the radio buttons
        declarationKindOptionGroup.add(knownDeclarationRadioButton);
        declarationKindOptionGroup.add(fileBasedDeclarationRadioButton);
        declarationKindOptionGroup.add(minimalDeclarationRadioButton);
        
        // link the radio buttons to the components they enable/disable
        knownDeclarationRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldsBasedOnCurrentState();
            }});
        
        fileBasedDeclarationRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldsBasedOnCurrentState();
            }});
        
        minimalDeclarationRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateFieldsBasedOnCurrentState();
            }});
        
        // set up the action for the browse button
        browseFileBasedDeclarationFileNameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(getFileBasedDeclarationFileName());
                fileChooser.setDialogTitle(GemCutter.getResourceString("SwitchWorkspaceSelectDeclarationFile"));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                
                // Set up other customizations
                FileFilter filter = new ExtensionFileFilter(WorkspaceDeclarationPathMapper.INSTANCE.getFileExtension(), GemCutter.getResourceString("CWSFilesFilterName"));
              
                fileChooser.setAcceptAllFileFilterUsed(true);
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);            
                
                int result = fileChooser.showDialog(SwitchWorkspaceDialog.this, GemCutter.getResourceString("SwitchWorkspaceFileChooserApproveButtonText"));
                
                if (result == JFileChooser.APPROVE_OPTION) {
                    fileBasedDeclarationFileNameField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }});
        
        // set up the ok and cancel buttons' actions
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (inputValid()) {
                    okSelected = true;
                    saveValuesToPreferences();
                    SwitchWorkspaceDialog.this.dispose();
                } else {
                    showInvalidInputMessage();
                }
            }});
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okSelected = false;
                SwitchWorkspaceDialog.this.dispose();
            }});
    }
    
    /**
     * Setup and layout the UI.
     */
    private void setupUI() {
        setTitle(GemCutter.getResourceString("SwitchWorkspaceDialogTitle"));
        
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
        
        // the main section
        {
            JPanel knownDeclarationPanel = new JPanel(new GridBagLayout());
            {
                knownDeclarationRadioButton.setVerticalTextPosition(SwingConstants.TOP);
                
                knownDeclarationPanel.add(knownDeclarationRadioButton, gridBagConstraints);
                
                JPanel comboBoxPanel = new JPanel(new BorderLayout());
                {
                    comboBoxPanel.add(knownDeclarationComboBox, BorderLayout.CENTER);
                    comboBoxPanel.setBorder(BorderFactory.createEmptyBorder(6, 36, 0, 6));
                }
                knownDeclarationPanel.add(comboBoxPanel, gridBagConstraints);

                knownDeclarationPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
            }
            contentPane.add(knownDeclarationPanel, gridBagConstraints);
            
            contentPane.add(new JSeparator(), gridBagConstraints);
            
            JPanel fileBasedDeclarationPanel = new JPanel(new GridBagLayout());
            {
                fileBasedDeclarationRadioButton.setVerticalTextPosition(SwingConstants.TOP);
                
                fileBasedDeclarationPanel.add(fileBasedDeclarationRadioButton, gridBagConstraints);
                
                JPanel fileNameFieldPanel = new JPanel(new BorderLayout());
                {
                    fileNameFieldPanel.add(fileBasedDeclarationFileNameField, BorderLayout.CENTER);
                    Box browseButtonBox = Box.createHorizontalBox();
                    browseButtonBox.add(Box.createHorizontalStrut(12));
                    browseButtonBox.add(browseFileBasedDeclarationFileNameButton);
                    fileNameFieldPanel.add(browseButtonBox, BorderLayout.EAST);
                    fileNameFieldPanel.setBorder(BorderFactory.createEmptyBorder(6, 36, 0, 6));
                }
                fileBasedDeclarationPanel.add(fileNameFieldPanel, gridBagConstraints);
            
                fileBasedDeclarationPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
            }
            contentPane.add(fileBasedDeclarationPanel, gridBagConstraints);

            contentPane.add(new JSeparator(), gridBagConstraints);
            
            JPanel minimalDeclarationPanel = new JPanel(new GridBagLayout());
            {
                minimalDeclarationRadioButton.setVerticalTextPosition(SwingConstants.TOP);
                
                minimalDeclarationPanel.add(minimalDeclarationRadioButton, gridBagConstraints);
                
                JPanel createMinimalWorkspaceWrapperPanel = new JPanel(new BorderLayout());
                {
                    createMinimalWorkspaceWrapperPanel.add(createMinimalWorkspacePanel, BorderLayout.CENTER);
                    createMinimalWorkspaceWrapperPanel.setBorder(BorderFactory.createEmptyBorder(6, 36, 0, 6));
                }
                minimalDeclarationPanel.add(createMinimalWorkspaceWrapperPanel, gridBagConstraints2);        
                
                minimalDeclarationPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
            }
            contentPane.add(minimalDeclarationPanel, gridBagConstraints2);
        }
        
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
        Dimension dimension = new Dimension(480, 575);
        contentPane.setMinimumSize(dimension);
        contentPane.setPreferredSize(dimension);
        
        setModal(true);
        pack();
    }
    
    /**
     * Updates which fields should be enabled/disabled based on the current state of the various fields.
     */
    private void updateFieldsBasedOnCurrentState() {
        boolean useKnownDeclaration = shouldUseKnownDeclaration();
        knownDeclarationComboBox.setEnabled(useKnownDeclaration);
        
        boolean useFileBasedDeclaration = shouldUseFileBasedDeclaration();
        fileBasedDeclarationFileNameField.setEnabled(useFileBasedDeclaration);
        browseFileBasedDeclarationFileNameButton.setEnabled(useFileBasedDeclaration);
        
        boolean useMinimalDeclaration = shouldUseMinimalDeclaration();
        createMinimalWorkspacePanel.setEnabled(useMinimalDeclaration);
    }
    
    /**
     * @return whether OK was selected at the closing of the dialog.
     */
    boolean isOKSelected() {
        return okSelected;
    }
    
    /**
     * @return true if the user has provided appropriate input values.
     */
    private boolean inputValid() {
        if (shouldUseFileBasedDeclaration()) {
            File file = getFileBasedDeclarationFileName();
            return file.toString().length() > 0 && file.isFile();
        }
        
        if (shouldUseMinimalDeclaration()) {
            return !createMinimalWorkspacePanel.getDependencyFinder().getRootSet().isEmpty();
        }
        
        return true;
    }
    
    /**
     * Displays a message box stating that some input is invalid.
     */
    private void showInvalidInputMessage() {
        if (shouldUseFileBasedDeclaration()) {
            File file = getFileBasedDeclarationFileName();
            if (file.toString().length() > 0) {
                JOptionPane.showMessageDialog(
                    this,
                    GemCutter.getResourceString("SwitchWorkspaceInvalidCWSFile"),
                    GemCutter.getResourceString("SwitchWorkspaceInvalidInputMessageDialogTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    GemCutter.getResourceString("SwitchWorkspaceMissingCWSFile"),
                    GemCutter.getResourceString("SwitchWorkspaceMissingInputMessageDialogTitle"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (shouldUseMinimalDeclaration()) {
            JOptionPane.showMessageDialog(
                this,
                GemCutter.getResourceString("SwitchWorkspaceMissingMinimalRootSet"),
                GemCutter.getResourceString("SwitchWorkspaceMissingInputMessageDialogTitle"),
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * @return the kind of declaration specified by the user.
     */
    private Kind getKind() {
        if (shouldUseKnownDeclaration()) {
            return Kind.KNOWN_DECL;
        } else if (shouldUseFileBasedDeclaration()) {
            return Kind.FILE_BASED;
        } else if (shouldUseMinimalDeclaration()) {
            return Kind.MINIMAL;
        } else {
            throw new IllegalStateException();
        }
    }
    
    /**
     * @return true if the user has specified that a known declaration from the environment should be used.
     */
    private boolean shouldUseKnownDeclaration() {
        return knownDeclarationRadioButton.isSelected();
    }
    
    /**
     * @return true if the user has specified that a declaration file on the file system should be used.
     */
    private boolean shouldUseFileBasedDeclaration() {
        return fileBasedDeclarationRadioButton.isSelected();
    }
    
    /**
     * @return true if the user has specified that a minimal workspace declaration should be used.
     */
    private boolean shouldUseMinimalDeclaration() {
        return minimalDeclarationRadioButton.isSelected();
    }
    
    /**
     * @return the declaration file on the file system.
     */
    private File getFileBasedDeclarationFileName() {
        return new File(fileBasedDeclarationFileNameField.getText().trim());
    }
    
    /**
     * @return the name of the workspace declaration used to initialize the current workspace.
     */
    private String getCurrentWorkspaceDeclarationName() {
        return workspaceManager.getInitialWorkspaceDeclarationName();
    }

    /**
     * @return the workspace declaration stream provider for the workspace to switch to.
     */
    WorkspaceDeclaration.StreamProvider getNextWorkspaceDeclarationStreamProvider() {
        
        if (shouldUseKnownDeclaration()) {
            Object selectedItem = knownDeclarationComboBox.getSelectedItem();
            return (WorkspaceDeclaration.StreamProvider)selectedItem;
        }
        
        if (shouldUseFileBasedDeclaration()) {
            return new FileBasedWorkspaceDeclarationStreamProvider(getFileBasedDeclarationFileName());
        }
        
        if (shouldUseMinimalDeclaration()) {
            return new MinimalWorkspaceDeclarationStreamProvider(createMinimalWorkspacePanel.getMinimalWorkspaceDeclaration());

        }
        
        throw new IllegalStateException("Invalid declaration kind.");
    }
    
    /**
     * @return an array of the stream providers for the workspace declarations that are accessible from the current environment
     * (e.g. the nullary environment).
     */
    private WorkspaceDeclaration.StreamProvider[] getWorkspaceDeclarationStreamProvidersFromEnvironment() {
        
        List<WorkspaceDeclaration.StreamProvider> providers = new ArrayList<WorkspaceDeclaration.StreamProvider>();
        
        CALWorkspace workspace = workspaceManager.getWorkspace();
        
        final String workspaceLocation = workspace.getWorkspaceLocationString();
        
        WorkspaceDeclarationManager workspaceDeclarationManager = workspace.getWorkspaceDeclarationManager();
        
        if (workspaceDeclarationManager != null) {
            ResourceStore resourceStore = workspaceDeclarationManager.getResourceStore();
            Iterator<WorkspaceResource> it = resourceStore.getResourceIterator();
            
            while (it.hasNext()) {
                final WorkspaceResource decl = it.next();
                providers.add(new ResourceBasedWorkspaceDeclarationStreamProvider(decl, workspaceLocation));
            }
        }
        
        Collections.sort(providers, new Comparator<Object>() {
            public int compare(final Object a, final Object b) {
                return a.toString().compareTo(b.toString());
            }
        });
        
        return providers.toArray(new WorkspaceDeclaration.StreamProvider[providers.size()]);
    }
}
