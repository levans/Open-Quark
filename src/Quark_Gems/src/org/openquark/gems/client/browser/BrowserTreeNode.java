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
 * BrowserTreeNode.java
 * Creation date: Dec 19th 2002
 * By: Ken Wong
 */

package org.openquark.gems.client.browser;

import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.GemComparatorSorter;
import org.openquark.gems.client.browser.BrowserTreeModel.CategoryNodeProvider;
import org.openquark.gems.client.browser.GemCategory.CategoryKey;
import org.openquark.util.Pair;
import org.openquark.util.UnsafeCast;


/**
 * Specialized version of DefaultMutableTreeNode.
 * Adds a getDisplayedString method that is used by the BrowserTreeCellRenderer to get the
 * displayable string for a node. By default returns toString but can be overridden to
 * return more complex information.
 * This class also adds support for storing node sorting and categorization information.
 * @author Ken Wong
 * Creation Date: Dec 19th 2002
 */
public abstract class BrowserTreeNode extends DefaultMutableTreeNode {
    /** The categorizer associated with this node. */
    private GemCategorizer<?> categorizer;
    
    /** The category node provider for the categorizer that is associated with this node. */
    private CategoryNodeProvider provider;
    
    /** The sorter that is associated with this node. */
    private GemComparatorSorter sorter;
    
    /** A map of existing category children nodes. */
    private final Map<CategoryKey<?>, BrowserTreeNode> categoryToNodeMap = new HashMap<CategoryKey<?>, BrowserTreeNode>();
    
    /**
     * An object which can store mappings for a node's children and GemDrawer descendants, from which they can later be retrieved by displayed name/module name.
     * @author Edward Lam
     */
    static class NodeReuseInfo {
        
        /** map from displayed name to the node with that name, for all of the node's children. */
        private final Map<Pair<String, Class<? extends BrowserTreeNode>>, BrowserTreeNode> displayedStringToChildNodeReuseMap = new HashMap<Pair<String, Class<? extends BrowserTreeNode>>, BrowserTreeNode>();

        /** map from module name to a descendent GemDrawer. */
        private final Map<ModuleName, GemDrawer> moduleNameToDescendantGemDrawerReuseMap = new HashMap<ModuleName, GemDrawer>();
        
        /**
         * Constructor for a NodeReuseInfo.
         * @param selectedNode the node for which this info will be constructed.
         */
        private NodeReuseInfo(BrowserTreeNode selectedNode) {
            populateDisplayedStringToChildNodeReuseMap(selectedNode);
            populateModuleNameToDescendantGemDrawerReuseMap(selectedNode);
        }

        /**
         * Populate the displayedStringToChildNodeReuseMap
         * @param selectedNode
         */
        private void populateDisplayedStringToChildNodeReuseMap(BrowserTreeNode selectedNode) {
            for (int i = 0, nChildren = selectedNode.getChildCount(); i < nChildren; i++) {
                BrowserTreeNode nextNode = (BrowserTreeNode)selectedNode.getChildAt(i);
                displayedStringToChildNodeReuseMap.put(new Pair<String, Class<? extends BrowserTreeNode>>(nextNode.getDisplayedString(), nextNode.getClass()), nextNode);
            }
        }
        
        /**
         * Populate the moduleNameToDescendantGemDrawerReuseMap
         * @param selectedNode
         */
        private void populateModuleNameToDescendantGemDrawerReuseMap(BrowserTreeNode selectedNode) {
            for (int i = 0, nChildren = selectedNode.getChildCount(); i < nChildren; i++) {
                BrowserTreeNode nextNode = (BrowserTreeNode)selectedNode.getChildAt(i);
                
                if (nextNode instanceof GemDrawer) {
                    moduleNameToDescendantGemDrawerReuseMap.put(((GemDrawer)nextNode).getModuleName(), (GemDrawer)nextNode);
                }
                
                populateModuleNameToDescendantGemDrawerReuseMap(nextNode);
            }
        }
        
        /**
         * Retrieves a cached node from the displayedStringToChildNodeReuseMap map.
         * @param displayedString the displayed string of the node.
         * @param nodeClass the class of the node.
         * @return the cached node, or null if one does not exist.
         */
        BrowserTreeNode getChildNodeFromDisplayedString(String displayedString, Class<GemCategoryNode> nodeClass) {
            return displayedStringToChildNodeReuseMap.get(new Pair<String, Class<GemCategoryNode>>(displayedString, nodeClass));
        }
        
