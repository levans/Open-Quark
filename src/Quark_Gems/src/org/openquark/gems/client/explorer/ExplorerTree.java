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
 * ExplorerTree.java
 * Creation date: Jan 17th 2003
 * By: Ken Wong
 */

package org.openquark.gems.client.explorer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.openquark.cal.metadata.FunctionMetadata;
import org.openquark.gems.client.CodeGem;
import org.openquark.gems.client.CollectorGem;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.RecordFieldSelectionGem;
import org.openquark.gems.client.ReflectorGem;
import org.openquark.gems.client.valueentry.ValueEditor;


/**
 * This class represents the JTree JComponent that is used in the TableTopExplorer.
 * @author Ken Wong
 */
public class ExplorerTree extends JTree implements Autoscroll{
    
    private static final long serialVersionUID = 1943630679610833258L;

    /**
     * The editor used to change the names of the code gems and collector gems
     * @author Ken Wong
     */
    private static class ExplorerCellEditor extends DefaultTreeCellEditor {
        
        /**
         * Default Constructor for this editor
         * @param tree the explorer tree the editor is for
         * @param renderer the cell renderer for the tree
         * @param field the cell editor for the field we should edit
         */
        ExplorerCellEditor(ExplorerTree tree, DefaultTreeCellRenderer renderer, ExplorerTreeCellEditor field) {
            super (tree, renderer, field);
        }
        
        /**
         * @see org.openquark.gems.client.explorer.ExplorerTreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
         */
        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            
            Component component = super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);

            renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
            editingIcon = renderer.getIcon();
            
            boolean valueEditor = ((DefaultTreeCellEditor.EditorContainer) component).getComponent(0) instanceof ValueEditor; 
            tree.setRowHeight(valueEditor ? -1 : 16);
            
