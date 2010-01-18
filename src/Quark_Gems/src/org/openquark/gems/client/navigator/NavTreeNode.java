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
 * NavTreeNode.java
 * Creation date: Jul 4, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.openquark.cal.compiler.ClassInstance;
import org.openquark.cal.compiler.ClassMethod;
import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Function;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.compiler.TypeClass;
import org.openquark.cal.compiler.TypeConstructor;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.MetaModule;
import org.openquark.gems.client.ModuleNameDisplayUtilities;
import org.openquark.gems.client.ModuleNameDisplayUtilities.TreeViewDisplayMode;


/**
 * This class implements an abstract base class for nodes in the CAL Navigator tree.
 * Nodes for specific entities are derived from this base class and included in this
 * file as package-private implementations.
 * 
 * @author Frank Worsley
 */
public abstract class NavTreeNode extends DefaultMutableTreeNode {
    
    /** An instance of a Comparator to sort NavTreeNodes. */
    public static final Comparator<NavTreeNode> NODE_SORTER = new NavTreeNodeSorter();

    /** The name of this node. */
    private final String name;
    
    /**
     * Constructs a new NavTreeNode with the specified name.
     * @param name the name of the node
     */
    public NavTreeNode(String name) {
        super(name);
        this.name = name;
    }

    /**
     * @return the name of this node.
     */
    public String getName() {
        return name;
    }
    
    /**     
     * @return name to use for sorting. This is different in the case of class instances,
     *      since the context is ignored (as well as any '(' around the type constructor). 
     */
    String getNameForSorting() {
        return getName();
    }

    /**
     * @return the tooltip text that should be displayed for this node.
     */
    public String getToolTipText() {
        return getName();
    }
    
    /**
     * Returns the address that identifies this node inside the CAL navigator. This
     * method may return null if the node is not uniquely identified by an address.
     * @return the address associated with this node
     * */
    public abstract NavAddress getAddress();
}

/**
 * This class implements a Comparator for sorting NavTreeNodes in alphabetical order by name.
 * @author Frank Worsley
 */
class NavTreeNodeSorter implements Comparator<NavTreeNode> {

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(NavTreeNode n1, NavTreeNode n2) {

        return n1.getNameForSorting().compareTo(n2.getNameForSorting());
    }
}

/**
 * This node represents the root of the NavTree. It is not actually user visible but
 * is uniquely identified by the root cal:// url. Clicking on a link to the root url
 * will bring up the home page of the CAL Navigator.
 * @author Frank Worsley
 */ 
class NavRootNode extends NavTreeNode {
    
    private static final long serialVersionUID = -8068893323869515862L;

    /**
     * Constructs a new NavRootNode.
     */
    public NavRootNode() {
        super ("Root");
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavTreeNode#getAddress()
     */
    @Override
    public NavAddress getAddress() {
        return NavAddress.getRootAddress(NavAddress.WORKSPACE_METHOD);
    }
}

/**
 * A NavVaultNode is a NavTreeNode that acts as a folder for other nodes. NavVaultNodes do
 * not represent entities within the CAL language but are used only to group other nodes together.
 * This class is declared abstract so that specific folder nodes can derive from it.
 * @author Frank Worsley
 */
abstract class NavVaultNode extends NavTreeNode {
    
    /**
     * Default constructor for a new NavVaultNode.
     * @param name the name of the vault
     */
    public NavVaultNode(String name) {
        super (name);
    }
}

/**
 * A NavModuleNode represents a module from CAL.
 * @author Frank Worsley
 */
class NavModuleNode extends NavTreeNode {
    
    private static final long serialVersionUID = 4696670665596743586L;
    
    /** The MetaModule this node represents. */
    private final MetaModule metaModule;
    
    /**
     * Constructor for a new NavModuleNode for the given module.
     * @param module the module the node is for
     * @param workspaceModuleNameResolver the module name resolver to use to generate an appropriate module name
     * @param treeViewDisplayMode the display mode for the tree.
     */
    public NavModuleNode(final MetaModule module, final ModuleNameResolver workspaceModuleNameResolver, final TreeViewDisplayMode treeViewDisplayMode) {
        super(ModuleNameDisplayUtilities.getDisplayNameForModuleInTreeView(module.getName(), workspaceModuleNameResolver, treeViewDisplayMode));
        this.metaModule = module;
    }
    
