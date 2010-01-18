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
 * JavaGemGenerator.java
 * Creation date: Oct 8, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.generators;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openquark.cal.compiler.LanguageInfo;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.TypeChecker;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.compiler.SourceModel.ModuleDefn;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.GemCutter;
import org.openquark.gems.client.ValueRunner;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;
import org.openquark.gems.client.valueentry.ValueEditorManager;


/**
 * This is the container class for the Java gem factories to import Java types.
 * @author Frank Worsley
 */
public final class JavaGemGenerator {
    /** The icon used by the generator. */
    private static final Icon GENERATOR_ICON = new ImageIcon(GemGenerator.class.getResource("/Resources/supercombinator.gif"));
    
    private JavaGemGenerator() {}

    /**
     * This is the Java generator that imports Java methods, constructors and fields as foreign functions.
     * @author Frank Worsley
     */
    public static class JavaFunctionGenerator implements GemGenerator {

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
    
            final JavaGemGeneratorDialog generatorUI = new JavaGemGeneratorDialog(parent, perspective, false);
    
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
            return GeneratorMessages.getString("JGF_MethodImportMenuName");
        }
        
        /**
         * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorTitle()
         */
        public String getGeneratorTitle() {
            return GeneratorMessages.getString("JGF_MethodImportTitle");
        }
    
        /**
         * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorIcon()
         */
        public Icon getGeneratorIcon() {
            return GENERATOR_ICON;
        }
    }
    
    /**
     * This is the Java generator that imports Java classes as foreign data types.
     * @author Frank Worsley
     */
    public static class JavaDataTypeGenerator implements GemGenerator {

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
    
            final JavaGemGeneratorDialog generatorUI = new JavaGemGeneratorDialog(parent, perspective, true);
    
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
            return GeneratorMessages.getString("JGF_DataTypeImportName");
        }
        
        /**
         * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorTitle()
         */
        public String getGeneratorTitle() {
            return GeneratorMessages.getString("JGF_MethodImportTitle");
        }
    
        /**
         * @see org.openquark.gems.client.generators.GemGenerator#getGeneratorIcon()
         */
        public Icon getGeneratorIcon() {
            return GENERATOR_ICON;
        }
    }
}

/**
 * This is the user interface class for either of the Java factories.
 * @author Frank Worsley
 */
class JavaGemGeneratorDialog extends JDialog {
    private static final long serialVersionUID = 7978474516115445555L;

    /** The icon to use for error messages. */
    private static final Icon ERROR_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/error.gif"));

    /** The icon to use for warning messages. */
    private static final Icon WARNING_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/warning.gif"));
    
    /** The icon to use if everything is ok. */
    private static final Icon OK_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/checkmark.gif"));

    /** The perspective this UI is running in. */
    private final Perspective perspective;
    
    /**
     * Map from source name to source code. 
     * The list of source definitions we want to create. Ordered by insertion order, so that
     * definitions we want to create first will be created first.
     */
    private final Map<String, String> sourceDefinitions = new LinkedHashMap<String, String>();
    
    /**
     * Map of Java class to new CAL data declaration name
     * that we need to add to import the foreign function.
     */ 
    private final Map<Class<?>, QualifiedName> newDataTypes = new HashMap<Class<?>, QualifiedName>();

    /** A specialized JList for displaying the members of a Java class. */
    private final JavaMemberList memberList = new JavaMemberList();

    /** The text field for entering the name of the new gem. */
    private final JTextField gemNameField = new JTextField();
    
    /** The text field for entering the comment for the new gem. */
    private final JTextField commentField = new JTextField();
    
    /** The text field for entering the class name. */
    private final JComboBox classNameCombo = new JComboBox();    

    /** The radio button for selecting private scope. */
    private final JRadioButton privateButton = new JRadioButton(GeneratorMessages.getString("PrivateLabel"));
    
    /** The radio button for selecting public scope. */
    private final JRadioButton publicButton = new JRadioButton(GeneratorMessages.getString("PublicLabel"));

    /** The button group for the radio buttons. */
    private final ButtonGroup buttonGroup = new ButtonGroup();

    /** The label for displaying status messages. */
    private final JLabel statusLabel = new JLabel();

    /** Whether or not we are adding a data type. */
    private final boolean addingDataType;
    
    /** The OK button for the dialog. */
    private JButton okButton = null;
    
    /** The cancel button for the dialog. */
    private JButton cancelButton = null;
    
    /** Whether or not the user has typed into the name field. */
    private boolean userHasTyped = false;
    
    /** The last class name we used to populate the combo. */
    private String lastClassName = null;

    /**
     * Constructor for a new generator ui.
     * @param parent the parent of the dialog
     * @param perspective the perspective the UI should use
     * @param addingDataType whether to display the UI for adding a data type
     */
    public JavaGemGeneratorDialog(JFrame parent, Perspective perspective, boolean addingDataType) {
        super(parent, true);
        
        if (perspective == null) {
            throw new NullPointerException();
        }
        
        this.perspective = perspective;
        this.addingDataType = addingDataType;

        String titleId = addingDataType ? "JGF_DataTypeImportTitle" : "JGF_MethodImportTitle";
        setTitle(GeneratorMessages.getString(titleId));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getTitlePanel(), BorderLayout.NORTH);
        getContentPane().add(getMainPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);

        getRootPane().setDefaultButton(getOkButton());

        buttonGroup.add(publicButton);
        buttonGroup.add(privateButton);
        buttonGroup.setSelected(publicButton.getModel(), true);
        
        memberList.setOnlyShowConstructors(addingDataType);

        updateState();

        // Make the class name field have default focus
        setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            
            private static final long serialVersionUID = 3866828721074107359L;

