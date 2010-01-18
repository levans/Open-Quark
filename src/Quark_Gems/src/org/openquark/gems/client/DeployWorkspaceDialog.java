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
 * DeployWorkspaceDialog.java
 * Creation date: Dec 6, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.util.ui.DialogBase;



/**
 * This is a dialog which allows the user to select a number of modules in the workspace as those to deploy to a vault as 
 * part of a deployed workspace declaration.
 * To use, instantiate, then show using doModal(), which should return whether the dialog was accepted.
 * @author Edward Lam
 */
public class DeployWorkspaceDialog extends DialogBase {
    private static final long serialVersionUID = 3835794264425138925L;

    /** The names to display on the column headers. */
    private static final String[] columnNames = {
            GemCutter.getResourceString("DWD_DeployColumnTitle"),
            GemCutter.getResourceString("DWD_ModuleColumnTitle"),
            GemCutter.getResourceString("DWD_RevisionColumnTitle")
    };
    
    /** The tooltips for the column headers. */
    private static final String[] columnToolTips = {
            GemCutter.getResourceString("DWD_DeployColumnTitleToolTip"),
            GemCutter.getResourceString("DWD_ModuleColumnTitleToolTip"),
            GemCutter.getResourceString("DWD_RevisionColumnTitleToolTip")
    };
    
    /** The cell renderer to render numbers. */
    private static final NumberCellRenderer numberCellRenderer = new NumberCellRenderer();
    
    /** The size of the padding on the right side of a number cell. */
    private static final int NUMBER_CELL_PADDING = 8;
    
    /** The Preference key for the workspace name. */
    private static final String WORKSPACE_DECLARATION_NAME_KEY = "workspaceDeclarationName";

    /** The workspace names which currently exist and may be selected by the Choose... button. */
    private final String[] existingWorkspaceNames;
    
    private final JButton okButton;
    private final JButton chooseButton;
    private final JTextField workspaceNameEntryField;

    /** The table model for the module/revision selection. */
    private final ModuleRevisionTableModel tableModel;


    /** The action associated with the "Choose" button. */
    private final Action chooseAction = new AbstractAction(GemCutter.getResourceString("DWD_ChooseActionLabel")) {
        private static final long serialVersionUID = -1610473389806842260L;

        public void actionPerformed(ActionEvent e) {
            handleChooseButtonPress();
        }
    };

    /**
     * The cell renderer class to use to render numbers.
     * This is overridden to create some padding on the right side of the cell.
     * @author Edward Lam
     */
    private static class NumberCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1514162208237566662L;


