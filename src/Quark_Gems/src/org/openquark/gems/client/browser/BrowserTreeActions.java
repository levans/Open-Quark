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
 * BrowserTreeActions.java
 * Creation date: Apr 14, 2003.
 * By: Edward Lam
 */
package org.openquark.gems.client.browser;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.tree.TreePath;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.MetaModule;
import org.openquark.gems.client.GemCutter;
import org.openquark.gems.client.ModuleNameDisplayUtilities.TreeViewDisplayMode;
import org.openquark.gems.client.navigator.NavAddress;
import org.openquark.util.ui.UIUtilities;



/**
 * A helper class to provide some default actions for a browser tree.
 * @author Edward Lam
 */    
public class BrowserTreeActions {
    /** The browser tree with which this popup menu is associated. */
    private final BrowserTree browserTree;
    
    /**
     * The action for displaying unimported gems. This action is only 
     * created once since it is used to synchronized state between
     * the menu item and corresponding toggle button.
     */
    private Action displayUnimportedGemsAction = null;
    
    /**
     * The action for displaying type expressions in the browser.
     * Only created once since it is shared between the menu item
     * and corresponding toggle button.
     */
    private Action displayTypeExprAction = null;
    
    /** 
     * The action for displaying only public gems in the browser.
     * Only created once since it is shared between the menu item and 
     * corresponding toggle button.
     *  */
    private Action displayPublicGemsOnlyAction = null;
    
    /** The button to sort the root node alphabetically. */
    private JToggleButton sortAZButton = null;
    
    /** The button to sort the root node by gem type. */
    private JToggleButton sortGemTypeButton = null;
    
    /** The button to categorize the root node by module (flat abbreviated view). */
    private JToggleButton catByModuleFlatAbbreviatedButton = null;
    
    /** The button to categorize the root node by module (flat fully qualified view). */
    private JToggleButton catByModuleFlatFullyQualifiedButton = null;
    
    /** The button to categorize the root node by module (hierarchical view). */
    private JToggleButton catByModuleHierarchicalButton = null;
    
    /** The button to categorize the root node by arity. */
    private JToggleButton catByArityButton = null;
    
    /** The button to categorize the root node by gem type. */
    private JToggleButton catByGemTypeButton = null;
    
    /** The button to categorize the root node by input type. */
    private JToggleButton catByInputTypeButton = null;
    
    /** The button to categorize the root node by output type. */
    private JToggleButton catByOutputTypeButton = null;
    
    /** The button for displaying unimported gems. */
    private JToggleButton displayUnimportedGemsButton = null;
    
    /** The button for displaying type expr. */
    private JToggleButton displayTypeExprButton = null;

    /** The button for displaying public gems only */ 
    private JToggleButton displayPublicGemsButton = null;
    
    /** The button group for the mutually exclusive sort/categorization toggle buttons. */
    private final ButtonGroup buttonGroup = new ButtonGroup();
    
    /** The button panel with the buttons to modify the browser tree. */
    private JPanel buttonPanel = null;
    
    private static final ImageIcon sortAZIcon;
    private static final ImageIcon sortConstructorsFunctionsIcon;
    private static final ImageIcon catByModuleFlatAbbrevIcon;
    private static final ImageIcon catByModuleFlatIcon;
    private static final ImageIcon catByModuleHierarchicalIcon;
    private static final ImageIcon catByArityIcon;
    private static final ImageIcon catByGemTypeIcon;
    private static final ImageIcon catByInputTypeIcon;
    private static final ImageIcon catByOutputTypeIcon;
    private static final ImageIcon showUnimportedGemsIcon;
    private static final ImageIcon showTypeExprIcon;
    private static final ImageIcon showPublicGemsIcon; 
    
    static {
        sortAZIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/sortAZ.gif"));
        sortConstructorsFunctionsIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/sortConstructorsFunctions.gif"));
        catByModuleFlatAbbrevIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/nav_flatAbbrev.gif"));
        catByModuleFlatIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/nav_flat.gif"));
        catByModuleHierarchicalIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/nav_hierarchical.gif"));
        catByArityIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/catByArity.gif"));
        catByGemTypeIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/catByGemType.gif"));
        catByInputTypeIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/catByInputType.gif"));
        catByOutputTypeIcon = new ImageIcon(BrowserTreeActions.class.getResource("/Resources/catByOutputType.gif"));
        showUnimportedGemsIcon = new ImageIcon(GemBrowser.class.getResource("/Resources/showUnimportedGems.gif"));
        showTypeExprIcon = new ImageIcon(GemBrowser.class.getResource("/Resources/showTypeExpressions.gif"));
        showPublicGemsIcon = new ImageIcon(GemBrowser.class.getResource("/Resources/showPublicGemsOnly.gif"));
    
    }

    /**
     * A simple class that enables a single method to be invoked on the browser tree.
     * @author Edward Lam
     */
    private static abstract class ActionHolder {
        /**
         * Perform the action
         */
        abstract void doAction();
    }
    
