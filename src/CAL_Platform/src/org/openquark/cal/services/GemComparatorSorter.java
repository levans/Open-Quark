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
 * GemSorter.java
 * Creation date: Oct 25, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class is an implementation of the GemSorter interface that makes it
 * easy to define custom sorter implementations by providing an entity comparator.
 * @author Edward Lam
 */
public class GemComparatorSorter implements GemSorter {

    /** The Comparator held by this GemSorter. */
    private final Comparator<GemEntity> comparator;

    /**
     * Constructor for a GemSorter
     * @param entityComparator the comparator to be used during the sort.
     */
    public GemComparatorSorter(Comparator<GemEntity> entityComparator) {
        this.comparator = entityComparator;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<GemEntity> getSortedList(Collection<GemEntity> entityCollection) {
        ArrayList<GemEntity> entityList = new ArrayList<GemEntity>(entityCollection);
        Collections.sort(entityList, comparator);
        return entityList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        // In this implementation, the only thing that can change is the comparator.
        if (!(obj instanceof GemSorter)) {
            return false;
        }
        return comparator.equals(((GemComparatorSorter)obj).comparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 17 * comparator.hashCode() + this.getClass().hashCode();
    }

}