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
 * NavEditorComponent.java
 * Creation date: Jul 22, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openquark.cal.compiler.AdjunctSource;
import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.metadata.CALExample;
import org.openquark.cal.metadata.CALExpression;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.valuenode.Target;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.cal.valuenode.TargetRunner.ProgramCompileException;
import org.openquark.gems.client.AutoCompleteManager;
import org.openquark.gems.client.AutoCompletePopupMenu;
import org.openquark.gems.client.CodeGemEditor;
import org.openquark.gems.client.GemCodeSyntaxListener;
import org.openquark.gems.client.QualificationPanel;
import org.openquark.gems.client.QualificationsDisplay;
import org.openquark.gems.client.ValueRunner;
import org.openquark.gems.client.ModuleNameDisplayUtilities.TreeViewDisplayMode;
import org.openquark.gems.client.caleditor.AdvancedCALEditor;
import org.openquark.gems.client.navigator.NavAddress.NavAddressMethod;
import org.openquark.util.Pair;


/**
 * A base class for editing components that appear in editing sections.
 * The base class provides the actual component that does the editing,
 * the title for the component, and a setter/getter for the component's value.
 * 
 * @param <V> the type of the value stored by the editor
 * @author Frank Worsley
 */
abstract class NavEditorComponent<V> {

    /** The default outside border of editor components with borders. */
    static final Border DEFAULT_OUTSIDE_BORDER = BorderFactory.createLineBorder(Color.GRAY, 1);

    /** The default inside border of editor components with borders. */
    static final Border DEFAULT_INSIDE_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    /** The default compound border of editor components with borders. */
    static final Border DEFAULT_BORDER = BorderFactory.createCompoundBorder(DEFAULT_OUTSIDE_BORDER, DEFAULT_INSIDE_BORDER);
    
    /** The default background color for editors with a background. */
    static final Color DEFAULT_BACKGROUND_COLOR = Color.LIGHT_GRAY;
    
    /** The title of the editor component. */
    private final String title;
    
    /** The description of the editor component. */
    private final String description;
    
    /** The unique key that identifies this editor inside its editor section. */
    private final String key;

    /** The editor section this component is used in. */
    private final NavEditorSection editorSection;
    
    /**
     * Constructs a new editor without a unique key, title, or description.
     * @param editorSection the editor section the editor belongs to
     */
    public NavEditorComponent(NavEditorSection editorSection) {
        this (editorSection, null);
    }
    
    /**
     * Constructs a new editor with a unique key.
     * @param editorSection the editor section the editor belongs to
     * @param key the unique key of the editor
     */
    public NavEditorComponent(NavEditorSection editorSection, String key) {
        this (editorSection, key, null);
    }

    /**
     * Constructs a new editor with a unique key and title.
     * @param editorSection the editor section this editor belongs to
     * @param key the unique key of the editor
     * @param title the title of the editor
     */
    public NavEditorComponent(NavEditorSection editorSection, String key, String title) {
        this (editorSection, key, title, null);
    }
    
    /**
     * Constructs a new editor with the given title, description, and unique key.
     * @param editorSection the editor section the editor belongs to
     * @param key the unique key of the editor
     * @param title the title of the editor
     * @param description the description of the editor
     */
    public NavEditorComponent(NavEditorSection editorSection, String key, String title, String description) {

        if (editorSection == null) {
            throw new NullPointerException();
        }
        
        this.key = key;
        this.title = title;
        this.description = description;
        this.editorSection = editorSection;
    }
    
    /**
     * @return the title of the editor. May be null if the editor doesn't have a title.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * @return the description of the editor. May be null if the editor doesn't have a description.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @return the unique key that identifies this editor component.
     * Maybe be null if this editor does not have a unique key.
     */
    public String getKey() {
        return key;
    }
    
    /**
     * @return the editor section this editor is used by
     */
    NavEditorSection getEditorSection() {
        return editorSection;
    }
    
    /**
     * Notifies the editor section that this editor belongs to that the
     * value stored by the editor has changed.
     */
    void editorChanged() {
        editorSection.editorChanged(this);
    }
    
    /**
     * @return the actual editor component
     */
    public abstract JComponent getEditorComponent();
    
    /**
     * @return the value stored by the editor
     */
    public abstract V getValue();
    
    /**
     * Sets the value stored by the editor.
     * @param value the new value of the editor
     */
    public abstract void setValue(V value);
}

/**
 * An editor component to edit a single line of text.
 * @author Frank Worsley
 */
class NavTextFieldEditor extends NavEditorComponent<String> {
    
    /** The text field for editing the text. */
    private final JTextField textField = new JTextField();

    /**
     * Constructs a new text field editor.
     * @param editorSection the section the editor belongs to
     * @param key the unique key of the editor
     * @param title the title of the editor
     * @param description the description of the editor
     */
    public NavTextFieldEditor(NavEditorSection editorSection, String key, String title, String description) {
        super(editorSection, key, title, description);
        
        textField.setColumns(50);
        
        textField.getDocument().addDocumentListener(new DocumentListener() {
            
            public void insertUpdate(DocumentEvent e) {
                editorChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                editorChanged();                
            }

            public void changedUpdate(DocumentEvent e) {
                editorChanged();
            }
        });
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return textField;
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public String getValue() {
        String text = textField.getText();
        return text != null && text.trim().length() > 0 ? text : null;
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(String value) {
        textField.setText(value);
    }
}

/**
 * An editor component to edit multiple lines of text.
 * @author Frank Worsley
 */
class NavTextAreaEditor extends NavEditorComponent<String> {

    /** The text area for editing the text. */
    private final JTextArea textArea = new JTextArea();

    /** The scrollpane that houses the text area. */
    private final JScrollPane scrollPane = new JScrollPane(textArea);
    
    /**
     * Constructs a new text area editor
     * @param editorSection the editor section the editor belongs to
     * @param key the unique key of the editor
     * @param title the title of the editor
     * @param description the description of the editor
     */
    public NavTextAreaEditor(NavEditorSection editorSection, String key, String title, String description) {

        super(editorSection, key, title, description);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        Dimension size = new Dimension(250, 100);
        scrollPane.setMinimumSize(size);
        scrollPane.setPreferredSize(size);
        scrollPane.setMaximumSize(size);
        
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            
            public void insertUpdate(DocumentEvent e) {
                editorChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                editorChanged();                
            }

            public void changedUpdate(DocumentEvent e) {
                editorChanged();
            }
        });
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return scrollPane;
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public String getValue() {
        String text = textArea.getText();
        return text != null && text.trim().length() > 0 ? text : null;
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(String value) {
        textArea.setText(value);
    }
}

/**
 * An editor component to edit a boolean value.
 * @author Frank Worsley
 */
class NavBooleanEditor extends NavEditorComponent<Boolean> {
    /** The panel that contains the editor components. */
    private final JPanel editorPanel;
    
    /** The button group for the yes/no radio buttons. */
    private final ButtonGroup buttonGroup = new ButtonGroup();
    
    /** The radio button for the true value. */
    private final JRadioButton yesButton = new JRadioButton(NavigatorMessages.getString("NAV_YesButtonLabel"));
    
    /** The radio button for the false value. */
    private final JRadioButton noButton = new JRadioButton(NavigatorMessages.getString("NAV_NoButtonLabel"));
    
    /**
     * Constructs a new boolean editor.
     * @param editorSection the section the editor belongs to
     * @param key the unique key of the editor
     * @param title the title of the editor
     * @param description the description of the editor
     */
    public NavBooleanEditor(NavEditorSection editorSection, String key, String title, String description) {
        
        super(editorSection, key, title, description);
        
        editorPanel = new JPanel();
        editorPanel.setOpaque(false);
        editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.X_AXIS));
        editorPanel.add(yesButton);
        editorPanel.add(Box.createHorizontalStrut(15));
        editorPanel.add(noButton);
        editorPanel.add(Box.createHorizontalGlue());
        
        yesButton.setOpaque(false);
        noButton.setOpaque(false);
        
        buttonGroup.add(yesButton);
        buttonGroup.add(noButton);
        
        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editorChanged();
            }
        });

        noButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editorChanged();
            }
        });
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return editorPanel;
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public Boolean getValue() {
        ButtonModel selected = buttonGroup.getSelection();
        if (selected == yesButton.getModel()) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Boolean value) {
        
        if (value.booleanValue()) {
            yesButton.setSelected(true);
        } else {
            noButton.setSelected(true);
        }
    }
}

/**
 * An editor component to edit a list of Strings.
 * @author Frank Worsley
 */
class NavListEditor extends NavEditorComponent<List<String>> {
    /** The icon for the up button. */
    private static final ImageIcon upIcon = new ImageIcon(NavListEditor.class.getResource("/Resources/up.gif"));
    
    /** The icon for the down button. */
    private static final ImageIcon downIcon = new ImageIcon(NavListEditor.class.getResource("/Resources/down.gif"));

    /** The list of data. */
    private final JList dataList = new JList();
    
    /** The new item text field. */
    private final JTextField itemField = new JTextField();

    /** The scrollpane that contains the JList. */
    private final JScrollPane scrollPane = new JScrollPane(dataList);
    
    /** The panel that contains the list and related buttons. */
    private final JPanel contentPanel = new JPanel();
    
    /** Whether or not the text in the text field can be added as a new item. */
    private boolean canAddNewItem = false;

    /**
     * Constructs a new list editor.
     * @param editorSection the section the editor belongs to
     * @param key the unique key of the editor
     * @param title the title of the editor
     * @param description the description of the editor
     */
    public NavListEditor(NavEditorSection editorSection, String key, String title, String description) {
        super(editorSection, key, title, description);
        
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout(5, 5));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        dataList.setModel(new DefaultListModel());
        dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Dimension size = new Dimension(250, 100);
        scrollPane.setMinimumSize(size);
        scrollPane.setPreferredSize(size);
        scrollPane.setMaximumSize(size);
        
