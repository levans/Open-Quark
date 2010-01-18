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
 * PreludeTestsSupport.java
 * Created: Mar 6, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.foreignsupport.module.Prelude_Tests;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.openquark.cal.foreignsupport.module.Prelude.OrderingValue;
import org.openquark.cal.runtime.CalFunction;
import org.openquark.cal.util.EquivalenceRelation;
import org.openquark.cal.util.FixedSizeList;



/**
 * Place to put some Java support for the Prelude_Tests module.
 * @author Bo Ilic
 */
public final class PreludeTestsSupport {
    
    private PreludeTestsSupport() {}
    
    public static final java.util.List<String> nuttyVector = new Vector<String>(Arrays.asList(new String[] {"pecan", "almond", "peanut", "walnut"}));
    public static final java.util.List<String> fruityArrayList = new ArrayList<String>(Arrays.asList(new String[] {"apple", "orange", "pear"}));
    public static final Object[] nutsAndFruits = new java.util.List[] {nuttyVector, fruityArrayList};
    public static final Object[] cityStrings = new String[] {"Vancouver", "Seattle", "Victoria"};
    
    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    
    /**
     * Helper class that contains the foreign implementation for the groupAllBy function:
     * 
     * groupAllBy :: (a -> a -> Boolean) -> [a] -> [[a]];
     * 
     * For example,
     * groupAllBy (\p q -> fst p == fst q) [('a', 1.0), ('a', 2.0), ('b', 3.0), ('c', 4.0), ('a', 5.0), ('b', 6.0)]
     *      == [[('a', 1.0), ('a', 2.0), ('a', 5.0)], [('b', 3.0), ('b', 6.0)], [('c', 4.0)]];
     * 
     * @author Bo Ilic
     */
    public static final class GroupAllBy {
        
        private GroupAllBy() {}
        
        public static <T> java.util.List<java.util.List<T>> groupAllBy(java.util.List<T> list, EquivalenceRelation<T> eqRelation) {
            
            if (eqRelation == null || list == null) {
                throw new NullPointerException();
            }
            
            LinkedList<T> sourceList = new LinkedList<T>(list);
            java.util.List<java.util.List<T>> resultListList = new ArrayList<java.util.List<T>>();
            
            sourceListIteration: while (!sourceList.isEmpty()) {
                
                T elem = sourceList.removeFirst();
                
                for (int i = 0, sizeResultListList = resultListList.size(); i < sizeResultListList; ++i) {
                    
                    java.util.List<T> resultList = resultListList.get(i);
                    
                    if (eqRelation.equivalent(resultList.get(0), elem)) {
                        resultList.add(elem);
                        continue sourceListIteration;
                    }
                }
                
                java.util.List<T> newResultList = new ArrayList<T>();
                newResultList.add(elem);
                resultListList.add(newResultList);
            }
            
            return resultListList;            
        }
    }           
    

    /**
     * A Java singly linked list, similar to the Prelude.List type. This is intended to be used for 
     * performance experiments with the basic evaluation loop of CAL
     * @author Bo Ilic
     */
    public static abstract class List {
        
        private List() {}
        
        public static final class Cons extends List {
            
            private final Object head;
            private final List tail;
            
            public Cons (Object head, List tail) {
                if (tail == null) {
                    throw new NullPointerException("tail cannot be null.");
                }
                this.head = head;
                this.tail = tail;
            } 
            
            public Object getHead() {
                return head;
            }
            
            public List getTail() {
                return tail;
            }
        }
        
        public static final class Nil extends List {
            
            public static final Nil NIL = new Nil ();
            
            private Nil() {}
        }
        
        /**         
         * @param n must be >= 1.
         * @return the List 1, 2, 3, ... n.
         */
        public static List upTo (int n) {
            
            List list = Nil.NIL;
            for (int i = n; i > 0; --i) {
                //we call JavaPrimitives.makeInteger instead of just new Integer() to simulate the caching behavior for
                //ints used by lecc.
                list = new Cons (Integer.valueOf(i), list);     
            }
            
            return list;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public final String toString () {
            StringBuilder result = new StringBuilder("[");
            
            List currentNode = this;
            boolean needsSeparator = false;
            while (currentNode != Nil.NIL) {
                
                if (needsSeparator) {
                    result.append(", ");
                } else {
                    needsSeparator = true;
                }
                
                Cons currentCons = (Cons)currentNode;
                result.append(currentCons.getHead());
                
                currentNode = currentCons.getTail();                
            }
            
            result.append(']');
            return result.toString();            
        }
        
        /**         
         * @return the length of this List. Will be O(n) in the length.
         */
        public final int length () {            
            List currentNode = this;
            int counter = 0;
            while (currentNode != Nil.NIL) {     
                ++counter;                
                currentNode = ((Cons)currentNode).getTail();                
            }
            
            return counter;
        }        
        
    }
    