        /**
         * Retrieves a cached node from the moduleNameToDescendantGemDrawerReuseMap map.
         * @param moduleName the module name corresponding to the node.
         * @return the cached node, or null if one does not exist.
         */
        GemDrawer getDescendantGemDrawer(ModuleName moduleName) {
            return moduleNameToDescendantGemDrawerReuseMap.get(moduleName);
        }
    }
    
    /**
     * Generic constructor
     */
    public BrowserTreeNode() {
        super();
    }
    
    /**
     * Generic constructor
     * @param userObject
     */
    public BrowserTreeNode(Object userObject) {
        super(userObject);
    }
    
    /**
     * Generic constructor
     * @param userObject
     * @param allowChildren
     */
    public BrowserTreeNode(Object userObject, boolean allowChildren) {
        super(userObject, allowChildren);
    }
    
    /**
     * Returns the string that will be used by the cell renderer to label the tree node.
     * The default implementation simply returns toString, subclasses should override this
     * if they wish to return more complex information.
     * @return String
     */
    public String getDisplayedString() {
        return toString();
    }

    /**
     * Associates the given sorter with this tree node.
     * @param sorter the sorter to associated
     */
    public void setSorter(GemComparatorSorter sorter) {
        this.sorter = sorter;
        
        // If we are sorted, we can't be categorized.
        categorizer = null;
        provider = null;
        categoryToNodeMap.clear();
    }
    
    /**
     * Associates the given categorizer and node provider with this tree node.
     * @param categorizer the categorizer to associated
     * @param provider the node provider for the categorizer
     */
    public void setCategorizer(GemCategorizer<?> categorizer, CategoryNodeProvider provider) {
        this.categorizer = categorizer;
        this.provider = provider;
        categoryToNodeMap.clear();
        
        // If we are categorized, we can't be sorted.
        sorter = null;
    }
    
    /**
     * Returns the sorter associated with this node.
     * @return GemComparatorSorter
     */
    public GemComparatorSorter getSorter() {
        return sorter;
    }
    
    /**
     * Returns the categorizer associated with this node.
     * @return GemCategorizer
     */
    public GemCategorizer<?> getCategorizer() {
        return categorizer;
    }
    
    /**
     * Returns the node provider associated with this node.
     * @return CategoryNodeProvider
     */
    public CategoryNodeProvider getNodeProvider() {
        return provider;
    }
    
    /**
     * Adds the tree node to use for representing a sub-category of this node.
     * This method will check if the tree node has been used before and add it
     * from the categoryToNodeMap if that is the case. Otherwise a new node will be
     * obtained from the node provider and stored in the map for future reuse.
     * We store the nodes in the map so that any categorization info associated with
     * the sub-category nodes is not lost if this node is re-categorized.
     * @param category the category for which to return a tree node
     * @return the tree node to use for the given category, which has been added to this node as a child.
     */
    public BrowserTreeNode addNewCategoryNode(GemCategory category) {
        return addNewCategoryNode(category, null);
    }
    
    /**
     * Adds the tree node to use for representing a sub-category of this node.
     * This method will check if the tree node has been used before and add it
     * from the categoryToNodeMap if that is the case. Otherwise a new node will be
     * obtained from the node provider and stored in the map for future reuse.
     * We store the nodes in the map so that any categorization info associated with
     * the sub-category nodes is not lost if this node is re-categorized.
     * 
     * @param category the category for which to return a tree node
     * @param nodeReuseInfo the node reuse info for this node, from which category nodes of the save name will be fetched and re-used.
     *   If null, all category nodes will be newly-created.
     * @return the tree node to use for the given category, which has been added to this node as a child.
     */
    BrowserTreeNode addNewCategoryNode(GemCategory category, NodeReuseInfo nodeReuseInfo) {
        
        BrowserTreeNode categoryNode = categoryToNodeMap.get(category.getCategoryKey());
        
        if (categoryNode == null) {
            categoryNode = provider.addCategoryNodeToParent(this, category, nodeReuseInfo);
        } else {
            if (!this.isNodeChild(categoryNode)) {
                this.add(categoryNode);
            }
        }
        
        return categoryNode;
    }
    
    /**
     * Adds the given category node, or a cached version of it if one exists.
     * @param categoryNode the category node to be added.
     * @param category the category for the node.
     * @return the actual node that now exists as a child - either the given category node, or a cached version of it.
     */
    BrowserTreeNode addCategoryNode(final BrowserTreeNode categoryNode, final GemCategory category) {

        final BrowserTreeNode categoryNodeFromMap = categoryToNodeMap.get(category.getCategoryKey());
        
        if (categoryNodeFromMap != null) {
            
            if (!this.isNodeChild(categoryNodeFromMap)) {
                this.add(categoryNodeFromMap);
            }
            return categoryNodeFromMap;
            
        } else {
            categoryToNodeMap.put(category.getCategoryKey(), categoryNode);
            
            if (!this.isNodeChild(categoryNode)) {
                this.add(categoryNode);
            }
            return categoryNode;
        }
    }

