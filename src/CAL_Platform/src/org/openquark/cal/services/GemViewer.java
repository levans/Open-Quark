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
 * GemViewer.java
 * Creation date: Oct 29, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A GemViewer is a viewer on a collection of GemEntities.
 * It supports custom sorting and filtering to alter the view on the given collection.
 * @author Edward Lam
 */
public class GemViewer {

    /** The sorter to apply. */
    private GemSorter sorter = null;
    
    /** Filters to apply. */
    private final List<GemFilter> filters = new ArrayList<GemFilter>();

    /**
     * Add the given filter to this viewer.
     * @param filterToAdd the filter to add.
     * @return boolean whether the operation was successful.
     */
    public boolean addFilter(GemFilter filterToAdd) {
        return filters.add(filterToAdd);
    }

    /**
     * Remove the given filter from this viewer.
     * @param filterToRemove the filter to remove.
     * @return boolean whether the operation was successful.
     */
    public boolean removeFilter(GemFilter filterToRemove) {
        return filters.remove(filterToRemove);
    }
    
    /**
     * Remove all filters from this viewer.
     */
    public void removeAllFilters() {
        filters.clear();
    }

    /**
     * Set this viewer's sorter.  Null turns sorting off.
     * @param newSorter the new sorter.  Null if none.
     */
    public void setSorter(GemSorter newSorter) {
        this.sorter = newSorter;
    }

    /**
     * View a collection of GemEntities through this viewer.
     * @param gemEntities the GemEntities to view
     * @return List the result of applying the viewer to the collection.
     */
    public List<GemEntity> view(Collection<GemEntity> gemEntities) {

        List<GemEntity> result = new ArrayList<GemEntity>(gemEntities);

        // Apply any filters.
        for (final GemFilter filter : filters) {
            result = filter.getFilteredList(result);
        }

        // Apply the sorter if any
        if (sorter != null) {
            result = sorter.getSortedList(result);
        }

        return result;
    }
}