            public Component getDefaultComponent(Container c) {
                return classNameCombo;
            }
        });
        
        // Add listeners to update the error message if things change
        gemNameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                userHasTyped = true;
                updateState();
            }
        });
        
        memberList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateState();
            }
        });
        
        // Let the user double click on the list to select an import
        memberList.addMouseListener(new MouseClickDragAdapter() {
            
            public boolean mouseReallyClicked(MouseEvent e) {
                
                boolean doubleClicked = super.mouseReallyClicked(e);
                
                if (doubleClicked && SwingUtilities.isLeftMouseButton(e) && okButton.isEnabled()) {
                    okButton.doClick();
                }
                
                return doubleClicked;
            }
        });
        
        KeyListener dismissKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        };

        // Cancel the dialog if the user presses ESC
        addKeyListener(dismissKeyListener);
        okButton.addKeyListener(dismissKeyListener);
        cancelButton.addKeyListener(dismissKeyListener);
        privateButton.addKeyListener(dismissKeyListener);
        publicButton.addKeyListener(dismissKeyListener);
        gemNameField.addKeyListener(dismissKeyListener);
        commentField.addKeyListener(dismissKeyListener);
        memberList.addKeyListener(dismissKeyListener);

        pack();
        setResizable(false);
        setSize(500, getSize().height);
        
        // position in the center of the parent window        
        int x = parent.getX() + parent.getWidth() / 2 - getWidth() / 2;
        int y = parent.getY() + parent.getHeight() / 2 - getHeight() / 2;
        setLocation(Math.max(parent.getX(), x), Math.max(parent.getY(), y));
    }
    
    /**
     * @return the new source definitions that should be created
     */
    public Map<String, String> getSourceDefinitions() {
        return sourceDefinitions;
    }

    /**
     * Updates the state of the Ok button to only be enabled if the user has entered
     * all required information. Also updates the information message displayed.
     */
    private void updateState() {

        // Make sure that you check for errors first, then for warnings.
        // Be careful about the order of checks, more important checks come first.
        
        // Note that the gem name check is more important than the class name check.
        // This is so the default gem name is set to nothing if an invalid class name is entered.

        if (!checkForValidGemName()) {
            return;
        }

        // check if a valid class name is entered
        if (memberList.getCurrentClass() == null) {
            
            String message = null;
            
            if (addingDataType) {
                message = GeneratorMessages.getString("JGF_InvalidClassNameForType");
            } else {
                message = GeneratorMessages.getString("JGF_InvalidClassName");
            }
            
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return;
        }            

        // check if a class member is selected
        Object selectedValue = memberList.getSelectedValue();

        if (selectedValue == null && !addingDataType) {

            String message = GeneratorMessages.getString("JGF_NoMemberSelected");
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return;
        }

        // check if a gem or data type with the given name already exists
        String gemName = gemNameField.getText();
        
        if (addingDataType && perspective.getWorkingModuleTypeInfo().getTypeConstructor(gemName) != null) {
            
            String message = GeneratorMessages.getString("JGF_DataTypeExists");
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return;
            
        } else if (!addingDataType && perspective.getWorkingModuleTypeInfo().getFunction(gemName) != null) {
            
            String message = GeneratorMessages.getString("JGF_GemExists");
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(WARNING_ICON);
            okButton.setEnabled(true);
            return;
        }

        // check if the selected data type is already imported under a different name
        QualifiedName existingName;
        try {
            existingName = getExistingTypeName(memberList.getCurrentClass());
        } catch (UnableToResolveForeignEntityException e) {
            
            String message = GeneratorMessages.getString("JGF_UnableToResolveForeignEntity", e.getCompilerMessage().getMessage());
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(ERROR_ICON);
            okButton.setEnabled(false);
            return;
        }
        
        if (addingDataType && existingName != null) {
            
            String message = GeneratorMessages.getString("JGF_DataTypeAlreadyImported", existingName.getQualifiedName());
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(WARNING_ICON);
            okButton.setEnabled(true);
            return;
        }
        
        // check if the user has selected a constructor
        if (addingDataType && selectedValue == null) {
            
            String message = null;
            
            if (memberList.getModel().getSize() == 0) {
                message = GeneratorMessages.getString("JGF_NoConstructors");
            } else {
                message = GeneratorMessages.getString("JGF_NoConstructorSelected");
            }
            
            statusLabel.setText(message);
            statusLabel.setToolTipText(message);
            statusLabel.setIcon(WARNING_ICON);
            okButton.setEnabled(true);
            return;
        }        

        // everything is fine, so enable the button
        String message = GeneratorMessages.getString(addingDataType ? "JGF_OkImportType" : "JGF_OkImportMember");
        statusLabel.setText(message);
        statusLabel.setToolTipText(message);
        statusLabel.setIcon(OK_ICON);
        okButton.setEnabled(true);
    }
    
    /**
     * Checks the vailidity of the name in the gem name field and suggests a default
     * name is no name has been entered.
     * For data types the suggested name is the class name with a 'J' prepended.
     * For methods/fields the suggested name is the method/field name with a 'j' prepended.
     * For constructors the suggested name is the class name with 'jMake' prepended.
     * @return true if the provided name is valid, false otherwise
     */
    private boolean checkForValidGemName() {

        String gemName = gemNameField.getText();
        
        if (!userHasTyped || (gemName.length() == 0 && !gemNameField.isFocusOwner())) {

            // Provide a default name for the user if he hasn't typed a name himself.
            
            String newName = null;
            
            if (addingDataType && memberList.getCurrentClass() != null) {
                Class<?> currentClass = memberList.getCurrentClass();
                newName = getUniqueTypeName(currentClass).getUnqualifiedName();
            
            } else if (!addingDataType && memberList.getCurrentClass() != null && memberList.getSelectedValue() != null) {
                
                // Get the name of the class the member belongs to.
                String className = getUniqueTypeName(memberList.getCurrentClass()).getUnqualifiedName();
                
                Object value = memberList.getSelectedValue();
                
                if (value instanceof Constructor<?>) {
                    newName = "make" + className;
                    
                } else if (value instanceof Method) {
                    newName = "j" + className.substring(1) + "_" + ((Method) value).getName();
                    
                } else if (value instanceof Field) {
                    newName = "j" + className.substring(1) + "_" + ((Field) value).getName();
                
                } else {
                    throw new IllegalStateException("invalid item in the member list");
                }
            }

            // Update the field with the suggested name
            gemNameField.setText(newName);                
        
        } else {
        
            // If the user has typed a name, check if for validity.
                    
            String messageId = null;
                    
            if (addingDataType && !LanguageInfo.isValidTypeConstructorName(gemName)) {
                messageId = "JGF_InvalidDataTypeName";
            } else if (!addingDataType && !LanguageInfo.isValidFunctionName(gemName)) {
                messageId = "JGF_InvalidGemName";
            }
            
            if (messageId != null) {
                statusLabel.setText(GeneratorMessages.getString(messageId));
                statusLabel.setIcon(ERROR_ICON);
                okButton.setEnabled(false);
                return false;
            }                
        }
        
        return true;
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
        
        String titleId = addingDataType ? "JGF_DataTypeImportTitle" : "JGF_MethodImportTitle";
        JLabel titleLabel = new JLabel(GeneratorMessages.getString(titleId));
        titleLabel.setFont(getFont().deriveFont(Font.BOLD, getFont().getSize() + 2));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        String subTitleId = addingDataType ? "JGF_DataTypeImportSubTitle" : "JGF_MethodImportSubTitle";
        JLabel subTitleLabel = new JLabel(GeneratorMessages.getString(subTitleId));
        titlePanel.add(subTitleLabel, BorderLayout.SOUTH);
        
        return titlePanel;
    }
    
    /**
     * @return the main panel that shows the contents of the dialog
     */
    private JPanel getMainPanel() {
        
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
        javaPanel.add(new JLabel(GeneratorMessages.getString("JGF_ClassNameHeader")), constraints);
        
        constraints.gridx = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        javaPanel.add(getClassNameCombo(), constraints);
        
        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        javaPanel.add(new JLabel(GeneratorMessages.getString("JGF_MembersHeader")), constraints);
        
        JScrollPane listScrollPane = new JScrollPane(memberList);
        listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        constraints.gridx = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;        
        javaPanel.add(listScrollPane, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        javaPanel.add(new JLabel(GeneratorMessages.getString(addingDataType ? "JGF_TypeNameHeader" : "JGF_GemNameHeader")), constraints);
        
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
        
        constraints.gridx = 4;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        javaPanel.add(new JLabel(""), constraints);
        
        return javaPanel;
    }
    
    /**
     * @return the panel that contains the buttons at the bottom of the dialog
     */
    private JPanel getButtonPanel() {
        
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(getOkButton());
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(getCancelButton());
        
        return buttonPanel;
    }
    
    /**
     * @return the OK button for the dialog
     */
    private JButton getOkButton() {
        
        if (okButton == null) {
            
            Action okAction = new AbstractAction(GeneratorMessages.getString("JGF_OkButton")) {
                private static final long serialVersionUID = 8780497621064785865L;

                public void actionPerformed(ActionEvent e) {
                    try {
                        generateSource();
                    } catch (UnableToResolveForeignEntityException ex) {
                        JOptionPane.showMessageDialog(
                            JavaGemGeneratorDialog.this,
                            GeneratorMessages.getString("JGF_UnableToResolveForeignEntity", ex.getCompilerMessage().getMessage()),
                            GeneratorMessages.getString("JGF_ErrorDialogTitle"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                    dispose();
                }
            };
            
            okButton = new JButton(okAction);
            okButton.setPreferredSize(getCancelButton().getPreferredSize());
        }
        
        return okButton;
    }
    
    /**
     * @return the cancel button for the dialog
     */
    private JButton getCancelButton() {
        
        if (cancelButton == null) {
            
            Action cancelAction = new AbstractAction(GeneratorMessages.getString("JGF_CancelButton")) {
                private static final long serialVersionUID = 1344989545249460016L;

                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            };
            
            cancelButton = new JButton(cancelAction);
        }
        
        return cancelButton;
    }
    
    /**
     * @return the text field for entering the Java class name.
     */
    private JComboBox getClassNameCombo() {
        
        final Timer classComboTimer = new Timer(300, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateClassNameCombo();
                updateState();
            }
        });
        
        classComboTimer.setRepeats(false);

        classNameCombo.setEditable(true);
        classNameCombo.setModel(new DefaultComboBoxModel());

        classNameCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            
            public void keyReleased(KeyEvent e) {

                int keyCode = e.getKeyCode();

                if (e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && 
                    keyCode != KeyEvent.VK_ENTER &&
                    keyCode != KeyEvent.VK_ESCAPE) {

                    // If the user types a key, restart the timer to update the list.
                    classComboTimer.restart();
                
                } else if (keyCode == KeyEvent.VK_ENTER && classNameCombo.getSelectedIndex() == -1) {
                    
                    // If the user hits enter but no item is selected in the combo, then
                    // select the first item for the user.
                    if (classNameCombo.getModel().getSize() > 0) {
                        classNameCombo.setSelectedIndex(0);
                    }
                    
                    classNameCombo.hidePopup();
                    classComboTimer.restart();
                    e.consume();
                }
            }
        });
        
        // If the list selection changes, restart the timer to update the member list.
        classNameCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                classComboTimer.restart();
            }
        });
        
        return classNameCombo;
    }
    
    private void updateClassNameCombo() {

        JTextField editor = (JTextField) classNameCombo.getEditor().getEditorComponent();                
        String className = editor.getText();
        String unqualifiedName = className;

        // Arrays can be denoted as [Ljava.lang.String; for String[] as an example.
        // If the user is entering an array, it doesn't make sense to use the unqualified name.        
        if (!className.startsWith("[")) {
            String[] tokens = className.split("\\.");
            unqualifiedName = tokens.length > 0 ? tokens[tokens.length - 1] : className;
        }

        Class<?>[] classes = resolveClassName(unqualifiedName);

        if (!className.equals(lastClassName)) {

            // If we're searching for a new class name, then update the combo list.
            lastClassName = className;

            int newSelected = -1;
            boolean shouldShowPopup = classNameCombo.isPopupVisible();
            int selectionStart = editor.getSelectionStart();
            int selectionEnd = editor.getSelectionEnd();
            int caretPosition = editor.getCaretPosition();

            DefaultComboBoxModel model = (DefaultComboBoxModel) classNameCombo.getModel();
            model.removeAllElements();
            
            if (classes.length == 0) {
                editor.setText(className);
                editor.setCaretPosition(caretPosition);
                editor.setSelectionStart(selectionStart);
                editor.setSelectionEnd(selectionEnd);
                memberList.updateForClass(null);
                return;
            }
    
            for (int i = 0; i < classes.length; i++) {
                model.addElement(classes[i].getName());
                    
                if (classes[i].getName().equals(className)) {
                    newSelected = i;
                }
            }
                    
            if (newSelected == -1) {
                shouldShowPopup = classes.length > 0;
            }

            // Have to hide and reshow the popup, so that it validates correctly.
            classNameCombo.setPopupVisible(false);
            classNameCombo.setPopupVisible(shouldShowPopup);
            classNameCombo.setSelectedIndex(newSelected);

            if (newSelected == -1) {
                editor.setText(className);
                editor.setCaretPosition(caretPosition);
                editor.setSelectionStart(selectionStart);
                editor.setSelectionEnd(selectionEnd);
            }
        }

        // Now update the member list for the selected item.
        if (classes.length == 0) {
            memberList.updateForClass(null);
        } else {
            int index = classNameCombo.getSelectedIndex();
            Class<?> selectedClass = index != -1 ? classes[index] : null;
            memberList.updateForClass(selectedClass);
            
            if (selectedClass != null) {
                editor.setText(selectedClass.getName());
            } else {
                editor.setText(className);
            }
        }
    }
    
    /**
     * Resolves an unqualified or array class name by returning the classes it resolves to, 
     * or an empty array if the name cannot be resolved to a class.
     * @param className the *unqualified* class name or array class name to resolve
     * @return the classes it resolves to or an empty array if no classes can be resolved
     */
    private Class<?>[] resolveClassName(String className) {
     
        if (className.startsWith("[")) {
            
            // If we're looking for an array, then the name is automatically fully qualified.
            try {
                return new Class<?>[] { Class.forName(className) };
                
            } catch (ClassNotFoundException ex) {
                // That's ok, class was not found.
                return new Class<?>[0];
                
            } catch (Exception ex) {
                ex.printStackTrace();
                return new Class<?>[0];
            }
        }
     
        // Looks like we're looking for an unqualified class name.
        // Iterate over each package and try to complete the name.
        
        List<Class<?>> classes = new ArrayList<Class<?>>();
        Package[] packages = Package.getPackages();
        
        for (final Package aPackage : packages) {
            
            String fullClassName = aPackage.getName() + "." + className;
            
            try {
                Class<?> classForName = Class.forName(fullClassName);

                if (Modifier.isPublic(classForName.getModifiers())) {
                    classes.add(classForName);
                }
            
            } catch (ClassNotFoundException ex) {
                // That's ok, class was not found.
                
            } catch (NoClassDefFoundError ex) {
                // This seems to be thrown when the class does not exist, but a class does exist with different case.
                //  eg. type in "Gemcutter" (the name of the class is "GemCutter").
                
                // TODO: It would be nice to suggest a correction in this situation.
            
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        return classes.toArray(new Class<?>[0]);
    }

    /**
     * Generates the source definitions for the Java import that the user has selected.
     */
    private void generateSource() throws UnableToResolveForeignEntityException {
     
        String gemSource = null;
        String gemName = gemNameField.getText();
        String gemComment = commentField.getText();
        Scope gemScope = publicButton.getModel().isSelected() ? Scope.PUBLIC : Scope.PRIVATE;
        Object javaImport = memberList.getSelectedValue();

        if (addingDataType) {
            
            // If we're adding a data type, put it into the map of new types to be added.
            newDataTypes.put(memberList.getCurrentClass(), QualifiedName.make(perspective.getWorkingModuleName(), gemName));
            
            // Now pick a sensible name for the constructor that was selected, if any.
            gemName = "make" + gemName;
        }

        // Generate the source for the method/field/constructor declaration.
        // This will cause additional needed data types to be added to the new types map.
        if (javaImport instanceof Method) {
            gemSource = getFunctionDeclaration(gemName, gemComment, gemScope, memberList.getCurrentClass(), (Method) javaImport);
            
        } else if (javaImport instanceof Constructor<?>) {
            gemSource = getConstructorDeclaration(gemName, gemComment, gemScope, (Constructor<?>) javaImport);
        
        } else if (javaImport instanceof Field) {
            gemSource = getFieldDeclaration(gemName, gemComment, gemScope, memberList.getCurrentClass(), (Field) javaImport);
        }

        // Add the required data types.
        for (final Class<?> dataClass : newDataTypes.keySet()) {
        
            QualifiedName dataName = newDataTypes.get(dataClass);
            String dataSource = getDataDeclaration(dataClass, dataName.getUnqualifiedName());
        
            sourceDefinitions.put(dataName.getUnqualifiedName(), dataSource);
        }

        // Finally add the method/field/constructor definition.
        if (javaImport != null) {
            sourceDefinitions.put(gemName, gemSource);
        }
    }

    /**
     * Generates the source for a data declaration for the given class and name.
     * @param javaClass the Java class the data declaration is for
     * @param unqualifiedName the unqualified name of the data type to create
     * @return the source code
     */    
    private String getDataDeclaration(Class<?> javaClass, String unqualifiedName) {

        StringBuilder source = new StringBuilder(GeneratorMessages.getString("JGF_ForeignDataDeclComment"));
        
        source.append("data foreign unsafe import jvm \"");
        source.append(javaClass.getName());
        source.append("\" ");

        source.append("public ");
        source.append(unqualifiedName);
        source.append(";\n");

        return source.toString();
    }
    
    /**
     * Generates a new field declaration.
     * @param gemName the name of the declaration
     * @param gemComment the comment to put in the source
     * @param gemScope the scope of the declaration
     * @param javaClass the Java class that declares the instance of the field that's called
     * @param javaField the java field to import
     * @return the source code
     */
    private String getFieldDeclaration(final String gemName, final String gemComment, final Scope gemScope, final Class<?> javaClass, final Field javaField) throws UnableToResolveForeignEntityException {

        // If a field is not static, the class CAL type has to precede the argument list.
        final boolean isStatic = Modifier.isStatic(javaField.getModifiers());
        final Class<?>[] javaArgTypes;

        // If not static then prepend the class types
        if (!isStatic) {
            javaArgTypes = new Class<?>[2];
            javaArgTypes[0] = javaClass;
            javaArgTypes[1] = javaField.getType();
        } else {
            javaArgTypes = new Class<?>[1];
            javaArgTypes[0] = javaField.getType();
        }
        
        // Convert the java types to CAL types.
        final QualifiedName[] calArgTypes = mapToCALTypes(javaArgTypes);

        // Generate source...
        final StringBuilder source = new StringBuilder(GeneratorMessages.getString("JGF_ForeignFieldDeclComment"));
        
        if (gemComment != null && gemComment.trim().length() > 0) {
            source.append("// " + gemComment + "\n");
        }
        
        source.append("foreign unsafe import jvm \"");
        
        if (isStatic) {
            source.append("static ");
        }
        
        source.append("field ");
        if (isStatic) {
            source.append(javaClass.getName());
            source.append(".");
        }
        source.append(javaField.getName());
        source.append("\" ");
        
        source.append(gemScope.toString()).append(' ');      
        
        source.append(gemName);
        source.append(" :: ");

        for (final QualifiedName calArgType : calArgTypes) {
            source.append(calArgType.getQualifiedName());
            source.append(" -> ");
        }

        // Remove trailing arrow
        source.delete(source.length() - 4, source.length()); 

        source.append(";\n");

        return source.toString();
    }
    
    /**
     * Generate a new constructor declaration.
     * @param gemName the name of the declaration
     * @param gemComment the comment to put in the source
     * @param gemScope the scope of the declaration
     * @param javaConstructor the constructor to import
     * @return the source code
     */
    private String getConstructorDeclaration(String gemName, String gemComment, Scope gemScope, Constructor<?> javaConstructor) throws UnableToResolveForeignEntityException {

        // Determine the java argument types        
        Class<?>[] consArgTypes = javaConstructor.getParameterTypes();
        Class<?>[] javaArgTypes = new Class<?>[consArgTypes.length + 1];
        System.arraycopy(consArgTypes, 0, javaArgTypes, 0, consArgTypes.length);
        javaArgTypes[javaArgTypes.length - 1] = javaConstructor.getDeclaringClass();
        
        // Convert the java types to CAL types.
        QualifiedName[] calArgTypes = mapToCALTypes(javaArgTypes);
        
        StringBuilder source = new StringBuilder(GeneratorMessages.getString("JGF_ForeignConstructorDeclComment"));
        
        if (gemComment != null && gemComment.trim().length() > 0) {
            source.append("// " + gemComment + "\n");
        }
        
        source.append("foreign unsafe import jvm \"constructor ");
        source.append(javaConstructor.getDeclaringClass().getName());
        source.append("\" ");
        
        source.append(gemScope.toString()).append(' ');         
       
        source.append(gemName);
        source.append(" :: ");

        for (final QualifiedName calArgType : calArgTypes) {
            source.append(calArgType.getQualifiedName());
            source.append(" -> ");
        }

        // Remove trailing arrow
        source.delete(source.length() - 4, source.length()); 

        source.append(";\n");

        return source.toString();
    }

    /**
     * Generates a new function declaration.
     * @param gemName the name of the declaration
     * @param gemComment the comment to put in the source
     * @param gemScope the scope of the declaration
     * @param javaClass the Java class that declares the instance of the method that's called
     * @param javaMethod the Java method to import
     * @return the source code
     */
    private String getFunctionDeclaration(final String gemName, final String gemComment, final Scope gemScope, final Class<?> javaClass, final Method javaMethod) throws UnableToResolveForeignEntityException {

        // If a method is not static, the class CAL type has to precede the argument list.
        final boolean isStatic = Modifier.isStatic(javaMethod.getModifiers());
        final int staticOffset = isStatic ? 0 : 1;

        final Class<?>[] methodArgTypes = javaMethod.getParameterTypes();        
        final Class<?>[] javaArgTypes = new Class<?>[methodArgTypes.length + 1 + staticOffset];

        // If not static then prepend the class types
        if (!isStatic) {
            javaArgTypes[0] = javaClass;
        }

        // Inset the method argument types.
        System.arraycopy(methodArgTypes, 0, javaArgTypes, staticOffset, methodArgTypes.length);
        
        // Add the return type at the end
        javaArgTypes[javaArgTypes.length - 1] = javaMethod.getReturnType();
        
        // Convert the java types to CAL types.
        final QualifiedName[] calArgTypes = mapToCALTypes(javaArgTypes);

        // Generate source...
        final StringBuilder source = new StringBuilder(GeneratorMessages.getString("JGF_ForeignFunctionDeclComment"));
        
        if (gemComment != null && gemComment.trim().length() > 0) {
            source.append("// " + gemComment + "\n");
        }
        
        source.append("foreign unsafe import jvm \"");
        
        if (isStatic) {
            source.append("static ");
        }
        
        source.append("method ");
        if (isStatic) {
            source.append(javaClass.getName());
            source.append(".");
        }
        source.append(javaMethod.getName());
        source.append("\" ");
        
        source.append(gemScope.toString()).append(' ');        
        
        source.append(gemName);
        source.append(" :: ");

        for (final QualifiedName calArgType : calArgTypes) {
            source.append(calArgType.getQualifiedName());
            source.append(" -> ");
        }

        // Remove trailing arrow
        source.delete(source.length() - 4, source.length()); 

        source.append(";\n");

        return source.toString();
    }

    /**
     * Maps the given Java Class to the QualifiedName of the matching CAL type.
     * If there is no matching CAL type a new type name will be created on the fly and an
     * entry for it added to the newDataTypes map. These new data types will need to have
     * data declaration created for them.
     * @param javaType the java type to find a CAL type for
     * @param visibleTypes the visible types to search through
     * @return the QualifiedName of the matching CAL type
     */
    private QualifiedName mapToCALType(Class<?> javaType, TypeConstructor[] visibleTypes) throws UnableToResolveForeignEntityException {

        if (javaType == null || visibleTypes == null) {
            throw new NullPointerException();
        }

        // Check to see if we have previously added our own declaration for this type.
        if (newDataTypes.containsKey(javaType)) {
            return newDataTypes.get(javaType);
        }
        
        // If we didn't yet add our own declaration, see if there is an existing one.
        // Start with the primitive types, then check the other types.
        
        if (javaType == Character.TYPE) {
            return CAL_Prelude.TypeConstructors.Char;
            
        } else if (javaType == Boolean.TYPE) {
            return CAL_Prelude.TypeConstructors.Boolean;
            
        } else if (javaType == Byte.TYPE) {
            return CAL_Prelude.TypeConstructors.Byte;
            
        } else if (javaType == Short.TYPE) {
            return CAL_Prelude.TypeConstructors.Short;
            
        } else if (javaType == Integer.TYPE) {
            return CAL_Prelude.TypeConstructors.Int;
            
        } else if (javaType == Long.TYPE) {
            return CAL_Prelude.TypeConstructors.Long;
            
        } else if (javaType == Float.TYPE) {
            return CAL_Prelude.TypeConstructors.Float;
            
        } else if (javaType == Double.TYPE) {
            return CAL_Prelude.TypeConstructors.Double;
            
        } else if (javaType == Void.TYPE) {
            return CAL_Prelude.TypeConstructors.Unit;
            
        } else if (javaType == String.class) {
            return CAL_Prelude.TypeConstructors.String;
            
        } else {

            for (final TypeConstructor visibleType : visibleTypes) {
                
                if (visibleType.getForeignTypeInfo() != null &&
                    javaType == visibleType.getForeignTypeInfo().getForeignType()) {
                    
                    return visibleType.getName();
                }
            }
        }
        
        // Looks like we have to make our own data declaration for this.
        // Pick a sensible name and add it to the map before returning it.
        QualifiedName typeName = getUniqueTypeName(javaType);
        newDataTypes.put(javaType, typeName);

        return typeName;
    }
    
    /**
     * Tries to find a unique name for the Java type. It stars the with base class name and
     * then prepends package names if that name is ambiguous. This is done so that, for example,
     * "java.util.Timer" and "javax.swing.Timer" don't both get imported as "JTimer".
     * Instead they might be imported as "JTimer" and "JSwingTimer".
     * @param javaType the java type to find a unique name for
     * @return a unqiue name for the Java type
     */
    private QualifiedName getUniqueTypeName(Class<?> javaType) {
        
        String arrayNameSuffix = "";
        
        while (javaType.isArray()) {
            javaType = javaType.getComponentType();
            arrayNameSuffix += "Array";
        }

        String javaTypeName = javaType.getName();
            
        // Arrays may contain primitive types that do not start with a capital
        javaTypeName = javaTypeName.substring(0, 1).toUpperCase() + javaTypeName.substring(1);

        // Get the fragments of the name.
        // The fragments are the package name pieces and the unqualified class name.        
        String[] nameFragments = javaTypeName.split("\\.");
        
        // By default the type name is simply "J" plus the unqualified class name.
        String typeName = "J" + nameFragments[nameFragments.length - 1] + arrayNameSuffix;

        ModuleTypeInfo workingModuleTypeInfo = perspective.getWorkingModuleTypeInfo();
        boolean outOfFragments = false;
        int fragmentIndex = 2;
        
        while (workingModuleTypeInfo.getTypeConstructor(typeName) != null) {

            // Disambiguate the name by adding additional fragments.

            if (fragmentIndex > nameFragments.length) {
                outOfFragments = true;
                break;
            }
            
            String newFragment = nameFragments[nameFragments.length - fragmentIndex];
            newFragment = newFragment.substring(0, 1).toUpperCase() + newFragment.substring(1);
            
            typeName = "J" + newFragment + typeName.substring(1);
            
            fragmentIndex++;
        }
        
        if (outOfFragments) {
            
            // If we're out of fragments, then disambiguate by adding number suffixes.
            
            int i = 1;
            String basicTypeName = typeName;
            while (workingModuleTypeInfo.getTypeConstructor(typeName) != null) {
                typeName = basicTypeName + "_" + i;
                i++;
            }            
        }
        
        return QualifiedName.make(perspective.getWorkingModuleName(), typeName);
    }

    /**
     * Checks if the given type is already imported and if it is returns
     * the matching CAL type name. Otherwise this returns null.
     * @param javaType the java type to check for
     * @return the CAL type name if the type is imported, null otherwise
     */
    private QualifiedName getExistingTypeName(Class<?> javaType) throws UnableToResolveForeignEntityException {
        
        if (javaType == null) {
            return null;
        }
        
        TypeConstructor[] visibleTypes = perspective.getTypeConstructors();
        
        for (final TypeConstructor visibleType : visibleTypes) {
            
            if (visibleType.getForeignTypeInfo() != null &&
                javaType == visibleType.getForeignTypeInfo().getForeignType()) {
                
                return visibleType.getName();
            }
        }
        
        return null;
    }
        
    /**
     * Maps all Java types to their matching CAL types.
     * @param javaTypes the java types to map
     * @return array of QualifiedNames for the matching CAL types
     */
    private QualifiedName[] mapToCALTypes(Class<?>[] javaTypes) throws UnableToResolveForeignEntityException {

        QualifiedName[] calTypes = new QualifiedName[javaTypes.length];
        TypeConstructor[] visibleTypes = perspective.getTypeConstructors();

        for (int i = 0; i < javaTypes.length; i++) {
            calTypes[i] = mapToCALType(javaTypes[i], visibleTypes);
        }
        
        return calTypes;
    }
}

/**
 * A special JList for displaying the members of a Java class.
 * This list uses a special renderer to highlight the tokens in the String
 * representation of the class members and display special icons.
 * @author Frank Worsley
 */
class JavaMemberList extends JList {
    private static final long serialVersionUID = -6594368143989165773L;

    /** The class the list is displaying members for. */
    private Class<?> currentClass = null;
    
    /** Whether only constructors are shown in the list. */
    private boolean onlyShowConstructors = false;
    
    /**
     * Constructor for a new list.
     */
    public JavaMemberList() {
        super(new DefaultListModel());
        
        setCellRenderer(new JavaMemberListCellRenderer());
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setToolTipText("JavaMemberList");

        DefaultListModel listModel = (DefaultListModel) getModel();
        listModel.addElement(GeneratorMessages.getString("JGF_InvalidClassName"));
    }
    
    /**
     * @return the class the list is displaying members for or null if no current class.
     */
    public Class<?> getCurrentClass() {
        return currentClass;
    }
    
    /**
     * @param onlyShowConstructors whether to show only constructors in the member list
     */
    public void setOnlyShowConstructors(boolean onlyShowConstructors) {
        this.onlyShowConstructors = onlyShowConstructors;
        updateForClass(getCurrentClass());
    }

    /**
     * Update the members displayed in the list for the given class. 
     * If the class is null then display an error message.
     * @param newClass the class to update for
     */    
    public void updateForClass(Class<?> newClass) {
        
        this.currentClass = newClass;
        
        DefaultListModel listModel = new DefaultListModel();
        
        if (currentClass == null) {
            listModel.addElement(GeneratorMessages.getString("JGF_InvalidClassName"));
            setModel(listModel);
            return;
        }

        List<AccessibleObject> items = new ArrayList<AccessibleObject>();
        
        Constructor<?>[] constructors = currentClass.getConstructors();
        for (final Constructor<?> constructor : constructors) {
            if (Modifier.isPublic(constructor.getModifiers())) {
                items.add(constructor);
            }
        }
        
        if (!onlyShowConstructors) {
            
            Method[] methods = currentClass.getMethods();
            for (final Method method : methods) {
                if (Modifier.isPublic(method.getModifiers())) {
                    items.add(method);
                }
            }
            
            Field[] fields = currentClass.getFields();
            for (final Field field : fields) {
                if (Modifier.isPublic(field.getModifiers())) {
                    items.add(field);
                }
            }
        }
        
        Collections.sort(items, new MemberListItemSorter());
        
        for (final AccessibleObject accessibleObject : items) {
            listModel.addElement(accessibleObject);
        }
        
        setModel(listModel);
    }
    
    /**
     * @param e the MouseEvent for which to get the tooltip text
     * @return the tooltip text for the list item at the coordinates or null if there is no
     * item at the coordinates
     */
    public String getToolTipText(MouseEvent e) {
        
        int index = locationToIndex(e.getPoint());
        
        if (index == -1) {
            return null;
        }
        
        Object value = getModel().getElementAt(index);
        
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return value.toString();
        }

        String basicString = JavaMemberListCellRenderer.getValueString(value);
        
        String[] tokens = basicString.split(" ", 2);
        
        if (tokens.length == 0) {
            return basicString;
            
        } else {
            return "<html><body><b>" + tokens[0] + "</b> <i>" + tokens[1] + "</i></body></html>";
        } 

    }
    
    /**
     * We want tooltips to be displayed to the right of an item.
     * If there is no item at the coordinates it returns null.
     * @param e the mouse event for which to determine the location
     * @return the tooltip location for the list item at the coordinates of the mouse event
     */
    public Point getToolTipLocation(MouseEvent e) {
        
        int index = locationToIndex(e.getPoint());
        
        if (index == -1) {
            return null;
        }
        
        Rectangle cellBounds = getCellBounds(index, index);

        // take off 50 and add 5 for good looks
        return new Point (cellBounds.x + cellBounds.width - 50, cellBounds.y + 5);
    }
}