        /**
         * Constructor for a DeployWorkspaceDialog.NumberCellRenderer.
         */
        public NumberCellRenderer() {
            super();
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        

        /**
         * {@inheritDoc}
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Get the regular component.
            JLabel c = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Add some padding on the right side.
            Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, NUMBER_CELL_PADDING);
            c.setBorder(BorderFactory.createCompoundBorder(c.getBorder(), emptyBorder));
            
            return c;
        }
    }
    
    
    /**
     * The table model for the table which displays the revisions of the modules to deploy.
     * @author Edward Lam
     */
    private static class ModuleRevisionTableModel extends AbstractTableModel {
        
        private static final long serialVersionUID = 8347197663314785760L;

        /** Map (sorted by module name) from module name to whether it is currently selected for deployment. */
        private final Map<ModuleName, Boolean> moduleNameToDeployMap = new TreeMap<ModuleName, Boolean>();
        
        /** The module revisions, sorted by module name. */
        private final ModuleRevisionInfo[] moduleRevisions;

        /**
         * Constructor for a ModuleRevisionTableModel.
         * @param moduleRevisions the modules and revisions from which to construct the table model.
         */
        ModuleRevisionTableModel(ModuleRevisionInfo[] moduleRevisions) {
            // Sort by module name.
            this.moduleRevisions = moduleRevisions.clone();
            Arrays.sort(this.moduleRevisions, new Comparator<ModuleRevisionInfo>() {
                public int compare(ModuleRevisionInfo o1, ModuleRevisionInfo o2) {
                    return o1.getModuleName().compareTo(o2.getModuleName());
                }
            });
            
            // Populate the module name to deploy map, for now saying we will deploy all modules.
            for (final ModuleRevisionInfo moduleRevisionInfo : moduleRevisions) {
                ModuleName moduleName = moduleRevisionInfo.getModuleName();
                moduleNameToDeployMap.put(moduleName, Boolean.TRUE);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        /**
         * {@inheritDoc}
         */
        public int getRowCount() {
            return moduleRevisions.length;
        }

        /**
         * {@inheritDoc}
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * {@inheritDoc}
         */
        public Object getValueAt(int row, int col) {
            // Columns:
            // 0: Deploy check box.
            // 1: Module name.
            // 2: Revision number.
            ModuleRevisionInfo moduleRevision = moduleRevisions[row];
            
            switch (col) {
                case 0:
                    return moduleNameToDeployMap.get(moduleRevision.getModuleName());
                    
                case 1:
                    return moduleRevision.isNewRevision() ? "*" + moduleRevision.getModuleName().toSourceText() : moduleRevision.getModuleName().toSourceText();
                    
                case 2:
                    return Integer.valueOf(moduleRevision.getRevisionNumber());
                    
                default:
                    throw new IllegalArgumentException("Column " + col + " is out of range.");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            // Only the check box is editable.
            return col == 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            if (!isCellEditable(row, col)) {
                throw new IllegalArgumentException();
            }
            moduleNameToDeployMap.put(moduleRevisions[row].getModuleName(), (Boolean)value);
            fireTableCellUpdated(row, col);
        }
        
        /**
         * @return the modules currently selected for deployment.
         */
        ModuleName[] getModulesToDeploy() {
            List<ModuleName> modulesToDeployList = new ArrayList<ModuleName>();
            for (final ModuleName moduleName : moduleNameToDeployMap.keySet()) {
                if (Boolean.TRUE.equals(moduleNameToDeployMap.get(moduleName))) {
                    modulesToDeployList.add(moduleName);
                }
            }
            ModuleName[] result = new ModuleName[modulesToDeployList.size()];
            return modulesToDeployList.toArray(result);
        }
    }
    
    /**
     * A simple container class to hold a module name and a revision number.
     * @author Edward Lam
     */
    public static class ModuleRevisionInfo {
        // TODOEL: This overlaps somewhat with the ModuleRevision class.
        
        private final ModuleName moduleName;
        private final int revisionNumber;
        private final boolean isNewRevision;

        ModuleRevisionInfo(ModuleName moduleName, int revisionNumber, boolean isNewRevision) {
            this.moduleName = moduleName;
            this.revisionNumber = revisionNumber;
            this.isNewRevision = isNewRevision;
        }
        public ModuleName getModuleName() {
            return moduleName;
        }
        public int getRevisionNumber() {
            return revisionNumber;
        }
        public boolean isNewRevision() {
            return isNewRevision;
        }
    }
    
    /**
     * A simple container class to hold the result of displaying this dialog.
     * @author Edward Lam
     */
    public static class DeployDialogResult {
        private final ModuleName[] modulesToDeploy;
        private final String workspaceName;

        DeployDialogResult(ModuleName[] modulesToDeploy, String workspaceName) {
            this.modulesToDeploy = modulesToDeploy;
            this.workspaceName = workspaceName;
        }
        public ModuleName[] getModulesToDeploy() {
            return modulesToDeploy.clone();
        }
        public String getWorkspaceName() {
            return workspaceName;
        }
    }
    
    
    /**
     * Constructor for a DeployWorkspaceDialog.
     * @param owner the owner for this dialog
     * @param moduleRevisions the module revisions for which this dialog should prompt.
     * @param existingWorkspaceNames workspace names already available in the vault.  Can be null.
     */
    public DeployWorkspaceDialog(Frame owner, ModuleRevisionInfo[] moduleRevisions, String[] existingWorkspaceNames) {

        super(owner, GemCutter.getResourceString("DWD_DialogTitle"));
        this.existingWorkspaceNames = existingWorkspaceNames == null ? null : (String[])existingWorkspaceNames.clone();

        // Initialize.
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // main panel
        JPanel topPanel = getTopPanel();
        setContentPane(topPanel);

        // Keep track of the number of rows.
        int numRows = 0;
        
        // Add the label for the table.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.anchor = GridBagConstraints.WEST;

            JLabel messageLabel = new JLabel(GemCutter.getResourceString("DWD_ModulesToDeployLabel"));

            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
            messagePanel.add(messageLabel);
            messagePanel.add(Box.createHorizontalStrut(200));
            messagePanel.add(Box.createHorizontalGlue());

            topPanel.add(messagePanel, constraints); 
            numRows++;
        }
        
        // Add the table from which to choose the module.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(5, 15, 5, 5);
            
            this.tableModel = new ModuleRevisionTableModel(moduleRevisions);

            final JTable modulesJTable = getJTable(tableModel);
            modulesJTable.setPreferredScrollableViewportSize(new Dimension(300, 200));

            setMinSizeColumnHeader(modulesJTable, 0);
            setMinSizeColumnHeader(modulesJTable, 2);
            
            JScrollPane modulesScrollPane = new JScrollPane(modulesJTable);
            modulesScrollPane.getViewport().setBackground(modulesJTable.getBackground());

            topPanel.add(modulesScrollPane, constraints);
            numRows++;
        }
        
        // Add a label explaining the asterisk.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = numRows;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = new Insets(0, 15, 10, 0);
            
            JLabel messageLabel = new JLabel(GemCutter.getResourceString("DWD_NewRevisionMarkerExplanationLabel"));
            
            JPanel messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
            messagePanel.add(messageLabel);
            messagePanel.add(Box.createHorizontalStrut(200));
            messagePanel.add(Box.createHorizontalGlue());
            
            topPanel.add(messagePanel, constraints); 
            numRows++;
        }
        
        // Add a separator.
        {
            addSeparator(topPanel, null, numRows, new Insets(10, 0, 5, 0));
            numRows++;
        }
        
        // Add the workspace name entry area
        {
            // Add the label.
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = numRows;
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 0, 5, 0);
                
                JLabel messageLabel = new JLabel(GemCutter.getResourceString("DWD_WorkspaceNameLabel"));
                
                JPanel messagePanel = new JPanel();
                messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
                messagePanel.add(messageLabel);
                messagePanel.add(Box.createHorizontalStrut(200));
                messagePanel.add(Box.createHorizontalGlue());
                
                topPanel.add(messagePanel, constraints); 
                numRows++;
            }
            
            // Add the entry area
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 0;
                constraints.gridy = numRows;
                constraints.weightx = 0.1;
                constraints.anchor = GridBagConstraints.SOUTHWEST;
                constraints.fill = GridBagConstraints.BOTH;
                constraints.insets = new Insets(0, 15, 0, 5);
                
                String suggestedWorkspaceName = getPreferences().get(WORKSPACE_DECLARATION_NAME_KEY, getDefaultWorkspaceName());
                
                workspaceNameEntryField = new JTextField(suggestedWorkspaceName);
                workspaceNameEntryField.getDocument().addDocumentListener(new DocumentListener() {

                    public void changedUpdate(DocumentEvent e) {
                        updateButtonState();
                    }

                    public void insertUpdate(DocumentEvent e) {
                        updateButtonState();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        updateButtonState();
                    }
                });
                
                topPanel.add(workspaceNameEntryField, constraints);
            }
            
