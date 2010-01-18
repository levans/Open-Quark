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
 * CreateMinimalWorkspacePanel.java
 * Creation date: May 25, 2006.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.VaultElementInfo;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;
import org.openquark.util.model.EventListenerList;
import org.openquark.util.ui.LabelProvider;


/**
 * This class implements a reusable panel for specifying the modules creating a minimal workspace.
 *
 * @author Joseph Wong
 */
class CreateMinimalWorkspacePanel extends JPanel {

    private static final long serialVersionUID = -8843872729742066027L;

    /**
     * A JList that adds check-boxes to each row and maintains a set of "checked"
     * values. Implements ItemSelectable so listeners can keep track of which
     * values are selected according to the check boxes.
     * 
     * @author Robin Salkeld
     */
    public static class ListWithCheckBoxes extends JList implements ItemSelectable {
        
        private static final long serialVersionUID = -3360677654265720512L;

        /** The background color of the parent list */
        private static final Color BACKGROUND = SystemColor.text;
        
        /** The label provider used to render the cells */
        private final LabelProvider labelProvider;
        
        /** The set of listeners triggered when checkboxes are set or cleared */
        private final EventListenerList checkboxListeners = new EventListenerList(ItemListener.class);
        
        /** An internal flag for determining whether events should be blocked. */
        private boolean blockEvents = false;
        