/**
 * A custom list cell renderer for the Java members list that highlights names and uses custom icons.
 * @author Frank Worsley
 */
class JavaMemberListCellRenderer extends DefaultListCellRenderer {
    
    private static final long serialVersionUID = -2498691767497372110L;

    /** The icon to use for constructors. */
    private static final Icon CONSTRUCTOR_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/javaConstructor.gif"));
    
    /** The icon to use for static methods. */
    private static final Icon STATIC_METHOD_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/javaStaticMethod.gif"));
    
    /** The icon to use for instance methods. */
    private static final Icon INSTANCE_METHOD_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/javaInstanceMethod.gif"));
    
    /** The icon to use for static fields. */
    private static final Icon STATIC_FIELD_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/javaStaticField.gif"));

    /** The icon to use for instance fields. */
    private static final Icon INSTANCE_FIELD_ICON = new ImageIcon(GemCutter.class.getResource("/Resources/javaInstanceField.gif"));
    
    /** The value we are rendering. */
    private Object value = null;
    
    /** Whether the cell being rendered is selected. */
    private boolean isSelected = false;
    
    /** The list the cell is being renderer for. */
    private JList list = null;
    
    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        this.list = list;
        this.value = value;
        this.isSelected = isSelected;

        super.getListCellRendererComponent(list, getValueString(value), index, isSelected, cellHasFocus);

