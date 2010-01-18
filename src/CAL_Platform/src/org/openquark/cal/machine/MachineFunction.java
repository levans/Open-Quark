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
 * MachineFunction.java
 * Creation date: (July 4 2002 1:07:07 PM)
 * By: rcypher
 */
package org.openquark.cal.machine;

import java.util.Set;

import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;


/**
 * Warning- this class should only be used by the CAL runtime implementation. It is not part of the
 * external API of the CAL platform.
 * <P>
 * A MachineFunction contains machine specific information about a function as
 * well as the compiler generated non-specific information. 
 *
 * @author rcypher
 * Creation date: (7/4/02 1:07:07 PM)
 *
 * If a specific compiler/machine implementation wants to store additional information
 * about the code (e.g. arity, type, etc.) it can be done so by deriving from this
 * class. 
 */
public interface MachineFunction extends Comparable<MachineFunction> {
    /**
     * @return true if this CAL function is marked as being valid for
     * eager evaluation. 
     */
    public boolean canFunctionBeEagerlyEvaluated ();
    
    /**
     * @return Returns the aliasOf.
     */
    public QualifiedName getAliasOf();

    /**
     * Returns the arity of the code associated with this label.
     * @return in The arity of the code associated with this label.
     */
    public int getArity ();

    /**
     * Derived classes can override this to generate appropriate disassembly.
     * @return the disassembled form of the function.
     */
    public String getDisassembly ();
    
    /**
     * @return Returns the expressionForm.
     */
    public Expression getExpressionForm();

    /**
     * @return literal value if this supercombinator is defined as a literal value, null otherwise
     */
    public Object getLiteralValue();

    /**
     * Get the number of formal arguments for this supercombinator.
     * Creation date: (3/9/00 3:30:22 PM)
     * @return int
     */
    public int getNFormalParameters();
    
    
    /**
     * Get the type signature for the function. This is arguments and return type.
     * @return The function type signature.
     */
    public TypeExpr[] getType();
    
    /**
     * Return the name for this label
     * @return String
     */
    public String getName();
    
    /**
     * @return Returns the parameterNames.
     */
    public String[] getParameterNames();

    /**
     * @return strictness info for the arguments.
     */
    public boolean[] getParameterStrictness();
    
    /**
     * @return types of the arguments.
     */
    public TypeExpr[] getParameterTypes();
    
    /** Return the qualified name for this label.
     * @return QualifiedName
     */
    public QualifiedName getQualifiedName ();
    
    /**
     * @return The result type of this function.
     */
    public TypeExpr getResultType ();
    
    /**
     * @return Returns the connectedComponent.
     */
    public Set<String> getStronglyConnectedComponents();
    
    /**
     * Return the timestamp associated with this entity.
     * @return long
     */
    public long getTimeStamp();
    
    /**
     * @return True if this function contained a call to unsafeCoerce.
     */
    public boolean getHadUnsafeCoerce();
    
    /**
     * A CAF is a constant applicative form.
     * Currently zero arity CAL functions are CAFs but foreign functions are not.
     * This is because a zero arity foreign function can return a different value
     * each time it is evaluated (ex. a constructor).
     * @return true if this is a constant applicative form.
     */
    public boolean isCAF ();
    
    /**
     * @return true if this is a CAL function (i.e. not primitive and not foreign), false otherwise.
     */
    public boolean isCALFunction ();
    
    /**
     * @return Returns the codeGenerated.
     */
    public boolean isCodeGenerated();
    
    /**
     * @return true if this is a CAL Data Constructor.
     */
    public boolean isDataConstructor ();
    
    /**
     * @return true if this MachineFunction corresponds to an adjunct function.
     */
    public boolean isForAdjunct ();
    
    /**
     * @return true if this is a foreign function, false otherwise.
     */
    public boolean isForeignFunction ();
    
    /**
     * @return Returns the optimized.
     */
    public boolean isOptimized();
    
    /** 
     * @return true if this is a primitive function, false otherwise.
     */
    public boolean isPrimitiveFunction();
    
    /**
     * @param function
     * @return true if this is strongly connected to function.
     */
    public boolean isStronglyConnectedTo(String function);
    
    /**
     * @return Returns the isTailRecursive flag.
     */
    public boolean isTailRecursive();
    
    /**
     * @param aliasOf The aliasOf to set.
     */
    void setAliasOf(QualifiedName aliasOf);
    
    /**
     * @param codeGenerated The codeGenerated to set.
     */
    public void setCodeGenerated(boolean codeGenerated);
    
    /**
     * Set the expression form for the associated function.
     * @param e
     */
    public void setExpression (Expression e);

    /**
     * @param isTailRecursive The isTailRecursive to set.
     */
    public void setIsTailRecursive(boolean isTailRecursive);

    /**
     * Mark this function as being optimized.
     */
    public void setOptimized();
    
    /**
     * Mark this function as having had a call to unsafeCoerce. This is used by the Global Optimizer.
     */
    public void setHadUnsafeCoerce();
    
    /**
     * @param connectedComponents (String set) The connectedComponents to set. Cannot be null.
     */
    public void setStronglyConnectedComponents(Set<String> connectedComponents);    
}

