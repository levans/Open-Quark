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
 * ArgumentTree.java
 * Creation date: May 25, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.gems.client.ArgumentTreeNode.ArgumentNode;
import org.openquark.gems.client.Gem.PartInput;
import org.openquark.gems.client.valueentry.StructuredValueEditor;
import org.openquark.gems.client.valueentry.ValueEditor;
import org.openquark.gems.client.valueentry.ValueEditorEvent;
import org.openquark.gems.client.valueentry.ValueEditorHierarchyManager;
import org.openquark.gems.client.valueentry.ValueEditorListener;
import org.openquark.gems.client.valueentry.ValueEntryPanel;
import org.openquark.gems.client.valueentry.ValueEntryPanel.MirrorValueEntryPanel;
import org.openquark.util.UnsafeCast;
import org.openquark.util.ui.HighlightTree;
import org.openquark.util.ui.HighlightTreeDnDHandler;



/**
 * This class represents the JTree component that is used in the ArgumentExplorer.
 * @author Edward Lam
 */
public class ArgumentTree extends HighlightTree {
    private static final long serialVersionUID = 5069177050123093096L;

    /** The completely-transparent color. */
    private static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

    /** The TableTopExplorerOwner of the TableTopExplorer using this tree. */
    private final ArgumentExplorerOwner owner;
    
    /** Whether or not mouse input for the tree is enabled. */
    private boolean mouseInputEnabled = true;
    
    /** The cell renderer. */
    private final CellRenderer cellRenderer = new CellRenderer(this);

    /** The dnd handler for this tree. */
    private final HighlightTreeDnDHandler dndHandler = new DnDHandler();

    /** The node currently being dragged as a transferable. */
    private ArgumentTreeNode draggingNode = null;

    /**
     * The custom cell renderer for the ArgumentExplorer tree.
     * @author Edward Lam
     */
    public static class CellRenderer extends DefaultTreeCellRenderer {
        private static final long serialVersionUID = 2722279109617670004L;

        /* the various icons used in the tree */
        private static final ImageIcon emitterIcon;
        private static final ImageIcon reflectorIcon;
        private static final ImageIcon targetEmitterIcon;
        private static final ImageIcon targetReflectorIcon;
        private static final ImageIcon partInputIcon;
        private static final ImageIcon unusedInputIcon;
        
        static {
            // Make icon objects
            emitterIcon = new ImageIcon(Object.class.getResource("/Resources/emitter.gif"));
            reflectorIcon = new ImageIcon(Object.class.getResource("/Resources/reflector.gif"));
            targetEmitterIcon = new ImageIcon(Object.class.getResource("/Resources/targetEmitter.gif"));
            targetReflectorIcon = new ImageIcon(Object.class.getResource("/Resources/targetReflector.gif"));
            partInputIcon = new ImageIcon(Object.class.getResource("/Resources/partinput.gif"));
            unusedInputIcon = new ImageIcon(Object.class.getResource("/Resources/unusedInput.gif"));
        }

        /** The argument tree for which rendering takes place. */
        private final ArgumentTree argumentTree;
        
        /** The default font. */
        private final Font defaultFont = getFont();

        /** 
         * A map from input to an editor containing a value for that input. 
         * If non-null, input values are displayed. 
         */
        private Map<PartInput, ValueEditor> inputToArgumentPanelMap = null;
        
        /**
         * @param argumentTree the tree being rendered.
         */
        public CellRenderer(ArgumentTree argumentTree) {
            this.argumentTree = argumentTree;
        }

        /**
         * Get the appropriate small icon to use for the given collector gem.
         * @param collectorGem the collector gem to be represented.
         * @return the corresponding icon.
         */
        public static ImageIcon getCollectorIcon(CollectorGem collectorGem) {
            boolean isTarget = collectorGem.getTargetCollector() == null;
            return collectorGem.getReflectedInputs().isEmpty() ? (isTarget ? targetEmitterIcon : emitterIcon) : 
                                                                 (isTarget ? targetReflectorIcon : reflectorIcon);
        }
        
