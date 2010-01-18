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
 * ExplorerDragAndDropHandler.java
 * Creation date: Jan 3rd 2002
 * By: Ken Wong
 */
package org.openquark.gems.client.explorer;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.openquark.cal.services.GemEntity;
import org.openquark.gems.client.AutoburnLogic;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.GemEntitySelection;
import org.openquark.gems.client.InputTransferable;
import org.openquark.gems.client.ReflectorGem;
import org.openquark.gems.client.SingleGemEntityDataFlavor;
import org.openquark.util.UnsafeCast;



/**
 * This is the drag and drop handler for the explorer
 * todoKW: Add support for the Ctrl-Drag clone gesture.
 * @author Ken Wong
 * Creation Date: Jan 6th 2003
 */
class ExplorerDragAndDropHandler implements DragGestureListener, DragSourceListener, DropTargetListener {
    /** the explorer we're dealing with */
    private TableTopExplorer tableTopExplorer; 
    
    /** Whether or not we are currently handling a drag operation */
    private boolean inDragMode = false;

    /**
     * Constructor for ExplorerDragAndDropHandler.
     * @param tableTopExplorer
     */
    public ExplorerDragAndDropHandler(TableTopExplorer tableTopExplorer) {
        super();
        this.tableTopExplorer = tableTopExplorer;
        // create the drag gesture recognizer for this tree.
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(tableTopExplorer.getExplorerTree(), 
                DnDConstants.ACTION_MOVE, this);
    }

    /**
     * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(DragGestureEvent)
     */
    public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
        
        if (!tableTopExplorer.getExplorerOwner().isDNDEnabled()) {
            return;
        }
        
        //todoKW: Add drag image for this drag gesture
        inDragMode = true;
        
        Point dragOrigin = dragGestureEvent.getDragOrigin();
        JTree explorerTree = tableTopExplorer.getExplorerTree();
        
        // if the explorer is not enabled for dragging (ie run mode) we just quit
        if (!tableTopExplorer.isMouseEnabled()) {
            return;
        }
        