    /**
     * @return the MetaModule this node represents
     */
    public MetaModule getMetaModule() {
        return metaModule;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText() {
        return metaModule.getName().toSourceText(); // always show the fully qualified module name for the tooltip
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NavAddress getAddress() {
        return NavAddress.getAddress(metaModule);
    }
}

/**
 * A node representing an intermediate node corresponding to a module name for which
 * there is no actual module.
 *
 * @author Joseph Wong
 */
class NavModuleNamespaceNode extends NavTreeNode {
    
    private static final long serialVersionUID = 4569101570100280782L;
    
    /** The module name. */
    private final ModuleName moduleName;
    
    NavModuleNamespaceNode(ModuleName moduleName) {
        super(moduleName.getLastComponent());
        this.moduleName = moduleName;
    }
    
    /**
     * @return the module name.
     */
    ModuleName getModuleName() {
        return moduleName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText() {
        return moduleName.toSourceText(); // always show the fully qualified module name for the tooltip
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public NavAddress getAddress() {
        return NavAddress.getModuleNamespaceAddress(moduleName);
    }
}

/**
 * A NavEntityNode represents an entity within the CAL language. This class is abstract and
 * a subclass exists for each of the entities within CAL.
 * @author Frank Worsley
 */
abstract class NavEntityNode extends NavTreeNode {
    
    /** The ScopedEntity that this node represents. */
    private final ScopedEntity entity;
    
    /**
     * Constructor for a NavEntityNode for the given entity.
     * @param entity the entity this node is for
     */
    public NavEntityNode(ScopedEntity entity) {
        super(entity.getName().getUnqualifiedName());
        this.entity = entity;
    }
    
    /**
     * @return the entity that this tree node represents.
     */
    public ScopedEntity getEntity() {
        return entity;
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavTreeNode#getAddress()
     */
    @Override
    public NavAddress getAddress() {
        return NavAddress.getAddress(entity);
    }
}

/**
 * This class represents CAL Data Constructor.
 * @author Frank Worsley
 */
class NavDataConstructorNode extends NavEntityNode {
    
    private static final long serialVersionUID = 4806634151726741778L;

    /**
     * Constructs a new NavDataConstructorNode for the given data constructor.
     * @param entity the data constructor this node is for
     */
    public NavDataConstructorNode(DataConstructor entity) {
        super(entity);
    }
}

/**
 * This class represents a vault of CAL functions.
 * @author Frank Worsley
 */
class NavFunctionVaultNode extends NavVaultNode {
        
    private static final long serialVersionUID = 7300179606548836772L;

    /**
     * Constructs a new NavFunctionVaultNode.
     */
    public NavFunctionVaultNode() {
        super ("Functions");
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavTreeNode#getAddress()
     */
    @Override
    public NavAddress getAddress() {
        
        NavTreeNode parent = (NavTreeNode) getParent();
        if (parent != null) {
            return parent.getAddress().withParameter(NavAddress.VAULT_PARAMETER, NavAddress.FUNCTION_VAULT_VALUE);            
        }
        
        return null;
    }
}

/**
 * This class represents a CAL Function.
 * @author Frank Worsley
 */
class NavFunctionNode extends NavEntityNode {
    
    private static final long serialVersionUID = -5750159772815321971L;

    /**
     * Constructs a new NavFunctionNode for the given Function.
     * @param entity the entity this node is for
     */
    public NavFunctionNode(Function entity) {
        super(entity);
    }
}

/**
 * This class represents a CAL Class Method.
 * @author Frank Worsley
 */
class NavClassMethodNode extends NavEntityNode {

    private static final long serialVersionUID = 6000247838676352909L;

    /**
     * Constructs a new NavClassMethodNode for the given ClassMethod.
     * @param entity the class method this node is for
     */
    public NavClassMethodNode(ClassMethod entity) {
        super(entity);
    }
}

/**
 * This class represents a vault of CAL Type Constructors.
 * @author Frank Worsley
 */
class NavTypeConstructorVaultNode extends NavVaultNode {
        
    private static final long serialVersionUID = 5787097081667770479L;

    /**
     * Constructs a new NavTypeConstructorVaultNode.
     */
    public NavTypeConstructorVaultNode() {
        super ("Types");
    }
    
    /**
     * @see org.openquark.gems.client.navigator.NavTreeNode#getAddress()
     */
    @Override
    public NavAddress getAddress() {
        
        NavTreeNode parent = (NavTreeNode) getParent();
        if (parent != null) {
            return parent.getAddress().withParameter(NavAddress.VAULT_PARAMETER, NavAddress.TYPE_VAULT_VALUE);            
        }
        
        return null;
    }
}

/**
 * This class represents a CAL Type Constructor.
 * @author Frank Worsley
 */
class NavTypeConstructorNode extends NavEntityNode {

    private static final long serialVersionUID = -252758399652125528L;

    /**
     * Constructs a new NavTypeConstructorNode for the given type constructor.
     * @param entity the type constructor this node is for.
     */
    public NavTypeConstructorNode(TypeConstructor entity) {
        super(entity);
    }
}

/**
 * This class represents a vault of CAL Type Classes.
 * @author Frank Worsley
 */
class NavTypeClassVaultNode extends NavVaultNode {
        
    private static final long serialVersionUID = 8470650821167857131L;

    /**
     * Constructs a new NavTypeClassVaultNode.
     */
    public NavTypeClassVaultNode() {
        super ("Classes");
    }

    /**
     * @see org.openquark.gems.client.navigator.NavTreeNode#getAddress()
     */
    @Override
    public NavAddress getAddress() {
        
        NavTreeNode parent = (NavTreeNode) getParent();
        if (parent != null) {
            return parent.getAddress().withParameter(NavAddress.VAULT_PARAMETER, NavAddress.CLASS_VAULT_VALUE);
        }
        
        return null;
    }   
}

/**
 * This class represents a CAL Type Class.
 * @author Frank Worsley
 */
class NavTypeClassNode extends NavEntityNode {

    private static final long serialVersionUID = -4698970379876431538L;

    /**
     * Constructs a new NavTypeClassNode for the given type class.
     * @param entity the type class this node is for.
     */
    public NavTypeClassNode(TypeClass entity) {
        super(entity);
    }
}

/**
 * This class represents a vault of CAL Class Instances.
 * @author Frank Worsley
 */
class NavClassInstanceVaultNode extends NavVaultNode {

    private static final long serialVersionUID = 8821191940341989496L;

    /**
     * Constructs a new NavClassInstanceVaultNode.
     */
    public NavClassInstanceVaultNode() {
        super ("Instances");
    }

    /**
     * @see org.openquark.gems.client.navigator.NavTreeNode#getAddress()
     */
    @Override
    public NavAddress getAddress() {
        
        NavTreeNode parent = (NavTreeNode) getParent();
        if (parent != null) {
            return parent.getAddress().withParameter(NavAddress.VAULT_PARAMETER, NavAddress.INSTANCE_VAULT_VALUE);            
        }
        
        return null;
    }
}

/**
 * This class represents a CAL class instance. Note that class instances are not scoped entities.
 * Therefore this class does not derive from NavEntityNode.
 * @author Frank Worsley
 */
class NavClassInstanceNode extends NavTreeNode {

    private static final long serialVersionUID = -7754991090768815159L;

    private final ClassInstance classInstance;
    
    /**
     * the instance name for use when sorting. Does not include the context, nor the first ( around the type constructor if any
     * so that Outputable Double sorts before Outputable (Either a b)
     */
    private final String nameForSorting;

    /**
     * Constructs a new NavClassInstanceNode for the given class instance.
     * @param classInstance the class instance this node is for.
     * @param moduleTypeInfo the type info of the module the instance is declared in
     */
    public NavClassInstanceNode(ClassInstance classInstance, ModuleTypeInfo moduleTypeInfo) {
        super(classInstance.getNameWithContext(new UnqualifiedUnlessAmbiguous(moduleTypeInfo)));
        this.classInstance = classInstance;
        this.nameForSorting = classInstance.getName().replaceFirst("\\(", "");
    }
    
    @Override
    String getNameForSorting() {
        return nameForSorting;
    }
    
    @Override
    public NavAddress getAddress() {
        return NavAddress.getAddress(classInstance);
    }
}

/**
 * This class represents a CAL Instance Method. Note that instance methods are not scoped entities.
 * Therefore this class does not derive from NavEntityNode.
 * @author Joseph Wong
 */
class NavInstanceMethodNode extends NavTreeNode {

    private static final long serialVersionUID = 2374212794741763685L;
    
    private final ClassInstance classInstance;
    private final String methodName;

    /**
     * Constructs a new NavInstanceMethodNode for the given class instance.
     * @param classInstance the class instance.
     * @param methodName the name of the instance method.
     * @param moduleTypeInfo the type info of the module the instance is declared in
     */
    public NavInstanceMethodNode(ClassInstance classInstance, String methodName, ModuleTypeInfo moduleTypeInfo) {
        super(methodName);
        this.classInstance = classInstance;
        this.methodName = methodName;
    }
    
    @Override
    public NavAddress getAddress() {
        return NavAddress.getAddress(CALFeatureName.getInstanceMethodFeatureName(classInstance, methodName));
    }
}