        // Create a text field and buttons for changing the list
        final JButton addButton = new JButton(NavigatorMessages.getString("NAV_AddItemButtonLabel"));        
        final JButton removeButton = new JButton(NavigatorMessages.getString("NAV_RemoveItemButtonLabel"));        
        itemField.setToolTipText(NavigatorMessages.getString("NAV_ItemFieldToolTip"));
        addButton.setEnabled(false);        
        addButton.setToolTipText(NavigatorMessages.getString("NAV_AddItemButtonToolTip"));
        removeButton.setEnabled(false);        
        removeButton.setToolTipText(NavigatorMessages.getString("NAV_RemoveItemButtonLabel"));
        
        // Add the components
        Box hbox = Box.createHorizontalBox();
        hbox.add(itemField);
        hbox.add(Box.createHorizontalStrut(5));
        hbox.add(addButton);
        hbox.add(Box.createHorizontalStrut(5));
        hbox.add(removeButton);
        contentPanel.add(hbox, BorderLayout.NORTH);

        // Create buttons for re-ordering the list
        final JButton upButton = new JButton(upIcon);
        final JButton downButton = new JButton(downIcon);
        upButton.setMargin(new Insets(0, 0, 0, 0));
        upButton.setEnabled(false);
        upButton.setToolTipText(NavigatorMessages.getString("NAV_MoveUpItemButtonToolTip"));
        downButton.setMargin(new Insets(0, 0, 0, 0));
        downButton.setEnabled(false);
        downButton.setToolTipText(NavigatorMessages.getString("NAV_MoveDownItemButtonToolTip"));
        
        // Add the components
        Box vbox = Box.createVerticalBox();
        vbox.add(Box.createVerticalGlue());
        vbox.add(upButton);
        vbox.add(Box.createVerticalStrut(5));
        vbox.add(downButton);
        vbox.add(Box.createVerticalGlue());
        contentPanel.add(vbox, BorderLayout.EAST);
        
        // Add a key listener to add the item if the enter key is pressed
        itemField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addNewItem();
                }
            }
        });
        
        // Add a ley listener to remove the item if the delete key is pressed
        dataList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    removeSelectedItem();
                }
            }            
        });
        
        // Add an action listener to the add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNewItem();
            }
        });
        
        // Add an action listener to the remove button
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
                itemField.requestFocusInWindow();
            }
        });
        
        // Add an action listener to the up button
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel listModel = (DefaultListModel) dataList.getModel();
                int index = dataList.getSelectedIndex();
                Object elem = listModel.getElementAt(index);
                Object old = listModel.getElementAt(index - 1);
                listModel.set(index, old);
                listModel.set(index - 1, elem);
                dataList.setSelectedIndex(index - 1);
                editorChanged();
            }
        });

        // Add an action listener to the down button
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DefaultListModel listModel = (DefaultListModel) dataList.getModel();
                int index = dataList.getSelectedIndex();
                Object elem = listModel.getElementAt(index);
                Object old = listModel.getElementAt(index + 1);
                listModel.set(index, old);
                listModel.set(index + 1, elem);
                dataList.setSelectedIndex(index + 1);
                editorChanged();
            }
        });
        
        // Add a list selection listener to disabled/enable the buttons
        dataList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int index = dataList.getSelectedIndex();
                removeButton.setEnabled(index != -1);
                upButton.setEnabled(index > 0);
                downButton.setEnabled(index != -1 && index < dataList.getModel().getSize() - 1);
            }
        });
        
        // Add a change listener to the text field. Only enable the add button
        // if the text field contains a valid value and the value is not already
        // in the list.
        itemField.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                resetAddButton();
            }

            public void removeUpdate(DocumentEvent e) {
                resetAddButton();
            }
            
            private void resetAddButton() {
                DefaultListModel listModel = (DefaultListModel) dataList.getModel();
                
                canAddNewItem = itemField.getText().trim().length() > 0 
                                  && !listModel.contains(itemField.getText());
                                  
                addButton.setEnabled(canAddNewItem);
            }
            
        });
    }

    /**
     * Adds a new item with the value entered in the item text field to
     * the list, if the new item is valid and not already in the list.
     */    
    private void addNewItem() {
        if (canAddNewItem) {
            DefaultListModel listModel = (DefaultListModel) dataList.getModel();
            listModel.addElement(itemField.getText());
            dataList.setSelectedValue(itemField.getText(), true);
            itemField.setText("");
            editorChanged();
        }        
    }
    
    /**
     * Removes the selected item from the list.
     */
    private void removeSelectedItem() {
        DefaultListModel listModel = (DefaultListModel) dataList.getModel();
        int index = dataList.getSelectedIndex();
        
        if (index != -1) {
            listModel.remove(index);

            // select the next item in the list
            if (listModel.getSize() > 0) {
                dataList.setSelectedIndex(index < listModel.getSize() ? index : index - 1);
            }

            editorChanged();
        }
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return contentPanel;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public List<String> getValue() {
        DefaultListModel listModel = (DefaultListModel) dataList.getModel();
        List<String> values = new ArrayList<String>();
        
        for (int i = 0; i < listModel.size(); i++) {
            values.add((String)listModel.get(i));
        }
        
        return values;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<String> values) {
        DefaultListModel listModel = new DefaultListModel();
        
        for (final String value : values) {
            listModel.addElement(value);
        }
        
        dataList.setModel(listModel);
    }
}

/**
 * An editor component to edit related features.
 * @author Frank Worsley
 */
class NavFeaturesEditor extends NavEditorComponent<List<CALFeatureName>> {
    /** The icon for the up button. */
    private static final ImageIcon upIcon = new ImageIcon(NavListEditor.class.getResource("/Resources/up.gif"));
    
    /** The icon for the down button. */
    private static final ImageIcon downIcon = new ImageIcon(NavListEditor.class.getResource("/Resources/down.gif"));
    
    /** The icon for the left button. */
    private static final ImageIcon leftIcon = new ImageIcon(NavListEditor.class.getResource("/Resources/leftArrow.gif"));
    
    /** The icon for the right button. */
    private static final ImageIcon rightIcon = new ImageIcon(NavListEditor.class.getResource("/Resources/rightArrow.gif"));

    /** The button for adding a related feature. */
    private final JButton addButton = new JButton(rightIcon);
    
    /** The button for removing a related feature. */
    private final JButton removeButton = new JButton(leftIcon);
    
    /** The button for moving up a related feature. */
    private final JButton upButton = new JButton(upIcon);
    
    /** The button for moving down a related feature. */
    private final JButton downButton = new JButton(downIcon);
    
    /** The tree of current related features. */
    private final JList relatedFeatures = new JList();
    
    /** The tree of non-related features. */
    private final NavTree otherFeatures = new NavTree();
    
    /** The panel that contains the trees and related buttons. */
    private final JPanel contentPanel = new JPanel();

    /**
     * Constructs a new list editor.
     * @param editorSection the section the editor belongs to
     * @param key the unique key of the editor
     * @param title the title of the editor
     * @param description the description of the editor
     */
    public NavFeaturesEditor(NavEditorSection editorSection, String key, String title, String description) {
        super(editorSection, key, title, description);
        
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridLayout(1, 2));
        
        Perspective perspective = getEditorSection().getEditorPanel().getNavigatorOwner().getPerspective();
        NavTreeModel model = new NavTreeModel();
        model.load(perspective, TreeViewDisplayMode.FLAT_ABBREVIATED);
        otherFeatures.setModel(model);
        otherFeatures.collapseToModules();
        otherFeatures.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        relatedFeatures.setModel(new DefaultListModel());
        relatedFeatures.setCellRenderer(createFeatureListRenderer());
        relatedFeatures.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Box leftBox = Box.createVerticalBox();
        leftBox.add(Box.createVerticalGlue());
        leftBox.add(addButton);
        leftBox.add(Box.createVerticalStrut(10));
        leftBox.add(removeButton);
        leftBox.add(Box.createVerticalGlue());
        leftBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Box rightBox = Box.createVerticalBox();
        rightBox.add(Box.createVerticalGlue());
        rightBox.add(upButton);
        rightBox.add(Box.createVerticalStrut(10));
        rightBox.add(downButton);
        rightBox.add(Box.createVerticalGlue());        
        rightBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(new JScrollPane(otherFeatures), BorderLayout.CENTER);
        leftPanel.add(leftBox, BorderLayout.EAST);
        leftPanel.setOpaque(false);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(new JScrollPane(relatedFeatures), BorderLayout.CENTER);
        rightPanel.add(rightBox, BorderLayout.EAST);
        rightPanel.setOpaque(false);
        
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        
        // Set a preferred height of 150, the width will resize dynamically anyway.
        contentPanel.setPreferredSize(new Dimension(150, 150));
        
        // Setup the buttons.
        addButton.setEnabled(false);
        addButton.setToolTipText(NavigatorMessages.getString("NAV_AddFeatureButtonToolTip"));
        addButton.setMargin(new Insets(0, 0, 0, 0));
        addButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        removeButton.setEnabled(false);
        removeButton.setToolTipText(NavigatorMessages.getString("NAV_RemoveFeatureButtonToolTip"));
        removeButton.setMargin(new Insets(0, 0, 0, 0));
        removeButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        upButton.setEnabled(false);
        upButton.setToolTipText(NavigatorMessages.getString("NAV_MoveUpFeatureButtonToolTip"));
        upButton.setMargin(new Insets(0, 0, 0, 0));
        
        downButton.setEnabled(false);
        downButton.setToolTipText(NavigatorMessages.getString("NAV_MoveDownFeatureButtonToolTip"));
        downButton.setMargin(new Insets(0, 0, 0, 0));
        