    /**
     * Constructor for a BrowserTreeActions object
     */
    BrowserTreeActions(BrowserTree browserTree) {
        this.browserTree = browserTree;
    }
    
    /**
     * @return the JPanel with the buttons to modify the browser tree.
     */
    public JPanel getBrowserTreeButtonPanel() {
        
        if (buttonPanel == null) {
            
            buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.setRollover(true);
            
            buttonPanel.add(Box.createHorizontalGlue());
            buttonPanel.add(toolBar);
            buttonPanel.add(Box.createHorizontalGlue());
            
            toolBar.add(getSortAlphabeticallyButton());
            toolBar.add(getSortByFormButton());
            toolBar.add(getCategorizeByModuleFlatAbbreviatedButton());
            toolBar.add(getCategorizeByModuleFlatFullyQualifiedButton());
            toolBar.add(getCategorizeByModuleHierarchicalButton());
            toolBar.add(getCategorizeByArityButton());
            toolBar.add(getCategorizeByGemTypeButton());
            toolBar.add(getCategorizeByInputTypeButton());
            toolBar.add(getCategorizeByOutputTypeButton());
            toolBar.addSeparator();
            toolBar.add(getDisplayTypeExprButton());
            toolBar.add(getDisplayUnimportedGemsButton());
            toolBar.add(getDisplayPublicGemsButton());
        }        
        
        return buttonPanel;
    }

    /**
     * Return a new default popup for the browser tree
     *   The popup will contain applicable actions in this object, appropriately activated.
     * @param selectionPath the selection path on which the popup was invoked.
     * @return JPopupMenu the default popup menu for the browser tree.
     */
    public JPopupMenu getDefaultBrowserTreePopup(TreePath selectionPath) {

        JPopupMenu popupMenu = new JPopupMenu();
        BrowserTreeNode selectedNode = (selectionPath == null) ? null : (BrowserTreeNode)selectionPath.getLastPathComponent();
        addDefaultMenuItems(popupMenu, selectedNode);

        return popupMenu;
    }
    
    /**
     * Add supported built-in popup menu items for actions in this object.
     *   Menu items with icons will have their icons left-justified to the menu border.
     * @param targetMenu the popup menu to which to add the items.
     * @param selectedNode the node for which the items should be added (if any)
     */
    public void addDefaultMenuItems(JPopupMenu targetMenu, BrowserTreeNode selectedNode) {

        // We should not be showing menu items for namespace nodes,
        // which do not really correspond to actual modes
        final boolean shouldAddItems;
        if (selectedNode instanceof GemDrawer) {
            shouldAddItems = !((GemDrawer)selectedNode).isNamespaceNode();
        } else {
            shouldAddItems = true;
        }

        if (shouldAddItems) {
            addSortCategorizeMenuItems(targetMenu, selectedNode);

            targetMenu.addSeparator();
            addDisplayMenuItems(targetMenu);

            // Can only view/edit metadata if a navigator owner is setup.
            if (browserTree.getNavigatorOwner() != null) {
                targetMenu.addSeparator();
                addMetadataEditMenuItems(targetMenu, selectedNode);
            }
        }
    }
    
    /**
     * Add the built-in popup menu items for changing the gem browser display settings.
     * @param targetMenu the popup menu to which to add the items.
     */
    public void addDisplayMenuItems(JPopupMenu targetMenu) {
     
        // Add menu item for displaying unimported gems
        JCheckBoxMenuItem displayGems = new JCheckBoxMenuItem();
        displayGems.setAction(getDisplayUnimportedGemsAction());
        displayGems.setToolTipText(null);
        displayGems.setIcon(null);
        displayGems.setSelected(((BrowserTreeModel) browserTree.getModel()).getShowAllModules());
        targetMenu.add(displayGems);

        // Add a menu item for displaying type expressions
        JCheckBoxMenuItem displayTypeExpr = new JCheckBoxMenuItem();
        displayTypeExpr.setAction(getDisplayTypeExprAction());
        displayTypeExpr.setToolTipText(null);
        displayTypeExpr.setIcon(null);
        displayTypeExpr.setSelected(browserTree.getDisplayTypeExpr());
        targetMenu.add(displayTypeExpr);
        
        // Add a menu item for displaying public gems only
        JCheckBoxMenuItem publicGemsOnly = new JCheckBoxMenuItem();
        publicGemsOnly.setAction(getDisplayPublicGemsAction());
        publicGemsOnly.setToolTipText(null);
        publicGemsOnly.setIcon(null);
        publicGemsOnly.setSelected(((BrowserTreeModel) browserTree.getModel()).getShowPublicGemsOnly());
        targetMenu.add(publicGemsOnly);
    }
    
