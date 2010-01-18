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
 * VaultResourceChooserDialog.java
 * Creation date: Nov 3, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.Vault;
import org.openquark.util.ui.DialogBase;



/**
 * This is a dialog which allows the user to choose a resource and revision number among those available in a given vault.
 * To use, instantiate, then show using doModal(), which should return whether the dialog was accepted.
 * @author Edward Lam
 */
public class VaultResourceChooserDialog extends DialogBase {
    
    private static final long serialVersionUID = -6782946885844512287L;

    /** The vault associated with this dialog. */
    private final Vault vault;

    /** Whether this is a chooser for a module revision.  If false, this chooser is for a workspace declaration revision. */
    private final boolean isModuleChooser;

    private final JButton okButton;
    private final JButton chooseButton;
    private JLabel revisionDescriptionLabel;
    
    /** The name of the currently selected resource in the list.*/
    private String selectedListValue = null;
    
    /** The currently selected revision. */
    private int selectedRevision = -1;
    
    private final Map<String, Integer> resourceNameToLastSelectedRevisionMap = new HashMap<String, Integer>();
    
    /** The action associated with the "Choose" button. */
    private final Action chooseAction = new AbstractAction(GemCutter.getResourceString("VMCD_ChooseActionLabel")) {
        private static final long serialVersionUID = -3707799347371965063L;

        public void actionPerformed(ActionEvent e) {
            handleChooseButtonPress();
        }
    };

    /**
     * A simple class to encapsulate the name and version number of the resource selected by the user..
     * @author Edward Lam
     */
    public static class SelectedResourceVersion {
        private final String resourceName;
        private final int revisionNumber;

        SelectedResourceVersion(String resourceName, int revisionNumber) {
            this.resourceName = resourceName;
            this.revisionNumber = revisionNumber;
        }
        /**
         * @return Returns the resourceName.
         */
        public String getResourceName() {
            return resourceName;
        }
        /**
         * @return Returns the revisionNumber.
         */
        public int getRevisionNumber() {
            return revisionNumber;
        }
    }

    /**
     * Factory method for a module chooser using this class.
     * The selected resource version will contain the module name as the name of the resource.
     * @param owner
     * @param dialogTitle
     * @param chooseActionMessage
     * @param vault
     * @return an instance of this class for choosing modules, or null if the resources for the vault could not be obtained.
     */
    public static VaultResourceChooserDialog getModuleChooserDialog(Frame owner, String dialogTitle, String chooseActionMessage, Vault vault) {
        return getModuleChooserDialog(owner, dialogTitle, chooseActionMessage, vault, null);
    }
    
    /**
     * Factory method for a module chooser using this class.
     * The selected resource version will contain the module name as the name of the resource.
     * @param owner
     * @param dialogTitle
     * @param chooseActionMessage
     * @param vault
     * @param moduleNamesToExclude the names of modules to exclude.  If null, no modules will be excluded.
     * @return an instance of this class for choosing modules, or null if the resources for the vault could not be obtained.
     */
    public static VaultResourceChooserDialog getModuleChooserDialog(Frame owner, String dialogTitle, 
                                                                    String chooseActionMessage, Vault vault, ModuleName[] moduleNamesToExclude) {
        
        Status status = new Status("Vault status.");
        ModuleName[] availableModules = vault.getAvailableModules(status);
        
        if (moduleNamesToExclude != null && availableModules != null) {
            List<ModuleName> availableModulesList = new ArrayList<ModuleName>(Arrays.asList(availableModules));
            availableModulesList.removeAll(Arrays.asList(moduleNamesToExclude));
            availableModules = availableModulesList.toArray(new ModuleName[availableModulesList.size()]);
        }
        
        String[] availableModuleNameStrings = new String[availableModules.length];
        for (int i = 0; i < availableModules.length; i++) {
            availableModuleNameStrings[i] = availableModules[i].toSourceText();
        }

        if (availableModules == null || availableModules.length == 0) {
            
            if (status.getSeverity().compareTo(Status.Severity.WARNING) >= 0) {
                return null;

            } else {
                // There are simply no modules to choose.
                // This is ok..
            }
        }
        
        return new VaultResourceChooserDialog(owner, dialogTitle, chooseActionMessage, vault, availableModuleNameStrings, true);
    }
    
