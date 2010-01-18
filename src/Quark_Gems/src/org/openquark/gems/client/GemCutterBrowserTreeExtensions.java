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
 * GemCutterBrowserTreeExtensions.java
 * Creation date: Oct 23, 2002.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.ModuleRevision;
import org.openquark.cal.services.Vault;
import org.openquark.cal.services.VaultElementInfo;
import org.openquark.gems.client.AutoburnLogic.AutoburnUnifyStatus;
import org.openquark.gems.client.IntellicutManager.IntellicutInfo;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;
import org.openquark.gems.client.browser.BrowserTree;
import org.openquark.gems.client.browser.BrowserTreeCellRenderer;
import org.openquark.gems.client.browser.BrowserTreeModel;
import org.openquark.gems.client.browser.BrowserTreeNode;
import org.openquark.gems.client.browser.GemDrawer;
import org.openquark.gems.client.browser.GemTreeNode;
import org.openquark.gems.client.browser.LeafNodeTriggeredEvent;
import org.openquark.gems.client.browser.LeafNodeTriggeredListener;
import org.openquark.util.UnsafeCast;
import org.openquark.util.ui.UIUtilities;


/**
 * Extensions are declared as outer-inner classes in this file.
 * @author Frank Worsley
 */
class GemCutterBrowserTreeExtensions {

    /**
     * The GemCutterVaultTreeCellRenderer is an extension to the BrowserTreeCellRenderer 
     * that displays additional visual cues for intellicut.
     * @author Edward Lam
     */
    static class CellRenderer extends BrowserTreeCellRenderer {

        private static final long serialVersionUID = -7121628652443578657L;
        /** The icons representing the scope of gem tree nodes, to be added to gem icons */
        private static final ImageIcon SCOPE_PUBLIC_ICON = new ImageIcon(Object.class.getResource("/Resources/scope_public.gif"));
        private static final ImageIcon SCOPE_PROTECTED_ICON = new ImageIcon(Object.class.getResource("/Resources/scope_protected.gif"));
        private static final ImageIcon SCOPE_PRIVATE_ICON = new ImageIcon(Object.class.getResource("/Resources/scope_private.gif"));
        
        /** The little decal we add to the gem icon, if the gem has a design available. */
        private static final ImageIcon GEM_DESIGN_DECAL = new ImageIcon(CellRenderer.class.getResource("/Resources/gemDesignDecal.gif"));
    
        /** The icon to use for gems that can't be connected at all. */
        private static final ImageIcon CANNOT_CONNECT_ICON = new ImageIcon(CellRenderer.class.getResource("/Resources/noParking.gif"));
    
        /** The icon to use for gems that can only be connected via burning. */
        private static final ImageIcon BURN_ICON = TableTopPanel.burnImageIconSmall;
    
        /** The icon to use for gems that can be connected directly and via burning. */
        private static final ImageIcon AMBIGUOUS_ICON = TableTopPanel.burnQuestionImageIconSmall;
    
        /** The text color for gems that can not be connected at all. */
        private static final Color CANNOT_CONNECT_COLOR = UIManager.getColor("TextField.inactiveForeground");
    
        /** The text color to use for gems that can only be connected by burning. */
        private static final Color BURN_COLOR = Color.red;
    
        /** The text color to use for gems that can be connected directly or via burning. */
        private static final Color AMBIGUOUS_COLOR = Color.red;

        /** The GemCutter this renderer is for. */
        private final GemCutter gemCutter;

        // Cache for the scope decorated icons 
        private final Map<Icon, Icon> imageCache_public = new HashMap<Icon, Icon>();
        private final Map<Icon, Icon> imageCache_protected = new HashMap<Icon, Icon>();
        private final Map<Icon, Icon> imageCache_private = new HashMap<Icon, Icon>();
        
        
        /**
         * Default GemCutterVaultTreeCellRenderer constructor.
         * @param gemCutter the GemCutter this renderer is for
         */
        public CellRenderer(GemCutter gemCutter) {
        
            if (gemCutter == null) {
                throw new NullPointerException();
            }
        
            this.gemCutter = gemCutter;
        }

