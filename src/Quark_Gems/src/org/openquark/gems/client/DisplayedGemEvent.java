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
 * DisplayedGemEvent.java
 * Creation date: (03/04/02 2:49:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Rectangle;
import java.util.EventObject;

/**
 * The base class for displayed gem events.
 * 
 * Creation date: (03/04/02 2:49:00 PM)
 * @author Edward Lam
 */
public abstract class DisplayedGemEvent extends EventObject {

	private final Rectangle oldBounds;

    public DisplayedGemEvent(DisplayedGem source, Rectangle oldBounds) {
        super(source);
        this.oldBounds = oldBounds;
    }
    
    /**
     * Get the old bounds of the displayed gem.
     * Creation date: (03/01/02 11:24:00 AM)
     * @return Rectangle the old bounds of the displayed gem.
     */
    public Rectangle getOldBounds() {
        return (oldBounds == null) ? null : new Rectangle(oldBounds);
    }

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event-logging and for debugging.
     *
     * Creation date: (02/28/02 5:03:00 PM)
     * @return a string identifying the event and its attributes
     */
    public String paramString() {
        return "DisplayedGemEvent";
    }
    
}