    /**
     * Factory method for a module chooser using this class.
     * The selected resource version will contain the workspace declaration name as the name of the resource.
     * @param owner
     * @param dialogTitle
     * @param chooseActionMessage
     * @param vault
     * @return an instance of this class for choosing modules, or null if the resources for the vault could not be obtained.
     */
    public static VaultResourceChooserDialog getWorkspaceDeclarationChooserDialog(Frame owner, String dialogTitle, 
                                                                                  String chooseActionMessage, Vault vault) {
        
        Status status = new Status("Vault status.");
        String[] availableWorkspaceDeclarations = vault.getAvailableWorkspaceDeclarations(status);

        if (availableWorkspaceDeclarations == null || availableWorkspaceDeclarations.length == 0) {
            
            if (status.getSeverity().compareTo(Status.Severity.WARNING) >= 0) {
                return null;

            } else {
                // There are simply no workspace declarations to choose.
                // This is ok..
            }
        }
        
        return new VaultResourceChooserDialog(owner, dialogTitle, chooseActionMessage, vault, availableWorkspaceDeclarations, false);
        
    }
    
    /**
     * Constructor for a VaultResourceChooserDialog.
     * @param owner
     * @param dialogTitle
     * @param chooseActionMessage
     * @param vault
     * @param availableResources
     * @param isModuleChooser Whether this is a chooser for a module revision.  If false, this chooser is for a workspace declaration revision.
     */
    private VaultResourceChooserDialog(Frame owner, String dialogTitle, String chooseActionMessage, 
                                       Vault vault, String[] availableResources, boolean isModuleChooser) {
        super(owner, dialogTitle);
        this.vault = vault;
        this.isModuleChooser = isModuleChooser;

        if (vault == null) {
            throw new NullPointerException();
        }
        
        // Initialize.
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // main panel
        JPanel topPanel = getTopPanel();
        setContentPane(topPanel);

        // Keep track of the number of rows.
        int numRows = 0;
        
        // Add the message.
        {
            GridBagConstraints constraints = new GridBagConstraints ();
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.anchor = GridBagConstraints.WEST;

            JLabel messageLabel = new JLabel(chooseActionMessage);

            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
            messagePanel.add(messageLabel);
            messagePanel.add(Box.createHorizontalStrut(200));
            messagePanel.add(Box.createHorizontalGlue());

            topPanel.add (messagePanel, constraints); 
            numRows++;
        }
        
        // Add the List box from which to choose the resource.
        {
            GridBagConstraints constraints = new GridBagConstraints ();
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 15, 10, 5);

            final JList resourcesJList = new JList(availableResources);
            JScrollPane resourcesScrollPane = new JScrollPane(resourcesJList);
            resourcesJList.addListSelectionListener(new ListSelectionListener() {

                // When the value changes, update the selected value member.
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        selectedListValue = (String)resourcesJList.getSelectedValue();
                        
                        Integer lastSelectedRevision = resourceNameToLastSelectedRevisionMap.get(selectedListValue);
                        if (lastSelectedRevision != null) {
                            selectedRevision = lastSelectedRevision.intValue();
                        } else {
                            selectedRevision = -1;
                        }

                        updateForInputs();
                    }
                }
            });

            resourcesScrollPane.setMinimumSize(new Dimension(1000, 1));

            topPanel.add(resourcesScrollPane, constraints);
            numRows++;
        }
        
        // Add the Revision area
        {
            {
                // revision label and number.
                GridBagConstraints constraints = new GridBagConstraints ();
                constraints.gridx = 0;
                constraints.gridy = numRows;
                constraints.anchor = GridBagConstraints.WEST;
                
                JPanel revisionArea = new JPanel();
                revisionArea.setLayout(new BoxLayout(revisionArea, BoxLayout.X_AXIS));
                
                JPanel labelAndNumberPanel = new JPanel();
                labelAndNumberPanel.setLayout(new BoxLayout(labelAndNumberPanel, BoxLayout.Y_AXIS));
                
                JPanel revisionLabelPanel = new JPanel();
                revisionLabelPanel.setLayout(new BoxLayout(revisionLabelPanel, BoxLayout.X_AXIS));
                revisionLabelPanel.add(new JLabel(GemCutter.getResourceString("VMCD_RevisionLabel")));
                revisionLabelPanel.add(Box.createHorizontalGlue());
                labelAndNumberPanel.add(revisionLabelPanel);
                
                JPanel revisionNumberPanel = new JPanel();
                revisionNumberPanel.setLayout(new BoxLayout(revisionNumberPanel, BoxLayout.X_AXIS));
                revisionDescriptionLabel = new JLabel();
                
                revisionNumberPanel.add(Box.createHorizontalStrut(15));
                revisionNumberPanel.add(revisionDescriptionLabel);
                revisionNumberPanel.add(Box.createHorizontalGlue());
                labelAndNumberPanel.add(revisionNumberPanel);

                topPanel.add(labelAndNumberPanel, constraints);
            }
            {
                // Choose revision button.
                GridBagConstraints constraints = new GridBagConstraints ();
                constraints.gridx = 1;
                constraints.gridy = numRows;
                constraints.anchor = GridBagConstraints.SOUTHEAST;

                chooseButton = new JButton(chooseAction);
                topPanel.add(chooseButton, constraints);
            }
            numRows++;
        }

        // Add a separator.
        {
            addSeparator(topPanel, null, numRows, new Insets(10, 0, 5, 0));
            numRows++;
        }
        
        // Add the button area.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.SOUTHEAST;

            Box buttonBox = new Box (BoxLayout.X_AXIS);
            buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            
            this.okButton = makeOKButton();
            JButton cancelButton = makeCancelButton();
            
            getRootPane().setDefaultButton(okButton);
            
            buttonBox.add (Box.createHorizontalGlue());
            buttonBox.add (okButton);
            buttonBox.add (Box.createHorizontalStrut(5));
            buttonBox.add (cancelButton);

            getTopPanel().add(buttonBox, constraints);
            
            numRows++;
        }
        
        // Pack, and constrain the minimum size.
        pack();
        addComponentListener(new SizeConstrainer(getSize()));

        // Update dialog for the selected resource.
        updateForInputs();
    }
    
    /**
     * Update the dialog state according to the values entered in the dialog.
     */
    private void updateForInputs() {
        boolean hasSelectedListValue = selectedListValue != null;
        
        okButton.setEnabled(hasSelectedListValue);
        chooseButton.setEnabled(hasSelectedListValue);

        revisionDescriptionLabel.setText(selectedRevision < 0 ? GemCutter.getResourceString("VRCD_Latest") : String.valueOf(selectedRevision));
        revisionDescriptionLabel.setEnabled(hasSelectedListValue);
    }
    
    /**
     * Handle the action where the user presses the "choose" button.
     */
    private void handleChooseButtonPress() {
        if (selectedListValue == null) {
            return;
        }
        
        // Display a dialog to choose the revision.
        VaultRevisionChooser revisionChooserDialog = new VaultRevisionChooser(VaultResourceChooserDialog.this, vault, isModuleChooser);
        Integer newSelectedRevisionInteger = revisionChooserDialog.showDialog(selectedListValue, selectedRevision);
        
        if (newSelectedRevisionInteger == null) {
            return;
        }
        this.selectedRevision = newSelectedRevisionInteger.intValue();
        
        // Update the map from resource name to last selected revision.
        resourceNameToLastSelectedRevisionMap.put(selectedListValue, Integer.valueOf(selectedRevision));
        
        // Update the dialog.
        updateForInputs();
    }

    /**
     * @return the resource version selected by the user, or null if there wasn't any.
     */
    public SelectedResourceVersion getSelectedResourceVersion() {
        if (selectedListValue == null) {
            return null;
        }
        
        return new SelectedResourceVersion(selectedListValue, selectedRevision);
    }
}