        // Set a proper icon
        if (value instanceof Constructor<?>) {
            setIcon(CONSTRUCTOR_ICON);
            
        } else if (value instanceof Method) {
            
            if (Modifier.isStatic(((Method) value).getModifiers())) {
                setIcon(STATIC_METHOD_ICON);
            } else {
                setIcon(INSTANCE_METHOD_ICON);
            }
            
        } else if (value instanceof Field) {
            
            if (Modifier.isStatic(((Field) value).getModifiers())) {
                setIcon(STATIC_FIELD_ICON);
            } else {
                setIcon(INSTANCE_FIELD_ICON);
            }
        }
        
        // For anything other than error strings we draw custom highlighted text.
        // So just set foreground to be the same as background and nothing will be drawn at all.
        if (!(value instanceof String)) {
            setForeground(getBackground());
        }
        
        return this;
    }
    
    /**
     * @return the x-coordinate at which point the text portion of the list cell starts.
     */
    private int getLabelStart() {
        
        Icon icon = getIcon();
    
        if (icon != null) {
            return icon.getIconWidth() + Math.max(1, getIconTextGap());
        }
        
        return 0;
    }     
    
    /**
     * Paint custom highlighted text for the items in the list.
     * @see java.awt.Component#paint(java.awt.Graphics)
     */
    public void paint(Graphics g) {
        
        super.paint(g);
        
        // Nothing special to do for error messages.
        if (value instanceof String) {
            return;
        }
        
        // Replace with our custom text.
        String valueString = getText();
        AttributedString customString = new AttributedString(valueString);
        
        // By default draw the text in the normal font and color
        customString.addAttribute(TextAttribute.FOREGROUND, isSelected ? list.getSelectionForeground() : list.getForeground());
        customString.addAttribute(TextAttribute.FONT, getFont());

        // Highlight the method/constructor/field name
        int nameEnd = valueString.indexOf(" ", 0);
        if (nameEnd == -1) {
            nameEnd = valueString.length();
        }

        Color highlight = value instanceof Field ? Color.BLUE : Color.GREEN.darker();

        customString.addAttribute(TextAttribute.FOREGROUND, highlight, 0, nameEnd);
        
        // Draw the rest of the text in gray and italics
        customString.addAttribute(TextAttribute.FOREGROUND, Color.GRAY, nameEnd + 1, valueString.length());        
        customString.addAttribute(TextAttribute.FONT, getFont().deriveFont(Font.ITALIC), nameEnd + 1, valueString.length());
        
        g.drawString(customString.getIterator(), getLabelStart(), getHeight() - 3);
    }

    /**
     * @param value the value to get a string representation for
     * @return the String used to represent the value in the list
     */    
    public static String getValueString(Object value) {
        
        if (value instanceof Constructor<?>) {
            Constructor<?> constructor = (Constructor<?>) value;
            Class<?>[] parameters = constructor.getParameterTypes();
            return getClassName(constructor.getDeclaringClass()) + " " + getSignatureString(parameters);
            
        } else if (value instanceof Method) {
            Method method = (Method) value;
            Class<?>[] parameters = method.getParameterTypes();
            String staticString = Modifier.isStatic(method.getModifiers()) ? "static " : "";
            return method.getName() + " " + getSignatureString(parameters) + " - " + staticString + getClassName(method.getReturnType());
            
        } else if (value instanceof Field) {
            Field field = (Field) value; 
            String staticString = Modifier.isStatic(field.getModifiers()) ? "static " : "";
            return field.getName() + " - " + staticString + getClassName(field.getType());
            
        } else {
            return value.toString();
        }
    }        
    
    /**
     * @param parameters the array of Class objects to generate a signature string for.
     * @return a method signature string for the array of parameters
     */
    private static String getSignatureString(Class<?>[] parameters) {

        StringBuilder text = new StringBuilder();
        
        text.append("(");
            
        for (final Class<?> parameter : parameters) {
            text.append(getClassName(parameter));
            text.append(", ");            
        }
            
        if (parameters.length > 0) {
            // Remove trailing comma
            text.delete(text.length() - 2, text.length());
        }
        
        text.append(")");
        
        return text.toString();
    }
    
    /**
     * @param parameter the class to get a name for
     * @return an unqualified, proper name for the class (arrays get [] appened to their element type)
     */
    public static String getClassName(Class<?> parameter) {

        if (parameter.isArray()) {
            return getClassName(parameter.getComponentType()) + "[]";
                
        } else {
            String[] tokens = parameter.getName().split("\\.");
            return tokens[tokens.length - 1];
        }
    }
}

