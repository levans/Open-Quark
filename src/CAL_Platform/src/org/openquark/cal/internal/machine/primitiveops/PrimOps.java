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

package org.openquark.cal.internal.machine.primitiveops;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.module.Cal.Collections.CAL_List_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Bits_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Dynamic_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Prelude_internal;
import org.openquark.cal.internal.module.Cal.Core.CAL_Record_internal;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;


/** 
 * This class contains information about the various primitive operations.
 * It also specifies the interface for an actual operation object.
 * @author Luke Evans, Bo Ilic, Raymond Cypher
 */
public class PrimOps extends Object {

    /**
     * Since this map is only mutated by code in the static initialization block
     * it isn't synchronized.  If, however, the map is ever mutated by code outside
     * the static initialization block it will need to be synchronized.
     */
    private static final Map<QualifiedName, PrimOpInfo> nameToInfo = new HashMap<QualifiedName, PrimOpInfo> ();
    
    /**
     * Since this map is only mutated by code in the static initialization block
     * it isn't synchronized.  If, however, the map is ever mutated by code outside
     * the static initialization block it will need to be synchronized.
     */
    private static final Map<Integer, PrimOpInfo> codeToInfo = new HashMap<Integer, PrimOpInfo> ();

    // Operator sub-code for primitive intrinsic operations
    public static final int PRIMOP_NOP = -1; // No operation
    
    public static final int PRIMOP_OR = 1; // Logical OR
    public static final int PRIMOP_AND = 2; // Logical AND

    //Built-in functions for basic integer comparison and arithmetic
    public static final int PRIMOP_EQUALS_INT = 3;
    public static final int PRIMOP_NOT_EQUALS_INT = 4;
    public static final int PRIMOP_GREATER_THAN_INT = 5;
    public static final int PRIMOP_GREATER_THAN_EQUALS_INT = 6;
    public static final int PRIMOP_LESS_THAN_INT = 7;
    public static final int PRIMOP_LESS_THAN_EQUALS_INT = 8;
    public static final int PRIMOP_ADD_INT = 9;
    public static final int PRIMOP_SUBTRACT_INT = 10;
    public static final int PRIMOP_MULTIPLY_INT = 11;
    public static final int PRIMOP_DIVIDE_INT = 12;
    public static final int PRIMOP_NEGATE_INT = 13;
    public static final int PRIMOP_REMAINDER_INT = 14;   

    //Built-in functions for basic double comparison and arithmetic
    public static final int PRIMOP_EQUALS_DOUBLE = 15;
    public static final int PRIMOP_NOT_EQUALS_DOUBLE = 16;
    public static final int PRIMOP_GREATER_THAN_DOUBLE = 17;
    public static final int PRIMOP_GREATER_THAN_EQUALS_DOUBLE = 18;
    public static final int PRIMOP_LESS_THAN_DOUBLE = 19;
    public static final int PRIMOP_LESS_THAN_EQUALS_DOUBLE = 20;
    public static final int PRIMOP_ADD_DOUBLE = 21;
    public static final int PRIMOP_SUBTRACT_DOUBLE = 22;
    public static final int PRIMOP_MULTIPLY_DOUBLE = 23;
    public static final int PRIMOP_DIVIDE_DOUBLE = 24;
    public static final int PRIMOP_NEGATE_DOUBLE = 25;
    public static final int PRIMOP_REMAINDER_DOUBLE = 26; 

    //Built-in functions for basic character comparison
    public static final int PRIMOP_EQUALS_CHAR = 27;
    public static final int PRIMOP_NOT_EQUALS_CHAR = 28;
    public static final int PRIMOP_GREATER_THAN_CHAR = 29;
    public static final int PRIMOP_GREATER_THAN_EQUALS_CHAR = 30;
    public static final int PRIMOP_LESS_THAN_CHAR = 31;
    public static final int PRIMOP_LESS_THAN_EQUALS_CHAR = 32;
    
