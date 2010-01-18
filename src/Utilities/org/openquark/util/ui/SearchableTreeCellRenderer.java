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
 * SearchableTreeCellRenderer.java
 * Created: Nov 4, 2004
 * By: Kevin Sit
 */
package org.openquark.util.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * A <code>TreeCellRenderer</code> implementation that is used in conjunction
 * with a <code>SearchableTree</code>.
 */
public class SearchableTreeCellRenderer implements TreeCellRenderer {
    
    /**
     * Each renderer can implements its own delegate to customize its behaviour
     * for displaying a search result.  By using a delegate, we can avoid
     * having a lot of instanceof checks during runtime, consider the fact
     * that a renderer is used so frequently.
     */
    private abstract class Delegate {
        
        /**
         * Configure the renderer for display.
         * @param renderer
         * @return Component
         */
        public abstract Component configureRenderer(Component renderer);
        
    }
    
    /**
     * A delegate implementation that is used for any TreeCellRenderer.
     */
    private class GenericTreeCellRendererDelegate extends Delegate {
        
        /** {@inheritDoc} */
        @Override
        public Component configureRenderer(Component renderer) {
            renderer.setForeground(HIGHLIGHT_COLOR);
            return renderer;
        }
    }
    
    /**
     * A delegate implementation that is used for DefaultTreeCellRenderer.
     */
    private class DefaultTreeCellRendererDelegate extends Delegate {
        
        /** {@inheritDoc} */
        @Override
        public Component configureRenderer(Component renderer) {
            DefaultTreeCellRenderer comp = (DefaultTreeCellRenderer) renderer;
            String text = comp.getText();
            if (text != null && lowerCaseSearchString != null) {
                int startIndex = text.toLowerCase().indexOf(lowerCaseSearchString);
                if (startIndex >= 0) {
                    int endIndex = startIndex + searchStringLength; // index is exclusive 
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html>"); //$NON-NLS-1$
                    
                    if (startIndex > 0) {
                        sb.append(text.substring(0, startIndex));
                    }
                    
                    if (searchStringLength > 0) {
                        sb.append("<font color=\"#"); //$NON-NLS-1$
                        sb.append(HIGHLIGHT_COLOR_HEX);
                        sb.append(")\">"); //$NON-NLS-1$
                        sb.append(text.substring(startIndex, endIndex));
                        sb.append("</font>"); //$NON-NLS-1$
                    }
                    
                    if (endIndex < text.length()) {
                        sb.append(text.substring(endIndex, text.length()));
                    }
                    
                    sb.append("</html>"); //$NON-NLS-1$
                    comp.setText(sb.toString());
                }
            }
            return comp;
        }
    }
    
    /** The default highlight color */
    private static final Color HIGHLIGHT_COLOR = Color.MAGENTA;
    
    /** The default highlight color as hex string */
    private static final String HIGHLIGHT_COLOR_HEX = "FF00FF"; //$NON-NLS-1$
    
    /** The wrapped renderer */
    private final TreeCellRenderer renderer;
    
    /** The delegate */
    private final Delegate delegate;
    
    /** The actual search string */
    private String searchString;
    
    /** Lower case search string, avoid recomputing this again and again */
    private String lowerCaseSearchString;
    
    /** Search string length, avoid recomputing this again and again */
    private int searchStringLength;
    
    private SearchableTreeCellRenderer(TreeCellRenderer renderer) {
        this.renderer = renderer;
        if (renderer instanceof DefaultTreeCellRenderer) {
            this.delegate = new DefaultTreeCellRendererDelegate();
        } else {
            this.delegate = new GenericTreeCellRendererDelegate();
        }
    }
    
    /**
     * Returns the search string.
     * @return String
     */
    public String getSearchString() {
        return searchString;
    }
    
    /**
     * Sets the search string.
     * @param searchString
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
        this.lowerCaseSearchString = searchString == null ? null : searchString.toLowerCase();
        this.searchStringLength = searchString == null ? 0 : searchString.length();
    }
    
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        Component c = renderer.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof SearchHighlightTreeNode) {
            return delegate.configureRenderer(c);
        } else {
            return c;
        }
    }
    
    /**
     * Creates a new instance of <code>SearchableTreeCellRenderer</code> and use it
     * to wrap the given tree cell renderer.
     * @param renderer
     * @return TreeCellRenderer
     */
    public static TreeCellRenderer create(TreeCellRenderer renderer) {
        // The given renderer can never be null
        if (renderer == null) {
            renderer = new DefaultTreeCellRenderer();
        }
        
        // Only wrap the renderer if it is not a searchable tree cell renderer
        if (renderer instanceof SearchableTreeCellRenderer) {
            return renderer;
        } else {
            return new SearchableTreeCellRenderer(renderer);
        }
    }
    
}
