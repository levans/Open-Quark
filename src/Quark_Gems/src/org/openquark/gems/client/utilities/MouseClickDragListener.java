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
 * MouseClickDragListener.java
 * Creation date: (12/13/01 4:28:24 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client.utilities;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * MouseListener and MouseMotionListener that use reasonable models for click and drag
 * Creation date: (12/13/01 4:28:24 PM)
 * @author Edward Lam
 */
public abstract class MouseClickDragListener implements MouseListener, MouseMotionListener {

    /** Naggle size the mouse can move before a button press becomes a drag */
    protected static final int INITIAL_DRAG_OFFSET = 2;    

    /** Max distance between clicks to count as a double click */
    protected static final int DOUBLE_CLICK_OFFSET_LIMIT = 2;

    /** The number of milliseconds that should bound a double click.*/
    protected static final long DOUBLE_CLICK_THRESHOLD_TIME    = 300L;    

    /** Null zone for drag */
    private java.awt.Rectangle dragNullRect;

    /** The previous mouseRelease event. */
    private MouseEvent lastMouseRelease;

    /** Where we pressed.  Null if we're not in the pressed state. */
    protected java.awt.Point pressedAt;

    /** What type of dragging action we are performing. */
    protected DragMode dragMode;
    
    /** 
     * The current drag was initiated with the CTRL modifier.  This allows us to tell
     * when a drag was started with the CTRL modifier but didn't end with it.
     */
    protected boolean dragInitiatedWithCTRL = false;
    
    /** 
     * The current drag was initiated with the SHIFT modifier.  This allows us to tell
     * when a drag was started with the SHIFT modifier but didn't end with it.
     */
    protected boolean dragInitiatedWithSHIFT = false;

    /**
     * Drag action enum pattern.
     * Creation date: (31/08/2001 10:58:43 AM)
     * @author Edward Lam
     */
    public static class DragMode extends EnumClass {
        /*
         * DRAGGING         - dragging
         * ABORTED            - dragging in aborted state
         * NOTDRAGGING         - not dragging
         */
        public static final DragMode DRAGGING           = new DragMode ("Dragging");
        public static final DragMode ABORTED            = new DragMode ("Aborted");        
        public static final DragMode NOTDRAGGING        = new DragMode ("Not dragging");

        /**
         * Default constructor for a drag mode.
         * Creation date: (12/06/01 10:31:30 AM)
         */
        public DragMode(String name) {
            super(name);
        }
    }

    /**
     * Constructor for a MouseClickDragListener
     * Creation date: (12/13/01 4:41:24 PM)
     */
    public MouseClickDragListener() {
        // Make the drag null zone rectangle
        dragNullRect = new java.awt.Rectangle(0, 0, INITIAL_DRAG_OFFSET * 2, INITIAL_DRAG_OFFSET * 2);

        // not dragging
        dragMode = DragMode.NOTDRAGGING;
    }

    /**
     * Move the drag mode into the aborted state.
     * Creation date: (12/06/01 12:22:15 PM)
     */
    protected void abortDrag() {
        // Set our drag states appropriately
        dragMode = DragMode.ABORTED;
    }

    /**
     * Return the number of mouse buttons associated with a mouse event.
     * Creation date: (03/08/2001 10:32:13 AM)
     * @return int the number of buttons
     * @param e MouseEvent the mouse event
     */
    public static final int getNumMouseButtons(MouseEvent e) {
        int numButtons = 0;
        if (javax.swing.SwingUtilities.isLeftMouseButton(e))
            numButtons++;
        if (javax.swing.SwingUtilities.isMiddleMouseButton(e))
            numButtons++;
        if (javax.swing.SwingUtilities.isRightMouseButton(e))
            numButtons++;
        return numButtons;
    }
    /**
     * Whether this drag mode actually enables accomplishing anything.
     *   Gem dragging, connecting, disconnecting, and selecting are useful.
     *   Aborted, useless, and not-dragging states are not useful.
     * Creation date: (12/06/01 3:31:30 PM)
     * @param mode DragMode the DragMode to check
     * @return boolean true if accomplishing anything with this drag.  
     */
    protected boolean isUsefulDragMode(DragMode mode) {
        return (mode == DragMode.DRAGGING);
    }
    /**
     * Invoked when the mouse has been clicked on a component.
     * Note: DO NOT USE unless you really want annoying UI behaviour.  Use mouseReallyClicked() instead.
     * You don't want to use this method (unless you're a masochist!)
     * Mouse clicked events are not generated unless the pointer doesn't move even one pixel between press and release!
     * Creation date: (12/13/01 4:28:24 PM)
     * @see MouseListener#mouseClicked(MouseEvent)
     */
    public final void mouseClicked(MouseEvent e) {
    }

    /**
     * Creation date: (12/13/01 4:28:24 PM)
     * @see MouseMotionListener#mouseDragged(MouseEvent)
     */
    public void mouseDragged(MouseEvent e) {
    
        try {
            // If we bailed out, do nothing.
            if (dragMode == DragMode.ABORTED) {
                return;
            }

            // abort drags with > 1 mouse button
            if (getNumMouseButtons(e) > 1) {
                abortDrag();
                return;
            }    

            // Where are we now?
            java.awt.Point where = e.getPoint();
        
            // already dragging?
            boolean wasDragging = (dragMode != DragMode.NOTDRAGGING);

            // Check for a transition from the pressed to the dragging state.
            // This is triggered by a mouse move beyond the mouse naggle.
            if (!wasDragging && !dragNullRect.contains(where)) {
                // Remember whether any modifiers were used or not
                dragInitiatedWithCTRL = e.isControlDown();
                dragInitiatedWithSHIFT = e.isShiftDown();

                enterDragState(e);
            }
        
            // now figure out what to do if we're doing something in the drag state
            if (isUsefulDragMode(dragMode)) {
                mouseReallyDragged(e, where, wasDragging);
            }

        } catch (Throwable t) {
            // some error occurred.  Treat this as an aborted drag.
            abortDrag();
            t.printStackTrace();
        }
    }
    /**
     * Invoked when a mouse button has been pressed on a component.
     * Creation date: (12/13/01 4:28:24 PM)
     * @see MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e){

        try {
            // If we're already doing a drag action, then abort!
            // This can happen if the user is dragging with MB1 down and then presses another button.
            if (dragMode != DragMode.NOTDRAGGING && dragMode != DragMode.ABORTED) {
                abortDrag();
                return;
            }
            
            // update the drag mode and press location
            dragMode = DragMode.NOTDRAGGING;
            pressedAt = e.getPoint();

            // Set up the drag null zone for this click
            dragNullRect.x = pressedAt.x - INITIAL_DRAG_OFFSET;
            dragNullRect.y = pressedAt.y - INITIAL_DRAG_OFFSET;

        } catch (Throwable t) {
            // some error occurred.  Treat this as an aborted drag.
            abortDrag();
            t.printStackTrace();
        }
    }
    /**
     * Invoked when a mouse button has been released on a component.
     * Creation date: (12/13/01 4:28:24 PM)
     * @see MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
    
        // are we really dragging?  If not, it's a "click".
        if (dragMode == DragMode.NOTDRAGGING) {
            mouseReallyClicked(convertEvent(e, MouseEvent.MOUSE_CLICKED));

        } else {
            // We've finished dragging.  Finish up and take appropriate action
            exitDragState(e);
            
            // Clear the flags that tracks use of the CTRL and SHIFT modifiers.
            dragInitiatedWithCTRL = false;      
            dragInitiatedWithSHIFT = false;
        }
    }

    /**
     * Surrogate method for mouseClicked.  Called only when our definition of click occurs.
     * Creation date: (12/13/01 5:23:24 PM)
     * @param e MouseEvent the relevant event
     * @return boolean true if the click was a double click
     */
    public boolean mouseReallyClicked(MouseEvent e){

        boolean doubleClicked = false;
        long eventTime = e.getWhen();
        long mouseUpClickTime = (lastMouseRelease == null) ? 0 : lastMouseRelease.getWhen();
    
        // Calculate the time difference since the last release.  Make sure the difference is a positive number.
        // Note that the time value on a long of machines is a 64-bit *unsigned* value, but Java doesn't have unsigned 
        // numbers, so at a certain point the values roll over into the very high negative numbers which then 
        // actually *decrease* (since Java mandates a two's complement arithmetic).
        long timeDiff = eventTime - mouseUpClickTime;
        if (timeDiff < 0) {
            timeDiff = -timeDiff;
        }
    
        // Did this click happen with the given time bound?
        if (DOUBLE_CLICK_THRESHOLD_TIME > timeDiff) {

            // did this occur with the same mouse button(s)?
            if (lastMouseRelease != null && e.getModifiers() == lastMouseRelease.getModifiers()) {

                // Set up the double click zone for this click
                int zoneX = lastMouseRelease.getX() - DOUBLE_CLICK_OFFSET_LIMIT;
                int zoneY = lastMouseRelease.getY() - DOUBLE_CLICK_OFFSET_LIMIT;
                
                java.awt.Rectangle doubleClickZoneRect = 
                    new java.awt.Rectangle(zoneX, zoneY, DOUBLE_CLICK_OFFSET_LIMIT * 2, DOUBLE_CLICK_OFFSET_LIMIT * 2);

                // is the second click within the double click zone?
                if (doubleClickZoneRect.contains(e.getPoint())) {
                    doubleClicked = true;
                }
            }
        }
    
        // update the previous mouse release event
        if (doubleClicked) {
            lastMouseRelease = null;        // reset to avoid counting a triple click as two double clicks

        } else {
            lastMouseRelease = e;
        }
        
        return doubleClicked;
    }

    /**
     * Surrogate method for mouseDragged.  Called only when our definition of drag occurs.
     * Creation date: (12/13/01 5:23:24 PM)
     * @param e MouseEvent the relevant event
     * @param where java.awt.Point the (possibly adjusted from e) coordinates of the drag
     * @param wasDragging boolean True: this is a continuation of a drag.  False: first call upon transition
     * from pressed to drag.
     */
    public abstract void mouseReallyDragged(MouseEvent e, java.awt.Point where, boolean wasDragging);

    /**
     * Carry out setup appropriate to enter the drag state.
     * Principal effect is to change dragMode as appropriate.
     * Creation date: (12/13/01 5:23:24 PM)
     * @param e MouseEvent the mouse event which triggered entry into the drag state.
     */
    public void enterDragState(MouseEvent e) {
        dragMode = DragMode.DRAGGING;
    }

    /**
     * Carry out setup appropriate to exit the drag state.  
     * Principal effect is to change dragMode as appropriate.
     * Creation date: (12/06/01 6:14:42 PM)
     * @param e MouseEvent the mouse event which caused an exit from the drag state
     */
    public void exitDragState(MouseEvent e) {
        dragMode = DragMode.NOTDRAGGING;
    }
    
    /**
     * Converts the given mouse event to the new event type and returns the
     * resulting event.
     * @param e the original event
     * @param id the new event id
     * @return MouseEvent the converted mouse event
     */
    private MouseEvent convertEvent(MouseEvent e, int id) {
        
        return new MouseEvent((Component) e.getSource(),
                              id,
                              System.currentTimeMillis(),
                              e.getModifiers(),
                              e.getX(),
                              e.getY(),
                              e.getClickCount(),
                              e.isPopupTrigger(),
                              e.getButton());
    }

}

