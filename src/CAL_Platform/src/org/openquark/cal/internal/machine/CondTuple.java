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
 * CondTuple.java
 * Created: ??
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine;

import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;

/** 
 * Expression tuple for conditionals.
 */
public final class CondTuple {

    private final Expression kCond; // Condition expr
    private final Expression kThen; // Then expr
    private final Expression kElse; // Else expr
    
    private CondTuple(Expression kCond, Expression kThen, Expression kElse) {
        this.kCond = kCond;
        this.kThen = kThen;
        this.kElse = kElse;
    }
    
    public Expression getConditionExpression () {
        return kCond;
    }
    
    public Expression getThenExpression () {
        return kThen;
    }
    
    public Expression getElseExpression () {
        return kElse;
    }
    
    /**
     * Determine if the expression is a conditional (application of "if").
     * If the expression matches as an "if" expression, a CondTuple will be populated 
     * with the condition parameters and returned.  Otherwise null returned.
     * Creation date: (3/24/00 3:53:55 PM)
     * @param e Expression the expression to test
     * @return CondTuple instantiated if the expression is an if condition, null otherwise
     */
    public static CondTuple isCondOp(Expression e) {
        // Look for all the elements of a condition
        // Has to match: EAp(EAp(EAp(EVar "if") kCond) kThen) kElse
        // Where kCond is the condition expression,
        //       kThen is the expression to evaluate on kCond being true 
        //       kElse is the expression to evaluate on kCond being false

        Expression[] appChain = appChain (e);
        if (appChain == null) {
            return null;
        }

        if (appChain.length != 4 || !appChain[0].asVar().getName().equals(QualifiedName.make(CAL_Prelude.MODULE_NAME, "if"))) {
            return null;
        }

        // We have a full match for an if expression
        return new CondTuple(appChain[1], appChain[2], appChain[3]);
    }    

    /**
     * Convert an application chain to an array.
     * @param root
     * @return null if this is not an SC application, array of Expression otherwise.
     */
    static private Expression[] appChain (Expression root) {
        if (root.asAppl() != null) {
            // Walk down the left branch.
            Expression c = root;
            int nArgs = 0;
            while (c instanceof Expression.Appl) {
                nArgs++;
                c = ((Expression.Appl)c).getE1();
            }
            
            // At this point c should be an Expression.Var
            if (!(c instanceof Expression.Var)) {
                return null;
            }
            
            Expression[] chain = new Expression [nArgs + 1];
            chain[0] = c;
            c = root;
            for (int i = nArgs; i >= 1; i--) {
                chain[i] = ((Expression.Appl)c).getE2();
                c = ((Expression.Appl)c).getE1();
            }
    
            return chain;
        } else
        if (root.asTailRecursiveCall() != null) {
            return appChain (root.asTailRecursiveCall().getApplForm());
        } else
        if (root.asVar() != null) {
            return new Expression[]{root};
        }
            
        return null;
    }
    
}
