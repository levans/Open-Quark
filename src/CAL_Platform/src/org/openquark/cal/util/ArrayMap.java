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
 * ArrayMap.java
 * Creation date: (Nov 6, 2002)
 * By: Bo Ilic
 */

package org.openquark.cal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A map whose elements are ordered by the insertion order of elements in the map.
 * This class allows both O(1) map lookup (like HashMap) and O(1) indexing (like ArrayList).
 * 
 * <P>The SequencedHashMap at http://jakarta.apache.org/commons/collections/ provides a similar interface
 * but doesn't support O(1) integer indexing.
 * 
 * <P>The LinkedHashMap of jdk 1.4 is almost as good, but we want to be able to conveniently define a type
 * safe iterator and we don't need to be able to remove elements from the map, so that this class does the job.
 * 
 * <P>Creation date (Nov 6, 2002).
 * @author Bo Ilic
 */
public final class ArrayMap<K, V> implements Map<K, V> {
    
    /** Map of KeyType -> ValueType. */
    private final transient HashMap<K, V> map;
    
    /** List of KeyType */
    private final ArrayList<K> list;
    
    public ArrayMap () {
        map = new LinkedHashMap<K, V>();
        list = new ArrayList<K>();
    }
    
    /**
     * Adds the (key, value) pair to the map, provided that the map doesn't already contain the key.
     * @param key
     * @param value
     * @return Object null (will throw and IllegalArgumentException if the key already exists in the map.
     * @throws NullPointerException if either argument is null.
     * {@inheritDoc}
     */
    public V put(K key, V value) {
            
        if (key == null) {
            throw new NullPointerException ("The argument 'key' can't be null.");
        }
        
        if (value == null) {
            throw new NullPointerException ("The argument 'value' can't be null.");
        }
                              
        if (map.containsKey(key)) {
            throw new IllegalArgumentException("The map already contains the key " + key);
        }
            
        map.put(key, value);
        list.add(key);
        
        return null;        
    }
    
    /** {@inheritDoc} */
    public V get(Object key) {
        return map.get(key);
    }
        
    public K getNthKey(int index) {
        return list.get(index);
    }
    
    public V getNthValue(int index) { 
        return map.get(list.get(index));
    } 
    
    /** {@inheritDoc} */
    public int size() {
        return list.size();
    }
    
    /** {@inheritDoc} */
    public boolean containsKey (Object key) {
        return map.containsKey(key);   
    }
    
    @Override
    public String toString() {
        //note: we don't just delegate to map.toString() because we want the ordering to
        //be that of list (without the performance cost of making map into a LinkedHashMap).
        
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0, size = size(); i < size; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            
            sb.append(getNthKey(i)).append("=").append(getNthValue(i));            
        }
        sb.append("}");
      
        return sb.toString();
    }

    /** {@inheritDoc} */
    public void clear() {
        map.clear();
        list.clear();               
    }

    /** {@inheritDoc} */
    public boolean isEmpty() {
        return list.isEmpty();       
    }

    /** {@inheritDoc} */
    public boolean containsValue(Object value) {
        return list.contains(value);       
    }

    /** {@inheritDoc} */
    public Collection<V> values() {        
        return Collections.unmodifiableCollection(map.values());
    }

    /** {@inheritDoc} */
    public void putAll(Map<? extends K,? extends V> t) {
        throw new UnsupportedOperationException();
        
    }

    /** {@inheritDoc} */
    public Set<Entry<K,V>> entrySet() {        
        return Collections.unmodifiableSet(map.entrySet());
    }

    /** {@inheritDoc} */
    public Set<K> keySet() {       
        return Collections.unmodifiableSet(map.keySet());
    }

    /** {@inheritDoc} */
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }
}
