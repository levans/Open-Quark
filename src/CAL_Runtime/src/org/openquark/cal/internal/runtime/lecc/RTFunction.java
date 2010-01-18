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
 * RTFunction.java
 * Created: Jan 22, 2002 at 2:16:12 PM 
 * By: RCypher 
 */
package org.openquark.cal.internal.runtime.lecc;



/**
 * An RTFunction represents a functional value (applicable to arguments and
 * reducible). An RTFunction has an arity and applied count.
 * @author LEvans
 */
public abstract class RTFunction extends RTValue {

    /**
     * @see org.openquark.cal.internal.runtime.lecc.RTValue#apply(org.openquark.cal.internal.runtime.lecc.RTValue)
     */
    @Override
    public RTValue apply(RTValue argument) {
        if (LECCMachineConfiguration.generateAppCounts()) {        
            RTValue.incrementNApplicationCalls();
        }
        return new RTApplication(this, argument);
    }
    
    @Override
    public RTValue apply(RTValue arg1, RTValue arg2) {
        if (LECCMachineConfiguration.generateAppCounts()) {
            RTValue.incrementNApplicationCalls();
        }
        return new RTApplication (new RTApplication(this, arg1), arg2);
    }
    
    @Override
    public RTValue apply(RTValue arg1, RTValue arg2, RTValue arg3) {
        if (LECCMachineConfiguration.generateAppCounts()) {
            RTValue.incrementNApplicationCalls();
        }
        return new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3);
    }
    
    @Override
    public RTValue apply(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4) {
        if (LECCMachineConfiguration.generateAppCounts()) {
            RTValue.incrementNApplicationCalls();
        }
        return new RTApplication(new RTApplication (new RTApplication (new RTApplication(this, arg1), arg2), arg3), arg4);
    }
    
    /**
     * Get the name of this function which is the name of the underlying
     * implementation
     * @return the name
     */
    String getClassName() {
        return getClass().getName();
    }
}