    /**
     * Add the built-in popup menu items for sorting and categorization.
     * @param targetMenu the popup menu to which to add the items.
     * @param selectedNode the node for which the items should be added (if any)
     */
    public void addSortCategorizeMenuItems(JPopupMenu targetMenu, BrowserTreeNode selectedNode) {
        targetMenu.add(UIUtilities.makeNewMenuItem(getSortAlphabeticallyAction(selectedNode)));
        targetMenu.add(UIUtilities.makeNewMenuItem(getSortByFormAction(selectedNode)));

        targetMenu.addSeparator();

        targetMenu.add(UIUtilities.makeNewMenuItem(getCategorizeByModuleFlatAbbreviatedAction(selectedNode)));
        targetMenu.add(UIUtilities.makeNewMenuItem(getCategorizeByModuleFlatFullyQualifiedAction(selectedNode)));
        targetMenu.add(UIUtilities.makeNewMenuItem(getCategorizeByModuleHierarchicalAction(selectedNode)));
        targetMenu.add(UIUtilities.makeNewMenuItem(getCategorizeByArityAction(selectedNode)));
        targetMenu.add(UIUtilities.makeNewMenuItem(getCategorizeByGemTypeAction(selectedNode)));
        targetMenu.add(UIUtilities.makeNewMenuItem(getCategorizeByInputTypeAction(selectedNode)));        
        targetMenu.add(UIUtilities.makeNewMenuItem(getCategorizeByOutputTypeAction(selectedNode))); 
    }
    
    /**
     * Add menu items for metadata viewing and editing.
     * @param targetMenu the popup menu to which to add the items.
     * @param selectedNode the node for which the items should be added (if any)
     */
    public void addMetadataEditMenuItems(JPopupMenu targetMenu, BrowserTreeNode selectedNode) {
        targetMenu.add(UIUtilities.makeNewMenuItem(getEditMetadataAction(selectedNode)));
        targetMenu.add(UIUtilities.makeNewMenuItem(getViewMetadataAction(selectedNode)));
    }

    /**
     * @return the button to sort the root node alphabetically
     */
    JToggleButton getSortAlphabeticallyButton() {

        if (sortAZButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            sortAZButton = makeNewButton(getSortAlphabeticallyAction(rootNode), buttonGroup);
        }
        
        return sortAZButton;
    }
 
    /**
     * @return the button to sort the root node by gem type
     */
    JToggleButton getSortByFormButton() {

        if (sortGemTypeButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            sortGemTypeButton = makeNewButton(getSortByFormAction(rootNode), buttonGroup);
        }
        
        return sortGemTypeButton;
    } 

    /**
     * @return the button to categorize the root node by module (flat abbreviated view)
     */        
    JToggleButton getCategorizeByModuleFlatAbbreviatedButton() {

        if (catByModuleFlatAbbreviatedButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            catByModuleFlatAbbreviatedButton = makeNewButton(getCategorizeByModuleFlatAbbreviatedAction(rootNode), buttonGroup);
        }
        
        return catByModuleFlatAbbreviatedButton;
    }

    /**
     * @return the button to categorize the root node by module (flat fully qualified view)
     */        
    JToggleButton getCategorizeByModuleFlatFullyQualifiedButton() {

        if (catByModuleFlatFullyQualifiedButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            catByModuleFlatFullyQualifiedButton = makeNewButton(getCategorizeByModuleFlatFullyQualifiedAction(rootNode), buttonGroup);
        }
        
        return catByModuleFlatFullyQualifiedButton;
    }

    /**
     * @return the button to categorize the root node by module (flat hierarchical view)
     */        
    JToggleButton getCategorizeByModuleHierarchicalButton() {

        if (catByModuleHierarchicalButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            catByModuleHierarchicalButton = makeNewButton(getCategorizeByModuleHierarchicalAction(rootNode), buttonGroup);
        }
        
        return catByModuleHierarchicalButton;
    }

    /**
     * @return the button to categorize the root node by arity
     */
    JToggleButton getCategorizeByArityButton() {

        if (catByArityButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            catByArityButton = makeNewButton(getCategorizeByArityAction(rootNode), buttonGroup);
        }
        
        return catByArityButton;
    }
    
    /**
     * @return the button to categorize the root node by gem type
     */
    JToggleButton getCategorizeByGemTypeButton() {

        if (catByGemTypeButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            catByGemTypeButton = makeNewButton(getCategorizeByGemTypeAction(rootNode), buttonGroup);
        }
        
        return catByGemTypeButton;
    }

    /**
     * @return the button to categorize the root node by input type
     */    
    JToggleButton getCategorizeByInputTypeButton() {

        if (catByInputTypeButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            catByInputTypeButton = makeNewButton(getCategorizeByInputTypeAction(rootNode), buttonGroup);
        }
        
        return catByInputTypeButton;
    }