    //  Built-in functions for basic long comparison and arithmetic
    public static final int PRIMOP_EQUALS_LONG = 33;
    public static final int PRIMOP_NOT_EQUALS_LONG = 34;
    public static final int PRIMOP_GREATER_THAN_LONG = 35;
    public static final int PRIMOP_GREATER_THAN_EQUALS_LONG = 36;
    public static final int PRIMOP_LESS_THAN_LONG = 37;
    public static final int PRIMOP_LESS_THAN_EQUALS_LONG = 38;
    public static final int PRIMOP_ADD_LONG = 39;
    public static final int PRIMOP_SUBTRACT_LONG = 40;
    public static final int PRIMOP_MULTIPLY_LONG = 41;
    public static final int PRIMOP_DIVIDE_LONG = 42;
    public static final int PRIMOP_NEGATE_LONG = 43;
    public static final int PRIMOP_REMAINDER_LONG = 44;   
    
    //  Built-in functions for basic short comparison
    public static final int PRIMOP_EQUALS_SHORT = 45;
    public static final int PRIMOP_NOT_EQUALS_SHORT = 46;
    public static final int PRIMOP_GREATER_THAN_SHORT = 47;
    public static final int PRIMOP_GREATER_THAN_EQUALS_SHORT = 48;
    public static final int PRIMOP_LESS_THAN_SHORT = 49;
    public static final int PRIMOP_LESS_THAN_EQUALS_SHORT = 50;
    
    //  Built-in functions for basic byte comparison
    public static final int PRIMOP_EQUALS_BYTE = 51;
    public static final int PRIMOP_NOT_EQUALS_BYTE = 52;
    public static final int PRIMOP_GREATER_THAN_BYTE = 53;
    public static final int PRIMOP_GREATER_THAN_EQUALS_BYTE = 54;
    public static final int PRIMOP_LESS_THAN_BYTE = 55;
    public static final int PRIMOP_LESS_THAN_EQUALS_BYTE = 56;
    
    //  Built-in functions for basic float comparison and arithmetic
    public static final int PRIMOP_EQUALS_FLOAT = 57;
    public static final int PRIMOP_NOT_EQUALS_FLOAT = 58;
    public static final int PRIMOP_GREATER_THAN_FLOAT = 59;
    public static final int PRIMOP_GREATER_THAN_EQUALS_FLOAT = 60;
    public static final int PRIMOP_LESS_THAN_FLOAT = 61;
    public static final int PRIMOP_LESS_THAN_EQUALS_FLOAT = 62;
    public static final int PRIMOP_ADD_FLOAT = 63;
    public static final int PRIMOP_SUBTRACT_FLOAT = 64;
    public static final int PRIMOP_MULTIPLY_FLOAT = 65;
    public static final int PRIMOP_DIVIDE_FLOAT = 66;
    public static final int PRIMOP_NEGATE_FLOAT = 67;
    public static final int PRIMOP_REMAINDER_FLOAT = 68;   
    
    // bitwise operations in Int
    public static final int PRIMOP_BITWISE_AND_INT = 69;
    public static final int PRIMOP_BITWISE_OR_INT = 70;
    public static final int PRIMOP_BITWISE_XOR_INT = 71;
    public static final int PRIMOP_COMPLEMENT_INT = 72;
    public static final int PRIMOP_HIGHEST_MASK_INT = 73;
    public static final int PRIMOP_SHIFTL_INT = 74;
    public static final int PRIMOP_SHIFTR_INT = 75;
    public static final int PRIMOP_SHIFTR_UNSIGNED_INT = 76;

    // bitwise operations on Long
    public static final int PRIMOP_BITWISE_AND_LONG = 77;
    public static final int PRIMOP_BITWISE_OR_LONG = 78;
    public static final int PRIMOP_BITWISE_XOR_LONG = 79;
    public static final int PRIMOP_COMPLEMENT_LONG = 80;
    public static final int PRIMOP_HIGHEST_MASK_LONG = 81;
    public static final int PRIMOP_SHIFTL_LONG = 82;
    public static final int PRIMOP_SHIFTR_LONG = 83;
    public static final int PRIMOP_SHIFTR_UNSIGNED_LONG = 84;
    
