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
 * UndoableAddDisplayedGemEdit.java
 * Creation date: (03/28/2002 1:52:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Point;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * An UndoableEdit used to undo the addition of a displayed gem to the tabletop.
 * Creation date: (03/28/2002 1:52:00 PM)
 * @author Edward Lam
 */
class UndoableAddDisplayedGemEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = 3773383463579562392L;

    /** The tabletop for the gem. */    
    private final TableTop tableTop;

    /** The displayed gem in question. */
    private final DisplayedGem displayedGem;
    
    /** The position of the gem relative to the original origin */
    private Point positionRelativeToOriginalOrigin;

    /** Displayed name */
    private final String displayText;
    /**
     * Constructor for an UndoableAddDisplayedGemEdit.
     * Creation date: (03/28/2002 1:52:00 PM)
     * @param tableTop TableTop the tabletop to which to add.
     * @param displayedGem DisplayedGem the displayed gem to add.
     */
    UndoableAddDisplayedGemEdit(TableTop tableTop, DisplayedGem displayedGem) {
        this.tableTop = tableTop;
        this.displayedGem = displayedGem;
        displayText = displayedGem.getDisplayText();
    }
    
    /**
     * A reasonable name for this edit.
     * Creation date: (03/28/2002 1:58:00 PM)
     * @return the presentation name for this edit
     */
    @Override
    public String getPresentationName() {
        return GemCutterMessages.getString("UndoText_Add", displayText);
    }

    /**
     * Redo the edit.
     * Creation date: (03/28/2002 2:00:00 PM)
     */
    @Override
    public void redo() {
        super.redo();
        java.awt.Point originalOrigin = tableTop.getOriginalOrigin();
        tableTop.addGem(displayedGem, new Point(positionRelativeToOriginalOrigin.x + originalOrigin.x, positionRelativeToOriginalOrigin.y + originalOrigin.y));
    }

    /**
     * Undo the edit.
     * Creation date: (03/28/2002 2:00:00 PM)
     */
    @Override
    public void undo() {
        super.undo();
        Point originalOrigin = tableTop.getOriginalOrigin();
        Point gemLocation = displayedGem.getLocation();
        positionRelativeToOriginalOrigin = new Point(gemLocation.x - originalOrigin.x, gemLocation.y - originalOrigin.y);
        tableTop.deleteGem(displayedGem.getGem());
    }
}

