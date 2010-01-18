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
 * PreludeSupport.java
 * Created: May 13, 2002
 * By: Bo Ilic
 */
package org.openquark.cal.internal.foreignsupport.module.Prelude;

import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Holds some helper functions, fields and method intended to support the Prelude module.
 * 
 * Note that even though this class, and various methods in it are public, they are part of the
 * implementation of the compiler, and clients should not make use of them directly.
 * 
 * Creation date: (May 13, 2002)
 * @author Bo Ilic
 */
public final class PreludeSupport {
    
    private PreludeSupport () {}
          
            
    /**
     * Converts an Enumeration to an Iterator in a lazy fashion. It is surprising that this is not a standard Java library function.
     * java.util.Collections.list(Enumeration) non-lazily evaluates the enumeration, so is not suitable.
     * @param e
     * @return Iterator
     */
    public static <T> Iterator<T> enumerationToIterator(final Enumeration<T> e) {
        
        if (e == null) {
            throw new NullPointerException("the Enumeration e cannot be null");
        }
        
        final class IteratorAdapter implements Iterator<T> {

            public boolean hasNext() {
                return e.hasMoreElements();
            }

            public T next() {
                return e.nextElement();
            }

            public void remove() {
                throw new UnsupportedOperationException();
                
            }                        
        }
        
        return new IteratorAdapter();
    }
    
    /**
     * Converts an Iterator to an Enumeration in a lazy fashion. It is surprising that this is not a standard Java library function.     
     * @param it
     * @return Enumeration
     */
    public static <T> Enumeration<T> iteratorToEnumeration(final Iterator<T> it) {

        if (it == null) {
            throw new NullPointerException("the Iterator it cannot be null");
        }

        final class EnumerationAdapter implements Enumeration<T> {

            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public T nextElement() {
                return it.next();
            }
                      
        }

        return new EnumerationAdapter();
    } 
    
    /**    
     * @param object
     * @return true if object is a primitive or reference array
     */
    public static boolean isArray(final Object object) {
        return object != null && object.getClass().isArray();
    }
    
    /**
     * Converts an Object which is in fact a Java reference array or a Java primitive array to an Iterator in a lazy fashion.
     * Primitive arrays have their elements auto-boxed when returned as values of the iteration.
     * It is surprising that this is not a standard Java library function.
     * 
     * @param array
     * @return Iterator
     */
    public static Iterator<?> arrayToIterator(final Object array) {
        
        if (array == null) {
            throw new NullPointerException("array cannot be null");
        }
                
        final int len = Array.getLength(array);
        
        final class IteratorAdapter implements Iterator<Object> {
            
            int curPos = 0;

            public boolean hasNext() {
                return curPos < len;
            }

            public Object next() {
                final Object value = Array.get(array, curPos);
                ++curPos;
                return value;              
            }

            public void remove() {
                throw new UnsupportedOperationException();

            }                        
        }
        
        return new IteratorAdapter();
    }    
}

