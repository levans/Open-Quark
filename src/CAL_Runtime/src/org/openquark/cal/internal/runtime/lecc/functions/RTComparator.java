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
 * RTComparator.java
 * Created: Feb 2, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import java.util.Comparator;

import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTFunction;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * A special comparator that uses a CAL function of type a -> a -> Ordering to
 * define the compare method.
 * 
 * This is used to implement the built-in primitive function defined in the Prelude module:
 * makeComparator :: (a -> a -> Ordering) -> JComparator 
 * where JComparator is a foreign type corresponding to java.util.Comparator.
 * 
 * The actual implementation class returned by a call to makeComparator is a RTComparator.
 * 
 * @author Bo Ilic
 */
public final class RTComparator implements Comparator<RTValue> {
         
    private final RTFunction comparisonFunction;
    
    private final RTExecutionContext executionContext;
    
    public RTComparator(RTValue comparisonFunction, RTExecutionContext executionContext) {
        if (comparisonFunction == null || executionContext == null) {
            throw new NullPointerException();
        }
        
        this.comparisonFunction = (RTFunction)comparisonFunction;
        this.executionContext = executionContext;
    }
    
    /** {@inheritDoc} */
    public int compare(RTValue o1, RTValue o2) {
        
        try {
            
            switch (comparisonFunction.apply(o1, o2).evaluate(executionContext).getOrdinalValue()) {
            
                case 0: {
                    // Prelude.LT
                    return -1;
                }
                case 1: {
                    // Prelude.EQ
                    return 0;
                }
                case 2: {
                    // Prelude.GT
                    return 1;
                }
                
                default:
                {
                    throw new RuntimeException("Unrecognized Cal.Core.Prelude.Ordering data constructor.");
                }
            
            }
            
        } catch (CALExecutorException executorException) {
            //wrap the exception up in a non-checked exception and rethrow.
            //todoBI make sure we handle this nicely.
            throw new RuntimeException(executorException);
        }
    } 
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return comparisonFunction.toString();
    }
}

