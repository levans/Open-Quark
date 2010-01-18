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
 * UndoableConnectGemsEdit.java
 * Creation date: (04/01/2002 10:27:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.StateEdit;

/**
 * An UndoableEdit used to undo the addition of a connection to the tabletop.
 * Creation date: (04/01/2002 10:27:00 PM)
 * @author Edward Lam
 */
class UndoableConnectGemsEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = 7153895958220058625L;

    /** The tabletop for the gem. */    
    private final TableTop tableTop;

    /** The gem which has the output of the gem in question. */
    private final Gem sourceGem;

    /** The gem which has the connection input. */
    private final Gem destinationGem;
    
    /** The index of the input to which the connection is connected. */
    private final int destinationIndex;

    /** A StateEdit representing the change in the collector arguments and emitter inputs. */
    private final StateEdit collectorArgumentState;

    /**
     * Constructor for an UndoableAddDisplayedConnectionEdit.
     * @param tableTop the tabletop to which to add.
     * @param connection the displayed connection to add.
     */
    UndoableConnectGemsEdit(TableTop tableTop, Connection connection) {
        this(tableTop, connection, null);
    }

    /**
     * Constructor for an UndoableAddDisplayedConnectionEdit.
     * @param tableTop the tabletop to which to add.
     * @param connection the displayed connection to add.
     * @param collectorArgumentState a StateEdit representing the change in the collector arguments and emitter inputs, or null if
     *   there is no relevant change.
     */
    UndoableConnectGemsEdit(TableTop tableTop, Connection connection, StateEdit collectorArgumentState) {
        this.tableTop = tableTop;
        sourceGem = connection.getSource().getGem();
        destinationGem = connection.getDestination().getGem();
        destinationIndex = connection.getDestination().getInputNum();
        this.collectorArgumentState = collectorArgumentState;
        if (collectorArgumentState != null) {
            collectorArgumentState.end();
        }
    }
    
    /**
     * A reasonable name for this edit.
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_ConnectGems");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        tableTop.getGemGraph().setArgumentUpdatingDisabled(true);
        tableTop.connect(sourceGem.getOutputPart(), destinationGem.getInputPart(destinationIndex));
        if (collectorArgumentState != null) {
            collectorArgumentState.redo();
        }
        tableTop.getGemGraph().setArgumentUpdatingDisabled(false);
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        tableTop.getGemGraph().setArgumentUpdatingDisabled(true);
        if (collectorArgumentState != null) {
            collectorArgumentState.undo();
        }
        tableTop.disconnect(sourceGem.getOutputPart().getConnection());
        tableTop.getGemGraph().setArgumentUpdatingDisabled(false);
    }
}