        // Add an action listener to the add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFeature();
                editorChanged();
            }
        });
        
        // Add an action listener to the remove button
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeFeature();
                editorChanged();
            }
        });
        
        // Add an action listener to the up button
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveFeatureUp();
                editorChanged();
            }
        });

        // Add an action listener to the down button
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveFeatureDown();
                editorChanged();
            }
        });
        
        // Add selection listeners to enable/disable the buttons
        otherFeatures.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getNewLeadSelectionPath();
                boolean enabled = false;
                
                if (path != null) {
                    relatedFeatures.clearSelection();
                    
                    NavTreeNode node = (NavTreeNode) path.getLastPathComponent();
                    DefaultListModel listModel = (DefaultListModel) relatedFeatures.getModel();
                    
                    NavAddress address = node.getAddress();
                    
                    // We can add all nodes other than valut nodes as related features. 
                    enabled = address.getParameter(NavAddress.VAULT_PARAMETER) == null &&
                              address.getMethod() != NavAddress.MODULE_NAMESPACE_METHOD &&
                              !address.equals(getEditorSection().getAddress()) &&
                              !listModel.contains(address.toFeatureName());
                }
                
                addButton.setEnabled(enabled);
            }
        });
        
        relatedFeatures.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int index = relatedFeatures.getSelectedIndex();
                removeButton.setEnabled(index != -1);
                upButton.setEnabled(index > 0);
                downButton.setEnabled(index != -1 && index < relatedFeatures.getModel().getSize() - 1);
                if (index != -1) {
                    otherFeatures.clearSelection();
                }
            }
        });
    }

    /**
     * Adds the currently selected features to the list of related features.
     */
    private void addFeature() {
        
        NavTreeNode selected = (NavTreeNode) otherFeatures.getSelectionPath().getLastPathComponent();
        NavAddress address = selected.getAddress();
        
        DefaultListModel listModel = (DefaultListModel) relatedFeatures.getModel();
        listModel.addElement(address.toFeatureName());

        NavTreeNode next = (NavTreeNode) selected.getNextSibling();
        if (next != null) {
            TreePath path = new TreePath(next.getPath());
            otherFeatures.setSelectionPath(path);
            otherFeatures.makeVisible(path);
        } else {
            addButton.setEnabled(false);
        }
    }
    
    /**
     * Removes the selected features from the related features.
     */
    private void removeFeature() {
        
        DefaultListModel listModel = (DefaultListModel) relatedFeatures.getModel();
        int index = relatedFeatures.getSelectedIndex();
        
        listModel.remove(index);

        // select the next item in the list
        if (listModel.getSize() > 0) {
            relatedFeatures.setSelectedIndex(index < listModel.getSize() ? index : index - 1);
        } else {
            removeButton.setEnabled(false);
        }
    }

    /**
     * Moves up the selected related feature.
     */
    private void moveFeatureUp() {
        DefaultListModel listModel = (DefaultListModel) relatedFeatures.getModel();
        int index = relatedFeatures.getSelectedIndex();
        Object elem = listModel.getElementAt(index);
        Object old = listModel.getElementAt(index - 1);
        listModel.set(index, old);
        listModel.set(index - 1, elem);
        relatedFeatures.setSelectedIndex(index - 1);
    }
    
    /**
     * Moves down the selected related feature.
     */
    private void moveFeatureDown() {
        DefaultListModel listModel = (DefaultListModel) relatedFeatures.getModel();
        int index = relatedFeatures.getSelectedIndex();
        Object elem = listModel.getElementAt(index);
        Object old = listModel.getElementAt(index + 1);
        listModel.set(index, old);
        listModel.set(index + 1, elem);
        relatedFeatures.setSelectedIndex(index + 1);
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return contentPanel;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public List<CALFeatureName> getValue() {
        List<CALFeatureName> featureNames = new ArrayList<CALFeatureName>();
        DefaultListModel listModel = (DefaultListModel)relatedFeatures.getModel();
        
        for (int i = 0, size = listModel.getSize(); i < size; i++) {
            featureNames.add((CALFeatureName)listModel.getElementAt(i));
        }
        
        return featureNames;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<CALFeatureName> calFeatureNames) {
        DefaultListModel listModel = new DefaultListModel();

        for (final CALFeatureName calFeatureName : calFeatureNames) {
            listModel.addElement(calFeatureName);
        }
        
        relatedFeatures.setModel(listModel);
    }
    
    /**
     * @return the list cell renderer to use for the related features list
     */
    private DefaultListCellRenderer createFeatureListRenderer() {
        
        return new DefaultListCellRenderer() {
         
            private static final long serialVersionUID = -1761318607899607831L;

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                
                NavAddress address = NavAddress.getAddress((CALFeatureName) value);
                NavAddressMethod method = address.getMethod();
                NavFrameOwner owner = getEditorSection().getEditorPanel().getNavigatorOwner();
                ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(owner.getPerspective().getWorkingModuleTypeInfo());
                
                String text = NavAddressHelper.getDisplayText(owner, address, namingPolicy);
                Icon icon = null;
                
                if (method == NavAddress.MODULE_METHOD) {
                    icon = NavTreeCellRenderer.moduleNodeIcon;
                } else if (method == NavAddress.FUNCTION_METHOD) {
                    icon = NavTreeCellRenderer.functionNodeIcon;
                } else if (method == NavAddress.TYPE_CLASS_METHOD) {
                    icon = NavTreeCellRenderer.typeClassNodeIcon;
                } else if (method == NavAddress.TYPE_CONSTRUCTOR_METHOD) {
                    icon = NavTreeCellRenderer.typeConstructorNodeIcon;
                } else if (method == NavAddress.CLASS_METHOD_METHOD) {
                    icon = NavTreeCellRenderer.functionNodeIcon;
                } else if (method == NavAddress.DATA_CONSTRUCTOR_METHOD) {
                    icon = NavTreeCellRenderer.functionNodeIcon;
                } else if (method == NavAddress.CLASS_INSTANCE_METHOD) {
                    icon = NavTreeCellRenderer.classInstanceNodeIcon;
                } else if (method == NavAddress.INSTANCE_METHOD_METHOD) {
                    icon = NavTreeCellRenderer.functionNodeIcon;
                }
                
                JLabel label = (JLabel) super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                
                label.setIcon(icon);
                
                return label;
            }
        };
    }
}

/**
 * An editor component to edit a CALExample.
 * @author Frank Worsley
 */
class NavExampleEditor extends NavEditorComponent<CALExample> {
    /** The main panel that contains the editor components. */
    private final JPanel contentPanel = new JPanel();

    /** The field for editing the example description. */
    private final JTextField descriptionField = new JTextField();
    
    /** The checkbox for editing the run automatically flag. */    
    private final JCheckBox runAutomaticallyBox = new JCheckBox(NavigatorMessages.getString("NAV_AutoRunExampleCheckBox"));

    /** The expression editor panel for editing the example expression. */
    private final ExpressionPanel expressionPanel;

    /** The combox box for selecting the module context. */
    private final JComboBox moduleContextBox;
    
    /** The CAL example this editor is currently editing. */
    private CALExample currentValue = null;

    /**
     * Constructs a new example editor.
     * @param editorSection the section the editor belongs to
     * @param example the example with which to initialize the editor
     */
    public NavExampleEditor(NavEditorSection editorSection, CALExample example) {
        
        super(editorSection);
        
        // Constraints for components in the left column
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.WEST;
        left.fill = GridBagConstraints.BOTH;
        left.gridx = 0;
        left.weightx = 0;
        left.insets = new Insets(2, 0, 3, 5);
        
        // Right column constraints
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.WEST;
        right.fill = GridBagConstraints.BOTH;
        right.gridx = 1;
        right.weightx = 1;
        right.insets = new Insets(2, 5, 3, 0);

        // Setup the controls
        moduleContextBox = EditorHelper.getModuleContextComboBox(getEditorSection().getEditorPanel().getNavigatorOwner());
        
        expressionPanel = new ExpressionPanel(editorSection.getEditorPanel().getNavigatorOwner());
        
        runAutomaticallyBox.setOpaque(false);

        // Create a JPanel to hold the top editing controls
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new GridBagLayout());
        topPanel.add(new JLabel(NavigatorMessages.getString("NAV_Description")), left);
        topPanel.add(descriptionField, right);
        topPanel.add(new JLabel(NavigatorMessages.getString("NAV_ExpressionRunInModule")), left);
        topPanel.add(moduleContextBox, right);
        
        left.gridwidth = GridBagConstraints.REMAINDER;
        topPanel.add(runAutomaticallyBox, left);


        // Setup the main content panel
        
        contentPanel.setOpaque(true);
        contentPanel.setBackground(DEFAULT_BACKGROUND_COLOR);
        contentPanel.setBorder(DEFAULT_BORDER);
        contentPanel.setLayout(new BorderLayout(5, 5));
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(expressionPanel, BorderLayout.CENTER);

