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
 * ConstructorOpTuple.java
 * Created: Dec 11, 2002 at 4:31:51 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.Expression;

/**
 * Models a fully saturated data constructor appearing within an expression.
 * Created: Dec 11, 2002 at 4:31:50 PM
 * @author Raymond Cypher
 */
public final class ConstructorOpTuple {
       
    private final DataConstructor dataConstructor;
    
    /** The argument expressions. */
    private final Expression[] argumentExpressions;
    
    private final Expression.Var dcExpression;
    
    public static final ConstructorOpTuple emptyConstructorOpTuple = new ConstructorOpTuple(null, null);    
    
    private ConstructorOpTuple(Expression.Var dcExpression, Expression [] argumentExpressions) {
        this.dcExpression = dcExpression;
        this.dataConstructor = dcExpression == null ? null : dcExpression.getDataConstructor();
        this.argumentExpressions = argumentExpressions;       
    }
    
    /**
     * @return DataConstructor
     */        
    public DataConstructor getDataConstructor () {
        return dataConstructor;
    } 
    
    /**
     * @return the data constructor expression.
     */
    public Expression.Var getDataConstructorExpression () {
        return dcExpression;
    }
    
    /**
     * @return the number of expression arguments in the basic op.
     * Creation date: (May 1, 2002)
     */        
    public int getNArguments () {
        
        if (argumentExpressions == null) {
            return -1;
        }
            
        return argumentExpressions.length; 
    }
    
    /**
     * Accessor for the argument expressions in the constructor application.
     * @param argN zero-based expression argument index.
     * @return Expression the expression argument
     */    
    public Expression getArgument (int argN) {
        
        return argumentExpressions [argN];      
    }  
    
    /**
     * @return the ordinal of the constructor.
     */
    public int getOrdinal () {
        return dataConstructor.getOrdinal();
    }
    
   /**
     * Determine if the expression is a basic operation.
     * If the expression matches a basic operation,
     * a ConstructorOpTuple will be populated with operator parameters and returned.  
     * Otherwise, null is returned.
     * Creation date: (3/24/00 3:53:34 PM)
     * @return ConstructorOpTuple instantiated if the expression matched as a basic operation, null otherwise
     * @param e Expression the expression to test
     * @param testOnly boolean only test, don't return a real BasicOpTuple
     */
    public static ConstructorOpTuple isConstructorOp(Expression e, boolean testOnly) {

        // Look for all the elements of a basic operation
        // Has to match: EAp(EAp(EVar <op>) e1) e2, for a binary op
        // Has to match: EAp(EVar <op>) e1, for a unary op
        // Where e<n> are the argument expressions

        // Check for an application node spine, and count the number of arguments          
        Expression spineNode = e;     
        int nArguments = 0;             
        for (Expression.Appl applNode = e.asAppl (); applNode != null; applNode = spineNode.asAppl ())
        {
            if (applNode.getE2 () == null) {
                throw new IllegalArgumentException("Programming error. The argument 'e' is not a valid application node.");              
            }
            
            ++nArguments;
            spineNode = applNode.getE1 ();                    
        }       
                
        Expression.Var var = spineNode.asVar();
        if (var == null) {
            return null;
        }
           
        DataConstructor dc = var.getDataConstructor ();
        
        if (dc == null) {
            return null;
        }

        // Check that the full number of arguments are supplied.
        int nExpectedArguments = dc.getArity();
            
        if (nExpectedArguments != nArguments) {
            return null;
        }            
        
        if (testOnly) {
            return ConstructorOpTuple.emptyConstructorOpTuple;
        }
        
  
        Expression[] argumentExpressions = new Expression [nArguments];
        spineNode = e;
        for (int argN = nArguments - 1; argN >= 0; --argN)
        {
            Expression.Appl applNode = spineNode.asAppl();
            argumentExpressions[argN] = applNode.getE2 ();           
            spineNode = applNode.getE1 ();
        }
        
        return new ConstructorOpTuple (var, argumentExpressions);
    }                 
    
}
