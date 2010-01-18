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
 * InputPolicy.java
 * Created: Jan 19, 2004 at 12:15:01 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.compiler.io;

import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.compiler.SourceModel.TypeExprDefn;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * The InputPolicy class contains the information necessary for the runtime to marshal 
 * Java input values into the CAL domain.
 * @author RCypher
 * @author mbyne
 */
public final class InputPolicy {

    /**
     * this expression is used for inputting CAL values, it has the following form:
     * unsafeFromCalValue # input 
     */
    private static final SourceModel.Expr calValueInputExpr = 
        SourceModel.Expr.BinaryOp.Compose.make(
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.unsafeFromCalValue),                               
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.input));
    
    /**
     * this expression is used as the default input marshaler 
     */
    private static final SourceModel.Expr defaultInputExpr =
        SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.input);
    
    /**
     * This is the default input policy. It can be used for for inputs that are inputable
     * and are not ambiguous, e.g. Double, String, [Int], etc, but not Num a => a, [a], etc
     */
    public static final InputPolicy DEFAULT_INPUT_POLICY = 
        makeWithMarshaler(defaultInputExpr);
    
    /**
     * This input policy is: (unsafeCoerce # ( input :: JObject -> CalValue))
     * It can be used for inputting an opaque CAL value (Prelude.CalValue), typically obtained using the CAL_VALUE_OUTPUT_POLICY
     * or STRICT_CAL_VALUE_OUTPUT_POLICY, to a CAL function. The CAL function must not have an ambiguous type for the corresponding parameter.
     * If the type is ambiguous you should use makeTypedCalValueInputPolicy.
     * <p>   
     * @see #makeTypedCalValueInputPolicy
     */
    public static final InputPolicy CAL_VALUE_INPUT_POLICY = 
        makeWithMarshaler(calValueInputExpr);     
    
    /**
     * This is a typed input policy for Prelude.Boolean values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Boolean.
     */
    public static final InputPolicy BOOLEAN_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Boolean));    

    /**
     * This is a typed input policy for Prelude.Char values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Char.
     */
    public static final InputPolicy CHAR_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Char));
        
    
    /**
     * This is a typed input policy for Prelude.Byte values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Byte.
     */
    public static final InputPolicy BYTE_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Byte));   
    
    /**
     * This is a typed input policy for Prelude.Short values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Short.
     */
    public static final InputPolicy SHORT_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Short));       
    
    /**
     * This is a typed input policy for Prelude.Int values. It should be used instead of
     * the default input policy when the functionalAgent has a polymorphic input 
     * and it needs to be bound to an Int.
     */
    public static final InputPolicy INT_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Int));

    /**
     * This is a typed input policy for Prelude.Long values. It should be used instead of
     * the default input policy when the functionalAgent has a polymorphic input 
     * and it needs to be bound to a Long.
     */
    public static final InputPolicy LONG_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Long));
    
    /**
     * This is a typed input policy for Prelude.Float values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Float.
     */
    public static final InputPolicy FLOAT_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Float));    

    /**
     * This is a typed input policy for Prelude.Double values. It should be used instead of
     * the default input policy when the functionalAgent has a polymorphic input 
     * and it needs to be bound to a Double.
     */
    public static final InputPolicy DOUBLE_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Double));    
    
    /**
     * This is a typed input policy for Prelude.String values. It should be used instead of
     * the default input policy when the functionalAgent has a polymorphic input 
     * and it needs to be bound to a String.
     */
    public static final InputPolicy STRING_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.String));
    
    /**
     * This is a typed input policy for Prelude.Integer values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Integer.
     */
    public static final InputPolicy INTEGER_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Integer));
               
    /**
     * This is a typed input policy for Prelude.Decimal values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Decimal.
     */
    public static final InputPolicy DECIMAL_INPUT_POLICY = 
        makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Decimal));


    
    /** A piece of CAL source code that converts from JObject(s) to a CAL type. Cannot be null. */
    private final SourceModel.Expr marshaler; 
    
    /** The number of arguments needed by the marshaler. Must be zero or more.*/
    private final int nArguments;
        
    /**
     * Constructs an InputPolicy with an expression in source model
     * representation specifying the marshaler.
     * 
     * @param type
     *            the CAL type to marshal to, cannot be null 
     * @param marshaler
     *            the marshaler that converts from JObject(s) to a CAL type, cannot be null
     * @param nArguments
     *            the number of arguments needed by the marshaler, must be zero or more
     */
    private InputPolicy (SourceModel.TypeExprDefn type, SourceModel.Expr marshaler, int nArguments) {
        if (marshaler == null) {
            throw new NullPointerException("Argument marshaler cannot be null.");
        }
        if (type == null) {
            throw new NullPointerException("Argument typeExprDefn cannot be null.");
        }
        if (nArguments < 0) {
            throw new IllegalArgumentException("Argument nArguments must be 0 or more.");
        }
    
        
        SourceModel.TypeExprDefn marshalerType = type;
        for (int i = 0; i < nArguments; i++) {
            marshalerType = SourceModel.TypeExprDefn.Function.make(
                SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.JObject), marshalerType);
        }
        
        this.marshaler = 
            SourceModel.Expr.ExprTypeSignature.make(marshaler, SourceModel.TypeSignature.make(marshalerType));
        this.nArguments = nArguments;
    }

    /**
     * Constructs an InputPolicy with an expression in source model
     * representation specifying the marshaler.
     * @param marshaler source expression representing the marshaler, this cannot be null.
     */
    private InputPolicy (SourceModel.Expr marshaler) {  
        if (marshaler == null) {
            throw new NullPointerException("Argument marshaler cannot be null.");
        }
        
        this.marshaler = marshaler;
        this.nArguments = 1;
    }
     
    /**
     * This is a factory method for creating a typed default input policy
     * It should be used when the input is ambiguous (e.g a) and an explicit type is required.
     * If the input is non-ambiguous (e.g. Double) it is easier to use DEFAULT_INPUT_POLICY
     * @param type the CAL type of the input
     * @return the input policy
     * 
     * @see #DEFAULT_INPUT_POLICY
     */
    public static InputPolicy makeTypedDefaultInputPolicy(SourceModel.TypeExprDefn type) {
       return makeWithTypeAndMarshaler(type, defaultInputExpr, 1);
    }
    
    /**
     * This is a factory method for creating a CAL Value input policy
     * It should be used when the input is ambiguous (e.g [a]) and an explicit type is required.
     * If the input is non-ambiguous (e.g. [Double]) it is easier to use CAL_VALUE_INPUT_POLICY
     * @param type the type of the opaque CAL value
     * @return the input policy
     * 
     * @see #CAL_VALUE_INPUT_POLICY
     */
    public static InputPolicy makeTypedCalValueInputPolicy(SourceModel.TypeExprDefn type) {
        return makeWithTypeAndMarshaler(type, calValueInputExpr, 1);
    }          
    
    /**
     * This is a factory method to create a new input policy with a specified marshaler
     * @param marshaler
     * @return the new input policy
     */
    public static InputPolicy makeWithMarshaler(Expr marshaler) {
        return new InputPolicy(marshaler);
    }

    /**
     * This is a factory method for creating an input policy with a typedefn and a marshaler
     * @param type
     * @param marshaler
     * @param nArguments the number of arguments with input policy requires
     * @return the new input policy
     */
    public static InputPolicy makeWithTypeAndMarshaler(TypeExprDefn type, Expr marshaler, int nArguments) {
        return new InputPolicy(type, marshaler, nArguments);
    }

    /**    
     * Get the source model expression representing the marshaler.
     * @return a marshaler expression from JObject(s) to a CAL type. This is never null.
     */
    public SourceModel.Expr  getMarshaler() {
        return marshaler;
    }
    
    /**   
     * Get the number of arguments used by the marshaler.  
     * @return the number of arguments needed by the marshaler. This is always zero or more.
     */
    public int getNArguments() {
        return nArguments;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString () {
        return "InputPolicy: Marshaler = " + marshaler + ", nArguments = " + nArguments;
    }
}
