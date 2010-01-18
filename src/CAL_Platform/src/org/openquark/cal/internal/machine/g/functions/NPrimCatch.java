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
 * NPrimCatch.java
 * Created: Sep 7, 2006
 * By: Bo Ilic
 */

package org.openquark.cal.internal.machine.g.functions;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.g.Executor;
import org.openquark.cal.internal.machine.g.NPrimitiveFunc;
import org.openquark.cal.internal.machine.g.NValObject;
import org.openquark.cal.internal.machine.g.Node;
import org.openquark.cal.internal.module.Cal.Core.CAL_Exception_internal;
import org.openquark.cal.runtime.CALExecutorException;


/**
 * Implements the primitive function defined in the Exception module:
 * primCatch :: a -> (JThrowable -> a) -> a;
 * 
 * @author Bo Ilic
 */
public final class NPrimCatch extends NPrimitiveFunc {
    
    public static final QualifiedName name = CAL_Exception_internal.Functions.primCatch;
    public static final NPrimCatch instance = new NPrimCatch ();
    
    private NPrimCatch () {/* constructor made private for singleton */ }    

    /** {@inheritDoc} */
    @Override
    public Node doEvaluation(Node[] arguments, Executor executor) throws CALExecutorException {
               
        //arguments[0] == expr :: a
        //arguments[1] == handler :: JThrowable -> a
        
        try {
            return executor.internalEvaluate(arguments[0]);
            
        } catch (CALExecutorException.ExternalException.PrimThrowFunctionException primThrowException) {
            
            //the Exception.throw function was called
            return arguments[1].apply(new NValObject(primThrowException.getCause()));
            
        } catch (CALExecutorException.ExternalException.ForeignOrPrimitiveFunctionException foreignFunctionException) {
            
            //an exception occurred while calling a foreign function 
            return arguments[1].apply(new NValObject(foreignFunctionException.getCause()));
            
        } catch (CALExecutorException.ExternalException.PatternMatchFailure patternMatchFailure) {
            
            return arguments[1].apply(new NValObject(patternMatchFailure));
            
        } catch (CALExecutorException.ExternalException.ErrorFunctionException errorException) {                                   
            
            return arguments[1].apply(new NValObject(errorException));
                    
        } catch (Throwable throwable) {
            
            if (throwable instanceof CALExecutorException) {
                throw (CALExecutorException)throwable;
            }
            
            //todoBI check this for the g-machine
            //most likely an unchecked foreign function exception. Could also be a programming error in CAL's Java implementation.
            //at the moment we have no way to distinguish these 2 cases. 
            //todoBI
            //One solution is to wrap all calls to foreign functions so that a CALExecutor.CALForeignFunctionException is thrown,
            //but this is a performance hit.
            return arguments[1].apply(new NValObject(throwable));
        }   
        
        //todoBI handle CALExecutor.CALTerminatedByClientException. We may want to give code a chance to clean up after
        //a client has terminated.      
    }

    /** {@inheritDoc} */
    @Override
    protected int getArity() {        
        return 2;
    }

    /** {@inheritDoc} */
    @Override
    protected QualifiedName getName() {        
        return name;
    }    
}
