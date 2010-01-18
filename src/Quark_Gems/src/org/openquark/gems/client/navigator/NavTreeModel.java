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
 * NavTreeModel.java
 * Creation date: Jul 3, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.FunctionalAgent;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.ModuleNameDisplayUtilities.TreeViewDisplayMode;


/**
 * This class implements a TreeModel for the NavTree.
 * It takes care of loading modules and CAL entities from the perspective and categorizing them in the tree.
 * 
 * @author Frank Worsley
 */
public class NavTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = -1302161883211389665L;

    /** The root node of the model. */
    private final NavTreeNode rootNode;

    /** The map that maps the location part of cal:// urls to their respective nodes. */
    private final Map<NavAddress, NavTreeNode> urlToNodeMap = new HashMap<NavAddress, NavTreeNode>();
    
    /**
     * Constructor for a new NavTreeModel.
     */
    public NavTreeModel() {
        super(new NavRootNode());
        
        this.rootNode = getRoot();
        urlToNodeMap.put(rootNode.getAddress(), rootNode);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NavRootNode getRoot() {
        return (NavRootNode)super.getRoot();
    }
    
    /**
     * This method loads CAL entities from the perspective and categorizes
     * them in the model.
     * @param treeViewDisplayMode the display mode for the tree.
     */
    public void load(Perspective perspective, TreeViewDisplayMode treeViewDisplayMode) {
        
        rootNode.removeAllChildren();
        
        List<MetaModule> modules = new ArrayList<MetaModule>(perspective.getVisibleMetaModules());
        modules.addAll(perspective.getInvisibleMetaModules());
        
        Set<ModuleName> moduleNames = new HashSet<ModuleName>();
        for (final MetaModule metaModule : modules) {
            moduleNames.add(metaModule.getName());
        }
        
        ModuleNameResolver workspaceModuleNameResolver = ModuleNameResolver.make(moduleNames);
        
        List<NavModuleNode> moduleNodes = new ArrayList<NavModuleNode>(modules.size());
        
        for (final MetaModule metaModule : modules) {
            NavModuleNode moduleNode = makeModuleNode(metaModule, workspaceModuleNameResolver, treeViewDisplayMode);
            moduleNodes.add(moduleNode);
        }
        
        buildModuleTree(moduleNodes, treeViewDisplayMode);
    }
    
    /**
     * Builds the tree of module nodes.
     * @param moduleNodes a list of NavModuleNodes.
     * @param treeViewDisplayMode the display mode for the tree.
     */
    private void buildModuleTree(final List<NavModuleNode> moduleNodes, final TreeViewDisplayMode treeViewDisplayMode) {
        
        if (treeViewDisplayMode != TreeViewDisplayMode.HIERARCHICAL) {
            addAllNodes(rootNode, moduleNodes, true);
            
        } else {
            // We are using a SortedMap so that a module namespace node "A.B" is added ahead of the module with the same name "A.B" 
            final SortedMap<ModuleName, NavTreeNode> nodeMap = new TreeMap<ModuleName, NavTreeNode>();

            // first we process all the existing module nodes
            final int nModules = moduleNodes.size();
            for (int i = 0; i < nModules; i++) {
                NavModuleNode moduleNode = moduleNodes.get(i);
                nodeMap.put(moduleNode.getMetaModule().getName(), moduleNode);
            }
            
            // now we figure out what intermediate namespace nodes are required
            final Map<ModuleName, NavModuleNamespaceNode> namespaceNodeMap = new HashMap<ModuleName, NavModuleNamespaceNode>();
            
            // a map from a module name (non-null) to a list of the children nodes
            final Map<ModuleName, List<NavTreeNode>> nodeChildrenMap = new HashMap<ModuleName, List<NavTreeNode>>();
            
            // a list of the root's children
            final LinkedHashSet<NavTreeNode> rootChildrenList = new LinkedHashSet<NavTreeNode>();
            
            for (final Map.Entry<ModuleName, NavTreeNode> entry : nodeMap.entrySet()) {
                final ModuleName moduleName = entry.getKey();
                final NavTreeNode moduleNode = entry.getValue();

                helpProcessParentChild(namespaceNodeMap, nodeChildrenMap, rootChildrenList, moduleName.getImmediatePrefix(), moduleNode);
            }
            
            // finally, build the tree
            for (final Map.Entry<ModuleName, List<NavTreeNode>> entry : nodeChildrenMap.entrySet()) {
                final ModuleName parentModuleName = entry.getKey();
                final List<NavTreeNode> children = entry.getValue();
                
                final NavTreeNode parentNode = namespaceNodeMap.get(parentModuleName);
                addAllNodes(parentNode, children, true);
            }
            
            // we deal with the root's children separately
            addAllNodes(rootNode, new ArrayList<NavTreeNode>(rootChildrenList), true);
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                nodeStructureChanged(rootNode);
            }
        });
    }

    /**
     * Helper method for processing a pair of parent/child nodes in a hierarchically displayed tree.
     * @param namespaceNodeMap the map containing module namespace nodes (to be added to).
     * @param nodeChildrenMap the map from a module name (non-null) to a list of the children nodes (to be added to).
     * @param rootChildrenList a list of the root's children  (to be added to).
     * @param parentModuleName the module name corresponding to the parent node.
     * @param child the child node.
     */
    private void helpProcessParentChild(
        final Map<ModuleName, NavModuleNamespaceNode> namespaceNodeMap,
        final Map<ModuleName, List<NavTreeNode>> nodeChildrenMap,
        final LinkedHashSet<NavTreeNode> rootChildrenList,
        final ModuleName parentModuleName,
        final NavTreeNode child) {
        
        
        final Collection<NavTreeNode> childrenListForPrefix;
        if (parentModuleName == null) {
            childrenListForPrefix = rootChildrenList;
            
        } else {
            final List<NavTreeNode> childrenListForPrefixFromMap = nodeChildrenMap.get(parentModuleName);
            
            if (childrenListForPrefixFromMap == null) {
                final List<NavTreeNode> newChildrenListForPrefix = new ArrayList<NavTreeNode>();
                childrenListForPrefix = newChildrenListForPrefix;
                nodeChildrenMap.put(parentModuleName, newChildrenListForPrefix);
                
            } else {
                childrenListForPrefix = childrenListForPrefixFromMap;
            }
        }

        childrenListForPrefix.add(child);

        if (parentModuleName != null) {
            if (namespaceNodeMap.containsKey(parentModuleName)) {
                helpProcessParentChild(namespaceNodeMap, nodeChildrenMap, rootChildrenList, parentModuleName.getImmediatePrefix(), namespaceNodeMap.get(parentModuleName));

            } else {
                // if there is no module namespace node for the given prefix, then we
                // need a new module namespace node

                NavModuleNamespaceNode namespaceNode = new NavModuleNamespaceNode(parentModuleName);
                namespaceNodeMap.put(parentModuleName, namespaceNode);

                helpProcessParentChild(namespaceNodeMap, nodeChildrenMap, rootChildrenList, parentModuleName.getImmediatePrefix(), namespaceNode);
            }
        }
    }
    
    /**
     * This method loads all entities from a CAL module and adds tree nodes for them.
     * @param metaModule the module to load entities for.
     * @param workspaceModuleNameResolver the module name resolver to use to generate an appropriate module name
     * @param treeViewDisplayMode the display mode for the tree.
     * @return a node for the module.
     */
    private NavModuleNode makeModuleNode(final MetaModule metaModule, final ModuleNameResolver workspaceModuleNameResolver, TreeViewDisplayMode treeViewDisplayMode) {
        ModuleTypeInfo moduleInfo = metaModule.getTypeInfo();

        NavModuleNode moduleNode = new NavModuleNode(metaModule, workspaceModuleNameResolver, treeViewDisplayMode);
        urlToNodeMap.put(moduleNode.getAddress(), moduleNode);

        // Load all functions.
        FunctionalAgent[] entities = moduleInfo.getFunctionalAgents();
        if (entities.length > 0) {

            NavVaultNode functionsNode = new NavFunctionVaultNode();
            List<NavFunctionNode> functionNodes = new ArrayList<NavFunctionNode>();
                        
            for (final FunctionalAgent entity : entities) {
                if (entity instanceof Function) {
                    functionNodes.add(new NavFunctionNode((Function) entity));
                }
            }

            if (functionNodes.size() > 0) {
                moduleNode.add(functionsNode);
                urlToNodeMap.put(functionsNode.getAddress(), functionsNode);
                addAllNodes(functionsNode, functionNodes, false);
            }
        }
        
        // Load all the type constructors.
        int constructorCount = moduleInfo.getNTypeConstructors();
        if (constructorCount > 0) {

            List<NavTreeNode> typeConstructorNodes = new ArrayList<NavTreeNode>(constructorCount);
            for (int i = 0; i < constructorCount; i++) {
                
                TypeConstructor typeConstructor = moduleInfo.getNthTypeConstructor(i);
                NavTreeNode typeConstructorNode = new NavTypeConstructorNode(typeConstructor); 
                typeConstructorNodes.add(typeConstructorNode);
                
                // Load the data constructors.
                int dataConstructorCount = typeConstructor.getNDataConstructors();
                for (int n = 0; n < dataConstructorCount; n++) {
                    NavTreeNode dataConstructorNode = new NavDataConstructorNode(typeConstructor.getNthDataConstructor(n));
                    typeConstructorNode.add(dataConstructorNode);
                    urlToNodeMap.put(dataConstructorNode.getAddress(), dataConstructorNode);
                }
            }
            
            NavVaultNode typeConstructorsNode = new NavTypeConstructorVaultNode();
            moduleNode.add(typeConstructorsNode);
            urlToNodeMap.put(typeConstructorsNode.getAddress(), typeConstructorsNode);
            addAllNodes(typeConstructorsNode, typeConstructorNodes, false);
        }

        // Load all the type classes.
        int classCount = moduleInfo.getNTypeClasses();
        if (classCount > 0) {

            List<NavTreeNode> classNodes = new ArrayList<NavTreeNode>(classCount);
            
            for (int i = 0; i < classCount; i++) {
    
                TypeClass typeClass = moduleInfo.getNthTypeClass(i);
    
                NavTreeNode typeClassNode = new NavTypeClassNode(typeClass);
                classNodes.add(typeClassNode);
    
                // Load the class methods.
                int methodCount = typeClass.getNClassMethods();
                if (methodCount > 0) {
    
                    List<NavClassMethodNode> classMethodNodes = new ArrayList<NavClassMethodNode>(methodCount);                
                    for (int n = 0; n < methodCount; n++) {
                        classMethodNodes.add(new NavClassMethodNode(typeClass.getNthClassMethod(n)));
                    }

                    addAllNodes(typeClassNode, classMethodNodes, false);
                }
            }

            NavVaultNode classesNode = new NavTypeClassVaultNode();
            moduleNode.add(classesNode);
            urlToNodeMap.put(classesNode.getAddress(), classesNode);
            addAllNodes(classesNode, classNodes, false);
        }

        // Load all the class instances
        int instanceCount = moduleInfo.getNClassInstances();
        if (instanceCount > 0) {

            List<NavTreeNode> classInstanceNodes = new ArrayList<NavTreeNode>(instanceCount);
            for (int n = 0; n < instanceCount; n++) {
                
                ClassInstance instance = moduleInfo.getNthClassInstance(n);
                
                NavTreeNode classInstanceNode = new NavClassInstanceNode(moduleInfo.getNthClassInstance(n), moduleInfo);
                classInstanceNodes.add(classInstanceNode);
                
                // Load the instance methods.
                int methodCount = instance.getNInstanceMethods();
                if (methodCount > 0) {
    
                    List<NavInstanceMethodNode> instanceMethodNodes = new ArrayList<NavInstanceMethodNode>(methodCount);                
                    for (int k = 0; k < methodCount; k++) {
                        String methodName = instance.getTypeClass().getNthClassMethod(k).getName().getUnqualifiedName();
                        instanceMethodNodes.add(new NavInstanceMethodNode(moduleInfo.getNthClassInstance(n), methodName, moduleInfo));
                    }

                    addAllNodes(classInstanceNode, instanceMethodNodes, false);
                }
            }
            
            NavVaultNode classInstancesNode = new NavClassInstanceVaultNode();
            moduleNode.add(classInstancesNode);
            urlToNodeMap.put(classInstancesNode.getAddress(), classInstancesNode);
            addAllNodes(classInstancesNode, classInstanceNodes, false);
        }
        return moduleNode;
    }

    /**
     * This method adds all nodes in the given list to the given parent node.
     * The nodes in the list are sorted before adding them.
     * @param parent the parent node to add nodes to
     * @param nodes the List of nodes to add
     * @param addToFront whether the new nodes are to be added ahead of the existing children.
     */
    private void addAllNodes(final NavTreeNode parent, final List<? extends NavTreeNode> nodes, final boolean addToFront) {
        
        Collections.sort(nodes, NavTreeNode.NODE_SORTER);
        
        if (addToFront) {
            final int nNodes = nodes.size();
            for (int i = nNodes - 1; i >= 0 ; i--) {
                NavTreeNode node = nodes.get(i);
                parent.insert(node, 0);
                urlToNodeMap.put(node.getAddress(), node);
            }
        } else {
            for (final NavTreeNode node : nodes) {
                parent.add(node);
                urlToNodeMap.put(node.getAddress(), node);
            }
        }
    }
    
    /**
     * @param url the url to find get the node for
     * @return the NavTreeNode that is associated with the specified url or null if there is no such node
     */
    public NavTreeNode getNodeForAddress(NavAddress url) {
        return urlToNodeMap.get(url.withAnchor(null));
    }
}