        // Add run button
        expressionPanel.addRunButton(NavigatorMessages.getString("NAV_RunExampleButton"), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                expressionPanel.runExpression();
            }
        });

        // Add delete button
        expressionPanel.addDeleteButton(NavigatorMessages.getString("NAV_DeleteExampleButton"), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getEditorSection().removeEditor(NavExampleEditor.this);
            }
        });

        // Add change listeners to know when the editor changed
        moduleContextBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                expressionPanel.setModuleName((ModuleName) moduleContextBox.getSelectedItem());
            }
        });

        runAutomaticallyBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editorChanged();
            }
        });
        
        // Add change listener to know when editor contents change
        expressionPanel.setPanelChangeListener(new ExpressionPanel.PanelChangeListener() {
            public void panelContentsChanged() {
                editorChanged();
            }
        });
        
        descriptionField.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void insertUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void removeUpdate(DocumentEvent e) {
                editorChanged();
            }
        });
        
        // Finally set the supplied value
        setValue(example);
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return contentPanel;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public CALExample getValue() {
        ModuleName moduleContext = (ModuleName) moduleContextBox.getSelectedItem();
        String description = (descriptionField.getText().trim().length() > 0) ? descriptionField.getText() : null;
        CALExpression expression = new CALExpression(
                moduleContext, expressionPanel.getExpressionText(), expressionPanel.getQualificationMap(), expressionPanel.getQualifiedExpressionText());
        CALExample example = new CALExample(expression, description, runAutomaticallyBox.isSelected());
        return example;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(CALExample value) {
        currentValue = value;

        moduleContextBox.setSelectedItem(currentValue.getExpression().getModuleContext());
        expressionPanel.setModuleName(currentValue.getExpression().getModuleContext());
        expressionPanel.setQualificationMap(currentValue.getExpression().getQualificationMap().makeCopy());
        expressionPanel.setExpressionText(currentValue.getExpression().getExpressionText());
        descriptionField.setText(currentValue.getDescription());
        runAutomaticallyBox.setSelected(currentValue.evaluateExample());
        
        if (currentValue.evaluateExample()) {
            if (currentValue.getExpression().getExpressionText().length() == 0) {
                expressionPanel.setStatusMessage(NavigatorMessages.getString("NAV_EnterUsageExample_Message"));
            } else {
                expressionPanel.runExpression();
            }
        } else {
            expressionPanel.setStatusMessage(NavigatorMessages.getString("NAV_ClickToRunExample_Message"));            
        }
    }
}

/**
 * An editor component to edit a custom metadata attribute.
 * @author Joseph Wong
 */
class NavCustomAttributeEditor extends NavEditorComponent<Pair<String, String>> {
    
    /** The main panel that contains the editor components. */
    private final JPanel contentPanel = new JPanel();

    /** The text field for the custom attribute's name. */
    private final JTextField attributeName = new JTextField();
    
    /** The text area for the custom attribute's value. */
    private final JTextArea attributeValue = new JTextArea();
    
    /** The scrollpane that contains the text area. */
    private final JScrollPane scrollPane = new JScrollPane(attributeValue);
    
    /** The icon for the delete button. */
    private static final ImageIcon deleteIcon = new ImageIcon(ExpressionPanel.class.getResource("/Resources/delete.gif"));
    
    /** The button for deleting the custom attribute. */
    private final JButton deleteButton = new JButton(deleteIcon);
    
    /**
     * Constructs a new custom attribute editor.
     * @param editorSection the section the editor belongs to
     */
    public NavCustomAttributeEditor(NavEditorSection editorSection, String attrName, String attrValue) {
        
        super(editorSection);
        
        // Initial values for attribute name and value
        attributeName.setText(attrName);
        attributeValue.setText(attrValue);
        
        attributeValue.setLineWrap(true);
        attributeValue.setWrapStyleWord(true);
        
        Dimension size = new Dimension(250, 100);
        scrollPane.setMinimumSize(size);
        scrollPane.setPreferredSize(size);
        scrollPane.setMaximumSize(size);
        
        // Constraints for components in the left column
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.WEST;
        left.fill = GridBagConstraints.BOTH;
        left.gridx = 0;
        left.weightx = 0;
        left.insets = new Insets(2, 0, 3, 5);
        
        // Center column constraints
        GridBagConstraints center = new GridBagConstraints();
        center.anchor = GridBagConstraints.WEST;
        center.fill = GridBagConstraints.BOTH;
        center.gridx = 1;
        center.weightx = 1;
        center.insets = new Insets(2, 0, 3, 5);

        // Right column constraints
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.WEST;
        right.fill = GridBagConstraints.BOTH;
        right.gridx = 2;
        right.weightx = 0;
        right.insets = new Insets(2, 0, 3, 0);
        
        // Setup the left panel
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.add(new JLabel(NavigatorMessages.getString("NAV_CustomAttributeName")), left);
        leftPanel.add(attributeName, center);
        leftPanel.add(new JLabel(NavigatorMessages.getString("NAV_CustomAttributeValue")), left);
        leftPanel.add(scrollPane, center);
        
        // Setup the right panel
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(deleteButton);
        rightPanel.add(Box.createVerticalGlue());
        
        // Setup the content panel
        contentPanel.setOpaque(true);
        contentPanel.setBackground(DEFAULT_BACKGROUND_COLOR);
        contentPanel.setBorder(DEFAULT_BORDER);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(leftPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);
        
        // Setup the delete button
        deleteButton.setToolTipText(NavigatorMessages.getString("NAV_DeleteCustomAttributeButton"));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getEditorSection().removeEditor(NavCustomAttributeEditor.this);
            }
        });

        // Add change listener to know when editor contents change
        attributeName.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void insertUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void removeUpdate(DocumentEvent e) {
                editorChanged();
            }
        });
        
        attributeValue.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void insertUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void removeUpdate(DocumentEvent e) {
                editorChanged();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getEditorComponent() {
        return contentPanel;
    }

    /**
     * @return a Pair of Strings - the (name, value) pair.
     */
    @Override
    public Pair<String, String> getValue() {
        String name = attributeName.getText();
        String value = attributeValue.getText();
        
        if (name == null || name.trim().length() == 0) {
            name = null;
        }
        
        if (value == null || value.trim().length() == 0) {
            value = null;
        }
        
        return new Pair<String, String>(name, value);
    }
    
    /**
     * @param value a Pair of Strings - the (name, value) pair to be set into the editor.
     */
    @Override
    public void setValue(Pair<String, String> value) {
        attributeName.setText(value.fst());
        attributeValue.setText(value.snd());
    }
}

/**
 * An editor component to edit display the argument information for an entity with
 * a button to edit the argument metadata.
 * @author Frank Worsley
 */
class NavEntityArgumentEditor extends NavEditorComponent<ArgumentMetadata> {
    /** The main content panel for the editor. */
    private final JPanel contentPanel = new JPanel();
    
    /** The label for displaying the type of the argument. */
    private final JLabel typeLabel = new JLabel();
    
    /** The label for displaying the name of the argument. */
    private final JLabel nameLabel = new JLabel();
    
    /** The field for editing the argument display name. */
    private final JTextField displayNameField = new JTextField();
    
    /** The field for editing the argument description. */
    private final JTextField descriptionField = new JTextField();
    
    /** The button for editing the argument. */
    private final JButton editButton = new JButton(NavigatorMessages.getString("NAV_EditArgProperties"));
    
    /** The number of the argument. */
    private final int argumentNumber;
    
    /** The adjusted name of the argument. */
    private String adjustedName = null;
    
    /** The type of the argument. */
    private String typeString = null;
    
    /** The argument metadata this editor was initialized with. */
    private ArgumentMetadata initialValue = null;

    /**
     * Constructs a new argument editor.
     * @param editorSection the section the editor belongs to
     * @param metadata the argument metadata
     * @param typeString a String that represents the type of the argument (ie: Num a => [a])
     * @param adjustedName the adjusted name of the argument
     */
    NavEntityArgumentEditor(NavEditorSection editorSection, int argNum, ArgumentMetadata metadata, String typeString, String adjustedName) {
        
        super(editorSection);
        
        this.argumentNumber = argNum;
        this.adjustedName = adjustedName;
        this.typeString = typeString;
        
        contentPanel.setOpaque(true);
        contentPanel.setBackground(DEFAULT_BACKGROUND_COLOR);
        contentPanel.setBorder(DEFAULT_BORDER);
        contentPanel.setLayout(new BorderLayout(5, 5));

        JLabel descriptionTitle = new JLabel(NavigatorMessages.getString("NAV_ShortDescription"));
        JLabel nameTitle = new JLabel(NavigatorMessages.getString("NAV_DisplayName"));
        nameTitle.setPreferredSize(descriptionTitle.getPreferredSize());

        nameTitle.setToolTipText(NavigatorMessages.getString("NAV_DisplayNameDescription"));
        descriptionTitle.setToolTipText(NavigatorMessages.getString("NAV_ShortDescriptionDescription"));
        
        typeLabel.setFont(typeLabel.getFont().deriveFont(Font.ITALIC));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        
        editButton.setToolTipText(NavigatorMessages.getString("NAV_EditArgPropertiesToolTip"));
        
        Dimension prefSize = displayNameField.getPreferredSize();
        prefSize = new Dimension(150, prefSize.height);
        displayNameField.setMaximumSize(prefSize);
        displayNameField.setPreferredSize(prefSize);
        
        prefSize = descriptionField.getPreferredSize();
        prefSize = new Dimension(500, prefSize.height);
        descriptionField.setMaximumSize(prefSize);
        descriptionField.setPreferredSize(prefSize);
                
        Box topBox = Box.createHorizontalBox();
        topBox.add(nameLabel);
        topBox.add(typeLabel);
        topBox.add(Box.createHorizontalGlue());
        topBox.add(editButton);

        Box centerBox = Box.createHorizontalBox();
        centerBox.add(nameTitle);
        centerBox.add(Box.createHorizontalStrut(5));
        centerBox.add(displayNameField);
        centerBox.add(Box.createHorizontalGlue());
        
        Box bottomBox = Box.createHorizontalBox();
        bottomBox.add(descriptionTitle);
        bottomBox.add(Box.createHorizontalStrut(5));
        bottomBox.add(descriptionField);
        bottomBox.add(Box.createHorizontalGlue());
        
        contentPanel.add(topBox, BorderLayout.NORTH);
        contentPanel.add(centerBox, BorderLayout.CENTER);
        contentPanel.add(bottomBox, BorderLayout.SOUTH);
        
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NavFrameOwner owner = getEditorSection().getEditorPanel().getNavigatorOwner();
                NavAddress address = getEditorSection().getEditorPanel().getAddress();
                owner.editMetadata(address.withParameter(NavAddress.ARGUMENT_PARAMETER, Integer.toString(argumentNumber)));
            }
        });
        
        // Add document listeners to let the section know when the user edits a value
        displayNameField.getDocument().addDocumentListener(new DocumentListener() {
            
            public void changedUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void insertUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void removeUpdate(DocumentEvent e) {
                editorChanged();
            }            
        });

        descriptionField.getDocument().addDocumentListener(new DocumentListener() {
            
            public void changedUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void insertUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void removeUpdate(DocumentEvent e) {
                editorChanged();
            }            
        });
        
        setValue(metadata);
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return contentPanel;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public ArgumentMetadata getValue() {
        ArgumentMetadata currentValue = (ArgumentMetadata) initialValue.copy();
        currentValue.setDisplayName(displayNameField.getText());            
        currentValue.setShortDescription(descriptionField.getText());
        return currentValue;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(ArgumentMetadata value) {
        initialValue = value;
        displayNameField.setText(initialValue.getDisplayName());
        descriptionField.setText(initialValue.getShortDescription());
        setAdjustedName(adjustedName);
        setType(typeString);
    }
    
    /**
     * @param adjustedName the adjusted name of the argument to use when there is no real name
     */
    public void setAdjustedName(String adjustedName) {
        this.adjustedName = adjustedName;
        nameLabel.setText(adjustedName);
    }
    
    /**
     * @param typeString the type of the argument to display in the UI
     */
    public void setType(String typeString) {
        this.typeString = typeString;
        typeLabel.setText(typeString != null ? " :: " + typeString : null);        
    }   
}

