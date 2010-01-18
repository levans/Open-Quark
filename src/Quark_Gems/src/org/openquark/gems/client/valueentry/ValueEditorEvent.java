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
 * ValueEditorEvent.java
 * Creation date: (12/04/01 11:23:59 AM)
 * By: Michael Cheng
 */
package org.openquark.gems.client.valueentry;

import java.util.EventObject;

import org.openquark.cal.valuenode.ValueNode;


/**
 * The ValueEditorEvent.
 * This class contains two important pieces of information.
 * 1) The source of this event (the ValueEditor whose data has changed)
 * 2) The old ValueNode value.
 * 
 * @author Michael Cheng
 */
public class ValueEditorEvent extends EventObject {

    private static final long serialVersionUID = 7276645382282515934L;
    
    /** The old ValueNode value. */
    private final ValueNode oldValue;

    /**
     * ValueEditorEvent constructor.
     * @param source the value editor which spawned the event.
     * @param oldValueNode the old value corresponding to the event, if any.
     */
    public ValueEditorEvent(Object source, ValueNode oldValueNode) {
        super(source);
        this.oldValue = oldValueNode;
    }
    
    /**
     * Returns the old ValueNode value.
     *   For value changed events, this is the edit valuenode before it changed.
     *   For value committed events, this is the owner valuenode before the commit occurred.
     * @return ValueNode
     */
    public ValueNode getOldValue() {
        return oldValue;
    }
}
