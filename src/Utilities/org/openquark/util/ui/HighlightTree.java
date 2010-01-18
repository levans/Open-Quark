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
 * HighlightTree.java
 * Creation date: Jun 8, 2004.
 * By: Edward Lam
 */
package org.openquark.util.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * A subclass of JTree that can display a background image and a separator during drag-and-drop.
 * @author Edward Lam
 */
public abstract class HighlightTree extends UtilTree implements Autoscroll {
    
    /** The shared DnD handler that rejects all drop operations */
    protected static final RejectDropDnDHandler REJECT_DROP_HANDLER = new RejectDropDnDHandler();

    /** The Insets used by getAutoscrollInsets(). */
    private static final Insets AUTOSCROLL_INSETS = new Insets(8, 8, 8, 8);
    
    /** If non-null, the background image to display in the tree. */
    private ImageIcon backgroundImage;

    /** This menu will be displayed when the right mouse click occurs away from any of the tree nodes. */
    private JPopupMenu defaultContextMenu;
    
    /** The number of milliseconds to wait before expanding a path */
    private int expansionDelay = 500; // default: 500 ms
    
    /** The drop target listener responsible for creating highlights in the tree. */
    private final HighlightTreeDropTargetListener dropTargetListener;

    /**
     * A special <code>HighlightTreeDnDHandler</code> that rejects all drop operations.
     */
    private static class RejectDropDnDHandler implements HighlightTreeDnDHandler {
        
        /** {@inheritDoc} */
        public boolean canAcceptDataFlavor(DataFlavor[] flavors) {
            return false;
        }

        /** {@inheritDoc} */
        public boolean doDrop(Transferable transferable, Point mousePoint,
                TreePath path, boolean forceDialog) {
            return false;
        }

        /** {@inheritDoc} */
        public boolean doDrop(Transferable transferable, TreePath dropNodePath,
                boolean upperHalf, boolean onIcon) {
            return false;
        }

        /** {@inheritDoc} */
        public int getDropActions(DataFlavor[] transferFlavors, int dropAction,
                TreePath path, Point location) {
            return 0;
        }
        
        /** {@inheritDoc} */
        public TreePath getPathForDrop(DropTargetDragEvent dtde) {
            return null;
        }
    }

    /**
     * The drop target listener for the highlight tree.
     * @author Edward Lam
     */
    private class HighlightTreeDropTargetListener implements DropTargetListener {
        /** Flag to ignore events which result from drag-over events. */
        private boolean trackingDrop = false;

        /** The path on which the current drag operation will drop. */
        private TreePath pathForDrop = null;

        /** The current highlighter for the highlight tree. */
        private TreeHighlighter highlighter = null;

        /** A timer that expand the saved selection path when time is up! */
        private Timer expandTimer;
        
        /** A reference to the tree path that will be expanded */
        private TreePath expandPath;
        