    public static final int PRIMOP_CAL_VALUE_TO_OBJECT = 85;
    public static final int PRIMOP_OBJECT_TO_CAL_VALUE = 86;
    public static final int PRIMOP_MAKE_COMPARATOR = 87;
    public static final int PRIMOP_MAKE_EQUIVALENCE_RELATION = 88;
    public static final int PRIMOP_MAKE_CAL_FUNCTION = 89;
                  
    //record related functions
    public static final int PRIMOP_FIELD_NAMES = 90;
    public static final int PRIMOP_FIELD_VALUES = 91;
    public static final int PRIMOP_HAS_FIELD = 92;
    public static final int PRIMOP_RECORD_FIELD_INDEX = 93;
            
    // primitive for forcing eager evaluation
    public static final int PRIMOP_EAGER = 94;
    
    //for the System.executionContext function
    public static final int PRIMOP_EXECUTION_CONTEXT = 95;
    
    //for List.makeIterator :: [a] -> (a -> JObject) -> JIterator;
    public static final int PRIMOP_MAKE_ITERATOR = 96;
        
    public static final int PRIMOP_FOREIGN_FUNCTION = 97;
    //leave PRIMOP_FOREIGN_FUNCTION last!
    
    
    /////////////////////////////////////////////////////////////////////////////////

