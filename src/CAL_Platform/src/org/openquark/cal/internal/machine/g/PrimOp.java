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
 * Creation date: Dec 19, 2002
 * By: rcypher
 */

package org.openquark.cal.internal.machine.g;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.machine.primitiveops.PrimOps;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.CalFunction;
import org.openquark.cal.util.EquivalenceRelation;


/** 
 * This class contains information about the various primitive operations.
 * It also specifies the interface for an actual operation object.
 */
abstract class PrimOp {
   
    private PrimOps.PrimOpInfo info = null;
    
    /** 
     * Since this is a static map which is mutated we need to synchronize it.
     */
    private static Map<Integer, PrimOp> codeToOp = Collections.synchronizedMap (new HashMap<Integer, PrimOp> ());
    
    
    /** QualifiedName -> PrimOp
     *  Since this is a static map which is mutated we need to synchronize it. */
    private static Map<QualifiedName, PrimOp> nameToOp = Collections.synchronizedMap (new HashMap<QualifiedName, PrimOp> ());
    
    // static initialization block
    static {
        
        for (final PrimOps.PrimOpInfo primOp : PrimOps.primOps()) {
            addOp (primOp);
        }
    }

    private static void addOp (PrimOps.PrimOpInfo info) {
        PrimOp op;
        
        switch (info.getCode ()) {
            case PrimOps.PRIMOP_NOP:
                op = new PrimopNOP (info);
                break;
            
            case PrimOps.PRIMOP_OR:
                op = new PrimopOR (info);
                break;
            
            case PrimOps.PRIMOP_AND:
                op = new PrimopAND (info);
                break;
            
            case PrimOps.PRIMOP_EQUALS_INT:
                op = new PrimopEQUALS_INT (info);
                break;
            
            case PrimOps.PRIMOP_NOT_EQUALS_INT:
                op = new PrimopNOT_EQUALS_INT (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_INT:
                op = new PrimopGREATER_THAN_INT (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_INT:
                op = new PrimopGREATER_THAN_EQUALS_INT (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_INT:
                op = new PrimopLESS_THAN_INT (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_INT:
                op = new PrimopLESS_THAN_EQUALS_INT (info);
                break;
            
            case PrimOps.PRIMOP_ADD_INT:
                op = new PrimopADD_INT (info);
                break;
            
            case PrimOps.PRIMOP_SUBTRACT_INT:
                op = new PrimopSUBTRACT_INT (info);
                break;
            
            case PrimOps.PRIMOP_MULTIPLY_INT:
                op = new PrimopMULTIPLY_INT (info);
                break;

            case PrimOps.PRIMOP_DIVIDE_INT:
                op = new PrimopDIVIDE_INT (info);
                break;
            
            case PrimOps.PRIMOP_NEGATE_INT:
                op = new PrimopNEGATE_INT (info);
                break;
                
            case PrimOps.PRIMOP_REMAINDER_INT:
                op = new PrimopREMAINDER_INT (info);
                break;                
            
            case PrimOps.PRIMOP_EQUALS_DOUBLE:
                op = new PrimopEQUALS_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_NOT_EQUALS_DOUBLE:
                op = new PrimopNOT_EQUALS_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_DOUBLE:
                op = new PrimopGREATER_THAN_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_DOUBLE:
                op = new PrimopGREATER_THAN_EQUALS_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_DOUBLE:
                op = new PrimopLESS_THAN_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_DOUBLE:
                op = new PrimopLESS_THAN_EQUALS_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_ADD_DOUBLE:
                op = new PrimopADD_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_SUBTRACT_DOUBLE:
                op = new PrimopSUBTRACT_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_MULTIPLY_DOUBLE:
                op = new PrimopMULTIPLY_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_DIVIDE_DOUBLE:
                op = new PrimopDIVIDE_DOUBLE (info);
                break;
            
            case PrimOps.PRIMOP_NEGATE_DOUBLE:
                op = new PrimopNEGATE_DOUBLE (info);
                break;
                
            case PrimOps.PRIMOP_REMAINDER_DOUBLE:
                op = new PrimopREMAINDER_DOUBLE (info);
                break;                
            
            case PrimOps.PRIMOP_EQUALS_LONG:
                op = new PrimopEQUALS_LONG (info);
                break;
            
            case PrimOps.PRIMOP_NOT_EQUALS_LONG:
                op = new PrimopNOT_EQUALS_LONG (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_LONG:
                op = new PrimopGREATER_THAN_LONG (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_LONG:
                op = new PrimopGREATER_THAN_EQUALS_LONG (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_LONG:
                op = new PrimopLESS_THAN_LONG (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_LONG:
                op = new PrimopLESS_THAN_EQUALS_LONG (info);
                break;
            
            case PrimOps.PRIMOP_ADD_LONG:
                op = new PrimopADD_LONG (info);
                break;
            
            case PrimOps.PRIMOP_SUBTRACT_LONG:
                op = new PrimopSUBTRACT_LONG (info);
                break;
            
            case PrimOps.PRIMOP_MULTIPLY_LONG:
                op = new PrimopMULTIPLY_LONG (info);
                break;
            
            case PrimOps.PRIMOP_DIVIDE_LONG:
                op = new PrimopDIVIDE_LONG (info);
                break;
            
            case PrimOps.PRIMOP_NEGATE_LONG:
                op = new PrimopNEGATE_LONG (info);
                break;
                
           case PrimOps.PRIMOP_REMAINDER_LONG:
                op = new PrimopREMAINDER_LONG (info);
                break;                                           
            
            case PrimOps.PRIMOP_EQUALS_BYTE:
                op = new PrimopEQUALS_BYTE (info);
                break;
            
            case PrimOps.PRIMOP_NOT_EQUALS_BYTE:
                op = new PrimopNOT_EQUALS_BYTE (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_BYTE:
                op = new PrimopGREATER_THAN_BYTE (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_BYTE:
                op = new PrimopGREATER_THAN_EQUALS_BYTE (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_BYTE:
                op = new PrimopLESS_THAN_BYTE (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_BYTE:
                op = new PrimopLESS_THAN_EQUALS_BYTE (info);
                break;
            
            case PrimOps.PRIMOP_EQUALS_SHORT:
                op = new PrimopEQUALS_SHORT (info);
                break;
            
            case PrimOps.PRIMOP_NOT_EQUALS_SHORT:
                op = new PrimopNOT_EQUALS_SHORT (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_SHORT:
                op = new PrimopGREATER_THAN_SHORT (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_SHORT:
                op = new PrimopGREATER_THAN_EQUALS_SHORT (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_SHORT:
                op = new PrimopLESS_THAN_SHORT (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_SHORT:
                op = new PrimopLESS_THAN_EQUALS_SHORT (info);
                break;
            
            case PrimOps.PRIMOP_EQUALS_FLOAT:
                op = new PrimopEQUALS_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_NOT_EQUALS_FLOAT:
                op = new PrimopNOT_EQUALS_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_FLOAT:
                op = new PrimopGREATER_THAN_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_FLOAT:
                op = new PrimopGREATER_THAN_EQUALS_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_FLOAT:
                op = new PrimopLESS_THAN_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_FLOAT:
                op = new PrimopLESS_THAN_EQUALS_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_ADD_FLOAT:
                op = new PrimopADD_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_SUBTRACT_FLOAT:
                op = new PrimopSUBTRACT_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_MULTIPLY_FLOAT:
                op = new PrimopMULTIPLY_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_DIVIDE_FLOAT:
                op = new PrimopDIVIDE_FLOAT (info);
                break;
            
            case PrimOps.PRIMOP_NEGATE_FLOAT:
                op = new PrimopNEGATE_FLOAT (info);
                break;
                
           case PrimOps.PRIMOP_REMAINDER_FLOAT:
                op = new PrimopREMAINDER_FLOAT (info);
                break;                
                                           
            case PrimOps.PRIMOP_EQUALS_CHAR:
                op = new PrimopEQUALS_CHAR (info);
                break;
            
            case PrimOps.PRIMOP_NOT_EQUALS_CHAR:
                op = new PrimopNOT_EQUALS_CHAR (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_CHAR:
                op = new PrimopGREATER_THAN_CHAR (info);
                break;
            
            case PrimOps.PRIMOP_GREATER_THAN_EQUALS_CHAR:
                op = new PrimopGREATER_THAN_EQUALS_CHAR (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_CHAR:
                op = new PrimopLESS_THAN_CHAR (info);
                break;
            
            case PrimOps.PRIMOP_LESS_THAN_EQUALS_CHAR:
                op = new PrimopLESS_THAN_EQUALS_CHAR (info);
                break;

            case PrimOps.PRIMOP_FIELD_NAMES:
            {
                op = new PrimOpFIELD_NAMES(info);
                break;               
            }
            
            case PrimOps.PRIMOP_FIELD_VALUES:
            {
                op = new PrimOpFIELD_VALUES(info);
                break;               
            }            
            
            case PrimOps.PRIMOP_HAS_FIELD:
            {
                op = new PrimOpHAS_FIELD(info);
                break;               
            }
            
            case PrimOps.PRIMOP_RECORD_FIELD_INDEX:
            {
                op = new PrimOpRECORD_FIELD_INDEX(info);
                break;               
            }
            
            case PrimOps.PRIMOP_OBJECT_TO_CAL_VALUE:
            {
                op = new PrimOpOBJECT_TO_CAL_VALUE(info);
                break;               
            }                
                
            case PrimOps.PRIMOP_CAL_VALUE_TO_OBJECT:
            {
                op = new PrimOpCAL_VALUE_TO_OBJECT(info);
                break;               
            }                
            
            case PrimOps.PRIMOP_MAKE_ITERATOR:
            {
                op = new PrimOpMAKE_ITERATOR(info);
                break;               
            }               
            
            case PrimOps.PRIMOP_MAKE_COMPARATOR:
            {
                op = new PrimOpMAKE_COMPARATOR(info);
                break;               
            }                
            
            case PrimOps.PRIMOP_MAKE_EQUIVALENCE_RELATION:
            {
                op = new PrimOpMAKE_EQUIVALENCE_RELATION(info);
                break;               
            }
            
            case PrimOps.PRIMOP_MAKE_CAL_FUNCTION:
            {
                op = new PrimOpMAKE_CAL_FUNCTION(info);
                break;               
            }               
              
            case PrimOps.PRIMOP_BITWISE_AND_LONG:
                op = new PrimopBITWISE_AND_LONG (info);
                break;               

            case PrimOps.PRIMOP_BITWISE_OR_LONG:
                op = new PrimopBITWISE_OR_LONG (info);
                break;               

            case PrimOps.PRIMOP_BITWISE_XOR_LONG:
                op = new PrimopBITWISE_XOR_LONG (info);
                break;               

            case PrimOps.PRIMOP_COMPLEMENT_LONG:
                op = new PrimopCOMPLEMENT_LONG (info);
                break;               
            
            case PrimOps.PRIMOP_SHIFTL_LONG:
                op = new PrimopSHIFTL_LONG (info);
                break;               
            
            case PrimOps.PRIMOP_SHIFTR_LONG:
                op = new PrimopSHIFTR_LONG (info);
                break;               

            case PrimOps.PRIMOP_SHIFTR_UNSIGNED_LONG:
                op = new PrimopSHIFTR_UNSIGNED_LONG (info);
                break;               

            case PrimOps.PRIMOP_BITWISE_AND_INT:
                op = new PrimopBITWISE_AND_INT (info);
                break;               

            case PrimOps.PRIMOP_BITWISE_OR_INT:
                op = new PrimopBITWISE_OR_INT (info);
                break;               

            case PrimOps.PRIMOP_BITWISE_XOR_INT:
                op = new PrimopBITWISE_XOR_INT (info);
                break;               

            case PrimOps.PRIMOP_COMPLEMENT_INT:
                op = new PrimopCOMPLEMENT_INT (info);
                break;               
            
            case PrimOps.PRIMOP_SHIFTL_INT:
                op = new PrimopSHIFTL_INT (info);
                break;               
            
            case PrimOps.PRIMOP_SHIFTR_INT:
                op = new PrimopSHIFTR_INT (info);
                break;               

            case PrimOps.PRIMOP_SHIFTR_UNSIGNED_INT:
                op = new PrimopSHIFTR_UNSIGNED_INT (info);
                break; 
                
            case PrimOps.PRIMOP_EXECUTION_CONTEXT:
            {
                op = new PrimopEXECUTION_CONTEXT (info);
                break;
            }
                
            case PrimOps.PRIMOP_EAGER:
                // Eager gets ignored, we simply end up forcing evaluation of its argument.
                return;
                
            default:
                throw new IllegalArgumentException("Missing implementation of primitive op: " + info.getName());
        }
        
        codeToOp.put (Integer.valueOf(info.getCode ()), op);
        nameToOp.put (info.getName (), op);
        
    }
            
    private PrimOp (PrimOps.PrimOpInfo info) {
        this.info = info;
    }
    
    public QualifiedName getName () {
        return info.getName();
    }
    public int getCode () {
        return info.getCode ();
    }
    public int getArity () {
        return info.getArity ();
    }
    public String getDescription () {
        return info.getDescription ();
    }
    
    public abstract void perform (Executor e) throws CALExecutorException;

    public static PrimOp getPrimOpForCode (int code) {
        return codeToOp.get (Integer.valueOf(code));
    }

    public static PrimOp getPrimOpForName (QualifiedName name) {
        return nameToOp.get (name);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////

    private static class PrimopNOP extends PrimOp {
        public PrimopNOP (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
        }
    }

    private static class PrimopOR extends PrimOp {
        public PrimopOR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {  
            Executor.GStack stack = e.stack;                        
            stack.pushBoolean(stack.popBoolean() || stack.popBoolean());                   
        }
    }

    private static class PrimopAND extends PrimOp { 
        public PrimopAND (PrimOps.PrimOpInfo info) {
            super (info);
        }

        @Override
        public void perform (Executor e) throws CALExecutorException {    
            Executor.GStack stack = e.stack;                       
            stack.pushBoolean(stack.popBoolean() && stack.popBoolean());                    
        }
    }

    //Built-in functions for basic integer comparison and arithmetic
    private static class  PrimopEQUALS_INT extends PrimOp {
        public PrimopEQUALS_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;
            stack.pushBoolean(stack.popInt() == stack.popInt());            
        }
    }

    private static class  PrimopNOT_EQUALS_INT extends PrimOp {
        public PrimopNOT_EQUALS_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popInt() != stack.popInt());            
        }
    }

    private static class  PrimopGREATER_THAN_INT extends PrimOp {
        public PrimopGREATER_THAN_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popInt() < stack.popInt());           
        }
    }

    private static class  PrimopGREATER_THAN_EQUALS_INT extends PrimOp {
        public PrimopGREATER_THAN_EQUALS_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popInt() <= stack.popInt());            
        }
    }

    private static class  PrimopLESS_THAN_INT extends PrimOp {
        public PrimopLESS_THAN_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popInt() > stack.popInt());            
        }
    }

    private static class  PrimopLESS_THAN_EQUALS_INT extends PrimOp {
        public PrimopLESS_THAN_EQUALS_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popInt() >= stack.popInt());            
        }
    }

    private static class  PrimopADD_INT extends PrimOp {
        public PrimopADD_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushInt (stack.popInt() + stack.popInt());
        }
    }

    private static class PrimopSUBTRACT_INT extends PrimOp {
        public PrimopSUBTRACT_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            int i = stack.popInt();
            stack.pushInt (stack.popInt() - i);
        }
    }

    private static class PrimopMULTIPLY_INT extends PrimOp {
        public PrimopMULTIPLY_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushInt (stack.popInt() * stack.popInt());
        }
    }
      
    private static class PrimopDIVIDE_INT extends PrimOp {
        public PrimopDIVIDE_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack; 
            int i = stack.popInt();
            stack.pushInt (stack.popInt() / i);
        }
    }    

    private static class PrimopNEGATE_INT extends PrimOp {
        public PrimopNEGATE_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushInt (-stack.popInt());
        }
    }
    
    private static class PrimopREMAINDER_INT extends PrimOp {
        public PrimopREMAINDER_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack; 
            int i = stack.popInt();
            stack.pushInt (stack.popInt() % i);
        }
    }    

    //Built-in functions for basic double comparison and arithmetic
    private static class PrimopEQUALS_DOUBLE extends PrimOp {
        public PrimopEQUALS_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popDouble() == stack.popDouble());            
        }
    }