            // Add the choose button.
            {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = 1;
                constraints.gridy = numRows;
                constraints.anchor = GridBagConstraints.SOUTHEAST;

                chooseButton = new JButton(chooseAction);
                chooseButton.setEnabled(existingWorkspaceNames != null && existingWorkspaceNames.length > 0);
                
                topPanel.add(chooseButton, constraints);
            }
            numRows++;
        }
        
        // Add a separator.
        {
            addSeparator(topPanel, null, numRows, new Insets(15, 0, 5, 0));
            numRows++;
        }
        
        // Add the button area.
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridy = numRows;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.SOUTHEAST;

            Box buttonBox = new Box(BoxLayout.X_AXIS);
            buttonBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            
            this.okButton = makeOKButton();
            JButton cancelButton = makeCancelButton();
            
            getRootPane().setDefaultButton(okButton);
            
            buttonBox.add(Box.createHorizontalGlue());
            buttonBox.add(okButton);
            buttonBox.add(Box.createHorizontalStrut(5));
            buttonBox.add(cancelButton);

            getTopPanel().add(buttonBox, constraints);
            
            numRows++;
        }
        
        // Pack, and constrain the minimum size.
        pack();
        addComponentListener(new SizeConstrainer(getSize()));
        
        // Override the focus traversal policy to set the default component.
        final FocusTraversalPolicy defaultPolicy = getFocusTraversalPolicy();
        
        setFocusTraversalPolicy(new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
                return defaultPolicy.getComponentAfter(focusCycleRoot, aComponent);
            }

            @Override
            public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
                return defaultPolicy.getComponentBefore(focusCycleRoot, aComponent);
            }

            @Override
            public Component getFirstComponent(Container focusCycleRoot) {
                return defaultPolicy.getFirstComponent(focusCycleRoot);
            }

            @Override
            public Component getLastComponent(Container focusCycleRoot) {
                return defaultPolicy.getLastComponent(focusCycleRoot);
            }

            @Override
            public Component getDefaultComponent(Container focusCycleRoot) {
                return workspaceNameEntryField;
            }
        });
        
        // Update dialog's button state.
        updateButtonState();
    }
    
    /**
     * @return the Preferences node for this package.
     */
    static Preferences getPreferences() {
        return Preferences.userNodeForPackage(DeployWorkspaceDialog.class);
    }

    /**
     * Get a Table for the given table model.
     * @param tableModel the table model.
     * @return the Table initialized with the given model.
     */
    private static JTable getJTable(TableModel tableModel) {

        final JTable jTable = new JTable(tableModel){
            private static final long serialVersionUID = 2821561043719244069L;

            /**
             * {@inheritDoc}
             * Overriden to implement table header tool tips.
             */
            @Override
            protected JTableHeader createDefaultTableHeader() {

                return new JTableHeader(columnModel) {
                    private static final long serialVersionUID = -158445819121843666L;

                    @Override
                    public String getToolTipText(MouseEvent e) {
                        int index = columnModel.getColumnIndexAtX(e.getPoint().x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        return columnToolTips[realIndex];
                    }
                };
            }
            
            /**
             * {@inheritDoc}
             * Override to attempt to force the width to be the same as the viewport width.
             */
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }

        };
        // Disable Dnd
        jTable.setDragEnabled(false);
        
        // Dragging while the mouse button is down causes the selected cell to change..
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Disable column reordering and resizing, cell selection, and grid cell visibility.
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.getTableHeader().setResizingAllowed(false);
        jTable.setCellSelectionEnabled(false);
        jTable.setShowGrid(false);
        
        jTable.getColumnModel().getColumn(2).setCellRenderer(numberCellRenderer);

        return jTable;
    }

    /**
     * Set the column header for a column on the given table, set to the minimum reasonable size to fit its text.
     * @param jTable the table.
     * @param column the index of the colum on the table.
     */
    private static void setMinSizeColumnHeader(JTable jTable, int column) {
        TableCellRenderer tableHeaderRenderer = jTable.getTableHeader().getDefaultRenderer();
        TableColumn tableColumn = jTable.getColumnModel().getColumn(column);
        
        // Set the header renderer, or else setting the size of the column will have no effect.
        tableColumn.setHeaderRenderer(tableHeaderRenderer);
        
        // Resize just enough to fit the header width.
        tableColumn.sizeWidthToFit();

        // Resize again to add a bit more space.
        int minWidth = tableColumn.getMinWidth();
        tableColumn.setMinWidth(minWidth + 15);

        int maxWidth = tableColumn.getMaxWidth();
        tableColumn.setMaxWidth(maxWidth + 15);
        
        // Set the preferred width last, or else for some reason the table sizing won't be correct.
        tableColumn.setPreferredWidth(minWidth + 15);
    }
    
