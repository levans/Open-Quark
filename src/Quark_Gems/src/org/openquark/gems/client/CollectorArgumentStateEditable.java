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
 * CollectorArgumentStateEditable.java
 * Creation date: May 21, 2004.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.Hashtable;

import javax.swing.undo.StateEditable;


/**
 * A state editable class to represent information about collector arguments and emitter inputs.
 * Information captured includes collector targeting arguments and reflected arguments, and emitter state.
 * @author Edward Lam
 */
public class CollectorArgumentStateEditable implements StateEditable {

    /** The GemGraph whose relevant state will be captured. */
    private final GemGraph gemGraph;
    
    /**
     * Constructor for a CollectorArgumentStateEditable.
     * @param gemGraph the GemGraph whose relevant state will be captured.
     */
    public CollectorArgumentStateEditable(GemGraph gemGraph) {
        this.gemGraph = gemGraph;
    }

    /**
     * {@inheritDoc}
     */
    public void restoreState(Hashtable<?, ?> state) {
        for (final Gem gem : gemGraph.getGems()) {
            if (gem instanceof CollectorGem) {
                CollectorGem collectorGem = (CollectorGem)gem;
                collectorGem.getArgumentStateEditable().restoreState(state);

            } else if (gem instanceof ReflectorGem) {
                ReflectorGem reflectorGem = (ReflectorGem)gem;
                reflectorGem.restoreState(state);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void storeState(Hashtable<Object, Object> state) {
        for (final Gem gem : gemGraph.getGems()) {
            if (gem instanceof CollectorGem) {
                CollectorGem collectorGem = (CollectorGem)gem;
                collectorGem.getArgumentStateEditable().storeState(state);

            } else if (gem instanceof ReflectorGem) {
                ReflectorGem reflectorGem = (ReflectorGem)gem;
                reflectorGem.storeState(state);
            }
        }
    }
}