    /**
     * @return ChildNameInfo for this node.
     */
    NodeReuseInfo getChildNameInfo() {
        return new NodeReuseInfo(this);
    }
    
    /**
     * Removes each leaf node that descends from this node from its parent.  
     */
    void removeAllGemNodeDescendants() {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            BrowserTreeNode currentNode = (BrowserTreeNode)getChildAt(i);

            if (currentNode instanceof GemTreeNode) {
                currentNode.removeFromParent();
            } else {
                currentNode.removeAllGemNodeDescendants();
            }
        }
    }
    
    /**
     * Removes each leaf node that descends from this node (but still within the scope of the same drawer) from its parent.  
     */
    void removeAllGemNodeDescendantsInGivenDrawerOnly(GemDrawer drawerNode) {
        if (this instanceof GemDrawer && this != drawerNode) {
            return;
        }
        
        for (int i = getChildCount() - 1; i >= 0; i--) {
            BrowserTreeNode currentNode = (BrowserTreeNode)getChildAt(i);

            if (currentNode instanceof GemTreeNode) {
                currentNode.removeFromParent();
            } else {
                currentNode.removeAllGemNodeDescendantsInGivenDrawerOnly(drawerNode);
            }
        }
    }
    
    /**
     * Collects all GemDrawer descendants into the given list.
     * @param drawerList the list for collecting the GemDrawer descendants.
     */
    void collectAllGemDrawerDescendants(final List<GemDrawer> drawerList) {
        
        for (int i = getChildCount() - 1; i >= 0; i--) {
            BrowserTreeNode currentNode = (BrowserTreeNode)getChildAt(i);

            if (currentNode instanceof GemDrawer) {
                drawerList.add((GemDrawer)currentNode);
            }
            
            currentNode.collectAllGemDrawerDescendants(drawerList);
        }
    }
    
    /**
     * Returns the string that will be used by the browser tree to display the tooltip
     * for this tree node. Subclasses that want to return a custom tooltip need to override this.
     * By default it returns the name of the node and information on the nodes in the hierachy
     * below this one.
     * @return String
     */
    public String getToolTipText() {

        StringBuilder toolTipText = new StringBuilder("<html><b>" + getDisplayedString() + "</b><br>");
        toolTipText.append(getSecondaryToolTipText());        
        toolTipText.append("</html>");

        return toolTipText.toString();
    }
    
    /**
     * Returns information about the node hierarchy (ie: number of gems and categories contained), 
     * which is displayed in non-bold form in the tooltip.
     * @return String
     */
    public String getSecondaryToolTipText() {
        int numberOfGems = 0;
        int numberOfCategories = 0;
        
        Enumeration<DefaultMutableTreeNode> children = UnsafeCast.unsafeCast(this.breadthFirstEnumeration());
        
        // This node is returned first, so skip over it.
        children.nextElement();
        
        while (children.hasMoreElements()) {
            BrowserTreeNode child = (BrowserTreeNode) children.nextElement();
            
            if (child instanceof GemTreeNode && child.isLeaf()) {
                // collect information on the gems in this category
                numberOfGems++;                
            } else if (child instanceof GemCategoryNode ||
                       child instanceof GemDrawer) {
                // keep track of the number of subcategories
                numberOfCategories++;
            }
        }
        
        // format our message from the resources
        Object[] arguments = { Integer.valueOf(numberOfGems), Integer.valueOf(numberOfCategories) };
        
        double[] limits = {0, 1, 2};
        String[] gemStrings = { BrowserMessages.getString("GB_NoGems"),
                                BrowserMessages.getString("GB_OneGem"),
                                BrowserMessages.getString("GB_ManyGems") };
        String[] catStrings = { BrowserMessages.getString("GB_NoCategories"),
                                BrowserMessages.getString("GB_OneCategory"),
                                BrowserMessages.getString("GB_ManyCategories") };

        ChoiceFormat gemChoice = new ChoiceFormat(limits, gemStrings);
        ChoiceFormat catChoice = new ChoiceFormat(limits, catStrings);
        ChoiceFormat[] formats = { gemChoice, catChoice };
        
        MessageFormat message = new MessageFormat(BrowserMessages.getString("GB_NodeToolTip"));
        message.setFormats(formats);
        return message.format(arguments);
    }
}