/**
 * Sorts the items in the java member list. Constructors go first, then methods, then fields.
 * For items of the same type it uses case-insensitive sorting.
 * @author Frank Worsley
 */
class MemberListItemSorter implements Comparator<AccessibleObject> {
 
    public int compare(AccessibleObject o1, AccessibleObject o2) {
        
        // Constructors come before anything else.
        if (o1 instanceof Constructor<?>) {
            
            if (o2 instanceof Constructor<?>) {
                return compareString(o1, o2);
                
            } else {
                return -1;
            }
        }
        
        // Methods come after constructors, but before fields.
        if (o1 instanceof Method) {
            
            if (o2 instanceof Constructor<?>) {
                return 1;
                
            } else if (o2 instanceof Field) {
                return -1;
                
            } else {
                return compareString(o1, o2);
            }
        }
        
        // Fields come after everything else.
        if (o1 instanceof Field) {
            
            if (o2 instanceof Field) {
                return compareString(o1, o2);
                
            } else {
                return 1;
            }
        }
        
        throw new IllegalStateException("invalid value in the list");
    }
    
    private int compareString(AccessibleObject o1, AccessibleObject o2) {
        return JavaMemberListCellRenderer.getValueString(o1).compareToIgnoreCase(JavaMemberListCellRenderer.getValueString(o2));
    }
}
