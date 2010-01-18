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
 * OutputPolicy.java
 * Created: Jan 19, 2004 at 12:15:01 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.compiler.io;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModelUtilities;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/**
 * The OutputPolicy class contains the information necessary for 
 * the runtime to marshal a CAL output value into to the Java
 * domain.
 * @author RCypher
 * @author mbyne
 */
public final class OutputPolicy {
    
    /**
     * This is the source model expression for outputting to an Iterator: (Prelude.output # List.toJIterator)
     */
    private static final SourceModel.Expr iteratorValueOutputExpr = 
        SourceModel.Expr.BinaryOp.Compose.make(
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.output), 
            SourceModel.Expr.makeGemCall(CAL_List.Functions.toJIterator));           
    /**
     * This is the source model expression for outputting to a CAL value:
     * (Prelude.output # Prelude.toCalValue)
     */
    private static final SourceModel.Expr calValueOutputExpr = 
        SourceModel.Expr.BinaryOp.Compose.make(           
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.output),                     
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.toCalValue));    
    
    /**
     * This is the source model for outputting a CAL value strictly (i.e. the value is evaluated to
     * weak-head normal form). This is sometimes needed when the external Java client wants to control the
     * order of side effects in a series of CAL computations.
     * 
     * (Prelude.outputCalValueStrict # Prelude.toCalValue)
     */
    private static final SourceModel.Expr strictCalValueOutputExpr = 
        SourceModel.Expr.BinaryOp.Compose.make(            
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.outputCalValueStrict),                    
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.toCalValue));    
    
        
        
    /** Compose the output with the Prelude.output function */
    public static final OutputPolicy DEFAULT_OUTPUT_POLICY =
        make(SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.output));
    
    /**
     * Compose the output with (Prelude.output # List.toJIterator).
     * This will output a CAL list to a Java Object that can then be cast to a java.util.Iterator.
     * The iterator can then be used to allow the Java client to marshal the elements of the CAL list under Java's control.
     */
    public static final OutputPolicy ITERATOR_OUTPUT_POLICY = make (iteratorValueOutputExpr);      
                        

    /** 
     * Compose the output with ((Prelude.output :: Prelude.CalValue -> Prelude.JObject) # Prelude.toCalValue).
     * This has the effect of outputting the CAL value to Java code without modification as an "opaque" value.
     * This value can then be passed back into CAL at some later point for further computations.
     * The value itself is not evaluated i.e. is "untouched".
     */
    public static final OutputPolicy CAL_VALUE_OUTPUT_POLICY = make (calValueOutputExpr);
        
    /**
     * Compose the output with ((Prelude.outputCalValueStrict # Prelude.toCalValue).
     * This has the effect of outputting the CAL value to Java code without modification as an "opaque" value.
     * This value can then be passed back into CAL at some later point for further computations.
     * The value itself is first evaluated to weak-head normal form. This is in contrast to the CAL_VALUE_OUTPUT_POLICY
     * which is purely lazy. This is sometimes needed when the external Java client wants to control the
     * order of side effects in a series of CAL computations.    
     */
    public static final OutputPolicy STRICT_CAL_VALUE_OUTPUT_POLICY = make (strictCalValueOutputExpr);
                        
    
    /**
     * This is a typed output policy for Prelude.Boolean values. It should be used instead of
     * the default output policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Boolean.
     */
    public static final OutputPolicy BOOLEAN_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Boolean));  
    
    /**
     * This is a typed output policy for Prelude.Char values. It should be used instead of
     * the default output policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Char.
     */
    public static final OutputPolicy CHAR_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Char)); 
     
    /**
     * This is a typed output policy for Prelude.Byte values. It should be used instead of
     * the default output policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Byte.
     */
    public static final OutputPolicy BYTE_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Byte));
    
    
    /**
     * This is a typed output policy for Prelude.Short values. It should be used instead of
     * the default output policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Short.
     */
    public static final OutputPolicy SHORT_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Short));    
           
    /**
     * This is a typed output policy for Prelude.Int values. It should be used instead of
     * the default output policy when the functionalAgent is polymorphic 
     * and it needs to be bound to an Int.
     */
    public static final OutputPolicy INT_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Int));

    /**
     * This is a typed input policy for Prelude.Long values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic
     * and it needs to be bound to a Long.
     */
    public static final OutputPolicy LONG_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Long));
        
    /**
     * This is a typed input policy for Prelude.Float values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Double.
     */
    public static final OutputPolicy FLOAT_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Float));        
    
    /**
     * This is a typed input policy for Prelude.Double values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Double.
     */
    public static final OutputPolicy DOUBLE_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Double));    
            
    /**
     * This is a typed output policy for Prelude.Double values. It should be used instead of
     * the default output policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Integer.
     */
    public static final OutputPolicy INTEGER_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Integer));
 
    /**
     * This is a typed output policy for Prelude.Decimal values. It should be used instead of
     * the default output policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a Decimal.
     */
    public static final OutputPolicy DECIMAL_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.Decimal));
    
    /**
     * This is a typed input policy for Prelude.String values. It should be used instead of
     * the default input policy when the functionalAgent is polymorphic  
     * and it needs to be bound to a String.
     */
    public static final OutputPolicy STRING_OUTPUT_POLICY = 
        makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.String)); 
    
              
           
    /**
     * This is the source model expression for the marshaler. It cannot be null.
     */
    private final SourceModel.Expr marshaler;
    
    /**
     * Constructs an OutputPolicy with an expression in source model
     * representation specifying the marshaler.
     * 
     * @param marshaler
     *            the marshaler that converts a CAL output value into the Java
     *            domain
     */
    private OutputPolicy (SourceModel.Expr marshaler) {
        if (marshaler == null) {
            throw (new NullPointerException ("The marshaler must be non-null for an OutputPolicy."));
        }  
        
        this.marshaler = marshaler;
    }


    /**
     * This is a factory method for creating a typed default output policy
     * It should be used when the output is ambiguous (e.g [a]) and an explicit type is required.
     * If the output is non-ambiguous it is easier to use DEFAULT_OUTPUT_POLICY
     * @param typeSignature the CAL type of the input
     * @return the input policy
     * 
     * @see #DEFAULT_OUTPUT_POLICY
     */
    public static OutputPolicy makeTypedDefaultOutputPolicy(SourceModel.TypeExprDefn typeSignature) {
        return make(SourceModel.Expr.ExprTypeSignature.make(
            SourceModel.Expr.makeGemCall(CAL_Prelude.Functions.output),
            SourceModel.TypeSignature.make( 
                SourceModel.TypeExprDefn.Function.make(
                    typeSignature,
                    SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.JObject)))));
    }


    /**
     * This is a factory method for creating a typed CAL_VALUE_OUTPUT_POLICY.
     * It should be used when the output is ambiguous (requires type class overloading resolution) 
     * and an explicit type is required.
     * If the output is non-ambiguous it is easier to use CAL_VALUE_OUTPUT_POLICY
     * @param type the CAL type of the output
     * @return the input policy
     * 
     * @see #CAL_VALUE_OUTPUT_POLICY
     */
    public static OutputPolicy makeTypedCalValueOutputPolicy(SourceModel.TypeExprDefn type) {
        return make(SourceModel.Expr.ExprTypeSignature.make(
            calValueOutputExpr,
            SourceModel.TypeSignature.make( 
                SourceModel.TypeExprDefn.Function.make(
                    type,
                    SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.JObject)
                    ))));
    }
    
    /**
     * This is a factory method for creating a typed STRICT_CAL_VALUE_OUTPUT_POLICY
     * It should be used when the output is ambiguous (requires type class overloading resolution) 
     * and an explicit type is required.
     * If the output is non-ambiguous it is easier to use STRICT_CAL_VALUE_OUTPUT_POLICY
     * @param type the CAL type of the output
     * @return the input policy
     * 
     * @see #STRICT_CAL_VALUE_OUTPUT_POLICY
     */
    public static OutputPolicy makeTypedStrictCalValueOutputPolicy(SourceModel.TypeExprDefn type) {
        return make(SourceModel.Expr.ExprTypeSignature.make(
            strictCalValueOutputExpr,
            SourceModel.TypeSignature.make( 
                SourceModel.TypeExprDefn.Function.make(
                    type,
                    SourceModel.TypeExprDefn.TypeCons.make(CAL_Prelude.TypeConstructors.JObject)
                ))));
    }    
    
    /**
     * This is a factory method for creating a typed Iterator output policy
     * It should be used when the output is ambiguous (e.g [a]) and an explicit type is required.
     * If the output is non-ambiguous it is easier to use ITERATOR_OUTPUT_POLICY
     * @param type the CAL type of the input
     * @return the output policy
     * 
     * @see #ITERATOR_OUTPUT_POLICY
     */
    public static OutputPolicy makeTypedIteratorOutputPolicy(SourceModel.TypeExprDefn type) {
        return make(SourceModel.Expr.ExprTypeSignature.make(
            iteratorValueOutputExpr,
            SourceModel.TypeSignature.make( 
                SourceModel.TypeExprDefn.Function.make(
                    type,
                    SourceModel.TypeExprDefn.TypeCons.make(CAL_List.TypeConstructors.JIterator)))));
    }
    

    /**
     * Makes an output policy by parsing a string representing the marshaler
     * @param marshaler  a string representing the marshaler
     * @return the new output policy.
     */
    public static OutputPolicy make(String marshaler) {
        if (marshaler == null) {
            throw (new NullPointerException ("The marshaler must be non-null for an OutputPolicy."));
        }
        
        //parse the marshaler
        Expr marshalerExpr = SourceModelUtilities.TextParsing.parseExprIntoSourceModel(marshaler);
        
        if (marshalerExpr == null) {
            throw (new IllegalArgumentException("The marshaler cannot be parsed: " + marshaler));
        }
        
        return new OutputPolicy(marshalerExpr);
    }

    /**
     * 
     * Makes an OutputPolicy with specified marshaler.
     * @param marshaler source model expression describing how to marshal a CAL output to a Java object
     * @return the new output policy
     */
    public static OutputPolicy make(Expr marshaler) {
        return new OutputPolicy(marshaler);
    }


    /**
     * Makes an OutputPolicy with a qualified name specifying the marshaler.
     * 
     * @param marshaler
     *            the marshaler that converts a CAL output value into the Java domain
     */
    public static OutputPolicy makeWithNamedMarshaler(QualifiedName marshaler) {
        return make(marshaler.getQualifiedName());
    }


    /**
     * Get the marshaler. This cannot be null.
     * @return the marshaler
     */
    public SourceModel.Expr getMarshaler() {
        return marshaler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString () {
        return "OutputPolicy: marshaler = " + getMarshaler();
    }



}
