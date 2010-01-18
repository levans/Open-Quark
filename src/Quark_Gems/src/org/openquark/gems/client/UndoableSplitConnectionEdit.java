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

package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.StateEdit;

/**
 * An UndoableEdit used to undo splitting a connection into a collector/emitter pair from the tabletop.
 * @author Jennifer Chen
 */
public class UndoableSplitConnectionEdit extends AbstractUndoableEdit {
    private static final long serialVersionUID = 4286942729677806605L;

    /** The tabletop for the gem. */    
    private final TableTop tableTop;

    /** The source of the displayed connection in question. */
    private final Gem.PartOutput output;

    /** The destination of the displayed connection in question. */
    private final Gem.PartInput input;

    /** A StateEdit representing the change in the collector arguments and emitter inputs. */
    private final StateEdit collectorArgumentState;
    
    /** The new collector and emitter gems created by the initial splitConnection method*/
    private final DisplayedGem collGem;
    private final DisplayedGem emitGem;
    
    /**
     * Constructor for an UndoableSplitConnectionEdit.
     * @param tableTop TableTop the tabletop from which to remove.
     * @param connection the connection to be replaced
     * @param collectorGem the new collectorGem to be connected to the connection's source part
     * @param emitterGem the new emitterGem to be connected to the connection's destination part
     * @param collectorArgumentState a StateEdit representing the change in the collector arguments and emitter inputs.
     */
    UndoableSplitConnectionEdit(TableTop tableTop, Connection connection, DisplayedGem collectorGem, DisplayedGem emitterGem, StateEdit collectorArgumentState) {
        this.tableTop = tableTop;
        
        this.collectorArgumentState = collectorArgumentState;
        collectorArgumentState.end();

        output = connection.getSource();
        input = connection.getDestination();
        collGem = collectorGem;
        emitGem = emitterGem;
    }
    
    /**
     * A reasonable name for this edit.
     * @return the presentation name for this edit
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_SplitConnection");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        tableTop.getGemGraph().setArgumentUpdatingDisabled(true);
        tableTop.splitConnectionWith(output.getConnection(), collGem, emitGem);
        collectorArgumentState.redo();
        tableTop.getGemGraph().setArgumentUpdatingDisabled(false);
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        tableTop.getGemGraph().setArgumentUpdatingDisabled(true);
        collectorArgumentState.undo();
        tableTop.disconnect(output.getConnection());
        tableTop.disconnect(input.getConnection());
        tableTop.deleteGem(emitGem.getGem());
        tableTop.deleteGem(collGem.getGem());
        tableTop.connect(output, input);
        tableTop.getGemGraph().setArgumentUpdatingDisabled(false);
    }
    
}