        /**
         * Get the appropriate small icon to use for the given argument node.
         * @param argumentNode the node for the argument to be represented.
         * @return the corresponding icon.
         */
        public static ImageIcon getInputIcon(ArgumentTreeNode.ArgumentNode argumentNode) {
            Gem.PartInput inputPart = argumentNode.getArgument();
            
            return ((ArgumentTreeNode.CollectorNode)argumentNode.getParent()).getCollectorGem().isReflected(inputPart) ? 
                         partInputIcon : unusedInputIcon;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, 
                                                      boolean leaf, int row, boolean hasFocus) {

            ImageIcon customIcon = null;
            String customText = value.toString();
            
            if (value instanceof ArgumentTreeNode.CollectorNode) {
                CollectorGem collectorGem = ((ArgumentTreeNode.CollectorNode)value).getCollectorGem();
                
                // Show an emitter rather than a collector.
                customIcon = getCollectorIcon(collectorGem);
                customText = collectorGem.getUnqualifiedName();
                
            } else if (value instanceof ArgumentTreeNode.ArgumentNode) {
                ArgumentTreeNode.ArgumentNode argumentNode = (ArgumentTreeNode.ArgumentNode)value;
                Gem.PartInput inputPart = argumentNode.getArgument();

                // Get the input icon.
                customIcon = getInputIcon(argumentNode);
                customText = inputPart.getNameString();
           
                // If we're in run mode, show input values if available.
                if (inputToArgumentPanelMap != null) {
                    ValueEditor inputEditor = inputToArgumentPanelMap.get(inputPart);
                    if (inputEditor != null) {
                        customText += ": " + inputEditor.getValueNode().getTextValue();
                    }
                }

            } else {
                // Unrecognized node type.  What to do?
                // For now just go with the defaults.
            }
            
            // If we're in run mode, change the font.
            if (inputToArgumentPanelMap != null) {
                setFont(new Font("Dialog", Font.BOLD, ValueEntryPanel.PANEL_HEIGHT * 2 / 3));
            } else {
                setFont(defaultFont);
            }
            
            // Always render a dragging node as selected
            sel |= (value == argumentTree.getDraggingNode());
            
            super.getTreeCellRendererComponent(tree, customText, sel, expanded, leaf, row, hasFocus);
            
            if (customIcon != null) {
                setIcon(customIcon);
                setDisabledIcon(customIcon);
            }
            
            return this;
        }

        /**
         * Show or hide argument values.
         * @param inputToArgumentPanelMap a map from input to a value entry panel editing its argument value.
         *   If non-null, argument values will be shown.  If null, argument values will not be shown.
         */
        public void showArgumentValues(Map<PartInput, ValueEditor> inputToArgumentPanelMap) {
            this.inputToArgumentPanelMap = (inputToArgumentPanelMap == null) ? null : new HashMap<PartInput, ValueEditor>(inputToArgumentPanelMap);
        }
    }
    

    /**
     * This is a value editor which encapsulates another value editor, and displays a component to the left of it.
     * @author Edward Lam
     */
    private static class AugmentedValueEditor extends StructuredValueEditor {
        private static final long serialVersionUID = -4156318472919110794L;

        /** The value editor within this value editor. */
        private final ValueEditor simpleValueEditor;

