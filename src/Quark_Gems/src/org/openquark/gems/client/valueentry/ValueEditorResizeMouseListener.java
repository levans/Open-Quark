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
 * ValueEditorResizeMouseListener.java
 * Creation date: (03/07/01 9:57:00 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;

/**
 * A mouse listener that enables resize capability for value editors. It allows resizing the editor from
 * the left, right and bottom borders. This listener is automatically added by the ValueEditor base class.
 * Note: If you use the listener, remember to add it as a Mouse and MouseMotion listener.
 * Note: Since this class may change the JPanel's mouse cursor, it is very important that any JPanel that use this
 * sets its components (Eg: ScrollPane, buttons, etc) to have the default cursor. This way, the default cursor will
 * be shown on the JPanel's components, while the mouse cursor on the JPanel itself will be determined by this class. 
 * @author Michael Cheng
 * @author Frank Worsley
 */
class ValueEditorResizeMouseListener extends MouseInputAdapter {
    
    /**
     * Unless a person is extremely dexterious, allow a 'fudge' factor
     * that if the person moves the mouse pointer close enough to the
     * border of the editor, then enable the resize.
     */
    private static final int FUDGE_FACTOR = 8;
    
    /** The default minimum size for an editor. */
    private static final Dimension DEFAULT_MIN_SIZE = new Dimension(50, 50);
    
    /** The default maximum size for an editor. */
    private static final Dimension DEFAULT_MAX_SIZE = new Dimension(2048, 2048);

    /** Whether the south side is being resized. */
    private boolean southResize = false;
    
    /** Whether the east side is being resized. */
    private boolean eastResize = false;
    
    /** Whether the west side is being resized. */
    private boolean westResize = false;
    
    /** Whether the north side is being resized. */
    private boolean northResize = false;
    
    /** Whether the location of the editor is being changed. */
    private boolean locationChange = false;
    
    /** The point relative to which the user is dragging the mouse. */
    private Point dragPoint = null;

    /** The panel this resize listener is for. */
    private final ValueEditor editor;
    
    /**
     * Constructor for a new ValueEditorResizeMouseListener.
     * @param editor the ValueEditor this listener is for
     */
    public ValueEditorResizeMouseListener(ValueEditor editor) {

        if (editor == null) {
            throw new NullPointerException();
        }

        this.editor = editor;
    }
    
    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        // Determine which resizes are valid, and resize accordingly.
        // In case you were wondering, e's X and Y are relative to the editor's position.
        
        if (locationChange && editor.isMoveable()) {

            int newX = editor.getX() + e.getX() - dragPoint.x;
            int newY = editor.getY() + e.getY() - dragPoint.y;
            
            editor.setLocation(newX, newY);
            editor.revalidate();
            editor.repaint();
            
        } else if (!editor.isResizable()) {
            return;
            
        } else if (westResize && southResize) {
            
            int resizeWidth = editor.getWidth() + -e.getX();
            int resizeHeight = e.getY();

            reshape(resizeWidth, resizeHeight);
            
        } else if (eastResize && southResize) {
            
            int resizeWidth = e.getX();
            int resizeHeight = e.getY();

            reshape(resizeWidth, resizeHeight);
            
        } else if (westResize && northResize) {
            
            int resizeWidth = editor.getWidth() + -e.getX();
            int resizeHeight = editor.getHeight() + -e.getY();

            reshape(resizeWidth, resizeHeight);
            
        } else if (eastResize && northResize) {
            
            int resizeWidth = e.getX();            
            int resizeHeight = editor.getHeight() + -e.getY();

            reshape(resizeWidth, resizeHeight);            
            
        } else if (eastResize) {

            int resizeWidth = e.getX();

            reshape(resizeWidth, editor.getHeight());
            
       } else if (westResize) {
           
           int resizeWidth = editor.getWidth() + -e.getX();
           
           reshape(resizeWidth, editor.getHeight());
               
        } else if (southResize) {

            int resizeHeight = e.getY();

            reshape(editor.getWidth(), resizeHeight);
        
        } else if (northResize) {
            
            int resizeHeight = editor.getHeight() + -e.getY();

            reshape(editor.getWidth(), resizeHeight);
        }
    }
    
    /**
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        
        southResize = e.getY() >= editor.getHeight() - FUDGE_FACTOR && e.getY() < editor.getHeight();
        eastResize = e.getX() >= editor.getWidth() - FUDGE_FACTOR && e.getX() < editor.getWidth();
        westResize = e.getX() <= FUDGE_FACTOR && e.getX() > 0;
        northResize = e.getY() <= FUDGE_FACTOR && e.getY() > 0;
        locationChange = e.getY() <= editor.getInsets().top && e.getY() > FUDGE_FACTOR;

        if (!editor.isResizable()) {
            return;
            
        } else if (eastResize && southResize) {
            editor.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
            
        } else if (westResize && southResize) {
            editor.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
            
        } else if (eastResize && northResize) {
            editor.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
            
        } else if (westResize && northResize) {
            editor.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
        
        } else if (eastResize) {
            editor.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));

        } else if (southResize) {
            editor.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
        
        } else if (westResize) {
            editor.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            
        } else if (northResize) {
            editor.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
        
        } else {
            editor.setCursor(null);
        }
    }
    
    /**
     * Resizes the editor to the given width and height. If the given size is outside
     * the allowed resize range, then it will be changed to fit the range.
     * @param width the new width
     * @param height the new height
     */
    private void reshape(int width, int height) {

        // Enfore the minimum/maximum size requirements.        
        Dimension minSize = editor.getMinResizeDimension();
        Dimension maxSize = editor.getMaxResizeDimension();
        
        if (minSize == null) {
            minSize = DEFAULT_MIN_SIZE;
        }
        
        if (maxSize == null) {
            maxSize = DEFAULT_MAX_SIZE;
        }
        
        width = ValueEditorManager.clamp(minSize.width, width, maxSize.width);
        height = ValueEditorManager.clamp(minSize.height, height, maxSize.height);

        // Editor position may change while this is running.
        int x = editor.getX();
        int y = editor.getY();
        
        Component parent = editor.getParent();
        Rectangle parentRect = new Rectangle(parent.getWidth(), parent.getHeight());
        
        if (parent instanceof JComponent) { 
            parentRect = ((JComponent) parent).getVisibleRect();
        }
        
        if (westResize) {
            
            // Components resize to the east, so if we want to give the user the
            // appearance that he is resizing to the west, we need to move the
            // editor to the left or right.
            x = editor.getX() + editor.getWidth() - width;
            
            if (x < parentRect.x) {
                // If we have sized all the way to the left edge, then we
                // don't want to resize any further.
                x = parentRect.x;
                width = editor.getX() + editor.getWidth() - parentRect.x;
            }
        }
        
        if (northResize) {
            
            // See above.
            y = editor.getY() + editor.getHeight() - height;
            
            if (y < parentRect.y) {
                y = parentRect.y;
                height = editor.getY() + editor.getHeight() - parentRect.y;
            }
        }
        
        // We have to set size and location twice for this to work in all cases.
        // This is because the value editors override these methods to enforce that
        // they are always displayed fully within their parent's bounds. If the user
        // resizes an editor very quickly and we try to change its location before 
        // changing its size, then the editor may be positioned outside the parent's
        // bounds. The setLocation method will then modify the location to place the
        // editor within the parent bounds. At this point the sizing will be screwed
        // up. Sometimes we need to set location before setting size, at other times
        // it's the reverse. Therefore we just do it twice to catch all cases.
        
        if (x + width > parentRect.x + parentRect.width) {
            width = parentRect.x + parentRect.width - x;
        }
        
        if (y + height > parentRect.y + parentRect.height) {
            height = parentRect.y + parentRect.height - y;
        }
        
        editor.setLocation(x, y);
        editor.setSize(width, height);
        editor.setLocation(x, y);
        editor.setSize(width, height);
        
        editor.revalidate();
        editor.repaint();
        editor.userHasResized();
    }
    
    /**
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        dragPoint = e.getPoint();
    }
    
    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        dragPoint = null;
    }
}
