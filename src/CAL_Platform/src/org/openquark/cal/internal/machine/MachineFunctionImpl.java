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
 * MachineFunctionImpl.java
 * Creation date: Dec. 10, 2006
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine;


import java.io.IOException;
import java.util.Set;

import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.CoreFunction;
import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.internal.serialization.ModuleSerializationTags;
import org.openquark.cal.internal.serialization.RecordInputStream;
import org.openquark.cal.internal.serialization.RecordOutputStream;
import org.openquark.cal.internal.serialization.RecordInputStream.RecordHeaderInfo;
import org.openquark.cal.machine.MachineFunction;


/**
 * @author rcypher
 *
 */
public abstract class MachineFunctionImpl implements MachineFunction {
    private static final int serialSchema = 0;
    
    /** The CoreFunction being specialized for a specific machine. */
    private CoreFunction coreFunction;
    
    /** Flag indicating that machine specific code has been generated. */
    private boolean codeGenerated = false;

    /** Flag indicating transformation optimizations have been applied. */
    private boolean optimized = false;
    
    /**
     * Construct a label from name and offset
     * @param coreFunction
     */
    public MachineFunctionImpl(CoreFunction coreFunction) {
        if (coreFunction == null) {
            throw new IllegalArgumentException ("Unable to create MachineFunction due to null argument.");
        }
        this.coreFunction = coreFunction;

    }
    
    /**
     * Set the expression form for the associated function.
     * @param e
     */
    public final void setExpression (Expression e) {
        coreFunction.setExpression(e);
    }
    
    // Zero arity constructor for serialization.
    protected MachineFunctionImpl() {
    }

    /** Return the qualified name for this label.
     * @return QualifiedName
     */
    public final QualifiedName getQualifiedName () {
        return coreFunction.getName();
    }
    
    /**
     * Return the name for this label
     * @return String
     */
    public final String getName() {
        return getQualifiedName().getUnqualifiedName();
    }

    /**
     * Returns the arity of the code associated with this label.
     * @return in The arity of the code associated with this label.
     */
    public final int getArity () {
        return coreFunction.getNFormalParameters();
    }
    
    /**
     * @return Returns the aliasOf.
     */
    public final QualifiedName getAliasOf() {
        return coreFunction.getAliasOf ();
    }
    
    /** 
     * @return True if the function contained a call to unsafeCoerce.
     */
    public final boolean getHadUnsafeCoerce(){
        return coreFunction.getHadUnsafeCoerce();
    }
    
    /** 
     * Mark the expression as having contained an unsafeCoerce call.
     */
    public final void setHadUnsafeCoerce(){
       coreFunction.setHadUnsafeCoerce();
    }
    
    /** 
     * @return true if this is a primitive function, false otherwise.
     */
    public final boolean isPrimitiveFunction() {
        return coreFunction.isPrimitiveFunction();
    }
    
    /**
     * @return true if this is a foreign function, false otherwise.
     */
    public final boolean isForeignFunction () {
        return coreFunction.isForeignFunction();
    }
    
    /**
     * @return true if this is a CAL function (i.e. not primitive and not foreign), false otherwise.
     */
    public final boolean isCALFunction () {
        return coreFunction.isCALFunction();
    }
    
    /**
     * @return true if this is a CAL Data Constructor.
     */
    public final boolean isDataConstructor () {
        return coreFunction.isDataConstructor();
    }

    /**
     * A CAF is a constant applicative form.
     * Currently zero arity CAL functions are CAFs but foreign functions are not.
     * This is because a zero arity foreign function can return a different value
     * each time it is evaluated (ex. a constructor).
     * @return true if this is a constant applicative form.
     */
    public final boolean isCAF () {
        return getArity() == 0 && !isForeignFunction() && !isDataConstructor();
    }
    
    /**
     * Return the timestamp associated with this entity.
     * @return long
     */
    public final long getTimeStamp() {
        return coreFunction.getTimeStamp();
    }

    /**
     * @return strictness info for the arguments.
     */
    public final boolean[] getParameterStrictness() {
        return coreFunction.getParameterStrictness();
    }

    /**
     * @return types of the arguments.
     */
    public final TypeExpr[] getParameterTypes() {
        return coreFunction.getParameterTypes();
    }

    /**
     * @return Returns the isTailRecursive flag.
     */
    public final boolean isTailRecursive() {
        return coreFunction.isTailRecursive();
    }

    /**
     * @return Returns the expressionForm.
     */
    public final Expression getExpressionForm() {
        return coreFunction.getExpression();
    }
    
    /**
     * @return Returns the parameterNames.
     */
    public final String[] getParameterNames() {
        return coreFunction.getFormalParameters();
    }
    
