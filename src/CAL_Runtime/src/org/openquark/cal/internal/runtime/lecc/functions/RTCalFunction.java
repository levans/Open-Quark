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
 * RTCalFunction.java
 * Created: Nov 3, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.internal.runtime.lecc.functions;

import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.RTFunction;
import org.openquark.cal.internal.runtime.lecc.RTValue;
import org.openquark.cal.internal.runtime.lecc.RTData.CAL_Opaque;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalFunction;


/**
 * Runtime support for the CAL function Prelude.makeCalFunction :: (JObject -> JObject) -> CalFunction.
 * @author Bo Ilic
 */
public final class RTCalFunction implements CalFunction {
    
    /** a CAL function of type JObject -> JObject */
    private final RTFunction calFunction;
    
    private final RTExecutionContext executionContext;
    
    public RTCalFunction(RTValue calFunction, RTExecutionContext executionContext) {
        if (calFunction == null || executionContext == null) {
            throw new NullPointerException();
        }
        
        this.calFunction = (RTFunction)calFunction;
        this.executionContext = executionContext;
    }
       
    /** {@inheritDoc} */
    public Object evaluate(Object argument) {
        
        //evaluate (calFunction argument).
        
         try {          
             
            return calFunction.apply(CAL_Opaque.make(argument)).evaluate(executionContext).getOpaqueValue();
            
        } catch (CALExecutorException executorException) {
            //wrap the exception up in a non-checked exception and rethrow.            
            throw new RuntimeException(executorException);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return calFunction.toString();
    }
}
