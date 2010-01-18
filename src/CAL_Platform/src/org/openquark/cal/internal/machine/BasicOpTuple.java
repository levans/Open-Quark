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
 * BasicOpTuple.java
 * Creation date: (May 1, 2002)
 * By: Bo Ilic
 */
package org.openquark.cal.internal.machine;

import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.ForeignFunctionInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.internal.machine.primitiveops.PrimOps;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.ErrorInfo;


/** 
 * Expression tuple for basic operations.
 * This class is used to recognize fully-saturated calls to built-in or
 * foreign functions within an expression.
 */
public final class BasicOpTuple {
    
    /** The type of primitive op being invoked (InstructionBase.PRIMOP_* manifest). */
    private final int primOp;
    
    /** The foreign function info if the basic op is in fact an external function call. Null otherwise. */
    private final ForeignFunctionInfo foreignFunctionInfo;
    
    /** The information use to identify the source of an error when an exception is thrown. Only for PrimopERROR.
     * This may be null for the case of internally generated expressions. */
    private final ErrorInfo errorInfo;
    
    /** The argument expressions. */
    private final Expression[] argumentExpressions;
    
    /** The name of the operation. */
    private final QualifiedName opName;
    
    //public static final BasicOpTuple emptyBasicOpTuple = new BasicOpTuple(PrimOps.PRIMOP_NOP, null, null);
    
    
    private BasicOpTuple(int primOp, ForeignFunctionInfo foreignFunctionInfo, ErrorInfo errorInfo, Expression [] argumentExpressions, QualifiedName opName) {
        this.primOp = primOp;
        this.foreignFunctionInfo = foreignFunctionInfo;
        this.errorInfo = errorInfo;
        this.argumentExpressions = argumentExpressions;
        this.opName = opName;
    }
    
    /**
     * @return primitive operator index of the basic op.
     * Creation date: (May 1, 2002)
     */        
    public int getPrimitiveOp () {
            
        return primOp;
    } 
      
    /**
     * @return the foreign function info if the basic op is in fact an external function call.
     * Creation date: (May 3, 2002)
     */         
    public ForeignFunctionInfo getForeignFunctionInfo () {
        return foreignFunctionInfo;
    }          
    
    /** 
     * @return The error info if the basic op is in fact an error call.
     */
    public ErrorInfo getErrorInfo() {
        return errorInfo;
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
     * Accessor for the argument expressions in the basic op.
     * @param argN zero-based expression argument index.
     * @return Expression the expression argument
     * Creation date: (May 1, 2002)
     */    
    public Expression getArgument (int argN) {
        
        return argumentExpressions [argN];      
    }  
    
    /**
     * @return the qualified name of the operation
     */
    public QualifiedName getName () {
        return opName;
    }
    
    /**
     * Determine if the expression is a basic operation.
     * If the expression matches a basic operation,
     * a BasicOpTuple will be populated with operator parameters and returned.  
     * Otherwise, null is returned.
     * If the expression corresponds to a foreign function invocation, and the field/method/constructor could not be resolved,
     * a CodeGenerationException is thrown.
     * Creation date: (3/24/00 3:53:34 PM)
     * @param e Expression the expression to test
     * @return BasicOpTuple instantiated if the expression matched as a basic operation, null otherwise
     * @throws CodeGenerationException if the expression corresponds to a foreign function invocation, and the field/method/constructor could not be resolved.
     */
    public static BasicOpTuple isBasicOp(Expression e) throws CodeGenerationException {
        
        // Look for all the elements of a basic operation
        // Has to match: EAp(EAp(EVar <op>) e1) e2, for a binary op
        // Has to match: EAp(EVar <op>) e1, for a unary op
        // Where e<n> are the argument expressions

        Expression[] appChain = appChain (e);
        if (appChain == null) {
            return null;
        }

        int nArguments = appChain.length - 1;
        Expression.Var var = appChain[0].asVar();
           
        // Check that the full number of arguments are supplied.
        ForeignFunctionInfo foreignFunctionInfo = var.getForeignFunctionInfo();
        PrimOps.PrimOpInfo info = null;
        info = PrimOps.fetchInfo (var.getName());
        int nExpectedArguments;
        
        if (info == null && foreignFunctionInfo == null) {
            //not a built-in primitive or a foreign function call
            return null;
        } 
        
        
        if (info != null) {  
            nExpectedArguments = info.getArity ();
        } else 
        if (foreignFunctionInfo != null) {
            try {
                nExpectedArguments = foreignFunctionInfo.getNArguments();
            } catch (UnableToResolveForeignEntityException ex) {
                throw new CodeGenerationException("Failed to resolve foreign method, field, or constructor.", ex);
            }
        } else {
            //not a built-in primitive or a foreign function call
            return null;
        }
                      
        if (nExpectedArguments != nArguments) {
            return null;
        }
            
        Expression[] argumentExpressions = new Expression [nArguments];
        for (int i = 0; i < nArguments; ++i) {
            argumentExpressions[i] = appChain[i+1];
        }
        
        ErrorInfo errorInfo = null;
        if (var.getErrorInfo() != null){
            Expression.ErrorInfo ei = var.getErrorInfo();
            errorInfo = new ErrorInfo(ei.getTopLevelFunctionName(), ei.getLine(), ei.getColumn());
        }
        return new BasicOpTuple (info == null ? PrimOps.PRIMOP_FOREIGN_FUNCTION : info.getCode(), foreignFunctionInfo, errorInfo, argumentExpressions, var.getName());                     
    }    
    
    /**
     * Determine if the expression is an application of 'and' or 'or'.
     * If the expression matches either operation
     * a BasicOpTuple will be populated with operator parameters and returned.  
     * Otherwise, null is returned.
     * @param e Expression the expression to test
     * @return BasicOpTuple instantiated if the expression matched as a basic operation, null otherwise
     */
    public static BasicOpTuple isAndOr(Expression e) {
        
        // Has to match: EAp(EAp(EVar <op>) e1) e2, where <op> is 'and' or 'or'.

        Expression[] appChain = appChain (e);
        if (appChain == null) {
            return null;
        }

        int nArguments = appChain.length - 1;
        Expression.Var var = appChain[0].asVar();
           
        // fixup the names for Prelude.and and Prelude.or
        QualifiedName qn = var.getName();
        if (!qn.getModuleName().equals(CAL_Prelude.MODULE_NAME) || (!qn.getUnqualifiedName().equals("and") && !qn.getUnqualifiedName().equals("or"))) {
            return null;
        }
        
        int opCode;
        if (qn.getUnqualifiedName().equals("and")) {
            opCode = PrimOps.PRIMOP_AND;
        } else {
            opCode = PrimOps.PRIMOP_OR;
        }
            
        
        if (nArguments != 2) {
            return null;
        }
            
        Expression[] argumentExpressions = new Expression [nArguments];
        for (int i = 0; i < nArguments; ++i) {
            argumentExpressions[i] = appChain[i+1];
        }
        
        return new BasicOpTuple (opCode, null, null, argumentExpressions, qn);                     
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
