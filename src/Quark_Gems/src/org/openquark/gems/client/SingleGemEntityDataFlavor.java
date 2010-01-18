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
 * SingleGemEntityDataFlavor.java
 * Creation date: Jan 8th 2002
 * By: Ken Wong
 */

package org.openquark.gems.client;

import java.awt.datatransfer.DataFlavor;

import org.openquark.cal.services.GemEntity;

/**
 * The SingleGemEntityDataFlavor is used to describe the data that is carried
 * in the GemEntitySelection transferables. Note that this dataflavor is only
 * supported by the transferable iff there is only one GemEntity selected. In
 * such a case, an instance of this class will be created, and the ability to
 * type check will be available
 * 
 * @author Ken Wong
 */
public class SingleGemEntityDataFlavor extends DataFlavor {
    
    // The entity that we will be transferring
    private final GemEntity gemEntity;

    /**
     * Constructor for SingleGemEntityDataFlavor.
     * @param gemEntity
     */
    public SingleGemEntityDataFlavor(GemEntity gemEntity) {
        super("object/gementity", "object");
        this.gemEntity = gemEntity;
    }
    
    /**
     * Returns the associated entity
     * @return boolean
     */
    public GemEntity getGemEntity() {
        return gemEntity;
    }
    
    /**
     * Returns the DataFlavor that 'equals' dataflavors of this class
     * @return DataFlavor
     */
    public static DataFlavor getSingleGemEntityDataFlavor() {
        return new DataFlavor("object/gementity", "object");
    }
}
