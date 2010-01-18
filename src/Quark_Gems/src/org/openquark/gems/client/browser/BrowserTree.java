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
 * BrowserTree.java
 * Creation date: (10/29/00 11:16:35 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.browser;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.MetaModule;
import org.openquark.gems.client.GemEntitySelection;
import org.openquark.gems.client.PickListKeyStrokeNavigator;
import org.openquark.gems.client.ToolTipHelpers;
import org.openquark.gems.client.navigator.NavAddress;
import org.openquark.gems.client.navigator.NavFrameOwner;
import org.openquark.gems.client.utilities.MouseClickDragAdapter;
import org.openquark.util.UnsafeCast;
import org.openquark.util.ui.UtilTree;


/**
 * Extension of JTree adding drag source features.
 * Also added are pop up menus which enable sorting capabilities.
 * @author Luke Evans
 */
public class BrowserTree extends UtilTree {
    
    private static final long serialVersionUID = -283997771683405356L;

    /** 
     * todoSN - A JDK 1.4 problem (see java bug #4485987, 4669873) periodically freezes 
     *          the drag image and makes it unusable.  The problem seems to be related to the 
     *          calls to paintImmediately() and drawImage() in the drop target version of dragOver()
     *          in the DnD handler (both here in the BrowserTree and in the TableTop).
     *          The drag image can be enabled/disabled by setting this flag so testing to see
     *          if the problem is fixed, supposedly for 1.4.1, should be trivial.
     * 
     * A boolean to turn drag images on/off when dragging from the gem browser. Enabling the drag image
     * will disable the drag cursor, while disabling the drag image will enable the drag cursor.
     */
    private static final boolean DRAG_IMAGE_ENABLED = false;
    
    /** The display name given to modules with "" (no name) as their name. */
    public static final String DEFAULT_MODULE_NAME = "<default>";

    /** Whether the system supports drag images natively (otherwise we must render our own). */
    private static final boolean DRAG_IMAGE_SUPPORTED = DragSource.isDragImageSupported();
    
    /** The handler for drag and drop operations. */
    private final DragAndDropHandler dragAndDropHandler;

    /** Rendering hints - render at highest quality*/
    private static final RenderingHints RENDERING_HINTS_HIGH_QUALITY = new RenderingHints(null);
    
    /** Used by saveState() and restoreSavedState().
     *  The saved selected node and its siblings, in the order that they appear as children of the parent.
     *  If the selected node is not displayed after the model is reloaded this list will be used to choose 
     *  the closest(/best) one which does. */
    private List<Object> savedChildrenList;
    
    static {
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        RENDERING_HINTS_HIGH_QUALITY.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    /** The BrowserTreeActions object associated with this tree. */
    private final BrowserTreeActions browserTreeActions = new BrowserTreeActions(this);

    /** The navigator owner that is used for editing/viewing metadata. */
    private NavFrameOwner navigatorOwner;

    /** The popup menu provider for this tree. */
    private PopupMenuProvider popupMenuProvider;

    /** Flag to indicate whether or not the Gems in the Tree are draggable.*/
    private boolean isDraggable;
    
    /** Flag to indicate whether or not type expressions should be displayed in the tree. */
    private boolean displayTypeExpr;
    
    /** 
     * Whether the current module should be highlighted in a different colour. 
     * Note: Should this flag be in the cell renderer instead? 
     */
    private boolean highlightCurrentModule;

    /** A list to hold any LeafNodeTriggeredListeners that are interested in receiving events for this BrowserTree 
     * Use a synchronized list, as the collection may be added-to by different threads..*/
    private final List<LeafNodeTriggeredListener> leafNodeTriggeredListeners = new Vector<LeafNodeTriggeredListener>();

    
    /**
     * Handler for drag and drop events.
     * Creation date: (03/15/2002 2:03:35 PM)
     * @author Edward Lam
     */
    private class DragAndDropHandler implements DragGestureListener, DragSourceListener, DropTargetListener {

        /** The image that shows up when dragging. */
        private BufferedImage dragImage = null;
                
        /** The offset of the mouse from the image origin while dragging. */
        private final Point mousePointOffset = new Point(0, 0);
        
        /** The bounds of the last drawn drag ghost. */
        private final Rectangle lastGhostRect = new Rectangle();
        
        /** The base image (a black arrow) used to build images for cursors. */
        private Image baseCursorImage = null;
        
        /** The drag and drop cursor to use when over a valid drop target. */
        private Cursor dndValidCursor = null;
        
        /**
         * Constructor for a drag-and-drop handler
         * Creation date: (03/15/2002 2:10:00 PM)
         */
        private DragAndDropHandler() {
            // create the drag gesture recognizer for this tree.
            DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(BrowserTree.this, 
                    DnDConstants.ACTION_COPY_OR_MOVE, this);

            // Load the image to use as a base when building cursor images
            baseCursorImage = new javax.swing.ImageIcon(BrowserTree.class.getResource("/Resources/arrowMed.gif")).getImage();
        }

        /*
         * Methods implementing DragGestureListener ***********************************************************
         */
    
        /**
         * A <code>DragGestureRecognizer</code> has detected a platform-dependent drag initiating gesture and 
         * is notifying this listener in order for it to initiate the action for the user.
         * <P>
         * @param dge the <code>DragGestureEvent</code> describing the gesture that has just occurred
         */
        public void dragGestureRecognized(DragGestureEvent dge) {
            // First, check to make sure that dragging is enabled.  
            if (!isDraggable) {
                // Do nothing.
                return;
            }
            
            // Check for valid selections to drag
            if (getSelectionCount() == 0) {
                // Nothing selected, no drag possible
                return;
            }
            
            // Dragging of an individual drawer is allowed; check if this is the case
            if (getSelectionCount() == 1) {

                TreePath[] selectedPaths = getSelectionPaths();
                TreeNode endpoint = (TreeNode)selectedPaths[0].getLastPathComponent();
                
                if (endpoint instanceof MaterialGemDrawer) {
                    // Get a cursor based on the selected tree nodes
                    dndValidCursor = buildValidDnDCursor(selectedPaths);
                
                    if (!DRAG_IMAGE_ENABLED) {
                        // Someone has disabled the drag image feature so start the drag without it.
                        dge.startDrag(null, null, null, new GemDrawerSelection(((MaterialGemDrawer)endpoint).getModuleName()), this); 
                    
                    } else {
                        Point ptDragOrigin = dge.getDragOrigin();
                        TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
                        if (path == null) {
                            // not a drag condition
                            return;
                        }
                        Rectangle pathBounds = getPathBounds(path);
                        mousePointOffset.setLocation(ptDragOrigin.x - pathBounds.x, ptDragOrigin.y - pathBounds.y);
                
                        // Start the drag.  Prepare the drag image, in case the system can use it.
                        prepareDragImage(path);
                        dge.startDrag(null, dragImage, mousePointOffset, new GemDrawerSelection(((MaterialGemDrawer)endpoint).getModuleName()), this);
                    }
                    
                    return;
                }
            }
        
            // Make the list we'll build the list of gems in
            List<GemEntity> selectedGems = new ArrayList<GemEntity>();
        
            // Get all the selected paths
            TreePath[] paths = getSelectionPaths();
        
            // For each path, check it's a supercombinator and add to the string
            //   Note: (for now) we don't add sc's that aren't visible..
            for (final TreePath treePath : paths) {
                // Get the fully qualified name of the supercombinator represented by this path
                GemEntity gemEntity = getEntityFromPath(treePath);
        
                // If this is non-null, emit the name
                if (gemEntity != null && ((BrowserTreeModel)getModel()).isVisibleGem(gemEntity)) {            
                    // Add the supercombinator
                    selectedGems.add(gemEntity);
                }   
            }   
        
            // Did we get any gems to drag?
            if (!selectedGems.isEmpty()) {
                // Build a selection list comprising of all the gem names to be transfered
                GemEntitySelection scs = new GemEntitySelection(selectedGems); 

                // Get a cursor based on the selected tree nodes
                dndValidCursor = buildValidDnDCursor(paths);

                // Make sure that the drag image has been enabled
                if (!DRAG_IMAGE_ENABLED) {
                    // Someone has disabled the drag image feature so start the drag without it.
                    dge.startDrag(null, null, null, scs, this);
                } else {
                    // Start the drag gesture
                    // figure out the offset of the mouse from the bounding rectangle of the cell being dragged
                    Point ptDragOrigin = dge.getDragOrigin();
                    TreePath path = getPathForLocation(ptDragOrigin.x, ptDragOrigin.y);
                    if (path == null || !Arrays.asList(paths).contains(path)) {
                        // not a drag condition
                        return;
                    }
                    Rectangle pathBounds = getPathBounds(path);
                    mousePointOffset.setLocation(ptDragOrigin.x - pathBounds.x, ptDragOrigin.y - pathBounds.y);
            
                    // prepare the image
                    prepareDragImage(path);
                    
                    // Start the drag.  Give the drag image, in case the system can use it.
                    dge.startDrag(null, dragImage, mousePointOffset, scs, this);
                }
            }
        }
        
        /**
         * Renders the mouse drag image for the specific tree path.
         */
        private void prepareDragImage(TreePath path) {
            // First calculate an image to drag (maybe later the gem drag ghost?)
            
            // Get the tree cell renderer
            Rectangle pathBounds = getPathBounds(path);
            JLabel rendererComponent = (JLabel) getCellRenderer().getTreeCellRendererComponent (
                BrowserTree.this,                                 // tree
                path.getLastPathComponent(),                    // value
                false,                                          // isSelected
                isExpanded(path),                               // isExpanded
                getModel().isLeaf(path.getLastPathComponent()), // isLeaf
                0,                                              // row
                false                                           // hasFocus
            );
        
            // The layout manager normally does this...
            rendererComponent.setSize((int)pathBounds.getWidth(), (int)pathBounds.getHeight()); 

            // Get a buffered image of the selection for dragging a ghost image
            dragImage = new BufferedImage(
                (int)pathBounds.getWidth(), 
                (int)pathBounds.getHeight(), 
                BufferedImage.TYPE_INT_ARGB_PRE
            );

            // Get a graphics context for this image, suggest antialiasing.
            Graphics2D g2d = dragImage.createGraphics();

            // Might as well render at the highest quality, if we're only going to do this once
            g2d.setRenderingHints(RENDERING_HINTS_HIGH_QUALITY);

            // Make the image ghostlike
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));

            // Ask the cell renderer to paint itself into the BufferedImage
            rendererComponent.paint(g2d);

            // Finished with the graphics context now
            g2d.dispose();
        }
        
        /**
         * Builds a cursor for a drag and drop that can be used over a valid drop target.
         * Creation date: (06/12/2002 11:15:00 AM).
         * @param paths javax.swing.tree.TreePath[] the paths of the selected tree nodes.
         * @return Cursor
         */
        private Cursor buildValidDnDCursor(TreePath[] paths) {
            
            // Get the toolkit and scale the composite image to an appropriate size.
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension bestsize = toolkit.getBestCursorSize(1,1);

            if (bestsize.width <= 1) {
                // We don't appear to support custom cursors, so just use a cross hair
                return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
            }   
            
            // We're looking to see if more than one type of node was selected (constructor vs. sc) and 
            // which came first.  The array holds the indices of the first occurrence of each type and the boolean
            // tracks which type came first.
            boolean isFirstNodeConstructor = false;
            int gemCount = 0;
            int[] nodes = {-1, -1}; 
            
            // Go thru the paths and see which nodes were dragged
            for (int i = 0; i < paths.length && nodes[1] == -1; i++) {

                // Get the entity represented by this path
                GemEntity gemEntity = getEntityFromPath(paths[i]);
        
                // If the sc is non-null see if it is a data constructor or just a regular sc.
                if (gemEntity != null) {
                    boolean isConstructor = gemEntity.isDataConstructor();
                    gemCount++;
                    
                    if (nodes[0] == -1) {
                        // We have found our first node so remember the details
                        nodes[0] = i;
                        isFirstNodeConstructor = isConstructor;
                        
                    } else if (isConstructor != isFirstNodeConstructor) {
                        // The first node has already been found and the second node is a different type.
                        nodes[1] = i;
                    }
                }   
            }   
            
            // Don't build a cursor if we have no gems to drag
            if (gemCount == 0) {
                return null;
            }
            
            // If gem count is greater than 1 but we only have one type, just display the same type twice
            if (gemCount > 1 && nodes[1] == -1) {
                nodes[1] = nodes[0];
            }
                     
            // Get the icons used in the labels and track the size of the composite image we need to build
            javax.swing.Icon[] icons = new javax.swing.Icon[2];
            int compWidth = baseCursorImage.getWidth(null); 
            int compHeight = baseCursorImage.getHeight(null);
            for (int i = 0; i < nodes.length && nodes[i] >= 0; i++) {

                javax.swing.JLabel label = (javax.swing.JLabel)getCellRenderer().getTreeCellRendererComponent (
                        BrowserTree.this,                                            // tree
                        paths[nodes[i]].getLastPathComponent(),                    // value
                        false,                                                     // isSelected
                        isExpanded(paths[nodes[i]]),                               // isExpanded
                        getModel().isLeaf(paths[nodes[i]].getLastPathComponent()), // isLeaf
                        0,                                                         // row
                        false                                                      // hasFocus
                );
                icons[i] = label.getIcon();
                
                // Start tracking the size of the composite cursor image
                compWidth += icons[i].getIconWidth();
                compHeight += icons[i].getIconHeight();
            }
                        
            // Get a buffered image (and its graphics) to build a composite cursor image
            BufferedImage compImage = new BufferedImage(compWidth, compHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D)compImage.getGraphics();
            
            // Paint the base image onto the composite graphics
            g2d.drawImage(baseCursorImage, 0, 0, null);

            // Paint the icons below and to the right of the base image.  The -2's are used to reduce the amount 
            // of space between the bottom of the arrow and the top of the first tree node image.  
            int locX = baseCursorImage.getWidth(null)-2;
            int locY = baseCursorImage.getHeight(null)-2;
            for (int i = 0; i < icons.length && nodes[i] >=0; i++) {

                icons[i].paintIcon(null, g2d, locX, locY);
                
                // Shift the location so the next icon image is painted a little to the right.
                locX += 8;
            }
            
            g2d.dispose();

            // Scale the image correctly
            BufferedImage scaledCompImage = new BufferedImage(bestsize.width, bestsize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D scaledGraphics = scaledCompImage.createGraphics();
            scaledGraphics.drawImage(compImage, null, null);
            scaledGraphics.dispose();

            // Actually build a cursor from the scaled image
            return toolkit.createCustomCursor(scaledCompImage, new Point(0, 0), "dndValidCursor");
        }
    
        /*
         * Methods implementing DragSourceListener ************************************************************
         */
    
        /**
         * This method is invoked to signify that the Drag and Drop operation is complete. 
         * The getDropSuccess() method of the <code>DragSourceDropEvent</code> can be used to 
         * determine the termination state. The getDropAction() method returns the operation 
         * that the <code>DropTarget</code> selected (via the DropTargetDropEvent acceptDrop() parameter)
         * to apply to the Drop operation. Once this method is complete, the current 
         * <code>DragSourceContext</code> and associated resources become invalid.
         * <P>
         * @param dsde the <code>DragSourceDropEvent</code>
         */
        public void dragDropEnd(DragSourceDropEvent dsde) {
            // If necessary, erase the last ghost image

            if (!DRAG_IMAGE_SUPPORTED) {
                paintImmediately(lastGhostRect.getBounds());
            }
            
            // set image to null - we use this to find out when the operation ended
            dragImage = null;
        }
    
        /**
         * Called as the hotspot enters a platform dependent drop site.
         * This method is invoked when the following conditions are true:
         * <UL>
         * <LI>The logical cursor's hotspot initially intersects a GUI <code>Component</code>'s  visible geometry.
         * <LI>That <code>Component</code> has an active <code>DropTarget</code> associated with it.
         * <LI>The <code>DropTarget</code>'s registered <code>DropTargetListener</code> dragEnter() method 
         * is invoked and returns successfully.
         * <LI>The registered <code>DropTargetListener</code> invokes the <code>DropTargetDragEvent</code>'s 
         * acceptDrag() method to accept the drag based upon interrogation of the source's 
         * potential drop action(s) and available data types (<code>DataFlavor</code>s).
         * </UL>
         *<P>
         *@param dsde the <code>DragSourceDragEvent</code>
         */
        public void dragEnter(DragSourceDragEvent dsde) {

            if (!DRAG_IMAGE_ENABLED) {
                // Set the cursor to the valid DnD cursor since we are entering a valid drop site.
                dsde.getDragSourceContext().setCursor(dndValidCursor);
            }
        }
    
        /**
         * Called as the hotspot exits a platform dependent drop site.
         * This method is invoked when the following conditions
         * are true:
         * <UL>
         * <LI>The cursor's logical hotspot no longer intersects the visible geometry of the <code>Component</code>
         * associated with the previous dragEnter() invocation.
         * </UL>
         * OR
         * <UL>
         * <LI>The <code>Component</code> that the logical cursor's hotspot intersected that resulted in the 
         * previous dragEnter() invocation no longer has an active <code>DropTarget</code> or 
         * <code>DropTargetListener</code> associated with it.
         * </UL>
         * OR
         * <UL>
         * <LI> The current <code>DropTarget</code>'s <code>DropTargetListener</code> has invoked rejectDrag()
         * since the last dragEnter() or dragOver() invocation.
         * </UL>
         * <P>
         * @param dse the <code>DragSourceEvent</code>
         */
        public void dragExit(DragSourceEvent dse) {

            if (!DRAG_IMAGE_ENABLED) {
                // Set the invalid cursor since we are leaving a valid drop site.
                dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
            }
        }
    
        /**
         * Called as the hotspot moves over a platform dependent drop site.
         * This method is invoked when the following conditions
         * are true:
         *<UL>
         *<LI>The cursor's logical hotspot has moved but still
         * intersects the visible geometry of the <code>Component</code>
         * associated with the previous dragEnter() invocation.
         * <LI>That <code>Component</code> still has a 
         * <code>DropTarget</code> associated with it.
         * <LI>That <code>DropTarget</code> is still active.
         * <LI>The <code>DropTarget</code>'s registered
         * <code>DropTargetListener</code> dragOver() method 
         * is invoked and returns successfully.
         * <LI>The <code>DropTarget</code> does not reject 
         * the drag via rejectDrag()
         * </UL>
         * <P>
         * @param dsde the <code>DragSourceDragEvent</code>
         */
        public void dragOver(DragSourceDragEvent dsde) {
            
            if (!DRAG_IMAGE_ENABLED) {
                // Set the invalid cursor since we are leaving a valid drop site.
                dsde.getDragSourceContext().setCursor(dndValidCursor);
            }
        }
    
        /**
         * Called when the user has modified the drop gesture.
         * This method is invoked when the state of the input
         * device(s) that the user is interacting with changes.
         * Such devices are typically the mouse buttons or keyboard
         * modifiers that the user is interacting with.
         * <P>
         * @param dsde the <code>DragSourceDragEvent</code>
         */
        public void dropActionChanged(DragSourceDragEvent dsde) {
        }
    
        /*
         * Methods implementing DropTargetListener ************************************************************
         */
    
        /**
         * Called when a drag operation has encountered the <code>DropTarget</code>.
         * <P>
         * Creation date: (03/14/2002 5:59:00 PM)
         * @param dtde the <code>DropTargetDragEvent</code> 
         */
        public void dragEnter(DropTargetDragEvent dtde){
        }
    
        /**
         * Called when a drag operation is ongoing on the <code>DropTarget</code>.
         * <P>
         * Creation date: (03/14/2002 5:59:00 PM)
         * @param dtde the <code>DropTargetDragEvent</code> 
         */
        public void dragOver(DropTargetDragEvent dtde){
    
            // To draw the drag image:
            //   First, repaint the real estate the drag image last occupied. 
            //    Note that simply calling repaint() won't work because it effectively delays the repainting, 
            //    possibly until after you have drawn the new drag image, and therefore, erases all or part of it. 
            //    You really must paint the area immediately, using the, you guessed it, paintImmediately() method.
            //
            //   Second, you draw the ghost image in its new location. 
            //    Note that you draw the image the same distance away from the mouse pointer as when the node 
            //    was first clicked.
            if (!DRAG_IMAGE_SUPPORTED && dragImage != null) {
                Graphics2D g2d = (Graphics2D)getGraphics();

                // Calculate new location
                Point mouseLocation = dtde.getLocation();
                int newX = mouseLocation.x - mousePointOffset.x;
                int newY = mouseLocation.y - mousePointOffset.y;
    
                // Update if the location changed
                if (newX != lastGhostRect.x || newY != lastGhostRect.y) {
                    // Erase the last ghost image and cue line
                    paintImmediately(lastGhostRect.getBounds());    
                    // Remember where you are about to draw the new ghost image
                    lastGhostRect.setBounds(newX, newY, dragImage.getWidth(), dragImage.getHeight());
                    // Draw the ghost image
                    g2d.drawImage(dragImage, 
                            AffineTransform.getTranslateInstance(lastGhostRect.getX(), lastGhostRect.getY()), null);
                }
                
                g2d.dispose();
            }

        }
    
        /**
         * Called if the user has modified the current drop gesture.
         * <P>
         * Creation date: (03/14/2002 5:59:00 PM)
         * @param dtde the <code>DropTargetDragEvent</code>
         */
        public void dropActionChanged(DropTargetDragEvent dtde){
        }
    
        /**
         * The drag operation has departed the <code>DropTarget</code> without dropping.
         * <P>
         * Creation date: (03/14/2002 5:59:00 PM)
         * @param dte the <code>DropTargetEvent</code> 
         */
        public void dragExit(DropTargetEvent dte){
            // If necessary, erase the last ghost image
            if (!DRAG_IMAGE_SUPPORTED) {
                paintImmediately(lastGhostRect.getBounds());
            }
        }
    
        /**
         * The drag operation has terminated with a drop on this <code>DropTarget</code>.
         * This method is responsible for undertaking the transfer of the data associated with the
         * gesture. The <code>DropTargetDropEvent</code> provides a means to obtain a <code>Transferable</code>
         * object that represents the data object(s) to be transfered.<P>
         * From this method, the <code>DropTargetListener</code> shall accept or reject the drop via the   
         * acceptDrop(int dropAction) or rejectDrop() methods of the <code>DropTargetDropEvent</code> parameter.
         * <P>
         * Subsequent to acceptDrop(), but not before, <code>DropTargetDropEvent</code>'s getTransferable()
         * method may be invoked, and data transfer may be performed via the returned <code>Transferable</code>'s 
         * getTransferData() method.
         * <P>
         * At the completion of a drop, an implementation of this method is required to signal the success/failure
         * of the drop by passing an appropriate <code>boolean</code> to the <code>DropTargetDropEvent</code>'s
         * dropComplete(boolean success) method.
         * <P>
         * Note: The actual processing of the data transfer is not required to finish before this method returns. It may be
         * deferred until later.
         * <P>
         * Creation date: (03/14/2002 5:59:00 PM)
         * @param dtde the <code>DropTargetDropEvent</code> 
         */
        public void drop(DropTargetDropEvent dtde){
            dtde.rejectDrop();
        }
    
    }

    /**
     * Listens for the user triggering the pop up menu mouse event.
     * Note: This listener automatically corrects for the strange
     * bug that right clicking on a JTree node does not select it.
     */
    private class PopupListener extends MouseAdapter {  

        @Override
        public void mousePressed(MouseEvent e) {
            // Correct for the RMB not selecting bug.
            if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
                int selectedRow = getRowForLocation(e.getX(), e.getY());
                    
                // If there is actually a row there, then select it.
                // Else, do nothing.
                if (selectedRow != -1) {    
                    setSelectionRow(selectedRow);
                }
            }
                
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {       
            maybeShowPopup(e);
        }
        
        /**
         * Show the popup, if the given mouse event is the popup trigger.
         * @param e the mouse event.
         */
        private void maybeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger() && popupMenuProvider != null) {

                // Get the popup menu for the current selection path.
                TreePath selectionPath = getSelectionPath();
                JPopupMenu browserTreePopupMenu = popupMenuProvider.getPopupMenu(selectionPath);

                // Show the popup menu if it has at least one item.
                if (browserTreePopupMenu.getSubElements().length > 0) {
                    browserTreePopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }

    /**
     * Interface to define a provider for a popup menu for this tree.
     * @author Edward Lam
     */
    public interface PopupMenuProvider {
        /**
         * Get the popup menu for a given selection path in this tree.
         * @param selectionPath the path on which the popup menu was invoked.
         * @return the popup menu for this tree.
         */
        JPopupMenu getPopupMenu(TreePath selectionPath);
    }
    
    /**
     * This listener fires a leafNodeTriggered event if the user selects leaf nodes and hits enter.
     * @author Edward Lam
     */
    private class KeyStrokeNavigator extends PickListKeyStrokeNavigator {

        /**
         * Constructor for the KeyStrokeNavigator class.
         * Creation date: (07/09/2002 8:33:00 AM).
         * @param component Component - the component that this listener is navigating.
         */
        public KeyStrokeNavigator(Component component) {
            super(component);
        }
        
        /**
         * Watch for the user to press ENTER.
         * Creation date: (04/16/2002 3:33:00 PM)
         */
        @Override
        public void keyPressed(KeyEvent evt) {
           
            if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                // Get the currently selected paths
                TreePath[] selectedPaths = getSelectionPaths();
                
                // Loop thru the paths and build a list of objects for all the leaves
                if (selectedPaths != null) {
                    
                    List<Object> userObjects = new ArrayList<Object>();
                    for (final TreePath treePath : selectedPaths) {
                            
                        Object lastComponent = treePath.getLastPathComponent();
                        if (lastComponent instanceof BrowserTreeNode 
                            && ((BrowserTreeNode)lastComponent).isLeaf()) {
                                
                            userObjects.add(((BrowserTreeNode)lastComponent).getUserObject());
                        }
                    }
                    
                    // Fire off an event for these nodes
                    fireLeafNodeTriggeredEvent(userObjects);
                }
            }
            
            // Hand the event off to the super version
            super.keyPressed(evt);
        }

        /**
         * Get the String text displayed in the nth row of the pick list.
         * @param index the index of the string to return
         * @return the text displayed in the nth row
         */
        @Override
        public String getNthRowString(int index){
            
            // get the node corresponding to this row
            TreePath tp = getPathForRow(index);
            BrowserTreeNode node = (BrowserTreeNode) tp.getLastPathComponent();

            // use the cell renderer to find out what text it would print out
            boolean isLeaf = (node instanceof GemTreeNode);
            TreeCellRenderer tcr = getCellRenderer();
            Component c = tcr.getTreeCellRendererComponent(BrowserTree.this, node, false, false, isLeaf, 0, false);

            // The returned component is an instance of JLabel.  Get the text which would be displayed, in lower case.
            String rowString = ((JLabel) c).getText();

            return rowString;
        }

        /**
         * Get the number of rows in the pick list.
         * Creation date: (11/09/2001 3:03:52 PM)
         * @return int the number of rows in the pick list
         */
        @Override
        public int getNumRows(){
            return getRowCount();
        }

        /**
         * Get the index of the row in the pick list which is currently selected.
         * Creation date: (11/09/2001 3:03:52 PM)
         * @return int the index of the currently selected row
         */
        @Override
        public int getSelectedRowIndex(){
            return getLeadSelectionRow();
        }

        /**
         * Move the selection in the pick list to a given row.
         * Creation date: (11/09/2001 3:03:52 PM)
         * @param index int the index of the row to set selected
         */
        @Override
        public void selectRow(int index){
            setSelectionRow(index);
            scrollRowToVisible(index);
        }
    }

    /**
     * This listener fires a leafNodeTriggered event if the user double-clicks on a leaf node.
     * @author Frank Worsley
     */    
    private class LeafNodeMouseListener extends MouseClickDragAdapter { 
        
         @Override
        public boolean mouseReallyClicked(MouseEvent e) {

             boolean doubleClicked = super.mouseReallyClicked(e);

             TreePath[] selectedPaths = getSelectionPaths();
             
             // If there are no selected paths, then use the path that was clicked on.
             if (selectedPaths == null || selectedPaths.length == 0) {
                 selectedPaths = new TreePath[1];
                 selectedPaths[0] = getPathForLocation(e.getX(), e.getY());
             }
            
             if (doubleClicked && SwingUtilities.isLeftMouseButton(e)) {
                
                 List<Object> userObjects = new ArrayList<Object>();
                 
                 for (final TreePath treePath : selectedPaths) {
                        
                     if (treePath == null) {
                         continue;
                     }
                     
                     Object lastComponent = treePath.getLastPathComponent();
                     
                     if (lastComponent instanceof BrowserTreeNode 
                         && ((BrowserTreeNode) lastComponent).isLeaf()) {
                            
                         userObjects.add(((BrowserTreeNode) lastComponent).getUserObject());
                     }
                 }
                 
                 if (userObjects.size() > 0) {
                    fireLeafNodeTriggeredEvent(userObjects);
                 }
             }
            
             return doubleClicked;
         }
     }

    /**
     * Default BrowserTree constructor.
     */
    public BrowserTree() {
        
        // Use an empty model until the Services get initialized        
        BrowserTreeModel browserTreeModel = new BrowserTreeModel();
        setModel(browserTreeModel);

        // We don't want to see the root node.
        setRootVisible(false);
        setShowsRootHandles(true);

        // Set the renderer for this tree
        setCellRenderer(new BrowserTreeCellRenderer());
    
        // Add pop up menu capabilities.
        addMouseListener(new PopupListener());
    
        // Add the ability to quickly get to a gem by typing in its name
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyStrokeNavigator(this));

        // Add a listener to fire leaf node events if the user double-clicks
        addMouseListener(new LeafNodeMouseListener());

        // Register the drag-and-drop handler
        dragAndDropHandler = new DragAndDropHandler();

        // Lie and say we are a drop target.
        // We do this so we can draw the drag image if that operation is not
        // supported natively on the platform. We never actually accept any drops.
        setDropTarget(new DropTarget(this, dragAndDropHandler));
        
        // Set the default popup menu provider
        popupMenuProvider = new PopupMenuProvider() {
            public JPopupMenu getPopupMenu(TreePath selectionPath) {
                return browserTreeActions.getDefaultBrowserTreePopup(selectionPath);
            }
        };

        isDraggable = true;
        highlightCurrentModule = true;
        
        // Add a listener that will update the metadata viewer when the selection changes.
        addTreeSelectionListener(new TreeSelectionListener() {
                    
            public void valueChanged(TreeSelectionEvent evt) {
                
                TreePath path = evt.getNewLeadSelectionPath();
                
                if (path == null) {
                    return;
                }
                
                BrowserTreeNode node = (BrowserTreeNode) path.getLastPathComponent();
                
                if (navigatorOwner != null) {
    
                    final NavAddress address;
                    if (node instanceof GemTreeNode) {
                        GemEntity gemEntity = (GemEntity) node.getUserObject();
                        address = NavAddress.getAddress(gemEntity);
                        navigatorOwner.displayMetadata(address, false);
                            
                    } else if (node instanceof GemDrawer) {
                        final GemDrawer gemDrawer = (GemDrawer) node;
                        final ModuleName moduleName = gemDrawer.getModuleName();
                        if (gemDrawer.isNamespaceNode()) {
                            address = NavAddress.getModuleNamespaceAddress(moduleName);
                            navigatorOwner.displayMetadata(address, false);
                        } else {
                            MetaModule metaModule = navigatorOwner.getPerspective().getMetaModule(moduleName);
                            if (metaModule != null) {
                                address = NavAddress.getAddress(metaModule);
                                navigatorOwner.displayMetadata(address, false);
                            }
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Set the navigator owner to use for editing/displaying gem metadata.
     * @param navigatorOwner the navigator owner to use
     */
    public void setNavigatorOwner(NavFrameOwner navigatorOwner) {
        this.navigatorOwner = navigatorOwner;
    }
    
    /**
     * @return the navigator owner being used to display/edit metadata. This may be
     * null if no navigator owner has been setup.
     */
    public NavFrameOwner getNavigatorOwner() {
        return navigatorOwner;
    }

    /**
     * Expand all visible tree nodes at a given depth.
     * <p>
     * <strong>This should be run on the AWT event-handler thread.</strong>
     * 
     * @param depth 0-based level of the nodes to expand.  Nodes with this depth will be expanded.
     */
    public void expandLevel(int depth) {

        for (Enumeration<BrowserTreeNode> nodeEnum = UnsafeCast.unsafeCast(((BrowserTreeNode)getModel().getRoot()).breadthFirstEnumeration()); nodeEnum.hasMoreElements(); ) {

            BrowserTreeNode node = nodeEnum.nextElement();

            int nodeDepth = node.getLevel();
            if (nodeDepth <= depth) {
                expandPath(new TreePath(node.getPath()));

            } else if (nodeDepth > depth) {
                return;     // breadth first, so all leftover elements will have depth >= the current depth
            }
        }
    }
    
    /**
     * If there is a node in the tree for the given entity, this will select that
     * node and scroll it into view. If there is no such node, then this does nothing.
     * <p>
     * <strong>This should be run on the AWT event-handler thread.</strong>
     * 
     * @param newGemEntity the entity to select
     */
    public void selectGemNode(GemEntity newGemEntity) {
        
        BrowserTreeModel browserTreeModel = (BrowserTreeModel) getModel();
        BrowserTreeNode gemNode = browserTreeModel.getTreeNode(newGemEntity);
        
        if (gemNode != null) {
            TreePath nodePath = new TreePath(gemNode.getPath());
            scrollPathIntoView(nodePath);
            setSelectionPath(nodePath);
        }      
    }
    
    /**
     * If there is a drawer node with given name, this will select that
     * node and scroll it into view. If there is no such node, then this does nothing.
     * <p>
     * <strong>This should be run on the AWT event-handler thread.</strong>
     * 
     * @param drawerName the name of the drawer to select
     */
    public void selectDrawerNode(ModuleName drawerName) {
        
        Enumeration<BrowserTreeNode> allNodes = UnsafeCast.unsafeCast(((BrowserTreeNode)getModel().getRoot()).breadthFirstEnumeration());
        
        while (allNodes.hasMoreElements()) {
            
            BrowserTreeNode node = allNodes.nextElement();
                        
            if (node instanceof GemDrawer && ((GemDrawer)node).getModuleName().equals(drawerName)) {
                TreePath nodePath = new TreePath(node.getPath());
                setSelectionPath(nodePath);
                scrollPathIntoView(nodePath);
                break;
            }
        }
    }
    
    /**
     * If there is a node with the given title, this will select that node
     * and scroll it into view. If there is no such node, then this does nothing.
     * If there are several nodes with the given title, the first node will be selected.
     * <p>
     * <strong>This should be run on the AWT event-handler thread.</strong>
     * 
     * @param nodeTitle the title of the node to select
     */
    public void selectNode(String nodeTitle) {
        
        Enumeration<BrowserTreeNode> allNodes = UnsafeCast.unsafeCast(((BrowserTreeNode)getModel().getRoot()).breadthFirstEnumeration());
        
        while (allNodes.hasMoreElements()) {
            
            BrowserTreeNode node = allNodes.nextElement();
                        
            if (node.getDisplayedString().equals(nodeTitle)) {
                TreePath nodePath = new TreePath(node.getPath());
                setSelectionPath(nodePath);
                scrollPathIntoView(nodePath);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     * Additionally saves the list of children that includes the selected node. 
     */
    @Override
    public void saveState() {

        // Remember the currently expanded nodes.
        Enumeration<TreePath> expandedPaths = getExpandedDescendants(new TreePath(getModel().getRoot()));
        savedExpandedPaths = new HashSet<TreePath>();

        if (expandedPaths != null) {
            while (expandedPaths.hasMoreElements()) {
                TreePath path = expandedPaths.nextElement();
                savedExpandedPaths.add(path);
            }
        }

        // Remember the selected node and the current position of the selected node in respect to all its siblings
        savedSelectionPath = getSelectionPath();
        if (savedSelectionPath != null) {
            BrowserTreeNode parentNode = (BrowserTreeNode) savedSelectionPath.getParentPath().getLastPathComponent();
            Enumeration<TreeNode> enumChildren = UnsafeCast.unsafeCast(parentNode.children());
            savedChildrenList  = new ArrayList<Object>();
            while (enumChildren.hasMoreElements()) {
                savedChildrenList.add(enumChildren.nextElement());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restoreSavedState() {

        for (final TreePath treePath : savedExpandedPaths) {
            this.expandPath(treePath);
        }

        if (savedSelectionPath != null) {

            TreeNode lastPathComponent = ((TreeNode) savedSelectionPath.getLastPathComponent());
            TreePath parentPath = savedSelectionPath.getParentPath();
            TreeNode parentNode = (TreeNode)parentPath.getLastPathComponent();
            TreePath newPath = parentPath;  // starts out as parent path, but can be changed below.
            
            // Check if the path contains a drawerNode and is still valid (ie. the module is not removed)
            boolean workingModuleExists = false;
            
            Object[] nodesOnPath = savedSelectionPath.getPath();
            int indexOfDrawerNodeOnPath = 0;
           
            for (int i = 0; i < savedSelectionPath.getPathCount(); i++) {
                if (nodesOnPath[i] instanceof GemDrawer) {
                    indexOfDrawerNodeOnPath = i;
                    TreeNode prevNodeOnPath = (TreeNode) nodesOnPath[i - 1];

                    for (int j = 0; j < prevNodeOnPath.getChildCount(); j++) {
                        if (prevNodeOnPath.getChildAt(j).equals(nodesOnPath[i])) {
                            workingModuleExists = true;
                            break;
                        }
                    }
                    break;
                }
            }

            // If there was a module on the path and it no longer exists, its parent will be selected
            if (indexOfDrawerNodeOnPath != 0 && !workingModuleExists) {
                TreeNode[] tempNewPath = new TreeNode[indexOfDrawerNodeOnPath];
                for (int i = 0; i < indexOfDrawerNodeOnPath; i++){
                    tempNewPath[i]= (TreeNode) nodesOnPath[i];
                }
                newPath = new TreePath(tempNewPath);
              
            // Leaf nodes are reconstructed therefore need to compare the user objects to find the correct path.
            } else if (lastPathComponent instanceof GemTreeNode) {
               
                GemEntity lastNodeObj = (GemEntity)((GemTreeNode) lastPathComponent).getUserObject();

                if (!((BrowserTreeModel)getModel()).getShowPublicGemsOnly() || lastNodeObj.getScope().isPublic()){

                    // Finding the new tree node for the gem entity
                    GemTreeNode child = getNodeFromEntity(parentNode, lastNodeObj);
                    if (child != null){  // for the case of a search node, the child can become null
                        newPath = new TreePath(child.getPath());
                    } 

                } else { // If only showing public gems and the selected gem is not public, propagate selection to one of its siblings.
                    int prevChildIndex = savedChildrenList.indexOf(lastPathComponent);
                    int prevChildCount = savedChildrenList.size();

                    // First search downward from the index of the selected gem for the next public gem
                    for (int i = prevChildIndex; i < prevChildCount; i++) {
                        TreeNode oldSibling = (TreeNode) savedChildrenList.get(i);
                        if (oldSibling instanceof GemTreeNode) {
                            TreePath publicChildPath = getPublicGemTreeNodePath(parentNode, (GemTreeNode) oldSibling);
                            if (publicChildPath != null) {
                                newPath = publicChildPath;
                                break;
                            }
                        }
                    }
                    // If cannot find any public gems so far, go back to the index and search upward
                    if (newPath == parentPath) {
                        for (int i = prevChildIndex - 1; i >= 0; i--) {
                            TreeNode oldSibling = (TreeNode) savedChildrenList.get(i);
                            if (oldSibling instanceof GemTreeNode) {
                                TreePath publicChildPath = getPublicGemTreeNodePath(parentNode, (GemTreeNode)oldSibling);
                                if (publicChildPath != null) {
                                    newPath = publicChildPath;
                                    break;
                                }
                            }
                        }
                    }
                }

            } else {
                newPath = savedSelectionPath;
            }

            // If the last component on the new path is an empty category node, propagate the selection up the hierarchy
            TreeNode newLastPathComponent = (TreeNode)newPath.getLastPathComponent();
            if (newLastPathComponent instanceof GemCategoryNode) {
                while(newLastPathComponent.getChildCount() == 0 && newLastPathComponent instanceof GemCategoryNode) {
                    newPath = newPath.getParentPath();
                    newLastPathComponent = (TreeNode)newPath.getLastPathComponent();
                }
            }
            
            setSelectionPath(newPath); 
            scrollPathIntoView(newPath);

        }
    }

    /**
     * Get the TreeNodePath to the child if the child's gem entity is public.
     * Otherwise return null.
     * @param parentNode the parent tree node.
     * @param oldGemTreeNode the gem tree node to look at (from the saved state)
     * @return the child if the child's gem entity is public.
     * Otherwise null.
     */
    private TreePath getPublicGemTreeNodePath(TreeNode parentNode, GemTreeNode oldGemTreeNode) {

        GemEntity childUserObject = (GemEntity)oldGemTreeNode.getUserObject();
                  
        if (childUserObject.getScope().isPublic()) {
            GemTreeNode sibling = getNodeFromEntity(parentNode, childUserObject);
            return new TreePath(sibling.getPath());
        }
        return null;
    }

    /** 
     * To find the tree node that contains the gem entity
     * 
     * @param parentNode the parent of the node of interest
     * @param gemEntity the entity that we wish to find
     * @return child the GemTreeNode that contains the entity
     */
    private GemTreeNode getNodeFromEntity(TreeNode parentNode, GemEntity gemEntity){
        
        // Need to compare the qualified name because it is consistent even after recompiling
        QualifiedName entityName = gemEntity.getName();
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            GemTreeNode child = ((GemTreeNode) parentNode.getChildAt(i));
            QualifiedName childName = ((GemEntity)child.getUserObject()).getName();
            if (childName.equals(entityName)) {
                return child;
            }
        }
        return null;
    }


    /**
     * Obtain the entity for a Gem from a BrowserTree path.
     * @return the entity of the Gem or NULL if not a gem (eg. a drawer or workspace)
     * @param path the path to the Gem
     */
    private static GemEntity getEntityFromPath(TreePath path) {

        // First we can check if this is a Gem.  The endpoint must be a terminator node
        TreeNode endpoint = (TreeNode)path.getLastPathComponent();

        if (endpoint.getAllowsChildren()) {
            // No good, this is not a terminator
            return null;
        }
    
        return (GemEntity)((BrowserTreeNode)endpoint).getUserObject();
    }

    /**
     * Sets whether or not the gems in the BrowserTree can be involved with Drag and Drop.
     * Creation date: (06/04/01 12:01:00 PM)
     * @param allowed boolean
     */
    public void setEnabledDragAndDrop(boolean allowed) {
        isDraggable = allowed;  
    }
    
    /**
     * Sets whether or not type expressions are displayed next to gem names in the tree.
     * @param display
     */
    public void setDisplayTypeExpr(boolean display) {
        displayTypeExpr = display;
        updateUI();
    }
    
    /**
     * Gets whether or not type expressions are displayed next to gem names in the tree.
     * @return true if type expressions are displayed
     */
    public boolean getDisplayTypeExpr() {
        return displayTypeExpr;
    }
    
    /**
     * Sets whether the current module should be highlighted in the tree.
     * @param highlightCurrentModule
     */
    public void setHighlightCurrentModule(boolean highlightCurrentModule) {
        this.highlightCurrentModule = highlightCurrentModule;
    }
    
    /**
     * Return whether the current module should be highlighted in the tree.
     * @return whether the current module should be highlighted in the tree.
     */
    boolean getHighlightCurrentModule() {
        return highlightCurrentModule;
    }
    
    /**
     * Set the provider of popups to this tree.
     * @param newMenuProvider the new popup menu provider.
     */
    public void setPopupMenuProvider(PopupMenuProvider newMenuProvider) {
        this.popupMenuProvider = newMenuProvider;
    }
    
    /**
     * Return the BrowserTreeActions object associated with this tree
     * @return BrowserTreeActions
     */
    public BrowserTreeActions getBrowserTreeActions() {
        return browserTreeActions;
    }
    
    /**
     * Return the current drag image if the gem browser is the source of the drag.
     * Creation date: (03/15/01 3:48:00 PM)
     * @return BufferedImage the image to drag, or null if the browser is not the source of a drag.
     */
    public BufferedImage getDragImage() {
        return dragAndDropHandler.dragImage;
    }

    /**
     * Return the current drag offset if the gem browser is the source of the drag.
     * Creation date: (03/15/01 3:51:00 PM)
     * @return Point the current drag offset.
     */
    public Point getDragOffset() {
        return new Point(dragAndDropHandler.mousePointOffset);
    }

    /** 
     * Add a LeafNodeTriggeredListener to the listener list.
     * Creation date: (04/17/2002 10:06:00 AM).
     * @param listener LeafNodeTriggeredListener - the listener to add
     */
    public void addLeafNodeTriggeredListener(LeafNodeTriggeredListener listener) {
       
        if (listener != null) {
            leafNodeTriggeredListeners.add(listener); 
        }
    }
    
    /**
     * Notify the appropriate registered listeners that a node has been triggered.
     * Creation date: (04/17/2002 10:08:00 AM).
     * @param userObjects the objects of the nodes that were triggered
     */
    protected void fireLeafNodeTriggeredEvent(List<Object> userObjects) {
        
        // Create the LeafNodeTriggeredEvent
        LeafNodeTriggeredEvent event = new LeafNodeTriggeredEvent(this, userObjects);
        
        // Notify each listener
        for (int i = 0, listenerCount = leafNodeTriggeredListeners.size(); i < listenerCount; i++) {
            
            leafNodeTriggeredListeners.get(i).leafNodeTriggered(event);
        }
    }

    /**
     * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
     */
    @Override
    public String getToolTipText(MouseEvent mouseEvent) {
               
        Point mouseLocation = mouseEvent.getPoint();
        TreePath path = getPathForLocation(mouseLocation.x, mouseLocation.y);
        
        if (path != null) {
            BrowserTreeNode node = (BrowserTreeNode) path.getLastPathComponent();
             
            if (node instanceof GemTreeNode && node.isLeaf ()) {
                BrowserTreeModel browserTreeModel = (BrowserTreeModel) getModel();
                ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(browserTreeModel.getPerspective().getWorkingModuleTypeInfo());
                GemEntity gemEntity = (GemEntity)node.getUserObject();  
                return ToolTipHelpers.getEntityToolTip(gemEntity, namingPolicy, this);
                
            } else if (node instanceof GemDrawer) {
                
                BrowserTreeModel browserTreeModel = (BrowserTreeModel)getModel();
                GemDrawer gemDrawer = (GemDrawer)node;
                ModuleName moduleName = gemDrawer.getModuleName();

                // Display visibility description in tooltip
                String drawerVisibility;
                if (!browserTreeModel.isVisibleModule(moduleName)) {
                    drawerVisibility = BrowserMessages.getString("GB_NotImported");
                    
                } else if (browserTreeModel.isWorkingModule(moduleName)) {
                    drawerVisibility = BrowserMessages.getString("GB_CurrentlyEdited");
                        
                } else {
                    drawerVisibility = BrowserMessages.getString("GB_Imported");
                }
                
                StringBuilder toolTipText = new StringBuilder("<html><b>" + node.getDisplayedString() + "</b>&nbsp;(" + drawerVisibility + ")<br>");
                toolTipText.append(node.getSecondaryToolTipText());        
                toolTipText.append("</html>");

                return toolTipText.toString();
                
            } else {
                return node.getToolTipText();
            }
        }
        
        return null;
    }
    
    /**
     * @see javax.swing.JComponent#getToolTipLocation(java.awt.event.MouseEvent)
     */
    @Override
    public Point getToolTipLocation(MouseEvent e) {

        Point mouseLocation = e.getPoint();
        TreePath path = getPathForLocation(mouseLocation.x, mouseLocation.y);
        
        if (path != null) {

            Rectangle bounds = getPathBounds(path);
            
            if (bounds != null) {

                Rectangle visibleRect = getVisibleRect();
    
                // always display tooltip along the right side of the tree
                int x = visibleRect.x + visibleRect.width - 20;
                return new Point (x > 0 ? x : 0, bounds.y);
            }
        }
        
        return null;
    }
}