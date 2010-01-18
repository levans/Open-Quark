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
 * GemContext.java
 * Creation date: Dec 5, 2003.
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A GemContext represents the context in which a gem exists.
 * It can be used during saving/loading to uniquely identify a gem within a gem graph when there are
 *   other gems with otherwise the same identifying characteristics (eg. value gems with identical values).
 * 
 * @author Edward Lam
 */
public class GemContext {
    
    /** By default, identifiers are ints. */
    private int nextIdentifier = 0;
    
    /** Map from gem to its associated identifier. */
    private final Map<Gem, String> gemToIdentifierMap = new HashMap<Gem, String>();
    
    /** Map from identifier to its associated gem. */
    private final Map<String, Gem> identifierToGemMap = new HashMap<String, Gem>();
    
    /** The collector gems in this context. */
    private final Set<CollectorGem> collectorSet = new HashSet<CollectorGem>();
    
    public GemContext() {
    }
    
    /**
     * Get the unique identifier for the given gem within this context.
     * @param gem the gem in question.
     * @param createIfAbsent whether to generate a new identifier if the gem is not already known to this context 
     *   (by previous queries with a value of true.).
     * @return a unique identifier for the given gem, or null if createIfAbsent is false and the gem is not known to this context.
     */
    public String getIdentifier(Gem gem, boolean createIfAbsent) {

        // Find the corresponding identifier if any..
        String gemIdentifier = gemToIdentifierMap.get(gem);
        
        // Handle the case where the identifier doesn't already exist.
        if (gemIdentifier == null) {
            if (!createIfAbsent) {
                return null;
            }
            
            // Add the identifier to the map
            gemIdentifier = String.valueOf(nextIdentifier);
            addGem(gem, gemIdentifier);
            
            // Increment the identifier index.
            nextIdentifier++;
        }

        return gemIdentifier;
    }
    
    /**
     * Get the Gem identified by a given unique identifier in this context.
     * @param identifier the identifier for the given gem
     * @return Gem the corresponding gem, or null if a gem corresponding to the identifier does not exist in this context.
     */
    public Gem getGem(String identifier) {
        return identifierToGemMap.get(identifier);
    }
    
    /**
     * Get the collector gem with the given name.
     * @param unqualifiedCollectorName the name of the collector.
     * @return the collector gem within this context with the given name, or null if none exists.
     */
    public CollectorGem getCollector(String unqualifiedCollectorName) {
        for (final CollectorGem collectorGem : collectorSet) {
            if (collectorGem.getUnqualifiedName().equals(unqualifiedCollectorName)) {
                return collectorGem;
            }
        }
        return null;
    }
    
    /**
     * Add a gem to this context.  Any previous gem associated with the given identifier will be lost.
     * @param gem the gem to add.
     * @param identifier the gem's unique identifier within this context.
     */
    public void addGem(Gem gem, String identifier) {
        Gem oldGem = identifierToGemMap.put(identifier, gem);
        gemToIdentifierMap.remove(oldGem);
        gemToIdentifierMap.put(gem, identifier);

        // If the gem is a collector, add to the nameToCollectorMap.
        collectorSet.remove(oldGem);
        if (gem instanceof CollectorGem) {
            collectorSet.add((CollectorGem)gem);
        }
    }
}