        /**
         * Constructor for an augmented value editor.
         * @param hierarchyManager
         * @param leftComponent the component which will appear on the left of the value editor.
         * @param simpleValueEditor the editor encapsulated by this editor.
         */
        AugmentedValueEditor(ValueEditorHierarchyManager hierarchyManager, JComponent leftComponent, ValueEditor simpleValueEditor) {
            super(hierarchyManager);
            this.simpleValueEditor = simpleValueEditor;

            setOpaque(false);
            setBackground(TRANSPARENT_COLOR);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBorder(BorderFactory.createEmptyBorder());
            
            add(leftComponent);
            add(simpleValueEditor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Component getDefaultFocusComponent() {
            return simpleValueEditor.getDefaultFocusComponent();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setInitialValue() {
            simpleValueEditor.setInitialValue();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void handleElementLaunchingEditor() {
        }
    }
    
    /**
     * The custom cell editor for the ArgumentExplorer tree.
     * @author Edward Lam
     */
    private class CellEditor implements TreeCellEditor {

        /** A list of event listeners for this editor. */
        private final EventListenerList listenerList = new EventListenerList();
        
        /** A change event instance. */
        private final ChangeEvent changeEvent = new ChangeEvent(this);

        /** The hierarchy manager for the display of value editors in run mode. */
        private ValueEditorHierarchyManager hierarchyManager = null;

        /** The editor currently displayed, if an editor is currently displayed. */
        private ValueEditor valueEditor = null;

        /** a map from input to an editor containing a value for that input. */
        private final Map<PartInput, ValueEditor> inputToArgumentPanelMap;

        /**
         * Constructor for the cell editor.
         * @param inputToArgumentPanelMap a map from input to a value entry panel editing its argument value.
         */
        CellEditor(Map<PartInput, ValueEditor> inputToArgumentPanelMap) {
            this.inputToArgumentPanelMap = new HashMap<PartInput, ValueEditor>(inputToArgumentPanelMap);
        }

        /**
         * Get the value editor to being used to display the edit value.
         * @param inputPart the input for which the editor will be returned.
         * @return ValueEditor the editor used to display the value.
         */
        private ValueEntryPanel getArgumentPanel(PartInput inputPart) {
            return (ValueEntryPanel)inputToArgumentPanelMap.get(inputPart);
        }

        /**
         * {@inheritDoc}
         */
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            
            // Just render the input dot.  Otherwise, render the whole cell.
            JComponent cellRendererComponent = new JLabel(CellRenderer.getInputIcon((ArgumentNode)value));
            
            // Set up the value editor stuff.
            ValueEditor mirroredValueEditor = getMirroredEntryPanel((ArgumentTreeNode)value);
            valueEditor = new AugmentedValueEditor(getValueEditorHierarchyManager(), cellRendererComponent, mirroredValueEditor);
            getValueEditorHierarchyManager().addTopValueEditor(valueEditor);
            getValueEditorHierarchyManager().addEditorToHierarchy(mirroredValueEditor, valueEditor);
            
            
            // Make the panel as wide as the visible part of the tree.
            Dimension preferredSize = valueEditor.getPreferredSize();
            preferredSize.width = tree.getVisibleRect().width - tree.getPathBounds(new TreePath(((ArgumentTreeNode)value).getPath())).x;
            valueEditor.setPreferredSize(preferredSize);

            valueEditor.revalidate();

            return valueEditor;
        }
        
        /**
         * @return the hierarchy manager used to instantiate value editors..
         */
        private ValueEditorHierarchyManager getValueEditorHierarchyManager() {
            // Lazily create a hierarchy manager if none yet exists.
            if (hierarchyManager == null) {
                hierarchyManager = new ValueEditorHierarchyManager(owner.getValueEditorManager());
                
                // Set up the hierarchy so that hierarchy commit/cancel events close the top-level editor.
                hierarchyManager.setHierarchyCommitCancelHandler(new ValueEditorHierarchyManager.HierarchyCommitCancelHandler() {
                    public void handleHierarchyCommitCancel(boolean commit) {
                        if (commit) {
                            stopEditing();
                        } else {
                            cancelEditing();
                        }
                    }
                });
            }
            return hierarchyManager;
        }

        /**
         * Get a mirrored entry panel to edit the value for the given argument tree node.
         * @param argumentTreeNode
         * @return a mirrored entry panel that can be used by the argument tree to edit the argument's value.
         */
        private MirrorValueEntryPanel getMirroredEntryPanel(ArgumentTreeNode argumentTreeNode) {

            if (argumentTreeNode instanceof ArgumentTreeNode.ArgumentNode) {

                ArgumentTreeNode.ArgumentNode argumentNode = (ArgumentTreeNode.ArgumentNode)argumentTreeNode;
                Gem.PartInput inputPart = argumentNode.getArgument();

                MirrorValueEntryPanel mirroredEntryPanel = 
                        new ValueEntryPanel.MirrorValueEntryPanel(getValueEditorHierarchyManager(), getArgumentPanel(inputPart));

                // Add a listener to update the node when the value changes..
                mirroredEntryPanel.addValueEditorListener(new ValueEditorListener() {

                    public void valueChanged(ValueEditorEvent evt) {
                    }

                    public void valueCommitted(ValueEditorEvent evt) {
                        // On commit, generate nodeChanged() events for every node.
                        getArgumentTreeModel().targetArgumentNodesChanged();
                    }

                    public void valueCanceled(ValueEditorEvent evt) {
                    }
                });

                return mirroredEntryPanel;
            }

            return null;
        }

        /**
         * Remove the given editor from the hierarchy.
         * @param editorToRemove the editor to remove.
         * @param commit true to commit on remove, false to cancel.
         */
        private void removeValueEditor(ValueEditor editorToRemove, boolean commit) {
            ValueEditorHierarchyManager vehm = getValueEditorHierarchyManager();
            
            // Under some circumstances this method can be called twice.
            // Therefore only remove the value editor if it really is managed by the hierarchy manager.            
            if (vehm.getTopValueEditors().contains(editorToRemove)) {
                vehm.removeValueEditor(editorToRemove, commit);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public Object getCellEditorValue() {
            return valueEditor.getValueNode();
        }

        /**
         * {@inheritDoc}
         */
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                Point eventLocation = ((MouseEvent)e).getPoint();
                
                // True if the cell corresponds to an argument.
                TreePath path = getPathForLocation(eventLocation.x, eventLocation.y);
                if (path != null) {
                    return path.getLastPathComponent() instanceof ArgumentTreeNode.ArgumentNode;
                }
            }
            
            return false;
        }
        
        /**
         * {@inheritDoc}
         */
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        public boolean stopCellEditing() {
            fireEditingStopped();
            if (valueEditor != null) {
                removeValueEditor(valueEditor, true);
            }
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public void cancelCellEditing() {
            fireEditingCanceled();
            if (valueEditor != null) {
                removeValueEditor(valueEditor, false);
            }
        }
        
        /**
         * {@inheritDoc}
         */
        public void addCellEditorListener(CellEditorListener l) {
            listenerList.add(CellEditorListener.class, l);
        }

        /**
         * {@inheritDoc}
         */
        public void removeCellEditorListener(CellEditorListener l) {
            listenerList.remove(CellEditorListener.class, l);
        }

        /**
         * Notify all listeners that have registered interest for notification on this event type. 
         */
        private void fireEditingStopped() {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            
            // Process the listeners last to first, notifying those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    ((CellEditorListener)listeners[i + 1]).editingStopped(changeEvent);
                }
            }
        }

        /**
         * Notify all listeners that have registered interest for notification on this event type. 
         */
        private void fireEditingCanceled() {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            
            // Process the listeners last to first, notifying those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == CellEditorListener.class) {
                    ((CellEditorListener)listeners[i + 1]).editingCanceled(changeEvent);
                }
            }
        }
    }

