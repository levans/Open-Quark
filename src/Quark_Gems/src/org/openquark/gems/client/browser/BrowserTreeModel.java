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
 * BrowserTreeModel.java
 * Creation date: (9/14/00 10:58:58 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.browser;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.services.GemComparatorSorter;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.GemFormSorter;
import org.openquark.cal.services.GemSorter;
import org.openquark.cal.services.GemUnqualifiedNameCaseInsensitiveSorter;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.FeatureVisibilityPolicy;
import org.openquark.gems.client.ModuleNameDisplayUtilities;
import org.openquark.gems.client.ModuleNameDisplayUtilities.TreeViewDisplayMode;
import org.openquark.gems.client.browser.BrowserTreeNode.NodeReuseInfo;
import org.openquark.gems.client.browser.GemCategory.CategoryKey;
import org.openquark.util.UnsafeCast;
import org.openquark.util.WildcardPatternMatcher;


/**
 * The model for the browser tree displayed in the outline control.
 * This model contains the abstract tree model and has various methods to construct the displayed tree.
 * 
 * @author Luke Evans
 */
public class BrowserTreeModel extends DefaultTreeModel {
    private static final long serialVersionUID = 2746652802349469424L;

    /** Refers to the root of the abstract tree. */
    private final RootAbstractTreeNode workspaceAbstractTreeNode;

    /** Refers to the tree node for the workspace. */
    private final WorkspaceRootTreeNode workspaceTreeNode = new WorkspaceRootTreeNode(BrowserMessages.getString("GB_WorkspaceNodeName"));

    /** Refers to the tree node that holds the search results. */
    private final SearchResultRootTreeNode searchResultsTreeNode = new SearchResultRootTreeNode ();

    /**
     * The last search string that was used to search the model. Reload uses this to
     * restore the model state if it is reloaded.
     */
    private String lastSearchString = null;

    /** a map from an entity to a corresponding GemTreeNode.
     * This is used for updating the UI when we want to open "the" node for an entity.
     * There may be more than one, but it doesn't matter which one we pick, so this 
     * map stores only one of the possibilities. */
    private final Map<GemEntity, GemTreeNode> entityToTreeNodeMap;

    /** The perspective which this model represents (if any). */
    private Perspective perspective = null;
    
    /** If true unimported modules will be shown. */
    private boolean showAllModules = true;
    
    /** If true show only public gems. default is to show all gems*/
    private boolean showPublicGemsOnly = false;
   
    /** This predicate decides if a CAL feature is visible to the user */
    private FeatureVisibilityPolicy visibilityPolicy;
    
    /** This module name resolver maintains the module name resolution rules for the currently displayed set of modules. */
    private ModuleNameResolver workspaceModuleNameResolver = ModuleNameResolver.make(Collections.<ModuleName>emptySet());
    
    static final class GemDrawerCategoryInfo {
        private final ModuleName moduleName;
        private final boolean isNamespaceNode;
        
        /**
         * @param moduleName
         * @param isNamespaceNode
         */
        GemDrawerCategoryInfo(final ModuleName moduleName, final boolean isNamespaceNode) {
            this.moduleName = moduleName;
            this.isNamespaceNode = isNamespaceNode;
        }

        ModuleName getModuleName() {
            return moduleName;
        }

        boolean isNamespaceNode() {
            return isNamespaceNode;
        }
        
        @Override
        public boolean equals(Object other) {
            if (other instanceof GemDrawerCategoryInfo) {
                return equals((GemDrawerCategoryInfo)other);
            } else {
                return false;
            }
        }
        
        public boolean equals(GemDrawerCategoryInfo other) {
            return this.moduleName.equals(other.moduleName) && this.isNamespaceNode == other.isNamespaceNode;
        }
        
        @Override
        public int hashCode() {
            return 37 * moduleName.hashCode() + Boolean.valueOf(isNamespaceNode).hashCode();
        }
    }
    
    private final class MaterialGemDrawerCategoryNodeProvider implements CategoryNodeProvider {
        
        private final TreeViewDisplayMode moduleTreeDisplayMode;
        
        private MaterialGemDrawerCategoryNodeProvider(TreeViewDisplayMode moduleTreeDisplayMode) {
            if (moduleTreeDisplayMode == null) {
                throw new NullPointerException();
            }
            this.moduleTreeDisplayMode = moduleTreeDisplayMode;
        }
        
        private TreeViewDisplayMode getModuleTreeDisplayMode() {
            return moduleTreeDisplayMode;
        }
        
        public BrowserTreeNode addCategoryNodeToParent(final BrowserTreeNode parent, final GemCategory category, final NodeReuseInfo nodeReuseInfo) {
            
            final GemDrawerCategoryInfo categoryInfo = (GemDrawerCategoryInfo)(category.getCategoryKey()).getValue();
            final ModuleName moduleName = categoryInfo.getModuleName();
            
            if (nodeReuseInfo != null) {
                final GemDrawer reusedGemDrawer = nodeReuseInfo.getDescendantGemDrawer(moduleName);
                
                // the namespace node state must match before the node can be reused
                if (reusedGemDrawer != null && !reusedGemDrawer.isNamespaceNode()) {
                    
                    if (getModuleTreeDisplayMode() != TreeViewDisplayMode.HIERARCHICAL) {
                        return parent.addCategoryNode(reusedGemDrawer, category);
                        
                    } else {
                        final int nComponents = moduleName.getNComponents();
                        
                        BrowserTreeNode parentNode = parent;
                        
                        for (int i = 0; i < nComponents; i++) {
                            final ModuleName prefix = moduleName.getPrefix(i + 1); // goes from 1 to nComponents through the loop iterations 
                            final boolean isIntermediateNamespaceNode = i != nComponents - 1;
                            final GemCategory prefixCategory = new GemCategory(new GemCategory.CategoryKey<GemDrawerCategoryInfo>(new GemDrawerCategoryInfo(prefix, isIntermediateNamespaceNode)));
                            
                            final GemDrawer gemDrawer;
                            if (!isIntermediateNamespaceNode) {
                                // for the final node in the sequence, use the reused node
                                gemDrawer = reusedGemDrawer;
                            } else {
                                gemDrawer = new MaterialGemDrawer(prefix, workspaceModuleNameResolver, getModuleTreeDisplayMode(), true);
                            }

                            // prefixNode may be different from materialGemDrawer - if the parent already has a node of the same category
                            final BrowserTreeNode prefixNode = parentNode.addCategoryNode(gemDrawer, prefixCategory);
                            
                            // for the next loop iteration:
                            parentNode = prefixNode;
                        }
                        
                        return parentNode;
                        
                    }
                }
            }
            
            if (getModuleTreeDisplayMode() != TreeViewDisplayMode.HIERARCHICAL) {
                return parent.addCategoryNode(new MaterialGemDrawer(moduleName, workspaceModuleNameResolver, getModuleTreeDisplayMode(), false), category);
                
            } else {
                final int nComponents = moduleName.getNComponents();
                
                BrowserTreeNode parentNode = parent;
                
                for (int i = 0; i < nComponents; i++) {
                    final ModuleName prefix = moduleName.getPrefix(i + 1); // goes from 1 to nComponents through the loop iterations 
                    final boolean isIntermediateNamespaceNode = i != nComponents - 1;
                    final GemCategory prefixCategory = new GemCategory(new GemCategory.CategoryKey<GemDrawerCategoryInfo>(new GemDrawerCategoryInfo(prefix, isIntermediateNamespaceNode)));
                    
                    final MaterialGemDrawer materialGemDrawer = new MaterialGemDrawer(prefix, workspaceModuleNameResolver, getModuleTreeDisplayMode(), isIntermediateNamespaceNode);

                    // prefixNode may be different from materialGemDrawer - if the parent already has a node of the same category
                    final BrowserTreeNode prefixNode = parentNode.addCategoryNode(materialGemDrawer, prefixCategory);
                    
                    // for the next loop iteration:
                    parentNode = prefixNode;
                }
                
                return parentNode;
            }
        }
    }

    /**
     * The abstract super class for nodes in the abstract tree.
     * The abstract tree has three levels.
     * 1) The root workspace level  (the root of the tree).
     * 2) The Drawer level (corresponding to modules).
     * 3) The Entity level
     * 
     * It is possible to get the entire subtree rooted at the node that you are looking
     * at simply by using its children list, and its children's children list...etc.
     */
    private static abstract class ModelAbstractTreeNode {

        /** The name of this node */
        private String name;
        
        /**
         * Constructor for a ModelAbstractTreeNode
         * @param nodeName the name of the node
         */
        public ModelAbstractTreeNode(String nodeName) {
            this.name = nodeName;
        }

        public String getName() {
            return name;
        }
        
        public void setName(String nodeName) {
            name = nodeName;
        }
    } 
    
    /**
     * A model tree node that can have children.
     * @author Edward Lam
     */
    private static class AbstractParentTreeNode extends ModelAbstractTreeNode {

        /** The children of this node. */
        private final List<ModelAbstractTreeNode> childrenList;
        
        /** Map from child node name to the child node. */
        private final Map<String, ModelAbstractTreeNode> childMap;
        
        public AbstractParentTreeNode(String viewName) {
            super(viewName);

            childrenList = new ArrayList<ModelAbstractTreeNode>();
            childMap = new HashMap<String, ModelAbstractTreeNode>();
        }

        /**
         * Add a child to this abstract tree node.
         * @param childNode ModelAbstractTreeNode the child node to add.
         * @return boolean whether the add was successful.
         */    
        public boolean addChild(ModelAbstractTreeNode childNode) {
            childMap.put(childNode.getName(), childNode);
            return childrenList.add(childNode);
        }

        /**
         * Get a child of this node by name.
         * @param childName String the name of the child to retrieve
         * @return ModelAbstractTreeNode the child node with the given name
         */
        public ModelAbstractTreeNode getChild(String childName) {
            return childMap.get(childName);
        }
        
        /**
         * Returns whether there is a child whose name is prefixed by the given prefix.
         * @param namePrefix
         * @return true if there is a child whose name is prefixed by the given prefix.
         */
        public boolean hasChildWithPrefix(final String namePrefix) {
            for (final String childName : childMap.keySet()) {
                if (childName.startsWith(namePrefix)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Get the children of this node.
         * @return List the children of the node.
         */
        public List<ModelAbstractTreeNode> getChildrenList() {
            return (childrenList != null) ? new ArrayList<ModelAbstractTreeNode>(childrenList) : new ArrayList<ModelAbstractTreeNode>();
        }

        /**
         * Clear all children.  Needed for hack GemCutter reentrancy.
         * Only needed by ViewAbstractTreeNode in order to clear all drawers..
         */
        void clearChildren() {
            childMap.clear();
            childrenList.clear();
        }
        
        /**
         * Compare two children by their order in the abstract tree..
         * @param child1 the first child to compare
         * @param child2 the second child to compare
         * @return int the result of the comparison.
         */
        int compareOrder(ModelAbstractTreeNode child1, ModelAbstractTreeNode child2) {

            if (child1 == null || child2 == null) {
                throw new IllegalArgumentException("Attempt to compare nodes which are null.");
            }

            if (child1 == child2) {
                return 0;
            }

            // Get the children's relative order
            int index1 = childrenList.indexOf(child1);
            int index2 = childrenList.indexOf(child2);
            
            if (index1 < 0 || index2 < 0) {
                throw new IllegalArgumentException("Attempt to compare nodes which are not children.");
            }
            
            return index1 - index2;
        }

        /**
         * Compare two children by their order in the abstract tree..
         * @param childName1 the name of the first child to compare
         * @param childName2 the name of the second child to compare
         * @return int the result of the comparison.
         */
        int compareOrder(String childName1, String childName2) {
            return compareOrder(getChild(childName1), getChild(childName2));
        }
    }
    
    /**
     * The abstract tree node for the root.
     * There exists only one root for the tree.
     */
    private static class RootAbstractTreeNode extends AbstractParentTreeNode {
        public RootAbstractTreeNode(String nodeName) {
            super(nodeName);
        }
    }
    
    /**
     * The abstract GemDrawer tree node.
     * A GemDrawer is a module (or possibly some other source of entities).
     */
    private static abstract class GemDrawerAbstractTreeNode extends AbstractParentTreeNode {
        public GemDrawerAbstractTreeNode(String gemDrawerName) {
            super(gemDrawerName);
        }
    }
    
    /**
     * The abstract MaterialGemDrawer tree node.
     * It represents material (compiled) modules and entities.
     */
    private static class MaterialGemDrawerAbstractTreeNode extends GemDrawerAbstractTreeNode {
        public MaterialGemDrawerAbstractTreeNode(ModuleName moduleName) {
            super(getGemDrawerNameForModule(moduleName));      
        }
    }
    
    /**
     * The model tree node that holds an entity.
     * @author Edward Lam
     */
    private static class EntityAbstractTreeNode extends ModelAbstractTreeNode {  
        private final GemEntity gemEntity;
    
        public EntityAbstractTreeNode(GemEntity gemEntity) {
            super(gemEntity.getName().getUnqualifiedName());
            this.gemEntity = gemEntity;
        }   
    
        public GemEntity getEntity() {
            return gemEntity;
        }   
    }

    /**
     * Get a comparator on entities according to the name of the gem drawer they are in.
     * The primary key is whether the drawer actually exists.
     * The secondary key is the name of the drawer.
     * @author Edward Lam
     */
    private class GemDrawerNameComparator implements Comparator<GemCategory.CategoryKey<GemDrawerCategoryInfo>> {
        
        private final TreeViewDisplayMode moduleTreeDisplayMode;
        
        private GemDrawerNameComparator(final TreeViewDisplayMode moduleTreeDisplayMode) {
            if (moduleTreeDisplayMode == null) {
                throw new NullPointerException();
            }
            this.moduleTreeDisplayMode = moduleTreeDisplayMode;
        }
        
        private TreeViewDisplayMode getModuleTreeDisplayMode() {
            return moduleTreeDisplayMode;
        }
        
        public int compare(final GemCategory.CategoryKey<GemDrawerCategoryInfo> e1, final GemCategory.CategoryKey<GemDrawerCategoryInfo> e2) {

            final GemDrawerCategoryInfo categoryInfo1 = e1.getValue();
            final GemDrawerCategoryInfo categoryInfo2 = e2.getValue();

            final ModuleName moduleName1 = categoryInfo1.getModuleName();
            final ModuleName moduleName2 = categoryInfo2.getModuleName();
            
            final boolean isNamespaceNode1 = categoryInfo1.isNamespaceNode();
            final boolean isNamespaceNode2 = categoryInfo2.isNamespaceNode();
            
            return compareDrawers(moduleName1, isNamespaceNode1, moduleName2, isNamespaceNode2);            
        }

        /**
         * Compares the 2 given drawers by their names and by whether they are namespace nodes or not.
         * @param moduleName1
         * @param isNamespaceNode1
         * @param moduleName2
         * @param isNamespaceNode2
         */
        private int compareDrawers(final ModuleName moduleName1, final boolean isNamespaceNode1, final ModuleName moduleName2, final boolean isNamespaceNode2) {
            
            final boolean isVisible1 = isNamespaceNode1 ? doesNamespaceContainVisibleModules(moduleName1) : isVisibleModule(moduleName1);
            final boolean isVisible2 = isNamespaceNode2 ? doesNamespaceContainVisibleModules(moduleName2) : isVisibleModule(moduleName2);
            
            if (getModuleTreeDisplayMode() == TreeViewDisplayMode.HIERARCHICAL) {
                
                final ModuleName commonPrefix = moduleName1.getCommonPrefix(moduleName2);
                final int commonPrefixLength = (commonPrefix == null) ? 0 : commonPrefix.getNComponents();
                
                final int moduleNameLength1 = moduleName1.getNComponents();
                final int moduleNameLength2 = moduleName2.getNComponents();
                
                if (commonPrefixLength == moduleNameLength1) {
                    if (commonPrefixLength == moduleNameLength2) {
                        // the module names are equal, so the namespace node comes after the module node
                        if (isNamespaceNode1) {
                            if (isNamespaceNode2) {
                                return 0;
                            } else {
                                return 1;
                            }
                        } else if (isNamespaceNode2) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } else {
                        // moduleName1 is a prefix of moduleName2, so moduleName2 comes after because its namespace node
                        // which shares the name of moduleName1 comes after
                        return -1;
                    }
                    
                } else if (commonPrefixLength == moduleNameLength2) {
                    // moduleName2 is a prefix of moduleName1, so moduleName1 comes after because its namespace node
                    // which shares the name of moduleName2 comes after
                    return 1;
                    
                } else {
                    // neither module name is a prefix of the other, so order by the immediate children of the common prefix
                    final ModuleName shortestDisambiguator1 = moduleName1.getPrefix(commonPrefixLength + 1);
                    final ModuleName shortestDisambiguator2 = moduleName2.getPrefix(commonPrefixLength + 1);
                    
                    final boolean shortestDisambiguatorIsNamespaceNode1 =
                        (shortestDisambiguator1.getNComponents() == moduleName1.getNComponents()) ? isNamespaceNode1 : true;
                    
                    final boolean shortestDisambiguatorIsNamespaceNode2 =
                        (shortestDisambiguator2.getNComponents() == moduleName2.getNComponents()) ? isNamespaceNode2 : true;
                    
                    final boolean shortestDisambiguatorIsVisible1 =
                        shortestDisambiguatorIsNamespaceNode1 ? doesNamespaceContainVisibleModules(shortestDisambiguator1) : isVisibleModule(shortestDisambiguator1);
                        
                    final boolean shortestDisambiguatorIsVisible2 =
                        shortestDisambiguatorIsNamespaceNode2 ? doesNamespaceContainVisibleModules(shortestDisambiguator2) : isVisibleModule(shortestDisambiguator2);
                    
                    // Note that drawers which are visible should always come before those that aren't.
                    if (shortestDisambiguatorIsVisible1 && !shortestDisambiguatorIsVisible2) {
                        return -1;
                    
                    } else if (!shortestDisambiguatorIsVisible1 && shortestDisambiguatorIsVisible2) {
                        return 1;
                    
                    } else {
                        // These drawers either are both visible, or both invisible... in both cases they are treated in a similar fashion
                        return shortestDisambiguator1.compareTo(shortestDisambiguator2);
                    }
                }
                
            } else if (getModuleTreeDisplayMode() == TreeViewDisplayMode.FLAT_FULLY_QUALIFIED) {
                
                // Note that drawers which are visible should always come before those that aren't.
                if (isVisible1 && !isVisible2) {
                    return -1;
                
                } else if (!isVisible1 && isVisible2) {
                    return 1;
                
                } else {
                    // These drawers either are both visible, or both invisible... in both cases they are treated in a similar fashion
                    return moduleName1.compareTo(moduleName2);
                }
                
            } else if (getModuleTreeDisplayMode() == TreeViewDisplayMode.FLAT_ABBREVIATED) {
                
                // Note that drawers which are visible should always come before those that aren't.
                if (isVisible1 && !isVisible2) {
                    return -1;
                
                } else if (!isVisible1 && isVisible2) {
                    return 1;
                
                } else {
                    // These drawers either are both visible, or both invisible... in both cases they are treated in a similar fashion
                    final int nameBasedResult = moduleName1.getLastComponent().compareTo(moduleName2.getLastComponent());
                    if (nameBasedResult != 0) {
                        return nameBasedResult;
                    } else {
                        // if the last components are the same (and thus the names are abbreviated to the same name), they
                        // must be distinguished by their immediate prefixes.
                        final ModuleName immediatePrefix1 = moduleName1.getImmediatePrefix();
                        final ModuleName immediatePrefix2 = moduleName2.getImmediatePrefix();
                        
                        if (immediatePrefix1 == null) {
                            if (immediatePrefix2 == null) {
                                return 0;
                            } else {
                                return -1;
                            }
                        } else if (immediatePrefix2 == null) {
                            return 1;
                        } else {
                            return immediatePrefix1.compareTo(immediatePrefix2);    
                        }
                    }
                }
                
            } else {
                throw new IllegalStateException("Unexpected TreeViewDisplayMode: " + getModuleTreeDisplayMode());
            }
        }
    }

    /**
     * A category node provider provides a tree node for a given category.
     * @author Edward Lam
     */
    interface CategoryNodeProvider {
        /**
         * Get the tree node appropriate to hold nodes in a given category.
         * @param parent the parent to which the node is to be added.
         * @param category the category
         * @param nodeReuseInfo the node reuse info
         * @return BrowserTreeNode the node that ends up as a child of the parent.
         */
        public BrowserTreeNode addCategoryNodeToParent(BrowserTreeNode parent, GemCategory category, NodeReuseInfo nodeReuseInfo);
    }

    /**
     * The default category node provider just returns an empty default mutable tree node. 
     * @author Edward Lam
     */
    public static class DefaultCategoryNodeProvider implements CategoryNodeProvider {

        /** {@inheritDoc} */
        public BrowserTreeNode addCategoryNodeToParent(final BrowserTreeNode parent, final GemCategory category, final NodeReuseInfo nodeReuseInfo) {
            if (nodeReuseInfo == null) {
                return parent.addCategoryNode(new GemCategoryNode(category.getCategoryKey()), category);
            } else {
                final BrowserTreeNode reusedNode = nodeReuseInfo.getChildNodeFromDisplayedString(category.getCategoryKey().getName(), GemCategoryNode.class);
                if (reusedNode != null) {
                    return parent.addCategoryNode(reusedNode, category);
                } else {
                    return parent.addCategoryNode(new GemCategoryNode(category.getCategoryKey()), category);
                }
            }
        }
    }

    /**
     * The special implementation of the org.apache.commons.collections 
     * <code>Predicate</code> interface that filters out modules that fail
     * the visibility check.
     */    
    private class MetaModuleListFilter implements Predicate {
        /** @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object) */
        public boolean evaluate(Object object) {
            if (object instanceof MetaModule) {
                return visibilityPolicy.isModuleVisible((MetaModule) object);
            }
            return false;
        }
    }

    /**
     * A GemCategorizer which categorizes by a GemEntity's module name.
     * @author Edward Lam
     */
    private class ModuleCategorizer extends GemCategorizer<GemDrawerCategoryInfo> {
        
        private final GemDrawerNameComparator drawerComparator;
        
        private final TreeViewDisplayMode moduleTreeDisplayMode;

        /**
         * Constructor for a VaultModel.ModuleCategorizer.
         */
        public ModuleCategorizer(final List<GemDrawerCategoryInfo> categoriesToInclude, final boolean formNewCategories, final TreeViewDisplayMode moduleTreeDisplayMode) {
            super(categoriesToInclude, formNewCategories);
            if (moduleTreeDisplayMode == null) {
                throw new NullPointerException();
            }
            this.drawerComparator = new GemDrawerNameComparator(moduleTreeDisplayMode);
            this.moduleTreeDisplayMode = moduleTreeDisplayMode;
        }
        
        TreeViewDisplayMode getModuleTreeDisplayMode() {
            return moduleTreeDisplayMode;
        }
        
        @Override
        public List<GemCategory.CategoryKey<GemDrawerCategoryInfo>> getCategoryKeyList(GemEntity gemEntity) {
            final ModuleName moduleName = gemEntity.getName().getModuleName();
            return Collections.singletonList(new GemCategory.CategoryKey<GemDrawerCategoryInfo>(new GemDrawerCategoryInfo(moduleName, false)));
        }

        @Override
        public int compareKey(GemCategory.CategoryKey<GemDrawerCategoryInfo> key1, GemCategory.CategoryKey<GemDrawerCategoryInfo> key2) {
            return drawerComparator.compare(key1, key2);
        }
    }
    
    /**
     * Default constructor for BrowserTreeModel.
     * Automatically constructs a root node in the abstract tree and display
     * all modules/gems found in the workspace.
     */
    public BrowserTreeModel() {
        // The main root node will not be visible
        super(new BrowserTreeRootNode(), true);

        // Add the search results and workspace nodes
        ((BrowserTreeNode) getRoot()).add(workspaceTreeNode);
        ((BrowserTreeNode) getRoot()).add(searchResultsTreeNode);
        
        // Create a new root abstract tree node.
        workspaceAbstractTreeNode = new RootAbstractTreeNode(workspaceTreeNode.getName());

        // Use a weak hashmap for the entities so that GemEntities which are no longer
        // loaded are automatically cleared from the map.
        entityToTreeNodeMap = new WeakHashMap<GemEntity, GemTreeNode>();

        // visibility predicate can never be null
        visibilityPolicy = FeatureVisibilityPolicy.getDefault();
    }   
    
    /**
     * Returns the visibility policy used for this browser tree model.  The return value can never be null.
     * @return FeatureVisibilityPolicy
     */
    public FeatureVisibilityPolicy getFeatureVisibilityPolicy() {
        return visibilityPolicy;
    }

    /**
     * Sets the visibility policy used for this browser tree model.  A visibility policy is a predicate
     * function that determines in a module or a gem is visible in the browser tree.  If
     * <code>null</code> is used as an argument, then the default visibility predicate is used.
     * @param predicate
     */
    public void setFeatureVisibilityPolicy(FeatureVisibilityPolicy predicate) {
        if (predicate == null) {
            visibilityPolicy = FeatureVisibilityPolicy.getDefault();
        } else {
            visibilityPolicy = predicate;
        }
    }

    /**
     * Return whether a given module is visible from the perspective from which the tree was last populated.
     * @param moduleName
     * @return whether the module is visible from the perspective from which the tree was last populated.
     */
    public boolean isVisibleModule(ModuleName moduleName) {
        if (perspective == null) {
            return false;
        }
        return perspective.isVisibleModule(moduleName);
    }
    
    /**
     * Return whether a given namespace contains modules visible from the perspective from which the tree was last populated.
     * @param namespaceName
     * @return whether the namespace contains modules visible from the perspective from which the tree was last populated.
     */
    public boolean doesNamespaceContainVisibleModules(ModuleName namespaceName) {
        if (perspective == null) {
            return false;
        }
        
        final MetaModule workingModule = perspective.getWorkingModule();
        if (workingModule == null) {
            return false;
        }
        
        if (namespaceName.isProperPrefixOf(workingModule.getName())) {
            return true;
        }
        
        final ModuleTypeInfo moduleTypeInfo = workingModule.getTypeInfo();
        final int n = moduleTypeInfo.getNImportedModules();
        for (int i = 0; i < n; i++) {
            if (namespaceName.isProperPrefixOf(moduleTypeInfo.getNthImportedModule(i).getModuleName())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Return whether a given gem is visible from the perspective from which the tree was last populated.
     * @param gemEntity
     * @return whether the gem is visible from the perspective from which the tree was last populated.
     */
    public boolean isVisibleGem(GemEntity gemEntity) {
        ModuleName moduleName = gemEntity.getName().getModuleName();
        return isVisibleModule(moduleName);
    }
    
    /**
     * Return whether a given module is the working module (according to the perspective when the tree was last populated).
     * @param moduleName the name of the module.
     * @return whether the name is the name of the working module.
     */
    boolean isWorkingModule(ModuleName moduleName) {
        if (perspective == null) {
            return false;
        }
        ModuleName workingModuleName = perspective.getWorkingModuleName();
        return workingModuleName.equals(moduleName);
    }

    /**
     * Get a GemTreeNode corresponding to a given entity.
     * This is usually, but NOT ALWAYS, the only node that corresponds to the entity.
     * Some entities will have multiple associated tree nodes; this function returns one
     * chosen arbitrarily. 
     * @param newGemEntity the entity to look up
     * @return GemTreeNode the corresponding node.
     */
    public GemTreeNode getTreeNode(GemEntity newGemEntity) {
        return entityToTreeNodeMap.get(newGemEntity);
    }

    /**
     * Returns all the entities in the leaf nodes (GemTreeNodes) descended from the specified tree node.
     * @param treeNode the tree node to analyze.
     * @return List of all entities in GemTreeNodes descended from the current node.
     */
    private List<GemEntity> getAllEntities(BrowserTreeNode treeNode) {
        Set<GemEntity> entitySet = new HashSet<GemEntity>();
        getAllEntitiesHelper(treeNode, entitySet);
        return new ArrayList<GemEntity>(entitySet);
    }
    
    /**
     * Adds all the entities in the leaf nodes (GemTreeNodes) descended from the specified tree node to a
     * provided Set. 
     * @param treeNode the tree node to analyze
     * @param entitySet the Set to add the descended entities to
     */
    private void getAllEntitiesHelper(BrowserTreeNode treeNode, Set<GemEntity> entitySet) {    

        if (treeNode instanceof GemTreeNode) {
            entitySet.add((GemEntity)treeNode.getUserObject());
            return;
        }

        int numChildren = treeNode.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            BrowserTreeNode childNode = (BrowserTreeNode)treeNode.getChildAt(i);

            if (childNode instanceof GemTreeNode) {
                entitySet.add((GemEntity)childNode.getUserObject());

            } else {
                // must be a node that holds other nodes
                getAllEntitiesHelper(childNode, entitySet);
            }
        }
    }
    

    /**
     * Categorize all entities descending from this node according to a given categorizer.
     * @param selectedNode The node below which all entities will be categorized.
     * @param categorizer The categorizer to use to categorize the gementities.
     */
    private void categorizeByX(BrowserTreeNode selectedNode, GemCategorizer<?> categorizer) {
        categorizeByX(selectedNode, categorizer, new DefaultCategoryNodeProvider());
    }

    /**
     * Categorize all entities descending from this node according to a given categorizer, and producing category
     * nodes according to a given category node provider.
     * @param selectedNode The node below which all entities will be categorized.
     * @param categorizer The categorizer to use to categorize the gementities.
     * @param provider The CategoryNodeProvider to provide custom nodes to represent the categories.
     */
    private void categorizeByX(BrowserTreeNode selectedNode, GemCategorizer<?> categorizer, CategoryNodeProvider provider) {
        categorizeByX(selectedNode, categorizer, provider, false);
    }

    /**
     * Categorize all entities descending from this node according to a given categorizer, and producing category
     * nodes according to a given category node provider.
     * @param selectedNode The node below which all entities will be categorized.
     * @param categorizer The categorizer to use to categorize the gementities.
     * @param provider The CategoryNodeProvider to provide custom nodes to represent the categories.
     * @param reuseNodes whether to try to reuse child nodes when re-categorizing a given node.
     */
    private void categorizeByX(final BrowserTreeNode selectedNode, GemCategorizer<?> categorizer, CategoryNodeProvider provider, boolean reuseNodes) {

        ModuleTypeInfo workingModuleTypeInfo = perspective.getWorkingModuleTypeInfo();
        
        // Before categorizing sort gems alphabetically so they appear
        // in alphabetical order inside their categories.
        GemComparatorSorter sorter = new GemUnqualifiedNameCaseInsensitiveSorter();
        List<GemEntity> childList = sorter.getSortedList(getAllEntities(selectedNode));

        // Attach the categorizer info to this node.
        selectedNode.setCategorizer(categorizer, provider);

        // Categorize the children list.
        List<GemCategory> categories = categorizer.formCategories(childList);
        
        // Construct the map for reuse of nodes.  Populate if necessary.
        BrowserTreeNode.NodeReuseInfo nodeReuseInfo = reuseNodes ? selectedNode.getChildNameInfo() : null;
        
        if (selectedNode instanceof GemDrawer) {
            // Now, remove all the children of the selected node, and re-insert them into categories.
            selectedNode.removeAllGemNodeDescendantsInGivenDrawerOnly((GemDrawer)selectedNode);
            removeAllChildrenExceptForVisibleDescendantDrawers(selectedNode);            
            
        } else {
            // Now, remove all the children of the selected node, and re-insert them into categories.
            selectedNode.removeAllGemNodeDescendants();
            selectedNode.removeAllChildren();
        }

        int nCategories = categories.size();
        for (int i = 0; i < nCategories; i++) {

            GemCategory category = categories.get(i);
            
            // Create the category node.
            BrowserTreeNode categoryNode = selectedNode.addNewCategoryNode(category, nodeReuseInfo);
                        
            // Add the entity nodes to the category node.
            List<GemEntity> categoryItems = category.getCategoryItems();
            int numEntities = categoryItems.size();
            for (int j = 0; j < numEntities; j++) {
                GemEntity gemEntity = categoryItems.get(j);
                if (selectedNode instanceof SearchResultRootTreeNode) {
                    categoryNode.add(new SearchResultGemTreeNode(gemEntity, workingModuleTypeInfo));
                } else {
                    GemTreeNode node = new GemTreeNode(gemEntity, workingModuleTypeInfo); 
                    categoryNode.add(node);
                    
                    // This node is guaranteed to be in the tree, so set it to be the node associated with the gemEntity
                    entityToTreeNodeMap.put(gemEntity, node);
                }
            }
        }
    
        // Notify the model about the changes.
        invokeOnEventDispatchThread(new Runnable() {
            public void run() {
                nodeStructureChanged(selectedNode);
            }
        });
    }

    /**
     * Starting from the currently selected node, we categorize the currently 
     * selected node's children according to the child's arity.
     * Note: Categorize means that the children will be put into different folders based on its arity.
     * @param selectedNode The node below which all entities will be categorized.
     */
    public void categorizeByArity(BrowserTreeNode selectedNode) {   

        // Categorize by arity
        GemCategorizer<Integer> categorizer = new GemCategorizer<Integer>() {
           
            @Override
            public List<GemCategory.CategoryKey<Integer>> getCategoryKeyList(GemEntity gemEntity) {
                return Collections.singletonList(new GemCategory.CategoryKey<Integer>(Integer.valueOf(gemEntity.getTypeArity())));
            }

            @Override
            public int compareKey(CategoryKey<Integer> key1, CategoryKey<Integer> key2) {
                return key1.getValue().compareTo(key2.getValue());
            }
            
        };

        categorizeByX(selectedNode, categorizer);
    }

    /**
     * Starting from the currently selected node, we categorize the currently 
     * selected node's children according to the child's gem type (type signature).
     * Note: Categorize means that the children will be put into different folders based on their type.
     * @param selectedNode The node below which all entities will be categorized.
     */
    public void categorizeByGemType(BrowserTreeNode selectedNode) {  

        // Categorize by type expr
        GemCategorizer<String> categorizer = new GemCategorizer<String>() {

            private final ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(perspective.getWorkingModuleTypeInfo());
            
            @Override
            public List<GemCategory.CategoryKey<String>> getCategoryKeyList(GemEntity gemEntity) {
                return Collections.singletonList(new GemCategory.CategoryKey<String>(gemEntity.getTypeExpr().toString(namingPolicy)));
            }

            // Sort categories alphabetically 
            @Override
            public int compareKey(GemCategory.CategoryKey<String> key1, GemCategory.CategoryKey<String> key2) {
                return key1.getValue().compareTo(key2.getValue());
            }
        };

        categorizeByX(selectedNode, categorizer);
    }

    /**
     * Starting from the currently selected node, we categorize the currently 
     * selected node's children according to the child's output type.
     * Note: Categorize means that the children will be put into different folders based on its arity.
     * @param selectedNode The node below which all entities will be categorized.
     */
    public void categorizeByOutput(BrowserTreeNode selectedNode) {  

        // Categorize by output
        GemCategorizer<String> categorizer = new GemCategorizer<String>() {
            
            private final ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(perspective.getWorkingModuleTypeInfo());
            
            @Override
            public List<GemCategory.CategoryKey<String>> getCategoryKeyList(GemEntity gemEntity) {
                return Collections.singletonList(new GemCategory.CategoryKey<String>(gemEntity.getTypeExpr().getResultType().toString(namingPolicy)));
            }

            // Sort categories alphabetically
            @Override
            public int compareKey(GemCategory.CategoryKey<String> key1, GemCategory.CategoryKey<String> key2) {
                return key1.getValue().compareTo(key2.getValue());
            }
        };

        categorizeByX(selectedNode, categorizer);
    }
    
    /**
     * Starting from the currently selected node, we categorize the currently 
     * selected node's children according to the child's input type.
     * Note: Categorize means that the children will be put into different folders based on their input type.
     * @param selectedNode The node below which all entities will be categorized.
     */
    public void categorizeByInput(BrowserTreeNode selectedNode) {  

        // Categorize by input
        GemCategorizer<String> categorizer = new GemCategorizer<String>() {
            
            // the category key to use for gems with no inputs
            private final GemCategory.CategoryKey<String> noInputsKey = new GemCategory.CategoryKey<String>(BrowserMessages.getString("GB_NoInputs"));

            private final ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(perspective.getWorkingModuleTypeInfo());
            
            // Each gem belongs to the category of each of its argument types
            @Override
            public List<GemCategory.CategoryKey<String>> getCategoryKeyList(GemEntity gemEntity) {
                int arity = gemEntity.getTypeExpr().getArity();
                
                if(arity == 0) {
                    return Collections.singletonList(noInputsKey);
                }
                
                Set<GemCategory.CategoryKey<String>> categories = new HashSet<GemCategory.CategoryKey<String>>();
                for (int i = 0; i < arity; i++) {
                    String categoryName = gemEntity.getTypeExpr().getTypePieces()[i].toString(namingPolicy);
                    categories.add(new GemCategory.CategoryKey<String>(categoryName));
                }

                return new ArrayList<GemCategory.CategoryKey<String>>(categories);
            }
            
            // Sort categories alphabetically
            @Override
            public int compareKey(GemCategory.CategoryKey<String> key1, GemCategory.CategoryKey<String> key2) {
                
                // always sort no inputs to the top before everything else
                if (key1.equals(key2)) {
                    return 0;
                }
                
                if (key1.equals(noInputsKey)) {
                    return -1;
                }
                
                if (key2.equals(noInputsKey)) {
                    return 1;
                }
                
                return key1.getValue().compareTo(key2.getValue());
            }
        };
        
        categorizeByX(selectedNode, categorizer);
    }    
    
    /**
     * Starting from the currently selected node, we categorize the currently 
     * selected node's children according to the child's module.
     * @param selectedNode The node below which all entities will be categorized.
     * @param moduleTreeDisplayMode the display mode for the module tree.
     */
    public void categorizeByModule(BrowserTreeNode selectedNode, TreeViewDisplayMode moduleTreeDisplayMode) {

        // Create the categorizer.
        GemCategorizer<GemDrawerCategoryInfo> categorizer = getModuleCategorizer(selectedNode, moduleTreeDisplayMode);
        
        // Provide a custom node for the module.
        CategoryNodeProvider provider = new MaterialGemDrawerCategoryNodeProvider(moduleTreeDisplayMode);

        categorizeByX(selectedNode, categorizer, provider);
    }

    /**
     * Get a ModuleCategorizer based on the current perspective.
     * @param browserTreeNode the tree node for which the categorizer will be provided.
     * @param moduleTreeDisplayMode the display mode for the module tree.
     * @return a ModuleCategorizer for that node.
     */
    private ModuleCategorizer getModuleCategorizer(BrowserTreeNode browserTreeNode, TreeViewDisplayMode moduleTreeDisplayMode) {

        // If it's the first level under the workspace root, include all modules.  
        //   Otherwise, only include those modules which actually contain any gems.
        List<GemDrawerCategoryInfo> categoriesToInclude = (browserTreeNode == workspaceTreeNode) ? getGemDrawerCategoryInfoList(moduleTreeDisplayMode) : Collections.<GemDrawerCategoryInfo>emptyList();
        return new ModuleCategorizer(categoriesToInclude, true, moduleTreeDisplayMode);
    }

    /**
     * @param moduleTreeDisplayMode the display mode for the module tree.
     * @return the list of pairs (drawer name, isNamespaceNode), ordered according to the current visibility policy of the vault.
     * ie. Visible modules in alphabetical order followed optionally by non-visible modules in alphabetical order.
     */
    private List<GemDrawerCategoryInfo> getGemDrawerCategoryInfoList(final TreeViewDisplayMode moduleTreeDisplayMode) {
        List<GemDrawerCategoryInfo> drawerNamesList = new ArrayList<GemDrawerCategoryInfo>();
        
        List<MetaModule> visibleModules = new ArrayList<MetaModule>();
        CollectionUtils.select(perspective.getVisibleMetaModules(), new MetaModuleListFilter(), visibleModules);
        List<MetaModule> hiddenModules = perspective.getInvisibleMetaModules();
        
        // Collect all module names for creating a module name resolver
        Set<ModuleName> moduleNames = new HashSet<ModuleName>();
        for (final MetaModule visibleModule : visibleModules) {
            moduleNames.add(visibleModule.getName());
        }
        for (final MetaModule hiddenModule: hiddenModules) {
            moduleNames.add(hiddenModule.getName());
        }
        
        ModuleNameResolver workspaceModuleNameResolver = ModuleNameResolver.make(moduleNames);
        
        // Add the visible modules in alphabetical order.
        Collections.sort(visibleModules, getModuleAlphaComparator(workspaceModuleNameResolver, moduleTreeDisplayMode));

        for (final MetaModule metaModule : visibleModules) {
            drawerNamesList.add(new GemDrawerCategoryInfo(metaModule.getName(), false));
        }

        // If we are supposed to show all modules then add the invisible ones.
        if (showAllModules) {
            Collections.sort(hiddenModules, getModuleAlphaComparator(workspaceModuleNameResolver, moduleTreeDisplayMode));
            
            for (final MetaModule module : hiddenModules) {
                drawerNamesList.add(new GemDrawerCategoryInfo(module.getName(), false));
            }
        }
        
        return drawerNamesList;
    }
    
    /**
     * Starting from the currently selected node, we sort the currently selected node's 
     * children according to the given sorter.
     * @param selectedNode The node below which all entities will be sorted.
     * @param sorter the sorter to use.
     */
    private void sortByX(final BrowserTreeNode selectedNode, GemComparatorSorter sorter) {

        List<GemEntity> childList = getAllEntities(selectedNode);
        List<GemEntity> sortedList = sorter.getSortedList(childList);
        
        // Attach the sorter to the selected node.
        selectedNode.setSorter(sorter);
        
        // Now, remove all the children of the selected node, and re-insert them in correct order.
        selectedNode.removeAllChildren();   

        // Add the entity nodes in the correct sorting order
        int numEntities = sortedList.size();
        for (int j = 0; j < numEntities; j++) {
            
            GemEntity gemEntity = sortedList.get(j);
            
            if (selectedNode instanceof SearchResultRootTreeNode) {
                selectedNode.add(new SearchResultGemTreeNode(gemEntity, perspective.getWorkingModuleTypeInfo()));
            } else {
                GemTreeNode node = new GemTreeNode(gemEntity, perspective.getWorkingModuleTypeInfo()); 
                selectedNode.add(node);
                
                // This node is guaranteed to be in the tree, so set it to be the node associated with the gemEntity
                entityToTreeNodeMap.put(gemEntity, node);
            }                
        }

        // Notify the model about the changes.
        invokeOnEventDispatchThread(new Runnable() {
            public void run() {
                nodeStructureChanged(selectedNode);
            }
        });
    }

    /**
     * Starting from the currently selected node, we sort the currently selected node's 
     * children according to the alphabet:  A, a, B, b, etc... 
     * Side Note: This method assumes that the gems will have a name of length greater than 0.
     * @param selectedNode The node below which all entities will be sorted.
     */
    public void sortByUnqualifiedName(BrowserTreeNode selectedNode) {
        sortByX(selectedNode, new GemUnqualifiedNameCaseInsensitiveSorter());
    }

    /**
     * Starting from the currently selected node, we sort the currently selected node's 
     * children according to its form (Data Constructor, Supercombinator, Class Method, ...)
     * @param treeNode the node below which all entities will be sorted.
     */
    public void sortByForm(BrowserTreeNode treeNode) {
        sortByX(treeNode, new GemFormSorter());
    }

    /**
     * Determines whether modules not visible from the current perspective should be shown
     * in the tree view. If you change this property you have to reload the module.
     * 
     * @param showAllModules if true all modules will be shown, if false only visible modules
     */
    public void setShowAllModules(boolean showAllModules) {
        this.showAllModules = showAllModules;
    }

    /**
     * Determines whether modules not visible from the current perspective should be shown
     * in the tree view. This value may not reflect what is currently shown in the view
     * since the model needs to be reloaded if the property is changed.
     * 
     * @return boolean true if all modules should be shown, false if only visible modules should be shown
     */
    public boolean getShowAllModules() {
        return showAllModules;
    }

    /**
     * Determines whether only public gems should be displayed. The value may not reflect what 
     * is currently shown in the view, since the model needs to be reloaded if the property is changed.
     * 
     * @param showPublicGemsOnly true if only public gems should be shown, false if all gems 
     * (public, private, protected)should be shown.
     */
    public void setShowPublicGemsOnly(boolean showPublicGemsOnly) {
        this.showPublicGemsOnly = showPublicGemsOnly;
    }
    
    /** 
     * Determines whether only public gems should be displayed. The value may not reflect what 
     * is currently shown in the view, since the model needs to be reloaded if the property is changed.
     *
     * @return boolean true is only public gems should be shown, false if all gems 
     * (public, private, protected)should be shown.
     */
    public boolean getShowPublicGemsOnly() {
        return showPublicGemsOnly;
    }

    /**
     * Reverts the browser tree model to the "original" structure.
     * Drawers are organized in import-dependent order - the order depends on the order of imports into the current drawer
     *   (based on the perspective at the time the model was populated).
     * Entities are organized in alphabetical order within drawers.
     * @param moduleTreeDisplayMode the display mode for the module tree.
     */
    public void arrangeAsDefaultTreeStructure(TreeViewDisplayMode moduleTreeDisplayMode) {

        // Gems appear in alphabetical order on their unqualified name.
        sortByUnqualifiedName(workspaceTreeNode);

        // Categorize the first level by module.
        categorizeByModule(workspaceTreeNode, moduleTreeDisplayMode);
    }

    /**
     * Returns the tree node that represents the "Search Results" node.
     * @return the search results tree node
     */
    public BrowserTreeNode getSearchResultsNode () {
        return searchResultsTreeNode;
    }

    /**
     * Returns the tree node that represents the "Workspace" node.
     * @return the workspace node
     */
    public BrowserTreeNode getWorkspaceNode () {
        return workspaceTreeNode;
    }

    /**
     * Sets the name of the Workspace node in the tree to include the name of
     * the current workspace in square brackets.
     * @param name the name to have in square brackets
     */
    public void setWorkspaceNodeName(String name) {
        String fullName = BrowserMessages.getString("GB_WorkspaceNodeName") + " [" + name + "]";
        getWorkspaceNode().setUserObject(fullName);
        workspaceAbstractTreeNode.setName(fullName);
    }

    /**
     * Performs a sub-string search on all node names and type expressions in the model
     * and inserts nodes for matching gems into the search results node. The search is
     * case insensitive.
     * @param searchString the string to search for
     */
    public void doSearch (String searchString) {

        searchResultsTreeNode.removeAllChildren();
        
        // If the search string is empty we don't do anything
        if (searchString.trim().equals("")) {
            nodeStructureChanged(searchResultsTreeNode);
            lastSearchString = null;
            return;
        }
        
        // We want to search case insensitively.
        Pattern searchPattern = Pattern.compile(WildcardPatternMatcher.wildcardPatternToRegExp(searchString), Pattern.CASE_INSENSITIVE);
        
        // Keep a list of all the matching gem nodes.
        List<GemEntity> resultList = new ArrayList<GemEntity>();
        for (final GemEntity gemEntity : entityToTreeNodeMap.keySet()) {
            if (!showAllModules && !isVisibleGem(gemEntity)) {
                continue;
            }
            
            String nameString = gemEntity.getName().getUnqualifiedName();
            String typeExprQualifiedString = gemEntity.getTypeExpr().toString();
            String typeExprUnqualifiedString = gemEntity.getTypeExpr().toString(ScopedEntityNamingPolicy.UNQUALIFIED);
            
            // we use find() instead of matches() on the Matcher since we want to simply find a matching subsequence
            // and not whether the *whole* string matches the pattern.
            if (searchPattern.matcher(nameString).find() ||
                searchPattern.matcher(typeExprUnqualifiedString).find() ||
                searchPattern.matcher(typeExprQualifiedString).find()) {
                    resultList.add(gemEntity);
                }
        }          
        
        GemComparatorSorter sorter = new GemUnqualifiedNameCaseInsensitiveSorter();
        List<GemEntity> sortedList = sorter.getSortedList(resultList);
        
        // Add the matching gem nodes to the results node.
        for (final GemEntity gemEntity : sortedList) {
            searchResultsTreeNode.add(new SearchResultGemTreeNode(gemEntity, perspective.getWorkingModuleTypeInfo()));
        }
               
        nodeStructureChanged(searchResultsTreeNode);

        lastSearchString = searchString;
    }            

    public String getSearchString() {
        return lastSearchString;
    }
    
    /**
     * Reloads the model from the perspective it was originally populated from and uses the
     * same parameters as when originally populated. Tree structure state is not remembered
     * by the model. Use GemBrowser.refresh() to reload the model and maintain the tree structure.
     * 
     * @see BrowserTreeModel#populate
     * @see org.openquark.gems.client.browser.GemBrowser#refresh()
     */
    @Override
    public void reload() {
        
        // Clear the existing drawers and nodes
        clearDrawers();
        entityToTreeNodeMap.clear();
        
        // Repopulate the model with the previous perspective.
        populate (perspective, showAllModules, TreeViewDisplayMode.FLAT_ABBREVIATED); // this choice of TreeViewDisplayMode shouldn't matter at all, so we just choose the default...
        
        // Update node categorizers on the model.
        updateModuleCategorizers();
        
        // Repeat the previous search to restore the search node.
        // Some new gems might have been added that need to be included in the search.
        if (lastSearchString != null) {
            doSearch (lastSearchString);
        }

        // Apply any categorization or sorting that is associated with the search node.
        GemComparatorSorter sorter = searchResultsTreeNode.getSorter();
        if (sorter != null) {
            sortByX(searchResultsTreeNode, sorter);
        }
        
        GemCategorizer<?> categorizer = searchResultsTreeNode.getCategorizer();
        if (categorizer != null) {
            categorizeByX(searchResultsTreeNode, categorizer, searchResultsTreeNode.getNodeProvider());
        }

        super.reload();
    }
    
    /**
     * Update any module categorizers held by nodes in the tree model to hold on to the current list of modules.
     *   This method should be invoked if the modules to display in the tree are changed.
     */
    private void updateModuleCategorizers() {
        for (Enumeration<TreeNode> nodeEnum = UnsafeCast.unsafeCast(((BrowserTreeNode)getRoot()).breadthFirstEnumeration()); nodeEnum.hasMoreElements(); ) {
            BrowserTreeNode nextNode = (BrowserTreeNode)nodeEnum.nextElement();
            GemCategorizer<?> categorizer = nextNode.getCategorizer();

            if (categorizer != null) {
                if (categorizer instanceof ModuleCategorizer) {
                    categorizer = getModuleCategorizer(nextNode, ((ModuleCategorizer)categorizer).getModuleTreeDisplayMode());
                }
                // recategorize, re-using previously existing categories if possible..
                categorizeByX(nextNode, categorizer, nextNode.getNodeProvider(), true);
            }
        }
    }
    
    /**
     * Populate the model with entities.
     * Note: any modules which already exist both in the tree and in the program will be replaced by the program definition.
     * @param perspective the perspective from which entities are gathered.
     * @param showAllModules whether modules not visible from the given perspective should also be visible.
     * @param moduleTreeDisplayMode the display mode for the module tree.
     */
    public void populate(final Perspective perspective, final boolean showAllModules, final TreeViewDisplayMode moduleTreeDisplayMode) {
        
        this.perspective = perspective;
        this.showAllModules = showAllModules;

        // A set of all GemEntities in the modules.
        Set<GemEntity> allEntities = new LinkedHashSet<GemEntity>();
        
        // Get all visible modules for this perspective.
        List<MetaModule> visibleModules = new ArrayList<MetaModule>();
        CollectionUtils.select(perspective.getVisibleMetaModules(), new MetaModuleListFilter(), visibleModules);
        
        List<MetaModule> hiddenModules = perspective.getInvisibleMetaModules();
        
        // Collect all module names for creating a module name resolver
        Set<ModuleName> moduleNames = new HashSet<ModuleName>();
        for (final MetaModule metaModule : visibleModules) {
            moduleNames.add(metaModule.getName());
        }
        for (final MetaModule metaModule : hiddenModules) {
            moduleNames.add(metaModule.getName());
        }
        
        // Refresh the module name resolver with the current set of module names
        workspaceModuleNameResolver = ModuleNameResolver.make(moduleNames);

        Collections.sort(visibleModules, getModuleAlphaComparator(workspaceModuleNameResolver, moduleTreeDisplayMode));
        
        // Add drawers for each of the modules.
        for (int i = 0, size = visibleModules.size(); i < size; i++) {
            MetaModule module = visibleModules.get(i);
            ModuleName moduleName = module.getName();
            Set<GemEntity> visibleEntitySet = perspective.getVisibleGemEntities(module);
            addDrawer(moduleName, visibleEntitySet);
            allEntities.addAll(visibleEntitySet);
        }
        
        if (showAllModules) {
            // If we are supposed to show all modules then add the invisible ones.
            Collections.sort(hiddenModules, getModuleAlphaComparator(workspaceModuleNameResolver, moduleTreeDisplayMode));
            
            // Add drawers for each of the modules.
            for (final MetaModule module : hiddenModules) {
                ModuleName moduleName = module.getName();

                // Create a set with all the public entities from the module.
                Set<GemEntity> publicEntitySet = perspective.getVisibleGemEntities(module);
    
                addDrawer(moduleName, publicEntitySet);
                
                allEntities.addAll(publicEntitySet);
            }
        }

        // Make sure tree nodes for all the entities exist.
        for (final GemEntity gemEntity : allEntities) {
            if (entityToTreeNodeMap.containsKey(gemEntity)) {
                continue;       // the entity is not new.

            } 

            GemTreeNode newNode = new GemTreeNode(gemEntity, perspective.getWorkingModuleTypeInfo());
            entityToTreeNodeMap.put(gemEntity, newNode);
        }
        
        // Sort the set into a list by unqualified name. This way all the gems
        // appear in alphabetical order in whatever category they fall into.
        GemComparatorSorter sorter = new GemUnqualifiedNameCaseInsensitiveSorter();
        List<GemEntity> sortedList = sorter.getSortedList(allEntities);
        
        // Fill up the workspace node with the entities
        fillTreeStructure(workspaceTreeNode, sortedList);

        invokeOnEventDispatchThread(new Runnable() {
            public void run() {
                nodeStructureChanged(workspaceTreeNode);
            }
        });
    }
    
    /**
     * Needed for Swing consistency. Check which thread the event is currently on.
     * If already on the AWT event thread, just run; else place the event on the AWT event thread queue.

     * @param runnable the event that needs to run on the AWT event thread
     */
    static void invokeOnEventDispatchThread(final Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }
    
    /**
     * Helper method to populate the model with an abstract tree node for a gem drawer.
     * @param moduleName the name of the drawer.
     * @param entitySet The gem entities which "belong" under this drawer.
     *   Nodes will be created for these gem entities and will be put into the drawer named drawerName.
     */
    private void addDrawer(ModuleName moduleName, Set<GemEntity> entitySet) {
        
        // Get the corresponding drawer.  Create and add it if it doesn't already exist.
        GemDrawerAbstractTreeNode drawerNode = (GemDrawerAbstractTreeNode)workspaceAbstractTreeNode.getChild(getGemDrawerNameForModule(moduleName));
        if (drawerNode == null) {
            drawerNode = new MaterialGemDrawerAbstractTreeNode(moduleName);
            workspaceAbstractTreeNode.addChild(drawerNode);
        } 

        // Make sure the drawer is empty
        drawerNode.clearChildren();
            
        // Now add back new EntityNodes for the entities.
        for (final GemEntity gemEntity : entitySet) {
            EntityAbstractTreeNode entityNode = new EntityAbstractTreeNode(gemEntity);
            drawerNode.addChild(entityNode);
        }
    }
    
    /**
     * Returns the actual name of a gem drawer for a module.
     * @param moduleName the name of the module.
     * @return the corresponding gem drawer name.
     */
    static String getGemDrawerNameForModule(ModuleName moduleName) {
        return moduleName.toSourceText();
    }   
    
    /**
     * @param workspaceModuleNameResolver the module name resolver to use to generate an appropriate module name
     * @param moduleTreeDisplayMode 
     * @return the moduleAlphaComparator
     */
    private static Comparator<MetaModule> getModuleAlphaComparator(final ModuleNameResolver workspaceModuleNameResolver, final TreeViewDisplayMode moduleTreeDisplayMode) {
        
        return new Comparator<MetaModule>() {
            public int compare(final MetaModule m1, final MetaModule m2) {
                final String gemDrawerNameForModule1 = ModuleNameDisplayUtilities.getDisplayNameForModuleInTreeView(m1.getName(), workspaceModuleNameResolver, moduleTreeDisplayMode);
                final String gemDrawerNameForModule2 = ModuleNameDisplayUtilities.getDisplayNameForModuleInTreeView(m2.getName(), workspaceModuleNameResolver, moduleTreeDisplayMode);
                
                return gemDrawerNameForModule1.compareTo(gemDrawerNameForModule2);
            }
        };
    }

    /**
     * Fill an empty TreeNode structure with gems, using any previously-used policies.
     * Preconditions: subtree is empty, entities map to GemTreeNodes
     * @param rootNode the root of the subtree to fill
     * @param entityList the gems that should go into the subtree
     */
    private void fillTreeStructure(BrowserTreeNode rootNode, List<GemEntity> entityList) {

        // Remove all old children of this node
        if (rootNode instanceof GemDrawer) {
            removeAllChildrenExceptForVisibleDescendantDrawers(rootNode);            
        } else {
            rootNode.removeAllChildren();
        }

        // Apply any sorter associated with this node
        GemSorter sorter = rootNode.getSorter();
        if (sorter != null) {
            entityList = sorter.getSortedList(entityList);
        }
        
        // Apply any categorization associated with this node
        GemCategorizer<?> categorizer = rootNode.getCategorizer();
        if (categorizer != null) {

            List<GemCategory> categoryList = categorizer.formCategories(entityList);

            // Iterate over the categories
            int nCategories = categoryList.size();
            for (int i = 0; i < nCategories; i++) {
    
                GemCategory category = categoryList.get(i);
                
                // Special case handling for gem drawer categories. 
                // Don't show hidden modules if we're not supposed to.
                if (rootNode.getNodeProvider() instanceof MaterialGemDrawerCategoryNodeProvider && !showAllModules) {
                    
                    GemDrawerCategoryInfo pair = (GemDrawerCategoryInfo)(category.getCategoryKey()).getValue();
                    
                    if (!isVisibleModule(pair.getModuleName())) {
                        continue;
                    }
                }
                
                BrowserTreeNode categoryNode = rootNode.addNewCategoryNode(category);

                // Recurse into that node.
                fillTreeStructure(categoryNode, category.getCategoryItems());
            }

        } else {           
            // No categorizer.
            // This means this level isn't categorized - all entities go in here.

            for (final GemEntity gemEntity : entityList) {

                // display all gems, else only add the public gems 
                if (!showPublicGemsOnly || gemEntity.getScope().isPublic()){
                    rootNode.add(getTreeNode(gemEntity));
                }
            }
        }
    }

    /**
     * Removes all children except for those whose descendants include visible drawers.
     * @param rootNode the root of the tree to process.
     */
    private void removeAllChildrenExceptForVisibleDescendantDrawers(BrowserTreeNode rootNode) {
        List<GemDrawer> subDrawersToKeep = new ArrayList<GemDrawer>();
        rootNode.collectAllGemDrawerDescendants(subDrawersToKeep);
        
        for (Iterator<GemDrawer> it = subDrawersToKeep.iterator(); it.hasNext(); ) {
            if (!isDrawerInAbstractTree(it.next().getModuleName().toSourceText())) {
                it.remove();
            }
        }
        
        for (int i = rootNode.getChildCount()-1; i >= 0; i--) {
            final TreeNode child = rootNode.getChildAt(i);
            
            if (child instanceof GemDrawer) {
                
                // we do not remove the child if it, or a descendant of it, is a drawer that is supposed to be visible
                boolean shouldRemove = true;
                for (int j = 0, n = subDrawersToKeep.size(); j < n; j++) {
                    if (((BrowserTreeNode)child).isNodeDescendant(subDrawersToKeep.get(j))) {
                        shouldRemove = false;
                    }
                }
                
                if (shouldRemove) {
                    rootNode.remove(i);
                }
            } else {
                rootNode.remove(i);
            }
        }
    }
    
    /**
     * Clear all gem drawers from the workspace.
     */
    public void clearDrawers() {
        workspaceAbstractTreeNode.clearChildren();
    }

    /**
     * @return the perspective the model was populated with
     */    
    Perspective getPerspective() {
        return perspective;
    }

    /**
     * Refreshes with a new perspective.
     * @param perspective the new perspective
     */
    public void setPerspectiveAndReload(Perspective perspective) {
        this.perspective = perspective;
        reload();
    }

    /**
     * Returns whether the given drawer is in the abstract tree.
     * @param drawerName the name of the drawer.
     * @return true if the drawer is in the abstract tree (i.e. it will be displayed), false otherwise.
     */
    private boolean isDrawerInAbstractTree(final String drawerName) {
        return (workspaceAbstractTreeNode.getChild(drawerName) != null);
    }
}
