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
 * UndoableRetargetCollectorEdit.java
 * Creation date: Jun 22, 2005.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;


/**
 * An UndoableEdit used to retarget a collector to a different collector.
 * @author Edward Lam
 */
public class UndoableRetargetCollectorEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = 4618099682492678910L;
    private final TableTop tableTop;
    private final CollectorGem collectorToRetarget;
    private final CollectorGem newTarget;
    private final CollectorGem oldTarget;
    
    /**
     * Constructor for an UndoableRetargetInputArgumentEdit.
     * @param tableTop
     * @param collectorToRetarget
     * @param oldTarget
     * @param newTarget
     */
    public UndoableRetargetCollectorEdit(TableTop tableTop, CollectorGem collectorToRetarget, 
                                             CollectorGem oldTarget, CollectorGem newTarget) {
        this.tableTop = tableTop;
        this.collectorToRetarget = collectorToRetarget;
        this.oldTarget = oldTarget;
        this.newTarget = newTarget;
    }
    
    /**
     * A reasonable name for this edit.
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_RetargetCollector");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        collectorToRetarget.setTargetCollector(newTarget);
        tableTop.getTableTopPanel().repaint(tableTop.getDisplayedGem(collectorToRetarget).getBounds());
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        collectorToRetarget.setTargetCollector(oldTarget);
        tableTop.getTableTopPanel().repaint(tableTop.getDisplayedGem(collectorToRetarget).getBounds());
    }
}
