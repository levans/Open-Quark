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
 * TableTopExplorer.java
 * Creation date: Dec 21st 2002
 * By: Ken Wong
 */
package org.openquark.gems.client.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEdit;

import org.openquark.gems.client.BurnEvent;
import org.openquark.gems.client.BurnListener;
import org.openquark.gems.client.CodeGem;
import org.openquark.gems.client.CodeGemDefinitionChangeEvent;
import org.openquark.gems.client.CodeGemDefinitionChangeListener;
import org.openquark.gems.client.CodeGemEditor;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.Connection;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.GemGraphAdditionEvent;
import org.openquark.gems.client.GemGraphChangeListener;
import org.openquark.gems.client.GemGraphConnectionEvent;
import org.openquark.gems.client.GemGraphDisconnectionEvent;
import org.openquark.gems.client.GemGraphRemovalEvent;
import org.openquark.gems.client.InputChangeEvent;
import org.openquark.gems.client.InputChangeListener;
import org.openquark.gems.client.NameChangeEvent;
import org.openquark.gems.client.NameChangeListener;
import org.openquark.gems.client.RecordCreationGem;
import org.openquark.gems.client.RecordFieldSelectionGem;
import org.openquark.gems.client.ReflectorGem;
import org.openquark.gems.client.ValueGem;
import org.openquark.gems.client.ValueGemChangeEvent;
import org.openquark.gems.client.ValueGemChangeListener;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.valueentry.MetadataRunner;
import org.openquark.gems.client.valueentry.ValueEditorDirector;
import org.openquark.gems.client.valueentry.ValueEditorHierarchyManager;


/**
 * This gem explorer presents a tree-based interface for viewing and manipulating the gem graph. Note that this is currently,
 * primarily a UI class. The actual manipulation of the gem graph is delegated to the 'TableTopExplorerOwner'. We implement
 * GemGraphChangeListener, NameChangeListener, CodeGemDefinitionChangeListener, so that we can keep up with changes to the GemGraph. 
 * @author Ken Wong
  */
public class TableTopExplorer extends JComponent implements GemGraphChangeListener {
    
    private static final long serialVersionUID = -4416110823508003865L;

    /** The namespace for log messages from this package. */
    public static final String EXPLORER_LOGGER_NAMESPACE = TableTopExplorer.class.getPackage().getName();
    
    /** An instance of a Logger for messages from the explorer package. */
    static final Logger EXPLORER_LOGGER = Logger.getLogger(EXPLORER_LOGGER_NAMESPACE);

    /** The owner of the explorer, which completes most of the heavy duty work. */
    private TableTopExplorerOwner explorerOwner;
    
    /** The tree that is currently displayed. Note that this tree is reconstructed at each update */
    private ExplorerTree explorerTree;
    
    /** Whether or not mouse input for the tree is enabled. */
    private boolean mouseInputEnabled = true;
    
    /** Mouse listeners added to the tree before mouse input was disabled. */
    private MouseListener[] savedMouseListeners = new MouseListener[0];
    
    /** Mouse motion listeners added to the tree before mouse input was disabled. */
    private MouseMotionListener[] savedMouseMotionListeners = new MouseMotionListener[0];

    /** A listener which updates the tree on various gem events. */
    private final GemChangeListener gemChangeListener = new GemChangeListener();
    
    /** The root node of the TableTopExplorer tree */
    private ExplorerRootNode explorerTreeRoot;
    
    /**
     * Map from Gem to its tree node.
     */
    private Map<Gem, ExplorerGemNode> gemToNodesMap;
    
    /**
     * Map from a PartInput to its tree node.
     */
    private Map<PartInput, ExplorerInputNode> inputsToNodeMap;
    
    /**
     * If the selection in the explorer tree changes this is the old selection path.
     * If there is no old selection path it will equal the new/current path.
     */
    private TreePath oldSelectionPath = null;
    
    /** The ValueEditorHierarchyManager for editors in the explorer.*/
    private ValueEditorHierarchyManager valueEditorHierarchyManager;

    /** Wraps the explorer tree to ensure proper scrolling */
    private final JScrollPane treeScrollPane;
        
