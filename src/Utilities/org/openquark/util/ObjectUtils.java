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
 * ObjectUtils.java
 * Created: Aug 3, 2004
 * By: Kevin Sit
 */
package org.openquark.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A collection of <code>String</code> and generic container utility methods. 
 */
public final class ObjectUtils {

    private ObjectUtils() {
    }
    
    /**
     * Convert the given value to a string value, or simply return an empty string
     * if the specified value is <code>null</code>.
     * @param value
     * @return String
     */
    public static String toString(Object value) {
        return value == null ? "" : value.toString();
    }
    
    /**
     * Convert the given value to a string value, or simply return the default string
     * if the specified value is <code>null</code>.
     * @param value
     * @param defaultString 
     * @return String
     */
    public static String toString(Object value, String defaultString) {
        return value == null ? defaultString : value.toString();
    }
    
    /**
     * Minor helper method that checks if 2 objects are either both null or equal using the equals()
     * method.  This makes long if statements (such as those found in equals() methods) easier to read. 
     * @param a
     * @param b
     * @return True if both a and b are null or if a.equals(b).  Otherwise false is returned.
     */
    public static boolean equals(Object a, Object b) {
        return (a == null ? b == null : a.equals(b));
    }
    
    /**
     * Minor helper method that checks if 2 objects are of the same class.
     * If either is null we return false;
     * @param a
     * @param b
     * @return True if both objects are non - null and of the same type
     */
    public static boolean sameClass(Object a, Object b) {
        if (a == null || b == null)
            return false;
        return a.getClass() == b.getClass();
    }
    
    /**
     * Compares the elements of two lists together using just the public List interface.  This
     * allows comparison of different types of lists or of lists that do not implement the
     * equals() method.
     * @param a
     * @param b
     * @return True if a and b have the same number of elements in the same order and the elements
     * are all equal.
     */
    public static <T> boolean equalsLists(List<T> a, List<T> b) {
        
        // Do a quick check to see if a and b are the same object
        if (a == b) {
            return true;
        }
                
        // Ensure that both lists are the same size
        if (a.size() != b.size())
            return false;
        
        // Compare each element in turn and return false if any of them are different.
        for (Iterator<T> iterA = a.iterator(), iterB = b.iterator(); iterA.hasNext();) {
            T elemA = iterA.next();
            T elemB = iterB.next();
            if (!equals(elemA, elemB)) {
                return false;
            }
        }
        
        // No mismatches were found, the list elements are equals
        return true;
    }
    
    /**
     * Compares the elements of two collections using Object.equals on the elements.  
     * Differences in the element order are not considered.
     * @param a
     * @param b
     * @return True if a and b have the same number of elements and the elements
     *         are all equal.
     */
    public static <T> boolean equalsUnorderedCollections(Collection<T> a, Collection<T> b) {
        
        // Do a quick check to see if a and b are the same object
        if (a == b) {
            return true;
        }
        
        // Ensure that both lists are the same size
        if (a.size() != b.size())
            return false;
        
        // Compare each element in turn and return false if any of them are different.
        List<T> _b = new ArrayList<T>(b);
        
        for (final T elemA : a) {
            
            boolean foundMatching = false;    
            
            for (Iterator<T> iterB = _b.iterator(); iterB.hasNext(); ) {
                T elemB = iterB.next();
                if (equals(elemA, elemB)) {
                    iterB.remove();
                    foundMatching = true;
                    break;
                }
            }
            
            // We have walked past the end of the second list and still
            // cannot find a matching item.
            if (!foundMatching) {
                return false;
            }
        }
        
        // No mismatches were found, the list elements are equals
        return _b.isEmpty();
    }
    
    /**
     * Compares the elements of two maps together using just the public Map
     * interface. The keys and values are compared using the normal equals, so
     * if they happen to be lists or maps then they will not be compared using
     * the special forms of this method that are in this class.
     * 
     * @param a
     * @param b
     * @return True if a and b have the same number of elements and the keys and
     *         values for each element are equal.
     */
    public static <K, V> boolean equalsMaps(Map<K, V> a, Map<K, V> b) {
        
        // Do a quick check to see if a and b are the same object
        if (a == b) {
            return true;
        }
        // Ensure that both maps are the same size
        if (a.size() != b.size()) {
            return false;
        }
        
        // Compare each key/value pair in turn and return false if any of them are different.
        for (final Map.Entry<K, V> mapEntry : a.entrySet()) {            
            K keyA = mapEntry.getKey();
            V valueA = mapEntry.getValue();
            V valueB = b.get(keyA);
            if (!equals(valueA, valueB)) {
                return false;
            }
        }
        
        // No mismatches were found, the map elements are equals
        return true;
    }
    
    /**
     * Filter the collection, removing all items the filter declares as false.
     * @param collection must be modifiable
     * @param filter (null is a no-op)
     */
    public static <T> void filterCollection(Collection<T> collection, Filter<T> filter) {
        if (filter != null) {
            
            for (Iterator<T> iter = collection.iterator(); iter.hasNext(); ) {
                T o = iter.next();
                if (!filter.filter(o))
                    iter.remove();
            }
        }
    }
    
    /**
     * Join a collection of items into a string.
     * @param collection A collection of items to join
     * @param joinString The string to place between the items
     * @return A string consisting of the string representations of the items in
     *         collection, concatenated, with joinString interspersed between them.
     */
    public static <T> String join(Collection<T> collection, String joinString) {
        
        StringBuilder result = new StringBuilder();
        
        boolean isFirst = true;
        for (final T elem : collection) {
            if (!isFirst) {
                result.append(joinString);
            } else {
                isFirst = false;
            }
            
            result.append(elem.toString());
        }
        
        return result.toString();
    }
}
