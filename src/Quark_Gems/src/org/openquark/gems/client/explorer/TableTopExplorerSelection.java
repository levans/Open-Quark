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
 * ExplorerSelection.java
 * Creation date: Jan 6th 2002
 * By: Ken Wong
 */
package org.openquark.gems.client.explorer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Arrays;

import org.openquark.gems.client.Gem;


/**
 * This class represents the transferable that will be used to transfer data between
 * one node of the TableTopExplorer to another
 * 
 * The Dataflavour supported is the ExplorerSingleGemDataFlavor which allows us to do 
 * type evaluations
 *
 * @author Ken Wong
 * Creation date: Jan 6th
 */
public class TableTopExplorerSelection implements Transferable {
    
    private DataFlavor[] dataFlavors;
    private Gem gem;
    
    /**
     * Constructor for TableTopExplorerSelection.
     * @param gem
     * @param tableTopExplorer
     */
    public TableTopExplorerSelection(Gem gem, TableTopExplorer tableTopExplorer) {
        DataFlavor[] dataFlavors = {new TableTopExplorerSingleGemDataFlavor(gem, tableTopExplorer)};
        this.dataFlavors = dataFlavors;
        this.gem = gem;
    }
    
    /**
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    public DataFlavor[] getTransferDataFlavors() {
        return dataFlavors.clone();
    }

    /**
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(DataFlavor)
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        // Temp: Only one dataflavor supported right now
        return Arrays.asList(dataFlavors).contains(flavor);
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(DataFlavor)
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(TableTopExplorerSingleGemDataFlavor.getSingleGemDataFlavor())) {
            return gem;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

}