    /**
     * This listener selects the appropriate gem on the table top, if the user
     * selects a gem in the tree.
     */
    private TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {

        public void valueChanged(TreeSelectionEvent e) {

            if (explorerTree.isEditing()) {
                explorerTree.cancelEditing();
                return;
            }

            oldSelectionPath = e.getOldLeadSelectionPath();
            TreePath newSelection = e.getNewLeadSelectionPath();
            
            if (oldSelectionPath == null) {
                oldSelectionPath = newSelection;
            }

            if (newSelection == null) { 
                explorerOwner.selectGem(null, true);

            } else {
                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)newSelection.getLastPathComponent();

                // unfortunately, removing a node causes it to be selected in a value changed event!
                if (!node.getRoot().equals(explorerTree.getModel().getRoot())) {
                    return;
                }
                
                
                if (node instanceof ExplorerGemNode) {
                    ExplorerGemNode gemNode = (ExplorerGemNode) node;
                    explorerOwner.selectGem(gemNode.getGem(), true);
                
                } else if (node instanceof ExplorerInputNode) {
                    ExplorerInputNode inputNode = (ExplorerInputNode) node;
                    explorerOwner.selectGem(inputNode.getPartInput().getGem(), true); 
                
                } else {
                    explorerOwner.selectGem(null, true);
                }
                
                if (newSelection.getLastPathComponent() != explorerTreeRoot) {
                    explorerTree.scrollPathToVisible(newSelection);
                }
            }
        }
    };
    
    /**
     * This listener displays the explorer popup menu if the right-mouse button is pressed.
     */
    private MouseListener mouseListener = new MouseAdapter() {

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopupMenu(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopupMenu(e);
        }

        /**
         * Displays the popup menu for a mouse event if the event is a popup trigger.
         * @param e the MouseEvent
         */
        private void maybeShowPopupMenu(MouseEvent e) {
            
            if (e.isPopupTrigger()) {

                TreePath treePath = getExplorerTree().getPathForLocation(e.getX(), e.getY());

                // If the right-click was not on one of the selected items, then change
                // the selection to be the item at the click location (if any).
                // This will allow multiple-selection to be preserved when right-clicking.
                if (treePath == null || !getExplorerTree().isPathSelected(treePath)) {
                    getExplorerTree().setSelectionPath(treePath);
                }

                JPopupMenu popupMenu = getPopup(treePath);
                if (popupMenu != null) { 
                    popupMenu.show(getExplorerTree(), e.getX(), e.getY());
                }
            }
        }
        
        /**
         * Gets the correct popup menu from the explorer owner.
         * @param triggeredPath the path to the tree node on which the context menu was invoked (may be null)
         * @return JPopupMenu the popup menu
         */
        private JPopupMenu getPopup(TreePath triggeredPath) {

            // Get the current selection path.
            TreePath[] treePaths = getExplorerTree().getSelectionPaths();
            if (treePaths == null || treePaths.length == 0) {
                return explorerOwner.getPopupMenu();
            }

            // Build a list of the selected nodes and check whether all the nodes are the same type.
            Set<Gem> selectedGems = new LinkedHashSet<Gem>();
            Set<PartInput> selectedInputs = new LinkedHashSet<PartInput>();
            boolean allGemNodes = true;
            boolean allInputNodes = true;

            for (int nodeN = 0, nNodes = treePaths.length; nodeN < nNodes; ++nodeN) {
                Object selectedNode = treePaths[nodeN].getLastPathComponent();

                if (!(selectedNode instanceof ExplorerGemNode)) {
                    allGemNodes = false;
                }

                if (!(selectedNode instanceof ExplorerInputNode)) {
                    allInputNodes = false;
                }

                if (selectedNode instanceof ExplorerGemNode) {
                    ExplorerGemNode gemNode = (ExplorerGemNode) selectedNode;
                    selectedGems.add(gemNode.getGem());
                }
                else if (selectedNode instanceof ExplorerInputNode) {
                    ExplorerInputNode inputNode = (ExplorerInputNode) selectedNode;
                    selectedInputs.add(inputNode.getPartInput());
                }
            }

            // If a mixture of inputs and gems are selected, then change the selection to be
            // the triggered item and determine menu based on this selection.
            if (triggeredPath != null) {
                Object triggeredNode = triggeredPath.getLastPathComponent();
                if ((triggeredNode instanceof ExplorerGemNode && !allGemNodes)
                        || (triggeredNode instanceof ExplorerInputNode && !allInputNodes)) {
                    getExplorerTree().setSelectionPath(triggeredPath);
                    getPopup(triggeredPath);
                }
            }

            // Get the correct popup menu for the type of node.
            if (allGemNodes) {
                return explorerOwner.getPopupMenu(selectedGems.toArray(new Gem[0]));
            }
            else if (allInputNodes) {
                return explorerOwner.getPopupMenu(selectedInputs.toArray(new Gem.PartInput[0]));
            }
            else {
                return explorerOwner.getPopupMenu();
            }
        }
    };
    
    /**
     * A listener on various gem change events.
     * @author Edward Lam
     */
    private class GemChangeListener
        implements NameChangeListener, InputChangeListener, ValueGemChangeListener, BurnListener, 
                   CodeGemDefinitionChangeListener, CodeGemEditor.EditHandler {

        /**
         * {@inheritDoc}
         */
        public void burntStateChanged(BurnEvent e) {
            handleBurnStateChange((Gem.PartInput)e.getSource());
        }
        
        /**
         * {@inheritDoc}
         */
        public void definitionEdited (CodeGemEditor codeGemEditor, Gem.PartInput[] oldInputs, UndoableEdit codeGemEdit) {
            handleCodeGemDefinitionEdit();
        }
        
        /**
         * {@inheritDoc}
         */
        public void nameChanged(NameChangeEvent e) {
            handleNameChange((Gem)e.getSource());
        }
        
        /**
         * {@inheritDoc}
         */
        public void valueChanged(ValueGemChangeEvent e) {
            handleValueChange((ValueGem)e.getSource());
        }

        /**
         * {@inheritDoc}
         */
        public void inputsChanged(InputChangeEvent e) {
            handleInputsChanged((Gem)e.getSource());
        }
        
        /**
         * {@inheritDoc}
         * TODOEL: necessary?  inputsChanged() will also be called.
         */
        public void codeGemDefinitionChanged(CodeGemDefinitionChangeEvent e) {
            handleCodeGemDefinitionChange((CodeGem)e.getSource());
        }

    }

    /**
     * Default constructor for the TableTopExplorer.
     * @param explorerOwner the owner of this table top explorer
     * @param rootNodeName the name of the root node
     */
    public TableTopExplorer(TableTopExplorerOwner explorerOwner, String rootNodeName) {
        this(explorerOwner, rootNodeName, null, null);
    }
    
    /**
     * Default constructor for the TableTopExplorer.
     * @param explorerOwner the owner of this table top explorer
     * @param rootNodeName the name of the root node
     * @param navigationHelper A helper for the explorer tree to control whether nodes can be focussed on
     * @param cellRenderer A customized cell renderer.  If this is null then the default renderer will be
     * used
     */
    public TableTopExplorer(TableTopExplorerOwner explorerOwner,
                            String rootNodeName,
                            ExplorerNavigationHelper navigationHelper,
                            DefaultTreeCellRenderer cellRenderer) {
        if (explorerOwner == null || rootNodeName == null) {
            throw new NullPointerException();
        }
        
        this.explorerOwner = explorerOwner; 
        
        gemToNodesMap = new HashMap<Gem, ExplorerGemNode>();
        inputsToNodeMap = new HashMap<PartInput, ExplorerInputNode>();
        explorerTreeRoot = new ExplorerRootNode(rootNodeName);        

        explorerTree = new ExplorerTree(explorerTreeRoot, explorerOwner, this, navigationHelper, cellRenderer);

        explorerTree.addTreeSelectionListener(treeSelectionListener);
        explorerTree.addMouseListener(mouseListener);
        explorerTree.setDropTarget(new DropTarget(explorerTree, new ExplorerDragAndDropHandler(this)));
        explorerTree.setAutoscrolls(true);  

        // Create a scroll pane around the tree
        treeScrollPane = new JScrollPane(explorerTree);
        treeScrollPane.setPreferredSize(new Dimension(500,500));
        
        // Add the scroll pane as the centre component for the table top explorer
        this.setLayout(new BorderLayout());
        this.add(treeScrollPane, BorderLayout.CENTER);
        
        rebuildTree();
    }

    /**
     * Scrolls the explorer tree to make the desired bounds visible.  This is done by calling
     * scrollRectToVisible() on the explorer trees scroll pane viewpoint.
     * @param bounds Rectangle - The rectangle that should be visible after scrolling.
     */
    public void scrollExplorerTreeToVisible(Rectangle bounds) {
        treeScrollPane.getViewport().scrollRectToVisible(bounds);        
    }

    /**
     * Builds the tree from the roots.
     * @param roots the set of root nodes
     * @return JTree the explorer tree
     */
    public JTree growTrees(Set<Gem> roots) {
        
        List<Gem> sortedRoots = new ArrayList<Gem>(roots);

        explorerTreeRoot.removeAllChildren();
        
        // Iterate through the set of roots and grow the trees
        for (final Gem root : sortedRoots) {
            explorerTreeRoot.add(growTree(root));
        }
        
        getExplorerTree().setVisible(true);
        return getExplorerTree();
    }
    
    /**
     * Creates a new explorer node and prepares the node appropriately by adding various listeners to keep the node
     * updated. This function should always be used in place of the ExplorerGemNode constructor if the created node
     * is to be used in the TableTopExplorer.
     * @param gem the gem for which to create the node
     * @return the new node
     */
    private ExplorerGemNode createNewExplorerGemNode(Gem gem) {
        gem.addInputChangeListener(gemChangeListener);
        
        if (gem instanceof CollectorGem) {
            gem.addNameChangeListener(gemChangeListener);
        
        } else if (gem instanceof CodeGem) {
            gem.addNameChangeListener(gemChangeListener);
            gem.addInputChangeListener(gemChangeListener);
            ((CodeGem)gem).addDefinitionChangeListener(gemChangeListener);
            
        } else if (gem instanceof RecordFieldSelectionGem) {
            gem.addNameChangeListener(gemChangeListener);
           
        } else if (gem instanceof RecordCreationGem) {
            gem.addInputChangeListener(gemChangeListener);
            gem.addNameChangeListener(gemChangeListener);
        
        } else if (gem instanceof ReflectorGem) {
            gem.addInputChangeListener(gemChangeListener);
        
        } else if (gem instanceof ValueGem) {
            ((ValueGem)gem).addValueChangeListener(gemChangeListener);

        } else if (gem instanceof FunctionalAgentGem) {
            ((FunctionalAgentGem)gem).addNameChangeListener(gemChangeListener);
        }
        
        gem.addBurnListener(gemChangeListener);
        
        ExplorerGemNode newNode = new ExplorerGemNode(gem);
        gemToNodesMap.put(gem, newNode);
        return newNode;
    }
    
    /**
     * Creates a new ExplorerInputNode and preps it for the tree.
     * @param input the input to create the node for
     * @return the new node
     */
    private ExplorerInputNode createNewExplorerGemNode(Gem.PartInput input) {
        ExplorerInputNode newNode = new ExplorerInputNode(input);
        inputsToNodeMap.put(input, newNode);
        return newNode;
    }
    
    /**
     * Fires a node structure changed event to the tree model and saves/restores the
     * tree state before and after firing the event.
     * @param node the node that has changed
     */
    private void fireNodeStructureChanged(TreeNode node) {
        getExplorerTree().saveState();
        getExplorerTreeModel().nodeStructureChanged(node);
        getExplorerTree().restoreSavedState();
    }
    
    /**
     * Expands the given node.
     * @param node the node to be expanded
     */
    private void expandNode(DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        explorerTree.expandPath(path);
    }
    
    /**
     * A recursive method that grows each individual gem tree.
     * @param gem the gem that is the root of the tree to grow
     * @return ExplorerGemNode the root node for the tree
     */
    ExplorerGemNode growTree(Gem gem) {
        
        ExplorerGemNode node = gemToNodesMap.containsKey(gem) ? gemToNodesMap.get(gem) : createNewExplorerGemNode(gem);
        
        // We want to show the proper gem inputs too.
        Gem.PartInput[] inputs = gem.getInputParts();
        for (final PartInput input : inputs) {
            
            if (input.isConnected()) {
                Gem sourceGem = input.getConnection().getSource().getGem();
                node.add(growTree(sourceGem));
                
            } else {
                ExplorerInputNode mutableTreeNode = createNewExplorerGemNode(input);
                node.add(mutableTreeNode);        
            }
        }
        
        return node;
    }
    
    /**
     * Called to grow a tree or nodes starting from a part input rather than a gem.
     * @param input the input from which to grow the tree
     * @return ExplorerGemNode the root node for the tree
     */
    ExplorerInputNode growTree(PartInput input) {
        
        ExplorerInputNode node = createNewExplorerGemNode(input);
        
        // We want to show the proper descendants if there are any
        if (input.isConnected()) {
            node.add(growTree(input.getConnection().getSource().getGem()));
        }
        
        return node;
    }
    
    /**
     * @see org.openquark.gems.client.GemGraphChangeListener#gemRemoved(GemGraphRemovalEvent)
     */
    public void gemRemoved(GemGraphRemovalEvent e) {
        
        Gem gem = (Gem)e.getSource();
        
        // If we want to remove a gem, we need to remove all of its inputs from our cache
        // we also need to add its dependents onto the root. (We shouldn't really have to, since the gemGraph should've done
        // disconnections before the deletion. but just to be safe
        ExplorerGemNode node = gemToNodesMap.get(gem);
        
        // This is possible, in the case of unconnected emitters and thus is nothing to worry about.
        if (node == null) {
            return;
        }
        
        for (int i = 0; i < node.getChildCount(); i++) { 
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
            
            // So if it was an input, we remove it from our map, if it's a gem, then we disconect it by adding
            // it onto the root
            if (child instanceof ExplorerInputNode) {
                ExplorerInputNode inputNode = (ExplorerInputNode) child;
                inputsToNodeMap.remove(inputNode.getPartInput());
                i--;
            } else {
                explorerTreeRoot.add(child);
            }
            
            child.removeFromParent();
        }
        
        // We need to recreate the input of this node's parent (There was no input node because that spot was taken up by this gem
        // we go through the inputs and if the input doesn't exist, we create it.
        DefaultMutableTreeNode parent  =(DefaultMutableTreeNode)node.getParent();
        if (parent instanceof ExplorerGemNode) {
            ExplorerGemNode explorerGemNode = (ExplorerGemNode) parent;
            Gem parentGem = explorerGemNode.getGem();
            
            // cycle through the input parts and see if they are all there.
            Gem.PartInput[] inputs = parentGem.getInputParts();
            for (int i = 0; i < inputs.length; i++) {
                // we create a new input only if there's supposed to be an input there!
                if (!inputsToNodeMap.containsKey(inputs[i]) && !inputs[i].isConnected()) {
                    ExplorerInputNode mutableTreeNode = createNewExplorerGemNode(inputs[i]);
                    parent.add(mutableTreeNode);        
                }
            }
        }
        if (parent != null) {
            parent.remove(node);
        }
        
        gemToNodesMap.remove(gem);
        
        fireNodeStructureChanged(explorerTreeRoot);
    }
    
    /**
     * @see org.openquark.gems.client.GemGraphChangeListener#gemDisconnected(GemGraphDisconnectionEvent)
     */
    public void gemDisconnected(GemGraphDisconnectionEvent e) {
        
        Connection connection = (Connection)e.getSource();
        Gem sourceGem = connection.getSource().getGem();
        Gem destinationGem = connection.getDestination().getGem();
        ExplorerGemNode sourceGemNode = gemToNodesMap.get(sourceGem);
        ExplorerGemNode destinationGemNode = gemToNodesMap.get(destinationGem);
        
        // This scope may not be showing the gem that triggered the event so there may be nothing to do.
        if (sourceGemNode == null) {
            return;
        }

        // We add the disconnected node (the source gem) onto the root.
        sourceGemNode.removeFromParent();
        explorerTreeRoot.add(sourceGemNode);
        
        // Add the missing input to the destination gem's node.
        Gem.PartInput[] inputs = destinationGem.getInputParts();
        for (int i = 0; i < inputs.length; i++) {
            if (!inputsToNodeMap.containsKey(inputs[i]) && !inputs[i].isConnected()) {
                ExplorerInputNode inputNode = createNewExplorerGemNode(inputs[i]);    
                inputsToNodeMap.put(inputs[i], inputNode);
                destinationGemNode.insert(inputNode, i);
            }
        }
        
        fireNodeStructureChanged(explorerTreeRoot);
        expandNode(sourceGemNode);
    }
    
    /**
     * @see org.openquark.gems.client.GemGraphChangeListener#gemConnected(GemGraphConnectionEvent)
     */
    public void gemConnected(GemGraphConnectionEvent e) {
        
        Connection connection = (Connection)e.getSource();
        Gem.PartInput destinationInput = connection.getDestination();
        ExplorerGemNode sourceGemNode = gemToNodesMap.get(connection.getSource().getGem());
        ExplorerGemNode destinationGemNode = gemToNodesMap.get(destinationInput.getGem());
        ExplorerInputNode destinationInputNode = inputsToNodeMap.get(destinationInput);

        // If the source gem is not being shown by the explorer then there is nothing to do.        
        if (sourceGemNode == null) {
            return;
        }
            
        // Remove the source gem from it's parent.
        sourceGemNode.removeFromParent();
        
        // Remove the previously unconnected input node. It may have been removed already!
        // This is done for a special reason
        // in a connection to a code gem, where the type of the expression was ambiguous,
        // a definition update event may be generated, and in fact, it is generated before the connection
        // event is posted, but after the connection has been bound. And so, in such cases,
        // the source gem must be disconnected from the root, or whatever it was connected to,
        // before continuing. Consequently, we may not have to disconnect here, as it might have been done
        // already.
        if (destinationInputNode != null) {
            destinationInputNode.removeFromParent();
            inputsToNodeMap.remove(destinationInput);
        }
        
        // Put the gem node in place of the old input node.
        int childIndex = destinationInput.getInputNum();
        destinationGemNode.insert(sourceGemNode, childIndex);
        
        fireNodeStructureChanged(explorerTreeRoot);
        expandNode(sourceGemNode);
    }
    
    /**
     * @see org.openquark.gems.client.GemGraphChangeListener#gemAdded(GemGraphAdditionEvent)
     */
    public void gemAdded(GemGraphAdditionEvent e) {
        handleGemAdded((Gem)e.getSource());
    }
    
    /**
     * Handle the addition of a new gem to the tabletop.
     * Creates the necessary nodes, and adds it onto the root node.
     * @param gem the gem which was added.
     */
    private void handleGemAdded(Gem gem) {
        
        // Create the gem node.
        ExplorerGemNode explorerGemNode = gemToNodesMap.containsKey(gem) ? gemToNodesMap.get(gem) : createNewExplorerGemNode(gem);
        
        // Add the input nodes.
        Gem.PartInput[] inputs = gem.getInputParts();
        for (final PartInput input : inputs) {
            ExplorerInputNode inputNode = createNewExplorerGemNode(input);
            explorerGemNode.add(inputNode);
        }
        
        // Add the node to the tree, and notify.
        explorerTreeRoot.add(explorerGemNode);
        fireNodeStructureChanged(explorerTreeRoot);
        expandNode(explorerGemNode);
    }

    /**
     * Handle a change in the burn state of an input.
     * @param input
     */
    public void handleBurnStateChange(Gem.PartInput input) {
        getExplorerTreeModel().nodeChanged(inputsToNodeMap.get(input));
    }
    
    /**
     * Handle an edit to a code gem's definition.
     */
    public void handleCodeGemDefinitionEdit() {
        // Cancel any editing if the user changes a code gem definition via a code panel.
        getExplorerTree().cancelEditing();
    }
    
    /**
     * Handle a change in the name of a gem.
     * @param gem
     */
    public void handleNameChange(Gem gem) {
        if (!explorerTree.isEditing()) {
            refreshForRename(gem);
        }
    }
    
    /**
     * Handle a change in the value of a value gem.
     * @param valueGem
     */
    public void handleValueChange(ValueGem valueGem) {
        ExplorerGemNode explorerGemNode = gemToNodesMap.get(valueGem);

        // On deleting a value gem, the gem is removed, then its editor is closed. The editor commits on close,
        // but the gem is already gone, meaning that the explorer gem node is no longer in the map.
        if (explorerGemNode == null) {
            return;
        }
        
        // Handle the text of the node changing.
        getExplorerTreeModel().nodeChanged(explorerGemNode);
    }
    
    /**
     * Handle a change in the inputs of a gem.
     * @param gem
     */
    public void handleInputsChanged(Gem gem) {
        ExplorerGemNode explorerGemNode = gemToNodesMap.get(gem);

        // Rebuild the node.
        // TODOEL: do we need to rebuild on disconnect??
        rebuildGemNode(explorerGemNode);
    }
    
    /**
     * Handle a change in the definition of a code gem.
     * @param codeGem
     */
    public void handleCodeGemDefinitionChange(CodeGem codeGem) {
        ExplorerGemNode explorerGemNode = gemToNodesMap.get(codeGem);

        // If the definition changes, then the node may need to update to reflect brokenness
        getExplorerTreeModel().nodeChanged(explorerGemNode);
    }
    
    /**
     * Completely updates the tree structure by rebuilding the tree from the ground up
     */
    public void rebuildTree() {
        
        gemToNodesMap.clear(); 
        inputsToNodeMap.clear();
    
        Set<Gem> roots = explorerOwner.getRoots();
        
        growTrees(roots);
        getExplorerTreeModel().nodeStructureChanged(explorerTreeRoot);
        
        // We expand everything at initially, because it looks better, and is generally what people want.
        for (int i = 0; i < explorerTree.getRowCount(); i++) {
            explorerTree.expandRow(i);
        }
        
        revalidate();
    }
     
    /**
     * @return the explorer tree displayed by this table top explorer
     */
    public ExplorerTree getExplorerTree() {
        return explorerTree;
    }
    
    /**
     * @return the tree model of the explorer tree
     */
    public DefaultTreeModel getExplorerTreeModel() {
        return (DefaultTreeModel) explorerTree.getModel();
    }
   
   /**
    * Returns the tree node for a given gem.
    * @param gem
    * @return DefaultMutableTreeNode
    */
   public DefaultMutableTreeNode getGemNode(Gem gem) {
       return gemToNodesMap.get(gem);
   }
   
   /**
    * @param input the input to get a node for
    * @return the node for the input
    */
   public DefaultMutableTreeNode getInputNode(Gem.PartInput input) {
       return inputsToNodeMap.get(input);
   }
    
    /**
     * @return the explorer owner of this table top explorer
     */
    TableTopExplorerOwner getExplorerOwner() {
        return explorerOwner;
    }
    
    /**
     * Get the ValueEditorHierarchyManager for editors in the explorer.
     * @return valueEditorHierarchyManager
     */
    public ValueEditorHierarchyManager getValueEditorHierarchyManager() {
        if (valueEditorHierarchyManager == null && explorerOwner.getValueEditorManager() != null) {
            valueEditorHierarchyManager = new ValueEditorHierarchyManager(explorerOwner.getValueEditorManager());

            // Set up the hierarchy so that hierarchy commit/cancel events close the top-level editor.
            valueEditorHierarchyManager.setHierarchyCommitCancelHandler(new ValueEditorHierarchyManager.HierarchyCommitCancelHandler() {
                public void handleHierarchyCommitCancel(boolean commit) {
                    if (commit) {
                        explorerTree.stopEditing();
                    } else {
                        explorerTree.cancelEditing();
                    }
                }
            });
        }
        return valueEditorHierarchyManager;
    }
    
    /**
     * Get the MetadataRunner for editors in the explorer.
     * @param gem The gem for which we wish to run metadata
     * @return a metadata runner object
     */
    public MetadataRunner getMetadataRunner(Gem gem) {
        return explorerOwner.getMetadataRunner(gem);
    }

    /**
     * Get the ValueEditorDirector for editors in the explorer.
     * @return valueEditorDirector
     */
    public ValueEditorDirector getValueEditorDirector() {
        return explorerOwner.getValueEditorManager().getValueEditorDirector();
    }

    /**
     * Refreshes the tree after a rename operation. If the node of the gem that was renamed is
     * a child of the root node, this will re-sort all nodes in the root so that they still are
     * in alphabetical order. Does nothing if the gem node is connected to an input.
     * @param gem the gem that was renamed
     */    
    void refreshForRename(Gem gem) {

        ExplorerGemNode node = gemToNodesMap.get(gem);
        
        if (node.getParent() == explorerTreeRoot) {
            node.removeFromParent();
            explorerTreeRoot.add(node);
            fireNodeStructureChanged(explorerTreeRoot);
        }
    }
    
    /**
     * Rebuilds the given gem node by removing and reading its inputs. This should be called if
     * an inputs was added/removed or if the input order has changed.
     * @param explorerGemNode the gem node to rebuild
     */
    private void rebuildGemNode(ExplorerGemNode explorerGemNode) {
        
        // We get rid of all the 'old' inputs
        while (explorerGemNode.getChildCount() > 0) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) explorerGemNode.getFirstChild();
            child.removeFromParent();
            inputsToNodeMap.remove(child.getUserObject());
        }
        
        // And repopulate with the new ones. (provided that they are not connected)
        Gem gem = explorerGemNode.getGem();
        Gem.PartInput[] partInputs = gem.getInputParts();
        
        for (final PartInput partInput : partInputs) {
            
            if (partInput.isConnected()) {
                
                // Due to the order that the events are handled, it is possible that the gem to which this input is
                // connected has not been added to the tree yet.
                // If this is the case, then a new node will be created for it (even though the tree will likely be
                // rebuilt with the new structure afterwards anyway).
                Gem sourceGem = partInput.getConnectedGem();
                ExplorerGemNode source = gemToNodesMap.containsKey(sourceGem) ? gemToNodesMap.get(sourceGem) : createNewExplorerGemNode(sourceGem);                

                // This is done for a special reason
                // in a connection to a code gem, where the type of the expression was ambiguous,
                // a definition update event may be generated, and in fact, it is generated before the connection
                // event is posted, but after the connection has been bound. And so, in such cases,
                // the source gem must be disconnected from the root, or whatever it was connected to,
                // before continuing.
                explorerGemNode.add(source);

            } else {
                ExplorerInputNode newInputNode = createNewExplorerGemNode(partInput);
                explorerGemNode.add(newInputNode);
            }
        }

        fireNodeStructureChanged(explorerGemNode);
        expandNode(explorerGemNode);
    }
    
    /**
     * Enables and disables the mouse events (such that the explorer can be used purely as a view).
     * @param enabled whether to enable mouse events
     */
    public void enableMouseInputs(boolean enabled) { 
        // Skip out earlier if the state hasn't changed.  This is important since we don't want to add
        // back the mouse listeners everytime this method is called, we only want to add them once when
        // the run state changes from disabled to enabled.
        if (mouseInputEnabled == enabled) {
            return;
        }

        this.mouseInputEnabled = enabled;
        
        // Maybe this should be just disabled?
        // explorerTree.setEnabled(enabled);
        
        if (enabled) {

            for (final MouseListener listener : savedMouseListeners) {
                explorerTree.addMouseListener(listener);
            }

            for (final MouseMotionListener listener : savedMouseMotionListeners) {
                explorerTree.addMouseMotionListener(listener);
            }
            
        } else {

            savedMouseListeners = explorerTree.getMouseListeners();
            savedMouseMotionListeners = explorerTree.getMouseMotionListeners();
            
            for (final MouseListener listener : savedMouseListeners) {
                explorerTree.removeMouseListener(listener);
            }

            for (final MouseMotionListener listener : savedMouseMotionListeners) {
                explorerTree.removeMouseMotionListener(listener);
            }
        }
    }
    
    /**
     * Returns the current status of the explorer
     * @return boolean
     */
    public boolean isMouseEnabled() { 
        return mouseInputEnabled;
    }
    
    /**
     * Selects the correponding gem in the explorer. This allows for parallel selection between the TableTop and the explorer.
     * @param gem
     */
    public void selectGem (Gem gem) {
        
        if (gemToNodesMap != null && gem != null) {
            
            DefaultMutableTreeNode node = gemToNodesMap.get(gem);
            
            if (node != null) {
                TreePath path = new TreePath(node.getPath());
                getExplorerTree().setSelectionPath(path);
            }
            
        } else {
            getExplorerTree().setSelectionPath(null);
        }
    }
    
    /**
     * Selects the correponding gem in the explorer. This allows for parallel selection between the TableTop and the explorer.
     * @param input
     */
    public void selectInput (Gem.PartInput input) {
        if (inputsToNodeMap != null && input != null) {
            DefaultMutableTreeNode node = inputsToNodeMap.get(input);
            if (node != null) {
                TreePath path = new TreePath(node.getPath());
                getExplorerTree().setSelectionPath(path);
            }
        }
    }
    
    /**
     * Selects the correponding gem in the explorer. This allows for parallel selection between the TableTop and the explorer.
     */
    public void selectRoot () {
        TreePath path = new TreePath(explorerTreeRoot.getPath());
        getExplorerTree().setSelectionPath(path);
    }
    
    /**
     * Display the gem name editor associated with the defined gem
     * @param gem
     */
    public void renameGem(Gem gem) {
        ExplorerGemNode node = gemToNodesMap.get(gem);
        explorerTree.startEditingAtPath(new TreePath(node.getPath()));
    }
    
    /**
     * Edit the ValueGem specified in the parameter
     * @param valueGem
     */
    public void editValueGem(ValueGem valueGem) {
        ExplorerGemNode node = gemToNodesMap.get(valueGem);
        explorerTree.startEditingAtPath(new TreePath(node.getPath()));
    }
    
    /**
     * Edit the Gem.PartInput specified in the parameters
     * @param partInput
     */
    public void editPartInput(Gem.PartInput partInput) {
        
        if (!explorerOwner.canEditInputsAsValues()) {
            throw new IllegalStateException("Editing PartInput is not supported");
        }
        
        ExplorerInputNode node = inputsToNodeMap.get(partInput);
        explorerTree.startEditingAtPath(new TreePath(node.getPath()));
    }
    
    /**
     * This returns the old selection path, if there is one. This is useful for determining
     * if the user clicked on a part-input and the selection immediately changes to the input's
     * gem because that gem became selected on the table top.
     * @return the old selection path for the explorer tree
     */
    public final TreePath getOldSelectionPath() {
        return oldSelectionPath;
    }
}


