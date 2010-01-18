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
 * CALExpression.java
 * Created: Apr 17, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.metadata;

import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.ModuleName;

/**
 * CALExpression models an expression in CAL, similar to what would be entered into the code gem
 * in the GemCutter. In this version the expression is assumed to have no free variables i.e.
 * the code gem would have 0 arguments.
 * <p>
 * Sample expressions:
 * <pre>
 * [10.0, 20.0, 30.0]
 * take 10 allPrimes
 * Prelude.ones
 * \x y -> sqrt (x * x - y * y)
 * </pre>
 * @author Bo Ilic
 */
public class CALExpression {
   
    /** module context in which this expression should be evaluated. */
    private final ModuleName moduleContext;
    
    /** expression in CAL syntax, similar to what would be entered into a code gem, 
     * which may contian unqualified symbols but no free variables. */
    private final String calExpressionText;
    
    /** fully qualified version of the cal expression text */
    private final String qualifiedCalExpressionText;
    
    /** mapping from unqualified to qualified symbol names appearing in code */
    private final CodeQualificationMap qualificationMap;
   
    /**
     * Constructor for a new CALExpression with the given module context and expression text.
     * @param moduleContext the module context this expression should be evaluated in
     * @param calExpressionText the cal code text of this expression
     * @param qualificationMap the qualification map to use for the code text
     * @param qualifiedExpressionText the fully qualified cal code of the expression
     */
    public CALExpression (ModuleName moduleContext, String calExpressionText, CodeQualificationMap qualificationMap, String qualifiedExpressionText) {
        
        if (moduleContext == null || calExpressionText == null || qualificationMap == null || qualifiedExpressionText == null) {
            throw new NullPointerException();
        }
        
        this.moduleContext = moduleContext;
        this.calExpressionText = calExpressionText;
        this.qualificationMap = qualificationMap;
        this.qualifiedCalExpressionText = qualifiedExpressionText;
    }
    
    /**
     * @return the cal code that should be displayed
     */
    public String getExpressionText() {
        return calExpressionText;
    }
    
    /**
     * @return the qualified cal code that should be evaluated
     */
    public String getQualifiedExpressionText() {
        return qualifiedCalExpressionText;
    }
    
    /**
     * @return the module context this expression should be evaluated in
     */
    public ModuleName getModuleContext() {
        return moduleContext;
    }
    
    /**
     * @return the qualification map of the expression code
     */
    public CodeQualificationMap getQualificationMap() {
        return qualificationMap.makeCopy();
    }
    
    /**
     * @return a copy of this expression object
     */
    public CALExpression copy() {
        return new CALExpression(moduleContext, calExpressionText, qualificationMap, qualifiedCalExpressionText);
    }
}