        /**
         * @see java.awt.dnd.DropTargetListener#dragEnter(DropTargetDragEvent)
         */
        public void dragEnter(DropTargetDragEvent dtde) {
            // Decide if this drag should be accepted based on its data flavors
            if (getHighlightTreeDnDHandler().canAcceptDataFlavor(dtde.getCurrentDataFlavors())) {
                pathForDrop = getHighlightTreeDnDHandler().getPathForDrop(dtde);
                highlighter = createHighlighter();
                
                dtde.acceptDrag(dtde.getDropAction());

            } else {
                dtde.rejectDrag();
            }
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragExit(DropTargetEvent)
         */
        public void dragExit(DropTargetEvent dte) {
            // Reset the timer since the mouse moving away from the drop zone
            resetTimer();
            
            if (highlighter != null) {
                highlighter.restore();
                highlighter = null;
            }
        }

        /**
         * @see java.awt.dnd.DropTargetListener#drop(DropTargetDropEvent)
         */
        public void drop(DropTargetDropEvent dtde) {
            
            Point location = dtde.getLocation();

            TreePath path = getClosestPathForLocation(location.x, location.y);

            if (path != null) {
                int dropActions = getHighlightTreeDnDHandler().getDropActions(dtde.getCurrentDataFlavors(), dtde.getDropAction(), path, location);

                if (dropActions != DnDConstants.ACTION_NONE) {
                    dtde.acceptDrop(dropActions);
                    
                    highlighter.restore ();
                    
                    highlighter = null;

                    Transferable t = dtde.getTransferable();

                    if (pathForDrop != null) {
                        // Interpret a 'copy' drop as the user requesting a dialog to appear even if it isn't
                        // strictly necessary
                        boolean forceDialog = (dtde.getDropAction() & DnDConstants.ACTION_COPY) != 0;

                        Point mousePoint = new Point (dtde.getLocation());
                        dtde.dropComplete(getHighlightTreeDnDHandler().doDrop(t, mousePoint, pathForDrop, forceDialog));

                    } else {
                        TreeGapHighlighter.HitTestInfo hitTestInfo = TreeGapHighlighter.hitTest(HighlightTree.this, location);
                        
                        if (hitTestInfo != null) {
                            TreePath dropNodePath = getPathForRow(hitTestInfo.row);
                            dtde.dropComplete(getHighlightTreeDnDHandler().doDrop(t, dropNodePath, hitTestInfo.upperHalf, hitTestInfo.inIcon));
                        }
                    }
                    
                    return;
                }
            }

            dtde.rejectDrop();
        }
        
        /**
         * Stops the timer if it is currently running and reset all expansion timer
         * related variables. 
         */
        private void resetTimer() {
            if (expandTimer != null) {
                expandTimer.stop();
            }
            expandTimer = null;
            expandPath = null;
        }
        
        /**
         * Starts the timer that will expand the given tree path.
         * @param expandThisPath
         */
        private void startTimer(final TreePath expandThisPath) {
            // If the tree path points to a leaf node, then there is no need
            // to start a timer
            TreeNode node = (TreeNode) expandThisPath.getLastPathComponent();
            if (node.getChildCount() == 0) {
                return;
            }

            // Otherwise, start a timer.  Note that this timer might get reset
            // if the expansion path is changed.
            expandTimer = new Timer(getPathExpansionDelay(), new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    expandPath(expandThisPath);
                    resetTimer();
                }
            });
            expandTimer.setCoalesce(true);
            expandTimer.setRepeats(false);
            expandTimer.start();
            expandPath = expandThisPath;
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dragOver(DropTargetDragEvent)
         */
        public void dragOver(DropTargetDragEvent dtde) {
            Point location = dtde.getLocation();

            TreePath path = getClosestPathForLocation(location.x, location.y);

            if (path == null) {
                dtde.rejectDrag();
                return;
            }
            
            // If the new path is different from the expansion path that is associated
            // with the current timer, then kills the timer
            if (expandPath != null && !expandPath.equals(path)) {
                resetTimer();
            }
            
            // Start the timer to expand the path that is closest to the current
            // mouse location.  Do not start a new timer again if the expansion
            // path is not changed!
            if (expandTimer == null) {
                startTimer(path);
            }

            trackingDrop = true;

            updateForDrag(dtde);
            
            // The highlighter can be null if we don't support any of the drag flavours present during
            // the drag enter event
            if (highlighter != null) {
                // TEMP: if the drop path is non-null, adjust the location so that it is in the center of the path bounds.
                if (pathForDrop != null) {
                    Rectangle bounds = getPathBounds(pathForDrop);
                    location = new Point((int)bounds.getCenterX(), (int)bounds.getCenterY());
                }
                highlighter.showHighlight(location);
            }

            trackingDrop = false;

            int possibleDropActions = getHighlightTreeDnDHandler().getDropActions(dtde.getCurrentDataFlavors(), dtde.getDropAction(), path, location);

            if (possibleDropActions != DnDConstants.ACTION_NONE) {
                dtde.acceptDrag(possibleDropActions);
            } else {
                dtde.rejectDrag();
            }
        }