    // Initialization block to set up the info for the primitive ops.
    static {
//        addPreludeOp ("||", PRIMOP_OR, 2, "||");
//        addPreludeOp ("&&", PRIMOP_AND, 2, "&&");
        addOp (CAL_Prelude_internal.Functions.equalsInt, PRIMOP_EQUALS_INT, 2);
        addOp (CAL_Prelude_internal.Functions.notEqualsInt, PRIMOP_NOT_EQUALS_INT, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanInt, PRIMOP_GREATER_THAN_INT, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanEqualsInt, PRIMOP_GREATER_THAN_EQUALS_INT, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanInt, PRIMOP_LESS_THAN_INT, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanEqualsInt, PRIMOP_LESS_THAN_EQUALS_INT, 2);
        addOp (CAL_Prelude_internal.Functions.addInt, PRIMOP_ADD_INT, 2);
        addOp (CAL_Prelude_internal.Functions.subtractInt, PRIMOP_SUBTRACT_INT, 2);
        addOp (CAL_Prelude_internal.Functions.multiplyInt, PRIMOP_MULTIPLY_INT, 2);
        addOp (CAL_Prelude_internal.Functions.divideInt, PRIMOP_DIVIDE_INT, 2);        
        addOp (CAL_Prelude_internal.Functions.negateInt, PRIMOP_NEGATE_INT, 1);
        addOp (CAL_Prelude_internal.Functions.remainderInt, PRIMOP_REMAINDER_INT, 2);
        addOp (CAL_Prelude_internal.Functions.equalsDouble, PRIMOP_EQUALS_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.notEqualsDouble, PRIMOP_NOT_EQUALS_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanDouble, PRIMOP_GREATER_THAN_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanEqualsDouble, PRIMOP_GREATER_THAN_EQUALS_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanDouble, PRIMOP_LESS_THAN_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanEqualsDouble, PRIMOP_LESS_THAN_EQUALS_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.addDouble, PRIMOP_ADD_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.subtractDouble, PRIMOP_SUBTRACT_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.multiplyDouble, PRIMOP_MULTIPLY_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.divideDouble, PRIMOP_DIVIDE_DOUBLE, 2);
        addOp (CAL_Prelude_internal.Functions.negateDouble, PRIMOP_NEGATE_DOUBLE, 1); 
        addOp (CAL_Prelude_internal.Functions.remainderDouble, PRIMOP_REMAINDER_DOUBLE, 2);       
        addOp (CAL_Prelude_internal.Functions.equalsChar, PRIMOP_EQUALS_CHAR, 2);
        addOp (CAL_Prelude_internal.Functions.notEqualsChar, PRIMOP_NOT_EQUALS_CHAR, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanChar, PRIMOP_GREATER_THAN_CHAR, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanEqualsChar, PRIMOP_GREATER_THAN_EQUALS_CHAR, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanChar, PRIMOP_LESS_THAN_CHAR, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanEqualsChar, PRIMOP_LESS_THAN_EQUALS_CHAR, 2);
        
        addOp (CAL_Prelude_internal.Functions.equalsLong, PRIMOP_EQUALS_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.notEqualsLong, PRIMOP_NOT_EQUALS_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanLong, PRIMOP_GREATER_THAN_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanEqualsLong, PRIMOP_GREATER_THAN_EQUALS_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanLong, PRIMOP_LESS_THAN_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanEqualsLong, PRIMOP_LESS_THAN_EQUALS_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.addLong, PRIMOP_ADD_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.subtractLong, PRIMOP_SUBTRACT_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.multiplyLong, PRIMOP_MULTIPLY_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.divideLong, PRIMOP_DIVIDE_LONG, 2);
        addOp (CAL_Prelude_internal.Functions.negateLong, PRIMOP_NEGATE_LONG, 1);
        addOp (CAL_Prelude_internal.Functions.remainderLong, PRIMOP_REMAINDER_LONG, 2);        

        addOp (CAL_Prelude_internal.Functions.equalsShort, PRIMOP_EQUALS_SHORT, 2);
        addOp (CAL_Prelude_internal.Functions.notEqualsShort, PRIMOP_NOT_EQUALS_SHORT, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanShort, PRIMOP_GREATER_THAN_SHORT, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanEqualsShort, PRIMOP_GREATER_THAN_EQUALS_SHORT, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanShort, PRIMOP_LESS_THAN_SHORT, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanEqualsShort, PRIMOP_LESS_THAN_EQUALS_SHORT, 2);

        addOp (CAL_Prelude_internal.Functions.equalsByte, PRIMOP_EQUALS_BYTE, 2);
        addOp (CAL_Prelude_internal.Functions.notEqualsByte, PRIMOP_NOT_EQUALS_BYTE, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanByte, PRIMOP_GREATER_THAN_BYTE, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanEqualsByte, PRIMOP_GREATER_THAN_EQUALS_BYTE, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanByte, PRIMOP_LESS_THAN_BYTE, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanEqualsByte, PRIMOP_LESS_THAN_EQUALS_BYTE, 2);

        addOp (CAL_Prelude_internal.Functions.equalsFloat, PRIMOP_EQUALS_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.notEqualsFloat, PRIMOP_NOT_EQUALS_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanFloat, PRIMOP_GREATER_THAN_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.greaterThanEqualsFloat, PRIMOP_GREATER_THAN_EQUALS_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanFloat, PRIMOP_LESS_THAN_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.lessThanEqualsFloat, PRIMOP_LESS_THAN_EQUALS_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.addFloat, PRIMOP_ADD_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.subtractFloat, PRIMOP_SUBTRACT_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.multiplyFloat, PRIMOP_MULTIPLY_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.divideFloat, PRIMOP_DIVIDE_FLOAT, 2);
        addOp (CAL_Prelude_internal.Functions.negateFloat, PRIMOP_NEGATE_FLOAT, 1);
        addOp (CAL_Prelude_internal.Functions.remainderFloat, PRIMOP_REMAINDER_FLOAT, 2);       
      
        addOp (CAL_Bits_internal.Functions.bitwiseAndInt, PRIMOP_BITWISE_AND_INT, 2);
        addOp (CAL_Bits_internal.Functions.bitwiseOrInt, PRIMOP_BITWISE_OR_INT, 2);
        addOp (CAL_Bits_internal.Functions.bitwiseXorInt, PRIMOP_BITWISE_XOR_INT, 2);
        addOp (CAL_Bits_internal.Functions.complementInt, PRIMOP_COMPLEMENT_INT, 1);
        addOp (CAL_Bits_internal.Functions.shiftLInt, PRIMOP_SHIFTL_INT, 2);
        addOp (CAL_Bits_internal.Functions.shiftRInt, PRIMOP_SHIFTR_INT, 2);
        addOp (CAL_Bits_internal.Functions.shiftRUnsignedInt, PRIMOP_SHIFTR_UNSIGNED_INT, 2);
        
        addOp (CAL_Bits_internal.Functions.bitwiseAndLong, PRIMOP_BITWISE_AND_LONG, 2);
        addOp (CAL_Bits_internal.Functions.bitwiseOrLong, PRIMOP_BITWISE_OR_LONG, 2);
        addOp (CAL_Bits_internal.Functions.bitwiseXorLong, PRIMOP_BITWISE_XOR_LONG, 2);
        addOp (CAL_Bits_internal.Functions.complementLong, PRIMOP_COMPLEMENT_LONG, 1);
        addOp (CAL_Bits_internal.Functions.shiftLLong, PRIMOP_SHIFTL_LONG, 2);
        addOp (CAL_Bits_internal.Functions.shiftRLong, PRIMOP_SHIFTR_LONG, 2);
        addOp (CAL_Bits_internal.Functions.shiftRUnsignedLong, PRIMOP_SHIFTR_UNSIGNED_LONG, 2);
        
        addOp (CAL_Prelude_internal.Functions.calValueToObject, PRIMOP_CAL_VALUE_TO_OBJECT, 1);
        addOp (CAL_Prelude_internal.Functions.objectToCalValue, PRIMOP_OBJECT_TO_CAL_VALUE, 1);
        addOp (CAL_Prelude_internal.Functions.makeComparator, PRIMOP_MAKE_COMPARATOR, 1);
        addOp (CAL_Prelude_internal.Functions.makeEquivalenceRelation, PRIMOP_MAKE_EQUIVALENCE_RELATION, 1);
        addOp (CAL_Prelude.Functions.makeCalFunction, PRIMOP_MAKE_CAL_FUNCTION, 1);
                        
        addOp (CAL_Record_internal.Functions.fieldNamesPrimitive, PRIMOP_FIELD_NAMES, 1);
        addOp (CAL_Record_internal.Functions.hasFieldPrimitive, PRIMOP_HAS_FIELD, 2);    
        addOp (CAL_Dynamic_internal.Functions.fieldValuesPrimitive, PRIMOP_FIELD_VALUES, 1);        
        addOp (CAL_Dynamic_internal.Functions.recordFieldIndex, PRIMOP_RECORD_FIELD_INDEX, 2);
        
        addOp (CAL_Prelude.Functions.eager, PRIMOP_EAGER, 1);
        
        addOp (CAL_Prelude_internal.Functions.executionContext, PRIMOP_EXECUTION_CONTEXT, 0);
        
        addOp (CAL_List_internal.Functions.makeIterator, PRIMOP_MAKE_ITERATOR, 2);
    }
 