    private static class PrimopNOT_EQUALS_DOUBLE extends PrimOp {
        public PrimopNOT_EQUALS_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popDouble() != stack.popDouble());            
        }
    }

    private static class PrimopGREATER_THAN_DOUBLE extends PrimOp {
        public PrimopGREATER_THAN_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popDouble() < stack.popDouble());            
        }
    }

    private static class PrimopGREATER_THAN_EQUALS_DOUBLE extends PrimOp {
        public PrimopGREATER_THAN_EQUALS_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popDouble() <= stack.popDouble());            
        }
    }

    private static class PrimopLESS_THAN_DOUBLE extends PrimOp {
        public PrimopLESS_THAN_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popDouble() > stack.popDouble());            
        }
    }

    private static class PrimopLESS_THAN_EQUALS_DOUBLE extends PrimOp {
        public PrimopLESS_THAN_EQUALS_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popDouble() >= stack.popDouble());           
        }
    }

    private static class PrimopADD_DOUBLE extends PrimOp {
        public PrimopADD_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushDouble (stack.popDouble() + stack.popDouble());
        }
    }

    private static class PrimopSUBTRACT_DOUBLE extends PrimOp {
        public PrimopSUBTRACT_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;   
            double d = stack.popDouble();
            stack.pushDouble (stack.popDouble() - d);
        }
    }

    private static class PrimopMULTIPLY_DOUBLE extends PrimOp {
        public PrimopMULTIPLY_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushDouble (stack.popDouble() * stack.popDouble());
        }
    }

    private static class PrimopDIVIDE_DOUBLE extends PrimOp {
        public PrimopDIVIDE_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack; 
            double d = stack.popDouble();
            stack.pushDouble (stack.popDouble() / d);
        }
    }

    private static class PrimopNEGATE_DOUBLE extends PrimOp {
        public PrimopNEGATE_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushDouble (- stack.popDouble());
        }
    }
    
    private static class PrimopREMAINDER_DOUBLE extends PrimOp {
        public PrimopREMAINDER_DOUBLE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack; 
            double d = stack.popDouble();
            stack.pushDouble (stack.popDouble() % d);
        }
    }    
    
    //  Built-in functions for basic long comparison and arithmetic
    private static class PrimopEQUALS_LONG extends PrimOp {
        public PrimopEQUALS_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popLong() == stack.popLong());            
        }
    }

    private static class PrimopNOT_EQUALS_LONG extends PrimOp {
        public PrimopNOT_EQUALS_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popLong() != stack.popLong());            
        }
    }

    private static class PrimopGREATER_THAN_LONG extends PrimOp {
        public PrimopGREATER_THAN_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popLong() < stack.popLong());            
        }
    }

    private static class PrimopGREATER_THAN_EQUALS_LONG extends PrimOp {
        public PrimopGREATER_THAN_EQUALS_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popLong() <= stack.popLong());            
        }
    }

    private static class PrimopLESS_THAN_LONG extends PrimOp {
        public PrimopLESS_THAN_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popLong() > stack.popLong());            
        }
    }

    private static class PrimopLESS_THAN_EQUALS_LONG extends PrimOp {
        public PrimopLESS_THAN_EQUALS_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popLong() >= stack.popLong());           
        }
    }

    private static class PrimopADD_LONG extends PrimOp {
        public PrimopADD_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushLong (stack.popLong() + stack.popLong());
        }
    }

    private static class PrimopSUBTRACT_LONG extends PrimOp {
        public PrimopSUBTRACT_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            long l = stack.popLong();
            stack.pushLong (stack.popLong() - l);
        }
    }

    private static class PrimopMULTIPLY_LONG extends PrimOp {
        public PrimopMULTIPLY_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushLong (stack.popLong() * stack.popLong());
        }
    }

    private static class PrimopDIVIDE_LONG extends PrimOp {
        public PrimopDIVIDE_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;  
            long l = stack.popLong();
            stack.pushLong (stack.popLong() / l);
        }
    }

    private static class PrimopNEGATE_LONG extends PrimOp {
        public PrimopNEGATE_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushLong (- stack.popLong());
        }
    }
    
    private static class PrimopREMAINDER_LONG extends PrimOp {
        public PrimopREMAINDER_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;  
            long l = stack.popLong();
            stack.pushLong (stack.popLong() % l);
        }
    }            

    //  Built-in functions for basic short comparison
    private static class PrimopEQUALS_SHORT extends PrimOp {
        public PrimopEQUALS_SHORT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popShort() == stack.popShort());            
        }
    }

    private static class PrimopNOT_EQUALS_SHORT extends PrimOp {
        public PrimopNOT_EQUALS_SHORT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popShort() != stack.popShort());            
        }
    }

    private static class PrimopGREATER_THAN_SHORT extends PrimOp {
        public PrimopGREATER_THAN_SHORT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popShort() < stack.popShort());            
        }
    }

    private static class PrimopGREATER_THAN_EQUALS_SHORT extends PrimOp {
        public PrimopGREATER_THAN_EQUALS_SHORT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popShort() <= stack.popShort());            
        }
    }

    private static class PrimopLESS_THAN_SHORT extends PrimOp {
        public PrimopLESS_THAN_SHORT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popShort() > stack.popShort());            
        }
    }

    private static class PrimopLESS_THAN_EQUALS_SHORT extends PrimOp {
        public PrimopLESS_THAN_EQUALS_SHORT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popShort() >= stack.popShort());           
        }
    } 

    //  Built-in functions for basic byte comparison
    private static class PrimopEQUALS_BYTE extends PrimOp {
        public PrimopEQUALS_BYTE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popByte() == stack.popByte());            
        }
    }

    private static class PrimopNOT_EQUALS_BYTE extends PrimOp {
        public PrimopNOT_EQUALS_BYTE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popByte() != stack.popByte());            
        }
    }

    private static class PrimopGREATER_THAN_BYTE extends PrimOp {
        public PrimopGREATER_THAN_BYTE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popByte() < stack.popByte());            
        }
    }

    private static class PrimopGREATER_THAN_EQUALS_BYTE extends PrimOp {
        public PrimopGREATER_THAN_EQUALS_BYTE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popByte() <= stack.popByte());            
        }
    }

    private static class PrimopLESS_THAN_BYTE extends PrimOp {
        public PrimopLESS_THAN_BYTE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popByte() > stack.popByte());            
        }
    }

    private static class PrimopLESS_THAN_EQUALS_BYTE extends PrimOp {
        public PrimopLESS_THAN_EQUALS_BYTE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popByte() >= stack.popByte());           
        }
    }  

    //  Built-in functions for basic float comparison and arithmetic
    private static class PrimopEQUALS_FLOAT extends PrimOp {
        public PrimopEQUALS_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popFloat() == stack.popFloat());            
        }
    }

    private static class PrimopNOT_EQUALS_FLOAT extends PrimOp {
        public PrimopNOT_EQUALS_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popFloat() != stack.popFloat());            
        }
    }

    private static class PrimopGREATER_THAN_FLOAT extends PrimOp {
        public PrimopGREATER_THAN_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean (stack.popFloat() < stack.popFloat());            
        }
    }

    private static class PrimopGREATER_THAN_EQUALS_FLOAT extends PrimOp {
        public PrimopGREATER_THAN_EQUALS_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popFloat() <= stack.popFloat());            
        }
    }

    private static class PrimopLESS_THAN_FLOAT extends PrimOp {
        public PrimopLESS_THAN_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popFloat() > stack.popFloat());            
        }
    }

    private static class PrimopLESS_THAN_EQUALS_FLOAT extends PrimOp {
        public PrimopLESS_THAN_EQUALS_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popFloat() >= stack.popFloat());           
        }
    }

    private static class PrimopADD_FLOAT extends PrimOp {
        public PrimopADD_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushFloat (stack.popFloat() + stack.popFloat());
        }
    }

    private static class PrimopSUBTRACT_FLOAT extends PrimOp {
        public PrimopSUBTRACT_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            float f = stack.popFloat();
            stack.pushFloat (stack.popFloat() - f);
        }
    }

    private static class PrimopMULTIPLY_FLOAT extends PrimOp {
        public PrimopMULTIPLY_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushFloat (stack.popFloat() * stack.popFloat());
        }
    }

    private static class PrimopDIVIDE_FLOAT extends PrimOp {
        public PrimopDIVIDE_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;  
            float f = stack.popFloat();
            stack.pushFloat (stack.popFloat() / f);
        }
    }

    private static class PrimopNEGATE_FLOAT extends PrimOp {
        public PrimopNEGATE_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushFloat (- stack.popFloat());
        }
    }
    
    private static class PrimopREMAINDER_FLOAT extends PrimOp {
        public PrimopREMAINDER_FLOAT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;  
            float f = stack.popFloat();
            stack.pushFloat (stack.popFloat() % f);
        }
    }       

    //Built-in functions for basic character comparison
    private static class PrimopEQUALS_CHAR extends PrimOp {
        public PrimopEQUALS_CHAR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {  
            Executor.GStack stack = e.stack;                        
            stack.pushBoolean(stack.popChar() == stack.popChar());                      
        }
    }

    private static class PrimopNOT_EQUALS_CHAR extends PrimOp {
        public PrimopNOT_EQUALS_CHAR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popChar() != stack.popChar());                      
        }
    }

    private static class PrimopGREATER_THAN_CHAR extends PrimOp {
        public PrimopGREATER_THAN_CHAR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;                        
            stack.pushBoolean(stack.popChar() < stack.popChar());            
        }
    }

    private static class PrimopGREATER_THAN_EQUALS_CHAR extends PrimOp {
        public PrimopGREATER_THAN_EQUALS_CHAR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popChar() <= stack.popChar());          
        }
    }

    private static class PrimopLESS_THAN_CHAR extends PrimOp {
        public PrimopLESS_THAN_CHAR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popChar() > stack.popChar());          
        }
    }

    private static class PrimopLESS_THAN_EQUALS_CHAR extends PrimOp {
        public PrimopLESS_THAN_EQUALS_CHAR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;             
            stack.pushBoolean(stack.popChar() >= stack.popChar());            
        }
    }

    private static class PrimOpFIELD_NAMES extends PrimOp {
        public PrimOpFIELD_NAMES  (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            NRecordValue recordValue = (NRecordValue)e.stack.pop();            
            e.stack.push (new NValObject(recordValue.fieldNames()));
        }
    }
    
    private static class PrimOpFIELD_VALUES extends PrimOp {
        public PrimOpFIELD_VALUES  (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            NRecordValue recordValue = (NRecordValue)e.stack.pop();            
            e.stack.push (new NValObject(recordValue.fieldValues()));
        }
    }    
    
    private static class PrimOpHAS_FIELD extends PrimOp {
        public PrimOpHAS_FIELD (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            String fieldName = e.stack.popString ();            
            NRecordValue recordValue = (NRecordValue)e.stack.pop ();
            e.stack.pushBoolean(recordValue.hasField(fieldName));
        }
    }
    
    private static class PrimOpRECORD_FIELD_INDEX extends PrimOp {
        public PrimOpRECORD_FIELD_INDEX (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            String fieldName = e.stack.popString ();            
            NRecordValue recordValue = (NRecordValue)e.stack.pop ();
            e.stack.pushInt(recordValue.indexOfField(fieldName));
        }
    }
    
    private static class PrimOpOBJECT_TO_CAL_VALUE extends PrimOp {
        public PrimOpOBJECT_TO_CAL_VALUE (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Node n = e.stack.pop ();
            e.stack.push ((Node)(((NValObject)n).getValue()));
        }
    }         
    
    private static class PrimOpCAL_VALUE_TO_OBJECT extends PrimOp {
        public PrimOpCAL_VALUE_TO_OBJECT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            e.stack.push (new NValObject (e.stack.pop()));
        }
    }
    
    /**
     * Implements the primitive function declared in the List module
     * makeIterator :: [a] -> (a -> JObject) -> JIterator
     * 
     * @author Bo Ilic
     */    
    private static final class PrimOpMAKE_ITERATOR extends PrimOp {
        
        private static final class CalListIterator implements Iterator<Object> {
            
            private Node calListValue;
            private final NInd elementMarshalingFunction;               
            private final Executor executor;
            
            private CalListIterator(Node calListValue, Node elementMarshalingFunction, Executor oldExecutor) {
                if (calListValue == null || elementMarshalingFunction == null || oldExecutor == null) {
                    throw new NullPointerException();
                }
                this.calListValue = calListValue;
                this.elementMarshalingFunction = (NInd)elementMarshalingFunction;
                this.executor = new Executor (oldExecutor.program, oldExecutor.resourceAccess, oldExecutor.getExecutionContext());
            }

            /** {@inheritDoc} */
            public boolean hasNext() {

                //evaluate calListValue to WHNF and see if it is a Cons. This means there is a next value
                                
                try {
                    this.executor.reset ();
                    this.executor.stack.push (calListValue);                    
                    this.executor.exec (new Code(Instruction.I_Eval));
                    
                    calListValue = this.executor.stack.pop();
                    
                    //0 == Prelude.Nil
                    //1 == Prelude.Cons
                    return calListValue.getOrdinalValue() == 1;
                    
                } catch (CALExecutorException cee) {
                    //wrap the exception up in a non-checked exception and rethrow.
                    throw new RuntimeException (cee);
                }                               
            }

            /** {@inheritDoc} */
            public Object next() {
                try {
                    this.executor.reset ();
                    this.executor.stack.push (calListValue);                    
                    this.executor.exec (new Code(Instruction.I_Eval));
                     
                    calListValue = this.executor.stack.pop();
                    switch (calListValue.getOrdinalValue()) {
                        case 0:
                        {
                            //Prelude.Nil
                            throw new NoSuchElementException();
                        }
    
                        case 1:
                        {
                            //Prelude.Cons                        
                            NConstr2 listCons = (NConstr2)calListValue;
                            Node headValue = listCons.getN0();
                            calListValue = listCons.getN1();
                            
                            executor.stack.push(elementMarshalingFunction.apply(headValue));
                            this.executor.exec (new Code(Instruction.I_Eval));
                            
                            return executor.stack.pop().getValue();
                        }
    
                        default:
                        {
                            throw new IndexOutOfBoundsException();
                        }
                    }

                } catch (CALExecutorException executorException) {
                    //wrap the exception up in a non-checked exception and rethrow.          
                    throw new RuntimeException(executorException);
                }       
            }

            /** {@inheritDoc} */
            public void remove() {
                throw new UnsupportedOperationException();
            }                     
        }
        
        public PrimOpMAKE_ITERATOR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Node elementMarshalingFunction = e.stack.pop();
            Node calListValue = e.stack.pop();
            e.stack.push(new NValObject(new CalListIterator(calListValue, elementMarshalingFunction, e)));           
        }
    }    
    
    /**
     * Implements the primitive function declared in the Prelude module
     * makeEquivalenceRelation :: (a -> a -> Ordering) -> Comparator
     * 
     * @author Bo Ilic
     */    
    private static final class PrimOpMAKE_COMPARATOR extends PrimOp {
        
        private static final class GComparator implements java.util.Comparator<Node> {
            
            private final NInd comparisonFunction;
            private final Executor executor;
            
            private GComparator(Node comparisonFunction, Executor oldExecutor) {
                if (comparisonFunction == null || oldExecutor == null) {
                    throw new NullPointerException();
                }
                this.comparisonFunction = (NInd)comparisonFunction;
                this.executor = new Executor (oldExecutor.program, oldExecutor.resourceAccess, oldExecutor.getExecutionContext());
            }
            
            /** {@inheritDoc} */
            public int compare(Node object1, Node object2) {
                this.executor.reset ();
                this.executor.stack.push (new NAp(new NAp (comparisonFunction, object1), object2));
                try {
                    this.executor.exec (new Code(Instruction.I_Eval));
                } catch (CALExecutorException cee) {
                    //wrap the exception up in a non-checked exception and rethrow.
                    throw new RuntimeException (cee);
                }
                
                int result = this.executor.stack.pop().getOrdinalValue();
                
                switch (result) {
                
                    case 0: {
                        // Prelude.LT
                        return -1;
                    }
                    case 1: {
                        // Prelude.EQ
                        return 0;
                    }
                    case 2: {
                        // Prelude.GT
                        return 1;
                    }
                    
                    default:
                    {
                        throw new RuntimeException("Unrecognized " + CAL_Prelude.TypeConstructors.Ordering.getQualifiedName() + " data constructor.");
                    }
                
                }
            }
            
            /** {@inheritDoc} */
            @Override
            public String toString() {
                return comparisonFunction.toString();
            }                
        }
        
        public PrimOpMAKE_COMPARATOR (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Node comparisonFunction = e.stack.pop();
            e.stack.push(new NValObject(new GComparator(comparisonFunction, e)));           
        }
    }
    
    /**
     * Implements the primitive function declared in the Prelude module
     * makeEquivalenceRelation :: (a -> a -> Boolean) -> EquivalenceRelation
     * 
     * @author Bo Ilic
     */
    private static final class PrimOpMAKE_EQUIVALENCE_RELATION extends PrimOp {
        
        private static final class GEquivalenceRelation implements EquivalenceRelation<Node> {
            
            private final NInd equivalenceFunction;
            private final Executor executor;
            
            private GEquivalenceRelation(Node equivalenceFunction, Executor oldExecutor) {
                if (equivalenceFunction == null || oldExecutor == null) {
                    throw new NullPointerException();
                }
                this.equivalenceFunction = (NInd)equivalenceFunction;
                this.executor = new Executor (oldExecutor.program, oldExecutor.resourceAccess, oldExecutor.getExecutionContext());
            }
            
            /** {@inheritDoc} */
            public boolean equivalent(Node object1, Node object2) {
                //evaluate the result of the equivalence function applied to the two objects in question.
                this.executor.reset ();
                
                this.executor.stack.push (new NAp (new NAp (equivalenceFunction, object1), object2));
                try {
                    this.executor.exec (new Code (Instruction.I_Eval));
                } catch (CALExecutorException cee) {
                    //wrap the exception up in a non-checked exception and rethrow.
                    throw new RuntimeException (cee);
                }
                boolean result = ((NValBoolean)this.executor.stack.pop()).getBooleanValue();
                return result;
            }

            /** {@inheritDoc} */
            @Override
            public String toString() {
                return equivalenceFunction.toString();
            }             
        }        
        
        public PrimOpMAKE_EQUIVALENCE_RELATION (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Node eqFunction = e.stack.pop();
            e.stack.push(new NValObject(new GEquivalenceRelation(eqFunction, e)));
        }
    }
    
    /**
     * Implements the primitive function declared in the Prelude module
     * makeCalFunction :: (JObject -> JObject) -> CalFunction
     * 
     * @author Bo Ilic
     */    
    private static final class PrimOpMAKE_CAL_FUNCTION extends PrimOp {
        
        private static final class GCalFunction implements CalFunction {
            
            private final NInd calFunction;
            private final Executor executor;
            
            private GCalFunction(Node calFunction, Executor oldExecutor) {
                if (calFunction == null || oldExecutor == null) {
                    throw new NullPointerException();
                }
                this.calFunction = (NInd)calFunction;
                this.executor = new Executor (oldExecutor.program, oldExecutor.resourceAccess, oldExecutor.getExecutionContext());
            }
            
            /** {@inheritDoc} */
            public Object evaluate(Object object) {
                this.executor.reset ();
                this.executor.stack.push (new NAp(calFunction, new NValObject(object)));
                try {
                    this.executor.exec (new Code(Instruction.I_Eval));
                } catch (CALExecutorException cee) {
                    //wrap the exception up in a non-checked exception and rethrow.
                    throw new RuntimeException (cee);
                }
                
                return this.executor.stack.pop().getValue();               
            }
            
            /** {@inheritDoc} */
            @Override
            public String toString() {
                return calFunction.toString();
            }                
        }
        
        public PrimOpMAKE_CAL_FUNCTION (PrimOps.PrimOpInfo info) {
            super (info);
        }
        
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Node calFunction = e.stack.pop();
            e.stack.push(new NValObject(new GCalFunction(calFunction, e)));           
        }
    }    

    private static class PrimopBITWISE_AND_INT extends PrimOp {
        public PrimopBITWISE_AND_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            int y = stack.popInt();
            int x = stack.popInt();
            stack.pushInt (x & y);
        }
    }

    private static class PrimopBITWISE_OR_INT extends PrimOp {
        public PrimopBITWISE_OR_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            int i = stack.popInt();
            stack.pushInt (stack.popInt() | i);
        }
    }

    private static class PrimopBITWISE_XOR_INT extends PrimOp {
        public PrimopBITWISE_XOR_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            int i = stack.popInt();
            stack.pushInt (stack.popInt() ^ i);
        }
    }

    private static class PrimopCOMPLEMENT_INT extends PrimOp {
        public PrimopCOMPLEMENT_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            int i = stack.popInt();
            stack.pushInt (~i);
        }
    }
    
    private static class PrimopSHIFTL_INT extends PrimOp {
        public PrimopSHIFTL_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            int y = stack.popInt();
            int x = stack.popInt();
            stack.pushInt (x << y);
        }
    }

    private static class PrimopSHIFTR_INT extends PrimOp {
        public PrimopSHIFTR_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            int y = stack.popInt();
            int x = stack.popInt();
            stack.pushInt (x >> y);
        }
    }

    private static class PrimopSHIFTR_UNSIGNED_INT extends PrimOp {
        public PrimopSHIFTR_UNSIGNED_INT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            int y = stack.popInt();
            int x = stack.popInt();
            stack.pushInt (x >>> y);
        }
    }

    private static class PrimopBITWISE_AND_LONG extends PrimOp {
        public PrimopBITWISE_AND_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            long y = stack.popLong();
            long x = stack.popLong();
            stack.pushLong (x & y);
        }
    }

    private static class PrimopBITWISE_OR_LONG extends PrimOp {
        public PrimopBITWISE_OR_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            long i = stack.popLong();
            stack.pushLong (stack.popLong() | i);
        }
    }

    private static class PrimopBITWISE_XOR_LONG extends PrimOp {
        public PrimopBITWISE_XOR_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            long i = stack.popLong();
            stack.pushLong (stack.popLong() ^ i);
        }
    }

    private static class PrimopCOMPLEMENT_LONG extends PrimOp {
        public PrimopCOMPLEMENT_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            long i = stack.popLong();
            stack.pushLong (~i);
        }
    }
    
    private static class PrimopSHIFTL_LONG extends PrimOp {
        public PrimopSHIFTL_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            long y = stack.popLong();
            long x = stack.popLong();
            stack.pushLong (x << y);
        }
    }

    private static class PrimopSHIFTR_LONG extends PrimOp {
        public PrimopSHIFTR_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            long y = stack.popLong();
            long x = stack.popLong();
            stack.pushLong (x >> y);
        }
    }

    private static class PrimopSHIFTR_UNSIGNED_LONG extends PrimOp {
        public PrimopSHIFTR_UNSIGNED_LONG (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            Executor.GStack stack = e.stack;    
            long y = stack.popLong();
            long x = stack.popLong();
            stack.pushLong (x >>> y);
        }
    }
    
    private static class PrimopEXECUTION_CONTEXT extends PrimOp {
        public PrimopEXECUTION_CONTEXT (PrimOps.PrimOpInfo info) {
            super (info);
        }
        @Override
        public void perform (Executor e) throws CALExecutorException {
            e.stack.push(new NValObject(e.getContext()));         
        }
    }    
    
}
