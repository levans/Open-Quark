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
 * UndoablePasteDisplayedGemsEdit.java
 * Creation date: Jul 8, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.StateEdit;
import javax.swing.undo.UndoableEdit;


/**
 * An UndoableEdit used to undo the pasting of a group of displayed gems to the tabletop.
 * @author Edward Lam
 */
class UndoablePasteDisplayedGemsEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -1752454553736479961L;

    /** The tabletop for the paste. */
    private final TableTop tableTop;

    /** UndoableEdits for the addition of displayed gems.. */
    private final Set<UndoableAddDisplayedGemEdit> addedDisplayedGemEdits;
    
    /** UndoableEdits for the addition of connections. */
    private final Set<UndoableConnectGemsEdit> addedConnectionEdits;

    /** A StateEdit representing the change in the collector arguments and emitter inputs. */
    private final StateEdit collectorArgumentStateEdit;


    /**
     * Constructor for an UndoablePasteDisplayedGemsEdit.
     * @param tableTop TableTop the tabletop to which to paste.
     * @param addedDisplayedGems the displayed gems, in the order in which they were added to the tabletop.
     * @param addedConnections the connections, in the order in which they were added to the tabletop.
     * @param collectorArgumentStateEdit an open StateEdit representing the change in the collector arguments and emitter inputs,
     *   or null if there was no relevant change.
     */
    UndoablePasteDisplayedGemsEdit(TableTop tableTop, Collection<DisplayedGem> addedDisplayedGems, 
                                   Collection<Connection> addedConnections, StateEdit collectorArgumentStateEdit) {
        this.tableTop = tableTop;

        // Get the edit for the changes in arguments.
        collectorArgumentStateEdit.end();
        this.collectorArgumentStateEdit = collectorArgumentStateEdit;
        
        // Get edits for the addition of displayed gems and connections
        this.addedDisplayedGemEdits = new LinkedHashSet<UndoableAddDisplayedGemEdit>();
        for (final DisplayedGem displayedGem : addedDisplayedGems) {
            addedDisplayedGemEdits.add(new UndoableAddDisplayedGemEdit(tableTop, displayedGem));
        }
        
        this.addedConnectionEdits = new LinkedHashSet<UndoableConnectGemsEdit>();
        for (final Connection connection : addedConnections) {
            addedConnectionEdits.add(new UndoableConnectGemsEdit(tableTop, connection));
        }
    }
    
    /**
     * A reasonable name for this edit.
     * @return the presentation name for this edit
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_Paste");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        GemGraph gemGraph = tableTop.getGemGraph();
        gemGraph.setArgumentUpdatingDisabled(true);
        
        // Redo the gem and connection adds.
        for (final UndoableEdit edit : addedDisplayedGemEdits ){
            edit.redo();
        }
        
        for (final UndoableEdit edit : addedConnectionEdits ){
            edit.redo();
        }
        
        // Change the argument state.
        if (collectorArgumentStateEdit != null) {
            collectorArgumentStateEdit.redo();
        }
        
        gemGraph.setArgumentUpdatingDisabled(false);
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        GemGraph gemGraph = tableTop.getGemGraph();
        gemGraph.setArgumentUpdatingDisabled(true);

        // Undo the gem and connection adds, in reverse order.
        List<UndoableConnectGemsEdit> reversedAddConnectionEdits = new ArrayList<UndoableConnectGemsEdit>(addedConnectionEdits);
        Collections.reverse(reversedAddConnectionEdits);
        for (final UndoableEdit edit : reversedAddConnectionEdits ){
            edit.undo();
        }
        
        List<UndoableAddDisplayedGemEdit> reversedAddGemEdits = new ArrayList<UndoableAddDisplayedGemEdit>(addedDisplayedGemEdits);
        Collections.reverse(reversedAddGemEdits);
        for (final UndoableEdit edit : reversedAddGemEdits ){
            edit.undo();
        }
        
        // Change the argument state.
        if (collectorArgumentStateEdit != null) {
            collectorArgumentStateEdit.undo();
        }
        
        gemGraph.setArgumentUpdatingDisabled(false);
    }
}