    @Override
    public final String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append(getCoreFunction().toString());
        sb.append("alias = " + getAliasOf() + "\n");
        sb.append("literalValue = " + getLiteralValue() + "\n");
        sb.append("timeStamp = " + getTimeStamp() + "\n");
        sb.append("isCAF = " + isCAF() + "\n");
        sb.append("isCALFunction = " + isCALFunction() + "\n");
        sb.append("isCodeGenerated = " + isCodeGenerated() + "\n");
        sb.append("isDataConstructor = " + isDataConstructor() + "\n");
        sb.append("isForAdjunct = " + isForAdjunct() + "\n");
        sb.append("isForeignFunction = " + isForeignFunction() + "\n");
        sb.append("isOptimized = " + isOptimized() + "\n");
        sb.append("isPrimitiveFunction = " + isPrimitiveFunction() + "\n");
        sb.append("isTailRecursive = " + isTailRecursive() + "\n");
        return sb.toString();
    }
    
    /**
     * @return Returns the connectedComponent.
     */
    public final Set<String> getStronglyConnectedComponents() {
        return coreFunction.getStronglyConnectedComponents();
    }    
    
    /**
     * @param function
     * @return true if this is strongly connected to function.
     */
    public final boolean isStronglyConnectedTo(String function) {
        return coreFunction.isStronglyConnectedTo(function);       
    }
    
    /**
     * Derived classes can override this to generate appropriate disassembly.
     * @return the disassembled form of the function.
     */
    public String getDisassembly () {
        return "Unable to disassemble function " + getQualifiedName() + ".";
    }
    /**
     * @return Returns the codeGenerated.
     */
    public final boolean isCodeGenerated() {
        return codeGenerated;
    }
    /**
     * @param codeGenerated The codeGenerated to set.
     */
    public final void setCodeGenerated(boolean codeGenerated) {
        this.codeGenerated = codeGenerated;
    }
    
    /**
     * @param aliasOf The aliasOf to set.
     */
    public final void setAliasOf(QualifiedName aliasOf) {
        coreFunction.setAliasOf(aliasOf);
    }
    
    
    /**
     * @param connectedComponents (String set) The connectedComponents to set. Cannot be null.
     * {@inheritDoc}
     */
    public void setStronglyConnectedComponents(Set<String> connectedComponents) {
        coreFunction.setStronglyConnectedComponents(connectedComponents);
    }
    
    /**
     * @param isTailRecursive The isTailRecursive to set.
     */
    public final void setIsTailRecursive(boolean isTailRecursive) {
        coreFunction.setIsTailRecursive(isTailRecursive);
    }
    
    /**
     * @return Returns the coreFunction.
     */
    public final CoreFunction getCoreFunction() {
        return coreFunction;
    }
    
    /**
     * Write this MachineFunction instance out to the RecordOutputStream. 
     * @param s
     * @throws IOException
     */
    public void write (RecordOutputStream s) throws IOException {
        s.startRecord (ModuleSerializationTags.MACHINE_FUNCTION, serialSchema);
        coreFunction.write (s);
        s.endRecord ();
    }


    /**
     * Reads this MachineFunction instance.
     * Read position in the stream is before record header.
     * @param s
     * @param mti
     * @param msgLogger the logger to which to log deserialization messages.
     * @throws IOException
     */
    protected void read (RecordInputStream s, ModuleTypeInfo mti, CompilerMessageLogger msgLogger) throws IOException {
        // Load the record header and determine which actual class we are loading.
        RecordHeaderInfo rhi = s.findRecord(ModuleSerializationTags.MACHINE_FUNCTION);
        if (rhi == null) {
            throw new IOException ("Unable to find record header for MachineFunction.");
        }
        if (rhi.getSchema() > serialSchema) {
            throw new IOException("Saved schema is greater than current schema in MachineFunction.");
        }
        
        this.optimized = true;
        this.codeGenerated = true;
        coreFunction = CoreFunction.load(s, mti, msgLogger);
        
        s.skipRestOfRecord();
    }
    
    /**
     * @return Returns the optimized.
     */
    public final boolean isOptimized() {
        return optimized;
    }

    /**
     * Mark this function as being optimized.
     */
    public final void setOptimized() {
        this.optimized = true;
    }
   
    /**
     * Get the number of formal arguments for this supercombinator.
     * Creation date: (3/9/00 3:30:22 PM)
     * @return int
     */
    public final int getNFormalParameters() {
        return coreFunction.getNFormalParameters();
    }
    
    /**
     * Get the type signature for the function. This is arguments and return type.
     * @return The function type signature.
     */
    public final TypeExpr[] getType(){
        return coreFunction.getType();
    }
    
    /**
     * @return literal value if this supercombinator is defined as a literal value, null otherwise
     */
    public final Object getLiteralValue() {
        return coreFunction.getLiteralValue();
    }
    
    /**
     * @return true if this MachineFunction corresponds to an adjunct function.
     */
    public final boolean isForAdjunct ()  {
        return coreFunction.isForAdjunct();
    }
    
    /**
     * @return The result type of this function.
     */
    public final TypeExpr getResultType () {
        return coreFunction.getResultType();
    }
    
    /**
     * @return true if this CAL function is marked as being valid for
     * eager evaluation. 
     */
    public final boolean canFunctionBeEagerlyEvaluated () {
        return coreFunction.canFunctionBeEagerlyEvaluated();
    }
    
    /** {@inheritDoc}*/
    public final int compareTo (MachineFunction o) {
        return getName().compareTo(o.getName());
    }
    
    @Override
    public final boolean equals (Object o) {
        if (o instanceof MachineFunction) {
            return getName().equals(((MachineFunction)o).getName());
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        return getName().hashCode();
    }


}
