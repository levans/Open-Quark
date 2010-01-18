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
 * SingleInputDataFlavor.java
 * Creation date: March 25th 2003
 * By: Richard Webster
 */
package org.openquark.gems.client;

import java.awt.datatransfer.DataFlavor;


/**
 * The SingleInputDataFlavor is used to describe the data that is carried in the InputTransferable transferables.
 * Note that this dataflavor is only supported by the transferables iff there is only one input selected. In such a case,
 * an instance of this class will be created, and the ability to check target validity will be available.
 * 
 * @author Richard Webster
 * @see InputTransferable
 */
public class SingleInputDataFlavor extends DataFlavor {
    
    /** The MIME type associated with this data flavor. */
    public static final String MIME_TYPE = "object/singleinput";
    
    /** The gem input that is being transfered. */
    private final Gem.PartInput input;

    /**
     * Constructor for SingleInputDataFlavor
     * @param input
     */
    public SingleInputDataFlavor(Gem.PartInput input) {
        super(MIME_TYPE, "object");
        this.input = input;
    }

    /**
     * Returns the associated gem input
     */
    public Gem.PartInput getPartInput() {
        return input;
    }

    /**
     * Return whether the input in this transferable can target the given collector.
     * @param collectorGem the collector in question.
     * @return whether the input within this transferable can target the given collector.
     */
    public boolean canTarget(CollectorGem collectorGem) {
        return canTarget(input, collectorGem);
    }

    /**
     * Return whether the given input can target the given collector.
     * @param input the input to target.
     * @param collectorGem the collector in question.
     * @return whether the given input can target the given collector.
     */
    public static boolean canTarget(Gem.PartInput input, CollectorGem collectorGem) {
        // Get the collector at the root of the inputs tree.
        CollectorGem rootCollectorGem = input.getGem().getRootCollectorGem();
        
        // If there is no such collector, the input can't target any collector.
        if (rootCollectorGem == null) {
            return false;
        }
        
        // True if the collector is the root, or if it encloses the root.
        return collectorGem.enclosesCollector(rootCollectorGem);
    }

    /**
     * Returns a dataflavor that 'equals' an instance of this class
     * @return DataFlavor
     */
    public static DataFlavor getSingleInputDataFlavor() {
        return new DataFlavor(MIME_TYPE, "object");
    }
}
