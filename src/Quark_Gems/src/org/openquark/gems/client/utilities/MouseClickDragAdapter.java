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
 * MouseClickDragAdapter.java
 * Creation date: (12/13/01 5:35:24 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client.utilities;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * Adapter for a MouseClickDragListener
 * Creation date: (12/13/01 5:35:24 PM)
 * @author Edward Lam
 */
public class MouseClickDragAdapter extends MouseClickDragListener {

    /**
     * Constructor for MouseClickDragAdapter.
     */
    public MouseClickDragAdapter() {
        super();
    }

    /**
     * @see MouseClickDragListener#mouseReallyDragged(MouseEvent, Point, boolean)
     */
    public void mouseReallyDragged(MouseEvent e, Point where, boolean wasDragging) {
    }

    /**
     * Creation date: (12/13/01 4:28:24 PM)
     * @see MouseClickDragListener#mouseMoved(MouseEvent)
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * @see MouseClickDragListener#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * @see MouseClickDragListener#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
    }

}

