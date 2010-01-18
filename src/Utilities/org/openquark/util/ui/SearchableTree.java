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
 * SearchableTree.java
 * Created: Sept 15, 2004
 * By: Richard Webster
 */
package org.openquark.util.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.openquark.util.Messages;


/**
 * A tree which can show search results.
 * @author Richard Webster
 */
public class SearchableTree implements SearchableComponent, TreeModelListener {

    /** Use this message bundle to get string resources. */
    private static Messages messages = UIMessages.instance;

    /** The tree to make searchable. */
    private final UtilTree tree;

    /** The parent node for all search results. */
    private DefaultMutableTreeNode searchNode;

    /** Keep track of the last search performed. */
    private String lastSearchText;

    /** Keep track of whether a search is in progress. */
    private boolean searching = false;

// TODO: rerunning the search each time a change is made to the tree could be problematic if many changes are done at once...
//       If this turns out to be a problem, then it might be better to use a timer to rerun the search after a small delay.
//    /** The timer used for updating the search results after changes are made to the tree. */
//    private Timer searchUpdateTimer;

    /**
     * SearchableTree constructor.
     * @param tree  the tree to make searchable
     */
    public SearchableTree(final UtilTree tree) {
        super();
        this.tree = tree;

        tree.getModel().addTreeModelListener(SearchableTree.this);

        tree.addPropertyChangeListener(JTree.TREE_MODEL_PROPERTY, new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    rerunLastSearch();
                    tree.getModel().addTreeModelListener(SearchableTree.this);
                }
            });

        installRenderer(tree);
        
