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
 * ArrayStack.java
 * Created: Aug 2, 2007
 * By: Bo Ilic
 */

package org.openquark.cal.util;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * A simple stack implementation based on {@link ArrayList}. It is faster for 
 * single-threaded use than {@link java.util.Stack} which is thread-safe.
 * 
 * @author Joseph Wong, Bo Ilic
 */
public class ArrayStack<E> extends ArrayList<E> {
    
    private static final long serialVersionUID = 8626939976311092253L;

    protected ArrayStack() {
    }
    
    public static <E> ArrayStack<E> make() {
        return new ArrayStack<E>();
    }

    /**
     * Unlike java.util.Stack.push, this method does not return its argument. 
     * This is a small micro-optimization.
     *          
     * @param e adds the element 'e' to the top of the stack 
     */
    public final void push(final E e) {
        add(e);      
    }

    /**     
     * @return returns and removes the top element of the stack
     * @throws EmptyStackException if the stack is empty
     */
    public final E pop() {          
        final int len = size();

        if (len == 0) {
            throw new EmptyStackException();
        }
        
        return remove(size() - 1);       
    }
    
    /**
     * Pops n elements off the stack.    
     * @param n number of elements to pop
     * @throws EmptyStackException if there are not enough elements to pop
     */
    public final void popN(int n) {        
        final int len = size();
        if (n > len) {
            throw new EmptyStackException();
        }
        removeRange(len - n, len);
    }    

    /**     
     * @return returns the top element of the stack, without removing it
     * @throws EmptyStackException if the stack is empty
     */
    public final E peek() {
        final int len = size();

        if (len == 0) {
            throw new EmptyStackException();
        }
        
        return get(len - 1);
    }
    
    /**     
     * @param index the zero-based index of an element from the top of the stack
     * @return returns the element at the given index from the top of the stack, without removing it
     * @throws EmptyStackException if the stack does not have enough elements to satisfy this request.
     */
    public final E peek(int index) {
        final int len = size();

        if (index >= len) {
            throw new EmptyStackException();
        }

        return get(len - 1 - index);
    }    

    /**     
     * @return true iff the stack is empty
     */
    public final boolean empty() {
        return size() == 0;
    }

    /**         
     * @param e element to find. Can be null.
     * @return the 1-based position of 'e' on the stack, or -1 if 'e' is not on the stack.
     */
    public final int search(E e) {
        int i = lastIndexOf(e);

        if (i >= 0) {
            return size() - i;
        }
        return -1;
    }
}
