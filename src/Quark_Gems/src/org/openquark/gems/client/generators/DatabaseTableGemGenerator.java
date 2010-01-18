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
 * DatabaseTableGemGenerator.java
 * Creation date: May 6, 2004
 * By: Richard Webster
 */
package org.openquark.gems.client.generators;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
import org.openquark.gems.client.ValueRunner;
import org.openquark.gems.client.valueentry.ValueEditorManager;
import org.openquark.util.UnsafeCast;
import org.openquark.util.datadictionary.ValueType;
import org.openquark.util.ui.WizardBase;
import org.openquark.util.ui.WizardCard;
import org.openquark.util.ui.WizardCardStack;


/**
 * A gem generator for creating strongly-typed database table records.
 * @author Richard Webster
 */
public class DatabaseTableGemGenerator implements GemGenerator {
    
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

        final DatabaseTableGemGeneratorDialog generatorUI = new DatabaseTableGemGeneratorDialog(parent, perspective, valueRunner);

        generatorUI.doModal();
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
        return GeneratorMessages.getString("DBTableGF_FactoryMenuName");
    }

    /**
     * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorTitle()
     */
    public String getGeneratorTitle() {
        return GeneratorMessages.getString("DBTableGF_FactoryTitle");
    }

    /**
     * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorIcon()
     */
    public Icon getGeneratorIcon() {
        return GENERATOR_ICON;
    }

    /**
     * This is the user interface class for the Database Tables gem generator.
     * @author Richard Webster
     */
    private static class DatabaseTableGemGeneratorDialog extends WizardBase {

        private static final long serialVersionUID = -9142879910177397417L;

        /** The perspective this UI is running in. */
        private final Perspective perspective;

        /** A value runner for running simple CAL programs. */
        private final ValueRunner valueRunner;

        /** This state is the information collected by the wizard. */
        private final TableWizardState wizardState = new TableWizardState();

        /**
         * Constructor for a new generator ui.
         * @param parent       the parent of the dialog
         * @param perspective  the perspective the UI should use
         * @param valueRunner  a value runner for executing CAL code
         */
        public DatabaseTableGemGeneratorDialog(JFrame parent,
                                    Perspective perspective,
                                    ValueRunner valueRunner) {
            super(parent, GeneratorMessages.getString("DBTableGF_CreateDbTableGemsTitle"));

            if (perspective == null || valueRunner == null) {
                throw new NullPointerException();
            }

            this.perspective = perspective;
            this.valueRunner = valueRunner;
        }

        /**
         * @see org.openquark.util.ui.WizardBase#makeCardStack()
         */
        @Override
        protected WizardCardStack makeCardStack () {
            return new DatabaseTableGemGeneratorCardStack();
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

        /**
         * A card stack for this wizard.
         */
        private class DatabaseTableGemGeneratorCardStack extends WizardCardStack {
            private static final long serialVersionUID = -8407335721996562142L;

            DatabaseTableGemGeneratorCardStack() {
                addCard(new GeneralInfoCard(wizardState));
                addCard (new SelectTablesCard(wizardState));
//                addCard (new CustomizeTableGemsCard(wizardState));

                finishInit ();
            }
        }

        /**
         * @return the new source definitions that should be created
         */
        public Map<String, String> getSourceDefinitions() {
            Map<String, String> sourceDefinitions = new LinkedHashMap<String, String>();
            Set<String> tableGemNamesUsed = new HashSet<String>();

            for (final String tableName : wizardState.tables) {
                // Determine the name of the gem for this table.
                // Make sure it is unique. 
                String tableGemName = makeTableGemName(tableName, tableGemNamesUsed);
                if (tableGemName == null || tableGemName.length() == 0) {
                    continue; 
                }
                tableGemNamesUsed.add(tableGemName);

                String tableGemSourceBody = buildTableGemSource(tableName);
                if (tableGemSourceBody == null || tableGemSourceBody.length() == 0) {
                    continue; 
                }

                StringBuilder source = new StringBuilder(GeneratorMessages.getString("DBTableGF_FunctionDeclComment"));

                if (wizardState.gemComment != null && wizardState.gemComment.trim().length() > 0) {
                    source.append("// " + wizardState.gemComment + "\n");
                }

                // TODO: include a type signature...

                source.append(wizardState.gemScope.toString()).append(' ');                

                source.append(tableGemName);

                // Add the table argument.
                source.append(" table");

                source.append(" = \n");
                source.append(tableGemSourceBody);
                source.append(";\n");

                sourceDefinitions.put(tableGemName, source.toString()); 
            }

            return sourceDefinitions;
        }

        /**
         * Generates a valid (and unique) gem name based on the table name.  
         * @param tableName          the name of the table
         * @param tableGemNamesUsed  the table gem names already used
         * @return                   a unique and valid name for the table gem
         */
        private String makeTableGemName(String tableName, Set<String> tableGemNamesUsed) {
            String baseRecordGemName = tableName + "TableFields";
            String validGemName = IdentifierUtils.makeIdentifierName(baseRecordGemName);

            // Ensure that the name is unique in the current module and is not already used
            // by one of the other table gems being generated.
            String uniqueGemName = validGemName;
            int counter = 0;
            while (tableGemNamesUsed.contains(uniqueGemName)
                    || perspective.getWorkingModuleTypeInfo().getFunctionalAgent(uniqueGemName) != null) {
                ++counter;
                uniqueGemName = validGemName + counter;
            }

            return uniqueGemName;
        }

        /**
         * Generates the source for the table gem body.
         * @param tableName  the name of the table
         * @return           the code for the body of the table gem
         */
        private String buildTableGemSource(String tableName) {
            // TODO: handle qualified table names...
            // TODO: marshal the list of records directly instead of converting to a tuple once this is implemented for records...
            String fieldInfoCode = CAL_Prelude.Functions.output.getQualifiedName() + " (" + CAL_List.Functions.map.getQualifiedName() + " (\\rec -> (rec.columnName, rec.valueType)) (" + CAL_DataGems.Functions.jdbcGetTableFieldInfo.getQualifiedName() + " " + wizardState.connectionGemName + " \"" + tableName + "\"))";
            List<List<?>> tupleList = UnsafeCast.unsafeCast(getListFromCode(fieldInfoCode));

            // Build the code for each field.
            List /*String[3]*/<String[]> fieldCodeItemsList = new ArrayList<String[]>();
            Set<String> recordFieldNamesUsed = new HashSet<String>();

            for (final List<?> tuple : tupleList) {
                String tableFieldName = (String) tuple.get(0);
                ValueType valueType = (ValueType) tuple.get(1);

                // Get the name of the field gem of the appropriate type.
                String makeFieldGemName = getMakeFieldGem(valueType);

                // Leave out any gems of unrecognized types.
                if (makeFieldGemName == null || makeFieldGemName.length() == 0) {
                    System.out.println("DatabaseTableGemGenerator:  skipping field '" + tableFieldName + "' of type " + valueType.toString() + " as this type is not currently supported.");
                    continue;
                }

                // Find a valid (and unique) name for the field with the record.
                String recordFieldName = makeRecordFieldName(tableFieldName, recordFieldNamesUsed);
                if (recordFieldName == null || recordFieldName.length() == 0) {
                    continue; 
                }
                recordFieldNamesUsed.add(recordFieldName);

                fieldCodeItemsList.add(new String[] { recordFieldName, makeFieldGemName, tableFieldName});
            }

            if (fieldCodeItemsList.isEmpty()) {
                return null;
            }

            // Find the longest record field name and the longest makeField gem name.
            int maxFieldNameLength = 0;
            int maxMakeFieldGemNameLength = 0;
            for (final String[] codeParts : fieldCodeItemsList) {
                int recordFieldNameLen = codeParts[0].length();
                if (recordFieldNameLen > maxFieldNameLength) {
                    maxFieldNameLength = recordFieldNameLen;
                }

                int makeFieldGemNameLen = codeParts[1].length();
                if (makeFieldGemNameLen > maxMakeFieldGemNameLength) {
                    maxMakeFieldGemNameLength = makeFieldGemNameLen;
                }
            }

            // Assemble the field code segments into the body of the gem.
            StringBuilder sb = new StringBuilder("    {\n");

            for (int fieldN = 0, nFields = fieldCodeItemsList.size(); fieldN < nFields; ++fieldN) {
                String[] codeParts = fieldCodeItemsList.get(fieldN);

                String recordFieldName = codeParts[0];
                String makeFieldGemName = codeParts[1];
                String databaseFieldName = codeParts[2]; 

                sb.append("      ");
                sb.append(recordFieldName);
                
                // Add some padding after the record field name so that the code lines up nicely.
                int recordFieldNameLen = recordFieldName.length();
                for (int i = recordFieldNameLen; i < maxFieldNameLength; ++i) {
                    sb.append(' ');
                }

                sb.append(" = ");
                sb.append(makeFieldGemName);

                // Add some padding after the make field gem name so that the code lines up nicely.
                int makeFieldGemNameLen = makeFieldGemName.length();
                for (int i = makeFieldGemNameLen; i < maxMakeFieldGemNameLength; ++i) {
                    sb.append(' ');
                }

                sb.append(" table \"");
                sb.append(databaseFieldName);
                sb.append('\"');

                if (fieldN < nFields - 1) {
                    sb.append(',');
                }
                sb.append('\n');
            }
            sb.append("    }");

            return sb.toString();
        }

        /**
         * Returns the name of the gem which will build a field expression of the specified value type.
         * @param valueType  the field value type
         * @return           the name of the gem which will build a field expression of the specified type, or null if there is none
         */
        private String getMakeFieldGem(ValueType valueType) {
            switch (valueType.value()) {
                case ValueType._stringType :  return CAL_Sql.Functions.stringField.getQualifiedName();
                case ValueType._intType :     return CAL_Sql.Functions.intField.getQualifiedName();
                case ValueType._doubleType :  return CAL_Sql.Functions.doubleField.getQualifiedName();
                case ValueType._booleanType : return CAL_Sql.Functions.booleanField.getQualifiedName();
                case ValueType._timeType :    return CAL_Sql.Functions.timeField.getQualifiedName();
                case ValueType._binaryType :  return CAL_Sql.Functions.binaryField.getQualifiedName();

                case ValueType._nullType :
                default :
                    return null;
            }
        }

        /**
         * Generates a valid (and unique) record field name based on the table field name.  
         * @param tableFieldName        the name of the field in the table
         * @param recordFieldNamesUsed  the record field names already used
         * @return                      a unique and valid name for the record field
         */
        private String makeRecordFieldName(String tableFieldName, Set<String> recordFieldNamesUsed) {
            String validFieldName = IdentifierUtils.makeIdentifierName(tableFieldName);

            // Ensure that the name is not already used by one of the other record fields.
            String uniqueFieldName = validFieldName;
            int counter = 0;
            while (recordFieldNamesUsed.contains(uniqueFieldName)) {
                ++counter;
                uniqueFieldName = validFieldName + counter;
            }

            return uniqueFieldName;
        }

        /**
         * A class to hold the state of the dialog.
         */
        private static class TableWizardState {
            private String gemComment = "";
            private Scope gemScope = Scope.PUBLIC;
            private String connectionGemName = "";
            private final List <String> tables = new ArrayList<String>();
        }

        /**
         * A wizard card for entering general info about the table gems.
         */
        private class GeneralInfoCard extends WizardCard {
            private static final long serialVersionUID = -5973608436858769130L;

            /** The name of this card. */
            static final String CARD_NAME = "GeneralInfo";

            /** The wizard state to be used for this card. */
            private final TableWizardState wizardState;

//            /** The text field for entering the name of the new gem. */
//            private final JTextField gemNameField = new JTextField();
    
            /** The text field for entering the comment for the new gem. */
            private final JTextField commentField = new JTextField();

            /** The radio button for selecting private scope. */
            private final JRadioButton privateButton = new JRadioButton(GeneratorMessages.getString("PrivateLabel"));
    
            /** The radio button for selecting public scope. */
            private final JRadioButton publicButton = new JRadioButton(GeneratorMessages.getString("PublicLabel"));

            /** The button group for the radio buttons. */
            private final ButtonGroup buttonGroup = new ButtonGroup();

            /** The combobox for selecting a connection gem. */
            private final JComboBox connectionGemCombo = new JComboBox(new DefaultComboBoxModel());

            /**
             * Constructor for GeneralInfoCard.
             */
            GeneralInfoCard(TableWizardState wizardState) {
                this.wizardState = wizardState;

                buttonGroup.add(publicButton);
                buttonGroup.add(privateButton);
                buttonGroup.setSelected(publicButton.getModel(), true);
                
                connectionGemCombo.addActionListener(new ActionListener () {
                        public void actionPerformed(ActionEvent e) {
                            cardStateChanged();
                        }
                    });
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getTitle()
             */
            @Override
            protected String getTitle() {
                return GeneratorMessages.getString("DBTableGF_GeneralInfoCardTitle");
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getSubtitle()
             */
            @Override
            protected String getSubtitle() {
                return GeneratorMessages.getString("DBTableGF_GeneralInfoCardSubtitle");
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getCardName()
             */
            @Override
            public String getCardName() {
                return CARD_NAME;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getNextCardName()
             */
            @Override
            protected String getNextCardName () {
                return SelectTablesCard.CARD_NAME;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getMainPanel()
             */
            @Override
            protected JComponent getMainPanel () {
                JPanel javaPanel = new JPanel();

                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(5, 5, 5, 5);

//                constraints.gridx = 1;
//                constraints.weightx = 0;
//                constraints.weighty = 0;
//                constraints.gridwidth = 1;
//                javaPanel.add(new JLabel(GemCutter.getResourceString("JGF_GemNameHeader")), constraints);
//
//                constraints.gridx = 2;
//                constraints.weightx = 1;
//                constraints.weighty = 0;        
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                gemNameField.setColumns(20);
//                javaPanel.add(gemNameField, constraints);

                constraints.gridx = 1;
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                javaPanel.add(new JLabel(GeneratorMessages.getString("JGF_CommentHeader")), constraints);

                constraints.gridx = 2;
                constraints.weightx = 1;
                constraints.weighty = 0;        
                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                commentField.setColumns(20);
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
                constraints.weightx = 0;
                constraints.weighty = 0;
                constraints.gridwidth = 1;
                javaPanel.add(new JLabel(GeneratorMessages.getString("JDBCGF_ConnectionGemNameHeader")), constraints);

                constraints.gridx = 2;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                javaPanel.add(connectionGemCombo, constraints);

                constraints.gridx = 4;
                constraints.weightx = 1;
                constraints.weighty = 0;
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                javaPanel.add(new JLabel(""), constraints);

                return javaPanel;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#initControls()
             */
            @Override
            protected boolean initControls () {
                // Populate the connection gems list, if necessary.
                if (connectionGemCombo.getItemCount() == 0) {
//                    // Add a special option to create a new connection.
//                    connectionGemCombo.addItem("<new>");

                    // Add each available connection gem.
                    List<String> connectionGems = fetchConnectionGemNames();
                    for (final String string : connectionGems) {
                        connectionGemCombo.addItem(string);  
                    }
                }

                // Update the controls with the current values.
//                gemNameField.setText(gemName);
                commentField.setText(wizardState.gemComment);

                if (wizardState.gemScope == Scope.PUBLIC) {
                    publicButton.setSelected(true);
                } else {
                    privateButton.setSelected(true);
                }

                connectionGemCombo.setSelectedItem(wizardState.connectionGemName);

                return true;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#commitChanges()
             */
            @Override
            protected boolean commitChanges () {
//                gemName = gemNameField.getText();
                wizardState.gemComment = commentField.getText();
                wizardState.gemScope = publicButton.getModel().isSelected() ? Scope.PUBLIC : Scope.PRIVATE;
                wizardState.connectionGemName = (String) connectionGemCombo.getSelectedItem();

                return true;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#canFinish()
             */
            @Override
            protected boolean canFinish () {
                return false;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getTipInfo()
             */
            @Override
            protected TipInfo getTipInfo () {
                // Check whether a connection gem has been specified.
                String connectionGemName = (String) connectionGemCombo.getSelectedItem();
                if (connectionGemName == null || connectionGemName.length() == 0) {
                    return new TipInfo (WARNING_TIP, GeneratorMessages.getString("DBTableGF_Tip_SelectDatabaseConnection"));
                }

                return new TipInfo (INFO_TIP, GeneratorMessages.getString("DBTableGF_Tip_NextToSelectTables"));
            }

            /**
             * @see org.openquark.util.ui.WizardCard#onFinish()
             */
            @Override
            protected boolean onFinish () {
                return false;
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
             * Returns a list of the names of the gems which have the specified return type and no inputs.
             */
            private List <String> fetchGemsOfType(final TypeExpr returnTypeExpr) {
                GemViewer gemViewer = new GemViewer();
                
                final ModuleTypeInfo moduleTypeInfo = perspective.getWorkingModuleTypeInfo();

                // Create a filter which will find all gems which return the specified type
                // and take no inputs.
                // TODO: add support for gems which do take inputs...
                GemFilter filter = new GemFilter() {
                    @Override
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
         * A wizard card for selecting one or more database tables.
         */
        private class SelectTablesCard extends WizardCard {
            private static final long serialVersionUID = 5923107637829194229L;

            /** The name of this card. */
            static final String CARD_NAME = "SelectTables";

            /** The wizard state to be used for this card. */
            private final TableWizardState wizardState;

            /** The connection gem used to fetch the list of tables currently displayed. */
            private String connectionForTables = "";

            /** The list for selecting tables. */
            private final JList tableList = new JList(new DefaultListModel());

            /** A button to select all the tables. */
            private final JButton selectAllButton = new JButton(GeneratorMessages.getString("DBTableGF_SelectAllCaption"));

            /**
             * Constructor for SelectTablesCard.
             */
            SelectTablesCard(TableWizardState wizardState) {
                this.wizardState = wizardState;

                // Allow multiple selection in the list.
                tableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                tableList.addListSelectionListener(new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent e) {
                            cardStateChanged();
                        }
                    });

                selectAllButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            int nTablesAvailable = tableList.getModel().getSize();
                            int[] selectionIndices = new int[nTablesAvailable];
                            for (int tableN = 0; tableN < nTablesAvailable; ++tableN) {
                                selectionIndices[tableN] = tableN;
                            }
                            tableList.setSelectedIndices(selectionIndices);
                        }
                    });
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getTitle()
             */
            @Override
            protected String getTitle() {
                return GeneratorMessages.getString("DBTableGF_SelectTablesCardTitle");
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getSubtitle()
             */
            @Override
            protected String getSubtitle() {
                return GeneratorMessages.getString("DBTableGF_SelectTablesCardSubtitle");
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getCardName()
             */
            @Override
            public String getCardName() {
                return CARD_NAME;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getNextCardName()
             */
            @Override
            protected String getNextCardName () {
                return null;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getMainPanel()
             */
            @Override
            protected JComponent getMainPanel () {
                JPanel javaPanel = new JPanel();

                javaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                javaPanel.setLayout(new GridBagLayout());

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.NORTHWEST;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                constraints.insets = new Insets(5, 5, 5, 5);

//                constraints.gridx = 1;
//                constraints.weightx = 0;
//                constraints.weighty = 0;
//                constraints.gridwidth = 1;
//                javaPanel.add(new JLabel(GemCutter.getResourceString("JGF_GemNameHeader")), constraints);
//
//                constraints.gridx = 2;
//                constraints.weightx = 1;
//                constraints.weighty = 0;        
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                gemNameField.setColumns(20);
//                javaPanel.add(gemNameField, constraints);

//                constraints.gridy = 0;
//                constraints.weightx = 0;
//                constraints.weighty = 0;
//                constraints.gridwidth = 1;
//                javaPanel.add(new JLabel(GemCutter.getResourceString("DBTableGF_TablesHeader")), constraints);

                constraints.gridy = 0;
                constraints.weightx = 1;
                constraints.weighty = 1;        
                constraints.fill = GridBagConstraints.BOTH;
                javaPanel.add(new JScrollPane(tableList), constraints);

//                constraints.gridx = 4;
//                constraints.weightx = 1;
//                constraints.weighty = 0;
//                constraints.gridwidth = GridBagConstraints.REMAINDER;
//                javaPanel.add(new JLabel(""), constraints);

                constraints.gridy = 1;
                constraints.weightx = 1;
                constraints.weighty = 0;        
                constraints.fill = GridBagConstraints.NONE;
                javaPanel.add(selectAllButton, constraints);

                return javaPanel;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#initControls()
             */
            @Override
            protected boolean initControls () {
                // If the connection has been changed since the tables were fetched, rebuild the list.
                DefaultListModel tableListModel = (DefaultListModel) tableList.getModel();
                if (!connectionForTables.equals(wizardState.connectionGemName)) {
                    tableListModel.removeAllElements();
                    wizardState.tables.clear();
                }

                // Populate the table list, if necessary.
                if (tableListModel.isEmpty()) {

                    // Add each available table.
                    List<String> tableNames = fetchTableNames();
                    for (final String tableName : tableNames) {
                        tableListModel.addElement(tableName);  
                    }

                    connectionForTables = wizardState.connectionGemName;
                }

                // Update the controls with the current values.
                List<Integer> selectionIndexList = new ArrayList<Integer>();

                for (final String table : wizardState.tables) {
                    int listIndex = tableListModel.indexOf(table);
                    if (listIndex >= 0) {
                        selectionIndexList.add(Integer.valueOf(listIndex));
                    }
                }

                int[] selectionIndices = new int[selectionIndexList.size()];
                for (int indexN = 0, nIndices = selectionIndexList.size(); indexN < nIndices; ++indexN) {
                    int selectionIndex = selectionIndexList.get(indexN).intValue();
                    selectionIndices[indexN] = selectionIndex;
                }
                tableList.setSelectedIndices(selectionIndices);


                return true;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#commitChanges()
             */
            @Override
            protected boolean commitChanges () {
                wizardState.tables.clear();
                Object[] selectedValues = tableList.getSelectedValues();
                for (int valueN = 0, nValues = selectedValues.length; valueN < nValues; ++valueN) {
                    wizardState.tables.add((String)selectedValues[valueN]);
                }

                return true;
            }

            /**
             * @see org.openquark.util.ui.WizardCard#canFinish()
             */
            @Override
            protected boolean canFinish () {
                return (tableList.getSelectedValue() != null);
            }

            /**
             * @see org.openquark.util.ui.WizardCard#getTipInfo()
             */
            @Override
            protected TipInfo getTipInfo () {
                // Check whether any tables has been specified.
                if (tableList.getSelectedValue() == null) {
                    return new TipInfo (WARNING_TIP, GeneratorMessages.getString("DBTableGF_Tip_SelectTables"));
                }
                return new TipInfo (ALLOK_TIP, GeneratorMessages.getString("DBTableGF_Tip_FinishToGenerateGems"));
            }

            /**
             * @see org.openquark.util.ui.WizardCard#onFinish()
             */
            @Override
            protected boolean onFinish () {
                return commitChanges();
            }

            /**
             * Returns a list of the table names for the current connection.
             */
            private List <String> fetchTableNames() {
                // TODO: return qualified table names...
                String code = CAL_Prelude.Functions.output.getQualifiedName() + " (" + CAL_DataGems.Functions.jdbcGetConnectionTableNames.getQualifiedName() + " " + wizardState.connectionGemName + ")";
                return UnsafeCast.unsafeCast(getListFromCode(code));
            }
        }


        // TODO: other cards...

    }
}
