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
 * EmptyIterator.java
 * Creation date: (10/10/2000 4:12:18 PM)
 * By: Raymond Cypher
 */
package org.openquark.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An Iterator that is empty.
 * @author Raymond Cypher
 */
public final class EmptyIterator<T> implements Iterator<T> {
    
    private static final Iterator<Object> INSTANCE = new EmptyIterator<Object>();
    
    /**
     * Private constructor - not intended to be instantiated.
     */
    private EmptyIterator() {
        super();
    }

    /**
     * @return an EmptyIterator (immutable).
     */
    @SuppressWarnings("unchecked")
    public static final <T> EmptyIterator<T> emptyIterator() {
        return (EmptyIterator<T>)INSTANCE;
    }

    /**
     * @return <tt>false</tt>.
     */
    public boolean hasNext() {
        return false;
    }
    
    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public T next() {
        throw new NoSuchElementException ();
    }

    /**
     * 
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *        operation is not supported by this Iterator.
     
     * @exception IllegalStateException if the <tt>next</tt> method has not
     *        yet been called, or the <tt>remove</tt> method has already
     *        been called after the last call to the <tt>next</tt>
     *        method.
     */
    public void remove() {
        throw new IllegalStateException ();
    }
}
