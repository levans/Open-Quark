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
 * GemEntitySelection.java
 * Creation date: (11/17/00 11:00:50 AM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;

import org.openquark.cal.services.GemEntity;

/**
 * A Transferable class representing a selection of GemEntities.
 * @author Luke Evans
 */
public class GemEntitySelection implements Transferable {

    private final List<GemEntity> entityList;

    private DataFlavor[] dataFlavors = {
            getEntityListDF (),
            DataFlavor.stringFlavor
    };

    /**
     * Construct a GemEntitySelection from a List of Entities.
     */
    public GemEntitySelection(List<GemEntity> entityList) {
        this.entityList = new ArrayList<GemEntity>(entityList);

        // If there is only one entity in the list then we support 
        // the SingleGemEntityDataFlavor.
        if (entityList.size() == 1) {
            DataFlavor[] newFlavors;
            newFlavors = new DataFlavor[dataFlavors.length + 1];
            newFlavors[0] = new SingleGemEntityDataFlavor(entityList.get(0));
            System.arraycopy (dataFlavors, 0, newFlavors, 1, dataFlavors.length);
            dataFlavors = newFlavors;
        }
    }

    /**
     * Return a DataFlavor which represents passing the data as an Entity.
     * @return DataFlavor the entity DataFlavor
     */
    public static DataFlavor getEntityListDF() {
        try {
            return new DataFlavor("object/gementityvector", "object");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isDataFlavorSupported (DataFlavor flavor) {
        for (final DataFlavor dataFlavor : dataFlavors) {
            if (dataFlavor.equals (flavor)) {
                return true;
            }
        }
        
        return false;
    }
    
    public DataFlavor[] getTransferDataFlavors () {
        return dataFlavors.clone();
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    public Object getTransferData(DataFlavor flavour) throws UnsupportedFlavorException {

        if (flavour.equals(getEntityListDF())) {
            return entityList;
        } else if (flavour.equals(SingleGemEntityDataFlavor.getSingleGemEntityDataFlavor())) {
            return entityList.get(0);
        } else if (flavour.equals(DataFlavor.stringFlavor)) {

            // Otherwise it's the string version we're returning
            // We only want the first element in the selection
            GemEntity gemEntity = entityList.get(0);
    
            // return the unqualified name of the gem
            return gemEntity.getName().getUnqualifiedName();
        } else {
            throw  new UnsupportedFlavorException(flavour);
        }
    }
}
