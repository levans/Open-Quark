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
 * GemBrowser.java
 * Creation date: Mar 4, 2003
 * By: David Mosimann
 */
package org.openquark.gems.client.browser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.ModuleNameDisplayUtilities.TreeViewDisplayMode;


/**
 * This class encapsulates the functionality of the gem browser and a set of buttons to manipulate the
 * tree sorting.  After constructing the object the initialize method must be called to fill the tree.
 * The UI component can then be retrieved via the getGemBrowserPanel() method.
 */
public class GemBrowser {
    /** The BrowserTree that displays the browser tree model */
    private final BrowserTree browserTree;
        
    /** If true the control buttons at the bottom of the tree are shown. */
    private final boolean showControlPanel;
    
    /** The JPanel that contains the tree and control buttons */
    private JPanel gemBrowserPanel;

    /** The timer used for updating the search results if the user stopped typing */
    private Timer searchUpdateTimer;
    
    /* Preference key names. */
    private static final String BROWSER_MODULE_TREE_DISPLAY_MODE = "browserModuleTreeDisplayMode";
    /**
     * Constructor for a GemBrowser
     * Note that the browser tree is initially empty and will need to be filled with data from the current Perspective
     */
    public GemBrowser() {
        this(true);
    }
    
    /**
     * Constructor for a GemBrowser
     * Note that the browser tree is initially empty and will need to be filled with data from the current Perspective
     * @param showControlPanel whether to show the control panel (buttons to control sorting/categorization)
     */
    public GemBrowser(boolean showControlPanel) {
        
        // Note that initialize() MUST be called before this object will work correctly
        this.showControlPanel = showControlPanel;

        browserTree = new BrowserTree();
        browserTree.setToolTipText("BrowserTreeToolTip");
    }

