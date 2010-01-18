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
 * MaterialGemDrawer.java
 * Creation date: (10/25/00 8:44:19 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.browser;

import javax.swing.tree.MutableTreeNode;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ModuleNameResolver;
import org.openquark.gems.client.ModuleNameDisplayUtilities;
import org.openquark.gems.client.ModuleNameDisplayUtilities.TreeViewDisplayMode;


/**
 * A Vault Drawer (tree node) which represents material (compiled) modules and entities.
 * Creation date: (10/25/00 8:44:19 AM)
 * @author Luke Evans
 */
class MaterialGemDrawer extends GemDrawer {
    
    private static final long serialVersionUID = -8442272696944239342L;

    /** Whether this node is a namespace node - i.e. it does not contain children corresponding to entities. */
    private final boolean isNamespaceNode;

    /**
     * The displayed name of the drawer.
     */
    private final String displayedString;
    
    /**
     * Construct a MaterialGemDrawer from a name.
     * Creation date: (10/25/00 8:54:58 AM)
     * @param workspaceModuleNameResolver the module name resolver to use to generate an appropriate module name
     * @param moduleTreeDisplayMode the display mode of the module tree.
     * @param isNamespaceNode whether this node is a namespace node.
     */
    public MaterialGemDrawer(final ModuleName nodeName, final ModuleNameResolver workspaceModuleNameResolver, final TreeViewDisplayMode moduleTreeDisplayMode, final boolean isNamespaceNode) {
        super(nodeName);
        this.displayedString = ModuleNameDisplayUtilities.getDisplayNameForModuleInTreeView(nodeName, workspaceModuleNameResolver, moduleTreeDisplayMode);
        this.isNamespaceNode = isNamespaceNode;
    }

    /**
     * Returns the name of this MaterialGemDrawer's module.
     * Creation date: (05/02/01 9:55:42 AM)
     * @return java.lang.String
     */
    @Override
    public ModuleName getModuleName() {
        return (ModuleName) getUserObject();
    }

    /**
     * Return the name of the drawer.
     * Note: Unlike the getModuleName method, if the module name has length
     * zero, then a default name will be returned.
     * Creation date: (10/27/00 2:23:56 PM)
     * @return java.lang.String the name
     */
    @Override
    public String toString() {
        return getDisplayedString();
    }
    
    /**
     * @see org.openquark.gems.client.browser.BrowserTreeNode#getDisplayedString()
     */
    @Override
    public String getDisplayedString() {
        return displayedString.length() > 0 ? displayedString : BrowserTree.DEFAULT_MODULE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(MutableTreeNode newChild, int childIndex) {
        super.insert(newChild, childIndex);
    }
    
    /**
     * @return whether this node is a namespace node - i.e. it does not contain children corresponding to entities.
     */
    @Override
    public boolean isNamespaceNode() {
        return isNamespaceNode;
    }
}