/**
 * An editor component to edit display the return value information for an entity.
 * @author Frank Worsley
 */
class NavEntityReturnValueEditor extends NavEditorComponent<String> {
    /** The main content panel for the editor. */
    private final JPanel contentPanel = new JPanel();
    
    /** The label for displaying the type of the return value. */
    private final JLabel typeLabel = new JLabel();
    
    /** The label for displaying the return value indicator (e.g. "result"). */
    private final JLabel nameLabel = new JLabel(NavigatorMessages.getString("NAV_ReturnValueIndicator"));
    
    /** The field for editing the return value description. */
    private final JTextField descriptionField = new JTextField();
    
    /** The type of the argument. */
    private String typeString = null;
    
    /** The metadata value this editor was initialized with. Could be null. */
    private String initialValue = null;

    /**
     * Constructs a new return value editor.
     * @param editorSection the section the editor belongs to
     * @param key the key of the editor.
     * @param returnValueDesc the metadata value. Could be null.
     * @param typeString a String that represents the type of the return value (e.g. Num a => [a])
     */
    NavEntityReturnValueEditor(NavEditorSection editorSection, String key, String returnValueDesc, String typeString) {
        
        super(editorSection, key);
        
        this.typeString = typeString;
        
        contentPanel.setOpaque(true);
        contentPanel.setBackground(DEFAULT_BACKGROUND_COLOR);
        contentPanel.setBorder(DEFAULT_BORDER);
        contentPanel.setLayout(new BorderLayout(5, 5));

        JLabel descriptionTitle = new JLabel(NavigatorMessages.getString("NAV_ShortDescription"));
        JLabel nameTitle = new JLabel(NavigatorMessages.getString("NAV_DisplayName"));
        nameTitle.setPreferredSize(descriptionTitle.getPreferredSize());

        nameTitle.setToolTipText(NavigatorMessages.getString("NAV_DisplayNameDescription"));
        descriptionTitle.setToolTipText(NavigatorMessages.getString("NAV_ShortDescriptionDescription"));
        
        typeLabel.setFont(typeLabel.getFont().deriveFont(Font.ITALIC));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.ITALIC | Font.BOLD));
        
        Dimension prefSize = descriptionField.getPreferredSize();
        prefSize = new Dimension(500, prefSize.height);
        descriptionField.setMaximumSize(prefSize);
        descriptionField.setPreferredSize(prefSize);
                
        Box topBox = Box.createHorizontalBox();
        topBox.add(nameLabel);
        topBox.add(typeLabel);
        topBox.add(Box.createHorizontalGlue());

        Box bottomBox = Box.createHorizontalBox();
        bottomBox.add(descriptionTitle);
        bottomBox.add(Box.createHorizontalStrut(5));
        bottomBox.add(descriptionField);
        bottomBox.add(Box.createHorizontalGlue());
        
        contentPanel.add(topBox, BorderLayout.NORTH);
        contentPanel.add(bottomBox, BorderLayout.CENTER);
        
        // Add document listeners to let the section know when the user edits a value
        descriptionField.getDocument().addDocumentListener(new DocumentListener() {
            
            public void changedUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void insertUpdate(DocumentEvent e) {
                editorChanged();
            }
    
            public void removeUpdate(DocumentEvent e) {
                editorChanged();
            }            
        });
        
        setValue(returnValueDesc);
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return contentPanel;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public String getValue() {
        String description = descriptionField.getText().trim();
        if (description.length() == 0) {
            return null;
        } else {
            return description;
        }
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(String value) {
        initialValue = value;
        if (initialValue == null) {
            descriptionField.setText("");
        } else {
            descriptionField.setText(initialValue);
        }
        setType(typeString);
    }
    
    /**
     * @param typeString the type of the argument to display in the UI
     */
    public void setType(String typeString) {
        this.typeString = typeString;
        typeLabel.setText(typeString != null ? " :: " + typeString : null);        
    }   
}

/**
 * An editor for editing CAL expressions.
 * @author Frank Worsley
 */
class NavExpressionEditor extends NavEditorComponent<CALExpression> {
    /** The main content panel for the editor. */
    private final JPanel contentPanel = new JPanel();

    /** The combo box for selecting the module context. */
    private final JComboBox moduleContextBox;
    
    /** The expression editor panel for editing the expression. */
    private final ExpressionPanel expressionPanel;

    /** The current CAL expression being edited. */
    private CALExpression currentValue;

    /**
     * Constructs a new expression editor.
     * @param editorSection the editor section the editor belongs to
     * @param key the unique key of the editor
     * @param title the title of the editor
     * @param description the description of the editor
     */
    public NavExpressionEditor(NavEditorSection editorSection, String key, String title, String description) {
        super(editorSection, key, title, description);

        // Create the components
        moduleContextBox = EditorHelper.getModuleContextComboBox(editorSection.getEditorPanel().getNavigatorOwner());
        expressionPanel = new ExpressionPanel(editorSection.getEditorPanel().getNavigatorOwner());
        expressionPanel.setStatusMessage(NavigatorMessages.getString("NAV_ExpressionClickButtonToRun_Message"));
        
        // Setup the content panel
        contentPanel.setLayout(new BorderLayout(5, 5));
        contentPanel.setBackground(DEFAULT_BACKGROUND_COLOR);
        contentPanel.setOpaque(true);
        contentPanel.setBorder(DEFAULT_BORDER);
        
        // Add the components
        Box topBox = Box.createHorizontalBox();
        topBox.add(new JLabel(NavigatorMessages.getString("NAV_ExpressionRunInModule")));
        topBox.add(Box.createHorizontalStrut(5));
        topBox.add(moduleContextBox);
        contentPanel.add(topBox, BorderLayout.NORTH);
        contentPanel.add(expressionPanel, BorderLayout.CENTER);
        
        // Add a run button
        expressionPanel.addRunButton(NavigatorMessages.getString("NAV_ExpressionClickToRun_Message"), new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                expressionPanel.runExpression();
            }
        });
        
        // Add change listeners to update editor with new module
        moduleContextBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                expressionPanel.setModuleName((ModuleName) moduleContextBox.getSelectedItem());
            }
        });
        // Add change listener to knwo when editor contents change
        expressionPanel.setPanelChangeListener(new ExpressionPanel.PanelChangeListener() {
            public void panelContentsChanged() {
                editorChanged();
            }
        });
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getEditorComponent()
     */
    @Override
    public JComponent getEditorComponent() {
        return contentPanel;
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#getValue()
     */
    @Override
    public CALExpression getValue() {
        
        if (expressionPanel.getExpressionText().trim().length() == 0 || moduleContextBox.getSelectedItem() == null) {
            return null;
        }
        
        return new CALExpression((ModuleName) moduleContextBox.getSelectedItem(), expressionPanel.getExpressionText(), 
                expressionPanel.getQualificationMap(), expressionPanel.getQualifiedExpressionText());
    }

    /**
     * @see org.openquark.gems.client.navigator.NavEditorComponent#setValue(java.lang.Object)
     */
    @Override
    public void setValue(CALExpression value) {
        currentValue = value;
        
        if (value != null) {
            moduleContextBox.setSelectedItem(currentValue.getModuleContext());
            expressionPanel.setExpressionText(currentValue.getExpressionText());
        } else {
            moduleContextBox.setSelectedItem(CAL_Prelude.MODULE_NAME);
            expressionPanel.setExpressionText(null);
        }
        
        expressionPanel.setStatusMessage(NavigatorMessages.getString("NAV_ExpressionClickButtonToRun_Message"));        
    }   
}

/**
 * A simple helper class that defines one static method that is used by several editors.
 * @author Frank Worsley
 */
class EditorHelper {
    //
    // This class is not intended to be instantiated.
    //
    private EditorHelper() {
    }
    
