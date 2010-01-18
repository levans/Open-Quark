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
 * PrimOp.java
 * Created: May 8, 2003  5:55:26 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.internal.machine.lecc;

import org.openquark.cal.internal.javamodel.JavaExpression;
import org.openquark.cal.internal.javamodel.JavaOperator;
import org.openquark.cal.internal.javamodel.JavaTypeName;
import org.openquark.cal.internal.javamodel.JavaExpression.CastExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodVariable;
import org.openquark.cal.internal.javamodel.JavaExpression.OperatorExpression;
import org.openquark.cal.internal.javamodel.JavaExpression.MethodInvocation.InvocationType;
import org.openquark.cal.internal.machine.CodeGenerationException;
import org.openquark.cal.internal.machine.primitiveops.PrimOps;



/**
 * This is the PrimOp class/interface.
 * This class contains defintions for the primitive operations.
 * <p>
 * Note that there is an assumption that all PrimOps produce a value in weak-head normal form.
 * 
 * <p>
 * Created: May 8, 2003 3:19:37 PM
 * @author RCypher
 */
final class PrimOp {    
    
    /**
     * Get the Java expression corresponding to a given primOp
     * @param code the PrimOp code.  This is defined in class PrimOps.  Shouldn't be NOP.
     * @param args the arguments to the op.
     * @return JavaExpression
     * @throws CodeGenerationException
     */
    static JavaExpression getPrimOpDefinition(int code, JavaExpression[] args) throws CodeGenerationException {
        switch (code) {
            case PrimOps.PRIMOP_NOP:
                throw new CodeGenerationException ("Attempt to generate code for PRIMOP_NOP.");

            case PrimOps.PRIMOP_OR:
                return new OperatorExpression.Binary(JavaOperator.CONDITIONAL_OR, args);

            case PrimOps.PRIMOP_AND:
                return new OperatorExpression.Binary(JavaOperator.CONDITIONAL_AND, args);

            case PrimOps.PRIMOP_EQUALS_INT:
                return new OperatorExpression.Binary(JavaOperator.EQUALS_INT, args);

            case PrimOps.PRIMOP_NOT_EQUALS_INT:
                return new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_INT, args);

            case PrimOps.PRIMOP_GREATER_THAN_INT:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_INT, args);

            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_INT:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_EQUALS_INT, args);

            case PrimOps.PRIMOP_LESS_THAN_INT:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_INT, args);

            case PrimOps.PRIMOP_LESS_THAN_EQUALS_INT:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_EQUALS_INT, args);

            case PrimOps.PRIMOP_ADD_INT:
                return new OperatorExpression.Binary(JavaOperator.ADD_INT, args);

            case PrimOps.PRIMOP_SUBTRACT_INT:
                return new OperatorExpression.Binary(JavaOperator.SUBTRACT_INT, args);

            case PrimOps.PRIMOP_MULTIPLY_INT:
                return new OperatorExpression.Binary(JavaOperator.MULTIPLY_INT, args);
            
            case PrimOps.PRIMOP_DIVIDE_INT:
                return new OperatorExpression.Binary(JavaOperator.DIVIDE_INT, args);            

            case PrimOps.PRIMOP_ADD_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.ADD_DOUBLE, args);

            case PrimOps.PRIMOP_SUBTRACT_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.SUBTRACT_DOUBLE, args);

            case PrimOps.PRIMOP_MULTIPLY_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.MULTIPLY_DOUBLE, args);

            case PrimOps.PRIMOP_DIVIDE_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.DIVIDE_DOUBLE, args);

            case PrimOps.PRIMOP_EQUALS_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.EQUALS_DOUBLE, args);

            case PrimOps.PRIMOP_NOT_EQUALS_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_DOUBLE, args);

            case PrimOps.PRIMOP_GREATER_THAN_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_DOUBLE, args);

            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_EQUALS_DOUBLE, args);

            case PrimOps.PRIMOP_LESS_THAN_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_DOUBLE, args);

            case PrimOps.PRIMOP_LESS_THAN_EQUALS_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_EQUALS_DOUBLE, args);
            
            case PrimOps.PRIMOP_ADD_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.ADD_FLOAT, args);

            case PrimOps.PRIMOP_SUBTRACT_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.SUBTRACT_FLOAT, args);

            case PrimOps.PRIMOP_MULTIPLY_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.MULTIPLY_FLOAT, args);

            case PrimOps.PRIMOP_DIVIDE_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.DIVIDE_FLOAT, args);

            case PrimOps.PRIMOP_EQUALS_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.EQUALS_FLOAT, args);

            case PrimOps.PRIMOP_NOT_EQUALS_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_FLOAT, args);

            case PrimOps.PRIMOP_GREATER_THAN_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_FLOAT, args);

            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_EQUALS_FLOAT, args);

            case PrimOps.PRIMOP_LESS_THAN_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_FLOAT, args);

            case PrimOps.PRIMOP_LESS_THAN_EQUALS_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_EQUALS_FLOAT, args);
            
            case PrimOps.PRIMOP_ADD_LONG:
                return new OperatorExpression.Binary(JavaOperator.ADD_LONG, args);

            case PrimOps.PRIMOP_SUBTRACT_LONG:
                return new OperatorExpression.Binary(JavaOperator.SUBTRACT_LONG, args);

            case PrimOps.PRIMOP_MULTIPLY_LONG:
                return new OperatorExpression.Binary(JavaOperator.MULTIPLY_LONG, args);

            case PrimOps.PRIMOP_DIVIDE_LONG:
                return new OperatorExpression.Binary(JavaOperator.DIVIDE_LONG, args);

            case PrimOps.PRIMOP_EQUALS_LONG:
                return new OperatorExpression.Binary(JavaOperator.EQUALS_LONG, args);

            case PrimOps.PRIMOP_NOT_EQUALS_LONG:
                return new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_LONG, args);

            case PrimOps.PRIMOP_GREATER_THAN_LONG:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_LONG, args);

            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_LONG:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_EQUALS_LONG, args);

            case PrimOps.PRIMOP_LESS_THAN_LONG:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_LONG, args);

            case PrimOps.PRIMOP_LESS_THAN_EQUALS_LONG:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_EQUALS_LONG, args);

            case PrimOps.PRIMOP_EQUALS_SHORT:
                return new OperatorExpression.Binary(JavaOperator.EQUALS_SHORT, args);

            case PrimOps.PRIMOP_NOT_EQUALS_SHORT:
                return new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_SHORT, args);

            case PrimOps.PRIMOP_GREATER_THAN_SHORT:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_SHORT, args);

            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_SHORT:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_EQUALS_SHORT, args);

            case PrimOps.PRIMOP_LESS_THAN_SHORT:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_SHORT, args);

            case PrimOps.PRIMOP_LESS_THAN_EQUALS_SHORT:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_EQUALS_SHORT, args);

            case PrimOps.PRIMOP_EQUALS_BYTE:
                return new OperatorExpression.Binary(JavaOperator.EQUALS_BYTE, args);

            case PrimOps.PRIMOP_NOT_EQUALS_BYTE:
                return new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_BYTE, args);

            case PrimOps.PRIMOP_GREATER_THAN_BYTE:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_BYTE, args);

            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_BYTE:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_EQUALS_BYTE, args);

            case PrimOps.PRIMOP_LESS_THAN_BYTE:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_BYTE, args);

            case PrimOps.PRIMOP_LESS_THAN_EQUALS_BYTE:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_EQUALS_BYTE, args);
           

            case PrimOps.PRIMOP_EQUALS_CHAR:
                return new OperatorExpression.Binary(JavaOperator.EQUALS_CHAR, args);

            case PrimOps.PRIMOP_NOT_EQUALS_CHAR:
                return new OperatorExpression.Binary(JavaOperator.NOT_EQUALS_CHAR, args);

            case PrimOps.PRIMOP_GREATER_THAN_CHAR:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_CHAR, args);

            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_CHAR:
                return new OperatorExpression.Binary(JavaOperator.GREATER_THAN_EQUALS_CHAR, args);

            case PrimOps.PRIMOP_LESS_THAN_CHAR:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_CHAR, args);

            case PrimOps.PRIMOP_LESS_THAN_EQUALS_CHAR:
                return new OperatorExpression.Binary(JavaOperator.LESS_THAN_EQUALS_CHAR, args);
    
            case PrimOps.PRIMOP_NEGATE_INT:
                return new OperatorExpression.Unary(JavaOperator.NEGATE_INT, args[0]);
                
            case PrimOps.PRIMOP_REMAINDER_INT:
                return new OperatorExpression.Binary(JavaOperator.REM_INT, args);                

            case PrimOps.PRIMOP_NEGATE_DOUBLE:
                return new OperatorExpression.Unary(JavaOperator.NEGATE_DOUBLE, args[0]);
                
            case PrimOps.PRIMOP_REMAINDER_DOUBLE:
                return new OperatorExpression.Binary(JavaOperator.REM_DOUBLE, args);                   
            
            case PrimOps.PRIMOP_NEGATE_FLOAT:
                return new OperatorExpression.Unary(JavaOperator.NEGATE_FLOAT, args[0]);
                
            case PrimOps.PRIMOP_REMAINDER_FLOAT:
                return new OperatorExpression.Binary(JavaOperator.REM_FLOAT, args);                       
            
            case PrimOps.PRIMOP_NEGATE_LONG:
                return new OperatorExpression.Unary(JavaOperator.NEGATE_LONG, args[0]);
                
            case PrimOps.PRIMOP_REMAINDER_LONG:
                return new OperatorExpression.Binary(JavaOperator.REM_LONG, args);                            

            case PrimOps.PRIMOP_FIELD_NAMES:
            {                
                //((RTRecordValue)args[0]).fieldNames()
                return new MethodInvocation.Instance(new CastExpression(JavaTypeNames.RTRECORD_VALUE, args[0]),
                    "fieldNames", JavaTypeName.LIST, InvocationType.VIRTUAL);                              
            }
            
            case PrimOps.PRIMOP_FIELD_VALUES:
            {                
                //((RTRecordValue)args[0]).fieldValues()
                return new MethodInvocation.Instance(new CastExpression(JavaTypeNames.RTRECORD_VALUE, args[0]),
                    "fieldValues", JavaTypeName.LIST, InvocationType.VIRTUAL);                              
            }            
            
            case PrimOps.PRIMOP_HAS_FIELD:
            {  
                //((RTRecordValue)args[0]).hasField(args[1])            
                return new MethodInvocation.Instance(new CastExpression(JavaTypeNames.RTRECORD_VALUE, args[0]), "hasField",
                    args[1], JavaTypeName.STRING, JavaTypeName.BOOLEAN, InvocationType.VIRTUAL);
            }
            
            case PrimOps.PRIMOP_RECORD_FIELD_INDEX:
            {  
                //((RTRecordValue)args[0]).indexOfField(args[1])            
                return new MethodInvocation.Instance(
                        new CastExpression(JavaTypeNames.RTRECORD_VALUE, args[0]),
                        "indexOfField", args[1], JavaTypeName.STRING, JavaTypeName.INT, InvocationType.VIRTUAL);
            }
            
            case PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT: 
            {
                return new JavaExpression.MethodInvocation.Static (JavaTypeNames.RTDATA_OPAQUE,
                                                                   "make",
                                                                   args[0],
                                                                   JavaTypeName.OBJECT,
                                                                   JavaTypeNames.RTDATA_OPAQUE);
            }
            
            case PrimOps.PRIMOP_OBJECT_TO_CAL_VALUE:
            {
                //note that the call to evaluate is necessary because the code generator makes some assumptions that all PrimOps produce
                //a value in weak-head normal form.
                //((RTValue) args[0]).evaluate($ec);
                return SCJavaDefn.createInvocation(new JavaExpression.CastExpression(JavaTypeNames.RTVALUE, args[0]), SCJavaDefn.EVALUATE, SCJavaDefn.EXECUTION_CONTEXT_VAR);
            }
            
            case PrimOps.PRIMOP_MAKE_ITERATOR:
            {                
                //new RTCalListIterator(args[0], args[1], $ec)               
                JavaExpression[] arguments = new JavaExpression[] {args[0], args[1], new MethodVariable(SCJavaDefn.EXECUTION_CONTEXT_NAME)};
                JavaTypeName[] argTypes = new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeNames.RTVALUE, JavaTypeNames.RTEXECUTION_CONTEXT};               
                return new JavaExpression.ClassInstanceCreationExpression(JavaTypeNames.RTCAL_LIST_ITERATOR, arguments, argTypes);
            }            
            
            case PrimOps.PRIMOP_MAKE_COMPARATOR:
            {                
                //new RTComparator(args[0], $ec)               
                JavaExpression[] arguments = new JavaExpression[] {args[0], new MethodVariable(SCJavaDefn.EXECUTION_CONTEXT_NAME)};
                JavaTypeName[] argTypes = new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeNames.RTEXECUTION_CONTEXT};               
                return new JavaExpression.ClassInstanceCreationExpression(JavaTypeNames.RTCOMPARATOR, arguments, argTypes);
            }
            
            case PrimOps.PRIMOP_MAKE_EQUIVALENCE_RELATION:
            {
                //new RTEquivalenceRelation(args[0], $ec)                
                JavaExpression[] arguments = new JavaExpression[] {args[0], new MethodVariable(SCJavaDefn.EXECUTION_CONTEXT_NAME)};
                JavaTypeName[] argTypes = new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeNames.RTEXECUTION_CONTEXT};               
                return new JavaExpression.ClassInstanceCreationExpression(JavaTypeNames.RTEQUIVALENCE_RELATION, arguments, argTypes);                                
            }
            
            case PrimOps.PRIMOP_MAKE_CAL_FUNCTION:
            {                
                //new RTCalFunction(args[0], $ec)               
                JavaExpression[] arguments = new JavaExpression[] {args[0], new MethodVariable(SCJavaDefn.EXECUTION_CONTEXT_NAME)};
                JavaTypeName[] argTypes = new JavaTypeName[] {JavaTypeNames.RTVALUE, JavaTypeNames.RTEXECUTION_CONTEXT};               
                return new JavaExpression.ClassInstanceCreationExpression(JavaTypeNames.RTCAL_FUNCTION, arguments, argTypes);
            }            
            
            case PrimOps.PRIMOP_BITWISE_AND_INT: {
                return new OperatorExpression.Binary (JavaOperator.BITWISE_AND_INT, args);
            }
            
            case PrimOps.PRIMOP_BITWISE_OR_INT: {
                return new OperatorExpression.Binary (JavaOperator.BITWISE_OR_INT, args);
            }
            
            case PrimOps.PRIMOP_BITWISE_XOR_INT: {
                return new OperatorExpression.Binary (JavaOperator.BITWISE_XOR_INT, args);
            }
            
            case PrimOps.PRIMOP_COMPLEMENT_INT: {
                return new OperatorExpression.Unary (JavaOperator.COMPLEMENT_INT, args[0]);
            }
            
            case PrimOps.PRIMOP_SHIFTL_INT: {
                return new OperatorExpression.Binary (JavaOperator.SHIFTL_INT, args);
            }
            
            case PrimOps.PRIMOP_SHIFTR_INT: {
                return new OperatorExpression.Binary (JavaOperator.SHIFTR_INT, args);
            }
            
            case PrimOps.PRIMOP_SHIFTR_UNSIGNED_INT: {
                return new OperatorExpression.Binary (JavaOperator.SHIFTR_UNSIGNED_INT, args);
            }

            case PrimOps.PRIMOP_BITWISE_AND_LONG: {
                return new OperatorExpression.Binary (JavaOperator.BITWISE_AND_LONG, args);
            }
            
            case PrimOps.PRIMOP_BITWISE_OR_LONG: {
                return new OperatorExpression.Binary (JavaOperator.BITWISE_OR_LONG, args);
            }
            
            case PrimOps.PRIMOP_BITWISE_XOR_LONG: {
                return new OperatorExpression.Binary (JavaOperator.BITWISE_XOR_LONG, args);
            }
            
            case PrimOps.PRIMOP_COMPLEMENT_LONG: {
                return new OperatorExpression.Unary (JavaOperator.COMPLEMENT_LONG, args[0]);
            }
            
            case PrimOps.PRIMOP_SHIFTL_LONG: {
                return new OperatorExpression.Binary (JavaOperator.SHIFTL_LONG, args);
            }
            
            case PrimOps.PRIMOP_SHIFTR_LONG: {
                return new OperatorExpression.Binary (JavaOperator.SHIFTR_LONG, args);
            }
            
            case PrimOps.PRIMOP_SHIFTR_UNSIGNED_LONG: {
                return new OperatorExpression.Binary (JavaOperator.SHIFTR_UNSIGNED_LONG, args);
            }
            
            case PrimOps.PRIMOP_EXECUTION_CONTEXT:
            {  
                //reference to the "$ec" execution context variable
                return SCJavaDefn.EXECUTION_CONTEXT_VAR;
            }            
            
            // This is now handled higher up since more context information is needed
            // in order to add the source position to the error message.
            //
            default:
            {
                throw new CodeGenerationException("Unrecognized PrimOp: " + code + ".");
            }
        }
        
    }
    
    /**    
     * @param primOp
     * @return return type of the primitive op. null for the case of PRIMOP_FOREIGNSC. 
     * @throws CodeGenerationException
     */
    static JavaTypeName getTypeNameForPrimOp (int primOp) throws CodeGenerationException {
        switch (primOp) {
            case PrimOps.PRIMOP_NOP:
                throw new CodeGenerationException ("attempt to get type for primop_nop.");
    
            case PrimOps.PRIMOP_OR:
            case PrimOps.PRIMOP_AND:
            case PrimOps.PRIMOP_EQUALS_INT:
            case PrimOps.PRIMOP_NOT_EQUALS_INT:
            case PrimOps.PRIMOP_GREATER_THAN_INT:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_INT:
            case PrimOps.PRIMOP_LESS_THAN_INT:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_INT:
            case PrimOps.PRIMOP_EQUALS_DOUBLE:
            case PrimOps.PRIMOP_NOT_EQUALS_DOUBLE:
            case PrimOps.PRIMOP_GREATER_THAN_DOUBLE:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_DOUBLE:
            case PrimOps.PRIMOP_LESS_THAN_DOUBLE:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_DOUBLE:
            case PrimOps.PRIMOP_EQUALS_LONG:
            case PrimOps.PRIMOP_NOT_EQUALS_LONG:
            case PrimOps.PRIMOP_GREATER_THAN_LONG:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_LONG:
            case PrimOps.PRIMOP_LESS_THAN_LONG:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_LONG:
            case PrimOps.PRIMOP_EQUALS_CHAR:
            case PrimOps.PRIMOP_NOT_EQUALS_CHAR:
            case PrimOps.PRIMOP_GREATER_THAN_CHAR:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_CHAR:
            case PrimOps.PRIMOP_LESS_THAN_CHAR:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_CHAR:
            case PrimOps.PRIMOP_EQUALS_SHORT:
            case PrimOps.PRIMOP_NOT_EQUALS_SHORT:
            case PrimOps.PRIMOP_GREATER_THAN_SHORT:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_SHORT:
            case PrimOps.PRIMOP_LESS_THAN_SHORT:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_SHORT:
            case PrimOps.PRIMOP_EQUALS_FLOAT:
            case PrimOps.PRIMOP_NOT_EQUALS_FLOAT:
            case PrimOps.PRIMOP_GREATER_THAN_FLOAT:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_FLOAT:
            case PrimOps.PRIMOP_LESS_THAN_FLOAT:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_FLOAT:
            case PrimOps.PRIMOP_EQUALS_BYTE:
            case PrimOps.PRIMOP_NOT_EQUALS_BYTE:
            case PrimOps.PRIMOP_GREATER_THAN_BYTE:
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_BYTE:
            case PrimOps.PRIMOP_LESS_THAN_BYTE:
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_BYTE:
            case PrimOps.PRIMOP_HAS_FIELD:
                return JavaTypeName.BOOLEAN;
            
            case PrimOps.PRIMOP_ADD_INT:
            case PrimOps.PRIMOP_SUBTRACT_INT:
            case PrimOps.PRIMOP_MULTIPLY_INT:
            case PrimOps.PRIMOP_DIVIDE_INT:            
            case PrimOps.PRIMOP_NEGATE_INT:
            case PrimOps.PRIMOP_REMAINDER_INT:            
            case PrimOps.PRIMOP_BITWISE_AND_INT:
            case PrimOps.PRIMOP_BITWISE_OR_INT:
            case PrimOps.PRIMOP_BITWISE_XOR_INT:
            case PrimOps.PRIMOP_COMPLEMENT_INT:
            case PrimOps.PRIMOP_SHIFTL_INT:
            case PrimOps.PRIMOP_SHIFTR_INT:
            case PrimOps.PRIMOP_SHIFTR_UNSIGNED_INT:
            case PrimOps.PRIMOP_RECORD_FIELD_INDEX:
                return JavaTypeName.INT;
            
            case PrimOps.PRIMOP_ADD_DOUBLE:
            case PrimOps.PRIMOP_SUBTRACT_DOUBLE:
            case PrimOps.PRIMOP_MULTIPLY_DOUBLE:
            case PrimOps.PRIMOP_DIVIDE_DOUBLE:
            case PrimOps.PRIMOP_NEGATE_DOUBLE:
            case PrimOps.PRIMOP_REMAINDER_DOUBLE:           
                return JavaTypeName.DOUBLE;
            
            case PrimOps.PRIMOP_ADD_LONG:
            case PrimOps.PRIMOP_SUBTRACT_LONG:
            case PrimOps.PRIMOP_MULTIPLY_LONG:
            case PrimOps.PRIMOP_DIVIDE_LONG:
            case PrimOps.PRIMOP_NEGATE_LONG:
            case PrimOps.PRIMOP_REMAINDER_LONG:            
            case PrimOps.PRIMOP_BITWISE_AND_LONG:
            case PrimOps.PRIMOP_BITWISE_OR_LONG:
            case PrimOps.PRIMOP_BITWISE_XOR_LONG:
            case PrimOps.PRIMOP_COMPLEMENT_LONG:
            case PrimOps.PRIMOP_SHIFTL_LONG:
            case PrimOps.PRIMOP_SHIFTR_LONG:
            case PrimOps.PRIMOP_SHIFTR_UNSIGNED_LONG:
                return JavaTypeName.LONG;
            
            case PrimOps.PRIMOP_ADD_FLOAT:
            case PrimOps.PRIMOP_SUBTRACT_FLOAT:
            case PrimOps.PRIMOP_MULTIPLY_FLOAT:
            case PrimOps.PRIMOP_DIVIDE_FLOAT:
            case PrimOps.PRIMOP_NEGATE_FLOAT:
            case PrimOps.PRIMOP_REMAINDER_FLOAT:           
                return JavaTypeName.FLOAT;
            
            case PrimOps.PRIMOP_FIELD_NAMES:
            case PrimOps.PRIMOP_FIELD_VALUES:
                return JavaTypeName.LIST;
            
            case PrimOps.PRIMOP_OBJECT_TO_CAL_VALUE:
                return JavaTypeName.CAL_VALUE;
                
            case PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT:
                return JavaTypeName.OBJECT;
                
            case PrimOps.PRIMOP_MAKE_ITERATOR:
                return JavaTypeName.ITERATOR;
            
            case PrimOps.PRIMOP_MAKE_COMPARATOR:
                return JavaTypeName.COMPARATOR;
            
            case PrimOps.PRIMOP_MAKE_EQUIVALENCE_RELATION:
                return JavaTypeName.EQUIVALENCE_RELATION;
                
            case PrimOps.PRIMOP_MAKE_CAL_FUNCTION:
                return JavaTypeName.CAL_FUNCTION;                
                
            case PrimOps.PRIMOP_EXECUTION_CONTEXT:
                return JavaTypeNames.RTEXECUTION_CONTEXT;
            
            case PrimOps.PRIMOP_FOREIGN_FUNCTION:
                //more information is needed to determine the return type of a foreign function...
                return null;
            
            default: 
            {
                throw new CodeGenerationException("Unrecognized primitive op code " + primOp);
            }
        }              
    }
    
    static final JavaTypeName getTypeNameForPrimOpArgument (int op, int argNum) throws CodeGenerationException {
        switch (op) {
        case PrimOps.PRIMOP_NOP:
            throw new CodeGenerationException ("Attemp to get argument type for PRIMOP_NOP");
        
        case PrimOps.PRIMOP_OR:
        case PrimOps.PRIMOP_AND:
            return JavaTypeName.BOOLEAN;

        case PrimOps.PRIMOP_EQUALS_INT:
        case PrimOps.PRIMOP_NOT_EQUALS_INT:
        case PrimOps.PRIMOP_GREATER_THAN_INT:
        case PrimOps.PRIMOP_GREATER_THAN_EQUALS_INT:
        case PrimOps.PRIMOP_LESS_THAN_INT:
        case PrimOps.PRIMOP_LESS_THAN_EQUALS_INT:
        case PrimOps.PRIMOP_ADD_INT:
        case PrimOps.PRIMOP_SUBTRACT_INT:
        case PrimOps.PRIMOP_MULTIPLY_INT:
        case PrimOps.PRIMOP_DIVIDE_INT:
        case PrimOps.PRIMOP_NEGATE_INT:
        case PrimOps.PRIMOP_REMAINDER_INT:               
        case PrimOps.PRIMOP_BITWISE_AND_INT: 
        case PrimOps.PRIMOP_BITWISE_OR_INT: 
        case PrimOps.PRIMOP_BITWISE_XOR_INT: 
        case PrimOps.PRIMOP_COMPLEMENT_INT: 
        case PrimOps.PRIMOP_SHIFTL_INT: 
        case PrimOps.PRIMOP_SHIFTR_INT: 
        case PrimOps.PRIMOP_SHIFTR_UNSIGNED_INT: 
            return JavaTypeName.INT;
        
        case PrimOps.PRIMOP_EQUALS_LONG:
        case PrimOps.PRIMOP_NOT_EQUALS_LONG:
        case PrimOps.PRIMOP_GREATER_THAN_LONG:
        case PrimOps.PRIMOP_GREATER_THAN_EQUALS_LONG:
        case PrimOps.PRIMOP_LESS_THAN_LONG:
        case PrimOps.PRIMOP_LESS_THAN_EQUALS_LONG:
        case PrimOps.PRIMOP_ADD_LONG:
        case PrimOps.PRIMOP_SUBTRACT_LONG:
        case PrimOps.PRIMOP_MULTIPLY_LONG:
        case PrimOps.PRIMOP_DIVIDE_LONG:
        case PrimOps.PRIMOP_NEGATE_LONG:
        case PrimOps.PRIMOP_REMAINDER_LONG:                    
        case PrimOps.PRIMOP_BITWISE_AND_LONG: 
        case PrimOps.PRIMOP_BITWISE_OR_LONG: 
        case PrimOps.PRIMOP_BITWISE_XOR_LONG: 
        case PrimOps.PRIMOP_COMPLEMENT_LONG: 
        case PrimOps.PRIMOP_SHIFTL_LONG: 
        case PrimOps.PRIMOP_SHIFTR_LONG: 
        case PrimOps.PRIMOP_SHIFTR_UNSIGNED_LONG: 
            return JavaTypeName.LONG;

        case PrimOps.PRIMOP_EQUALS_SHORT:
        case PrimOps.PRIMOP_NOT_EQUALS_SHORT:
        case PrimOps.PRIMOP_GREATER_THAN_SHORT:
        case PrimOps.PRIMOP_GREATER_THAN_EQUALS_SHORT:
        case PrimOps.PRIMOP_LESS_THAN_SHORT:
        case PrimOps.PRIMOP_LESS_THAN_EQUALS_SHORT:
            return JavaTypeName.SHORT;

        case PrimOps.PRIMOP_EQUALS_BYTE:
        case PrimOps.PRIMOP_NOT_EQUALS_BYTE:
        case PrimOps.PRIMOP_GREATER_THAN_BYTE:
        case PrimOps.PRIMOP_GREATER_THAN_EQUALS_BYTE:
        case PrimOps.PRIMOP_LESS_THAN_BYTE:
        case PrimOps.PRIMOP_LESS_THAN_EQUALS_BYTE:
            return JavaTypeName.BYTE;

        case PrimOps.PRIMOP_EQUALS_FLOAT:
        case PrimOps.PRIMOP_NOT_EQUALS_FLOAT:
        case PrimOps.PRIMOP_GREATER_THAN_FLOAT:
        case PrimOps.PRIMOP_GREATER_THAN_EQUALS_FLOAT:
        case PrimOps.PRIMOP_LESS_THAN_FLOAT:
        case PrimOps.PRIMOP_LESS_THAN_EQUALS_FLOAT:
        case PrimOps.PRIMOP_ADD_FLOAT:
        case PrimOps.PRIMOP_SUBTRACT_FLOAT:
        case PrimOps.PRIMOP_MULTIPLY_FLOAT:
        case PrimOps.PRIMOP_DIVIDE_FLOAT:
        case PrimOps.PRIMOP_NEGATE_FLOAT:
        case PrimOps.PRIMOP_REMAINDER_FLOAT:                     
            return JavaTypeName.FLOAT;

        case PrimOps.PRIMOP_EQUALS_DOUBLE:
        case PrimOps.PRIMOP_NOT_EQUALS_DOUBLE:
        case PrimOps.PRIMOP_GREATER_THAN_DOUBLE:
        case PrimOps.PRIMOP_GREATER_THAN_EQUALS_DOUBLE:
        case PrimOps.PRIMOP_LESS_THAN_DOUBLE:
        case PrimOps.PRIMOP_LESS_THAN_EQUALS_DOUBLE:
        case PrimOps.PRIMOP_ADD_DOUBLE:
        case PrimOps.PRIMOP_SUBTRACT_DOUBLE:
        case PrimOps.PRIMOP_MULTIPLY_DOUBLE:
        case PrimOps.PRIMOP_DIVIDE_DOUBLE:
        case PrimOps.PRIMOP_NEGATE_DOUBLE:
        case PrimOps.PRIMOP_REMAINDER_DOUBLE:                     
            return JavaTypeName.DOUBLE;

        case PrimOps.PRIMOP_EQUALS_CHAR:
        case PrimOps.PRIMOP_NOT_EQUALS_CHAR:
        case PrimOps.PRIMOP_GREATER_THAN_CHAR:
        case PrimOps.PRIMOP_GREATER_THAN_EQUALS_CHAR:
        case PrimOps.PRIMOP_LESS_THAN_CHAR:
        case PrimOps.PRIMOP_LESS_THAN_EQUALS_CHAR:
            return JavaTypeName.CHAR;
        
        case PrimOps.PRIMOP_FIELD_NAMES: 
        case PrimOps.PRIMOP_FIELD_VALUES:
            return JavaTypeNames.RTVALUE;
            
        case PrimOps.PRIMOP_HAS_FIELD:
        case PrimOps.PRIMOP_RECORD_FIELD_INDEX:
        {
            if (argNum == 0) {
                return JavaTypeNames.RTVALUE;
            } else {
                return JavaTypeName.STRING;
            }
        } 

        case PrimOps.PRIMOP_OBJECT_TO_CAL_VALUE:
            return JavaTypeName.OBJECT;
        
        case PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT:
            return JavaTypeNames.RTVALUE;
            
        case PrimOps.PRIMOP_MAKE_ITERATOR:
        case PrimOps.PRIMOP_MAKE_COMPARATOR:
        case PrimOps.PRIMOP_MAKE_EQUIVALENCE_RELATION:
        case PrimOps.PRIMOP_MAKE_CAL_FUNCTION:
            return JavaTypeNames.RTVALUE;    
            
        case PrimOps.PRIMOP_EXECUTION_CONTEXT:
            throw new CodeGenerationException("PRIMOP_EXECUTION_CONTEXT has no arguments.");

        case PrimOps.PRIMOP_FOREIGN_FUNCTION:
            // can't determine the arg type here.
            return null;            
            
        default:
            throw new CodeGenerationException("Unrecognized primop " + op + " in PrimOp.getTypeNameForPrimOpArgument.");        
    }       
        
    }
}