            return component;
        }
    }

    /**
     * Used to listen to mouse clicked events and when one is received on a focussable node the
     * navigation helper is notified that the user wants to focus on the desired gem.
     */
    private class ExplorerMouseEventListener extends MouseAdapter {
        /**
         * Clicking on a focussable node should trigger a navigation focus event on the node.
         * @param e The mouse event that just happened
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            Point location = e.getPoint();
            if (isFocusablePoint(location)) {
                TreePath path = getPathForLocation(location.x, location.y);
                Object node = path.getLastPathComponent();
                if (node instanceof ExplorerGemNode) {
                    // Indicate that the navigation helper should change to focus on the gem
                    Gem gem = ((ExplorerGemNode)node).getGem();
                    navigationHelper.focusOn(gem);
                }
            }
        }
    }
    
    /**
     * This class uses the navigation helper to determine if a node is focusable.  If it is then the
     * cursor is switched to a hand to mimic the behaviour in browsers of mousing over a link.
     */
    private class ExplorerMouseMotionListener extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            if (isFocusablePoint(e.getPoint())) {
                // We can focus on the gem and its node should be rendered as a hyperlink so change
                // to the hand cursor
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                // The mouse is not over a hyperlink so ensure that it is reset to the default cursor
                setCursor(null);
            }
        }
    }
    
    /**
     * Whenever something in the model changes we need to make sure the cursor is reset to the default
     * cursor.  If we don't then the cursor may be left as a hand even though the mouse is no longer
     * over a hyperlink.
     */
    private class ExplorerTreeModelListener implements TreeModelListener {
        public void treeNodesChanged(TreeModelEvent e) {
            setCursor(null);
        }
        
        public void treeNodesInserted(TreeModelEvent e) {
            setCursor(null);
        }

        public void treeNodesRemoved(TreeModelEvent e) {
            setCursor(null);
        }

        public void treeStructureChanged(TreeModelEvent e) {
            setCursor(null);
        }
    }
    
    /** The TableTopExplorerOwner of the TableTopExplorer using this tree. */
    private TableTopExplorerOwner owner;
    
    /** The current background image (null if none). */
    private BufferedImage backgroundImage;

    /** 
     * Controls whether the tree is drawn using the UIManager specified look and feel or a customized
     * look and feel that uses alternating light and dark bands to provide visual separation.
     */
    private boolean useBandedLookAndFeel = false;
    
    /** The set of expanded paths that was saved when the tree state was saved. */
    private Set<TreePath> savedExpandedPaths = new HashSet<TreePath>();
    
    /** The saved selection path. */
    private TreePath savedSelectionPath = null;

    /**
     * Navigation helper used to determine extra information about whether nodes can be focussed on.  The
     * navigation helper can be null in which case navigation functionality will be disabled.
     */
    private final ExplorerNavigationHelper navigationHelper;
    
    /**
     * Constructor for ExplorerGemTree.
     * @param explorerRootNode the root node to use for the tree
     * @param owner the explorer owner
     * @param tableTopExplorer the explorer this tree is for
     */
    public ExplorerTree(ExplorerRootNode explorerRootNode, TableTopExplorerOwner owner, TableTopExplorer tableTopExplorer) {
        this(explorerRootNode, owner, tableTopExplorer, null, null);
    }

    /**
     * Constructor for ExplorerGemTree.
     * @param explorerRootNode the root node to use for the tree
     * @param owner the explorer owner
     * @param tableTopExplorer the explorer this tree is for
     * @param navigationHelper A helper that allows navigation between tree nodes
     * @param cellRenderer A customized cell renderer.  If this is null then the default renderer will be
     * used
     */
    public ExplorerTree(ExplorerRootNode explorerRootNode,
            TableTopExplorerOwner owner,
            TableTopExplorer tableTopExplorer,
            ExplorerNavigationHelper navigationHelper,
            DefaultTreeCellRenderer cellRenderer) {
        
        super(explorerRootNode);
        
        this.owner = owner;
        this.navigationHelper = navigationHelper;
        
        setEditable(true);
        setRowHeight(-1);
        setInvokesStopCellEditing(true);
        setFocusCycleRoot(true);
        
        // Create a default cell renderer if necessary
        if (cellRenderer == null) {
            cellRenderer = new ExplorerCellRenderer(owner);
        }
            
        setCellRenderer(cellRenderer);
        setCellEditor(new ExplorerCellEditor(this, cellRenderer, new ExplorerTreeCellEditor(tableTopExplorer)));
        
        TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        setSelectionModel(selectionModel);
        
        ToolTipManager.sharedInstance().registerComponent(this);

        // If we have a navigation helper then add a mouse listeners to change the cursor when it
        // is over a hyperlink, focus on a node when clicked, and listen to model changes to ensure
        // the cursor is reset correctly.
        if (navigationHelper != null) {
            addMouseListener(new ExplorerMouseEventListener());
            addMouseMotionListener(new ExplorerMouseMotionListener());
            getModel().addTreeModelListener(new ExplorerTreeModelListener());
        }
    }

    /**
     * @see javax.swing.JComponent#getToolTipText()
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        
        TreePath treePath = getPathForLocation(e.getX(), e.getY());

        if (treePath == null) {
            return null;
        }
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Object userObject = node.getUserObject();
        
        if (userObject instanceof Gem.PartInput) {
            return owner.getHTMLFormattedMetadata((Gem.PartInput) userObject);
            
        } else if (userObject instanceof FunctionalAgentGem) {
            return owner.getHTMLFormattedFunctionalAgentGemDescription((FunctionalAgentGem) userObject);
            
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
            toolTip += ExplorerMessages.getString("ResultTypeToolTip", owner.getTypeString(((CollectorGem) userObject).getResultType()));
            
            // End with the end html tag
            toolTip += "</html>";
            return toolTip;
            
        } else if (userObject instanceof ReflectorGem) {
            return "<html>" + ExplorerMessages.getString("OutputTypeToolTip", owner.getTypeString(((ReflectorGem)userObject).getOutputPart().getType())) + "</html>";
            
        } else if (userObject instanceof CodeGem) {
            return "<HTML><B>" + ((CodeGem) userObject).getUnqualifiedName() + "</B></HTML>";
        
        } else if (userObject instanceof RecordFieldSelectionGem) {
            return "<html>" + ExplorerMessages.getString("FieldToExtractToolTip", ((RecordFieldSelectionGem)userObject).getFieldName().toString() + "</html>");
        }
        
        return null;
    }
    
    /**
     * @see java.awt.dnd.Autoscroll#autoscroll(Point)
     */
    public void autoscroll(Point point) {
        Rectangle visibleRect = getVisibleRect();
        Insets insets =  getAutoscrollInsets();
        int offset = (point.y > (getHeight() - insets.bottom)) ? 20 : -20;
        scrollRectToVisible(new Rectangle(visibleRect.x, visibleRect.y + offset, visibleRect.width, visibleRect.height));
    }
    
    /**
     * @see java.awt.dnd.Autoscroll#getAutoscrollInsets()
     */
    public Insets getAutoscrollInsets() {
        Rectangle visibleRect = getVisibleRect();
        int topScrollSection = visibleRect.y + 30;
        int bottemScrollSection = getHeight() - (visibleRect.height + visibleRect.y - 30);
        return new Insets (topScrollSection, 0, bottemScrollSection, 0);
    }
    
    /**
     * @see javax.swing.JComponent#paintComponent(Graphics)
     * Paint the Explorer
     * @param g java.awt.Graphics
     */
    @Override
    public void paintComponent(java.awt.Graphics g) {

        if (backgroundImage != null) {

            // Paint a tiled image          
            Rectangle bounds = g.getClipBounds();

            // Determine which tiled instances intersect with bounds and draw them
            int imageWidth = backgroundImage.getWidth();
            int imageHeight = backgroundImage.getHeight();
            
            int offsetX = (bounds.x / imageWidth) * imageWidth;
            int offsetY = (bounds.y / imageHeight) * imageHeight;

            for (int yRegistration = offsetY; yRegistration < bounds.y + bounds.height; yRegistration += imageHeight) {
                for (int xRegistration = offsetX; xRegistration < bounds.x + bounds.width; xRegistration += imageWidth) {
                    g.drawImage(backgroundImage, xRegistration, yRegistration, null);
                }
            }
        }
        
        // Just paint a simple background!
        super.paintComponent(g);
    }
    
    /**
     * Sets the background image of this tree.
     * @param backgroundImage the background image to use, null for normal background.
     */
    public void setBackgroundImage(BufferedImage backgroundImage) {
        
        // we lighten the colours a bit to make it easier to read.
        if (backgroundImage == null) {
            this.backgroundImage = null;
            setOpaque(true);
            repaint();    
            return;
        }
        
        RescaleOp rescaleOp = new RescaleOp(1.1f, 35, null);
        
        // Create an RGB buffered image
        BufferedImage bimage = new BufferedImage(backgroundImage.getWidth(null), backgroundImage.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);

        // Copy non-RGB image to the RGB buffered image
        Graphics2D g = bimage.createGraphics();
        g.drawImage(backgroundImage, 0, 0, null);
        
        // Copy non-RGB image to the RGB buffered image
        this.backgroundImage = rescaleOp.filter(bimage, null);
        setOpaque(false);
        repaint();
    }

    /**
     * @param useBandedLookAndFeel true if the banded look&feel should be used, false for normal LAF
     */
    public void setBandedLookAndFeel(boolean useBandedLookAndFeel) {
        
        this.useBandedLookAndFeel = useBandedLookAndFeel;
        if (useBandedLookAndFeel) {
            super.setUI(new BandedTreeUI());
        } else {    
            super.setUI(UIManager.getUI(this));
        }

        // Inform the cell renderer since it will behave slightly differently for the banded look and feel
        TreeCellRenderer cellRenderer = getCellRenderer();
        if (cellRenderer instanceof ExplorerCellRenderer) {
            ((ExplorerCellRenderer)cellRenderer).setBandedLookAndFeel(useBandedLookAndFeel);
        }
    }
    
    /**
     * Overide this method so that we can explicitly use a special banded look and feel even if a different
     * look and feel is requested.
     * @param newUI
     */
    @Override
    public void setUI(TreeUI newUI) {
        if (useBandedLookAndFeel) {
            super.setUI(new BandedTreeUI());
        } else { 
            super.setUI(newUI);
        }
    }
    
    
    /**
     * Returns the user object stored in the node underneath specified location.
     * @param location the location for the node
     * @param source the component in whose coordinate space the location is
     * @return the user object or null if there is no node at the given location
     */
    public Object getUserObjectAt(Point location, JComponent source) {
        return getUserObjectAt(SwingUtilities.convertPoint(source, location, this));
    }
    
    /**
     * Returns the user object stored in the node underneath specified location.
     * @param location the location for the node
     * @return the user object or null if there is no node at the given location
     */
    public Object getUserObjectAt(Point location) {
        TreePath path = getPathForLocation(location.x, location.y);
        return path == null ? null : ((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject();
    }

    /**
     * Determines if the point in question is over a focussable point in the tree.  The node at this point
     * will be rendered as a hyperlink if this is a focussable point so this method can be used to check
     * if the cursor should be changed or to take action when the user clicks.
     * @param location The point that will be checked.  This point should be relative to the explorer tree.
     * @return Returns true if the point is over a hyperlink rendered node and false if not.
     */
    boolean isFocusablePoint(Point location) {
        // As a safety precaution, bail early if we don't have a navigation helper
        if (navigationHelper == null) {
            return false;
        }
        
        // Get the tree path for the specified location
        TreePath path = getPathForLocation(location.x, location.y);
        if (path != null) {
            // We don't want to consider the icon as part of the hyperlink
            Rectangle pathBounds = getPathBounds(path);
            Point relativePoint = new Point(location.x - pathBounds.x, location.y - pathBounds.y);
            ExplorerCellRenderer renderer = (ExplorerCellRenderer)getCellRenderer(); 
            if (!renderer.isPointOverIcon(relativePoint)) {
                Object node = path.getLastPathComponent();
                if (node instanceof ExplorerGemNode) {
                    // Check if the gem is focusable which means it will be rendered as a hyperlink
                    Gem gem = ((ExplorerGemNode)node).getGem();
                    if (navigationHelper.isFocusable(gem)) {
                        return true;
                    }
                }
            }
        }
        
        // Not a focusable point
        return false;
    }
    
    /**
     * Saves the current state of the tree: the selected node and the expanded nodes.
     */
    void saveState () {
        
        // Remember the currently expanded nodes.
        Enumeration<TreePath> expandedPaths = getExpandedDescendants(new TreePath(getModel().getRoot()));
        savedExpandedPaths = new HashSet<TreePath>();
        
        while (expandedPaths != null && expandedPaths.hasMoreElements()) {
            TreePath path = expandedPaths.nextElement();
            savedExpandedPaths.add(path);
        }

        // Remember the selected node.
        savedSelectionPath = getSelectionPath();
    }

    /**
     * Restores the state that was last saved. Does nothing if no state was ever saved. The restored
     * state might not exactly match the saved state if the tree model changed in such a way that a 
     * full restore is not possible (ie: nodes removed).
     */    
    void restoreSavedState () {
        
        for (final TreePath treePath : savedExpandedPaths) {
            this.expandPath(treePath);
        }
        
        if (savedSelectionPath != null) {
            setSelectionPath(savedSelectionPath);
        }
    }
}


/**
 * Provides a customized look and feel based that is identical to the BasicTreeUI except for alternating
 * bands of colour to separate the top level of nodes in the tree.  When the root node is hidden this
 * provides separation between what appear to be the 'root' nodes of the tree.
 */
class BandedTreeUI extends BasicTreeUI {

    /** Color for the light band */
    private Color lightBand = new Color(220, 220, 220, 100);
    
    /** Color for the dark band. */
    private Color darkBand = new Color(180, 180, 180, 100);
    
    /**
     * @see javax.swing.plaf.basic.BasicTreeUI#paintRow(java.awt.Graphics, java.awt.Rectangle, java.awt.Insets, java.awt.Rectangle, javax.swing.tree.TreePath, int, boolean, boolean, boolean)
     */
    @Override
    protected void paintRow(Graphics g,
            Rectangle clipBounds,
            Insets insets,
            Rectangle bounds,
            TreePath path,
            int row,
            boolean isExpanded,
            boolean hasBeenExpanded,
            boolean isLeaf) {
        
        // Don't paint the renderer if editing this row.
        if(editingComponent != null && editingRow == row) {
            return;
        }
        
        Component component = null;
        TreeNode currentNode = (TreeNode)path.getLastPathComponent();
        TreeNode rootNode = (TreeNode)getModel().getRoot();
        
        if (currentNode != rootNode) {
            
            // Determine the colour for this particular node so that we alternate colour for
            // top level gems
            TreeNode topLevelNode = getTopLevelNode(rootNode, currentNode);
            int index = getModel().getIndexOfChild(rootNode, topLevelNode);
            if (index % 2 == 0) {
                g.setColor(darkBand);
            } else {
                g.setColor(lightBand);
            }

            // Fill in the entire space available for the current row with the previously set colour
            g.fillRect(clipBounds.x, bounds.y, clipBounds.width, bounds.height);
        }

        component = currentCellRenderer.getTreeCellRendererComponent (tree, path.getLastPathComponent(),
                tree.isRowSelected(row), isExpanded, isLeaf, row,
                tree.hasFocus());
        
        rendererPane.paintComponent (g, component, tree, bounds.x, bounds.y,
                bounds.width, bounds.height, true); 
    }
    
    /**
     * Method to retrieve the top level node for the specified node. The top level node is defined to be
     * the ancestor that is one level below the root. In other words, with the root node hidden it will
     * be the top node drawn in the tree. This method should not be called on the root gem or a
     * NullPointerException will be thrown.
     * @param rootNode the real root node
     * @param currentNode the current node whose top level parent we are searching for
     * @return TreeNode the top-level parent of the current node
     */
    private TreeNode getTopLevelNode(TreeNode rootNode, TreeNode currentNode) {

        if (currentNode == null) {
            return null;
        }
        
        // Determine if this is the top level node, otherwise recurse up our parent again.
        TreeNode parentNode = currentNode.getParent();
        if (parentNode == rootNode) {
            return currentNode;
        } else {
            return getTopLevelNode(rootNode, parentNode);
        }
    }
}