    /**
     * The class responsible for handling drag source-related updates.
     * @author Edward Lam
     */
    private class DragSourceHandler extends DragSourceAdapter implements DragGestureListener {
        
        /**
         * {@inheritDoc}
         */
        public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
            
            // if the explorer is not enabled for dragging (ie run mode) we just quit
            if (!isMouseEnabled()) {
                return;
            }
            
            // todoEL: Add drag image for this drag gesture
            
            // Find the proper node 
            Point dragOrigin = dragGestureEvent.getDragOrigin();
            TreePath pathToNode = getPathForLocation(dragOrigin.x, dragOrigin.y);
            if (pathToNode == null) {
                return;
            }
            ArgumentTreeNode treeNode = (ArgumentTreeNode)pathToNode.getLastPathComponent();
            
            Rectangle pathBounds = getPathBounds(pathToNode);
            Point mousePointOffset = new Point(dragOrigin.x - pathBounds.x, dragOrigin.y - pathBounds.y);
            
            // If we are dragging the node, start a drag gesture if recognizable.
            if (treeNode instanceof ArgumentTreeNode.ArgumentNode) {
                ArgumentTreeNode.ArgumentNode argumentNode = (ArgumentTreeNode.ArgumentNode)treeNode;
                dragGestureEvent.startDrag(null, null, mousePointOffset, new InputTransferable(argumentNode.getArgument()), this);
                draggingNode = treeNode;
                
            } else if (treeNode instanceof ArgumentTreeNode.CollectorNode) {
                // What to do?  Just create a string for now...
                ArgumentTreeNode.CollectorNode collectorNode = (ArgumentTreeNode.CollectorNode)treeNode;
                dragGestureEvent.startDrag(null, null, mousePointOffset, new StringSelection(collectorNode.getCollectorGem().getUnqualifiedName()), this);
                draggingNode = treeNode;
            }
        }
        
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void dragDropEnd(DragSourceDropEvent dsde) {
            draggingNode = null;
        }
    }
    
    /**
     * The instance of HighlightTreeDnDHandler for this argument tree.
     * @author Edward Lam
     */
    private class DnDHandler implements HighlightTreeDnDHandler {

        /**
         * {@inheritDoc}
         */
        public TreePath getPathForDrop(DropTargetDragEvent dtde) {
            // Can drop on the node if it's a collector node.
            Point dragLocation = dtde.getLocation();
            TreePath pathUnder = getClosestPathForLocation(dragLocation.x, dragLocation.y);

            if (pathUnder != null) {
                Object lastPathComponent = pathUnder.getLastPathComponent();
                if (lastPathComponent instanceof ArgumentTreeNode.CollectorNode) {
                    return pathUnder;

                } else if (lastPathComponent instanceof ArgumentTreeNode.ArgumentNode) {
                    
                    // Two ways to adjust:
                    // If we're dragging an unused argument around its target, highlight its collector.
                    // If we're dragging a targeting argument around the unused argument part of its collector, do the same.
                    
                    // TODOEL: it would be nice to be able to figure out if an argument retargeting from another collector
                    //   would be unused.
                    
                    // Make a copy of the dragging node in case it changes..
                    ArgumentTreeNode draggingNode = ArgumentTree.this.draggingNode;

                    // If not currently dragging an input, return the path.
                    if (!(draggingNode instanceof ArgumentTreeNode.ArgumentNode)) {
                        return null;
                    }
                    ArgumentTreeNode.ArgumentNode draggingArgumentNode = (ArgumentTreeNode.ArgumentNode)draggingNode;
                    
                    // Check if the node being dragged currently an unused argument on that node's target.
                    
                    // If there's no collector relevant to the path, return null.
                    TreePath collectorNodePath = getCollectorNodePath(pathUnder);
                    if (collectorNodePath == null) {
                        return null;
                    }

                    // Get the collector node, and its reflected inputs.
                    ArgumentTreeNode.CollectorNode collectorNode = (ArgumentTreeNode.CollectorNode)collectorNodePath.getLastPathComponent();
                    List<PartInput> reflectedInputs = collectorNode.getCollectorGem().getReflectedInputs();

                    // If it's dragging around the unused argument area of a collector's arguments, return the collector.
                    ArgumentTreeNode.ArgumentNode argumentNodeUnder = (ArgumentTreeNode.ArgumentNode)lastPathComponent;
                    if (!reflectedInputs.contains(argumentNodeUnder.getArgument())) {
                        return collectorNodePath;
                    }
                    
                    // If it's not an argument to this collector, return null.
                    if (draggingArgumentNode.getParent() != collectorNode) {
                        return null;
                    }
                    
                    // It's an argument to this collector.
                    // If it's not a reflected argument, return the collector.
                    if (!reflectedInputs.contains(draggingArgumentNode.getArgument())) {
                        return collectorNodePath;
                    }
                    
                    // It's a reflected argument.
                    return null;
                }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         */
        public boolean canAcceptDataFlavor(DataFlavor[] flavors) {
            // For now, only accept input drags.
            return Arrays.asList(flavors).contains(SingleInputDataFlavor.getSingleInputDataFlavor());
        }

        /**
         * {@inheritDoc}
         */
        public int getDropActions(DataFlavor[] flavors, int dropAction, TreePath path, Point location) {

            // if the explorer is not ready, then we reject all drag sources!
            if (!isMouseEnabled()) {
                return DnDConstants.ACTION_NONE;
            }
            
            // Check that the dataflavor of the transferable is supported.
            if (!canAcceptDataFlavor(flavors)) {
                return DnDConstants.ACTION_NONE;
            }
            
            // Get the collector node path associated with the drag location.
            TreePath collectorNodePath = getCollectorNodePath(path);
            
            // Reject if there is no associated collector.
            if (collectorNodePath == null) {
                return DnDConstants.ACTION_NONE;
            }

            // Get the collector gem from the associated collector node.
            ArgumentTreeNode lastPathComponent = (ArgumentTreeNode)collectorNodePath.getLastPathComponent();
            CollectorGem collectorGem = (CollectorGem)lastPathComponent.getUserObject();
            
            // Ensure the drag is acceptable
            for (final DataFlavor dataFlavor : flavors) {
                
                if (dataFlavor.equals(SingleInputDataFlavor.getSingleInputDataFlavor())) {
                    SingleInputDataFlavor singleInputDataFlavor = (SingleInputDataFlavor)dataFlavor;

                    // Accept only if the argument can be targeted at the collector.
                    if (singleInputDataFlavor.canTarget(collectorGem)) {
                        return DnDConstants.ACTION_MOVE;
                    
                    } else {
                        return DnDConstants.ACTION_NONE;
                    }
                    
                } 
            }
            
            // An acceptable data flavor was not found.
            return DnDConstants.ACTION_NONE;
        }

        /**
         * {@inheritDoc}
         */
        public boolean doDrop(Transferable transferable, Point mousePoint, TreePath path, boolean forceDialog) {
            // This should be a drop on a collector node.
            
            // Get the current node targeted for the drop.
            Object lastPathComponent = path.getLastPathComponent();
            
            // Abort if it's not a collector node.
            if (!(lastPathComponent instanceof ArgumentTreeNode.CollectorNode)) {
                return false;
            }

            CollectorGem collectorGem = ((ArgumentTreeNode.CollectorNode)lastPathComponent).getCollectorGem();
            
            if (transferable.isDataFlavorSupported(SingleInputDataFlavor.getSingleInputDataFlavor())) {
                try {
                    // Grab the input.
                    Gem.PartInput input = (Gem.PartInput)transferable.getTransferData(SingleInputDataFlavor.getSingleInputDataFlavor());
                    
                    // Abort if the input can't be targeted at the given collector gem.
                    if (!SingleInputDataFlavor.canTarget(input, collectorGem)) {
                        return false;
                    }
                    
                    // Do nothing if the input is already targeted at the given collector gem.  But pretend the drop succeeded.
                    if (collectorGem.getTargetArguments().contains(input)) {
                        return true;
                    }
                  
                    // Retarget the argument, and select it in the tree.
                    getOwner().retargetInputArgument(input, collectorGem, -1);
                    selectInput(input);

                    // Success!
                    return true;
                    
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                    return false;

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return false;
        }

        /**
         * {@inheritDoc}
         */
        public boolean doDrop(Transferable transferable, TreePath dropNodePath, boolean upperHalf, boolean onIcon) {
            
            // Get the current node targeted for the drop.
            TreePath collectorNodePath = getCollectorNodePath(dropNodePath);
            
            // Abort if there was no associated collector node.
            if (collectorNodePath == null) {
                return false;
            }
            ArgumentTreeNode.CollectorNode collectorNode = (ArgumentTreeNode.CollectorNode)collectorNodePath.getLastPathComponent();
            CollectorGem collectorGem = collectorNode.getCollectorGem();
            
            if (transferable.isDataFlavorSupported(SingleInputDataFlavor.getSingleInputDataFlavor())) {
                try {
                    // Grab the input.
                    Gem.PartInput input = (Gem.PartInput)transferable.getTransferData(SingleInputDataFlavor.getSingleInputDataFlavor());
                    
                    // Abort if the input can't be targeted at the given collector gem.
                    if (!SingleInputDataFlavor.canTarget(input, collectorGem)) {
                        return false;
                    }
                  
                    // Communicate to the owner that the input should be transferred to its new location.

                    // Figure out the location at which the drop took place.
                    ArgumentTreeNode lastPathComponent = (ArgumentTreeNode)dropNodePath.getLastPathComponent();

                    // Calculate what this corresponds to in terms of the new argument index.
                    int addIndex;
                    if (lastPathComponent instanceof ArgumentTreeNode.CollectorNode) {
                        addIndex = -1;

                    } else if (lastPathComponent instanceof ArgumentTreeNode.ArgumentNode) {
                        ArgumentTreeNode.ArgumentNode argumentNode = (ArgumentTreeNode.ArgumentNode)lastPathComponent;
                        
                        // Get the index of the argument with respect to its collector's arguments.
                        // The add index is the index of the drop argument, modified a bit..
                        addIndex = collectorGem.getTargetArguments().indexOf(argumentNode.getArgument());
                        
                        // If the drop location is in the upper half of the bounds, the drop argument should be added before it.
                        //  Otherwise, it should be added after.
                        if (!upperHalf) {
                            addIndex++;
                        }
                        
                        // Adjust for the case where the input is being retargeted to a higher location on the same collector.
                        if (collectorGem == GemGraph.getInputArgumentTarget(input) && collectorGem.getReflectedInputs().indexOf(input) < addIndex) {
                            addIndex--;
                        }
                        
                    } else {
                        throw new IllegalStateException("Unrecognized drop target class: " + lastPathComponent.getClass());
                    }
                    
                    // Now retarget the argument, and select it in the tree.
                    getOwner().retargetInputArgument(input, collectorGem, addIndex);
                    selectInput(input);

                    // Success!
                    return true;
                    
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                    return false;

                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
    }
    
    /**
     * Constructor for an ArgumentTree
     * @param gemGraph the related GemGraph
     * @param owner
     */
    ArgumentTree(GemGraph gemGraph, ArgumentExplorerOwner owner) {
        this.owner = owner;
        
        // Set our custom model.
        ArgumentTreeNode.RootNode root = new ArgumentTreeNode.RootNode();
        ArgumentTreeModel argumentTreeModel = new ArgumentTreeModel(gemGraph, root);
        
        // Changes are commited on edit interrupt.
        setInvokesStopCellEditing(true);

        // Ensure that inserted nodes are expanded.
        // Note that we add the listener before any other listener can be added.  
        //  This is because the JTree adds its own listeners to update itself on model change, but any added nodes will be 
        //  collapsed by default.  The model listeners are notified in *reverse* order from the order in which they are added, 
        //  meaning that if we add this listener first, it will be notified last, after any new nodes have appeared.
        argumentTreeModel.addTreeModelListener(new TreeModelListener() {

            public void treeNodesChanged(TreeModelEvent e) {
                expandAll(e.getTreePath(), true);
            }

            public void treeNodesInserted(TreeModelEvent e) {
                expandAll(e.getTreePath(), true);
            }

            public void treeNodesRemoved(TreeModelEvent e) {
            }

            public void treeStructureChanged(TreeModelEvent e) {
                expandAll(e.getTreePath(), true);
            }
        });
        setModel(argumentTreeModel);
        
        // Set our custom renderer.
        setCellRenderer(cellRenderer);
        
        // Don't show the root, or root handles for expand/collapse.
        setRootVisible(false);
        setShowsRootHandles(false);
        
        // Register with the tooltip manager.
        ToolTipManager.sharedInstance().registerComponent(this);

        // Allow single selection only.. (for now..)
        DefaultTreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
        treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setSelectionModel(treeSelectionModel);

        // Create the drag gesture recognizer for this tree.
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, new DragSourceHandler());
        
        // Veto collapsing (for now);
        addTreeWillExpandListener(new TreeWillExpandListener() {

            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                throw new ExpandVetoException(event);
            }

            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            }
        });

        // Ensure that the initial view is fully expanded.
        treeDidChange();
        expandRow(0);
        updateUI();
        
        // Set the row height to a negative value so that the cell renderer is used to query for each row's height.
        setRowHeight(-1);
    }
    
    /**
     * @return the ArgumentTreeModel backing this tree.
     */
    ArgumentTreeModel getArgumentTreeModel() {
        return (ArgumentTreeModel)getModel();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected HighlightTreeDnDHandler getHighlightTreeDnDHandler() {
        return dndHandler;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        
        TreePath treePath = getPathForLocation(e.getX(), e.getY());

        if (treePath == null) {
            return null;
        }
        
        ArgumentTreeNode node = (ArgumentTreeNode) treePath.getLastPathComponent();
        Object userObject = node.getUserObject();
        
        if (userObject instanceof Gem.PartInput) {
            return owner.getHTMLFormattedMetadata((Gem.PartInput) userObject);
            
        } else if (userObject instanceof CollectorGem) {
            // Start with the open html tag
            String toolTip = "<html>";
             
            // If available add the metadata short description       
            FunctionMetadata metadata = ((CollectorGem)userObject).getDesignMetadata();
            String shortDescription = metadata.getShortDescription();
            if (shortDescription != null && shortDescription.length() > 0) {
                toolTip += "<p>" +  shortDescription + "</p>";
            }

            // Stick in the result type information
            toolTip += GemCutterMessages.getString("ResultTypeToolTip", owner.getTypeString(((CollectorGem) userObject).getResultType()));            
            
            // End with the end html tag
            toolTip += "</html>";
            return toolTip;
        }
        
        return null;
    }
    
    /**
     * @return any node currently being dragged as a transferable.  Null if none.
     */
    private ArgumentTreeNode getDraggingNode() {
        return draggingNode;
    }
    
    /**
     * Expand or collapse all nodes under a given path.
     * @param parentPath the path to the node under which nodes will be expanded or collapsed.
     * @param expand true to expand, false to collapse.
     */
    private void expandAll(TreePath parentPath, boolean expand) {
        // Expansion or collapsing must be done from the bottom-up.
        // Traverse children first.
        TreeNode parentNode = (TreeNode)parentPath.getLastPathComponent();
        for (Enumeration<TreeNode> e = UnsafeCast.unsafeCast(parentNode.children()); e.hasMoreElements(); ) {
            TreeNode nextChildNode = e.nextElement();
            TreePath childPath = parentPath.pathByAddingChild(nextChildNode);
            expandAll(childPath, expand);
        }
    
        // Now deal with the parent.
        if (expand) {
            expandPath(parentPath);
        } else {
            collapsePath(parentPath);
        }
    }
    
    /**
     * @return the owner.
     */
    public ArgumentExplorerOwner getOwner() {
        return owner;
    }

    /**
     * Display the controls for argument entry.
     * @param inputToArgumentPanelMap a map from input to a value entry panel editing its argument value, 
     *   for those inputs for which the currently running gem requires arguments.  
     *   An iterator on this map should return the inputs in the correct order.
     */
    public void showArgumentControls(Map<PartInput, ValueEditor> inputToArgumentPanelMap) {

        cellRenderer.showArgumentValues(inputToArgumentPanelMap);
        setCellEditor(new CellEditor(inputToArgumentPanelMap));
        
        setRowHeight(ValueEntryPanel.PANEL_HEIGHT);
        setEditable(true);

        // This seems to be the only way to get the tree to repaint itself properly.
        getArgumentTreeModel().nodeStructureChanged((ArgumentTreeNode)getArgumentTreeModel().getRoot());
    }
    
    /**
     * Hide the controls for argument entry.
     */
    public void hideArgumentControls() {
        cellRenderer.showArgumentValues(null);

        setRowHeight(-1);
        setEditable(false);
        
        getArgumentTreeModel().nodeStructureChanged((ArgumentTreeNode)getArgumentTreeModel().getRoot());
    }

    /**
     * Enables and disables the mouse events (such that the explorer can be used purely as a view).
     * @param enabled whether to enable mouse events
     */
    void enableMouseInputs(boolean enabled) { 
        // Skip out earlier if the state hasn't changed.  This is important since we don't want to add
        // back the mouse listeners everytime this method is called, we only want to add them once when
        // the run state changes from disabled to enabled.
        if (mouseInputEnabled == enabled) {
            return;
        }

        this.mouseInputEnabled = enabled;
    }
    
    /**
     * Returns the current status of the explorer
     * @return boolean
     */
    public boolean isMouseEnabled() { 
        return mouseInputEnabled;
    }

    /**
     * Select the given input in the tree.
     * @param inputToSelect the input to select
     */
    void selectInput(Gem.PartInput inputToSelect) {
        ArgumentTreeNode.ArgumentNode argumentNode = getArgumentTreeModel().getArgumentNode(inputToSelect);
        setSelectionPath(new TreePath(argumentNode.getPath()));
    }
    
    /**
     * Get the collector node path which is relevant to a given path
     * @param treePath the path in question.
     * @return the collector node path related to the given tree path.
     */
    TreePath getCollectorNodePath(TreePath treePath) {
        
        if (treePath == null) {
            return null;
        }
        
        Object lastPathComponent = treePath.getLastPathComponent();
        
        // If it's a collector node, return that node.
        if (lastPathComponent instanceof ArgumentTreeNode.CollectorNode) {
            return treePath;
        }
        
        // If it's an argument node, return the parent path (which should be a path to the targeted collector).
        if (lastPathComponent instanceof ArgumentTreeNode.ArgumentNode) {
            return treePath.getParentPath();
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canDropOnIcon(int row) {
        return false;
    }
}
