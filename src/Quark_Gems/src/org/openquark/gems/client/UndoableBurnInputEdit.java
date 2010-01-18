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
 * UndoableBurnInputEdit.java
 * Creation date: (04/01/2002 11:49:00 AM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * An UndoableEdit used to undo the burning/unburning of a gem input.
 * Creation date: (04/01/2002 11:49:00 AM)
 * @author Edward Lam
 */
class UndoableBurnInputEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -8263604921632642371L;

    /** The tabletop for the gem. */    
    private final TableTop tableTop;

    /** The input in question. */
    private final Gem.PartInput input;
    
    /** The input's old burn status (before the edit). */
    private final AutoburnLogic.BurnStatus oldBurnStatus;

    /** The input's new burn status (after the edit). */
    private final AutoburnLogic.BurnStatus newBurnStatus;

    /**
     * Constructor for an UndoableBurnInputEdit.
     * Creation date: (04/01/2002 11:49:00 AM)
     * @param tableTop TableTop the tabletop to which to add.
     * @param input Gem.PartInput the input in question
     * @param oldBurnStatus TableTop.BurnStatus the old burn status of the input.
     */
    UndoableBurnInputEdit(TableTop tableTop, Gem.PartInput input, AutoburnLogic.BurnStatus oldBurnStatus) {
        this.tableTop = tableTop;
        this.input = input;
        this.oldBurnStatus = oldBurnStatus;
        this.newBurnStatus = tableTop.getBurnManager().getBurnStatus(input);
    }
    
    /**
     * A reasonable name for this edit.
     * Creation date: (04/01/2002 11:49:00 AM)
     */
    @Override
    public String getPresentationName() {
        if (newBurnStatus == AutoburnLogic.BurnStatus.MANUALLY_BURNT || 
                newBurnStatus == AutoburnLogic.BurnStatus.AUTOMATICALLY_BURNT) {
            return GemCutter.getResourceString("UndoText_BurnInput");

        } else if (newBurnStatus == AutoburnLogic.BurnStatus.NOT_BURNT) {
            return GemCutter.getResourceString("UndoText_UnburnInput");

        } else {
            throw new Error("Unknown burn status: " + newBurnStatus);
        }
    }

    /**
     * Redo the edit.
     * Creation date: (04/01/2002 11:49:00 AM)
     */
    @Override
    public void redo() {
        super.redo();
        tableTop.getBurnManager().burnInput(input, newBurnStatus);
    }

    /**
     * Undo the edit.
     * Creation date: (04/01/2002 11:49:00 AM)
     */
    @Override
    public void undo() {
        super.undo();
        tableTop.getBurnManager().burnInput(input, oldBurnStatus);
    }

}

