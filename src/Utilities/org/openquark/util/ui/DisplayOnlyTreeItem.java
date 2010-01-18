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
 * DisplayOnlyTreeItem.java
 * Created: Sept 15, 2004
 * By: Richard Webster
 */
package org.openquark.util.ui;

import javax.swing.Icon;

/**
 * A simple tree item implementation that can be used as an user object.  This
 * item will provide a display string and an icon for the renderer.
 * 
 * @author Richard Webster
 */
public class DisplayOnlyTreeItem {

    /** Display this string, cannot be null */
    private final String text;
        
    /** Use this icon as the icon for the tree render, can be null */
    private final Icon icon;
        
    /**
     * DisplayOnlyTreeItem constructor.
     * @param text  the text to be displayed for the node in the tree
     */
    public DisplayOnlyTreeItem(String text) {
        this(text, null);
    }

    /**
     * DisplayOnlyTreeItem constructor.
     * @param text  the text to be displayed for the node in the tree
     * @param icon  the icon to be displayed for the tree node
     */
    public DisplayOnlyTreeItem(String text, Icon icon) {
        this.text = text;
        this.icon = icon;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getText();
    }

    /**
     * Returns the display text.
     * @return the display text
     */
    public String getText() {
        return text;
    }
        
    /**
     * Returns the node icon, or null if there isn't one.
     * @return the node icon
     */
    public Icon getIcon() {
        return icon;
    }
}