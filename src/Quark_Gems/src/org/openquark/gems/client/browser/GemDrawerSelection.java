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
 * GemDrawerSelection.java
 * Creation date: Apr 05, 2004
 * By: Iulian Radu
 */

package org.openquark.gems.client.browser;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import org.openquark.cal.compiler.ModuleName;


/**
 * Transferable class for a gem drawer. It holds on to 
 * the string representation of the drawer name.
 * 
 * @author Iulian Radu
 * Creation date: Apr 05, 2004
 */
public class GemDrawerSelection extends DataFlavor implements Transferable {
    
    // Type info for the gem drawer (module) being transfered
    private final ModuleName gemDrawer;

    private final DataFlavor[] dataFlavors = {
        getGemDrawerDataFlavor(),
        DataFlavor.stringFlavor
    };
    
    /**
     * Constructor 
     * @param gemDrawer
     */
    public GemDrawerSelection(ModuleName gemDrawer) {
        super("object/gemdrawer", "object");
        this.gemDrawer = gemDrawer;
    }
    
    /**
     * Returns the associated gem drawer
     * @return boolean
     */
    public ModuleName getGemDrawer() {
        return gemDrawer;
    }
    
    /**
     * Returns the DataFlavor that 'equals' dataflavors of this class
     * @return DataFlavor
     */
    public static DataFlavor getGemDrawerDataFlavor() {
        return new DataFlavor("object/gemdrawer", "object");
    }
    
    /**
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
     */
    public boolean isDataFlavorSupported (DataFlavor flavor) {
        return (getGemDrawerDataFlavor().equals (flavor));
    }
    
    /**
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    public DataFlavor[] getTransferDataFlavors () {
        return dataFlavors.clone();
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    public Object getTransferData(DataFlavor flavour) throws UnsupportedFlavorException {

        if (flavour.equals(getGemDrawerDataFlavor())) {
            return gemDrawer;
        } else if (flavour.equals(DataFlavor.stringFlavor)) {
            return gemDrawer;  
        } else {
            throw  new UnsupportedFlavorException(flavour);
        }
    }
}
