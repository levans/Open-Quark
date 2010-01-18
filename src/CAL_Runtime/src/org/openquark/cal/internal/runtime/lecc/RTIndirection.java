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
 * RTIndirection.java
 * Created: May 22, 2003 11:46:48 AM
 * By: RCypher  
 */

package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;

/**
 * This is the RTIndirection class/interface.
 * This class is used as a place holder in the recursive let situation.
 * <p>
 * Created: May 22, 2003 11:46:48 AM
 * @author RCypher
 */
public class RTIndirection extends RTResultFunction {

    /* (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.RTValue#unwind()
     */
    @Override
    protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException  {
        if (result == null) {
            throw new NullPointerException ("Invalid reduction state in indirection.  This is probably caused by a circular let variable definition.");
        }
        
        if (LECCMachineConfiguration.concurrentRuntime()) {
            return result.synchronizedReduce(ec);
        }
        return result.reduce(ec);       
    }
    
    /**
     * Determine if this value should represent a logical 'true' if employed in
     * a logical expression (particularly the condition of an 'if').
     * @return boolean
     */
    @Override
    public final boolean isLogicalTrue() {
        // All normal values are assumed to represent 'true'
        // The one special value 'CAL_Boolean.CAL_False' in the kernel overrides this
        // to 'false'
        if (result != null) {
            return result.isLogicalTrue();
        }
        return true;
    }

    /**
     * Return the evaluated RTValue.  This may cause the value to be evaluated
     * if this hasn't already been done.
     * @param ec
     * @return RTValue the result of evaluating this RTValue
     * @throws CALExecutorException
     */
    @Override
    public final RTValue evaluate(RTExecutionContext ec) throws CALExecutorException {
       if (LECCMachineConfiguration.concurrentRuntime()) {
           return synchronizedEvaluate(ec);
       } 
       
       return unsynchronizedEvaluate(ec);             
    }
    
    synchronized private final RTValue synchronizedEvaluate(RTExecutionContext ec) throws CALExecutorException {
        return unsynchronizedEvaluate(ec);
    }

    private final RTValue unsynchronizedEvaluate(RTExecutionContext ec) throws CALExecutorException {
        if (result == null) {
            throw new NullPointerException ("Invalid reduction state in indirection.  This is probably caused by a circular let variable definition.");
        }
        setResult (result.evaluate(ec));
        return result;
    }

    /*
     *  (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
     */
    @Override
    public void clearMembers () {
        // No members to clear in an RTIndirection.
    }                  
}