        // Find the proper node 
        TreePath pathToNode = explorerTree.getPathForLocation(dragOrigin.x, dragOrigin.y);
        if (pathToNode == null) {
            return;
        }
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)pathToNode.getLastPathComponent();
        
        Rectangle pathBounds = explorerTree.getPathBounds(pathToNode);
        Point ptDragOrigin = dragGestureEvent.getDragOrigin();
        Point mousePointOffset = new Point(ptDragOrigin.x - pathBounds.x, ptDragOrigin.y - pathBounds.y);
        
        // If we are dragging the gems...
        if (treeNode instanceof ExplorerGemNode) {
            ExplorerGemNode explorerGemNode = (ExplorerGemNode) treeNode;
            
            Gem gem = explorerGemNode.getGem();
            
            if (gem instanceof CollectorGem) {
                CollectorGem collectorGem = (CollectorGem) gem;
                gem = new ReflectorGem(collectorGem);
            }
            tableTopExplorer.getExplorerOwner().beginUndoableEdit();
            dragGestureEvent.startDrag(null, null, mousePointOffset, new TableTopExplorerSelection(gem, tableTopExplorer), this);
                  
        } 
        else if (treeNode instanceof ExplorerInputNode) {
            ExplorerInputNode inputNode = (ExplorerInputNode) treeNode;
            
            Gem.PartInput input = inputNode.getPartInput();

            tableTopExplorer.getExplorerOwner().beginUndoableEdit();
            dragGestureEvent.startDrag(null, null, mousePointOffset, new InputTransferable(input), this);
        }
    }
    
    /**
     * @see java.awt.dnd.DragSourceListener#dragEnter(DragSourceDragEvent)
     */
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragOver(DragSourceDragEvent)
     */
    public void dragOver(DragSourceDragEvent dsde) {
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dropActionChanged(DragSourceDragEvent)
     */
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragExit(DragSourceEvent)
     */
    public void dragExit(DragSourceEvent dse) {
    }

    /**
     * @see java.awt.dnd.DragSourceListener#dragDropEnd(DragSourceDropEvent)
     */
    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (inDragMode) {
            tableTopExplorer.getExplorerOwner().endUndoableEdit();
            inDragMode = false;
        }
    }
    
    /**
     * Finishes off the drag and drop action by restoring the states of the undo and of the explorer
     * @param e
     * @param accepted
     */
    private void completeDragAndDropAction(DropTargetDropEvent e, boolean accepted) {
        
        if (inDragMode) {

            tableTopExplorer.getExplorerOwner().endUndoableEdit();

            TreePath selectionPath = tableTopExplorer.getExplorerTree().getSelectionPath();
            
            if (selectionPath != null){
            
                Rectangle bounds = tableTopExplorer.getExplorerTree().getPathBounds(selectionPath);
                
                if (bounds != null) {
                    tableTopExplorer.scrollExplorerTreeToVisible(bounds);
                }
            }

            
            if (accepted) { 
                e.acceptDrop(DnDConstants.ACTION_MOVE);
            } else {
                e.rejectDrop();
            }
            
            inDragMode = false;
        }
    }
    
    /**
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    public void dragOver(DropTargetDragEvent dropTargetDragEvent) {
        
        // if the explorer is not ready, then we reject all drag sources!
        if (!tableTopExplorer.isMouseEnabled()) {
            dropTargetDragEvent.rejectDrag();
            return;
        }
        
         // We need to make sure that the dataflavor of the transferable is supported.
        if (!((dropTargetDragEvent.isDataFlavorSupported(TableTopExplorerSingleGemDataFlavor.getSingleGemDataFlavor())) ||  
            (dropTargetDragEvent.isDataFlavorSupported(GemEntitySelection.getEntityListDF())) ||
            (dropTargetDragEvent.isDataFlavorSupported(SingleGemEntityDataFlavor.getSingleGemEntityDataFlavor())))){               
            
            dropTargetDragEvent.rejectDrag();
            return;
        }  
                          
        // We want to highlight the node that the drop would affect                
        Point dragLocation = dropTargetDragEvent.getLocation();
        JTree explorerTree = tableTopExplorer.getExplorerTree();
        TreePath selectedNodePath = explorerTree.getPathForLocation(dragLocation.x, dragLocation.y);
        if (selectedNodePath == null) {
            selectedNodePath = new TreePath(((ExplorerRootNode)explorerTree.getModel().getRoot()).getPath());
        }
        explorerTree.setSelectionPath(selectedNodePath);
        
        DataFlavor[] flavors = dropTargetDragEvent.getCurrentDataFlavors();

        if (selectedNodePath.getLastPathComponent() == explorerTree.getModel().getRoot()) {
            // accept all drops on the root (just disconnection)
            dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_MOVE);
            return;
        }
                
        Object userObject = ((DefaultMutableTreeNode)selectedNodePath.getLastPathComponent()).getUserObject();
        if (!(userObject instanceof Gem.PartInput)) {
            // if it wasn't the root that we are dragging over, and it's not an input,
            // the we don't care what it is. We're rejecting it.
            dropTargetDragEvent.rejectDrag();
            return;                        
        }
        
        // Now we do the type checking and stuff to make sure the drag is acceptable
        
        Gem.PartInput input = (Gem.PartInput)userObject;
        for (final DataFlavor flavor : flavors) {
            
            if (flavor.equals(SingleGemEntityDataFlavor.getSingleGemEntityDataFlavor())) { 

                SingleGemEntityDataFlavor entityDataFlavor = (SingleGemEntityDataFlavor)flavor;
                AutoburnLogic.AutoburnUnifyStatus unifyStatus = tableTopExplorer.getExplorerOwner().canConnect(entityDataFlavor.getGemEntity(), input);
                
                if (unifyStatus.isAutoConnectable()) {
                    dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_MOVE);
                } else {
                    dropTargetDragEvent.rejectDrag();   
                }
                
                return;
                
            } else if (flavor.equals(TableTopExplorerSingleGemDataFlavor.getSingleGemDataFlavor())) {
                
                // The dataflavor actually stores the gem itself! so we can check if the parts are connectable
                TableTopExplorerSingleGemDataFlavor gemDataFlavor = (TableTopExplorerSingleGemDataFlavor)flavor;
                AutoburnLogic.AutoburnUnifyStatus unifyStatus= gemDataFlavor.canConnectTo(input);

                if (unifyStatus.isAutoConnectable()) {
                    dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_MOVE);
                } else {
                    dropTargetDragEvent.rejectDrag();                            
                }   
                
                return;
                 
            } else {
                
                // if for some reason, some other flavor exists, we will reject!
                dropTargetDragEvent.rejectDrag();
            }
        }                                                                              
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    /**
     * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    public void dragExit(DropTargetEvent dte) {
    }

    /**
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    public void drop(DropTargetDropEvent dropTargetDropEvent) {
        // maybe a foreign source
        if (inDragMode == false) {
            tableTopExplorer.getExplorerOwner().beginUndoableEdit();   
            inDragMode = true;
        }
        
        String editName = "";
        
        // if undo support is available, we'll use it.
        
        // get the transferable object
        Transferable transferable = dropTargetDropEvent.getTransferable();
        Gem[] gems = null;
        JTree tree = tableTopExplorer.getExplorerTree();
        TreePath path = tree.getSelectionPath();
        DefaultMutableTreeNode node = (path == null) ? (DefaultMutableTreeNode)tree.getModel().getRoot() : (DefaultMutableTreeNode)path.getLastPathComponent();
        Gem.PartInput input = null;

        // if we are transporting gem entities
        if (transferable.isDataFlavorSupported(GemEntitySelection.getEntityListDF())) {
            // we assume all the type checking and stuff is done in drag over, such that inappropriate drags would have been weeded
            // out by now.
            try{
                
                // so we grab the list of entities
                List<GemEntity> entities = UnsafeCast.unsafeCast(transferable.getTransferData(GemEntitySelection.getEntityListDF()));
                
                // and we add the gems to the tabletop (worry about connection later)
                gems = tableTopExplorer.getExplorerOwner().addGems(entities.toArray(new GemEntity[entities.size()]));
                if (node.getUserObject() instanceof Gem.PartInput) {
                    input = (Gem.PartInput) node.getUserObject();   
                }
                
                if (node instanceof ExplorerRootNode) {
                    editName = ExplorerMessages.getString("TTX_Undo_AddNew");
                } else {
                    editName = ExplorerMessages.getString("TTX_Undo_ConnectNew");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
                completeDragAndDropAction(dropTargetDropEvent, false);
                return;
            } catch (UnsupportedFlavorException unsupportedFlavorException) {
                unsupportedFlavorException.printStackTrace();
                completeDragAndDropAction(dropTargetDropEvent, false);
                return;
            }
            
        } else if (transferable.isDataFlavorSupported(TableTopExplorerSingleGemDataFlavor.getSingleGemDataFlavor())) {
            // if it is a single gem that we are transferring
            try{
                // then we'll identify it too.
                gems = new Gem[1];
                gems[0] = (Gem)transferable.getTransferData(TableTopExplorerSingleGemDataFlavor.getSingleGemDataFlavor());
                
                if (node instanceof ExplorerInputNode) {
                    input = ((ExplorerInputNode)node).getPartInput();
                    editName = ExplorerMessages.getString("TTX_Undo_Connect");
                } else if (node instanceof ExplorerRootNode) {
                    editName = ExplorerMessages.getString("TTX_Undo_Disconnect");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
                completeDragAndDropAction(dropTargetDropEvent, false);
                return;
            } catch (UnsupportedFlavorException unsupportedFlavorException) {
                unsupportedFlavorException.printStackTrace();
                completeDragAndDropAction(dropTargetDropEvent, false);
                return;
            }
            
        } else {
            completeDragAndDropAction(dropTargetDropEvent, false);
            return;
        }
        tableTopExplorer.getExplorerOwner().setUndoableName(editName);

        for (final Gem gem : gems) {
            // now we go through all the gems that we have identified, 
            // disconnect them and connect them to the target            
            Gem.PartOutput output = gem.getOutputPart();
            if (output != null && output.isConnected()) {
                tableTopExplorer.getExplorerOwner().disconnect(output.getConnection());
            }
            
            // dropping a gem onto an input
            if (input != null && output != null) {
                tableTopExplorer.getExplorerOwner().connect(output, input);
            }
        }
        
        // We're done our action, just to show that we accepted the drop
        // select the gem we just dragged.
        tableTopExplorer.selectGem(gems[0]);
        
        completeDragAndDropAction(dropTargetDropEvent, true);
    }
}
 