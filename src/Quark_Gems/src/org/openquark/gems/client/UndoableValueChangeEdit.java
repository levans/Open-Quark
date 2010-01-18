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
 * UndoableValueChangeEdit.java
 * Creation date: (04/01/2002 3:48:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;

import org.openquark.cal.valuenode.ValueNode;


/**
 * An UndoableEdit used to undo a change in the value of a value gem.
 * @author Edward Lam
 */
class UndoableValueChangeEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = 6824211788786145L;

    /** The displayed value gem in question. */
    private final ValueGem valueGem;
    
    /** The old value of the value gem. */
    private final ValueNode oldValue;

    /** The new value of the value gem. */
    private final ValueNode newValue;

    /**
     * Constructor for an UndoableValueChangeEdit.
     * @param valueGem ValueGem the displayed value gem in question
     * @param oldValue ValueNode the value of the gem before the event
     */
    UndoableValueChangeEdit(ValueGem valueGem, ValueNode oldValue) {
        this.valueGem = valueGem;
        this.oldValue = oldValue;
        this.newValue = valueGem.getValueNode();
    }
    
    /**
     * A reasonable name for this edit.
     * @return the presentation name for this edit
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_ChangeValue");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        valueGem.changeValue(newValue);
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        valueGem.changeValue(oldValue);
    }
}

