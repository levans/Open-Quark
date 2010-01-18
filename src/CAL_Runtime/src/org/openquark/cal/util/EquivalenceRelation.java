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
 * EquivalenceRelation.java
 * Created: Feb 4, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.util;

/**
 * An interface defining an equivalence relation. 
 * 
 * The interface EquivalenceRelation has the same relationship to the class Object (and its method equals)
 * that the interface Comparator has to the interface Comparable.  
 * 
 * @author Bo Ilic
 */
public interface EquivalenceRelation<T> {
    
    /**
     * Should satisfy the properties of an equivalence relation: i.e. the equivalent method must be 
     * reflexive, symmetric and transitive for all objects that can be compared by this EquivalenceRelation.
     * 
     * Note: the conditions above imply that the set of objects that can be compared by this EquivalenceRelation
     * is divided into equivalence classes by this EquivalenceRelation.
     * 
     * @param object1
     * @param object2
     * @return boolean
     * @throws ClassCastException if the arguments types prevent them from being compared by this equivalence relation.
     */
    boolean equivalent(T object1, T object2);
}
