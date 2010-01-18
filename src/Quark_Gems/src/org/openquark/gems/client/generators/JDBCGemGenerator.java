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
 * JDBCGemGenerator.java
 * Creation date: Oct 9, 2003
 * By: Richard Webster
 */
package org.openquark.gems.client.generators;

import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.module.Cal.Data.CAL_DataGems;
import org.openquark.cal.module.Cal.Data.CAL_Sql;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.GemFilter;
import org.openquark.cal.services.GemViewer;
import org.openquark.cal.services.IdentifierUtils;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.valuenode.Target;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.GemCutter;
import org.openquark.gems.client.ValueRunner;
import org.openquark.gems.client.valueentry.JDBCResultSetEditor;
import org.openquark.gems.client.valueentry.ValueEditor;
import org.openquark.gems.client.valueentry.ValueEditorHierarchyManager;
import org.openquark.gems.client.valueentry.ValueEditorManager;
import org.openquark.util.UnsafeCast;
import org.openquark.util.datadictionary.ValueType;


/**
 * A JDBC data source gem generator.
 * @author Richard Webster
 */
public class JDBCGemGenerator implements GemGenerator {
    /** The icon to use for the generator. */
    private static final Icon GENERATOR_ICON = new ImageIcon(GemGenerator.class.getResource("/Resources/supercombinator.gif"));

    /**
     * @see org.openquark.gems.client.generators.GemGenerator#launchGenerator(javax.swing.JFrame, org.openquark.cal.services.Perspective, org.openquark.gems.client.ValueRunner, org.openquark.gems.client.valueentry.ValueEditorManager, org.openquark.cal.compiler.TypeChecker)
     */
    public GemGenerator.GeneratedDefinitions launchGenerator(JFrame parent,
                                                         Perspective perspective,
                                                         ValueRunner valueRunner,
                                                         ValueEditorManager valueEditorManager,
                                                         TypeChecker typeChecker) {
        if (parent == null || perspective == null) {
            throw new NullPointerException();
        }

        final JDBCGemGeneratorDialog generatorUI = new JDBCGemGeneratorDialog(parent, perspective, valueRunner, valueEditorManager, typeChecker);

        generatorUI.setVisible(true);

        return new GemGenerator.GeneratedDefinitions() {

            public ModuleDefn getModuleDefn() {
                return null;
            }

            public Map<String, String> getSourceElementMap() {
                return generatorUI.getSourceDefinitions();
            }
        };
    }

    /**
     * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorMenuName()
     */
    public String getGeneratorMenuName() {
        return GeneratorMessages.getString("JDBCGF_MenuName");
    }
    
    /**
     * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorTitle()
     */
    public String getGeneratorTitle() {
        return GeneratorMessages.getString("JDBCGF_GenerateDataSourceTitle");
    }

    /**
     * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorIcon()
     */
    public Icon getGeneratorIcon() {
        return GENERATOR_ICON;
    }

    /**
     * This is the user interface class for the JDBC gem generator.
     * @author Richard Webster
     */
    private static class JDBCGemGeneratorDialog extends JDialog {
        private static final long serialVersionUID = -803397038103407688L;

        /** The icon to use for error messages. */
        private static final Icon ERROR_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/error.gif"));

        /** The icon to use for warning messages. */
        private static final Icon WARNING_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/warning.gif"));

//        /** The icon to use if everything is ok. */
//        private static final Icon OK_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/checkmark.gif"));

        /** Default width for list boxes in the dialog. */
        private static final int DEFAULT_LIST_WIDTH = 160;

        /** Default height for list boxes in the dialog. */
        private static final int DEFAULT_LIST_HEIGHT = 160;

        /** The perspective this UI is running in. */
        private final Perspective perspective;

        /** A value runner for running simple CAL programs. */
        private final ValueRunner valueRunner;

        /** The manager for value editors in the dialog. */ 
        private final ValueEditorManager valueEditorManager;

        /** The hierarchy manager for value editors in the dialog. */
        private final ValueEditorHierarchyManager valueEditorHierarchyManager;

        /** A type checker for fetching type information. */
        private final TypeChecker typeChecker;

        /**
         * Map from source name to source code. 
         * The list of source definitions we want to create. Ordered by insertion order, so that
         * definitions we want to create first will be created first.
         */
        private final Map<String, String> sourceDefinitions = new LinkedHashMap<String, String>();

        /** The panel which holds the pages of the wizard. */
        private JPanel cardPanel;

        /** The card layout manager for the wizard. */
        private final CardLayout cardLayout = new CardLayout();

        /** The Finish button for the dialog. */
        private JButton finishButton = null;
    
        /** The cancel button for the dialog. */
        private JButton cancelButton = null;

        /** The previous button for the dialog. */
        private JButton previousButton = null;

        /** The next button for the dialog. */
        private JButton nextButton = null;       

        /** A map of page names to pages. */
        private Map<String, WizardCard> nameToPageMap = new HashMap<String, WizardCard>();

        /** A stack of the previous and current pages. */
        private Stack<WizardCard> viewedPageStack = new Stack<WizardCard>();

        // The following fields are used to store the values when the pages of the wizard are committed.
        private String gemName = "";
        private String gemComment = "";
        private Scope gemScope = Scope.PUBLIC;
        private String connectionGemName = "";
        private String sqlBuilderGemName = "";
        private String tableName = "";
        private final List <FieldInfo> selectedFields = new ArrayList<FieldInfo>();
        private final List <SortField> sortFields = new ArrayList<SortField>();
        private boolean includeRecordExtractorGem = false;

        /**
         * Constructor for a new generator ui.
         * @param parent the parent of the dialog
         * @param perspective the perspective the UI should use
         */
        public JDBCGemGeneratorDialog(JFrame parent,
                                    Perspective perspective,
                                    ValueRunner valueRunner,
                                    ValueEditorManager valueEditorManager,
                                    TypeChecker typeChecker) {
            super(parent, true);

            if (perspective == null || valueRunner == null) {
                throw new NullPointerException();
            }

            this.perspective = perspective;
            this.valueRunner = valueRunner;
            this.valueEditorManager = valueEditorManager;
//            this.valueEditorHierarchyManager = valueEditorHierarchyManager;
            this.valueEditorHierarchyManager = new ValueEditorHierarchyManager(valueEditorManager);
            this.typeChecker = typeChecker;

            setTitle(GeneratorMessages.getString("JDBCGF_CreateResultsetGemTitle"));

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(getCardPanel(), BorderLayout.CENTER);
            getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);

            pack();

            // position in the center of the parent window
            int x = parent.getX() + parent.getWidth() / 2 - getWidth() / 2;
            int y = parent.getY() + parent.getHeight() / 2 - getHeight() / 2;
            setLocation(Math.max(parent.getX(), x), Math.max(parent.getY(), y));

            // Prevent the dialog from being resized too small.
            // TODO: use the SizeConstrainer class here instead.
            final Dimension minimumSize = getSize();
            addComponentListener(new ComponentAdapter() {
                    public void componentResized(ComponentEvent e) {
                        super.componentResized(e);

                        Dimension size = getSize ();
                        size.width = Math.max(minimumSize.width, size.width);
                        size.height = Math.max(minimumSize.height, size.height);
                        setSize (size);
                    }
                });
        }

        /**
         * @return the new source definitions that should be created
         */
        public Map<String, String> getSourceDefinitions() {
            return sourceDefinitions;
        }

        /**
         * Returns a panel which holds the cards for each page of the wizard.
         */
        private JPanel getCardPanel() {
            if (cardPanel == null) {
                cardPanel = new JPanel();
                cardPanel.setLayout(cardLayout);

                WizardCard firstPage = new GemInfoCard();
                addWizardPage(firstPage);
                addWizardPage(new SelectConnectionCard());
                addWizardPage(new SelectTablesCard());
                addWizardPage(new SelectFieldsCard());
                addWizardPage(new SortingCard());
                addWizardPage(new SqlQueryCard());
                addWizardPage(new PreviewCard());

                showFirstPage(firstPage);
            }

            return cardPanel;
        }

        /**
         * Adds a page to the wizard and builds a mapping of page names to pages.
         */
        private void addWizardPage(WizardCard page) {
            page.buildUI();
            String pageName = page.getCardName();
            cardPanel.add(page, pageName);
            nameToPageMap.put(pageName, page);
        }

        /**
         * A helper class to hold information about a field.
         */
        private static class FieldInfo {
            public final String fieldName;
            public final ValueType valueType;

            FieldInfo(String fieldName, ValueType valueType) {
                this.fieldName = fieldName;
                this.valueType = valueType;
            }

            public String toString() {
                return fieldName;
            }

            public boolean equals(Object other) {
                if (other == null || other.getClass() != getClass()) {
                    return false;
                }

                FieldInfo otherFieldInfo = (FieldInfo) other;
                return fieldName.equals(otherFieldInfo.fieldName);
            }

            public int hashCode() {
                return fieldName.hashCode();
            }
        }

        /**
         * A helper class to hold information about a sort field.
         */
        private static class SortField {
            public final FieldInfo fieldInfo;
            public boolean ascending = true;

            SortField(FieldInfo fieldInfo, boolean ascending) {
                this.fieldInfo = fieldInfo;
                this.ascending = ascending;
            }

            public String toString() {
                String directionString = ascending ? "ASC" : "DESC";
                return fieldInfo.toString() + " - " + directionString;
            }

            public boolean equals(Object other) {
                if (other == null || other.getClass() != getClass()) {
                    return false;
                }

                SortField otherSortField = (SortField) other;
                return fieldInfo.equals(otherSortField.fieldInfo);
            }

            public int hashCode() {
                return fieldInfo.hashCode();
            }
        }

        /**
         * Base class for UI panels of a wizard.
         */
        private abstract class WizardCard extends JPanel {
            /** Indicates whether the Finish button will enable when the contents of this page are valid. */
            private final boolean canFinishOnPage;

            /**
             * WizardCard constructor.
             */
            WizardCard(boolean canFinishOnPage) {
                this.canFinishOnPage = canFinishOnPage;
            }