    /**
     * Initializes the tree based on the perspective passed in. If this GemBrowser needs to be
     * initialized again, use {@link #reinitialize} instead.
     * 
     * @param perspective
     * @param showAllModules whether to show modules not visible from the given perspective.
     * @param highlightCurrentModule whether the current module should be highlighted.
     */
    public void initialize(Perspective perspective, boolean showAllModules, boolean highlightCurrentModule) {
        
        final TreeViewDisplayMode preferredModuleTreeDisplayMode = getPreferredModuleTreeDisplayMode();
        
        // Fill in the gem browser with the available gems
        BrowserTreeModel browserTreeModel = (BrowserTreeModel)getBrowserTree().getModel();
        browserTreeModel.clearDrawers();
        browserTreeModel.populate(perspective, showAllModules, preferredModuleTreeDisplayMode);

        // Clean up the default display a little
        browserTree.setHighlightCurrentModule(highlightCurrentModule);
        browserTreeModel.arrangeAsDefaultTreeStructure(preferredModuleTreeDisplayMode);
        
        // expandLevel modifies the view of the tree and thus needs to run on the AWT event-handler thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                browserTree.expandLevel(1);
            }
        });
        
        // Update the state of the buttons
        browserTree.getBrowserTreeActions().getDisplayUnimportedGemsAction().putValue(GemBrowserActionKeys.ACTION_SELECTED_KEY, Boolean.valueOf(showAllModules));
        
        if (preferredModuleTreeDisplayMode == TreeViewDisplayMode.FLAT_ABBREVIATED) {
            browserTree.getBrowserTreeActions().getCategorizeByModuleFlatAbbreviatedButton().getModel().setSelected(true);
        } else if (preferredModuleTreeDisplayMode == TreeViewDisplayMode.FLAT_FULLY_QUALIFIED) {
            browserTree.getBrowserTreeActions().getCategorizeByModuleFlatFullyQualifiedButton().getModel().setSelected(true);
        } else if (preferredModuleTreeDisplayMode == TreeViewDisplayMode.HIERARCHICAL) {
            browserTree.getBrowserTreeActions().getCategorizeByModuleHierarchicalButton().getModel().setSelected(true);
        } else {
            throw new IllegalStateException("Unexpected: " + preferredModuleTreeDisplayMode);
        }
    }

    /**
     * Refreshes the tree using the perspective and other parameters it was originally 
     * initialized with. This will remember the tree expansion & selection state and
     * restore it after reloading the tree model.
     */
    public void refresh() {

        final BrowserTree browserTree = getBrowserTree();
        final BrowserTreeModel browserTreeModel = (BrowserTreeModel) browserTree.getModel();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Save the tree state
                browserTree.saveState();
                
                // Reload the model
                browserTreeModel.reload();
                
                // Restore the tree state
                browserTree.restoreSavedState();        
            }
        });
    }
    
    /**
     * Reinitializes the tree based on the perspective passed in. This will remember the tree expansion & selection state and
     * restore it after reloading the tree model.
     * 
     * This is very similar to {@link #refresh}, which uses the existing perspective when reloading the tree model.
     * 
     * @param perspective
     */
    public void reinitialize(final Perspective perspective) {
        final BrowserTreeModel browserTreeModel = (BrowserTreeModel)getBrowserTree().getModel();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                browserTree.saveState();

                // Reload the model
                browserTreeModel.setPerspectiveAndReload(perspective);

                browserTree.restoreSavedState();        
            }
        });
    }
    
    /**
     * Return the GemBrowserPanel property value.
     * @return JPanel
     */
    public JPanel getGemBrowserPanel() {
        
        if (gemBrowserPanel == null) {
            
            // First wrap the gem browser tree in a scoll pane
            JScrollPane scrollableBrowserTree = new JScrollPane(getBrowserTree());
            
            gemBrowserPanel = new JPanel();
            gemBrowserPanel.setName("GemBrowserPanel");
            gemBrowserPanel.setLayout(new BorderLayout());
            gemBrowserPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            gemBrowserPanel.add(scrollableBrowserTree, BorderLayout.CENTER);
            
            if (showControlPanel) {
                
                // Add the main vbox
                Box vbox = Box.createVerticalBox();
                gemBrowserPanel.add(vbox, BorderLayout.SOUTH);

                // Add an hbox for the search text field right below the tree
                Box hbox = Box.createHorizontalBox();
                vbox.add(hbox);
                hbox.add(getSearchTextField());
                
                // Add an hbox for the buttons below the search text field
                hbox = Box.createHorizontalBox();
                vbox.add(Box.createVerticalStrut(5));
                vbox.add(hbox);
                
                hbox.add(Box.createHorizontalGlue());
                hbox.add(browserTree.getBrowserTreeActions().getBrowserTreeButtonPanel());
                hbox.add(Box.createHorizontalGlue());
                
                vbox.add(Box.createVerticalStrut(5));
            }
        }
        
        return gemBrowserPanel;
    }

    /**
     * Returns the search text field the use can use to search for gems.
     * @return JTextField
     */
    private JTextField getSearchTextField () {

        final JTextField searchField = new JTextField();
        searchField.setColumns(15);
        searchField.setToolTipText(BrowserMessages.getString("GB_SearchFieldToolTip"));
        
        // A timer that runs once the user stops typing.
        // This will execute the model search with the text the user has entered.
        searchUpdateTimer = new Timer(250, new ActionListener() {

            BrowserTreeModel browserTreeModel = (BrowserTreeModel) browserTree.getModel();
            
            public void actionPerformed(ActionEvent e) {
                
                if (e.getSource() == searchUpdateTimer) {

                    browserTree.saveState();
                    browserTreeModel.doSearch(searchField.getText());
                    browserTree.restoreSavedState();

                    if (searchField.getText().trim().equals("")) {
                        // If we didn't search for anything scroll to the workspace node.
                        BrowserTreeNode workspaceNode = browserTreeModel.getWorkspaceNode();
                        TreePath workspaceNodePath = new TreePath(workspaceNode.getPath());
                        browserTree.scrollPathToTop(workspaceNodePath);
                        browserTree.setSelectionPath(workspaceNodePath);
                    
                    } else {
                        // Invoke this later once the tree has synced up with the model.
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                TreePath searchNodePath = new TreePath(browserTreeModel.getSearchResultsNode().getPath());                       
                                browserTree.expandPath(searchNodePath);
                                browserTree.setSelectionPath(searchNodePath);
                                browserTree.scrollPathToTop(searchNodePath);
                            }
                        });
                    }
                }
            }
        });

        searchUpdateTimer.setRepeats(false);
        
        // Add a document listener so we can update the search results if the text changes.
        searchField.getDocument().addDocumentListener(new DocumentListener () {
                    
            public void insertUpdate(DocumentEvent e) {
                searchUpdateTimer.setInitialDelay(searchUpdateDelay());
                searchUpdateTimer.restart();
            }
                    
            public void removeUpdate(DocumentEvent e) {
                searchUpdateTimer.setInitialDelay(searchUpdateDelay());
                searchUpdateTimer.restart();
            }
                    
            public void changedUpdate(DocumentEvent e) {
            }
            
            /**
             * Return the number of milliseconds to wait until we start searching.  This number varies depending
             * upon the amount of text that has been entered by the user.  If the user has entered only one or
             * two characters, then we use a long delay, otherwise a standard one.  This makes it less likely that
             * we will do a costly search that returns thousands of results.
             * @return int
             */
            private int searchUpdateDelay() {
                if (searchField.getText().length() > 0 && searchField.getText().length() < 3) {
                    return 750;
                } else {
                    return 250;
                }
            }
        });
        
        return searchField;
    }        

    
    /**
     * Return the browser tree from this browser.
     * @return BrowserTree
     */
    public BrowserTree getBrowserTree() {
        return browserTree;
    }

    /**
     * Sets the name of the workspace visible in square brackets in the
     * Workspace node of the Gem Browser tree. 
     * @param name the name to display
     */
    public void setWorkspaceNodeName(String name) {
        TreeModel treeModel = getBrowserTree().getModel();
        
        if (treeModel instanceof BrowserTreeModel) {
            BrowserTreeModel browserTreeModel = (BrowserTreeModel)treeModel;
            browserTreeModel.setWorkspaceNodeName(name);
        }
    }
    
    /**
     * Marks the current workspace as being "dirty" (changed from the saved
     * version) by adding an asterisk in front of the name of the workspace in
     * the Gem Browser tree
     */
    public void markWorkspaceDirty() {
        TreeModel treeModel = getBrowserTree().getModel();
        
        if (treeModel instanceof BrowserTreeModel) {
            BrowserTreeModel model = (BrowserTreeModel)treeModel;
            BrowserTreeNode workspaceNode = model.getWorkspaceNode();
            String name = workspaceNode.getDisplayedString();
            
            // find where start of workspace name is
            int workspaceNameLoc = name.indexOf('[') + 1;
            
            // check if already dirty
            if (name.charAt(workspaceNameLoc) != '*') {
                // mane new node name string containing '*'
                String newName = name.substring(0, workspaceNameLoc) + "*" + name.substring(workspaceNameLoc);
                
                // set node name
                workspaceNode.setUserObject(newName);
            }
        }
    }

    /**
     * @return the preferences instance for this class.
     */
    private static Preferences getPreferences() {
        return Preferences.userNodeForPackage(GemBrowser.class);
    }
    
    static TreeViewDisplayMode getPreferredModuleTreeDisplayMode() {
        return TreeViewDisplayMode.fromName(getPreferences().get(BROWSER_MODULE_TREE_DISPLAY_MODE, TreeViewDisplayMode.FLAT_ABBREVIATED.getName()));
    }
    
    static void setPreferredModuleTreeDisplayMode(TreeViewDisplayMode preferredModuleTreeDisplayMode) {
        getPreferences().put(BROWSER_MODULE_TREE_DISPLAY_MODE, preferredModuleTreeDisplayMode.getName());
    }

}
