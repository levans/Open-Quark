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
 * RTOApp2.java
 * Created: Nov 10, 2003  3:31:41 PM
 * By: RCypher
 */
package org.openquark.cal.internal.runtime.lecc;

import java.util.concurrent.atomic.AtomicInteger;

import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalValue;

/**
 * This is a specialized representation of an oversaturated application with two arguments.
 * It is used when the function is known to take 1 argument and produce another function
 * which takes exactly one argument.
 * This means that this node can be treated like a fully saturated application. (i.e. the
 * arity and arg count are zero).
 *  @author RCypher
 */
public class RTOApp2 extends RTFullApp {

    private RTValue function;
    private RTValue arg1;
    private RTValue arg2;
    
    /** number of instances of this class. Used for statistics purposes. */
    private static final AtomicInteger nInstances = new AtomicInteger(0);
    
    public RTOApp2 (RTValue function, RTValue arg1, RTValue arg2) {
        assert (function != null && arg1 != null && arg2 != null) :
            "Invalid argument value in RTOApp2 constructor.";
        
        this.function = function;
        this.arg1 = arg1;
        this.arg2 = arg2;
        if (LECCMachineConfiguration.generateAppCounts()) {
            nInstances.incrementAndGet();
        }
    }
    
    public static final int getNInstances() {
        return nInstances.get();
    }
    
    public static final void resetNInstances() {
        nInstances.set(0);
    }    
    
    @Override
    protected final RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
        // Reduce from this application
        // Update and return result
        if (function != null) {
            RTValue v;
            if (function instanceof RTSupercombinator) {
                v = function.f1L (arg1, ec).evaluate(ec);
            } else {
                v = function.apply(arg1).evaluate(ec);
            }
            if (v instanceof RTSupercombinator) {
                setResult (v.f1L(arg2, ec));
            } else {
                setResult (v.apply(arg2));
            }
            function = null;
            arg1 = null;
            arg2 = null;
        } else if (result == null) {
            throw new NullPointerException ("Invalid reduction state in application.  This is probably caused by a circular function definition.");
        }
        return (result);
    }
    
    /*
     *  (non-Javadoc)
     * @see org.openquark.cal.internal.runtime.lecc.RTResultFunction#clearMembers()
     */
    @Override
    public void clearMembers () {
        function = null;
        if (arg1 == null) {
            arg2 = null;
        } else {
            arg1 = null;
        }
    }
       
    /**     
     * {@inheritDoc}
     */
    @Override
    public final int debug_getNChildren() {            
        if (result != null) {
            return super.debug_getNChildren();
        }
        return 3;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final CalValue debug_getChild(int childN) {
        if (result != null) {
            return super.debug_getChild(childN);
        }
        
        switch (childN) {
        case 0:
            return function;
        case 1:
            return arg1;
        case 2:
            return arg2;
        default:
            throw new IndexOutOfBoundsException();
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeStartText() {
        if (result != null) {
            return super.debug_getNodeStartText();
        }        
        
        return "(";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getNodeEndText() {
        if (result != null) {
            return super.debug_getNodeEndText();
        }
        
        return ")";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final String debug_getChildPrefixText(int childN) {
        if (result != null) {
            return super.debug_getChildPrefixText(childN);
        }
        
        switch (childN) {
        case 0:
            return "";
        case 1:
        case 2:
            return " ";
        default:
            throw new IndexOutOfBoundsException();     
        }           
    }      
    
}
