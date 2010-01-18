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
 * NonGenericVars.java
 * Creation date: (July 17, 2000)
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

/**
 * Lists of non-generic type variables. Non-generic variables are variables
 * that are not free to be specialized independently. For example, a variable bound by a lambda
 * abstraction. For example, in (\a -> a 2 +  a True), a is non-generic, and the whole expression is
 * ill typed.
 * 
 * It is important to note that a type variable that occurs in an instantiation of a non-generic
 * type variable are copied by CopyEnv in the same way as the official non-generic variables- i.e.
 * by reference. The same applies to record variables.
 * 
 * NonGenericVars is a persistent list in the sense that extending the list to add a new non-generic
 * variable does not alter the old list. The null value is used to represent the empty list. 
 *
 * Creation date: (July 17, 2000)
 * @author Bo Ilic
 */

class NonGenericVars {

    private final TypeVar head;
    private final NonGenericVars tail;

    /**
     * Insert the method's description here.
     * @param tail
     * @param head     
     */
    private NonGenericVars(NonGenericVars tail, TypeVar head) {
        if (head == null) {
            throw new NullPointerException();
        }
        this.head = head;
        this.tail = tail;
    }

    /**
     * Extend nonGenericVars by the head type variable. This is a static in order to easily handle empty
     * nonGenericVars lists which occur quite often.
     *
     * @return NonGenericVars
     * @param tail
     * @param head
     */
    static NonGenericVars extend(NonGenericVars tail, TypeVar head) {
        if (head == null) {
            throw new NullPointerException();
        }

        return new NonGenericVars(tail, head);
    }

    /**
     * Whether an uninstantiated type variable is generic w.r.t. a list of
     * non-generic type variables. What this means is that typeVar is not one of the nonGenericVars,
     * nor is it one of TypeVars occurring in any instantiation of the nonGenericVars
     *
     * Creation date: (7/17/00 4:47:59 PM)
     * @return boolean
     * @param uninstantiatedTypeVar
     */
    boolean isGenericTypeVar(TypeVar uninstantiatedTypeVar) {
        if (uninstantiatedTypeVar.getInstance() != null) {
            throw new IllegalArgumentException();
        }
        
        for (NonGenericVars nonGenericVars = this; nonGenericVars != null; nonGenericVars = nonGenericVars.tail) {
            if (nonGenericVars.head.containsUninstantiatedTypeVar(uninstantiatedTypeVar)) {                           
                return false;
            }
        }

        return true;       
    }
    
    /**
     * Returns false if the uninstantiated record var occurs somewhere in the TypeExpr of any of the
     * non generic variables.
     * @param uninstantiatedRecordVar
     * @return boolean
     */
    boolean isGenericRecordVar(RecordVar uninstantiatedRecordVar) {
        if (uninstantiatedRecordVar.getInstance() != null) {
            throw new IllegalArgumentException();
        }
        
        for (NonGenericVars nonGenericVars = this; nonGenericVars != null; nonGenericVars = nonGenericVars.tail) {
            if (nonGenericVars.head.containsRecordVar(uninstantiatedRecordVar)) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        int i = 0;
        PolymorphicVarContext pvc = PolymorphicVarContext.make();
        for (NonGenericVars nonGenericVars = this; nonGenericVars != null; nonGenericVars = nonGenericVars.tail) {
            result.append("var#").append(i).append(" = ");
            result.append(nonGenericVars.head.toString(pvc, null)); 
            result.append('\n');
            ++i;
        }
        
        return result.toString();        
    }
}