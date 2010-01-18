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
 * BrowserTreeCellRenderer.java
 * Creation date: (2/1/01 11:21:29 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client.browser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.services.GemEntity;
import org.openquark.util.WildcardPatternMatcher;


/**
 * The BrowserTreeCellRenderer sets the look of the BrowserTree cells based on their content.
 * Creation date: (2/1/01 11:21:29 AM)
 * @author Luke Evans
 */
public class BrowserTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -1966243017498574478L;

    /** The padding between the name and type when displaying types for the gem tree node. */
    private static final String NAME_TYPE_PAD = "  ";
    
    private static final Color NON_VISIBLE_COLOR = UIManager.getColor("TextField.inactiveForeground");
    private static final Color CURRENT_MODULE_COLOR = Color.MAGENTA;

    private static final Color TYPE_SIGNATURE_COLOR = Color.GRAY;
    private static final Color SEARCH_HIGHLIGHT_COLOR = Color.MAGENTA.darker();

    private static final ImageIcon constructorIcon;
    private static final ImageIcon scIcon;
    private static final ImageIcon workspaceIcon;
    private static final ImageIcon drawerIcon;
    private static final ImageIcon namespaceIcon;
    private static final ImageIcon searchResultIcon;

    static {
        // Make icon objects
        constructorIcon = new ImageIcon(Object.class.getResource("/Resources/Gem_Yellow.gif"));
        scIcon = new ImageIcon(Object.class.getResource("/Resources/Gem_Red.gif"));
        workspaceIcon = new ImageIcon(Object.class.getResource("/Resources/vault.gif"));
        drawerIcon = new ImageIcon(Object.class.getResource("/Resources/drawer.png"));
        namespaceIcon = new ImageIcon(Object.class.getResource("/Resources/nav_namespace.png"));
        searchResultIcon = new ImageIcon(Object.class.getResource("/Resources/flashlight.gif"));
    }

    /** The BrowserTree we are rendering for. */
    private BrowserTree browserTree;

    /** The BrowserTreeModel of the tree we are rendering for. */
    private BrowserTreeModel browserTreeModel;

    /** The tree node we are rendering. */
    private BrowserTreeNode treeNode;

    /**
     * Default BrowserTreeCellRenderer constructor
     */
    public BrowserTreeCellRenderer() {
        super();
    }

    /**
     * {@inheritDoc}
     * This is the default getTreeCellRendererComponent to add logic for selecting icons etc.
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        // Remember the important stuff for later when we are painting
        browserTree = (BrowserTree) tree;
        browserTreeModel = (BrowserTreeModel) tree.getModel();
        treeNode = (BrowserTreeNode) value;
        
        // We want to do everything 'normal' except for a few tweaks
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // The node's display text.
        String displayString = treeNode.getDisplayedString();
        
        if (leaf) {
            // If the node is a leaf we know it must be a GemTreeNode
            
            if (isDataConstructor(value)) {
                setIcon(constructorIcon);
            } else {
                setIcon(scIcon);
            }

            // Paint non-visible entities in a different color
            GemEntity gemEntity = (GemEntity) treeNode.getUserObject();
            if (!browserTreeModel.isVisibleGem(gemEntity)) {
                setForeground(NON_VISIBLE_COLOR);
            }
            
            // Update the text if we are supposed to show type signatures
            if (browserTree.getDisplayTypeExpr()) {
                ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(browserTreeModel.getPerspective().getWorkingModuleTypeInfo());
                displayString += NAME_TYPE_PAD + gemEntity.getTypeExpr().toString(namingPolicy);
            }

        } else {

            if (isWorkspaceRoot(value)) {
                
                // Set the workspace icon
                setIcon(workspaceIcon);

            } else if (isDrawer(value)) {
                
                GemDrawer gemDrawer = (GemDrawer)value;
                ModuleName moduleName = gemDrawer.getModuleName();

                // Set the drawer icon
                if (gemDrawer.isNamespaceNode()) {
                    setIcon(namespaceIcon);
                } else {
                    setIcon(drawerIcon);
                }
                
                // Draw non-visible modules in a different color and highlight the working module.
                if (gemDrawer.isNamespaceNode()) {
                    if (!browserTreeModel.doesNamespaceContainVisibleModules(moduleName)) {
                        setForeground(NON_VISIBLE_COLOR);
                    }
                } else if (!browserTreeModel.isVisibleModule(moduleName)) {
                    setForeground(NON_VISIBLE_COLOR);
                } else if (browserTree.getHighlightCurrentModule() && browserTreeModel.isWorkingModule(moduleName)) {
                    setForeground(CURRENT_MODULE_COLOR);
                }
            
            } else if (isSearchResultParentNode(value)) {
             
                // Set the search result icon   
                setIcon(searchResultIcon);
            } 

            // If it fails the tests above, this must be one of those 'category' folders.
            // Note: This will need an icon later...
        }
        
        // Set the node's displayed text.
        setText(displayString);
        
        return this;
    }

    /**
     * Sets up the basic AttributedString to use for drawing the node names.
     * Later this string is modified to draw in different colours, sizes, etc.
     * @return AttributedString
     */
    private AttributedString getAttributedString() {
        
        AttributedString theString = new AttributedString(getText());
        GemEntity gemEntity = (GemEntity) treeNode.getUserObject();
        Color foreground = (selected) ? getTextSelectionColor() : getTextNonSelectionColor();
        
        if (browserTreeModel.isVisibleGem(gemEntity)) {
            theString.addAttribute(TextAttribute.FOREGROUND, foreground);
        } else {
            theString.addAttribute(TextAttribute.FOREGROUND, NON_VISIBLE_COLOR);
        }

        theString.addAttribute(TextAttribute.FONT, getFont());
        
        return theString;
    }

    /**
     * @return if the renderer should render any part of the node text with attributes different from those
     *   which will be specified in the graphics context (eg. custom fonts or colours), then the corresponding
     *   AttributedString will be returned with the desired attributes.  Otherwise null.
     */
    private AttributedString getStylizedName() {
        AttributedString stylizedName = null;

        if (isGemNode(treeNode) && browserTree.getDisplayTypeExpr()) {
            // If required draw type signatures next to gem names in a different font.
            
            stylizedName = getAttributedString();
            
            GemEntity gemEntity = (GemEntity) treeNode.getUserObject();                
            ScopedEntityNamingPolicy namingPolicy = new UnqualifiedUnlessAmbiguous(browserTreeModel.getPerspective().getWorkingModuleTypeInfo());
            int nameLength = treeNode.getDisplayedString().length() + NAME_TYPE_PAD.length();
            int typeLength = gemEntity.getTypeExpr().toString(namingPolicy).length();
            
            // Make the type name appear in italic and in a smaller size
            stylizedName.addAttribute(TextAttribute.FONT, getFont().deriveFont(Font.ITALIC), nameLength, nameLength + typeLength);
            
            if (browserTreeModel.isVisibleGem(gemEntity)) {
                // Visible entities get a different colour for type signature.
                stylizedName.addAttribute(TextAttribute.FOREGROUND, TYPE_SIGNATURE_COLOR, nameLength, nameLength + typeLength);
            }
        }
            
        if (isSearchResultNode(treeNode)) {
            // For search result nodes we highlight the text that matches the search string.
            // Note that a search result node is also a gem node.

            if (stylizedName == null) {
                stylizedName = getAttributedString();
            }

            String searchString = browserTreeModel.getSearchString();
            if (searchString != null) {
                Pattern searchPattern = Pattern.compile(WildcardPatternMatcher.wildcardPatternToRegExp(searchString), Pattern.CASE_INSENSITIVE);
                Matcher matcher = searchPattern.matcher(getText());
                
                // find() is a method on Matcher that returns true for as long as there are
                // additional matches from the end of the last match to the end of the string.
                // The Matcher itself is stateful and stores the location of the start and end of the last match,
                // and a call find() is akin to Iterator.next() in that it alters these internal states.
                while (matcher.find()) {
                    stylizedName.addAttribute(TextAttribute.FOREGROUND, SEARCH_HIGHLIGHT_COLOR, matcher.start(), matcher.end());
                }
            }
        }
        
        return stylizedName;
    }
    
    /**
     * Overrides paint to draw the gem type signatures in a different font/color and to
     * highlight gem search results in a different colour.
     */
    @Override
    public void paint(Graphics g) {

        // Draw the normal representation first. We draw over it for our custom changes.
        // TODO: This is slightly inefficient.  However, even in the drawn-over case, we still need 
        //   to call the super method to draw the icon.
        super.paint(g);

        AttributedString stylizedName = getStylizedName();
        if (stylizedName != null) {
            
            Graphics2D g2 = (Graphics2D)g;
            
            Color background = (selected) ? getBackgroundSelectionColor() : getBackgroundNonSelectionColor();
            
            // Update our border and background to match the new stylized text
            TextLayout layout = new TextLayout(stylizedName.getIterator(), g2.getFontRenderContext());
            int width = layout.getBounds().getBounds().width;
                    
            // Erase the previous background and text
            g2.setColor(getBackground());
            g2.fillRect(getLabelStart(), 0, getWidth(), getHeight());

            // Draw the new background
            g2.setColor(background);
            g2.fillRect(getLabelStart(), 0, width + 2, getHeight());

            // Draw the focus border
            Color focusBorderColor = getBorderSelectionColor();
            if (hasFocus && focusBorderColor != null) {
                g2.setColor(focusBorderColor);
                g2.drawRect(getLabelStart(), 0, width + 2, getHeight() - 1);
            }            
            
            // Finally draw the actual text
            g2.drawString(stylizedName.getIterator(), getLabelStart() + 1, getHeight() - 3);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize() {
        
        Dimension preferredSize = super.getPreferredSize();
        
        AttributedString stylizedName = getStylizedName();
        if (stylizedName != null) {
            
            Graphics2D g2d = ((Graphics2D)getGraphics());
            if (g2d != null) {
                // Override to get the correct size when taking into account the fact that the attributed string 
                // size is different from the size when all text is rendered with the component's font.
                
                FontRenderContext fontRenderContext = g2d.getFontRenderContext();
                
                float unadjustedTextAdvance = (new TextLayout(getText(), getFont(), fontRenderContext)).getAdvance();
                float adjustedTextAdvance = (new TextLayout(stylizedName.getIterator(), fontRenderContext)).getAdvance();
                
                preferredSize.width += (int)(adjustedTextAdvance - unadjustedTextAdvance);
            }
        }
        
        return preferredSize;
    }
    
    /**
     * Returns the x-coordinate at which point the text portion of the tree node starts.
     * @return int
     */
    private int getLabelStart() {
        Icon icon = getIcon();
    
        if (icon != null) {
            return icon.getIconWidth() + Math.max(0, getIconTextGap() - 1);
        }
        
        return 0;
    }    

    /**
     * Determine if this tree element is representing a data constructor.
     * @return boolean true if a constructor
     * @param value Object the tree element
     */
    private boolean isDataConstructor(Object value) {

        if (value instanceof BrowserTreeNode) {
            BrowserTreeNode gem = (BrowserTreeNode)value;
            GemEntity gemEntity = (GemEntity)gem.getUserObject();
            return gemEntity.isDataConstructor();
        }   
        return false;
    }

    /**
     * Determine if this tree element is representing a drawer.
     * @return boolean true if a drawer (any type of drawer)
     * @param value Object the tree element
     */
    private boolean isDrawer(Object value) {
        return (value instanceof GemDrawer);
    }

    /**
     * Determine if this tree element is representing the workspace.
     * @return boolean true if it's the workspace.
     * @param value Object the tree element
     */
    private boolean isWorkspaceRoot(Object value) {
        return (value instanceof WorkspaceRootTreeNode);
    }
    
    /**
     * Determine if this tree element represents a gem node.
     * @return boolean true if a gem node
     * @param value Object the tree element
     */
    private boolean isGemNode(Object value) {
        return (value instanceof GemTreeNode);
    }
    
    /**
     * Determine if this tree element represent a search result gem tree node.
     * @return boolean true if a search results gem tree node
     * @param value Object the tree element
     */
    private boolean isSearchResultNode(Object value) {
        return (value instanceof SearchResultGemTreeNode);
    }
    
    /**
     * Determine if this tree element represents the search result parent node.
     * @return boolean true if a search result parent node
     * @param value Object the tree element
     */
    private boolean isSearchResultParentNode(Object value) {
        return (value instanceof SearchResultRootTreeNode);
    }
}
