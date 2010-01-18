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
 * TableTopPanel.java
 * Creation date: February 13th 2003
 * By: Ken Wong
 */

package org.openquark.gems.client.explorer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.CodeGem;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.ValueGem;
import org.openquark.gems.client.ValueGemChangeEvent;
import org.openquark.gems.client.ValueGemChangeListener;
import org.openquark.gems.client.valueentry.ValueEditor;
import org.openquark.gems.client.valueentry.ValueEditorDirector;
import org.openquark.gems.client.valueentry.ValueEditorEvent;
import org.openquark.gems.client.valueentry.ValueEditorHierarchyManager;
import org.openquark.gems.client.valueentry.ValueEditorListener;
import org.openquark.gems.client.valueentry.ValueEntryPanel;


/**
 * The cell editor for the explorer tree. Displays a text field to edit gem names or a value entry
 * panel to edit value gem and part input values.
 * @author Ken Wong
 */
class ExplorerTreeCellEditor implements TreeCellEditor {

    /** The explorer owner that will provide all the logic for this IdentifierTextField */
    private final TableTopExplorerOwner explorerOwner;

    /** The TableTopExplorer where this field will appear */
    private final TableTopExplorer tableTopExplorer;
    
    /** List of cell editor listeners. Currently not used */
    private final List<CellEditorListener> cellEditorListeners = new ArrayList<CellEditorListener>();
    
    /** The component currently being used as the cell editor. */
    private Component editorComponent = null;

    /** The value gem, if applicable, currently being edited by the editor component */ 
    private ValueGem editorValueGem = null;
    
    /** The gem definition listener used when the editor component is editing a value gem */
    private ValueGemChangeListener editorValueGemListener = null;
    
    /** The user object currently being edited. */
    private Object userObject = null;
    
    /** The tree the editor component is for. */
    private JTree tree = null;
    
    /**
     * Constructor for a new ExplorerTreeCellEditor.
     * @param explorer the explorer this editor is for
     */
    ExplorerTreeCellEditor(TableTopExplorer explorer) {

        if (explorer == null) {
            throw new NullPointerException();
        }
        
        this.tableTopExplorer = explorer;
        this.explorerOwner = explorer.getExplorerOwner();
    }
    
    /**
     * @see javax.swing.CellEditor#addCellEditorListener(CellEditorListener)
     */
    public void addCellEditorListener (CellEditorListener cellEditorListener) {
        cellEditorListeners.add(cellEditorListener);
    }

    /**
     * @see org.openquark.gems.client.explorer.ExplorerTreeCellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
     */
    public void removeCellEditorListener(CellEditorListener l) {
        cellEditorListeners.remove(l);
    }
        
    /**
     * @see javax.swing.CellEditor#stopCellEditing()
     */
    public boolean stopCellEditing() {

        if (editorComponent instanceof ValueEditor) {
            removeValueEditor((ValueEditor)editorComponent, true);
            
        } else if (editorComponent instanceof ExplorerGemNameEditor) {
            ExplorerGemNameEditor nameField = (ExplorerGemNameEditor) editorComponent;
            nameField.stopEditing();
        } else {
            throw new IllegalStateException("invalid type of editor component: " + editorComponent);
        }

        return true;
    }
    
    /**
     * @see javax.swing.CellEditor#cancelCellEditing()
     */
    public void cancelCellEditing() {

        if (editorComponent instanceof ValueEditor) {
            removeValueEditor((ValueEditor)editorComponent, false);
            
        } else if (editorComponent instanceof ExplorerGemNameEditor) {
            ((ExplorerGemNameEditor) editorComponent).cancelEditing();
            
        } else {
            throw new IllegalStateException("invalid type of editor component: " + editorComponent);
        }
    }
    
    private void removeValueEditor(ValueEditor editorComponent, boolean commit) {
        // Remove the gem definition listener if necessary
        if (editorValueGem != null && editorValueGemListener != null) {
            editorValueGem.removeValueChangeListener(editorValueGemListener);
            editorValueGem = null;
            editorValueGemListener = null;
        }

        ValueEditorHierarchyManager vehm = tableTopExplorer.getValueEditorHierarchyManager();
        
        // Under some circumstances this method can be called twice.
        // Therefore only remove the value editor if it really is managed by the hierarchy manager.            
        if (vehm.getTopValueEditors().contains(editorComponent)) {
            vehm.removeValueEditor(editorComponent, commit);
        }
    }
    