        /**
         * {@inheritDoc}
         * We override this to draw items differently if Intellicut is active.
         */
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            BrowserTreeModel browserTreeModel = (BrowserTreeModel) tree.getModel();
            IntellicutManager intellicutManager = gemCutter.getIntellicutManager();
        
            if (intellicutManager.getIntellicutMode() == IntellicutMode.NOTHING) {
                // If Intellicut is not active

                if (value instanceof GemTreeNode) {
                   
                    // Add scope icon (public, protected, private) to the gem icon on the bottom right corner
                    // Uses a cache to store icons already combined
                    GemEntity entity = (GemEntity) ((GemTreeNode) value).getUserObject();
                    Icon baseGemIcon = getIcon();
                    Icon overlayIcon = null;
                    Map<Icon, Icon> cache = null;
                    
                    if (entity.getScope().isPublic()) {
                        cache = imageCache_public;
                        overlayIcon = SCOPE_PUBLIC_ICON;
                   
                    } else if (entity.getScope().isProtected()) {
                        cache = imageCache_protected;
                        overlayIcon = SCOPE_PROTECTED_ICON;

                    } else { //entity.getScope().isPrivate()
                        cache = imageCache_private;
                        overlayIcon = SCOPE_PRIVATE_ICON;
                    }

                    Icon cachedImage = cache.get(baseGemIcon);
                    if (cachedImage != null) {
                        setIcon(cachedImage);

                    } else {
                        Icon newIcon = UIUtilities.combineIcons(baseGemIcon, overlayIcon);
                        setIcon(newIcon);
                        cache.put(baseGemIcon, newIcon);
                    }

                    if (((GemTreeNode) value).hasDesign()) {
                        // Add a design decal to the gem icon
                        baseGemIcon = getIcon();
                        Icon newIcon = UIUtilities.combineIcons(baseGemIcon, GEM_DESIGN_DECAL);
                        setIcon(newIcon);
                    }
                }
            
                return this;
            }

            // If Intellicut is active, use special Intellicut icons to indicate Intellicut status.
        
