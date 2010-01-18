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
 * UndoableChangeCollectorDesignMetadataEdit.java
 * Creation date: Nov 1, 2005.
 * By: Joseph Wong
 */
package org.openquark.gems.client;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;

import org.openquark.cal.metadata.FunctionMetadata;


/**
 * An UndoableEdit used to undo a change in the metadata of a collector gem.
 *
 * @author Joseph Wong
 */
public class UndoableChangeCollectorDesignMetadataEdit extends AbstractUndoableEdit {

    private static final long serialVersionUID = -1892209914203570523L;

    /** The tabletop for the gem. */    
    private final TableTop tableTop;

    /** The collector gem in question. */
    private final CollectorGem collectorGem;
    
    /** The old metadata of the collector gem. */
    private final FunctionMetadata oldMetadata;

    /** The new metadata of the collector gem. */
    private final FunctionMetadata newMetadata;

    /**
     * Constructs an instance of this UndoableEdit.
     * 
     * @param tableTop the tabletop to which to add.
     * @param collectorGem the collector gem in question
     * @param oldMetadata the metadata of the gem before the event
     */
    UndoableChangeCollectorDesignMetadataEdit(TableTop tableTop, CollectorGem collectorGem, FunctionMetadata oldMetadata) {
        this.tableTop = tableTop;
        this.collectorGem = collectorGem;
        this.oldMetadata = oldMetadata;
        this.newMetadata = collectorGem.getDesignMetadata();
    }
    
    /**
     * A reasonable name for this edit.
     */
    @Override
    public String getPresentationName() {
        return GemCutter.getResourceString("UndoText_ChangeCollectorMetadata");
    }

    /**
     * Redo the edit.
     */
    @Override
    public void redo() {
        super.redo();
        collectorGem.setDesignMetadata(newMetadata);
    }

    /**
     * Undo the edit.
     */
    @Override
    public void undo() {
        super.undo();
        collectorGem.setDesignMetadata(oldMetadata);
    }

    /**
     * Replace anEdit if possible.
     * @return boolean whether the edit was absorbed.
     */
    @Override
    public boolean replaceEdit(UndoableEdit anEdit){

        // Replace if the other edit is equivalent to this one
        if (!(anEdit instanceof UndoableChangeCollectorDesignMetadataEdit)) {
            return false;
        }
        
        UndoableChangeCollectorDesignMetadataEdit otherEdit = (UndoableChangeCollectorDesignMetadataEdit)anEdit;

        return (otherEdit.tableTop == this.tableTop &&
                otherEdit.collectorGem == this.collectorGem &&
                otherEdit.oldMetadata.equals(this.oldMetadata) &&
                otherEdit.newMetadata.equals(this.newMetadata));
        
    }
}
