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
 * CALExample.java
 * Created: Apr 17, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.metadata;

/**
 * A class to represent a small coding example in CAL. Typically this is a snippet of
 * executable CAL code, along with a textual description of the code.
 * 
 * @author Bo Ilic
 */
public class CALExample {

    /** short localized description of the example. */
    private final String description;
    
    /** CAL expression defining the example. */
    private final CALExpression exampleExpression;
    
    /**
     * true if exampleExpression should be evaluated and the results displayed. Some examples
     * may not be good to evaluate because they take too much time, or have undesirable
     * side-effects.
     */
    private final boolean evaluateExample;
    
         
    public CALExample(CALExpression exampleExpression, String description, boolean evaluateExample) {
        
        if (exampleExpression == null) {
            throw new NullPointerException();
        }
        
        this.exampleExpression = exampleExpression;
        this.description = description;
        this.evaluateExample = evaluateExample;        
    }

    /**    
     * @return gets the short localized description of the example.
     */
    public String getDescription() {
        return description;
    }

    /**    
     * @return CALExpression CAL expression defining the example.
     */
    public CALExpression getExpression() {
        return exampleExpression;
    }
    
    /** 
     * @return boolean true if exampleExpression should be evaluated and the results displayed. Some examples
     *    may not be good to evaluate because they take too much time, or have undesirable
     *    side-effects.
     */    
    public boolean evaluateExample() {
        return evaluateExample;
    }

    /**
     * @return a copy of this example
     */    
    public CALExample copy() {
        return new CALExample(exampleExpression.copy(), description, evaluateExample);
    }
}