            if (leaf) {
            
                // This is a gem. Give it an icon and text color that indicates how intellicut can connect it.
            
                GemEntity gemEntity = (GemEntity) ((GemTreeNode) value).getUserObject();
            
                if (browserTreeModel.isVisibleGem(gemEntity)) {
                
                    IntellicutInfo intellicutInfo = intellicutManager.getIntellicutInfo(gemEntity);
                    AutoburnUnifyStatus autoburnStatus = intellicutInfo.getAutoburnUnifyStatus();

                    if (autoburnStatus == AutoburnUnifyStatus.UNAMBIGUOUS) {
                        setIcon(BURN_ICON);
                        setForeground(BURN_COLOR);
                    
                    } else if (autoburnStatus == AutoburnUnifyStatus.AMBIGUOUS) {
                        setIcon(AMBIGUOUS_ICON);
                        setForeground(AMBIGUOUS_COLOR);
                    
                    } else if (autoburnStatus == AutoburnUnifyStatus.NOT_POSSIBLE) {
                        setIcon(CANNOT_CONNECT_ICON);
                        setForeground(CANNOT_CONNECT_COLOR);

                    } else if (autoburnStatus == AutoburnUnifyStatus.UNAMBIGUOUS_NOT_NECESSARY &&                    
                               intellicutInfo.getBurnTypeCloseness() > intellicutInfo.getNoBurnTypeCloseness()) {
                               
                        setIcon(BURN_ICON);
                        setForeground(BURN_COLOR);
                
                    } else if (autoburnStatus == AutoburnUnifyStatus.AMBIGUOUS_NOT_NECESSARY && 
                               intellicutInfo.getBurnTypeCloseness() > intellicutInfo.getNoBurnTypeCloseness()) {

                        setIcon(AMBIGUOUS_ICON);
                        setForeground(AMBIGUOUS_COLOR);
                    }
                
                } else {
                    setIcon(CANNOT_CONNECT_ICON);
                    setForeground(CANNOT_CONNECT_COLOR);                
                }
    
            } else {

                // This is a folder. Determine if and how it should pulse.

                // Assume we can't connect at all to start with.
                Color folderColor = CANNOT_CONNECT_COLOR;

                Enumeration<TreeNode> subTreeEnum = UnsafeCast.<Enumeration<TreeNode>>unsafeCast(((DefaultMutableTreeNode)value).breadthFirstEnumeration());
            
                while (subTreeEnum.hasMoreElements()) {

                    DefaultMutableTreeNode childTreeNode = (DefaultMutableTreeNode) subTreeEnum.nextElement();
                
                    if (childTreeNode instanceof GemTreeNode) {

                        GemEntity gemEntity = (GemEntity) childTreeNode.getUserObject();

                        if (browserTreeModel.isVisibleGem(gemEntity)) {

                            IntellicutInfo intellicutInfo = intellicutManager.getIntellicutInfo(gemEntity);
                            AutoburnUnifyStatus autoburnStatus = intellicutInfo.getAutoburnUnifyStatus();

                            if (autoburnStatus == AutoburnUnifyStatus.NOT_NECESSARY) {
                                // Connections take precedence. If we find a connection then stop.
                                folderColor = null;
                                break;
                            
                            } else if (autoburnStatus == AutoburnUnifyStatus.UNAMBIGUOUS ||
                                       autoburnStatus == AutoburnUnifyStatus.AMBIGUOUS) {

                                folderColor = BURN_COLOR;

                             } else if ((autoburnStatus == AutoburnUnifyStatus.UNAMBIGUOUS_NOT_NECESSARY ||
                                        autoburnStatus == AutoburnUnifyStatus.AMBIGUOUS_NOT_NECESSARY)) {
                                        
                                if (intellicutInfo.getBurnTypeCloseness() > intellicutInfo.getNoBurnTypeCloseness()) {
                                    folderColor = BURN_COLOR;
                                } else {
                                    folderColor = null;
                                    break;
                                }
                             }
                        }
                    }
                }
            
                if (folderColor != null) {
                    setForeground(folderColor);
                }
            }