    /**
     * Test class for scope resolution static errors. Foreign types in CAL should not be able to access non-public classes
     * and methods.
     * @author Bo Ilic
     */
    static class PackageScopeInnerClass {
        
        //not really public, since it is in a package scope class
        public PackageScopeInnerClass() {
        }        
        
        //not really public, since it is in a package scope class
        static public int publicScopeIntField = 1;
        
        //not really public, since it is in a package scope class
        static public int publicScopeAddOne (int i) {
            return i + 1;
        }        
    }
    
    static public class PublicScopeExtendsPackageScopeInnerClass extends PackageScopeInnerClass {
                      
    }    
    
    /**
     * Test class for scope resolution static errors. Foreign types in CAL should not be able to access non-public classes
     * and methods.
     * @author Bo Ilic
     */    
    static public class PublicScopeInnerClass {
        
        static int packageScopeAddOne (int i) {
            return i + 1;
        }
        
        static protected int protectedScopeAddOne (int i) {
            return i + 1;
        }
        
        static public int publicScopeAddOne (int i) {
            return i + 1;
        }        
    }
    
    /**
     * Example of creating a Java Comparator from a CalFunction. This is done directly (and more efficienlty) in the Prelude 
     * via the makeComparator function, but can also be accomplished this way.
     * 
     * @author Bo Ilic
     */
    static public final class CalComparator implements Comparator<Object> {
             
        private final CalFunction function;
        
        public CalComparator(CalFunction function) {
            if (function == null) {
                throw new NullPointerException();
            }
            this.function = function;
        }
       
        public int compare(Object o1, Object o2) {
            
            return ((OrderingValue)function.evaluate(FixedSizeList.make(o1, o2))).toInt();           
        }
    }
    
  
    /**    
     * Similar to String.valueOf, except arrays (at the outermost level) are handled by displaying
     * their components. For example, an int[][] could display as: [[1, 2], [2, 3, 4], [3]].   
     * This is a useful method for debug code involving arrays, as the default implementation of toString on
     * arrays is just Object.toString i.e. shows the class name followed by a reference id.
     * 
     * @param object
     * @return String
     */
    static public final String toStringHandlingArrays(Object object) {
        if (object == null || !object.getClass().isArray()) {
            return String.valueOf(object);
        }
        
        final int length = Array.getLength(object);
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(toStringHandlingArrays(Array.get(object, i)));                       
        }
        result.append("]");
        
        return result.toString();
        
    }
    
    static public char[] charPrimArray() {
        return new char[] {'p', 'r', 'i', 'm'};
    }
    
    static public Character[] charReferenceArray() {
        return new Character[] {new Character('r'), new Character('e'), new Character('f')};
    }
    
    static public Set<Character> charSet() {
        Set<Character> s = new LinkedHashSet<Character>();
        s.add(new Character('s'));
        s.add(new Character('e'));
        s.add(new Character('t'));
        return s;
    }
    
    static public Iterator<Character> charIterator() {
        Set<Character> s = new LinkedHashSet<Character>();
        s.add(new Character('i'));
        s.add(new Character('t'));       
        return s.iterator();
    }   

    static public Enumeration<Character> charEnumeration() {
        Set<Character> s = new LinkedHashSet<Character>();
        s.add(new Character('e'));
        s.add(new Character('n'));
        s.add(new Character('u'));
        s.add(new Character('m'));
        
        return Collections.enumeration(s);
    }   
    
    static public final class Counter {
        
        private Counter() {}
        
        private final static ThreadLocal<Integer> value = new ThreadLocal<Integer>();
        
        public static int increment() {
            int returnValue = value.get().intValue() + 1;
            value.set(Integer.valueOf(returnValue));
            return returnValue;
        }
        
        public static void reset() {
            value.set(Integer.valueOf(0));
        }
        
    }
}