    /**
     * @return the button to categorize the root node by output type
     */
    JToggleButton getCategorizeByOutputTypeButton() {

        if (catByOutputTypeButton == null) {
            BrowserTreeNode rootNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();            
            catByOutputTypeButton = makeNewButton(getCategorizeByOutputTypeAction(rootNode), buttonGroup);
        }
        
        return catByOutputTypeButton;
    }
    
    /**
     * Returns the toggle button for displaying unimported gems in the gem browser.
     * @return JToggleButton
     */
    JToggleButton getDisplayUnimportedGemsButton () {

        if (displayUnimportedGemsButton == null) {

            displayUnimportedGemsButton = makeNewButton(getDisplayUnimportedGemsAction(), null);
            
            // Add a property change listener to this action so we can change the state of
            // the button if the corresponding menu item invokes this action.
            getDisplayUnimportedGemsAction().addPropertyChangeListener(new PropertyChangeListener () {

                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(GemBrowserActionKeys.ACTION_SELECTED_KEY)) {
                        boolean showingGems = ((Boolean) e.getNewValue()).booleanValue();
                        String toolTipId = showingGems ? "GB_DisplayingUnimportedGemsToolTip" : "GB_HidingUnimportedGemsToolTip";
                        displayUnimportedGemsButton.getModel().setSelected(showingGems);
                        displayUnimportedGemsButton.setToolTipText(BrowserMessages.getString(toolTipId));
                    }
                }
            });
        }        

        return displayUnimportedGemsButton; 
    }      

    /**
     * Returns the toggle button for displaying unimported gems in the gem browser.
     * @return JToggleButton
     */
    JToggleButton getDisplayTypeExprButton () {

        if (displayTypeExprButton == null) {
            
            displayTypeExprButton = makeNewButton(getDisplayTypeExprAction(), null);
            
            // Add a property change listener to this action so we can change the state of
            // the button if the corresponding menu item invokes this action.
            getDisplayTypeExprAction().addPropertyChangeListener(new PropertyChangeListener () {
                       
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(GemBrowserActionKeys.ACTION_SELECTED_KEY)) {
                        boolean showingTypeExpr = ((Boolean) e.getNewValue()).booleanValue();
                        String toolTipId = showingTypeExpr ? "GB_DisplayingTypeExprToolTip" : "GB_HidingTypeExprToolTip";
                        displayTypeExprButton.getModel().setSelected(showingTypeExpr);
                        displayTypeExprButton.setToolTipText(BrowserMessages.getString(toolTipId));
                    }
                }
            });
        }                

        return displayTypeExprButton; 
    }
    
    /**
     * Returns the toggle button for displaying only the public gems in the gem browser.
     * @return JToggleButton
     */
    JToggleButton getDisplayPublicGemsButton() {

        if (displayPublicGemsButton == null){

            displayPublicGemsButton = makeNewButton(getDisplayPublicGemsAction(),null);

            //Add a property change listener
            getDisplayPublicGemsAction().addPropertyChangeListener ( new PropertyChangeListener () {

                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(GemBrowserActionKeys.ACTION_SELECTED_KEY)) {
                        boolean showingPublicGems = ((Boolean) e.getNewValue()).booleanValue();
                        String toolTipId = showingPublicGems ? "GB_DisplayingPublicGemsOnlyToolTip" : "GB_DisplayingAllGemsToolTip";
                        displayPublicGemsButton.getModel().setSelected(showingPublicGems);
                        displayPublicGemsButton.setToolTipText(BrowserMessages.getString(toolTipId));
                    }
                }
            });
        }

        return displayPublicGemsButton;
    }
    
    /**
     * Return a new action that handles sorting alphabetically
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getSortAlphabeticallyAction(final BrowserTreeNode selectedNode) {
    
        Action sortAlphabeticallyAction = new AbstractAction (BrowserMessages.getString("GB_SortAlpha"), sortAZIcon) {
                                                    
            private static final long serialVersionUID = -4510477496332052077L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction(){
                        BrowserTreeModel browserTreeModel = (BrowserTreeModel)browserTree.getModel();
                        browserTreeModel.sortByUnqualifiedName(selectedNode);
                        
                        if (selectedNode == browserTreeModel.getWorkspaceNode()) {
                            updateButtonGroup(sortAZButton);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        sortAlphabeticallyAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_SORT_ALPHABETICALLY));
        sortAlphabeticallyAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_SortAlphaToolTip"));

        enableSortCategorizeAction(selectedNode, sortAlphabeticallyAction);

        return sortAlphabeticallyAction;
    }
    
    /**
     * Return a new action that handles sorting by form (gem "type"..).
     *   What is meant by 'Sort by Gem Type' is that the Gems will be sorted
     *   into two sets, the Data Constructor set, and the Supercombinator set.
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getSortByFormAction(final BrowserTreeNode selectedNode) {
    
        Action sortByFormAction = new AbstractAction (BrowserMessages.getString("GB_SortConstructorsThenFunctions"), sortConstructorsFunctionsIcon) {
                                                    
            private static final long serialVersionUID = 7730729244475313080L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        BrowserTreeModel browserModel = (BrowserTreeModel)browserTree.getModel();
                        browserModel.sortByForm(selectedNode);        

                        if (selectedNode == browserModel.getWorkspaceNode()) {
                            updateButtonGroup(sortGemTypeButton);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        sortByFormAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_SORT_BY_FORM));
        sortByFormAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_SortConstructorsThenFunctionsToolTip"));

        enableSortCategorizeAction(selectedNode, sortByFormAction);

        return sortByFormAction;
    }

    /**
     * Return a new action that handles categorization by modules (flat abbreviated view).
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getCategorizeByModuleFlatAbbreviatedAction(final BrowserTreeNode selectedNode) {
    
        Action categorizeByModuleAction = new AbstractAction (BrowserMessages.getString("GB_CatByModuleFlatAbbrev"), catByModuleFlatAbbrevIcon) {
                                                
            private static final long serialVersionUID = -4160939901530872712L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        BrowserTreeModel browserTreeModel = (BrowserTreeModel)browserTree.getModel();
                        browserTreeModel.categorizeByModule(selectedNode, TreeViewDisplayMode.FLAT_ABBREVIATED);
                        
                        if (selectedNode == browserTreeModel.getWorkspaceNode()) {
                            updateButtonGroup(catByModuleFlatAbbreviatedButton);
                            GemBrowser.setPreferredModuleTreeDisplayMode(TreeViewDisplayMode.FLAT_ABBREVIATED);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        categorizeByModuleAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_CATEGORIZE_BY_MODULE_FLAT_ABBREV));
        categorizeByModuleAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_CatByModuleFlatAbbrevToolTip"));

        enableSortCategorizeAction(selectedNode, categorizeByModuleAction);

        return categorizeByModuleAction;
    }
    
    /**
     * Return a new action that handles categorization by modules (flat fully qualified view).
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getCategorizeByModuleFlatFullyQualifiedAction(final BrowserTreeNode selectedNode) {
    
        Action categorizeByModuleAction = new AbstractAction (BrowserMessages.getString("GB_CatByModuleFlat"), catByModuleFlatIcon) {
                                                
            private static final long serialVersionUID = 2855158975387678224L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        BrowserTreeModel browserTreeModel = (BrowserTreeModel)browserTree.getModel();
                        browserTreeModel.categorizeByModule(selectedNode, TreeViewDisplayMode.FLAT_FULLY_QUALIFIED);
                        
                        if (selectedNode == browserTreeModel.getWorkspaceNode()) {
                            updateButtonGroup(catByModuleFlatFullyQualifiedButton);
                            GemBrowser.setPreferredModuleTreeDisplayMode(TreeViewDisplayMode.FLAT_FULLY_QUALIFIED);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        categorizeByModuleAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_CATEGORIZE_BY_MODULE_FLAT));
        categorizeByModuleAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_CatByModuleFlatToolTip"));

        enableSortCategorizeAction(selectedNode, categorizeByModuleAction);

        return categorizeByModuleAction;
    }
    
    /**
     * Return a new action that handles categorization by modules (hierarchical view).
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getCategorizeByModuleHierarchicalAction(final BrowserTreeNode selectedNode) {
    
        Action categorizeByModuleAction = new AbstractAction (BrowserMessages.getString("GB_CatByModuleHierarchical"), catByModuleHierarchicalIcon) {
                                                
            private static final long serialVersionUID = 3999294713899147893L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        BrowserTreeModel browserTreeModel = (BrowserTreeModel)browserTree.getModel();
                        browserTreeModel.categorizeByModule(selectedNode, TreeViewDisplayMode.HIERARCHICAL);
                        
                        if (selectedNode == browserTreeModel.getWorkspaceNode()) {
                            updateButtonGroup(catByModuleHierarchicalButton);
                            GemBrowser.setPreferredModuleTreeDisplayMode(TreeViewDisplayMode.HIERARCHICAL);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        categorizeByModuleAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_CATEGORIZE_BY_MODULE_HIERARCHCICAL));
        categorizeByModuleAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_CatByModuleHierarchicalToolTip"));

        enableSortCategorizeAction(selectedNode, categorizeByModuleAction);

        return categorizeByModuleAction;
    }
    
    /**
     * Return a new action that handles categorizing by gem type.
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getCategorizeByGemTypeAction(final BrowserTreeNode selectedNode) {
    
        Action categorizeByTypeAction = new AbstractAction (BrowserMessages.getString("GB_CatByGemType"), catByGemTypeIcon) {
                                                
            private static final long serialVersionUID = 4989845787502335699L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        BrowserTreeModel browserTreeModel = (BrowserTreeModel)browserTree.getModel();
                        browserTreeModel.categorizeByGemType(selectedNode);

                        if (selectedNode == browserTreeModel.getWorkspaceNode()) {
                            updateButtonGroup(catByGemTypeButton);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        categorizeByTypeAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_CATEGORIZE_BY_TYPE));
        categorizeByTypeAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_CatByGemTypeToolTip"));

        enableSortCategorizeAction(selectedNode, categorizeByTypeAction);

        return categorizeByTypeAction;
    }
    
    /**
     * Return a new action that handles categorizing by arity
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getCategorizeByArityAction(final BrowserTreeNode selectedNode) {
    
        Action categorizeByArityAction = new AbstractAction (BrowserMessages.getString("GB_CatByArity"), catByArityIcon) {
                                                
            private static final long serialVersionUID = 933194855360216607L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        BrowserTreeModel browserTreeModel = (BrowserTreeModel)browserTree.getModel();
                        browserTreeModel.categorizeByArity(selectedNode);

                        if (selectedNode == browserTreeModel.getWorkspaceNode()) {
                            updateButtonGroup(catByArityButton);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        categorizeByArityAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_CATEGORIZE_BY_ARITY));
        categorizeByArityAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_CatByArityToolTip"));

        enableSortCategorizeAction(selectedNode, categorizeByArityAction);

        return categorizeByArityAction;
    }
    
    /**
     * Return a new action that handles categorizing by output type
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getCategorizeByOutputTypeAction(final BrowserTreeNode selectedNode) {
    
        Action categorizeByOutputAction = new AbstractAction (BrowserMessages.getString("GB_CatByOutputType"), catByOutputTypeIcon) {
                                                
            private static final long serialVersionUID = 148408557706639845L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        BrowserTreeModel browserTreeModel = (BrowserTreeModel)browserTree.getModel();
                        browserTreeModel.categorizeByOutput(selectedNode);

                        if (selectedNode == browserTreeModel.getWorkspaceNode()) {
                            updateButtonGroup(catByOutputTypeButton);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        categorizeByOutputAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_CATEGORIZE_BY_OUTPUT));
        categorizeByOutputAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_CatByOutputTypeToolTip"));

        enableSortCategorizeAction(selectedNode, categorizeByOutputAction);

        return categorizeByOutputAction;
    }

    /**
     * Return a new action that handles categorizing by output type
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getCategorizeByInputTypeAction(final BrowserTreeNode selectedNode) {
    
        Action categorizeByInputAction = new AbstractAction (BrowserMessages.getString("GB_CatByInputType"), catByInputTypeIcon) {
                                                
            private static final long serialVersionUID = 6989782310657432696L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        BrowserTreeModel browserTreeModel = (BrowserTreeModel)browserTree.getModel();
                        browserTreeModel.categorizeByInput(selectedNode);

                        if (selectedNode == browserTreeModel.getWorkspaceNode()) {
                            updateButtonGroup(catByInputTypeButton);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        categorizeByInputAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_CATEGORIZE_BY_INPUT));
        categorizeByInputAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_CatByInputTypeToolTip"));

        enableSortCategorizeAction(selectedNode, categorizeByInputAction);

        return categorizeByInputAction;
    }
    
    /**
     * Return a new action that handles editing metadata for a gem
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getEditMetadataAction(final BrowserTreeNode selectedNode) {
    
        Action editMetadataAction = new AbstractAction (BrowserMessages.getString("GB_EditGemProperties"),
                                                        new ImageIcon(GemCutter.class.getResource("/Resources/nav_edit.gif"))) {

            private static final long serialVersionUID = -7408780266351267761L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {
                        
                        final NavAddress address;
                        if (selectedNode instanceof GemTreeNode) {
                            GemEntity gemEntity = (GemEntity) selectedNode.getUserObject();
                            address = NavAddress.getAddress(gemEntity);
                            browserTree.getNavigatorOwner().editMetadata(address);
                        
                        } else if (selectedNode instanceof GemDrawer) {
                            final GemDrawer gemDrawer = (GemDrawer) selectedNode;
                            final ModuleName moduleName = gemDrawer.getModuleName();
                            if (gemDrawer.isNamespaceNode()) {
                                address = NavAddress.getModuleNamespaceAddress(moduleName);
                            } else {
                                MetaModule metaModule = browserTree.getNavigatorOwner().getPerspective().getMetaModule(moduleName);
                                address = NavAddress.getAddress(metaModule);
                            }
                            browserTree.getNavigatorOwner().editMetadata(address);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        editMetadataAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_EDIT_METADATA));
        editMetadataAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_EditGemPropertiesToolTip"));

        enableMetadataAction(selectedNode, editMetadataAction);

        return editMetadataAction;
    }
    
    /**
     * Return a new action that handles viewing metadata for a gem
     * @param selectedNode the node on which to invoke the action.
     * @return Action
     */
    Action getViewMetadataAction(final BrowserTreeNode selectedNode) {
    
        Action viewMetadataAction = new AbstractAction (BrowserMessages.getString("GB_ViewGemProperties")) {
                                                
            private static final long serialVersionUID = 3203818148537546800L;

            public void actionPerformed(ActionEvent evt) {
                doBrowserAction(new ActionHolder() {
                    @Override
                    void doAction() {

                        final NavAddress address;
                        if (selectedNode instanceof GemTreeNode) {
                            GemEntity gemEntity = (GemEntity) selectedNode.getUserObject();
                            address = NavAddress.getAddress(gemEntity);
                            browserTree.getNavigatorOwner().displayMetadata(address, true);
                        
                        } else if (selectedNode instanceof GemDrawer) {
                            final GemDrawer gemDrawer = (GemDrawer) selectedNode;
                            final ModuleName moduleName = gemDrawer.getModuleName();
                            if (gemDrawer.isNamespaceNode()) {
                                address = NavAddress.getModuleNamespaceAddress(moduleName);
                            } else {
                                MetaModule metaModule = browserTree.getNavigatorOwner().getPerspective().getMetaModule(moduleName);
                                address = NavAddress.getAddress(metaModule);
                            }
                            browserTree.getNavigatorOwner().displayMetadata(address, true);
                        }
                    }
                }, evt.getSource());
            }
        };
    
        viewMetadataAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemBrowserActionKeys.MNEMONIC_VIEW_METADATA));
        viewMetadataAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString("GB_ViewGemPropertiesToolTip"));

        enableMetadataAction(selectedNode, viewMetadataAction);

        return viewMetadataAction;
    }

    /**
     * Return a new action to toggle if hidden modules should be shown in the gem browser.
     * 
     * @return Action the new displayUnimportedGemsAction.
     */
    Action getDisplayUnimportedGemsAction() {
        
        if (displayUnimportedGemsAction == null) {
        
            final BrowserTreeModel browserTreeModel = (BrowserTreeModel) browserTree.getModel();

            boolean showingGems = ((BrowserTreeModel) browserTree.getModel()).getShowAllModules();
            String toolTipId = showingGems ? "GB_DisplayingUnimportedGemsToolTip" : "GB_HidingUnimportedGemsToolTip";
            
            displayUnimportedGemsAction = new AbstractAction (BrowserMessages.getString("GB_DisplayUnimportedGems"), showUnimportedGemsIcon) {
                private static final long serialVersionUID = 3138132771127291139L;

                public void actionPerformed(ActionEvent evt) {

                    BrowserTreeModel.invokeOnEventDispatchThread(new Runnable (){

                        public void run(){
                            boolean hiddenModulesAreShowing = browserTreeModel.getShowAllModules();
                            browserTree.saveState();
                            browserTreeModel.setShowAllModules(!hiddenModulesAreShowing);
                            browserTreeModel.reload();
                            browserTree.restoreSavedState();
                            putValue(GemBrowserActionKeys.ACTION_SELECTED_KEY, Boolean.valueOf(!hiddenModulesAreShowing));
                        }
                    });

                }
            };
    
            displayUnimportedGemsAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString(toolTipId));
            displayUnimportedGemsAction.setEnabled(true);
        }
        
        return displayUnimportedGemsAction;
    }

    /**
     * Returns a new action to toggle if type expressions are shown in the gem browser.
     *
     * @return Action the new displayTypeExprAction
     */
    
    Action getDisplayTypeExprAction() {
        
        if (displayTypeExprAction == null) {

            boolean showingTypeExpr = browserTree.getDisplayTypeExpr();
            String toolTipId = showingTypeExpr ? "GB_DisplayingTypeExprToolTip" : "GB_HidingTypeExprToolTip";
        
            displayTypeExprAction = new AbstractAction (BrowserMessages.getString("GB_DisplayTypeExpr"), showTypeExprIcon) {

                private static final long serialVersionUID = 806950495350707496L;

                public void actionPerformed(ActionEvent evt) {
                    browserTree.setDisplayTypeExpr(!browserTree.getDisplayTypeExpr());
                    putValue(GemBrowserActionKeys.ACTION_SELECTED_KEY, Boolean.valueOf(browserTree.getDisplayTypeExpr()));
                }
            };
    
            displayTypeExprAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString(toolTipId));
            displayTypeExprAction.setEnabled(true);
        }
        
        return displayTypeExprAction;
    }


    /**
     * Return a new action to toggle if only public gems should be shown in the gem browser
     * 
     * @return Action the new displayPublicGemsAction.
     */
    Action getDisplayPublicGemsAction(){

        if (displayPublicGemsOnlyAction ==null) {

            final BrowserTreeModel browserTreeModel = (BrowserTreeModel) browserTree.getModel();

            boolean showingPublicGems = browserTreeModel.getShowPublicGemsOnly();
            String toolTipId = showingPublicGems ? "GB_DisplayingPublicGemsOnlyToolTip" : "GB_DisplayingAllGemsToolTip";

            displayPublicGemsOnlyAction = new AbstractAction (BrowserMessages.getString("GB_DisplayPublicGemsOnly"), showPublicGemsIcon) {
                private static final long serialVersionUID = 8426923208607274866L;

                public void actionPerformed(ActionEvent evt) {

                    BrowserTreeModel.invokeOnEventDispatchThread(new Runnable (){

                        public void run(){
                            boolean onlyPublicGemsAreShowing = browserTreeModel.getShowPublicGemsOnly();
                            browserTree.saveState();
                            browserTreeModel.setShowPublicGemsOnly(!onlyPublicGemsAreShowing);
                            browserTreeModel.reload();
                            browserTree.restoreSavedState();

                            putValue(GemBrowserActionKeys.ACTION_SELECTED_KEY, Boolean.valueOf(browserTreeModel.getShowPublicGemsOnly()));
                        }
                    });
                }
            };

            displayPublicGemsOnlyAction.putValue(Action.SHORT_DESCRIPTION, BrowserMessages.getString(toolTipId));
            displayPublicGemsOnlyAction.setEnabled(true);
        }

        return displayPublicGemsOnlyAction;

    }


    /**
     * Perform an action invoked from the popup menu.
     *   This method exists to factor out some code used by all the actions.
     * @param browserActionHolder
     * @param source the object that originated the event.
     */
    private void doBrowserAction(ActionHolder browserActionHolder, Object source) {

        // The re-organizing may take awhile, so change cursor to wait.
        Container parentContainer = browserTree.getTopLevelAncestor();
        parentContainer.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {

            browserActionHolder.doAction();

            // Determine whether the action was activated by a popup on the browser tree.
            boolean invokedFromBrowserTreePopup = (source instanceof JMenuItem) && 
                                                (((JMenuItem)source).getParent() instanceof JPopupMenu) && 
                                                (((JPopupMenu)((JMenuItem)source).getParent()).getInvoker() == browserTree);

            BrowserTreeNode selectedNode;
            if (!invokedFromBrowserTreePopup) {
                // If the action was not activated from the popup then scroll the workspace node into view.
                selectedNode = ((BrowserTreeModel) browserTree.getModel()).getWorkspaceNode();
                browserTree.scrollPathToTop(new TreePath(selectedNode.getPath()));
            } else {
                selectedNode = (BrowserTreeNode) browserTree.getSelectionPath().getLastPathComponent();
            }
            
            // Expand the node the action was performed on.
            browserTree.expandPath(new TreePath(selectedNode.getPath()));
            
        } finally {
            // Re-organization done, set cursor back to norm.
            parentContainer.setCursor(null);
        }
    }

    /**
     * Creates a new JButton and configures it for use in the button panel.
     * @param action the action used to configure the JButton
     * @param buttonGroup the button group the button belongs in (can be null)
     * @return JButton
     */
    private JToggleButton makeNewButton(Action action, ButtonGroup buttonGroup) {
        
        JToggleButton newButton = new JToggleButton();
        
        // Specify some of the customized characteristics here.
        newButton.setAction(action);
        newButton.setText(null);
        newButton.setMargin(new Insets(0, 0, 0, 0));
        
        // Clear the mnemonic so that the buttons are not activated by them.
        newButton.setMnemonic(KeyEvent.KEY_LOCATION_UNKNOWN);
        
        newButton.setBackground(new Color(0,0,0,0));
        newButton.setOpaque(false);        
        
        // Add it to the button group
        if (buttonGroup != null) {
            buttonGroup.add(newButton);
        }
        
        return newButton;
    }

    /**
     * Enable or disable a sort/categorize action according to whether it applies to a given node.
     * @param selectedNode the selected node.
     * @param targetAction the action to enable / disable.
     */
    private void enableSortCategorizeAction(BrowserTreeNode selectedNode, Action targetAction) {

        // Enable on category folders.
        //   Assumption is that it will be a category folder if it's a node which isn't a GemTreeNode.
        boolean enableSortCategorizeAction = (selectedNode != null && 
                                            !(selectedNode instanceof GemTreeNode));

        targetAction.setEnabled(enableSortCategorizeAction);
    }

    /**
     * Enable or disable a metadata action according to whether it applies to a given node.
     * @param selectedNode the selected node.
     * @param targetAction the action to enable / disable.
     */
    private void enableMetadataAction(BrowserTreeNode selectedNode, Action targetAction) {

        // Enable on modules and gems.
        boolean enableMetadataAction = selectedNode instanceof GemTreeNode ||
                                       selectedNode instanceof GemDrawer;

        targetAction.setEnabled(enableMetadataAction);
    }
    
    /**
     * Updates the browser tree button group to select the given button.
     * @param selectedButton the button to select
     */
    private void updateButtonGroup(JToggleButton selectedButton) {
        buttonGroup.setSelected(selectedButton.getModel(), true);
    }
}