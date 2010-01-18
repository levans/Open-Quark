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
 * NavTreeCellRenderer.java
 * Creation date: Jul 4, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client.navigator;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.gems.client.GemCutter;
import org.openquark.util.ui.UIUtilities;


/**
 * This class implements the cell renderer to use for the navigation tree.
 * The cell renderer is the same as the default renderer except that it users custom icons.
 * 
 * @author Frank Worsley
 */
public class NavTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 7867708447965817398L;

    /** The icon for module nodes. */
    static final ImageIcon moduleNodeIcon = new ImageIcon(GemCutter.class.getResource("/Resources/nav_module.png"));
    
    /** The icon for namespace nodes. */
    static final ImageIcon namespaceNodeIcon = new ImageIcon(GemCutter.class.getResource("/Resources/nav_namespace.png"));
    
    /** The icon for vault nodes. */
    static final ImageIcon vaultNodeIcon = new ImageIcon(GemCutter.class.getResource("/Resources/nav_vault.gif"));
    
    /** The icon for function nodes. */
    static final ImageIcon functionNodeIcon = new ImageIcon(GemCutter.class.getResource("/Resources/nav_function.gif"));
    
    /** The icon for data constructor nodes. */
    static final ImageIcon dataConstructorNodeIcon = new ImageIcon(GemCutter.class.getResource("/Resources/nav_dataconstructor.gif"));
    
    /** The icon for type constructors nodes. */
    static final ImageIcon typeConstructorNodeIcon = new ImageIcon(GemCutter.class.getResource("/Resources/nav_typeconstructor.gif"));
    
    /** The icon for type class nodes. */
    static final ImageIcon typeClassNodeIcon = new ImageIcon(GemCutter.class.getResource("/Resources/nav_typeclass.gif"));
    
    /** The icon for class instance nodes. */
    static final ImageIcon classInstanceNodeIcon = new ImageIcon(GemCutter.class.getResource("/Resources/nav_classinstance.gif"));
   
    /** The icons representing the scopes of gem tree nodes, to be added to the gem icon */
    private static final ImageIcon SCOPE_PUBLIC_ICON = new ImageIcon(Object.class.getResource("/Resources/scope_public.gif"));
    private static final ImageIcon SCOPE_PROTECTED_ICON = new ImageIcon(Object.class.getResource("/Resources/scope_protected.gif"));
    private static final ImageIcon SCOPE_PRIVATE_ICON = new ImageIcon(Object.class.getResource("/Resources/scope_private.gif"));
    
    // Cache for the composite icons
    private final Map<Icon, Icon> imageCache_public = new HashMap<Icon, Icon>();
    private final Map<Icon, Icon> imageCache_protected = new HashMap<Icon, Icon>();
    private final Map<Icon, Icon> imageCache_private = new HashMap<Icon, Icon>();
    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        JLabel renderer = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // Use our custom icons instead of the default icon
        if (value instanceof NavModuleNode) {
            renderer.setIcon(moduleNodeIcon);
        } else if (value instanceof NavModuleNamespaceNode) {
            renderer.setIcon(namespaceNodeIcon);
        } else if (value instanceof NavVaultNode) {
            renderer.setIcon(vaultNodeIcon);
        } else if (value instanceof NavFunctionNode) {
            Icon scopedIcon = addScopeIcon (functionNodeIcon, (NavFunctionNode)value);
            renderer.setIcon(scopedIcon);
        } else if (value instanceof NavTypeConstructorNode) {
            Icon scopedIcon = addScopeIcon (typeConstructorNodeIcon, (NavTypeConstructorNode)value);
            renderer.setIcon(scopedIcon);
        } else if (value instanceof NavTypeClassNode) {
            Icon scopedIcon = addScopeIcon (typeClassNodeIcon, (NavTypeClassNode) value);
            renderer.setIcon(scopedIcon);
        } else if (value instanceof NavClassMethodNode) {
            Icon scopedIcon = addScopeIcon (functionNodeIcon, (NavClassMethodNode) value);
            renderer.setIcon(scopedIcon);
        } else if (value instanceof NavDataConstructorNode) {
            Icon scopedIcon = addScopeIcon (dataConstructorNodeIcon, (NavDataConstructorNode)value);
            renderer.setIcon(scopedIcon);
        } else if (value instanceof NavClassInstanceNode) {
            renderer.setIcon(classInstanceNodeIcon);
        } else if (value instanceof NavInstanceMethodNode) {
            renderer.setIcon(functionNodeIcon);
        }

        return renderer;
    }
    
    /**
     *  Add a scope decal (public, protected, private) to the base icon 
     * @param baseImage the underlying icon
     * @param value the node that the new icon is being applied to
     * @return an combined icon that has the scope decal
     */
    private Icon addScopeIcon (Icon baseImage, NavEntityNode value){
        // Uses a cache to store icons already combined
        ScopedEntity entity = value.getEntity();
        Icon baseIcon = baseImage;
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

        Icon cachedIcon = cache.get(baseIcon);
        if (cachedIcon != null) {
            return cachedIcon;

        } else {
            Icon newIcon = UIUtilities.combineIcons(baseIcon, overlayIcon);
            cache.put(baseIcon, newIcon);
            return newIcon;
        }
    }


}
