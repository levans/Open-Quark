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
 * RTEquivalenceRelation.java
 * Created: Feb 4, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTFunction;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.util.EquivalenceRelation;


/**
 * A special EquivalenceRelation that uses a CAL function of type a -> a -> Boolean to
 * define the equivalent method.
 * 
 * This is used to implement the built-in primitive function defined in the Prelude module:
 * makeEquivalenceRelation :: (a -> a -> Boolean) -> JEquivalenceRelation 
 * where JEquivalenceRelation is a foreign type corresponding to 
 * org.openquark.cal.foreignsupport.core.EquivalenceRelation.
 * 
 * The actual implementation class returned by a call to makeComparator is a RTEquivalenceRelation.
 * @author Bo Ilic
 */
public final class RTEquivalenceRelation implements EquivalenceRelation<RTValue> {
  
    private final RTFunction eqFunction;
    
    private final RTExecutionContext executionContext;
    
    public RTEquivalenceRelation(RTValue eqFunction, RTExecutionContext executionContext) {
        if (eqFunction == null || executionContext == null) {
            throw new NullPointerException();
        }
        
        this.eqFunction = (RTFunction)eqFunction;
        this.executionContext = executionContext;
    }
      
    /** {@inheritDoc} */
    public boolean equivalent(RTValue object1, RTValue object2) {
        
        try {
            
            return eqFunction.apply(object1, object2).evaluate(executionContext).getBooleanValue();
            
        } catch (CALExecutorException executorException) {
            //wrap the exception up in a non-checked exception and rethrow.
            //todoBI make sure we handle this nicely.
            throw new RuntimeException(executorException);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return eqFunction.toString();
    }    

}
