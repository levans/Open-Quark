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
 * GemFilter.java
 * Creation date: Oct 25, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A GemFilter filters a collection of entities.
 * @author Edward Lam
 */
public abstract class GemFilter {
    
    /**
     * Get the result of filtering the given collection of entities.
     * @param entityCollection the collection to which this filter should be applied.  
     * This collection will not be modified.
     * @return the filtered list of those entities.
     */
    public List<GemEntity> getFilteredList(Collection<GemEntity> entityCollection) {

        List<GemEntity> entityList = new ArrayList<GemEntity>();

        for (final GemEntity gemEntity : entityCollection) {
            if (select(gemEntity)) {
                entityList.add(gemEntity);
            }
        }

        return entityList;
    }

    /**
     * Decide whether the entity passes the filter.
     * @param gemEntity the entity to analyze.
     * @return boolean whether the entity passes the filter (is accepted).
     */
    public abstract boolean select(GemEntity gemEntity);

}