            /**
             * Constructs the UI.
             * This needs to be done after the constructor since it will call methods implemented
             * in subclasses.
             */
            void buildUI() {
                setLayout(new BorderLayout());
                add(getTitlePanel(), BorderLayout.NORTH);
                add(getMainPanel(), BorderLayout.CENTER);
            }

            /**
             * Returns a string which indentifies the page.
             */
            abstract String getCardName();

            /**
             * Returns the name of the following card, or null if this is the last card.
             */
            abstract String getNextCardName();

            /**
             * Returns the resource ID for the card title.
             */
            abstract String getTitleResourceID();

            /**
             * Returns the resource ID for the card subtitle.
             */
            abstract String getSubtitleResourceID();

            /**
             * Returns the panel containing the main UI for the card.
             */
            abstract JPanel getMainPanel();

            /**
             * Returns whether the values in the UI controls of this page are acceptable.
             */
            abstract boolean isPageContentValid();

            /**
             * If the values in the UI are acceptable, the values are copied to
             * the result info and true is returned.
             * Otherwise, a message should be displayed and false returned.
             */
            abstract boolean commitChanges();

            /**
             * This is called before displaying the page to allow the UI controls
             * to be set with the current values.
             */
            void initializeControls() {
                updateButtonBar();
            }

            /**
             * Enables/disables the Previous, Next, and Finish buttons in the dialog
             * depending on the current state of the page.
             */
            void updateButtonBar() {
                boolean contentValid = isPageContentValid();

                // Enable the Previous button, unless this is the first page.
                getPreviousButton().setEnabled(!isFirstPage());

                // Enable the Next button, unless this is the last page.
                getNextButton().setEnabled(contentValid && !isLastPage());

                // Enable the Finish button if it is allowed on this page.
                getFinishButton().setEnabled(contentValid && canFinishOnPage); 
            }

            /**
             * Returns whether this is the first page of the wizard.
             */
            boolean isFirstPage() {
                return (getFirstCard() == this);
            }

            /**
             * Returns whether this is the last page of the wizard.
             */
            boolean isLastPage() {
                return (getNextCardName() == null);
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
            
                JLabel titleLabel = new JLabel(GeneratorMessages.getString(getTitleResourceID()));
                titleLabel.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 2));
                titlePanel.add(titleLabel, BorderLayout.NORTH);
    
                JLabel subTitleLabel = new JLabel(GeneratorMessages.getString(getSubtitleResourceID()));
                titlePanel.add(subTitleLabel, BorderLayout.SOUTH);
    
