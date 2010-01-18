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
 * ArraySet.java
 * Created: Nov 2, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A Set whose elements are ordered by the insertion order of elements in the set.
 * This class allows both O(1) membership testing (like HashSet) and O(1) indexing (like ArrayList).
 * 
 * The optional Set interface methods addAll, remove, removeAll and retailAll throw an UnsupportedOperationException.
 * 
 * This Set does not support null elements.
 *
 * Creation date (Nov 2, 2005).
 * @author Bo Ilic
 */
public final class ArraySet<E> extends AbstractSet<E> {
       
    private final transient HashSet<E> set;
    private final ArrayList<E> list;
       
    private class IndexedSetIterator implements ListIterator<E> {
        
        /** 
         * index of the element of list that will be returned by the next call to next().
         * Will satisfy  0 <= index <= list.size() 
         */
        private int index;
        
        private IndexedSetIterator() {
            index = 0;
        }
        
        private IndexedSetIterator(int index) {
            if (index < 0 || index > list.size()) {
                throw new IndexOutOfBoundsException();
            }
                    
            this.index = index;
        }
        
        /** {@inheritDoc} */       
        public boolean hasNext() {
            return index < list.size();
        }
        
        /** {@inheritDoc} */
        public E next() {
            if (index >= list.size()) {
                throw new NoSuchElementException();
            }
            
            E result = list.get(index);
            ++index;
            return result;           
        }
           
        /** {@inheritDoc} */
        public int nextIndex() {            
            return index;
        }
      
        /** {@inheritDoc} */
        public boolean hasPrevious() {           
            return index > 0;
        }

        /** {@inheritDoc} */
        public E previous() {   
            if (index == 0 || list.isEmpty()) {
                throw new NoSuchElementException();
            }
            
            index = index - 1;            
            return list.get(index);
        }
        
        /** {@inheritDoc} */
        public int previousIndex() {            
            return index - 1;
        }
        
        /** {@inheritDoc} */
        public void add(E o) {
            throw new UnsupportedOperationException();            
        }
        
        /** {@inheritDoc} */
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        /** {@inheritDoc} */
        public void set(E o) {
            throw new UnsupportedOperationException();
            
        }        
    }
    
    public ArraySet () {
        set = new HashSet<E>();
        list = new ArrayList<E>();
    }
      
    @Override
    public boolean add(E object) {
        if (object == null) {
            throw new NullPointerException();
        }
        
        if (set.contains(object)) {
            return false;
        }
        
        set.add(object);
        list.add(object);
        return true;
    }
    
    @Override
    public void clear() {
        set.clear();
        list.clear();
    }
    
    @Override
    public int size() {
        return list.size();
    }
    
    /**
     * @return the Iterator returned does not support the optional remove method.
     */
    @Override
    public Iterator<E> iterator() {
        return new IndexedSetIterator();
    }
    
    /**
     * Functions according to the specification of the List.listIterator() interface method.
     * @return the ListIterator returned does not support the optional remove, add and set methods.
     */    
    public ListIterator<E> listIterator() {
        return new IndexedSetIterator();
    }
    
    /**
     * Functions according to the specification of the List.listIterator(int) interface method.
     * @return the ListIterator returned does not support the optional remove, add and set methods.
     */    
    public ListIterator<E> listIterator(int index) { 
        return new IndexedSetIterator(index);
    }
    
    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }
    
    @Override
    public String toString() {
        return list.toString();
    }
   
    public E get(int index) {
        return list.get(index);        
    }
  
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(Object o) {        
        return list.lastIndexOf(o);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();       
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);        
    }
}