    /**
     * @param owner the navigator owner from which to get the perspective
     * @return a combo box with the <code>ModuleName</code>s of all modules in the perspective
     */
    public static JComboBox getModuleContextComboBox(NavFrameOwner owner) {
        
        List<ModuleName> modules = new ArrayList<ModuleName>();
        Perspective perspective = owner.getPerspective();
        
        // get names of visible modules
        for (final MetaModule metaModule : perspective.getVisibleMetaModules()) {
            modules.add(metaModule.getName());
        }
        
        // get names of invisible modules
        for (final MetaModule metaModule : perspective.getInvisibleMetaModules()) {
            modules.add(metaModule.getName());
        }
        
        // sort alphabetically
        Collections.sort(modules);
        
        // load the combobox model with the modules
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (final ModuleName moduleName : modules) {
            model.addElement(moduleName);
        }
        
        return new JComboBox(model);
    }

    /**
     * Evaluates the given CAL expression and puts the result text into the given StringBuilder.
     * @param owner the navigator owner
     * @param expression the expression to evaluate
     * @param result the buffer to hold the result text
     * @return true if evaluated successfully, false if error occurs
     */
    public static boolean evaluateExpression(NavFrameOwner owner, CALExpression expression, StringBuilder result) {
        
        // Check for old example format..
        if (expression.getQualifiedExpressionText().equals("") && expression.getExpressionText().length()>0) {
            // Expression has not been qualified
            CompilerMessageLogger messageLogger = new MessageLogger();
            CALExpression qualifiedExpression = qualifyExpression(owner, expression, messageLogger);
            if ((qualifiedExpression == null) && (result != null)) {
                result.append(messageLogger.getFirstError().getMessage());
                return false;
            }
        }
        
        String expressionText = expression.getQualifiedExpressionText();
        CompilerMessageLogger messageLogger = new MessageLogger();
        SourceModel.Expr expressionSourceModel = SourceModelUtilities.TextParsing.parseExprIntoSourceModel(expressionText, messageLogger);
        
        if (messageLogger.getNErrors() > 0) {
            // The expression could not be parsed.
            result.append(messageLogger.getFirstError().getMessage());
            return false;
        }
        
        Target expressionTarget = new Target.SimpleTarget(expressionSourceModel);
        ModuleName expressionModule = expression.getModuleContext();
        AdjunctSource expressionDef = expressionTarget.getTargetDef(null, owner.getTypeChecker().getTypeCheckInfo(expressionModule).getModuleTypeInfo());
        
        TypeExpr resultType = owner.getTypeChecker().checkFunction(expressionDef, expressionModule, messageLogger);

        if (resultType == null) {
            // The type checker couldn't figure out the type.
            result.append(messageLogger.getFirstError().getMessage());
            return false;
        }

        try {
            // Now create a target to be run by a value runner.
            
            ValueRunner valueRunner = owner.getValueRunner();
            ValueNode valueNode = valueRunner.getValue(expressionTarget, expressionModule);
            
            if (valueRunner.isErrorFlagged()) {
                result.append(valueRunner.getErrorMessage(expressionTarget, expressionModule));
                return false;
                
            } else if (valueNode == null) {
                result.append(NavigatorMessages.getString("NAV_NullValueNode_Message"));
                return false;
                
            } else {
                result.append(valueNode.getTextValue());
                return true;
            }

        } catch (ProgramCompileException ex) {
            result.append(NavigatorMessages.getString("NAV_CompilationError_Message"));
            return false;

        } catch (Throwable ex) {
            result.append(NavigatorMessages.getString("NAV_ExecutionError_Message"));
            return false;
        }
    }
    
    /**
     * Analyses the specified expression's text, updates the qualified code and qualification map. 
     * 
     * Note: This method is here because CAL expressions may have been created with no qualified code;
     * this happens when loading old metadata which does not contain qualification map, and thus this 
     * method is here for backwards compatibility. 
     * 
     * @param owner
     * @param expression
     * @param messageLogger logger to hold errors encountered in auto-qualification
     * @return expression with updated qualified code and map; null if errors occurred
     */
    private static CALExpression qualifyExpression(NavFrameOwner owner, CALExpression expression, CompilerMessageLogger messageLogger) {
        CodeAnalyser analyser = new CodeAnalyser(
                owner.getTypeChecker(),
                owner.getPerspective().getMetaModule(expression.getModuleContext()).getTypeInfo(), 
                true, 
                true);
        CodeAnalyser.QualificationResults qualificationResults = 
                analyser.qualifyExpression(expression.getExpressionText(), null, expression.getQualificationMap(), messageLogger);
        if (qualificationResults == null) {
            return null;
        }

        return new CALExpression(
                expression.getModuleContext(), 
                expression.getExpressionText(),
                qualificationResults.getQualificationMap(), 
                qualificationResults.getQualifiedCode());
    }
}

/**
 * A special panel for editing and running a CALExpression. It provides a CAL source
 * code editor, a status label and optional run/delete buttons.
 * @author Frank Worsley
 */
class ExpressionPanel extends JPanel implements AutoCompleteManager.AutoCompleteEditor {
    
    private static final long serialVersionUID = -6858240765439671805L;
    
    /*
     * Icons used for popup menu items
     */
    private static final ImageIcon POPUP_CONSTRUCTOR_ICON   = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/Gem_Yellow.gif"));
    private static final ImageIcon POPUP_FUNCTION_ICON      = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/nav_function.gif"));
    private static final ImageIcon POPUP_CLASS_ICON         = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/nav_typeclass.gif"));
    private static final ImageIcon POPUP_TYPE_ICON          = new ImageIcon(AdvancedCALEditor.class.getResource("/Resources/nav_typeconstructor.gif"));
    
    /**
     * Class providing default popup menus for identifiers
     * @author Iulian Radu
     */
    private class PopupProvider implements AdvancedCALEditor.IdentifierPopupMenuProvider {
        
        /**
         * Focus listener for editor popup menus. 
         * On focus lost, display selections are cleared
         */
        private class EditorMenuFocusListener implements PopupMenuListener {
            public void popupMenuCanceled(PopupMenuEvent e) {
                getQualificationsDisplay().clearSelection();
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                getQualificationsDisplay().clearSelection();
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        }
        private final EditorMenuFocusListener editorMenuFocusListener = new EditorMenuFocusListener();
        
        /**
         * Menu item for converting an ambiguity to a proper qualification
         * @author Iulian Radu
         */
        private class ToQualificationMenuItem extends JCheckBoxMenuItem implements ActionListener {
            
            private static final long serialVersionUID = 7755582940170807447L;
            
            private final String unqualifiedName;
            private final ModuleName moduleName;
            private final SourceIdentifier.Category type; 
            
            /**
             * Constructor
             * @param unqualifiedName unqualified name of the identifier
             * @param moduleName module which the identifier will belong to if menu clicked
             * @param type type of identifier
             */
            ToQualificationMenuItem(String unqualifiedName, ModuleName moduleName, SourceIdentifier.Category type) {
                super(QualifiedName.make(moduleName, unqualifiedName).getQualifiedName());
                this.unqualifiedName = unqualifiedName;
                this.moduleName = moduleName;
                this.setIcon(getTypeIcon(type));
                this.type = type;
                this.addActionListener(this);
            }
            
            /**
             * Converts the identifier to the proper qualification
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent evt) {
                
                Object eventSource = evt.getSource();
                if (eventSource == this) {
                    
                    CodeQualificationMap qualificationMap = ExpressionPanel.this.userResolvedAmbiguities;
                    qualificationMap.putQualification(unqualifiedName, moduleName, type);
                    
                    updateQualifications();
                } 
            }
            
            /**
             * Overwrite tooltip location to always be on the right side of the menu.
             * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
             */
            @Override
            public Point getToolTipLocation(MouseEvent e) {
                return new Point(this.getWidth(), 0);
            }
        }
        
        /**
         * Menu item for changing module of the identifier
         * @author Iulian Radu
         */
        private class ModuleChangeMenuItem extends JCheckBoxMenuItem implements ActionListener {
            
            private static final long serialVersionUID = -2341741495125721145L;
            
            private final String unqualifiedName;
            private final ModuleName newModuleName;
            private final SourceIdentifier.Category type;
            
            /**
             * Constructor
             * @param unqualifiedName unqualified name of the identifier
             * @param newModuleName module to which the identifier will be switched by the menu item
             * @param type type of identifier
             * @param active whether this menu item represents the current form of the
             *               identifier
             */
            ModuleChangeMenuItem(String unqualifiedName, ModuleName newModuleName, SourceIdentifier.Category type, boolean active) {
                super(QualifiedName.make(newModuleName, unqualifiedName).getQualifiedName());
                this.unqualifiedName = unqualifiedName;
                this.newModuleName = newModuleName;
                this.type = type;
                this.setIcon(getTypeIcon(type));
                this.setState(active);
                if (!active) {
                    // Only act if not active
                    this.addActionListener(this);
                }
            }
            
            /**
             * Informs all the listeners that a module change is requested
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent evt) {
                
                Object eventSource = evt.getSource();
                if (eventSource == this) {
                    CodeQualificationMap qualificationMap = ExpressionPanel.this.userResolvedAmbiguities;
                    qualificationMap.removeQualification(unqualifiedName, type);
                    qualificationMap.putQualification(unqualifiedName, newModuleName, type);
                    
                    updateQualifications();
                } 
            }
            
            /**
             * Overwrite tooltip location to always be on the right side of the menu.
             * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
             */
            @Override
            public Point getToolTipLocation(MouseEvent e) {
                return new Point(this.getWidth(), 0);
            }
        }
        
