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
 * TableTopExplorerSingleGemDataFlavor.java
 * Creation date: Jan 8th 2002
 * By: Ken Wong
 */

package org.openquark.gems.client.explorer;

import java.awt.datatransfer.DataFlavor;

import org.openquark.gems.client.AutoburnLogic;
import org.openquark.gems.client.Gem;


/**
 * The TableTopExplorerSingleGemDataFlavor is used to describe the data that is carried in the TableTopExplorerSelection transferables.
 * Note that this dataflavor is only supported by the transferables iff there is only one gem selected. In such a case,
 * an instance of this class will be created, and the ability to type check and check connection validity will be
 * available
 * 
 * @author Ken Wong
 * @see org.openquark.gems.client.explorer.TableTopExplorerSelection
 * Creation Date: Jan 8th 2003
 */
public class TableTopExplorerSingleGemDataFlavor extends DataFlavor {
    
    // The gem that is being transfered
    private Gem gem;
    
    private TableTopExplorer tableTopExplorer;

    /**
     * Constructor for TableTopExplorerSingleGemDataFlavor
     * @param gem
     * @param tableTopExplorer
     */
    public TableTopExplorerSingleGemDataFlavor(Gem gem, TableTopExplorer tableTopExplorer) {
        super("object/singlegem", "object");
        this.gem = gem;
        this.tableTopExplorer = tableTopExplorer;
    }

    /**
     * Returns the associated gem
     */
    public Gem getGem() {
        return gem;
    }

    /**
     * Checks whether the connection is okay
     * @param input
     * @return AutoburnLogic.AutoburnUnifyStatus
     */
    public AutoburnLogic.AutoburnUnifyStatus canConnectTo(Gem.PartInput input) {
        return tableTopExplorer.getExplorerOwner().canConnect(gem.getOutputPart(), input);
    }
    
    /**
     * Returns a dataflavor that 'equals' an instance of this class
     * @return DataFlavor
     */
    public static DataFlavor getSingleGemDataFlavor() {
        return new DataFlavor("object/singlegem", "object");
    }
}