        /**
         * Update the highlighter state based on the current drag.
         */
        private void updateForDrag(DropTargetDragEvent dtde) {
            TreePath newPathForDrop = getHighlightTreeDnDHandler().getPathForDrop(dtde);
            if (pathForDrop != newPathForDrop) {
                
                if (highlighter != null) {
                    highlighter.eraseHighlight();
                }

                pathForDrop = newPathForDrop;
                highlighter = createHighlighter();
            }
        }

        /**
         * @see java.awt.dnd.DropTargetListener#dropActionChanged(DropTargetDragEvent)
         */
        public void dropActionChanged(DropTargetDragEvent dtde) {
            dtde.acceptDrag(dtde.getDropAction());         
        }

        public boolean isTrackingDrop() {
            return trackingDrop;
        }

        private TreeHighlighter createHighlighter () {
            if (pathForDrop != null) {
                return new TreeNodeHighlighter (HighlightTree.this);
            } else {
                return new TreeGapHighlighter (HighlightTree.this);
            }
        }
        
        void eraseHighlight () {
            if (highlighter != null) {
                highlighter.eraseHighlight();
            }
        }
    }

    /**
     * Constructor for HighlightTree.
     */
    public HighlightTree() {
        this(new DefaultTreeModel(null));
    }

    /**
     * Constructor for HighlightTree.
     */
    public HighlightTree(DefaultTreeModel model) {
        super(model);

        getSelectionModel().setSelectionMode (TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        this.setCellRenderer(new DefaultHighlightTreeCellRenderer());

        // Prevent the component from drawing its own background
        // so that a background image can be drawn.
        this.setOpaque(false);
        
        this.dropTargetListener = new HighlightTreeDropTargetListener();
        new DropTarget(this, dropTargetListener);
    }
    
    /**
     * Returns the number of milliseconds to wait before expanding a tree path.
     * @return int
     */
    public int getPathExpansionDelay() {
        return expansionDelay;
    }
    
    /**
     * Sets the number of milliseconds to wait before expanding a tree path.
     * By default, this number is set at 750.  Negative numbers will be treated
     * as zeroes.
     * @param expansionDelay
     */
    public void setPathExpansionDelay(int expansionDelay) {
        this.expansionDelay = Math.max(0, expansionDelay);
    }
    
    /**
     * Returns the drop-and-drop handler for this <code>HighlightTree</code>.
     * Subclasses must override this method to provide a non-null implementation
     * of the <code>HighlightTreeDnDHandler</code> interface.  If a subclass
     * simply wants to disable dropping items on this tree, then return the
     * shared handler instance defined in this class: REJECT_DROP_HANDLER.
     * However, if this is the case, then it might be better for the developer
     * to create its tree implementation from a JTree instead.  
     * @return the drag-and-drop handler of the highlight tree.
     */
    protected abstract HighlightTreeDnDHandler getHighlightTreeDnDHandler();
    
//    public String getToolTipText (MouseEvent event) {
//        // Determine the node at this location in the tree.
//        TreePath path = getClosestPathForLocation (event.getX (), event.getY ());
//
//        if (path != null) {
//            // Add a new node to the nearest node in the tree.
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent ();
//            return node.getUserObject ().toString ();
//        }
//
//        return null;
//    }
    
    /**
     * @see java.awt.dnd.Autoscroll#getAutoscrollInsets()
     */
    public Insets getAutoscrollInsets() {
//        return new Insets (5, 5, 5, 5);
        Rectangle r = getVisibleRect();
        Dimension size = getSize();
        Insets i =
            new Insets(
                r.y + AUTOSCROLL_INSETS.top,
                r.x + AUTOSCROLL_INSETS.left,
                size.height - r.y - r.height + AUTOSCROLL_INSETS.bottom,
                size.width - r.x - r.width + AUTOSCROLL_INSETS.right);
                
        return i;
    }

    /**
     * @see java.awt.dnd.Autoscroll#autoscroll(java.awt.Point)
     */
    public void autoscroll(Point location) {
        Dimension size = getSize();
        
        int left   = Math.max(location.x - AUTOSCROLL_INSETS.left, 0),
            right  = Math.min (location.x + AUTOSCROLL_INSETS.right, size.width),
            top    = Math.max(location.y - AUTOSCROLL_INSETS.top, 0),
            bottom = Math.min (location.y + AUTOSCROLL_INSETS.bottom, size.height);
            
        if (left >= right || top >= bottom) {
            return;
        }
            
        Rectangle rect = new Rectangle (left, top, right - left, bottom - top);
                                        
        Rectangle visibleRect = getVisibleRect();
        
        if (!visibleRect.contains(rect)) {                                        
            dropTargetListener.eraseHighlight();
    
            scrollRectToVisible(rect);
        }
    }
    
    /* (non-Javadoc)
     * @see java.awt.Component#processMouseEvent(java.awt.event.MouseEvent)
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        
        if (e.isPopupTrigger()) {
            // If the right-click was not on one of the selected items, then change
            // the selection to be the item at the click location (if any).
            // This will allow multiple-selection to be preserved when right-clicking.
            TreePath treePath = getPathForLocation(e.getX(), e.getY());
            if (treePath == null || !isPathSelected(treePath)) {
                setSelectionPath(treePath);
            }

            // Check whether there is a pop-up menu for the current selection.
            JPopupMenu menu = getContextMenu(treePath);
            if (menu != null) {
                menu.show(this, e.getX(), e.getY());
            }
            else if (defaultContextMenu != null){
                // If the click isn't over a node in the tree, then display
                // a popup menu with options for the outline view.
                defaultContextMenu.show(this, e.getX(), e.getY());
            }
        }
    }
    
    /**
     * Get the popup menu corresponding to a given path.
     * @param invocationPath the path on which the popup menu was invoked.
     * @return the corresponding popup menu, or null to show the default popup menu.
     */
    protected JPopupMenu getContextMenu(TreePath invocationPath) {
        return null;
    }

    /**
     * Sets the context menu to be displayed when the mouse is clicked
     * away from any of the tree nodes.
     */
    public void setDefaultContextMenu(JPopupMenu defaultContextMenu) {
        this.defaultContextMenu = defaultContextMenu;
    }

    protected abstract boolean canDropOnIcon(int row);

    /**
     * Method isTrackingDrop.
     * @return boolean
     */
    public boolean isTrackingDrop() {
        return dropTargetListener.isTrackingDrop();
    }
    
    /**
     * Returns the image to be displayed in the background of the outline view.
     */
    public ImageIcon getBackgroundImage() {
        return backgroundImage;
    }

    /**
     * Sets the image to be displayed in the background of the outline view.
     */
    public void setBackgroundImage(ImageIcon image) {
        this.backgroundImage = image;
        repaint();
    }

    /**
     * @see javax.swing.JComponent#paintComponent(Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Paint oldPaint = g2.getPaint();
        g2.setPaint(getBackground());

        try {
            // Fill in the background of the component.
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw the background image, if any.
            ImageIcon backgroundImage = getBackgroundImage();
            if (backgroundImage != null) {

                final int imageX;
                final int imageY;

                // If the tree is contained within a scroll pane, then keep the background
                // image in the center of the view.
                Component parent = getParent();
                if (parent instanceof JViewport) {
                    JViewport viewport = (JViewport) parent;
                    Rectangle viewRect = viewport.getViewRect();

                    imageX = viewRect.x + (viewRect.width - backgroundImage.getIconWidth()) / 2;
                    imageY = viewRect.y + (viewRect.height - backgroundImage.getIconHeight()) / 2;
                }
                else {
                    imageX = (getWidth() - backgroundImage.getIconWidth()) / 2;
                    imageY = (getHeight() - backgroundImage.getIconHeight()) / 2;
                }

                g2.drawImage (backgroundImage.getImage(),
                              imageX,
                              imageY,
                              backgroundImage.getIconWidth(),
                              backgroundImage.getIconHeight(),
                              getBackground(),
                              null);
            }
        }
        finally {
            g2.setPaint(oldPaint);
        }

        super.paintComponent(g);
    }
}