        public ListWithCheckBoxes(LabelProvider labelProvider) {
            super();
            this.labelProvider = labelProvider;
            setBackground(BACKGROUND);
            setCellRenderer(new LabelWithCheckBoxCellRenderer());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleMousePressedEvent(e);
                }
            });
        }
        
        /**
         * Returns a list of objects that are selected by the user.
         * @return the checked values
         */
        public Set<ModuleName> getCheckedValues() {
            Set<ModuleName> retval = new HashSet<ModuleName>();
            ListModel model = getModel();
            for (int i = 0, size = model.getSize(); i < size; i++) {
                LabelWithCheckBox checkBox = (LabelWithCheckBox) model.getElementAt(i);
                if (checkBox.isSelected()) {
                    retval.add((ModuleName)checkBox.getObject());
                }
            }
            return retval;
        }
        
        /**
         * {@inheritDoc}
         */
        public void addItemListener(ItemListener l) {
            checkboxListeners.add(l);
        }
        
        /**
         * {@inheritDoc}
         */
        public Object[] getSelectedObjects() {
            return getCheckedValues().toArray();
        }
        
        /**
         * {@inheritDoc}
         */
        public void removeItemListener(ItemListener l) {
            checkboxListeners.remove(l);
        }
        
        /**
         * Builds the valid elements list.
         */
        public void buildList(Set<FieldName> selectedValues, List<ModuleName> allValues) {
            DefaultListModel model = new DefaultListModel();
            for (int i = 0, size = allValues.size(); i < size; i++) {
                Object value = allValues.get(i);
                
                LabelWithCheckBox checkBox = new LabelWithCheckBox(value, labelProvider);
                checkBox.setBackground(BACKGROUND);
                if (selectedValues.contains(value)) {
                    checkBox.setSelected(true);
                }
                
                model.add(i, checkBox);
            }
            setModel(model);
        }
        
        /**
         * Selects all the elements in the list.
         */
        public void selectAll() {
            blockEvents = true;
            DefaultListModel model = (DefaultListModel)getModel();
            for (int i = 0, n = model.getSize(); i < n; i++) {
                LabelWithCheckBox checkBox = (LabelWithCheckBox)model.get(i);
                checkBox.setSelected(true);
            }
            blockEvents = false;
            fireEvent(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, model, ItemEvent.SELECTED));
            repaint();
        }
        
        /**
         * Deselects all the elements in the list.
         */
        public void deselectAll() {
            blockEvents = true;
            DefaultListModel model = (DefaultListModel)getModel();
            for (int i = 0, n = model.getSize(); i < n; i++) {
                LabelWithCheckBox checkBox = (LabelWithCheckBox)model.get(i);
                checkBox.setSelected(false);
            }
            blockEvents = false;
            fireEvent(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, model, ItemEvent.DESELECTED));
            repaint();
        }
        
        /**
         * This event handler is called whenever the mouse button is pressed.
         * @param event
         */
        private void handleMousePressedEvent(MouseEvent event) {
            int index = locationToIndex(event.getPoint());
            if (index >= 0) {
                LabelWithCheckBox checkBox =
                    (LabelWithCheckBox) getModel().getElementAt(index);
                if (checkBox.isEnabled()) {
                    checkBox.setSelected(!checkBox.isSelected());
                }
                repaint();
            }
        }
        
        /**
         * Fires an item state change event to all relevant listeners.
         */
        private void fireEvent(ItemEvent event) {
            if (!blockEvents) {
                for (Iterator<EventListener> iter = checkboxListeners.getListeners(); iter.hasNext();) {
                    ItemListener listener = (ItemListener)iter.next();
                    listener.itemStateChanged(event);
                }
            }
        }
        
        /**
         * A simple panel that emulates a checkbox, with custom node type icons.
         */
        private class LabelWithCheckBox extends JPanel {
            private static final long serialVersionUID = 4609318443869473231L;

            /** Stores a reference to the object */
            private final Object object;
            
            /** Do not display the text with this checkbox */
            private JCheckBox checkBox;
            
            /** Display the node type name and icon with this component */
            private JLabel label;
            
            /** The label provider used to render the object */
            private final LabelProvider labelProvider;
            
            public LabelWithCheckBox(Object object, LabelProvider labelProvider) {
                super(new BorderLayout());
                this.object = object;
                this.labelProvider = labelProvider;
                initializeUI();
            }
            
            /**
             * Returns the object associated with this check box.
             * @return Object
             */
            public Object getObject() {
                return object;
            }
            
            /**
             * Sets the selection state of this check box and fires listeners as needed.
             * @param selected
             */
            public void setSelected(boolean selected) {
                boolean wasSelected = checkBox.isSelected();
                checkBox.setSelected(selected);
                
                if (wasSelected != selected) {
                    int stateChange = (wasSelected ? ItemEvent.DESELECTED : ItemEvent.SELECTED);
                    ItemEvent event = new ItemEvent(ListWithCheckBoxes.this, ItemEvent.ITEM_STATE_CHANGED,
                            getObject(), stateChange);
                    fireEvent(event);
                }
            }
            
            /**
             * Returns the selection state of this check box.
             * @return boolean
             */
            public boolean isSelected() {
                return checkBox.isSelected();
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public void setBackground(Color color) {
                super.setBackground(color);
                if (checkBox != null) {
                    checkBox.setBackground(color);
                }
                if (label != null) {
                    label.setBackground(color);
                }
            }
            
            /**
             * Initializes the UI and add comonents to the panel.  This should be
             * called once only.
             */
            private void initializeUI() {
                checkBox = new JCheckBox();
                add(checkBox, BorderLayout.WEST);
                
                label = new JLabel(labelProvider.getText(object));
                Icon icon = labelProvider.getIcon(object, false);
                if (icon != null) {
                    label.setIcon(icon);
                }
                add(label, BorderLayout.CENTER);
            }
            
            /**
             * {@inheritDoc}
             */
            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                
                label.setEnabled(enabled);
                checkBox.setEnabled(enabled);
            }
            
        }
        
        /**
         * A simple <code>ListCellRenderer</code> that is able to render checkboxes
         * in a JList component.
         */
        private static class LabelWithCheckBoxCellRenderer implements ListCellRenderer {
            
            /**
             * {@inheritDoc}
             */
            public Component getListCellRendererComponent(JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                return (LabelWithCheckBox) value;
            }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            
            DefaultListModel model = (DefaultListModel)getModel();
            for (int i = 0, n = model.getSize(); i < n; i++) {
                LabelWithCheckBox checkBox = (LabelWithCheckBox)model.get(i);
                checkBox.setEnabled(enabled);
            }
        }
    }
    
    /** The list of checkboxes for the user to specify which modules should be the root set of the minimal workspace. */
    private ListWithCheckBoxes fromList;
    
    /** The list for displaying all the modules in the minimal workspace. */
    private JList modulesInMinimalWorkspaceList;
    
    /** The current workspace. */
    private final CALWorkspace workspace;

    /** The button for selecting all the modules in the list of checkboxes. */
    private final JButton selectAllButton = new JButton(GemCutter.getResourceString("CreateMinimalWorkspaceSelectAll"));

    /** The button for deselecting all the modules in the list of checkboxes. */
    private final JButton deselectAllButton = new JButton(GemCutter.getResourceString("CreateMinimalWorkspaceDeselectAll"));
    
    /**
     * Constructs a CreateMinimalWorkspacePanel.
     * @param workspace the current workspace.
     */
    CreateMinimalWorkspacePanel(CALWorkspace workspace) {
        
        super(new GridLayout(2, 0));
       
        if (workspace == null) {
            throw new NullPointerException();
        }
        
        this.workspace = workspace;
        
        ModuleName[] moduleNames = workspace.getModuleNames();
        Arrays.sort(moduleNames);

        Dimension listDimension = new Dimension(130, 130);

        JPanel fromListPanel = new JPanel(new BorderLayout());
        {
            fromList = new ListWithCheckBoxes(new LabelProvider() {
                
                public Icon getIcon(Object item, boolean expanded) {
                    return null;
                }
                
                public String getText(Object item) {
                    return item.toString();
                }
                
                public String getTooltipText(Object item) {
                    return item.toString();
                }
            });
            
            fromList.buildList(Collections.<FieldName>emptySet(), Arrays.asList(moduleNames));
            
            fromList.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    updateModulesInMinimalWorkspaceList();
                }
            });
            
            JScrollPane fromListScrollPane = new JScrollPane(fromList);
            fromListScrollPane.setMinimumSize(listDimension);
            fromListScrollPane.setPreferredSize(listDimension);
            
            fromListPanel.add(fromListScrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new BorderLayout());
            {
                JPanel innerButtonPanel = new JPanel();
                {
                    innerButtonPanel.setLayout(new GridLayout(0, 1));
                    
                    selectAllButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            fromList.selectAll();
                        }
                    });
                    
                    deselectAllButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            fromList.deselectAll();
                        }
                    });
                    
                    innerButtonPanel.add(wrapWithBorder(selectAllButton, 0, 6, 3, 0));
                    innerButtonPanel.add(wrapWithBorder(deselectAllButton, 3, 6, 0, 0));
                }
                buttonPanel.add(innerButtonPanel, BorderLayout.NORTH);
            }
            fromListPanel.add(buttonPanel, BorderLayout.EAST);
            
            fromListPanel.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(0, 0, 12, 0),
                    BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), GemCutter.getResourceString("CreateMinimalWorkspaceSelectModules"))));
        }
        this.add(fromListPanel);
        
        JPanel toListPanel = new JPanel(new BorderLayout());
        {
            modulesInMinimalWorkspaceList = new JList();
            modulesInMinimalWorkspaceList.setModel(new DefaultListModel());
            
            modulesInMinimalWorkspaceList.setCellRenderer(new ListCellRenderer() {
                private final JLabel label = new JLabel();
                private final Font origFont = label.getFont();
                private final Font boldFont = origFont.deriveFont(Font.BOLD);
                
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Pair<ModuleName, Boolean> p = UnsafeCast.<Pair<ModuleName, Boolean>>unsafeCast(value);
                    ModuleName moduleName = p.fst();
                    boolean isUserSelected = (p.snd()).booleanValue();
                    label.setText(moduleName.toSourceText());
                    if (!list.isEnabled()) {
                        label.setForeground(SystemColor.textInactiveText);
                    } else if (isUserSelected) {
                        label.setForeground(SystemColor.textText);
                        label.setFont(boldFont);
                    } else {
                        label.setForeground(SystemColor.textText);
                        label.setFont(origFont);
                    }
                    
                    return label;
                }
            });
            
            JScrollPane toListScrollPane = new JScrollPane(modulesInMinimalWorkspaceList);
            toListScrollPane.setMinimumSize(listDimension);
            toListScrollPane.setPreferredSize(listDimension);
            
            toListPanel.add(toListScrollPane, BorderLayout.CENTER);

            toListPanel.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), GemCutter.getResourceString("CreateMinimalWorkspaceModulesInMinimalWorkspace")));
        }
        this.add(toListPanel);
    }
    
    /**
     * Wraps the specified component with a panel with a empty border of the
     * specified dimensions.
     * 
     * @param component the component to be wrapped.
     * @param top an integer specifying the width of the top, in pixels.
     * @param left an integer specifying the width of the left side, in pixels.
     * @param bottom an integer specifying the width of the right side, in pixels.
     * @param right an integer specifying the width of the bottom, in pixels.
     * @return a new panel containing the component.
     */
    private JPanel wrapWithBorder(Component component, int top, int left, int bottom, int right) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(component, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
        return panel;
    }
    
    /**
     * Updates the list of modules in the minimal workspace.
     */
    private void updateModulesInMinimalWorkspaceList() {
        DefaultListModel model = (DefaultListModel)modulesInMinimalWorkspaceList.getModel();
        model.clear();
        
        CALWorkspace.DependencyFinder depFinder = getDependencyFinder();
        
        List <Pair<ModuleName, Boolean>> items = new ArrayList<Pair<ModuleName, Boolean>>();
        
        SortedSet<ModuleName> rootSet = depFinder.getRootSet();
        SortedSet<ModuleName> importedModulesSet = depFinder.getImportedModulesSet();
        
        for (final ModuleName moduleName : rootSet) {
            items.add(new Pair<ModuleName, Boolean>(moduleName, Boolean.TRUE));
        }
        
        for (final ModuleName moduleName : importedModulesSet) {
            items.add(new Pair<ModuleName, Boolean>(moduleName, Boolean.FALSE));
        }
        
        Collections.sort(items, new Comparator<Pair<ModuleName, Boolean>>() {
            public int compare(Pair<ModuleName, Boolean> a, Pair<ModuleName, Boolean> b) {
                ModuleName aKey = a.fst();
                ModuleName bKey = b.fst();
                return aKey.compareTo(bKey);
            }
        });
        
        for (int i = 0, n = items.size(); i < n; i++) {
            model.addElement(items.get(i));
        }
    }

    /**
     * @return a dependency finder based on the root set specified in {@link #fromList}.
     */
    CALWorkspace.DependencyFinder getDependencyFinder() {
        return workspace.getDependencyFinder(fromList.getCheckedValues());
    }

    /**
     * @return the content of the minimal workspace declaration.
     */
    String getMinimalWorkspaceDeclaration() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        CALWorkspace.DependencyFinder depFinder = getDependencyFinder();
        
        pw.println("// This CAL workspace declaration file is automatically generated at " + new Date());
        
        pw.println();
        pw.println("// imported modules");
        for (final ModuleName moduleName : depFinder.getImportedModulesSet()) {
            pw.println(getWorkspaceDeclarationLine(moduleName));
        }

        pw.println();
        pw.println("// root modules");
        for (final ModuleName moduleName : depFinder.getRootSet()) {
            pw.println(getWorkspaceDeclarationLine(moduleName));
        }
        
        pw.flush();
        
        return sw.toString();
    }
    
    /**
     * Returns a line for a workspace declaration based on the given module name.
     * @param moduleName
     * @return a line for a workspace declaration.
     */
    private String getWorkspaceDeclarationLine(ModuleName moduleName) {
        VaultElementInfo vaultInfo = workspace.getVaultInfo(moduleName);
        if (vaultInfo instanceof VaultElementInfo.Basic) {
            return ((VaultElementInfo.Basic)vaultInfo).toDeclarationString();
        } else {
            return "StandardVault " + moduleName;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        fromList.setEnabled(enabled);
        selectAllButton.setEnabled(enabled);
        deselectAllButton.setEnabled(enabled);
        modulesInMinimalWorkspaceList.setEnabled(enabled);
        modulesInMinimalWorkspaceList.repaint(); // to force the repainting of this list with disabled labels
    }
}