        /**
         * Get the popup menu for a clicked identifier
         * @param identifier the identifier selected
         * @return JPopupMenu the popup menu to be displayed; null if no menu for this item
         */
        public JPopupMenu getPopupMenu(AdvancedCALEditor.PositionlessIdentifier identifier) {
             
            // Determine if identifier is an ambiguity or qualification
            CodeAnalyser.AnalysedIdentifier.QualificationType qualificationType = identifier.getQualificationType();
            
            // Qualified symbol ?
            if (qualificationType.isResolvedTopLevelSymbol()) {
                String unqualifiedName = identifier.getName();
                ModuleName moduleName = identifier.getResolvedModuleName();
                if (moduleName == null) {
                    return null;
                }
                SourceIdentifier.Category type = identifier.getCategory();
                boolean mapQualified = (qualificationType == CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedResolvedTopLevelSymbol);
                
                getQualificationsDisplay().selectPanelForIdentifier(identifier);
                JPopupMenu menu = getQualificationPopupMenu(unqualifiedName, moduleName, type, mapQualified);
                menu.addPopupMenuListener(editorMenuFocusListener);
                return menu;
            }
            
            // Ambiguity ? 
            if (qualificationType == CodeAnalyser.AnalysedIdentifier.QualificationType.UnqualifiedAmbiguousTopLevelSymbol) {
                getQualificationsDisplay().selectPanelForIdentifier(identifier);
                JPopupMenu menu = getAmbiguityPopupMenu(identifier.getName(), identifier.getCategory());
                menu.addPopupMenuListener(editorMenuFocusListener);
                return menu;
            }
            
            // This menu only handles ambiguities, and qualified identifiers
            return null;
        }
        
        /**
         * Create menu for a qualification. 
         * This is the menu for a qualification panel, or for a CAL editor identifier which is not an argument. 
         * 
         * @param unqualifiedName
         * @param moduleName
         * @param type
         * @param changeableModule true if identifier can have its module change
         * @return qualification popup menu
         */
        private JPopupMenu getQualificationPopupMenu(String unqualifiedName, ModuleName moduleName, SourceIdentifier.Category type, boolean changeableModule) {
            JPopupMenu menu = new JPopupMenu();
            
            // Add module change items
            
            if (!changeableModule) {
                JCheckBoxMenuItem newItem = new ModuleChangeMenuItem(unqualifiedName, moduleName, type, true);
                newItem.setToolTipText(calEditor.getMetadataToolTipText(unqualifiedName, moduleName, type, moduleTypeInfo));
                newItem.setEnabled(false);
                menu.add(newItem);
                
            } else {
                // This is an unqualified module
                
                List<ModuleName> candidateModules = CodeAnalyser.getModulesContainingIdentifier(unqualifiedName, type, moduleTypeInfo);
                for (final ModuleName newModule : candidateModules) {
                    JCheckBoxMenuItem newItem = new ModuleChangeMenuItem(unqualifiedName, newModule, type, (newModule.equals(moduleName)));
                    newItem.setToolTipText(calEditor.getMetadataToolTipText(unqualifiedName, newModule, type, moduleTypeInfo));
                    menu.add(newItem);
                }
            } 
            
            return menu;
        }
        
        /**
         * Create menu for an ambiguity. 
         * This is the menu for a text identifier which could not be qualified; the options
         * are to qualify to a matching function.
         * 
         * @param unqualifiedName
         * @return ambiguity popup menu
         */
        private JPopupMenu getAmbiguityPopupMenu(String unqualifiedName, SourceIdentifier.Category type) {
            JPopupMenu menu = new JPopupMenu();

            List<ModuleName> candidateModules = CodeAnalyser.getModulesContainingIdentifier(unqualifiedName, type, moduleTypeInfo);
            for (final ModuleName moduleName : candidateModules) {
                JMenuItem newItem = new ToQualificationMenuItem(unqualifiedName, moduleName, type);
                newItem.setToolTipText(calEditor.getMetadataToolTipText(unqualifiedName, moduleName, type, moduleTypeInfo));
                menu.add(newItem);
            }
            
            return menu;
        }
        
        /**
         * Retrieves the icon associated to a given type
         * @param type
         * @return icon associated to type
         */
        private ImageIcon getTypeIcon(SourceIdentifier.Category type) {
            
            if (type == SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD) {
                return POPUP_FUNCTION_ICON;
                
            } else if (type == SourceIdentifier.Category.DATA_CONSTRUCTOR) {
                return POPUP_CONSTRUCTOR_ICON;
                
            } else  if (type == SourceIdentifier.Category.TYPE_CLASS) {
                return POPUP_CLASS_ICON;
                
            } else if (type == SourceIdentifier.Category.TYPE_CONSTRUCTOR) {
                return POPUP_TYPE_ICON;
                
            } else {
                throw new IllegalArgumentException();
            }
        }
    }
    
    /**
     * Interface allowing listeners to be notified that the panel contents
     * have changed.
     * @author Iulian Radu
     */
    public interface PanelChangeListener {
        
        /**
         * Notifies listeners that the panel contents have changed 
         */
        public void panelContentsChanged();
    }
    
    /**
     * Listener for change events on this panel
     */
    private PanelChangeListener panelChangeListener;
    
    /** The icon for the run button. */
    private static final ImageIcon runIcon = new ImageIcon(ExpressionPanel.class.getResource("/Resources/play.gif"));
    
    /** The icon for the delete button. */
    private static final ImageIcon deleteIcon = new ImageIcon(ExpressionPanel.class.getResource("/Resources/delete.gif"));

    /** The label that displays status messages and expression output. */
    private final JLabel statusLabel = new JLabel();

    /** The CAL editor for editing the expression. */
    private final AdvancedCALEditor calEditor;
    
    /** The module in which the CAL code belongs */
    private ModuleTypeInfo moduleTypeInfo;
    
    /** The qualification map for the expression code */
    private CodeQualificationMap qualificationMap;
    
    /** 
     * Qualification map of all identifiers which have been assigned to specific modules,
     * regardless of whether they are still used within the code. The map for the expression
     * code contains only necessary entries from this map, and entries representing
     * unambiguous qualifications.  
     */
    private CodeQualificationMap userResolvedAmbiguities = new CodeQualificationMap();
    
    /** The expression in its fully qualified form */
    private String qualifiedExpressionText;
    
    /** Transfer handler for the editor component */
    private CodeGemEditor.EditorTextTransferHandler editorTransferHandler;
    
    /** The code analyser which inspects the editor text */
    private CodeAnalyser codeAnalyser;
    
    /** The scrollpane that embeds the CAL editor. */
    private final JScrollPane scrollPane;
    
    /** The split pane that holds the editor and qualifications scroll pane */
    private JSplitPane splitPane;
    
    /** The panel showing qualifications */
    private QualificationsDisplay qualificationsDisplay;
    
    /** The button for running the example. */
    private final JButton runButton = new JButton(runIcon);
    
    /** The button for deleting the example. */
    private final JButton deleteButton = new JButton(deleteIcon);
    
    /** The box for holding the run/delete buttons. */
    private final Box buttonBox = Box.createVerticalBox();
    
    /** The navigator owner that uses this expression panel. */
    private final NavFrameOwner owner;

    /** The code syntax listener used for chromacoding */
    private final GemCodeSyntaxListener codeSyntaxListener;
    
    /**
     * Constructs a new expression panel.
     * @param owner the navigator owner of the expression panel
     */
    public ExpressionPanel(NavFrameOwner owner) {
        
        this.owner = owner;

        // set default module to be as defined by perspective
        Perspective perspective = owner.getPerspective();
        this.moduleTypeInfo = perspective.getWorkingModuleTypeInfo();
        
        // Create and initialize components 
        
        this.calEditor = new AdvancedCALEditor(moduleTypeInfo, perspective.getWorkspace());
        this.codeSyntaxListener = new GemCodeSyntaxListener(perspective);
        try {
            this.calEditor.addCALSyntaxStyleListener(codeSyntaxListener);
        } catch (TooManyListenersException e) {
            // The only effect this has on the editor is that custom text coloring does not work;
            // however, the system will be in an erroneous state.
        }
        this.scrollPane = new JScrollPane(calEditor);
        getSplitPane().setPreferredSize(new Dimension(150, 100));
        
        setOpaque(false);
        setLayout(new BorderLayout(5, 5));
        add(getSplitPane(), BorderLayout.CENTER);
        add(buttonBox, BorderLayout.EAST);
        add(statusLabel, BorderLayout.SOUTH);
        
        codeAnalyser = new CodeAnalyser(owner.getTypeChecker(), moduleTypeInfo, false, false);
        editorTransferHandler = new CodeGemEditor.EditorTextTransferHandler(getCALEditor().getTransferHandler(), codeAnalyser);
        editorTransferHandler.setUserQualifiedIdentifiers(userResolvedAmbiguities);
        getCALEditor().setTransferHandler(editorTransferHandler);
        
        // Add popup menu providers
        
        AdvancedCALEditor.IdentifierPopupMenuProvider popupProvider = new PopupProvider();
        getCALEditor().setPopupMenuProvider(popupProvider);
        qualificationsDisplay.setPopupMenuProvider(popupProvider);
        
        // Add listeners to respond to changes in typed code text by updating qualifications
        
        getCALEditor().getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent e) {
                updateQualifications();
            }

            public void insertUpdate(DocumentEvent e) {
                updateQualifications();
            }