//    /**
//     * Test target.
//     * @param args
//     */
//    public static void main(String[] args) throws Exception {
//        ModuleRevisionInfo[] moduleRevisions = {
//                new ModuleRevisionInfo(CAL_Prelude.MODULE_NAME, 16, true),
//                new ModuleRevisionInfo(CALPlatformTestModuleNames.M1, 2, false),
//                new ModuleRevisionInfo(CALPlatformTestModuleNames.M2, 1, true),
//                new ModuleRevisionInfo("Time", 3, false),
//                new ModuleRevisionInfo("CA1", 5, true),
//                new ModuleRevisionInfo("LegacyAttributes1", 1, true),
//                new ModuleRevisionInfo("Attributes", 5, false),
//                
////                new ModuleRevisionInfo(CAL_Prelude.MODULE_NAME, 16),
////                new ModuleRevisionInfo(CALPlatformTestModuleNames.M1, 2),
////                new ModuleRevisionInfo(CALPlatformTestModuleNames.M2, 1),
////                new ModuleRevisionInfo("Time", 3),
////                new ModuleRevisionInfo("CA1", 5),
////                new ModuleRevisionInfo("LegacyAttributes1", 1),
////                new ModuleRevisionInfo("Attributes", 5),
////                new ModuleRevisionInfo(CAL_Prelude.MODULE_NAME, 16),
////                new ModuleRevisionInfo(CALPlatformTestModuleNames.M1, 2),
////                new ModuleRevisionInfo(CALPlatformTestModuleNames.M2, 1),
////                new ModuleRevisionInfo("Time", 3),
////                new ModuleRevisionInfo("CA1", 5),
////                new ModuleRevisionInfo("LegacyAttributes1", 1),
////                new ModuleRevisionInfo("Attributes", 5),
////                new ModuleRevisionInfo(CAL_Prelude.MODULE_NAME, 16),
////                new ModuleRevisionInfo(CALPlatformTestModuleNames.M1, 2),
////                new ModuleRevisionInfo(CALPlatformTestModuleNames.M2, 1),
////                new ModuleRevisionInfo("Time", 3),
////                new ModuleRevisionInfo("CA1", 5),
////                new ModuleRevisionInfo("LegacyAttributes1", 1),
////                new ModuleRevisionInfo("Attributes", 5),
//                
//                new ModuleRevisionInfo("Foo", 1, false)
//        };
////        String[] workspaceNames = {"foo.cws", "bar.cws"};
//        String[] workspaceNames = null;
//        
//        
//        DeployWorkspaceDialog dialog = new DeployWorkspaceDialog(null, moduleRevisions, workspaceNames) {
//            public void hide() {
//                super.hide();
//                System.exit(0);
//            }
//        };
//        
//        dialog.doModal();
//        
//    }

    /**
     * @return a default name for the workspace text entry area.
     */
    private String getDefaultWorkspaceName() {
        // Use the system user name, if any..
        String userName = System.getProperty("user.name");
        if (userName != null) {
            return userName.toLowerCase() + ".cws";
        }
        
        // Couldn't get a system user name.  Hmm...
        return "default.cws";
    }
    
    /**
     * Update the dialog state according to the values entered in the dialog.
     */
    private void updateButtonState() {
        String textFieldText = workspaceNameEntryField.getText();
        okButton.setEnabled(textFieldText != null && textFieldText.length() > 0);
    }
    

    /**
     * Handle the action where the user presses the "choose" button.
     */
    private void handleChooseButtonPress() {
        String message = "Select an existing workspace";
        String title = "Select workspace";
        Object selectedWorkspaceName = JOptionPane.showInputDialog(DeployWorkspaceDialog.this, message, title, 
                JOptionPane.QUESTION_MESSAGE, null, existingWorkspaceNames, existingWorkspaceNames[0]);
        
        if (selectedWorkspaceName == null) {
            return;

        } else {
            // Set the text field.
            workspaceNameEntryField.setText((String)selectedWorkspaceName);
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doModal() {
        boolean result = super.doModal();

        // Save the preference if not cancelled.
        if (result) {
            getPreferences().put(WORKSPACE_DECLARATION_NAME_KEY, getResult().getWorkspaceName());
        }
        
        return result;
    }
    
    /**
     * @return the current user-editable info contained in the dialog.
     */
    public DeployDialogResult getResult() {
        return new DeployDialogResult(tableModel.getModulesToDeploy(), workspaceNameEntryField.getText());
    }

}
