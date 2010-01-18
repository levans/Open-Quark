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
 * NavTree.java
 * Creation date: Jul 3, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.openquark.util.UnsafeCast;

/**
 * This class implements the navigation tree component for the CAL Navigator.
 * 
 * @author Frank Worsley
 */
public class NavTree extends JTree {

    private static final long serialVersionUID = 4667100235898468183L;

    /**
     * Constructs a new NavTree.
     */
    public NavTree() {
        super(new NavTreeModel());
        
        setCellRenderer(new NavTreeCellRenderer());

        setShowsRootHandles(true);
        setRootVisible(false);
        setAutoscrolls(true);
        setToolTipText("NavTree");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NavTreeModel getModel() {
        return (NavTreeModel)super.getModel();
    }

    /**
     * Collapses all expanded tree paths so that only the root node is expanded
     * and the module nodes are visible.
     */
    // todo-jowong this is not currently used, but can be useful in hierarchical view
    public void collapseToModules() {
        
        final Set<NavTreeNode> keepExpanded = new HashSet<NavTreeNode>();

        // first we expand
        for (int i = 0; i < getRowCount(); i++) { // note that getRowCount() may change as the tree changes
            final TreePath path = getPathForRow(i);
            
            if (path != null) {
                final NavTreeNode treeNode = (NavTreeNode) path.getLastPathComponent();
                
                if (treeNode instanceof NavModuleNamespaceNode) {
                    // we do not collapse intermediate namespace nodes so as to keep their descendant modules visible
                    expandRow(i);
                    keepExpanded.add(treeNode);
                    
                } else if (treeNode instanceof NavModuleNode) {
                    // we do not collapse module nodes with descendant modules
                    boolean shouldExpand = false;
                    for (final Enumeration<NavTreeNode> children = UnsafeCast.unsafeCast(treeNode.children()); children.hasMoreElements(); ) {  // actually unsafe
                        NavTreeNode child = children.nextElement();

                        if (child instanceof NavModuleNode || child instanceof NavModuleNamespaceNode) {
                            shouldExpand = true;
                            break;
                        }
                    }
                    
                    if (shouldExpand) {
                        expandRow(i);
                        keepExpanded.add(treeNode);
                    }
                }
            }
        }

        // then we collapse
        for (int i = getRowCount() - 1; i >= 0; i--) { // note that getRowCount() may change as the tree changes
            final TreePath path = getPathForRow(i);
            
            if (path != null) {
                final NavTreeNode treeNode = (NavTreeNode) path.getLastPathComponent();
                
                if (!keepExpanded.contains(treeNode)) {
                    collapseRow(i);
                }
            } else {
                collapseRow(i);
            }
        }
}
    
    /**
     * @return the tooltip text for the node the mouse is hovering over or
     * null if there is no node at that location
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        
        Point location = e.getPoint();
        TreePath path = getPathForLocation(location.x, location.y);
        
        if (path != null) {
            NavTreeNode treeNode = (NavTreeNode) path.getLastPathComponent();
            return treeNode.getToolTipText();
        }
        
        return null;
    }
}
