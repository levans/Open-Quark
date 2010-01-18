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
 * UndoableChangeGemLocationEdit.java
 * Creation date: (04/01/2002 2:16:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.Point;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * An UndoableEdit used to undo the change in the location of a gem.
 * Creation date: (04/01/2002 2:16:00 PM)
 * @author Edward Lam
 */
class UndoableChangeGemLocationEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = 8238292260827802613L;

    /** The tabletop for the gem. */    
    private final TableTop tableTop;

    /** The displayed gem in question. */
    private final DisplayedGem displayedGem;

    /** The old location of the displayed gem. */
    private final Point oldLocationRelativeToOriginalOrigin;

    /** The new location of the displayed gem. */
    private final Point newLocationRelativeToOriginalOrigin;

    /**
     * Constructor for an UndoableChangeGemLocationEdit.
     * @param tableTop the table top to which this edit belongs
     * @param displayedGem the displayed gem in question
     * @param oldLocation the former location of the gem
     */
    public UndoableChangeGemLocationEdit(TableTop tableTop, DisplayedGem displayedGem, java.awt.Point oldLocation) {
        this.tableTop = tableTop;
        this.displayedGem = displayedGem;
        Point originalOrigin = tableTop.getOriginalOrigin();
        Point newLocation = displayedGem.getLocation();
        
        this.oldLocationRelativeToOriginalOrigin = new Point(oldLocation.x - originalOrigin.x, oldLocation.y - originalOrigin.y);
        this.newLocationRelativeToOriginalOrigin = new Point(newLocation.x - originalOrigin.x, newLocation.y - originalOrigin.y);
    }
    
    /**
     * A reasonable name for this edit.
     * Creation date: (04/01/2002 2:16:00 PM)
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_MoveGem");
    }

    /**
     * Redo the edit.
     * Creation date: (04/01/2002 2:16:00 PM)
     */
    @Override
    public void redo() {
        super.redo();
        Point originalOrigin = tableTop.getOriginalOrigin();
        Point newLocation = new Point(newLocationRelativeToOriginalOrigin.x + originalOrigin.x, 
                                      newLocationRelativeToOriginalOrigin.y + originalOrigin.y);
        tableTop.changeGemLocation(displayedGem, newLocation);
    }

    /**
     * Undo the edit.
     * Creation date: (04/01/2002 2:16:00 PM)
     */
    @Override
    public void undo() {
        super.undo();
        Point originalOrigin = tableTop.getOriginalOrigin();
        Point oldLocation = new Point(oldLocationRelativeToOriginalOrigin.x + originalOrigin.x, 
                                      oldLocationRelativeToOriginalOrigin.y + originalOrigin.y);
        tableTop.changeGemLocation(displayedGem, oldLocation);
    }
}

