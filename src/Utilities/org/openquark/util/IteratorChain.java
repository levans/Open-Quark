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
 * IteratorChain.java
 * Created: Aug 8, 2007
 * By: Edward Lam
 */

package org.openquark.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Combine the given iterators into one iterator
 * 
 * Originally implemented by Greg as "CombinedIterator".
 * 
 * @author Greg McClement
 * @author Edward Lam
 */
public class IteratorChain<E> implements Iterator<E>{
    
    /** The iterators to be combined. */
    private final ArrayList<Iterator<E>> iterators = new ArrayList<Iterator<E>>();

    public IteratorChain(List<? extends Iterator<E>> iterators) {
        this.iterators.addAll(iterators);
    }
    
    /**
     * Two-argument constructor for an IteratorChain.
     * @param i1 the first iterator in the chain
     * @param i2 the second iterator in the chain.
     */
    public IteratorChain(Iterator<E> i1, Iterator<E> i2){
        iterators.add(i1);
        iterators.add(i2);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        while (true) {
            if (iterators.isEmpty()) {
                return false;
            }
            if (iterators.get(0).hasNext()) {
                return true;
            }
            iterators.remove(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public E next() {
        return iterators.get(0).next();
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
        iterators.get(0).remove();
    }
}