                return titlePanel;
            }
        }

        /**
         * Panel to display UI for setting general properties for the gem (name, scope, comments, etc...).
         */
        private class GemInfoCard extends WizardCard {
            private static final long serialVersionUID = 3421717262252083704L;

            static final String GEMINFO_CARDNAME = "GemInfo";

            /** The text field for entering the name of the new gem. */
            private final JTextField gemNameField = new JTextField();
    
            /** The text field for entering the comment for the new gem. */
            private final JTextField commentField = new JTextField();

            /** The radio button for selecting private scope. */
            private final JRadioButton privateButton = new JRadioButton(GeneratorMessages.getString("PrivateLabel"));
    
            /** The radio button for selecting public scope. */
            private final JRadioButton publicButton = new JRadioButton(GeneratorMessages.getString("PublicLabel"));

            /** The button group for the radio buttons. */
            private final ButtonGroup buttonGroup = new ButtonGroup();

            /** A check box to indicate whether a record extractor gem should also be generated. */
            private final JCheckBox addRecordExtractorCheck = new JCheckBox(GeneratorMessages.getString("JDBCGF_IncludeRecordExtractorGemLabel"));
            
            /** The label for displaying status messages. */
            private final JLabel statusLabel = new JLabel();

            /** Whether or not the user has typed into the name field. */
            private boolean userHasTyped = false;

            /**
             * GemInfoCard constructor.
             */
            GemInfoCard() {
                super(false);
                buttonGroup.add(publicButton);
                buttonGroup.add(privateButton);
                buttonGroup.setSelected(publicButton.getModel(), true);

                updateState();

                // Make the class name field have default focus
                setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            
                    private static final long serialVersionUID = -4407775698002965050L;

                    public Component getDefaultComponent(Container c) {
                        return gemNameField;
                    }
                });
        
                // Add listeners to update the error message if things change
                gemNameField.addKeyListener(new KeyAdapter() {
                    public void keyReleased(KeyEvent e) {
                        userHasTyped = true;
                        updateState();
                    }
                });
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getCardName()
             */
            String getCardName() {
                return GEMINFO_CARDNAME;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getNextCardName()
             */
            String getNextCardName() {
                return SelectConnectionCard.SELECTCONNECTION_CARDNAME;
            }

            /**
             * Returns the resource ID for the card title.
             */
            String getTitleResourceID() {
                return "JDBCGF_GemInfoPageTitle";
            }

            /**
             * Returns the resource ID for the card subtitle.
             */
            String getSubtitleResourceID() {
                return "JDBCGF_GemInfoPageSubTitle";
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#isPageContentValid()
             */
            boolean isPageContentValid() {
                return checkForValidName();
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#commitChanges()
             */
            boolean commitChanges() {
                gemName = gemNameField.getText();
                gemComment = commentField.getText();
                gemScope = publicButton.getModel().isSelected() ? Scope.PUBLIC : Scope.PRIVATE;
                includeRecordExtractorGem = addRecordExtractorCheck.isSelected();

                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#initializeControls()
             */
            void initializeControls() {
                super.initializeControls();

                // Update the controls with the current values.
                gemNameField.setText(gemName);
                commentField.setText(gemComment);
                if (gemScope == Scope.PUBLIC) {
                    publicButton.setSelected(true);
                } else {
                    privateButton.setSelected(true);
                }

                addRecordExtractorCheck.setSelected(includeRecordExtractorGem);
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getMainPanel()
             */
            JPanel getMainPanel() {

                JPanel javaPanel = new JPanel();

                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                constraints.insets = new Insets(5, 5, 10, 5);
                javaPanel.add(statusLabel, constraints);

                statusLabel.setFont(getFont().deriveFont(Font.BOLD));

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                javaPanel.add(new JLabel(GeneratorMessages.getString("JGF_GemNameHeader")), constraints);

                constraints.gridx = 2;
                constraints.weightx = 1;
                constraints.weighty = 0;        
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                gemNameField.setColumns(20);
                javaPanel.add(gemNameField, constraints);

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                javaPanel.add(new JLabel(GeneratorMessages.getString("JGF_CommentHeader")), constraints);

                constraints.gridx = 2;
                constraints.weightx = 1;
                constraints.weighty = 0;        
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                gemNameField.setColumns(20);
                javaPanel.add(commentField, constraints);

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                javaPanel.add(new JLabel(GeneratorMessages.getString("JGF_VisibilityHeader")), constraints);

                constraints.gridx = 2;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;        
                javaPanel.add(publicButton, constraints);

                constraints.gridx = 3;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;        
                javaPanel.add(privateButton, constraints);

                constraints.gridx = 1;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                javaPanel.add(addRecordExtractorCheck, constraints);

                constraints.gridx = 4;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                javaPanel.add(new JLabel(""), constraints);

                return javaPanel;
            }

            /**
             * Updates the state of the Ok button to only be enabled if the user has entered
             * all required information.
             */
            private void updateState() {

                if (!checkForValidName()) {
                    if (userHasTyped) {
                        statusLabel.setText(GeneratorMessages.getString("JGF_InvalidGemName"));
                        statusLabel.setIcon(ERROR_ICON);
                    }
                }
                else {
                    // check if the gem or data type already exists
                    String gemName = gemNameField.getText();
                    if (perspective.getWorkingModuleTypeInfo().getFunction(gemName) != null) {
                        statusLabel.setText(GeneratorMessages.getString("JGF_GemExists"));
                        statusLabel.setIcon(WARNING_ICON);
                    }
                    else {
//                        statusLabel.setText(GeneratorMessages.getString("JDBCGF_OkCreateGem"));
//                        statusLabel.setIcon(OK_ICON);
                        statusLabel.setText("");
                        statusLabel.setIcon(null);
                    }
                }

                // Enable/disable the Next and/or Finish buttons in the dialog.
                updateButtonBar();
            }

            /**
             * Checks the vailidity of the name in the gem name field.
             * @return true if the provided name is valid, false otherwise
             */
            private boolean checkForValidName() {
                String gemName = gemNameField.getText();

                // If the user has typed a name, check if for validity.
                boolean validGemName = LanguageInfo.isValidFunctionName(gemName);
//                if (!validGemName) {
//                    okButton.setEnabled(false);
//
//                    if (userHasTyped) {
//                        statusLabel.setText(GeneratorMessages.getString("JGF_InvalidGemName"));
//                        statusLabel.setIcon(ERROR_ICON);
//                    }
//                }

                return validGemName;
            }
        }

        /**
         * Panel to display UI for selecting the JDBC connection gem.
         */
        private class SelectConnectionCard extends WizardCard {
            private static final long serialVersionUID = 7494176766603605837L;

            static final String SELECTCONNECTION_CARDNAME = "SelectConnection";

            /** The combobox for selecting a connection gem. */
            private final JComboBox connectionGemCombo = new JComboBox(new DefaultComboBoxModel());

            /** The combobox for selecting a SQL builder gem. */
            private final JComboBox sqlBuilderGemCombo = new JComboBox(new DefaultComboBoxModel());

            /**
             * SelectConnectionCard constructor.
             */
            SelectConnectionCard() {
                super(false);

                // Make the connection combo box have default focus
                setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            
                    private static final long serialVersionUID = -226727560145900885L;

                    public Component getDefaultComponent(Container c) {
                        return connectionGemCombo;
                    }
                });
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getCardName()
             */
            String getCardName() {
                return SELECTCONNECTION_CARDNAME;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getNextCardName()
             */
            String getNextCardName() {
                return SelectTablesCard.SELECTTABLES_CARDNAME;
            }

            /**
             * Returns the resource ID for the card title.
             */
            String getTitleResourceID() {
                return "JDBCGF_SelectConnectionPageTitle";
            }

            /**
             * Returns the resource ID for the card subtitle.
             */
            String getSubtitleResourceID() {
                return "JDBCGF_SelectConnectionPageSubTitle";
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#isPageContentValid()
             */
            boolean isPageContentValid() {
                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#commitChanges()
             */
            boolean commitChanges() {
                connectionGemName = connectionGemCombo.getSelectedItem().toString();
                sqlBuilderGemName = sqlBuilderGemCombo.getSelectedItem().toString();

                if (connectionGemName.equals("<new>")) {
                    JOptionPane.showMessageDialog(this, "The '<new>' connection gem option is not implemented yet.");
                    return false;
                }
                // TODO: attempt to open the connection.
                //       If this fails, display the error message and return false.

                // TODO: invalidate all info about the query if the connection gem changes...

                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#initializeControls()
             */
            void initializeControls() {
                super.initializeControls();

                // Populate the connection gems list, if necessary.
                if (connectionGemCombo.getItemCount() == 0) {
                    // Add a special option to create a new connection.
                    connectionGemCombo.addItem("<new>");
  
                    // Add each available connection gem.
                    List<String> connectionGems = fetchConnectionGemNames();
                    for (final String aConnectionGemName : connectionGems) {
                        connectionGemCombo.addItem(aConnectionGemName);  
                    }
                }

                // Populate the SQL builder gem list, if necessary.
                if (sqlBuilderGemCombo.getItemCount() == 0) {
                    // Add each available SQL builder gem.
                    List<String> sqlBuilderGems = fetchSqlBuilderGemNames();
                    for (final String aSqlBuilderGemName : sqlBuilderGems) {
                        sqlBuilderGemCombo.addItem(aSqlBuilderGemName);  
                    }
                }

                // Update the controls with the current values.
                connectionGemCombo.setSelectedItem(connectionGemName);
                sqlBuilderGemCombo.setSelectedItem(sqlBuilderGemName);
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getMainPanel()
             */
            JPanel getMainPanel() {
            
                JPanel javaPanel = new JPanel();
            
                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());
    
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;
    
//                constraints.gridx = 1;
//                constraints.weightx = 0;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                constraints.insets = new Insets(5, 5, 10, 5);
//                javaPanel.add(statusLabel, constraints);
//    
//                statusLabel.setFont(getFont().deriveFont(Font.BOLD));
    
                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                javaPanel.add(new JLabel(GeneratorMessages.getString("JDBCGF_ConnectionGemNameHeader")), constraints);
    
                constraints.gridx = 2;
                constraints.weightx = 1;
                constraints.weighty = 0;        
                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                connectionGemCombo.setColumns(20);
                javaPanel.add(connectionGemCombo, constraints);

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                javaPanel.add(new JLabel(GeneratorMessages.getString("JDBCGF_SqlBuilderGemNameHeader")), constraints);

                constraints.gridx = 2;
                constraints.weightx = 1;
                constraints.weighty = 0;        
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                javaPanel.add(sqlBuilderGemCombo, constraints);

                constraints.gridx = 4;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                javaPanel.add(new JLabel(""), constraints);
    
                return javaPanel;
            }

            /**
             * Returns a list of the names of the available connection gems.
             */
            private List <String> fetchConnectionGemNames() {
                TypeConstructor typeConstructor = perspective.getTypeConstructor(CAL_DataGems.TypeConstructors.JDBCConnection);
                final TypeExpr typeExpr = TypeExpr.makeNonParametricType(typeConstructor);

                return fetchGemsOfType(typeExpr);
            }

            /**
             * Returns a list of the names of the available connection gems.
             */
            private List <String> fetchSqlBuilderGemNames() {
                TypeExpr typeExpr = typeChecker.getTypeFromString(CAL_Sql.TypeConstructors.SqlBuilder.getQualifiedName(), perspective.getWorkingModule().getName(), null);
                return fetchGemsOfType(typeExpr);
            }

            /**
             * Returns a list of the names of the gems which have the specified return type and no inputs.
             */
            private List <String> fetchGemsOfType(final TypeExpr returnTypeExpr) {
                GemViewer gemViewer = new GemViewer();
                
                final ModuleTypeInfo moduleTypeInfo = perspective.getWorkingModuleTypeInfo();

                // Create a filter which will find all gems which return the specified type
                // and take no inputs.
                // TODO: add support for gems which do take inputs...
                GemFilter filter = new GemFilter() {
                    public boolean select(GemEntity gemEntity) {
                        TypeExpr gemType = gemEntity.getTypeExpr();
                        return TypeExpr.canPatternMatch(gemType, returnTypeExpr, moduleTypeInfo);
                    }
                };
                gemViewer.addFilter(filter);

                Perspective newPerspective = new Perspective(perspective.getWorkspace(), perspective.getWorkingModule());
                newPerspective.setGemEntityViewer(gemViewer);

                Set<GemEntity> matchingGems = newPerspective.getVisibleGemEntities(); 

                // Extract the gem names from the list.
                List<String> gemNames = new ArrayList<String>();
                for (final GemEntity gemEntity : matchingGems) {
                    gemNames.add(gemEntity.getName().getQualifiedName());
                }

                return gemNames;
            }
        }

        /**
         * Panel to display UI for selecting the tables.
         */
        private class SelectTablesCard extends WizardCard {
            private static final long serialVersionUID = 255937577857138828L;

            static final String SELECTTABLES_CARDNAME = "SelectTables";

            /** The connection gem used to fetch the list of tables currently in the combobox. */
            private String connectionForTables = "";

            /** The combobox for selecting a table. */
            private final JComboBox tableCombo = new JComboBox(new DefaultComboBoxModel());

// TODO: use this later...
//            /** The list displaying the available tables. */
//            private final JListBox sourceTableList;
//
//            /** The list display the selected tables. */
//            private final JListBox destTableList;

            /**
             * SelectTablesCard constructor.
             */
            SelectTablesCard() {
                super(false);

// TODO: use this later to tell the user to select at least one table...
//                updateState();

                // Make the connection combo box have default focus
                setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            
                    private static final long serialVersionUID = 4742375628497579705L;

                    public Component getDefaultComponent(Container c) {
                        return tableCombo;
                    }
                });
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getCardName()
             */
            String getCardName() {
                return SELECTTABLES_CARDNAME;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getNextCardName()
             */
            String getNextCardName() {
                return SelectFieldsCard.SELECTFIELDS_CARDNAME;
            }

            /**
             * Returns the resource ID for the card title.
             */
            String getTitleResourceID() {
                return "JDBCGF_SelectTablesPageTitle";
            }

            /**
             * Returns the resource ID for the card subtitle.
             */
            String getSubtitleResourceID() {
                return "JDBCGF_SelectTablesPageSubTitle";
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#isPageContentValid()
             */
            boolean isPageContentValid() {
                // TODO: handle the case where no tables can be found in the connection...
                // TODO: check that at least one table is selected (once multi-table support is added)...
                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#commitChanges()
             */
            boolean commitChanges() {
                tableName = tableCombo.getSelectedItem().toString();

                // TODO: invalidate all info on subsequent tabs (field selection, etc...) if the connection gem changes...

                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#initializeControls()
             */
            void initializeControls() {
                super.initializeControls();

                // If the connection has been changed since the tables were fetched, rebuild the list.
                if (!connectionForTables.equals(connectionGemName)) {
                    tableCombo.removeAllItems();
                }

                // Populate the table list, if necessary.
                if (tableCombo.getItemCount() == 0) {

                    // Add each available table.
                    List<String> tableNames = fetchTableNames();
                    for (final String aTableName : tableNames) {
                        tableCombo.addItem(aTableName);  
                    }

                    connectionForTables = connectionGemName;
                }

                // Update the controls with the current values.
                tableCombo.setSelectedItem(tableName);
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getMainPanel()
             */
            JPanel getMainPanel() {
            
                JPanel javaPanel = new JPanel();
            
                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());
    
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;
    
//                constraints.gridx = 1;
//                constraints.weightx = 0;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                constraints.insets = new Insets(5, 5, 10, 5);
//                javaPanel.add(statusLabel, constraints);
//    
//                statusLabel.setFont(getFont().deriveFont(Font.BOLD));
    
                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                javaPanel.add(new JLabel(GeneratorMessages.getString("JDBCGF_TableNameHeader")), constraints);
    
                constraints.gridx = 2;
                constraints.weightx = 1;
                constraints.weighty = 0;        
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                javaPanel.add(tableCombo, constraints);

                constraints.gridx = 4;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                javaPanel.add(new JLabel(""), constraints);
    
                return javaPanel;
            }

//            /**
//             * Updates the state of the Ok button to only be enabled if the user has entered
//             * all required information.
//             */
//            private void updateState() {
////
////                if (!checkForValidName()) {
////                    if (userHasTyped) {
////                        statusLabel.setText(GeneratorMessages.getString("JGF_InvalidGemName"));
////                        statusLabel.setIcon(ERROR_ICON);
////                    }
////                }
////                else {
////                    // check if the gem or data type already exists
////                    String gemName = gemNameField.getText();
////                    if (perspective.getWorkingModuleTypeInfo().getSupercombinator(gemName) != null) {
////                        statusLabel.setText(GeneratorMessages.getString("JGF_GemExists"));
////                        statusLabel.setIcon(WARNING_ICON);
////                    }
////                    else {
//////                        statusLabel.setText(GeneratorMessages.getString("JDBCGF_OkCreateGem"));
//////                        statusLabel.setIcon(OK_ICON);
////                        statusLabel.setText("");
////                        statusLabel.setIcon(null);
////                    }
////                }
//
//                // Enable/disable the Next and/or Finish buttons in the dialog.
//                updateButtonBar();
//            }

            /**
             * Returns a list of the table names for the current connection.
             */
            private List <String> fetchTableNames() {
                // TODO: return qualified table names...
                String code = CAL_Prelude.Functions.output.getQualifiedName() + " (" + CAL_DataGems.Functions.jdbcGetConnectionTableNames.getQualifiedName() + " " + connectionGemName + ")";
                return UnsafeCast.unsafeCast(getListFromCode(code));
            }
        }

        /**
         * Panel to display UI for selecting fields.
         */
        private class SelectFieldsCard extends WizardCard {
            private static final long serialVersionUID = 1702879395054351612L;

            static final String SELECTFIELDS_CARDNAME = "SelectFields";

            /** The label for displaying status messages. */
            private final JLabel statusLabel = new JLabel();

            /** Whether or not the user has added a field to the selected list. */
            private boolean fieldAdded = false;

            /** The connection gem used to fetch the list of tables currently in the combobox. */
            private String connectionForFields = "";

            /** The table used to fetch the list of fields currently in the source fields list. */
            private String tableForFields = "";

            /** The list displaying the available fields. */
            private final JList availableFieldsList = new JList(new DefaultListModel());

            /** The list display the selected fields. */
            private final JList selectedFieldsList = new JList(new DefaultListModel());

            /** The button for adding a field to the selected list. */
            private final JButton addButton = new JButton(">");

            /** The button for removing a field from the selected list. */
            private final JButton removeButton = new JButton("<");

            /** The button for moving a field up in the selected list. */
            private final JButton upButton = new JButton(new ImageIcon(GemCutter.class.getResource("/Resources/up.gif")));

            /** The button for moving a field up in the selected list. */
            private final JButton downButton = new JButton(new ImageIcon(GemCutter.class.getResource("/Resources/down.gif")));

            /**
             * SelectFieldsCard constructor.
             */
            SelectFieldsCard() {
                super(true);

                updateState();

                // Enable the 'add' button when a field is selected in the available fields list.
                availableFieldsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                availableFieldsList.addListSelectionListener(new ListSelectionListener () {
                                                        public void valueChanged(ListSelectionEvent e) {
                                                            boolean itemSelected = (availableFieldsList.getSelectedIndex () >= 0);
                                                            addButton.setEnabled(itemSelected);
                                                        }
                                                    });

                // Add fields when double clicked in the available fields list.
                availableFieldsList.addMouseListener(new MouseAdapter() {
                                                        public void mouseReleased(MouseEvent e) {
                                                            if (e.getClickCount() > 1) {
                                                                onAddFields();
                                                            }
                                                        }
                                                    });

                // Enable the 'remove' button when a field is selected in the selected fields list.
                // Also, enable the 'up' and 'down' buttons where appropriate.
                final DefaultListModel selectedListModel = (DefaultListModel) selectedFieldsList.getModel();
                selectedFieldsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                selectedFieldsList.addListSelectionListener(new ListSelectionListener () {
                                                        public void valueChanged(ListSelectionEvent e) {
                                                            int itemSelected = selectedFieldsList.getSelectedIndex ();
                                                            removeButton.setEnabled(itemSelected >= 0);

                                                            upButton.setEnabled(itemSelected > 0);
                                                            downButton.setEnabled(itemSelected < selectedListModel.getSize() - 1);
                                                        }
                                                    });

                // Remove fields when double clicked in the selected fields list.
                selectedFieldsList.addMouseListener(new MouseAdapter() {
                                                        public void mouseReleased(MouseEvent e) {
                                                            if (e.getClickCount() > 1) {
                                                                onRemoveFields();
                                                            }
                                                        }
                                                    });

                // Set the action event handler for the 'add' button.
                addButton.setEnabled(false);
                addButton.addActionListener(new ActionListener () {
                                    public void actionPerformed(ActionEvent e){
                                        onAddFields();
                                    }
                                  });

                // Set the action event handler for the 'remove' button.
                removeButton.setEnabled(false);
                removeButton.addActionListener(new ActionListener () {
                                                    public void actionPerformed(ActionEvent e){
                                                        onRemoveFields();
                                                    }
                                                  });

                // Set the action event handler for the 'up' button.
                upButton.setEnabled(false);
                upButton.setMargin(new Insets (0, 0, 0, 0));
                upButton.addActionListener(new ActionListener () {
                                                    public void actionPerformed(ActionEvent e){
                                                        int selectedIndex = selectedFieldsList.getSelectedIndex();
                                                        if (selectedIndex > 0) {
                                                            // Remove the item from the list and insert it at the previous position.
                                                            Object item = selectedListModel.remove(selectedIndex);
                                                            selectedListModel.insertElementAt(item, selectedIndex - 1);

                                                            // Reselect an appropriate item in the list.
                                                            selectedFieldsList.setSelectedIndex(selectedIndex - 1);
                                                        }
                                                    }
                                                  });

                // Set the action event handler for the 'down' button.
                downButton.setEnabled(false);
                downButton.setMargin(new Insets (0, 0, 0, 0));
                downButton.addActionListener(new ActionListener () {
                                                    public void actionPerformed(ActionEvent e){
                                                        int selectedIndex = selectedFieldsList.getSelectedIndex();
                                                        if (selectedIndex >= 0 && selectedIndex < selectedListModel.getSize() - 1) {
                                                            // Remove the item from the list and insert it at the next position.
                                                            Object item = selectedListModel.remove(selectedIndex);
                                                            selectedListModel.insertElementAt(item, selectedIndex + 1);

                                                            // Reselect an appropriate item in the list.
                                                            selectedFieldsList.setSelectedIndex(selectedIndex + 1);
                                                        }
                                                    }
                                                  });

                // Make the source fields list box have default focus
                setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            
                    private static final long serialVersionUID = -4997301051799673595L;

                    public Component getDefaultComponent(Container c) {
                        return availableFieldsList;
                    }
                });
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getCardName()
             */
            String getCardName() {
                return SELECTFIELDS_CARDNAME;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getNextCardName()
             */
            String getNextCardName() {
                return SortingCard.SORTING_CARDNAME;
            }

            /**
             * Returns the resource ID for the card title.
             */
            String getTitleResourceID() {
                return "JDBCGF_SelectFieldsPageTitle";
            }

            /**
             * Returns the resource ID for the card subtitle.
             */
            String getSubtitleResourceID() {
                return "JDBCGF_SelectFieldsPageSubTitle";
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#isPageContentValid()
             */
            boolean isPageContentValid() {
                // Check that at least one field has been selected.
                return selectedFieldsList.getModel().getSize() > 0;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#commitChanges()
             */
            boolean commitChanges() {
                selectedFields.clear();

                ListModel model = selectedFieldsList.getModel();
                for (int itemN = 0, nItems = model.getSize(); itemN < nItems; ++itemN) {
                    FieldInfo fieldInfo = (FieldInfo) model.getElementAt(itemN);
                    selectedFields.add(fieldInfo);
                }

                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#initializeControls()
             */
            void initializeControls() {
                super.initializeControls();

                DefaultListModel sourceModel = (DefaultListModel) availableFieldsList.getModel();
                DefaultListModel destModel = (DefaultListModel) selectedFieldsList.getModel();

                // If the connection or table has been changed since the fields were fetched, rebuild the list.
                if (!connectionForFields.equals(connectionGemName)
                        || !tableForFields.equals(tableName)) {
                    sourceModel.clear();
                    destModel.clear();
                }

                // Populate the source fields list, if necessary.
                if (sourceModel.isEmpty()) {

                    // Add each available field.
                    List<FieldInfo> fieldsInfo = fetchFieldsInfo();
                    for (final FieldInfo fieldInfo : fieldsInfo) {
                        sourceModel.addElement(fieldInfo);  
                    }

                    connectionForFields = connectionGemName;
                    tableForFields = tableName;

                    // Select the first item in the selected fields list, if any.
                    if (!sourceModel.isEmpty()) {
                        availableFieldsList.setSelectedIndex(0);
                    }
                }

                // Update the controls with the current values.
                destModel.clear();
                for (final FieldInfo fieldInfo : selectedFields) {
                    destModel.addElement(fieldInfo);
                }

                // Select the first item in the selected fields list, if any.
                if (!destModel.isEmpty()) {
                    selectedFieldsList.setSelectedIndex(0);
                }
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getMainPanel()
             */
            JPanel getMainPanel() {

                JPanel javaPanel = new JPanel();

                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());

                JLabel availableLabel = new JLabel (GeneratorMessages.getString("JDBCGF_AvailableFieldsHeader"));
                JLabel selectedLabel = new JLabel (GeneratorMessages.getString("JDBCGF_SelectedFieldsHeader"));

                JScrollPane availableFieldsScrollPane = new JScrollPane(availableFieldsList);
                JScrollPane selectedFieldsScrollPane = new JScrollPane(selectedFieldsList);
                availableFieldsScrollPane.setPreferredSize(new Dimension(DEFAULT_LIST_WIDTH, DEFAULT_LIST_HEIGHT));
                selectedFieldsScrollPane.setPreferredSize(new Dimension(DEFAULT_LIST_WIDTH, DEFAULT_LIST_HEIGHT));
                
                Box addRemoveBox = Box.createVerticalBox();
                addRemoveBox.add(addButton);
                addRemoveBox.add(removeButton);

                Box upDownBox = Box.createVerticalBox();
                upDownBox.add(upButton);
                upDownBox.add(downButton);

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                constraints.insets = new Insets(5, 5, 10, 5);
                javaPanel.add(statusLabel, constraints);
                statusLabel.setFont(getFont().deriveFont(Font.BOLD));
    
                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                javaPanel.add(availableLabel, constraints);
                constraints.gridx = 3;
                javaPanel.add(selectedLabel, constraints);

                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.weighty = 1;
                constraints.fill = GridBagConstraints.BOTH;
                javaPanel.add(availableFieldsScrollPane, constraints);
                constraints.gridx = 3;
                javaPanel.add(selectedFieldsScrollPane, constraints);
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 2;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.anchor = GridBagConstraints.CENTER;
                javaPanel.add(addRemoveBox, constraints);
                constraints.gridx = 4;
                javaPanel.add(upDownBox, constraints);
                constraints.anchor = GridBagConstraints.NORTHWEST;

//                constraints.gridx = 4;
//                constraints.weightx = 1;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                javaPanel.add(new JLabel(""), constraints);
    
                return javaPanel;
            }

            /**
             * Updates the state of the buttons depending on what is available and selected.
             */
            private void updateState() {
                if (fieldAdded && selectedFieldsList.getModel().getSize() == 0) {
                    statusLabel.setText(GeneratorMessages.getString("JDBCGF_FieldRequired"));
                    statusLabel.setIcon(ERROR_ICON);
                }
                else {
                    statusLabel.setText("");
                    statusLabel.setIcon(null);
                }
//
//                if (!checkForValidName()) {
//                    if (userHasTyped) {
//                        statusLabel.setText(GeneratorMessages.getString("JGF_InvalidGemName"));
//                        statusLabel.setIcon(ERROR_ICON);
//                    }
//                }
//                else {
//                    // check if the gem or data type already exists
//                    String gemName = gemNameField.getText();
//                    if (perspective.getWorkingModuleTypeInfo().getSupercombinator(gemName) != null) {
//                        statusLabel.setText(GeneratorMessages.getString("JGF_GemExists"));
//                        statusLabel.setIcon(WARNING_ICON);
//                    }
//                    else {
////                        statusLabel.setText(GeneratorMessages.getString("JDBCGF_OkCreateGem"));
////                        statusLabel.setIcon(OK_ICON);
//                        statusLabel.setText("");
//                        statusLabel.setIcon(null);
//                    }
//                }

                // Enable/disable the Next and/or Finish buttons in the dialog.
                updateButtonBar();
            }

            /**
             * Returns a list of the field names for the current table and connection.
             */
            private List<FieldInfo> fetchFieldsInfo() {
                // TODO: return qualified field names if multiple tables are selected...
                // TODO: also fetch info about the field type, max length, etc...
                // TODO: marshal the list of records directly instead of converting to a tuple once this is implemented for records...
                String code = CAL_Prelude.Functions.output.getQualifiedName() + " (" + CAL_List.Functions.map.getQualifiedName() + " (\\rec -> (rec.columnName, rec.valueType)) (" + CAL_DataGems.Functions.jdbcGetTableFieldInfo.getQualifiedName() + " " + connectionGemName + " \"" + tableName + "\"))";
                List<List<?>> tupleList = UnsafeCast.unsafeCast(getListFromCode(code));

                List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
                for (final List<?> tuple : tupleList) {
                    String fieldName = (String) tuple.get(0);
                    ValueType valueType = (ValueType) tuple.get(1);

                    fieldInfoList.add(new FieldInfo(fieldName, valueType));
                }

                return fieldInfoList;
            }

            /**
             * Add any selected fields from the available fields list to the selected fields list.
             */
            private void onAddFields(){
                final DefaultListModel availableListModel = (DefaultListModel) availableFieldsList.getModel();
                final DefaultListModel selectedListModel = (DefaultListModel) selectedFieldsList.getModel();

                Object[] selectedValues = availableFieldsList.getSelectedValues();
                int nSelected = selectedValues.length;

                for (int selectionN = 0; selectionN < nSelected; ++selectionN) {
                    FieldInfo selectedValue = (FieldInfo) selectedValues[selectionN];

                    // Don't add the same field multiple times.
                    if (!selectedListModel.contains(selectedValue)) {
                        selectedListModel.addElement(selectedValue);
                        fieldAdded = true;
                    }
                }

                if (nSelected == 1) {
                    // Select the new item in the selected fields list.
                    selectedFieldsList.setSelectedIndex(selectedListModel.getSize() - 1);

                    // Move the selection to the next field in the available fields list.
                    int[] selectedIndexes = availableFieldsList.getSelectedIndices();
                    if (selectedIndexes.length == 1) {
                        int itemSelected = selectedIndexes[0]; // availableFieldsList.getSelectedIndex();
                        if (itemSelected >= 0 && itemSelected < (availableListModel.getSize() - 1)) {
                            availableFieldsList.setSelectedIndex(itemSelected + 1);
                        }
                    }
                }

                // Update the state of the wizard's buttons.
                updateState();
            }

            /**
             * Removes any selected fields from the selected fields list.
             */
            public void onRemoveFields(){
                final DefaultListModel selectedListModel = (DefaultListModel) selectedFieldsList.getModel();

                int selectedIndex = selectedFieldsList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    selectedListModel.remove(selectedIndex);

                    // Reselect an appropriate item in the list.
                    int newSelection = selectedIndex;
                    if (newSelection >= selectedListModel.getSize()) {
                        newSelection = selectedListModel.getSize() - 1;
                    }
                    selectedFieldsList.setSelectedIndex(newSelection);

                    // Update the state of the wizard's buttons.
                    updateState();
                }
            }
        }

        /**
         * Panel to display UI for specifying sorting.
         */
        private class SortingCard extends WizardCard {
            private static final long serialVersionUID = 434382250419609741L;

            static final String SORTING_CARDNAME = "Sorting";

            /** The label for displaying status messages. */
            private final JLabel statusLabel = new JLabel();

            /** The connection gem used to fetch the list of tables currently in the combobox. */
            private String connectionForFields = "";

            /** The table used to fetch the list of fields currently in the source fields list. */
            private String tableForFields = "";

            /** The list displaying the available fields. */
            private final JList availableFieldsList = new JList(new DefaultListModel());

            /** The list display the sorting fields. */
            private final JList sortFieldsList = new JList(new DefaultListModel());

            /** The button for adding a field to the sorting list. */
            private final JButton addButton = new JButton(">");

            /** The button for removing a field from the sorting list. */
            private final JButton removeButton = new JButton("<");

            /** The button for moving a field up in the sorting list. */
            private final JButton upButton = new JButton(new ImageIcon(GemCutter.class.getResource("/Resources/up.gif")));

            /** The button for moving a field up in the sorting list. */
            private final JButton downButton = new JButton(new ImageIcon(GemCutter.class.getResource("/Resources/down.gif")));

            /** The check box which controls whether the sorting is ascending or descending. */
            private final JCheckBox ascendingCheckBox = new JCheckBox(GeneratorMessages.getString("JDBCGF_SortAscendingLabel"));

            /**
             * SortingCard constructor.
             */
            SortingCard() {
                super(true);

                // Enable the 'add' button when a field is selected in the available fields list.
                availableFieldsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                availableFieldsList.addListSelectionListener(new ListSelectionListener () {
                                                        public void valueChanged(ListSelectionEvent e) {
                                                            boolean itemSelected = (availableFieldsList.getSelectedIndex () >= 0);
                                                            addButton.setEnabled(itemSelected);
                                                        }
                                                    });

                // Add fields when double clicked in the available fields list.
                availableFieldsList.addMouseListener(new MouseAdapter() {
                                                        public void mouseReleased(MouseEvent e) {
                                                            if (e.getClickCount() > 1) {
                                                                onAddFields();
                                                            }
                                                        }
                                                    });

                // Enable the 'remove' button when a field is selected in the sorting fields list.
                // Also, enable the 'up' and 'down' buttons where appropriate.
                // Set the correct value for the Ascending check box as well.
                final DefaultListModel sortListModel = (DefaultListModel) sortFieldsList.getModel();
                sortFieldsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                sortFieldsList.addListSelectionListener(new ListSelectionListener () {
                                                        public void valueChanged(ListSelectionEvent e) {
                                                            int itemSelected = sortFieldsList.getSelectedIndex ();
                                                            removeButton.setEnabled(itemSelected >= 0);

                                                            ascendingCheckBox.setEnabled(itemSelected >= 0);
                                                            if (itemSelected < 0) {
                                                                ascendingCheckBox.setSelected(false);
                                                            }

                                                            upButton.setEnabled(itemSelected > 0);
                                                            downButton.setEnabled(itemSelected < sortListModel.getSize() - 1);

                                                            SortField sortField = (SortField) sortFieldsList.getSelectedValue();
                                                            if (sortField != null) {
                                                                ascendingCheckBox.setSelected(sortField.ascending);
                                                            }
                                                        }
                                                    });

                // Upate the selected sort field when the ascending check box is changed.
                ascendingCheckBox.setEnabled(false);
                ascendingCheckBox.addActionListener(new ActionListener () {
                                                    public void actionPerformed(ActionEvent e){
                                                        SortField sortField = (SortField) sortFieldsList.getSelectedValue();
                                                        if (sortField != null) {
                                                            sortField.ascending = ascendingCheckBox.isSelected();
                                                            sortFieldsList.repaint();
                                                        }
                                                    }
                                                  });

                // Remove fields when double clicked in the sorting fields list.
                sortFieldsList.addMouseListener(new MouseAdapter() {
                                                        public void mouseReleased(MouseEvent e) {
                                                            if (e.getClickCount() > 1) {
                                                                onRemoveFields();
                                                            }
                                                        }
                                                    });

                // Set the action event handler for the 'add' button.
                addButton.setEnabled(false);
                addButton.addActionListener(new ActionListener () {
                                    public void actionPerformed(ActionEvent e){
                                        onAddFields();
                                    }
                                  });

                // Set the action event handler for the 'remove' button.
                removeButton.setEnabled(false);
                removeButton.addActionListener(new ActionListener () {
                                                    public void actionPerformed(ActionEvent e){
                                                        onRemoveFields();
                                                    }
                                                  });

                // Set the action event handler for the 'up' button.
                upButton.setEnabled(false);
                upButton.setMargin(new Insets (0, 0, 0, 0));
                upButton.addActionListener(new ActionListener () {
                                                    public void actionPerformed(ActionEvent e){
                                                        int selectedIndex = sortFieldsList.getSelectedIndex();
                                                        if (selectedIndex > 0) {
                                                            // Remove the item from the list and insert it at the previous position.
                                                            Object item = sortListModel.remove(selectedIndex);
                                                            sortListModel.insertElementAt(item, selectedIndex - 1);

                                                            // Reselect an appropriate item in the list.
                                                            sortFieldsList.setSelectedIndex(selectedIndex - 1);
                                                        }
                                                    }
                                                  });

                // Set the action event handler for the 'down' button.
                downButton.setEnabled(false);
                downButton.setMargin(new Insets (0, 0, 0, 0));
                downButton.addActionListener(new ActionListener () {
                                                    public void actionPerformed(ActionEvent e){
                                                        int selectedIndex = sortFieldsList.getSelectedIndex();
                                                        if (selectedIndex >= 0 && selectedIndex < sortListModel.getSize() - 1) {
                                                            // Remove the item from the list and insert it at the next position.
                                                            Object item = sortListModel.remove(selectedIndex);
                                                            sortListModel.insertElementAt(item, selectedIndex + 1);

                                                            // Reselect an appropriate item in the list.
                                                            sortFieldsList.setSelectedIndex(selectedIndex + 1);
                                                        }
                                                    }
                                                  });

                // Make the source fields list box have default focus
                setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            
                    private static final long serialVersionUID = 5994841658123338234L;

                    public Component getDefaultComponent(Container c) {
                        return availableFieldsList;
                    }
                });
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getCardName()
             */
            String getCardName() {
                return SORTING_CARDNAME;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getNextCardName()
             */
            String getNextCardName() {
                return SqlQueryCard.SQLQUERY_CARDNAME;
            }

            /**
             * Returns the resource ID for the card title.
             */
            String getTitleResourceID() {
                return "JDBCGF_SortingPageTitle";
            }

            /**
             * Returns the resource ID for the card subtitle.
             */
            String getSubtitleResourceID() {
                return "JDBCGF_SortingPageSubTitle";
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#isPageContentValid()
             */
            boolean isPageContentValid() {
                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#commitChanges()
             */
            boolean commitChanges() {
                sortFields.clear();

                ListModel model = sortFieldsList.getModel();
                for (int itemN = 0, nItems = model.getSize(); itemN < nItems; ++itemN) {
                    SortField sortField = (SortField) model.getElementAt(itemN);
                    sortFields.add(sortField);
                }

                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#initializeControls()
             */
            void initializeControls() {
                super.initializeControls();

                DefaultListModel sourceModel = (DefaultListModel) availableFieldsList.getModel();
                DefaultListModel destModel = (DefaultListModel) sortFieldsList.getModel();

                // If the connection or table has been changed since the fields were fetched, rebuild the list.
                if (!connectionForFields.equals(connectionGemName)
                        || !tableForFields.equals(tableName)) {
                    sourceModel.clear();
                    destModel.clear();
                }

                // Populate the source fields list, if necessary.
                if (sourceModel.isEmpty()) {

                    // Add each available field.
                    List<FieldInfo> fieldsInfo = fetchFieldsInfo();
                    for (final FieldInfo fieldInfo : fieldsInfo) {
                        sourceModel.addElement(fieldInfo);  
                    }

                    connectionForFields = connectionGemName;
                    tableForFields = tableName;

                    // Select the first item in the available fields list, if any.
                    if (!sourceModel.isEmpty()) {
                        availableFieldsList.setSelectedIndex(0);
                    }
                }

                // Update the controls with the current values.
                destModel.clear();
                for (final SortField sortField : sortFields) {
                    destModel.addElement(sortField);
                }

                // Select the first item in the sort fields list, if any.
                if (!destModel.isEmpty()) {
                    sortFieldsList.setSelectedIndex(0);
                }
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getMainPanel()
             */
            JPanel getMainPanel() {

                JPanel javaPanel = new JPanel();

                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());

                JLabel availableLabel = new JLabel (GeneratorMessages.getString("JDBCGF_AvailableFieldsHeader"));
                JLabel selectedLabel = new JLabel (GeneratorMessages.getString("JDBCGF_SortFieldsHeader"));

                JScrollPane availableFieldsScrollPane = new JScrollPane(availableFieldsList);
                JScrollPane sortFieldsScrollPane = new JScrollPane(sortFieldsList);
                availableFieldsScrollPane.setPreferredSize(new Dimension(DEFAULT_LIST_WIDTH, DEFAULT_LIST_HEIGHT));
                sortFieldsScrollPane.setPreferredSize(new Dimension(DEFAULT_LIST_WIDTH, DEFAULT_LIST_HEIGHT));

                Box addRemoveBox = Box.createVerticalBox();
                addRemoveBox.add(addButton);
                addRemoveBox.add(removeButton);

                Box upDownBox = Box.createVerticalBox();
                upDownBox.add(upButton);
                upDownBox.add(downButton);

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                constraints.insets = new Insets(5, 5, 10, 5);
                javaPanel.add(statusLabel, constraints);
                statusLabel.setFont(getFont().deriveFont(Font.BOLD));
    
                constraints.gridx = 1;
                constraints.gridy = 1;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                javaPanel.add(availableLabel, constraints);
                constraints.gridx = 3;
                javaPanel.add(selectedLabel, constraints);

                constraints.gridx = 1;
                constraints.gridy = 2;
                constraints.weighty = 1;
                constraints.fill = GridBagConstraints.BOTH;
                javaPanel.add(availableFieldsScrollPane, constraints);
                constraints.gridx = 3;
                javaPanel.add(sortFieldsScrollPane, constraints);
                constraints.fill = GridBagConstraints.HORIZONTAL;

                constraints.gridx = 2;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.anchor = GridBagConstraints.CENTER;
                javaPanel.add(addRemoveBox, constraints);
                constraints.gridx = 4;
                javaPanel.add(upDownBox, constraints);
                constraints.anchor = GridBagConstraints.NORTHWEST;

                constraints.gridx = 3;
                constraints.gridy = 3;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                javaPanel.add(ascendingCheckBox, constraints);

//                constraints.gridx = 4;
//                constraints.weightx = 1;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                javaPanel.add(new JLabel(""), constraints);
    
                return javaPanel;
            }

            /**
             * Returns a list of the field names for the current table and connection.
             */
            private List<FieldInfo> fetchFieldsInfo() {
                // Fetch info about the available fields.
                // Exclude any long-type fields (blobs, clobs, etc...) since these cannot typically be sorted on.
                // TODO: return qualified field names if multiple tables are selected...
                // TODO: also fetch info about the max length, etc...
                // TODO: marshal the list of records directly instead of converting to a tuple once this is implemented for records...
                String code = CAL_Prelude.Functions.output.getQualifiedName() + " (" + CAL_List.Functions.map.getQualifiedName() + " (\\rec -> (rec.columnName, rec.valueType)) (" + CAL_List.Functions.filter.getQualifiedName() + " (\\rec -> " + CAL_Prelude.Functions.not.getQualifiedName() + " rec.isLongType) (" + CAL_DataGems.Functions.jdbcGetTableFieldInfo.getQualifiedName() + " " + connectionGemName + " \"" + tableName + "\")))";
                List<List<?>> tupleList = UnsafeCast.unsafeCast(getListFromCode(code));

                List<FieldInfo> fieldInfoList = new ArrayList<FieldInfo>();
                for (final List<?> tuple : tupleList) {
                    String fieldName = (String) tuple.get(0);
                    ValueType valueType = (ValueType) tuple.get(1);

                    fieldInfoList.add(new FieldInfo(fieldName, valueType));
                }

                return fieldInfoList;
            }

            /**
             * Add any selected fields from the available fields list to the sort fields list.
             */
            private void onAddFields(){
                final DefaultListModel availableListModel = (DefaultListModel) availableFieldsList.getModel();
                final DefaultListModel sortListModel = (DefaultListModel) sortFieldsList.getModel();

                Object[] selectedValues = availableFieldsList.getSelectedValues();
                int nSelected = selectedValues.length;

                for (int selectionN = 0; selectionN < nSelected; ++selectionN) {
                    FieldInfo selectedValue = (FieldInfo) selectedValues[selectionN];
                    SortField sortField = new SortField(selectedValue, true);

                    // Don't add the same field multiple times.
                    if (!sortListModel.contains(sortField)) {
                        sortListModel.addElement(sortField);
                    }
                }

                if (nSelected == 1) {
                    // Select the new item in the selected fields list.
                    sortFieldsList.setSelectedIndex(sortListModel.getSize() - 1);

                    // Move the selection to the next field in the available fields list.
                    int[] selectedIndexes = availableFieldsList.getSelectedIndices();
                    if (selectedIndexes.length == 1) {
                        int itemSelected = selectedIndexes[0]; // availableFieldsList.getSelectedIndex();
                        if (itemSelected >= 0 && itemSelected < (availableListModel.getSize() - 1)) {
                            availableFieldsList.setSelectedIndex(itemSelected + 1);
                        }
                    }
                }
            }

            /**
             * Removes any selected fields from the selected fields list.
             */
            public void onRemoveFields(){
                final DefaultListModel sortListModel = (DefaultListModel) sortFieldsList.getModel();

                int selectedIndex = sortFieldsList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    sortListModel.remove(selectedIndex);

                    // Reselect an appropriate item in the list.
                    int newSelection = selectedIndex;
                    if (newSelection >= sortListModel.getSize()) {
                        newSelection = sortListModel.getSize() - 1;
                    }
                    sortFieldsList.setSelectedIndex(newSelection);
                }
            }
        }

        /**
         * Panel to display UI for displaying the current SQL for the query.
         */
        private class SqlQueryCard extends WizardCard {
            private static final long serialVersionUID = 673118312331546643L;

            static final String SQLQUERY_CARDNAME = "SqlQuery";

            /** A text area for displaying the SQL query. */
            private final JTextArea sqlQueryEdit = new JTextArea();

            // TODO: add an 'Edit' button which switches to edit mode...

//            /** The label for displaying status messages. */
//            private final JLabel statusLabel = new JLabel();

            /**
             * PreviewCard constructor.
             */
            SqlQueryCard() {
                super(true);

                // The query should be read-only initially.
                sqlQueryEdit.setEditable(false);

                // Make the source fields list box have default focus
                setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            
                    private static final long serialVersionUID = 4202556912362889526L;

                    public Component getDefaultComponent(Container c) {
                        return sqlQueryEdit;
                    }
                });
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getCardName()
             */
            String getCardName() {
                return SQLQUERY_CARDNAME;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getNextCardName()
             */
            String getNextCardName() {
                return PreviewCard.PREVIEW_CARDNAME;
            }

            /**
             * Returns the resource ID for the card title.
             */
            String getTitleResourceID() {
                return "JDBCGF_SqlQueryPageTitle";
            }

            /**
             * Returns the resource ID for the card subtitle.
             */
            String getSubtitleResourceID() {
                return "JDBCGF_SqlQueryPageSubTitle";
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#isPageContentValid()
             */
            boolean isPageContentValid() {
                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#commitChanges()
             */
            boolean commitChanges() {
                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#initializeControls()
             */
            void initializeControls() {
                super.initializeControls();

                // Set the current SQL into the edit box.
                String sqlQuery = generateSqlQuery();
                sqlQueryEdit.setText(sqlQuery);
                sqlQueryEdit.setCaretPosition(0);   // Move the caret back to the top of the edit box.
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getMainPanel()
             */
            JPanel getMainPanel() {

                JPanel javaPanel = new JPanel();

                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;

//                constraints.gridx = 1;
//                constraints.weightx = 0;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                constraints.insets = new Insets(5, 5, 10, 5);
//                javaPanel.add(statusLabel, constraints);
//    
//                statusLabel.setFont(getFont().deriveFont(Font.BOLD));

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                constraints.insets = new Insets(5, 5, 5, 5);
                javaPanel.add(new JLabel(GeneratorMessages.getString("JDBCGF_SqlQueryLabel")), constraints);

                constraints.gridx = 1;
                constraints.weightx = 1;
                constraints.weighty = 1;        
                constraints.fill = GridBagConstraints.BOTH;
                JScrollPane scrollPane = new JScrollPane(sqlQueryEdit);
                javaPanel.add(scrollPane, constraints);

//                constraints.gridx = 4;
//                constraints.weightx = 1;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                javaPanel.add(new JLabel(""), constraints);

                return javaPanel;
            }
        }

        /**
         * Panel to display UI for viewing the results of the current query.
         */
        private class PreviewCard extends WizardCard {
            private static final long serialVersionUID = -1750021788640630651L;

            static final String PREVIEW_CARDNAME = "Preview";

            /** A panel which will hold the resultset editor. */
            private final JPanel editorPanel = new JPanel(new BorderLayout());

            /** The value editor provider for the resultset editor. */
            private final JDBCResultSetEditor.JDBCResultSetEditorProvider provider;

            /** The current value editor for displaying the resultset data. */
            private ValueEditor valueEditor;

            /**
             * PreviewCard constructor.
             */
            PreviewCard() {
                super(true);

                provider = new JDBCResultSetEditor.JDBCResultSetEditorProvider(valueEditorManager);
//                this.valueEditor = provider.getEditorInstance(valueEditorHierarchyManager, null);

//                // Make the source fields list box have default focus
//                setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
//            
//                    public Component getDefaultComponent(Container c) {
//                        return valueEditor;
//                    }
//    
//                    public Component getInitialComponent(Container c) {
//                        return valueEditor;
//                    }
//                });
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getCardName()
             */
            String getCardName() {
                return PREVIEW_CARDNAME;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getNextCardName()
             */
            String getNextCardName() {
                return null;
            }

            /**
             * Returns the resource ID for the card title.
             */
            String getTitleResourceID() {
                return "JDBCGF_PreviewPageTitle";
            }

            /**
             * Returns the resource ID for the card subtitle.
             */
            String getSubtitleResourceID() {
                return "JDBCGF_PreviewPageSubTitle";
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#isPageContentValid()
             */
            boolean isPageContentValid() {
                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#commitChanges()
             */
            boolean commitChanges() {
                return true;
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#initializeControls()
             */
            void initializeControls() {
                super.initializeControls();

                // Remove the previous value editor, if any.
                if (valueEditor != null) {
                    valueEditorHierarchyManager.removeValueEditor(valueEditor, true);
                    valueEditor = null;
                }
                editorPanel.removeAll();

                // Build the code necessary to execute the query.
                // TODO: what should happen if there are parameters in the code?
                String queryCode = generateResultsetFunctionBody();
                ValueNode vn = getValueNodeFromCode(queryCode);

                // If unable to run the query, display a message instead of the value editor.
                if (vn == null) {
                    // TODO: display the error message as well...
                    JTextArea messageText = new JTextArea(GeneratorMessages.getString("JDBCGF_PreviewErrorLabel"));
                    messageText.setEditable(false);
                    editorPanel.add(new JScrollPane(messageText), BorderLayout.CENTER);
                }
                else {
                    // Create a new value editor for displaying the resultset data.
                    valueEditor = provider.getEditorInstance(valueEditorHierarchyManager, vn);

                    // Add the editor as a top level panel.
                    valueEditorHierarchyManager.addTopValueEditor(valueEditor);

                    // Turn off the border on the editor.
                    valueEditor.setBorder(null);

                    // Set the value editor into the editor panel.
                    editorPanel.add(valueEditor, BorderLayout.CENTER);
                }
            }

            /**
             * @see org.openquark.gems.client.generators.JDBCGemGenerator.JDBCGemGeneratorDialog.WizardCard#getMainPanel()
             */
            JPanel getMainPanel() {

                JPanel javaPanel = new JPanel();

                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;

//                constraints.gridx = 1;
//                constraints.weightx = 0;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                constraints.insets = new Insets(5, 5, 10, 5);
//                javaPanel.add(statusLabel, constraints);
//    
//                statusLabel.setFont(getFont().deriveFont(Font.BOLD));

//                constraints.gridx = 1;
//                constraints.weightx = 0;
//                constraints.weighty = 0;
//                constraints.gridwidth = 1;
//                constraints.insets = new Insets(5, 5, 5, 5);
//                javaPanel.add(new JLabel(GeneratorMessages.getString("JDBCGF_PreviewLabel")), constraints);

                constraints.gridx = 1;
                constraints.weightx = 1;
                constraints.weighty = 1;        
                constraints.fill = GridBagConstraints.BOTH;
                javaPanel.add(editorPanel, constraints);

//                constraints.gridx = 4;
//                constraints.weightx = 1;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                javaPanel.add(new JLabel(""), constraints);

                return javaPanel;
            }
        }

        /**
         * @return the panel that contains the buttons at the bottom of the dialog
         */
        private JPanel getButtonPanel() {
        
            JPanel buttonPanel = new JPanel();
        
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(getPreviousButton());
            buttonPanel.add(Box.createHorizontalStrut(5));
            buttonPanel.add(getNextButton());

            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(getFinishButton());
            buttonPanel.add(Box.createHorizontalStrut(5));
            buttonPanel.add(getCancelButton());

            return buttonPanel;
        }

        /**
         * @return the Finish button for the dialog
         */
        private JButton getFinishButton() {
        
            if (finishButton == null) {
            
                Action finishAction = new AbstractAction(GeneratorMessages.getString("JDBCGF_FinishButton")) {
                    private static final long serialVersionUID = 2893908785764439019L;

                    public void actionPerformed(ActionEvent e) {
                        // Commit any changes from the current card.
                        WizardCard currentCard = getCurrentCard();
                        if (!currentCard.commitChanges()) {
                            return;
                        }

                        // Generate the source for the resultset gem.
                        generateSource();

                        // Generate a gem to extract a list of records from the resultset, if requested.
                        if (includeRecordExtractorGem) {
                            generateRecordExtractorSource();
                        }

                        dispose();
                    }
                };
            
                finishButton = new JButton(finishAction);
            }
        
            return finishButton;
        }
    
        /**
         * @return the cancel button for the dialog
         */
        private JButton getCancelButton() {
        
            if (cancelButton == null) {
            
                Action cancelAction = new AbstractAction(GeneratorMessages.getString("JGF_CancelButton")) {
                    private static final long serialVersionUID = -3095854626817672401L;

                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                };
            
                cancelButton = new JButton(cancelAction);
            }
        
            return cancelButton;
        }

        /**
         * @return the Previous button for the dialog
         */
        private JButton getPreviousButton() {
        
            if (previousButton == null) {
            
                Action previousAction = new AbstractAction(GeneratorMessages.getString("JDBCGF_PreviousButton")) {
                    private static final long serialVersionUID = -5893020996076600881L;

                    public void actionPerformed(ActionEvent e) {
                        switchToPreviousTab();
                    }
                };
            
                previousButton = new JButton(previousAction);
            }
        
            return previousButton;
        }

        /**
         * @return the Next button for the dialog
         */
        private JButton getNextButton() {
        
            if (nextButton == null) {
            
                Action nextAction = new AbstractAction(GeneratorMessages.getString("JDBCGF_NextButton")) {
                    private static final long serialVersionUID = 2323270174994712464L;

                    public void actionPerformed(ActionEvent e) {
                        switchToNextTab();
                    }
                };
            
                nextButton = new JButton(nextAction);
            }
        
            return nextButton;
        }

        /**
         * Returns the first page of the wizard.
         */
        private WizardCard getFirstCard() {
            // The current page will be at the bottom of the stack.
            if (viewedPageStack.isEmpty()) {
                return null;
            }
                
            return viewedPageStack.get(0);
        }

        /**
         * Returns the current wizard page being viewed.
         */
        private WizardCard getCurrentCard() {
            // The current page will be at the top of the stack.
            if (viewedPageStack.isEmpty()) {
                return null;
            }
                
            return viewedPageStack.peek();
        }

        /**
         * Sets up the wizard to display the first page.
         */
        private void showFirstPage(WizardCard firstPage) {
            viewedPageStack.push(firstPage);
//            firstPage.updateButtonBar();
            firstPage.initializeControls();
        }

        /**
         * Changes to the previous tab in the dialog.
         */
        private void switchToPreviousTab() {
            viewedPageStack.pop();

            // Initialize the UI for the new current card.
            WizardCard currentCard = getCurrentCard();
            currentCard.initializeControls();

            // Display the new current page.
            cardLayout.show(cardPanel, currentCard.getCardName());
        }

        /**
         * Changes to the next tab in the dialog.
         */
        private void switchToNextTab() {
            WizardCard currentCard = getCurrentCard();

            if (currentCard.isLastPage()) {
                return;
            }

            // Commit any changes from the current card.
            if (!currentCard.commitChanges()) {
                return;
            }

            // Determine the next card to activate.
            String nextCardName = currentCard.getNextCardName();
            WizardCard nextCard = nameToPageMap.get(nextCardName);
            if (nextCard == null) {
                return;
            }

            // Initialize the UI for the next card.
            nextCard.initializeControls();

            // Push this card onto the stack.
            viewedPageStack.push(nextCard);

            // Display the next card.
            cardLayout.show(cardPanel, nextCardName);
        }

        /**
         * Generates the source definitions for the Java import that the user has selected.
         */
        private void generateSource() {
            StringBuilder source = new StringBuilder(GeneratorMessages.getString("JDBCGF_FunctionDeclComment"));

            if (gemComment != null && gemComment.trim().length() > 0) {
                source.append("// " + gemComment + "\n");
            }

            // TODO: include a type signature...

            if (gemScope == Scope.PUBLIC) {
                source.append("public ");
            } else {
                source.append("private ");
            }

            source.append(gemName);

            // TODO: add arguments...

            source.append(" = \n");

            String functionBody = generateResultsetFunctionBody();
            source.append(functionBody);

            source.append(";\n");

            String gemSource = source.toString();

            // Finally add the main definition
            sourceDefinitions.put(gemName, gemSource);
        }

        /**
         * Generates the body of the function which creates the JDBC resultset.
         */
        private String generateResultsetFunctionBody() {
            return generateLetBlock(false) + CAL_DataGems.Functions.jdbcQueryToResultSet.getQualifiedName() + " conn sql";
        }

        /**
         * Generates the body of the function which creates the SQL query.
         */
        private String generateSqlFunctionBody() {
            return generateLetBlock(true) + "sql";
        }

        /**
         * Produces the body of the let block with values for 'conn' and 'sql'.
         * @param forDisplay  whether the SQL should be formatted for display or not
         * @return            the body of the let block
         */
        private String generateLetBlock(boolean forDisplay) {
            StringBuilder sb = new StringBuilder("    let\n");
            sb.append("        conn = ");
            sb.append(connectionGemName);
            sb.append(";\n\n");
            sb.append("        table = " + CAL_Sql.Functions.makeQueryTable.getQualifiedName() + " \"");
            sb.append(tableName);
            sb.append("\";\n");
            sb.append("        resultFields = [");

            for (int fieldN = 0, nFields = selectedFields.size(); fieldN < nFields; ++fieldN) {
                if (fieldN > 0) {
                    sb.append(", ");
                }

                FieldInfo fieldInfo = selectedFields.get(fieldN);
                sb.append(CAL_Sql.Functions.untypedField.getQualifiedName() + " table \"");
                sb.append(fieldInfo.fieldName);
                sb.append("\"");
            }

            sb.append("];\n");
            sb.append("        query1 = " + CAL_Sql.Functions.project.getQualifiedName() + " " + CAL_Sql.Functions.newQuery.getQualifiedName() + " resultFields;\n\n");
            sb.append("        orderFields = [");

            for (int sortFieldN = 0, nSortFields = sortFields.size(); sortFieldN < nSortFields; ++sortFieldN) {
                if (sortFieldN > 0) {
                    sb.append(", ");
                }

                SortField sortField = sortFields.get(sortFieldN);
                sb.append("(" + CAL_Sql.Functions.untypedField.getQualifiedName() + " table \"");
                sb.append(sortField.fieldInfo.fieldName);
                sb.append("\", ");
                sb.append(sortField.ascending ? CAL_Prelude.DataConstructors.True.getQualifiedName() : CAL_Prelude.DataConstructors.False.getQualifiedName());
                sb.append(')');
            }

            sb.append("];\n");
            sb.append("        query2 = " + CAL_Sql.Functions.order2.getQualifiedName() + " query1 orderFields;\n\n");
            sb.append("        sqlBuilder = ");
            sb.append(sqlBuilderGemName);
            sb.append(";\n");
            sb.append("        sql = " + CAL_Sql.Functions.queryText.getQualifiedName() + " sqlBuilder ");
            sb.append(forDisplay ? CAL_Prelude.DataConstructors.True.getQualifiedName() : CAL_Prelude.DataConstructors.False.getQualifiedName());
            sb.append(" query2;\n");
            sb.append("    in\n");
            sb.append("        ");
            return sb.toString();
        }

        /**
         * Generates the source definitions for a record extractor gem for the recordset.
         */
        private void generateRecordExtractorSource() {
            StringBuilder source = new StringBuilder(GeneratorMessages.getString("JDBCGF_FunctionDeclComment"));

            if (gemComment != null && gemComment.trim().length() > 0) {
                source.append("// " + gemComment + "\n");
            }

            // TODO: include a type signature...

            source.append(gemScope.toString()).append(' ');            

            // TODO: perhaps the name of this gem should be configurable...
            String extractorGemName = gemName + "Records";
            source.append(extractorGemName);

            // TODO: add arguments...

            source.append(" = \n");
            source.append("    let\n");
            source.append("        extractFn rs = \n");
            source.append("            let\n");

            for (int fieldN = 0, nFields = selectedFields.size(); fieldN < nFields; ++fieldN) {
            
                FieldInfo fieldInfo = selectedFields.get(fieldN);
                String extractorFunction = getFieldValueExtractorFunction(fieldInfo);

                source.append("                val");
                source.append(fieldN + 1);
                source.append(" = ");
                source.append(extractorFunction);
                source.append(" ");
                source.append(fieldN + 1);
                source.append(" rs;\n");
            }

            source.append("            in\n");
            source.append("                ");

            for (int fieldN = 0, nFields = selectedFields.size(); fieldN < nFields; ++fieldN) {
                if (fieldN > 0) {
                    source.append('(');
                }
                source.append(CAL_Prelude.Functions.seq.getQualifiedName() + " val");
                source.append(fieldN + 1);
                source.append(" ");
            }

            source.append("{ ");

            List<String> recordFieldNames = makeRecordFieldNames(selectedFields);
            for (int fieldN = 0, nFields = selectedFields.size(); fieldN < nFields; ++fieldN) {
                if (fieldN > 0) {
                    source.append(", ");
                }

                String recordFieldName = recordFieldNames.get(fieldN);

                source.append(recordFieldName);
                source.append(" = val");
                source.append(fieldN + 1);
            }
            source.append(" }");

            for (int fieldN = 0, nFields = selectedFields.size() - 1; fieldN < nFields; ++fieldN) {
                source.append(')');
            }
            source.append(";\n");

            source.append("    in\n");
            source.append("        " + CAL_DataGems.Functions.dataFromResultSet.getQualifiedName() + " ");
            source.append(gemName);
            source.append(" extractFn;\n");

            String gemSource = source.toString();

            // Finally add the main definition
            sourceDefinitions.put(extractorGemName, gemSource);
        }

        /**
         * Returns a list of the record field names corresponding to the database field names.
         * @param databaseFieldsInfo  a list of database field names
         * @return                    a list of record field names
         */
        private List<String> makeRecordFieldNames(List<FieldInfo> databaseFieldsInfo) {
            List<String> recordFieldNames = new ArrayList<String>();

            for (int fieldN = 0, nFields = databaseFieldsInfo.size(); fieldN < nFields; ++fieldN) {
                FieldInfo dbFieldInfo = databaseFieldsInfo.get(fieldN);

                // Create the base record name by removing any invalid chars, 
                //and ensuring that the first char is a lower case letter.
                String baseRecordFieldName = IdentifierUtils.makeIdentifierName(dbFieldInfo.fieldName);
                if (baseRecordFieldName == null || baseRecordFieldName.length() == 0) {
                    baseRecordFieldName = "field" + (fieldN + 1);
                }

                // Ensure that the name is unique within the record.
                // If necessary, append a number to the end of the base name to make it unique.
                String uniqueName = baseRecordFieldName;
                int counter = 1;
                while (recordFieldNames.contains(uniqueName)) {
                    uniqueName = baseRecordFieldName + counter;
                    ++counter;
                }

                recordFieldNames.add(uniqueName);
            }

            return recordFieldNames;
        }

        /**
         * Returns the name of the gem to use extract the value of the specified database field.
         * @param fieldInfo  information about the field
         * @return           the name of the gem to use extract the database field value
         */
        private String getFieldValueExtractorFunction(FieldInfo fieldInfo) {
            switch (fieldInfo.valueType.value()) {
            case ValueType._stringType :    return CAL_DataGems.Functions.extractString.getQualifiedName();
            case ValueType._intType :       return CAL_DataGems.Functions.extractInt.getQualifiedName();
            case ValueType._doubleType :    return CAL_DataGems.Functions.extractDouble.getQualifiedName();
            case ValueType._booleanType :   return CAL_DataGems.Functions.extractBoolean.getQualifiedName();
            case ValueType._timeType :      return CAL_DataGems.Functions.extractTime.getQualifiedName();

            case ValueType._nullType :
            default :
                // By default, just return a string value for the column.
                return CAL_DataGems.Functions.extractString.getQualifiedName();
            }
        }
        
        /**
         * Generates the SQL for the current query.
         */
        private String generateSqlQuery() {
            // Build the code necessary to execute the query.
            // TODO: what should happen if there are parameters in the code?
            String queryCode = generateSqlFunctionBody();
            ValueNode vn = getValueNodeFromCode(queryCode);

            Object sqlObj = vn.getValue();
            if (sqlObj instanceof String) {
                return (String) sqlObj;
            }
            else {
                return "<Failed to generate SQL query.>";
            }
        }

        /**
         * Returns the ValueNode corresponding to the specified value expression, or null if it is not valid.
         */
        private ValueNode getValueNodeFromCode(String valueText) {
            // Now create a target to be run by a value runner, and return the result.
            Target valueTarget = new Target.SimpleTarget(valueText);

            try {
                return valueRunner.getValue (valueTarget, perspective.getWorkingModuleName());
            } catch (Exception e) {
                e.printStackTrace ();
                return null;
            }
        }

        /**
         * Runs the specified code and returns the result as a Java Object.
         */
        private Object getObjectFromCode(String code) {
            ValueNode vn = getValueNodeFromCode(code);
            return (vn == null) ? null : vn.getValue();
        }

        /**
         * Runs the specified code and returns the list result.
         * Returns an empty list if the result is not a list.
         */
        private List<?> getListFromCode(String code) {
            Object obj = getObjectFromCode(code);
            return (obj instanceof List<?>) ? (List<?>) obj : Collections.EMPTY_LIST;
        }
    }
}