            return this;
        }
    }

    /**
     * A customized popup menu provider for the BrowserTree. It adds GemCutter specific menu items.
     * @author Frank Worsley
     */
    static class PopupMenuProvider implements BrowserTree.PopupMenuProvider {
    
        /** The GemCutter this provider is for. */
        private final GemCutter gemCutter;
    
        /**
         * Constructor for a new GemCutterVaultTreePopupMenuProvider.
         * @param gemCutter the GemCutter the provider is for
         */
        public PopupMenuProvider(GemCutter gemCutter) {
        
            if (gemCutter == null) {
                throw new NullPointerException();
            }
        
            this.gemCutter = gemCutter;
        }
    
        /**
         * @see org.openquark.gems.client.browser.BrowserTree.PopupMenuProvider#getPopupMenu(javax.swing.tree.TreePath)
         */
        public JPopupMenu getPopupMenu(TreePath selectionPath) {

            // Create the popup menu so we can add our custom items.
            JPopupMenu popupMenu = new JPopupMenu();

            BrowserTreeNode selectedNode = (selectionPath == null) ? null : (BrowserTreeNode) selectionPath.getLastPathComponent();

            if (selectedNode instanceof GemDrawer) {
                // The node represents a "module".
                
                if (((GemDrawer)selectedNode).isNamespaceNode()) {
                    // a module namespace node is a 'phantom' node that does not really
                    // correspond to an actual module
                    
                } else {
                    // Add the 'Change Working Module' menu item.
                    ModuleName moduleName = ((GemDrawer) selectedNode).getModuleName();
                    Action changeModuleAction = getChangeModuleAction(moduleName);
                    popupMenu.add(GemCutter.makeNewMenuItem(changeModuleAction));

                    // Add a separator.
                    popupMenu.addSeparator();

                    // Add menu items for the workspace.
                    popupMenu.add(GemCutter.makeNewMenuItem(getSyncModuleAction(moduleName)));
                    popupMenu.add(GemCutter.makeNewMenuItem(getSyncModuleToRevisionAction(moduleName)));
                    popupMenu.add(GemCutter.makeNewMenuItem(getRevertModuleAction(moduleName)));
                    popupMenu.addSeparator();
                    popupMenu.add(GemCutter.makeNewMenuItem(getAddTypeDeclsToModuleAction(moduleName)));
                    popupMenu.add(GemCutter.makeNewMenuItem(getCleanModuleImportsAction(moduleName)));
                    popupMenu.add(GemCutter.makeNewMenuItem(getRenameModuleAction(moduleName)));
                    popupMenu.add(GemCutter.makeNewMenuItem(getRemoveModuleAction(moduleName)));
                }
                    
            } else if (selectedNode instanceof GemTreeNode) {
                // The node represents a gem.
                // Add a 'Load Design...' menu item to load the gem design.
            
                GemEntity gemEntity = (GemEntity) selectedNode.getUserObject();
                popupMenu.add(GemCutter.makeNewMenuItem(getOpenDesignAction(gemEntity)));
                popupMenu.add(GemCutter.makeNewMenuItem(getRenameGemAction(gemEntity)));
                popupMenu.add(GemCutter.makeNewMenuItem(getSearchForGemAction(gemEntity)));
                popupMenu.add(GemCutter.makeNewMenuItem(getSearchForGemDefinitionAction(gemEntity)));
            }
                    
            if (popupMenu.getComponentCount() > 0) {
                popupMenu.addSeparator();
            }

            // Add the default menu items.
            gemCutter.getBrowserTree().getBrowserTreeActions().addDefaultMenuItems(popupMenu, selectedNode);

            return popupMenu;
        }
    
    
        /**
         * @param gemEntity the entity to load a saved design for
         * @return an action to load a saved design.
         */
        private Action getOpenDesignAction(final GemEntity gemEntity) {
        
            Action loadDesignAction = new AbstractAction(GemCutter.getResourceString("OpenDesign")) {
                private static final long serialVersionUID = -7655463979081031688L;

                public void actionPerformed(ActionEvent e) {
                    gemCutter.openGemDesign(gemEntity);
                }
            };
        
            loadDesignAction.putValue(Action.SMALL_ICON, new ImageIcon(GemCutter.class.getResource("/Resources/open.gif")));
            loadDesignAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("OpenDesignToolTip"));        
            loadDesignAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT && gemEntity.hasDesign());
        
            return loadDesignAction;
        }
        
        private Action getRenameModuleAction(final ModuleName moduleName) {
            Action renameModuleAction = new AbstractAction(GemCutter.getResourceString("RenameModule")) {
                private static final long serialVersionUID = 4668938145711913122L;

                public void actionPerformed(ActionEvent e) {
                    gemCutter.showRenameModuleDialog(moduleName);
                }
            };
            
            renameModuleAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("RenameModuleToolTip"));
            renameModuleAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT &&
                    (!moduleName.equals(CAL_Prelude.MODULE_NAME) || gemCutter.isAllowPreludeRenamingMode()));
            
            return renameModuleAction;
        }
        
        /**
         * @param gemEntity the entity to rename 
         * @return an action to popup the rename refactoring dialog.
         */
        private Action getRenameGemAction(final GemEntity gemEntity) {
        
            Action renameGemAction = new AbstractAction(GemCutter.getResourceString("RenameGem")) {
                private static final long serialVersionUID = 5715650083979358935L;

                public void actionPerformed(ActionEvent e) {
                    gemCutter.showRenameEntityDialog(RenameRefactoringDialog.EntityType.Gem, gemEntity.getName().getQualifiedName());
                }
            };
        
            renameGemAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("RenameGemToolTip"));        
            renameGemAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT &&
                    ((!gemEntity.getName().getModuleName().equals(CAL_Prelude.MODULE_NAME))
                    || gemCutter.isAllowPreludeRenamingMode()));
        
            return renameGemAction;
        }

        /**
         * @param gemEntity the entity to search for
         * @return an action to popup the search dialog with the qualified name of the
         *          currently-selected gem filled in.
         */
        private Action getSearchForGemAction(final GemEntity gemEntity) {
            
            Action searchForGemAction = new AbstractAction(GemCutter.getResourceString("SearchForGemPopup")) {
                private static final long serialVersionUID = -6698686245610450051L;

                public void actionPerformed(ActionEvent e) {
                    SearchDialog searchDialog = gemCutter.showSearchDialog();
                    searchDialog.performSearch(gemEntity.getName().getQualifiedName(), SearchDialog.SearchType.REFERENCES);
                }
            };

            searchForGemAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(GemCutterActionKeys.MNEMONIC_SEARCH));
            searchForGemAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("SearchForGemPopupToolTip"));
            searchForGemAction.setEnabled(!gemCutter.isSearchPending());
            
            return searchForGemAction;
        }    
        
        /**
         * @param gemEntity the entity to search for the definition of
         * @return an action to popup the search dialog with the qualified name of the
         *          currently-selected gem filled in.
         */
        private Action getSearchForGemDefinitionAction(final GemEntity gemEntity) {
            
            Action searchForGemDefinitionAction = new AbstractAction(GemCutter.getResourceString("SearchForGemDefinitionPopup")) {
                private static final long serialVersionUID = 8119618611507146279L;

                public void actionPerformed(ActionEvent e) {
                    SearchDialog searchDialog = gemCutter.showSearchDialog();
                    searchDialog.performSearch(gemEntity.getName().getQualifiedName(), SearchDialog.SearchType.DEFINITION);
                }
            };

            searchForGemDefinitionAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("SearchForGemDefinitionPopupToolTip"));
            searchForGemDefinitionAction.setEnabled(!gemCutter.isSearchPending());
            
            return searchForGemDefinitionAction;
        }    
        
        /**
         * Return a new action to change the module
         * @param newModuleName the name of the module to which to change.
         * @return Action the new action.
         */
        private Action getChangeModuleAction(final ModuleName newModuleName) {
        
            Action changeModuleAction = new AbstractAction (GemCutter.getResourceString("ChangeModulePopup")) {
                private static final long serialVersionUID = 7260606500969827421L;

                public void actionPerformed(ActionEvent evt) {
                    gemCutter.doChangeModuleUserAction(newModuleName);
                }
            };
        
            changeModuleAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("ChangeModulePopupToolTip"));
            changeModuleAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT &&
                                          !newModuleName.equals(gemCutter.getWorkingModuleName()));
            
            return changeModuleAction;
        }
        
        /**
         * Return a new action to perform the Add Type Declarations refactoring on a module
         * @param targetModule String name of the module to refactor
         * @return Action the new action
         */
        private Action getAddTypeDeclsToModuleAction(final ModuleName targetModule) {
        
            Action addTypeDeclsAction = new AbstractAction (GemCutter.getResourceString("AddTypedeclsPopup")) {
                private static final long serialVersionUID = 4895749473356263923L;

                public void actionPerformed(ActionEvent evt) {
                    gemCutter.doAddTypeDeclsUserAction(targetModule);
                }
            };
        
            addTypeDeclsAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("AddTypedeclsPopupToolTip"));
            addTypeDeclsAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT);
            
            return addTypeDeclsAction;
        }

        /**
         * Return a new action to perform the Clean Imports refactoring on a module
         * @param targetModule String name of the module to refactor
         * @return Action the new action
         */
        private Action getCleanModuleImportsAction(final ModuleName targetModule) {
        
            Action cleanImportsAction = new AbstractAction (GemCutter.getResourceString("CleanImportsPopup")) {
                private static final long serialVersionUID = 5792348192459545571L;

                public void actionPerformed(ActionEvent evt) {
                    gemCutter.doCleanImportsUserAction(targetModule);
                }
            };
        
            cleanImportsAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("CleanImportsPopupToolTip"));
            cleanImportsAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT);
            
            return cleanImportsAction;
        }

        /**
         * Return a new action to remove a module from the workspace.
         * @param moduleNameToRemove the name of the module to remove from the workspace.
         * @return Action the new action.
         */
        private Action getRemoveModuleAction(final ModuleName moduleNameToRemove) {
        
            Action syncModuleAction = new AbstractAction (GemCutter.getResourceString("RemoveModulePopup")) {
                private static final long serialVersionUID = 7205929596263407738L;

                public void actionPerformed(ActionEvent evt) {
                    gemCutter.doRemoveModuleUserAction(moduleNameToRemove);
                }
            };
        
            syncModuleAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("RemoveModulePopupToolTip"));
            syncModuleAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT &&
                                        !moduleNameToRemove.equals(CAL_Prelude.MODULE_NAME));
            
            return syncModuleAction;
        }
        
        /**
         * Return a new action to sync a module
         * @param moduleNameToSync the name of the module to sync with its vault.
         * @return Action the new action.
         */
        private Action getSyncModuleAction(final ModuleName moduleNameToSync) {
        
            Action syncModuleAction = new AbstractAction (GemCutter.getResourceString("SyncModulePopup")) {
                private static final long serialVersionUID = -2098576892178968782L;

                public void actionPerformed(ActionEvent evt) {
                    gemCutter.doSyncModulesToLatestUserAction(Collections.singletonList(moduleNameToSync));
                }
            };
        
            syncModuleAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("SyncModulePopupToolTip"));
            syncModuleAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT);
            
            return syncModuleAction;
        }
        
        /**
         * Return a new action to sync a module to a given revision
         * @param moduleNameToSync the name of the module to sync with its vault.
         * @return Action the new action.
         */
        private Action getSyncModuleToRevisionAction(final ModuleName moduleNameToSync) {
        
            Action syncModuleToRevisionAction = new AbstractAction (GemCutter.getResourceString("SyncModuleToRevisionPopup")) {
                
                private static final long serialVersionUID = 2447645113483308106L;

                public void actionPerformed(ActionEvent evt) {
                    // Get the associated vault.
                    Vault vault = gemCutter.getWorkspace().getVault(moduleNameToSync);
                    
                    // Show a dialog to get the revision with which to sync.
                    VaultRevisionChooser revisionChooserDialog = new VaultRevisionChooser(gemCutter, vault, true);
                    Integer selectedRevision = revisionChooserDialog.showDialog(moduleNameToSync.toSourceText());
                    
                    // If any revision was selected, sync.
                    if (selectedRevision != null) {
                        int selectedRevisionNum = selectedRevision.intValue();
                        ModuleRevision moduleRevision = new ModuleRevision(moduleNameToSync, selectedRevisionNum);
                        gemCutter.doSyncModulesUserAction(Collections.singletonList(moduleRevision), false);
                    }
                }
            };
        
            syncModuleToRevisionAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("SyncModuleToRevisionPopupToolTip"));
            syncModuleToRevisionAction.setEnabled(gemCutter.getGUIState() == GemCutter.GUIState.EDIT);
            
            return syncModuleToRevisionAction;
        }

        /**
         * Return a new action to sync a module to a given revision
         * @param moduleNameToSync the name of the module to sync with its vault.
         * @return Action the new action.
         */
        private Action getRevertModuleAction(final ModuleName moduleNameToSync) {
            final VaultElementInfo vaultInfo = gemCutter.getWorkspace().getVaultInfo(moduleNameToSync);
            boolean hasVaultInfo = vaultInfo != null;
            
            Action revertModuleAction = new AbstractAction (GemCutter.getResourceString("RevertModulePopup")) {
                private static final long serialVersionUID = 5546677553785665358L;

                public void actionPerformed(ActionEvent evt) {
                    int moduleRevision = vaultInfo.getRevision();       // shouldn't be null.
                    gemCutter.doSyncModulesUserAction(Collections.singletonList(new ModuleRevision(moduleNameToSync, moduleRevision)), true);
                }
            };
            
            // Calculate whether the action should be enabled..
            boolean enableRevert;
            if (gemCutter.getGUIState() != GemCutter.GUIState.EDIT || !hasVaultInfo) {
                enableRevert = false;
            
            } else {
                // Getting the vault status communicates with the vault.
                // This might lead to the undesirable situation where invoking the popup prompts for log on to Enterprise.
                
//                // enabled if the module is modified.
//                VaultStatus moduleVaultStatus = gemCutter.getWorkspace().getVaultStatus(moduleNameToSync);
//                enableRevert = moduleVaultStatus.isModuleModified(moduleNameToSync);
                
                enableRevert = true;
            }
            
            revertModuleAction.putValue(Action.SHORT_DESCRIPTION, GemCutter.getResourceString("RevertModulePopupToolTip"));
            revertModuleAction.setEnabled(enableRevert);
            
            return revertModuleAction;
        }

    }

    
    /**
     * This listener will add gems from the gem browser to the table top. They are connected
     * using intellicut if intellicut is active.
     * @author Frank Worsley
     */
    static class LeafNodeListener implements LeafNodeTriggeredListener {

        /** The GemCutter this listener is for. */
        private final GemCutter gemCutter;
    
        /**
         * Constructor for a new GemCutterVaultTreeLeafNodeListener.
         * @param gemCutter the GemCutter the listener is for
         */
        public LeafNodeListener(GemCutter gemCutter) {
        
            if (gemCutter == null) {
                throw new NullPointerException();
            }
        
            this.gemCutter = gemCutter;
        }
    
        /**
         * @see org.openquark.gems.client.browser.LeafNodeTriggeredListener#leafNodeTriggered(org.openquark.gems.client.browser.LeafNodeTriggeredEvent)
         */
        public void leafNodeTriggered(LeafNodeTriggeredEvent evt) {

            IntellicutManager intellicutManager = gemCutter.getIntellicutManager();
            TableTop tableTop = gemCutter.getTableTop();
        
            BrowserTreeModel browserTreeModel = (BrowserTreeModel) gemCutter.getBrowserTree().getModel();
            
            if (intellicutManager.getIntellicutMode() != IntellicutMode.NOTHING) {

                // evt.getTriggeredObjects() could be empty if a module is selected
                if (!evt.getTriggeredObjects().isEmpty()){
                    Object obj = evt.getTriggeredObjects().get(0);

                    if (obj instanceof GemEntity) {

                        GemEntity gemEntity = (GemEntity) obj;
                        IntellicutInfo intellicutInfo = intellicutManager.getIntellicutInfo(gemEntity);
                        AutoburnUnifyStatus autoburnStatus = intellicutInfo.getAutoburnUnifyStatus();

                        if (autoburnStatus != AutoburnUnifyStatus.NOT_POSSIBLE) {

                            tableTop.getUndoableEditSupport().beginUpdate();

                            // Add the new gem to the table top.
                            DisplayedGem dGem = gemCutter.getTableTop().createDisplayedFunctionalAgentGem(new Point(), gemEntity);
                            Point location = tableTop.findAvailableDisplayedGemLocation(dGem);
                            tableTop.doAddGemUserAction(dGem, location);

                            intellicutManager.attemptIntellicutAutoConnect(dGem);
                            intellicutManager.stopIntellicut();

                            // Set the proper undo name.
                            tableTop.getUndoableEditSupport().setEditName(GemCutterMessages.getString("UndoText_Add", dGem.getDisplayText()));                        
                            tableTop.getUndoableEditSupport().endUpdate();
                        }
                    }
                }

            } else {

                gemCutter.getTableTop().getUndoableEditSupport().beginUpdate();

                List<Object> triggeredObjects = evt.getTriggeredObjects();
                
                for (final Object triggeredObj : triggeredObjects) {
    
                    if (triggeredObj instanceof GemEntity) {
                        
                        GemEntity gemEntity = (GemEntity) triggeredObj;
                        
                        if (browserTreeModel.isVisibleGem(gemEntity)) {
                            DisplayedGem dGem = gemCutter.getTableTop().createDisplayedFunctionalAgentGem(new Point(), gemEntity);
                            gemCutter.getTableTop().doAddGemUserAction(dGem);
                        }
                            
                        // TODO: in future we may want to warn the user if they are trying to add an entity from a module 
                        //   which is not visible, and give them the option of importing it.
                    }
                }
    
                // Override the default undo name if more than one gem was added.
                if (triggeredObjects.size() > 1) {
                    gemCutter.getTableTop().getUndoableEditSupport().setEditName(GemCutter.getResourceString("UndoText_AddGems"));
                }

                gemCutter.getTableTop().getUndoableEditSupport().endUpdate();
            }
        }
    }

    /**
     * If intellicut is pulsing this will display a status message if
     * the user hovers the mouse over a gem in the gem browser.
     * @author Edward Lam
     */
    static class MouseListener extends MouseMotionAdapter { 

        /** The GemCutter the mouse listener is for. */
        private final GemCutter gemCutter;
    
        /**
         * Constructs a new GemCutterVaultTreeMouseListener
         * @param gemCutter the GemCutter the listener is for
         */
        public MouseListener(GemCutter gemCutter) {
        
            if (gemCutter == null) {
                throw new NullPointerException();
            }
        
            this.gemCutter = gemCutter;
        }
    
        /**
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseMoved(MouseEvent e) {

            IntellicutManager intellicutManager = gemCutter.getIntellicutManager();
            StatusMessageDisplayer statusMessageDisplayer = gemCutter.getStatusMessageDisplayer();
        
            TreePath path = gemCutter.getBrowserTree().getPathForLocation(e.getX(), e.getY());
            Object lastPathComponent = path != null ? path.getLastPathComponent() : null; 

            if (!(lastPathComponent instanceof GemTreeNode)) {
                statusMessageDisplayer.clearMessage(this);

            } else {

                // If we've hovering over a gem display an appropriate message
                GemTreeNode treeNode = (GemTreeNode) lastPathComponent;
                GemEntity gemEntity = (GemEntity) treeNode.getUserObject();

                if (intellicutManager.getIntellicutMode() == IntellicutManager.IntellicutMode.NOTHING) {
                    statusMessageDisplayer.clearMessage(this);

                } else if (((BrowserTreeModel) gemCutter.getBrowserTree().getModel()).isVisibleGem(gemEntity)) {

                    IntellicutInfo intellicutInfo = intellicutManager.getIntellicutInfo(gemEntity);
                    AutoburnUnifyStatus autoburnStatus = intellicutInfo.getAutoburnUnifyStatus();

                    if (autoburnStatus.isAutoConnectable()) {
                        statusMessageDisplayer.setMessageFromResource(this, "SM_IntellicutGemConnectable", StatusMessageDisplayer.MessageType.PERSISTENT);
                    } else {
                        statusMessageDisplayer.clearMessage(this);
                    }

                } else {
                    statusMessageDisplayer.clearMessage(this);
                }
            }
        }
    }
}