//        // A timer that runs once changes are made to the tree.
//        searchUpdateTimer = new Timer(250, new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    if (e.getSource() == searchUpdateTimer) {
//                        rerunLastSearch();
//                    }
//                }
//            });
//        searchUpdateTimer.setRepeats(false);
    }

    /**
     * Reruns the last search on the tree.
     * This can be called after updating the tree to apply the search criteria to the new tree contents.
     */
    public void rerunLastSearch() {
        if (lastSearchText != null && lastSearchText.length() > 0) {
            showSearchResults(lastSearchText, false);
        }
    }

    /**
     * Returns the tree model.
     * @return the tree model
     */
    private DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel) tree.getModel();
    }

    /**
     * Returns the root node of the tree.
     * @return the root node of the tree
     */
    private DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode) getTreeModel().getRoot();
    }

    /**
     * Installs appropriate renderer to the tree to highlight search results.
     * @param tree
     */
    private void installRenderer(final UtilTree tree) {
        // Wrap the existing cell renderer with a search tree cell renderer
        TreeCellRenderer renderer = tree.getCellRenderer();
        if (!(renderer instanceof SearchableTreeCellRenderer)) {
            tree.setCellRenderer(SearchableTreeCellRenderer.create(renderer));
        }
        
        // Install a listener that listens for cell renderer changes
        tree.addPropertyChangeListener(
                JTree.CELL_RENDERER_PROPERTY,
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        TreeCellRenderer renderer = (TreeCellRenderer) evt.getNewValue();
                        tree.setCellRenderer(SearchableTreeCellRenderer.create(renderer));
                    }
                }
        );
    }

    /**
     * Sets up the tree for showing search results.
     */
    private void initSearch() {
        final DefaultMutableTreeNode rootNode = getRootNode();

        // If the search node already exists, then make sure it is in the correct spot in the tree.
        if (searchNode != null) {
            int searchIndex = rootNode.getIndex(searchNode);

            // If the search node is already the last child of the root, then do nothing.
            if (rootNode.getChildCount() != 0 && searchIndex == rootNode.getChildCount() - 1) {
                return;
            }          

            // If the search node is in the wrong spot, then remove it.
            if (searchIndex > 0) {
                rootNode.remove(searchNode);
            }
        }

        // Create the search node, if necessary.
        if (searchNode == null) {
            searchNode = new DefaultMutableTreeNode(new DisplayOnlyTreeItem(messages.getString("Search_Results_TreeNode_Caption"), UIUtilities.LoadImageIcon("/Resources/search_results.gif")));  //$NON-NLS-1$//$NON-NLS-2$
        }
        
        // Add the search node as the last child of the root.
        rootNode.add(searchNode);
        getTreeModel().nodeStructureChanged(rootNode);
    }
    /**
     * Updates the tree to show search results for any items matching the specified substring.
     * If an empty string is provided, then the search results will be cleared.
     * @param searchSubstring  the substring to be searched for, or an empty string to clear the search results
     */
    public void showSearchResults(String searchSubstring) {
        showSearchResults(searchSubstring, true);
    }

    /**
     * Updates the tree to show search results for any items matching the specified substring.
     * If an empty string is provided, then the search results will be cleared.
     * @param searchSubstring  the substring to be searched for, or an empty string to clear the search results
     * @param scrollIntoView   if True then the search results will be scrolled into view 
     */
    public void showSearchResults(String searchSubstring, boolean scrollIntoView) {
        // Don't start another search when already searching.
        if (searching) {
            return;
        }
        searching = true;

        // Trim any leading and trailing spaces from the search substring
        searchSubstring = searchSubstring.trim();

        // Set the search string in the renderer so that the renderer knows what to highlight
        TreeCellRenderer renderer = tree.getCellRenderer();
        if (renderer instanceof SearchableTreeCellRenderer) {
            ((SearchableTreeCellRenderer) renderer).setSearchString(searchSubstring);
        }
        
        try {
            final DefaultMutableTreeNode rootNode = getRootNode();

            tree.saveState();

            // Make sure that the search node has been added to the tree.
            initSearch();

            // Clear the current search, if any.
            searchNode.removeAllChildren();

            if (searchSubstring == null || searchSubstring.length() == 0) {
                // Remove the search node if the search text is cleared.
                rootNode.remove(searchNode);
                getTreeModel().nodeStructureChanged(rootNode);

                tree.restoreSavedState();

                // If we didn't search for anything scroll to the all first tree node.
                if (rootNode.getChildCount() > 0) {
                    DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) rootNode.getFirstChild();
                    final TreePath firstNodePath = new TreePath(firstNode.getPath());
                    tree.setSelectionPath(firstNodePath);
                    tree.scrollPathToTop(firstNodePath);
                }

                return;
            }

            // Search for the substring without matching case.
            searchSubstring = searchSubstring.toLowerCase();
            List<MutableTreeNode> resultNodes = getSearchResults(searchSubstring);

            for (final MutableTreeNode mutableTreeNode : resultNodes) {
                searchNode.add(mutableTreeNode);
            }

            // Change the search node text to include the number of search results.
            String searchNodeText = messages.getString("Search_Results_TreeNode_Caption") + " (" + resultNodes.size() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            searchNode.setUserObject(new DisplayOnlyTreeItem(searchNodeText, UIUtilities.LoadImageIcon("/Resources/search_results.gif"))); //$NON-NLS-1$

            getTreeModel().nodeStructureChanged(searchNode);

            tree.restoreSavedState();

            // Expand the search node, select it, and scroll it into view.
            final TreePath searchNodePath = new TreePath(searchNode.getPath());
            tree.expandPath(searchNodePath);
            tree.setSelectionPath(searchNodePath);

            if (scrollIntoView) {
                // Invoke this later once the tree has synced up with the model.
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        tree.scrollPathToTop(searchNodePath);
                    }
                });
            }
        }
        finally {
            // Keep track of the last search text.
            this.lastSearchText = searchSubstring;

            searching = false;
        }
    }

    /**
     * Returns a list of nodes for the values matching the search string.
     * @param searchString  the string to search for
     * @return a list of nodes for the values matching the search string
     */
    private List<MutableTreeNode> getSearchResults(String searchString) {
        final DefaultMutableTreeNode rootNode = getRootNode();

        List<MutableTreeNode> resultNodes = new ArrayList<MutableTreeNode>();
        if (rootNode != null) {
            addNodeSearchResults(searchString, resultNodes, rootNode);
        }

        // Sort the resulting nodes by their display names (not case sensitive).
        Collections.sort(resultNodes, new Comparator<MutableTreeNode>() {
            public int compare(MutableTreeNode node1, MutableTreeNode node2) {
                return node1.toString().compareToIgnoreCase(node2.toString());
            }});

        return resultNodes;
    }

    /**
     * A recursive helper function for searching all nodes in a tree.
     * @param searchString  the search text
     * @param resultNodes   the current list of result nodes
     * @param node          the node to be searched
     */
    private void addNodeSearchResults(String searchString, List<MutableTreeNode> resultNodes, TreeNode node) {
        // Check whether the current node is searchable.
        if (node instanceof SearchableTreeNode) {
            SearchableTreeNode searchableNode = (SearchableTreeNode) node;
            MutableTreeNode searchResult = searchableNode.searchNode(searchString);

            if (searchResult != null) {
                resultNodes.add(searchResult);
            }
        }

        // Check any child nodes as well.
        for (int childN = 0, nChildren = node.getChildCount(); childN < nChildren; ++childN) {
            addNodeSearchResults(searchString, resultNodes, node.getChildAt(childN));
        }
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event.TreeModelEvent)
     */
    public void treeNodesChanged(TreeModelEvent e) {
//      if (!searching) {
//          searchUpdateTimer.restart();
//      }
        rerunLastSearch();
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesInserted(javax.swing.event.TreeModelEvent)
     */
    public void treeNodesInserted(TreeModelEvent e) {
//      if (!searching) {
//          searchUpdateTimer.restart();
//      }
        rerunLastSearch();
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeNodesRemoved(javax.swing.event.TreeModelEvent)
     */
    public void treeNodesRemoved(TreeModelEvent e) {
//      if (!searching) {
//          searchUpdateTimer.restart();
//      }
        rerunLastSearch();
    }

    /**
     * @see javax.swing.event.TreeModelListener#treeStructureChanged(javax.swing.event.TreeModelEvent)
     */
    public void treeStructureChanged(TreeModelEvent e) {
//      if (!searching) {
//          searchUpdateTimer.restart();
//      }
        rerunLastSearch();
    }
}