    private static void addOp (QualifiedName name, int code, int arity) {
        addOp (name, code, arity, name.getUnqualifiedName());
    }
    
    // Add information about a named op, including description, to the maps.
    private static void addOp (QualifiedName name, int code, int arity, String description) {
        //System.out.println ("addOp " + name);
        PrimOpInfo pof = new PrimOpInfo (name, code, arity, description);
        nameToInfo.put (name, pof);
        codeToInfo.put (Integer.valueOf(code), pof);
    }
    
    // Fetch info by qualified name.
    public static PrimOps.PrimOpInfo fetchInfo (QualifiedName name) {
        return nameToInfo.get(name);
    }
    
    // Fetch info by code.
    public static PrimOps.PrimOpInfo fetchInfo (int code) {
        return codeToInfo.get (Integer.valueOf(code));
    }
        
    public static Collection<PrimOpInfo> primOps () {
        return codeToInfo.values();
    }
    
    public static final class PrimOpInfo {
        private final QualifiedName name;
        private final int arity;
        private final int code;
        private final String description;

        private PrimOpInfo (QualifiedName name, int code, int arity, String description) {
            this.name = name;
            this.code = code;
            this.arity = arity;
            this.description = description;
        }
        
        public QualifiedName getName () {
            return name;
        }
        public int getArity () {
            return arity;
        }
        public int getCode () {
            return code;
        }
        public String getDescription () {
            return description;
        }
        @Override
        public String toString () {
            return name + ": arity = " + arity + ", code = " + code + " " + description;
        }
    }        
}
