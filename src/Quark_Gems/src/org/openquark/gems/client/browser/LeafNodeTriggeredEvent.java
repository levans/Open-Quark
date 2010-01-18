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
 * LeafNodeTriggeredEvent.java
 * Creation date: (04/17/2002 8:50:00 AM).
 * By: Steve Norton
 */
package org.openquark.gems.client.browser;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * An event that indicates one or more leaf nodes were triggered.  A node is triggered when it is
 * selected from a tree and ENTER is pressed.
 * NOTE: multiple nodes may be selected when they are triggered so all of them will be included in one event.
 * Creation date: (04/17/2002 8:50:00 AM).
 * @author Steve Norton
 */
public class LeafNodeTriggeredEvent extends EventObject {
    
    private static final long serialVersionUID = 6458293372557684235L;
    // The list of objects of the nodes that were triggered.
    private final List<Object> userObjects;

    /**
     * Constructor for the LeafNodeTriggeredEvent class.
     * Creation date: (04/17/2002 8:58:00 AM).
     * @param source Object - the source of the TreeNodeTriggeredEvent
     * @param userObjects the objects of the nodes that were triggered
     */
    public LeafNodeTriggeredEvent(Object source, List<Object> userObjects) {
        
        super(source);
        this.userObjects = userObjects;
    }
    
    /**
     * Returns a parameter string identifying this event.
     * This method is useful for event-logging and for debugging.
     * Creation date: (04/17/2002 8:59:00 AM)
     * @return a string identifying the event and its attributes
     */
    public String paramString() {
        return "LeafNodeTriggeredEvent";
    }
    
    /**
     * Return a copy of the list of objects of the nodes that were triggered. 
     * Creation date: (04/17/2002 9:11:00 AM)
     * @return list of triggered objects
     */
    public List<Object> getTriggeredObjects() {
        
        return new ArrayList<Object>(userObjects);
    }
}
