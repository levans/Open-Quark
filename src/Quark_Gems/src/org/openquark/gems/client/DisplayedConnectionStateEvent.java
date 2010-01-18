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
 * DisplayedConnectionStateEvent.java
 * Creation date: (03/04/02 4:04:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Rectangle;


/**
 * A listener that listens for Connection state changes.
 * 
 * Creation date: (03/04/02 4:04:00 PM)
 * @author Edward Lam
 */
public class DisplayedConnectionStateEvent extends DisplayedConnectionEvent {

    private static final long serialVersionUID = 8878415636228303252L;
    /** The type of event. */
    private final EventType eventType;

    /**
     * Constructor for a displayed connection state event.
     */
    public DisplayedConnectionStateEvent(DisplayedConnection source, Rectangle oldBounds, EventType eventType) {
        super(source, oldBounds);
        this.eventType = eventType;
    }

    /**
     * Gets the type of event.
     * @return EventType the type
     */
    public final EventType getType() {
        return eventType;
    }

    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event-logging and for debugging.
     *
     * @return a string identifying the event and its attributes
     */
    @Override
    public String paramString() {
        return "DisplayedConnectionStateChangeEvent";
    }
    
    /**
     * Enumeration for displayed connection state events.
     * Creation date: (03/04/02 4:04:00 PM)
     */
    public static final class EventType {

        private final String typeString;

        private EventType(String s) {
            typeString = s;
        }

        /** Bad type. */
        public static final EventType BAD = new EventType("BAD");

        /**
         * Converts the type to a string.
         * @return the string
         */
        @Override
        public String toString() {
            return typeString;
        }
    }
}
