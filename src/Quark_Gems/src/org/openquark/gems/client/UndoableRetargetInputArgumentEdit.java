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
 * UndoableRetargetInputArgumentEdit.java
 * Creation date: Feb 27, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;


/**
 * An UndoableEdit used to retarget an input argument to a different collector.
 * @author Edward Lam
 */
public class UndoableRetargetInputArgumentEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -1748866945066989913L;
    private final TableTop tableTop;
    private final Gem.PartInput inputArgument;
    private final CollectorGem newTarget;
    private final int oldArgIndex;
    private final CollectorGem oldTarget;
    
    /**
     * Constructor for an UndoableRetargetInputArgumentEdit.
     * @param tableTop
     * @param inputArgument
     * @param newTarget
     * @param oldArgIndex
     */
    public UndoableRetargetInputArgumentEdit(TableTop tableTop, Gem.PartInput inputArgument, 
                                             CollectorGem oldTarget, CollectorGem newTarget, int oldArgIndex) {
        this.tableTop = tableTop;
        this.inputArgument = inputArgument;
        this.oldTarget = oldTarget;
        this.newTarget = newTarget;
        this.oldArgIndex = oldArgIndex;
    }
    
    /**
     * A reasonable name for this edit.
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_RetargetInput");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        tableTop.retargetInputArgument(inputArgument, newTarget, -1);
        tableTop.getTableTopPanel().repaint();
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        tableTop.retargetInputArgument(inputArgument, oldTarget, oldArgIndex);
        tableTop.getTableTopPanel().repaint();
    }
}
