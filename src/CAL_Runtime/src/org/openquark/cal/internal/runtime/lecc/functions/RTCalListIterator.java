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
 * RTCalListIterator.java
 * Created: Oct 30, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openquark.cal.internal.runtime.lecc.RTCons;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTFunction;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * A helper class for implementing List.makeIterator :: [a] -> (a -> JObject) -> JIterator.
 * This class allows Java client control of the marshaling to Java of a CAL list value.
 * 
 * @author Bo Ilic
 */
public final class RTCalListIterator implements Iterator<Object> {
    
    private RTValue calListValue;
    private final RTFunction elementMarshalingFunction;    
    private final RTExecutionContext executionContext;
    
    public RTCalListIterator(RTValue calListValue, RTValue elementMarshalingFunction, RTExecutionContext executionContext) {
        if (calListValue == null || elementMarshalingFunction == null || executionContext == null) {
            throw new NullPointerException();
        }
        
        this.calListValue = calListValue;
        this.elementMarshalingFunction = (RTFunction)elementMarshalingFunction;
        this.executionContext = executionContext;
    }
       
    public boolean hasNext() {
        
        //evaluate calListValue to WHNF and see if it is a Cons. This means there is a next value
        
        try {
            calListValue = calListValue.evaluate(executionContext);            
            //0 == Prelude.Nil
            //1 == Prelude.Cons
            return calListValue.getOrdinalValue() == 1;
        } catch (CALExecutorException executorException) {
            //wrap the exception up in a non-checked exception and rethrow.          
            throw new RuntimeException(executorException);
        }       
    }

    public Object next() {
        try {
            calListValue = calListValue.evaluate(executionContext);
            switch (calListValue.getOrdinalValue()) {
                case 0:
                {
                    //Prelude.Nil
                    throw new NoSuchElementException();
                }
                    
                case 1:
                {
                    //Prelude.Cons                    
                    RTCons listCons = (RTCons)calListValue;
                    RTValue headValue = listCons.getFieldByIndex(1, 0, null);
                    calListValue = listCons.getFieldByIndex(1, 1, null);
                    
                    return elementMarshalingFunction.apply(headValue).evaluate(executionContext).getOpaqueValue();
                }
                    
                default:
                {
                    throw new IndexOutOfBoundsException();
                }
            }
                       
        } catch (CALExecutorException executorException) {
            //wrap the exception up in a non-checked exception and rethrow.          
            throw new RuntimeException(executorException);
        }       
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

}