    /**
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() { 
        return userObject;
    }
    
    /**
     * @see javax.swing.CellEditor#isCellEditable(EventObject)
     */
    public boolean isCellEditable(EventObject e) {
        
        TreePath path = tableTopExplorer.getExplorerTree().getSelectionPath();
        if (path == null) {
            return false;
        }
        
        DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = defaultMutableTreeNode.getUserObject();
        boolean hasValueEditorManager = explorerOwner.getValueEditorManager() != null;

        if (userObject instanceof Gem.PartInput) {
            
            Gem.PartInput input = (Gem.PartInput) userObject;
            
            return hasValueEditorManager && !input.isBurnt() && 
                   explorerOwner.canEditInputsAsValues() && 
                   explorerOwner.getValueNode(input) != null;
        }
        
        return userObject instanceof CodeGem ||
               userObject instanceof CollectorGem ||
               (userObject instanceof ValueGem && hasValueEditorManager);
    }
    
    /**
     * @see javax.swing.CellEditor#shouldSelectCell(EventObject)
     */
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }
    
    /**
     * @see org.openquark.gems.client.explorer.ExplorerTreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        this.userObject = node.getUserObject();
        this.tree = tree;
        this.editorComponent = null;
        
        if (userObject instanceof ValueGem) {
            ValueGem valueGem = (ValueGem)userObject;
            Gem connectedGem = valueGem.getOutputPart().getConnectedGem();
            int connectedInputNum = valueGem.isConnected() ? valueGem.getOutputPart().getConnection().getDestination().getInputNum() : -1;
            QualifiedName entityName = null;
            if (connectedGem instanceof FunctionalAgentGem) {
                entityName = ((FunctionalAgentGem)connectedGem).getName();
            }
                
            ValueEditorDirector ved = tableTopExplorer.getValueEditorDirector();
            ValueEditor valueEditor = ved.getRootValueEditor(
                                                tableTopExplorer.getValueEditorHierarchyManager(),
                                                valueGem.getValueNode(),
                                                entityName,
                                                connectedInputNum,
                                                connectedGem == null ? null : tableTopExplorer.getMetadataRunner(connectedGem));
            setupEditorComponentForValueGem(valueEditor, valueGem);
            setupValueEditor(node, valueEditor);
        
        } else if (userObject instanceof Gem.PartInput) {
            Gem.PartInput input = (Gem.PartInput)userObject;
            Gem gem = input.getGem();
            QualifiedName entityName = null;
            if (gem instanceof FunctionalAgentGem) {
                entityName = ((FunctionalAgentGem)gem).getName();
            }
                
            ValueEditorDirector ved = tableTopExplorer.getValueEditorDirector();
            ValueEditor valueEditor = ved.getRootValueEditor(
                                                tableTopExplorer.getValueEditorHierarchyManager(),
                                                input.getType(),
                                                entityName,
                                                input.getInputNum(),
                                                tableTopExplorer.getMetadataRunner(gem));
            setupEditorComponentForPartInput(valueEditor, input);
            setupValueEditor(node, valueEditor);
        
        } else if (userObject instanceof CodeGem || userObject instanceof CollectorGem) {
            final Gem editedGem = (Gem)userObject;
            final ExplorerGemNameEditor nameEditor = explorerOwner.getGemNameEditor(editedGem); 
            editorComponent = nameEditor.getComponent();
            

            // Add a focus listener to commit the changes when the field loses focus.
            editorComponent.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    stopCellEditing();
                    tableTopExplorer.refreshForRename(editedGem);
                }
            });
        
            // Add a key listener to commit the changes when the user presses 'Enter' and cancel on 'ESC'.
            editorComponent.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    
                    if (keyCode == KeyEvent.VK_ENTER) {
                        // Stop the cell editing which will commit any changes to the value.  Follow this
                        // by ensuring the that JTree itself switches out of editing mode and refreshes
                        // to reflect any changes
                        stopCellEditing();
                        tableTopExplorer.getExplorerTree().stopEditing();
                        tableTopExplorer.refreshForRename(editedGem);
                    } else if (keyCode == KeyEvent.VK_ESCAPE) {
                        cancelCellEditing();
                        tableTopExplorer.refreshForRename(editedGem);
                    }
                }
            });

            /* We can only get focus if the component is actually visible on
             * the screen. It only gets created here, so it wont be visible
             * until later. So we invoke requestFocus later once the
             * component is visible.
             */
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    nameEditor.getComponent().requestFocusInWindow();
                }
            });            
        }
        
        if (editorComponent != null) {
            
            // We have to invoke this later since request focus has no effect
            // if the component is not yet visible.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (editorComponent instanceof ValueEntryPanel &&
                        ((ValueEntryPanel) editorComponent).getDefaultFocusComponent() != null) {
                            
                        ((ValueEntryPanel) editorComponent).getDefaultFocusComponent().requestFocusInWindow();
                    
                    } else {
                        editorComponent.requestFocusInWindow();
                    }
                }
            });
        }

        return editorComponent;
    }
    
    /**
     * Sets up a a value entry panel by adding it to the value editor hierarchy and configuring it to be displayed
     * in the explorer tree as an editor.
     * @param node the tree node the panel is editing
     * @param valueEditor the panel to configure
     */
    private void setupValueEditor(DefaultMutableTreeNode node, ValueEditor valueEditor) {

        ValueEditorHierarchyManager valueEditorHierarchyManager = tableTopExplorer.getValueEditorHierarchyManager();
        valueEditorHierarchyManager.addTopValueEditor(valueEditor);

        // Make the panel as wide as the visible part of the tree.
        Dimension preferredSize = valueEditor.getPreferredSize();
        preferredSize.width = tree.getVisibleRect().width - tree.getPathBounds(new TreePath(node.getPath())).x - 50;
        valueEditor.setPreferredSize(preferredSize);

        valueEditor.revalidate();
    }

    private void setupEditorComponentForValueGem(final ValueEditor valueEditor, final ValueGem valueGem) {
        // Set the current editor component to be the value editor specified
        editorComponent = valueEditor;
        editorValueGem = valueGem;

        // Create and add a listener for value change events so we can cancel the editor
        editorValueGemListener = new ValueGemChangeListener() {
                        public void valueChanged(ValueGemChangeEvent e) {
                            tree.cancelEditing();
                        }
                    };
        valueGem.addValueChangeListener(editorValueGemListener);

        // Create a listener so that we can do some extra work when committing the value
        ValueEditorListener vel = new ValueEditorListener() {
                public void valueChanged(ValueEditorEvent evt) {
                }
                
                public void valueCommitted(ValueEditorEvent evt) {
                    // HACK: We have to remove the listener so that it doesn't cancel editing
                    // as a result of our own change.
                    valueGem.removeValueChangeListener(editorValueGemListener);
                    
                    ValueEditor valueEditor = ((ValueEditor)editorComponent); 
                    ValueNode vn = valueEditor.getValueNode();
                    explorerOwner.changeValueNode(valueGem, vn);
                    valueEditor.changeOwnerValue(explorerOwner.getValueNode(valueGem));

                    valueGem.addValueChangeListener(editorValueGemListener);
                }
                
                public void valueCanceled(ValueEditorEvent evt) {
                }
        };

        valueEditor.addValueEditorListener(vel);
        valueEditor.setContext(explorerOwner.getValueEditorContext(valueGem));
    }

    private void setupEditorComponentForPartInput(final ValueEditor valueEditor, final Gem.PartInput input) {
        // Set the current editor component to be the value editor specified
        editorComponent = valueEditor;

        // Get a value node for the input and set it into the value editor as an initial value
        valueEditor.setOwnerValueNode(explorerOwner.getValueNode(input));

        // Register a listener so that we can do some extra work when committing the value
        valueEditor.addValueEditorListener(new ValueEditorListener() {
                public void valueChanged(ValueEditorEvent evt) {
                }
                
                public void valueCommitted(ValueEditorEvent evt) {
                    explorerOwner.changeValueNode(input, ((ValueEditor)editorComponent).getValueNode());
                
                    ValueNode valueNode = explorerOwner.getValueNode(input);
                    if (valueNode != null) {
        
                        // todoFW: What is the correct behaviour in this case? 
                        // Sometimes the returned value will be null.
                        // This should really never happen, but since the client
                        // restores the tree anyway it is ok, since we will never use this VEP again,
                        // so therefore we don't need to change the owner value node.
                        
                        ((ValueEditor)editorComponent).changeOwnerValue(valueNode);
                    }
                }
                
                public void valueCanceled(ValueEditorEvent evt) {
                }
        });
    }
}