            public void removeUpdate(DocumentEvent e) {
                updateQualifications();
            }
        });
        
        final AutoCompleteManager autoCompleteManager = new AutoCompleteManager(this, perspective);
        getCALEditor().addKeyListener(new KeyAdapter() {
        
            @Override
            public void keyPressed(KeyEvent e) {
                // The gesture is control - space - a la Eclipse et al.
                if ((e.isControlDown()) && (e.getKeyCode() == KeyEvent.VK_SPACE)) {
                    // Show the autocomplete popup just underneath the cursor
                    try {
                        autoCompleteManager.showCodeEditorPopupMenu();
                    } catch (AutoCompletePopupMenu.AutoCompleteException ex) {
                        setStatusMessage(NavigatorMessages.getString("NAV_NoAutocompleteAvailable_Message"));
                    }
                }
            }
        });
        
        // Add listener to respond to qualification panel events
        
        qualificationsDisplay.addPanelEventListener(new QualificationsDisplay.PanelEventListener() {
            
            /** Do nothing on icon double click since we do not allow arguments */
            public void panelTypeIconDoubleClicked(QualificationPanel panel) {
            }
            
            /** Show menu at mouse position if module label double clicked */
            public void panelModuleLabelDoubleClicked(QualificationPanel panel, Point mousePoint) {
                JPopupMenu menu = qualificationsDisplay.getPopupMenuProvider().getPopupMenu(panel.getIdentifier());
                if (menu != null) {
                    menu.show(qualificationsDisplay, mousePoint.x, mousePoint.y);
                }
            }
        });
    }
    
    public void setPanelChangeListener(PanelChangeListener listener) {
        this.panelChangeListener = listener;
    }
    
    /**
     * @return split pane displaying cal editor and qualifications
     */
    private JSplitPane getSplitPane() {
        if (splitPane == null) {
            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splitPane.setName("ExampleEditorSplitPane1");
            splitPane.setOneTouchExpandable(true);
            splitPane.setRightComponent(scrollPane);
            splitPane.setLeftComponent(getQualificationsDisplay());
            splitPane.setDividerLocation(175);
        }
        return splitPane;
    }
    
    /**
     * @return the cal editor component used to edit the expression text.
     */
    public AdvancedCALEditor getCALEditor() {
        return calEditor;
    }
    
    /**
     * @return the qualifications display to edit code qualifications
     */
    public QualificationsDisplay getQualificationsDisplay() {
        if (qualificationsDisplay == null) {
            qualificationsDisplay = new QualificationsDisplay(owner.getPerspective().getWorkspace());
        }
        
        return qualificationsDisplay;
    }
    
    /**
     * Sets the expression text to display.
     * @param text the expression text
     */
    public void setExpressionText(String text) {
        if (userResolvedAmbiguities == null) {
            userResolvedAmbiguities = new CodeQualificationMap();
            editorTransferHandler.setUserQualifiedIdentifiers(userResolvedAmbiguities);
        }
        
        calEditor.setText(text);
        calEditor.setCaretPosition(0);  // Ensure that the beginning of the text is visible
        
        updateQualifications();
    }
    
    /**
     * @return the expression text currently displayed
     */
    public String getExpressionText() {
        return calEditor.getText();
    }

    /**
     * Adds a run button with the given tooltip and action listener
     * @param toolTip the tooltip
     * @param listener the action listener
     */
    public void addRunButton(String toolTip, ActionListener listener) {
        runButton.addActionListener(listener);
        runButton.setToolTipText(toolTip);
        buttonBox.add(runButton);
        buttonBox.add(Box.createVerticalStrut(5));
    }

    /**
     * Adds a delete button with the given tooltip and action listener
     * @param toolTip the tooltip
     * @param listener the action listener
     */    
    public void addDeleteButton(String toolTip, ActionListener listener) {
        deleteButton.addActionListener(listener);
        deleteButton.setToolTipText(toolTip);
        buttonBox.add(deleteButton);
        buttonBox.add(Box.createVerticalGlue());
    }
    
    /**
     * Sets the status message to display in the status label.
     * @param message the message to display
     */
    public void setStatusMessage(String message) {
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setText(message);
    }

    /**
     * Runs the example stored in the editor and displays the result in the status label.
     */
    public boolean runExpression() {
        
        if (qualifiedExpressionText.equals("") && (calEditor.getText().length() > 0)) {
            // Expression hasn't been qualified. Do this before running
            updateQualifications();
        } 

        CALExpression expression = new CALExpression(moduleTypeInfo.getModuleName(), calEditor.getText(), qualificationMap, qualifiedExpressionText);
        StringBuilder result = new StringBuilder();
        boolean isValid = EditorHelper.evaluateExpression(owner, expression, result);

        statusLabel.setText(NavigatorMessages.getString("NAV_Result") + " " + result.toString());
        statusLabel.setForeground(isValid ? Color.BLACK : Color.RED);
        
        return isValid;
    }
    
    /**
     * Analyses the editor code, updates qualification map, qualifies expression code,
     * and updates the UI with the analysis results.
     */
    public void updateQualifications() {
        // Qualify expression 
        CompilerMessageLogger messageLogger = new MessageLogger();
        CodeAnalyser.QualificationResults qualificationResults;
        
        qualificationResults = codeAnalyser.qualifyExpression(calEditor.getText(), null, userResolvedAmbiguities, messageLogger);
        
        if (qualificationResults == null) {
            // Expression parsing failed.
            qualifiedExpressionText = calEditor.getText();
            qualificationMap = new CodeQualificationMap();
            calEditor.setSourceIdentifiers(null);
            calEditor.updateAmbiguityIndicators();
            qualificationsDisplay.generateQualificationPanels(qualificationMap, null, moduleTypeInfo);
            
        } else {
            qualifiedExpressionText = qualificationResults.getQualifiedCode();
            qualificationMap = qualificationResults.getQualificationMap();
            calEditor.setSourceIdentifiers(qualificationResults.getAnalysedIdentifiers());
            calEditor.updateAmbiguityIndicators();
            qualificationsDisplay.generateQualificationPanels(qualificationMap, qualificationResults.getAnalysedIdentifiers(), moduleTypeInfo);
            
            // Build up list of variable names for the chromacoder
            List<String> variableNames = new ArrayList<String>();
            for (final CodeAnalyser.AnalysedIdentifier identifier : qualificationResults.getAnalysedIdentifiers()) {
                if (identifier.getCategory() == SourceIdentifier.Category.LOCAL_VARIABLE_DEFINITION &&
                    !variableNames.contains(identifier.getName())) {
                    variableNames.add(identifier.getName());
                }
            }
            Collections.sort(variableNames);
            String[] variableNamesArray = new String[variableNames.size()];
            variableNames.toArray(variableNamesArray);
            codeSyntaxListener.setLocalVariableNames(variableNamesArray);
        }
        
        notifyEditorChanged();
    }
    
    /**
     * Carry out actions needed to insert the specified string into the editor
     * from the auto-complete manager.  
     * 
     * @param backtrackLength
     * @param insertion
     */
    public void insertAutoCompleteString(int backtrackLength, String insertion) {
        
        try {
            // Remove text from editor
            AdvancedCALEditor editor = getCALEditor();
            int caretPosition = editor.getCaretPosition();
            CodeAnalyser.AnalysedIdentifier identifier = editor.getIdentifierAtPosition(caretPosition);
            editor.getDocument().remove (caretPosition - backtrackLength, backtrackLength);
            if ((identifier == null) || (insertion.indexOf('.') < 0)) {
                // Not a qualified (ie: ambiguous) insetion, or cannot locate identifier
                // Do regular insert
                editor.getDocument().insertString(caretPosition - backtrackLength, insertion, null);
                return;
            } else {
                // Qualified completion, and type of the identifier is known
                // So do a smart insert and update the qualification map
                SourceIdentifier.Category identifierCategory = identifier.getCategory();
                QualifiedName rawCompletedName = QualifiedName.makeFromCompoundName(insertion);
                ModuleName moduleName = moduleTypeInfo.getModuleNameResolver().resolve(rawCompletedName.getModuleName()).getResolvedModuleName();
                QualifiedName completedName = QualifiedName.make(moduleName, rawCompletedName.getUnqualifiedName());
                CodeGemEditor.EditorTextTransferHandler.insertEditorQualification(getCALEditor(), completedName, identifierCategory, userResolvedAmbiguities, false);
            }
            
        } catch (BadLocationException e) {
            throw new IllegalStateException("bad location on auto-complete insert");
        }
    }
    
    /**
     * @see org.openquark.gems.client.AutoCompleteManager.AutoCompleteEditor#getEditorComponent()
     */
    public JTextComponent getEditorComponent() {
        return getCALEditor();
    }
    
    /**
     * Notifies panel listeners that the contents have changed
     */
    private void notifyEditorChanged() {
        // Notify the panel listeners of update
        if (panelChangeListener != null) {
            panelChangeListener.panelContentsChanged();
        }
    }
    
    /**
     * Set the module to use, and update code qualifications
     * @param moduleName name of module to use for this expression
     */
    public void setModuleName(ModuleName moduleName) {
        this.moduleTypeInfo = owner.getPerspective().getMetaModule(moduleName).getTypeInfo();
        
        // Create new code analyser and editor transfer handle, since they depend on the current module
        codeAnalyser = new CodeAnalyser(owner.getTypeChecker(), moduleTypeInfo, false, false);
        editorTransferHandler = new CodeGemEditor.EditorTextTransferHandler(getCALEditor().getTransferHandler(), codeAnalyser);
        
        // Module change may affect visibility of qualifications in code, so update them
        if (!calEditor.getText().equals("")) {
            if (userResolvedAmbiguities == null) {
                userResolvedAmbiguities = new CodeQualificationMap();
                editorTransferHandler.setUserQualifiedIdentifiers(userResolvedAmbiguities);
            }
            updateQualifications();
        } else {
            notifyEditorChanged();
        }
    }

    /**
     * Set the qualification map to use for qualifying the expression
     * @param qualificationMap
     */
    public void setQualificationMap(CodeQualificationMap qualificationMap) {
        this.qualificationMap = qualificationMap;
        this.userResolvedAmbiguities = qualificationMap;
        this.editorTransferHandler.setUserQualifiedIdentifiers(userResolvedAmbiguities);
    }
    
    /**
     * @return the qualification map used to qualify expression
     */
    public CodeQualificationMap getQualificationMap() {
        return qualificationMap;
    }
    
    /**
     * @return fully qualified expression text 
     */
    public String getQualifiedExpressionText() {
        return qualifiedExpressionText;
    }
}
