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
 * UtilTree.java
 * Creation date: (09/15/04)
 * By: Richard Webster
 */
package org.openquark.util.ui;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * A tree control with some utility functions.
 * @author Richard Webster
 */
public class UtilTree extends JTree {

    private static final long serialVersionUID = 1094185551776056164L;

    /** The set of paths to nodes that were expanded when the tree state was saved. */
    protected Set<TreePath> savedExpandedPaths = new HashSet<TreePath>();
    
    /** The path to the node that was selected when the tree state was saved. */
    protected TreePath savedSelectionPath = null;

    /**
     * UtilTree constructor.
     */
    public UtilTree() {
        super();
    }

    /**
     * UtilTree constructor.
     * @param newModel  the tree model
     */
    public UtilTree(TreeModel newModel) {
        super(newModel);
    }

    /**
     * Saves the current state of the tree: the selected node and the expanded nodes. If you reload
     * the tree model you might want to save the state and restore it afterwards.
     * <p>
     * <strong>This should be run on the AWT event-handler thread.</strong>
     */
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

        // Remember the selected node.
        savedSelectionPath = getSelectionPath();
    }

    /**
     * Restores the state that was last saved. Does nothing if no state was ever saved. The restored
     * state might not exactly match the saved state if the tree model changed in such a way that a 
     * full restore is not possible (ie: nodes removed).
     * <p>
     * <strong>This should be run on the AWT event-handler thread.</strong>
     */    
    public void restoreSavedState() {
        
        for (final TreePath treePath : savedExpandedPaths) {
            this.expandPath(treePath);
        }
        
        if (savedSelectionPath != null) {
            setSelectionPath(savedSelectionPath);
        }
    }
    
    /**
     * Scrolls the given tree path to the top of the tree or as close to the top as possible.
     * <p>
     * <strong>This should be run on the AWT event-handler thread.</strong>
     * @param path the path to scroll to the top
     */
    public void scrollPathToTop(TreePath path) {
     
        makeVisible(path);
    
        JViewport viewport = getViewport();
        Rectangle pathBounds = getPathBounds(path);

        if (pathBounds != null && viewport != null) {
            
            // Calculate the new view position
            Point p = new Point(0, pathBounds.y);
            
            // Sanity check the new view position
            if (getHeight() - p.y < viewport.getExtentSize().height) {
                p.y = getHeight() - viewport.getExtentSize().height;
            }

            viewport.setViewPosition(p);
        }
    }        

    /**
     * Scrolls the given tree path into view. This is different from scrollPathToVisible in
     * that it tries to place the node this path points to into the top 1/4 pixels of the tree.
     * scrollPathToVisible simply places the node at the very top or very bottom of the tree
     * so that it is just visible. Using this function instead makes for a nicer appearance
     * to the user. 
     * <p>
     * <strong>This should be run on the AWT event-handler thread.</strong>
     * @param path the path to scroll into view
     */
    public void scrollPathIntoView (TreePath path) {

        makeVisible(path);

        JViewport viewport = getViewport();    
        Rectangle pathBounds = getPathBounds(path);

        if (pathBounds != null && viewport != null) {
            
            // Calculate the new view position
            int quarter = viewport.getExtentSize().height / 4;
            Point p = new Point(0, pathBounds.y - quarter);
            
            // Sanity check the new view position
            if (p.y < 0) {
                p.y = 0;
            } else if (getHeight() - p.y < viewport.getExtentSize().height) {
                p.y = getHeight() - viewport.getExtentSize().height;
            }

            viewport.setViewPosition(p);
        }
    }

    /**
     * This function finds the JViewport this tree is embedded in.
     * @return the viewport or null if there is no viewport
     */
    private JViewport getViewport() {
        // Find the viewport of this tree.
        Container parent = getParent();
        while (parent != null && !(parent instanceof JViewport)) {
            parent = parent.getParent ();
        }
        
        return (JViewport) parent;
    }
}
